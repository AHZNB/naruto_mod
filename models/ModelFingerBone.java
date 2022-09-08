// Made with Blockbench 3.9.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelFingerBone extends ModelBase {
	private final ModelRenderer bone2;
	private final ModelRenderer bone;

	public ModelFingerBone() {
		textureWidth = 32;
		textureHeight = 32;

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 19.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 12, 0, -1.5F, -1.0F, -1.5F, 3, 2, 3, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 9, 5, -1.5F, -1.5F, -1.5F, 3, 3, 3, -0.1F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 8, -1.5F, -2.0F, -1.5F, 3, 4, 3, -0.3F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -1.5F, -2.5F, -1.5F, 3, 5, 3, -0.5F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 22.5F, 0.0F);
		setRotationAngle(bone, 0.0F, 0.5236F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 8, 17, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.1F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 15, -1.0F, -1.5F, -1.0F, 2, 3, 2, -0.1F, false));
		bone.cubeList.add(new ModelBox(bone, 12, 11, -1.0F, -2.0F, -1.0F, 2, 4, 2, -0.3F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bone2.render(f5);
		bone.render(f5);
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