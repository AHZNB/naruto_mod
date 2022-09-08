package net.narutomod.procedure;

import net.narutomod.potion.PotionParalysis;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureTotsukaSwordLivingEntityIsHitWithTool extends ElementsNarutomodMod.ModElement {
	public ProcedureTotsukaSwordLivingEntityIsHitWithTool(ElementsNarutomodMod instance) {
		super(instance, 699);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure TotsukaSwordLivingEntityIsHitWithTool!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionParalysis.potion, (int) 60, (int) 0));
		if (entity instanceof EntityLivingBase) {
			Chakra.pathway((EntityLivingBase) entity).consume(500.0d);
		}
	}
}
