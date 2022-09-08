// Made with Blockbench 3.9.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelBeetle extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer Mouth1;
	private final ModelRenderer Mouth2;
	private final ModelRenderer Eye1;
	private final ModelRenderer Eye2;
	private final ModelRenderer body;
	private final ModelRenderer Leg1;
	private final ModelRenderer foreleg1;
	private final ModelRenderer Leg2;
	private final ModelRenderer foreleg2;
	private final ModelRenderer Leg3;
	private final ModelRenderer foreleg3;
	private final ModelRenderer Leg4;
	private final ModelRenderer foreleg4;
	private final ModelRenderer Leg5;
	private final ModelRenderer foreleg5;
	private final ModelRenderer Leg6;
	private final ModelRenderer foreleg6;

	public ModelBeetle() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -1.5F, -5.0F);
		setRotationAngle(head, 0.3491F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -1.5F, 0.5F, -7.0F, 3, 2, 1, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 15, 24, -1.5F, 0.0F, -6.0F, 3, 3, 3, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 16, 17, -2.5F, 0.0F, -4.0F, 5, 3, 4, 0.1F, false));

		Mouth1 = new ModelRenderer(this);
		Mouth1.setRotationPoint(-1.0F, 1.7352F, -6.5119F);
		head.addChild(Mouth1);
		setRotationAngle(Mouth1, 0.3491F, -0.2618F, 0.0F);
		Mouth1.cubeList.add(new ModelBox(Mouth1, 4, 6, -0.5F, -0.4852F, -1.9881F, 1, 1, 2, -0.1F, false));

		Mouth2 = new ModelRenderer(this);
		Mouth2.setRotationPoint(1.0F, 1.7352F, -6.5119F);
		head.addChild(Mouth2);
		setRotationAngle(Mouth2, 0.3491F, 0.2618F, 0.0F);
		Mouth2.cubeList.add(new ModelBox(Mouth2, 4, 6, -0.5F, -0.4852F, -1.9881F, 1, 1, 2, -0.1F, true));

		Eye1 = new ModelRenderer(this);
		Eye1.setRotationPoint(-1.25F, 0.4852F, -6.0119F);
		head.addChild(Eye1);
		Eye1.cubeList.add(new ModelBox(Eye1, 4, 3, -0.5F, -0.7352F, -0.4881F, 1, 1, 2, 0.0F, false));

		Eye2 = new ModelRenderer(this);
		Eye2.setRotationPoint(1.25F, 0.4852F, -6.0119F);
		head.addChild(Eye2);
		Eye2.cubeList.add(new ModelBox(Eye2, 4, 3, -0.5F, -0.7352F, -0.4881F, 1, 1, 2, 0.0F, true));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 0, -4.0F, -1.5F, -5.0F, 8, 4, 10, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 0, 14, -3.5F, -1.0F, 5.0F, 7, 4, 3, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 0, 21, -3.0F, 0.0F, 7.0F, 6, 3, 3, 0.0F, false));
		body.cubeList.add(new ModelBox(body, 26, 0, -2.5F, 1.0F, 9.0F, 5, 2, 2, 0.0F, false));

		Leg1 = new ModelRenderer(this);
		Leg1.setRotationPoint(-3.0F, 2.0F, -4.0F);
		setRotationAngle(Leg1, 0.0F, -0.5236F, 0.5236F);
		Leg1.cubeList.add(new ModelBox(Leg1, 17, 14, -5.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, false));

		foreleg1 = new ModelRenderer(this);
		foreleg1.setRotationPoint(-4.5F, 0.25F, 0.0F);
		Leg1.addChild(foreleg1);
		foreleg1.cubeList.add(new ModelBox(foreleg1, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, false));

		Leg2 = new ModelRenderer(this);
		Leg2.setRotationPoint(3.0F, 2.0F, -4.0F);
		setRotationAngle(Leg2, 0.0F, 0.5236F, -0.5236F);
		Leg2.cubeList.add(new ModelBox(Leg2, 17, 14, 0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, true));

		foreleg2 = new ModelRenderer(this);
		foreleg2.setRotationPoint(4.5F, 0.25F, 0.0F);
		Leg2.addChild(foreleg2);
		foreleg2.cubeList.add(new ModelBox(foreleg2, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, true));

		Leg3 = new ModelRenderer(this);
		Leg3.setRotationPoint(-3.0F, 2.0F, 0.0F);
		setRotationAngle(Leg3, 0.0F, 0.0F, 0.5236F);
		Leg3.cubeList.add(new ModelBox(Leg3, 17, 14, -5.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, false));

		foreleg3 = new ModelRenderer(this);
		foreleg3.setRotationPoint(-4.5F, 0.25F, 0.0F);
		Leg3.addChild(foreleg3);
		foreleg3.cubeList.add(new ModelBox(foreleg3, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, false));

		Leg4 = new ModelRenderer(this);
		Leg4.setRotationPoint(3.0F, 2.0F, 0.0F);
		setRotationAngle(Leg4, 0.0F, 0.0F, -0.5236F);
		Leg4.cubeList.add(new ModelBox(Leg4, 17, 14, 0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, true));

		foreleg4 = new ModelRenderer(this);
		foreleg4.setRotationPoint(4.5F, 0.25F, 0.0F);
		Leg4.addChild(foreleg4);
		foreleg4.cubeList.add(new ModelBox(foreleg4, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, true));

		Leg5 = new ModelRenderer(this);
		Leg5.setRotationPoint(-3.0F, 2.0F, 4.0F);
		setRotationAngle(Leg5, 0.0F, 0.5236F, 0.5236F);
		Leg5.cubeList.add(new ModelBox(Leg5, 17, 14, -5.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, false));

		foreleg5 = new ModelRenderer(this);
		foreleg5.setRotationPoint(-4.5F, 0.25F, 0.0F);
		Leg5.addChild(foreleg5);
		foreleg5.cubeList.add(new ModelBox(foreleg5, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, false));

		Leg6 = new ModelRenderer(this);
		Leg6.setRotationPoint(3.0F, 2.0F, 4.0F);
		setRotationAngle(Leg6, 0.0F, -0.5236F, -0.5236F);
		Leg6.cubeList.add(new ModelBox(Leg6, 17, 14, 0.0F, -0.5F, -0.5F, 5, 1, 1, 0.0F, true));

		foreleg6 = new ModelRenderer(this);
		foreleg6.setRotationPoint(4.5F, 0.25F, 0.0F);
		Leg6.addChild(foreleg6);
		foreleg6.cubeList.add(new ModelBox(foreleg6, 0, 3, -0.5F, -0.25F, -0.5F, 1, 5, 1, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		body.render(f5);
		Leg1.render(f5);
		Leg2.render(f5);
		Leg3.render(f5);
		Leg4.render(f5);
		Leg5.render(f5);
		Leg6.render(f5);
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