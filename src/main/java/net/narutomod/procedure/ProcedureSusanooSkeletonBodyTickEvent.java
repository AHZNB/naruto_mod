package net.narutomod.procedure;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSusanooSkeletonBodyTickEvent extends ElementsNarutomodMod.ModElement {
	public ProcedureSusanooSkeletonBodyTickEvent(ElementsNarutomodMod instance) {
		super(instance, 167);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure SusanooSkeletonBodyTickEvent!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SusanooSkeletonBodyTickEvent!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		ItemStack helmet = ItemStack.EMPTY;
		entity.getEntityData().setDouble("susanoo_ticks", ((entity.getEntityData().getDouble("susanoo_ticks")) + 1));
		if ((!(((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false) || ((entity instanceof EntityPlayer)
				? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemRinnegan.helmet, (int) (1)))
				: false)))) {
			if ((!(world.isRemote))) {
				Entity entitySpawned = world.getEntityByID(ProcedureSusanoo.getSummonedSusanooId(entity));
				if ((entitySpawned == null || !(entitySpawned instanceof EntitySusanooBase) || !entitySpawned.isEntityAlive()
						|| ((entity.getEntityData().getDouble("susanoo_ticks")) > 820))) {
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("entity", entity);
						$_dependencies.put("world", world);
						ProcedureSusanoo.executeProcedure($_dependencies);
					}
				}
			}
		}
	}
}
