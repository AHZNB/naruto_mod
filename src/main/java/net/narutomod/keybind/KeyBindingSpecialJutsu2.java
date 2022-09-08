
package net.narutomod.keybind;

import org.lwjgl.input.Keyboard;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.Minecraft;

import net.narutomod.procedure.ProcedureSpecialJutsu2OnKeyPressed;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class KeyBindingSpecialJutsu2 extends ElementsNarutomodMod.ModElement {
	private KeyBinding keys;
	private boolean wasKeyDown;

	public KeyBindingSpecialJutsu2(ElementsNarutomodMod instance) {
		super(instance, 65);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(KeyBindingPressedMessageHandler.class, KeyBindingPressedMessage.class, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		keys = new KeyBinding("key.mcreator.specialjutsu2", Keyboard.KEY_F, "key.mcreator.category");
		ClientRegistry.registerKeyBinding(keys);
		MinecraftForge.EVENT_BUS.register(this);
	}

	/*@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (Minecraft.getMinecraft().currentScreen == null) {
			int key = this.keys.getKeyCode();
			boolean isKeyDown = Keyboard.isKeyDown(key);
			if (isKeyDown || Keyboard.getEventKey() == key) {
				NarutomodMod.PACKET_HANDLER.sendToServer(new KeyBindingPressedMessage(isKeyDown));
				pressAction(Minecraft.getMinecraft().player, isKeyDown);
			}
		}
	}*/

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (Minecraft.getMinecraft().currentScreen == null && this.keys.getKeyCode() > 0) {
			this.processKeyBind();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMouseEvent(InputEvent.MouseInputEvent event) {
		if (Minecraft.getMinecraft().currentScreen == null && this.keys.getKeyCode() <= 0) {
			this.processKeyBind();
		}
	}

	@SideOnly(Side.CLIENT)
	private void processKeyBind() {
		boolean isKeyDown = this.keys.isKeyDown();
		if (isKeyDown || this.wasKeyDown) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new KeyBindingPressedMessage(isKeyDown));
			pressAction(Minecraft.getMinecraft().player, isKeyDown);
		}
		this.wasKeyDown = isKeyDown;
	}

	public static class KeyBindingPressedMessageHandler implements IMessageHandler<KeyBindingPressedMessage, IMessage> {
		@Override
		public IMessage onMessage(KeyBindingPressedMessage message, MessageContext context) {
			EntityPlayerMP entity = context.getServerHandler().player;
			entity.getServerWorld().addScheduledTask(() -> {
				pressAction(entity, message.is_pressed);
			});
			return null;
		}
	}

	public static class KeyBindingPressedMessage implements IMessage {
		boolean is_pressed;
		public KeyBindingPressedMessage() {
		}

		public KeyBindingPressedMessage(boolean is_pressed) {
			this.is_pressed = is_pressed;
		}

		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(this.is_pressed);
		}

		public void fromBytes(ByteBuf buf) {
			this.is_pressed = buf.readBoolean();
		}
	}

	private static void pressAction(EntityPlayer entity, boolean is_pressed) {
		World world = entity.world;
		int x = (int) entity.posX;
		int y = (int) entity.posY;
		int z = (int) entity.posZ;
		// security measure to prevent arbitrary chunk generation
		if (!world.isBlockLoaded(new BlockPos(x, y, z)))
			return;
		{
			java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
			$_dependencies.put("is_pressed", is_pressed);
			$_dependencies.put("entity", entity);
			$_dependencies.put("x", x);
			$_dependencies.put("y", y);
			$_dependencies.put("z", z);
			$_dependencies.put("world", world);
			ProcedureSpecialJutsu2OnKeyPressed.executeProcedure($_dependencies);
		}
	}
}
