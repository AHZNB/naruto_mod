package net.narutomod.procedure;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.Minecraft;

//import net.narutomod.item.ItemMangekyoSharinganObito;
//import net.narutomod.item.ItemMangekyoSharinganEternal;
//import net.narutomod.EntityTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnLivingUpdate extends ElementsNarutomodMod.ModElement {
	//private static boolean noClip = false;
	//private static double motionY;
	
	public ProcedureOnLivingUpdate(ElementsNarutomodMod instance) {
		super(instance, 105);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(CustomDataMessage.Handler.class, CustomDataMessage.class, Side.CLIENT);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static boolean isNoClip(Entity player) {
		//return noClip;
		return player.getEntityData().getByte(NarutomodModVariables.noClipFlag) != 0;
	}

	public static boolean noClipAllowClicks(Entity player) {
		return (player.getEntityData().getByte(NarutomodModVariables.noClipFlag) & 2) != 0;
	}

	public static void setNoClip(Entity player, boolean noClip) {
		setNoClip(player, noClip, false);
	}

	public static void setNoClip(Entity player, boolean noClip, boolean allowMouseClicks) {
		if (player instanceof EntityPlayerMP) {
			byte flag = noClip ? (byte)(1|(allowMouseClicks?2:0)) : (byte)0;
			player.noClip = noClip;
			if (noClip) {
				player.getEntityData().setByte(NarutomodModVariables.noClipFlag, flag);
			} else {
				player.getEntityData().removeTag(NarutomodModVariables.noClipFlag);
			}
			NarutomodMod.PACKET_HANDLER.sendTo(new CustomDataMessage(player, flag), (EntityPlayerMP) player);
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new CustomDataMessage(player, flag), player);
		}
	}
	
	public static class CustomDataMessage implements IMessage {
		int id;
		byte flag;
		//double my;

		public CustomDataMessage() {
		}

		public CustomDataMessage(Entity entity, byte flagIn) {
			this.id = entity.getEntityId();
			this.flag = flagIn;
			//this.my = entity.motionY;
		}

		public static class Handler implements IMessageHandler<CustomDataMessage, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(CustomDataMessage message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					Entity player = Minecraft.getMinecraft().world.getEntityByID(message.id);
					if (player instanceof EntityPlayer) {
						if (message.flag == 0) {
							player.getEntityData().removeTag(NarutomodModVariables.noClipFlag);
							player.noClip = false;
						} else {
							player.getEntityData().setByte(NarutomodModVariables.noClipFlag, message.flag);
							player.noClip = true;
						}
					}
					//noClip = message.bVar1;
					//motionY = message.qwVar2;
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeByte(this.flag);
			//buf.writeDouble(this.my);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.flag = buf.readByte();
			//this.my = buf.readDouble();
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;
		//EntityTracker.getOrCreate(entity).saveBB();
		if (entity.getEntityData().hasKey(NarutomodModVariables.noClipFlag)) {
			entity.noClip = isNoClip(entity);
			if (entity.noClip) {
				if (!entity.hasNoGravity()) {
					if (world.isAirBlock(new BlockPos(entity.posX, entity.posY-0.1d, entity.posZ)) || entity.isSneaking()) {
						entity.motionY -= 0.01d;
					} else {
						entity.motionY = 0;
					}
				}
			}
		}
		double d = entity.getEntityData().getDouble(NarutomodModVariables.DeathAnimationTime);
		if (!world.isRemote && d > 0.0D) {
			event.setCanceled(true);
			{
				java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				ProcedureDeathAnimations.executeProcedure($_dependencies);
			}
			d -= 1.0D;
			entity.getEntityData().setDouble(NarutomodModVariables.DeathAnimationTime, d);
			if (d <= 0.0D) {
				ProcedureUtils.clearDeathAnimations(entity);
				if (entity instanceof EntityPlayer) {
					entity.attackEntityFrom(ProcedureUtils.SPECIAL_DAMAGE, Float.MAX_VALUE);
				} else {
					entity.setDead();
				}
			}
		}
		if (entity.getEntityData().hasKey(NarutomodModVariables.InvulnerableTime)) {
			d = entity.getEntityData().getDouble(NarutomodModVariables.InvulnerableTime);
			if (d > 0.0D) {
				entity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, d - 1d);
			} else {
				entity.getEntityData().removeTag(NarutomodModVariables.InvulnerableTime);
			}
		}
		if (entity.getEntityData().hasKey("FearEffect")) {
			int i = entity.getEntityData().getInteger("FearEffect");
			if (i > 0) {
				entity.getEntityData().setInteger("FearEffect", i - 1);
			} else {
				entity.getEntityData().removeTag("FearEffect");
			}
		}
		if (entity.getEntityData().hasKey("ForceExtinguish")) {
			int i = entity.getEntityData().getInteger("ForceExtinguish");
			if (i > 0) {
				entity.getEntityData().setInteger("ForceExtinguish", i - 1);
				entity.extinguish();
			} else {
				entity.getEntityData().removeTag("ForceExtinguish");
			}
		}
		if (entity.getEntityData().hasKey("UntargetableTicks")) {
			int i = entity.getEntityData().getInteger("UntargetableTicks");
			if (i > 0) {
				entity.getEntityData().setInteger("UntargetableTicks", i - 1);
			} else {
				entity.getEntityData().removeTag("UntargetableTicks");
			}
		}
		if (world.isRemote && entity.getEntityData().hasKey("GlowingTicks")) {
			int i = entity.getEntityData().getInteger("GlowingTicks");
			entity.setGlowing(i > 0);
			if (i > 0) {
				setGlowingFor(entity, i - 1);
			} else {
				entity.getEntityData().removeTag("GlowingTicks");
			}
		}
		if (entity instanceof EntityLiving) {
			EntityLivingBase target = ((EntityLiving)entity).getAttackTarget();
			if (target != null && !target.isEntityAlive()) {
				((EntityLiving)entity).setAttackTarget(null);
			}
		}
	}

	public static void setGlowingFor(Entity entity, int ticks) {
		entity.getEntityData().setInteger("GlowingTicks", ticks);
	}

	public static void setUntargetable(Entity entity, int ticks) {
		if (entity.world.isRemote) {
			ProcedureSync.EntityNBTTag.sendToServer(entity, "UntargetableTicks", ticks);
		} else {
			entity.getEntityData().setInteger("UntargetableTicks", ticks);
		}
	}

	public static boolean isUntargetable(Entity entity) {
		return entity.getEntityData().getInteger("UntargetableTicks") > 0;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderName(RenderLivingEvent.Specials.Pre event) {
		if (isNoClip(event.getEntity())) {
			event.setCanceled(true);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderLivingPre(RenderLivingEvent.Pre event) {
		if (event.getRenderer().getMainModel() instanceof ModelBiped) {
			ModelBiped model = (ModelBiped)event.getRenderer().getMainModel();
			if (event.getEntity().getEntityData().getBoolean(NarutomodModVariables.forceBowPose)) {
				model.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onDeath(LivingDeathEvent event) {
		ProcedureUtils.clearDeathAnimations(event.getEntityLiving());
	}
}
