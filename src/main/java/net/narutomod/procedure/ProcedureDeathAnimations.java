package net.narutomod.procedure;

import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureDeathAnimations extends ElementsNarutomodMod.ModElement {
	public ProcedureDeathAnimations(ElementsNarutomodMod instance) {
		super(instance, 273);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure DeathAnimations!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure DeathAnimations!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double w = 0;
		double h = 0;
		double n = 0;
		if (((entity.getEntityData().getDouble("deathAnimationType")) == 1)) {
			w = entity.width / 2;
			h = entity.height / 2;
			n = (double) (((NarutomodModVariables.DeathAnimation_slowDust)
					- (entity.getEntityData().getDouble((NarutomodModVariables.DeathAnimationTime)))) * (h));
			((WorldServer) entity.world).spawnParticle(EnumParticleTypes.FALLING_DUST, entity.posX, entity.posY + h, entity.posZ, (int) n, w * 0.5,
					h * 0.3, w * 0.5, 0, 4);
		} else if (((entity.getEntityData().getDouble("deathAnimationType")) == 2)) {
			w = entity.width / 2;
			h = entity.height / 3;
			if (world instanceof WorldServer)
				((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (entity.posX), ((entity.posY) + (h)), (entity.posZ), (int) 20,
						((w) * 0.5), ((h) * 0.5), ((w) * 0.5), 0, new int[0]);
		}
	}
}
