
package net.narutomod.entity;

import net.narutomod.item.ItemClaw;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppet3rdKazekage extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 411;
	public static final int ENTITYID_RANGED = 412;

	public EntityPuppet3rdKazekage(ElementsNarutomodMod instance) {
		super(instance, 808);
	}

	@Override
	public void initElements() {
		elements.entities.add(
				() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "puppet_3rd_kazekage"), ENTITYID)
						.name("puppet_3rd_kazekage").tracker(64, 3, true).egg(-1, -1).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
	}

	public static class EntityCustom extends EntityPuppet.Base {
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
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			livingdata = super.onInitialSpawn(difficulty, livingdata);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemClaw.block));
			return livingdata;
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
		protected void initEntityAI() {
			super.initEntityAI();
			//this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.0d, 20, 48f));
			this.tasks.addTask(2, new EntityPuppet.Base.AIChargeAttack(this));
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
	    	this.setNoGravity(this.getOwner() != null);
			if (this.getOwner() != null && this.getVelocity() > 0.01d && this.ticksExisted % 2 == 0) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:wood_click"))), 
				 1f, this.rand.nextFloat() * 0.6f + 0.6f);
			}
		}

		public double getVelocity() {
			return MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends EntityPuppet.Renderer<EntityCustom> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/puppet_3rdkazekage.png");

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn, new ModelPuppetHundred(), 0.5f);
			this.addLayer(new LayerHeldItem(this));
		}
		
		@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return this.texture;
		}
	}

	// Made with Blockbench 4.5.2
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelPuppetHundred extends ModelBiped {
		//private final ModelRenderer bipedHead;
		private final ModelRenderer jaw;
		private final ModelRenderer niceHair;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r2;
		private final ModelRenderer cube_r3;
		//private final ModelRenderer bipedHeadwear;
		private final ModelRenderer collar;
		private final ModelRenderer collar2;
		private final ModelRenderer collar3;
		private final ModelRenderer collar4;
		private final ModelRenderer collar5;
		private final ModelRenderer collar6;
		private final ModelRenderer collar7;
		//private final ModelRenderer bipedBody;
		private final ModelRenderer bone;
		private final ModelRenderer bone2;
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
		//private final ModelRenderer bipedRightLeg;
		//private final ModelRenderer bipedLeftLeg;
		public ModelPuppetHundred() {
			textureWidth = 64;
			textureHeight = 64;
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
			jaw = new ModelRenderer(this);
			jaw.setRotationPoint(0.0F, -1.0F, 0.0F);
			bipedHead.addChild(jaw);
			jaw.cubeList.add(new ModelBox(jaw, 50, 24, -1.5F, -1.0F, -4.0F, 3, 2, 4, 0.0F, false));
			niceHair = new ModelRenderer(this);
			niceHair.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHead.addChild(niceHair);
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(3.4118F, -8.1983F, -3.0337F);
			niceHair.addChild(cube_r1);
			setRotationAngle(cube_r1, -0.0873F, 0.5236F, -0.0873F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 17, 17, -0.9464F, 0.1951F, -0.6367F, 2, 10, 2, 0.15F, false));
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(-3.4118F, -8.1983F, -3.0337F);
			niceHair.addChild(cube_r2);
			setRotationAngle(cube_r2, -0.0873F, -0.5236F, 0.0873F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 17, 17, -1.0536F, 0.1951F, -0.6367F, 2, 10, 2, 0.15F, true));
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.005F, -7.9697F, -3.8572F);
			niceHair.addChild(cube_r3);
			setRotationAngle(cube_r3, -1.1345F, 0.0F, 0.0F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 32, 11, -4.0F, -6.15F, 0.15F, 8, 6, 7, 0.22F, false));
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			collar = new ModelRenderer(this);
			collar.setRotationPoint(0.0F, -0.116F, -2.884F);
			bipedHeadwear.addChild(collar);
			setRotationAngle(collar, -1.0472F, 0.0F, 0.0F);
			collar.cubeList.add(new ModelBox(collar, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
			collar2 = new ModelRenderer(this);
			collar2.setRotationPoint(0.0F, 0.134F, -2.884F);
			bipedHeadwear.addChild(collar2);
			setRotationAngle(collar2, -1.0908F, 0.0F, 0.0F);
			collar2.cubeList.add(new ModelBox(collar2, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
			collar3 = new ModelRenderer(this);
			collar3.setRotationPoint(0.0F, 0.384F, -2.884F);
			bipedHeadwear.addChild(collar3);
			setRotationAngle(collar3, -1.1345F, 0.0F, 0.0F);
			collar3.cubeList.add(new ModelBox(collar3, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
			collar4 = new ModelRenderer(this);
			collar4.setRotationPoint(0.0F, 0.634F, -2.884F);
			bipedHeadwear.addChild(collar4);
			setRotationAngle(collar4, -1.1781F, 0.0F, 0.0F);
			collar4.cubeList.add(new ModelBox(collar4, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
			collar5 = new ModelRenderer(this);
			collar5.setRotationPoint(0.0F, 0.884F, -2.634F);
			bipedHeadwear.addChild(collar5);
			setRotationAngle(collar5, -1.2217F, 0.0F, 0.0F);
			collar5.cubeList.add(new ModelBox(collar5, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
			collar6 = new ModelRenderer(this);
			collar6.setRotationPoint(0.0F, 1.134F, -2.634F);
			bipedHeadwear.addChild(collar6);
			setRotationAngle(collar6, -1.2654F, 0.0F, 0.0F);
			collar6.cubeList.add(new ModelBox(collar6, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
			collar7 = new ModelRenderer(this);
			collar7.setRotationPoint(0.0F, 1.384F, -2.634F);
			bipedHeadwear.addChild(collar7);
			setRotationAngle(collar7, -1.309F, 0.0F, 0.0F);
			collar7.cubeList.add(new ModelBox(collar7, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.addChild(bone);
			setRotationAngle(bone, -0.0873F, 0.0F, 0.0F);
			bone.cubeList.add(new ModelBox(bone, 16, 36, -4.0F, 0.0F, -2.0F, 8, 24, 4, 0.5F, false));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.addChild(bone2);
			setRotationAngle(bone2, 0.0873F, 0.0F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 40, 36, -4.0F, 0.0F, -2.0F, 8, 24, 4, 0.5F, false));
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 48, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
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
