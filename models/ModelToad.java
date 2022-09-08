// Made with Blockbench 4.2.1
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelToad extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer bone4;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer head_r1;
	private final ModelRenderer jaw;
	private final ModelRenderer head_r2;
	private final ModelRenderer Pipe;
	private final ModelRenderer body;
	private final ModelRenderer chest;
	private final ModelRenderer bone6;
	private final ModelRenderer bone11;
	private final ModelRenderer bone5;
	private final ModelRenderer bunda;
	private final ModelRenderer bunda_r1;
	private final ModelRenderer armRight;
	private final ModelRenderer bone7;
	private final ModelRenderer handRight;
	private final ModelRenderer bone8;
	private final ModelRenderer bone9;
	private final ModelRenderer bone10;
	private final ModelRenderer blade;
	private final ModelRenderer armLeft;
	private final ModelRenderer bone14;
	private final ModelRenderer handLeft;
	private final ModelRenderer bone15;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer legRight;
	private final ModelRenderer bone21;
	private final ModelRenderer legLowerRight;
	private final ModelRenderer legLowerRight3_r1;
	private final ModelRenderer footRight;
	private final ModelRenderer bone12;
	private final ModelRenderer bone3;
	private final ModelRenderer bone13;
	private final ModelRenderer legLeft;
	private final ModelRenderer bone18;
	private final ModelRenderer legLowerLeft;
	private final ModelRenderer legLowerRight4_r1;
	private final ModelRenderer footLeft;
	private final ModelRenderer bone19;
	private final ModelRenderer bone20;
	private final ModelRenderer bone22;

	public ModelToad() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 11.58F, -5.464F);
		head.cubeList.add(new ModelBox(head, 0, 20, -4.46F, -5.558F, -6.0708F, 9, 5, 8, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.04F, -5.5151F, 1.8721F);
		head.addChild(bone4);
		setRotationAngle(bone4, -0.9163F, 0.0F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 42, -4.5F, -0.0449F, 0.0F, 9, 3, 4, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(-2.71F, -4.308F, -6.5708F);
		head.addChild(bone);
		setRotationAngle(bone, 0.0F, 0.0873F, 0.5672F);
		bone.cubeList.add(new ModelBox(bone, 13, 49, -2.29F, -0.5F, 0.25F, 4, 1, 5, 0.3F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(2.71F, -4.308F, -6.5708F);
		head.addChild(bone2);
		setRotationAngle(bone2, 0.0F, -0.0873F, -0.5672F);
		bone2.cubeList.add(new ModelBox(bone2, 13, 49, -1.71F, -0.5F, 0.25F, 4, 1, 5, 0.3F, true));
		bone2.cubeList.add(new ModelBox(bone2, 51, 35, -1.71F, -0.525F, 0.175F, 4, 1, 1, 0.3F, true));

		head_r1 = new ModelRenderer(this);
		head_r1.setRotationPoint(0.29F, -0.025F, 0.675F);
		bone2.addChild(head_r1);
		setRotationAngle(head_r1, 0.0436F, 0.0F, 3.1397F);
		head_r1.cubeList.add(new ModelBox(head_r1, 51, 35, -2.0F, -0.55F, -0.375F, 4, 1, 1, 0.3F, false));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.04F, -1.0003F, -1.4417F);
		head.addChild(jaw);
		setRotationAngle(jaw, 0.0436F, 0.0F, 0.0F);

		head_r2 = new ModelRenderer(this);
		head_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
		jaw.addChild(head_r2);
		setRotationAngle(head_r2, 0.0436F, 0.0F, 0.0F);
		head_r2.cubeList.add(new ModelBox(head_r2, 0, 33, -4.5F, 0.4203F, -4.6505F, 9, 2, 8, 0.0F, false));

		Pipe = new ModelRenderer(this);
		Pipe.setRotationPoint(4.1917F, -0.5133F, -4.8393F);
		head.addChild(Pipe);
		setRotationAngle(Pipe, 0.2618F, -0.8727F, 0.0F);
		Pipe.cubeList.add(new ModelBox(Pipe, 0, 4, -1.8662F, -2.0667F, -6.0098F, 2, 1, 2, 0.0F, false));
		Pipe.cubeList.add(new ModelBox(Pipe, 0, 0, -1.8662F, -0.8167F, -6.0098F, 2, 2, 2, 0.0F, false));
		Pipe.cubeList.add(new ModelBox(Pipe, 0, 7, -1.3662F, -1.3167F, -5.5098F, 1, 1, 1, 0.0F, false));
		Pipe.cubeList.add(new ModelBox(Pipe, 52, 52, -1.3662F, -0.3167F, -4.0098F, 1, 1, 5, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 11.58F, -5.464F);

		chest = new ModelRenderer(this);
		chest.setRotationPoint(0.25F, 1.32F, 0.464F);
		body.addChild(chest);
		setRotationAngle(chest, -0.7854F, 0.0F, 0.0F);

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(-0.2F, -1.5397F, 3.6327F);
		chest.addChild(bone6);
		setRotationAngle(bone6, -0.0873F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 0, 0, -6.0F, -5.7157F, -2.0345F, 12, 11, 9, 0.0F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(-0.2F, -1.5397F, 3.8827F);
		chest.addChild(bone11);
		bone11.cubeList.add(new ModelBox(bone11, 29, 28, -5.5F, -4.9657F, -6.7845F, 11, 6, 5, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 0.0343F, -7.4345F);
		bone11.addChild(bone5);
		setRotationAngle(bone5, 0.7854F, 0.0F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 33, 0, -5.5F, 1.0076F, -0.1743F, 11, 6, 3, 0.0F, false));

		bunda = new ModelRenderer(this);
		bunda.setRotationPoint(-0.242F, -6.0646F, 10.9442F);
		chest.addChild(bunda);
		setRotationAngle(bunda, -0.3927F, 0.0F, 0.0F);

		bunda_r1 = new ModelRenderer(this);
		bunda_r1.setRotationPoint(0.0F, 2.8378F, -0.9923F);
		bunda.addChild(bunda_r1);
		setRotationAngle(bunda_r1, -0.0873F, 0.0F, 0.0F);
		bunda_r1.cubeList.add(new ModelBox(bunda_r1, 30, 39, -5.5F, -3.3378F, 0.4923F, 11, 9, 4, -0.1F, false));

		armRight = new ModelRenderer(this);
		armRight.setRotationPoint(-5.05F, 10.9F, -2.91F);
		setRotationAngle(armRight, -0.5236F, 0.6109F, 0.3491F);
		armRight.cubeList.add(new ModelBox(armRight, 52, 9, -1.576F, -1.7778F, -1.4648F, 3, 8, 3, 0.2F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(-0.66F, 6.898F, -0.1F);
		armRight.addChild(bone7);
		setRotationAngle(bone7, 0.0F, 0.0F, -0.5236F);
		bone7.cubeList.add(new ModelBox(bone7, 40, 52, -0.712F, -0.9138F, -1.3328F, 3, 6, 3, 0.1F, false));

		handRight = new ModelRenderer(this);
		handRight.setRotationPoint(1.26F, 6.728F, -0.848F);
		bone7.addChild(handRight);
		setRotationAngle(handRight, 1.0472F, 0.2618F, 0.0F);

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(-1.604F, -0.0618F, 2.0292F);
		handRight.addChild(bone8);
		setRotationAngle(bone8, 0.0F, 0.3491F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(-0.604F, -0.0618F, 2.0292F);
		handRight.addChild(bone9);
		bone9.cubeList.add(new ModelBox(bone9, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.396F, -0.0618F, 2.0292F);
		handRight.addChild(bone10);
		setRotationAngle(bone10, 0.0F, -0.3491F, 0.0F);
		bone10.cubeList.add(new ModelBox(bone10, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, false));

		blade = new ModelRenderer(this);
		blade.setRotationPoint(-0.75F, 0.5F, 0.5F);
		handRight.addChild(blade);
		blade.cubeList.add(new ModelBox(blade, 0, 62, -4.25F, -0.5F, -0.5F, 8, 1, 1, 0.0F, false));
		blade.cubeList.add(new ModelBox(blade, 18, 61, 3.75F, 0.0F, -0.5F, 10, 0, 1, 0.02F, false));

		armLeft = new ModelRenderer(this);
		armLeft.setRotationPoint(5.05F, 10.9F, -2.91F);
		setRotationAngle(armLeft, -0.5236F, -0.6109F, -0.3491F);
		armLeft.cubeList.add(new ModelBox(armLeft, 52, 9, -1.424F, -1.7778F, -1.4648F, 3, 8, 3, 0.2F, true));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(0.66F, 6.898F, -0.1F);
		armLeft.addChild(bone14);
		setRotationAngle(bone14, 0.0F, 0.0F, 0.5236F);
		bone14.cubeList.add(new ModelBox(bone14, 40, 52, -2.288F, -0.9138F, -1.3328F, 3, 6, 3, 0.1F, true));

		handLeft = new ModelRenderer(this);
		handLeft.setRotationPoint(-1.26F, 6.728F, -0.848F);
		bone14.addChild(handLeft);
		setRotationAngle(handLeft, 1.0472F, -0.2618F, 0.0F);

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(1.604F, -0.0618F, 2.0292F);
		handLeft.addChild(bone15);
		setRotationAngle(bone15, 0.0F, -0.3491F, 0.0F);
		bone15.cubeList.add(new ModelBox(bone15, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, true));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(0.604F, -0.0618F, 2.0292F);
		handLeft.addChild(bone16);
		bone16.cubeList.add(new ModelBox(bone16, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, true));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(-0.396F, -0.0618F, 2.0292F);
		handLeft.addChild(bone17);
		setRotationAngle(bone17, 0.0F, 0.3491F, 0.0F);
		bone17.cubeList.add(new ModelBox(bone17, 16, 55, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.2F, true));

		legRight = new ModelRenderer(this);
		legRight.setRotationPoint(-5.677F, 19.8471F, 1.9223F);
		setRotationAngle(legRight, 0.2618F, 1.0472F, 0.0F);

		bone21 = new ModelRenderer(this);
		bone21.setRotationPoint(0.241F, 1.0282F, 0.8872F);
		legRight.addChild(bone21);
		setRotationAngle(bone21, -0.6981F, 0.0F, 0.0F);
		bone21.cubeList.add(new ModelBox(bone21, 32, 10, -2.901F, -1.6142F, -9.4876F, 5, 3, 10, 0.2F, false));

		legLowerRight = new ModelRenderer(this);
		legLowerRight.setRotationPoint(-0.0653F, -4.0517F, -5.8381F);
		legRight.addChild(legLowerRight);
		setRotationAngle(legLowerRight, -0.5236F, 0.0F, 0.0F);

		legLowerRight3_r1 = new ModelRenderer(this);
		legLowerRight3_r1.setRotationPoint(-0.1735F, 1.045F, -0.854F);
		legLowerRight.addChild(legLowerRight3_r1);
		setRotationAngle(legLowerRight3_r1, -0.7418F, 0.0F, 0.0F);
		legLowerRight3_r1.cubeList
				.add(new ModelBox(legLowerRight3_r1, 0, 49, -1.3772F, -3.0266F, -0.0999F, 3, 3, 7, 0.2F, false));

		footRight = new ModelRenderer(this);
		footRight.setRotationPoint(-0.0107F, 5.1235F, 4.6603F);
		legLowerRight.addChild(footRight);
		setRotationAngle(footRight, 0.2182F, 0.0F, 0.0F);

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(-0.896F, -0.0341F, -0.0512F);
		footRight.addChild(bone12);
		setRotationAngle(bone12, 0.0F, 0.3491F, 0.0F);
		bone12.cubeList.add(new ModelBox(bone12, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.104F, -0.0341F, -0.0512F);
		footRight.addChild(bone3);
		bone3.cubeList.add(new ModelBox(bone3, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(1.104F, -0.0341F, -0.0512F);
		footRight.addChild(bone13);
		setRotationAngle(bone13, 0.0F, -0.3491F, 0.0F);
		bone13.cubeList.add(new ModelBox(bone13, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));

		legLeft = new ModelRenderer(this);
		legLeft.setRotationPoint(5.677F, 19.8471F, 1.9223F);
		setRotationAngle(legLeft, 0.2618F, -1.0472F, 0.0F);

		bone18 = new ModelRenderer(this);
		bone18.setRotationPoint(-0.241F, 1.0282F, 0.8872F);
		legLeft.addChild(bone18);
		setRotationAngle(bone18, -0.6981F, 0.0F, 0.0F);
		bone18.cubeList.add(new ModelBox(bone18, 32, 10, -2.099F, -1.6142F, -9.4876F, 5, 3, 10, 0.2F, true));

		legLowerLeft = new ModelRenderer(this);
		legLowerLeft.setRotationPoint(0.0653F, -4.0517F, -5.8381F);
		legLeft.addChild(legLowerLeft);
		setRotationAngle(legLowerLeft, -0.5236F, 0.0F, 0.0F);

		legLowerRight4_r1 = new ModelRenderer(this);
		legLowerRight4_r1.setRotationPoint(0.1735F, 1.045F, -0.854F);
		legLowerLeft.addChild(legLowerRight4_r1);
		setRotationAngle(legLowerRight4_r1, -0.7418F, 0.0F, 0.0F);
		legLowerRight4_r1.cubeList
				.add(new ModelBox(legLowerRight4_r1, 0, 49, -1.6228F, -3.0266F, -0.0999F, 3, 3, 7, 0.2F, true));

		footLeft = new ModelRenderer(this);
		footLeft.setRotationPoint(0.0107F, 5.1235F, 4.6603F);
		legLowerLeft.addChild(footLeft);
		setRotationAngle(footLeft, 0.2182F, 0.0F, 0.0F);

		bone19 = new ModelRenderer(this);
		bone19.setRotationPoint(0.896F, -0.0341F, -0.0512F);
		footLeft.addChild(bone19);
		setRotationAngle(bone19, 0.0F, -0.3491F, 0.0F);
		bone19.cubeList.add(new ModelBox(bone19, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));

		bone20 = new ModelRenderer(this);
		bone20.setRotationPoint(-0.104F, -0.0341F, -0.0512F);
		footLeft.addChild(bone20);
		bone20.cubeList.add(new ModelBox(bone20, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));

		bone22 = new ModelRenderer(this);
		bone22.setRotationPoint(-1.104F, -0.0341F, -0.0512F);
		footLeft.addChild(bone22);
		setRotationAngle(bone22, 0.0F, 0.3491F, 0.0F);
		bone22.cubeList.add(new ModelBox(bone22, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		body.render(f5);
		armRight.render(f5);
		armLeft.render(f5);
		legRight.render(f5);
		legLeft.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
		this.head.rotateAngleY = f3 / (180F / (float) Math.PI);
		this.head.rotateAngleX = f4 / (180F / (float) Math.PI);
	}
}