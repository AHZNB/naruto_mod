// Made with Blockbench 4.2.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelDragonHead extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer jaw;
	private final ModelRenderer hornRight;
	private final ModelRenderer hornRight0;
	private final ModelRenderer hornRight1;
	private final ModelRenderer hornRight2;
	private final ModelRenderer hornRight3;
	private final ModelRenderer hornRight4;
	private final ModelRenderer hornLeft;
	private final ModelRenderer hornLeft0;
	private final ModelRenderer hornLeft1;
	private final ModelRenderer hornLeft2;
	private final ModelRenderer hornLeft3;
	private final ModelRenderer hornLeft4;
	private final ModelRenderer whiskerLeft;
	private final ModelRenderer whiskerLeft0;
	private final ModelRenderer whiskerLeft1;
	private final ModelRenderer whiskerLeft2;
	private final ModelRenderer whiskerLeft3;
	private final ModelRenderer whiskerLeft4;
	private final ModelRenderer whiskerRight;
	private final ModelRenderer whiskerRight0;
	private final ModelRenderer whiskerRight1;
	private final ModelRenderer whiskerRight2;
	private final ModelRenderer whiskerRight3;
	private final ModelRenderer whiskerRight4;
	private final ModelRenderer spine;
	private final ModelRenderer spine2;
	private final ModelRenderer spine3;
	private final ModelRenderer spine4;
	private final ModelRenderer spine5;
	private final ModelRenderer spine6;
	private final ModelRenderer spine7;
	private final ModelRenderer spine8;
	private final ModelRenderer eyes;

	public ModelDragonHead() {
		textureWidth = 256;
		textureHeight = 256;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 176, 44, -6.0F, 6.0F, -26.0F, 12, 5, 16, 1.0F, false));
		head.cubeList.add(new ModelBox(head, 112, 30, -8.0F, -1.0F, -11.0F, 16, 16, 16, 1.0F, false));
		head.cubeList.add(new ModelBox(head, 112, 0, -5.0F, 5.0F, -27.0F, 2, 2, 4, 1.0F, false));
		head.cubeList.add(new ModelBox(head, 112, 0, 3.0F, 5.0F, -27.0F, 2, 2, 4, 1.0F, true));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(9.0F, 7.0F, -11.0F);
		head.addChild(bone);
		setRotationAngle(bone, 0.0F, -0.7854F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 200, 0.0F, -8.0F, 0.0F, 10, 16, 0, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-9.0F, 7.0F, -11.0F);
		head.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.7854F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 200, -10.0F, -8.0F, 0.0F, 10, 16, 0, 0.0F, true));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.0F, 11.0F, -9.0F);
		head.addChild(jaw);
		setRotationAngle(jaw, 0.5236F, 0.0F, 0.0F);
		jaw.cubeList.add(new ModelBox(jaw, 176, 65, -6.0F, 0.0F, -16.0F, 12, 4, 16, 1.0F, false));

		hornRight = new ModelRenderer(this);
		hornRight.setRotationPoint(-6.0F, -2.0F, -13.0F);
		head.addChild(hornRight);
		setRotationAngle(hornRight, 0.0873F, -0.5236F, 0.0F);
		hornRight.cubeList.add(new ModelBox(hornRight, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 1.0F, false));

		hornRight0 = new ModelRenderer(this);
		hornRight0.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornRight.addChild(hornRight0);
		setRotationAngle(hornRight0, 0.0873F, 0.0873F, 0.0F);
		hornRight0.cubeList.add(new ModelBox(hornRight0, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.8F, false));

		hornRight1 = new ModelRenderer(this);
		hornRight1.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornRight0.addChild(hornRight1);
		setRotationAngle(hornRight1, 0.0873F, 0.0873F, 0.0F);
		hornRight1.cubeList.add(new ModelBox(hornRight1, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.6F, false));

		hornRight2 = new ModelRenderer(this);
		hornRight2.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornRight1.addChild(hornRight2);
		setRotationAngle(hornRight2, 0.0873F, 0.0873F, 0.0F);
		hornRight2.cubeList.add(new ModelBox(hornRight2, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.4F, false));

		hornRight3 = new ModelRenderer(this);
		hornRight3.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornRight2.addChild(hornRight3);
		setRotationAngle(hornRight3, 0.0873F, 0.0873F, 0.0F);
		hornRight3.cubeList.add(new ModelBox(hornRight3, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.2F, false));

		hornRight4 = new ModelRenderer(this);
		hornRight4.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornRight3.addChild(hornRight4);
		setRotationAngle(hornRight4, 0.0873F, 0.0873F, 0.0F);
		hornRight4.cubeList.add(new ModelBox(hornRight4, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.0F, false));

		hornLeft = new ModelRenderer(this);
		hornLeft.setRotationPoint(6.0F, -2.0F, -13.0F);
		head.addChild(hornLeft);
		setRotationAngle(hornLeft, 0.0873F, 0.5236F, 0.0F);
		hornLeft.cubeList.add(new ModelBox(hornLeft, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 1.0F, true));

		hornLeft0 = new ModelRenderer(this);
		hornLeft0.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornLeft.addChild(hornLeft0);
		setRotationAngle(hornLeft0, 0.0873F, -0.0873F, 0.0F);
		hornLeft0.cubeList.add(new ModelBox(hornLeft0, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.8F, true));

		hornLeft1 = new ModelRenderer(this);
		hornLeft1.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornLeft0.addChild(hornLeft1);
		setRotationAngle(hornLeft1, 0.0873F, -0.0873F, 0.0F);
		hornLeft1.cubeList.add(new ModelBox(hornLeft1, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.6F, true));

		hornLeft2 = new ModelRenderer(this);
		hornLeft2.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornLeft1.addChild(hornLeft2);
		setRotationAngle(hornLeft2, 0.0873F, -0.0873F, 0.0F);
		hornLeft2.cubeList.add(new ModelBox(hornLeft2, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.4F, true));

		hornLeft3 = new ModelRenderer(this);
		hornLeft3.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornLeft2.addChild(hornLeft3);
		setRotationAngle(hornLeft3, 0.0873F, -0.0873F, 0.0F);
		hornLeft3.cubeList.add(new ModelBox(hornLeft3, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.2F, true));

		hornLeft4 = new ModelRenderer(this);
		hornLeft4.setRotationPoint(0.0F, 0.0F, 7.0F);
		hornLeft3.addChild(hornLeft4);
		setRotationAngle(hornLeft4, 0.0873F, -0.0873F, 0.0F);
		hornLeft4.cubeList.add(new ModelBox(hornLeft4, 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.0F, true));

		whiskerLeft = new ModelRenderer(this);
		whiskerLeft.setRotationPoint(6.0F, 6.0F, -24.0F);
		head.addChild(whiskerLeft);
		setRotationAngle(whiskerLeft, 0.0F, 1.0472F, 0.0F);
		whiskerLeft.cubeList.add(new ModelBox(whiskerLeft, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.8F, true));

		whiskerLeft0 = new ModelRenderer(this);
		whiskerLeft0.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerLeft.addChild(whiskerLeft0);
		setRotationAngle(whiskerLeft0, -0.0873F, -0.1745F, 0.0F);
		whiskerLeft0.cubeList.add(new ModelBox(whiskerLeft0, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.7F, true));

		whiskerLeft1 = new ModelRenderer(this);
		whiskerLeft1.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerLeft0.addChild(whiskerLeft1);
		setRotationAngle(whiskerLeft1, -0.0873F, -0.1745F, 0.0F);
		whiskerLeft1.cubeList.add(new ModelBox(whiskerLeft1, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.6F, true));

		whiskerLeft2 = new ModelRenderer(this);
		whiskerLeft2.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerLeft1.addChild(whiskerLeft2);
		setRotationAngle(whiskerLeft2, -0.0873F, -0.1745F, 0.0F);
		whiskerLeft2.cubeList.add(new ModelBox(whiskerLeft2, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.5F, true));

		whiskerLeft3 = new ModelRenderer(this);
		whiskerLeft3.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerLeft2.addChild(whiskerLeft3);
		setRotationAngle(whiskerLeft3, -0.0873F, -0.1745F, 0.0F);
		whiskerLeft3.cubeList.add(new ModelBox(whiskerLeft3, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.4F, true));

		whiskerLeft4 = new ModelRenderer(this);
		whiskerLeft4.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerLeft3.addChild(whiskerLeft4);
		setRotationAngle(whiskerLeft4, -0.0873F, -0.1745F, 0.0F);
		whiskerLeft4.cubeList.add(new ModelBox(whiskerLeft4, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.2F, true));

		whiskerRight = new ModelRenderer(this);
		whiskerRight.setRotationPoint(-6.0F, 6.0F, -24.0F);
		head.addChild(whiskerRight);
		setRotationAngle(whiskerRight, 0.0F, -1.0472F, 0.0F);
		whiskerRight.cubeList.add(new ModelBox(whiskerRight, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.8F, false));

		whiskerRight0 = new ModelRenderer(this);
		whiskerRight0.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerRight.addChild(whiskerRight0);
		setRotationAngle(whiskerRight0, -0.0873F, 0.1745F, 0.0F);
		whiskerRight0.cubeList.add(new ModelBox(whiskerRight0, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.7F, false));

		whiskerRight1 = new ModelRenderer(this);
		whiskerRight1.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerRight0.addChild(whiskerRight1);
		setRotationAngle(whiskerRight1, -0.0873F, 0.1745F, 0.0F);
		whiskerRight1.cubeList.add(new ModelBox(whiskerRight1, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.6F, false));

		whiskerRight2 = new ModelRenderer(this);
		whiskerRight2.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerRight1.addChild(whiskerRight2);
		setRotationAngle(whiskerRight2, -0.0873F, 0.1745F, 0.0F);
		whiskerRight2.cubeList.add(new ModelBox(whiskerRight2, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.5F, false));

		whiskerRight3 = new ModelRenderer(this);
		whiskerRight3.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerRight2.addChild(whiskerRight3);
		setRotationAngle(whiskerRight3, -0.0873F, 0.1745F, 0.0F);
		whiskerRight3.cubeList.add(new ModelBox(whiskerRight3, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.4F, false));

		whiskerRight4 = new ModelRenderer(this);
		whiskerRight4.setRotationPoint(0.0F, 0.0F, 6.0F);
		whiskerRight3.addChild(whiskerRight4);
		setRotationAngle(whiskerRight4, -0.0873F, 0.1745F, 0.0F);
		whiskerRight4.cubeList.add(new ModelBox(whiskerRight4, 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.2F, false));

		spine = new ModelRenderer(this);
		spine.setRotationPoint(0.0F, 6.5F, 7.0F);
		spine.cubeList.add(new ModelBox(spine, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
		spine.cubeList.add(new ModelBox(spine, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));

		spine2 = new ModelRenderer(this);
		spine2.setRotationPoint(0.0F, 0.0F, 11.0F);
		spine.addChild(spine2);
		setRotationAngle(spine2, -0.5236F, 0.0F, 0.0F);
		spine2.cubeList.add(new ModelBox(spine2, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
		spine2.cubeList.add(new ModelBox(spine2, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));

		spine3 = new ModelRenderer(this);
		spine3.setRotationPoint(0.0F, 0.0F, 11.0F);
		spine2.addChild(spine3);
		setRotationAngle(spine3, -0.5236F, 0.0F, 0.0F);
		spine3.cubeList.add(new ModelBox(spine3, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
		spine3.cubeList.add(new ModelBox(spine3, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));

		spine4 = new ModelRenderer(this);
		spine4.setRotationPoint(0.0F, 0.0F, 11.0F);
		spine3.addChild(spine4);
		setRotationAngle(spine4, -0.5236F, 0.0F, 0.0F);
		spine4.cubeList.add(new ModelBox(spine4, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
		spine4.cubeList.add(new ModelBox(spine4, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));

		spine5 = new ModelRenderer(this);
		spine5.setRotationPoint(0.0F, 0.0F, 11.0F);
		spine4.addChild(spine5);
		setRotationAngle(spine5, -0.5236F, 0.0F, 0.0F);
		spine5.cubeList.add(new ModelBox(spine5, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
		spine5.cubeList.add(new ModelBox(spine5, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));

		spine6 = new ModelRenderer(this);
		spine6.setRotationPoint(0.0F, 0.0F, 11.0F);
		spine5.addChild(spine6);
		setRotationAngle(spine6, 0.2618F, 0.0F, 0.0F);
		spine6.cubeList.add(new ModelBox(spine6, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
		spine6.cubeList.add(new ModelBox(spine6, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));

		spine7 = new ModelRenderer(this);
		spine7.setRotationPoint(0.0F, 0.0F, 11.0F);
		spine6.addChild(spine7);
		setRotationAngle(spine7, 0.2618F, 0.0F, 0.0F);
		spine7.cubeList.add(new ModelBox(spine7, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
		spine7.cubeList.add(new ModelBox(spine7, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));

		spine8 = new ModelRenderer(this);
		spine8.setRotationPoint(0.0F, 0.0F, 11.0F);
		spine7.addChild(spine8);
		spine8.cubeList.add(new ModelBox(spine8, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
		spine8.cubeList.add(new ModelBox(spine8, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));

		eyes = new ModelRenderer(this);
		eyes.setRotationPoint(0.0F, 0.0F, 0.0F);
		eyes.cubeList.add(new ModelBox(eyes, 130, 50, -6.6F, 2.6F, -12.1F, 3, 2, 0, 0.0F, false));
		eyes.cubeList.add(new ModelBox(eyes, 130, 50, 3.6F, 2.6F, -12.1F, 3, 2, 0, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		spine.render(f5);
		eyes.render(f5);
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