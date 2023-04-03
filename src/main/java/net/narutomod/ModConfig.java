package net.narutomod;

import net.minecraftforge.common.config.Config;

@Config(modid = NarutomodMod.MODID)
@ElementsNarutomodMod.ModElement.Tag
public class ModConfig extends ElementsNarutomodMod.ModElement {
    @Config.Comment("If enabled tailed beasts spawn naturally around the world.")
	public static boolean SPAWN_TAILED_BEASTS = true;

	@Config.Comment("If enabled players has a chance of spawning as jinchuriki.")
	public static boolean SPAWN_AS_JINCHURIKI = true;

	@Config.Comment("If enabled, rinnegan/tenseigan/ems gained without the prerequisite achievements will be removed.")
	public static boolean REMOVE_CHEAT_DOJUTSUS = false;

	@Config.Comment("Itachi's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_ITACHI = 5;

	@Config.Comment("Kisame's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_KISAME = 5;

	@Config.Comment("Zabuza's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_ZABUZA = 5;

	@Config.Comment("White zetsu's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_WHITEZETSU = 10;

	@Config.Comment("Whether or not bosses are aggressive on sight")
	public static boolean AGGRESSIVE_BOSSES = false;

	@Config.Comment("Stupid arms in the back Naruto run animation")
	public static boolean NARUTO_RUN = true;

	public ModConfig(ElementsNarutomodMod instance) {
		super(instance, 837);
	}
}
