// Made with Blockbench 3.9.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelAnbuMask extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer bipedHeadwear;

	public ModelAnbuMask() {
		textureWidth = 32;
		textureHeight = 32;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -9.0F, -4.0F, 8, 9, 8, 0.25F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 0, 23, -4.0F, -9.0F, -4.25F, 8, 9, 0, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
		bipedHeadwear.render(f5);
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