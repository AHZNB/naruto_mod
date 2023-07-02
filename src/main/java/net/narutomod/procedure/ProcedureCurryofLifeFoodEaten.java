package net.narutomod.procedure;

import net.narutomod.potion.PotionChakraRegeneration;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureCurryofLifeFoodEaten extends ElementsNarutomodMod.ModElement {
	public ProcedureCurryofLifeFoodEaten(ElementsNarutomodMod instance) {
		super(instance, 781);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure CurryofLifeFoodEaten!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionChakraRegeneration.potion, (int) 400, (int) 1, (false), (false)));
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, (int) 100, (int) 1, (false), (false)));
	}
}
