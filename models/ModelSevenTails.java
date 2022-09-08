// Made with Blockbench 3.9.2
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelSevenTails extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer chin;
	private final ModelRenderer mandible;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer hair;
	private final ModelRenderer bone31;
	private final ModelRenderer bone32;
	private final ModelRenderer bone33;
	private final ModelRenderer bone34;
	private final ModelRenderer bone35;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer bipedBody;
	private final ModelRenderer rightShoulder;
	private final ModelRenderer bone19;
	private final ModelRenderer bone20;
	private final ModelRenderer bone21;
	private final ModelRenderer bone22;
	private final ModelRenderer bone23;
	private final ModelRenderer shoulderSpike;
	private final ModelRenderer bone37;
	private final ModelRenderer bone38;
	private final ModelRenderer bone39;
	private final ModelRenderer bone40;
	private final ModelRenderer bone24;
	private final ModelRenderer bone25;
	private final ModelRenderer bone26;
	private final ModelRenderer bone27;
	private final ModelRenderer leftShoulder;
	private final ModelRenderer bone12;
	private final ModelRenderer bone18;
	private final ModelRenderer bone28;
	private final ModelRenderer bone29;
	private final ModelRenderer bone30;
	private final ModelRenderer shoulderSpike2;
	private final ModelRenderer bone36;
	private final ModelRenderer bone41;
	private final ModelRenderer bone42;
	private final ModelRenderer bone43;
	private final ModelRenderer bone44;
	private final ModelRenderer bone45;
	private final ModelRenderer bone46;
	private final ModelRenderer bone47;
	private final ModelRenderer Chest;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer bone8;
	private final ModelRenderer bone48;
	private final ModelRenderer stomach;
	private final ModelRenderer bone11;
	private final ModelRenderer bone10;
	private final ModelRenderer bone9;
	private final ModelRenderer bone7;
	private final ModelRenderer bone13;
	private final ModelRenderer bone14;
	private final ModelRenderer bone15;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer tail0;
	private final ModelRenderer tail1;
	private final ModelRenderer tail2;
	private final ModelRenderer tail3;
	private final ModelRenderer tail4;
	private final ModelRenderer tail5;
	private final ModelRenderer tail6_0;
	private final ModelRenderer tail6_1;
	private final ModelRenderer tail6_2;
	private final ModelRenderer tail6_3;
	private final ModelRenderer tail6_4;
	private final ModelRenderer tail6_5;
	private final ModelRenderer tail6_6;
	private final ModelRenderer tail6_7;
	private final ModelRenderer tail6_8;
	private final ModelRenderer tail6_9;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer arm1;
	private final ModelRenderer cube_r7;
	private final ModelRenderer bone52;
	private final ModelRenderer bone53;
	private final ModelRenderer bone54;
	private final ModelRenderer arm2;
	private final ModelRenderer cube_r8;
	private final ModelRenderer bone49;
	private final ModelRenderer bone50;
	private final ModelRenderer bone51;
	private final ModelRenderer arm3;
	private final ModelRenderer cube_r9;
	private final ModelRenderer bone55;
	private final ModelRenderer bone56;
	private final ModelRenderer bone57;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer arm4;
	private final ModelRenderer cube_r10;
	private final ModelRenderer bone58;
	private final ModelRenderer bone59;
	private final ModelRenderer bone60;
	private final ModelRenderer arm5;
	private final ModelRenderer cube_r11;
	private final ModelRenderer bone61;
	private final ModelRenderer bone62;
	private final ModelRenderer bone63;
	private final ModelRenderer arm6;
	private final ModelRenderer cube_r12;
	private final ModelRenderer bone64;
	private final ModelRenderer bone65;
	private final ModelRenderer bone66;

	public ModelSevenTails() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 2.5F, -0.5F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 12, 34, -1.5F, -2.0F, -3.0F, 3, 2, 3, -0.05F, false));

		chin = new ModelRenderer(this);
		chin.setRotationPoint(0.0F, -0.1F, -2.75F);
		bipedHead.addChild(chin);
		setRotationAngle(chin, 0.4363F, 0.0F, 0.0F);
		chin.cubeList.add(new ModelBox(chin, 38, 0, -1.5F, -0.2F, -0.3F, 3, 1, 3, -0.1F, false));

		mandible = new ModelRenderer(this);
		mandible.setRotationPoint(0.5F, 0.4F, -0.25F);
		chin.addChild(mandible);
		setRotationAngle(mandible, 0.5236F, 0.0F, 0.0F);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-0.35F, 2.125F, -0.825F);
		mandible.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.1309F, 0.1004F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 11, 53, -1.575F, -3.475F, -2.9F, 4, 4, 7, -2.28F, true));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-0.65F, 2.125F, -0.825F);
		mandible.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.1309F, -0.1004F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 11, 53, -2.425F, -3.475F, -2.9F, 4, 4, 7, -2.28F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-1.1894F, 0.5802F, -0.4707F);
		mandible.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.1309F, 0.1004F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 40, 35, 0.95F, -0.5F, -1.35F, 1, 1, 3, -0.2F, true));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-1.075F, 0.5998F, -0.6086F);
		mandible.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.1309F, -0.1004F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 40, 35, -0.725F, -0.5F, -1.35F, 1, 1, 3, -0.2F, false));

		hair = new ModelRenderer(this);
		hair.setRotationPoint(0.0F, -1.15F, -1.55F);
		bipedHead.addChild(hair);
		setRotationAngle(hair, -0.545F, 0.7483F, -0.4043F);
		hair.cubeList.add(new ModelBox(hair, 24, 34, -1.5F, -1.75F, -1.5F, 3, 2, 3, -0.4F, false));

		bone31 = new ModelRenderer(this);
		bone31.setRotationPoint(0.9F, -1.45F, -0.9F);
		hair.addChild(bone31);
		setRotationAngle(bone31, 0.0873F, 0.0F, 0.0873F);
		bone31.cubeList.add(new ModelBox(bone31, 44, 45, -1.9F, -0.825F, -0.1F, 2, 2, 2, -0.1F, false));

		bone32 = new ModelRenderer(this);
		bone32.setRotationPoint(-0.2F, -0.8125F, 0.2F);
		bone31.addChild(bone32);
		setRotationAngle(bone32, 0.0436F, 0.0F, 0.0436F);
		bone32.cubeList.add(new ModelBox(bone32, 44, 45, -1.7F, -1.0625F, -0.3F, 2, 2, 2, -0.3F, false));

		bone33 = new ModelRenderer(this);
		bone33.setRotationPoint(-0.2F, -1.1042F, 0.2F);
		bone32.addChild(bone33);
		setRotationAngle(bone33, 0.0436F, 0.0F, 0.0436F);
		bone33.cubeList.add(new ModelBox(bone33, 44, 45, -1.5F, -0.9333F, -0.5F, 2, 2, 2, -0.5F, false));

		bone34 = new ModelRenderer(this);
		bone34.setRotationPoint(-0.25F, -0.3333F, 0.25F);
		bone33.addChild(bone34);
		setRotationAngle(bone34, 0.0436F, 0.0F, 0.0436F);
		bone34.cubeList.add(new ModelBox(bone34, 44, 45, -1.25F, -1.25F, -0.75F, 2, 2, 2, -0.7F, false));

		bone35 = new ModelRenderer(this);
		bone35.setRotationPoint(0.0F, -0.4F, 0.0F);
		bone34.addChild(bone35);
		setRotationAngle(bone35, 0.0436F, 0.0F, 0.0436F);
		bone35.cubeList.add(new ModelBox(bone35, 44, 45, -1.25F, -1.25F, -0.75F, 2, 2, 2, -0.8F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 2.5F, -0.5F);
		bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 36, 53, -2.0F, -2.6F, -3.505F, 4, 3, 4, -0.5F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);

		rightShoulder = new ModelRenderer(this);
		rightShoulder.setRotationPoint(-3.0F, 0.75F, 0.0F);
		bipedBody.addChild(rightShoulder);
		setRotationAngle(rightShoulder, 0.0F, 0.0F, -0.4363F);

		bone19 = new ModelRenderer(this);
		bone19.setRotationPoint(-1.0F, -1.0F, -2.0F);
		rightShoulder.addChild(bone19);

		bone20 = new ModelRenderer(this);
		bone20.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone19.addChild(bone20);
		setRotationAngle(bone20, 1.0472F, 0.0F, 0.0F);
		bone20.cubeList.add(new ModelBox(bone20, 0, 38, -0.8452F, 0.9063F, -5.5698F, 2, 1, 4, 0.0F, false));

		bone21 = new ModelRenderer(this);
		bone21.setRotationPoint(0.0F, 3.0F, 1.0F);
		bone19.addChild(bone21);
		bone21.cubeList.add(new ModelBox(bone21, 36, 30, -0.8452F, -1.1874F, -1.0F, 2, 1, 4, 0.0F, false));

		bone22 = new ModelRenderer(this);
		bone22.setRotationPoint(0.0F, 0.0F, 4.0F);
		bone19.addChild(bone22);
		setRotationAngle(bone22, -1.0472F, 0.0F, 0.0F);
		bone22.cubeList.add(new ModelBox(bone22, 36, 24, -0.8452F, 0.9063F, 1.5698F, 2, 1, 4, 0.0F, false));

		bone23 = new ModelRenderer(this);
		bone23.setRotationPoint(0.0F, 3.5F, 6.0F);
		bone19.addChild(bone23);
		setRotationAngle(bone23, -2.0071F, 0.0F, 0.0F);
		bone23.cubeList.add(new ModelBox(bone23, 12, 29, -0.8452F, -0.766F, 1.6428F, 3, 1, 4, 0.0F, false));

		shoulderSpike = new ModelRenderer(this);
		shoulderSpike.setRotationPoint(0.5F, -0.25F, 2.0F);
		bone19.addChild(shoulderSpike);
		setRotationAngle(shoulderSpike, 0.0F, -1.0472F, 0.0F);
		shoulderSpike.cubeList
				.add(new ModelBox(shoulderSpike, 36, 19, -0.9226F, 1.0626F, 0.232F, 1, 1, 1, 0.0F, false));

		bone37 = new ModelRenderer(this);
		bone37.setRotationPoint(0.0F, -0.75F, 0.0F);
		shoulderSpike.addChild(bone37);
		setRotationAngle(bone37, 0.1309F, 0.0F, 0.0F);
		bone37.cubeList.add(new ModelBox(bone37, 36, 19, -0.9226F, 1.1427F, -0.0109F, 1, 1, 1, -0.1F, false));

		bone38 = new ModelRenderer(this);
		bone38.setRotationPoint(0.0F, -0.65F, 0.0F);
		bone37.addChild(bone38);
		setRotationAngle(bone38, 0.1309F, 0.0F, 0.0F);
		bone38.cubeList.add(new ModelBox(bone38, 36, 19, -0.9226F, 1.1903F, -0.2621F, 1, 1, 1, -0.2F, false));

		bone39 = new ModelRenderer(this);
		bone39.setRotationPoint(0.0F, -0.5F, 0.0F);
		bone38.addChild(bone39);
		setRotationAngle(bone39, 0.1309F, 0.0F, 0.0F);
		bone39.cubeList.add(new ModelBox(bone39, 36, 19, -0.9226F, 1.2048F, -0.5174F, 1, 1, 1, -0.3F, false));

		bone40 = new ModelRenderer(this);
		bone40.setRotationPoint(0.0F, -0.25F, 0.0F);
		bone39.addChild(bone40);
		setRotationAngle(bone40, 0.1309F, 0.0F, 0.0F);
		bone40.cubeList.add(new ModelBox(bone40, 36, 19, -0.9226F, 1.1858F, -0.7724F, 1, 1, 1, -0.35F, false));

		bone24 = new ModelRenderer(this);
		bone24.setRotationPoint(-1.0F, -1.0F, 0.0F);
		rightShoulder.addChild(bone24);

		bone25 = new ModelRenderer(this);
		bone25.setRotationPoint(0.0F, 0.0F, -2.0F);
		bone24.addChild(bone25);
		setRotationAngle(bone25, 1.0472F, 0.0F, -0.5236F);
		bone25.cubeList.add(new ModelBox(bone25, 36, 19, -3.6383F, 0.5736F, -4.9935F, 2, 1, 4, 0.0F, false));

		bone26 = new ModelRenderer(this);
		bone26.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone24.addChild(bone26);
		setRotationAngle(bone26, -1.0472F, 0.0F, -0.5236F);
		bone26.cubeList.add(new ModelBox(bone26, 36, 14, -3.6383F, 0.5736F, 0.9935F, 2, 1, 4, 0.0F, false));

		bone27 = new ModelRenderer(this);
		bone27.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone24.addChild(bone27);
		setRotationAngle(bone27, 0.0F, 0.0F, -0.5236F);
		bone27.cubeList.add(new ModelBox(bone27, 32, 35, -3.6383F, 1.1472F, -2.0F, 2, 1, 4, 0.0F, false));

		leftShoulder = new ModelRenderer(this);
		leftShoulder.setRotationPoint(3.0F, 0.75F, 0.0F);
		bipedBody.addChild(leftShoulder);
		setRotationAngle(leftShoulder, 0.0F, 0.0F, 0.4363F);

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(1.0F, -1.0F, -2.0F);
		leftShoulder.addChild(bone12);

		bone18 = new ModelRenderer(this);
		bone18.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone12.addChild(bone18);
		setRotationAngle(bone18, 1.0472F, 0.0F, 0.0F);
		bone18.cubeList.add(new ModelBox(bone18, 0, 38, -1.1548F, 0.9063F, -5.5698F, 2, 1, 4, 0.0F, true));

		bone28 = new ModelRenderer(this);
		bone28.setRotationPoint(0.0F, 3.0F, 1.0F);
		bone12.addChild(bone28);
		bone28.cubeList.add(new ModelBox(bone28, 36, 30, -1.1548F, -1.1874F, -1.0F, 2, 1, 4, 0.0F, true));

		bone29 = new ModelRenderer(this);
		bone29.setRotationPoint(0.0F, 0.0F, 4.0F);
		bone12.addChild(bone29);
		setRotationAngle(bone29, -1.0472F, 0.0F, 0.0F);
		bone29.cubeList.add(new ModelBox(bone29, 36, 24, -1.1548F, 0.9063F, 1.5698F, 2, 1, 4, 0.0F, true));

		bone30 = new ModelRenderer(this);
		bone30.setRotationPoint(0.0F, 3.5F, 6.0F);
		bone12.addChild(bone30);
		setRotationAngle(bone30, -2.0071F, 0.0F, 0.0F);
		bone30.cubeList.add(new ModelBox(bone30, 12, 29, -2.1548F, -0.766F, 1.6428F, 3, 1, 4, 0.0F, true));

		shoulderSpike2 = new ModelRenderer(this);
		shoulderSpike2.setRotationPoint(-0.5F, -0.25F, 2.0F);
		bone12.addChild(shoulderSpike2);
		setRotationAngle(shoulderSpike2, 0.0F, 1.0472F, 0.0F);
		shoulderSpike2.cubeList
				.add(new ModelBox(shoulderSpike2, 36, 19, -0.0774F, 1.0626F, 0.232F, 1, 1, 1, 0.0F, true));

		bone36 = new ModelRenderer(this);
		bone36.setRotationPoint(0.0F, -0.75F, 0.0F);
		shoulderSpike2.addChild(bone36);
		setRotationAngle(bone36, 0.1309F, 0.0F, 0.0F);
		bone36.cubeList.add(new ModelBox(bone36, 36, 19, -0.0774F, 1.1427F, -0.0109F, 1, 1, 1, -0.1F, true));

		bone41 = new ModelRenderer(this);
		bone41.setRotationPoint(0.0F, -0.65F, 0.0F);
		bone36.addChild(bone41);
		setRotationAngle(bone41, 0.1309F, 0.0F, 0.0F);
		bone41.cubeList.add(new ModelBox(bone41, 36, 19, -0.0774F, 1.1903F, -0.2621F, 1, 1, 1, -0.2F, true));

		bone42 = new ModelRenderer(this);
		bone42.setRotationPoint(0.0F, -0.5F, 0.0F);
		bone41.addChild(bone42);
		setRotationAngle(bone42, 0.1309F, 0.0F, 0.0F);
		bone42.cubeList.add(new ModelBox(bone42, 36, 19, -0.0774F, 1.2048F, -0.5174F, 1, 1, 1, -0.3F, true));

		bone43 = new ModelRenderer(this);
		bone43.setRotationPoint(0.0F, -0.25F, 0.0F);
		bone42.addChild(bone43);
		setRotationAngle(bone43, 0.1309F, 0.0F, 0.0F);
		bone43.cubeList.add(new ModelBox(bone43, 36, 19, -0.0774F, 1.1858F, -0.7724F, 1, 1, 1, -0.35F, true));

		bone44 = new ModelRenderer(this);
		bone44.setRotationPoint(1.0F, -1.0F, 0.0F);
		leftShoulder.addChild(bone44);

		bone45 = new ModelRenderer(this);
		bone45.setRotationPoint(0.0F, 0.0F, -2.0F);
		bone44.addChild(bone45);
		setRotationAngle(bone45, 1.0472F, 0.0F, 0.5236F);
		bone45.cubeList.add(new ModelBox(bone45, 36, 19, 1.6383F, 0.5736F, -4.9935F, 2, 1, 4, 0.0F, true));

		bone46 = new ModelRenderer(this);
		bone46.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone44.addChild(bone46);
		setRotationAngle(bone46, -1.0472F, 0.0F, 0.5236F);
		bone46.cubeList.add(new ModelBox(bone46, 36, 14, 1.6383F, 0.5736F, 0.9935F, 2, 1, 4, 0.0F, true));

		bone47 = new ModelRenderer(this);
		bone47.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone44.addChild(bone47);
		setRotationAngle(bone47, 0.0F, 0.0F, 0.5236F);
		bone47.cubeList.add(new ModelBox(bone47, 32, 35, 1.6383F, 1.1472F, -2.0F, 2, 1, 4, 0.0F, true));

		Chest = new ModelRenderer(this);
		Chest.setRotationPoint(0.0F, 18.4F, 0.0F);
		bipedBody.addChild(Chest);
		Chest.cubeList.add(new ModelBox(Chest, 12, 0, -2.5F, -16.0F, -2.0F, 5, 3, 4, 0.0F, false));
		Chest.cubeList.add(new ModelBox(Chest, 26, 29, -2.0F, -13.0F, -1.5F, 4, 2, 3, 0.0F, false));
		Chest.cubeList.add(new ModelBox(Chest, 6, 47, -1.0F, -11.0F, -1.0F, 2, 2, 2, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, 2.0F, 0.0F);
		Chest.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, 0.5672F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 14, 47, -6.25F, -12.0F, -1.0F, 1, 2, 2, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.0F, 2.0F, 0.0F);
		Chest.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 0.0F, -0.5672F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 14, 47, 5.25F, -12.0F, -1.0F, 1, 2, 2, 0.0F, true));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -16.0F, 4.0F);
		Chest.addChild(bone2);
		setRotationAngle(bone2, -0.5236F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 21, 34, -1.0F, -0.134F, 0.0F, 2, 2, 1, 0.1F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone2.addChild(bone3);
		setRotationAngle(bone3, 0.1745F, 0.0F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 21, 34, -1.0F, 0.0134F, -0.316F, 2, 2, 1, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, -1.75F, 0.0F);
		bone3.addChild(bone4);
		setRotationAngle(bone4, 0.1745F, 0.0F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 21, 34, -1.0F, 0.1036F, -0.6527F, 2, 2, 1, -0.1F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, -1.75F, 0.0F);
		bone4.addChild(bone5);
		setRotationAngle(bone5, 0.1745F, 0.0F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 21, 34, -1.0F, 0.134F, -1.0F, 2, 2, 1, -0.2F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, -1.5F, 0.0F);
		bone5.addChild(bone6);
		setRotationAngle(bone6, 0.1745F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 21, 34, -1.0F, 0.1036F, -1.3473F, 2, 2, 1, -0.3F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.5F, -1.366F, -0.5F);
		bone6.addChild(bone8);
		setRotationAngle(bone8, 0.1745F, 0.0F, 0.7854F);
		bone8.cubeList.add(new ModelBox(bone8, 12, 0, 0.8927F, -1.4387F, -1.0839F, 1, 3, 1, -0.2F, false));
		bone8.cubeList.add(new ModelBox(bone8, 12, 27, 0.8927F, -0.9387F, -1.0839F, 1, 2, 1, -0.1F, false));
		bone8.cubeList.add(new ModelBox(bone8, 24, 16, 0.8927F, -0.4387F, -1.0839F, 1, 1, 1, 0.0F, false));

		bone48 = new ModelRenderer(this);
		bone48.setRotationPoint(-0.5F, -1.366F, -0.5F);
		bone6.addChild(bone48);
		setRotationAngle(bone48, 0.1745F, 0.0F, -0.7854F);
		bone48.cubeList.add(new ModelBox(bone48, 12, 0, -1.8927F, -1.4387F, -1.0839F, 1, 3, 1, -0.2F, true));
		bone48.cubeList.add(new ModelBox(bone48, 12, 27, -1.8927F, -0.9387F, -1.0839F, 1, 2, 1, -0.1F, true));
		bone48.cubeList.add(new ModelBox(bone48, 24, 16, -1.8927F, -0.4387F, -1.0839F, 1, 1, 1, 0.0F, true));

		stomach = new ModelRenderer(this);
		stomach.setRotationPoint(0.0F, 8.55F, 0.0F);
		bipedBody.addChild(stomach);
		stomach.cubeList.add(new ModelBox(stomach, 12, 14, -2.0F, 0.0F, -2.0F, 4, 3, 4, -0.8F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(0.0F, 0.0F, -1.2F);
		stomach.addChild(bone11);
		setRotationAngle(bone11, -0.0873F, 0.0F, 0.0F);
		bone11.cubeList.add(new ModelBox(bone11, 24, 10, -2.0F, 0.9924F, -0.6257F, 4, 2, 4, -0.6F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.0F, 0.8F, 0.2F);
		bone11.addChild(bone10);
		setRotationAngle(bone10, -0.0873F, 0.0F, 0.0F);
		bone10.cubeList.add(new ModelBox(bone10, 24, 17, -2.0F, 0.9696F, -0.6527F, 4, 2, 4, -0.4F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(0.0F, 1.05F, -0.8F);
		bone10.addChild(bone9);
		setRotationAngle(bone9, -0.0873F, 0.0F, 0.0F);
		bone9.cubeList.add(new ModelBox(bone9, 26, 3, -2.0F, 0.9319F, 0.3176F, 4, 2, 4, -0.2F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, 1.4F, -0.2F);
		bone9.addChild(bone7);
		setRotationAngle(bone7, -0.0873F, 0.0F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 0, 27, -2.0F, 0.8794F, 0.684F, 4, 2, 4, 0.0F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(0.0F, 1.45F, 0.1F);
		bone7.addChild(bone13);
		setRotationAngle(bone13, -0.0873F, 0.0F, 0.0F);
		bone13.cubeList.add(new ModelBox(bone13, 20, 23, -2.0F, 0.8126F, 0.7452F, 4, 2, 4, -0.2F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(0.0F, 1.15F, 0.3F);
		bone13.addChild(bone14);
		setRotationAngle(bone14, -0.0873F, 0.0F, 0.0F);
		bone14.cubeList.add(new ModelBox(bone14, 8, 21, -2.0F, 0.7321F, 0.6F, 4, 2, 4, -0.4F, false));

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(0.0F, 0.25F, 0.2F);
		bone14.addChild(bone15);
		setRotationAngle(bone15, -0.0873F, 0.0F, 0.0F);
		bone15.cubeList.add(new ModelBox(bone15, 12, 7, -2.0F, 0.6383F, 0.5472F, 4, 3, 4, -0.6F, false));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(0.0F, 1.8F, 0.3F);
		bone15.addChild(bone16);
		setRotationAngle(bone16, -0.0873F, 0.0F, 0.0F);
		bone16.cubeList.add(new ModelBox(bone16, 0, 33, -1.5F, 0.5321F, 0.7856F, 3, 2, 3, -0.4F, false));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(0.0F, 0.05F, 0.0F);
		bone16.addChild(bone17);
		setRotationAngle(bone17, 0.5236F, 0.0F, 0.0F);
		bone17.cubeList.add(new ModelBox(bone17, 42, 4, -1.0F, 2.4696F, 0.3473F, 2, 3, 2, -0.1F, false));

		tail0 = new ModelRenderer(this);
		tail0.setRotationPoint(0.025F, 4.525F, 1.25F);
		bone17.addChild(tail0);
		setRotationAngle(tail0, -0.5856F, 0.7706F, -1.3628F);
		tail0.cubeList.add(new ModelBox(tail0, 44, 39, -1.0359F, -3.9303F, -0.9398F, 2, 4, 2, 0.0F, false));
		tail0.cubeList.add(new ModelBox(tail0, 36, 40, -0.9859F, -9.5803F, -0.9398F, 2, 6, 2, -0.1F, false));
		tail0.cubeList.add(new ModelBox(tail0, 28, 40, -0.9859F, -14.5803F, -0.9398F, 2, 6, 2, -0.2F, false));
		tail0.cubeList.add(new ModelBox(tail0, 20, 39, -0.9859F, -19.8303F, -0.9398F, 2, 6, 2, -0.4F, false));
		tail0.cubeList.add(new ModelBox(tail0, 12, 39, -0.9859F, -24.8303F, -0.9398F, 2, 6, 2, -0.6F, false));
		tail0.cubeList.add(new ModelBox(tail0, 0, 0, -5.4859F, -24.0803F, 0.0602F, 6, 24, 0, 0.0F, false));

		tail1 = new ModelRenderer(this);
		tail1.setRotationPoint(0.025F, 4.525F, 1.25F);
		bone17.addChild(tail1);
		setRotationAngle(tail1, -0.8853F, 0.8037F, -1.3757F);
		tail1.cubeList.add(new ModelBox(tail1, 44, 39, -1.0359F, -3.9303F, -0.9398F, 2, 4, 2, 0.0F, false));
		tail1.cubeList.add(new ModelBox(tail1, 36, 40, -0.9859F, -9.5803F, -0.9398F, 2, 6, 2, -0.1F, false));
		tail1.cubeList.add(new ModelBox(tail1, 28, 40, -0.9859F, -14.5803F, -0.9398F, 2, 6, 2, -0.2F, false));
		tail1.cubeList.add(new ModelBox(tail1, 20, 39, -0.9859F, -19.8303F, -0.9398F, 2, 6, 2, -0.4F, false));
		tail1.cubeList.add(new ModelBox(tail1, 12, 39, -0.9859F, -24.8303F, -0.9398F, 2, 6, 2, -0.6F, false));
		tail1.cubeList.add(new ModelBox(tail1, 0, 0, -5.4859F, -24.0803F, 0.0602F, 6, 24, 0, 0.0F, false));

		tail2 = new ModelRenderer(this);
		tail2.setRotationPoint(0.025F, 4.525F, 1.25F);
		bone17.addChild(tail2);
		setRotationAngle(tail2, -1.1803F, 0.8462F, -1.3792F);
		tail2.cubeList.add(new ModelBox(tail2, 44, 39, -1.0359F, -3.9303F, -0.9398F, 2, 4, 2, 0.0F, false));
		tail2.cubeList.add(new ModelBox(tail2, 36, 40, -0.9859F, -9.5803F, -0.9398F, 2, 6, 2, -0.1F, false));
		tail2.cubeList.add(new ModelBox(tail2, 28, 40, -0.9859F, -14.5803F, -0.9398F, 2, 6, 2, -0.2F, false));
		tail2.cubeList.add(new ModelBox(tail2, 20, 39, -0.9859F, -19.8303F, -0.9398F, 2, 6, 2, -0.4F, false));
		tail2.cubeList.add(new ModelBox(tail2, 12, 39, -0.9859F, -24.8303F, -0.9398F, 2, 6, 2, -0.6F, false));
		tail2.cubeList.add(new ModelBox(tail2, 0, 0, -5.4859F, -24.0803F, 0.0602F, 6, 24, 0, 0.0F, false));

		tail3 = new ModelRenderer(this);
		tail3.setRotationPoint(-0.025F, 4.525F, 1.25F);
		bone17.addChild(tail3);
		setRotationAngle(tail3, -1.1803F, -0.8462F, 1.3792F);
		tail3.cubeList.add(new ModelBox(tail3, 44, 39, -0.9641F, -3.9303F, -0.9398F, 2, 4, 2, 0.0F, true));
		tail3.cubeList.add(new ModelBox(tail3, 36, 40, -1.0141F, -9.5803F, -0.9398F, 2, 6, 2, -0.1F, true));
		tail3.cubeList.add(new ModelBox(tail3, 28, 40, -1.0141F, -14.5803F, -0.9398F, 2, 6, 2, -0.2F, true));
		tail3.cubeList.add(new ModelBox(tail3, 20, 39, -1.0141F, -19.8303F, -0.9398F, 2, 6, 2, -0.4F, true));
		tail3.cubeList.add(new ModelBox(tail3, 12, 39, -1.0141F, -24.8303F, -0.9398F, 2, 6, 2, -0.6F, true));
		tail3.cubeList.add(new ModelBox(tail3, 0, 0, -0.5141F, -24.0803F, 0.0602F, 6, 24, 0, 0.0F, true));

		tail4 = new ModelRenderer(this);
		tail4.setRotationPoint(-0.025F, 4.525F, 1.25F);
		bone17.addChild(tail4);
		setRotationAngle(tail4, -0.8853F, -0.8037F, 1.3757F);
		tail4.cubeList.add(new ModelBox(tail4, 44, 39, -0.9641F, -3.9303F, -0.9398F, 2, 4, 2, 0.0F, true));
		tail4.cubeList.add(new ModelBox(tail4, 36, 40, -1.0141F, -9.5803F, -0.9398F, 2, 6, 2, -0.1F, true));
		tail4.cubeList.add(new ModelBox(tail4, 28, 40, -1.0141F, -14.5803F, -0.9398F, 2, 6, 2, -0.2F, true));
		tail4.cubeList.add(new ModelBox(tail4, 20, 39, -1.0141F, -19.8303F, -0.9398F, 2, 6, 2, -0.4F, true));
		tail4.cubeList.add(new ModelBox(tail4, 12, 39, -1.0141F, -24.8303F, -0.9398F, 2, 6, 2, -0.6F, true));
		tail4.cubeList.add(new ModelBox(tail4, 0, 0, -0.5141F, -24.0803F, 0.0602F, 6, 24, 0, 0.0F, true));

		tail5 = new ModelRenderer(this);
		tail5.setRotationPoint(-0.025F, 4.525F, 1.25F);
		bone17.addChild(tail5);
		setRotationAngle(tail5, -0.5856F, -0.7706F, 1.3628F);
		tail5.cubeList.add(new ModelBox(tail5, 44, 39, -0.9641F, -3.9303F, -0.9398F, 2, 4, 2, 0.0F, true));
		tail5.cubeList.add(new ModelBox(tail5, 36, 40, -1.0141F, -9.5803F, -0.9398F, 2, 6, 2, -0.1F, true));
		tail5.cubeList.add(new ModelBox(tail5, 28, 40, -1.0141F, -14.5803F, -0.9398F, 2, 6, 2, -0.2F, true));
		tail5.cubeList.add(new ModelBox(tail5, 20, 39, -1.0141F, -19.8303F, -0.9398F, 2, 6, 2, -0.4F, true));
		tail5.cubeList.add(new ModelBox(tail5, 12, 39, -1.0141F, -24.8303F, -0.9398F, 2, 6, 2, -0.6F, true));
		tail5.cubeList.add(new ModelBox(tail5, 0, 0, -0.5141F, -24.0803F, 0.0602F, 6, 24, 0, 0.0F, true));

		tail6_0 = new ModelRenderer(this);
		tail6_0.setRotationPoint(0.0F, 3.25F, 1.0F);
		bone17.addChild(tail6_0);
		setRotationAngle(tail6_0, 1.309F, 0.0F, 0.0F);
		tail6_0.cubeList.add(new ModelBox(tail6_0, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, 0.1F, false));

		tail6_1 = new ModelRenderer(this);
		tail6_1.setRotationPoint(0.0F, 3.0F, 0.0F);
		tail6_0.addChild(tail6_1);
		setRotationAngle(tail6_1, 0.1745F, 0.0F, 0.0F);
		tail6_1.cubeList.add(new ModelBox(tail6_1, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, 0.0F, false));

		tail6_2 = new ModelRenderer(this);
		tail6_2.setRotationPoint(0.0F, 3.0F, 0.0F);
		tail6_1.addChild(tail6_2);
		setRotationAngle(tail6_2, 0.1745F, 0.0F, 0.0F);
		tail6_2.cubeList.add(new ModelBox(tail6_2, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, 0.0F, false));

		tail6_3 = new ModelRenderer(this);
		tail6_3.setRotationPoint(0.0F, 3.0F, 0.0F);
		tail6_2.addChild(tail6_3);
		setRotationAngle(tail6_3, 0.1745F, 0.0F, 0.0F);
		tail6_3.cubeList.add(new ModelBox(tail6_3, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, 0.0F, false));

		tail6_4 = new ModelRenderer(this);
		tail6_4.setRotationPoint(0.0F, 3.0F, 0.0F);
		tail6_3.addChild(tail6_4);
		setRotationAngle(tail6_4, 0.1745F, 0.0F, 0.0F);
		tail6_4.cubeList.add(new ModelBox(tail6_4, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, 0.0F, false));

		tail6_5 = new ModelRenderer(this);
		tail6_5.setRotationPoint(0.0F, 3.0F, 0.0F);
		tail6_4.addChild(tail6_5);
		setRotationAngle(tail6_5, 0.1745F, 0.0F, 0.0F);
		tail6_5.cubeList.add(new ModelBox(tail6_5, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, -0.05F, false));

		tail6_6 = new ModelRenderer(this);
		tail6_6.setRotationPoint(0.0F, 3.0F, 0.0F);
		tail6_5.addChild(tail6_6);
		setRotationAngle(tail6_6, 0.1745F, 0.0F, 0.0F);
		tail6_6.cubeList.add(new ModelBox(tail6_6, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, -0.1F, false));

		tail6_7 = new ModelRenderer(this);
		tail6_7.setRotationPoint(0.0F, 3.0F, 0.0F);
		tail6_6.addChild(tail6_7);
		setRotationAngle(tail6_7, 0.1745F, 0.0F, 0.0F);
		tail6_7.cubeList.add(new ModelBox(tail6_7, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, -0.15F, false));

		tail6_8 = new ModelRenderer(this);
		tail6_8.setRotationPoint(0.0F, 3.0F, 0.0F);
		tail6_7.addChild(tail6_8);
		setRotationAngle(tail6_8, 0.1745F, 0.0F, 0.0F);
		tail6_8.cubeList.add(new ModelBox(tail6_8, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, -0.2F, false));

		tail6_9 = new ModelRenderer(this);
		tail6_9.setRotationPoint(0.0F, 3.0F, 0.0F);
		tail6_8.addChild(tail6_9);
		setRotationAngle(tail6_9, 0.1745F, 0.0F, 0.0F);
		tail6_9.cubeList.add(new ModelBox(tail6_9, 0, 43, -1.0F, 0.3452F, -2.8126F, 2, 4, 2, -0.25F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-2.25F, 3.85F, 0.75F);

		arm1 = new ModelRenderer(this);
		arm1.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(arm1);
		setRotationAngle(arm1, -0.411F, 0.6635F, 0.6582F);

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.1F, 3.3F, 2.5F);
		arm1.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 0.0F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 27, -0.6F, -3.4F, -3.0F, 1, 3, 1, 0.0F, false));

		bone52 = new ModelRenderer(this);
		bone52.setRotationPoint(0.0F, 2.6F, 0.1F);
		arm1.addChild(bone52);
		setRotationAngle(bone52, -1.0472F, 0.0F, 0.0F);
		bone52.cubeList.add(new ModelBox(bone52, 20, 21, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.1F, false));

		bone53 = new ModelRenderer(this);
		bone53.setRotationPoint(0.0F, 2.5F, -0.1F);
		bone52.addChild(bone53);
		setRotationAngle(bone53, 0.8462F, 0.1719F, -0.3053F);
		bone53.cubeList.add(new ModelBox(bone53, 12, 14, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.15F, false));

		bone54 = new ModelRenderer(this);
		bone54.setRotationPoint(0.0F, 2.4F, 0.0F);
		bone53.addChild(bone54);
		setRotationAngle(bone54, 0.5133F, 0.1084F, -0.1897F);
		bone54.cubeList.add(new ModelBox(bone54, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, false));

		arm2 = new ModelRenderer(this);
		arm2.setRotationPoint(0.0F, 1.0F, -0.75F);
		bipedRightArm.addChild(arm2);
		setRotationAngle(arm2, -0.4977F, 0.6059F, 0.5121F);

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.1F, 3.3F, 2.5F);
		arm2.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.0F, 0.0F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 27, -0.6F, -3.4F, -3.0F, 1, 3, 1, 0.0F, false));

		bone49 = new ModelRenderer(this);
		bone49.setRotationPoint(0.0F, 2.6F, 0.1F);
		arm2.addChild(bone49);
		setRotationAngle(bone49, -1.0472F, 0.0F, 0.0F);
		bone49.cubeList.add(new ModelBox(bone49, 20, 21, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.1F, false));

		bone50 = new ModelRenderer(this);
		bone50.setRotationPoint(0.0F, 2.5F, -0.1F);
		bone49.addChild(bone50);
		setRotationAngle(bone50, 0.8462F, 0.1719F, -0.3053F);
		bone50.cubeList.add(new ModelBox(bone50, 12, 14, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.15F, false));

		bone51 = new ModelRenderer(this);
		bone51.setRotationPoint(0.0F, 2.4F, 0.0F);
		bone50.addChild(bone51);
		setRotationAngle(bone51, 0.9496F, 0.1084F, -0.1897F);
		bone51.cubeList.add(new ModelBox(bone51, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, false));

		arm3 = new ModelRenderer(this);
		arm3.setRotationPoint(0.25F, 2.0F, -1.75F);
		bipedRightArm.addChild(arm3);
		setRotationAngle(arm3, -0.5477F, 0.5623F, 0.4215F);

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.1F, 3.3F, 2.5F);
		arm3.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, 0.0F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 27, -0.6F, -3.4F, -3.0F, 1, 3, 1, 0.0F, false));

		bone55 = new ModelRenderer(this);
		bone55.setRotationPoint(0.0F, 2.6F, 0.1F);
		arm3.addChild(bone55);
		setRotationAngle(bone55, -1.0472F, 0.0F, 0.0F);
		bone55.cubeList.add(new ModelBox(bone55, 20, 21, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.1F, false));

		bone56 = new ModelRenderer(this);
		bone56.setRotationPoint(0.0F, 2.5F, -0.1F);
		bone55.addChild(bone56);
		setRotationAngle(bone56, 0.8462F, 0.1719F, -0.3053F);
		bone56.cubeList.add(new ModelBox(bone56, 12, 14, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.15F, false));

		bone57 = new ModelRenderer(this);
		bone57.setRotationPoint(0.0F, 2.4F, 0.0F);
		bone56.addChild(bone57);
		setRotationAngle(bone57, 1.1241F, 0.1084F, -0.1897F);
		bone57.cubeList.add(new ModelBox(bone57, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(2.25F, 3.85F, 0.75F);

		arm4 = new ModelRenderer(this);
		arm4.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(arm4);
		setRotationAngle(arm4, -0.411F, -0.6635F, -0.6582F);

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(-0.1F, 3.3F, 2.5F);
		arm4.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 27, -0.4F, -3.4F, -3.0F, 1, 3, 1, 0.0F, true));

		bone58 = new ModelRenderer(this);
		bone58.setRotationPoint(0.0F, 2.6F, 0.1F);
		arm4.addChild(bone58);
		setRotationAngle(bone58, -1.0472F, 0.0F, 0.0F);
		bone58.cubeList.add(new ModelBox(bone58, 20, 21, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.1F, true));

		bone59 = new ModelRenderer(this);
		bone59.setRotationPoint(0.0F, 2.5F, -0.1F);
		bone58.addChild(bone59);
		setRotationAngle(bone59, 0.8462F, -0.1719F, 0.3053F);
		bone59.cubeList.add(new ModelBox(bone59, 12, 14, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.15F, true));

		bone60 = new ModelRenderer(this);
		bone60.setRotationPoint(0.0F, 2.4F, 0.0F);
		bone59.addChild(bone60);
		setRotationAngle(bone60, 0.5133F, -0.1084F, 0.1897F);
		bone60.cubeList.add(new ModelBox(bone60, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, true));

		arm5 = new ModelRenderer(this);
		arm5.setRotationPoint(0.0F, 1.0F, -0.75F);
		bipedLeftArm.addChild(arm5);
		setRotationAngle(arm5, -0.4977F, -0.6059F, -0.5121F);

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(-0.1F, 3.3F, 2.5F);
		arm5.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.0F, 0.0F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 27, -0.4F, -3.4F, -3.0F, 1, 3, 1, 0.0F, true));

		bone61 = new ModelRenderer(this);
		bone61.setRotationPoint(0.0F, 2.6F, 0.1F);
		arm5.addChild(bone61);
		setRotationAngle(bone61, -1.0472F, 0.0F, 0.0F);
		bone61.cubeList.add(new ModelBox(bone61, 20, 21, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.1F, true));

		bone62 = new ModelRenderer(this);
		bone62.setRotationPoint(0.0F, 2.5F, -0.1F);
		bone61.addChild(bone62);
		setRotationAngle(bone62, 0.8462F, -0.1719F, 0.3053F);
		bone62.cubeList.add(new ModelBox(bone62, 12, 14, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.15F, true));

		bone63 = new ModelRenderer(this);
		bone63.setRotationPoint(0.0F, 2.4F, 0.0F);
		bone62.addChild(bone63);
		setRotationAngle(bone63, 0.9496F, -0.1084F, 0.1897F);
		bone63.cubeList.add(new ModelBox(bone63, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, true));

		arm6 = new ModelRenderer(this);
		arm6.setRotationPoint(-0.25F, 2.0F, -1.75F);
		bipedLeftArm.addChild(arm6);
		setRotationAngle(arm6, -0.5477F, -0.5623F, -0.4215F);

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-0.1F, 3.3F, 2.5F);
		arm6.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, 0.0F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 27, -0.4F, -3.4F, -3.0F, 1, 3, 1, 0.0F, true));

		bone64 = new ModelRenderer(this);
		bone64.setRotationPoint(0.0F, 2.6F, 0.1F);
		arm6.addChild(bone64);
		setRotationAngle(bone64, -1.0472F, 0.0F, 0.0F);
		bone64.cubeList.add(new ModelBox(bone64, 20, 21, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.1F, true));

		bone65 = new ModelRenderer(this);
		bone65.setRotationPoint(0.0F, 2.5F, -0.1F);
		bone64.addChild(bone65);
		setRotationAngle(bone65, 0.8462F, -0.1719F, 0.3053F);
		bone65.cubeList.add(new ModelBox(bone65, 12, 14, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.15F, true));

		bone66 = new ModelRenderer(this);
		bone66.setRotationPoint(0.0F, 2.4F, 0.0F);
		bone65.addChild(bone66);
		setRotationAngle(bone66, 1.1241F, -0.1084F, 0.1897F);
		bone66.cubeList.add(new ModelBox(bone66, 12, 7, -0.5F, -0.15F, -0.5F, 1, 3, 1, -0.25F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
		bipedHeadwear.render(f5);
		bipedBody.render(f5);
		bipedRightArm.render(f5);
		bipedLeftArm.render(f5);
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