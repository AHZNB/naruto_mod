package net.narutomod;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.state.IBlockState;

import javax.vecmath.Vector3d;

import java.util.Random;

import net.narutomod.entity.EntityBijuManager;

@ElementsNarutomodMod.ModElement.Tag
public class SpawnTailedBeasts extends ElementsNarutomodMod.ModElement {
	private static final String SPAWN_TB_RULE = "spawnTailedBeasts";
	public SpawnTailedBeasts(ElementsNarutomodMod instance) {
		super(instance, 835);
	}
	private static final int SPAWN_MAX_RADIUS = 100000;
	private static final int SPAWN_MIN_RADIUS = 10000;
	private static final int REQUIRED_DISTANCE = 100;

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

		Random rand = new Random();
		for (EntityBijuManager bm : EntityBijuManager.getBMList()) {
			if (bm.isAddedToWorld(world) || bm.isSealed() || bm.getTails() == 10) {
				continue;
			}

			Vec3d spawnPos;

			if (bm.hasSpawnPos()) {
				spawnPos = bm.getSpawnPos();
			} else {
				final int x = (rand.nextInt(SPAWN_MAX_RADIUS - SPAWN_MIN_RADIUS) + SPAWN_MIN_RADIUS) + world.getSpawnPoint().getX();
				final int z = (rand.nextInt(SPAWN_MAX_RADIUS - SPAWN_MIN_RADIUS) + SPAWN_MIN_RADIUS) + world.getSpawnPoint().getY();

				spawnPos = new Vec3d(x, 0.0D, z);
				Biome biome = world.getBiome(new BlockPos(spawnPos.x, spawnPos.y, spawnPos.z));

				if (!bm.canSpawnInBiome(biome)) {
					continue;
				}

				// Find the highest non-air block
				Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(spawnPos.x, 0.0D, spawnPos.z));
				int height = chunk.getHeightValue((int) spawnPos.x & 15, (int) spawnPos.z & 15);
				for (int y = height; y >= 0; y--) {
					BlockPos currentPos = new BlockPos(spawnPos.x, y, spawnPos.z);
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

				bm.setSpawnPos(spawnPos.addVector(0.0D, height, 0.0D));
				//System.out.println(String.format("[SPAWNED TAILED BEAST] Tails: %s, Location: %s", bm.getTails(), spawnPos));
			}
			boolean playerInRange = false;

			for (EntityPlayer player : world.playerEntities) {
				double distanceSq = player.getDistanceSq(spawnPos.x, spawnPos.y, spawnPos.z);

				if (distanceSq < REQUIRED_DISTANCE) {
					playerInRange = true;
					break;
				}
			}
			if (playerInRange) {
				bm.spawnEntity(world, spawnPos.x, spawnPos.y, spawnPos.z, 0.0F);
			}
		}
	}
}
