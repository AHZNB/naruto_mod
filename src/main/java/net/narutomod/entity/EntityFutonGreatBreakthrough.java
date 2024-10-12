
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemFuton;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFutonGreatBreakthrough extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 187;
	public static final int ENTITYID_RANGED = 188;

	public EntityFutonGreatBreakthrough(ElementsNarutomodMod instance) {
		super(instance, 450);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "futon_great_breakthrough"), ENTITYID)
		 .name("futon_great_breakthrough").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityWindParticle.class)
		 .id(new ResourceLocation("narutomod", "futon_great_breakthrough_particle"), ENTITYID_RANGED)
		 .name("futon_great_breakthrough_particle").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements ItemJutsu.IJutsu {
		public static final float MAX_RANGE = 64.0f;
		private int duration = 100;
		private EntityLivingBase user;
		private float power;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase userIn, float powerIn) {
			this(userIn.world);
			this.user = userIn;
			this.power = powerIn;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.FUTON;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (this.ticksExisted == 1) {
					this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:wind")),
					 1f, this.power * 0.2f);
				}
				boolean canfly = this.user instanceof EntityPlayer && !this.user.onGround;
				this.shoot(this.power, this.power * 0.25d, canfly);
				if (canfly && Chakra.pathway(this.user).consume(ItemFuton.BIGBLOW.chakraUsage * this.power * 0.0025d)) {
					++this.duration;
					ProcedureUtils.addVelocity(this.user, Vec3d.fromPitchYaw(this.user.rotationPitch, this.user.rotationYawHead).scale(-this.power * 0.003f));
				}
			}
			if (!this.world.isRemote && this.ticksExisted > this.duration) {
				this.setDead();
			}
		}

		protected void shoot(double range, double farRadius, boolean inAir) {
			Vec3d vec0 = this.user.getLookVec();
			Vec3d vec = vec0.scale(2d).addVector(this.user.posX, this.user.posY + 1.5d, this.user.posZ);
			int particleMaxAge = 16;
			int particleColor = 0x40FFFFFF;
			if (!inAir) {
				for (int i = 0; i < 5; i++) {
					Vec3d vec1 = vec0.scale((this.rand.nextDouble()*0.7d+0.3d) * range * 0.2d);
					double d = vec1.lengthVector() / range;
					this.world.spawnEntity(new EntityWindParticle(this, vec.x, vec.y, vec.z,
					 vec1.x + (this.rand.nextDouble()-0.5d) * farRadius * d * 2.5d,
					 vec1.y + (this.rand.nextDouble()-0.5d) * farRadius * d * 2.5d,
					 vec1.z + (this.rand.nextDouble()-0.5d) * farRadius * d * 2.5d, range));
				}
			} else {
				farRadius = 0.8d;
				particleMaxAge = 6;
				particleColor = 0x10FFFFFF;
			}
			Particles.Renderer particles = new Particles.Renderer(this.world);
			for (int i = 1; i <= 50; i++) {
				int maxage = (int)((double)particleMaxAge / (this.rand.nextDouble()*0.8D+0.2D));
				Vec3d vec1 = vec0.scale((this.rand.nextDouble()*0.7d+0.3d) * range * 0.2d);
				double d = vec1.lengthVector() / range;
				particles.spawnParticles(Particles.Types.SMOKE, vec.x, vec.y, vec.z, 1, 0d, 0d, 0d, 
				 vec1.x + (this.rand.nextDouble()-0.5d) * farRadius * d * 2.5d,
				 vec1.y + (this.rand.nextDouble()-0.5d) * farRadius * d * 2.5d,
				 vec1.z + (this.rand.nextDouble()-0.5d) * farRadius * d * 2.5d,
				 particleColor, (int)(vec1.lengthVector() * (inAir ? 10d : 40d)) + this.rand.nextInt(20), maxage);
			}
			particles.send();
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean isInRangeToRenderDist(double distance) {
			double d = 68.5d * this.getRenderDistanceWeight();
			return distance < d * d;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= this.getBasePower()) {
					entity.world.spawnEntity(new EC(entity, power));
					return true;
				}
				return false;
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
				return EC.MAX_RANGE;
			}
		}
	}

	public static class EntityWindParticle extends EntityParticle.Base {
		private static final DataParameter<Integer> SHOOTER = EntityDataManager.<Integer>createKey(EntityWindParticle.class, DataSerializers.VARINT);
		private static final DataParameter<Float> RANGE = EntityDataManager.<Float>createKey(EntityWindParticle.class, DataSerializers.FLOAT);
		private final List<Material> canRaiseDustList = Lists.newArrayList(Material.GRASS, Material.GROUND, Material.ROCK,
		 Material.WATER, Material.LAVA, Material.LEAVES, Material.PLANTS, Material.SAND, Material.SNOW, Material.CLAY);
		private EC ecEntity;
		
		public EntityWindParticle(World w) {
			super(w);
		}

		public EntityWindParticle(EC ecIn, double x, double y, double z, double mX, double mY, double mZ, double rangeIn) {
			super(ecIn.world, x, y, z, mX, mY, mZ, 0x40FFFFFF, 2.5f, 0);
			this.setMaxAge((int)(16.0f / (this.rand.nextFloat()*0.8f+0.2f)));
			this.ecEntity = ecIn;
			this.setShooter(ecIn.user);
			this.setRange((float)rangeIn);
		}
				
		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SHOOTER, Integer.valueOf(-1));
			this.getDataManager().register(RANGE, Float.valueOf(0.0f));
		}

		@Nullable
		protected EntityLivingBase getShooter() {
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(SHOOTER)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void setShooter(@Nullable EntityLivingBase player) {
			this.getDataManager().set(SHOOTER, Integer.valueOf(player != null ? player.getEntityId() : -1));
		}

		public float getRange() {
			return ((Float)this.getDataManager().get(RANGE)).floatValue();
		}

		protected void setRange(float range) {
			this.getDataManager().set(RANGE, Float.valueOf(range));
		}

		@Override
		public void onUpdate() {
			int age = this.getAge();
			int maxAge = this.getMaxAge();
			this.setParticleTextureOffset(MathHelper.clamp(7 - age * 8 / maxAge, 0, 7));
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.motionY += 0.004d;
			EntityLivingBase shooter = this.getShooter();
			RayTraceResult res = ProjectileHelper.forwardsRaycast(this, true, false, shooter);
			if (res != null && shooter != null) {
				if (res.entityHit != null) {
					ProcedureUtils.pushEntity(shooter, res.entityHit, this.getRange(), 3.0F);
				} else if (this.world.isRemote) {
					IBlockState blockstate = this.world.getBlockState(res.getBlockPos());
					if (this.canRaiseDustList.contains(blockstate.getMaterial())) {
						Vec3d vec = new Vec3d(res.sideHit.getDirectionVec());
						vec = this.multiply(vec, vec).scale(-2.0d).addVector(1.0d, 1.0d, 1.0d).scale(0.2d);
						Vec3d vec1 = res.hitVec.subtract(shooter.getPositionEyes(1f));
						vec1 = this.multiply(vec, vec1.normalize().scale(this.getRange() - (float)vec1.lengthVector()));
						Particles.spawnParticle(this.world, Particles.Types.BLOCK_DUST, res.hitVec.x, res.hitVec.y, res.hitVec.z,
						 6, 0.4d, 0.3d, 0.4d, vec1.x, vec1.y, vec1.z, Block.getIdFromBlock(blockstate.getBlock()), 50+this.rand.nextInt(50), 70, 10);
					}
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
				if (age > maxAge || this.ecEntity == null || this.ecEntity.isDead) {
					this.onDeath();
				}
			}
		}

		private Vec3d multiply(Vec3d vec1, Vec3d vec2) {
			return new Vec3d(vec1.x * vec2.x, vec1.y * vec2.y, vec1.z * vec2.z);
		}

		@Override
		protected int getTexV() {
			return 2;
		}

		@Override
		public boolean shouldDisableDepth() {
			return true;
		}
	}
}
