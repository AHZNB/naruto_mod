
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.event.EventSphericalExplosion;
import net.narutomod.Particles;
import net.narutomod.NarutomodMod;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTruthSeekerBall extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 107;
	public static final int ENTITYID_RANGED = 108;

	public EntityTruthSeekerBall(ElementsNarutomodMod instance) {
		super(instance, 321);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "truthseekerball"), ENTITYID).name("truthseekerball").tracker(96, 3, true).build());
	}

	public static class EntityCustom extends EntityScalableProjectile.Base {
		private static final Vec3d[] VEC = {
			new Vec3d(0.0d, 2.0387d, -0.4395d), new Vec3d(-0.4102d, 1.7629d, -0.4395d), 
			new Vec3d(0.4102d, 1.7629d, -0.4395d), new Vec3d(-0.5859d, 1.3113d, -0.4395d), 
			new Vec3d(0.5859d, 1.3113d, -0.4395d), new Vec3d(-0.5273d, 0.8012d, -0.4395d), 
			new Vec3d(0.5273d, 0.8012d, -0.4395d), new Vec3d(-0.2344d, 0.4082d, -0.4395d),
			new Vec3d(0.2344d, 0.4082d, -0.4395d), new Vec3d(0.0d, 3.5d, 0.0d)
		};
		private static final DataParameter<Boolean> SHIELD_ON = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private Vec3d idleVec = VEC[0];
		protected boolean follow = true;
		private float shieldProgress;
		private int shieldToggleInProgressDirection;
		private float shieldSize = 8f;
		private boolean shieldOn;
		private ItemStack heldItem;
		private float hp;
		private int deathTicks;
		private Entity target;
		private int targetTime = -1;
		private final float inititalScale = 0.8f;
		private float maxScale = inititalScale;

		public EntityCustom(World world) {
			super(world);
			this.isImmuneToFire = true;
			this.setOGSize(0.25F, 0.25F);
			this.setEntityScale(this.inititalScale);
			this.setNoGravity(true);
		}

		public EntityCustom(EntityLivingBase shooter, int posIndex, ItemStack helditem) {
			super(shooter);
			this.isImmuneToFire = true;
			this.heldItem = helditem;
			this.setOGSize(0.25F, 0.25F);
			this.setEntityScale(this.inititalScale);
			this.idleVec = VEC[posIndex % VEC.length];
			Vec3d vec = this.getIdlePosition();
			this.setLocationAndAngles(vec.x, vec.y, vec.z, 0.0f, 0.0f);
			this.hp = 1000.0f;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SHIELD_ON, Boolean.valueOf(false));
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				((WorldServer)this.world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this. posX, this. posY + this.height/2, this. posZ,
				 (int)(this.width * 100), 0.5d * this.width, 0.5d * this.height, 0.5d * this.width, 0.0d);
				if (this.shootingEntity != null) {
					this.resetFlySpeed(this.shootingEntity);
				}
			}
		}

		private void slowFlySpeed(Entity entity) {
			if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.getFlySpeed() > 0.01f) {
				ReflectionHelper.setPrivateValue(PlayerCapabilities.class, ((EntityPlayer)entity).capabilities, 0.01f, 5);
				((EntityPlayer)entity).sendPlayerAbilities();
			}
		}

		private void resetFlySpeed(Entity entity) {
			if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.getFlySpeed() < 0.05f) {
				ReflectionHelper.setPrivateValue(PlayerCapabilities.class, ((EntityPlayer)entity).capabilities, 0.05f, 5);
				((EntityPlayer)entity).sendPlayerAbilities();
			}
		}

		@Override
		public boolean isImmuneToExplosions() {
			return true;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.world.isRemote && !this.getIsInvulnerable()) {
				if (ItemJutsu.isDamageSourceNinjutsu(source)) {
					Entity entity = source.getImmediateSource();
					if (entity != null && !(entity instanceof EntityLivingBase)) {
						entity.setDead();
					}
				} else if (source.getTrueSource() == null || !source.getTrueSource().equals(this.shootingEntity)) {
					if (this.hurtResistantTime > 10 || (!this.isLauchedAtTarget() && !this.isShieldOn())) {
						return false;
					}
					this.hp -= amount;
					this.hurtResistantTime = 20;
					if (this.hp <= 0.0f) {
						this.onDeath();
					}
					return true;
				}
			}
			return false;
		}

		public float getHealth() {
			return this.hp;
		}

		public void onDeath() {
			++this.deathTicks;
			this.setEntityScale(this.getEntityScale() * 0.9f);
			if (this.deathTicks > 5) {
				this.setDead();
			}
		}

		private Vec3d getIdlePosition() {
			if (this.shootingEntity != null) {
				if (this.isShieldOn()) {
					double y = this.shootingEntity.height + this.shieldProgress * -1.8f;
					return this.shootingEntity.getPositionVector().addVector(0d, y, 0d);
				} else {
					return this.idleVec.rotateYaw(-this.shootingEntity.renderYawOffset * 0.017453292F)
					 .add(this.shootingEntity.getPositionVector());
				}
			}
			return this.getPositionVector();
		}

		public void setNextPosition(Vec3d vec, double speed) {
			if (this.getDistance(vec.x, vec.y, vec.z) > speed && this.targetTime >= 0) {
				this.setVelocity(vec.subtract(this.getPositionVector()).normalize().scale(speed));
			} else {
				this.setVelocity(vec.subtract(this.getPositionVector()));
				if (this.targetTime >= 0 && vec.equals(this.getIdlePosition())) {
					this.setTarget(null, 0);
				}
			}
		}

		public void setTarget(@Nullable Entity targetIn, int time) {
			this.target = targetIn;
			this.targetTime = targetIn != null ? time : -1;
		}

		@Nullable
		public Entity getTarget() {
			return this.target;
		}

		public boolean isLauchedAtTarget() {
			return this.target != null && this.targetTime > 0;
		}

		public void setMaxScale(float scale) {
			this.maxScale = scale;
		}

		private void moveGrowAndShoot() {
			if (this.shootingEntity != null) {
				Vec3d vec = this.shootingEntity.getPositionVector().addVector(0d, this.shootingEntity.height + 2.0f, 0d);
				if (this.getDistance(vec.x, vec.y, vec.z) > 0.2d) {
					this.setVelocity(vec.subtract(this.getPositionVector()).normalize().scale(0.1d));
				} else if (this.maxScale > 0) {
					this.setVelocity(Vec3d.ZERO);
					float scale = this.getEntityScale();
					if (scale < this.maxScale) {
						this.setEntityScale(scale * 1.03f);
					} else {
						Vec3d vec2 = this.shootingEntity.getLookVec();
						this.shoot(vec2.x, vec2.y, vec2.z, 0.95f, 0f);
						this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(
						 "narutomod:Kaguya_FinalTSB")), 5.0F, 1.0F);
					}
				} else {
					this.setDead();
				}
			}
		}

		@Override
		public void shoot(double x, double y, double z, float speed, float inaccuracy) {
			super.shoot(x, y, z, speed, inaccuracy);
			this.targetTime = this.maxScale <= this.shieldSize ? (int)MathHelper.sqrt(1600.0d / speed) : 200;
		}

		private void setVelocity(Vec3d vec) {
			this.motionX = vec.x;
			this.motionY = vec.y;
			this.motionZ = vec.z;
			this.isAirBorne = true;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.deathTicks > 0) {
				this.onDeath();
			}
			this.updateShieldProgress();
			if (!this.isDead && this.follow && this.shootingEntity != null) {
				if (!this.isLaunched()) {
					if (this.maxScale != this.getEntityScale()) {
						this.moveGrowAndShoot();
					} else if (this.target != null && this.targetTime > 0) {
						if (this.target.isEntityAlive()) {
							this.setNextPosition(this.target.getPositionEyes(1f), 1.5d);
							--this.targetTime;
						} else {
							this.targetTime = 0;
						}
					} else if (this.getDistance(this.shootingEntity) < 70.0d) {
						this.setNextPosition(this.getIdlePosition(), 0.5d);
					}
					if (this.isShieldOn()) {
						this.shootingEntity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 2d);
					}
					if (this.target != null) {
						for (Entity entity : this.world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(0.1d))) {
							if (!entity.equals(this.shootingEntity) && !entity.equals(this)) {
								this.applyEntityCollision(entity);
							}
						}
					}
				} else {
					if (this.targetTime > 0) {
						--this.targetTime;
					} else {
						this.haltMotion();
					}
				}
			}
			if (!this.world.isRemote && (this.shootingEntity == null || !this.shootingEntity.isEntityAlive()
			 || (this.heldItem != null && (this.heldItem.isEmpty() || 
			  (!this.shootingEntity.getHeldItemMainhand().equals(this.heldItem)
			  && !this.shootingEntity.getHeldItemOffhand().equals(this.heldItem)))))) {
				this.setDead();
			}
			if (this.hurtResistantTime > 0) {
				--this.hurtResistantTime;
			}
		}

		public void toggleShield() {
			if (this.isShieldOn()) {
				this.shieldToggleInProgressDirection = -1;
			} else {
				this.shieldToggleInProgressDirection = 1;
				this.getDataManager().set(SHIELD_ON, Boolean.valueOf(true));
				this.slowFlySpeed(this.shootingEntity);
			}
		}

		public boolean isShieldOn() {
			//return this.shieldOn;
			return ((Boolean)this.getDataManager().get(SHIELD_ON)).booleanValue();
		}

		private void updateShieldProgress() {
			if (this.shieldToggleInProgressDirection != 0) {
				this.shieldProgress += 0.05f * this.shieldToggleInProgressDirection;
				if (this.shieldProgress >= 1.0f) {
					this.shieldProgress = 1.0f;
					this.shieldToggleInProgressDirection = 0;
				}
				if (this.shieldProgress <= 0.0f) {
					this.shieldProgress = 0.0f;
					this.shieldToggleInProgressDirection = 0;
					//this.shieldOn = false;
					this.getDataManager().set(SHIELD_ON, Boolean.valueOf(false));
					this.resetFlySpeed(this.shootingEntity);
				}
				float f = 1f + this.shieldProgress * this.shieldSize;
				this.setEntityScale(f);
				this.maxScale = f;
			}
		}

		protected void eventOnTick(World world, int x, int y, int z, int radius, Entity entity, int tick) {
			//float damage = (radius >= 30) ? Float.MAX_VALUE : (radius * 5);
			float damage = radius * 10;
			ProcedureAoeCommand.set(world, x, y, z, 0.0D, radius).exclude(entity).exclude(EntityCustom.class)
			 .resetHurtResistanceTime().damageEntities(DamageSource.GENERIC.setDamageIsAbsolute(), damage);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntityCustom))
				return;
			if (!this.world.isRemote) {
				float radius = this.getEntityScale() * 4f + 15f;
				Particles.spawnParticle(this.world, Particles.Types.EXPANDING_SPHERE, this.posX, this.posY, this.posZ, 1,
				 0d, 0d, 0d, 0d, 0d, 0d, 192d, (int)(radius * 10f), (int)(radius * 4), 0);
				//EntitySpecialEffect.spawn(this.world, EntitySpecialEffect.Type.EXPANDING_SPHERES_FADE_TO_BLACK,
				// radius, (int)(radius * 2), this.posX, this.posY, this.posZ);
				new EventSphericalExplosion(this.world, this.shootingEntity, (int) this.posX, (int) this.posY,
				 (int) this.posZ, (int) radius, 0, false, 0.0f, false, true) {
					protected void doOnTick(int currentTick) {
						eventOnTick(getWorld(), getX0(), getY0(), getZ0(), getRadius(), getEntity(), currentTick);
					}
				};
				if (this.getEntityScale() >= 25.0F && result.entityHit instanceof EntityLivingBase) {
					((EntityLivingBase)result.entityHit).setHealth(0.0F);
				}
				if (this.maxScale > this.shieldSize) {
					this.onDeath();
				} else {
					this.targetTime = 0;
					this.haltMotion();
				}
			}
		}

		@Override
		public void renderParticles() {
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public boolean canBePushed() {
			//return this.shieldOn;
			return this.isShieldOn();
		}

		@Override
		@Nullable
		public AxisAlignedBB getCollisionBoundingBox() {
			return this.isShieldOn() ? this.getEntityBoundingBox() : null;
		}

	    @Override
	    public void applyEntityCollision(Entity entityIn) {
	        if (!this.isRidingSameEntity(entityIn) && !entityIn.noClip && !entityIn.isBeingRidden()) {
	            double d0 = entityIn.posX - this.posX;
	            double d1 = entityIn.posZ - this.posZ;
                entityIn.addVelocity(d0 * 0.15D, 0.0D, d1 * 0.15D);
                if (this.target != null) {
					entityIn.hurtResistantTime = 10;
	                if (entityIn instanceof EntityLivingBase) {
						entityIn.attackEntityFrom(ItemJutsu.causeSenjutsuDamage(this, this.shootingEntity), 10.0f);
	                } else if (!(entityIn instanceof EntityCustom)
	                 || (this.shootingEntity != null && !this.shootingEntity.equals(((EntityCustom)entityIn).shootingEntity))) {
	                	entityIn.onKillCommand();
	                }
                }
	        }
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/truthhseekerball.png");
			protected ModelBase mainModel;
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.mainModel = new ModelTruthSeekerBall();
			}
	
			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				this.bindEntityTexture(entity);			
				GlStateManager.pushMatrix();
				GlStateManager.disableCull();
				float scale = entity.getEntityScale();
				GlStateManager.translate(x, y + (0.125F * scale), z);
				GlStateManager.scale(scale, scale, scale);
				if (!entity.isShieldOn()) {
					GlStateManager.rotate(((float)entity.ticksExisted + partialTicks) * 90.0F, 1.0F, 1.0F, 0.0F);
				}
				GlStateManager.disableLighting();
				//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				this.mainModel.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.popMatrix();
				
				//super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelTruthSeekerBall extends ModelBase {
			private final ModelRenderer bb_main;
	
			public ModelTruthSeekerBall() {
				this.textureWidth = 16;
				this.textureHeight = 16;
				this.bb_main = new ModelRenderer(this);
				this.bb_main.setRotationPoint(0.0F, 2.0F, 0.0F);
				this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
			}
	
			@Override
			public void render(Entity entityIn, float f0, float f1, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				this.bb_main.render(scale);
			}
		}
	}
}
