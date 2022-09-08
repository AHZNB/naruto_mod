//Made with Blockbench
//Paste this code into your mod.

public static class ModelHunchman extends ModelBase {
	private final ModelRenderer right_leg;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer left_leg;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;
	private final ModelRenderer groin;
	private final ModelRenderer waist;
	private final ModelRenderer chest;
	private final ModelRenderer right_arm;
	private final ModelRenderer bone9;
	private final ModelRenderer bone13;
	private final ModelRenderer left_arm;
	private final ModelRenderer bone10;
	private final ModelRenderer bone11;
	private final ModelRenderer head;

	public ModelHunchman() {
		textureWidth = 80;
		textureHeight = 40;

		right_leg = new ModelRenderer(this);
		right_leg.setRotationPoint(-3.0F, 8.75F, -1.0F);
		setRotationAngle(right_leg, 0.0F, 0.0F, 0.0F);
		right_leg.cubeList.add(new ModelBox(right_leg, 76, 0, -0.5F, 0.0F,
				-1.0F, 1, 1, 1, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(-0.6F, 4.516F, -2.1F);
		setRotationAngle(bone, -0.5236F, 0.2618F, 0.0F);
		right_leg.addChild(bone);
		bone.cubeList.add(new ModelBox(bone, 24, 0, -2.0F, -4.0F, -2.0F, 4, 8,
				4, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 1.3617F, -1.35F);
		setRotationAngle(bone2, 1.0472F, 0.0F, 0.0F);
		bone.addChild(bone2);
		bone2.cubeList.add(new ModelBox(bone2, 24, 0, -2.0F, 2.6383F, -3.65F,
				4, 8, 4, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, 4.3617F, -0.35F);
		setRotationAngle(bone3, 0.4363F, 0.0F, 0.0F);
		bone.addChild(bone3);
		bone3.cubeList.add(new ModelBox(bone3, 24, 0, -2.0F, -0.9787F,
				-1.3646F, 4, 2, 2, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-0.1F, 4.8598F, 0.25F);
		bone.addChild(bone4);
		bone4.cubeList.add(new ModelBox(bone4, 24, 0, -1.9F, -0.9498F, -1.362F,
				4, 2, 2, -0.01F, false));

		left_leg = new ModelRenderer(this);
		left_leg.setRotationPoint(3.0F, 8.75F, -1.0F);
		setRotationAngle(left_leg, 0.0F, 0.0F, 0.0F);
		left_leg.cubeList.add(new ModelBox(left_leg, 76, 0, -0.5F, 0.0F, -1.0F,
				1, 1, 1, 0.0F, true));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.6F, 4.516F, -2.1F);
		setRotationAngle(bone5, -0.5236F, -0.2618F, 0.0F);
		left_leg.addChild(bone5);
		bone5.cubeList.add(new ModelBox(bone5, 24, 0, -2.0F, -4.0F, -2.0F, 4,
				8, 4, 0.0F, true));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 1.3617F, -1.35F);
		setRotationAngle(bone6, 1.0472F, 0.0F, 0.0F);
		bone5.addChild(bone6);
		bone6.cubeList.add(new ModelBox(bone6, 24, 0, -2.0F, 2.6383F, -3.65F,
				4, 8, 4, 0.0F, true));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, 4.3617F, -0.35F);
		setRotationAngle(bone7, 0.4363F, 0.0F, 0.0F);
		bone5.addChild(bone7);
		bone7.cubeList.add(new ModelBox(bone7, 24, 0, -2.0F, -0.9787F,
				-1.3646F, 4, 2, 2, 0.0F, true));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.1F, 4.8598F, 0.25F);
		bone5.addChild(bone8);
		bone8.cubeList.add(new ModelBox(bone8, 24, 0, -2.1F, -0.9498F, -1.362F,
				4, 2, 2, -0.01F, true));

		groin = new ModelRenderer(this);
		groin.setRotationPoint(0.0F, 9.5F, -1.0F);
		groin.cubeList.add(new ModelBox(groin, 24, 0, -5.5F, -2.5F, -2.0F, 11,
				5, 4, 0.0F, false));

		waist = new ModelRenderer(this);
		waist.setRotationPoint(0.0F, 4.9544F, -1.0209F);
		setRotationAngle(waist, 0.1745F, 0.0F, 0.0F);
		waist.cubeList.add(new ModelBox(waist, 24, 0, -6.5F, -2.0F, -2.5F, 13,
				4, 5, 0.0F, false));

		chest = new ModelRenderer(this);
		chest.setRotationPoint(0.0F, -0.0456F, -2.5209F);
		setRotationAngle(chest, 0.1745F, 0.0F, 0.0F);
		chest.cubeList.add(new ModelBox(chest, 24, 0, -7.5F, -4.0F, -3.0F, 15,
				8, 7, 0.0F, false));

		right_arm = new ModelRenderer(this);
		right_arm.setRotationPoint(-7.5F, -2.0F, -1.5F);
		setRotationAngle(right_arm, 0.0F, 0.0F, 0.0F);
		right_arm.cubeList.add(new ModelBox(right_arm, 0, 0, -0.5F, -0.5F,
				-0.5001F, 1, 1, 1, 0.0F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(0.0F, 0.5F, 0.0F);
		setRotationAngle(bone9, 0.3491F, 0.0F, 0.0F);
		right_arm.addChild(bone9);
		bone9.cubeList.add(new ModelBox(bone9, 24, 0, -4.0F, -2.5603F, -2.842F,
				4, 12, 5, 0.0F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(-2.0F, 4.6206F, 0.684F);
		setRotationAngle(bone13, -0.7854F, 0.0F, 0.0F);
		bone9.addChild(bone13);
		bone13.cubeList.add(new ModelBox(bone13, 24, 0, -2.0F, 2.7189F,
				-1.2321F, 4, 12, 5, 0.0F, false));

		left_arm = new ModelRenderer(this);
		left_arm.setRotationPoint(7.5F, -2.0F, -1.5F);
		setRotationAngle(left_arm, 0.0F, 0.0F, 0.0F);
		left_arm.cubeList.add(new ModelBox(left_arm, 0, 0, -0.5F, -0.5F,
				-0.5001F, 1, 1, 1, 0.0F, true));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.0F, 0.5F, 0.0F);
		setRotationAngle(bone10, 0.3491F, 0.0F, 0.0F);
		left_arm.addChild(bone10);
		bone10.cubeList.add(new ModelBox(bone10, 24, 0, 0.0F, -2.5603F,
				-2.842F, 4, 12, 5, 0.0F, true));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(2.0F, 4.6206F, 0.684F);
		setRotationAngle(bone11, -0.7854F, 0.0F, 0.0F);
		bone10.addChild(bone11);
		bone11.cubeList.add(new ModelBox(bone11, 24, 0, -2.0F, 2.7189F,
				-1.2321F, 4, 12, 5, 0.0F, true));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -4.5F, -3.5F);
		head.cubeList.add(new ModelBox(head, 0, 0, -2.5F, -4.5F, -2.5F, 5, 5,
				5, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		float scale = 2f;
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(0.0F, -0.75F, 0.0F);
		right_leg.render(f5);
		left_leg.render(f5);
		groin.render(f5);
		waist.render(f5);
		chest.render(f5);
		right_arm.render(f5);
		left_arm.render(f5);
		head.render(f5);
		GlStateManager.popMatrix();
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
		left_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * -1.0F
				* limbSwingAmount;
		right_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.3331F
				+ (float) Math.PI)
				* limbSwingAmount;
		head.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
		head.rotateAngleX = headPitch / (180F / (float) Math.PI);
		right_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.0F
				* limbSwingAmount;
		left_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.3331F)
				* limbSwingAmount;
		right_arm.rotateAngleZ = 0.0F;
		left_arm.rotateAngleZ = 0.0F;

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
			modelrenderer.rotateAngleY += chest.rotateAngleY * 2.0F;
			modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress
					* (float) Math.PI)
					* -0.4F;
		}

		right_arm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		left_arm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		right_arm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		left_arm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
	}

	protected ModelRenderer getArmForSide(EnumHandSide side) {
		return side == EnumHandSide.LEFT ? left_arm : right_arm;
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