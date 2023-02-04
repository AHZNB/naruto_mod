
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import javax.vecmath.Vector4f;
import java.util.List;
import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class EntityParticle extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 199;
	public static final int ENTITYID_RANGED = 200;

	public EntityParticle(ElementsNarutomodMod instance) {
		super(instance, 516);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(Base.class).id(new ResourceLocation("narutomod", "particle"), ENTITYID)
				.name("particle").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(Base.class, renderManager -> new CustomRender(renderManager));
	}

	public static class Base extends Entity {
		private static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> MAXAGE = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Float> RED = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> GREEN = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> BLUE = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> ALPHA = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private int idleTime;
		private int texU;
		private int texV;
		private int deathTicks;

		public Base(World worldIn) {
			super(worldIn);
			this.setSize(0.2f, 0.2f);
			this.isImmuneToFire = true;
		}

		public Base(World worldIn, double x, double y, double z, double mX, double mY, double mZ, int color, float scale, int maxAgeIn) {
			this(worldIn);
			this.setPosition(x, y, z);
			this.motionX = mX;
			this.motionY = mY;
			this.motionZ = mZ;
			float f = (this.rand.nextFloat() - 0.5F) * 0.1F;
			float a = (float)(color >> 24 & 0xFF) / 255.0F;
			float r = MathHelper.clamp((float)(color >> 16 & 0xFF) / 255.0F + f, 0.0f, 1.0f);
			float g = MathHelper.clamp((float)(color >> 8 & 0xFF) / 255.0F + f, 0.0f, 1.0f);
			float b = MathHelper.clamp((float)(color & 0xFF) / 255.0F + f, 0.0f, 1.0f);
			this.setColor(r, g, b, a);
			this.setScale(scale);
			if (maxAgeIn > 0) {
				this.setMaxAge((int)((this.rand.nextFloat() * 0.4f + 0.8f) * maxAgeIn));
			}
		}

		@Override
		protected void entityInit() {
			this.dataManager.register(AGE, Integer.valueOf(0));
			this.dataManager.register(MAXAGE, Integer.valueOf((int)(20.0D / (this.rand.nextDouble() * 0.8D + 0.2D))));
			this.dataManager.register(RED, Float.valueOf(1f));
			this.dataManager.register(GREEN, Float.valueOf(1f));
			this.dataManager.register(BLUE, Float.valueOf(1f));
			this.dataManager.register(ALPHA, Float.valueOf(1f));
			this.dataManager.register(SCALE, Float.valueOf(1f));
		}

		public int getAge() {
			return ((Integer) this.dataManager.get(AGE)).intValue();
		}

		protected void setAge(int age) {
			this.dataManager.set(AGE, Integer.valueOf(age));
		}

		public int getMaxAge() {
			return ((Integer) this.dataManager.get(MAXAGE)).intValue();
		}

		protected void setMaxAge(int age) {
			this.dataManager.set(MAXAGE, Integer.valueOf(age));
		}

		public float getScale() {
			return ((Float) this.dataManager.get(SCALE)).floatValue();
		}

		protected void setScale(float f) {
			this.dataManager.set(SCALE, Float.valueOf(f));
		}

		public Vector4f getColor() {
			return new Vector4f(((Float)this.dataManager.get(RED)).floatValue(), ((Float)this.dataManager.get(GREEN)).floatValue(),
			                    ((Float)this.dataManager.get(BLUE)).floatValue(), ((Float)this.dataManager.get(ALPHA)).floatValue());
		}

		protected void setColor(float r, float g, float b, float a) {
			this.dataManager.set(RED, Float.valueOf(r));
			this.dataManager.set(GREEN, Float.valueOf(g));
			this.dataManager.set(BLUE, Float.valueOf(b));
			this.dataManager.set(ALPHA, Float.valueOf(a));
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (SCALE.equals(key) && this.world.isRemote) {
				float scale = this.getScale();
				this.setSize(0.2f * scale, 0.2f * scale);
			}
		}

		@Override
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}

    	public void move(double x, double y, double z) {
	        double d0 = y;
	        double origX = x;
	        double origZ = z;
	
	        if (!this.noClip) {
	            List<AxisAlignedBB> list = this.world.getCollisionBoxes(null, this.getEntityBoundingBox().expand(x, y, z));
	            for (AxisAlignedBB axisalignedbb : list) {
	                y = axisalignedbb.calculateYOffset(this.getEntityBoundingBox(), y);
	            }
	            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
	            for (AxisAlignedBB axisalignedbb1 : list) {
	                x = axisalignedbb1.calculateXOffset(this.getEntityBoundingBox(), x);
	            }
	            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
	            for (AxisAlignedBB axisalignedbb2 : list) {
	                z = axisalignedbb2.calculateZOffset(this.getEntityBoundingBox(), z);
	            }
	            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));
	        } else {
	            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
	        }
	        this.resetPositionToBB();
	        this.onGround = d0 != y && d0 < 0.0D;
	        this.collidedHorizontally = origX != x || origZ != z;
	        if (origX != x) {
	            this.motionX = 0.0D;
	        }
	        if (origZ != z) {
	            this.motionZ = 0.0D;
	        }
	    }

		@Override
		public void onUpdate() {
			//if (this.world.isRemote) {
			//	float scale = this.getScale();
			//	if (scale != 1.0f) {
			//		this.setSize(0.2f * scale, 0.2f * scale);
			//	}
			//}
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.8D;
			this.motionY *= 0.8D;
			this.motionZ *= 0.8D;
			double d = this.getVelocity();
			this.idleTime = d < 0.001d ? this.idleTime + 1 : 0;
			int age = this.getAge() + 1;
			this.setAge(age);
			this.setParticleTextureOffset(this.texU + (d > 0.01d ? 1 : 0) % 8);
			if (this.deathTicks > 0) {
				if (this.deathTicks >= 20 && !this.world.isRemote) {
					this.setDead();
				}
				this.motionY -= 0.05d;
				++this.deathTicks;
			} else if (age > this.getMaxAge() || this.idleTime > 1000) {
				this.deathTicks = 1;
			}
		}

		public void setParticleTextureOffset(int offset) {
			this.texU = offset;
		}

		public double getVelocity() {
			return ProcedureUtils.getVelocity(this);
		}

		public Random getRNG() {
			return this.rand;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			this.setAge(compound.getInteger("age"));
			this.setMaxAge(compound.getInteger("maxAge"));
			this.setColor(compound.getFloat("red"), compound.getFloat("green"), compound.getFloat("blue"), compound.getFloat("alpha"));
			this.setScale(compound.getFloat("scale"));
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setInteger("age", this.getAge());
			compound.setInteger("maxAge", this.getMaxAge());
			Vector4f vec = this.getColor();
			compound.setFloat("red", vec.x);
			compound.setFloat("green", vec.y);
			compound.setFloat("blue", vec.z);
			compound.setFloat("alpha", vec.w);
			compound.setFloat("scale", this.getScale());
		}
	}

	@SideOnly(Side.CLIENT)
	public class CustomRender extends Render<Base> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/particles.png");

		public CustomRender(RenderManager renderManager) {
			super(renderManager);
		}

		@Override
		public void doRender(Base entity, double x, double y, double z, float entityYaw, float partialTicks) {
			this.bindEntityTexture(entity);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			//GlStateManager.enableRescaleNormal();
			float scale = entity.getScale();
			GlStateManager.scale(scale, scale, scale);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1f : 1f) * -this.renderManager.playerViewX,
			 1.0F, 0.0F, 0.0F);
			double d = (double)entity.texU / 8d;
			double d1 = d + 0.125d;
			double d2 = entity.texV;
			double d3 = d2 + 0.125d;
			Vector4f vec = entity.getColor();
			if (vec.w < 1.0F) {
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			}
			GlStateManager.disableLighting();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int i = entity.getBrightnessForRender();
			int j = i >> 16 & 0xFFFF;
			int k = i & 0xFFFF;
			bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			bufferbuilder.pos(-0.1D, 0.0D, 0.0D).tex(d, d3).color(vec.x, vec.y, vec.z, vec.w).lightmap(j, k).endVertex();
			bufferbuilder.pos(0.1D, 0.0D, 0.0D).tex(d1, d3).color(vec.x, vec.y, vec.z, vec.w).lightmap(j, k).endVertex();
			bufferbuilder.pos(0.1D, 0.2D, 0.0D).tex(d1, d2).color(vec.x, vec.y, vec.z, vec.w).lightmap(j, k).endVertex();
			bufferbuilder.pos(-0.1D, 0.2D, 0.0D).tex(d, d2).color(vec.x, vec.y, vec.z, vec.w).lightmap(j, k).endVertex();
			tessellator.draw();
			GlStateManager.enableLighting();
			if (vec.w < 1.0F) {
				GlStateManager.disableBlend();
				GlStateManager.disableAlpha();
			}
			//GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(Base entity) {
			return TEXTURE;
		}
	}
}
