
package net.narutomod.entity;

import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemScytheHidan;
import net.narutomod.item.ItemScytheHidanThrown;
import net.narutomod.procedure.ProcedureWhenPlayerAttcked;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import com.google.common.base.Predicate;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.EntityLivingBase;

@ElementsNarutomodMod.ModElement.Tag
public class EntityHidan extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 467;
	public static final int ENTITYID_RANGED = 468;

	public EntityHidan(ElementsNarutomodMod instance) {
		super(instance, 896);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "hidan"), ENTITYID)
				.name("hidan").tracker(64, 3, true).egg(-16777216, -6750157).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private static final DataParameter<Integer> JASHIN_TICKS = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private final int transitionTime = 60;
		private int transitionDirection;
		private EntityLivingBase hitTarget;
		private boolean scytheOnRetrieval;

		public EntityCustom(World world) {
			super(world, 120, 7000d);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemToInventory(new ItemStack(ItemScytheHidan.block), 0);
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobe.body));
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
			this.transitionDirection = i;
			if (i < 0) {
				this.setJashinTicks(this.transitionTime);
			} else if (i > 0) {
				this.setJashinTicks(1);
				ItemStack stack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
				if (stack.getItem() == ItemAkatsukiRobe.body) {
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
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false,
				new Predicate<EntityPlayer>() {
					public boolean apply(@Nullable EntityPlayer p_apply_1_) {
						return p_apply_1_ != null && (ModConfig.AGGRESSIVE_BOSSES || EntityBijuManager.isJinchuriki(p_apply_1_));
					}
				}));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityNinjaMob.AILeapAtTarget(this, 1.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !EntityCustom.this.isRiding()
							&& EntityCustom.this.getAttackTarget().posY - EntityCustom.this.posY > 3d;
				}
			});
			this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 10, 16.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.getDistance(EntityCustom.this.getAttackTarget()) > 6.0d
					 && EntityCustom.this.getHeldItemMainhand().getItem() == ItemScytheHidan.block;
				}
			});
			this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0d, true) {
				@Override
				protected double getAttackReachSqr(EntityLivingBase attackTarget) {
					return 16.0d;
				}
			});
			this.tasks.addTask(4, new EntityAIWatchClosest2(this, EntityPlayer.class, 32.0F, 1.0F));
			this.tasks.addTask(5, new EntityAIWander(this, 0.5d));
			this.tasks.addTask(6, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			EntityLivingBase target = this.getAttackTarget();
			ItemStack stack = this.getHeldItemMainhand();
			if (stack.getItem() != ItemScytheHidanThrown.block) {
				super.updateAITasks();
			} else {
				ItemScytheHidan.EntityCustom entity = ((ItemScytheHidanThrown.RangedItem)stack.getItem()).getEntity(this.world, stack);
				this.hitTarget = entity.getHitTarget();
				if (entity.inGround()) {
					this.standStillFor(30);
					stack.onPlayerStoppedUsing(this.world, this, 0);
					this.scytheOnRetrieval = true;
				}
			}
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
			this.swingArm(EnumHand.MAIN_HAND);
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			ItemStack stack = this.getHeldItemMainhand();
			if (stack.getItem() == ItemScytheHidan.block) {
				stack.onPlayerStoppedUsing(this.world, this, stack.getMaxItemUseDuration() - 50);
			}
		}

		@Override
		public void onLivingUpdate() {
			super.onLivingUpdate();
			if (this.scytheOnRetrieval) {
				for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D))) {
					if (entity instanceof ItemScytheHidan.EntityCustom && this.equals(((ItemScytheHidan.EntityCustom)entity).getShooter())) {
						this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemScytheHidan.block));
						entity.setDead();
						this.scytheOnRetrieval = false;
					}
				}
			}
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || EntityNinjaMob.TeamAkatsuki.contains(entityIn.getClass());
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
			
			this.trackAttackedPlayers();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

			/*if (!this.world.isRemote) {
				if (this.ticksExisted == 80) {
					this.setJashinTransitionDirection(1);
				} else if (this.ticksExisted == 240) {
					this.setJashinTransitionDirection(-1);
				}
			}*/
			if (!this.world.isRemote) {
				ProcedureWhenPlayerAttcked.setExtraDamageReduction(this, 0.95f);
			}
			
			if (!this.world.isRemote && this.transitionDirection != 0) {
				int jashinTicks = this.getJashinTicks();
				if (jashinTicks > 0) {
					this.setJashinTicks(jashinTicks + this.transitionDirection);
				} else {
					this.transitionDirection = 0;
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/hidan.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelHidan());
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
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
				float jashinTicks = (float)((EntityCustom)entityIn).getJashinTicks();
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
