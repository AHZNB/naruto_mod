
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

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
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.Lists;
import com.google.common.base.Predicate;

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
					this.setEntityScale(Math.max(this.maxScale * 0.1f * this.ticksAlive / this.launchTime, 1f));
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
			private final ResourceLocation blank_tex = new ResourceLocation("narutomod:textures/blank.png");
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
				return entity.getEntityScale() > entity.maxScale * 0.4f ? this.blank_tex : this.texture;
			} // meteor2
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
