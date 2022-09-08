//Made with Blockbench
//Paste this code into your mod.

public static class Modelbuffman extends ModelBase {
	private final ModelRenderer leg_right;
	private final ModelRenderer leg_left;
	private final ModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer arm_left;
	private final ModelRenderer arm_right;

	public Modelbuffman() {
		textureWidth = 64;
		textureHeight = 64;

		leg_right = new ModelRenderer(this);
		leg_right.setRotationPoint(-2.0F, 12.0F, -1.0F);
		leg_right.cubeList.add(new ModelBox(leg_right, 0, 0, -2.0F, 0.0F,
				-2.0F, 4, 12, 4, 0.0F, false));

		leg_left = new ModelRenderer(this);
		leg_left.setRotationPoint(2.0F, 12.0F, -1.0F);
		leg_left.cubeList.add(new ModelBox(leg_left, 16, 0, -2.0F, 0.0F, -2.0F,
				4, 12, 4, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 6.0F, -1.0F);
		body.cubeList.add(new ModelBox(body, 0, 16, -4.0F, -6.0F, -2.0F, 8, 12,
				4, 0.0F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, -1.0F);
		head.cubeList.add(new ModelBox(head, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8,
				8, 0.0F, false));

		arm_left = new ModelRenderer(this);
		arm_left.setRotationPoint(4.0F, 3.0F, -1.0F);
		arm_left.cubeList.add(new ModelBox(arm_left, 24, 16, 0.0F, -3.0F,
				-2.0F, 4, 13, 4, 0.0F, false));

		arm_right = new ModelRenderer(this);
		arm_right.setRotationPoint(-4.0F, 3.0F, -1.0F);
		arm_right.cubeList.add(new ModelBox(arm_right, 24, 16, -4.0F, -3.0F,
				-2.0F, 4, 13, 4, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		leg_right.render(f5);
		leg_left.render(f5);
		body.render(f5);
		head.render(f5);
		arm_left.render(f5);
		arm_right.render(f5);
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y,
			float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3,
			float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
		this.leg_right.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
		this.head.rotateAngleY = f3 / (180F / (float) Math.PI);
		this.head.rotateAngleX = f4 / (180F / (float) Math.PI);
		this.arm_right.rotateAngleX = MathHelper.cos(f * 0.6662F
				+ (float) Math.PI)
				* f1;
		this.leg_left.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
		this.arm_left.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
	}
}