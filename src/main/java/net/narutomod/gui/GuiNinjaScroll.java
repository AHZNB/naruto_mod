
package net.narutomod.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Keyboard;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class GuiNinjaScroll extends ElementsNarutomodMod.ModElement {
	private static Map<Integer, GuiContainerMod> guiMap = Maps.<Integer, GuiContainerMod>newHashMap();

	public GuiNinjaScroll(ElementsNarutomodMod instance) {
		super(instance, 445);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(GUIButtonPressedMessage.Handler.class, GUIButtonPressedMessage.class, Side.SERVER);
	}

	public static abstract class GuiContainerMod extends Container implements Supplier<Map<Integer, Slot>> {
		private IInventory internal;
		private World world;
		private EntityPlayer entity;
		protected int x, y, z;
		private final int id;
		private Map<Integer, Slot> customSlots = new HashMap<>();

		public GuiContainerMod(World world, int x, int y, int z, EntityPlayer player, int guiID) {
			this.world = world;
			this.entity = player;
			this.x = x;
			this.y = y;
			this.z = z;
			this.internal = new InventoryBasic("", true, 0);
			this.id = guiID;
			guiMap.put(guiID, this);
		}

		public Map<Integer, Slot> get() {
			return customSlots;
		}

		@Override
		public boolean canInteractWith(EntityPlayer player) {
			return this.internal.isUsableByPlayer(player);
		}

		@Override
		public void onContainerClosed(EntityPlayer playerIn) {
			super.onContainerClosed(playerIn);
			if ((this.internal instanceof InventoryBasic) && (playerIn instanceof EntityPlayerMP)) {
				this.clearContainer(playerIn, playerIn.world, this.internal);
			}
		}

		protected void handleButtonAction(EntityPlayer player, int buttonID) {
			if (player instanceof EntityPlayerMP) {
				if (player.getHeldItemMainhand().getMaxDamage() == 1) {
					player.getHeldItemMainhand().shrink(1);
				} else if (player.getHeldItemOffhand().getMaxDamage() == 1) {
					player.getHeldItemOffhand().shrink(1);
				}
				if (!ProcedureUtils.advancementAchieved((EntityPlayerMP)player, "narutomod:learned_1st_jutsu")) {
					ProcedureUtils.grantAdvancement((EntityPlayerMP)player, "narutomod:learned_1st_jutsu", true);
				}
			}
		}
	}

	//private static int guiId = 0;

	@SideOnly(Side.CLIENT)
	public static abstract class GuiWindow extends GuiContainer {
		private final int id;
		protected World world;
		protected int x, y, z;
		protected EntityPlayer entity;

		public GuiWindow(GuiContainerMod container) {
			super(container);
			this.world = container.world;
			this.x = container.x;
			this.y = container.y;
			this.z = container.z;
			this.entity = container.entity;
			this.xSize = 176;
			this.ySize = 166;
			this.id = container.id;
			//guiMap.put(this.id, this);
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			this.drawDefaultBackground();
			super.drawScreen(mouseX, mouseY, partialTicks);
			this.renderHoveredToolTip(mouseX, mouseY);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.zLevel = 0.0F;
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/scoll_screen.png"));
			Gui.drawModalRectWithCustomSizedTexture(this.guiLeft - 70, this.guiTop - 70, 0f, 0f, 320, 320, 320f, 320f);
		}

		@Override
		public void updateScreen() {
			super.updateScreen();
		}

		@Override
		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		@Override
		protected void keyTyped(char typedChar, int keyCode) throws IOException {
			super.keyTyped(typedChar, keyCode);
		}

		@Override
		public void onGuiClosed() {
			super.onGuiClosed();
			Keyboard.enableRepeatEvents(false);
		}

		@Override
		public void initGui() {
			super.initGui();
			this.guiLeft = (this.width - 176) / 2;
			this.guiTop = (this.height - 166) / 2;
			Keyboard.enableRepeatEvents(true);
			this.buttonList.clear();
			this.buttonList.add(new GuiButton(0, this.guiLeft - 56, this.guiTop + 127, 39, 20, "Learn"));
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new GUIButtonPressedMessage(this.id, button.id));
			//this.handleButtonAction(this.entity, button.id);
			guiMap.get(this.id).handleButtonAction(this.entity, button.id);
		}

		@Override
		public boolean doesGuiPauseGame() {
			return false;
		}
	}

	public static ItemStack enableJutsu(EntityPlayer player, ItemJutsu.Base item, ItemJutsu.JutsuEnum jutsu, boolean enable) {
		ItemStack stack = ProcedureUtils.getMatchingItemStack(player, item);
		if (stack == null && PlayerTracker.isNinja(player) && enable) {
			stack = new ItemStack(item, 1);
			((ItemJutsu.Base)stack.getItem()).setOwner(stack, player);
			ItemHandlerHelper.giveItemToPlayer(player, stack);
		}
		if (stack != null) {
			((ItemJutsu.Base)stack.getItem()).enableJutsu(stack, jutsu, enable);
		}
		return stack;
	}

	public static class GUIButtonPressedMessage implements IMessage {
		int guiWindow, buttonID;
	
		public GUIButtonPressedMessage() {
		}
	
		public GUIButtonPressedMessage(int win, int button) {
			this.guiWindow = win;
			this.buttonID = button;
		}

		public static class Handler implements IMessageHandler<GUIButtonPressedMessage, IMessage> {
			@Override
			public IMessage onMessage(GUIButtonPressedMessage message, MessageContext context) {
				EntityPlayerMP entity = context.getServerHandler().player;
				entity.getServerWorld().addScheduledTask(() -> {
					if (guiMap.containsKey(message.guiWindow)) {
						guiMap.get(message.guiWindow).handleButtonAction(entity, message.buttonID);
					}
				});
				return null;
			}
		}

		@Override
		public void toBytes(io.netty.buffer.ByteBuf buf) {
			buf.writeInt(this.guiWindow);
			buf.writeInt(this.buttonID);
		}
	
		@Override
		public void fromBytes(io.netty.buffer.ByteBuf buf) {
			this.guiWindow = buf.readInt();
			this.buttonID = buf.readInt();
		}
	}
}
