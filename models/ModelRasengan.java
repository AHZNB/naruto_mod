// Made with Blockbench 3.6.5
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

public static class ModelRasengan extends ModelBase {
	private final ModelRenderer core;
	private final ModelRenderer bone;
	private final ModelRenderer bone4;
	private final ModelRenderer bone3;
	private final ModelRenderer bone2;
	private final ModelRenderer shell;
	private final ModelRenderer bone5;
	private final ModelRenderer bone8;
	private final ModelRenderer bone7;
	private final ModelRenderer bone6;

	public ModelRasengan() {
		textureWidth = 32;
		textureHeight = 32;

		core = new ModelRenderer(this);
		core.setRotationPoint(0.0F, 0.0F, 0.0F);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, -4.0F, 0.0F);
		core.addChild(bone);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 0, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 0, 0, -1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, -4.0F, 0.0F);
		core.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 0.0F, 0.7854F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
		bone4.cubeList.add(new ModelBox(bone4, 0, 0, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
		bone4.cubeList.add(new ModelBox(bone4, 0, 0, -1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, -4.0F, 0.0F);
		core.addChild(bone3);
		setRotationAngle(bone3, 0.0F, -0.7854F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
		bone3.cubeList.add(new ModelBox(bone3, 0, 0, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
		bone3.cubeList.add(new ModelBox(bone3, 0, 0, -1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -4.0F, 0.0F);
		core.addChild(bone2);
		setRotationAngle(bone2, -0.7854F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F, false));

		shell = new ModelRenderer(this);
		shell.setRotationPoint(0.0F, 0.0F, 0.0F);

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, -4.0F, 0.0F);
		shell.addChild(bone5);
		bone5.cubeList.add(new ModelBox(bone5, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, -4.0F, 0.0F);
		shell.addChild(bone8);
		setRotationAngle(bone8, 0.0F, 0.0F, 0.7854F);
		bone8.cubeList.add(new ModelBox(bone8, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, -4.0F, 0.0F);
		shell.addChild(bone7);
		setRotationAngle(bone7, 0.0F, -0.7854F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, -4.0F, 0.0F);
		shell.addChild(bone6);
		setRotationAngle(bone6, -0.7854F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 0, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		core.render(f5);
		shell.render(f5);
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