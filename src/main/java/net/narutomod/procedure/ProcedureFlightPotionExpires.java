package net.narutomod.procedure;

import net.narutomod.potion.PotionFlight;
import net.narutomod.potion.PotionFeatherFalling;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.Collection;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureFlightPotionExpires extends ElementsNarutomodMod.ModElement {
	public ProcedureFlightPotionExpires(ElementsNarutomodMod instance) {
		super(instance, 417);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure FlightPotionExpires!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if ((!(new Object() {
			boolean check() {
				if (entity instanceof EntityLivingBase) {
					Collection<PotionEffect> effects = ((EntityLivingBase) entity).getActivePotionEffects();
					for (PotionEffect effect : effects) {
						if (effect.getPotion() == PotionFlight.potion)
							return true;
					}
				}
				return false;
			}
		}.check()))) {
			if (entity instanceof EntityPlayer) {
				((EntityPlayer) entity).capabilities.isFlying = (false);
				((EntityPlayer) entity).sendPlayerAbilities();
			}
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionFeatherFalling.potion, (int) 200, (int) 4, (false), (false)));
		}
	}
}
