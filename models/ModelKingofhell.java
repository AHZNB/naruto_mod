//Made with Blockbench
//Paste this code into your mod.

public static class ModelKingofhell extends ModelBase {
	private final ModelRenderer bone;
	private final ModelRenderer bone3;
	private final ModelRenderer mask_right;
	private final ModelRenderer bone4;
	private final ModelRenderer mask_left;
	private final ModelRenderer bone2;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;
	private final ModelRenderer bone10;
	private final ModelRenderer bone11;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer bone9;

	public ModelKingofhell() {
		textureWidth = 144;
		textureHeight = 144;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -8.0F, -24.0F, -8.0F, 16,
				24, 16, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-8.0F, -4.0F, -8.0F);
		bone.addChild(bone3);
		bone3.cubeList.add(new ModelBox(bone3, 64, 26, -0.1F, -7.0F, -0.1F, 0,
				11, 16, 0.0F, false));

		mask_right = new ModelRenderer(this);
		mask_right.setRotationPoint(-0.1F, 0.0F, 0.0F);
		setRotationAngle(mask_right, 0.0F, 0.0873F, 0.0F);
		bone3.addChild(mask_right);
		mask_right.cubeList.add(new ModelBox(mask_right, 96, 26, 0.0F, -7.0F,
				0.0F, 8, 11, 0, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(8.0F, -4.0F, -8.0F);
		bone.addChild(bone4);
		bone4.cubeList.add(new ModelBox(bone4, 64, 26, 0.1F, -7.0F, -0.1F, 0,
				11, 16, 0.0F, true));

		mask_left = new ModelRenderer(this);
		mask_left.setRotationPoint(0.1F, 0.0F, 0.0F);
		setRotationAngle(mask_left, 0.0F, -0.0873F, 0.0F);
		bone4.addChild(mask_left);
		mask_left.cubeList.add(new ModelBox(mask_left, 96, 26, -8.0F, -7.0F,
				0.0F, 8, 11, 0, 0.0F, true));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 5.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 64, 0, -9.0F, -4.0F, -9.0F, 18,
				7, 18, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(-4.5F, 3.0F, -9.5F);
		setRotationAngle(bone5, 0.0F, -0.4363F, 0.0F);
		bone2.addChild(bone5);
		bone5.cubeList.add(new ModelBox(bone5, 112, 26, -0.5F, -8.0F, -0.5F, 1,
				8, 1, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, -7.0F, 0.0F);
		setRotationAngle(bone6, -0.7854F, 0.0F, 0.0F);
		bone5.addChild(bone6);
		bone6.cubeList.add(new ModelBox(bone6, 116, 26, -0.5845F, -8.2961F,
				-1.0524F, 1, 8, 1, 0.0F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, -7.0F, 0.0F);
		setRotationAngle(bone7, -0.7854F, 0.0F, 0.0F);
		bone6.addChild(bone7);
		bone7.cubeList.add(new ModelBox(bone7, 120, 26, -0.5F, -8.0F, -1.6F, 1,
				8, 1, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, -8.0F, -0.2F);
		setRotationAngle(bone8, -0.5236F, 0.0F, 0.0F);
		bone7.addChild(bone8);
		bone8.cubeList.add(new ModelBox(bone8, 124, 26, -0.5F, -7.2929F,
				-1.2071F, 1, 8, 1, 0.0F, false));
		bone8.cubeList.add(new ModelBox(bone8, 128, 26, -0.5F, -14.364F,
				-1.2071F, 1, 8, 1, 0.0F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(4.5F, 3.0F, -9.5F);
		setRotationAngle(bone10, 0.0F, 0.4363F, 0.0F);
		bone2.addChild(bone10);
		bone10.cubeList.add(new ModelBox(bone10, 112, 26, -0.5F, -8.0F, -0.5F,
				1, 8, 1, 0.0F, true));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(0.0F, -7.0F, 0.0F);
		setRotationAngle(bone11, -0.7854F, 0.0F, 0.0F);
		bone10.addChild(bone11);
		bone11.cubeList.add(new ModelBox(bone11, 116, 26, -0.4155F, -8.2961F,
				-1.0524F, 1, 8, 1, 0.0F, true));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(0.0F, -7.0F, 0.0F);
		setRotationAngle(bone12, -0.7854F, 0.0F, 0.0F);
		bone11.addChild(bone12);
		bone12.cubeList.add(new ModelBox(bone12, 120, 26, -0.5F, -8.0F, -1.6F,
				1, 8, 1, 0.0F, true));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(0.0F, -8.0F, -0.2F);
		setRotationAngle(bone13, -0.5236F, 0.0F, 0.0F);
		bone12.addChild(bone13);
		bone13.cubeList.add(new ModelBox(bone13, 124, 26, -0.5F, -7.2929F,
				-1.2071F, 1, 8, 1, 0.0F, true));
		bone13.cubeList.add(new ModelBox(bone13, 128, 26, -0.5F, -14.364F,
				-1.2071F, 1, 8, 1, 0.0F, true));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(0.0F, 14.0F, 4.0F);
		bone9.cubeList.add(new ModelBox(bone9, 0, 16, -11.9F, -10.0F, -16.0F,
				0, 20, 24, 0.0F, false));
		bone9.cubeList.add(new ModelBox(bone9, 0, 37, -12.0F, -10.0F, -16.0F,
				0, 20, 24, 0.0F, false));
		bone9.cubeList.add(new ModelBox(bone9, 0, 16, 11.9F, -10.0F, -16.0F, 0,
				20, 24, 0.0F, true));
		bone9.cubeList.add(new ModelBox(bone9, 0, 37, 12.0F, -10.0F, -16.0F, 0,
				20, 24, 0.0F, true));
		bone9.cubeList.add(new ModelBox(bone9, 48, 61, -12.0F, -10.0F, 7.9F,
				24, 20, 0, 0.0F, false));
		bone9.cubeList.add(new ModelBox(bone9, 96, 61, -12.0F, -10.0F, 8.0F,
				24, 20, 0, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		int popoutend = 60;
		float scale = 3.0F;
		float translate = scale;
		GlStateManager.pushMatrix();
		if (entity.ticksExisted <= popoutend)
			translate = ((float) entity.ticksExisted / (float) popoutend)
					* scale;
		GlStateManager.translate(0.0F, 1.5F - 1.5F * translate, 0.0F);
		GlStateManager.scale(scale, scale, scale);
		bone.render(f5);
		bone2.render(f5);
		bone9.render(f5);
		GlStateManager.popMatrix();
	}
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y,
			float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}