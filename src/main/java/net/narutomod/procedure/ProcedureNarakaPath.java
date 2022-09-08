package net.narutomod.procedure;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import java.util.UUID;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureNarakaPath extends ElementsNarutomodMod.ModElement {
	public ProcedureNarakaPath(ElementsNarutomodMod instance) {
		super(instance, 223);
	}

	public static void executeProcedure(java.util.Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ProcedureNarakaPath!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ProcedureNarakaPath!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).swingArm(EnumHand.MAIN_HAND);
		}
		if (!world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer living = (EntityPlayer)entity;
			ItemStack stack = living.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			if (stack.getItem() == ItemRinnegan.helmet || stack.getItem() == ItemTenseigan.helmet) {
				UUID entity_id = ProcedureUtils.getUniqueId(stack, "KoH_id");
				if (entity_id == null) {
					if (Chakra.pathway(living).consume(ItemRinnegan.getNarakaPathChakraUsage(living))) {
						EntityKingOfHell.EntityCustom entityToSpawn = new EntityKingOfHell.EntityCustom(living);
						entity.world.spawnEntity(entityToSpawn);
						stack.getTagCompound().setUniqueId("KoH_id", entityToSpawn.getUniqueID());
					}
				} else {
					Entity entitySpawned = ((WorldServer)entity.world).getEntityFromUuid(entity_id);
					if (entitySpawned instanceof EntityKingOfHell.EntityCustom) {
						((EntityLivingBase) entitySpawned).setHealth(0.0F);
					}
					ProcedureUtils.removeUniqueIdTag(stack, "KoH_id");
				}
			}
		}
	}
}
