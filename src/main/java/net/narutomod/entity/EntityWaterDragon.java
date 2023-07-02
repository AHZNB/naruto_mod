
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockLiquid;

import net.narutomod.item.ItemSuiton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWaterDragon extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 366;
	public static final int ENTITYID_RANGED = 367;

	public EntityWaterDragon(ElementsNarutomodMod instance) {
		super(instance, 728);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "water_dragon"), ENTITYID).name("water_dragon").tracker(64, 3, true).build());
	}

	public static class EC extends EntityScalableProjectile.Base {
		private final int wait = 60;
		private Vec3d shootVec;
		private float prevHeadYaw;
		private float prevHeadPitch;
		//public float prevLimbSwingAmount;
		//public float limbSwingAmount;
		//public float limbSwing;
		private Vec3d lastVec;
		private double yOrigin;
		private final List<ProcedureUtils.Vec2f> partRot = Lists.newArrayList(
			new ProcedureUtils.Vec2f(0.0f, 0.0f), new ProcedureUtils.Vec2f(0.0f, 30.0f), new ProcedureUtils.Vec2f(0.0f, 30.0f),
			new ProcedureUtils.Vec2f(0.0f, 30.0f), new ProcedureUtils.Vec2f(0.0f, 30.0f), new ProcedureUtils.Vec2f(0.0f, -15.0f),
			new ProcedureUtils.Vec2f(0.0f, -15.0f), new ProcedureUtils.Vec2f(0.0f, 0.0f)
		);

		public EC(World a) {
			super(a);
			this.setOGSize(1.0F, 1.0F);
		}

		public EC(EntityLivingBase shooter, float power) {
			super(shooter);
			this.setOGSize(1.0F, 1.0F);
			this.setEntityScale(power);
			this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
			this.yOrigin = shooter.posY;
		}

		public EC(EntityLivingBase shooter, double x, double y, double z, float power) {
			this(shooter, power);
			this.shootVec = new Vec3d(x, y, z);
		}

		private void setWaitPosition() {
			if (this.shootVec != null) {
				ProcedureUtils.Vec2f v2f = ProcedureUtils.getYawPitchFromVec(this.shootVec);
				this.setRotation(v2f.x, v2f.y);
			} else if (this.shootingEntity != null) {
				Vec3d vec = this.shootingEntity instanceof EntityLiving && ((EntityLiving)this.shootingEntity).getAttackTarget() != null
				 ? ((EntityLiving)this.shootingEntity).getAttackTarget().getPositionVector().subtract(this.getPositionVector())
				 : ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d).hitVec.subtract(this.getPositionVector());
				ProcedureUtils.Vec2f v2f = ProcedureUtils.getYawPitchFromVec(vec);
				this.setRotation(v2f.x, v2f.y);
			}
			this.motionY = this.ticksAlive <= this.wait / 2 ? 3d * this.getEntityScale() / (double)this.wait * 2d : 0.0d;
		}

		/*private void updateLimbSwing() {
			this.prevLimbSwingAmount = this.limbSwingAmount;
	        double d5 = this.posX - this.prevPosX;
	        double d7 = this.posZ - this.prevPosZ;
	        double d9 = this.posY - this.prevPosY;
	        float f10 = MathHelper.sqrt(d5 * d5 + d9 * d9 + d7 * d7) * 4.0F;
	        if (f10 > 1.0F) {
	            f10 = 1.0F;
	        }
	        this.limbSwingAmount += (f10 - this.limbSwingAmount) * 0.4F;
	        this.limbSwing += this.limbSwingAmount;
		}*/

		@Override
		public void onUpdate() {
			if (this.prevHeadYaw == 0.0f && this.prevHeadPitch == 0.0f) {
				this.prevHeadYaw = this.rotationYaw;
				this.prevHeadPitch = this.rotationPitch;
			}
			super.onUpdate();
			if (!this.world.isRemote && (this.ticksAlive > 100 || this.shootingEntity == null || !this.shootingEntity.isEntityAlive())) {
				this.setDead();
			} else {
				if (this.ticksAlive <= this.wait) {
					this.lastVec = this.getPositionVector();
					this.setWaitPosition();
					//this.setEntityScale(this.fullScale * MathHelper.clamp((float)this.ticksAlive / (float) this.wait, 0.1F, 1.0F));
				} else if (!this.isLaunched()) {
					if (this.shootVec != null) {
						this.shoot(this.shootVec.x, this.shootVec.y, this.shootVec.z, 0.95f, 0f);
					} else if (this.shootingEntity != null) {
						Vec3d vec = this.shootingEntity instanceof EntityLiving && ((EntityLiving)this.shootingEntity).getAttackTarget() != null
						 ? ((EntityLiving)this.shootingEntity).getAttackTarget().getPositionVector().subtract(this.getPositionVector())
						 : ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d).hitVec.subtract(this.getPositionVector());
						this.shoot(vec.x, vec.y, vec.z, 0.95f, 0f);
					}
				}
				this.updateSegments();
				//this.updateLimbSwing();
				this.prevHeadYaw = this.rotationYaw;
				this.prevHeadPitch = this.rotationPitch;
			}
		}

		public void updateSegments() {
			Vec3d cposvec = this.getPositionVector();
			float slength = this.getEntityScale() * 11.0F * 0.0625F;
			ProcedureUtils.Vec2f vec = new ProcedureUtils.Vec2f(this.rotationYaw, this.rotationPitch)
			 .subtract(this.prevRotationYaw, this.prevHeadPitch);
			Vec3d vec4 = cposvec.subtract(this.lastVec);
			double d4 = vec4.lengthVector();
//String s = ">>> ["+(this.world.isRemote?"client":"server")+"], vec="+vec+", moved:"+d4;
			if (d4 >= slength && this.ticksAlive > this.wait) {
				this.partRot.add(0, vec);
				int i = 1;
				for ( ; i < (int)(d4 / slength); i++) {
					this.partRot.add(0, ProcedureUtils.Vec2f.ZERO);
				}
				this.lastVec = vec4.normalize().scale(slength * i).add(this.lastVec);
			} else {
				this.partRot.set(0, this.partRot.get(0).add(vec));
			}
//System.out.println(s+", partRot:"+this.partRot);
		}

		@Override
		public void renderParticles() {
			if (this.world instanceof WorldServer) {
				if (this.isLaunched()) {
					((WorldServer)this.world).spawnParticle(EnumParticleTypes.WATER_DROP, this.posX,
					 this.posY + this.height/2, this.posZ, 200, this.width/2, this.height/2, this.width/2, 0);
				} else if (this.lastVec != null) {
					Vec3d vec = new Vec3d(0d, 0d, -1.75d * this.getEntityScale())
					 .rotateYaw(-this.rotationYaw * 0.0174532925f).addVector(this.lastVec.x, this.yOrigin, this.lastVec.z);
					((WorldServer)this.world).spawnParticle(EnumParticleTypes.WATER_WAKE, vec.x, vec.y, vec.z, 
					  this.ticksAlive * 5, this.width/2, this.height/2, this.width/2, 0.05);
				}
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && result.entityHit.equals(this.shootingEntity))
				return;
			if (!this.world.isRemote) {
				float size = this.getEntityScale();
				this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, 5.0F * size, false,
				  net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity));
				ProcedureAoeCommand.set(this, 0.0D, 3.0D).exclude(this.shootingEntity)
				  .damageEntities(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), 20f * size);
				Map<BlockPos, IBlockState> map = Maps.newHashMap();
				for (BlockPos pos : ProcedureUtils.getAllAirBlocks(this.world, this.getEntityBoundingBox().contract(0d, this.height-1, 0d))) {
					map.put(pos, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(1)));
				}
				new net.narutomod.event.EventSetBlocks(this.world, map, 0, 10, false, false);
				this.setDead();
			}
		}

		@Override
		protected void checkOnGround() {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 1.0f && entity.onGround
				 && (entity.isOverWater() || Chakra.pathway(entity).consume(ItemSuiton.WATERDRAGON.chakraUsage * 2))) {
				 	this.createJutsu(entity, power);
					return true;
				}
				return false;
			}

			public void createJutsu(EntityLivingBase entity, float power) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
				  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:suiton_suiryuudan"))),
				  net.minecraft.util.SoundCategory.NEUTRAL, 5, 1f);
				entity.world.spawnEntity(new EC(entity, power));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderDragon(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderDragon extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/dragon_blue.png");
			private final ResourceLocation texture2 = new ResourceLocation("narutomod:textures/gas256.png");
			private final ModelDragonHead model = new ModelDragonHead();

			public RenderDragon(RenderManager renderManager) {
				super(renderManager);
				//this.model = new ModelDragonHead();
				this.shadowSize = 0.1F;
			}

			@Override
			public boolean shouldRender(EC livingEntity, net.minecraft.client.renderer.culling.ICamera camera,
										double camX, double camY, double camZ) {
				return true;
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float yaw, float pt) {
				float age = (float)entity.ticksExisted + pt;
				//float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * pt;
				//float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - pt);
				float f5 = 0.0f;
				float f6 = 0.0f;
				float f1 = -entity.prevRotationYaw - MathHelper.wrapDegrees(entity.rotationYaw - entity.prevRotationYaw) * pt;
				float f2 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt;
				boolean flag = entity.ticksAlive <= entity.wait;
				float scale = entity.getEntityScale();
				this.model.setRotationAngles(f6, f5, age, 0f, 0f, 0.0625F, entity);
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) x, (float) y + scale, (float) z);
				GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(f2 - 180F, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.bindTexture(texture2);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.translate(0.0F, age * 0.01F, 0.0F);
				GlStateManager.matrixMode(5888);
				this.model.teethUpper.showModel = false;
				this.model.teethLower.showModel = false;
				this.model.eyes.showModel = false;
				GlStateManager.color(0.04F, 0.325F, 0.733F, 1.0F);
				this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F * 0.99F);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				this.bindEntityTexture(entity);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.8F);
				this.model.teethUpper.showModel = true;
				this.model.teethLower.showModel = true;
				this.model.eyes.showModel = true;
				this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return texture;
			}
		}

		// Made with Blockbench 3.5.4
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelDragonHead extends ModelBase {
			private final ModelRenderer head;
			private final ModelRenderer bone;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer teethUpper;
			private final ModelRenderer teethLower;
			private final ModelRenderer jaw;
			private final ModelRenderer hornRight;
			private final ModelRenderer hornRight0;
			private final ModelRenderer hornRight1;
			private final ModelRenderer hornRight2;
			private final ModelRenderer hornRight3;
			private final ModelRenderer hornRight4;
			private final ModelRenderer hornLeft;
			private final ModelRenderer hornLeft0;
			private final ModelRenderer hornLeft1;
			private final ModelRenderer hornLeft2;
			private final ModelRenderer hornLeft3;
			private final ModelRenderer hornLeft4;
			private final ModelRenderer[] whiskerLeft = new ModelRenderer[6];
			private final ModelRenderer[] whiskerRight = new ModelRenderer[6];
			private final ModelRenderer[] spine = new ModelRenderer[100];
			private final ModelRenderer eyes;

			public ModelDragonHead() {
				textureWidth = 256;
				textureHeight = 256;

				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 0.0F, 0.0F);
				head.cubeList.add(new ModelBox(head, 176, 44, -6.0F, 6.0F, -26.0F, 12, 5, 16, 1.0F, false));
				head.cubeList.add(new ModelBox(head, 112, 30, -8.0F, -1.0F, -11.0F, 16, 16, 16, 1.0F, false));
				head.cubeList.add(new ModelBox(head, 112, 0, -5.0F, 5.0F, -26.0F, 2, 2, 4, 1.0F, false));
				head.cubeList.add(new ModelBox(head, 112, 0, 3.0F, 5.0F, -26.0F, 2, 2, 4, 1.0F, true));

				teethUpper = new ModelRenderer(this);
				teethUpper.setRotationPoint(0.0F, 24.0F, 0.0F);
				head.addChild(teethUpper);
				teethUpper.cubeList.add(new ModelBox(teethUpper, 152, 146, -6.0F, -12.0F, -26.0F, 12, 2, 16, 0.5F, false));

				bone = new ModelRenderer(this);
				bone.setRotationPoint(9.0F, 7.0F, -11.0F);
				head.addChild(bone);
				setRotationAngle(bone, 0.0F, -0.7854F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 0, 200, 0.0F, -8.0F, 0.0F, 8, 16, 0, 0.0F, false));

				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-9.0F, 7.0F, -11.0F);
				head.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.7854F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 200, -8.0F, -8.0F, 0.0F, 8, 16, 0, 0.0F, true));

				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, -2.0F, -11.0F);
				head.addChild(bone3);
				setRotationAngle(bone3, -0.8727F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 50, -8.0F, -10.0F, 0.0F, 16, 10, 0, 0.0F, false));

				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, 11.0F, -9.0F);
				head.addChild(jaw);
				jaw.cubeList.add(new ModelBox(jaw, 176, 65, -6.0F, 0.0F, -16.75F, 12, 4, 16, 1.0F, false));

				teethLower = new ModelRenderer(this);
				teethLower.setRotationPoint(0.0F, 13.0F, 9.0F);
				jaw.addChild(teethLower);
				teethLower.cubeList.add(new ModelBox(teethLower, 112, 144, -6.0F, -16.0F, -25.75F, 12, 2, 16, 0.5F, false));

				hornRight = new ModelRenderer(this);
				hornRight.setRotationPoint(-6.0F, -2.0F, -13.0F);
				head.addChild(hornRight);
				setRotationAngle(hornRight, 0.0873F, -0.5236F, 0.0F);
				hornRight.cubeList.add(new ModelBox(hornRight, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 1.0F, false));

				hornRight0 = new ModelRenderer(this);
				hornRight0.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornRight.addChild(hornRight0);
				setRotationAngle(hornRight0, 0.0873F, 0.0873F, 0.0F);
				hornRight0.cubeList.add(new ModelBox(hornRight0, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.8F, false));

				hornRight1 = new ModelRenderer(this);
				hornRight1.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornRight0.addChild(hornRight1);
				setRotationAngle(hornRight1, 0.0873F, 0.0873F, 0.0F);
				hornRight1.cubeList.add(new ModelBox(hornRight1, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.6F, false));

				hornRight2 = new ModelRenderer(this);
				hornRight2.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornRight1.addChild(hornRight2);
				setRotationAngle(hornRight2, 0.0873F, 0.0873F, 0.0F);
				hornRight2.cubeList.add(new ModelBox(hornRight2, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.4F, false));

				hornRight3 = new ModelRenderer(this);
				hornRight3.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornRight2.addChild(hornRight3);
				setRotationAngle(hornRight3, 0.0873F, 0.0873F, 0.0F);
				hornRight3.cubeList.add(new ModelBox(hornRight3, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.2F, false));

				hornRight4 = new ModelRenderer(this);
				hornRight4.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornRight3.addChild(hornRight4);
				setRotationAngle(hornRight4, 0.0873F, 0.0873F, 0.0F);
				hornRight4.cubeList.add(new ModelBox(hornRight4, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.0F, false));

				hornLeft = new ModelRenderer(this);
				hornLeft.setRotationPoint(6.0F, -2.0F, -13.0F);
				head.addChild(hornLeft);
				setRotationAngle(hornLeft, 0.0873F, 0.5236F, 0.0F);
				hornLeft.cubeList.add(new ModelBox(hornLeft, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 1.0F, true));

				hornLeft0 = new ModelRenderer(this);
				hornLeft0.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornLeft.addChild(hornLeft0);
				setRotationAngle(hornLeft0, 0.0873F, -0.0873F, 0.0F);
				hornLeft0.cubeList.add(new ModelBox(hornLeft0, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.8F, true));

				hornLeft1 = new ModelRenderer(this);
				hornLeft1.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornLeft0.addChild(hornLeft1);
				setRotationAngle(hornLeft1, 0.0873F, -0.0873F, 0.0F);
				hornLeft1.cubeList.add(new ModelBox(hornLeft1, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.6F, true));

				hornLeft2 = new ModelRenderer(this);
				hornLeft2.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornLeft1.addChild(hornLeft2);
				setRotationAngle(hornLeft2, 0.0873F, -0.0873F, 0.0F);
				hornLeft2.cubeList.add(new ModelBox(hornLeft2, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.4F, true));

				hornLeft3 = new ModelRenderer(this);
				hornLeft3.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornLeft2.addChild(hornLeft3);
				setRotationAngle(hornLeft3, 0.0873F, -0.0873F, 0.0F);
				hornLeft3.cubeList.add(new ModelBox(hornLeft3, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.2F, true));

				hornLeft4 = new ModelRenderer(this);
				hornLeft4.setRotationPoint(0.0F, 0.0F, 7.0F);
				hornLeft3.addChild(hornLeft4);
				setRotationAngle(hornLeft4, 0.0873F, -0.0873F, 0.0F);
				hornLeft4.cubeList.add(new ModelBox(hornLeft4, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.0F, true));

				whiskerLeft[0] = new ModelRenderer(this);
				whiskerLeft[0].setRotationPoint(6.0F, 6.0F, -24.0F);
				head.addChild(whiskerLeft[0]);
				setRotationAngle(whiskerLeft[0], 0.0F, 1.0472F, 0.0F);
				whiskerLeft[0].cubeList.add(new ModelBox(whiskerLeft[0], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.8F, true));

				whiskerLeft[1] = new ModelRenderer(this);
				whiskerLeft[1].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[0].addChild(whiskerLeft[1]);
				setRotationAngle(whiskerLeft[1], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[1].cubeList.add(new ModelBox(whiskerLeft[1], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.7F, true));

				whiskerLeft[2] = new ModelRenderer(this);
				whiskerLeft[2].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[1].addChild(whiskerLeft[2]);
				setRotationAngle(whiskerLeft[2], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[2].cubeList.add(new ModelBox(whiskerLeft[2], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.6F, true));

				whiskerLeft[3] = new ModelRenderer(this);
				whiskerLeft[3].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[2].addChild(whiskerLeft[3]);
				setRotationAngle(whiskerLeft[3], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[3].cubeList.add(new ModelBox(whiskerLeft[3], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.5F, true));

				whiskerLeft[4] = new ModelRenderer(this);
				whiskerLeft[4].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[3].addChild(whiskerLeft[4]);
				setRotationAngle(whiskerLeft[4], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[4].cubeList.add(new ModelBox(whiskerLeft[4], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.4F, true));

				whiskerLeft[5] = new ModelRenderer(this);
				whiskerLeft[5].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[4].addChild(whiskerLeft[5]);
				setRotationAngle(whiskerLeft[5], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[5].cubeList.add(new ModelBox(whiskerLeft[5], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.2F, true));

				whiskerRight[0] = new ModelRenderer(this);
				whiskerRight[0].setRotationPoint(-6.0F, 6.0F, -24.0F);
				head.addChild(whiskerRight[0]);
				setRotationAngle(whiskerRight[0], 0.0F, -1.0472F, 0.0F);
				whiskerRight[0].cubeList.add(new ModelBox(whiskerRight[0], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.8F, false));

				whiskerRight[1] = new ModelRenderer(this);
				whiskerRight[1].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[0].addChild(whiskerRight[1]);
				setRotationAngle(whiskerRight[1], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[1].cubeList.add(new ModelBox(whiskerRight[1], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.7F, false));

				whiskerRight[2] = new ModelRenderer(this);
				whiskerRight[2].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[1].addChild(whiskerRight[2]);
				setRotationAngle(whiskerRight[2], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[2].cubeList.add(new ModelBox(whiskerRight[2], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.6F, false));

				whiskerRight[3] = new ModelRenderer(this);
				whiskerRight[3].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[2].addChild(whiskerRight[3]);
				setRotationAngle(whiskerRight[3], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[3].cubeList.add(new ModelBox(whiskerRight[3], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.5F, false));

				whiskerRight[4] = new ModelRenderer(this);
				whiskerRight[4].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[3].addChild(whiskerRight[4]);
				setRotationAngle(whiskerRight[4], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[4].cubeList.add(new ModelBox(whiskerRight[4], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.4F, false));

				whiskerRight[5] = new ModelRenderer(this);
				whiskerRight[5].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[4].addChild(whiskerRight[5]);
				setRotationAngle(whiskerRight[5], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[5].cubeList.add(new ModelBox(whiskerRight[5], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.2F, false));

				for (int i = 0; i < spine.length; i++) {
					spine[i] = new ModelRenderer(this);
					spine[i].cubeList.add(new ModelBox(spine[i], 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
					spine[i].cubeList.add(new ModelBox(spine[i], 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));
					if (i == 0) {
						spine[i].setRotationPoint(0.0F, 6.5F, 7.0F);
					} else {
						spine[i].setRotationPoint(0.0F, 0.0F, 11.0F);
						spine[i-1].addChild(spine[i]);
					}
				}

				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, 0.0F, 0.0F);
				eyes.cubeList.add(new ModelBox(eyes, 130, 50, -6.6F, 2.6F, -12.1F, 3, 2, 0, 0.0F, false));
				eyes.cubeList.add(new ModelBox(eyes, 130, 50, 3.6F, 2.6F, -12.1F, 3, 2, 0, 0.0F, true));
			}

			@Override
			public void render(Entity entityIn, float f, float f1, float f2, float f3, float f4, float f5) {
				this.head.render(f5);
				this.spine[0].render(f5);
				if (this.eyes.showModel) {
					GlStateManager.disableLighting();
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
					this.eyes.render(f5);
					GlStateManager.enableLighting();
				}
			}

			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}

			@Override
			public void setRotationAngles(float limbSwing, float f1, float ageInTicks, float f3, float headPitch, float f5, Entity e) {
				super.setRotationAngles(limbSwing, f1, ageInTicks, f3, headPitch, f5, e);
				EC entity = (EC)e;
				float pt = ageInTicks - e.ticksExisted;
				float f6 = (float)Math.PI / 180.0F;
				this.head.rotateAngleX = headPitch * f6;
				if (entity.ticksAlive > entity.wait) {
					this.jaw.rotateAngleX = 0.5236F;
				}
				for (int i = 2; i < 6; i++) {
					whiskerLeft[i].rotateAngleZ = 0.2618F * ageInTicks;
					whiskerRight[i].rotateAngleZ = -0.2618F * ageInTicks;
				}
				for (int i = 0; i < this.spine.length; i++) {
					if (i < entity.partRot.size()) {
						this.spine[i].showModel = true;
						ProcedureUtils.Vec2f vec = entity.partRot.get(i);
						this.spine[i].rotateAngleX = -vec.y * f6;
						this.spine[i].rotateAngleY = -vec.x * f6;
					} else {
						this.spine[i].showModel = false;
					}
				}
			}
		}
	}
}
