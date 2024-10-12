
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.item.ItemStack;

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

	public static class EC extends EntitySweep.Base implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> SERIAL_INDEX = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);

		public EC(World world) {
			super(world);
		}

		public EC(EntityLivingBase shooter, float scale, int index) {
			super(shooter, -1, scale);
			this.setIndex(index);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.FUTON;
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
					for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(0d, -this.height, 0d))) {
						if (entity != this.shootingEntity && !entity.equals(this)
						 && entity.getDistance(this) <= 0.5d * this.width) {
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
		public class RenderCustom extends EntitySweep.Renderer.RenderCustom {
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			protected void renderParticles(EntitySweep.Base entity, Vec3d entityVec, Vec3d relVec, int color, float scale) {
				Vec3d vec1 = entityVec.add(relVec);
				Vec3d vec2 = relVec.scale(0.15d);
				Particles.spawnParticle(entity.world, Particles.Types.SMOKE, vec1.x, vec1.y, vec1.z,
				 1, 0d, 0d, 0d, vec2.x, vec2.y, vec2.z, (0x10 << 24) | (color & 0x00FFFFFF), (int)(scale * 5), (int)(8.0d / (entity.rand().nextDouble() * 0.8d + 0.2d)));
			}
		}
	}
}
