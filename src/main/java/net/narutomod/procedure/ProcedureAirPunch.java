package net.narutomod.procedure;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.item.EntityFallingBlock;

import net.narutomod.Particles;

import java.util.Random;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;

public abstract class ProcedureAirPunch {
	private final List<RayTraceResult> affectedTraceList = Lists.newArrayList();
	protected Random rand = new Random();
	protected float blockDropChance = 0.1F;
	protected float blockHardnessLimit = 2f;
	public EnumParticleTypes particlesPre = EnumParticleTypes.EXPLOSION_LARGE;
	public EnumParticleTypes particlesDuring = EnumParticleTypes.EXPLOSION_NORMAL;
	private double range;
	private double radius;
	public static final Predicate<Entity> COLLIDABLE = new Predicate<Entity>() {
        public boolean apply(@Nullable Entity p_apply_1_) {
            return (!(p_apply_1_ instanceof EntityPlayer) || !((EntityPlayer)p_apply_1_).isSpectator())
              && p_apply_1_.canBeCollidedWith();
        }
    };

	public static int getPressDuration(Entity entity) {
		return entity.getEntityData().getInteger("pressDuration");
	}

	private static void setPressDuration(Entity entity, int duration) {
		entity.getEntityData().setInteger("pressDuration", duration);
	}

	public void execute(boolean is_pressed, EntityPlayer player) {
		int pressDuration = getPressDuration(player);
		if (is_pressed) {
			pressDuration++;
			player.sendStatusMessage(new TextComponentString("Power range: " + (int) this.getRange(pressDuration)), true);
			Particles.spawnParticle(player.world, Particles.Types.SMOKE, player.posX, player.posY, player.posZ, pressDuration, 0.25D, 0.0D, 0.25D,
					0.0D, 0.15D, 0.0D, 0x2000DDFF, 25, (int) (3.0D / (Math.random() * 0.8D + 0.2D)));
		} else if (pressDuration > 0) {
			execute(player, this.getRange(pressDuration), this.getFarRadius(pressDuration));
			pressDuration = 0;
		}
		setPressDuration(player, pressDuration);
	}

	public void execute(EntityLivingBase player, double range, double radius) {
		execute(player, range, radius, 0d, 1.5d);
	}

	public void execute2(EntityLivingBase player, double range, double radius) {
		execute(player, range, radius, radius, 0);
	}

