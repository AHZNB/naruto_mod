// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelSizPathRobe extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer bipedBody;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer skirtRight;
	private final ModelRenderer bone2;
	private final ModelRenderer bone;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer skirtLeft;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;
	private final ModelRenderer bone9;
	private final ModelRenderer bone10;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer bipedLeftLeg;

	public ModelSizPathRobe() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 0.0F, -1.8F);
		bipedBody.addChild(bone5);
		setRotationAngle(bone5, -0.3927F, 0.0F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 24, 48, -4.0F, -4.0F, 0.0F, 8, 4, 4, 1.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 1.0F, 3.0F);
		bone5.addChild(bone6);
		setRotationAngle(bone6, -0.5236F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 44, 0, -4.0F, -5.0F, -1.0F, 8, 4, 2, 1.0F, false));

		skirtRight = new ModelRenderer(this);
		skirtRight.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedBody.addChild(skirtRight);

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -12.0F, -2.0F);
		skirtRight.addChild(bone2);
		setRotationAngle(bone2, -0.1745F, 0.0F, 0.1745F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 48, -4.0F, 0.0F, 0.0F, 4, 8, 0, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(-4.0F, -12.25F, 0.0F);
		skirtRight.addChild(bone);
		setRotationAngle(bone, -0.1745F, 0.0F, 0.1745F);
		bone.cubeList.add(new ModelBox(bone, 8, 48, 0.0F, 0.0F, -2.0F, 0, 8, 4, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-4.0F, -12.25F, 0.0F);
		skirtRight.addChild(bone3);
		setRotationAngle(bone3, 0.1745F, 0.0F, 0.1745F);
		bone3.cubeList.add(new ModelBox(bone3, 16, 48, 0.0F, 0.0F, -2.0F, 0, 8, 4, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, -12.0F, 2.0F);
		skirtRight.addChild(bone4);
		setRotationAngle(bone4, 0.1745F, 0.0F, 0.1745F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 56, -4.0F, 0.0F, 0.0F, 4, 8, 0, 0.0F, false));

		skirtLeft = new ModelRenderer(this);
		skirtLeft.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedBody.addChild(skirtLeft);

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, -12.0F, -2.0F);
		skirtLeft.addChild(bone7);
		setRotationAngle(bone7, -0.1745F, 0.0F, -0.1745F);
		bone7.cubeList.add(new ModelBox(bone7, 0, 48, 0.0F, 0.0F, 0.0F, 4, 8, 0, 0.0F, true));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(4.0F, -12.25F, 0.0F);
		skirtLeft.addChild(bone8);
		setRotationAngle(bone8, -0.1745F, 0.0F, -0.1745F);
		bone8.cubeList.add(new ModelBox(bone8, 8, 48, 0.0F, 0.0F, -2.0F, 0, 8, 4, 0.0F, true));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(4.0F, -12.25F, 0.0F);
		skirtLeft.addChild(bone9);
		setRotationAngle(bone9, 0.1745F, 0.0F, -0.1745F);
		bone9.cubeList.add(new ModelBox(bone9, 16, 48, 0.0F, 0.0F, -2.0F, 0, 8, 4, 0.0F, true));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.0F, -12.0F, 2.0F);
		skirtLeft.addChild(bone10);
		setRotationAngle(bone10, 0.1745F, 0.0F, -0.1745F);
		bone10.cubeList.add(new ModelBox(bone10, 0, 56, 0.0F, 0.0F, 0.0F, 4, 8, 0, 0.0F, true));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		setRotationAngle(bipedRightArm, -0.1745F, 0.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.1F, false));
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.3F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		setRotationAngle(bipedLeftArm, -0.1745F, 0.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.1F, true));
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.3F, true));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		setRotationAngle(bipedRightLeg, 0.0F, 0.0F, 0.0349F);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		setRotationAngle(bipedLeftLeg, 0.0F, 0.0F, -0.0349F);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, true));
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
		bipedBody.render(f5);
		bipedRightArm.render(f5);
		bipedLeftArm.render(f5);
		bipedRightLeg.render(f5);
		bipedLeftLeg.render(f5);
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