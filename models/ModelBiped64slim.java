// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

public static class ModelBiped64slim extends ModelBiped {
	public ModelBiped64slim() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.leftArmPose = ModelBiped.ArmPose.EMPTY;
		this.rightArmPose = ModelBiped.ArmPose.EMPTY;
		this.bipedHead = new ModelRenderer(this);
		this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
		this.bipedHeadwear = new ModelRenderer(this);
		this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedHeadwear.cubeList
				.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));
		this.bipedBody = new ModelRenderer(this);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
		this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
		this.bipedRightArm = new ModelRenderer(this);
		this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.bipedRightArm.cubeList
				.add(new ModelBox(this.bipedRightArm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
		this.bipedRightArm.cubeList
				.add(new ModelBox(this.bipedRightArm, 40, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
		this.bipedLeftArm = new ModelRenderer(this);
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.bipedLeftArm.cubeList
				.add(new ModelBox(this.bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
		this.bipedLeftArm.cubeList
				.add(new ModelBox(this.bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
		this.bipedRightLeg = new ModelRenderer(this);
		this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.bipedRightLeg.cubeList
				.add(new ModelBox(this.bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
		this.bipedRightLeg.cubeList
				.add(new ModelBox(this.bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
		this.bipedLeftLeg = new ModelRenderer(this);
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.bipedLeftLeg.cubeList
				.add(new ModelBox(this.bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
		this.bipedLeftLeg.cubeList
				.add(new ModelBox(this.bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}
}