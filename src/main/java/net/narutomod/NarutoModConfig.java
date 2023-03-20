package net.narutomod;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.World;

import java.util.Random;

@Config(modid = NarutomodMod.MODID)
@ElementsNarutomodMod.ModElement.Tag
public class NarutoModConfig extends ElementsNarutomodMod.ModElement {
    @Config.Comment("If enabled tailed beasts spawn naturally around the world.")
	public static boolean SPAWN_TAILED_BEASTS = false;

	public NarutoModConfig(ElementsNarutomodMod instance) {
		super(instance, 837);
	}
}
