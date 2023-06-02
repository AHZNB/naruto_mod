package net.narutomod.procedure;

import net.narutomod.item.ItemByakugan;
import net.narutomod.entity.EntityHakkeshoKeiten;
import net.narutomod.PlayerTracker;
import net.narutomod.Particles;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureHakkeshoKaiten extends ElementsNarutomodMod.ModElement {
	public ProcedureHakkeshoKaiten(ElementsNarutomodMod instance) {
		super(instance, 263);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure HakkeshoKaiten!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure HakkeshoKaiten!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure HakkeshoKaiten!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		boolean f1 = false;
		double cooldown = 0;
		double ticksExisted = 0;
		ItemStack helmetstack = ItemStack.EMPTY;
		helmetstack = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
		if (((helmetstack).hasTagCompound() && (helmetstack).getTagCompound().getBoolean((NarutomodModVariables.RINNESHARINGAN_ACTIVATED)))) {
			cooldown = (double) (entity.getEntityData().getDouble("press_time"));
			if ((is_pressed)) {
				if (((cooldown) < 200)) {
					entity.getEntityData().setDouble("press_time", ((cooldown) + 1));
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer) entity).sendStatusMessage(new TextComponentString((("Power: ") + "" + (((cooldown) / 2)))), (true));
					}
				}
			} else {
				world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
						(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:dojutsu")),
						SoundCategory.NEUTRAL, (float) 1, (float) 1);
				for (int index0 = 0; index0 < (int) (1000); index0++) {
					Particles.spawnParticle(world, Particles.Types.SMOKE, entity.posX, entity.posY + 1.4d, entity.posZ, 1, 1d, 0d, 1d,
							ProcedureUtils.rngGaussian(), 1d, ProcedureUtils.rngGaussian(), 0x10FFFFFF, 30, 0);
				}
				ProcedureAoeCommand.set(entity, 0d, cooldown / 2).exclude(entity).knockback(3f);
				ProcedureUtils.purgeHarmfulEffects((EntityLivingBase) entity);
				(entity).extinguish();
				entity.getEntityData().setDouble("press_time", 0);
			}
		} else {
			f1 = ProcedureUtils.isOriginalOwner((EntityPlayer) entity, helmetstack);
			if ((!(((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)
					|| ((f1) && (PlayerTracker.getBattleXp((EntityPlayer) entity) >= 1500))))) {
				return;
			}
			if ((!(world.isRemote))) {
				if ((is_pressed)) {
					if (!(entity.getRidingEntity() instanceof EntityHakkeshoKeiten.EntityCustom)) {
						cooldown = (double) ((helmetstack).hasTagCompound() ? (helmetstack).getTagCompound().getDouble("HakkeshoKaitenCD") : -1);
						if ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)
								|| (((NarutomodModVariables.world_tick) > (cooldown))
										|| ((NarutomodModVariables.world_tick) < ((cooldown) - 6000))))) {
							if ((Chakra.pathway((EntityPlayer) entity).getAmount() >= ItemByakugan.getKaitenChakraUsage((EntityLivingBase) entity))) {
								world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
										(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
												.getObject(new ResourceLocation("narutomod:HakkeshoKaiten")),
										SoundCategory.NEUTRAL, (float) 1, (float) 1);
								entity.world.spawnEntity(new EntityHakkeshoKeiten.EntityCustom((EntityPlayer) entity));
							}
						} else {
							if ((entity instanceof EntityPlayer)) {
								((EntityPlayer) entity).sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted",
										(int) ((cooldown - NarutomodModVariables.world_tick) / 20)), true);
							}
						}
					}
				} else {
					Entity entitySpawned = entity.getRidingEntity();
					if (entitySpawned instanceof EntityHakkeshoKeiten.EntityCustom) {
						entitySpawned.setDead();
					}
				}
			}
		}
	}
}
