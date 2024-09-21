
package net.narutomod.entity;

import net.narutomod.item.ItemSenbonArm;
import net.narutomod.item.ItemPoisonSenbon;
import net.narutomod.item.ItemScrollHiruko;
import net.narutomod.item.ItemScroll3rdKazekage;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.Particles;
import net.narutomod.ModConfig;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Biomes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySasori extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 437;
	public static final int ENTITYID_RANGED = 438;

	public EntitySasori(ElementsNarutomodMod instance) {
		super(instance, 868);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "sasori"), ENTITYID).name("sasori").tracker(64, 3, true).egg(-16777216, -65485).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCore.class)
		 .id(new ResourceLocation("narutomod", "sasori_core"), ENTITYID_RANGED).name("sasori_core").tracker(64, 3, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		int i = MathHelper.clamp(ModConfig.SPAWN_WEIGHT_SASORI, 0, 20);
		if (i > 0) {
			EntityRegistry.addSpawn(EntityCustom.class, i, 1, 1, EnumCreatureType.MONSTER,
					Biomes.BEACH, Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.MESA, Biomes.MESA_ROCK,
					Biomes.ICE_PLAINS, Biomes.PLAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.STONE_BEACH,
					Biomes.MUTATED_DESERT, Biomes.MUTATED_MESA, Biomes.MUTATED_MESA_ROCK, Biomes.MUTATED_PLAINS,
					Biomes.MUTATED_SAVANNA, Biomes.MUTATED_SAVANNA_ROCK, Biomes.COLD_BEACH);
		}
		MinecraftForge.EVENT_BUS.register(new AttackHook());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private static final DataParameter<Integer> ROBE_OFF_TICKS = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> BREAKING_TICKS = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Boolean> NO_CORE = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
		private final int BLOCKING_CD = 30;
		private final int SANDGATHERING_CD = 400;
		private final int SANDBULLET_CD = 160;
		private final double chakra4elementals = 200d;
		private int lastBlockTime;
		private EntityCore coreEntity;
		private EntityPuppetHiruko.EntityCustom hirukoEntity;
		private EntityPuppet3rdKazekage.EntityCustom thirdEntity;
		private int senbonArmShootCount;
		private final int bladesOpenTime = 60;
		private boolean thirdScrollUsed;
		private int lastSandBulletTime;
		private int lastSandGatheringTime;
		private int lastElementalJutsuTime;
		private Entity lastElementalJutsu;
		private int fireImmuneTicks;
		private final int breakingProgressEnd = 10;
		private final int breakingEnd = 150;
		private int breakingDirection;
		private boolean canDie;
		private EnumStage hundredStage;
		private EntityPuppetHundred.EntityScroll scroll100Entity;

		public EntityCustom(World world) {
			super(world, 60, 7000d);
			this.setSize(0.525f, 1.75f);
			this.hundredStage = EnumStage.CANNOT_USE;
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemToInventory(new ItemStack(ItemPoisonSenbon.block), 0);
			this.setItemToInventory(new ItemStack(ItemSenbonArm.block), 1);
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ItemScroll3rdKazekage.block, 1));
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(ROBE_OFF_TICKS, Integer.valueOf(-1));
			this.dataManager.register(BREAKING_TICKS, Integer.valueOf(-1));
			this.dataManager.register(NO_CORE, Boolean.valueOf(false));
		}

		private void setRobeOffTicks(int ticks) {
			this.dataManager.set(ROBE_OFF_TICKS, Integer.valueOf(ticks));
		}
	
		public int getRobeOffTicks() {
			return ((Integer)this.getDataManager().get(ROBE_OFF_TICKS)).intValue();
		}

		protected void takeOffRobe(boolean off) {
			this.setRobeOffTicks(off ? 0 : -1);
		}

		protected boolean isRobeOff() {
			return this.getRobeOffTicks() >= 0;
		}

		private void setBreakingTicks(int ticks) {
			this.dataManager.set(BREAKING_TICKS, Integer.valueOf(ticks));
			if (ticks < 0) {
				this.setEntityInvulnerable(false);
				this.breakingDirection = 0;
			}
		}
	
		private int getBreakingTicks() {
			return ((Integer)this.getDataManager().get(BREAKING_TICKS)).intValue();
		}

		private void setBreaking(boolean b) {
			if (b) {
				this.setNoCore(true);
				this.setBreakingTicks(0);
				this.breakingDirection = 1;
				this.setEntityInvulnerable(true);
				Vec3d vec = this.getRevengeTarget() != null ? this.getPositionVector().subtract(this.getRevengeTarget().getPositionVector()) : new Vec3d(this.motionX, this.motionY, this.motionZ);
				this.coreEntity = new EntityCore(this);
				this.coreEntity.shoot(vec.x + (this.rand.nextFloat()-0.5f) * 0.2f, vec.y, vec.z + (this.rand.nextFloat()-0.5f) * 0.2f, 0.98f, 0.0f);
				this.world.spawnEntity(this.coreEntity);
				if (this.lastElementalJutsu != null && !this.lastElementalJutsu.isDead) {
					this.lastElementalJutsu.setDead();
				}
			} else {
				this.setBreakingTicks(-1);
			}
		}

		private void reverseBreak() {
			this.setBreakingTicks(this.breakingProgressEnd);
			this.breakingDirection = -1;
			if (this.coreEntity != null) {
				this.coreEntity.setReturnToOwner();
				Vec3d vec = this.coreEntity.getPositionVector().subtract(this.getPositionVector());
				vec = vec.addVector(0.0d, MathHelper.sqrt(vec.x * vec.x + vec.z * vec.z) * 0.4d, 0.0d).scale(0.1d);
				this.motionX = vec.x;
				this.motionY = vec.y;
				this.motionZ = vec.z;
				this.isAirBorne = true;
			}
		}

		public boolean isBroken() {
			return this.getBreakingTicks() >= 0;
		}

		private void setNoCore(boolean b) {
			this.dataManager.set(NO_CORE, Boolean.valueOf(b));
		}
	
		public boolean hasNoCore() {
			return ((Boolean)this.getDataManager().get(NO_CORE)).booleanValue();
		}

		public boolean actionsHalted() {
			return this.isStandingStill() || this.isBroken() || this.hasNoCore();
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
			if (this.rand.nextFloat() < 0.5f) {
				ItemStack stack = new ItemStack(ItemScrollHiruko.block);
				stack.setItemDamage(10 + this.rand.nextInt((int)EntityPuppetHiruko.EntityCustom.MAXHEALTH - 10));
				this.entityDropItem(stack, 0.0f);
			}
			if (this.rand.nextFloat() < 0.25f) {
				ItemStack stack = new ItemStack(ItemScroll3rdKazekage.block);
				stack.setItemDamage(10 + this.rand.nextInt((int)EntityPuppet3rdKazekage.EntityCustom.MAXHEALTH - 10));
				this.entityDropItem(stack, 0.0f);
			}
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedTactical(this, 0.4d, 60, 12.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.isRidingHiruko() && !EntityCustom.this.actionsHalted();
				}
			});
			this.tasks.addTask(2, new EntityNinjaMob.AIAttackRangedTactical(this, 1.0d, SANDBULLET_CD, 30.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !EntityCustom.this.isRidingHiruko() && !EntityCustom.this.actionsHalted();
				}
			});
			this.tasks.addTask(3, new EntityAIWatchClosest2(this, EntityPlayer.class, 15.0F, 1.0F));
			this.tasks.addTask(4, new EntityAIWander(this, 0.3));
			this.tasks.addTask(5, new EntityAILookIdle(this));
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.targetTasks.addTask(2, new EntityPuppet.AIRidingHurtByTarget(this));
			this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false,
					new Predicate<EntityPlayer>() {
						public boolean apply(@Nullable EntityPlayer p_apply_1_) {
							return p_apply_1_ != null && (ModConfig.AGGRESSIVE_BOSSES || EntityBijuManager.isJinchuriki(p_apply_1_));
						}
					}));
			//this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityMob.class, false, false));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			ItemStack offStack = this.getHeldItemOffhand();
			ItemStack inv1Stack = this.getItemFromInventory(1);
			boolean bowpose = this.getEntityData().getBoolean(NarutomodModVariables.forceBowPose);
			if (this.actionsHalted()) {
				// do nothing
			} else if (this.getAttackTarget() != null) {
				if (this.isRidingHiruko() && this.hirukoEntity.getHealth() < this.hirukoEntity.getMaxHealth() * 0.5f
			 	 && inv1Stack.getItem() == ItemSenbonArm.block && this.rand.nextFloat() < 0.01f) {
					this.swapWithInventory(EntityEquipmentSlot.OFFHAND, 1);
			 	} else if (!this.isRidingHiruko() && !this.thirdScrollUsed) {
			 		if (inv1Stack.getItem() == ItemScroll3rdKazekage.block) {
			 			this.swapWithInventory(EntityEquipmentSlot.OFFHAND, 1);
			 		} else if (offStack.getItem() == ItemScroll3rdKazekage.block) {
			 			if (this.onGround) {
					 		this.swingArm(EnumHand.OFF_HAND);
							BlockPos pos = new BlockPos(this.getAttackTarget().getPositionVector()
							 .subtract(this.getPositionEyes(1f)).normalize().scale(1.5d).add(this.getPositionEyes(1f)));
							for ( ; this.world.isAirBlock(pos); pos = pos.down()) ;
							ItemScroll3rdKazekage.RangedItem.useItem(offStack, this, pos);
							this.standStillFor(30);
							this.thirdScrollUsed = true;
			 			}
			 		} else {
			 			this.thirdScrollUsed = true;
			 		}
				} else if (this.isThirdSummoned() && offStack.getItem() == ItemScroll3rdKazekage.block) {
					this.thirdEntity.setAttackTarget(this.getAttackTarget());
					if (!bowpose) ProcedureSync.EntityNBTTag.setAndSync(this, NarutomodModVariables.forceBowPose, true);
					if (this.isHandActive()) {
						if (offStack.getMaxItemUseDuration() - this.getItemInUseCount() >= 80) {
							this.stopActiveHand();
						}
					} else if (ItemScroll3rdKazekage.GATHERING.jutsu.isActivated(this.thirdEntity)
					 && (this.ticksExisted - this.lastSandGatheringTime) % 60 == 50) {
						((ItemScroll3rdKazekage.RangedItem)offStack.getItem()).executeJutsu(offStack, this, 1f);
					}
				} else if (this.isRobeOff()) {
					switch (this.hundredStage) {
					case CAN_USE:
						this.swingArm(EnumHand.OFF_HAND);
						this.playSound(net.minecraft.init.SoundEvents.BLOCK_CLOTH_PLACE, 1, 1f / (this.rand.nextFloat() * 0.5f + 1f) + 0.5f);
						this.scroll100Entity = new EntityPuppetHundred.EntityScroll(this);
						this.scroll100Entity.setLocationAndAngles(this.posX, 2.5d + this.posY, this.posZ, this.rotationYaw + 90.0f, 0.0f);
						this.world.spawnEntity(this.scroll100Entity);
						this.standStillFor(30);
						ProcedureSync.EntityNBTTag.setAndSync(this, NarutomodModVariables.forceBowPose, false);
						this.hundredStage = EnumStage.OPENING_SCROLL;
						break;
					case OPENING_SCROLL:
						if (this.scroll100Entity != null && this.scroll100Entity.allPuppetsSpawned()) {
							this.hundredStage = EnumStage.USING;
						}
						break;
					case USING:
						if (this.scroll100Entity != null && this.scroll100Entity.allPuppetsDead()) {
							this.hundredStage = EnumStage.USED;
						}
						break;
					case USED:
						this.scroll100Entity = null;
					case CANNOT_USE:
						if (this.lastElementalJutsu != null && !this.lastElementalJutsu.isDead) {
							if (!bowpose) ProcedureSync.EntityNBTTag.setAndSync(this, NarutomodModVariables.forceBowPose, true);
						} else if (bowpose) {
							ProcedureSync.EntityNBTTag.setAndSync(this, NarutomodModVariables.forceBowPose, false);
						}
						break;
					}
				} else if (((offStack.getItem() != ItemScroll3rdKazekage.block && inv1Stack.getItem() != ItemScroll3rdKazekage.block)
				 || (this.thirdEntity != null && !this.thirdEntity.isEntityAlive())) && !this.isRidingHiruko()) {
					this.takeOffRobe(true);
					this.resetActiveHand();
					if (offStack.getItem() == ItemScroll3rdKazekage.block) {
						this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
					}
				}
			} else {
				this.takeOffRobe(false);
				if (bowpose) {
					ProcedureSync.EntityNBTTag.setAndSync(this, NarutomodModVariables.forceBowPose, false);
				}
				if (this.isRidingHiruko()) {
					this.hirukoEntity.takeRobeOff(false);
				} else if (this.isThirdSummoned() && offStack.getItem() == ItemScroll3rdKazekage.block) {
					this.resetActiveHand();
					ItemScroll3rdKazekage.RangedItem.interactWithEntity(offStack, this, this.thirdEntity);
					this.thirdScrollUsed = false;
					this.thirdEntity = null;
				} else if (this.scroll100Entity != null) {
					this.scroll100Entity.setDead();
					this.scroll100Entity = null;
					this.hundredStage = EnumStage.CANNOT_USE;
				} else if (this.lastElementalJutsu != null && !this.lastElementalJutsu.isDead) {
					this.lastElementalJutsu.setDead();
				}
			}
			if (this.isRidingHiruko() && this.ticksExisted > this.lastBlockTime + 20) {
				this.hirukoEntity.blockAttack(false);
			}
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || EntityNinjaMob.TeamAkatsuki.contains(entityIn.getClass());
		}

		@Override
		public void dismountEntity(Entity entityIn) {
			if (entityIn.equals(this.hirukoEntity)) {
				Vec3d vec = this.getAttackTarget() != null
				 ? this.getPositionVector().subtract(this.getAttackTarget().getPositionVector()).normalize().scale(0.5d)
				 : this.getLookVec().scale(-0.5d);
				this.motionX = vec.x;
				this.motionZ = vec.z;
				this.motionY = 0.62d;
				this.isAirBorne = true;
				this.fallDistance = 0.0f;
			} else {
				super.dismountEntity(entityIn);
			}
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			if (this.isRidingHiruko()) {
				return this.hirukoEntity.attackEntityAsMob(entityIn);
			}
			return super.attackEntityAsMob(entityIn);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.FALL) {
				return false;
			}
			if (this.equals(source.getTrueSource())) {
				return false;
			}
			if (source.isExplosion() && source.getTrueSource() != null && source.getTrueSource().equals(this.thirdEntity)) {
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

	    @Override
	    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
	        if (!this.isEntityInvulnerable(damageSrc)) {
	            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(this, damageSrc, damageAmount);
	            if (damageAmount <= 0) return;
	            damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
	            damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
	            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingDamage(this, damageSrc, damageAmount);
	            if (damageAmount != 0.0F) {
	                float f1 = this.getHealth();
	            	if (!this.canDie && !this.isAIDisabled() && f1 - damageAmount <= 0.0F) {
	            		if (f1 > 0.01f) {
			                this.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);
			                this.setHealth(0.01F);
	            			this.setBreaking(true);
	            		}
	            	} else {
		                this.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);
		                this.setHealth(f1 - damageAmount); // Forge: moved to fix MC-121048
	            	}
	            }
	        }
	    }

		@Override
		public void setSwingingArms(boolean swingingArms) {
			if (this.isThirdSummoned()) {
				ProcedureSync.EntityNBTTag.setAndSync(this, NarutomodModVariables.forceBowPose, swingingArms);
			}
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			ItemStack stack = this.getHeldItemOffhand(); 
			if (this.isRidingHiruko()) {
				double d = this.getDistance(target);
				if (d > 8.0d && stack.getItem() == ItemSenbonArm.block) {
					Vec3d vec = target.getPositionEyes(1f).subtract(this.getPositionVector());
					ItemSenbonArm.RangedItem.shootItem(this.hirukoEntity, vec.x, vec.y + d * 0.2d, vec.z, 0.5f);
					if (++this.senbonArmShootCount > 1) {
						this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
					}
					this.swapWithInventory(EntityEquipmentSlot.OFFHAND, 1);
				} else if (d < 7.0d) {
					this.swingArm(EnumHand.MAIN_HAND);
					this.attackEntityAsMob(target);
				} else {
					Vec3d vec = target.getPositionEyes(1f);
					for (int i = 0; i < 20; i++) {
						ItemPoisonSenbon.spawnArrow(this.hirukoEntity, vec);
					}
				}
			} else if (this.isThirdSummoned() && stack.getItem() == ItemScroll3rdKazekage.block
			 && !ItemScroll3rdKazekage.GATHERING.jutsu.isActivated(this.thirdEntity) && !this.isHandActive()) {
				if (this.ticksExisted - this.lastSandGatheringTime > SANDGATHERING_CD && this.rand.nextFloat() < 0.25f) {
					((ItemScroll3rdKazekage.RangedItem)stack.getItem()).setCurrentJutsu(stack, ItemScroll3rdKazekage.GATHERING);
					((ItemScroll3rdKazekage.RangedItem)stack.getItem()).executeJutsu(stack, this, 1f);
					this.lastSandGatheringTime = this.ticksExisted;
				} else if (this.ticksExisted - this.lastSandBulletTime > SANDBULLET_CD) {
					((ItemScroll3rdKazekage.RangedItem)stack.getItem()).setCurrentJutsu(stack, ItemScroll3rdKazekage.SANDBULLET);
					this.setActiveHand(EnumHand.OFF_HAND);
					this.lastSandBulletTime = this.ticksExisted;
				}
			} else if (this.isRobeOff() && (this.hundredStage == EnumStage.CANNOT_USE || this.hundredStage == EnumStage.USED)
			 && this.ticksExisted - this.lastElementalJutsuTime > 150) {
				if (this.rand.nextFloat() <= 0.3333f && this.consumeChakra(this.chakra4elementals)) {
					this.fireImmuneTicks = 300;
					this.lastElementalJutsu = new EntityFirestream.EC.Jutsu2().createJutsu(this, 25.0f, 200);
					this.lastElementalJutsuTime = this.ticksExisted;
				} else if (this.rand.nextFloat() < 0.5f && this.consumeChakra(this.chakra4elementals)) {
					this.lastElementalJutsu = new EntityWaterStream.EC.Jutsu().createJutsu(this, 25.0f, 200);
					this.lastElementalJutsuTime = this.ticksExisted;
				} else if (this.consumeChakra(this.chakra4elementals)) {
					this.lastElementalJutsu = new EntityFutonVacuum.EC.Jutsu().createJutsu(this, 25.0f, 200);
					this.lastElementalJutsuTime = this.ticksExisted;
				}
			}
		}

		@Override
		public void travel(float strafe, float vertical, float forward) {
			if (this.isServerWorld() && this.isRidingHiruko() && this.motionY > 0.01d) {
				this.hirukoEntity.motionY = this.motionY * 0.9d;
			}
			super.travel(strafe, vertical, forward);
		}

		@Override
		protected void collideWithNearbyEntities() {
			if (this.getRobeOffTicks() > this.bladesOpenTime && !this.isBroken()) {
				for (Entity entity : this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(2.4d, 0d, 2.4d),
					Predicates.and(EntitySelectors.getTeamCollisionPredicate(this), new Predicate<Entity>() {
						@Override
						public boolean apply(@Nullable Entity p_apply_1_) {
							return p_apply_1_ instanceof EntityLivingBase;
						}
					}))) {
					entity.attackEntityFrom(DamageSource.causeThornsDamage(this), 8f + this.rand.nextFloat() * 8f);
				}
			}
			super.collideWithNearbyEntities();
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere()
			 && this.world.getEntities(EntityCustom.class, EntitySelectors.IS_ALIVE).isEmpty()
			 && !EntityNinjaMob.SpawnData.spawnedRecentlyHere(this, 36000);
			 //&& this.world.getEntitiesWithinAABB(EntityCustom.class, this.getEntityBoundingBox().grow(128.0D)).isEmpty();
			 //&& this.rand.nextInt(5) == 0;
		}

		@Override
		public void removeTrackingPlayer(EntityPlayerMP player) {
			super.removeTrackingPlayer(player);
			if (this.bossInfo.getPlayers().contains(player)) {
				this.bossInfo.removePlayer(player);
			}
		}

		private void trackAttackedPlayers() {
			Entity entity = this.getAttackingEntity();
			if (entity instanceof EntityPlayerMP || (entity = this.getAttackTarget()) instanceof EntityPlayerMP) {
				this.bossInfo.addPlayer((EntityPlayerMP) entity);
			}
		}

		@Override
		public void onUpdate() {
			if (this.hirukoEntity == null) {
				if (this.getRidingEntity() instanceof EntityPuppetHiruko.EntityCustom) {
					this.hirukoEntity = (EntityPuppetHiruko.EntityCustom)this.getRidingEntity();
					this.hirukoEntity.setAkatsuki(true);
				} else if (!this.world.isRemote) {
					this.hirukoEntity = new EntityPuppetHiruko.EntityCustom(this, this.posX, this.posY, this.posZ);
					this.hirukoEntity.setAkatsuki(true);
					this.world.spawnEntity(this.hirukoEntity);
				}
			}
			ItemStack stack = this.getHeldItemOffhand();
			if (!this.world.isRemote) {
				if (this.isRidingHiruko()) {
					if (this.width < this.hirukoEntity.width - 0.01f) {
						this.setSize(this.hirukoEntity.width - 0.01f, this.hirukoEntity.height - 0.01f);
					}
				} else if (this.width > 0.525f) {
					this.setSize(0.525f, 1.75f);
				}
			} else if (this.isRidingHiruko()) {
				this.hirukoEntity.raiseLeftArm(stack.getItem() == ItemSenbonArm.block);
			}
			if (this.thirdEntity == null && stack.getItem() == ItemScroll3rdKazekage.block) {
				this.thirdEntity = ((ItemScroll3rdKazekage.RangedItem)stack.getItem()).getPuppetEntity(stack, this.world);
			}
			this.clearActivePotions();
			super.onUpdate();
			if (!this.world.isRemote) {
				int breakingTicks = this.getBreakingTicks();
				if (breakingTicks > this.breakingEnd) {
					this.reverseBreak();
				} else if (breakingTicks >= 0) {
					this.setBreakingTicks(breakingTicks + this.breakingDirection);
					if (breakingTicks > this.breakingProgressEnd && !this.isStandingStill()) {
						this.standStillFor(this.breakingEnd * 3);
					}
				}
				int robeOffTicks = this.getRobeOffTicks();
				if (!this.isRidingHiruko() && robeOffTicks >= 0) {
					this.setRobeOffTicks(++robeOffTicks);
				}
			}
			this.isImmuneToFire = this.fireImmuneTicks > 0;
			if (this.fireImmuneTicks > 0) {
				--this.fireImmuneTicks;
			}
			this.trackAttackedPlayers();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
		}

		private void setFullHealthAndEnableHundred() {
			this.setNoCore(false);
			this.standStillFor(0);
			this.setHealth(this.getMaxHealth());
			if (this.isRobeOff() && this.hundredStage == EnumStage.CANNOT_USE) {
				this.hundredStage = EnumStage.CAN_USE;
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				if (this.hirukoEntity != null && this.hirukoEntity.isEntityAlive()) {
					this.hirukoEntity.setDead();
				}
				if (this.thirdEntity != null && this.thirdEntity.isEntityAlive()) {
					ProcedureUtils.poofWithSmoke(this.thirdEntity);
					this.thirdEntity.setDead();
				}
				if (this.scroll100Entity != null) {
					this.scroll100Entity.setDead();
				}
			}
		}

		private boolean isRidingHiruko() {
			return this.hirukoEntity != null && this.hirukoEntity.isEntityAlive() && this.hirukoEntity.equals(this.getRidingEntity());
		}

		private boolean isThirdSummoned() {
			return this.thirdEntity != null && this.thirdEntity.isEntityAlive();
		}

		@Override
		public double getYOffset() {
			return -0.35d;
		}
	}

	public static class AttackHook {
		@SubscribeEvent
		public void onAttacked(LivingAttackEvent event) {
			if (event.getEntityLiving() instanceof EntityPuppetHiruko.EntityCustom
			 && event.getEntityLiving().getControllingPassenger() instanceof EntityCustom
			 && !event.getSource().isUnblockable()) {
				EntityPuppetHiruko.EntityCustom hiruko = (EntityPuppetHiruko.EntityCustom)event.getEntityLiving();
				EntityCustom sasori = (EntityCustom)event.getEntityLiving().getControllingPassenger();
				if (!sasori.isAIDisabled() && sasori.ticksExisted > sasori.lastBlockTime + sasori.BLOCKING_CD) {
					hiruko.blockAttack(true);
					sasori.lastBlockTime = sasori.ticksExisted;
				}
			}
		}
	}

	public static class EntityCore extends EntityThrowable {
		private static final DataParameter<Integer> OWNER = EntityDataManager.<Integer>createKey(EntityCore.class, DataSerializers.VARINT);
		private float health = 40.0f;
		private boolean returnToOwner;
		private int hurtTime;

		public EntityCore(World worldIn) {
			super(worldIn);
			this.setSize(0.25f, 0.15f);
		}

		public EntityCore(EntityCustom throwerIn) {
			super(throwerIn.world, throwerIn);
			this.setSize(0.25f, 0.15f);
			this.setOwner(throwerIn);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(OWNER, Integer.valueOf(-1));
		}

		private void setOwner(EntityCustom shooter) {
			this.getDataManager().set(OWNER, Integer.valueOf(shooter.getEntityId()));
		}

		@Nullable
		protected EntityCustom getOwner() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(OWNER)).intValue());
			return entity instanceof EntityCustom ? (EntityCustom)entity : null;
		}

		@Override
		protected float getGravityVelocity() {
			return 0.05f;
		}

		protected void setReturnToOwner() {
			this.returnToOwner = true;
			this.world.setEntityState(this, (byte)17);
		}

		@SideOnly(Side.CLIENT)
		public void handleStatusUpdate(byte id) {
			if (id == 17) {
				this.returnToOwner = true;
			} else if (id == 18) {
				this.hurtTime = 10;
			}
		}

		@Override
		public void onUpdate() {
			EntityCustom owner = this.getOwner();
			if (this.returnToOwner && this.ticksExisted % 4 == 1) {
				this.setNoGravity(true);
				if (owner != null) {
					this.inGround = false;
					Vec3d vec = owner.getPositionVector().addVector(0d, 1.5d, 0d).subtract(this.getPositionVector()).normalize().scale(0.3d);
					this.motionX = vec.x;
					this.motionY = vec.y;
					this.motionZ = vec.z;
					this.isAirBorne = true;
				}
			}
			super.onUpdate();
			if (this.hurtTime > 0) {
				--this.hurtTime;
			}
			if (!this.world.isRemote && owner == null) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			EntityCustom owner = this.getOwner();
			if (this.returnToOwner) {
				if (!this.world.isRemote && result.entityHit != null && result.entityHit.equals(owner)) {
					owner.setFullHealthAndEnableHundred();
					this.setDead();
				}
			} else if (result.entityHit != null) {
				if (!result.entityHit.equals(owner)) {
					this.motionX *= -0.4d;
					this.motionY *= -0.4d;
					this.motionZ *= -0.4d;
			        this.posX = result.hitVec.x;
			        this.posY = result.hitVec.y;
			        this.posZ = result.hitVec.z;
				}
			} else if (ProcedureUtils.getVelocity(this) < 0.1d) {
				BlockPos blockpos = result.getBlockPos();
				ReflectionHelper.setPrivateValue(EntityThrowable.class, this, blockpos.getX(), 0); //this.xTile = blockpos.getX();
				ReflectionHelper.setPrivateValue(EntityThrowable.class, this, blockpos.getY(), 1); // this.yTile = blockpos.getY();
				ReflectionHelper.setPrivateValue(EntityThrowable.class, this, blockpos.getZ(), 2); // this.zTile = blockpos.getZ();
		        ReflectionHelper.setPrivateValue(EntityThrowable.class, this, this.world.getBlockState(blockpos).getBlock(), 3); //this.inTile = iblockstate.getBlock();
				//ReflectionHelper.setPrivateValue(EntityThrowable.class, this, 900, 8); // this.ticksInGround = 900;
		        this.motionX = 0.0d;
		        this.motionY = 0.0d;
		        this.motionZ = 0.0d;
		        this.posX = result.hitVec.x;
		        this.posY = result.hitVec.y;
		        this.posZ = result.hitVec.z;
		        this.inGround = true;
			} else {
				this.motionX *= 0.4d;
				this.motionY *= 0.4d;
				this.motionZ *= 0.4d;
		        this.posX = result.hitVec.x;
		        this.posY = result.hitVec.y;
		        this.posZ = result.hitVec.z;
				if (result.sideHit.getAxis() == EnumFacing.Axis.X) {
					this.motionX *= -0.8d;
				}
				if (result.sideHit.getAxis() == EnumFacing.Axis.Y) {
					this.motionY *= -0.8d;
				}
				if (result.sideHit.getAxis() == EnumFacing.Axis.Z) {
					this.motionZ *= -0.8d;
				}
			}
		}

		@Override
		public boolean canBeCollidedWith() {
			return true;
		}

		@Override
		public void onKillCommand() {
			this.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.world.isRemote && this.ticksExisted > 10) {
				this.health -= amount;
				this.world.setEntityState(this, (byte)18);
				if (this.health <= 0.0f) {
					this.setDead();
					Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY+this.height/2, this.posZ,
					 300, this.width * 0.5d, this.height * 0.3d, this.width * 0.5d, 0d, 0d, 0d, 0xD0FFFFFF, 30);
					EntityCustom owner = this.getOwner();
					if (owner != null) {
						owner.canDie = true;
						owner.setEntityInvulnerable(false);
						owner.attackEntityFrom(source, amount);
					}
				} else if (source.getTrueSource() instanceof EntityLivingBase) {
					Vec3d vec = this.getPositionVector().subtract(source.getTrueSource().getPositionVector()).normalize().scale(0.2d);
					this.motionX += vec.x;
					this.motionZ += vec.z;
					this.motionY += this.inGround ? 0.4d : 0.0d;
					this.isAirBorne = true;
					this.inGround = false;
				}
				return true;
			}
			return false;
		}
	}

	public enum EnumStage {
		CANNOT_USE,
		CAN_USE,
		OPENING_SCROLL,
		USING,
		USED;
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCore.class, renderManager -> new RenderCore(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/sasori2.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelSasori());
			}

			@Override
			protected void renderLayers(EntityCustom entity, float f0, float f1, float f2, float f3, float f4, float f5, float f6) {
				if (!entity.isInvisible()) {
					super.renderLayers(entity, f0, f1, f2, f3, f4, f5, f6);
				}
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				float f = 0.0625f * 14;
				GlStateManager.scale(f, f, f);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderCore extends Render<EntityCore> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/sasori_core.png");
			private final ModelSasoriCore model = new ModelSasoriCore();
	
			public RenderCore(RenderManager renderManager) {
				super(renderManager);
				this.shadowSize = 0.1F;
			}
	
			@Override
			public void doRender(EntityCore entity, double x, double y, double z, float yaw, float pt) {
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) x, (float) y + entity.height * 0.5f, (float) z);
				GlStateManager.scale(0.6f, 0.6f, 0.6f);
				GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt - 180.0F, 1.0F, 0.0F, 0.0F);
				if (entity.hurtTime > 0) {
					GlStateManager.color(1.0f, 0.4f, 0.4f, 1.0f);
				}
				this.model.render(entity, 0.0F, 0.0F, pt + entity.ticksExisted, 0.0F, 0.0F, 0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCore entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelSasori extends EntityNinjaMob.ModelNinja {
			//private final ModelRenderer bipedHead;
			private final ModelRenderer eyesPiercing;
			private final ModelRenderer eyesDead;
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer robeHead;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer robeBody;
			private final ModelRenderer scrolls;
			private final ModelRenderer backBladesRight;
			private final ModelRenderer bone;
			private final ModelRenderer[] blade = new ModelRenderer[10];
			private final ModelRenderer backBladesLeft;
			private final ModelRenderer bone12;
			private final ModelRenderer core;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer robeRightArm;
			private final ModelRenderer gunRight;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer robeLeftArm;
			private final ModelRenderer gunLeft;
			//private final ModelRenderer bipedRightLeg;
			private final ModelRenderer robeRightLeg;
			//private final ModelRenderer bipedLeftLeg;
			private final ModelRenderer robeLeftLeg;
			private final Vector3f[] backBladesRightPreset = { new Vector3f(0.0F, 0.2618F, 1.309F), new Vector3f(0.0F, 0.5236F, -0.2618F) };
			private final Vector3f[] backBladesLeftPreset = { new Vector3f(0.0F, -0.2618F, -1.309F), new Vector3f(0.0F, -0.5236F, 0.2618F) };
			private final float[][] bladePresetZ = { { -3.1416F, -0.5236F }, { -3.098F, 0.0873F }, { -3.0543F, 0.6981F }, { -3.0107F, 1.309F }, { -2.9671F, 1.9199F }, { 3.1398F, 0.5236F }, { 3.098F, -0.0873F }, { 3.0543F, -0.6981F }, { 3.0107F, -1.309F }, { 2.9671F, -1.9199F } };
			private final float[][] bipedHeadPreset = { { 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F }, { -5.0F, 21.0F, 26.0F, -1.5708F, 0.0F, 1.0908F } };
			private final float[][] bipedBodyPreset = { { 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F }, { 10.0F, 22.0F, 1.0F, -1.5708F, 0.5236F, 0.0F } };
			private final float[][] bipedRightArmPreset = { { -5.0F, 2.5F, 0.0F, 0.0F, 0.0F, 0.0F }, { -22.0F, 23.0F, 19.0F, 0.8727F, 0.0F, 1.5708F } };
			private final float[][] bipedLeftArmPreset = { { 5.0F, 2.5F, 0.0F, 0.0F, 0.0F, 0.0F }, { 18.0F, 22.0F, 12.0F, 1.5708F, -1.2217F, 3.1416F } };
			private final float[][] bipedRightLegPreset = { { -1.9F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F }, { -15.9F, 22.0F, -10.0F, -1.5708F, 0.829F, 0.0F } };
			private final float[][] bipedLeftLegPreset = { { 1.9F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F }, { 19.9F, 22.0F, 0.0F, -1.5708F, -1.309F, 0.0F } };
			
			public ModelSasori() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bipedHead, -0.1047F, 0.0873F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.3F, false));
				eyesPiercing = new ModelRenderer(this);
				eyesPiercing.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.addChild(eyesPiercing);
				eyesPiercing.cubeList.add(new ModelBox(eyesPiercing, 24, 4, -4.0F, -5.0F, -4.01F, 8, 2, 0, 0.0F, false));
				eyesDead = new ModelRenderer(this);
				eyesDead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.addChild(eyesDead);
				eyesDead.cubeList.add(new ModelBox(eyesDead, 24, 6, -4.0F, -5.0F, -4.01F, 8, 2, 0, 0.0F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bipedHeadwear, -0.1047F, 0.0873F, 0.0F);
				robeHead = new ModelRenderer(this);
				robeHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.addChild(robeHead);
				robeHead.cubeList.add(new ModelBox(robeHead, 32, 54, -4.0F, -2.0F, -4.0F, 8, 2, 8, 0.65F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				robeBody = new ModelRenderer(this);
				robeBody.setRotationPoint(0.0F, 24.0F, 0.0F);
				bipedBody.addChild(robeBody);
				robeBody.cubeList.add(new ModelBox(robeBody, 16, 32, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.5F, false));
				scrolls = new ModelRenderer(this);
				scrolls.setRotationPoint(0.0F, 22.0F, 0.0F);
				bipedBody.addChild(scrolls);
				scrolls.cubeList.add(new ModelBox(scrolls, 24, 0, -3.0F, -21.0F, 2.0F, 6, 2, 2, 0.0F, false));
				scrolls.cubeList.add(new ModelBox(scrolls, 24, 0, -3.0F, -18.0F, 2.0F, 6, 2, 2, 0.0F, false));
				scrolls.cubeList.add(new ModelBox(scrolls, 24, 0, -3.0F, -15.0F, 2.0F, 6, 2, 2, 0.0F, false));
				scrolls.cubeList.add(new ModelBox(scrolls, 0, 20, -2.0F, -21.175F, 1.275F, 4, 9, 3, 0.0F, false));
				backBladesRight = new ModelRenderer(this);
				backBladesRight.setRotationPoint(-3.2F, 6.0F, 3.0F);
				bipedBody.addChild(backBladesRight);
				setRotationAngle(backBladesRight, backBladesRightPreset[0].x, backBladesRightPreset[0].y, backBladesRightPreset[0].z); //setRotationAngle(backBladesRight, 0.0F, 0.5236F, -0.2618F);
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -1.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -3.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -5.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -7.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -9.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-8.8F, 0.5F, -0.5F);
				backBladesRight.addChild(bone);
				setRotationAngle(bone, 0.0F, -1.0472F, 0.0F);
				for (int i = 0; i < blade.length / 2; i++) {
					blade[i] = new ModelRenderer(this);
					blade[i].setRotationPoint(0.0F, 0.0F, 0.5F);
					bone.addChild(blade[i]);
					setRotationAngle(blade[i], 0.0F, 0.0F, bladePresetZ[i][0]); //setRotationAngle(blade1, 0.0F, 0.0F, -0.5236F);
					blade[i].cubeList.add(new ModelBox(blade[i], 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
					blade[i].cubeList.add(new ModelBox(blade[i], 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
					blade[i].cubeList.add(new ModelBox(blade[i], 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
					blade[i].cubeList.add(new ModelBox(blade[i], 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				}
				backBladesLeft = new ModelRenderer(this);
				backBladesLeft.setRotationPoint(3.2F, 6.0F, 3.0F);
				bipedBody.addChild(backBladesLeft);
				setRotationAngle(backBladesLeft, backBladesLeftPreset[0].x, backBladesLeftPreset[0].y, backBladesLeftPreset[0].z); //setRotationAngle(backBladesLeft, 0.0F, -0.5236F, 0.2618F);
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, -0.2F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 1.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 3.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 5.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 7.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(8.8F, 0.5F, -0.5F);
				backBladesLeft.addChild(bone12);
				setRotationAngle(bone12, 0.0F, 0.7854F, 0.0F);
				for (int i = blade.length / 2; i < blade.length; i++) {
					blade[i] = new ModelRenderer(this);
					blade[i].setRotationPoint(0.0F, 0.0F, 0.5F);
					bone12.addChild(blade[i]);
					setRotationAngle(blade[i], 0.0F, 0.0F, bladePresetZ[i][0]); //setRotationAngle(blade[5], 0.0F, 0.0F, 0.5236F);
					blade[i].cubeList.add(new ModelBox(blade[i], 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
					blade[i].cubeList.add(new ModelBox(blade[i], 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
					blade[i].cubeList.add(new ModelBox(blade[i], 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
					blade[i].cubeList.add(new ModelBox(blade[i], 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				}
				core = new ModelRenderer(this);
				core.setRotationPoint(0.0F, 5.0F, 0.9F);
				bipedBody.addChild(core);
				core.cubeList.add(new ModelBox(core, 14, 16, 0.75F, -3.6F, -3.0F, 3, 3, 0, 0.0F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				setRotationAngle(bipedRightArm, -0.1745F, 0.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
				robeRightArm = new ModelRenderer(this);
				robeRightArm.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightArm.addChild(robeRightArm);
				robeRightArm.cubeList.add(new ModelBox(robeRightArm, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.5F, false));
				gunRight = new ModelRenderer(this);
				gunRight.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightArm.addChild(gunRight);
				gunRight.cubeList.add(new ModelBox(gunRight, 36, 16, -1.5F, 8.25F, -1.0F, 2, 2, 2, 0.0F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				setRotationAngle(bipedLeftArm, 0.1745F, 0.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, true));
				robeLeftArm = new ModelRenderer(this);
				robeLeftArm.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftArm.addChild(robeLeftArm);
				robeLeftArm.cubeList.add(new ModelBox(robeLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.5F, true));
				gunLeft = new ModelRenderer(this);
				gunLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftArm.addChild(gunLeft);
				gunLeft.cubeList.add(new ModelBox(gunLeft, 36, 16, -0.5F, 8.25F, -1.0F, 2, 2, 2, 0.0F, true));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				setRotationAngle(bipedRightLeg, 0.2618F, 0.0F, 0.0349F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				robeRightLeg = new ModelRenderer(this);
				robeRightLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightLeg.addChild(robeRightLeg);
				robeRightLeg.cubeList.add(new ModelBox(robeRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				setRotationAngle(bipedLeftLeg, -0.2618F, 0.0F, -0.0349F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				robeLeftLeg = new ModelRenderer(this);
				robeLeftLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftLeg.addChild(robeLeftLeg);
				robeLeftLeg.cubeList.add(new ModelBox(robeLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
			}

			@Override
			public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				if (entityIn instanceof EntityCustom) {
					this.setRobeOff(((EntityCustom)entityIn).isRobeOff());
				}
				super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			}

			private void setRobeOff(boolean robeOff) {
				this.robeHead.showModel = !robeOff;
				this.robeBody.showModel = !robeOff;
				this.robeLeftArm.showModel = !robeOff;
				this.robeLeftLeg.showModel = !robeOff;
				this.robeRightArm.showModel = !robeOff;
				this.robeRightLeg.showModel = !robeOff;
				this.scrolls.showModel = robeOff;
				this.backBladesLeft.showModel = robeOff;
				this.backBladesRight.showModel = robeOff;
			}

			@Override
			public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
				this.bodyPartAngles(this.bipedHead, this.bipedHeadPreset, 0.0f);
				this.bodyPartAngles(this.bipedBody, this.bipedBodyPreset, 0.0f);
				this.bodyPartAngles(this.bipedRightArm, this.bipedRightArmPreset, 0.0f);
				this.bodyPartAngles(this.bipedLeftArm, this.bipedLeftArmPreset, 0.0f);
				this.bodyPartAngles(this.bipedRightLeg, this.bipedRightLegPreset, 0.0f);
				this.bodyPartAngles(this.bipedLeftLeg, this.bipedLeftLegPreset, 0.0f);

				super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

				if (entityIn instanceof EntityCustom) {
					EntityCustom entity = (EntityCustom)entityIn;
					float partialTicks = ageInTicks - entityIn.ticksExisted;
					float robeOffTicks = (float)entity.getRobeOffTicks() + partialTicks;
					int bt = entity.getBreakingTicks();
					boolean nocore = entity.hasNoCore();
					if (robeOffTicks >= 0) {
						if (robeOffTicks <= entity.bladesOpenTime) {
							float f = Math.min(robeOffTicks / entity.bladesOpenTime, 1.0F);
							this.backBladesRight.rotateAngleX = this.backBladesRightPreset[0].x + (this.backBladesRightPreset[1].x - this.backBladesRightPreset[0].x) * f;
							this.backBladesRight.rotateAngleY = this.backBladesRightPreset[0].y + (this.backBladesRightPreset[1].y - this.backBladesRightPreset[0].y) * f;
							this.backBladesRight.rotateAngleZ = this.backBladesRightPreset[0].z + (this.backBladesRightPreset[1].z - this.backBladesRightPreset[0].z) * f;
							this.backBladesLeft.rotateAngleX = this.backBladesLeftPreset[0].x + (this.backBladesLeftPreset[1].x - this.backBladesLeftPreset[0].x) * f;
							this.backBladesLeft.rotateAngleY = this.backBladesLeftPreset[0].y + (this.backBladesLeftPreset[1].y - this.backBladesLeftPreset[0].y) * f;
							this.backBladesLeft.rotateAngleZ = this.backBladesLeftPreset[0].z + (this.backBladesLeftPreset[1].z - this.backBladesLeftPreset[0].z) * f;
							for (int i = 0; i < this.blade.length; i++) {
								this.blade[i].rotateAngleZ = this.bladePresetZ[i][0] + (this.bladePresetZ[i][1] - this.bladePresetZ[i][0]) * f;
							}
						} else if (!nocore) {
							for (int i = 0; i < this.blade.length; i++) {
								this.blade[i].rotateAngleZ = this.bladePresetZ[i][1] + ageInTicks * 2.5132F;
							}
						}
					}
					if (bt >= 0) {
						float f = MathHelper.clamp(((float)bt + partialTicks * entity.breakingDirection) / entity.breakingProgressEnd, 0.0F, 1.0F);
						this.bodyPartAngles(this.bipedHead, this.bipedHeadPreset, f);
						this.bodyPartAngles(this.bipedBody, this.bipedBodyPreset, f);
						this.bodyPartAngles(this.bipedRightArm, this.bipedRightArmPreset, f);
						this.bodyPartAngles(this.bipedLeftArm, this.bipedLeftArmPreset, f);
						this.bodyPartAngles(this.bipedRightLeg, this.bipedRightLegPreset, f);
						this.bodyPartAngles(this.bipedLeftLeg, this.bipedLeftLegPreset, f);
						this.setRobeOff(true);
					}
					if (nocore) {
						this.core.showModel = false;
						this.eyesPiercing.showModel = false;
					} else {
						this.core.showModel = true;
						this.eyesPiercing.showModel = true;
					}
					this.eyesDead.showModel = !this.eyesPiercing.showModel;
				}
			}

			private void bodyPartAngles(ModelRenderer part, float[][] preset, float progress) {
				part.rotationPointX = preset[0][0] + (preset[1][0] - preset[0][0]) * progress;
				part.rotationPointY = preset[0][1] + (preset[1][1] - preset[0][1]) * progress;
				part.rotationPointZ = preset[0][2] + (preset[1][2] - preset[0][2]) * progress;
				part.rotateAngleX = preset[0][3] + (preset[1][3] - preset[0][3]) * progress;
				part.rotateAngleY = preset[0][4] + (preset[1][4] - preset[0][4]) * progress;
				part.rotateAngleZ = preset[0][5] + (preset[1][5] - preset[0][5]) * progress;
			}
		}
		
		@SideOnly(Side.CLIENT)
		public class ModelSasoriCore extends ModelBase {
			private final ModelRenderer hexadecagon;
			private final ModelRenderer hexadecagon_r1;
			private final ModelRenderer hexadecagon_r2;
			private final ModelRenderer hexadecagon_r3;
			private final ModelRenderer hexadecagon_r4;
			private final ModelRenderer hexadecagon2;
			private final ModelRenderer hexadecagon_r5;
			private final ModelRenderer hexadecagon_r6;
			private final ModelRenderer hexadecagon_r7;
			private final ModelRenderer hexadecagon_r8;
		
			public ModelSasoriCore() {
				textureWidth = 64;
				textureHeight = 64;
		
				hexadecagon = new ModelRenderer(this);
				hexadecagon.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 0, 27, -0.5027F, -2.5F, -4.0F, 1, 5, 8, 0.2F, false));
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 0, 9, -2.5F, -0.5027F, -4.0F, 5, 1, 8, 0.2F, false));
		
				hexadecagon_r1 = new ModelRenderer(this);
				hexadecagon_r1.setRotationPoint(0.0F, 0.0F, -2.0F);
				hexadecagon.addChild(hexadecagon_r1);
				setRotationAngle(hexadecagon_r1, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -2.5F, -0.5027F, -2.0F, 5, 1, 8, 0.2F, false));
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 18, 19, -0.5027F, -2.5F, -2.0F, 1, 5, 8, 0.2F, false));
		
				hexadecagon_r2 = new ModelRenderer(this);
				hexadecagon_r2.setRotationPoint(0.0F, 0.0F, -2.0F);
				hexadecagon.addChild(hexadecagon_r2);
				setRotationAngle(hexadecagon_r2, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 18, -2.5F, -0.5027F, -2.0F, 5, 1, 8, 0.2F, false));
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 28, 6, -0.5027F, -2.5F, -2.0F, 1, 5, 8, 0.2F, false));
		
				hexadecagon_r3 = new ModelRenderer(this);
				hexadecagon_r3.setRotationPoint(0.0F, 0.0F, -2.0F);
				hexadecagon.addChild(hexadecagon_r3);
				setRotationAngle(hexadecagon_r3, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 18, 1, -0.5027F, -2.5F, -2.0F, 1, 5, 8, 0.2F, false));
		
				hexadecagon_r4 = new ModelRenderer(this);
				hexadecagon_r4.setRotationPoint(0.0F, 0.0F, -2.0F);
				hexadecagon.addChild(hexadecagon_r4);
				setRotationAngle(hexadecagon_r4, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 28, 24, -0.5027F, -2.5F, -2.0F, 1, 5, 8, 0.2F, false));
		
				hexadecagon2 = new ModelRenderer(this);
				hexadecagon2.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.cubeList.add(new ModelBox(hexadecagon2, 0, 40, -0.5027F, -2.5F, -4.0F, 1, 5, 8, 0.0F, false));
				hexadecagon2.cubeList.add(new ModelBox(hexadecagon2, 20, 37, -2.5F, -0.5027F, -4.0F, 5, 1, 8, 0.0F, false));
				hexadecagon2.cubeList.add(new ModelBox(hexadecagon2, 28, 0, -2.5F, -2.5F, -4.0F, 5, 5, 0, 0.0F, false));
		
				hexadecagon_r5 = new ModelRenderer(this);
				hexadecagon_r5.setRotationPoint(0.0F, 0.0F, -2.0F);
				hexadecagon2.addChild(hexadecagon_r5);
				setRotationAngle(hexadecagon_r5, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 38, 38, -2.5F, -0.5027F, -2.0F, 5, 1, 8, 0.0F, false));
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 10, 51, -0.5027F, -2.5F, -2.0F, 1, 5, 8, 0.0F, false));
		
				hexadecagon_r6 = new ModelRenderer(this);
				hexadecagon_r6.setRotationPoint(0.0F, 0.0F, -2.0F);
				hexadecagon2.addChild(hexadecagon_r6);
				setRotationAngle(hexadecagon_r6, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 28, 55, -2.5F, -0.5027F, -2.0F, 5, 1, 8, 0.0F, false));
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 38, 13, -0.5027F, -2.5F, -2.0F, 1, 5, 8, 0.0F, false));
		
				hexadecagon_r7 = new ModelRenderer(this);
				hexadecagon_r7.setRotationPoint(0.0F, 0.0F, -2.0F);
				hexadecagon2.addChild(hexadecagon_r7);
				setRotationAngle(hexadecagon_r7, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r7.cubeList.add(new ModelBox(hexadecagon_r7, 10, 32, -0.5027F, -2.5F, -2.0F, 1, 5, 8, 0.0F, false));
		
				hexadecagon_r8 = new ModelRenderer(this);
				hexadecagon_r8.setRotationPoint(0.0F, 0.0F, -2.0F);
				hexadecagon2.addChild(hexadecagon_r8);
				setRotationAngle(hexadecagon_r8, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r8.cubeList.add(new ModelBox(hexadecagon_r8, 38, 0, -0.5027F, -2.5F, -2.0F, 1, 5, 8, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				hexadecagon2.render(f5);
				float color = 0.65F + MathHelper.sin(f2 * 0.1F) * 0.35F;
				GlStateManager.color(color, color, color, 1.0F);
				hexadecagon.render(f5);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
