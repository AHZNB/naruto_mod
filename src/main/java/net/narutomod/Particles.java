package net.narutomod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.potion.PotionEffect;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;

import net.narutomod.potion.PotionCorrosion;
import net.narutomod.procedure.ProcedureUtils;

import java.util.Random;
import java.util.Map;
import java.util.List;
import io.netty.buffer.ByteBuf;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.glu.GLU;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class Particles extends ElementsNarutomodMod.ModElement {
	private static final Random rand = new Random();
	
	public Particles(ElementsNarutomodMod instance) {
		super(instance, 294);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(ParticleRenderer.Message.Handler.class, ParticleRenderer.Message.class, Side.CLIENT);
		this.elements.addNetworkMessage(BurningAsh.Message.Handler.class, BurningAsh.Message.class, Side.SERVER);
		this.elements.addNetworkMessage(AcidSpit.Message.Handler.class, AcidSpit.Message.class, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.SMOKE.getID(), new Smoke.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.SUSPENDED.getID(), new Suspend.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.FALLING_DUST.getID(), new FallingDust.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.FLAME.getID(), new Flame.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.MOB_APPEARANCE.getID(), new MobAppearance.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.BURNING_ASH.getID(), new BurningAsh.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.HOMING_ORB.getID(), new HomingOrb.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.PORTAL_SPIRAL.getID(), new SpiralPortal.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.EXPANDING_SPHERE.getID(), new ExpandingSphere.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.SEAL_FORMULA.getID(), new SealFormula.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.ACID_SPIT.getID(), new AcidSpit.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.WHIRLPOOL.getID(), new Whirlpool.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.SONIC_BOOM.getID(), new SonicBoom.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.BLOCK_DUST.getID(), new BlockDust.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.SAND.getID(), new Sand.Factory());
		Minecraft.getMinecraft().effectRenderer.registerParticle(Types.WATER_SPLASH.getID(), new WaterSplash.Factory());
	}

	public static void spawnParticle(World world, Types type, double x, double y, double z, int count, 
	 double xOff, double yOff, double zOff, double xSpeed, double ySpeed, double zSpeed, int... args) {
		spawnParticle(world, type, x, y, z, count, xOff, yOff, zOff, xSpeed, ySpeed, zSpeed, 64.0D, args);
	}

	public static void spawnParticle(World world, Types type, double x, double y, double z, int count, 
	 double xOff, double yOff, double zOff, double xSpeed, double ySpeed, double zSpeed, double renderDistance, int... args) {
		if (world.isRemote) {
			new Renderer().spawnParticles(type, x, y, z, count, xOff, yOff, zOff, xSpeed, ySpeed, zSpeed, args);
		} else {
			NarutomodMod.PACKET_HANDLER.sendToAllAround(new ParticleRenderer.Message(
			 new ParticleRenderer.MessageContents(type, x, y, z, count, xOff, yOff, zOff, xSpeed, ySpeed, zSpeed, args)),
			 new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, renderDistance));
		}
		//Renderer pr = new Renderer(world, renderDistance);
		//pr.spawnParticles(type, x, y, z, count, xOff, yOff, zOff, xSpeed, ySpeed, zSpeed, args);
		//pr.send();
	}

	public static abstract class ParticleRenderer {
		private final List<MessageContents> msgQueue = Lists.newArrayList();
		protected final World world;
		protected final double renderDistance;

		public ParticleRenderer() {
			this(null, 64d);
		}

		public ParticleRenderer(World worldIn, double renderDistanceIn) {
			this.world = worldIn;
			this.renderDistance = renderDistanceIn;
		}

		public void spawnParticles(Particles.Types type, double x, double y, double z, int count, 
		 double xOff, double yOff, double zOff, double xSpeed, double ySpeed, double zSpeed, int... args) {
			this.msgQueue.add(new MessageContents(type, x, y, z, count, xOff, yOff, zOff, xSpeed, ySpeed, zSpeed, args));
		}

		public void send() {
			if (this.world instanceof WorldServer && !this.msgQueue.isEmpty()) {
				double x = 0d;
				double y = 0d;
				double z = 0d;
				for (MessageContents msgc : this.msgQueue) {
					x += msgc.x;
					y += msgc.y;
					z += msgc.z;
				}
				x /= this.msgQueue.size();
				y /= this.msgQueue.size();
				z /= this.msgQueue.size();
				NarutomodMod.PACKET_HANDLER.sendToAllAround(new Message(this.msgQueue),
				  new NetworkRegistry.TargetPoint(this.world.provider.getDimension(), x, y, z, this.renderDistance));
			}
		}

		public static class MessageContents {
			Types type;
			int count;
			double x, y, z;
			double ox, oy, oz;
			double speedx, speedy, speedz;
			int args;
			int[] parms;

			public MessageContents(Types typeIn, double xIn, double yIn, double zIn, int countIn,
			 double xOff, double yOff, double zOff, double xSpeed, double ySpeed, double zSpeed, int... argsIn) {
				this.type = typeIn;
				this.count = countIn;
				this.x = xIn;
				this.y = yIn;
				this.z = zIn;
				this.ox = xOff;
				this.oy = yOff;
				this.oz = zOff;
				this.speedx = xSpeed;
				this.speedy = ySpeed;
				this.speedz = zSpeed;
				this.args = argsIn.length;
				this.parms = argsIn;
			}

			public MessageContents(ByteBuf buf) {
				this.fromBytes(buf);
			}

			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.type.getID());
				buf.writeInt(this.count);
				buf.writeDouble(this.x);
				buf.writeDouble(this.y);
				buf.writeDouble(this.z);
				buf.writeDouble(this.ox);
				buf.writeDouble(this.oy);
				buf.writeDouble(this.oz);
				buf.writeDouble(this.speedx);
				buf.writeDouble(this.speedy);
				buf.writeDouble(this.speedz);
				buf.writeInt(this.args);
				for (int j = 0; j < this.type.getArgsCount() && j < this.args; j++)
					buf.writeInt(this.parms[j]);
			}
	
			public void fromBytes(ByteBuf buf) {
				this.type = Types.getTypeFromId(buf.readInt());
				this.count = buf.readInt();
				this.x = buf.readDouble();
				this.y = buf.readDouble();
				this.z = buf.readDouble();
				this.ox = buf.readDouble();
				this.oy = buf.readDouble();
				this.oz = buf.readDouble();
				this.speedx = buf.readDouble();
				this.speedy = buf.readDouble();
				this.speedz = buf.readDouble();
				this.args = buf.readInt();
				int i = Math.min(this.type.getArgsCount(), this.args);
				this.parms = new int[i];
				for (int j = 0; j < i; j++)
					this.parms[j] = buf.readInt();
			}

			@Override
			public String toString() {
				String s = "";
				for (int i = 0; i < parms.length; i++) {
					s += parms[i] + ", ";
				}
				return ""+this.type+", "+count+", ("+x+", "+y+", "+z+"), ("+ox+", "+oy+", "+oz+"), ("+speedx+", "+speedy+", "+speedz+"), "+s;
			}
		}
		
		public static class Message implements IMessage {
			List<MessageContents> list;

			public Message() {}

			public Message(MessageContents msgcontents) {
				this.list = Lists.newArrayList(msgcontents);
			}

			public Message(List<MessageContents> listIn) {
				this.list = listIn;
			}

			@Override
			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.list.size());
				for (MessageContents msg : this.list) {
					msg.toBytes(buf);
				}
			}

			@Override
			public void fromBytes(ByteBuf buf) {
				int j = buf.readInt();
				this.list = Lists.newArrayList();
				for (int i = 0; i < j; i++) {
					this.list.add(new MessageContents(buf));
				}
			}

			public static class Handler implements IMessageHandler<Message, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(Message message, MessageContext context) {
					Minecraft.getMinecraft().addScheduledTask(() -> {
						Renderer render = new Renderer();
						for (MessageContents msgc : message.list) {
							render.spawnParticles(msgc.type, msgc.x, msgc.y, msgc.z,
							 msgc.count, msgc.ox, msgc.oy, msgc.oz, msgc.speedx, msgc.speedy, msgc.speedz, msgc.parms);
						}
					});
					return null;
				}
			}
		}
	}
	
	public static class Renderer extends ParticleRenderer {
		public Renderer() {
			super();
		}

		public Renderer(World worldIn) {
			this(worldIn, 64d);
		}

		public Renderer(World worldIn, double renderDistanceIn) {
			super(worldIn, renderDistanceIn);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void spawnParticles(Particles.Types type, double x, double y, double z, int count, 
		 double xOff, double yOff, double zOff, double xSpeed, double ySpeed, double zSpeed, int... args) {
			if (this.world instanceof WorldServer) {
				super.spawnParticles(type, x, y, z, count, xOff, yOff, zOff, xSpeed, ySpeed, zSpeed, args);
			} else {
				Minecraft mc = Minecraft.getMinecraft();
				if (mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
					for (int i = 0; i < count; i++) {
						double d1 = rand.nextGaussian() * xOff;
						double d2 = rand.nextGaussian() * yOff;
						double d3 = rand.nextGaussian() * zOff;
						if (count > 5) {
							xSpeed *= rand.nextDouble() * 0.3d + 0.85d;
							ySpeed *= rand.nextDouble() * 0.3d + 0.85d;
							zSpeed *= rand.nextDouble() * 0.3d + 0.85d;
						}
						mc.effectRenderer.spawnEffectParticle(type.getID(), x + d1, y + d2, z + d3, xSpeed, ySpeed, zSpeed, args);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Smoke extends Particle {
		private static final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/particles.png");
		protected final TextureManager textureManager;
		protected float smokeParticleScale;
		private int particleBrightness;
		protected double floatMotionY;
		private int viewerId;

		protected Smoke(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, 
		 double motionX, double motionY, double motionZ, int color, float scale, int maxAge, int brightness, 
		 int playerId, double floatSpeed) {
			super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
			//super(worldIn, xCoordIn, yCoordIn, zCoordIn);
			this.particleTextureIndexY = 2;
			this.textureManager = Minecraft.getMinecraft().getTextureManager();
			this.motionX *= 0.1D;
			this.motionY *= 0.1D;
			this.motionZ *= 0.1D;
			this.motionX += motionX;
			this.motionY += motionY;
			this.motionZ += motionZ;
			this.particleAlpha = (color >> 24 & 0xFF) / 255.0F;
			this.particleRed = (color >> 16 & 0xFF) / 255.0F;
			this.particleGreen = (color >> 8 & 0xFF) / 255.0F;
			this.particleBlue = (color & 0xFF) / 255.0F;
			float f = (this.rand.nextFloat() - 0.5F) * 0.1F;
			if (this.particleRed + f >= 0.0F && this.particleRed + f <= 1.0F)
				this.particleRed += f;
			if (this.particleGreen + f >= 0.0F && this.particleGreen + f <= 1.0F)
				this.particleGreen += f;
			if (this.particleBlue + f >= 0.0F && this.particleBlue + f <= 1.0F)
				this.particleBlue += f;
			this.particleScale *= 0.75F;
			this.particleScale *= scale;
			this.smokeParticleScale = this.particleScale;
			if (maxAge > 0) {
				this.particleMaxAge = maxAge;
			} else {
				this.particleMaxAge = (int) (8.0D / (this.rand.nextDouble() * 0.8D + 0.2D));
				this.particleMaxAge = (int) (this.particleMaxAge * scale);
			}
			if (brightness != 0) {
				this.particleBrightness = brightness;
			}
			this.floatMotionY = floatSpeed;
			this.viewerId = playerId >= 0 ? playerId : -1;
		}

		public int getBrightnessForRender(float p_189214_1_) {
			return this.particleBrightness != 0 ? ((this.particleBrightness << 16) | this.particleBrightness) 
			  : super.getBrightnessForRender(p_189214_1_);
		}

		public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ,
				float rotationXY, float rotationXZ) {
			if (entityIn.getEntityId() != this.viewerId || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
				float f = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge;
				this.particleScale = this.smokeParticleScale * MathHelper.clamp(f * 32.0F, 0.0F, 1.0F);
				//super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
		     	this.textureManager.bindTexture(TEXTURE);
		        float f0 = (float)this.particleTextureIndexX / 8.0F;
		        float f1 = f0 + 0.124875F;
		        float f2 = (float)this.particleTextureIndexY / 8.0F;
		        float f3 = f2 + 0.124875F;
		        float f4 = 0.1F * this.particleScale;
		        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		        float f8 = this.particleAlpha * (1.0F - f * f * 0.5F);
		        int i = this.getBrightnessForRender(partialTicks);
		        int j = i >> 16 & 65535;
		        int k = i & 65535;
		        Vec3d[] avec3d = new Vec3d[] {new Vec3d((double)(-rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(-rotationYZ * f4 - rotationXZ * f4)), new Vec3d((double)(-rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(-rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(rotationYZ * f4 - rotationXZ * f4))};
		        buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, f8).lightmap(j, k).endVertex();
		        buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, f8).lightmap(j, k).endVertex();
		        buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f0, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, f8).lightmap(j, k).endVertex();
		        buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f0, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, f8).lightmap(j, k).endVertex();
			}
		}

		public void onUpdate() {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (this.particleMaxAge == 0 || this.particleAge++ >= this.particleMaxAge) {
				this.setExpired();
				return;
			}
			//this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
			this.particleTextureIndexX = MathHelper.clamp(7 - this.particleAge * 8 / this.particleMaxAge, 0, 7);
			this.motionY += this.floatMotionY;
			this.move(this.motionX, this.motionY, this.motionZ);
			if (this.posY == this.prevPosY) {
				this.motionX *= 1.1D;
				this.motionZ *= 1.1D;
			}
			this.motionX *= 0.9599999785423279D;
			this.motionY *= 0.9599999785423279D;
			this.motionZ *= 0.9599999785423279D;
			if (this.onGround) {
				this.motionX *= 0.699999988079071D;
				this.motionZ *= 0.699999988079071D;
			}
		}

		public boolean shouldDisableDepth() {
			return true;
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				double arg5 = (parameters.length > 5) ? (double)parameters[5] / 1000.0D : 0.004D;
				int arg4 = (parameters.length > 4) ? parameters[4] : -1;
				int arg3 = (parameters.length > 3) ? parameters[3] : 0;
				int arg2 = (parameters.length > 2) ? parameters[2] : 0;
				float arg1 = (parameters.length > 1) ? ((float)parameters[1] / 10.0F) : 1.0F;
				int arg0 = (parameters.length > 0) ? parameters[0] : -1;
				return new Smoke(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, arg0, arg1, arg2, arg3, arg4, arg5);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Suspend extends Particle {
		protected Suspend(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn,
				int color, float scale, int age) {
			super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
			this.particleAlpha = (color >> 24 & 0xFF) / 255.0F;
			this.particleRed = (color >> 16 & 0xFF) / 255.0F;
			this.particleGreen = (color >> 8 & 0xFF) / 255.0F;
			this.particleBlue = (color & 0xFF) / 255.0F;
			float f = (this.rand.nextFloat() - 0.5F) * 0.1F;
			if (this.particleRed + f >= 0.0F && this.particleRed + f <= 1.0F)
				this.particleRed += f;
			if (this.particleGreen + f >= 0.0F && this.particleGreen + f <= 1.0F)
				this.particleGreen += f;
			if (this.particleBlue + f >= 0.0F && this.particleBlue + f <= 1.0F)
				this.particleBlue += f;
			this.setParticleTextureIndex(0);
			this.setSize(0.02F * scale, 0.02F * scale);
			this.particleScale *= scale * (this.rand.nextFloat() * 0.6F + 0.5F);
			this.motionX *= 0.019999999552965164D;
			this.motionY *= 0.019999999552965164D;
			this.motionZ *= 0.019999999552965164D;
			this.motionX += xSpeedIn;
			this.motionY += ySpeedIn;
			this.motionZ += zSpeedIn;
			if (age > 0) {
				this.particleMaxAge = (int)((this.rand.nextFloat() * 0.4f + 0.8f) * age);
			} else {
				this.particleMaxAge = (int)(20.0D / (this.rand.nextDouble() * 0.8D + 0.2D));
			}
		}

		public void onUpdate() {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.8D;
			this.motionY *= 0.8D;
			this.motionZ *= 0.8D;
			if (this.particleMaxAge-- <= 0)
				this.setExpired();
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				int arg2 = (parameters.length > 2) ? parameters[2] : 0;
				float arg1 = (parameters.length > 1) ? ((float)parameters[1] / 10f) : 1f;
				int arg0 = (parameters.length > 0) ? parameters[0] : -1;
				return new Suspend(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, arg0, arg1, arg2);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FallingDust extends ParticleSimpleAnimated {
		public FallingDust(World world, double x, double y, double z, double xMotion, double yMotion, double zMotion, int color) {
			super(world, x, y, z, 176, 4, -0.025F);
			this.motionX = xMotion;
			this.motionY = yMotion;
			this.motionZ = zMotion;
			this.particleAlpha = (color >> 24 & 0xFF) / 255.0F;
			this.particleRed = (color >> 16 & 0xFF) / 255.0F;
			this.particleGreen = (color >> 8 & 0xFF) / 255.0F;
			this.particleBlue = (color & 0xFF) / 255.0F;
			float f = (this.rand.nextFloat() - 0.5F) * 0.1F;
			if (this.particleRed + f >= 0.0F && this.particleRed + f <= 1.0F)
				this.particleRed += f;
			if (this.particleGreen + f >= 0.0F && this.particleGreen + f <= 1.0F)
				this.particleGreen += f;
			if (this.particleBlue + f >= 0.0F && this.particleBlue + f <= 1.0F)
				this.particleBlue += f;
			this.particleScale *= 0.75F;
			this.particleMaxAge = 60 + this.rand.nextInt(12);
			this.setBaseAirFriction(0.2F);
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				int arg0 = (parameters.length > 0) ? parameters[0] : -1;
				return new FallingDust(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, arg0);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Flame extends Particle {
		private static final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/particles.png");
		private final TextureManager textureManager;
		private final float flameScale;

		protected Flame(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn,
				int color, float scale) {
			super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
			this.motionX = this.motionX * 0.009999999776482582D + xSpeedIn;
			this.motionY = this.motionY * 0.009999999776482582D + ySpeedIn;
			this.motionZ = this.motionZ * 0.009999999776482582D + zSpeedIn;
			this.posX += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
			this.posY += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
			this.posZ += (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
			this.flameScale = this.particleScale * scale;
			this.particleAlpha = (color >> 24 & 0xFF) / 255.0F;
			this.particleRed = (color >> 16 & 0xFF) / 255.0F;
			this.particleGreen = (color >> 8 & 0xFF) / 255.0F;
			this.particleBlue = (color & 0xFF) / 255.0F;
			float f = (this.rand.nextFloat() - 0.5F) * 0.1F;
			if (this.particleRed + f >= 0.0F && this.particleRed + f <= 1.0F)
				this.particleRed += f;
			if (this.particleGreen + f >= 0.0F && this.particleGreen + f <= 1.0F)
				this.particleGreen += f;
			if (this.particleBlue + f >= 0.0F && this.particleBlue + f <= 1.0F)
				this.particleBlue += f;
			this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
			//this.setParticleTextureIndex(48);
			this.particleTextureIndexY = 1;
			this.textureManager = Minecraft.getMinecraft().getTextureManager();
		}

		//public void move(double x, double y, double z) {
		//	this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
		//	this.resetPositionToBB();
		//}

	    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ,
	     float rotationYZ, float rotationXY, float rotationXZ) {
	     	this.textureManager.bindTexture(TEXTURE);
			float f = Math.min(((float)this.particleAge + partialTicks) / (float) this.particleMaxAge, 1.0F);
			//float f8 = 1.0F - f * f * 0.8F;
			float f11 = f - 0.5f;
			float f8 = 1f - f11 * f11 * 3.5f;
			this.particleScale = this.flameScale * f8;
	        float f0 = (float)this.particleTextureIndexX / 8.0F;
	        float f1 = f0 + 0.124875F;
	        float f2 = (float)this.particleTextureIndexY / 8.0F;
	        float f3 = f2 + 0.124875F;
	        float f4 = 0.1F * this.particleScale;
	        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
	        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
	        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
	        float f9 = this.particleAlpha * f8;
	        float f10 = this.particleGreen * (1.0F - f);
	        int j = 0xF0;
	        int k = 0xF0;
	        Vec3d[] avec3d = new Vec3d[] {new Vec3d((double)(-rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(-rotationYZ * f4 - rotationXZ * f4)), new Vec3d((double)(-rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(-rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(rotationYZ * f4 - rotationXZ * f4))};
	        buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).color(this.particleRed, f10, this.particleBlue, f9).lightmap(j, k).endVertex();
	        buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).color(this.particleRed, f10, this.particleBlue, f9).lightmap(j, k).endVertex();
	        buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f0, (double)f2).color(this.particleRed, f10, this.particleBlue, f9).lightmap(j, k).endVertex();
	        buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f0, (double)f3).color(this.particleRed, f10, this.particleBlue, f9).lightmap(j, k).endVertex();
	    }

		public void onUpdate() {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (this.particleAge++ >= this.particleMaxAge) {
				this.setExpired();
			}
			this.particleTextureIndexX = (this.particleAge / 2) % 8;
			this.motionY += 0.003D;
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.9599999785423279D;
			this.motionY *= 0.9599999785423279D;
			this.motionZ *= 0.9599999785423279D;
			if (this.onGround) {
				this.motionX *= 0.699999988079071D;
				this.motionZ *= 0.699999988079071D;
			}
		}

		public boolean shouldDisableDepth() {
			return true;
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				float arg1 = (parameters.length > 1) ? (float)parameters[1] / 10.0F : 1.0F;
				int arg0 = (parameters.length > 0) ? parameters[0] : -1;
				return new Flame(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, arg0, arg1);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class MobAppearance extends Particle {
		private static final Map<Integer, Class <? extends Entity>> particleEntities = ImmutableMap.of(
			net.narutomod.entity.EntityItachi.ENTITYID_RANGED, net.narutomod.entity.EntityItachi.Entity4MobAppearance.class
		);
	    private Entity entity;
	    private int entityTypeId;
	
	    protected MobAppearance(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, int id) {
	        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
	        this.particleRed = 1.0F;
	        this.particleGreen = 1.0F;
	        this.particleBlue = 1.0F;
	        this.motionX = 0.0D;
	        this.motionY = 0.0D;
	        this.motionZ = 0.0D;
	        this.particleGravity = 0.0F;
	        this.particleMaxAge = 30;
	        this.entityTypeId = id;
	        if (particleEntities.containsKey(id)) {
	            try {
	            	this.entity = particleEntities.get(this.entityTypeId).getConstructor(World.class).newInstance(this.world);
	            } catch (Exception e) {
	            	throw new RuntimeException("Unregistered custom mob appearance particle type " + this.entityTypeId, e);
	            }
	        }
	    }
	
	    public int getFXLayer() {
	        return 3;
	    }
	
	    public void onUpdate() {
	        super.onUpdate();
			if (this.entity == null) {
	        	Entity entity1 = this.world.getEntityByID(this.entityTypeId);
	        	if (entity1 instanceof EntityPlayer) {
	        		this.entity = entity1;
	        	}
			}
	    }
	
	    @Override
	    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, 
	    		float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
	        if (this.entity != null) {
	            RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
	            rendermanager.setRenderPosition(Particle.interpPosX, Particle.interpPosY, Particle.interpPosZ);
	            //float f = 0.42553192F;
	            float f1 = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge;
	            GlStateManager.depthMask(true);
	            GlStateManager.enableBlend();
	            GlStateManager.enableDepth();
	            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
	            GlStateManager.pushMatrix();
	            float f3 = 0.05F + 0.5F * MathHelper.sin(f1 * (float)Math.PI);
	            GlStateManager.color(1.0F, 1.0F, 1.0F, f3);
	            GlStateManager.translate(0.0F, 1.5F, 0.0F);
	            GlStateManager.rotate(180.0F - entityIn.rotationYaw, 0.0F, 1.0F, 0.0F);
	            //GlStateManager.rotate(60.0F - 150.0F * f1 - entityIn.rotationPitch, 1.0F, 0.0F, 0.0F);
	            float f4 = 1f + f1 * 1.5f;
	            GlStateManager.translate(0.0F, -f4 * 1.5f, -1.5F);
	            GlStateManager.scale(f4, f4, f4);
	            float f5 = this.entity.rotationYaw;
	            float f6 = this.entity.prevRotationYaw;
	            float f7 = 0f;
	            float f8 = 0f;
	            this.entity.rotationYaw = 0.0F;
	            this.entity.prevRotationYaw = 0.0F;
	            if (this.entity instanceof EntityLivingBase) {
	            	f7 = ((EntityLivingBase)this.entity).rotationYawHead;
	            	f8 = ((EntityLivingBase)this.entity).prevRotationYawHead;
		            ((EntityLivingBase)this.entity).rotationYawHead = 0.0F;
		            ((EntityLivingBase)this.entity).prevRotationYawHead = 0.0F;
	            }
	            rendermanager.renderEntity(this.entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
	            this.entity.rotationYaw = f5;
	            this.entity.prevRotationYaw = f6;
	            if (this.entity instanceof EntityLivingBase) {
		            ((EntityLivingBase)this.entity).rotationYawHead = f7;
		            ((EntityLivingBase)this.entity).prevRotationYawHead = f8;
	            }
	            GlStateManager.popMatrix();
	            GlStateManager.enableDepth();
	        }
	    }
	
	    @SideOnly(Side.CLIENT)
	    public static class Factory implements IParticleFactory {
	        public Particle createParticle(int particleID, World worldIn, double xCoordIn, 
	        		double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... parameters) {
                int arg0 = (parameters.length > 0) ? parameters[0] : net.narutomod.entity.EntityItachi.ENTITYID;
                return new MobAppearance(worldIn, xCoordIn, yCoordIn, zCoordIn, arg0);
            }
        }
	}

	@SideOnly(Side.CLIENT)
	public static class HomingOrb extends Particle {
		protected HomingOrb(World worldIn, double xIn, double yIn, double zIn, double radiusIn, float scaleIn) {
			super(worldIn, xIn, yIn, zIn, 0.0D, 0.0D, 0.0D);
			if (radiusIn == 0d) {
				radiusIn = Math.random() * 4.0D + 1.0D;
			}
			this.setPosition(xIn + this.rand.nextGaussian() * radiusIn, yIn + this.rand.nextGaussian() * radiusIn, zIn + this.rand.nextGaussian() * radiusIn);
			double d0 = xIn - this.posX;
			double d1 = yIn - this.posY;
			double d2 = zIn - this.posZ;
			this.particleMaxAge = (int)(Math.random() * 10.0D) + 50;
			this.motionX = d0 / (double)this.particleMaxAge;
			this.motionY = d1 / (double)this.particleMaxAge;
			this.motionZ = d2 / (double)this.particleMaxAge;
			if (scaleIn != 0f) {
				this.particleScale *= scaleIn;
			}
			this.setParticleTextureIndex(this.rand.nextBoolean() ? 49 : 97);
		}

	    @Override
	    public void move(double x, double y, double z) {
	        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
	        this.resetPositionToBB();
	    }
	    
		public void onUpdate() {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (this.particleAge++ >= this.particleMaxAge)
				this.setExpired();
			this.move(this.motionX, this.motionY, this.motionZ);
		}

		public int getBrightnessForRender(float p_189214_1_) {
			return (240 << 16) | 240;
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				float arg1 = parameters.length > 1 ? ((float)parameters[1] / 10f) : 0f;
				double arg0 = parameters.length > 0 ? (double)parameters[0] : 0d;
				//double speed = (xSpeedIn + ySpeedIn + zSpeedIn) / 3d;
				return new HomingOrb(worldIn, xCoordIn, yCoordIn, zCoordIn, arg0, arg1);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class SpiralPortal extends Particle {
		private final double portalX;
		private final double portalY;
		private final double portalZ;
		private final ProcedureUtils.Vec2f ogRotation;
		private final double ogLength;

		protected SpiralPortal(World worldIn, double xIn, double yIn, double zIn, double radiusIn, int color, float scaleIn) {
			super(worldIn, xIn, yIn, zIn, 0.0D, 0.0D, 0.0D);
			if (radiusIn == 0d) {
				radiusIn = this.rand.nextDouble() * 4.0D + 1.0D;
			}
			this.setPosition(xIn + (this.rand.nextDouble()-0.5d) * radiusIn * 2.0d,
			 yIn + (this.rand.nextDouble()-0.5d) * radiusIn * 2.0d, zIn + (this.rand.nextDouble()-0.5d) * radiusIn * 2.0d);
			this.portalX = xIn;
			this.portalY = yIn;
			this.portalZ = zIn;
			this.particleMaxAge = this.rand.nextInt(11) + 50;
			Vec3d vec = new Vec3d(this.posX - xIn, this.posY - yIn, this.posZ - zIn);
			this.ogRotation = ProcedureUtils.getYawPitchFromVec(vec);
			this.ogLength = vec.lengthVector();
			this.setParticleTextureIndex(0);
			this.particleAlpha = (color >> 24 & 0xFF) / 255.0F;
			this.particleRed = (color >> 16 & 0xFF) / 255.0F;
			this.particleGreen = (color >> 8 & 0xFF) / 255.0F;
			this.particleBlue = (color & 0xFF) / 255.0F;
			float f = (this.rand.nextFloat() - 0.5F) * 0.1F;
			if (this.particleRed + f >= 0.0F && this.particleRed + f <= 1.0F)
				this.particleRed += f;
			if (this.particleGreen + f >= 0.0F && this.particleGreen + f <= 1.0F)
				this.particleGreen += f;
			if (this.particleBlue + f >= 0.0F && this.particleBlue + f <= 1.0F)
				this.particleBlue += f;
			if (scaleIn != 0f) {
				this.particleScale *= scaleIn;
			}
		}

	    @Override
	    public void move(double x, double y, double z) {
	        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
	        this.resetPositionToBB();
	    }
	    
	    @Override
		public void onUpdate() {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (++this.particleAge >= this.particleMaxAge) {
				this.setExpired();
			}
			float f = (float)this.particleAge / (float)this.particleMaxAge;
			float f1 = (this.ogRotation.x + 180f) / 90f;
			float f2 = f1 > 3f ? -f : f1 > 2f ? f : f1 > 1f ? -f : f;
			Vec3d vec = new Vec3d(0d, 0d, this.ogLength * (1d - f)).rotatePitch((-this.ogRotation.y + f2 * 360f) * (float)Math.PI / 180f)
			 .rotateYaw(-this.ogRotation.x * (float)Math.PI / 180f).addVector(this.portalX, this.portalY, this.portalZ)
			 .subtract(this.posX, this.posY, this.posZ);
			this.motionX = vec.x;
			this.motionY = vec.y;
			this.motionZ = vec.z;
			this.move(this.motionX, this.motionY, this.motionZ);
		}

	    @Override
		public boolean shouldDisableDepth() {
			return true;
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				float arg2 = parameters.length > 2 ? ((float)parameters[2] / 10f) : 0f;
				int arg1 = (parameters.length > 1) ? parameters[1] : -1;
				double arg0 = parameters.length > 0 ? (double)parameters[0] : 0d;
				return new SpiralPortal(worldIn, xCoordIn, yCoordIn, zCoordIn, arg0, arg1, arg2);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class BurningAsh extends Smoke {
		private final Entity excludeEntity;

		protected BurningAsh(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn,
		  int excludeEntityId) {
			super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, 0xFF606060, 8f + Particles.rand.nextFloat() * 5f, 100, 0, -1, 0f);
			this.excludeEntity = worldIn.getEntityByID(excludeEntityId);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.isAlive()) {
				List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox().grow(1));
				if (!list.isEmpty()) {
					for (EntityLivingBase entity : list) {
						if (!entity.equals(this.excludeEntity)) {
							NarutomodMod.PACKET_HANDLER.sendToServer(new Message(entity.getEntityId(), 10));
						}
					}
				}
			}
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				int arg0 = (parameters.length > 0) ? parameters[0] : -1;
				return new BurningAsh(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, arg0);
			}
		}

		public static class Message implements IMessage {
			int id;
			int sec;
			
			public Message() {
			}
	
			public Message(int entityId, int seconds) {
				this.id = entityId;
				this.sec = seconds;
			}
	
			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.id);
				buf.writeInt(this.sec);
			}
	
			public void fromBytes(ByteBuf buf) {
				this.id = buf.readInt();
				this.sec = buf.readInt();
			}

			public static class Handler implements IMessageHandler<Message, IMessage> {
				@Override
				public IMessage onMessage(Message message, MessageContext context) {
					WorldServer world = context.getServerHandler().player.getServerWorld();
					world.addScheduledTask(() -> {
						Entity entity = world.getEntityByID(message.id);
						if (entity instanceof EntityLivingBase) {
							entity.setFire(message.sec);
						}
					});
					return null;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class AcidSpit extends Smoke {
		private static int PARTICLE_ID = -1;
		private final int id = PARTICLE_ID--;
		private final Entity excludedEntity;
		private EntityLivingBase affectedEntity;
		private double heightOffset;

		protected AcidSpit(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, 
		 double xSpeedIn, double ySpeedIn, double zSpeedIn, int excludeEntityId, int color) {
			super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, color, 0.5f + Particles.rand.nextFloat() * 4.5f, 0, 0, -1, -0.005f);
			this.excludedEntity = worldIn.getEntityByID(excludeEntityId);
			this.setSize(this.width * this.particleScale, this.height * this.particleScale);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.isAlive()) {
				if (this.affectedEntity == null) {
					for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox())) {
						if (!entity.equals(this.excludedEntity)) {
							NarutomodMod.PACKET_HANDLER.sendToServer(new Message(entity.getEntityId(), this.particleMaxAge - this.particleAge));
							this.affectedEntity = entity;
							this.heightOffset = this.posY - entity.posY;
						}
					}
				} else {
					this.setPosition(this.affectedEntity.posX, this.affectedEntity.posY + this.heightOffset, this.affectedEntity.posZ);
					this.heightOffset += this.floatMotionY;
				}
				BlockPos pos = ProcedureUtils.getNearestNonAirBlock(this.world, this.getBoundingBox().grow(0.01d),
				 new BlockPos(this.posX, this.posY, this.posZ), 50f, true);
				if (pos != null) {
					Minecraft.getMinecraft().renderGlobal.sendBlockBreakProgress(this.id, pos, 5);
				}
			}
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				int arg1 = (parameters.length > 1) ? parameters[1] : 0x80ffd6ba;
				int arg0 = (parameters.length > 0) ? parameters[0] : -1;
				return new AcidSpit(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, arg0, arg1);
			}
		}

		public static class Message implements IMessage {
			int id;
			int ticks;
			
			public Message() {
			}
	
			public Message(int entityId, int t) {
				this.id = entityId;
				this.ticks = t;
			}
	
			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.id);
				buf.writeInt(this.ticks);
			}
	
			public void fromBytes(ByteBuf buf) {
				this.id = buf.readInt();
				this.ticks = buf.readInt();
			}

			public static class Handler implements IMessageHandler<Message, IMessage> {
				@Override
				public IMessage onMessage(Message message, MessageContext context) {
					WorldServer world = context.getServerHandler().player.getServerWorld();
					world.addScheduledTask(() -> {
						Entity entity = world.getEntityByID(message.id);
						if (entity instanceof EntityLivingBase) {
							((EntityLivingBase)entity).addPotionEffect(new PotionEffect(PotionCorrosion.potion, message.ticks, 1));
						}
					});
					return null;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class ExpandingSphere extends Particle {
		private static final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/white_square.png");
		private int sphereIdOutside;
		private int sphereIdInside;
		private Sphere sphere;
		private final TextureManager textureManager;

		protected ExpandingSphere(TextureManager textureManagerIn, World worldIn, double x, double y, double z,
		 double speedX, double speedY, double speedZ, float size, int life, int color) {
			super(worldIn, x, y, z, 0.0D, 0.0D, 0.0D);
			this.motionX = speedX;
			this.motionY = speedY;
			this.motionZ = speedZ;
			this.particleRed = (color >> 16 & 0xFF) / 255.0F;
			this.particleGreen = (color >> 8 & 0xFF) / 255.0F;
			this.particleBlue = (color & 0xFF) / 255.0F;
			this.particleScale = size;
			this.particleMaxAge = life;
			this.textureManager = textureManagerIn;
		}

		@Override
		public void onUpdate() {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (this.particleAge++ >= this.particleMaxAge) {
				this.setExpired();
			}
			this.move(this.motionX, this.motionY, this.motionZ);
		}

		private void compileDisplayList() {
			this.sphere = new Sphere();
			this.sphere.setDrawStyle(GLU.GLU_FILL);
			this.sphere.setNormals(GLU.GLU_SMOOTH);
			this.sphere.setOrientation(GLU.GLU_OUTSIDE);
			this.sphereIdOutside = GLAllocation.generateDisplayLists(1);
			GlStateManager.glNewList(this.sphereIdOutside, org.lwjgl.opengl.GL11.GL_COMPILE);
			this.textureManager.bindTexture(TEXTURE);
			this.sphere.draw(1.0F, 32, 32);
			GlStateManager.glEndList();	
			this.sphere.setOrientation(GLU.GLU_INSIDE);
			this.sphereIdInside = GLAllocation.generateDisplayLists(1);
			GlStateManager.glNewList(this.sphereIdInside, org.lwjgl.opengl.GL11.GL_COMPILE);
			this.textureManager.bindTexture(TEXTURE);
			this.sphere.draw(1.0F, 32, 32);
			GlStateManager.glEndList();
		}

		@Override
		public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, 
		 float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
	        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
	        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
	        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
	        float f1 = 1.0f;
			if (this.particleAge > 0.8f * this.particleMaxAge) {
				f1 = 1.0f - (this.particleAge - 0.8f * this.particleMaxAge) / (0.2f * this.particleMaxAge);
			}
			if (this.sphere == null) {
				this.compileDisplayList();
			}
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(0x204, 0.0f);
			GlStateManager.enableBlend();
			GlStateManager.depthMask(false);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xF0, 0xF0);
			float scale = this.particleAge * 0.5F;
			for (int i = 0; scale > 0f; scale = (this.particleAge - ++i) * 0.5F) {
				if (scale <= this.particleScale) {
					float r = 1.0F - 0.05F * i * (1.0F - this.particleRed);
					float g = 1.0F - 0.05F * i * (1.0F - this.particleGreen);
					float b = 1.0F - 0.05F * i * (1.0F - this.particleBlue);
					GlStateManager.pushMatrix();
					GlStateManager.color(r, g, b, 0.05F * f1);
					GlStateManager.translate(f5, f6, f7);
					GlStateManager.scale(scale, scale, scale);
					GlStateManager.callList(this.sphereIdOutside);
					GlStateManager.callList(this.sphereIdInside);
					GlStateManager.popMatrix();
				}
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableLighting();
			GlStateManager.depthMask(true);
			GlStateManager.alphaFunc(0x204, 0.1f);
		}

		@Override
	    public int getFXLayer() {
	        return 3;
	    }

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double x, double y, double z, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				int arg2 = (parameters.length > 2) ? parameters[2] : -1;
				int arg1 = (parameters.length > 1) ? parameters[1] : 1;
				float arg0 = (parameters.length > 0) ? (float)parameters[0] / 10f: 1f;
				return new ExpandingSphere(Minecraft.getMinecraft().getTextureManager(), worldIn, x, y, z, 
				 xSpeedIn, ySpeedIn, zSpeedIn, arg0, arg1, arg2);
			}
		}
	}

	public static abstract class TextureAsParticle extends Particle {
		protected final TextureManager textureManager;
		protected int particleBrightness;
		protected float rotateY;
		protected float rotateX;
		protected float rotateZ;

		protected TextureAsParticle(TextureManager textureManagerIn, World worldIn, double x, double y, double z, 
		 double speedX, double speedY, double speedZ, int color, float scale, int maxAge, int brightness) {
			super(worldIn, x, y, z, 0.0d, 0.0d, 0.0d);
			this.textureManager = textureManagerIn;
			this.particleAlpha = (color >> 24 & 0xFF) / 255.0F;
			this.particleRed = (color >> 16 & 0xFF) / 255.0F;
			this.particleGreen = (color >> 8 & 0xFF) / 255.0F;
			this.particleBlue = (color & 0xFF) / 255.0F;
			float f = (this.rand.nextFloat() - 0.5F) * 0.1F;
			if (this.particleRed + f >= 0.0F && this.particleRed + f <= 1.0F)
				this.particleRed += f;
			if (this.particleGreen + f >= 0.0F && this.particleGreen + f <= 1.0F)
				this.particleGreen += f;
			if (this.particleBlue + f >= 0.0F && this.particleBlue + f <= 1.0F)
				this.particleBlue += f;
			this.particleScale *= scale;
			this.motionX *= 0.02D;
			this.motionY *= 0.02D;
			this.motionZ *= 0.02D;
			this.motionX += speedX;
			this.motionY += speedY;
			this.motionZ += speedZ;
			if (maxAge > 0) {
				this.particleMaxAge = maxAge;
			} else {
				this.particleMaxAge = (int) (4.0D / (this.rand.nextDouble() * 0.8D + 0.2D));
				this.particleMaxAge = (int) (this.particleMaxAge * scale);
			}
			if (brightness != 0) {
				this.particleBrightness = brightness;
			}
		}

		protected abstract ResourceLocation getTexture();

		@Override
		public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ,
		 float rotationYZ, float rotationXY, float rotationXZ) {
			this.textureManager.bindTexture(this.getTexture());
			float f1 = (float)this.particleAge + partialTicks;
			float f2 = f1 / (float)this.particleMaxAge;
			float f3 = this.particleScale * (f2 * 0.6f + 0.7f);
			float f4 = this.particleAlpha * (1.0F - (f2 - 0.1F) * (f2 - 0.1F));
			if (f2 <= 0.1F) {
				f4 = f2 / 0.1F;
			}
			float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
			float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
			float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
	        int i = this.getBrightnessForRender(partialTicks);
	        int j = i >> 16 & 65535;
	        int k = i & 65535;
			GlStateManager.pushMatrix();
			GlStateManager.translate(f5, f6, f7);
			GlStateManager.rotate(-this.rotateY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(this.rotateX, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(this.rotateZ - 30.0F * f1, 0.0F, 0.0F, 1.0F);
	        GlStateManager.enableBlend();
	        GlStateManager.depthMask(false);
			GlStateManager.alphaFunc(0x204, 0.001f);
	        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			buffer.pos(-0.5D * f3, -0.5D * f3, 0.0D).tex(0.0D, 1.0D).color(this.particleRed, this.particleGreen, this.particleBlue, f4).lightmap(j, k).endVertex();
			buffer.pos(0.5D * f3, -0.5D * f3, 0.0D).tex(1.0D, 1.0D).color(this.particleRed, this.particleGreen, this.particleBlue, f4).lightmap(j, k).endVertex();
			buffer.pos(0.5D * f3, 0.5D * f3, 0.0D).tex(1.0D, 0.0D).color(this.particleRed, this.particleGreen, this.particleBlue, f4).lightmap(j, k).endVertex();
			buffer.pos(-0.5D * f3, 0.5D * f3, 0.0D).tex(0.0D, 0.0D).color(this.particleRed, this.particleGreen, this.particleBlue, f4).lightmap(j, k).endVertex();
			Tessellator.getInstance().draw();
			GlStateManager.alphaFunc(0x204, 0.1f);
			GlStateManager.depthMask(true);
			GlStateManager.popMatrix();
		}

	    @Override
	    public void move(double x, double y, double z) {
	        this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
	        this.resetPositionToBB();
	    }

		@Override
		public void onUpdate() {
	        this.prevPosX = this.posX;
	        this.prevPosY = this.posY;
	        this.prevPosZ = this.posZ;
	        if (this.particleAge++ >= this.particleMaxAge) {
	            this.setExpired();
	        }
	        this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.98D;
			this.motionY *= 0.98D;
			this.motionZ *= 0.98D;
		}

		@Override
		public int getBrightnessForRender(float p_189214_1_) {
			return this.particleBrightness != 0 ? ((this.particleBrightness << 16) | this.particleBrightness) 
			  : super.getBrightnessForRender(p_189214_1_);
		}

		@Override
	    public int getFXLayer() {
	        return 3;
	    }
	}

	@SideOnly(Side.CLIENT)
	public static class SealFormula extends TextureAsParticle {
		private static final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/seal_black_512.png");
		private final int growTime;

		protected SealFormula(TextureManager textureManagerIn, World worldIn, double x, double y, double z, 
		 double speedX, double speedY, double speedZ, float size, float yRotation, int maxAge) {
			super(textureManagerIn, worldIn, x, y, z, 0.0D, 0.0D, 0.0D, -1, size, maxAge, 0);
			this.motionX = 0.0d;
			this.motionY = 0.0d;
			this.motionZ = 0.0d;
			this.rotateY = yRotation;
			this.particleScale = size;
			this.growTime = Math.min(this.particleMaxAge, 20);
		}

		@Override
		protected ResourceLocation getTexture() {
			return TEXTURE;
		}

		@Override
		public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ,
		 float rotationYZ, float rotationXY, float rotationXZ) {
			this.textureManager.bindTexture(TEXTURE);
			float f0 = partialTicks + this.particleAge;
			float f1 = 0.5F;
			float f2 = 0.0F;
			float f4 = 1.0F;
			if (f0 >= 0.8F * this.particleMaxAge) {
				f4 = Math.max(1.0F - (f0 - 0.8F * this.particleMaxAge) / (0.2F * this.particleMaxAge), 0.0F);
			}
			float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
			float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
			float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
			float f8 = Math.min(f0 / this.growTime, 1.0F);
			float f3 = this.particleScale * f8;
			GlStateManager.pushMatrix();
			GlStateManager.translate(f5, f6, f7);
			GlStateManager.rotate(-this.rotateY, 0.0F, 1.0F, 0.0F);
			buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
			buffer.pos(-f1 * f3, -f2 * f3, -0.5F * f3).tex(0.5d * (1.0d - f8), 0.5d * (1.0d + f8)).color(1.0F, 1.0F, 1.0F, f4).normal(0.0F, 1.0F, 0.0F).endVertex();
			buffer.pos(-f1 * f3, -f2 * f3, 0.5F * f3).tex(0.5d * (1.0d - f8), 0.5d * (1.0d - f8)).color(1.0F, 1.0F, 1.0F, f4).normal(0.0F, 1.0F, 0.0F).endVertex();
			buffer.pos(f1 * f3, f2 * f3, 0.5F * f3).tex(0.5d * (1.0d + f8), 0.5d * (1.0d - f8)).color(1.0F, 1.0F, 1.0F, f4).normal(0.0F, 1.0F, 0.0F).endVertex();
			buffer.pos(f1 * f3, f2 * f3, -0.5F * f3).tex(0.5d * (1.0d + f8), 0.5d * (1.0d + f8)).color(1.0F, 1.0F, 1.0F, f4).normal(0.0F, 1.0F, 0.0F).endVertex();
			Tessellator.getInstance().draw();
			GlStateManager.popMatrix();
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double x, double y, double z, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				int arg2 = parameters.length > 2 ? parameters[2] : 20;
				float arg1 = parameters.length > 1 ? (float)parameters[1] / 10f : 0.0f;
				float arg0 = parameters.length > 0 ? (float)parameters[0] / 10f : 1.0f;
				return new SealFormula(Minecraft.getMinecraft().getTextureManager(), worldIn, x, y, z, 
				 xSpeedIn, ySpeedIn, zSpeedIn, arg0, arg1, arg2);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Whirlpool extends TextureAsParticle {
		private static final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/swirl_white_2.png");

		protected Whirlpool(TextureManager textureManagerIn, World worldIn, double x, double y, double z, 
		 double speedX, double speedY, double speedZ, int color, float scale, int maxAge, int brightness) {
		 	super(textureManagerIn, worldIn, x, y, z, speedX, speedY, speedZ, color, scale, maxAge, brightness);
			this.rotateX = ProcedureUtils.getPitchFromVec(this.motionX, this.motionY, this.motionZ);
			this.rotateY = ProcedureUtils.getYawFromVec(this.motionX, this.motionZ);
			this.rotateZ = this.rand.nextFloat() * 360.0F;
		}

		@Override
		protected ResourceLocation getTexture() {
			return TEXTURE;
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double x, double y, double z, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				int arg3 = parameters.length > 3 ? parameters[3] : 0;
				int arg2 = parameters.length > 2 ? parameters[2] : 0;
				float arg1 = parameters.length > 1 ? (float)parameters[1] / 10f : 1.0f;
				int arg0 = parameters.length > 0 ? parameters[0] : -1;
				return new Whirlpool(Minecraft.getMinecraft().getTextureManager(), worldIn, x, y, z, 
				 xSpeedIn, ySpeedIn, zSpeedIn, arg0, arg1, arg2, arg3);
			}
		}
	}

	public static class SonicBoom extends TextureAsParticle {
		private static final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/sonicboom.png");

		protected SonicBoom(TextureManager textureManagerIn, World worldIn, double x, double y, double z, 
		 double speedX, double speedY, double speedZ, int color, float scale, int maxAge, int brightness) {
			super(textureManagerIn, worldIn, x, y, z, speedX, speedY, speedZ, color, scale, maxAge, brightness);
			this.rotateX = ProcedureUtils.getPitchFromVec(this.motionX, this.motionY, this.motionZ);
			this.rotateY = ProcedureUtils.getYawFromVec(this.motionX, this.motionZ);
			this.rotateZ = this.rand.nextFloat() * 360.0F;
		}

		@Override
		protected ResourceLocation getTexture() {
			return TEXTURE;
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double x, double y, double z, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				int arg3 = parameters.length > 3 ? parameters[3] : 0;
				int arg2 = parameters.length > 2 ? parameters[2] : 0;
				float arg1 = parameters.length > 1 ? (float)parameters[1] / 10f : 1.0f;
				int arg0 = parameters.length > 0 ? parameters[0] : -1;
				return new SonicBoom(Minecraft.getMinecraft().getTextureManager(), worldIn, x, y, z, 
				 xSpeedIn, ySpeedIn, zSpeedIn, arg0, arg1, arg2, arg3);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class BlockDust extends ParticleBlockDust {
	    protected BlockDust(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
	     double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state, float size) {
	        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);
	        this.particleScale *= size;
	    }
	
	    @SideOnly(Side.CLIENT)
	    public static class Factory implements IParticleFactory {
	        @Nullable
	        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
	         double xSpeedIn, double ySpeedIn, double zSpeedIn, int... params) {
	            IBlockState iblockstate = Block.getStateById(params[0]);
	            float arg1 = params.length > 1 ? (float)params[1] / 10f : 1f;
	            return iblockstate.getRenderType() == EnumBlockRenderType.INVISIBLE ? null
	             : (new BlockDust(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, iblockstate, arg1)).init();
	        }
	    }
	}

	@SideOnly(Side.CLIENT)
	public static class Sand extends Smoke {
		protected Sand(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, 
		 double motionX, double motionY, double motionZ, int color, float scale, int maxAge, double floatSpeed) {
			super(worldIn, xCoordIn, yCoordIn, zCoordIn, motionX, motionY, motionZ, color, scale, maxAge, 0, -1, floatSpeed);
			this.particleTextureIndexY = 0;
			this.particleScale = scale;
			this.motionX = motionX;
			this.motionY = motionY;
			this.motionZ = motionZ;
		}

		@Override
		public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ,
				float rotationXY, float rotationXZ) {
	     	this.textureManager.bindTexture(Smoke.TEXTURE);
	        float f0 = (float)this.particleTextureIndexX / 8.0F;
	        float f1 = f0 + 0.124875F;
	        float f2 = (float)this.particleTextureIndexY / 8.0F;
	        float f3 = f2 + 0.124875F;
	        float f4 = 0.1F * this.particleScale;
	        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
	        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
	        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
	        float f8 = this.particleAlpha;
	        int i = this.getBrightnessForRender(partialTicks);
	        int j = i >> 16 & 65535;
	        int k = i & 65535;
	        Vec3d[] avec3d = new Vec3d[] {new Vec3d((double)(-rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(-rotationYZ * f4 - rotationXZ * f4)), new Vec3d((double)(-rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(-rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double)(rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(rotationYZ * f4 - rotationXZ * f4))};
	        buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, f8).lightmap(j, k).endVertex();
	        buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, f8).lightmap(j, k).endVertex();
	        buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f0, (double)f2).color(this.particleRed, this.particleGreen, this.particleBlue, f8).lightmap(j, k).endVertex();
	        buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f0, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, f8).lightmap(j, k).endVertex();
		}

		@Override
		public void onUpdate() {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (this.particleMaxAge == 0 || this.particleAge++ >= this.particleMaxAge) {
				this.setExpired();
				return;
			}
			double d = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
			this.particleTextureIndexX = (this.particleTextureIndexX + (d > 0.01d ? 1 : 0)) % 8;
			this.motionY += this.floatMotionY;
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.96D;
			this.motionY *= 0.96D;
			this.motionZ *= 0.96D;
			if (this.onGround) {
				this.motionX *= 0.7D;
				this.motionZ *= 0.7D;
			}
		}

		@Override
		public boolean shouldDisableDepth() {
			return false;
		}

		@SideOnly(Side.CLIENT)
		public static class Factory implements IParticleFactory {
			public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
					double ySpeedIn, double zSpeedIn, int... parameters) {
				double arg3 = (parameters.length > 3) ? (double)parameters[3] / 1000.0D : -0.004D;
				int arg2 = (parameters.length > 2) ? parameters[2] : 0;
				float arg1 = (parameters.length > 1) ? ((float)parameters[1] / 10.0F) : 1.0F;
				int arg0 = (parameters.length > 0) ? parameters[0] : -1;
				return new Sand(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, arg0, arg1, arg2, arg3);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class WaterSplash extends ParticleRain {
	    protected WaterSplash(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float scale) {
	        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
	        this.setParticleTextureIndex(19 + this.rand.nextInt(3));
	        this.particleScale *= scale;
            this.motionX = xSpeedIn;
            this.motionY = ySpeedIn;
            this.motionZ = zSpeedIn;
	    }
	
	    @SideOnly(Side.CLIENT)
	    public static class Factory implements IParticleFactory {
	        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
	        		double xSpeedIn, double ySpeedIn, double zSpeedIn, int... parameters) {
	        	float arg0 = (parameters.length > 0) ? ((float)parameters[0] / 10.0F) : 1.0F;
	            return new WaterSplash(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, arg0);
	        }
	    }
	}

	public enum Types {
		SMOKE("smoke_colored", 54678400, 6), 
		SUSPENDED("suspended_colored", 54678401, 3), 
		FALLING_DUST("falling_dust", 54678402, 1), 
		FLAME("flame_colored", 54678403, 2),
		MOB_APPEARANCE("mob_appearance", 54678404, 1),
		BURNING_ASH("burning_ash", 54678405, 1),
		HOMING_ORB("homing_orb", 54678406, 2),
		EXPANDING_SPHERE("expanding_sphere", 54678407, 3),
		PORTAL_SPIRAL("portal_spiral", 54678408, 3),
		SEAL_FORMULA("seal_formula", 54678409, 3),
		ACID_SPIT("acid_spit", 54678410, 2),
		WHIRLPOOL("whirlpool", 54678411, 4),
		BLOCK_DUST("block_dust", 54678412, 2),
		SONIC_BOOM("sonic_boom", 54678413, 4),
		SAND("sand_colored", 54678414, 4),
		WATER_SPLASH("water_splash", 54678415, 1);
		
		private final String particleName;
		private final int particleID;
		private final int argumentCount;
		private static final Map<Integer, Types> PARTICLES = Maps.newHashMap();
		
		static {
			for (Types type : values())
				PARTICLES.put(Integer.valueOf(type.getID()), type);
		}
		
		Types(String name, int id, int args) {
			this.particleName = name;
			this.particleID = id;
			this.argumentCount = args;
		}

		public String getName() {
			return this.particleName;
		}

		public int getID() {
			return this.particleID;
		}

		public int getArgsCount() {
			return this.argumentCount;
		}

		public static Types getTypeFromId(int id) {
			return PARTICLES.get(Integer.valueOf(id));
		}
	}
}
