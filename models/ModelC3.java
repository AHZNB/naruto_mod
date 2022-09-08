// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelC3 extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer leftWing;
	private final ModelRenderer leftWingTip;
	private final ModelRenderer bone6;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;
	private final ModelRenderer rightWing;
	private final ModelRenderer rightWingTip;
	private final ModelRenderer bone11;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer head;
	private final ModelRenderer hump;
	private final ModelRenderer bone2;

	public ModelC3() {
		textureWidth = 64;
		textureHeight = 64;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 22, -5.0F, 4.0F, -5.0F, 10, 10, 10, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 0, 0, -6.0F, 14.0F, -6.0F, 12, 10, 12, 0.0F, false));

		leftWing = new ModelRenderer(this);
		leftWing.setRotationPoint(5.0F, 8.0F, 0.0F);
		body.addChild(leftWing);
		setRotationAngle(leftWing, -0.6981F, 0.0F, 0.0F);
		leftWing.cubeList.add(new ModelBox(leftWing, 0, 42, 0.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		leftWingTip = new ModelRenderer(this);
		leftWingTip.setRotationPoint(2.0F, 4.0F, 0.0F);
		leftWing.addChild(leftWingTip);
		setRotationAngle(leftWingTip, 0.0F, 0.0F, 0.7854F);
		leftWingTip.cubeList.add(new ModelBox(leftWingTip, 48, 12, -2.5F, -2.0F, -1.5F, 5, 6, 3, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(2.5F, 7.0F, 0.0F);
		leftWingTip.addChild(bone6);
		setRotationAngle(bone6, 0.0F, 0.0F, -0.2618F);
		bone6.cubeList.add(new ModelBox(bone6, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.0F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, 7.0F, 0.0F);
		leftWingTip.addChild(bone7);
		bone7.cubeList.add(new ModelBox(bone7, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(-2.5F, 7.0F, 0.0F);
		leftWingTip.addChild(bone8);
		setRotationAngle(bone8, 0.0F, 0.0F, 0.2618F);
		bone8.cubeList.add(new ModelBox(bone8, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.0F, false));

		rightWing = new ModelRenderer(this);
		rightWing.setRotationPoint(-5.0F, 8.0F, 0.0F);
		body.addChild(rightWing);
		setRotationAngle(rightWing, -0.8727F, 0.0F, 0.0F);
		rightWing.cubeList.add(new ModelBox(rightWing, 0, 42, -4.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, true));

		rightWingTip = new ModelRenderer(this);
		rightWingTip.setRotationPoint(-2.0F, 4.0F, 0.0F);
		rightWing.addChild(rightWingTip);
		setRotationAngle(rightWingTip, 0.0F, 0.0F, -0.7854F);
		rightWingTip.cubeList.add(new ModelBox(rightWingTip, 48, 12, -2.5F, -2.0F, -1.5F, 5, 6, 3, 0.0F, true));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(-2.5F, 7.0F, 0.0F);
		rightWingTip.addChild(bone11);
		setRotationAngle(bone11, 0.0F, 0.0F, 0.2618F);
		bone11.cubeList.add(new ModelBox(bone11, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.0F, true));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(0.0F, 7.0F, 0.0F);
		rightWingTip.addChild(bone12);
		bone12.cubeList.add(new ModelBox(bone12, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.0F, true));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(2.5F, 7.0F, 0.0F);
		rightWingTip.addChild(bone13);
		setRotationAngle(bone13, 0.0F, 0.0F, -0.2618F);
		bone13.cubeList.add(new ModelBox(bone13, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.0F, true));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 4.5F, -3.5F);
		body.addChild(head);
		head.cubeList.add(new ModelBox(head, 44, 31, -2.5F, -3.5F, -2.5F, 5, 6, 5, 0.0F, false));

		hump = new ModelRenderer(this);
		hump.setRotationPoint(0.0F, 4.0F, 1.0F);
		body.addChild(hump);
		hump.cubeList.add(new ModelBox(hump, 30, 22, -4.5F, -2.0F, -3.0F, 9, 2, 7, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -2.0F, -0.45F);
		hump.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.0F, 0.7854F);
		bone2.cubeList.add(new ModelBox(bone2, 36, 0, -3.0F, -3.0F, -2.05F, 6, 6, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		body.render(f5);
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