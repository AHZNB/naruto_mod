package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureFlightOnPotionActiveTick extends ElementsNarutomodMod.ModElement {
	public ProcedureFlightOnPotionActiveTick(ElementsNarutomodMod instance) {
		super(instance, 416);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure FlightOnPotionActiveTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (entity instanceof EntityPlayer) {
			((EntityPlayer) entity).capabilities.isFlying = (true);
			((EntityPlayer) entity).sendPlayerAbilities();
		}
	}
}
