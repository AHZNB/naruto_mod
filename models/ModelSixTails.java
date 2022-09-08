// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelSixTails extends ModelBase {
	private final ModelRenderer Head;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer HornRight;
	private final ModelRenderer Horns2;
	private final ModelRenderer Horns3;
	private final ModelRenderer Horns4;
	private final ModelRenderer bone3;
	private final ModelRenderer HornLeft;
	private final ModelRenderer Horns6;
	private final ModelRenderer Horns7;
	private final ModelRenderer Horns8;
	private final ModelRenderer bone4;
	private final ModelRenderer Body;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
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
	private final ModelRenderer Tail4_0;
	private final ModelRenderer Tail4_1;
	private final ModelRenderer Tail4_2;
	private final ModelRenderer Tail4_3;
	private final ModelRenderer Tail4_4;
	private final ModelRenderer Tail4_5;
	private final ModelRenderer Tail5_0;
	private final ModelRenderer Tail5_1;
	private final ModelRenderer Tail5_2;
	private final ModelRenderer Tail5_3;
	private final ModelRenderer Tail5_4;
	private final ModelRenderer Tail5_5;
	private final ModelRenderer RightArm;
	private final ModelRenderer bone;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer LeftArm;
	private final ModelRenderer bone5;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer RightLeg;
	private final ModelRenderer cube_r12;
	private final ModelRenderer cube_r13;
	private final ModelRenderer RightLeg1;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer Legdetail;
	private final ModelRenderer LeftLeg;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer RightLeg4;
	private final ModelRenderer cube_r18;
	private final ModelRenderer cube_r19;
	private final ModelRenderer Legdetail2;

	public ModelSixTails() {
		textureWidth = 64;
		textureHeight = 64;

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 14.0F, -2.0F);
		Head.cubeList.add(new ModelBox(Head, 0, 22, -3.0F, -4.8F, -2.1F, 6, 5, 3, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 20, 0, -3.0F, -4.8F, -3.1F, 6, 5, 1, 0.0F, false));
		Head.cubeList.add(new ModelBox(Head, 17, 11, -3.0F, -3.4F, -4.6F, 6, 3, 2, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, -3.8499F, -3.4747F);
		Head.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.829F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 31, 9, -3.0F, -0.9F, -0.4F, 6, 2, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, -0.4907F, -3.3866F);
		Head.addChild(cube_r2);
		setRotationAngle(cube_r2, 1.0472F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 26, 6, -3.0F, -1.0F, -0.7F, 6, 2, 1, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, -1.0169F, 0.9617F);
		Head.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.1745F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 18, 25, -3.0F, -3.7F, -0.3F, 6, 5, 1, 0.0F, false));

		HornRight = new ModelRenderer(this);
		HornRight.setRotationPoint(-2.5F, -4.75F, -2.5F);
		Head.addChild(HornRight);
		setRotationAngle(HornRight, 0.0F, 0.0F, -0.4363F);
		HornRight.cubeList.add(new ModelBox(HornRight, 0, 0, 0.0F, -0.45F, -0.5F, 1, 1, 1, 0.0F, false));

		Horns2 = new ModelRenderer(this);
		Horns2.setRotationPoint(0.0F, -0.5F, 0.0F);
		HornRight.addChild(Horns2);
		setRotationAngle(Horns2, 0.0F, 0.0F, 0.0873F);
		Horns2.cubeList.add(new ModelBox(Horns2, 0, 0, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, false));

		Horns3 = new ModelRenderer(this);
		Horns3.setRotationPoint(0.0F, -0.5F, 0.0F);
		Horns2.addChild(Horns3);
		setRotationAngle(Horns3, 0.0873F, 0.0F, 0.0F);
		Horns3.cubeList.add(new ModelBox(Horns3, 0, 0, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.1F, false));

		Horns4 = new ModelRenderer(this);
		Horns4.setRotationPoint(0.0F, -0.5F, 0.0F);
		Horns3.addChild(Horns4);
		setRotationAngle(Horns4, 0.0873F, 0.0F, 0.0F);
		Horns4.cubeList.add(new ModelBox(Horns4, 0, 0, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.15F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, -0.6499F, 0.0253F);
		Horns4.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 0.0F, -0.0873F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 2, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, false));

		HornLeft = new ModelRenderer(this);
		HornLeft.setRotationPoint(2.5F, -4.75F, -2.5F);
		Head.addChild(HornLeft);
		setRotationAngle(HornLeft, 0.0F, 0.0F, 0.4363F);
		HornLeft.cubeList.add(new ModelBox(HornLeft, 0, 0, -1.0F, -0.45F, -0.5F, 1, 1, 1, 0.0F, true));

		Horns6 = new ModelRenderer(this);
		Horns6.setRotationPoint(0.0F, -0.5F, 0.0F);
		HornLeft.addChild(Horns6);
		setRotationAngle(Horns6, 0.0F, 0.0F, -0.0873F);
		Horns6.cubeList.add(new ModelBox(Horns6, 0, 0, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, true));

		Horns7 = new ModelRenderer(this);
		Horns7.setRotationPoint(0.0F, -0.5F, 0.0F);
		Horns6.addChild(Horns7);
		setRotationAngle(Horns7, -0.0873F, 0.0F, 0.0F);
		Horns7.cubeList.add(new ModelBox(Horns7, 0, 0, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.1F, true));

		Horns8 = new ModelRenderer(this);
		Horns8.setRotationPoint(0.0F, -0.5F, 0.0F);
		Horns7.addChild(Horns8);
		setRotationAngle(Horns8, -0.0873F, 0.0F, 0.0F);
		Horns8.cubeList.add(new ModelBox(Horns8, 0, 0, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.15F, true));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, -0.6499F, 0.0253F);
		Horns8.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 0.0F, 0.0873F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 2, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, true));

		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, 14.0F, 0.0F);
		Body.cubeList.add(new ModelBox(Body, 17, 16, -3.0F, 2.0044F, -4.9001F, 6, 4, 5, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, 7.5F, -2.4F);
		Body.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.0873F, 0.0F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 0, -3.5F, -2.5F, -3.6F, 7, 5, 6, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, 2.0044F, -2.4001F);
		Body.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.1309F, 0.0F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 11, -3.0F, -3.6F, -2.5F, 6, 5, 5, -0.2F, false));

		Tail0_0 = new ModelRenderer(this);
		Tail0_0.setRotationPoint(1.25F, 8.75F, 0.6F);
		Body.addChild(Tail0_0);
		setRotationAngle(Tail0_0, -1.2217F, 1.309F, 0.0F);
		Tail0_0.cubeList.add(new ModelBox(Tail0_0, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

		Tail0_1 = new ModelRenderer(this);
		Tail0_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_0.addChild(Tail0_1);
		setRotationAngle(Tail0_1, 0.2618F, 0.0F, 0.0F);
		Tail0_1.cubeList.add(new ModelBox(Tail0_1, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

		Tail0_2 = new ModelRenderer(this);
		Tail0_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_1.addChild(Tail0_2);
		setRotationAngle(Tail0_2, 0.2618F, 0.0F, 0.0F);
		Tail0_2.cubeList.add(new ModelBox(Tail0_2, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

		Tail0_3 = new ModelRenderer(this);
		Tail0_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_2.addChild(Tail0_3);
		setRotationAngle(Tail0_3, 0.2618F, 0.0F, 0.0F);
		Tail0_3.cubeList.add(new ModelBox(Tail0_3, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

		Tail0_4 = new ModelRenderer(this);
		Tail0_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_3.addChild(Tail0_4);
		setRotationAngle(Tail0_4, 0.2618F, 0.0F, 0.0F);
		Tail0_4.cubeList.add(new ModelBox(Tail0_4, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail0_5 = new ModelRenderer(this);
		Tail0_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0_4.addChild(Tail0_5);
		setRotationAngle(Tail0_5, 0.2618F, 0.0F, 0.0F);
		Tail0_5.cubeList.add(new ModelBox(Tail0_5, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail1_0 = new ModelRenderer(this);
		Tail1_0.setRotationPoint(0.75F, 8.75F, 0.6F);
		Body.addChild(Tail1_0);
		setRotationAngle(Tail1_0, -0.7854F, 0.7854F, 0.0F);
		Tail1_0.cubeList.add(new ModelBox(Tail1_0, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

		Tail1_1 = new ModelRenderer(this);
		Tail1_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_0.addChild(Tail1_1);
		setRotationAngle(Tail1_1, 0.2618F, 0.0F, 0.0F);
		Tail1_1.cubeList.add(new ModelBox(Tail1_1, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

		Tail1_2 = new ModelRenderer(this);
		Tail1_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_1.addChild(Tail1_2);
		setRotationAngle(Tail1_2, 0.2618F, 0.0F, 0.0F);
		Tail1_2.cubeList.add(new ModelBox(Tail1_2, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

		Tail1_3 = new ModelRenderer(this);
		Tail1_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_2.addChild(Tail1_3);
		setRotationAngle(Tail1_3, 0.2618F, 0.0F, 0.0F);
		Tail1_3.cubeList.add(new ModelBox(Tail1_3, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

		Tail1_4 = new ModelRenderer(this);
		Tail1_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_3.addChild(Tail1_4);
		setRotationAngle(Tail1_4, -0.2618F, 0.0F, 0.0F);
		Tail1_4.cubeList.add(new ModelBox(Tail1_4, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail1_5 = new ModelRenderer(this);
		Tail1_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1_4.addChild(Tail1_5);
		setRotationAngle(Tail1_5, -0.2618F, 0.0F, 0.0F);
		Tail1_5.cubeList.add(new ModelBox(Tail1_5, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail2_0 = new ModelRenderer(this);
		Tail2_0.setRotationPoint(0.25F, 8.75F, 0.6F);
		Body.addChild(Tail2_0);
		setRotationAngle(Tail2_0, -1.0472F, 0.2618F, 0.0F);
		Tail2_0.cubeList.add(new ModelBox(Tail2_0, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

		Tail2_1 = new ModelRenderer(this);
		Tail2_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_0.addChild(Tail2_1);
		setRotationAngle(Tail2_1, 0.2618F, 0.0F, 0.0F);
		Tail2_1.cubeList.add(new ModelBox(Tail2_1, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

		Tail2_2 = new ModelRenderer(this);
		Tail2_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_1.addChild(Tail2_2);
		setRotationAngle(Tail2_2, 0.2618F, 0.0F, 0.0F);
		Tail2_2.cubeList.add(new ModelBox(Tail2_2, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

		Tail2_3 = new ModelRenderer(this);
		Tail2_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_2.addChild(Tail2_3);
		setRotationAngle(Tail2_3, 0.2618F, 0.0F, 0.0F);
		Tail2_3.cubeList.add(new ModelBox(Tail2_3, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

		Tail2_4 = new ModelRenderer(this);
		Tail2_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_3.addChild(Tail2_4);
		setRotationAngle(Tail2_4, 0.2618F, 0.0F, 0.0F);
		Tail2_4.cubeList.add(new ModelBox(Tail2_4, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail2_5 = new ModelRenderer(this);
		Tail2_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2_4.addChild(Tail2_5);
		setRotationAngle(Tail2_5, -0.2618F, 0.0F, 0.0F);
		Tail2_5.cubeList.add(new ModelBox(Tail2_5, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail3_0 = new ModelRenderer(this);
		Tail3_0.setRotationPoint(-0.25F, 8.75F, 0.6F);
		Body.addChild(Tail3_0);
		setRotationAngle(Tail3_0, -0.3491F, -0.2618F, 0.0F);
		Tail3_0.cubeList.add(new ModelBox(Tail3_0, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

		Tail3_1 = new ModelRenderer(this);
		Tail3_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_0.addChild(Tail3_1);
		setRotationAngle(Tail3_1, -0.2618F, 0.0F, 0.0F);
		Tail3_1.cubeList.add(new ModelBox(Tail3_1, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

		Tail3_2 = new ModelRenderer(this);
		Tail3_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_1.addChild(Tail3_2);
		setRotationAngle(Tail3_2, 0.2618F, 0.0F, 0.0F);
		Tail3_2.cubeList.add(new ModelBox(Tail3_2, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

		Tail3_3 = new ModelRenderer(this);
		Tail3_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_2.addChild(Tail3_3);
		setRotationAngle(Tail3_3, 0.2618F, 0.0F, 0.0F);
		Tail3_3.cubeList.add(new ModelBox(Tail3_3, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

		Tail3_4 = new ModelRenderer(this);
		Tail3_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_3.addChild(Tail3_4);
		setRotationAngle(Tail3_4, 0.2618F, 0.0F, 0.0F);
		Tail3_4.cubeList.add(new ModelBox(Tail3_4, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail3_5 = new ModelRenderer(this);
		Tail3_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3_4.addChild(Tail3_5);
		setRotationAngle(Tail3_5, -0.2618F, 0.0F, 0.0F);
		Tail3_5.cubeList.add(new ModelBox(Tail3_5, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail4_0 = new ModelRenderer(this);
		Tail4_0.setRotationPoint(-0.75F, 8.75F, 0.6F);
		Body.addChild(Tail4_0);
		setRotationAngle(Tail4_0, -0.8727F, -0.7854F, 0.0F);
		Tail4_0.cubeList.add(new ModelBox(Tail4_0, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

		Tail4_1 = new ModelRenderer(this);
		Tail4_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail4_0.addChild(Tail4_1);
		setRotationAngle(Tail4_1, 0.2618F, 0.0F, 0.0F);
		Tail4_1.cubeList.add(new ModelBox(Tail4_1, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

		Tail4_2 = new ModelRenderer(this);
		Tail4_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail4_1.addChild(Tail4_2);
		setRotationAngle(Tail4_2, 0.2618F, 0.0F, 0.0F);
		Tail4_2.cubeList.add(new ModelBox(Tail4_2, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

		Tail4_3 = new ModelRenderer(this);
		Tail4_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail4_2.addChild(Tail4_3);
		setRotationAngle(Tail4_3, -0.2618F, 0.0F, 0.0F);
		Tail4_3.cubeList.add(new ModelBox(Tail4_3, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

		Tail4_4 = new ModelRenderer(this);
		Tail4_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail4_3.addChild(Tail4_4);
		setRotationAngle(Tail4_4, -0.2618F, 0.0F, 0.0F);
		Tail4_4.cubeList.add(new ModelBox(Tail4_4, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail4_5 = new ModelRenderer(this);
		Tail4_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail4_4.addChild(Tail4_5);
		setRotationAngle(Tail4_5, -0.2618F, 0.0F, 0.0F);
		Tail4_5.cubeList.add(new ModelBox(Tail4_5, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail5_0 = new ModelRenderer(this);
		Tail5_0.setRotationPoint(-1.25F, 8.75F, 0.6F);
		Body.addChild(Tail5_0);
		setRotationAngle(Tail5_0, -1.2217F, -1.309F, 0.0F);
		Tail5_0.cubeList.add(new ModelBox(Tail5_0, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

		Tail5_1 = new ModelRenderer(this);
		Tail5_1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail5_0.addChild(Tail5_1);
		setRotationAngle(Tail5_1, 0.2618F, 0.0F, 0.0F);
		Tail5_1.cubeList.add(new ModelBox(Tail5_1, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

		Tail5_2 = new ModelRenderer(this);
		Tail5_2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail5_1.addChild(Tail5_2);
		setRotationAngle(Tail5_2, 0.2618F, 0.0F, 0.0F);
		Tail5_2.cubeList.add(new ModelBox(Tail5_2, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

		Tail5_3 = new ModelRenderer(this);
		Tail5_3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail5_2.addChild(Tail5_3);
		setRotationAngle(Tail5_3, 0.2618F, 0.0F, 0.0F);
		Tail5_3.cubeList.add(new ModelBox(Tail5_3, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

		Tail5_4 = new ModelRenderer(this);
		Tail5_4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail5_3.addChild(Tail5_4);
		setRotationAngle(Tail5_4, -0.2618F, 0.0F, 0.0F);
		Tail5_4.cubeList.add(new ModelBox(Tail5_4, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail5_5 = new ModelRenderer(this);
		Tail5_5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail5_4.addChild(Tail5_5);
		setRotationAngle(Tail5_5, -0.2618F, 0.0F, 0.0F);
		Tail5_5.cubeList.add(new ModelBox(Tail5_5, 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		RightArm = new ModelRenderer(this);
		RightArm.setRotationPoint(-3.0F, 16.75F, -1.5F);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 0.0F);
		RightArm.addChild(bone);
		setRotationAngle(bone, 0.0F, 0.3491F, 0.0F);

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-0.287F, 0.2867F, -1.7153F);
		bone.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.2618F, -0.5672F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 12, 31, -1.0F, -0.9F, -1.5F, 2, 2, 3, -0.4F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(-0.9F, 0.25F, -0.3F);
		bone.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.1309F, 0.0F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 30, 35, -0.8F, -1.2F, -1.2F, 2, 2, 2, -0.4F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(-0.5869F, -0.0397F, 0.43F);
		bone.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.1309F, -0.5672F, -0.0873F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 34, 16, -1.1F, -1.0F, -1.5F, 2, 2, 2, -0.4F, false));

		LeftArm = new ModelRenderer(this);
		LeftArm.setRotationPoint(3.0F, 16.75F, -1.5F);

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
		LeftArm.addChild(bone5);
		setRotationAngle(bone5, 0.0F, -0.3491F, 0.0F);

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.287F, 0.2867F, -1.7153F);
		bone5.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.2618F, 0.5672F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 12, 31, -1.0F, -0.9F, -1.5F, 2, 2, 3, -0.4F, true));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(0.9F, 0.25F, -0.3F);
		bone5.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.1309F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 30, 35, -1.2F, -1.2F, -1.2F, 2, 2, 2, -0.4F, true));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(0.5869F, -0.0397F, 0.43F);
		bone5.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.1309F, 0.5672F, 0.0873F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 34, 16, -0.9F, -1.0F, -1.5F, 2, 2, 2, -0.4F, true));

		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(-2.75F, 19.0F, -3.0F);

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-3.0464F, 4.5F, -0.8128F);
		RightLeg.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, -0.2618F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 22, 32, -1.1F, -0.5F, -1.5F, 2, 1, 3, -0.1F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(-3.1083F, 4.151F, -0.8684F);
		RightLeg.addChild(cube_r13);
		setRotationAngle(cube_r13, -0.1745F, -0.1745F, 0.7418F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 33, 12, -0.5F, -0.5F, -1.5F, 1, 1, 3, -0.1F, false));

		RightLeg1 = new ModelRenderer(this);
		RightLeg1.setRotationPoint(0.5F, 3.6667F, 2.5333F);
		RightLeg.addChild(RightLeg1);
		setRotationAngle(RightLeg1, 0.48F, -0.2618F, 0.0436F);

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(-2.7866F, -3.3303F, -0.9362F);
		RightLeg1.addChild(cube_r14);
		setRotationAngle(cube_r14, -0.3927F, 0.3491F, 0.6545F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 29, 28, -1.0F, -2.5F, -1.4F, 3, 4, 3, -0.1F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(-3.0F, -1.2667F, -1.4333F);
		RightLeg1.addChild(cube_r15);
		setRotationAngle(cube_r15, -0.5236F, 0.0F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 30, -1.5F, -1.7F, -2.0F, 3, 3, 3, -0.1F, false));

		Legdetail = new ModelRenderer(this);
		Legdetail.setRotationPoint(-3.0F, -1.2667F, -1.4333F);
		RightLeg1.addChild(Legdetail);

		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(2.75F, 19.0F, -3.0F);

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(3.0464F, 4.5F, -0.8128F);
		LeftLeg.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.0F, 0.2618F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 22, 32, -0.9F, -0.5F, -1.5F, 2, 1, 3, -0.1F, true));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(3.1083F, 4.151F, -0.8684F);
		LeftLeg.addChild(cube_r17);
		setRotationAngle(cube_r17, -0.1745F, 0.1745F, -0.7418F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 33, 12, -0.5F, -0.5F, -1.5F, 1, 1, 3, -0.1F, true));

		RightLeg4 = new ModelRenderer(this);
		RightLeg4.setRotationPoint(-0.5F, 3.6667F, 2.5333F);
		LeftLeg.addChild(RightLeg4);
		setRotationAngle(RightLeg4, 0.48F, 0.2618F, -0.0436F);

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(2.7866F, -3.3303F, -0.9362F);
		RightLeg4.addChild(cube_r18);
		setRotationAngle(cube_r18, -0.3927F, -0.3491F, -0.6545F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 29, 28, -2.0F, -2.5F, -1.4F, 3, 4, 3, -0.1F, true));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(3.0F, -1.2667F, -1.4333F);
		RightLeg4.addChild(cube_r19);
		setRotationAngle(cube_r19, -0.5236F, 0.0F, 0.0F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 30, -1.5F, -1.7F, -2.0F, 3, 3, 3, -0.1F, true));

		Legdetail2 = new ModelRenderer(this);
		Legdetail2.setRotationPoint(3.0F, -1.2667F, -1.4333F);
		RightLeg4.addChild(Legdetail2);

	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Head.render(f5);
		Body.render(f5);
		RightArm.render(f5);
		LeftArm.render(f5);
		RightLeg.render(f5);
		LeftLeg.render(f5);
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