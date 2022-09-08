package net.narutomod.procedure;

import net.narutomod.item.ItemUchiha;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureUchihaBodyTickEvent extends ElementsNarutomodMod.ModElement {
	public ProcedureUchihaBodyTickEvent(ElementsNarutomodMod instance) {
		super(instance, 184);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure UchihaBodyTickEvent!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure UchihaBodyTickEvent!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		if ((((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(1) : ItemStack.EMPTY)
				.getItem() == new ItemStack(ItemUchiha.legs, (int) (1)).getItem())
				&& (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(0) : ItemStack.EMPTY)
						.getItem() == new ItemStack(ItemUchiha.boots, (int) (1)).getItem()))
				&& ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
						.getItem() == new ItemStack(ItemSharingan.helmet, (int) (1)).getItem())
						|| ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
								.getItem() == new ItemStack(ItemMangekyoSharingan.helmet, (int) (1)).getItem())
								|| (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
										.getItem() == new ItemStack(ItemMangekyoSharinganObito.helmet, (int) (1)).getItem()))))) {
			if ((!(world.isRemote))) {
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, (int) 1, (int) 1, (false), (false)));
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, (int) 1, (int) 2, (false), (false)));
			}
		}
	}
}
