package net.narutomod.procedure;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

//import net.narutomod.item.ItemEightGates;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import io.netty.buffer.ByteBuf;
import java.util.List;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnLeftClickEmpty extends ElementsNarutomodMod.ModElement {
	private static final List<Item> qualifiedItemMainhand = Lists.newArrayList();
	private static final List<Item> qualifiedItemOffhand = Lists.newArrayList();

	public ProcedureOnLeftClickEmpty(ElementsNarutomodMod instance) {
		super(instance, 320);
	}

	@SubscribeEvent
	public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
		if (qualifiedItemMainhand.contains(event.getEntityPlayer().getHeldItemMainhand().getItem()))
			NarutomodMod.PACKET_HANDLER.sendToServer(new Message(true));
		else if (qualifiedItemOffhand.contains(event.getEntityPlayer().getHeldItemOffhand().getItem()))
			NarutomodMod.PACKET_HANDLER.sendToServer(new Message(false));
	}

	public static void addQualifiedItem(Item item, EnumHand hand) {
		if (hand == EnumHand.MAIN_HAND && !qualifiedItemMainhand.contains(item)) {
			qualifiedItemMainhand.add(item);
		} else if (hand == EnumHand.OFF_HAND && !qualifiedItemOffhand.contains(item)) {
			qualifiedItemOffhand.add(item);
		}
	}

	public static void removeQualifiedItem(Item item, EnumHand hand) {
		if (hand == EnumHand.MAIN_HAND) {
			qualifiedItemMainhand.remove(item);
		} else if (hand == EnumHand.OFF_HAND) {
			qualifiedItemOffhand.remove(item);
		}
	}

	public static class Message implements IMessage {
		boolean isMainHand;

		public Message() {
		}

		public Message(boolean mainHand) {
			this.isMainHand = mainHand;
		}

		public static class Handler implements IMessageHandler<Message, IMessage> {
			@Override
			public IMessage onMessage(Message message, MessageContext context) {
				EntityPlayerMP entity = context.getServerHandler().player;
				entity.getServerWorld().addScheduledTask(() -> {
					ItemStack stack = message.isMainHand ? entity.getHeldItemMainhand() : entity.getHeldItemOffhand();
					stack.getItem().onLeftClickEntity(stack, entity, entity);
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(this.isMainHand);
		}

		public void fromBytes(ByteBuf buf) {
			this.isMainHand = buf.readBoolean();
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(Message.Handler.class, Message.class, Side.SERVER);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

}
