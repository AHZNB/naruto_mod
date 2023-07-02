
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityIntonRaiha extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 348;
	public static final int ENTITYID_RANGED = 349;

	public EntityIntonRaiha(ElementsNarutomodMod instance) {
		super(instance, 704);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "inton_raiha"), ENTITYID).name("inton_raiha").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private EntityLivingBase shooter;
		private float power;
		private int waitTime;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase shooterIn, float powerIn) {
			this(shooterIn.world);
			this.shooter = shooterIn;
			this.power = powerIn;
			this.setLocationAndAngles(shooterIn.posX, shooterIn.posY, shooterIn.posZ, 0f, 0f);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.shooter instanceof EntityPlayer) {
				ProcedureSync.EntityNBTTag.removeAndSync(this.shooter, NarutomodModVariables.forceBowPose);
			}
		}

		@Override
		public void onUpdate() {
			if (this.shooter != null && this.shooter.isEntityAlive()) {
				if (this.ticksExisted == 1) {
					if (this.shooter instanceof EntityPlayer) {
						ProcedureSync.EntityNBTTag.setAndSync(this.shooter, NarutomodModVariables.forceBowPose, true);
					}
					if (this.power >= 4.0f) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:intonraiha")), 1.0f, 1.0f);
						this.waitTime = 50;
					}
				}
				this.setPosition(this.shooter.posX, this.shooter.posY, this.shooter.posZ);
				float duration = (float)this.waitTime + this.power * 10.0f;
				if (this.ticksExisted > this.waitTime) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:electricity")),
					  0.6f, this.rand.nextFloat() + 0.5f);
					Vec3d vec = this.shooter.getLookVec();
					Vec3d vec1 = vec.add(this.shooter.getPositionVector().addVector(0d, 1.3d, 0d))
					 .addVector((this.rand.nextDouble()-0.5d) * 0.2d, (this.rand.nextDouble()-0.5d) * 0.2d, (this.rand.nextDouble()-0.5d) * 0.2d);
					Vec3d vec2 = vec.scale(this.power * 5f).rotateYaw((this.rand.nextFloat()-0.5f) * 1.0472f)
					 .rotatePitch((this.rand.nextFloat()-0.5f) * 1.0472f).add(this.shooter.getPositionEyes(1.0f));
					float f = 5.0f + this.power * 5.0f + 1.0f / ((float)this.ticksExisted - duration - 0.4f);
					EntityLightningArc.Base entity = new EntityLightningArc.Base(this.world, vec1, vec2, 0x80FF00FF, (int)f, 0.4f, 0.04f);
					entity.setDamage(ItemJutsu.causeSenjutsuDamage(this, this.shooter), this.power, true, this.shooter);
					this.world.spawnEntity(entity);
				}
				if (this.ticksExisted > (int)duration) {
					this.setDead();
				}
			} else if (!this.world.isRemote) {
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
				entity.world.spawnEntity(new EC(entity, power));
				return true;
			}
		}
	}
}
