
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.OpenGlHelper;

import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTenseiBakuGold extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 342;
	public static final int ENTITYID_RANGED = 343;

	public EntityTenseiBakuGold(ElementsNarutomodMod instance) {
		super(instance, 697);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "tensei_baku_gold"), ENTITYID).name("tensei_baku_gold").tracker(128, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
	}

	public static class EC extends EntityBeamBase.Base {
		private final int growTime = 20;
		private final AirPunch beam = new AirPunch();
		private float power;
		
		public EC(World worldIn) {
			super(worldIn);
		}

		public EC(EntityLivingBase shooter, float powerIn) {
			super(shooter);
			this.power = powerIn;
			this.updatePosition();
			this.shoot(powerIn);
		}

		@Override
		protected void updatePosition() {
			EntityLivingBase shooter = this.getShooter();
			if (shooter != null) {
				Vec3d vec = shooter.getLookVec().addVector(shooter.posX, shooter.posY + 1.2d, shooter.posZ);
				this.setPosition(vec.x, vec.y, vec.z);
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.shootingEntity instanceof EntityPlayer) {
				//PlayerRender.forceBowPose((EntityPlayer)this.shootingEntity, EnumHandSide.RIGHT, false);
				ProcedureSync.EntityNBTTag.removeAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.ticksAlive == 1 && this.shootingEntity instanceof EntityPlayer) {
				//PlayerRender.forceBowPose((EntityPlayer)this.shootingEntity, EnumHandSide.RIGHT, true);
				ProcedureSync.EntityNBTTag.setAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose, true);
			}
			if (!this.world.isRemote) {
				this.shoot(this.power);
				if (this.ticksAlive > this.growTime) {
					this.beam.execute2((EntityLivingBase)this.shootingEntity, (double)this.getBeamLength(), 3.0f);
				}
			}
			if (this.ticksAlive > 100 + this.growTime) {
				this.setDead();
			}
		}

		public class AirPunch extends ProcedureAirPunch {
			public AirPunch() {
				this.blockDropChance = -1.0F;
				this.blockHardnessLimit = 100f;
				this.particlesPre = null;
				//this.particlesDuring = net.minecraft.util.EnumParticleTypes.SMOKE_LARGE;
			}
			
			@Override
			protected void attackEntityFrom(EntityLivingBase player, Entity target) {
				target.hurtResistantTime = 10;
				target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(EC.this, player), EC.this.power * 0.6f);
			}

			@Nullable
			protected net.minecraft.entity.item.EntityItem processAffectedBlock(EntityLivingBase player, BlockPos pos, EnumFacing facing) {
				if (player.getRNG().nextFloat() < 0.005f) {
					player.world.playSound(null, pos,
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:explosion"))),
					 net.minecraft.util.SoundCategory.BLOCKS, 4.0f, player.getRNG().nextFloat() * 0.5f + 0.75f);
				}
				return super.processAffectedBlock(player, pos, facing);
			}

			@Override
			protected void breakBlockParticles(World world, BlockPos pos) {
				Particles.spawnParticle(world, Particles.Types.SMOKE, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
				 1, 0D, 0D, 0D, 0D, 0D, 0D, 0x80000000, 60);
			}

			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				return 1.0F;
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.spawnEntity(new EC(entity, power));
				entity.world.playSound(null, entity.posX, entity.posY + 2.0d, entity.posZ,
				 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:laser"))),
				 net.minecraft.util.SoundCategory.PLAYERS, 4.0f, 1.0f);
				return true;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class CustomRender extends Render<EC> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/beam_gold.png");

		public CustomRender(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}

		@Override
		public boolean shouldRender(EC livingEntity, ICamera camera, double camX, double camY, double camZ) {
			return true;
		}

	    protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
	        float f;
	        for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) ;
	        while (f >= 180.0F) {
	            f -= 360.0F;
	        }
	        return prevYawOffset + partialTicks * f;
	    }

		@Override
		public void doRender(EC bullet, double x, double y, double z, float yaw, float pt) {
			float f = ((float)bullet.ticksExisted + pt) * 0.01F;
			double max_l = bullet.getBeamLength();
			this.bindEntityTexture(bullet);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			//GlStateManager.rotate(bullet.prevRotationYaw + (bullet.rotationYaw - bullet.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(this.interpolateRotation(bullet.prevRotationYaw, bullet.rotationYaw, pt), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(90.0F - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * pt, 1.0F, 0.0F, 0.0F);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			GlStateManager.shadeModel(0x1D01);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			float f5 = 0.0F - f;
			float f6 = (float) max_l / 32.0F - f;
			float f10 = Math.min(((float)bullet.ticksExisted + pt) / (float)bullet.growTime, 1.0F);
			f10 *= f10;
			float f11 = 1.5F + (1.0F - f10) * 10.0F;
			bufferbuilder.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
			for (int j = 0; j <= 8; j++) {
				float f7 = MathHelper.sin((j % 8) * ((float) Math.PI * 2F) / 8.0F) * 0.5F;
				float f8 = MathHelper.cos((j % 8) * ((float) Math.PI * 2F) / 8.0F) * 0.5F;
				float f9 = (j % 8) / 8.0F;
				bufferbuilder.pos(f7, 0.0D, f8).tex(f9, f5).color(1.0f, 1.0f, 1.0f, 0.7f).endVertex();
				bufferbuilder.pos(f7 * f11, (float) max_l * f10, f8 * f11).tex(f9, f6).color(1.0f, 1.0f, 1.0f, 0.7f * f10).endVertex();
			}
			for (int j = 0; f10 > 0.98F && j <= 8; j++) {
				float f7 = MathHelper.sin((j % 8) * ((float) Math.PI * 2F) / 8.0F) * 0.6F;
				float f8 = MathHelper.cos((j % 8) * ((float) Math.PI * 2F) / 8.0F) * 0.6F;
				float f9 = (j % 8) / 8.0F;
				bufferbuilder.pos(f7, 0.0D, f8).tex(f9, f5).color(1.0f, 1.0f, 1.0f, 0.11f).endVertex();
				bufferbuilder.pos(f7 * f11, (float) max_l * f10, f8 * f11).tex(f9, f6).color(1.0f, 1.0f, 1.0f, 0.11f).endVertex();
			}
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.shadeModel(0x1D00);
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return texture;
		}
	}
}
