
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.MoverType;

import net.narutomod.item.ItemJutsu;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.event.EventSphericalExplosion;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureGravityPower;

import java.util.List;
import java.util.Comparator;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.Map;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityChibakuTenseiBall extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 130;
	public static final int ENTITYID_RANGED = 131;

	public EntityChibakuTenseiBall(ElementsNarutomodMod instance) {
		super(instance, 375);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "chibaku_tensei_ball"), ENTITYID)
		 .name("chibaku_tensei_ball").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(Satellite.class)
		 .id(new ResourceLocation("narutomod", "chibaku_satellite"), ENTITYID_RANGED)
		 .name("chibaku_satellite").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new RenderCustom(renderManager);
		});
	}

	public static class EntityCustom extends EntityScalableProjectile.Base {
		private final float maxScale = 80f;
		private final int launchTime = 100;
		private final List<Entity> affectedEntities = Lists.newArrayList();
		private List<BlockPos> airBlocks;
		//private List<Entity> entityList;
		private List<BlockPos> blockList;
		private boolean maxSizeReached;
		private int dropTime;
		private double stationaryX;
		private double stationaryY;
		private double stationaryZ;

		public EntityCustom(World world) {
			super(world);
			this.setOGSize(0.25F, 0.25F);
		}

		public EntityCustom(EntityLivingBase shooter) {
			super(shooter);
			this.setOGSize(0.25F, 0.25F);
			this.setPosition(shooter.posX, shooter.posY + shooter.height + 0.5D, shooter.posZ);
			shooter.getEntityData().setBoolean("chibakutensei_active", true);
		}

		@Override
		public void shoot(double x, double y, double z, float speed, float inaccuracy) {
			this.setDead();
			List<? extends BlockPos> list = ProcedureUtils.getNonAirBlocks(this.world, this.getEntityBoundingBox().grow(1));
			if (!list.isEmpty() && this.shootingEntity != null) {
				this.world.spawnEntity(new Satellite(this.shootingEntity, list));
			}
		}

		public void createEntitiesAndBlocksList() {
			AxisAlignedBB aabb = this.getEntityBoundingBox().grow(100d);
			/*this.entityList = this.world.getEntitiesInAABBexcluding(this, aabb, new Predicate<Entity>() {
				public boolean apply(@Nullable Entity p_apply_1_) {
					return p_apply_1_ != null && !p_apply_1_.equals(EntityCustom.this.shootingEntity)
					 && (!(p_apply_1_ instanceof EntityPlayer) 
					  || !((EntityPlayer)p_apply_1_).isSpectator() && !((EntityPlayer)p_apply_1_).isCreative());
				}
			});
			this.entityList.sort(new ProcedureUtils.EntitySorter(this));*/
			this.blockList = ProcedureUtils.getNonAirBlocks(this.world, aabb, true);
			this.blockList.sort(new ProcedureUtils.BlockposSorter(new BlockPos(this)));
		}

		@Override
		public void setDead() {
			if (this.shootingEntity != null) {
				this.shootingEntity.getEntityData().setBoolean("chibakutensei_active", false);
			}
			this.resetAffectedEntityGravity();
			super.setDead();
		}

		private Vec3d getCenter() {
			AxisAlignedBB aabb = this.getEntityBoundingBox();
			return new Vec3d(aabb.minX + (aabb.maxX - aabb.minX) * 0.5D, aabb.minY + (aabb.maxY - aabb.minY) * 0.5D, 
			  aabb.minZ + (aabb.maxZ - aabb.minZ) * 0.5D);
		}

		private void doBlackholeThings() {
			//if (this.entityList != null && this.blockList != null) {
				/*if (!this.maxSizeReached && !this.entityList.isEmpty() && this.rand.nextInt(10) == 0) {
					Entity entity = this.entityList.get(0);
					//entity.setNoGravity(true);
					this.affectedEntities.add(entity);
					this.entityList.remove(0);
				}*/
				if (!this.maxSizeReached && this.rand.nextInt(10) == 0) {
					EntityLivingBase entity = (EntityLivingBase)ProcedureUtils.findNearestEntityWithinAABB(this.world,
					 EntityLivingBase.class, this.getEntityBoundingBox().grow(128d), this, new Predicate<EntityLivingBase>() {
						@Override
						public boolean apply(@Nullable EntityLivingBase e) {
							return ItemJutsu.canTarget(e) && !e.equals(EntityCustom.this.shootingEntity)
							 && (!(e instanceof EntityPlayer) || !((EntityPlayer)e).isCreative())
 							 && !EntityCustom.this.affectedEntities.contains(e);
						}
					 });
					 if (entity != null) {
					 	this.affectedEntities.add(entity);
					 }
				}
				if (!this.maxSizeReached && this.rand.nextInt(10) == 0) {
					for (EntityEarthBlocks.Base entity = null; entity == null && !this.blockList.isEmpty(); ) {
						BlockPos pos = this.blockList.get(0);
						this.blockList.remove(0);
						if (!world.isAirBlock(pos)) {
							entity = ProcedureGravityPower.dislodgeBlocks(this.world, pos, 6 + this.rand.nextInt(4));
							if (entity != null) {
								entity.motionY = 0.1d;
								this.affectedEntities.add(entity);
								Iterator<BlockPos> iter = this.blockList.iterator();
								while (iter.hasNext()) {
									BlockPos pos1 = iter.next();
									for (BlockPos pos2 : entity.getBlockposList()) {
										if (pos1.equals(pos2)) {
											iter.remove();
										}
									}
								}
							}
						}
					}
				}
				Iterator<Entity> iter1 = this.affectedEntities.iterator();
				while (iter1.hasNext()) {
					Entity ent = iter1.next();
					if (ItemJutsu.canTarget(ent)) {
						Vec3d vec = this.getCenter().subtract(ent.posX, ent.posY + ent.height/2, ent.posZ).normalize().scale(0.1d);
						ent.addVelocity(vec.x, vec.y, vec.z);
						ent.velocityChanged = true;
					} else {
						iter1.remove();
					}
				}
			//}
		}

		private void resetAffectedEntityGravity() {
			Iterator<Entity> iter = this.affectedEntities.iterator();
			while (iter.hasNext()) {
				Entity entity = iter.next();
				entity.setNoGravity(false);
				ProcedureUtils.setVelocity(entity, entity.motionX, entity.motionY - 0.1d, entity.motionZ);
				iter.remove();
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			//if (!this.world.isRemote && this.ticksAlive > this.launchTime
			// && (this.blockList == null || this.blockList.isEmpty()) && (this.entityList == null || this.entityList.isEmpty())) {
			//	this.setDead();
			//} else {
				if (this.ticksAlive < this.launchTime) {
					this.motionY += 0.03d;
					this.setEntityScale(Math.max(this.maxScale * 0.2f * this.ticksAlive / this.launchTime, 1f));
				} else if (!this.maxSizeReached || this.dropTime > 0) {
					this.motionX = 0d;
					this.motionY = 0d;
					this.motionZ = 0d;
					if (this.ticksAlive == this.launchTime) {
						this.stationaryX = this.posX;
						this.stationaryY = this.posY;
						this.stationaryZ = this.posZ;
					} else {
						this.setPosition(this.stationaryX, this.stationaryY, this.stationaryZ);
					}
					if (!this.world.isRemote) {
						if (this.blockList == null) {
							this.createEntitiesAndBlocksList();
							this.setEntityScale(this.maxScale);
						} else {
							this.doBlackholeThings();
							this.collideWithNearbyEntities();
						}
					}
				} else {
					this.shoot(0, 0, 0, 0, 0);
				}
				if (this.dropTime > 0) {
					--this.dropTime;
				}
				this.motionY *= 0.98d;
			//}
		}

		protected void collideWithNearbyEntities() {
			for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(6d))) {
				if (entity.getEntityBoundingBox().intersects(this.getEntityBoundingBox())) {
					Vec3d vec = this.getCenter();
					if (!(entity instanceof EntityLivingBase) || entity.getDistance(vec.x, vec.y, vec.z) <= this.width / 2) {
						this.applyEntityCollision(entity);
					}
				}
			}
		}

		@Override
		public void applyEntityCollision(Entity entity) {
			if (!this.world.isRemote) {
				if (entity instanceof EntityEarthBlocks.Base) {
					Vec3d vec = this.getCenter();
					if (this.airBlocks == null) {
						this.airBlocks = ProcedureUtils.getAllAirBlocks(this.world,
						 this.getEntityBoundingBox().grow(this.maxRadius() - this.width / 2).offset(0d, 1d, 0d));
						this.airBlocks.sort(new ProcedureUtils.BlockposSorter(new BlockPos(vec)));
					}
					if (!this.airBlocks.isEmpty()) {
						Map<Vec3d, IBlockState> map = ((EntityEarthBlocks.Base)entity).getBlocksMap();
						List<IBlockState> list = Lists.newArrayList(map.values());
						Iterator<BlockPos> iter = this.airBlocks.iterator();
						for (int i = 0; iter.hasNext() && i < list.size(); i++) {
							BlockPos pos = iter.next();
							this.world.setBlockState(pos, list.get(i), 3);
							iter.remove();
						}
						map.clear();
						entity.setDead();
						if (!this.maxSizeReached && this.airBlocks.get(0).distanceSqToCenter(vec.x, vec.y, vec.z) >= this.width * this.width / 4) {
							this.maxSizeReached = true;
							this.dropTime = 30;
						}
					}
				} else {
					entity.attackEntityFrom(DamageSource.FLY_INTO_WALL, 10f);
					if (entity instanceof EntityLivingBase) {
						((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2, 3, false, false));
					}
				}
			}
		}

		private double maxRadius() {
			AxisAlignedBB aabb = this.getEntityBoundingBox();
			double d0 = (aabb.maxX - aabb.minX) / 2;
			double d1 = (aabb.maxY - aabb.minY) / 2;
			double d2 = (aabb.maxZ - aabb.minZ) / 2;
			return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
		}

		@Override
		protected void checkOnGround() {
		}
	}

	public static class Satellite extends EntityEarthBlocks.Base {
		private EntityLivingBase summoner;
		private boolean explosionSet;

		public Satellite(World world) {
			super(world);
		}

		public Satellite(EntityLivingBase summonerIn, List<? extends BlockPos> list) {
			super(summonerIn.world, list);
			this.summoner = summonerIn;
			this.setNoGravity(true);
			this.motionY = -0.1d;
			this.setFallTime(1200);
		}

		@Override
		protected void onImpact(float impact) {
			if (!this.world.isRemote) {
				if (!this.explosionSet && this.getTicksAlive() - this.fallTicks <= 1200) {
					if (this.summoner != null) {
						this.summoner.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 300d);
					}
					new EventSphericalExplosion(this.world, this.summoner, (int)this.posX, (int)this.posY, (int)this.posZ, 60, 0, 0.3f) {
						protected void doOnTick(int currentTick) {
							ProcedureAoeCommand.set(getWorld(), getX0(), getY0(), getZ0(), 0d, getRadius())
							 .exclude(getEntity()).damageEntities(DamageSource.FALLING_BLOCK, (float)getRadius());
						}
					};
					this.explosionSet = true;
					this.explodeOnImpact(true);
				}
			}
			super.onImpact(impact);
		}

		@Override
		public boolean griefingAllowed() {
			return this.getTicksAlive() == 1 ? true : super.griefingAllowed();
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EntityCustom> {
		protected ModelBase mainModel;

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn);
			this.mainModel = new ModelBall();
		}

		@Override
		public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
			this.bindEntityTexture(entity);
			GlStateManager.pushMatrix();
			GlStateManager.disableCull();
			float scale = entity.getEntityScale();
			GlStateManager.translate(x, y + (0.125F * scale), z);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(entity.ticksExisted * 10.0F, 1.0F, 1.0F, 0.0F);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			this.mainModel.render(entity, 0.0F, 0.0F, partialTicks + entity.ticksExisted, 0.0F, 0.0F, 0.0625F);
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return new ResourceLocation(entity.getEntityScale() > entity.maxScale * 0.4f 
			  ? "narutomod:textures/blank.png" : "narutomod:textures/truthhseekerball.png");
		} // meteor2
	}

	@SideOnly(Side.CLIENT)
	public class ModelBall extends ModelBase {
		private final ModelRenderer bb_main;

		public ModelBall() {
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
