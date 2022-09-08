package net.narutomod.event;

import net.narutomod.block.BlockMeteor;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Random;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayer;

public class EventMeteorShower extends SpecialEvent {
	private Entity entity;
	private int radius;
	private int strikeInterval;
	private int duration;
	private boolean allPlayers;

	public EventMeteorShower() {
		super();
	}

	public EventMeteorShower(World worldIn, int x, int y, int z, long startTime, int radiusIn, int interval, int durationIn) {
		super(EnumEventType.METEOR_SHOWER, worldIn, null, x, y, z, startTime);
		if (!worldIn.isRemote) {
			this.radius = radiusIn;
			this.strikeInterval = interval;
			this.duration = durationIn;
		}
	}

	public EventMeteorShower(World worldIn, Entity entityIn, long startTime, int radiusIn, int interval, int durationIn) {
		super(EnumEventType.METEOR_SHOWER, worldIn, entityIn, (int)entityIn.posX, (int)entityIn.posY, (int)entityIn.posZ, startTime);
		if (!worldIn.isRemote) {
			this.entity = entityIn;
			this.radius = radiusIn;
			this.strikeInterval = interval;
			this.duration = durationIn;
		}
	}

	public EventMeteorShower(World worldIn, long startTime, int radiusIn, int interval, int durationIn) {
		super(EnumEventType.METEOR_SHOWER, worldIn, null, 0, 0, 0, startTime);
		if (!worldIn.isRemote) {
			this.radius = radiusIn;
			this.strikeInterval = interval;
			this.duration = durationIn;
			this.allPlayers = true;
		}
	}

	@Override
	protected void onUpdate() {
		if (!this.shouldExecute())
			return;

		super.onUpdate();

		if (this.world.getTotalWorldTime() > this.startTime + this.duration) {
			this.clear();
			return;
		}
		if (this.tick % (this.strikeInterval + this.rand.nextInt(3) - 1) == 0) {
			if (this.allPlayers && this.world.playerEntities.size() > 0) {
				this.entity = this.world.playerEntities.get(this.rand.nextInt(this.world.playerEntities.size()));
			} else if (this.entityUuid != null && this.entity == null) {
				this.entity = this.getEntity();
			}
			int x = (this.entity != null ? (int)this.entity.posX : this.x0) + this.rand.nextInt(this.radius * 2) - this.radius;
			int z = (this.entity != null ? (int)this.entity.posZ : this.z0) + this.rand.nextInt(this.radius * 2) - this.radius;
			this.world.setBlockState(new BlockPos(x, 250, z), BlockMeteor.block.getDefaultState(), 3);
//System.out.println("meteor:(" + x + "," + z + "), "+this.entity+", "+super.toString());
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("radius", this.radius);
		compound.setInteger("strikeInterval", this.strikeInterval);
		compound.setInteger("duration", this.duration);
		compound.setBoolean("allPlayers", this.allPlayers);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.radius = compound.getInteger("radius");
		this.strikeInterval = compound.getInteger("strikeInterval");
		this.duration = compound.getInteger("duration");
		this.allPlayers = compound.getBoolean("allPlayers");
	}

	@Override
	public String toString() {
		return super.toString() + " {radius:" + this.radius + ",strikeInterval:" + this.strikeInterval + ",duration:" + this.duration + "}";
	}
}
