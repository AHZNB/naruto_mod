//Made with Blockbench
//Paste this code into your mod.

public static class ModelHelmetSnug extends ModelBase {
	private final ModelRenderer bb_main;

	public ModelHelmetSnug() {
		textureWidth = 32;
		textureHeight = 16;

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -4.0F, -8.0F, -4.0F,
				8, 8, 8, 0.1F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		bb_main.render(f5);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y,
			float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}