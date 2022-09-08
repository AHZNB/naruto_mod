// Made with Blockbench 4.0.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelGedoMazo extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer bipedBody;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer bone4;
	private final ModelRenderer bone5;
	private final ModelRenderer bone14;
	private final ModelRenderer bone15;
	private final ModelRenderer bone6;
	private final ModelRenderer bone7;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer bone8;
	private final ModelRenderer bone9;
	private final ModelRenderer bone10;
	private final ModelRenderer bone11;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer bipedLeftLeg;

	public ModelGedoMazo() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -3.0F, -6.0F, -4.0F, 6, 6, 6, 0.0F, false));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 24, 0, -3.0F, -6.0F, -4.0F, 6, 6, 6, 0.1F, false));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 50, 0, -2.0F, 0.0F, -4.0F, 4, 2, 2, 0.0F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
		bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-2.0F, 3.0F, -1.0F);
		bipedBody.addChild(bone2);
		setRotationAngle(bone2, 0.2618F, 0.0F, -0.7854F);
		bone2.cubeList.add(new ModelBox(bone2, 56, 16, -1.0F, -5.0F, -1.0F, 2, 6, 2, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, -4.5F, 0.0F);
		bone2.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 0.0F, 0.6981F);
		bone3.cubeList.add(new ModelBox(bone3, 56, 16, -1.0F, -5.5F, -1.0F, 2, 6, 2, 0.0F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(2.0F, 3.0F, -1.0F);
		bipedBody.addChild(bone12);
		setRotationAngle(bone12, 0.2618F, 0.0F, 0.7854F);
		bone12.cubeList.add(new ModelBox(bone12, 56, 16, -1.0F, -5.0F, -1.0F, 2, 6, 2, 0.0F, true));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(0.0F, -4.5F, 0.0F);
		bone12.addChild(bone13);
		setRotationAngle(bone13, 0.0F, 0.0F, -0.6981F);
		bone13.cubeList.add(new ModelBox(bone13, 56, 16, -1.0F, -5.5F, -1.0F, 2, 6, 2, 0.0F, true));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-3.0F, 2.0F, 0.0F);
		bipedBody.addChild(bone4);
		setRotationAngle(bone4, -0.1745F, 0.0F, -0.5236F);
		bone4.cubeList.add(new ModelBox(bone4, 56, 16, -1.0F, -5.0F, -1.0F, 2, 6, 2, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, -4.5F, 0.0F);
		bone4.addChild(bone5);
		setRotationAngle(bone5, 0.0F, 0.0F, 0.5236F);
		bone5.cubeList.add(new ModelBox(bone5, 56, 16, -1.0F, -5.5F, -1.0F, 2, 6, 2, 0.0F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(3.0F, 2.0F, 0.0F);
		bipedBody.addChild(bone14);
		setRotationAngle(bone14, -0.1745F, 0.0F, 0.5236F);
		bone14.cubeList.add(new ModelBox(bone14, 56, 16, -1.0F, -5.0F, -1.0F, 2, 6, 2, 0.0F, true));

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(0.0F, -4.5F, 0.0F);
		bone14.addChild(bone15);
		setRotationAngle(bone15, 0.0F, 0.0F, -0.5236F);
		bone15.cubeList.add(new ModelBox(bone15, 56, 16, -1.0F, -5.5F, -1.0F, 2, 6, 2, 0.0F, true));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(-3.0F, 5.0F, 1.0F);
		bipedBody.addChild(bone6);
		setRotationAngle(bone6, -0.5236F, 0.0F, -0.7854F);
		bone6.cubeList.add(new ModelBox(bone6, 56, 17, -1.0F, -5.0F, -1.0F, 2, 6, 2, 0.0F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, -4.5F, 0.0F);
		bone6.addChild(bone7);
		setRotationAngle(bone7, 0.0F, 0.0F, 0.5236F);
		bone7.cubeList.add(new ModelBox(bone7, 56, 16, -1.0F, -5.5F, -1.0F, 2, 6, 2, 0.0F, false));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(3.0F, 5.0F, 1.0F);
		bipedBody.addChild(bone16);
		setRotationAngle(bone16, -0.5236F, 0.0F, 0.7854F);
		bone16.cubeList.add(new ModelBox(bone16, 56, 16, -1.0F, -5.0F, -1.0F, 2, 6, 2, 0.0F, true));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(0.0F, -4.5F, 0.0F);
		bone16.addChild(bone17);
		setRotationAngle(bone17, 0.0F, 0.0F, -0.5236F);
		bone17.cubeList.add(new ModelBox(bone17, 56, 16, -1.0F, -5.5F, -1.0F, 2, 6, 2, 0.0F, true));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, 0.0F, 1.0F);
		bipedBody.addChild(bone8);
		setRotationAngle(bone8, -0.6981F, 0.0F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 56, 16, -1.0F, -5.0F, -1.0F, 2, 6, 2, 0.0F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(0.0F, -4.5F, 0.0F);
		bone8.addChild(bone9);
		setRotationAngle(bone9, 0.6981F, 0.0F, 0.0F);
		bone9.cubeList.add(new ModelBox(bone9, 56, 16, -1.0F, -5.5F, -1.0F, 2, 6, 2, 0.0F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.0F, 8.0F, 1.0F);
		bipedBody.addChild(bone10);
		setRotationAngle(bone10, -1.0472F, 0.0F, 0.0F);
		bone10.cubeList.add(new ModelBox(bone10, 56, 16, -1.0F, -5.0F, -1.0F, 2, 6, 2, 0.0F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(0.0F, -4.5F, 0.0F);
		bone10.addChild(bone11);
		setRotationAngle(bone11, 0.5236F, 0.0F, 0.0F);
		bone11.cubeList.add(new ModelBox(bone11, 56, 16, -1.0F, -5.5F, -1.0F, 2, 6, 2, 0.0F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, true));
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, true));
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