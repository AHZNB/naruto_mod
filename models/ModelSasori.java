// Made with Blockbench 4.10.2
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelSasori extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer robeHead;
	private final ModelRenderer bipedBody;
	private final ModelRenderer robeBody;
	private final ModelRenderer scrolls;
	private final ModelRenderer backBladesRight;
	private final ModelRenderer bone;
	private final ModelRenderer blade1;
	private final ModelRenderer blade2;
	private final ModelRenderer blade3;
	private final ModelRenderer blade4;
	private final ModelRenderer blade5;
	private final ModelRenderer backBladesLeft;
	private final ModelRenderer bone12;
	private final ModelRenderer blade6;
	private final ModelRenderer blade7;
	private final ModelRenderer blade8;
	private final ModelRenderer blade9;
	private final ModelRenderer blade10;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer robeRightArm;
	private final ModelRenderer gunRight;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer robeLeftArm;
	private final ModelRenderer gunLeft;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer robeRightLeg;
	private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer robeLeftLeg;

	public ModelSasori() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		setRotationAngle(bipedHead, -0.1047F, 0.0873F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.3F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		setRotationAngle(bipedHeadwear, -0.1047F, 0.0873F, 0.0F);

		robeHead = new ModelRenderer(this);
		robeHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHeadwear.addChild(robeHead);
		robeHead.cubeList.add(new ModelBox(robeHead, 32, 54, -4.0F, -2.0F, -4.0F, 8, 2, 8, 0.65F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

		robeBody = new ModelRenderer(this);
		robeBody.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedBody.addChild(robeBody);
		robeBody.cubeList.add(new ModelBox(robeBody, 16, 32, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.5F, false));

		scrolls = new ModelRenderer(this);
		scrolls.setRotationPoint(0.0F, 22.0F, 0.0F);
		bipedBody.addChild(scrolls);
		scrolls.cubeList.add(new ModelBox(scrolls, 24, 0, -3.0F, -21.0F, 2.0F, 6, 2, 2, 0.0F, false));
		scrolls.cubeList.add(new ModelBox(scrolls, 24, 0, -3.0F, -18.0F, 2.0F, 6, 2, 2, 0.0F, false));
		scrolls.cubeList.add(new ModelBox(scrolls, 24, 0, -3.0F, -15.0F, 2.0F, 6, 2, 2, 0.0F, false));
		scrolls.cubeList.add(new ModelBox(scrolls, 0, 20, -2.0F, -21.175F, 1.275F, 4, 9, 3, 0.0F, false));

		backBladesRight = new ModelRenderer(this);
		backBladesRight.setRotationPoint(-3.2F, 6.0F, 3.0F);
		bipedBody.addChild(backBladesRight);
		setRotationAngle(backBladesRight, 0.0F, 0.5236F, -0.2618F);
		backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -1.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
		backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -3.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
		backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -5.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
		backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -7.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));
		backBladesRight.cubeList.add(new ModelBox(backBladesRight, 0, 0, -9.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(-8.8F, 0.5F, -0.5F);
		backBladesRight.addChild(bone);
		setRotationAngle(bone, 0.0F, -1.0472F, 0.0F);

		blade1 = new ModelRenderer(this);
		blade1.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone.addChild(blade1);
		setRotationAngle(blade1, 0.0F, 0.0F, -0.5236F);
		blade1.cubeList.add(new ModelBox(blade1, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade1.cubeList.add(new ModelBox(blade1, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade1.cubeList.add(new ModelBox(blade1, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade1.cubeList.add(new ModelBox(blade1, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));

		blade2 = new ModelRenderer(this);
		blade2.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone.addChild(blade2);
		setRotationAngle(blade2, 0.0F, 0.0F, 0.0873F);
		blade2.cubeList.add(new ModelBox(blade2, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade2.cubeList.add(new ModelBox(blade2, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade2.cubeList.add(new ModelBox(blade2, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade2.cubeList.add(new ModelBox(blade2, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));

		blade3 = new ModelRenderer(this);
		blade3.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone.addChild(blade3);
		setRotationAngle(blade3, 0.0F, 0.0F, 0.6981F);
		blade3.cubeList.add(new ModelBox(blade3, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade3.cubeList.add(new ModelBox(blade3, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade3.cubeList.add(new ModelBox(blade3, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade3.cubeList.add(new ModelBox(blade3, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));

		blade4 = new ModelRenderer(this);
		blade4.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone.addChild(blade4);
		setRotationAngle(blade4, 0.0F, 0.0F, 1.309F);
		blade4.cubeList.add(new ModelBox(blade4, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade4.cubeList.add(new ModelBox(blade4, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade4.cubeList.add(new ModelBox(blade4, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade4.cubeList.add(new ModelBox(blade4, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));

		blade5 = new ModelRenderer(this);
		blade5.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone.addChild(blade5);
		setRotationAngle(blade5, 0.0F, 0.0F, 1.9199F);
		blade5.cubeList.add(new ModelBox(blade5, 0, 2, -4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade5.cubeList.add(new ModelBox(blade5, 0, 2, -8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade5.cubeList.add(new ModelBox(blade5, 0, 2, -12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));
		blade5.cubeList.add(new ModelBox(blade5, 0, 3, -16.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, false));

		backBladesLeft = new ModelRenderer(this);
		backBladesLeft.setRotationPoint(3.2F, 6.0F, 3.0F);
		bipedBody.addChild(backBladesLeft);
		setRotationAngle(backBladesLeft, 0.0F, -0.5236F, 0.2618F);
		backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, -0.2F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
		backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 1.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
		backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 3.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
		backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 5.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));
		backBladesLeft.cubeList.add(new ModelBox(backBladesLeft, 0, 0, 7.8F, 0.0F, -1.0F, 2, 1, 1, 0.1F, true));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(8.8F, 0.5F, -0.5F);
		backBladesLeft.addChild(bone12);
		setRotationAngle(bone12, 0.0F, 0.7854F, 0.0F);

		blade6 = new ModelRenderer(this);
		blade6.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone12.addChild(blade6);
		setRotationAngle(blade6, 0.0F, 0.0F, 0.5236F);
		blade6.cubeList.add(new ModelBox(blade6, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade6.cubeList.add(new ModelBox(blade6, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade6.cubeList.add(new ModelBox(blade6, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade6.cubeList.add(new ModelBox(blade6, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));

		blade7 = new ModelRenderer(this);
		blade7.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone12.addChild(blade7);
		setRotationAngle(blade7, 0.0F, 0.0F, -0.0873F);
		blade7.cubeList.add(new ModelBox(blade7, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade7.cubeList.add(new ModelBox(blade7, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade7.cubeList.add(new ModelBox(blade7, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade7.cubeList.add(new ModelBox(blade7, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));

		blade8 = new ModelRenderer(this);
		blade8.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone12.addChild(blade8);
		setRotationAngle(blade8, 0.0F, 0.0F, -0.6981F);
		blade8.cubeList.add(new ModelBox(blade8, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade8.cubeList.add(new ModelBox(blade8, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade8.cubeList.add(new ModelBox(blade8, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade8.cubeList.add(new ModelBox(blade8, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));

		blade9 = new ModelRenderer(this);
		blade9.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone12.addChild(blade9);
		setRotationAngle(blade9, 0.0F, 0.0F, -1.309F);
		blade9.cubeList.add(new ModelBox(blade9, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade9.cubeList.add(new ModelBox(blade9, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade9.cubeList.add(new ModelBox(blade9, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade9.cubeList.add(new ModelBox(blade9, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));

		blade10 = new ModelRenderer(this);
		blade10.setRotationPoint(0.0F, 0.0F, 0.5F);
		bone12.addChild(blade10);
		setRotationAngle(blade10, 0.0F, 0.0F, -1.9199F);
		blade10.cubeList.add(new ModelBox(blade10, 0, 2, 0.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade10.cubeList.add(new ModelBox(blade10, 0, 2, 4.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade10.cubeList.add(new ModelBox(blade10, 0, 2, 8.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));
		blade10.cubeList.add(new ModelBox(blade10, 0, 3, 12.0F, -0.5F, -0.5F, 4, 1, 0, 0.0F, true));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
		setRotationAngle(bipedRightArm, -0.1745F, 0.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));

		robeRightArm = new ModelRenderer(this);
		robeRightArm.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(robeRightArm);
		robeRightArm.cubeList.add(new ModelBox(robeRightArm, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.5F, false));

		gunRight = new ModelRenderer(this);
		gunRight.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(gunRight);
		gunRight.cubeList.add(new ModelBox(gunRight, 36, 16, -1.5F, 8.25F, -1.0F, 2, 2, 2, 0.0F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
		setRotationAngle(bipedLeftArm, 0.1745F, 0.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, true));

		robeLeftArm = new ModelRenderer(this);
		robeLeftArm.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(robeLeftArm);
		robeLeftArm.cubeList.add(new ModelBox(robeLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.5F, true));

		gunLeft = new ModelRenderer(this);
		gunLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(gunLeft);
		gunLeft.cubeList.add(new ModelBox(gunLeft, 36, 16, -0.5F, 8.25F, -1.0F, 2, 2, 2, 0.0F, true));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		setRotationAngle(bipedRightLeg, 0.2618F, 0.0F, 0.0349F);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));

		robeRightLeg = new ModelRenderer(this);
		robeRightLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightLeg.addChild(robeRightLeg);
		robeRightLeg.cubeList.add(new ModelBox(robeRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		setRotationAngle(bipedLeftLeg, -0.2618F, 0.0F, -0.0349F);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		robeLeftLeg = new ModelRenderer(this);
		robeLeftLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftLeg.addChild(robeLeftLeg);
		robeLeftLeg.cubeList.add(new ModelBox(robeLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
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