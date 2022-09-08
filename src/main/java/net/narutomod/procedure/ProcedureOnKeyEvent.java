package net.narutomod.procedure;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.entity.Entity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.Minecraft;

import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnKeyEvent extends ElementsNarutomodMod.ModElement {
	public ProcedureOnKeyEvent(ElementsNarutomodMod instance) {
		super(instance, 341);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKey(InputEvent.KeyInputEvent event) {
		if (Minecraft.getMinecraft().currentScreen == null) {
			Entity entity = Minecraft.getMinecraft().player;
			if (entity.getEntityData().getInteger("FearEffect") > 0) {
				KeyBinding.unPressAllKeys();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
