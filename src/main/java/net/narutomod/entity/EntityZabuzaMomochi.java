
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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.BlockLiquid;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemZabuzaSword;
import net.narutomod.item.ItemSuiton;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityZabuzaMomochi extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 154;
	public static final int ENTITYID_RANGED = 155;

	public EntityZabuzaMomochi(ElementsNarutomodMod instance) {
		super(instance, 411);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "zabuza_momochi"), ENTITYID).name("zabuza_momochi")
				.tracker(64, 3, true).egg(-6710887, -16764058).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		int i = MathHelper.clamp(ModConfig.SPAWN_WEIGHT_ZABUZA, 0, 20);
		if (i > 0) {
			EntityRegistry.addSpawn(EntityCustom.class, i, 1, 1, EnumCreatureType.MONSTER,
					Biomes.PLAINS, Biomes.EXTREME_HILLS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMPLAND, Biomes.RIVER,
					Biomes.JUNGLE, Biomes.SAVANNA, Biomes.ICE_MOUNTAINS, Biomes.ICE_PLAINS, Biomes.BEACH, Biomes.COLD_BEACH,
					Biomes.MUTATED_PLAINS, Biomes.MUTATED_EXTREME_HILLS, Biomes.MUTATED_FOREST, Biomes.MUTATED_TAIGA,
					Biomes.MUTATED_SWAMPLAND, Biomes.MUTATED_JUNGLE, Biomes.MUTATED_SAVANNA, Biomes.MUTATED_ICE_FLATS);
		}
		//DungeonHooks.addDungeonMob(new ResourceLocation("narutomod:zabuza_momochi"), 50);
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IRangedAttackMob, IMob {
		private final int MIST_CD = 1200;
		private final int WATERPRISON_CD = 400;
		private final int WATERDRAGON_CD = 300;
		private final int WATERCLONE_CD = 100;
		private final int BLOCKING_CD = 20;
		private final double MIST_CHAKRA = 100d;
		private final double WATERPRISON_CHAKRA = 200d;
		private final double WATERDRAGON_CHAKRA = 200d;
		private final double WATERCLONE_CHAKRA = 500d;
		private int mistLastUsed = -MIST_CD;
		private int prisonLastUsed = -WATERPRISON_CD + 40;
		private int cloneLastUsed = -WATERCLONE_CD + 40;
		private int lastBlockTime;
		private EntityCustom original;
		private int clones;
		private EntityHaku.EntityCustom haku;
		private List<EntityLivingBase> avoidEntitiesList = Lists.newArrayList();
		private final EntityAINearestAttackableTarget aiTargetPlayer;
		private final EntityAIHurtByTarget aiTargetHurt = new EntityAIHurtByTarget(this, true);
		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.setSize(0.6f, 1.9375f);
			java.util.Arrays.fill(this.inventoryHandsDropChances, 0.0F);
			this.aiTargetPlayer = new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelector);
			this.setAttackTargetsTasks();
		}

		public EntityCustom(EntityCustom cloneFrom) {
			this(cloneFrom.world);
			this.original = cloneFrom;
			++this.original.clones;
			this.copyLocationAndAnglesFrom(cloneFrom);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1D);
			this.setHealth(10f);
			this.targetTasks.addTask(3, new EntityNinjaMob.AIDefendEntity(this, this.original));
			this.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(this)), null);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			livingdata = super.onInitialSpawn(difficulty, livingdata);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemZabuzaSword.block));
			this.spawnHaku();
			return livingdata;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityLivingBase.class, new Predicate<Entity>() {
				public boolean apply(@Nullable Entity p_apply_1_) {
					return p_apply_1_ != null && EntityCustom.this.avoidEntitiesList.contains(p_apply_1_);
				}
			}, 10f, 1.25d, 1.5d));
			this.tasks.addTask(2, new EntityNinjaMob.AIAttackRangedJutsu(this, WATERDRAGON_CD, 12.0F));
			this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 1.0f));
			this.tasks.addTask(3, new EntityNinjaMob.AIAttackMelee(this, 1.5d, true));
			this.tasks.addTask(4, new EntityAIWatchClosest2(this, EntityPlayer.class, 15.0F, 1.0F));
			this.tasks.addTask(5, new EntityAIWander(this, 0.5));
			this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityMob.class, 8.0F));
		}

		public void removeTargetsTasks() {
			this.targetTasks.removeTask(this.aiTargetHurt);
			this.targetTasks.removeTask(this.aiTargetPlayer);
			this.setAttackTarget(null);
		}

		public void setAttackTargetsTasks() {
			this.targetTasks.addTask(1, this.aiTargetHurt);
			this.targetTasks.addTask(2, this.aiTargetPlayer);
		}

		@Override
		protected double meleeReach() {
			return 4.8d;
		}

		@Override
		protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
			if (!this.isClone()) {
				this.entityDropItem(this.getHeldItemMainhand(), 0f);
				this.entityDropItem(this.getItemFromInventory(0), 0f);
			}
		}

		public boolean isClone() {
			return this.original != null;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.isAIDisabled() && !source.isUnblockable()) {
				if (this.getHeldItemMainhand().isEmpty() && source.getTrueSource() instanceof EntityLivingBase) {
					this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 0);
				}
				if (!this.getHeldItemMainhand().isEmpty() && !this.isActiveItemStackBlocking()) {
					this.setActiveHand(EnumHand.MAIN_HAND);
					this.activeItemStackUseCount = this.getActiveItemStack().getMaxItemUseDuration() - 5;
					this.lastBlockTime = this.ticksExisted;
				}
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();

			EntityLivingBase target = this.getAttackTarget();
			if (target != null && target.isEntityAlive()) {
				double distanceToTarget = this.getDistance(target);
				if (!this.isClone() && this.ticksExisted > this.mistLastUsed + MIST_CD && this.consumeChakra(MIST_CHAKRA)) {
					new ItemSuiton.EntityMist.Jutsu().createJutsu(this.getHeldItemOffhand(), this, 1f);
					this.mistLastUsed = this.ticksExisted;
				}
				if (this.isClone() && !EntityWaterPrison.isEntityTrapped(target) && distanceToTarget >= 2d
						&& this.ticksExisted > this.prisonLastUsed + WATERPRISON_CD && this.getChakra() >= WATERPRISON_CHAKRA) {
					this.getLookHelper().setLookPositionWithEntity(target, 90f, 30f);
					if (new EntityWaterPrison.EC.Jutsu().createJutsu(this, target, 300) != null) {
						this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 0);
						this.consumeChakra(WATERPRISON_CHAKRA);
						this.prisonLastUsed = this.ticksExisted;
					}
				}
				if (!this.isClone() && this.clones < 1
						&& this.ticksExisted > this.cloneLastUsed + WATERCLONE_CD && this.consumeChakra(WATERCLONE_CHAKRA)) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kagebunshin")), 1f, 1f);
					this.world.spawnEntity(new EntityCustom(this));
					this.cloneLastUsed = this.ticksExisted;
				}
			}
			if (!this.isClone()) {
				float hp = this.getHealth();
				float maxhp = this.getMaxHealth();
				if (hp <= maxhp * 0.4f && this.getRevengeTarget() != null && !this.avoidEntitiesList.contains(this.getRevengeTarget())) {
					this.removeTargetsTasks();
					this.avoidEntitiesList.add(this.getRevengeTarget());
					this.callHelp();
			 	} else if (hp > maxhp * 0.6f && !this.avoidEntitiesList.isEmpty()) {
					this.avoidEntitiesList.clear();
					this.setAttackTargetsTasks();
				}
			}
		}

		private void callHelp() {
			for (Class<? extends EntityNinjaMob.Base> oclass : EntityNinjaMob.TeamZabuza) {
				for (EntityNinjaMob.Base ninja : this.world.getEntitiesWithinAABB(oclass, this.getEntityBoundingBox().grow(64.0D, 8.0D, 64.0D))) {
					if (ninja != this && (ninja.getAttackTarget() == null || !this.avoidEntitiesList.contains(ninja.getAttackTarget()))) {
						EntityLivingBase target = this.avoidEntitiesList.get(this.rand.nextInt(this.avoidEntitiesList.size()));
						for (int i = 0; i < 5 && !target.isEntityAlive(); i++, target = this.avoidEntitiesList.get(this.rand.nextInt(this.avoidEntitiesList.size())));
						if (target.isEntityAlive() && !ninja.isOnSameTeam(target)) {
							ninja.setAttackTarget(target);
						}
					}
				}
			}
		}

		@Override
		protected void onDeathUpdate() {
			if (!this.world.isRemote && this.isClone()) {
				this.playSound(net.minecraft.init.SoundEvents.ENTITY_GENERIC_SPLASH, 1f, 1f);
				new net.narutomod.event.EventSetBlocks(this.world,
				 com.google.common.collect.ImmutableMap.of(new BlockPos(this).up(),
				 Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(1))), 0, 10, false, false);
				this.setDead();
			} else {
				super.onDeathUpdate();
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.isClone()) {
				--this.original.clones;
			}
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			if (!this.world.isRemote && this.getChakra() >= WATERDRAGON_CHAKRA) {
				new EntityWaterDragon.EC.Jutsu().createJutsu(this, 1f);
				this.consumeChakra(WATERDRAGON_CHAKRA);
				this.standStillFor(60);
			}
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
		public boolean isNonBoss() {
			return this.isClone();
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
			super.onUpdate();
			if (this.isActiveItemStackBlocking() && this.ticksExisted > this.lastBlockTime + this.BLOCKING_CD) {
				this.resetActiveHand();
			}
			if (!this.isClone()) {
				this.trackAttackedPlayers();
				this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
				if (!this.world.isRemote && this.ticksExisted > 20 && this.haku == null) {
					this.spawnHaku();
				}
			} else if (!this.world.isRemote) {
				this.setNoAI(EntityWaterPrison.isEntityTrapping(this));
				if (!this.original.isEntityAlive() || (this.original.getAttackTarget() == null && this.original.avoidEntitiesList.isEmpty())) {
					this.setHealth(0f);
				}
			}
		}

		private void spawnHaku() {
			if (!this.isClone()) {
				this.haku = (EntityHaku.EntityCustom)this.world
						.findNearestEntityWithinAABB(EntityHaku.EntityCustom.class, this.getEntityBoundingBox().grow(128d, 32d, 128d), this);
				if (this.haku == null) {
					this.haku = new EntityHaku.EntityCustom(this.world);
					this.haku.setLeader(this);
					this.haku.setPosition(this.posX + (this.rand.nextBoolean()?3d:-3d), this.posY, this.posZ + (this.rand.nextBoolean()?3d:-3d));
					this.haku.onInitialSpawn(this.world.getDifficultyForLocation(this.getPosition()), null);
					this.world.spawnEntity(this.haku);
				} else {
					this.haku.setLeader(this);
				}
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/zabuzamomochi.png");

			public RenderCustom(RenderManager renderManager) {
				super(renderManager, new ModelBiped64());
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				GlStateManager.scale(0.9375F, 0.96875F, 0.9375F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelBiped64 extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer hair;
			public ModelBiped64() {
				super();
				this.bipedHead = new ModelRenderer(this);
				this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, -0.5F, 0.0F);
				bipedHead.addChild(hair);
				ModelRenderer bone1 = new ModelRenderer(this);
				bone1.setRotationPoint(-2.0F, -5.0F, 0.0F);
				hair.addChild(bone1);
				setRotationAngle(bone1, 0.0F, 0.0F, -0.5236F);
				bone1.cubeList.add(new ModelBox(bone1, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
				ModelRenderer bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-2.0F, -4.5F, -2.0F);
				hair.addChild(bone2);
				setRotationAngle(bone2, 0.3491F, 0.0F, -0.3491F);
				bone2.cubeList.add(new ModelBox(bone2, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
				ModelRenderer bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-2.0F, -4.5F, 2.0F);
				hair.addChild(bone3);
				setRotationAngle(bone3, -0.3491F, 0.0F, -0.3491F);
				bone3.cubeList.add(new ModelBox(bone3, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
				ModelRenderer bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -5.0F, -2.0F);
				hair.addChild(bone4);
				setRotationAngle(bone4, 0.5236F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
				ModelRenderer bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, -5.0F, 2.0F);
				hair.addChild(bone5);
				setRotationAngle(bone5, -0.5236F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
				ModelRenderer bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(2.0F, -5.0F, 0.0F);
				hair.addChild(bone6);
				setRotationAngle(bone6, 0.0F, 0.0F, 0.5236F);
				bone6.cubeList.add(new ModelBox(bone6, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, true));
				ModelRenderer bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(2.0F, -4.5F, 2.0F);
				hair.addChild(bone7);
				setRotationAngle(bone7, -0.3491F, 0.0F, 0.3491F);
				bone7.cubeList.add(new ModelBox(bone7, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, true));
				ModelRenderer bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(2.0F, -4.5F, -2.0F);
				hair.addChild(bone8);
				setRotationAngle(bone8, 0.3491F, 0.0F, 0.3491F);
				bone8.cubeList.add(new ModelBox(bone8, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, true));
				this.bipedHeadwear = new ModelRenderer(this);
				this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));
				this.bipedBody = new ModelRenderer(this);
				this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F, false));
				this.bipedRightArm = new ModelRenderer(this);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
				this.bipedLeftArm = new ModelRenderer(this);
				this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
				this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, true));
				this.bipedRightLeg = new ModelRenderer(this);
				this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
				this.bipedLeftLeg = new ModelRenderer(this);
				this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, true));
			}
		}
	}
}
