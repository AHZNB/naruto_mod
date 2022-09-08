// Made with Blockbench 3.7.5
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

public static class ModelShark extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer foreHead;
	private final ModelRenderer jaw;
	private final ModelRenderer tail;
	private final ModelRenderer tailFin;
	private final ModelRenderer tailFinUpper_r1;
	private final ModelRenderer tailFinLower_r1;
	private final ModelRenderer backFin;
	private final ModelRenderer leftFin;
	private final ModelRenderer rightFin;

	public ModelShark() {
		textureWidth = 64;
		textureHeight = 64;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 24.0F, -5.0F);
		body.cubeList.add(new ModelBox(body, 0, 0, -4.0F, -7.0F, 0.0F, 8, 7, 13, 0.0F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, -3.0F, 0.0F);
		body.addChild(head);

		foreHead = new ModelRenderer(this);
		foreHead.setRotationPoint(0.0F, -3.5F, 0.0F);
		head.addChild(foreHead);
		setRotationAngle(foreHead, 0.1745F, 0.0F, 0.0F);
		foreHead.cubeList.add(new ModelBox(foreHead, 19, 20, -4.0F, 0.0F, -6.0F, 8, 4, 6, 0.0F, false));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.0F, 2.5F, 0.25F);
		head.addChild(jaw);
		jaw.cubeList.add(new ModelBox(jaw, 29, 0, -3.5F, -1.5F, -4.75F, 7, 2, 5, 0.0F, false));

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, -3.5F, 13.0F);
		body.addChild(tail);
		tail.cubeList.add(new ModelBox(tail, 0, 20, -2.0F, -2.5F, -1.0F, 4, 5, 11, 0.0F, false));

		tailFin = new ModelRenderer(this);
		tailFin.setRotationPoint(0.0F, -0.5F, 8.0F);
		tail.addChild(tailFin);

		tailFinUpper_r1 = new ModelRenderer(this);
		tailFinUpper_r1.setRotationPoint(0.0F, -1.0F, 1.0F);
		tailFin.addChild(tailFinUpper_r1);
		setRotationAngle(tailFinUpper_r1, -0.6109F, 0.0F, 0.0F);
		tailFinUpper_r1.cubeList
				.add(new ModelBox(tailFinUpper_r1, 0, 20, -0.5F, -6.9924F, -1.1743F, 1, 8, 3, 0.0F, false));

		tailFinLower_r1 = new ModelRenderer(this);
		tailFinLower_r1.setRotationPoint(0.0F, 1.0F, 1.0F);
		tailFin.addChild(tailFinLower_r1);
		setRotationAngle(tailFinLower_r1, 0.5236F, 0.0F, 0.0F);
		tailFinLower_r1.cubeList
				.add(new ModelBox(tailFinLower_r1, 0, 36, -0.5F, -1.4924F, -1.0403F, 1, 6, 3, 0.0F, false));

		backFin = new ModelRenderer(this);
		backFin.setRotationPoint(0.0F, -6.0F, 6.0F);
		body.addChild(backFin);
		setRotationAngle(backFin, -0.5236F, 0.0F, 0.0F);
		backFin.cubeList.add(new ModelBox(backFin, 0, 0, -0.5F, -7.75F, -1.5F, 1, 8, 4, 0.0F, false));

		leftFin = new ModelRenderer(this);
		leftFin.setRotationPoint(3.0F, -3.0F, 8.0F);
		body.addChild(leftFin);
		setRotationAngle(leftFin, 0.9599F, 0.0F, 1.8675F);
		leftFin.cubeList.add(new ModelBox(leftFin, 32, 34, 0.0F, -4.0F, -1.5F, 1, 4, 7, 0.0F, false));

		rightFin = new ModelRenderer(this);
		rightFin.setRotationPoint(-3.0F, -3.0F, 8.0F);
		body.addChild(rightFin);
		setRotationAngle(rightFin, 0.9599F, 0.0F, -1.8675F);
		rightFin.cubeList.add(new ModelBox(rightFin, 32, 34, -1.0F, -4.0F, -1.5F, 1, 4, 7, 0.0F, false));
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
		this.tail.rotateAngleY = MathHelper.cos(f * 1.0F) * 1.0F * f1;
		this.tailFin.rotateAngleY = MathHelper.cos(f * 1.0F) * 1.0F * f1;
	}
}