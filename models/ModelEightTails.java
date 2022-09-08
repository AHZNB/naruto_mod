// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelEightTails extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer hornRight1;
	private final ModelRenderer hornRight2;
	private final ModelRenderer hornRight3;
	private final ModelRenderer hornRight4;
	private final ModelRenderer hornRight5;
	private final ModelRenderer hornRight6;
	private final ModelRenderer hornRight13;
	private final ModelRenderer hornRight14;
	private final ModelRenderer hornRight15;
	private final ModelRenderer hornRight16;
	private final ModelRenderer hornRight17;
	private final ModelRenderer hornRight18;
	private final ModelRenderer hornRight19;
	private final ModelRenderer hornRight20;
	private final ModelRenderer hornRight21;
	private final ModelRenderer hornRight22;
	private final ModelRenderer hornRight23;
	private final ModelRenderer hornRight24;
	private final ModelRenderer hornRight7;
	private final ModelRenderer hornRight8;
	private final ModelRenderer hornRight9;
	private final ModelRenderer hornRight10;
	private final ModelRenderer hornRight11;
	private final ModelRenderer hornRight12;
	private final ModelRenderer snout;
	private final ModelRenderer bone3;
	private final ModelRenderer bone6;
	private final ModelRenderer bone;
	private final ModelRenderer bone4;
	private final ModelRenderer bone2;
	private final ModelRenderer bone5;
	private final ModelRenderer jaw;
	private final ModelRenderer bone8;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer bipedBody;
	private final ModelRenderer chest;
	private final ModelRenderer hump;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer upperArmRight;
	private final ModelRenderer foreArmRight;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer upperArmLeft;
	private final ModelRenderer foreArmLeft;
	private final ModelRenderer Tail0_0;
	private final ModelRenderer Tail0_1;
	private final ModelRenderer Tail0_2;
	private final ModelRenderer Tail0_3;
	private final ModelRenderer Tail0_4;
	private final ModelRenderer Tail0_5;
	private final ModelRenderer Tail0_6;
	private final ModelRenderer Tail0_7;
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

	public ModelEightTails() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 4.0F, -6.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 34, -3.0F, -8.0F, -3.0F, 6, 8, 6, 0.0F, false));

		hornRight1 = new ModelRenderer(this);
		hornRight1.setRotationPoint(-3.5F, -7.0F, -1.0F);
		bipedHead.addChild(hornRight1);
		setRotationAngle(hornRight1, 0.2618F, 0.0F, 0.0F);
		hornRight1.cubeList.add(new ModelBox(hornRight1, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));

		hornRight2 = new ModelRenderer(this);
		hornRight2.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight1.addChild(hornRight2);
		setRotationAngle(hornRight2, 0.0F, -0.3491F, 0.0F);
		hornRight2.cubeList.add(new ModelBox(hornRight2, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, false));

		hornRight3 = new ModelRenderer(this);
		hornRight3.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight2.addChild(hornRight3);
		setRotationAngle(hornRight3, 0.0F, -0.3491F, 0.0F);
		hornRight3.cubeList.add(new ModelBox(hornRight3, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, false));

		hornRight4 = new ModelRenderer(this);
		hornRight4.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight3.addChild(hornRight4);
		setRotationAngle(hornRight4, 0.0F, -0.3491F, 0.0F);
		hornRight4.cubeList.add(new ModelBox(hornRight4, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, false));

		hornRight5 = new ModelRenderer(this);
		hornRight5.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight4.addChild(hornRight5);
		setRotationAngle(hornRight5, 0.0F, -0.3491F, 0.0F);
		hornRight5.cubeList.add(new ModelBox(hornRight5, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, false));

		hornRight6 = new ModelRenderer(this);
		hornRight6.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight5.addChild(hornRight6);
		setRotationAngle(hornRight6, 0.0F, -0.3491F, 0.0F);
		hornRight6.cubeList.add(new ModelBox(hornRight6, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, false));

		hornRight13 = new ModelRenderer(this);
		hornRight13.setRotationPoint(-1.5F, -7.5F, 1.0F);
		bipedHead.addChild(hornRight13);
		setRotationAngle(hornRight13, 0.0F, 0.5236F, 1.309F);
		hornRight13.cubeList.add(new ModelBox(hornRight13, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));

		hornRight14 = new ModelRenderer(this);
		hornRight14.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight13.addChild(hornRight14);
		setRotationAngle(hornRight14, 0.0F, 0.0436F, 0.0F);
		hornRight14.cubeList.add(new ModelBox(hornRight14, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, false));

		hornRight15 = new ModelRenderer(this);
		hornRight15.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight14.addChild(hornRight15);
		setRotationAngle(hornRight15, 0.0F, 0.0436F, 0.0F);
		hornRight15.cubeList.add(new ModelBox(hornRight15, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, false));

		hornRight16 = new ModelRenderer(this);
		hornRight16.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight15.addChild(hornRight16);
		setRotationAngle(hornRight16, 0.0F, 0.0436F, 0.0F);
		hornRight16.cubeList.add(new ModelBox(hornRight16, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, false));

		hornRight17 = new ModelRenderer(this);
		hornRight17.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight16.addChild(hornRight17);
		setRotationAngle(hornRight17, 0.0F, 0.0436F, 0.0F);
		hornRight17.cubeList.add(new ModelBox(hornRight17, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, false));

		hornRight18 = new ModelRenderer(this);
		hornRight18.setRotationPoint(-1.0F, 0.0F, 0.0F);
		hornRight17.addChild(hornRight18);
		setRotationAngle(hornRight18, 0.0F, 0.0436F, 0.0F);
		hornRight18.cubeList.add(new ModelBox(hornRight18, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, false));

		hornRight19 = new ModelRenderer(this);
		hornRight19.setRotationPoint(1.5F, -7.5F, 1.0F);
		bipedHead.addChild(hornRight19);
		setRotationAngle(hornRight19, 0.0F, -0.5236F, -1.309F);
		hornRight19.cubeList.add(new ModelBox(hornRight19, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));

		hornRight20 = new ModelRenderer(this);
		hornRight20.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight19.addChild(hornRight20);
		setRotationAngle(hornRight20, 0.0F, -0.0436F, 0.0F);
		hornRight20.cubeList.add(new ModelBox(hornRight20, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, true));

		hornRight21 = new ModelRenderer(this);
		hornRight21.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight20.addChild(hornRight21);
		setRotationAngle(hornRight21, 0.0F, -0.0436F, 0.0F);
		hornRight21.cubeList.add(new ModelBox(hornRight21, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, true));

		hornRight22 = new ModelRenderer(this);
		hornRight22.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight21.addChild(hornRight22);
		setRotationAngle(hornRight22, 0.0F, -0.0436F, 0.0F);
		hornRight22.cubeList.add(new ModelBox(hornRight22, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, true));

		hornRight23 = new ModelRenderer(this);
		hornRight23.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight22.addChild(hornRight23);
		setRotationAngle(hornRight23, 0.0F, -0.0436F, 0.0F);
		hornRight23.cubeList.add(new ModelBox(hornRight23, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, true));

		hornRight24 = new ModelRenderer(this);
		hornRight24.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight23.addChild(hornRight24);
		setRotationAngle(hornRight24, 0.0F, -0.0436F, 0.0F);
		hornRight24.cubeList.add(new ModelBox(hornRight24, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, true));

		hornRight7 = new ModelRenderer(this);
		hornRight7.setRotationPoint(3.5F, -7.0F, -1.0F);
		bipedHead.addChild(hornRight7);
		setRotationAngle(hornRight7, 0.2618F, 0.0F, 0.0F);
		hornRight7.cubeList.add(new ModelBox(hornRight7, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));

		hornRight8 = new ModelRenderer(this);
		hornRight8.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight7.addChild(hornRight8);
		setRotationAngle(hornRight8, 0.0F, 0.3491F, 0.0F);
		hornRight8.cubeList.add(new ModelBox(hornRight8, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, true));

		hornRight9 = new ModelRenderer(this);
		hornRight9.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight8.addChild(hornRight9);
		setRotationAngle(hornRight9, 0.0F, 0.3491F, 0.0F);
		hornRight9.cubeList.add(new ModelBox(hornRight9, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, true));

		hornRight10 = new ModelRenderer(this);
		hornRight10.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight9.addChild(hornRight10);
		setRotationAngle(hornRight10, 0.0F, 0.3491F, 0.0F);
		hornRight10.cubeList.add(new ModelBox(hornRight10, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, true));

		hornRight11 = new ModelRenderer(this);
		hornRight11.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight10.addChild(hornRight11);
		setRotationAngle(hornRight11, 0.0F, 0.3491F, 0.0F);
		hornRight11.cubeList.add(new ModelBox(hornRight11, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, true));

		hornRight12 = new ModelRenderer(this);
		hornRight12.setRotationPoint(1.0F, 0.0F, 0.0F);
		hornRight11.addChild(hornRight12);
		setRotationAngle(hornRight12, 0.0F, 0.3491F, 0.0F);
		hornRight12.cubeList.add(new ModelBox(hornRight12, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, true));

		snout = new ModelRenderer(this);
		snout.setRotationPoint(0.0F, -4.0F, -3.0F);
		bipedHead.addChild(snout);

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, -2.0F, 0.0F);
		snout.addChild(bone3);
		setRotationAngle(bone3, 0.2618F, 0.0F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 46, 44, -1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.5F, -1.0F, 0.0F);
		snout.addChild(bone6);
		bone6.cubeList.add(new ModelBox(bone6, 24, 20, -2.0F, 0.0F, -4.0F, 3, 3, 4, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(-3.0F, -2.0F, 0.0F);
		snout.addChild(bone);
		setRotationAngle(bone, 0.2618F, -0.3491F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 50, 0, 0.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(3.0F, -2.0F, 0.0F);
		snout.addChild(bone4);
		setRotationAngle(bone4, 0.2618F, 0.3491F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 50, 0, -2.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, true));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-3.0F, 2.0F, 0.0F);
		snout.addChild(bone2);
		setRotationAngle(bone2, 0.0F, -0.3491F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 34, 48, 0.0F, -2.0F, -4.0F, 2, 2, 4, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(3.0F, 2.0F, 0.0F);
		snout.addChild(bone5);
		setRotationAngle(bone5, 0.0F, 0.3491F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 34, 48, -2.0F, -2.0F, -4.0F, 2, 2, 4, 0.0F, true));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.0F, -2.0F, -3.0F);
		bipedHead.addChild(jaw);
		jaw.cubeList.add(new ModelBox(jaw, 24, 0, -1.5F, 0.0F, -4.0F, 3, 2, 4, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, 2.0F, -2.0F);
		jaw.addChild(bone8);
		setRotationAngle(bone8, -0.2618F, 0.0F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 0, 24, -1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 4.0F, -6.1F);
		bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 18, -3.0F, -8.0F, -3.0F, 6, 2, 0, 0.0F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 18.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -7.75F, -5.9F, -4.0F, 8, 12, 8, -0.2F, false));
		bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -0.25F, -5.9F, -4.0F, 8, 12, 8, -0.2F, true));
		bipedBody.cubeList.add(new ModelBox(bipedBody, 18, 48, -7.0F, -4.0F, -4.5F, 7, 8, 1, -0.2F, false));
		bipedBody.cubeList.add(new ModelBox(bipedBody, 18, 48, 0.0F, -4.0F, -4.5F, 7, 8, 1, -0.2F, true));

		chest = new ModelRenderer(this);
		chest.setRotationPoint(1.0F, -7.0F, 4.0F);
		bipedBody.addChild(chest);
		setRotationAngle(chest, 0.5236F, 0.0F, 0.0F);
		chest.cubeList.add(new ModelBox(chest, 0, 0, -9.0F, -11.0F, -8.0F, 8, 12, 8, 0.0F, false));
		chest.cubeList.add(new ModelBox(chest, 0, 0, -1.0F, -11.0F, -8.0F, 8, 12, 8, 0.0F, true));
		chest.cubeList.add(new ModelBox(chest, 0, 48, -9.0F, -10.0F, -8.75F, 8, 8, 1, -0.2F, false));
		chest.cubeList.add(new ModelBox(chest, 0, 48, -1.0F, -10.0F, -8.75F, 8, 8, 1, -0.2F, true));

		hump = new ModelRenderer(this);
		hump.setRotationPoint(-1.0F, -6.0F, -2.0F);
		chest.addChild(hump);
		setRotationAngle(hump, -0.5236F, -0.6981F, 0.3491F);
		hump.cubeList.add(new ModelBox(hump, 0, 20, -4.0F, -6.0F, -4.0F, 8, 6, 8, 0.0F, false));
		hump.cubeList.add(new ModelBox(hump, 0, 20, -3.0F, -1.0F, -3.0F, 8, 6, 8, -1.0F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-7.0F, 6.0F, -5.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 5, -1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		upperArmRight = new ModelRenderer(this);
		upperArmRight.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(upperArmRight);
		setRotationAngle(upperArmRight, 0.0F, 0.0F, 0.3491F);
		upperArmRight.cubeList.add(new ModelBox(upperArmRight, 32, 0, -6.0F, -2.0F, -3.0F, 6, 12, 6, 0.0F, false));

		foreArmRight = new ModelRenderer(this);
		foreArmRight.setRotationPoint(-3.0F, 8.0F, 2.0F);
		upperArmRight.addChild(foreArmRight);
		setRotationAngle(foreArmRight, -0.5236F, 0.0F, -0.5236F);
		foreArmRight.cubeList.add(new ModelBox(foreArmRight, 26, 28, -3.0F, 0.0F, -5.0F, 6, 14, 6, -0.2F, false));
		foreArmRight.cubeList.add(new ModelBox(foreArmRight, 0, 0, -2.75F, -2.0F, -1.0F, 2, 3, 2, 0.0F, false));
		foreArmRight.cubeList.add(new ModelBox(foreArmRight, 0, 0, -2.75F, -4.0F, -1.0F, 2, 3, 2, -0.2F, false));
		foreArmRight.cubeList.add(new ModelBox(foreArmRight, 0, 0, -2.75F, -6.0F, -1.0F, 2, 3, 2, -0.4F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(7.0F, 6.0F, -5.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 5, 0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, true));

		upperArmLeft = new ModelRenderer(this);
		upperArmLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(upperArmLeft);
		setRotationAngle(upperArmLeft, 0.0F, 0.0F, -0.3491F);
		upperArmLeft.cubeList.add(new ModelBox(upperArmLeft, 32, 0, 0.0F, -2.0F, -3.0F, 6, 12, 6, 0.0F, true));

		foreArmLeft = new ModelRenderer(this);
		foreArmLeft.setRotationPoint(3.0F, 8.0F, 2.0F);
		upperArmLeft.addChild(foreArmLeft);
		setRotationAngle(foreArmLeft, -0.5236F, 0.0F, 0.5236F);
		foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 26, 28, -3.0F, 0.0F, -5.0F, 6, 14, 6, -0.2F, true));
		foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 0, 0, 0.75F, -2.0F, -1.0F, 2, 3, 2, 0.0F, true));
		foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 0, 0, 0.75F, -4.0F, -1.0F, 2, 3, 2, -0.2F, true));
		foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 0, 0, 0.75F, -6.0F, -1.0F, 2, 3, 2, -0.4F, true));

		Tail0_0 = new ModelRenderer(this);
		Tail0_0.setRotationPoint(4.0F, 23.5F, 0.0F);
		setRotationAngle(Tail0_0, -1.4835F, 1.8326F, 0.0F);
		Tail0_0.cubeList.add(new ModelBox(Tail0_0, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));

		Tail0_1 = new ModelRenderer(this);
		Tail0_1.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail0_0.addChild(Tail0_1);
		setRotationAngle(Tail0_1, 0.2618F, 0.0F, 0.0F);
		Tail0_1.cubeList.add(new ModelBox(Tail0_1, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));

		Tail0_2 = new ModelRenderer(this);
		Tail0_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail0_1.addChild(Tail0_2);
		setRotationAngle(Tail0_2, 0.2618F, 0.0F, 0.0F);
		Tail0_2.cubeList.add(new ModelBox(Tail0_2, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));

		Tail0_3 = new ModelRenderer(this);
		Tail0_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail0_2.addChild(Tail0_3);
		setRotationAngle(Tail0_3, 0.2618F, 0.0F, 0.0F);
		Tail0_3.cubeList.add(new ModelBox(Tail0_3, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));

		Tail0_4 = new ModelRenderer(this);
		Tail0_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail0_3.addChild(Tail0_4);
		setRotationAngle(Tail0_4, 0.2618F, 0.0F, 0.0F);
		Tail0_4.cubeList.add(new ModelBox(Tail0_4, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));

		Tail0_5 = new ModelRenderer(this);
		Tail0_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail0_4.addChild(Tail0_5);
		setRotationAngle(Tail0_5, 0.2618F, 0.0F, 0.0F);
		Tail0_5.cubeList.add(new ModelBox(Tail0_5, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));

		Tail0_6 = new ModelRenderer(this);
		Tail0_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail0_5.addChild(Tail0_6);
		setRotationAngle(Tail0_6, 0.2618F, 0.0F, 0.0F);
		Tail0_6.cubeList.add(new ModelBox(Tail0_6, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail0_7 = new ModelRenderer(this);
		Tail0_7.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail0_6.addChild(Tail0_7);
		setRotationAngle(Tail0_7, 0.2618F, 0.0F, 0.0F);
		Tail0_7.cubeList.add(new ModelBox(Tail0_7, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));

		Tail1_0 = new ModelRenderer(this);
		Tail1_0.setRotationPoint(3.0F, 23.5F, 0.0F);
		setRotationAngle(Tail1_0, -1.4835F, 1.309F, 0.0F);
		Tail1_0.cubeList.add(new ModelBox(Tail1_0, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));

		Tail1_1 = new ModelRenderer(this);
		Tail1_1.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail1_0.addChild(Tail1_1);
		setRotationAngle(Tail1_1, 0.2618F, 0.0F, 0.0F);
		Tail1_1.cubeList.add(new ModelBox(Tail1_1, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));

		Tail1_2 = new ModelRenderer(this);
		Tail1_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail1_1.addChild(Tail1_2);
		setRotationAngle(Tail1_2, 0.2618F, 0.0F, 0.0F);
		Tail1_2.cubeList.add(new ModelBox(Tail1_2, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));

		Tail1_3 = new ModelRenderer(this);
		Tail1_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail1_2.addChild(Tail1_3);
		setRotationAngle(Tail1_3, 0.2618F, 0.0F, 0.0F);
		Tail1_3.cubeList.add(new ModelBox(Tail1_3, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));

		Tail1_4 = new ModelRenderer(this);
		Tail1_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail1_3.addChild(Tail1_4);
		setRotationAngle(Tail1_4, 0.2618F, 0.0F, 0.0F);
		Tail1_4.cubeList.add(new ModelBox(Tail1_4, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));

		Tail1_5 = new ModelRenderer(this);
		Tail1_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail1_4.addChild(Tail1_5);
		setRotationAngle(Tail1_5, 0.2618F, 0.0F, 0.0F);
		Tail1_5.cubeList.add(new ModelBox(Tail1_5, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));

		Tail1_6 = new ModelRenderer(this);
		Tail1_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail1_5.addChild(Tail1_6);
		setRotationAngle(Tail1_6, 0.2618F, 0.0F, 0.0F);
		Tail1_6.cubeList.add(new ModelBox(Tail1_6, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail1_7 = new ModelRenderer(this);
		Tail1_7.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail1_6.addChild(Tail1_7);
		setRotationAngle(Tail1_7, 0.2618F, 0.0F, 0.0F);
		Tail1_7.cubeList.add(new ModelBox(Tail1_7, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));

		Tail2_0 = new ModelRenderer(this);
		Tail2_0.setRotationPoint(2.0F, 23.5F, 0.0F);
		setRotationAngle(Tail2_0, -1.4835F, 0.7854F, 0.0F);
		Tail2_0.cubeList.add(new ModelBox(Tail2_0, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));

		Tail2_1 = new ModelRenderer(this);
		Tail2_1.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_0.addChild(Tail2_1);
		setRotationAngle(Tail2_1, 0.2618F, 0.0F, 0.0F);
		Tail2_1.cubeList.add(new ModelBox(Tail2_1, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));

		Tail2_2 = new ModelRenderer(this);
		Tail2_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_1.addChild(Tail2_2);
		setRotationAngle(Tail2_2, 0.2618F, 0.0F, 0.0F);
		Tail2_2.cubeList.add(new ModelBox(Tail2_2, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));

		Tail2_3 = new ModelRenderer(this);
		Tail2_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_2.addChild(Tail2_3);
		setRotationAngle(Tail2_3, 0.2618F, 0.0F, 0.0F);
		Tail2_3.cubeList.add(new ModelBox(Tail2_3, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));

		Tail2_4 = new ModelRenderer(this);
		Tail2_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_3.addChild(Tail2_4);
		setRotationAngle(Tail2_4, 0.2618F, 0.0F, 0.0F);
		Tail2_4.cubeList.add(new ModelBox(Tail2_4, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));

		Tail2_5 = new ModelRenderer(this);
		Tail2_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_4.addChild(Tail2_5);
		setRotationAngle(Tail2_5, 0.2618F, 0.0F, 0.0F);
		Tail2_5.cubeList.add(new ModelBox(Tail2_5, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));

		Tail2_6 = new ModelRenderer(this);
		Tail2_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_5.addChild(Tail2_6);
		setRotationAngle(Tail2_6, 0.2618F, 0.0F, 0.0F);
		Tail2_6.cubeList.add(new ModelBox(Tail2_6, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail2_7 = new ModelRenderer(this);
		Tail2_7.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail2_6.addChild(Tail2_7);
		setRotationAngle(Tail2_7, 0.2618F, 0.0F, 0.0F);
		Tail2_7.cubeList.add(new ModelBox(Tail2_7, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));

		Tail3_0 = new ModelRenderer(this);
		Tail3_0.setRotationPoint(1.0F, 23.5F, 0.0F);
		setRotationAngle(Tail3_0, -1.4835F, 0.2618F, 0.0F);
		Tail3_0.cubeList.add(new ModelBox(Tail3_0, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));

		Tail3_1 = new ModelRenderer(this);
		Tail3_1.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_0.addChild(Tail3_1);
		setRotationAngle(Tail3_1, 0.2618F, 0.0F, 0.0F);
		Tail3_1.cubeList.add(new ModelBox(Tail3_1, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));

		Tail3_2 = new ModelRenderer(this);
		Tail3_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_1.addChild(Tail3_2);
		setRotationAngle(Tail3_2, 0.2618F, 0.0F, 0.0F);
		Tail3_2.cubeList.add(new ModelBox(Tail3_2, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));

		Tail3_3 = new ModelRenderer(this);
		Tail3_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_2.addChild(Tail3_3);
		setRotationAngle(Tail3_3, 0.2618F, 0.0F, 0.0F);
		Tail3_3.cubeList.add(new ModelBox(Tail3_3, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));

		Tail3_4 = new ModelRenderer(this);
		Tail3_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_3.addChild(Tail3_4);
		setRotationAngle(Tail3_4, 0.2618F, 0.0F, 0.0F);
		Tail3_4.cubeList.add(new ModelBox(Tail3_4, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));

		Tail3_5 = new ModelRenderer(this);
		Tail3_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_4.addChild(Tail3_5);
		setRotationAngle(Tail3_5, 0.2618F, 0.0F, 0.0F);
		Tail3_5.cubeList.add(new ModelBox(Tail3_5, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));

		Tail3_6 = new ModelRenderer(this);
		Tail3_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_5.addChild(Tail3_6);
		setRotationAngle(Tail3_6, 0.2618F, 0.0F, 0.0F);
		Tail3_6.cubeList.add(new ModelBox(Tail3_6, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail3_7 = new ModelRenderer(this);
		Tail3_7.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail3_6.addChild(Tail3_7);
		setRotationAngle(Tail3_7, 0.2618F, 0.0F, 0.0F);
		Tail3_7.cubeList.add(new ModelBox(Tail3_7, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));

		Tail4_0 = new ModelRenderer(this);
		Tail4_0.setRotationPoint(-1.0F, 23.5F, 0.0F);
		setRotationAngle(Tail4_0, -1.4835F, -0.2618F, 0.0F);
		Tail4_0.cubeList.add(new ModelBox(Tail4_0, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));

		Tail4_1 = new ModelRenderer(this);
		Tail4_1.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_0.addChild(Tail4_1);
		setRotationAngle(Tail4_1, 0.2618F, 0.0F, 0.0F);
		Tail4_1.cubeList.add(new ModelBox(Tail4_1, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));

		Tail4_2 = new ModelRenderer(this);
		Tail4_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_1.addChild(Tail4_2);
		setRotationAngle(Tail4_2, 0.2618F, 0.0F, 0.0F);
		Tail4_2.cubeList.add(new ModelBox(Tail4_2, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));

		Tail4_3 = new ModelRenderer(this);
		Tail4_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_2.addChild(Tail4_3);
		setRotationAngle(Tail4_3, 0.2618F, 0.0F, 0.0F);
		Tail4_3.cubeList.add(new ModelBox(Tail4_3, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));

		Tail4_4 = new ModelRenderer(this);
		Tail4_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_3.addChild(Tail4_4);
		setRotationAngle(Tail4_4, 0.2618F, 0.0F, 0.0F);
		Tail4_4.cubeList.add(new ModelBox(Tail4_4, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));

		Tail4_5 = new ModelRenderer(this);
		Tail4_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_4.addChild(Tail4_5);
		setRotationAngle(Tail4_5, 0.2618F, 0.0F, 0.0F);
		Tail4_5.cubeList.add(new ModelBox(Tail4_5, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));

		Tail4_6 = new ModelRenderer(this);
		Tail4_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_5.addChild(Tail4_6);
		setRotationAngle(Tail4_6, 0.2618F, 0.0F, 0.0F);
		Tail4_6.cubeList.add(new ModelBox(Tail4_6, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail4_7 = new ModelRenderer(this);
		Tail4_7.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail4_6.addChild(Tail4_7);
		setRotationAngle(Tail4_7, 0.2618F, 0.0F, 0.0F);
		Tail4_7.cubeList.add(new ModelBox(Tail4_7, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));

		Tail5_0 = new ModelRenderer(this);
		Tail5_0.setRotationPoint(-2.0F, 23.5F, 0.0F);
		setRotationAngle(Tail5_0, -1.4835F, -0.7854F, 0.0F);
		Tail5_0.cubeList.add(new ModelBox(Tail5_0, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));

		Tail5_1 = new ModelRenderer(this);
		Tail5_1.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_0.addChild(Tail5_1);
		setRotationAngle(Tail5_1, 0.2618F, 0.0F, 0.0F);
		Tail5_1.cubeList.add(new ModelBox(Tail5_1, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));

		Tail5_2 = new ModelRenderer(this);
		Tail5_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_1.addChild(Tail5_2);
		setRotationAngle(Tail5_2, 0.2618F, 0.0F, 0.0F);
		Tail5_2.cubeList.add(new ModelBox(Tail5_2, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));

		Tail5_3 = new ModelRenderer(this);
		Tail5_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_2.addChild(Tail5_3);
		setRotationAngle(Tail5_3, 0.2618F, 0.0F, 0.0F);
		Tail5_3.cubeList.add(new ModelBox(Tail5_3, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));

		Tail5_4 = new ModelRenderer(this);
		Tail5_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_3.addChild(Tail5_4);
		setRotationAngle(Tail5_4, 0.2618F, 0.0F, 0.0F);
		Tail5_4.cubeList.add(new ModelBox(Tail5_4, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));

		Tail5_5 = new ModelRenderer(this);
		Tail5_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_4.addChild(Tail5_5);
		setRotationAngle(Tail5_5, 0.2618F, 0.0F, 0.0F);
		Tail5_5.cubeList.add(new ModelBox(Tail5_5, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));

		Tail5_6 = new ModelRenderer(this);
		Tail5_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_5.addChild(Tail5_6);
		setRotationAngle(Tail5_6, 0.2618F, 0.0F, 0.0F);
		Tail5_6.cubeList.add(new ModelBox(Tail5_6, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail5_7 = new ModelRenderer(this);
		Tail5_7.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail5_6.addChild(Tail5_7);
		setRotationAngle(Tail5_7, 0.2618F, 0.0F, 0.0F);
		Tail5_7.cubeList.add(new ModelBox(Tail5_7, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));

		Tail6_0 = new ModelRenderer(this);
		Tail6_0.setRotationPoint(-3.0F, 23.5F, 0.0F);
		setRotationAngle(Tail6_0, -1.4835F, -1.309F, 0.0F);
		Tail6_0.cubeList.add(new ModelBox(Tail6_0, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));

		Tail6_1 = new ModelRenderer(this);
		Tail6_1.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_0.addChild(Tail6_1);
		setRotationAngle(Tail6_1, 0.2618F, 0.0F, 0.0F);
		Tail6_1.cubeList.add(new ModelBox(Tail6_1, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));

		Tail6_2 = new ModelRenderer(this);
		Tail6_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_1.addChild(Tail6_2);
		setRotationAngle(Tail6_2, 0.2618F, 0.0F, 0.0F);
		Tail6_2.cubeList.add(new ModelBox(Tail6_2, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));

		Tail6_3 = new ModelRenderer(this);
		Tail6_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_2.addChild(Tail6_3);
		setRotationAngle(Tail6_3, 0.2618F, 0.0F, 0.0F);
		Tail6_3.cubeList.add(new ModelBox(Tail6_3, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));

		Tail6_4 = new ModelRenderer(this);
		Tail6_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_3.addChild(Tail6_4);
		setRotationAngle(Tail6_4, 0.2618F, 0.0F, 0.0F);
		Tail6_4.cubeList.add(new ModelBox(Tail6_4, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));

		Tail6_5 = new ModelRenderer(this);
		Tail6_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_4.addChild(Tail6_5);
		setRotationAngle(Tail6_5, 0.2618F, 0.0F, 0.0F);
		Tail6_5.cubeList.add(new ModelBox(Tail6_5, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));

		Tail6_6 = new ModelRenderer(this);
		Tail6_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_5.addChild(Tail6_6);
		setRotationAngle(Tail6_6, 0.2618F, 0.0F, 0.0F);
		Tail6_6.cubeList.add(new ModelBox(Tail6_6, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail6_7 = new ModelRenderer(this);
		Tail6_7.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail6_6.addChild(Tail6_7);
		setRotationAngle(Tail6_7, 0.2618F, 0.0F, 0.0F);
		Tail6_7.cubeList.add(new ModelBox(Tail6_7, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));

		Tail7_0 = new ModelRenderer(this);
		Tail7_0.setRotationPoint(-4.0F, 23.5F, 0.0F);
		setRotationAngle(Tail7_0, -1.4835F, -1.8326F, 0.0F);
		Tail7_0.cubeList.add(new ModelBox(Tail7_0, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));

		Tail7_1 = new ModelRenderer(this);
		Tail7_1.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_0.addChild(Tail7_1);
		setRotationAngle(Tail7_1, 0.2618F, 0.0F, 0.0F);
		Tail7_1.cubeList.add(new ModelBox(Tail7_1, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));

		Tail7_2 = new ModelRenderer(this);
		Tail7_2.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_1.addChild(Tail7_2);
		setRotationAngle(Tail7_2, 0.2618F, 0.0F, 0.0F);
		Tail7_2.cubeList.add(new ModelBox(Tail7_2, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));

		Tail7_3 = new ModelRenderer(this);
		Tail7_3.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_2.addChild(Tail7_3);
		setRotationAngle(Tail7_3, 0.2618F, 0.0F, 0.0F);
		Tail7_3.cubeList.add(new ModelBox(Tail7_3, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));

		Tail7_4 = new ModelRenderer(this);
		Tail7_4.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_3.addChild(Tail7_4);
		setRotationAngle(Tail7_4, 0.2618F, 0.0F, 0.0F);
		Tail7_4.cubeList.add(new ModelBox(Tail7_4, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));

		Tail7_5 = new ModelRenderer(this);
		Tail7_5.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_4.addChild(Tail7_5);
		setRotationAngle(Tail7_5, 0.2618F, 0.0F, 0.0F);
		Tail7_5.cubeList.add(new ModelBox(Tail7_5, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));

		Tail7_6 = new ModelRenderer(this);
		Tail7_6.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_5.addChild(Tail7_6);
		setRotationAngle(Tail7_6, 0.2618F, 0.0F, 0.0F);
		Tail7_6.cubeList.add(new ModelBox(Tail7_6, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail7_7 = new ModelRenderer(this);
		Tail7_7.setRotationPoint(0.0F, -7.0F, 0.0F);
		Tail7_6.addChild(Tail7_7);
		setRotationAngle(Tail7_7, 0.2618F, 0.0F, 0.0F);
		Tail7_7.cubeList.add(new ModelBox(Tail7_7, 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
		bipedHeadwear.render(f5);
		bipedBody.render(f5);
		bipedRightArm.render(f5);
		bipedLeftArm.render(f5);
		Tail0_0.render(f5);
		Tail1_0.render(f5);
		Tail2_0.render(f5);
		Tail3_0.render(f5);
		Tail4_0.render(f5);
		Tail5_0.render(f5);
		Tail6_0.render(f5);
		Tail7_0.render(f5);
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