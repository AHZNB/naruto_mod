
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFalseDarkness extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 241;
	public static final int ENTITYID_RANGED = 242;
	private static final float BASE_DAMAGE = 30f;

	public EntityFalseDarkness(ElementsNarutomodMod instance) {
		super(instance, 568);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "false_darkness"), ENTITYID).name("false_darkness").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private EntityLivingBase user;
		private EntityLivingBase target;
		private float power;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, EntityLivingBase targetIn, float powerIn) {
			this(userIn.world);
			this.user = userIn;
			this.target = targetIn;
			this.power = powerIn;
			this.setPosition(userIn.posX, userIn.posY + userIn.getEyeHeight() - 0.2d, userIn.posZ);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY + this.user.getEyeHeight() - 0.2d, this.user.posZ);
				int buildtime = (int)(this.power * 20f);
				if (this.ticksExisted <= buildtime) {
					float f = Math.min((float)this.ticksExisted / buildtime, 1.0f);
					if (this.rand.nextFloat() <= f * 0.2f) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:electricity"))),
						  0.4f, this.rand.nextFloat() * 0.5f + 1.5f);
					}
					for (int i = 0; i < (int)(f * 8f); i++) {
						EntityLightningArc.spawnAsParticle(this.world, this.posX + (this.rand.nextDouble()-0.5d)*0.6d, 
						 this.posY + (this.rand.nextDouble()-0.5d)*0.6d, this.posZ + (this.rand.nextDouble()-0.5d)*0.6d,
						 0.15d, 0d, 0d, 0d, 0x000000ff);
					}
				} else if (this.target != null) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:electricity"))),
					  10f, this.rand.nextFloat() * 0.6f + 0.3f);
					this.world.playSound(null, this.target.posX, this.target.posY, this.target.posZ,
					 SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
					EntityLightningArc.Base entity = new EntityLightningArc.Base(this.world, this.getPositionVector(), 
					 this.target.getPositionEyes(1f), 0x000000FF, 40, 0f);
					entity.setDamage(ItemJutsu.causeJutsuDamage(this, this.user), BASE_DAMAGE * this.power, this.user);
					this.world.spawnEntity(entity);
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
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 20d, 3d);
				if (res != null && res.entityHit instanceof EntityLivingBase) {
					entity.world.spawnEntity(new EC(entity, (EntityLivingBase)res.entityHit, power));
					return true;
				}
				return false;
			}
		}
	}
}
