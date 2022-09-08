// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelChainLink extends ModelBase {
	private final ModelRenderer chain;
	private final ModelRenderer link;

	public ModelChainLink() {
		textureWidth = 16;
		textureHeight = 16;

		chain = new ModelRenderer(this);
		chain.setRotationPoint(0.0F, 0.0F, 0.0F);
		chain.cubeList.add(new ModelBox(chain, 0, 0, -0.5F, 0.0F, -1.0F, 1, 3, 2, 0.0F, false));
		chain.cubeList.add(new ModelBox(chain, 8, 2, -0.5F, 0.0F, -1.0F, 1, 3, 2, 0.2F, false));

		link = new ModelRenderer(this);
		link.setRotationPoint(0.0F, 0.0F, 0.0F);
		chain.addChild(link);
		setRotationAngle(link, 0.0F, -1.5708F, 0.0F);
		link.cubeList.add(new ModelBox(link, 0, 0, -0.5F, -2.5F, -1.0F, 1, 3, 2, 0.0F, false));
		link.cubeList.add(new ModelBox(link, 8, 2, -0.5F, -2.5F, -1.0F, 1, 3, 2, 0.2F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		chain.render(f5);
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