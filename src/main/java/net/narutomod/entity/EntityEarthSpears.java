
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEarthSpears extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 243;
	public static final int ENTITYID_RANGED = 244;

	public EntityEarthSpears(ElementsNarutomodMod instance) {
		super(instance, 571);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
			.id(new ResourceLocation("narutomod", "earth_spears"), ENTITYID).name("earth_spears").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> {
			return new EntitySpike.Renderer<EC>(renderManager) {
				@Override
				protected ResourceLocation getEntityTexture(EC entity) {
					return new ResourceLocation("narutomod:textures/spike_stone.png");
				}
			};
		});
	}

	public static class EC extends EntitySpike.Base {
		private final int growTime = 8;
		private final float maxScale = 2.0f;
		private final float damage = 10.0f;

		public EC(World worldIn) {
			super(worldIn);
			this.setColor(0xFFFFFFFF);
		}

		public EC(EntityLivingBase userIn, float damageIn) {
			super(userIn, 0xFFFFFFFF);
			//this.damage = Math.min(damageIn, 20f);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksAlive <= this.growTime) {
				if (this.world instanceof WorldServer) {
					((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST,
					 this.posX, this.posY, this.posZ, 6, 0D, 0D, 0D, 0.15D, Block.getIdFromBlock(Blocks.STONE));
				}
				this.setEntityScale(MathHelper.clamp(this.maxScale * (float)this.ticksAlive / this.growTime, 0.0f, this.maxScale));
				for (EntityLivingBase entity : 
				 this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(1d, 0d, 1d))) {
					if (!entity.equals(this.shootingEntity)) {
						entity.hurtResistantTime = 10;
						entity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity),
						 this.damage * (1f - (float)(this.ticksAlive - 1) / this.growTime));
					}
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				World world = entity.world;
				Vec3d vec3d = entity.getPositionEyes(1f);
				Vec3d vec3d2 = vec3d.add(entity.getLookVec().scale(30d));
				RayTraceResult res = world.rayTraceBlocks(vec3d, vec3d2, false, true, true);
				if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK && res.sideHit == EnumFacing.UP) {
					world.playSound(null, res.getBlockPos(),
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hand_press")),
					 net.minecraft.util.SoundCategory.BLOCKS, 5f, entity.getRNG().nextFloat() * 0.4f + 0.8f);
					float f = MathHelper.sqrt(power * 9f / 5f);
					for (int i = 0; i < Math.round(power); i++) {
						EC entity1 = new EC(entity, power);
						Vec3d vec = res.hitVec.addVector((entity.getRNG().nextDouble() - 0.5d) * f, 0d, (entity.getRNG().nextDouble() - 0.5d) * f);
						for (; !world.getBlockState(new BlockPos(vec)).isTopSolid(); vec = vec.subtract(0d, 1d, 0d));
						for (; world.getBlockState(new BlockPos(vec).up()).isTopSolid(); vec = vec.addVector(0d, 1d, 0d));
						entity1.setLocationAndAngles(vec.x, vec.y + 0.5d, vec.z, entity.getRNG().nextFloat() * 360f, (entity.getRNG().nextFloat() - 0.5f) * 60f);
						world.spawnEntity(entity1);
					}
					return true;
				}
				return false;
			}
		}
	}
}
