package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.event.ForgeEventFactory;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureExplosiveTagBlockDestroyedByExplosion extends ElementsNarutomodMod.ModElement {
	public ProcedureExplosiveTagBlockDestroyedByExplosion(ElementsNarutomodMod instance) {
		super(instance, 433);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure ExplosiveTagBlockDestroyedByExplosion!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure ExplosiveTagBlockDestroyedByExplosion!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure ExplosiveTagBlockDestroyedByExplosion!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ExplosiveTagBlockDestroyedByExplosion!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		world.setBlockToAir(new BlockPos((int) x, (int) y, (int) z));
		if ((!(world.isRemote))) {
			world.createExplosion(null, x, y, z, 4f, ForgeEventFactory.getMobGriefingEvent(world, null));
		}
	}
}
