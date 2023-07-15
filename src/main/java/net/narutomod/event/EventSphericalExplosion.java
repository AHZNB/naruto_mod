package net.narutomod.event;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureCameraShake;

public class EventSphericalExplosion extends SpecialEvent {
	private int radius;
	private int tx;
	private int ty;
	private int tz;
	private int tr;
	private boolean mobGriefing;
	private boolean useBlockExplosionResistance;
	private float fireChance;
	private final BlockPos.MutableBlockPos posList[] = {
		new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), 
		new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), 
		new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(),
		new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(),
		new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(),
		new BlockPos.MutableBlockPos()
	};

	public EventSphericalExplosion() {
		super();
	}

	public EventSphericalExplosion(World worldIn, Entity excludeEntity, int x, int y, int z, int radiusIn, long startTime) {
		this(worldIn, excludeEntity, x, y, z, radiusIn, startTime, 0f);
	}

	public EventSphericalExplosion(World worldIn, Entity excludeEntity, int x, int y, int z, int radiusIn, long startTime, float fireChanceIn) {
		this(worldIn, excludeEntity, x, y, z, radiusIn, startTime, fireChanceIn, true, true);
	}

	public EventSphericalExplosion(World worldIn, Entity excludeEntity, int x, int y, int z, int radiusIn, long startTime, float fireChanceIn, boolean particlesIn, boolean soundIn) {
		this(worldIn, excludeEntity, x, y, z, radiusIn, startTime, true, fireChanceIn, particlesIn, soundIn);
	}

	public EventSphericalExplosion(World worldIn, Entity excludeEntity, int x, int y, int z, int radiusIn, long startTime, boolean useResistance, float fireChanceIn, boolean particlesIn, boolean soundIn) {
		super(EnumEventType.SPHERICAL_EXPLOSION, worldIn, excludeEntity, x, y, z, startTime, particlesIn, soundIn);
		if (!worldIn.isRemote) {
			this.mobGriefing = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, excludeEntity);
			this.radius = radiusIn;
			this.ty = this.radius;
			this.useBlockExplosionResistance = useResistance;
			this.fireChance = fireChanceIn;
		}
	}

	public int getRadius() {
		return this.radius;
	}

	@Override
	protected void onUpdate() {
		if (!this.shouldExecute())
			return;
		super.onUpdate();
		this.doOnTick(this.tick);
		if (this.sound) {
			if (this.tick == 1) {
				this.world.playSound(null, this.x0, this.y0, this.z0, SoundEvents.ENTITY_GENERIC_EXPLODE,
				 SoundCategory.BLOCKS, 10.0F, this.rand.nextFloat() * 0.5F + 0.5F);
			} else if (this.tick % 40 == 10) {
				this.world.playSound(null, this.x0, this.y0, this.z0,
				 (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:explosion")),
				 SoundCategory.BLOCKS, Math.max(10.0F - this.tick/40, 1.0F), this.rand.nextFloat() * 0.5F + 0.5F);
			}
		}
		if (this.radius > 20) {
			float f = 1f - this.tr / this.radius;
			ProcedureCameraShake.sendToClients(this.world.provider.getDimension(), this.x0, this.y0, this.z0,
			 8f * MathHelper.sqrt(f) + this.tr, 80, 8f * f);
		}
		for (int i = 0; i < 1024; ) {
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
				IBlockState blockstate = this.world.getBlockState(pos);
				if (blockstate.getMaterial() != Material.AIR
				 && this.rand.nextFloat() <= 1.75f - pos.getDistance(this.x0, this.y0, this.z0) / this.radius) {
					if (this.mobGriefing && blockstate.getBlockHardness(this.world, pos) >= 0.0F) {
						float f = this.radius * (0.7F + this.rand.nextFloat() * 0.6F);
						float f1 = blockstate.getBlock().getExplosionResistance(null);
						f1 = this.useBlockExplosionResistance ? f1 : f1 >= 3600000.0f ? f1 : 0.0f;
						f -= (f1 + 0.3F) * 0.3F;
						if (f > 0.0f) {
							blockstate.getBlock().dropBlockAsItemWithChance(this.world, pos, blockstate, 0.5F / this.radius, 0);
							this.world.setBlockToAir(pos);
						}
					}
					if (this.particles) {
						((WorldServer)this.world).spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, pos.getX(), pos.getY(), pos.getZ(),
						 1, 1.0D, 1.0D, 1.0D, 3.0D, new int[0]);
						((WorldServer)this.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX(), pos.getY(), pos.getZ(),
						 1, 1.0D, 1.0D, 1.0D, 0.0D, new int[0]);
					}
					i++;
				}
			}
			int i1 = (int) Math.round(Math.sqrt(this.radius * this.radius - this.tr * this.tr));
			this.ty--;
			if (this.y0 + this.ty >= 255) {
				continue;
			}
			if (this.ty < -i1 || this.y0 + this.ty < 0) {
				this.ty = i1;
				this.tz++;
				this.tx = (int) Math.round(Math.sqrt(this.tr * this.tr - this.tz * this.tz));
			}
			if (this.tz > (int) Math.round(this.tr / 1.41421356D)) {
				this.tz = 0;
				this.tx = ++this.tr;
			}
			if (this.tr > this.radius) {
				if (this.mobGriefing && this.fireChance > 0.01f) {
					ProcedureAoeCommand.set(this.world, (double)this.x0, (double)this.y0, (double)this.z0, 0d, (double)this.radius*1.1d)
					  .setFire(this.fireChance);
				}
				this.clear();
				return;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("radius", this.radius);
		compound.setInteger("tx", this.tx);
		compound.setInteger("ty", this.ty);
		compound.setInteger("tz", this.tz);
		compound.setInteger("tr", this.tr);
		compound.setBoolean("mobGriefing", this.mobGriefing);
		compound.setBoolean("useBlockExplosionResistance", this.useBlockExplosionResistance);
		compound.setFloat("fireChance", this.fireChance);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.radius = compound.getInteger("radius");
		this.tx = compound.getInteger("tx");
		this.ty = compound.getInteger("ty");
		this.tz = compound.getInteger("tz");
		this.tr = compound.getInteger("tr");
		this.mobGriefing = compound.getBoolean("mobGriefing");
		this.useBlockExplosionResistance = compound.getBoolean("useBlockExplosionResistance");
		this.fireChance = compound.getFloat("fireChance");
	}

	@Override
	public String toString() {
		return super.toString() + " {radius:" + this.radius + ",mobGriefing:" + this.mobGriefing + ",fireChance:" + this.fireChance + "}";
	}
}
