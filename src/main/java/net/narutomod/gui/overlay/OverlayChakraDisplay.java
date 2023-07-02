
package net.narutomod.gui.overlay;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.Minecraft;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodMod;
import net.narutomod.PlayerTracker;
import net.narutomod.Chakra;

import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class OverlayChakraDisplay extends ElementsNarutomodMod.ModElement {
	private static OverlayChakraDisplay instance;
	private int warningTime;
	private boolean showSageBar;
	
	public OverlayChakraDisplay(ElementsNarutomodMod instanceIn) {
		super(instanceIn, 397);
		instance = this;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(WarningMessage.Handler.class, WarningMessage.class, Side.CLIENT);
		elements.addNetworkMessage(ShowFlamesMessage.Handler.class, ShowFlamesMessage.class, Side.CLIENT);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new GUIRenderEventClass());
	}

	public static void notEnoughChakraWarning(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			NarutomodMod.PACKET_HANDLER.sendTo(new WarningMessage(60), (EntityPlayerMP)player);
		} else {
			instance.warningTime = 60;
		}
	}

	public static class WarningMessage implements IMessage {
		int ticks;

		public WarningMessage() {
		}

		public WarningMessage(int t) {
			this.ticks = t;
		}

		public static class Handler implements IMessageHandler<WarningMessage, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(WarningMessage message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					instance.warningTime = message.ticks;
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.ticks);
		}

		public void fromBytes(ByteBuf buf) {
			this.ticks = buf.readInt();
		}
	}

	public static class ShowFlamesMessage implements IMessage {
		boolean show;

		public ShowFlamesMessage() {
		}

		public ShowFlamesMessage(boolean b) {
			this.show = b;
		}

		public static void send(EntityPlayerMP player, boolean b) {
			NarutomodMod.PACKET_HANDLER.sendTo(new ShowFlamesMessage(b), player);
		}

		public static class Handler implements IMessageHandler<ShowFlamesMessage, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(ShowFlamesMessage message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					instance.showSageBar = message.show;
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(this.show);
		}

		public void fromBytes(ByteBuf buf) {
			this.show = buf.readBoolean();
		}
	}

	public static class GUIRenderEventClass {
		@SubscribeEvent(priority = EventPriority.NORMAL)
		@SideOnly(Side.CLIENT)
		public void eventHandler(RenderGameOverlayEvent event) {
			if (!event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
				int sWidth = event.getResolution().getScaledWidth();
				int sHeight = event.getResolution().getScaledHeight();
				Minecraft mc = Minecraft.getMinecraft();
				EntityPlayer entity = mc.player;
				World world = entity.world;
				if (PlayerTracker.isNinja(entity) && Chakra.isInitialized(entity)) {
					int color = (instance.warningTime % 20 < 10) ? 0xFF00FFFF : 0xFFFF0000;
					Chakra.Pathway p = Chakra.pathway(entity);
					double d = p.getAmount() / p.getMax();
					double d1 = d - Math.floor(d);
					d1 = d != 0d && d1 == 0d ? 1d : d1;
					int bartop = sHeight - (4 * ((int)Math.ceil(d) - 1) + 9);
					int left = sWidth / 2 - 206;
					int w = 80;
					if (instance.showSageBar) {
						mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/flames_green.png"));
						int w1 = w +10;
						int h1 = sHeight - bartop + 20;
						int v = (entity.ticksExisted % 8) / 2;
						GuiIngame.drawModalRectWithCustomSizedTexture(left - 5, bartop - 20, 0f, (float)v * h1, w1, h1, w1, h1 * 4);
					}
					GuiIngame.drawRect(left - 1, bartop - 1, left + w + 1, sHeight - 5, 0xFF202020);
					for (int i = bartop; i <= sHeight - 9; i += 4) {
						GuiIngame.drawRect(left, i, left + (int)((i == bartop ? d1 : 1d) * w), i + 3, (i == sHeight - 9) ? color : 0xFFFFFF00);
					}
					String chakraText = String.format("%d/%d", (int)p.getAmount(), (int)p.getMax());
					int chakraTextLen = mc.fontRenderer.getStringWidth(chakraText);
					mc.fontRenderer.drawStringWithShadow(
					  chakraText, left + (80 / 2) - chakraTextLen / 2, bartop - 10, color);
				}
				if (instance.warningTime > 0)
					--instance.warningTime;
			}
		}
	}
}
