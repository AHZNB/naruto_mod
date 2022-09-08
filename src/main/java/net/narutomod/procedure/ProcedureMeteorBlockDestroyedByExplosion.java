package net.narutomod.procedure;

import net.narutomod.block.BlockMeteor;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureMeteorBlockDestroyedByExplosion extends ElementsNarutomodMod.ModElement {
	public ProcedureMeteorBlockDestroyedByExplosion(ElementsNarutomodMod instance) {
		super(instance, 35);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure MeteorBlockDestroyedByExplosion!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure MeteorBlockDestroyedByExplosion!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure MeteorBlockDestroyedByExplosion!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure MeteorBlockDestroyedByExplosion!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		world.setBlockToAir(new BlockPos((int) x, (int) y, (int) z));
		world.setBlockState(new BlockPos((int) x, (int) y, (int) z), BlockMeteor.block.getDefaultState(), 3);
	}
}
