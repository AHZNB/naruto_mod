
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;

import com.google.common.collect.ImmutableMap;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWaterStream extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 439;
	public static final int ENTITYID_RANGED = 440;

	public EntityWaterStream(ElementsNarutomodMod instance) {
		super(instance, 869);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "water_stream"), ENTITYID).name("water_stream").tracker(64, 3, true).build());
	}

	public static class EC extends EntityBeamBase.Base implements ItemJutsu.IJutsu {
		private final AirPunch stream = new AirPunch();
		private final float damageModifier = 0.5f;
		private int maxLife = 100;
		private float power;

		public EC(World a) {
			super(a);
		}

		public EC(EntityLivingBase shooter, float scale) {
			super(shooter);
			this.power = scale;
		}

		public void shoot() {
			if (this.shootingEntity != null) {
				Vec3d vec3d = this.shootingEntity.getLookVec();
				Vec3d vec3d1 = vec3d.add(this.shootingEntity.getPositionEyes(1f).subtract(0d, 0.2d, 0d));
				this.setPositionAndRotation(vec3d1.x, vec3d1.y, vec3d1.z, this.shootingEntity.rotationYaw, this.shootingEntity.rotationPitch);
				vec3d1 = vec3d.scale(this.power);
				this.shoot(vec3d1.x, vec3d1.y, vec3d1.z);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null) {
				if (this.ticksAlive == 1) {
					this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:waterblast")), 0.5f, this.power / 30f);
				} else if (this.ticksAlive > 40 && this.ticksAlive % 20 == 1) {
					this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:waterstream")), 0.4f, this.power / 30f - this.rand.nextFloat() * 0.1f);
				}
				this.shoot();
				this.stream.execute2(this.shootingEntity, (double)this.power, 0.5d);
			}
			if (!this.world.isRemote && (this.ticksAlive > this.maxLife || this.shootingEntity == null || !this.shootingEntity.isEntityAlive())) {
				this.setDead();
			}
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.SUITON;
		}

		public class AirPunch extends ProcedureAirPunch {
			public AirPunch() {
				this.blockDropChance = 0.4F;
				this.blockHardnessLimit = 5f;
				this.particlesPre = EnumParticleTypes.WATER_DROP;
				this.particlesDuring = EnumParticleTypes.WATER_WAKE;
			}

			@Override
			protected void preExecuteParticles(Entity player) {
				double range = this.getRange(0);
				Vec3d vec0 = EC.this.getPositionVector();
				Vec3d vec1 = player.getLookVec();
				Particles.Renderer particles = new Particles.Renderer(player.world);
				for (int i = 1, j = (int)(range * 10d); i < j; i++) {
					Vec3d vec = vec0.addVector((this.rand.nextDouble()-0.5d) * 0.2d,
					 this.rand.nextDouble() * 0.2d, (this.rand.nextDouble()-0.5d) * 0.2d);
					Vec3d vec3d = vec1.scale(range * (this.rand.nextDouble() * 0.5d + 0.5d) * 0.4d);
					particles.spawnParticles(Particles.Types.WATER_SPLASH, vec.x, vec.y, vec.z,
					 1, 0, 0, 0, vec3d.x, vec3d.y, vec3d.z, 35 + this.rand.nextInt(15));
				}
				particles.send();
			}

			@Override
			protected void attackEntityFrom(Entity player, Entity target) {
				target.extinguish();
				target.attackEntityFrom(ItemJutsu.causeJutsuDamage(EC.this, player),
						EC.this.power * EC.this.damageModifier);
			}

			@Override
			protected EntityItem processAffectedBlock(Entity player, BlockPos pos, EnumFacing facing) {
				EntityItem ret = super.processAffectedBlock(player, pos, facing);
				if ((ret != null || this.rand.nextFloat() < 0.025f) && player.world.isAirBlock(pos.up())) {
					new net.narutomod.event.EventSetBlocks(player.world, ImmutableMap.of(pos.up(),
					 Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(1))),
					 0, 10, false, false);
				}
				return ret;
			}

			@Override
			protected float getBreakChance(BlockPos pos, Entity player, double range) {
				return 1.0F - (float) ((Math.sqrt(player.getDistanceSqToCenter(pos))) / range);
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 5.0f) {
					this.createJutsu(entity, power, 100);
					return true;
				}
				return false;
			}

			public EC createJutsu(EntityLivingBase entity, float power, int duration) {
				EC entityarrow = new EC(entity, power);
				entityarrow.maxLife = duration;
				entityarrow.shoot();
				entity.world.spawnEntity(entityarrow);
				return entityarrow;
			}

			@Override
			public float getBasePower() {
				return 5.0f;
			}
	
			@Override
			public float getPowerupDelay() {
				return 20.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 30.0f;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderStream(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderStream extends EntityBeamBase.Renderer<EC> {
			private final ResourceLocation texture = new ResourceLocation("minecraft:textures/blocks/water_flow.png");
			private final ModelLongCube model = new ModelLongCube(1f);

			public RenderStream(RenderManager renderManager) {
				super(renderManager);
			}

			@Override
			public EntityBeamBase.Model getMainModel(EC entity, float pt) {
				float f = entity.ticksAlive >= entity.maxLife - 10
						? Math.max(((float)entity.maxLife - (float)entity.ticksAlive - pt) / 10f, 0f)
						: Math.min(((float)entity.ticksAlive + pt) / 10f, 1f);
				this.model.setLength(entity.getBeamLength() * f);
				return this.model;
				//return new ModelLongCube(entity.getBeamLength() * f);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 3.5.4
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelLongCube extends EntityBeamBase.Model {
			private ModelRenderer bone;
			private float length;
			protected float scale = 1.0F;

			public ModelLongCube(float lengthIn) {
				this.textureWidth = 32;
				this.textureHeight = 1024;
				this.setLength(lengthIn);
			}

			public void setLength(float lengthIn) {
				if (lengthIn < this.length - 0.01f || lengthIn > this.length + 0.01f) {
					this.bone = new ModelRenderer(this);
					this.bone.setRotationPoint(0.0F, 0.0F, 0.0F);
					this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -4.0F, -16.0F, -4.0F, 8, (int) (16f * lengthIn), 8, 0.0F, false));
					this.length = lengthIn;
				}
			}

			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, (this.scale - 1.0F) * 1.5F + 1F, 0.0F);
				GlStateManager.scale(this.scale * 0.6F, this.scale, this.scale * 0.6F);
				GlStateManager.color(1f, 1f, 1f, 1f);
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.bone.render(f5);
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		}
	}
}
