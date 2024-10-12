
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemYooton;
import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityQuicklime extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 434;
	public static final int ENTITYID_RANGED = 435;

	public EntityQuicklime(ElementsNarutomodMod instance) {
		super(instance, 860);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "quicklime"), ENTITYID).name("quicklime").tracker(64, 3, true).build());
	}

	public static class EC extends EntityParticle.Base implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> SHOOTER = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private float damage;
		private EntityLivingBase hitEntity;
		private int hitTime;
		
		public EC(World w) {
			super(w);
		}

		public EC(EntityLivingBase shooterIn, double x, double y, double z, double mX, double mY, double mZ, float damageIn) {
			super(shooterIn.world, x, y, z, mX, mY, mZ, 0xF8B0B0B0, 6.0f, 0);
			this.setMaxAge(200);
			this.setShooter(shooterIn);
			this.damage = damageIn;
		}
				
		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.YOOTON;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SHOOTER, Integer.valueOf(-1));
		}

		@Nullable
		protected EntityLivingBase getShooter() {
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(SHOOTER)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void setShooter(@Nullable EntityLivingBase player) {
			this.getDataManager().set(SHOOTER, Integer.valueOf(player != null ? player.getEntityId() : -1));
		}

		@Override
		public void onUpdate() {
			//this.setParticleTextureOffset(4 + this.rand.nextInt(4));
			int age = this.getAge();
			int maxAge = this.getMaxAge();
			this.setParticleTextureOffset(MathHelper.clamp(7 - age * 8 / maxAge, 0, 7));
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			if (this.hitEntity == null) {
				this.motionY -= 0.05D;
				EntityLivingBase shooter = this.getShooter();
				RayTraceResult res = EntityScalableProjectile.forwardsRaycast(this, true, false, shooter);
				if (res != null && res.entityHit instanceof EntityLivingBase) {
					this.hitEntity = (EntityLivingBase)res.entityHit;
					this.hitTime = age;
					this.hitEntity.getEntityData().setBoolean("TempData_disableKnockback", true);
					this.hitEntity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, shooter), this.damage);
				}
			} else {
				this.motionX = this.hitEntity.posX - this.posX;
				this.motionY -= 0.01d;
				this.motionZ = this.hitEntity.posZ - this.posZ;
				if (!this.world.isRemote && (age - this.hitTime) % 15 == 1) {
					this.hitEntity.addPotionEffect(new PotionEffect(PotionHeaviness.potion, 16, (age - this.hitTime) / 15, false, false));
				}
			}
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.96D;
			this.motionY *= 0.96D;
			this.motionZ *= 0.96D;
			if (this.onGround) {
				this.motionX *= 0.5D;
				this.motionZ *= 0.5D;
			}
			if (!this.world.isRemote) {
				this.setAge(++age);
			}
			if (age > maxAge) {
				this.onDeath();
			}
		}

		@Override
		protected int getTexV() {
			return 2;
		}

		@Override
		public boolean shouldDisableDepth() {
			return true;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final float BASE_DAMAGE = 20.0f;

			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entityIn, float power) {
				if (stack.getItem() instanceof ItemYooton.RangedItem) {
					power = 1.0f / ((ItemYooton.RangedItem)stack.getItem()).getCurrentJutsuXpModifier(stack, entityIn);
					return this.createJutsu(entityIn, 2.0d, power) != null;
				}
				return false;
			}

			@Nullable
			public static EC createJutsu(EntityLivingBase entityIn, double speed, float damageMultiplier) {
				Vec3d vec = entityIn.getPositionEyes(1.0f);
				Vec3d vec1 = entityIn.getLookVec().scale(speed);
				EC entity1 = new EC(entityIn, vec.x, vec.y, vec.z, vec1.x, vec1.y, vec1.z, damageMultiplier * BASE_DAMAGE);
				if (entityIn.world.spawnEntity(entity1)) {
					entityIn.world.playSound(null, entityIn.posX, entityIn.posY, entityIn.posZ, 
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:spitbig")),
					 net.minecraft.util.SoundCategory.NEUTRAL, 1f, 0.8f + entityIn.getRNG().nextFloat() * 0.3f);
					Particles.Renderer particles = new Particles.Renderer(entityIn.world);
					for (int i = 0; i < 300; i++) {
						double d = entityIn.getRNG().nextDouble() * 0.4d;
						Vec3d vec2 = entityIn.getLookVec().scale(speed * (d + 0.6d));
						particles.spawnParticles(Particles.Types.SPIT, vec.x, vec.y, vec.z, 1,
						 0d, 0d, 0d, vec2.x, vec2.y, vec2.z, 0xF8B0B0B0, 5 + (int)(d * 237.5d), entityIn.getEntityId());
					}
					particles.send();
					return entity1;
				}
				return null;
			}
		}
	}
}
