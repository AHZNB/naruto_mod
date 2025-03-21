
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemJiton;
import net.narutomod.potion.PotionParalysis;
import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPaperBind extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 486;
	public static final int ENTITYID_RANGED = 487;
	private static final String slownessAmp = "paperbindSlownessAmplitude";

	public EntityPaperBind(ElementsNarutomodMod instance) {
		super(instance, 910);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "paper_bind"), ENTITYID).name("paper_bind").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityPaper.class)
		 .id(new ResourceLocation("narutomod", "paper_tag"), ENTITYID_RANGED).name("paper_tag").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> TARGET_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private EntityLivingBase user;
		private EntityLivingBase targetEntity;
		private ItemJiton.SwarmTarget swarmTarget;
		private ProcedureSync.PositionRotationPacket capturedPRP;
		private static final int MAXTIME = 600;

		public EC(World world) {
			super(world);
			this.setSize(0.2f, 0.2f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, EntityLivingBase targetIn) {
			this(userIn.world);
			this.user = userIn;
			this.targetEntity = targetIn;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
			this.swarmTarget = new ItemJiton.SwarmTarget<EntityPaper>(this.world, 250, userIn.getEntityBoundingBox(),
			 targetIn.getEntityBoundingBox(), new Vec3d(0.5d, 0.0d, 0.5d), 0.6f, 0.05f, false, 1f, -1) {
				@Override
				protected EntityPaper createParticle(double x, double y, double z, double mx, double my, double mz, int c, float sc, int life) {
					return new EntityPaper(userIn, x, y, z, mx, my, mz);
				}
				@Override
				protected void playFlyingSound(double x, double y, double z, float volume, float pitch) {
					if (this.getTicks() % 2 == 0) {
						userIn.world.playSound(null, x, y, z, net.minecraft.util.SoundEvent.REGISTRY
						 .getObject(new ResourceLocation("narutomod:paperflip")),
						 net.minecraft.util.SoundCategory.BLOCKS, volume, pitch);
					}
				}
			};
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.NINJUTSU;
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(TARGET_ID, Integer.valueOf(-1));
		}

		@Nullable
		public EntityLivingBase getTarget() {
	    	Entity e = this.world.getEntityByID(((Integer)this.getDataManager().get(TARGET_ID)).intValue());
	    	return e instanceof EntityLivingBase ? (EntityLivingBase)e : null;
		}

		protected void setTarget(@Nullable EntityLivingBase entity) {
			this.getDataManager().set(TARGET_ID, Integer.valueOf(entity == null ? -1 : entity.getEntityId()));
		}

		private Vec3d getUserVector() {
			return this.user.getPositionVector().addVector(0d, this.user.height * 0.6667f, 0d);
		}

		private boolean isTargetCaptured() {
			boolean flag = this.capturedPRP != null;
			if (!flag && this.getEntityBoundingBox().intersects(this.targetEntity.getEntityBoundingBox())) {
				AxisAlignedBB bb = this.getEntityBoundingBox().intersect(this.targetEntity.getEntityBoundingBox());
				flag = bb.equals(this.targetEntity.getEntityBoundingBox())
				 && this.getEntityBoundingBox().getAverageEdgeLength() < this.targetEntity.getEntityBoundingBox().getAverageEdgeLength() * 2.5d;
			}
			if (flag && this.capturedPRP == null) {
				this.capturedPRP = new ProcedureSync.PositionRotationPacket(this.targetEntity);
			} else if (!flag) {
				this.capturedPRP = null;
			}
			return flag;
		}

		private void holdTarget() {
			this.capturedPRP.setPositionAndUpdate(this.targetEntity);
			if (this.targetEntity instanceof EntityPlayer) {
				ProcedureOnLivingUpdate.disableMouseClicks((EntityPlayer)this.targetEntity, 2);
			} else if (this.targetEntity instanceof EntityLiving) {
				ProcedureOnLivingUpdate.disableAIfor((EntityLiving)this.targetEntity, 2);
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				if (this.targetEntity != null) {
					this.targetEntity.getEntityData().removeTag(slownessAmp);
				}
				if (this.swarmTarget == null || this.swarmTarget.shouldRemove()) {
					Particles.Renderer particles = new Particles.Renderer(this.world);
					for (int i = 0; i < 200; i++) {
						Vec3d vec = ProcedureUtils.BB.randomPosOnBB(this.getEntityBoundingBox());
						particles.spawnParticles(Particles.Types.PAPER, vec.x, vec.y, vec.z, 1, 0, 0, 0,
						 (this.rand.nextFloat()-0.5f) * 0.1f, 0.0f, (this.rand.nextFloat()-0.5f) * 0.1f);
					}
					particles.send();
				}
			}
		}

		@Override
		public void onUpdate() {
			if (this.user != null && this.user.isEntityAlive() && ItemJutsu.canTarget(this.targetEntity) && this.ticksExisted < MAXTIME) {
			 	boolean swarmactive = this.swarmTarget != null && !this.swarmTarget.shouldRemove();
				if (this.isTargetCaptured()) {
					this.holdTarget();
					if (swarmactive) {
						if (this.getEntityBoundingBox().getAverageEdgeLength() > this.targetEntity.getEntityBoundingBox().getAverageEdgeLength() * 1.2d) {
							this.swarmTarget.setTarget(this.targetEntity.getEntityBoundingBox(), 0.3f, 0.0f, false);
						} else {
							this.swarmTarget.forceRemove();
							this.setTarget(this.targetEntity);
							this.setEntityBoundingBox(this.targetEntity.getEntityBoundingBox().grow(0.05d));
						}
					} else {
						this.targetEntity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.user).setDamageBypassesArmor(), 4.0f);
					}
				} else if (swarmactive && this.targetEntity != null) {
					this.swarmTarget.setTarget(this.targetEntity.getEntityBoundingBox(), 2.5f, 0.03f, false);
				}
				if (swarmactive) {
					this.swarmTarget.setStartBB(this.user.getEntityBoundingBox());
					this.swarmTarget.onUpdate();
					this.setEntityBoundingBox(this.swarmTarget.getBorders());
				}
				this.resetPositionToBB();
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		@Override
		public void setEntityBoundingBox(AxisAlignedBB bb) {
			super.setEntityBoundingBox(bb);
			this.width = (float)Math.min(bb.maxX - bb.minX, bb.maxZ - bb.minZ);
			this.height = (float)(bb.maxY - bb.minY);
		}

		@Override
		public void resetPositionToBB() {
			super.resetPositionToBB();
			if (!this.world.isRemote && this.isAddedToWorld()) {
				ProcedureSync.ResetBoundingBox.sendToTracking(this);
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
				RayTraceResult result = ProcedureUtils.objectEntityLookingAt(entity, 30d, 3d, true, true, EntityShikigami.EC.class);
				if (result != null) {
					return this.createJutsu(entity, result.entityHit) != null;
				}
				return false;
			}

			@Nullable
			public static EC createJutsu(EntityLivingBase entity, @Nullable Entity target) {
				if (target != null) {
					if (target instanceof EC && entity.equals(((EC)target).user)) {
						target.ticksExisted = EC.MAXTIME;
						return null;
					}
					if (target instanceof EntityLivingBase) {
						for (EC ec : entity.world.getEntitiesWithinAABB(EC.class, entity.getEntityBoundingBox().grow(30d))) {
							if (target.equals(ec.targetEntity) && entity.equals(ec.user)) {
								return null;
							}
						}
						EC entity1 = new EC(entity, (EntityLivingBase)target);
						entity.world.spawnEntity(entity1);
						return entity1;
					}
				}
				return null;
			}
		}
	}

	public static class EntityPaper extends Entity implements ItemJiton.ISwarmEntity {
		private EntityLivingBase host;
		private int maxAge;
		public float prevRotationRoll;
		public float rotationRoll;
		private int lastUpdateTime;
		private int deathTicks;

		public EntityPaper(World worldIn) {
			super(worldIn);
			this.setSize(0.25f, 0.25f);
			this.maxAge = 1000;
		}

		public EntityPaper(EntityLivingBase hostIn, double x, double y, double z, double mX, double mY, double mZ) {
			this(hostIn.world);
			this.host = hostIn;
			this.setPosition(x, y, z);
			this.motionX = mX;
			this.motionY = mY;
			this.motionZ = mZ;
			float f1 = MathHelper.sqrt(mX * mX + mZ * mZ);
			this.rotationYaw = (float) (-MathHelper.atan2(mX, mZ) * (180d / Math.PI));
			this.rotationPitch = (float) (-MathHelper.atan2(mY, f1) * (180d / Math.PI));
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}

		/*@Override
		public boolean canBePushed() {
			return !this.isDead;
		}

		@Override
		public void applyEntityCollision(Entity entityIn) {
		}*/

		public void updateInFlightRotations() {
            double d = (double)MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            float yaw = -(float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            float pitch = -(float)(MathHelper.atan2(this.motionY, d) * (180D / Math.PI));
            float deltaYaw = ProcedureUtils.subtractDegreesWrap(yaw, this.rotationYaw);
            float roll = deltaYaw * (float)d * 30f;
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationRoll = this.rotationRoll;
            this.rotationYaw = yaw;
            this.rotationPitch = pitch;
            this.rotationRoll = roll;
		}

		@Override
		public void onUpdate() {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.updateInFlightRotations();
			this.motionX *= 0.8D;
			this.motionY *= 0.8D;
			this.motionZ *= 0.8D;
			if (!this.world.isRemote) {
				if (this.deathTicks > 0 || this.ticksExisted > this.maxAge || this.host == null) {
					this.onDeath();
				} else {
					for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox())) {
						if (!entity.equals(this.host)) {
							float ampf = entity.getEntityData().getFloat(slownessAmp);
							PotionEffect effect = entity.getActivePotionEffect(PotionHeaviness.potion);
							if (effect == null) {
								ampf = 0.0f;
								entity.addPotionEffect(new PotionEffect(PotionHeaviness.potion, 60, 0, false, false));
							} else if (effect.getAmplifier() < (int)ampf) {
								if (effect.getAmplifier() < (int)ampf - 1) {
									ampf = effect.getAmplifier();
								}
								entity.addPotionEffect(new PotionEffect(PotionHeaviness.potion, 60, Math.min((int)ampf, 5), false, false));
							}
							entity.getEntityData().setFloat(slownessAmp, ampf + 0.01f);
						}
					}
					if (this.ticksExisted > this.lastUpdateTime + 20) {
						this.maxAge = this.ticksExisted;
					}
				}
			}
		}

		public void onDeath() {
			if (++this.deathTicks > 100 && !this.world.isRemote) {
				this.setDead();
			}
			this.motionY -= 0.01d;
		}

		@Override
		public void setLastUpdateTime() {
			this.lastUpdateTime = this.ticksExisted;
		}

		@Override
		protected void playStepSound(net.minecraft.util.math.BlockPos pos, net.minecraft.block.Block blockIn) {
		}

		public double getVelocity() {
			return ProcedureUtils.getVelocity(this);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean isInRangeToRenderDist(double distance) {
			return distance < 4096.0d;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			this.ticksExisted = compound.getInteger("age");
			this.maxAge = compound.getInteger("maxAge");
			this.deathTicks = compound.getInteger("deathTicks");
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setInteger("age", this.ticksExisted);
			compound.setInteger("maxAge", this.maxAge);
			compound.setInteger("deathTicks", this.deathTicks);
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderEC(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntityPaper.class, renderManager -> new RenderPaper(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderEC extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/paper_wrap.png");

			public RenderEC(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
			
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float pt) {
				EntityLivingBase target = entity.getTarget();
				if (target != null) {
					Render renderer = this.renderManager.getEntityRenderObject(target);
					if (renderer instanceof RenderLivingBase) {
						ModelBase model = ((RenderLivingBase)renderer).getMainModel();
						float f = (float)target.ticksExisted + pt;
			            float f1 = ProcedureUtils.interpolateRotation(target.prevRenderYawOffset, target.renderYawOffset, pt);
			            float f2 = ProcedureUtils.interpolateRotation(target.prevRotationYawHead, target.rotationYawHead, pt);
			            float f3 = f2 - f1;
		                float f5 = target.prevLimbSwingAmount + (target.limbSwingAmount - target.prevLimbSwingAmount) * pt;
		                float f6 = target.limbSwing - target.limbSwingAmount * (1.0F - pt);
			            float f7 = target.prevRotationPitch + (target.rotationPitch - target.prevRotationPitch) * pt;
						x = target.lastTickPosX + (target.posX - target.lastTickPosX) * pt - this.renderManager.viewerPosX;
						y = target.lastTickPosY + (target.posY - target.lastTickPosY) * pt - this.renderManager.viewerPosY;
						z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * pt - this.renderManager.viewerPosZ;
						this.bindEntityTexture(entity);
						if (target.isSneaking()) {
							y -= 0.125F;
						}
						float scale = 1.05F;
						GlStateManager.pushMatrix();
						GlStateManager.translate(x, y + 0.05, z);
						GlStateManager.rotate(180F - f1, 0.0F, 1.0F, 0.0F);
						float f4 = ((RenderLivingBase)renderer).prepareScale(target, pt);
						GlStateManager.scale(scale, scale, scale);
						model.render(target, f6, f5, f, f3, f7, f4);
			            GlStateManager.popMatrix();
					}
				}
			}
			
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderPaper extends Render<EntityPaper> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/paper_white.png");
			protected final ModelPaper model;
	
			public RenderPaper(RenderManager renderManager) {
				super(renderManager);
				this.model = new ModelPaper();
			}
	
			@Override
			public void doRender(EntityPaper entity, double x, double y, double z, float entityYaw, float partialTicks) {
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);
				GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(-entity.prevRotationRoll - (entity.rotationRoll - entity.prevRotationRoll) * partialTicks, 0.0F, 0.0F, 1.0F);
				this.model.render(entity, 0.0F, 0.0F, partialTicks + entity.ticksExisted, 0.0F, 0.0F, 0.0625F);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityPaper entity) {
				return this.texture;
			}
		}
	
		// Made with Blockbench 3.9.3
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelPaper extends ModelBase {
			private final ModelRenderer tag;
			private final ModelRenderer[] bone = new ModelRenderer[4];
			private final float[][] bonePresetX = {
				{ 1.5708F, 1.5708F-0.3491F, 1.5708F, 1.5708F, 1.5708F }, { 0.0F, 0.3491F, -0.3491F, 0.0F, 0.0F }, { 0.0F, 0.3491F, 0.3491F, -0.3491F, 0.0F }, { 0.0F, -0.3491F, 0.3491F, 0.3491F, -0.3491F }
			};
		
			public ModelPaper() {
				textureWidth = 8;
				textureHeight = 8;
		
				tag = new ModelRenderer(this);
				tag.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone[0] = new ModelRenderer(this);
				bone[0].setRotationPoint(0.0F, 0.0F, 0.0F);
				tag.addChild(bone[0]);
				setRotationAngle(bone[0], 1.5708F, 0.0F, 3.1416F);
				bone[0].cubeList.add(new ModelBox(bone[0], 0, 0, -2.0F, 0.0F, 0.0F, 4, 2, 0, 0.005F, false));
				bone[1] = new ModelRenderer(this);
				bone[1].setRotationPoint(0.0F, 2.0F, 0.0F);
				bone[0].addChild(bone[1]);
				bone[1].cubeList.add(new ModelBox(bone[1], 0, 2, -2.0F, 0.0F, 0.0F, 4, 2, 0, 0.005F, false));
				bone[2] = new ModelRenderer(this);
				bone[2].setRotationPoint(0.0F, 2.0F, 0.0F);
				bone[1].addChild(bone[2]);
				bone[2].cubeList.add(new ModelBox(bone[2], 0, 4, -2.0F, 0.0F, 0.0F, 4, 2, 0, 0.005F, false));
				bone[3] = new ModelRenderer(this);
				bone[3].setRotationPoint(0.0F, 2.0F, 0.0F);
				bone[2].addChild(bone[3]);
				bone[3].cubeList.add(new ModelBox(bone[3], 0, 6, -2.0F, 0.0F, 0.0F, 4, 2, 0, 0.005F, false));
			}
		
			@Override
			public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				for (int i = 0; i < bone.length; i++) {
					float f1 = ageInTicks % 20.0F;
					float f2 = (f1 % 4.0F) / 4.0F;
					int j = (int)(f1 / 4.0F);
					int k = j < 4 ? j + 1 : 0;
					bone[i].rotateAngleX = bonePresetX[i][j] + (bonePresetX[i][k] - bonePresetX[i][j]) * f2;
				}
				tag.render(scale);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}	
	}
}
