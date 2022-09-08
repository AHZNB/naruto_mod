package net.narutomod.procedure;

import net.narutomod.event.SpecialEvent;
import net.narutomod.block.BlockMeteorite;
import net.narutomod.block.BlockMeteor;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureMeteorBlockAdded extends ElementsNarutomodMod.ModElement {
	public ProcedureMeteorBlockAdded(ElementsNarutomodMod instance) {
		super(instance, 41);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure MeteorBlockAdded!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure MeteorBlockAdded!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure MeteorBlockAdded!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure MeteorBlockAdded!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		if ((!(world.isAirBlock(new BlockPos((int) x, (int) (y - 1), (int) z))))) {
			if ((!(world.isRemote))) {
				SpecialEvent.setSphericalExplosionEvent(world, x, y - 1, z, 12, null, 0.33f);
				ProcedureAoeCommand.set(world, x, y, z, 0d, 12d).damageEntities(DamageSource.causeExplosionDamage((Explosion) null), 30)
						.knockback(1.5f);
			}
			if (((world.getBlockState(new BlockPos((int) x, (int) y, (int) z))).getBlock() == BlockMeteor.block.getDefaultState().getBlock())) {
				world.setBlockToAir(new BlockPos((int) x, (int) y, (int) z));
				world.setBlockState(new BlockPos((int) x, (int) y, (int) z), BlockMeteorite.block.getDefaultState(), 3);
			}
		}
	}
}
