package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.state.IBlockState;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureLightSourceUpdateTick extends ElementsNarutomodMod.ModElement {
	public ProcedureLightSourceUpdateTick(ElementsNarutomodMod instance) {
		super(instance, 723);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure LightSourceUpdateTick!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure LightSourceUpdateTick!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure LightSourceUpdateTick!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure LightSourceUpdateTick!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		double age = 0;
		age = (double) (new Object() {
			public double getValue(BlockPos pos, String tag) {
				TileEntity tileEntity = world.getTileEntity(pos);
				if (tileEntity != null)
					return tileEntity.getTileData().getDouble(tag);
				return -1;
			}
		}.getValue(new BlockPos((int) x, (int) y, (int) z), "lightsourceAge"));
		if (((!(world.isRemote)) && ((age) > 1))) {
			world.setBlockToAir(new BlockPos((int) x, (int) y, (int) z));
		}
		if (!world.isRemote) {
			BlockPos _bp = new BlockPos((int) x, (int) y, (int) z);
			TileEntity _tileEntity = world.getTileEntity(_bp);
			IBlockState _bs = world.getBlockState(_bp);
			if (_tileEntity != null)
				_tileEntity.getTileData().setDouble("lightsourceAge", ((age) + 1));
			world.notifyBlockUpdate(_bp, _bs, _bs, 3);
		}
	}
}
