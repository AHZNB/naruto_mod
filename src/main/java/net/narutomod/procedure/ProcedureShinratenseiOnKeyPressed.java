package net.narutomod.procedure;

import net.narutomod.potion.PotionFlight;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.event.SpecialEvent;
import net.narutomod.Particles;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureShinratenseiOnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureShinratenseiOnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 21);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure ShinratenseiOnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
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
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		String string = "";
		double power = 0;
		double cd_modifier = 0;
		double chakraAmount = 0;
		if ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)
				|| (((NarutomodModVariables.world_tick) < ((entity.getEntityData().getDouble("shinratenseicd")) - 400))
						|| ((NarutomodModVariables.world_tick) > ((entity.getEntityData().getDouble("shinratenseicd")) + 100))))) {
			power = (double) (entity.getEntityData().getDouble("shinratensei_power"));
			Chakra.Pathway cp = Chakra.pathway((EntityPlayer) entity);
			chakraAmount = cp.getAmount();
			if ((is_pressed)) {
				if ((!(entity.getEntityData().getBoolean("was_pressed")))) {
					power = (double) 10;
				}
				if (((((power) + 0.1) < ItemJutsu.getMaxPower((EntityLivingBase) entity,
						ItemRinnegan.getShinratenseiChakraUsage((EntityLivingBase) entity))) && ((power) < 100))) {
					power = (double) ((power) + 0.1);
				}
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer) entity).sendStatusMessage(
							new TextComponentString((("Power ") + "" + ((new java.text.DecimalFormat(".1").format((power)))))), (true));
				}
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionFlight.potion, (int) 200, (int) 1, (false), (false)));
				entity.getEntityData().setBoolean("was_pressed", (true));
			} else {
				if ((entity.getEntityData().getBoolean("was_pressed"))) {
					entity.getEntityData().setBoolean("was_pressed", (false));
					if ((((power) >= 5) && cp.consume(power * ItemRinnegan.getShinratenseiChakraUsage((EntityLivingBase) entity)))) {
						entity.getEntityData().setDouble((NarutomodModVariables.InvulnerableTime), 60);
						for (int index0 = 0; index0 < (int) (1000); index0++) {
							Particles.spawnParticle(world, Particles.Types.SMOKE, entity.posX, entity.posY + 1.4d, entity.posZ, 1, 1d, 0d, 1d,
									(ProcedureUtils.rng().nextDouble() - 0.5d) * 2, (ProcedureUtils.rng().nextDouble() - 0.5d) * 2,
									(ProcedureUtils.rng().nextDouble() - 0.5d) * 2, 0x10FFFFFF, 25 + ProcedureUtils.rngInt(25), 0);
						}
						if (((power) >= 20)) {
							world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
									.getObject(new ResourceLocation("narutomod:ShinraTensei")), SoundCategory.NEUTRAL, (float) 5, (float) 1);
						} else {
							world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
									.getObject(new ResourceLocation("narutomod:BanshoTenin")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
						}
						if (entity instanceof EntityPlayer && !entity.world.isRemote) {
							((EntityPlayer) entity).sendStatusMessage(
									new TextComponentString((("Power ") + "" + ((new java.text.DecimalFormat(".1").format((power)))))), (true));
						}
						if (((power) > 20)) {
							SpecialEvent.setSphericalExplosionEvent(world, x, y + 2, z, (int) (power * power / 200), entity);
						}
						ProcedureAoeCommand.set(entity, 0d, power).exclude(entity).damageEntities(entity, (float) power).knockback(2f)
								.noGravity(false);
						ProcedureUtils.purgeHarmfulEffects((EntityLivingBase) entity);
						(entity).extinguish();
						cd_modifier = Chakra.getChakraModifier((EntityLivingBase) entity);
						entity.getEntityData().setDouble("shinratenseicd", ((NarutomodModVariables.world_tick) + (((power) * 10) * (cd_modifier))));
					}
					power = (double) 0;
				}
			}
			entity.getEntityData().setDouble("shinratensei_power", (power));
		} else {
			string = net.minecraft.util.text.translation.I18n.translateToLocal("chattext.cooldown");
			if (entity instanceof EntityPlayer && !entity.world.isRemote) {
				((EntityPlayer) entity).sendStatusMessage(
						new TextComponentString((((string)) + "" + (" ") + "" + ((new java.text.DecimalFormat(".1").format(
								((((entity.getEntityData().getDouble("shinratenseicd")) - (NarutomodModVariables.world_tick)) + 100) / 20)))))),
						(true));
			}
		}
	}
}
