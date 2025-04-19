
package net.narutomod.entity;

import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemBlackReceiver;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureBanShoTenin;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureShinraTenseiOnKeyPressed;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPainDeva extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 490;
	public static final int ENTITYID_RANGED = 491;

	public EntityPainDeva(ElementsNarutomodMod instance) {
		super(instance, 912);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "pain_deva"), ENTITYID).name("pain_deva").tracker(64, 3, true).egg(-16777216, -26368).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private static final int AMPUSH_CD = 100;
		private static final int UPULL_CD = 100;
		private int amPushTime = -AMPUSH_CD;
		private int uPullTime = -UPULL_CD;
		private int outofrangeTicks;
		private boolean chibakutenseiUsed;
		private int blockingTicks;

		public EntityCustom(World world) {
			super(world, 160, 10000d);
			this.setSize(0.6f, 1.8f);
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelectorAkatsuki));
			this.getEntityData().setBoolean("BlackReceiverTolerance", true);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setCustomNameTag(net.minecraft.util.text.translation.I18n.translateToLocal("entity.pain.name"));
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobe.body));
			this.setItemToInventory(new ItemStack(ItemBlackReceiver.block), 0);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(18.0D);
		}

		@Override
		protected double meleeReach() {
			return 3.4d;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 0.0F, 24.0F));
			this.tasks.addTask(3, new EntityNinjaMob.AIAttackMelee(this, 1.5d, true) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.getDistance(EntityCustom.this.getAttackTarget()) < 6d;
				}
				@Override
				public boolean shouldContinueExecuting() {
					return super.shouldContinueExecuting() && EntityCustom.this.getDistance(EntityCustom.this.getAttackTarget()) < 6d;
				}
			});
			this.tasks.addTask(4, new EntityAIAttackRanged(this, 1.0d, 20, 20f) {
				@Override
				public void startExecuting() {
					super.startExecuting();
					EntityCustom.this.setSwingingArms(true);
				}
				@Override
				public void resetTask() {
					super.resetTask();
					EntityCustom.this.setSwingingArms(false);
				}
			});
			this.tasks.addTask(5, new EntityAIWatchClosest2(this, EntityPlayer.class, 32.0F, 1.0F));
			this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityNinjaMob.Base.class, 24.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !this.entity.isOnSameTeam(this.closestEntity);
				}
			});
			this.tasks.addTask(7, new EntityAIWander(this, 0.5d));
			this.tasks.addTask(8, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive()) {
				if (ProcedureBanShoTenin.isInUse(this)) {
					ProcedureBanShoTenin.execute(this.ticksExisted - this.uPullTime < 40, this, null);
				} else if (this.outofrangeTicks > 60 && this.ticksExisted > this.uPullTime + UPULL_CD) {
					ProcedureBanShoTenin.execute(true, this, new RayTraceResult(this.getAttackTarget()));
					this.uPullTime = this.ticksExisted;
				}
				if (this.getDistanceSq(this.getAttackTarget()) > 256d) {
					++this.outofrangeTicks;
				} else if (this.onGround) {
					this.outofrangeTicks = 0;
				}
			} else {
				this.setSwingingArms(false);
			}
		}

		@Nullable
		private RayTraceResult findSomeBlocks() {
			for (int i = 0; i < 20; i++) {
				Vec3d vec1 = this.getPositionEyes(1f);
				Vec3d vec2 = Vec3d.fromPitchYaw((this.rand.nextFloat()-0.5f) * 90f, this.renderYawOffset + (this.rand.nextFloat()-0.5f) * 180f).scale(24).add(vec1);
				RayTraceResult result = this.world.rayTraceBlocks(vec1, vec2, false, false, true);
				if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
					return result;
				}
			}
			return null;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.FALL) {
				return false;
			}
			if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F && amount <= this.lastDamage) {
				return false;
			}
			Entity attacker = source.getTrueSource();
			if (!this.world.isRemote && source != ProcedureUtils.SPECIAL_DAMAGE) {
				if (attacker instanceof EntityLivingBase && !this.isAIDisabled() && this.rand.nextInt(4) != 0 && !source.isUnblockable()) {
					this.setRevengeTarget((EntityLivingBase)attacker);
					this.world.setEntityState(this, (byte)101);
					this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ting")),
					 0.5f, this.rand.nextFloat() * 0.6f + 0.5f);
					return false;
				}
				if (this.ticksExisted > this.amPushTime + AMPUSH_CD 
				 && this.consumeChakra(ItemRinnegan.SHINRATENSEI_CHAKRA_USAGE * 7.5d)) {
				 	if (attacker instanceof EntityLivingBase) {
						this.setRevengeTarget((EntityLivingBase)attacker);
				 	}
					this.useAMPush(null, 15 + this.rand.nextInt(11));
					return false;
				}
			}
			return super.attackEntityFrom(source, amount);
		}

		private void useAMPush(@Nullable Entity target, float power) {
			ProcedureOnLivingUpdate.setUntargetable(this, 5);
			ProcedureShinraTenseiOnKeyPressed.execute(this, target != null ? new RayTraceResult(target) : null, power);
			this.amPushTime = this.ticksExisted;
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
			ProcedureOnLivingUpdate.forceBowPose(this, swingingArms);
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			RayTraceResult t;
			if (ProcedureBanShoTenin.grabbedEarthBlocks(this)
			 && this.consumeChakra(ItemRinnegan.SHINRATENSEI_CHAKRA_USAGE * 7.5d)) {
				this.useAMPush(target, 19);
			} else if (this.ticksExisted > this.uPullTime + UPULL_CD && (t = this.findSomeBlocks()) != null) {
				this.setSneaking(true);
				ProcedureBanShoTenin.execute(true, this, t);
				this.setSneaking(false);
				this.uPullTime = this.ticksExisted;
			} else { 
				ItemBlackReceiver.EntityArrowCustom.shoot(this, target, 2.0f);
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void handleStatusUpdate(byte id) {
			if (id == 101) {
				this.blockingTicks = 10;
			} else {
				super.handleStatusUpdate(id);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.blockingTicks > 0) {
				--this.blockingTicks;
			}
		}
	}

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
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/pain_deva.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelPainDeva());
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				float f = 0.0625f * 15;
				GlStateManager.scale(f, f, f);
			}

			@Override
			public void transformHeldFull3DItemLayer() {
				GlStateManager.translate(0.0F, 0.1875F, 0.0F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 4.12.1
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelPainDeva extends EntityNinjaMob.ModelNinja {
			//private final ModelRenderer bipedHead;
			private final ModelRenderer hair;
			private final ModelRenderer bone1;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer eyeRight;
			private final ModelRenderer eyeLeft;
			//private final ModelRenderer bipedHeadwear;
			//private final ModelRenderer bipedBody;
			//private final ModelRenderer bipedRightArm;
			//private final ModelRenderer bipedLeftArm;
			//private final ModelRenderer bipedRightLeg;
			//private final ModelRenderer bipedLeftLeg;
		
			public ModelPainDeva() {
				textureWidth = 64;
				textureHeight = 64;
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 52, -6.0F, -12.0F, -7.55F, 12, 12, 0, -3.5F, false));
		
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, -1.0F, 0.0F);
				bipedHead.addChild(hair);
				
		
				bone1 = new ModelRenderer(this);
				bone1.setRotationPoint(-2.0F, -5.0F, 0.0F);
				hair.addChild(bone1);
				setRotationAngle(bone1, 0.0F, 0.0F, -0.5236F);
				bone1.cubeList.add(new ModelBox(bone1, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-2.0F, -4.5F, -2.0F);
				hair.addChild(bone2);
				setRotationAngle(bone2, 0.3491F, 0.0F, -0.3491F);
				bone2.cubeList.add(new ModelBox(bone2, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-2.0F, -4.5F, 2.0F);
				hair.addChild(bone3);
				setRotationAngle(bone3, -0.3491F, 0.0F, -0.3491F);
				bone3.cubeList.add(new ModelBox(bone3, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -5.0F, -2.0F);
				hair.addChild(bone4);
				setRotationAngle(bone4, 0.5236F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, -5.0F, 2.0F);
				hair.addChild(bone5);
				setRotationAngle(bone5, -0.5236F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(2.0F, -5.0F, 0.0F);
				hair.addChild(bone6);
				setRotationAngle(bone6, 0.0F, 0.0F, 0.5236F);
				bone6.cubeList.add(new ModelBox(bone6, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, true));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(2.0F, -4.5F, 2.0F);
				hair.addChild(bone7);
				setRotationAngle(bone7, -0.3491F, 0.0F, 0.3491F);
				bone7.cubeList.add(new ModelBox(bone7, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, true));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(2.0F, -4.5F, -2.0F);
				hair.addChild(bone8);
				setRotationAngle(bone8, 0.3491F, 0.0F, 0.3491F);
				bone8.cubeList.add(new ModelBox(bone8, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, true));
		
				eyeRight = new ModelRenderer(this);
				eyeRight.setRotationPoint(3.0F, 3.25F, -3.95F);
				bipedHead.addChild(eyeRight);
				eyeRight.cubeList.add(new ModelBox(eyeRight, 40, 52, -11.0F, -12.0F, -5.0F, 12, 12, 0, -4.9F, false));
		
				eyeLeft = new ModelRenderer(this);
				eyeLeft.setRotationPoint(-3.0F, 3.25F, -3.95F);
				bipedHead.addChild(eyeLeft);
				eyeLeft.cubeList.add(new ModelBox(eyeLeft, 40, 52, -1.0F, -12.0F, -5.0F, 12, 12, 0, -4.9F, true));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F, false));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				setRotationAngle(bipedRightArm, -0.3927F, 0.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.2F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				setRotationAngle(bipedLeftArm, 0.3927F, 0.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.2F, true));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				setRotationAngle(bipedRightLeg, 0.3927F, 0.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				setRotationAngle(bipedLeftLeg, -0.3927F, 0.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F, true));
			}

			@Override
			public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
				super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
				if (((EntityCustom)entityIn).blockingTicks > 0) {
					setRotationAngle(bipedRightArm, -1.8326F, -0.7854F, 0.3491F);
				}
			}
		}
	}
}
