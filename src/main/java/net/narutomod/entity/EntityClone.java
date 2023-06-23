
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.block.material.Material;

import net.narutomod.potion.PotionFeatherFalling;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemOnBody;
import net.narutomod.ElementsNarutomodMod;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class EntityClone extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 121;
	public static final int ENTITYID_RANGED = 122;

	public EntityClone(ElementsNarutomodMod instance) {
		super(instance, 365);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(_Base.class, renderManager -> {
			return ClientRLM.getInstance().new RenderClone(renderManager);
		});
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new AITaskHook());
	}

	// mobs will attack instances of Base but won't attack instances of _Base
	public static abstract class Base extends _Base {
		public Base(World world) {
			super(world);
		}

		public Base(EntityLivingBase summonerIn) {
			super(summonerIn);
		}
	}
	
	public static abstract class _Base extends EntityCreature {
		private static final DataParameter<Integer> SUMMONER_ID = EntityDataManager.<Integer>createKey(_Base.class, DataSerializers.VARINT);
		private static final DataParameter<Float> MODEL_SCALE = EntityDataManager.<Float>createKey(_Base.class, DataSerializers.FLOAT);
		private EntityLivingBase summoner;
		private int collectedXPPoints;
		protected boolean shouldDefendSummoner;

		public _Base(World world) {
			super(world);
			this.setSize(0.6f, 1.8f);
			this.experienceValue = 0;
			this.isImmuneToFire = false;
			this.setNoAI(false);
			this.enablePersistence();
			this.shouldDefendSummoner = true;
			for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				this.setDropChance(slot, 0f);
			}
		}

		public _Base(EntityLivingBase summonerIn) {
			this(summonerIn.world);
			this.setSummoner(summonerIn);
			this.setCustomNameTag(summonerIn.getName());
			this.setLeftHanded(summonerIn.getPrimaryHand() == EnumHandSide.LEFT);
			for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
				this.setItemStackToSlot(entityequipmentslot, summoner.getItemStackFromSlot(entityequipmentslot).copy());
			}
			this.prevRotationYaw = this.prevRenderYawOffset = this.renderYawOffset = summonerIn.rotationYaw;
			this.rotationYawHead = this.prevRotationYawHead = summonerIn.rotationYawHead;
			this.copyLocationAndAnglesFrom(summonerIn);
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			PathNavigateGround navi = new EntityNinjaMob.NavigateGround(this, worldIn);
			navi.setCanSwim(true);
			return navi;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SUMMONER_ID, Integer.valueOf(-1));
			this.getDataManager().register(MODEL_SCALE, Float.valueOf(1f));
		}

		@Nullable
		public EntityLivingBase getSummoner() {
			if (!this.world.isRemote)
				return this.summoner;
	    	Entity e = this.world.getEntityByID(((Integer)this.getDataManager().get(SUMMONER_ID)).intValue());
	    	return e instanceof EntityLivingBase ? (EntityLivingBase)e : null;
		}

		protected void setSummoner(EntityLivingBase entity) {
			this.summoner = entity;
			this.getDataManager().set(SUMMONER_ID, Integer.valueOf(entity.getEntityId()));
		}

		protected float getScale() {
			return ((Float) this.getDataManager().get(MODEL_SCALE)).floatValue();
		}

		protected void setScale(float scale) {
			this.getDataManager().set(MODEL_SCALE, Float.valueOf(scale));
			this.setSize(0.6f * scale, 1.8f * scale);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.25d, true));
			//this.tasks.addTask(2, new AIFollowSummoner(this, 0.6d, 3.0F));
			this.tasks.addTask(5, new EntityAILookIdle(this));
		}

		@Override
		public float getRenderSizeModifier() {
			return this.getScale();
		}
		
		@Override
		public EnumCreatureAttribute getCreatureAttribute() {
			return EnumCreatureAttribute.UNDEFINED;
		}

		@Override
		protected boolean canDespawn() {
			return false;
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		protected boolean sameSummoner(_Base entity) {
			return this.getSummoner() != null && this.getSummoner().equals(entity.getSummoner());
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return entityIn.equals(this.getSummoner()) || (entityIn instanceof _Base && this.sameSummoner((_Base)entityIn));
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean isInvisibleToPlayer(EntityPlayer player) {
			return super.isInvisibleToPlayer(player) && !this.isOnSameTeam(player);
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
			return null;
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		private void defendSummoner() {
			if (!this.isAIDisabled() && this.shouldDefendSummoner) {
				EntityLivingBase target = null;
				if (this.summoner.getAttackingEntity() != null) {
					target = this.summoner.getAttackingEntity();
				}
				if (target == null && this.summoner.getLastAttackedEntity() != null 
				 && !this.isOnSameTeam(this.summoner.getLastAttackedEntity())
				 && this.summoner.ticksExisted - this.summoner.getLastAttackedEntityTime() < 200) {
					target = this.summoner.getLastAttackedEntity();
				}
				if (target != null && EntityAITarget.isSuitableTarget(this, target, false, false)) {
					this.setAttackTarget(target);
				} else if (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive()) {
					this.setAttackTarget(null);
				}
			}
		}

		@Override
		protected void updateAITasks() {
			if (this.summoner != null) {
				this.defendSummoner();
			}
			super.updateAITasks();
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			return ProcedureUtils.attackEntityAsMob(this, entityIn);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.summoner instanceof EntityPlayer) {
				((EntityPlayer)this.summoner).addExperience(this.collectedXPPoints);
			}
		}

		@Override
		public void onKillEntity(EntityLivingBase entityIn) {
			if (!this.world.isRemote && entityIn instanceof EntityLiving && this.summoner instanceof EntityPlayer) {
				int i = 0;
				try {
					Field xpValue = ProcedureUtils.getFieldByIndex(entityIn.getClass(), EntityLiving.class, 2);
					if (xpValue.getType() == int.class)
						i = xpValue.getInt(entityIn);
				} catch (Exception e) {
					throw new RuntimeException("experienceValue");
				}
	            for (ItemStack stack : ((EntityLiving)entityIn).getArmorInventoryList()) {
	                if (!stack.isEmpty()) 
	                    i += 1 + this.rand.nextInt(3);
	            }
	            for (ItemStack stack : ((EntityLiving)entityIn).getHeldEquipment()) {
	                if (!stack.isEmpty())
	                    i += 1 + this.rand.nextInt(3);
	            }
                while (i > 0) {
                    int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.world.spawnEntity(new ModifiedXPOrb(this.world, entityIn.posX, entityIn.posY, entityIn.posZ, j));
                }
			}
		}

		@Override
		public void onLivingUpdate() {
			this.updateArmSwingProgress();
			super.onLivingUpdate();
		}

		@Override
		public void onUpdate() {
			if (!this.world.isRemote 
			 && (this.summoner == null || !this.summoner.isEntityAlive() || this.summoner.isPlayerSleeping() 
			  || ProcedureUtils.isPlayerDisconnected(this.summoner))) {
				this.setDead();
			}
			if (this.world.isRemote && this.height != this.getScale() * 1.8f) {
				this.setSize(0.6f * this.getScale(), 1.8f * this.getScale());
			}
			if (!this.world.isRemote && this.ticksExisted % 200 == 1) {
				this.addPotionEffect(new PotionEffect(PotionFeatherFalling.potion, 202, 1, false, false));
			}
			BlockPos pos = new BlockPos(this);
			if (this.world.getBlockState(pos).getMaterial() == Material.WATER
			 && this.world.getBlockState(pos.up()).getMaterial() != Material.WATER) {
				this.motionY = 0.01d;
				this.onGround = true;
			}
			super.onUpdate();
		}

		@Override
		public int getMaxFallHeight() {
			return 12;
		}

		@Override
		protected boolean canDropLoot() {
			return false;
		}

		@Override
		protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		}

		@Override
		protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
		}
	}

	public static class ModifiedXPOrb extends EntityXPOrb {
		private _Base closestClone;
		private int xpTargetColor;

		public ModifiedXPOrb(World world) {
			super(world);
		}

		public ModifiedXPOrb(World world, double x, double y, double z, int expValue) {
			super(world, x, y, z, expValue);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();

	        if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
	            if (this.closestClone == null || this.closestClone.getDistanceSq(this) > 64.0D) {
	                this.closestClone = (_Base)this.world.findNearestEntityWithinAABB(_Base.class, this.getEntityBoundingBox().grow(8.0D), this);
	            }	
	            this.xpTargetColor = this.xpColor;
	        }
	        if (this.closestClone != null) {
	            double d1 = (this.closestClone.posX - this.posX) / 8.0D;
	            double d2 = (this.closestClone.posY + (double)this.closestClone.getEyeHeight() / 2.0D - this.posY) / 8.0D;
	            double d3 = (this.closestClone.posZ - this.posZ) / 8.0D;
	            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
	            double d5 = 1.0D - d4;
	            if (d5 > 0.0D) {
	                d5 = d5 * d5;
	                this.motionX += d1 / d4 * d5 * 0.1D;
	                this.motionY += d2 / d4 * d5 * 0.1D;
	                this.motionZ += d3 / d4 * d5 * 0.1D;
	            }
	        }
	        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		}

		@Override
		public boolean canBePushed() {
			return true;
		}

		@Override
		public void applyEntityCollision(Entity entityIn) {
			if (!this.world.isRemote && entityIn instanceof _Base) {
				this.setDead();
				((_Base)entityIn).collectedXPPoints += this.xpValue;
				//System.out.println("collect XP: " + ((Base)entityIn).collectedXPPoints);
			}
		}
	}

	public static class AIFollowSummoner extends EntityAIBase {
	    private final EntityLiving entity;
	    protected EntityLivingBase followingEntity;
	    private final double speedModifier;
	    private final PathNavigate navigation;
	    private int timeToRecalcPath;
	    protected final float stopDistance;
	    private float oldWaterCost;
	
	    public AIFollowSummoner(EntityLiving entityIn, double speed, float stopRange) {
	        this.entity = entityIn;
	        this.speedModifier = speed;
	        this.navigation = entityIn.getNavigator();
	        this.stopDistance = stopRange;
	        this.setMutexBits(3);
	    }
	
		@Override
	    public boolean shouldExecute() {
	        EntityLivingBase entitylivingbase = this.getFollowEntity();
	        if (entitylivingbase == null) {
	            return false;
	        } else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).isSpectator()) {
	            return false;
	        } else if (this.entity.getDistanceSq(entitylivingbase) < (double)(this.stopDistance * this.stopDistance)) {
	            return false;
	        } else {
	            this.followingEntity = entitylivingbase;
	            return true;
	        }
	    }

		@Override
	    public boolean shouldContinueExecuting() {
	        return this.followingEntity != null && !this.navigation.noPath() && this.entity.getDistanceSq(this.followingEntity) > (double)(this.stopDistance * this.stopDistance);
	    }
	
		@Override
	    public void startExecuting() {
	        this.timeToRecalcPath = 0;
	        this.oldWaterCost = this.entity.getPathPriority(PathNodeType.WATER);
	        this.entity.setPathPriority(PathNodeType.WATER, 0.0F);
	    }
	
		@Override
	    public void resetTask() {
	        this.followingEntity = null;
	        this.navigation.clearPath();
	        this.entity.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	    }
	
		@Override
	    public void updateTask() {
	        if (this.followingEntity != null) {
	            if (--this.timeToRecalcPath <= 0) {
	                this.timeToRecalcPath = 10;
	                double d = this.entity.getDistance(this.followingEntity);
	                if (d > (double)this.stopDistance) {
	                    if (!this.navigation.tryMoveToEntityLiving(this.followingEntity, this.getSpeed())) {
	                    	Vec3d vec = this.findOpenSpaceTowardsSummoner(ProcedureUtils.getFollowRange(this.entity) / 2);
	                    	if (vec != null) {
	                    		this.entity.setLocationAndAngles(vec.x, vec.y, vec.z, this.entity.rotationYaw, this.entity.rotationPitch);
	                    	}
	                    }
	                } else {
	                    this.navigation.clearPath();
	                    if (d <= (double)this.stopDistance * 0.5d) {
	                    	double d4 = this.followingEntity.posX - this.entity.posX;
	                    	double d5 = this.followingEntity.posZ - this.entity.posZ;
	                    	this.navigation.tryMoveToXYZ(this.entity.posX - d4, this.entity.posY, this.entity.posZ - d5, this.getSpeed());
	                    }
	                }
	            }
	            this.entity.getLookHelper().setLookPositionWithEntity(this.followingEntity, 10.0F, (float)this.entity.getVerticalFaceSpeed());
	        }
	    }

	    @Nullable
	    protected Vec3d findOpenSpaceTowardsSummoner(double maxDistanceToSummoner) {
	    	Vec3d vec = this.entity.getPositionVector().subtract(this.followingEntity.getPositionVector()).normalize().scale(maxDistanceToSummoner);
	    	List<BlockPos> list = ProcedureUtils.getAllAirBlocks(this.entity.world, this.followingEntity.getEntityBoundingBox().expand(vec.x, vec.y, vec.z));
	    	list.sort(new ProcedureUtils.BlockposSorter(this.entity.getPosition()));
	    	for (BlockPos pos : list) {
	    		Material material = this.entity.world.getBlockState(pos.down()).getMaterial();
	    		if ((material.isSolid() || material == material.WATER) && ProcedureUtils.isSpaceOpenToStandOn(this.entity, pos)) {
	    			return new Vec3d(0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ());
	    		}
	    	}
	    	return null;
	    }

	    protected double getSpeed() {
	    	return this.speedModifier;
	    }

	    protected EntityLivingBase getFollowEntity() {
	    	return ((_Base)this.entity).getSummoner();
	    }
	}

    public static class AIFlyControl extends EntityMoveHelper {
        public AIFlyControl(EntityLiving entityIn) {
            super(entityIn);
        }
	
        @Override
        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                this.action = EntityMoveHelper.Action.WAIT;
            	this.entity.setNoGravity(true);
                double d0 = this.posX - this.entity.posX;
                double d1 = this.posY - this.entity.posY;
                double d2 = this.posZ - this.entity.posZ;
                double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                if (d3 >= 0.001D) {
                	float f = (float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue());
                    this.entity.motionX += d0 / d3 * 0.05D * f;
                    this.entity.motionY += d1 / d3 * 0.05D * f;
                    this.entity.motionZ += d2 / d3 * 0.05D * f;
                    if (this.entity.getAttackTarget() == null) {
                        this.entity.rotationYaw = -((float)MathHelper.atan2(this.entity.motionX, this.entity.motionZ)) * (180F / (float)Math.PI);
                        this.entity.renderYawOffset = this.entity.rotationYaw;
                    } else {
                        double d4 = this.entity.getAttackTarget().posX - this.entity.posX;
                        double d5 = this.entity.getAttackTarget().posZ - this.entity.posZ;
                        this.entity.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
                        this.entity.renderYawOffset = this.entity.rotationYaw;
                    }
                } else {
                    this.entity.motionX *= 0.5D;
                    this.entity.motionY *= 0.5D;
                    this.entity.motionZ *= 0.5D;
                }
            } else {
            	this.entity.setNoGravity(false);
            	this.entity.setMoveVertical(0.0F);
            	this.entity.setMoveForward(0.0F);
            }
        }
    }

    public class AITaskHook {
    	@SubscribeEvent
    	public void onEntitySpawn(EntityJoinWorldEvent event) throws Exception {
    		Entity entity = event.getEntity();
    		if (entity instanceof EntityLiving) {
    			this.addCloneToAITargetTasks(((EntityLiving)entity).targetTasks);
    		}    		
    	}
 
		public void addCloneToAITargetTasks(EntityAITasks tasks) throws Exception {
			EntityAITasks.EntityAITaskEntry entry = null;
			for (EntityAITasks.EntityAITaskEntry aitaskentry : tasks.taskEntries) {
				if (aitaskentry.action instanceof EntityAINearestAttackableTarget) {
					Object targetClass = ProcedureUtils.getFieldByIndex(aitaskentry.action.getClass(), EntityAINearestAttackableTarget.class, 0).get(aitaskentry.action);
					if (targetClass.equals(Base.class)) {
						return;
					//} else if (targetClass.equals(EntityPlayer.class)) {
					} else if (EntityPlayer.class.isAssignableFrom((Class)targetClass)) {
						entry = aitaskentry;
					}
				} else if (aitaskentry.action instanceof EntityAIFindEntityNearest) {
					Object targetClass = ProcedureUtils.getFieldByIndex(aitaskentry.action.getClass(), EntityAIFindEntityNearest.class, 5).get(aitaskentry.action);
					if (targetClass.equals(Base.class)) {
						return;
					} else if (EntityPlayer.class.isAssignableFrom((Class)targetClass)) {
						entry = aitaskentry;
					}
				} else if (aitaskentry.action instanceof EntityAIFindEntityNearestPlayer) {
					entry = aitaskentry;
				}
			}
			if (entry != null) {
				if (entry.action instanceof EntityAINearestAttackableTarget) {
					Field taskOwner = ProcedureUtils.getFieldByIndex(entry.action.getClass(), EntityAITarget.class, 0);
					Field checkSight = ProcedureUtils.getFieldByIndex(entry.action.getClass(), EntityAITarget.class, 1);
					Field nearbyOnly = ProcedureUtils.getFieldByIndex(entry.action.getClass(), EntityAITarget.class, 2);
					tasks.addTask(entry.priority-1, new EntityAINearestAttackableTarget(
					  (EntityCreature)taskOwner.get(entry.action), Base.class, checkSight.getBoolean(entry.action), 
					  nearbyOnly.getBoolean(entry.action)));
					//System.out.println("added task EntityAINearestAttackableTarget for " + taskOwner.get(entry.action));
				} else if (entry.action instanceof EntityAIFindEntityNearestPlayer) {
					Field taskOwner = ProcedureUtils.getFieldByIndex(entry.action.getClass(), EntityAIFindEntityNearestPlayer.class, 1);
					tasks.addTask(entry.priority-1, new EntityAIFindEntityNearest((EntityLiving)taskOwner.get(entry.action), Base.class));
					//System.out.println("added task EntityAIFindEntityNearest from EntityAIFindEntityNearestPlayer for " + taskOwner.get(entry.action));
				} else if (entry.action instanceof EntityAIFindEntityNearest) {
					Field taskOwner = ProcedureUtils.getFieldByIndex(entry.action.getClass(), EntityAIFindEntityNearest.class, 1);
					tasks.addTask(entry.priority-1, new EntityAIFindEntityNearest((EntityLiving)taskOwner.get(entry.action), Base.class));
					//System.out.println("added task EntityAIFindEntityNearest for " + taskOwner.get(entry.action));
				}
			}
		}
    }

    public static class ClientRLM {
    	private static ClientRLM instance;

    	public ClientRLM() {
    		instance = this;
    	}

    	public static ClientRLM getInstance() {
    		if (instance == null) {
    			new ClientRLM();
    		}
    		return instance;
    	}

		@SideOnly(Side.CLIENT)
		public class RenderClone<T extends _Base> extends RenderLivingBase<T> {
			private final ModelClone altModel = new ModelClone(0.0F, true);
			//private boolean smallArms;
	
		    public RenderClone(RenderManager renderManager) {
		        super(renderManager, new ModelClone(0.0F, false), 0.5F);
		        //this.smallArms = false;
		        this.addLayer(new BipedArmorLayer(this));//this.addLayer(PlayerRender.getInstance().new LayerArmorCustom(this));
		        this.addLayer(new net.minecraft.client.renderer.entity.layers.LayerHeldItem(this));
		        //this.addLayer(new net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head(this));
		        //this.addLayer(new net.minecraft.client.renderer.entity.layers.LayerCape(this));
		        this.addLayer(new net.minecraft.client.renderer.entity.layers.LayerCustomHead(((ModelBiped)this.getMainModel()).bipedHead));
		        this.addLayer(new net.minecraft.client.renderer.entity.layers.LayerElytra(this));
		        //this.addLayer(new net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder(renderManager));
		        this.addLayer(new LayerInventoryItem(this));
		    }

		    @Override
		    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		    	this.setPose(entity);
		    	super.doRender(entity, x, y, z, entityYaw, partialTicks);
		    }

		    private void setPose(T entity) {
		    	ModelBiped model = (ModelBiped)this.getMainModel();
	            ItemStack itemstack = entity.getHeldItemMainhand();
	            ItemStack itemstack1 = entity.getHeldItemOffhand();
	            ModelBiped.ArmPose mainhandpose = ModelBiped.ArmPose.EMPTY;
	            ModelBiped.ArmPose offhandpose = ModelBiped.ArmPose.EMPTY;
	            if (!itemstack.isEmpty()) {
	                mainhandpose = ModelBiped.ArmPose.ITEM;
	                if (entity.getItemInUseCount() > 0) {
	                    EnumAction enumaction = itemstack.getItemUseAction();
	                    if (enumaction == EnumAction.BLOCK) {
	                        mainhandpose = ModelBiped.ArmPose.BLOCK;
	                    } else if (enumaction == EnumAction.BOW) {
	                        mainhandpose = ModelBiped.ArmPose.BOW_AND_ARROW;
	                    }
	                }
	            }
	            if (!itemstack1.isEmpty()) {
	                offhandpose = ModelBiped.ArmPose.ITEM;
	                if (entity.getItemInUseCount() > 0) {
	                    EnumAction enumaction1 = itemstack1.getItemUseAction();
	                    if (enumaction1 == EnumAction.BLOCK) {
	                        offhandpose = ModelBiped.ArmPose.BLOCK;
	                    } else if (enumaction1 == EnumAction.BOW) {
	                        offhandpose = ModelBiped.ArmPose.BOW_AND_ARROW;
	                    }
	                }
	            }
	            if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
	                model.rightArmPose = mainhandpose;
	                model.leftArmPose = offhandpose;
	            } else {
	                model.rightArmPose = offhandpose;
	                model.leftArmPose = mainhandpose;
	            }
		    }

		    @Override
		    protected void renderLayers(T entity, float f0, float f1, float f2, float f3, float f4, float f5, float f6) {
		    	if (!entity.isInvisible() || !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player)) {
		    		super.renderLayers(entity, f0, f1, f2, f3, f4, f5, f6);
		    	}
		    }
		
		    @Override
		    public ResourceLocation getEntityTexture(T entity) {
		    	EntityLivingBase summoner = entity.getSummoner();
		        if (summoner instanceof AbstractClientPlayer) {
		        	AbstractClientPlayer clientPlayer = (AbstractClientPlayer)summoner;
		        	if (clientPlayer.getSkinType().equals("slim")) {
		        		this.mainModel = this.altModel;
		        	}
		        	return clientPlayer.getLocationSkin();
		        }
		        return null;
		    }
		
		    @Override
		    public void transformHeldFull3DItemLayer() {
		        GlStateManager.translate(0.0F, 0.1875F, 0.0F);
		    }
	
		    @Override
		    protected void preRenderCallback(T entity, float partialTickTime) {
		        if (entity.getSummoner() instanceof AbstractClientPlayer) {
			        float f = 0.9375F;
			        GlStateManager.scale(f, f, f);
		        }
		        float f = entity.getScale();
		        if (f != 1.0f) {
			        GlStateManager.scale(f, f, f);
		        }
		    }
		}
	
		@SideOnly(Side.CLIENT)
	    public class BipedArmorLayer extends net.minecraft.client.renderer.entity.layers.LayerBipedArmor {
	    	private final RenderLivingBase<?> renderer;
	
	    	public BipedArmorLayer(RenderLivingBase<?> rendererIn) {
	    		super(rendererIn);
	    		this.renderer = rendererIn;
	    	}
	
	    	@Override
	    	public void doRenderLayer(EntityLivingBase entityIn, float limbSwing, float f1, float f2, float f3, float f4, float f5, float f6) {
	    		GlStateManager.enableBlend();
	    		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	    		super.doRenderLayer(entityIn, limbSwing * 1.8F / entityIn.height, f1, f2, f3, f4, f5, f6);
	    		GlStateManager.disableBlend();
	    	}
	
	    	@Override
	    	protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slotIn) {
	    		model.setVisible(false);
	    		ModelBiped rendererModel = (ModelBiped)this.renderer.getMainModel();
	    		switch (slotIn) {
		            case HEAD:
		            	if (rendererModel.bipedHead.showModel) {
			                model.bipedHead.showModel = true;
			                model.bipedHeadwear.showModel = true;
		            	}
		                break;
		            case CHEST:
		            	if (rendererModel.bipedBody.showModel) {
			                model.bipedBody.showModel = true;
		            	}
		            	if (rendererModel.bipedRightArm.showModel) {
			                model.bipedRightArm.showModel = true;
		            	}
		            	if (rendererModel.bipedLeftArm.showModel) {
			                model.bipedLeftArm.showModel = true;
		            	}
		                break;
		            case LEGS:
		            	if (rendererModel.bipedBody.showModel) {
			                model.bipedBody.showModel = true;
		            	}
		            	if (rendererModel.bipedRightLeg.showModel) {
			                model.bipedRightLeg.showModel = true;
		            	}
		            	if (rendererModel.bipedLeftLeg.showModel) {
			                model.bipedLeftLeg.showModel = true;
		            	}
		                break;
		            case FEET:
		            	if (rendererModel.bipedRightLeg.showModel) {
		                	model.bipedRightLeg.showModel = true;
		            	}
		            	if (rendererModel.bipedLeftLeg.showModel) {
		                	model.bipedLeftLeg.showModel = true;
		            	}
		        }
	    	}
	    }
	
		@SideOnly(Side.CLIENT)
		public class LayerInventoryItem implements net.minecraft.client.renderer.entity.layers.LayerRenderer<_Base> {
			private final RenderClone renderer;
	
			public LayerInventoryItem(RenderClone rendererIn) {
				this.renderer = rendererIn;
			}
	
			@Override
			public void doRenderLayer(_Base entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				EntityLivingBase e = entityIn.getSummoner();
				if (e instanceof AbstractClientPlayer) {
					AbstractClientPlayer summoner = (AbstractClientPlayer)e;
					for (int i = 0; i < summoner.inventory.mainInventory.size(); i++) {
						ItemStack stack = summoner.inventory.mainInventory.get(i);
						if (stack.getItem() instanceof ItemOnBody.Interface) {
							ItemOnBody.Interface item = (ItemOnBody.Interface)stack.getItem();
							if (item.showSkinLayer()) {
								this.renderSkinLayer(stack, entityIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
							}
							if (item.showOnBody() != ItemOnBody.BodyPart.NONE && !entityIn.getHeldItemMainhand().isItemEqualIgnoreDurability(stack)) {
								this.renderItemOnBody(stack, entityIn, item.showOnBody());
							}
						}
					}
				}
			}
	
			private void renderSkinLayer(ItemStack stack, _Base entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				ModelBiped model = stack.getItem().getArmorModel(entityIn, stack, EntityEquipmentSlot.HEAD, new ModelBiped(1.0F));
				if (model != null) {
					String s = stack.getItem().getArmorTexture(stack, entityIn, EntityEquipmentSlot.HEAD, null);
					if (s != null) {
						model.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
						this.renderer.bindTexture(new ResourceLocation(s));
						model.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
					}
				}
			}
	
			private void renderItemOnBody(ItemStack stack, _Base entityIn, ItemOnBody.BodyPart bodypart) {
				Vec3d offset = ((ItemOnBody.Interface)stack.getItem()).getOffset();
				GlStateManager.pushMatrix();
				ModelBiped model = (ModelBiped)this.renderer.getMainModel();
				switch (bodypart) {
					case HEAD:
						model.bipedHead.postRender(0.0625F);
						break;
					case TORSO:
						model.bipedBody.postRender(0.0625F);
						break;
					case RIGHT_ARM:
						model.bipedRightArm.postRender(0.0625F);
						break;
					case LEFT_ARM:
						model.bipedLeftArm.postRender(0.0625F);
						break;
					case RIGHT_LEG:
						model.bipedRightLeg.postRender(0.0625F);
						break;
					case LEFT_LEG:
						model.bipedLeftLeg.postRender(0.0625F);
						break;
				}
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.translate(offset.x, -0.25F + offset.y, offset.z);
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(0.625F, -0.625F, -0.625F);
				Minecraft.getMinecraft().getItemRenderer().renderItem(entityIn, stack, ItemCameraTransforms.TransformType.HEAD);
				GlStateManager.popMatrix();
			}
	
			@Override
			public boolean shouldCombineTextures() {
				return false;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelClone extends ModelBiped {
		    //private ModelRenderer bipedLeftArmwear;
		    //private ModelRenderer bipedRightArmwear;
		    //private ModelRenderer bipedLeftLegwear;
		    //private ModelRenderer bipedRightLegwear;
		    //private ModelRenderer bipedBodyWear;
		    //private final ModelRenderer bipedCape;
		    //private final ModelRenderer bipedDeadmau5Head;
		    private final boolean smallArms;
		
		    public ModelClone(float modelSize, boolean smallArmsIn) {
		        super(modelSize, 0.0F, 64, 64);
		        this.smallArms = smallArmsIn;
		
		        if (smallArmsIn) {
		            this.bipedLeftArm = new ModelRenderer(this);
		            //this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
		            this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize, false));
					this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F, false));
		            this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
		            this.bipedRightArm = new ModelRenderer(this);
		            //this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
					this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize, false));
					this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F, false));
		            this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
		            //this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
		            //this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
		            //this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
		            //this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
		            //this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
		            //this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
		        } else {
		            this.bipedLeftArm = new ModelRenderer(this);
		            //this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
					this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize, false));
					this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F, false));
		            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		            //this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
		            //this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
		            //this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
		            this.bipedRightArm = new ModelRenderer(this);
		            //this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
					this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize, false));
					this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F, false));
		            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		            //this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
		            //this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
		            //this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
		        }
		
		        this.bipedLeftLeg = new ModelRenderer(this);
		        //this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
				this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize, false));
				this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F, false));
		        this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		        //this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
		        //this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
		        //this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
				this.bipedRightLeg = new ModelRenderer(this);
				this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize, false));
				this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F, false));
				this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		        //this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
		        //this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
		        //this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
				this.bipedBody = new ModelRenderer(this);
				this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize, false));
				this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F, false));
				this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		        //this.bipedBodyWear = new ModelRenderer(this, 16, 32);
		        //this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
		        //this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
		    }
	
			@Override
			public void setRotationAngles(float limbSwing, float f1, float f2, float f3, float f4, float f5, Entity entityIn) {
				super.setRotationAngles(limbSwing * 1.8F / entityIn.height, f1, f2, f3, f4, f5, entityIn);
			}
	
		    /*@Override
		    public void setVisible(boolean visible) {
		    	super.setVisible(visible);
		        this.bipedLeftArmwear.showModel = visible;
		        this.bipedRightArmwear.showModel = visible;
		        this.bipedLeftLegwear.showModel = visible;
		        this.bipedRightLegwear.showModel = visible;
		        this.bipedBodyWear.showModel = visible;
		        //this.bipedCape.showModel = visible;
		        //this.bipedDeadmau5Head.showModel = visible;
		    }*/
	
		    @Override
		    public void postRenderArm(float scale, EnumHandSide side) {
		        ModelRenderer modelrenderer = this.getArmForSide(side);
		        if (this.smallArms) {
		            float f = 0.5F * (float)(side == EnumHandSide.RIGHT ? 1 : -1);
		            modelrenderer.rotationPointX += f;
		            modelrenderer.postRender(scale);
		            modelrenderer.rotationPointX -= f;
		        } else {
		            modelrenderer.postRender(scale);
		        }
		    }
		}
    }
}
