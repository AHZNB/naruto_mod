
package net.narutomod.entity;

import net.narutomod.item.ItemSenbonArm;
import net.narutomod.item.ItemPoisonSenbon;
import net.narutomod.item.ItemScrollHiruko;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
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
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;

import javax.annotation.Nullable;
import com.google.common.base.Predicate;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySasori extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 437;
	public static final int ENTITYID_RANGED = 438;

	public EntitySasori(ElementsNarutomodMod instance) {
		super(instance, 868);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "sasori"), ENTITYID)
				.name("sasori").tracker(64, 3, true).egg(-16777216, -65485).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityCustom.AttackHook());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
		private final int BLOCKING_CD = 30;
		private int lastBlockTime;
		private EntityPuppetHiruko.EntityCustom hirukoEntity;
		private int senbonArmShootCount;

		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.setSize(0.525f, 1.75f);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemToInventory(new ItemStack(ItemPoisonSenbon.block), 0);
			this.setItemToInventory(new ItemStack(ItemSenbonArm.block), 1);
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
		protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
			if (this.rand.nextFloat() < 0.5f) {
				this.entityDropItem(new ItemStack(ItemScrollHiruko.block, 1), 0f);
			}
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedTactical(this, 0.6d, 30, 12.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.isRidingHiruko();
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
							return p_apply_1_ != null && EntityBijuManager.isJinchuriki(p_apply_1_);
						}
					}));
			//this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityMob.class, false, false));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.getAttackTarget() != null && this.rand.nextFloat() < 0.01f
			 && this.isRidingHiruko() && this.hirukoEntity.getHealth() < this.hirukoEntity.getMaxHealth() * 0.3f
			 && this.getItemFromInventory(1).getItem() == ItemSenbonArm.block) {
				this.swapWithInventory(EntityEquipmentSlot.OFFHAND, 1);
			}
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || EntityNinjaMob.TeamAkatsuki.contains(entityIn.getClass());
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
			if (this.isRidingHiruko()) {
				this.hirukoEntity.attackEntityFrom(source, amount);
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			if (this.isRidingHiruko()) {
				double d = this.getDistance(target);
				if (d > 8.0d && this.getHeldItemOffhand().getItem() == ItemSenbonArm.block) {
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
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere()
			 && this.world.getEntitiesWithinAABB(EntityCustom.class, this.getEntityBoundingBox().grow(128.0D)).isEmpty();
			 //&& this.rand.nextInt(5) == 0;
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
		public void onUpdate() {
			if (this.hirukoEntity == null) {
				if (this.getRidingEntity() instanceof EntityPuppetHiruko.EntityCustom) {
					this.hirukoEntity = (EntityPuppetHiruko.EntityCustom)this.getRidingEntity();
				} else if (!this.world.isRemote) {
					this.hirukoEntity = new EntityPuppetHiruko.EntityCustom(this, this.posX, this.posY, this.posZ);
					this.hirukoEntity.setAkatsuki(true);
					this.world.spawnEntity(this.hirukoEntity);
				}
			}
			if (!this.world.isRemote) {
				if (this.isRidingHiruko()) {
					if (this.width < this.hirukoEntity.width - 0.01f) {
						this.setSize(this.hirukoEntity.width - 0.01f, this.hirukoEntity.height - 0.01f);
					}
				} else if (this.width > 0.525f) {
					this.setSize(0.525f, 1.75f);
				}
			} else if (this.isRidingHiruko()) {
				this.hirukoEntity.raiseLeftArm(this.getHeldItemOffhand().getItem() == ItemSenbonArm.block);
			}
			this.clearActivePotions();
			super.onUpdate();
			if (!this.world.isRemote && this.isRidingHiruko() && this.ticksExisted > this.lastBlockTime + 20) {
				this.hirukoEntity.blockAttack(false);
			}
			this.trackAttackedPlayers();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.hirukoEntity != null && this.hirukoEntity.isEntityAlive()) {
				this.hirukoEntity.setDead();
			}
		}

		private boolean isRidingHiruko() {
			return this.hirukoEntity != null && this.hirukoEntity.isEntityAlive() && this.hirukoEntity.equals(this.getRidingEntity());
		}

		@Override
		public double getYOffset() {
			return -0.35d;
		}

		public static class AttackHook {
			@SubscribeEvent
			public void onAttacked(LivingAttackEvent event) {
				if (event.getEntityLiving() instanceof EntityPuppetHiruko.EntityCustom
				 && event.getEntityLiving().getControllingPassenger() instanceof EntityCustom
				 && !event.getSource().isUnblockable()) {
					EntityPuppetHiruko.EntityCustom hiruko = (EntityPuppetHiruko.EntityCustom)event.getEntityLiving();
					EntityCustom sasori = (EntityCustom)event.getEntityLiving().getControllingPassenger();
					if (sasori.ticksExisted > sasori.lastBlockTime + sasori.BLOCKING_CD) {
						hiruko.blockAttack(true);
						sasori.lastBlockTime = sasori.ticksExisted;
					}
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
		public class ModelSasori extends EntityNinjaMob.ModelNinja {
			//private final ModelRenderer bipedHead;
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer robeHead;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer robeBody;
			private final ModelRenderer scrolls;
			private final ModelRenderer backBladesRight;
			private final ModelRenderer bone;
			private final ModelRenderer blade1;
			private final ModelRenderer blade2;
			private final ModelRenderer blade3;
			private final ModelRenderer blade4;
			private final ModelRenderer blade5;
			private final ModelRenderer backBladesLeft;
			private final ModelRenderer bone12;
			private final ModelRenderer blade6;
			private final ModelRenderer blade7;
			private final ModelRenderer blade8;
			private final ModelRenderer blade9;
			private final ModelRenderer blade10;
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
			public ModelSasori() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bipedHead, -0.1047F, 0.0873F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.3F, false));
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
				setRotationAngle(backBladesRight, 0.0F, 0.5236F, -0.2618F);
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -1.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -3.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -5.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -7.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -9.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-8.8F, 0.5F, -0.5F);
				backBladesRight.addChild(bone);
				setRotationAngle(bone, 0.0F, -1.0472F, 0.0F);
				blade1 = new ModelRenderer(this);
				blade1.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone.addChild(blade1);
				setRotationAngle(blade1, 0.0F, 0.0F, -0.5236F);
				blade1.cubeList.add(new ModelBox(blade1, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade1.cubeList.add(new ModelBox(blade1, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade1.cubeList.add(new ModelBox(blade1, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade1.cubeList.add(new ModelBox(blade1, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade2 = new ModelRenderer(this);
				blade2.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone.addChild(blade2);
				setRotationAngle(blade2, 0.0F, 0.0F, 0.0873F);
				blade2.cubeList.add(new ModelBox(blade2, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade2.cubeList.add(new ModelBox(blade2, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade2.cubeList.add(new ModelBox(blade2, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade2.cubeList.add(new ModelBox(blade2, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade3 = new ModelRenderer(this);
				blade3.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone.addChild(blade3);
				setRotationAngle(blade3, 0.0F, 0.0F, 0.6981F);
				blade3.cubeList.add(new ModelBox(blade3, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade3.cubeList.add(new ModelBox(blade3, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade3.cubeList.add(new ModelBox(blade3, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade3.cubeList.add(new ModelBox(blade3, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade4 = new ModelRenderer(this);
				blade4.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone.addChild(blade4);
				setRotationAngle(blade4, 0.0F, 0.0F, 1.309F);
				blade4.cubeList.add(new ModelBox(blade4, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade4.cubeList.add(new ModelBox(blade4, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade4.cubeList.add(new ModelBox(blade4, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade4.cubeList.add(new ModelBox(blade4, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade5 = new ModelRenderer(this);
				blade5.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone.addChild(blade5);
				setRotationAngle(blade5, 0.0F, 0.0F, 1.9199F);
				blade5.cubeList.add(new ModelBox(blade5, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade5.cubeList.add(new ModelBox(blade5, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade5.cubeList.add(new ModelBox(blade5, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				blade5.cubeList.add(new ModelBox(blade5, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
				backBladesLeft = new ModelRenderer(this);
				backBladesLeft.setRotationPoint(3.2F, 6.0F, 3.0F);
				bipedBody.addChild(backBladesLeft);
				setRotationAngle(backBladesLeft, 0.0F, -0.5236F, 0.2618F);
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, -0.2F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 1.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 3.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 5.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 7.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(8.8F, 0.5F, -0.5F);
				backBladesLeft.addChild(bone12);
				setRotationAngle(bone12, 0.0F, 0.7854F, 0.0F);
				blade6 = new ModelRenderer(this);
				blade6.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone12.addChild(blade6);
				setRotationAngle(blade6, 0.0F, 0.0F, 0.5236F);
				blade6.cubeList.add(new ModelBox(blade6, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade6.cubeList.add(new ModelBox(blade6, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade6.cubeList.add(new ModelBox(blade6, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade6.cubeList.add(new ModelBox(blade6, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade7 = new ModelRenderer(this);
				blade7.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone12.addChild(blade7);
				setRotationAngle(blade7, 0.0F, 0.0F, -0.0873F);
				blade7.cubeList.add(new ModelBox(blade7, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade7.cubeList.add(new ModelBox(blade7, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade7.cubeList.add(new ModelBox(blade7, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade7.cubeList.add(new ModelBox(blade7, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade8 = new ModelRenderer(this);
				blade8.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone12.addChild(blade8);
				setRotationAngle(blade8, 0.0F, 0.0F, -0.6981F);
				blade8.cubeList.add(new ModelBox(blade8, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade8.cubeList.add(new ModelBox(blade8, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade8.cubeList.add(new ModelBox(blade8, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade8.cubeList.add(new ModelBox(blade8, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade9 = new ModelRenderer(this);
				blade9.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone12.addChild(blade9);
				setRotationAngle(blade9, 0.0F, 0.0F, -1.309F);
				blade9.cubeList.add(new ModelBox(blade9, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade9.cubeList.add(new ModelBox(blade9, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade9.cubeList.add(new ModelBox(blade9, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade9.cubeList.add(new ModelBox(blade9, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade10 = new ModelRenderer(this);
				blade10.setRotationPoint(0.0F, 0.0F, 0.5F);
				bone12.addChild(blade10);
				setRotationAngle(blade10, 0.0F, 0.0F, -1.9199F);
				blade10.cubeList.add(new ModelBox(blade10, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade10.cubeList.add(new ModelBox(blade10, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade10.cubeList.add(new ModelBox(blade10, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
				blade10.cubeList.add(new ModelBox(blade10, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
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

				scrolls.showModel = false;
				backBladesLeft.showModel = false;
				backBladesRight.showModel = false;
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
