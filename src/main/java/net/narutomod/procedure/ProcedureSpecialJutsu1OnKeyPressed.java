package net.narutomod.procedure;

import net.narutomod.item.ItemDojutsu;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSpecialJutsu1OnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureSpecialJutsu1OnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 64);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		ItemStack helmet = ItemStack.EMPTY;
		entity.getEntityData().setBoolean((NarutomodModVariables.JutsuKey1Pressed), (is_pressed));
		if (((world.isRemote) || ((EntityPlayer) entity).isSpectator())) {
			return;
		}
		helmet = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
		if (helmet.getItem() instanceof ItemDojutsu.Base) {
			((ItemDojutsu.Base) helmet.getItem()).onJutsuKey1(is_pressed, helmet, (EntityPlayer) entity);
		}
	}
}
