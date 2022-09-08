package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureFeatherFallingOnPotionActiveTick extends ElementsNarutomodMod.ModElement {
	public ProcedureFeatherFallingOnPotionActiveTick(ElementsNarutomodMod instance) {
		super(instance, 389);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure FeatherFallingOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("amplifier") == null) {
			System.err.println("Failed to load dependency amplifier for procedure FeatherFallingOnPotionActiveTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int amplifier = (int) dependencies.get("amplifier");
		double fallDistance = 0;
		fallDistance = entity.fallDistance;
		entity.fallDistance = (float) (((fallDistance) - (0.3 * ((amplifier) + 1))));
	}
}
