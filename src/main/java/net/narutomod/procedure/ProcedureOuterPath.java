package net.narutomod.procedure;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.entity.EntityGedoStatue;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOuterPath extends ElementsNarutomodMod.ModElement {
	public ProcedureOuterPath(ElementsNarutomodMod instance) {
		super(instance, 253);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure OuterPath!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure OuterPath!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure OuterPath!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure OuterPath!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure OuterPath!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure OuterPath!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		double h = 0;
		boolean isJinchuriki = false;
		if (((!(world.isRemote)) && (!(is_pressed)))) {
			if ((PlayerTracker.getNinjaLevel((EntityPlayer) entity) >= 90)) {
				isJinchuriki = (boolean) EntityBijuManager.isJinchurikiOf((EntityPlayer) entity, EntityTenTails.EntityCustom.class);
				if ((EntityTenTails.getBijuManager().hasJinchuriki() && (!(isJinchuriki)))) {
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
								net.minecraft.util.text.translation.I18n.translateToLocal("chattext.outerpath.hasjinchuriki")), (false));
					}
					return;
				}
				if (entity instanceof EntityLivingBase) {
					((EntityLivingBase) entity).swingArm(EnumHand.MAIN_HAND);
				}
				EntityLivingBase entityToSpawn = EntityGedoStatue.getThisEntity(world);
				if (entityToSpawn != null) {
					(entityToSpawn).world.removeEntity(entityToSpawn);
				} else {
					entityToSpawn = EntityTenTails.getBijuManager().getEntityInWorld(world);
					if (entityToSpawn != null) {
						(entityToSpawn).world.removeEntity(entityToSpawn);
					} else if ((isJinchuriki)) {
						return;
					} else if (Chakra.pathway((EntityLivingBase) entity).consume(ItemRinnegan.getOuterPathChakraUsage((EntityLivingBase) entity))) {
						entityToSpawn = EntityGedoStatue.gotAll9Bijus() && EntityTenTails.getBijuManager().getHasLived()
								? new EntityTenTails.EntityCustom((EntityPlayer) entity)
								: new EntityGedoStatue.EntityCustom((EntityLivingBase) entity);
						entityToSpawn.rotationYawHead = entity.rotationYaw;
						entityToSpawn.setLocationAndAngles(x, world.getTopSolidOrLiquidBlock(new BlockPos(x, y, z)).getY(), z, entity.rotationYaw,
								0f);
						if (world.spawnEntity(entityToSpawn)) {
							entity.getEntityData().setDouble((NarutomodModVariables.InvulnerableTime), 100);
							world.playSound((EntityPlayer) null, x, y, z,
									(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
											.getObject(new ResourceLocation("narutomod:kuchiyosenojutsu")),
									SoundCategory.NEUTRAL, (float) 2, (float) 0.9);
							{
								MinecraftServer mcserv = FMLCommonHandler.instance().getMinecraftServerInstance();
								if (mcserv != null)
									mcserv.getPlayerList().sendMessage(new TextComponentString((((entity.getDisplayName().getUnformattedText())) + ""
											+ (" has summoned the ") + "" + ((entityToSpawn.getDisplayName().getUnformattedText())))));
							}
						}
					} else {
						if ((entity instanceof EntityPlayer)) {
							Chakra.pathway((EntityPlayer) entity).warningDisplay();
						}
						return;
					}
				}
			} else {
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer) entity).sendStatusMessage(
							new TextComponentString(net.minecraft.util.text.translation.I18n.translateToLocal("chattext.outerpath.notenoughxp")),
							(false));
				}
			}
		}
	}
}
