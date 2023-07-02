
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
//import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemIceSenbon;
import net.narutomod.item.ItemHyoton;
import net.narutomod.ElementsNarutomodMod;

import java.util.Iterator;
import java.util.ArrayList;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityHaku extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 237;
	public static final int ENTITYID_RANGED = 238;

	public EntityHaku(ElementsNarutomodMod instance) {
		super(instance, 547);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "haku"), ENTITYID)
				.name("haku").tracker(64, 3, true).egg(-16737895, -1).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IRangedAttackMob, IMob {
		private EntityLivingBase leader;
		private boolean shouldDefendLeader = true;
		private final int ICE_SPEARS_CD = 80;
		private final int DOME_SPEARS_CD = 160;
		private final int DOME_CD = 500;
		private final int MAX_DOME_USAGE = DOME_SPEARS_CD + 180;
		private final double ICE_SPEARS_CHAKRA = ItemHyoton.ICESPEARS.chakraUsage;
		private final double ICE_DOME_CHAKRA = ItemHyoton.ICEDOME.chakraUsage;
		private EntityIceDome.EC domeEntity;
		private int domeSpearsLastUsed;
		private int domeLastUsed;
		private final ItemStack senbon = new ItemStack(ItemIceSenbon.block, 1);

		public EntityCustom(World worldIn) {
			super(worldIn, 80, 4000d);
			this.setSize(0.525f, 1.75f);
			this.isImmuneToFire = true;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.5d, true) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && this.attacker.getRevengeTarget() == null;
				}
				@Override
				public boolean shouldContinueExecuting() {
					return super.shouldContinueExecuting() && this.attacker.getAttackTarget().getDistance(EntityCustom.this) <= 3d;
				}
			});
			this.tasks.addTask(2, new EntityNinjaMob.AIAttackRangedTactical(this, 1.25D, ICE_SPEARS_CD, 10.0F));
			this.tasks.addTask(3, new AIFollowLeader(this, 0.5d, 4f));
			this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
		}

		@Override
		protected float getSoundPitch() {
			return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 2.4F;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(40D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		public void setLeader(EntityLivingBase entity) {
			this.leader = entity;
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || EntityNinjaMob.TeamZabuza.contains(entityIn.getClass());
		}

		@Override
		public void setAttackTarget(@Nullable EntityLivingBase entityIn) {
			super.setAttackTarget(entityIn);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, entityIn == null ? ItemStack.EMPTY : this.senbon);
		}

		/*private void defendLeader() {
			if (!this.isAIDisabled() && this.shouldDefendLeader) {
				EntityLivingBase target = null;
				if (this.leader.getAttackingEntity() != null) {
					target = this.leader.getAttackingEntity();
				}
				if (target == null && this.leader.getLastAttackedEntity() != null 
				 && !this.isOnSameTeam(this.leader.getLastAttackedEntity())
				 && this.leader.ticksExisted - this.leader.getLastAttackedEntityTime() < 200) {
					target = this.leader.getLastAttackedEntity();
				}
				if (target != null) {
					this.setAttackTarget(target);
				} else if (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive()) {
					this.setAttackTarget(null);
				}
			}
		}*/

		@Override
		protected void updateAITasks() {
			//if (this.leader != null) {
			//	this.defendLeader();
			//}
			if (this.getAttackTarget() != null) {
				EntityLivingBase target = this.getAttackTarget();
				if (((this.leader != null && this.leader.getHealth() < this.leader.getMaxHealth() * 0.25f)
						|| (this.getHealth() < this.getMaxHealth() * 0.5f))
						&& !this.domeActive() && this.ticksExisted > this.domeLastUsed + DOME_CD && this.getChakra() > ICE_DOME_CHAKRA * 5d) {
					this.domeEntity = new EntityIceDome.EC.Jutsu().createJutsu(this, target.posX, target.posY - 0.1d, target.posZ);
					this.domeEntity.excludeEntity(this.leader);
					this.domeSpearsLastUsed = this.ticksExisted - DOME_SPEARS_CD + 60;
					this.domeLastUsed = this.ticksExisted;
				}
			}
			if (this.domeActive()) {
				if (this.domeEntity.ticksExisted > MAX_DOME_USAGE) {
					//|| !this.domeEntity.getEntitiesInside().contains(this.getAttackTarget())) {
					this.domeEntity.setDead();
				} else if (this.ticksExisted > this.domeSpearsLastUsed + DOME_SPEARS_CD) {
					this.domeEntity.shootSpears();
					this.domeSpearsLastUsed = this.ticksExisted;
				}
			}
			super.updateAITasks();
		}

		private boolean domeActive() {
			return this.domeEntity != null && this.domeEntity.isEntityAlive();
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			if (!this.world.isRemote && this.consumeChakra(ICE_SPEARS_CHAKRA)) {
				new EntityIceSpear.EC.Jutsu().createJutsu(this, target, 2f);
			}
		}

		public static class AIFollowLeader extends EntityClone.AIFollowSummoner {
			private final EntityCustom entity;

			public AIFollowLeader(EntityCustom entityIn, double speed, float stopRange) {
				super(entityIn, speed, stopRange);
				this.entity = entityIn;
			}

			@Override
			public boolean shouldExecute() {
				if (this.entity.leader == null) {
					return false;
				} else if (this.entity.leader instanceof EntityPlayer && ((EntityPlayer)this.entity.leader).isSpectator()) {
					return false;
				} else if (this.entity.getDistanceSq(this.entity.leader) < (double)(this.stopDistance * this.stopDistance)) {
					return false;
				} else {
					this.followingEntity = this.entity.leader;
					return true;
				}
			}

			@Override
			protected double getSpeed() {
				if (this.followingEntity instanceof EntityLiving
						&& ((EntityLiving)this.followingEntity).getAttackTarget() != null) {
					return super.getSpeed() * 2.0d;
				}
				return super.getSpeed();
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
				return new RenderCustom(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/haku.png");

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
				return TEXTURE;
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
				this.bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.3F, false));
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

			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
			}
		}
	}
}
