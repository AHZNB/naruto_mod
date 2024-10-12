
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.init.SoundEvents;

import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFutonVacuum extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 185;
	public static final int ENTITYID_RANGED = 186;

	public EntityFutonVacuum(ElementsNarutomodMod instance) {
		super(instance, 449);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "futon_vacuum"), ENTITYID).name("futon_vacuum").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements ItemJutsu.IJutsu {
		private final AirStream airStream = new AirStream();
		private float damageModifier = 0.5f;
		private EntityLivingBase user;
		private float power;
		private int maxDuration;
		private float bulletSize;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase userIn, float powerIn) {
			this(userIn.world);
			this.user = userIn;
			this.power = powerIn;
			this.maxDuration = (int)(powerIn * 4f);
			this.bulletSize = 1.5f;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.FUTON;
		}

		@Override
		protected void entityInit() {
		}

		public void setBulletSize(float size) {
			this.bulletSize = size;
		}

		public void setDamageModifier(float dm) {
			this.damageModifier = dm;
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (this.ticksExisted % 5 == 1) {
					this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, MathHelper.sqrt(this.bulletSize) * 0.81f , this.rand.nextFloat() * 0.5f + 0.8f);
					this.airStream.execute2(this.user, this.power, this.bulletSize / 3);
				}
			}
			if (!this.world.isRemote && (this.ticksExisted > this.maxDuration || this.user == null || !this.user.isEntityAlive())) {
				this.setDead();
			}
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
				if (power >= 1.0f) {
					this.createJutsu(entity, power, (int)(power * 4f));
					return true;
				}
				return false;
			}

			public EC createJutsu(EntityLivingBase entity, float power, int duration) {
				EC entity1 = new EC(entity, power);
				entity1.maxDuration = duration;
				entity.world.spawnEntity(entity1);
				return entity1;
			}

			@Override
			public float getBasePower() {
				return 0.0f;
			}
	
			@Override
			public float getPowerupDelay() {
				return 20.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 50.0f;
			}
		}

		public void playImpactSound(double x, double y, double z) {
			this.world.playSound(null, x, y, z, net.minecraft.util.SoundEvent.REGISTRY
			 .getObject(new ResourceLocation("narutomod:bullet_impact")), SoundCategory.NEUTRAL, 
			 1f, 0.4f + this.rand.nextFloat() * 0.6f);
		}

		public class AirStream extends ProcedureAirPunch {
			public AirStream() {
				this.particlesDuring = null;
			}
	
			@Override
			protected void attackEntityFrom(Entity player, Entity target) {
				if (!target.equals(player)) {
					EC.this.playImpactSound(target.posX, target.posY, target.posZ);
					target.attackEntityFrom(ItemJutsu.causeJutsuDamage(EC.this, player), EC.this.power * EC.this.damageModifier);
				}
			}
	
			@Override
			protected void preExecuteParticles(Entity player) {
				Vec3d vec = player.getLookVec();
				Vec3d vec0 = vec.scale(2d).add(player.getPositionEyes(1f));
				Particles.Renderer particles = new Particles.Renderer(player.world);
				for (int i = 1; i < 200; i++) {
					Vec3d vec1 = vec.scale(EC.this.rand.nextDouble() * EC.this.power * 0.25d);
					particles.spawnParticles(Particles.Types.SMOKE, vec0.x, vec0.y, vec0.z, 1, 0d, 0d, 0d,
					 vec1.x, vec1.y, vec1.z, 0x20FFFFFF, (int)(EC.this.bulletSize * 10f), 8 + EC.this.rand.nextInt(33));
				}
				particles.send();
			}
		
			@Override
			protected float getBreakChance(BlockPos pos, Entity player, double range) {
				EC.this.playImpactSound(pos.getX(), pos.getY(), pos.getZ());
				return 0.2F;
			}
		}
	}
}
