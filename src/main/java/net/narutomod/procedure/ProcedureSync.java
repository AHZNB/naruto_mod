package net.narutomod.procedure;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;

import net.narutomod.entity.EntityEarthBlocks;
import net.narutomod.entity.EntityLightningArc;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSync extends ElementsNarutomodMod.ModElement {
	public ProcedureSync(ElementsNarutomodMod instance) {
		super(instance, 536);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(SwingMainArm.Handler.class, SwingMainArm.class, Side.CLIENT);
		this.elements.addNetworkMessage(RemoveEntity.Handler.class, RemoveEntity.class, Side.CLIENT);
		this.elements.addNetworkMessage(MobAppearanceParticle.Handler.class, MobAppearanceParticle.class, Side.CLIENT);
		this.elements.addNetworkMessage(SetGlowing.Handler.class, SetGlowing.class, Side.CLIENT);
		this.elements.addNetworkMessage(ResetBoundingBox.ClientHandler.class, ResetBoundingBox.class, Side.CLIENT);
		this.elements.addNetworkMessage(ResetBoundingBox.ServerHandler.class, ResetBoundingBox.class, Side.SERVER);
		this.elements.addNetworkMessage(MotionPacket.ClientHandler.class, MotionPacket.class, Side.CLIENT);
		this.elements.addNetworkMessage(MotionPacket.ServerHandler.class, MotionPacket.class, Side.SERVER);
		this.elements.addNetworkMessage(EntityPositionAndRotation.ClientHandler.class, EntityPositionAndRotation.class, Side.CLIENT);
		this.elements.addNetworkMessage(EntityPositionAndRotation.ServerHandler.class, EntityPositionAndRotation.class, Side.SERVER);
		this.elements.addNetworkMessage(EntityState.Handler.class, EntityState.class, Side.CLIENT);
		this.elements.addNetworkMessage(EntityNBTTag.ServerHandler.class, EntityNBTTag.class, Side.SERVER);
		this.elements.addNetworkMessage(EntityNBTTag.ClientHandler.class, EntityNBTTag.class, Side.CLIENT);
		this.elements.addNetworkMessage(CPacketEarthBlocks.Handler.class, CPacketEarthBlocks.class, Side.SERVER);
		this.elements.addNetworkMessage(SPacketEarthBlocks.Handler.class, SPacketEarthBlocks.class, Side.CLIENT);
		this.elements.addNetworkMessage(CPacketVec3d.Handler.class, CPacketVec3d.class, Side.SERVER);
		this.elements.addNetworkMessage(RenderDistance.ServerHandler.class, RenderDistance.class, Side.SERVER);
		this.elements.addNetworkMessage(RenderDistance.ClientHandler.class, RenderDistance.class, Side.CLIENT);
		this.elements.addNetworkMessage(CPacketSpawnLightning.Handler.class, CPacketSpawnLightning.class, Side.SERVER);
		this.elements.addNetworkMessage(MultiPartsPacket.ServerHandler.class, MultiPartsPacket.class, Side.SERVER);
		this.elements.addNetworkMessage(MultiPartsPacket.ClientHandler.class, MultiPartsPacket.class, Side.CLIENT);
		this.elements.addNetworkMessage(MultiPartsSetPassengers.ClientHandler.class, MultiPartsSetPassengers.class, Side.CLIENT);
		this.elements.addNetworkMessage(SoundEffectMessage.Handler.class, SoundEffectMessage.class, Side.SERVER);
	}
	
	public static class SwingMainArm implements IMessage {
		int id;

		public SwingMainArm() {}

		public SwingMainArm(Entity entity) {
			this.id = entity.getEntityId();
		}

		public static void send(EntityLivingBase entity) {
			if (entity instanceof EntityPlayerMP) {
				NarutomodMod.PACKET_HANDLER.sendTo(new SwingMainArm(entity), (EntityPlayerMP) entity);
			} else {
				entity.swingArm(EnumHand.MAIN_HAND);
				NarutomodMod.PACKET_HANDLER.sendToAllTracking(new SwingMainArm(entity), entity);
			}
		}
		
		public static class Handler implements IMessageHandler<SwingMainArm, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(SwingMainArm message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.id);
					if (entity instanceof EntityLivingBase) {
						((EntityLivingBase) entity).swingArm(EnumHand.MAIN_HAND);
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
		}
	}

	public static class RemoveEntity implements IMessage {
		int id;

		public RemoveEntity() { }

		public RemoveEntity(Entity entity) {
			this.id = entity.getEntityId();
		}

		public static void send(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new RemoveEntity(entity), entity);
		}
		
		public static class Handler implements IMessageHandler<RemoveEntity, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(RemoveEntity message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					World world = Minecraft.getMinecraft().world;
					Entity entity = world.getEntityByID(message.id);
					if (entity != null) {
						world.removeEntity(entity);
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
		}
	}

	public static class MobAppearanceParticle implements IMessage {
		int id;

		public MobAppearanceParticle() { }

		public MobAppearanceParticle(int entityId) {
			this.id = entityId;
		}

		public static void send(EntityPlayerMP entity, int entityId) {
			NarutomodMod.PACKET_HANDLER.sendTo(new MobAppearanceParticle(entityId), entity);
		}
		
		public static class Handler implements IMessageHandler<MobAppearanceParticle, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(MobAppearanceParticle message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					EntityPlayer player = Minecraft.getMinecraft().player;
					if (player != null) {
						player.world.spawnAlwaysVisibleParticle(Particles.Types.MOB_APPEARANCE.getID(), 
						 player.posX, player.posY, player.posZ, 0d, 0d, 0d, message.id);
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
		}
	}

	public static class SetGlowing implements IMessage {
		int id;
		int ticks;

		public SetGlowing() { }

		public SetGlowing(Entity entity, int glowTicks) {
			this.id = entity.getEntityId();
			this.ticks = glowTicks;
		}

		public static void send(EntityPlayerMP player, Entity entity, int glowTicks) {
			NarutomodMod.PACKET_HANDLER.sendTo(new SetGlowing(entity, glowTicks), player);
		}

		public static class Handler implements IMessageHandler<SetGlowing, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(SetGlowing message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					Entity entity = mc.world.getEntityByID(message.id);
					if (entity != null) {
						ProcedureOnLivingUpdate.setGlowingFor(entity, message.ticks);
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeInt(this.ticks);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.ticks = buf.readInt();
		}
	}

	public static class ResetBoundingBox implements IMessage {
		int id;
		AxisAlignedBB aabb;
		float w, h;

		public ResetBoundingBox() {
		}

		public ResetBoundingBox(Entity entity) {
			this.id = entity.getEntityId();
			this.aabb = entity.getEntityBoundingBox();
			this.w = entity.width;
			this.h = entity.height;
		}

		public static void sendToTracking(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new ResetBoundingBox(entity), entity);
		}

		public static void sendToPlayer(Entity entity, EntityPlayerMP player) {
			NarutomodMod.PACKET_HANDLER.sendTo(new ResetBoundingBox(entity), player);
		}

		public static void sendToServer(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new ResetBoundingBox(entity));
		}

		private static void setBoundingBox(@Nullable Entity entity, ResetBoundingBox message) {
			if (entity != null) {
				entity.setEntityBoundingBox(message.aabb);
				entity.width = message.w;
				entity.height = message.h;
				entity.resetPositionToBB();
			}
		}

		public static class ClientHandler implements IMessageHandler<ResetBoundingBox, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(ResetBoundingBox message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					setBoundingBox(Minecraft.getMinecraft().world.getEntityByID(message.id), message);
				});
				return null;
			}
		}

		public static class ServerHandler implements IMessageHandler<ResetBoundingBox, IMessage> {
			@Override
			public IMessage onMessage(ResetBoundingBox message, MessageContext context) {
				WorldServer world = context.getServerHandler().player.getServerWorld();
				world.addScheduledTask(() -> {
					setBoundingBox(world.getEntityByID(message.id), message);
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeDouble(this.aabb.minX);
			buf.writeDouble(this.aabb.minY);
			buf.writeDouble(this.aabb.minZ);
			buf.writeDouble(this.aabb.maxX);
			buf.writeDouble(this.aabb.maxY);
			buf.writeDouble(this.aabb.maxZ);
			buf.writeFloat(this.w);
			buf.writeFloat(this.h);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			double minX = buf.readDouble();
			double minY = buf.readDouble();
			double minZ = buf.readDouble();
			double maxX = buf.readDouble();
			double maxY = buf.readDouble();
			double maxZ = buf.readDouble();
			this.aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
			this.w = buf.readFloat();
			this.h = buf.readFloat();
		}
	}

	public static class MotionPacket implements IMessage {
		int id;
		double mX, mY, mZ;

		public MotionPacket() {
		}

		public MotionPacket(Entity entity) {
			this.id = entity.getEntityId();
			this.mX = entity.motionX;
			this.mY = entity.motionY;
			this.mZ = entity.motionZ;
		}

		public static void sendToTracking(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new MotionPacket(entity), entity);
		}

		public static void sendToServer(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new MotionPacket(entity));
		}

		private static void setMotion(@Nullable Entity entity, MotionPacket message) {
			if (entity != null) {
				entity.motionX = message.mX;
				entity.motionY = message.mY;
				entity.motionZ = message.mZ;
			}
		}

		public static class ClientHandler implements IMessageHandler<MotionPacket, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(MotionPacket message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					setMotion(Minecraft.getMinecraft().world.getEntityByID(message.id), message);
				});
				return null;
			}
		}

		public static class ServerHandler implements IMessageHandler<MotionPacket, IMessage> {
			@Override
			public IMessage onMessage(MotionPacket message, MessageContext context) {
				WorldServer world = context.getServerHandler().player.getServerWorld();
				world.addScheduledTask(() -> {
					setMotion(world.getEntityByID(message.id), message);
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeDouble(this.mX);
			buf.writeDouble(this.mY);
			buf.writeDouble(this.mZ);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.mX = buf.readDouble();
			this.mY = buf.readDouble();
			this.mZ = buf.readDouble();
		}
	}

	public static class PositionRotationPacket {
		double posX;
		double posY;
		double posZ;
		float rotationYaw;
		float rotationPitch;

		PositionRotationPacket() { }
		
		PositionRotationPacket(double x, double y, double z, float yaw, float pitch) {
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			this.rotationYaw = yaw;
			this.rotationPitch = pitch;
		}

		PositionRotationPacket(ByteBuf buf) {
			this.posX = buf.readDouble();
			this.posY = buf.readDouble();
			this.posZ = buf.readDouble();
			this.rotationYaw = buf.readFloat();
			this.rotationPitch = buf.readFloat();
		}

		void toBytes(ByteBuf buf) {
			buf.writeDouble(this.posX);
			buf.writeDouble(this.posY);
			buf.writeDouble(this.posZ);
			buf.writeFloat(this.rotationYaw);
			buf.writeFloat(this.rotationPitch);
		}
	}

	public static class EntityPositionAndRotation implements IMessage {
		int id;
		PositionRotationPacket prp;

		public EntityPositionAndRotation() { }

		public EntityPositionAndRotation(Entity entity) {
			this.id = entity.getEntityId();
			this.prp = new PositionRotationPacket(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
		}

		public static void sendToTracking(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new EntityPositionAndRotation(entity), entity);
		}

		public static void sendToSelf(EntityPlayerMP entity) {
			NarutomodMod.PACKET_HANDLER.sendTo(new EntityPositionAndRotation(entity), entity);
		}

		public static void sendToServer(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new EntityPositionAndRotation(entity));
		}

		public static class ClientHandler implements IMessageHandler<EntityPositionAndRotation, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(EntityPositionAndRotation message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					Entity entity = mc.world.getEntityByID(message.id);
					if (entity != null) {
						entity.setPositionAndRotationDirect(message.prp.posX, message.prp.posY, message.prp.posZ,
						 message.prp.rotationYaw, message.prp.rotationPitch, 3, false);
					}
				});
				return null;
			}
		}

		public static class ServerHandler implements IMessageHandler<EntityPositionAndRotation, IMessage> {
			@Override
			public IMessage onMessage(EntityPositionAndRotation message, MessageContext context) {
				WorldServer world = context.getServerHandler().player.getServerWorld();
				world.addScheduledTask(() -> {
					Entity entity = world.getEntityByID(message.id);
					if (entity instanceof EntityPlayerMP) {
						entity.dismountRidingEntity();
						((EntityPlayerMP)entity).connection.setPlayerLocation(message.prp.posX,
						 message.prp.posY, message.prp.posZ, message.prp.rotationYaw, message.prp.rotationPitch);
					} else if (entity != null) {
						entity.setLocationAndAngles(message.prp.posX, message.prp.posY, message.prp.posZ,
						 message.prp.rotationYaw, message.prp.rotationPitch);
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			this.prp.toBytes(buf);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.prp = new PositionRotationPacket(buf);
		}
	}

	public static class EntityState implements IMessage {
		int id;
		boolean onground;

		public EntityState() { }

		public EntityState(Entity entity) {
			this.id = entity.getEntityId();
			this.onground = entity.onGround;
		}

		public static void sendToTracking(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new EntityState(entity), entity);
		}

		public static class Handler implements IMessageHandler<EntityState, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(EntityState message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					Entity entity = mc.world.getEntityByID(message.id);
					if (entity != null) {
						entity.onGround = message.onground;
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeBoolean(this.onground);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.onground = buf.readBoolean();
		}
	}

	public static class EntityNBTTag implements IMessage {
		int id;
		int dataType; // 0:remove, 1:integer, 2:double, 3:boolean
		String tag;
		int iData;
		double dData;
		boolean bData;

		public EntityNBTTag() {
		}

		public EntityNBTTag(Entity entity, String tagName) {
			this.setup(entity, tagName, 0);
		}

		public EntityNBTTag(Entity entity, String tagName, int data) {
			this.setup(entity, tagName, 1);
			this.iData = data;
		}

		public EntityNBTTag(Entity entity, String tagName, double data) {
			this.setup(entity, tagName, 2);
			this.dData = data;
		}

		public EntityNBTTag(Entity entity, String tagName, boolean data) {
			this.setup(entity, tagName, 3);
			this.bData = data;
		}

		private void setup(Entity entity, String tagName, int type) {
			this.id = entity.getEntityId();
			this.tag = tagName;
			this.dataType = type;
		}

		public static void removeAndSync(Entity entity, String tagName) {
			entity.getEntityData().removeTag(tagName);
			EntityNBTTag.sendToTracking(entity, tagName);
		}

		public static void setAndSync(Entity entity, String tagName, int i) {
			entity.getEntityData().setInteger(tagName, i);
			EntityNBTTag.sendToTracking(entity, tagName, i);
		}

		public static void setAndSync(Entity entity, String tagName, double d) {
			entity.getEntityData().setDouble(tagName, d);
			EntityNBTTag.sendToTracking(entity, tagName, d);
		}

		public static void setAndSync(Entity entity, String tagName, boolean b) {
			entity.getEntityData().setBoolean(tagName, b);
			EntityNBTTag.sendToTracking(entity, tagName, b);
		}

		public static void sendToSelf(EntityPlayerMP entity, String tagName) {
			NarutomodMod.PACKET_HANDLER.sendTo(new EntityNBTTag(entity, tagName), entity);
		}

		public static void sendToSelf(EntityPlayerMP entity, String tagName, int i) {
			NarutomodMod.PACKET_HANDLER.sendTo(new EntityNBTTag(entity, tagName, i), entity);
		}

		public static void sendToSelf(EntityPlayerMP entity, String tagName, double d) {
			NarutomodMod.PACKET_HANDLER.sendTo(new EntityNBTTag(entity, tagName, d), entity);
		}

		public static void sendToSelf(EntityPlayerMP entity, String tagName, boolean b) {
			NarutomodMod.PACKET_HANDLER.sendTo(new EntityNBTTag(entity, tagName, b), entity);
		}

		public static void sendToTracking(Entity entity, String tagName) {
			if (entity instanceof EntityPlayerMP) {
				EntityNBTTag.sendToSelf((EntityPlayerMP)entity, tagName);
			}
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new EntityNBTTag(entity, tagName), entity);
		}

		public static void sendToTracking(Entity entity, String tagName, int i) {
			if (entity instanceof EntityPlayerMP) {
				EntityNBTTag.sendToSelf((EntityPlayerMP)entity, tagName, i);
			}
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new EntityNBTTag(entity, tagName, i), entity);
		}

		public static void sendToTracking(Entity entity, String tagName, double d) {
			if (entity instanceof EntityPlayerMP) {
				EntityNBTTag.sendToSelf((EntityPlayerMP)entity, tagName, d);
			}
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new EntityNBTTag(entity, tagName, d), entity);
		}

		public static void sendToTracking(Entity entity, String tagName, boolean b) {
			if (entity instanceof EntityPlayerMP) {
				EntityNBTTag.sendToSelf((EntityPlayerMP)entity, tagName, b);
			}
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new EntityNBTTag(entity, tagName, b), entity);
		}

		public static void sendToServer(Entity entity, String tagName, int i) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new EntityNBTTag(entity, tagName, i));
		}

		public static void sendToServer(Entity entity, String tagName, double d) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new EntityNBTTag(entity, tagName, d));
		}

		public static void sendToServer(Entity entity, String tagName, boolean b) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new EntityNBTTag(entity, tagName, b));
		}

		private static void setDataTag(@Nullable Entity entity, EntityNBTTag message) {
			if (entity != null) {
				switch (message.dataType) {
					case 0:
						entity.getEntityData().removeTag(message.tag);
						break;
					case 1:
						entity.getEntityData().setInteger(message.tag, message.iData);
						break;
					case 2:
						entity.getEntityData().setDouble(message.tag, message.dData);
						break;
					case 3:
						entity.getEntityData().setBoolean(message.tag, message.bData);
						break;
				}
			}
		}

		public static class ClientHandler implements IMessageHandler<EntityNBTTag, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(EntityNBTTag message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					EntityNBTTag.setDataTag(message.id == mc.player.getEntityId() ? mc.player : mc.world.getEntityByID(message.id), message);
				});
				return null;
			}
		}

		public static class ServerHandler implements IMessageHandler<EntityNBTTag, IMessage> {
			@Override
			public IMessage onMessage(EntityNBTTag message, MessageContext context) {
				WorldServer world = context.getServerHandler().player.getServerWorld();
				world.addScheduledTask(() -> {
					EntityNBTTag.setDataTag(world.getEntityByID(message.id), message);
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeInt(this.dataType);
			writeString(buf, this.tag);
			buf.writeInt(this.iData);
			buf.writeDouble(this.dData);
			buf.writeBoolean(this.bData);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.dataType = buf.readInt();
			this.tag = readString(buf);
			this.iData = buf.readInt();
			this.dData = buf.readDouble();
			this.bData = buf.readBoolean();
		}
	}

	public static class SPacketEarthBlocks implements IMessage {
		public int id, state, blocks;
		public double x, y, z;

		public SPacketEarthBlocks() {
		}

		public SPacketEarthBlocks(Entity entity, int total, Vec3d vec, IBlockState blockstate) {
			this.id = entity.getEntityId();
			this.state = Block.getStateId(blockstate);
			this.blocks = total;
			this.x = vec.x; //(double)pos.getX();
			this.y = vec.y; //(double)pos.getY();
			this.z = vec.z; //(double)pos.getZ();
		}

		public static void sendToPlayer(EntityPlayerMP player, Entity entity, int total, Vec3d vec, IBlockState state) {
			NarutomodMod.PACKET_HANDLER.sendTo(new SPacketEarthBlocks(entity, total, vec, state), player);
		}

		public static class Handler implements IMessageHandler<SPacketEarthBlocks, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(SPacketEarthBlocks message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.id);
					if (entity instanceof EntityEarthBlocks.Base) {
						((EntityEarthBlocks.Base)entity).handleServerPacket(message);
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeInt(this.state);
			buf.writeInt(this.blocks);
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.state = buf.readInt();
			this.blocks = buf.readInt();
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
		}
	}

	public static class CPacketEarthBlocks implements IMessage {
		public int op, id;
		public float amount;

		public CPacketEarthBlocks() {
		}

		public CPacketEarthBlocks(int operation, Entity entity, float amountIn) {
			this.op = operation;
			this.id = entity.getEntityId();
			this.amount = amountIn;
		}

		public static void sendToServer(int operation, Entity entity) {
			sendToServer(operation, entity, 0f);
		}

		public static void sendToServer(int operation, Entity entity, float amountIn) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new CPacketEarthBlocks(operation, entity, amountIn));
		}

		public static class Handler implements IMessageHandler<CPacketEarthBlocks, IMessage> {
			@Override
			public IMessage onMessage(CPacketEarthBlocks message, MessageContext context) {
				EntityPlayerMP entity = context.getServerHandler().player;
				entity.getServerWorld().addScheduledTask(() -> {
					Entity entity1 = entity.world.getEntityByID(message.id);
					if (entity1 instanceof EntityEarthBlocks.Base) {
						((EntityEarthBlocks.Base)entity1).handleClientPacket(entity, message);
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.op);
			buf.writeInt(this.id);
			buf.writeFloat(this.amount);
		}

		public void fromBytes(ByteBuf buf) {
			this.op = buf.readInt();
			this.id = buf.readInt();
			this.amount = buf.readFloat();
		}
	}

	public static class CPacketVec3d implements IMessage {
		int id;
		double x, y, z;

		public CPacketVec3d() {
		}

		public CPacketVec3d(Entity entity, Vec3d vec) {
			this.id = entity.getEntityId();
			this.x = vec.x;
			this.y = vec.y;
			this.z = vec.z;
		}

		public static void sendToServer(Entity entity, Vec3d vec) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new CPacketVec3d(entity, vec));
		}

		public static class Handler implements IMessageHandler<CPacketVec3d, IMessage> {
			@Override
			public IMessage onMessage(CPacketVec3d message, MessageContext context) {
				WorldServer world = context.getServerHandler().player.getServerWorld();
				world.addScheduledTask(() -> {
					Entity entity = world.getEntityByID(message.id);
					if (entity instanceof CPacketVec3d.IHandler) {
						((CPacketVec3d.IHandler)entity).handleClientPacket(new Vec3d(message.x, message.y, message.z));
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
		}

		public static interface IHandler {
			void handleClientPacket(Vec3d vec);
		}
	}

	public static class RenderDistance implements IMessage {
		int id;
		int chunks;
		int sendbackId;

		public RenderDistance() { }

		public RenderDistance(int entityId, int ch, int sendbackToEntityId) {
			this.id = entityId;
			this.chunks = ch;
			this.sendbackId = sendbackToEntityId;
		}

		public static void sendToSelf(EntityPlayerMP player, int chunksIn, @Nullable Entity sendbacktoEntity) {
			NarutomodMod.PACKET_HANDLER.sendTo(new RenderDistance(player.getEntityId(), 
			 chunksIn, sendbacktoEntity != null ? sendbacktoEntity.getEntityId() : -1), player);
		}

		public static class ClientHandler implements IMessageHandler<RenderDistance, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(RenderDistance message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					if (message.sendbackId >= 0) {
						NarutomodMod.PACKET_HANDLER.sendToServer(
						 new RenderDistance(message.sendbackId, mc.gameSettings.renderDistanceChunks, -1));
					}
					mc.gameSettings.renderDistanceChunks = message.chunks;
				});
				return null;
			}
		}

		public static class ServerHandler implements IMessageHandler<RenderDistance, IMessage> {
			@Override
			public IMessage onMessage(RenderDistance message, MessageContext context) {
				EntityPlayerMP player = context.getServerHandler().player;
				player.getServerWorld().addScheduledTask(() -> {
					Entity entity = player.world.getEntityByID(message.id);
					if (entity instanceof RenderDistance.IHandler) {
						((RenderDistance.IHandler)entity).handleClientPacket(player, message.chunks);
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeInt(this.chunks);
			buf.writeInt(this.sendbackId);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.chunks = buf.readInt();
			this.sendbackId = buf.readInt();
		}

		public static interface IHandler {
			void handleClientPacket(EntityPlayer player, int oldChunkDistance);
		}
	}

	public static class CPacketSpawnLightning implements IMessage {
		double x;
		double y;
		double z;
		double len;
		double speedx;
		double speedy;
		double speedz;
		int args;
		int[] parms;
		
		public CPacketSpawnLightning() {
		}

		public CPacketSpawnLightning(double cx, double cy, double cz, double l, double sx, double sy, double sz, int... p) {
			this.x = cx;
			this.y = cy;
			this.z = cz;
			this.len = l;
			this.speedx = sx;
			this.speedy = sy;
			this.speedz = sz;
			this.args = p.length;
			this.parms = p;
		}

		public static void sendToServer(double cx, double cy, double cz, double l, double sx, double sy, double sz, int... p) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new CPacketSpawnLightning(cx, cy, cz, l, sx, sy, sz, p));
		}

		public static class Handler implements IMessageHandler<CPacketSpawnLightning, IMessage> {
			public IMessage onMessage(CPacketSpawnLightning message, MessageContext context) {
				WorldServer world = context.getServerHandler().player.getServerWorld();
				world.addScheduledTask(() -> {
					EntityLightningArc.spawnAsParticle(world, message.x, message.y, message.z, message.len, message.speedx, message.speedy, message.speedz, message.parms);
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			buf.writeDouble(this.len);
			buf.writeDouble(this.speedx);
			buf.writeDouble(this.speedy);
			buf.writeDouble(this.speedz);
			buf.writeInt(this.args);
			for (int j = 0; j < this.args; j++)
				buf.writeInt(this.parms[j]);
		}

		public void fromBytes(ByteBuf buf) {
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			this.len = buf.readDouble();
			this.speedx = buf.readDouble();
			this.speedy = buf.readDouble();
			this.speedz = buf.readDouble();
			this.args = buf.readInt();
			this.parms = new int[this.args];
			for (int j = 0; j < this.args; j++)
				this.parms[j] = buf.readInt();
		}
	}

	public static class MultiPartsPacket implements IMessage {
		int id;
		int parts;
		PositionRotationPacket pr[];

		public MultiPartsPacket() {
		}

		public MultiPartsPacket(Entity entity) {
			this.id = entity.getEntityId();
			Entity[] partentity = entity.getParts();
			if (partentity == null) {
				throw new IllegalArgumentException("" + entity.getClass() + "not multi-part entity!");
			} else {
				this.parts = partentity.length;
				this.pr = new PositionRotationPacket[this.parts];
				for (int i = 0; i < this.parts; i++) {
					this.pr[i] = new PositionRotationPacket(partentity[i].posX, partentity[i].posY, partentity[i].posZ,
					 partentity[i].rotationYaw, partentity[i].rotationPitch);
				}
			}
		}

		public static void sendToTracking(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new MultiPartsPacket(entity), entity);
		}

		public static void sendToServer(Entity entity) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new MultiPartsPacket(entity));
		}

		private static void setParts(@Nullable Entity entity, MultiPartsPacket message) {
			if (entity instanceof IEntityMultiPart) {
				Entity[] entityparts = entity.getParts();
				if (entityparts != null && entityparts.length == message.parts) {
					for (int i = 0; i < message.parts; i++) {
						entityparts[i].setLocationAndAngles(message.pr[i].posX, message.pr[i].posY,
						 message.pr[i].posZ, message.pr[i].rotationYaw, message.pr[i].rotationPitch);
					}
				}
			}
		}

		public static class ClientHandler implements IMessageHandler<MultiPartsPacket, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(MultiPartsPacket message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					setParts(mc.world.getEntityByID(message.id), message);
				});
				return null;
			}
		}

		public static class ServerHandler implements IMessageHandler<MultiPartsPacket, IMessage> {
			@Override
			public IMessage onMessage(MultiPartsPacket message, MessageContext context) {
				WorldServer world = context.getServerHandler().player.getServerWorld();
				world.addScheduledTask(() -> {
					setParts(world.getEntityByID(message.id), message);
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeInt(this.parts);
			for (int i = 0; i < this.parts; i++) {
				this.pr[i].toBytes(buf);
			}
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.parts = buf.readInt();
			this.pr = new PositionRotationPacket[this.parts];
			for (int i = 0; i < this.parts; i++) {
				this.pr[i] = new PositionRotationPacket(buf);
			}
		}
	}

	public static class MultiPartsSetPassengers implements IMessage {
		int id;
		int partid;
		int passengers;
		int[] passengerIds;

		public MultiPartsSetPassengers() {
		}

		public MultiPartsSetPassengers(Entity entity, int partId) {
			this.id = entity.getEntityId();
			Entity[] partentities = entity.getParts();
			if (partentities == null) {
				throw new IllegalArgumentException("" + entity.getClass() + "not multi-part entity!");
			} else {
				for (Entity part : partentities) {
					if (part.getEntityId() == partId) {
						this.partid = partId;
						List<Entity> list = part.getPassengers();
						this.passengers = list.size();
						this.passengerIds = new int[this.passengers];
						for (int i = 0; i < this.passengers; ++i) {
							this.passengerIds[i] = ((Entity)list.get(i)).getEntityId();
						}
					}
				}
				if (this.partid == 0) {
					System.err.println("Sending passengers for non-existing part");
				}
			}
		}

		public static void sendToTracking(Entity entity, int partId) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new MultiPartsSetPassengers(entity, partId), entity);
		}

		private static void setParts(@Nullable Entity entity, MultiPartsSetPassengers message) {
			if (entity instanceof IEntityMultiPart) {
				Entity[] entityparts = entity.getParts();
				if (entityparts != null && entityparts.length > 0) {
					for (Entity part : entityparts) {
						if (part.getEntityId() == message.partid) {
							for (int i : message.passengerIds) {
								Entity entity1 = entity.world.getEntityByID(i);
								if (entity1 != null) {
									entity1.startRiding(part, true);
								}
							}
						}
					}
				}
			}
		}

		public static class ClientHandler implements IMessageHandler<MultiPartsSetPassengers, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(MultiPartsSetPassengers message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					setParts(mc.world.getEntityByID(message.id), message);
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeInt(this.partid);
			buf.writeInt(this.passengers);
			for (int i = 0; i < this.passengers; i++) {
				buf.writeInt(this.passengerIds[i]);
			}
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.partid = buf.readInt();
			this.passengers = buf.readInt();
			this.passengerIds = new int[this.passengers];
			for (int i = 0; i < this.passengers; i++) {
				this.passengerIds[i] = buf.readInt();
			}
		}
	}

	public static class SoundEffectMessage implements IMessage {
		double x;
		double y;
		double z;
		String domain;
		String path;
		String category;
		float volume;
		float pitch;
		
		public SoundEffectMessage() {}
	
		public SoundEffectMessage(double _x, double _y, double _z, SoundEvent sound, SoundCategory cat, float vol, float p) {
			this.x = _x;
			this.y = _y;
			this.z = _z;
			this.domain = sound.getSoundName().getResourceDomain();
			this.path = sound.getSoundName().getResourcePath();
			this.category = cat.getName();
			this.volume = vol;
			this.pitch = p;
		}
		
		public static void sendToServer(double _x, double _y, double _z, SoundEvent sound, SoundCategory cat, float vol, float p) {
			NarutomodMod.PACKET_HANDLER.sendToServer(new SoundEffectMessage(_x, _y, _z, sound, cat, vol, p));
		}
	
		public static class Handler implements IMessageHandler<SoundEffectMessage, IMessage> {
			@Override
			public IMessage onMessage(SoundEffectMessage message, MessageContext context) {
				EntityPlayerMP entity = context.getServerHandler().player;
				entity.getServerWorld().addScheduledTask(() -> {
					entity.world.playSound(null, message.x, message.y, message.z,
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(message.domain, message.path)),
					 net.minecraft.util.SoundCategory.getByName(message.category), message.volume, message.pitch);
				});
				return null;
			}
		}
	
		public void toBytes(ByteBuf buf) {
			buf.writeDouble(this.x);
			buf.writeDouble(this.y);
			buf.writeDouble(this.z);
			writeString(buf, this.domain);
			writeString(buf, this.path);
			writeString(buf, this.category);
			buf.writeFloat(this.volume);
			buf.writeFloat(this.pitch);
		}

		public void fromBytes(ByteBuf buf) {
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			this.domain = readString(buf);
			this.path = readString(buf);
			this.category = readString(buf);
			this.volume = buf.readFloat();
			this.pitch = buf.readFloat();
		}
	}

	public static void writeString(ByteBuf buf, String str) {
		buf.writeInt(str.length());
		for (int i = 0; i < str.length(); i++) {
			buf.writeChar(str.charAt(i));
		}
	}

	public static String readString(ByteBuf buf) {
		int len = buf.readInt();
		char[] tagArray = new char[len];
		for (int i = 0; i < len; i++) {
			tagArray[i] = buf.readChar();
		}
		return new String(tagArray);
	}
}
