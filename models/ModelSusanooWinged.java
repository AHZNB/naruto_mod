//Made with Blockbench
//Paste this code into your mod.

public static class ModelSusanooWinged extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer bipedBody;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer rightWing;
	private final ModelRenderer rightWingTip;
	private final ModelRenderer leftWing;
	private final ModelRenderer leftWingTip;

	public ModelSusanooWinged() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F,
				-4.0F, 8, 8, 8, 0.0F, false));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 0, -4.0F, -8.0F,
				-4.0F, 8, 8, 8, 0.5F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F,
				-2.0F, 8, 12, 4, 0.0F, false));
		bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F,
				-2.0F, 8, 12, 4, 0.5F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F,
				-2.0F, -2.0F, 4, 12, 4, 0.0F, false));
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F,
				-2.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F,
				-2.0F, -2.0F, 4, 12, 4, 0.0F, true));
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F,
				-2.0F, -2.0F, 4, 12, 4, 0.5F, true));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F,
				0.0F, -2.0F, 4, 12, 4, 0.0F, false));
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F,
				0.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F,
				0.0F, -2.0F, 4, 12, 4, 0.0F, true));
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F,
				0.0F, -2.0F, 4, 12, 4, 0.5F, true));

		rightWing = new ModelRenderer(this);
		rightWing.setRotationPoint(-1.0F, 7.0F, 3.0F);
		setRotationAngle(rightWing, 0.0F, 1.309F, 0.0F);
		rightWing.cubeList.add(new ModelBox(rightWing, 0, 48, -4.0F, -7.0F,
				0.0F, 4, 15, 1, 0.0F, false));
		rightWing.cubeList.add(new ModelBox(rightWing, 0, 48, -8.0F, -9.0F,
				0.0F, 4, 15, 1, 0.0F, false));
		rightWing.cubeList.add(new ModelBox(rightWing, 0, 48, -12.0F, -11.0F,
				0.0F, 4, 15, 1, 0.0F, false));
		rightWing.cubeList.add(new ModelBox(rightWing, 0, 48, -16.0F, -13.0F,
				0.0F, 4, 15, 1, 0.0F, false));

		rightWingTip = new ModelRenderer(this);
		rightWingTip.setRotationPoint(-15.75F, -5.0F, 0.5F);
		rightWing.addChild(rightWingTip);
		rightWingTip.cubeList.add(new ModelBox(rightWingTip, 0, 48, 11.75F,
				-2.0F, -0.5F, 4, 15, 1, 0.0F, false));
		rightWingTip.cubeList.add(new ModelBox(rightWingTip, 0, 48, 7.75F,
				-4.0F, -0.5F, 4, 15, 1, 0.0F, false));
		rightWingTip.cubeList.add(new ModelBox(rightWingTip, 0, 48, 3.75F,
				-6.0F, -0.5F, 4, 15, 1, 0.0F, false));
		rightWingTip.cubeList.add(new ModelBox(rightWingTip, 0, 48, -0.25F,
				-8.0F, -0.5F, 4, 15, 1, 0.0F, false));

		leftWing = new ModelRenderer(this);
		leftWing.setRotationPoint(1.0F, 7.0F, 3.0F);
		setRotationAngle(leftWing, 0.0F, -1.309F, 0.0F);
		leftWing.cubeList.add(new ModelBox(leftWing, 0, 48, 0.0F, -7.0F, 0.0F,
				4, 15, 1, 0.0F, true));
		leftWing.cubeList.add(new ModelBox(leftWing, 0, 48, 4.0F, -9.0F, 0.0F,
				4, 15, 1, 0.0F, true));
		leftWing.cubeList.add(new ModelBox(leftWing, 0, 48, 8.0F, -11.0F, 0.0F,
				4, 15, 1, 0.0F, true));
		leftWing.cubeList.add(new ModelBox(leftWing, 0, 48, 12.0F, -13.0F,
				0.0F, 4, 15, 1, 0.0F, true));

		leftWingTip = new ModelRenderer(this);
		leftWingTip.setRotationPoint(15.75F, -5.0F, 0.5F);
		leftWing.addChild(leftWingTip);
		leftWingTip.cubeList.add(new ModelBox(leftWingTip, 0, 48, -15.75F,
				-2.0F, -0.5F, 4, 15, 1, 0.0F, true));
		leftWingTip.cubeList.add(new ModelBox(leftWingTip, 0, 48, -11.75F,
				-4.0F, -0.5F, 4, 15, 1, 0.0F, true));
		leftWingTip.cubeList.add(new ModelBox(leftWingTip, 0, 48, -7.75F,
				-6.0F, -0.5F, 4, 15, 1, 0.0F, true));
		leftWingTip.cubeList.add(new ModelBox(leftWingTip, 0, 48, -3.75F,
				-8.0F, -0.5F, 4, 15, 1, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		bipedHead.render(f5);
		bipedBody.render(f5);
		bipedRightArm.render(f5);
		bipedLeftArm.render(f5);
		bipedRightLeg.render(f5);
		bipedLeftLeg.render(f5);
		rightWing.render(f5);
		leftWing.render(f5);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y,
			float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}