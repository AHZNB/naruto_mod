package net.narutomod.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import net.narutomod.item.ItemJutsu;
//import net.narutomod.item.ItemExpandedTruthSeekerBall;
//import net.narutomod.item.ItemEightyGodsKusho;
import net.narutomod.item.ItemEightGates;
import net.narutomod.item.ItemAsuraPathArmor;
import net.narutomod.item.ItemBlackReceiver;
import net.narutomod.item.ItemAshBones;
import net.narutomod.item.ItemBoneDrill;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnItemTossed extends ElementsNarutomodMod.ModElement {
	public ProcedureOnItemTossed(ElementsNarutomodMod instance) {
		super(instance, 178);
	}

	@SubscribeEvent
	public void onToss(ItemTossEvent event) {
		EntityPlayer entity = event.getPlayer();
		if (!entity.world.isRemote) {
			ItemStack itemstack = event.getEntityItem().getItem();
			if (itemstack.getItem() == ItemBlackReceiver.block
			 || itemstack.getItem() == ItemEightGates.block
			 || itemstack.getItem() == ItemBoneDrill.block
			 || itemstack.getItem() == ItemAsuraPathArmor.body
			 || itemstack.getItem() == ItemAshBones.block) {
				event.setCanceled(true);
			}
			if (itemstack.getItem() instanceof ItemJutsu.Base) {
				event.setCanceled(true);
				if (!ProcedureUtils.hasItemInInventory(entity, itemstack.getItem())) {
					ItemHandlerHelper.giveItemToPlayer(entity, itemstack.copy());
				}
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
