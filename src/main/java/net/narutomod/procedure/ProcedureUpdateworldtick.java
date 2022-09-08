package net.narutomod.procedure;

import net.narutomod.event.SpecialEvent;
import net.narutomod.NarutomodModVariables;
import net.narutomod.EntityTracker;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureUpdateworldtick extends ElementsNarutomodMod.ModElement {
	public ProcedureUpdateworldtick(ElementsNarutomodMod instance) {
		super(instance, 36);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		NarutomodModVariables.world_tick = (double) ((NarutomodModVariables.world_tick) + 0.5);
		SpecialEvent.executeSpecialEvent();
		if (((((TickEvent.WorldTickEvent) dependencies.get("event")).world.getTotalWorldTime() % 40) == 0)) {
			EntityTracker.clearRemovedData();
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			World world = event.world;
			java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
			dependencies.put("world", world);
			dependencies.put("event", event);
			this.executeProcedure(dependencies);
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
