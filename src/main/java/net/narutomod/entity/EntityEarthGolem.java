
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.block.Block;

import javax.vecmath.Vector3f;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEarthGolem extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 407;
	public static final int ENTITYID_RANGED = 408;

	public EntityEarthGolem(ElementsNarutomodMod instance) {
		super(instance, 799);
	}

	@Override
	public void initElements() {
		elements.entities
				.add(() -> EntityEntryBuilder.create().entity(EC.class).id(new ResourceLocation("narutomod", "earth_golem"), ENTITYID)
						.name("earth_golem").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
	}

	public static class EC extends EntitySummonAnimal.Base {
		private final int growTime = 40;
		private int attackTimer;
		private int nextStepDistance;
		private int deathTicks;
		
		public EC(World w) {
			super(w);
			this.setOGSize(1f, 2.875f);
			this.isImmuneToFire = true;
			this.postScaleFixup();
		}

		public EC(EntityLivingBase summonerIn, float size) {
			super(summonerIn);
			this.setOGSize(1f, 2.875f);
			this.isImmuneToFire = true;
			this.setScale(size);
			this.setLocationAndAngles(summonerIn.posX + summonerIn.getLookVec().x, summonerIn.posY, summonerIn.posZ + summonerIn.getLookVec().z, summonerIn.rotationYaw, 0f);
		}

		@Override
		protected void postScaleFixup() {
			float f = this.getScale();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20.0D * f);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D * f);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(15.0D * f);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(10.0D + 6.0D * f);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D + (f - 1F) * 0.1D);
			super.postScaleFixup();
			this.experienceValue = (int)(f * 10);
		}

		@Override
		protected void dontWander(boolean set) {
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0d, true));
		}

		@Override
		protected void playStepSound(BlockPos pos, Block blockIn) {
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
		public double getMountedYOffset() {
			return (double)this.height - 0.35d;
		}

		@Override
		protected boolean canFitPassenger(Entity passenger) {
			return this.getScale() >= 2.0f && this.getPassengers().size() < 1;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getTrueSource() instanceof EntityLivingBase && source.getTrueSource().equals(this.getSummoner())) {
				this.onDeathUpdate();
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			boolean ret = super.attackEntityAsMob(entityIn);
			if (ret) {
				entityIn.motionY += 0.4D * (0.3D + 0.7D * this.getScale());
				entityIn.velocityChanged = true;
			}
			this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
			this.attackTimer = 10;
			this.world.setEntityState(this, (byte)4);
			return ret;
		}

	    @SideOnly(Side.CLIENT)
	    @Override
	    public void handleStatusUpdate(byte id) {
	        if (id == 4) {
	            this.attackTimer = 10;
	            this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
	        } else {
	        	super.handleStatusUpdate(id);
	        }
	    }

	    @Override
	    public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
	    	super.setAttackTarget(this.getAge() > this.growTime ? entitylivingbaseIn : null);
	    }

	    @SideOnly(Side.CLIENT)
    	public int getAttackTimer() {
    		return this.attackTimer;
    	}

		@Override
		protected void onDeathUpdate() {
			if (!this.world.isRemote) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rocks")), 1.0F, 0.8F);
				Particles.spawnParticle(this.world, Particles.Types.BLOCK_DUST, this.posX, this.posY + this.height * 0.5,
				 this.posZ, (int)(this.getScale() * 1000), 0.5d * this.width, 0.3d * this.height, 0.5d * this.width,
				 0d, 0d, 0d, Block.getIdFromBlock(Blocks.DIRT), 40);
			}
			this.setDead();
		}

    	@Override
    	public void onLivingUpdate() {
    		this.clearActivePotions();
    		super.onLivingUpdate();
    		if (this.getSummoner() == null) {
    			this.onDeathUpdate();
    		} else {
	    		int age = this.getAge();
	    		if (age == 1) {
	    			this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rocks")), 1.0F, 0.8F);
	    		}
	    		if (age <= this.growTime) {
					for (int i = 0; i < (int)(this.getScale() * 20); i++) {
						this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ,
						 (this.rand.nextDouble()-0.5d) * this.width * 0.25d, 0.15d,
						 (this.rand.nextDouble()-0.5d) * this.width * 0.25d, Block.getIdFromBlock(Blocks.DIRT));
					}
	    		}
	    		float f = this.distanceWalkedOnStepModified * this.ogHeight / this.height;
	    		if (f > this.nextStepDistance && !this.world.isAirBlock(new BlockPos(this.posX, this.posY - 0.2d, this.posZ))) {
	    			this.nextStepDistance = (int)f + 1;
	    			if (!this.isInWater()) {
	    				this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1f, 1);
	    			}
	    		}
    		}
    		if (this.attackTimer > 0) {
    			--this.attackTimer;
    		}
    	}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 1.0f) {
					entity.world.spawnEntity(new EC(entity, power));
					return true;
				}
				return false;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends RenderLiving<EC> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/golem_rock.png");

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn, new ModelRockGolem(), 0.5F);
		}

		@Override
		public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
			this.shadowSize = 0.6f * entity.getScale();
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return this.texture;
		}

		@Override
		protected void applyRotations(EC entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
			super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
			if ((double)entityLiving.limbSwingAmount >= 0.01D) {
				float f1 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
				float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
				GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
			}
		}
	}

	// Made with Blockbench 4.4.3
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelRockGolem extends ModelBase {
		private final ModelRenderer ironGolemHead;
		private final ModelRenderer hornRight;
		private final ModelRenderer hornLeft;
		private final ModelRenderer ironGolemBody;
		private final ModelRenderer ironGolemRightArm;
		private final ModelRenderer right_arm;
		private final ModelRenderer bone;
		private final ModelRenderer ironGolemLeftArm;
		private final ModelRenderer left_arm;
		private final ModelRenderer bone2;
		private final ModelRenderer ironGolemRightLeg;
		private final ModelRenderer right_leg;
		private final ModelRenderer right_leg2;
		private final ModelRenderer ironGolemLeftLeg;
		private final ModelRenderer left_leg;
		private final ModelRenderer left_leg2;
		private final Vector3f headStart = new Vector3f(0.0F, 34.0F, 0.0F);
		private final Vector3f headEnd = new Vector3f(0.0F, -12.0F, 0.0F);
		private final Vector3f bodyStart = new Vector3f(0.0F, 24.0F, 0.0F);
		private final Vector3f bodyEnd = new Vector3f(0.0F, -12.0F, 0.0F);
		private final Vector3f rightArmStart = new Vector3f(0.0F, 26.0F, 2.0F);
		private final Vector3f rightArmEnd = new Vector3f(-8.0F, -10.0F, 0.0F);
		private final Vector3f leftArmStart = new Vector3f(0.0F, 26.0F, 2.0F);
		private final Vector3f leftArmEnd = new Vector3f(8.0F, -10.0F, 0.0F);
		private final Vector3f rightLegStart = new Vector3f(-0.5F, 26.5F, 0.0F);
		private final Vector3f rightLegEnd = new Vector3f(-3.5F, 5.5F, 0.0F);
		private final Vector3f leftLegStart = new Vector3f(0.5F, 26.5F, 0.0F);
		private final Vector3f leftLegEnd = new Vector3f(3.5F, 5.5F, 0.0F);

		public ModelRockGolem() {
			textureWidth = 128;
			textureHeight = 128;
			ironGolemHead = new ModelRenderer(this);
			ironGolemHead.setRotationPoint(0.0F, -12.0F, 0.0F);
			ironGolemHead.cubeList.add(new ModelBox(ironGolemHead, 32, 24, -4.0F, -10.0F, -7.5F, 8, 10, 8, 0.0F, false));
			hornRight = new ModelRenderer(this);
			hornRight.setRotationPoint(-1.35F, -10.5F, -5.4F);
			ironGolemHead.addChild(hornRight);
			setRotationAngle(hornRight, 0.0F, 0.0F, 0.5236F);
			hornRight.cubeList.add(new ModelBox(hornRight, 24, 24, -2.05F, -0.2F, -2.1F, 4, 2, 2, -0.01F, false));
			hornLeft = new ModelRenderer(this);
			hornLeft.setRotationPoint(1.35F, -10.5F, -5.4F);
			ironGolemHead.addChild(hornLeft);
			setRotationAngle(hornLeft, 0.0F, 0.0F, -0.5236F);
			hornLeft.cubeList.add(new ModelBox(hornLeft, 24, 24, -1.95F, -0.2F, -2.1F, 4, 2, 2, -0.01F, true));
			ironGolemBody = new ModelRenderer(this);
			ironGolemBody.setRotationPoint(0.0F, -12.0F, 0.0F);
			ironGolemBody.cubeList.add(new ModelBox(ironGolemBody, 0, 0, -9.0F, 0.0F, -6.0F, 18, 13, 11, 0.0F, false));
			ironGolemBody.cubeList.add(new ModelBox(ironGolemBody, 50, 52, -5.0F, 13.5F, -3.0F, 10, 8, 6, 0.5F, false));
			ironGolemRightArm = new ModelRenderer(this);
			ironGolemRightArm.setRotationPoint(-8.0F, -10.0F, 0.0F);
			right_arm = new ModelRenderer(this);
			right_arm.setRotationPoint(0.0F, 1.0F, 0.0F);
			ironGolemRightArm.addChild(right_arm);
			setRotationAngle(right_arm, 0.0F, -0.5236F, 0.1745F);
			right_arm.cubeList.add(new ModelBox(right_arm, 24, 42, -8.0F, -2.5F, -3.5F, 8, 8, 8, 0.0F, false));
			right_arm.cubeList.add(new ModelBox(right_arm, 48, 42, -6.0F, 5.5F, -2.5F, 4, 2, 6, 0.0F, false));
			bone = new ModelRenderer(this);
			bone.setRotationPoint(-4.0F, 7.5F, 3.5F);
			right_arm.addChild(bone);
			setRotationAngle(bone, -0.2618F, 0.0F, 0.0F);
			bone.cubeList.add(new ModelBox(bone, 48, 42, -2.0F, 0.0F, -6.0F, 4, 2, 6, 0.0F, false));
			bone.cubeList.add(new ModelBox(bone, 0, 24, -4.0F, 2.0F, -7.0F, 8, 12, 8, 0.0F, false));
			ironGolemLeftArm = new ModelRenderer(this);
			ironGolemLeftArm.setRotationPoint(8.0F, -10.0F, 0.0F);
			left_arm = new ModelRenderer(this);
			left_arm.setRotationPoint(0.0F, 1.0F, 0.0F);
			ironGolemLeftArm.addChild(left_arm);
			setRotationAngle(left_arm, 0.0F, 0.5236F, -0.1745F);
			left_arm.cubeList.add(new ModelBox(left_arm, 24, 42, 0.0F, -2.5F, -3.5F, 8, 8, 8, 0.0F, true));
			left_arm.cubeList.add(new ModelBox(left_arm, 48, 42, 2.0F, 5.5F, -2.5F, 4, 2, 6, 0.0F, true));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(4.0F, 7.5F, 3.5F);
			left_arm.addChild(bone2);
			setRotationAngle(bone2, -0.2618F, 0.0F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 48, 42, -2.0F, 0.0F, -6.0F, 4, 2, 6, 0.0F, true));
			bone2.cubeList.add(new ModelBox(bone2, 0, 24, -4.0F, 2.0F, -7.0F, 8, 12, 8, 0.0F, true));
			ironGolemRightLeg = new ModelRenderer(this);
			ironGolemRightLeg.setRotationPoint(-3.5F, 5.5F, 0.0F);		
			right_leg = new ModelRenderer(this);
			right_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
			ironGolemRightLeg.addChild(right_leg);
			setRotationAngle(right_leg, -0.2618F, 0.2618F, 0.0F);
			right_leg.cubeList.add(new ModelBox(right_leg, 58, 0, -5.5F, 0.0F, -3.0F, 6, 10, 6, -0.2F, false));
			right_leg2 = new ModelRenderer(this);
			right_leg2.setRotationPoint(-2.5F, 10.0F, -3.0F);
			right_leg.addChild(right_leg2);
			setRotationAngle(right_leg2, 0.2618F, 0.0F, 0.0F);
			right_leg2.cubeList.add(new ModelBox(right_leg2, 0, 44, -3.0F, 0.0F, 0.0F, 6, 10, 6, 0.2F, false));
			ironGolemLeftLeg = new ModelRenderer(this);
			ironGolemLeftLeg.setRotationPoint(3.5F, 5.5F, 0.0F);
			left_leg = new ModelRenderer(this);
			left_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
			ironGolemLeftLeg.addChild(left_leg);
			setRotationAngle(left_leg, -0.2618F, -0.2618F, 0.0F);
			left_leg.cubeList.add(new ModelBox(left_leg, 58, 0, -0.5F, 0.0F, -3.0F, 6, 10, 6, -0.2F, true));
			left_leg2 = new ModelRenderer(this);
			left_leg2.setRotationPoint(2.5F, 10.0F, -3.0F);
			left_leg.addChild(left_leg2);
			setRotationAngle(left_leg2, 0.2618F, 0.0F, 0.0F);
			left_leg2.cubeList.add(new ModelBox(left_leg2, 0, 44, -3.0F, 0.0F, 0.0F, 6, 10, 6, 0.2F, true));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			float scale = ((EC)entity).getScale();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 1.5F - 1.5F * scale, 0.0F);
			GlStateManager.scale(scale, scale, scale);
			ironGolemHead.render(f5);
			ironGolemBody.render(f5);
			ironGolemRightArm.render(f5);
			ironGolemLeftArm.render(f5);
			ironGolemRightLeg.render(f5);
			ironGolemLeftLeg.render(f5);
			GlStateManager.popMatrix();
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

	    @Override
	    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
	        limbSwing = limbSwing * ((EC)entityIn).ogHeight / entityIn.height;
	        this.ironGolemHead.rotateAngleY = netHeadYaw * 0.017453292F;
	        this.ironGolemHead.rotateAngleX = headPitch * 0.017453292F;
	        this.ironGolemLeftLeg.rotateAngleX = -1.5F * this.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
	        this.ironGolemRightLeg.rotateAngleX = 1.5F * this.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
	        this.ironGolemLeftLeg.rotateAngleY = 0.0F;
	        this.ironGolemRightLeg.rotateAngleY = 0.0F;
	    }
	
	    @Override
	    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
	        EC entity = (EC)entitylivingbaseIn;
	        limbSwing = limbSwing * entity.ogHeight / entity.height;
	        float f = MathHelper.clamp(((float)entity.getAge() + partialTickTime) / (float)entity.growTime, 0.0f, 1.0f);
	        this.ironGolemHead.setRotationPoint(this.headStart.x + (this.headEnd.x - this.headStart.x) * f, this.headStart.y + (this.headEnd.y - this.headStart.y) * f, this.headStart.z + (this.headEnd.z - this.headStart.z) * f);
	        this.ironGolemBody.setRotationPoint(this.bodyStart.x + (this.bodyEnd.x - this.bodyStart.x) * f, this.bodyStart.y + (this.bodyEnd.y - this.bodyStart.y) * f, this.bodyStart.z + (this.bodyEnd.z - this.bodyStart.z) * f);
	        this.ironGolemRightArm.setRotationPoint(this.rightArmStart.x + (this.rightArmEnd.x - this.rightArmStart.x) * f, this.rightArmStart.y + (this.rightArmEnd.y - this.rightArmStart.y) * f, this.rightArmStart.z + (this.rightArmEnd.z - this.rightArmStart.z) * f);
	        this.ironGolemLeftArm.setRotationPoint(this.leftArmStart.x + (this.leftArmEnd.x - this.leftArmStart.x) * f, this.leftArmStart.y + (this.leftArmEnd.y - this.leftArmStart.y) * f, this.leftArmStart.z + (this.leftArmEnd.z - this.leftArmStart.z) * f);
	        this.ironGolemRightLeg.setRotationPoint(this.rightLegStart.x + (this.rightLegEnd.x - this.rightLegStart.x) * f, this.rightLegStart.y + (this.rightLegEnd.y - this.rightLegStart.y) * f, this.rightLegStart.z + (this.rightLegEnd.z - this.rightLegStart.z) * f);
	        this.ironGolemLeftLeg.setRotationPoint(this.leftLegStart.x + (this.leftLegEnd.x - this.leftLegStart.x) * f, this.leftLegStart.y + (this.leftLegEnd.y - this.leftLegStart.y) * f, this.leftLegStart.z + (this.leftLegEnd.z - this.leftLegStart.z) * f);
	        int i = entity.getAttackTimer();
	        if (i > 0) {
	            this.ironGolemRightArm.rotateAngleX = -2.0F + 1.5F * this.triangleWave((float)i - partialTickTime, 10.0F);
	            this.ironGolemLeftArm.rotateAngleX = -2.0F + 1.5F * this.triangleWave((float)i - partialTickTime, 10.0F);
	        } else {
                this.ironGolemRightArm.rotateAngleX = (-0.2F + 1.5F * this.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
                this.ironGolemLeftArm.rotateAngleX = (-0.2F - 1.5F * this.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
	        }
	    }
	
	    private float triangleWave(float p_78172_1_, float p_78172_2_) {
	        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
	    }
	}
}
