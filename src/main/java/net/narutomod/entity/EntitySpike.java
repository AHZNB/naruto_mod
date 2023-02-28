
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySpike extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 220;
	public static final int ENTITYID_RANGED = 221;

	public EntitySpike(ElementsNarutomodMod instance) {
		super(instance, 533);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(Base.class)
		 .id(new ResourceLocation("narutomod", "spike"), ENTITYID).name("spike").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(Base.class, renderManager -> new Renderer(renderManager));
	}

	@Nullable
	public static Base spawnSpike(World worldIn, int color) {
		Base entity = new Base(worldIn, color);
		return worldIn.spawnEntity(entity) ? entity : null;
	}

	@Nullable
	public static Base spawnSpike(EntityLivingBase entityIn, int color) {
		Base entity = new Base(entityIn, color);
		return entityIn.world.spawnEntity(entity) ? entity : null;
	}

	public static class Base extends EntityScalableProjectile.Base {
		private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private Vec3d tipOffset = Vec3d.ZERO;

		public Base(World worldIn) {
			super(worldIn);
			this.setOGSize(0.5f, 1.82f);
			this.maxInGroundTime = 400;
			//this.setNoGravity(true);
		}

		public Base(EntityLivingBase userIn) {
			super(userIn);
			this.setOGSize(0.5f, 1.82f);
			this.maxInGroundTime = 400;
			//this.setNoGravity(false);
		}

		public Base(EntityLivingBase userIn, int color) {
			this(userIn);
			this.setColor(color);
		}

		public Base(World worldIn, int color) {
			this(worldIn);
			this.setColor(color);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(COLOR, Integer.valueOf(0xFFFFFFFF));
		}

		protected int getColor() {
			return ((Integer)this.dataManager.get(COLOR)).intValue();
		}

		protected void setColor(int color) {
			this.dataManager.set(COLOR, Integer.valueOf(color));
		}

		protected void setTipOffset(Vec3d vec) {
			this.tipOffset = vec;
		}

		@Override
		public void shoot(double x, double y, double z, float speed, float inaccuracy) {
			super.shoot(x, y, z, speed, inaccuracy);
			this.rotationPitch = MathHelper.wrapDegrees(this.rotationPitch + 90f);
			this.prevRotationPitch = this.rotationPitch;
			this.tipOffset = new Vec3d(0d, 1.82d, 0d);
		}

		@Override
		protected void checkOnGround() {
			Vec3d vec = this.getTransformedTip();
			BlockPos pos = new BlockPos(vec);
			if (!this.world.isAirBlock(pos)) {
				AxisAlignedBB aabb = this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos);
				if (aabb != net.minecraft.block.Block.NULL_AABB && aabb.offset(pos).contains(vec)) {
					this.onGround = true;
					this.setNoGravity(false);
				}
			}
		}

		private Vec3d getTransformedTip() {
			return this.tipOffset.scale(this.getEntityScale()).rotatePitch(-this.rotationPitch * 0.017453292F)
			 .rotateYaw(-this.rotationYaw * 0.017453292F).add(this.getPositionVector());
		}

		@Override
		protected RayTraceResult forwardsRaycast(boolean includeEntities, boolean ignoreExcludedEntity, @Nullable Entity excludedEntity) {
			Vec3d vec1 = this.getTransformedTip();
			Vec3d vec2 = vec1.addVector(this.motionX, this.motionY, this.motionZ);
			RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec1, vec2, false, true, false);
			if (includeEntities) {
				if (raytraceresult != null) {
					vec2 = raytraceresult.hitVec;
				}
				Entity entity = null;
				AxisAlignedBB bigAABB = this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D);
				double d0 = 0.0D;
				for (Entity entity1 : this.world.getEntitiesWithinAABBExcludingEntity(this, bigAABB)) {
					if (entity1.canBeCollidedWith() && (ignoreExcludedEntity || !entity1.equals(excludedEntity)) && !entity1.noClip) {
						RayTraceResult result = entity1.getEntityBoundingBox().calculateIntercept(vec1, vec2);
						if (result != null) {
							double d = vec1.distanceTo(result.hitVec);
							if (d < d0 || d0 == 0.0D) {
								entity = entity1;
								d0 = d;
							}
						}
					}
				}
				if (entity != null) {
					raytraceresult = new RayTraceResult(entity);
				}
			}
			return raytraceresult;
		}

		@Override
		public void renderParticles() {			
		}

		@Override
		protected void onImpact(RayTraceResult result) {	
		}
	}

	@SideOnly(Side.CLIENT)
	public static class Renderer<T extends Base> extends Render<T> {
		private static final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/spike.png");
		protected final ModelSpike model;

		public Renderer(RenderManager renderManagerIn) {
			super(renderManagerIn);
			this.model = new ModelSpike();
			this.shadowSize = 0.1f;
		}

		@Override
		public void doRender(T entity, double x, double y, double z, float entityYaw, float pt) {
			GlStateManager.pushMatrix();
			this.bindEntityTexture(entity);
			float scale = entity.getEntityScale();
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt - 180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.scale(scale, scale, scale);
			int color = entity.getColor();
			float alpha = (color >> 24 & 0xFF) / 255.0F;
			float red = (color >> 16 & 0xFF) / 255.0F;
			float green = (color >> 8 & 0xFF) / 255.0F;
			float blue = (color & 0xFF) / 255.0F;
			if (alpha < 1.0f) {
				GlStateManager.disableCull();
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableLighting();
			}
			//GlStateManager.color(0.616f, 0.882f, 1.0f, 0.5f);
			GlStateManager.color(red, green, blue, alpha);
			//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			if (alpha < 1.0f) {
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.disableAlpha();
				GlStateManager.enableCull();
			}
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(T entity) {
			return TEXTURE;
		}
	}

	// Made with Blockbench 3.8.3
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public static class ModelSpike extends ModelBase {
		private final ModelRenderer bone;
		private final ModelRenderer bone2;
		private final ModelRenderer bone3;
		private final ModelRenderer bone4;
		private final ModelRenderer bone5;
		private final ModelRenderer bone6;
		public ModelSpike() {
			textureWidth = 32;
			textureHeight = 32;
			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 0.0F, 4.0F);
			bone.addChild(bone2);
			setRotationAngle(bone2, 0.1309F, 0.0F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 0, 0, -4.0F, -32.0F, 0.0F, 8, 32, 0, 0.0F, false));
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(0.0F, 0.0F, -4.0F);
			bone.addChild(bone3);
			setRotationAngle(bone3, -0.1309F, 0.0F, 0.0F);
			bone3.cubeList.add(new ModelBox(bone3, 8, 0, -4.0F, -32.0F, 0.0F, 8, 32, 0, 0.0F, false));
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(4.0F, 0.0F, 0.0F);
			bone.addChild(bone4);
			setRotationAngle(bone4, -0.1309F, -1.5708F, 0.0F);
			bone4.cubeList.add(new ModelBox(bone4, 8, 0, -4.0F, -32.0F, 0.0F, 8, 32, 0, 0.0F, false));
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(-4.0F, 0.0F, 0.0F);
			bone.addChild(bone5);
			setRotationAngle(bone5, 0.1309F, -1.5708F, 0.0F);
			bone5.cubeList.add(new ModelBox(bone5, 0, 0, -4.0F, -32.0F, 0.0F, 8, 32, 0, 0.0F, false));
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone.addChild(bone6);
			setRotationAngle(bone6, -1.5708F, 0.0F, 0.0F);
			bone6.cubeList.add(new ModelBox(bone6, 16, 24, -4.0F, -4.0F, 0.0F, 8, 8, 0, 0.0F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			bone.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
