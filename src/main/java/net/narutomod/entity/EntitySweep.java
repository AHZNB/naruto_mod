
package net.narutomod.entity;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySweep extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 465;
	public static final int ENTITYID_RANGED = 466;

	public EntitySweep(ElementsNarutomodMod instance) {
		super(instance, 895);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(Base.class).id(new ResourceLocation("narutomod", "sweep"), ENTITYID)
				.name("sweep").tracker(64, 3, true).build());
	}

	public static class Base extends EntityScalableProjectile.Base {
		private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		protected int maxAge = 4;

		public Base(World world) {
			super(world);
			this.setOGSize(1.0F, 0.5F);
			this.isImmuneToFire = false;
		}

		public Base(EntityLivingBase shooter, int color, float scale) {
			super(shooter);
			this.setOGSize(1.0F, 0.5F);
			this.setColor(color);
			Vec3d vec = shooter.getPositionEyes(1f);
			this.setEntityScale(scale);
			this.setLocationAndAngles(vec.x, vec.y, vec.z, shooter.rotationYaw, shooter.rotationPitch);
			this.isImmuneToFire = true;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(COLOR, Integer.valueOf(0xFFFFFFFF));
		}

		private void setColor(int color) {
			if (!this.world.isRemote) {
				this.getDataManager().set(COLOR, Integer.valueOf(color));
			}
		}

		protected int getColor() {
			return ((Integer)this.dataManager.get(COLOR)).intValue();
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.ticksExisted > this.maxAge) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult param1RayTraceResult) {
		}

		@Override
		public boolean canBeCollidedWith() {
			return false;
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void renderParticles() {
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(Base.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public static class RenderCustom extends Render<Base> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/vacuumwave.png");
			private final ModelSweep model = new ModelSweep();
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public void doRender(Base entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.ticksExisted < 1) {
					return;
				}
				float ageInTicks = partialTicks + entity.ticksExisted;
				float scale = entity.getEntityScale();
				int color = entity.getColor();
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				//y += entity.height * 0.5F;
				GlStateManager.translate(x, y, z);
				float f1 = ProcedureUtils.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
				float f2 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
				GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(f2 - 180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.rotationRoll, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.alphaFunc(0x204, 0.01f);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				GlStateManager.depthMask(false);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, (color >> 24 & 0xFF) / 255.0F);
				float f = ageInTicks / (entity.maxAge + 1);
				for (int i = 0; i < this.model.segment.length; i++) {
					this.model.segment[i].showModel = false;
				}
				Vec3d vec = new Vec3d(x + this.renderManager.viewerPosX, y + this.renderManager.viewerPosY, z + this.renderManager.viewerPosZ);
				for (int j = (int)(f * 17.0F) + 1, i = Math.max(0, j - 4); i < Math.min(j, this.model.segment.length); i++) {
					this.model.segment[i].showModel = true;
					ProcedureUtils.RotationMatrix rotmat = new ProcedureUtils.RotationMatrix().rotateYaw(-f1).rotatePitch(-f2).rotateRoll(-entity.rotationRoll).rotateYaw(11.25F * i);
					Vec3d vec3 = new Vec3d(-0.5d * scale, 0d, 0d);
					for (int k = 0; k < 5; k++, rotmat.rotateYaw(2.25f)) {
						this.renderParticles(entity, vec, rotmat.transform(vec3), color, scale);
					}
				}
				this.model.render(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.depthMask(true);
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(0x204, 0.1f);
				GlStateManager.popMatrix();
			}

			protected void renderParticles(Base entity, Vec3d entityVec, Vec3d relVec, int color, float scale) {
			}
	
			@Override
			protected ResourceLocation getEntityTexture(Base entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		static class ModelSweep extends ModelBase {
			private final ModelRenderer bone;
			private final ModelRenderer[] segment = new ModelRenderer[17];
		
			public ModelSweep() {
				textureWidth = 16;
				textureHeight = 16;
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bone, 0.0F, -0.7854F, 0.0F);
				for (int j = 0; j < segment.length; j++) {
					segment[j] = new ModelRenderer(this);
					segment[j].setRotationPoint(0.0F, 0.0F, 0.0F);
					bone.addChild(segment[j]);
					setRotationAngle(segment[j], 0.0F, -0.1963F * j, 0.0F);
					segment[j].cubeList.add(new ModelBox(segment[j], -8, 0, -8.0F, 0.0F, 0.0F, 8, 0, 8, 0.0F, false));
				}
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
}
