// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelToadSamurai extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer neck;
	private final ModelRenderer browRight;
	private final ModelRenderer browLeft;
	private final ModelRenderer jaw;
	private final ModelRenderer body;
	private final ModelRenderer chest;
	private final ModelRenderer bone6;
	private final ModelRenderer bone11;
	private final ModelRenderer chest_r1;
	private final ModelRenderer bone5;
	private final ModelRenderer bunda;
	private final ModelRenderer armRight;
	private final ModelRenderer forearmRight;
	private final ModelRenderer handRight;
	private final ModelRenderer bone23;
	private final ModelRenderer bone9;
	private final ModelRenderer bone10;
	private final ModelRenderer bone24;
	private final ModelRenderer blade;
	private final ModelRenderer armLeft;
	private final ModelRenderer forearmLeft;
	private final ModelRenderer handLeft;
	private final ModelRenderer bone15;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer bone7;
	private final ModelRenderer legRight;
	private final ModelRenderer thighRight;
	private final ModelRenderer legLowerRight;
	private final ModelRenderer legLowerRight3_r1;
	private final ModelRenderer footRight;
	private final ModelRenderer bone12;
	private final ModelRenderer bone3;
	private final ModelRenderer bone13;
	private final ModelRenderer legLeft;
	private final ModelRenderer thighLeft;
	private final ModelRenderer legLowerLeft;
	private final ModelRenderer legLowerRight4_r1;
	private final ModelRenderer footLeft;
	private final ModelRenderer bone19;
	private final ModelRenderer bone20;
	private final ModelRenderer bone22;

	public ModelToadSamurai() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 9.58F, -2.464F);
		head.cubeList.add(new ModelBox(head, 0, 20, -4.46F, -5.558F, -6.0708F, 9, 5, 8, 0.0F, false));

		neck = new ModelRenderer(this);
		neck.setRotationPoint(0.04F, -5.5151F, 1.8721F);
		head.addChild(neck);
		setRotationAngle(neck, -0.9163F, 0.0F, 0.0F);
		neck.cubeList.add(new ModelBox(neck, 0, 42, -4.5F, -0.0449F, 0.0F, 9, 3, 4, 0.0F, false));

		browRight = new ModelRenderer(this);
		browRight.setRotationPoint(-2.71F, -4.308F, -6.5708F);
		head.addChild(browRight);
		setRotationAngle(browRight, 0.0F, 0.0873F, 0.5672F);
		browRight.cubeList.add(new ModelBox(browRight, 13, 49, -2.29F, -0.5F, 0.25F, 4, 1, 5, 0.3F, false));

		browLeft = new ModelRenderer(this);
		browLeft.setRotationPoint(2.71F, -4.308F, -6.5708F);
		head.addChild(browLeft);
		setRotationAngle(browLeft, 0.0F, -0.0873F, -0.5672F);
		browLeft.cubeList.add(new ModelBox(browLeft, 13, 49, -1.71F, -0.5F, 0.25F, 4, 1, 5, 0.3F, true));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.04F, -0.5003F, -1.1917F);
		head.addChild(jaw);
		setRotationAngle(jaw, 0.0873F, 0.0F, 0.0F);
		jaw.cubeList.add(new ModelBox(jaw, 0, 33, -4.5F, -0.0901F, -4.8784F, 9, 2, 8, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 9.58F, -2.464F);

		chest = new ModelRenderer(this);
		chest.setRotationPoint(0.25F, 1.32F, -0.536F);
		body.addChild(chest);
		setRotationAngle(chest, -1.309F, 0.0F, 0.0F);

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(-0.2F, -1.5397F, 3.6327F);
		chest.addChild(bone6);
		setRotationAngle(bone6, -0.0873F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 0, 0, -6.0F, -5.7157F, -2.0345F, 12, 11, 9, 0.0F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(-0.2F, -1.5397F, 3.8827F);
		chest.addChild(bone11);

		chest_r1 = new ModelRenderer(this);
		chest_r1.setRotationPoint(0.0F, -5.9657F, -1.8845F);
		bone11.addChild(chest_r1);
		setRotationAngle(chest_r1, 0.2443F, 0.0F, 0.0F);
		chest_r1.cubeList.add(new ModelBox(chest_r1, 29, 28, -5.5F, 0.1254F, -4.9F, 11, 6, 5, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 0.0343F, -5.6845F);
		bone11.addChild(bone5);
		setRotationAngle(bone5, 0.5323F, 0.0F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 33, 0, -5.5F, 0.0076F, -0.0243F, 11, 6, 3, 0.0F, false));

		bunda = new ModelRenderer(this);
		bunda.setRotationPoint(-0.242F, -6.0646F, 9.9442F);
		chest.addChild(bunda);
		setRotationAngle(bunda, -0.2618F, 0.0F, 0.0F);
		bunda.cubeList.add(new ModelBox(bunda, 30, 39, -5.5F, -0.5F, -0.5F, 11, 9, 4, -0.1F, false));

		armRight = new ModelRenderer(this);
		armRight.setRotationPoint(-5.05F, 1.32F, 1.554F);
		body.addChild(armRight);
		setRotationAngle(armRight, -0.7854F, 0.5236F, 0.3491F);
		armRight.cubeList.add(new ModelBox(armRight, 52, 9, -1.576F, -1.7778F, -1.4648F, 3, 8, 3, 0.2F, false));

		forearmRight = new ModelRenderer(this);
		forearmRight.setRotationPoint(-1.572F, 5.7342F, 0.0672F);
		armRight.addChild(forearmRight);
		setRotationAngle(forearmRight, -0.6981F, 0.0F, -0.6545F);
		forearmRight.cubeList.add(new ModelBox(forearmRight, 40, 52, 0.0F, 0.25F, -1.5F, 3, 6, 3, 0.1F, false));

		handRight = new ModelRenderer(this);
		handRight.setRotationPoint(0.472F, 9.1418F, -1.0152F);
		forearmRight.addChild(handRight);
		setRotationAngle(handRight, 1.6144F, 1.4835F, 0.0F);
		handRight.cubeList.add(new ModelBox(handRight, 26, 24, -3.0F, 0.0F, 0.25F, 4, 1, 3, 0.0F, true));

		bone23 = new ModelRenderer(this);
		bone23.setRotationPoint(0.396F, -0.0618F, 0.5292F);
		handRight.addChild(bone23);
		setRotationAngle(bone23, 1.5708F, 0.0F, 0.0F);
		bone23.cubeList.add(new ModelBox(bone23, 19, 57, -0.5F, -1.0F, -2.0F, 1, 1, 2, 0.0F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(-1.004F, -0.0618F, 0.5292F);
		handRight.addChild(bone9);
		setRotationAngle(bone9, 1.5708F, 0.0F, 0.0F);
		bone9.cubeList.add(new ModelBox(bone9, 19, 57, -0.5F, -1.0F, -2.0F, 1, 1, 2, 0.0F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(-2.354F, -0.0618F, 0.5292F);
		handRight.addChild(bone10);
		setRotationAngle(bone10, 1.5708F, 0.0F, 0.0F);
		bone10.cubeList.add(new ModelBox(bone10, 19, 57, -0.5F, -1.0F, -2.0F, 1, 1, 2, 0.0F, false));

		bone24 = new ModelRenderer(this);
		bone24.setRotationPoint(-0.604F, 0.4382F, 2.5292F);
		handRight.addChild(bone24);
		setRotationAngle(bone24, 0.0F, -1.0472F, 0.7854F);
		bone24.cubeList.add(new ModelBox(bone24, 17, 55, -0.5F, -0.4995F, -3.7282F, 1, 1, 4, 0.0F, false));

		blade = new ModelRenderer(this);
		blade.setRotationPoint(-0.2997F, 1.4445F, 0.9669F);
		handRight.addChild(blade);
		setRotationAngle(blade, -1.5708F, 0.0F, 0.0F);
		blade.cubeList.add(new ModelBox(blade, 0, 62, -4.25F, -0.5F, -0.5F, 8, 1, 1, 0.0F, false));
		blade.cubeList.add(new ModelBox(blade, 18, 61, 3.75F, 0.0F, -0.5F, 10, 0, 1, 0.02F, false));
		blade.cubeList.add(new ModelBox(blade, 18, 62, 3.5F, 0.0F, -0.5F, 1, 0, 1, 0.02F, false));

		armLeft = new ModelRenderer(this);
		armLeft.setRotationPoint(5.05F, 1.32F, 1.554F);
		body.addChild(armLeft);
		setRotationAngle(armLeft, -1.0472F, -0.2182F, -0.3491F);
		armLeft.cubeList.add(new ModelBox(armLeft, 52, 9, -1.424F, -1.7778F, -1.4648F, 3, 8, 3, 0.2F, true));

		forearmLeft = new ModelRenderer(this);
		forearmLeft.setRotationPoint(1.572F, 5.7342F, 0.0672F);
		armLeft.addChild(forearmLeft);
		setRotationAngle(forearmLeft, -0.5236F, 0.0F, 0.2618F);
		forearmLeft.cubeList.add(new ModelBox(forearmLeft, 40, 52, -3.0F, 0.25F, -1.5F, 3, 6, 3, 0.1F, true));

		handLeft = new ModelRenderer(this);
		handLeft.setRotationPoint(-1.972F, 6.3918F, -2.0152F);
		forearmLeft.addChild(handLeft);
		setRotationAngle(handLeft, 0.0F, -0.1745F, 0.0F);
		handLeft.cubeList.add(new ModelBox(handLeft, 47, 59, -3.028F, 0.6082F, -1.9848F, 6, 0, 5, 0.0F, false));
		handLeft.cubeList.add(new ModelBox(handLeft, 26, 24, -1.0F, 0.0F, 0.25F, 4, 1, 3, 0.0F, false));

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(1.604F, -0.0618F, 2.0292F);
		handLeft.addChild(bone15);
		setRotationAngle(bone15, 0.0436F, -0.3491F, 0.0F);
		bone15.cubeList.add(new ModelBox(bone15, 17, 55, 0.0F, 0.0F, -3.75F, 1, 1, 4, 0.0F, true));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(0.604F, -0.0618F, 2.0292F);
		handLeft.addChild(bone16);
		setRotationAngle(bone16, 0.0436F, 0.0F, 0.0F);
		bone16.cubeList.add(new ModelBox(bone16, 17, 55, 0.0F, 0.0F, -3.75F, 1, 1, 4, 0.0F, true));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(-0.396F, -0.0618F, 2.2792F);
		handLeft.addChild(bone17);
		setRotationAngle(bone17, 0.0436F, 0.3491F, 0.0F);
		bone17.cubeList.add(new ModelBox(bone17, 17, 55, 0.0F, 0.0F, -3.75F, 1, 1, 4, 0.0F, true));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.604F, -0.0618F, 3.0292F);
		handLeft.addChild(bone7);
		setRotationAngle(bone7, 0.0436F, 1.5708F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 17, 55, 0.0F, 0.0F, -3.75F, 1, 1, 4, 0.0F, true));

		legRight = new ModelRenderer(this);
		legRight.setRotationPoint(-5.677F, 19.8471F, 1.9223F);
		setRotationAngle(legRight, 0.2618F, 0.5236F, 0.0F);

		thighRight = new ModelRenderer(this);
		thighRight.setRotationPoint(0.241F, 1.0282F, 0.8872F);
		legRight.addChild(thighRight);
		setRotationAngle(thighRight, -0.6981F, 0.0F, 0.0F);
		thighRight.cubeList.add(new ModelBox(thighRight, 32, 10, -2.901F, -1.6142F, -9.4876F, 5, 3, 10, 0.2F, false));

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
		setRotationAngle(legLeft, 0.2618F, -0.5236F, 0.0F);

		thighLeft = new ModelRenderer(this);
		thighLeft.setRotationPoint(-0.241F, 1.0282F, 0.8872F);
		legLeft.addChild(thighLeft);
		setRotationAngle(thighLeft, -0.6981F, 0.0F, 0.0F);
		thighLeft.cubeList.add(new ModelBox(thighLeft, 32, 10, -2.099F, -1.6142F, -9.4876F, 5, 3, 10, 0.2F, true));

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
	}
}