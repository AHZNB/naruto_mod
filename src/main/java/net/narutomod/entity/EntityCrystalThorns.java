
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.block.material.Material;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityCrystalThorns extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 476;
	public static final int ENTITYID_RANGED = 477;

	public EntityCrystalThorns(ElementsNarutomodMod instance) {
		super(instance, 903);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "crystal_thorns"), ENTITYID).name("crystal_thorns").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCrystalSpike.class)
				.id(new ResourceLocation("narutomod", "crystal_thorn"), ENTITYID_RANGED).name("crystal_thorn").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		protected static final String ENTITYID_KEY = "CrystalThornsEntityId";
		private final float speed = 1.5f;
		private int maxLife = 5;
		private EntityLivingBase shooter;
		private EntityCrystalSpike lastSpike;
		private EntityLivingBase target;
		
		public EC(World worldIn) {
			super(worldIn);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase shooterIn) {
			this(shooterIn.world);
			this.shooter = shooterIn;
			this.setIdlePosition();
		}

		@Override
		protected void entityInit() {
		}

		protected void setIdlePosition() {
			if (this.shooter != null) {
				this.setPosition(this.shooter.posX, this.shooter.posY, this.shooter.posZ);
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.shooter != null) {
				this.shooter.getEntityData().removeTag(ENTITYID_KEY);
			}
		}

		@Override
		public void onUpdate() {
			if (!this.world.isRemote && (this.ticksExisted > this.maxLife || this.shooter == null || !this.shooter.isEntityAlive())) {
				this.setDead();
			} else {
				this.setIdlePosition();
				if (!this.world.isRemote) {
					Vec3d targetpos = null;
					Vec3d lastpos = this.lastSpike != null ? this.lastSpike.getPositionVector() : this.shooter.getLookVec().scale(2d).add(this.shooter.getPositionEyes(1f));
					if (this.target == null) {
						RayTraceResult res = ProcedureUtils.objectEntityLookingAt(this.shooter, 40d, 3d, false, true, new Predicate<Entity>() {
							@Override
							public boolean apply(@Nullable Entity p_apply_1_) {
								return p_apply_1_ instanceof EntityLivingBase && !(p_apply_1_ instanceof EntityCrystalSpike);
							}
						});
						if (res != null) {
							if (res.entityHit != null) {
								this.target = (EntityLivingBase)res.entityHit;
							}
							targetpos = res.hitVec;
						}
					} else if (this.target.getDistance(this) < 45d) {
						targetpos = this.target.getPositionVector().addVector(0d, this.target.height * 0.5f, 0d);
					}
					if (targetpos != null) {
						Vec3d vec = targetpos.subtract(lastpos);
						if (vec.x * vec.x + vec.z * vec.z >= this.speed * this.speed) {
							vec = new Vec3d(vec.x, 0d, vec.z).normalize().scale(this.speed).add(lastpos)
							 .addVector((this.rand.nextFloat()-0.5f) * 2f, vec.y, (this.rand.nextFloat()-0.5f) * 2f);
							BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
							Material material = this.world.getBlockState(pos.setPos(vec.x, vec.y, vec.z)).getMaterial();
							for (; !material.isSolid() && !material.isLiquid(); material = this.world.getBlockState(pos.move(EnumFacing.DOWN)).getMaterial());
							for (; material.isSolid() || material.isLiquid(); material = this.world.getBlockState(pos.move(EnumFacing.UP)).getMaterial());
							vec = new Vec3d(vec.x, (double)pos.getY() - 0.5d, vec.z);
							pos.release();
							Vec3d vec1 = targetpos.subtract(vec);
							ProcedureUtils.Vec2f vec2 = ProcedureUtils.getYawPitchFromVec(vec1);
							float yaw = vec2.x + (this.rand.nextFloat()-0.5f) * 30.0f;
							float pitch = this.rand.nextFloat() * 20.0f + 15.0f;
							float length = 4.56f + this.rand.nextFloat() * 1.825f;
							if (this.target != null && vec1.y > length && MathHelper.sqrt(vec1.x * vec1.x + vec1.z * vec1.z) < 3.5f) {
								yaw = vec2.x;
								pitch = 90.0f + vec2.y;
								length = Math.max((float)vec1.lengthVector() + 1.5f, length);
							}
							this.lastSpike = new EntityCrystalSpike(this.shooter, length);
							this.lastSpike.setLocationAndAngles(vec.x, vec.y, vec.z, yaw, pitch);
							this.world.spawnEntity(this.lastSpike);
						}
					}
				}
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
				Entity entity1;
				if (power == 1.0f) {
					entity1 = new EC(entity);
					entity.world.spawnEntity(entity1);
					entity.getEntityData().setInteger(ENTITYID_KEY, entity1.getEntityId());
					return true;
				} else {
				 	entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ENTITYID_KEY));
				 	if (entity1 instanceof EC) {
				 		++((EC)entity1).maxLife;
				 		return true;
				 	}
				}
			 	return false;
			}

			@Override
			public float getBasePower() {
				return 2.0f;
			}
		}
	}

	public static class EntityCrystalSpike extends EntitySpike.Base implements ItemJutsu.IJutsu {
		private final int growTime = 10;
		private float maxScale;
		private final float damage = 8.0f;
		private EntityLivingBase user;

		public EntityCrystalSpike(World worldIn) {
			super(worldIn);
			this.setColor(0xA0FFFFFF);
			this.isImmuneToFire = true;
		}

		public EntityCrystalSpike(EntityLivingBase userIn, float length) {
			this(userIn.world);
			this.user = userIn;
			this.maxScale = length / 1.825f;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.SHOTON;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.ticksAlive <= this.growTime) {
				if (this.ticksAlive == 1) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ice_shoot_small")),
					 1.0f, this.rand.nextFloat() * 0.4f + 0.8f);
				}
				this.setEntityScale(MathHelper.clamp(this.maxScale * (float)this.ticksAlive / this.growTime, 0.0f, this.maxScale));
				for (EntityLivingBase entity : 
				 this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(1d, 0d, 1d))) {
					if (!entity.equals(this.user)) {
						entity.hurtResistantTime = 10;
						//entity.getEntityData().setBoolean("TempData_disableKnockback", true);
						entity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.user),
						 this.damage * (1f - (float)(this.ticksAlive - 1) / this.growTime));
					}
				}
			}
		}

		@Override
		protected void checkOnGround() {
			this.onGround = true;
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCrystalSpike.class, renderManager -> new CustomRender(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends EntitySpike.ClientSide.Renderer<EntityCrystalSpike> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/crystal_blue.png");
	
			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn, new EntitySpike.ClientSide.ModelCrystal());
			}

			@Override
			protected void prepareScale(float scale) {
				float f = Math.min(scale, 2.5f);
				GlStateManager.scale(f, scale, f);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCrystalSpike entity) {
				return this.texture;
			}
		}
	}
}
