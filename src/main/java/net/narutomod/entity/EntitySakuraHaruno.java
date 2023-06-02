
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.potion.PotionEffect;

import net.narutomod.item.ItemScrollHealing;
import net.narutomod.item.ItemScrollEnhancedStrength;
import net.narutomod.item.ItemMilitaryRationsPill;
import net.narutomod.item.ItemMilitaryRationsPillGold;
import net.narutomod.item.ItemIryoJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.potion.PotionChakraEnhancedStrength;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySakuraHaruno extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 168;
	public static final int ENTITYID_RANGED = 169;

	public EntitySakuraHaruno(ElementsNarutomodMod instance) {
		super(instance, 439);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "sakura_haruno"), ENTITYID).name("sakura_haruno")
		 .tracker(64, 3, true).egg(-3407668, -26215).build());
	}


	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityCustom.LivingHook());
		EntityRegistry.addSpawn(EntityCustom.class, 1, 1, 1, EnumCreatureType.AMBIENT,
			Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.FOREST_HILLS,
			Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.PLAINS, Biomes.ROOFED_FOREST, Biomes.TAIGA,
			Biomes.TAIGA_HILLS, Biomes.REDWOOD_TAIGA, Biomes.REDWOOD_TAIGA_HILLS, Biomes.MUTATED_PLAINS,
			Biomes.MUTATED_FOREST, Biomes.MUTATED_BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST_HILLS, Biomes.MUTATED_JUNGLE,
			Biomes.MUTATED_JUNGLE_EDGE, Biomes.MUTATED_ROOFED_FOREST, Biomes.MUTATED_TAIGA, Biomes.MUTATED_REDWOOD_TAIGA,
			Biomes.MUTATED_REDWOOD_TAIGA_HILLS);
	}

	public static class EntityCustom extends EntityNinjaMerchant.Base {
		private EntityLivingBase healTarget;
		private List<EntityLivingBase> healableEntities = Lists.newArrayList();

		public EntityCustom(World world) {
			super(world, 80);
			this.setSize(0.525f, 1.75f);
			Arrays.fill(this.inventoryHandsDropChances, 0.0F);
		}

		@Override
		public Map<EntityNinjaMerchant.TradeLevel, MerchantRecipeList> getTrades() {
			Map<EntityNinjaMerchant.TradeLevel, MerchantRecipeList> trades = Maps.newHashMap();

			MerchantRecipeList commonTrades = new MerchantRecipeList();
			commonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 1), ItemStack.EMPTY, new ItemStack(Items.BAKED_POTATO, 3), 0, 1));
			commonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 5), ItemStack.EMPTY, new ItemStack(ItemMilitaryRationsPill.block, 2), 0, 1));

			MerchantRecipeList uncommonTrades = new MerchantRecipeList();
			uncommonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 20), ItemStack.EMPTY, new ItemStack(ItemScrollHealing.block, 1), 0, 1));
			uncommonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 15), ItemStack.EMPTY, new ItemStack(ItemMilitaryRationsPillGold.block, 1, 1), 0, 1));

			MerchantRecipeList rareTrades = new MerchantRecipeList();
			rareTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 45), ItemStack.EMPTY, new ItemStack(ItemScrollEnhancedStrength.block, 1), 0, 1));

			trades.put(EntityNinjaMerchant.TradeLevel.COMMON, commonTrades);
			trades.put(EntityNinjaMerchant.TradeLevel.UNCOMMON, uncommonTrades);
			trades.put(EntityNinjaMerchant.TradeLevel.RARE, rareTrades);

			return trades;
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			livingdata = super.onInitialSpawn(difficulty, livingdata);
			ItemStack iryoJutsu = new ItemStack(ItemIryoJutsu.block);
			ItemIryoJutsu.RangedItem item = (ItemIryoJutsu.RangedItem)iryoJutsu.getItem();
			item.setOwner(iryoJutsu, this);
			item.enableJutsu(iryoJutsu, ItemIryoJutsu.HEALING, true);
			item.addCurrentJutsuXp(iryoJutsu, item.getCurrentJutsuRequiredXp(iryoJutsu));
			this.setItemToInventory(iryoJutsu, 0);
			return livingdata;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityZombie.class, false, false) {
				@Override
				public boolean shouldExecute() {
					return EntityCustom.this.hasHome() && super.shouldExecute();
				}
			});
			this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityCreeper.class, false, false) {
				@Override
				public boolean shouldExecute() {
					return EntityCustom.this.hasHome() && super.shouldExecute();
				}
			});
			this.tasks.addTask(1, new AIHeal(this, 1.2d));
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return EntityNinjaMob.TeamKonoha.contains(entityIn.getClass()) || this.healableEntities.contains(entityIn);
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere() 
			 && this.world.getEntitiesWithinAABB(EntityCustom.class, this.getEntityBoundingBox().grow(128d, 16d, 128d)).isEmpty();
		}

		@Override
		protected float getSoundPitch() {
			return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 2.4F;
		}

		@Nullable
		public EntityLivingBase getHealTarget() {
			return this.healTarget;
		}

		public void setHealTarget(@Nullable EntityLivingBase target) {
			if ((target == null) == (this.getHeldItemMainhand().getItem() == ItemIryoJutsu.block)) {
				this.resetActiveHand();
				this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 0);
			}
			this.healTarget = target;
		}

		public boolean canHealEntity(EntityLivingBase entity) {
			return this.healableEntities.contains(entity) || this.isOnSameTeam(entity) || entity instanceof EntityVillager;
		}

		public void addHealableEntity(EntityLivingBase entity) {
			this.healableEntities.add(entity);
		}

		public void healTargetEntity() {
			if (this.healTarget != null && this.getHeldItemMainhand().getItem() == ItemIryoJutsu.block) {
				if (this.getDistance(this.healTarget) <= 0.8d * (this.width + this.healTarget.width)) {
					if (!this.isHandActive()) {
						this.setActiveHand(EnumHand.MAIN_HAND);
					}
				} else if (this.isHandActive()) {
					this.resetActiveHand();
				}
			}
		}

		//@Override
		//protected void onDeathUpdate() {
		//	super.onDeathUpdate();
		//	this.deathTime = 0;
		//	this.setHealth(10f);
		//}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source != DamageSource.OUT_OF_WORLD && amount >= this.getHealth()) {
				amount = this.getHealth() - 1f;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			boolean flag = super.attackEntityAsMob(entityIn);
			if (flag) {
				ProcedureUtils.pushEntity(this, entityIn, 10d, 1.5f);
			}
			return flag;
		}

		@Override
		public void onLivingUpdate() {
			super.onLivingUpdate();
			if (this.ticksExisted % 20 == 0) {
				if (this.getHealth() < this.getMaxHealth()) {
					this.heal(10f);
				}
				this.addPotionEffect(new PotionEffect(PotionChakraEnhancedStrength.potion, 21, 10, false, false));
			}
		}

		@Override
		protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		}

		@Override
		protected boolean canDropLoot() {
			return false;
		}

		public class AIHeal extends EntityAIBase {
			private final EntityCustom entity;
			private final double speed;
			private final int chance;
			private EntityLivingBase target;
		    private int delayCounter;
		    private Vec3d lastVec;

			public AIHeal(EntityCustom healer, double speedIn) {
				this(healer, speedIn, 40);
			}

			public AIHeal(EntityCustom healer, double speedIn, int chanceIn) {
				this.entity = healer;
				this.speed = speedIn;
				this.chance = chanceIn;
				this.setMutexBits(3);
			}

			@Override
			public boolean shouldExecute() {
				Predicate canHeal = Predicates.and(EntitySelectors.CAN_AI_TARGET, new Predicate<EntityLivingBase>() {
					@Override
					public boolean apply(@Nullable EntityLivingBase p_apply_1_) {
						return p_apply_1_ != null && p_apply_1_.getHealth() < p_apply_1_.getMaxHealth() * 0.4f
						 && AIHeal.this.entity.canHealEntity(p_apply_1_)
						 && AIHeal.this.inRange(p_apply_1_);
					}
				});
				EntityLivingBase livingentity = this.entity.getHealTarget();
				if (livingentity == null && this.entity.hasHome() && this.entity.rand.nextInt(this.chance) == 0) {
					double d = (double)this.entity.getMaximumHomeDistance();
					AxisAlignedBB bb = new AxisAlignedBB(this.entity.getHomePosition()).grow(d, 8d, d);
					livingentity = (EntityLivingBase)ProcedureUtils.findNearestEntityWithinAABB(this.entity.world,
					 EntityLivingBase.class, bb, this.entity, canHeal);
				}
				if (livingentity != null && livingentity.isEntityAlive() && canHeal.apply(livingentity)) {
					this.target = livingentity;
				}
				return this.target != null;
			}

			private boolean inRange(Entity entityIn) {
				return this.entity.getDistance(entityIn) <= ProcedureUtils.getFollowRange(this.entity);
			}

			@Override
			public boolean shouldContinueExecuting() {
				return this.target.isEntityAlive() && this.inRange(this.target)
				 && this.target.getHealth() < this.target.getMaxHealth() * 0.9f;
			}

			@Override
			public void startExecuting() {
				this.entity.setHealTarget(this.target);
				this.entity.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
			}

			@Override
			public void resetTask() {
				this.target = null;
				this.entity.setHealTarget(null);
				this.entity.getNavigator().clearPath();
			}

			@Override
			public void updateTask() {
				Vec3d vec = this.target.getPositionEyes(1f).subtract(0d, 0.3d, 0d);
		        this.entity.getLookHelper().setLookPosition(vec.x, vec.y, vec.z, 30.0F, 30.0F);
		        double d0 = this.entity.getDistanceSq(this.target);
		        --this.delayCounter;
		        if (this.delayCounter <= 0 && (this.lastVec == null
		         || this.target.getDistanceSq(this.lastVec.x, this.lastVec.y, this.lastVec.z) >= 1.0D
		         || this.entity.getRNG().nextFloat() < 0.05F)) {
		        	this.lastVec = this.target.getPositionVector();
		            this.delayCounter = 4 + this.entity.getRNG().nextInt(7);
		            double d = d0 < 9.0D ? this.speed * 0.4d : this.speed;
		            if (!this.entity.getNavigator().tryMoveToEntityLiving(this.target, d)) {
		                this.delayCounter += 15;
		            }
		        }
	        	this.entity.healTargetEntity();
			}
		}

		public static class LivingHook {
			@SubscribeEvent
			public void onLivingDrop(LivingDropsEvent event) {
				if (event.getEntity() instanceof EntityCustom) {
					event.setCanceled(true);
				}
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new EntityNinjaMob.RenderBase<EntityCustom>(renderManager, new ModelBiped64slim()) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/sakura_slim.png");
					@Override
					protected ResourceLocation getEntityTexture(EntityCustom entity) {
						return this.texture;
					}
					@Override
					protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
						float f = 0.0625f * 14;
						GlStateManager.scale(f, f, f);
					}
				};
			});
		}

		// Made with Blockbench 3.7.4
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelBiped64slim extends ModelBiped {
			public ModelBiped64slim() {
				this.textureWidth = 64;
				this.textureHeight = 64;
				this.leftArmPose = ModelBiped.ArmPose.EMPTY;
				this.rightArmPose = ModelBiped.ArmPose.EMPTY;
				this.bipedHead = new ModelRenderer(this);
				this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				this.bipedHeadwear = new ModelRenderer(this);
				this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));
				this.bipedBody = new ModelRenderer(this);
				this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
				this.bipedRightArm = new ModelRenderer(this);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
				this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
				this.bipedLeftArm = new ModelRenderer(this);
				this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
				this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
				this.bipedRightLeg = new ModelRenderer(this);
				this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
				this.bipedLeftLeg = new ModelRenderer(this);
				this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
			}
		}
	}
}
