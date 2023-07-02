package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureHeavinessOnPotionActiveTick extends ElementsNarutomodMod.ModElement {
	public ProcedureHeavinessOnPotionActiveTick(ElementsNarutomodMod instance) {
		super(instance, 437);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure HeavinessOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("amplifier") == null) {
			System.err.println("Failed to load dependency amplifier for procedure HeavinessOnPotionActiveTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int amplifier = (int) dependencies.get("amplifier");
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) 2, (int) (amplifier), (false), (false)));
		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.JUMP_BOOST)) {
			((EntityLivingBase) entity).removePotionEffect(MobEffects.JUMP_BOOST);
		}
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity)
					.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, (int) 2, (int) ((-2) - (amplifier)), (false), (false)));
	}
}
