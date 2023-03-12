
package net.narutomod.entity;

//import net.minecraftforge.fml.relauncher.SideOnly;
//import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityHidingInAsh extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 175;
	public static final int ENTITYID_RANGED = 176;

	public EntityHidingInAsh(ElementsNarutomodMod instance) {
		super(instance, 442);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "hiding_in_ash"), ENTITYID).name("hiding_in_ash").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private static final DataParameter<Integer> USER_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Float> RANGE = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final int maxLife = 110;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase userIn, double rangeIn) {
			this(userIn.world);
			this.setUser(userIn);
			this.setRange((float)rangeIn);
			this.setIdlePosition();
			userIn.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, this.maxLife, 0, false, false));
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(USER_ID, Integer.valueOf(-1));
			this.getDataManager().register(RANGE, Float.valueOf(1));
		}

		private void setUser(EntityLivingBase shooter) {
			this.getDataManager().set(USER_ID, Integer.valueOf(shooter.getEntityId()));
		}

		protected EntityLivingBase getUser() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(USER_ID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		public float getRange() {
			return ((Float)this.getDataManager().get(RANGE)).floatValue();
		}

		protected void setRange(float range) {
			this.getDataManager().set(RANGE, Float.valueOf(range));
		}

		protected void setIdlePosition() {
			EntityLivingBase user = this.getUser();
			if (user != null) {
				Vec3d vec3d = user.getLookVec();
				this.setPosition(user.posX + vec3d.x, user.posY + user.getEyeHeight() + vec3d.y - 0.2d, user.posZ + vec3d.z);
			}
		}

		@Override
		public void onUpdate() {
			//super.onUpdate();
			this.setIdlePosition();
			if (this.world.isRemote) {
				EntityLivingBase user = this.getUser();
				float range = this.getRange();
				for (int i = 0; i < (int)(range * 10); i++) {
					Particles.spawnParticle(this.world, Particles.Types.BURNING_ASH, this.posX, this.posY, this.posZ, 
					  1, 0, 0, 0, range * (this.rand.nextDouble()-0.5d) * 0.1d, (this.rand.nextDouble()-0.5d) * range * 0.1d,
					  range * (this.rand.nextDouble()-0.5d) * 0.1d, user != null ? user.getEntityId() : -1);
				}
			}
			if (this.ticksExisted > this.maxLife) {
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
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (net.minecraft.util.SoundEvent) 
				  net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:hiding_in_ash"))),
				  net.minecraft.util.SoundCategory.NEUTRAL, 5, 1f);
				entity.world.spawnEntity(new EC(entity, power));
				return true;
			}
		}
	}
}