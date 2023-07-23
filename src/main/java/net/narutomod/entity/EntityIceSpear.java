
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
//import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityIceSpear extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 222;
	public static final int ENTITYID_RANGED = 223;

	public EntityIceSpear(ElementsNarutomodMod instance) {
		super(instance, 534);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "ice_spear"), ENTITYID).name("ice_spear").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
	}

	@SideOnly(Side.CLIENT)
	public class CustomRender extends EntitySpike.Renderer<EC> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/spike_ice.png");

		public CustomRender(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}

		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return TEXTURE;
		}
	}

	public static class EC extends EntitySpike.Base {
		private static final DataParameter<Float> RAND_YAW = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> RAND_PITCH = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		
		public EC(World world) {
			super(world);
			this.setColor(0xC0FFFFFF);
			this.setRandYawPitch();
		}

		public EC(EntityLivingBase userIn) {
			super(userIn, 0xC0FFFFFF);
			this.setRandYawPitch();
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(RAND_YAW, Float.valueOf(0f));
			this.dataManager.register(RAND_PITCH, Float.valueOf(0f));
		}

		private float getRandYaw() {
			return ((Float)this.dataManager.get(RAND_YAW)).floatValue();
		}

		private float getRandPitch() {
			return ((Float)this.dataManager.get(RAND_PITCH)).floatValue();
		}

		private void setRandYawPitch() {
			this.dataManager.set(RAND_YAW, Float.valueOf((this.rand.nextFloat() - 0.5f) * 90f));
			this.dataManager.set(RAND_PITCH, Float.valueOf((this.rand.nextFloat() - 0.5f) * 60f));
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.isLaunched() && !this.hasNoGravity() && !this.onGround) {
				this.rotationYaw += this.getRandYaw();
				this.rotationPitch += this.getRandPitch();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote 
			 && result.entityHit instanceof EntityLivingBase && !result.entityHit.equals(this.shootingEntity)) {
				((EntityLivingBase)result.entityHit).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 1));
				result.entityHit.hurtResistantTime = 10;
				result.entityHit.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity).setProjectile(), 10f);
				this.setDead();
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Vec3d vec = entity.getLookVec();
				Vec3d vec1 = entity.getPositionEyes(1f).add(vec.scale(1.5d));
				double d = MathHelper.sqrt(power);
				for (int i = 0; i < (int)(power * 3f); i++) {
					Vec3d vec2 = vec1.addVector((entity.getRNG().nextDouble()-0.5d) * d, entity.getRNG().nextDouble()-0.5d,
					 (entity.getRNG().nextDouble()-0.5d) * d);
					Vec3d vec3 = vec2.add(vec);
					this.createJutsu(entity.world, entity, vec2.x, vec2.y, vec2.z, vec3.x, vec3.y, vec3.z, 1.2f, 0.05f);
				}
				return true;
			}

			public void createJutsu(EntityLivingBase attacker, EntityLivingBase target, float power) {
				Vec3d vec1 = attacker.getPositionEyes(1f).add(attacker.getLookVec().scale(1.5d));
				for (int i = 0; i < (int)(power * 3f); i++) {
					Vec3d vec2 = vec1.addVector(attacker.getRNG().nextDouble()-0.5d, attacker.getRNG().nextDouble()-0.5d, attacker.getRNG().nextDouble()-0.5d);
					this.createJutsu(attacker.world, attacker, vec2.x, vec2.y, vec2.z, target.posX, target.posY + target.height/2, target.posZ, 1.2f, 0.05f);
				}
			}

			public void createJutsu(World world, int num, double fromX, double fromY, double fromZ, double toX, double toY, double toZ, float speed, float inaccuracy) {
				for (int i = 0; i < num; i++) {
					this.createJutsu(world, null, fromX, fromY, fromZ, toX, toY, toZ, speed, inaccuracy);
				}
			}

			public void createJutsu(World world, @Nullable EntityLivingBase shooter,
			 double fromX, double fromY, double fromZ, double toX, double toY, double toZ, float speed, float inaccuracy) {
				world.playSound(null, fromX, fromY, fromZ, SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ice_shoot_small")),
				 SoundCategory.NEUTRAL, 0.8f, world.rand.nextFloat() * 0.4f + 0.8f);
				EC entity1 = shooter != null ? new EC(shooter) : new EC(world);
				entity1.setEntityScale(0.5f);
				entity1.setPosition(fromX, fromY, fromZ);
				entity1.shoot(toX - fromX, toY - fromY, toZ - fromZ, speed, inaccuracy);
				entity1.setNoGravity(true);
				world.spawnEntity(entity1);
			}
		}

		public static void spawnShatteredShard(World worldIn, double x, double y, double z, double mX, double mY, double mZ) {
			EC entity = new EC(worldIn);
			entity.setEntityScale(worldIn.rand.nextFloat() * 0.5f + 0.05f);
			entity.setPositionAndRotation(x, y, z, entity.getRandYaw(), entity.getRandPitch());
			entity.motionX = mX;
			entity.motionY = mY;
			entity.motionZ = mZ;
			worldIn.spawnEntity(entity);
		}
	}
}
