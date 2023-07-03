
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;

import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import io.netty.buffer.ByteBuf;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class ItemOnBody extends ElementsNarutomodMod.ModElement {
	private static Vec3d RIGHT_LEG_OFFSET = new Vec3d(0.125d, -0.6875d, 0d);
	private static Vec3d LEFT_LEG_OFFSET = new Vec3d(-0.125d, -0.6875d, 0d);
	private static Vec3d RIGHT_ARM_OFFSET = new Vec3d(0.3125d, -0.125d, 0d);
	private static Vec3d LEFT_ARM_OFFSET = new Vec3d(-0.3125d, -0.125d, 0d);
	
	public ItemOnBody(ElementsNarutomodMod instance) {
		super(instance, 711);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(InventoryTracker.Message.Handler.class, InventoryTracker.Message.class, Side.CLIENT);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new InventoryTracker.TrackingHook());
	}

	public interface Interface {
		default Vec3d getOffset() {
			switch (this.showOnBody()) {
				case RIGHT_ARM:
					return RIGHT_ARM_OFFSET;
				case RIGHT_LEG:
					return RIGHT_LEG_OFFSET;
				case LEFT_ARM:
					return LEFT_ARM_OFFSET;
				case LEFT_LEG:
					return LEFT_LEG_OFFSET;
				case HEAD:
				case TORSO:
				default:
					return Vec3d.ZERO;
			}
		}
		default boolean showSkinLayer() { return false; }
		default BodyPart showOnBody() { return BodyPart.TORSO; }
	}

	public enum BodyPart {
		NONE,
		HEAD, 
		TORSO, 
		RIGHT_ARM, 
		LEFT_ARM, 
		RIGHT_LEG, 
		LEFT_LEG;
	}

	public static class InventoryTracker {
		private static final Map<Integer, InventoryTracker> TRACKERMAP = Maps.newHashMap();
		private final EntityPlayer player;
		private final Map<Integer, ItemStack> slotMap = Maps.newHashMap();
		private int currentItem;
		private boolean forceUpdate;

		public static void createOrSyncInventory(EntityPlayerMP playerIn) {
			getOrCreate(playerIn).sync();
		}

		public static InventoryTracker getOrCreate(EntityPlayer entity) {
			InventoryTracker inventoryTracker = TRACKERMAP.get(Integer.valueOf(entity.getEntityId()));
			if (inventoryTracker == null) {
				inventoryTracker = new InventoryTracker(entity);
			}
			return inventoryTracker;
		}

		public static void clearEmpties() {
			Iterator<InventoryTracker> iter = TRACKERMAP.values().iterator();
			while (iter.hasNext()) {
				InventoryTracker it = iter.next();
				if (it.slotMap.isEmpty()) {
					iter.remove();
				}
			}
		}

		private InventoryTracker(EntityPlayer playerIn) {
			this.player = playerIn;
			TRACKERMAP.put(Integer.valueOf(playerIn.getEntityId()), this);
		}

		private boolean needsUpdate() {
			boolean update = false;
			for (int i = 0; i < this.player.inventory.getSizeInventory(); ++i) {
				ItemStack stack1 = this.player.inventory.getStackInSlot(i);
				ItemStack stack2 = this.slotMap.get(Integer.valueOf(i));
				boolean flag = stack2 != null && ItemStack.areItemStacksEqual(stack1, stack2);
				if (!flag && (stack1.getItem() instanceof Interface || (stack2 != null && stack2.getItem() instanceof Interface))) {
					this.slotMap.put(Integer.valueOf(i), stack1);
					update = true;
				} else if (flag && !(stack2.getItem() instanceof Interface)) {
					this.slotMap.remove(Integer.valueOf(i));
				}
			}
			if (!this.slotMap.isEmpty() && (this.forceUpdate || this.currentItem != this.player.inventory.currentItem)) {
				this.slotMap.put(Integer.valueOf(this.currentItem), this.player.inventory.mainInventory.get(this.currentItem));
				this.slotMap.put(Integer.valueOf(this.player.inventory.currentItem), this.player.inventory.getCurrentItem());
				update = true;
			}
			this.currentItem = this.player.inventory.currentItem;
			return update;
		}

		private void sync() {
			if (this.needsUpdate()) {
//System.out.println("+++ from:"+player.getName()+", cur:"+currentItem+", "+slotMap);
				NarutomodMod.PACKET_HANDLER.sendToAllTracking(new Message(this), this.player);
				this.forceUpdate = false;
			}
		}

		private void forceUpdate() {
			this.forceUpdate = true;
		}

		public static class Message implements IMessage {
			int id;
			int cur;
			int mapsize;
			Map<Integer, ItemStack> map;
	
			public Message() { }
	
			public Message(InventoryTracker invtracker) {
				this.id = invtracker.player.getEntityId();
				this.cur = invtracker.currentItem;
				this.mapsize = invtracker.slotMap.size();
				this.map = invtracker.slotMap;
			}
	
			public static class Handler implements IMessageHandler<Message, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(Message message, MessageContext context) {
					Minecraft mc = Minecraft.getMinecraft();
					mc.addScheduledTask(() -> {
						Entity entity = mc.world.getEntityByID(message.id);
						if (entity instanceof EntityOtherPlayerMP) {
							EntityOtherPlayerMP othermp = (EntityOtherPlayerMP)entity;
//System.out.println("+++ from:"+entity+" to:"+mc.player.getName()+", cur:"+message.cur+", "+message.map+", old:"+othermp.inventory.currentItem+", "+othermp.inventory.mainInventory);
							if (othermp.inventory.currentItem == 0 && message.cur != 0) {
								ItemStack stack = message.map.get(0);
								othermp.inventory.setInventorySlotContents(0, stack != null ? stack : ItemStack.EMPTY);
							}
							othermp.inventory.currentItem = message.cur;
							for (Map.Entry<Integer, ItemStack> entry : message.map.entrySet()) {
								othermp.inventory.setInventorySlotContents(entry.getKey(), entry.getValue());
							}
						}
					});
					return null;
				}
			}
	
			public void toBytes(ByteBuf buf) {
				PacketBuffer pbuf = new PacketBuffer(buf);
				pbuf.writeInt(this.id);
				pbuf.writeInt(this.cur);
				pbuf.writeInt(this.mapsize);
				for (Map.Entry<Integer, ItemStack> entry : this.map.entrySet()) {
					pbuf.writeInt(entry.getKey());
					pbuf.writeItemStack(entry.getValue());
				}
			}
	
			public void fromBytes(ByteBuf buf) {
				PacketBuffer pbuf = new PacketBuffer(buf);
				this.id = pbuf.readInt();
				this.cur = pbuf.readInt();
				this.mapsize = pbuf.readInt();
				this.map = Maps.newHashMap();
				try {
					for (int i = 0; i < this.mapsize; i++) {
						int j = pbuf.readInt();
						ItemStack stack = pbuf.readItemStack();
						this.map.put(Integer.valueOf(j), stack);
					}
				} catch (Exception e) {
					new IOException("Inventory sync: ", e);
				}
			}
		}

		public static class TrackingHook {
			@SubscribeEvent
			public void onTracking(PlayerEvent.StartTracking event) {
				if (event.getTarget() instanceof EntityPlayerMP) {
					getOrCreate(event.getEntityPlayer()).forceUpdate();
				}
			}

			/*@SubscribeEvent
			public void onEquipmentChange(LivingEquipmentChangeEvent event) {
				if (event.getEntityLiving() instanceof EntityPlayerMP && event.getSlot().getSlotType() == EntityEquipmentSlot.Type.HAND) {
					createOrSyncInventory((EntityPlayerMP)event.getEntityLiving());
				}
			}*/

			@SubscribeEvent
			public void onJoin(EntityJoinWorldEvent event) {
				if (event.getEntity() instanceof EntityPlayerMP) {
					TRACKERMAP.remove(Integer.valueOf(event.getEntity().getEntityId()));
				}
			}
		}
	}
}
