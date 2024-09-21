package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.init.MobEffects;

import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySusanooSkeleton extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 32;
	
	public EntitySusanooSkeleton(ElementsNarutomodMod instance) {
		super(instance, 217);
	}

	public void initElements() {
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "susanooskeleton"), ENTITYID).name("susanooskeleton").tracker(64, 1, true).build());
	}
	
	public static class EntityCustom extends EntitySusanooBase {
		private static final DataParameter<Boolean> FULL_BODY = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);

		public EntityCustom(World world) {
			super(world);
			this.setSize(2.4F, 2.4F);
		}

		public EntityCustom(EntityPlayer player) {
			this(player, false);
		}

		public EntityCustom(EntityPlayer player, boolean full) {
			super(player);
			this.setSize(2.4F, 2.4F);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			 .setBaseValue(Math.min(this.playerXp, EntitySusanooBase.BXP_REQUIRED_L2) * 0.003d);
			if (!full) {
				this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(0.0D);
				this.chakraUsage = 30d;
			} else {
				this.setFullBody(true);
			}
			this.stepHeight = this.height / 3.0F;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(FULL_BODY, Boolean.valueOf(false));
		}

		public boolean isFullBody() {
			return ((Boolean)this.getDataManager().get(FULL_BODY)).booleanValue();
		}

		protected void setFullBody(boolean b) {
			this.getDataManager().set(FULL_BODY, Boolean.valueOf(b));
			this.setSize(2.4f, b ? 3.6f : 2.4f);
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (FULL_BODY.equals(key) && this.world.isRemote) {
				this.setSize(2.4f, this.isFullBody() ? 3.6f : 2.4f);
			}
		}

		@Override
		public void onUpdate() {
			EntityLivingBase owner = this.getOwnerPlayer();
			if (owner != null && owner.swingProgressInt == -1) {
				this.swingArm(EnumHand.MAIN_HAND);
			}
			if (this.ticksExisted % 20 == 1) {
				this.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 22, 2, false, false));
			}
			super.onUpdate();
		}

		@Override
		public boolean shouldShowSword() {
			return false;
		}

		@Override
		public void setShowSword(boolean show) {
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			if (!this.world.isRemote && this.getOwnerPlayer() != null
			 && this.getOwnerPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemMangekyoSharingan.helmet
			 && entity instanceof EntityLivingBase && !entity.equals(this.getOwnerPlayer()))
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, 200, 0, false, false));
			super.collideWithEntity(entity);
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderSusanooSkeleton(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderSusanooSkeleton extends RenderLiving<EntityCustom> {
			private final ResourceLocation mainTexture = new ResourceLocation("narutomod:textures/susanooskeleton.png");
			private final ResourceLocation flameTexture = new ResourceLocation("narutomod:textures/gas256.png");

			public RenderSusanooSkeleton(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelSusanooSkeleton(), 1.5F);
			}

			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				this.setModelVisibilities(entity);
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			private void setModelVisibilities(EntityCustom entity) {
				ModelSusanooSkeleton model = (ModelSusanooSkeleton) this.getMainModel();
				model.bipedLeftLeg.showModel = false;
				model.bipedRightLeg.showModel = false;
				boolean flag = entity.isFullBody();
				model.bipedHead.showModel = flag;
				model.bipedHeadwear.showModel = flag;
				model.bipedRightArm.showModel = flag;
				model.bipedLeftArm.showModel = flag;
			}

			@Override
			protected void renderModel(EntityCustom entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
				if (this.bindEntityTexture(entity)) {
					ModelSusanooSkeleton model = (ModelSusanooSkeleton) this.getMainModel();
					model.renderFlame = false;
					this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
					this.bindTexture(this.flameTexture);
					model.renderFlame = true;
					this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor * 0.99f);
				}
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.mainTexture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelSusanooSkeleton extends ModelBiped {
			//private final ModelRenderer bipedHead;
			private final ModelRenderer head7_r1;
			private final ModelRenderer head6_r1;
			private final ModelRenderer head5_r1;
			private final ModelRenderer head_r1;
			private final ModelRenderer HornStyle1;
			private final ModelRenderer right;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer left;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer cube_r6;
			private final ModelRenderer HornStyle2;
			private final ModelRenderer right5;
			private final ModelRenderer cube_r7;
			private final ModelRenderer cube_r8;
			private final ModelRenderer cube_r9;
			private final ModelRenderer left8;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer cube_r13;
			private final ModelRenderer cube_r14;
			private final ModelRenderer cube_r15;
			private final ModelRenderer rightHand;
			private final ModelRenderer rightFingers;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer cube_r16;
			private final ModelRenderer cube_r17;
			private final ModelRenderer cube_r18;
			private final ModelRenderer leftHand;
			private final ModelRenderer leftFingers;
			private final float maxAlpha = 0.6f;
			private boolean renderFlame;
			private final float[][] rightArmPreset = { { -1.0472F, 1.0472F, 0.5236F }, { -1.0472F, -1.0472F, 0.0F }, { -0.9599F, 0.0F, 0.0F } };

			public ModelSusanooSkeleton() {
				textureWidth = 512;
				textureHeight = 512;

				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -8.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 36, 147, -10.0F, -23.0F, -10.0F, 20, 14, 20, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 203, -9.0F, -24.0F, -9.0F, 18, 1, 18, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 85, 201, -9.0F, -21.0F, -11.0F, 18, 12, 3, 0.0F, false));

				head7_r1 = new ModelRenderer(this);
				head7_r1.setRotationPoint(0.0F, -0.5F, -9.0F);
				bipedHead.addChild(head7_r1);
				setRotationAngle(head7_r1, 0.1745F, 0.0F, 0.0F);
				head7_r1.cubeList.add(new ModelBox(head7_r1, 152, 76, -4.0F, -1.5F, 0.5F, 8, 3, 2, 0.0F, false));

				head6_r1 = new ModelRenderer(this);
				head6_r1.setRotationPoint(0.0F, -1.3025F, 4.122F);
				bipedHead.addChild(head6_r1);
				setRotationAngle(head6_r1, 0.4363F, 0.0F, 0.0F);
				head6_r1.cubeList.add(new ModelBox(head6_r1, 115, 43, -4.0F, -5.0F, -0.622F, 8, 10, 8, 0.0F, false));

				head5_r1 = new ModelRenderer(this);
				head5_r1.setRotationPoint(0.0F, -7.6846F, -8.9841F);
				bipedHead.addChild(head5_r1);
				setRotationAngle(head5_r1, 0.2182F, 0.0F, 0.0F);
				head5_r1.cubeList.add(new ModelBox(head5_r1, 141, 108, -7.0F, -2.5F, -1.5F, 14, 3, 3, 0.0F, false));

				head_r1 = new ModelRenderer(this);
				head_r1.setRotationPoint(0.0F, -15.0F, 23.0F);
				bipedHead.addChild(head_r1);
				setRotationAngle(head_r1, 0.1309F, 0.0F, 0.0F);
				head_r1.cubeList.add(new ModelBox(head_r1, 132, 149, -9.0F, 1.0F, -33.0F, 18, 9, 18, 0.0F, false));

				HornStyle1 = new ModelRenderer(this);
				HornStyle1.setRotationPoint(0.0F, 32.0F, 0.0F);
				bipedHead.addChild(HornStyle1);


				right = new ModelRenderer(this);
				right.setRotationPoint(0.0F, 0.0F, 0.0F);
				HornStyle1.addChild(right);


				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(-19.0517F, -58.4594F, -0.5F);
				right.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.0F, 0.0F, 1.1781F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 312, -17.0F, -15.5406F, -15.0F, 33, 30, 30, -14.0F, false));

				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(-13.1084F, -54.1158F, -0.5F);
				right.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.0F, 0.0F, 0.1745F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 312, -14.0F, -15.0F, -15.0F, 31, 30, 30, -12.0F, false));

				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(-15.0F, -54.5F, -0.5F);
				right.addChild(cube_r3);
				setRotationAngle(cube_r3, 0.0F, 0.0F, 0.7854F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 312, -17.5F, -15.0F, -15.0F, 32, 30, 30, -13.0F, false));

				left = new ModelRenderer(this);
				left.setRotationPoint(0.0F, 0.0F, 0.0F);
				HornStyle1.addChild(left);


				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(19.0517F, -58.4594F, -0.5F);
				left.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.0F, 0.0F, -1.1781F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 312, -16.0F, -15.5406F, -15.0F, 33, 30, 30, -14.0F, true));

				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(15.0F, -54.5F, -0.5F);
				left.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.0F, 0.0F, -0.7854F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 312, -14.5F, -15.0F, -15.0F, 32, 30, 30, -13.0F, true));

				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(13.1084F, -54.1158F, -0.5F);
				left.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.0F, 0.0F, -0.1745F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 312, -17.0F, -15.0F, -15.0F, 31, 30, 30, -12.0F, true));

				HornStyle2 = new ModelRenderer(this);
				HornStyle2.setRotationPoint(0.0F, 35.0F, -2.0F);
				bipedHead.addChild(HornStyle2);


				right5 = new ModelRenderer(this);
				right5.setRotationPoint(-5.8867F, -56.8719F, -5.5F);
				HornStyle2.addChild(right5);
				setRotationAngle(right5, 0.0F, -1.2217F, 0.0F);


				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(-10.165F, -4.5875F, 0.0F);
				right5.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.0F, 0.0F, 1.1781F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 312, -17.0F, -15.5406F, -15.0F, 33, 30, 30, -14.0F, false));

				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(-4.2217F, -0.2439F, 0.0F);
				right5.addChild(cube_r8);
				setRotationAngle(cube_r8, 0.0F, 0.0F, 0.1745F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 312, -14.0F, -15.0F, -15.0F, 31, 30, 30, -12.0F, false));

				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(-6.1133F, -0.6281F, 0.0F);
				right5.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.0F, 0.0F, 0.7854F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 312, -17.5F, -15.0F, -15.0F, 32, 30, 30, -13.0F, false));

				left8 = new ModelRenderer(this);
				left8.setRotationPoint(5.8867F, -56.8719F, -5.5F);
				HornStyle2.addChild(left8);
				setRotationAngle(left8, 0.0F, 1.2217F, 0.0F);


				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(10.165F, -4.5875F, 0.0F);
				left8.addChild(cube_r10);
				setRotationAngle(cube_r10, 0.0F, 0.0F, -1.1781F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 312, -16.0F, -15.5406F, -15.0F, 33, 30, 30, -14.0F, true));

				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(4.2217F, -0.2439F, 0.0F);
				left8.addChild(cube_r11);
				setRotationAngle(cube_r11, 0.0F, 0.0F, -0.1745F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 312, -17.0F, -15.0F, -15.0F, 31, 30, 30, -12.0F, true));

				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(6.1133F, -0.6281F, 0.0F);
				left8.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.0F, 0.0F, -0.7854F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 312, -14.5F, -15.0F, -15.0F, 32, 30, 30, -13.0F, true));

				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, -8.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 177, 202, -9.0F, -21.0F, -11.05F, 18, 12, 0, 0.0F, false));

				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, -8.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 50, -16.0F, 0.0F, -14.0F, 32, 24, 27, 0.0F, true));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -13.0F, 18.0F, -11.0F, 26, 18, 22, 0.0F, false));

				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-17.0F, -7.0F, -1.0F);				
		
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(-8.25F, 33.9513F, -3.6693F);
				bipedRightArm.addChild(cube_r13);
				setRotationAngle(cube_r13, -0.6109F, 0.0F, 0.0F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 115, -3.0F, -8.0F, -3.0F, 6, 16, 6, 0.0F, false));
		
				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(-4.0F, 3.0F, 0.0F);
				bipedRightArm.addChild(cube_r14);
				setRotationAngle(cube_r14, 0.0F, 0.0F, 0.1745F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 142, -3.0F, 0.0F, -3.0F, 6, 28, 6, 0.0F, false));
		
				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(-3.3421F, 0.9674F, 0.0F);
				bipedRightArm.addChild(cube_r15);
				setRotationAngle(cube_r15, 0.0F, 0.0F, 1.1345F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 114, 0, -3.0F, -8.0F, -5.0F, 6, 16, 10, 0.0F, false));
		
				rightHand = new ModelRenderer(this);
				rightHand.setRotationPoint(-8.25F, 40.6069F, -8.3621F);
				bipedRightArm.addChild(rightHand);
				setRotationAngle(rightHand, -0.6109F, 0.0F, 0.0F);
				rightHand.cubeList.add(new ModelBox(rightHand, 2, 246, -8.0F, -3.1436F, -11.9735F, 19, 23, 21, -3.0F, false));
		
				rightFingers = new ModelRenderer(this);
				rightFingers.setRotationPoint(-5.0F, 16.8564F, -1.4735F);
				rightHand.addChild(rightFingers);
				setRotationAngle(rightFingers, 0.0F, 0.0F, 1.0472F);
				rightFingers.cubeList.add(new ModelBox(rightFingers, 97, 246, -3.0F, -20.0F, -10.5F, 19, 23, 21, -3.0F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(17.0F, -7.0F, -1.0F);
				
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(8.25F, 33.9513F, -3.6693F);
				bipedLeftArm.addChild(cube_r16);
				setRotationAngle(cube_r16, -0.6109F, 0.0F, 0.0F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 115, -3.0F, -8.0F, -3.0F, 6, 16, 6, 0.0F, true));
		
				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(4.0F, 3.0F, 0.0F);
				bipedLeftArm.addChild(cube_r17);
				setRotationAngle(cube_r17, 0.0F, 0.0F, -0.1745F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 0, 142, -3.0F, 0.0F, -3.0F, 6, 28, 6, 0.0F, true));
		
				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(3.3421F, 0.9674F, 0.0F);
				bipedLeftArm.addChild(cube_r18);
				setRotationAngle(cube_r18, 0.0F, 0.0F, -1.1345F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 114, 0, -3.0F, -8.0F, -5.0F, 6, 16, 10, 0.0F, true));
		
				leftHand = new ModelRenderer(this);
				leftHand.setRotationPoint(8.25F, 40.6069F, -8.3621F);
				bipedLeftArm.addChild(leftHand);
				setRotationAngle(leftHand, -0.6109F, 0.0F, 0.0F);
				leftHand.cubeList.add(new ModelBox(leftHand, 2, 246, -11.0F, -3.1436F, -11.9735F, 19, 23, 21, -3.0F, true));
		
				leftFingers = new ModelRenderer(this);
				leftFingers.setRotationPoint(5.0F, 16.8564F, -1.4735F);
				leftHand.addChild(leftFingers);
				leftFingers.cubeList.add(new ModelBox(leftFingers, 97, 246, -16.0F, -20.0F, -10.5F, 19, 23, 21, -3.0F, true));
			}

			@Override
			public void render(Entity entity, float f, float f1, float age, float f3, float f4, float f5) {
				bipedRightLeg.showModel = false;
				bipedLeftLeg.showModel = false;
				HornStyle1.showModel = false;
				int color = ((EntityCustom) entity).getFlameColor();
				float red = (float) (color >> 16 & 0xFF) / 255.0F;
				float green = (float) (color >> 8 & 0xFF) / 255.0F;
				float blue = (float) (color & 0xFF) / 255.0F;
				GlStateManager.enableBlend();
				//GlStateManager.enableCull();
				//GlStateManager.depthMask(false);
				if (this.renderFlame) {
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					GlStateManager.translate(0.0F, age * 0.01F, 0.0F);
					GlStateManager.matrixMode(5888);
					bipedHeadwear.showModel = false;
					bipedBody.showModel = false;
				}
				GlStateManager.color(red, green, blue, this.maxAlpha * Math.min(age / 60f, 1f));
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				super.render(entity, f, f1, age, f3, f4, f5);
				GlStateManager.color(1f, 1f, 1f, 1f);
				bipedHeadwear.render(f5);
				GlStateManager.enableLighting();
				if (this.renderFlame) {
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					GlStateManager.matrixMode(5888);
					bipedHeadwear.showModel = true;
					bipedBody.showModel = true;
				}
				//GlStateManager.depthMask(true);
				//GlStateManager.disableCull();
				GlStateManager.disableBlend();
			}

			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}

			@Override
			public void setRotationAngles(float limbSwing, float f1, float f2, float f3, float f4, float f5, Entity entityIn) {
				super.setRotationAngles(limbSwing * 2.0F / entityIn.height, f1, f2, f3, f4, f5, entityIn);
				bipedHead.rotationPointY += -8.0F;
				bipedHeadwear.rotationPointY += -8.0F;
				bipedRightArm.rotationPointZ += -1.0F;
				bipedRightArm.rotationPointX += -12.0F;
				bipedLeftArm.rotationPointZ += -1.0F;
				bipedLeftArm.rotationPointX += 12.0F;
				if (this.swingProgress > 0.0F) {
					if (this.swingProgress <= 0.4F) {
						bipedRightArm.rotateAngleX = rightArmPreset[0][0] * this.swingProgress / 0.4F;
						bipedRightArm.rotateAngleY = rightArmPreset[0][1] * this.swingProgress / 0.4F;
						bipedRightArm.rotateAngleZ = rightArmPreset[0][2] * this.swingProgress / 0.4F;
					} else if (this.swingProgress <= 0.6F) {
						bipedRightArm.rotateAngleX = rightArmPreset[0][0] + (rightArmPreset[1][0] - rightArmPreset[0][0]) * (this.swingProgress - 0.4F) / 0.2F;
						bipedRightArm.rotateAngleY = rightArmPreset[0][1] + (rightArmPreset[1][1] - rightArmPreset[0][1]) * (this.swingProgress - 0.4F) / 0.2F;
						bipedRightArm.rotateAngleZ = rightArmPreset[0][2] + (rightArmPreset[1][2] - rightArmPreset[0][2]) * (this.swingProgress - 0.4F) / 0.2F;
					} else {
						bipedRightArm.rotateAngleX = rightArmPreset[1][0] + (0.0F - rightArmPreset[1][0]) * (this.swingProgress - 0.6F) / 0.4F;
						bipedRightArm.rotateAngleY = rightArmPreset[1][1] + (0.0F - rightArmPreset[1][1]) * (this.swingProgress - 0.6F) / 0.4F;
						bipedRightArm.rotateAngleZ = rightArmPreset[1][2] + (0.0F - rightArmPreset[1][2]) * (this.swingProgress - 0.6F) / 0.4F;
					}
					bipedRightArm.rotateAngleY += bipedBody.rotateAngleY * 2;
				}
			}
		}
	}
}
