package net.narutomod.procedure;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.World;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

//import net.narutomod.item.ItemExpandedTruthSeekerBall;
//import net.narutomod.item.ItemEightyGodsKusho;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemAshBones;
import net.narutomod.gui.overlay.OverlayByakuganView;
//import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import java.util.HashMap;
import net.minecraft.util.text.TextComponentString;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureByakuganHelmetTickEvent extends ElementsNarutomodMod.ModElement {
	//private static final double CHAKRA_USAGE = 10d; // per half second

	public ProcedureByakuganHelmetTickEvent(ElementsNarutomodMod instance) {
		super(instance, 99);
	}

	public static void executeProcedure(HashMap<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure MCreatorByakuganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure MCreatorByakuganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure MCreatorByakuganHelmetTickEvent!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		//entity.getEntityData().setDouble(NarutomodModVariables.MostRecentWornDojutsuTime, NarutomodModVariables.world_tick);
		if (entity instanceof EntityLivingBase && !world.isRemote) {
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 210, 0, false, false));
		}
		if (entity.getEntityData().getBoolean("byakugan_activated")) {
			float fov = (float) entity.getEntityData().getDouble("byakugan_fov");
			if (fov < 1.0F || fov > 110.0F) {
				entity.getEntityData().setDouble("byakugan_fov", 110.0D);
				OverlayByakuganView.sendCustomData(entity, true, 110.0F);
			}
			if (entity instanceof EntityPlayer && entity.ticksExisted % 10 == 0) {
				Chakra.pathway((EntityPlayer)entity).consume(ItemByakugan.getByakuganChakraUsage((EntityLivingBase)entity));
					//ProcedureUtils.isOriginalOwner((EntityLivingBase)entity, itemstack) ? CHAKRA_USAGE : (CHAKRA_USAGE*2));
			}
		}
		/*if (ItemByakugan.isRinnesharinganActivated(itemstack)) {
			//if (((EntityPlayer) entity).experienceLevel < 100)
			//	((EntityPlayer) entity).experienceLevel = 100;
			if (!world.isRemote) {
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 2, 4, false, false));
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SATURATION, 2, 1, false, false));
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 2, 5, false, false));
			}
			((EntityPlayer) entity).capabilities.isFlying = true;
			if (!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemEightyGodsKusho.block))
				ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, new ItemStack(ItemEightyGodsKusho.block));
			if (!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemAshBones.block))
				ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, new ItemStack(ItemAshBones.block));
			if (!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemExpandedTruthSeekerBall.block))
				ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entity, new ItemStack(ItemExpandedTruthSeekerBall.block));
		}*/
//((EntityPlayer) entity).sendStatusMessage(new TextComponentString("uuid: "+entity.getUniqueID().toString()), true);
	}
}
