// Made with Blockbench 4.6.1
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelRobe extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer hatAka;
	private final ModelRenderer Bell_r1;
	private final ModelRenderer Bell_r2;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer cube_r13;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cube_r15;
	private final ModelRenderer cube_r16;
	private final ModelRenderer hatKage;
	private final ModelRenderer pipes2;
	private final ModelRenderer pipe_r1;
	private final ModelRenderer pipe_r2;
	private final ModelRenderer pipes;
	private final ModelRenderer pipe_r3;
	private final ModelRenderer pipe_r4;
	private final ModelRenderer hhat2;
	private final ModelRenderer bone22;
	private final ModelRenderer cube_r17;
	private final ModelRenderer bone24;
	private final ModelRenderer cube_r18;
	private final ModelRenderer hhat;
	private final ModelRenderer logo3;
	private final ModelRenderer logo2;
	private final ModelRenderer cube_r19;
	private final ModelRenderer logo;
	private final ModelRenderer cube_r20;
	private final ModelRenderer bone23;
	private final ModelRenderer cube_r21;
	private final ModelRenderer bone21;
	private final ModelRenderer cube_r22;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer bipedBody;
	private final ModelRenderer bone;
	private final ModelRenderer bone8;
	private final ModelRenderer collar;
	private final ModelRenderer bone6;
	private final ModelRenderer collar2;
	private final ModelRenderer bone2;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedLeftArm;

	public ModelRobe() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);

		hatAka = new ModelRenderer(this);
		hatAka.setRotationPoint(0.0F, -5.5F, 0.0F);
		bipedHead.addChild(hatAka);
		setRotationAngle(hatAka, -0.0436F, 0.0F, 0.0F);

		Bell_r1 = new ModelRenderer(this);
		Bell_r1.setRotationPoint(-3.825F, 0.0F, -9.0F);
		hatAka.addChild(Bell_r1);
		setRotationAngle(Bell_r1, 0.0F, 2.0071F, 0.0F);
		Bell_r1.cubeList.add(new ModelBox(Bell_r1, 52, 16, 0.0F, 0.35F, -1.0F, 0, 7, 2, 0.0F, false));

		Bell_r2 = new ModelRenderer(this);
		Bell_r2.setRotationPoint(-3.825F, 0.0F, -9.0F);
		hatAka.addChild(Bell_r2);
		setRotationAngle(Bell_r2, 0.2182F, 0.3927F, 0.0F);
		Bell_r2.cubeList.add(new ModelBox(Bell_r2, 52, 24, 0.0F, -0.225F, -1.175F, 0, 2, 2, 0.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.9599F, 0.3927F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.9599F, 0.7854F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.9599F, 1.1781F, 0.0F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.9599F, 1.5708F, 0.0F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.9599F, 1.9635F, 0.0F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.9599F, 2.3562F, 0.0F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r7);
		setRotationAngle(cube_r7, 0.9599F, 2.7489F, 0.0F);
		cube_r7.cubeList.add(new ModelBox(cube_r7, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.9599F, 3.1416F, 0.0F);
		cube_r8.cubeList.add(new ModelBox(cube_r8, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r9);
		setRotationAngle(cube_r9, 0.9599F, -2.7489F, 0.0F);
		cube_r9.cubeList.add(new ModelBox(cube_r9, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r10);
		setRotationAngle(cube_r10, 0.9599F, -2.3562F, 0.0F);
		cube_r10.cubeList.add(new ModelBox(cube_r10, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r11);
		setRotationAngle(cube_r11, 0.9599F, -1.9635F, 0.0F);
		cube_r11.cubeList.add(new ModelBox(cube_r11, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.9599F, -1.5708F, 0.0F);
		cube_r12.cubeList.add(new ModelBox(cube_r12, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.9599F, -1.1781F, 0.0F);
		cube_r13.cubeList.add(new ModelBox(cube_r13, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.9599F, -0.7854F, 0.0F);
		cube_r14.cubeList.add(new ModelBox(cube_r14, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r15 = new ModelRenderer(this);
		cube_r15.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r15);
		setRotationAngle(cube_r15, 0.9599F, -0.3927F, 0.0F);
		cube_r15.cubeList.add(new ModelBox(cube_r15, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		cube_r16 = new ModelRenderer(this);
		cube_r16.setRotationPoint(0.0F, -6.5F, 0.0F);
		hatAka.addChild(cube_r16);
		setRotationAngle(cube_r16, 0.9599F, 0.0F, 0.0F);
		cube_r16.cubeList.add(new ModelBox(cube_r16, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));

		hatKage = new ModelRenderer(this);
		hatKage.setRotationPoint(0.0F, -0.075F, 0.0F);
		bipedHead.addChild(hatKage);

		pipes2 = new ModelRenderer(this);
		pipes2.setRotationPoint(4.771F, -8.9446F, 0.0F);
		hatKage.addChild(pipes2);

		pipe_r1 = new ModelRenderer(this);
		pipe_r1.setRotationPoint(0.0F, 0.0F, -1.7125F);
		pipes2.addChild(pipe_r1);
		setRotationAngle(pipe_r1, -1.5708F, 1.0472F, 2.138F);
		pipe_r1.cubeList.add(new ModelBox(pipe_r1, 25, 1, -2.0F, 0.0F, -1.0F, 4, 0, 2, 0.0F, false));

		pipe_r2 = new ModelRenderer(this);
		pipe_r2.setRotationPoint(0.879F, -0.0304F, 2.0F);
		pipes2.addChild(pipe_r2);
		setRotationAngle(pipe_r2, -1.5708F, -1.0472F, 2.138F);
		pipe_r2.cubeList.add(new ModelBox(pipe_r2, 25, 1, -2.0F, 0.575F, -0.275F, 4, 0, 2, 0.0F, false));

		pipes = new ModelRenderer(this);
		pipes.setRotationPoint(3.121F, -9.8946F, 0.0F);
		hatKage.addChild(pipes);

		pipe_r3 = new ModelRenderer(this);
		pipe_r3.setRotationPoint(0.0F, 0.0F, -1.7125F);
		pipes.addChild(pipe_r3);
		setRotationAngle(pipe_r3, -1.5708F, 1.0472F, 2.138F);
		pipe_r3.cubeList.add(new ModelBox(pipe_r3, 25, 1, -2.0F, 0.0F, -1.0F, 4, 0, 2, 0.0F, false));

		pipe_r4 = new ModelRenderer(this);
		pipe_r4.setRotationPoint(0.879F, -0.0304F, 2.0F);
		pipes.addChild(pipe_r4);
		setRotationAngle(pipe_r4, -1.5708F, -1.0472F, 2.138F);
		pipe_r4.cubeList.add(new ModelBox(pipe_r4, 25, 1, -2.0F, 0.575F, -0.275F, 4, 0, 2, 0.0F, false));

		hhat2 = new ModelRenderer(this);
		hhat2.setRotationPoint(0.0F, -8.75F, 0.0F);
		hatKage.addChild(hhat2);
		setRotationAngle(hhat2, 0.0F, 2.3562F, 0.0F);

		bone22 = new ModelRenderer(this);
		bone22.setRotationPoint(0.866F, -0.25F, -4.0354F);
		hhat2.addChild(bone22);
		setRotationAngle(bone22, 0.0F, 3.1416F, 0.0F);

		cube_r17 = new ModelRenderer(this);
		cube_r17.setRotationPoint(0.866F, 2.5F, 3.392F);
		bone22.addChild(cube_r17);
		setRotationAngle(cube_r17, -0.6589F, 0.0F, 0.0F);
		cube_r17.cubeList.add(new ModelBox(cube_r17, -10, 16, -7.5F, -0.0361F, -9.9375F, 15, 0, 10, 0.0F, false));

		bone24 = new ModelRenderer(this);
		bone24.setRotationPoint(0.866F, -0.25F, 11.6146F);
		hhat2.addChild(bone24);

		cube_r18 = new ModelRenderer(this);
		cube_r18.setRotationPoint(-0.866F, 2.5F, -4.2037F);
		bone24.addChild(cube_r18);
		setRotationAngle(cube_r18, -0.6589F, 0.0F, 0.0F);
		cube_r18.cubeList.add(new ModelBox(cube_r18, -10, 16, -7.5F, -0.0361F, -9.9375F, 15, 0, 10, 0.0F, false));

		hhat = new ModelRenderer(this);
		hhat.setRotationPoint(0.0F, -8.75F, 0.0F);
		hatKage.addChild(hhat);
		setRotationAngle(hhat, 0.0F, 0.7854F, 0.0F);

		logo3 = new ModelRenderer(this);
		logo3.setRotationPoint(0.15F, 32.85F, -0.1F);
		hhat.addChild(logo3);

		logo2 = new ModelRenderer(this);
		logo2.setRotationPoint(0.0F, -33.7014F, -3.6204F);
		logo3.addChild(logo2);
		setRotationAngle(logo2, 0.0F, 1.5708F, 0.0F);

		cube_r19 = new ModelRenderer(this);
		cube_r19.setRotationPoint(-3.5459F, 3.0514F, 7.4279F);
		logo2.addChild(cube_r19);
		setRotationAngle(cube_r19, -0.6589F, 0.0F, 0.0F);
		cube_r19.cubeList.add(new ModelBox(cube_r19, 23, 16, -0.5F, -0.0361F, -8.9375F, 8, 0, 9, 0.0F, false));

		logo = new ModelRenderer(this);
		logo.setRotationPoint(0.866F, -33.15F, -4.1104F);
		logo3.addChild(logo);
		setRotationAngle(logo, 0.0F, 3.1416F, 0.0F);

		cube_r20 = new ModelRenderer(this);
		cube_r20.setRotationPoint(0.866F, 2.5F, 3.392F);
		logo.addChild(cube_r20);
		setRotationAngle(cube_r20, -0.6589F, 0.0F, 0.0F);
		cube_r20.cubeList.add(new ModelBox(cube_r20, 39, 16, -7.5F, -0.0361F, -8.9375F, 8, 0, 9, 0.0F, false));

		bone23 = new ModelRenderer(this);
		bone23.setRotationPoint(0.866F, -0.25F, -4.0354F);
		hhat.addChild(bone23);
		setRotationAngle(bone23, 0.0F, 3.1416F, 0.0F);

		cube_r21 = new ModelRenderer(this);
		cube_r21.setRotationPoint(0.866F, 2.5F, 3.392F);
		bone23.addChild(cube_r21);
		setRotationAngle(cube_r21, -0.6589F, 0.0F, 0.0F);
		cube_r21.cubeList.add(new ModelBox(cube_r21, -10, 16, -7.5F, -0.0361F, -9.9375F, 15, 0, 10, 0.0F, false));

		bone21 = new ModelRenderer(this);
		bone21.setRotationPoint(0.866F, -0.25F, 11.6146F);
		hhat.addChild(bone21);

		cube_r22 = new ModelRenderer(this);
		cube_r22.setRotationPoint(-0.866F, 2.5F, -4.2037F);
		bone21.addChild(cube_r22);
		setRotationAngle(cube_r22, -0.6589F, 0.0F, 0.0F);
		cube_r22.cubeList.add(new ModelBox(cube_r22, -10, 16, -7.5F, -0.0361F, -9.9375F, 15, 0, 10, 0.0F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -7.85F, -4.0F, 8, 8, 8, 1.0F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(bone);
		setRotationAngle(bone, 0.1222F, 0.0F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 40, 32, -4.0F, 0.0F, -2.0F, 8, 20, 4, 0.6F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.addChild(bone8);
		setRotationAngle(bone8, -0.1222F, 0.0F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 16, 32, -4.0F, 0.0F, -2.0F, 8, 20, 4, 0.6F, false));

		collar = new ModelRenderer(this);
		collar.setRotationPoint(0.0F, 0.0F, -1.8F);
		bipedBody.addChild(collar);
		setRotationAngle(collar, -0.2182F, 0.0F, 0.0F);
		collar.cubeList.add(new ModelBox(collar, 40, 56, -4.0F, -4.0F, 0.0F, 8, 4, 4, 1.5F, false));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 0.35F, 3.25F);
		collar.addChild(bone6);
		setRotationAngle(bone6, -0.6109F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 20, 57, -4.0F, -6.0F, -1.0F, 8, 5, 2, 1.5F, false));

		collar2 = new ModelRenderer(this);
		collar2.setRotationPoint(0.0F, 0.0F, -1.8F);
		bipedBody.addChild(collar2);
		setRotationAngle(collar2, -0.2182F, 0.0F, 0.0F);
		collar2.cubeList.add(new ModelBox(collar2, 0, 24, -4.0F, -4.0F, 0.0F, 8, 4, 4, 1.4F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 0.35F, 3.25F);
		collar2.addChild(bone2);
		setRotationAngle(bone2, -0.6109F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 57, -4.0F, -6.0F, -1.0F, 8, 5, 2, 1.4F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));
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