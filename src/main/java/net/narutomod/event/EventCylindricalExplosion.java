package net.narutomod.event;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Random;
import net.minecraft.util.EnumParticleTypes;

public class EventCylindricalExplosion extends SpecialEvent {
	private int radius;
	private int height;
	private int tx;
	private int ty;
	private int tz;
	private int tr;
	private boolean mobGriefing;
	private final BlockPos.MutableBlockPos posList[] = {
			new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), 
			new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), 
			new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(),
			new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(),
			new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(),
			new BlockPos.MutableBlockPos()
	};

	public EventCylindricalExplosion() {
		super();
	}

	public EventCylindricalExplosion(World worldIn, Entity entityIn, int x, int yTop, int z, int yBottom, int radiusIn, long startTime) {
		super(EnumEventType.CYLINDRICAL_EXPLOSION, worldIn, entityIn, x, yBottom, z, startTime);
		if (!worldIn.isRemote) {
			this.mobGriefing = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, entityIn);
			this.radius = radiusIn;
			this.ty = yTop;
			this.height = this.ty - this.y0;
			//SpecialEvent.Save.getInstance().markDirty();
		}
	}

	@Override
	protected void onUpdate() {
		if (!this.shouldExecute())
			return;
		super.onUpdate();
		if (this.sound && this.tick % 10 == 0) {
			this.world.playSound(null, this.x0, this.y0, this.z0, (net.minecraft.util.SoundEvent) 
			  net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ground_charge")),
			  SoundCategory.NEUTRAL, 50.0F, this.rand.nextFloat() * 0.7f + 0.3f);
		}
		for (int i = 0; i < 1024;) {
			this.posList[0].setPos(this.x0 + this.tx, this.y0 + this.ty, this.z0 + this.tz);
			this.posList[1].setPos(this.x0 - this.tx, this.y0 + this.ty, this.z0 + this.tz);
			this.posList[2].setPos(this.x0 + this.tx, this.y0 + this.ty, this.z0 - this.tz);
			this.posList[3].setPos(this.x0 - this.tx, this.y0 + this.ty, this.z0 - this.tz);
			this.posList[4].setPos(this.x0 + this.tz, this.y0 + this.ty, this.z0 + this.tx);
			this.posList[5].setPos(this.x0 + this.tz, this.y0 + this.ty, this.z0 - this.tx);
			this.posList[6].setPos(this.x0 - this.tz, this.y0 + this.ty, this.z0 + this.tx);
			this.posList[7].setPos(this.x0 - this.tz, this.y0 + this.ty, this.z0 - this.tx);
			this.posList[8].setPos(this.x0 + this.tx - 1, this.y0 + this.ty, this.z0 + this.tz);
			this.posList[9].setPos(this.x0 - this.tx + 1, this.y0 + this.ty, this.z0 + this.tz);
			this.posList[10].setPos(this.x0 + this.tx - 1, this.y0 + this.ty, this.z0 - this.tz);
			this.posList[11].setPos(this.x0 - this.tx + 1, this.y0 + this.ty, this.z0 - this.tz);
			this.posList[12].setPos(this.x0 + this.tz, this.y0 + this.ty, this.z0 + this.tx - 1);
			this.posList[13].setPos(this.x0 + this.tz, this.y0 + this.ty, this.z0 - this.tx + 1);
			this.posList[14].setPos(this.x0 - this.tz, this.y0 + this.ty, this.z0 + this.tx - 1);
			this.posList[15].setPos(this.x0 - this.tz, this.y0 + this.ty, this.z0 - this.tx + 1);
			for (BlockPos pos : this.posList) {
				if (!this.world.isAirBlock(pos)) {
					if (this.mobGriefing)
						this.world.setBlockToAir(pos);
					if (this.particles) {
						((WorldServer) this.world).spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, pos.getX(), pos.getY(), pos.getZ(), 1,
								1.0D, 1.0D, 1.0D, 3.0D, new int[0]);
						((WorldServer) this.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY(), pos.getZ(), 1, 1.0D,
								1.0D, 1.0D, 0.0D, new int[0]);
					}
					i++;
				}
			}
			this.ty--;
			if (this.ty < this.y0) {
				this.ty = this.y0 + this.height;
				this.tz++;
				this.tx = (int) Math.round(Math.sqrt(this.tr * this.tr - this.tz * this.tz));
			}
			if (this.tz > (int) Math.round(this.tr / 1.41421356D)) {
				this.tz = 0;
				this.tx = ++this.tr;
			}
			if (this.tr > this.radius) {
				this.clear();
				return;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("radius", this.radius);
		compound.setInteger("height", this.height);
		compound.setInteger("tx", this.tx);
		compound.setInteger("ty", this.ty);
		compound.setInteger("tz", this.tz);
		compound.setInteger("tr", this.tr);
		compound.setBoolean("mobGriefing", this.mobGriefing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.radius = compound.getInteger("radius");
		this.height = compound.getInteger("height");
		this.tx = compound.getInteger("tx");
		this.ty = compound.getInteger("ty");
		this.tz = compound.getInteger("tz");
		this.tr = compound.getInteger("tr");
		this.mobGriefing = compound.getBoolean("mobGriefing");
	}

	@Override
	public String toString() {
		return super.toString() + " {radius:" + this.radius + ",height:" + this.height + ",mobGriefing:" + this.mobGriefing + "}";
	}
}
