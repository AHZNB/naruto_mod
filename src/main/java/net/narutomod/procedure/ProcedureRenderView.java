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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayerMP;

import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureRenderView extends ElementsNarutomodMod.ModElement {
	private static ProcedureRenderView instance;
	private long shouldChangeColor = 0;
	private long shouldChangeDensity = 0;
	private long changeFOV = 0;
	private float newRed;
	private float newGreen;
	private float newBlue;
	private float newDensity;
	private float newFOV;
	
	public ProcedureRenderView(ElementsNarutomodMod instanceIn) {
		super(instanceIn, 387);
		instance = this;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(Message.Handler.class, Message.class, Side.CLIENT);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onChangeColor(EntityViewRenderEvent.FogColors event) {
		if (this.shouldChangeColor > Minecraft.getMinecraft().world.getTotalWorldTime()) {
			event.setRed(this.newRed);
			event.setGreen(this.newGreen);
			event.setBlue(this.newBlue);
			//--this.shouldChangeColor;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onChangeDensity(EntityViewRenderEvent.FogDensity event) {
		long l = Minecraft.getMinecraft().world.getTotalWorldTime();
		if (this.shouldChangeDensity > l) {
			GlStateManager.setFog(GlStateManager.FogMode.EXP);
			event.setDensity(this.newDensity);
			event.setCanceled(true);
			//--this.shouldChangeDensity;
		} else if (l < this.shouldChangeDensity + 40) {
			GlStateManager.setFog(GlStateManager.FogMode.EXP);
			event.setDensity(this.newDensity * (this.shouldChangeDensity + 40 - l) / 40f);
			event.setCanceled(true);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onChangeFOV(EntityViewRenderEvent.FOVModifier event) {
		if (this.changeFOV > Minecraft.getMinecraft().world.getTotalWorldTime()) {
			event.setFOV(this.newFOV);
			//--this.changeFOV;
		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof EntityPlayerMP) {
			NarutomodMod.PACKET_HANDLER.sendTo(new Message(0, 0, 0, 0, 0, 0, 0, 0), (EntityPlayerMP)event.getEntity());
		}
	}

	@SubscribeEvent
	public void onChangeDimemsion(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.player instanceof EntityPlayerMP) {
			NarutomodMod.PACKET_HANDLER.sendTo(new Message(0, 0, 0, 0, 0, 0, 0, 0), (EntityPlayerMP)event.player);
		}
	}

	public static void setFogColor(int dimid, double x, double y, double z, double range, boolean change, float r, float g, float b) {
		//NarutomodMod.PACKET_HANDLER.sendToAllAround(new Message(change, false, r, g, b, 0), new NetworkRegistry.TargetPoint(dimid, x, y, z, range));
		changeFog(dimid, x, y, z, range, change, false, r, g, b, 0);
	}

	public static void setFogColor(Entity entity, double range, boolean change, float r, float g, float b) {
		setFogColor(entity.dimension, entity.posX, entity.posY, entity.posZ, range, change, r, g, b);
	}

	public static void setFogDensity(int dimid, double x, double y, double z, double range, boolean change, float den) {
		changeFog(dimid, x, y, z, range, false, change, 0, 0, 0, den);
	}

	public static void setFogDensity(Entity entity, double range, boolean change, float den) {
		setFogDensity(entity.dimension, entity.posX, entity.posY, entity.posZ, range, change, den);
	}
	
	public static void setFogDensity(Entity entity, float den) {
		setFogDensity(entity, den, 0x7FFFFFFF);
	}

	public static void setFogDensity(Entity entity, float den, int ticks) {
		sendToPlayer(entity, ticks, -1, den, 0);
	}

	public static void setFOV(int dimid, double x, double y, double z, double range, int ticks, float fov) {
		sendToClients(dimid, x, y, z, range, 0, 0, ticks, 0f, 0f, 0f, 0f, fov);
	}

	public static void setFOV(Entity entity, int ticks, float fov) {
		sendToPlayer(entity, -1, ticks, 0, fov);
	}
	
	public static void changeFog(int dimid, double x, double y, double z, double range, boolean color, boolean density, float r, float g, float b, float den) {
		sendToClients(dimid, x, y, z, range, color?0x7FFFFFFF:0, density?0x7FFFFFFF:0, -1, r, g, b, den, 0f);
	}

	public static void sendToClients(int dimid, double x, double y, double z, double range, int colorticks, int densityticks, int fovticks, float r, float g, float b, float den, float fov) {
		NarutomodMod.PACKET_HANDLER.sendToAllAround(new Message(colorticks, densityticks, fovticks, r, g, b, den, fov), new NetworkRegistry.TargetPoint(dimid, x, y, z, range));
	}

	public static void sendToPlayer(Entity entity, int densityticks, int fovticks, float den, float fov) {
		if (entity instanceof EntityPlayerMP) {
			NarutomodMod.PACKET_HANDLER.sendTo(new Message(-1, densityticks, fovticks, 0, 0, 0, den, fov), (EntityPlayerMP)entity);
		}
	}

	public static void sendToPlayer(Entity entity, int cticks, int dticks, float r, float g, float b, float den) {
		if (entity instanceof EntityPlayerMP) {
			NarutomodMod.PACKET_HANDLER.sendTo(new Message(cticks, dticks, -1, r, g, b, den, 0f), (EntityPlayerMP)entity);
		}
	}

	public static void changeFog(Entity entity, double range, boolean color, boolean density, float r, float g, float b, float den) {
		changeFog(entity.dimension, entity.posX, entity.posY, entity.posZ, range, color, density, r, g, b, den);
	}
	
	public static void changeFog(Entity entity, double range, int cticks, int dticks, float r, float g, float b, float den) {
		sendToClients(entity.dimension, entity.posX, entity.posY, entity.posZ, range, cticks, dticks, -1, r, g, b, den, 0f);
	}

	public static class Message implements IMessage {
		int fogColor;
		int fogDensity;
		int fovTicks;
		float red;
		float green;
		float blue;
		float density;
		float fov;
		
		public Message() {
		}

		public Message(int colorticks, int densityticks, int fovticks, float r, float g, float b, float den, float f) {
			this.fogColor = colorticks;
			this.fogDensity = densityticks;
			this.fovTicks = fovticks;
			this.red = r;
			this.green = g;
			this.blue = b;
			this.density = den;
			this.fov = f;
		}

		public static class Handler implements IMessageHandler<Message, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(Message message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					if (message.fogColor >= 0) {
						instance.shouldChangeColor = mc.world.getTotalWorldTime() + message.fogColor;
						instance.newRed = message.red;
						instance.newGreen = message.green;
						instance.newBlue = message.blue;
					}
					if (message.fogDensity >= 0) {
						instance.shouldChangeDensity = mc.world.getTotalWorldTime() + message.fogDensity;
						instance.newDensity = message.density;
					}
					if (message.fovTicks >= 0) {
						instance.changeFOV = mc.world.getTotalWorldTime() + message.fovTicks;
						instance.newFOV = message.fov;
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.fogColor);
			buf.writeInt(this.fogDensity);
			buf.writeInt(this.fovTicks);
			buf.writeFloat(this.red);
			buf.writeFloat(this.green);
			buf.writeFloat(this.blue);
			buf.writeFloat(this.density);
			buf.writeFloat(this.fov);
		}

		public void fromBytes(ByteBuf buf) {
			this.fogColor = buf.readInt();
			this.fogDensity = buf.readInt();
			this.fovTicks = buf.readInt();
			this.red = buf.readFloat();
			this.green = buf.readFloat();
			this.blue = buf.readFloat();
			this.density = buf.readFloat();
			this.fov = buf.readFloat();
		}
	}
}
