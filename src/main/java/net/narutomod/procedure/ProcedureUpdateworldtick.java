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
	private static long TOTAL_WORLD_TIME;
	
	public ProcedureUpdateworldtick(ElementsNarutomodMod instance) {
		super(instance, 36);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure Updateworldtick!");
			return;
		}
		World world = (World) dependencies.get("world");
		NarutomodModVariables.world_tick = (double) ((NarutomodModVariables.world_tick) + 0.5);
		SpecialEvent.executeEvents();
		long l = world.getTotalWorldTime();
		if (world.provider.getDimension() == 0) {
			TOTAL_WORLD_TIME = l;
		} else if (l != TOTAL_WORLD_TIME) {
			world.getWorldInfo().setWorldTotalTime(TOTAL_WORLD_TIME);
		}
		if (l % 40 == 0) {
			EntityTracker.clearRemovedData();
		}
	}

	public static long getTotalWorldTime() {
		return TOTAL_WORLD_TIME;
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
