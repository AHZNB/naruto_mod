package net.narutomod.event;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class EventDelayedSpawn extends SpecialEvent {
	private Entity entityToSpawn;

	public EventDelayedSpawn() {
		super();
	}

	public EventDelayedSpawn(World worldIn, Entity entityIn, int x, int y, int z, long timeToExecute) {
		super(EnumEventType.DELAYED_SPAWN, worldIn, entityIn, x, y, z, timeToExecute);
		if (!worldIn.isRemote) {
			this.entityToSpawn = entityIn;
			this.x0 += (int) entityIn.posX;
			this.y0 += (int) entityIn.posY;
			this.z0 += (int) entityIn.posZ;
		}
	}

	@Override
	protected void onUpdate() {
		if (this.shouldExecute()) {
			super.onUpdate();
			if (this.entityToSpawn != null) {
				this.entityToSpawn.setPosition(this.x0, this.y0, this.z0);
				this.world.spawnEntity(this.entityToSpawn);
			}
			this.clear();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setString("EntityClass", this.entityToSpawn.getClass().getName());
		this.entityToSpawn.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.entityToSpawn = this.newEntityFromClassName(compound.getString("EntityClass"));
		if (this.entityToSpawn != null) {
			this.entityToSpawn.readFromNBT(compound);
		}
	}

	@Override
	public String toString() {
		return super.toString() + " {entityToSpawn:" + this.entityToSpawn.getClass().getName() + "}";
	}
}
