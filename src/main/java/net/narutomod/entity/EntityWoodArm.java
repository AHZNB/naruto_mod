
package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemMokuton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWoodArm extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 321;
	public static final int ENTITYID_RANGED = 322;

	public EntityWoodArm(ElementsNarutomodMod instance) {
		super(instance, 673);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class).id(new ResourceLocation("narutomod", "wood_arm"), ENTITYID)
				.name("wood_arm").tracker(64, 3, true).build());
	}

	public static class EC extends ItemMokuton.WoodSegment {
		private int lifespan = 200;
		private EC prevSegment;
		private Entity target;
		private double targetDistance;
		private double targetYOffset;
		private float targetYawOffset;
		private int reachedCount;

		public EC(World world) {
			super(world);
		}

		public EC(EntityLivingBase user, Entity targetIn) {
			this(user.world);
			this.setParent(user);
			this.setOffset(-0.4d, 1.2d, 0d, 0f, 90f);
			this.setPositionAndRotationFromParent(1f);
			this.lifespan = 200;
			this.prevSegment = this;
			this.target = targetIn;
		}

		public EC(EC segment, float yawOffset, float pitchOffset) {
			super(segment, yawOffset, pitchOffset);
			this.target = segment.target;
		}

		private void setLifespan(int ticks) {
			this.lifespan = ticks;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted == 1 && this.rand.nextFloat() < 0.5f) {
				this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:woodgrow"))),
				 1.0f, this.rand.nextFloat() * 0.4f + 0.6f);
			}
			Entity parent = this.getParent();
			if (parent != null && parent.isEntityAlive() && this.ticksExisted < this.lifespan) {
				if (!this.world.isRemote && this.getIndex() == 0 && this.ticksExisted > 1 && this.ticksExisted <= 50) {
					ProcedureUtils.Vec2f vec2f = new ProcedureUtils.Vec2f((this.rand.nextFloat()-0.5f) * 30f, (this.rand.nextFloat()-0.5f) * 30f);
					if (this.hasLivingTarget() && this.prevSegment.getIndex() > 10) {
						Vec3d vec = this.target.getPositionVector().addVector(0d, this.target.height/2, 0d);
						double d = this.prevSegment.getDistance(vec.x, vec.y, vec.z);
						float f1 = 1.0f / (this.target.width + (float)MathHelper.clamp(d, 1.0d, 4.4d));
						vec2f = ProcedureUtils.getYawPitchFromVec(vec.subtract(this.prevSegment.getPositionVector()))
						 .add(-this.prevSegment.rotationYaw, 90.0f - this.prevSegment.rotationPitch).scale(f1);
						if (d < (this.target.width + this.width) * 0.6f) {
							++this.reachedCount;
						}
						if (this.targetDistance == 0d && this.reachedCount >= MathHelper.ceil(this.target.width * 4f / this.width)) {
							vec = this.target.getPositionVector().subtract(parent.getPositionVector());
							this.targetDistance = vec.lengthVector();
							this.targetYOffset = vec.y;
							this.targetYawOffset = MathHelper.wrapDegrees(ProcedureUtils.getYawFromVec(vec.x, vec.z) - parent.rotationYaw);
						}
					}
					this.prevSegment = new EC(this.prevSegment, vec2f.x, vec2f.y);
					this.prevSegment.setLifespan(this.lifespan - this.ticksExisted * 2);
					this.world.spawnEntity(this.prevSegment);
				}
				if (this.ticksExisted == 1 && this.targetTargetable()
				 && this.getEntityBoundingBox().intersects(this.target.getEntityBoundingBox())) {
				 	this.target.getEntityData().setBoolean("TempData_disableKnockback", true);
					this.target.attackEntityFrom(ItemJutsu.causeJutsuDamage(this,
					 parent instanceof EntityLivingBase ? (EntityLivingBase)parent : null), 4.0f);
				}
				if (this.targetDistance != 0d && this.targetTargetable() && this.ticksExisted < this.lifespan * 4 / 5) {
					Vec3d vec = new Vec3d(0d, 0d, this.targetDistance)
					 .rotateYaw(-(parent.rotationYaw + this.targetYawOffset) * 0.017453292F)
					 .addVector(parent.posX, this.targetYOffset + parent.posY, parent.posZ);
					this.target.setPositionAndUpdate(vec.x, vec.y, vec.z);
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		private boolean hasLivingTarget() {
			return this.target != null && this.target.isEntityAlive();
		}

		private boolean targetTargetable() {
			return ItemJutsu.canTarget(this.target);
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 30d);
				if (res != null && res.entityHit != null) {
					entity.world.spawnEntity(new EC(entity, res.entityHit));
					return true;
				}
				return false;
			}
		}
	}
}
