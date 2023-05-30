
package net.narutomod.entity;

import com.google.common.collect.Maps;
import net.minecraft.entity.*;
import net.minecraft.village.Village;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
//import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.MerchantRecipe;

import net.minecraft.inventory.EntityEquipmentSlot;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.*;
import net.narutomod.block.BlockExplosiveTag;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTenten extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 170;
	public static final int ENTITYID_RANGED = 171;

	public EntityTenten(ElementsNarutomodMod instance) {
		super(instance, 440);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "tenten"), ENTITYID).name("tenten").tracker(64, 3, true).egg(-1, -13210).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		EntityRegistry.addSpawn(EntityCustom.class, 5, 1, 1, EnumCreatureType.AMBIENT,
			Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.FOREST_HILLS,
			Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.PLAINS, Biomes.ROOFED_FOREST, Biomes.TAIGA,
			Biomes.TAIGA_HILLS, Biomes.REDWOOD_TAIGA, Biomes.REDWOOD_TAIGA_HILLS, Biomes.MUTATED_PLAINS,
			Biomes.MUTATED_FOREST, Biomes.MUTATED_BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST_HILLS, Biomes.MUTATED_JUNGLE,
			Biomes.MUTATED_JUNGLE_EDGE, Biomes.MUTATED_ROOFED_FOREST, Biomes.MUTATED_TAIGA, Biomes.MUTATED_REDWOOD_TAIGA,
			Biomes.MUTATED_REDWOOD_TAIGA_HILLS);
	}

	public static class EntityCustom extends EntityNinjaMerchant.Base implements IRangedAttackMob {
		//private final ItemStack kunai = new ItemStack(ItemKunai.block);

		public EntityCustom(World world) {
			super(world, 60);
			this.setSize(0.525f, 1.75f);
			Arrays.fill(this.inventoryHandsDropChances, 0.0F);
		}

		@Override
		public Map<EntityNinjaMerchant.TradeLevel, MerchantRecipeList> getTrades() {
			Map<EntityNinjaMerchant.TradeLevel, MerchantRecipeList> trades = Maps.newHashMap();

			MerchantRecipeList commonTrades = new MerchantRecipeList();
			commonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, new ItemStack(ItemShuriken.block, 24), 0, 1));
			commonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, new ItemStack(ItemKunai.block, 3), 0, 1));
			commonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, new ItemStack(BlockExplosiveTag.block, 3), 0, 1));

			MerchantRecipeList uncommonTrades = new MerchantRecipeList();
			uncommonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, new ItemStack(ItemKunaiExplosive.block, 2), 0, 1));
			uncommonTrades.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 15), ItemStack.EMPTY, new ItemStack(ItemChokuto.block, 1), 0, 1));

			trades.put(EntityNinjaMerchant.TradeLevel.COMMON, commonTrades);
			trades.put(EntityNinjaMerchant.TradeLevel.UNCOMMON, uncommonTrades);
			return trades;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3D);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemToInventory(new ItemStack(ItemKunai.block), 0);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return EntityNinjaMob.TeamKonoha.contains(entityIn.getClass());
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere() 
			 && this.world.getEntitiesWithinAABB(EntityCustom.class, this.getEntityBoundingBox().grow(128d, 16d, 128d)).isEmpty();
		}
		
		@Override
		protected float getSoundPitch() {
			return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 3.0F;
		}
		
		@Override
		public void setAttackTarget(@Nullable EntityLivingBase entityIn) {
			super.setAttackTarget(entityIn);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, entityIn == null ? ItemStack.EMPTY : new ItemStack(ItemKunai.block, 1));
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedTactical(this, 1.0D, 15, 15f) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.getDistance(EntityCustom.this.getAttackTarget()) >= 4d;
				}
			});
			this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntitySkeleton.class, false, false));
			this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityZombie.class, false, false));
		}

		//@Override
		//protected void updateAITasks() {
		//	if (this.getHeldItemMainhand().isEmpty() != (this.getAttackTarget() == null)) {
		//		this.swapWithInventory(EntityEquipmentSlot.MAINHAND);
		//	}
		//	super.updateAITasks();
		//}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			this.swingArm(EnumHand.MAIN_HAND);
			if (!this.world.isRemote) {
				ItemShuriken.EntityArrowCustom entityarrow = new ItemShuriken.EntityArrowCustom(this.world, this);
				double d0 = target.posX - entityarrow.posX;
				double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - entityarrow.posY;
				double d2 = target.posZ - entityarrow.posZ;
				double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
				entityarrow.shoot(d0, d1 + d3 * 0.2D, d2, 1.6f, 0f);
				entityarrow.setSilent(true);
				entityarrow.setIsCritical(false);
				entityarrow.setDamage(5);
				entityarrow.setKnockbackStrength(1);
				this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1, 1f / (this.rand.nextFloat() * 0.5f + 1f) + 0.5f);
				entityarrow.pickupStatus = net.minecraft.entity.projectile.EntityArrow.PickupStatus.DISALLOWED;
				this.world.spawnEntity(entityarrow);
			}
		}

		@Override
		protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
			this.entityDropItem(new ItemStack(ItemKunai.block, 1 + this.rand.nextInt(6)), 0f);
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
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/tenten.png");
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelBiped64slim());
				//this.addLayer(new LayerHeldItem(this));
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
				this.bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -3.3F, 8, 8, 8, 1.0F, false));
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
