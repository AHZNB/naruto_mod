package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockLiquid;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySuitonShark extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 193;
	public static final int ENTITYID_RANGED = 194;

	public EntitySuitonShark(ElementsNarutomodMod instance) {
		super(instance, 453);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "suiton_shark"), ENTITYID).name("suiton_shark").tracker(64, 3, true).build());
	}

	public static class EC extends EntityScalableProjectile.Base {
		private static final DataParameter<Float> PREVLSA = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> LSA = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> LS = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> MOUTHOPENAMOUNT = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private final int wait = 30;
		private final int mouthOpenTime = 20;
		private float fullScale;
		private Entity target;
		private float health;
		
		public EC(World a) {
			super(a);
			this.setOGSize(1.0F, 0.5F);
			this.setWaterSlowdown(1.0f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase shooter, float power) {
			super(shooter);
			this.setOGSize(1.0F, 0.5F);
			this.setWaitPosition();
			this.setEntityScale(0.2f);
			this.fullScale = power;
			this.health = power * 20f;
			this.setWaterSlowdown(1.0f);
			this.isImmuneToFire = true;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(PREVLSA, Float.valueOf(0.0F));
			this.getDataManager().register(LSA, Float.valueOf(0.0F));
			this.getDataManager().register(LS, Float.valueOf(0.0F));
			this.getDataManager().register(MOUTHOPENAMOUNT, Float.valueOf(0.0F));
		}

		public float getPrevLSA() {
			return ((Float) this.getDataManager().get(PREVLSA)).floatValue();
		}

		public void setPrevLSA(float f) {
			this.getDataManager().set(PREVLSA, Float.valueOf(f));
		}

		public float getLSA() {
			return ((Float) this.getDataManager().get(LSA)).floatValue();
		}

		public void setLSA(float f) {
			this.getDataManager().set(LSA, Float.valueOf(f));
		}

		public float getLS() {
			return ((Float) this.getDataManager().get(LS)).floatValue();
		}

		public void setLS(float f) {
			this.getDataManager().set(LS, Float.valueOf(f));
		}

		public float getMOA() {
			return ((Float) this.getDataManager().get(MOUTHOPENAMOUNT)).floatValue();
		}

		public void setMOA(float f) {
			this.getDataManager().set(MOUTHOPENAMOUNT, Float.valueOf(f));
		}

		private void setWaitPosition() {
			Vec3d vec = this.shootingEntity.getLookVec().scale(2d).add(this.shootingEntity.getPositionEyes(1f));
			this.setLocationAndAngles(vec.x, vec.y, vec.z, this.shootingEntity.rotationYawHead, this.shootingEntity.rotationPitch);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null) {
				if (!this.isLaunched() && !this.isBeingRidden()) {
					this.setWaitPosition();
				}
				if (this.ticksAlive <= this.wait) {
					this.setEntityScale(this.fullScale * MathHelper.clamp((float)this.ticksAlive / this.wait, 0.2F, 1.0F));
					if (this.world instanceof WorldServer) {
						((WorldServer)this.world).spawnParticle(EnumParticleTypes.WATER_WAKE, this.posX, this.posY + this.height/2, this.posZ, 
						  10, this.width/2, this.height/2, this.width/2, 0);
					}
				} else {
					if (this.target != null) {
						Vec3d vec = this.target.getPositionEyes(1f).subtract(this.getPositionVector());
						this.shoot(vec.x, vec.y, vec.z, this.isInWater() ? 0.85f : 0.8f, 0f);
					} else {
						this.target = this.shootingEntity instanceof EntityLiving ? ((EntityLiving)this.shootingEntity).getAttackTarget()
						 : ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d, this).entityHit;
						Vec3d vec = this.target != null ? this.target.getPositionEyes(1f).subtract(this.getPositionVector())
						 : this.shootingEntity.getLookVec();
						this.shoot(vec.x, vec.y, vec.z, this.isInWater() ? 0.9f : 0.85f, 0f);
					}
					if (this.ticksAlive <= this.wait + this.mouthOpenTime) {
						this.setMOA((float)(this.ticksAlive - this.wait) / this.mouthOpenTime);
					}
				}
			}
			this.updateLimbSwing();
			if (!this.world.isRemote
			 && (this.ticksInAir > 120 || this.shootingEntity == null || !this.shootingEntity.isEntityAlive())) {
				this.setDead();
			}
		}

		private void updateLimbSwing() {
			if (!this.world.isRemote) {
				this.setPrevLSA(this.getLSA());
		        double d5 = this.posX - this.prevPosX;
		        double d7 = this.posZ - this.prevPosZ;
		        double d9 = this.posY - this.prevPosY;
		        float f10 = MathHelper.sqrt(d5 * d5 + d9 * d9 + d7 * d7) * 4.0F;
		        if (f10 > 1.0F) {
		            f10 = 1.0F;
		        }
		        this.setLSA(this.getLSA() + (f10 - this.getLSA()) * 0.4F);
		        this.setLS(this.getLS() + this.getLSA());
			}
		}

		@Override
		public void renderParticles() {
			if (this.world instanceof WorldServer) {
				((WorldServer)this.world).spawnParticle(EnumParticleTypes.WATER_DROP, this.posX, this.posY + this.height/2, this.posZ, 
				  100, this.width/2, this.height/2, this.width/2, 0);
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && result.entityHit.equals(this.shootingEntity))
				return;
			if (result.typeOfHit == RayTraceResult.Type.BLOCK && this.fullScale >= 2.0f && this.ticksInAir <= 15) {
				return;
			}
			if (!this.world.isRemote) {
				if (result.typeOfHit == RayTraceResult.Type.BLOCK
				 || (result.entityHit != null && result.entityHit.equals(this.target))) {
					float size = this.getEntityScale();
					ProcedureAoeCommand.set(this, 0.0D, size).exclude(this.shootingEntity)
					  .damageEntities(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), size * (this.isInWater() ? 24f : 16f));
					this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, size * 2.0F, false,
					  net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity));
					Map<BlockPos, IBlockState> map = Maps.newHashMap();
					for (BlockPos pos : ProcedureUtils.getAllAirBlocks(this.world, this.getEntityBoundingBox().contract(0d, this.height-1, 0d))) {
						map.put(pos, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(1)));
					}
					new net.narutomod.event.EventSetBlocks(this.world, map, 0, 10, false, false);
					this.setDead();
				}
			}
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
			if (this.fullScale >= 2.0f && this.ticksInAir <= 10) {
				return player.startRiding(this);
			}
			return false;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (this.fullScale >= 4.0f && ItemJutsu.isDamageSourceNinjutsu(source)) {
				this.setEntityScale(this.getEntityScale() + amount * 0.013333f);
				if (source.getImmediateSource() != null && !(source.getImmediateSource() instanceof EntityLivingBase)) {
					source.getImmediateSource().setDead();
				}
				return false;
			} else if (this.fullScale >= 1.0f) {
				this.health -= amount;
				if (this.health <= 0.0f) {
					this.setDead();
				}
				return true;
			}
			return super.attackEntityFrom(source, amount);
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				return power >= 1.0f ? this.createJutsu(entity, power) : false;
			}

			public boolean createJutsu(EntityLivingBase entity, float power) {
				if (!entity.isInsideOfMaterial(Material.WATER)) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (net.minecraft.util.SoundEvent) 
					  net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:suikodannojutsu"))),
					  net.minecraft.util.SoundCategory.PLAYERS, 1, 1f);
				}
				entity.world.spawnEntity(new EC(entity, power));
				return true;
			}
		}
	}

	@SideOnly(Side.CLIENT)
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
			private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/shark.png");
			private final ModelShark model;

			public RenderCustom(RenderManager renderManager) {
				super(renderManager);
				this.model = new ModelShark();
				this.shadowSize = 0.5F;
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float yaw, float pt) {
				float f5 = entity.getPrevLSA() + (entity.getLSA() - entity.getPrevLSA()) * pt;
				float f6 = entity.getLS() - entity.getLSA() * (1.0F - pt);
				this.model.setRotationAngles(f6, f5, (float)entity.ticksExisted + pt, 0f, entity.getMOA(), 0.0625F, entity);
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				float scale = entity.getEntityScale();
				GlStateManager.translate(x, y, z);
				GlStateManager.rotate(-entity.prevRotationYaw - MathHelper.wrapDegrees(entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt - 180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableLighting();
				this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return TEXTURE;
			}
		}

		// Made with Blockbench 3.7.5
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelShark extends ModelBase {
			private final ModelRenderer body;
			private final ModelRenderer head;
			private final ModelRenderer foreHead;
			private final ModelRenderer jaw;
			private final ModelRenderer tail;
			private final ModelRenderer tailFin;
			private final ModelRenderer tailFinUpper;
			private final ModelRenderer tailFinLower;
			private final ModelRenderer backFin;
			private final ModelRenderer leftFin;
			private final ModelRenderer rightFin;
			public ModelShark() {
				textureWidth = 64;
				textureHeight = 64;
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 0.0F, -5.0F);
				body.cubeList.add(new ModelBox(body, 0, 0, -4.0F, -7.0F, 0.0F, 8, 7, 13, 0.0F, false));
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, -3.0F, 0.0F);
				body.addChild(head);
				foreHead = new ModelRenderer(this);
				foreHead.setRotationPoint(0.0F, -3.5F, 0.0F);
				head.addChild(foreHead);
				setRotationAngle(foreHead, 0.1745F, 0.0F, 0.0F);
				foreHead.cubeList.add(new ModelBox(foreHead, 19, 20, -4.0F, 0.0F, -6.0F, 8, 4, 6, 0.0F, false));
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, 1.5F, 0.25F);
				head.addChild(jaw);
				jaw.cubeList.add(new ModelBox(jaw, 29, 0, -3.5F, -1.5F, -4.75F, 7, 2, 5, 0.0F, false));
				tail = new ModelRenderer(this);
				tail.setRotationPoint(0.0F, -3.5F, 13.0F);
				body.addChild(tail);
				tail.cubeList.add(new ModelBox(tail, 0, 20, -2.0F, -2.5F, -1.0F, 4, 5, 11, 0.0F, false));
				tailFin = new ModelRenderer(this);
				tailFin.setRotationPoint(0.0F, -0.5F, 8.0F);
				tail.addChild(tailFin);
				tailFinUpper = new ModelRenderer(this);
				tailFinUpper.setRotationPoint(0.0F, -1.0F, 1.0F);
				tailFin.addChild(tailFinUpper);
				setRotationAngle(tailFinUpper, -0.6109F, 0.0F, 0.0F);
				tailFinUpper.cubeList.add(new ModelBox(tailFinUpper, 0, 20, -0.5F, -6.9924F, -1.1743F, 1, 8, 3, 0.0F, false));
				tailFinLower = new ModelRenderer(this);
				tailFinLower.setRotationPoint(0.0F, 1.0F, 1.0F);
				tailFin.addChild(tailFinLower);
				setRotationAngle(tailFinLower, 0.5236F, 0.0F, 0.0F);
				tailFinLower.cubeList.add(new ModelBox(tailFinLower, 0, 36, -0.5F, -1.4924F, -1.0403F, 1, 6, 3, 0.0F, false));
				backFin = new ModelRenderer(this);
				backFin.setRotationPoint(0.0F, -6.0F, 6.0F);
				body.addChild(backFin);
				setRotationAngle(backFin, -0.5236F, 0.0F, 0.0F);
				backFin.cubeList.add(new ModelBox(backFin, 0, 0, -0.5F, -7.75F, -1.5F, 1, 8, 4, 0.0F, false));
				leftFin = new ModelRenderer(this);
				leftFin.setRotationPoint(3.0F, -3.0F, 8.0F);
				body.addChild(leftFin);
				setRotationAngle(leftFin, 0.9599F, 0.0F, 1.8675F);
				leftFin.cubeList.add(new ModelBox(leftFin, 32, 34, 0.0F, -4.0F, -1.5F, 1, 4, 7, 0.0F, false));
				rightFin = new ModelRenderer(this);
				rightFin.setRotationPoint(-3.0F, -3.0F, 8.0F);
				body.addChild(rightFin);
				setRotationAngle(rightFin, 0.9599F, 0.0F, -1.8675F);
				rightFin.cubeList.add(new ModelBox(rightFin, 32, 34, -1.0F, -4.0F, -1.5F, 1, 4, 7, 0.0F, false));
			}

			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				body.render(f5);
			}

			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}

			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				this.tail.rotateAngleY = MathHelper.cos(f * 0.6662F) * 0.4F * f1;
				this.tailFin.rotateAngleY = MathHelper.cos(f * 0.6662F) * 0.4F * f1;
				this.foreHead.rotateAngleX = 0.1745F - f4 * 0.4363F;
				this.jaw.rotateAngleX = f4 * 0.5236F;
			}
		}
	}
}
