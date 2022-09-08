package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureChakraRegenerationOnPotionActiveTick extends ElementsNarutomodMod.ModElement {
	public ProcedureChakraRegenerationOnPotionActiveTick(ElementsNarutomodMod instance) {
		super(instance, 402);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ChakraRegenerationOnPotionActiveTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if ((entity instanceof EntityPlayerMP)) {
			if (entity.ticksExisted % 20 == 0)
				Chakra.pathway((EntityPlayer) entity).consume(-0.05f, true);
		}
	}
}
