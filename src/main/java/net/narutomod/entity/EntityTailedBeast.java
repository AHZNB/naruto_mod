
package net.narutomod.entity;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.item.Item;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.block.material.Material;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.event.EventSphericalExplosion;
import net.narutomod.Particles;
import net.narutomod.SaveData;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTailedBeast extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 251;
	public static final int ENTITYID_RANGED = 252;

	public EntityTailedBeast(ElementsNarutomodMod instance) {
		super(instance, 579);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityTailBeastBall.class)
		  .id(new ResourceLocation("narutomod", "tailbeastball"), ENTITYID_RANGED).name("tailbeastball")
		  .tracker(128, 1, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerHooks());
	}

	public static abstract class Base extends EntityMob implements IRangedAttackMob, EntityBijuManager.ITailBeast, ICollisionData {
		private static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> VESSEL = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Boolean> SHOOT = EntityDataManager.<Boolean>createKey(Base.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Boolean> CANSTEER = EntityDataManager.<Boolean>createKey(Base.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Boolean> FACEDOWN = EntityDataManager.<Boolean>createKey(Base.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Float> TRANSPARENCY = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		protected final List<Material> canBreakList = Lists.newArrayList(Material.WOOD, Material.CACTUS, Material.ICE,
		 Material.GLASS, Material.LEAVES, Material.PLANTS, Material.SNOW, Material.VINE, Material.WEB, Material.CARPET);
		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
		public static final int ATTACK_CD_MIN = 20;
		public static final int ATTACK_CD_MAX = 100;
		//public static final int BIJUDAMA_CD = 100;
		private final double targetRange = 112.0D;
		private final double bijudamaMinRange = 64.0D;
		private int deathTicks;
		private int deathTotalTicks;
		private EntityPlayer summoningPlayer;
		private int tailBeastBallTime = ATTACK_CD_MAX;
		private int angerLevel;
		private int lifeSpan = Integer.MAX_VALUE - 1;
		protected boolean canPassengerDismount = true;
		protected boolean spawnedBySpawner;
		protected final ProcedureUtils.CollisionHelper collisionData;
		protected Entity mouthShootingJutsu;
		private int meleeTime;

		public Base(World world) {
			super(world);
			this.collisionData = new ProcedureUtils.CollisionHelper(this);
			this.isImmuneToFire = true;
			//this.stepHeight = this.height / 3.0F;
			this.deathTicks = 0;
			this.setNoAI(false);
			this.enablePersistence();
			this.setHealth(this.getMaxHealth());
			this.deathTotalTicks = 200;
			if (!this.world.isRemote && this.getBijuManager().hasSpawnPos()) {
				this.setHomePosAndDistance(this.getBijuManager().getSpawnPos(), 128);
			}
		}

		public Base(EntityPlayer player) {
			this(player.world);
			if (player.equals(this.getBijuManager().getJinchurikiPlayer())) {
				this.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
				player.startRiding(this);
			} else {
				Vec3d vec = player.getPositionEyes(1f).add(player.getLookVec().scale(20d));
				this.setLocationAndAngles(vec.x, player.posY + 10d, vec.z, player.rotationYaw - 180f, 0f);
				this.setSummoningPlayer(player);
			}
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			PathNavigateGround navi = new NavigateGround(this, worldIn);
			this.moveHelper = new MoveHelper(this);
			return navi;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(AGE, Integer.valueOf(0));
			this.getDataManager().register(VESSEL, Integer.valueOf(-1));
			this.getDataManager().register(SHOOT, Boolean.valueOf(false));
			this.getDataManager().register(CANSTEER, Boolean.valueOf(false));
			this.getDataManager().register(FACEDOWN, Boolean.valueOf(false));
			this.getDataManager().register(TRANSPARENCY, Float.valueOf(1.0f));
		}

		public int getAge() {
			return ((Integer) this.getDataManager().get(AGE)).intValue();
		}

		protected void setAge(int age) {
			this.getDataManager().set(AGE, Integer.valueOf(age));
		}

		public void setLifeSpan(int lifespan) {
			this.lifeSpan = lifespan;
		}

		protected boolean isFaceDown() {
			return ((Boolean)this.getDataManager().get(FACEDOWN)).booleanValue();
		}

		public void setFaceDown(boolean down) {
			if (!this.world.isRemote) {
				this.getDataManager().set(FACEDOWN, Boolean.valueOf(down));
			}
		}

		public abstract float getModelScale();

		@Nullable
		protected Entity getTargetVessel() {
			return this.world.getEntityByID(((Integer)this.getDataManager().get(VESSEL)).intValue());
		}

		private void setTargetVessel(@Nullable Entity player) {
			this.getDataManager().set(VESSEL, Integer.valueOf(player != null ? player.getEntityId() : -1));
		}

		public float getTransparency() {
			return ((Float)this.getDataManager().get(TRANSPARENCY)).floatValue();
		}

		protected void setTransparency(float transparency) {
			this.getDataManager().set(TRANSPARENCY, Float.valueOf(transparency));
		}

		//@Override
		//public void notifyDataManagerChange(DataParameter<?> key) {
		//	super.notifyDataManagerChange(key);
		//	if (FACEDOWN.equals(key) && this.world.isRemote) {
		//		this.setFaceDown(this.isFaceDown());
		//	}
		//}

		public void setAngerLevel(int i) {
			this.angerLevel = MathHelper.clamp(i, 0, 2);
		}

		public abstract EntityBijuManager getBijuManager();

		public void setSummoningPlayer(EntityPlayer player) {
			this.summoningPlayer = player;
		}

		public EntityPlayer getSummoningPlayer() {
			return this.summoningPlayer;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(ProcedureUtils.MAXHEALTH);
			this.getAttributeMap().registerAttribute(EntityPlayer.REACH_DISTANCE);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(this.getTargetRange());
			this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(200.0D);
		}

		@Override
		public IAttributeInstance getEntityAttribute(IAttribute attribute) {
			return super.getEntityAttribute(attribute == SharedMonsterAttributes.MAX_HEALTH ? ProcedureUtils.MAXHEALTH : attribute);
		}

		public double getBijudamaMinRange() {
			return this.bijudamaMinRange;
		}

		public double getTargetRange() {
			return this.targetRange;
		}

		protected void setAttackTasks() {
			this.tasks.addTask(0, new EntityAIAttackRanged(this, 1.2D, ATTACK_CD_MIN, ATTACK_CD_MAX, (float)this.getBijudamaMinRange()) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !Base.this.isMotionHalted() && Base.this.meleeTime <= 0;
				}
				@Override
				public boolean shouldContinueExecuting() {
					return super.shouldContinueExecuting() && !Base.this.isMotionHalted() && Base.this.rand.nextFloat() > 0.004f;
				}
				@Override
				public void resetTask() {
					super.resetTask();
					Base.this.meleeTime = 100;
				}
			});
			this.tasks.addTask(1, new AILeapAtTarget(this, 36.0d) {
				@Override
				public boolean shouldExecute() {
					return !Base.this.isMotionHalted() && super.shouldExecute();
				}
			});
			this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.2D, true) {
				@Override
				public boolean shouldExecute() {
					return !Base.this.isMotionHalted() && super.shouldExecute();
				}
				@Override
				public boolean shouldContinueExecuting() {
					return super.shouldContinueExecuting() && !Base.this.isMotionHalted() && Base.this.meleeTime > 0;
				}
				@Override
				protected double getAttackReachSqr(EntityLivingBase attackTarget) {
					return ProcedureUtils.getReachDistanceSq(this.attacker) * 0.36d;
				}
			});
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false) {
				@Override
				protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean includeInvincibles) {
					return super.isSuitableTarget(target, includeInvincibles) && Base.this.canTargetEntity(target);
				}
			});
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true, false) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && Base.this.canTargetEntity(this.targetEntity) && Base.this.angerLevel > 0;
				}
			});
			this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, true, false) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && Base.this.angerLevel > 1 && Base.this.canTargetEntity(this.targetEntity);
				}
				@Override
				public boolean shouldContinueExecuting() {
					return super.shouldContinueExecuting() && Base.this.canTargetEntity(this.targetEntity);
				}
				@Override
				protected double getTargetDistance() {
					return Base.this.getTargetRange() * 0.5d;
				}
			});
			this.setAttackTasks();
			this.tasks.addTask(4, new EntityAIWander(this, 0.8D) {
				@Override
				public boolean shouldExecute() {
					if (Base.this.isMotionHalted() || Base.this.getAttackTarget() != null) {
						return false;
					}
					if (Base.this.isInWater() && Base.this.hatesWater() && Base.this.getNavigator().noPath()) {
						this.makeUpdate();
					}
					return super.shouldExecute();
				}
				@Override @Nullable
				protected Vec3d getPosition() {
					if (Base.this.hatesWater()) {
						for (int i = 0; i < Base.this.getTargetRange() - 56; i += 16) {
							Vec3d vec = RandomPositionGenerator.getLandPos(this.entity, 56 + i, 35);
							if (vec != null) {
								return vec;
							}
						}
					}
					return RandomPositionGenerator.findRandomTarget(this.entity, 56, 35);
				}
			});
			this.tasks.addTask(5, new EntityAILookIdle(this) {
				@Override
				public boolean shouldExecute() {
					return !Base.this.isMotionHalted() && super.shouldExecute();
				}
			});
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			EntityLivingBase target = this.getAttackTarget();
			if (target != null && !target.isEntityAlive()) {
				this.setAttackTarget(null);
				target = null;
			}
			if (this.meleeTime > 0) {
				--this.meleeTime;
			} else if (target != null && this.getDistance(target) <= ProcedureUtils.getReachDistance(this) * 0.6d) {
				this.meleeTime = 80;
			}
		}

		protected void setMeleeTime(int ticks) {
			this.meleeTime = ticks;
		}

		protected int getMeleeTime() {
			return this.meleeTime;
		}

		protected boolean isMotionHalted() {
			return this.isFaceDown();
		}

		protected boolean hatesWater() {
			return true;
		}

		@Override
		protected float getWaterSlowDown() {
			return 0.96F;
		}

		protected boolean canTargetEntity(Entity target) {
			return !this.isMotionHalted() && !this.isOnSameTeam(target) && (!this.hatesWater() || !target.isInWater() || !this.isInWater());
		}

		@Override
		protected boolean canDespawn() {
			return false;
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return (entityIn.equals(this.getBijuManager().getJinchurikiPlayer()) || entityIn.equals(this.summoningPlayer));
		}

		@Override
		protected float getSoundVolume() {
			return 50.0F;
		}

		@Override
		public int getTalkInterval() {
			return 320 - this.angerLevel * 120;
		}

		@Override
		public boolean canBePushed() {
			return false;
		}

		@Override
		protected void collideWithEntity(Entity entityIn) {
			this.applyEntityCollision(entityIn);
		}

		@Override
		public void applyEntityCollision(Entity entity) {
			if (!this.isRidingSameEntity(entity) && !entity.noClip && !entity.isBeingRidden()) {
				double d2 = entity.posX - this.posX;
				double d3 = entity.posZ - this.posZ;
				double d4 = MathHelper.absMax(d2, d3);
				if (d4 >= 0.01D) {
					d4 = (double)MathHelper.sqrt(d4);
					d2 /= d4;
					d3 /= d4;
					double d5 = d4 >= 1.0D ? 1.0D / d4 : 1.0D;
					d2 *= d5 * 0.05d;
					d3 *= d5 * 0.05d;
					entity.motionX = d2;
					entity.motionZ = d3;
					entity.isAirBorne = true;
                }
			}
		}

		public Vec3d getPositionMouth() {
			return Vec3d.fromPitchYaw(this.rotationPitch, this.rotationYawHead)
			 .scale(this.width * 1.5f).addVector(this.posX, this.posY + this.getEyeHeight(), this.posZ);
		}

		public float getFuuinBeamHeight() {
			return 0.5833f * this.height;
		}

		@Override
		public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			super.processInteract(entity, hand);
			if (!this.world.isRemote && (entity.equals(this.getBijuManager().getJinchurikiPlayer()) || entity.equals(this.summoningPlayer))) {
				entity.startRiding(this);
				return true;
			}
			return false;
		}

		@Override
		public double getMountedYOffset() {
			return this.height + 0.35D - (this.getControllingPassenger().equals(this.getBijuManager().getJinchurikiPlayer()) ? 3.0D : 0.0D);
		}

		@Override
		public boolean shouldRiderSit() {
			return false;
		}

		private void setCanSteer(boolean b) {
			this.getDataManager().set(CANSTEER, Boolean.valueOf(b));
		}

		@Override
		public boolean canBeSteered() {
			//return this.isBeingRidden() && this.getControllingPassenger().equals(this.getBijuManager().getJinchurikiPlayer());
			return ((Boolean)this.getDataManager().get(CANSTEER)).booleanValue();
		}

		@Override
		protected boolean canBeRidden(Entity entityIn) {
			return entityIn.equals(this.getBijuManager().getJinchurikiPlayer()) || entityIn.equals(this.summoningPlayer)
					|| (entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).isCreative());
		}

		@Override
		protected boolean canFitPassenger(Entity passenger) {
			return super.canFitPassenger(passenger) && this.canBeRidden(passenger);
		}

		@Override
		protected void addPassenger(Entity passenger) {
			super.addPassenger(passenger);
			if (!this.world.isRemote) {
				if (passenger.equals(this.getBijuManager().getJinchurikiPlayer())) {
					this.setNoAI(true);
					this.setCanSteer(true);
					this.canPassengerDismount = false;
				} else {
					this.setCanSteer(false);
				}
			}
		}

		@Override
		public Entity getControllingPassenger() {
			return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
		}

		@Override
		public boolean shouldDismountInWater(Entity rider) {
			return false;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (this.getHealth() <= 0.0f)
				return false;
			if (source.getTrueSource() instanceof EntityPlayer && source.getTrueSource().equals(this.getControllingPassenger()))
				return false;
			if (source.getImmediateSource() instanceof net.minecraft.entity.projectile.EntityPotion)
				return false;
			if (source == DamageSource.FALL)
				return false;
			if (source == DamageSource.CACTUS)
				return false;
			if (source == DamageSource.DROWN)
				return false;
			if (source == DamageSource.LIGHTNING_BOLT)
				return false;
			if (this.equals(source.getTrueSource())) {
				return false;
			}
			if (!source.isUnblockable() && source.getTrueSource() != null) {
				Vec3d vec3d = source.getDamageLocation();
				if (vec3d != null) {
					Vec3d vec3d1 = this.getLook(1.0F);
					Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(this.posX, this.posY, this.posZ)).normalize();
					vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);
					if (vec3d2.dotProduct(vec3d1) < 0.0D) {
						amount *= this.rand.nextFloat();
					}
				}
			}
			float hp = this.getHealth();
			if (source.getTrueSource() instanceof EntityLivingBase) {
				float maxhp = this.getMaxHealth();
				this.setAngerLevel(Math.max(source.getTrueSource() instanceof EntityPlayer ? 1 : hp < 0.5f * maxhp ? 2 : hp < maxhp - 500f ? 1 : 0, this.angerLevel));
			}
			//return super.attackEntityFrom(source, amount);
			boolean flag = super.attackEntityFrom(source, amount);
			EntityPlayer jin = this.getBijuManager().getJinchurikiPlayer();
			if (flag && jin != null && !this.isEntityAlive()) {
				jin.attackEntityFrom(source, CombatRules.getDamageAfterAbsorb(amount, (float)this.getTotalArmorValue(), 0f) - hp);
			}
			return flag;
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			if (super.attackEntityAsMob(entityIn)) {
				this.world.createExplosion(this, entityIn.posX, entityIn.posY, entityIn.posZ, 10f,
				 net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this));
				ProcedureAoeCommand.set(entityIn, 0d, 5d).exclude(this).exclude(this.getSummoningPlayer())
				 .exclude(this.getBijuManager().getJinchurikiPlayer()).exclude(this.getControllingPassenger())
				 .damageEntities(this, (float)ProcedureUtils.getModifiedAttackDamage(this) * (this.rand.nextFloat() * 0.5f + 0.75f));
				return true;
			}
			return false;
		}

		public boolean couldBreakBlocks() {
			return this.world.getGameRules().getBoolean("mobGriefing");
		}

		@Override
		public ProcedureUtils.CollisionHelper getCollisionData() {
			return this.collisionData;
		}

		@Override
		public void move(MoverType type, double x, double y, double z) {
			this.collisionData.collideWithAABBs(x, y, z);
			if (this.couldBreakBlocks()) {
				for (BlockPos pos : this.collisionData.getHitBlocks()) {
					if (this.canBreakList.contains(this.world.getBlockState(pos).getMaterial())) {
						this.world.destroyBlock(pos, this.rand.nextFloat() < 0.2f);
						x *= 0.96d;
						y *= 0.96d;
						z *= 0.96d;
					}
				}
			}
			super.move(type, x, y, z);
		}

		@Override
		public void travel(float ti, float tj, float tk) {
			if (this.isBeingRidden() && this.canBeSteered()) {
				Entity entity = this.getControllingPassenger();
				this.rotationYaw = entity.rotationYaw;
				this.prevRotationYaw = this.rotationYaw;
				this.rotationPitch = entity.rotationPitch;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.jumpMovementFactor = this.getAIMoveSpeed() * 0.15F;
				this.renderYawOffset = entity.rotationYaw;
				this.rotationYawHead = entity.rotationYaw;
				this.stepHeight = this.height / 3.0F;
				if (entity instanceof EntityLivingBase) {
					this.setAIMoveSpeed((float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
					float forward = ((EntityLivingBase) entity).moveForward;
					float strafe = ((EntityLivingBase) entity).moveStrafing;
					super.travel(strafe, 0.0F, forward);
				}
			} else {
				this.jumpMovementFactor = 0.02F;
				super.travel(ti, tj, tk);
			}
		}

		public boolean canBeSealed() {
			return !this.getBijuManager().isSealed();
		}

		@Override
		public void fuuinIntoVessel(Entity vessel, int fuuinTime) {
			if (this.canBeSealed() && this.getHealth() < this.getMaxHealth() * 0.1f
			 && (!(vessel instanceof EntityPlayer) || !EntityBijuManager.isJinchuriki((EntityPlayer)vessel))) {
				if (!vessel.equals(this.getTargetVessel())) {
					this.deathTicks = 0;
				}
				this.setTargetVessel(vessel);
				this.setHealth(0.0F);
				this.deathTotalTicks = fuuinTime;
			}
			if (this.angerLevel == 0) {
				this.setAngerLevel(1);
			}
		}

		@Override
		public boolean isFuuinInProgress() {
			return this.getTargetVessel() != null && this.deathTicks > 0;
		}

		@Override
		public void cancelFuuin() {
			this.setHealth(this.getMaxHealth() * 0.05f);
			this.setTargetVessel(null);
			this.deathTicks = 0;
			this.deathTotalTicks = 200;
		}

		@Override
		public void incFuuinProgress(int i) {
			if (this.getTargetVessel() != null) {
				this.deathTicks += i;
			}
		}

		@Override
		public float getFuuinProgress() {
			return this.isFuuinInProgress() ? (float)this.deathTicks / (float)this.deathTotalTicks : 0.0f;
		}

		@Override
		protected void onDeathUpdate() {
			this.deathTicks++;
			if (!this.world.isRemote) {
				Entity jinchuriki = this.getTargetVessel();
				if (jinchuriki != null) {
					if (this.getBijuManager().isSealed() && this.deathTicks == 1) {
						jinchuriki = null;
						this.setTargetVessel(null);
						this.deathTotalTicks = 200;
					}
				}
				if (jinchuriki != null && this.ticksExisted % 50 == 1) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:KamuiSFX")), 3.0F, 1.0F);
				}
				for (int i = 0; i < (int)(((float)this.deathTicks / this.deathTotalTicks) * 100f); i++) {
					Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY + this.height * 0.5, this.posZ,
					  1, this.width * 0.5D, this.height * 0.5D, this.width * 0.5D, 0d, 0.5d, 0d, 96d, 0x10B00000, 100);
				}
				if (this.deathTicks > this.deathTotalTicks) {
					if (jinchuriki != null) {
						this.getBijuManager().setVesselEntity(jinchuriki);
						ProcedureUtils.sendChatAll(I18n.translateToLocalFormatted("chattext.tentails.sealedintoplayer", this.getName(), jinchuriki.getName()));
					}
					this.setDead();
				}
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				if (this.getBijuManager().getCloakLevel() == 3) {
					this.getBijuManager().toggleBijuCloak();
				}
				this.getBijuManager().onRemovedFromWorld(this);
			}
		}

		private void clampMotion(double d) {
			if (Math.abs(this.motionX) > d)
				this.motionX = (this.motionX > 0.0D) ? d : -d;
			if (Math.abs(this.motionY) > d)
				this.motionY = (this.motionY > 0.0D) ? d : -d;
			if (Math.abs(this.motionZ) > d)
				this.motionZ = (this.motionZ > 0.0D) ? d : -d;
		}

		@Override
		public void onEntityUpdate() {
			int age = this.getAge() + 1;
			this.setAge(age);
			EntityPlayer jinchuriki = this.getBijuManager().getJinchurikiPlayer();
			if (this.isBeingRidden() && !this.canPassengerDismount) {
			 //&& this.getControllingPassenger().equals(jinchuriki)) {
				this.getControllingPassenger().setSneaking(false);
			}
			super.onEntityUpdate();
			if (this.deathTicks > 0) {
				return;
			}
			if (!this.world.isRemote) {
				if ((jinchuriki instanceof EntityPlayerMP && ((EntityPlayerMP)jinchuriki).hasDisconnected())
				 || age > this.lifeSpan) {
					this.setDead();
				}
				float hp = this.getHealth();
				float maxhp = this.getMaxHealth();
				if (hp > maxhp * 0.1f && this.isFaceDown()) {
					this.setFaceDown(false);
				} else if (hp <= maxhp * 0.1f && !this.isFaceDown()) {
					this.setFaceDown(true);
				}
				if (this.isAIDisabled() && jinchuriki != null && jinchuriki.getHealth() <= 0.0F) {
					this.setNoAI(false);
				}
				if (this.ticksExisted % 100 == 0 && hp > 0.0f && hp < maxhp) {
					this.setHealth(hp + 0.01f * Math.max(hp, maxhp * 0.1f));
				}
				if (this.angerLevel > 0 && this.ticksExisted - this.getRevengeTimer() > 6000) {
					this.setAngerLevel(0);
				}
			}
			if (this.isBeingRidden()) {
				this.clampMotion(0.05D);
			}
			if (this.tailBeastBallTime > 0) {
				--this.tailBeastBallTime;
			}
		}

		protected boolean isShooting() {
			return ((Boolean)this.getDataManager().get(SHOOT)).booleanValue();
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
			this.getDataManager().set(SHOOT, Boolean.valueOf(swingingArms));
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			boolean flag = this.isAIDisabled();
			if (flag && this.tailBeastBallTime > 0) {
				if (target instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) target;
					player.sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", this.tailBeastBallTime / 20), true);
				}
				return;
			}
			if ((this.mouthShootingJutsu == null || this.mouthShootingJutsu.isDead) && (flag || (distanceFactor >= 1.0f && this.canShootBijudama()))) {
				this.mouthShootingJutsu = EntityTailBeastBall.spawn(this, 14f, 1000f);
				if (this.mouthShootingJutsu != null) {
					this.setSwingingArms(true);
					this.tailBeastBallTime = ATTACK_CD_MAX;
				}
			} else if (!flag && distanceFactor <= ProcedureUtils.getReachDistance(this) * 0.6d / this.getBijudamaMinRange()) {
				this.swingArm(EnumHand.MAIN_HAND);
				this.attackEntityAsMob(target);
			}
		}

		protected boolean consumeHealthAsChakra(float amount) {
			float f = this.getHealth();
			if (this.canShootBijudama() && f > amount) {
				this.setHealth(f - amount);
				return true;
			}
			return false;
		}

		protected boolean canShootBijudama() {
			return this.getHealth() >= this.getMaxHealth() * 0.3f;
		}

		@Override
		public Vec3d getLookVec() {
			return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead); 
		}

		@Override
		public boolean isNonBoss() {
			return false;
		}

		protected void enableBoss(boolean enable) {
			this.bossInfo.setVisible(enable);
		}

		@Override
		public void addTrackingPlayer(EntityPlayerMP player) {
			super.addTrackingPlayer(player);
			if (this.getBijuManager().getJinchurikiPlayer() == null) {
				this.bossInfo.addPlayer(player);
			}
		}

		@Override
		public void removeTrackingPlayer(EntityPlayerMP player) {
			super.removeTrackingPlayer(player);
			this.bossInfo.removePlayer(player);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setAge(compound.getInteger("age"));
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setInteger("age", this.getAge());
		}

		@Override
		public void onAddedToWorld() {
			super.onAddedToWorld();
			if (!this.world.isRemote) {
				this.getBijuManager().onAddedToWorld(this);
			}
		}

		@Override
		@Nullable
    	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
    		this.spawnedBySpawner = true;
    		return livingdata;
    	}
	}

	public static class EntityTailBeastBall extends EntityScalableProjectile.Base {
		private static final DataParameter<Integer> SHOOTER = EntityDataManager.<Integer>createKey(EntityTailBeastBall.class, DataSerializers.VARINT);
		private final int buildupTime = 100;
		private float maxScale;
		private float maxDamage;
		private boolean shooterAIDisabled;

		public EntityTailBeastBall(World worldIn) {
			super(worldIn);
			this.setOGSize(0.3125F, 0.3125F);
			this.setEntityScale(0.01f);
			this.setWaterSlowdown(0.98f);
		}

		public EntityTailBeastBall(EntityLivingBase shooter, float maxscale, float maxdamage) {
			super(shooter);
			this.setShooter(shooter);
			this.setOGSize(0.3125F, 0.3125F);
			if (shooter instanceof EntityLiving) {
				this.shooterAIDisabled = ((EntityLiving)shooter).isAIDisabled();
				((EntityLiving)shooter).setNoAI(true);
			}
			this.setBuildupPosition();
			this.setEntityScale(0.01f);
			this.setWaterSlowdown(0.98f);
			this.maxScale = maxscale;
			this.maxDamage = maxdamage;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SHOOTER, Integer.valueOf(-1));
		}

		@Nullable
		protected Entity getShooter() {
			return this.world.getEntityByID(((Integer)this.getDataManager().get(SHOOTER)).intValue());
		}

		private void setShooter(@Nullable Entity player) {
			this.getDataManager().set(SHOOTER, Integer.valueOf(player != null ? player.getEntityId() : -1));
		}

		private void setBuildupPosition() {
			Vec3d vec3d = this.shootingEntity instanceof Base ? ((Base)this.shootingEntity).getPositionMouth()
			 : Vec3d.fromPitchYaw(this.shootingEntity.rotationPitch, this.shootingEntity.rotationYawHead)
			 .scale(this.shootingEntity.width * 1.5).add(this.shootingEntity.getPositionEyes(1.0f));
			this.setPosition(vec3d.x, vec3d.y, vec3d.z);
		}

		private void closeMouth() {
			if (this.shootingEntity instanceof Base) {
				((Base)this.shootingEntity).setSwingingArms(false);
				((Base)this.shootingEntity).mouthShootingJutsu = null;
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null && !this.isDead) {
				if (this.ticksAlive <= this.buildupTime) {
					if (this.ticksAlive == 1) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:bijudama")), 10f, 1f);
					}
					this.setBuildupPosition();
					this.setEntityScale(this.maxScale * (float)this.ticksAlive / this.buildupTime);
					if (this.ticksAlive <= this.buildupTime - 40) {
						Particles.spawnParticle(this.world, Particles.Types.HOMING_ORB, this.posX, this.posY + this.maxScale * 0.15625f, this.posZ,
						  2, 0d, 0d, 0d, 0d, 0d, 0d, MathHelper.ceil(this.maxScale * 0.35f), (int)(this.maxScale * 2.2f));
					}
				} else if (this.shootingEntity instanceof EntityLiving && !this.shooterAIDisabled) {
					EntityLiving living = (EntityLiving)this.shootingEntity;
					if (living.isAIDisabled()) {
						living.setNoAI(false);
					}
					if (!this.isLaunched()) {
						if (living.getAttackTarget() != null) {
							this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:nagiharai")), 10f, 1f);
							this.shoot(living.getAttackTarget().posX - this.posX, living.getAttackTarget().posY - this.posY - (this.height / 2.0F), 
							  living.getAttackTarget().posZ - this.posZ, 1.05F, 0.0F);
						} else {
							this.setDead();
						}
						this.closeMouth();
					}
				} else if (!this.isLaunched()) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:nagiharai")), 10f, 1f);
					Vec3d vec = this.shootingEntity.getLookVec();
					this.shoot(vec.x, vec.y, vec.z, 1.05F, 0.0F);
					this.closeMouth();
				}
			}
			if (!this.world.isRemote && this.ticksAlive > this.buildupTime + 100) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && result.entityHit.equals(this.shootingEntity))
				return;
			if (!this.world.isRemote) {
				Entity excludePlayer = this.shootingEntity instanceof Base 
				 ? ((Base)this.shootingEntity).getBijuManager().getJinchurikiPlayer() != null
				   ? ((Base)this.shootingEntity).getBijuManager().getJinchurikiPlayer() 
				   : ((Base)this.shootingEntity).getSummoningPlayer() 
				 : this.shootingEntity;
				if (excludePlayer != null) {
					excludePlayer.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 40.0D);
				}
				float radius = MathHelper.sqrt(this.getEntityScale()) * 6f;
				new EventSphericalExplosion(this.world, this.shootingEntity, (int) this.posX, (int) this.posY,
				 (int) this.posZ, (int)radius, 0, 0.33f);
				ProcedureAoeCommand.set(this, 0d, radius * 1.2).exclude(excludePlayer)
				 .damageEntitiesCentered(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), this.maxDamage);
				this.setDead();
			}
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void renderParticles() {
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.maxDamage = compound.getFloat("maxDamage");
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setFloat("maxDamage", this.maxDamage);
		}

		@Nullable
		public static EntityTailBeastBall spawn(EntityLivingBase summonerIn, float maxscale, float maxdamage) {
			double chakraUsage = 100d * maxscale;
			EntityLivingBase user = null;
			if (summonerIn instanceof Base) {
				user = ((Base)summonerIn).getBijuManager().getJinchurikiPlayer();
			}
			if (user == null) {
				user = summonerIn;
			}
			if ((user instanceof Base && ((Base)user).consumeHealthAsChakra((float)chakraUsage * 0.1f))
			 || (user != null && net.narutomod.Chakra.pathway(user).consume(chakraUsage))) {
				EntityTailBeastBall entity = new EntityTailBeastBall(summonerIn, maxscale, maxdamage);
				summonerIn.world.spawnEntity(entity);
				return entity;
			}
			return null;
		}

		protected static class CDTracker {
			private static final Map<EntityLivingBase, CDTracker> cdMap = Maps.newHashMap();
			int cooldown;
			float power;

			CDTracker() {
				this(0, 0f);
			}

			CDTracker(int cd, float p) {
				this.cooldown = cd;
				this.power = p;
			}

			private static void clean() {
				Iterator<Map.Entry<EntityLivingBase, CDTracker>> iter = cdMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<EntityLivingBase, CDTracker> entry = iter.next();
					if (!entry.getKey().isEntityAlive() || !entry.getKey().isAddedToWorld()) {
						iter.remove();
					}
				}
			}
		}
		
		public static void create(EntityLivingBase entity, boolean is_pressed) {
			if (!CDTracker.cdMap.containsKey(entity)) {
				CDTracker.cdMap.put(entity, new CDTracker());
			}
			CDTracker cd = CDTracker.cdMap.get(entity);
			if (entity.ticksExisted >= cd.cooldown) {
				if (is_pressed) {
					if (cd.power < 14.0f) {
						cd.power += entity instanceof EntityPlayer ? (float)EntityBijuManager.getCloakXp((EntityPlayer)entity) * 0.1f / 4800 : 0.01f;
					}
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer)entity).sendStatusMessage(
						 new TextComponentString(String.format("%.1f", cd.power)), true);
					}
				} else {
					if (spawn(entity, cd.power, cd.power * 70f) != null) {
						cd.cooldown = entity.ticksExisted + (int)(cd.power * 7.143f);
					}
					cd.power = 0f;
				}
			} else if (entity instanceof EntityPlayer && !entity.world.isRemote) {
				((EntityPlayer)entity).sendStatusMessage(
				 new TextComponentTranslation("chattext.cooldown.formatted", (cd.cooldown - entity.ticksExisted) / 20), true);
			}
		}
	}

	public static class AILeapAtTarget extends EntityAIBase {
	    private EntityLiving leaper;
	    private EntityLivingBase leapTarget;
	    private double leapRange;
	
	    public AILeapAtTarget(EntityLiving leapingEntity, double range) {
	        this.leaper = leapingEntity;
	        this.leapRange = range;
	        this.setMutexBits(5);
	    }
	
	 	@Override
	    public boolean shouldExecute() {
			this.leapTarget = this.leaper.getAttackTarget();
			if (this.leapTarget != null) {
				double d0 = this.leaper.getDistance(this.leapTarget);
				//double d1 = this.leaper.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
				//return d0 >= 64d && d0 <= d1 * d1 && this.leaper.onGround && this.leaper.getRNG().nextInt(5) == 0;
				return d0 > 2.0d * this.leaper.width && d0 <= this.leapRange
				 && this.leaper.onGround && this.leaper.getRNG().nextInt(5) == 0;
			}
			return false;
	    }
	
	 	@Override
	    public boolean shouldContinueExecuting() {
	        return !this.leaper.onGround;
	    }
	
	 	@Override
	    public void startExecuting() {
            double d0 = this.leapTarget.posX - this.leaper.posX;
            double d1 = this.leapTarget.posZ - this.leaper.posZ;
            double d2 = this.leapTarget.posY - this.leaper.posY;
            double d3 = d0 * d0 + d1 * d1;
            if (d3 + d2 * d2 > 2.5E-7D) {
	            double d = ProcedureUtils.getFollowRange(this.leaper);
				d3 = MathHelper.sqrt(d3);
	            if (d3 > d) {
	            	double d4 = d / d3;
	            	d0 *= d4;
	            	d1 *= d4;
	            	d2 *= d4;
	            	d3 = d;
	            }
	            this.leaper.rotationYaw = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
	            this.leaper.motionX = d0 * 0.145d;
	            this.leaper.motionZ = d1 * 0.145d;
	            this.leaper.motionY = 0.32d + (Math.max(d2, 0.0d) + d3 * 0.6d) * 0.1d;
            }
	    }
	}

	public static class NavigateGround extends PathNavigateGround {
		private BlockPos targetPos;
	    private int ticksAtLastPos;
	    private Vec3d lastPosCheck = Vec3d.ZERO;

		public NavigateGround(EntityLiving entityLivingIn, World worldIn) {
			super(entityLivingIn, worldIn);
		}

		@Override
		protected PathFinder getPathFinder() {
			return null;
		}

		public double getSpeed() {
			return this.speed;
		}

		@Override
		public float getPathSearchRange() {
			return (float)this.entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
		}

		@Override
		protected boolean canNavigate() {
			return true;
		}

		@Override
		protected Vec3d getEntityPosition() {
			return this.entity.getPositionVector();
		}

		@Override
		public Path getPathToPos(BlockPos pos) {
	        if (this.world.getBlockState(pos).getMaterial() == Material.AIR) {
	            BlockPos blockpos;
	            for (blockpos = pos.down(); blockpos.getY() > 0
	             && this.world.getBlockState(blockpos).getMaterial() == Material.AIR; blockpos = blockpos.down()) ;
	            if (blockpos.getY() > 0) {
	                return this.getPathTo(blockpos.up());
	            }
	            while (blockpos.getY() < this.world.getHeight() && this.world.getBlockState(blockpos).getMaterial() == Material.AIR) {
	                blockpos = blockpos.up();
	            }
	            pos = blockpos;
	        }
	        if (!this.world.getBlockState(pos).getMaterial().isSolid()) {
	            return this.getPathTo(pos);
	        } else {
	            BlockPos blockpos1;
	            for (blockpos1 = pos.up(); blockpos1.getY() < this.world.getHeight()
	             && this.world.getBlockState(blockpos1).getMaterial().isSolid(); blockpos1 = blockpos1.up()) ;
	            return this.getPathTo(blockpos1);
	        }
		}

		private double distanceTo(double x, double y, double z) {
			return MathHelper.sqrt(this.getEntityPosition().squareDistanceTo(x, y, z));
		}

		protected Path getPathTo(BlockPos pos) {
			if (!this.canNavigate() || this.distanceTo(0.5d+pos.getX(), this.entity.posY, 0.5d+pos.getZ()) > this.getPathSearchRange()) {
				return null;
			} else if (this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.targetPos)) {
				return this.currentPath;
			} else {
				this.targetPos = pos;
				BlockPos blockpos = new BlockPos(this.entity);
				PathPoint[] pathpoints = { new PathPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ()),
				 new PathPoint(pos.getX(), pos.getY(), pos.getZ()) };
				return new Path(pathpoints);
			}
		}

		@Override
		public boolean setPath(@Nullable Path pathentityIn, double speedIn) {
			if (super.setPath(pathentityIn, speedIn)) {
				this.ticksAtLastPos = this.totalTicks;
				this.lastPosCheck = this.getEntityPosition();
				return true;
			}
			return false;
		}

		@Override
		public void onUpdateNavigation() {
			++this.totalTicks;
//System.out.println("------ totalTicks="+totalTicks+(currentPath!=null?(", isFinished?"+currentPath.isFinished()):", currentPath=null"));
			if (!this.noPath()) {
				this.checkForStuck(this.getEntityPosition());
				if (!this.noPath()) {
					Vec3d vec3d2 = this.currentPath.getCurrentPos();
//System.out.println("       vec3d2:"+vec3d2+", currentPathIndex:"+currentPath.getCurrentPathIndex()+", pathLength:"+currentPath.getCurrentPathLength());
					if (this.distanceTo(vec3d2.x, vec3d2.y, vec3d2.z) < 0.5d * (this.entity.width + 1.0d)) {
						this.currentPath.incrementPathIndex();
					} else {
//System.out.println("       setMoveTo speed="+speed);
		                this.entity.getMoveHelper().setMoveTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
					}
				}
			}
		}

	    @Override
	    protected void checkForStuck(Vec3d positionVec3) {
	        if (this.totalTicks - this.ticksAtLastPos > 30) {
	            if (positionVec3.squareDistanceTo(this.lastPosCheck) < this.entity.width * this.entity.width) {
	                this.clearPath();
	            }
	            this.ticksAtLastPos = this.totalTicks;
	            this.lastPosCheck = positionVec3;
	        }
	    }

		@Override
		public String toString() {
			String s = "currentPath:";
			if (currentPath != null) {
				for (int i = 0; i < currentPath.getCurrentPathLength(); i++) {
					s = s+" ("+currentPath.getPathPointFromIndex(i)+")";
				}
				s += ", index:"+currentPath.getCurrentPathIndex();
			} else {
				s += "nul";
			}
			return s;
		}

		@Override
		protected void removeSunnyPath() {
		}

	    @Override
	    public void setBreakDoors(boolean canBreakDoors) {
	    }
	
	    @Override
	    public void setEnterDoors(boolean enterDoors) {
	    }
	
	    @Override
	    public boolean getEnterDoors() {
	        return false;
	    }
	
	    @Override
	    public void setCanSwim(boolean canSwim) {
	    }
	
	    @Override
	    public boolean getCanSwim() {
	        return false;
	    }
	}

	public static class MoveHelper<T extends EntityLiving & ICollisionData> extends EntityMoveHelper {
		private T baseEntity;
		
		public MoveHelper(T entity) {
			super(entity);
			this.baseEntity = entity;
		}

		@Override
		public void onUpdateMoveHelper() {
			if (this.isUpdating()) {
	            this.action = EntityMoveHelper.Action.WAIT;
	            double d0 = this.posX - this.entity.posX;
	            double d1 = this.posZ - this.entity.posZ;
	            double d2 = this.posY - this.entity.posY;
	            double d3 = d0 * d0 + d2 * d2 + d1 * d1;
	            if (d3 < 2.5E-7D) {
	                this.entity.setMoveForward(0.0F);
	                return;
	            }
				if (this.entity.collidedHorizontally) {
					ProcedureUtils.CollisionHelper collisionData = this.baseEntity.getCollisionData();
					double d5 = this.entity.posY;
					for (EnumFacing face : EnumFacing.HORIZONTALS) {
						for (BlockPos pos : collisionData.hitsOnSide(face)) {
							double d6 = this.getHighestSolidTop(this.entity.world, pos);
							if (d6 > d5) {
								d5 = d6;
							}
						}
					}
					if (d5 - this.entity.posY > 2d * this.entity.height || collisionData.hitOnSide(EnumFacing.UP)) {
						if (collisionData.hitOnAxis(EnumFacing.Axis.X)) {
							d0 = 0.0d;
						}
						if (collisionData.hitOnAxis(EnumFacing.Axis.Z)) {
							d1 = 0.0d;
						}
					} else if (this.entity.onGround) {
						this.entity.motionY = 0.8d + (d5 - this.entity.posY) * 0.1d;
					} else {
						this.entity.motionY += 0.1d;
					}
				}
	            float f = (float)(MathHelper.atan2(d1, d0) * 180D / Math.PI) - 90.0F;
	            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f, 90.0F);
	            this.entity.renderYawOffset = this.entity.rotationYaw;
				f = (float)(-(MathHelper.atan2(d2, MathHelper.sqrt(d0 * d0 + d1 * d1)) * (180D / Math.PI)));
				this.entity.rotationPitch = this.limitAngle(this.entity.rotationPitch, f, 60.0F);
	            this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
			} else {
				super.onUpdateMoveHelper();
			}
		}

		private double getHighestSolidTop(World world, BlockPos pos) {
            while (pos.getY() < world.getHeight() && world.getBlockState(pos.up()).getCollisionBoundingBox(world, pos) != null) {
                pos = pos.up();
            }
            return world.getBlockState(pos).getBoundingBox(world, pos).maxY + pos.getY();
		}
	}

	public static class FlySwimHelper extends EntityMoveHelper {
		private Base baseEntity;
			
		public FlySwimHelper(Base entityIn) {
			super(entityIn);
			this.baseEntity = entityIn;
		}
				
		@Override
		public void onUpdateMoveHelper() {
			if (this.action == EntityMoveHelper.Action.MOVE_TO) {
				this.action = EntityMoveHelper.Action.WAIT;
				double d0 = this.posX - this.entity.posX;
				double d1 = this.posY - this.entity.posY;
				double d2 = this.posZ - this.entity.posZ;
				double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
				if (d3 < 1.6E-7D) {
					ProcedureUtils.multiplyVelocity(this.entity, 0.0d);
				} else {
					double movementSpeed = this.entity.hasNoGravity() && this.entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED) != null
					 ? this.entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue()
					 : this.entity.getEntityAttribute(EntityLivingBase.SWIM_SPEED).getAttributeValue();
					float f = (float)(this.speed * movementSpeed);
					float f1 = -((float)MathHelper.atan2(d0, d2)) * (180F / (float)Math.PI);
					this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f1, 60.0F);
					//this.entity.renderYawOffset = this.entity.rotationYaw;
					f1 = (float)(-(MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2)) * (180D / Math.PI)));
					this.entity.rotationPitch = this.limitAngle(this.entity.rotationPitch, f1, 30.0F);
					if (this.entity.collided) {
						if (this.baseEntity.collisionData.hitOnSide(EnumFacing.UP)) {
							d1 = 0.0d;
							if (this.baseEntity.collisionData.hitOnAxis(EnumFacing.Axis.X)) {
								d0 = 0.0d;
								d2 = d3;
							}
							if (this.baseEntity.collisionData.hitOnAxis(EnumFacing.Axis.Z)) {
								d2 = 0.0d;
								d0 = d3;
							}
						} else {
							d1 = 12.0d;
						}
					}
					d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
					this.entity.motionX = d0 / d3 * f;
					this.entity.motionY = d1 / d3 * f;
					this.entity.motionZ = d2 / d3 * f;
				}
			} else {
				ProcedureUtils.multiplyVelocity(this.entity, 0.6d);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new ClientOnly().register();
	}

	public static class ClientOnly extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityTailBeastBall.class, RenderTailBeastBall::new);
		}

		@SideOnly(Side.CLIENT)
		public static class LayerEntityDeath implements LayerRenderer<Base> {
			private final ResourceLocation textureRed = new ResourceLocation("narutomod:textures/fuuin_beam_red.png");
			private final ResourceLocation texture10t = new ResourceLocation("narutomod:textures/fuuin_beam_10tails.png");
			private final RenderLiving renderer;

			public LayerEntityDeath(RenderLiving rendererIn) {
				this.renderer = rendererIn;
			}

			@Override
			public void doRenderLayer(Base entity, float _1, float _2, float partialTicks, float _3, float _4, float _5, float _6) {
				Entity vessel = entity.getTargetVessel();
				if (entity.deathTicks > 1 && vessel != null) {
					float f = ((float) entity.deathTicks + partialTicks) * 0.01F;
					float offset = entity.getFuuinBeamHeight();
					double d0 = vessel.lastTickPosX + (vessel.posX - vessel.lastTickPosX) * partialTicks;
					double d1 = vessel.lastTickPosY + (vessel.posY - vessel.lastTickPosY) * partialTicks + vessel.getEyeHeight();
					double d2 = vessel.lastTickPosZ + (vessel.posZ - vessel.lastTickPosZ) * partialTicks;
					double dx = d0 - entity.posX;
					double dy = d1 - (entity.posY + offset);
					double dz = d2 - entity.posZ;
					double dxz = MathHelper.sqrt(dx * dx + dz * dz);
					double max_l = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
					float rot_y = (float) -Math.atan2(dx, dz) * 180.0F / (float) Math.PI;
					float rot_x = (float) -Math.atan2(dy, dxz) * 180.0F / (float) Math.PI;
					rot_y = MathHelper.wrapDegrees(rot_y - entity.renderYawOffset);
					this.renderer.bindTexture(entity instanceof EntityTenTails.EntityCustom ? this.texture10t : this.textureRed);
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.0F, -offset + (vessel instanceof EntityPlayer ? 1.501F : 0F), 0.0F);
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(rot_y, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(rot_x - 90.0F, 1.0F, 0.0F, 0.0F);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferbuilder = tessellator.getBuffer();
					RenderHelper.disableStandardItemLighting();
					GlStateManager.enableBlend();
					GlStateManager.disableCull();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					GlStateManager.shadeModel(0x1D01);
					float f5 = 0.0F - f;
					float f6 = (float) max_l / 32.0F - f;
					bufferbuilder.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
					for (int j = 0; j <= 8; j++) {
						float f7 = MathHelper.sin((j % 8) * ((float) Math.PI * 2F) / 8.0F);
						float f8 = MathHelper.cos((j % 8) * ((float) Math.PI * 2F) / 8.0F);
						float f9 = (j % 8) / 8.0F;
						bufferbuilder.pos(f7 * 1.5F, f8 * 1.5F, 0.0D).tex(f9, f5).color(0, 0, 0, 128).endVertex();
						bufferbuilder.pos(f7 * 0.3F, f8 * 0.3F, (float) max_l).tex(f9, f6).color(255, 255, 255, 192).endVertex();
					}
					tessellator.draw();
					GlStateManager.enableCull();
					GlStateManager.disableBlend();
					GlStateManager.shadeModel(0x1D00);
					RenderHelper.enableStandardItemLighting();
					GlStateManager.popMatrix();
				}
			}

			@Override
			public boolean shouldCombineTextures() {
				return false;
			}
		}

		@SideOnly(Side.CLIENT)
		public static abstract class Renderer<T extends Base> extends RenderLiving<T> {
			public Renderer(RenderManager renderManagerIn, ModelBase model, float shadowsize) {
				super(renderManagerIn, model, shadowsize);
				this.addLayer(new LayerEntityDeath(this));
			}

			@Override
			public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.isBeingRidden() && entity.canBeSteered() && entity.getControllingPassenger() instanceof EntityLivingBase) {
					this.copyLimbSwing(entity, (EntityLivingBase) entity.getControllingPassenger());
				}
				this.setModelVisibilities(entity);
				this.shadowSize = entity.getModelScale() * 0.5f;
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			@Override
			protected void renderModel(T entity, float f0, float f1, float f2, float f3, float f4, float f5) {
				float f = entity.getTransparency();
				boolean flag = f > 0.0f && f < 1.0f;
				if (flag) {
					GlStateManager.enableBlend();
					GlStateManager.color(1.0f, 1.0f, 1.0f, f);
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				}
				super.renderModel(entity, f0, f1, f2, f3, f4, f5);
				if (flag) {
					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
					GlStateManager.disableBlend();
				}
			}

			protected void copyLimbSwing(T entity, EntityLivingBase rider) {
				entity.swingProgress = rider.swingProgress;
				entity.swingProgressInt = rider.swingProgressInt;
				entity.prevSwingProgress = rider.prevSwingProgress;
				entity.isSwingInProgress = rider.isSwingInProgress;
				entity.swingingHand = rider.swingingHand;
			}

			protected void setModelVisibilities(T entity) {
				if (this.getMainModel() instanceof ModelBiped) {
					ModelBiped model = (ModelBiped) this.getMainModel();
					model.setVisible(true);
					if (Minecraft.getMinecraft().getRenderViewEntity().equals(entity.getControllingPassenger())
							&& this.renderManager.options.thirdPersonView == 0) {
						model.bipedHead.showModel = false;
						model.bipedHeadwear.showModel = false;
						model.bipedBody.showModel = false;
					}
				} else if (this.getMainModel() instanceof ModelQuadruped) {
					ModelQuadruped model = (ModelQuadruped) this.getMainModel();
					model.head.showModel = Minecraft.getMinecraft().getRenderViewEntity().equals(entity.getControllingPassenger())
					 && this.renderManager.options.thirdPersonView == 0 ? false : true;
				}
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderTailBeastBall extends Render<EntityTailBeastBall> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/bijudama1.png");
			protected final ModelBase mainModel;

			public RenderTailBeastBall(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.mainModel = new ModelBijudama();
			}

			@Override
			public void doRender(EntityTailBeastBall entity, double x, double y, double z, float entityYaw, float partialTicks) {
				float ageInTicks = partialTicks + entity.ticksExisted;
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				//GlStateManager.disableCull();
				float scale = entity.getEntityScale();
				GlStateManager.translate(x, y + (0.15625F * scale), z);
				GlStateManager.scale(scale, scale, scale);
				//GlStateManager.rotate(ageInTicks * 30.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				float alpha = 1.0F;
				Entity shooter = entity.getShooter();
				if (shooter instanceof EntityPlayer && shooter == this.renderManager.renderViewEntity && this.renderManager.options.thirdPersonView == 0 && entity.ticksExisted <= entity.buildupTime) {
					alpha = 0.2F;
				}
				this.mainModel.render(entity, alpha, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableLighting();
				//GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				//GlStateManager.enableCull();
				GlStateManager.popMatrix();
				//super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityTailBeastBall entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelBijudama extends ModelBase {
			private final ModelRenderer bb_main;
			private final ModelRenderer hexadecagon;
			private final ModelRenderer hexadecagon_r1;
			private final ModelRenderer hexadecagon_r2;
			private final ModelRenderer hexadecagon_r3;
			private final ModelRenderer hexadecagon_r4;
			private final ModelRenderer hexadecagon_r5;
			private final ModelRenderer hexadecagon_r6;
			private final ModelRenderer hexadecagon_r7;
			private final ModelRenderer hexadecagon2;
			private final ModelRenderer hexadecagon_r8;
			private final ModelRenderer hexadecagon_r9;
			private final ModelRenderer hexadecagon_r10;
			private final ModelRenderer hexadecagon_r11;
			private final ModelRenderer hexadecagon_r12;
			private final ModelRenderer hexadecagon_r13;
			private final ModelRenderer hexadecagon_r14;
			private final ModelRenderer hexadecagon3;
			private final ModelRenderer hexadecagon_r15;
			private final ModelRenderer hexadecagon_r16;
			private final ModelRenderer hexadecagon_r17;
			private final ModelRenderer hexadecagon_r18;
			private final ModelRenderer hexadecagon_r19;
			private final ModelRenderer hexadecagon_r20;
			private final ModelRenderer hexadecagon_r21;
			private final ModelRenderer hexadecagon4;
			private final ModelRenderer hexadecagon_r22;
			private final ModelRenderer hexadecagon_r23;
			private final ModelRenderer hexadecagon_r24;
			private final ModelRenderer hexadecagon_r25;
			private final ModelRenderer hexadecagon_r26;
			private final ModelRenderer hexadecagon_r27;
			private final ModelRenderer hexadecagon_r28;
			private final ModelRenderer hexadecagon5;
			private final ModelRenderer hexadecagon_r29;
			private final ModelRenderer hexadecagon_r30;
			private final ModelRenderer hexadecagon_r31;
			private final ModelRenderer hexadecagon_r32;
			private final ModelRenderer hexadecagon_r33;
			private final ModelRenderer hexadecagon_r34;
			private final ModelRenderer hexadecagon_r35;
			private final ModelRenderer hexadecagon6;
			private final ModelRenderer hexadecagon_r36;
			private final ModelRenderer hexadecagon_r37;
			private final ModelRenderer hexadecagon_r38;
			private final ModelRenderer hexadecagon_r39;
			private final ModelRenderer hexadecagon_r40;
			private final ModelRenderer hexadecagon_r41;
			private final ModelRenderer hexadecagon_r42;
			private final ModelRenderer hexadecagon7;
			private final ModelRenderer hexadecagon_r43;
			private final ModelRenderer hexadecagon_r44;
			private final ModelRenderer hexadecagon_r45;
			private final ModelRenderer hexadecagon_r46;
			private final ModelRenderer hexadecagon_r47;
			private final ModelRenderer hexadecagon_r48;
			private final ModelRenderer hexadecagon_r49;
			private final ModelRenderer hexadecagon8;
			private final ModelRenderer hexadecagon_r50;
			private final ModelRenderer hexadecagon_r51;
			private final ModelRenderer hexadecagon_r52;
			private final ModelRenderer hexadecagon_r53;
			private final ModelRenderer hexadecagon_r54;
			private final ModelRenderer hexadecagon_r55;
			private final ModelRenderer hexadecagon_r56;
		
			public ModelBijudama() {
				textureWidth = 4;
				textureHeight = 4;
		
				bb_main = new ModelRenderer(this);
				bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				hexadecagon = new ModelRenderer(this);
				hexadecagon.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon);
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r1 = new ModelRenderer(this);
				hexadecagon_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r1);
				setRotationAngle(hexadecagon_r1, 0.0F, 0.0F, 1.9635F);
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r2 = new ModelRenderer(this);
				hexadecagon_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r2);
				setRotationAngle(hexadecagon_r2, 0.0F, 0.0F, 1.5708F);
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r3 = new ModelRenderer(this);
				hexadecagon_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r3);
				setRotationAngle(hexadecagon_r3, 0.0F, 0.0F, 1.1781F);
				hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r4 = new ModelRenderer(this);
				hexadecagon_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r4);
				setRotationAngle(hexadecagon_r4, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r5 = new ModelRenderer(this);
				hexadecagon_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r5);
				setRotationAngle(hexadecagon_r5, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r6 = new ModelRenderer(this);
				hexadecagon_r6.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r6);
				setRotationAngle(hexadecagon_r6, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r7 = new ModelRenderer(this);
				hexadecagon_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r7);
				setRotationAngle(hexadecagon_r7, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r7.cubeList.add(new ModelBox(hexadecagon_r7, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon2 = new ModelRenderer(this);
				hexadecagon2.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon2);
				setRotationAngle(hexadecagon2, 0.0F, -0.3927F, 0.0F);
				
		
				hexadecagon_r8 = new ModelRenderer(this);
				hexadecagon_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r8);
				setRotationAngle(hexadecagon_r8, 0.0F, 0.0F, 1.9635F);
				hexadecagon_r8.cubeList.add(new ModelBox(hexadecagon_r8, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r9 = new ModelRenderer(this);
				hexadecagon_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r9);
				setRotationAngle(hexadecagon_r9, 0.0F, 0.0F, 1.5708F);
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r10 = new ModelRenderer(this);
				hexadecagon_r10.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r10);
				setRotationAngle(hexadecagon_r10, 0.0F, 0.0F, 1.1781F);
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r11 = new ModelRenderer(this);
				hexadecagon_r11.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r11);
				setRotationAngle(hexadecagon_r11, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r11.cubeList.add(new ModelBox(hexadecagon_r11, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r12 = new ModelRenderer(this);
				hexadecagon_r12.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r12);
				setRotationAngle(hexadecagon_r12, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r12.cubeList.add(new ModelBox(hexadecagon_r12, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r13 = new ModelRenderer(this);
				hexadecagon_r13.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r13);
				setRotationAngle(hexadecagon_r13, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r14 = new ModelRenderer(this);
				hexadecagon_r14.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r14);
				setRotationAngle(hexadecagon_r14, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon3 = new ModelRenderer(this);
				hexadecagon3.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon3);
				setRotationAngle(hexadecagon3, 0.0F, -0.7854F, 0.0F);
				
		
				hexadecagon_r15 = new ModelRenderer(this);
				hexadecagon_r15.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r15);
				setRotationAngle(hexadecagon_r15, 0.0F, 0.0F, 1.9635F);
				hexadecagon_r15.cubeList.add(new ModelBox(hexadecagon_r15, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r16 = new ModelRenderer(this);
				hexadecagon_r16.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r16);
				setRotationAngle(hexadecagon_r16, 0.0F, 0.0F, 1.5708F);
				hexadecagon_r16.cubeList.add(new ModelBox(hexadecagon_r16, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r17 = new ModelRenderer(this);
				hexadecagon_r17.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r17);
				setRotationAngle(hexadecagon_r17, 0.0F, 0.0F, 1.1781F);
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r18 = new ModelRenderer(this);
				hexadecagon_r18.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r18);
				setRotationAngle(hexadecagon_r18, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r19 = new ModelRenderer(this);
				hexadecagon_r19.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r19);
				setRotationAngle(hexadecagon_r19, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r19.cubeList.add(new ModelBox(hexadecagon_r19, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r20 = new ModelRenderer(this);
				hexadecagon_r20.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r20);
				setRotationAngle(hexadecagon_r20, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r20.cubeList.add(new ModelBox(hexadecagon_r20, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r21 = new ModelRenderer(this);
				hexadecagon_r21.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r21);
				setRotationAngle(hexadecagon_r21, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon4 = new ModelRenderer(this);
				hexadecagon4.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon4);
				setRotationAngle(hexadecagon4, 0.0F, -1.1781F, 0.0F);
				
		
				hexadecagon_r22 = new ModelRenderer(this);
				hexadecagon_r22.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r22);
				setRotationAngle(hexadecagon_r22, 0.0F, 0.0F, 1.9635F);
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r23 = new ModelRenderer(this);
				hexadecagon_r23.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r23);
				setRotationAngle(hexadecagon_r23, 0.0F, 0.0F, 1.5708F);
				hexadecagon_r23.cubeList.add(new ModelBox(hexadecagon_r23, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r24 = new ModelRenderer(this);
				hexadecagon_r24.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r24);
				setRotationAngle(hexadecagon_r24, 0.0F, 0.0F, 1.1781F);
				hexadecagon_r24.cubeList.add(new ModelBox(hexadecagon_r24, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r25 = new ModelRenderer(this);
				hexadecagon_r25.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r25);
				setRotationAngle(hexadecagon_r25, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r26 = new ModelRenderer(this);
				hexadecagon_r26.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r26);
				setRotationAngle(hexadecagon_r26, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r27 = new ModelRenderer(this);
				hexadecagon_r27.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r27);
				setRotationAngle(hexadecagon_r27, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r27.cubeList.add(new ModelBox(hexadecagon_r27, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r28 = new ModelRenderer(this);
				hexadecagon_r28.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r28);
				setRotationAngle(hexadecagon_r28, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r28.cubeList.add(new ModelBox(hexadecagon_r28, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon5 = new ModelRenderer(this);
				hexadecagon5.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon5);
				setRotationAngle(hexadecagon5, 0.0F, -1.5708F, 0.0F);
				
		
				hexadecagon_r29 = new ModelRenderer(this);
				hexadecagon_r29.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r29);
				setRotationAngle(hexadecagon_r29, 0.0F, 0.0F, 1.9635F);
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r30 = new ModelRenderer(this);
				hexadecagon_r30.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r30);
				setRotationAngle(hexadecagon_r30, 0.0F, 0.0F, 1.5708F);
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r31 = new ModelRenderer(this);
				hexadecagon_r31.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r31);
				setRotationAngle(hexadecagon_r31, 0.0F, 0.0F, 1.1781F);
				hexadecagon_r31.cubeList.add(new ModelBox(hexadecagon_r31, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r32 = new ModelRenderer(this);
				hexadecagon_r32.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r32);
				setRotationAngle(hexadecagon_r32, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r32.cubeList.add(new ModelBox(hexadecagon_r32, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r33 = new ModelRenderer(this);
				hexadecagon_r33.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r33);
				setRotationAngle(hexadecagon_r33, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r33.cubeList.add(new ModelBox(hexadecagon_r33, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r34 = new ModelRenderer(this);
				hexadecagon_r34.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r34);
				setRotationAngle(hexadecagon_r34, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r34.cubeList.add(new ModelBox(hexadecagon_r34, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r35 = new ModelRenderer(this);
				hexadecagon_r35.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r35);
				setRotationAngle(hexadecagon_r35, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r35.cubeList.add(new ModelBox(hexadecagon_r35, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon6 = new ModelRenderer(this);
				hexadecagon6.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon6);
				setRotationAngle(hexadecagon6, 0.0F, -1.9635F, 0.0F);
				
		
				hexadecagon_r36 = new ModelRenderer(this);
				hexadecagon_r36.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r36);
				setRotationAngle(hexadecagon_r36, 0.0F, 0.0F, 1.9635F);
				hexadecagon_r36.cubeList.add(new ModelBox(hexadecagon_r36, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r37 = new ModelRenderer(this);
				hexadecagon_r37.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r37);
				setRotationAngle(hexadecagon_r37, 0.0F, 0.0F, 1.5708F);
				hexadecagon_r37.cubeList.add(new ModelBox(hexadecagon_r37, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r38 = new ModelRenderer(this);
				hexadecagon_r38.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r38);
				setRotationAngle(hexadecagon_r38, 0.0F, 0.0F, 1.1781F);
				hexadecagon_r38.cubeList.add(new ModelBox(hexadecagon_r38, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r39 = new ModelRenderer(this);
				hexadecagon_r39.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r39);
				setRotationAngle(hexadecagon_r39, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r39.cubeList.add(new ModelBox(hexadecagon_r39, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r40 = new ModelRenderer(this);
				hexadecagon_r40.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r40);
				setRotationAngle(hexadecagon_r40, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r40.cubeList.add(new ModelBox(hexadecagon_r40, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r41 = new ModelRenderer(this);
				hexadecagon_r41.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r41);
				setRotationAngle(hexadecagon_r41, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r41.cubeList.add(new ModelBox(hexadecagon_r41, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r42 = new ModelRenderer(this);
				hexadecagon_r42.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r42);
				setRotationAngle(hexadecagon_r42, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r42.cubeList.add(new ModelBox(hexadecagon_r42, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon7 = new ModelRenderer(this);
				hexadecagon7.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon7);
				setRotationAngle(hexadecagon7, 0.0F, -2.3562F, 0.0F);
				
		
				hexadecagon_r43 = new ModelRenderer(this);
				hexadecagon_r43.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r43);
				setRotationAngle(hexadecagon_r43, 0.0F, 0.0F, 1.9635F);
				hexadecagon_r43.cubeList.add(new ModelBox(hexadecagon_r43, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r44 = new ModelRenderer(this);
				hexadecagon_r44.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r44);
				setRotationAngle(hexadecagon_r44, 0.0F, 0.0F, 1.5708F);
				hexadecagon_r44.cubeList.add(new ModelBox(hexadecagon_r44, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r45 = new ModelRenderer(this);
				hexadecagon_r45.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r45);
				setRotationAngle(hexadecagon_r45, 0.0F, 0.0F, 1.1781F);
				hexadecagon_r45.cubeList.add(new ModelBox(hexadecagon_r45, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r46 = new ModelRenderer(this);
				hexadecagon_r46.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r46);
				setRotationAngle(hexadecagon_r46, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r46.cubeList.add(new ModelBox(hexadecagon_r46, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r47 = new ModelRenderer(this);
				hexadecagon_r47.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r47);
				setRotationAngle(hexadecagon_r47, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r47.cubeList.add(new ModelBox(hexadecagon_r47, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r48 = new ModelRenderer(this);
				hexadecagon_r48.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r48);
				setRotationAngle(hexadecagon_r48, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r48.cubeList.add(new ModelBox(hexadecagon_r48, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r49 = new ModelRenderer(this);
				hexadecagon_r49.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r49);
				setRotationAngle(hexadecagon_r49, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r49.cubeList.add(new ModelBox(hexadecagon_r49, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon8 = new ModelRenderer(this);
				hexadecagon8.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon8);
				setRotationAngle(hexadecagon8, 0.0F, -2.7489F, 0.0F);
				
		
				hexadecagon_r50 = new ModelRenderer(this);
				hexadecagon_r50.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r50);
				setRotationAngle(hexadecagon_r50, 0.0F, 0.0F, 1.9635F);
				hexadecagon_r50.cubeList.add(new ModelBox(hexadecagon_r50, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r51 = new ModelRenderer(this);
				hexadecagon_r51.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r51);
				setRotationAngle(hexadecagon_r51, 0.0F, 0.0F, 1.5708F);
				hexadecagon_r51.cubeList.add(new ModelBox(hexadecagon_r51, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r52 = new ModelRenderer(this);
				hexadecagon_r52.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r52);
				setRotationAngle(hexadecagon_r52, 0.0F, 0.0F, 1.1781F);
				hexadecagon_r52.cubeList.add(new ModelBox(hexadecagon_r52, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r53 = new ModelRenderer(this);
				hexadecagon_r53.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r53);
				setRotationAngle(hexadecagon_r53, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r53.cubeList.add(new ModelBox(hexadecagon_r53, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r54 = new ModelRenderer(this);
				hexadecagon_r54.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r54);
				setRotationAngle(hexadecagon_r54, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r54.cubeList.add(new ModelBox(hexadecagon_r54, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r55 = new ModelRenderer(this);
				hexadecagon_r55.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r55);
				setRotationAngle(hexadecagon_r55, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r55.cubeList.add(new ModelBox(hexadecagon_r55, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r56 = new ModelRenderer(this);
				hexadecagon_r56.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r56);
				setRotationAngle(hexadecagon_r56, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r56.cubeList.add(new ModelBox(hexadecagon_r56, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float alpha, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
				bb_main.render(f5);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}

	public static abstract class SaveBase extends WorldSavedData implements SaveData.ISaveData {
		public SaveBase(String name) {
			super(name);
		}

		protected abstract EntityBijuManager getBijuManager();

		protected abstract Base createEntity(World world);

		@Override
		public void resetData() {
			EntityBijuManager bm = this.getBijuManager();
			if (bm.getCloakLevel() > 0) {
				bm.toggleBijuCloak();
			}
			bm.reset();
		}

		@Override
		public void readFromNBT(NBTTagCompound compound) {
			if (this.getBijuManager().getEntity() == null && compound.getBoolean("spawned")) {
				World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(compound.getInteger("dimension"));
				if (world != null) {
					this.getBijuManager().onAddedToWorld(this.createEntity(world), false);
					this.getBijuManager().loadEntityFromNBT(compound.getCompoundTag("entityNBT"));
				}
			}
			if (compound.hasUniqueId("JinchurikiUUID")) {
				UUID vesseluuid = compound.getUniqueId("JinchurikiUUID");
				this.getBijuManager().setVesselUuid(vesseluuid);
				this.getBijuManager().setVesselName(compound.getString("VesselName"));
				this.getBijuManager().setVesselTime(compound.getLong("VesselSetTime"));
				this.getBijuManager().setJinchurikiLastActiveTime(compound.getLong("JinchurikiLastActiveTime"), false);
				this.getBijuManager().setCloakXPs(compound.getIntArray("JinchurikiCloakXp"));
				this.getBijuManager().setCloakCD(compound.getLong("JinchurikiCloakCD"));
				Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(vesseluuid);
				if (entity != null) {
					this.getBijuManager().setVesselEntity(entity, false);
				}
			}
			if (compound.hasKey("spawnPosX")) {
				this.getBijuManager().setSpawnPos(new BlockPos(compound.getDouble("spawnPosX"),
						compound.getDouble("spawnPosY"), compound.getDouble("spawnPosZ")), false);
			}
			this.getBijuManager().setTicksSinceDeath(compound.getInteger("ticksSinceDeath"), false);
			this.getBijuManager().setHasLived(compound.getBoolean("hasLived"), false);
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			Entity entity = this.getBijuManager().getEntity();
			compound.setBoolean("spawned", entity != null);
			if (entity != null) {
				compound.setInteger("dimension", entity.dimension);
				compound.setTag("entityNBT", entity.writeToNBT(new NBTTagCompound()));
			}
			UUID vesseluuid = this.getBijuManager().getVesselUuid();
			if (vesseluuid != null) {
				compound.setUniqueId("JinchurikiUUID", vesseluuid);
				compound.setString("VesselName", this.getBijuManager().getVesselName());
				compound.setLong("VesselSetTime", this.getBijuManager().getVesselSetTime());
				compound.setLong("JinchurikiLastActiveTime", this.getBijuManager().getJinchurikiLastActiveTime());
				compound.setIntArray("JinchurikiCloakXp", this.getBijuManager().getCloakXPs());
				compound.setLong("JinchurikiCloakCD", this.getBijuManager().getCloakCD());
			} else {
				compound.removeTag("JinchurikiUUIDMost");
				compound.removeTag("JinchurikiUUIDLeast");
				compound.removeTag("VesselName");
				compound.removeTag("VesselSetTime");
				compound.removeTag("JinchurikiLastActiveTime");
				compound.removeTag("JinchurikiCloakXp");
				compound.removeTag("JinchurikiCloakCD");
			}
			if (this.getBijuManager().hasSpawnPos()) {
				BlockPos spawnPos = this.getBijuManager().getSpawnPos();
				compound.setDouble("spawnPosX", spawnPos.getX());
				compound.setDouble("spawnPosY", spawnPos.getY());
				compound.setDouble("spawnPosZ", spawnPos.getZ());
			}
			compound.setInteger("ticksSinceDeath", this.getBijuManager().getTicksSinceDeath());
			compound.setBoolean("hasLived", this.getBijuManager().getHasLived());
			return compound;
		}
	}

	public class PlayerHooks {
		private void checkAndRemove(EntityPlayer entity) {
			if (EntityBijuManager.cloakLevel(entity) > 0) {
				EntityBijuManager.toggleBijuCloak(entity);
			}
			for (EntityBijuManager bm : EntityBijuManager.getBMList()) {
				Base biju = bm.getEntityInWorld(entity.world);
				if (biju != null && biju.isEntityAlive()
						&& (entity.equals(biju.summoningPlayer) || entity.equals(bm.getJinchurikiPlayer()))) {
					biju.setDead();
				}
			}
		}

		@SubscribeEvent
		public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
			for (EntityBijuManager bm : EntityBijuManager.getBMList()) {
				bm.verifyVesselEntity(event.player);
			}
		}

		@SubscribeEvent
		public void onPlayerChangeDimension(EntityTravelToDimensionEvent event) {
			if (event.getEntity() instanceof EntityPlayer) {
				this.checkAndRemove((EntityPlayer) event.getEntity());
			}
		}

		@SubscribeEvent
		public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
			this.checkAndRemove(event.player);
		}

		@SubscribeEvent
		public void onServerDisconnect(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
			this.checkAndRemove(((net.minecraft.network.NetHandlerPlayServer) event.getHandler()).player);
		}

		@SubscribeEvent
		public void onClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
			EntityBijuManager bm = EntityBijuManager.getBijuManagerFrom(event.getOriginal());
			if (bm != null) {
				bm.setVesselEntity(event.getEntityPlayer(), false);
			}
		}

		@SubscribeEvent
		public void onPlayerTick(TickEvent.PlayerTickEvent event) {
			if (event.phase == TickEvent.Phase.END && event.player instanceof EntityPlayerMP && event.player.ticksExisted % 40 == 1) {
				EntityBijuManager bm = EntityBijuManager.getBijuManagerFrom(event.player);
				if (bm != null) {
					long l = ((EntityPlayerMP)event.player).getLastActiveTime();
					if (l != bm.getJinchurikiLastActiveTime()) {
						bm.setJinchurikiLastActiveTime(l);
					}
				}
			}
		}
	}

	public interface ICollisionData {
		ProcedureUtils.CollisionHelper getCollisionData();
	}
}
