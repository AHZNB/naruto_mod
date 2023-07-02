package net.narutomod.procedure;

import net.narutomod.potion.PotionFlight;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.entity.EntityEarthBlocks;
import net.narutomod.event.SpecialEvent;
import net.narutomod.Particles;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureShinraTenseiOnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureShinraTenseiOnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 21);
	}

	public static void executeProcedure(java.util.Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure ShinratenseiOnKeyPressed!");
			return;
		}
		if (!(dependencies.get("entity") instanceof EntityLivingBase)) {
			System.err.println("Failed to load dependency entity for procedure ShinratenseiOnKeyPressed!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure ShinratenseiOnKeyPressed!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure ShinratenseiOnKeyPressed!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure ShinratenseiOnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ShinratenseiOnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		EntityLivingBase entity = (EntityLivingBase) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		
		if ((entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative())
		 || ProcedureUpdateworldtick.getTotalWorldTime() < entity.getEntityData().getLong("shinratenseicd") - 400
		 || ProcedureUpdateworldtick.getTotalWorldTime() > entity.getEntityData().getLong("shinratenseicd") + 100) {
			double power = (double) (entity.getEntityData().getDouble("shinratensei_power"));
			if (is_pressed) {
				if (!entity.getEntityData().getBoolean("was_pressed")) {
					power = 10.0d;
				}
				if (power + 0.1d < ItemJutsu.getMaxPower(entity, ItemRinnegan.getShinratenseiChakraUsage(entity)) && power < 100d) {
					power += 0.1d;
				}
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer)entity).sendStatusMessage(new TextComponentString("Power " + (int)power), true);
				}
				entity.addPotionEffect(new PotionEffect(PotionFlight.potion, 200, 1, false, false));
				entity.getEntityData().setBoolean("was_pressed", true);
			} else {
				if (entity.getEntityData().getBoolean("was_pressed")) {
					entity.getEntityData().setBoolean("was_pressed", false);
					if (power >= 5 && Chakra.pathway(entity).consume(power * ItemRinnegan.getShinratenseiChakraUsage(entity))) {
						List<EntityEarthBlocks.Base> list = ProcedureBanShoTenin.getGrabbedEarthBlocks(entity);
						RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 40d, 3d);
						if (!list.isEmpty() && res != null && res.entityHit != null && !list.contains(res.entityHit)) {
							for (EntityEarthBlocks.Base entity1 : list) {
								entity1.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:BanshoTenin")), 1f, 1f);
								Vec3d vec = res.entityHit.getPositionEyes(1f).subtract(entity1.getPositionVector()).normalize().scale(power * 0.1d);
								entity1.motionX = vec.x;
								entity1.motionY = vec.y;
								entity1.motionZ = vec.z;
								entity1.setNoGravity(false);
							}
						} else {
							entity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 60);
							for (int i = 0; i < 1000; i++) {
								Particles.spawnParticle(world, Particles.Types.SMOKE, entity.posX, entity.posY + 1.4d, entity.posZ,
								 1, 1d, 0d, 1d, (entity.getRNG().nextDouble() - 0.5d) * 2, (entity.getRNG().nextDouble() - 0.5d) * 2,
								 (entity.getRNG().nextDouble() - 0.5d) * 2, 0x10FFFFFF, 25 + entity.getRNG().nextInt(25), 0);
							}
							if (power >= 20) {
								SpecialEvent.setSphericalExplosionEvent(world, x, y + 2, z, (int)(power * power / 200), entity);
								world.playSound(null, x, y, z, SoundEvent.REGISTRY
								 .getObject(new ResourceLocation("narutomod:ShinraTensei")), SoundCategory.NEUTRAL, 5f, 1f);
							} else {
								world.playSound(null, x, y, z, SoundEvent.REGISTRY
								 .getObject(new ResourceLocation("narutomod:BanshoTenin")), SoundCategory.NEUTRAL, 1f, 1f);
							}
							if (entity instanceof EntityPlayer && !entity.world.isRemote) {
								((EntityPlayer)entity).sendStatusMessage(new TextComponentString("Power " + (int)power), true);
							}
							ProcedureAoeCommand.set(entity, 0d, power).exclude(entity).damageEntities(entity, (float)power).knockback(2f).noGravity(false);
							ProcedureUtils.purgeHarmfulEffects(entity);
							entity.extinguish();
						}
						double cd_modifier = Chakra.getChakraModifier(entity);
						entity.getEntityData().setLong("shinratenseicd", ProcedureUpdateworldtick.getTotalWorldTime() + (long)(power * 10 * cd_modifier));
					}
					power = 0.0d;
				}
			}
			entity.getEntityData().setDouble("shinratensei_power", power);
		} else {
			if (entity instanceof EntityPlayer && !entity.world.isRemote) {
				((EntityPlayer)entity).sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", 
				 (entity.getEntityData().getLong("shinratenseicd") - ProcedureUpdateworldtick.getTotalWorldTime() + 100) / 20), true);
			}
		}
	}
}
