package net.narutomod.procedure;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureCameraShake extends ElementsNarutomodMod.ModElement {
	private static ProcedureCameraShake instance;
	private long shakeDuration;
	private float shakeScale;
	
	public ProcedureCameraShake(ElementsNarutomodMod instanceIn) {
		super(instanceIn, 191);
		instance = this;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onShake(EntityViewRenderEvent.CameraSetup event) {
		if (this.shakeDuration > Minecraft.getMinecraft().world.getTotalWorldTime()) {
			event.setYaw(((float) Math.random() - 0.5F) * instance.shakeScale + event.getYaw());
			event.setPitch(((float) Math.random() - 0.5F) * instance.shakeScale + event.getPitch());
			event.setRoll(((float) Math.random() - 0.5F) * instance.shakeScale + event.getRoll());
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(Message.Handler.class, Message.class, Side.CLIENT);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public static void sendToClients(int dimid, double x, double y, double z, double range, int duration, float scale) {
		NarutomodMod.PACKET_HANDLER.sendToAllAround(new Message(duration, scale), new NetworkRegistry.TargetPoint(dimid, x, y, z, range));
	}

	public static void sendToClient(EntityPlayer player, int duration, float scale) {
		if (player instanceof EntityPlayerMP) {
			NarutomodMod.PACKET_HANDLER.sendTo(new Message(duration, scale), (EntityPlayerMP)player);
		} else if (player.world.isRemote) {
			instance.shakeDuration = player.world.getTotalWorldTime() + duration;
			instance.shakeScale = scale;
		}
	}

	public static class Message implements IMessage {
		int duration;
		float scale;

		public Message() {
		}

		public Message(int dur, float sc) {
			this.duration = dur;
			this.scale = sc;
		}

		public static class Handler implements IMessageHandler<Message, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(Message message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					sendToClient(mc.player, message.duration, message.scale);
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.duration);
			buf.writeFloat(this.scale);
		}

		public void fromBytes(ByteBuf buf) {
			this.duration = buf.readInt();
			this.scale = buf.readFloat();
		}
	}
}
