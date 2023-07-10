
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.item.ItemSuiton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityToad extends ElementsNarutomodMod.ModElement {
	public EntityToad(ElementsNarutomodMod instance) {
		super(instance, 709);
	}

	public static abstract class EntityCustom extends EntitySummonAnimal.Base implements IRangedAttackMob {
		private static DataParameter<Boolean> MOUTH_OPEN = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private float prevJumpProgress;
		private float jumpProgress;
		protected final Navigator jumpNavigator;
		private final EntityAIWander aiWander = new EntityAIWander(this, 1.2, 20) {
			@Override
			@Nullable
			protected Vec3d getPosition() {
				float f = EntityCustom.this.getScale();
				Vec3d vec = this.entity.getPositionVector();
				while (vec != null && vec.distanceTo(this.entity.getPositionVector()) < 4.0d + f) {
					vec = RandomPositionGenerator.findRandomTarget(this.entity, 7 + (int)(f * 3), 6 + (int)f);
				}
				return vec;
			}
			@Override
			public boolean shouldContinueExecuting() {
				return !this.entity.onGround;
			}
			@Override
			public void startExecuting() {
				EntityCustom.this.jumpNavigator.setNavigateTarget(this.x, this.y, this.z);
			}
		};

		public EntityCustom(World world) {
			super(world);
			this.setOGSize(0.8f, 1.125f);
			this.isImmuneToFire = false;
			this.setNoAI(!true);
			this.moveHelper = new MoveHelper(this);
			this.jumpNavigator = new Navigator(this);
			this.dontWander(false);
		}

		public EntityCustom(EntityLivingBase summonerIn) {
			super(summonerIn);
			this.setOGSize(0.8f, 1.125f);
			this.isImmuneToFire = false;
			this.setNoAI(!true);
			this.moveHelper = new MoveHelper(this);
			this.jumpNavigator = new Navigator(this);
		}

		@Override
		public void entityInit() {
			super.entityInit();
			this.getDataManager().register(MOUTH_OPEN, Boolean.valueOf(false));
		}

		public Navigator getToadNavigator() {
			return this.jumpNavigator;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		}

		@Override
		protected void postScaleFixup() {
			float f = this.getScale();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5D * f);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D * f * f);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6D * f);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(13D + 3D * f);
			super.postScaleFixup();
			//this.setSize(this.ogWidth * f, this.ogHeight * f);
			//this.setHealth(this.getMaxHealth());
			this.experienceValue = (int)(f * 10);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAISwimming(this));
			this.tasks.addTask(2, new AIAttackMelee(this, true));
			this.tasks.addTask(4, new EntityAILookIdle(this));
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			/*this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityMob.class, true, false) {
				@Override
				protected AxisAlignedBB getTargetableArea(double targetDistance) {
					return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 2.0d * EntityCustom.this.getScale(), targetDistance);
				}
			});*/
		}

		@Override
		protected void dontWander(boolean set) {
			if (!set) {
				this.tasks.addTask(3, this.aiWander);
			} else {
				this.tasks.removeTask(this.aiWander);
			}
		}

		@Override
		public boolean couldBreakBlocks() {
			return this.world.getGameRules().getBoolean("mobGriefing") && this.getScale() >= 4f;
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			this.jumpNavigator.updateNavigation();
		}

		@Override
		public boolean canAttackClass(Class <? extends EntityLivingBase > cls) {
			return !EntityCustom.class.isAssignableFrom(cls);
		}

		@Override
		public void setAttackTarget(@Nullable EntityLivingBase entityIn) {
			if (entityIn == null || !this.isOnSameTeam(entityIn)) {
				super.setAttackTarget(entityIn);
			}
		}

	    @Override
	    public boolean isOnSameTeam(Entity entityIn) {
	    	return entityIn instanceof EntityCustom || super.isOnSameTeam(entityIn);
	    }

		@Override
		public SoundEvent getAmbientSound() {
			return null;
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return SoundEvents.ENTITY_ILLUSION_ILLAGER_HURT;
		}

		@Override
		public SoundEvent getDeathSound() {
			return null;
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		protected float getSoundPitch() {
			return 0.6F;
		}

		@Override
		public double getMountedYOffset() {
			return (double)this.ogHeight * this.getScale();
		}

		@Override
		public boolean shouldRiderSit() {
			return false;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			float f = this.getScale();
			Vec3d vec[] = { new Vec3d(0.0d, 0.0d, 0.346d * f) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-(this.rotationYawHead - this.renderYawOffset) * 0.017453292F)
				 .addVector(0.0d, 0.0d, 0.3415d * f)
				 .rotateYaw(-this.renderYawOffset * 0.017453292F).add(this.getPositionVector());
				passenger.setPosition(vec2.x, vec2.y + (double)this.ogHeight * f, vec2.z);
			}
		}

		@Override
		public boolean canSitOnShoulder() {
			return this.getScale() <= 0.6f;
		}

		@SideOnly(Side.CLIENT)
		float getJumpProgress(float partialTicks) {
			return this.prevJumpProgress + (this.jumpProgress - this.prevJumpProgress) * partialTicks;
		}

		@Override
		public void onEntityUpdate() {
			this.prevJumpProgress = this.jumpProgress;
			super.onEntityUpdate();
		}

		@Override
		public void onLivingUpdate() {
			super.onLivingUpdate();
			this.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2, 4, false, false));
			if (this.isRiding()) {
				this.jumpProgress = 0;
			} else if (!this.onGround) {
				this.fallDistance = 0.0f;
				BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
				int i = (int)this.posY;
				int j = MathHelper.floor(this.posX);
				int k = MathHelper.floor(this.posZ);
				AxisAlignedBB bb = null;
				for ( ; bb == null && i > 0; i--) {
					bb = this.world.getBlockState(pos.setPos(j, i, k)).getCollisionBoundingBox(this.world, pos);
				}
				pos.release();
				if (i > 0) {
					this.jumpProgress = (float)MathHelper.clamp((this.posY - (bb.maxY + i)) / (0.8125d * this.getScale()), 0.0d, 1.0d);
				}
			}
		}

		protected boolean getMouthOpen() {
			return ((Boolean)this.getDataManager().get(MOUTH_OPEN)).booleanValue();
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
			this.getDataManager().set(MOUTH_OPEN, Boolean.valueOf(swingingArms));
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			new ItemSuiton.EntityStream.Jutsu().createJutsu(ItemStack.EMPTY, this, distanceFactor);
		}

		public class Navigator {
			private final EntityCustom entity;
			private Vec3d target;
			private int attempts;
			private int ticksInJump;
			private int jumpDelay;
			private boolean doubleJumpInProgress;

			protected Navigator(EntityCustom entityIn) {
				this.entity = entityIn;
			}
			
			protected void setNavigateTarget(@Nullable Vec3d targetVec) {
				if (targetVec == null || !targetVec.equals(this.target)) {
					this.attempts = 0;
					this.jumpDelay = 5;
				}
				this.target = targetVec;
			}

			public void setNavigateTarget(double x, double y, double z) {
				this.setNavigateTarget(new Vec3d(x, y, z));
			}
	
			protected boolean noPath() {
				return this.target == null;
			}
	
			private void updateNavigation() {
//System.out.println(">>> "+(noPath() ? "noPath" : ("target:"+target+", attempts:"+attempts+", ticksInJump:"+ticksInJump+", jumpDelay:"+jumpDelay+", doubleJump:"+doubleJumpInProgress)));
				if (!this.noPath()) {
					if (this.attempts > 5) {
						this.setNavigateTarget(null);
					} else if (this.entity.onGround) {
						this.ticksInJump = 0;
						this.doubleJumpInProgress = false;
						if (this.target.distanceTo(this.entity.getPositionVector()) > this.entity.width * 0.5f + 2.0f) {
							if (--this.jumpDelay <= 0) {
								this.entity.getMoveHelper().setMoveTo(this.target.x, this.target.y, this.target.z, 1.0d);
								this.jumpDelay = 10 + this.entity.getRNG().nextInt(11);
								++this.attempts;
							}
						} else {
							this.setNavigateTarget(null);
						}
					} else {
						++this.ticksInJump;
						//if (!this.doubleJumpInProgress && this.attempts > 3 && this.ticksInJump == (this.attempts-3) * 10) {
						if (!this.doubleJumpInProgress && this.attempts > 3 && this.ticksInJump > 3 && this.entity.motionY < 0.2d) {
							this.entity.getMoveHelper().setMoveTo(this.target.x, this.target.y, this.target.z, 1.0d);
							//++this.attempts;
							this.doubleJumpInProgress = true;
						}
					}
				}
			}
		}

		static class AIAttackMelee extends EntityAIBase {
		    protected EntityCustom attacker;
		    protected int attackTick;
		    boolean longMemory;
		    private int delayCounter;
		    private double targetX;
		    private double targetY;
		    private double targetZ;
		    protected final int attackInterval = 20;

			public AIAttackMelee(EntityCustom entityIn, boolean useLongMemory) {
				this.attacker = entityIn;
				this.longMemory = useLongMemory;
				this.setMutexBits(3);
			}

			@Override
			public boolean shouldExecute() {
				EntityLivingBase target = this.attacker.getAttackTarget();
				if (target != null && target.isEntityAlive()) {
					if (target.getDistanceSq(this.attacker) > this.getAttackReachSqr(target)) {
						this.attacker.jumpNavigator.setNavigateTarget(target.getPositionVector());
					}
					return true;
				}
				return false;
			}

			@Override
			public boolean shouldContinueExecuting() {
				EntityLivingBase target = this.attacker.getAttackTarget();
				if (target == null || !target.isEntityAlive()) {
					return false;
				} else if (!this.longMemory) {
					return !this.attacker.jumpNavigator.noPath();
				} else {
					return !(target instanceof EntityPlayer) || !((EntityPlayer)target).isSpectator() && !((EntityPlayer)target).isCreative();
				}
			}

			@Override
			public void startExecuting() {
				this.attacker.jumpNavigator.setNavigateTarget(this.attacker.getAttackTarget().getPositionVector());
				this.delayCounter = 0;
			}

			@Override
			public void resetTask() {
				EntityLivingBase target = this.attacker.getAttackTarget();
				if (target instanceof EntityPlayer && (((EntityPlayer)target).isSpectator() || ((EntityPlayer)target).isCreative())) {
					this.attacker.setAttackTarget(null);
				}
				this.attacker.jumpNavigator.setNavigateTarget(null);
			}

			@Override
			public void updateTask() {
				EntityLivingBase target = this.attacker.getAttackTarget();
				this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
				double d0 = this.attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
				--this.delayCounter;
		        if ((this.longMemory || this.attacker.getEntitySenses().canSee(target))
		         && this.delayCounter <= 0 
		         && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D 
		          || target.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D 
		          || this.attacker.getRNG().nextFloat() < 0.05F)) {
		            this.targetX = target.posX;
		            this.targetY = target.getEntityBoundingBox().minY;
		            this.targetZ = target.posZ;
		            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
		            this.attacker.jumpNavigator.setNavigateTarget(target.getPositionVector());
		        }
		        this.attackTick = Math.max(this.attackTick - 1, 0);
		        this.checkAndPerformAttack(target, d0);
			}

		    protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_) {
		        if (p_190102_2_ <= this.getAttackReachSqr(p_190102_1_) && this.attackTick <= 0) {
		            this.attackTick = 20;
		            this.attacker.swingArm(EnumHand.MAIN_HAND);
		            this.attacker.attackEntityAsMob(p_190102_1_);
		        }
		    }

			protected double getAttackReachSqr(EntityLivingBase attackTarget) {
				return (double)(this.attacker.width * this.attacker.width * 4.0F + attackTarget.width);
			}
		}

		static class AIAttackRanged extends EntityAIBase {
		    private final EntityCustom entityHost;
		    private final IRangedAttackMob rangedAttackEntityHost;
		    private EntityLivingBase attackTarget;
		    private int rangedAttackTime;
		    private int seeTime;
		    private final int attackIntervalMin;
		    private final int maxRangedAttackTime;
		    private final float attackRadius;
		    private final float maxAttackDistance;
		
		    public AIAttackRanged(IRangedAttackMob attacker, int maxAttackTime, float maxAttackDistanceIn) {
		        this(attacker, maxAttackTime, maxAttackTime, maxAttackDistanceIn);
		    }
		
		    public AIAttackRanged(IRangedAttackMob attacker, int p_i1650_4_, int maxAttackTime, float maxAttackDistanceIn) {
		        this.rangedAttackTime = -1;
		        if (!(attacker instanceof EntityCustom)) {
		            throw new IllegalArgumentException("AIAttackRanged requires Mob implements RangedAttackMob");
		        } else {
		            this.rangedAttackEntityHost = attacker;
		            this.entityHost = (EntityCustom)attacker;
		            this.attackIntervalMin = p_i1650_4_;
		            this.maxRangedAttackTime = maxAttackTime;
		            this.attackRadius = maxAttackDistanceIn;
		            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
		            this.setMutexBits(3);
		        }
		    }
		
		    @Override
		    public boolean shouldExecute() {
		        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();
		        if (entitylivingbase == null || !entitylivingbase.isEntityAlive()) {
		            return false;
		        } else {
		            this.attackTarget = entitylivingbase;
		            return true;
		        }
		    }
		
			@Override
		    public boolean shouldContinueExecuting() {
		        return this.shouldExecute() || !this.entityHost.getToadNavigator().noPath();
		    }
		
		    @Override
		    public void resetTask() {
		        this.attackTarget = null;
		        this.seeTime = 0;
		        this.rangedAttackTime = -1;
		    }
		
		    @Override
		    public void updateTask() {
		        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
		        boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);
		        if (flag) {
		            ++this.seeTime;
		        } else {
		            this.seeTime = 0;
		        }
		        if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20) {
		            this.entityHost.getToadNavigator().setNavigateTarget(null);
		        } else {
		            this.entityHost.getToadNavigator().setNavigateTarget(this.attackTarget.getPositionVector());
		        }		
		        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
		        if (--this.rangedAttackTime == 0) {
		            if (!flag) {
		                return;
		            }
		            float f = MathHelper.sqrt(d0) / this.attackRadius;
		            float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
		            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, lvt_5_1_);
		            this.rangedAttackTime = MathHelper.floor(f * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
		        } else if (this.rangedAttackTime < 0) {
		            float f2 = MathHelper.sqrt(d0) / this.attackRadius;
		            this.rangedAttackTime = MathHelper.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
		        }
		    }
		}

		static class MoveHelper extends EntityMoveHelper {
			public MoveHelper(EntityCustom entity) {
				super(entity);
			}

			@Override
			public void onUpdateMoveHelper() {
				if (this.isUpdating()) {
		            this.action = EntityMoveHelper.Action.WAIT;
		            double d0 = this.posX - this.entity.posX;
		            double d1 = this.posZ - this.entity.posZ;
		            double d2 = this.posY - this.entity.posY;
		            double d3 = d0 * d0 + d1 * d1;
		            if (d3 + d2 * d2 < 2.5E-7D) {
		                this.entity.setMoveForward(0.0F);
		                return;
		            }
		            double d = ProcedureUtils.getFollowRange(this.entity);
					d3 = MathHelper.sqrt(d3);
		            if (d3 > d) {
		            	double d4 = d / d3;
		            	d0 *= d4;
		            	d1 *= d4;
		            	d2 *= d4;
		            	d3 = d;
		            }
		            this.entity.rotationYaw = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
		            this.entity.motionX = d0 * 0.145d;
		            this.entity.motionZ = d1 * 0.145d;
		            this.entity.motionY = 0.32d + (Math.max(d2, 0.0d) + d3 * 0.6d) * 0.1d;
				}
			}
		}
	}

	public static abstract class RenderCustom<T extends EntityCustom> extends RenderLiving<T> {
		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn, new ModelToad(), 0.5f);
		}

		@Override
		public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
			ModelToad model = (ModelToad)this.mainModel;
			model.scale = entity.getScale();
			this.shadowSize = 0.5f * model.scale;
			model.jumpProgress = entity.getJumpProgress(partialTicks);
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

		@Override
		protected ResourceLocation getEntityTexture(T entity) {
			return null;
		}
	}

	// Made with Blockbench 4.2.1
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelToad extends ModelBase {
		private final ModelRenderer head;
		private final ModelRenderer bone4;
		private final ModelRenderer bone;
		private final ModelRenderer bone2;
		private final ModelRenderer head_r1;
		private final ModelRenderer jaw;
		private final ModelRenderer pipe;
		private final ModelRenderer body;
		private final ModelRenderer chest;
		private final ModelRenderer bone6;
		private final ModelRenderer bone11;
		private final ModelRenderer chest_r1;
		private final ModelRenderer bone5;
		private final ModelRenderer bunda;
		private final ModelRenderer bunda_r1;
		private final ModelRenderer armRight;
		private final ModelRenderer forearmRight;
		private final ModelRenderer handRight;
		private final ModelRenderer bone8;
		private final ModelRenderer bone9;
		private final ModelRenderer bone10;
		private final ModelRenderer blade;
		private final ModelRenderer armLeft;
		private final ModelRenderer bone14;
		private final ModelRenderer handLeft;
		private final ModelRenderer bone15;
		private final ModelRenderer bone16;
		private final ModelRenderer bone17;
		private final ModelRenderer legRight;
		private final ModelRenderer bone21;
		private final ModelRenderer legLowerRight;
		private final ModelRenderer legLowerRight3_r1;
		private final ModelRenderer footRight;
		private final ModelRenderer bone12;
		private final ModelRenderer bone3;
		private final ModelRenderer bone13;
		private final ModelRenderer legLeft;
		private final ModelRenderer bone18;
		private final ModelRenderer legLowerLeft;
		private final ModelRenderer legLowerRight4_r1;
		private final ModelRenderer footLeft;
		private final ModelRenderer bone19;
		private final ModelRenderer bone20;
		private final ModelRenderer bone22;
		private float jumpProgress;
		private float scale = 1.0f;

		public ModelToad() {
			textureWidth = 64;
			textureHeight = 64;

			head = new ModelRenderer(this);
			head.setRotationPoint(0.0F, 11.58F, -5.464F);
			head.cubeList.add(new ModelBox(head, 0, 20, -4.46F, -5.558F, -6.0708F, 9, 5, 8, 0.0F, false));
	
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(0.04F, -5.5151F, 1.8721F);
			head.addChild(bone4);
			setRotationAngle(bone4, -0.9163F, 0.0F, 0.0F);
			bone4.cubeList.add(new ModelBox(bone4, 0, 42, -4.5F, -0.0449F, 0.0F, 9, 3, 4, 0.0F, false));
	
			bone = new ModelRenderer(this);
			bone.setRotationPoint(-2.71F, -4.308F, -6.5708F);
			head.addChild(bone);
			setRotationAngle(bone, 0.0F, 0.0873F, 0.5672F);
			bone.cubeList.add(new ModelBox(bone, 13, 49, -2.29F, -0.5F, 0.25F, 4, 1, 5, 0.3F, false));
	
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(2.71F, -4.308F, -6.5708F);
			head.addChild(bone2);
			setRotationAngle(bone2, 0.0F, -0.0873F, -0.5672F);
			bone2.cubeList.add(new ModelBox(bone2, 13, 49, -1.71F, -0.5F, 0.25F, 4, 1, 5, 0.3F, true));
			bone2.cubeList.add(new ModelBox(bone2, 51, 35, -1.71F, -0.525F, 0.175F, 4, 1, 1, 0.3F, true));
	
			head_r1 = new ModelRenderer(this);
			head_r1.setRotationPoint(0.29F, -0.025F, 0.675F);
			bone2.addChild(head_r1);
			setRotationAngle(head_r1, 0.0436F, 0.0F, 3.1397F);
			head_r1.cubeList.add(new ModelBox(head_r1, 51, 35, -2.0F, -0.55F, -0.375F, 4, 1, 1, 0.3F, false));
	
			jaw = new ModelRenderer(this);
			jaw.setRotationPoint(0.04F, -0.5003F, -1.1917F);
			head.addChild(jaw);
			setRotationAngle(jaw, 0.0873F, 0.0F, 0.0F);
			jaw.cubeList.add(new ModelBox(jaw, 0, 33, -4.5F, -0.0901F, -4.8784F, 9, 2, 8, 0.0F, false));
	
			pipe = new ModelRenderer(this);
			pipe.setRotationPoint(4.1917F, -0.5133F, -4.8393F);
			head.addChild(pipe);
			setRotationAngle(pipe, 0.2618F, -0.8727F, 0.0F);
			pipe.cubeList.add(new ModelBox(pipe, 0, 4, -1.8662F, -2.0667F, -6.0098F, 2, 1, 2, 0.0F, false));
			pipe.cubeList.add(new ModelBox(pipe, 0, 0, -1.8662F, -0.8167F, -6.0098F, 2, 2, 2, 0.0F, false));
			pipe.cubeList.add(new ModelBox(pipe, 0, 7, -1.3662F, -1.3167F, -5.5098F, 1, 1, 1, 0.0F, false));
			pipe.cubeList.add(new ModelBox(pipe, 52, 52, -1.3662F, -0.3167F, -4.0098F, 1, 1, 5, 0.0F, false));
	
			body = new ModelRenderer(this);
			body.setRotationPoint(0.0F, 11.58F, -5.464F);
			
	
			chest = new ModelRenderer(this);
			chest.setRotationPoint(0.25F, 1.32F, 0.464F);
			body.addChild(chest);
			setRotationAngle(chest, -0.7854F, 0.0F, 0.0F);
			
	
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(-0.2F, -1.5397F, 3.6327F);
			chest.addChild(bone6);
			setRotationAngle(bone6, -0.0873F, 0.0F, 0.0F);
			bone6.cubeList.add(new ModelBox(bone6, 0, 0, -6.0F, -5.7157F, -2.0345F, 12, 11, 9, 0.0F, false));
	
			bone11 = new ModelRenderer(this);
			bone11.setRotationPoint(-0.2F, -1.5397F, 3.8827F);
			chest.addChild(bone11);
			
	
			chest_r1 = new ModelRenderer(this);
			chest_r1.setRotationPoint(0.0F, -5.9657F, -1.8845F);
			bone11.addChild(chest_r1);
			setRotationAngle(chest_r1, 0.2443F, 0.0F, 0.0F);
			chest_r1.cubeList.add(new ModelBox(chest_r1, 29, 28, -5.5F, 0.1254F, -4.9F, 11, 6, 5, 0.0F, false));
	
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(0.0F, 0.0343F, -5.6845F);
			bone11.addChild(bone5);
			setRotationAngle(bone5, 0.5323F, 0.0F, 0.0F);
			bone5.cubeList.add(new ModelBox(bone5, 33, 0, -5.5F, 0.0076F, -0.0243F, 11, 6, 3, 0.0F, false));
	
			bunda = new ModelRenderer(this);
			bunda.setRotationPoint(-0.242F, -6.0646F, 10.9442F);
			chest.addChild(bunda);
			setRotationAngle(bunda, -0.3927F, 0.0F, 0.0F);
			
	
			bunda_r1 = new ModelRenderer(this);
			bunda_r1.setRotationPoint(0.0F, 2.8378F, -0.9923F);
			bunda.addChild(bunda_r1);
			setRotationAngle(bunda_r1, -0.0873F, 0.0F, 0.0F);
			bunda_r1.cubeList.add(new ModelBox(bunda_r1, 30, 39, -5.5F, -3.3378F, 0.4923F, 11, 9, 4, -0.1F, false));
	
			armRight = new ModelRenderer(this);
			armRight.setRotationPoint(-5.05F, -0.68F, 2.554F);
			body.addChild(armRight);
			setRotationAngle(armRight, -0.5236F, 0.5236F, 0.3491F);
			armRight.cubeList.add(new ModelBox(armRight, 52, 9, -1.576F, -1.7778F, -1.4648F, 3, 8, 3, 0.2F, false));
	
			forearmRight = new ModelRenderer(this);
			forearmRight.setRotationPoint(-0.66F, 6.898F, -0.1F);
			armRight.addChild(forearmRight);
			setRotationAngle(forearmRight, 0.0F, 0.0F, -0.5236F);
			forearmRight.cubeList.add(new ModelBox(forearmRight, 40, 52, -0.712F, -0.9138F, -1.3328F, 3, 6, 3, 0.1F, false));
	
			handRight = new ModelRenderer(this);
			handRight.setRotationPoint(1.01F, 5.478F, 0.152F);
			forearmRight.addChild(handRight);
			setRotationAngle(handRight, 1.0472F, 0.2618F, 0.0F);
			
	
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(-1.1037F, -0.2173F, 0.496F);
			handRight.addChild(bone8);
			setRotationAngle(bone8, 0.0F, 0.3491F, 0.0F);
			bone8.cubeList.add(new ModelBox(bone8, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, false));
	
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(-0.1037F, -0.2173F, 0.496F);
			handRight.addChild(bone9);
			bone9.cubeList.add(new ModelBox(bone9, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, false));
	
			bone10 = new ModelRenderer(this);
			bone10.setRotationPoint(0.8963F, -0.2173F, 0.496F);
			handRight.addChild(bone10);
			setRotationAngle(bone10, 0.0F, -0.3491F, 0.0F);
			bone10.cubeList.add(new ModelBox(bone10, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, false));
	
			blade = new ModelRenderer(this);
			blade.setRotationPoint(-0.2497F, 0.1945F, -1.0331F);
			handRight.addChild(blade);
			setRotationAngle(blade, 0.7854F, 0.1745F, 0.0F);
			blade.cubeList.add(new ModelBox(blade, 0, 62, -4.25F, -0.5F, -0.5F, 8, 1, 1, 0.0F, false));
			blade.cubeList.add(new ModelBox(blade, 18, 61, 3.75F, 0.0F, -0.5F, 10, 0, 1, 0.02F, false));
			blade.cubeList.add(new ModelBox(blade, 18, 62, 3.5F, 0.0F, -0.5F, 1, 0, 1, 0.02F, false));
	
			armLeft = new ModelRenderer(this);
			armLeft.setRotationPoint(5.05F, -0.68F, 2.554F);
			body.addChild(armLeft);
			setRotationAngle(armLeft, -0.5236F, -0.5236F, -0.3491F);
			armLeft.cubeList.add(new ModelBox(armLeft, 52, 9, -1.424F, -1.7778F, -1.4648F, 3, 8, 3, 0.2F, true));
	
			bone14 = new ModelRenderer(this);
			bone14.setRotationPoint(0.66F, 6.898F, -0.1F);
			armLeft.addChild(bone14);
			setRotationAngle(bone14, 0.0F, 0.0F, 0.5236F);
			bone14.cubeList.add(new ModelBox(bone14, 40, 52, -2.288F, -0.9138F, -1.3328F, 3, 6, 3, 0.1F, true));
	
			handLeft = new ModelRenderer(this);
			handLeft.setRotationPoint(-1.26F, 6.728F, -0.848F);
			bone14.addChild(handLeft);
			setRotationAngle(handLeft, 1.0472F, -0.2618F, 0.0F);
			
	
			bone15 = new ModelRenderer(this);
			bone15.setRotationPoint(1.604F, -0.0618F, 2.0292F);
			handLeft.addChild(bone15);
			setRotationAngle(bone15, 0.0F, -0.3491F, 0.0F);
			bone15.cubeList.add(new ModelBox(bone15, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, true));
	
			bone16 = new ModelRenderer(this);
			bone16.setRotationPoint(0.604F, -0.0618F, 2.0292F);
			handLeft.addChild(bone16);
			bone16.cubeList.add(new ModelBox(bone16, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, true));
	
			bone17 = new ModelRenderer(this);
			bone17.setRotationPoint(-0.396F, -0.0618F, 2.0292F);
			handLeft.addChild(bone17);
			setRotationAngle(bone17, 0.0F, 0.3491F, 0.0F);
			bone17.cubeList.add(new ModelBox(bone17, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, true));
	
			legRight = new ModelRenderer(this);
			legRight.setRotationPoint(-5.677F, 19.8471F, 1.9223F);
			setRotationAngle(legRight, 0.2618F, 1.0472F, 0.0F);
			
	
			bone21 = new ModelRenderer(this);
			bone21.setRotationPoint(0.241F, 1.0282F, 0.8872F);
			legRight.addChild(bone21);
			setRotationAngle(bone21, -0.6981F, 0.0F, 0.0F);
			bone21.cubeList.add(new ModelBox(bone21, 32, 10, -2.901F, -1.6142F, -9.4876F, 5, 3, 10, 0.2F, false));
	
			legLowerRight = new ModelRenderer(this);
			legLowerRight.setRotationPoint(-0.0653F, -4.0517F, -5.8381F);
			legRight.addChild(legLowerRight);
			setRotationAngle(legLowerRight, -0.5236F, 0.0F, 0.0F);
			
	
			legLowerRight3_r1 = new ModelRenderer(this);
			legLowerRight3_r1.setRotationPoint(-0.1735F, 1.045F, -0.854F);
			legLowerRight.addChild(legLowerRight3_r1);
			setRotationAngle(legLowerRight3_r1, -0.7418F, 0.0F, 0.0F);
			legLowerRight3_r1.cubeList.add(new ModelBox(legLowerRight3_r1, 0, 49, -1.3772F, -3.0266F, -0.0999F, 3, 3, 7, 0.2F, false));
	
			footRight = new ModelRenderer(this);
			footRight.setRotationPoint(-0.0107F, 5.1235F, 4.6603F);
			legLowerRight.addChild(footRight);
			setRotationAngle(footRight, 0.2182F, 0.0F, 0.0F);
			
	
			bone12 = new ModelRenderer(this);
			bone12.setRotationPoint(-0.896F, -0.0341F, -0.0512F);
			footRight.addChild(bone12);
			setRotationAngle(bone12, 0.0F, 0.3491F, 0.0F);
			bone12.cubeList.add(new ModelBox(bone12, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));
	
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(0.104F, -0.0341F, -0.0512F);
			footRight.addChild(bone3);
			bone3.cubeList.add(new ModelBox(bone3, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));
	
			bone13 = new ModelRenderer(this);
			bone13.setRotationPoint(1.104F, -0.0341F, -0.0512F);
			footRight.addChild(bone13);
			setRotationAngle(bone13, 0.0F, -0.3491F, 0.0F);
			bone13.cubeList.add(new ModelBox(bone13, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));
	
			legLeft = new ModelRenderer(this);
			legLeft.setRotationPoint(5.677F, 19.8471F, 1.9223F);
			setRotationAngle(legLeft, 0.2618F, -1.0472F, 0.0F);
			
	
			bone18 = new ModelRenderer(this);
			bone18.setRotationPoint(-0.241F, 1.0282F, 0.8872F);
			legLeft.addChild(bone18);
			setRotationAngle(bone18, -0.6981F, 0.0F, 0.0F);
			bone18.cubeList.add(new ModelBox(bone18, 32, 10, -2.099F, -1.6142F, -9.4876F, 5, 3, 10, 0.2F, true));
	
			legLowerLeft = new ModelRenderer(this);
			legLowerLeft.setRotationPoint(0.0653F, -4.0517F, -5.8381F);
			legLeft.addChild(legLowerLeft);
			setRotationAngle(legLowerLeft, -0.5236F, 0.0F, 0.0F);
			
	
			legLowerRight4_r1 = new ModelRenderer(this);
			legLowerRight4_r1.setRotationPoint(0.1735F, 1.045F, -0.854F);
			legLowerLeft.addChild(legLowerRight4_r1);
			setRotationAngle(legLowerRight4_r1, -0.7418F, 0.0F, 0.0F);
			legLowerRight4_r1.cubeList.add(new ModelBox(legLowerRight4_r1, 0, 49, -1.6228F, -3.0266F, -0.0999F, 3, 3, 7, 0.2F, true));
	
			footLeft = new ModelRenderer(this);
			footLeft.setRotationPoint(0.0107F, 5.1235F, 4.6603F);
			legLowerLeft.addChild(footLeft);
			setRotationAngle(footLeft, 0.2182F, 0.0F, 0.0F);
			
	
			bone19 = new ModelRenderer(this);
			bone19.setRotationPoint(0.896F, -0.0341F, -0.0512F);
			footLeft.addChild(bone19);
			setRotationAngle(bone19, 0.0F, -0.3491F, 0.0F);
			bone19.cubeList.add(new ModelBox(bone19, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
	
			bone20 = new ModelRenderer(this);
			bone20.setRotationPoint(-0.104F, -0.0341F, -0.0512F);
			footLeft.addChild(bone20);
			bone20.cubeList.add(new ModelBox(bone20, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
	
			bone22 = new ModelRenderer(this);
			bone22.setRotationPoint(-1.104F, -0.0341F, -0.0512F);
			footLeft.addChild(bone22);
			setRotationAngle(bone22, 0.0F, 0.3491F, 0.0F);
			bone22.cubeList.add(new ModelBox(bone22, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
		}

		public void showPipe(boolean show) {
			this.pipe.showModel = show;
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 1.5F - 1.5F * this.scale, 0.0F);
			GlStateManager.scale(this.scale, this.scale, this.scale);
			head.render(f5);
			body.render(f5);
			legLeft.render(f5);
			legRight.render(f5);
			GlStateManager.popMatrix();
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		@Override
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
			this.head.rotateAngleY = f3 / (180F / (float) Math.PI);
			this.head.rotateAngleX = f4 / (180F / (float) Math.PI);
			this.jaw.rotateAngleX = ((EntityCustom)e).getMouthOpen() ? 0.5236F : 0.0873F;
			this.body.rotateAngleY = 0.0F;
			this.armRight.rotateAngleX = -0.5236F;
			this.armRight.rotateAngleZ = 0.3491F;
			this.forearmRight.rotateAngleZ = -0.5236F;
			this.handRight.rotateAngleZ = 0.0F;
			if (this.swingProgress > 0.0F) {
				this.body.rotateAngleY = MathHelper.sin(MathHelper.sqrt(this.swingProgress) * ((float)Math.PI * 2F)) * 0.1F;
				if (this.swingProgress < 0.3333F) {
					float f6 = this.swingProgress / 0.3333F;
					this.armRight.rotateAngleX = -0.5236F - 1.5708F * f6;
					this.armRight.rotateAngleZ = 0.3491F - 0.8727F * f6;
				} else if (this.swingProgress < 0.6667F) {
					float f6 = (this.swingProgress - 0.3333F) / 0.3333F;
					this.armRight.rotateAngleX = -2.0944F + 1.5708F * f6;
					this.armRight.rotateAngleZ = -0.5236F + 0.5236F * f6;
					this.forearmRight.rotateAngleZ = -0.5236F + 0.2618F * f6;
					this.handRight.rotateAngleZ = 0.5236F * f6;
				} else {
					float f6 = (this.swingProgress - 0.6667F) / 0.3333F;
					this.armRight.rotateAngleZ = 0.3491F * f6;
					this.forearmRight.rotateAngleZ = -0.2618F - 0.2618F * f6;
					this.handRight.rotateAngleZ = 0.5236F - 0.5236F * f6;
				}
			}
	        this.armRight.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
	        this.armLeft.rotateAngleZ = -0.3491F - MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
	        this.armRight.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
	        this.armLeft.rotateAngleX = -0.5236F - MathHelper.sin(f2 * 0.067F) * 0.05F;
			if (!e.onGround) {
				this.legRight.rotateAngleX = 0.2618F + this.jumpProgress * 1.5708F;
				this.legLowerRight.rotateAngleX = -0.5236F - this.jumpProgress * 1.5708F;
				this.footRight.rotateAngleX = 0.2182F + this.jumpProgress * 1.0908F;
				this.legLeft.rotateAngleX = 0.2618F + this.jumpProgress * 1.5708F;
				this.legLowerLeft.rotateAngleX = -0.5236F - this.jumpProgress * 1.5708F;
				this.footLeft.rotateAngleX = 0.2182F + this.jumpProgress * 1.0908F;
			} else {
				this.legRight.rotateAngleX = 0.2618F;
				this.legLowerRight.rotateAngleX = -0.5236F;
				this.footRight.rotateAngleX = 0.2182F;
				this.legLeft.rotateAngleX = 0.2618F;
				this.legLowerLeft.rotateAngleX = -0.5236F;
				this.footLeft.rotateAngleX = 0.2182F;
			}
		}
	}
}
