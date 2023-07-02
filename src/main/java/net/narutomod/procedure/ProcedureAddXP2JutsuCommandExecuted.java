package net.narutomod.procedure;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemEightGates;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAddXP2JutsuCommandExecuted extends ElementsNarutomodMod.ModElement {
	public ProcedureAddXP2JutsuCommandExecuted(ElementsNarutomodMod instance) {
		super(instance, 559);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure AddXP2JutsuCommandExecuted!");
			return;
		}
		if (dependencies.get("cmdparams") == null) {
			System.err.println("Failed to load dependency cmdparams for procedure AddXP2JutsuCommandExecuted!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		HashMap cmdparams = (HashMap) dependencies.get("cmdparams");
		double xp2add = 0;
		ItemStack itemmainhand = ItemStack.EMPTY;
		ItemStack itemoffhand = ItemStack.EMPTY;
		String playerName = "";
		xp2add = (double) new Object() {
			int convert(String s) {
				try {
					return Integer.parseInt(s.trim());
				} catch (Exception e) {
				}
				return 0;
			}
		}.convert((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText()));
		playerName = (String) (new Object() {
			public String getText() {
				String param = (String) cmdparams.get("1");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText());
		EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
		if (player != null) {
			entity = player;
		}
		if (((((playerName)).equals("")) || player != null)) {
			itemmainhand = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY);
			itemoffhand = ((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemOffhand() : ItemStack.EMPTY);
			if ((EntityBijuManager.cloakLevel((EntityPlayer) entity) > 0)) {
				EntityBijuManager.addCloakXp((EntityPlayer) entity, (int) xp2add);
			} else if (((itemmainhand).getItem() == new ItemStack(ItemEightGates.block, (int) (1)).getItem())) {
				ItemEightGates.addBattleXP((EntityPlayer) entity, (int) xp2add);
			} else if (itemmainhand.getItem() instanceof ItemJutsu.Base) {
				ItemJutsu.addBattleXP((EntityPlayer) entity, (int) xp2add);
			} else if (((itemoffhand).getItem() == new ItemStack(ItemEightGates.block, (int) (1)).getItem())) {
				ItemEightGates.addBattleXP((EntityPlayer) entity, (int) xp2add);
			} else if (itemoffhand.getItem() instanceof ItemJutsu.Base) {
				ItemJutsu.addBattleXP((EntityPlayer) entity, (int) xp2add);
			}
		} else {
			if (entity instanceof EntityPlayer && !entity.world.isRemote) {
				((EntityPlayer) entity).sendStatusMessage(new TextComponentString((("No player found with user name  ") + "" + ((playerName)))),
						(false));
			}
		}
	}
}
