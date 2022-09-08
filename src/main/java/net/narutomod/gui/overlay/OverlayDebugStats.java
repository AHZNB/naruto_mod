
/*package net.narutomod.gui.overlay;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;

import net.narutomod.EntityTracker;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class OverlayDebugStats extends ElementsNarutomodMod.ModElement {
	public OverlayDebugStats(ElementsNarutomodMod instance) {
		super(instance, 537);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new GUIRenderEventClass());
	}
	public static class GUIRenderEventClass {
		@SubscribeEvent(priority = EventPriority.NORMAL)
		@SideOnly(Side.CLIENT)
		public void eventHandler(RenderGameOverlayEvent event) {
			if (!event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
				int posX = (event.getResolution().getScaledWidth()) / 2;
				int posY = (event.getResolution().getScaledHeight()) / 2;
				EntityPlayer entity = Minecraft.getMinecraft().player;
				World world = entity.world;
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				if (true) {
					Minecraft.getMinecraft().fontRenderer.drawString("Tracking:"+EntityTracker.trackingTotal(), posX + -32, posY + -110, -1);
				}
			}
		}
	}
}
*/
