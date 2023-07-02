// Made with Blockbench 4.4.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelRockGolem extends ModelBase {
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
		ironGolemRightArm.setRotationPoint(-8.0F, -10.0F, -1.0F);

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
		ironGolemLeftArm.setRotationPoint(8.0F, -10.0F, -1.0F);

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
		ironGolemRightLeg.setRotationPoint(-3.5F, 6.5F, 0.0F);

		right_leg = new ModelRenderer(this);
		right_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
		ironGolemRightLeg.addChild(right_leg);
		setRotationAngle(right_leg, -0.5236F, 0.5236F, 0.0F);
		right_leg.cubeList.add(new ModelBox(right_leg, 58, 0, -5.5F, 0.0F, -3.0F, 6, 10, 6, 0.0F, false));

		right_leg2 = new ModelRenderer(this);
		right_leg2.setRotationPoint(-2.5F, 10.0F, -3.0F);
		right_leg.addChild(right_leg2);
		setRotationAngle(right_leg2, 0.5236F, 0.0F, 0.0F);
		right_leg2.cubeList.add(new ModelBox(right_leg2, 0, 44, -3.0F, 0.0F, 0.0F, 6, 10, 6, 0.0F, false));

		ironGolemLeftLeg = new ModelRenderer(this);
		ironGolemLeftLeg.setRotationPoint(3.5F, 6.5F, 0.0F);

		left_leg = new ModelRenderer(this);
		left_leg.setRotationPoint(0.0F, 0.0F, 0.0F);
		ironGolemLeftLeg.addChild(left_leg);
		setRotationAngle(left_leg, -0.5236F, -0.5236F, 0.0F);
		left_leg.cubeList.add(new ModelBox(left_leg, 58, 0, -0.5F, 0.0F, -3.0F, 6, 10, 6, 0.0F, true));

		left_leg2 = new ModelRenderer(this);
		left_leg2.setRotationPoint(2.5F, 10.0F, -3.0F);
		left_leg.addChild(left_leg2);
		setRotationAngle(left_leg2, 0.5236F, 0.0F, 0.0F);
		left_leg2.cubeList.add(new ModelBox(left_leg2, 0, 44, -3.0F, 0.0F, 0.0F, 6, 10, 6, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		ironGolemHead.render(f5);
		ironGolemBody.render(f5);
		ironGolemRightArm.render(f5);
		ironGolemLeftArm.render(f5);
		ironGolemRightLeg.render(f5);
		ironGolemLeftLeg.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}
}