package net.narutomod.procedure;

import net.narutomod.Particles;
import net.narutomod.event.EventDelayedCallback;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import java.util.Map;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedurePoisonbombBulletHitsBlock extends ElementsNarutomodMod.ModElement {
	private static final SpawnSmokeCallback callback = new SpawnSmokeCallback();

	public ProcedurePoisonbombBulletHitsBlock(ElementsNarutomodMod instance) {
		super(instance, 833);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure PoisonbombBulletHitsBlock!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure PoisonbombBulletHitsBlock!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure PoisonbombBulletHitsBlock!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure PoisonbombBulletHitsBlock!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		if (!world.isRemote) {
			for (int i = 0; i < 60; i++) {
				new EventDelayedCallback(world, x + world.rand.nextInt(5) - 2, y, z + world.rand.nextInt(5) - 2,
				 world.getTotalWorldTime() + i, callback);
			}
		}
	}

	public static class SpawnSmokeCallback extends EventDelayedCallback.Callback {
		public SpawnSmokeCallback() {
			super(833);
		}

		@Override
		public void execute(World world, int x, int y, int z, @Nullable Entity excludeEntity) {
			Particles.Renderer particles = new Particles.Renderer(world);
			for (int i = 0; i < 200; i++) {
				particles.spawnParticles(Particles.Types.SMOKE, x, y, z, 1, 2.0d, 1.0d, 2.0d,
				 (world.rand.nextDouble()-0.5d) * 0.4d, world.rand.nextDouble() * 0.2d,
				 (world.rand.nextDouble()-0.5d) * 0.4d, 0xff630065, 40 + world.rand.nextInt(21), 0, 0, -1, 1);
			}
			particles.send();
			for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x-4, y-4, z-4, x+4, y+4, z+4))) {
				if (excludeEntity == null || !excludeEntity.equals(entity)) {
					entity.addPotionEffect(new PotionEffect(MobEffects.WITHER, 300, 2));
				}
			}
		}
	}
}
