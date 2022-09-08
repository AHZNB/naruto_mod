// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelTwoTails extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer Ears;
	private final ModelRenderer RightEar;
	private final ModelRenderer cube_r3;
	private final ModelRenderer LeftEar;
	private final ModelRenderer cube_r4;
	private final ModelRenderer Hair;
	private final ModelRenderer Hair4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer Hair3;
	private final ModelRenderer cube_r6;
	private final ModelRenderer Hair2;
	private final ModelRenderer cube_r7;
	private final ModelRenderer Hair7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer Hair8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer Hair9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer Hair10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer Hair5;
	private final ModelRenderer cube_r13;
	private final ModelRenderer Hair6;
	private final ModelRenderer cube_r14;
	private final ModelRenderer TopMouth;
	private final ModelRenderer cube_r15;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer nose;
	private final ModelRenderer cube_r18;
	private final ModelRenderer UpperTeeth;
	private final ModelRenderer cube_r19;
	private final ModelRenderer cube_r20;
	private final ModelRenderer cube_r21;
	private final ModelRenderer cube_r22;
	private final ModelRenderer cube_r23;
	private final ModelRenderer Jaw;
	private final ModelRenderer cube_r24;
	private final ModelRenderer cube_r25;
	private final ModelRenderer cube_r26;
	private final ModelRenderer cube_r27;
	private final ModelRenderer cube_r28;
	private final ModelRenderer cube_r29;
	private final ModelRenderer bone;
	private final ModelRenderer cube_r30;
	private final ModelRenderer cube_r31;
	private final ModelRenderer bone2;
	private final ModelRenderer cube_r32;
	private final ModelRenderer cube_r33;
	private final ModelRenderer bone3;
	private final ModelRenderer cube_r34;
	private final ModelRenderer cube_r35;
	private final ModelRenderer bone4;
	private final ModelRenderer cube_r36;
	private final ModelRenderer cube_r37;
	private final ModelRenderer LowerTeeth;
	private final ModelRenderer cube_r38;
	private final ModelRenderer eyes;
	private final ModelRenderer body;
	private final ModelRenderer cube_r39;
	private final ModelRenderer cube_r40;
	private final ModelRenderer cube_r41;
	private final ModelRenderer Tail0_0;
	private final ModelRenderer cube_r42;
	private final ModelRenderer Tail0_1;
	private final ModelRenderer cube_r43;
	private final ModelRenderer Tail0_2;
	private final ModelRenderer cube_r44;
	private final ModelRenderer Tail0_3;
	private final ModelRenderer cube_r45;
	private final ModelRenderer Tail0_4;
	private final ModelRenderer cube_r46;
	private final ModelRenderer Tail0_5;
	private final ModelRenderer cube_r47;
	private final ModelRenderer Tail0_6;
	private final ModelRenderer cube_r48;
	private final ModelRenderer Tail0_7;
	private final ModelRenderer cube_r49;
	private final ModelRenderer Tail1_0;
	private final ModelRenderer cube_r50;
	private final ModelRenderer Tail1_1;
	private final ModelRenderer cube_r51;
	private final ModelRenderer Tail1_2;
	private final ModelRenderer cube_r52;
	private final ModelRenderer Tail1_3;
	private final ModelRenderer cube_r53;
	private final ModelRenderer Tail1_4;
	private final ModelRenderer cube_r54;
	private final ModelRenderer Tail1_5;
	private final ModelRenderer cube_r55;
	private final ModelRenderer Tail1_6;
	private final ModelRenderer cube_r56;
	private final ModelRenderer Tail1_7;
	private final ModelRenderer cube_r57;
	private final ModelRenderer leg1;
	private final ModelRenderer Joint7;
	private final ModelRenderer cube_r58;
	private final ModelRenderer Joint8;
	private final ModelRenderer cube_r59;
	private final ModelRenderer Foot1;
	private final ModelRenderer cube_r60;
	private final ModelRenderer cube_r61;
	private final ModelRenderer cube_r62;
	private final ModelRenderer cube_r63;
	private final ModelRenderer leg2;
	private final ModelRenderer Joint2;
	private final ModelRenderer cube_r64;
	private final ModelRenderer Joint5;
	private final ModelRenderer cube_r65;
	private final ModelRenderer Foot2;
	private final ModelRenderer cube_r66;
	private final ModelRenderer cube_r67;
	private final ModelRenderer cube_r68;
	private final ModelRenderer cube_r69;
	private final ModelRenderer leg3;
	private final ModelRenderer Joint3;
	private final ModelRenderer cube_r70;
	private final ModelRenderer Joint4;
	private final ModelRenderer cube_r71;
	private final ModelRenderer Foot3;
	private final ModelRenderer cube_r72;
	private final ModelRenderer cube_r73;
	private final ModelRenderer cube_r74;
	private final ModelRenderer cube_r75;
	private final ModelRenderer leg4;
	private final ModelRenderer Joint6;
	private final ModelRenderer cube_r76;
	private final ModelRenderer Joint9;
	private final ModelRenderer cube_r77;
	private final ModelRenderer Foot4;
	private final ModelRenderer cube_r78;
	private final ModelRenderer cube_r79;
	private final ModelRenderer cube_r80;
	private final ModelRenderer cube_r81;

	public ModelTwoTails() {
		textureWidth = 128;
		textureHeight = 128;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 3.0F, -6.0F);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(6.0F, 2.75F, 4.0F);
		head.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 45, 43, -11.0F, -8.0F, -14.0F, 10, 2, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(6.0F, 9.75F, 4.25F);
		head.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 57, 20, -11.0F, -15.0F, -14.0F, 10, 10, 11, 0.0F, false));

		Ears = new ModelRenderer(this);
		Ears.setRotationPoint(11.75F, 5.25F, 0.25F);
		head.addChild(Ears);

		RightEar = new ModelRenderer(this);
		RightEar.setRotationPoint(-12.75F, 6.0F, -11.0F);
		Ears.addChild(RightEar);
		setRotationAngle(RightEar, -0.5744F, -0.3332F, -0.468F);
		RightEar.cubeList.add(new ModelBox(RightEar, 0, 8, 3.5F, -18.5F, -8.25F, 2, 6, 1, 0.0F, false));
		RightEar.cubeList.add(new ModelBox(RightEar, 0, 0, 4.3284F, -18.5F, -8.25F, 2, 6, 1, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(16.4047F, -0.6456F, 0.0F);
		RightEar.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.0F, -0.7854F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 28, 70, 3.5F, -21.75F, -8.25F, 2, 2, 1, 0.0F, false));

		LeftEar = new ModelRenderer(this);
		LeftEar.setRotationPoint(-10.75F, 6.0F, -11.0F);
		Ears.addChild(LeftEar);
		setRotationAngle(LeftEar, -0.5744F, 0.3332F, 0.468F);
		LeftEar.cubeList.add(new ModelBox(LeftEar, 0, 8, -5.5F, -18.5F, -8.25F, 2, 6, 1, 0.0F, true));
		LeftEar.cubeList.add(new ModelBox(LeftEar, 0, 0, -6.3284F, -18.5F, -8.25F, 2, 6, 1, 0.0F, true));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-16.4047F, -0.6456F, 0.0F);
		LeftEar.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, 0.7854F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 28, 70, -5.5F, -21.75F, -8.25F, 2, 2, 1, 0.0F, true));

		Hair = new ModelRenderer(this);
		Hair.setRotationPoint(11.75F, 5.25F, 0.25F);
		head.addChild(Hair);

		Hair4 = new ModelRenderer(this);
		Hair4.setRotationPoint(3.5F, 10.25F, -19.25F);
		Hair.addChild(Hair4);
		setRotationAngle(Hair4, -1.1345F, 0.0436F, -1.0036F);
		Hair4.cubeList.add(new ModelBox(Hair4, 28, 66, -1.6651F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, false));
		Hair4.cubeList.add(new ModelBox(Hair4, 42, 60, -0.8367F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
		Hair4.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, 0.7854F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 70, 0, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

		Hair3 = new ModelRenderer(this);
		Hair3.setRotationPoint(-27.0F, 10.25F, -19.25F);
		Hair.addChild(Hair3);
		setRotationAngle(Hair3, -1.1345F, -0.0436F, 1.0036F);
		Hair3.cubeList.add(new ModelBox(Hair3, 28, 66, -0.3349F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, true));
		Hair3.cubeList.add(new ModelBox(Hair3, 42, 60, -1.1633F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, true));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(15.8072F, -6.0753F, 0.0F);
		Hair3.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.0F, -0.7854F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 70, 0, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));

		Hair2 = new ModelRenderer(this);
		Hair2.setRotationPoint(3.5F, 8.25F, -10.5F);
		Hair.addChild(Hair2);
		setRotationAngle(Hair2, -0.829F, 0.0436F, -0.9163F);
		Hair2.cubeList.add(new ModelBox(Hair2, 0, 60, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, false));
		Hair2.cubeList.add(new ModelBox(Hair2, 6, 42, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
		Hair2.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.0F, 0.7854F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 70, 3, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

		Hair7 = new ModelRenderer(this);
		Hair7.setRotationPoint(-27.0F, 8.25F, -10.5F);
		Hair.addChild(Hair7);
		setRotationAngle(Hair7, -0.829F, -0.0436F, 0.9163F);
		Hair7.cubeList.add(new ModelBox(Hair7, 0, 60, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, true));
		Hair7.cubeList.add(new ModelBox(Hair7, 6, 42, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, true));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(15.8072F, -6.0753F, 0.0F);
		Hair7.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.0F, 0.0F, -0.7854F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 70, 3, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));

		Hair8 = new ModelRenderer(this);
		Hair8.setRotationPoint(5.75F, 5.25F, -10.5F);
		Hair.addChild(Hair8);
		setRotationAngle(Hair8, -0.829F, 0.0436F, -0.9163F);
		Hair8.cubeList.add(new ModelBox(Hair8, 45, 33, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, false));
		Hair8.cubeList.add(new ModelBox(Hair8, 10, 36, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
		Hair8.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, 0.0F, 0.7854F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 66, 41, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

		Hair9 = new ModelRenderer(this);
		Hair9.setRotationPoint(-29.25F, 5.25F, -10.5F);
		Hair.addChild(Hair9);
		setRotationAngle(Hair9, -0.829F, -0.0436F, 0.9163F);
		Hair9.cubeList.add(new ModelBox(Hair9, 45, 33, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, true));
		Hair9.cubeList.add(new ModelBox(Hair9, 10, 36, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, true));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(15.8072F, -6.0753F, 0.0F);
		Hair9.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0F, 0.0F, -0.7854F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 66, 41, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));

		Hair10 = new ModelRenderer(this);
		Hair10.setRotationPoint(-9.0F, -34.75F, 12.0F);
		Hair.addChild(Hair10);
		setRotationAngle(Hair10, -1.1781F, 0.0F, 0.0F);
		Hair10.cubeList.add(new ModelBox(Hair10, 9, 8, -3.3358F, 24.2726F, 14.5601F, 2, 5, 1, 0.0F, false));
		Hair10.cubeList.add(new ModelBox(Hair10, 9, 0, -4.1642F, 24.2726F, 14.5601F, 2, 5, 1, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(12.8063F, 39.8289F, 31.3101F);
		Hair10.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.0F, 0.0F, -0.7854F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 45, -0.6569F, -23.3431F, -14.6549F, 2, 2, 1, 0.0F, false));
		cube_r11.cubeList.add(new ModelBox(cube_r11, 45, 38, -3.1318F, -25.8179F, -14.6549F, 2, 2, 1, 0.0F, false));
		cube_r11.cubeList.add(new ModelBox(cube_r11, 35, 62, -2.6433F, -23.8315F, -15.3642F, 2, 2, 1, 0.0F, false));
		cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 65, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-18.3063F, 39.8289F, 31.3101F);
		Hair10.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, 0.0F, 0.7854F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 45, 38, 1.1318F, -25.8179F, -14.6549F, 2, 2, 1, 0.0F, true));
		cube_r12.cubeList.add(new ModelBox(cube_r12, 35, 62, 0.6433F, -23.8315F, -15.3642F, 2, 2, 1, 0.0F, true));

		Hair5 = new ModelRenderer(this);
		Hair5.setRotationPoint(4.25F, 9.75F, -10.5F);
		Hair.addChild(Hair5);
		setRotationAngle(Hair5, -0.829F, -0.3491F, -0.9163F);
		Hair5.cubeList.add(new ModelBox(Hair5, 52, 0, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, false));
		Hair5.cubeList.add(new ModelBox(Hair5, 0, 39, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
		Hair5.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.0F, 0.0F, 0.7854F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 68, 17, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

		Hair6 = new ModelRenderer(this);
		Hair6.setRotationPoint(-27.75F, 9.75F, -10.5F);
		Hair.addChild(Hair6);
		setRotationAngle(Hair6, -0.829F, 0.3491F, 0.9163F);
		Hair6.cubeList.add(new ModelBox(Hair6, 52, 0, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, true));
		Hair6.cubeList.add(new ModelBox(Hair6, 0, 39, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, true));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(15.8072F, -6.0753F, 0.0F);
		Hair6.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, 0.0F, -0.7854F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 68, 17, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));

		TopMouth = new ModelRenderer(this);
		TopMouth.setRotationPoint(-0.25F, 20.5F, 14.25F);
		head.addChild(TopMouth);

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(7.5815F, -13.5F, -19.9805F);
		TopMouth.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.0873F, 0.1309F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 88, 0, -5.0F, -8.0F, -10.0F, 2, 3, 7, 0.0F, true));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(-7.0815F, -13.5F, -19.9805F);
		TopMouth.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.0873F, -0.1309F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 88, 0, 3.0F, -8.0F, -10.0F, 2, 3, 7, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(0.25F, -20.4088F, -30.0418F);
		TopMouth.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.1745F, 0.0F, 0.0F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 69, 7, -3.0F, -0.1988F, -0.1005F, 6, 3, 7, 0.0F, false));

		nose = new ModelRenderer(this);
		nose.setRotationPoint(6.25F, -13.5F, -20.75F);
		TopMouth.addChild(nose);

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(0.0F, 0.0F, 0.0F);
		nose.addChild(cube_r18);
		setRotationAngle(cube_r18, 0.0873F, 0.0F, 0.0F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 55, 46, -7.0F, -8.0F, -9.0F, 2, 1, 1, 0.0F, false));

		UpperTeeth = new ModelRenderer(this);
		UpperTeeth.setRotationPoint(0.0F, -0.5F, -9.25F);
		TopMouth.addChild(UpperTeeth);

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(3.556F, -17.25F, -1.7657F);
		UpperTeeth.addChild(cube_r19);
		setRotationAngle(cube_r19, 3.1416F, 0.0F, -3.1416F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 31, 0.807F, -0.5F, 18.4824F, 1, 2, 0, 0.0F, false));

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(16.2956F, -17.0F, -19.249F);
		UpperTeeth.addChild(cube_r20);
		setRotationAngle(cube_r20, 0.0F, -1.5272F, 0.0F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 45, 46, -1.807F, -0.5F, 18.4824F, 5, 1, 0, 0.0F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(-15.7956F, -17.0F, -19.249F);
		UpperTeeth.addChild(cube_r21);
		setRotationAngle(cube_r21, 0.0F, 1.5272F, 0.0F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 45, 47, -3.193F, -0.5F, 18.4824F, 5, 1, 0, 0.0F, false));

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(0.556F, -17.0F, -1.7657F);
		UpperTeeth.addChild(cube_r22);
		setRotationAngle(cube_r22, 3.1416F, 0.0F, -3.1416F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 9, 6, -1.193F, -0.5F, 18.4824F, 3, 1, 0, 0.0F, false));

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(-0.444F, -17.25F, -1.7657F);
		UpperTeeth.addChild(cube_r23);
		setRotationAngle(cube_r23, 3.1416F, 0.0F, -3.1416F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 14, 31, 0.807F, -0.5F, 18.4824F, 1, 2, 0, 0.0F, false));

		Jaw = new ModelRenderer(this);
		Jaw.setRotationPoint(-0.0749F, 3.6113F, -9.4922F);
		head.addChild(Jaw);
		setRotationAngle(Jaw, 0.5236F, 0.0F, 0.0F);

		cube_r24 = new ModelRenderer(this);
		cube_r24.setRotationPoint(6.0749F, 8.1425F, -10.4545F);
		Jaw.addChild(cube_r24);
		setRotationAngle(cube_r24, 0.1745F, 0.0F, 0.0F);
		cube_r24.cubeList.add(new ModelBox(cube_r24, 20, 62, -9.0F, -6.0F, 9.0F, 6, 1, 3, 0.0F, false));

		cube_r25 = new ModelRenderer(this);
		cube_r25.setRotationPoint(6.0749F, 9.3049F, 2.6281F);
		Jaw.addChild(cube_r25);
		setRotationAngle(cube_r25, -0.3054F, 0.0F, 0.0F);
		cube_r25.cubeList.add(new ModelBox(cube_r25, 0, 34, -9.0F, -6.0F, -10.0F, 6, 1, 1, 0.0F, false));

		cube_r26 = new ModelRenderer(this);
		cube_r26.setRotationPoint(6.0749F, 5.9394F, 5.9459F);
		Jaw.addChild(cube_r26);
		setRotationAngle(cube_r26, 0.0873F, 0.0F, 0.0F);
		cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 31, -9.0F, -6.0F, -10.0F, 6, 1, 2, 0.0F, false));

		cube_r27 = new ModelRenderer(this);
		cube_r27.setRotationPoint(-6.8275F, 5.6387F, 4.6908F);
		Jaw.addChild(cube_r27);
		setRotationAngle(cube_r27, 0.0873F, -0.0873F, 0.0F);
		cube_r27.cubeList.add(new ModelBox(cube_r27, 0, 0, 3.0F, -6.0F, -10.0F, 1, 1, 7, 0.0F, false));

		cube_r28 = new ModelRenderer(this);
		cube_r28.setRotationPoint(6.9773F, 5.6387F, 4.6908F);
		Jaw.addChild(cube_r28);
		setRotationAngle(cube_r28, 0.0873F, 0.0873F, 0.0F);
		cube_r28.cubeList.add(new ModelBox(cube_r28, 0, 8, -4.0F, -6.0F, -10.0F, 1, 1, 7, 0.0F, false));

		cube_r29 = new ModelRenderer(this);
		cube_r29.setRotationPoint(6.0749F, 5.6387F, 4.9922F);
		Jaw.addChild(cube_r29);
		setRotationAngle(cube_r29, 0.0873F, 0.0F, 0.0F);
		cube_r29.cubeList.add(new ModelBox(cube_r29, 69, 84, -9.0F, -6.0F, -10.0F, 6, 1, 7, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(8.2288F, -3.2172F, 4.8801F);
		Jaw.addChild(bone);

		cube_r30 = new ModelRenderer(this);
		cube_r30.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone.addChild(cube_r30);
		setRotationAngle(cube_r30, 0.1809F, 0.1538F, -0.7744F);
		cube_r30.cubeList.add(new ModelBox(cube_r30, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

		cube_r31 = new ModelRenderer(this);
		cube_r31.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
		bone.addChild(cube_r31);
		setRotationAngle(cube_r31, 0.0564F, 0.0308F, -0.7859F);
		cube_r31.cubeList.add(new ModelBox(cube_r31, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(9.2288F, -3.2172F, 4.8801F);
		Jaw.addChild(bone2);

		cube_r32 = new ModelRenderer(this);
		cube_r32.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone2.addChild(cube_r32);
		setRotationAngle(cube_r32, 0.1809F, 0.1538F, -0.7744F);
		cube_r32.cubeList.add(new ModelBox(cube_r32, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

		cube_r33 = new ModelRenderer(this);
		cube_r33.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
		bone2.addChild(cube_r33);
		setRotationAngle(cube_r33, 0.0564F, 0.0308F, -0.7859F);
		cube_r33.cubeList.add(new ModelBox(cube_r33, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(10.2288F, -3.2172F, 4.8801F);
		Jaw.addChild(bone3);

		cube_r34 = new ModelRenderer(this);
		cube_r34.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone3.addChild(cube_r34);
		setRotationAngle(cube_r34, 0.1809F, 0.1538F, -0.7744F);
		cube_r34.cubeList.add(new ModelBox(cube_r34, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

		cube_r35 = new ModelRenderer(this);
		cube_r35.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
		bone3.addChild(cube_r35);
		setRotationAngle(cube_r35, 0.0564F, 0.0308F, -0.7859F);
		cube_r35.cubeList.add(new ModelBox(cube_r35, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(11.2288F, -3.2172F, 4.8801F);
		Jaw.addChild(bone4);

		cube_r36 = new ModelRenderer(this);
		cube_r36.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone4.addChild(cube_r36);
		setRotationAngle(cube_r36, 0.1809F, 0.1538F, -0.7744F);
		cube_r36.cubeList.add(new ModelBox(cube_r36, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

		cube_r37 = new ModelRenderer(this);
		cube_r37.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
		bone4.addChild(cube_r37);
		setRotationAngle(cube_r37, 0.0564F, 0.0308F, -0.7859F);
		cube_r37.cubeList.add(new ModelBox(cube_r37, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

		LowerTeeth = new ModelRenderer(this);
		LowerTeeth.setRotationPoint(-18.1751F, 15.1387F, -4.0078F);
		Jaw.addChild(LowerTeeth);
		setRotationAngle(LowerTeeth, -3.1416F, -1.5272F, 3.1416F);
		LowerTeeth.cubeList.add(new ModelBox(LowerTeeth, 60, 15, -2.1529F, -15.5F, -20.6758F, 4, 1, 0, 0.0F, false));
		LowerTeeth.cubeList.add(new ModelBox(LowerTeeth, 52, 15, -1.9348F, -15.5F, -15.6805F, 4, 1, 0, 0.0F, false));

		cube_r38 = new ModelRenderer(this);
		cube_r38.setRotationPoint(16.4522F, -15.25F, -18.291F);
		LowerTeeth.addChild(cube_r38);
		setRotationAngle(cube_r38, 0.0F, -1.5272F, 0.0F);
		cube_r38.cubeList.add(new ModelBox(cube_r38, 52, 14, -3.193F, -0.25F, 18.4824F, 5, 1, 0, 0.0F, false));

		eyes = new ModelRenderer(this);
		eyes.setRotationPoint(0.0F, 3.0F, -6.0F);
		eyes.cubeList.add(new ModelBox(eyes, 76, 0, -4.0F, -3.0F, -9.8F, 8, 2, 0, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 3.0F, 0.0F);

		cube_r39 = new ModelRenderer(this);
		cube_r39.setRotationPoint(6.0F, 8.0F, 4.25F);
		body.addChild(cube_r39);
		setRotationAngle(cube_r39, 0.0F, 0.0F, 0.0F);
		cube_r39.cubeList.add(new ModelBox(cube_r39, 0, 31, -13.0F, -12.0F, 5.0F, 14, 12, 17, 0.0F, false));

		cube_r40 = new ModelRenderer(this);
		cube_r40.setRotationPoint(6.0F, 8.0F, -8.0F);
		body.addChild(cube_r40);
		setRotationAngle(cube_r40, -0.0436F, 0.0F, 0.0F);
		cube_r40.cubeList.add(new ModelBox(cube_r40, 48, 46, -14.0F, -14.0F, 14.0F, 16, 14, 14, 0.0F, false));

		cube_r41 = new ModelRenderer(this);
		cube_r41.setRotationPoint(6.0F, 8.0F, -13.5F);
		body.addChild(cube_r41);
		setRotationAngle(cube_r41, -0.0873F, 0.0F, 0.0F);
		cube_r41.cubeList.add(new ModelBox(cube_r41, 0, 0, -15.0F, -16.0F, 5.0F, 18, 15, 16, 0.0F, false));

		Tail0_0 = new ModelRenderer(this);
		Tail0_0.setRotationPoint(-2.75F, 1.0F, 26.25F);
		body.addChild(Tail0_0);
		setRotationAngle(Tail0_0, -1.309F, -0.7854F, 0.0F);

		cube_r42 = new ModelRenderer(this);
		cube_r42.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail0_0.addChild(cube_r42);
		setRotationAngle(cube_r42, 0.0F, 0.0F, 0.0F);
		cube_r42.cubeList.add(new ModelBox(cube_r42, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.0F, false));

		Tail0_1 = new ModelRenderer(this);
		Tail0_1.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail0_0.addChild(Tail0_1);
		setRotationAngle(Tail0_1, 0.2618F, 0.0F, 0.0873F);

		cube_r43 = new ModelRenderer(this);
		cube_r43.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail0_1.addChild(cube_r43);
		setRotationAngle(cube_r43, 0.0F, 0.0F, 0.0F);
		cube_r43.cubeList.add(new ModelBox(cube_r43, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.2F, false));

		Tail0_2 = new ModelRenderer(this);
		Tail0_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail0_1.addChild(Tail0_2);
		setRotationAngle(Tail0_2, 0.2618F, 0.0F, 0.0873F);

		cube_r44 = new ModelRenderer(this);
		cube_r44.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail0_2.addChild(cube_r44);
		setRotationAngle(cube_r44, 0.0F, 0.0F, 0.0F);
		cube_r44.cubeList.add(new ModelBox(cube_r44, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.4F, false));

		Tail0_3 = new ModelRenderer(this);
		Tail0_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail0_2.addChild(Tail0_3);
		setRotationAngle(Tail0_3, 0.2618F, 0.0F, 0.0873F);

		cube_r45 = new ModelRenderer(this);
		cube_r45.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail0_3.addChild(cube_r45);
		setRotationAngle(cube_r45, 0.0F, 0.0F, 0.0F);
		cube_r45.cubeList.add(new ModelBox(cube_r45, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.5F, false));

		Tail0_4 = new ModelRenderer(this);
		Tail0_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail0_3.addChild(Tail0_4);
		setRotationAngle(Tail0_4, -0.2618F, 0.0F, 0.0873F);

		cube_r46 = new ModelRenderer(this);
		cube_r46.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail0_4.addChild(cube_r46);
		setRotationAngle(cube_r46, 0.0F, 0.0F, 0.0F);
		cube_r46.cubeList.add(new ModelBox(cube_r46, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.3F, false));

		Tail0_5 = new ModelRenderer(this);
		Tail0_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail0_4.addChild(Tail0_5);
		setRotationAngle(Tail0_5, -0.2618F, 0.0F, 0.0873F);

		cube_r47 = new ModelRenderer(this);
		cube_r47.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail0_5.addChild(cube_r47);
		setRotationAngle(cube_r47, 0.0F, 0.0F, 0.0F);
		cube_r47.cubeList.add(new ModelBox(cube_r47, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.1F, false));

		Tail0_6 = new ModelRenderer(this);
		Tail0_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail0_5.addChild(Tail0_6);
		setRotationAngle(Tail0_6, -0.2618F, 0.0F, 0.0873F);

		cube_r48 = new ModelRenderer(this);
		cube_r48.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail0_6.addChild(cube_r48);
		setRotationAngle(cube_r48, 0.0F, 0.0F, 0.0F);
		cube_r48.cubeList.add(new ModelBox(cube_r48, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, -0.3F, false));

		Tail0_7 = new ModelRenderer(this);
		Tail0_7.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail0_6.addChild(Tail0_7);
		setRotationAngle(Tail0_7, -0.2618F, 0.0F, 0.0873F);

		cube_r49 = new ModelRenderer(this);
		cube_r49.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail0_7.addChild(cube_r49);
		setRotationAngle(cube_r49, 0.0F, 0.0F, 0.0F);
		cube_r49.cubeList.add(new ModelBox(cube_r49, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, -0.8F, false));

		Tail1_0 = new ModelRenderer(this);
		Tail1_0.setRotationPoint(3.25F, 1.0F, 26.25F);
		body.addChild(Tail1_0);
		setRotationAngle(Tail1_0, -1.3963F, 0.7854F, 0.0F);

		cube_r50 = new ModelRenderer(this);
		cube_r50.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail1_0.addChild(cube_r50);
		setRotationAngle(cube_r50, 0.0F, 0.0F, 0.0F);
		cube_r50.cubeList.add(new ModelBox(cube_r50, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.0F, false));

		Tail1_1 = new ModelRenderer(this);
		Tail1_1.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail1_0.addChild(Tail1_1);
		setRotationAngle(Tail1_1, -0.2618F, 0.0F, -0.0873F);

		cube_r51 = new ModelRenderer(this);
		cube_r51.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail1_1.addChild(cube_r51);
		setRotationAngle(cube_r51, 0.0F, 0.0F, 0.0F);
		cube_r51.cubeList.add(new ModelBox(cube_r51, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.2F, false));

		Tail1_2 = new ModelRenderer(this);
		Tail1_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail1_1.addChild(Tail1_2);
		setRotationAngle(Tail1_2, 0.2618F, 0.0F, -0.0873F);

		cube_r52 = new ModelRenderer(this);
		cube_r52.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail1_2.addChild(cube_r52);
		setRotationAngle(cube_r52, 0.0F, 0.0F, 0.0F);
		cube_r52.cubeList.add(new ModelBox(cube_r52, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.4F, false));

		Tail1_3 = new ModelRenderer(this);
		Tail1_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail1_2.addChild(Tail1_3);
		setRotationAngle(Tail1_3, 0.2618F, 0.0F, -0.0873F);

		cube_r53 = new ModelRenderer(this);
		cube_r53.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail1_3.addChild(cube_r53);
		setRotationAngle(cube_r53, 0.0F, 0.0F, 0.0F);
		cube_r53.cubeList.add(new ModelBox(cube_r53, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.5F, false));

		Tail1_4 = new ModelRenderer(this);
		Tail1_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail1_3.addChild(Tail1_4);
		setRotationAngle(Tail1_4, 0.2618F, 0.0F, -0.0873F);

		cube_r54 = new ModelRenderer(this);
		cube_r54.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail1_4.addChild(cube_r54);
		setRotationAngle(cube_r54, 0.0F, 0.0F, 0.0F);
		cube_r54.cubeList.add(new ModelBox(cube_r54, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.3F, false));

		Tail1_5 = new ModelRenderer(this);
		Tail1_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail1_4.addChild(Tail1_5);
		setRotationAngle(Tail1_5, 0.2618F, 0.0F, -0.0873F);

		cube_r55 = new ModelRenderer(this);
		cube_r55.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail1_5.addChild(cube_r55);
		setRotationAngle(cube_r55, 0.0F, 0.0F, 0.0F);
		cube_r55.cubeList.add(new ModelBox(cube_r55, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, 0.1F, false));

		Tail1_6 = new ModelRenderer(this);
		Tail1_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail1_5.addChild(Tail1_6);
		setRotationAngle(Tail1_6, -0.2618F, 0.0F, -0.0873F);

		cube_r56 = new ModelRenderer(this);
		cube_r56.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail1_6.addChild(cube_r56);
		setRotationAngle(cube_r56, 0.0F, 0.0F, 0.0F);
		cube_r56.cubeList.add(new ModelBox(cube_r56, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, -0.3F, false));

		Tail1_7 = new ModelRenderer(this);
		Tail1_7.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail1_6.addChild(Tail1_7);
		setRotationAngle(Tail1_7, -0.2618F, 0.0F, -0.0873F);

		cube_r57 = new ModelRenderer(this);
		cube_r57.setRotationPoint(-4.0F, 16.0F, -29.75F);
		Tail1_7.addChild(cube_r57);
		setRotationAngle(cube_r57, 0.0F, 0.0F, 0.0F);
		cube_r57.cubeList.add(new ModelBox(cube_r57, 52, 0, 1.0F, -23.0F, 26.75F, 6, 8, 6, -0.8F, false));

		leg1 = new ModelRenderer(this);
		leg1.setRotationPoint(-4.75F, 4.0F, -1.0F);

		Joint7 = new ModelRenderer(this);
		Joint7.setRotationPoint(0.0F, -2.0F, -1.0F);
		leg1.addChild(Joint7);
		setRotationAngle(Joint7, 0.1745F, 0.0F, 0.0F);

		cube_r58 = new ModelRenderer(this);
		cube_r58.setRotationPoint(2.0F, 12.0F, -11.25F);
		Joint7.addChild(cube_r58);
		setRotationAngle(cube_r58, -0.0436F, 0.0F, 0.0F);
		cube_r58.cubeList.add(new ModelBox(cube_r58, 28, 66, -8.0F, -12.0F, 7.0F, 6, 14, 8, 0.0F, false));

		Joint8 = new ModelRenderer(this);
		Joint8.setRotationPoint(-2.875F, 15.25F, 0.75F);
		Joint7.addChild(Joint8);
		setRotationAngle(Joint8, -0.5236F, 0.0F, 0.0F);

		cube_r59 = new ModelRenderer(this);
		cube_r59.setRotationPoint(4.375F, 5.75F, -14.75F);
		Joint8.addChild(cube_r59);
		setRotationAngle(cube_r59, 0.0F, 0.0F, 0.0F);
		cube_r59.cubeList.add(new ModelBox(cube_r59, 0, 82, -7.0F, -8.0F, 11.0F, 5, 10, 5, 0.0F, false));

		Foot1 = new ModelRenderer(this);
		Foot1.setRotationPoint(-0.125F, 5.9375F, 0.4375F);
		Joint8.addChild(Foot1);
		setRotationAngle(Foot1, 0.3491F, 0.0F, 0.0F);

		cube_r60 = new ModelRenderer(this);
		cube_r60.setRotationPoint(6.5F, 2.0625F, -22.4375F);
		Foot1.addChild(cube_r60);
		setRotationAngle(cube_r60, 0.0F, 0.0F, 0.0F);
		cube_r60.cubeList.add(new ModelBox(cube_r60, 6, 39, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

		cube_r61 = new ModelRenderer(this);
		cube_r61.setRotationPoint(8.0F, 2.0625F, -22.4375F);
		Foot1.addChild(cube_r61);
		setRotationAngle(cube_r61, 0.0F, 0.0F, 0.0F);
		cube_r61.cubeList.add(new ModelBox(cube_r61, 12, 42, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

		cube_r62 = new ModelRenderer(this);
		cube_r62.setRotationPoint(5.0F, 2.0625F, -22.4375F);
		Foot1.addChild(cube_r62);
		setRotationAngle(cube_r62, 0.0F, 0.0F, 0.0F);
		cube_r62.cubeList.add(new ModelBox(cube_r62, 12, 45, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

		cube_r63 = new ModelRenderer(this);
		cube_r63.setRotationPoint(4.5F, 1.8125F, -15.1875F);
		Foot1.addChild(cube_r63);
		setRotationAngle(cube_r63, 0.0F, 0.0F, 0.0F);
		cube_r63.cubeList.add(new ModelBox(cube_r63, 76, 74, -7.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, false));

		leg2 = new ModelRenderer(this);
		leg2.setRotationPoint(4.75F, 4.0F, -1.0F);

		Joint2 = new ModelRenderer(this);
		Joint2.setRotationPoint(0.0F, -2.0F, -1.0F);
		leg2.addChild(Joint2);
		setRotationAngle(Joint2, 0.1745F, 0.0F, 0.0F);

		cube_r64 = new ModelRenderer(this);
		cube_r64.setRotationPoint(-2.0F, 12.0F, -11.25F);
		Joint2.addChild(cube_r64);
		setRotationAngle(cube_r64, -0.0436F, 0.0F, 0.0F);
		cube_r64.cubeList.add(new ModelBox(cube_r64, 28, 66, 2.0F, -12.0F, 7.0F, 6, 14, 8, 0.0F, true));

		Joint5 = new ModelRenderer(this);
		Joint5.setRotationPoint(2.875F, 15.25F, 0.75F);
		Joint2.addChild(Joint5);
		setRotationAngle(Joint5, -0.5236F, 0.0F, 0.0F);

		cube_r65 = new ModelRenderer(this);
		cube_r65.setRotationPoint(-4.375F, 5.75F, -14.75F);
		Joint5.addChild(cube_r65);
		setRotationAngle(cube_r65, 0.0F, 0.0F, 0.0F);
		cube_r65.cubeList.add(new ModelBox(cube_r65, 0, 82, 2.0F, -8.0F, 11.0F, 5, 10, 5, 0.0F, true));

		Foot2 = new ModelRenderer(this);
		Foot2.setRotationPoint(0.125F, 5.9375F, 0.4375F);
		Joint5.addChild(Foot2);
		setRotationAngle(Foot2, 0.3491F, 0.0F, 0.0F);

		cube_r66 = new ModelRenderer(this);
		cube_r66.setRotationPoint(-6.5F, 2.0625F, -22.4375F);
		Foot2.addChild(cube_r66);
		setRotationAngle(cube_r66, 0.0F, 0.0F, 0.0F);
		cube_r66.cubeList.add(new ModelBox(cube_r66, 6, 39, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

		cube_r67 = new ModelRenderer(this);
		cube_r67.setRotationPoint(-8.0F, 2.0625F, -22.4375F);
		Foot2.addChild(cube_r67);
		setRotationAngle(cube_r67, 0.0F, 0.0F, 0.0F);
		cube_r67.cubeList.add(new ModelBox(cube_r67, 12, 42, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

		cube_r68 = new ModelRenderer(this);
		cube_r68.setRotationPoint(-5.0F, 2.0625F, -22.4375F);
		Foot2.addChild(cube_r68);
		setRotationAngle(cube_r68, 0.0F, 0.0F, 0.0F);
		cube_r68.cubeList.add(new ModelBox(cube_r68, 12, 45, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

		cube_r69 = new ModelRenderer(this);
		cube_r69.setRotationPoint(-4.5F, 1.8125F, -15.1875F);
		Foot2.addChild(cube_r69);
		setRotationAngle(cube_r69, 0.0F, 0.0F, 0.0F);
		cube_r69.cubeList.add(new ModelBox(cube_r69, 76, 74, 2.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, true));

		leg3 = new ModelRenderer(this);
		leg3.setRotationPoint(-5.5F, 8.0F, 21.5F);

		Joint3 = new ModelRenderer(this);
		Joint3.setRotationPoint(0.0F, 0.0F, 0.0F);
		leg3.addChild(Joint3);
		setRotationAngle(Joint3, -1.5708F, 0.0F, 0.0F);

		cube_r70 = new ModelRenderer(this);
		cube_r70.setRotationPoint(2.25F, 17.5F, -2.75F);
		Joint3.addChild(cube_r70);
		setRotationAngle(cube_r70, 0.7854F, 0.0F, 0.0F);
		cube_r70.cubeList.add(new ModelBox(cube_r70, 0, 60, -8.0F, -14.3536F, 10.182F, 6, 14, 8, 0.2F, false));

		Joint4 = new ModelRenderer(this);
		Joint4.setRotationPoint(1.25F, 17.5F, -2.75F);
		Joint3.addChild(Joint4);

		cube_r71 = new ModelRenderer(this);
		cube_r71.setRotationPoint(-10.0F, 27.5103F, 8.3355F);
		Joint4.addChild(cube_r71);
		setRotationAngle(cube_r71, -1.1781F, 0.0F, 0.0F);
		cube_r71.cubeList.add(new ModelBox(cube_r71, 56, 74, 3.0F, -24.6324F, -38.7863F, 5, 11, 5, 0.0F, false));

		Foot3 = new ModelRenderer(this);
		Foot3.setRotationPoint(-4.5F, -16.9625F, 16.4375F);
		Joint4.addChild(Foot3);
		setRotationAngle(Foot3, 1.5708F, 0.0F, 0.0F);

		cube_r72 = new ModelRenderer(this);
		cube_r72.setRotationPoint(6.5F, 2.0625F, -22.4375F);
		Foot3.addChild(cube_r72);
		setRotationAngle(cube_r72, 0.0F, 0.0F, 0.0F);
		cube_r72.cubeList.add(new ModelBox(cube_r72, 6, 39, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

		cube_r73 = new ModelRenderer(this);
		cube_r73.setRotationPoint(8.0F, 2.0625F, -22.4375F);
		Foot3.addChild(cube_r73);
		setRotationAngle(cube_r73, 0.0F, 0.0F, 0.0F);
		cube_r73.cubeList.add(new ModelBox(cube_r73, 12, 42, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

		cube_r74 = new ModelRenderer(this);
		cube_r74.setRotationPoint(5.0F, 2.0625F, -22.4375F);
		Foot3.addChild(cube_r74);
		setRotationAngle(cube_r74, 0.0F, 0.0F, 0.0F);
		cube_r74.cubeList.add(new ModelBox(cube_r74, 12, 45, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

		cube_r75 = new ModelRenderer(this);
		cube_r75.setRotationPoint(4.5F, 1.8125F, -15.1875F);
		Foot3.addChild(cube_r75);
		setRotationAngle(cube_r75, 0.0F, 0.0F, 0.0F);
		cube_r75.cubeList.add(new ModelBox(cube_r75, 76, 74, -7.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, false));

		leg4 = new ModelRenderer(this);
		leg4.setRotationPoint(5.5F, 8.0F, 21.5F);

		Joint6 = new ModelRenderer(this);
		Joint6.setRotationPoint(0.0F, 0.0F, 0.0F);
		leg4.addChild(Joint6);
		setRotationAngle(Joint6, -1.5708F, 0.0F, 0.0F);

		cube_r76 = new ModelRenderer(this);
		cube_r76.setRotationPoint(-2.25F, 17.5F, -2.75F);
		Joint6.addChild(cube_r76);
		setRotationAngle(cube_r76, 0.7854F, 0.0F, 0.0F);
		cube_r76.cubeList.add(new ModelBox(cube_r76, 0, 60, 2.0F, -14.3536F, 10.182F, 6, 14, 8, 0.2F, true));

		Joint9 = new ModelRenderer(this);
		Joint9.setRotationPoint(-1.25F, 17.5F, -2.75F);
		Joint6.addChild(Joint9);

		cube_r77 = new ModelRenderer(this);
		cube_r77.setRotationPoint(10.0F, 27.5103F, 8.3355F);
		Joint9.addChild(cube_r77);
		setRotationAngle(cube_r77, -1.1781F, 0.0F, 0.0F);
		cube_r77.cubeList.add(new ModelBox(cube_r77, 56, 74, -8.0F, -24.6324F, -38.7863F, 5, 11, 5, 0.0F, true));

		Foot4 = new ModelRenderer(this);
		Foot4.setRotationPoint(4.5F, -16.9625F, 16.4375F);
		Joint9.addChild(Foot4);
		setRotationAngle(Foot4, 1.5708F, 0.0F, 0.0F);

		cube_r78 = new ModelRenderer(this);
		cube_r78.setRotationPoint(-6.5F, 2.0625F, -22.4375F);
		Foot4.addChild(cube_r78);
		setRotationAngle(cube_r78, 0.0F, 0.0F, 0.0F);
		cube_r78.cubeList.add(new ModelBox(cube_r78, 6, 39, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

		cube_r79 = new ModelRenderer(this);
		cube_r79.setRotationPoint(-8.0F, 2.0625F, -22.4375F);
		Foot4.addChild(cube_r79);
		setRotationAngle(cube_r79, 0.0F, 0.0F, 0.0F);
		cube_r79.cubeList.add(new ModelBox(cube_r79, 12, 42, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

		cube_r80 = new ModelRenderer(this);
		cube_r80.setRotationPoint(-5.0F, 2.0625F, -22.4375F);
		Foot4.addChild(cube_r80);
		setRotationAngle(cube_r80, 0.0F, 0.0F, 0.0F);
		cube_r80.cubeList.add(new ModelBox(cube_r80, 12, 45, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

		cube_r81 = new ModelRenderer(this);
		cube_r81.setRotationPoint(-4.5F, 1.8125F, -15.1875F);
		Foot4.addChild(cube_r81);
		setRotationAngle(cube_r81, 0.0F, 0.0F, 0.0F);
		cube_r81.cubeList.add(new ModelBox(cube_r81, 76, 74, 2.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
		eyes.render(f5);
		body.render(f5);
		leg1.render(f5);
		leg2.render(f5);
		leg3.render(f5);
		leg4.render(f5);
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