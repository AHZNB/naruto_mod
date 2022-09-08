package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureTotsukaSwordToolInHandTick extends ElementsNarutomodMod.ModElement {
	public ProcedureTotsukaSwordToolInHandTick(ElementsNarutomodMod instance) {
		super(instance, 550);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure TotsukaSwordToolInHandTick!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure TotsukaSwordToolInHandTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double x1 = 0;
		double y1 = 0;
		double z1 = 0;
		double entity_scale = 0;
		double random = 0;
		double renderYawOffset = 0;
		double y_offset = 0;
		if ((Math.random() < 0.05)) {
			world.playSound((EntityPlayer) null, (entity.posX), ((entity.posY) + ((entity_scale) * 0.9)), (entity.posZ),
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("block.fire.ambient")),
					SoundCategory.NEUTRAL, (float) 0.9, (float) ((Math.random() * 0.7) + 0.3));
		}
	}
}
