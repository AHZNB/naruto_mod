package net.narutomod.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.Entity;

import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureGrabEntity extends ElementsNarutomodMod.ModElement {
	private static Map<Entity, ProcedurePullAndHold> map = Maps.<Entity, ProcedurePullAndHold>newHashMap();
	
	public ProcedureGrabEntity(ElementsNarutomodMod instance) {
		super(instance, 333);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure GrabEntity!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure GrabEntity!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure GrabEntity!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		ProcedurePullAndHold procedure = map.get(entity);
		if (procedure == null) {
			procedure = new ProcedurePullAndHold();
			map.put(entity, procedure);
		}
		Entity grabbedEntity = null;
		RayTraceResult t = ProcedureUtils.objectEntityLookingAt(entity, 4d);
		if (is_pressed && procedure.getGrabbedEntity() == null && t.entityHit != null) {
			grabbedEntity = t.entityHit;
		}
		procedure.execute(is_pressed, entity, grabbedEntity);
	}
}
