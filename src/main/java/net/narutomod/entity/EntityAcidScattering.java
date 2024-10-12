
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.potion.PotionCorrosion;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityAcidScattering extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 457;
	public static final int ENTITYID_RANGED = 458;

	public EntityAcidScattering(ElementsNarutomodMod instance) {
		super(instance, 891);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "acid_scattering"), ENTITYID).name("acid_scattering").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityAcidParticle.class)
		 .id(new ResourceLocation("narutomod", "acid_particle"), ENTITYID_RANGED).name("acid_particle").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements ItemJutsu.IJutsu {
		private static final int PARTICLE_COLOR = 0xf0ffd6ba;
		private int maxLife = 60;
		private EntityLivingBase shooter;
		private float width, range;
		private int potionAmplifier;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase shooterIn, float widthIn, float rangeIn) {
			this(shooterIn.world);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
			this.shooter = shooterIn;
			this.setIdlePosition();
			this.width = widthIn;
			this.range = rangeIn;
			this.potionAmplifier = (int)(rangeIn * 0.5f);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.SUITON;
		}

		@Override
		protected void entityInit() {
		}

		public void setPotionAmplifier(int amp) {
			this.potionAmplifier = amp;
		}

		public void setDuration(int ticks) {
			this.maxLife = ticks;
		}

		public EntityLivingBase getShooter() {
			return this.shooter;
		}

		protected void setIdlePosition() {
			if (this.shooter != null) {
				Vec3d vec3d = this.shooter.getLookVec().add(this.shooter.getPositionEyes(1f));
				this.setPosition(vec3d.x, vec3d.y - 0.2d, vec3d.z);
			}
		}

		@Override
		public void onUpdate() {
			//super.onUpdate();
			if (!this.world.isRemote && (this.ticksExisted > this.maxLife
			 || this.shooter == null || !this.shooter.isEntityAlive() || this.shooter.isInWater())) {
				this.setDead();
			} else {
				this.setIdlePosition();
				if (!this.world.isRemote) {
					if (this.shooter != null) {
						float f = (float)this.ticksExisted / this.maxLife;
						f = 1.0f - f * f * 0.8f;
						if (this.shooter instanceof EntityLiving && ((EntityLiving)this.shooter).getAttackTarget() != null) {
							ProcedureUtils.Vec2f rota = ProcedureUtils.getYawPitchFromVec(((EntityLiving)this.shooter)
							 .getAttackTarget().getPositionVector().subtract(this.getPositionVector()));
							this.shoot(rota.x, rota.y, this.range * f, this.width * f);
						} else {
							this.shoot(this.shooter.rotationYawHead, this.shooter.rotationPitch, this.range * f, this.width * f);
						}
					}
					if (this.ticksExisted < this.maxLife - 20 && this.ticksExisted % 10 == 1) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:waterstream")),
						 this.range > 30.0d ? 5.0f : 1.0f, this.rand.nextFloat() * 0.4f + 0.2f);
					}
				}
			}
		}

		protected void shoot(float directionYaw, float directionPitch, float range, float radius) {
			float angle = (float)(Math.atan(radius / range) * 180d / Math.PI);
			for (int i = 0; i < (int)(MathHelper.sqrt(radius) * 3.0d); i++) {
				Vec3d vec = Vec3d.fromPitchYaw(directionPitch + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d),
				 directionYaw + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d)).scale(range * 0.1d);
				this.world.spawnEntity(new EntityAcidParticle(this, vec.x, vec.y, vec.z));
			}
			Particles.Renderer particles = new Particles.Renderer(this.world);
			for (int i = 0; i < 50; i++) {
				Vec3d vec = Vec3d.fromPitchYaw(directionPitch + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d),
				 directionYaw + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d)).scale(range * 0.1d);
				particles.spawnParticles(Particles.Types.SPIT, this.posX, this.posY, this.posZ, 1, 0, 0, 0,
				 vec.x, vec.y, vec.z, PARTICLE_COLOR, (int)(vec.lengthVector()*70d)+this.rand.nextInt(30),
				 this.shooter.getEntityId(), (int)(32.0f / (this.rand.nextFloat() * 0.8f + 0.2f)));
			}
			particles.send();
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
				if (power >= this.getBasePower()) {
					this.createJutsu(entity, power);
					return true;
				}
				return false;
			}

			public static EC createJutsu(EntityLivingBase entity, float power) {
				EC ec = new EC(entity, power * 0.2f, power);
				entity.world.spawnEntity(ec);
				return ec;
			}

			@Override
			public float getPowerupDelay() {
				return 30.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 30.0f;
			}
		}
	}

	public static class EntityAcidParticle extends EntityParticle.Base {
		private static final DataParameter<Integer> SHOOTER = EntityDataManager.<Integer>createKey(EntityAcidParticle.class, DataSerializers.VARINT);
		private EC ecEntity;
		private EntityLivingBase hitEntity;
		private int hitTime;

		public EntityAcidParticle(World w) {
			super(w);
		}

		public EntityAcidParticle(EC ecIn, double mX, double mY, double mZ) {
			super(ecIn.world, ecIn.posX, ecIn.posY, ecIn.posZ, mX, mY, mZ, EC.PARTICLE_COLOR, 2.5f, 0);
			this.setShooter(ecIn.shooter);
			this.setMaxAge((int)(16.0f / (this.rand.nextFloat()*0.8f+0.2f)));
			this.ecEntity = ecIn;
		}
				
		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SHOOTER, Integer.valueOf(-1));
		}

		@Nullable
		protected EntityLivingBase getShooter() {
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(SHOOTER)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void setShooter(@Nullable EntityLivingBase player) {
			this.getDataManager().set(SHOOTER, Integer.valueOf(player != null ? player.getEntityId() : -1));
		}

		@Override
		public void onUpdate() {
			int age = this.getAge();
			int maxAge = this.getMaxAge();
			this.setParticleTextureOffset(MathHelper.clamp(7 - age * 8 / maxAge, 0, 7));
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (this.hitEntity == null) {
				this.motionY -= 0.05D;
				EntityLivingBase shooter = this.getShooter();
				RayTraceResult res = EntityScalableProjectile.forwardsRaycast(this, true, false, shooter);
				if (res != null && res.entityHit instanceof EntityLivingBase && !res.entityHit.equals(shooter)) {
					this.hitEntity = (EntityLivingBase)res.entityHit;
					this.hitTime = age;
					if (!this.world.isRemote && this.ecEntity != null) {
						this.hitEntity.getEntityData().setBoolean("TempData_disableKnockback", true);
						this.hitEntity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, shooter), 2.0f * this.ecEntity.potionAmplifier);
						this.hitEntity.addPotionEffect(new PotionEffect(PotionCorrosion.potion, 100, this.ecEntity.potionAmplifier, false, false));
					}
				}
			} else {
				this.motionX = this.hitEntity.posX - this.posX;
				this.motionY -= 0.005d;
				this.motionZ = this.hitEntity.posZ - this.posZ;
			}
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.96D;
			this.motionY *= 0.96D;
			this.motionZ *= 0.96D;
			if (this.onGround) {
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}
			if (!this.world.isRemote) {
				this.setAge(++age);
				if (age > maxAge || this.ecEntity == null || this.ecEntity.isDead) {
					this.onDeath();
				}
			}
		}

		@Override
		protected int getTexV() {
			return 2;
		}

		@Override
		public boolean shouldDisableDepth() {
			return true;
		}
	}
}
