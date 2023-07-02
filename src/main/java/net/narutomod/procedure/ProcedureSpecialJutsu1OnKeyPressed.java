package net.narutomod.procedure;

import net.narutomod.world.WorldKamuiDimension;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemByakugan;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.HashMap;

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
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SpecialJutsu1OnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		ItemStack helmet = ItemStack.EMPTY;
		if (((world.isRemote) || ((EntityPlayer) entity).isSpectator())) {
			return;
		}
		helmet = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
		if ((((helmet).getItem() == new ItemStack(ItemRinnegan.helmet, (int) (1)).getItem())
				|| ((helmet).getItem() == new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem()))) {
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("is_pressed", is_pressed);
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				ProcedureShinraTenseiOnKeyPressed.executeProcedure($_dependencies);
			}
		} else if ((((helmet).getItem() == new ItemStack(ItemMangekyoSharingan.helmet, (int) (1)).getItem())
				|| ((helmet).getItem() == new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)).getItem()))) {
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("is_pressed", is_pressed);
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				ProcedureAmaterasu.executeProcedure($_dependencies);
			}
		} else if (((helmet).getItem() == new ItemStack(ItemMangekyoSharinganObito.helmet, (int) (1)).getItem())) {
			if ((((world.provider.getDimension()) == (WorldKamuiDimension.DIMID)) && (!(entity.isSneaking())))) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("is_pressed", is_pressed);
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", world);
					ProcedureGrabEntity.executeProcedure($_dependencies);
				}
			} else {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("is_pressed", is_pressed);
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", world);
					$_dependencies.put("x", x);
					$_dependencies.put("y", y);
					$_dependencies.put("z", z);
					ProcedureKamuiJikukanIdo.executeProcedure($_dependencies);
				}
			}
		} else if (((helmet).getItem() == new ItemStack(ItemByakugan.helmet, (int) (1)).getItem())) {
			if ((entity.isSneaking())) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("is_pressed", is_pressed);
					$_dependencies.put("entity", entity);
					ProcedureHakkeKusho.executeProcedure($_dependencies);
				}
			} else {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("is_pressed", is_pressed);
					$_dependencies.put("entity", entity);
					$_dependencies.put("x", x);
					$_dependencies.put("y", y);
					$_dependencies.put("z", z);
					$_dependencies.put("world", world);
					ProcedureByakuganActivate.executeProcedure($_dependencies);
				}
			}
		}
	}
}
