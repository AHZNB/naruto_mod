
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemFutton;
import net.narutomod.item.ItemKaton;
import net.narutomod.item.ItemSuiton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFiveTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 261;
	public static final int ENTITYID_RANGED = 262;
	private static final float MODELSCALE = 20.0F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntityFiveTails(ElementsNarutomodMod instance) {
		super(instance, 584);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "five_tails"), ENTITYID).name("five_tails")
				.tracker(96, 3, true).egg(-1, -3355648).build());
	}

	public static TailBeastManager getBijuManager() {
		return tailBeastManager;
	}

	public static class TailBeastManager extends EntityBijuManager<EntityCustom> {
		public TailBeastManager() {
			super(EntityCustom.class, 5);
		}

		@Override
		public void setVesselEntity(@Nullable Entity player) {
			super.setVesselEntity(player);
			if (player instanceof EntityPlayer && !ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemFutton.block)) {
				ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, new ItemStack(ItemFutton.block));
				if (!ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemKaton.block)) {
					ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, new ItemStack(ItemKaton.block));
				}
				if (!ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemSuiton.block)) {
					ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, new ItemStack(ItemSuiton.block));
				}
			}
		}

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}

	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_fivetails";
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
		public EntityCustom(World world) {
			super(world);
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
		public float getModelScale() {
			return MODELSCALE;
		}

		@Override
		public void setFaceDown(boolean down) {
			super.setFaceDown(down);
			this.setSize(this.width, MODELSCALE * (down ? 0.5F : 0.7F));
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.8D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10000.0D);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(30.0D);
		}

		@Override
		public EntityBijuManager getBijuManager() {
			return tailBeastManager;
		}

		@Override
		public double getMountedYOffset() {
			return this.isFaceDown() ? 3.0d * 0.0625d * MODELSCALE : (12.0d * 0.0625d * MODELSCALE);
		}

		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0.0d, 0.0d, 0.5d * MODELSCALE) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-this.rotationYaw * 0.017453292F);
				passenger.setPosition(this.posX + vec2.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec2.z);
			}
		}

		@Override
		public float getFuuinBeamHeight() {
			return this.isFaceDown() ? 3.0f * 0.0625f * MODELSCALE : super.getFuuinBeamHeight();
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return null;
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return null;
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return null;
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/fivetails.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelFiveTails(), MODELSCALE * 0.5F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 3.8.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelFiveTails extends ModelQuadruped {
			private final ModelRenderer eyesHighlight;
			private final ModelRenderer headsync;
			private final ModelRenderer eye4_r1;
			private final ModelRenderer eye3_r1;
			private final ModelRenderer eye3_r2;
			//private final ModelRenderer body;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			//private final ModelRenderer head;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer bone;
			private final ModelRenderer cube_r6;
			private final ModelRenderer cube_r7;
			private final ModelRenderer cube_r8;
			private final ModelRenderer bone4;
			private final ModelRenderer cube_r9;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer bone5;
			private final ModelRenderer cube_r12;
			private final ModelRenderer cube_r13;
			private final ModelRenderer cube_r14;
			private final ModelRenderer bone3;
			private final ModelRenderer cube_r15;
			private final ModelRenderer cube_r16;
			private final ModelRenderer cube_r17;
			private final ModelRenderer eyes;
			private final ModelRenderer eye2_r1;
			private final ModelRenderer eye1_r1;
			private final ModelRenderer eye1_r2;
			private final ModelRenderer jaw;
			private final ModelRenderer cube_r18;
			private final ModelRenderer cube_r19;
			private final ModelRenderer bone2;
			//private final ModelRenderer leg1;
			private final ModelRenderer cube_r20;
			private final ModelRenderer cube_r21;
			private final ModelRenderer cube_r22;
			private final ModelRenderer Foot;
			private final ModelRenderer hoof_r1;
			private final ModelRenderer hoof_r2;
			private final ModelRenderer hoof_r3;
			//private final ModelRenderer leg2;
			private final ModelRenderer cube_r23;
			private final ModelRenderer cube_r24;
			private final ModelRenderer cube_r25;
			private final ModelRenderer Foot2;
			private final ModelRenderer hoof_r4;
			private final ModelRenderer hoof_r5;
			private final ModelRenderer hoof_r6;
			//private final ModelRenderer leg3;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer Foot3;
			private final ModelRenderer hoof_r7;
			private final ModelRenderer hoof_r8;
			private final ModelRenderer hoof_r9;
			//private final ModelRenderer leg4;
			private final ModelRenderer bone9;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer Foot4;
			private final ModelRenderer hoof_r10;
			private final ModelRenderer hoof_r11;
			private final ModelRenderer hoof_r12;
			private final ModelRenderer[][] tail = new ModelRenderer[5][8];
			private final float tailSwayX[][] = new float[5][8];
			private final float tailSwayY[][] = new float[5][8];
			private final float tailSwayZ[][] = new float[5][8];
			private final Random rand = new Random();

			public ModelFiveTails() {
				super(12, 0.0F);

				textureWidth = 64;
				textureHeight = 64;

				eyesHighlight = new ModelRenderer(this);
				eyesHighlight.setRotationPoint(0.0F, 13.0F, 0.0F);


				headsync = new ModelRenderer(this);
				headsync.setRotationPoint(0.0F, 0.0F, -4.0F);
				eyesHighlight.addChild(headsync);


				eye4_r1 = new ModelRenderer(this);
				eye4_r1.setRotationPoint(-2.0F, 0.3521F, -5.7954F);
				headsync.addChild(eye4_r1);
				setRotationAngle(eye4_r1, 0.2618F, 0.0F, 0.0F);
				eye4_r1.cubeList.add(new ModelBox(eye4_r1, 48, 9, -0.53F, -1.1F, -0.5F, 1, 2, 2, -0.4F, false));
				eye4_r1.cubeList.add(new ModelBox(eye4_r1, 48, 9, 3.53F, -1.1F, -0.5F, 1, 2, 2, -0.4F, true));

				eye3_r1 = new ModelRenderer(this);
				eye3_r1.setRotationPoint(-2.0099F, 0.5605F, -6.9564F);
				headsync.addChild(eye3_r1);
				setRotationAngle(eye3_r1, 0.2618F, -0.1693F, -0.0436F);
				eye3_r1.cubeList.add(new ModelBox(eye3_r1, 56, 9, -0.33F, -1.0F, -0.5F, 1, 2, 2, -0.4F, false));

				eye3_r2 = new ModelRenderer(this);
				eye3_r2.setRotationPoint(1.9099F, 0.5605F, -6.9564F);
				headsync.addChild(eye3_r2);
				setRotationAngle(eye3_r2, 0.2618F, 0.1693F, 0.0436F);
				eye3_r2.cubeList.add(new ModelBox(eye3_r2, 56, 9, -0.57F, -1.0F, -0.5F, 1, 2, 2, -0.4F, true));

				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 13.0F, 0.0F);
				body.cubeList.add(new ModelBox(body, 0, 13, -3.0F, -2.1F, -3.4F, 6, 6, 7, -0.1F, false));

				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, 1.2393F, 8.4817F);
				body.addChild(cube_r1);
				setRotationAngle(cube_r1, -0.1745F, 0.0F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -3.0F, -2.6F, -6.0F, 6, 5, 8, -0.3F, false));

				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(0.0F, 3.2643F, 2.7833F);
				body.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.1309F, 0.0F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 22, 22, -2.5F, -1.4F, 0.6F, 5, 2, 4, 0.0F, false));

				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 0.0F, -4.0F);
				body.addChild(head);


				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(0.0F, 0.0272F, -8.158F);
				head.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.48F, 0.0F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 32, 0, -2.0F, -0.4F, -0.4F, 4, 2, 2, -0.36F, false));

				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(0.0F, 0.1F, -7.5F);
				head.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.2618F, 0.0F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 28, 8, -2.0F, -0.4F, -0.7F, 4, 2, 3, -0.1F, false));

				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.0F, 0.3861F, 0.5902F);
				head.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.2182F, 0.0F, 0.0F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 26, -2.0F, -2.5658F, -6.2052F, 4, 4, 4, 0.0F, false));

				bone = new ModelRenderer(this);
				bone.setRotationPoint(-1.2F, -0.7182F, -4.8788F);
				head.addChild(bone);
				setRotationAngle(bone, -0.0873F, 0.0F, -0.4363F);


				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(0.0F, -3.0F, 1.0F);
				bone.addChild(cube_r6);
				setRotationAngle(cube_r6, -0.4363F, 0.0F, 0.0F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 20, 37, -0.5F, -1.0F, 0.0F, 1, 2, 1, -0.2F, false));

				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(0.0F, -2.517F, 0.8706F);
				bone.addChild(cube_r7);
				setRotationAngle(cube_r7, -0.3491F, 0.0F, 0.0F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 40, 13, -0.5F, -0.5F, -0.1F, 1, 2, 1, -0.1F, false));

				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(0.0F, -0.6818F, 0.3788F);
				bone.addChild(cube_r8);
				setRotationAngle(cube_r8, -0.2618F, 0.0F, 0.0F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, false));
				cube_r8.cubeList.add(new ModelBox(cube_r8, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, false));

				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-1.2F, -1.1182F, -3.1288F);
				head.addChild(bone4);
				setRotationAngle(bone4, -0.2182F, 0.0F, -0.3491F);


				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(0.0F, -3.0F, 1.5F);
				bone4.addChild(cube_r9);
				setRotationAngle(cube_r9, -0.4363F, 0.0F, 0.0F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 20, 37, -0.5F, -1.5F, 0.0F, 1, 2, 1, -0.2F, false));

				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(0.0F, -2.517F, 1.3706F);
				bone4.addChild(cube_r10);
				setRotationAngle(cube_r10, -0.3491F, 0.0F, 0.0F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 40, 13, -0.5F, -1.0F, -0.1F, 1, 2, 1, -0.1F, false));

				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(0.0F, -0.6818F, 0.8788F);
				bone4.addChild(cube_r11);
				setRotationAngle(cube_r11, -0.2618F, 0.0F, 0.0F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, false));
				cube_r11.cubeList.add(new ModelBox(cube_r11, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, false));

				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(1.2F, -1.1182F, -3.1288F);
				head.addChild(bone5);
				setRotationAngle(bone5, -0.3054F, 0.0F, 0.3054F);


				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(0.0F, -3.0F, 1.5F);
				bone5.addChild(cube_r12);
				setRotationAngle(cube_r12, -0.4363F, 0.0F, 0.0F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 20, 37, -0.5F, -1.5F, 0.0F, 1, 2, 1, -0.2F, true));

				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(0.0F, -2.517F, 1.3706F);
				bone5.addChild(cube_r13);
				setRotationAngle(cube_r13, -0.3491F, 0.0F, 0.0F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 40, 13, -0.5F, -1.0F, -0.1F, 1, 2, 1, -0.1F, true));

				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(0.0F, -0.6818F, 0.8788F);
				bone5.addChild(cube_r14);
				setRotationAngle(cube_r14, -0.2618F, 0.0F, 0.0F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, true));
				cube_r14.cubeList.add(new ModelBox(cube_r14, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, true));

				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(1.2F, -0.7182F, -5.8788F);
				head.addChild(bone3);
				setRotationAngle(bone3, -0.1745F, 0.0F, 0.4363F);


				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(0.0F, -3.1736F, 2.9848F);
				bone3.addChild(cube_r15);
				setRotationAngle(cube_r15, -0.4363F, 0.0F, 0.0F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 20, 37, -0.5F, -1.0F, -1.0F, 1, 2, 1, -0.2F, true));

				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(0.0F, -2.6907F, 2.8554F);
				bone3.addChild(cube_r16);
				setRotationAngle(cube_r16, -0.3491F, 0.0F, 0.0F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 40, 13, -0.5F, -0.5F, -1.1F, 1, 2, 1, -0.1F, true));

				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(0.0F, -0.8554F, 2.3636F);
				bone3.addChild(cube_r17);
				setRotationAngle(cube_r17, -0.2618F, 0.0F, 0.0F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 19, 17, -0.5F, -1.2F, -1.2F, 1, 1, 1, 0.0F, true));
				cube_r17.cubeList.add(new ModelBox(cube_r17, 16, 26, -0.5F, -0.2F, -1.2F, 1, 2, 1, 0.1F, true));

				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, 0.0F, 0.0F);
				head.addChild(eyes);


				eye2_r1 = new ModelRenderer(this);
				eye2_r1.setRotationPoint(-1.9099F, 0.5605F, -6.9564F);
				eyes.addChild(eye2_r1);
				setRotationAngle(eye2_r1, 0.2618F, -0.1745F, -0.0436F);
				eye2_r1.cubeList.add(new ModelBox(eye2_r1, 19, 13, -0.4F, -1.0F, -0.5F, 1, 2, 2, -0.4F, false));

				eye1_r1 = new ModelRenderer(this);
				eye1_r1.setRotationPoint(-2.0F, 0.3521F, -5.7954F);
				eyes.addChild(eye1_r1);
				setRotationAngle(eye1_r1, 0.2618F, 0.0F, 0.0F);
				eye1_r1.cubeList.add(new ModelBox(eye1_r1, 0, 13, -0.5F, -1.1F, -0.5F, 1, 2, 2, -0.4F, false));
				eye1_r1.cubeList.add(new ModelBox(eye1_r1, 0, 13, 3.5F, -1.1F, -0.5F, 1, 2, 2, -0.4F, true));

				eye1_r2 = new ModelRenderer(this);
				eye1_r2.setRotationPoint(1.9099F, 0.5605F, -6.9564F);
				eyes.addChild(eye1_r2);
				setRotationAngle(eye1_r2, 0.2618F, 0.1745F, 0.0436F);
				eye1_r2.cubeList.add(new ModelBox(eye1_r2, 19, 13, -0.6F, -1.0F, -0.5F, 1, 2, 2, -0.4F, true));

				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(-0.0099F, 1.0605F, -4.2064F);
				head.addChild(jaw);
				jaw.cubeList.add(new ModelBox(jaw, 15, 38, -2.1901F, 0.0395F, -1.8936F, 1, 2, 3, 0.0F, false));
				jaw.cubeList.add(new ModelBox(jaw, 15, 38, 1.2099F, 0.0395F, -1.8936F, 1, 2, 3, 0.0F, true));
				jaw.cubeList.add(new ModelBox(jaw, 36, 4, -1.4901F, 0.0395F, -4.8936F, 3, 2, 1, 0.0F, false));
				jaw.cubeList.add(new ModelBox(jaw, 19, 13, -1.5F, 1.4F, -4.45F, 3, 1, 6, -0.3F, false));
				jaw.cubeList.add(new ModelBox(jaw, 0, 17, -2.4901F, -0.5605F, -1.0936F, 1, 1, 2, -0.3F, false));
				jaw.cubeList.add(new ModelBox(jaw, 0, 17, 1.5099F, -0.5605F, -1.0936F, 1, 1, 2, -0.3F, true));

				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(-1.6901F, 1.0395F, -3.2936F);
				jaw.addChild(cube_r18);
				setRotationAngle(cube_r18, 0.0F, -0.2182F, 0.0F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 36, 19, -0.2F, -1.0F, -1.5F, 1, 2, 3, 0.0F, false));
		
				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(1.7099F, 1.0395F, -3.2936F);
				jaw.addChild(cube_r19);
				setRotationAngle(cube_r19, 0.0F, 0.2182F, 0.0F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 37, 25, -0.8F, -1.0F, -1.5F, 1, 2, 3, 0.0F, true));

				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -1.6F, -2.3F);
				head.addChild(bone2);
				setRotationAngle(bone2, 0.0873F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 20, 0, -2.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F, false));

				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(-2.0F, 0.0F, -1.0F);
				body.addChild(leg1);
				setRotationAngle(leg1, 0.0F, 0.0F, 0.0873F);


				cube_r20 = new ModelRenderer(this);
				cube_r20.setRotationPoint(-2.0F, 6.6F, 1.0F);
				leg1.addChild(cube_r20);
				setRotationAngle(cube_r20, -0.3491F, 0.0F, 0.0F);
				cube_r20.cubeList.add(new ModelBox(cube_r20, 0, 0, -0.6F, -1.6F, -2.0F, 2, 5, 2, -0.1F, false));

				cube_r21 = new ModelRenderer(this);
				cube_r21.setRotationPoint(-1.3722F, 3.9631F, -1.0951F);
				leg1.addChild(cube_r21);
				setRotationAngle(cube_r21, 0.3054F, 0.0F, 0.1309F);
				cube_r21.cubeList.add(new ModelBox(cube_r21, 0, 34, -1.0F, -2.0F, -0.9F, 2, 4, 3, 0.0F, false));

				cube_r22 = new ModelRenderer(this);
				cube_r22.setRotationPoint(-0.3946F, -0.5729F, 0.0539F);
				leg1.addChild(cube_r22);
				setRotationAngle(cube_r22, -0.3927F, 0.0F, 0.1745F);
				cube_r22.cubeList.add(new ModelBox(cube_r22, 16, 28, -1.2F, -2.3F, -1.5F, 3, 6, 3, 0.1F, false));

				Foot = new ModelRenderer(this);
				Foot.setRotationPoint(-2.6173F, 10.3869F, -1.7375F);
				leg1.addChild(Foot);


				hoof_r1 = new ModelRenderer(this);
				hoof_r1.setRotationPoint(1.6173F, 0.2131F, -0.2625F);
				Foot.addChild(hoof_r1);
				setRotationAngle(hoof_r1, -1.5708F, 0.0F, -0.0873F);
				hoof_r1.cubeList.add(new ModelBox(hoof_r1, 0, 47, -1.6F, -2.2F, -0.3F, 2, 3, 1, -0.05F, true));

				hoof_r2 = new ModelRenderer(this);
				hoof_r2.setRotationPoint(0.6173F, -0.7869F, 0.7375F);
				Foot.addChild(hoof_r2);
				setRotationAngle(hoof_r2, 0.0436F, 0.0F, 0.0122F);
				hoof_r2.cubeList.add(new ModelBox(hoof_r2, 0, 52, -0.6F, -0.9F, -0.9F, 2, 2, 2, -0.1F, true));

				hoof_r3 = new ModelRenderer(this);
				hoof_r3.setRotationPoint(1.6173F, 0.2131F, -0.2625F);
				Foot.addChild(hoof_r3);
				setRotationAngle(hoof_r3, -1.0908F, 0.0F, -0.0873F);
				hoof_r3.cubeList.add(new ModelBox(hoof_r3, 8, 47, -1.6F, -1.8F, -0.7F, 2, 3, 1, -0.1F, true));

				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(2.0F, 0.0F, -1.0F);
				body.addChild(leg2);
				setRotationAngle(leg2, 0.0F, 0.0F, -0.0873F);


				cube_r23 = new ModelRenderer(this);
				cube_r23.setRotationPoint(2.0F, 6.6F, 1.0F);
				leg2.addChild(cube_r23);
				setRotationAngle(cube_r23, -0.3491F, 0.0F, -0.0349F);
				cube_r23.cubeList.add(new ModelBox(cube_r23, 0, 0, -1.35F, -1.5361F, -2.3407F, 2, 5, 2, -0.1F, true));

				cube_r24 = new ModelRenderer(this);
				cube_r24.setRotationPoint(1.3722F, 3.9631F, -1.0951F);
				leg2.addChild(cube_r24);
				setRotationAngle(cube_r24, 0.3054F, 0.0F, -0.1309F);
				cube_r24.cubeList.add(new ModelBox(cube_r24, 0, 34, -1.0F, -2.0F, -0.9F, 2, 4, 3, 0.0F, true));

				cube_r25 = new ModelRenderer(this);
				cube_r25.setRotationPoint(0.3946F, -0.5729F, 0.0539F);
				leg2.addChild(cube_r25);
				setRotationAngle(cube_r25, -0.3927F, 0.0F, -0.1745F);
				cube_r25.cubeList.add(new ModelBox(cube_r25, 16, 28, -1.8F, -2.3F, -1.5F, 3, 6, 3, 0.1F, true));

				Foot2 = new ModelRenderer(this);
				Foot2.setRotationPoint(2.6173F, 10.3869F, -1.7375F);
				leg2.addChild(Foot2);


				hoof_r4 = new ModelRenderer(this);
				hoof_r4.setRotationPoint(-0.6173F, 0.2131F, -0.2625F);
				Foot2.addChild(hoof_r4);
				setRotationAngle(hoof_r4, -1.5708F, 0.0F, 0.0873F);
				hoof_r4.cubeList.add(new ModelBox(hoof_r4, 0, 47, -1.32F, -1.9F, -0.3F, 2, 3, 1, -0.05F, true));

				hoof_r5 = new ModelRenderer(this);
				hoof_r5.setRotationPoint(-0.6173F, 0.2131F, -0.2625F);
				Foot2.addChild(hoof_r5);
				setRotationAngle(hoof_r5, -1.0908F, 0.0F, 0.0873F);
				hoof_r5.cubeList.add(new ModelBox(hoof_r5, 8, 47, -1.32F, -1.6F, -0.8F, 2, 3, 1, -0.1F, true));

				hoof_r6 = new ModelRenderer(this);
				hoof_r6.setRotationPoint(-0.6173F, -0.7869F, 0.7375F);
				Foot2.addChild(hoof_r6);
				setRotationAngle(hoof_r6, 0.0436F, 0.0F, 0.0314F);
				hoof_r6.cubeList.add(new ModelBox(hoof_r6, 0, 52, -1.3F, -0.9F, -1.2F, 2, 2, 2, -0.1F, true));

				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(-2.5F, 2.0F, 9.0F);
				body.addChild(leg3);
				setRotationAngle(leg3, 0.0F, 0.0F, 0.0873F);


				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg3.addChild(bone6);
				setRotationAngle(bone6, -0.2618F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 28, 28, -1.5989F, -2.3433F, -0.6F, 3, 5, 3, 0.3F, false));

				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(-0.0989F, 3.1067F, -0.35F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, 0.4363F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 31, 13, -1.5F, -0.55F, -0.2F, 3, 3, 3, -0.1F, false));

				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, 2.3F, 2.45F);
				bone7.addChild(bone8);
				setRotationAngle(bone8, -0.4363F, 0.0F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 10, 35, -1.0F, -0.25F, -1.9F, 2, 3, 2, -0.1F, false));

				Foot3 = new ModelRenderer(this);
				Foot3.setRotationPoint(0.0827F, 2.5369F, -1.7875F);
				bone8.addChild(Foot3);
				setRotationAngle(Foot3, 0.2618F, 0.0F, 0.0F);


				hoof_r7 = new ModelRenderer(this);
				hoof_r7.setRotationPoint(0.7173F, 1.9631F, -0.2625F);
				Foot3.addChild(hoof_r7);
				setRotationAngle(hoof_r7, -1.0908F, 0.0F, -0.0873F);
				hoof_r7.cubeList.add(new ModelBox(hoof_r7, 8, 47, -1.78F, -2.15F, -0.8F, 2, 3, 1, -0.1F, true));

				hoof_r8 = new ModelRenderer(this);
				hoof_r8.setRotationPoint(-0.2827F, 0.9631F, 0.7375F);
				Foot3.addChild(hoof_r8);
				setRotationAngle(hoof_r8, 0.0436F, 0.0F, -0.0087F);
				hoof_r8.cubeList.add(new ModelBox(hoof_r8, 0, 52, -0.8F, -1.1F, -0.8F, 2, 2, 2, -0.1F, true));

				hoof_r9 = new ModelRenderer(this);
				hoof_r9.setRotationPoint(0.7173F, 1.9631F, -0.2625F);
				Foot3.addChild(hoof_r9);
				setRotationAngle(hoof_r9, -1.5708F, 0.0F, -0.0873F);
				hoof_r9.cubeList.add(new ModelBox(hoof_r9, 0, 47, -1.78F, -2.4F, -0.55F, 2, 3, 1, -0.05F, true));

				leg4 = new ModelRenderer(this);
				leg4.setRotationPoint(2.5F, 2.0F, 9.0F);
				body.addChild(leg4);
				setRotationAngle(leg4, 0.0F, 0.0F, -0.0873F);


				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg4.addChild(bone9);
				setRotationAngle(bone9, -0.2618F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 28, 28, -1.4011F, -2.3433F, -0.6F, 3, 5, 3, 0.3F, true));

				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0989F, 3.1067F, -0.35F);
				bone9.addChild(bone10);
				setRotationAngle(bone10, 0.4363F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 31, 13, -1.5F, -0.55F, -0.2F, 3, 3, 3, -0.1F, true));

				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, 2.3F, 2.45F);
				bone10.addChild(bone11);
				setRotationAngle(bone11, -0.4363F, 0.0F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 10, 35, -1.0F, -0.25F, -1.9F, 2, 3, 2, -0.1F, true));

				Foot4 = new ModelRenderer(this);
				Foot4.setRotationPoint(-0.0827F, 2.5369F, -1.7875F);
				bone11.addChild(Foot4);
				setRotationAngle(Foot4, 0.2618F, 0.0F, 0.0F);


				hoof_r10 = new ModelRenderer(this);
				hoof_r10.setRotationPoint(-0.7173F, 1.9631F, -0.2625F);
				Foot4.addChild(hoof_r10);
				setRotationAngle(hoof_r10, -1.0908F, 0.0F, 0.0873F);
				hoof_r10.cubeList.add(new ModelBox(hoof_r10, 8, 47, -0.22F, -2.15F, -0.8F, 2, 3, 1, -0.1F, false));

				hoof_r11 = new ModelRenderer(this);
				hoof_r11.setRotationPoint(0.2827F, 0.9631F, 0.7375F);
				Foot4.addChild(hoof_r11);
				setRotationAngle(hoof_r11, 0.0436F, 0.0F, 0.0087F);
				hoof_r11.cubeList.add(new ModelBox(hoof_r11, 0, 52, -1.2F, -1.1F, -0.8F, 2, 2, 2, -0.1F, false));

				hoof_r12 = new ModelRenderer(this);
				hoof_r12.setRotationPoint(-0.7173F, 1.9631F, -0.2625F);
				Foot4.addChild(hoof_r12);
				setRotationAngle(hoof_r12, -1.5708F, 0.0F, 0.0873F);
				hoof_r12.cubeList.add(new ModelBox(hoof_r12, 0, 47, -0.22F, -2.4F, -0.55F, 2, 3, 1, -0.05F, false));

				tail[0][0] = new ModelRenderer(this);
				tail[0][0].setRotationPoint(0.0F, 0.5F, 10.5F);
				body.addChild(tail[0][0]);
				setRotationAngle(tail[0][0], -0.7854F, 0.0F, 0.0F);
				tail[0][0].cubeList.add(new ModelBox(tail[0][0], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[0][1] = new ModelRenderer(this);
				tail[0][1].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[0][0].addChild(tail[0][1]);
				setRotationAngle(tail[0][1], -0.2618F, 0.0F, 0.0F);
				tail[0][1].cubeList.add(new ModelBox(tail[0][1], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[0][2] = new ModelRenderer(this);
				tail[0][2].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[0][1].addChild(tail[0][2]);
				setRotationAngle(tail[0][2], -0.2618F, 0.0F, 0.0F);
				tail[0][2].cubeList.add(new ModelBox(tail[0][2], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[0][3] = new ModelRenderer(this);
				tail[0][3].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[0][2].addChild(tail[0][3]);
				setRotationAngle(tail[0][3], -0.2618F, 0.0F, 0.0F);
				tail[0][3].cubeList.add(new ModelBox(tail[0][3], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[0][4] = new ModelRenderer(this);
				tail[0][4].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[0][3].addChild(tail[0][4]);
				setRotationAngle(tail[0][4], 0.2618F, 0.0F, 0.0F);
				tail[0][4].cubeList.add(new ModelBox(tail[0][4], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

				tail[0][5] = new ModelRenderer(this);
				tail[0][5].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[0][4].addChild(tail[0][5]);
				setRotationAngle(tail[0][5], 0.2618F, 0.0F, 0.0F);
				tail[0][5].cubeList.add(new ModelBox(tail[0][5], 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

				tail[0][6] = new ModelRenderer(this);
				tail[0][6].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[0][5].addChild(tail[0][6]);
				setRotationAngle(tail[0][6], 0.2618F, 0.0F, 0.0F);
				tail[0][6].cubeList.add(new ModelBox(tail[0][6], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

				tail[0][7] = new ModelRenderer(this);
				tail[0][7].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[0][6].addChild(tail[0][7]);
				setRotationAngle(tail[0][7], 0.2618F, 0.0F, 0.0F);
				tail[0][7].cubeList.add(new ModelBox(tail[0][7], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

				tail[1][0] = new ModelRenderer(this);
				tail[1][0].setRotationPoint(-1.0F, 0.5F, 10.5F);
				body.addChild(tail[1][0]);
				setRotationAngle(tail[1][0], -1.0472F, -0.2618F, 0.0F);
				tail[1][0].cubeList.add(new ModelBox(tail[1][0], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[1][1] = new ModelRenderer(this);
				tail[1][1].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[1][0].addChild(tail[1][1]);
				setRotationAngle(tail[1][1], -0.2618F, 0.0F, 0.0F);
				tail[1][1].cubeList.add(new ModelBox(tail[1][1], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[1][2] = new ModelRenderer(this);
				tail[1][2].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[1][1].addChild(tail[1][2]);
				setRotationAngle(tail[1][2], -0.2618F, 0.0F, 0.0F);
				tail[1][2].cubeList.add(new ModelBox(tail[1][2], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[1][3] = new ModelRenderer(this);
				tail[1][3].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[1][2].addChild(tail[1][3]);
				setRotationAngle(tail[1][3], -0.2618F, 0.0F, 0.0F);
				tail[1][3].cubeList.add(new ModelBox(tail[1][3], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[1][4] = new ModelRenderer(this);
				tail[1][4].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[1][3].addChild(tail[1][4]);
				setRotationAngle(tail[1][4], 0.2618F, 0.0F, 0.0F);
				tail[1][4].cubeList.add(new ModelBox(tail[1][4], 24, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

				tail[1][5] = new ModelRenderer(this);
				tail[1][5].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[1][4].addChild(tail[1][5]);
				setRotationAngle(tail[1][5], 0.2618F, 0.0F, 0.0F);
				tail[1][5].cubeList.add(new ModelBox(tail[1][5], 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

				tail[1][6] = new ModelRenderer(this);
				tail[1][6].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[1][5].addChild(tail[1][6]);
				setRotationAngle(tail[1][6], 0.2618F, 0.0F, 0.0F);
				tail[1][6].cubeList.add(new ModelBox(tail[1][6], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

				tail[1][7] = new ModelRenderer(this);
				tail[1][7].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[1][6].addChild(tail[1][7]);
				setRotationAngle(tail[1][7], 0.2618F, 0.0F, 0.0F);
				tail[1][7].cubeList.add(new ModelBox(tail[1][7], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

				tail[2][0] = new ModelRenderer(this);
				tail[2][0].setRotationPoint(1.0F, 0.5F, 10.5F);
				body.addChild(tail[2][0]);
				setRotationAngle(tail[2][0], -1.5708F, 0.2618F, 0.0F);
				tail[2][0].cubeList.add(new ModelBox(tail[2][0], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[2][1] = new ModelRenderer(this);
				tail[2][1].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[2][0].addChild(tail[2][1]);
				setRotationAngle(tail[2][1], -0.2618F, 0.0F, 0.0F);
				tail[2][1].cubeList.add(new ModelBox(tail[2][1], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[2][2] = new ModelRenderer(this);
				tail[2][2].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[2][1].addChild(tail[2][2]);
				setRotationAngle(tail[2][2], -0.2618F, 0.0F, 0.0F);
				tail[2][2].cubeList.add(new ModelBox(tail[2][2], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[2][3] = new ModelRenderer(this);
				tail[2][3].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[2][2].addChild(tail[2][3]);
				setRotationAngle(tail[2][3], -0.2618F, 0.0F, 0.0F);
				tail[2][3].cubeList.add(new ModelBox(tail[2][3], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[2][4] = new ModelRenderer(this);
				tail[2][4].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[2][3].addChild(tail[2][4]);
				setRotationAngle(tail[2][4], 0.2618F, 0.0F, 0.0F);
				tail[2][4].cubeList.add(new ModelBox(tail[2][4], 24, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

				tail[2][5] = new ModelRenderer(this);
				tail[2][5].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[2][4].addChild(tail[2][5]);
				setRotationAngle(tail[2][5], 0.2618F, 0.0F, 0.0F);
				tail[2][5].cubeList.add(new ModelBox(tail[2][5], 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

				tail[2][6] = new ModelRenderer(this);
				tail[2][6].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[2][5].addChild(tail[2][6]);
				setRotationAngle(tail[2][6], 0.2618F, 0.0F, 0.0F);
				tail[2][6].cubeList.add(new ModelBox(tail[2][6], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

				tail[2][7] = new ModelRenderer(this);
				tail[2][7].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[2][6].addChild(tail[2][7]);
				setRotationAngle(tail[2][7], 0.2618F, 0.0F, 0.0F);
				tail[2][7].cubeList.add(new ModelBox(tail[2][7], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

				tail[3][0] = new ModelRenderer(this);
				tail[3][0].setRotationPoint(-2.0F, 0.5F, 10.5F);
				body.addChild(tail[3][0]);
				setRotationAngle(tail[3][0], -1.3963F, -0.5236F, 0.0F);
				tail[3][0].cubeList.add(new ModelBox(tail[3][0], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[3][1] = new ModelRenderer(this);
				tail[3][1].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[3][0].addChild(tail[3][1]);
				setRotationAngle(tail[3][1], -0.2618F, 0.0F, 0.0F);
				tail[3][1].cubeList.add(new ModelBox(tail[3][1], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[3][2] = new ModelRenderer(this);
				tail[3][2].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[3][1].addChild(tail[3][2]);
				setRotationAngle(tail[3][2], -0.2618F, 0.0F, 0.0F);
				tail[3][2].cubeList.add(new ModelBox(tail[3][2], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[3][3] = new ModelRenderer(this);
				tail[3][3].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[3][2].addChild(tail[3][3]);
				setRotationAngle(tail[3][3], -0.2618F, 0.0F, 0.0F);
				tail[3][3].cubeList.add(new ModelBox(tail[3][3], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[3][4] = new ModelRenderer(this);
				tail[3][4].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[3][3].addChild(tail[3][4]);
				setRotationAngle(tail[3][4], 0.2618F, 0.0F, 0.0F);
				tail[3][4].cubeList.add(new ModelBox(tail[3][4], 24, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

				tail[3][5] = new ModelRenderer(this);
				tail[3][5].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[3][4].addChild(tail[3][5]);
				setRotationAngle(tail[3][5], 0.2618F, 0.0F, 0.0F);
				tail[3][5].cubeList.add(new ModelBox(tail[3][5], 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

				tail[3][6] = new ModelRenderer(this);
				tail[3][6].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[3][5].addChild(tail[3][6]);
				setRotationAngle(tail[3][6], 0.2618F, 0.0F, 0.0F);
				tail[3][6].cubeList.add(new ModelBox(tail[3][6], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

				tail[3][7] = new ModelRenderer(this);
				tail[3][7].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[3][6].addChild(tail[3][7]);
				setRotationAngle(tail[3][7], 0.2618F, 0.0F, 0.0F);
				tail[3][7].cubeList.add(new ModelBox(tail[3][7], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

				tail[4][0] = new ModelRenderer(this);
				tail[4][0].setRotationPoint(2.0F, 0.5F, 10.5F);
				body.addChild(tail[4][0]);
				setRotationAngle(tail[4][0], -1.2217F, 0.5236F, 0.0F);
				tail[4][0].cubeList.add(new ModelBox(tail[4][0], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[4][1] = new ModelRenderer(this);
				tail[4][1].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[4][0].addChild(tail[4][1]);
				setRotationAngle(tail[4][1], -0.2618F, 0.0F, 0.0F);
				tail[4][1].cubeList.add(new ModelBox(tail[4][1], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[4][2] = new ModelRenderer(this);
				tail[4][2].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[4][1].addChild(tail[4][2]);
				setRotationAngle(tail[4][2], -0.2618F, 0.0F, 0.0F);
				tail[4][2].cubeList.add(new ModelBox(tail[4][2], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[4][3] = new ModelRenderer(this);
				tail[4][3].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[4][2].addChild(tail[4][3]);
				setRotationAngle(tail[4][3], -0.2618F, 0.0F, 0.0F);
				tail[4][3].cubeList.add(new ModelBox(tail[4][3], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

				tail[4][4] = new ModelRenderer(this);
				tail[4][4].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[4][3].addChild(tail[4][4]);
				setRotationAngle(tail[4][4], 0.2618F, 0.0F, 0.0F);
				tail[4][4].cubeList.add(new ModelBox(tail[4][4], 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

				tail[4][5] = new ModelRenderer(this);
				tail[4][5].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[4][4].addChild(tail[4][5]);
				setRotationAngle(tail[4][5], 0.2618F, 0.0F, 0.0F);
				tail[4][5].cubeList.add(new ModelBox(tail[4][5], 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

				tail[4][6] = new ModelRenderer(this);
				tail[4][6].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[4][5].addChild(tail[4][6]);
				setRotationAngle(tail[4][6], 0.2618F, 0.0F, 0.0F);
				tail[4][6].cubeList.add(new ModelBox(tail[4][6], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

				tail[4][7] = new ModelRenderer(this);
				tail[4][7].setRotationPoint(0.0F, -2.0F, 0.0F);
				tail[4][6].addChild(tail[4][7]);
				setRotationAngle(tail[4][7], 0.2618F, 0.0F, 0.0F);
				tail[4][7].cubeList.add(new ModelBox(tail[4][7], 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

				for (int i = 0; i < 5; i++) {
					for (int j = 1; j < 8; j++) {
						tailSwayX[i][j] = (rand.nextFloat() * 0.2618F + 0.2618F) * (rand.nextBoolean() ? -1F : 1F);
						tailSwayZ[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
						tailSwayY[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F);
					}
				}
			}

			@Override
			public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F);
				//GlStateManager.translate(0.0F, 0.0F, 0.375F * MODELSCALE);
				GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
				body.render(f5);
				//super.render(entity, f0, f1, f2, f3, f4, f5);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				eyesHighlight.render(f5);
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
				super.setRotationAngles(f0 * 2.0F / e.height, f1, f2, f3, f4, f5, e);
				body.rotateAngleX = 0.0F;
				for (int i = 0; i < 5; i++) {
					for (int j = 1; j < 8; j++) {
						tail[i][j].rotateAngleX = MathHelper.sin((f2 - j) * 0.2F) * tailSwayX[i][j];
						tail[i][j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.2F) * tailSwayZ[i][j];
						//tail[i][j].rotateAngleY = MathHelper.sin((f2 - j) * 0.1F) * tailSwayY[i][j];
					}
				}
				if (((EntityCustom) e).isShooting()) {
					head.rotateAngleX += -0.1745F;
					jaw.rotateAngleX = 0.7854F;
				} else {
					jaw.rotateAngleX = 0.0F;
				}
				if (((EntityCustom) e).isFaceDown()) {
					body.rotationPointY = 19.0F;
					leg1.rotateAngleX = -0.9599F;
					leg2.rotateAngleX = -0.9599F;
					leg3.rotateAngleX = 1.1345F;
					leg4.rotateAngleX = 1.1345F;
				} else {
					body.rotationPointY = 13.0F;
				}
				this.copyModelAngles(body, eyesHighlight);
				this.copyModelAngles(head, headsync);
			}
		}
	}
}
