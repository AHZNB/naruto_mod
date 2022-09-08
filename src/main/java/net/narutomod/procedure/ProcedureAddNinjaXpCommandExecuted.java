package net.narutomod.procedure;

import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.world.WorldServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAddNinjaXpCommandExecuted extends ElementsNarutomodMod.ModElement {
	public ProcedureAddNinjaXpCommandExecuted(ElementsNarutomodMod instance) {
		super(instance, 578);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure AddNinjaXpCommandExecuted!");
			return;
		}
		if (dependencies.get("cmdparams") == null) {
			System.err.println("Failed to load dependency cmdparams for procedure AddNinjaXpCommandExecuted!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		HashMap cmdparams = (HashMap) dependencies.get("cmdparams");
		double xp = 0;
		String username = "";
		String param1 = "";
		if (cmdparams.values().size() < 2) {
			if (entity instanceof EntityPlayer && !entity.world.isRemote) {
				((EntityPlayer) entity).sendStatusMessage(new TextComponentString("/addninjaxp <target> <integer>"), (false));
			}
		}
		username = (String) (new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText());
		EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(username);
		if (player != null) {
			if ((((player instanceof EntityPlayerMP) && ((player).world instanceof WorldServer))
					? ((EntityPlayerMP) player).getAdvancements()
							.getProgress(((WorldServer) (player).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:ninjaachievement")))
							.isDone()
					: false)) {
				param1 = (String) (new Object() {
					public String getText() {
						String param = (String) cmdparams.get("1");
						if (param != null) {
							return param;
						}
						return "";
					}
				}.getText());
				xp = (double) Double.parseDouble(param1);
				PlayerTracker.addBattleXp(player, xp);
			} else {
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer) entity).sendStatusMessage(
							new TextComponentString((((player.getDisplayName().getUnformattedText())) + "" + (" is not a ninja."))), (false));
				}
			}
		} else {
			if (entity instanceof EntityPlayer && !entity.world.isRemote) {
				((EntityPlayer) entity).sendStatusMessage(new TextComponentString((("No player found with user name ") + "" + ((username)))),
						(false));
			}
		}
	}
}
