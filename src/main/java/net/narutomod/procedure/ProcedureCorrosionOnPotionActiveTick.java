package net.narutomod.procedure;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureCorrosionOnPotionActiveTick extends ElementsNarutomodMod.ModElement {
	public ProcedureCorrosionOnPotionActiveTick(ElementsNarutomodMod instance) {
		super(instance, 601);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure CorrosionOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("amplifier") == null) {
			System.err.println("Failed to load dependency amplifier for procedure CorrosionOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure CorrosionOnPotionActiveTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int amplifier = (int) dependencies.get("amplifier");
		World world = (World) dependencies.get("world");
		double amp = 0;
		amp = (double) (amplifier);
		entity.attackEntityFrom(ItemJutsu.NINJUTSU_DAMAGE, (float) amp + 1f);
		if ((Math.random() <= 0.5)) {
			world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("block.fire.extinguish")),
					SoundCategory.NEUTRAL, (float) 0.8, (float) ((Math.random() * 0.6) + 0.6));
		}
	}
}
