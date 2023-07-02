
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.potion.PotionParalysis;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityLightningBeast extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 189;
	public static final int ENTITYID_RANGED = 190;
	private static final float MODELSCALE = 2.0F;

	public EntityLightningBeast(ElementsNarutomodMod instance) {
		super(instance, 451);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "lightning_beast"), ENTITYID).name("lightning_beast").tracker(64, 3, true).build());
	}

	public static class EC extends EntityTameable {
		private float power;
		private BlockPos destPos;
		private Vec3d startVec;
		private int jumpTicks;
		private final double ogSpeed = 1.6D;

		public EC(World world) {
			super(world);
			this.setSize(0.6F * MODELSCALE, 0.85F * MODELSCALE);
			this.isImmuneToFire = true;
			this.stepHeight = 8f;
			this.enablePersistence();
			this.setNoGravity(true);
		}

		public EC(EntityPlayer player, float powerIn) {
			this(player.world);
			RayTraceResult res = ProcedureUtils.raytraceBlocks(player, 4.0D);
			double x = res.getBlockPos().getX();
			double z = res.getBlockPos().getZ();
			this.setPosition(x + 0.5, player.posY, z + 0.5);
			this.rotationYaw = player.rotationYaw - 180.0f;
			this.rotationYawHead = this.rotationYaw;
			this.setTamedBy(player);
			this.power = powerIn;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
		}

		@Override
		public SoundEvent getAmbientSound() {
			return SoundEvents.ENTITY_WOLF_GROWL;
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return null;
		}

		@Override
		public SoundEvent getDeathSound() {
			return null;
		}

		@Override
		protected float getSoundVolume() {
			return 2.0F;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.ogSpeed);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(this.ogSpeed);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000D);
			//this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64D);
		}

		@Override
		public void addPotionEffect(PotionEffect potioneffectIn) {
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return false;
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			return EntityLightningArc.onStruck(entityIn, ItemJutsu.causeJutsuDamage(this, null), this.power);
		}

		private BlockPos findDestination() {
			EntityLivingBase owner = this.getOwner();
			if (owner != null) {
				RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(owner, 30d, this);
				if (rtr != null) {
					if (rtr.entityHit != null) {
						this.startVec = this.getPositionVector();
						return new BlockPos(rtr.hitVec);
					} else if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
						IBlockState state = this.world.getBlockState(rtr.getBlockPos());
						if (state.isTopSolid() || state.getMaterial().isLiquid()) {
							this.startVec = this.getPositionVector();
							return rtr.getBlockPos().up();
						}
					}
				}
			}
			this.startVec = null;
			return null;
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.ticksExisted % 10 == 0) {
				if (this.destPos != null) {
					Vec3d vec = new Vec3d(this.destPos).subtract(this.getPositionVector()).normalize().scale(this.ogSpeed);
					this.motionX = vec.x;
					this.motionY = vec.y + (this.onGround ? 0.08d : 0.0d);
					this.motionZ = vec.z;
                    this.rotationYaw = -((float)MathHelper.atan2(this.motionX, this.motionZ)) * (180F / (float)Math.PI);
                    this.renderYawOffset = this.rotationYaw;
				}
				if (this.destPos == null || this.isDestOnPath()) {
					this.destPos = this.findDestination();
				}
			}
		}

		private boolean isDestOnPath() {
			return this.startVec != null && this.destPos != null
			 && this.getDistanceSq(this.startVec.x, this.startVec.y, this.startVec.z) > this.getDistanceSqToCenter(this.destPos);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			EntityLivingBase owner = this.getOwner();
			if (!this.world.isRemote && this.ticksExisted % 4 == 0 && owner != null) {
				this.world.spawnEntity(new EntityLightningArc.Base(this.world, owner.getPositionEyes(1f), 
				 this.getPositionEyes(1f), 0xC00000FF, 1, 0f));
			}
			if (this.rand.nextInt(8) == 0) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:electricity")),
				  1f, this.rand.nextFloat() * 0.6f + 0.9f);
			}
			if (this.rand.nextFloat() <= 0.4f) {
				EntityLightningArc.spawnAsParticle(this.world, this.posX + this.rand.nextGaussian() * this.width * 0.5d,
				 this.posY + this.rand.nextDouble() * this.height, this.posZ + this.rand.nextGaussian() * this.width * 0.5d);
			}
			if (!this.world.isRemote && (this.ticksExisted > 20 + (int)(this.power * 2) || owner == null || !owner.isEntityAlive())) {
				this.setDead();
			}
		}

		@Override
		protected void collideWithNearbyEntities() {
			Vec3d vec1 = this.getPositionVector().addVector(0d, 0.5d * this.height, 0d);
			Vec3d vec2 = vec1.add(ProcedureUtils.getMotion(this));
			for (Entity entity : this.world.getEntitiesInAABBexcluding(this,
			 this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ),
			 EntitySelectors.getTeamCollisionPredicate(this))) {
				if (entity.getEntityBoundingBox().grow(this.width * 0.5, this.height * 0.5, this.width * 0.5)
				 .calculateIntercept(vec1, vec2) != null) {
					this.collideWithEntity(entity);
				}
			}
		}

		@Override
		protected void collideWithEntity(Entity entityIn) {
			if (!entityIn.equals(this.getOwner())) {
				this.attackEntityAsMob(entityIn);
			}
			super.collideWithEntity(entityIn);
		}

		@Override
		protected float getWaterSlowDown() {
			return 1.0f;
		}

		@Override
		public EC createChild(EntityAgeable ageable) {
			return this.isTamed() ? new EC((EntityPlayer)this.getOwner(), this.power) : null;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (entity instanceof EntityPlayer && power >= 5.0f) {
					entity.world.spawnEntity(new EC((EntityPlayer)entity, power));
					return true;
				}
				return false;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends RenderLiving<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/wolf_lightning.png");
	
			public RenderCustom(RenderManager p_i47187_1_) {
				super(p_i47187_1_, new ModelLightningWolf(), 0.5F * MODELSCALE);
				this.addLayer(new LayerLightningCharge(this));
			}
			
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				GlStateManager.enableBlend();
				GlStateManager.color(1f, 1f, 1f, Math.min((float)entity.ticksExisted/60f, 1f));
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				//GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);			
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class LayerLightningCharge implements LayerRenderer<EC> {
			private final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("narutomod:textures/electric_armor.png");
			private final RenderCustom renderer;
			private final ModelLightningWolf renderModel = new ModelLightningWolf();
	
			public LayerLightningCharge(RenderCustom rendererIn) {
				this.renderer = rendererIn;
			}
	
			@Override
			public void doRenderLayer(EC entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
					float netHeadYaw, float headPitch, float scale) {
				if (entitylivingbaseIn.ticksExisted % 20 <= 10 + entitylivingbaseIn.getRNG().nextInt(10)) {
					GlStateManager.depthMask(true);
					this.renderer.bindTexture(LIGHTNING_TEXTURE);
					GlStateManager.scale(1.1F, 1.1F, 1.1F);
					//GlStateManager.translate(0.0F, 0.25F, 0.0F);
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					float f = entitylivingbaseIn.ticksExisted + partialTicks;
					GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
					GlStateManager.matrixMode(5888);
					GlStateManager.enableAlpha();
					GlStateManager.enableBlend();
					GlStateManager.color(0.5F, 0.5F, 0.5F, 0.5F);
					GlStateManager.disableLighting();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
					this.renderModel.setModelAttributes(this.renderer.getMainModel());
					Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
					this.renderModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
					this.renderModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
					Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					GlStateManager.matrixMode(5888);
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.depthMask(false);
				}
			}
	
			@Override
			public boolean shouldCombineTextures() {
				return false;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelLightningWolf extends ModelBase {
			private final ModelRenderer head;
			private final ModelRenderer body;
			private final ModelRenderer upperBody;
			private final ModelRenderer leg0;
			private final ModelRenderer leg1;
			private final ModelRenderer leg2;
			private final ModelRenderer leg3;
			private final ModelRenderer tail;
	
			public ModelLightningWolf() {
				textureWidth = 64;
				textureHeight = 32;
				head = new ModelRenderer(this);
				head.setRotationPoint(-1.0F, 13.5F, -7.0F);
				head.cubeList.add(new ModelBox(head, 0, 0, -2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F, false));
				head.cubeList.add(new ModelBox(head, 0, 10, -0.5F, -0.0F, -5.0F, 3, 3, 4, 0.0F, false));
				head.cubeList.add(new ModelBox(head, 16, 14, -2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F, false));
				head.cubeList.add(new ModelBox(head, 16, 14, 2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F, false));
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 14.0F, 2.0F);
				setRotationAngle(body, 1.5708F, 0.0F, 0.0F);
				body.cubeList.add(new ModelBox(body, 18, 14, -4.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F, false));
				upperBody = new ModelRenderer(this);
				upperBody.setRotationPoint(-1.0F, 14.0F, 2.0F);
				setRotationAngle(upperBody, -1.5708F, 0.0F, 0.0F);
				upperBody.cubeList.add(new ModelBox(upperBody, 21, 0, -4.0F, 2.0F, -4.0F, 8, 6, 7, 0.0F, false));
				leg0 = new ModelRenderer(this);
				leg0.setRotationPoint(-2.5F, 16.0F, 7.0F);
				leg0.cubeList.add(new ModelBox(leg0, 0, 18, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(0.5F, 16.0F, 7.0F);
				leg1.cubeList.add(new ModelBox(leg1, 0, 18, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(-2.5F, 16.0F, -4.0F);
				leg2.cubeList.add(new ModelBox(leg2, 0, 18, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(0.5F, 16.0F, -4.0F);
				leg3.cubeList.add(new ModelBox(leg3, 0, 18, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
				tail = new ModelRenderer(this);
				tail.setRotationPoint(-1.0F, 12.0F, 8.0F);
				setRotationAngle(tail, 0.9599F, 0.0F, 0.0F);
				tail.cubeList.add(new ModelBox(tail, 9, 18, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float age, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F);
				GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
				head.render(f5);
				body.render(f5);
				upperBody.render(f5);
				leg0.render(f5);
				leg1.render(f5);
				leg2.render(f5);
				leg3.render(f5);
				tail.render(f5);
				GlStateManager.popMatrix();
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				f *= 2.0f / e.height;
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				this.head.rotateAngleY = f3 / (180F / (float) Math.PI);
				this.head.rotateAngleX = f4 / (180F / (float) Math.PI);
				this.leg0.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
				this.leg1.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
				this.leg2.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
				this.tail.rotateAngleZ = f2 * 0.2f;
				this.leg3.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
			}
		}
	}
}
