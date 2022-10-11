// Made with Blockbench 4.4.2
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelScrollSanshouo extends ModelBase {
	private final ModelRenderer hinge;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;
	private final ModelRenderer bone9;
	private final ModelRenderer bone10;
	private final ModelRenderer bone11;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer bone14;

	public ModelScrollSanshouo() {
		textureWidth = 16;
		textureHeight = 16;

		hinge = new ModelRenderer(this);
		hinge.setRotationPoint(0.0F, 23.15F, 0.0F);
		hinge.cubeList.add(new ModelBox(hinge, 0, 0, -4.0F, -0.5F, -0.5F, 4, 1, 1, 0.1F, false));
		hinge.cubeList.add(new ModelBox(hinge, 0, 0, 0.0F, -0.5F, -0.5F, 4, 1, 1, 0.1F, true));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.5F);
		setRotationAngle(bone, -1.5708F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 2, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone.addChild(bone2);
		setRotationAngle(bone2, -1.0472F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 3, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone2.addChild(bone3);
		setRotationAngle(bone3, -1.0472F, 0.0F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 4, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone3.addChild(bone4);
		setRotationAngle(bone4, -1.0472F, 0.0F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 5, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone4.addChild(bone5);
		setRotationAngle(bone5, -1.0472F, 0.0F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 0, 6, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone5.addChild(bone6);
		setRotationAngle(bone6, -1.0472F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 0, 7, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone6.addChild(bone7);
		setRotationAngle(bone7, -1.0472F, 0.0F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 0, 8, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone7.addChild(bone8);
		setRotationAngle(bone8, -1.0472F, 0.0F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 0, 9, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone8.addChild(bone9);
		setRotationAngle(bone9, -1.0472F, 0.0F, 0.0F);
		bone9.cubeList.add(new ModelBox(bone9, 0, 10, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone9.addChild(bone10);
		setRotationAngle(bone10, -1.0472F, 0.0F, 0.0F);
		bone10.cubeList.add(new ModelBox(bone10, 0, 11, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone10.addChild(bone11);
		setRotationAngle(bone11, -1.0472F, 0.0F, 0.0F);
		bone11.cubeList.add(new ModelBox(bone11, 0, 12, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone11.addChild(bone12);
		setRotationAngle(bone12, -1.0472F, 0.0F, 0.0F);
		bone12.cubeList.add(new ModelBox(bone12, 0, 13, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone12.addChild(bone13);
		setRotationAngle(bone13, -1.0472F, 0.0F, 0.0F);
		bone13.cubeList.add(new ModelBox(bone13, 0, 14, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone13.addChild(bone14);
		setRotationAngle(bone14, -1.0472F, 0.0F, 0.0F);
		bone14.cubeList.add(new ModelBox(bone14, 0, 15, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		hinge.render(f5);
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