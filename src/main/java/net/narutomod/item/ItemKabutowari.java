
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.EntityEquipmentSlot;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
/*
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import io.netty.buffer.ByteBuf;
*/

@ElementsNarutomodMod.ModElement.Tag
public class ItemKabutowari extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:kabutowari")
	public static final Item block = null;

	public ItemKabutowari(ElementsNarutomodMod instance) {
		super(instance, 677);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kabutowari", "inventory"));
	}

	//@Override
	//public void preInit(FMLPreInitializationEvent event) {
	//	this.elements.addNetworkMessage(Message.Handler.class, Message.class, Side.SERVER);
	//}
	
	//@SideOnly(Side.CLIENT)
	//@Override
	//public void init(FMLInitializationEvent event) {
	//	MinecraftForge.EVENT_BUS.register(this);
	//}

	public static class ItemCustom extends Item implements ItemOnBody.Interface {
		public ItemCustom() {
			setMaxDamage(0);
			maxStackSize = 1;
			setUnlocalizedName("kabutowari");
			setRegistryName("kabutowari");
			setCreativeTab(TabModTab.tab);
		}

		@Override
		public int getItemEnchantability() {
			return 0;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 0;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
			return 0F;
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.kabutowari.general"));
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int slot, boolean par5) {
			super.onUpdate(itemstack, world, entity, slot, par5);
			if (!world.isRemote && entity instanceof EntityPlayer) {
				EntityPlayer living = (EntityPlayer)entity;
				if (living.getHeldItemMainhand().equals(itemstack)) {
					living.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemKabutowariHammer.block));
					ProcedureUtils.swapItemToSlot(living, EntityEquipmentSlot.OFFHAND, new ItemStack(ItemKabutowariAxe.block));
				} else if (living.getHeldItemOffhand().equals(itemstack)) {
					living.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ItemKabutowariHammer.block));
					ProcedureUtils.swapItemToSlot(living, EntityEquipmentSlot.MAINHAND, new ItemStack(ItemKabutowariAxe.block));
				}
			}
		}
	}

	/*@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiContainerDrawn(GuiContainerEvent.DrawForeground event) {
		//GuiContainer container = event.getGuiContainer();
		InventoryPlayer inventory = Minecraft.getMinecraft().player.inventory;
		ItemStack heldstack = inventory.getItemStack();
		if (heldstack != null && (heldstack.getItem() == ItemKabutowariHammer.block
		 || heldstack.getItem() == ItemKabutowariAxe.block)) {
		 	inventory.setItemStack(new ItemStack(block));
		 	Message.sendToServer();
		}
	}

	public static class Message implements IMessage {
		public Message() {}

		public static void sendToServer() {
			NarutomodMod.PACKET_HANDLER.sendToServer(new Message());
		}
		
		public static class Handler implements IMessageHandler<Message, IMessage> {
			@Override
			public IMessage onMessage(Message message, MessageContext context) {
				EntityPlayerMP player = context.getServerHandler().player;
				player.getServerWorld().addScheduledTask(() -> {
					player.inventory.setItemStack(new ItemStack(block));
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
		}

		public void fromBytes(ByteBuf buf) {
		}
	}*/
}
