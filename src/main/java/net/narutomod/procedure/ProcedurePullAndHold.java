package net.narutomod.procedure;

import net.minecraft.util.math.Vec3d;
//import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.Entity;
import javax.annotation.Nullable;

import net.narutomod.entity.EntityEarthBlocks;

public class ProcedurePullAndHold {
	private Entity grabbedEntity = null;

	public void execute(boolean is_pressed, Entity puller, @Nullable Entity target) {
		if (this.grabbedEntity == null && target == null)
			return;
		if (is_pressed) {
			if (this.grabbedEntity == null) {
				this.grabbedEntity = target;
			}
			if (this.grabbedEntity instanceof net.minecraft.entity.item.EntityItem
			 || this.grabbedEntity instanceof net.minecraft.entity.item.EntityXPOrb) {
				this.grabbedEntity.setPosition(puller.posX, puller.posY, puller.posZ);
			} else if (this.grabbedEntity instanceof EntityEarthBlocks.Base) {
				Vec3d vec3d = ProcedureUtils.raytraceBlocks(puller, 5d).hitVec;
				if (this.grabbedEntity.getDistance(vec3d.x, vec3d.y, vec3d.z) > 2.0D) {
					this.grabbedEntity.setNoGravity(true);
					ProcedureUtils.pullEntity(vec3d, this.grabbedEntity, 
					  2.5f / (float)this.grabbedEntity.getEntityBoundingBox().getAverageEdgeLength());
				} else {
					this.grabbedEntity.setPositionAndUpdate(vec3d.x, vec3d.y - 0.5D, vec3d.z);
				}
			} else {
				Vec3d vec3d = ProcedureUtils.raytraceBlocks(puller, 3d).hitVec;
				//this.grabbedEntity.motionY += 0.05D;
				this.grabbedEntity.setNoGravity(true);
				this.grabbedEntity.setPositionAndUpdate(vec3d.x, vec3d.y - this.grabbedEntity.height / 2.0F, vec3d.z);
			}
		} else if (this.grabbedEntity != null) {
			this.reset();
		}
	}

	public Entity getGrabbedEntity() {
		return this.grabbedEntity;
	}

	public void reset() {
		if (this.grabbedEntity != null) {
			this.grabbedEntity.setNoGravity(false);
			this.grabbedEntity = null;
		}
	}
}
