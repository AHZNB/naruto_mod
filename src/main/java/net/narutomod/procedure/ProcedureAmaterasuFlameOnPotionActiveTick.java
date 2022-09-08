package net.narutomod.procedure;

import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAmaterasuFlameOnPotionActiveTick extends ElementsNarutomodMod.ModElement {
	public ProcedureAmaterasuFlameOnPotionActiveTick(ElementsNarutomodMod instance) {
		super(instance, 175);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure AmaterasuFlameOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("amplifier") == null) {
			System.err.println("Failed to load dependency amplifier for procedure AmaterasuFlameOnPotionActiveTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int amplifier = (int) dependencies.get("amplifier");
		boolean f1 = false;
		double w = 0;
		double h = 0;
		double amp = 0;
		if (((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
				.getItem() == new ItemStack(ItemMangekyoSharingan.helmet, (int) (1)).getItem())
				|| (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
						.getItem() == new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)).getItem()))) {
			((EntityLivingBase) entity).removePotionEffect(PotionAmaterasuFlame.potion);
			(entity).extinguish();
		} else {
			amp = (double) (amplifier);
			entity.attackEntityFrom(ProcedureUtils.AMATERASU, (float) (amp + 1));
			w = entity.width / 2;
			h = entity.height;
			Particles.spawnParticle(entity.world, Particles.Types.FLAME, entity.posX, entity.posY + h / 2, entity.posZ, amplifier + 1, w * 0.5,
					h * 0.2, w * 0.5, 0d, 0d, 0d, 0xA0000000, 20);
		}
	}
}
