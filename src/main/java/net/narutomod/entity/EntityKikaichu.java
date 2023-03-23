
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.MoverType;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumParticleTypes;

import net.narutomod.item.ItemJiton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class EntityKikaichu extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 323;
	public static final int ENTITYID_RANGED = 324;

	public EntityKikaichu(ElementsNarutomodMod instance) {
		super(instance, 674);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "kikaichu"), ENTITYID).name("kikaichu").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "bugball"), ENTITYID_RANGED).name("bugball").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new CustomRender(renderManager));
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new ECRender(renderManager));
	}

	@SideOnly(Side.CLIENT)
	public class ECRender extends Render<EC> {
		public ECRender(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}
		@Override
		public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
		}
		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return null;
		}
	}

	public static class EC extends Entity {
		private EntityLivingBase user;
		private EntityLivingBase target;
		private ItemJiton.SwarmTarget bugsTarget;
		private static final int MAXTIME = 600;

		public EC(World worldIn) {
			super(worldIn);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase userIn, EntityLivingBase targetIn, float power) {
			this(userIn.world);
			this.user = userIn;
			this.target = targetIn;
			Vec3d vec = this.getUserVector();
			this.setPosition(vec.x, vec.y, vec.z);
			this.bugsTarget = new ItemJiton.SwarmTarget(this.world, (int)(power * 50), vec,
			 this.getTargetVector(), new Vec3d(0.4d, 0.4d, 0.4d), 0.6f, 0.05f, false, 1f, -1) {
				@Override
				protected Entity createParticle(double x, double y, double z, double mx, double my, double mz, int c, float sc, int life) {
					return new EntityCustom(userIn, x, y, z, mx, my, mz, c, sc, life);
				}
				@Override
				protected void playFlyingSound(double x, double y, double z, float volume, float pitch) {
					if (this.getTicks() % 2 == 0) {
						userIn.world.playSound(null, x, y, z, net.minecraft.util.SoundEvent.REGISTRY
						 .getObject(new ResourceLocation("narutomod:bugs")),
						 net.minecraft.util.SoundCategory.BLOCKS, volume, pitch);
					}
				}
			};
		}

		@Override
		protected void entityInit() {
		}

		private Vec3d getUserVector() {
			return this.user.getPositionVector().addVector(0d, this.user.height * 0.6667f, 0d);
		}

		private AxisAlignedBB getTargetVector() {
			//return this.target.getPositionVector().addVector(0d, this.target.height * 0.6667f, 0d);
			return this.target.getEntityBoundingBox();
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.bugsTarget != null && !this.bugsTarget.shouldRemove()) {
				this.bugsTarget.forceRemove();
			}
		}

		@Override
		public void onUpdate() {
			if (this.user != null && this.user.isEntityAlive() 
			 && this.bugsTarget != null && !this.bugsTarget.shouldRemove() && this.target != null) {
				if (this.target.isEntityAlive() && this.ticksExisted < MAXTIME) {
					if (this.bugsTarget.allParticlesReachedTarget()) {
						this.bugsTarget.setTarget(this.getTargetVector(), 0.2f, 0.01f, false);
					} else {
						this.bugsTarget.setTarget(this.getTargetVector(), 0.6f, 0.05f, false);
					}
				} else {
					this.bugsTarget.setTarget(this.getUserVector(), 0.6f, 0.05f, true);
				}
				this.bugsTarget.onUpdate();
				this.setEntityBoundingBox(this.bugsTarget.getBorders());
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
				RayTraceResult result = ProcedureUtils.objectEntityLookingAt(entity, 30d, 1.5d, true);
				if (result != null) {
					if (result.entityHit instanceof EC) {
						result.entityHit.ticksExisted = EC.MAXTIME;
						return false;
					}
					if (result.entityHit instanceof EntityLivingBase) {
						entity.world.spawnEntity(new EC(entity, (EntityLivingBase)result.entityHit, power));
						return true;
					}
				}
				return false;
			}
		}
	}

	public static class EntityCustom extends Entity {
		private EntityLivingBase host;
		private int idleTime;
		private int maxAge;
	    public float prevLimbSwingAmount;
	    public float limbSwingAmount;
	    public float limbSwing;
	    private double chakra;

		public EntityCustom(World worldIn) {
			super(worldIn);
			this.setSize(0.1f, 0.1f);
			this.maxAge = 6000;
		}

		public EntityCustom(EntityLivingBase hostIn, double x, double y, double z, double mX, double mY, double mZ, int color, float scale, int maxAgeIn) {
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
			if (maxAgeIn > 0) {
				this.maxAge = maxAgeIn;
			}
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}

    	public void updateLimbSwing() {
	        if (this.collided) {
		        this.prevLimbSwingAmount = this.limbSwingAmount;
		        double d5 = this.posX - this.prevPosX;
		        double d7 = this.posZ - this.prevPosZ;
		        float f10 = MathHelper.sqrt(d5 * d5 + d7 * d7) * 4.0F;
		        if (f10 > 1.0F) {
		            f10 = 1.0F;
		        }
		        this.limbSwingAmount += (f10 - this.limbSwingAmount) * 0.4F;
		        this.limbSwing += this.limbSwingAmount;
	        }
	    }

		private void updateInFlightRotations() {
            float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = -(float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            for (this.rotationPitch = -(float)(MathHelper.atan2(this.motionY, (double)f4) * (180D / Math.PI)); 
             this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) ;
            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }
            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }
            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }
            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
		}

		@Override
		public void onUpdate() {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.updateLimbSwing();
			this.updateInFlightRotations();
			this.motionX *= 0.8D;
			this.motionY *= 0.8D;
			this.motionZ *= 0.8D;
			if (!this.world.isRemote) {
				for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox())) {
					if (!entity.equals(this.host)) {
						if (this.chakra < 100d && Chakra.pathway(entity).consume(0.25d)) {
							this.chakra += 0.25d;
						}
					} else if (this.chakra > 0.0d) {
						Chakra.pathway(this.host).consume(-this.chakra);
						this.chakra = 0.0d;
					}
				}
			}
			this.idleTime = this.getVelocity() < 0.001d ? this.idleTime + 1 : 0;
			if (!this.world.isRemote && (this.ticksExisted > this.maxAge || this.idleTime > 1000)) {
				this.setDead();
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.IN_WALL || source == DamageSource.DROWN
			 || source == DamageSource.FALL || source == DamageSource.FLY_INTO_WALL) {
				return false;
			}
			if (!this.world.isRemote && amount >= 1.0f) {
				this.setDead();
				((WorldServer)this.world).spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX, this.posY, this.posZ, 1, 0d, 0d, 0d, 0d);
				return true;
			}
			return false;
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
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}

	@SideOnly(Side.CLIENT)
	public class CustomRender extends Render<EntityCustom> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/beetle.png");
		protected final ModelBeetle model;

		public CustomRender(RenderManager renderManager) {
			super(renderManager);
			this.model = new ModelBeetle();
		}

		@Override
		public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
            float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
            if (f5 > 1.0F) {
                f5 = 1.0F;
            }
			this.bindEntityTexture(entity);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 0.03125d, z);
			GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.scale(0.1F, 0.1F, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.model.render(entity, f6, f5, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return TEXTURE;
		}
	}

	// Made with Blockbench 3.9.3
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelBeetle extends ModelQuadruped {
		//private final ModelRenderer head;
		private final ModelRenderer Mouth1;
		private final ModelRenderer Mouth2;
		private final ModelRenderer Eye1;
		private final ModelRenderer Eye2;
		//private final ModelRenderer body;
		//private final ModelRenderer leg1;
		private final ModelRenderer wingLeft;
		private final ModelRenderer wingRight;
		private final ModelRenderer foreleg1;
		//private final ModelRenderer leg2;
		private final ModelRenderer foreleg2;
		//private final ModelRenderer leg3;
		private final ModelRenderer foreleg3;
		//private final ModelRenderer leg4;
		private final ModelRenderer foreleg4;
		private final ModelRenderer leg5;
		private final ModelRenderer foreleg5;
		private final ModelRenderer leg6;
		private final ModelRenderer foreleg6;
		public ModelBeetle() {
			super(12, 0.0F);
			textureWidth = 64;
			textureHeight = 64;
			head = new ModelRenderer(this);
			head.setRotationPoint(0.0F, -1.5F, -5.0F);
			setRotationAngle(head, 0.3491F, 0.0F, 0.0F);
			head.cubeList.add(new ModelBox(head, 0, 0, -1.5F, 0.5F, -7.0F, 3, 2, 1, 0.0F, false));
			head.cubeList.add(new ModelBox(head, 15, 24, -1.5F, 0.0F, -6.0F, 3, 3, 3, 0.0F, false));
			head.cubeList.add(new ModelBox(head, 16, 17, -2.5F, 0.0F, -4.0F, 5, 3, 4, 0.1F, false));
			Mouth1 = new ModelRenderer(this);
			Mouth1.setRotationPoint(-1.0F, 1.7352F, -6.5119F);
			head.addChild(Mouth1);
			setRotationAngle(Mouth1, 0.3491F, -0.2618F, 0.0F);
			Mouth1.cubeList.add(new ModelBox(Mouth1, 4, 6, -0.5F, -0.4852F, -1.9881F, 1, 1, 2, -0.1F, false));
			Mouth2 = new ModelRenderer(this);
			Mouth2.setRotationPoint(1.0F, 1.7352F, -6.5119F);
			head.addChild(Mouth2);
			setRotationAngle(Mouth2, 0.3491F, 0.2618F, 0.0F);
			Mouth2.cubeList.add(new ModelBox(Mouth2, 4, 6, -0.5F, -0.4852F, -1.9881F, 1, 1, 2, -0.1F, true));
			Eye1 = new ModelRenderer(this);
			Eye1.setRotationPoint(-1.25F, 0.4852F, -6.0119F);
			head.addChild(Eye1);
			Eye1.cubeList.add(new ModelBox(Eye1, 4, 3, -0.5F, -0.7352F, -0.4881F, 1, 1, 2, 0.0F, false));
			Eye2 = new ModelRenderer(this);
			Eye2.setRotationPoint(1.25F, 0.4852F, -6.0119F);
			head.addChild(Eye2);
			Eye2.cubeList.add(new ModelBox(Eye2, 4, 3, -0.5F, -0.7352F, -0.4881F, 1, 1, 2, 0.0F, true));
			body = new ModelRenderer(this);
			body.setRotationPoint(0.0F, 0.0F, 0.0F);
			body.cubeList.add(new ModelBox(body, 0, 0, -4.0F, -1.5F, -5.0F, 8, 4, 10, 0.0F, false));
			body.cubeList.add(new ModelBox(body, 0, 14, -3.5F, -1.0F, 5.0F, 7, 4, 3, 0.0F, false));
			body.cubeList.add(new ModelBox(body, 0, 21, -3.0F, 0.0F, 7.0F, 6, 3, 3, 0.0F, false));
			body.cubeList.add(new ModelBox(body, 26, 0, -2.5F, 1.0F, 9.0F, 5, 2, 2, 0.0F, false));
			wingLeft = new ModelRenderer(this);
			wingLeft.setRotationPoint(1.0F, -1.75F, -4.0F);
			body.addChild(wingLeft);
			setRotationAngle(wingLeft, 0.0873F, 0.2618F, 0.0F);
			//setRotationAngle(wingLeft, 0.1309F, 1.0472F, 0.0F);
			//setRotationAngle(wingLeft, 0.2618F, 0.5236F, 0.0F);
			wingLeft.cubeList.add(new ModelBox(wingLeft, 28, 0, -3.0F, 0.0F, 0.0F, 6, 0, 12, 0.0F, false));
			wingRight = new ModelRenderer(this);
			wingRight.setRotationPoint(-1.0F, -1.75F, -4.0F);
			body.addChild(wingRight);
			setRotationAngle(wingRight, 0.0873F, -0.2618F, 0.0F);
			//setRotationAngle(wingRight, 0.1309F, -1.0472F, 0.0F);
			//setRotationAngle(wingRight, 0.2618F, -0.5236F, 0.0F);
			wingRight.cubeList.add(new ModelBox(wingRight, 28, 0, -3.0F, 0.0F, 0.0F, 6, 0, 12, 0.0F, true));
			leg1 = new ModelRenderer(this);
			leg1.setRotationPoint(-3.0F, 2.0F, -4.0F);
			setRotationAngle(leg1, 0.0F, -0.5236F, 0.5236F);
			leg1.cubeList.add(new ModelBox(leg1, 17, 14, -5.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, false));
			foreleg1 = new ModelRenderer(this);
			foreleg1.setRotationPoint(-4.5F, 0.25F, 0.0F);
			leg1.addChild(foreleg1);
			foreleg1.cubeList.add(new ModelBox(foreleg1, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, false));
			leg2 = new ModelRenderer(this);
			leg2.setRotationPoint(3.0F, 2.0F, -4.0F);
			setRotationAngle(leg2, 0.0F, 0.5236F, -0.5236F);
			leg2.cubeList.add(new ModelBox(leg2, 17, 14, 0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, true));
			foreleg2 = new ModelRenderer(this);
			foreleg2.setRotationPoint(4.5F, 0.25F, 0.0F);
			leg2.addChild(foreleg2);
			foreleg2.cubeList.add(new ModelBox(foreleg2, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, true));
			leg3 = new ModelRenderer(this);
			leg3.setRotationPoint(-3.0F, 2.0F, 0.0F);
			setRotationAngle(leg3, 0.0F, 0.0F, 0.5236F);
			leg3.cubeList.add(new ModelBox(leg3, 17, 14, -5.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, false));
			foreleg3 = new ModelRenderer(this);
			foreleg3.setRotationPoint(-4.5F, 0.25F, 0.0F);
			leg3.addChild(foreleg3);
			foreleg3.cubeList.add(new ModelBox(foreleg3, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, false));
			leg4 = new ModelRenderer(this);
			leg4.setRotationPoint(3.0F, 2.0F, 0.0F);
			setRotationAngle(leg4, 0.0F, 0.0F, -0.5236F);
			leg4.cubeList.add(new ModelBox(leg4, 17, 14, 0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, true));
			foreleg4 = new ModelRenderer(this);
			foreleg4.setRotationPoint(4.5F, 0.25F, 0.0F);
			leg4.addChild(foreleg4);
			foreleg4.cubeList.add(new ModelBox(foreleg4, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, true));
			leg5 = new ModelRenderer(this);
			leg5.setRotationPoint(-3.0F, 2.0F, 4.0F);
			setRotationAngle(leg5, 0.0F, 0.5236F, 0.5236F);
			leg5.cubeList.add(new ModelBox(leg5, 17, 14, -5.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, false));
			foreleg5 = new ModelRenderer(this);
			foreleg5.setRotationPoint(-4.5F, 0.25F, 0.0F);
			leg5.addChild(foreleg5);
			foreleg5.cubeList.add(new ModelBox(foreleg5, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, false));
			leg6 = new ModelRenderer(this);
			leg6.setRotationPoint(3.0F, 2.0F, 4.0F);
			setRotationAngle(leg6, 0.0F, -0.5236F, -0.5236F);
			leg6.cubeList.add(new ModelBox(leg6, 17, 14, 0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, true));
			foreleg6 = new ModelRenderer(this);
			foreleg6.setRotationPoint(4.5F, 0.25F, 0.0F);
			leg6.addChild(foreleg6);
			foreleg6.cubeList.add(new ModelBox(foreleg6, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, true));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
			head.render(f5);
			body.render(f5);
			leg1.render(f5);
			leg2.render(f5);
			leg3.render(f5);
			leg4.render(f5);
			leg5.render(f5);
			leg6.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		@Override
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			if (e.onGround) {
				setRotationAngle(wingRight, 0.0873F, -0.2618F, 0.0F);
				setRotationAngle(wingLeft, 0.0873F, 0.2618F, 0.0F);
			} else {
				float f6 = MathHelper.sin(f2);
				wingLeft.rotateAngleX = 0.1964F + f6 * 0.0655F;
				wingLeft.rotateAngleY = 0.7854F + f6 * 0.2618F;
				wingRight.rotateAngleX = 0.1964F + f6 * 0.0655F;
				wingRight.rotateAngleY = -0.7854F - f6 * 0.2618F;
			}
	        leg1.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
	        leg2.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
	        leg3.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
	        leg4.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
	        leg5.rotateAngleX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
	        leg6.rotateAngleX = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 1.4F * f1;
		}
	}
}
