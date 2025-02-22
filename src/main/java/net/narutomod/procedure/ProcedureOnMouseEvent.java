package net.narutomod.procedure;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.MouseEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.Minecraft;

import net.narutomod.item.ItemEightGates;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnMouseEvent extends ElementsNarutomodMod.ModElement {
	public ProcedureOnMouseEvent(ElementsNarutomodMod instance) {
		super(instance, 904);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(Message.Handler.class, Message.class, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMouseEvent(MouseEvent event) {
		if (FMLClientHandler.instance().isGUIOpen(GuiChat.class) || Minecraft.getMinecraft().player == null) {
			return;
		}
		EntityPlayer player = Minecraft.getMinecraft().player;
		// kamui intangible. dont allow clicks
		if (ProcedureOnLivingUpdate.isNoClip(player) && !ProcedureOnLivingUpdate.noClipAllowClicks(player)) {
			event.setCanceled(true);
			return;
		}
		if (player.getEntityData().getInteger("FearEffect") > 0 || ProcedureOnLivingUpdate.isMouseDisabled(player)) {
			event.setCanceled(true);
			return;
		}
		if (event.getButton() == 0 && event.isButtonstate()) {
			onLeftClick();
			return;
		}
		// hack
		if (event.getButton() == 1 && !event.isButtonstate()) {
			onRightRelease();
		}
	}

	@SideOnly(Side.CLIENT)
	private void onRightRelease() {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player.getHeldItemMainhand().getItem() == ItemEightGates.block
		 || player.getHeldItemOffhand().getItem() == ItemEightGates.block && player.isSneaking()) {
			Minecraft.getMinecraft().playerController.onStoppedUsingItem(player);
		}
	}

	@SideOnly(Side.CLIENT)
	private void onLeftClick() {
		Minecraft mc = Minecraft.getMinecraft();
		EntityLivingBase attacker = mc.player.isRiding() && mc.player.getRidingEntity() instanceof EntityLivingBase
		 ? (EntityLivingBase)mc.player.getRidingEntity() : mc.player;
		double reach = ProcedureUtils.getReachDistance(attacker);
		if (reach > 5.0D) {
			RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(mc.player, reach, 3.0d);
			if (rtr != null && rtr.entityHit != null
			 && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null || rtr.entityHit != mc.objectMouseOver.entityHit)) {
				NarutomodMod.PACKET_HANDLER.sendToServer(new Message(attacker.getEntityId(), rtr.entityHit.getEntityId()));
			}
		}
	}

	public static class Message implements IMessage {
		int iVar1;
		int iVar2;

		public Message() {
		}

		public Message(int var1, int var2) {
			this.iVar1 = var1;
			this.iVar2 = var2;
		}

		public static class Handler implements IMessageHandler<Message, IMessage> {
			@Override
			public IMessage onMessage(Message message, MessageContext context) {
				EntityPlayerMP entity = context.getServerHandler().player;
				entity.getServerWorld().addScheduledTask(() -> {
					World world = entity.world;
					if (!world.isBlockLoaded(new BlockPos(entity.posX, entity.posY, entity.posZ)))
						return;
					Entity source = world.getEntityByID(message.iVar1);
					Entity target = world.getEntityByID(message.iVar2);
					if (source != null && target != null) {
						if (source instanceof EntityPlayer) {
							((EntityPlayer) source).attackTargetEntityWithCurrentItem(target);
						} else if (source instanceof EntityLivingBase) {
							((EntityLivingBase) source).attackEntityAsMob(target);
						}
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.iVar1);
			buf.writeInt(this.iVar2);
		}

		public void fromBytes(ByteBuf buf) {
			this.iVar1 = buf.readInt();
			this.iVar2 = buf.readInt();
		}
	}
}
