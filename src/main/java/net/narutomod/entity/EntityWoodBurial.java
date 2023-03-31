
package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemMokuton;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWoodBurial extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 327;
	public static final int ENTITYID_RANGED = 328;

	public EntityWoodBurial(ElementsNarutomodMod instance) {
		super(instance, 679);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "wood_burial"), ENTITYID).name("wood_burial").tracker(64, 3, true).build());
	}

	public static class EC extends ItemMokuton.WoodSegment {
		private int lifespan = 300;
		private EC prevSegment;
		private Entity target;
		private Vec3d targetVec;

		public EC(World world) {
			super(world);
		}

		public EC(Entity targetIn) {
			this(targetIn.world);
			this.setParent(this);
			this.setLocationAndAngles(targetIn.posX, targetIn.posY-0.5d, targetIn.posZ, 0f, 0f);
			this.setPositionAndRotationFromParent(1f);
			this.prevSegment = this;
			this.target = targetIn;
			this.targetVec = targetIn.getPositionVector();
		}

		public EC(EC segment, float yawOffset, float pitchOffset) {
			super(segment, yawOffset, pitchOffset);
			this.target = segment.target;
			this.targetVec = segment.targetVec;
		}

		public EC(EC segment, double offsetX, double offsetY, double offsetZ, float yawOffset, float pitchOffset) {
			super(segment, offsetX, offsetY, offsetZ, yawOffset, pitchOffset);
			this.target = segment.target;
			this.targetVec = segment.targetVec;
		}

		private void setLifespan(int ticks) {
			this.lifespan = ticks;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted == 1 && this.rand.nextFloat() < 0.05f) {
				this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:woodgrow"))),
				 1.0f, this.rand.nextFloat() * 0.4f + 0.6f);
			}
			if (this.getParent() != null && this.ticksExisted < this.lifespan) {
				if (!this.world.isRemote && this.getIndex() == 0 && this.ticksExisted == 1 && this.hasLivingTarget()) {
					for (int i = 0; i < (int)MathHelper.clamp(this.target.width * 5f, 6f, 22f); i++) {
						Vec3d vec = new Vec3d((this.rand.nextDouble()-0.5d) * this.target.width * 2.5d, 0d, (this.rand.nextDouble()-0.5d) * this.target.width * 2.5d);
						float f = ProcedureUtils.getYawFromVec(this.targetVec.subtract(this.getPositionVector().add(vec)));
						EC segment = new EC(this, vec.x, vec.y, vec.z, f + ((this.rand.nextFloat()-0.5f) * 160f), 80f);
						segment.setLifespan(this.lifespan - this.ticksExisted * 2);
						segment.prevSegment = segment;
						this.world.spawnEntity(segment);
					}
				}
				if (!this.world.isRemote && this.getIndex() == 1 && this.ticksExisted > 1 && this.ticksExisted <= 50) {
					float yaw = (this.rand.nextFloat()-0.5f) * 30f;
					int i = this.prevSegment.getIndex();
					if (this.hasLivingTarget() && i > 1) {
						yaw = MathHelper.wrapDegrees(ProcedureUtils.getYawFromVec(this.targetVec
						 .subtract(this.prevSegment.getPositionVector())) - this.prevSegment.rotationYaw);
						yaw /= this.target.width + Math.max(4.4f - (float)i * 0.075f, 1f);
					}
					this.prevSegment = new EC(this.prevSegment, yaw, -0.5f);
					this.prevSegment.setLifespan(this.lifespan - this.ticksExisted * 2);
					this.world.spawnEntity(this.prevSegment);
				}
				if (!this.world.isRemote && this.getIndex() > 48 && this.ticksExisted < 5) {
					BlockPos pos = new BlockPos(this);
					for (; !this.world.isAirBlock(pos); pos = pos.offset(EnumFacing.random(this.rand), this.rand.nextInt(2)));
					new net.narutomod.event.EventSetBlocks(this.world,
					 ImmutableMap.of(pos, Blocks.LEAVES.getStateFromMeta(0)), 0, this.lifespan - this.ticksExisted, false, false);
				}
				if (this.targetVec != null && this.targetTargetable()) {
					if (this.ticksExisted > 50) {
						this.target.attackEntityFrom(DamageSource.IN_WALL, 10.0f);
					}
					this.target.setPositionAndUpdate(this.targetVec.x, this.targetVec.y, this.targetVec.z);
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		private boolean hasLivingTarget() {
			return this.target != null && this.target.isEntityAlive();
		}

		private boolean targetTargetable() {
			if (!ItemJutsu.canTarget(this.target)) {
				this.target = null;
				return false;
			}
			return true;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 20d, 1.6d, false, false, new Predicate<Entity>() {
					public boolean apply(@Nullable Entity p_apply_1_) {
						return p_apply_1_ instanceof EntityLivingBase;
					}
				});
				if (res != null && res.entityHit != null) {
					entity.world.spawnEntity(new EC(res.entityHit));
					((ItemJutsu.Base)stack.getItem()).setCurrentJutsuCooldown(stack, 300);
					return true;
				}
				return false;
			}
		}
	}
}
