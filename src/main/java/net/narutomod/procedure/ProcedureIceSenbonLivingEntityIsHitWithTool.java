package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureIceSenbonLivingEntityIsHitWithTool extends ElementsNarutomodMod.ModElement {
	public ProcedureIceSenbonLivingEntityIsHitWithTool(ElementsNarutomodMod instance) {
		super(instance, 727);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure IceSenbonLivingEntityIsHitWithTool!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure IceSenbonLivingEntityIsHitWithTool!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		if ((!(world.isRemote))) {
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) 200, (int) 1));
		}
	}
}
