// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelFlameFormation extends ModelBase {
	private final ModelRenderer bone;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer bone2;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer bone3;
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
	private final ModelRenderer bone6;
	private final ModelRenderer cube_r16;
	private final ModelRenderer cube_r17;
	private final ModelRenderer cube_r18;
	private final ModelRenderer bone7;
	private final ModelRenderer cube_r19;
	private final ModelRenderer cube_r20;
	private final ModelRenderer cube_r21;
	private final ModelRenderer bone8;
	private final ModelRenderer cube_r22;
	private final ModelRenderer cube_r23;
	private final ModelRenderer cube_r24;
	private final ModelRenderer bone9;
	private final ModelRenderer cube_r25;
	private final ModelRenderer cube_r26;
	private final ModelRenderer cube_r27;

	public ModelFlameFormation() {
		textureWidth = 32;
		textureHeight = 32;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -8.0F, -32.0F, -8.0F, 16, 32, 0, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, -16.0F, 0.0F);
		bone.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 3.1416F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -8.0F, -16.0F, -8.0F, 16, 32, 0, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, -16.0F, 0.0F);
		bone.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, -1.5708F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 0, -8.0F, -16.0F, -8.0F, 16, 32, 0, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, -16.0F, 0.0F);
		bone.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, 1.5708F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 0, -8.0F, -16.0F, -8.0F, 16, 32, 0, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 31, -8.0F, -33.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, -48.0F, 0.0F);
		bone2.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 3.1416F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 31, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, -48.0F, 0.0F);
		bone2.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, -1.5708F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 31, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.0F, -48.0F, 0.0F);
		bone2.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, 1.5708F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 31, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 0, 30, -8.0F, -34.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.0F, -49.0F, 0.0F);
		bone3.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.0F, 3.1416F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 30, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, -49.0F, 0.0F);
		bone3.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.0F, -1.5708F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 30, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, -49.0F, 0.0F);
		bone3.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.0F, 1.5708F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 30, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 29, -8.0F, -35.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(0.0F, -50.0F, 0.0F);
		bone4.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.0F, 3.1416F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 29, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(0.0F, -50.0F, 0.0F);
		bone4.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.0F, -1.5708F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 29, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(0.0F, -50.0F, 0.0F);
		bone4.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, 1.5708F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 29, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 0, 28, -8.0F, -36.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(0.0F, -51.0F, 0.0F);
		bone5.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.0F, 3.1416F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 28, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(0.0F, -51.0F, 0.0F);
		bone5.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, -1.5708F, 0.0F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 28, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(0.0F, -51.0F, 0.0F);
		bone5.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.0F, 1.5708F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 28, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 0, 27, -8.0F, -37.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(0.0F, -52.0F, 0.0F);
		bone6.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.0F, 3.1416F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 27, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(0.0F, -52.0F, 0.0F);
		bone6.addChild(cube_r17);
		setRotationAngle(cube_r17, 0.0F, -1.5708F, 0.0F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, 0, 27, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(0.0F, -52.0F, 0.0F);
		bone6.addChild(cube_r18);
		setRotationAngle(cube_r18, 0.0F, 1.5708F, 0.0F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 27, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 0, 26, -8.0F, -38.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(0.0F, -53.0F, 0.0F);
		bone7.addChild(cube_r19);
		setRotationAngle(cube_r19, 0.0F, 3.1416F, 0.0F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 26, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(0.0F, -53.0F, 0.0F);
		bone7.addChild(cube_r20);
		setRotationAngle(cube_r20, 0.0F, -1.5708F, 0.0F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 0, 26, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(0.0F, -53.0F, 0.0F);
		bone7.addChild(cube_r21);
		setRotationAngle(cube_r21, 0.0F, 1.5708F, 0.0F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, 0, 26, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 0, 25, -8.0F, -39.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(0.0F, -54.0F, 0.0F);
		bone8.addChild(cube_r22);
		setRotationAngle(cube_r22, 0.0F, 3.1416F, 0.0F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, 0, 25, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r23 = new ModelRenderer(this);
		cube_r23.setRotationPoint(0.0F, -54.0F, 0.0F);
		bone8.addChild(cube_r23);
		setRotationAngle(cube_r23, 0.0F, -1.5708F, 0.0F);
		cube_r23.cubeList.add(new ModelBox(cube_r23, 0, 25, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r24 = new ModelRenderer(this);
		cube_r24.setRotationPoint(0.0F, -54.0F, 0.0F);
		bone8.addChild(cube_r24);
		setRotationAngle(cube_r24, 0.0F, 1.5708F, 0.0F);
		cube_r24.cubeList.add(new ModelBox(cube_r24, 0, 25, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone9.cubeList.add(new ModelBox(bone9, 0, 24, -8.0F, -40.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r25 = new ModelRenderer(this);
		cube_r25.setRotationPoint(0.0F, -55.0F, 0.0F);
		bone9.addChild(cube_r25);
		setRotationAngle(cube_r25, 0.0F, 3.1416F, 0.0F);
		cube_r25.cubeList.add(new ModelBox(cube_r25, 0, 24, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r26 = new ModelRenderer(this);
		cube_r26.setRotationPoint(0.0F, -55.0F, 0.0F);
		bone9.addChild(cube_r26);
		setRotationAngle(cube_r26, 0.0F, -1.5708F, 0.0F);
		cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 24, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));

		cube_r27 = new ModelRenderer(this);
		cube_r27.setRotationPoint(0.0F, -55.0F, 0.0F);
		bone9.addChild(cube_r27);
		setRotationAngle(cube_r27, 0.0F, 1.5708F, 0.0F);
		cube_r27.cubeList.add(new ModelBox(cube_r27, 0, 24, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bone.render(f5);
		bone2.render(f5);
		bone3.render(f5);
		bone4.render(f5);
		bone5.render(f5);
		bone6.render(f5);
		bone7.render(f5);
		bone8.render(f5);
		bone9.render(f5);
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