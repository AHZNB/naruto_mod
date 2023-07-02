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

	@Config.Comment("Itachi's chance to be real (1~100). Lower value means higher chance. 1 means it will be real everytime.")
	public static int ITACHI_REAL_CHANCE = 10;

	@Config.Comment("Chakra regeneration rate. 0.006 means 0.6% of your max chakra every 4 seconds")
	public static float CHAKRA_REGEN_RATE = 0.006F;

	@Config.Comment("Disable this to not allow any jutsu scrolls in loot chests")
	public static boolean ENABLE_JUTSU_SCROLLS_IN_LOOTCHESTS = true;

	@Config.Comment("Amaterasu flame on block duration (reference: vanilla fire is 3)")
	public static int AMATERASU_BLOCK_DURATION = 100;

	public ModConfig(ElementsNarutomodMod instance) {
		super(instance, 837);
	}
}
