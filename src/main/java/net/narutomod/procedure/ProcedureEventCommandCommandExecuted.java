package net.narutomod.procedure;

import net.narutomod.event.EventVillageSiege;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;

import java.util.Map;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureEventCommandCommandExecuted extends ElementsNarutomodMod.ModElement {
	public ProcedureEventCommandCommandExecuted(ElementsNarutomodMod instance) {
		super(instance, 830);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("cmdparams") == null) {
			System.err.println("Failed to load dependency cmdparams for procedure EventCommandCommandExecuted!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure EventCommandCommandExecuted!");
			return;
		}
		HashMap cmdparams = (HashMap) dependencies.get("cmdparams");
		World world = (World) dependencies.get("world");
		double d = 0;
		double d1 = 0;
		double d2 = 0;
		double d3 = 0;
		double d4 = 0;
		d = (double) new Object() {
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
		if (((!(world.isRemote)) && ((d) == 4))) {
			d1 = (double) new Object() {
				int convert(String s) {
					try {
						return Integer.parseInt(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert((new Object() {
				public String getText() {
					String param = (String) cmdparams.get("1");
					if (param != null) {
						return param;
					}
					return "";
				}
			}.getText()));
			d2 = (double) new Object() {
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
			d3 = (double) new Object() {
				int convert(String s) {
					try {
						return Integer.parseInt(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert((new Object() {
				public String getText() {
					String param = (String) cmdparams.get("3");
					if (param != null) {
						return param;
					}
					return "";
				}
			}.getText()));
			d4 = (double) new Object() {
				int convert(String s) {
					try {
						return Integer.parseInt(s.trim());
					} catch (Exception e) {
					}
					return 0;
				}
			}.convert((new Object() {
				public String getText() {
					String param = (String) cmdparams.get("4");
					if (param != null) {
						return param;
					}
					return "";
				}
			}.getText()));
			new EventVillageSiege(world, null, (int) d1, (int) d2, (int) d3, 0, (int) d4, 80);
		}
	}
}
