package net.narutomod.procedure;

import net.narutomod.item.ItemRyo10000;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.WorldServer;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnZombieDoctor extends ElementsNarutomodMod.ModElement {
	public ProcedureOnZombieDoctor(ElementsNarutomodMod instance) {
		super(instance, 829);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		EntityPlayer entity = dependencies.get("entity") instanceof EntityPlayer ? (EntityPlayer)dependencies.get("entity") : null;
		Advancement adv = dependencies.get("advancement") instanceof Advancement ? (Advancement)dependencies.get("advancement") : null;
		if (entity == null) {
			System.err.println("Failed to load dependency entity for procedure OnZombieDoctor!");
			return;
		}
		if (adv == null) {
			System.err.println("Failed to load dependency advancement for procedure OnZombieDoctor!");
			return;
		}
		if (adv.getId().getResourcePath().equals("story/cure_zombie_villager")) {
			ItemHandlerHelper.giveItemToPlayer(entity, new ItemStack(ItemRyo10000.block, 1));
		}
	}

	@SubscribeEvent
	public void onPlayerAdvancement(AdvancementEvent event) {
		EntityPlayer entity = event.getEntityPlayer();
		Advancement advancement = event.getAdvancement();
		java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
		dependencies.put("x", (int) entity.posX);
		dependencies.put("y", (int) entity.posY);
		dependencies.put("z", (int) entity.posZ);
	 	dependencies.put("world", entity.world);
	 	dependencies.put("entity", entity);
	 	dependencies.put("advancement", advancement);
	 	dependencies.put("event", event);
		this.executeProcedure(dependencies);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
