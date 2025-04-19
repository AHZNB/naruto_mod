
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemShoton;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityCrystalRay extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 478;
	public static final int ENTITYID_RANGED = 479;

	public EntityCrystalRay(ElementsNarutomodMod instance) {
		super(instance, 905);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "crystal_ray"), ENTITYID).name("crystal_ray").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityBeam.class)
		 .id(new ResourceLocation("narutomod", "crystal_ray_beam"), ENTITYID_RANGED).name("crystal_ray_beam").tracker(96, 1, true).build());
	}

	public static class EC extends EntityShieldBase implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> BEAM_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		protected static final String ENTITYID_KEY = "CrystalRayEntityId";
		private final int duration = 600;

		public EC(World world) {
			super(world);
			this.setSize(3.5f, 3.75f);
		}

		public EC(EntityLivingBase summonerIn) {
			super(summonerIn);
			this.setOwnerCanSteer(true, 0.0f);
			this.setNoGravity(true);
			summonerIn.getEntityData().setInteger(ENTITYID_KEY, this.getEntityId());
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.SHOTON;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(BEAM_ID, Integer.valueOf(-1));
		}

		private void setBeamEntity(EntityBeam entity) {
			this.getDataManager().set(BEAM_ID, Integer.valueOf(entity.getEntityId()));
		}

		@Nullable
		private EntityBeam getBeamEntity() {
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(BEAM_ID)).intValue());
			return entity instanceof EntityBeam ? (EntityBeam)entity : null;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(500.0D);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getTrueSource() == null) {
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public double getMountedYOffset() {
			return 1.125d;
		}

		@Override
		protected void turnBodyAndHead(Entity passenger) {
			this.rotationYaw = passenger.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase user = this.getSummoner();
				if (user != null) {
					user.getEntityData().removeTag(ENTITYID_KEY);
					ItemStack stack = ProcedureUtils.getMatchingItemStack(user, ItemShoton.block);
					if (stack != null) {
						ItemJutsu.setJutsuCooldown(stack, user, ItemShoton.RAY, 1800);
					}
				}
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ice_shoot_small")),
				 0.8f, this.rand.nextFloat() * 0.4f + 0.9f);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.world.isRemote) {
				EntityLivingBase user = this.getSummoner();
				if (user != null) {
					Particles.spawnParticle(this.world, Particles.Types.SMOKE, user.posX, user.posY + 0.8d, user.posZ, 
					 30, 0.2d, 0.4d, 0.2d, 0d, 0.1d, 0d, 0x10dce4ff, 40, 5, 0xF0, user.getEntityId());
				}
			} else if (this.ticksExisted > this.duration) {
				this.setDead();
			}
		}

		protected boolean shootRay() {
			if (!this.world.isRemote) {
				EntityLivingBase user = this.getSummoner();
				EntityBeam beamEntity = this.getBeamEntity();
				if (user != null && (beamEntity == null || beamEntity.isDead)) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ray")), 1.0f, 1.0f);
					this.setBeamEntity(EntityBeam.shoot(user, 0.5f, 0.7f));
					return true;
				}
			}
			return false;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			 	Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ENTITYID_KEY));
				if (!(entity1 instanceof EC)) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
					 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ice_shoot")),
					 net.minecraft.util.SoundCategory.PLAYERS, 0.8f, entity.getRNG().nextFloat() * 0.4f + 0.6f);
					entity.world.spawnEntity(new EC(entity));
					return true;
				} else {
					return ((EC)entity1).shootRay();
				}
			}

			@Override
			public void onUsingTick(ItemStack stack, EntityLivingBase player, float power) {
				if (!player.isRiding()) {
					player.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 5, 4, false, false));
				}
				ItemJutsu.IJutsuCallback.super.onUsingTick(stack, player, power);
			}
		}
	}

	public static class EntityBeam extends EntityBeamBase.Base {
		private static final DataParameter<Float> BEAM_WIDTH = EntityDataManager.<Float>createKey(EntityBeam.class, DataSerializers.FLOAT);
		private final int waitTime = 10;
		private final int duration = 60;
		private float power = 2.0f;
		private float damageMultiplier;
		private float prevBeamLength;
		
		public EntityBeam(World worldIn) {
			super(worldIn);
		}

		public EntityBeam(EntityLivingBase shooter, float damageMultiplierIn) {
			super(shooter);
			this.damageMultiplier = damageMultiplierIn;
			this.updatePosition();
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(BEAM_WIDTH, Float.valueOf(0.8f));
		}

		public float getBeamWidth() {
			return ((Float)this.getDataManager().get(BEAM_WIDTH)).floatValue();
		}

		protected void setBeamWidth(float width) {
			this.getDataManager().set(BEAM_WIDTH, Float.valueOf(width));
		}

		@Override
		protected void updatePosition() {
			EntityLivingBase shooter = this.getShooter();
			if (shooter != null) {
				Vec3d vec = shooter.getPositionEyes(1f).add(shooter.getLookVec().scale(3d));
				this.setPosition(vec.x, vec.y, vec.z);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.prevBeamLength = this.getBeamLength();
			if (!this.world.isRemote) {
				if (this.shootingEntity == null || !(this.shootingEntity.getRidingEntity() instanceof EC)
				 || this.ticksAlive > this.duration || this.power > 100.0f) {
					this.setDead();
				} else if (this.ticksAlive >= this.waitTime) {
					if (this.ticksAlive == this.waitTime) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:laser_long")),
						 4.0F, this.rand.nextFloat() * 0.4f + 0.7f);
					}
					this.power = Math.min((this.power + 1.0f) * 1.4f, 100.0f);
					Vec3d vec1 = this.getPositionVector();
					Vec3d vec2 = this.shootingEntity.getLookVec().scale(this.power).add(this.shootingEntity.getPositionEyes(1f));
					RayTraceResult hitres = ProcedureUtils.rayTrace(this.shootingEntity, vec1, vec2, 0d, false, false, null);
					if (hitres != null) {
						this.shoot(hitres);
						if (this.hitTrace.typeOfHit != RayTraceResult.Type.MISS) {
							ProcedureAoeCommand.set(this.world, this.hitTrace.hitVec.x, this.hitTrace.hitVec.y, this.hitTrace.hitVec.z, 0d, 3d)
							 .exclude(this.shootingEntity).resetHurtResistanceTime()
							 .damageEntities(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), this.power * this.damageMultiplier);
							this.world.newExplosion(this.shootingEntity, this.hitTrace.hitVec.x, this.hitTrace.hitVec.y, this.hitTrace.hitVec.z,
							 5.0f + this.damageMultiplier, this.rand.nextInt(4) == 0,
							 net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity));
						}
					}
				}
			}
		}

		public static EntityBeam shoot(EntityLivingBase shooter, float damageMultiplier, float beamWidth) {
			EntityBeam entity = new EntityBeam(shooter, damageMultiplier);
			entity.setBeamWidth(beamWidth);
			shooter.world.spawnEntity(entity);
			return entity;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntityBeam.class, renderManager -> new RenderBeam(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/prism.png");
			private final ResourceLocation textureBeam = new ResourceLocation("narutomod:textures/laser_blue.png");
			private final ModelPrism model = new ModelPrism();
			private final int growTime = 30;

			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.shadowSize = 0.0f;
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float pt) {
				float age = pt + entity.ticksExisted;
				entityYaw = -ProcedureUtils.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, pt);
				GlStateManager.pushMatrix();
				this.bindEntityTexture(entity);
				GlStateManager.translate(x, y + 1.875D, z);
				GlStateManager.rotate(entityYaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.color(1.0f, 1.0f, 1.0f, MathHelper.clamp(age / this.growTime, 0f, this.renderManager.renderViewEntity == entity.getControllingPassenger() && this.renderManager.options.thirdPersonView == 0  ? 0.2f : 1.0f));
				this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
				if (entityYaw != 0.0F && Math.abs(entityYaw) != 360.0F) {
					Vec3d vec = new Vec3d(x + this.renderManager.viewerPosX, y + this.renderManager.viewerPosY, z + this.renderManager.viewerPosZ);
					Vec3d vec2 = vec.addVector(0.0D, 3.75D, 0.0D);
					Vec3d vec3 = new Vec3d(1.75D, 1.875D, 0.0D).rotateYaw(entityYaw * (float)Math.PI / 180F).add(vec);
					Vec3d vec4 = new Vec3d(-1.75D, 1.875D, 0.0D).rotateYaw(entityYaw * (float)Math.PI / 180F).add(vec);
					this.renderParticles(vec, 0.3F, 0x40dce4ff);
					this.renderParticles(vec2, 0.3F, 0x40dce4ff);
					this.renderParticles(vec3, 0.3F, 0x40dce4ff);
					this.renderParticles(vec4, 0.3F, 0x40dce4ff);
					this.renderParticles(new Vec3d(0.0D, 1.875D, 1.75D).rotateYaw(entityYaw * (float)Math.PI / 180F).add(vec), 0.3F, 0x40dce4ff);
					this.renderParticles(new Vec3d(0.0D, 1.875D, -1.75D).rotateYaw(entityYaw * (float)Math.PI / 180F).add(vec), 0.3F, 0x40dce4ff);
					EntityBeam beamEntity = entity.getBeamEntity();
					if (beamEntity != null && !beamEntity.isDead) {
						Vec3d vec5 = this.getPosVec(beamEntity, pt);
						this.bindTexture(this.textureBeam);
						this.renderBeam(vec, vec5, age);
						this.renderBeam(vec2, vec5, age);
						this.renderBeam(vec3, vec5, age);
						this.renderBeam(vec4, vec5, age);
						this.renderParticles(vec5, 0.6F, 0x4078ccfb);
					}
				}
			}

			private void renderParticles(Vec3d vec, float size, int color) {
				World world = this.renderManager.world;
				for (int i = 0; i < (int)(size / 0.3F * 8.0F); i++) {
					Particles.spawnParticle(world, Particles.Types.SMOKE, vec.x, vec.y, vec.z, 1, 0d, 0d, 0d,
					 (world.rand.nextFloat()-0.5f) * size, (world.rand.nextFloat()-0.5f) * size, (world.rand.nextFloat()-0.5f) * size,
					 color, (int)(size / 0.3F * 5 + 5), 5, 0xf0, -1, 0);
				}
			}

			private void renderBeam(Vec3d from, Vec3d to, float ageInTicks) {
				Vec3d vec3d = to.subtract(from);
				float yaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180d / Math.PI));
				float pitch = (float) (-MathHelper.atan2(vec3d.y, MathHelper.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z)) * (180d / Math.PI));
				double d = vec3d.lengthVector();
				float f = ageInTicks * 0.02F;
				float f5 = 0.0F - f;
				float f6 = (float) d / 32.0F - f;
				GlStateManager.pushMatrix();
				GlStateManager.translate(from.x - this.renderManager.viewerPosX, from.y - this.renderManager.viewerPosY, from.z - this.renderManager.viewerPosZ);
				GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(ageInTicks * 180.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				bufferbuilder.pos(-0.1F, 0, 0).tex(0, f5).color(1.0F, 1.0F, 1.0F, 0.95F).endVertex();
				bufferbuilder.pos(0.1F, 0, 0).tex(1, f5).color(1.0F, 1.0F, 1.0F, 0.95F).endVertex();
				bufferbuilder.pos(0.1F, 0, d).tex(1, f6).color(1.0F, 1.0F, 1.0F, 0.95F).endVertex();
				bufferbuilder.pos(-0.1F, 0, d).tex(0, f6).color(1.0F, 1.0F, 1.0F, 0.95F).endVertex();
				tessellator.draw();
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			private Vec3d getPosVec(Entity entity, float pt) {
				Vec3d vec1 = new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ);
				return entity.getPositionVector().subtract(vec1).scale(pt).add(vec1);
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderBeam extends Render<EntityBeam> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/laser_blue.png");
	
			public RenderBeam(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			public boolean shouldRender(EntityBeam livingEntity, ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
			@Override
			public void doRender(EntityBeam bullet, double x, double y, double z, float yaw, float pt) {
				if (bullet.ticksAlive <= bullet.waitTime) {
					return;
				}
				float beamRadius = bullet.getBeamWidth();
				float age = (float)bullet.ticksExisted + pt;
				float f = age * 0.02F;
				float max_l = bullet.prevBeamLength + (bullet.getBeamLength() - bullet.prevBeamLength) * pt;
				yaw = ProcedureUtils.interpolateRotation(bullet.prevRotationYaw, bullet.rotationYaw, pt);
				float pitch = 90.0F - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * pt;
				this.bindEntityTexture(bullet);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);
				GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
    			GlStateManager.rotate(age * 180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.shadeModel(0x1D01);
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				float f5 = 0.0F - f;
				float f6 = max_l / 32.0F - f;
				float f11 = 1.4F;
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				bufferbuilder.pos(-beamRadius * 0.5F, 0, 0).tex(0, f5).color(1.0f, 1.0f, 1.0f, 0.95f).endVertex();
				bufferbuilder.pos(beamRadius * 0.5F, 0, 0).tex(1, f5).color(1.0f, 1.0f, 1.0f, 0.95f).endVertex();
				bufferbuilder.pos(beamRadius * 0.5F * f11, max_l, 0).tex(1, f6).color(1.0f, 1.0f, 1.0f, 0.98f).endVertex();
				bufferbuilder.pos(-beamRadius * 0.5F * f11, max_l, 0).tex(0, f6).color(1.0f, 1.0f, 1.0f, 0.98f).endVertex();
				tessellator.draw();
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.shadeModel(0x1D00);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityBeam entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelPrism extends ModelBase {
			private final ModelRenderer prism;
			private final ModelRenderer bone5;
			private final ModelRenderer bone;
			private final ModelRenderer bone4;
			private final ModelRenderer bone3;
			private final ModelRenderer bone2;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer bone9;
			private final ModelRenderer bone10;
		
			public ModelPrism() {
				textureWidth = 64;
				textureHeight = 64;
		
				prism = new ModelRenderer(this);
				prism.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
				prism.addChild(bone5);
				setRotationAngle(bone5, 0.0F, -0.7854F, 0.0F);
				
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, -15.0F, 0.0F);
				bone5.addChild(bone);
				setRotationAngle(bone, 0.0F, 0.0F, -0.5847F);
				bone.cubeList.add(new ModelBox(bone, 0, -20, 0.0F, 0.0F, -10.0F, 0, 18, 20, 0.0F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -15.0F, 0.0F);
				bone5.addChild(bone4);
				setRotationAngle(bone4, 1.5708F, -0.9861F, -1.5708F);
				bone4.cubeList.add(new ModelBox(bone4, 0, -20, 0.0F, 0.0F, -10.0F, 0, 18, 20, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, -15.0F, 0.0F);
				bone5.addChild(bone3);
				setRotationAngle(bone3, -1.5708F, 0.9861F, -1.5708F);
				bone3.cubeList.add(new ModelBox(bone3, 0, -20, 0.0F, 0.0F, -10.0F, 0, 18, 20, 0.0F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -15.0F, 0.0F);
				bone5.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.0F, 0.5847F);
				bone2.cubeList.add(new ModelBox(bone2, 0, -20, 0.0F, 0.0F, -10.0F, 0, 18, 20, 0.0F, true));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
				prism.addChild(bone6);
				setRotationAngle(bone6, 0.0F, -0.7854F, -3.1416F);
				
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, -15.0F, 0.0F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, 0.0F, 0.0F, -0.5847F);
				bone7.cubeList.add(new ModelBox(bone7, 0, -20, 0.0F, 0.0F, -10.0F, 0, 18, 20, 0.0F, false));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, -15.0F, 0.0F);
				bone6.addChild(bone8);
				setRotationAngle(bone8, 1.5708F, -0.9861F, -1.5708F);
				bone8.cubeList.add(new ModelBox(bone8, 0, -20, 0.0F, 0.0F, -10.0F, 0, 18, 20, 0.0F, false));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, -15.0F, 0.0F);
				bone6.addChild(bone9);
				setRotationAngle(bone9, -1.5708F, 0.9861F, -1.5708F);
				bone9.cubeList.add(new ModelBox(bone9, 0, -20, 0.0F, 0.0F, -10.0F, 0, 18, 20, 0.0F, false));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0F, -15.0F, 0.0F);
				bone6.addChild(bone10);
				setRotationAngle(bone10, 0.0F, 0.0F, 0.5847F);
				bone10.cubeList.add(new ModelBox(bone10, 0, -20, 0.0F, 0.0F, -10.0F, 0, 18, 20, 0.0F, true));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				prism.render(f5);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
