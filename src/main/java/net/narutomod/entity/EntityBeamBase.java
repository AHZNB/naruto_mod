package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityBeamBase extends ElementsNarutomodMod.ModElement {
	public EntityBeamBase(ElementsNarutomodMod instance) {
		super(instance, 283);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(Base.class, renderManager -> new Renderer(renderManager));
	}
	
	public static abstract class Base extends Entity {
		private static final DataParameter<Integer> SHOOTER_ID = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Float> BEAM_LENGTH = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		public int ticksAlive;
		public Entity shootingEntity;
		public RayTraceResult hitTrace;
		
		public Base(World a) {
			super(a);
			this.width = 0.125F;
			this.height = 0.125F;
			this.ticksAlive = 0;
			this.ignoreFrustumCheck = true;
			//this.setRenderDistanceWeight(10d);
		}

		public Base(World worldIn, double x, double y, double z) {
			this(worldIn);
			this.setLocationAndAngles(x, y, z, 0f, 0f);
			this.setAlwaysRenderNameTag(false);
		}

		public Base(EntityLivingBase shooter) {
			this(shooter.world, shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
			this.prevRotationPitch = this.rotationPitch = shooter.rotationPitch;
			this.prevRotationYaw = this.rotationYaw = shooter.rotationYaw;
			this.setShooter(shooter);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(SHOOTER_ID, Integer.valueOf(-1));
			this.getDataManager().register(BEAM_LENGTH, Float.valueOf(1));
		}

		private void setShooter(EntityLivingBase shooter) {
			this.getDataManager().set(SHOOTER_ID, Integer.valueOf(shooter.getEntityId()));
			this.shootingEntity = shooter;
		}

		protected EntityLivingBase getShooter() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(SHOOTER_ID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		public float getBeamLength() {
			return ((Float)this.getDataManager().get(BEAM_LENGTH)).floatValue();
		}

		protected void setBeamLength(float length) {
			this.getDataManager().set(BEAM_LENGTH, Float.valueOf(length));
		}

		public void shoot(double range) {
			if (this.shootingEntity != null) {
				this.hitTrace = ProcedureUtils.objectEntityLookingAt(this.shootingEntity, range);
				this.shoot(this.hitTrace.hitVec.x - this.posX, this.hitTrace.hitVec.y - this.posY, this.hitTrace.hitVec.z - this.posZ);
			}
		}

		public void shoot(double x, double y, double z) {
			if (this.hitTrace == null) {
				this.hitTrace = new RayTraceResult(new Vec3d(this.posX + x, this.posY + y, this.posZ + z),
				 EnumFacing.getFacingFromVector((float)x, (float)y, (float)z).getOpposite());
			}
			float f = MathHelper.sqrt(x * x + y * y + z * z);
			this.setBeamLength(f + 0.1f);
			x /= f;
			y /= f;
			z /= f;
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
			float f1 = MathHelper.sqrt(x * x + z * z);
			this.rotationYaw = (float) (MathHelper.atan2(x, z) * (180d / Math.PI));
			this.rotationPitch = (float) (MathHelper.atan2(y, f1) * (180d / Math.PI));
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
			//this.ticksAlive = 0;
		}

		protected void updatePosition() {
		}

		@Override
		public void onUpdate() {
			if (this.world.isRemote && this.prevRotationYaw == 0.0f && this.prevRotationPitch == 0.0f) {
				this.prevRotationYaw = this.rotationYaw;
				this.prevRotationPitch = this.rotationPitch;
			}
			this.updatePosition();
			this.ticksAlive++;
		}

		@Override
		public void onKillCommand() {
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			compound.setFloat("beamLength", this.getBeamLength());
			compound.setInteger("life", this.ticksAlive);
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			this.setBeamLength(compound.getFloat("beamLength"));
			this.ticksAlive = compound.getInteger("life");
		}
	}
	
	private static int texIncrement = 0;
	
	@SideOnly(Side.CLIENT)
	public static class Renderer<T extends Base> extends Render<T> {
		private static final ResourceLocation texture = new ResourceLocation("narutomod:textures/beam.png");
		
		public Renderer(RenderManager renderManager) {
			super(renderManager);
			this.shadowSize = 0.1F;
		}

		public Model getMainModel(T entity) {
			return new Model(entity.getBeamLength());
		}

		@Override
		public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
			return true;
		}
		
		@Override
		public void doRender(T bullet, double x, double y, double z, float yaw, float pt) {
			//if (yaw != bullet.rotationYaw)
			//	return;
			this.bindEntityTexture(bullet);
			GlStateManager.pushMatrix();
			GlStateManager.depthMask(true);
			GlStateManager.translate((float) x, (float) y, (float) z);
			GlStateManager.rotate(bullet.prevRotationYaw + MathHelper.wrapDegrees(bullet.rotationYaw - bullet.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(90.0F - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * pt, 1.0F, 0.0F, 0.0F);
			//GlStateManager.rotate(bullet.rotationYaw, 0.0F, 1.0F, 0.0F);
			//GlStateManager.rotate(90.0F - bullet.rotationPitch, 1.0F, 0.0F, 0.0F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f = --texIncrement;
			GlStateManager.translate(f * 0.01F, f * 0.02F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			//GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
			this.getMainModel(bullet).render(bullet, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			//GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(false);
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(T entity) {
			return texture;
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Model extends ModelBase {
		private final ModelRenderer bone;
		
		public Model() {
			this(1f);
		}

		public Model(float length) {
			this.textureWidth = 16;
			this.textureHeight = 16;
			this.bone = new ModelRenderer(this);
			this.bone.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -3.0F, 0.0F, -0.5F, 1, (int)(16f * length), 1, 0.0F, true));
			this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, 2.0F, 0.0F, -0.5F, 1, (int)(16f * length), 1, 0.0F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			this.bone.render(f5);
		}
	}
}
