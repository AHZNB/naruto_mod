
package net.narutomod.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Keyboard;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemTeamScroll;
import net.narutomod.procedure.ProcedureTeamManagerJoin;
import net.narutomod.procedure.ProcedureTeamManagerLeave;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

import java.util.Collection;

@ElementsNarutomodMod.ModElement.Tag
public class GuiTeamManager extends ElementsNarutomodMod.ModElement {
	public static int GUIID = 41;
	public static HashMap guistate = new HashMap();

	public GuiTeamManager(ElementsNarutomodMod instance) {
		super(instance, 553);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(GUIButtonPressedMessage.Handler.class, GUIButtonPressedMessage.class, Side.SERVER);
		elements.addNetworkMessage(GUISlotChangedMessage.Handler.class, GUISlotChangedMessage.class, Side.SERVER);
	}
	
	public static class GuiContainerMod extends Container implements Supplier<Map<Integer, Slot>> {
		private IInventory internal;
		private World world;
		private EntityPlayer entity;
		private int x, y, z;
		private Map<Integer, Slot> customSlots = new HashMap<>();

		public GuiContainerMod(World world, int x, int y, int z, EntityPlayer player) {
			this.world = world;
			this.entity = player;
			this.x = x;
			this.y = y;
			this.z = z;
			this.internal = new InventoryBasic("", true, 0);
		}

		public Map<Integer, Slot> get() {
			return customSlots;
		}

		@Override
		public boolean canInteractWith(EntityPlayer player) {
			return internal.isUsableByPlayer(player);
		}

		@Override
		public void onContainerClosed(EntityPlayer playerIn) {
			super.onContainerClosed(playerIn);
			if ((internal instanceof InventoryBasic) && (playerIn instanceof EntityPlayerMP)) {
				this.clearContainer(playerIn, playerIn.world, internal);
			}
		}

		private void slotChanged(int slotid, int ctype, int meta) {
			if (this.world != null && this.world.isRemote) {
				NarutomodMod.PACKET_HANDLER.sendToServer(new GUISlotChangedMessage(slotid, x, y, z, ctype, meta));
				handleSlotAction(entity, slotid, ctype, meta, x, y, z);
			}
		}
	}

	public static class GuiWindow extends GuiContainer {
		private World world;
		private int x, y, z;
		private EntityPlayer entity;
		//GuiTextField teamName;
		private GuiButton joinButton;
		private GuiButton leaveButton;

