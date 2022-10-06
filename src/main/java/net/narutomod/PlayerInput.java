/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods, you can remove them as they
 * are overrides of the base class ElementsNarutomodMod.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list, it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor, the compilation will fail.
 *
 * If you want to make a plain independent class, create it in
 * "Workspace" -> "Source" menu.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
*/
package net.narutomod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.WorldServer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.MovementInput;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
//import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureUtils;

import io.netty.buffer.ByteBuf;
import org.lwjgl.input.Mouse;
import javax.annotation.Nullable;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Iterator;

@ElementsNarutomodMod.ModElement.Tag
public class PlayerInput extends ElementsNarutomodMod.ModElement {
	private static final Hook INPUTHOOK = new Hook();

	public PlayerInput(ElementsNarutomodMod instance) {
		super(instance, 611);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(Hook.MovementPacket.ServerHandler.class, Hook.MovementPacket.class, Side.SERVER);
		this.elements.addNetworkMessage(Hook.MovementPacket.ClientHandler.class, Hook.MovementPacket.class, Side.CLIENT);
		this.elements.addNetworkMessage(Hook.MousePacket.ServerHandler.class, Hook.MousePacket.class, Side.SERVER);
		this.elements.addNetworkMessage(Hook.MousePacket.ClientHandler.class, Hook.MousePacket.class, Side.CLIENT);
		this.elements.addNetworkMessage(Hook.CopyInput.ClientHandler.class, Hook.CopyInput.class, Side.CLIENT);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {		
		MinecraftForge.EVENT_BUS.register(INPUTHOOK);
	}

	public static class Hook {
		private List<Integer> handlerList = Lists.newArrayList();
		private boolean haltAllInput;
		private boolean newMovementInput;
		private float strafe;
		private float forward;
		private boolean forwardKeyDown;
		private boolean backKeyDown;
		private boolean leftKeyDown;
		private boolean rightKeyDown;
		private boolean jump;
		private boolean sneak;
		private boolean newMouseEvent;
		private float mouseSensitivity;
		private int dx;
		private int dy;
		private boolean attackPressed;
		private boolean useItemPressed;

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onMovementInput(InputUpdateEvent event) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			if (!this.handlerList.isEmpty()) {
				MovementInput mi = player.movementInput;
				Iterator<Integer> iter = this.handlerList.iterator();
				while (iter.hasNext()) {
					Entity entity = mc.world.getEntityByID(iter.next());
					if (entity instanceof IHandler) {
						MovementPacket.sendToServer(entity, mi.moveStrafe, mi.moveForward, mi.forwardKeyDown, mi.backKeyDown,
						 mi.leftKeyDown, mi.rightKeyDown, mi.jump, mi.sneak);
					} else {
						iter.remove();
					}
				}
			} else {
				if (this.newMovementInput) {
					this.newMovementInput = false;
					player.movementInput.moveStrafe = this.strafe;
					player.movementInput.moveForward = this.forward;
					player.movementInput.forwardKeyDown = this.forwardKeyDown;
					player.movementInput.backKeyDown = this.backKeyDown;
					player.movementInput.leftKeyDown = this.leftKeyDown;
					player.movementInput.rightKeyDown = this.rightKeyDown;
					player.movementInput.jump = this.jump;
					player.movementInput.sneak = this.sneak;
				}
				if (this.newMouseEvent) {
	                this.newMouseEvent = false;
		            float f = mc.gameSettings.mouseSensitivity * 1.2F + 0.4F;
		            float f1 = f * f * f * 8.0F;
		            float f2 = (float)this.dx * f1;
		            float f3 = (float)this.dy * f1;
	                player.turn(f2, f3 * (mc.gameSettings.invertMouse ? -1F : 1F));
	                if (this.attackPressed) {
	                	this.simulateLeftMouseClick();
	                }
	                if (this.useItemPressed) {
	                	this.simulateRightMouseClick();
	                } else if (player.isHandActive()) {
	                	mc.playerController.onStoppedUsingItem(player);
	                }
				}
			}
		}

		public void handleMovement(EntityLivingBase entity) {
			this.clearMovementInput();
			if (entity instanceof EntityPlayerMP) {
				MovementPacket.sendToClient((EntityPlayerMP)entity, this.strafe, this.forward, 
				 this.forwardKeyDown, this.backKeyDown, this.leftKeyDown, this.rightKeyDown, this.jump, this.sneak);
			} else {
				if (this.jump && entity.onGround) {
					entity.motionY = 0.42d;
					entity.isAirBorne = true;
				}
				entity.moveRelative(this.strafe, 0f, this.forward, 0.2f);
				entity.move(net.minecraft.entity.MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);
				if (!entity.hasNoGravity()) {
					entity.motionY += -0.08d;
				}
				entity.motionX *= 0.1d;
				entity.motionZ *= 0.1d;
				entity.motionY *= 0.98d;
				entity.setSneaking(this.sneak);
			}
		}

		public void handleMouseEvent(EntityLivingBase entity) {
			this.clearMouseEvent();
			if (entity instanceof EntityPlayerMP) {
				MousePacket.sendToClient((EntityPlayerMP)entity, this.dx, this.dy, this.attackPressed, this.useItemPressed);
			} else {
	            float f1 = this.mouseSensitivity > 0.0F ? this.mouseSensitivity * 20.0F : 6.0F;
	            float f2 = (float)this.dx * f1;
	            float f3 = (float)this.dy * f1;
	            entity.rotationYaw = entity.rotationYawHead = (float)((double)entity.rotationYawHead + (double)f2 * 0.15D);
	            entity.rotationPitch = (float)((double)entity.rotationPitch - (double)f3 * 0.15D);
	            entity.rotationPitch = MathHelper.clamp(entity.rotationPitch, -90.0F, 90.0F);
	            if (this.attackPressed) {
	            	entity.swingArm(EnumHand.MAIN_HAND);
	            	RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 3d);
	            	if (res.entityHit != null) {
	            		entity.attackEntityAsMob(res.entityHit);
	            	}
	            }
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onMouseInput(InputEvent.MouseInputEvent event) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			if (!this.handlerList.isEmpty()) {
				Iterator<Integer> iter = this.handlerList.iterator();
				while (iter.hasNext()) {
					Entity entity = mc.world.getEntityByID(iter.next());
					if (entity instanceof IHandler) {
						MousePacket.sendToServer(entity, Mouse.getEventDX(), Mouse.getEventDY(), mc.gameSettings.mouseSensitivity,
						 mc.gameSettings.keyBindAttack.isPressed(), mc.gameSettings.keyBindUseItem.isPressed());
					} else {
						iter.remove();
					}
				}
			}
		}

		@SideOnly(Side.CLIENT)
		private void simulateLeftMouseClick() {
			Minecraft mc = Minecraft.getMinecraft();
            if (mc.objectMouseOver != null && !mc.player.isRowingBoat()) {
            	if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
               		mc.playerController.attackEntity(mc.player, mc.objectMouseOver.entityHit);
            	} else {
                	mc.player.resetCooldown();
            	}
           		mc.player.swingArm(EnumHand.MAIN_HAND);
            }
		}

