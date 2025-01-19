
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

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
			this.setSize(MODELSCALE * 0.5F, MODELSCALE * 0.875F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.5F, MODELSCALE * 0.875F);
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
		protected void updateAITasks() {
			super.updateAITasks();
			EntityLivingBase target = this.getAttackTarget();
			if (target != null && this.getMeleeTime() < 2 && this.getDistance(target) < this.getBijudamaMinRange()) {
				this.setMeleeTime(80);
			}
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			ProcedureUtils.addVelocity(this, entityIn.getPositionVector().subtract(this.getPositionVector()).normalize().scale(3d));
			boolean ret = super.attackEntityAsMob(entityIn);
			for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, entityIn.getEntityBoundingBox().grow(6d))) {
				ProcedureUtils.pushEntity(this, entity, 30d, 1.8f);
			}
			return ret;
		}

		@Override
		public void onUpdate() {
			if (this.ticksExisted % 10 == 1) {
				this.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 12, 3, false, false));
			}
			super.onUpdate();
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
			private final ModelRenderer eye5_r1;
			private final ModelRenderer eye4_r1;
			private final ModelRenderer eye3_r1;
			//private final ModelRenderer body;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			//private final ModelRenderer head;
			private final ModelRenderer bone2;
			private final ModelRenderer bone7;
			private final ModelRenderer bone6;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer cube_r6;
			private final ModelRenderer cube_r7;
			private final ModelRenderer jaw;
			private final ModelRenderer cube_r8;
			private final ModelRenderer cube_r9;
			private final ModelRenderer eyes;
			private final ModelRenderer eye4_r2;
			private final ModelRenderer eye3_r2;
			private final ModelRenderer eye1_r1;
			private final ModelRenderer bone;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			private final ModelRenderer bone3;
			private final ModelRenderer cube_r13;
			private final ModelRenderer cube_r14;
			private final ModelRenderer cube_r15;
			private final ModelRenderer bone4;
			private final ModelRenderer cube_r16;
			private final ModelRenderer cube_r17;
			private final ModelRenderer cube_r18;
			private final ModelRenderer cube_r19;
			private final ModelRenderer bone5;
			private final ModelRenderer cube_r20;
			private final ModelRenderer cube_r21;
			private final ModelRenderer cube_r22;
			private final ModelRenderer cube_r23;
			//private final ModelRenderer leg1;
			private final ModelRenderer leg1_1;
			private final ModelRenderer leg1_2;
			private final ModelRenderer leg1_3;
			private final ModelRenderer foot1;
			private final ModelRenderer hoof_r1;
			private final ModelRenderer hoof_r2;
			private final ModelRenderer hoof_r3;
			//private final ModelRenderer leg2;
			private final ModelRenderer leg2_1;
			private final ModelRenderer leg2_2;
			private final ModelRenderer leg2_3;
			private final ModelRenderer foot2;
			private final ModelRenderer hoof_r4;
			private final ModelRenderer hoof_r5;
			private final ModelRenderer hoof_r6;
			//private final ModelRenderer leg3;
			private final ModelRenderer leg3_1;
			private final ModelRenderer leg3_2;
			private final ModelRenderer leg3_3;
			private final ModelRenderer foot3;
			private final ModelRenderer hoof_r7;
			private final ModelRenderer hoof_r8;
			private final ModelRenderer hoof_r9;
			//private final ModelRenderer leg4;
			private final ModelRenderer leg4_1;
			private final ModelRenderer leg4_2;
			private final ModelRenderer leg4_3;
			private final ModelRenderer foot4;
			private final ModelRenderer hoof_r10;
			private final ModelRenderer hoof_r11;
			private final ModelRenderer hoof_r12;
			private final ModelRenderer[][] tail = new ModelRenderer[5][8];
			private final float tailSwayX[][] = new float[5][8];
			private final float tailSwayY[][] = new float[5][8];
			private final float tailSwayZ[][] = new float[5][8];
			private final Random rand = new Random();

			private final float[][] swingingBodyPreset = { { 0.0F, 12.75F, 0.0F, 0.0F, 0.0F, 0.0F }, { 0.0F, 15.75F, 0.0F, 0.2618F, 0.0F, 0.0F }, { 0.0F, 13.75F, 0.0F, 0.0F, 0.0F, 0.0F } };
			private final float[][] swingingHeadPreset = { { 0.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, -3.0F, 0.7854F, 0.0F, 0.0F }, { 0.0F, 0.0F, -3.0F, -0.6981F, 0.0F, 0.0F } };
			private final float[][] swingingLeg1_1Preset = { { -0.3946F, -0.5729F, 0.0539F, -0.3927F, 0.0F, 0.2618F }, { -0.3946F, -0.5729F, 0.0539F, 0.1309F, 0.0F, 0.4363F }, { -0.3946F, -0.5729F, 0.0539F, -0.3927F, 0.0F, 0.2618F } };
			private final float[][] swingingLeg1_2Preset = { { -0.2777F, 2.536F, 0.101F, 0.8727F, 0.0F, -0.0436F }, { -0.2777F, 2.536F, 0.101F, 1.0472F, 0.0F, -0.1745F }, { -0.2777F, 2.536F, 0.101F, 0.8727F, 0.0F, -0.0436F } };
			private final float[][] swingingLeg1_3Preset = { { 0.0354F, 3.913F, 1.1241F, -0.9599F, -0.0436F, -0.1745F }, { 0.0354F, 3.913F, 1.1241F, -1.6581F, -0.0436F, -0.1745F }, { 0.0354F, 3.913F, 1.1241F, -0.9599F, -0.0436F, -0.1745F } };
			private final float[][] swingingFoot1Preset = { { -0.0629F, 4.1779F, -1.0035F, 0.48F, 0.0F, 0.0436F }, { -0.0629F, 4.1779F, -1.0035F, 0.2182F, 0.0F, -0.0436F }, { -0.0629F, 4.1779F, -1.0035F, 0.48F, 0.0F, 0.0436F } };
			private final float[][] swingingLeg2_1Preset = { { 0.3946F, -0.5729F, 0.0539F, -0.3927F, 0.0F, -0.2618F }, { 0.3946F, -0.5729F, 0.0539F, 0.1309F, 0.0F, -0.4363F }, { 0.3946F, -0.5729F, 0.0539F, -0.3927F, 0.0F, -0.2618F } };
			private final float[][] swingingLeg2_2Preset = { { 0.2777F, 2.536F, 0.101F, 0.8727F, 0.0F, 0.0436F }, { 0.2777F, 2.536F, 0.101F, 1.0472F, 0.0F, 0.1745F }, { 0.2777F, 2.536F, 0.101F, 0.8727F, 0.0F, 0.0436F } };
			private final float[][] swingingLeg2_3Preset = { { -0.0354F, 3.913F, 1.1241F, -0.9599F, 0.0436F, 0.1745F }, { -0.0354F, 3.913F, 1.1241F, -1.6581F, 0.0436F, 0.1745F }, { -0.0354F, 3.913F, 1.1241F, -0.9599F, 0.0436F, 0.1745F } };
			private final float[][] swingingFoot2Preset = { { 0.0629F, 4.1779F, -1.0035F, 0.48F, 0.0F, -0.0436F }, { 0.0629F, 4.1779F, -1.0035F, 0.2182F, 0.0F, 0.0436F }, { 0.0629F, 4.1779F, -1.0035F, 0.48F, 0.0F, -0.0436F } };

			public ModelFiveTails() {
				super(12, 0.0F);

				textureWidth = 64;
				textureHeight = 64;

				eyesHighlight = new ModelRenderer(this);
				eyesHighlight.setRotationPoint(0.0F, 12.75F, 0.0F);
				
		
				headsync = new ModelRenderer(this);
				headsync.setRotationPoint(0.0F, 0.0F, -3.0F);
				eyesHighlight.addChild(headsync);
				
		
				eye5_r1 = new ModelRenderer(this);
				eye5_r1.setRotationPoint(2.0F, -2.2479F, -7.7954F);
				headsync.addChild(eye5_r1);
				setRotationAngle(eye5_r1, 0.2618F, 0.0F, 0.0F);
				eye5_r1.cubeList.add(new ModelBox(eye5_r1, 48, 9, -0.47F, -1.1F, -0.5F, 1, 2, 2, -0.4F, true));
				eye5_r1.cubeList.add(new ModelBox(eye5_r1, 48, 9, -4.53F, -1.1F, -0.5F, 1, 2, 2, -0.4F, false));
		
				eye4_r1 = new ModelRenderer(this);
				eye4_r1.setRotationPoint(2.0099F, -2.0395F, -8.9564F);
				headsync.addChild(eye4_r1);
				setRotationAngle(eye4_r1, 0.2618F, 0.2129F, 0.0436F);
				eye4_r1.cubeList.add(new ModelBox(eye4_r1, 56, 9, -0.7099F, -1.0F, -0.5F, 1, 2, 2, -0.4F, true));
		
				eye3_r1 = new ModelRenderer(this);
				eye3_r1.setRotationPoint(-2.0099F, -2.0395F, -8.9564F);
				headsync.addChild(eye3_r1);
				setRotationAngle(eye3_r1, 0.2618F, -0.2129F, -0.0436F);
				eye3_r1.cubeList.add(new ModelBox(eye3_r1, 56, 9, -0.2901F, -1.0F, -0.5F, 1, 2, 2, -0.4F, false));
		
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 12.75F, 0.0F);
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
				head.setRotationPoint(0.0F, 0.0F, -3.0F);
				body.addChild(head);
				
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -0.5F, 0.0F);
				head.addChild(bone2);
				setRotationAngle(bone2, -0.5236F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 20, 0, -2.0F, -0.9995F, -5.4071F, 4, 4, 4, 0.1F, false));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 3.0005F, -1.2571F);
				bone2.addChild(bone7);
				bone7.cubeList.add(new ModelBox(bone7, 20, 0, -2.0F, -4.0F, 0.0F, 4, 4, 4, 0.2F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, -1.0995F, -5.2571F);
				bone2.addChild(bone6);
				setRotationAngle(bone6, 0.5236F, 0.0F, 0.0F);
				
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(0.0F, 1.6272F, -5.958F);
				bone6.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.6109F, 0.1745F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 33, 0, -1.0F, -0.3772F, -0.422F, 3, 2, 2, -0.3F, false));
		
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(0.0F, 1.6272F, -5.958F);
				bone6.addChild(cube_r4);
				setRotationAngle(cube_r4, -0.6109F, -0.1745F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 33, 0, -2.0F, -0.3772F, -0.422F, 3, 2, 2, -0.3F, true));
		
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.9F, 1.6F, -5.6F);
				bone6.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.3054F, 0.0873F, 0.0F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 29, 8, -2.0F, -0.4F, -0.7F, 3, 2, 3, -0.1F, true));
		
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(-0.9F, 1.6F, -5.6F);
				bone6.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.3054F, -0.0873F, 0.0F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 29, 8, -1.0F, -0.4F, -0.7F, 3, 2, 3, -0.1F, false));
		
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(0.0F, 1.8861F, 2.5902F);
				bone6.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.2182F, 0.0F, 0.0F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 26, -2.0F, -2.5658F, -6.2052F, 4, 4, 4, 0.0F, false));
		
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, 2.5605F, -2.2064F);
				bone6.addChild(jaw);
				jaw.cubeList.add(new ModelBox(jaw, 15, 38, -2.2F, 0.0395F, -1.8936F, 1, 2, 3, 0.0F, false));
				jaw.cubeList.add(new ModelBox(jaw, 15, 38, 1.2F, 0.0395F, -1.8936F, 1, 2, 3, 0.0F, true));
				jaw.cubeList.add(new ModelBox(jaw, 36, 4, -1.5F, 0.0395F, -4.8936F, 3, 2, 1, 0.0F, false));
				jaw.cubeList.add(new ModelBox(jaw, 19, 13, -1.5F, 1.3395F, -4.45F, 3, 1, 6, -0.3F, false));
				jaw.cubeList.add(new ModelBox(jaw, 0, 17, -2.5F, -0.5605F, -1.0936F, 1, 1, 2, -0.3F, false));
				jaw.cubeList.add(new ModelBox(jaw, 0, 17, 1.5F, -0.5605F, -1.0936F, 1, 1, 2, -0.3F, true));
		
				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(-1.7F, 1.0395F, -3.2936F);
				jaw.addChild(cube_r8);
				setRotationAngle(cube_r8, 0.0F, -0.2182F, 0.0F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 36, 19, -0.184F, -1.0F, -1.52F, 1, 2, 3, 0.0F, false));
		
				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(1.7F, 1.0395F, -3.2936F);
				jaw.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.0F, 0.2182F, 0.0F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 37, 25, -0.816F, -1.0F, -1.52F, 1, 2, 3, 0.0F, true));
		
				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, 1.5F, 2.0F);
				bone6.addChild(eyes);
				
		
				eye4_r2 = new ModelRenderer(this);
				eye4_r2.setRotationPoint(-1.9099F, 0.5605F, -6.9564F);
				eyes.addChild(eye4_r2);
				setRotationAngle(eye4_r2, 0.2618F, -0.2182F, -0.0585F);
				eye4_r2.cubeList.add(new ModelBox(eye4_r2, 19, 13, -0.3421F, -1.0075F, -0.5F, 1, 2, 2, -0.4F, false));
		
				eye3_r2 = new ModelRenderer(this);
				eye3_r2.setRotationPoint(1.9099F, 0.5605F, -6.9564F);
				eyes.addChild(eye3_r2);
				setRotationAngle(eye3_r2, 0.2618F, 0.2182F, 0.0585F);
				eye3_r2.cubeList.add(new ModelBox(eye3_r2, 19, 13, -0.6579F, -1.0075F, -0.5F, 1, 2, 2, -0.4F, true));
		
				eye1_r1 = new ModelRenderer(this);
				eye1_r1.setRotationPoint(-2.0F, 0.3521F, -5.7954F);
				eyes.addChild(eye1_r1);
				setRotationAngle(eye1_r1, 0.2618F, 0.0F, 0.0F);
				eye1_r1.cubeList.add(new ModelBox(eye1_r1, 0, 13, -0.5F, -1.1F, -0.5F, 1, 2, 2, -0.4F, false));
				eye1_r1.cubeList.add(new ModelBox(eye1_r1, 0, 13, 3.5F, -1.1F, -0.5F, 1, 2, 2, -0.4F, true));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-1.55F, 0.7818F, -2.8788F);
				bone6.addChild(bone);
				setRotationAngle(bone, -0.4162F, 0.1666F, -0.5152F);
				
		
				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(0.0F, -3.5F, 1.1F);
				bone.addChild(cube_r10);
				setRotationAngle(cube_r10, -0.4363F, 0.0F, 0.0F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 20, 37, -0.5F, -1.0F, 0.0F, 1, 2, 1, -0.2F, false));
		
				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(0.0F, -2.767F, 0.8706F);
				bone.addChild(cube_r11);
				setRotationAngle(cube_r11, -0.3491F, 0.0F, 0.0F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 40, 13, -0.5F, -0.5F, -0.1F, 1, 2, 1, -0.1F, false));
		
				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(0.0F, -0.6818F, 0.3788F);
				bone.addChild(cube_r12);
				setRotationAngle(cube_r12, -0.2618F, 0.0F, 0.0F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, false));
				cube_r12.cubeList.add(new ModelBox(cube_r12, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(1.55F, 0.7818F, -2.8788F);
				bone6.addChild(bone3);
				setRotationAngle(bone3, -0.4162F, -0.1666F, 0.5152F);
				
		
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(0.0F, -3.5F, 1.1F);
				bone3.addChild(cube_r13);
				setRotationAngle(cube_r13, -0.4363F, 0.0F, 0.0F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 20, 37, -0.5F, -1.0F, 0.0F, 1, 2, 1, -0.2F, true));
		
				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(0.0F, -2.767F, 0.8706F);
				bone3.addChild(cube_r14);
				setRotationAngle(cube_r14, -0.3491F, 0.0F, 0.0F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 40, 13, -0.5F, -0.5F, -0.1F, 1, 2, 1, -0.1F, true));
		
				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(0.0F, -0.6818F, 0.3788F);
				bone3.addChild(cube_r15);
				setRotationAngle(cube_r15, -0.2618F, 0.0F, 0.0F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, true));
				cube_r15.cubeList.add(new ModelBox(cube_r15, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, true));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-1.45F, 0.5318F, -0.1288F);
				bone6.addChild(bone4);
				setRotationAngle(bone4, -0.6109F, 0.0F, -0.6981F);
				
		
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(0.0F, -4.5F, 0.9F);
				bone4.addChild(cube_r16);
				setRotationAngle(cube_r16, -0.4363F, 0.0F, 0.0F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 20, 37, -0.5F, -1.5F, 0.0F, 1, 2, 1, -0.2F, false));
		
				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(0.0F, -3.517F, 0.6206F);
				bone4.addChild(cube_r17);
				setRotationAngle(cube_r17, -0.3491F, 0.0F, 0.0F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 40, 13, -0.5F, -1.0F, -0.1F, 1, 2, 1, -0.1F, false));
		
				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(0.0F, -1.6818F, 0.1288F);
				bone4.addChild(cube_r18);
				setRotationAngle(cube_r18, -0.2618F, 0.0F, 0.0F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, false));
				cube_r18.cubeList.add(new ModelBox(cube_r18, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.05F, false));
		
				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(0.0F, -0.6818F, -0.1212F);
				bone4.addChild(cube_r19);
				setRotationAngle(cube_r19, -0.2618F, 0.0F, 0.0F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, false));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(1.45F, 0.5318F, -0.1288F);
				bone6.addChild(bone5);
				setRotationAngle(bone5, -0.6109F, 0.0F, 0.6981F);
				
		
				cube_r20 = new ModelRenderer(this);
				cube_r20.setRotationPoint(0.0F, -4.5F, 0.9F);
				bone5.addChild(cube_r20);
				setRotationAngle(cube_r20, -0.4363F, 0.0F, 0.0F);
				cube_r20.cubeList.add(new ModelBox(cube_r20, 20, 37, -0.5F, -1.5F, 0.0F, 1, 2, 1, -0.2F, true));
		
				cube_r21 = new ModelRenderer(this);
				cube_r21.setRotationPoint(0.0F, -3.517F, 0.6206F);
				bone5.addChild(cube_r21);
				setRotationAngle(cube_r21, -0.3491F, 0.0F, 0.0F);
				cube_r21.cubeList.add(new ModelBox(cube_r21, 40, 13, -0.5F, -1.0F, -0.1F, 1, 2, 1, -0.1F, true));
		
				cube_r22 = new ModelRenderer(this);
				cube_r22.setRotationPoint(0.0F, -1.6818F, 0.1288F);
				bone5.addChild(cube_r22);
				setRotationAngle(cube_r22, -0.2618F, 0.0F, 0.0F);
				cube_r22.cubeList.add(new ModelBox(cube_r22, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, true));
				cube_r22.cubeList.add(new ModelBox(cube_r22, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.05F, true));
		
				cube_r23 = new ModelRenderer(this);
				cube_r23.setRotationPoint(0.0F, -0.6818F, -0.1212F);
				bone5.addChild(cube_r23);
				setRotationAngle(cube_r23, -0.2618F, 0.0F, 0.0F);
				cube_r23.cubeList.add(new ModelBox(cube_r23, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, true));
		
				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(-2.0F, 1.0F, -1.0F);
				body.addChild(leg1);
				
		
				leg1_1 = new ModelRenderer(this);
				leg1_1.setRotationPoint(-0.3946F, -0.5729F, 0.0539F);
				leg1.addChild(leg1_1);
				setRotationAngle(leg1_1, -0.3927F, 0.0F, 0.2618F);
				leg1_1.cubeList.add(new ModelBox(leg1_1, 16, 28, -1.2F, -2.3F, -1.5F, 3, 6, 3, 0.1F, false));
		
				leg1_2 = new ModelRenderer(this);
				leg1_2.setRotationPoint(-0.2777F, 2.536F, 0.101F);
				leg1_1.addChild(leg1_2);
				setRotationAngle(leg1_2, 0.8727F, 0.0F, -0.0436F);
				leg1_2.cubeList.add(new ModelBox(leg1_2, 0, 34, -0.9868F, 0.0261F, -1.621F, 2, 4, 3, 0.0F, false));
		
				leg1_3 = new ModelRenderer(this);
				leg1_3.setRotationPoint(0.0354F, 3.913F, 1.1241F);
				leg1_2.addChild(leg1_3);
				setRotationAngle(leg1_3, -0.9599F, -0.0436F, -0.1745F);
				leg1_3.cubeList.add(new ModelBox(leg1_3, 0, 0, -1.0955F, -0.209F, -1.916F, 2, 5, 2, -0.1F, false));
		
				foot1 = new ModelRenderer(this);
				foot1.setRotationPoint(-0.0629F, 4.1779F, -1.0035F);
				leg1_3.addChild(foot1);
				setRotationAngle(foot1, 0.48F, 0.0F, 0.0436F);
				
		
				hoof_r1 = new ModelRenderer(this);
				hoof_r1.setRotationPoint(0.5947F, 1.8511F, -1.0411F);
				foot1.addChild(hoof_r1);
				setRotationAngle(hoof_r1, -1.5708F, 0.0F, -0.0873F);
				hoof_r1.cubeList.add(new ModelBox(hoof_r1, 0, 47, -1.6F, -2.2F, -0.3F, 2, 3, 1, -0.05F, true));
		
				hoof_r2 = new ModelRenderer(this);
				hoof_r2.setRotationPoint(-0.4053F, 0.8511F, -0.0411F);
				foot1.addChild(hoof_r2);
				setRotationAngle(hoof_r2, 0.0436F, 0.0F, 0.0122F);
				hoof_r2.cubeList.add(new ModelBox(hoof_r2, 0, 52, -0.6F, -0.9F, -0.9F, 2, 2, 2, -0.1F, true));
		
				hoof_r3 = new ModelRenderer(this);
				hoof_r3.setRotationPoint(0.5947F, 1.8511F, -1.0411F);
				foot1.addChild(hoof_r3);
				setRotationAngle(hoof_r3, -1.0908F, 0.0F, -0.0873F);
				hoof_r3.cubeList.add(new ModelBox(hoof_r3, 8, 47, -1.6F, -1.8F, -0.7F, 2, 3, 1, -0.1F, true));
		
				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(2.0F, 1.0F, -1.0F);
				body.addChild(leg2);
				
		
				leg2_1 = new ModelRenderer(this);
				leg2_1.setRotationPoint(0.3946F, -0.5729F, 0.0539F);
				leg2.addChild(leg2_1);
				setRotationAngle(leg2_1, -0.3927F, 0.0F, -0.2618F);
				leg2_1.cubeList.add(new ModelBox(leg2_1, 16, 28, -1.8F, -2.3F, -1.5F, 3, 6, 3, 0.1F, true));
		
				leg2_2 = new ModelRenderer(this);
				leg2_2.setRotationPoint(0.2777F, 2.536F, 0.101F);
				leg2_1.addChild(leg2_2);
				setRotationAngle(leg2_2, 0.8727F, 0.0F, 0.0436F);
				leg2_2.cubeList.add(new ModelBox(leg2_2, 0, 34, -1.0132F, 0.0261F, -1.621F, 2, 4, 3, 0.0F, true));
		
				leg2_3 = new ModelRenderer(this);
				leg2_3.setRotationPoint(-0.0354F, 3.913F, 1.1241F);
				leg2_2.addChild(leg2_3);
				setRotationAngle(leg2_3, -0.9599F, 0.0436F, 0.1745F);
				leg2_3.cubeList.add(new ModelBox(leg2_3, 0, 0, -0.9045F, -0.209F, -1.916F, 2, 5, 2, -0.1F, true));
		
				foot2 = new ModelRenderer(this);
				foot2.setRotationPoint(0.0629F, 4.1779F, -1.0035F);
				leg2_3.addChild(foot2);
				setRotationAngle(foot2, 0.48F, 0.0F, -0.0436F);
				
		
				hoof_r4 = new ModelRenderer(this);
				hoof_r4.setRotationPoint(-0.5947F, 1.8511F, -1.0411F);
				foot2.addChild(hoof_r4);
				setRotationAngle(hoof_r4, -1.5708F, 0.0F, 0.0873F);
				hoof_r4.cubeList.add(new ModelBox(hoof_r4, 0, 47, -0.4F, -2.2F, -0.3F, 2, 3, 1, -0.05F, false));
		
				hoof_r5 = new ModelRenderer(this);
				hoof_r5.setRotationPoint(0.4053F, 0.8511F, -0.0411F);
				foot2.addChild(hoof_r5);
				setRotationAngle(hoof_r5, 0.0436F, 0.0F, -0.0122F);
				hoof_r5.cubeList.add(new ModelBox(hoof_r5, 0, 52, -1.4F, -0.9F, -0.9F, 2, 2, 2, -0.1F, false));
		
				hoof_r6 = new ModelRenderer(this);
				hoof_r6.setRotationPoint(-0.5947F, 1.8511F, -1.0411F);
				foot2.addChild(hoof_r6);
				setRotationAngle(hoof_r6, -1.0908F, 0.0F, 0.0873F);
				hoof_r6.cubeList.add(new ModelBox(hoof_r6, 8, 47, -0.4F, -1.8F, -0.7F, 2, 3, 1, -0.1F, false));
		
				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(-2.5F, 3.0F, 9.0F);
				body.addChild(leg3);
				
		
				leg3_1 = new ModelRenderer(this);
				leg3_1.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg3.addChild(leg3_1);
				setRotationAngle(leg3_1, 0.0F, 0.0F, 0.0873F);
				leg3_1.cubeList.add(new ModelBox(leg3_1, 28, 28, -1.5989F, -2.3433F, -0.6F, 3, 5, 3, 0.3F, false));
		
				leg3_2 = new ModelRenderer(this);
				leg3_2.setRotationPoint(-0.5989F, 2.9567F, -0.75F);
				leg3_1.addChild(leg3_2);
				setRotationAngle(leg3_2, 0.9163F, 0.0F, 0.0F);
				leg3_2.cubeList.add(new ModelBox(leg3_2, 28, 28, -1.5F, -0.1978F, -0.1692F, 3, 5, 3, -0.4F, false));
		
				leg3_3 = new ModelRenderer(this);
				leg3_3.setRotationPoint(0.0F, 4.4022F, 2.3808F);
				leg3_2.addChild(leg3_3);
				setRotationAngle(leg3_3, -1.0908F, 0.0F, 0.0F);
				leg3_3.cubeList.add(new ModelBox(leg3_3, 10, 35, -1.0F, -0.1734F, -1.8357F, 2, 3, 2, -0.1F, false));
		
				foot3 = new ModelRenderer(this);
				foot3.setRotationPoint(0.0827F, 2.6135F, -1.7232F);
				leg3_3.addChild(foot3);
				setRotationAngle(foot3, 0.1745F, 0.0F, 0.0F);
				
		
				hoof_r7 = new ModelRenderer(this);
				hoof_r7.setRotationPoint(0.7173F, 1.9631F, -0.2625F);
				foot3.addChild(hoof_r7);
				setRotationAngle(hoof_r7, -1.0908F, 0.0F, -0.0873F);
				hoof_r7.cubeList.add(new ModelBox(hoof_r7, 8, 47, -1.78F, -2.15F, -0.8F, 2, 3, 1, -0.1F, true));
		
				hoof_r8 = new ModelRenderer(this);
				hoof_r8.setRotationPoint(-0.2827F, 0.9631F, 0.7375F);
				foot3.addChild(hoof_r8);
				setRotationAngle(hoof_r8, 0.0436F, 0.0F, -0.0087F);
				hoof_r8.cubeList.add(new ModelBox(hoof_r8, 0, 52, -0.8F, -1.1F, -0.8F, 2, 2, 2, -0.1F, true));
		
				hoof_r9 = new ModelRenderer(this);
				hoof_r9.setRotationPoint(0.7173F, 1.9631F, -0.2625F);
				foot3.addChild(hoof_r9);
				setRotationAngle(hoof_r9, -1.5708F, 0.0F, -0.0873F);
				hoof_r9.cubeList.add(new ModelBox(hoof_r9, 0, 47, -1.78F, -2.4F, -0.55F, 2, 3, 1, -0.05F, true));
		
				leg4 = new ModelRenderer(this);
				leg4.setRotationPoint(2.5F, 3.0F, 9.0F);
				body.addChild(leg4);
				
		
				leg4_1 = new ModelRenderer(this);
				leg4_1.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg4.addChild(leg4_1);
				setRotationAngle(leg4_1, 0.0F, 0.0F, -0.0873F);
				leg4_1.cubeList.add(new ModelBox(leg4_1, 28, 28, -1.4011F, -2.3433F, -0.6F, 3, 5, 3, 0.3F, true));
		
				leg4_2 = new ModelRenderer(this);
				leg4_2.setRotationPoint(0.5989F, 2.9567F, -0.75F);
				leg4_1.addChild(leg4_2);
				setRotationAngle(leg4_2, 0.9163F, 0.0F, 0.0F);
				leg4_2.cubeList.add(new ModelBox(leg4_2, 28, 28, -1.5F, -0.1978F, -0.1692F, 3, 5, 3, -0.4F, true));
		
				leg4_3 = new ModelRenderer(this);
				leg4_3.setRotationPoint(0.0F, 4.4022F, 2.3808F);
				leg4_2.addChild(leg4_3);
				setRotationAngle(leg4_3, -1.0908F, 0.0F, 0.0F);
				leg4_3.cubeList.add(new ModelBox(leg4_3, 10, 35, -1.0F, -0.1734F, -1.8357F, 2, 3, 2, -0.1F, true));
		
				foot4 = new ModelRenderer(this);
				foot4.setRotationPoint(-0.0827F, 2.6135F, -1.7232F);
				leg4_3.addChild(foot4);
				setRotationAngle(foot4, 0.1745F, 0.0F, 0.0F);
				
		
				hoof_r10 = new ModelRenderer(this);
				hoof_r10.setRotationPoint(-0.7173F, 1.9631F, -0.2625F);
				foot4.addChild(hoof_r10);
				setRotationAngle(hoof_r10, -1.0908F, 0.0F, 0.0873F);
				hoof_r10.cubeList.add(new ModelBox(hoof_r10, 8, 47, -0.22F, -2.15F, -0.8F, 2, 3, 1, -0.1F, false));
		
				hoof_r11 = new ModelRenderer(this);
				hoof_r11.setRotationPoint(0.2827F, 0.9631F, 0.7375F);
				foot4.addChild(hoof_r11);
				setRotationAngle(hoof_r11, 0.0436F, 0.0F, 0.0087F);
				hoof_r11.cubeList.add(new ModelBox(hoof_r11, 0, 52, -1.2F, -1.1F, -0.8F, 2, 2, 2, -0.1F, false));
		
				hoof_r12 = new ModelRenderer(this);
				hoof_r12.setRotationPoint(-0.7173F, 1.9631F, -0.2625F);
				foot4.addChild(hoof_r12);
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
				if (this.swingProgress > 0.0F) {
					if (this.swingProgress < 0.5F) {
						float f6 = this.swingProgress / 0.5F;
						this.bodyPartAngles(this.body, this.swingingBodyPreset, 0, f6);
						this.bodyPartAngles(this.head, this.swingingHeadPreset, 0, f6);
						this.bodyPartAngles(this.leg1_1, this.swingingLeg1_1Preset, 0, f6);
						this.bodyPartAngles(this.leg1_2, this.swingingLeg1_2Preset, 0, f6);
						this.bodyPartAngles(this.leg1_3, this.swingingLeg1_3Preset, 0, f6);
						this.bodyPartAngles(this.foot1, this.swingingFoot1Preset, 0, f6);
						this.bodyPartAngles(this.leg2_1, this.swingingLeg2_1Preset, 0, f6);
						this.bodyPartAngles(this.leg2_2, this.swingingLeg2_2Preset, 0, f6);
						this.bodyPartAngles(this.leg2_3, this.swingingLeg2_3Preset, 0, f6);
						this.bodyPartAngles(this.foot2, this.swingingFoot2Preset, 0, f6);
					} else if (this.swingProgress < 0.7F) {
						float f6 = (this.swingProgress - 0.5F) / 0.2F;
						this.bodyPartAngles(this.body, this.swingingBodyPreset, 1, f6);
						this.bodyPartAngles(this.head, this.swingingHeadPreset, 1, f6);
						this.bodyPartAngles(this.leg1_1, this.swingingLeg1_1Preset, 1, f6);
						this.bodyPartAngles(this.leg1_2, this.swingingLeg1_2Preset, 1, f6);
						this.bodyPartAngles(this.leg1_3, this.swingingLeg1_3Preset, 1, f6);
						this.bodyPartAngles(this.foot1, this.swingingFoot1Preset, 1, f6);
						this.bodyPartAngles(this.leg2_1, this.swingingLeg2_1Preset, 1, f6);
						this.bodyPartAngles(this.leg2_2, this.swingingLeg2_2Preset, 1, f6);
						this.bodyPartAngles(this.leg2_3, this.swingingLeg2_3Preset, 1, f6);
						this.bodyPartAngles(this.foot2, this.swingingFoot2Preset, 1, f6);
					} else {
						float f6 = (this.swingProgress - 0.7F) / 0.3F;
						this.bodyPartAngles(this.body, this.swingingBodyPreset, 2, f6);
						this.bodyPartAngles(this.head, this.swingingHeadPreset, 2, f6);
						this.bodyPartAngles(this.leg1_1, this.swingingLeg1_1Preset, 2, f6);
						this.bodyPartAngles(this.leg1_2, this.swingingLeg1_2Preset, 2, f6);
						this.bodyPartAngles(this.leg1_3, this.swingingLeg1_3Preset, 2, f6);
						this.bodyPartAngles(this.foot1, this.swingingFoot1Preset, 2, f6);
						this.bodyPartAngles(this.leg2_1, this.swingingLeg2_1Preset, 2, f6);
						this.bodyPartAngles(this.leg2_2, this.swingingLeg2_2Preset, 2, f6);
						this.bodyPartAngles(this.leg2_3, this.swingingLeg2_3Preset, 2, f6);
						this.bodyPartAngles(this.foot2, this.swingingFoot2Preset, 2, f6);
					}
				}
				if (((EntityCustom) e).isShooting()) {
					head.rotateAngleX += -0.1745F;
					jaw.rotateAngleX = 0.7854F;
				} else {
					jaw.rotateAngleX = 0.0F;
				}
				if (((EntityCustom) e).isFaceDown()) {
					body.rotationPointY = 18.75F;
					head.rotateAngleX = 0.4363F;
					leg1.rotateAngleX = -0.9599F;
					leg2.rotateAngleX = -0.9599F;
					leg3.rotateAngleX = 1.1345F;
					leg4.rotateAngleX = 1.1345F;
				} else {
					body.rotationPointY = 12.75F;
				}
				this.copyModelAngles(body, eyesHighlight);
				this.copyModelAngles(head, headsync);
			}

			private void bodyPartAngles(ModelRenderer part, float[][] preset, int seg, float progress) {
				if (seg < 0 || seg >= preset.length) {
					return;
				}
				int to = seg == preset.length - 1 ? 0 : (seg + 1);
				part.rotationPointX = preset[seg][0] + (preset[to][0] - preset[seg][0]) * progress;
				part.rotationPointY = preset[seg][1] + (preset[to][1] - preset[seg][1]) * progress;
				part.rotationPointZ = preset[seg][2] + (preset[to][2] - preset[seg][2]) * progress;
				part.rotateAngleX = preset[seg][3] + (preset[to][3] - preset[seg][3]) * progress;
				part.rotateAngleY = preset[seg][4] + (preset[to][4] - preset[seg][4]) * progress;
				part.rotateAngleZ = preset[seg][5] + (preset[to][5] - preset[seg][5]) * progress;
			}
		}
	}
}
