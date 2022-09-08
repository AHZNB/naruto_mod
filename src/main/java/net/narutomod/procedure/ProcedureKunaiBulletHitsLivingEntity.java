package net.narutomod.procedure;

import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureKunaiBulletHitsLivingEntity extends ElementsNarutomodMod.ModElement {
	public ProcedureKunaiBulletHitsLivingEntity(ElementsNarutomodMod instance) {
		super(instance, 159);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("sourceentity") == null) {
			System.err.println("Failed to load dependency sourceentity for procedure KunaiBulletHitsLivingEntity!");
			return;
		}
		Entity sourceentity = (Entity) dependencies.get("sourceentity");
		if ((sourceentity instanceof EntityPlayerMP)) {
			PlayerTracker.logBattleExp((EntityPlayer) sourceentity, 1);
		}
	}
}
