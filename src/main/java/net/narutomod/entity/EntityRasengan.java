
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.init.SoundEvents;

import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.item.ItemSenjutsu;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.item.ItemJutsu;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityRasengan extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 147;
	public static final int ENTITYID_RANGED = 148;

	public EntityRasengan(ElementsNarutomodMod instance) {
		super(instance, 405);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "rasengan"), ENTITYID).name("rasengan").tracker(64, 3, true).build());
	}

	public static class EC extends EntityScalableProjectile.Base implements ProcedureSync.CPacketVec3d.IHandler {
		private static final DataParameter<Integer> OWNER_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private final int growTime = 30;
		private ItemStack usingItemstack;
		private float fullScale;
		private Vec3d angles;
		private DamageSource damageSource;

		public EC(World a) {
			super(a);
			this.setOGSize(0.35F, 0.35F);
			this.isImmuneToFire = true;
			this.damageSource = ItemJutsu.NINJUTSU_DAMAGE;
		}

		public EC(EntityLivingBase shooter, float scale, ItemStack stack) {
			super(shooter);
			this.setOGSize(0.35F, 0.35F);
			this.setEntityScale(0.1f);
			this.setOwner(shooter);
			this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, 0.0f, 0.0f);
			this.fullScale = scale;
			this.usingItemstack = stack;
			this.isImmuneToFire = true;
			this.damageSource = ItemJutsu.causeJutsuDamage(this, shooter);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(OWNER_ID, Integer.valueOf(-1));
		}

		@Nullable
		public EntityLivingBase getOwner() {
			 Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(OWNER_ID)).intValue());
			 return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		protected void setOwner(EntityLivingBase entity) {
			this.getDataManager().set(OWNER_ID, Integer.valueOf(entity.getEntityId()));
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				if (this.shootingEntity != null) {
					ProcedureSync.EntityNBTTag.removeAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose);
				}
				if (this.usingItemstack != null) {
					ItemStack stack = this.usingItemstack;
					if (this.shootingEntity instanceof EntityPlayer) {
						stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)this.shootingEntity, stack.getItem());
					}
					if (stack != null && stack.hasTagCompound()) {
						stack.getTagCompound().removeTag("RasenganSize");
						stack.getTagCompound().removeTag(Jutsu.ID_KEY);
					}
				}
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.ticksAlive == 1 && this.shootingEntity != null) {
				ProcedureSync.EntityNBTTag.setAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose, true);
			}
			if (!this.world.isRemote && this.ticksAlive <= this.growTime) {
				this.setEntityScale(this.fullScale * this.ticksAlive / this.growTime);
			}
			if (this.world.isRemote && this.angles != null) {
				ProcedureSync.CPacketVec3d.sendToServer(this, this.angles);
			}
			if (this.shootingEntity != null) {
				this.setPositionToHand();
			}
			if (this.ticksAlive % 15 == 0) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rasengan_during")), 0.2f, 1.0f);
			}
			if (!this.world.isRemote) {
				this.breakBlocks(this.world.getCollisionBoxes(null, this.getEntityBoundingBox()));
			}
			if (!this.world.isRemote && (this.shootingEntity == null || (!this.shootingEntity.getHeldItemMainhand().isEmpty() && 
			  !(this.shootingEntity.getHeldItemMainhand().getItem() instanceof ItemJutsu.Base)))) {
				this.setDead();
			}
		}

		private void breakBlocks(List<AxisAlignedBB> list) {
			if (!list.isEmpty()) {
				for (AxisAlignedBB aabb : list) {
					ProcedureUtils.breakBlockAndDropWithChance(this.world, new BlockPos(ProcedureUtils.BB.getCenter(aabb)), 5.0F, 1.0F, 0.3F);
				}
			}
			//if (!list.isEmpty() && this.shootingEntity != null && this.shootingEntity.isSwingInProgress)
			//	this.setDead();
		}

		private void setPositionToHand() {
			EntityLivingBase entity = this.shootingEntity;
			/*Vec3d vec3d = entity.isSwingInProgress 
			 ? entity.getLookVec().scale(2d + 3d * Math.sin(entity.swingProgress * Math.PI))
			   .addVector(0d, entity.getEyeHeight() + this.height, 0d)
			 : ProcedureUtils.rotateRoll(new Vec3d(0.0F, -0.75F - this.height, 0.0F), this.angles != null ? (float)-this.angles.z : 0.0F)
			   .rotateYaw(this.angles != null ? (float)-this.angles.y : 0.0F)
			   .rotatePitch(this.angles != null ? (float)-this.angles.x : 0.314F)
			   .addVector(0.0625F * -5, 1.5F, 0.0F)
			   .rotateYaw(-entity.renderYawOffset * (float)(Math.PI / 180d));
			this.setPosition(entity.posX + vec3d.x, entity.posY + vec3d.y, entity.posZ + vec3d.z);*/
			if (entity.isSwingInProgress) {
				Vec3d vec = entity.getLookVec().scale(2d + 3d * Math.sin(entity.swingProgress * Math.PI))
				 .add(entity.getPositionEyes(1.0f));
				this.setPosition(vec.x, vec.y, vec.z);
			} else if (this.angles != null) {
				this.setPosition(this.angles.x, this.angles.y, this.angles.z);
			} else {
				//Vec3d vec = new Vec3d(0.0F, -0.75F - this.height, 0.0F).rotatePitch(0.314F).addVector(0.0625F * -5, 1.5F, 0.0F)
				// .rotateYaw(-entity.renderYawOffset * (float)(Math.PI / 180d));
				Vec3d vec = Vec3d.ZERO;
				this.setPosition(entity.posX + vec.x, entity.posY + vec.y, entity.posZ + vec.z);
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public boolean canBePushed() {
			return true;
		}

		private boolean bunshinHasSameSummoner(Entity entityIn) {
			if (this.shootingEntity instanceof EntityKageBunshin.EC) {
				EntityLivingBase summoner = ((EntityKageBunshin.EC)this.shootingEntity).getSummoner();
				if (summoner == null) {
					return false;
				} else if (summoner.equals(entityIn)) {
					return true;
				} else if (entityIn instanceof EntityKageBunshin.EC) {
					if (summoner.equals(((EntityKageBunshin.EC)entityIn).getSummoner())) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public void applyEntityCollision(Entity entityIn) {
			if (this.ticksAlive > this.growTime && this.shootingEntity != null
			 && !entityIn.equals(this.shootingEntity) && !this.bunshinHasSameSummoner(entityIn)) {
				if (entityIn.attackEntityFrom(this.damageSource, 10f + this.fullScale * this.fullScale * 20f)) {
					this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0F, this.rand.nextFloat() * 0.5F + 0.5F);
					Vec3d vec = ProcedureUtils.pushEntity(this.shootingEntity, entityIn, 20d, 2f);
					Vec3d vec1 = this.shootingEntity.getLookVec().add(this.shootingEntity.getPositionEyes(1.0f));
					for (int i = 1; i <= 100; i++) {
						double d = (double)i * vec.lengthVector() * 0.05d;
						Vec3d vec2 = vec.normalize().scale(d);
						Particles.spawnParticle(this.world, Particles.Types.WHIRLPOOL, vec1.x, vec1.y, vec1.z, 1,
						 0d, 0d, 0d, vec2.x, vec2.y, vec2.z, 0x80b9fffd, (int)(d * 20), (int)d, 0xF0);
					}
				}
				this.setDead();
			}
		}

		@Override
		public void handleClientPacket(Vec3d vec) {
			this.angles = vec;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "RasenganEntityId";
			
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = stack.hasTagCompound() ? entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY)) : null;
				if (entity1 instanceof EC && entity instanceof EntityPlayer) {
					entity1.setDead();
				} else if ((stack.getItem() == ItemNinjutsu.block && power >= 0.5f)
				 || (stack.getItem() == ItemSenjutsu.block && power >= 3.0f)) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
					  .getObject(new ResourceLocation("narutomod:rasengan_start")), SoundCategory.NEUTRAL, 1.0F, 1.0F);
					EC entity2 = new EC(entity, power, stack);
					if (stack.getItem() == ItemSenjutsu.block) {
						entity2.damageSource = ItemJutsu.causeSenjutsuDamage(entity2, entity);
					}
					entity.world.spawnEntity(entity2);
					stack.getTagCompound().setInteger(ID_KEY, entity2.getEntityId());
					stack.getTagCompound().setFloat("RasenganSize", power);
					return true;
				}
				return false;
			}

			@Override
			public boolean isActivated(ItemStack stack) {
				return this.getPower(stack) > 0.0f;
			}

			@Override
			public float getPower(ItemStack stack) {
				return stack.hasTagCompound() ? stack.getTagCompound().getFloat("RasenganSize") : 0.0f;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> {
				return new RenderRasengan(renderManager);
			});
		}

	    @SideOnly(Side.CLIENT)
	    public static void rotateArmIn1stPerson(Entity viewer, float partialTicks) {
	    	if (viewer instanceof EntityPlayerSP) {
		        EntityPlayerSP entityplayersp = (EntityPlayerSP)viewer;
		        float swingProgress = entityplayersp.getSwingProgress(partialTicks);
		        float f0 = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * partialTicks;
		        float f01 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * partialTicks;
		        GlStateManager.rotate((entityplayersp.rotationPitch - f0) * 0.1F, 1.0F, 0.0F, 0.0F);
		        GlStateManager.rotate((entityplayersp.rotationYaw - f01) * 0.1F, 0.0F, 1.0F, 0.0F);
		        float f = 1.0F;
		        float f1 = MathHelper.sqrt(swingProgress);
		        float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
		        float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
		        float f4 = -0.4F * MathHelper.sin(swingProgress * (float)Math.PI);
		        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + 0F * -0.6F, f4 + -0.71999997F);
		        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
		        float f5 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
		        float f6 = MathHelper.sin(f1 * (float)Math.PI);
		        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
		        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
		        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
		        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
		        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
	    	}
	    }
	
		@SideOnly(Side.CLIENT)
		public class RenderRasengan extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/longcube_white.png");
			private final Random rand = new Random();
			protected ModelBase mainModel;
	
			public RenderRasengan(RenderManager renderManager) {
				super(renderManager);
				this.mainModel = new ModelRasengan();
				shadowSize = 0.1f;
			}
	
			private Vec3d transform3rdPerson(Vec3d startvec, Vec3d angles, EntityLivingBase entity, float pt) {
				return ProcedureUtils.rotateRoll(startvec, (float)-angles.z).rotatePitch((float)-angles.x).rotateYaw((float)-angles.y)
				   .addVector(0.0586F * -6F, 1.02F-(entity.isSneaking()?0.3f:0f), 0.0F)
				   .rotateYaw((-entity.prevRenderYawOffset - (entity.renderYawOffset - entity.prevRenderYawOffset) * pt) * (float)(Math.PI / 180d))
				   .addVector(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt);
			}
	
			@Override
			public void doRender(EC entity, double x, double y, double z, float f, float partialTicks) {
				this.bindEntityTexture(entity);
				EntityLivingBase owner = entity.getOwner();
				float scale = entity.getEntityScale();
	            GlStateManager.pushMatrix();
	            if (owner != null) {
					Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
					ModelBiped model = (ModelBiped)((RenderLivingBase)this.renderManager.getEntityRenderObject(owner)).getMainModel();
					this.forceRightArmBowPose(model, owner, partialTicks);
					Vec3d ballVec = this.transform3rdPerson(new Vec3d(0.0d, -0.5825d - entity.height * 0.5d, 0.0d),
					 new Vec3d(model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ),
					 owner, partialTicks).addVector(0.0d, 0.275d - entity.height * 0.5d, 0.0d);
					if (viewer.equals(owner) || !(owner instanceof EntityPlayer)) {
						entity.angles = ballVec;
						//ProcedureSync.CPacketVec3d.sendToServer(entity, entity.angles);
					}
		            if (viewer.equals(owner) && this.renderManager.options.thirdPersonView == 0) {
			            GlStateManager.translate(0F, 1.925F, 0F);
			            GlStateManager.rotate(-this.interpolateRotation(owner.prevRotationYaw, owner.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
			            GlStateManager.rotate(owner.prevRotationPitch + (owner.rotationPitch - owner.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
			            GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);
		            	rotateArmIn1stPerson(owner, partialTicks);
			            model.postRenderArm(0.0625F * 0.9375F, EnumHandSide.RIGHT);
			            GlStateManager.translate(-0.125F, entity.height - 0.025F, 0.0F);
		            } else {
						this.renderParticles(entity.world, ballVec.addVector(0.0d, entity.height/2, 0.0d), scale);
						x = owner.lastTickPosX + (owner.posX - owner.lastTickPosX) * partialTicks - this.renderManager.viewerPosX;
						y = owner.lastTickPosY + (owner.posY - owner.lastTickPosY) * partialTicks - this.renderManager.viewerPosY;
						z = owner.lastTickPosZ + (owner.posZ - owner.lastTickPosZ) * partialTicks - this.renderManager.viewerPosZ;
			            GlStateManager.translate(x, y, z);
			            GlStateManager.rotate(-this.interpolateRotation(owner.prevRenderYawOffset, owner.renderYawOffset, partialTicks), 0.0F, 1.0F, 0.0F);
			            GlStateManager.translate(0.0F, 1.4F, 0.0F);
			            GlStateManager.rotate(180F, 1.0F, 0.0F, 0.0F);
			            if (owner.isSneaking()) 
			                GlStateManager.translate(0.0F, 0.2F, 0.0F);
			            model.postRenderArm(0.0625F * 0.9375F, EnumHandSide.RIGHT);
			            GlStateManager.translate(-0.05F, entity.height + 0.125F, 0.0F);
		            }
				}
				GlStateManager.translate(0f, 0.5F - 0.175F * scale, 0f);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.rotate(entity.ticksExisted * 30.0F, 1.0F, 1.0F, 0.0F);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				for (int i = 0; i < 10; i++) {
					GlStateManager.rotate(rand.nextFloat() * 30f, 0f, 1f, 0f);
					GlStateManager.rotate(rand.nextFloat() * 30f, 1f, 1f, 0f);
					this.mainModel.render(entity, 0.0F, 0.0F, partialTicks + entity.ticksExisted, 0.0F, 0.0F, 0.0625F);
				}
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
	
			private void forceRightArmBowPose(ModelBiped model, EntityLivingBase owner, float partialTicks) {
	            float f = this.interpolateRotation(owner.prevRenderYawOffset, owner.renderYawOffset, partialTicks);
	            float f1 = this.interpolateRotation(owner.prevRotationYawHead, owner.rotationYawHead, partialTicks);
	            float f2 = (f1 - f) * 0.017453292F;
	            float f7 = (owner.prevRotationPitch + (owner.rotationPitch - owner.prevRotationPitch) * partialTicks) * 0.017453292F;
				model.bipedRightArm.rotateAngleY = -0.1F + f2;
				model.bipedRightArm.rotateAngleX = -((float)Math.PI / 2F) + f7;
			}
	
			private void renderParticles(World worldIn, Vec3d vec, float size) {
				for (int i = 0; i < 10; i++) {
					Particles.spawnParticle(worldIn, Particles.Types.SMOKE, vec.x, vec.y, vec.z,
					 1, 0d, 0.02d, 0d, 0.2d * worldIn.rand.nextGaussian(), 0.2d * worldIn.rand.nextGaussian(), 
					 0.2d * worldIn.rand.nextGaussian(), 0x10FFFFFF, (int)(size * 5), 0);
				}
			}
	
			private float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
				return prevYawOffset + ProcedureUtils.Vec2f.wrapDegrees(yawOffset - prevYawOffset) * partialTicks;
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return texture;
			}
		}
	
		// Made with Blockbench 3.6.5
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelRasengan extends ModelBase {
			private final ModelRenderer core;
			private final ModelRenderer bone;
			private final ModelRenderer bone8;
			private final ModelRenderer bone7;
			private final ModelRenderer bone6;
			private final ModelRenderer shell;
			private final ModelRenderer bone5;
			private final ModelRenderer bone4;
			private final ModelRenderer bone3;
			private final ModelRenderer bone2;
		
			public ModelRasengan() {
				textureWidth = 32;
				textureHeight = 32;
		
				core = new ModelRenderer(this);
				core.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				core.addChild(bone);
				bone.cubeList.add(new ModelBox(bone, 0, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
				bone.cubeList.add(new ModelBox(bone, 0, 0, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				bone.cubeList.add(new ModelBox(bone, 0, 0, -1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F, false));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, 0.0F, 0.0F);
				core.addChild(bone8);
				setRotationAngle(bone8, 0.0F, 0.0F, 0.7854F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
				bone8.cubeList.add(new ModelBox(bone8, 0, 0, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				bone8.cubeList.add(new ModelBox(bone8, 0, 0, -1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 0.0F, 0.0F);
				core.addChild(bone7);
				setRotationAngle(bone7, 0.0F, -0.7854F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
				bone7.cubeList.add(new ModelBox(bone7, 0, 0, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				bone7.cubeList.add(new ModelBox(bone7, 0, 0, -1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
				core.addChild(bone6);
				setRotationAngle(bone6, -0.7854F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F, false));
		
				shell = new ModelRenderer(this);
				shell.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
				shell.addChild(bone5);
				bone5.cubeList.add(new ModelBox(bone5, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
				shell.addChild(bone4);
				setRotationAngle(bone4, 0.0F, 0.0F, 0.7854F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
				shell.addChild(bone3);
				setRotationAngle(bone3, 0.0F, -0.7854F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
				shell.addChild(bone2);
				setRotationAngle(bone2, -0.7854F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.color(1f, 1f, 1f, 0.3f);
				core.render(f5);
				GlStateManager.color(0.66F, 0.87F, 1.0F, 0.3F);
				shell.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
			}
		}
	}
}
