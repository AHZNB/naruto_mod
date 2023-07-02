package net.narutomod.event;

import net.minecraftforge.fml.common.FMLCommonHandler;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import net.narutomod.NarutomodMod;
import net.narutomod.SaveData;

public abstract class SpecialEvent {
	private static final Map<Integer, Class<? extends SpecialEvent>> REGISTERED_EVENTS = 
		ImmutableMap.<Integer, Class<? extends SpecialEvent>>builder()
		.put(EnumEventType.CYLINDRICAL_EXPLOSION.getIndex(), EventCylindricalExplosion.class)
		.put(EnumEventType.SPHERICAL_EXPLOSION.getIndex(), EventSphericalExplosion.class)
		.put(EnumEventType.DELAYED_SPAWN.getIndex(), EventDelayedSpawn.class)
		.put(EnumEventType.VILLAGE_SIEGE.getIndex(), EventVillageSiege.class)
		//.put(EnumEventType.METEOR_SHOWER.getIndex(), EventMeteorShower.class)
		.put(EnumEventType.SET_BLOCKS.getIndex(), EventSetBlocks.class)
		.put(EnumEventType.VANILLA_EXPLOSION.getIndex(), EventVanillaExplosion.class)
		.put(EnumEventType.DELAYED_CALLBACK.getIndex(), EventDelayedCallback.class)
		.build();
	
	private static final Map<Integer, SpecialEvent> eventsMap = Maps.<Integer, SpecialEvent>newHashMap();
	protected static final Random rand = new Random();

	public static void setMassExplosionEvent(World worldIn, int x, int yt, int z, int yb, int r) {
		if (!worldIn.isRemote && r > 0) {
			new EventCylindricalExplosion(worldIn, null, x, yt, z, yb, r, 0);
		}
	}

	public static void setSphericalExplosionEvent(World worldIn, int x, int y, int z, int r, Entity excludeEntity) {
		if (!worldIn.isRemote && r > 0) {
			new EventSphericalExplosion(worldIn, excludeEntity, x, y, z, r, 0);
		}
	}

	public static void setSphericalExplosionEvent(World worldIn, int x, int y, int z, int r, Entity excludeEntity, float fireChance) {
		if (!worldIn.isRemote && r > 0) {
			new EventSphericalExplosion(worldIn, excludeEntity, x, y, z, r, 0, fireChance);
		}
	}

	public static void setDelayedSpawnEvent(World worldIn, Entity entityIn, int xOffset, int yOffset, int zOffset, long timeToSpawn) {
		if (!worldIn.isRemote) {
			new EventDelayedSpawn(worldIn, entityIn, xOffset, yOffset, zOffset, timeToSpawn);
		}
	}

	public static void setVillageSiegeEvent(World worldIn, int centerX, int centerY, int centerZ, long startTime, int radius,
			Entity mob, int spawnInterval) {
		if (!worldIn.isRemote) {
			new EventVillageSiege(worldIn, mob, centerX, centerY, centerZ, startTime, radius, spawnInterval);
		}
	}
	
	protected EnumEventType type = EnumEventType.NO_EVENT;
	protected int id;
	protected World world = null;
	protected UUID entityUuid = null;
	protected long startTime = 0;
	protected int tick = 0;
	protected int x0;
	protected int y0;
	protected int z0;
	protected boolean particles;
	protected boolean sound;
	private boolean clear;
		
	public SpecialEvent() {
	}

	public SpecialEvent(EnumEventType typeIn, World worldIn, Entity entityIn, int x, int y, int z, long timeToExecute) {
		this(typeIn, worldIn, entityIn, x, y, z, timeToExecute, true, true);
	}

	public SpecialEvent(EnumEventType typeIn, World worldIn, Entity entityIn, int x, int y, int z, long timeToExecute, boolean particles, boolean sounds) {
		if (!worldIn.isRemote) {
			this.type = typeIn;
			this.id = rand.nextInt();
			this.particles = particles;
			this.sound = sounds;
			this.world = worldIn;
			if (entityIn != null && entityIn.isAddedToWorld())
				this.entityUuid = entityIn.getUniqueID();
			this.x0 = x;
			this.y0 = y;
			this.z0 = z;
			this.startTime = timeToExecute < this.world.getTotalWorldTime() ? this.world.getTotalWorldTime() : timeToExecute;
			//eventsList.add(this);
			eventsMap.put(this.id, this);
			Save.getInstance().markDirty();
		}
	}

	public void clear() {
		this.clear = true;
	}

	public boolean isCleared() {
		return this.clear;
	}

	public int getID() {
		return this.id;
	}

	public static SpecialEvent getEventFromId(int id) {
		return eventsMap.get(id);
	}

	public World getWorld() {
		return this.world;
	}

	public int getX0() {
		return this.x0;
	}

	public int getY0() {
		return this.y0;
	}

	public int getZ0() {
		return this.z0;
	}

	public Entity getEntity() {
		return ((WorldServer) this.world).getEntityFromUuid(this.entityUuid);
	}

	protected boolean shouldExecute() {
		return this.world != null && this.world.getTotalWorldTime() >= this.startTime;
	}

