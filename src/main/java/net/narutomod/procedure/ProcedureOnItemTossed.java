package net.narutomod.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemEightGates;
import net.narutomod.item.ItemAsuraPathArmor;
import net.narutomod.item.ItemBlackReceiver;
import net.narutomod.item.ItemAshBones;
import net.narutomod.item.ItemBoneDrill;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.event.EventDelayedCallback;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnItemTossed extends ElementsNarutomodMod.ModElement {
	private static final EventCallback CB = new EventCallback();

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
			} else if (itemstack.getItem() instanceof ItemJutsu.Base) {
				event.setCanceled(true);
				new EventDelayedCallback(entity.world, 0, 0, 0, entity, entity.world.getTotalWorldTime() + 3, CB.setItemStack(itemstack));
			}
		}
	}

	public static class EventCallback extends EventDelayedCallback.Callback {
		private ItemStack itemstack = ItemStack.EMPTY;
		
		public EventCallback() {
			super(178);
		}

		public EventCallback setItemStack(ItemStack stack) {
			this.itemstack = stack.copy();
			return this;
		}
	
		@Override
		public void execute(World world, int x, int y, int z, @Nullable Entity entity) {
			if (entity instanceof EntityPlayer && !this.itemstack.isEmpty()
			 && !ProcedureUtils.hasItemInInventory((EntityPlayer)entity, this.itemstack.getItem())) {
				ItemHandlerHelper.giveItemToPlayer((EntityPlayer)entity, this.itemstack);
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
