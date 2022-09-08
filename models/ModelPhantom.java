// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelPhantom extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer leftWingBody;
	private final ModelRenderer leftWing;
	private final ModelRenderer rightWingBody;
	private final ModelRenderer rightWing;
	private final ModelRenderer head;
	private final ModelRenderer tail;
	private final ModelRenderer tailtip;

	public ModelPhantom() {
		textureWidth = 64;
		textureHeight = 64;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 8, -3.0F, -2.0F, -8.0F, 5, 3, 9, 0.0F, false));

		leftWingBody = new ModelRenderer(this);
		leftWingBody.setRotationPoint(2.0F, -2.0F, -8.0F);
		body.addChild(leftWingBody);
		setRotationAngle(leftWingBody, 0.0F, 0.0F, 0.0873F);
		leftWingBody.cubeList.add(new ModelBox(leftWingBody, 23, 12, 0.0F, 0.0F, 0.0F, 6, 2, 9, 0.0F, false));

		leftWing = new ModelRenderer(this);
		leftWing.setRotationPoint(6.0F, 0.0F, 0.0F);
		leftWingBody.addChild(leftWing);
		setRotationAngle(leftWing, 0.0F, 0.0F, 0.1745F);
		leftWing.cubeList.add(new ModelBox(leftWing, 16, 24, 0.0F, 0.0F, 0.0F, 13, 1, 9, 0.0F, false));

		rightWingBody = new ModelRenderer(this);
		rightWingBody.setRotationPoint(-3.0F, -2.0F, -8.0F);
		body.addChild(rightWingBody);
		setRotationAngle(rightWingBody, 0.0F, 0.0F, -0.0873F);
		rightWingBody.cubeList.add(new ModelBox(rightWingBody, 23, 12, -6.0F, 0.0F, 0.0F, 6, 2, 9, 0.0F, true));

		rightWing = new ModelRenderer(this);
		rightWing.setRotationPoint(-6.0F, 0.0F, 0.0F);
		rightWingBody.addChild(rightWing);
		setRotationAngle(rightWing, 0.0F, 0.0F, -0.1745F);
		rightWing.cubeList.add(new ModelBox(rightWing, 16, 24, -13.0F, 0.0F, 0.0F, 13, 1, 9, 0.0F, true));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 1.0F, -7.0F);
		body.addChild(head);
		head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -2.0F, -5.0F, 7, 3, 5, 0.0F, false));

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, -2.0F, 1.0F);
		body.addChild(tail);
		setRotationAngle(tail, -0.0873F, 0.0F, 0.0F);
		tail.cubeList.add(new ModelBox(tail, 3, 20, -2.0F, 0.0F, 0.0F, 3, 2, 6, 0.0F, false));

		tailtip = new ModelRenderer(this);
		tailtip.setRotationPoint(0.0F, 0.5F, 6.0F);
		tail.addChild(tailtip);
		setRotationAngle(tailtip, -0.0873F, 0.0F, 0.0F);
		tailtip.cubeList.add(new ModelBox(tailtip, 4, 29, -1.0F, 0.0F, 0.0F, 1, 1, 6, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		body.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
		this.leftWingBody.rotateAngleZ = f2 / 20.f;
		this.leftWing.rotateAngleZ = f2 / 20.f;
		this.rightWing.rotateAngleZ = f2 / 20.f;
		this.rightWingBody.rotateAngleZ = f2 / 20.f;
	}
}