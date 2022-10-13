// Made with Blockbench 4.4.2
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelPuppetHiruko extends ModelBase {
	private final ModelRenderer body;
	private final ModelRenderer bipedHead;
	private final ModelRenderer jaw;
	private final ModelRenderer jawMid;
	private final ModelRenderer mask;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer hair;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer bone18;
	private final ModelRenderer bone19;
	private final ModelRenderer bone33;
	private final ModelRenderer bone34;
	private final ModelRenderer bone35;
	private final ModelRenderer bone36;
	private final ModelRenderer bone29;
	private final ModelRenderer bone30;
	private final ModelRenderer bone31;
	private final ModelRenderer bone32;
	private final ModelRenderer bone25;
	private final ModelRenderer bone26;
	private final ModelRenderer bone27;
	private final ModelRenderer bone28;
	private final ModelRenderer bone20;
	private final ModelRenderer bone22;
	private final ModelRenderer bone23;
	private final ModelRenderer bone24;
	private final ModelRenderer bone8;
	private final ModelRenderer bone10;
	private final ModelRenderer bone11;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer bipedBody;
	private final ModelRenderer robe;
	private final ModelRenderer bone21;
	private final ModelRenderer rightArm;
	private final ModelRenderer rightForeArm;
	private final ModelRenderer leftArm;
	private final ModelRenderer leftForeArm;
	private final ModelRenderer backShield;
	private final ModelRenderer bone4;
	private final ModelRenderer bone3;
	private final ModelRenderer bone6;
	private final ModelRenderer bone9;
	private final ModelRenderer bone7;
	private final ModelRenderer bone5;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedRightUpperArm;
	private final ModelRenderer bipedRightForeArm;
	private final ModelRenderer bipedRightUpperArm2;
	private final ModelRenderer bipedRightForeArm2;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer bipedLeftUpperArm;
	private final ModelRenderer bipedLeftForeArm;
	private final ModelRenderer bipedLeftUpperArm2;
	private final ModelRenderer bipedLeftForeArm2;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer rightThigh;
	private final ModelRenderer calfRight;
	private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer leftThigh;
	private final ModelRenderer calfLeft;
	private final ModelRenderer tail;
	private final ModelRenderer tail1;
	private final ModelRenderer tail2;
	private final ModelRenderer tail3;
	private final ModelRenderer tail4;
	private final ModelRenderer tail5;
	private final ModelRenderer tail6;
	private final ModelRenderer tail7;
	private final ModelRenderer tail8;
	private final ModelRenderer tail9;
	private final ModelRenderer tail10;
	private final ModelRenderer tail11;
	private final ModelRenderer tail12;
	private final ModelRenderer tail13;
	private final ModelRenderer tail14;
	private final ModelRenderer tail15;
	private final ModelRenderer tail16;
	private final ModelRenderer tailEnd;
	private final ModelRenderer bone;
	private final ModelRenderer bone14;
	private final ModelRenderer bone2;
	private final ModelRenderer bone15;

	public ModelPuppetHiruko() {
		textureWidth = 128;
		textureHeight = 128;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 15.0F, 0.0F);
		setRotationAngle(body, 1.0472F, 0.0F, 0.0F);

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, -12.0F, 0.0F);
		body.addChild(bipedHead);
		setRotationAngle(bipedHead, -1.0472F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 44, 18, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 64, -0.5F, -1.0F, -3.9F, 1, 1, 4, -0.01F, false));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.0F, -2.0F, 0.0F);
		bipedHead.addChild(jaw);
		jaw.cubeList.add(new ModelBox(jaw, 0, 74, 1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));
		jaw.cubeList.add(new ModelBox(jaw, 0, 74, -3.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, true));

		jawMid = new ModelRenderer(this);
		jawMid.setRotationPoint(0.0F, 0.0F, 0.0F);
		jaw.addChild(jawMid);
		jawMid.cubeList.add(new ModelBox(jawMid, 12, 74, -1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));

		mask = new ModelRenderer(this);
		mask.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedHead.addChild(mask);
		mask.cubeList.add(new ModelBox(mask, 68, 10, -4.0F, -27.0F, -4.0F, 8, 4, 8, 0.25F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, -12.0F, 0.0F);
		body.addChild(bipedHeadwear);
		setRotationAngle(bipedHeadwear, -1.0472F, 0.0F, 0.0F);

		hair = new ModelRenderer(this);
		hair.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHeadwear.addChild(hair);

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(0.0F, -6.0F, 3.75F);
		hair.addChild(bone16);
		setRotationAngle(bone16, 1.0472F, 0.0F, 0.0F);
		bone16.cubeList.add(new ModelBox(bone16, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone16.addChild(bone17);
		setRotationAngle(bone17, 0.5236F, 0.0F, 0.0F);
		bone17.cubeList.add(new ModelBox(bone17, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone18 = new ModelRenderer(this);
		bone18.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone17.addChild(bone18);
		setRotationAngle(bone18, 0.5236F, 0.0F, 0.0F);
		bone18.cubeList.add(new ModelBox(bone18, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone19 = new ModelRenderer(this);
		bone19.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone18.addChild(bone19);
		setRotationAngle(bone19, 0.2618F, 0.0F, 0.0F);
		bone19.cubeList.add(new ModelBox(bone19, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone33 = new ModelRenderer(this);
		bone33.setRotationPoint(-1.5F, -5.0F, 3.75F);
		hair.addChild(bone33);
		setRotationAngle(bone33, 1.0472F, 0.0F, -0.7854F);
		bone33.cubeList.add(new ModelBox(bone33, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone34 = new ModelRenderer(this);
		bone34.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone33.addChild(bone34);
		setRotationAngle(bone34, 0.5236F, 0.0F, 0.0F);
		bone34.cubeList.add(new ModelBox(bone34, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone35 = new ModelRenderer(this);
		bone35.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone34.addChild(bone35);
		setRotationAngle(bone35, 0.5236F, 0.0F, 0.0F);
		bone35.cubeList.add(new ModelBox(bone35, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone36 = new ModelRenderer(this);
		bone36.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone35.addChild(bone36);
		setRotationAngle(bone36, 0.2618F, 0.0F, 0.0F);
		bone36.cubeList.add(new ModelBox(bone36, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone29 = new ModelRenderer(this);
		bone29.setRotationPoint(1.5F, -5.0F, 3.75F);
		hair.addChild(bone29);
		setRotationAngle(bone29, 1.0472F, 0.0F, 0.7854F);
		bone29.cubeList.add(new ModelBox(bone29, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone30 = new ModelRenderer(this);
		bone30.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone29.addChild(bone30);
		setRotationAngle(bone30, 0.5236F, 0.0F, 0.0F);
		bone30.cubeList.add(new ModelBox(bone30, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone31 = new ModelRenderer(this);
		bone31.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone30.addChild(bone31);
		setRotationAngle(bone31, 0.5236F, 0.0F, 0.0F);
		bone31.cubeList.add(new ModelBox(bone31, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone32 = new ModelRenderer(this);
		bone32.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone31.addChild(bone32);
		setRotationAngle(bone32, 0.2618F, 0.0F, 0.0F);
		bone32.cubeList.add(new ModelBox(bone32, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone25 = new ModelRenderer(this);
		bone25.setRotationPoint(-0.75F, -5.75F, 3.75F);
		hair.addChild(bone25);
		setRotationAngle(bone25, 1.0472F, 0.0F, -0.4363F);
		bone25.cubeList.add(new ModelBox(bone25, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone26 = new ModelRenderer(this);
		bone26.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone25.addChild(bone26);
		setRotationAngle(bone26, 0.5236F, 0.0F, 0.0F);
		bone26.cubeList.add(new ModelBox(bone26, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone27 = new ModelRenderer(this);
		bone27.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone26.addChild(bone27);
		setRotationAngle(bone27, 0.5236F, 0.0F, 0.0F);
		bone27.cubeList.add(new ModelBox(bone27, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone28 = new ModelRenderer(this);
		bone28.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone27.addChild(bone28);
		setRotationAngle(bone28, 0.2618F, 0.0F, 0.0F);
		bone28.cubeList.add(new ModelBox(bone28, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone20 = new ModelRenderer(this);
		bone20.setRotationPoint(0.75F, -5.75F, 3.75F);
		hair.addChild(bone20);
		setRotationAngle(bone20, 1.0472F, 0.0F, 0.4363F);
		bone20.cubeList.add(new ModelBox(bone20, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone22 = new ModelRenderer(this);
		bone22.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone20.addChild(bone22);
		setRotationAngle(bone22, 0.5236F, 0.0F, 0.0F);
		bone22.cubeList.add(new ModelBox(bone22, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone23 = new ModelRenderer(this);
		bone23.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone22.addChild(bone23);
		setRotationAngle(bone23, 0.5236F, 0.0F, 0.0F);
		bone23.cubeList.add(new ModelBox(bone23, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone24 = new ModelRenderer(this);
		bone24.setRotationPoint(0.0F, 0.0F, 2.0F);
		bone23.addChild(bone24);
		setRotationAngle(bone24, 0.2618F, 0.0F, 0.0F);
		bone24.cubeList.add(new ModelBox(bone24, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, -8.5F, -4.25F);
		hair.addChild(bone8);
		bone8.cubeList.add(new ModelBox(bone8, 68, 0, -0.5F, 0.25F, 0.25F, 1, 2, 8, 0.05F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(2.5F, -8.5F, -4.25F);
		hair.addChild(bone10);
		setRotationAngle(bone10, 0.0F, 0.0F, 0.2618F);
		bone10.cubeList.add(new ModelBox(bone10, 68, 0, -0.5F, 0.4F, 0.25F, 1, 2, 8, 0.05F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(-2.5F, -8.5F, -4.25F);
		hair.addChild(bone11);
		setRotationAngle(bone11, 0.0F, 0.0F, -0.2618F);
		bone11.cubeList.add(new ModelBox(bone11, 68, 0, -0.5F, 0.4F, 0.25F, 1, 2, 8, 0.05F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(-4.5F, -6.5F, -4.25F);
		hair.addChild(bone12);
		setRotationAngle(bone12, 0.0F, 0.0F, -0.7854F);
		bone12.cubeList.add(new ModelBox(bone12, 68, 0, -0.25F, 0.0F, 0.25F, 1, 2, 8, 0.05F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(4.5F, -6.5F, -4.25F);
		hair.addChild(bone13);
		setRotationAngle(bone13, 0.0F, 0.0F, 0.7854F);
		bone13.cubeList.add(new ModelBox(bone13, 68, 0, -0.75F, 0.0F, 0.25F, 1, 2, 8, 0.05F, true));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, -12.0F, 0.0F);
		body.addChild(bipedBody);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 48, 34, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));

		robe = new ModelRenderer(this);
		robe.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(robe);
		robe.cubeList.add(new ModelBox(robe, 0, 0, -7.0F, 0.0F, -6.0F, 14, 14, 12, 0.05F, false));

		bone21 = new ModelRenderer(this);
		bone21.setRotationPoint(0.0F, 0.0F, -6.0F);
		robe.addChild(bone21);
		setRotationAngle(bone21, 0.5236F, 0.0F, 0.0F);
		bone21.cubeList.add(new ModelBox(bone21, 0, 26, -7.0F, 0.0F, -10.0F, 14, 18, 10, 0.05F, false));
		bone21.cubeList.add(new ModelBox(bone21, 40, 0, -7.0F, 16.0F, -0.75F, 14, 2, 4, 0.05F, false));

		rightArm = new ModelRenderer(this);
		rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		robe.addChild(rightArm);
		setRotationAngle(rightArm, -1.0472F, 0.0F, 0.0F);
		rightArm.cubeList.add(new ModelBox(rightArm, 0, 64, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.55F, false));

		rightForeArm = new ModelRenderer(this);
		rightForeArm.setRotationPoint(-1.0F, 4.0F, 2.0F);
		rightArm.addChild(rightForeArm);
		setRotationAngle(rightForeArm, -0.2618F, 0.0F, 0.0F);
		rightForeArm.cubeList.add(new ModelBox(rightForeArm, 16, 64, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.55F, false));

		leftArm = new ModelRenderer(this);
		leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		robe.addChild(leftArm);
		setRotationAngle(leftArm, -1.0472F, 0.0F, 0.0F);
		leftArm.cubeList.add(new ModelBox(leftArm, 0, 64, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.55F, true));

		leftForeArm = new ModelRenderer(this);
		leftForeArm.setRotationPoint(1.0F, 4.0F, 2.0F);
		leftArm.addChild(leftForeArm);
		setRotationAngle(leftForeArm, -0.2618F, 0.0F, 0.0F);
		leftForeArm.cubeList.add(new ModelBox(leftForeArm, 16, 64, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.55F, true));

		backShield = new ModelRenderer(this);
		backShield.setRotationPoint(0.0F, 3.0F, 3.0F);
		bipedBody.addChild(backShield);
		setRotationAngle(backShield, 0.0873F, 0.0F, 0.0F);
		backShield.cubeList.add(new ModelBox(backShield, 0, 0, -2.0F, 0.0F, -1.0F, 4, 8, 2, 0.5F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-2.5F, 5.0F, 1.5F);
		backShield.addChild(bone4);
		setRotationAngle(bone4, 0.0F, -0.5236F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 26, -2.5F, -5.0F, -2.5F, 2, 8, 2, 0.5F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(2.5F, 5.0F, 1.5F);
		backShield.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 0.5236F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 26, 0.5F, -5.0F, -2.5F, 2, 8, 2, 0.5F, true));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 8.5F, 1.5F);
		backShield.addChild(bone6);
		setRotationAngle(bone6, -0.5236F, 0.0F, 0.0F);

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone6.addChild(bone9);
		setRotationAngle(bone9, -0.0873F, 0.0F, 0.0F);
		bone9.cubeList.add(new ModelBox(bone9, 40, 6, -2.0F, 0.5F, -2.5F, 4, 4, 2, 0.5F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(-2.5F, 0.25F, 0.0F);
		bone6.addChild(bone7);
		setRotationAngle(bone7, 0.0F, -0.4625F, -0.2618F);
		bone7.cubeList.add(new ModelBox(bone7, 46, 60, -2.45F, 0.25F, -2.52F, 2, 4, 2, 0.5F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(2.5F, 0.25F, 0.0F);
		bone6.addChild(bone5);
		setRotationAngle(bone5, 0.0F, 0.4625F, 0.2618F);
		bone5.cubeList.add(new ModelBox(bone5, 46, 60, 0.45F, 0.25F, -2.52F, 2, 4, 2, 0.5F, true));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, -10.0F, 0.0F);
		body.addChild(bipedRightArm);

		bipedRightUpperArm = new ModelRenderer(this);
		bipedRightUpperArm.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(bipedRightUpperArm);
		setRotationAngle(bipedRightUpperArm, -1.0472F, 0.0F, 0.0F);
		bipedRightUpperArm.cubeList
				.add(new ModelBox(bipedRightUpperArm, 16, 54, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		bipedRightForeArm = new ModelRenderer(this);
		bipedRightForeArm.setRotationPoint(-1.0F, 4.0F, 2.0F);
		bipedRightUpperArm.addChild(bipedRightForeArm);
		setRotationAngle(bipedRightForeArm, -0.2618F, 0.0F, 0.0F);
		bipedRightForeArm.cubeList
				.add(new ModelBox(bipedRightForeArm, 44, 50, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.0F, false));

		bipedRightUpperArm2 = new ModelRenderer(this);
		bipedRightUpperArm2.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(bipedRightUpperArm2);
		setRotationAngle(bipedRightUpperArm2, -0.5236F, 0.2618F, 1.3963F);
		bipedRightUpperArm2.cubeList
				.add(new ModelBox(bipedRightUpperArm2, 16, 54, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));

		bipedRightForeArm2 = new ModelRenderer(this);
		bipedRightForeArm2.setRotationPoint(-1.0F, 4.0F, 2.0F);
		bipedRightUpperArm2.addChild(bipedRightForeArm2);
		setRotationAngle(bipedRightForeArm2, -1.0472F, 0.0F, 0.0F);
		bipedRightForeArm2.cubeList
				.add(new ModelBox(bipedRightForeArm2, 44, 50, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.0F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, -10.0F, 0.0F);
		body.addChild(bipedLeftArm);

		bipedLeftUpperArm = new ModelRenderer(this);
		bipedLeftUpperArm.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(bipedLeftUpperArm);
		setRotationAngle(bipedLeftUpperArm, -1.0472F, 0.0F, 0.0F);
		bipedLeftUpperArm.cubeList
				.add(new ModelBox(bipedLeftUpperArm, 16, 54, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, true));

		bipedLeftForeArm = new ModelRenderer(this);
		bipedLeftForeArm.setRotationPoint(1.0F, 4.0F, 2.0F);
		bipedLeftUpperArm.addChild(bipedLeftForeArm);
		setRotationAngle(bipedLeftForeArm, -0.2618F, 0.0F, 0.0F);
		bipedLeftForeArm.cubeList.add(new ModelBox(bipedLeftForeArm, 44, 50, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.0F, true));
		bipedLeftForeArm.cubeList.add(new ModelBox(bipedLeftForeArm, 44, 66, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.5F, true));

		bipedLeftUpperArm2 = new ModelRenderer(this);
		bipedLeftUpperArm2.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(bipedLeftUpperArm2);
		setRotationAngle(bipedLeftUpperArm2, -0.5236F, -1.5708F, -1.3963F);
		bipedLeftUpperArm2.cubeList
				.add(new ModelBox(bipedLeftUpperArm2, 16, 54, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, true));

		bipedLeftForeArm2 = new ModelRenderer(this);
		bipedLeftForeArm2.setRotationPoint(1.0F, 4.0F, 2.0F);
		bipedLeftUpperArm2.addChild(bipedLeftForeArm2);
		setRotationAngle(bipedLeftForeArm2, -1.0472F, 0.0F, 0.0F);
		bipedLeftForeArm2.cubeList
				.add(new ModelBox(bipedLeftForeArm2, 44, 50, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.0F, true));
		bipedLeftForeArm2.cubeList
				.add(new ModelBox(bipedLeftForeArm2, 44, 66, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.5F, true));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 15.0F, 0.0F);

		rightThigh = new ModelRenderer(this);
		rightThigh.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightLeg.addChild(rightThigh);
		setRotationAngle(rightThigh, -0.7854F, 0.6545F, 0.0F);
		rightThigh.cubeList.add(new ModelBox(rightThigh, 0, 54, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));

		calfRight = new ModelRenderer(this);
		calfRight.setRotationPoint(0.0F, 6.0F, -2.0F);
		rightThigh.addChild(calfRight);
		setRotationAngle(calfRight, 0.7854F, 0.0F, 0.0F);
		calfRight.cubeList.add(new ModelBox(calfRight, 52, 6, -2.0F, 0.0F, 0.0F, 4, 6, 4, 0.0F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 15.0F, 0.0F);

		leftThigh = new ModelRenderer(this);
		leftThigh.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftLeg.addChild(leftThigh);
		setRotationAngle(leftThigh, -0.7854F, -0.6545F, 0.0F);
		leftThigh.cubeList.add(new ModelBox(leftThigh, 0, 54, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, true));

		calfLeft = new ModelRenderer(this);
		calfLeft.setRotationPoint(0.0F, 6.0F, -2.0F);
		leftThigh.addChild(calfLeft);
		setRotationAngle(calfLeft, 0.7854F, 0.0F, 0.0F);
		calfLeft.cubeList.add(new ModelBox(calfLeft, 52, 6, -2.0F, 0.0F, 0.0F, 4, 6, 4, 0.0F, true));

		tail = new ModelRenderer(this);
		tail.setRotationPoint(0.0F, 15.0F, 0.0F);
		setRotationAngle(tail, 0.7854F, 0.0F, 0.0F);
		tail.cubeList.add(new ModelBox(tail, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail1 = new ModelRenderer(this);
		tail1.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail.addChild(tail1);
		setRotationAngle(tail1, 0.2618F, 0.0F, 0.1745F);
		tail1.cubeList.add(new ModelBox(tail1, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail2 = new ModelRenderer(this);
		tail2.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail1.addChild(tail2);
		setRotationAngle(tail2, 0.2618F, 0.0F, 0.1745F);
		tail2.cubeList.add(new ModelBox(tail2, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail3 = new ModelRenderer(this);
		tail3.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail2.addChild(tail3);
		setRotationAngle(tail3, 0.2618F, 0.0F, 0.1745F);
		tail3.cubeList.add(new ModelBox(tail3, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail4 = new ModelRenderer(this);
		tail4.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail3.addChild(tail4);
		setRotationAngle(tail4, 0.2618F, 0.0F, 0.0F);
		tail4.cubeList.add(new ModelBox(tail4, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail5 = new ModelRenderer(this);
		tail5.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail4.addChild(tail5);
		setRotationAngle(tail5, 0.2618F, 0.0F, 0.0F);
		tail5.cubeList.add(new ModelBox(tail5, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail6 = new ModelRenderer(this);
		tail6.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail5.addChild(tail6);
		setRotationAngle(tail6, 0.2618F, 0.0F, 0.0F);
		tail6.cubeList.add(new ModelBox(tail6, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail7 = new ModelRenderer(this);
		tail7.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail6.addChild(tail7);
		setRotationAngle(tail7, 0.2618F, 0.0F, 0.0F);
		tail7.cubeList.add(new ModelBox(tail7, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail8 = new ModelRenderer(this);
		tail8.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail7.addChild(tail8);
		setRotationAngle(tail8, 0.2618F, 0.0F, 0.0F);
		tail8.cubeList.add(new ModelBox(tail8, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail9 = new ModelRenderer(this);
		tail9.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail8.addChild(tail9);
		setRotationAngle(tail9, 0.2618F, 0.0F, 0.0F);
		tail9.cubeList.add(new ModelBox(tail9, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail10 = new ModelRenderer(this);
		tail10.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail9.addChild(tail10);
		setRotationAngle(tail10, 0.1309F, 0.0F, 0.0F);
		tail10.cubeList.add(new ModelBox(tail10, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail11 = new ModelRenderer(this);
		tail11.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail10.addChild(tail11);
		setRotationAngle(tail11, 0.1309F, 0.0F, 0.0F);
		tail11.cubeList.add(new ModelBox(tail11, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail12 = new ModelRenderer(this);
		tail12.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail11.addChild(tail12);
		setRotationAngle(tail12, 0.1309F, 0.0F, 0.0F);
		tail12.cubeList.add(new ModelBox(tail12, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail13 = new ModelRenderer(this);
		tail13.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail12.addChild(tail13);
		setRotationAngle(tail13, 0.1309F, 0.0F, 0.0F);
		tail13.cubeList.add(new ModelBox(tail13, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail14 = new ModelRenderer(this);
		tail14.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail13.addChild(tail14);
		setRotationAngle(tail14, 0.1309F, 0.0F, 0.0F);
		tail14.cubeList.add(new ModelBox(tail14, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail15 = new ModelRenderer(this);
		tail15.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail14.addChild(tail15);
		setRotationAngle(tail15, 0.1309F, 0.0F, 0.0F);
		tail15.cubeList.add(new ModelBox(tail15, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tail16 = new ModelRenderer(this);
		tail16.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail15.addChild(tail16);
		setRotationAngle(tail16, 0.1309F, 0.0F, 0.0F);
		tail16.cubeList.add(new ModelBox(tail16, 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));

		tailEnd = new ModelRenderer(this);
		tailEnd.setRotationPoint(0.0F, 0.0F, 4.0F);
		tail16.addChild(tailEnd);
		setRotationAngle(tailEnd, 0.2618F, 0.0F, 0.0F);
		tailEnd.cubeList.add(new ModelBox(tailEnd, 58, 58, -2.0F, -0.5F, 0.0F, 4, 1, 2, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.5F, 2.0F);
		tailEnd.addChild(bone);
		setRotationAngle(bone, 0.2618F, 0.0F, 0.0F);

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(0.0F, -1.0F, 0.0F);
		bone.addChild(bone14);
		setRotationAngle(bone14, 0.0F, 0.7854F, 0.0F);
		bone14.cubeList.add(new ModelBox(bone14, 56, 50, -1.5F, 0.0F, -1.5F, 3, 1, 3, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -0.5F, 2.0F);
		tailEnd.addChild(bone2);
		setRotationAngle(bone2, -0.2618F, 0.0F, 0.0F);

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(0.0F, 1.0F, 0.0F);
		bone2.addChild(bone15);
		setRotationAngle(bone15, 0.0F, 0.7854F, 0.0F);
		bone15.cubeList.add(new ModelBox(bone15, 60, 54, -1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		body.render(f5);
		bipedRightLeg.render(f5);
		bipedLeftLeg.render(f5);
		tail.render(f5);
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