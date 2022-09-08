package net.narutomod.procedure;

import net.narutomod.block.BlockKamuiBlock;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureKamuiDimensionPlayerEntersDimension extends ElementsNarutomodMod.ModElement {
	public ProcedureKamuiDimensionPlayerEntersDimension(ElementsNarutomodMod instance) {
		super(instance, 117);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure KamuiDimensionPlayerEntersDimension!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure KamuiDimensionPlayerEntersDimension!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure KamuiDimensionPlayerEntersDimension!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure KamuiDimensionPlayerEntersDimension!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		double x1 = 0;
		double z1 = 0;
		double y1 = 0;
		double w = 0;
		entity.fallDistance = (float) (0);
		if ((world.isAirBlock(new BlockPos((int) x, (int) 60, (int) z)))) {
			world.setBlockState(new BlockPos((int) x, (int) 64, (int) z), BlockKamuiBlock.block.getDefaultState(), 3);
			w = (double) ((Math.random() * 5) + 5);
			y1 = (double) 64;
			while (((y1) > 0)) {
				x1 = (double) (x + (w));
				while (((x1) > (x - (w)))) {
					z1 = (double) (z + (w));
					while (((z1) > (z - (w)))) {
						world.setBlockState(new BlockPos((int) (x1), (int) (y1), (int) (z1)), BlockKamuiBlock.block.getDefaultState(), 3);
						z1 = (double) ((z1) - 1);
					}
					x1 = (double) ((x1) - 1);
				}
				y1 = (double) ((y1) - 1);
			}
		}
	}
}
