/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods, you can remove them as they
 * are overrides of the base class ElementsJadenssword.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list, it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor, the compilation will fail.
 *
 * If you want to make a plain independent class, create it in
 * "Workspace" -> "Source" menu.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
*/
package net.narutomod;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import java.util.List;
import java.util.Arrays;

@ElementsNarutomodMod.ModElement.Tag
public class SaveData extends ElementsNarutomodMod.ModElement {
	private static final List<ISaveData> savedataList = Arrays.asList(
		new net.narutomod.event.SpecialEvent.Save(), 
		new net.narutomod.entity.EntityTenTails.Save(),
		new net.narutomod.entity.EntityNineTails.Save(),
		new net.narutomod.entity.EntityEightTails.Save(),
		new net.narutomod.entity.EntitySevenTails.Save(),
		new net.narutomod.entity.EntitySixTails.Save(),
		new net.narutomod.entity.EntityFiveTails.Save(),
		new net.narutomod.entity.EntityFourTails.Save(),
		new net.narutomod.entity.EntityThreeTails.Save(),
		new net.narutomod.entity.EntityTwoTails.Save(),
		new net.narutomod.entity.EntityOneTail.Save()
	);

	public SaveData(ElementsNarutomodMod instance) {
		super(instance, 334);
	}

//	@Override
//	public void serverLoad(FMLServerStartingEvent event) {
//System.out.println("<<<<<<<<<<<< serverLoad event >>> players:"+event.getServer().getCurrentPlayerCount()+", entities:"+event.getServer().getWorld(0).loadedEntityList.size());
//	}

	/*public static void loadAllSavedData() {
		System.out.println("Loading all save data for mod "+NarutomodMod.MODID);
		for (ISaveData savedata : savedataList) {
			savedata.loadData();
		}
	}*/

	public static interface ISaveData {
		WorldSavedData loadData();
		void resetData();
	}

	public class EventHook {
		private boolean loaded = false;

		@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload event) {
			World world = event.getWorld();
			if (!world.isRemote && world.provider.getDimension() == 0) {
				for (ISaveData savedata : savedataList) {
					savedata.resetData();
				}
				this.loaded = false;
			}
		}

		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void onEntityJoinWorld(EntityJoinWorldEvent event) {
			if (!event.getWorld().isRemote && !this.loaded) {
				System.out.println("Loading all save data for mod "+NarutomodMod.MODID);
				for (ISaveData savedata : savedataList) {
					savedata.loadData();
				}
				this.loaded = true;
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EventHook());
	}
	
}
