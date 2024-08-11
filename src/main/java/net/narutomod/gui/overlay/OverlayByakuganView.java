package net.narutomod.gui.overlay;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.MouseEvent;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.Minecraft;

import net.narutomod.item.ItemByakugan;
import net.narutomod.entity.EntityAltCamView;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import io.netty.buffer.ByteBuf;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class OverlayByakuganView extends ElementsNarutomodMod.ModElement {
	public static boolean byakuganActivated = false;
	private static float renderDistanceChunks;
	
	public OverlayByakuganView(ElementsNarutomodMod instance) {
		super(instance, 102);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(CustomDataMessage.Handler.class, CustomDataMessage.class, Side.CLIENT);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new GUIRenderEventClass());
	}

	public static void sendCustomData(Entity player, boolean activated, float fov) {
		if (player != null && player instanceof EntityPlayerMP)
			NarutomodMod.PACKET_HANDLER.sendTo(new CustomDataMessage(activated, fov), (EntityPlayerMP) player);
	}
	
	public static class CustomDataMessage implements IMessage {
		boolean activated;
		float fov;
		public CustomDataMessage() {
		}

		public CustomDataMessage(boolean activated, float fov) {
			this.activated = activated;
			this.fov = fov;
		}

		public static class Handler implements IMessageHandler<CustomDataMessage, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(CustomDataMessage message, MessageContext context) {
				if (!byakuganActivated && !message.activated) {
					Minecraft.getMinecraft().gameSettings.fovSetting = message.fov;
				} else {
					byakuganActivated = message.activated;
					renderDistanceChunks = message.fov;
				}
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(this.activated);
			buf.writeFloat(this.fov);
		}

		public void fromBytes(ByteBuf buf) {
			this.activated = buf.readBoolean();
			this.fov = buf.readFloat();
		}
	}

	public static class GUIRenderEventClass {
		private final List<EntityLivingBase> glowList = Lists.newArrayList();
		private float prevrenderDistanceChunks;
		private int prevRenderDistance;
		private boolean first_on = true;
		private EntityAltCamView.EntityCustom camEntity = null;

		@SubscribeEvent(priority = EventPriority.NORMAL)
		@SideOnly(Side.CLIENT)
		public void eventHandler(RenderGameOverlayEvent event) {
			if (!event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.HELMET) {
				Minecraft mc = Minecraft.getMinecraft();
				EntityPlayer player = mc.player;
				if (player.inventory.armorInventory.get(3).getItem() == ItemByakugan.helmet && byakuganActivated) {
					int sWidth = event.getResolution().getScaledWidth();
					int sHeight = event.getResolution().getScaledHeight();
					int color = 0x1AFFFFFF;
					GlStateManager.enableAlpha();
					GlStateManager.enableColorLogic();
					GlStateManager.colorLogicOp(GlStateManager.LogicOp.INVERT);
					GuiIngame.drawRect(0, 0, sWidth, sHeight, color);
					GlStateManager.colorLogicOp(GlStateManager.LogicOp.COPY);
					GlStateManager.disableColorLogic();
					GlStateManager.disableAlpha();
					GuiIngame.drawRect(sWidth / 2 - 5, sHeight / 2, sWidth / 2 + 5, sHeight / 2 + 1, -1);
					GuiIngame.drawRect(sWidth / 2, sHeight / 2 - 5, sWidth / 2 + 1, sHeight / 2 + 5, -1);
					this.setFOV(player);
					for (EntityLivingBase entitylb : mc.world.getEntitiesWithinAABB(EntityLivingBase.class, 
					 player.getEntityBoundingBox().grow(mc.gameSettings.renderDistanceChunks * 8))) {
						if (!entitylb.isGlowing() && !entitylb.equals(player)) {
							entitylb.setGlowing(true);
							this.glowList.add(entitylb);
						}
					}
				} else {
					this.resetFOV(player);
					if (!this.glowList.isEmpty()) {
						for (EntityLivingBase entitylb : this.glowList) {
							if (!entitylb.isInvisible()) {
								entitylb.setGlowing(false);
							}
						}
						this.glowList.clear();
					}
				}
			}
		}

		@SideOnly(Side.CLIENT)
		private void setFOV(EntityPlayer player) {
			double xp = PlayerTracker.getNinjaLevel(player) / 3;
			if (this.first_on) {
				Minecraft mc = Minecraft.getMinecraft();
				this.prevRenderDistance = mc.gameSettings.renderDistanceChunks;
				mc.gameSettings.renderDistanceChunks = MathHelper.clamp((int)xp * 11 / 16, 16, 32);
				this.camEntity = new EntityAltCamView.EntityCustom(player);
				mc.world.spawnEntity(this.camEntity);
				mc.setRenderViewEntity(this.camEntity);
				this.first_on = false;
			}
			if (this.camEntity != null) {
				Vec3d vec3d1 = player.getPositionEyes(1.0F)
				 .add(player.getLookVec().scale(((110.0F - renderDistanceChunks) * Math.min((float)xp, 70f) / 10.0F + 1.0F)));
				this.camEntity.setLocationAndAngles(vec3d1.x, vec3d1.y, vec3d1.z, player.rotationYaw, player.rotationPitch);
			}
		}

		@SideOnly(Side.CLIENT)
		private void resetFOV(EntityPlayer player) {
			if (!this.first_on) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.setRenderViewEntity(player);
				if (this.camEntity != null) {
					player.world.removeEntity(this.camEntity);
					this.camEntity = null;
				}
				mc.gameSettings.renderDistanceChunks = this.prevRenderDistance;
				this.first_on = true;
			}
		}

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void onMouseEvent(MouseEvent event) {
			if ((event.getButton() == 0 || event.getButton() == 1) && event.isButtonstate()) {
				Minecraft mc = Minecraft.getMinecraft();

				if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityPlayer && mc.objectMouseOver.entityHit.equals(mc.player)) {
					event.setCanceled(true);
				}
			}
		}
	}
}
