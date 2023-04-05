
package net.narutomod.util;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.util.ResourceLocation;

import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class LootTableJutsuLootTable extends ElementsNarutomodMod.ModElement {
	public static ResourceLocation jutsuLootTable;

	public LootTableJutsuLootTable(ElementsNarutomodMod instance) {
		super(instance, 514);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		jutsuLootTable = LootTableList.register(new ResourceLocation("narutomod", "jutsu_loot_table"));
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static LootTable getJutsuLootTable(World world) {
		return world.getLootTableManager().getLootTableFromLocation(jutsuLootTable);
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if (ModConfig.ENABLE_JUTSU_SCROLLS_IN_LOOTCHESTS
		 && (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) || event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)
		  || event.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE) || event.getName().equals(LootTableList.CHESTS_IGLOO_CHEST)
		  || event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE) || event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)
		  || event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON) || event.getName().equals(LootTableList.CHESTS_SPAWN_BONUS_CHEST)
		  || event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR) || event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CROSSING)
		  || event.getName().equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY) || event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)
		  || event.getName().equals(LootTableList.CHESTS_WOODLAND_MANSION))) {
			LootTable loottable = event.getTable();
			LootTable newtable = event.getLootTableManager().getLootTableFromLocation(jutsuLootTable);
			if (loottable.getPool("rank_a_pool") == null) {
				loottable.addPool(newtable.getPool("rank_a_pool"));
			}
			if (loottable.getPool("rank_b_pool") == null) {
				loottable.addPool(newtable.getPool("rank_b_pool"));
			}
			if (loottable.getPool("rank_cd_pool") == null) {
				loottable.addPool(newtable.getPool("rank_cd_pool"));
			}
		}
	}
}
