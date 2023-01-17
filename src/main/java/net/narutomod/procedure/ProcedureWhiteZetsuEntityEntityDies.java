package net.narutomod.procedure;

import net.narutomod.item.ItemWhiteZetsuFlesh;
import net.narutomod.item.ItemKunai;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.block.material.Material;

import java.util.Random;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureWhiteZetsuEntityEntityDies extends ElementsNarutomodMod.ModElement {
	public ProcedureWhiteZetsuEntityEntityDies(ElementsNarutomodMod instance) {
		super(instance, 230);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure WhiteZetsuEntityEntityDies!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure WhiteZetsuEntityEntityDies!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure WhiteZetsuEntityEntityDies!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure WhiteZetsuEntityEntityDies!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		double rand = 0;
		rand = (double) Math.random();
		if (((rand) <= 0.1)) {
			if (!world.isRemote) {
				EntityItem entityToSpawn = new EntityItem(world, x, y, z, new ItemStack(ItemWhiteZetsuFlesh.block, (int) (1)));
				entityToSpawn.setPickupDelay(10);
				world.spawnEntity(entityToSpawn);
			}
		}
		if (((rand) >= 0.6)) {
			if (!world.isRemote) {
				EntityItem entityToSpawn = new EntityItem(world, x, y, z, new ItemStack(ItemKunai.block, (int) (1)));
				entityToSpawn.setPickupDelay(10);
				world.spawnEntity(entityToSpawn);
			}
		}
		if (((!(world.isRemote)) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, null))) {
			if ((!((world.isAirBlock(new BlockPos((int) x, (int) (y - 1), (int) z)))
					|| ((world.getBlockState(new BlockPos((int) x, (int) (y - 1), (int) z))).getBlock() == Blocks.BEDROCK.getDefaultState()
							.getBlock())))) {
				if ((!((world.getBlockState(new BlockPos((int) x, (int) (y - 1), (int) z))).getMaterial() == Material.GRASS))) {
					world.setBlockState(new BlockPos((int) x, (int) (y - 1), (int) z), Blocks.GRASS.getDefaultState(), 3);
				}
				Random random = new Random();
				WorldGenAbstractTree worldgenabstracttree = world.getBiome(new BlockPos(x, y, z)).getRandomTreeFeature(random);
				if (worldgenabstracttree != null) {
					worldgenabstracttree.setDecorationDefaults();
					worldgenabstracttree.generate(world, random, new BlockPos(x, y, z));
				}
			}
		}
	}
}
