// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelPuppetHundred extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer jaw;
	private final ModelRenderer jaw2;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer niceHair;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer crazyHair;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;
	private final ModelRenderer bone9;
	private final ModelRenderer bone10;
	private final ModelRenderer bone11;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer bone14;
	private final ModelRenderer bipedBody;
	private final ModelRenderer collar;
	private final ModelRenderer collar1;
	private final ModelRenderer collar2;
	private final ModelRenderer collar3;
	private final ModelRenderer collar4;
	private final ModelRenderer collar5;
	private final ModelRenderer collar6;
	private final ModelRenderer collar7;
	private final ModelRenderer collar8;
	private final ModelRenderer collar9;
	private final ModelRenderer collar10;
	private final ModelRenderer collar11;
	private final ModelRenderer collar12;
	private final ModelRenderer collar13;
	private final ModelRenderer collar14;
	private final ModelRenderer collar15;
	private final ModelRenderer collar16;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedLeftArm;

	public ModelPuppetHundred() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.0F, -1.0F, 0.0F);
		bipedHead.addChild(jaw);
		setRotationAngle(jaw, 0.2618F, 0.0F, 0.0F);
		jaw.cubeList.add(new ModelBox(jaw, 50, 24, -1.5F, -1.0F, -4.01F, 3, 2, 4, 0.0F, false));

		jaw2 = new ModelRenderer(this);
		jaw2.setRotationPoint(0.0F, -1.0F, 0.0F);
		bipedHead.addChild(jaw2);
		setRotationAngle(jaw2, 0.1745F, 0.0F, 0.0F);
		jaw2.cubeList.add(new ModelBox(jaw2, 36, 24, -4.0F, -2.0F, -4.0F, 3, 3, 4, -0.01F, false));
		jaw2.cubeList.add(new ModelBox(jaw2, 36, 24, 1.0F, -2.0F, -4.0F, 3, 3, 4, -0.01F, true));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);

		niceHair = new ModelRenderer(this);
		niceHair.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHeadwear.addChild(niceHair);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(3.4118F, -8.1983F, -3.0337F);
		niceHair.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.0873F, 0.5236F, -0.0873F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 16, 16, -0.9464F, 0.1951F, -0.6367F, 2, 10, 2, 0.15F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-3.4118F, -8.1983F, -3.0337F);
		niceHair.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.0873F, -0.5236F, 0.0873F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 16, 16, -1.0536F, 0.1951F, -0.6367F, 2, 10, 2, 0.15F, true));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.005F, -7.9697F, -3.8572F);
		niceHair.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.8727F, 0.0873F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 32, 11, -4.0F, -6.15F, 0.15F, 8, 6, 7, 0.15F, true));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.005F, -7.9697F, -3.8572F);
		niceHair.addChild(cube_r4);
		setRotationAngle(cube_r4, -1.0908F, -0.0873F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 32, 11, -4.0F, -6.15F, 0.15F, 8, 6, 7, 0.2F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.005F, -7.9697F, -3.8572F);
		niceHair.addChild(cube_r5);
		setRotationAngle(cube_r5, -1.2654F, 0.1745F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 32, 11, -4.0F, -6.65F, 0.25F, 8, 6, 7, 0.25F, true));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.005F, -7.9697F, -3.8572F);
		niceHair.addChild(cube_r6);
		setRotationAngle(cube_r6, -1.4399F, -0.1745F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 32, 11, -4.0F, -7.15F, 0.35F, 8, 6, 7, 0.3F, false));

		crazyHair = new ModelRenderer(this);
		crazyHair.setRotationPoint(0.0F, -5.5F, -5.5F);
		bipedHeadwear.addChild(crazyHair);

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-1.0F, -0.75F, -0.25F);
		crazyHair.addChild(bone3);
		setRotationAngle(bone3, -0.4363F, -0.0436F, -0.1745F);
		bone3.cubeList.add(new ModelBox(bone3, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-1.0F, -2.0F, 1.0F);
		crazyHair.addChild(bone4);
		setRotationAngle(bone4, -0.7854F, -0.1309F, -0.3054F);
		bone4.cubeList.add(new ModelBox(bone4, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(-1.0F, -3.0F, 3.0F);
		crazyHair.addChild(bone5);
		setRotationAngle(bone5, -1.0472F, -0.2182F, -0.3491F);
		bone5.cubeList.add(new ModelBox(bone5, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(-1.25F, -3.25F, 5.0F);
		crazyHair.addChild(bone6);
		setRotationAngle(bone6, -1.309F, -0.1309F, -0.3054F);
		bone6.cubeList.add(new ModelBox(bone6, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(-1.25F, -4.25F, 9.75F);
		crazyHair.addChild(bone7);
		setRotationAngle(bone7, -2.618F, -0.0436F, -0.0873F);
		bone7.cubeList.add(new ModelBox(bone7, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(-1.25F, -2.0F, 10.5F);
		crazyHair.addChild(bone8);
		setRotationAngle(bone8, -2.9671F, 0.0F, -0.0436F);
		bone8.cubeList.add(new ModelBox(bone8, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(1.0F, -0.75F, -0.25F);
		crazyHair.addChild(bone9);
		setRotationAngle(bone9, -0.4363F, 0.0436F, 0.1745F);
		bone9.cubeList.add(new ModelBox(bone9, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(1.0F, -2.0F, 1.0F);
		crazyHair.addChild(bone10);
		setRotationAngle(bone10, -0.7854F, 0.1309F, 0.3054F);
		bone10.cubeList.add(new ModelBox(bone10, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(1.0F, -3.0F, 3.0F);
		crazyHair.addChild(bone11);
		setRotationAngle(bone11, -1.0472F, 0.2182F, 0.3491F);
		bone11.cubeList.add(new ModelBox(bone11, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(1.25F, -3.25F, 5.0F);
		crazyHair.addChild(bone12);
		setRotationAngle(bone12, -1.309F, 0.1309F, 0.3054F);
		bone12.cubeList.add(new ModelBox(bone12, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(1.25F, -4.25F, 9.75F);
		crazyHair.addChild(bone13);
		setRotationAngle(bone13, -2.618F, 0.0436F, 0.0873F);
		bone13.cubeList.add(new ModelBox(bone13, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(1.25F, -2.0F, 10.5F);
		crazyHair.addChild(bone14);
		setRotationAngle(bone14, -2.9671F, 0.0F, 0.0436F);
		bone14.cubeList.add(new ModelBox(bone14, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);

		collar = new ModelRenderer(this);
		collar.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(collar);

		collar1 = new ModelRenderer(this);
		collar1.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar1);
		setRotationAngle(collar1, -1.0472F, 0.0F, 0.0F);
		collar1.cubeList.add(new ModelBox(collar1, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar2 = new ModelRenderer(this);
		collar2.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar2);
		setRotationAngle(collar2, -1.0908F, 0.0F, 0.0873F);
		collar2.cubeList.add(new ModelBox(collar2, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar3 = new ModelRenderer(this);
		collar3.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar3);
		setRotationAngle(collar3, -1.1345F, 0.0F, -0.0873F);
		collar3.cubeList.add(new ModelBox(collar3, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar4 = new ModelRenderer(this);
		collar4.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar4);
		setRotationAngle(collar4, -1.1781F, 0.0F, 0.0873F);
		collar4.cubeList.add(new ModelBox(collar4, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar5 = new ModelRenderer(this);
		collar5.setRotationPoint(0.0F, -0.116F, -2.634F);
		collar.addChild(collar5);
		setRotationAngle(collar5, -1.2217F, 0.0F, -0.0873F);
		collar5.cubeList.add(new ModelBox(collar5, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar6 = new ModelRenderer(this);
		collar6.setRotationPoint(0.0F, -0.116F, -2.634F);
		collar.addChild(collar6);
		setRotationAngle(collar6, -1.2654F, 0.0F, 0.0873F);
		collar6.cubeList.add(new ModelBox(collar6, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar7 = new ModelRenderer(this);
		collar7.setRotationPoint(0.0F, -0.116F, -2.634F);
		collar.addChild(collar7);
		setRotationAngle(collar7, -1.309F, 0.0F, -0.0873F);
		collar7.cubeList.add(new ModelBox(collar7, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar8 = new ModelRenderer(this);
		collar8.setRotationPoint(0.0F, -0.116F, -2.634F);
		collar.addChild(collar8);
		setRotationAngle(collar8, -1.3526F, 0.0F, 0.0873F);
		collar8.cubeList.add(new ModelBox(collar8, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar9 = new ModelRenderer(this);
		collar9.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar9);
		setRotationAngle(collar9, -1.3963F, 0.0F, -0.0873F);
		collar9.cubeList.add(new ModelBox(collar9, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar10 = new ModelRenderer(this);
		collar10.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar10);
		setRotationAngle(collar10, -1.4399F, 0.0F, 0.0873F);
		collar10.cubeList.add(new ModelBox(collar10, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar11 = new ModelRenderer(this);
		collar11.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar11);
		setRotationAngle(collar11, -1.4835F, 0.0F, -0.0873F);
		collar11.cubeList.add(new ModelBox(collar11, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar12 = new ModelRenderer(this);
		collar12.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar12);
		setRotationAngle(collar12, -1.5272F, 0.0F, 0.0873F);
		collar12.cubeList.add(new ModelBox(collar12, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar13 = new ModelRenderer(this);
		collar13.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar13);
		setRotationAngle(collar13, -1.5708F, 0.0F, -0.0873F);
		collar13.cubeList.add(new ModelBox(collar13, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar14 = new ModelRenderer(this);
		collar14.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar14);
		setRotationAngle(collar14, -1.6144F, 0.0F, 0.0873F);
		collar14.cubeList.add(new ModelBox(collar14, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

		collar15 = new ModelRenderer(this);
		collar15.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar15);
		setRotationAngle(collar15, -1.6581F, 0.0F, -0.0873F);
		collar15.cubeList.add(new ModelBox(collar15, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));

		collar16 = new ModelRenderer(this);
		collar16.setRotationPoint(0.0F, -0.116F, -2.884F);
		collar.addChild(collar16);
		setRotationAngle(collar16, -1.7017F, 0.0F, 0.0F);
		collar16.cubeList.add(new ModelBox(collar16, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));

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

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedBody.addChild(bipedRightLeg);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedBody.addChild(bipedLeftLeg);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 48, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
		bipedHeadwear.render(f5);
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