		public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
			super(new GuiContainerMod(world, x, y, z, entity));
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			this.entity = entity;
			this.xSize = 257;
			this.ySize = 156;
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			this.drawDefaultBackground();
			super.drawScreen(mouseX, mouseY, partialTicks);
			this.renderHoveredToolTip(mouseX, mouseY);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			GL11.glColor4f(1, 1, 1, 1);
			zLevel = 0.0F;
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/scroll_empty.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 1, this.guiTop + -50, 0, 0, 256, 256, 256, 256);
		}

		@Override
		public void updateScreen() {
			super.updateScreen();
			//this.teamName.updateCursorCounter();
			ItemStack helditem = this.entity.getHeldItemMainhand();
			if (helditem.getItem() == ItemTeamScroll.block) {
				Collection<String> names = ItemTeamScroll.ItemCustom.getTeamMembers(this.world, helditem);
				this.joinButton.enabled = names.size() < 3 && !names.contains(this.entity.getName());
				this.leaveButton.enabled = names.contains(this.entity.getName());
				//if (!this.teamName.getText().equals(ItemTeamScroll.ItemCustom.getTeamDisplayName(this.world, helditem))) {
				//	ItemTeamScroll.ItemCustom.setTeamDisplayName(this.world, helditem, this.teamName.getText());
				//}
			} else {
				this.joinButton.enabled = false;
				this.leaveButton.enabled = false;
			}
		}

		/*@Override
		protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
			teamName.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		@Override
		protected void keyTyped(char typedChar, int keyCode) throws IOException {
			teamName.textboxKeyTyped(typedChar, keyCode);
			if (teamName.isFocused())
				return;
			super.keyTyped(typedChar, keyCode);
		}*/

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			ItemStack helditem = this.entity.getHeldItemMainhand();
			if (helditem.getItem() == ItemTeamScroll.block) {
				this.fontRenderer.drawString("Team "+ItemTeamScroll.ItemCustom.getTeamDisplayName(this.world, helditem), 61, 34, 0xFF1C1C1C);
				int i = 0;
				int j = 66;
				for (String name : ItemTeamScroll.ItemCustom.getTeamMembers(this.world, helditem)) {
					if (i >= 3) {
						break;
					}
					this.fontRenderer.drawString(name, 61, j, 0xff2f38b4);
					j += 16;
					i++;
				}
			}
			//this.fontRenderer.drawString("" + teamName.getText() + "", 61, 66, -12829636);
			//this.fontRenderer.drawString("tick " + entity.ticksExisted, 61, 82, -12829636);
			//this.fontRenderer.drawString("member3", 61, 98, -12829636);
			//this.teamName.drawTextBox();
		}

		@Override
		public void onGuiClosed() {
			super.onGuiClosed();
			Keyboard.enableRepeatEvents(false);
		}

		@Override
		public void initGui() {
			super.initGui();
			this.guiLeft = (this.width - 257) / 2;
			this.guiTop = (this.height - 156) / 2;
			Keyboard.enableRepeatEvents(true);
			this.buttonList.clear();
			this.leaveButton = new GuiButton(0, this.guiLeft + 205, this.guiTop + 106, 44, 20, "Leave");
			this.joinButton = new GuiButton(1, this.guiLeft + 205, this.guiTop + 82, 44, 20, "Join");
			this.buttonList.add(this.leaveButton);
			this.buttonList.add(this.joinButton);
			//this.teamName = new GuiTextField(0, this.fontRenderer, 141, 30, 108, 20);
			//guistate.put("text:teamName", this.teamName);
			//this.teamName.setMaxStringLength(32767);
			ItemStack helditem = this.entity.getHeldItemMainhand();
			//this.teamName.setText("");
			//if (helditem.getItem() == ItemTeamScroll.block) {
			//	this.teamName.setText(ItemTeamScroll.ItemCustom.getTeamDisplayName(this.world, helditem));
			//}
		}

		@Override
		protected void actionPerformed(GuiButton button) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new GUIButtonPressedMessage(button.id, x, y, z));
			handleButtonAction(entity, button.id, x, y, z);
		}

		@Override
		public boolean doesGuiPauseGame() {
			return false;
		}
	}

	public static class GUIButtonPressedMessage implements IMessage {
		int buttonID, x, y, z;
		public GUIButtonPressedMessage() {
		}

		public GUIButtonPressedMessage(int buttonID, int x, int y, int z) {
			this.buttonID = buttonID;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public static class Handler implements IMessageHandler<GUIButtonPressedMessage, IMessage> {
			@Override
			public IMessage onMessage(GUIButtonPressedMessage message, MessageContext context) {
				EntityPlayerMP entity = context.getServerHandler().player;
				entity.getServerWorld().addScheduledTask(() -> {
					int buttonID = message.buttonID;
					int x = message.x;
					int y = message.y;
					int z = message.z;
					handleButtonAction(entity, buttonID, x, y, z);
				});
				return null;
			}
		}

		@Override
		public void toBytes(io.netty.buffer.ByteBuf buf) {
			buf.writeInt(buttonID);
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
		}

		@Override
		public void fromBytes(io.netty.buffer.ByteBuf buf) {
			buttonID = buf.readInt();
			x = buf.readInt();
			y = buf.readInt();
			z = buf.readInt();
		}
	}

	public static class GUISlotChangedMessage implements IMessage {
		int slotID, x, y, z, changeType, meta;
		public GUISlotChangedMessage() {
		}

		public GUISlotChangedMessage(int slotID, int x, int y, int z, int changeType, int meta) {
			this.slotID = slotID;
			this.x = x;
			this.y = y;
			this.z = z;
			this.changeType = changeType;
			this.meta = meta;
		}

		public static class Handler implements IMessageHandler<GUISlotChangedMessage, IMessage> {
			@Override
			public IMessage onMessage(GUISlotChangedMessage message, MessageContext context) {
				EntityPlayerMP entity = context.getServerHandler().player;
				entity.getServerWorld().addScheduledTask(() -> {
					int slotID = message.slotID;
					int changeType = message.changeType;
					int meta = message.meta;
					int x = message.x;
					int y = message.y;
					int z = message.z;
					handleSlotAction(entity, slotID, changeType, meta, x, y, z);
				});
				return null;
			}
		}

		@Override
		public void toBytes(io.netty.buffer.ByteBuf buf) {
			buf.writeInt(slotID);
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
			buf.writeInt(changeType);
			buf.writeInt(meta);
		}

		@Override
		public void fromBytes(io.netty.buffer.ByteBuf buf) {
			slotID = buf.readInt();
			x = buf.readInt();
			y = buf.readInt();
			z = buf.readInt();
			changeType = buf.readInt();
			meta = buf.readInt();
		}
	}

	private static void handleButtonAction(EntityPlayer entity, int buttonID, int x, int y, int z) {
		World world = entity.world;
		// security measure to prevent arbitrary chunk generation
		if (!world.isBlockLoaded(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				ProcedureTeamManagerLeave.executeProcedure($_dependencies);
			}
		}
		if (buttonID == 1) {
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				ProcedureTeamManagerJoin.executeProcedure($_dependencies);
			}
		}
	}

	private static void handleSlotAction(EntityPlayer entity, int slotID, int changeType, int meta, int x, int y, int z) {
		World world = entity.world;
		// security measure to prevent arbitrary chunk generation
		if (!world.isBlockLoaded(new BlockPos(x, y, z)))
			return;
	}
}
