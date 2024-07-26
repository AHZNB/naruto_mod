// Made with Blockbench 4.10.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelToadSkinny extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer hairFukasaku;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer hairShima;
	private final ModelRenderer neck;
	private final ModelRenderer browRight;
	private final ModelRenderer browRight2;
	private final ModelRenderer browLeft;
	private final ModelRenderer browLeft2;
	private final ModelRenderer jaw;
	private final ModelRenderer goatee;
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
	private final ModelRenderer bone12;
	private final ModelRenderer bone9;
	private final ModelRenderer bone13;
	private final ModelRenderer armLeft;
	private final ModelRenderer forearmLeft;
	private final ModelRenderer handLeft;
	private final ModelRenderer bone15;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer legRight;
	private final ModelRenderer thighRight;
	private final ModelRenderer legLowerRight;
	private final ModelRenderer legLowerRight3_r1;
	private final ModelRenderer footRight;
	private final ModelRenderer bone2;
	private final ModelRenderer bone7;
	private final ModelRenderer bone3;
	private final ModelRenderer bone14;
	private final ModelRenderer bone8;
	private final ModelRenderer bone10;
	private final ModelRenderer legLeft;
	private final ModelRenderer thighLeft;
	private final ModelRenderer legLowerLeft;
	private final ModelRenderer legLowerLeft3_r1;
	private final ModelRenderer footLeft;
	private final ModelRenderer bone19;
	private final ModelRenderer bone20;
	private final ModelRenderer bone22;
	private final ModelRenderer bone23;
	private final ModelRenderer bone24;
	private final ModelRenderer bone25;

	public ModelToadSkinny() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 9.58F, -2.464F);
		head.cubeList.add(new ModelBox(head, 2, 21, -4.5F, -4.58F, -6.036F, 9, 5, 6, 0.0F, false));

		hairFukasaku = new ModelRenderer(this);
		hairFukasaku.setRotationPoint(0.0F, -4.4781F, -1.4783F);
		head.addChild(hairFukasaku);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, 1.25F, -0.25F);
		hairFukasaku.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0436F, 0.0F, 0.1309F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 42, 51, -2.0F, -4.249F, -3.5577F, 4, 6, 7, -0.01F, true));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, 1.0467F, -1.5833F);
		hairFukasaku.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.2618F, 0.0F, -0.0873F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 42, 51, -2.0F, -3.9659F, -2.4743F, 4, 6, 7, 0.01F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, 0.25F, 0.0F);
		hairFukasaku.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.2618F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 42, 51, -2.0F, -4.3F, -3.8077F, 4, 6, 7, -0.8F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, 0.0F, 0.15F);
		hairFukasaku.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.1309F, 0.0F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 42, 51, -2.0F, -3.0F, -3.4577F, 4, 6, 7, 0.0F, false));

		hairShima = new ModelRenderer(this);
		hairShima.setRotationPoint(0.0F, -5.4781F, -1.7283F);
		head.addChild(hairShima);
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.05F, 0.8981F, -5.0577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -1.95F, 0.8981F, -5.0577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -3.95F, 0.8981F, -4.8077F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.05F, 0.8981F, -4.8077F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.8F, 1.1481F, -4.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 3.05F, 1.1481F, -2.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 3.05F, 1.3981F, -0.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.8F, 1.6481F, 1.4423F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.8F, 1.3981F, 1.6923F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -0.95F, 1.6481F, 1.9423F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -2.95F, 1.3981F, 1.6923F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.7F, 1.6481F, 1.4423F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.95F, 1.3981F, -0.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.95F, 1.1481F, -2.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.7F, 1.1481F, -4.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -3.95F, -0.1019F, -4.3077F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.2F, -0.6019F, -2.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -2.45F, -1.1019F, -2.8077F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.55F, -1.1019F, -2.8077F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.8F, -1.1019F, -0.8077F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.05F, -0.3519F, 1.1923F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -2.7F, -1.1019F, -0.8077F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -1.95F, -0.3519F, 1.1923F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -0.95F, -1.1019F, -3.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -0.95F, -1.6019F, -2.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -0.95F, -1.6019F, -0.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.05F, -1.1019F, 0.6923F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -1.95F, -1.1019F, 0.6923F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.2F, -0.6019F, -0.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -1.95F, -0.1019F, -4.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.05F, -0.1019F, -4.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.05F, -0.1019F, -4.3077F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.3F, -0.6019F, -2.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.3F, -0.6019F, -0.5577F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.05F, 0.1481F, 0.9423F, 2, 2, 2, 0.0F, false));
		hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -3.95F, 0.1481F, 0.9423F, 2, 2, 2, 0.0F, false));

		neck = new ModelRenderer(this);
		neck.setRotationPoint(0.0F, -4.58F, -0.1279F);
		head.addChild(neck);
		setRotationAngle(neck, -0.9163F, 0.0F, 0.0F);
		neck.cubeList.add(new ModelBox(neck, 27, 0, -4.5F, 0.0F, -0.0081F, 9, 4, 5, -0.03F, false));

		browRight = new ModelRenderer(this);
		browRight.setRotationPoint(-2.71F, -3.308F, -6.5708F);
		head.addChild(browRight);
		setRotationAngle(browRight, 0.0F, 0.0873F, 0.5672F);
		browRight.cubeList.add(new ModelBox(browRight, 43, 14, -2.29F, -0.5F, 0.25F, 4, 1, 4, 0.3F, false));

		browRight2 = new ModelRenderer(this);
		browRight2.setRotationPoint(2.0F, 0.0F, 0.0F);
		browRight.addChild(browRight2);
		setRotationAngle(browRight2, 0.0F, 0.0F, -0.2182F);
		browRight2.cubeList.add(new ModelBox(browRight2, 33, 52, -4.39F, -0.5F, 0.25F, 4, 1, 4, 0.3F, false));

		browLeft = new ModelRenderer(this);
		browLeft.setRotationPoint(2.71F, -3.308F, -6.5708F);
		head.addChild(browLeft);
		setRotationAngle(browLeft, 0.0F, -0.0873F, -0.5672F);
		browLeft.cubeList.add(new ModelBox(browLeft, 43, 14, -1.71F, -0.5F, 0.25F, 4, 1, 4, 0.3F, true));

		browLeft2 = new ModelRenderer(this);
		browLeft2.setRotationPoint(-2.0F, 0.0F, 0.0F);
		browLeft.addChild(browLeft2);
		setRotationAngle(browLeft2, 0.0F, 0.0F, 0.2182F);
		browLeft2.cubeList.add(new ModelBox(browLeft2, 33, 52, 0.39F, -0.5F, 0.25F, 4, 1, 4, 0.3F, true));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.04F, 0.4997F, -1.1917F);
		head.addChild(jaw);
		setRotationAngle(jaw, 0.0436F, 0.0F, 0.0F);
		jaw.cubeList.add(new ModelBox(jaw, 28, 26, -4.54F, -0.0901F, -4.8784F, 9, 2, 6, 0.0F, false));

		goatee = new ModelRenderer(this);
		goatee.setRotationPoint(0.0F, 1.0F, -4.9F);
		jaw.addChild(goatee);
		setRotationAngle(goatee, -0.0873F, 0.0F, 0.0F);
		goatee.cubeList.add(new ModelBox(goatee, 42, 9, -2.04F, 0.0F, 0.0F, 4, 4, 0, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 9.58F, -2.464F);
		setRotationAngle(body, -0.2618F, 0.0F, 0.0F);

		chest = new ModelRenderer(this);
		chest.setRotationPoint(0.25F, 3.7695F, -0.9502F);
		body.addChild(chest);
		setRotationAngle(chest, -0.7854F, 0.0F, 0.0F);

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(-0.2F, -7.5397F, 2.1327F);
		chest.addChild(bone6);
		setRotationAngle(bone6, -0.2182F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 0, 0, -4.55F, 0.1397F, -0.0173F, 9, 9, 9, 0.0F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(-0.2F, -1.5397F, 3.8827F);
		chest.addChild(bone11);

		chest_r1 = new ModelRenderer(this);
		chest_r1.setRotationPoint(0.0F, -5.9657F, -1.8845F);
		bone11.addChild(chest_r1);
		setRotationAngle(chest_r1, 0.2443F, 0.0F, 0.0F);
		chest_r1.cubeList.add(new ModelBox(chest_r1, 26, 34, -4.55F, 0.1154F, -4.8982F, 9, 6, 5, -0.01F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 1.1343F, -5.1845F);
		bone11.addChild(bone5);
		setRotationAngle(bone5, 0.4363F, 0.0F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 0, 54, -4.5F, -0.7806F, 0.5558F, 9, 3, 3, 0.0F, false));

		bunda = new ModelRenderer(this);
		bunda.setRotationPoint(-0.242F, -5.4646F, 10.7942F);
		chest.addChild(bunda);
		setRotationAngle(bunda, -0.6545F, 0.0F, 0.0F);
		bunda.cubeList.add(new ModelBox(bunda, 0, 32, -4.508F, 0.15F, -0.1F, 9, 8, 4, -0.1F, false));

		armRight = new ModelRenderer(this);
		armRight.setRotationPoint(-5.05F, 3.7695F, 1.1398F);
		body.addChild(armRight);
		setRotationAngle(armRight, -0.2182F, 0.0F, 0.2618F);
		armRight.cubeList.add(new ModelBox(armRight, 56, 0, -1.076F, -1.7778F, -0.9648F, 2, 8, 2, 0.2F, false));

		forearmRight = new ModelRenderer(this);
		forearmRight.setRotationPoint(-0.66F, 6.898F, -0.1F);
		armRight.addChild(forearmRight);
		setRotationAngle(forearmRight, 0.0F, 0.0F, -0.5236F);
		forearmRight.cubeList.add(new ModelBox(forearmRight, 0, 0, -0.212F, -0.9138F, -0.8328F, 2, 6, 2, 0.1F, false));

		handRight = new ModelRenderer(this);
		handRight.setRotationPoint(1.01F, 5.478F, 0.152F);
		forearmRight.addChild(handRight);
		setRotationAngle(handRight, 0.5236F, 0.2618F, 0.0F);

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(-0.8537F, -0.2173F, 0.496F);
		handRight.addChild(bone12);
		setRotationAngle(bone12, 0.0F, 0.2618F, 0.0F);
		bone12.cubeList.add(new ModelBox(bone12, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(-0.1037F, -0.2173F, 0.496F);
		handRight.addChild(bone9);
		bone9.cubeList.add(new ModelBox(bone9, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(0.6463F, -0.2173F, 0.496F);
		handRight.addChild(bone13);
		setRotationAngle(bone13, 0.0F, -0.2618F, 0.0F);
		bone13.cubeList.add(new ModelBox(bone13, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, false));

		armLeft = new ModelRenderer(this);
		armLeft.setRotationPoint(5.05F, 3.7695F, 1.1398F);
		body.addChild(armLeft);
		setRotationAngle(armLeft, -0.2182F, 0.0F, -0.2618F);
		armLeft.cubeList.add(new ModelBox(armLeft, 56, 0, -0.924F, -1.7778F, -0.9648F, 2, 8, 2, 0.2F, true));

		forearmLeft = new ModelRenderer(this);
		forearmLeft.setRotationPoint(0.66F, 6.898F, -0.1F);
		armLeft.addChild(forearmLeft);
		setRotationAngle(forearmLeft, 0.0F, 0.0F, 0.5236F);
		forearmLeft.cubeList.add(new ModelBox(forearmLeft, 0, 0, -1.788F, -0.9138F, -0.8328F, 2, 6, 2, 0.1F, true));

		handLeft = new ModelRenderer(this);
		handLeft.setRotationPoint(-1.01F, 5.478F, 0.152F);
		forearmLeft.addChild(handLeft);
		setRotationAngle(handLeft, 0.5236F, -0.2618F, 0.0F);

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(0.8537F, -0.2173F, 0.496F);
		handLeft.addChild(bone15);
		setRotationAngle(bone15, 0.0F, -0.2618F, 0.0F);
		bone15.cubeList.add(new ModelBox(bone15, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, true));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(0.1037F, -0.2173F, 0.496F);
		handLeft.addChild(bone16);
		bone16.cubeList.add(new ModelBox(bone16, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, true));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(-0.6463F, -0.2173F, 0.496F);
		handLeft.addChild(bone17);
		setRotationAngle(bone17, 0.0F, 0.2618F, 0.0F);
		bone17.cubeList.add(new ModelBox(bone17, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, true));

		legRight = new ModelRenderer(this);
		legRight.setRotationPoint(-4.677F, 19.8471F, 0.9223F);
		setRotationAngle(legRight, 0.2618F, 0.5236F, 0.0F);

		thighRight = new ModelRenderer(this);
		thighRight.setRotationPoint(0.241F, 1.0282F, 0.8872F);
		legRight.addChild(thighRight);
		setRotationAngle(thighRight, -0.6981F, 0.0F, 0.0F);
		thighRight.cubeList.add(new ModelBox(thighRight, 26, 9, -1.901F, -1.6142F, -9.4876F, 3, 3, 10, 0.2F, false));

		legLowerRight = new ModelRenderer(this);
		legLowerRight.setRotationPoint(-0.0653F, -4.0517F, -5.8381F);
		legRight.addChild(legLowerRight);
		setRotationAngle(legLowerRight, -0.5236F, 0.0F, 0.0F);

		legLowerRight3_r1 = new ModelRenderer(this);
		legLowerRight3_r1.setRotationPoint(-0.1735F, 1.045F, -0.854F);
		legLowerRight.addChild(legLowerRight3_r1);
		setRotationAngle(legLowerRight3_r1, -0.7418F, 0.0F, 0.0F);
		legLowerRight3_r1.cubeList
				.add(new ModelBox(legLowerRight3_r1, 0, 45, -0.8772F, -2.0266F, -0.0999F, 2, 2, 7, 0.2F, false));

		footRight = new ModelRenderer(this);
		footRight.setRotationPoint(-0.0107F, 5.1235F, 4.6603F);
		legLowerRight.addChild(footRight);
		setRotationAngle(footRight, 0.2182F, 0.0F, 0.0F);

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-0.396F, -0.0341F, -0.0512F);
		footRight.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.1745F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, -0.25F, -4.0F);
		bone2.addChild(bone7);
		setRotationAngle(bone7, 0.2618F, 0.0F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.104F, -0.0341F, -0.0512F);
		footRight.addChild(bone3);
		bone3.cubeList.add(new ModelBox(bone3, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(0.0F, -0.25F, -4.0F);
		bone3.addChild(bone14);
		setRotationAngle(bone14, 0.2618F, 0.0F, 0.0F);
		bone14.cubeList.add(new ModelBox(bone14, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.604F, -0.0341F, -0.0512F);
		footRight.addChild(bone8);
		setRotationAngle(bone8, 0.0F, -0.1745F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.0F, -0.25F, -4.0F);
		bone8.addChild(bone10);
		setRotationAngle(bone10, 0.2618F, 0.0F, 0.0F);
		bone10.cubeList.add(new ModelBox(bone10, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, false));

		legLeft = new ModelRenderer(this);
		legLeft.setRotationPoint(4.677F, 19.8471F, 0.9223F);
		setRotationAngle(legLeft, 0.2618F, -0.5236F, 0.0F);

		thighLeft = new ModelRenderer(this);
		thighLeft.setRotationPoint(-0.241F, 1.0282F, 0.8872F);
		legLeft.addChild(thighLeft);
		setRotationAngle(thighLeft, -0.6981F, 0.0F, 0.0F);
		thighLeft.cubeList.add(new ModelBox(thighLeft, 26, 9, -1.099F, -1.6142F, -9.4876F, 3, 3, 10, 0.2F, true));

		legLowerLeft = new ModelRenderer(this);
		legLowerLeft.setRotationPoint(0.0653F, -4.0517F, -5.8381F);
		legLeft.addChild(legLowerLeft);
		setRotationAngle(legLowerLeft, -0.5236F, 0.0F, 0.0F);

		legLowerLeft3_r1 = new ModelRenderer(this);
		legLowerLeft3_r1.setRotationPoint(0.1735F, 1.045F, -0.854F);
		legLowerLeft.addChild(legLowerLeft3_r1);
		setRotationAngle(legLowerLeft3_r1, -0.7418F, 0.0F, 0.0F);
		legLowerLeft3_r1.cubeList
				.add(new ModelBox(legLowerLeft3_r1, 0, 45, -1.1228F, -2.0266F, -0.0999F, 2, 2, 7, 0.2F, true));

		footLeft = new ModelRenderer(this);
		footLeft.setRotationPoint(0.0107F, 5.1235F, 4.6603F);
		legLowerLeft.addChild(footLeft);
		setRotationAngle(footLeft, 0.2182F, 0.0F, 0.0F);

		bone19 = new ModelRenderer(this);
		bone19.setRotationPoint(0.396F, -0.0341F, -0.0512F);
		footLeft.addChild(bone19);
		setRotationAngle(bone19, 0.0F, -0.1745F, 0.0F);
		bone19.cubeList.add(new ModelBox(bone19, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));

		bone20 = new ModelRenderer(this);
		bone20.setRotationPoint(0.0F, -0.25F, -4.0F);
		bone19.addChild(bone20);
		setRotationAngle(bone20, 0.2618F, 0.0F, 0.0F);
		bone20.cubeList.add(new ModelBox(bone20, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, true));

		bone22 = new ModelRenderer(this);
		bone22.setRotationPoint(-0.104F, -0.0341F, -0.0512F);
		footLeft.addChild(bone22);
		bone22.cubeList.add(new ModelBox(bone22, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));

		bone23 = new ModelRenderer(this);
		bone23.setRotationPoint(0.0F, -0.25F, -4.0F);
		bone22.addChild(bone23);
		setRotationAngle(bone23, 0.2618F, 0.0F, 0.0F);
		bone23.cubeList.add(new ModelBox(bone23, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, true));

		bone24 = new ModelRenderer(this);
		bone24.setRotationPoint(-0.604F, -0.0341F, -0.0512F);
		footLeft.addChild(bone24);
		setRotationAngle(bone24, 0.0F, 0.1745F, 0.0F);
		bone24.cubeList.add(new ModelBox(bone24, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));

		bone25 = new ModelRenderer(this);
		bone25.setRotationPoint(0.0F, -0.25F, -4.0F);
		bone24.addChild(bone25);
		setRotationAngle(bone25, 0.2618F, 0.0F, 0.0F);
		bone25.cubeList.add(new ModelBox(bone25, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, true));
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