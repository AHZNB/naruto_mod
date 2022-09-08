//Made with Blockbench
//Paste this code into your mod.

public static class ModelSusanooSkeleton extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer bodywear;
	private final ModelRenderer rightArmwear;
	private final ModelRenderer bone;
	private final ModelRenderer leftArmwear;
	private final ModelRenderer bone2;

	public ModelSusanooSkeleton() {
		textureWidth = 64;
		textureHeight = 32;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -13.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -14.0F, -4.0F, 8, 8,
				8, 6.0F, false));

		bodywear = new ModelRenderer(this);
		bodywear.setRotationPoint(0.0F, 6.0F, 0.0F);
		bodywear.cubeList.add(new ModelBox(bodywear, 16, 16, -4.0F, -6.0F,
				-2.0F, 8, 12, 4, 12.0F, false));

		rightArmwear = new ModelRenderer(this);
		rightArmwear.setRotationPoint(-20.0F, -9.0F, 0.0F);
		rightArmwear.cubeList.add(new ModelBox(rightArmwear, 40, 16, -4.0F,
				-1.0F, -1.0F, 2, 12, 2, 2.0F, false));
		rightArmwear.cubeList.add(new ModelBox(rightArmwear, 40, 16, -4.0F,
				15.0F, -1.0F, 2, 12, 2, 2.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(7.0F, 25.0F, 0.0F);
		setRotationAngle(bone, -0.5236F, 0.0F, 0.0F);
		rightArmwear.addChild(bone);
		bone.cubeList.add(new ModelBox(bone, 40, 16, -11.0F, 2.0F, -1.0F, 2,
				12, 2, 2.0F, false));
		bone.cubeList.add(new ModelBox(bone, 40, 16, -11.0F, 18.0F, -1.0F, 2,
				12, 2, 2.0F, false));

		leftArmwear = new ModelRenderer(this);
		leftArmwear.setRotationPoint(20.0F, -9.0F, 0.0F);
		leftArmwear.cubeList.add(new ModelBox(leftArmwear, 40, 16, 2.0F, -1.0F,
				-1.0F, 2, 12, 2, 2.0F, true));
		leftArmwear.cubeList.add(new ModelBox(leftArmwear, 40, 16, 2.0F, 15.0F,
				-1.0F, 2, 12, 2, 2.0F, true));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-7.0F, 25.0F, 0.0F);
		setRotationAngle(bone2, -0.5236F, 0.0F, 0.0F);
		leftArmwear.addChild(bone2);
		bone2.cubeList.add(new ModelBox(bone2, 40, 16, 9.0F, 2.0F, -1.0F, 2,
				12, 2, 2.0F, true));
		bone2.cubeList.add(new ModelBox(bone2, 40, 16, 9.0F, 18.0F, -1.0F, 2,
				12, 2, 2.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableNormalize();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		head.render(f5);
		bodywear.render(f5);
		rightArmwear.render(f5);
		leftArmwear.render(f5);

		GlStateManager.disableBlend();
		GlStateManager.disableNormalize();
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y,
			float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	public void setRotationAngles(float limbSwing, float limbSwingAmount,
			float ageInTicks, float netHeadYaw, float headPitch,
			float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks,
				netHeadYaw, headPitch, scaleFactor, entityIn);
		rightArmwear.rotateAngleX = MathHelper.cos(limbSwing * 0.3331F
				+ (float) Math.PI)
				* limbSwingAmount;
		head.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
		head.rotateAngleX = headPitch / (180F / (float) Math.PI);
		leftArmwear.rotateAngleX = MathHelper.cos(limbSwing * 0.3331F)
				* limbSwingAmount;
		rightArmwear.rotateAngleZ = 0.0F;
		leftArmwear.rotateAngleZ = 0.0F;

		if (this.swingProgress > 0.0F) {
			EnumHandSide enumhandside = this.getMainHand(entityIn);
			ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
			float f1 = this.swingProgress;
			f1 = 1.0F - this.swingProgress;
			f1 = f1 * f1;
			f1 = f1 * f1;
			f1 = 1.0F - f1;
			float f2 = MathHelper.sin(f1 * (float) Math.PI);
			float f3 = MathHelper.sin(this.swingProgress * (float) Math.PI)
					* -(head.rotateAngleX - 0.7F) * 0.75F;
			modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
			modelrenderer.rotateAngleY += bodywear.rotateAngleY * 2.0F;
			modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress
					* (float) Math.PI)
					* -0.4F;
		}

		rightArmwear.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		leftArmwear.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		rightArmwear.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		leftArmwear.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
	}
	protected ModelRenderer getArmForSide(EnumHandSide side) {
		return side == EnumHandSide.LEFT ? leftArmwear : rightArmwear;
	}
	protected EnumHandSide getMainHand(Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) entityIn;
			EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
			return entitylivingbase.swingingHand == EnumHand.MAIN_HAND
					? enumhandside
					: enumhandside.opposite();
		} else {
			return EnumHandSide.RIGHT;
		}
	}
}