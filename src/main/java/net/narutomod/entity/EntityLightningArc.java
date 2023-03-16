
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.WorldServer;

import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.potion.PotionParalysis;
import net.narutomod.procedure.ProcedureSync;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityLightningArc extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 135;
	public static final int ENTITYID_RANGED = 136;
	private static final Random rng = new Random();

	public EntityLightningArc(ElementsNarutomodMod instance) {
		super(instance, 380);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(Base.class)
		  .id(new ResourceLocation("narutomod", "lightning_arc"), ENTITYID).name("lightning_arc").tracker(96, 3, true).build());
		//elements.entities.add(() -> EntityEntryBuilder.create().entity(ClientBase.class)
		//  .id(new ResourceLocation("narutomod", "lightning_c"), ENTITYID_RANGED).name("lightning_c").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(Base.class, renderManager -> {
			return new RenderCustom(renderManager);
		});
	}

	public static boolean onStruck(Entity entity, DamageSource source, float damage) {
		return onStruck(entity, source, damage, 100, true);
	}

	public static boolean onStruck(Entity entity, DamageSource source, float damage, int paralysisTicks) {
		boolean retval = entity.attackEntityFrom(source, damage);
		boolean flag = entity.isBurning();
		entity.onStruckByLightning(new EntityLightningBolt(entity.world, 0, 0, 0, true));
		if (!flag) {
			entity.extinguish();
		}
		if (entity instanceof EntityLivingBase && paralysisTicks > 0) {
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(PotionParalysis.potion, paralysisTicks, 2 + (int)(damage * 0.1f), false, false));
		}
		return retval;
	}

	public static boolean onStruck(Entity entity, DamageSource source, float damage, int ticks, boolean spreadInWater) {
		if (spreadInWater && entity.isInWater()) {
			for (EntityLivingBase entity1 : entity.world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().grow(10d))) {
				float f = (float)entity1.getDistance(entity);
				if (entity1.isInWater() && f <= 10f) {
					onStruck(entity1, source, damage * (1f - f * 0.1f), ticks);
				}
			}
		}
		return onStruck(entity, source, damage, ticks);
	}

	public static void spawnAsParticle(World world, double x, double y, double z, int... param) {
		spawnAsParticle(world, x, y, z, 0.3d, 0d, 0.15d, 0d, param);
	}

	public static void spawnAsParticle(World world, double x, double y, double z, double length,
	 double xSpeed, double ySpeed, double zSpeed, int... param) {
		if (!world.isRemote) {
			world.spawnEntity(new Base(world, new Vec3d(x, y, z), length, xSpeed, ySpeed, zSpeed,
			 param.length>0?param[0]:0xc00000ff, param.length>1?param[1]:0, 0f, 0.1f));
		} else {
			ProcedureSync.CPacketSpawnLightning.sendToServer(x, y, z, length, xSpeed, ySpeed, zSpeed, param);
		}
	}

	public static class Base extends Entity {
		private static final DataParameter<Float> END_X = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> END_Y = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> END_Z = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Float> THICKNESS = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		private static final DataParameter<Integer> MAX_RECURSIVE_DEPTH = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private Vec3d ogEndVec;
		private float inaccuracy;
		private int livingTime;
		private DamageSource damageSource;
		private EntityLivingBase excludeEntity;
		private float damageAmount;
		private boolean resetHurtResistantTime;
		
		public Base(World worldIn) {
			super(worldIn);
			this.setSize(0.1f, 0.1f);
			//this.setRenderDistanceWeight(10d);
			this.isImmuneToFire = true;
			this.ignoreFrustumCheck = true;
			this.livingTime = this.rand.nextInt(3) + 1;
		}

		public Base(World worldIn, Vec3d centerVec, double length, double xMotion, double yMotion, double zMotion) {
			this(worldIn, centerVec, length, xMotion, yMotion, zMotion, 0xc00000ff, 0, 0f, 0.1f);
		}

		public Base(World worldIn, Vec3d centerVec, double length, double xMotion, double yMotion, double zMotion, float inaccuracyIn) {
			this(worldIn, centerVec, length, xMotion, yMotion, zMotion, 0xc00000ff, 0, 0f, inaccuracyIn);
		}

		public Base(World worldIn, Vec3d centerVec, double length, double xMotion, double yMotion, double zMotion, int color) {
			this(worldIn, centerVec, length, xMotion, yMotion, zMotion, color, 0, 0f, 0.1f);
		}

		public Base(World worldIn, Vec3d centerVec, double length, double xMotion, double yMotion, double zMotion, int color, int duration, float thickness, float inaccuracyIn) {
			this(worldIn, centerVec, centerVec.addVector((rng.nextDouble()-0.5d) * length * 2d, (rng.nextDouble()-0.5d) * length * 2d, 
			  (rng.nextDouble()-0.5d) * length * 2d), color, duration, inaccuracyIn, thickness);
			this.motionX = xMotion;
			this.motionY = yMotion;
			this.motionZ = zMotion;
		}

		public Base(World worldIn, Vec3d fromVec, Vec3d toVec) {
			this(worldIn, fromVec, toVec, -1, 0, 0.5f);
		}

		public Base(World worldIn, Vec3d fromVec, Vec3d toVec, int colorIn, int duration, float inaccuracyIn) {
			this(worldIn, fromVec, toVec, colorIn, duration, inaccuracyIn, 0f, 4);
		}

		public Base(World worldIn, Vec3d fromVec, Vec3d toVec, int colorIn, int duration, float inaccuracyIn, float thickness) {
			this(worldIn, fromVec, toVec, colorIn, duration, inaccuracyIn, thickness, 4);
		}

		public Base(World worldIn, Vec3d fromVec, Vec3d toVec, int colorIn, int duration, float inaccuracyIn, float thickness, int sections) {
			this(worldIn);
			this.setPosition(fromVec.x, fromVec.y, fromVec.z);
			this.ogEndVec = toVec;
			this.setEndVec(toVec.addVector(this.rand.nextGaussian() * inaccuracyIn, this.rand.nextGaussian() * inaccuracyIn, 
			  this.rand.nextGaussian() * inaccuracyIn));
			this.setColor(colorIn);
			if (duration > 0)
				this.livingTime = duration;
			this.inaccuracy = inaccuracyIn;
			if (thickness != 0f) {
				this.setThickness(thickness);
			}
			this.setMaxRecursiveDepth(sections);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(END_X, Float.valueOf(0f));
			this.getDataManager().register(END_Y, Float.valueOf(0f));
			this.getDataManager().register(END_Z, Float.valueOf(0f));
			this.getDataManager().register(COLOR, Integer.valueOf(-1));
			this.getDataManager().register(THICKNESS, Float.valueOf(0f));
			this.getDataManager().register(MAX_RECURSIVE_DEPTH, Integer.valueOf(4));
		}

		public Vec3d getEndVec() {
			return new Vec3d( ((Float) this.getDataManager().get(END_X)).floatValue(), 
			                  ((Float) this.getDataManager().get(END_Y)).floatValue(), 
			                  ((Float) this.getDataManager().get(END_Z)).floatValue() );
		}

		private void setEndVec(Vec3d vec) {
			this.getDataManager().set(END_X, Float.valueOf((float)vec.x));
			this.getDataManager().set(END_Y, Float.valueOf((float)vec.y));
			this.getDataManager().set(END_Z, Float.valueOf((float)vec.z));
		}

		public int getColor() {
			return ((Integer) this.getDataManager().get(COLOR)).intValue();
		}

		protected void setColor(int color) {
			this.getDataManager().set(COLOR, Integer.valueOf(color));
		}

		public float getThickness() {
			return ((Float)this.getDataManager().get(THICKNESS)).floatValue();
		}

		private void setThickness(float f) {
			this.getDataManager().set(THICKNESS, Float.valueOf(f));
		}

		private int getMaxRecursiveDepth() {
			return ((Integer)this.getDataManager().get(MAX_RECURSIVE_DEPTH)).intValue();
		}

		private void setMaxRecursiveDepth(int depth) {
			this.getDataManager().set(MAX_RECURSIVE_DEPTH, Integer.valueOf(depth));
		}

		public void setDamage(DamageSource source, float amount, @Nullable EntityLivingBase entity) {
			this.setDamage(source, amount, false, entity);
		}

		public void setDamage(DamageSource source, float amount, boolean resetHurtTime, @Nullable EntityLivingBase entity) {
			this.damageSource = source;
			this.excludeEntity = entity;
			this.damageAmount = amount;
			this.resetHurtResistantTime = resetHurtTime;
		}

		@Override
		public void onUpdate() {
			if (this.inaccuracy > 0.0f) {
				this.setEndVec(this.ogEndVec.addVector((this.rand.nextFloat()-0.5d) * this.inaccuracy * 2,
				  this.rand.nextFloat() * this.inaccuracy * 2, this.rand.nextFloat() * this.inaccuracy * 2));
			}
			//super.onUpdate();
			if (this.motionX != 0d || this.motionY != 0d || this.motionZ != 0d) {
				this.setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			}
			if (this.damageAmount > 0f) {
				for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this.excludeEntity, this.getEntityBoundingBox()
				  .expand(this.ogEndVec.x - this.posX, this.ogEndVec.y - this.posY, this.ogEndVec.z - this.posZ).grow(1))) {
					if (entity.getEntityBoundingBox().calculateIntercept(this.getPositionVector(), this.ogEndVec) != null) {
						if (this.resetHurtResistantTime) {
							entity.hurtResistantTime = 10;
						}
						onStruck(entity, this.damageSource, this.damageAmount);
					}
				}
			}
			if (!this.world.isRemote && --this.livingTime <= 0) {
				this.setDead();
			}
		}

		@Override
		public boolean isImmuneToExplosions() {
			return true;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<Base> {
		private final double segmentOffset = 0.1d;
		private int maxRecursiveDepth;

	    public RenderCustom(RenderManager renderManagerIn) {
	        super(renderManagerIn);
	    }
	
	    @Override
	    public boolean shouldRender(Base livingEntity, ICamera camera, double camX, double camY, double camZ) {
	        return true;
	    }

	    @Override
	    public void doRender(Base entity, double x, double y, double z, float entityYaw, float partialTicks) {
	    	this.maxRecursiveDepth = entity.getMaxRecursiveDepth();
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			Vec3d vec3d = entity.getEndVec().subtract(entity.posX, entity.posY, entity.posZ);
			float yaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180d / Math.PI));
			float pitch = (float) (-MathHelper.atan2(vec3d.y, MathHelper.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z)) * (180d / Math.PI));
			GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
			double d = (double)entity.getThickness();
			d = d == 0d ? Math.max(vec3d.lengthVector() * 0.004d, 0.0005d) : d;
	    	this.renderSection(new Vec3d(0d, 0d, 0d), new Vec3d(0d, 0d, vec3d.lengthVector()), d, entity.getColor(), 0, false);
	    	GlStateManager.popMatrix();
	    }

	    private void renderSection(Vec3d fromVec, Vec3d toVec, double thickness, int color, int recursiveDepth, boolean isBranch) {
	    	if (recursiveDepth == this.maxRecursiveDepth) {
		        Tessellator tessellator = Tessellator.getInstance();
		        BufferBuilder bufferbuilder = tessellator.getBuffer();
		        GlStateManager.disableTexture2D();
		        GlStateManager.enableBlend();
		        GlStateManager.disableCull();
		        GlStateManager.depthMask(false);
		        GlStateManager.disableLighting();
		        //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, isBranch?160F:240F, isBranch?160F:240F);
				bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
                //int a = (color & 0xFF000000) >> 24;
                int r = (color & 0x00FF0000) >> 16;
                int g = (color & 0x0000FF00) >> 8;
                int b = color & 0x000000FF;
                for (int i = 1; i <= 3; i++) {
	                if (!isBranch || i >= 2) {
	                	double w = thickness * i;
	                	int a = 0xF0;
		                int r1 = r;
		                int g1 = g;
		                int b1 = b;
	                	if (i == 1) {
	                		r1 = 255;
	                		g1 = 255;
	                		b1 = 255;
	                		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
	                	} else {
	                		a = i == 3 ? 0x10 : 0x20;
		        			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	                	}
		                bufferbuilder.pos(fromVec.x - w, fromVec.y - w, fromVec.z).color(r1, g1, b1, a).endVertex();
		                bufferbuilder.pos(toVec.x - w, toVec.y - w, toVec.z).color(r1, g1, b1, a).endVertex();
		                bufferbuilder.pos(fromVec.x - w, fromVec.y + w, fromVec.z).color(r1, g1, b1, a).endVertex();
		                bufferbuilder.pos(toVec.x - w, toVec.y + w, toVec.z).color(r1, g1, b1, a).endVertex();
		                bufferbuilder.pos(fromVec.x + w, fromVec.y + w, fromVec.z).color(r1, g1, b1, a).endVertex();
		                bufferbuilder.pos(toVec.x + w, toVec.y + w, toVec.z).color(r1, g1, b1, a).endVertex();
		                bufferbuilder.pos(fromVec.x + w, fromVec.y - w, fromVec.z).color(r1, g1, b1, a).endVertex();
		                bufferbuilder.pos(toVec.x + w, toVec.y - w, toVec.z).color(r1, g1, b1, a).endVertex();
		                bufferbuilder.pos(fromVec.x - w, fromVec.y - w, fromVec.z).color(r1, g1, b1, a).endVertex();
		                bufferbuilder.pos(toVec.x - w, toVec.y - w, toVec.z).color(r1, g1, b1, a).endVertex();
	                }
                }
                tessellator.draw();
	        	GlStateManager.enableLighting();
		        GlStateManager.depthMask(true);
		        GlStateManager.enableCull();
		        GlStateManager.disableBlend();
	        	GlStateManager.enableTexture2D();
	    	} else {
	    		Random rand = new Random();
	    		Vec3d vec3d = toVec.subtract(fromVec).scale(0.5d);
	    		double offset = vec3d.lengthVector() * this.segmentOffset;
	    		vec3d = vec3d.addVector(rand.nextGaussian() * offset, rand.nextGaussian() * offset, rand.nextGaussian() * offset);
	    		this.renderSection(fromVec, fromVec.add(vec3d), thickness, color, recursiveDepth + 1, isBranch);
	    		this.renderSection(fromVec.add(vec3d), toVec, thickness, color, recursiveDepth + 1, isBranch);
	    		if (rand.nextInt(5) == 0) {
	    			this.renderSection(fromVec.add(vec3d), fromVec.add(vec3d.scale(1.8d)), thickness * 0.6d, color, recursiveDepth + 1, true);
	    		}
	    	}
	    }
	
		@Override
	    protected ResourceLocation getEntityTexture(Base entity) {
	        return null;
	    }
	}
}
