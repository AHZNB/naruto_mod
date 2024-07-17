// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelDisk extends ModelBase {
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer bone5;

	public ModelDisk() {
		textureWidth = 16;
		textureHeight = 16;

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(bone2, 0.0F, 0.0F, 0.0436F);
		bone2.cubeList.add(new ModelBox(bone2, -8, 0, -4.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(bone3, 0.0F, 0.0F, -0.0436F);
		bone3.cubeList.add(new ModelBox(bone3, -8, 0, -4.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(bone4, -0.0436F, 0.0F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, -8, 0, -4.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 24.0F, 0.0F);
		setRotationAngle(bone5, 0.0436F, 0.0F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, -8, 0, -4.0F, 0.0F, -4.0F, 8, 0, 8, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bone2.render(f5);
		bone3.render(f5);
		bone4.render(f5);
		bone5.render(f5);
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