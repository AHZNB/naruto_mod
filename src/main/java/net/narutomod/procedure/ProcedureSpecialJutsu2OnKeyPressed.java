package net.narutomod.procedure;

import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemByakugan;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.HashMap;

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
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SpecialJutsu2OnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
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
		if ((world.isRemote)) {
			return;
		}
		entity.getEntityData().setBoolean((NarutomodModVariables.JutsuKey2Pressed), (is_pressed));
		stack = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY);
		helmet = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
		if ((((helmet).getItem() == new ItemStack(ItemRinnegan.helmet, (int) (1)).getItem())
				|| ((helmet).getItem() == new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem()))) {
			if ((!(is_pressed))) {
				which_path = (double) ((helmet).hasTagCompound() ? (helmet).getTagCompound().getDouble("which_path") : -1);
				if (((which_path) == 0)) {
					if ((entity.isSneaking())) {
						{
							Map<String, Object> $_dependencies = new HashMap<>();
							$_dependencies.put("entity", entity);
							$_dependencies.put("world", world);
							$_dependencies
									.put("x",
											(entity.world
													.rayTraceBlocks(entity.getPositionEyes(1f),
															entity.getPositionEyes(1f).addVector(entity.getLook(1f).x * 100,
																	entity.getLook(1f).y * 100, entity.getLook(1f).z * 100),
															false, false, true)
													.getBlockPos().getX()));
							$_dependencies
									.put("y",
											(entity.world
													.rayTraceBlocks(entity.getPositionEyes(1f),
															entity.getPositionEyes(1f).addVector(entity.getLook(1f).x * 100,
																	entity.getLook(1f).y * 100, entity.getLook(1f).z * 100),
															false, false, true)
													.getBlockPos().getY()));
							$_dependencies
									.put("z",
											(entity.world
													.rayTraceBlocks(entity.getPositionEyes(1f),
															entity.getPositionEyes(1f).addVector(entity.getLook(1f).x * 100,
																	entity.getLook(1f).y * 100, entity.getLook(1f).z * 100),
															false, false, true)
													.getBlockPos().getZ()));
							ProcedureMeteorStrike.executeProcedure($_dependencies);
						}
					} else {
						{
							Map<String, Object> $_dependencies = new HashMap<>();
							$_dependencies.put("entity", entity);
							$_dependencies.put("world", world);
							$_dependencies.put("x", x);
							$_dependencies.put("y", y);
							$_dependencies.put("z", z);
							ProcedureChibakuTenseiOnKeyPressed.executeProcedure($_dependencies);
						}
					}
				} else if (((which_path) == 4)) {
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("entity", entity);
						$_dependencies.put("world", world);
						ProcedureNarakaPath.executeProcedure($_dependencies);
					}
				} else if (((which_path) == 3)) {
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("entity", entity);
						$_dependencies.put("world", world);
						ProcedurePretaPath.executeProcedure($_dependencies);
					}
				} else if (((which_path) == 2)) {
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("entity", entity);
						$_dependencies.put("world", world);
						ProcedureAnimalPath.executeProcedure($_dependencies);
					}
				} else if (((which_path) == 5)) {
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("is_pressed", is_pressed);
						$_dependencies.put("entity", entity);
						$_dependencies.put("world", world);
						$_dependencies.put("x",
								(entity.world.rayTraceBlocks(entity.getPositionEyes(1f), entity.getPositionEyes(1f)
										.addVector(entity.getLook(1f).x * 5, entity.getLook(1f).y * 5, entity.getLook(1f).z * 5), false, false, true)
										.getBlockPos().getX()));
						$_dependencies.put("y", y);
						$_dependencies.put("z",
								(entity.world.rayTraceBlocks(entity.getPositionEyes(1f), entity.getPositionEyes(1f)
										.addVector(entity.getLook(1f).x * 5, entity.getLook(1f).y * 5, entity.getLook(1f).z * 5), false, false, true)
										.getBlockPos().getZ()));
						ProcedureOuterPath.executeProcedure($_dependencies);
					}
				}
			}
		} else if (((((helmet).getItem() == new ItemStack(ItemMangekyoSharingan.helmet, (int) (1)).getItem())
				|| ((helmet).getItem() == new ItemStack(ItemMangekyoSharinganObito.helmet, (int) (1)).getItem()))
				|| ((helmet).getItem() == new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)).getItem()))) {
			if ((!(is_pressed))) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", world);
					ProcedureSusanoo.executeProcedure($_dependencies);
				}
			}
		} else if (((helmet).getItem() == new ItemStack(ItemByakugan.helmet, (int) (1)).getItem())) {
			if ((!(is_pressed))) {
				if (((helmet).hasTagCompound() && (helmet).getTagCompound().getBoolean((NarutomodModVariables.RINNESHARINGAN_ACTIVATED)))) {
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("entity", entity);
						$_dependencies.put("world", world);
						ProcedureYomotsuHirasaka.executeProcedure($_dependencies);
					}
				} else {
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("entity", entity);
						$_dependencies.put("world", world);
						ProcedureEightTrigrams64Palms.executeProcedure($_dependencies);
					}
				}
			}
		} else if (EntityBijuManager.isJinchuriki((EntityPlayer) entity)) {
			if ((!(is_pressed))) {
				EntityBijuManager.toggleBijuCloak((EntityPlayer) entity);
			}
		}
		entity.getEntityData().setBoolean((CTRL_pressed), (false));
	}
}
