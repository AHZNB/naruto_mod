// Made with Blockbench 3.9.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelWoodSegment extends ModelBase {
	private final ModelRenderer bb_main;
	private final ModelRenderer bone5;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;

	public ModelWoodSegment() {
		textureWidth = 16;
		textureHeight = 16;

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
		bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -2.0F, -3.0F, -2.0F, 4, 4, 4, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 1.0F, 0.0F);
		bb_main.addChild(bone5);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, -4.0F, -2.0F);
		bone5.addChild(bone);
		setRotationAngle(bone, -0.5236F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -2.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -4.0F, 2.0F);
		bone5.addChild(bone2);
		setRotationAngle(bone2, 0.5236F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -2.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-2.0F, -4.0F, 0.0F);
		bone5.addChild(bone3);
		setRotationAngle(bone3, 0.0F, -1.5708F, 0.5236F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 0, -2.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(2.0F, -4.0F, 0.0F);
		bone5.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 1.5708F, -0.5236F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 0, -2.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bb_main.render(f5);
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