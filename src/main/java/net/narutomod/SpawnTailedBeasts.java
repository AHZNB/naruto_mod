package net.narutomod;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.Biomes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;

import javax.vecmath.Vector3d;

import javax.annotation.Nonnull;

import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class SpawnTailedBeasts extends ElementsNarutomodMod.ModElement {
	private static final String SPAWN_TB_RULE = "spawnTailedBeasts";
	private static final HashMap<String, List<Biome>> TAILED_BEASTS = Maps.newHashMap();
	static {
		TAILED_BEASTS.put("one_tail", Arrays.asList(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.MESA));
		TAILED_BEASTS.put("two_tails", Arrays.asList(Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.SWAMPLAND));
		TAILED_BEASTS.put("three_tails", Arrays.asList(Biomes.OCEAN, Biomes.BEACH));
		TAILED_BEASTS.put("four_tails", Arrays.asList(Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS));
		TAILED_BEASTS.put("five_tails", Arrays.asList(Biomes.PLAINS, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU));
		TAILED_BEASTS.put("six_tails", Arrays.asList(Biomes.RIVER, Biomes.SWAMPLAND));
		TAILED_BEASTS.put("seven_tails",
				Arrays.asList(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.MUTATED_BIRCH_FOREST, Biomes.MUTATED_FOREST));
		TAILED_BEASTS.put("eight_tails", Arrays.asList(Biomes.OCEAN, Biomes.DEEP_OCEAN));
		TAILED_BEASTS.put("nine_tails", Arrays.asList(Biomes.FOREST, Biomes.FOREST_HILLS, Biomes.ROOFED_FOREST));
	}
	public SpawnTailedBeasts(ElementsNarutomodMod instance) {
		super(instance, 835);
	}
	private static final int SPAWN_MAX_RADIUS = 100000;
	private static final int SPAWN_MIN_RADIUS = 10000;
	private static final int REQUIRED_DISTANCE = 300;
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		if (!world.isRemote && !world.getGameRules().hasRule(SPAWN_TB_RULE)) {
			world.getGameRules().addGameRule(SPAWN_TB_RULE, "true", net.minecraft.world.GameRules.ValueType.BOOLEAN_VALUE);
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		final World world = event.world;
		if (world.isRemote) {
			return;
		}
		if (!world.getGameRules().getBoolean(SPAWN_TB_RULE)) {
			return;
		}
		TailedBeastSpawnData data = TailedBeastSpawnData.get(world);
		if (data.spawned.size() == TAILED_BEASTS.size()) {
			return;
		}
		Random rand = new Random();
		for (final Map.Entry<String, List<Biome>> entry : TAILED_BEASTS.entrySet()) {
			if (data.hasSpawned(entry.getKey())) {
				continue;
			}
			Vector3d spawnPos;
			if (data.hasPosition(entry.getKey())) {
				spawnPos = data.getPosition(entry.getKey());
			} else {
				final int x = (rand.nextInt(SPAWN_MAX_RADIUS - SPAWN_MIN_RADIUS) + SPAWN_MIN_RADIUS) + world.getSpawnPoint().getX();
				final int z = (rand.nextInt(SPAWN_MAX_RADIUS - SPAWN_MIN_RADIUS) + SPAWN_MIN_RADIUS) + world.getSpawnPoint().getZ();
				spawnPos = new Vector3d(x, 0.0D, z);
				Biome biome = world.getBiome(new BlockPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));
				if (!entry.getValue().contains(biome)) {
					continue;
				}
				// Find the highest non-air block
				Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(spawnPos.getX(), 0.0D, spawnPos.getZ()));
				int height = chunk.getHeightValue((int) spawnPos.getX() & 15, (int) spawnPos.getZ() & 15);
				for (int y = height; y >= 0; y--) {
					BlockPos currentPos = new BlockPos(spawnPos.getX(), y, spawnPos.getZ());
					IBlockState state = chunk.getBlockState(currentPos);
					if (!state.getBlock().isAir(state, world, currentPos)) {
						height = y;
						break;
					}
				}
				// Sometimes it's not possible :(
				if (height == -1) {
					continue;
				}
				spawnPos.setY(height);
				data.setPosition(entry.getKey(), spawnPos);
				data.markDirty();
				//System.out.printf("[SPAWNED TAILED BEAST] Key: %s, Location: %s%n", entry.getKey(), spawnPos);
			}
			boolean playerInRange = false;
			for (EntityPlayer player : world.playerEntities) {
				double distanceSq = player.getDistanceSq(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
				if (distanceSq < REQUIRED_DISTANCE) {
					playerInRange = true;
					break;
				}
			}
			if (playerInRange) {
				final ResourceLocation key = new ResourceLocation(NarutomodMod.MODID, entry.getKey());
				Entity entity = EntityList.createEntityByIDFromName(key, world);
				// Most likely will never happen
				if (entity == null) {
					continue;
				}
				entity.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
				world.spawnEntity(entity);
				data.setSpawned(entry.getKey());
			}
		}
	}
	public static class TailedBeastSpawnData extends WorldSavedData {
		private static final String DATA_NAME = "tailed_beast_spawn_data";
		private static final String POSITIONS_KEY = "positions";
		private static final String SPAWNED_KEY = "spawns";
		private final HashMap<String, Vector3d> positions = Maps.newHashMap();
		private final List<String> spawned = Lists.newArrayList();
		public TailedBeastSpawnData() {
			super(DATA_NAME);
		}

		public static TailedBeastSpawnData get(World world) {
			TailedBeastSpawnData data = (TailedBeastSpawnData) world.getPerWorldStorage().getOrLoadData(TailedBeastSpawnData.class, DATA_NAME);
			if (data == null) {
				data = new TailedBeastSpawnData();
				world.getPerWorldStorage().setData(DATA_NAME, data);
			}
			return data;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			NBTTagList positionsTag = nbt.getTagList(POSITIONS_KEY, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < positionsTag.tagCount(); i++) {
				NBTTagCompound positionTag = positionsTag.getCompoundTagAt(i);
				String tb = positionTag.getString("tb");
				double x = positionTag.getDouble("x");
				double y = positionTag.getDouble("y");
				double z = positionTag.getDouble("z");
				this.positions.put(tb, new Vector3d(x, y, z));
			}
			NBTTagList spawnedTag = nbt.getTagList(SPAWNED_KEY, Constants.NBT.TAG_STRING);
			for (int i = 0; i < spawnedTag.tagCount(); i++) {
				String tb = spawnedTag.getStringTagAt(i);
				this.spawned.add(tb);
			}
		}

		@Override
		@Nonnull
		public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
			NBTTagList positionsTag = new NBTTagList();
			for (Map.Entry<String, Vector3d> entry : this.positions.entrySet()) {
				NBTTagCompound positionTag = new NBTTagCompound();
				positionTag.setString("tb", entry.getKey());
				positionTag.setDouble("x", entry.getValue().getX());
				positionTag.setDouble("y", entry.getValue().getY());
				positionTag.setDouble("z", entry.getValue().getZ());
				positionsTag.appendTag(positionTag);
			}
			nbt.setTag(POSITIONS_KEY, positionsTag);
			NBTTagList spawnedTag = new NBTTagList();
			for (String tb : this.spawned) {
				spawnedTag.appendTag(new NBTTagString(tb));
			}
			nbt.setTag(SPAWNED_KEY, spawnedTag);
			return nbt;
		}

		public boolean hasPosition(String tb) {
			return this.positions.containsKey(tb);
		}

		public Vector3d getPosition(String tb) {
			return this.positions.get(tb);
		}

		public void setPosition(String tb, Vector3d pos) {
			this.positions.put(tb, pos);
		}

		public void setSpawned(String tb) {
			this.spawned.add(tb);
		}

		public boolean hasSpawned(String tb) {
			return this.spawned.contains(tb);
		}
	}
}
