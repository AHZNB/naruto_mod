
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Biomes;

import net.narutomod.ElementsNarutomodMod;

import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class EntityThreeTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 257;
	public static final int ENTITYID_RANGED = 258;
	private static final float MODELSCALE = 20.0F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntityThreeTails(ElementsNarutomodMod instance) {
		super(instance, 582);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "three_tails"), ENTITYID).name("three_tails").tracker(96, 3, true)
		 .egg(-10066330, -6750208).build());
	}

	public static TailBeastManager getBijuManager() {
		return tailBeastManager;
	}

	public static class TailBeastManager extends EntityBijuManager<EntityCustom> {
		public TailBeastManager() {
			super(EntityCustom.class, 3);
		}

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}

	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_threetails";
		private static Save instance = null;

		public Save() {
			super(DATA_NAME);
		}

		public Save(String name) {
			super(name);
		}

	 	@Override
	 	public Save loadData() {
	 		instance = null;
	 		return this.getInstance();
	 	}

	 	@Override
	 	public void resetData() {
	 		super.resetData();
	 		instance = null;
	 	}

	 	public static Save getInstance() {
	 		if (instance == null) {
	 			MapStorage storage = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
	 			instance = (Save) storage.getOrLoadData(Save.class, DATA_NAME);
	 			if (instance == null) {
	 				instance = new Save(); 
	 				storage.setData(DATA_NAME, instance);
	 			} 
	 		} 
	 		return instance;
	 	}

	 	@Override
	 	protected EntityBijuManager getBijuManager() {
	 		return tailBeastManager;
	 	}

	 	@Override
	 	protected EntityTailedBeast.Base createEntity(World world) {
	 		return new EntityCustom(world);
	 	}
	}

	public static class EntityCustom extends EntityTailedBeast.Base {
		private EntityMoveHelper altMoveHelper;
		private EntityMoveHelper mainMoveHelper;

		public EntityCustom(World worldIn) {
			super(worldIn);
			this.setSize(MODELSCALE * 0.5F, MODELSCALE * 0.7F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.5F, MODELSCALE * 0.7F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			PathNavigate nav = super.createNavigator(worldIn);
			this.mainMoveHelper = this.moveHelper;
			this.altMoveHelper = new EntityTailedBeast.FlySwimHelper(this);
			return nav;
		}

		@Override
		public float getModelScale() {
			return MODELSCALE;
		}

		@Override
		public void setFaceDown(boolean down) {
			super.setFaceDown(down);
			this.setSize(this.width, MODELSCALE * (down ? 0.625F : 0.7F));
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
			this.getEntityAttribute(EntityLivingBase.SWIM_SPEED).setBaseValue(0.8D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10000.0D);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(30.0D);
		}

		@Override
		public EntityBijuManager getBijuManager() {
			return tailBeastManager;
		}

		@Override
		public double getMountedYOffset() {
			return this.isFaceDown() ? 2.0d * 0.0625d * MODELSCALE : (double)this.height + 0.35D;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0.25d * MODELSCALE, 0d, 0d) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
				passenger.setPosition(this.posX + vec2.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec2.z);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.isInWater() && this.moveHelper != this.altMoveHelper) {
				this.moveHelper = this.altMoveHelper;
			} else if (!this.isInWater() && this.moveHelper != this.mainMoveHelper) {
				this.moveHelper = this.mainMoveHelper;
			}
		}

		@Override
		public float getBlockPathWeight(BlockPos pos) {
			Biome biome = this.world.getBiome(pos);
			return biome == Biomes.DEEP_OCEAN ? 50.0F : biome == Biomes.OCEAN ? 20.0F
			 : biome == Biomes.BEACH || biome == Biomes.STONE_BEACH ? 10.0F : 0.0F;
		}

		@Override
		public boolean isPushedByWater() {
			return false;
		}

		@Override
		public boolean canBreatheUnderwater() {
			return true;
		}

		@Override
		protected float getWaterSlowDown() {
			return 1.0F;
		}

		@Override
		public float getFuuinBeamHeight() {
			return this.isFaceDown() ? 2.0f * 0.0625f * MODELSCALE : super.getFuuinBeamHeight();
		}

		@Override
		public SoundEvent getAmbientSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation(this.rand.nextInt(4) == 0 ? "narutomod:isobu_roar" : "narutomod:guttural"));
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return null;
		}

		@Override
		public SoundEvent getDeathSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:isobu_hurt"));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityTailedBeast.ClientOnly.Renderer<EntityCustom> {
			private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/threetails.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelThreeTails(), MODELSCALE * 0.5F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return TEXTURE;
			}
		}

		// Made with Blockbench 3.8.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelThreeTails extends ModelBiped {
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer eye;
			//private final ModelRenderer bipedBody;
			//private final ModelRenderer bipedHead;
			private final ModelRenderer hair;
			private final ModelRenderer bone;
			private final ModelRenderer bone25;
			private final ModelRenderer spike11;
			private final ModelRenderer bone13;
			private final ModelRenderer bone14;
			private final ModelRenderer bone15;
			private final ModelRenderer bone16;
			private final ModelRenderer spike12;
			private final ModelRenderer bone17;
			private final ModelRenderer bone18;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer spike17;
			private final ModelRenderer bone75;
			private final ModelRenderer bone76;
			private final ModelRenderer bone77;
			private final ModelRenderer bone78;
			private final ModelRenderer spike18;
			private final ModelRenderer bone79;
			private final ModelRenderer bone80;
			private final ModelRenderer bone81;
			private final ModelRenderer bone82;
			private final ModelRenderer spike13;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
			private final ModelRenderer bone24;
			private final ModelRenderer spike14;
			private final ModelRenderer bone26;
			private final ModelRenderer bone27;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			private final ModelRenderer spike15;
			private final ModelRenderer bone41;
			private final ModelRenderer bone56;
			private final ModelRenderer bone67;
			private final ModelRenderer bone68;
			private final ModelRenderer bone69;
			private final ModelRenderer spike16;
			private final ModelRenderer bone70;
			private final ModelRenderer bone71;
			private final ModelRenderer bone72;
			private final ModelRenderer bone73;
			private final ModelRenderer bone74;
			private final ModelRenderer Jaw;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer spike1;
			private final ModelRenderer bone30;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer spike2;
			private final ModelRenderer bone42;
			private final ModelRenderer bone43;
			private final ModelRenderer bone44;
			private final ModelRenderer bone45;
			private final ModelRenderer spike3;
			private final ModelRenderer bone34;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer spike4;
			private final ModelRenderer bone8;
			private final ModelRenderer bone38;
			private final ModelRenderer bone39;
			private final ModelRenderer bone40;
			private final ModelRenderer spike6;
			private final ModelRenderer bone51;
			private final ModelRenderer bone52;
			private final ModelRenderer bone53;
			private final ModelRenderer bone54;
			private final ModelRenderer bone55;
			private final ModelRenderer spike5;
			private final ModelRenderer bone46;
			private final ModelRenderer bone47;
			private final ModelRenderer bone48;
			private final ModelRenderer bone49;
			private final ModelRenderer bone50;
			private final ModelRenderer spike7;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone7;
			private final ModelRenderer bone9;
			private final ModelRenderer spike8;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer spike9;
			private final ModelRenderer bone57;
			private final ModelRenderer bone58;
			private final ModelRenderer bone59;
			private final ModelRenderer bone60;
			private final ModelRenderer bone61;
			private final ModelRenderer spike10;
			private final ModelRenderer bone62;
			private final ModelRenderer bone63;
			private final ModelRenderer bone64;
			private final ModelRenderer bone65;
			private final ModelRenderer bone66;
			private final ModelRenderer torso;
			private final ModelRenderer bone83;
			private final ModelRenderer cube_r6;
			private final ModelRenderer cube_r7;
			private final ModelRenderer shell;
			private final ModelRenderer cube_r8;
			private final ModelRenderer cube_r9;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			private final ModelRenderer cube_r13;
			private final ModelRenderer cube_r14;
			private final ModelRenderer cube_r15;
			private final ModelRenderer spike19;
			private final ModelRenderer bone95;
			private final ModelRenderer bone96;
			private final ModelRenderer bone97;
			private final ModelRenderer spike22;
			private final ModelRenderer bone103;
			private final ModelRenderer bone104;
			private final ModelRenderer bone105;
			private final ModelRenderer spike32;
			private final ModelRenderer bone133;
			private final ModelRenderer bone134;
			private final ModelRenderer bone135;
			private final ModelRenderer spike23;
			private final ModelRenderer bone106;
			private final ModelRenderer bone107;
			private final ModelRenderer bone108;
			private final ModelRenderer spike26;
			private final ModelRenderer bone115;
			private final ModelRenderer bone116;
			private final ModelRenderer bone117;
			private final ModelRenderer spike27;
			private final ModelRenderer bone118;
			private final ModelRenderer bone119;
			private final ModelRenderer bone120;
			private final ModelRenderer spike28;
			private final ModelRenderer bone121;
			private final ModelRenderer bone122;
			private final ModelRenderer bone123;
			private final ModelRenderer spike29;
			private final ModelRenderer bone124;
			private final ModelRenderer bone125;
			private final ModelRenderer bone126;
			private final ModelRenderer spike30;
			private final ModelRenderer bone127;
			private final ModelRenderer bone128;
			private final ModelRenderer bone129;
			private final ModelRenderer spike31;
			private final ModelRenderer bone130;
			private final ModelRenderer bone131;
			private final ModelRenderer bone132;
			private final ModelRenderer spike24;
			private final ModelRenderer bone109;
			private final ModelRenderer bone110;
			private final ModelRenderer bone111;
			private final ModelRenderer spike25;
			private final ModelRenderer bone112;
			private final ModelRenderer bone113;
			private final ModelRenderer bone114;
			private final ModelRenderer spike33;
			private final ModelRenderer bone136;
			private final ModelRenderer bone137;
			private final ModelRenderer bone138;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer cube_r16;
			private final ModelRenderer cube_r17;
			private final ModelRenderer spike20;
			private final ModelRenderer bone94;
			private final ModelRenderer bone98;
			private final ModelRenderer bone99;
			private final ModelRenderer RightHand;
			private final ModelRenderer bone84;
			private final ModelRenderer bone85;
			private final ModelRenderer bone86;
			private final ModelRenderer bone87;
			private final ModelRenderer bone88;
			private final ModelRenderer bone139;
			private final ModelRenderer bone143;
			private final ModelRenderer bone144;
			private final ModelRenderer bone140;
			private final ModelRenderer bone141;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer cube_r18;
			private final ModelRenderer cube_r19;
			private final ModelRenderer spike21;
			private final ModelRenderer bone89;
			private final ModelRenderer bone90;
			private final ModelRenderer bone91;
			private final ModelRenderer LeftHand;
			private final ModelRenderer bone92;
			private final ModelRenderer bone93;
			private final ModelRenderer bone100;
			private final ModelRenderer bone101;
			private final ModelRenderer bone102;
			private final ModelRenderer bone142;
			private final ModelRenderer bone145;
			private final ModelRenderer bone146;
			private final ModelRenderer bone147;
			private final ModelRenderer bone148;
			private final ModelRenderer tails;
			private final ModelRenderer[][] tail = new ModelRenderer[3][8];
			private final float tailSwayX[][] = new float[3][8];
			private final float tailSwayY[][] = new float[3][8];
			private final float tailSwayZ[][] = new float[3][8];
			private final Random rand = new Random();

			public ModelThreeTails() {
				textureWidth = 64;
				textureHeight = 64;

				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 21.0F, 7.5F);


				eye = new ModelRenderer(this);
				eye.setRotationPoint(0.0F, -5.0F, -9.5F);
				bipedHeadwear.addChild(eye);
				eye.cubeList.add(new ModelBox(eye, 17, 52, 0.6F, -1.2F, -5.0F, 1, 1, 1, 0.1F, false));

				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 21.0F, 7.5F);


				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -5.0F, -9.5F);
				bipedBody.addChild(bipedHead);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 27, -2.5F, -3.0F, -5.0F, 5, 5, 4, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 47, -2.0F, -2.5F, -1.0F, 4, 4, 2, 0.0F, false));

				hair = new ModelRenderer(this);
				hair.setRotationPoint(-0.9267F, -3.1145F, -3.1F);
				bipedHead.addChild(hair);
				hair.cubeList.add(new ModelBox(hair, 42, 32, -0.0733F, -0.8355F, -2.0F, 2, 1, 4, 0.0F, false));

				bone = new ModelRenderer(this);
				bone.setRotationPoint(-0.4733F, 0.0645F, 0.0F);
				hair.addChild(bone);
				setRotationAngle(bone, 0.0F, 0.0F, -0.5236F);
				bone.cubeList.add(new ModelBox(bone, 30, 42, -1.0F, -0.5F, -2.0F, 2, 1, 4, 0.0F, false));

				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(2.3267F, 0.0645F, 0.0F);
				hair.addChild(bone25);
				setRotationAngle(bone25, 0.0F, 0.0F, 0.5236F);
				bone25.cubeList.add(new ModelBox(bone25, 22, 41, -1.0F, -0.5F, -2.0F, 2, 1, 4, 0.0F, false));

				spike11 = new ModelRenderer(this);
				spike11.setRotationPoint(1.4267F, 0.2145F, -1.75F);
				hair.addChild(spike11);
				setRotationAngle(spike11, -2.2689F, 2.9671F, -0.7418F);
				spike11.cubeList.add(new ModelBox(spike11, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike11.addChild(bone13);
				setRotationAngle(bone13, -0.0873F, 0.0F, 0.0873F);
				bone13.cubeList.add(new ModelBox(bone13, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone13.addChild(bone14);
				setRotationAngle(bone14, -0.0873F, 0.0F, 0.0873F);
				bone14.cubeList.add(new ModelBox(bone14, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone14.addChild(bone15);
				setRotationAngle(bone15, -0.0873F, 0.0F, 0.0873F);
				bone15.cubeList.add(new ModelBox(bone15, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone15.addChild(bone16);
				setRotationAngle(bone16, -0.0873F, 0.0F, 0.0873F);
				bone16.cubeList.add(new ModelBox(bone16, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike12 = new ModelRenderer(this);
				spike12.setRotationPoint(0.4267F, 0.2145F, -1.75F);
				hair.addChild(spike12);
				setRotationAngle(spike12, -2.2689F, -2.9671F, 0.7418F);
				spike12.cubeList.add(new ModelBox(spike12, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike12.addChild(bone17);
				setRotationAngle(bone17, -0.0873F, 0.0F, -0.0873F);
				bone17.cubeList.add(new ModelBox(bone17, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone17.addChild(bone18);
				setRotationAngle(bone18, -0.0873F, 0.0F, -0.0873F);
				bone18.cubeList.add(new ModelBox(bone18, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone18.addChild(bone19);
				setRotationAngle(bone19, -0.0873F, 0.0F, -0.0873F);
				bone19.cubeList.add(new ModelBox(bone19, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone19.addChild(bone20);
				setRotationAngle(bone20, -0.0873F, 0.0F, -0.0873F);
				bone20.cubeList.add(new ModelBox(bone20, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike17 = new ModelRenderer(this);
				spike17.setRotationPoint(2.1767F, -0.2855F, -0.25F);
				hair.addChild(spike17);
				setRotationAngle(spike17, 1.2217F, -1.2217F, 0.0F);
				spike17.cubeList.add(new ModelBox(spike17, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone75 = new ModelRenderer(this);
				bone75.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike17.addChild(bone75);
				setRotationAngle(bone75, -0.0873F, 0.0F, -0.0873F);
				bone75.cubeList.add(new ModelBox(bone75, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone76 = new ModelRenderer(this);
				bone76.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone75.addChild(bone76);
				setRotationAngle(bone76, -0.0873F, 0.0F, -0.0873F);
				bone76.cubeList.add(new ModelBox(bone76, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone77 = new ModelRenderer(this);
				bone77.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone76.addChild(bone77);
				setRotationAngle(bone77, -0.0873F, 0.0F, -0.0873F);
				bone77.cubeList.add(new ModelBox(bone77, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone78 = new ModelRenderer(this);
				bone78.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone77.addChild(bone78);
				setRotationAngle(bone78, -0.0873F, 0.0F, -0.0873F);
				bone78.cubeList.add(new ModelBox(bone78, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike18 = new ModelRenderer(this);
				spike18.setRotationPoint(-0.2233F, -0.2855F, -0.25F);
				hair.addChild(spike18);
				setRotationAngle(spike18, 1.309F, 2.2253F, -0.0873F);
				spike18.cubeList.add(new ModelBox(spike18, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone79 = new ModelRenderer(this);
				bone79.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike18.addChild(bone79);
				setRotationAngle(bone79, -0.0873F, 0.0F, 0.0873F);
				bone79.cubeList.add(new ModelBox(bone79, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone80 = new ModelRenderer(this);
				bone80.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone79.addChild(bone80);
				setRotationAngle(bone80, -0.0873F, 0.0F, 0.0873F);
				bone80.cubeList.add(new ModelBox(bone80, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone81 = new ModelRenderer(this);
				bone81.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone80.addChild(bone81);
				setRotationAngle(bone81, -0.0873F, 0.0F, 0.0873F);
				bone81.cubeList.add(new ModelBox(bone81, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone82 = new ModelRenderer(this);
				bone82.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone81.addChild(bone82);
				setRotationAngle(bone82, -0.0873F, 0.0F, 0.0873F);
				bone82.cubeList.add(new ModelBox(bone82, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike13 = new ModelRenderer(this);
				spike13.setRotationPoint(-1.0733F, 0.4645F, -1.25F);
				hair.addChild(spike13);
				setRotationAngle(spike13, -2.4435F, -2.8362F, 0.829F);
				spike13.cubeList.add(new ModelBox(spike13, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike13.addChild(bone21);
				setRotationAngle(bone21, -0.0873F, 0.0F, -0.0873F);
				bone21.cubeList.add(new ModelBox(bone21, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone21.addChild(bone22);
				setRotationAngle(bone22, -0.0873F, 0.0F, -0.0873F);
				bone22.cubeList.add(new ModelBox(bone22, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone22.addChild(bone23);
				setRotationAngle(bone23, -0.0873F, 0.0F, -0.0873F);
				bone23.cubeList.add(new ModelBox(bone23, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone23.addChild(bone24);
				setRotationAngle(bone24, -0.0873F, 0.0F, -0.0873F);
				bone24.cubeList.add(new ModelBox(bone24, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike14 = new ModelRenderer(this);
				spike14.setRotationPoint(2.9267F, 0.4645F, -1.25F);
				hair.addChild(spike14);
				setRotationAngle(spike14, -2.4435F, 2.8362F, -0.829F);
				spike14.cubeList.add(new ModelBox(spike14, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike14.addChild(bone26);
				setRotationAngle(bone26, -0.0873F, 0.0F, 0.0873F);
				bone26.cubeList.add(new ModelBox(bone26, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone26.addChild(bone27);
				setRotationAngle(bone27, -0.0873F, 0.0F, 0.0873F);
				bone27.cubeList.add(new ModelBox(bone27, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone27.addChild(bone28);
				setRotationAngle(bone28, -0.0873F, 0.0F, 0.0873F);
				bone28.cubeList.add(new ModelBox(bone28, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone28.addChild(bone29);
				setRotationAngle(bone29, -0.0873F, 0.0F, 0.0873F);
				bone29.cubeList.add(new ModelBox(bone29, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike15 = new ModelRenderer(this);
				spike15.setRotationPoint(-1.2233F, 0.4145F, -0.25F);
				hair.addChild(spike15);
				setRotationAngle(spike15, -0.6545F, 1.8762F, 0.4363F);
				spike15.cubeList.add(new ModelBox(spike15, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.1F, false));

				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.0F, 0.75F, 0.0F);
				spike15.addChild(bone41);
				setRotationAngle(bone41, 0.0873F, 0.0F, 0.0F);
				bone41.cubeList.add(new ModelBox(bone41, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone56 = new ModelRenderer(this);
				bone56.setRotationPoint(0.0F, 0.75F, 0.0F);
				bone41.addChild(bone56);
				setRotationAngle(bone56, 0.0873F, 0.0F, 0.0F);
				bone56.cubeList.add(new ModelBox(bone56, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone67 = new ModelRenderer(this);
				bone67.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone56.addChild(bone67);
				setRotationAngle(bone67, 0.0873F, 0.0F, 0.0F);
				bone67.cubeList.add(new ModelBox(bone67, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone68 = new ModelRenderer(this);
				bone68.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone67.addChild(bone68);
				setRotationAngle(bone68, 0.0873F, 0.0F, 0.0F);
				bone68.cubeList.add(new ModelBox(bone68, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone69 = new ModelRenderer(this);
				bone69.setRotationPoint(0.0F, 0.25F, 0.0F);
				bone68.addChild(bone69);
				setRotationAngle(bone69, 0.0873F, 0.0F, 0.0F);
				bone69.cubeList.add(new ModelBox(bone69, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike16 = new ModelRenderer(this);
				spike16.setRotationPoint(3.0767F, 0.4145F, -0.25F);
				hair.addChild(spike16);
				setRotationAngle(spike16, -0.6545F, -1.8762F, -0.4363F);
				spike16.cubeList.add(new ModelBox(spike16, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.1F, false));

				bone70 = new ModelRenderer(this);
				bone70.setRotationPoint(0.0F, 0.75F, 0.0F);
				spike16.addChild(bone70);
				setRotationAngle(bone70, 0.0873F, 0.0F, 0.0F);
				bone70.cubeList.add(new ModelBox(bone70, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone71 = new ModelRenderer(this);
				bone71.setRotationPoint(0.0F, 0.75F, 0.0F);
				bone70.addChild(bone71);
				setRotationAngle(bone71, 0.0873F, 0.0F, 0.0F);
				bone71.cubeList.add(new ModelBox(bone71, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone72 = new ModelRenderer(this);
				bone72.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone71.addChild(bone72);
				setRotationAngle(bone72, 0.0873F, 0.0F, 0.0F);
				bone72.cubeList.add(new ModelBox(bone72, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone73 = new ModelRenderer(this);
				bone73.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone72.addChild(bone73);
				setRotationAngle(bone73, 0.0873F, 0.0F, 0.0F);
				bone73.cubeList.add(new ModelBox(bone73, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone74 = new ModelRenderer(this);
				bone74.setRotationPoint(0.0F, 0.25F, 0.0F);
				bone73.addChild(bone74);
				setRotationAngle(bone74, 0.0873F, 0.0F, 0.0F);
				bone74.cubeList.add(new ModelBox(bone74, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.4F, false));

				Jaw = new ModelRenderer(this);
				Jaw.setRotationPoint(0.0F, 1.0F, -2.75F);
				bipedHead.addChild(Jaw);
				Jaw.cubeList.add(new ModelBox(Jaw, 0, 16, 2.5F, 0.4F, -2.45F, 0, 1, 2, 0.0F, false));
				Jaw.cubeList.add(new ModelBox(Jaw, 0, 20, -2.5F, 0.4F, -2.45F, 0, 1, 2, 0.0F, false));

				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, 1.3946F, -2.4274F);
				Jaw.addChild(cube_r1);
				setRotationAngle(cube_r1, -1.5708F, 0.0F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 21, 17, -2.5F, -2.0F, 0.0F, 5, 2, 0, 0.0F, false));

				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(-1.0F, 0.2786F, -2.4943F);
				Jaw.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.5236F, 0.0F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 44, 7, -1.5F, -1.0F, -0.5F, 5, 2, 0, 0.0F, false));

				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(-2.5F, 0.9F, 0.55F);
				Jaw.addChild(cube_r3);
				setRotationAngle(cube_r3, 0.3927F, 0.0F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 4, 2, 5.0F, -1.0F, -1.2F, 0, 1, 1, 0.0F, false));
				cube_r3.cubeList.add(new ModelBox(cube_r3, 36, 32, 0.0F, -1.0F, -0.2F, 5, 1, 0, 0.0F, false));
				cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 11, 0.0F, -1.0F, -1.2F, 0, 1, 1, 0.0F, false));

				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(-2.5F, 0.7481F, -2.2189F);
				Jaw.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.5236F, 0.0F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 4, 5.0F, -1.5F, -0.5F, 0, 2, 1, 0.0F, false));
				cube_r4.cubeList.add(new ModelBox(cube_r4, 4, 0, 0.0F, -1.5F, -0.5F, 0, 2, 1, 0.0F, false));

				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.0F, 1.4385F, 0.5566F);
				Jaw.addChild(cube_r5);
				setRotationAngle(cube_r5, -1.1781F, 0.0F, 0.0F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 32, 26, -2.5F, 0.0F, -0.5F, 5, 1, 0, 0.0F, false));

				spike1 = new ModelRenderer(this);
				spike1.setRotationPoint(-1.5F, 0.55F, -2.25F);
				Jaw.addChild(spike1);
				setRotationAngle(spike1, 0.9599F, -0.0436F, -0.6545F);
				spike1.cubeList.add(new ModelBox(spike1, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike1.addChild(bone30);
				setRotationAngle(bone30, -0.0873F, 0.0F, 0.0873F);
				bone30.cubeList.add(new ModelBox(bone30, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone30.addChild(bone31);
				setRotationAngle(bone31, -0.0873F, 0.0F, 0.0873F);
				bone31.cubeList.add(new ModelBox(bone31, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone31.addChild(bone32);
				setRotationAngle(bone32, -0.0873F, 0.0F, 0.0873F);
				bone32.cubeList.add(new ModelBox(bone32, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone32.addChild(bone33);
				setRotationAngle(bone33, -0.0873F, 0.0F, 0.0873F);
				bone33.cubeList.add(new ModelBox(bone33, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike2 = new ModelRenderer(this);
				spike2.setRotationPoint(1.75F, 0.55F, -2.25F);
				Jaw.addChild(spike2);
				setRotationAngle(spike2, 0.9599F, 0.0436F, 0.6545F);
				spike2.cubeList.add(new ModelBox(spike2, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike2.addChild(bone42);
				setRotationAngle(bone42, -0.0873F, 0.0F, -0.0873F);
				bone42.cubeList.add(new ModelBox(bone42, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone42.addChild(bone43);
				setRotationAngle(bone43, -0.0873F, 0.0F, -0.0873F);
				bone43.cubeList.add(new ModelBox(bone43, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone43.addChild(bone44);
				setRotationAngle(bone44, -0.0873F, 0.0F, -0.0873F);
				bone44.cubeList.add(new ModelBox(bone44, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone44.addChild(bone45);
				setRotationAngle(bone45, -0.0873F, 0.0F, -0.0873F);
				bone45.cubeList.add(new ModelBox(bone45, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike3 = new ModelRenderer(this);
				spike3.setRotationPoint(-0.4F, 0.5F, -2.5F);
				Jaw.addChild(spike3);
				setRotationAngle(spike3, 1.0036F, -0.0436F, -0.5236F);
				spike3.cubeList.add(new ModelBox(spike3, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike3.addChild(bone34);
				setRotationAngle(bone34, -0.0873F, 0.0F, 0.0873F);
				bone34.cubeList.add(new ModelBox(bone34, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone34.addChild(bone35);
				setRotationAngle(bone35, -0.0873F, 0.0F, 0.0873F);
				bone35.cubeList.add(new ModelBox(bone35, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone35.addChild(bone36);
				setRotationAngle(bone36, -0.0873F, 0.0F, 0.0873F);
				bone36.cubeList.add(new ModelBox(bone36, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone36.addChild(bone37);
				setRotationAngle(bone37, -0.0873F, 0.0F, 0.0873F);
				bone37.cubeList.add(new ModelBox(bone37, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike4 = new ModelRenderer(this);
				spike4.setRotationPoint(0.75F, 0.5F, -2.5F);
				Jaw.addChild(spike4);
				setRotationAngle(spike4, 1.0036F, 0.0436F, 0.5236F);
				spike4.cubeList.add(new ModelBox(spike4, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike4.addChild(bone8);
				setRotationAngle(bone8, -0.0873F, 0.0F, -0.0873F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone8.addChild(bone38);
				setRotationAngle(bone38, -0.0873F, 0.0F, -0.0873F);
				bone38.cubeList.add(new ModelBox(bone38, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone38.addChild(bone39);
				setRotationAngle(bone39, -0.0873F, 0.0F, -0.0873F);
				bone39.cubeList.add(new ModelBox(bone39, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone39.addChild(bone40);
				setRotationAngle(bone40, -0.0873F, 0.0F, -0.0873F);
				bone40.cubeList.add(new ModelBox(bone40, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike6 = new ModelRenderer(this);
				spike6.setRotationPoint(-2.15F, 0.5F, -2.25F);
				Jaw.addChild(spike6);
				setRotationAngle(spike6, 0.829F, 1.0036F, 0.0F);
				spike6.cubeList.add(new ModelBox(spike6, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));

				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike6.addChild(bone51);
				setRotationAngle(bone51, -0.0873F, 0.0F, 0.0F);
				bone51.cubeList.add(new ModelBox(bone51, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone52 = new ModelRenderer(this);
				bone52.setRotationPoint(0.0F, -0.75F, 0.0F);
				bone51.addChild(bone52);
				setRotationAngle(bone52, -0.0873F, 0.0F, 0.0F);
				bone52.cubeList.add(new ModelBox(bone52, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone53 = new ModelRenderer(this);
				bone53.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone52.addChild(bone53);
				setRotationAngle(bone53, -0.0873F, 0.0F, 0.0F);
				bone53.cubeList.add(new ModelBox(bone53, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone54 = new ModelRenderer(this);
				bone54.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone53.addChild(bone54);
				setRotationAngle(bone54, -0.0873F, 0.0F, 0.0F);
				bone54.cubeList.add(new ModelBox(bone54, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone55 = new ModelRenderer(this);
				bone55.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone54.addChild(bone55);
				setRotationAngle(bone55, -0.0873F, 0.0F, 0.0F);
				bone55.cubeList.add(new ModelBox(bone55, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike5 = new ModelRenderer(this);
				spike5.setRotationPoint(2.65F, 0.5F, -2.25F);
				Jaw.addChild(spike5);
				setRotationAngle(spike5, 0.829F, -1.0036F, 0.0F);
				spike5.cubeList.add(new ModelBox(spike5, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));

				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(0.0F, -0.75F, 0.0F);
				spike5.addChild(bone46);
				setRotationAngle(bone46, -0.0873F, 0.0F, 0.0F);
				bone46.cubeList.add(new ModelBox(bone46, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(0.0F, -0.75F, 0.0F);
				bone46.addChild(bone47);
				setRotationAngle(bone47, -0.0873F, 0.0F, 0.0F);
				bone47.cubeList.add(new ModelBox(bone47, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone47.addChild(bone48);
				setRotationAngle(bone48, -0.0873F, 0.0F, 0.0F);
				bone48.cubeList.add(new ModelBox(bone48, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone48.addChild(bone49);
				setRotationAngle(bone49, -0.0873F, 0.0F, 0.0F);
				bone49.cubeList.add(new ModelBox(bone49, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone49.addChild(bone50);
				setRotationAngle(bone50, -0.0873F, 0.0F, 0.0F);
				bone50.cubeList.add(new ModelBox(bone50, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike7 = new ModelRenderer(this);
				spike7.setRotationPoint(-2.4F, 0.75F, -1.25F);
				Jaw.addChild(spike7);
				setRotationAngle(spike7, -0.7418F, 0.829F, 0.48F);
				spike7.cubeList.add(new ModelBox(spike7, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.1F, false));

				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.75F, 0.0F);
				spike7.addChild(bone2);
				setRotationAngle(bone2, 0.0873F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.75F, 0.0F);
				bone2.addChild(bone3);
				setRotationAngle(bone3, 0.0873F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone3.addChild(bone4);
				setRotationAngle(bone4, 0.0873F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone4.addChild(bone7);
				setRotationAngle(bone7, 0.0873F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 0.25F, 0.0F);
				bone7.addChild(bone9);
				setRotationAngle(bone9, 0.0873F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike8 = new ModelRenderer(this);
				spike8.setRotationPoint(2.4F, 0.75F, -1.25F);
				Jaw.addChild(spike8);
				setRotationAngle(spike8, -0.7418F, -0.829F, -0.48F);
				spike8.cubeList.add(new ModelBox(spike8, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.1F, false));

				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.75F, 0.0F);
				spike8.addChild(bone5);
				setRotationAngle(bone5, 0.0873F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 0.75F, 0.0F);
				bone5.addChild(bone6);
				setRotationAngle(bone6, 0.0873F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone6.addChild(bone10);
				setRotationAngle(bone10, 0.0873F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone10.addChild(bone11);
				setRotationAngle(bone11, 0.0873F, 0.0F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, 0.25F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, 0.0873F, 0.0F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike9 = new ModelRenderer(this);
				spike9.setRotationPoint(-0.9F, 1.0F, -2.25F);
				Jaw.addChild(spike9);
				setRotationAngle(spike9, -1.0036F, 0.7418F, -0.3927F);
				spike9.cubeList.add(new ModelBox(spike9, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.1F, false));

				bone57 = new ModelRenderer(this);
				bone57.setRotationPoint(0.0F, 0.75F, 0.0F);
				spike9.addChild(bone57);
				setRotationAngle(bone57, 0.0873F, 0.0F, 0.0F);
				bone57.cubeList.add(new ModelBox(bone57, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone58 = new ModelRenderer(this);
				bone58.setRotationPoint(0.0F, 0.75F, 0.0F);
				bone57.addChild(bone58);
				setRotationAngle(bone58, 0.0873F, 0.0F, 0.0F);
				bone58.cubeList.add(new ModelBox(bone58, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone59 = new ModelRenderer(this);
				bone59.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone58.addChild(bone59);
				setRotationAngle(bone59, 0.0873F, 0.0F, 0.0F);
				bone59.cubeList.add(new ModelBox(bone59, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone60 = new ModelRenderer(this);
				bone60.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone59.addChild(bone60);
				setRotationAngle(bone60, 0.0873F, 0.0F, 0.0F);
				bone60.cubeList.add(new ModelBox(bone60, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone61 = new ModelRenderer(this);
				bone61.setRotationPoint(0.0F, 0.25F, 0.0F);
				bone60.addChild(bone61);
				setRotationAngle(bone61, 0.0873F, 0.0F, 0.0F);
				bone61.cubeList.add(new ModelBox(bone61, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike10 = new ModelRenderer(this);
				spike10.setRotationPoint(0.9F, 1.0F, -2.25F);
				Jaw.addChild(spike10);
				setRotationAngle(spike10, -1.0036F, -0.7418F, 0.3927F);
				spike10.cubeList.add(new ModelBox(spike10, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.1F, false));

				bone62 = new ModelRenderer(this);
				bone62.setRotationPoint(0.0F, 0.75F, 0.0F);
				spike10.addChild(bone62);
				setRotationAngle(bone62, 0.0873F, 0.0F, 0.0F);
				bone62.cubeList.add(new ModelBox(bone62, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F, false));

				bone63 = new ModelRenderer(this);
				bone63.setRotationPoint(0.0F, 0.75F, 0.0F);
				bone62.addChild(bone63);
				setRotationAngle(bone63, 0.0873F, 0.0F, 0.0F);
				bone63.cubeList.add(new ModelBox(bone63, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone64 = new ModelRenderer(this);
				bone64.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone63.addChild(bone64);
				setRotationAngle(bone64, 0.0873F, 0.0F, 0.0F);
				bone64.cubeList.add(new ModelBox(bone64, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone65 = new ModelRenderer(this);
				bone65.setRotationPoint(0.0F, 0.5F, 0.0F);
				bone64.addChild(bone65);
				setRotationAngle(bone65, 0.0873F, 0.0F, 0.0F);
				bone65.cubeList.add(new ModelBox(bone65, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone66 = new ModelRenderer(this);
				bone66.setRotationPoint(0.0F, 0.25F, 0.0F);
				bone65.addChild(bone66);
				setRotationAngle(bone66, 0.0873F, 0.0F, 0.0F);
				bone66.cubeList.add(new ModelBox(bone66, 0, 23, -0.5F, 0.0F, -0.5F, 1, 1, 1, -0.4F, false));

				torso = new ModelRenderer(this);
				torso.setRotationPoint(0.0F, -5.0F, -7.5F);
				bipedBody.addChild(torso);
				setRotationAngle(torso, -0.5236F, 0.0F, 0.0F);
				torso.cubeList.add(new ModelBox(torso, 0, 0, -3.5F, -3.0F, -3.0F, 7, 5, 7, 0.0F, false));
				torso.cubeList.add(new ModelBox(torso, 21, 12, -4.5F, 0.0F, 5.0F, 1, 3, 1, 0.0F, false));
				torso.cubeList.add(new ModelBox(torso, 21, 12, 3.5F, 0.0F, 5.0F, 1, 3, 1, 0.0F, true));
				torso.cubeList.add(new ModelBox(torso, 3, 4, -4.5F, 1.0F, 6.0F, 1, 2, 1, 0.0F, false));
				torso.cubeList.add(new ModelBox(torso, 3, 4, 3.5F, 1.0F, 6.0F, 1, 2, 1, 0.0F, true));
				torso.cubeList.add(new ModelBox(torso, 0, 0, -4.5F, -1.0F, 4.0F, 1, 4, 1, 0.0F, false));
				torso.cubeList.add(new ModelBox(torso, 0, 0, 3.5F, -1.0F, 4.0F, 1, 4, 1, 0.0F, true));
				torso.cubeList.add(new ModelBox(torso, 27, 36, -3.5F, 0.0F, 5.0F, 7, 4, 1, 0.0F, false));
				torso.cubeList.add(new ModelBox(torso, 32, 17, -3.5F, -1.0F, 4.0F, 7, 5, 1, 0.0F, false));
				torso.cubeList.add(new ModelBox(torso, 0, 12, -3.6F, 1.2593F, -2.9789F, 7, 3, 7, 0.0F, false));
				torso.cubeList.add(new ModelBox(torso, 21, 20, -5.5F, -2.0F, -2.7F, 2, 4, 7, 0.0F, false));
				torso.cubeList.add(new ModelBox(torso, 21, 20, 3.5F, -2.0F, -2.7F, 2, 4, 7, 0.0F, true));
		
				bone83 = new ModelRenderer(this);
				bone83.setRotationPoint(0.0F, 4.0F, 6.0F);
				torso.addChild(bone83);
				setRotationAngle(bone83, 0.6545F, 0.0F, 0.0F);
				bone83.cubeList.add(new ModelBox(bone83, 20, 31, -3.5F, -3.0F, 1.35F, 7, 3, 2, 0.0F, false));
				bone83.cubeList.add(new ModelBox(bone83, 20, 31, -3.5F, -3.0F, 0.0F, 7, 3, 2, 0.0F, false));
		
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(3.0F, 2.7211F, -0.1882F);
				torso.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.0F, 0.0873F, 0.7418F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 11, 29, 0.3F, -2.1F, -2.55F, 1, 3, 7, 0.0F, true));
		
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(-3.0F, 2.7211F, -0.1882F);
				torso.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.0F, -0.0873F, -0.7418F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 11, 29, -1.3F, -2.1F, -2.55F, 1, 3, 7, 0.0F, false));

				shell = new ModelRenderer(this);
				shell.setRotationPoint(-0.1F, -7.25F, -5.0F);
				bipedBody.addChild(shell);
				setRotationAngle(shell, -0.5236F, 0.0F, 0.0F);
				shell.cubeList.add(new ModelBox(shell, 21, 0, -3.9F, -0.5F, -4.0F, 8, 1, 6, 0.0F, false));

				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(4.1F, 0.5F, 4.0F);
				shell.addChild(cube_r8);
				setRotationAngle(cube_r8, -0.5236F, 0.0F, 0.5236F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 44, -0.5F, 0.1553F, -0.1739F, 3, 1, 2, 0.0F, true));

				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(-3.9F, 0.5F, 4.0F);
				shell.addChild(cube_r9);
				setRotationAngle(cube_r9, -0.5236F, 0.0F, -0.5236F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 44, -2.5F, 0.1553F, -0.1739F, 3, 1, 2, 0.0F, false));

				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(4.1F, -0.5F, 2.0F);
				shell.addChild(cube_r10);
				setRotationAngle(cube_r10, -0.48F, 0.0F, 0.5236F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 41, 12, 0.0F, 0.0F, 0.0F, 4, 1, 2, 0.0F, true));

				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(-3.9F, -0.5F, 2.0F);
				shell.addChild(cube_r11);
				setRotationAngle(cube_r11, -0.48F, 0.0F, -0.5236F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 41, 12, -4.0F, 0.0F, 0.0F, 4, 1, 2, 0.0F, false));

				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(4.1F, -0.5F, 2.0F);
				shell.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.0F, 0.0F, 0.5236F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 32, 23, 0.0F, 0.0F, -2.0F, 5, 1, 2, 0.0F, true));
				cube_r12.cubeList.add(new ModelBox(cube_r12, 28, 7, 0.0F, 0.0F, -6.0F, 6, 1, 4, 0.0F, true));

				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(-3.9F, -0.5F, 2.0F);
				shell.addChild(cube_r13);
				setRotationAngle(cube_r13, 0.0F, 0.0F, -0.5236F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 32, 23, -5.0F, 0.0F, -2.0F, 5, 1, 2, 0.0F, false));
				cube_r13.cubeList.add(new ModelBox(cube_r13, 28, 7, -6.0F, 0.0F, -6.0F, 6, 1, 4, 0.0F, false));

				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(0.1F, 1.5F, 5.5F);
				shell.addChild(cube_r14);
				setRotationAngle(cube_r14, -0.7854F, 0.0F, 0.0F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 21, 12, -4.0F, 0.0179F, -0.0311F, 8, 1, 4, 0.0F, false));

				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(0.1F, -0.5F, 2.0F);
				shell.addChild(cube_r15);
				setRotationAngle(cube_r15, -0.5236F, 0.0F, 0.0F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 22, -4.0F, 0.0F, 0.0F, 8, 1, 4, 0.0F, false));

				spike19 = new ModelRenderer(this);
				spike19.setRotationPoint(7.6F, 1.75F, -2.25F);
				shell.addChild(spike19);
				setRotationAngle(spike19, 0.0F, 2.0508F, 0.6545F);
				spike19.cubeList.add(new ModelBox(spike19, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone95 = new ModelRenderer(this);
				bone95.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike19.addChild(bone95);
				setRotationAngle(bone95, -0.0873F, 0.0F, 0.0873F);
				bone95.cubeList.add(new ModelBox(bone95, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone96 = new ModelRenderer(this);
				bone96.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone95.addChild(bone96);
				setRotationAngle(bone96, -0.0873F, 0.0F, 0.0873F);
				bone96.cubeList.add(new ModelBox(bone96, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone97 = new ModelRenderer(this);
				bone97.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone96.addChild(bone97);
				setRotationAngle(bone97, -0.0873F, 0.0F, 0.0873F);
				bone97.cubeList.add(new ModelBox(bone97, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike22 = new ModelRenderer(this);
				spike22.setRotationPoint(2.6F, -0.15F, -2.25F);
				shell.addChild(spike22);
				setRotationAngle(spike22, 0.0F, 2.138F, 0.0F);
				spike22.cubeList.add(new ModelBox(spike22, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone103 = new ModelRenderer(this);
				bone103.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike22.addChild(bone103);
				setRotationAngle(bone103, -0.0873F, 0.0F, 0.0873F);
				bone103.cubeList.add(new ModelBox(bone103, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone104 = new ModelRenderer(this);
				bone104.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone103.addChild(bone104);
				setRotationAngle(bone104, -0.0873F, 0.0F, 0.0873F);
				bone104.cubeList.add(new ModelBox(bone104, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone105 = new ModelRenderer(this);
				bone105.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone104.addChild(bone105);
				setRotationAngle(bone105, -0.0873F, 0.0F, 0.0873F);
				bone105.cubeList.add(new ModelBox(bone105, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike32 = new ModelRenderer(this);
				spike32.setRotationPoint(5.1F, 0.55F, -1.25F);
				shell.addChild(spike32);
				setRotationAngle(spike32, 0.0F, 0.6109F, 0.3054F);
				spike32.cubeList.add(new ModelBox(spike32, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone133 = new ModelRenderer(this);
				bone133.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike32.addChild(bone133);
				setRotationAngle(bone133, -0.0873F, 0.0F, 0.0873F);
				bone133.cubeList.add(new ModelBox(bone133, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone134 = new ModelRenderer(this);
				bone134.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone133.addChild(bone134);
				setRotationAngle(bone134, -0.0873F, 0.0F, 0.0873F);
				bone134.cubeList.add(new ModelBox(bone134, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone135 = new ModelRenderer(this);
				bone135.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone134.addChild(bone135);
				setRotationAngle(bone135, -0.0873F, 0.0F, 0.0873F);
				bone135.cubeList.add(new ModelBox(bone135, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike23 = new ModelRenderer(this);
				spike23.setRotationPoint(-0.4F, -0.45F, -1.25F);
				shell.addChild(spike23);
				setRotationAngle(spike23, 0.0F, -2.5307F, 0.0F);
				spike23.cubeList.add(new ModelBox(spike23, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone106 = new ModelRenderer(this);
				bone106.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike23.addChild(bone106);
				setRotationAngle(bone106, -0.0873F, 0.0F, 0.0873F);
				bone106.cubeList.add(new ModelBox(bone106, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone107 = new ModelRenderer(this);
				bone107.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone106.addChild(bone107);
				setRotationAngle(bone107, -0.0873F, 0.0F, 0.0873F);
				bone107.cubeList.add(new ModelBox(bone107, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone108 = new ModelRenderer(this);
				bone108.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone107.addChild(bone108);
				setRotationAngle(bone108, -0.0873F, 0.0F, 0.0873F);
				bone108.cubeList.add(new ModelBox(bone108, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike26 = new ModelRenderer(this);
				spike26.setRotationPoint(0.6F, -0.35F, 1.75F);
				shell.addChild(spike26);
				setRotationAngle(spike26, 0.0F, -1.0472F, 0.0F);
				spike26.cubeList.add(new ModelBox(spike26, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone115 = new ModelRenderer(this);
				bone115.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike26.addChild(bone115);
				setRotationAngle(bone115, -0.0873F, 0.0F, 0.0873F);
				bone115.cubeList.add(new ModelBox(bone115, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone116 = new ModelRenderer(this);
				bone116.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone115.addChild(bone116);
				setRotationAngle(bone116, -0.0873F, 0.0F, 0.0873F);
				bone116.cubeList.add(new ModelBox(bone116, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone117 = new ModelRenderer(this);
				bone117.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone116.addChild(bone117);
				setRotationAngle(bone117, -0.0873F, 0.0F, 0.0873F);
				bone117.cubeList.add(new ModelBox(bone117, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike27 = new ModelRenderer(this);
				spike27.setRotationPoint(3.6F, -0.05F, 0.75F);
				shell.addChild(spike27);
				setRotationAngle(spike27, 0.0F, 0.3491F, 0.0F);
				spike27.cubeList.add(new ModelBox(spike27, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone118 = new ModelRenderer(this);
				bone118.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike27.addChild(bone118);
				setRotationAngle(bone118, -0.0873F, 0.0F, 0.0873F);
				bone118.cubeList.add(new ModelBox(bone118, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone119 = new ModelRenderer(this);
				bone119.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone118.addChild(bone119);
				setRotationAngle(bone119, -0.0873F, 0.0F, 0.0873F);
				bone119.cubeList.add(new ModelBox(bone119, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone120 = new ModelRenderer(this);
				bone120.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone119.addChild(bone120);
				setRotationAngle(bone120, -0.0873F, 0.0F, 0.0873F);
				bone120.cubeList.add(new ModelBox(bone120, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike28 = new ModelRenderer(this);
				spike28.setRotationPoint(4.6F, 0.65F, 2.75F);
				shell.addChild(spike28);
				setRotationAngle(spike28, -0.5672F, 0.6545F, 0.0F);
				spike28.cubeList.add(new ModelBox(spike28, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone121 = new ModelRenderer(this);
				bone121.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike28.addChild(bone121);
				setRotationAngle(bone121, -0.0873F, 0.0F, 0.0873F);
				bone121.cubeList.add(new ModelBox(bone121, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone122 = new ModelRenderer(this);
				bone122.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone121.addChild(bone122);
				setRotationAngle(bone122, -0.0873F, 0.0F, 0.0873F);
				bone122.cubeList.add(new ModelBox(bone122, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone123 = new ModelRenderer(this);
				bone123.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone122.addChild(bone123);
				setRotationAngle(bone123, -0.0873F, 0.0F, 0.0873F);
				bone123.cubeList.add(new ModelBox(bone123, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike29 = new ModelRenderer(this);
				spike29.setRotationPoint(-5.4F, 0.55F, 0.75F);
				shell.addChild(spike29);
				setRotationAngle(spike29, -0.5236F, -1.7017F, 0.0F);
				spike29.cubeList.add(new ModelBox(spike29, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone124 = new ModelRenderer(this);
				bone124.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike29.addChild(bone124);
				setRotationAngle(bone124, -0.0873F, 0.0F, 0.0873F);
				bone124.cubeList.add(new ModelBox(bone124, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone125 = new ModelRenderer(this);
				bone125.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone124.addChild(bone125);
				setRotationAngle(bone125, -0.0873F, 0.0F, 0.0873F);
				bone125.cubeList.add(new ModelBox(bone125, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone126 = new ModelRenderer(this);
				bone126.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone125.addChild(bone126);
				setRotationAngle(bone126, -0.0873F, 0.0F, 0.0873F);
				bone126.cubeList.add(new ModelBox(bone126, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike30 = new ModelRenderer(this);
				spike30.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike29.addChild(spike30);
				setRotationAngle(spike30, 0.0F, -2.5307F, 0.1745F);
				spike30.cubeList.add(new ModelBox(spike30, 0, 23, -3.0673F, -0.2679F, 0.4344F, 1, 1, 1, -0.1F, false));

				bone127 = new ModelRenderer(this);
				bone127.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike30.addChild(bone127);
				setRotationAngle(bone127, -0.0873F, 0.0F, 0.0873F);
				bone127.cubeList.add(new ModelBox(bone127, 0, 23, -2.9937F, -0.132F, 0.5139F, 1, 1, 1, -0.2F, false));

				bone128 = new ModelRenderer(this);
				bone128.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone127.addChild(bone128);
				setRotationAngle(bone128, -0.0873F, 0.0F, 0.0873F);
				bone128.cubeList.add(new ModelBox(bone128, 0, 23, -2.9086F, -0.0105F, 0.6044F, 1, 1, 1, -0.3F, false));

				bone129 = new ModelRenderer(this);
				bone129.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone128.addChild(bone129);
				setRotationAngle(bone129, -0.0873F, 0.0F, 0.0873F);
				bone129.cubeList.add(new ModelBox(bone129, 0, 23, -2.8132F, 0.0949F, 0.7044F, 1, 1, 1, -0.4F, false));

				spike31 = new ModelRenderer(this);
				spike31.setRotationPoint(-3.0F, -0.8F, 1.0F);
				spike29.addChild(spike31);
				setRotationAngle(spike31, 0.0F, -2.5307F, 0.1745F);
				spike31.cubeList.add(new ModelBox(spike31, 0, 23, -3.0673F, -0.2679F, 0.4344F, 1, 1, 1, -0.1F, false));

				bone130 = new ModelRenderer(this);
				bone130.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike31.addChild(bone130);
				setRotationAngle(bone130, -0.0873F, 0.0F, 0.0873F);
				bone130.cubeList.add(new ModelBox(bone130, 0, 23, -2.9937F, -0.132F, 0.5139F, 1, 1, 1, -0.2F, false));

				bone131 = new ModelRenderer(this);
				bone131.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone130.addChild(bone131);
				setRotationAngle(bone131, -0.0873F, 0.0F, 0.0873F);
				bone131.cubeList.add(new ModelBox(bone131, 0, 23, -2.9086F, -0.0105F, 0.6044F, 1, 1, 1, -0.3F, false));

				bone132 = new ModelRenderer(this);
				bone132.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone131.addChild(bone132);
				setRotationAngle(bone132, -0.0873F, 0.0F, 0.0873F);
				bone132.cubeList.add(new ModelBox(bone132, 0, 23, -2.8132F, 0.0949F, 0.7044F, 1, 1, 1, -0.4F, false));

				spike24 = new ModelRenderer(this);
				spike24.setRotationPoint(-6.4F, 1.55F, -1.25F);
				shell.addChild(spike24);
				setRotationAngle(spike24, 0.6545F, 1.9635F, 0.0F);
				spike24.cubeList.add(new ModelBox(spike24, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone109 = new ModelRenderer(this);
				bone109.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike24.addChild(bone109);
				setRotationAngle(bone109, -0.0873F, 0.0F, 0.0873F);
				bone109.cubeList.add(new ModelBox(bone109, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone110 = new ModelRenderer(this);
				bone110.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone109.addChild(bone110);
				setRotationAngle(bone110, -0.0873F, 0.0F, 0.0873F);
				bone110.cubeList.add(new ModelBox(bone110, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone111 = new ModelRenderer(this);
				bone111.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone110.addChild(bone111);
				setRotationAngle(bone111, -0.0873F, 0.0F, 0.0873F);
				bone111.cubeList.add(new ModelBox(bone111, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike25 = new ModelRenderer(this);
				spike25.setRotationPoint(-2.4F, 0.05F, -3.25F);
				shell.addChild(spike25);
				setRotationAngle(spike25, -0.1309F, 2.5307F, 0.0873F);
				spike25.cubeList.add(new ModelBox(spike25, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone112 = new ModelRenderer(this);
				bone112.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike25.addChild(bone112);
				setRotationAngle(bone112, -0.0873F, 0.0F, 0.0873F);
				bone112.cubeList.add(new ModelBox(bone112, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone113 = new ModelRenderer(this);
				bone113.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone112.addChild(bone113);
				setRotationAngle(bone113, -0.0873F, 0.0F, 0.0873F);
				bone113.cubeList.add(new ModelBox(bone113, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone114 = new ModelRenderer(this);
				bone114.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone113.addChild(bone114);
				setRotationAngle(bone114, -0.0873F, 0.0F, 0.0873F);
				bone114.cubeList.add(new ModelBox(bone114, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				spike33 = new ModelRenderer(this);
				spike33.setRotationPoint(-7.8F, 1.85F, -3.25F);
				shell.addChild(spike33);
				setRotationAngle(spike33, -0.1309F, 2.5307F, -0.7418F);
				spike33.cubeList.add(new ModelBox(spike33, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone136 = new ModelRenderer(this);
				bone136.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike33.addChild(bone136);
				setRotationAngle(bone136, -0.0873F, 0.0F, 0.0873F);
				bone136.cubeList.add(new ModelBox(bone136, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone137 = new ModelRenderer(this);
				bone137.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone136.addChild(bone137);
				setRotationAngle(bone137, -0.0873F, 0.0F, 0.0873F);
				bone137.cubeList.add(new ModelBox(bone137, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone138 = new ModelRenderer(this);
				bone138.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone137.addChild(bone138);
				setRotationAngle(bone138, -0.0873F, 0.0F, 0.0873F);
				bone138.cubeList.add(new ModelBox(bone138, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.5F, -5.0F, -8.0F);
				bipedBody.addChild(bipedRightArm);


				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(-2.4F, 4.4526F, -2.95F);
				bipedRightArm.addChild(cube_r16);
				setRotationAngle(cube_r16, -0.48F, -0.0873F, 0.0F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 36, -1.3F, -2.5F, -1.1F, 3, 5, 3, 0.0F, false));

				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(-2.0F, 1.0F, -1.0F);
				bipedRightArm.addChild(cube_r17);
				setRotationAngle(cube_r17, -0.48F, 0.0F, 0.5236F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 40, 38, -1.5F, -2.0F, -1.5F, 3, 4, 3, 0.0F, false));

				spike20 = new ModelRenderer(this);
				spike20.setRotationPoint(-3.0F, 0.3F, -1.25F);
				bipedRightArm.addChild(spike20);
				setRotationAngle(spike20, 0.1309F, 0.8727F, -1.0472F);
				spike20.cubeList.add(new ModelBox(spike20, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));

				bone94 = new ModelRenderer(this);
				bone94.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike20.addChild(bone94);
				setRotationAngle(bone94, -0.0873F, 0.0F, 0.0873F);
				bone94.cubeList.add(new ModelBox(bone94, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));

				bone98 = new ModelRenderer(this);
				bone98.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone94.addChild(bone98);
				setRotationAngle(bone98, -0.0873F, 0.0F, 0.0873F);
				bone98.cubeList.add(new ModelBox(bone98, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));

				bone99 = new ModelRenderer(this);
				bone99.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone98.addChild(bone99);
				setRotationAngle(bone99, -0.0873F, 0.0F, 0.0873F);
				bone99.cubeList.add(new ModelBox(bone99, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));

				RightHand = new ModelRenderer(this);
				RightHand.setRotationPoint(-2.0F, 6.5F, -4.0F);
				bipedRightArm.addChild(RightHand);
				RightHand.cubeList.add(new ModelBox(RightHand, 12, 39, -1.6F, 0.0F, -3.0F, 3, 1, 4, 0.0F, false));
				RightHand.cubeList.add(new ModelBox(RightHand, 36, 27, -1.6F, 0.5F, -3.0F, 3, 1, 4, 0.0F, false));

				bone84 = new ModelRenderer(this);
				bone84.setRotationPoint(-0.1F, 0.6F, -1.75F);
				RightHand.addChild(bone84);
				bone84.cubeList.add(new ModelBox(bone84, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));

				bone85 = new ModelRenderer(this);
				bone85.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone84.addChild(bone85);
				setRotationAngle(bone85, 0.5236F, 0.0F, 0.0F);
				bone85.cubeList.add(new ModelBox(bone85, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));

				bone86 = new ModelRenderer(this);
				bone86.setRotationPoint(-1.2F, 0.6F, -1.75F);
				RightHand.addChild(bone86);
				bone86.cubeList.add(new ModelBox(bone86, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));

				bone87 = new ModelRenderer(this);
				bone87.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone86.addChild(bone87);
				setRotationAngle(bone87, 0.5236F, 0.0F, 0.0F);
				bone87.cubeList.add(new ModelBox(bone87, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));

				bone88 = new ModelRenderer(this);
				bone88.setRotationPoint(-1.2F, 0.6F, -1.5F);
				RightHand.addChild(bone88);
				setRotationAngle(bone88, 0.0F, 0.48F, 0.0F);
				bone88.cubeList.add(new ModelBox(bone88, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));

				bone139 = new ModelRenderer(this);
				bone139.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone88.addChild(bone139);
				setRotationAngle(bone139, 0.5236F, 0.0F, 0.0F);
				bone139.cubeList.add(new ModelBox(bone139, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));

				bone143 = new ModelRenderer(this);
				bone143.setRotationPoint(1.0F, 0.6F, -1.75F);
				RightHand.addChild(bone143);
				bone143.cubeList.add(new ModelBox(bone143, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));

				bone144 = new ModelRenderer(this);
				bone144.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone143.addChild(bone144);
				setRotationAngle(bone144, 0.5236F, 0.0F, 0.0F);
				bone144.cubeList.add(new ModelBox(bone144, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));

				bone140 = new ModelRenderer(this);
				bone140.setRotationPoint(1.15F, 0.6F, -0.75F);
				RightHand.addChild(bone140);
				setRotationAngle(bone140, 0.0F, -1.0472F, 0.0F);
				bone140.cubeList.add(new ModelBox(bone140, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));

				bone141 = new ModelRenderer(this);
				bone141.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone140.addChild(bone141);
				setRotationAngle(bone141, 0.5236F, 0.2618F, 0.0F);
				bone141.cubeList.add(new ModelBox(bone141, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));

				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.5F, -5.0F, -8.0F);
				bipedBody.addChild(bipedLeftArm);


				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(2.4F, 4.4526F, -2.95F);
				bipedLeftArm.addChild(cube_r18);
				setRotationAngle(cube_r18, -0.48F, 0.0873F, 0.0F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 36, -1.7F, -2.5F, -1.1F, 3, 5, 3, 0.0F, true));

				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(2.0F, 1.0F, -1.0F);
				bipedLeftArm.addChild(cube_r19);
				setRotationAngle(cube_r19, -0.48F, 0.0F, -0.5236F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 40, 38, -1.5F, -2.0F, -1.5F, 3, 4, 3, 0.0F, true));

				spike21 = new ModelRenderer(this);
				spike21.setRotationPoint(3.0F, 0.3F, -1.25F);
				bipedLeftArm.addChild(spike21);
				setRotationAngle(spike21, 0.1309F, -0.8727F, 1.0472F);
				spike21.cubeList.add(new ModelBox(spike21, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));

				bone89 = new ModelRenderer(this);
				bone89.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike21.addChild(bone89);
				setRotationAngle(bone89, -0.0873F, 0.0F, -0.0873F);
				bone89.cubeList.add(new ModelBox(bone89, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));

				bone90 = new ModelRenderer(this);
				bone90.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone89.addChild(bone90);
				setRotationAngle(bone90, -0.0873F, 0.0F, -0.0873F);
				bone90.cubeList.add(new ModelBox(bone90, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));

				bone91 = new ModelRenderer(this);
				bone91.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone90.addChild(bone91);
				setRotationAngle(bone91, -0.0873F, 0.0F, -0.0873F);
				bone91.cubeList.add(new ModelBox(bone91, 0, 23, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));

				LeftHand = new ModelRenderer(this);
				LeftHand.setRotationPoint(2.0F, 6.5F, -4.0F);
				bipedLeftArm.addChild(LeftHand);
				LeftHand.cubeList.add(new ModelBox(LeftHand, 12, 39, -1.4F, 0.0F, -3.0F, 3, 1, 4, 0.0F, true));
				LeftHand.cubeList.add(new ModelBox(LeftHand, 36, 27, -1.4F, 0.5F, -3.0F, 3, 1, 4, 0.0F, true));

				bone92 = new ModelRenderer(this);
				bone92.setRotationPoint(0.1F, 0.6F, -1.75F);
				LeftHand.addChild(bone92);
				bone92.cubeList.add(new ModelBox(bone92, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));

				bone93 = new ModelRenderer(this);
				bone93.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone92.addChild(bone93);
				setRotationAngle(bone93, 0.5236F, 0.0F, 0.0F);
				bone93.cubeList.add(new ModelBox(bone93, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));

				bone100 = new ModelRenderer(this);
				bone100.setRotationPoint(1.2F, 0.6F, -1.75F);
				LeftHand.addChild(bone100);
				bone100.cubeList.add(new ModelBox(bone100, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));

				bone101 = new ModelRenderer(this);
				bone101.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone100.addChild(bone101);
				setRotationAngle(bone101, 0.5236F, 0.0F, 0.0F);
				bone101.cubeList.add(new ModelBox(bone101, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));

				bone102 = new ModelRenderer(this);
				bone102.setRotationPoint(1.2F, 0.6F, -1.5F);
				LeftHand.addChild(bone102);
				setRotationAngle(bone102, 0.0F, -0.48F, 0.0F);
				bone102.cubeList.add(new ModelBox(bone102, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));

				bone142 = new ModelRenderer(this);
				bone142.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone102.addChild(bone142);
				setRotationAngle(bone142, 0.5236F, 0.0F, 0.0F);
				bone142.cubeList.add(new ModelBox(bone142, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));

				bone145 = new ModelRenderer(this);
				bone145.setRotationPoint(-1.0F, 0.6F, -1.75F);
				LeftHand.addChild(bone145);
				bone145.cubeList.add(new ModelBox(bone145, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));

				bone146 = new ModelRenderer(this);
				bone146.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone145.addChild(bone146);
				setRotationAngle(bone146, 0.5236F, 0.0F, 0.0F);
				bone146.cubeList.add(new ModelBox(bone146, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));

				bone147 = new ModelRenderer(this);
				bone147.setRotationPoint(-1.15F, 0.6F, -0.75F);
				LeftHand.addChild(bone147);
				setRotationAngle(bone147, 0.0F, 1.0472F, 0.0F);
				bone147.cubeList.add(new ModelBox(bone147, 21, 0, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));

				bone148 = new ModelRenderer(this);
				bone148.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone147.addChild(bone148);
				setRotationAngle(bone148, 0.5236F, -0.2618F, 0.0F);
				bone148.cubeList.add(new ModelBox(bone148, 21, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));

				tails = new ModelRenderer(this);
				tails.setRotationPoint(0.0F, 21.0F, 7.5F);
				//setRotationAngle(tails, -0.8727F, 0.0F, 0.0F);


				tail[0][0] = new ModelRenderer(this);
				tail[0][0].setRotationPoint(0.0F, 0.0F, 0.0F);
				tails.addChild(tail[0][0]);
				setRotationAngle(tail[0][0], -0.7854F, 0.0F, 0.0F);
				tail[0][0].cubeList.add(new ModelBox(tail[0][0], 54, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.3F, false));

				tail[0][1] = new ModelRenderer(this);
				tail[0][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][0].addChild(tail[0][1]);
				setRotationAngle(tail[0][1], 0.2618F, 0.0F, 0.0F);
				tail[0][1].cubeList.add(new ModelBox(tail[0][1], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.3F, false));

				tail[0][2] = new ModelRenderer(this);
				tail[0][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][1].addChild(tail[0][2]);
				setRotationAngle(tail[0][2], 0.2618F, 0.0F, 0.0F);
				tail[0][2].cubeList.add(new ModelBox(tail[0][2], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.25F, false));

				tail[0][3] = new ModelRenderer(this);
				tail[0][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][2].addChild(tail[0][3]);
				setRotationAngle(tail[0][3], 0.2618F, 0.0F, 0.0F);
				tail[0][3].cubeList.add(new ModelBox(tail[0][3], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.2F, false));

				tail[0][4] = new ModelRenderer(this);
				tail[0][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][3].addChild(tail[0][4]);
				setRotationAngle(tail[0][4], 0.2618F, 0.0F, 0.0F);
				tail[0][4].cubeList.add(new ModelBox(tail[0][4], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.15F, false));

				tail[0][5] = new ModelRenderer(this);
				tail[0][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][4].addChild(tail[0][5]);
				setRotationAngle(tail[0][5], -0.2618F, 0.0F, 0.0F);
				tail[0][5].cubeList.add(new ModelBox(tail[0][5], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.1F, false));

				tail[0][6] = new ModelRenderer(this);
				tail[0][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][5].addChild(tail[0][6]);
				setRotationAngle(tail[0][6], -0.2618F, 0.0F, 0.0F);
				tail[0][6].cubeList.add(new ModelBox(tail[0][6], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.05F, false));

				tail[0][7] = new ModelRenderer(this);
				tail[0][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][6].addChild(tail[0][7]);
				setRotationAngle(tail[0][7], -0.2618F, 0.0F, 0.0F);
				tail[0][7].cubeList.add(new ModelBox(tail[0][7], 54, 59, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.0F, false));

				tail[1][0] = new ModelRenderer(this);
				tail[1][0].setRotationPoint(2.0F, 0.0F, 0.0F);
				tails.addChild(tail[1][0]);
				setRotationAngle(tail[1][0], -1.309F, 0.6981F, 0.0F);
				tail[1][0].cubeList.add(new ModelBox(tail[1][0], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.3F, false));

				tail[1][1] = new ModelRenderer(this);
				tail[1][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][0].addChild(tail[1][1]);
				setRotationAngle(tail[1][1], 0.2618F, 0.0F, 0.0F);
				tail[1][1].cubeList.add(new ModelBox(tail[1][1], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.3F, false));

				tail[1][2] = new ModelRenderer(this);
				tail[1][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][1].addChild(tail[1][2]);
				setRotationAngle(tail[1][2], 0.2618F, 0.0F, 0.0F);
				tail[1][2].cubeList.add(new ModelBox(tail[1][2], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.25F, false));

				tail[1][3] = new ModelRenderer(this);
				tail[1][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][2].addChild(tail[1][3]);
				setRotationAngle(tail[1][3], 0.2618F, 0.0F, 0.0F);
				tail[1][3].cubeList.add(new ModelBox(tail[1][3], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.2F, false));

				tail[1][4] = new ModelRenderer(this);
				tail[1][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][3].addChild(tail[1][4]);
				setRotationAngle(tail[1][4], 0.2618F, 0.0F, 0.0F);
				tail[1][4].cubeList.add(new ModelBox(tail[1][4], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.15F, false));

				tail[1][5] = new ModelRenderer(this);
				tail[1][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][4].addChild(tail[1][5]);
				setRotationAngle(tail[1][5], -0.2618F, 0.0F, 0.0F);
				tail[1][5].cubeList.add(new ModelBox(tail[1][5], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.1F, false));

				tail[1][6] = new ModelRenderer(this);
				tail[1][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][5].addChild(tail[1][6]);
				setRotationAngle(tail[1][6], -0.2618F, 0.0F, 0.0F);
				tail[1][6].cubeList.add(new ModelBox(tail[1][6], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.05F, false));

				tail[1][7] = new ModelRenderer(this);
				tail[1][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][6].addChild(tail[1][7]);
				setRotationAngle(tail[1][7], -0.2618F, 0.0F, 0.0F);
				tail[1][7].cubeList.add(new ModelBox(tail[1][7], 54, 59, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.0F, false));

				tail[2][0] = new ModelRenderer(this);
				tail[2][0].setRotationPoint(-2.0F, 0.0F, 0.0F);
				tails.addChild(tail[2][0]);
				setRotationAngle(tail[2][0], -1.309F, -0.6981F, 0.0F);
				tail[2][0].cubeList.add(new ModelBox(tail[2][0], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.3F, true));

				tail[2][1] = new ModelRenderer(this);
				tail[2][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][0].addChild(tail[2][1]);
				setRotationAngle(tail[2][1], 0.2618F, 0.0F, 0.0F);
				tail[2][1].cubeList.add(new ModelBox(tail[2][1], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.3F, true));

				tail[2][2] = new ModelRenderer(this);
				tail[2][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][1].addChild(tail[2][2]);
				setRotationAngle(tail[2][2], 0.2618F, 0.0F, 0.0F);
				tail[2][2].cubeList.add(new ModelBox(tail[2][2], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.25F, true));

				tail[2][3] = new ModelRenderer(this);
				tail[2][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][2].addChild(tail[2][3]);
				setRotationAngle(tail[2][3], 0.2618F, 0.0F, 0.0F);
				tail[2][3].cubeList.add(new ModelBox(tail[2][3], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.2F, true));

				tail[2][4] = new ModelRenderer(this);
				tail[2][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][3].addChild(tail[2][4]);
				setRotationAngle(tail[2][4], 0.2618F, 0.0F, 0.0F);
				tail[2][4].cubeList.add(new ModelBox(tail[2][4], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.15F, true));

				tail[2][5] = new ModelRenderer(this);
				tail[2][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][4].addChild(tail[2][5]);
				setRotationAngle(tail[2][5], -0.2618F, 0.0F, 0.0F);
				tail[2][5].cubeList.add(new ModelBox(tail[2][5], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.1F, true));

				tail[2][6] = new ModelRenderer(this);
				tail[2][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][5].addChild(tail[2][6]);
				setRotationAngle(tail[2][6], -0.2618F, 0.0F, 0.0F);
				tail[2][6].cubeList.add(new ModelBox(tail[2][6], 43, 0, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.05F, true));

				tail[2][7] = new ModelRenderer(this);
				tail[2][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][6].addChild(tail[2][7]);
				setRotationAngle(tail[2][7], -0.2618F, 0.0F, 0.0F);
				tail[2][7].cubeList.add(new ModelBox(tail[2][7], 54, 59, -2.0F, -4.0F, -0.5F, 4, 4, 1, 0.0F, true));

				for (int i = 0; i < 3; i++) {
					for (int j = 1; j < 8; j++) {
						tailSwayX[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
						tailSwayZ[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
						tailSwayY[i][j] = (rand.nextFloat() * 0.0873F + 0.0873F);
					}
				}
				bipedRightLeg.showModel = false;
				bipedLeftLeg.showModel = false;
			}

			@Override
			public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				float scale = ((EntityTailedBeast.Base) entity).getModelScale();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * scale, 0.0F);
				//GlStateManager.translate(0.0F, 0.0F, 0.375F * MODELSCALE);
				GlStateManager.scale(scale, scale, scale);
				bipedBody.render(f5);
				//super.render(entity, f0, f1, f2, f3, f4, f5);
				tails.render(f5);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				bipedHeadwear.render(f5);
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}

			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}

			@Override
			public void setRotationAngles(float f0, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f0 * 0.25F, f1, f2, f3, f4, f5, e);
				bipedHead.rotationPointY += -5.0F;
				bipedRightArm.setRotationPoint(-5.5F, -5.0F, -8.0F);
				bipedLeftArm.setRotationPoint(5.5F, -5.0F, -8.0F);
				for (int i = 0; i < 3; i++) {
					for (int j = 1; j < 8; j++) {
						if (j <= 4) {
							tail[i][j].rotateAngleX = 0.2618F + MathHelper.sin((f2 - j) * 0.05F) * tailSwayX[i][j];
						} else {
							tail[i][j].rotateAngleX = -0.2618F + MathHelper.sin((f2 - j) * 0.05F) * tailSwayX[i][j];
						}
						tail[i][j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.05F) * tailSwayZ[i][j];
						tail[i][j].rotateAngleY = MathHelper.sin((f2 - j) * 0.1F) * tailSwayY[i][j];
					}
				}
				if (((EntityCustom) e).isShooting()) {
					bipedHead.rotateAngleX += -0.1745F;
					Jaw.rotateAngleX = 0.7854F;
				} else {
					Jaw.rotateAngleX = 0.0F;
				}
				if (((EntityCustom) e).isFaceDown()) {
					bipedBody.rotationPointZ = 11.0F;
					bipedBody.rotateAngleX = 0.4363F;
					bipedHead.rotateAngleX = -0.2618F;
					bipedRightArm.rotateAngleX = -0.8727F;
					bipedLeftArm.rotateAngleX = -0.8727F;
					tails.rotationPointZ = 11.0F;
					tails.rotateAngleX = -0.8727F;
				} else {
					bipedBody.rotationPointZ = 7.5F;
					bipedBody.rotateAngleX = 0.0F;
					tails.rotationPointZ = 7.5F;
					tails.rotateAngleX = 0.0F;
				}
				this.copyModelAngles(bipedBody, bipedHeadwear);
				this.copyModelAngles(bipedHead, eye);
			}
		}
	}
}
