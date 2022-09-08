
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemDoton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEarthSandwich extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 177;
	public static final int ENTITYID_RANGED = 178;

	public EntityEarthSandwich(ElementsNarutomodMod instance) {
		super(instance, 443);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		  .id(new ResourceLocation("narutomod", "earth_sandwich"), ENTITYID).name("earth_sandwich").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private ItemDoton.EntityEarthWall[] wall = new ItemDoton.EntityEarthWall[2];
		private float[] orientation = new float[2];
		private EntityEarthBlocks.BlocksMoveHelper[] moveHelper = new EntityEarthBlocks.BlocksMoveHelper[2];
		private int moveTick;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase user, Entity target, double widthIn) {
			this(user.world);
			this.setPosition(target.posX, target.posY, target.posZ);
			float yaw = this.facing(user.rotationYaw);
			Vec3d[] vec1 = {target.getPositionVector().add(Vec3d.fromPitchYaw(0f, yaw - 90f).scale(widthIn)),
			                target.getPositionVector().add(Vec3d.fromPitchYaw(0f, yaw + 90f).scale(widthIn))};
			for (int i = 0; i < 2; i++) {
				this.wall[i] = new ItemDoton.EntityEarthWall(user.world, vec1[i].x, vec1[i].y, vec1[i].z,
				  yaw + 90f, widthIn, widthIn, widthIn * 0.6d, false);
				this.world.spawnEntity(this.wall[i]);
			}
			this.orientation[0] = yaw + 90f;
			this.orientation[1] = yaw - 90f;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				for (int i = 0; i < 2; i++) {
					if (this.wall[i] != null && !this.wall[i].isDead) {
						this.wall[i].setDead();
					}
					if (this.moveHelper[i] != null) {
						this.moveHelper[i].fall();
					}
				}
			}
		}

		private float facing(float yaw) {
			return MathHelper.wrapDegrees((float)EnumFacing.fromAngle(yaw).getHorizontalIndex() * 90f);
		}

		@Override
		public void onUpdate() {
			if (this.wall[0] != null && this.wall[1] != null) {
				if (this.wall[0].isDone() && this.wall[1].isDone()) {
					if (this.moveTick == 0) {
						this.moveTick = this.ticksExisted;
						for (int i = 0; i < 2; i++) {
							this.moveHelper[i] = new EntityEarthBlocks.BlocksMoveHelper(this.world, this.wall[i].getAllBlocks());
							Vec3d vec = Vec3d.fromPitchYaw(0f, this.orientation[i]).scale(0.15d);
							this.moveHelper[i].move(vec.x, vec.y, vec.z);
						}
					}
					if (this.moveTick > 0 && this.ticksExisted <= this.moveTick + 100) {
						for (int i = 0; i < 2; i++) {
							this.moveHelper[i].move(this.moveHelper[i].motionX, this.moveHelper[i].motionY, this.moveHelper[i].motionZ);
							if (this.moveHelper[i].collided) {
					            for (BlockPos pos : this.moveHelper[i].getCollidedBlocks()) {
						        	ProcedureUtils.breakBlockAndDropWithChance(this.world, pos, this.moveHelper[i].destroyHardness(), 
						           	  /*this.moveHelper[i].collisionForce() * 0.2f*/1f, 0.1f, false);
			           			}
							}
							for (Entity entity : this.moveHelper[i].getCollidedEntities()) {
								entity.attackEntityFrom(DamageSource.FALLING_BLOCK, this.moveHelper[i].collisionForce() * 4f);
							}
						}
					}
					if (this.moveTick > 0 && this.ticksExisted > this.moveTick + 100) {
						this.setDead();
					}
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean isInRangeToRenderDist(double distance) {
			double d = 68.5d * this.getRenderDistanceWeight();
			return distance < d * d;
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
				if (power >= 2f) {
					RayTraceResult rt = ProcedureUtils.objectEntityLookingAt(entity, 30d);
					if (rt != null && rt.entityHit != null) {
						if (power >= 8f) {
							entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) SoundEvent.REGISTRY
							 .getObject(new ResourceLocation(("narutomod:sando_no_jutsu"))), SoundCategory.NEUTRAL, 5, 1f);
						} else {
							entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) SoundEvent.REGISTRY
							 .getObject(new ResourceLocation(("narutomod:jutsu"))), SoundCategory.NEUTRAL, 1, 1f);
						}
						entity.world.spawnEntity(new EC(entity, rt.entityHit, (double)power));
						return true;
					}
				}
				return false;
			}
		}
	}
}
