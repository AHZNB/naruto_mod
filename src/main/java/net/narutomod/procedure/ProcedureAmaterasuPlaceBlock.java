package net.narutomod.procedure;

import net.narutomod.block.BlockAmaterasuBlock;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAmaterasuPlaceBlock extends ElementsNarutomodMod.ModElement {
	public ProcedureAmaterasuPlaceBlock(ElementsNarutomodMod instance) {
		super(instance, 227);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure AmaterasuPlaceBlock!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure AmaterasuPlaceBlock!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure AmaterasuPlaceBlock!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure AmaterasuPlaceBlock!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		if ((world.isAirBlock(new BlockPos((int) x, (int) y, (int) z)))) {
			world.setBlockState(new BlockPos((int) x, (int) y, (int) z), BlockAmaterasuBlock.block.getDefaultState(), 3);
		}
	}
}
