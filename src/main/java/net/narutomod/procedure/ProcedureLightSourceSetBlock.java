package net.narutomod.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

import net.narutomod.block.BlockLightSource;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureLightSourceSetBlock extends ElementsNarutomodMod.ModElement {
	public ProcedureLightSourceSetBlock(ElementsNarutomodMod instance) {
		super(instance, 725);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure LightSourceSetBlock!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure LightSourceSetBlock!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure LightSourceSetBlock!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure LightSourceSetBlock!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		execute(world, x, y, z);
	}

	public static void execute(World world, int x, int y, int z) {
		if (!world.isRemote) {
			if (((new Object() {
				public double getValue(BlockPos pos, String tag) {
					TileEntity tileEntity = world.getTileEntity(pos);
					if (tileEntity != null)
						return tileEntity.getTileData().getDouble(tag);
					return -1;
				}
			}.getValue(new BlockPos((int) x, (int) y, (int) z), "lightsourceAge")) <= 0)) {
				world.setBlockState(new BlockPos((int) x, (int) y, (int) z), BlockLightSource.block.getDefaultState(), 3);
			} else {
				if (!world.isRemote) {
					BlockPos _bp = new BlockPos((int) x, (int) y, (int) z);
					TileEntity _tileEntity = world.getTileEntity(_bp);
					IBlockState _bs = world.getBlockState(_bp);
					if (_tileEntity != null)
						_tileEntity.getTileData().setDouble("lightsourceAge", 1);
					world.notifyBlockUpdate(_bp, _bs, _bs, 3);
				}
			}
		}
	}
}
