
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;

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

	public static class EC extends Entity {
		private final AirStream airStream = new AirStream();
		private final float damageModifier = 0.5f;
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
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (this.ticksExisted % 5 == 1) {
					this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1f, this.rand.nextFloat() * 0.5f + 0.8f);
					this.airStream.execute2(this.user, this.power, 0.5d);
				}
			}
			if (!this.world.isRemote && this.ticksExisted > (int)this.power * 4) {
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
					entity.world.spawnEntity(new EC(entity, power));
					return true;
				}
				return false;
			}
		}

		public void playImpactSound(double x, double y, double z) {
			this.world.playSound(null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
			 .getObject(new ResourceLocation("narutomod:bullet_impact")), SoundCategory.NEUTRAL, 
			 1f, 0.4f + this.rand.nextFloat() * 0.6f);
		}

		public class AirStream extends ProcedureAirPunch {
			public AirStream() {
				this.particlesDuring = null;
			}
	
			@Override
			protected void attackEntityFrom(EntityLivingBase player, Entity target) {
				if (!target.equals(player)) {
					EC.this.playImpactSound(target.posX, target.posY, target.posZ);
					target.attackEntityFrom(ItemJutsu.causeJutsuDamage(EC.this, player), EC.this.power * EC.this.damageModifier);				}
			}
	
			@Override
			protected void preExecuteParticles(EntityLivingBase player) {
				Vec3d vec = player.getLookVec();
				Vec3d vec0 = vec.scale(2d).addVector(player.posX, player.posY+1.6d, player.posZ);
				for (int i = 1; i < 400; i++) {
					Vec3d vec1 = vec.scale(player.getRNG().nextDouble() * EC.this.power * 0.25d);
					Particles.spawnParticle(player.world, Particles.Types.SMOKE, vec0.x, vec0.y, vec0.z,
					 1, 0d, 0d, 0d, vec1.x, vec1.y, vec1.z, 0x20FFFFFF, 10);
				}
			}
		
			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				EC.this.playImpactSound(pos.getX(), pos.getY(), pos.getZ());
				return 0.2F;
			}
		}
	}
}
