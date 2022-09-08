package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureParalysisPotionExpires extends ElementsNarutomodMod.ModElement {
	public ProcedureParalysisPotionExpires(ElementsNarutomodMod instance) {
		super(instance, 409);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ParalysisPotionExpires!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (((entity instanceof EntityLiving) && (entity.getEntityData().getBoolean("temporaryDisableAI")))) {
			((EntityLiving) entity).setNoAI(false);
			entity.getEntityData().setBoolean("temporaryDisableAI", (false));
		}
	}
}
