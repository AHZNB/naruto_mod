// Made with Blockbench 3.8.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelSpike extends ModelBase {
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;

	public ModelSpike() {
		textureWidth = 32;
		textureHeight = 32;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 0.0F, 4.0F);
		bone.addChild(bone2);
		setRotationAngle(bone2, 0.1309F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -4.0F, -32.0F, 0.0F, 8, 32, 0, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, 0.0F, -4.0F);
		bone.addChild(bone3);
		setRotationAngle(bone3, -0.1309F, 0.0F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 0, -4.0F, -32.0F, 0.0F, 8, 32, 0, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(4.0F, 0.0F, 0.0F);
		bone.addChild(bone4);
		setRotationAngle(bone4, -0.1309F, -1.5708F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 0, -4.0F, -32.0F, 0.0F, 8, 32, 0, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(-4.0F, 0.0F, 0.0F);
		bone.addChild(bone5);
		setRotationAngle(bone5, 0.1309F, -1.5708F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 0, 0, -4.0F, -32.0F, 0.0F, 8, 32, 0, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(bone6);
		setRotationAngle(bone6, -1.5708F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 16, 24, -4.0F, -4.0F, 0.0F, 8, 8, 0, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
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