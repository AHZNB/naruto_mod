// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelFourTails extends ModelBase {
	private final ModelRenderer Head;
	private final ModelRenderer bone;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r5;
	private final ModelRenderer bone2;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r6;
	private final ModelRenderer tooth1;
	private final ModelRenderer cube_r7;
	private final ModelRenderer tooth2;
	private final ModelRenderer cube_r8;
	private final ModelRenderer Jaw;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer Body;
	private final ModelRenderer pec6_r1;
	private final ModelRenderer pec2_r1;
	private final ModelRenderer RIghtArm;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer cube_r13;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer cube_r18;
	private final ModelRenderer cube_r19;
	private final ModelRenderer LeftArm;
	private final ModelRenderer cube_r26;
	private final ModelRenderer cube_r27;
	private final ModelRenderer cube_r28;
	private final ModelRenderer cube_r29;
	private final ModelRenderer cube_r30;
	private final ModelRenderer cube_r31;
	private final ModelRenderer cube_r32;
	private final ModelRenderer cube_r33;
	private final ModelRenderer cube_r34;
	private final ModelRenderer RightLeg;
	private final ModelRenderer cube_r20;
	private final ModelRenderer cube_r21;
	private final ModelRenderer cube_r22;
	private final ModelRenderer cube_r23;
	private final ModelRenderer cube_r24;
	private final ModelRenderer cube_r25;
	private final ModelRenderer LeftLeg;
	private final ModelRenderer cube_r35;
	private final ModelRenderer cube_r36;
	private final ModelRenderer cube_r37;
	private final ModelRenderer cube_r38;
	private final ModelRenderer cube_r39;
	private final ModelRenderer cube_r40;
	private final ModelRenderer Tail0_0;
	private final ModelRenderer Tail0_1;
	private final ModelRenderer Tail0_2;
	private final ModelRenderer Tail0_3;
	private final ModelRenderer Tail0_4;
	private final ModelRenderer Tail0_5;
	private final ModelRenderer Tail1_0;
	private final ModelRenderer Tail1_1;
	private final ModelRenderer Tail1_2;
	private final ModelRenderer Tail1_3;
	private final ModelRenderer Tail1_4;
	private final ModelRenderer Tail1_5;
	private final ModelRenderer Tail2_0;
	private final ModelRenderer Tail2_1;
	private final ModelRenderer Tail2_2;
	private final ModelRenderer Tail2_3;
	private final ModelRenderer Tail2_4;
	private final ModelRenderer Tail2_5;
	private final ModelRenderer Tail3_0;
	private final ModelRenderer Tail3_1;
	private final ModelRenderer Tail3_2;
	private final ModelRenderer Tail3_3;
	private final ModelRenderer Tail3_4;
	private final ModelRenderer Tail3_5;

	public ModelFourTails() {
		textureWidth = 64;
		textureHeight = 64;

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 13.0F, -3.0F);
		Head.cubeList.add(new ModelBox(Head, 18, 28, -1.9F, -4.0F, -2.2F, 4, 4, 4, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 21, 0, 1.4F, -1.3F, -2.5F, 1, 3, 1, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 0, 0, -2.35F, -1.3F, -2.5F, 1, 3, 1, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 48, 46, -0.9F, -1.3F, -2.5F, 2, 1, 1, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 6, 55, -1.5F, -0.05F, -2.3F, 3, 1, 1, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 53, 27, -2.0F, 1.35F, -2.5F, 1, 1, 1, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 25, 52, -1.75F, 1.85F, -2.0F, 1, 1, 1, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 52, 16, 0.75F, 1.85F, -2.0F, 1, 1, 1, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 51, 52, 1.0F, 1.35F, -2.5F, 1, 1, 1, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 27, 46, -1.5F, -0.3F, -1.75F, 3, 2, 1, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 11.95F, 2.75F);
		Head.addChild(bone);
		bone.cubeList.add(new ModelBox(bone, 53, 45, -1.2F, -15.7F, -5.5F, 1, 1, 1, 0.0F, false));
		bone.cubeList.add(new ModelBox(bone, 28, 50, -2.2F, -15.5F, -5.4F, 2, 1, 1, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-3.3F, -15.0F, -4.9F);
		bone.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 0.0F, 0.1745F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 22, 50, -0.2F, -0.8F, -0.5F, 2, 1, 1, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-3.1F, -15.6F, -4.9F);
		bone.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, 1.1781F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 50, -1.4F, -0.25F, -0.5F, 2, 1, 1, -0.1F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 11.95F, 2.75F);
		Head.addChild(bone2);
		bone2.cubeList.add(new ModelBox(bone2, 53, 45, 0.2F, -15.7F, -5.5F, 1, 1, 1, 0.0F, true));
		bone2.cubeList.add(new ModelBox(bone2, 28, 50, 0.2F, -15.5F, -5.4F, 2, 1, 1, 0.0F, true));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(3.3F, -15.0F, -4.9F);
		bone2.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, -0.1745F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 22, 50, -1.8F, -0.8F, -0.5F, 2, 1, 1, 0.0F, true));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(3.1F, -15.6F, -4.9F);
		bone2.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, -1.1781F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 50, -0.6F, -0.25F, -0.5F, 2, 1, 1, -0.1F, true));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(1.7162F, -0.7121F, -2.5F);
		Head.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 0.7854F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 53, 29, -2.75F, 1.4F, 0.0F, 1, 1, 1, 0.0F, false));
		cube_r1.cubeList.add(new ModelBox(cube_r1, 44, 53, -0.9F, -0.45F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(2.1F, -2.4F, 0.1F);
		Head.addChild(cube_r6);
		setRotationAngle(cube_r6, -0.1745F, 0.0F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 21, 52, -0.7F, -1.0F, -0.5F, 1, 2, 1, 0.0F, true));
		cube_r6.cubeList.add(new ModelBox(cube_r6, 21, 52, -4.3F, -1.0F, -0.5F, 1, 2, 1, 0.0F, false));

		tooth1 = new ModelRenderer(this);
		tooth1.setRotationPoint(-1.0F, 0.7F, -3.0F);
		Head.addChild(tooth1);
		setRotationAngle(tooth1, -0.0873F, 0.0F, 0.0F);

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
		tooth1.addChild(cube_r7);
		setRotationAngle(cube_r7, -0.1745F, 0.1745F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 52, 43, -0.5F, -0.5F, 0.0F, 1, 1, 1, -0.2F, false));
		cube_r7.cubeList.add(new ModelBox(cube_r7, 50, 41, -0.5F, 0.05F, 0.0F, 1, 1, 1, -0.25F, false));
		cube_r7.cubeList.add(new ModelBox(cube_r7, 37, 48, -0.5F, 0.45F, 0.0F, 1, 1, 1, -0.3F, false));

		tooth2 = new ModelRenderer(this);
		tooth2.setRotationPoint(1.0F, 0.7F, -3.0F);
		Head.addChild(tooth2);
		setRotationAngle(tooth2, -0.0873F, 0.0F, 0.0F);

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
		tooth2.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.1745F, -0.1745F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 52, 43, -0.5F, -0.5F, 0.0F, 1, 1, 1, -0.2F, true));
		cube_r8.cubeList.add(new ModelBox(cube_r8, 50, 41, -0.5F, 0.05F, 0.0F, 1, 1, 1, -0.25F, true));
		cube_r8.cubeList.add(new ModelBox(cube_r8, 37, 48, -0.5F, 0.45F, 0.0F, 1, 1, 1, -0.3F, true));

		Jaw = new ModelRenderer(this);
		Jaw.setRotationPoint(0.0F, -0.3F, 0.0F);
		Head.addChild(Jaw);
		Jaw.cubeList.add(new ModelBox(Jaw, 30, 28, -1.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F, false));
		Jaw.cubeList.add(new ModelBox(Jaw, 0, 28, 0.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F, false));
		Jaw.cubeList.add(new ModelBox(Jaw, 0, 48, -1.5021F, 1.6088F, -2.6F, 3, 1, 1, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, 3.0F, -2.0F);
		Jaw.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, 0.0F, -0.7854F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 20, -1.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-1.0F, 5.5F, -4.0F);
		Jaw.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.2182F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 36, 45, -0.5F, -3.5F, 3.0F, 3, 1, 2, 0.0F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 13.0F, -3.0F);
		bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 5, 54, -1.9F, -2.5F, -2.25F, 4, 1, 0, 0.0F, false));

		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, 13.0F, 0.0F);
		Body.cubeList.add(new ModelBox(Body, 0, 28, -2.5F, 0.0F, -2.0F, 5, 3, 4, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 22, 11, -2.5F, 3.0F, -1.5F, 5, 1, 4, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 38, 20, -2.5F, 4.0F, -0.6F, 5, 1, 3, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 36, 8, -2.5F, 5.0F, 0.4F, 5, 1, 3, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 44, 14, -2.5F, 0.0F, -3.0F, 5, 1, 1, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 44, 12, -2.5F, 1.0F, -2.6F, 5, 1, 1, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 0, 9, -5.05F, -3.3F, -1.2F, 5, 5, 6, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 0, 9, 0.05F, -3.3F, -1.2F, 5, 5, 6, 0.0F, true));
		Body.cubeList.add(new ModelBox(Body, 0, 0, -4.0F, -5.0F, -1.0F, 8, 4, 5, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 18, 16, -3.0F, -2.2F, 1.8F, 6, 8, 4, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 18, 1, -4.1F, 1.0F, -2.0F, 2, 2, 8, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 18, 1, 2.1F, 1.0F, -2.0F, 2, 2, 8, 0.0F, true));
		Body.cubeList.add(new ModelBox(Body, 0, 20, -3.9F, 3.0F, -1.3F, 2, 1, 7, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 0, 20, 1.9F, 3.0F, -1.3F, 2, 1, 7, 0.0F, true));
		Body.cubeList.add(new ModelBox(Body, 30, 0, -3.0F, -1.0F, 5.75F, 6, 7, 1, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 0, 35, -3.0F, 0.0F, 6.75F, 6, 6, 1, 0.0F, false));
		Body.cubeList.add(new ModelBox(Body, 26, 39, -2.5F, 1.0F, 7.75F, 5, 6, 1, 0.0F, false));

		pec6_r1 = new ModelRenderer(this);
		pec6_r1.setRotationPoint(1.5F, 1.9301F, -2.4586F);
		Body.addChild(pec6_r1);
		setRotationAngle(pec6_r1, 0.6545F, 0.0F, 0.0F);
		pec6_r1.cubeList.add(new ModelBox(pec6_r1, 49, 8, -1.5F, 3.0699F, -0.5414F, 2, 2, 1, -0.1F, true));
		pec6_r1.cubeList.add(new ModelBox(pec6_r1, 49, 8, -3.5F, 3.0699F, -0.5414F, 2, 2, 1, -0.1F, false));
		pec6_r1.cubeList.add(new ModelBox(pec6_r1, 47, 35, -1.6F, 1.0699F, -0.7914F, 3, 2, 1, -0.2F, true));
		pec6_r1.cubeList.add(new ModelBox(pec6_r1, 47, 35, -4.4F, 1.0699F, -0.7914F, 3, 2, 1, -0.2F, false));

		pec2_r1 = new ModelRenderer(this);
		pec2_r1.setRotationPoint(1.5F, 2.0F, -2.5F);
		Body.addChild(pec2_r1);
		setRotationAngle(pec2_r1, 0.6545F, 0.0F, 0.0F);
		pec2_r1.cubeList.add(new ModelBox(pec2_r1, 47, 38, -1.5F, -1.0F, -0.5F, 3, 2, 1, -0.05F, true));
		pec2_r1.cubeList.add(new ModelBox(pec2_r1, 47, 38, -4.5F, -1.0F, -0.5F, 3, 2, 1, -0.05F, false));

		RIghtArm = new ModelRenderer(this);
		RIghtArm.setRotationPoint(-5.25F, 12.25F, 0.5F);
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 44, 29, -2.65F, -0.55F, -1.6F, 3, 3, 3, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 0, 42, -3.05F, -0.55F, -0.5F, 3, 3, 3, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 38, 39, -3.05F, -0.55F, -1.6F, 3, 3, 3, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 22, 46, -1.4F, -1.75F, -1.6F, 1, 1, 3, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 22, 46, -1.4F, -1.5F, -1.6F, 1, 1, 3, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 43, 45, -0.65F, -2.1F, -1.6F, 1, 2, 3, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 14, 28, -2.15F, -1.2F, -1.6F, 1, 1, 3, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 13, 52, -0.25F, 9.75F, -5.5F, 1, 2, 1, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 9, 51, -0.25F, 8.85F, -5.5F, 1, 2, 1, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 51, 48, -1.35F, 9.75F, -6.0F, 1, 2, 1, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 5, 51, -1.35F, 9.05F, -6.0F, 1, 2, 1, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 33, 51, -2.45F, 9.75F, -6.0F, 1, 2, 1, 0.0F, false));
		RIghtArm.cubeList.add(new ModelBox(RIghtArm, 51, 0, -2.45F, 9.15F, -6.0F, 1, 2, 1, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(2.75F, 10.75F, 3.1F);
		RIghtArm.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.0F, -0.7418F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 9, -6.5F, -2.5F, -4.1F, 1, 1, 2, 0.0F, false));
		cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 12, -6.5F, -2.5F, -4.9F, 1, 1, 2, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-3.05F, 8.75F, -5.0F);
		RIghtArm.addChild(cube_r12);
		setRotationAngle(cube_r12, -0.6545F, 0.6545F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 51, 24, -0.5F, -1.2F, -0.1F, 1, 2, 1, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(-1.25F, 8.75F, -5.0F);
		RIghtArm.addChild(cube_r13);
		setRotationAngle(cube_r13, -0.6545F, 0.0F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 37, 51, -1.2F, -1.0F, -0.5F, 1, 2, 1, 0.0F, false));
		cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 52, -0.1F, -1.0F, -0.5F, 1, 2, 1, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(-3.45F, 10.15F, -5.1F);
		RIghtArm.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, 0.4363F, 0.0F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 48, 50, -0.2F, -0.9F, -0.3F, 1, 2, 1, 0.0F, false));
		cube_r14.cubeList.add(new ModelBox(cube_r14, 51, 19, -0.2F, -0.4F, -0.3F, 1, 2, 1, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(1.45F, 9.25F, -5.0F);
		RIghtArm.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.0F, -0.3054F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 36, 12, -0.2F, 0.5F, -0.5F, 1, 2, 1, 0.0F, false));
		cube_r15.cubeList.add(new ModelBox(cube_r15, 44, 50, -0.2F, -1.0F, -0.5F, 1, 2, 1, 0.0F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(0.25F, 8.75F, -5.0F);
		RIghtArm.addChild(cube_r16);
		setRotationAngle(cube_r16, -0.6109F, 0.0F, -0.5236F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 17, 52, -0.8F, -1.6F, -0.5F, 1, 2, 1, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(-3.35F, 7.9553F, -3.4708F);
		RIghtArm.addChild(cube_r17);
		setRotationAngle(cube_r17, -0.5672F, 0.0F, 0.0F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 16, 11, 1.0F, 0.0F, -1.2F, 2, 1, 3, 0.0F, false));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(-1.35F, 5.65F, -1.2F);
		RIghtArm.addChild(cube_r18);
		setRotationAngle(cube_r18, -0.5672F, 0.0F, 0.0F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 44, 0, -1.4F, -1.0F, -1.9F, 2, 5, 3, 0.0F, false));
		cube_r18.cubeList.add(new ModelBox(cube_r18, 12, 44, -1.0F, -1.0F, -1.9F, 2, 5, 3, 0.0F, false));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(-1.45F, 3.5387F, 0.7532F);
		RIghtArm.addChild(cube_r19);
		setRotationAngle(cube_r19, -0.3491F, 0.0F, 0.0F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 34, 24, -1.3F, -2.0F, -2.9F, 2, 4, 4, 0.0F, false));
		cube_r19.cubeList.add(new ModelBox(cube_r19, 36, 12, -0.9F, -2.0F, -2.9F, 2, 4, 4, 0.0F, false));

		LeftArm = new ModelRenderer(this);
		LeftArm.setRotationPoint(5.25F, 12.25F, 0.5F);
		LeftArm.cubeList.add(new ModelBox(LeftArm, 44, 29, -0.35F, -0.55F, -1.6F, 3, 3, 3, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 0, 42, 0.05F, -0.55F, -0.5F, 3, 3, 3, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 38, 39, 0.05F, -0.55F, -1.6F, 3, 3, 3, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 22, 46, 0.4F, -1.75F, -1.6F, 1, 1, 3, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 22, 46, 0.4F, -1.5F, -1.6F, 1, 1, 3, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 43, 45, -0.35F, -2.1F, -1.6F, 1, 2, 3, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 14, 28, 1.15F, -1.2F, -1.6F, 1, 1, 3, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 13, 52, -0.75F, 9.75F, -5.5F, 1, 2, 1, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 9, 51, -0.75F, 8.85F, -5.5F, 1, 2, 1, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 51, 48, 0.35F, 9.75F, -6.0F, 1, 2, 1, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 5, 51, 0.35F, 9.05F, -6.0F, 1, 2, 1, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 33, 51, 1.45F, 9.75F, -6.0F, 1, 2, 1, 0.0F, true));
		LeftArm.cubeList.add(new ModelBox(LeftArm, 51, 0, 1.45F, 9.15F, -6.0F, 1, 2, 1, 0.0F, true));

		cube_r26 = new ModelRenderer(this);
		cube_r26.setRotationPoint(-2.75F, 10.75F, 3.1F);
		LeftArm.addChild(cube_r26);
		setRotationAngle(cube_r26, 0.0F, 0.7418F, 0.0F);
		cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 9, 5.5F, -2.5F, -4.1F, 1, 1, 2, 0.0F, true));
		cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 12, 5.5F, -2.5F, -4.9F, 1, 1, 2, 0.0F, true));

		cube_r27 = new ModelRenderer(this);
		cube_r27.setRotationPoint(3.05F, 8.75F, -5.0F);
		LeftArm.addChild(cube_r27);
		setRotationAngle(cube_r27, -0.6545F, -0.6545F, 0.0F);
		cube_r27.cubeList.add(new ModelBox(cube_r27, 51, 24, -0.5F, -1.2F, -0.1F, 1, 2, 1, 0.0F, true));

		cube_r28 = new ModelRenderer(this);
		cube_r28.setRotationPoint(1.25F, 8.75F, -5.0F);
		LeftArm.addChild(cube_r28);
		setRotationAngle(cube_r28, -0.6545F, 0.0F, 0.0F);
		cube_r28.cubeList.add(new ModelBox(cube_r28, 37, 51, 0.2F, -1.0F, -0.5F, 1, 2, 1, 0.0F, true));
		cube_r28.cubeList.add(new ModelBox(cube_r28, 0, 52, -0.9F, -1.0F, -0.5F, 1, 2, 1, 0.0F, true));

		cube_r29 = new ModelRenderer(this);
		cube_r29.setRotationPoint(3.45F, 10.15F, -5.1F);
		LeftArm.addChild(cube_r29);
		setRotationAngle(cube_r29, 0.0F, -0.4363F, 0.0F);
		cube_r29.cubeList.add(new ModelBox(cube_r29, 48, 50, -0.8F, -0.9F, -0.3F, 1, 2, 1, 0.0F, true));
		cube_r29.cubeList.add(new ModelBox(cube_r29, 51, 19, -0.8F, -0.4F, -0.3F, 1, 2, 1, 0.0F, true));

		cube_r30 = new ModelRenderer(this);
		cube_r30.setRotationPoint(-1.45F, 9.25F, -5.0F);
		LeftArm.addChild(cube_r30);
		setRotationAngle(cube_r30, 0.0F, 0.3054F, 0.0F);
		cube_r30.cubeList.add(new ModelBox(cube_r30, 36, 12, -0.8F, 0.5F, -0.5F, 1, 2, 1, 0.0F, true));
		cube_r30.cubeList.add(new ModelBox(cube_r30, 44, 50, -0.8F, -1.0F, -0.5F, 1, 2, 1, 0.0F, true));

		cube_r31 = new ModelRenderer(this);
		cube_r31.setRotationPoint(-0.25F, 8.75F, -5.0F);
		LeftArm.addChild(cube_r31);
		setRotationAngle(cube_r31, -0.6109F, 0.0F, 0.5236F);
		cube_r31.cubeList.add(new ModelBox(cube_r31, 17, 52, -0.2F, -1.6F, -0.5F, 1, 2, 1, 0.0F, true));

		cube_r32 = new ModelRenderer(this);
		cube_r32.setRotationPoint(3.35F, 7.9553F, -3.4708F);
		LeftArm.addChild(cube_r32);
		setRotationAngle(cube_r32, -0.5672F, 0.0F, 0.0F);
		cube_r32.cubeList.add(new ModelBox(cube_r32, 16, 11, -3.0F, 0.0F, -1.2F, 2, 1, 3, 0.0F, true));

		cube_r33 = new ModelRenderer(this);
		cube_r33.setRotationPoint(1.35F, 5.65F, -1.2F);
		LeftArm.addChild(cube_r33);
		setRotationAngle(cube_r33, -0.5672F, 0.0F, 0.0F);
		cube_r33.cubeList.add(new ModelBox(cube_r33, 44, 0, -0.6F, -1.0F, -1.9F, 2, 5, 3, 0.0F, true));
		cube_r33.cubeList.add(new ModelBox(cube_r33, 12, 44, -1.0F, -1.0F, -1.9F, 2, 5, 3, 0.0F, true));

		cube_r34 = new ModelRenderer(this);
		cube_r34.setRotationPoint(1.45F, 3.5387F, 0.7532F);
		LeftArm.addChild(cube_r34);
		setRotationAngle(cube_r34, -0.3491F, 0.0F, 0.0F);
		cube_r34.cubeList.add(new ModelBox(cube_r34, 34, 24, -0.7F, -2.0F, -2.9F, 2, 4, 4, 0.0F, true));
		cube_r34.cubeList.add(new ModelBox(cube_r34, 36, 12, -1.1F, -2.0F, -2.9F, 2, 4, 4, 0.0F, true));

		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(-1.5F, 19.5F, 6.75F);
		RightLeg.cubeList.add(new ModelBox(RightLeg, 42, 24, -6.5F, 3.5F, -3.85F, 3, 1, 3, 0.0F, false));
		RightLeg.cubeList.add(new ModelBox(RightLeg, 14, 36, -6.5F, -1.1F, -3.85F, 3, 5, 3, 0.0F, false));
		RightLeg.cubeList.add(new ModelBox(RightLeg, 33, 48, -6.3F, 3.0F, -4.15F, 1, 1, 2, 0.0F, false));
		RightLeg.cubeList.add(new ModelBox(RightLeg, 6, 48, -5.2F, 3.0F, -4.15F, 1, 1, 2, 0.0F, false));
		RightLeg.cubeList.add(new ModelBox(RightLeg, 11, 23, -4.1F, 3.0F, -4.15F, 1, 1, 2, 0.0F, false));

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(-2.5F, 3.5F, -3.15F);
		RightLeg.addChild(cube_r20);
		setRotationAngle(cube_r20, 0.0F, -0.6981F, 0.0F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 0, 23, -0.5F, -0.5F, -0.2F, 1, 1, 2, 0.0F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(-7.0F, 3.5F, -3.15F);
		RightLeg.addChild(cube_r21);
		setRotationAngle(cube_r21, 0.0F, 0.5672F, 0.0F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 39, 48, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, false));

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(-2.2F, 3.4F, -6.25F);
		RightLeg.addChild(cube_r22);
		setRotationAngle(cube_r22, 0.0F, -0.4363F, 0.0873F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 11, 20, 0.6F, -0.3F, 0.8F, 1, 1, 2, 0.0F, false));

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(-6.0F, 4.0F, -3.75F);
		RightLeg.addChild(cube_r23);
		setRotationAngle(cube_r23, 0.2182F, 0.0F, 0.0F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 9, 42, 1.9F, -1.1F, -2.2F, 1, 1, 2, 0.0F, false));
		cube_r23.cubeList.add(new ModelBox(cube_r23, 19, 44, 0.8F, -1.1F, -2.2F, 1, 1, 2, 0.0F, false));
		cube_r23.cubeList.add(new ModelBox(cube_r23, 48, 16, -0.3F, -1.1F, -2.2F, 1, 1, 2, 0.0F, false));

		cube_r24 = new ModelRenderer(this);
		cube_r24.setRotationPoint(-7.7113F, 3.6739F, -4.2028F);
		RightLeg.addChild(cube_r24);
		setRotationAngle(cube_r24, 0.2182F, 0.5672F, -0.0873F);
		cube_r24.cubeList.add(new ModelBox(cube_r24, 48, 43, -0.5F, -0.5F, -1.0F, 1, 1, 2, 0.0F, false));

		cube_r25 = new ModelRenderer(this);
		cube_r25.setRotationPoint(-2.5F, 0.7F, -2.05F);
		RightLeg.addChild(cube_r25);
		setRotationAngle(cube_r25, 0.0F, 0.0F, -0.1745F);
		cube_r25.cubeList.add(new ModelBox(cube_r25, 31, 33, -2.0F, -1.8F, 0.5F, 5, 3, 3, 0.0F, false));

		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(1.5F, 19.5F, 6.75F);
		LeftLeg.cubeList.add(new ModelBox(LeftLeg, 42, 24, 3.5F, 3.5F, -3.85F, 3, 1, 3, 0.0F, true));
		LeftLeg.cubeList.add(new ModelBox(LeftLeg, 14, 36, 3.5F, -1.1F, -3.85F, 3, 5, 3, 0.0F, true));
		LeftLeg.cubeList.add(new ModelBox(LeftLeg, 33, 48, 5.3F, 3.0F, -4.15F, 1, 1, 2, 0.0F, true));
		LeftLeg.cubeList.add(new ModelBox(LeftLeg, 6, 48, 4.2F, 3.0F, -4.15F, 1, 1, 2, 0.0F, true));
		LeftLeg.cubeList.add(new ModelBox(LeftLeg, 11, 23, 3.1F, 3.0F, -4.15F, 1, 1, 2, 0.0F, true));

		cube_r35 = new ModelRenderer(this);
		cube_r35.setRotationPoint(2.5F, 3.5F, -3.15F);
		LeftLeg.addChild(cube_r35);
		setRotationAngle(cube_r35, 0.0F, 0.6981F, 0.0F);
		cube_r35.cubeList.add(new ModelBox(cube_r35, 0, 23, -0.5F, -0.5F, -0.2F, 1, 1, 2, 0.0F, true));

		cube_r36 = new ModelRenderer(this);
		cube_r36.setRotationPoint(7.0F, 3.5F, -3.15F);
		LeftLeg.addChild(cube_r36);
		setRotationAngle(cube_r36, 0.0F, -0.5672F, 0.0F);
		cube_r36.cubeList.add(new ModelBox(cube_r36, 39, 48, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, true));

		cube_r37 = new ModelRenderer(this);
		cube_r37.setRotationPoint(2.2F, 3.4F, -6.25F);
		LeftLeg.addChild(cube_r37);
		setRotationAngle(cube_r37, 0.0F, 0.4363F, -0.0873F);
		cube_r37.cubeList.add(new ModelBox(cube_r37, 11, 20, -1.6F, -0.3F, 0.8F, 1, 1, 2, 0.0F, true));

		cube_r38 = new ModelRenderer(this);
		cube_r38.setRotationPoint(6.0F, 4.0F, -3.75F);
		LeftLeg.addChild(cube_r38);
		setRotationAngle(cube_r38, 0.2182F, 0.0F, 0.0F);
		cube_r38.cubeList.add(new ModelBox(cube_r38, 9, 42, -2.9F, -1.1F, -2.2F, 1, 1, 2, 0.0F, true));
		cube_r38.cubeList.add(new ModelBox(cube_r38, 19, 44, -1.8F, -1.1F, -2.2F, 1, 1, 2, 0.0F, true));
		cube_r38.cubeList.add(new ModelBox(cube_r38, 48, 16, -0.7F, -1.1F, -2.2F, 1, 1, 2, 0.0F, true));

		cube_r39 = new ModelRenderer(this);
		cube_r39.setRotationPoint(7.7113F, 3.6739F, -4.2028F);
		LeftLeg.addChild(cube_r39);
		setRotationAngle(cube_r39, 0.2182F, -0.5672F, 0.0873F);
		cube_r39.cubeList.add(new ModelBox(cube_r39, 48, 43, -0.5F, -0.5F, -1.0F, 1, 1, 2, 0.0F, true));

		cube_r40 = new ModelRenderer(this);
		cube_r40.setRotationPoint(2.5F, 0.7F, -2.05F);
		LeftLeg.addChild(cube_r40);
		setRotationAngle(cube_r40, 0.0F, 0.0F, 0.1745F);
		cube_r40.cubeList.add(new ModelBox(cube_r40, 31, 33, -3.0F, -1.8F, 0.5F, 5, 3, 3, 0.0F, true));

		Tail0_0 = new ModelRenderer(this);
		Tail0_0.setRotationPoint(1.5F, 17.5F, 9.0F);
		setRotationAngle(Tail0_0, -1.0472F, 1.0472F, 0.0F);
		Tail0_0.cubeList.add(new ModelBox(Tail0_0, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
		Tail0_0.cubeList.add(new ModelBox(Tail0_0, 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));

		Tail0_1 = new ModelRenderer(this);
		Tail0_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_0.addChild(Tail0_1);
		setRotationAngle(Tail0_1, 0.2618F, 0.0F, 0.0F);
		Tail0_1.cubeList.add(new ModelBox(Tail0_1, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
		Tail0_1.cubeList.add(new ModelBox(Tail0_1, 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));

		Tail0_2 = new ModelRenderer(this);
		Tail0_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_1.addChild(Tail0_2);
		setRotationAngle(Tail0_2, 0.2618F, 0.0F, 0.0F);
		Tail0_2.cubeList.add(new ModelBox(Tail0_2, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
		Tail0_2.cubeList.add(new ModelBox(Tail0_2, 0, 61, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));

		Tail0_3 = new ModelRenderer(this);
		Tail0_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_2.addChild(Tail0_3);
		setRotationAngle(Tail0_3, 0.2618F, 0.0F, 0.0F);
		Tail0_3.cubeList.add(new ModelBox(Tail0_3, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		Tail0_3.cubeList.add(new ModelBox(Tail0_3, 0, 61, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));

		Tail0_4 = new ModelRenderer(this);
		Tail0_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_3.addChild(Tail0_4);
		setRotationAngle(Tail0_4, -0.2618F, 0.0F, 0.0F);
		Tail0_4.cubeList.add(new ModelBox(Tail0_4, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		Tail0_4.cubeList.add(new ModelBox(Tail0_4, 0, 61, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));

		Tail0_5 = new ModelRenderer(this);
		Tail0_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_4.addChild(Tail0_5);
		setRotationAngle(Tail0_5, -0.2618F, 0.0F, 0.0F);
		Tail0_5.cubeList.add(new ModelBox(Tail0_5, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		Tail0_5.cubeList.add(new ModelBox(Tail0_5, 0, 61, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));

		Tail1_0 = new ModelRenderer(this);
		Tail1_0.setRotationPoint(0.5F, 17.5F, 9.0F);
		setRotationAngle(Tail1_0, -1.0472F, 0.3491F, 0.0F);
		Tail1_0.cubeList.add(new ModelBox(Tail1_0, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
		Tail1_0.cubeList.add(new ModelBox(Tail1_0, 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));

		Tail1_1 = new ModelRenderer(this);
		Tail1_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_0.addChild(Tail1_1);
		setRotationAngle(Tail1_1, 0.2618F, 0.0F, 0.0F);
		Tail1_1.cubeList.add(new ModelBox(Tail1_1, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
		Tail1_1.cubeList.add(new ModelBox(Tail1_1, 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));

		Tail1_2 = new ModelRenderer(this);
		Tail1_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_1.addChild(Tail1_2);
		setRotationAngle(Tail1_2, 0.2618F, 0.0F, 0.0F);
		Tail1_2.cubeList.add(new ModelBox(Tail1_2, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
		Tail1_2.cubeList.add(new ModelBox(Tail1_2, 0, 61, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));

		Tail1_3 = new ModelRenderer(this);
		Tail1_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_2.addChild(Tail1_3);
		setRotationAngle(Tail1_3, 0.2618F, 0.0F, 0.0F);
		Tail1_3.cubeList.add(new ModelBox(Tail1_3, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		Tail1_3.cubeList.add(new ModelBox(Tail1_3, 0, 61, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));

		Tail1_4 = new ModelRenderer(this);
		Tail1_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_3.addChild(Tail1_4);
		setRotationAngle(Tail1_4, -0.2618F, 0.0F, 0.0F);
		Tail1_4.cubeList.add(new ModelBox(Tail1_4, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		Tail1_4.cubeList.add(new ModelBox(Tail1_4, 0, 61, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));

		Tail1_5 = new ModelRenderer(this);
		Tail1_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_4.addChild(Tail1_5);
		setRotationAngle(Tail1_5, -0.2618F, 0.0F, 0.0F);
		Tail1_5.cubeList.add(new ModelBox(Tail1_5, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		Tail1_5.cubeList.add(new ModelBox(Tail1_5, 0, 61, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));

		Tail2_0 = new ModelRenderer(this);
		Tail2_0.setRotationPoint(-0.5F, 17.5F, 9.0F);
		setRotationAngle(Tail2_0, -1.0472F, -0.3491F, 0.0F);
		Tail2_0.cubeList.add(new ModelBox(Tail2_0, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
		Tail2_0.cubeList.add(new ModelBox(Tail2_0, 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));

		Tail2_1 = new ModelRenderer(this);
		Tail2_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_0.addChild(Tail2_1);
		setRotationAngle(Tail2_1, 0.2618F, 0.0F, 0.0F);
		Tail2_1.cubeList.add(new ModelBox(Tail2_1, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
		Tail2_1.cubeList.add(new ModelBox(Tail2_1, 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));

		Tail2_2 = new ModelRenderer(this);
		Tail2_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_1.addChild(Tail2_2);
		setRotationAngle(Tail2_2, 0.2618F, 0.0F, 0.0F);
		Tail2_2.cubeList.add(new ModelBox(Tail2_2, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
		Tail2_2.cubeList.add(new ModelBox(Tail2_2, 0, 61, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));

		Tail2_3 = new ModelRenderer(this);
		Tail2_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_2.addChild(Tail2_3);
		setRotationAngle(Tail2_3, 0.2618F, 0.0F, 0.0F);
		Tail2_3.cubeList.add(new ModelBox(Tail2_3, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		Tail2_3.cubeList.add(new ModelBox(Tail2_3, 0, 61, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));

		Tail2_4 = new ModelRenderer(this);
		Tail2_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_3.addChild(Tail2_4);
		setRotationAngle(Tail2_4, -0.2618F, 0.0F, 0.0F);
		Tail2_4.cubeList.add(new ModelBox(Tail2_4, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		Tail2_4.cubeList.add(new ModelBox(Tail2_4, 0, 61, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));

		Tail2_5 = new ModelRenderer(this);
		Tail2_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_4.addChild(Tail2_5);
		setRotationAngle(Tail2_5, -0.2618F, 0.0F, 0.0F);
		Tail2_5.cubeList.add(new ModelBox(Tail2_5, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		Tail2_5.cubeList.add(new ModelBox(Tail2_5, 0, 61, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));

		Tail3_0 = new ModelRenderer(this);
		Tail3_0.setRotationPoint(-1.5F, 17.5F, 9.0F);
		setRotationAngle(Tail3_0, -1.0472F, -1.0472F, 0.0F);
		Tail3_0.cubeList.add(new ModelBox(Tail3_0, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
		Tail3_0.cubeList.add(new ModelBox(Tail3_0, 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));

		Tail3_1 = new ModelRenderer(this);
		Tail3_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_0.addChild(Tail3_1);
		setRotationAngle(Tail3_1, 0.2618F, 0.0F, 0.0F);
		Tail3_1.cubeList.add(new ModelBox(Tail3_1, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
		Tail3_1.cubeList.add(new ModelBox(Tail3_1, 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));

		Tail3_2 = new ModelRenderer(this);
		Tail3_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_1.addChild(Tail3_2);
		setRotationAngle(Tail3_2, 0.2618F, 0.0F, 0.0F);
		Tail3_2.cubeList.add(new ModelBox(Tail3_2, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
		Tail3_2.cubeList.add(new ModelBox(Tail3_2, 0, 61, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));

		Tail3_3 = new ModelRenderer(this);
		Tail3_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_2.addChild(Tail3_3);
		setRotationAngle(Tail3_3, 0.2618F, 0.0F, 0.0F);
		Tail3_3.cubeList.add(new ModelBox(Tail3_3, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		Tail3_3.cubeList.add(new ModelBox(Tail3_3, 0, 61, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));

		Tail3_4 = new ModelRenderer(this);
		Tail3_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_3.addChild(Tail3_4);
		setRotationAngle(Tail3_4, -0.2618F, 0.0F, 0.0F);
		Tail3_4.cubeList.add(new ModelBox(Tail3_4, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		Tail3_4.cubeList.add(new ModelBox(Tail3_4, 0, 61, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));

		Tail3_5 = new ModelRenderer(this);
		Tail3_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_4.addChild(Tail3_5);
		setRotationAngle(Tail3_5, -0.2618F, 0.0F, 0.0F);
		Tail3_5.cubeList.add(new ModelBox(Tail3_5, 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		Tail3_5.cubeList.add(new ModelBox(Tail3_5, 0, 61, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Head.render(f5);
		bipedHeadwear.render(f5);
		Body.render(f5);
		RIghtArm.render(f5);
		LeftArm.render(f5);
		RightLeg.render(f5);
		LeftLeg.render(f5);
		Tail0_0.render(f5);
		Tail1_0.render(f5);
		Tail2_0.render(f5);
		Tail3_0.render(f5);
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