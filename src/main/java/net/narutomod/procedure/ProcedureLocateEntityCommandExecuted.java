package net.narutomod.procedure;

import net.narutomod.entity.EntityTwoTails;
import net.narutomod.entity.EntityThreeTails;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.entity.EntitySixTails;
import net.narutomod.entity.EntitySevenTails;
import net.narutomod.entity.EntityOneTail;
import net.narutomod.entity.EntityNineTails;
import net.narutomod.entity.EntityFourTails;
import net.narutomod.entity.EntityFiveTails;
import net.narutomod.entity.EntityEightTails;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureLocateEntityCommandExecuted extends ElementsNarutomodMod.ModElement {
	public ProcedureLocateEntityCommandExecuted(ElementsNarutomodMod instance) {
		super(instance, 255);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure LocateEntityCommandExecuted!");
			return;
		}
		if (dependencies.get("cmdparams") == null) {
			System.err.println("Failed to load dependency cmdparams for procedure LocateEntityCommandExecuted!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		HashMap cmdparams = (HashMap) dependencies.get("cmdparams");
		boolean f1 = false;
		String string = "";
		double x = 0;
		double y = 0;
		double z = 0;
		double tailnum = 0;
		Vec3d vec3d = null;
		if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("TenTails"))) {
			vec3d = EntityTenTails.getBijuManager().locateEntity();
			string = (String) "Ten Tails";
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("NineTails"))) {
			vec3d = EntityNineTails.getBijuManager().locateEntity();
			string = (String) "Nine Tails";
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("EightTails"))) {
			vec3d = EntityEightTails.getBijuManager().locateEntity();
			string = (String) "Eight Tails";
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("SevenTails"))) {
			vec3d = EntitySevenTails.getBijuManager().locateEntity();
			string = (String) "Seven Tails";
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("SixTails"))) {
			vec3d = EntitySixTails.getBijuManager().locateEntity();
			string = (String) "Six Tails";
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("FiveTails"))) {
			vec3d = EntityFiveTails.getBijuManager().locateEntity();
			string = (String) "Five Tails";
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("FourTails"))) {
			vec3d = EntityFourTails.getBijuManager().locateEntity();
			string = (String) "Four Tails";
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("ThreeTails"))) {
			vec3d = EntityThreeTails.getBijuManager().locateEntity();
			string = (String) "Three Tails";
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("TwoTails"))) {
			vec3d = EntityTwoTails.getBijuManager().locateEntity();
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("OneTail"))) {
			vec3d = EntityOneTail.getBijuManager().locateEntity();
			string = (String) "One Tail";
		} else if ((((new Object() {
			public String getText() {
				String param = (String) cmdparams.get("0");
				if (param != null) {
					return param;
				}
				return "";
			}
		}.getText())).equals("jinchuriki"))) {
			if ((((new Object() {
				public String getText() {
					String param = (String) cmdparams.get("1");
					if (param != null) {
						return param;
					}
					return "";
				}
			}.getText())).equals("list"))) {
				List<String> list = EntityBijuManager.listJinchuriki();
				for (int index0 = 0; index0 < (int) (list.size()); index0++) {
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer) entity).sendStatusMessage(new TextComponentString(list.get(index0)), (false));
					}
				}
			} else if ((((new Object() {
				public String getText() {
					String param = (String) cmdparams.get("1");
					if (param != null) {
						return param;
					}
					return "";
				}
			}.getText())).equals("revoke"))) {
				if ((((new Object() {
					public String getText() {
						String param = (String) cmdparams.get("2");
						if (param != null) {
							return param;
						}
						return "";
					}
				}.getText())).equals("all"))) {
					EntityBijuManager.revokeAllJinchuriki();
				} else {
					tailnum = (double) new Object() {
						int convert(String s) {
							try {
								return Integer.parseInt(s.trim());
							} catch (Exception e) {
							}
							return 0;
						}
					}.convert((new Object() {
						public String getText() {
							String param = (String) cmdparams.get("2");
							if (param != null) {
								return param;
							}
							return "";
						}
					}.getText()));
					if ((((tailnum) >= 1) && ((tailnum) <= 10))) {
						EntityBijuManager.revokeJinchurikiByTails((int) tailnum);
					}
				}
			} else {
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
							"Usage: /locateEntity <TenTails | NineTails | EightTails | SevenTails | SixTails | FiveTails | FourTails | ThreeTails | TwoTails | OneTail | jinchuriki {list | revoke {all | [num]}}>"),
							(false));
				}
			}
			return;
		} else {
			if (entity instanceof EntityPlayer && !entity.world.isRemote) {
				((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
						"Usage: /locateEntity <TenTails | NineTails | EightTails | SevenTails | SixTails | FiveTails | FourTails | ThreeTails | TwoTails | OneTail | jinchuriki {list | revoke {all | [num]}}>"),
						(false));
			}
			return;
		}
		if (vec3d != null) {
			string = (String) (((string)) + "" + (" entity last known position at: ") + "" + (Math.floor(vec3d.x)) + "" + (", ") + ""
					+ (Math.floor(vec3d.y)) + "" + (", ") + "" + (Math.floor(vec3d.z)));
		} else {
			string = (String) "Entity not found";
		}
		if (entity instanceof EntityPlayer && !entity.world.isRemote) {
			((EntityPlayer) entity).sendStatusMessage(new TextComponentString((string)), (false));
		}
	}
}
