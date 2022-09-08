
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityRantonKoga extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 350;
	public static final int ENTITYID_RANGED = 351;

	public EntityRantonKoga(ElementsNarutomodMod instance) {
		super(instance, 705);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "ranton_koga"), ENTITYID).name("ranton_koga").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private EntityLivingBase shooter;
		private float power;

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
		public void onUpdate() {
			if (this.shooter != null && this.shooter.isEntityAlive()) {
				if (this.ticksExisted == 1) {
					this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:laser_short"))), 1.0f, 1.0f);
				}
				this.setPosition(this.shooter.posX, this.shooter.posY, this.shooter.posZ);
				Vec3d vec = this.shooter.getLookVec();
				Vec3d vec1 = vec.add(this.shooter.getPositionEyes(1f).subtract(0d, 0.15d, 0d));
				Vec3d vec2 = vec.scale(this.power * 4f).add(this.shooter.getPositionEyes(1.0f));
				EntityLightningArc.Base entity = new EntityLightningArc.Base(this.world, vec1, vec2, 0x80FF00FF, 1, 0f, 0f, 0);
				entity.setDamage(ItemJutsu.causeSenjutsuDamage(this, this.shooter), 20f * this.power, true, this.shooter);
				this.world.spawnEntity(entity);
				if (this.ticksExisted > 20) {
					this.setDead();
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
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
