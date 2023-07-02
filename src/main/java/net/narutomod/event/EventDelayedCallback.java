package net.narutomod.event;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;

public class EventDelayedCallback extends SpecialEvent {
	private Callback callback;

	public EventDelayedCallback() {
		super();
	}

	public EventDelayedCallback(World worldIn, int x, int y, int z, long startTime, Callback callbackIn) {
		this(worldIn, x, y, z, null, startTime, callbackIn);
	}

	public EventDelayedCallback(World worldIn, int x, int y, int z, @Nullable Entity excludeEntity, long startTime, Callback callbackIn) {
		super(EnumEventType.DELAYED_CALLBACK, worldIn, excludeEntity, x, y, z, startTime, false, false);
		if (!worldIn.isRemote) {
			this.callback = callbackIn;
		}
	}

	@Override
	protected void onUpdate() {
		if (!this.shouldExecute())
			return;

		super.onUpdate();
		this.callback.execute(this.world, this.x0, this.y0, this.z0, this.getEntity());
		this.clear();
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("callbackID", this.callback.getId());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int i = compound.getInteger("callbackID");
		this.callback = Callback.getCallbackFromId(i);
		if (this.callback == null) {
			throw new IllegalArgumentException("Callback ID "+i+" not registered!");
		}
	}

	public static abstract class Callback {
		private static final Map<Integer, Callback> cbMap = Maps.newHashMap();
		private final int id;

		@Nullable
		public static Callback getCallbackFromId(int i) {
			return cbMap.get(Integer.valueOf(i));
		}
		
		public Callback(int callbackId) {
			if (cbMap.containsKey(Integer.valueOf(callbackId))) {
				throw new IllegalArgumentException("Callback ID already exists!");
			} else {
				this.id = callbackId;
				cbMap.put(Integer.valueOf(this.id), this);
			}
		}

		public int getId() {
			return this.id;
		}
		
		public abstract void execute(World world, int x, int y, int z, @Nullable Entity excludeEntity);
	}
}
