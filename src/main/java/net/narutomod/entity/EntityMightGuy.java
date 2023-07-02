
package net.narutomod.entity;

import com.google.common.collect.Maps;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.potion.PotionEffect;
//import net.minecraft.nbt.NBTTagList;
//import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.entity.monster.IMob;
import net.minecraft.village.Village;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemEightGates;
import net.narutomod.item.ItemNinjaArmorJumpsuit;
import net.narutomod.event.SpecialEvent;
import net.narutomod.event.EventVillageSiege;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class EntityMightGuy extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 111;
	public static final int ENTITYID_RANGED = 112;

	public EntityMightGuy(ElementsNarutomodMod instance) {
		super(instance, 323);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "mightguy"), ENTITYID)
				.name("mightguy").tracker(64, 3, true).egg(-16751104, -3355648).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		EntityRegistry.addSpawn(EntityCustom.class, 20, 1, 1, EnumCreatureType.AMBIENT,
				Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.FOREST, Biomes.BIRCH_FOREST,
				Biomes.BIRCH_FOREST_HILLS, Biomes.FOREST_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_EDGE,
				Biomes.JUNGLE_HILLS, Biomes.ROOFED_FOREST, Biomes.REDWOOD_TAIGA, Biomes.REDWOOD_TAIGA_HILLS,
				Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.MUTATED_SAVANNA, Biomes.MUTATED_FOREST,
				Biomes.MUTATED_BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST_HILLS, Biomes.MUTATED_JUNGLE,
				Biomes.MUTATED_JUNGLE_EDGE, Biomes.MUTATED_ROOFED_FOREST, Biomes.MUTATED_TAIGA,
				Biomes.MUTATED_TAIGA_COLD, Biomes.MUTATED_REDWOOD_TAIGA, Biomes.MUTATED_REDWOOD_TAIGA_HILLS);
	}

	public static class EntityCustom extends EntityNinjaMerchant.Base implements IRangedAttackMob {
		private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		//private final NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
		private int closeGatesCountdown;
		private int gateCooldown;
		private int attackCooldown;
		private EntityPlayer customer;
		private int siegeStartingVillagers;
		private ScoreObjective customerKillCount;
		private int killCount;
		private float gateOpened;

		public EntityCustom(World world) {
			super(world, 120, 5000d);
			this.setSize(0.6f, 2.0f);
			this.tasks.removeTask(this.leapAI);
			this.setDropChance(EntityEquipmentSlot.MAINHAND, 0f);
			this.gateCooldown = 100;
		}

		@Override
		public Map<EntityNinjaMerchant.TradeLevel, MerchantRecipeList> getTrades() {
			Map<EntityNinjaMerchant.TradeLevel, MerchantRecipeList> trades = Maps.newHashMap();
			MerchantRecipeList commonTrades = new MerchantRecipeList();
			commonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, new ItemStack(ItemNinjaArmorJumpsuit.legs, 1), 0, 1));
			trades.put(EntityNinjaMerchant.TradeLevel.COMMON, commonTrades);
			return trades;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			livingdata = super.onInitialSpawn(difficulty, livingdata);
			this.setItemToInventory(new ItemStack(ItemEightGates.block), 0);
			return livingdata;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.2d, 200, 30f) {
				@Override
				public boolean shouldExecute() {
					return !EntityCustom.this.hasHome() && EntityCustom.this.gateOpened >= 7f && super.shouldExecute();
				}
			});
			this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 1.0F) {
				@Override
				public boolean shouldExecute() {
					this.target = this.leaper.getAttackTarget();
					if (this.target != null) {
						double d1 = this.target.posX - this.leaper.posX;
						double d2 = this.target.posY - this.leaper.posY;
						double d3 = this.target.posZ - this.leaper.posZ;
						double d0 = d1 * d1 + d2 * d2 + d3 * d3;
						double d4 = d1 * d1 + d3 * d3;
						if (d0 >= 9.0D && (d0 <= 144.0d || d4 < 256d) && this.leaper.onGround) {
							return this.leaper.getRNG().nextInt(5) == 0;
						}
					}
					return false;
				}
				@Override
				public void startExecuting() {
					Vec3d vec = this.leaper.getPositionVector().subtract(this.target.getPositionVector())
							.normalize().add(this.target.getPositionVector());
					if (this.leaper.world.rayTraceBlocks(this.leaper.getPositionEyes(1f), vec, false, true, false) == null) {
						this.leaper.setPositionAndRotation(vec.x, vec.y, vec.z, this.leaper.rotationYawHead, this.leaper.rotationPitch);
						this.leaper.motionX = 0.0d;
						this.leaper.motionY = 0.0d;
						this.leaper.motionZ = 0.0d;
						this.leaper.isAirBorne = true;
					} else {
						super.startExecuting();
					}
				}
			});
			this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityMob.class, false, false) {
				@Override
				public boolean shouldExecute() {
					return EntityCustom.this.hasHome() && super.shouldExecute();
				}
			});
			this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this,
					EntityNinjaMob.Base.class, 10, false, false, IMob.MOB_SELECTOR) {
				@Override
				public boolean shouldExecute() {
					return EntityCustom.this.hasHome() && super.shouldExecute();
				}
			});
		}

		@Override
		protected boolean canDespawn() {
			return !this.hasHome();
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return SoundEvents.ENTITY_ILLUSION_ILLAGER_AMBIENT;
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return SoundEvents.ENTITY_ILLUSION_ILLAGER_HURT;
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return SoundEvents.ENTITY_ILLAGER_DEATH;
		}

		private boolean isEntityInFOV(Entity entityIn) {
			return ProcedureUtils.isEntityInFOV(this, entityIn);
			//double yaw = -MathHelper.atan2(entityIn.posX - this.posX, entityIn.posZ - this.posZ) * (180d / Math.PI);
			//yaw = Math.abs(MathHelper.wrapDegrees(yaw - this.rotationYawHead));
			//return yaw < 85d && this.getEntitySenses().canSee(entityIn);
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || EntityNinjaMob.TeamKonoha.contains(entityIn.getClass())
					|| (this.isTrackingCustomer() && entityIn.equals(this.customer));
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.FALL)
				return false;
			if (source.getTrueSource() instanceof EntityPlayer && this.getVillage() != null) {
				this.getVillage().modifyPlayerReputation(source.getTrueSource().getUniqueID(), -3);
			}
			if (source.getTrueSource() instanceof EntityLivingBase
					&& !source.isUnblockable() && this.isEntityInFOV(source.getTrueSource())) {
				amount *= this.rand.nextFloat() * 0.2f;
				this.swingArm(EnumHand.OFF_HAND);
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			boolean flag = true;
			this.swingArm(EnumHand.MAIN_HAND);
			if ((int)this.gateOpened == 6 && !this.hasHome()) {
				ItemStack stack = this.getHeldItemMainhand();
				if (stack.getItem() == ItemEightGates.block) {
					Vec3d vec = entityIn.getPositionEyes(1f).subtract(this.getPositionEyes(1f));
					((ItemEightGates.RangedItem)stack.getItem()).attackAsakujaku(this, vec.x, vec.y, vec.z);
				}
			} else {
				float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
				flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f * (1f + (this.rand.nextFloat() * 0.4f)));
				if (flag) {
					entityIn.motionY += f * 0.02d / entityIn.height;
					this.applyEnchantments(this, entityIn);
				}
			}
			return flag;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected void updateAITasks() {
			EntityLivingBase target = this.getAttackTarget();
			if (target != null && target.isEntityAlive()) {
				if (this.gateCooldown == 0) {
					float gate = 0f;
					if (this.getHealth() < this.getMaxHealth() / 2) {
						gate = 3.5f;
					}
					float targetStrength = (float) ProcedureUtils.getModifiedAttackDamage(target);
					if (targetStrength > 10.0f || target.getMaxHealth() >= 50.0f) {
						double d = Chakra.getLevel(target);
						gate = MathHelper.sqrt((targetStrength + d) * (target.getMaxHealth() + d)) / 25f;
					}
					if (gate >= 1f) {
						this.tryOpenGate(gate);
					}
				}
				if (--this.attackCooldown <= 0 && this.getDistance(target) <= 2d + target.width) {
					this.attackCooldown = 10;
					this.attackEntityAsMob(target);
				}
			} else {
				this.setAttackTarget(null);
				if (--this.closeGatesCountdown <= 0) {
					this.closeGates();
				}
			}
			if (this.gateCooldown > 0) {
				--this.gateCooldown;
			}
			super.updateAITasks();
		}

		@Override
		public boolean getCanSpawnHere() {
			Village village = this.world.getVillageCollection().getNearestVillage(new BlockPos(this), 32);
			if (village == null || village.getNumVillageDoors() < 20 || village.getNumVillagers() < 10
					|| !this.world.getEntities(EntityCustom.class, EntitySelectors.IS_ALIVE).isEmpty()) {
				return false;
			}
			return super.getCanSpawnHere();
		}

		@Override
		public void onKillEntity(EntityLivingBase entity) {
			if (this.isTrackingCustomer()) {
				++this.killCount;
				this.world.getScoreboard().getOrCreateScore(this.getName(), this.customerKillCount).setScorePoints(this.killCount);
			}
		}

		public boolean isTrackingCustomer() {
			return this.customerKillCount != null;
		}

		public void startTrackingCustomer() {
			if (!this.world.isRemote && this.customer != null && this.getVillage() != null) {
				ProcedureUtils.sendChat(this.customer, TextFormatting.GREEN + I18n.translateToLocal("entity.mightguy.name") + ": "
						+ TextFormatting.WHITE + I18n.translateToLocal("chattext.mightguy.interact2"));
				this.siegeStartingVillagers = this.getVillage().getNumVillagers();
				Scoreboard _sc = this.world.getScoreboard();
				this.customerKillCount = _sc.getObjective("siege_kills");
				if (this.customerKillCount == null) {
					this.customerKillCount = _sc.addScoreObjective("siege_kills", ScoreCriteria.TOTAL_KILL_COUNT);
					this.customerKillCount.setDisplayName(I18n.translateToLocal("scoreboard.objective.siege_kills"));
				}
				_sc.getOrCreateScore(this.getName(), this.customerKillCount).setScorePoints(0);
				_sc.getOrCreateScore(this.customer.getName(), this.customerKillCount).setScorePoints(0);
				_sc.setObjectiveInDisplaySlot(1, this.customerKillCount);
			}
		}

		public void stopTrackingCustomer() {
			if (!this.world.isRemote && this.customer != null && this.getVillage() != null && this.customerKillCount != null) {
				int kills = this.world.getScoreboard().getOrCreateScore(this.customer.getName(), this.customerKillCount).getScorePoints();
				int villagersKilled = this.siegeStartingVillagers - this.getVillage().getNumVillagers();
				if (villagersKilled > 0 || kills <= this.killCount / 2 || !this.customer.isEntityAlive()) {
					// failed
					ProcedureUtils.sendChat(this.customer, TextFormatting.GREEN + I18n.translateToLocal("entity.mightguy.name") + ": "
							+ TextFormatting.WHITE + I18n.translateToLocal("chattext.mightguy.interact4"));
					this.getVillage().modifyPlayerReputation(this.customer.getUniqueID(), -3);
				} else {
					ProcedureUtils.sendChat(this.customer, TextFormatting.GREEN + I18n.translateToLocal("entity.mightguy.name") + ": "
							+ TextFormatting.WHITE + I18n.translateToLocal("chattext.mightguy.interact3"));
					this.getVillage().modifyPlayerReputation(this.customer.getUniqueID(), 3);
					ItemHandlerHelper.giveItemToPlayer(this.customer, new ItemStack(ItemEightGates.block));
					ProcedureUtils.grantAdvancement((EntityPlayerMP)this.customer, "narutomod:openedgates", true);
				}
				this.customer.sendStatusMessage(new TextComponentString(
						"Villagers killed: " + villagersKilled + ", your kills: " + kills + ", Might Guy's kills: " + this.killCount), false);
				this.customer.getWorldScoreboard().removeObjective(this.customerKillCount);
				this.customer = null;
				this.customerKillCount = null;
				this.killCount = 0;
			}
		}

		@Override
		public boolean processInteract(EntityPlayer player, EnumHand hand) {
			if (!this.world.isRemote && this.getVillage() != null) {
				Village village = this.getVillage();
				if (this.customer == null && village.getPlayerReputation(player.getUniqueID()) >= 0 && !this.isTrading()) {
					this.customer = player;
					ProcedureUtils.sendChat(player, TextFormatting.GREEN + I18n.translateToLocal("entity.mightguy.name") + ": "
							+ TextFormatting.WHITE + I18n.translateToLocal("chattext.mightguy.interact1"));
					long startTime = this.world.getTotalWorldTime() + 18000L - (this.world.getWorldTime() % 24000L);
					new EventVillageSiege(this.world, null, village.getCenter().getX(), village.getCenter().getY(),
							village.getCenter().getZ(), startTime, village.getVillageRadius() + 5, 60) {
						@Override
						protected void doOnTick(int currentTick) {
							if (currentTick == 0) startTrackingCustomer();
							else stopTrackingCustomer();
						}
					};
					return true;
				}
			}
			return super.processInteract(player, hand);
		}

		private void tryOpenGate(float gate) {
			if (this.getHeldItemMainhand().getItem() == ItemEightGates.block) {
				this.gateOpened = this.getGateOpened();
				if (this.getHealth() < this.getMaxHealth() * 0.9f) {
					if (gate > 4f && this.gateOpened < 4f) {
						gate = 3.5f;
					} else if (this.getHealth() < 4f && this.gateOpened >= 4f) {
						this.closeGates();
						return;
					}
				}
				if (this.gateOpened < Math.min(gate, 7f)) {
					if (!this.isHandActive()) {
						this.setSneaking(true);
						this.setActiveHand(EnumHand.MAIN_HAND);
					}
				} else if (this.isHandActive()) {
					this.resetActiveHand();
					this.setSneaking(false);
				}
				this.closeGatesCountdown = 100;
			} else {
				this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 0);
			}
		}

		private void closeGates() {
			if (this.getGateOpened() > 0f) {
				this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 0);
				this.gateOpened = 0f;
				this.gateCooldown = 600;
			}
		}

		private float getGateOpened() {
			ItemStack stack = this.getHeldItemMainhand();
			return stack.getItem() == ItemEightGates.block ? ((ItemEightGates.RangedItem)stack.getItem()).getGateOpened(stack) : 0f;
		}

		private void debugChatInfo() {
			float gateOpened = this.getGateOpened();
			ProcedureUtils.sendChatAll("main hand:" + this.getHeldItemMainhand() + ", gate opened:" + gateOpened + ", dmg:" + ProcedureUtils.getModifiedAttackDamage(this)
					+ ", health:" + this.getMaxHealth());
		}

		private void clampVelocity(double d) {
			Vec3d vec3d = new Vec3d(this.motionX, this.motionY, this.motionZ);
			if (vec3d.lengthVector() > d) {
				vec3d = vec3d.scale(d / vec3d.lengthVector());
				ProcedureUtils.setVelocity(this, vec3d.x, vec3d.y, vec3d.z);
			}
		}

		@Override
		public void onEntityUpdate() {
			if (!this.world.isRemote && this.ticksExisted == 1) {
				ProcedureUtils.sendChatAll(I18n.translateToLocal("chattext.mightguy.arrival"));
				this.playSound((net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("narutomod:howl_youth")), 10f, 1f);
			}
			if (this.ticksExisted % 100 == 0) {
				this.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 101, 5, false, false));
				if (this.getHealth() < this.getMaxHealth())
					this.heal(5f);
			}
			if (this.customer != null
					&& (!this.customer.isEntityAlive() || (this.customer instanceof EntityPlayerMP
					&& ((EntityPlayerMP)this.customer).hasDisconnected())) && this.isTrackingCustomer()) {
				this.stopTrackingCustomer();
			}
			this.clampVelocity(1d);
			//this.decrementAnimations();
			super.onEntityUpdate();
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
			this.dataManager.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
		}

		public boolean isSwingingArms() {
			return ((Boolean)this.dataManager.get(SWINGING_ARMS)).booleanValue();
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			if (this.getHeldItemMainhand().getItem() == ItemEightGates.block) {
				((ItemEightGates.RangedItem)this.getHeldItemMainhand().getItem()).attackHirudora(this);
			}
		}

		@Override
		public boolean isNonBoss() {
			return false;
		}

		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS);

		@Override
		public void addTrackingPlayer(EntityPlayerMP player) {
			super.addTrackingPlayer(player);
			this.bossInfo.addPlayer(player);
		}

		@Override
		public void removeTrackingPlayer(EntityPlayerMP player) {
			super.removeTrackingPlayer(player);
			this.bossInfo.removePlayer(player);
		}

		private void trackAttackingPlayer() {
			Entity attacker = this.getAttackingEntity();
			if (attacker instanceof EntityPlayerMP || (attacker = this.getAttackTarget()) instanceof EntityPlayerMP) {
				this.bossInfo.addPlayer((EntityPlayerMP) attacker);
			} else {
				java.util.List<EntityPlayerMP> list = new java.util.ArrayList<EntityPlayerMP>();
				for (EntityPlayerMP entityplayermp : this.bossInfo.getPlayers())
					list.add(entityplayermp);
				for (EntityPlayerMP entityplayermp : list)
					this.bossInfo.removePlayer(entityplayermp);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.trackAttackingPlayer();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				RenderBiped customRender = new EntityNinjaMob.RenderBase<EntityCustom>(renderManager, new ModelMightguy()) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/might_guy.png");
					@Override
					protected ResourceLocation getEntityTexture(EntityCustom entity) {
						return texture;
					}
					@Override
					protected int getColorMultiplier(EntityCustom entity, float lightBrightness, float partialTick) {
						if (entity.getGateOpened() >= 3f) {
							return 0xB0C00000;
						}
						return super.getColorMultiplier(entity, lightBrightness, partialTick);
					}
				};
			/*customRender.addLayer(new net.minecraft.client.renderer.entity.layers.LayerBipedArmor(customRender) {
				protected void initArmor() {
					this.modelLeggings = new ModelBiped(0.5f);
					this.modelArmor = new ModelBiped(1);
				}
			});*/
				return customRender;
			});
		}

		@SideOnly(Side.CLIENT)
		public class ModelMightguy extends ModelBiped {
			public ModelMightguy() {
				super(0f, 0f, 64, 64);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
				//bipedRightArm = new ModelRenderer(this);
				//bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				//bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
				//bipedLeftArm = new ModelRenderer(this);
				//bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				//bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
				//bipedRightLeg = new ModelRenderer(this);
				//bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				//bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
				//bipedLeftLeg = new ModelRenderer(this);
				//bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				//bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
			}

			@Override
			public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
				super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTickTime);
				if (((EntityCustom) entity).isSwingingArms()) {
					this.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
				}
			}
		}
	}
}
