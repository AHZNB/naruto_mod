
package net.narutomod.entity;

import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.event.entity.living.LivingSpawnEvent;
//import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
//import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.Village;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.narutomod.item.ItemBijuMap;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class EntityNinjaMerchant extends ElementsNarutomodMod.ModElement {
	public EntityNinjaMerchant(ElementsNarutomodMod instance) {
		super(instance, 435);
	}

	public enum TradeLevel {
		COMMON,
		UNCOMMON,
		RARE
	}

	public abstract static class Base extends EntityNinjaMob.Base implements IMerchant {
		private Map<TradeLevel, MerchantRecipeList> trades;
		private final List<UUID> assholeList = Lists.newArrayList();
		private EntityPlayer customer;
		private int homeCheckTimer;
		private Village village;
		private int recipeResetTime;
		private boolean hasTraded;
		private int level;
		protected EntityNinjaMob.AILeapAtTarget leapAI = new EntityNinjaMob.AILeapAtTarget(this, 1.0F);

		public Base(World worldIn, int level) {
			this(worldIn, level, (double)level * level);
		}

		public Base(World worldIn, int level, double maxchakra) {
			super(worldIn, level, maxchakra);
			this.tasks.addTask(2, this.leapAI);
			this.trades = this.getTrades();
			this.addRareTrades();
			((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
		}

		@Override
		public String toString() {
			return super.toString();
		}

		public abstract Map<TradeLevel, MerchantRecipeList> getTrades();

		// This code can be used in the future to add rare trades
		private void addRareTrades() {
			MerchantRecipeList rareTrades = this.trades.get(TradeLevel.RARE);

			if (rareTrades == null) {
				rareTrades = new MerchantRecipeList();
			}
			rareTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 64), ItemStack.EMPTY, new ItemStack(ItemBijuMap.block, 1), 0, 1));
			this.trades.put(TradeLevel.RARE, rareTrades);
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			compound.setBoolean("hasTraded", this.hasTraded);
			compound.setInteger("level", this.level);

			NBTTagCompound tradesTag = new NBTTagCompound();

			for (Map.Entry<TradeLevel, MerchantRecipeList> entry : trades.entrySet()) {
				tradesTag.setTag(entry.getKey().name(), entry.getValue().getRecipiesAsTags());
			}
			compound.setTag("trades", tradesTag);

			return super.writeToNBT(compound);
		}

		@Override
		public void readFromNBT(NBTTagCompound compound) {
			super.readFromNBT(compound);

			this.hasTraded = compound.getBoolean("hasTraded");
			this.level = compound.getInteger("level");

			NBTTagCompound tradesTag = compound.getCompoundTag("trades");

			this.trades = Maps.newHashMap();

			for (String key : tradesTag.getKeySet()) {
				TradeLevel level = TradeLevel.valueOf(key);
				NBTTagCompound recipeListTag = tradesTag.getCompoundTag(key);
				MerchantRecipeList recipeList = new MerchantRecipeList();
				recipeList.readRecipiesFromTags(recipeListTag);
				this.trades.put(level, recipeList);
			}
		}

		@Override
		protected boolean canDespawn() {
			return !this.hasTraded;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, true));
			this.tasks.addTask(7, new AITradePlayer(this));
			this.tasks.addTask(8, new AIWatchCustomer(this));
			this.tasks.addTask(9, new EntityAIOpenDoor(this, true));
			this.tasks.addTask(10, new EntityAIMoveTowardsRestriction(this, 0.8D));
			this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 15f, 0.05f));
			this.tasks.addTask(12, new EntityAIWanderAvoidWater(this, 0.6));
			this.tasks.addTask(13, new EntityAIWatchClosest(this, EntityLiving.class, 8f));
			this.targetTasks.addTask(1, new AIDefendVillage(this));
			this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
		}

		@Override
		public void setCustomer(@Nullable EntityPlayer player) {
			this.customer = player;
		}

		@Override
		@Nullable
		public EntityPlayer getCustomer() {
			return this.customer;
		}

		public boolean isTrading() {
			return this.customer != null;
		}

		@Override
		@Nullable
		public MerchantRecipeList getRecipes(EntityPlayer player) {
			MerchantRecipeList recipes = this.trades.entrySet().stream()
					.filter(entry -> entry.getKey().ordinal() <= this.level)
					.flatMap(entry -> entry.getValue().stream())
					.collect(Collectors.toCollection(MerchantRecipeList::new));
			return recipes.isEmpty() ? null : recipes;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void setRecipes(@Nullable MerchantRecipeList recipeList) {
		}

		@Override
		public void useRecipe(MerchantRecipe recipe) {
			recipe.incrementToolUses();
			this.livingSoundTime = -this.getTalkInterval();
			this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
			if (recipe.isRecipeDisabled() && this.recipeResetTime <= 0) {
				this.recipeResetTime = 6000;
			}
			if (this.customer != null && this.village != null) {
				this.village.modifyPlayerReputation(this.customer.getUniqueID(), 1);
			}
		}

		@Override
		public void verifySellingItem(ItemStack stack) {
			if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
				this.livingSoundTime = -this.getTalkInterval();
				this.playSound(stack.isEmpty() ? SoundEvents.ENTITY_VILLAGER_NO : SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
				this.hasTraded = true;
				this.level++;
			}
		}

		public Village getVillage() {
			return this.village;
		}

		@Override
		protected void updateAITasks() {
			if (--this.homeCheckTimer <= 0) {
				this.homeCheckTimer = 70 + this.rand.nextInt(50);
				this.village = this.world.getVillageCollection().getNearestVillage(new BlockPos(this), 96);
				if (this.village == null) {
					this.detachHome();
				} else {
					BlockPos blockpos = this.village.getCenter();
					this.setHomePosAndDistance(blockpos, (int) (this.village.getVillageRadius() * 0.6F));
				}
			}
			if (!this.isTrading() && this.recipeResetTime > 0) {
				--this.recipeResetTime;
				if (this.recipeResetTime <= 0) {
					for (MerchantRecipeList recipeList : this.trades.values()) {
						for (MerchantRecipe recipe : recipeList) {
							if (recipe.isRecipeDisabled()) {
								recipe.increaseMaxTradeUses(1);
							}
						}
					}
				}
			}
			super.updateAITasks();
		}

		@Override
		public boolean processInteract(EntityPlayer player, EnumHand hand) {
			ItemStack stack = player.getHeldItem(hand);

			if (stack.isEmpty() && this.isEntityAlive() && !this.isTrading() && !player.isSneaking()) {
				if (!this.world.isRemote && this.getRecipes(player) != null && !this.assholeList.contains(player.getUniqueID())) {
					this.setCustomer(player);
					player.displayVillagerTradeGui(this);
				} else if (this.world.isRemote) {
					return true;
				}
			}
			return super.processInteract(player, hand);
		}

		@Override
		public boolean getCanSpawnHere() {
			//System.out.println(">>> got here. " + this);
			this.village = this.world.getVillageCollection().getNearestVillage(new BlockPos(this), 32);
			if (this.village == null
					|| this.world.getEntitiesWithinAABB(Base.class, new AxisAlignedBB(this.village.getCenter()).grow(96d, 10d, 96d)).size() >= 2
					|| this.rand.nextInt(10) != 0) {
				return false;
			}
			return super.getCanSpawnHere();
		}

		@Override
		public ITextComponent getDisplayName() {
			return super.getDisplayName();
		}

		@Override
		public World getWorld() {
			return this.world;
		}

		@Override
		public BlockPos getPos() {
			return new BlockPos(this);
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return this.isTrading() ? SoundEvents.ENTITY_VILLAGER_TRADING : null;
		}

		@Override
		public void onDeath(DamageSource cause) {
			if (this.attackingPlayer != null && this.village != null) {
				this.village.modifyPlayerReputation(this.attackingPlayer.getUniqueID(), -5);
			}
			super.onDeath(cause);
		}

		@Override
		public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
			super.setRevengeTarget(livingBase);
			if (livingBase instanceof EntityPlayer) {
				this.assholeList.add(livingBase.getUniqueID());

				if (this.village != null) {
					this.village.modifyPlayerReputation(livingBase.getUniqueID(), -1);
				}
			}
		}

		public class AITradePlayer extends EntityAIBase {
			private final Base merchant;

			public AITradePlayer(Base merchantIn) {
				this.merchant = merchantIn;
				this.setMutexBits(5);
			}

			@Override
			public boolean shouldExecute() {
				if (!this.merchant.isEntityAlive() || this.merchant.isInWater()
						|| !this.merchant.onGround || this.merchant.velocityChanged) {
					return false;
				} else {
					EntityPlayer entityplayer = this.merchant.getCustomer();
					if (entityplayer == null || this.merchant.getDistanceSq(entityplayer) > 16.0D) {
						return false;
					} else {
						return entityplayer.openContainer != null;
					}
				}
			}

			@Override
			public void startExecuting() {
				this.merchant.getNavigator().clearPath();
			}

			@Override
			public void resetTask() {
				this.merchant.setCustomer((EntityPlayer)null);
			}
		}

		public class AIWatchCustomer extends EntityAIWatchClosest {
			public AIWatchCustomer(Base merchant) {
				super(merchant, EntityPlayer.class, 8.0F);
			}

			@Override
			public boolean shouldExecute() {
				if (((Base)this.entity).isTrading()) {
					this.closestEntity = ((Base)this.entity).getCustomer();
					return true;
				}
				return false;
			}
		}

		public class AIDefendVillage extends EntityAITarget {
			Base ninja;
			EntityLivingBase villageAgressorTarget;

			public AIDefendVillage(Base entityIn) {
				super(entityIn, false, true);
				this.ninja = entityIn;
				this.setMutexBits(1);
			}

			@Override
			public boolean shouldExecute() {
				Village village = this.ninja.getVillage();
				if (village == null) {
					return false;
				} else {
					this.villageAgressorTarget = village.findNearestVillageAggressor(this.ninja);
					if (this.villageAgressorTarget instanceof net.minecraft.entity.monster.EntityCreeper) {
						return false;
					} else if (this.isSuitableTarget(this.villageAgressorTarget, false)) {
						return true;
					} else {
						this.villageAgressorTarget = this.findNearestVillagerChaser(this.ninja.world, village);
						if (this.isSuitableTarget(this.villageAgressorTarget, false)) {
							return true;
						} else if (this.taskOwner.getRNG().nextInt(20) == 0) {
							this.villageAgressorTarget = village.getNearestTargetPlayer(this.ninja);
							return this.isSuitableTarget(this.villageAgressorTarget, false);
						}
						return false;
					}
				}
			}

			@Nullable
			private EntityLivingBase findNearestVillagerChaser(World world, Village village) {
				List<EntityVillager> list = world.<EntityVillager>getEntitiesWithinAABB(EntityVillager.class,
						new AxisAlignedBB(village.getCenter()).grow(village.getVillageRadius(), 8d, village.getVillageRadius()));
				//new AxisAlignedBB((double)(village.getCenter().getX() - village.getVillageRadius()),
				// (double)(village.getCenter().getY() - 4), (double)(village.getCenter().getZ() - village.getVillageRadius()),
				// (double)(village.getCenter().getX() + village.getVillageRadius()),
				// (double)(village.getCenter().getY() + 4), (double)(village.getCenter().getZ() + village.getVillageRadius())));
				Map<EntityLivingBase, Double> aggressorMap = Maps.<EntityLivingBase, Double>newHashMap();
				for (EntityVillager villager : list) {
					List<EntityZombie> list2 = world.<EntityZombie>getEntitiesWithinAABB(EntityZombie.class,
							villager.getEntityBoundingBox().grow(8d, 3d, 8d));
					if (!list2.isEmpty()) {
						list2.sort(new ProcedureUtils.EntitySorter(villager));
						EntityZombie zombie = list2.get(0);
						double d = zombie.getDistance(villager);
						if (!aggressorMap.containsKey(zombie) || aggressorMap.get(zombie) > d) {
							aggressorMap.put(zombie, d);
						}
					}
				}
				LinkedHashMap<EntityLivingBase, Double> sortedMap = new LinkedHashMap<>();
				aggressorMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
						.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
				Iterator<EntityLivingBase> iter = sortedMap.keySet().iterator();
				return iter.hasNext() ? iter.next() : null;
			}

			@Override
			public void startExecuting() {
				this.ninja.setAttackTarget(this.villageAgressorTarget);
				super.startExecuting();
			}
		}
	}

	/*public class CheckSpawnHook {
		@SubscribeEvent
		public void onSpawnCheck(LivingSpawnEvent.CheckSpawn event) {
			EntityLivingBase entity = event.getEntityLiving();
			if (!event.isSpawner() && entity instanceof Base) {
				entity.setPosition(event.getX(), event.getY(), event.getZ());
				if (!((Base)entity).getCanSpawnHere()) {
					event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
				}
			}
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new CheckSpawnHook());
	}*/
}
