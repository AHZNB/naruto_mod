
package net.narutomod.entity;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class EntityLightningPanther extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 409;
	public static final int ENTITYID_RANGED = 410;

	public EntityLightningPanther(ElementsNarutomodMod instance) {
		super(instance, 801);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "lightning_panther"), ENTITYID)
		 .name("lightning_panther").tracker(64, 3, true).build());
	}

	public static class EC extends EntityTameable {
		private static final DataParameter<Float> POWER = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private final float ogWidth = 1.2F;
		private final float ogHeight = 1.75F;
		private final double ogSpeed = 2.0D;
		private BlockPos destPos;
		private Vec3d startVec;

		public EC(World world) {
			super(world);
			this.setSize(this.ogWidth, this.ogHeight);
			this.isImmuneToFire = true;
			this.stepHeight = 8f;
			this.enablePersistence();
			this.setNoGravity(true);
		}

		public EC(EntityPlayer player, float powerIn) {
			this(player.world);
			this.rotationYawHead = player.rotationYaw;
			this.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, 0f);
			this.setTamedBy(player);
			this.setPower(powerIn);
			Vec3d vec = player.getLookVec();
			this.motionX = vec.x * 0.2d;
			this.motionZ = vec.z * 0.2d;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(POWER, Float.valueOf(0f));
		}

		public float getPower() {
			return ((Float)this.getDataManager().get(POWER)).floatValue();
		}

		protected void setPower(float power) {
			this.getDataManager().set(POWER, Float.valueOf(power));
			this.setSize(this.ogWidth * power, this.ogHeight * power);
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (POWER.equals(key) && this.world.isRemote) {
				float scale = this.getPower();
				this.setSize(this.ogWidth * scale, this.ogHeight * scale);
			}
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
		}

		@Override
		public SoundEvent getAmbientSound() {
			return null;
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
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.ogSpeed);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(this.ogSpeed);
		}

		@Override
		public boolean canBePushed() {
			return false;
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
			return EntityLightningArc.onStruck(entityIn, ItemJutsu.causeJutsuDamage(this, null), this.getPower() * 20.0f);
		}

		private BlockPos findDestination() {
			EntityLivingBase owner = this.getOwner();
			if (owner != null) {
				RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(owner, 40d, this);
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
			float f = this.getPower();
			if (!this.world.isRemote && (owner == null || !owner.isEntityAlive() || this.ticksExisted > 20 + (int)(f * 20))) {
				this.setDead();
			} else {
				if (this.isInWater()) {
					if (this.width < this.ogWidth * f * 4.0f) {
						this.setSize(this.ogWidth * f * 4.0f, this.ogHeight * f * 3.0f);
					}
				}
				if (!this.world.isRemote && this.ticksExisted == 1) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:roar")), 5f, 1f);
				}
				if (!this.world.isRemote && owner != null) {
					this.world.spawnEntity(new EntityLightningArc.Base(this.world,
					 owner.getPositionVector().addVector(0d, this.rand.nextDouble() * 1.5d, 0d), 
					 this.getPositionEyes(1f), 0x00000000, 0, 0f));
				}
				if (this.rand.nextInt(3) == 2) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:electricity")),
					  1f, this.rand.nextFloat() * 0.6f + 0.9f);
				}
				if (this.world.isRemote) {
					EntityLightningArc.spawnAsParticle(this.world, this.posX + (this.rand.nextDouble()-0.5d) * this.width,
					 this.posY + this.rand.nextDouble() * this.height, this.posZ + (this.rand.nextDouble()-0.5d) * this.width,
					 this.width, 0d, 0.15d, 0d, 0);
				}
			}
		}

		@Override
		protected void collideWithNearbyEntities() {
			Vec3d vec1 = this.getPositionVector().addVector(0d, 0.5d * this.height, 0d);
			Vec3d vec2 = vec1.add(ProcedureUtils.getMotion(this));
			for (Entity entity : this.world.getEntitiesInAABBexcluding(this,
			 this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ), EntitySelectors.getTeamCollisionPredicate(this))) {
				if (entity.getEntityBoundingBox().grow(this.width * 0.5, this.height * 0.5, this.width * 0.5).calculateIntercept(vec1, vec2) != null) {
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
			return this.isTamed() ? new EC((EntityPlayer)this.getOwner(), this.getPower()) : null;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (entity instanceof EntityPlayer && power >= 1.0f) {
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/blackcat.png");
			protected final ModelBigCat model;
	
			public RenderCustom(RenderManager p_i47187_1_) {
				super(p_i47187_1_, new ModelBigCat(), 0.5F);
				this.addLayer(new LayerLightningCharge(this));
				this.model = (ModelBigCat)this.mainModel;
			}
			
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				GlStateManager.enableBlend();
				//GlStateManager.color(1f, 1f, 1f, Math.min((float)entity.ticksExisted/60f, 1f));
				//GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/electric_armor.png");
			private final RenderCustom renderer;
	
			public LayerLightningCharge(RenderCustom rendererIn) {
				this.renderer = rendererIn;
			}
	
			@Override
			public void doRenderLayer(EC entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
					float netHeadYaw, float headPitch, float scale) {
				if (entitylivingbaseIn.ticksExisted % 20 <= 10 + entitylivingbaseIn.getRNG().nextInt(10)) {
					GlStateManager.pushMatrix();
					GlStateManager.scale(1.1F, 1.1F, 1.1F);
					GlStateManager.depthMask(true);
					this.renderer.bindTexture(this.texture);
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
					this.renderer.model.setModelAttributes(this.renderer.getMainModel());
					Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
					this.renderer.model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
					this.renderer.model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
					Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					GlStateManager.matrixMode(5888);
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.depthMask(false);
					GlStateManager.popMatrix();
				}
			}
	
			@Override
			public boolean shouldCombineTextures() {
				return false;
			}
		}
	
		// Made with Blockbench 4.4.3
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelBigCat extends ModelBase {
			private final ModelRenderer head;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer ears;
			private final ModelRenderer RightEar;
			private final ModelRenderer cube_r3;
			private final ModelRenderer LeftEar;
			private final ModelRenderer cube_r4;
			private final ModelRenderer hair;
			private final ModelRenderer Hair4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer Hair3;
			private final ModelRenderer cube_r6;
			private final ModelRenderer Hair2;
			private final ModelRenderer cube_r7;
			private final ModelRenderer Hair7;
			private final ModelRenderer cube_r8;
			private final ModelRenderer Hair8;
			private final ModelRenderer cube_r9;
			private final ModelRenderer Hair9;
			private final ModelRenderer cube_r10;
			private final ModelRenderer Hair10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			private final ModelRenderer Hair5;
			private final ModelRenderer cube_r13;
			private final ModelRenderer Hair6;
			private final ModelRenderer cube_r14;
			private final ModelRenderer snout;
			private final ModelRenderer cube_r15;
			private final ModelRenderer cube_r16;
			private final ModelRenderer cube_r17;
			private final ModelRenderer nose;
			private final ModelRenderer cube_r18;
			private final ModelRenderer UpperTeeth;
			private final ModelRenderer cube_r19;
			private final ModelRenderer cube_r20;
			private final ModelRenderer cube_r21;
			private final ModelRenderer cube_r22;
			private final ModelRenderer cube_r23;
			private final ModelRenderer jaw;
			private final ModelRenderer cube_r24;
			private final ModelRenderer cube_r25;
			private final ModelRenderer cube_r26;
			private final ModelRenderer cube_r27;
			private final ModelRenderer cube_r28;
			private final ModelRenderer cube_r29;
			private final ModelRenderer bone;
			private final ModelRenderer cube_r30;
			private final ModelRenderer cube_r31;
			private final ModelRenderer bone2;
			private final ModelRenderer cube_r32;
			private final ModelRenderer cube_r33;
			private final ModelRenderer bone3;
			private final ModelRenderer cube_r34;
			private final ModelRenderer cube_r35;
			private final ModelRenderer bone4;
			private final ModelRenderer cube_r36;
			private final ModelRenderer cube_r37;
			private final ModelRenderer LowerTeeth;
			private final ModelRenderer cube_r38;
			private final ModelRenderer eyes;
			private final ModelRenderer body;
			private final ModelRenderer cube_r39;
			private final ModelRenderer cube_r41;
			private final ModelRenderer[] tail = new ModelRenderer[7];
			private final float tailSwayX[] = new float[7];
			private final float tailSwayY[] = new float[7];
			private final float tailSwayZ[] = new float[7];
			private final ModelRenderer leg1;
			private final ModelRenderer Joint7;
			private final ModelRenderer cube_r42;
			private final ModelRenderer Joint8;
			private final ModelRenderer cube_r43;
			private final ModelRenderer Foot1;
			private final ModelRenderer cube_r44;
			private final ModelRenderer cube_r45;
			private final ModelRenderer cube_r46;
			private final ModelRenderer cube_r47;
			private final ModelRenderer leg2;
			private final ModelRenderer Joint2;
			private final ModelRenderer cube_r48;
			private final ModelRenderer Joint5;
			private final ModelRenderer cube_r49;
			private final ModelRenderer Foot2;
			private final ModelRenderer cube_r50;
			private final ModelRenderer cube_r51;
			private final ModelRenderer cube_r52;
			private final ModelRenderer cube_r53;
			private final ModelRenderer leg3;
			private final ModelRenderer Joint3;
			private final ModelRenderer cube_r54;
			private final ModelRenderer Joint4;
			private final ModelRenderer cube_r55;
			private final ModelRenderer Foot3;
			private final ModelRenderer cube_r56;
			private final ModelRenderer cube_r57;
			private final ModelRenderer cube_r58;
			private final ModelRenderer cube_r59;
			private final ModelRenderer leg4;
			private final ModelRenderer Joint6;
			private final ModelRenderer cube_r60;
			private final ModelRenderer Joint9;
			private final ModelRenderer cube_r61;
			private final ModelRenderer Foot4;
			private final ModelRenderer cube_r62;
			private final ModelRenderer cube_r63;
			private final ModelRenderer cube_r64;
			private final ModelRenderer cube_r65;
			private final Random rand = new Random();
			
			public ModelBigCat() {
				textureWidth = 128;
				textureHeight = 128;
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 3.0F, -6.0F);
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(6.0F, 2.75F, 4.0F);
				head.addChild(cube_r1);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 45, 43, -11.0F, -8.0F, -14.0F, 10, 2, 1, 0.0F, false));
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(6.0F, 9.75F, 4.25F);
				head.addChild(cube_r2);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 57, 20, -11.0F, -15.0F, -14.0F, 10, 10, 11, 0.0F, false));
				ears = new ModelRenderer(this);
				ears.setRotationPoint(11.75F, 5.25F, 0.25F);
				head.addChild(ears);
				RightEar = new ModelRenderer(this);
				RightEar.setRotationPoint(-12.75F, 6.0F, -11.0F);
				ears.addChild(RightEar);
				setRotationAngle(RightEar, -0.5744F, -0.3332F, -0.468F);
				RightEar.cubeList.add(new ModelBox(RightEar, 0, 8, 3.5F, -18.5F, -8.25F, 2, 6, 1, 0.0F, false));
				RightEar.cubeList.add(new ModelBox(RightEar, 0, 0, 4.3284F, -18.5F, -8.25F, 2, 6, 1, 0.0F, false));
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(16.4047F, -0.6456F, 0.0F);
				RightEar.addChild(cube_r3);
				setRotationAngle(cube_r3, 0.0F, 0.0F, -0.7854F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 28, 70, 3.5F, -21.75F, -8.25F, 2, 2, 1, 0.0F, false));
				LeftEar = new ModelRenderer(this);
				LeftEar.setRotationPoint(-10.75F, 6.0F, -11.0F);
				ears.addChild(LeftEar);
				setRotationAngle(LeftEar, -0.5744F, 0.3332F, 0.468F);
				LeftEar.cubeList.add(new ModelBox(LeftEar, 0, 8, -5.5F, -18.5F, -8.25F, 2, 6, 1, 0.0F, true));
				LeftEar.cubeList.add(new ModelBox(LeftEar, 0, 0, -6.3284F, -18.5F, -8.25F, 2, 6, 1, 0.0F, true));
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(-16.4047F, -0.6456F, 0.0F);
				LeftEar.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.0F, 0.0F, 0.7854F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 28, 70, -5.5F, -21.75F, -8.25F, 2, 2, 1, 0.0F, true));
				hair = new ModelRenderer(this);
				hair.setRotationPoint(11.75F, 5.25F, 0.25F);
				head.addChild(hair);
				Hair4 = new ModelRenderer(this);
				Hair4.setRotationPoint(3.5F, 10.25F, -19.25F);
				hair.addChild(Hair4);
				setRotationAngle(Hair4, -1.1345F, 0.0436F, -1.0036F);
				Hair4.cubeList.add(new ModelBox(Hair4, 28, 66, -1.6651F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, false));
				Hair4.cubeList.add(new ModelBox(Hair4, 42, 60, -0.8367F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, false));
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				Hair4.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.0F, 0.0F, 0.7854F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 70, 0, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));
				Hair3 = new ModelRenderer(this);
				Hair3.setRotationPoint(-27.0F, 10.25F, -19.25F);
				hair.addChild(Hair3);
				setRotationAngle(Hair3, -1.1345F, -0.0436F, 1.0036F);
				Hair3.cubeList.add(new ModelBox(Hair3, 28, 66, -0.3349F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, true));
				Hair3.cubeList.add(new ModelBox(Hair3, 42, 60, -1.1633F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, true));
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				Hair3.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.0F, 0.0F, -0.7854F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 70, 0, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));
				Hair2 = new ModelRenderer(this);
				Hair2.setRotationPoint(3.5F, 8.25F, -10.5F);
				hair.addChild(Hair2);
				setRotationAngle(Hair2, -0.829F, 0.0436F, -0.9163F);
				Hair2.cubeList.add(new ModelBox(Hair2, 0, 60, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, false));
				Hair2.cubeList.add(new ModelBox(Hair2, 6, 42, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, false));
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				Hair2.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.0F, 0.0F, 0.7854F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 70, 3, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));
				Hair7 = new ModelRenderer(this);
				Hair7.setRotationPoint(-27.0F, 8.25F, -10.5F);
				hair.addChild(Hair7);
				setRotationAngle(Hair7, -0.829F, -0.0436F, 0.9163F);
				Hair7.cubeList.add(new ModelBox(Hair7, 0, 60, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, true));
				Hair7.cubeList.add(new ModelBox(Hair7, 6, 42, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, true));
				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				Hair7.addChild(cube_r8);
				setRotationAngle(cube_r8, 0.0F, 0.0F, -0.7854F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 70, 3, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));
				Hair8 = new ModelRenderer(this);
				Hair8.setRotationPoint(5.75F, 5.25F, -10.5F);
				hair.addChild(Hair8);
				setRotationAngle(Hair8, -0.829F, 0.0436F, -0.9163F);
				Hair8.cubeList.add(new ModelBox(Hair8, 45, 33, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, false));
				Hair8.cubeList.add(new ModelBox(Hair8, 10, 36, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, false));
				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				Hair8.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.0F, 0.0F, 0.7854F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 66, 41, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));
				Hair9 = new ModelRenderer(this);
				Hair9.setRotationPoint(-29.25F, 5.25F, -10.5F);
				hair.addChild(Hair9);
				setRotationAngle(Hair9, -0.829F, -0.0436F, 0.9163F);
				Hair9.cubeList.add(new ModelBox(Hair9, 45, 33, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, true));
				Hair9.cubeList.add(new ModelBox(Hair9, 10, 36, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, true));
				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				Hair9.addChild(cube_r10);
				setRotationAngle(cube_r10, 0.0F, 0.0F, -0.7854F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 66, 41, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));
				Hair10 = new ModelRenderer(this);
				Hair10.setRotationPoint(-9.0F, -34.75F, 12.0F);
				hair.addChild(Hair10);
				setRotationAngle(Hair10, -1.1781F, 0.0F, 0.0F);
				Hair10.cubeList.add(new ModelBox(Hair10, 9, 8, -3.3358F, 24.2726F, 14.5601F, 2, 5, 1, 0.0F, false));
				Hair10.cubeList.add(new ModelBox(Hair10, 9, 0, -4.1642F, 24.2726F, 14.5601F, 2, 5, 1, 0.0F, false));
				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(12.8063F, 39.8289F, 31.3101F);
				Hair10.addChild(cube_r11);
				setRotationAngle(cube_r11, 0.0F, 0.0F, -0.7854F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 45, -0.6569F, -23.3431F, -14.6549F, 2, 2, 1, 0.0F, false));
				cube_r11.cubeList.add(new ModelBox(cube_r11, 45, 38, -3.1318F, -25.8179F, -14.6549F, 2, 2, 1, 0.0F, false));
				cube_r11.cubeList.add(new ModelBox(cube_r11, 35, 62, -2.6433F, -23.8315F, -15.3642F, 2, 2, 1, 0.0F, false));
				cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 65, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));
				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(-18.3063F, 39.8289F, 31.3101F);
				Hair10.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.0F, 0.0F, 0.7854F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 45, 38, 1.1318F, -25.8179F, -14.6549F, 2, 2, 1, 0.0F, true));
				cube_r12.cubeList.add(new ModelBox(cube_r12, 35, 62, 0.6433F, -23.8315F, -15.3642F, 2, 2, 1, 0.0F, true));
				Hair5 = new ModelRenderer(this);
				Hair5.setRotationPoint(4.25F, 9.75F, -10.5F);
				hair.addChild(Hair5);
				setRotationAngle(Hair5, -0.829F, -0.3491F, -0.9163F);
				Hair5.cubeList.add(new ModelBox(Hair5, 52, 0, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, false));
				Hair5.cubeList.add(new ModelBox(Hair5, 0, 39, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, false));
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				Hair5.addChild(cube_r13);
				setRotationAngle(cube_r13, 0.0F, 0.0F, 0.7854F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 68, 17, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));
				Hair6 = new ModelRenderer(this);
				Hair6.setRotationPoint(-27.75F, 9.75F, -10.5F);
				hair.addChild(Hair6);
				setRotationAngle(Hair6, -0.829F, 0.3491F, 0.9163F);
				Hair6.cubeList.add(new ModelBox(Hair6, 52, 0, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, true));
				Hair6.cubeList.add(new ModelBox(Hair6, 0, 39, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, true));
				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				Hair6.addChild(cube_r14);
				setRotationAngle(cube_r14, 0.0F, 0.0F, -0.7854F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 68, 17, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));
				snout = new ModelRenderer(this);
				snout.setRotationPoint(-0.25F, 20.5F, 14.25F);
				head.addChild(snout);
				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(7.5815F, -13.5F, -19.9805F);
				snout.addChild(cube_r15);
				setRotationAngle(cube_r15, 0.0873F, 0.1309F, 0.0F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 88, 0, -5.0F, -8.0F, -10.0F, 2, 3, 7, 0.0F, true));
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(-7.0815F, -13.5F, -19.9805F);
				snout.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.0873F, -0.1309F, 0.0F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 88, 0, 3.0F, -8.0F, -10.0F, 2, 3, 7, 0.0F, false));
				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(0.25F, -20.4088F, -30.0418F);
				snout.addChild(cube_r17);
				setRotationAngle(cube_r17, 0.1745F, 0.0F, 0.0F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 69, 7, -3.0F, -0.1988F, -0.1005F, 6, 3, 7, 0.0F, false));
				nose = new ModelRenderer(this);
				nose.setRotationPoint(6.25F, -13.5F, -20.75F);
				snout.addChild(nose);
				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(0.0F, 0.0F, 0.0F);
				nose.addChild(cube_r18);
				setRotationAngle(cube_r18, 0.0873F, 0.0F, 0.0F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 55, 46, -7.0F, -8.0F, -9.0F, 2, 1, 1, 0.0F, false));
				UpperTeeth = new ModelRenderer(this);
				UpperTeeth.setRotationPoint(0.0F, -0.5F, -9.25F);
				snout.addChild(UpperTeeth);
				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(3.556F, -17.25F, -1.7657F);
				UpperTeeth.addChild(cube_r19);
				setRotationAngle(cube_r19, 3.1416F, 0.0F, -3.1416F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 31, 0.807F, -0.5F, 18.4824F, 1, 2, 0, 0.0F, false));
				cube_r20 = new ModelRenderer(this);
				cube_r20.setRotationPoint(16.2956F, -17.0F, -19.249F);
				UpperTeeth.addChild(cube_r20);
				setRotationAngle(cube_r20, 0.0F, -1.5272F, 0.0F);
				cube_r20.cubeList.add(new ModelBox(cube_r20, 45, 46, -1.807F, -0.5F, 18.4824F, 5, 1, 0, 0.0F, false));
				cube_r21 = new ModelRenderer(this);
				cube_r21.setRotationPoint(-15.7956F, -17.0F, -19.249F);
				UpperTeeth.addChild(cube_r21);
				setRotationAngle(cube_r21, 0.0F, 1.5272F, 0.0F);
				cube_r21.cubeList.add(new ModelBox(cube_r21, 45, 47, -3.193F, -0.5F, 18.4824F, 5, 1, 0, 0.0F, false));
				cube_r22 = new ModelRenderer(this);
				cube_r22.setRotationPoint(0.556F, -17.0F, -1.7657F);
				UpperTeeth.addChild(cube_r22);
				setRotationAngle(cube_r22, 3.1416F, 0.0F, -3.1416F);
				cube_r22.cubeList.add(new ModelBox(cube_r22, 9, 6, -1.193F, -0.5F, 18.4824F, 3, 1, 0, 0.0F, false));
				cube_r23 = new ModelRenderer(this);
				cube_r23.setRotationPoint(-0.444F, -17.25F, -1.7657F);
				UpperTeeth.addChild(cube_r23);
				setRotationAngle(cube_r23, 3.1416F, 0.0F, -3.1416F);
				cube_r23.cubeList.add(new ModelBox(cube_r23, 14, 31, 0.807F, -0.5F, 18.4824F, 1, 2, 0, 0.0F, false));
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(-0.0749F, 3.6113F, -9.4922F);
				head.addChild(jaw);
				setRotationAngle(jaw, 0.5236F, 0.0F, 0.0F);
				cube_r24 = new ModelRenderer(this);
				cube_r24.setRotationPoint(6.0749F, 8.1425F, -10.4545F);
				jaw.addChild(cube_r24);
				setRotationAngle(cube_r24, 0.1745F, 0.0F, 0.0F);
				cube_r24.cubeList.add(new ModelBox(cube_r24, 20, 62, -9.0F, -6.0F, 9.0F, 6, 1, 3, 0.0F, false));
				cube_r25 = new ModelRenderer(this);
				cube_r25.setRotationPoint(6.0749F, 9.3049F, 2.6281F);
				jaw.addChild(cube_r25);
				setRotationAngle(cube_r25, -0.3054F, 0.0F, 0.0F);
				cube_r25.cubeList.add(new ModelBox(cube_r25, 0, 34, -9.0F, -6.0F, -10.0F, 6, 1, 1, 0.0F, false));
				cube_r26 = new ModelRenderer(this);
				cube_r26.setRotationPoint(6.0749F, 5.9394F, 5.9459F);
				jaw.addChild(cube_r26);
				setRotationAngle(cube_r26, 0.0873F, 0.0F, 0.0F);
				cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 31, -9.0F, -6.0F, -10.0F, 6, 1, 2, 0.0F, false));
				cube_r27 = new ModelRenderer(this);
				cube_r27.setRotationPoint(-6.8275F, 5.6387F, 4.6908F);
				jaw.addChild(cube_r27);
				setRotationAngle(cube_r27, 0.0873F, -0.0873F, 0.0F);
				cube_r27.cubeList.add(new ModelBox(cube_r27, 0, 0, 3.0F, -6.0F, -10.0F, 1, 1, 7, 0.0F, false));
				cube_r28 = new ModelRenderer(this);
				cube_r28.setRotationPoint(6.9773F, 5.6387F, 4.6908F);
				jaw.addChild(cube_r28);
				setRotationAngle(cube_r28, 0.0873F, 0.0873F, 0.0F);
				cube_r28.cubeList.add(new ModelBox(cube_r28, 0, 8, -4.0F, -6.0F, -10.0F, 1, 1, 7, 0.0F, false));
				cube_r29 = new ModelRenderer(this);
				cube_r29.setRotationPoint(6.0749F, 5.6387F, 4.9922F);
				jaw.addChild(cube_r29);
				setRotationAngle(cube_r29, 0.0873F, 0.0F, 0.0F);
				cube_r29.cubeList.add(new ModelBox(cube_r29, 69, 84, -9.0F, -6.0F, -10.0F, 6, 1, 7, 0.0F, false));
				bone = new ModelRenderer(this);
				bone.setRotationPoint(8.2288F, -3.2172F, 4.8801F);
				jaw.addChild(bone);
				cube_r30 = new ModelRenderer(this);
				cube_r30.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(cube_r30);
				setRotationAngle(cube_r30, 0.1809F, 0.1538F, -0.7744F);
				cube_r30.cubeList.add(new ModelBox(cube_r30, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));
				cube_r31 = new ModelRenderer(this);
				cube_r31.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone.addChild(cube_r31);
				setRotationAngle(cube_r31, 0.0564F, 0.0308F, -0.7859F);
				cube_r31.cubeList.add(new ModelBox(cube_r31, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(9.2288F, -3.2172F, 4.8801F);
				jaw.addChild(bone2);
				cube_r32 = new ModelRenderer(this);
				cube_r32.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone2.addChild(cube_r32);
				setRotationAngle(cube_r32, 0.1809F, 0.1538F, -0.7744F);
				cube_r32.cubeList.add(new ModelBox(cube_r32, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));
				cube_r33 = new ModelRenderer(this);
				cube_r33.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone2.addChild(cube_r33);
				setRotationAngle(cube_r33, 0.0564F, 0.0308F, -0.7859F);
				cube_r33.cubeList.add(new ModelBox(cube_r33, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(10.2288F, -3.2172F, 4.8801F);
				jaw.addChild(bone3);
				cube_r34 = new ModelRenderer(this);
				cube_r34.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone3.addChild(cube_r34);
				setRotationAngle(cube_r34, 0.1809F, 0.1538F, -0.7744F);
				cube_r34.cubeList.add(new ModelBox(cube_r34, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));
				cube_r35 = new ModelRenderer(this);
				cube_r35.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone3.addChild(cube_r35);
				setRotationAngle(cube_r35, 0.0564F, 0.0308F, -0.7859F);
				cube_r35.cubeList.add(new ModelBox(cube_r35, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(11.2288F, -3.2172F, 4.8801F);
				jaw.addChild(bone4);
				cube_r36 = new ModelRenderer(this);
				cube_r36.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone4.addChild(cube_r36);
				setRotationAngle(cube_r36, 0.1809F, 0.1538F, -0.7744F);
				cube_r36.cubeList.add(new ModelBox(cube_r36, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));
				cube_r37 = new ModelRenderer(this);
				cube_r37.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone4.addChild(cube_r37);
				setRotationAngle(cube_r37, 0.0564F, 0.0308F, -0.7859F);
				cube_r37.cubeList.add(new ModelBox(cube_r37, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));
				LowerTeeth = new ModelRenderer(this);
				LowerTeeth.setRotationPoint(-18.1751F, 15.1387F, -4.0078F);
				jaw.addChild(LowerTeeth);
				setRotationAngle(LowerTeeth, -3.1416F, -1.5272F, 3.1416F);
				LowerTeeth.cubeList.add(new ModelBox(LowerTeeth, 60, 15, -2.1529F, -15.5F, -20.6758F, 4, 1, 0, 0.0F, false));
				LowerTeeth.cubeList.add(new ModelBox(LowerTeeth, 52, 15, -1.9348F, -15.5F, -15.6805F, 4, 1, 0, 0.0F, false));
				cube_r38 = new ModelRenderer(this);
				cube_r38.setRotationPoint(16.4522F, -15.25F, -18.291F);
				LowerTeeth.addChild(cube_r38);
				setRotationAngle(cube_r38, 0.0F, -1.5272F, 0.0F);
				cube_r38.cubeList.add(new ModelBox(cube_r38, 52, 14, -3.193F, -0.25F, 18.4824F, 5, 1, 0, 0.0F, false));
				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, 3.0F, -6.0F);
				eyes.cubeList.add(new ModelBox(eyes, 76, 0, -4.0F, -3.0F, -9.8F, 8, 2, 0, 0.0F, false));
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 3.0F, 0.0F);
				cube_r39 = new ModelRenderer(this);
				cube_r39.setRotationPoint(6.0F, 8.0F, 4.25F);
				body.addChild(cube_r39);
				cube_r39.cubeList.add(new ModelBox(cube_r39, 0, 31, -13.0F, -13.0F, 3.0F, 14, 12, 18, 0.0F, false));
				cube_r41 = new ModelRenderer(this);
				cube_r41.setRotationPoint(6.0F, 8.0F, -13.5F);
				body.addChild(cube_r41);
				setRotationAngle(cube_r41, -0.0873F, 0.0F, 0.0F);
				cube_r41.cubeList.add(new ModelBox(cube_r41, 0, 0, -15.0F, -16.0F, 5.0F, 18, 15, 16, 0.0F, false));
				tail[0] = new ModelRenderer(this);
				tail[0].setRotationPoint(0.0F, -2.0F, 25.0F);
				body.addChild(tail[0]);
				setRotationAngle(tail[0], -1.309F, 0.0F, 0.0F);
				tail[0].cubeList.add(new ModelBox(tail[0], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.0F, false));
				tail[1] = new ModelRenderer(this);
				tail[1].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[0].addChild(tail[1]);
				setRotationAngle(tail[1], 0.2618F, 0.0F, 0.0873F);
				tail[1].cubeList.add(new ModelBox(tail[1], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.15F, false));
				tail[2] = new ModelRenderer(this);
				tail[2].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[1].addChild(tail[2]);
				setRotationAngle(tail[2], 0.2618F, 0.0F, 0.0873F);
				tail[2].cubeList.add(new ModelBox(tail[2], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.3F, false));
				tail[3] = new ModelRenderer(this);
				tail[3].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[2].addChild(tail[3]);
				setRotationAngle(tail[3], 0.2618F, 0.0F, 0.0873F);
				tail[3].cubeList.add(new ModelBox(tail[3], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.45F, false));
				tail[4] = new ModelRenderer(this);
				tail[4].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[3].addChild(tail[4]);
				setRotationAngle(tail[4], -0.2618F, 0.0F, 0.0873F);
				tail[4].cubeList.add(new ModelBox(tail[4], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.6F, false));
				tail[5] = new ModelRenderer(this);
				tail[5].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[4].addChild(tail[5]);
				setRotationAngle(tail[5], -0.2618F, 0.0F, 0.0873F);
				tail[5].cubeList.add(new ModelBox(tail[5], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.75F, false));
				tail[6] = new ModelRenderer(this);
				tail[6].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[5].addChild(tail[6]);
				setRotationAngle(tail[6], -0.2618F, 0.0F, 0.0873F);
				tail[6].cubeList.add(new ModelBox(tail[6], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.9F, false));
				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(-4.75F, 4.0F, -1.0F);
				Joint7 = new ModelRenderer(this);
				Joint7.setRotationPoint(0.0F, -2.0F, -1.0F);
				leg1.addChild(Joint7);
				setRotationAngle(Joint7, 0.1745F, 0.0F, 0.1745F);
				cube_r42 = new ModelRenderer(this);
				cube_r42.setRotationPoint(2.0F, 12.0F, -11.25F);
				Joint7.addChild(cube_r42);
				setRotationAngle(cube_r42, -0.0436F, 0.0F, 0.0F);
				cube_r42.cubeList.add(new ModelBox(cube_r42, 28, 66, -8.0F, -12.0F, 7.0F, 6, 14, 8, 0.0F, false));
				Joint8 = new ModelRenderer(this);
				Joint8.setRotationPoint(-2.875F, 15.25F, 0.75F);
				Joint7.addChild(Joint8);
				setRotationAngle(Joint8, -0.517F, -0.0869F, -0.1515F);
				cube_r43 = new ModelRenderer(this);
				cube_r43.setRotationPoint(4.375F, 5.75F, -14.75F);
				Joint8.addChild(cube_r43);
				cube_r43.cubeList.add(new ModelBox(cube_r43, 0, 82, -7.0F, -8.0F, 11.0F, 5, 10, 5, 0.0F, false));
				Foot1 = new ModelRenderer(this);
				Foot1.setRotationPoint(-0.125F, 6.1875F, 0.4375F);
				Joint8.addChild(Foot1);
				setRotationAngle(Foot1, 0.3491F, 0.0F, 0.0F);
				cube_r44 = new ModelRenderer(this);
				cube_r44.setRotationPoint(6.5F, 2.0625F, -22.4375F);
				Foot1.addChild(cube_r44);
				cube_r44.cubeList.add(new ModelBox(cube_r44, 6, 39, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));
				cube_r45 = new ModelRenderer(this);
				cube_r45.setRotationPoint(8.0F, 2.0625F, -22.4375F);
				Foot1.addChild(cube_r45);
				cube_r45.cubeList.add(new ModelBox(cube_r45, 12, 42, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));
				cube_r46 = new ModelRenderer(this);
				cube_r46.setRotationPoint(5.0F, 2.0625F, -22.4375F);
				Foot1.addChild(cube_r46);
				cube_r46.cubeList.add(new ModelBox(cube_r46, 12, 45, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));
				cube_r47 = new ModelRenderer(this);
				cube_r47.setRotationPoint(4.5F, 1.8125F, -15.1875F);
				Foot1.addChild(cube_r47);
				cube_r47.cubeList.add(new ModelBox(cube_r47, 76, 74, -7.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, false));
				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(4.75F, 4.0F, -1.0F);
				Joint2 = new ModelRenderer(this);
				Joint2.setRotationPoint(0.0F, -2.0F, -1.0F);
				leg2.addChild(Joint2);
				setRotationAngle(Joint2, 0.1745F, 0.0F, -0.1745F);
				cube_r48 = new ModelRenderer(this);
				cube_r48.setRotationPoint(-2.0F, 12.0F, -11.25F);
				Joint2.addChild(cube_r48);
				setRotationAngle(cube_r48, -0.0436F, 0.0F, 0.0F);
				cube_r48.cubeList.add(new ModelBox(cube_r48, 28, 66, 2.0F, -12.0F, 7.0F, 6, 14, 8, 0.0F, true));
				Joint5 = new ModelRenderer(this);
				Joint5.setRotationPoint(2.875F, 15.25F, 0.75F);
				Joint2.addChild(Joint5);
				setRotationAngle(Joint5, -0.517F, 0.0869F, 0.1515F);
				cube_r49 = new ModelRenderer(this);
				cube_r49.setRotationPoint(-4.375F, 5.75F, -14.75F);
				Joint5.addChild(cube_r49);
				cube_r49.cubeList.add(new ModelBox(cube_r49, 0, 82, 2.0F, -8.0F, 11.0F, 5, 10, 5, 0.0F, true));
				Foot2 = new ModelRenderer(this);
				Foot2.setRotationPoint(0.125F, 6.1875F, 0.4375F);
				Joint5.addChild(Foot2);
				setRotationAngle(Foot2, 0.3491F, 0.0F, 0.0F);
				cube_r50 = new ModelRenderer(this);
				cube_r50.setRotationPoint(-6.5F, 2.0625F, -22.4375F);
				Foot2.addChild(cube_r50);
				cube_r50.cubeList.add(new ModelBox(cube_r50, 6, 39, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));
				cube_r51 = new ModelRenderer(this);
				cube_r51.setRotationPoint(-8.0F, 2.0625F, -22.4375F);
				Foot2.addChild(cube_r51);
				cube_r51.cubeList.add(new ModelBox(cube_r51, 12, 42, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));
				cube_r52 = new ModelRenderer(this);
				cube_r52.setRotationPoint(-5.0F, 2.0625F, -22.4375F);
				Foot2.addChild(cube_r52);
				cube_r52.cubeList.add(new ModelBox(cube_r52, 12, 45, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));
				cube_r53 = new ModelRenderer(this);
				cube_r53.setRotationPoint(-4.5F, 1.8125F, -15.1875F);
				Foot2.addChild(cube_r53);
				cube_r53.cubeList.add(new ModelBox(cube_r53, 76, 74, 2.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, true));
				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(-5.5F, 8.0F, 21.5F);
				Joint3 = new ModelRenderer(this);
				Joint3.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg3.addChild(Joint3);
				setRotationAngle(Joint3, -1.5708F, 0.0F, 0.0F);
				cube_r54 = new ModelRenderer(this);
				cube_r54.setRotationPoint(2.25F, 17.5F, -2.75F);
				Joint3.addChild(cube_r54);
				setRotationAngle(cube_r54, 0.7854F, 0.0F, 0.0F);
				cube_r54.cubeList.add(new ModelBox(cube_r54, 0, 60, -8.0F, -14.3536F, 10.182F, 6, 14, 8, 0.2F, false));
				Joint4 = new ModelRenderer(this);
				Joint4.setRotationPoint(1.25F, 17.5F, -2.75F);
				Joint3.addChild(Joint4);
				cube_r55 = new ModelRenderer(this);
				cube_r55.setRotationPoint(-10.0F, 27.5103F, 8.3355F);
				Joint4.addChild(cube_r55);
				setRotationAngle(cube_r55, -1.1781F, 0.0F, 0.0F);
				cube_r55.cubeList.add(new ModelBox(cube_r55, 56, 74, 3.0F, -24.6324F, -38.7863F, 5, 11, 5, 0.0F, false));
				Foot3 = new ModelRenderer(this);
				Foot3.setRotationPoint(-4.5F, -16.9625F, 16.4375F);
				Joint4.addChild(Foot3);
				setRotationAngle(Foot3, 1.5708F, 0.0F, 0.0F);
				cube_r56 = new ModelRenderer(this);
				cube_r56.setRotationPoint(6.5F, 2.0625F, -22.4375F);
				Foot3.addChild(cube_r56);
				cube_r56.cubeList.add(new ModelBox(cube_r56, 6, 39, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));
				cube_r57 = new ModelRenderer(this);
				cube_r57.setRotationPoint(8.0F, 2.0625F, -22.4375F);
				Foot3.addChild(cube_r57);
				cube_r57.cubeList.add(new ModelBox(cube_r57, 12, 42, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));
				cube_r58 = new ModelRenderer(this);
				cube_r58.setRotationPoint(5.0F, 2.0625F, -22.4375F);
				Foot3.addChild(cube_r58);
				cube_r58.cubeList.add(new ModelBox(cube_r58, 12, 45, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));
				cube_r59 = new ModelRenderer(this);
				cube_r59.setRotationPoint(4.5F, 1.8125F, -15.1875F);
				Foot3.addChild(cube_r59);
				cube_r59.cubeList.add(new ModelBox(cube_r59, 76, 74, -7.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, false));
				leg4 = new ModelRenderer(this);
				leg4.setRotationPoint(5.5F, 8.0F, 21.5F);
				Joint6 = new ModelRenderer(this);
				Joint6.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg4.addChild(Joint6);
				setRotationAngle(Joint6, -1.5708F, 0.0F, 0.0F);
				cube_r60 = new ModelRenderer(this);
				cube_r60.setRotationPoint(-2.25F, 17.5F, -2.75F);
				Joint6.addChild(cube_r60);
				setRotationAngle(cube_r60, 0.7854F, 0.0F, 0.0F);
				cube_r60.cubeList.add(new ModelBox(cube_r60, 0, 60, 2.0F, -14.3536F, 10.182F, 6, 14, 8, 0.2F, true));
				Joint9 = new ModelRenderer(this);
				Joint9.setRotationPoint(-1.25F, 17.5F, -2.75F);
				Joint6.addChild(Joint9);
				cube_r61 = new ModelRenderer(this);
				cube_r61.setRotationPoint(10.0F, 27.5103F, 8.3355F);
				Joint9.addChild(cube_r61);
				setRotationAngle(cube_r61, -1.1781F, 0.0F, 0.0F);
				cube_r61.cubeList.add(new ModelBox(cube_r61, 56, 74, -8.0F, -24.6324F, -38.7863F, 5, 11, 5, 0.0F, true));
				Foot4 = new ModelRenderer(this);
				Foot4.setRotationPoint(4.5F, -16.9625F, 16.4375F);
				Joint9.addChild(Foot4);
				setRotationAngle(Foot4, 1.5708F, 0.0F, 0.0F);
				cube_r62 = new ModelRenderer(this);
				cube_r62.setRotationPoint(-6.5F, 2.0625F, -22.4375F);
				Foot4.addChild(cube_r62);
				cube_r62.cubeList.add(new ModelBox(cube_r62, 6, 39, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));
				cube_r63 = new ModelRenderer(this);
				cube_r63.setRotationPoint(-8.0F, 2.0625F, -22.4375F);
				Foot4.addChild(cube_r63);
				cube_r63.cubeList.add(new ModelBox(cube_r63, 12, 42, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));
				cube_r64 = new ModelRenderer(this);
				cube_r64.setRotationPoint(-5.0F, 2.0625F, -22.4375F);
				Foot4.addChild(cube_r64);
				cube_r64.cubeList.add(new ModelBox(cube_r64, 12, 45, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));
				cube_r65 = new ModelRenderer(this);
				cube_r65.setRotationPoint(-4.5F, 1.8125F, -15.1875F);
				Foot4.addChild(cube_r65);
				cube_r65.cubeList.add(new ModelBox(cube_r65, 76, 74, 2.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, true));
				for (int j = 1; j < tailSwayX.length; j++) {
					tailSwayX[j] = (rand.nextFloat() * 0.2618F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayZ[j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayY[j] = (rand.nextFloat() * 0.1745F + 0.1745F);
				}
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				float scale = ((EC)entity).getPower();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * scale, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				head.render(f5);
				body.render(f5);
				leg1.render(f5);
				leg2.render(f5);
				leg3.render(f5);
				leg4.render(f5);
				eyes.render(f5);
				tail[0].render(f5);
				GlStateManager.popMatrix();
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				f *= 2.0f / e.height;
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				this.eyes.rotateAngleY = this.head.rotateAngleY = f3 / (180F / (float) Math.PI);
				this.eyes.rotateAngleX = this.head.rotateAngleX = f4 / (180F / (float) Math.PI);
				this.leg1.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
				this.leg2.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
				this.leg3.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
				this.leg4.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
				for (int j = 1; j < tailSwayX.length; j++) {
					tail[j].rotateAngleX = MathHelper.sin((f2 - j) * 0.1F) * tailSwayX[j];
					tail[j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.1F) * tailSwayZ[j];
					tail[j].rotateAngleY = MathHelper.sin((f2 - j) * 0.1F) * tailSwayY[j];
				}
			}
		}
	}
}
