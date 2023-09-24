package net.narutomod;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.WorldServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityNinjaMob;
import net.narutomod.gui.overlay.OverlayChakraDisplay;

import java.util.Random;
import java.util.Map;
import java.util.Collection;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class Chakra extends ElementsNarutomodMod.ModElement {
	private static final Map<EntityPlayer, PathwayPlayer> playerMap = Maps.newHashMap();
	private static final Map<EntityLivingBase, Pathway> livingMap = Maps.newHashMap();
	private static final String DATAKEY = "ChakraPathwaySystem";
	//@SideOnly(Side.CLIENT)
	private static PathwayPlayer clientPlayerPathway = null;

	public Chakra(ElementsNarutomodMod instance) {
		super(instance, 395);
	}

	@SideOnly(Side.CLIENT)
	public static boolean isInitialized(EntityPlayer player) {
		return clientPlayerPathway != null && clientPlayerPathway.user == player;
	}

	public static Collection<PathwayPlayer> getPlayerMap() {
		return playerMap.values();
	}

	public static double getLevel(EntityLivingBase entity) {
		Pathway cp = pathway(entity);
		return MathHelper.sqrt(Math.max(cp.getAmount(), cp.getMax()));
	}

	public static double getChakraModifier(EntityLivingBase entity) {
		return ProcedureUtils.getCDModifier(getLevel(entity));
	}
	
	public static Pathway pathway(EntityLivingBase user) {
		if (user instanceof EntityPlayer) {
			return pathway((EntityPlayer)user);
		} else if (user instanceof EntityNinjaMob.Base) {
			return ((EntityNinjaMob.Base)user).getChakraPathway();
		} else {
			Pathway p = livingMap.get(user);
			if (p == null) {
				p = new Pathway(user);
				livingMap.put(user, p);
			}
			return p;
		}
	}

	public static PathwayPlayer pathway(EntityPlayer player) {
		if (player.world.isRemote) {
			if (clientPlayerPathway == null || clientPlayerPathway.user != player) {
				clientPlayerPathway = new PathwayPlayer(player);
			}
			return clientPlayerPathway;
		}
		PathwayPlayer p = playerMap.get(player);
		return p != null ? p : new PathwayPlayer(player);
	}

	public static class Pathway<T extends EntityLivingBase> {
		protected final T user;
		private double amount;
		private double max;
		
		protected Pathway(T userIn) {
			this.user = userIn;
		}

		public double getMax() {
			return this.max;
		}

		public Pathway<T> setMax(double maxIn) {
			this.max = maxIn;
			return this;
		}

		protected void set(double amountIn) {
			this.amount = amountIn;
		}

		public boolean consume(double amountIn, boolean ignoreMax) {
			double d = this.getAmount();
			double max = this.getMax();
			double d1 = d - amountIn;
			d1 = d1 > max ? (ignoreMax ? d1 : amountIn > 0d ? d1 : d > max ? d : max) : d1 > 0 ? d1 : d;
			this.set(d1);
			return d != d1;
		}

		public boolean consume(double amountIn) {
			return this.consume(amountIn, false);
		}

		public void consume(float percent) {
			this.consume(percent, false);
		}

		public void consume(float percent, boolean ignoreMax) {
			this.consume(this.getMax() * percent, ignoreMax);
		}

		public void clear() {
			this.set(0.0d);
		}

		public boolean isFull() {
			return this.getAmount() >= this.getMax();
		}

		public double getAmount() {
			return this.amount;
		}

		protected void onUpdate() {
			double d = this.getAmount();
			double d1 = this.getMax();
			if (d > d1 * 4d && this.user.isEntityAlive()) {
				this.user.attackEntityFrom(DamageSource.CRAMMING, Float.MAX_VALUE);
				return;
			}
			if (d > d1 && this.user.ticksExisted % 20 == 0) {
				this.consume(10.0d);
			}
			if (d < 10.0d && d1 > 150.0d
			 && (!(this.user instanceof EntityPlayer) || !((EntityPlayer)this.user).isCreative())) {
				this.user.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 3));
				this.user.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 3));
				this.user.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 3));
			}
		}

		public void warningDisplay() {
		}

		@Override
		public String toString() {
			return "Chakra:{amount:"+this.getAmount()+",max:"+this.getMax()+","+this.user.getName()+"}";
		}
	}
	
	public static class PathwayPlayer extends Pathway<EntityPlayer> {
		private boolean forceSync;
		private int motionlessTime;
		private double prevX;
		private double prevZ;

		protected PathwayPlayer(EntityPlayer playerIn) {
			super(playerIn);
			this.setMax(PlayerTracker.getBattleXp(playerIn) * 0.5d);
			this.set(playerIn.getEntityData().getDouble(DATAKEY));
			if (this.getAmount() < 0d) {
				this.set(this.getMax());
			}
			this.prevX = playerIn.posX;
			this.prevZ = playerIn.posZ;
			if (!playerIn.world.isRemote) {
				playerMap.put(playerIn, this);
				this.sendToClient();
			}
		}

		@Override
		public void warningDisplay() {
			OverlayChakraDisplay.notEnoughChakraWarning(this.user);
		}

		private void sendToClient() {
			if (this.user instanceof EntityPlayerMP) {
				ServerMessage.sendToSelf((EntityPlayerMP)this.user, this.getAmount(), this.getMax());
			}
		}

		@Override
		protected void set(double amountIn) {
			if (amountIn != this.getAmount()) {
				super.set(amountIn);
				this.user.getEntityData().setDouble(DATAKEY, amountIn);
				this.sendToClient();
				this.motionlessTime = 0;
			}
		}

		@Override
		public boolean consume(double amountIn, boolean ignoreMax) {
			boolean flag = super.consume(amountIn, ignoreMax);
			if (amountIn > 0d && !flag) {
				this.warningDisplay();
			}
			return flag;
		}

		@Override
		protected void onUpdate() {
			super.onUpdate();
			if (this.user.world instanceof WorldServer && ((WorldServer)this.user.world).areAllPlayersAsleep()) {
				this.consume(-0.6f);
			}
			++this.motionlessTime;
			if (this.user.posX != this.prevX || this.user.posZ != this.prevZ
			 || !this.user.onGround || this.user.isSwingInProgress) {
			 	this.motionlessTime = 0;
			}
			if (this.motionlessTime > 80) {
				this.consume(-ModConfig.CHAKRA_REGEN_RATE - 0.001f * this.user.getFoodStats().getSaturationLevel());
			}
			double d = PlayerTracker.getBattleXp(this.user) * 0.5d;
			if (d != this.getMax() || this.forceSync) {
				this.forceSync = false;
				this.setMax(d);
				this.sendToClient();
			}
			this.prevX = this.user.posX;
			this.prevZ = this.user.posZ;
		}

		public static class PlayerHook {
			@SubscribeEvent
			public void onDeath(LivingDeathEvent event) {
				Entity entity = event.getEntityLiving();
				if (entity instanceof EntityPlayer) {
					if (entity.world.isRemote) {
						clientPlayerPathway = null;
					} else {
						Pathway p = playerMap.get((EntityPlayer)entity);
						if (p != null) {
							p.set(Math.min(PlayerTracker.keepNinjaXp(entity.world) && PlayerTracker.getBattleXp((EntityPlayer)entity) > 10d ? 10d : 0d, p.getMax()));
							playerMap.remove((EntityPlayer)entity);
						}
					}
				}
			}
	
			@SubscribeEvent
			public void onTick(TickEvent.PlayerTickEvent event) {
				if (PlayerTracker.isNinja(event.player)) {
					if (event.phase == TickEvent.Phase.END && !event.player.world.isRemote) {
						Pathway p = playerMap.get(event.player);
						if (p != null) {
							p.onUpdate();
						} else if (event.player.experienceLevel >= 10) {
							pathway(event.player);
						}
					}
				}
			}
	
			@SubscribeEvent
			public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
				if (event.player instanceof EntityPlayerMP) {
					((EntityPlayerMP)event.player).connection
					  .sendPacket(new net.minecraft.network.play.server.SPacketSetExperience(
					  event.player.experience, event.player.experienceTotal, event.player.experienceLevel));
					PathwayPlayer p = playerMap.get(event.player);
					if (p != null) {
						p.forceSync = true;
					}
				}
			}
	
			@SubscribeEvent
			public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
				if (!event.player.world.isRemote && PlayerTracker.isNinja(event.player) && event.player.experienceLevel >= 10) {
					pathway(event.player);
				}
			}

			@SubscribeEvent(priority = EventPriority.LOWEST)
			public void onRespawn(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
				//if (!event.isWasDeath()) {
					EntityPlayer oldPlayer = event.getOriginal();
					EntityPlayer newPlayer = event.getEntityPlayer();
					PathwayPlayer p = playerMap.get(oldPlayer);
					if (p != null) {
						if (oldPlayer == newPlayer) {
							p.forceSync = true;
						} else {
							PathwayPlayer pnew = pathway(newPlayer);
							pnew.set(p.getAmount());
							pnew.forceSync = true;
							playerMap.remove(oldPlayer);
						}
					}
				//}
			}
		}

		public static class ServerMessage implements IMessage {
			//int id;
			double amount;
			double max;
	
			public ServerMessage() { }
	
			public ServerMessage(double amountIn, double maxIn) {
				//this.id = pathway.player.getEntityId();
				this.amount = amountIn;
				this.max = maxIn;
			}

			public static void sendToSelf(EntityPlayerMP player, double d1, double d2) {
				NarutomodMod.PACKET_HANDLER.sendTo(new ServerMessage(d1, d2), player);
			}
	
			public static class Handler implements IMessageHandler<ServerMessage, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(ServerMessage message, MessageContext context) {
					Minecraft.getMinecraft().addScheduledTask(() -> {
						EntityPlayer player = Minecraft.getMinecraft().player;
						if (player != null) {
							pathway(player).setMax(message.max).set(message.amount);
						}
					});
					return null;
				}
			}
	
			public void toBytes(ByteBuf buf) {
				//buf.writeInt(this.id);
				buf.writeDouble(this.amount);
				buf.writeDouble(this.max);
			}
	
			public void fromBytes(ByteBuf buf) {
				//this.id = buf.readInt();
				this.amount = buf.readDouble();
				this.max = buf.readDouble();
			}
		}

		public static class ConsumeMessage implements IMessage {
			public double amount;
	
			public ConsumeMessage() {
			}
	
			public ConsumeMessage(double amountIn) {
				this.amount = amountIn;
			}
	
			public static void sendToServer(double amountIn) {
				NarutomodMod.PACKET_HANDLER.sendToServer(new ConsumeMessage(amountIn));
			}
	
			public static class Handler implements IMessageHandler<ConsumeMessage, IMessage> {
				@Override
				public IMessage onMessage(ConsumeMessage message, MessageContext context) {
					EntityPlayerMP entity = context.getServerHandler().player;
					entity.getServerWorld().addScheduledTask(() -> {
						pathway(entity).consume(message.amount);
					});
					return null;
				}
			}
	
			public void toBytes(ByteBuf buf) {
				buf.writeDouble(this.amount);
			}
	
			public void fromBytes(ByteBuf buf) {
				this.amount = buf.readDouble();
			}
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(PathwayPlayer.ServerMessage.Handler.class, PathwayPlayer.ServerMessage.class, Side.CLIENT);
		elements.addNetworkMessage(PathwayPlayer.ConsumeMessage.Handler.class, PathwayPlayer.ConsumeMessage.class, Side.SERVER);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PathwayPlayer.PlayerHook());
	}
}
