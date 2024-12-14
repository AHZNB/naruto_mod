package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.DamageSource;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.WorldServer;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityScalableProjectile extends ElementsNarutomodMod.ModElement {
	public EntityScalableProjectile(ElementsNarutomodMod instance) {
		super(instance, 520);
	}
	
	public static abstract class Base extends Entity implements IProjectile {
		private static final DataParameter<Float> MODEL_SCALE = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private float ogWidth;
		private float ogHeight;
		public EntityLivingBase shootingEntity;
		private double accelerationX;
		private double accelerationY;
		private double accelerationZ;
		protected int ticksAlive;
		protected int ticksInAir;
		protected int ticksInGround;
		protected int maxInGroundTime = 1200;
		private float motionFactor;
		private float waterSlowdown = 0.8f;
		public float prevRotationRoll;
		public float rotationRoll;
		
		public Base(World world) {
			super(world);
			this.isImmuneToFire = false;
			//this.setEntityInvulnerable(true);
		}

		public Base(EntityLivingBase shooter) {
			this(shooter.world);
			this.shootingEntity = shooter;
			//this.setPosition(shooter.posX, shooter.posY + shooter.height + 0.5D, shooter.posZ);
			this.setNoGravity(true);
			this.setAlwaysRenderNameTag(false);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(MODEL_SCALE, Float.valueOf(1.0F));
		}

		protected void setOGSize(float width, float height) {
			this.ogWidth = width;
			this.ogHeight = height;
			if (this.firstUpdate) {
				this.setSize(width, height);
			}
		}

		public float getEntityScale() {
			return ((Float) this.getDataManager().get(MODEL_SCALE)).floatValue();
		}

		public void setEntityScale(float scale) {
			double x = this.posX;
			double y = this.posY;
			double z = this.posZ;
			this.setSize(this.ogWidth * scale, this.ogHeight * scale);
			this.setPosition(x, y, z);
			if (!this.world.isRemote) {
				this.getDataManager().set(MODEL_SCALE, Float.valueOf(scale));
			}
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (MODEL_SCALE.equals(key) && this.world.isRemote) {
				this.setEntityScale(this.getEntityScale());
			}
		}

		public boolean isLaunched() {
			return this.motionFactor > 0.0F;
		}

		public void haltMotion() {
			this.motionFactor = 0.0f;
			this.accelerationX = 0.0D;
			this.accelerationY = 0.0D;
			this.accelerationZ = 0.0D;
			this.motionX = 0.0d;
			this.motionY = 0.0d;
			this.motionZ = 0.0d;
		}

		protected void setMotionFactor(float f) {
			this.motionFactor = f;
		}

		protected float getMotionFactor() {
			return this.motionFactor;
		}

		/*public void onKillCommand() {
		}*/

		protected void setWaterSlowdown(float f) {
			this.waterSlowdown = f;
		}

		@Override
		public void shoot(double x, double y, double z, float speed, float inaccuracy) {
			this.shoot(x, y, z, speed, inaccuracy, true);
		}

		public void shoot(double x, double y, double z, float speed, float inaccuracy, boolean updateRotations) {
			x += this.rand.nextGaussian() * inaccuracy;
			y += this.rand.nextGaussian() * inaccuracy;
			z += this.rand.nextGaussian() * inaccuracy;
			if (updateRotations) {
				float f1 = MathHelper.sqrt(x * x + z * z);
				this.rotationYaw = (float) (-MathHelper.atan2(x, z) * (180d / Math.PI));
				this.rotationPitch = (float) (-MathHelper.atan2(y, f1) * (180d / Math.PI));
				if (this.motionFactor == 0.0f) {
					this.prevRotationYaw = this.rotationYaw;
					this.prevRotationPitch = this.rotationPitch;
					this.prevRotationRoll = this.rotationRoll;
				}
			}
			double d0 = MathHelper.sqrt(x * x + y * y + z * z);
			if (this.hasNoGravity()) {
				this.accelerationX = x / d0 * 0.1D;
				this.accelerationY = y / d0 * 0.1D;
				this.accelerationZ = z / d0 * 0.1D;
			} else {
				this.motionX = x / d0 * speed;
				this.motionY = y / d0 * speed;
				this.motionZ = z / d0 * speed;
			}
			this.motionFactor = speed;
		}

		public void shootPrecise(double x, double y, double z, float speed) {
			double d0 = MathHelper.sqrt(x * x + y * y + z * z);
			this.accelerationX = x / d0 * 0.1D;
			this.accelerationY = y / d0 * 0.1D;
			this.accelerationZ = z / d0 * 0.1D;
			double d1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			this.motionX = x / d0 * d1;
			this.motionY = y / d0 * d1;
			this.motionZ = z / d0 * d1;
			this.motionFactor = speed;
		}

		public double getAcceleration() {
			return MathHelper.sqrt(this.accelerationX * this.accelerationX + this.accelerationY * this.accelerationY + this.accelerationZ * this.accelerationZ) * this.motionFactor;
		}

		@Override
		public boolean canBeCollidedWith() {
			return true;
		}

		/*public boolean attackEntityFrom(DamageSource source, float amount) {
			return false;
		}*/

		protected void checkOnGround() {
			BlockPos pos = new BlockPos(this);
			if (!this.world.isAirBlock(pos)) {
				AxisAlignedBB aabb = this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos);
				if (aabb != net.minecraft.block.Block.NULL_AABB && aabb.offset(pos).contains(this.getPositionVector())) {
					this.onGround = true;
				}
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.ticksAlive++;
			if (!this.world.isRemote && this.shootingEntity != null && this.shootingEntity.isDead
			 || !this.world.isBlockLoaded(new BlockPos(this))) {
				this.setDead();
			} else {
				this.checkOnGround();
				if (this.onGround) {
					this.motionFactor = 0f;
					this.ticksInAir = 0;
					if (++this.ticksInGround > this.maxInGroundTime) {
						this.setDead();
					}
				} else {
					float f = this.motionFactor;
					if (f > 0f) {
						this.ticksInAir++;
						RayTraceResult raytraceresult = this.forwardsRaycast(true, this.ticksInAir >= 25, this.shootingEntity);
						if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
							this.onImpact(raytraceresult);
							//f *= 0.4F;
						}
					}
					this.posX += this.motionX;
					this.posY += this.motionY;
					this.posZ += this.motionZ;
					if (f > 0f && !this.hasNoGravity()) {
						this.updateInFlightRotations();
					}
					if (f > 0f && this.isInWater()) {
						for (int i = 0; i < 4; i++)
							this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D,
							  this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
						f *= this.waterSlowdown;
					}
					if (f > 0f && this.hasNoGravity()) {
						this.motionX += this.accelerationX;
						this.motionY += this.accelerationY;
						this.motionZ += this.accelerationZ;
						this.motionX *= f;
						this.motionY *= f;
						this.motionZ *= f;
						//this.isAirBorne = true;
					}
					if (!this.hasNoGravity()) {
						this.motionX *= 0.98D;
						this.motionZ *= 0.98D;
						this.motionY = this.motionY * 0.98D - 0.04D;
					}
					this.renderParticles();
					this.setPosition(this.posX, this.posY, this.posZ);
				}
			}
		}

		protected RayTraceResult forwardsRaycast(boolean includeEntities, boolean ignoreExcludedEntity, @Nullable Entity excludedEntity) {
			RayTraceResult res = EntityScalableProjectile.forwardsRaycast(this, ProcedureUtils.getMotion(this),
			 includeEntities, ignoreExcludedEntity, excludedEntity);
			return res != null && res.entityHit instanceof Base && ((Base)res.entityHit).shootingEntity != null 
			 && ((Base)res.entityHit).shootingEntity.equals(this.shootingEntity) ? null : res;
		}

		public Random rand() {
			return this.rand;
		}

		public int getTicksAlive() {
			return this.ticksAlive;
		}

		protected abstract void onImpact(RayTraceResult param1RayTraceResult);

		public void updateInFlightRotations() {
            double d = (double)MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            float yaw = -(float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            float pitch = -(float)(MathHelper.atan2(this.motionY, d) * (180D / Math.PI));
            float deltaYaw = ProcedureUtils.subtractDegreesWrap(yaw, this.prevRotationYaw);
            float deltaPitch = ProcedureUtils.subtractDegreesWrap(pitch, this.prevRotationPitch);
            float roll = MathHelper.wrapDegrees(deltaYaw * 1.5f);
            this.prevRotationYaw = yaw - deltaYaw;
            this.prevRotationPitch = pitch - deltaPitch;
            this.prevRotationRoll = this.rotationRoll;
            this.rotationPitch = this.prevRotationPitch + (pitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (yaw - this.prevRotationYaw) * 0.2F;
            this.rotationRoll = this.prevRotationRoll + (roll - this.prevRotationRoll) * 0.2F;
		}

		public void renderParticles() {
			if (this.motionFactor > 0f && this.world instanceof WorldServer) {
				((WorldServer)this.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + (this.height / 2.0F), 
				 this.posZ, (int)this.getEntityScale(), this.width * 0.5, this.height * 0.5, this.width * 0.5, 0.0D, new int[0]);
			}
				/*for (int i = 0; i < (int) this.getEntityScale(); i++) {
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + ((this.rand.nextFloat() - 0.5F) * this.width),
					  this.posY + (this.rand.nextFloat() * this.height), this.posZ + ((this.rand.nextFloat() - 0.5F) * this.width), 
					  0.0D, 0.0D, 0.0D);
				}*/
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean isInRangeToRenderDist(double distance) {
			return distance <= 4096d || super.isInRangeToRenderDist(distance);
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			if (compound.hasKey("power", 9)) {
				NBTTagList nbttaglist = compound.getTagList("power", 6);
				if (nbttaglist.tagCount() == 3) {
					this.accelerationX = nbttaglist.getDoubleAt(0);
					this.accelerationY = nbttaglist.getDoubleAt(1);
					this.accelerationZ = nbttaglist.getDoubleAt(2);
				}
			}
			if (compound.hasKey("direction", 9) && compound.getTagList("direction", 6).tagCount() == 3) {
				NBTTagList nbttaglist1 = compound.getTagList("direction", 6);
				this.motionX = nbttaglist1.getDoubleAt(0);
				this.motionY = nbttaglist1.getDoubleAt(1);
				this.motionZ = nbttaglist1.getDoubleAt(2);
			} else {
				this.setDead();
			}
			this.setEntityScale(compound.getFloat("scale"));
			this.setSize(this.ogWidth * this.getEntityScale(), this.ogHeight * getEntityScale());
			this.motionFactor = compound.getFloat("speed");
			this.ticksAlive = compound.getInteger("life");
			this.ticksInAir = compound.getInteger("flighttime");
			this.ticksInGround = compound.getInteger("groundtime");
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setTag("direction", this.newDoubleNBTList(new double[]{this.motionX, this.motionY, this.motionZ}));
			compound.setTag("power", this.newDoubleNBTList(new double[]{this.accelerationX, this.accelerationY, this.accelerationZ}));
			compound.setFloat("scale", this.getEntityScale());
			compound.setFloat("speed", this.motionFactor);
			compound.setInteger("life", this.ticksAlive);
			compound.setInteger("flighttime", this.ticksInAir);
			compound.setInteger("groundtime", this.ticksInGround);
		}
	}

	public static RayTraceResult forwardsRaycastBlocks(Entity projectile) {
		return forwardsRaycast(projectile, ProcedureUtils.getMotion(projectile), false, false, null);
	}

	public static RayTraceResult forwardsRaycast(Entity projectile, Vec3d motion, boolean includeEntities, boolean ignoreExcludedEntity, @Nullable Entity excludedEntity) {
		return forwardsRaycast(projectile, motion, true, includeEntities, ignoreExcludedEntity, excludedEntity);
	}

	public static RayTraceResult forwardsRaycast(Entity projectile, boolean includeEntities, boolean ignoreExcludedEntity, @Nullable Entity excludedEntity) {
		return forwardsRaycast(projectile, ProcedureUtils.getMotion(projectile), false, includeEntities, ignoreExcludedEntity, excludedEntity);
	}

	public static RayTraceResult forwardsRaycast(Entity projectile, Vec3d motion, boolean scaleBlocks, boolean includeEntities, boolean ignoreExcludedEntity, @Nullable Entity excludedEntity) {
		World world = projectile.world;
		Vec3d vec3d = new Vec3d(projectile.posX, projectile.posY + projectile.height * 0.5f, projectile.posZ);
		Vec3d vec3d2 = vec3d.add(motion);
		AxisAlignedBB bigAABB = projectile.getEntityBoundingBox().expand(motion.x, motion.y, motion.z).grow(1.0d);
		RayTraceResult raytraceresult = null;
		double d0 = 0.0D;
		if (scaleBlocks) {
			BlockPos.PooledMutableBlockPos blockpos = BlockPos.PooledMutableBlockPos.retain();
			EnumFacing facing = null;
			for (AxisAlignedBB aabb : world.getCollisionBoxes(null, bigAABB)) {
				RayTraceResult result = aabb.grow(projectile.width * 0.5f, projectile.height * 0.5f, projectile.width * 0.5f).calculateIntercept(vec3d, vec3d2);
				if (result != null) {
	 				double d = projectile.getDistanceSq((aabb.minX + aabb.maxX) * 0.5d, (aabb.minY + aabb.maxY) * 0.5d, (aabb.minZ + aabb.maxZ) * 0.5d);
					if (d < d0 || d0 == 0.0D) {
						blockpos.setPos(aabb.minX, aabb.minY, aabb.minZ);
						facing = result.sideHit;
						d0 = d;
					}
				}
			}
			if (facing != null) {
				BlockPos pos = blockpos.toImmutable();
				raytraceresult = new RayTraceResult(new Vec3d(pos), facing, pos);
			}
			blockpos.release();
		} else {
			raytraceresult = world.rayTraceBlocks(vec3d, vec3d2, false, true, false);
		}
		if (includeEntities) {
			Entity entity = null;
			Vec3d hitvec = null;
			for (Entity entity1 : world.getEntitiesWithinAABBExcludingEntity(projectile, bigAABB)) {
				if (entity1.canBeCollidedWith() && (ignoreExcludedEntity || !entity1.equals(excludedEntity)) && !entity1.noClip) {
					AxisAlignedBB aabb = entity1.getEntityBoundingBox().grow(projectile.width * 0.5f, projectile.height * 0.5f, projectile.width * 0.5f);
					RayTraceResult result = aabb.calculateIntercept(vec3d, vec3d2);
					if (result != null) {
						double d = vec3d.distanceTo(result.hitVec);
						if (d < d0 || d0 == 0.0D) {
							entity = entity1;
							hitvec = result.hitVec;
							d0 = d;
						}
					}
				}
			}
			if (entity != null) {
				raytraceresult = new RayTraceResult(entity, hitvec);
			}
		}
		return raytraceresult;
	}

    public Vec3d getCenter(AxisAlignedBB bb) {
        return new Vec3d(bb.minX + (bb.maxX - bb.minX) * 0.5D, bb.minY + (bb.maxY - bb.minY) * 0.5D, bb.minZ + (bb.maxZ - bb.minZ) * 0.5D);
    }
}

