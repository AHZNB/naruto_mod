
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
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

	public static class EntityCustom extends EntityScalableProjectile.Base implements ItemJutsu.IJutsu {
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
		private final float shieldSize = 8f;
		private boolean shieldOn;
		private ItemStack heldItem;
		private float hp;
		private int deathTicks;
		private Entity target;
		private int targetTime = -1;
		private final float inititalScale = 0.8f;
		private float maxScale = inititalScale;
		private final int growTicks = 20;

		public EntityCustom(World world) {
			super(world);
			this.isImmuneToFire = true;
			this.setOGSize(0.25F, 0.25F);
			this.setEntityScale(0.01f);
			this.setNoGravity(true);
		}

		public EntityCustom(EntityLivingBase shooter, int posIndex, ItemStack helditem) {
			super(shooter);
			this.isImmuneToFire = true;
			this.heldItem = helditem;
			this.setOGSize(0.25F, 0.25F);
			this.setEntityScale(0.01f);
			this.idleVec = VEC[posIndex % VEC.length];
			Vec3d vec = this.getIdlePosition();
			this.setLocationAndAngles(vec.x, vec.y, vec.z, 0.0f, 0.0f);
			this.hp = 1000.0f;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.SENJUTSU;
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
				if (!this.world.isRemote && this.ticksAlive <= this.growTicks) {
					this.setEntityScale(0.01f + (this.inititalScale - 0.01f) * (float)this.ticksAlive / (float)this.growTicks);
				}
				if (!this.isLaunched()) {
					if (this.maxScale > this.inititalScale) {
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
				//this.maxScale = f;
			}
		}

		protected void eventOnTick(World world, int x, int y, int z, int radius, Entity entity, int tick) {
			//float damage = (radius >= 30) ? Float.MAX_VALUE : (radius * 5);
			float damage = radius * 10;
			ProcedureAoeCommand.set(world, x, y, z, 0.0D, radius).exclude(entity).exclude(EntityCustom.class)
			 .resetHurtResistanceTime().damageEntities(
			 ItemJutsu.causeSenjutsuDamage(this, this.shootingEntity).setDamageBypassesArmor(), damage);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntityCustom))
				return;
			if (!this.world.isRemote) {
				float radius = this.getEntityScale() * 4f + 15f;
				Particles.spawnParticle(this.world, Particles.Types.CONCENTRIC_SPHERES, this.posX, this.posY, this.posZ, 1,
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
			private final ModelRenderer hexadecagon;
			private final ModelRenderer hexadecagon_r1;
			private final ModelRenderer hexadecagon_r2;
			private final ModelRenderer hexadecagon_r3;
			private final ModelRenderer hexadecagon_r4;
			private final ModelRenderer hexadecagon6;
			private final ModelRenderer hexadecagon_r5;
			private final ModelRenderer hexadecagon_r6;
			private final ModelRenderer hexadecagon_r7;
			private final ModelRenderer hexadecagon_r8;
			private final ModelRenderer hexadecagon7;
			private final ModelRenderer hexadecagon_r9;
			private final ModelRenderer hexadecagon_r10;
			private final ModelRenderer hexadecagon_r11;
			private final ModelRenderer hexadecagon_r12;
			private final ModelRenderer hexadecagon8;
			private final ModelRenderer hexadecagon_r13;
			private final ModelRenderer hexadecagon_r14;
			private final ModelRenderer hexadecagon_r15;
			private final ModelRenderer hexadecagon_r16;
			private final ModelRenderer hexadecagon2;
			private final ModelRenderer hexadecagon_r17;
			private final ModelRenderer hexadecagon_r18;
			private final ModelRenderer hexadecagon_r19;
			private final ModelRenderer hexadecagon_r20;
			private final ModelRenderer hexadecagon3;
			private final ModelRenderer hexadecagon_r21;
			private final ModelRenderer hexadecagon_r22;
			private final ModelRenderer hexadecagon_r23;
			private final ModelRenderer hexadecagon_r24;
			private final ModelRenderer hexadecagon4;
			private final ModelRenderer hexadecagon_r25;
			private final ModelRenderer hexadecagon_r26;
			private final ModelRenderer hexadecagon_r27;
			private final ModelRenderer hexadecagon_r28;
			private final ModelRenderer hexadecagon5;
			private final ModelRenderer hexadecagon_r29;
			private final ModelRenderer hexadecagon_r30;
			private final ModelRenderer hexadecagon_r31;
			private final ModelRenderer hexadecagon_r32;
	
			public ModelTruthSeekerBall() {
				this.textureWidth = 16;
				this.textureHeight = 16;
				
				bb_main = new ModelRenderer(this);
				bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				hexadecagon = new ModelRenderer(this);
				hexadecagon.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon);
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
		
				hexadecagon_r1 = new ModelRenderer(this);
				hexadecagon_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r1);
				setRotationAngle(hexadecagon_r1, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r2 = new ModelRenderer(this);
				hexadecagon_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r2);
				setRotationAngle(hexadecagon_r2, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r3 = new ModelRenderer(this);
				hexadecagon_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r3);
				setRotationAngle(hexadecagon_r3, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r4 = new ModelRenderer(this);
				hexadecagon_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r4);
				setRotationAngle(hexadecagon_r4, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon6 = new ModelRenderer(this);
				hexadecagon6.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon6);
				setRotationAngle(hexadecagon6, 0.0F, 0.3927F, 0.0F);
				hexadecagon6.cubeList.add(new ModelBox(hexadecagon6, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
				hexadecagon6.cubeList.add(new ModelBox(hexadecagon6, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
		
				hexadecagon_r5 = new ModelRenderer(this);
				hexadecagon_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r5);
				setRotationAngle(hexadecagon_r5, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r6 = new ModelRenderer(this);
				hexadecagon_r6.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r6);
				setRotationAngle(hexadecagon_r6, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r7 = new ModelRenderer(this);
				hexadecagon_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r7);
				setRotationAngle(hexadecagon_r7, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r7.cubeList.add(new ModelBox(hexadecagon_r7, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r8 = new ModelRenderer(this);
				hexadecagon_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r8);
				setRotationAngle(hexadecagon_r8, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r8.cubeList.add(new ModelBox(hexadecagon_r8, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon7 = new ModelRenderer(this);
				hexadecagon7.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon7);
				setRotationAngle(hexadecagon7, 0.0F, 0.7854F, 0.0F);
				hexadecagon7.cubeList.add(new ModelBox(hexadecagon7, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
				hexadecagon7.cubeList.add(new ModelBox(hexadecagon7, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
		
				hexadecagon_r9 = new ModelRenderer(this);
				hexadecagon_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r9);
				setRotationAngle(hexadecagon_r9, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r10 = new ModelRenderer(this);
				hexadecagon_r10.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r10);
				setRotationAngle(hexadecagon_r10, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r11 = new ModelRenderer(this);
				hexadecagon_r11.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r11);
				setRotationAngle(hexadecagon_r11, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r11.cubeList.add(new ModelBox(hexadecagon_r11, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r12 = new ModelRenderer(this);
				hexadecagon_r12.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r12);
				setRotationAngle(hexadecagon_r12, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r12.cubeList.add(new ModelBox(hexadecagon_r12, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon8 = new ModelRenderer(this);
				hexadecagon8.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon8);
				setRotationAngle(hexadecagon8, 0.0F, 1.1781F, 0.0F);
				hexadecagon8.cubeList.add(new ModelBox(hexadecagon8, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
				hexadecagon8.cubeList.add(new ModelBox(hexadecagon8, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
		
				hexadecagon_r13 = new ModelRenderer(this);
				hexadecagon_r13.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r13);
				setRotationAngle(hexadecagon_r13, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r14 = new ModelRenderer(this);
				hexadecagon_r14.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r14);
				setRotationAngle(hexadecagon_r14, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r15 = new ModelRenderer(this);
				hexadecagon_r15.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r15);
				setRotationAngle(hexadecagon_r15, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r15.cubeList.add(new ModelBox(hexadecagon_r15, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r16 = new ModelRenderer(this);
				hexadecagon_r16.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r16);
				setRotationAngle(hexadecagon_r16, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r16.cubeList.add(new ModelBox(hexadecagon_r16, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon2 = new ModelRenderer(this);
				hexadecagon2.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon2);
				setRotationAngle(hexadecagon2, 0.0F, -0.3927F, 0.0F);
				hexadecagon2.cubeList.add(new ModelBox(hexadecagon2, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
				hexadecagon2.cubeList.add(new ModelBox(hexadecagon2, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
		
				hexadecagon_r17 = new ModelRenderer(this);
				hexadecagon_r17.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r17);
				setRotationAngle(hexadecagon_r17, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r18 = new ModelRenderer(this);
				hexadecagon_r18.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r18);
				setRotationAngle(hexadecagon_r18, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r19 = new ModelRenderer(this);
				hexadecagon_r19.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r19);
				setRotationAngle(hexadecagon_r19, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r19.cubeList.add(new ModelBox(hexadecagon_r19, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r20 = new ModelRenderer(this);
				hexadecagon_r20.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r20);
				setRotationAngle(hexadecagon_r20, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r20.cubeList.add(new ModelBox(hexadecagon_r20, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon3 = new ModelRenderer(this);
				hexadecagon3.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon3);
				setRotationAngle(hexadecagon3, 0.0F, -0.7854F, 0.0F);
				hexadecagon3.cubeList.add(new ModelBox(hexadecagon3, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
				hexadecagon3.cubeList.add(new ModelBox(hexadecagon3, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
		
				hexadecagon_r21 = new ModelRenderer(this);
				hexadecagon_r21.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r21);
				setRotationAngle(hexadecagon_r21, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r22 = new ModelRenderer(this);
				hexadecagon_r22.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r22);
				setRotationAngle(hexadecagon_r22, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r23 = new ModelRenderer(this);
				hexadecagon_r23.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r23);
				setRotationAngle(hexadecagon_r23, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r23.cubeList.add(new ModelBox(hexadecagon_r23, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r24 = new ModelRenderer(this);
				hexadecagon_r24.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r24);
				setRotationAngle(hexadecagon_r24, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r24.cubeList.add(new ModelBox(hexadecagon_r24, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon4 = new ModelRenderer(this);
				hexadecagon4.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon4);
				setRotationAngle(hexadecagon4, 0.0F, -1.1781F, 0.0F);
				hexadecagon4.cubeList.add(new ModelBox(hexadecagon4, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
				hexadecagon4.cubeList.add(new ModelBox(hexadecagon4, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
		
				hexadecagon_r25 = new ModelRenderer(this);
				hexadecagon_r25.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r25);
				setRotationAngle(hexadecagon_r25, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r26 = new ModelRenderer(this);
				hexadecagon_r26.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r26);
				setRotationAngle(hexadecagon_r26, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r27 = new ModelRenderer(this);
				hexadecagon_r27.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r27);
				setRotationAngle(hexadecagon_r27, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r27.cubeList.add(new ModelBox(hexadecagon_r27, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r28 = new ModelRenderer(this);
				hexadecagon_r28.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r28);
				setRotationAngle(hexadecagon_r28, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r28.cubeList.add(new ModelBox(hexadecagon_r28, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon5 = new ModelRenderer(this);
				hexadecagon5.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.addChild(hexadecagon5);
				setRotationAngle(hexadecagon5, 0.0F, -1.5708F, 0.0F);
				hexadecagon5.cubeList.add(new ModelBox(hexadecagon5, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
				hexadecagon5.cubeList.add(new ModelBox(hexadecagon5, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
		
				hexadecagon_r29 = new ModelRenderer(this);
				hexadecagon_r29.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r29);
				setRotationAngle(hexadecagon_r29, 0.0F, 0.0F, 0.3927F);
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r30 = new ModelRenderer(this);
				hexadecagon_r30.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r30);
				setRotationAngle(hexadecagon_r30, 0.0F, 0.0F, -0.3927F);
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, 4, 0, -2.5F, -0.5027F, -0.5F, 5, 1, 1, 0.0F, false));
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r31 = new ModelRenderer(this);
				hexadecagon_r31.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r31);
				setRotationAngle(hexadecagon_r31, 0.0F, 0.0F, 0.7854F);
				hexadecagon_r31.cubeList.add(new ModelBox(hexadecagon_r31, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
		
				hexadecagon_r32 = new ModelRenderer(this);
				hexadecagon_r32.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r32);
				setRotationAngle(hexadecagon_r32, 0.0F, 0.0F, -0.7854F);
				hexadecagon_r32.cubeList.add(new ModelBox(hexadecagon_r32, 0, 0, -0.5027F, -2.5F, -0.5F, 1, 5, 1, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				bb_main.render(f5);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
