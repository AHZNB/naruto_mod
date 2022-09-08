package net.narutomod.procedure;

import net.narutomod.item.ItemOnBody;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSyncInventory extends ElementsNarutomodMod.ModElement {
	public ProcedureSyncInventory(ElementsNarutomodMod instance) {
		super(instance, 721);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure SyncInventory!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (((entity instanceof EntityPlayerMP) && entity.ticksExisted % 40 == 10)) {
			ItemOnBody.InventoryTracker.createOrSyncInventory((EntityPlayerMP) entity);
		}
	}
}