	public void execute(EntityLivingBase player, double range, double radius, double radiusNear, double random) {
		World world = player.world;
		this.range = range;
		this.radius = radius;
		if (!world.isRemote) {
			this.preExecuteParticles(player);
			if (this.getAffectedInSight(player, range, radiusNear, radius, random)) {
				List<Entity> list = Lists.newArrayList();
				for (RayTraceResult result : this.affectedTraceList) {
					if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
						BlockPos pos = result.getBlockPos();
						EntityItem entityItem = this.processAffectedBlock(player, pos, result.sideHit);
						this.breakBlockParticles(world, pos);
						if (entityItem != null)
							list.add(entityItem);
					} else if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
						list.add(result.entityHit);
					}
				}
				for (Entity entity1 : list) {
					this.attackEntityFrom(player, entity1);
				}
			}
		}
	}

	protected void preExecuteParticles(EntityLivingBase player) {
		if (this.particlesPre != null) {
			for (int i = 1; i <= this.range; i++) {
				Vec3d vec3d = player.getLookVec().scale(i);
				double d = this.radius * i / this.range;
				((WorldServer) player.world).spawnParticle(this.particlesPre, player.posX + vec3d.x,
						player.posY + 1.2D + vec3d.y, player.posZ + vec3d.z, i, d, d, d, 0.1D);
			}
		}
	}

	protected void breakBlockParticles(World world, BlockPos pos) {
		if (this.particlesDuring != null) {
			((WorldServer)world).spawnParticle(this.particlesDuring, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
			 2, 0.2D, 0.2D, 0.2D, 0.0D);
		}
	}

	@Nullable
	protected EntityItem processAffectedBlock(EntityLivingBase player, BlockPos pos, EnumFacing facing) {
		return ProcedureUtils.breakBlockAndDropWithChance(player.world, pos, this.blockHardnessLimit, 
		  net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(player.world, player) 
		  ? this.getBreakChance(pos, player, this.range) : 0f, this.blockDropChance, false);
	}

	protected double getRange(int paramInt) {
		return this.range;
	}

	protected double getFarRadius(int paramInt) {
		return this.radius;
	}

	protected void attackEntityFrom(EntityLivingBase player, Entity target) {
		if (target.canBePushed()) {
			ProcedureUtils.pushEntity(player, target, this.range, 2.0F);
		}
	}

	protected abstract float getBreakChance(BlockPos paramBlockPos, EntityLivingBase paramEntityPlayer, double paramDouble);

	protected boolean getAffectedInSight(Entity entity, double range, double nearRadius, double farRadius, double randomness) {
		this.affectedTraceList.clear();
		if (entity == null || entity.world == null)
			return false;
		Vec3d vec3d = entity.getPositionEyes(1.0F).subtract(0.0D, 0.4D, 0.0D);
		Vec3d vec3d1 = entity.getLookVec().scale(range);
		Vec3d vec3d2 = vec3d.add(vec3d1);
		Vec3d vec3d3 = new Vec3d(Math.copySign(farRadius, vec3d1.x), Math.copySign(farRadius, vec3d1.y), Math.copySign(farRadius, vec3d1.z));
		AxisAlignedBB bigAABB = entity.getEntityBoundingBox()
		  .expand(vec3d1.x, vec3d1.y, vec3d1.z)
		  .expand(Math.copySign(Math.min(range - Math.abs(vec3d1.x), farRadius), vec3d1.x),
		          Math.copySign(Math.min(range - Math.abs(vec3d1.y), farRadius), vec3d1.y), 
		          Math.copySign(Math.min(range - Math.abs(vec3d1.z), farRadius), vec3d1.z))
		  .expand(farRadius > Math.abs(vec3d1.x) ? vec3d1.x - vec3d3.x : 0d,
		          farRadius > Math.abs(vec3d1.y) ? vec3d1.y - vec3d3.y : 0d,
		          farRadius > Math.abs(vec3d1.z) ? vec3d1.z - vec3d3.z : 0d);
		farRadius -= nearRadius;
		BlockPos.PooledMutableBlockPos mpos = BlockPos.PooledMutableBlockPos.retain();
		for (AxisAlignedBB aabb : ProcedureUtils.getBoundingBoxes(entity.world, bigAABB)) {
			double d = Math.sqrt(entity.getDistanceSqToCenter(mpos.setPos(aabb.minX, aabb.minY, aabb.minZ)));
			aabb = aabb.grow(nearRadius + (d / range * farRadius) + (d > 3D ? (this.rand.nextDouble() * randomness) : 0D));
			RayTraceResult raytraceresult = aabb.calculateIntercept(vec3d, vec3d2);
			if (raytraceresult != null)
				this.affectedTraceList.add(new RayTraceResult(raytraceresult.hitVec, raytraceresult.sideHit, mpos.toImmutable()));
		}
		mpos.release();
		for (Entity entity1 : entity.world.getEntitiesInAABBexcluding(entity, bigAABB, COLLIDABLE)) {
			if (!entity1.isRidingSameEntity(entity)) {
				AxisAlignedBB aabb = entity1.getEntityBoundingBox().grow(nearRadius + (entity1.getDistance(entity) / range * farRadius) + 1);
				RayTraceResult raytraceresult = aabb.calculateIntercept(vec3d, vec3d2);
				if (aabb.contains(vec3d) || raytraceresult != null)
					this.affectedTraceList.add(new RayTraceResult(entity1));
			}
		}
		//this.affectedTraceList.sort(new ProcedureUtils.RayTraceResultSorter(vec3d));
		return !this.affectedTraceList.isEmpty();
	}
}
