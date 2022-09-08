//Made with Blockbench
//Paste this code into your mod.

public static class ModelArmorCustom extends ModelBase {
	private final ModelRenderer helmet;
	private final ModelRenderer bodywear;
	private final ModelRenderer rightArmwear;
	private final ModelRenderer leftArmwear;
	private final ModelRenderer rightLegwear;
	private final ModelRenderer leftLegwear;

	public ModelArmorCustom() {
		textureWidth = 64;
		textureHeight = 32;

		helmet = new ModelRenderer(this);
		helmet.setRotationPoint(0.0F, 0.0F, 0.0F);
		helmet.cubeList.add(new ModelBox(helmet, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.1F, false));
		helmet.cubeList.add(new ModelBox(helmet, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.3F, false));

		bodywear = new ModelRenderer(this);
		bodywear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bodywear.cubeList.add(new ModelBox(bodywear, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.1F, false));

		rightArmwear = new ModelRenderer(this);
		rightArmwear.setRotationPoint(-5.0F, 2.0F, 0.0F);
		rightArmwear.cubeList.add(new ModelBox(rightArmwear, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.1F, false));

		leftArmwear = new ModelRenderer(this);
		leftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
		leftArmwear.cubeList.add(new ModelBox(leftArmwear, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.1F, true));

		rightLegwear = new ModelRenderer(this);
		rightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
		rightLegwear.cubeList.add(new ModelBox(rightLegwear, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));

		leftLegwear = new ModelRenderer(this);
		leftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
		leftLegwear.cubeList.add(new ModelBox(leftLegwear, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		helmet.render(f5);
		bodywear.render(f5);
		rightArmwear.render(f5);
		leftArmwear.render(f5);
		rightLegwear.render(f5);
		leftLegwear.render(f5);
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