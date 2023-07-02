package net.narutomod.procedure;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.entity.EntityChibakuTenseiBall;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureChibakuTenseiOnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureChibakuTenseiOnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 29);
	}

	public static void executeProcedure(java.util.Map<String, Object> dependencies) {
		if (!(dependencies.get("entity") instanceof EntityLivingBase)) {
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
		EntityLivingBase entity = (EntityLivingBase) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");

		Chakra.Pathway cp = Chakra.pathway(entity);
		if ((entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative())
		 || (world.getTotalWorldTime() > entity.getEntityData().getLong("chibakutenseicd")
		     && cp.consume(ItemRinnegan.getChibaukutenseiChakraUsage(entity)))) {
			entity.getEntityData().setLong("chibakutenseicd", world.getTotalWorldTime() + 6000);
			world.playSound(null, x, y, z, net.minecraft.util.SoundEvent.REGISTRY
			 .getObject(new ResourceLocation("narutomod:ChibakuTensei")), SoundCategory.NEUTRAL, 10f, 1f);
			world.spawnEntity(new EntityChibakuTenseiBall.EntityCustom(entity));
		} else {
			if (cp.getAmount() < ItemRinnegan.getChibaukutenseiChakraUsage(entity)) {
				cp.warningDisplay();
			} else {
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer)entity).sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted",
					 (entity.getEntityData().getLong("chibakutenseicd") - world.getTotalWorldTime()) / 20), true);
				}
			}
		}
	}
}
