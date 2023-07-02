package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;

import java.util.UUID;
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
		((EntityLivingBase) entity).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
				.removeModifier(UUID.fromString("c69af92a-b96d-49b7-a396-9b3b0d77edd5"));
	}
}
