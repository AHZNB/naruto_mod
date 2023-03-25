package net.narutomod.procedure;

import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemOnBody;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.WorldServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSyncInventory extends ElementsNarutomodMod.ModElement {
	public ProcedureSyncInventory(ElementsNarutomodMod instance) {
		super(instance, 721);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure SyncInventory!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (((entity instanceof EntityPlayerMP) && entity.ticksExisted % 30 == 9)) {
			if (((!((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false))
					&& ModConfig.REMOVE_CHEAT_DOJUTSUS)) {
				if ((((entity instanceof EntityPlayer)
						? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)))
						: false)
						&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
								? ((EntityPlayerMP) entity).getAdvancements()
										.getProgress(((WorldServer) (entity).world).getAdvancementManager()
												.getAdvancement(new ResourceLocation("narutomod:mangekyosharinganopened")))
										.isDone()
								: false)))) {
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
								((TextFormatting.RED) + "" + ("You obtained your advanced dojutsu illegally, it will be removed"))), (false));
					}
					if (entity instanceof EntityPlayer)
						((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)).getItem(),
								-1, (int) (-1), null);
				}
				if ((((entity instanceof EntityPlayer)
						? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemRinnegan.helmet, (int) (1)))
						: false)
						&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
								? ((EntityPlayerMP) entity).getAdvancements()
										.getProgress(((WorldServer) (entity).world).getAdvancementManager()
												.getAdvancement(new ResourceLocation("narutomod:eternalmangekyoachieved")))
										.isDone()
								: false)))) {
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
								((TextFormatting.RED) + "" + ("You obtained your advanced dojutsu illegally, it will be removed"))), (false));
					}
					if (entity instanceof EntityPlayer)
						((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemRinnegan.helmet, (int) (1)).getItem(), -1, (int) (-1),
								null);
				}
				if ((((entity instanceof EntityPlayer)
						? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemTenseigan.helmet, (int) (1)))
						: false)
						&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
								? ((EntityPlayerMP) entity).getAdvancements()
										.getProgress(((WorldServer) (entity).world).getAdvancementManager()
												.getAdvancement(new ResourceLocation("narutomod:byakuganopened")))
										.isDone()
								: false)))) {
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
								((TextFormatting.RED) + "" + ("You obtained your advanced dojutsu illegally, it will be removed"))), (false));
					}
					if (entity instanceof EntityPlayer)
						((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem(), -1, (int) (-1),
								null);
				}
			}
			ItemOnBody.InventoryTracker.createOrSyncInventory((EntityPlayerMP) entity);
		}
	}
}
