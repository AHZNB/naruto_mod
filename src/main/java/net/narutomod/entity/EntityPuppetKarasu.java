
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;

import net.narutomod.item.ItemSenbon;
import net.narutomod.item.ItemPoisonbomb;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppetKarasu extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 379;
	public static final int ENTITYID_RANGED = 380;

	public EntityPuppetKarasu(ElementsNarutomodMod instance) {
		super(instance, 738);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "puppet_karasu"), ENTITYID)
		 .name("puppet_karasu").tracker(64, 3, true).build());
	}

	public static class EntityCustom extends EntityPuppet.Base implements IRangedAttackMob {
		private static final DataParameter<Boolean> MOUTH_OPEN = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Boolean> KNIVES_OUT = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		public static final float MAXHEALTH = 100.0f;
		private static final Vec3d offsetToOwner = new Vec3d(1.6d, 0.5d, 3.0d);
		private int meleeTime;
		
		public EntityCustom(World worldIn) {
			super(worldIn);
			this.setSize(0.6f, 2.2f);
		}

		public EntityCustom(EntityLivingBase ownerIn, double chakraUsage) {
			super(ownerIn, chakraUsage);
			this.setSize(0.6f, 2.2f);
			Vec3d vec = ownerIn.getLookVec();
			vec = ownerIn.getPositionVector().addVector(vec.x, 1d, vec.z);
			this.setLocationAndAngles(vec.x, vec.y, vec.z, ownerIn.rotationYaw, 0f);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(MOUTH_OPEN, Boolean.valueOf(false));
			this.getDataManager().register(KNIVES_OUT, Boolean.valueOf(false));
		}

		protected boolean isMouthOpen() {
			return ((Boolean)this.getDataManager().get(MOUTH_OPEN)).booleanValue();
		}

		public void setMouthOpen(boolean down) {
			this.getDataManager().set(MOUTH_OPEN, Boolean.valueOf(down));
		}

		protected boolean isKnivesOut() {
			return ((Boolean)this.getDataManager().get(KNIVES_OUT)).booleanValue();
		}

		public void setKnivesOut(boolean out) {
			this.getDataManager().set(KNIVES_OUT, Boolean.valueOf(out));
		}

		@Override
		protected Vec3d getOffsetToOwner() {
			return this.offsetToOwner;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAXHEALTH);
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedTactical(this, 1.2d, 20, 16.0f) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.meleeTime <= 0;
				}
				@Override
				public boolean shouldContinueExecuting() {
					return super.shouldContinueExecuting() && EntityCustom.this.rand.nextFloat() > 0.03f;
				}
				@Override
				public void resetTask() {
					super.resetTask();
					EntityCustom.this.meleeTime = 80;
				}
			});
			this.tasks.addTask(2, new EntityNinjaMob.AIAttackMelee(this, 2.0d, true) {
				@Override
				public boolean shouldContinueExecuting() {
					return super.shouldContinueExecuting() && EntityCustom.this.meleeTime > 0;
				}
			});
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.getAttackTarget() != null && !this.isKnivesOut()) {
				this.setKnivesOut(true);
			}
			if (this.getAttackTarget() == null && this.isKnivesOut()) {
				this.setKnivesOut(false);
			}
			if (this.meleeTime > 0) {
				--this.meleeTime;
			}
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
			this.setMouthOpen(swingingArms);
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			EntityLivingBase owner = this.getSummoner();
			if (owner != null && target.getDistance(owner) > 14.0d && this.rand.nextFloat() < 0.2f) {
				ItemPoisonbomb.EntityArrowCustom entityarrow = new ItemPoisonbomb.EntityArrowCustom(this.world, this);
				Vec3d vec = target.getPositionVector().subtract(this.getPositionEyes(1f));
				entityarrow.shoot(vec.x, vec.y, vec.z, 2f, 0f);
				entityarrow.setSilent(true);
				entityarrow.setIsCritical(false);
				entityarrow.setDamage(5);
				this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.snowman.shoot")), 1f, 1f / (this.rand.nextFloat() * 0.5f + 1f) + (1f / 2));
				this.world.spawnEntity(entityarrow);
			} else {
				Vec3d vec = target.getPositionEyes(1f);
				for (int i = 0; i < 8; i++) {
					ItemSenbon.spawnArrow(this, vec);
				}
			}
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new EntityPuppet.ClientClass.Renderer<EntityCustom>(renderManager, new ModelKarasu(), 0.5f) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/karasu.png");
					@Override
					protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
						float f = 0.0625f * 18f;
						GlStateManager.scale(f, f, f);
					}
					@Override
					protected ResourceLocation getEntityTexture(EntityCustom entity) {
						return this.texture;
					}
				};
			});
		}

		// Made with Blockbench 3.8.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelKarasu extends ModelBiped {
			private final ModelRenderer jaw;
			private final ModelRenderer shooter;
			private final ModelRenderer bone3;
			private final ModelRenderer bone;
			private final ModelRenderer bone2;
			private final ModelRenderer rightArm2;
			private final ModelRenderer bone5;
			private final ModelRenderer blade2;
			private final ModelRenderer leftArm2;
			private final ModelRenderer bone7;
			private final ModelRenderer blade3;
			private final ModelRenderer bone4;
			private final ModelRenderer blade0;
			private final ModelRenderer bone6;
			private final ModelRenderer blade1;
	
			public ModelKarasu() {
				textureWidth = 64;
				textureHeight = 64;
				
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, -0.5F, false));
		
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, -0.5F, -1.5F);
				bipedHead.addChild(jaw);
				setRotationAngle(jaw, 0.3491F, 0.0F, 0.0F);
				jaw.cubeList.add(new ModelBox(jaw, 0, 0, -1.0F, -1.0F, -2.0F, 2, 1, 2, 0.0F, false));
		
				shooter = new ModelRenderer(this);
				shooter.setRotationPoint(0.0F, -1.25F, -4.0F);
				bipedHead.addChild(shooter);
				shooter.cubeList.add(new ModelBox(shooter, 11, 16, -0.5F, -0.5F, -1.0F, 1, 1, 2, -0.1F, false));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, -4.0F, 0.5F);
				bipedHeadwear.addChild(bone3);
				setRotationAngle(bone3, 0.2618F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 32, 0, -4.0F, -4.0F, -4.0F, 8, 8, 8, 0.5F, false));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, -4.0F, 0.0F);
				bipedHeadwear.addChild(bone);
				setRotationAngle(bone, 0.0F, 0.0F, 0.2618F);
				bone.cubeList.add(new ModelBox(bone, 32, 0, -4.0F, -4.0F, -4.0F, 8, 8, 8, 0.5F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -4.0F, 0.0F);
				bipedHeadwear.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.0F, -0.2618F);
				bone2.cubeList.add(new ModelBox(bone2, 32, 0, -4.0F, -4.0F, -4.0F, 8, 8, 8, 0.5F, true));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.6F, false));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedBody.addChild(bipedRightLeg);
				setRotationAngle(bipedRightLeg, 0.0F, 0.0F, 0.0873F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -1.5F, 0.0F, -2.0F, 3, 12, 4, -0.2F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.6F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedBody.addChild(bipedLeftLeg);
				setRotationAngle(bipedLeftLeg, 0.0F, 0.0F, -0.0873F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -1.5F, 0.0F, -2.0F, 3, 12, 4, -0.2F, false));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.6F, false));
		
				rightArm2 = new ModelRenderer(this);
				rightArm2.setRotationPoint(-5.0F, 6.5F, 0.0F);
				bipedBody.addChild(rightArm2);
				setRotationAngle(rightArm2, 0.0F, 0.0F, 0.2618F);
				rightArm2.cubeList.add(new ModelBox(rightArm2, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.6F, false));
				rightArm2.cubeList.add(new ModelBox(rightArm2, 40, 20, -2.0F, -2.0F, -2.0F, 3, 8, 4, -0.2F, false));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-1.75F, 5.85F, 1.75F);
				rightArm2.addChild(bone5);
				setRotationAngle(bone5, -0.0873F, 0.0F, -0.0873F);
				bone5.cubeList.add(new ModelBox(bone5, 40, 20, -0.2282F, -0.249F, -3.75F, 3, 8, 4, -0.2F, false));
		
				blade2 = new ModelRenderer(this);
				blade2.setRotationPoint(0.25F, 12.65F, -1.75F);
				bone5.addChild(blade2);
				blade2.cubeList.add(new ModelBox(blade2, 24, 0, 1.0F, -5.0F, -1.0F, 0, 6, 2, 0.0F, false));
		
				leftArm2 = new ModelRenderer(this);
				leftArm2.setRotationPoint(5.0F, 6.5F, 0.0F);
				bipedBody.addChild(leftArm2);
				setRotationAngle(leftArm2, 0.0F, 0.0F, -0.2618F);
				leftArm2.cubeList.add(new ModelBox(leftArm2, 40, 32, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.6F, true));
				leftArm2.cubeList.add(new ModelBox(leftArm2, 40, 20, -1.0F, -2.0F, -2.0F, 3, 8, 4, -0.2F, true));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(1.75F, 5.85F, 1.75F);
				leftArm2.addChild(bone7);
				setRotationAngle(bone7, -0.0873F, 0.0F, 0.0873F);
				bone7.cubeList.add(new ModelBox(bone7, 40, 20, -2.7718F, -0.249F, -3.75F, 3, 8, 4, -0.2F, true));
		
				blade3 = new ModelRenderer(this);
				blade3.setRotationPoint(-0.25F, 12.65F, -1.75F);
				bone7.addChild(blade3);
				blade3.cubeList.add(new ModelBox(blade3, 24, 0, -1.0F, -5.0F, -1.0F, 0, 6, 2, 0.0F, true));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				setRotationAngle(bipedRightArm, 0.0F, 0.0F, 0.5236F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.6F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 32, 52, -2.0F, -2.0F, -2.0F, 3, 8, 4, -0.2F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-1.75F, 5.85F, 1.75F);
				bipedRightArm.addChild(bone4);
				setRotationAngle(bone4, -0.0873F, 0.0F, -0.0873F);
				bone4.cubeList.add(new ModelBox(bone4, 40, 20, -0.2282F, -0.249F, -3.75F, 3, 8, 4, -0.2F, false));
		
				blade0 = new ModelRenderer(this);
				blade0.setRotationPoint(0.25F, 12.65F, -1.75F);
				bone4.addChild(blade0);
				blade0.cubeList.add(new ModelBox(blade0, 24, 0, 1.0F, -5.0F, -1.0F, 0, 6, 2, 0.0F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				setRotationAngle(bipedLeftArm, 0.0F, 0.0F, -0.5236F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.6F, true));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 52, -1.0F, -2.0F, -2.0F, 3, 8, 4, -0.2F, true));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(1.75F, 5.85F, 1.75F);
				bipedLeftArm.addChild(bone6);
				setRotationAngle(bone6, -0.0873F, 0.0F, 0.0873F);
				bone6.cubeList.add(new ModelBox(bone6, 40, 20, -2.7718F, -0.249F, -3.75F, 3, 8, 4, -0.2F, true));
		
				blade1 = new ModelRenderer(this);
				blade1.setRotationPoint(-0.25F, 12.65F, -1.75F);
				bone6.addChild(blade1);
				blade1.cubeList.add(new ModelBox(blade1, 24, 0, -1.0F, -5.0F, -1.0F, 0, 6, 2, 0.0F, true));
			}
	
			@Override
			public void render(Entity entityIn, float f, float f1, float f2, float f3, float f4, float scale) {
				this.setRotationAngles(f, f1, f2, f3, f4, scale, entityIn);
				this.bipedHead.render(scale);
				this.bipedHeadwear.render(scale);
				this.bipedBody.render(scale);
				this.bipedRightArm.render(scale);
				this.bipedLeftArm.render(scale);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(0f, 0f, f2, f3, f4, f5, e);
				bipedRightArm.rotateAngleZ += 0.5236F;
				bipedLeftArm.rotateAngleZ += -0.5236F;
		        if (e instanceof EntityCustom) {
					boolean flag = ((EntityCustom)e).isKnivesOut();
					blade0.showModel = flag;
					blade1.showModel = flag;
					blade2.showModel = flag;
					blade3.showModel = flag;
					flag = ((EntityCustom)e).isMouthOpen();
					shooter.showModel = flag;
					jaw.rotateAngleX = flag ? 0.5236F : 0.0F;
					double velocity = ((EntityCustom)e).getVelocity();
					if (velocity > 0.001d && ((EntityCustom)e).isMovingForward()) {
						float fa = MathHelper.clamp((float)velocity * 2.5F, 0.0F, 1.0F);
						bipedBody.rotateAngleX += fa * 1.0472F;
						if (this.swingProgress <= 0.0F && rightArmPose == ModelBiped.ArmPose.EMPTY) {
							bipedRightArm.rotateAngleX += fa;
						}
						if (leftArmPose == ModelBiped.ArmPose.EMPTY) {
							bipedLeftArm.rotateAngleX += fa;
						}
						bipedRightLeg.rotateAngleX = 0.0F;
						bipedLeftLeg.rotateAngleX = 0.0F;
					}
		        }
		        if (this.swingProgress > 0.0F) {
					rightArm2.rotateAngleX = bipedRightArm.rotateAngleX;
					rightArm2.rotateAngleY = bipedRightArm.rotateAngleY;
					rightArm2.rotateAngleZ = bipedRightArm.rotateAngleZ - 0.2618F;
					leftArm2.rotateAngleX = bipedLeftArm.rotateAngleX;
					leftArm2.rotateAngleY = bipedLeftArm.rotateAngleY;
					leftArm2.rotateAngleZ = bipedLeftArm.rotateAngleZ + 0.2618F;
		        } else {
			        rightArm2.rotateAngleX = MathHelper.sin(f2 * 0.067F) * 0.03F;
		        	rightArm2.rotateAngleY = 0.0F;
			        rightArm2.rotateAngleZ = MathHelper.cos(f2 * 0.09F) * 0.03F + 0.2182F + 0.03F;
			        leftArm2.rotateAngleX = -MathHelper.sin(f2 * 0.067F) * 0.03F;
		        	leftArm2.rotateAngleY = 0.0F;
			        leftArm2.rotateAngleZ = -MathHelper.cos(f2 * 0.09F) * 0.03F - 0.2618F - 0.03F;
		        }
			}
		}
	}
}
