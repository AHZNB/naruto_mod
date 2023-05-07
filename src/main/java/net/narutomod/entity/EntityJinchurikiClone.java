
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.world.GameType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityJinchurikiClone extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 263;
	public static final int ENTITYID_RANGED = 264;

	public EntityJinchurikiClone(ElementsNarutomodMod instance) {
		super(instance, 586);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "jinchuriki_clone"), ENTITYID).name("jinchuriki_clone").tracker(64, 3, true).build());
	}

	public static class EntityCustom extends EntityClone.Base implements IRangedAttackMob {
		private int level;
		private Chakra.Pathway chakra;
		private int idleTime;

		public EntityCustom(World world) {
			super(world);
			this.shouldDefendSummoner = false;
		}

		public EntityCustom(EntityLivingBase user) {
			super(user);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64d);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ProcedureUtils.getModifiedSpeed(user));
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ProcedureUtils.getModifiedAttackDamage(user));
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(user.getMaxHealth());
			this.shouldDefendSummoner = false;
			this.setAttackTarget(user.getLastAttackedEntity());
			this.level = user instanceof EntityPlayer ? EntityBijuManager.cloakLevel((EntityPlayer)user) : 1;
			this.chakra = Chakra.pathway(this);
			Chakra.Pathway userChakra = Chakra.pathway(user);
			this.chakra.setMax(userChakra.getMax());
			this.chakra.consume(-userChakra.getAmount(), true);
			if (user instanceof EntityPlayerMP) {
				this.getEntityData().setInteger("OriginalGameMode", ((EntityPlayerMP)user).interactionManager.getGameType().getID());
				((EntityPlayerMP)user).setGameType(GameType.SPECTATOR);
			}
		}

		public int getLevel() {
			return this.level;
		}

		@Override
		protected void initEntityAI() {
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, false, false) {
				@Override
				protected AxisAlignedBB getTargetableArea(double targetDistance) {
					return EntityCustom.this. getEntityBoundingBox().grow(targetDistance, 14.0D, targetDistance);
				}
			});
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityNinjaMob.AILeapAtTarget(this, 1.5f));
			this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.0d, 40, 80f) {
				@Override
				public boolean shouldExecute() {
					return EntityCustom.this.level == 2 &&
					 super.shouldExecute() && EntityCustom.this.getDistance(EntityCustom.this.getAttackTarget()) > 24d;
				}
			});
			this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0d, true) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.getDistance(EntityCustom.this.getAttackTarget()) <= 24d;
				}
				@Override
				protected double getAttackReachSqr(EntityLivingBase attackTarget) {
					return (3d + EntityCustom.this.level) * (3d + EntityCustom.this.level);
				}
			});
			this.tasks.addTask(5, new EntityAILookIdle(this));
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			EntityTailedBeast.EntityTailBeastBall.spawn(this, 2f, 150f);
		}

		@Override
		public void setDead() {
			if (!this.world.isRemote && this.getSummoner() instanceof EntityPlayerMP && !this.isDead) {
				EntityPlayerMP user = (EntityPlayerMP)this.getSummoner();
				user.setGameType(GameType.getByID(this.getEntityData().getInteger("OriginalGameMode")));
				user.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 2, 0, false, false));
				if (user.isEntityAlive()) {
					float f = this.getHealth();
					user.setHealth(f);
					if (f <= 0.0f) {
						EntityBijuManager.toggleBijuCloak(user);
					}
				}
			}
			super.setDead();
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.getAttackTarget() == null || !this.getAttackTarget().isEntityAlive()) {
				++this.idleTime;
			} else {
				this.idleTime = 0;
			}
			if (this.idleTime > 200) {
				this.setDead();
				if (this.getSummoner() instanceof EntityPlayer) {
					EntityBijuManager.toggleBijuCloak((EntityPlayer)this.getSummoner());
				}
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.getSummoner() instanceof EntityPlayerMP) {
				EntityPlayerMP user = (EntityPlayerMP)this.getSummoner();
				if (!user.isEntityAlive()) {
					user.setSpectatingEntity(user);
				} else if (user.getSpectatingEntity() != this) {
					user.setSpectatingEntity(this);
				}
				Chakra.Pathway userChakra = Chakra.pathway(user);
				userChakra.consume(userChakra.getAmount() - this.chakra.getAmount());
			}
			for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				ItemStack itemstack = this.getItemStackFromSlot(slot);
				if (!itemstack.isEmpty()) {
					itemstack.updateAnimation(this.world, this, slot.getSlotIndex(), false);
				}
			}
		}
	}
}
