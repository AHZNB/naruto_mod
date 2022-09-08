//Made with Blockbench
//Paste this code into your mod.

public static class ModelBeam extends ModelBase {
	private final ModelRenderer bone;
	private final ModelRenderer bone2;

	public ModelBeam() {
		textureWidth = 16;
		textureHeight = 16;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(-2.0F, 16.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -1.0F, -8.0F, -1.0F, 2, 16,
				2, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(2.0F, 16.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -1.0F, -8.0F, -1.0F, 2,
				16, 2, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		bone.render(f5);
		bone2.render(f5);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y,
			float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}