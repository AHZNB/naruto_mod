package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureZzzRightClickedInAir extends ElementsNarutomodMod.ModElement {
	public ProcedureZzzRightClickedInAir(ElementsNarutomodMod instance) {
		super(instance, 715);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ZzzRightClickedInAir!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure ZzzRightClickedInAir!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ZzzRightClickedInAir!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		World world = (World) dependencies.get("world");
		double id = 0;
		(entity).extinguish();
		if ((!(world.isRemote))) {
			Entity entity1 = ProcedureUtils.objectEntityLookingAt(entity, 64d).entityHit;
			if (entity1 instanceof EntityLivingBase) {
				id = (double) ((itemstack).hasTagCompound() ? (itemstack).getTagCompound().getDouble("attackerID") : -1);
				if (((id) < 0)) {
					if ((entity1 instanceof EntityLiving)) {
						{
							ItemStack _stack = (itemstack);
							if (!_stack.hasTagCompound())
								_stack.setTagCompound(new NBTTagCompound());
							_stack.getTagCompound().setDouble("attackerID", entity1.getEntityId());
						}
						if (entity instanceof EntityPlayer && !entity.world.isRemote) {
							((EntityPlayer) entity).sendStatusMessage(
									new TextComponentString((("set attacker to ") + "" + ((entity1.getDisplayName().getUnformattedText())))),
									(false));
						}
					}
				} else {
					Entity attacker = entity.world.getEntityByID((int) id);
					if ((!(attacker instanceof EntityLiving))) {
						{
							ItemStack _stack = (itemstack);
							if (!_stack.hasTagCompound())
								_stack.setTagCompound(new NBTTagCompound());
							_stack.getTagCompound().setDouble("attackerID", (-1));
						}
					} else if (!attacker.equals(entity1)) {
						{
							ItemStack _stack = (itemstack);
							if (!_stack.hasTagCompound())
								_stack.setTagCompound(new NBTTagCompound());
							_stack.getTagCompound().setDouble("attackerID", (-1));
						}
						if (entity instanceof EntityPlayer && !entity.world.isRemote) {
							((EntityPlayer) entity).sendStatusMessage(
									new TextComponentString((("set target to ") + "" + ((entity1.getDisplayName().getUnformattedText())))), (false));
						}
						((EntityLiving) attacker).setAttackTarget((EntityLivingBase) entity1);
					}
				}
			}
		}
	}
}
