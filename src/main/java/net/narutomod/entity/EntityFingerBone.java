
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.block.Block;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFingerBone extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 319;
	public static final int ENTITYID_RANGED = 320;

	public EntityFingerBone(ElementsNarutomodMod instance) {
		super(instance, 667);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "finger_bone"), ENTITYID).name("finger_bone").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
	}

	public static class EC extends EntityScalableProjectile.Base {
		private final float damage = 8.0f;

		public EC(World worldIn) {
			super(worldIn);
			this.setOGSize(0.2f, 0.2f);
			this.setEntityScale(0.4f);
			this.setNoGravity(true);
		}

		public EC(EntityLivingBase userIn) {
			super(userIn);
			this.setOGSize(0.2f, 0.2f);
			this.setEntityScale(0.4f);
			//this.setNoGravity(false);
		}

		@Override
		public void shoot(double x, double y, double z, float speed, float inaccuracy) {
			super.shoot(x, y, z, speed, inaccuracy);
			this.rotationPitch = MathHelper.wrapDegrees(this.rotationPitch + 90f);
			this.prevRotationPitch = this.rotationPitch;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksAlive > 100) {
				this.setDead();
			}
		}

		@Override
		protected RayTraceResult forwardsRaycast(boolean includeEntities, boolean ignoreExcludedEntity, @Nullable Entity excludedEntity) {
			RayTraceResult res = ProjectileHelper.forwardsRaycast(this, includeEntities, ignoreExcludedEntity, excludedEntity);
			return res != null && res.entityHit instanceof EC && ((EC)res.entityHit).shootingEntity != null 
			 && ((EC)res.entityHit).shootingEntity.equals(this.shootingEntity) ? null : res;
		}

		@Override
		public void renderParticles() {			
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit instanceof EC || (result.entityHit != null && result.entityHit.equals(this.shootingEntity))) {
				return;
			}
			if (!this.world.isRemote) {
				if (result.typeOfHit == RayTraceResult.Type.BLOCK && this.world instanceof WorldServer) {
					((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST,
					 result.hitVec.x, result.hitVec.y, result.hitVec.z, 4, 0D, 0D, 0D, 0.15D,
					 Block.getIdFromBlock(Blocks.BONE_BLOCK));
				}
				this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:bullet_impact")),
				 1f, 0.4f + this.rand.nextFloat() * 0.6f);
				if (result.entityHit != null) {
					result.entityHit.hurtResistantTime = 10;
					result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), this.damage);
				}
				this.setDead();
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent)
					  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:bonecrack"))),
					  SoundCategory.PLAYERS, 0.5f, entity.getRNG().nextFloat() * 0.6f + 0.6f);
				this.createJutsu(entity);
				return true;
			}

			public void createJutsu(EntityLivingBase entity) {
				Vec3d vec = entity.getLookVec();
				Vec3d vec1 = entity.getPositionVector().add(vec).addVector(0.0d, 1.4d, 0.0d);
				EC bullet = new EC(entity);
				bullet.setPosition(vec1.x, vec1.y, vec1.z);
				bullet.motionX = vec.x * 0.1d;
				bullet.motionY = vec.y * 0.1d;
				bullet.motionZ = vec.z * 0.1d;
				bullet.shoot(vec.x, vec.y, vec.z, 1.2f, 0.05f);
				entity.world.spawnEntity(bullet);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EC> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/fingerbone.png");
		protected final ModelFingerBone model;

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn);
			this.model = new ModelFingerBone();
			this.shadowSize = 0.1f;
		}

		@Override
		public void doRender(EC entity, double x, double y, double z, float entityYaw, float pt) {
			this.bindEntityTexture(entity);
			float scale = entity.getEntityScale();
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt - 180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(((float)entity.ticksExisted + pt) * 30, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(scale, scale, scale);
			this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return TEXTURE;
		}
	}

	// Made with Blockbench 3.9.3
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelFingerBone extends ModelBase {
		private final ModelRenderer bone2;
		private final ModelRenderer bone;
		public ModelFingerBone() {
			textureWidth = 32;
			textureHeight = 32;
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 12, 0, -1.5F, -1.0F, -1.5F, 3, 2, 3, 0.0F, false));
			bone2.cubeList.add(new ModelBox(bone2, 9, 5, -1.5F, -1.5F, -1.5F, 3, 3, 3, -0.1F, false));
			bone2.cubeList.add(new ModelBox(bone2, 0, 8, -1.5F, -2.0F, -1.5F, 3, 4, 3, -0.3F, false));
			bone2.cubeList.add(new ModelBox(bone2, 0, 0, -1.5F, -2.5F, -1.5F, 3, 5, 3, -0.5F, false));
			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.0F, 3.5F, 0.0F);
			setRotationAngle(bone, 0.0F, 0.5236F, 0.0F);
			bone.cubeList.add(new ModelBox(bone, 8, 17, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.1F, false));
			bone.cubeList.add(new ModelBox(bone, 0, 15, -1.0F, -1.5F, -1.0F, 2, 3, 2, -0.1F, false));
			bone.cubeList.add(new ModelBox(bone, 12, 11, -1.0F, -2.0F, -1.0F, 2, 4, 2, -0.3F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			bone2.render(f5);
			bone.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
