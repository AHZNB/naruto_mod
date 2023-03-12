package net.narutomod.procedure;

import net.minecraft.util.text.TextComponentTranslation;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.entity.EntityChibakuTenseiBall;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.text.DecimalFormat;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureChibakutenseiOnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureChibakutenseiOnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 29);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ChibakutenseiOnKeyPressed!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure ChibakutenseiOnKeyPressed!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure ChibakutenseiOnKeyPressed!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure ChibakutenseiOnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ChibakutenseiOnKeyPressed!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		String string = "";
		double size_x = 0;
		double size_y = 0;
		double size_z = 0;
		double chakraAmount = 0;
		chakraAmount = Chakra.pathway((EntityPlayer) entity).getAmount();
		if ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)
				|| (((chakraAmount) >= ItemRinnegan.getChibaukutenseiChakraUsage((EntityLivingBase) entity))
						&& (world.getTotalWorldTime() > entity.getEntityData().getLong("chibakutenseicd"))))) {
			entity.getEntityData().setLong("chibakutenseicd", world.getTotalWorldTime() + 6000);
			world.playSound((EntityPlayer) null, x, y, z,
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ChibakuTensei")),
					SoundCategory.NEUTRAL, (float) 10, (float) 1);
			Entity entityToSpawn = new EntityChibakuTenseiBall.EntityCustom((EntityLivingBase) entity);
			world.spawnEntity(entityToSpawn);
			Chakra.pathway((EntityPlayer) entity).consume(ItemRinnegan.getChibaukutenseiChakraUsage((EntityLivingBase) entity));
		} else {
			if (((chakraAmount) < ItemRinnegan.getChibaukutenseiChakraUsage((EntityLivingBase) entity))) {
				Chakra.pathway((EntityPlayer) entity).warningDisplay();
			} else {
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer) entity)
							.sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", new DecimalFormat(".1")
									.format((entity.getEntityData().getLong("chibakutenseicd") - world.getTotalWorldTime()) / 20)), true);
				}
			}
		}
	}
}
