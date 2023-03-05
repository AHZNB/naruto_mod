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

import net.narutomod.entity.EntityBijuManager;
import net.narutomod.entity.EntityTailedBeast;

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

			Vector3d spawnPos;

			if (bm.hasPosition()) {
				spawnPos = bm.getPosition();
			} else {
				final int x = (rand.nextInt(SPAWN_MAX_RADIUS - SPAWN_MIN_RADIUS) + SPAWN_MIN_RADIUS) + world.getSpawnPoint().getX();
				final int z = (rand.nextInt(SPAWN_MAX_RADIUS - SPAWN_MIN_RADIUS) + SPAWN_MIN_RADIUS) + world.getSpawnPoint().getZ();
				spawnPos = new Vector3d(x, 0.0D, z);
				Biome biome = world.getBiome(new BlockPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));

				if (!bm.canSpawn(biome)) {
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
				bm.setPosition(spawnPos);
				//System.out.println(String.format("[SPAWNED TAILED BEAST] Tails: %s, Location: %s", bm.getTails(), spawnPos));
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
				bm.spawnEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0F);
			}
		}
	}
}
