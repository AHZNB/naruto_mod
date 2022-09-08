package net.narutomod.procedure;

import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemBijuCloak;
import net.narutomod.gui.overlay.OverlayByakuganView;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedurePowerIncreaseOnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedurePowerIncreaseOnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 104);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure PowerIncreaseOnKeyPressed!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure PowerIncreaseOnKeyPressed!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure PowerIncreaseOnKeyPressed!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double i = 0;
		ItemStack helmet = ItemStack.EMPTY;
		ItemStack itemmainhand = ItemStack.EMPTY;
		ItemStack itemoffhand = ItemStack.EMPTY;
		if ((!(world.isRemote))) {
			helmet = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
			itemmainhand = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY);
			itemoffhand = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemOffhand() : ItemStack.EMPTY);
			if (itemmainhand.getItem() instanceof ItemJutsu.Base) {
				if ((!(is_pressed))) {
					ItemJutsu.Base.switchNextJutsu(itemmainhand, (EntityLivingBase) entity);
				}
			} else if (itemoffhand.getItem() instanceof ItemJutsu.Base) {
				if ((!(is_pressed))) {
					ItemJutsu.Base.switchNextJutsu(itemoffhand, (EntityLivingBase) entity);
				}
			} else if ((((helmet).getItem() == new ItemStack(ItemByakugan.helmet, (int) (1)).getItem())
					&& (entity.getEntityData().getBoolean("byakugan_activated")))) {
				if ((is_pressed)) {
					entity.getEntityData().setDouble("byakugan_fov", ((entity.getEntityData().getDouble("byakugan_fov")) - 1));
					OverlayByakuganView.sendCustomData(entity, true, (float) entity.getEntityData().getDouble("byakugan_fov"));
				}
			} else if ((helmet.getItem() instanceof ItemSharingan.Base && entity.getRidingEntity() instanceof EntitySusanooBase)) {
				if ((!(is_pressed))) {
					ProcedureSusanoo.upgrade((EntityPlayer) entity);
				}
			} else if ((((helmet).getItem() == new ItemStack(ItemRinnegan.helmet, (int) (1)).getItem())
					|| ((helmet).getItem() == new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem()))) {
				if ((!(is_pressed))) {
					i = (double) (((helmet).hasTagCompound() ? (helmet).getTagCompound().getDouble("which_path") : -1) + 1);
					if (((i) > 5)) {
						i = (double) 0;
					}
					{
						ItemStack _stack = (helmet);
						if (!_stack.hasTagCompound())
							_stack.setTagCompound(new NBTTagCompound());
						_stack.getTagCompound().setDouble("which_path", (i));
					}
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
								net.minecraft.util.text.translation.I18n.translateToLocal(String.format("chattext.rinnegan.path%d", (int) i))),
								(true));
					}
				}
			} else if ((((helmet).getItem() == new ItemStack(ItemBijuCloak.helmet, (int) (1)).getItem())
					&& ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(2) : ItemStack.EMPTY)
							.getItem() == new ItemStack(ItemBijuCloak.body, (int) (1)).getItem())
							&& (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(1) : ItemStack.EMPTY)
									.getItem() == new ItemStack(ItemBijuCloak.legs, (int) (1)).getItem())))) {
				if ((!(is_pressed))) {
					EntityBijuManager.increaseCloakLevel((EntityPlayer) entity);
				}
			}
		}
	}
}
