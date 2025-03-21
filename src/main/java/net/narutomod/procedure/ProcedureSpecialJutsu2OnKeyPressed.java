package net.narutomod.procedure;

import net.narutomod.item.ItemDojutsu;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSpecialJutsu2OnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureSpecialJutsu2OnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 66);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double which_path = 0;
		boolean f1 = false;
		String CTRL_pressed = "";
		ItemStack stack = ItemStack.EMPTY;
		ItemStack helmet = ItemStack.EMPTY;
		CTRL_pressed = (String) "CTRL_pressed";
		if (((EntityPlayer) entity).isSpectator()) {
			return;
		}
		entity.getEntityData().setBoolean((NarutomodModVariables.JutsuKey2Pressed), (is_pressed));
		if ((world.isRemote)) {
			return;
		}
		stack = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY);
		helmet = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
		if ((helmet.getItem() instanceof ItemDojutsu.Base
				&& ((ItemDojutsu.Base) helmet.getItem()).onJutsuKey2(is_pressed, helmet, (EntityPlayer) entity))) {
			return;
		} else if (EntityBijuManager.isJinchuriki((EntityPlayer) entity)) {
			if ((!(is_pressed))) {
				EntityBijuManager.toggleBijuCloak((EntityPlayer) entity);
			}
		}
	}
}
