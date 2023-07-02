
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.EntityAIAttackMelee;
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

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySevenTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 284;
	public static final int ENTITYID_RANGED = 285;
	private static final float MODELSCALE = 14.0F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntitySevenTails(ElementsNarutomodMod instance) {
		super(instance, 604);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "seven_tails"), ENTITYID)
		 .name("seven_tails").tracker(96, 3, true).egg(-10066330, -13312).build());
	}

	public static TailBeastManager getBijuManager() {
		return tailBeastManager;
	}

	public static class TailBeastManager extends EntityBijuManager<EntityCustom> {
		public TailBeastManager() {
			super(EntityCustom.class, 7);
		}

		@Override
		public void setVesselEntity(@Nullable Entity player) {
			super.setVesselEntity(player);
			if (player instanceof EntityPlayer) {
				((EntityPlayer)player).capabilities.allowFlying = true;
				((EntityPlayer)player).sendPlayerAbilities();
			}
		}

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}

	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_seventails";
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
		public EntityCustom(World worldIn) {
			super(worldIn);
			this.setSize(MODELSCALE * 0.25F, MODELSCALE * 1.3125F);
			this.moveHelper = new EntityTailedBeast.FlySwimHelper(this);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.25F, MODELSCALE * 1.3125F);
			this.moveHelper = new EntityTailedBeast.FlySwimHelper(this);
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
			this.setSize(this.width, MODELSCALE * (down ? 0.625F : 1.3125F));
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(1.2D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.2D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10000.0D);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(30.0D);
		}

		@Override
		protected void setMeleeAttackTasks() {
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.2D, true) {
				@Override
				public boolean shouldExecute() {
					return !EntityCustom.this.isMotionHalted() && super.shouldExecute();
				}
				@Override
				protected double getAttackReachSqr(EntityLivingBase attackTarget) {
					return ProcedureUtils.getReachDistanceSq(this.attacker) * 0.36d;
				}
			});
		}

		@Override
		public EntityBijuManager getBijuManager() {
			return tailBeastManager;
		}

		@Override
		public double getMountedYOffset() {
			return this.isFaceDown() ? 3.0d * 0.0625d * MODELSCALE : ((double)this.height - 5.0D);
		}

		@Override
		public boolean shouldRiderSit() {
			return true;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0.3d * MODELSCALE, 0d, 0d) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
				passenger.setPosition(this.posX + vec2.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec2.z);
			}
		}

		@Override
		public float getFuuinBeamHeight() {
			return this.isFaceDown() ? 3.0f * 0.0625f * MODELSCALE : super.getFuuinBeamHeight();
		}

		@Override
		public SoundEvent getAmbientSound() {
			return null;
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return null;
		}

		@Override
		public SoundEvent getDeathSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public void onUpdate() {
			this.setNoGravity(true);
			super.onUpdate();
			if (!this.onGround && this.ticksExisted % 10 == 0) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:chomei_flying")),
				 5f, this.rand.nextFloat() * 0.4f + 0.8f);
			}
		}

		@Override
		public void travel(float ti, float tj, float tk) {
			if (this.isBeingRidden()) {
				EntityLivingBase entity = (EntityLivingBase)this.getControllingPassenger();
				if ((!this.onGround || entity.rotationPitch < 0.0F) && entity.moveForward > 0.0F) {
					this.motionY -= entity.rotationPitch / 45.0D;
				}
			}
			super.travel(ti, tj, tk);
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/seventails.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelSevenTails(), MODELSCALE * 0.5F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 3.9.2
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelSevenTails extends ModelBiped {
			//private final ModelRenderer bipedHead;
			private final ModelRenderer chin;
			private final ModelRenderer mandibleRight;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r4;
			private final ModelRenderer mandibleLeft;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r13;
			private final ModelRenderer hair;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer bone34;
			private final ModelRenderer bone35;
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer eyes;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer rightShoulder;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
			private final ModelRenderer shoulderSpike;
			private final ModelRenderer bone37;
			private final ModelRenderer bone38;
			private final ModelRenderer bone39;
			private final ModelRenderer bone40;
			private final ModelRenderer bone24;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			private final ModelRenderer bone27;
			private final ModelRenderer leftShoulder;
			private final ModelRenderer bone12;
			private final ModelRenderer bone18;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer shoulderSpike2;
			private final ModelRenderer bone36;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
			private final ModelRenderer bone43;
			private final ModelRenderer bone44;
			private final ModelRenderer bone45;
			private final ModelRenderer bone46;
			private final ModelRenderer bone47;
			private final ModelRenderer Chest;
			private final ModelRenderer cube_r5;
			private final ModelRenderer cube_r6;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone8;
			private final ModelRenderer bone48;
			private final ModelRenderer stomach;
			private final ModelRenderer bone11;
			private final ModelRenderer bone10;
			private final ModelRenderer bone9;
			private final ModelRenderer bone7;
			private final ModelRenderer bone13;
			private final ModelRenderer bone14;
			private final ModelRenderer bone15;
			private final ModelRenderer bone16;
			private final ModelRenderer bone17;
			private final ModelRenderer[] tail = new ModelRenderer[6];
			private final ModelRenderer[] tail6 = new ModelRenderer[10];
			private final float[] tailSwayX = new float[10];
			private final float[] tailSwayY = new float[10];
			private final float[] tailSwayZ = new float[10];
			private final float[] tailSwingX = { -0.5236F, -0.6981F, -0.6109F, -0.3491F, -0.1309F, 0.0873F, 0.1745F, 0.1745F, 0.1745F, 0.1745F };
			private final Random rand = new Random();
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer arm1;
			private final ModelRenderer cube_r7;
			private final ModelRenderer bone52;
			private final ModelRenderer bone53;
			private final ModelRenderer bone54;
			private final ModelRenderer arm2;
			private final ModelRenderer cube_r8;
			private final ModelRenderer bone49;
			private final ModelRenderer bone50;
			private final ModelRenderer bone51;
			private final ModelRenderer arm3;
			private final ModelRenderer cube_r9;
			private final ModelRenderer bone55;
			private final ModelRenderer bone56;
			private final ModelRenderer bone57;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer arm4;
			private final ModelRenderer cube_r10;
			private final ModelRenderer bone58;
			private final ModelRenderer bone59;
			private final ModelRenderer bone60;
			private final ModelRenderer arm5;
			private final ModelRenderer cube_r11;
			private final ModelRenderer bone61;
			private final ModelRenderer bone62;
			private final ModelRenderer bone63;
			private final ModelRenderer arm6;
			private final ModelRenderer cube_r12;
			private final ModelRenderer bone64;
			private final ModelRenderer bone65;
			private final ModelRenderer bone66;

			public ModelSevenTails() {
				textureWidth = 64;
				textureHeight = 64;

				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 24.0F, -3.4F);


				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, -19.0F, 2.9F);
				bipedHeadwear.addChild(eyes);
				eyes.cubeList.add(new ModelBox(eyes, 36, 53, -2.0F, -2.6F, -3.505F, 4, 3, 4, -0.5F, false));

				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 24.0F, -3.4F);


				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -19.0F, 2.9F);
				bipedBody.addChild(bipedHead);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 12, 34, -1.5F, -2.0F, -3.0F, 3, 2, 3, -0.05F, false));

				chin = new ModelRenderer(this);
				chin.setRotationPoint(0.0F, -0.1F, -2.75F);
				bipedHead.addChild(chin);
				setRotationAngle(chin, 0.4363F, 0.0F, 0.0F);
				chin.cubeList.add(new ModelBox(chin, 38, 0, -1.5F, -0.2F, -0.3F, 3, 1, 3, -0.1F, false));

				mandibleRight = new ModelRenderer(this);
				mandibleRight.setRotationPoint(-0.75F, 0.65F, 0.5F);
				chin.addChild(mandibleRight);
				setRotationAngle(mandibleRight, 0.5236F, -0.0873F, 0.0F);
		
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(0.6F, 1.625F, -1.575F);
				mandibleRight.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.1309F, -0.1004F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 11, 53, -2.425F, -3.475F, -2.9F, 4, 4, 7, -2.28F, false));
		
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(0.175F, 0.0998F, -1.3586F);
				mandibleRight.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.1309F, -0.1004F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 40, 35, -0.725F, -0.5F, -1.35F, 1, 1, 3, -0.2F, false));
		
				mandibleLeft = new ModelRenderer(this);
				mandibleLeft.setRotationPoint(0.75F, 0.65F, 0.5F);
				chin.addChild(mandibleLeft);
				setRotationAngle(mandibleLeft, 0.5236F, 0.0873F, 0.0F);
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(-0.6F, 1.625F, -1.575F);
				mandibleLeft.addChild(cube_r3);
				setRotationAngle(cube_r3, 0.1309F, 0.1004F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 11, 53, -1.575F, -3.475F, -2.9F, 4, 4, 7, -2.28F, true));
		
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(-0.175F, 0.0998F, -1.3586F);
				mandibleLeft.addChild(cube_r13);
				setRotationAngle(cube_r13, 0.1309F, 0.1004F, 0.0F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 40, 35, -0.275F, -0.5F, -1.35F, 1, 1, 3, -0.2F, true));

				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, -1.15F, -1.55F);
				bipedHead.addChild(hair);
				setRotationAngle(hair, -0.545F, 0.7483F, -0.4043F);
				hair.cubeList.add(new ModelBox(hair, 24, 34, -1.5F, -1.75F, -1.5F, 3, 2, 3, -0.4F, false));

				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.9F, -1.45F, -0.9F);
				hair.addChild(bone31);
				setRotationAngle(bone31, 0.0873F, 0.0F, 0.0873F);
				bone31.cubeList.add(new ModelBox(bone31, 44, 45, -1.9F, -0.825F, -0.1F, 2, 2, 2, -0.1F, false));

				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(-0.2F, -0.8125F, 0.2F);
				bone31.addChild(bone32);
				setRotationAngle(bone32, 0.0436F, 0.0F, 0.0436F);
				bone32.cubeList.add(new ModelBox(bone32, 44, 45, -1.7F, -1.0625F, -0.3F, 2, 2, 2, -0.3F, false));

				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(-0.2F, -1.1042F, 0.2F);
				bone32.addChild(bone33);
				setRotationAngle(bone33, 0.0436F, 0.0F, 0.0436F);
				bone33.cubeList.add(new ModelBox(bone33, 44, 45, -1.5F, -0.9333F, -0.5F, 2, 2, 2, -0.5F, false));

				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(-0.25F, -0.3333F, 0.25F);
				bone33.addChild(bone34);
				setRotationAngle(bone34, 0.0436F, 0.0F, 0.0436F);
				bone34.cubeList.add(new ModelBox(bone34, 44, 45, -1.25F, -1.25F, -0.75F, 2, 2, 2, -0.7F, false));

				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, -0.4F, 0.0F);
				bone34.addChild(bone35);
				setRotationAngle(bone35, 0.0436F, 0.0F, 0.0436F);
				bone35.cubeList.add(new ModelBox(bone35, 44, 45, -1.25F, -1.25F, -0.75F, 2, 2, 2, -0.8F, false));

				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-2.25F, -17.65F, 4.15F);
				bipedBody.addChild(bipedRightArm);

				arm1 = new ModelRenderer(this);
				arm1.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightArm.addChild(arm1);
				setRotationAngle(arm1, -0.2364F, 0.7334F, 0.9286F);

				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(0.1F, 3.3F, 2.5F);
				arm1.addChild(cube_r7);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 27, -0.6F, -3.4F, -3.0F, 1, 3, 1, 0.0F, false));

				bone52 = new ModelRenderer(this);
				bone52.setRotationPoint(0.0F, 2.6F, 0.1F);
				arm1.addChild(bone52);
				setRotationAngle(bone52, -1.0472F, 0.0F, 0.0F);
				bone52.cubeList.add(new ModelBox(bone52, 20, 21, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.1F, false));

				bone53 = new ModelRenderer(this);
				bone53.setRotationPoint(0.0F, 1.5F, -0.1F);
				bone52.addChild(bone53);
				setRotationAngle(bone53, 0.8462F, 0.1719F, -0.3053F);
				bone53.cubeList.add(new ModelBox(bone53, 12, 14, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.15F, false));

				bone54 = new ModelRenderer(this);
				bone54.setRotationPoint(0.0F, 1.4F, 0.0F);
				bone53.addChild(bone54);
				setRotationAngle(bone54, 0.5133F, 0.1084F, -0.1897F);
				bone54.cubeList.add(new ModelBox(bone54, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, false));

				arm2 = new ModelRenderer(this);
				arm2.setRotationPoint(0.0F, 1.0F, -0.75F);
				bipedRightArm.addChild(arm2);
				setRotationAngle(arm2, -0.4546F, 0.6635F, 0.6582F);

				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(0.1F, 3.3F, 2.5F);
				arm2.addChild(cube_r8);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 27, -0.6F, -3.4F, -3.0F, 1, 3, 1, 0.0F, false));

				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(0.0F, 2.6F, 0.1F);
				arm2.addChild(bone49);
				setRotationAngle(bone49, -1.0472F, 0.0F, 0.0F);
				bone49.cubeList.add(new ModelBox(bone49, 20, 21, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.1F, false));

				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(0.0F, 1.5F, -0.1F);
				bone49.addChild(bone50);
				setRotationAngle(bone50, 0.8462F, 0.1719F, -0.3053F);
				bone50.cubeList.add(new ModelBox(bone50, 12, 14, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.15F, false));

				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(0.0F, 1.4F, 0.0F);
				bone50.addChild(bone51);
				setRotationAngle(bone51, 0.9496F, 0.1084F, -0.1897F);
				bone51.cubeList.add(new ModelBox(bone51, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, false));

				arm3 = new ModelRenderer(this);
				arm3.setRotationPoint(0.25F, 2.0F, -1.75F);
				bipedRightArm.addChild(arm3);
				setRotationAngle(arm3, -0.5477F, 0.5623F, 0.4215F);

				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(0.1F, 3.3F, 2.5F);
				arm3.addChild(cube_r9);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 27, -0.6F, -3.4F, -3.0F, 1, 3, 1, 0.0F, false));

				bone55 = new ModelRenderer(this);
				bone55.setRotationPoint(0.0F, 2.6F, 0.1F);
				arm3.addChild(bone55);
				setRotationAngle(bone55, -1.0472F, 0.0F, 0.0F);
				bone55.cubeList.add(new ModelBox(bone55, 20, 21, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.1F, false));

				bone56 = new ModelRenderer(this);
				bone56.setRotationPoint(0.0F, 1.5F, -0.1F);
				bone55.addChild(bone56);
				setRotationAngle(bone56, 0.8462F, 0.1719F, -0.3053F);
				bone56.cubeList.add(new ModelBox(bone56, 12, 14, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.15F, false));

				bone57 = new ModelRenderer(this);
				bone57.setRotationPoint(0.0F, 1.4F, 0.0F);
				bone56.addChild(bone57);
				setRotationAngle(bone57, 1.1241F, 0.1084F, -0.1897F);
				bone57.cubeList.add(new ModelBox(bone57, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, false));

				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(2.25F, -17.65F, 4.15F);
				bipedBody.addChild(bipedLeftArm);


				arm4 = new ModelRenderer(this);
				arm4.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftArm.addChild(arm4);
				setRotationAngle(arm4, -0.2364F, -0.7334F, -0.9286F);

				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(-0.1F, 3.3F, 2.5F);
				arm4.addChild(cube_r10);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 27, -0.4F, -3.4F, -3.0F, 1, 3, 1, 0.0F, true));

				bone58 = new ModelRenderer(this);
				bone58.setRotationPoint(0.0F, 2.6F, 0.1F);
				arm4.addChild(bone58);
				setRotationAngle(bone58, -1.0472F, 0.0F, 0.0F);
				bone58.cubeList.add(new ModelBox(bone58, 20, 21, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.1F, true));

				bone59 = new ModelRenderer(this);
				bone59.setRotationPoint(0.0F, 1.5F, -0.1F);
				bone58.addChild(bone59);
				setRotationAngle(bone59, 0.8462F, -0.1719F, 0.3053F);
				bone59.cubeList.add(new ModelBox(bone59, 12, 14, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.15F, true));

				bone60 = new ModelRenderer(this);
				bone60.setRotationPoint(0.0F, 1.4F, 0.0F);
				bone59.addChild(bone60);
				setRotationAngle(bone60, 0.5133F, -0.1084F, 0.1897F);
				bone60.cubeList.add(new ModelBox(bone60, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, true));

				arm5 = new ModelRenderer(this);
				arm5.setRotationPoint(0.0F, 1.0F, -0.75F);
				bipedLeftArm.addChild(arm5);
				setRotationAngle(arm5, -0.4546F, -0.6635F, -0.6582F);

				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(-0.1F, 3.3F, 2.5F);
				arm5.addChild(cube_r11);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 27, -0.4F, -3.4F, -3.0F, 1, 3, 1, 0.0F, true));

				bone61 = new ModelRenderer(this);
				bone61.setRotationPoint(0.0F, 2.6F, 0.1F);
				arm5.addChild(bone61);
				setRotationAngle(bone61, -1.0472F, 0.0F, 0.0F);
				bone61.cubeList.add(new ModelBox(bone61, 20, 21, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.1F, true));

				bone62 = new ModelRenderer(this);
				bone62.setRotationPoint(0.0F, 1.5F, -0.1F);
				bone61.addChild(bone62);
				setRotationAngle(bone62, 0.8462F, -0.1719F, 0.3053F);
				bone62.cubeList.add(new ModelBox(bone62, 12, 14, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.15F, true));

				bone63 = new ModelRenderer(this);
				bone63.setRotationPoint(0.0F, 1.4F, 0.0F);
				bone62.addChild(bone63);
				setRotationAngle(bone63, 0.9496F, -0.1084F, 0.1897F);
				bone63.cubeList.add(new ModelBox(bone63, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, true));

				arm6 = new ModelRenderer(this);
				arm6.setRotationPoint(-0.25F, 2.0F, -1.75F);
				bipedLeftArm.addChild(arm6);
				setRotationAngle(arm6, -0.5477F, -0.5623F, -0.4215F);

				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(-0.1F, 3.3F, 2.5F);
				arm6.addChild(cube_r12);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 27, -0.4F, -3.4F, -3.0F, 1, 3, 1, 0.0F, true));

				bone64 = new ModelRenderer(this);
				bone64.setRotationPoint(0.0F, 2.6F, 0.1F);
				arm6.addChild(bone64);
				setRotationAngle(bone64, -1.0472F, 0.0F, 0.0F);
				bone64.cubeList.add(new ModelBox(bone64, 20, 21, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.1F, true));

				bone65 = new ModelRenderer(this);
				bone65.setRotationPoint(0.0F, 1.5F, -0.1F);
				bone64.addChild(bone65);
				setRotationAngle(bone65, 0.8462F, -0.1719F, 0.3053F);
				bone65.cubeList.add(new ModelBox(bone65, 12, 14, -0.5F, -0.15F, -0.5F, 1, 2, 1, -0.15F, true));

				bone66 = new ModelRenderer(this);
				bone66.setRotationPoint(0.0F, 1.4F, 0.0F);
				bone65.addChild(bone66);
				setRotationAngle(bone66, 1.1241F, -0.1084F, 0.1897F);
				bone66.cubeList.add(new ModelBox(bone66, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, true));

				rightShoulder = new ModelRenderer(this);
				rightShoulder.setRotationPoint(-3.0F, -20.75F, 3.4F);
				bipedBody.addChild(rightShoulder);
				setRotationAngle(rightShoulder, 0.0F, 0.0F, -0.4363F);


				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(-1.0F, -1.0F, -2.0F);
				rightShoulder.addChild(bone19);


				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone19.addChild(bone20);
				setRotationAngle(bone20, 1.0472F, 0.0F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 0, 38, -0.8452F, 0.9063F, -5.5698F, 2, 1, 4, 0.0F, false));

				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, 3.0F, 1.0F);
				bone19.addChild(bone21);
				bone21.cubeList.add(new ModelBox(bone21, 36, 30, -0.8452F, -1.1874F, -1.0F, 2, 1, 4, 0.0F, false));

				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(0.0F, 0.0F, 4.0F);
				bone19.addChild(bone22);
				setRotationAngle(bone22, -1.0472F, 0.0F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 36, 24, -0.8452F, 0.9063F, 1.5698F, 2, 1, 4, 0.0F, false));

				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, 3.5F, 6.0F);
				bone19.addChild(bone23);
				setRotationAngle(bone23, -2.0071F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 12, 29, -0.8452F, -0.766F, 1.6428F, 3, 1, 4, 0.0F, false));

				shoulderSpike = new ModelRenderer(this);
				shoulderSpike.setRotationPoint(0.5F, -0.25F, 2.0F);
				bone19.addChild(shoulderSpike);
				setRotationAngle(shoulderSpike, 0.0F, -1.0472F, 0.0F);
				shoulderSpike.cubeList.add(new ModelBox(shoulderSpike, 36, 19, -0.9226F, 1.0626F, 0.232F, 1, 1, 1, 0.0F, false));

				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, -0.75F, 0.0F);
				shoulderSpike.addChild(bone37);
				setRotationAngle(bone37, 0.1309F, 0.0F, 0.0F);
				bone37.cubeList.add(new ModelBox(bone37, 36, 19, -0.9226F, 1.1427F, -0.0109F, 1, 1, 1, -0.1F, false));

				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(0.0F, -0.65F, 0.0F);
				bone37.addChild(bone38);
				setRotationAngle(bone38, 0.1309F, 0.0F, 0.0F);
				bone38.cubeList.add(new ModelBox(bone38, 36, 19, -0.9226F, 1.1903F, -0.2621F, 1, 1, 1, -0.2F, false));

				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone38.addChild(bone39);
				setRotationAngle(bone39, 0.1309F, 0.0F, 0.0F);
				bone39.cubeList.add(new ModelBox(bone39, 36, 19, -0.9226F, 1.2048F, -0.5174F, 1, 1, 1, -0.3F, false));

				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone39.addChild(bone40);
				setRotationAngle(bone40, 0.1309F, 0.0F, 0.0F);
				bone40.cubeList.add(new ModelBox(bone40, 36, 19, -0.9226F, 1.1858F, -0.7724F, 1, 1, 1, -0.35F, false));

				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(-1.0F, -1.0F, 0.0F);
				rightShoulder.addChild(bone24);


				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0F, 0.0F, -2.0F);
				bone24.addChild(bone25);
				setRotationAngle(bone25, 1.0472F, 0.0F, -0.5236F);
				bone25.cubeList.add(new ModelBox(bone25, 36, 19, -3.6383F, 0.5736F, -4.9935F, 2, 1, 4, 0.0F, false));

				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone24.addChild(bone26);
				setRotationAngle(bone26, -1.0472F, 0.0F, -0.5236F);
				bone26.cubeList.add(new ModelBox(bone26, 36, 14, -3.6383F, 0.5736F, 0.9935F, 2, 1, 4, 0.0F, false));

				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone24.addChild(bone27);
				setRotationAngle(bone27, 0.0F, 0.0F, -0.5236F);
				bone27.cubeList.add(new ModelBox(bone27, 32, 35, -3.6383F, 1.1472F, -2.0F, 2, 1, 4, 0.0F, false));

				leftShoulder = new ModelRenderer(this);
				leftShoulder.setRotationPoint(3.0F, -20.75F, 3.4F);
				bipedBody.addChild(leftShoulder);
				setRotationAngle(leftShoulder, 0.0F, 0.0F, 0.4363F);


				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(1.0F, -1.0F, -2.0F);
				leftShoulder.addChild(bone12);


				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone12.addChild(bone18);
				setRotationAngle(bone18, 1.0472F, 0.0F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 0, 38, -1.1548F, 0.9063F, -5.5698F, 2, 1, 4, 0.0F, true));

				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.0F, 3.0F, 1.0F);
				bone12.addChild(bone28);
				bone28.cubeList.add(new ModelBox(bone28, 36, 30, -1.1548F, -1.1874F, -1.0F, 2, 1, 4, 0.0F, true));

				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(0.0F, 0.0F, 4.0F);
				bone12.addChild(bone29);
				setRotationAngle(bone29, -1.0472F, 0.0F, 0.0F);
				bone29.cubeList.add(new ModelBox(bone29, 36, 24, -1.1548F, 0.9063F, 1.5698F, 2, 1, 4, 0.0F, true));

				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.0F, 3.5F, 6.0F);
				bone12.addChild(bone30);
				setRotationAngle(bone30, -2.0071F, 0.0F, 0.0F);
				bone30.cubeList.add(new ModelBox(bone30, 12, 29, -2.1548F, -0.766F, 1.6428F, 3, 1, 4, 0.0F, true));

				shoulderSpike2 = new ModelRenderer(this);
				shoulderSpike2.setRotationPoint(-0.5F, -0.25F, 2.0F);
				bone12.addChild(shoulderSpike2);
				setRotationAngle(shoulderSpike2, 0.0F, 1.0472F, 0.0F);
				shoulderSpike2.cubeList.add(new ModelBox(shoulderSpike2, 36, 19, -0.0774F, 1.0626F, 0.232F, 1, 1, 1, 0.0F, true));

				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, -0.75F, 0.0F);
				shoulderSpike2.addChild(bone36);
				setRotationAngle(bone36, 0.1309F, 0.0F, 0.0F);
				bone36.cubeList.add(new ModelBox(bone36, 36, 19, -0.0774F, 1.1427F, -0.0109F, 1, 1, 1, -0.1F, true));

				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.0F, -0.65F, 0.0F);
				bone36.addChild(bone41);
				setRotationAngle(bone41, 0.1309F, 0.0F, 0.0F);
				bone41.cubeList.add(new ModelBox(bone41, 36, 19, -0.0774F, 1.1903F, -0.2621F, 1, 1, 1, -0.2F, true));

				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone41.addChild(bone42);
				setRotationAngle(bone42, 0.1309F, 0.0F, 0.0F);
				bone42.cubeList.add(new ModelBox(bone42, 36, 19, -0.0774F, 1.2048F, -0.5174F, 1, 1, 1, -0.3F, true));

				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone42.addChild(bone43);
				setRotationAngle(bone43, 0.1309F, 0.0F, 0.0F);
				bone43.cubeList.add(new ModelBox(bone43, 36, 19, -0.0774F, 1.1858F, -0.7724F, 1, 1, 1, -0.35F, true));

				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(1.0F, -1.0F, 0.0F);
				leftShoulder.addChild(bone44);


				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(0.0F, 0.0F, -2.0F);
				bone44.addChild(bone45);
				setRotationAngle(bone45, 1.0472F, 0.0F, 0.5236F);
				bone45.cubeList.add(new ModelBox(bone45, 36, 19, 1.6383F, 0.5736F, -4.9935F, 2, 1, 4, 0.0F, true));

				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone44.addChild(bone46);
				setRotationAngle(bone46, -1.0472F, 0.0F, 0.5236F);
				bone46.cubeList.add(new ModelBox(bone46, 36, 14, 1.6383F, 0.5736F, 0.9935F, 2, 1, 4, 0.0F, true));

				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone44.addChild(bone47);
				setRotationAngle(bone47, 0.0F, 0.0F, 0.5236F);
				bone47.cubeList.add(new ModelBox(bone47, 32, 35, 1.6383F, 1.1472F, -2.0F, 2, 1, 4, 0.0F, true));

				Chest = new ModelRenderer(this);
				Chest.setRotationPoint(0.0F, -3.1F, 3.4F);
				bipedBody.addChild(Chest);
				Chest.cubeList.add(new ModelBox(Chest, 12, 0, -2.5F, -16.0F, -2.0F, 5, 3, 4, 0.0F, false));
				Chest.cubeList.add(new ModelBox(Chest, 26, 29, -2.0F, -13.0F, -1.5F, 4, 2, 3, 0.0F, false));
				Chest.cubeList.add(new ModelBox(Chest, 6, 47, -1.0F, -11.0F, -1.0F, 2, 2, 2, 0.0F, false));

				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.0F, 2.0F, 0.0F);
				Chest.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.0F, 0.0F, 0.5672F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 14, 47, -6.25F, -12.0F, -1.0F, 1, 2, 2, 0.0F, false));

				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(0.0F, 2.0F, 0.0F);
				Chest.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.0F, 0.0F, -0.5672F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 14, 47, 5.25F, -12.0F, -1.0F, 1, 2, 2, 0.0F, true));

				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -16.0F, 4.0F);
				Chest.addChild(bone2);
				setRotationAngle(bone2, -0.5236F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 21, 34, -1.0F, -0.134F, 0.0F, 2, 2, 1, 0.1F, false));

				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone2.addChild(bone3);
				setRotationAngle(bone3, 0.1745F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 21, 34, -1.0F, 0.0134F, -0.316F, 2, 2, 1, 0.0F, false));

				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -1.75F, 0.0F);
				bone3.addChild(bone4);
				setRotationAngle(bone4, 0.1745F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 21, 34, -1.0F, 0.1036F, -0.6527F, 2, 2, 1, -0.1F, false));

				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, -1.75F, 0.0F);
				bone4.addChild(bone5);
				setRotationAngle(bone5, 0.1745F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 21, 34, -1.0F, 0.134F, -1.0F, 2, 2, 1, -0.2F, false));

				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, -1.5F, 0.0F);
				bone5.addChild(bone6);
				setRotationAngle(bone6, 0.1745F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 21, 34, -1.0F, 0.1036F, -1.3473F, 2, 2, 1, -0.3F, false));

				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.5F, -1.366F, -0.5F);
				bone6.addChild(bone8);
				setRotationAngle(bone8, 0.1745F, 0.0F, 0.7854F);
				bone8.cubeList.add(new ModelBox(bone8, 12, 0, 0.8927F, -1.4387F, -1.0839F, 1, 3, 1, -0.2F, false));
				bone8.cubeList.add(new ModelBox(bone8, 12, 27, 0.8927F, -0.9387F, -1.0839F, 1, 2, 1, -0.1F, false));
				bone8.cubeList.add(new ModelBox(bone8, 24, 16, 0.8927F, -0.4387F, -1.0839F, 1, 1, 1, 0.0F, false));

				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(-0.5F, -1.366F, -0.5F);
				bone6.addChild(bone48);
				setRotationAngle(bone48, 0.1745F, 0.0F, -0.7854F);
				bone48.cubeList.add(new ModelBox(bone48, 12, 0, -1.8927F, -1.4387F, -1.0839F, 1, 3, 1, -0.2F, true));
				bone48.cubeList.add(new ModelBox(bone48, 12, 27, -1.8927F, -0.9387F, -1.0839F, 1, 2, 1, -0.1F, true));
				bone48.cubeList.add(new ModelBox(bone48, 24, 16, -1.8927F, -0.4387F, -1.0839F, 1, 1, 1, 0.0F, true));

				stomach = new ModelRenderer(this);
				stomach.setRotationPoint(0.0F, -12.95F, 3.4F);
				bipedBody.addChild(stomach);
				stomach.cubeList.add(new ModelBox(stomach, 12, 14, -2.0F, 0.0F, -2.0F, 4, 3, 4, -0.8F, false));

				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, 0.0F, -1.2F);
				stomach.addChild(bone11);
				setRotationAngle(bone11, -0.0873F, 0.0F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 24, 10, -2.0F, 0.9924F, -0.6257F, 4, 2, 4, -0.6F, false));

				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0F, 0.8F, 0.2F);
				bone11.addChild(bone10);
				setRotationAngle(bone10, -0.0873F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 24, 17, -2.0F, 0.9696F, -0.6527F, 4, 2, 4, -0.4F, false));

				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 1.05F, -0.8F);
				bone10.addChild(bone9);
				setRotationAngle(bone9, -0.0873F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 26, 3, -2.0F, 0.9319F, 0.3176F, 4, 2, 4, -0.2F, false));

				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 1.4F, -0.2F);
				bone9.addChild(bone7);
				setRotationAngle(bone7, -0.0873F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 27, -2.0F, 0.8794F, 0.684F, 4, 2, 4, 0.0F, false));

				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, 1.45F, 0.1F);
				bone7.addChild(bone13);
				setRotationAngle(bone13, -0.0873F, 0.0F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 20, 23, -2.0F, 0.8126F, 0.7452F, 4, 2, 4, -0.2F, false));

				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, 1.15F, 0.3F);
				bone13.addChild(bone14);
				setRotationAngle(bone14, -0.0873F, 0.0F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 8, 21, -2.0F, 0.7321F, 0.6F, 4, 2, 4, -0.4F, false));

				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.0F, 0.25F, 0.2F);
				bone14.addChild(bone15);
				setRotationAngle(bone15, -0.0873F, 0.0F, 0.0F);
				bone15.cubeList.add(new ModelBox(bone15, 12, 7, -2.0F, 0.6383F, 0.5472F, 4, 3, 4, -0.6F, false));

				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, 1.8F, 0.3F);
				bone15.addChild(bone16);
				setRotationAngle(bone16, -0.0873F, 0.0F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 0, 33, -1.5F, 0.5321F, 0.7856F, 3, 2, 3, -0.4F, false));

				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, 0.05F, 0.0F);
				bone16.addChild(bone17);
				setRotationAngle(bone17, 0.5236F, 0.0F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 42, 4, -1.0F, 2.4696F, 0.3473F, 2, 3, 2, -0.1F, false));

				tail[0] = new ModelRenderer(this);
				tail[0].setRotationPoint(0.025F, 5.275F, 1.5F);
				bone17.addChild(tail[0]);
				setRotationAngle(tail[0], -0.5236F, 0.0F, -1.0472F);
				tail[0].cubeList.add(new ModelBox(tail[0], 44, 39, -0.9779F, -3.7093F, -1.0726F, 2, 4, 2, 0.0F, false));
				tail[0].cubeList.add(new ModelBox(tail[0], 36, 40, -0.9279F, -9.3593F, -1.0726F, 2, 6, 2, -0.1F, false));
				tail[0].cubeList.add(new ModelBox(tail[0], 28, 40, -0.9279F, -14.3593F, -1.0726F, 2, 6, 2, -0.2F, false));
				tail[0].cubeList.add(new ModelBox(tail[0], 20, 39, -0.9279F, -19.6093F, -1.0726F, 2, 6, 2, -0.3F, false));
				tail[0].cubeList.add(new ModelBox(tail[0], 20, 39, -0.9279F, -23.6093F, -1.0726F, 2, 6, 2, -0.4F, false));
				tail[0].cubeList.add(new ModelBox(tail[0], 20, 39, -0.9279F, -27.6093F, -1.0726F, 2, 6, 2, -0.5F, false));
				tail[0].cubeList.add(new ModelBox(tail[0], 12, 39, -0.8279F, -32.4093F, -1.0726F, 2, 6, 2, -0.6F, false));
				tail[0].cubeList.add(new ModelBox(tail[0], 52, 0, -5.4279F, -31.8593F, -0.0726F, 6, 32, 0, 0.0F, false));

				tail[1] = new ModelRenderer(this);
				tail[1].setRotationPoint(0.025F, 5.275F, 1.5F);
				bone17.addChild(tail[1]);
				setRotationAngle(tail[1], -0.6545F, 0.3927F, -0.9163F);
				tail[1].cubeList.add(new ModelBox(tail[1], 44, 39, -0.9779F, -3.7093F, -1.0726F, 2, 4, 2, 0.0F, false));
				tail[1].cubeList.add(new ModelBox(tail[1], 36, 40, -0.9279F, -9.3593F, -1.0726F, 2, 6, 2, -0.1F, false));
				tail[1].cubeList.add(new ModelBox(tail[1], 28, 40, -0.9279F, -14.3593F, -1.0726F, 2, 6, 2, -0.2F, false));
				tail[1].cubeList.add(new ModelBox(tail[1], 20, 39, -0.9279F, -19.6093F, -1.0726F, 2, 6, 2, -0.3F, false));
				tail[1].cubeList.add(new ModelBox(tail[1], 20, 39, -0.9279F, -23.6093F, -1.0726F, 2, 6, 2, -0.4F, false));
				tail[1].cubeList.add(new ModelBox(tail[1], 20, 39, -0.9279F, -27.6093F, -1.0726F, 2, 6, 2, -0.5F, false));
				tail[1].cubeList.add(new ModelBox(tail[1], 12, 39, -0.8279F, -32.4093F, -1.0726F, 2, 6, 2, -0.6F, false));
				tail[1].cubeList.add(new ModelBox(tail[1], 52, 0, -5.4279F, -31.8593F, -0.0726F, 6, 32, 0, 0.0F, false));

				tail[2] = new ModelRenderer(this);
				tail[2].setRotationPoint(0.025F, 5.275F, 1.5F);
				bone17.addChild(tail[2]);
				setRotationAngle(tail[2], -0.7854F, 0.7854F, -0.7854F);
				tail[2].cubeList.add(new ModelBox(tail[2], 44, 39, -0.9779F, -3.7093F, -1.0726F, 2, 4, 2, 0.0F, false));
				tail[2].cubeList.add(new ModelBox(tail[2], 36, 40, -0.9279F, -9.3593F, -1.0726F, 2, 6, 2, -0.1F, false));
				tail[2].cubeList.add(new ModelBox(tail[2], 28, 40, -0.9279F, -14.3593F, -1.0726F, 2, 6, 2, -0.2F, false));
				tail[2].cubeList.add(new ModelBox(tail[2], 20, 39, -0.9279F, -19.6093F, -1.0726F, 2, 6, 2, -0.3F, false));
				tail[2].cubeList.add(new ModelBox(tail[2], 20, 39, -0.9279F, -23.6093F, -1.0726F, 2, 6, 2, -0.4F, false));
				tail[2].cubeList.add(new ModelBox(tail[2], 20, 39, -0.9279F, -27.6093F, -1.0726F, 2, 6, 2, -0.5F, false));
				tail[2].cubeList.add(new ModelBox(tail[2], 12, 39, -0.8279F, -32.4093F, -1.0726F, 2, 6, 2, -0.6F, false));
				tail[2].cubeList.add(new ModelBox(tail[2], 52, 0, -5.4279F, -31.8593F, -0.0726F, 6, 32, 0, 0.0F, false));

				tail[3] = new ModelRenderer(this);
				tail[3].setRotationPoint(-0.025F, 5.275F, 1.5F);
				bone17.addChild(tail[3]);
				setRotationAngle(tail[3], -0.7854F, -0.7854F, 0.7854F);
				tail[3].cubeList.add(new ModelBox(tail[3], 44, 39, -1.0221F, -3.7093F, -1.0726F, 2, 4, 2, 0.0F, true));
				tail[3].cubeList.add(new ModelBox(tail[3], 36, 40, -1.0721F, -9.3593F, -1.0726F, 2, 6, 2, -0.1F, true));
				tail[3].cubeList.add(new ModelBox(tail[3], 28, 40, -1.0721F, -14.3593F, -1.0726F, 2, 6, 2, -0.2F, true));
				tail[3].cubeList.add(new ModelBox(tail[3], 20, 39, -1.0721F, -19.6093F, -1.0726F, 2, 6, 2, -0.3F, true));
				tail[3].cubeList.add(new ModelBox(tail[3], 20, 39, -1.0721F, -23.6093F, -1.0726F, 2, 6, 2, -0.4F, true));
				tail[3].cubeList.add(new ModelBox(tail[3], 20, 39, -1.0721F, -27.6093F, -1.0726F, 2, 6, 2, -0.5F, true));
				tail[3].cubeList.add(new ModelBox(tail[3], 12, 39, -1.1721F, -32.4093F, -1.0726F, 2, 6, 2, -0.6F, true));
				tail[3].cubeList.add(new ModelBox(tail[3], 52, 0, -0.5721F, -31.8593F, -0.0726F, 6, 32, 0, 0.0F, true));

				tail[4] = new ModelRenderer(this);
				tail[4].setRotationPoint(-0.025F, 5.275F, 1.5F);
				bone17.addChild(tail[4]);
				setRotationAngle(tail[4], -0.6545F, -0.3927F, 0.9163F);
				tail[4].cubeList.add(new ModelBox(tail[4], 44, 39, -1.0221F, -3.7093F, -1.0726F, 2, 4, 2, 0.0F, true));
				tail[4].cubeList.add(new ModelBox(tail[4], 36, 40, -1.0721F, -9.3593F, -1.0726F, 2, 6, 2, -0.1F, true));
				tail[4].cubeList.add(new ModelBox(tail[4], 28, 40, -1.0721F, -14.3593F, -1.0726F, 2, 6, 2, -0.2F, true));
				tail[4].cubeList.add(new ModelBox(tail[4], 20, 39, -1.0721F, -19.6093F, -1.0726F, 2, 6, 2, -0.3F, true));
				tail[4].cubeList.add(new ModelBox(tail[4], 20, 39, -1.0721F, -23.6093F, -1.0726F, 2, 6, 2, -0.4F, true));
				tail[4].cubeList.add(new ModelBox(tail[4], 20, 39, -1.0721F, -27.6093F, -1.0726F, 2, 6, 2, -0.5F, true));
				tail[4].cubeList.add(new ModelBox(tail[4], 12, 39, -1.1721F, -32.4093F, -1.0726F, 2, 6, 2, -0.6F, true));
				tail[4].cubeList.add(new ModelBox(tail[4], 52, 0, -0.5721F, -31.8593F, -0.0726F, 6, 32, 0, 0.0F, true));

				tail[5] = new ModelRenderer(this);
				tail[5].setRotationPoint(-0.025F, 5.275F, 1.5F);
				bone17.addChild(tail[5]);
				setRotationAngle(tail[5], -0.5236F, 0.0F, 1.0472F);
				tail[5].cubeList.add(new ModelBox(tail[5], 44, 39, -1.0221F, -3.7093F, -1.0726F, 2, 4, 2, 0.0F, true));
				tail[5].cubeList.add(new ModelBox(tail[5], 36, 40, -1.0721F, -9.5593F, -1.0726F, 2, 6, 2, -0.1F, true));
				tail[5].cubeList.add(new ModelBox(tail[5], 28, 40, -1.0721F, -15.2593F, -1.0726F, 2, 6, 2, -0.2F, true));
				tail[5].cubeList.add(new ModelBox(tail[5], 20, 39, -1.0721F, -20.0093F, -1.0726F, 2, 6, 2, -0.3F, true));
				tail[5].cubeList.add(new ModelBox(tail[5], 20, 39, -1.0721F, -24.3093F, -1.0726F, 2, 6, 2, -0.4F, true));
				tail[5].cubeList.add(new ModelBox(tail[5], 20, 39, -1.0721F, -28.3093F, -1.0726F, 2, 6, 2, -0.5F, true));
				tail[5].cubeList.add(new ModelBox(tail[5], 12, 39, -1.1721F, -32.4093F, -1.0726F, 2, 6, 2, -0.6F, true));
				tail[5].cubeList.add(new ModelBox(tail[5], 52, 0, -0.5721F, -31.8593F, -0.0726F, 6, 32, 0, 0.0F, true));

				tail6[0] = new ModelRenderer(this);
				tail6[0].setRotationPoint(0.0F, 4.25F, 1.45F);
				bone17.addChild(tail6[0]);
				setRotationAngle(tail6[0], 1.309F, 0.0F, 0.0F);
				tail6[0].cubeList.add(new ModelBox(tail6[0], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, 0.05F, false));

				tail6[1] = new ModelRenderer(this);
				tail6[1].setRotationPoint(0.0F, 3.0F, 0.0F);
				tail6[0].addChild(tail6[1]);
				setRotationAngle(tail6[1], 0.1745F, 0.0F, 0.0F);
				tail6[1].cubeList.add(new ModelBox(tail6[1], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, 0.0F, false));

				tail6[2] = new ModelRenderer(this);
				tail6[2].setRotationPoint(0.0F, 3.0F, 0.0F);
				tail6[1].addChild(tail6[2]);
				setRotationAngle(tail6[2], 0.1745F, 0.0F, 0.0F);
				tail6[2].cubeList.add(new ModelBox(tail6[2], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, 0.0F, false));

				tail6[3] = new ModelRenderer(this);
				tail6[3].setRotationPoint(0.0F, 3.0F, 0.0F);
				tail6[2].addChild(tail6[3]);
				setRotationAngle(tail6[3], 0.1745F, 0.0F, 0.0F);
				tail6[3].cubeList.add(new ModelBox(tail6[3], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, -0.05F, false));

				tail6[4] = new ModelRenderer(this);
				tail6[4].setRotationPoint(0.0F, 3.0F, 0.0F);
				tail6[3].addChild(tail6[4]);
				setRotationAngle(tail6[4], 0.1745F, 0.0F, 0.0F);
				tail6[4].cubeList.add(new ModelBox(tail6[4], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, -0.1F, false));

				tail6[5] = new ModelRenderer(this);
				tail6[5].setRotationPoint(0.0F, 3.0F, 0.0F);
				tail6[4].addChild(tail6[5]);
				setRotationAngle(tail6[5], 0.1745F, 0.0F, 0.0F);
				tail6[5].cubeList.add(new ModelBox(tail6[5], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, -0.15F, false));

				tail6[6] = new ModelRenderer(this);
				tail6[6].setRotationPoint(0.0F, 3.0F, 0.0F);
				tail6[5].addChild(tail6[6]);
				setRotationAngle(tail6[6], 0.1745F, 0.0F, 0.0F);
				tail6[6].cubeList.add(new ModelBox(tail6[6], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, -0.2F, false));

				tail6[7] = new ModelRenderer(this);
				tail6[7].setRotationPoint(0.0F, 3.0F, 0.0F);
				tail6[6].addChild(tail6[7]);
				setRotationAngle(tail6[7], 0.1745F, 0.0F, 0.0F);
				tail6[7].cubeList.add(new ModelBox(tail6[7], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, -0.3F, false));

				tail6[8] = new ModelRenderer(this);
				tail6[8].setRotationPoint(0.0F, 3.0F, 0.0F);
				tail6[7].addChild(tail6[8]);
				setRotationAngle(tail6[8], 0.1745F, 0.0F, 0.0F);
				tail6[8].cubeList.add(new ModelBox(tail6[8], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, -0.45F, false));

				tail6[9] = new ModelRenderer(this);
				tail6[9].setRotationPoint(0.0F, 3.0F, 0.0F);
				tail6[8].addChild(tail6[9]);
				setRotationAngle(tail6[9], 0.1745F, 0.0F, 0.0F);
				tail6[9].cubeList.add(new ModelBox(tail6[9], 0, 43, -1.0F, -0.4048F, -1.0626F, 2, 4, 2, -0.6F, false));

				for (int i = 0; i < 10; i++) {
					tailSwayX[i] = (rand.nextFloat() * 0.2618F + 0.2618F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayZ[i] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayY[i] = (rand.nextFloat() * 0.0873F + 0.0873F);
				}
			}

			@Override
			public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
				bipedLeftLeg.showModel = false;
				bipedRightLeg.showModel = false;
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F);
				//GlStateManager.translate(0.0F, 0.0F, 0.375F * MODELSCALE);
				GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
				bipedBody.render(f5);
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
				float armSwing = this.swingProgress;
				this.swingProgress = 0.0F;
				super.setRotationAngles(0.0F, 0.0F, f2, f3, f4, f5, e);
				this.swingProgress = armSwing;
				bipedHead.rotationPointY += -19.0F;
				bipedRightArm.setRotationPoint(-2.25F, -17.65F, 4.15F);
				bipedLeftArm.setRotationPoint(2.25F, -17.65F, 4.15F);
				if (!e.onGround) {
					float f = MathHelper.sin(f2 * 3F) * 0.1745F;
					tail[0].rotateAngleY = f;
					tail[1].rotateAngleY = f + 0.3927F;
					tail[2].rotateAngleY = f + 0.7854F;
					tail[3].rotateAngleY = -f - 0.7854F;
					tail[4].rotateAngleY = -f - 0.3927F;
					tail[5].rotateAngleY = -f;
				}
				for (int i = 1; i < 10; i++) {
					tail6[i].rotateAngleX = 0.1745F + MathHelper.sin((f2 - i) * 0.05F) * tailSwayX[i];
					tail6[i].rotateAngleZ = MathHelper.cos((f2 - i) * 0.05F) * tailSwayZ[i];
					tail6[i].rotateAngleY = MathHelper.sin((f2 - i) * 0.05F) * tailSwayY[i];
				}
				if (((EntityCustom)e).isFaceDown()) {
					bipedBody.rotationPointZ = 15.0F;
					bipedBody.rotateAngleX = 1.4835F;
					bipedHead.rotateAngleX = -0.2618F;
					tail6[0].rotateAngleX = 0.0F;
				} else {
					bipedBody.rotationPointZ = -3.4F;
					bipedBody.rotateAngleX = 0.0F;
					tail6[0].rotateAngleX = 1.309F;
					if (this.swingProgress > 0.0F) {
						float f = MathHelper.sqrt(MathHelper.sin(this.swingProgress * (float)Math.PI));
						for (int i = 0; i < 10; i++) {
							tail6[i].rotateAngleX += (tailSwingX[i] - tail6[i].rotateAngleX) * f;
							tail6[i].rotateAngleY += -tail6[i].rotateAngleY * f;
							tail6[i].rotateAngleZ += -tail6[i].rotateAngleZ * f;
						}
					}
					if (((EntityCustom)e).isShooting()) {
						bipedHead.rotateAngleX += -0.2618F;
						mandibleRight.rotateAngleY = 0.2182F;
						mandibleLeft.rotateAngleY = -0.2182F;
					} else {
						float f = MathHelper.sin(f2) * 0.0436F;
						mandibleRight.rotateAngleY = -0.0873F + f;
						mandibleLeft.rotateAngleY = 0.0873F - f;
					}
				}
				this.copyModelAngles(bipedBody, bipedHeadwear);
				this.copyModelAngles(bipedHead, eyes);
			}
		}
	}
}
