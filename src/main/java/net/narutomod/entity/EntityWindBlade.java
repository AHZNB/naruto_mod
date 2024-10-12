
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWindBlade extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 453;
	public static final int ENTITYID_RANGED = 454;

	public EntityWindBlade(ElementsNarutomodMod instance) {
		super(instance, 885);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "wind_blade"), ENTITYID).name("wind_blade").tracker(64, 3, true).build());
	}

	public static class EC extends EntityScalableProjectile.Base implements ItemJutsu.IJutsu {
		private RayTraceResult targetTrace;

		public EC(World a) {
			super(a);
			this.setOGSize(0.6F, 0.1F);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase shooter, float scale) {
			super(shooter);
			this.setOGSize(0.6F, 0.1F);
			Vec3d vec = shooter.getLookVec().scale(scale * 0.3f + 0.3f).add(shooter.getPositionEyes(1f));
			this.setLocationAndAngles(vec.x, vec.y, vec.z, shooter.rotationYaw, shooter.rotationPitch);
			this.setEntityScale(scale);
			this.isImmuneToFire = true;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.FUTON;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.updateInFlightRotations();
			if (!this.world.isRemote && (this.ticksInAir > 200 || this.isInWater())) {
				this.setDead();
			} else if (this.shootingEntity != null) {
				if (this.targetTrace == null || this.targetTrace.entityHit == null) {
					if (this.getDistance(this.shootingEntity) < 48d) {
						RayTraceResult rt = ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d, 3d, EC.class);
						if (!this.shootingEntity.equals(rt.entityHit)) {
							this.targetTrace = rt;
						}
					}
				}
				if (this.targetTrace.entityHit != null) {
					Vec3d vec = this.targetTrace.entityHit.getPositionVector().addVector(0d, this.targetTrace.entityHit.height * 0.5f, 0d).subtract(this.getPositionVector());
					this.shootPrecise(vec.x, vec.y, vec.z, 0.96f);
				} else {
					Vec3d vec = this.targetTrace.hitVec.subtract(this.getPositionVector());
					this.shoot(vec.x, vec.y, vec.z, 0.6f, 0f);
				}
				if (this.ticksAlive % 8 == 1) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:windecho")), 0.1f, this.rand.nextFloat() * 0.4f + 1.8f);
				}
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote) {
				float scale = this.getEntityScale();
				if (result.entityHit != null) {
					if (!result.entityHit.equals(this.shootingEntity) && !(result.entityHit instanceof EC)) {
						result.entityHit.hurtResistantTime = 10;
						result.entityHit.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), scale * 20f);
						this.setDead();
					}
				} else {
					((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST,
					 result.hitVec.x, result.hitVec.y, result.hitVec.z, (int)(scale * 8f), 0D, 0D, 0D, 0.4D,
					 Block.getIdFromBlock(this.world.getBlockState(result.getBlockPos()).getBlock()));
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:bullet_impact")),
				 	 0.5f, 0.4f + this.rand.nextFloat() * 0.6f);
					this.setDead();
				}
			}
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void renderParticles() {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 1.0F) {
					this.createJutsu(entity, power);
					return true;
				}
				return false;
			}

			public static EC createJutsu(EntityLivingBase entity, float power) {
				EC ec = new EC(entity, power);
				entity.world.spawnEntity(ec);
				return ec;
			}

			@Override
			public float getBasePower() {
				return 0.9f;
			}
	
			@Override
			public float getPowerupDelay() {
				return 100.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 5.0f;
			}
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/disk.png");
			private final ModelDisk mainModel;

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.mainModel = new ModelDisk();
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				float scale = entity.getEntityScale();
				float f = (float)entity.ticksExisted + partialTicks;
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				y += entity.height * 0.5F;
				GlStateManager.translate(x, y, z);
				float f1 = ProcedureUtils.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
				float f2 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
				float f3 = entity.prevRotationRoll + (entity.rotationRoll - entity.prevRotationRoll) * partialTicks;
				GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(f2 - 180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(f3, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.alphaFunc(0x204, 0.01f);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.4F);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.mainModel.render(entity, 0.0F, 0.0F, f, 0.0F, 0.0F, 0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(0x204, 0.1f);
				GlStateManager.popMatrix();
				this.renderParticles(entity.world, new Vec3d(x + this.renderManager.viewerPosX, y + this.renderManager.viewerPosY, z + this.renderManager.viewerPosZ), f1, f2, f3, scale, f);
			}

			private void renderParticles(World worldIn, Vec3d vec, float yaw, float pitch, float roll, float size, float ageInTicks) {
				Vec3d vec3 = new Vec3d(0.3d * size, 0d, 0d);
				ProcedureUtils.RotationMatrix rotationMatrix = new ProcedureUtils.RotationMatrix().rotateYaw(-yaw).rotatePitch(-pitch).rotateRoll(-roll).rotateY(ageInTicks);
				for (int i = 0; i < 4; i++) {
					Vec3d vec1 = rotationMatrix.rotateYaw(90f).transform(vec3).add(vec);
					Vec3d vec2 = vec1.subtract(vec).scale(0.5d);
					Particles.spawnParticle(worldIn, Particles.Types.SMOKE, vec1.x, vec1.y, vec1.z,
					 1, 0d, 0d, 0d, vec2.x, vec2.y, vec2.z, 0x10FFFFFF, (int)(size * 5), 0);
				}
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 4.10.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelDisk extends ModelBase {
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			public ModelDisk() {
				textureWidth = 16;
				textureHeight = 16;
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bone2, 0.0F, 0.0F, 0.0436F);
				bone2.cubeList.add(new ModelBox(bone2, -8, 0, -4.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bone3, 0.0F, 0.0F, -0.0436F);
				bone3.cubeList.add(new ModelBox(bone3, -8, 0, -4.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bone4, -0.0436F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, -8, 0, -4.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bone5, 0.0436F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, -8, 0, -4.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				bone2.rotateAngleY = f2 * 1.5F;
				bone3.rotateAngleY = f2 * 1.5F;
				bone4.rotateAngleY = f2 * 1.5F;
				bone5.rotateAngleY = f2 * 1.5F;
				bone2.render(f5);
				bone3.render(f5);
				bone4.render(f5);
				bone5.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
