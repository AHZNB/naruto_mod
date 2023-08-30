
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
//import net.minecraftforge.common.DungeonHooks;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumHand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.block.BlockLiquid;

import net.narutomod.block.BlockWaterStill;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemSamehada;
import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.ImmutableMap;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityKisameHoshigaki extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 195;
	public static final int ENTITYID_RANGED = 196;

	public EntityKisameHoshigaki(ElementsNarutomodMod instance) {
		super(instance, 454);
	}

	@Override
	public void initElements() {
		elements.entities
				.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
						.id(new ResourceLocation("narutomod", "kisame_hoshigaki"), ENTITYID)
						.name("kisame_hoshigaki").tracker(64, 3, true).egg(-13421773, -3355444).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		int i = MathHelper.clamp(ModConfig.SPAWN_WEIGHT_KISAME, 0, 20);
		if (i > 0) {
			EntityRegistry.addSpawn(EntityCustom.class, i, 1, 1, EnumCreatureType.MONSTER,
					Biomes.SWAMPLAND, Biomes.FOREST, Biomes.TAIGA, Biomes.RIVER, Biomes.BEACH,
					Biomes.FOREST_HILLS, Biomes.TAIGA_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE,
					Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.SAVANNA, Biomes.MUTATED_SWAMPLAND,
					Biomes.MUTATED_FOREST, Biomes.MUTATED_TAIGA, Biomes.MUTATED_JUNGLE, Biomes.MUTATED_JUNGLE_EDGE,
					Biomes.MUTATED_BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST_HILLS, Biomes.MUTATED_SAVANNA);
		}
		//DungeonHooks.addDungeonMob(new ResourceLocation("narutomod:kisame_hoshigaki"), 50);
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IRangedAttackMob, IMob {
		private static final DataParameter<Boolean> ALT_MODEL = EntityDataManager.createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
		private final int WATERSHARK_CD = 80;
		private final int BLOCKING_CD = 30;
		private final int WATERPRISON_CD = 600;
		private final int WATERCLONE_CD = 100;
		private final double WATERSHARK_CHAKRA = 75d;
		private final double WATERPRISON_CHAKRA = 200d;
		private final double WATERCLONE_CHAKRA = 500d;
		private final double WATERSHOCK_CHAKRA = 600d;
		private int prisonLastUsed = -WATERPRISON_CD;
		private int cloneLastUsed = -WATERCLONE_CD;
		private int lastBlockTime;
		private EntityCustom original;
		private EntityWaterShockwave.EC waterDome;
		private int clones;
		private final PathNavigate altNavigator;
		private final PathNavigate mainNavigator;
		private final EntityMoveHelper altMoveHelper;
		private final EntityMoveHelper mainMoveHelper;
		private static final AttributeModifier FUSED_STRENGTH = new AttributeModifier("kisame.fused.damage", 15f, 0);
		private static final AttributeModifier FUSED_HEALTH = new AttributeModifier("kisame.fused.health", 100f, 0);
		private final ItemStack hatStack = new ItemStack(ItemAkatsukiRobe.helmet);

		public EntityCustom(World worldIn) {
			super(worldIn, 140, 10000d);
			this.setSize(0.6f, 2.1f);
			this.mainNavigator = this.navigator;
			this.altNavigator = new PathNavigateSwimmer(this, worldIn);
			this.mainMoveHelper = this.moveHelper;
			this.altMoveHelper = new EntityNinjaMob.SwimHelper(this);
			//this.setItemToInventory(swordStack);
			java.util.Arrays.fill(this.inventoryHandsDropChances, 0.0F);
		}

		public EntityCustom(EntityCustom cloneFrom) {
			this(cloneFrom.world);
			this.original = cloneFrom;
			++this.original.clones;
			this.copyLocationAndAnglesFrom(cloneFrom);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1D);
			this.experienceValue = 0;
			this.targetTasks.addTask(3, new EntityNinjaMob.AIDefendEntity(this, this.original));
			this.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(this)), null);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemToInventory(new ItemStack(ItemSamehada.block), 0);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(ALT_MODEL, Boolean.valueOf(false));
		}

		public boolean useAltModel() {
			return ((Boolean)this.getDataManager().get(ALT_MODEL)).booleanValue();
		}

		protected void setUseAltModel(boolean flag) {
			this.getDataManager().set(ALT_MODEL, Boolean.valueOf(flag));
			if (flag) {
				this.navigator = this.altNavigator;
				this.moveHelper = this.altMoveHelper;
				//this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(FUSED_STRENGTH);
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(FUSED_HEALTH);
			} else {
				this.navigator = this.mainNavigator;
				this.moveHelper = this.mainMoveHelper;
				//this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(FUSED_STRENGTH);
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(FUSED_HEALTH);
			}
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			//this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true, false));
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false,
					new Predicate<EntityPlayer>() {
						public boolean apply(@Nullable EntityPlayer p_apply_1_) {
							return p_apply_1_ != null && EntityBijuManager.isJinchuriki(p_apply_1_);
						}
					}));
			this.tasks.addTask(0, new EntityAISwimming(this) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !EntityCustom.this.useAltModel();
				}
			});
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedJutsu(this, WATERSHARK_CD, 15.0F));
			this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 1.0f));
			this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.2d, true) {
				@Override
				protected double getAttackReachSqr(EntityLivingBase attackTarget) {
					return 5.3d + attackTarget.width;
				}
			});
			this.tasks.addTask(4, new EntityAIWatchClosest2(this, EntityPlayer.class, 15.0F, 1.0F));
			this.tasks.addTask(5, new EntityAIWander(this, 0.5));
			this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityMob.class, 8.0F));
		}

		public boolean isClone() {
			return this.original != null;
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || EntityNinjaMob.TeamItachi.contains(entityIn.getClass());
		}

		@Override
		public boolean canAttackClass(Class <? extends EntityLivingBase > cls) {
			return cls != EntityCustom.class;
		}

		@Override
		protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
			if (!this.isClone()) {
				this.entityDropItem(this.getHeldItemMainhand(), 0f);
				this.entityDropItem(this.getItemFromInventory(0), 0f);
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
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.isAIDisabled() && source instanceof EntityDamageSource
			 && !((EntityDamageSource)source).getIsThornsDamage() && !source.isUnblockable()) {
				if (this.getHeldItemMainhand().isEmpty()) {
					this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 0);
				}
				if (!this.getHeldItemMainhand().isEmpty() && !this.isActiveItemStackBlocking()
						&& this.ticksExisted > this.lastBlockTime + this.BLOCKING_CD) {
					this.setActiveHand(EnumHand.MAIN_HAND);
					this.activeItemStackUseCount = this.getActiveItemStack().getMaxItemUseDuration() - 5;
					this.lastBlockTime = this.ticksExisted;
				}
			}
			//if (this.useAltModel() && source.getTrueSource() instanceof EntityLivingBase) {
			//EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
			//ItemSamehada.applyEffects(attacker, this, 0.5f);
			//attacker.attackEntityFrom(DamageSource.causeThornsDamage(this), 8f + this.rand.nextFloat() * 2f);
			//if (!source.isUnblockable() && ProcedureUtils.isEntityInFOV(this, attacker)) {
			//	amount *= this.rand.nextFloat() * 0.2f;
			//	this.swingArm(EnumHand.OFF_HAND);
			//}
			//}
			return super.attackEntityFrom(source, amount);
		}

		/*@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			if (this.useAltModel() && entityIn instanceof EntityLivingBase) {
				ItemSamehada.applyEffects((EntityLivingBase)entityIn, this, 0.5f);
			}
			return super.attackEntityAsMob(entityIn);
		}*/

		@Override
		public void setDead() {
			super.setDead();
			if (this.isClone()) {
				--this.original.clones;
				this.playSound(SoundEvents.ENTITY_GENERIC_SPLASH, 1f, 1f);
				new net.narutomod.event.EventSetBlocks(this.world, ImmutableMap.of(new BlockPos(this).up(),
				 Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(1))), 0, 10, false, false);
				//this.world.setBlockState(new BlockPos(this).up(), Blocks.WATER.getDefaultState(), 3);
			}
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();

			EntityLivingBase target = this.getAttackTarget();
			//if (this.getHeldItemMainhand().isEmpty() != (target == null)) {
			//	this.swapWithInventory(EntityEquipmentSlot.MAINHAND);
			//}
			if (target != null && target.isEntityAlive()) {
				if (!this.useAltModel() && !this.isClone() && this.getHealth() < this.getMaxHealth() / 3
						&& this.consumeChakra(WATERSHOCK_CHAKRA)) {
					this.waterDome = new EntityWaterShockwave.EC.Jutsu().createJutsu(this, 20f);
					this.setUseAltModel(true);
					this.setHealth(this.getMaxHealth());
				}
				if (this.isClone() && !EntityWaterPrison.isEntityTrapped(target)
						&& this.ticksExisted > this.prisonLastUsed + WATERPRISON_CD && this.getChakra() >= WATERPRISON_CHAKRA) {
					this.getLookHelper().setLookPositionWithEntity(target, 90f, 30f);
					if (new EntityWaterPrison.EC.Jutsu().createJutsu(this, target, 300) != null) {
						this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 0);
						this.consumeChakra(WATERPRISON_CHAKRA);
						this.prisonLastUsed = this.ticksExisted;
					}
				}
				if (!this.useAltModel() && !this.isClone() && this.clones < 2
						&& this.ticksExisted > this.cloneLastUsed + WATERCLONE_CD && this.consumeChakra(WATERCLONE_CHAKRA)) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kagebunshin")), 1f, 1f);
					this.world.spawnEntity(new EntityCustom(this));
					this.cloneLastUsed = this.ticksExisted;
				}
			} else if (this.useAltModel() && this.peacefulTicks > 200) {
				this.setAttackTarget(target = null);
				if (this.waterDome != null) {
					this.waterDome.setShouldDie();
					this.waterDome = null;
				}
				this.setUseAltModel(false);
			}
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, target == null ? this.hatStack : ItemStack.EMPTY);
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			float power = this.useAltModel() ? 5f : (1f + this.rand.nextFloat());
			if (!this.world.isRemote && this.consumeChakra(WATERSHARK_CHAKRA * power)) {
				double d0 = target.posX - this.posX;
				double d1 = target.posY - (this.posY + this.height);
				double d2 = target.posZ - this.posZ;
				new EntitySuitonShark.EC.Jutsu().createJutsu(this, power);
				this.standStillFor(80);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.isActiveItemStackBlocking() && this.ticksExisted > this.lastBlockTime + 20) {
				this.resetActiveHand();
			}
			if (!this.isClone()) {
				this.trackAttackedPlayers();
				this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			} else if (!this.world.isRemote) {
				this.setNoAI(EntityWaterPrison.isEntityTrapping(this));
				if (!this.original.isEntityAlive() || this.original.getAttackTarget() == null || this.original.useAltModel()) {
					this.setDead();
				}
			}
		}

		@Override
		protected void blockUsingShield(EntityLivingBase attacker) {
			super.blockUsingShield(attacker);
			if (!this.isClone() && !(attacker instanceof EntityCustom)) {
				attacker.attackEntityFrom(DamageSource.causeThornsDamage(this), 2f + this.rand.nextFloat() * 8f);
			}
		}

		//@Override
		//public boolean canBreatheUnderwater() {
		//	return this.useAltModel();
		//}

		@Override
		public boolean isPushedByWater() {
			return !this.useAltModel();
		}

		@Override
		protected float getWaterSlowDown() {
			return this.useAltModel() ? 1.0f : 0.8f;
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere()
			 && this.world.getEntitiesWithinAABB(EntityCustom.class, this.getEntityBoundingBox().grow(128.0D)).isEmpty();
			 //&& this.rand.nextInt(5) == 0;
		}

		@Override
		public boolean isNonBoss() {
			return this.isClone();
		}

		@Override
		public void addTrackingPlayer(EntityPlayerMP player) {
			super.addTrackingPlayer(player);

			if (ModConfig.AGGRESSIVE_BOSSES) {
				this.setAttackTarget(player);
			}
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

			if (entity instanceof EntityPlayerMP || (entity = (ModConfig.AGGRESSIVE_BOSSES ? this.getLastAttackedEntity() : this.getAttackTarget())) instanceof EntityPlayerMP) {
				this.bossInfo.addPlayer((EntityPlayerMP) entity);
			}
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			if (this.isClone()) {
				compound.setUniqueId("originalUUID", this.original.getUniqueID());
			}
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			if (this.world instanceof WorldServer && compound.hasUniqueId("originalUUID")) {
				Entity entity = ((WorldServer)this.world).getEntityFromUuid(compound.getUniqueId("originalUUID"));
				if (entity instanceof EntityCustom) {
					this.original = (EntityCustom)entity;
				} else {
					this.setDead();
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation MAIN_TEXTURE = new ResourceLocation("narutomod:textures/kisame.png");
			private final ResourceLocation ALT_TEXTURE = new ResourceLocation("narutomod:textures/kisamefinal.png");
			private final ModelKisameFused altModel = new ModelKisameFused();
			private final ModelKisame model;

			public RenderCustom(RenderManager renderManager) {
				super(renderManager, new ModelKisame());
				this.model = (ModelKisame)this.mainModel;
			}

			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				this.mainModel = entity.useAltModel() ? this.altModel : this.model;
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			@Override
			protected void renderLayers(EntityCustom entityIn, float f0, float f1, float f2, float f3, float f4, float f5, float f6) {
				if (!entityIn.useAltModel()) {
					super.renderLayers(entityIn, f0, f1, f2, f3, f4, f5, f6);
				}
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				GlStateManager.scale(1.0625F, 1.0625F, 1.0625F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return entity.useAltModel() ? ALT_TEXTURE : MAIN_TEXTURE;
			}
		}

		// Made with Blockbench 3.7.5
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelKisame extends ModelBiped {
			public ModelKisame() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 40, 8, -3.0F, -9.25F, -4.75F, 6, 1, 1, -0.2F, false));
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 40, 8, -3.0F, -10.0F, -5.0F, 6, 2, 1, -0.4F, false));
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 40, 8, -3.0F, -10.5F, -5.25F, 6, 2, 1, -0.6F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelKisameFused extends ModelBiped {
			private final ModelRenderer RightFin;
			private final ModelRenderer LeftFin;

			public ModelKisameFused() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
				RightFin = new ModelRenderer(this);
				RightFin.setRotationPoint(-3.0F, 5.0F, 2.0F);
				bipedRightArm.addChild(RightFin);
				setRotationAngle(RightFin, 0.0F, -0.7854F, 0.0F);
				RightFin.cubeList.add(new ModelBox(RightFin, 32, 52, 0.0F, -3.0F, 0.0F, 0, 6, 6, 0.0F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, true));
				LeftFin = new ModelRenderer(this);
				LeftFin.setRotationPoint(3.0F, 5.0F, 2.0F);
				bipedLeftArm.addChild(LeftFin);
				setRotationAngle(LeftFin, 0.0F, 0.7854F, 0.0F);
				LeftFin.cubeList.add(new ModelBox(LeftFin, 32, 52, 0.0F, -3.0F, 0.0F, 0, 6, 6, 0.0F, true));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
			}

			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
