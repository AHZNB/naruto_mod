package net.narutomod.procedure;

import net.narutomod.entity.EntityFutonGreatBreakthrough;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Random;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureFoldingFanRangedItemUsed extends ElementsNarutomodMod.ModElement {
	public ProcedureFoldingFanRangedItemUsed(ElementsNarutomodMod instance) {
		super(instance, 707);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure FoldingFanRangedItemUsed!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure FoldingFanRangedItemUsed!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		{
			ItemStack _ist = (itemstack);
			if (_ist.attemptDamageItem((int) 1, new Random(), null)) {
				_ist.shrink(1);
				_ist.setItemDamage(0);
			}
		}
		if (((((itemstack)).getItemDamage()) <= (((itemstack)).getMaxDamage()))) {
			(entity).extinguish();
			new EntityFutonGreatBreakthrough.EC.Jutsu().createJutsu(itemstack, (EntityLivingBase) entity,
					Math.min(60f, 0.5f * (float) ((EntityLivingBase) entity).getItemInUseMaxCount()));
		}
	}
}
