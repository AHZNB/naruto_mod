
package net.narutomod.entity;

import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemScytheHidan;
import net.narutomod.item.ItemScytheHidanThrown;
import net.narutomod.item.ItemSpearRetractable;
import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.init.Biomes;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityHidan extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 467;
	public static final int ENTITYID_RANGED = 468;

	public EntityHidan(ElementsNarutomodMod instance) {
		super(instance, 896);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "hidan"), ENTITYID).name("hidan").tracker(64, 3, true).egg(-16777216, -6750157).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityJashinSymbol.class)
		 .id(new ResourceLocation("narutomod", "hidan_symbol"), ENTITYID_RANGED).name("hidan_symbol").tracker(64, 3, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		int i = MathHelper.clamp(ModConfig.SPAWN_WEIGHT_HIDAN, 0, 20);
		if (i > 0) {
			EntityRegistry.addSpawn(EntityCustom.class, i, 1, 1, EnumCreatureType.MONSTER,
					Biomes.BEACH, Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.MESA, Biomes.MESA_ROCK,
					Biomes.EXTREME_HILLS, Biomes.MUSHROOM_ISLAND, Biomes.MUTATED_DESERT, Biomes.MUTATED_EXTREME_HILLS,
					Biomes.MUTATED_MESA, Biomes.MUTATED_MESA_ROCK, Biomes.MUTATED_PLAINS, Biomes.MUTATED_SWAMPLAND,
					Biomes.PLAINS, Biomes.RIVER, Biomes.SWAMPLAND, Biomes.SAVANNA, Biomes.MUTATED_SAVANNA);
		}
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private static final DataParameter<Integer> JASHIN_TICKS = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private final DamageSource selfDamage = DamageSource.causeMobDamage(this).setDamageIsAbsolute().setDamageIsAbsolute();
		private final int transitionTime = 60;
		private int jashinTransitionDirection;
		private EntityLivingBase curseTarget;
		private boolean scytheOnRetrieval;
		private EntityJashinSymbol jashinSymbol;
		private int lastJashinTime;
		private final double jashinChakraUsage = 500d;

		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelectorAkatsuki));
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemToInventory(new ItemStack(ItemScytheHidan.block), 0);
			this.setItemToInventory(new ItemStack(ItemSpearRetractable.block), 1);
			ItemStack stack = new ItemStack(ItemAkatsukiRobe.body);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setBoolean("collarOpen", true);
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(JASHIN_TICKS, Integer.valueOf(0));
		}

		private void setJashinTicks(int ticks) {
			this.dataManager.set(JASHIN_TICKS, Integer.valueOf(ticks));
		}
	
		public int getJashinTicks() {
			return ((Integer)this.getDataManager().get(JASHIN_TICKS)).intValue();
		}

		public void setJashinTransitionDirection(int i) {
			this.jashinTransitionDirection = i;
			if (i < 0) {
				if (this.getJashinTicks() > this.transitionTime) {
					this.setJashinTicks(this.transitionTime);
				}
			} else if (i > 0) {
				this.setJashinTicks(1);
				ItemStack stack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
				if (stack.getItem() == ItemAkatsukiRobe.body) {
					this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.6F, this.rand.nextFloat() * 0.3F + 0.7F);
					if (!stack.hasTagCompound()) {
						stack.setTagCompound(new NBTTagCompound());
					}
					stack.getTagCompound().setBoolean("halfOff", true);
				}
			}
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected double meleeReach() {
			return 4.8d;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new AIMoveTowardsSymbol(this, 1.5d));
			this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 1.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.jashinTransitionDirection == 0;
				}
			});
			this.tasks.addTask(3, new EntityAIAttackRanged(this, 1.0D, 10, 16.0F) {
				@Override
				public boolean shouldExecute() {
					return EntityCustom.this.jashinTransitionDirection == 0 && super.shouldExecute()
					 && EntityCustom.this.getDistance(EntityCustom.this.getAttackTarget()) > 6.0d
					 && EntityCustom.this.getHeldItemMainhand().getItem() == ItemScytheHidan.block;
				}
			});
			this.tasks.addTask(4, new EntityNinjaMob.AIAttackMelee(this, 1.0d, true) {
				@Override
				public boolean shouldExecute() {
					return EntityCustom.this.jashinTransitionDirection == 0 && super.shouldExecute();
				}
			});
			this.tasks.addTask(5, new EntityAIWatchClosest(this, null, 48.0F, 1.0F) {
				@Override
				public boolean shouldExecute() {
					if (EntityCustom.this.jashinTransitionDirection > 0
					 && EntityCustom.this.curseTarget != null && EntityCustom.this.curseTarget.isEntityAlive()) {
						this.closestEntity = EntityCustom.this.curseTarget;
						return true;
					}
					return false;
				}
			});
			this.tasks.addTask(6, new EntityClone.AIFollowSummoner(this, 0.6d, 4f) {
				@Override @Nullable
				protected EntityLivingBase getFollowEntity() {
					return (EntityLivingBase)EntityCustom.this.world.findNearestEntityWithinAABB(EntityKakuzu.EntityCustom.class,
					 EntityCustom.this.getEntityBoundingBox().grow(256d, 16d, 256d), EntityCustom.this);
				}
			});
			this.tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 32.0F, 1.0F));
			this.tasks.addTask(8, new EntityAIWander(this, 0.5d));
			this.tasks.addTask(9, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			ItemStack stack = this.getHeldItemMainhand();
			if (stack.getItem() == ItemScytheHidanThrown.block) {
				ItemScytheHidan.EntityCustom entity = ((ItemScytheHidanThrown.RangedItem)stack.getItem()).getEntity(this.world, stack);
				if (entity.getHitTarget() instanceof EntityNinjaMob.Base || entity.getHitTarget() instanceof EntityPlayer) {
					this.curseTarget = entity.getHitTarget();
				}
				if (entity.inGround()) {
					this.standStillFor(30);
					stack.onPlayerStoppedUsing(this.world, this, 0);
					this.scytheOnRetrieval = true;
				}
			} else {
				if (this.curseTarget != null && EntityAITarget.isSuitableTarget(this, this.curseTarget, false, false)) {
					if (this.jashinSymbol != null && !this.jashinSymbol.isDead) {
						AxisAlignedBB bb1 = this.getEntityBoundingBox();
						AxisAlignedBB bb2 = this.jashinSymbol.getEntityBoundingBox().expand(0d, 1d, 0d);
						if (this.jashinTransitionDirection >= 0 && !ProcedureUtils.BB.touches(bb1, bb2)) {
							this.setJashinTransitionDirection(-1);
							this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
							this.setAttackTarget(this.curseTarget);
						} else if (this.jashinTransitionDirection <= 0 && ProcedureUtils.BB.touches(bb1, bb2)) {
							this.setJashinTransitionDirection(1);
							this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
							this.curseTarget.addPotionEffect(new PotionEffect(PotionHeaviness.potion, 60, 4));
						} else if (this.jashinTransitionDirection >= 0 && stack.getItem() == ItemSpearRetractable.block
						 && this.ticksExisted > this.lastJashinTime + 40 && this.ticksExisted % 40 == 2) {
						 	ItemSpearRetractable.setHurtSelf(stack);
							this.swingArm(EnumHand.MAIN_HAND);
							this.attackEntityFrom(this.selfDamage, 100.0f * this.rand.nextFloat() * Math.min((float)(this.ticksExisted - this.lastJashinTime) / 160f, 1.0f));
							this.curseTarget.addPotionEffect(new PotionEffect(PotionHeaviness.potion, 60, this.rand.nextInt(5)));
						}
					} else if (this.onGround) {
						if (this.consumeChakra(this.jashinChakraUsage)) {
							this.jashinSymbol = new EntityJashinSymbol(this.world);
							this.jashinSymbol.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f);
							this.world.spawnEntity(this.jashinSymbol);
							this.lastJashinTime = this.ticksExisted;
							this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hidan")), 2f, 1f);
							if (stack.getItem() != ItemSpearRetractable.block && this.getItemFromInventory(1).getItem() == ItemSpearRetractable.block) {
								this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 0);
								this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 1);
							}
						} else {
							this.setAttackTarget(this.curseTarget);
							this.curseTarget = null;
						}
					}
				} else if (this.jashinTransitionDirection > 0) {
					this.setJashinTransitionDirection(-1);
					this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
					if (this.jashinSymbol != null) {
						this.jashinSymbol.fadeOut();
					}
					if (stack.getItem() == ItemSpearRetractable.block) {
						this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 1);
					}
				}
				super.updateAITasks();
			}
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			ItemStack stack = this.getHeldItemMainhand();
			if (stack.getItem() == ItemScytheHidan.block) {
				this.swingArm(EnumHand.MAIN_HAND);
				stack.onPlayerStoppedUsing(this.world, this, stack.getMaxItemUseDuration() - 50);
			}
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			if (super.attackEntityAsMob(entityIn)) {
				if (this.getHeldItemMainhand().getItem() == ItemScytheHidan.block
				 && (entityIn instanceof EntityNinjaMob.Base || entityIn instanceof EntityPlayer)) {
					this.curseTarget = (EntityLivingBase)entityIn;
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (this.curseTarget != null && this.curseTarget.isEntityAlive() && this.jashinTransitionDirection > 0) {
				this.curseTarget.attackEntityFrom(source.setDamageBypassesArmor(), amount);
			}
			amount *= source != this.selfDamage && source.isUnblockable() && source.isDamageAbsolute() ? 1.0f : (this.rand.nextFloat() * 0.08f + 0.08f);
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void onLivingUpdate() {
			super.onLivingUpdate();
			if (this.scytheOnRetrieval) {
				for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D))) {
					if (entity instanceof ItemScytheHidan.EntityCustom && this.equals(((ItemScytheHidan.EntityCustom)entity).getShooter())) {
						this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ((ItemScytheHidan.EntityCustom)entity).getArrowStack());
						entity.setDead();
						this.scytheOnRetrieval = false;
					}
				}
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.jashinSymbol != null) {
				this.jashinSymbol.fadeOut();
			}
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return SoundEvents.ENTITY_EVOCATION_ILLAGER_AMBIENT;
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return SoundEvents.ENTITY_ILLAGER_DEATH;
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere() && (int)this.posY >= this.world.getSeaLevel() && this.world.canSeeSky(this.getPosition())
			 && this.world.getEntities(EntityCustom.class, EntitySelectors.IS_ALIVE).isEmpty()
			 && !EntityNinjaMob.SpawnData.spawnedRecentlyHere(this, 36000);
			 //&& this.rand.nextInt(5) == 0;
		}

		@Override
		public boolean isNonBoss() {
			return false;
		}

		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

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
				this.bossInfo.addPlayer((EntityPlayerMP)entity);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			
			if (!this.world.isRemote) {
				if (this.ticksExisted % 20 == 2) {
					float health = this.getHealth();
					if (health > 0.0f && health < this.getMaxHealth()) {
						this.heal(1.0f);
					}
				}
				if (this.jashinTransitionDirection != 0) {
					int jashinTicks = this.getJashinTicks();
					if (jashinTicks > 0) {
						this.setJashinTicks(jashinTicks + this.jashinTransitionDirection);
					} else {
						this.jashinTransitionDirection = 0;
					}
				}
			}

			this.trackAttackedPlayers();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
		}

		public class AIMoveTowardsSymbol extends EntityAIBase {
		    private final EntityCustom entity;
		    private EntityJashinSymbol targetEntity;
		    private final double speed;
		    private final float maxTargetDistance;
		    private int ticks;
		
		    public AIMoveTowardsSymbol(EntityCustom entityIn, double speedIn) {
		        this.entity = entityIn;
		        this.speed = speedIn;
		        this.maxTargetDistance = (float)entityIn.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
		        this.setMutexBits(1);
		    }
		
		    @Override
		    public boolean shouldExecute() {
		        this.targetEntity = this.entity.jashinSymbol;
		        if (this.targetEntity == null || this.targetEntity.isDead) {
		            return false;
		        } else if (this.entity.curseTarget == null || !this.entity.curseTarget.isEntityAlive()) {
		        	return false;
		        } else {
		        	double d = this.targetEntity.getDistanceSq(this.entity);
		        	if (d > (double)(this.maxTargetDistance * this.maxTargetDistance)) {
		            	return false;
		        	} else {
	                	return d > 1.0d;
		        	}
		        }
		    }
		
		    @Override
		    public boolean shouldContinueExecuting() {
		    	double d = this.targetEntity.getDistanceSq(this.entity);
		        return !this.entity.getNavigator().noPath() && !this.targetEntity.isDead
		         && d < (double)this.maxTargetDistance * this.maxTargetDistance && d > 1.0d && this.entity.curseTarget.isEntityAlive();
		    }
		
		    @Override
		    public void resetTask() {
		        this.targetEntity = null;
		    }
		
		    @Override
		    public void startExecuting() {
		    	double d = this.entity.getDistanceSq(this.targetEntity);
		        this.entity.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.speed * Math.min(d, 16.0d) / 16.0d);
		        this.ticks = 0;
		    }

		    @Override
		    public void updateTask() {
		    	double d = this.entity.getDistanceSq(this.targetEntity);
		    	if (++this.ticks % 20 == 0) {
		    		this.entity.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.speed * Math.min(d, 16.0d) / 16.0d);
		    	}
		    }
		}
	}

	public static class EntityJashinSymbol extends Entity {
		private static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(EntityJashinSymbol.class, DataSerializers.VARINT);
		private final int maxLife = 1200;
		private final int fadeInTime = 40;
		private final int fadeOutTime = 40;

		public EntityJashinSymbol(World worldIn) {
			super(worldIn);
			this.setSize(4.0f, 0.1f);
			this.isImmuneToFire = true;
			this.setEntityInvulnerable(true);
		}

		@Override
		protected void entityInit() {
			this.dataManager.register(AGE, Integer.valueOf(0));
		}

		private void setAge(int ticks) {
			this.dataManager.set(AGE, Integer.valueOf(ticks));
		}
	
		public int getAge() {
			return ((Integer)this.getDataManager().get(AGE)).intValue();
		}

		public void fadeOut() {
			this.setAge(this.maxLife - this.fadeOutTime - 1);
		}

		@Override
		public void onUpdate() {
			if (!this.world.isRemote) {
				int age = this.getAge();
				if (age >= this.maxLife) {
					this.setDead();
				}
				this.setAge(age + 1);
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			this.setAge(compound.getInteger("age"));
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setInteger("age", this.getAge());
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
			RenderingRegistry.registerEntityRenderingHandler(EntityJashinSymbol.class, renderManager -> new RenderJashinSymbol(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/hidan.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelHidan());
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

		@SideOnly(Side.CLIENT)
		public class RenderJashinSymbol extends Render<EntityJashinSymbol> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/jashin_symbol.png");
			
			public RenderJashinSymbol(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public boolean shouldRender(EntityJashinSymbol livingEntity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
			@Override
			public void doRender(EntityJashinSymbol entity, double x, double y, double z, float entityYaw, float partialTicks) {
				float f1 = partialTicks + entity.getAge();
				float f3 = entity.width + 1.0F;
				float f4 = 1.0F;
				if (f1 <= (float)entity.fadeInTime) {
					f4 *= f1 / (float)entity.fadeInTime;
				} else if (f1 > (float)entity.maxLife - (float)entity.fadeOutTime) {
					f4 *= Math.max(1.0F - (f1 - (float)(entity.maxLife - entity.fadeOutTime)) / (float)entity.fadeOutTime, 0.0F);
				}
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + 0.01D, z);
				GlStateManager.rotate(entityYaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				Tessellator tessallator = Tessellator.getInstance();
				BufferBuilder buffer = tessallator.getBuffer();
				buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
				buffer.pos(-0.5F * f3, 0.0F, -0.5F * f3).tex(0.0D, 1.0D).color(0.6F, 0.6F, 0.6F, f4).normal(0.0F, 1.0F, 0.0F).endVertex();
				buffer.pos(-0.5F * f3, 0.0F, 0.5F * f3).tex(1.0D, 1.0D).color(0.6F, 0.6F, 0.6F, f4).normal(0.0F, 1.0F, 0.0F).endVertex();
				buffer.pos(0.5F * f3, 0.0F, 0.5F * f3).tex(1.0D, 0.0D).color(0.6F, 0.6F, 0.6F, f4).normal(0.0F, 1.0F, 0.0F).endVertex();
				buffer.pos(0.5F * f3, 0.0F, -0.5F * f3).tex(0.0D, 0.0D).color(0.6F, 0.6F, 0.6F, f4).normal(0.0F, 1.0F, 0.0F).endVertex();
				tessallator.draw();
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityJashinSymbol entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 3.7.5
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelHidan extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer jashinHead;
			private final ModelRenderer jashinBody;
			private final ModelRenderer jashinRightArm;
			private final ModelRenderer jashinLeftArm;
		
			public ModelHidan() {
				textureWidth = 64;
				textureHeight = 64;
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, -0.1F, false));
		
				jashinHead = new ModelRenderer(this);
				jashinHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				jashinHead.cubeList.add(new ModelBox(jashinHead, 0, 48, -4.0F, -8.0F, -4.0F, 8, 8, 8, -0.1F, false));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.1F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.6F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -2.0F, 2.75F, -3.25F, 4, 4, 0, -1.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 4, -2.0F, 0.85F, -2.25F, 4, 4, 0, 0.0F, false));
		
				jashinBody = new ModelRenderer(this);
				jashinBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				jashinBody.cubeList.add(new ModelBox(jashinBody, 32, 48, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
		
				jashinRightArm = new ModelRenderer(this);
				jashinRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				jashinRightArm.cubeList.add(new ModelBox(jashinRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
		
				jashinLeftArm = new ModelRenderer(this);
				jashinLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				jashinLeftArm.cubeList.add(new ModelBox(jashinLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, true));
			}

			@Override
			public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
				float jashinTicks = entityIn instanceof EntityCustom ? (float)((EntityCustom)entityIn).getJashinTicks() : 0.0F;
				if (jashinTicks > 0.0F) {
					copyModelAngles(bipedHead, jashinHead);
					copyModelAngles(bipedBody, jashinBody);
					copyModelAngles(bipedRightArm, jashinRightArm);
					copyModelAngles(bipedLeftArm, jashinLeftArm);
					jashinTicks += ageInTicks - (float)entityIn.ticksExisted;
					GlStateManager.alphaFunc(0x204, 0.01f);
					GlStateManager.color(1.0F, 1.0F, 1.0F, Math.min(jashinTicks / (float)((EntityCustom)entityIn).transitionTime, 1.0F));
					jashinHead.render(scale);
					jashinBody.render(scale);
					jashinRightArm.render(scale);
					jashinLeftArm.render(scale);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.alphaFunc(0x204, 0.1f);
				}
				GlStateManager.disableBlend();
			}
		}
	}
}
