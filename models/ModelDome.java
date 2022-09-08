// Made with Blockbench 3.8.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelDome extends ModelBase {
	private final ModelRenderer dome;
	private final ModelRenderer wall;
	private final ModelRenderer bone2;
	private final ModelRenderer bone6;
	private final ModelRenderer bone3;
	private final ModelRenderer bone7;
	private final ModelRenderer bone4;
	private final ModelRenderer bone8;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer roof;
	private final ModelRenderer bone10;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer bone18;
	private final ModelRenderer bone16;
	private final ModelRenderer bone14;
	private final ModelRenderer bone17;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;

	public ModelDome() {
		textureWidth = 16;
		textureHeight = 16;

		dome = new ModelRenderer(this);
		dome.setRotationPoint(0.0F, 24.0F, 0.0F);

		wall = new ModelRenderer(this);
		wall.setRotationPoint(0.0F, 0.0F, 0.0F);
		dome.addChild(wall);

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -8.0F, -1.5F);
		wall.addChild(bone2);
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -4.0F, 0.0F, -8.0F, 8, 8, 0, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 0.0F, 17.7F);
		wall.addChild(bone6);
		bone6.cubeList.add(new ModelBox(bone6, 0, 0, -4.0F, -8.0F, -8.0F, 8, 8, 0, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(4.0F, -16.0F, -9.5F);
		wall.addChild(bone3);
		setRotationAngle(bone3, 0.0F, -0.7854F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 0, 0.0F, 8.0F, 0.0F, 8, 8, 0, 0.0F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(-9.6F, -8.0F, 4.1F);
		wall.addChild(bone7);
		setRotationAngle(bone7, 0.0F, -0.7854F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 0, 0, 0.0F, 0.0F, 0.0F, 8, 8, 0, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(9.75F, -16.0F, -3.75F);
		wall.addChild(bone4);
		setRotationAngle(bone4, 0.0F, -1.5708F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 0, -0.1F, 8.0F, 0.1F, 8, 8, 0, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(-9.55F, -16.0F, -3.75F);
		wall.addChild(bone8);
		setRotationAngle(bone8, 0.0F, -1.5708F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 0, 0, -0.15F, 8.0F, 0.05F, 8, 8, 0, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(9.6F, -8.0F, 4.2F);
		wall.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.7854F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -7.95F, 0.0F, -0.025F, 8, 8, 0, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-6.7F, -16.0F, -6.9F);
		wall.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.7854F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 0, -4.2023F, 8.0F, 0.0457F, 8, 8, 0, 0.0F, false));

		roof = new ModelRenderer(this);
		roof.setRotationPoint(0.0F, -16.0F, -9.5F);
		dome.addChild(roof);

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.05F, 2.4F, 9.65F);
		roof.addChild(bone10);
		setRotationAngle(bone10, -1.5708F, 0.0F, 0.0F);
		bone10.cubeList.add(new ModelBox(bone10, 0, 8, -4.0F, -4.0F, 0.0F, 8, 8, 0, 0.0F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(0.0F, 0.0F, 0.0F);
		roof.addChild(bone12);
		setRotationAngle(bone12, -0.7854F, 0.0F, 0.0F);
		bone12.cubeList.add(new ModelBox(bone12, 0, 8, -4.0F, -2.3431F, 5.6569F, 8, 8, 0, 0.0F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(7.0F, 0.0F, 3.0F);
		roof.addChild(bone13);
		setRotationAngle(bone13, -0.7854F, -0.7854F, 0.0F);
		bone13.cubeList.add(new ModelBox(bone13, 0, 8, -4.2426F, -2.3431F, 5.6569F, 8, 8, 0, 0.0F, false));

		bone18 = new ModelRenderer(this);
		bone18.setRotationPoint(0.0F, 0.0F, 19.2F);
		roof.addChild(bone18);
		setRotationAngle(bone18, 0.7854F, 0.0F, 0.0F);
		bone18.cubeList.add(new ModelBox(bone18, 0, 8, -4.0F, -2.3431F, -5.6569F, 8, 8, 0, 0.0F, false));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(7.0F, 0.0F, 16.3F);
		roof.addChild(bone16);
		setRotationAngle(bone16, 0.7854F, 0.7854F, 0.0F);
		bone16.cubeList.add(new ModelBox(bone16, 0, 8, -4.2426F, -2.3431F, -5.6569F, 8, 8, 0, 0.0F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(9.75F, 0.0F, 9.75F);
		roof.addChild(bone14);
		setRotationAngle(bone14, -0.7854F, -1.5708F, 0.0F);
		bone14.cubeList.add(new ModelBox(bone14, 0, 8, -4.1F, -2.3431F, 5.7569F, 8, 8, 0, 0.0F, false));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(-9.65F, 0.0F, 9.75F);
		roof.addChild(bone17);
		setRotationAngle(bone17, -0.7854F, 1.5708F, 0.0F);
		bone17.cubeList.add(new ModelBox(bone17, 0, 8, -3.9F, -2.3431F, 5.7569F, 8, 8, 0, 0.0F, true));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-6.8F, 8.0F, 2.7F);
		roof.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.7854F, 0.7854F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 8, -4.0609F, -8.0F, 0.0457F, 8, 8, 0, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-6.8F, 8.0F, 16.3F);
		roof.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.7854F, -0.7854F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 8, -3.8609F, -7.9F, -0.0457F, 8, 8, 0, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		dome.render(f5);
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