// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelNineTails extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer snout;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r13;
	private final ModelRenderer jaw;
	private final ModelRenderer earRight;
	private final ModelRenderer cube_r5;
	private final ModelRenderer earLeft;
	private final ModelRenderer cube_r4;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer bone;
	private final ModelRenderer bipedBody;
	private final ModelRenderer waist;
	private final ModelRenderer chest;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer upperArmRight;
	private final ModelRenderer lowerArmRight;
	private final ModelRenderer rightHand;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer bone11;
	private final ModelRenderer bone12;
	private final ModelRenderer bone14;
	private final ModelRenderer bone15;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;
	private final ModelRenderer bone9;
	private final ModelRenderer bone10;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer upperArmLeft;
	private final ModelRenderer lowerArmLeft;
	private final ModelRenderer leftHand;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer bone13;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer bone18;
	private final ModelRenderer bone19;
	private final ModelRenderer bone20;
	private final ModelRenderer bone21;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer upperLegRight;
	private final ModelRenderer lowerLegRight;
	private final ModelRenderer rightFoot;
	private final ModelRenderer bone25;
	private final ModelRenderer bone26;
	private final ModelRenderer bone27;
	private final ModelRenderer bone28;
	private final ModelRenderer bone29;
	private final ModelRenderer bone30;
	private final ModelRenderer bone31;
	private final ModelRenderer bone32;
	private final ModelRenderer bone33;
	private final ModelRenderer bone34;
	private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer upperLegLeft;
	private final ModelRenderer lowerLegLeft;
	private final ModelRenderer leftFoot;
	private final ModelRenderer bone22;
	private final ModelRenderer bone23;
	private final ModelRenderer bone24;
	private final ModelRenderer bone35;
	private final ModelRenderer bone36;
	private final ModelRenderer bone37;
	private final ModelRenderer bone38;
	private final ModelRenderer bone39;
	private final ModelRenderer bone40;
	private final ModelRenderer bone41;
	private final ModelRenderer Tail1_0;
	private final ModelRenderer Tail1_1;
	private final ModelRenderer Tail1_2;
	private final ModelRenderer Tail1_3;
	private final ModelRenderer Tail1_4;
	private final ModelRenderer Tail1_5;
	private final ModelRenderer Tail1_6;
	private final ModelRenderer Tail1_7;
	private final ModelRenderer Tail2_0;
	private final ModelRenderer Tail2_1;
	private final ModelRenderer Tail2_2;
	private final ModelRenderer Tail2_3;
	private final ModelRenderer Tail2_4;
	private final ModelRenderer Tail2_5;
	private final ModelRenderer Tail2_6;
	private final ModelRenderer Tail2_7;
	private final ModelRenderer Tail3_0;
	private final ModelRenderer Tail3_1;
	private final ModelRenderer Tail3_2;
	private final ModelRenderer Tail3_3;
	private final ModelRenderer Tail3_4;
	private final ModelRenderer Tail3_5;
	private final ModelRenderer Tail3_6;
	private final ModelRenderer Tail3_7;
	private final ModelRenderer Tail4_0;
	private final ModelRenderer Tail4_1;
	private final ModelRenderer Tail4_2;
	private final ModelRenderer Tail4_3;
	private final ModelRenderer Tail4_4;
	private final ModelRenderer Tail4_5;
	private final ModelRenderer Tail4_6;
	private final ModelRenderer Tail4_7;
	private final ModelRenderer Tail5_0;
	private final ModelRenderer Tail5_1;
	private final ModelRenderer Tail5_2;
	private final ModelRenderer Tail5_3;
	private final ModelRenderer Tail5_4;
	private final ModelRenderer Tail5_5;
	private final ModelRenderer Tail5_6;
	private final ModelRenderer Tail5_7;
	private final ModelRenderer Tail6_0;
	private final ModelRenderer Tail6_1;
	private final ModelRenderer Tail6_2;
	private final ModelRenderer Tail6_3;
	private final ModelRenderer Tail6_4;
	private final ModelRenderer Tail6_5;
	private final ModelRenderer Tail6_6;
	private final ModelRenderer Tail6_7;
	private final ModelRenderer Tail7_0;
	private final ModelRenderer Tail7_1;
	private final ModelRenderer Tail7_2;
	private final ModelRenderer Tail7_3;
	private final ModelRenderer Tail7_4;
	private final ModelRenderer Tail7_5;
	private final ModelRenderer Tail7_6;
	private final ModelRenderer Tail7_7;
	private final ModelRenderer Tail8_0;
	private final ModelRenderer Tail8_1;
	private final ModelRenderer Tail8_2;
	private final ModelRenderer Tail8_3;
	private final ModelRenderer Tail8_4;
	private final ModelRenderer Tail8_5;
	private final ModelRenderer Tail8_6;
	private final ModelRenderer Tail8_7;
	private final ModelRenderer Tail9_0;
	private final ModelRenderer Tail9_1;
	private final ModelRenderer Tail9_2;
	private final ModelRenderer Tail9_3;
	private final ModelRenderer Tail9_4;
	private final ModelRenderer Tail9_5;
	private final ModelRenderer Tail9_6;
	private final ModelRenderer Tail9_7;

	public ModelNineTails() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, -1.0F, -8.0F);
		setRotationAngle(bipedHead, 0.2618F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 3, 0.0F, false));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 0, -3.0F, -7.5F, -1.0F, 6, 8, 3, 0.0F, false));

		snout = new ModelRenderer(this);
		snout.setRotationPoint(0.0F, -1.0F, 2.0F);
		bipedHead.addChild(snout);

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(-2.0F, -2.5176F, -10.5681F);
		snout.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, -0.1745F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 12, 0.0F, -0.4483F, -0.3093F, 2, 3, 6, 0.0F, true));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(2.0F, -2.5176F, -10.5681F);
		snout.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.1745F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 12, -2.0F, -0.4483F, -0.3093F, 2, 3, 6, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-2.0F, -2.5176F, -10.5681F);
		snout.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.2618F, -0.1745F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 0, 0.0F, -0.4824F, -0.5681F, 2, 2, 6, 0.0F, true));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(2.0F, -2.5176F, -10.5681F);
		snout.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.2618F, 0.1745F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 0, -2.0F, -0.4824F, -0.5681F, 2, 2, 6, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(0.0F, -2.5F, -10.0F);
		snout.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.2793F, 0.0F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 16, 14, -1.5F, -0.5388F, -1.5257F, 3, 3, 6, 0.0F, false));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.0F, -1.5F, -3.5F);
		bipedHead.addChild(jaw);
		jaw.cubeList.add(new ModelBox(jaw, 0, 22, -2.0F, 0.5F, -5.0F, 4, 1, 5, 0.0F, false));

		earRight = new ModelRenderer(this);
		earRight.setRotationPoint(-3.25F, -6.0F, -4.0F);
		bipedHead.addChild(earRight);
		setRotationAngle(earRight, 0.2618F, 0.0F, 0.0F);
		earRight.cubeList.add(new ModelBox(earRight, 52, 16, -1.0F, -1.0152F, -0.4236F, 3, 3, 2, 0.0F, true));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-1.0F, 2.0F, 1.75F);
		earRight.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, -0.0873F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 36, 16, -0.0151F, -3.0152F, -0.173F, 2, 3, 3, 0.0F, true));
		cube_r5.cubeList.add(new ModelBox(cube_r5, 20, 24, -0.0151F, -3.0152F, 2.577F, 2, 3, 3, -0.2F, true));
		cube_r5.cubeList.add(new ModelBox(cube_r5, 30, 24, -0.0151F, -3.0152F, 4.827F, 2, 3, 3, -0.4F, true));
		cube_r5.cubeList.add(new ModelBox(cube_r5, 40, 24, -0.0151F, -3.0152F, 6.827F, 2, 3, 3, -0.6F, true));
		cube_r5.cubeList.add(new ModelBox(cube_r5, 50, 24, -0.0151F, -3.0152F, 8.327F, 2, 3, 3, -0.8F, true));

		earLeft = new ModelRenderer(this);
		earLeft.setRotationPoint(3.25F, -6.0F, -4.0F);
		bipedHead.addChild(earLeft);
		setRotationAngle(earLeft, 0.2618F, 0.0F, 0.0F);
		earLeft.cubeList.add(new ModelBox(earLeft, 52, 16, -2.0F, -1.0152F, -0.4236F, 3, 3, 2, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(1.0F, 2.0F, 1.75F);
		earLeft.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0873F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 36, 16, -1.9849F, -3.0152F, -0.173F, 2, 3, 3, 0.0F, false));
		cube_r4.cubeList.add(new ModelBox(cube_r4, 20, 24, -1.9849F, -3.0152F, 2.577F, 2, 3, 3, -0.2F, false));
		cube_r4.cubeList.add(new ModelBox(cube_r4, 30, 24, -1.9849F, -3.0152F, 4.827F, 2, 3, 3, -0.4F, false));
		cube_r4.cubeList.add(new ModelBox(cube_r4, 40, 24, -1.9849F, -3.0152F, 6.827F, 2, 3, 3, -0.6F, false));
		cube_r4.cubeList.add(new ModelBox(cube_r4, 50, 24, -1.9849F, -3.0152F, 8.327F, 2, 3, 3, -0.8F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, -1.0F, -8.0F);
		setRotationAngle(bipedHeadwear, 0.2618F, 0.0F, 0.0F);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, -5.25F, -4.35F);
		bipedHeadwear.addChild(bone);
		setRotationAngle(bone, 0.2618F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 48, 62, -4.0F, -1.0F, 0.0F, 8, 2, 0, 0.0F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, -1.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 32, 0, -5.0F, 9.0F, -2.0F, 10, 5, 5, 0.0F, false));

		waist = new ModelRenderer(this);
		waist.setRotationPoint(-1.0F, 9.0F, 0.0F);
		bipedBody.addChild(waist);
		setRotationAngle(waist, 0.5236F, 0.0F, 0.0F);
		waist.cubeList.add(new ModelBox(waist, 30, 0, -5.0F, -4.634F, -2.5F, 12, 6, 5, 0.0F, false));

		chest = new ModelRenderer(this);
		chest.setRotationPoint(-1.0F, 2.0F, -5.0F);
		bipedBody.addChild(chest);
		setRotationAngle(chest, 0.7854F, 0.0F, 0.0F);
		chest.cubeList.add(new ModelBox(chest, 24, 0, -6.0F, -3.2929F, -3.7071F, 14, 8, 6, 0.0F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-6.0F, 1.0F, -7.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 32, -1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));

		upperArmRight = new ModelRenderer(this);
		upperArmRight.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(upperArmRight);
		setRotationAngle(upperArmRight, 0.0F, 0.0F, 0.3491F);
		upperArmRight.cubeList
				.add(new ModelBox(upperArmRight, 0, 32, -3.6579F, -1.0603F, -2.0F, 4, 12, 4, 0.0F, false));

		lowerArmRight = new ModelRenderer(this);
		lowerArmRight.setRotationPoint(-5.5F, 8.2791F, -0.1201F);
		bipedRightArm.addChild(lowerArmRight);
		setRotationAngle(lowerArmRight, -0.5236F, 0.0F, -0.2618F);
		lowerArmRight.cubeList
				.add(new ModelBox(lowerArmRight, 0, 32, -1.2588F, -0.1635F, -1.517F, 3, 12, 3, 0.0F, false));

		rightHand = new ModelRenderer(this);
		rightHand.setRotationPoint(0.75F, 10.9709F, -0.6299F);
		lowerArmRight.addChild(rightHand);
		setRotationAngle(rightHand, 1.5708F, 0.5236F, 1.5708F);

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightHand.addChild(bone5);
		bone5.cubeList.add(new ModelBox(bone5, 0, 48, 0.483F, 0.3365F, -0.7588F, 3, 1, 1, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(3.2F, 0.8F, -0.2F);
		bone5.addChild(bone6);
		setRotationAngle(bone6, 0.0F, 0.0F, 0.5236F);
		bone6.cubeList.add(new ModelBox(bone6, 0, 48, -0.2635F, -0.467F, -0.5588F, 3, 1, 1, 0.0F, false));
		bone6.cubeList.add(new ModelBox(bone6, 12, 48, 1.9865F, -0.717F, -0.5588F, 2, 1, 1, -0.2F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(0.0F, 0.0F, 0.25F);
		rightHand.addChild(bone11);
		setRotationAngle(bone11, 0.0F, -0.1745F, 0.0F);
		bone11.cubeList.add(new ModelBox(bone11, 0, 48, 0.4307F, 0.3365F, -0.8388F, 3, 1, 1, 0.0F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(3.2F, 0.8F, -0.3F);
		bone11.addChild(bone12);
		setRotationAngle(bone12, 0.0F, 0.0F, 0.5236F);
		bone12.cubeList.add(new ModelBox(bone12, 0, 48, -0.2088F, -0.4909F, -0.5388F, 3, 1, 1, 0.0F, false));
		bone12.cubeList.add(new ModelBox(bone12, 11, 48, 2.0412F, -0.7409F, -0.5388F, 2, 1, 1, -0.2F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(0.5F, 0.9F, -0.25F);
		rightHand.addChild(bone14);
		setRotationAngle(bone14, -1.5708F, -0.7854F, 0.5236F);
		bone14.cubeList.add(new ModelBox(bone14, 0, 48, -0.0489F, -0.5603F, -0.5088F, 3, 1, 1, 0.0F, false));

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(2.6622F, -0.0175F, -0.0239F);
		bone14.addChild(bone15);
		setRotationAngle(bone15, 0.0F, 0.0F, 0.7854F);
		bone15.cubeList.add(new ModelBox(bone15, 0, 48, -0.2712F, -0.5654F, -0.485F, 3, 1, 1, 0.0F, false));
		bone15.cubeList.add(new ModelBox(bone15, 12, 48, 1.9788F, -0.8154F, -0.485F, 2, 1, 1, -0.2F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, 0.0F, -0.5F);
		rightHand.addChild(bone7);
		setRotationAngle(bone7, 0.0F, 0.1745F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 0, 48, 0.5206F, 0.3365F, -0.671F, 3, 1, 1, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(3.3F, 0.8F, -0.1F);
		bone7.addChild(bone8);
		setRotationAngle(bone8, 0.0F, 0.0F, 0.5236F);
		bone8.cubeList.add(new ModelBox(bone8, 0, 48, -0.2309F, -0.4358F, -0.571F, 3, 1, 1, 0.0F, false));
		bone8.cubeList.add(new ModelBox(bone8, 12, 48, 2.0191F, -0.6858F, -0.571F, 2, 1, 1, -0.2F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(0.0F, 0.0F, -1.0F);
		rightHand.addChild(bone9);
		setRotationAngle(bone9, 0.0F, 0.3491F, 0.0F);
		bone9.cubeList.add(new ModelBox(bone9, 0, 48, 0.5424F, 0.3365F, -0.578F, 3, 1, 1, 0.0F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(3.3F, 0.8F, 0.0F);
		bone9.addChild(bone10);
		setRotationAngle(bone10, 0.0F, 0.0F, 0.5236F);
		bone10.cubeList.add(new ModelBox(bone10, 0, 48, -0.212F, -0.4467F, -0.578F, 3, 1, 1, 0.0F, false));
		bone10.cubeList.add(new ModelBox(bone10, 12, 48, 2.038F, -0.6967F, -0.578F, 2, 1, 1, -0.2F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(6.0F, 1.0F, -7.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 32, 0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));

		upperArmLeft = new ModelRenderer(this);
		upperArmLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(upperArmLeft);
		setRotationAngle(upperArmLeft, 0.0F, 0.0F, -0.3491F);
		upperArmLeft.cubeList.add(new ModelBox(upperArmLeft, 0, 32, -0.3421F, -1.0603F, -2.0F, 4, 12, 4, 0.0F, false));

		lowerArmLeft = new ModelRenderer(this);
		lowerArmLeft.setRotationPoint(5.5F, 8.2791F, -0.1201F);
		bipedLeftArm.addChild(lowerArmLeft);
		setRotationAngle(lowerArmLeft, -0.5236F, 0.0F, 0.2618F);
		lowerArmLeft.cubeList
				.add(new ModelBox(lowerArmLeft, 0, 32, -1.7412F, -0.1635F, -1.517F, 3, 12, 3, 0.0F, false));

		leftHand = new ModelRenderer(this);
		leftHand.setRotationPoint(-0.75F, 10.9709F, -0.6299F);
		lowerArmLeft.addChild(leftHand);
		setRotationAngle(leftHand, 1.5708F, -0.5236F, -1.5708F);

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftHand.addChild(bone2);
		bone2.cubeList.add(new ModelBox(bone2, 0, 48, -3.483F, 0.3365F, -0.7588F, 3, 1, 1, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-3.2F, 0.8F, -0.2F);
		bone2.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 0.0F, -0.5236F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 48, -2.7365F, -0.467F, -0.5588F, 3, 1, 1, 0.0F, false));
		bone3.cubeList.add(new ModelBox(bone3, 12, 48, -3.9865F, -0.717F, -0.5588F, 2, 1, 1, -0.2F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, 0.0F, 0.25F);
		leftHand.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 0.1745F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 48, -3.4307F, 0.3365F, -0.8388F, 3, 1, 1, 0.0F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(-3.2F, 0.8F, -0.3F);
		bone4.addChild(bone13);
		setRotationAngle(bone13, 0.0F, 0.0F, -0.5236F);
		bone13.cubeList.add(new ModelBox(bone13, 0, 48, -2.7912F, -0.4909F, -0.5388F, 3, 1, 1, 0.0F, false));
		bone13.cubeList.add(new ModelBox(bone13, 11, 48, -4.0412F, -0.7409F, -0.5388F, 2, 1, 1, -0.2F, false));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(-0.5F, 0.9F, -0.25F);
		leftHand.addChild(bone16);
		setRotationAngle(bone16, -1.5708F, 0.7854F, -0.5236F);
		bone16.cubeList.add(new ModelBox(bone16, 0, 48, -2.9511F, -0.5603F, -0.5088F, 3, 1, 1, 0.0F, false));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(-2.6622F, -0.0175F, -0.0239F);
		bone16.addChild(bone17);
		setRotationAngle(bone17, 0.0F, 0.0F, -0.7854F);
		bone17.cubeList.add(new ModelBox(bone17, 0, 48, -2.7288F, -0.5654F, -0.485F, 3, 1, 1, 0.0F, false));
		bone17.cubeList.add(new ModelBox(bone17, 12, 48, -3.9788F, -0.8154F, -0.485F, 2, 1, 1, -0.2F, false));

		bone18 = new ModelRenderer(this);
		bone18.setRotationPoint(0.0F, 0.0F, -0.5F);
		leftHand.addChild(bone18);
		setRotationAngle(bone18, 0.0F, -0.1745F, 0.0F);
		bone18.cubeList.add(new ModelBox(bone18, 0, 48, -3.5206F, 0.3365F, -0.671F, 3, 1, 1, 0.0F, false));

		bone19 = new ModelRenderer(this);
		bone19.setRotationPoint(-3.3F, 0.8F, -0.1F);
		bone18.addChild(bone19);
		setRotationAngle(bone19, 0.0F, 0.0F, -0.5236F);
		bone19.cubeList.add(new ModelBox(bone19, 0, 48, -2.7691F, -0.4358F, -0.571F, 3, 1, 1, 0.0F, false));
		bone19.cubeList.add(new ModelBox(bone19, 12, 48, -4.0191F, -0.6858F, -0.571F, 2, 1, 1, -0.2F, false));

		bone20 = new ModelRenderer(this);
		bone20.setRotationPoint(0.0F, 0.0F, -1.0F);
		leftHand.addChild(bone20);
		setRotationAngle(bone20, 0.0F, -0.3491F, 0.0F);
		bone20.cubeList.add(new ModelBox(bone20, 0, 48, -3.5424F, 0.3365F, -0.578F, 3, 1, 1, 0.0F, false));

		bone21 = new ModelRenderer(this);
		bone21.setRotationPoint(-3.3F, 0.8F, 0.0F);
		bone20.addChild(bone21);
		setRotationAngle(bone21, 0.0F, 0.0F, -0.5236F);
		bone21.cubeList.add(new ModelBox(bone21, 0, 48, -2.788F, -0.4467F, -0.578F, 3, 1, 1, 0.0F, false));
		bone21.cubeList.add(new ModelBox(bone21, 12, 48, -4.038F, -0.6967F, -0.578F, 2, 1, 1, -0.2F, false));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.5F);

		upperLegRight = new ModelRenderer(this);
		upperLegRight.setRotationPoint(-0.1F, 0.0F, 1.0F);
		bipedRightLeg.addChild(upperLegRight);
		setRotationAngle(upperLegRight, -0.5236F, 0.0F, 1.5708F);
		upperLegRight.cubeList.add(new ModelBox(upperLegRight, 16, 32, -1.0F, 0.0F, -3.0F, 4, 12, 4, 0.5F, false));

		lowerLegRight = new ModelRenderer(this);
		lowerLegRight.setRotationPoint(0.0F, 11.0F, -1.0F);
		upperLegRight.addChild(lowerLegRight);
		setRotationAngle(lowerLegRight, 0.0F, 0.0F, -2.0944F);
		lowerLegRight.cubeList.add(new ModelBox(lowerLegRight, 16, 32, -2.5F, -0.134F, -2.0F, 4, 12, 4, 0.0F, false));

		rightFoot = new ModelRenderer(this);
		rightFoot.setRotationPoint(0.25F, 11.25F, -0.25F);
		lowerLegRight.addChild(rightFoot);
		setRotationAngle(rightFoot, 0.0F, 3.1416F, 0.5236F);

		bone25 = new ModelRenderer(this);
		bone25.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightFoot.addChild(bone25);
		bone25.cubeList.add(new ModelBox(bone25, 0, 48, 0.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		bone26 = new ModelRenderer(this);
		bone26.setRotationPoint(2.8F, 1.0F, 0.0F);
		bone25.addChild(bone26);
		setRotationAngle(bone26, 0.0F, 0.0F, 0.5236F);
		bone26.cubeList.add(new ModelBox(bone26, 0, 48, -0.2F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
		bone26.cubeList.add(new ModelBox(bone26, 12, 48, 2.05F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));

		bone27 = new ModelRenderer(this);
		bone27.setRotationPoint(0.0F, 0.0F, 0.25F);
		rightFoot.addChild(bone27);
		setRotationAngle(bone27, 0.0F, -0.1745F, 0.0F);
		bone27.cubeList.add(new ModelBox(bone27, 0, 48, 0.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		bone28 = new ModelRenderer(this);
		bone28.setRotationPoint(2.7F, 1.0F, 0.0F);
		bone27.addChild(bone28);
		setRotationAngle(bone28, 0.0F, 0.0F, 0.5236F);
		bone28.cubeList.add(new ModelBox(bone28, 0, 48, -0.1F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
		bone28.cubeList.add(new ModelBox(bone28, 12, 48, 2.15F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));

		bone29 = new ModelRenderer(this);
		bone29.setRotationPoint(0.0F, 0.0F, 0.75F);
		rightFoot.addChild(bone29);
		setRotationAngle(bone29, -0.5236F, -0.5236F, 0.0F);
		bone29.cubeList.add(new ModelBox(bone29, 0, 48, 0.0F, 0.366F, 0.0F, 3, 1, 1, 0.0F, false));

		bone30 = new ModelRenderer(this);
		bone30.setRotationPoint(2.7F, 0.9F, 0.5F);
		bone29.addChild(bone30);
		setRotationAngle(bone30, 0.0F, 0.0F, 0.5236F);
		bone30.cubeList.add(new ModelBox(bone30, 0, 48, -0.167F, -0.55F, -0.5F, 3, 1, 1, 0.0F, false));
		bone30.cubeList.add(new ModelBox(bone30, 12, 48, 2.083F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));

		bone31 = new ModelRenderer(this);
		bone31.setRotationPoint(0.0F, 0.0F, -0.5F);
		rightFoot.addChild(bone31);
		setRotationAngle(bone31, 0.0F, 0.1745F, 0.0F);
		bone31.cubeList.add(new ModelBox(bone31, 0, 48, 0.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		bone32 = new ModelRenderer(this);
		bone32.setRotationPoint(2.8F, 0.95F, 0.0F);
		bone31.addChild(bone32);
		setRotationAngle(bone32, 0.0F, 0.0F, 0.5236F);
		bone32.cubeList.add(new ModelBox(bone32, 0, 48, -0.2F, -0.45F, -0.5F, 3, 1, 1, 0.0F, false));
		bone32.cubeList.add(new ModelBox(bone32, 12, 48, 2.05F, -0.75F, -0.5F, 2, 1, 1, -0.2F, false));

		bone33 = new ModelRenderer(this);
		bone33.setRotationPoint(0.0F, 0.0F, -1.0F);
		rightFoot.addChild(bone33);
		setRotationAngle(bone33, 0.0F, 0.3491F, 0.0F);
		bone33.cubeList.add(new ModelBox(bone33, 0, 48, 0.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		bone34 = new ModelRenderer(this);
		bone34.setRotationPoint(2.8F, 1.0F, 0.0F);
		bone33.addChild(bone34);
		setRotationAngle(bone34, 0.0F, 0.0F, 0.5236F);
		bone34.cubeList.add(new ModelBox(bone34, 0, 48, -0.2F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
		bone34.cubeList.add(new ModelBox(bone34, 12, 48, 2.05F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.5F);

		upperLegLeft = new ModelRenderer(this);
		upperLegLeft.setRotationPoint(0.1F, 0.0F, 1.0F);
		bipedLeftLeg.addChild(upperLegLeft);
		setRotationAngle(upperLegLeft, -0.5236F, 0.0F, -1.5708F);
		upperLegLeft.cubeList.add(new ModelBox(upperLegLeft, 16, 32, -3.0F, 0.0F, -3.0F, 4, 12, 4, 0.5F, false));

		lowerLegLeft = new ModelRenderer(this);
		lowerLegLeft.setRotationPoint(0.0F, 11.0F, -1.0F);
		upperLegLeft.addChild(lowerLegLeft);
		setRotationAngle(lowerLegLeft, 0.0F, 0.0F, 2.0944F);
		lowerLegLeft.cubeList.add(new ModelBox(lowerLegLeft, 16, 32, -1.5F, -0.134F, -2.0F, 4, 12, 4, 0.0F, false));

		leftFoot = new ModelRenderer(this);
		leftFoot.setRotationPoint(-0.25F, 11.25F, -0.25F);
		lowerLegLeft.addChild(leftFoot);
		setRotationAngle(leftFoot, 0.0F, -3.1416F, -0.5236F);

		bone22 = new ModelRenderer(this);
		bone22.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftFoot.addChild(bone22);
		bone22.cubeList.add(new ModelBox(bone22, 0, 48, -3.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		bone23 = new ModelRenderer(this);
		bone23.setRotationPoint(-2.8F, 1.0F, 0.0F);
		bone22.addChild(bone23);
		setRotationAngle(bone23, 0.0F, 0.0F, -0.5236F);
		bone23.cubeList.add(new ModelBox(bone23, 0, 48, -2.8F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
		bone23.cubeList.add(new ModelBox(bone23, 12, 48, -4.05F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));

		bone24 = new ModelRenderer(this);
		bone24.setRotationPoint(0.0F, 0.0F, 0.25F);
		leftFoot.addChild(bone24);
		setRotationAngle(bone24, 0.0F, 0.1745F, 0.0F);
		bone24.cubeList.add(new ModelBox(bone24, 0, 48, -3.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		bone35 = new ModelRenderer(this);
		bone35.setRotationPoint(-2.7F, 1.0F, 0.0F);
		bone24.addChild(bone35);
		setRotationAngle(bone35, 0.0F, 0.0F, -0.5236F);
		bone35.cubeList.add(new ModelBox(bone35, 0, 48, -2.9F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
		bone35.cubeList.add(new ModelBox(bone35, 12, 48, -4.15F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));

		bone36 = new ModelRenderer(this);
		bone36.setRotationPoint(0.0F, 0.0F, 0.75F);
		leftFoot.addChild(bone36);
		setRotationAngle(bone36, -0.5236F, 0.5236F, 0.0F);
		bone36.cubeList.add(new ModelBox(bone36, 0, 48, -3.0F, 0.366F, 0.0F, 3, 1, 1, 0.0F, false));

		bone37 = new ModelRenderer(this);
		bone37.setRotationPoint(-2.7F, 0.9F, 0.5F);
		bone36.addChild(bone37);
		setRotationAngle(bone37, 0.0F, 0.0F, -0.5236F);
		bone37.cubeList.add(new ModelBox(bone37, 0, 48, -2.833F, -0.55F, -0.5F, 3, 1, 1, 0.0F, false));
		bone37.cubeList.add(new ModelBox(bone37, 12, 48, -4.083F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));

		bone38 = new ModelRenderer(this);
		bone38.setRotationPoint(0.0F, 0.0F, -0.5F);
		leftFoot.addChild(bone38);
		setRotationAngle(bone38, 0.0F, -0.1745F, 0.0F);
		bone38.cubeList.add(new ModelBox(bone38, 0, 48, -3.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		bone39 = new ModelRenderer(this);
		bone39.setRotationPoint(-2.8F, 0.95F, 0.0F);
		bone38.addChild(bone39);
		setRotationAngle(bone39, 0.0F, 0.0F, -0.5236F);
		bone39.cubeList.add(new ModelBox(bone39, 0, 48, -2.8F, -0.45F, -0.5F, 3, 1, 1, 0.0F, false));
		bone39.cubeList.add(new ModelBox(bone39, 12, 48, -4.05F, -0.75F, -0.5F, 2, 1, 1, -0.2F, false));

		bone40 = new ModelRenderer(this);
		bone40.setRotationPoint(0.0F, 0.0F, -1.0F);
		leftFoot.addChild(bone40);
		setRotationAngle(bone40, 0.0F, -0.3491F, 0.0F);
		bone40.cubeList.add(new ModelBox(bone40, 0, 48, -3.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));

		bone41 = new ModelRenderer(this);
		bone41.setRotationPoint(-2.8F, 1.0F, 0.0F);
		bone40.addChild(bone41);
		setRotationAngle(bone41, 0.0F, 0.0F, -0.5236F);
		bone41.cubeList.add(new ModelBox(bone41, 0, 48, -2.8F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
		bone41.cubeList.add(new ModelBox(bone41, 12, 48, -4.05F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));

		Tail1_0 = new ModelRenderer(this);
		Tail1_0.setRotationPoint(4.0F, 12.0F, 5.0F);
		setRotationAngle(Tail1_0, -0.2618F, 0.0F, 1.4835F);
		Tail1_0.cubeList.add(new ModelBox(Tail1_0, 32, 32, -1.0038F, -7.9158F, -1.9774F, 4, 8, 4, 0.0F, false));

		Tail1_1 = new ModelRenderer(this);
		Tail1_1.setRotationPoint(0.0F, -7.25F, 0.25F);
		Tail1_0.addChild(Tail1_1);
		setRotationAngle(Tail1_1, 0.0F, 0.0F, -0.1745F);
		Tail1_1.cubeList.add(new ModelBox(Tail1_1, 32, 32, -1.0336F, -7.4941F, -2.2274F, 4, 8, 4, 0.3F, false));

		Tail1_2 = new ModelRenderer(this);
		Tail1_2.setRotationPoint(0.0F, -6.75F, -0.25F);
		Tail1_1.addChild(Tail1_2);
		setRotationAngle(Tail1_2, 0.0F, 0.0F, -0.1745F);
		Tail1_2.cubeList.add(new ModelBox(Tail1_2, 32, 31, -1.0595F, -7.6608F, -1.9774F, 4, 8, 4, 0.6F, false));

		Tail1_3 = new ModelRenderer(this);
		Tail1_3.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail1_2.addChild(Tail1_3);
		setRotationAngle(Tail1_3, 0.0F, 0.0F, -0.1745F);
		Tail1_3.cubeList.add(new ModelBox(Tail1_3, 32, 32, -1.0927F, -7.8301F, -1.9774F, 4, 8, 4, 0.8F, false));

		Tail1_4 = new ModelRenderer(this);
		Tail1_4.setRotationPoint(0.0F, -7.25F, 0.0F);
		Tail1_3.addChild(Tail1_4);
		setRotationAngle(Tail1_4, 0.0F, 0.0F, -0.1745F);
		Tail1_4.cubeList.add(new ModelBox(Tail1_4, 32, 32, -1.1328F, -7.5026F, -1.9774F, 4, 8, 4, 0.4F, false));

		Tail1_5 = new ModelRenderer(this);
		Tail1_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail1_4.addChild(Tail1_5);
		setRotationAngle(Tail1_5, 0.0F, 0.0F, -0.1745F);
		Tail1_5.cubeList.add(new ModelBox(Tail1_5, 32, 32, -1.1794F, -7.4289F, -1.9774F, 4, 8, 4, 0.0F, false));

		Tail1_6 = new ModelRenderer(this);
		Tail1_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail1_5.addChild(Tail1_6);
		setRotationAngle(Tail1_6, 0.0F, 0.0F, -0.1745F);
		Tail1_6.cubeList.add(new ModelBox(Tail1_6, 32, 32, -1.2324F, -7.3595F, -1.9774F, 4, 8, 4, -0.4F, false));

		Tail1_7 = new ModelRenderer(this);
		Tail1_7.setRotationPoint(0.5F, -6.75F, 0.1F);
		Tail1_6.addChild(Tail1_7);
		setRotationAngle(Tail1_7, 0.0F, 0.0F, -0.1745F);
		Tail1_7.cubeList.add(new ModelBox(Tail1_7, 32, 32, -1.855F, -6.7077F, -2.0774F, 4, 8, 4, -0.8F, false));

		Tail2_0 = new ModelRenderer(this);
		Tail2_0.setRotationPoint(3.0F, 12.0F, 4.0F);
		setRotationAngle(Tail2_0, -0.2618F, 0.0F, 1.1345F);
		Tail2_0.cubeList.add(new ModelBox(Tail2_0, 32, 32, -1.0937F, -7.5918F, -1.8906F, 4, 8, 4, 0.0F, false));

		Tail2_1 = new ModelRenderer(this);
		Tail2_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail2_0.addChild(Tail2_1);
		setRotationAngle(Tail2_1, 0.0F, 0.0F, -0.1745F);
		Tail2_1.cubeList.add(new ModelBox(Tail2_1, 32, 32, -1.1327F, -7.7643F, -1.8906F, 4, 8, 4, 0.3F, false));

		Tail2_2 = new ModelRenderer(this);
		Tail2_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_1.addChild(Tail2_2);
		setRotationAngle(Tail2_2, 0.0F, 0.0F, -0.1745F);
		Tail2_2.cubeList.add(new ModelBox(Tail2_2, 32, 32, -1.1784F, -7.6906F, -1.8906F, 4, 8, 4, 0.6F, false));

		Tail2_3 = new ModelRenderer(this);
		Tail2_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_2.addChild(Tail2_3);
		setRotationAngle(Tail2_3, 0.0F, 0.0F, -0.1745F);
		Tail2_3.cubeList.add(new ModelBox(Tail2_3, 32, 32, -1.2303F, -7.6211F, -1.8906F, 4, 8, 4, 0.8F, false));

		Tail2_4 = new ModelRenderer(this);
		Tail2_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_3.addChild(Tail2_4);
		setRotationAngle(Tail2_4, 0.0F, 0.0F, -0.1745F);
		Tail2_4.cubeList.add(new ModelBox(Tail2_4, 32, 32, -1.288F, -7.5564F, -1.8906F, 4, 8, 4, 0.4F, false));

		Tail2_5 = new ModelRenderer(this);
		Tail2_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_4.addChild(Tail2_5);
		setRotationAngle(Tail2_5, 0.0F, 0.0F, -0.1745F);
		Tail2_5.cubeList.add(new ModelBox(Tail2_5, 32, 32, -1.3512F, -7.4969F, -1.8906F, 4, 8, 4, 0.0F, false));

		Tail2_6 = new ModelRenderer(this);
		Tail2_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_5.addChild(Tail2_6);
		setRotationAngle(Tail2_6, 0.0F, 0.0F, -0.1745F);
		Tail2_6.cubeList.add(new ModelBox(Tail2_6, 32, 32, -1.4194F, -7.4432F, -1.8906F, 4, 8, 4, -0.4F, false));

		Tail2_7 = new ModelRenderer(this);
		Tail2_7.setRotationPoint(0.45F, -6.5F, 0.25F);
		Tail2_6.addChild(Tail2_7);
		setRotationAngle(Tail2_7, 0.0F, 0.0F, -0.1745F);
		Tail2_7.cubeList.add(new ModelBox(Tail2_7, 32, 32, -1.9494F, -7.0289F, -2.1406F, 4, 8, 4, -0.8F, false));

		Tail3_0 = new ModelRenderer(this);
		Tail3_0.setRotationPoint(2.0F, 12.0F, 4.75F);
		setRotationAngle(Tail3_0, -0.2618F, 0.0F, 0.7854F);
		Tail3_0.cubeList.add(new ModelBox(Tail3_0, 32, 32, -1.2929F, -7.317F, -1.567F, 4, 8, 4, 0.0F, false));

		Tail3_1 = new ModelRenderer(this);
		Tail3_1.setRotationPoint(0.0F, -6.75F, 0.25F);
		Tail3_0.addChild(Tail3_1);
		setRotationAngle(Tail3_1, 0.0F, 0.0F, -0.1745F);
		Tail3_1.cubeList.add(new ModelBox(Tail3_1, 32, 32, -1.3551F, -7.5079F, -1.817F, 4, 8, 4, 0.3F, false));

		Tail3_2 = new ModelRenderer(this);
		Tail3_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_1.addChild(Tail3_2);
		setRotationAngle(Tail3_2, 0.0F, 0.0F, -0.1745F);
		Tail3_2.cubeList.add(new ModelBox(Tail3_2, 32, 31, -1.4223F, -7.4545F, -1.817F, 4, 8, 4, 0.6F, false));

		Tail3_3 = new ModelRenderer(this);
		Tail3_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_2.addChild(Tail3_3);
		setRotationAngle(Tail3_3, 0.0F, 0.0F, -0.1745F);
		Tail3_3.cubeList.add(new ModelBox(Tail3_3, 32, 32, -1.4938F, -7.4072F, -1.817F, 4, 8, 4, 0.8F, false));

		Tail3_4 = new ModelRenderer(this);
		Tail3_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_3.addChild(Tail3_4);
		setRotationAngle(Tail3_4, 0.0F, 0.0F, -0.1745F);
		Tail3_4.cubeList.add(new ModelBox(Tail3_4, 32, 32, -1.5693F, -7.3663F, -1.817F, 4, 8, 4, 0.4F, false));

		Tail3_5 = new ModelRenderer(this);
		Tail3_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_4.addChild(Tail3_5);
		setRotationAngle(Tail3_5, 0.0F, 0.0F, -0.1745F);
		Tail3_5.cubeList.add(new ModelBox(Tail3_5, 32, 32, -1.6479F, -7.3321F, -1.817F, 4, 8, 4, 0.0F, false));

		Tail3_6 = new ModelRenderer(this);
		Tail3_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_5.addChild(Tail3_6);
		setRotationAngle(Tail3_6, 0.0F, 0.0F, -0.1745F);
		Tail3_6.cubeList.add(new ModelBox(Tail3_6, 32, 32, -1.7293F, -7.3049F, -1.817F, 4, 8, 4, -0.4F, false));

		Tail3_7 = new ModelRenderer(this);
		Tail3_7.setRotationPoint(0.15F, -6.5F, 0.25F);
		Tail3_6.addChild(Tail3_7);
		setRotationAngle(Tail3_7, 0.0F, 0.0F, -0.1745F);
		Tail3_7.cubeList.add(new ModelBox(Tail3_7, 32, 32, -1.9638F, -6.8385F, -2.067F, 4, 8, 4, -0.8F, false));

		Tail4_0 = new ModelRenderer(this);
		Tail4_0.setRotationPoint(1.0F, 12.0F, 5.0F);
		setRotationAngle(Tail4_0, -0.2618F, 0.0F, 0.4363F);
		Tail4_0.cubeList.add(new ModelBox(Tail4_0, 32, 32, -1.5774F, -7.1246F, -1.7654F, 4, 8, 4, 0.0F, false));

		Tail4_1 = new ModelRenderer(this);
		Tail4_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail4_0.addChild(Tail4_1);
		setRotationAngle(Tail4_1, 0.0F, 0.0F, -0.1745F);
		Tail4_1.cubeList.add(new ModelBox(Tail4_1, 32, 32, -1.6553F, -7.341F, -1.7654F, 4, 8, 4, 0.3F, false));

		Tail4_2 = new ModelRenderer(this);
		Tail4_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_1.addChild(Tail4_2);
		setRotationAngle(Tail4_2, 0.0F, 0.0F, -0.1745F);
		Tail4_2.cubeList.add(new ModelBox(Tail4_2, 32, 32, -1.7359F, -7.3145F, -1.7654F, 4, 8, 4, 0.6F, false));

		Tail4_3 = new ModelRenderer(this);
		Tail4_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_2.addChild(Tail4_3);
		setRotationAngle(Tail4_3, 0.0F, 0.0F, -0.1745F);
		Tail4_3.cubeList.add(new ModelBox(Tail4_3, 32, 32, -1.8185F, -7.295F, -1.7654F, 4, 8, 4, 0.8F, false));

		Tail4_4 = new ModelRenderer(this);
		Tail4_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_3.addChild(Tail4_4);
		setRotationAngle(Tail4_4, 0.0F, 0.0F, -0.1745F);
		Tail4_4.cubeList.add(new ModelBox(Tail4_4, 32, 32, -1.9024F, -7.2828F, -1.7654F, 4, 8, 4, 0.4F, false));

		Tail4_5 = new ModelRenderer(this);
		Tail4_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_4.addChild(Tail4_5);
		setRotationAngle(Tail4_5, 0.0F, 0.0F, -0.1745F);
		Tail4_5.cubeList.add(new ModelBox(Tail4_5, 32, 32, -1.9871F, -7.278F, -1.7654F, 4, 8, 4, 0.0F, false));

		Tail4_6 = new ModelRenderer(this);
		Tail4_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_5.addChild(Tail4_6);
		setRotationAngle(Tail4_6, 0.0F, 0.0F, -0.1745F);
		Tail4_6.cubeList.add(new ModelBox(Tail4_6, 32, 32, -2.0719F, -7.2806F, -1.7654F, 4, 8, 4, -0.4F, false));

		Tail4_7 = new ModelRenderer(this);
		Tail4_7.setRotationPoint(-0.2F, -6.25F, 0.0F);
		Tail4_6.addChild(Tail4_7);
		setRotationAngle(Tail4_7, 0.0F, 0.0F, -0.1745F);
		Tail4_7.cubeList.add(new ModelBox(Tail4_7, 32, 32, -1.9184F, -6.9997F, -1.7654F, 4, 8, 4, -0.8F, false));

		Tail5_0 = new ModelRenderer(this);
		Tail5_0.setRotationPoint(0.0F, 12.0F, 5.0F);
		setRotationAngle(Tail5_0, -0.5236F, 0.0F, 0.0F);
		Tail5_0.cubeList.add(new ModelBox(Tail5_0, 32, 32, -2.0F, -7.134F, -1.5F, 4, 8, 4, 0.0F, false));

		Tail5_1 = new ModelRenderer(this);
		Tail5_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail5_0.addChild(Tail5_1);
		setRotationAngle(Tail5_1, 0.1745F, 0.0F, 0.0F);
		Tail5_1.cubeList.add(new ModelBox(Tail5_1, 32, 32, -2.0F, -7.3437F, -1.5774F, 4, 8, 4, 0.3F, false));

		Tail5_2 = new ModelRenderer(this);
		Tail5_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_1.addChild(Tail5_2);
		setRotationAngle(Tail5_2, 0.1745F, 0.0F, 0.0F);
		Tail5_2.cubeList.add(new ModelBox(Tail5_2, 32, 32, -2.0F, -7.3103F, -1.658F, 4, 8, 4, 0.6F, false));

		Tail5_3 = new ModelRenderer(this);
		Tail5_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_2.addChild(Tail5_3);
		setRotationAngle(Tail5_3, 0.1745F, 0.0F, 0.0F);
		Tail5_3.cubeList.add(new ModelBox(Tail5_3, 32, 32, -2.0F, -7.284F, -1.7413F, 4, 8, 4, 0.8F, false));

		Tail5_4 = new ModelRenderer(this);
		Tail5_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_3.addChild(Tail5_4);
		setRotationAngle(Tail5_4, 0.1745F, 0.0F, 0.0F);
		Tail5_4.cubeList.add(new ModelBox(Tail5_4, 32, 32, -2.0F, -7.2652F, -1.8265F, 4, 8, 4, 0.4F, false));

		Tail5_5 = new ModelRenderer(this);
		Tail5_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_4.addChild(Tail5_5);
		setRotationAngle(Tail5_5, 0.1745F, 0.0F, 0.0F);
		Tail5_5.cubeList.add(new ModelBox(Tail5_5, 32, 32, -2.0F, -7.2538F, -1.913F, 4, 8, 4, 0.0F, false));

		Tail5_6 = new ModelRenderer(this);
		Tail5_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_5.addChild(Tail5_6);
		setRotationAngle(Tail5_6, 0.1745F, 0.0F, 0.0F);
		Tail5_6.cubeList.add(new ModelBox(Tail5_6, 32, 32, -2.0F, -7.25F, -2.0002F, 4, 8, 4, -0.4F, false));

		Tail5_7 = new ModelRenderer(this);
		Tail5_7.setRotationPoint(-0.1F, -6.25F, 0.0F);
		Tail5_6.addChild(Tail5_7);
		setRotationAngle(Tail5_7, 0.1745F, 0.0F, 0.0F);
		Tail5_7.cubeList.add(new ModelBox(Tail5_7, 32, 32, -1.9F, -7.0038F, -2.0436F, 4, 8, 4, -0.8F, false));

		Tail6_0 = new ModelRenderer(this);
		Tail6_0.setRotationPoint(-1.0F, 12.0F, 5.0F);
		setRotationAngle(Tail6_0, -0.2618F, 0.0F, -0.4363F);
		Tail6_0.cubeList.add(new ModelBox(Tail6_0, 32, 32, -2.4226F, -7.1246F, -1.7654F, 4, 8, 4, 0.0F, false));

		Tail6_1 = new ModelRenderer(this);
		Tail6_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail6_0.addChild(Tail6_1);
		setRotationAngle(Tail6_1, 0.0F, 0.0F, 0.1745F);
		Tail6_1.cubeList.add(new ModelBox(Tail6_1, 32, 32, -2.3447F, -7.341F, -1.7654F, 4, 8, 4, 0.3F, false));

		Tail6_2 = new ModelRenderer(this);
		Tail6_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_1.addChild(Tail6_2);
		setRotationAngle(Tail6_2, 0.0F, 0.0F, 0.1745F);
		Tail6_2.cubeList.add(new ModelBox(Tail6_2, 32, 32, -2.2641F, -7.3145F, -1.7654F, 4, 8, 4, 0.6F, false));

		Tail6_3 = new ModelRenderer(this);
		Tail6_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_2.addChild(Tail6_3);
		setRotationAngle(Tail6_3, 0.0F, 0.0F, 0.1745F);
		Tail6_3.cubeList.add(new ModelBox(Tail6_3, 32, 32, -2.1815F, -7.295F, -1.7654F, 4, 8, 4, 0.8F, false));

		Tail6_4 = new ModelRenderer(this);
		Tail6_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_3.addChild(Tail6_4);
		setRotationAngle(Tail6_4, 0.0F, 0.0F, 0.1745F);
		Tail6_4.cubeList.add(new ModelBox(Tail6_4, 32, 32, -2.0976F, -7.2828F, -1.7654F, 4, 8, 4, 0.4F, false));

		Tail6_5 = new ModelRenderer(this);
		Tail6_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_4.addChild(Tail6_5);
		setRotationAngle(Tail6_5, 0.0F, 0.0F, 0.1745F);
		Tail6_5.cubeList.add(new ModelBox(Tail6_5, 32, 32, -2.0129F, -7.278F, -1.7654F, 4, 8, 4, 0.0F, false));

		Tail6_6 = new ModelRenderer(this);
		Tail6_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_5.addChild(Tail6_6);
		setRotationAngle(Tail6_6, 0.0F, 0.0F, 0.1745F);
		Tail6_6.cubeList.add(new ModelBox(Tail6_6, 32, 32, -1.9281F, -7.2806F, -1.7654F, 4, 8, 4, -0.4F, false));

		Tail6_7 = new ModelRenderer(this);
		Tail6_7.setRotationPoint(0.0F, -6.0F, 0.25F);
		Tail6_6.addChild(Tail6_7);
		setRotationAngle(Tail6_7, 0.0F, 0.0F, 0.1745F);
		Tail6_7.cubeList.add(new ModelBox(Tail6_7, 32, 32, -1.9281F, -7.2806F, -2.0154F, 4, 8, 4, -0.8F, false));

		Tail7_0 = new ModelRenderer(this);
		Tail7_0.setRotationPoint(-2.0F, 12.0F, 5.0F);
		setRotationAngle(Tail7_0, -0.2618F, 0.0F, -0.7854F);
		Tail7_0.cubeList.add(new ModelBox(Tail7_0, 32, 32, -2.7071F, -7.317F, -1.817F, 4, 8, 4, 0.0F, false));

		Tail7_1 = new ModelRenderer(this);
		Tail7_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail7_0.addChild(Tail7_1);
		setRotationAngle(Tail7_1, 0.0F, 0.0F, 0.1745F);
		Tail7_1.cubeList.add(new ModelBox(Tail7_1, 32, 32, -2.6449F, -7.5079F, -1.817F, 4, 8, 4, 0.3F, false));

		Tail7_2 = new ModelRenderer(this);
		Tail7_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_1.addChild(Tail7_2);
		setRotationAngle(Tail7_2, 0.0F, 0.0F, 0.1745F);
		Tail7_2.cubeList.add(new ModelBox(Tail7_2, 32, 31, -2.5777F, -7.4545F, -1.817F, 4, 8, 4, 0.6F, false));

		Tail7_3 = new ModelRenderer(this);
		Tail7_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_2.addChild(Tail7_3);
		setRotationAngle(Tail7_3, 0.0F, 0.0F, 0.1745F);
		Tail7_3.cubeList.add(new ModelBox(Tail7_3, 32, 32, -2.5062F, -7.4072F, -1.817F, 4, 8, 4, 0.8F, false));

		Tail7_4 = new ModelRenderer(this);
		Tail7_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_3.addChild(Tail7_4);
		setRotationAngle(Tail7_4, 0.0F, 0.0F, 0.1745F);
		Tail7_4.cubeList.add(new ModelBox(Tail7_4, 32, 32, -2.4307F, -7.3663F, -1.817F, 4, 8, 4, 0.4F, false));

		Tail7_5 = new ModelRenderer(this);
		Tail7_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_4.addChild(Tail7_5);
		setRotationAngle(Tail7_5, 0.0F, 0.0F, 0.1745F);
		Tail7_5.cubeList.add(new ModelBox(Tail7_5, 32, 32, -2.3521F, -7.3321F, -1.817F, 4, 8, 4, 0.0F, false));

		Tail7_6 = new ModelRenderer(this);
		Tail7_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_5.addChild(Tail7_6);
		setRotationAngle(Tail7_6, 0.0F, 0.0F, 0.1745F);
		Tail7_6.cubeList.add(new ModelBox(Tail7_6, 32, 32, -2.2707F, -7.3049F, -1.817F, 4, 8, 4, -0.4F, false));

		Tail7_7 = new ModelRenderer(this);
		Tail7_7.setRotationPoint(-0.25F, -6.25F, 0.25F);
		Tail7_6.addChild(Tail7_7);
		setRotationAngle(Tail7_7, 0.0F, 0.0F, 0.1745F);
		Tail7_7.cubeList.add(new ModelBox(Tail7_7, 32, 32, -1.9811F, -7.1021F, -2.067F, 4, 8, 4, -0.8F, false));

		Tail8_0 = new ModelRenderer(this);
		Tail8_0.setRotationPoint(-3.0F, 12.0F, 4.0F);
		setRotationAngle(Tail8_0, -0.2618F, 0.0F, -1.1345F);
		Tail8_0.cubeList.add(new ModelBox(Tail8_0, 32, 32, -2.9063F, -7.5918F, -1.8906F, 4, 8, 4, 0.0F, false));

		Tail8_1 = new ModelRenderer(this);
		Tail8_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail8_0.addChild(Tail8_1);
		setRotationAngle(Tail8_1, 0.0F, 0.0F, 0.1745F);
		Tail8_1.cubeList.add(new ModelBox(Tail8_1, 32, 32, -2.8673F, -7.7643F, -1.8906F, 4, 8, 4, 0.3F, false));

		Tail8_2 = new ModelRenderer(this);
		Tail8_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail8_1.addChild(Tail8_2);
		setRotationAngle(Tail8_2, 0.0F, 0.0F, 0.1745F);
		Tail8_2.cubeList.add(new ModelBox(Tail8_2, 32, 32, -2.8216F, -7.6906F, -1.8906F, 4, 8, 4, 0.6F, false));

		Tail8_3 = new ModelRenderer(this);
		Tail8_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail8_2.addChild(Tail8_3);
		setRotationAngle(Tail8_3, 0.0F, 0.0F, 0.1745F);
		Tail8_3.cubeList.add(new ModelBox(Tail8_3, 32, 32, -2.7697F, -7.6211F, -1.8906F, 4, 8, 4, 0.8F, false));

		Tail8_4 = new ModelRenderer(this);
		Tail8_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail8_3.addChild(Tail8_4);
		setRotationAngle(Tail8_4, 0.0F, 0.0F, 0.1745F);
		Tail8_4.cubeList.add(new ModelBox(Tail8_4, 32, 32, -2.712F, -7.5564F, -1.8906F, 4, 8, 4, 0.4F, false));

		Tail8_5 = new ModelRenderer(this);
		Tail8_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail8_4.addChild(Tail8_5);
		setRotationAngle(Tail8_5, 0.0F, 0.0F, 0.1745F);
		Tail8_5.cubeList.add(new ModelBox(Tail8_5, 32, 32, -2.6488F, -7.4969F, -1.8906F, 4, 8, 4, 0.0F, false));

		Tail8_6 = new ModelRenderer(this);
		Tail8_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail8_5.addChild(Tail8_6);
		setRotationAngle(Tail8_6, 0.0F, 0.0F, 0.1745F);
		Tail8_6.cubeList.add(new ModelBox(Tail8_6, 32, 32, -2.5806F, -7.4432F, -1.8906F, 4, 8, 4, -0.4F, false));

		Tail8_7 = new ModelRenderer(this);
		Tail8_7.setRotationPoint(-0.5F, -6.5F, 0.0F);
		Tail8_6.addChild(Tail8_7);
		setRotationAngle(Tail8_7, 0.0F, 0.0F, 0.1745F);
		Tail8_7.cubeList.add(new ModelBox(Tail8_7, 32, 32, -2.0014F, -7.0376F, -1.8906F, 4, 8, 4, -0.8F, false));

		Tail9_0 = new ModelRenderer(this);
		Tail9_0.setRotationPoint(-4.0F, 12.0F, 5.0F);
		setRotationAngle(Tail9_0, -0.2618F, 0.0F, -1.4835F);
		Tail9_0.cubeList.add(new ModelBox(Tail9_0, 32, 32, -2.9962F, -7.9158F, -1.9774F, 4, 8, 4, 0.0F, false));

		Tail9_1 = new ModelRenderer(this);
		Tail9_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail9_0.addChild(Tail9_1);
		setRotationAngle(Tail9_1, 0.0F, 0.0F, 0.1745F);
		Tail9_1.cubeList.add(new ModelBox(Tail9_1, 32, 32, -2.9851F, -8.0792F, -1.9774F, 4, 8, 4, 0.3F, false));

		Tail9_2 = new ModelRenderer(this);
		Tail9_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail9_1.addChild(Tail9_2);
		setRotationAngle(Tail9_2, 0.0F, 0.0F, 0.1745F);
		Tail9_2.cubeList.add(new ModelBox(Tail9_2, 32, 32, -2.9664F, -7.994F, -1.9774F, 4, 8, 4, 0.6F, false));

		Tail9_3 = new ModelRenderer(this);
		Tail9_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail9_2.addChild(Tail9_3);
		setRotationAngle(Tail9_3, 0.0F, 0.0F, 0.1745F);
		Tail9_3.cubeList.add(new ModelBox(Tail9_3, 32, 32, -2.9404F, -7.9107F, -1.9774F, 4, 8, 4, 0.8F, false));

		Tail9_4 = new ModelRenderer(this);
		Tail9_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail9_3.addChild(Tail9_4);
		setRotationAngle(Tail9_4, 0.0F, 0.0F, 0.1745F);
		Tail9_4.cubeList.add(new ModelBox(Tail9_4, 32, 32, -2.9073F, -7.83F, -1.9774F, 4, 8, 4, 0.4F, false));

		Tail9_5 = new ModelRenderer(this);
		Tail9_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail9_4.addChild(Tail9_5);
		setRotationAngle(Tail9_5, 0.0F, 0.0F, 0.1745F);
		Tail9_5.cubeList.add(new ModelBox(Tail9_5, 32, 32, -2.8672F, -7.7525F, -1.9774F, 4, 8, 4, 0.0F, false));

		Tail9_6 = new ModelRenderer(this);
		Tail9_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail9_5.addChild(Tail9_6);
		setRotationAngle(Tail9_6, 0.0F, 0.0F, 0.1745F);
		Tail9_6.cubeList.add(new ModelBox(Tail9_6, 32, 32, -2.8205F, -7.6788F, -1.9774F, 4, 8, 4, -0.4F, false));

		Tail9_7 = new ModelRenderer(this);
		Tail9_7.setRotationPoint(-0.75F, -6.75F, 0.0F);
		Tail9_6.addChild(Tail9_7);
		setRotationAngle(Tail9_7, 0.0F, 0.0F, 0.1745F);
		Tail9_7.cubeList.add(new ModelBox(Tail9_7, 32, 32, -1.9517F, -7.0704F, -1.9774F, 4, 8, 4, -0.8F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
		bipedHeadwear.render(f5);
		bipedBody.render(f5);
		bipedRightArm.render(f5);
		bipedLeftArm.render(f5);
		bipedRightLeg.render(f5);
		bipedLeftLeg.render(f5);
		Tail1_0.render(f5);
		Tail2_0.render(f5);
		Tail3_0.render(f5);
		Tail4_0.render(f5);
		Tail5_0.render(f5);
		Tail6_0.render(f5);
		Tail7_0.render(f5);
		Tail8_0.render(f5);
		Tail9_0.render(f5);
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