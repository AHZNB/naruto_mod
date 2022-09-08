// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

public static class ModelRasenshuriken extends ModelBase {
	private final ModelRenderer bone;
	private final ModelRenderer bone3;
	private final ModelRenderer bone6;
	private final ModelRenderer bone5;
	private final ModelRenderer bone4;
	private final ModelRenderer bone2;

	public ModelRasenshuriken() {
		textureWidth = 32;
		textureHeight = 32;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -0.5F, -4.5F, -0.5F, 1, 1, 1, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 0.0F, 1.5708F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(bone6);
		setRotationAngle(bone6, -1.5708F, 0.0F, 1.5708F);
		bone6.cubeList.add(new ModelBox(bone6, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(bone5);
		setRotationAngle(bone5, 3.1416F, 0.0F, 1.5708F);
		bone5.cubeList.add(new ModelBox(bone5, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(bone4);
		setRotationAngle(bone4, 1.5708F, 0.0F, 1.5708F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -1.0F, -5.0F, -1.0F, 2, 2, 2, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -1.5F, -5.5F, -1.5F, 3, 3, 3, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -2.0F, -6.0F, -2.0F, 4, 4, 4, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bone.rotateAngleY = (float) entity.ticksExisted * 0.2f;
		bone.render(f5);
		bone2.rotateAngleY = (float) -entity.ticksExisted * 0.2f;
		bone2.rotateAngleX = (float) entity.ticksExisted * 0.2f;
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