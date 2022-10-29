
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.pathfinding.PathNavigateFlying;

//import net.narutomod.procedure.ProcedureUtils;
//import net.narutomod.item.ItemKunai;
import net.narutomod.item.ItemSenbon;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppetKarasu extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 379;
	public static final int ENTITYID_RANGED = 380;

	public EntityPuppetKarasu(ElementsNarutomodMod instance) {
		super(instance, 738);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "puppet_karasu"), ENTITYID)
		 .name("puppet_karasu").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new EntityPuppet.Renderer(renderManager, new ModelKarasu(), 0.5f) {
				protected ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation("narutomod:textures/karasu.png");
				}
			};
		});
	}

	public static class EntityCustom extends EntityPuppet.Base implements IRangedAttackMob {
		public static final float MAXHEALTH = 40.0f;
		
		public EntityCustom(World worldIn) {
			super(worldIn);
			this.setSize(0.6f, 2.0f);
			this.navigator = new PathNavigateFlying(this, worldIn);
			this.moveHelper = new EntityPuppet.Base.FlyHelper(this);
		}

		public EntityCustom(EntityLivingBase ownerIn) {
			super(ownerIn);
			this.setSize(0.6f, 2.0f);
			Vec3d vec = ownerIn.getLookVec();
			vec = ownerIn.getPositionVector().addVector(vec.x, 1d, vec.z);
			this.setLocationAndAngles(vec.x, vec.y, vec.z, ownerIn.rotationYaw, 0f);
			this.navigator = new PathNavigateFlying(this, ownerIn.world);
			this.moveHelper = new EntityPuppet.Base.FlyHelper(this);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.0d, 20, 48f));
			this.tasks.addTask(2, new EntityPuppet.Base.AIChargeAttack(this));
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAXHEALTH);
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4D);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
	    	this.setNoGravity(this.getOwner() != null);
			if (this.getOwner() != null && this.getVelocity() > 0.01d && this.ticksExisted % 2 == 0) {
				this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:wood_click"))), 
				 1f, this.rand.nextFloat() * 0.6f + 0.6f);
			}
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float flval) {
			Vec3d vec = target.getPositionVector().addVector(0d, 0.5d * target.height, 0d);
			for (int i = 0; i < 10; i++) {
				ItemSenbon.spawnArrow(this, vec);
			}
			/*ItemKunai.EntityArrowCustom kunai = new ItemKunai.EntityArrowCustom(this.world, this);
			Vec3d vec = target.getPositionEyes(1f).subtract(kunai.getPositionVector());
			kunai.shoot(vec.x, vec.y + MathHelper.sqrt(vec.x * vec.x + vec.z * vec.z) * 0.2d, vec.z, 1f, 0);
			kunai.setDamage(5);
			kunai.setKnockbackStrength(0);
			this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1, 1f / (this.rand.nextFloat() * 0.5f + 1f) + 0.25f);
			this.world.spawnEntity(kunai);*/
		}

		public double getVelocity() {
			return MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		}
	}

	// Made with Blockbench 3.8.4
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelKarasu extends ModelBiped {
		private final ModelRenderer RightArm2;
		private final ModelRenderer LeftArm2;

		public ModelKarasu() {
			textureWidth = 64;
			textureHeight = 64;
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F, false));
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
			setRotationAngle(bipedRightArm, 0.0F, 0.0F, 0.3491F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
			setRotationAngle(bipedLeftArm, 0.0F, 0.0F, -0.3491F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			setRotationAngle(bipedRightLeg, 0.0F, 0.0F, 0.0873F);
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -1.5F, 0.0F, -2.0F, 3, 12, 4, 0.0F, false));
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			setRotationAngle(bipedLeftLeg, 0.0F, 0.0F, -0.0873F);
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -1.5F, 0.0F, -2.0F, 3, 12, 4, 0.0F, false));
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
			RightArm2 = new ModelRenderer(this);
			RightArm2.setRotationPoint(-5.0F, 7.5F, 0.0F);
			setRotationAngle(RightArm2, 0.0F, 0.0F, 0.2182F);
			RightArm2.cubeList.add(new ModelBox(RightArm2, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
			RightArm2.cubeList.add(new ModelBox(RightArm2, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
			LeftArm2 = new ModelRenderer(this);
			LeftArm2.setRotationPoint(5.0F, 7.5F, 0.0F);
			setRotationAngle(LeftArm2, 0.0F, 0.0F, -0.2618F);
			LeftArm2.cubeList.add(new ModelBox(LeftArm2, 32, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
			LeftArm2.cubeList.add(new ModelBox(LeftArm2, 48, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			super.render(entity, f, f1, f2, f3, f4, f5);
			RightArm2.render(f5);
			LeftArm2.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		@Override
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			super.setRotationAngles(0f, 0f, f2, f3, f4, f5, e);
			bipedRightArm.rotateAngleZ += 0.3491F;
			bipedLeftArm.rotateAngleZ += -0.3491F;
			double velocity = ((EntityCustom)e).getVelocity();
			if (velocity > 0.001d) {
				float fa = MathHelper.clamp((float)velocity, 0F, 1F) * 45F * (float)Math.PI / 180F;
				bipedRightArm.rotateAngleX += fa;
				bipedLeftArm.rotateAngleX += fa;
				RightArm2.rotateAngleX = bipedRightArm.rotateAngleX;
				RightArm2.rotateAngleY = bipedRightArm.rotateAngleY;
				RightArm2.rotateAngleZ = bipedRightArm.rotateAngleZ - 0.1309F;
				LeftArm2.rotateAngleX = bipedLeftArm.rotateAngleX;
				LeftArm2.rotateAngleY = bipedLeftArm.rotateAngleY;
				LeftArm2.rotateAngleZ = bipedLeftArm.rotateAngleZ + 0.1309F;
				bipedRightLeg.rotateAngleX += fa;
				bipedLeftLeg.rotateAngleX += fa;
			}
		}
	}
}
