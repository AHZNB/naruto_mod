// Made with Blockbench 4.7.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelGauntletMetal extends ModelBase {
	private final ModelRenderer claw;
	private final ModelRenderer finger1;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer finger2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer finger3;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer finger4;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer finger5;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer bone;
	private final ModelRenderer hexadecagon_r1;
	private final ModelRenderer hexadecagon_r2;
	private final ModelRenderer hexadecagon_r3;
	private final ModelRenderer hexadecagon_r4;
	private final ModelRenderer bolts;
	private final ModelRenderer hexadecagon_r5;
	private final ModelRenderer hexadecagon_r6;
	private final ModelRenderer chain;
	private final ModelRenderer chainroot;
	private final ModelRenderer chainx0_0;
	private final ModelRenderer chainx0_1;
	private final ModelRenderer chainx1_0;
	private final ModelRenderer chainx1_1;
	private final ModelRenderer chainx2_0;
	private final ModelRenderer chainx2_1;
	private final ModelRenderer chainx3_0;
	private final ModelRenderer chainx3_1;
	private final ModelRenderer chainx4_0;
	private final ModelRenderer chainx4_1;
	private final ModelRenderer chainx5_0;
	private final ModelRenderer chainx5_1;

	public ModelGauntletMetal() {
		textureWidth = 64;
		textureHeight = 64;

		claw = new ModelRenderer(this);
		claw.setRotationPoint(0.0F, 0.0F, 0.0F);
		setRotationAngle(claw, 0.0F, -1.5708F, 0.0F);
		claw.cubeList.add(new ModelBox(claw, 9, 0, -2.0F, 3.0F, -2.0F, 4, 1, 4, 0.0F, false));

		finger1 = new ModelRenderer(this);
		finger1.setRotationPoint(-2.2F, 3.0F, 1.9F);
		claw.addChild(finger1);
		setRotationAngle(finger1, 0.3927F, 0.0F, 0.7854F);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.7F, 1.5F, -2.15F);
		finger1.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.7854F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.9929F, 2.0F, -0.0287F);
		finger1.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.7854F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		finger2 = new ModelRenderer(this);
		finger2.setRotationPoint(-2.2F, 3.0F, 0.65F);
		claw.addChild(finger2);
		setRotationAngle(finger2, 0.1309F, 0.0F, 0.7854F);

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.7F, 1.5F, -2.15F);
		finger2.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.7854F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.9929F, 2.0F, -0.0287F);
		finger2.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.7854F, 0.0F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		finger3 = new ModelRenderer(this);
		finger3.setRotationPoint(-2.2F, 3.0F, -0.6F);
		claw.addChild(finger3);
		setRotationAngle(finger3, -0.1309F, 0.0F, 0.7854F);

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.7F, 1.5F, -2.15F);
		finger3.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.7854F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.9929F, 2.0F, -0.0287F);
		finger3.addChild(cube_r6);
		setRotationAngle(cube_r6, -0.7854F, 0.0F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		finger4 = new ModelRenderer(this);
		finger4.setRotationPoint(-2.2F, 3.0F, -1.85F);
		claw.addChild(finger4);
		setRotationAngle(finger4, -0.3927F, 0.0F, 0.7854F);

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.7F, 1.5F, -2.15F);
		finger4.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.7854F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.9929F, 2.0F, -0.0287F);
		finger4.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.7854F, 0.0F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		finger5 = new ModelRenderer(this);
		finger5.setRotationPoint(1.8F, 3.0F, -1.85F);
		claw.addChild(finger5);
		setRotationAngle(finger5, -1.933F, -0.7519F, 1.8241F);

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.7F, 1.5F, -2.15F);
		finger5.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, 0.7854F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(0.9929F, 2.0F, -0.0287F);
		finger5.addChild(cube_r10);
		setRotationAngle(cube_r10, -0.7854F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 1.5F, 0.0F);
		claw.addChild(bone);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 10, -3.5F, -1.5F, -0.6962F, 7, 3, 1, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 10, -3.5F, -1.5F, -0.2962F, 7, 3, 1, 0.0F, false));

		hexadecagon_r1 = new ModelRenderer(this);
		hexadecagon_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(hexadecagon_r1);
		setRotationAngle(hexadecagon_r1, 0.0F, -0.3927F, 0.0F);
		hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 10, -3.5F, -1.5F, -0.2962F, 7, 3, 1, 0.0F, false));
		hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 10, -3.5F, -1.5F, -0.6962F, 7, 3, 1, 0.0F, false));
		hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
		hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));

		hexadecagon_r2 = new ModelRenderer(this);
		hexadecagon_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(hexadecagon_r2);
		setRotationAngle(hexadecagon_r2, 0.0F, 0.3927F, 0.0F);
		hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 10, -3.5F, -1.5F, -0.2962F, 7, 3, 1, 0.0F, false));
		hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 10, -3.5F, -1.5F, -0.6962F, 7, 3, 1, 0.0F, false));
		hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
		hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));

		hexadecagon_r3 = new ModelRenderer(this);
		hexadecagon_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(hexadecagon_r3);
		setRotationAngle(hexadecagon_r3, 0.0F, -0.7854F, 0.0F);
		hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
		hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));

		hexadecagon_r4 = new ModelRenderer(this);
		hexadecagon_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(hexadecagon_r4);
		setRotationAngle(hexadecagon_r4, 0.0F, 0.7854F, 0.0F);
		hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
		hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));

		bolts = new ModelRenderer(this);
		bolts.setRotationPoint(0.0F, -2.5F, 0.0F);
		claw.addChild(bolts);

		hexadecagon_r5 = new ModelRenderer(this);
		hexadecagon_r5.setRotationPoint(0.0F, 4.0F, 0.0F);
		bolts.addChild(hexadecagon_r5);
		setRotationAngle(hexadecagon_r5, 0.0F, -0.7854F, 0.0F);
		hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 0, 0, -0.4544F, -0.5F, 3.0F, 1, 1, 1, 0.0F, false));

		hexadecagon_r6 = new ModelRenderer(this);
		hexadecagon_r6.setRotationPoint(0.0F, 4.0F, 0.0F);
		bolts.addChild(hexadecagon_r6);
		setRotationAngle(hexadecagon_r6, 0.0F, 0.7854F, 0.0F);
		hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 0, 0, -0.4544F, -0.5F, -4.0F, 1, 1, 1, 0.0F, true));

		chain = new ModelRenderer(this);
		chain.setRotationPoint(0.0F, 0.0F, 0.0F);
		setRotationAngle(chain, -1.5708F, -1.5708F, 1.5708F);

		chainroot = new ModelRenderer(this);
		chainroot.setRotationPoint(0.0F, 0.0F, 0.0F);
		chain.addChild(chainroot);
		setRotationAngle(chainroot, 0.0F, -0.2618F, -0.7854F);
		chainroot.cubeList.add(new ModelBox(chainroot, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx0_0 = new ModelRenderer(this);
		chainx0_0.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainroot.addChild(chainx0_0);
		setRotationAngle(chainx0_0, 0.0F, 0.5236F, 0.0F);
		chainx0_0.cubeList.add(new ModelBox(chainx0_0, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx0_1 = new ModelRenderer(this);
		chainx0_1.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx0_0.addChild(chainx0_1);
		setRotationAngle(chainx0_1, 0.0F, -0.5236F, 0.0F);
		chainx0_1.cubeList.add(new ModelBox(chainx0_1, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx1_0 = new ModelRenderer(this);
		chainx1_0.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx0_1.addChild(chainx1_0);
		setRotationAngle(chainx1_0, 0.0F, 0.5236F, 0.0F);
		chainx1_0.cubeList.add(new ModelBox(chainx1_0, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx1_1 = new ModelRenderer(this);
		chainx1_1.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx1_0.addChild(chainx1_1);
		setRotationAngle(chainx1_1, 0.0F, -0.5236F, 0.0F);
		chainx1_1.cubeList.add(new ModelBox(chainx1_1, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx2_0 = new ModelRenderer(this);
		chainx2_0.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx1_1.addChild(chainx2_0);
		setRotationAngle(chainx2_0, 0.0F, 0.5236F, 0.0F);
		chainx2_0.cubeList.add(new ModelBox(chainx2_0, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx2_1 = new ModelRenderer(this);
		chainx2_1.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx2_0.addChild(chainx2_1);
		setRotationAngle(chainx2_1, 0.0F, -0.5236F, 0.0F);
		chainx2_1.cubeList.add(new ModelBox(chainx2_1, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx3_0 = new ModelRenderer(this);
		chainx3_0.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx2_1.addChild(chainx3_0);
		setRotationAngle(chainx3_0, 0.0F, 0.5236F, 0.0F);
		chainx3_0.cubeList.add(new ModelBox(chainx3_0, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx3_1 = new ModelRenderer(this);
		chainx3_1.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx3_0.addChild(chainx3_1);
		setRotationAngle(chainx3_1, 0.0F, -0.5236F, 0.0F);
		chainx3_1.cubeList.add(new ModelBox(chainx3_1, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx4_0 = new ModelRenderer(this);
		chainx4_0.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx3_1.addChild(chainx4_0);
		setRotationAngle(chainx4_0, 0.0F, 0.5236F, 0.0F);
		chainx4_0.cubeList.add(new ModelBox(chainx4_0, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx4_1 = new ModelRenderer(this);
		chainx4_1.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx4_0.addChild(chainx4_1);
		setRotationAngle(chainx4_1, 0.0F, -0.5236F, 0.0F);
		chainx4_1.cubeList.add(new ModelBox(chainx4_1, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx5_0 = new ModelRenderer(this);
		chainx5_0.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx4_1.addChild(chainx5_0);
		setRotationAngle(chainx5_0, 0.0F, 0.5236F, 0.0F);
		chainx5_0.cubeList.add(new ModelBox(chainx5_0, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));

		chainx5_1 = new ModelRenderer(this);
		chainx5_1.setRotationPoint(1.0F, -1.0F, 0.0F);
		chainx5_0.addChild(chainx5_1);
		setRotationAngle(chainx5_1, 0.0F, -0.5236F, 0.0F);
		chainx5_1.cubeList.add(new ModelBox(chainx5_1, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		claw.render(f5);
		chain.render(f5);
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