// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

public static class ModelLongCube extends ModelBase {
	private final ModelRenderer bone;
	private final ModelRenderer bone2;

	public ModelLongCube() {
		textureWidth = 32;
		textureHeight = 32;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -0.5F, -16.0F, -0.5F, 1, 16, 1, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -1.0F, -16.0F, -1.0F, 2, 16, 2, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -1.5F, -16.0F, -1.5F, 3, 16, 3, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -2.0F, -16.0F, -2.0F, 4, 16, 4, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -4.0F, -16.0F, -4.0F, 8, 16, 8, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bone.render(f5);
		bone2.render(f5);
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