// Made with Blockbench 4.2.1
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelGourd extends ModelBase {
	private final ModelRenderer bipedBody;
	private final ModelRenderer bone5;
	private final ModelRenderer bone2;
	private final ModelRenderer bone4;
	private final ModelRenderer bone3;
	private final ModelRenderer bone9;
	private final ModelRenderer bone6;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer bone8;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer bone7;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer bone11;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer bone10;
	private final ModelRenderer cube_r13;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedLeftArm;

	public ModelGourd() {
		textureWidth = 64;
		textureHeight = 64;

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 5.25F, 4.5F);
		bipedBody.addChild(bone5);
		setRotationAngle(bone5, 0.0F, 0.0F, -0.5236F);
		bone5.cubeList.add(new ModelBox(bone5, 60, 0, -0.5F, -12.25F, 3.5F, 1, 1, 1, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 35, 0, -3.0F, -11.25F, 1.0F, 6, 2, 6, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 0, 0, 3.0F, -9.25F, 1.0F, 2, 6, 6, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 16, 0, -3.0F, -9.25F, 7.0F, 6, 6, 2, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 0, 0, -5.0F, -9.25F, 1.0F, 2, 6, 6, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 16, 0, -3.0F, -9.25F, -1.0F, 6, 6, 2, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 0, 16, 4.0F, -3.25F, 0.0F, 2, 8, 8, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 20, 16, -4.0F, -3.25F, 8.0F, 8, 8, 2, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 0, 16, -6.0F, -3.25F, 0.0F, 2, 8, 8, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 20, 16, -4.0F, -3.25F, -2.0F, 8, 8, 2, 0.5F, false));
		bone5.cubeList.add(new ModelBox(bone5, 0, 32, -4.0F, 4.75F, 0.0F, 8, 2, 8, 0.5F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 4.0F, 5.0F);
		bipedBody.addChild(bone2);
		bone2.cubeList.add(new ModelBox(bone2, 42, 16, -5.0F, -4.0F, -3.0F, 2, 1, 0, 0.3F, false));
		bone2.cubeList.add(new ModelBox(bone2, 42, 16, -5.0F, -4.0F, -7.0F, 2, 2, 0, 0.3F, false));
		bone2.cubeList.add(new ModelBox(bone2, 42, 16, 2.0F, 4.0F, -7.0F, 2, 2, 0, 0.3F, false));
		bone2.cubeList.add(new ModelBox(bone2, 42, 16, -5.0F, -4.0F, -7.0F, 2, 0, 4, 0.3F, false));
		bone2.cubeList.add(new ModelBox(bone2, 42, 16, 4.0F, 4.0F, -7.0F, 0, 2, 5, 0.3F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-3.0F, -3.5F, -3.0F);
		bone2.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 0.0F, -0.5236F);
		bone4.cubeList.add(new ModelBox(bone4, 42, 16, -2.0F, -0.5F, 0.0F, 2, 5, 0, 0.3F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-3.0F, -4.0F, -7.0F);
		bone2.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 0.0F, -0.6981F);
		bone3.cubeList.add(new ModelBox(bone3, 41, 16, -2.25F, 0.0F, 0.0F, 2, 11, 0, 0.3F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(-2.0F, 2.0F, 8.75F);
		bipedBody.addChild(bone9);
		setRotationAngle(bone9, 0.0F, 0.0F, -0.5236F);
		bone9.cubeList.add(new ModelBox(bone9, 24, 0, -1.0F, -9.4F, -1.0F, 2, 1, 2, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, -10.0F, 0.0F);
		bone9.addChild(bone6);
		bone6.cubeList.add(new ModelBox(bone6, 0, 0, -1.0F, 2.0F, -3.0F, 2, 1, 6, 0.42F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, 2.5F, 0.0F);
		bone6.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 2.3562F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, 2.5F, 0.0F);
		bone6.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 1.5708F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, 2.5F, 0.0F);
		bone6.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.7854F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, -12.5F, 0.0F);
		bone9.addChild(bone8);
		bone8.cubeList.add(new ModelBox(bone8, 6, 1, -2.0F, 6.0F, -5.0F, 4, 5, 10, 0.12F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, 10.5F, 0.0F);
		bone8.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, -0.7854F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 6, 1, -2.0F, -4.5F, -5.0F, 4, 5, 10, 0.12F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, 10.5F, 0.0F);
		bone8.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 1.5708F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 6, 1, -2.0F, -4.5F, -5.0F, 4, 5, 10, 0.12F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.0F, 10.5F, 0.0F);
		bone8.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.7854F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 6, 1, -2.0F, -4.5F, -5.0F, 4, 5, 10, 0.12F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, -3.6F, 0.0F);
		bone9.addChild(bone7);
		bone7.cubeList.add(new ModelBox(bone7, 14, 16, -3.0F, 2.0F, -7.0F, 6, 9, 14, -0.17F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.0F, 10.5F, 0.0F);
		bone7.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.7854F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 38, -3.0F, -8.5F, -7.0F, 6, 9, 14, -0.17F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, 10.5F, 0.0F);
		bone7.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.0F, -1.5708F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 8, 16, -3.0F, -8.5F, -7.0F, 6, 9, 14, -0.17F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, 10.5F, 0.0F);
		bone7.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, -0.7854F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 38, -3.0F, -8.5F, -7.0F, 6, 9, 14, -0.17F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(0.0F, -1.7F, 0.0F);
		bone9.addChild(bone11);
		bone11.cubeList.add(new ModelBox(bone11, 0, 16, -2.0F, 9.0F, -5.0F, 4, 2, 10, 0.12F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(0.0F, 10.5F, 0.0F);
		bone11.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0F, -0.7854F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 16, -2.0F, -1.5F, -5.0F, 4, 2, 10, 0.12F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(0.0F, 10.5F, 0.0F);
		bone11.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.0F, 1.5708F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 16, -2.0F, -1.5F, -5.0F, 4, 2, 10, 0.12F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(0.0F, 10.5F, 0.0F);
		bone11.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, 0.7854F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 16, -2.0F, -1.5F, -5.0F, 4, 2, 10, 0.12F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.0F, 7.75F, 0.0F);
		bone9.addChild(bone10);
		bone10.cubeList.add(new ModelBox(bone10, 0, 0, -1.0F, 2.0F, -3.0F, 2, 1, 6, 0.42F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(0.0F, 2.5F, 0.0F);
		bone10.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.0F, 2.3562F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(0.0F, 2.5F, 0.0F);
		bone10.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, 1.5708F, 0.0F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(0.0F, 2.5F, 0.0F);
		bone10.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.0F, 0.7854F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 48, 48, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedBody.render(f5);
		bipedRightArm.render(f5);
		bipedLeftArm.render(f5);
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