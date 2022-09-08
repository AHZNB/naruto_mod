// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelFiveTails extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer bone;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer bone4;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer bone5;
	private final ModelRenderer cube_r13;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer bone3;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer cube_r18;
	private final ModelRenderer Jaw;
	private final ModelRenderer cube_r19;
	private final ModelRenderer cube_r20;
	private final ModelRenderer bone2;
	private final ModelRenderer body;
	private final ModelRenderer cube_r21;
	private final ModelRenderer cube_r22;
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
	private final ModelRenderer leg1;
	private final ModelRenderer cube_r23;
	private final ModelRenderer cube_r24;
	private final ModelRenderer cube_r25;
	private final ModelRenderer Foot;
	private final ModelRenderer hoof_r1;
	private final ModelRenderer hoof_r2;
	private final ModelRenderer hoof_r3;
	private final ModelRenderer leg2;
	private final ModelRenderer cube_r26;
	private final ModelRenderer cube_r27;
	private final ModelRenderer cube_r28;
	private final ModelRenderer Foot2;
	private final ModelRenderer hoof_r4;
	private final ModelRenderer hoof_r5;
	private final ModelRenderer hoof_r6;
	private final ModelRenderer leg3;
	private final ModelRenderer cube_r29;
	private final ModelRenderer cube_r30;
	private final ModelRenderer cube_r31;
	private final ModelRenderer Foot3;
	private final ModelRenderer hoof_r7;
	private final ModelRenderer hoof_r8;
	private final ModelRenderer hoof_r9;
	private final ModelRenderer leg4;
	private final ModelRenderer cube_r32;
	private final ModelRenderer cube_r33;
	private final ModelRenderer cube_r34;
	private final ModelRenderer Foot4;
	private final ModelRenderer hoof_r10;
	private final ModelRenderer hoof_r11;
	private final ModelRenderer hoof_r12;

	public ModelFiveTails() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 13.0F, -4.0F);

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(2.0099F, 0.5605F, -6.9564F);
		head.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.2618F, 0.1745F, 0.0436F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 19, 13, -0.6F, -1.0F, -0.5F, 1, 2, 2, -0.4F, true));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-2.0099F, 0.5605F, -6.9564F);
		head.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.2618F, -0.1745F, -0.0436F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 19, 13, -0.4F, -1.0F, -0.5F, 1, 2, 2, -0.4F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(2.1F, 0.3521F, -5.7954F);
		head.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.2618F, 0.0F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 13, -0.5F, -1.1F, -0.5F, 1, 2, 2, -0.4F, true));
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 13, -4.7F, -1.1F, -0.5F, 1, 2, 2, -0.4F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, 1.0272F, -8.158F);
		head.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.5236F, 0.0F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 32, 0, -2.0F, -1.3F, -0.9F, 4, 2, 2, -0.4F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, 0.1F, -7.5F);
		head.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.2618F, 0.0F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 28, 8, -2.0F, -0.4F, -0.7F, 4, 2, 3, -0.1F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.0F, 0.3861F, 0.5902F);
		head.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.2182F, 0.0F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 26, -2.0F, -2.5658F, -6.2052F, 4, 4, 4, 0.0F, false));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(-1.2F, -0.7182F, -4.8788F);
		head.addChild(bone);
		setRotationAngle(bone, -0.0873F, 0.0F, -0.4363F);

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.0F, -3.0F, 1.0F);
		bone.addChild(cube_r7);
		setRotationAngle(cube_r7, -0.4363F, 0.0F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 20, 37, -0.5F, -1.0F, 0.0F, 1, 2, 1, -0.2F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, -2.517F, 0.8706F);
		bone.addChild(cube_r8);
		setRotationAngle(cube_r8, -0.3491F, 0.0F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 40, 13, -0.5F, -0.5F, -0.1F, 1, 2, 1, -0.1F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, -0.6818F, 0.3788F);
		bone.addChild(cube_r9);
		setRotationAngle(cube_r9, -0.2618F, 0.0F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, false));
		cube_r9.cubeList.add(new ModelBox(cube_r9, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-1.2F, -1.1182F, -3.1288F);
		head.addChild(bone4);
		setRotationAngle(bone4, -0.2182F, 0.0F, -0.3491F);

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(0.0F, -3.0F, 1.5F);
		bone4.addChild(cube_r10);
		setRotationAngle(cube_r10, -0.4363F, 0.0F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 20, 37, -0.5F, -1.5F, 0.0F, 1, 2, 1, -0.2F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(0.0F, -2.517F, 1.3706F);
		bone4.addChild(cube_r11);
		setRotationAngle(cube_r11, -0.3491F, 0.0F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 40, 13, -0.5F, -1.0F, -0.1F, 1, 2, 1, -0.1F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(0.0F, -0.6818F, 0.8788F);
		bone4.addChild(cube_r12);
		setRotationAngle(cube_r12, -0.2618F, 0.0F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, false));
		cube_r12.cubeList.add(new ModelBox(cube_r12, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(1.2F, -1.1182F, -3.1288F);
		head.addChild(bone5);
		setRotationAngle(bone5, -0.3054F, 0.0F, 0.3054F);

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(0.0F, -3.0F, 1.5F);
		bone5.addChild(cube_r13);
		setRotationAngle(cube_r13, -0.4363F, 0.0F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 20, 37, -0.5F, -1.5F, 0.0F, 1, 2, 1, -0.2F, true));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(0.0F, -2.517F, 1.3706F);
		bone5.addChild(cube_r14);
		setRotationAngle(cube_r14, -0.3491F, 0.0F, 0.0F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 40, 13, -0.5F, -1.0F, -0.1F, 1, 2, 1, -0.1F, true));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(0.0F, -0.6818F, 0.8788F);
		bone5.addChild(cube_r15);
		setRotationAngle(cube_r15, -0.2618F, 0.0F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 19, 17, -0.5F, -1.2F, -0.2F, 1, 1, 1, 0.0F, true));
		cube_r15.cubeList.add(new ModelBox(cube_r15, 16, 26, -0.5F, -0.2F, -0.2F, 1, 1, 1, 0.1F, true));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(1.2F, -0.7182F, -5.8788F);
		head.addChild(bone3);
		setRotationAngle(bone3, -0.1745F, 0.0F, 0.4363F);

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(0.0F, -3.1736F, 2.9848F);
		bone3.addChild(cube_r16);
		setRotationAngle(cube_r16, -0.4363F, 0.0F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 20, 37, -0.5F, -1.0F, -1.0F, 1, 2, 1, -0.2F, true));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(0.0F, -2.6907F, 2.8554F);
		bone3.addChild(cube_r17);
		setRotationAngle(cube_r17, -0.3491F, 0.0F, 0.0F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 40, 13, -0.5F, -0.5F, -1.1F, 1, 2, 1, -0.1F, true));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(0.0F, -0.8554F, 2.3636F);
		bone3.addChild(cube_r18);
		setRotationAngle(cube_r18, -0.2618F, 0.0F, 0.0F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 19, 17, -0.5F, -1.2F, -1.2F, 1, 1, 1, 0.0F, true));
		cube_r18.cubeList.add(new ModelBox(cube_r18, 16, 26, -0.5F, -0.2F, -1.2F, 1, 2, 1, 0.1F, true));

		Jaw = new ModelRenderer(this);
		Jaw.setRotationPoint(-0.0099F, 2.5605F, -2.9564F);
		head.addChild(Jaw);
		Jaw.cubeList.add(new ModelBox(Jaw, 15, 38, -2.1901F, -1.4605F, -3.0436F, 1, 2, 3, 0.0F, false));
		Jaw.cubeList.add(new ModelBox(Jaw, 15, 38, 1.2099F, -1.4605F, -3.0436F, 1, 2, 3, 0.0F, true));
		Jaw.cubeList.add(new ModelBox(Jaw, 36, 4, -1.4901F, -1.4605F, -6.1436F, 3, 2, 1, 0.0F, false));
		Jaw.cubeList.add(new ModelBox(Jaw, 19, 13, -1.5F, -0.1F, -5.7F, 3, 1, 6, -0.3F, false));
		Jaw.cubeList.add(new ModelBox(Jaw, 0, 17, -2.4901F, -2.0605F, -2.2436F, 1, 1, 2, -0.3F, false));
		Jaw.cubeList.add(new ModelBox(Jaw, 0, 17, 1.5099F, -2.0605F, -2.2436F, 1, 1, 2, -0.3F, true));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(-1.6901F, -0.4605F, -4.5436F);
		Jaw.addChild(cube_r19);
		setRotationAngle(cube_r19, 0.0F, -0.2182F, 0.0F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 36, 19, -0.2F, -1.0F, -1.5F, 1, 2, 3, 0.0F, false));

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(1.7099F, -0.4605F, -4.5436F);
		Jaw.addChild(cube_r20);
		setRotationAngle(cube_r20, 0.0F, 0.2182F, 0.0F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 37, 25, -0.8F, -1.0F, -1.5F, 1, 2, 3, 0.0F, true));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -1.6F, -2.3F);
		head.addChild(bone2);
		setRotationAngle(bone2, 0.0873F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 20, 0, -2.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F, false));

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 13.0F, 0.0F);
		body.cubeList.add(new ModelBox(body, 0, 13, -3.0F, -2.1F, -3.4F, 6, 6, 7, -0.1F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(0.0F, 1.2393F, 8.4817F);
		body.addChild(cube_r21);
		setRotationAngle(cube_r21, -0.1745F, 0.0F, 0.0F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 0, 0, -3.0F, -2.6F, -6.0F, 6, 5, 8, -0.3F, false));

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(0.0F, 3.2643F, 2.7833F);
		body.addChild(cube_r22);
		setRotationAngle(cube_r22, 0.1309F, 0.0F, 0.0F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 22, 22, -2.5F, -1.4F, 0.6F, 5, 2, 4, 0.0F, false));

		Tail0_0 = new ModelRenderer(this);
		Tail0_0.setRotationPoint(0.0F, 0.5F, 10.5F);
		body.addChild(Tail0_0);
		setRotationAngle(Tail0_0, -0.7854F, 0.0F, 0.0F);
		Tail0_0.cubeList.add(new ModelBox(Tail0_0, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail0_1 = new ModelRenderer(this);
		Tail0_1.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail0_0.addChild(Tail0_1);
		setRotationAngle(Tail0_1, -0.2618F, 0.0F, 0.0F);
		Tail0_1.cubeList.add(new ModelBox(Tail0_1, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail0_2 = new ModelRenderer(this);
		Tail0_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail0_1.addChild(Tail0_2);
		setRotationAngle(Tail0_2, -0.2618F, 0.0F, 0.0F);
		Tail0_2.cubeList.add(new ModelBox(Tail0_2, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail0_3 = new ModelRenderer(this);
		Tail0_3.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail0_2.addChild(Tail0_3);
		setRotationAngle(Tail0_3, -0.2618F, 0.0F, 0.0F);
		Tail0_3.cubeList.add(new ModelBox(Tail0_3, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail0_4 = new ModelRenderer(this);
		Tail0_4.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail0_3.addChild(Tail0_4);
		setRotationAngle(Tail0_4, 0.2618F, 0.0F, 0.0F);
		Tail0_4.cubeList.add(new ModelBox(Tail0_4, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

		Tail0_5 = new ModelRenderer(this);
		Tail0_5.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail0_4.addChild(Tail0_5);
		setRotationAngle(Tail0_5, 0.2618F, 0.0F, 0.0F);
		Tail0_5.cubeList.add(new ModelBox(Tail0_5, 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

		Tail0_6 = new ModelRenderer(this);
		Tail0_6.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail0_5.addChild(Tail0_6);
		setRotationAngle(Tail0_6, 0.2618F, 0.0F, 0.0F);
		Tail0_6.cubeList.add(new ModelBox(Tail0_6, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

		Tail0_7 = new ModelRenderer(this);
		Tail0_7.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail0_6.addChild(Tail0_7);
		setRotationAngle(Tail0_7, 0.2618F, 0.0F, 0.0F);
		Tail0_7.cubeList.add(new ModelBox(Tail0_7, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

		Tail1_0 = new ModelRenderer(this);
		Tail1_0.setRotationPoint(-1.0F, 0.5F, 10.5F);
		body.addChild(Tail1_0);
		setRotationAngle(Tail1_0, -1.0472F, -0.2618F, 0.0F);
		Tail1_0.cubeList.add(new ModelBox(Tail1_0, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail1_1 = new ModelRenderer(this);
		Tail1_1.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail1_0.addChild(Tail1_1);
		setRotationAngle(Tail1_1, -0.2618F, 0.0F, 0.0F);
		Tail1_1.cubeList.add(new ModelBox(Tail1_1, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail1_2 = new ModelRenderer(this);
		Tail1_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail1_1.addChild(Tail1_2);
		setRotationAngle(Tail1_2, -0.2618F, 0.0F, 0.0F);
		Tail1_2.cubeList.add(new ModelBox(Tail1_2, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail1_3 = new ModelRenderer(this);
		Tail1_3.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail1_2.addChild(Tail1_3);
		setRotationAngle(Tail1_3, -0.2618F, 0.0F, 0.0F);
		Tail1_3.cubeList.add(new ModelBox(Tail1_3, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail1_4 = new ModelRenderer(this);
		Tail1_4.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail1_3.addChild(Tail1_4);
		setRotationAngle(Tail1_4, 0.2618F, 0.0F, 0.0F);
		Tail1_4.cubeList.add(new ModelBox(Tail1_4, 24, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

		Tail1_5 = new ModelRenderer(this);
		Tail1_5.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail1_4.addChild(Tail1_5);
		setRotationAngle(Tail1_5, 0.2618F, 0.0F, 0.0F);
		Tail1_5.cubeList.add(new ModelBox(Tail1_5, 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

		Tail1_6 = new ModelRenderer(this);
		Tail1_6.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail1_5.addChild(Tail1_6);
		setRotationAngle(Tail1_6, 0.2618F, 0.0F, 0.0F);
		Tail1_6.cubeList.add(new ModelBox(Tail1_6, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

		Tail1_7 = new ModelRenderer(this);
		Tail1_7.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail1_6.addChild(Tail1_7);
		setRotationAngle(Tail1_7, 0.2618F, 0.0F, 0.0F);
		Tail1_7.cubeList.add(new ModelBox(Tail1_7, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

		Tail2_0 = new ModelRenderer(this);
		Tail2_0.setRotationPoint(1.0F, 0.5F, 10.5F);
		body.addChild(Tail2_0);
		setRotationAngle(Tail2_0, -1.5708F, 0.2618F, 0.0F);
		Tail2_0.cubeList.add(new ModelBox(Tail2_0, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail2_1 = new ModelRenderer(this);
		Tail2_1.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail2_0.addChild(Tail2_1);
		setRotationAngle(Tail2_1, -0.2618F, 0.0F, 0.0F);
		Tail2_1.cubeList.add(new ModelBox(Tail2_1, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail2_2 = new ModelRenderer(this);
		Tail2_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail2_1.addChild(Tail2_2);
		setRotationAngle(Tail2_2, -0.2618F, 0.0F, 0.0F);
		Tail2_2.cubeList.add(new ModelBox(Tail2_2, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail2_3 = new ModelRenderer(this);
		Tail2_3.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail2_2.addChild(Tail2_3);
		setRotationAngle(Tail2_3, -0.2618F, 0.0F, 0.0F);
		Tail2_3.cubeList.add(new ModelBox(Tail2_3, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail2_4 = new ModelRenderer(this);
		Tail2_4.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail2_3.addChild(Tail2_4);
		setRotationAngle(Tail2_4, 0.2618F, 0.0F, 0.0F);
		Tail2_4.cubeList.add(new ModelBox(Tail2_4, 24, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

		Tail2_5 = new ModelRenderer(this);
		Tail2_5.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail2_4.addChild(Tail2_5);
		setRotationAngle(Tail2_5, 0.2618F, 0.0F, 0.0F);
		Tail2_5.cubeList.add(new ModelBox(Tail2_5, 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

		Tail2_6 = new ModelRenderer(this);
		Tail2_6.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail2_5.addChild(Tail2_6);
		setRotationAngle(Tail2_6, 0.2618F, 0.0F, 0.0F);
		Tail2_6.cubeList.add(new ModelBox(Tail2_6, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

		Tail2_7 = new ModelRenderer(this);
		Tail2_7.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail2_6.addChild(Tail2_7);
		setRotationAngle(Tail2_7, 0.2618F, 0.0F, 0.0F);
		Tail2_7.cubeList.add(new ModelBox(Tail2_7, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

		Tail3_0 = new ModelRenderer(this);
		Tail3_0.setRotationPoint(-2.0F, 0.5F, 10.5F);
		body.addChild(Tail3_0);
		setRotationAngle(Tail3_0, -1.3963F, -0.5236F, 0.0F);
		Tail3_0.cubeList.add(new ModelBox(Tail3_0, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail3_1 = new ModelRenderer(this);
		Tail3_1.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail3_0.addChild(Tail3_1);
		setRotationAngle(Tail3_1, -0.2618F, 0.0F, 0.0F);
		Tail3_1.cubeList.add(new ModelBox(Tail3_1, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail3_2 = new ModelRenderer(this);
		Tail3_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail3_1.addChild(Tail3_2);
		setRotationAngle(Tail3_2, -0.2618F, 0.0F, 0.0F);
		Tail3_2.cubeList.add(new ModelBox(Tail3_2, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail3_3 = new ModelRenderer(this);
		Tail3_3.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail3_2.addChild(Tail3_3);
		setRotationAngle(Tail3_3, -0.2618F, 0.0F, 0.0F);
		Tail3_3.cubeList.add(new ModelBox(Tail3_3, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail3_4 = new ModelRenderer(this);
		Tail3_4.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail3_3.addChild(Tail3_4);
		setRotationAngle(Tail3_4, 0.2618F, 0.0F, 0.0F);
		Tail3_4.cubeList.add(new ModelBox(Tail3_4, 24, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

		Tail3_5 = new ModelRenderer(this);
		Tail3_5.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail3_4.addChild(Tail3_5);
		setRotationAngle(Tail3_5, 0.2618F, 0.0F, 0.0F);
		Tail3_5.cubeList.add(new ModelBox(Tail3_5, 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

		Tail3_6 = new ModelRenderer(this);
		Tail3_6.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail3_5.addChild(Tail3_6);
		setRotationAngle(Tail3_6, 0.2618F, 0.0F, 0.0F);
		Tail3_6.cubeList.add(new ModelBox(Tail3_6, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

		Tail3_7 = new ModelRenderer(this);
		Tail3_7.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail3_6.addChild(Tail3_7);
		setRotationAngle(Tail3_7, 0.2618F, 0.0F, 0.0F);
		Tail3_7.cubeList.add(new ModelBox(Tail3_7, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

		Tail4_0 = new ModelRenderer(this);
		Tail4_0.setRotationPoint(2.0F, 0.5F, 10.5F);
		body.addChild(Tail4_0);
		setRotationAngle(Tail4_0, -1.2217F, 0.5236F, 0.0F);
		Tail4_0.cubeList.add(new ModelBox(Tail4_0, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail4_1 = new ModelRenderer(this);
		Tail4_1.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail4_0.addChild(Tail4_1);
		setRotationAngle(Tail4_1, -0.2618F, 0.0F, 0.0F);
		Tail4_1.cubeList.add(new ModelBox(Tail4_1, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail4_2 = new ModelRenderer(this);
		Tail4_2.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail4_1.addChild(Tail4_2);
		setRotationAngle(Tail4_2, -0.2618F, 0.0F, 0.0F);
		Tail4_2.cubeList.add(new ModelBox(Tail4_2, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail4_3 = new ModelRenderer(this);
		Tail4_3.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail4_2.addChild(Tail4_3);
		setRotationAngle(Tail4_3, -0.2618F, 0.0F, 0.0F);
		Tail4_3.cubeList.add(new ModelBox(Tail4_3, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, 0.0F, false));

		Tail4_4 = new ModelRenderer(this);
		Tail4_4.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail4_3.addChild(Tail4_4);
		setRotationAngle(Tail4_4, 0.2618F, 0.0F, 0.0F);
		Tail4_4.cubeList.add(new ModelBox(Tail4_4, 33, 36, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.1F, false));

		Tail4_5 = new ModelRenderer(this);
		Tail4_5.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail4_4.addChild(Tail4_5);
		setRotationAngle(Tail4_5, 0.2618F, 0.0F, 0.0F);
		Tail4_5.cubeList.add(new ModelBox(Tail4_5, 33, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.2F, false));

		Tail4_6 = new ModelRenderer(this);
		Tail4_6.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail4_5.addChild(Tail4_6);
		setRotationAngle(Tail4_6, 0.2618F, 0.0F, 0.0F);
		Tail4_6.cubeList.add(new ModelBox(Tail4_6, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.4F, false));

		Tail4_7 = new ModelRenderer(this);
		Tail4_7.setRotationPoint(0.0F, -2.0F, 0.0F);
		Tail4_6.addChild(Tail4_7);
		setRotationAngle(Tail4_7, 0.2618F, 0.0F, 0.0F);
		Tail4_7.cubeList.add(new ModelBox(Tail4_7, 42, 42, -1.0F, -2.5F, -1.0F, 2, 3, 2, -0.6F, false));

		leg1 = new ModelRenderer(this);
		leg1.setRotationPoint(-2.0F, 13.0F, -1.0F);
		setRotationAngle(leg1, 0.0F, 0.0F, 0.0873F);

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(-2.0F, 6.6F, 1.0F);
		leg1.addChild(cube_r23);
		setRotationAngle(cube_r23, -0.3491F, 0.0F, 0.0F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 0, 0, -0.6F, -1.6F, -2.0F, 2, 5, 2, -0.1F, false));

		cube_r24 = new ModelRenderer(this);
		cube_r24.setRotationPoint(-1.3722F, 3.9631F, -1.0951F);
		leg1.addChild(cube_r24);
		setRotationAngle(cube_r24, 0.3054F, 0.0F, 0.1309F);
		cube_r24.cubeList.add(new ModelBox(cube_r24, 0, 34, -1.0F, -2.0F, -0.9F, 2, 4, 3, 0.0F, false));

		cube_r25 = new ModelRenderer(this);
		cube_r25.setRotationPoint(-0.3946F, -0.5729F, 0.0539F);
		leg1.addChild(cube_r25);
		setRotationAngle(cube_r25, -0.3927F, 0.0F, 0.1745F);
		cube_r25.cubeList.add(new ModelBox(cube_r25, 16, 28, -1.2F, -2.3F, -1.5F, 3, 6, 3, 0.1F, false));

		Foot = new ModelRenderer(this);
		Foot.setRotationPoint(-2.6173F, 10.3869F, -1.7375F);
		leg1.addChild(Foot);

		hoof_r1 = new ModelRenderer(this);
		hoof_r1.setRotationPoint(1.6173F, 0.2131F, -0.2625F);
		Foot.addChild(hoof_r1);
		setRotationAngle(hoof_r1, -1.5708F, 0.0F, -0.0873F);
		hoof_r1.cubeList.add(new ModelBox(hoof_r1, 0, 47, -1.6F, -2.2F, -0.3F, 2, 3, 1, -0.05F, true));

		hoof_r2 = new ModelRenderer(this);
		hoof_r2.setRotationPoint(0.6173F, -0.7869F, 0.7375F);
		Foot.addChild(hoof_r2);
		setRotationAngle(hoof_r2, 0.0436F, 0.0F, 0.0122F);
		hoof_r2.cubeList.add(new ModelBox(hoof_r2, 0, 52, -0.6F, -0.9F, -0.9F, 2, 2, 2, -0.1F, true));

		hoof_r3 = new ModelRenderer(this);
		hoof_r3.setRotationPoint(1.6173F, 0.2131F, -0.2625F);
		Foot.addChild(hoof_r3);
		setRotationAngle(hoof_r3, -1.0908F, 0.0F, -0.0873F);
		hoof_r3.cubeList.add(new ModelBox(hoof_r3, 8, 47, -1.6F, -1.8F, -0.7F, 2, 3, 1, -0.1F, true));

		leg2 = new ModelRenderer(this);
		leg2.setRotationPoint(2.0F, 13.0F, -1.0F);
		setRotationAngle(leg2, 0.0F, 0.0F, -0.0873F);

		cube_r26 = new ModelRenderer(this);
		cube_r26.setRotationPoint(2.0F, 6.6F, 1.0F);
		leg2.addChild(cube_r26);
		setRotationAngle(cube_r26, -0.3491F, 0.0F, -0.0349F);
		cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 0, -1.35F, -1.5361F, -2.3407F, 2, 5, 2, -0.1F, true));

		cube_r27 = new ModelRenderer(this);
		cube_r27.setRotationPoint(1.3722F, 3.9631F, -1.0951F);
		leg2.addChild(cube_r27);
		setRotationAngle(cube_r27, 0.3054F, 0.0F, -0.1309F);
		cube_r27.cubeList.add(new ModelBox(cube_r27, 0, 34, -1.0F, -2.0F, -0.9F, 2, 4, 3, 0.0F, true));

		cube_r28 = new ModelRenderer(this);
		cube_r28.setRotationPoint(0.3946F, -0.5729F, 0.0539F);
		leg2.addChild(cube_r28);
		setRotationAngle(cube_r28, -0.3927F, 0.0F, -0.1745F);
		cube_r28.cubeList.add(new ModelBox(cube_r28, 16, 28, -1.8F, -2.3F, -1.5F, 3, 6, 3, 0.1F, true));

		Foot2 = new ModelRenderer(this);
		Foot2.setRotationPoint(2.6173F, 10.3869F, -1.7375F);
		leg2.addChild(Foot2);

		hoof_r4 = new ModelRenderer(this);
		hoof_r4.setRotationPoint(-0.6173F, 0.2131F, -0.2625F);
		Foot2.addChild(hoof_r4);
		setRotationAngle(hoof_r4, -1.5708F, 0.0F, 0.0873F);
		hoof_r4.cubeList.add(new ModelBox(hoof_r4, 0, 47, -1.32F, -1.9F, -0.3F, 2, 3, 1, -0.05F, true));

		hoof_r5 = new ModelRenderer(this);
		hoof_r5.setRotationPoint(-0.6173F, 0.2131F, -0.2625F);
		Foot2.addChild(hoof_r5);
		setRotationAngle(hoof_r5, -1.0908F, 0.0F, 0.0873F);
		hoof_r5.cubeList.add(new ModelBox(hoof_r5, 8, 47, -1.32F, -1.6F, -0.8F, 2, 3, 1, -0.1F, true));

		hoof_r6 = new ModelRenderer(this);
		hoof_r6.setRotationPoint(-0.6173F, -0.7869F, 0.7375F);
		Foot2.addChild(hoof_r6);
		setRotationAngle(hoof_r6, 0.0436F, 0.0F, 0.0314F);
		hoof_r6.cubeList.add(new ModelBox(hoof_r6, 0, 52, -1.3F, -0.9F, -1.2F, 2, 2, 2, -0.1F, true));

		leg3 = new ModelRenderer(this);
		leg3.setRotationPoint(-2.5F, 15.0F, 9.0F);
		setRotationAngle(leg3, 0.0F, 0.0F, 0.0873F);

		cube_r29 = new ModelRenderer(this);
		cube_r29.setRotationPoint(-0.5989F, 6.5567F, 0.2F);
		leg3.addChild(cube_r29);
		setRotationAngle(cube_r29, -0.3054F, 0.0F, 0.0F);
		cube_r29.cubeList.add(new ModelBox(cube_r29, 10, 35, -0.5F, -2.4F, -1.0F, 2, 3, 2, -0.1F, false));

		cube_r30 = new ModelRenderer(this);
		cube_r30.setRotationPoint(-0.0989F, 3.8567F, -0.1F);
		leg3.addChild(cube_r30);
		setRotationAngle(cube_r30, 0.1745F, 0.0F, 0.0F);
		cube_r30.cubeList.add(new ModelBox(cube_r30, 31, 13, -1.5F, -1.8F, -1.2F, 3, 3, 3, -0.1F, false));

		cube_r31 = new ModelRenderer(this);
		cube_r31.setRotationPoint(0.7011F, -0.9433F, 0.4F);
		leg3.addChild(cube_r31);
		setRotationAngle(cube_r31, -0.2618F, 0.0F, 0.0F);
		cube_r31.cubeList.add(new ModelBox(cube_r31, 28, 28, -2.3F, -1.4F, -1.0F, 3, 5, 3, 0.3F, false));

		Foot3 = new ModelRenderer(this);
		Foot3.setRotationPoint(-1.0163F, 8.1936F, -0.7375F);
		leg3.addChild(Foot3);

		hoof_r7 = new ModelRenderer(this);
		hoof_r7.setRotationPoint(1.7173F, 0.4631F, -0.2625F);
		Foot3.addChild(hoof_r7);
		setRotationAngle(hoof_r7, -1.0908F, 0.0F, -0.0873F);
		hoof_r7.cubeList.add(new ModelBox(hoof_r7, 8, 47, -1.78F, -2.15F, -0.8F, 2, 3, 1, -0.1F, true));

		hoof_r8 = new ModelRenderer(this);
		hoof_r8.setRotationPoint(0.7173F, -0.5369F, 0.7375F);
		Foot3.addChild(hoof_r8);
		setRotationAngle(hoof_r8, 0.0436F, 0.0F, -0.0087F);
		hoof_r8.cubeList.add(new ModelBox(hoof_r8, 0, 52, -0.8F, -1.1F, -0.8F, 2, 2, 2, -0.1F, true));

		hoof_r9 = new ModelRenderer(this);
		hoof_r9.setRotationPoint(1.7173F, 0.4631F, -0.2625F);
		Foot3.addChild(hoof_r9);
		setRotationAngle(hoof_r9, -1.5708F, 0.0F, -0.0873F);
		hoof_r9.cubeList.add(new ModelBox(hoof_r9, 0, 47, -1.78F, -2.4F, -0.55F, 2, 3, 1, -0.05F, true));

		leg4 = new ModelRenderer(this);
		leg4.setRotationPoint(2.5F, 15.0F, 9.0F);
		setRotationAngle(leg4, 0.0F, 0.0F, -0.0873F);

		cube_r32 = new ModelRenderer(this);
		cube_r32.setRotationPoint(0.5989F, 6.5567F, 0.2F);
		leg4.addChild(cube_r32);
		setRotationAngle(cube_r32, -0.3054F, 0.0F, 0.0F);
		cube_r32.cubeList.add(new ModelBox(cube_r32, 10, 35, -1.5F, -2.3F, -1.2F, 2, 3, 2, -0.1F, true));

		cube_r33 = new ModelRenderer(this);
		cube_r33.setRotationPoint(0.0989F, 3.8567F, -0.1F);
		leg4.addChild(cube_r33);
		setRotationAngle(cube_r33, 0.1745F, 0.0F, 0.0F);
		cube_r33.cubeList.add(new ModelBox(cube_r33, 31, 13, -1.5F, -1.8F, -1.2F, 3, 3, 3, -0.1F, true));

		cube_r34 = new ModelRenderer(this);
		cube_r34.setRotationPoint(-0.7011F, -0.9433F, 0.4F);
		leg4.addChild(cube_r34);
		setRotationAngle(cube_r34, -0.2618F, 0.0F, 0.0F);
		cube_r34.cubeList.add(new ModelBox(cube_r34, 28, 28, -0.7F, -1.4F, -1.0F, 3, 5, 3, 0.3F, true));

		Foot4 = new ModelRenderer(this);
		Foot4.setRotationPoint(1.0163F, 8.1936F, -0.7375F);
		leg4.addChild(Foot4);

		hoof_r10 = new ModelRenderer(this);
		hoof_r10.setRotationPoint(-0.7173F, -0.5369F, 0.7375F);
		Foot4.addChild(hoof_r10);
		setRotationAngle(hoof_r10, 0.0436F, 0.0F, 0.0436F);
		hoof_r10.cubeList.add(new ModelBox(hoof_r10, 0, 52, -1.2F, -1.0F, -1.0F, 2, 2, 2, -0.1F, true));

		hoof_r11 = new ModelRenderer(this);
		hoof_r11.setRotationPoint(-0.7173F, 0.4631F, -0.2625F);
		Foot4.addChild(hoof_r11);
		setRotationAngle(hoof_r11, -1.0908F, 0.0F, 0.0873F);
		hoof_r11.cubeList.add(new ModelBox(hoof_r11, 8, 47, -1.2F, -2.0F, -0.9F, 2, 3, 1, -0.1F, true));

		hoof_r12 = new ModelRenderer(this);
		hoof_r12.setRotationPoint(-0.7173F, 0.4631F, -0.2625F);
		Foot4.addChild(hoof_r12);
		setRotationAngle(hoof_r12, -1.5708F, 0.0F, 0.0873F);
		hoof_r12.cubeList.add(new ModelBox(hoof_r12, 0, 47, -1.2F, -2.17F, -0.45F, 2, 3, 1, -0.05F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head.render(f5);
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