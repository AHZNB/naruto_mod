
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.block.material.Material;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap;

@ElementsNarutomodMod.ModElement.Tag
public class EntityMeltingJutsu extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 272;
	public static final int ENTITYID_RANGED = 273;

	public EntityMeltingJutsu(ElementsNarutomodMod instance) {
		super(instance, 593);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "melting_jutsu"), ENTITYID).name("melting_jutsu").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
	}

	public static class EC extends EntityScalableProjectile.Base {
		private final int growTime = 20;
		private int duration;
		private BlockPos drip;
		private int deathTicks;
		private int deathTime;

		public EC(World world) {
			super(world);
			this.setOGSize(0.25F, 0.25F);
			this.setNoGravity(false);
		}

		public EC(EntityLivingBase shooter, float powerIn) {
			super(shooter);
			this.setOGSize(0.25F, 0.25F);
			this.setNoGravity(powerIn > 0f);
			this.setEntityScale(0.5f);
			this.setRotation(this.rand.nextFloat() * 360f, 0f);
			this.setIdlePosition();
			this.duration = (int)(powerIn * 20);
		}

		private void setIdlePosition() {
			if (this.shootingEntity != null) {
				Vec3d vec = this.shootingEntity.getPositionEyes(1f).add(this.shootingEntity.getLookVec().scale(0.4d));
				this.setPosition(vec.x, vec.y - 0.1d, vec.z);
			}
		}

		private void solidifyLava(BlockPos pos) {
			if (this.world.getBlockState(pos).getMaterial() == Material.LAVA) {
				this.world.setBlockToAir(pos);
				new net.narutomod.event.EventSetBlocks(this.world,
				 ImmutableMap.of(pos, Blocks.OBSIDIAN.getDefaultState()), 0, 600, false, false);
				this.solidifyLava(pos.down());
				//this.solidifyLava(pos.east());
				//this.solidifyLava(pos.west());
				//this.solidifyLava(pos.north());
				//this.solidifyLava(pos.south());
			}
		}
		
		private void onDeathUpdate() {
			if (!this.world.isRemote) {
				if (this.deathTicks >= this.deathTime) {
					this.setDead();
					if (this.drip != null) {
						this.solidifyLava(this.drip);
					}
				} else {
					for (EntityLivingBase entity : 
					 this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(0d, -1.0d, 0d))) {
					 	ProcedureUtils.multiplyVelocity(entity, 0.4d);
						entity.motionY -= 0.04d;
					}
				}
			}
			++this.deathTicks;
		}

		private void setDie() {
			this.deathTicks = 1;
			this.deathTime = 120 + this.rand.nextInt(80);
			this.world.setEntityState(this, (byte)100);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void handleStatusUpdate(byte id) {
			if (id == 100) {
				this.setDie();
			} else {
				super.handleStatusUpdate(id);
			}
		}

		@Override
		public void onUpdate() {
			if (this.deathTicks > 0) {
				this.onDeathUpdate();
				return;
			}
			super.onUpdate();
			if (this.duration > 0) {
				this.setIdlePosition();
				if (this.duration > 1) {
					Vec3d vec = this.shootingEntity.getLookVec();
					for (int i = 0; i < 10; i++) {
						EC entity = new EC(this.shootingEntity, 0f);
						entity.shoot(vec.x, vec.y, vec.z, 0.85f, 0.1f);
						this.world.spawnEntity(entity);
					}
					--this.duration;
				}
			} else {
				if (!this.world.isRemote && this.ticksInAir <= this.growTime) {
					this.setEntityScale(0.5F + 4.5F * (float)this.ticksInAir / this.growTime);
				}
				if (this.ticksInAir == this.rand.nextInt(99) + 1) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:movement")),
					 0.8f, this.rand.nextFloat() * 0.4f + 0.8f);
				}
			}
			if (!this.world.isRemote && this.ticksAlive > 100) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote) {
				if (result.entityHit instanceof EC) {
					return;
				}
				if (result.entityHit != null) {
					result.entityHit.getEntityData().setBoolean("TempData_disableKnockback", true);
					result.entityHit.hurtResistantTime = 10;
					result.entityHit.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity).setFireDamage(), 4f);
					result.entityHit.setFire(15);
				}
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, result.hitVec.x, result.hitVec.y, result.hitVec.z,
				 100, this.width, 0.0d, this.width, 0d, 0d, 0d, 0xB0202020, 20 + this.rand.nextInt(30));
				this.playSound(SoundEvents.BLOCK_LAVA_AMBIENT, 1f, this.rand.nextFloat() * 0.4f + 0.8f);
				if (this.world.getGameRules().getBoolean("mobGriefing")) {
					BlockPos pos = result.typeOfHit == RayTraceResult.Type.BLOCK 
					 ? result.getBlockPos().offset(result.sideHit) : new BlockPos(result.hitVec);
					if (this.world.isAirBlock(pos)) {
						this.world.setBlockState(pos, Blocks.LAVA.getDefaultState(), 3);
						this.drip = pos;
					}
				}
				this.setDie();
			}
		}

		@Override
		protected void checkOnGround() {
			super.checkOnGround();
			if (this.isInWater()) {
				this.setDead();
			}
		}

		@Override
		public void renderParticles() {
		}

		@Override
		public void updateInFlightRotations() {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 1.0f) {
					entity.world.spawnEntity(new EC(entity, power));
					return true;
				}
				return false;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EC> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/lava.png");
		private final ModelBlock renderModel = new ModelBlock();

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn);
			this.shadowSize = 0.3f;
		}

		@Override
		public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
			this.bindEntityTexture(entity);
			GlStateManager.pushMatrix();
			float scale = entity.getEntityScale();
			GlStateManager.translate(x, y + scale * 0.25f, z);
			GlStateManager.rotate(entityYaw, 0.0f, 1.0f, 0.0f);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.depthMask(true);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f = entity.ticksExisted + partialTicks;
			GlStateManager.translate(0.0F, f * 0.01F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			this.renderModel.render(entity, 0.0F, 0.0F, f, 0.0F, 0.0F, 0.0625F);
			GlStateManager.enableLighting();
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.depthMask(false);
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return TEXTURE;
		}
	}

	// Made with Blockbench 3.9.2
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelBlock extends ModelBase {
		private final ModelRenderer bb_main;
		public ModelBlock() {
			textureWidth = 16;
			textureHeight = 16;
			bb_main = new ModelRenderer(this);
			bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
			bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			bb_main.render(f5);
		}
	}
}