		@SideOnly(Side.CLIENT)
		private void simulateRightMouseClick() {
			Minecraft mc = Minecraft.getMinecraft();
			if (!mc.playerController.getIsHittingBlock() && !mc.player.isRowingBoat()) {
				for (EnumHand enumhand : EnumHand.values()) {
					ItemStack itemstack = mc.player.getHeldItem(enumhand);
					if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY
					 && (mc.playerController.interactWithEntity(mc.player, mc.objectMouseOver.entityHit, mc.objectMouseOver, enumhand) == EnumActionResult.SUCCESS
                      || mc.playerController.interactWithEntity(mc.player, mc.objectMouseOver.entityHit, enumhand) == EnumActionResult.SUCCESS)) {
                    	return;
					}
                    if (!itemstack.isEmpty() && mc.playerController.processRightClick(mc.player, mc.world, enumhand) == EnumActionResult.SUCCESS) {
                        mc.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                        return;
                    }
				}
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onTickPre(TickEvent.ClientTickEvent event) {
			if (this.haltAllInput && event.phase == TickEvent.Phase.START) {
				Minecraft mc = Minecraft.getMinecraft();
				if (!(mc.currentScreen instanceof GuiChat)) {
					mc.displayGuiScreen(new GuiChat());
				}
			}
		}
			
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onGuiOpen(GuiOpenEvent event) {
			if (this.haltAllInput && !(event.getGui() instanceof GuiChat)) {
				event.setCanceled(true);
			}
		}

		/*@SideOnly(Side.CLIENT)
		public class GuiBlank extends GuiScreen {
			@Override
			public boolean doesGuiPauseGame() {
				return false;
			}

			@Override
			public void initGui() {
				this.mc.setIngameFocus();
				this.mc.setIngameNotInFocus();
			}
		}*/

		public static void copyInputFrom(EntityPlayerMP sourcePlayer, IHandler handlerEntity, boolean copy) {
			NarutomodMod.PACKET_HANDLER.sendTo(new CopyInput(handlerEntity instanceof Entity ? ((Entity)handlerEntity).getEntityId() : 0, copy), sourcePlayer);
		}

		public static void haltTargetInput(EntityLivingBase target, boolean halt) {
			if (target instanceof EntityPlayerMP) {
				NarutomodMod.PACKET_HANDLER.sendTo(new CopyInput(-1, halt), (EntityPlayerMP)target);
			} else if (target instanceof EntityLiving) {
				((EntityLiving)target).setNoAI(halt);
			}
		}

		public static class CopyInput implements IMessage {
			int handler;
			boolean flag;
	
			public CopyInput() { }
	
			public CopyInput(int handlerId, boolean b) {
				this.handler = handlerId;
				this.flag = b;
			}
	
			public static class ClientHandler implements IMessageHandler<CopyInput, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(CopyInput message, MessageContext context) {
					Minecraft.getMinecraft().addScheduledTask(() -> {
						switch (message.handler) {
							case -1:
								INPUTHOOK.haltAllInput = message.flag;
								break;
							case 0:
								INPUTHOOK.handlerList.clear();
								break;
							default:
								if (message.flag) {
									INPUTHOOK.handlerList.add(Integer.valueOf(message.handler));
								} else {
									INPUTHOOK.handlerList.remove(Integer.valueOf(message.handler));
								}
								break;
						}
						//inputHook.sourceHandlerId = message.handler;
						//inputHook.haltAllInput = message.handler == -1 ? message.haltinput : false;
					});
					return null;
				}
			}
	
			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.handler);
				buf.writeBoolean(this.flag);
			}
		
			public void fromBytes(ByteBuf buf) {
				this.handler = buf.readInt();
				this.flag = buf.readBoolean();
			}
		}
	
		public static class MovementPacket implements IMessage {
			int id;
			float moveStrafe;
			float moveForward;
			boolean forwardKeyDown;
			boolean backKeyDown;
			boolean leftKeyDown;
			boolean rightKeyDown;
			boolean jump;
			boolean sneak;
	
			public MovementPacket() { }
	
			public MovementPacket(int entityId, float f1, float f2, boolean forwardKey, boolean backKey, boolean leftKey, boolean rightKey, boolean j, boolean s) {
				this.id = entityId;
				this.moveStrafe = f1;
				this.moveForward = f2;
				this.forwardKeyDown = forwardKey;
				this.backKeyDown = backKey;
				this.leftKeyDown = leftKey;
				this.rightKeyDown = rightKey;
				this.jump = j;
				this.sneak = s;
			}
	
			public static void sendToServer(Entity entity, float strafe, float forward, boolean forwardKey, boolean backKey, boolean leftKey, boolean rightKey, boolean jp, boolean sn) {
				NarutomodMod.PACKET_HANDLER.sendToServer(new MovementPacket(entity.getEntityId(), strafe, forward, forwardKey, backKey, leftKey, rightKey, jp, sn));
			}
	
			public static void sendToClient(EntityPlayerMP target, float strafe, float forward, boolean forwardKey, boolean backKey, boolean leftKey, boolean rightKey, boolean jp, boolean sn) {
				NarutomodMod.PACKET_HANDLER.sendTo(new MovementPacket(-1, strafe, forward, forwardKey, backKey, leftKey, rightKey, jp, sn), target);
			}
	
			public static class ClientHandler implements IMessageHandler<MovementPacket, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(MovementPacket message, MessageContext context) {
					Minecraft mc = Minecraft.getMinecraft();
					mc.addScheduledTask(() -> {
						INPUTHOOK.newMovementInput = true;
						INPUTHOOK.strafe = message.moveStrafe;
						INPUTHOOK.forward = message.moveForward;
						INPUTHOOK.forwardKeyDown = message.forwardKeyDown;
						INPUTHOOK.backKeyDown = message.backKeyDown;
						INPUTHOOK.leftKeyDown = message.leftKeyDown;
						INPUTHOOK.rightKeyDown = message.rightKeyDown;
						INPUTHOOK.jump = message.jump;
						INPUTHOOK.sneak = message.sneak;
					});
					return null;
				}
			}
	
			public static class ServerHandler implements IMessageHandler<MovementPacket, IMessage> {
				@Override
				public IMessage onMessage(MovementPacket message, MessageContext context) {
					WorldServer world = context.getServerHandler().player.getServerWorld();
					world.addScheduledTask(() -> {
						Entity entity = world.getEntityByID(message.id);
						if (entity instanceof IHandler) {
							((IHandler)entity).handlePacket(message, null);
						}
					});
					return null;
				}
			}
		
			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.id);
				buf.writeFloat(this.moveStrafe);
				buf.writeFloat(this.moveForward);
				buf.writeBoolean(this.forwardKeyDown);
				buf.writeBoolean(this.backKeyDown);
				buf.writeBoolean(this.leftKeyDown);
				buf.writeBoolean(this.rightKeyDown);
				buf.writeBoolean(this.jump);
				buf.writeBoolean(this.sneak);
			}
		
			public void fromBytes(ByteBuf buf) {
				this.id = buf.readInt();
				this.moveStrafe = buf.readFloat();
				this.moveForward = buf.readFloat();
				this.forwardKeyDown = buf.readBoolean();
				this.backKeyDown = buf.readBoolean();
				this.leftKeyDown = buf.readBoolean();
				this.rightKeyDown = buf.readBoolean();
				this.jump = buf.readBoolean();
				this.sneak = buf.readBoolean();
			}
		}
	
		public static class MousePacket implements IMessage {
			int id;
			int dx;
			int dy;
			float sensitivity;
			boolean attackPressed;
			boolean useItemPressed;
	
			public MousePacket() { }
	
			public MousePacket(int eid, int i1, int i2, float f1, boolean b1, boolean b2) {
				this.id = eid;
				this.dx = i1;
				this.dy = i2;
				this.sensitivity = f1;
				this.attackPressed = b1;
				this.useItemPressed = b2;
			}
	
			public static void sendToClient(EntityPlayerMP target, int x, int y, boolean ap, boolean uip) {
				NarutomodMod.PACKET_HANDLER.sendTo(new MousePacket(0, x, y, 0f, ap, uip), target);
			}
	
			public static void sendToServer(Entity entity, int x, int y, float f, boolean ap, boolean uip) {
				NarutomodMod.PACKET_HANDLER.sendToServer(new MousePacket(entity.getEntityId(), x, y, f, ap, uip));
			}
	
			public static class ClientHandler implements IMessageHandler<MousePacket, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(MousePacket message, MessageContext context) {
					Minecraft mc = Minecraft.getMinecraft();
					mc.addScheduledTask(() -> {
						if (message.id == 0) {
							INPUTHOOK.newMouseEvent = true;
							INPUTHOOK.dx = message.dx;
							INPUTHOOK.dy = message.dy;
							INPUTHOOK.attackPressed = message.attackPressed;
							INPUTHOOK.useItemPressed = message.useItemPressed;
						} else if (message.id == -1) {
							INPUTHOOK.newMouseEvent = false;
						}
					});
					return null;
				}
			}
	
			public static class ServerHandler implements IMessageHandler<MousePacket, IMessage> {
				@Override
				public IMessage onMessage(MousePacket message, MessageContext context) {
					WorldServer world = context.getServerHandler().player.getServerWorld();
					world.addScheduledTask(() -> {
						Entity entity = world.getEntityByID(message.id);
						if (entity instanceof IHandler) {
							((IHandler)entity).handlePacket(null, message);
						}
					});
					return null;
				}
			}
	
			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.id);
				buf.writeInt(this.dx);
				buf.writeInt(this.dy);
				buf.writeFloat(this.sensitivity);
				buf.writeBoolean(this.attackPressed);
				buf.writeBoolean(this.useItemPressed);
			}
	
			public void fromBytes(ByteBuf buf) {
				this.id = buf.readInt();
				this.dx = buf.readInt();
				this.dy = buf.readInt();
				this.sensitivity = buf.readFloat();
				this.attackPressed = buf.readBoolean();
				this.useItemPressed = buf.readBoolean();
			}
		}

		public static interface IHandler {
			void handlePacket(@Nullable MovementPacket movementPacket, @Nullable MousePacket mousePacket);
		}

		public void copyMovementInput(MovementPacket packet) {
			this.strafe = packet.moveStrafe;
			this.forward = packet.moveForward;
			this.forwardKeyDown = packet.forwardKeyDown;
			this.backKeyDown = packet.backKeyDown;
			this.leftKeyDown = packet.leftKeyDown;
			this.rightKeyDown = packet.rightKeyDown;
			this.jump = packet.jump;
			this.sneak = packet.sneak;
			this.newMovementInput = true;
		}

		public void copyMouseInput(MousePacket packet) {
			this.dx = packet.dx;
			this.dy = packet.dy;
			this.mouseSensitivity = packet.sensitivity;
			this.attackPressed = packet.attackPressed;
			this.useItemPressed = packet.useItemPressed;
			this.newMouseEvent = true;
		}

		public boolean isInputHalted() {
			return this.haltAllInput;
		}

		public boolean hasNewMovementInput() {
			return this.newMovementInput;
		}

		public void clearMovementInput() {
			this.newMovementInput = false;
		}

		public float getStrafe() {
			return this.strafe;
		}

		public float getForward() {
			return this.forward;
		}

		public boolean isForwardKeyDown() {
			return this.forwardKeyDown;
		}

		public boolean isBackKeyDown() {
			return this.backKeyDown;
		}

		public boolean isLeftKeyDown() {
			return this.leftKeyDown;
		}

		public boolean isRightKeyDown() {
			return this.rightKeyDown;
		}

		public boolean isJumpKeyDown() {
			return this.jump;
		}

		public boolean isSneakKeyDown() {
			return this.sneak;
		}

		public boolean hasNewMouseEvent() {
			return this.newMouseEvent;
		}

		public void clearMouseEvent() {
			this.newMouseEvent = false;
		}

		public int getDX() {
			return this.dx;
		}

		public int getDY() {
			return this.dy;
		}

		public boolean isAttackPressed() {
			return this.attackPressed;
		}

		public boolean isUseItemPressed() {
			return this.useItemPressed;
		}

		public String movementPacketToString() {
			return "strafe:"+this.strafe+", forward="+this.forward+", forwardKeyDown:"+this.forwardKeyDown
			 +", backKeyDown:"+this.backKeyDown+", leftKeyDown:"+this.leftKeyDown+", rightKeyDown:"
			 +this.rightKeyDown+", jump:"+this.jump+", sneak:"+this.sneak;
		}

		public String mousePacketToString() {
			return "dx="+this.dx+", dy="+this.dy+", attackPressed:"+this.attackPressed+", useItemPressed:"+this.useItemPressed;
		}
	}
}
