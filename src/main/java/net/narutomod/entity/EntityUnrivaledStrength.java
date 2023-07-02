
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.init.SoundEvents;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemSteamArmor;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityUnrivaledStrength extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 286;
	public static final int ENTITYID_RANGED = 287;

	public EntityUnrivaledStrength(ElementsNarutomodMod instance) {
		super(instance, 606);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "unrivaled_strength"), ENTITYID)
		 .name("unrivaled_strength").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private EntityLivingBase user;
		private int duration;
		private boolean isWearingSteamArmor;
		private Entity target;
		private int attackTime;

		public EC(World worldIn) {
			super(worldIn);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, float power) {
			this(userIn.world);
			this.user = userIn;
			this.isWearingSteamArmor = ItemSteamArmor.isWearingFullSet(userIn);
			if (this.isWearingSteamArmor) {
				this.duration = (int)(power * 60f);
				power *= 1.5f;
			} else {
				this.duration = (int)(power * 20f);
			}
			this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
			this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kairikimuso")), 1f, 1f);
			PotionEffect effect = userIn.getActivePotionEffect(MobEffects.STRENGTH);
			userIn.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, this.duration, 
			 (int)power + (effect != null ? effect.getAmplifier() : -1), false, false));
			effect = userIn.getActivePotionEffect(MobEffects.SPEED);
			userIn.addPotionEffect(new PotionEffect(MobEffects.SPEED, this.duration, 
			 (int)(power * 2f) + (effect != null ? effect.getAmplifier() : -1), false, false));
			effect = userIn.getActivePotionEffect(MobEffects.JUMP_BOOST);
			userIn.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, this.duration, 
			 (int)(power * 0.2f) + (effect != null ? effect.getAmplifier() : -1), false, false));
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (!this.isWearingSteamArmor) {
					boolean flag = this.ticksExisted <= 10;
					if (flag) {
						for (int i = 0; i < 50; i++) {
							Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY, this.posZ, 1,
							 0d, 1d, 0d, this.rand.nextDouble() - 0.5d, this.rand.nextDouble() * 0.5d, this.rand.nextDouble() - 0.5d, 
							 0x20FFFFFF, 20 + this.rand.nextInt(11), 0);
						}
					} else {
						for (int i = 0; i < 20; i++) {
							Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY, this.posZ, 1,
							 0d, 1d, 0d, (this.rand.nextDouble() - 0.5d) * 0.2d, 0d, (this.rand.nextDouble() - 0.5d) * 0.2d,
							 0x20FFFFFF, 10 + this.rand.nextInt(11), 0, 0, this.user.getEntityId());
						}
					}
					for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(flag ? 7d : 4d))) {
						if (!entity.equals(this.user)) {
							entity.hurtResistantTime = 10;
							entity.attackEntityFrom(DamageSource.HOT_FLOOR, 1f);
						}
					}
				} else {
					Vec3d vec = new Vec3d(0d, 0d, -0.5d).rotateYaw(-this.user.renderYawOffset * (float)Math.PI / 180F)
					 .addVector(this.posX, this.posY + 1.4d, this.posZ);
					Particles.spawnParticle(this.world, Particles.Types.SMOKE, vec.x, vec.y, vec.z, 10,
					 0d, 0d, 0d, (this.rand.nextDouble() - 0.5d) * 0.1d, 0.0d, (this.rand.nextDouble() - 0.5d) * 0.1d,
					 0x20FFFFFF, 10 + this.rand.nextInt(11), 0, 0, this.user.getEntityId());
				}
				if (this.user.swingProgressInt == 1 && this.user instanceof EntityPlayer) {
					RayTraceResult res = ProcedureUtils.objectEntityLookingAt(this.user, 3d, this);
					if (res != null && res.entityHit instanceof EntityLivingBase) {
						ProcedureUtils.pushEntity(this.user, res.entityHit, 15d, 1.5f);
					} else {
						res = ProcedureUtils.objectEntityLookingAt(this.user, 12d, 3d, this);
						if (res != null && res.entityHit instanceof EntityLivingBase) {
							this.target = res.entityHit;
							this.attackTime = 0;
							this.user.rotationYaw = ProcedureUtils.getYawFromVec(this.target.getPositionVector()
							 .subtract(this.user.getPositionVector()));
							double d0 = this.target.posX - this.user.posX;
							double d1 = this.target.posY - this.user.posY;
							double d2 = this.target.posZ - this.user.posZ;
							double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
							ProcedureUtils.setVelocity(this.user, d0 * 0.4, d1 * 0.4 + d3 * 0.02d, d2 * 0.4);
						}
					}
				}
				if (this.attackTime < 12 && this.target != null && this.target.getDistanceSq(this.user) < 25d) {
					((EntityPlayer)this.user).attackTargetEntityWithCurrentItem(this.target);
					ProcedureUtils.pushEntity(this.user, this.target, 15d, 1.5f);
					this.target = null;
				}
				++this.attackTime;
			}
			if (this.ticksExisted % 5 == 4) {
				this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.2f, this.rand.nextFloat() * 0.5f + 0.4f);
			}
			if (!this.world.isRemote && (this.user == null || this.ticksExisted > this.duration)) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.spawnEntity(new EC(entity, power));
				return true;
			}
		}
	}
}
