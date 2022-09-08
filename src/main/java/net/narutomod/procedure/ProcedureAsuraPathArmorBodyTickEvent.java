package net.narutomod.procedure;

import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAsuraPathArmorBodyTickEvent extends ElementsNarutomodMod.ModElement {
	public ProcedureAsuraPathArmorBodyTickEvent(ElementsNarutomodMod instance) {
		super(instance, 212);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure AsuraPathArmorBodyTickEvent!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure AsuraPathArmorBodyTickEvent!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure AsuraPathArmorBodyTickEvent!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		World world = (World) dependencies.get("world");
		double ticks_used = 0;
		ItemStack helmet = ItemStack.EMPTY;
		helmet = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
		if ((!(((helmet).getItem() == new ItemStack(ItemRinnegan.helmet, (int) (1)).getItem())
				|| ((helmet).getItem() == new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem())))) {
			itemstack.shrink(1);
			return;
		}
		ticks_used = (double) (((itemstack).hasTagCompound() ? (itemstack).getTagCompound().getDouble("ticks_used") : -1) + 1);
		{
			ItemStack _stack = (itemstack);
			if (!_stack.hasTagCompound())
				_stack.setTagCompound(new NBTTagCompound());
			_stack.getTagCompound().setDouble("ticks_used", (ticks_used));
		}
		if (((!(world.isRemote)) && (((ticks_used) % 40) == 1))) {
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, (int) 41, (int) 24, (false), (false)));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, (int) 41, (int) 16, (false), (false)));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, (int) 41, (int) 5, (false), (false)));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, (int) 41, (int) 5, (false), (false)));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SATURATION, (int) 41, (int) 0, (false), (false)));
		}
	}
}
