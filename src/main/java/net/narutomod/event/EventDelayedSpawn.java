package net.narutomod.event;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public class EventDelayedSpawn extends SpecialEvent {
	private Entity entityToSpawn;
	private double dx;
	private double dy;
	private double dz;

	public EventDelayedSpawn() {
		super();
	}

	public EventDelayedSpawn(World worldIn, Entity entityIn, double x, double y, double z, long timeToExecute) {
		super(EnumEventType.DELAYED_SPAWN, worldIn, entityIn, 0, 0, 0, timeToExecute);
		if (!worldIn.isRemote) {
			this.entityToSpawn = entityIn;
			this.dx = entityIn.posX + x;
			this.dy = entityIn.posY + y;
			this.dz = entityIn.posZ + z;
		}
	}

	@Override
	protected void onUpdate() {
		if (this.shouldExecute()) {
			super.onUpdate();
			if (this.entityToSpawn != null) {
				this.entityToSpawn.setPosition(this.dx, this.dy, this.dz);
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
		compound.setDouble("SpawnPosX", this.dx);
		compound.setDouble("SpawnPosY", this.dy);
		compound.setDouble("SpawnPosZ", this.dz);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.entityToSpawn = this.newEntityFromClassName(compound.getString("EntityClass"));
		if (this.entityToSpawn != null) {
			this.entityToSpawn.readFromNBT(compound);
			this.dx = compound.getDouble("SpawnPosX");
			this.dy = compound.getDouble("SpawnPosY");
			this.dz = compound.getDouble("SpawnPosZ");
		}
	}

	@Override
	public String toString() {
		return super.toString() + " {entityToSpawn:" + this.entityToSpawn.getClass().getName() + " at:(" + this.dx + ", " + this.dy + ", " + this.dz + ")}";
	}
}
