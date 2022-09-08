package net.narutomod.procedure;

import net.narutomod.entity.EntityLightningArc;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureParalysisOnPotionActiveTick extends ElementsNarutomodMod.ModElement {
	public ProcedureParalysisOnPotionActiveTick(ElementsNarutomodMod instance) {
		super(instance, 383);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ParalysisOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("amplifier") == null) {
			System.err.println("Failed to load dependency amplifier for procedure ParalysisOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ParalysisOnPotionActiveTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int amplifier = (int) dependencies.get("amplifier");
		World world = (World) dependencies.get("world");
		if (((amplifier) == 1)) {
			entity.getEntityData().setInteger("FearEffect", 2);
		} else if (((amplifier) >= 2)) {
			if ((Math.random() <= (0.4 + (0.05 * ((amplifier) - 1))))) {
				EntityLightningArc.spawnAsParticle(entity.world, entity.posX + (Math.random() - 0.5d) * 0.4d, entity.posY + Math.random() * 1.3d,
						entity.posZ + (Math.random() - 0.5d) * 0.4d, 0.3d * Math.min(amplifier - 1, 12), 0d, 0.15d, 0d);
			}
		}
		if (entity instanceof EntityPlayer) {
			((EntityPlayer) entity).capabilities.isFlying = (false);
			((EntityPlayer) entity).sendPlayerAbilities();
		}
		if (entity instanceof EntityLivingBase) {
			entity.rotationYaw = ((EntityLivingBase) entity).rotationYawHead = ((EntityLivingBase) entity).renderYawOffset;
			entity.rotationPitch = 0;
		}
		if ((world.isAirBlock(new BlockPos((int) Math.floor((entity.posX)), (int) ((entity.posY) - 0.1), (int) Math.floor((entity.posZ)))))) {
			entity.motionX = 0;
			entity.motionY = ((entity.motionY) - 0.1);
			entity.motionZ = 0;
			entity.setPositionAndUpdate(entity.prevPosX, entity.posY + entity.motionY, entity.prevPosZ);
		} else if (((entity instanceof EntityLiving) && (!(entity.getEntityData().getBoolean("temporaryDisableAI"))))) {
			if (!((EntityLiving) entity).isAIDisabled()) {
				((EntityLiving) entity).setNoAI(true);
				entity.getEntityData().setBoolean("temporaryDisableAI", (true));
			}
		} else {
			entity.setPositionAndUpdate(entity.prevPosX, entity.prevPosY, entity.prevPosZ);
		}
	}
}
