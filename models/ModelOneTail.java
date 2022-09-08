// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelOneTail extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer jaw;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer Body;
	private final ModelRenderer Stomach;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer Upperbody;
	private final ModelRenderer Tail0;
	private final ModelRenderer Tail1;
	private final ModelRenderer Tail2;
	private final ModelRenderer Tail3;
	private final ModelRenderer Tail4;
	private final ModelRenderer Tail5;
	private final ModelRenderer Tail6;
	private final ModelRenderer Tail7;
	private final ModelRenderer Tail8;
	private final ModelRenderer Tail9;
	private final ModelRenderer RightArm;
	private final ModelRenderer LEftarm2;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer bone;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer cube_r13;
	private final ModelRenderer LeftArm;
	private final ModelRenderer LEftarm3;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer bone2;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer cube_r18;
	private final ModelRenderer cube_r19;
	private final ModelRenderer RightLeg;
	private final ModelRenderer cube_r20;
	private final ModelRenderer cube_r21;
	private final ModelRenderer cube_r22;
	private final ModelRenderer RightFoot;
	private final ModelRenderer cube_r23;
	private final ModelRenderer cube_r24;
	private final ModelRenderer cube_r25;
	private final ModelRenderer cube_r26;
	private final ModelRenderer cube_r27;
	private final ModelRenderer LeftLeg;
	private final ModelRenderer cube_r28;
	private final ModelRenderer cube_r29;
	private final ModelRenderer cube_r30;
	private final ModelRenderer LeftFoot;
	private final ModelRenderer cube_r31;
	private final ModelRenderer cube_r32;
	private final ModelRenderer cube_r33;
	private final ModelRenderer cube_r34;
	private final ModelRenderer cube_r35;

	public ModelOneTail() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 11.0F, -2.0F);
		head.cubeList.add(new ModelBox(head, 50, 13, -1.5F, -0.72F, -5.4006F, 3, 0, 3, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 50, 13, 0.0F, -0.72F, -4.3F, 2, 0, 2, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 28, 25, -3.0F, -2.75F, -2.5F, 6, 3, 4, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 8, 46, -2.0F, -3.9F, -1.5F, 4, 3, 2, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0436F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 40, 3, -1.6F, -3.2F, -4.6F, 3, 1, 1, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, 13.0F, -1.5F);
		head.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.1309F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 18, 29, -1.0F, -15.4F, -2.2F, 2, 1, 1, 0.0F, false));
		cube_r2.cubeList.add(new ModelBox(cube_r2, 48, 0, -1.5F, -15.5F, -2.1F, 3, 2, 2, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, 13.0F, -1.5F);
		head.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0873F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 47, 17, -1.5F, -17.5F, -1.3F, 3, 1, 3, 0.0F, false));
		cube_r3.cubeList.add(new ModelBox(cube_r3, 32, 35, -2.0F, -17.3F, -1.8F, 4, 4, 4, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-0.1F, -0.2346F, -4.0706F);
		head.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 1.5708F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 50, 13, -1.7F, -0.4854F, -1.8294F, 2, 0, 2, 0.0F, false));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(-0.1F, -0.7346F, -2.5706F);
		head.addChild(jaw);
		jaw.cubeList.add(new ModelBox(jaw, 47, 13, -1.5F, 0.0F, -3.0F, 3, 1, 3, 0.0F, false));
		jaw.cubeList.add(new ModelBox(jaw, 10, 19, -1.5F, 1.0146F, -3.0F, 3, 0, 3, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-1.1F, -3.6F, -4.2F);
		head.addChild(bone3);
		setRotationAngle(bone3, 0.3491F, -0.1745F, 0.1745F);
		bone3.cubeList.add(new ModelBox(bone3, 56, 0, -1.1F, -0.6F, -0.8F, 2, 1, 2, 0.0F, false));
		bone3.cubeList.add(new ModelBox(bone3, 0, 6, -1.3F, -0.8F, 0.0F, 1, 1, 2, -0.1F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(1.1F, -3.6F, -4.2F);
		head.addChild(bone4);
		setRotationAngle(bone4, 0.3491F, 0.1745F, -0.1745F);
		bone4.cubeList.add(new ModelBox(bone4, 56, 0, -0.9F, -0.6F, -0.8F, 2, 1, 2, 0.0F, true));
		bone4.cubeList.add(new ModelBox(bone4, 0, 6, 0.3F, -0.8F, 0.0F, 1, 1, 2, -0.1F, true));

		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, 11.0F, 0.0F);

		Stomach = new ModelRenderer(this);
		Stomach.setRotationPoint(0.0F, 13.0F, -3.5F);
		Body.addChild(Stomach);
		setRotationAngle(Stomach, -0.2618F, 0.0F, 0.0F);

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
		Stomach.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.1309F, 0.0F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 29, -4.0F, -8.0F, 5.3F, 8, 7, 1, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.0F, -12.4F, -3.7F);
		Stomach.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.1309F, 0.0F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 36, 8, -3.0F, 4.7769F, -1.3502F, 6, 7, 1, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(4.5F, -5.5F, 1.2F);
		Stomach.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.1309F, 0.0F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 0, -9.0F, -3.7037F, -4.9076F, 9, 8, 9, 0.0F, false));

		Upperbody = new ModelRenderer(this);
		Upperbody.setRotationPoint(0.0F, 13.0F, -2.5F);
		Body.addChild(Upperbody);
		Upperbody.cubeList.add(new ModelBox(Upperbody, 0, 17, -4.0F, -11.0F, -3.0F, 8, 3, 8, 0.0F, false));
		Upperbody.cubeList.add(new ModelBox(Upperbody, 24, 17, -4.0F, -12.0F, -2.0F, 8, 1, 7, 0.0F, false));
		Upperbody.cubeList.add(new ModelBox(Upperbody, 32, 32, -4.0F, -13.0F, 2.6F, 8, 1, 2, 0.0F, false));
		Upperbody.cubeList.add(new ModelBox(Upperbody, 27, 0, -4.0F, -15.0F, -1.0F, 8, 3, 5, 0.0F, false));

		Tail0 = new ModelRenderer(this);
		Tail0.setRotationPoint(0.0F, 10.0F, 3.0F);
		Body.addChild(Tail0);
		setRotationAngle(Tail0, -1.0472F, 0.0F, 0.0F);
		Tail0.cubeList.add(new ModelBox(Tail0, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 1.1F, false));

		Tail1 = new ModelRenderer(this);
		Tail1.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail0.addChild(Tail1);
		setRotationAngle(Tail1, 0.3491F, 0.0F, 0.0F);
		Tail1.cubeList.add(new ModelBox(Tail1, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 1.0F, false));

		Tail2 = new ModelRenderer(this);
		Tail2.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail1.addChild(Tail2);
		setRotationAngle(Tail2, 0.3491F, 0.0F, 0.0F);
		Tail2.cubeList.add(new ModelBox(Tail2, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.9F, false));

		Tail3 = new ModelRenderer(this);
		Tail3.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail2.addChild(Tail3);
		setRotationAngle(Tail3, 0.3491F, 0.0F, 0.0F);
		Tail3.cubeList.add(new ModelBox(Tail3, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));

		Tail4 = new ModelRenderer(this);
		Tail4.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail3.addChild(Tail4);
		setRotationAngle(Tail4, 0.3491F, 0.0F, 0.0F);
		Tail4.cubeList.add(new ModelBox(Tail4, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));

		Tail5 = new ModelRenderer(this);
		Tail5.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail4.addChild(Tail5);
		setRotationAngle(Tail5, 0.3491F, 0.0F, 0.0F);
		Tail5.cubeList.add(new ModelBox(Tail5, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));

		Tail6 = new ModelRenderer(this);
		Tail6.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail5.addChild(Tail6);
		setRotationAngle(Tail6, 0.2618F, 0.0F, 0.0F);
		Tail6.cubeList.add(new ModelBox(Tail6, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));

		Tail7 = new ModelRenderer(this);
		Tail7.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail6.addChild(Tail7);
		setRotationAngle(Tail7, 0.2618F, 0.0F, 0.0F);
		Tail7.cubeList.add(new ModelBox(Tail7, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));

		Tail8 = new ModelRenderer(this);
		Tail8.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail7.addChild(Tail8);
		setRotationAngle(Tail8, 0.2618F, 0.0F, 0.0F);
		Tail8.cubeList.add(new ModelBox(Tail8, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

		Tail9 = new ModelRenderer(this);
		Tail9.setRotationPoint(0.0F, -3.0F, 0.0F);
		Tail8.addChild(Tail9);
		setRotationAngle(Tail9, 0.2618F, 0.0F, 0.0F);
		Tail9.cubeList.add(new ModelBox(Tail9, 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, -0.4F, false));

		RightArm = new ModelRenderer(this);
		RightArm.setRotationPoint(-4.0F, 12.0F, -1.5F);
		RightArm.cubeList.add(new ModelBox(RightArm, 20, 39, -2.0F, -2.0F, -2.0F, 2, 3, 4, 0.0F, false));

		LEftarm2 = new ModelRenderer(this);
		LEftarm2.setRotationPoint(4.0F, -2.0F, -1.0F);
		RightArm.addChild(LEftarm2);
		setRotationAngle(LEftarm2, 0.0F, 0.0F, 0.0F);

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
		LEftarm2.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.6981F, -0.0436F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 37, -7.2F, 2.6F, 3.3F, 2, 7, 3, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
		LEftarm2.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.1309F, 0.0F, 0.2182F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 10, 37, -5.7F, 1.6F, -0.5F, 2, 6, 3, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(-6.25F, 9.25F, -1.75F);
		LEftarm2.addChild(bone);
		setRotationAngle(bone, 0.0F, -0.3054F, 0.0F);

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(6.0F, 4.75F, 1.75F);
		bone.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.3927F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 49, 32, -6.0F, -6.0F, -3.4F, 1, 1, 3, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(6.0F, -9.25F, 1.75F);
		bone.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.48F, 0.2182F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 27, 49, -6.8F, 7.6F, -10.4F, 1, 1, 3, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(6.0F, -9.25F, 1.75F);
		bone.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.48F, 0.1745F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 21, -6.8F, 6.8F, -10.3F, 1, 1, 3, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(6.0F, -9.25F, 1.75F);
		bone.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.5672F, 0.2182F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 17, -6.4F, 4.8F, -11.0F, 1, 1, 3, 0.0F, false));

		LeftArm = new ModelRenderer(this);
		LeftArm.setRotationPoint(4.0F, 12.0F, -1.5F);
		LeftArm.cubeList.add(new ModelBox(LeftArm, 20, 39, 0.0F, -2.0F, -2.0F, 2, 3, 4, 0.0F, true));

		LEftarm3 = new ModelRenderer(this);
		LEftarm3.setRotationPoint(-4.0F, -2.0F, -1.0F);
		LeftArm.addChild(LEftarm3);
		setRotationAngle(LEftarm3, 0.0F, 0.0F, 0.0F);

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(0.0F, 0.0F, 0.0F);
		LEftarm3.addChild(cube_r14);
		setRotationAngle(cube_r14, -0.6981F, 0.0436F, 0.0F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 37, 5.2F, 2.6F, 3.3F, 2, 7, 3, 0.0F, true));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(0.0F, 0.0F, 0.0F);
		LEftarm3.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.1309F, 0.0F, -0.2182F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 10, 37, 3.7F, 1.6F, -0.5F, 2, 6, 3, 0.0F, true));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(6.25F, 9.25F, -1.75F);
		LEftarm3.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.3054F, 0.0F);

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(-6.0F, 4.75F, 1.75F);
		bone2.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.3927F, 0.0F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 49, 32, 5.0F, -6.0F, -3.4F, 1, 1, 3, 0.0F, true));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(-6.0F, -9.25F, 1.75F);
		bone2.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.48F, -0.2182F, 0.0F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 27, 49, 5.8F, 7.6F, -10.4F, 1, 1, 3, 0.0F, true));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(-6.0F, -9.25F, 1.75F);
		bone2.addChild(cube_r18);
		setRotationAngle(cube_r18, 0.48F, -0.1745F, 0.0F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 21, 5.8F, 6.8F, -10.3F, 1, 1, 3, 0.0F, true));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(-6.0F, -9.25F, 1.75F);
		bone2.addChild(cube_r19);
		setRotationAngle(cube_r19, 0.5672F, -0.2182F, 0.0F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 17, 5.4F, 4.8F, -11.0F, 1, 1, 3, 0.0F, true));

		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(-4.5F, 18.0F, -0.75F);

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(5.5F, 6.0F, -1.75F);
		RightLeg.addChild(cube_r20);
		setRotationAngle(cube_r20, -0.0611F, 0.0F, -0.0436F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 44, 25, -7.0F, -5.0F, -0.7F, 4, 1, 3, 0.0F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(5.5F, 6.0F, -1.75F);
		RightLeg.addChild(cube_r21);
		setRotationAngle(cube_r21, -0.1309F, 0.0F, 0.0F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 18, 32, -8.0F, -4.0F, -1.0F, 5, 3, 4, 0.0F, false));

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(-0.7F, 0.8F, -0.05F);
		RightLeg.addChild(cube_r22);
		setRotationAngle(cube_r22, 1.5272F, 0.0F, 0.0F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 45, 44, -0.2F, -1.968F, -2.1467F, 2, 3, 3, 0.0F, false));

		RightFoot = new ModelRenderer(this);
		RightFoot.setRotationPoint(5.5F, 6.0F, -1.75F);
		RightLeg.addChild(RightFoot);
		RightFoot.cubeList.add(new ModelBox(RightFoot, 34, 44, -8.0F, -1.0F, 1.0F, 5, 1, 2, 0.0F, false));

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(-1.0F, 0.0F, 0.0F);
		RightFoot.addChild(cube_r23);
		setRotationAngle(cube_r23, 0.0F, -0.0873F, 0.0F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 0, 47, -3.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F, false));

		cube_r24 = new ModelRenderer(this);
		cube_r24.setRotationPoint(-1.0F, 0.0F, 0.0F);
		RightFoot.addChild(cube_r24);
		setRotationAngle(cube_r24, 0.0F, 0.0873F, 0.0F);
		cube_r24.cubeList.add(new ModelBox(cube_r24, 20, 46, -4.0F, -1.0F, -4.0F, 1, 1, 4, 0.0F, false));

		cube_r25 = new ModelRenderer(this);
		cube_r25.setRotationPoint(-1.0F, 0.0F, 0.0F);
		RightFoot.addChild(cube_r25);
		setRotationAngle(cube_r25, 0.0F, 0.1309F, 0.0F);
		cube_r25.cubeList.add(new ModelBox(cube_r25, 35, 47, -5.0F, -1.0F, -4.0F, 1, 1, 4, 0.0F, false));

		cube_r26 = new ModelRenderer(this);
		cube_r26.setRotationPoint(-1.0F, 0.0F, 0.0F);
		RightFoot.addChild(cube_r26);
		setRotationAngle(cube_r26, 0.0F, 0.1745F, 0.0F);
		cube_r26.cubeList.add(new ModelBox(cube_r26, 43, 38, -6.0F, -1.0F, -4.0F, 1, 1, 5, 0.0F, false));

		cube_r27 = new ModelRenderer(this);
		cube_r27.setRotationPoint(0.0F, 0.0F, 0.0F);
		RightFoot.addChild(cube_r27);
		setRotationAngle(cube_r27, 0.0F, 0.2182F, 0.0F);
		cube_r27.cubeList.add(new ModelBox(cube_r27, 27, 43, -8.0F, -1.0F, -4.0F, 1, 1, 5, 0.0F, false));

		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(4.5F, 18.0F, -0.75F);

		cube_r28 = new ModelRenderer(this);
		cube_r28.setRotationPoint(-5.5F, 6.0F, -1.75F);
		LeftLeg.addChild(cube_r28);
		setRotationAngle(cube_r28, -0.0611F, 0.0F, 0.0436F);
		cube_r28.cubeList.add(new ModelBox(cube_r28, 44, 25, 3.0F, -5.0F, -0.7F, 4, 1, 3, 0.0F, true));

		cube_r29 = new ModelRenderer(this);
		cube_r29.setRotationPoint(-5.5F, 6.0F, -1.75F);
		LeftLeg.addChild(cube_r29);
		setRotationAngle(cube_r29, -0.1309F, 0.0F, 0.0F);
		cube_r29.cubeList.add(new ModelBox(cube_r29, 18, 32, 3.0F, -4.0F, -1.0F, 5, 3, 4, 0.0F, true));

		cube_r30 = new ModelRenderer(this);
		cube_r30.setRotationPoint(0.7F, 0.8F, -0.05F);
		LeftLeg.addChild(cube_r30);
		setRotationAngle(cube_r30, 1.5272F, 0.0F, 0.0F);
		cube_r30.cubeList.add(new ModelBox(cube_r30, 45, 44, -1.8F, -1.968F, -2.1467F, 2, 3, 3, 0.0F, true));

		LeftFoot = new ModelRenderer(this);
		LeftFoot.setRotationPoint(-5.5F, 6.0F, -1.75F);
		LeftLeg.addChild(LeftFoot);
		LeftFoot.cubeList.add(new ModelBox(LeftFoot, 34, 44, 3.0F, -1.0F, 1.0F, 5, 1, 2, 0.0F, true));

		cube_r31 = new ModelRenderer(this);
		cube_r31.setRotationPoint(1.0F, 0.0F, 0.0F);
		LeftFoot.addChild(cube_r31);
		setRotationAngle(cube_r31, 0.0F, 0.0873F, 0.0F);
		cube_r31.cubeList.add(new ModelBox(cube_r31, 0, 47, 2.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F, true));

		cube_r32 = new ModelRenderer(this);
		cube_r32.setRotationPoint(1.0F, 0.0F, 0.0F);
		LeftFoot.addChild(cube_r32);
		setRotationAngle(cube_r32, 0.0F, -0.0873F, 0.0F);
		cube_r32.cubeList.add(new ModelBox(cube_r32, 20, 46, 3.0F, -1.0F, -4.0F, 1, 1, 4, 0.0F, true));

		cube_r33 = new ModelRenderer(this);
		cube_r33.setRotationPoint(1.0F, 0.0F, 0.0F);
		LeftFoot.addChild(cube_r33);
		setRotationAngle(cube_r33, 0.0F, -0.1309F, 0.0F);
		cube_r33.cubeList.add(new ModelBox(cube_r33, 35, 47, 4.0F, -1.0F, -4.0F, 1, 1, 4, 0.0F, true));

		cube_r34 = new ModelRenderer(this);
		cube_r34.setRotationPoint(1.0F, 0.0F, 0.0F);
		LeftFoot.addChild(cube_r34);
		setRotationAngle(cube_r34, 0.0F, -0.1745F, 0.0F);
		cube_r34.cubeList.add(new ModelBox(cube_r34, 43, 38, 5.0F, -1.0F, -4.0F, 1, 1, 5, 0.0F, true));

		cube_r35 = new ModelRenderer(this);
		cube_r35.setRotationPoint(0.0F, 0.0F, 0.0F);
		LeftFoot.addChild(cube_r35);
		setRotationAngle(cube_r35, 0.0F, -0.2182F, 0.0F);
		cube_r35.cubeList.add(new ModelBox(cube_r35, 27, 43, 7.0F, -1.0F, -4.0F, 1, 1, 5, 0.0F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
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