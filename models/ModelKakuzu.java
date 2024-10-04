// Made with Blockbench 4.11.0
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelKakuzu extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer bipedBody;
	private final ModelRenderer bodywear;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer rightLegwear;
	private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer leftLegwear;

	public ModelKakuzu() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 24, -8, -4.0F, 0.0F, -4.0F, 0, 4, 8, 0.0F, false));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 24, -8, 4.0F, 0.0F, -4.0F, 0, 4, 8, 0.0F, true));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 24, 4, -4.0F, 0.0F, 4.0F, 8, 4, 0, 0.0F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

		bodywear = new ModelRenderer(this);
		bodywear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(bodywear);
		bodywear.cubeList.add(new ModelBox(bodywear, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));

		rightLegwear = new ModelRenderer(this);
		rightLegwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightLeg.addChild(rightLegwear);
		rightLegwear.cubeList.add(new ModelBox(rightLegwear, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));

		leftLegwear = new ModelRenderer(this);
		leftLegwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftLeg.addChild(leftLegwear);
		leftLegwear.cubeList.add(new ModelBox(leftLegwear, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, true));
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
		this.rightLegwear.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
		this.bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * f1;
		this.bipedRightLeg.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
		this.bipedHeadwear.rotateAngleY = f3 / (180F / (float) Math.PI);
		this.bipedHeadwear.rotateAngleX = f4 / (180F / (float) Math.PI);
		this.bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
		this.bipedHead.rotateAngleY = f3 / (180F / (float) Math.PI);
		this.bipedHead.rotateAngleX = f4 / (180F / (float) Math.PI);
	}
}