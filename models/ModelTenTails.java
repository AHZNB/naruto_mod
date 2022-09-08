// Made with Blockbench 3.7.4
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports

public static class ModelTenTails extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer bipedBody;
	private final ModelRenderer waist;
	private final ModelRenderer chest;
	private final ModelRenderer hump;
	private final ModelRenderer bone61;
	private final ModelRenderer bone62;
	private final ModelRenderer bone63;
	private final ModelRenderer bone64;
	private final ModelRenderer bone65;
	private final ModelRenderer bone66;
	private final ModelRenderer bone67;
	private final ModelRenderer bone68;
	private final ModelRenderer bone69;
	private final ModelRenderer bone70;
	private final ModelRenderer bone71;
	private final ModelRenderer bone72;
	private final ModelRenderer bone73;
	private final ModelRenderer bone74;
	private final ModelRenderer bone75;
	private final ModelRenderer bone76;
	private final ModelRenderer bone77;
	private final ModelRenderer bone78;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r1;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r3;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r5;
	private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r7;
	private final ModelRenderer Tail1;
	private final ModelRenderer Tail1_1;
	private final ModelRenderer Tail1_2;
	private final ModelRenderer Tail1_3;
	private final ModelRenderer Tail1_4;
	private final ModelRenderer Tail1_5;
	private final ModelRenderer Tail1_6;
	private final ModelRenderer Tail2;
	private final ModelRenderer Tail2_1;
	private final ModelRenderer Tail2_2;
	private final ModelRenderer Tail2_3;
	private final ModelRenderer Tail2_4;
	private final ModelRenderer Tail2_5;
	private final ModelRenderer Tail2_6;
	private final ModelRenderer Tail3;
	private final ModelRenderer Tail3_1;
	private final ModelRenderer Tail3_2;
	private final ModelRenderer Tail3_3;
	private final ModelRenderer Tail3_4;
	private final ModelRenderer Tail3_5;
	private final ModelRenderer Tail3_6;
	private final ModelRenderer Tail4;
	private final ModelRenderer Tail4_1;
	private final ModelRenderer Tail4_2;
	private final ModelRenderer Tail4_3;
	private final ModelRenderer Tail4_4;
	private final ModelRenderer Tail4_5;
	private final ModelRenderer Tail4_6;
	private final ModelRenderer Tail5;
	private final ModelRenderer Tail5_1;
	private final ModelRenderer Tail5_2;
	private final ModelRenderer Tail5_3;
	private final ModelRenderer Tail5_4;
	private final ModelRenderer Tail5_5;
	private final ModelRenderer Tail5_6;
	private final ModelRenderer Tail6;
	private final ModelRenderer Tail6_1;
	private final ModelRenderer Tail6_2;
	private final ModelRenderer Tail6_3;
	private final ModelRenderer Tail6_4;
	private final ModelRenderer Tail6_5;
	private final ModelRenderer Tail6_6;
	private final ModelRenderer Tail7;
	private final ModelRenderer Tail7_1;
	private final ModelRenderer Tail7_2;
	private final ModelRenderer Tail7_3;
	private final ModelRenderer Tail7_4;
	private final ModelRenderer Tail7_5;
	private final ModelRenderer Tail7_6;
	private final ModelRenderer Tail8;
	private final ModelRenderer Tail8_1;
	private final ModelRenderer Tail8_2;
	private final ModelRenderer Tail8_3;
	private final ModelRenderer Tail8_4;
	private final ModelRenderer Tail8_5;
	private final ModelRenderer Tail8_6;
	private final ModelRenderer Tail9;
	private final ModelRenderer Tail9_1;
	private final ModelRenderer Tail9_2;
	private final ModelRenderer Tail9_3;
	private final ModelRenderer Tail9_4;
	private final ModelRenderer Tail9_5;
	private final ModelRenderer Tail9_6;
	private final ModelRenderer Tail10;
	private final ModelRenderer Tail10_1;
	private final ModelRenderer Tail10_2;
	private final ModelRenderer Tail10_3;
	private final ModelRenderer Tail10_4;
	private final ModelRenderer Tail10_5;
	private final ModelRenderer Tail10_6;

	public ModelTenTails() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, -9.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -7.7412F, -3.0341F, 8, 8, 8, 0.0F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, -9.0F);
		bipedHeadwear.cubeList
				.add(new ModelBox(bipedHeadwear, 0, 32, -4.0F, -7.7412F, -3.0341F, 8, 8, 8, 0.05F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 20.375F, 0.125F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 30, 0, -6.0F, -13.375F, -2.125F, 12, 5, 5, 0.0F, false));

		waist = new ModelRenderer(this);
		waist.setRotationPoint(0.0F, -11.875F, -0.125F);
		bipedBody.addChild(waist);
		setRotationAngle(waist, 0.5236F, 0.0F, 0.0F);
		waist.cubeList.add(new ModelBox(waist, 30, 0, -7.0F, -4.5F, -2.0F, 14, 5, 5, 0.0F, false));

		chest = new ModelRenderer(this);
		chest.setRotationPoint(0.0F, -17.375F, -4.125F);
		bipedBody.addChild(chest);
		setRotationAngle(chest, 0.7854F, 0.0F, 0.0F);
		chest.cubeList.add(new ModelBox(chest, 30, 0, -8.0F, -6.0F, -3.0F, 16, 10, 6, 0.0F, false));

		hump = new ModelRenderer(this);
		hump.setRotationPoint(-1.0F, -19.375F, -3.125F);
		bipedBody.addChild(hump);
		setRotationAngle(hump, -0.7418F, 0.0F, 0.0F);
		hump.cubeList.add(new ModelBox(hump, 30, 0, -3.0F, -4.6756F, -2.2627F, 6, 4, 6, 0.0F, false));
		hump.cubeList.add(new ModelBox(hump, 30, 0, -2.0F, -6.6756F, -1.0127F, 4, 2, 4, 0.0F, false));
		hump.cubeList.add(new ModelBox(hump, 30, 0, -1.0F, -8.6756F, -0.0127F, 2, 2, 2, 0.0F, false));
		hump.cubeList.add(new ModelBox(hump, 30, 0, -0.5F, -12.6756F, 0.4873F, 1, 4, 1, 0.0F, false));

		bone61 = new ModelRenderer(this);
		bone61.setRotationPoint(2.5F, -2.0F, -1.5F);
		hump.addChild(bone61);
		setRotationAngle(bone61, 0.0F, 0.0F, 0.6109F);
		bone61.cubeList.add(new ModelBox(bone61, 30, 0, -0.8875F, -2.5534F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone62 = new ModelRenderer(this);
		bone62.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone61.addChild(bone62);
		setRotationAngle(bone62, 0.0F, 0.0F, -0.1309F);
		bone62.cubeList.add(new ModelBox(bone62, 30, 0, -0.812F, -2.5993F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone63 = new ModelRenderer(this);
		bone63.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone62.addChild(bone63);
		setRotationAngle(bone63, 0.0F, 0.0F, -0.1309F);
		bone63.cubeList.add(new ModelBox(bone63, 30, 0, -0.7311F, -2.6348F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone64 = new ModelRenderer(this);
		bone64.setRotationPoint(2.5F, -3.0F, 1.5F);
		hump.addChild(bone64);
		setRotationAngle(bone64, 0.0F, 0.0F, 0.9163F);
		bone64.cubeList.add(new ModelBox(bone64, 30, 0, -1.036F, -2.4113F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone65 = new ModelRenderer(this);
		bone65.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone64.addChild(bone65);
		setRotationAngle(bone65, 0.0F, 0.0F, -0.1309F);
		bone65.cubeList.add(new ModelBox(bone65, 30, 0, -0.9777F, -2.4777F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone66 = new ModelRenderer(this);
		bone66.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone65.addChild(bone66);
		setRotationAngle(bone66, 0.0F, 0.0F, -0.1309F);
		bone66.cubeList.add(new ModelBox(bone66, 30, 0, -0.9113F, -2.536F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone67 = new ModelRenderer(this);
		bone67.setRotationPoint(0.5F, -3.0F, 2.5F);
		hump.addChild(bone67);
		setRotationAngle(bone67, -0.6545F, 0.0F, 0.0F);
		bone67.cubeList.add(new ModelBox(bone67, 30, 0, -0.5F, -2.9848F, -0.3264F, 1, 2, 1, 0.0F, false));

		bone68 = new ModelRenderer(this);
		bone68.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone67.addChild(bone68);
		setRotationAngle(bone68, 0.1309F, 0.0F, 0.0F);
		bone68.cubeList.add(new ModelBox(bone68, 30, 0, -0.5F, -2.9537F, -0.1993F, 1, 2, 1, 0.0F, false));

		bone69 = new ModelRenderer(this);
		bone69.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone68.addChild(bone69);
		setRotationAngle(bone69, 0.1309F, 0.0F, 0.0F);
		bone69.cubeList.add(new ModelBox(bone69, 30, 0, -0.5F, -2.9063F, -0.0774F, 1, 2, 1, 0.0F, false));

		bone70 = new ModelRenderer(this);
		bone70.setRotationPoint(-2.5F, -3.0F, 2.5F);
		hump.addChild(bone70);
		setRotationAngle(bone70, 0.0F, 0.0F, -0.6545F);
		bone70.cubeList.add(new ModelBox(bone70, 30, 0, -0.0887F, -2.536F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone71 = new ModelRenderer(this);
		bone71.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone70.addChild(bone71);
		setRotationAngle(bone71, 0.0F, 0.0F, 0.1309F);
		bone71.cubeList.add(new ModelBox(bone71, 30, 0, -0.1622F, -2.5851F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone72 = new ModelRenderer(this);
		bone72.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone71.addChild(bone72);
		setRotationAngle(bone72, 0.0F, 0.0F, 0.1309F);
		bone72.cubeList.add(new ModelBox(bone72, 30, 0, -0.2415F, -2.6242F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone73 = new ModelRenderer(this);
		bone73.setRotationPoint(-2.5F, -2.0F, -0.5F);
		hump.addChild(bone73);
		setRotationAngle(bone73, 0.0F, 0.0F, -1.0472F);
		bone73.cubeList.add(new ModelBox(bone73, 30, 0, 0.0851F, -2.3378F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone74 = new ModelRenderer(this);
		bone74.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone73.addChild(bone74);
		setRotationAngle(bone74, 0.0F, 0.0F, 0.1309F);
		bone74.cubeList.add(new ModelBox(bone74, 30, 0, 0.036F, -2.4113F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone75 = new ModelRenderer(this);
		bone75.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone74.addChild(bone75);
		setRotationAngle(bone75, 0.0F, 0.0F, 0.1309F);
		bone75.cubeList.add(new ModelBox(bone75, 30, 0, -0.0223F, -2.4777F, 0.2373F, 1, 2, 1, 0.0F, false));

		bone76 = new ModelRenderer(this);
		bone76.setRotationPoint(-0.5F, -2.0F, -2.5F);
		hump.addChild(bone76);
		setRotationAngle(bone76, 0.7418F, 0.0F, 0.0F);
		bone76.cubeList.add(new ModelBox(bone76, 30, 0, -0.5F, -2.0F, 0.5F, 1, 2, 1, 0.0F, false));

		bone77 = new ModelRenderer(this);
		bone77.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone76.addChild(bone77);
		setRotationAngle(bone77, -0.1309F, 0.0F, 0.0F);
		bone77.cubeList.add(new ModelBox(bone77, 30, 0, -0.5F, -2.1305F, 0.4914F, 1, 2, 1, 0.0F, false));

		bone78 = new ModelRenderer(this);
		bone78.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone77.addChild(bone78);
		setRotationAngle(bone78, -0.1309F, 0.0F, 0.0F);
		bone78.cubeList.add(new ModelBox(bone78, 30, 0, -0.5F, -2.2588F, 0.4659F, 1, 2, 1, 0.0F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-7.0F, 1.0F, -7.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 16, -1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, 0.0F, 0.3491F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 16, -4.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-5.0F, 8.2791F, -0.1201F);
		bipedRightArm.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.5236F, 0.0F, -0.2618F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 16, -2.0F, -1.0F, -2.0F, 4, 12, 4, 0.0F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(7.0F, 1.0F, -7.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 16, -1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.0F, -0.3491F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 16, 0.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(5.0F, 8.2791F, -0.1201F);
		bipedLeftArm.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.5236F, 0.0F, 0.2618F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 16, -2.0F, -1.0F, -2.0F, 4, 12, 4, 0.0F, true));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-0.5F, 0.0F, 1.0F);
		bipedRightLeg.addChild(cube_r6);
		setRotationAngle(cube_r6, -0.7854F, 0.0F, 0.7854F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 16, 16, -2.5F, 0.0F, -2.5F, 5, 12, 5, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-6.6F, 4.0F, -7.0F);
		bipedRightLeg.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.3491F, 0.0F, -0.3491F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 16, 16, -2.0F, 0.0F, -2.0F, 4, 8, 4, 0.0F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.5F, 0.0F, 1.0F);
		bipedLeftLeg.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.7854F, 0.0F, -0.7854F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 16, 16, -2.5F, 0.0F, -2.5F, 5, 12, 5, 0.0F, true));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(6.6F, 4.0F, -7.0F);
		bipedLeftLeg.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.3491F, 0.0F, 0.3491F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 16, 16, -2.0F, 0.0F, -2.0F, 4, 8, 4, 0.0F, true));

		Tail1 = new ModelRenderer(this);
		Tail1.setRotationPoint(4.0F, 12.0F, 5.0F);
		setRotationAngle(Tail1, -0.2618F, 0.0F, 1.4835F);
		Tail1.cubeList.add(new ModelBox(Tail1, 32, 16, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail1_1 = new ModelRenderer(this);
		Tail1_1.setRotationPoint(0.0F, -7.0F, 0.25F);
		Tail1.addChild(Tail1_1);
		setRotationAngle(Tail1_1, 0.0F, 0.0F, -0.0873F);
		Tail1_1.cubeList.add(new ModelBox(Tail1_1, 32, 16, -2.0F, -7.0F, -2.25F, 4, 8, 4, 0.0F, false));

		Tail1_2 = new ModelRenderer(this);
		Tail1_2.setRotationPoint(0.0F, -6.0F, -0.25F);
		Tail1_1.addChild(Tail1_2);
		setRotationAngle(Tail1_2, 0.0F, 0.0F, -0.0873F);
		Tail1_2.cubeList.add(new ModelBox(Tail1_2, 32, 16, -2.0F, -7.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail1_3 = new ModelRenderer(this);
		Tail1_3.setRotationPoint(0.0F, -5.75F, 0.0F);
		Tail1_2.addChild(Tail1_3);
		setRotationAngle(Tail1_3, 0.0F, 0.0F, -0.0873F);
		Tail1_3.cubeList.add(new ModelBox(Tail1_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail1_4 = new ModelRenderer(this);
		Tail1_4.setRotationPoint(0.0F, -6.25F, 0.0F);
		Tail1_3.addChild(Tail1_4);
		setRotationAngle(Tail1_4, 0.0F, 0.0F, -0.0873F);
		Tail1_4.cubeList.add(new ModelBox(Tail1_4, 32, 16, -2.0F, -7.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail1_5 = new ModelRenderer(this);
		Tail1_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail1_4.addChild(Tail1_5);
		setRotationAngle(Tail1_5, 0.0F, 0.0F, -0.0873F);
		Tail1_5.cubeList.add(new ModelBox(Tail1_5, 32, 16, -2.0F, -7.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail1_6 = new ModelRenderer(this);
		Tail1_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail1_5.addChild(Tail1_6);
		setRotationAngle(Tail1_6, 0.0F, 0.0F, -0.0873F);
		Tail1_6.cubeList.add(new ModelBox(Tail1_6, 32, 16, -2.0F, -7.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail2 = new ModelRenderer(this);
		Tail2.setRotationPoint(3.0F, 12.0F, 4.0F);
		setRotationAngle(Tail2, -0.2618F, 0.0F, 1.1345F);
		Tail2.cubeList.add(new ModelBox(Tail2, 32, 16, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail2_1 = new ModelRenderer(this);
		Tail2_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail2.addChild(Tail2_1);
		setRotationAngle(Tail2_1, 0.0F, 0.0F, -0.0873F);
		Tail2_1.cubeList.add(new ModelBox(Tail2_1, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail2_2 = new ModelRenderer(this);
		Tail2_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail2_1.addChild(Tail2_2);
		setRotationAngle(Tail2_2, 0.0F, 0.0F, -0.0873F);
		Tail2_2.cubeList.add(new ModelBox(Tail2_2, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail2_3 = new ModelRenderer(this);
		Tail2_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail2_2.addChild(Tail2_3);
		setRotationAngle(Tail2_3, 0.0F, 0.0F, -0.0873F);
		Tail2_3.cubeList.add(new ModelBox(Tail2_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail2_4 = new ModelRenderer(this);
		Tail2_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail2_3.addChild(Tail2_4);
		setRotationAngle(Tail2_4, 0.0F, 0.0F, -0.0873F);
		Tail2_4.cubeList.add(new ModelBox(Tail2_4, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail2_5 = new ModelRenderer(this);
		Tail2_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail2_4.addChild(Tail2_5);
		setRotationAngle(Tail2_5, 0.0F, 0.0F, -0.0873F);
		Tail2_5.cubeList.add(new ModelBox(Tail2_5, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail2_6 = new ModelRenderer(this);
		Tail2_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail2_5.addChild(Tail2_6);
		setRotationAngle(Tail2_6, 0.0F, 0.0F, -0.0873F);
		Tail2_6.cubeList.add(new ModelBox(Tail2_6, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail3 = new ModelRenderer(this);
		Tail3.setRotationPoint(2.0F, 12.0F, 4.75F);
		setRotationAngle(Tail3, -0.2618F, 0.0F, 0.7854F);
		Tail3.cubeList.add(new ModelBox(Tail3, 32, 16, -2.0F, -8.0F, -1.75F, 4, 8, 4, 0.0F, false));

		Tail3_1 = new ModelRenderer(this);
		Tail3_1.setRotationPoint(0.0F, -6.75F, 0.25F);
		Tail3.addChild(Tail3_1);
		setRotationAngle(Tail3_1, 0.0F, 0.0F, -0.0873F);
		Tail3_1.cubeList.add(new ModelBox(Tail3_1, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail3_2 = new ModelRenderer(this);
		Tail3_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail3_1.addChild(Tail3_2);
		setRotationAngle(Tail3_2, 0.0F, 0.0F, -0.0873F);
		Tail3_2.cubeList.add(new ModelBox(Tail3_2, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail3_3 = new ModelRenderer(this);
		Tail3_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail3_2.addChild(Tail3_3);
		setRotationAngle(Tail3_3, 0.0F, 0.0F, -0.0873F);
		Tail3_3.cubeList.add(new ModelBox(Tail3_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail3_4 = new ModelRenderer(this);
		Tail3_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail3_3.addChild(Tail3_4);
		setRotationAngle(Tail3_4, 0.0F, 0.0F, -0.0873F);
		Tail3_4.cubeList.add(new ModelBox(Tail3_4, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail3_5 = new ModelRenderer(this);
		Tail3_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail3_4.addChild(Tail3_5);
		setRotationAngle(Tail3_5, 0.0F, 0.0F, -0.0873F);
		Tail3_5.cubeList.add(new ModelBox(Tail3_5, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail3_6 = new ModelRenderer(this);
		Tail3_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail3_5.addChild(Tail3_6);
		setRotationAngle(Tail3_6, 0.0F, 0.0F, -0.0873F);
		Tail3_6.cubeList.add(new ModelBox(Tail3_6, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail4 = new ModelRenderer(this);
		Tail4.setRotationPoint(1.0F, 12.0F, 5.0F);
		setRotationAngle(Tail4, -0.2618F, 0.0F, 0.4363F);
		Tail4.cubeList.add(new ModelBox(Tail4, 32, 16, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail4_1 = new ModelRenderer(this);
		Tail4_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail4.addChild(Tail4_1);
		setRotationAngle(Tail4_1, 0.0F, 0.0F, -0.0873F);
		Tail4_1.cubeList.add(new ModelBox(Tail4_1, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail4_2 = new ModelRenderer(this);
		Tail4_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail4_1.addChild(Tail4_2);
		setRotationAngle(Tail4_2, 0.0F, 0.0F, -0.0873F);
		Tail4_2.cubeList.add(new ModelBox(Tail4_2, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail4_3 = new ModelRenderer(this);
		Tail4_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail4_2.addChild(Tail4_3);
		setRotationAngle(Tail4_3, 0.0F, 0.0F, -0.0873F);
		Tail4_3.cubeList.add(new ModelBox(Tail4_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail4_4 = new ModelRenderer(this);
		Tail4_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail4_3.addChild(Tail4_4);
		setRotationAngle(Tail4_4, 0.0F, 0.0F, -0.0873F);
		Tail4_4.cubeList.add(new ModelBox(Tail4_4, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail4_5 = new ModelRenderer(this);
		Tail4_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail4_4.addChild(Tail4_5);
		setRotationAngle(Tail4_5, 0.0F, 0.0F, -0.0873F);
		Tail4_5.cubeList.add(new ModelBox(Tail4_5, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail4_6 = new ModelRenderer(this);
		Tail4_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail4_5.addChild(Tail4_6);
		setRotationAngle(Tail4_6, 0.0F, 0.0F, -0.0873F);
		Tail4_6.cubeList.add(new ModelBox(Tail4_6, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail5 = new ModelRenderer(this);
		Tail5.setRotationPoint(0.0F, 12.0F, 5.0F);
		setRotationAngle(Tail5, -0.7854F, 0.0F, 0.0873F);
		Tail5.cubeList.add(new ModelBox(Tail5, 32, 16, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail5_1 = new ModelRenderer(this);
		Tail5_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail5.addChild(Tail5_1);
		setRotationAngle(Tail5_1, 0.0873F, 0.0F, 0.0F);
		Tail5_1.cubeList.add(new ModelBox(Tail5_1, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail5_2 = new ModelRenderer(this);
		Tail5_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail5_1.addChild(Tail5_2);
		setRotationAngle(Tail5_2, 0.0873F, 0.0F, 0.0F);
		Tail5_2.cubeList.add(new ModelBox(Tail5_2, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail5_3 = new ModelRenderer(this);
		Tail5_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail5_2.addChild(Tail5_3);
		setRotationAngle(Tail5_3, 0.0873F, 0.0F, 0.0F);
		Tail5_3.cubeList.add(new ModelBox(Tail5_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail5_4 = new ModelRenderer(this);
		Tail5_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail5_3.addChild(Tail5_4);
		setRotationAngle(Tail5_4, 0.0873F, 0.0F, 0.0F);
		Tail5_4.cubeList.add(new ModelBox(Tail5_4, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail5_5 = new ModelRenderer(this);
		Tail5_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail5_4.addChild(Tail5_5);
		setRotationAngle(Tail5_5, 0.0873F, 0.0F, 0.0F);
		Tail5_5.cubeList.add(new ModelBox(Tail5_5, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail5_6 = new ModelRenderer(this);
		Tail5_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail5_5.addChild(Tail5_6);
		setRotationAngle(Tail5_6, 0.0873F, 0.0F, 0.0F);
		Tail5_6.cubeList.add(new ModelBox(Tail5_6, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail6 = new ModelRenderer(this);
		Tail6.setRotationPoint(-1.0F, 12.0F, 5.0F);
		setRotationAngle(Tail6, -0.2618F, 0.0F, -0.4363F);
		Tail6.cubeList.add(new ModelBox(Tail6, 32, 16, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail6_1 = new ModelRenderer(this);
		Tail6_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail6.addChild(Tail6_1);
		setRotationAngle(Tail6_1, 0.0F, 0.0F, 0.0873F);
		Tail6_1.cubeList.add(new ModelBox(Tail6_1, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail6_2 = new ModelRenderer(this);
		Tail6_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail6_1.addChild(Tail6_2);
		setRotationAngle(Tail6_2, 0.0F, 0.0F, 0.0873F);
		Tail6_2.cubeList.add(new ModelBox(Tail6_2, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail6_3 = new ModelRenderer(this);
		Tail6_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail6_2.addChild(Tail6_3);
		setRotationAngle(Tail6_3, 0.0F, 0.0F, 0.0873F);
		Tail6_3.cubeList.add(new ModelBox(Tail6_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail6_4 = new ModelRenderer(this);
		Tail6_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail6_3.addChild(Tail6_4);
		setRotationAngle(Tail6_4, 0.0F, 0.0F, 0.0873F);
		Tail6_4.cubeList.add(new ModelBox(Tail6_4, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail6_5 = new ModelRenderer(this);
		Tail6_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail6_4.addChild(Tail6_5);
		setRotationAngle(Tail6_5, 0.0F, 0.0F, 0.0873F);
		Tail6_5.cubeList.add(new ModelBox(Tail6_5, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail6_6 = new ModelRenderer(this);
		Tail6_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail6_5.addChild(Tail6_6);
		setRotationAngle(Tail6_6, 0.0F, 0.0F, 0.0873F);
		Tail6_6.cubeList.add(new ModelBox(Tail6_6, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail7 = new ModelRenderer(this);
		Tail7.setRotationPoint(-2.0F, 12.0F, 5.0F);
		setRotationAngle(Tail7, -0.2618F, 0.0F, -0.7854F);
		Tail7.cubeList.add(new ModelBox(Tail7, 32, 16, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail7_1 = new ModelRenderer(this);
		Tail7_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail7.addChild(Tail7_1);
		setRotationAngle(Tail7_1, 0.0F, 0.0F, 0.0873F);
		Tail7_1.cubeList.add(new ModelBox(Tail7_1, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail7_2 = new ModelRenderer(this);
		Tail7_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail7_1.addChild(Tail7_2);
		setRotationAngle(Tail7_2, 0.0F, 0.0F, 0.0873F);
		Tail7_2.cubeList.add(new ModelBox(Tail7_2, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail7_3 = new ModelRenderer(this);
		Tail7_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail7_2.addChild(Tail7_3);
		setRotationAngle(Tail7_3, 0.0F, 0.0F, 0.0873F);
		Tail7_3.cubeList.add(new ModelBox(Tail7_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail7_4 = new ModelRenderer(this);
		Tail7_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail7_3.addChild(Tail7_4);
		setRotationAngle(Tail7_4, 0.0F, 0.0F, 0.0873F);
		Tail7_4.cubeList.add(new ModelBox(Tail7_4, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail7_5 = new ModelRenderer(this);
		Tail7_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail7_4.addChild(Tail7_5);
		setRotationAngle(Tail7_5, 0.0F, 0.0F, 0.0873F);
		Tail7_5.cubeList.add(new ModelBox(Tail7_5, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail7_6 = new ModelRenderer(this);
		Tail7_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail7_5.addChild(Tail7_6);
		setRotationAngle(Tail7_6, 0.0F, 0.0F, 0.0873F);
		Tail7_6.cubeList.add(new ModelBox(Tail7_6, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail8 = new ModelRenderer(this);
		Tail8.setRotationPoint(-3.0F, 12.0F, 4.0F);
		setRotationAngle(Tail8, -0.2618F, 0.0F, -1.1345F);
		Tail8.cubeList.add(new ModelBox(Tail8, 32, 16, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail8_1 = new ModelRenderer(this);
		Tail8_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail8.addChild(Tail8_1);
		setRotationAngle(Tail8_1, 0.0F, 0.0F, 0.0873F);
		Tail8_1.cubeList.add(new ModelBox(Tail8_1, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail8_2 = new ModelRenderer(this);
		Tail8_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail8_1.addChild(Tail8_2);
		setRotationAngle(Tail8_2, 0.0F, 0.0F, 0.0873F);
		Tail8_2.cubeList.add(new ModelBox(Tail8_2, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail8_3 = new ModelRenderer(this);
		Tail8_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail8_2.addChild(Tail8_3);
		setRotationAngle(Tail8_3, 0.0F, 0.0F, 0.0873F);
		Tail8_3.cubeList.add(new ModelBox(Tail8_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail8_4 = new ModelRenderer(this);
		Tail8_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail8_3.addChild(Tail8_4);
		setRotationAngle(Tail8_4, 0.0F, 0.0F, 0.0873F);
		Tail8_4.cubeList.add(new ModelBox(Tail8_4, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail8_5 = new ModelRenderer(this);
		Tail8_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail8_4.addChild(Tail8_5);
		setRotationAngle(Tail8_5, 0.0F, 0.0F, 0.0873F);
		Tail8_5.cubeList.add(new ModelBox(Tail8_5, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail8_6 = new ModelRenderer(this);
		Tail8_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail8_5.addChild(Tail8_6);
		setRotationAngle(Tail8_6, 0.0F, 0.0F, 0.0873F);
		Tail8_6.cubeList.add(new ModelBox(Tail8_6, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail9 = new ModelRenderer(this);
		Tail9.setRotationPoint(-4.0F, 12.0F, 5.0F);
		setRotationAngle(Tail9, -0.2618F, 0.0F, -1.4835F);
		Tail9.cubeList.add(new ModelBox(Tail9, 32, 16, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail9_1 = new ModelRenderer(this);
		Tail9_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail9.addChild(Tail9_1);
		setRotationAngle(Tail9_1, 0.0F, 0.0F, 0.0873F);
		Tail9_1.cubeList.add(new ModelBox(Tail9_1, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail9_2 = new ModelRenderer(this);
		Tail9_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail9_1.addChild(Tail9_2);
		setRotationAngle(Tail9_2, 0.0F, 0.0F, 0.0873F);
		Tail9_2.cubeList.add(new ModelBox(Tail9_2, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail9_3 = new ModelRenderer(this);
		Tail9_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail9_2.addChild(Tail9_3);
		setRotationAngle(Tail9_3, 0.0F, 0.0F, 0.0873F);
		Tail9_3.cubeList.add(new ModelBox(Tail9_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail9_4 = new ModelRenderer(this);
		Tail9_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail9_3.addChild(Tail9_4);
		setRotationAngle(Tail9_4, 0.0F, 0.0F, 0.0873F);
		Tail9_4.cubeList.add(new ModelBox(Tail9_4, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail9_5 = new ModelRenderer(this);
		Tail9_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail9_4.addChild(Tail9_5);
		setRotationAngle(Tail9_5, 0.0F, 0.0F, 0.0873F);
		Tail9_5.cubeList.add(new ModelBox(Tail9_5, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail9_6 = new ModelRenderer(this);
		Tail9_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail9_5.addChild(Tail9_6);
		setRotationAngle(Tail9_6, 0.0F, 0.0F, 0.0873F);
		Tail9_6.cubeList.add(new ModelBox(Tail9_6, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail10 = new ModelRenderer(this);
		Tail10.setRotationPoint(0.0F, 12.0F, 4.0F);
		setRotationAngle(Tail10, -0.3491F, 0.0F, -0.0873F);
		Tail10.cubeList.add(new ModelBox(Tail10, 32, 16, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail10_1 = new ModelRenderer(this);
		Tail10_1.setRotationPoint(0.0F, -6.75F, 0.0F);
		Tail10.addChild(Tail10_1);
		setRotationAngle(Tail10_1, 0.0F, 0.0F, 0.0873F);
		Tail10_1.cubeList.add(new ModelBox(Tail10_1, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail10_2 = new ModelRenderer(this);
		Tail10_2.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail10_1.addChild(Tail10_2);
		setRotationAngle(Tail10_2, 0.0F, 0.0F, -0.0873F);
		Tail10_2.cubeList.add(new ModelBox(Tail10_2, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail10_3 = new ModelRenderer(this);
		Tail10_3.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail10_2.addChild(Tail10_3);
		setRotationAngle(Tail10_3, 0.0F, 0.0F, 0.0873F);
		Tail10_3.cubeList.add(new ModelBox(Tail10_3, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail10_4 = new ModelRenderer(this);
		Tail10_4.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail10_3.addChild(Tail10_4);
		setRotationAngle(Tail10_4, 0.0F, 0.0F, -0.0873F);
		Tail10_4.cubeList.add(new ModelBox(Tail10_4, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail10_5 = new ModelRenderer(this);
		Tail10_5.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail10_4.addChild(Tail10_5);
		setRotationAngle(Tail10_5, 0.0F, 0.0F, 0.0873F);
		Tail10_5.cubeList.add(new ModelBox(Tail10_5, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));

		Tail10_6 = new ModelRenderer(this);
		Tail10_6.setRotationPoint(0.0F, -6.0F, 0.0F);
		Tail10_5.addChild(Tail10_6);
		setRotationAngle(Tail10_6, 0.0F, 0.0F, -0.0873F);
		Tail10_6.cubeList.add(new ModelBox(Tail10_6, 32, 16, -2.0F, -7.25F, -2.0F, 4, 8, 4, 0.0F, false));
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
		Tail1.render(f5);
		Tail2.render(f5);
		Tail3.render(f5);
		Tail4.render(f5);
		Tail5.render(f5);
		Tail6.render(f5);
		Tail7.render(f5);
		Tail8.render(f5);
		Tail9.render(f5);
		Tail10.render(f5);
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