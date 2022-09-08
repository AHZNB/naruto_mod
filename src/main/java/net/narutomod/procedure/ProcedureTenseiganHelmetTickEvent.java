package net.narutomod.procedure;

import net.narutomod.potion.PotionFeatherFalling;
import net.narutomod.item.ItemTenseiganChakraMode;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.World;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureTenseiganHelmetTickEvent extends ElementsNarutomodMod.ModElement {
	public ProcedureTenseiganHelmetTickEvent(ElementsNarutomodMod instance) {
		super(instance, 693);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure TenseiganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure TenseiganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure TenseiganHelmetTickEvent!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		World world = (World) dependencies.get("world");
		ItemStack stack = ItemStack.EMPTY;
		if ((!(world.isRemote))) {
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionFeatherFalling.potion, (int) 60, (int) 5, (false), (false)));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, (int) 2, (int) 2, (false), (false)));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, (int) 2, (int) 4, (false), (false)));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, (int) 210, (int) 0, (false), (false)));
		}
		stack = (itemstack);
		if ((ItemTenseigan.canUseChakraMode(itemstack, (EntityPlayer) entity) && (!((entity instanceof EntityPlayer)
				? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemTenseiganChakraMode.block, (int) (1)))
				: false)))) {
			if (entity instanceof EntityPlayer) {
				ItemStack _setstack = new ItemStack(ItemTenseiganChakraMode.block, (int) (1));
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
			}
		}
	}
}
