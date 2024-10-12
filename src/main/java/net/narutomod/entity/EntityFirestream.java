
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import javax.vecmath.Vector4f;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFirestream extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 421;
	public static final int ENTITYID_RANGED = 422;

	public EntityFirestream(ElementsNarutomodMod instance) {
		super(instance, 844);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "firestream"), ENTITYID).name("firestream").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(FlameParticle.class)
		 .id(new ResourceLocation("narutomod", "fireparticle"), ENTITYID_RANGED).name("fireparticle").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements ItemJutsu.IJutsu {
		private int wait = 50;
		private int maxLife = 110;
		private EntityLivingBase shooter;
		private double width, range;
		private int flameColor = 0xffffcf00;
		private float damage;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase shooterIn, double widthIn, double rangeIn) {
			this(shooterIn.world);
			this.shooter = shooterIn;
			this.setIdlePosition();
			this.width = widthIn;
			this.range = rangeIn;
			this.damage = (float)rangeIn;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.RAITON;
		}

		@Override
		protected void entityInit() {
		}

		protected void setIdlePosition() {
			if (this.shooter != null) {
				Vec3d vec3d = this.shooter.getLookVec();
				this.setPosition(this.shooter.posX + vec3d.x, this.shooter.posY + this.shooter.getEyeHeight() + vec3d.y - 0.2d, this.shooter.posZ + vec3d.z);
			}
		}

		public void setFlameColor(int color) {
			this.flameColor = color;
		}

		public void setDamage(float amount) {
			this.damage = amount;
		}

		@Override
		public void onUpdate() {
			//super.onUpdate();
			if (!this.world.isRemote && (this.ticksExisted > this.maxLife || this.handleWaterMovement()
			 || this.shooter == null || !this.shooter.isEntityAlive())) {
				this.setDead();
			} else {
				this.setIdlePosition();
				if (!this.world.isRemote && this.ticksExisted > this.wait) {
					if (this.shooter != null) {
						double d = (double)this.ticksExisted / this.maxLife;
						d = 1.0d - d * d * 0.8d;
						if (this.shooter instanceof EntityLiving && ((EntityLiving)this.shooter).getAttackTarget() != null) {
							ProcedureUtils.Vec2f rota = ProcedureUtils.getYawPitchFromVec(((EntityLiving)this.shooter)
							 .getAttackTarget().getPositionVector().subtract(this.shooter.getPositionEyes(1f)));
							this.shoot(rota.x, rota.y, this.range * d, this.width * d);
						} else {
							this.shoot(this.shooter.rotationYaw, this.shooter.rotationPitch, this.range * d, this.width * d);
						}
					}
					if (this.ticksExisted < this.maxLife - 20 && this.ticksExisted % 10 == 1) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:flamethrow")), 
						 this.range > 30.0d ? 5.0f : 1.0f, this.rand.nextFloat() * 0.5f + 0.6f);
					}
				}
			}
		}

		protected void shoot(float directionYaw, float directionPitch, double range, double radius) {
			double angle = Math.atan(radius / range) * 180d / Math.PI;
			for (int i = 0; i < (int)(MathHelper.sqrt(radius) * 3.0d); i++) {
				Vec3d vec3d = Vec3d.fromPitchYaw(directionPitch + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d),
				 directionYaw + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d)).scale(range * 0.1d);
				this.world.spawnEntity(new FlameParticle(this.shooter, this.posX, this.posY, this.posZ,
				 vec3d.x, vec3d.y, vec3d.z, this.flameColor, 2.5f, this.damage * (this.rand.nextFloat() * 0.4f + 0.8f)));
			}
			Particles.Renderer particles = new Particles.Renderer(this.world);
			for (int i = 0; i < (int)(range * radius * 0.8d); i++) {
				Vec3d vec3d = Vec3d.fromPitchYaw(directionPitch + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d),
				 directionYaw + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d)).scale(range * 0.1d);
				particles.spawnParticles(Particles.Types.FLAME, this.posX, this.posY, this.posZ, 1, 0, 0, 0,
				 vec3d.x, vec3d.y, vec3d.z, this.flameColor, (int)(vec3d.lengthVector()*50d)+this.rand.nextInt(20));
			}
			particles.send();
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}


		public static class Jutsu1 implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
				  SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:katon_gokamekeku")),
				  SoundCategory.NEUTRAL, 5, 1f);
				entity.world.spawnEntity(new EC(entity, power * 0.8, power * 1.5));
				//ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, (long)(power * 200));
				return true;
			}

			@Override
			public float getPowerupDelay() {
				return 30.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 25.0f;
			}
		}

		public static class Jutsu2 implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				this.createJutsu(entity, power, (int)(power * 10f));
				//ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, (long)(power * 200));
				return true;
			}

			public EC createJutsu(EntityLivingBase entity, float power, int duration) {
				return this.createJutsu(entity, power, duration, 0xffffcf00);
			}

			public EC createJutsu(EntityLivingBase entity, float power, int duration, int color) {
				EC entity1 = new EC(entity, 1.0f, power);
				entity1.wait = 0;
				entity1.maxLife = duration;
				entity1.setFlameColor(color);
				entity.world.spawnEntity(entity1);
				return entity1;
			}

			@Override
			public float getPowerupDelay() {
				return 30.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 30.0f;
			}
		}
	}

	public static class FlameParticle extends EntityParticle.Base {
		private EntityLivingBase shooter;
		private float damage;
		
		public FlameParticle(World w) {
			super(w);
		}

		public FlameParticle(EntityLivingBase shooterIn, double x, double y, double z, double mX, double mY, double mZ, int color, float scale, float damageIn) {
			super(shooterIn.world, x, y, z, mX, mY, mZ, color, scale, 0);
			this.setMaxAge((int) (8.0D / (this.rand.nextDouble() * 0.8D + 0.2D)) + 4);
			this.shooter = shooterIn;
			this.damage = damageIn;
		}
				
		@Override
		public void onUpdate() {
			int age = this.getAge();
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (age > this.getMaxAge()) {
				this.onDeath();
			}
			this.setParticleTextureOffset((age / 2) % 8);
			this.motionY += 0.003D;
			if (this.shooter != null) {
				RayTraceResult res = ProjectileHelper.forwardsRaycast(this, true, true, this.shooter);
				if (res != null && res.typeOfHit != RayTraceResult.Type.MISS) {
					this.onImpact(res);
				}
			}
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.96D;
			this.motionY *= 0.96D;
			this.motionZ *= 0.96D;
			if (this.onGround) {
				this.motionX *= 0.7D;
				this.motionZ *= 0.7D;
			}
			if (!this.world.isRemote) {
				this.setAge(++age);
			}
		}

		public void onImpact(RayTraceResult result) {
			int i = this.rand.nextInt(8);
			if (result.entityHit != null) {
				result.entityHit.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shooter).setFireDamage(), this.damage);
				result.entityHit.setFire(10);
			} else if (i == 0) {
				BlockPos pos = result.getBlockPos().offset(result.sideHit);
				if (this.world.isAirBlock(pos)) {
					this.world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 3);
				}
			}
		}

		@Override
		protected int getTexV() {
			return 1;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public int getBrightnessForRender() {
			return 0x00F000F0;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public float getScale(float partialTicks) {
			float f = Math.min(((float)this.getAge() + partialTicks) / (float) this.getMaxAge(), 1.0F);
			float f11 = f - 0.5f;
			float f8 = 1f - f11 * f11 * 3.5f;
			return this.getScale() * f8;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public Vector4f getColor(float partialTicks) {
			float f = Math.min(((float)this.getAge() + partialTicks) / (float) this.getMaxAge(), 1.0F);
			float f11 = f - 0.5f;
			float f8 = 1f - f11 * f11 * 3.5f;
			Vector4f vec4f = this.getColor();
	        return new Vector4f(vec4f.x, vec4f.y * (1.0F - f), vec4f.z, vec4f.w * f8);
		}

		@Override
		public boolean shouldDisableDepth() {
			return true;
		}
	}
}
