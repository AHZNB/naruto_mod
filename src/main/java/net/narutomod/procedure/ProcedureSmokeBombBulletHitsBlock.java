package net.narutomod.procedure;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;

import net.narutomod.Particles;
import net.narutomod.event.EventDelayedCallback;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSmokeBombBulletHitsBlock extends ElementsNarutomodMod.ModElement {
	private static final SpawnSmokeCallback callback = new SpawnSmokeCallback();

	public ProcedureSmokeBombBulletHitsBlock(ElementsNarutomodMod instance) {
		super(instance, 681);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure SmokeBombBulletHitsBlock!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure SmokeBombBulletHitsBlock!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure SmokeBombBulletHitsBlock!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SmokeBombBulletHitsBlock!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		if (!world.isRemote) {
			for (int i = 0; i < 60; i++) {
				new EventDelayedCallback(world, x, y, z, world.getTotalWorldTime() + i, callback);
			}
		}
	}

	public static class SpawnSmokeCallback extends EventDelayedCallback.Callback {
		public SpawnSmokeCallback() {
			super(681);
		}

		@Override
		public void execute(World world, int x, int y, int z, @Nullable Entity excludeEntity) {
			Particles.Renderer particles = new Particles.Renderer(world);
			for (int i = 0; i < 200; i++) {
				particles.spawnParticles(Particles.Types.SMOKE, x, y, z, 1, 2.0d, 1.0d, 2.0d,
				 (world.rand.nextDouble()-0.5d) * 0.4d, world.rand.nextDouble() * 0.2d,
				 (world.rand.nextDouble()-0.5d) * 0.4d, 0xff101010, 40 + world.rand.nextInt(21), 0, 0, -1, 1);
			}
			particles.send();
		}
	}
}
