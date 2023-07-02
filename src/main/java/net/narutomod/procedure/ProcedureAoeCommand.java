package net.narutomod.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.base.Predicates;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import java.util.Iterator;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAoeCommand extends ElementsNarutomodMod.ModElement {
	private static World world;
	private static double centerX;
	private static double centerY;
	private static double centerZ;
	private static AxisAlignedBB aabb;
	private static double minRange;
	private static double maxRange;
	//private static List<Entity> excludedEntities = Lists.newArrayList();
	private static List<Entity> entitiesList = Lists.newArrayList();
	private static ProcedureAoeCommand Instance;
	/*private static final Predicate<Entity> MIN_DISTANCE = Predicates.and(EntitySelectors.NOT_SPECTATING, (p) -> {
		return p.isEntityAlive() && !p.getEntityData().getBoolean("kamui_intangible")
			&& p.getDistanceSq(centerX, centerY, centerZ) >= minRange*minRange;
			//&& !excludedEntities.contains(p);*/
	private static final Predicate<Entity> MIN_DISTANCE = (p) -> {
		return ItemJutsu.canTarget(p) && p.getDistanceSq(centerX, centerY, centerZ) >= minRange*minRange;
	};
	
	public ProcedureAoeCommand(ElementsNarutomodMod instance) {
		super(instance, 169);
		Instance = this;
	}

	public static ProcedureAoeCommand set(World worldIn, double x, double y, double z, double minR, double maxR) {
		world = worldIn;
		centerX = x;
		centerY = y;
		centerZ = z;
		aabb = new AxisAlignedBB(x - 0.5D, y, z - 0.5D, x + 0.5D, y + 1.0D, z + 0.5D);
		minRange = minR;
		maxRange = maxR;
		//excludedEntities.clear();
		if (minRange < 0.0D)
			minRange = 0.0D;
		entitiesList = world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		return Instance;
	}

	public static ProcedureAoeCommand set(Entity entity, double minR, double maxR) {
		//set(entity.world, entity.posX, entity.posY, entity.posZ, minR, maxR);
		world = entity.world;
		centerX = entity.posX;
		centerY = entity.posY;
		centerZ = entity.posZ;
		aabb = entity.getEntityBoundingBox();
		minRange = minR;
		maxRange = maxR;
		//excludedEntities.clear();
		if (minRange < 0.0D)
			minRange = 0.0D;
		entitiesList = world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		return Instance;
	}

	public ProcedureAoeCommand exclude(Entity entity) {
		if (entity != null) {
			//excludedEntities.add(entity);
			entitiesList.remove(entity);
		}
		return this;
	}

	public ProcedureAoeCommand exclude(Class<? extends Entity> clazz) {
		if (!entitiesList.isEmpty()) {
			Iterator<Entity> iter = entitiesList.iterator();
			while (iter.hasNext()) {
				Entity entity = iter.next();
				if (clazz.isAssignableFrom(entity.getClass())) {
					iter.remove();
				}
			}
		}
		return this;
	}

	public ProcedureAoeCommand health(float health) {
		//List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase)entity).setHealth(health);
			}
		return this;
	}

	public ProcedureAoeCommand clear() {
		//List<EntityPlayer> list = this.world.getEntitiesWithinAABB(EntityPlayer.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				//if (entity.getDistance(centerX, centerY, centerZ) < minRange || excludedEntities.contains(entity))
				//	continue;
				if (entity instanceof EntityPlayer)
					((EntityPlayer)entity).inventory.clear();
			}
		return this;
	}

	public ProcedureAoeCommand kill() {
		return kill(false);
	}

	public ProcedureAoeCommand kill(boolean livingOnly) {
		//List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (livingOnly && !(entity instanceof EntityLivingBase))
					continue;
				entity.onKillCommand();
			}
		return this;
	}

	public ProcedureAoeCommand kill(Class<? extends Entity> entityClass) {
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (entityClass.isAssignableFrom(entity.getClass())) {
					entity.onKillCommand();
				}
			}
		return this;
	}

	public ProcedureAoeCommand killNonLiving() {
		//List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (entity instanceof EntityLivingBase)
					continue;
				entity.onKillCommand();
			}
		return this;
	}

	public ProcedureAoeCommand resetHurtResistanceTime() {
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				entity.hurtResistantTime = 10;
			}
		return this;
	}

	public ProcedureAoeCommand damageEntities(DamageSource source, float amount) {
		//List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				boolean bool = entity.attackEntityFrom(source, amount);
			}
		return this;
	}

	public ProcedureAoeCommand damageEntitiesCentered(DamageSource source, float maxAmount) {
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				double d = 1.0d - entity.getDistance(centerX, centerY, centerZ) / maxRange;
				boolean bool = entity.attackEntityFrom(source, maxAmount * (float)d);
			}
		return this;
	}

	public ProcedureAoeCommand damageEntities(@Nullable Entity caster, float amount) {
		if (caster instanceof EntityPlayer)
			return damageEntities(DamageSource.causePlayerDamage((EntityPlayer) caster), amount);
		if (caster instanceof EntityLivingBase)
			return damageEntities(DamageSource.causeMobDamage((EntityLivingBase) caster), amount);
		if (caster instanceof EntityFireball)
			return damageEntities((new EntityDamageSourceIndirect("fireball", caster, ((EntityFireball) caster).shootingEntity))
					.setDamageBypassesArmor().setFireDamage(), amount);
		return damageEntities(DamageSource.GENERIC, amount);
	}

	public ProcedureAoeCommand removeEntity() {
		//List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (!(entity instanceof EntityPlayer))
					this.world.removeEntity(entity);
			}
		return this;
	}

	public ProcedureAoeCommand tpDimension(int dimid) {
		return tpDimension(dimid, Entity.class);
	}

	public ProcedureAoeCommand tpDimension(int dimid, Class <? extends Entity> entityclassIn) {
		//List<Entity> list = this.world.getEntitiesWithinAABB(entityclassIn, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (entityclassIn.isAssignableFrom(entity.getClass()))
					ProcedureKamuiTeleportEntity.eEntity(entity, (int)entity.posX, (int)entity.posZ, dimid);
			}
		return this;
	}

	public ProcedureAoeCommand noGravity(boolean noGravity) {
		//List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				entity.setNoGravity(noGravity);
			}
		return this;
	}

	public ProcedureAoeCommand motion(double motionX, double motionY, double motionZ) {
		//List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (motionX == 0d && motionY == 0d && motionZ == 0d && entity instanceof EntityPlayer) {
					((EntityPlayer)entity).capabilities.isFlying = false;
					((EntityPlayer)entity).sendPlayerAbilities();
				}
				ProcedureUtils.setVelocity(entity, motionX, motionY, motionZ);
			}
		return this;
	}

	public ProcedureAoeCommand knockback(float multiplier) {
		//List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty()) {
			for (Entity entity : entitiesList) {
				//entity.onGround = false;
				//entity.setNoGravity(true);
				ProcedureUtils.pushEntity(new Vec3d(centerX, centerY, centerZ), entity, maxRange, multiplier);
			}
			//for (Entity entity : list)
			//	entity.setNoGravity(false);
		}
		return this;
	}

	public ProcedureAoeCommand effect(Potion potion, int duration, int amplifier) {
		//List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase)entity).addPotionEffect(new PotionEffect(potion, duration * 20, amplifier, false, true));
			}
		return this;
	}

	public ProcedureAoeCommand clearEffects() {
		//List<EntityPlayer> list = this.world.getEntitiesWithinAABB(EntityPlayer.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (entity instanceof EntityPlayer)
					((EntityPlayer)entity).clearActivePotions();
			}
		return this;
	}

	public ProcedureAoeCommand setTag(String key, boolean set) {
		//List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				entity.getEntityData().setBoolean(key, set);
			}
		return this;
	}

	public ProcedureAoeCommand setTag(String key, int set) {
		//List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				((EntityLivingBase)entity).getEntityData().setInteger(key, set);
			}
		return this;
	}

	public ProcedureAoeCommand setFire(float chance) {
		int i1 = MathHelper.floor(this.centerX - this.maxRange);
		int i2 = MathHelper.ceil(this.centerX + this.maxRange);
		int j1 = MathHelper.floor(this.centerZ - this.maxRange);
		int j2 = MathHelper.ceil(this.centerZ + this.maxRange);
		int k1 = MathHelper.floor(this.centerY - this.maxRange);
		int k2 = MathHelper.ceil(this.centerY + this.maxRange);
		int i3 = MathHelper.floor(this.centerX - this.minRange);
		int i4 = MathHelper.ceil(this.centerX + this.minRange);
		int j3 = MathHelper.floor(this.centerZ - this.minRange);
		int j4 = MathHelper.ceil(this.centerZ + this.minRange);
		int k3 = MathHelper.floor(this.centerY - this.minRange);
		int k4 = MathHelper.ceil(this.centerY + this.minRange);
		BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
		for (int i = i1; i < i2; i++) {
			for (int j = j1; j < j2; j++) {
				for (int k = k1; k < k2; k++) {
					//if (i <= i3 || i >= i4 || j <= j3 || j >= j4 || k <= k3 || k >= k4) {
					//BlockPos pos = new BlockPos(i, k, j);
					double d = pos.setPos(i, k, j).distanceSqToCenter(this.centerX, this.centerY, this.centerZ);
					if (d <= this.maxRange * this.maxRange && d > this.minRange * this.minRange) {
						if (this.world.isAirBlock(pos) && this.world.getBlockState(pos.down()).isFullBlock() && Math.random() <= chance) {
							this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
						}
					}
				}
			}
		}
		pos.release();
		return this;
	}

	public ProcedureAoeCommand setFire(int seconds) {
		//List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				entity.setFire(seconds);
			}
		return this;
	}

	public ProcedureAoeCommand fear(int duration) {
		//List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb.grow(maxRange), MIN_DISTANCE);
		if (!entitiesList.isEmpty())
			for (Entity entity : entitiesList) {
				if (entity instanceof EntityLivingBase) {
					entity.getEntityData().setInteger("FearEffect", duration * 20);
					double dx = centerX - entity.posX;
					double dy = centerY - entity.posY;
					double dz = centerZ - entity.posZ;
					entity.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, ProcedureUtils.getYawFromVec(dx, dz),
							ProcedureUtils.getPitchFromVec(dx, dy, dz));
				}
			}
		return this;
	}
}
