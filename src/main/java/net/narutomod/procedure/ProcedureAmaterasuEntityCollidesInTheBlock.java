package net.narutomod.procedure;

import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAmaterasuEntityCollidesInTheBlock extends ElementsNarutomodMod.ModElement {
	public ProcedureAmaterasuEntityCollidesInTheBlock(ElementsNarutomodMod instance) {
		super(instance, 79);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure AmaterasuEntityCollidesInTheBlock!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, (int) 10000, (int) 0, (false), (false)));
		entity.setFire((int) 10000);
	}
}
