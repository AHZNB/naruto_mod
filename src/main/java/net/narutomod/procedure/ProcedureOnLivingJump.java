package net.narutomod.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;

import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnLivingJump extends ElementsNarutomodMod.ModElement {
	public ProcedureOnLivingJump(ElementsNarutomodMod instance) {
		super(instance, 322);
	}

	public static void lunge(EntityPlayer entity) {
		double speed = ProcedureUtils.getModifiedSpeed(entity);
		if (entity.isPotionActive(MobEffects.JUMP_BOOST) && speed >= 0.14d && entity.isSneaking()) {
			double motionY = 0.42d + (double) (entity.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1d;
			if (speed > 0.4d && motionY > 0.8d) {
				RayTraceResult t = ProcedureUtils.objectEntityLookingAt(entity, 50d, 1.0d);
				if (t != null && (t.entityHit != null || !entity.world.isAirBlock(t.getBlockPos()))) {
					entity.motionX = entity.motionY = entity.motionZ = 0d;
					Vec3d vec = t.entityHit != null ? t.entityHit.getPositionVector() : t.hitVec;
					Vec3d vec3d = entity.getPositionVector().subtract(vec).normalize();
					entity.setPosition(vec.x + vec3d.x, vec.y + vec3d.y + 0.1d, vec.z + vec3d.z);
					if (entity.world.isRemote) {
						ProcedureSync.ResetBoundingBox.sendToServer(entity);
					}
				}
			} else {
				speed += 0.8d;
				float yaw = entity.rotationYaw * 0.017453292F;
				float pitch = entity.rotationPitch * -0.017453292F;
				double d0 = Math.min(Math.cos(pitch) / 0.7071d, 1.0d);
				entity.motionX += -Math.sin(yaw) * d0 * speed * 2.5d;
				entity.motionZ += Math.cos(yaw) * d0 * speed * 2.5d;
				entity.motionY = Math.max(motionY * Math.sin(pitch) * 2.0d, 0.42d);
			}
		}
	}

	@SubscribeEvent
	public void lunge(LivingEvent.LivingJumpEvent event) {
		if (event != null && event.getEntityLiving() instanceof EntityPlayer) {
			lunge((EntityPlayer)event.getEntityLiving());
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
