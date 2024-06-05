package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureZzzEntitySwingsItem extends ElementsNarutomodMod.ModElement {
	public ProcedureZzzEntitySwingsItem(ElementsNarutomodMod instance) {
		super(instance, 859);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ZzzEntitySwingsItem!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ZzzEntitySwingsItem!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		if ((!(world.isRemote))) {
			RayTraceResult t = ProcedureUtils.objectEntityLookingAt(entity, 30d, true);
			if (t.typeOfHit == RayTraceResult.Type.ENTITY) {
				entity = t.entityHit;
				(entity).world.removeEntity(entity);
			}
		}
	}
}
