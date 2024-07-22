
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@ElementsNarutomodMod.ModElement.Tag
public class EntityVacuumWave extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 455;
	public static final int ENTITYID_RANGED = 456;

	public EntityVacuumWave(ElementsNarutomodMod instance) {
		super(instance, 888);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "vacuum_wave"), ENTITYID).name("vacuum_wave").tracker(64, 3, true).build());
	}

	public static class EC extends EntityScalableProjectile.Base {
		private static final DataParameter<Integer> SERIAL_INDEX = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private final int maxAge = 4;

		public EC(World world) {
			super(world);
			this.setOGSize(1.0F, 1.0F);
			this.isImmuneToFire = false;
		}

		public EC(EntityLivingBase shooter, float scale, int index) {
			super(shooter);
			this.setOGSize(1.0F, 1.0F);
			Vec3d vec = shooter.getPositionEyes(1f);
			this.setEntityScale(scale);
			this.setLocationAndAngles(vec.x, vec.y - this.height * 0.5f, vec.z, shooter.rotationYaw, shooter.rotationPitch);
			this.setIndex(index);
			this.isImmuneToFire = true;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SERIAL_INDEX, Integer.valueOf(0));
		}

		private void setIndex(int index) {
			if (!this.world.isRemote) {
				this.getDataManager().set(SERIAL_INDEX, Integer.valueOf(index));
			}
			this.rotationRoll = 45.0f * index;
		}

		protected int getIndex() {
			return ((Integer)this.dataManager.get(SERIAL_INDEX)).intValue();
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (SERIAL_INDEX.equals(key) && this.world.isRemote) {
				this.setIndex(this.getIndex());
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				if (this.ticksExisted == 1) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:windblast")), 0.8f, 0.4f + this.rand.nextFloat() * 0.9f);
				}
				float scale = this.getEntityScale();
				if (this.ticksExisted > this.maxAge) {
					if (this.shootingEntity != null) {
						int index = this.getIndex();
						if (index < (int)((scale - 6.0f) * 0.5f) - 1) {
							this.world.spawnEntity(new EC(this.shootingEntity, scale, ++index));
						}
					}
					this.setDead();
				} else if (this.ticksExisted > this.maxAge - 2) {
					for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox())) {
						if (entity != this.shootingEntity && !entity.equals(this)
						 && entity.getDistance(this.posX, this. posY + this.height * 0.5f, this.posZ) <= 0.5d * this.width) {
							ProcedureUtils.Vec2f vec = ProcedureUtils.getYawPitchFromVec(entity.getPositionVector().subtract(this.getPositionVector()));
							vec = vec.subtract(this.rotationYaw, this.rotationPitch);
				            if (Math.abs(vec.x) <= 90f && Math.abs(vec.y) <= 90f) {
							 	entity.hurtResistantTime = 10;
								entity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), 17.0f + MathHelper.sqrt(scale));
				            }
						}
					}
				}
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

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 1.0F) {
					entity.world.spawnEntity(new EC(entity, power * 2f + 6f, 0));
					return true;
				}
				return false;
			}

			@Override
			public float getBasePower() {
				return 0.9f;
			}
	
			@Override
			public float getPowerupDelay() {
				return 60.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 10.0f;
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/vacuumwave.png");
			private final ModelVacuumWave model = new ModelVacuumWave();
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.ticksExisted < 1) {
					return;
				}
				float ageInTicks = partialTicks + entity.ticksExisted;
				float scale = entity.getEntityScale();
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				y += entity.height * 0.5F;
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
				float f = ageInTicks / (entity.maxAge + 1);
				for (int i = 0; i < this.model.segment.length; i++) {
					this.model.segment[i].showModel = false;
				}
				Vec3d vec = new Vec3d(x + this.renderManager.viewerPosX, y + this.renderManager.viewerPosY, z + this.renderManager.viewerPosZ);
				for (int j = (int)(f * 17.0F) + 1, i = Math.max(0, j - 4); i < Math.min(j, this.model.segment.length); i++) {
					this.model.segment[i].showModel = true;
					ProcedureUtils.RotationMatrix rotmat = new ProcedureUtils.RotationMatrix().rotateYaw(-f1).rotatePitch(f2).rotateRoll(-entity.rotationRoll).rotateYaw(11.25F * i);
					Vec3d vec3 = new Vec3d(-0.5d * scale, 0d, 0d);
					for (int k = 0; k < 5; k++, rotmat.rotateYaw(2.25f)) {
						Vec3d vec1 = rotmat.transform(vec3).add(vec);
						Vec3d vec2 = vec1.subtract(vec).scale(0.15d);
						Particles.spawnParticle(entity.world, Particles.Types.SMOKE, vec1.x, vec1.y, vec1.z,
						 1, 0d, 0d, 0d, vec2.x, vec2.y, vec2.z, 0x10FFFFFF, (int)(scale * 5), (int)(8.0d / (entity.rand().nextDouble() * 0.8d + 0.2d)));
					}
				}
				this.model.render(entity, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F, 0.0625F);
				GlStateManager.depthMask(true);
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.alphaFunc(0x204, 0.1f);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelVacuumWave extends ModelBase {
			private final ModelRenderer bone;
			private final ModelRenderer[] segment = new ModelRenderer[17];
		
			public ModelVacuumWave() {
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
