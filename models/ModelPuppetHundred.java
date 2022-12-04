// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelPuppetHundred extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer jaw;
	private final ModelRenderer niceHair;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer crazyHair;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer collar;
	private final ModelRenderer collar2;
	private final ModelRenderer collar3;
	private final ModelRenderer collar4;
	private final ModelRenderer collar5;
	private final ModelRenderer collar6;
	private final ModelRenderer collar7;
	private final ModelRenderer bipedBody;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer bipedLeftLeg;

	public ModelPuppetHundred() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.0F, -1.0F, 0.0F);
		bipedHead.addChild(jaw);
		jaw.cubeList.add(new ModelBox(jaw, 50, 24, -1.5F, -1.0F, -4.0F, 3, 2, 4, 0.0F, false));

		niceHair = new ModelRenderer(this);
		niceHair.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.addChild(niceHair);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(3.4118F, -8.1983F, -3.0337F);
		niceHair.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.0873F, 0.5236F, -0.0873F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 17, 17, -0.9464F, 0.1951F, -0.6367F, 2, 10, 2, 0.15F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-3.4118F, -8.1983F, -3.0337F);
		niceHair.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.0873F, -0.5236F, 0.0873F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 17, 17, -1.0536F, 0.1951F, -0.6367F, 2, 10, 2, 0.15F, true));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-0.045F, -7.2614F, 0.8988F);
		niceHair.addChild(cube_r3);
		setRotationAngle(cube_r3, -1.0908F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 32, 11, -3.95F, -2.425F, -2.625F, 8, 6, 7, 0.22F, false));

		crazyHair = new ModelRenderer(this);
		crazyHair.setRotationPoint(1.0F, 24.0F, 0.0F);
		bipedHead.addChild(crazyHair);

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-1.955F, -31.2614F, 0.8988F);
		crazyHair.addChild(cube_r4);
		setRotationAngle(cube_r4, -2.0617F, -1.5125F, 1.1018F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 32, 11, -4.65F, -4.325F, -1.4F, 8, 6, 7, 0.0F, true));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-1.955F, -31.2614F, 0.8988F);
		crazyHair.addChild(cube_r5);
		setRotationAngle(cube_r5, -2.1489F, -1.5125F, 1.1018F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 32, 11, -4.45F, -5.55F, -0.875F, 8, 6, 7, -0.4F, true));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-1.955F, -31.2614F, 0.8988F);
		crazyHair.addChild(cube_r6);
		setRotationAngle(cube_r6, -1.9744F, -1.5125F, 1.1018F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 32, 11, -4.875F, -4.325F, -1.85F, 8, 6, 7, 0.22F, true));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-0.045F, -31.2614F, 0.8988F);
		crazyHair.addChild(cube_r7);
		setRotationAngle(cube_r7, -1.9744F, 1.5125F, -1.1018F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 32, 11, -3.125F, -4.325F, -1.85F, 8, 6, 7, 0.22F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(-0.045F, -31.2614F, 0.8988F);
		crazyHair.addChild(cube_r8);
		setRotationAngle(cube_r8, -2.1489F, 1.5125F, -1.1018F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 32, 11, -3.55F, -5.55F, -0.875F, 8, 6, 7, -0.4F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(-0.045F, -31.2614F, 0.8988F);
		crazyHair.addChild(cube_r9);
		setRotationAngle(cube_r9, -2.0617F, 1.5125F, -1.1018F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 32, 11, -3.35F, -4.325F, -1.4F, 8, 6, 7, 0.0F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);

		collar = new ModelRenderer(this);
		collar.setRotationPoint(0.0F, -0.116F, -2.884F);
		bipedHeadwear.addChild(collar);
		setRotationAngle(collar, -1.0472F, 0.0F, 0.0F);
		collar.cubeList.add(new ModelBox(collar, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar2 = new ModelRenderer(this);
		collar2.setRotationPoint(0.0F, 0.134F, -2.884F);
		bipedHeadwear.addChild(collar2);
		setRotationAngle(collar2, -1.0908F, 0.0F, 0.0F);
		collar2.cubeList.add(new ModelBox(collar2, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar3 = new ModelRenderer(this);
		collar3.setRotationPoint(0.0F, 0.384F, -2.884F);
		bipedHeadwear.addChild(collar3);
		setRotationAngle(collar3, -1.1345F, 0.0F, 0.0F);
		collar3.cubeList.add(new ModelBox(collar3, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar4 = new ModelRenderer(this);
		collar4.setRotationPoint(0.0F, 0.634F, -2.884F);
		bipedHeadwear.addChild(collar4);
		setRotationAngle(collar4, -1.1781F, 0.0F, 0.0F);
		collar4.cubeList.add(new ModelBox(collar4, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar5 = new ModelRenderer(this);
		collar5.setRotationPoint(0.0F, 0.884F, -2.634F);
		bipedHeadwear.addChild(collar5);
		setRotationAngle(collar5, -1.2217F, 0.0F, 0.0F);
		collar5.cubeList.add(new ModelBox(collar5, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar6 = new ModelRenderer(this);
		collar6.setRotationPoint(0.0F, 1.134F, -2.634F);
		bipedHeadwear.addChild(collar6);
		setRotationAngle(collar6, -1.2654F, 0.0F, 0.0F);
		collar6.cubeList.add(new ModelBox(collar6, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar7 = new ModelRenderer(this);
		collar7.setRotationPoint(0.0F, 1.384F, -2.634F);
		bipedHeadwear.addChild(collar7);
		setRotationAngle(collar7, -1.309F, 0.0F, 0.0F);
		collar7.cubeList.add(new ModelBox(collar7, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(bone);
		setRotationAngle(bone, -0.0873F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 16, 36, -4.0F, 0.0F, -2.0F, 8, 24, 4, 0.5F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(bone2);
		setRotationAngle(bone2, 0.0873F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 40, 36, -4.0F, 0.0F, -2.0F, 8, 24, 4, 0.5F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 48, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
		bipedHeadwear.render(f5);
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