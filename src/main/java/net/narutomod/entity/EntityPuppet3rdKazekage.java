
package net.narutomod.entity;

import net.narutomod.procedure.ProcedureUtils;
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
import net.minecraft.util.math.MathHelper;
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
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.EntityAIAttackMelee;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppet3rdKazekage extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 411;
	public static final int ENTITYID_RANGED = 412;

	public EntityPuppet3rdKazekage(ElementsNarutomodMod instance) {
		super(instance, 808);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "puppet_3rd_kazekage"), ENTITYID)
		 .name("puppet_3rd_kazekage").tracker(64, 3, true).egg(-1, -1).build());
	}

	public static class EntityCustom extends EntityPuppet.Base {
		private static final DataParameter<Boolean> MOUTH_OPEN = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		public static final float MAXHEALTH = 40.0f;

		public EntityCustom(World worldIn) {
			super(worldIn);
			this.setSize(0.6f, 2.0f);
		}

		public EntityCustom(EntityLivingBase ownerIn) {
			super(ownerIn);
			this.setSize(0.6f, 2.0f);
			Vec3d vec = ownerIn.getLookVec();
			vec = ownerIn.getPositionVector().addVector(vec.x, 1d, vec.z);
			this.setLocationAndAngles(vec.x, vec.y, vec.z, ownerIn.rotationYaw, 0f);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			livingdata = super.onInitialSpawn(difficulty, livingdata);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemClaw.block));
			return livingdata;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(MOUTH_OPEN, Boolean.valueOf(false));
		}

		protected boolean isMouthOpen() {
			return ((Boolean)this.getDataManager().get(MOUTH_OPEN)).booleanValue();
		}

		public void setMouthOpen(boolean down) {
			this.getDataManager().set(MOUTH_OPEN, Boolean.valueOf(down));
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAXHEALTH);
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected Vec3d getOffsetToOwner() {
			return new Vec3d(-0.8d, 0.5d, 4.0d);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			//this.tasks.addTask(1, new EntityAIAttackRanged(this, 1.0d, 20, 48f));
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.5d, true));
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			return ProcedureUtils.attackEntityAsMob(this, entityIn);
		}

		private boolean isMovingForward() {
			return Math.abs(MathHelper.wrapDegrees(ProcedureUtils.getYawFromVec(this.motionX, this.motionZ) - this.rotationYaw)) < 90.0f;
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityPuppet.ClientClass.Renderer<EntityCustom> {
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
			private final ModelRenderer jaw2;
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer niceHair;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer collar;
			private final ModelRenderer collar1;
			private final ModelRenderer collar2;
			private final ModelRenderer collar3;
			private final ModelRenderer collar4;
			private final ModelRenderer collar5;
			private final ModelRenderer collar6;
			private final ModelRenderer collar7;
			private final ModelRenderer collar8;
			private final ModelRenderer collar9;
			private final ModelRenderer collar10;
			private final ModelRenderer collar11;
			private final ModelRenderer collar12;
			private final ModelRenderer collar13;
			private final ModelRenderer collar14;
			private final ModelRenderer collar15;
			private final ModelRenderer collar16;
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
				jaw.cubeList.add(new ModelBox(jaw, 50, 24, -1.5F, -1.0F, -4.01F, 3, 2, 4, 0.0F, false));
		
				jaw2 = new ModelRenderer(this);
				jaw2.setRotationPoint(0.0F, -1.0F, 0.0F);
				bipedHead.addChild(jaw2);
				jaw2.cubeList.add(new ModelBox(jaw2, 36, 24, -4.0F, -2.0F, -4.0F, 3, 3, 4, -0.01F, false));
				jaw2.cubeList.add(new ModelBox(jaw2, 36, 24, 1.0F, -2.0F, -4.0F, 3, 3, 4, -0.01F, true));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				niceHair = new ModelRenderer(this);
				niceHair.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.addChild(niceHair);
				
		
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(3.4118F, -8.1983F, -3.0337F);
				niceHair.addChild(cube_r1);
				setRotationAngle(cube_r1, -0.0873F, 0.5236F, -0.0873F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 16, 16, -0.9464F, 0.1951F, -0.6367F, 2, 10, 2, 0.15F, false));
		
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(-3.4118F, -8.1983F, -3.0337F);
				niceHair.addChild(cube_r2);
				setRotationAngle(cube_r2, -0.0873F, -0.5236F, 0.0873F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 16, 16, -1.0536F, 0.1951F, -0.6367F, 2, 10, 2, 0.15F, true));
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(0.005F, -7.9697F, -3.8572F);
				niceHair.addChild(cube_r3);
				setRotationAngle(cube_r3, -1.0472F, 0.0F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 32, 11, -4.0F, -6.15F, 0.15F, 8, 6, 7, 0.2F, false));
		
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(0.005F, -7.9697F, -3.8572F);
				niceHair.addChild(cube_r4);
				setRotationAngle(cube_r4, -1.2217F, 0.0F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 32, 11, -4.0F, -6.65F, 0.25F, 8, 6, 7, 0.25F, true));
		
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.005F, -7.9697F, -3.8572F);
				niceHair.addChild(cube_r5);
				setRotationAngle(cube_r5, -1.3963F, 0.0F, 0.0F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 32, 11, -4.0F, -7.15F, 0.35F, 8, 6, 7, 0.3F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				collar = new ModelRenderer(this);
				collar.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(collar);
				
		
				collar1 = new ModelRenderer(this);
				collar1.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar1);
				setRotationAngle(collar1, -1.0472F, 0.0F, 0.0F);
				collar1.cubeList.add(new ModelBox(collar1, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
		
				collar2 = new ModelRenderer(this);
				collar2.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar2);
				setRotationAngle(collar2, -1.0908F, 0.0F, 0.0436F);
				collar2.cubeList.add(new ModelBox(collar2, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
		
				collar3 = new ModelRenderer(this);
				collar3.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar3);
				setRotationAngle(collar3, -1.1345F, 0.0F, -0.0436F);
				collar3.cubeList.add(new ModelBox(collar3, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
		
				collar4 = new ModelRenderer(this);
				collar4.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar4);
				setRotationAngle(collar4, -1.1781F, 0.0F, 0.0436F);
				collar4.cubeList.add(new ModelBox(collar4, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
		
				collar5 = new ModelRenderer(this);
				collar5.setRotationPoint(0.0F, -0.116F, -2.634F);
				collar.addChild(collar5);
				setRotationAngle(collar5, -1.2217F, 0.0F, -0.0436F);
				collar5.cubeList.add(new ModelBox(collar5, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
		
				collar6 = new ModelRenderer(this);
				collar6.setRotationPoint(0.0F, -0.116F, -2.634F);
				collar.addChild(collar6);
				setRotationAngle(collar6, -1.2654F, 0.0F, 0.0436F);
				collar6.cubeList.add(new ModelBox(collar6, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
		
				collar7 = new ModelRenderer(this);
				collar7.setRotationPoint(0.0F, -0.116F, -2.634F);
				collar.addChild(collar7);
				setRotationAngle(collar7, -1.309F, 0.0F, -0.0436F);
				collar7.cubeList.add(new ModelBox(collar7, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
		
				collar8 = new ModelRenderer(this);
				collar8.setRotationPoint(0.0F, -0.116F, -2.634F);
				collar.addChild(collar8);
				setRotationAngle(collar8, -1.3526F, 0.0F, 0.0436F);
				collar8.cubeList.add(new ModelBox(collar8, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
		
				collar9 = new ModelRenderer(this);
				collar9.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar9);
				setRotationAngle(collar9, -1.3963F, 0.0F, -0.0436F);
				collar9.cubeList.add(new ModelBox(collar9, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
		
				collar10 = new ModelRenderer(this);
				collar10.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar10);
				setRotationAngle(collar10, -1.4399F, 0.0F, 0.0436F);
				collar10.cubeList.add(new ModelBox(collar10, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
		
				collar11 = new ModelRenderer(this);
				collar11.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar11);
				setRotationAngle(collar11, -1.4835F, 0.0F, -0.0436F);
				collar11.cubeList.add(new ModelBox(collar11, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
		
				collar12 = new ModelRenderer(this);
				collar12.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar12);
				setRotationAngle(collar12, -1.5272F, 0.0F, 0.0436F);
				collar12.cubeList.add(new ModelBox(collar12, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
		
				collar13 = new ModelRenderer(this);
				collar13.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar13);
				setRotationAngle(collar13, -1.5708F, 0.0F, -0.0436F);
				collar13.cubeList.add(new ModelBox(collar13, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
		
				collar14 = new ModelRenderer(this);
				collar14.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar14);
				setRotationAngle(collar14, -1.6144F, 0.0F, 0.0436F);
				collar14.cubeList.add(new ModelBox(collar14, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
		
				collar15 = new ModelRenderer(this);
				collar15.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar15);
				setRotationAngle(collar15, -1.6581F, 0.0F, -0.0436F);
				collar15.cubeList.add(new ModelBox(collar15, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
		
				collar16 = new ModelRenderer(this);
				collar16.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar16);
				setRotationAngle(collar16, -1.7017F, 0.0F, 0.0F);
				collar16.cubeList.add(new ModelBox(collar16, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
		
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
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedBody.addChild(bipedRightLeg);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedBody.addChild(bipedLeftLeg);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 48, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}

			@Override
			public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
				this.bipedHead.render(scale);
				this.bipedBody.render(scale);
				this.bipedRightArm.render(scale);
				this.bipedLeftArm.render(scale);
				this.bipedHeadwear.render(scale);
			}

			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(0f, 0f, f2, f3, f4, f5, e);
				double d = ((EntityCustom)e).getVelocity();
				if (d > 0.001d && ((EntityCustom)e).isMovingForward()) {
					float fa = MathHelper.clamp((float)d / 0.3F, 0F, 1F);
					bipedBody.rotateAngleX += fa * 0.7854F;
					collar.rotateAngleX = fa * -0.2618F;
					if (this.swingProgress <= 0.0F && rightArmPose == ModelBiped.ArmPose.EMPTY) {
						bipedRightArm.rotateAngleZ += fa * 1.3963F;
					}
					if (leftArmPose == ModelBiped.ArmPose.EMPTY) {
						bipedLeftArm.rotateAngleZ += fa * -1.3963F;
					}
					bipedRightLeg.rotateAngleX = 0.0F;
					bipedLeftLeg.rotateAngleX = 0.0F;
				}
				if (((EntityCustom)e).isMouthOpen()) {
					jaw.rotateAngleX = 0.2618F;
					jaw2.rotateAngleX = 0.1745F;
				} else {
					jaw.rotateAngleX = 0.0F;
					jaw2.rotateAngleX = 0.0F;
				}
			}
		}
	}
}
