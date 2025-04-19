
package net.narutomod.entity;

import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemNinjaArmorFishnets;
import net.narutomod.item.ItemBakuton;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Biomes;
import net.minecraft.init.SoundEvents;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

@ElementsNarutomodMod.ModElement.Tag
public class EntityDeidara extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 449;
	public static final int ENTITYID_RANGED = 450;

	public EntityDeidara(ElementsNarutomodMod instance) {
		super(instance, 882);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "deidara"), ENTITYID)
				.name("deidara").tracker(64, 3, true).egg(-16777216, -6750157).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		int i = MathHelper.clamp(ModConfig.SPAWN_WEIGHT_DEIDARA, 0, 20);
		if (i > 0) {
			EntityRegistry.addSpawn(EntityCustom.class, i, 1, 1, EnumCreatureType.MONSTER,
					Biomes.BEACH, Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.MESA, Biomes.MESA_ROCK,
					Biomes.ICE_PLAINS, Biomes.PLAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.STONE_BEACH,
					Biomes.MUTATED_DESERT, Biomes.MUTATED_MESA, Biomes.MUTATED_MESA_ROCK, Biomes.MUTATED_PLAINS,
					Biomes.MUTATED_SAVANNA, Biomes.MUTATED_SAVANNA_ROCK, Biomes.COLD_BEACH);
		}
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private final int explosiveCloneCD = 200;
		private int explosiveCloneLastUsed = -200;
		private EntityC2.EC c2Entity;
		private EntityC4.EC c4Entity;
		private final float flyHeightOffGround = 16.0f;
		private EntityGiantBird.AIHoverOverTarget aiHover;
		
		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelectorAkatsuki));
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemToInventory(new ItemStack(ItemAkatsukiRobe.helmet), 1);
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobe.body));
			this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemNinjaArmorFishnets.legs));
			return super.onInitialSpawn(difficulty, livingdata);
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
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedTactical(this, 1.0D, 30, 50, 40.0F));
			this.tasks.addTask(2, new EntityClone.AIFollowSummoner(this, 0.6d, 4f) {
				@Override @Nullable
				protected EntityLivingBase getFollowEntity() {
					Entity sasori = EntityCustom.this.world.findNearestEntityWithinAABB(EntitySasori.EntityCustom.class, EntityCustom.this.getEntityBoundingBox().grow(256d, 16d, 256d), EntityCustom.this);
					return sasori instanceof EntityLivingBase ? (EntityLivingBase)sasori : null;
				}
			});
			this.tasks.addTask(3, new EntityAIWatchClosest2(this, EntityPlayer.class, 32.0F, 1.0F));
			this.tasks.addTask(4, new EntityAIWander(this, 0.5d));
			this.tasks.addTask(5, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			EntityLivingBase target = this.getAttackTarget();
			if (target != null) {
				if (this.c4Entity == null && (this.getHealth() < this.getMaxHealth() * 0.25f || this.remainingChakra() < 0.25f)
				 && this.consumeChakra(ItemBakuton.CLAY.chakraUsage * 4d)) {
					Vec3d vec = this.getPositionEyes(1f).add(this.getLookVec());
					this.c4Entity = new EntityC4.EC(this);
					this.c4Entity.setLocationAndAngles(vec.x, vec.y, vec.z, this.rotationYaw, 0f);
					this.c4Entity.setRotationYawHead(this.rotationYaw);
					this.c4Entity.setExplosionDamage(100, 2);
					this.world.spawnEntity(this.c4Entity);
				}
				if (!this.isRiding() && this.ticksExisted - this.explosiveCloneLastUsed == 40 && this.getHealth() < this.getMaxHealth()
				 && this.consumeChakra(ItemBakuton.CLAY.chakraUsage * 2d)) {
					this.c2Entity = new EntityC2.EC(this);
					this.c2Entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0f);
					this.moveC2ToOpenSpace();
					ProcedureUtils.poofWithSmoke(this.c2Entity);
					this.world.spawnEntity(this.c2Entity);
					this.startRiding(this.c2Entity, true);
					this.c2Entity.setRemainingLife(10000);
					this.aiHover = new EntityGiantBird.AIHoverOverTarget(this.c2Entity, this.flyHeightOffGround, 10.0f, 1.0d);
					this.c2Entity.tasks.addTask(1, this.aiHover);
				} 
			} else {
				if (this.c4Entity != null && this.c4Entity.isEntityAlive()) {
					this.c4Entity.setDead();
				}
				if (this.isRiding() && this.getRidingEntity() == this.c2Entity && this.c2Entity.getRemainingLife() > 100) {
					this.c2Entity.setRemainingLife(100);
					this.c2Entity.tasks.removeTask(this.aiHover);
				}
			}
			if ((this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemAkatsukiRobe.helmet) != (target == null)) {
				this.swapWithInventory(EntityEquipmentSlot.HEAD, 1);
			}
		}

		private void moveC2ToOpenSpace() {
			BlockPos pos = this.c2Entity.getPosition();
			for (; pos.getY() < this.c2Entity.posY + this.flyHeightOffGround && !ProcedureUtils.isSpaceOpenToStandOn(this.c2Entity, pos); pos = pos.up());
			if (pos.getY() < this.c2Entity.posY + this.flyHeightOffGround) {
				 this.c2Entity.setPosition(0.5d + pos.getX(), pos.getY(), 0.5d + pos.getZ());
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.FALL) {
				return false;
			}
			if (!this.world.isRemote && !this.isAIDisabled() && source.getTrueSource() instanceof EntityLivingBase
			 && !this.isRiding() && this.ticksExisted - this.explosiveCloneLastUsed > this.explosiveCloneCD
			 //&& EntityAITarget.isSuitableTarget(this, (EntityLivingBase)source.getTrueSource(), false, false)
			 && this.consumeChakra(ItemBakuton.CLONE.chakraUsage)) {
				this.setRevengeTarget((EntityLivingBase)source.getTrueSource());
				EntityExplosiveClone.EC clone = EntityExplosiveClone.EC.Jutsu.createJutsu(this);
				clone.attackEntityFrom(source, amount);
				this.explosiveCloneLastUsed = this.ticksExisted;
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
			//ProcedureSync.EntityNBTTag.setAndSync(this, NarutomodModVariables.forceBowPose, swingingArms);
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			if (this.consumeChakra(ItemBakuton.CLAY.chakraUsage * 0.5d)) {
				this.swingArm(EnumHand.MAIN_HAND);
				this.setLastAttackedEntity(target);
				for (int i = 0; i < 1 + this.rand.nextInt(2); i++) {
					ItemBakuton.CLAY.jutsu.createJutsu(null, this, 1.0f);
				}
			}
		}

		@Override
		public double getYOffset() {
			return -0.35D;
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn)
			 || (entityIn instanceof EntityExplosiveClone.EC && this == ((EntityExplosiveClone.EC)entityIn).getSummoner());
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return SoundEvents.ENTITY_ILLUSION_ILLAGER_AMBIENT;
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
			 //&& this.world.getEntitiesWithinAABB(EntityCustom.class, this.getEntityBoundingBox().grow(128.0D)).isEmpty();
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
			this.trackAttackedPlayers();
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));

		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/deidara256.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelDeidara());
			}

			@Override
			protected void renderLayers(EntityCustom entity, float f0, float f1, float f2, float f3, float f4, float f5, float f6) {
				if (!entity.isInvisible()) {
					super.renderLayers(entity, f0, f1, f2, f3, f4, f5, f6);
				}
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

		// Made with Blockbench 4.10.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelDeidara extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer rightBag;
			private final ModelRenderer leftBag;

			public ModelDeidara() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -7.6F, -3.6F, 8, 8, 8, 0.7F, false));
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 48, 0, -4.0F, 0.0F, 4.0F, 8, 4, 0, 0.0F, false));
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, -3.7F, 1.0F);
				bipedHeadwear.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.1745F, 0.0F, 0.0873F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 32, 0, -4.0F, -4.0F, -4.0F, 8, 8, 8, 0.7F, false));
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(0.0F, -3.5F, 0.5F);
				bipedHeadwear.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.0873F, 0.0F, -0.0873F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 32, 0, -4.0F, -4.0F, -4.0F, 8, 8, 8, 0.7F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));		
				bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 34, -7.0F, -3.0F, -6.01F, 16, 12, 0, -4.0F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				rightBag = new ModelRenderer(this);
				rightBag.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightLeg.addChild(rightBag);
				rightBag.cubeList.add(new ModelBox(rightBag, 24, 0, -4.1F, 0.0F, -2.0F, 2, 4, 4, 0.0F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				leftBag = new ModelRenderer(this);
				leftBag.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftLeg.addChild(leftBag);
				leftBag.cubeList.add(new ModelBox(leftBag, 24, 0, 2.1F, 0.0F, -2.0F, 2, 4, 4, 0.0F, true));
				rightBag.showModel = false;
				leftBag.showModel = false;
			}
		}
	}
}