	protected void onUpdate() {
		if (this.type == EnumEventType.NO_EVENT) {
			this.clear();
			return;
		}
		this.tick++;
	}

	protected void doOnTick(int currentTick) {
	}

	protected Entity newEntityFromClassName(String name) {
		try {
			Constructor constructor = Class.forName(name).getConstructor(World.class);
			Object newobj = constructor.newInstance(this.world);
			if (newobj instanceof Entity)
				return (Entity) newobj;
		} catch (Exception e) {
			System.err.println("Entity class "+name+" not found or contructor(World) does not exist.");
		}
		return null;
	}

	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger("Type", this.type.getIndex());
		compound.setInteger("ID", this.id);
		compound.setInteger("World", this.world.provider.getDimension());
		if (this.entityUuid != null) {
			compound.setUniqueId("EntityUUID", this.entityUuid);
		}
		compound.setLong("Start", this.startTime);
		compound.setInteger("Tick", this.tick);
		compound.setInteger("x0", this.x0);
		compound.setInteger("y0", this.y0);
		compound.setInteger("z0", this.z0);
		compound.setBoolean("Sound", this.sound);
		compound.setBoolean("Particles", this.particles);
	}

	public void readFromNBT(NBTTagCompound compound) {
		this.type = EnumEventType.getTypeFromIndex(compound.getInteger("Type"));
		this.id = compound.getInteger("ID");
		//this.world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(compound.getInteger("World"));
		this.world = net.minecraftforge.common.DimensionManager.getWorld(compound.getInteger("World"));
		if (this.world != null) {
			if (compound.hasUniqueId("EntityUUID")) {
				this.entityUuid = compound.getUniqueId("EntityUUID");
			}
			this.startTime = compound.getLong("Start");
			this.tick = compound.getInteger("Tick");
			this.x0 = compound.getInteger("x0");
			this.y0 = compound.getInteger("y0");
			this.z0 = compound.getInteger("z0");
			this.sound = compound.getBoolean("Sound");
			this.particles = compound.getBoolean("Particles");
		} else {
			this.clear();
		}
	}

	public static void writeEventsToNBT(NBTTagCompound compound) {
		NBTTagList nbttaglist = new NBTTagList();
		for (SpecialEvent event : eventsMap.values()) {
			if (event != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				event.writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		if (!nbttaglist.hasNoTags()) {
			compound.setTag("SpecialEvents", nbttaglist);
		}
	}

	public static void readEventsFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("SpecialEvents", 9)) {
			NBTTagList nbttaglist = compound.getTagList("SpecialEvents", 10);
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				int type = nbttagcompound.getInteger("Type");
				try {
					SpecialEvent event = REGISTERED_EVENTS.get(type).getConstructor().newInstance();
					event.readFromNBT(nbttagcompound);
					eventsMap.put(event.id, event);
				} catch (Exception e) {
					throw new RuntimeException("Unregistered special event type " + type, e);
				}
			}
		}
//for (SpecialEvent evt : eventsMap.values())
//	System.out.println("===== end list: " + evt.toString());
	}

	public static void executeEvents() {
		Iterator<SpecialEvent> iter = eventsMap.values().iterator();
		while (iter.hasNext()) {
			SpecialEvent event = iter.next();
			if (event != null) {
				if (event.clear) {
					iter.remove();
					Save.getInstance().markDirty();
				} else {
//System.out.println("executing event " + event.type + "/" + event.id);
					event.onUpdate();
				}
			}
		}
	}

	public String toString() {
		return "Event:{Type:"+this.type+",ID:"+this.id+",Dim:"+(this.world!=null?this.world.provider.getDimension():"n")
		      +",EntityUUID:"+(this.entityUuid!=null?this.entityUuid.toString():"none")
		      +",startTime:"+this.startTime+",worldTime:"+(this.world!=null?this.world.getTotalWorldTime():"n")
		      +",at:("+this.x0+","+this.y0+","+this.z0+")"
		      +"}";
	}

	public static class Save extends WorldSavedData implements SaveData.ISaveData {
		private static final String DATA_NAME = NarutomodMod.MODID + "_specialevents";
		private static Save instance = null;
	
		public Save() {
			super(DATA_NAME);
		}
	
		public Save(String name) {
			super(name);
			instance = this;
		}
	
		public Save loadData() {
			return getInstance();
		}

		public void resetData() {
			eventsMap.clear();
			instance = null;
		}
	
		public static Save getInstance() {
			if (instance == null) {
				MapStorage storage = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
				instance = (Save) storage.getOrLoadData(Save.class, DATA_NAME);
				if (instance == null) {
					instance = new Save();
					storage.setData(DATA_NAME, instance);
//System.out.println("no save data, create new");
				}
//else System.out.println("got save data ourselves...");
			}
			return instance;
		}
	
		@Override
		public void readFromNBT(NBTTagCompound compound) {
			SpecialEvent.readEventsFromNBT(compound);
		}
	
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			SpecialEvent.writeEventsToNBT(compound);
			return compound;
		}
	}
}
