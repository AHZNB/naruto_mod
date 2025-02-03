
package net.narutomod.command;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.entity.EntityGedoStatue;
import net.narutomod.item.ItemSharingan;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureLocateEntityCommandExecuted;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommand;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class CommandModCommands extends ElementsNarutomodMod.ModElement {
	public CommandModCommands(ElementsNarutomodMod instance) {
		super(instance, 908);
	}

	@Override
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandHandler());
	}

	public static class CommandHandler implements ICommand {
		@Override
		public int compareTo(ICommand c) {
			return getName().compareTo(c.getName());
		}

		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender var1) {
			return var1.canUseCommand(4, this.getName());
		}

		@Override
		public List getAliases() {
			return new ArrayList();
		}

		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if (args.length == 1) {
				return Level1.getAllCommands();
			} else if (args.length == 2 && args[0].equals(Level1.LOCATE.toString())) {
				return LocateLevel2.getAllCommands();
			} else if (args.length == 2 && args[0].equals(Level1.JINCHURIKI.toString())) {
				return JinchurikiLevel2.getAllCommands();
			} else if (args.length == 3 && args[1].equals(JinchurikiLevel2.ASSIGN.toString())) {
				return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
			}
			return new ArrayList();
		}

		@Override
		public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override
		public String getName() {
			return "shinobicraftcmd";
		}

		@Override
		public String getUsage(ICommandSender var1) {
			return "/shinobicraftcmd <"+Level1.getAllCommandsFormatted()+">";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) {
			int x = sender.getPosition().getX();
			int y = sender.getPosition().getY();
			int z = sender.getPosition().getZ();
			Entity entity = sender.getCommandSenderEntity();
			if (entity instanceof EntityPlayer) {
				if (cmd.length < 1) {
					ProcedureUtils.sendChat((EntityPlayer)entity, this.getUsage(sender));
				} else if (cmd[0].equals(Level1.LOCATE.toString())) {
					String string = "Entity not found";
					if (cmd.length < 2) {
						string = "Usage: /shinobicraftcmd "+cmd[0]+" <"+LocateLevel2.getAllCommandsFormatted()+">";
					} else if (cmd[1].equals(LocateLevel2.GEDO.toString())) {
						Entity gedoEntity = EntityGedoStatue.getThisEntity();
						if (gedoEntity != null) {
							string = "The gedomazo's last known position at: dimension[" + "" + gedoEntity.dimension + "" + "] pos:["
							 + "" + gedoEntity.posX + ", " + gedoEntity.posY + ", " + gedoEntity.posZ + "" + "]";
						}
					} else if (cmd[1].equals(LocateLevel2.BIJU.toString())) {
						if (cmd.length < 3) {
							string = "Usage: /shinobicraftcmd "+cmd[0]+" "+cmd[1]+" <tails number>";
						} else {
							int tails = Integer.parseInt(cmd[2].trim());
							BlockPos pos = EntityBijuManager.getPositionByTails(tails);
							if (pos != null) {
								string = "The " + tails + " tails last known position at: (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
							}
						}
					}
					if (!entity.world.isRemote) {
						ProcedureUtils.sendChat((EntityPlayer)entity, string);
					}
				} else if (cmd[0].equals(Level1.JINCHURIKI.toString())) {
					World world = entity.world;
					HashMap<String, String> cmdparams = new HashMap<>();
					int[] index = {0};
					Arrays.stream(cmd).forEach(param -> {
						cmdparams.put(Integer.toString(index[0]), param);
						index[0]++;
					});
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("entity", entity);
						$_dependencies.put("cmdparams", cmdparams);
						ProcedureLocateEntityCommandExecuted.executeProcedure($_dependencies);
					}
				} else if (cmd[0].equals(Level1.SUSANOOCOLOR.toString())) {
					ItemStack stack = ((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
					if (stack.getItem() instanceof ItemSharingan.Base) {
						int color = new Random().nextInt();
						if (cmd.length >= 2) {
							color = Integer.parseInt(cmd[1].trim());
						}
						((ItemSharingan.Base)stack.getItem()).setColor(stack, color);
					} else if (!entity.world.isRemote) {
						ProcedureUtils.sendChat((EntityPlayer)entity, ""+cmd[0]+" failed: must be wearing a sharingan");
					}
				}
			}
		}
	}

	public enum Level1 {
		LOCATE("locateEntity"),
		JINCHURIKI("jinchuriki"),
		SUSANOOCOLOR("susanooColor"),
		UNKNOWN;

		private final String argString;
		private static final Map<String, Level1> COMMANDS = Maps.newHashMap();

		static {
			for (Level1 cmd : values()) {
				if (cmd.argString != null) {
					COMMANDS.put(cmd.argString, cmd);
				}
			}
		}

		Level1() {
			this.argString = null;
		}
		
		Level1(String str) {
			this.argString = str;
		}

		public String toString() {
			return this.argString;
		}

		public static Level1 getTypeFromString(String str) {
			return COMMANDS.get(str);
		}

		public static List<String> getAllCommands() {
			List<String> list = Lists.<String>newArrayList();
			list.addAll(COMMANDS.keySet());
			return list;
		}

		public static String getAllCommandsFormatted() {
			return String.join(" | ", COMMANDS.keySet());
		}
	}

	public enum LocateLevel2 {
		BIJU("biju"),
		GEDO("gedo"),
		UNKNOWN;
		
		private final String argString;
		private static final Map<String, LocateLevel2> COMMANDS = Maps.newHashMap();

		static {
			for (LocateLevel2 cmd : values()) {
				if (cmd.argString != null) {
					COMMANDS.put(cmd.argString, cmd);
				}
			}
		}

		LocateLevel2() {
			this.argString = null;
		}
		
		LocateLevel2(String str) {
			this.argString = str;
		}

		public String toString() {
			return this.argString;
		}

		public static LocateLevel2 getTypeFromString(String str) {
			return COMMANDS.get(str);
		}

		public static List<String> getAllCommands() {
			List<String> list = Lists.<String>newArrayList();
			list.addAll(COMMANDS.keySet());
			return list;
		}

		public static String getAllCommandsFormatted() {
			return String.join(" | ", COMMANDS.keySet());
		}
	}

	public enum JinchurikiLevel2 {
		LIST("list"),
		REVOKE("revoke"),
		ASSIGN("assign"),
		UNKNOWN;
		
		private final String argString;
		private static final Map<String, JinchurikiLevel2> COMMANDS = Maps.newHashMap();

		static {
			for (JinchurikiLevel2 cmd : values()) {
				if (cmd.argString != null) {
					COMMANDS.put(cmd.argString, cmd);
				}
			}
		}

		JinchurikiLevel2() {
			this.argString = null;
		}
		
		JinchurikiLevel2(String str) {
			this.argString = str;
		}

		public String toString() {
			return this.argString;
		}

		public static JinchurikiLevel2 getTypeFromString(String str) {
			return COMMANDS.get(str);
		}

		public static List<String> getAllCommands() {
			List<String> list = Lists.<String>newArrayList();
			list.addAll(COMMANDS.keySet());
			return list;
		}

		public static String getAllCommandsFormatted() {
			return String.join(" | ", COMMANDS.keySet());
		}
	}
}
