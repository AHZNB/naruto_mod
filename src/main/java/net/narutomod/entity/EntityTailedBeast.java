
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
import net.narutomod.procedure.ProcedureCameraShake;
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

	public static abstract class Base extends EntityMob implements IRangedAttackMob, EntityBijuManager.ITailBeast {
		protected static final List<Material> canBreakList = Lists.newArrayList(Material.WOOD, Material.CACTUS,
		 Material.GLASS, Material.LEAVES, Material.PLANTS, Material.SNOW, Material.VINE, Material.WEB);
		private static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> VESSEL = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Boolean> SHOOT = EntityDataManager.<Boolean>createKey(Base.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Boolean> CANSTEER = EntityDataManager.<Boolean>createKey(Base.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Boolean> FACEDOWN = EntityDataManager.<Boolean>createKey(Base.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Float> TRANSPARENCY = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
		public static final int BIJUDAMA_CD = 200;
		protected final double TARGET_RANGE = 108.0D;
		private int deathTicks;
		private int deathTotalTicks;
		private EntityPlayer summoningPlayer;
		private int tailBeastBallTime = BIJUDAMA_CD;
		private int angerLevel;
		private int lifeSpan = Integer.MAX_VALUE - 1;
		private boolean motionHalted;
		protected boolean canPassengerDismount = true;
		protected boolean spawnedBySpawner;
		protected final ProcedureUtils.CollisionHelper collisionData;

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
			this.setMeleeAttackTasks();
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
			this.motionHalted = down;
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

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (FACEDOWN.equals(key) && this.world.isRemote) {
				this.setFaceDown(this.isFaceDown());
			}
		}

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
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(TARGET_RANGE);
			this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(200.0D);
		}

		@Override
		public IAttributeInstance getEntityAttribute(IAttribute attribute) {
			return super.getEntityAttribute(attribute == SharedMonsterAttributes.MAX_HEALTH ? ProcedureUtils.MAXHEALTH : attribute);
		}

		protected void setMeleeAttackTasks() {
			this.tasks.addTask(0, new AILeapAtTarget(this, 24d, 2.0f) {
				@Override
				public boolean shouldExecute() {
					return !Base.this.isMotionHalted() && super.shouldExecute();
				}
			});
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.2D, true) {
				@Override
				public boolean shouldExecute() {
					return !Base.this.isMotionHalted() && super.shouldExecute();
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
			this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, false) {
				@Override
				protected double getTargetDistance() {
					return TARGET_RANGE;
				}
			});
			this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true, false) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !Base.this.isOnSameTeam(this.targetEntity) && Base.this.angerLevel > 0;
				}
				@Override
				protected double getTargetDistance() {
					return TARGET_RANGE;
				}
			});
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, true, false) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && Base.this.angerLevel > 1;
				}
				@Override
				protected double getTargetDistance() {
					return TARGET_RANGE * 0.5d;
				}
			});
			this.tasks.addTask(3, new EntityAIAttackRanged(this, 1.2D, BIJUDAMA_CD, (float)TARGET_RANGE + 64f) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !Base.this.isMotionHalted() && Base.this.canShootBijudama()
					 && Base.this.getDistance(Base.this.getAttackTarget()) > 32d
					 && !Base.this.isInsideOfMaterial(Material.WATER);
				}
				@Override
				public void resetTask() {
					super.resetTask();
					Base.this.setSwingingArms(false);
				}
			});
			this.tasks.addTask(4, new EntityAIWander(this, 1.0D) {
				@Override
				public boolean shouldExecute() {
					return !Base.this.isMotionHalted() && super.shouldExecute();
				}
				@Override @Nullable
				protected Vec3d getPosition() {
					return RandomPositionGenerator.findRandomTarget(this.entity, 56, 21);
				}
			});
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive()) {
				this.setAttackTarget(null);
			}
		}

		protected void haltMotion(boolean halt) {
			this.motionHalted = halt;
		}

		protected boolean isMotionHalted() {
			return this.motionHalted;
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
			 .scale(this.width * 1.5).add(this.getPositionEyes(1.0f));
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
				this.setAngerLevel(hp < 0.5f * maxhp ? 2 : hp < maxhp - 500f ? 1 : 0);
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
				 .damageEntities(this, (float)ProcedureUtils.getModifiedAttackDamage(this) * (this.rand.nextFloat() * 0.5f + 0.5f));
				return true;
			}
			return false;
		}

		public boolean couldBreakBlocks() {
			return this.world.getGameRules().getBoolean("mobGriefing");
		}

		@Override
		public void move(MoverType type, double x, double y, double z) {
			this.collisionData.collideWithAABBs(this.world.getCollisionBoxes(this, this.getEntityBoundingBox().expand(x, y, z)), x, y, z);
			if (this.couldBreakBlocks()) {
				for (BlockPos pos : this.collisionData.getHitBlocks()) {
					if (canBreakList.contains(this.world.getBlockState(pos).getMaterial())) {
						this.world.destroyBlock(pos, this.rand.nextFloat() < 0.3f);
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

		@Override
		public void fuuinIntoVessel(Entity vessel, int fuuinTime) {
			if (!this.getBijuManager().isSealed() && this.getHealth() < this.getMaxHealth() * 0.1f
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
				if (hp >= maxhp * 0.1f && this.isFaceDown()) {
					this.setFaceDown(false);
				}
				if (hp <= 150f && !this.isFaceDown()) {
					this.setFaceDown(true);
				}
				if (this.isAIDisabled() && jinchuriki != null && jinchuriki.getHealth() <= 0.0F) {
					this.setNoAI(false);
				}
				if (this.ticksExisted % 100 == 0 && hp > 0.0f && hp < maxhp) {
					this.setHealth(hp + 100f * Math.max(hp / maxhp, 0.1f));
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
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			if (this.isAIDisabled() && this.tailBeastBallTime > 0) {
				if (target instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) target;
					player.sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", this.tailBeastBallTime / 20), true);
				}
				return;
			}

			if (EntityTailBeastBall.spawn(this, 14f, 1000f)) {
				this.setSwingingArms(true);
				this.tailBeastBallTime = BIJUDAMA_CD;
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
			return this.getHealth() >= this.getMaxHealth() * 0.4f;
		}

		@Override
		public boolean isNonBoss() {
			return false;
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
		private final int buildupTime = 100;
		private float maxScale;
		private float maxDamage;
		private boolean shooterAIDisabled;

		public EntityTailBeastBall(World worldIn) {
			super(worldIn);
			this.setOGSize(0.25F, 0.25F);
			this.setEntityScale(0.01f);
			this.setWaterSlowdown(0.98f);
		}

		public EntityTailBeastBall(EntityLivingBase shooter, float maxscale, float maxdamage) {
			super(shooter);
			this.setOGSize(0.25F, 0.25F);
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

		private void setBuildupPosition() {
			Vec3d vec3d = this.shootingEntity instanceof Base ? ((Base)this.shootingEntity).getPositionMouth()
			 : Vec3d.fromPitchYaw(this.shootingEntity.rotationPitch, this.shootingEntity.rotationYawHead)
			 .scale(this.shootingEntity.width * 1.5).add(this.shootingEntity.getPositionEyes(1.0f));
			this.setPosition(vec3d.x, vec3d.y, vec3d.z);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null && !this.isDead) {
				if (this.ticksExisted <= this.buildupTime) {
					if (this.ticksExisted == 1) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:bijudama")), 10f, 1f);
					}
					this.setBuildupPosition();
					this.setEntityScale(this.maxScale * (float)this.ticksExisted / this.buildupTime);
					if (this.ticksExisted <= this.buildupTime - 40) {
						Particles.spawnParticle(this.world, Particles.Types.HOMING_ORB, this.posX, this.posY + this.height / 2, this.posZ,
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
					}
				} else if (!this.isLaunched()) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:nagiharai")), 10f, 1f);
					Vec3d vec = this.shootingEntity.getLookVec();
					this.shoot(vec.x, vec.y, vec.z, 1.05F, 0.0F);
					if (this.shootingEntity instanceof Base) {
						((Base)this.shootingEntity).setSwingingArms(false);
					}
				}
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

		public static boolean spawn(EntityLivingBase summonerIn, float maxscale, float maxdamage) {
			double chakraUsage = 100d * maxscale;
			EntityLivingBase user = null;
			if (summonerIn instanceof Base) {
				user = ((Base)summonerIn).getBijuManager().getJinchurikiPlayer();
			}
			if (user == null) {
				user = summonerIn;
			}
			if ((user instanceof Base && ((Base)user).consumeHealthAsChakra((float)chakraUsage * 0.2f))
			 || (user != null && net.narutomod.Chakra.pathway(user).consume(chakraUsage))) {
				return summonerIn.world.spawnEntity(new EntityTailBeastBall(summonerIn, maxscale, maxdamage));
			}
			return false;
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
					if (spawn(entity, cd.power, cd.power * 70f)) {
						cd.cooldown = entity.ticksExisted + 100;
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
	    private float leapStrength;
	    private double leapRange;
	
	    public AILeapAtTarget(EntityLiving leapingEntity, double range, float strength) {
	        this.leaper = leapingEntity;
	        this.leapRange = range;
	        this.leapStrength = strength;
	        this.setMutexBits(5);
	    }
	
	 	@Override
	    public boolean shouldExecute() {
			this.leapTarget = this.leaper.getAttackTarget();
			if (this.leapTarget != null) {
				double d0 = this.leaper.getDistanceSq(this.leapTarget);
				double d1 = this.leaper.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
				//return d0 >= 64d && d0 <= d1 * d1 && this.leaper.onGround && this.leaper.getRNG().nextInt(5) == 0;
				return (d0 > 64d || d0 > d1 * d1) && d0 <= this.leapRange * this.leapRange 
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
	    	float f = MathHelper.sqrt(d0 * d0 + d1 * d1);
	    	if ((double)f >= 1.0E-4D) {
	    		this.leaper.motionX += d0 / (double)f * (double)this.leapStrength * 0.4D + this.leaper.motionX * 0.2D;
	    		this.leaper.motionZ += d1 / (double)f * (double)this.leapStrength * 0.4D + this.leaper.motionZ * 0.2D;
	    	}
	    	this.leaper.motionY = (double)this.leapStrength;
	        this.leaper.rotationYaw = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
	    }
	}

	public static class NavigateGround extends PathNavigateGround {
		protected Base baseEntity; 
		private BlockPos targetPos;
		//private int stuckCount;
		//private int lastTimeAtPathIndex;

		public NavigateGround(Base entityLivingIn, World worldIn) {
			super(entityLivingIn, worldIn);
			this.baseEntity = entityLivingIn;
		}

		@Override
		protected PathFinder getPathFinder() {
			return null;
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
		public void onUpdateNavigation() {
			++this.totalTicks;
//debugPath("++++++ 1: ");
			if (!this.noPath()) {
				this.checkForStuck(this.getEntityPosition());
//debugPath("++++++ 2: ");
				if (!this.noPath()) {
					Vec3d vec3d2 = this.currentPath.getPosition(this.entity);
					if (this.distanceTo(vec3d2.x, vec3d2.y, vec3d2.z) < 0.5d * (this.entity.width + 1.0d)) {
						this.currentPath.incrementPathIndex();
					} else {
		                this.entity.getMoveHelper().setMoveTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
					}
				}
			}
		}

		/*private void debugPath(String str) {
			String s = str + "currentPath:";
			if (currentPath != null) {
				for (int i = 0; i < currentPath.getCurrentPathLength(); i++) {
					s = s+" ("+currentPath.getPathPointFromIndex(i)+")";
				}
				s += ", index:"+currentPath.getCurrentPathIndex();
			} else {
				s += "nul";
			}
			System.out.println(s);
		}

		@Override
		public boolean setPath(@Nullable Path pathentityIn, double speedIn) {
			if (super.setPath(pathentityIn, speedIn)) {
this.debugPath(">>>>>> pos:"+this.entity.getPosition()+", ");
				//this.lastTimeAtPathIndex = this.totalTicks;
				//this.stuckCount = 0;
				return true;
			} else {
				System.out.println(">>>>>> nul");
			}
			return false;
		}

		@Override
		protected void checkForStuck(Vec3d positionVec3) {
			super.checkForStuck(positionVec3);
			if (this.noPath()) {
				this.stuckCount++;
			} else {
				Vec3d vec = this.currentPath.getPosition(this.entity);
System.out.println("====== totalTicks:"+totalTicks+", lastTimeAtPathIndex:"+lastTimeAtPathIndex+", distanceTo:"+this.distanceTo(vec.x, vec.y, vec.z));
				if (this.totalTicks - this.lastTimeAtPathIndex > (int)this.distanceTo(vec.x, vec.y, vec.z) * 5) {
					this.stuckCount++;
					this.clearPath();
				}
			}
		}*/

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

	public static class MoveHelper extends EntityMoveHelper {
		private Base baseEntity;
		
		public MoveHelper(Base entity) {
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
					double d5 = this.entity.posY;
					for (EnumFacing face : EnumFacing.HORIZONTALS) {
						for (BlockPos pos : this.baseEntity.collisionData.hitsOnSide(face)) {
							double d6 = this.getHighestSolidTop(this.entity.world, pos);
							if (d6 > d5) {
								d5 = d6;
							}
						}
					}
					if (d5 - this.entity.posY > 2d * this.entity.height || this.baseEntity.collisionData.hitOnSide(EnumFacing.UP)) {
						if (this.baseEntity.collisionData.hitOnAxis(EnumFacing.Axis.X)) {
							d0 = 0.0d;
						}
						if (this.baseEntity.collisionData.hitOnAxis(EnumFacing.Axis.Z)) {
							d1 = 0.0d;
						}
					} else if (this.entity.onGround) {
						this.entity.motionY = 0.8d + (d5 - this.entity.posY) * 0.1d;
					} else {
						this.entity.motionY += 0.1d;
					}
				}
	            float f = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
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
					}
				}
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderTailBeastBall extends Render<EntityTailBeastBall> {
			private final ResourceLocation BIJUDAMA_TEXTURE = new ResourceLocation("narutomod:textures/longcube_white.png");
			protected final ModelBase mainModel;

			public RenderTailBeastBall(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.mainModel = new ModelSquareBall();
			}

			@Override
			public void doRender(EntityTailBeastBall entity, double x, double y, double z, float entityYaw, float partialTicks) {
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				//GlStateManager.disableCull();
				float scale = entity.getEntityScale();
				GlStateManager.translate(x, y + (0.125F * scale), z);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.rotate(entity.ticksExisted * 30.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				for (int i = 0; i < 6; i++) {
					GlStateManager.rotate(entity.getRNG().nextFloat() * 30f, 0f, 1f, 0f);
					GlStateManager.rotate(entity.getRNG().nextFloat() * 30f, 1f, 1f, 0f);
					this.mainModel.render(entity, 0.0F, 0.0F, partialTicks + entity.ticksExisted, 0.0F, 0.0F, 0.0625F);
				}
				GlStateManager.enableLighting();
				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				//GlStateManager.enableCull();
				GlStateManager.popMatrix();
				//super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityTailBeastBall entity) {
				return BIJUDAMA_TEXTURE;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelSquareBall extends ModelBase {
			private final ModelRenderer core;
			private final ModelRenderer shell;

			public ModelSquareBall() {
				textureWidth = 32;
				textureHeight = 32;

				core = new ModelRenderer(this);
				core.setRotationPoint(0.0F, 0.0F, 0.0F);
				ModelRenderer cube = new ModelRenderer(this);
				cube.setRotationPoint(0.0F, 0.0F, 0.0F);
				core.addChild(cube);
				cube.cubeList.add(new ModelBox(cube, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
				cube = new ModelRenderer(this);
				cube.setRotationPoint(0.0F, 0.0F, 0.0F);
				core.addChild(cube);
				setRotationAngle(cube, 0.0F, 0.0F, 0.7854F);
				cube.cubeList.add(new ModelBox(cube, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
				cube = new ModelRenderer(this);
				cube.setRotationPoint(0.0F, 0.0F, 0.0F);
				core.addChild(cube);
				setRotationAngle(cube, 0.0F, -0.7854F, 0.0F);
				cube.cubeList.add(new ModelBox(cube, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
				cube = new ModelRenderer(this);
				cube.setRotationPoint(0.0F, 0.0F, 0.0F);
				core.addChild(cube);
				setRotationAngle(cube, -0.7854F, 0.0F, 0.0F);
				cube.cubeList.add(new ModelBox(cube, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));

				shell = new ModelRenderer(this);
				shell.setRotationPoint(0.0F, 0.0F, 0.0F);
				cube = new ModelRenderer(this);
				cube.setRotationPoint(0.0F, 0.0F, 0.0F);
				shell.addChild(cube);
				cube.cubeList.add(new ModelBox(cube, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.1F, false));
				cube = new ModelRenderer(this);
				cube.setRotationPoint(0.0F, 0.0F, 0.0F);
				shell.addChild(cube);
				setRotationAngle(cube, 0.0F, 0.0F, 0.7854F);
				cube.cubeList.add(new ModelBox(cube, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.1F, false));
				cube = new ModelRenderer(this);
				cube.setRotationPoint(0.0F, 0.0F, 0.0F);
				shell.addChild(cube);
				setRotationAngle(cube, 0.0F, -0.7854F, 0.0F);
				cube.cubeList.add(new ModelBox(cube, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.1F, false));
				cube = new ModelRenderer(this);
				cube.setRotationPoint(0.0F, 0.0F, 0.0F);
				shell.addChild(cube);
				setRotationAngle(cube, -0.7854F, 0.0F, 0.0F);
				cube.cubeList.add(new ModelBox(cube, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.1F, false));
			}

			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.color(0.0f, 0.0f, 0.0f, 1.0f);
				core.render(f5);
				//shell.rotateAngleY += 0.52359876F;
				//shell.rotateAngleZ += 0.52359876F;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
				shell.render(f5);
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
}
