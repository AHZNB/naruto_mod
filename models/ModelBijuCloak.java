// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelBijuCloak extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer earLeft0;
	private final ModelRenderer earLeft1;
	private final ModelRenderer earLeft2;
	private final ModelRenderer earLeft3;
	private final ModelRenderer earLeft4;
	private final ModelRenderer earLeft5;
	private final ModelRenderer earRight0;
	private final ModelRenderer earRight1;
	private final ModelRenderer earRight2;
	private final ModelRenderer earRight3;
	private final ModelRenderer earRight4;
	private final ModelRenderer earRight5;
	private final ModelRenderer bipedBody;
	private final ModelRenderer tail0_0;
	private final ModelRenderer tail0_1;
	private final ModelRenderer tail0_2;
	private final ModelRenderer tail0_3;
	private final ModelRenderer tail0_4;
	private final ModelRenderer tail0_5;
	private final ModelRenderer tail1_0;
	private final ModelRenderer tail1_1;
	private final ModelRenderer tail1_2;
	private final ModelRenderer tail1_3;
	private final ModelRenderer tail1_4;
	private final ModelRenderer tail1_5;
	private final ModelRenderer tail2_0;
	private final ModelRenderer tail2_1;
	private final ModelRenderer tail2_2;
	private final ModelRenderer tail2_3;
	private final ModelRenderer tail2_4;
	private final ModelRenderer tail2_5;
	private final ModelRenderer tail3_0;
	private final ModelRenderer tail3_1;
	private final ModelRenderer tail3_2;
	private final ModelRenderer tail3_3;
	private final ModelRenderer tail3_4;
	private final ModelRenderer tail3_5;
	private final ModelRenderer tail4_0;
	private final ModelRenderer tail4_1;
	private final ModelRenderer tail4_2;
	private final ModelRenderer tail4_3;
	private final ModelRenderer tail4_4;
	private final ModelRenderer tail4_5;
	private final ModelRenderer tail5_0;
	private final ModelRenderer tail5_1;
	private final ModelRenderer tail5_2;
	private final ModelRenderer tail5_3;
	private final ModelRenderer tail5_4;
	private final ModelRenderer tail5_5;
	private final ModelRenderer tail6_0;
	private final ModelRenderer tail6_1;
	private final ModelRenderer tail6_2;
	private final ModelRenderer tail6_3;
	private final ModelRenderer tail6_4;
	private final ModelRenderer tail6_5;
	private final ModelRenderer tail7_0;
	private final ModelRenderer tail7_1;
	private final ModelRenderer tail7_2;
	private final ModelRenderer tail7_3;
	private final ModelRenderer tail7_4;
	private final ModelRenderer tail7_5;
	private final ModelRenderer tail8_0;
	private final ModelRenderer tail8_1;
	private final ModelRenderer tail8_2;
	private final ModelRenderer tail8_3;
	private final ModelRenderer tail8_4;
	private final ModelRenderer tail8_5;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer bipedLeftLeg;

	public ModelBijuCloak() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));

		earLeft0 = new ModelRenderer(this);
		earLeft0.setRotationPoint(3.5F, -8.5F, -0.5F);
		bipedHead.addChild(earLeft0);
		setRotationAngle(earLeft0, 0.0F, 0.0F, 0.7854F);
		earLeft0.cubeList.add(new ModelBox(earLeft0, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.8F, false));

		earLeft1 = new ModelRenderer(this);
		earLeft1.setRotationPoint(0.0F, -1.0F, 0.0F);
		earLeft0.addChild(earLeft1);
		setRotationAngle(earLeft1, 0.0F, 0.0F, -0.1745F);
		earLeft1.cubeList.add(new ModelBox(earLeft1, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.7F, false));

		earLeft2 = new ModelRenderer(this);
		earLeft2.setRotationPoint(0.0F, -1.0F, 0.0F);
		earLeft1.addChild(earLeft2);
		setRotationAngle(earLeft2, 0.0F, 0.0F, -0.1745F);
		earLeft2.cubeList.add(new ModelBox(earLeft2, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.6F, false));

		earLeft3 = new ModelRenderer(this);
		earLeft3.setRotationPoint(0.0F, -1.0F, 0.0F);
		earLeft2.addChild(earLeft3);
		setRotationAngle(earLeft3, 0.0F, 0.0F, -0.1745F);
		earLeft3.cubeList.add(new ModelBox(earLeft3, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.4F, false));

		earLeft4 = new ModelRenderer(this);
		earLeft4.setRotationPoint(0.0F, -1.0F, 0.0F);
		earLeft3.addChild(earLeft4);
		setRotationAngle(earLeft4, 0.0F, 0.0F, -0.1745F);
		earLeft4.cubeList.add(new ModelBox(earLeft4, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.2F, false));

		earLeft5 = new ModelRenderer(this);
		earLeft5.setRotationPoint(0.0F, -1.0F, 0.0F);
		earLeft4.addChild(earLeft5);
		setRotationAngle(earLeft5, 0.0F, 0.0F, -0.1745F);
		earLeft5.cubeList.add(new ModelBox(earLeft5, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, -0.1F, false));

		earRight0 = new ModelRenderer(this);
		earRight0.setRotationPoint(-3.5F, -8.5F, -0.5F);
		bipedHead.addChild(earRight0);
		setRotationAngle(earRight0, 0.0F, 0.0F, -0.7854F);
		earRight0.cubeList.add(new ModelBox(earRight0, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.8F, false));

		earRight1 = new ModelRenderer(this);
		earRight1.setRotationPoint(0.0F, -1.0F, 0.0F);
		earRight0.addChild(earRight1);
		setRotationAngle(earRight1, 0.0F, 0.0F, 0.1745F);
		earRight1.cubeList.add(new ModelBox(earRight1, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.7F, false));

		earRight2 = new ModelRenderer(this);
		earRight2.setRotationPoint(0.0F, -1.0F, 0.0F);
		earRight1.addChild(earRight2);
		setRotationAngle(earRight2, 0.0F, 0.0F, 0.1745F);
		earRight2.cubeList.add(new ModelBox(earRight2, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.6F, false));

		earRight3 = new ModelRenderer(this);
		earRight3.setRotationPoint(0.0F, -1.0F, 0.0F);
		earRight2.addChild(earRight3);
		setRotationAngle(earRight3, 0.0F, 0.0F, 0.1745F);
		earRight3.cubeList.add(new ModelBox(earRight3, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.4F, false));

		earRight4 = new ModelRenderer(this);
		earRight4.setRotationPoint(0.0F, -1.0F, 0.0F);
		earRight3.addChild(earRight4);
		setRotationAngle(earRight4, 0.0F, 0.0F, 0.1745F);
		earRight4.cubeList.add(new ModelBox(earRight4, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.2F, false));

		earRight5 = new ModelRenderer(this);
		earRight5.setRotationPoint(0.0F, -1.0F, 0.0F);
		earRight4.addChild(earRight5);
		setRotationAngle(earRight5, 0.0F, 0.0F, 0.1745F);
		earRight5.cubeList.add(new ModelBox(earRight5, 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, -0.1F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F, false));

		tail0_0 = new ModelRenderer(this);
		tail0_0.setRotationPoint(0.0F, 10.5F, 2.0F);
		bipedBody.addChild(tail0_0);
		setRotationAngle(tail0_0, -1.0472F, 0.0F, 0.0F);
		tail0_0.cubeList.add(new ModelBox(tail0_0, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail0_1 = new ModelRenderer(this);
		tail0_1.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail0_0.addChild(tail0_1);
		setRotationAngle(tail0_1, 0.2618F, 0.0F, 0.0F);
		tail0_1.cubeList.add(new ModelBox(tail0_1, 17, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail0_2 = new ModelRenderer(this);
		tail0_2.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail0_1.addChild(tail0_2);
		setRotationAngle(tail0_2, 0.2618F, 0.0F, 0.0F);
		tail0_2.cubeList.add(new ModelBox(tail0_2, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));

		tail0_3 = new ModelRenderer(this);
		tail0_3.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail0_2.addChild(tail0_3);
		setRotationAngle(tail0_3, 0.2618F, 0.0F, 0.0F);
		tail0_3.cubeList.add(new ModelBox(tail0_3, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail0_4 = new ModelRenderer(this);
		tail0_4.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail0_3.addChild(tail0_4);
		setRotationAngle(tail0_4, 0.2618F, 0.0F, 0.0F);
		tail0_4.cubeList.add(new ModelBox(tail0_4, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail0_5 = new ModelRenderer(this);
		tail0_5.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail0_4.addChild(tail0_5);
		setRotationAngle(tail0_5, 0.2618F, 0.0F, 0.0F);
		tail0_5.cubeList.add(new ModelBox(tail0_5, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));

		tail1_0 = new ModelRenderer(this);
		tail1_0.setRotationPoint(0.0F, 10.5F, 2.0F);
		bipedBody.addChild(tail1_0);
		setRotationAngle(tail1_0, -1.0472F, -0.5236F, 0.0F);
		tail1_0.cubeList.add(new ModelBox(tail1_0, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail1_1 = new ModelRenderer(this);
		tail1_1.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail1_0.addChild(tail1_1);
		setRotationAngle(tail1_1, 0.2618F, 0.0F, 0.0F);
		tail1_1.cubeList.add(new ModelBox(tail1_1, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail1_2 = new ModelRenderer(this);
		tail1_2.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail1_1.addChild(tail1_2);
		setRotationAngle(tail1_2, 0.2618F, 0.0F, 0.0F);
		tail1_2.cubeList.add(new ModelBox(tail1_2, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));

		tail1_3 = new ModelRenderer(this);
		tail1_3.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail1_2.addChild(tail1_3);
		setRotationAngle(tail1_3, 0.2618F, 0.0F, 0.0F);
		tail1_3.cubeList.add(new ModelBox(tail1_3, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail1_4 = new ModelRenderer(this);
		tail1_4.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail1_3.addChild(tail1_4);
		setRotationAngle(tail1_4, 0.2618F, 0.0F, 0.0F);
		tail1_4.cubeList.add(new ModelBox(tail1_4, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail1_5 = new ModelRenderer(this);
		tail1_5.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail1_4.addChild(tail1_5);
		setRotationAngle(tail1_5, 0.2618F, 0.0F, 0.0F);
		tail1_5.cubeList.add(new ModelBox(tail1_5, 17, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));

		tail2_0 = new ModelRenderer(this);
		tail2_0.setRotationPoint(0.0F, 10.5F, 2.0F);
		bipedBody.addChild(tail2_0);
		setRotationAngle(tail2_0, -1.0472F, 0.5236F, 0.0F);
		tail2_0.cubeList.add(new ModelBox(tail2_0, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail2_1 = new ModelRenderer(this);
		tail2_1.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail2_0.addChild(tail2_1);
		setRotationAngle(tail2_1, 0.2618F, 0.0F, 0.0F);
		tail2_1.cubeList.add(new ModelBox(tail2_1, 17, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail2_2 = new ModelRenderer(this);
		tail2_2.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail2_1.addChild(tail2_2);
		setRotationAngle(tail2_2, 0.2618F, 0.0F, 0.0F);
		tail2_2.cubeList.add(new ModelBox(tail2_2, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));

		tail2_3 = new ModelRenderer(this);
		tail2_3.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail2_2.addChild(tail2_3);
		setRotationAngle(tail2_3, 0.2618F, 0.0F, 0.0F);
		tail2_3.cubeList.add(new ModelBox(tail2_3, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail2_4 = new ModelRenderer(this);
		tail2_4.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail2_3.addChild(tail2_4);
		setRotationAngle(tail2_4, 0.2618F, 0.0F, 0.0F);
		tail2_4.cubeList.add(new ModelBox(tail2_4, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail2_5 = new ModelRenderer(this);
		tail2_5.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail2_4.addChild(tail2_5);
		setRotationAngle(tail2_5, 0.2618F, 0.0F, 0.0F);
		tail2_5.cubeList.add(new ModelBox(tail2_5, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));

		tail3_0 = new ModelRenderer(this);
		tail3_0.setRotationPoint(0.0F, 10.5F, 2.0F);
		bipedBody.addChild(tail3_0);
		setRotationAngle(tail3_0, -1.0472F, -1.0472F, 0.0F);
		tail3_0.cubeList.add(new ModelBox(tail3_0, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail3_1 = new ModelRenderer(this);
		tail3_1.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail3_0.addChild(tail3_1);
		setRotationAngle(tail3_1, 0.2618F, 0.0F, 0.0F);
		tail3_1.cubeList.add(new ModelBox(tail3_1, 17, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail3_2 = new ModelRenderer(this);
		tail3_2.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail3_1.addChild(tail3_2);
		setRotationAngle(tail3_2, 0.2618F, 0.0F, 0.0F);
		tail3_2.cubeList.add(new ModelBox(tail3_2, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));

		tail3_3 = new ModelRenderer(this);
		tail3_3.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail3_2.addChild(tail3_3);
		setRotationAngle(tail3_3, 0.2618F, 0.0F, 0.0F);
		tail3_3.cubeList.add(new ModelBox(tail3_3, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail3_4 = new ModelRenderer(this);
		tail3_4.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail3_3.addChild(tail3_4);
		setRotationAngle(tail3_4, 0.2618F, 0.0F, 0.0F);
		tail3_4.cubeList.add(new ModelBox(tail3_4, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail3_5 = new ModelRenderer(this);
		tail3_5.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail3_4.addChild(tail3_5);
		setRotationAngle(tail3_5, 0.2618F, 0.0F, 0.0F);
		tail3_5.cubeList.add(new ModelBox(tail3_5, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));

		tail4_0 = new ModelRenderer(this);
		tail4_0.setRotationPoint(0.0F, 10.5F, 2.0F);
		bipedBody.addChild(tail4_0);
		setRotationAngle(tail4_0, -1.0472F, 1.0472F, 0.0F);
		tail4_0.cubeList.add(new ModelBox(tail4_0, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail4_1 = new ModelRenderer(this);
		tail4_1.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail4_0.addChild(tail4_1);
		setRotationAngle(tail4_1, 0.2618F, 0.0F, 0.0F);
		tail4_1.cubeList.add(new ModelBox(tail4_1, 17, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail4_2 = new ModelRenderer(this);
		tail4_2.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail4_1.addChild(tail4_2);
		setRotationAngle(tail4_2, 0.2618F, 0.0F, 0.0F);
		tail4_2.cubeList.add(new ModelBox(tail4_2, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));

		tail4_3 = new ModelRenderer(this);
		tail4_3.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail4_2.addChild(tail4_3);
		setRotationAngle(tail4_3, 0.2618F, 0.0F, 0.0F);
		tail4_3.cubeList.add(new ModelBox(tail4_3, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail4_4 = new ModelRenderer(this);
		tail4_4.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail4_3.addChild(tail4_4);
		setRotationAngle(tail4_4, 0.2618F, 0.0F, 0.0F);
		tail4_4.cubeList.add(new ModelBox(tail4_4, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail4_5 = new ModelRenderer(this);
		tail4_5.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail4_4.addChild(tail4_5);
		setRotationAngle(tail4_5, 0.2618F, 0.0F, 0.0F);
		tail4_5.cubeList.add(new ModelBox(tail4_5, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));

		tail5_0 = new ModelRenderer(this);
		tail5_0.setRotationPoint(0.0F, 10.5F, 2.0F);
		bipedBody.addChild(tail5_0);
		setRotationAngle(tail5_0, -1.5708F, -0.2618F, 0.0F);
		tail5_0.cubeList.add(new ModelBox(tail5_0, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail5_1 = new ModelRenderer(this);
		tail5_1.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail5_0.addChild(tail5_1);
		setRotationAngle(tail5_1, 0.2618F, 0.0F, 0.0F);
		tail5_1.cubeList.add(new ModelBox(tail5_1, 17, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail5_2 = new ModelRenderer(this);
		tail5_2.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail5_1.addChild(tail5_2);
		setRotationAngle(tail5_2, 0.2618F, 0.0F, 0.0F);
		tail5_2.cubeList.add(new ModelBox(tail5_2, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));

		tail5_3 = new ModelRenderer(this);
		tail5_3.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail5_2.addChild(tail5_3);
		setRotationAngle(tail5_3, 0.2618F, 0.0F, 0.0F);
		tail5_3.cubeList.add(new ModelBox(tail5_3, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail5_4 = new ModelRenderer(this);
		tail5_4.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail5_3.addChild(tail5_4);
		setRotationAngle(tail5_4, 0.2618F, 0.0F, 0.0F);
		tail5_4.cubeList.add(new ModelBox(tail5_4, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail5_5 = new ModelRenderer(this);
		tail5_5.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail5_4.addChild(tail5_5);
		setRotationAngle(tail5_5, 0.2618F, 0.0F, 0.0F);
		tail5_5.cubeList.add(new ModelBox(tail5_5, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));

		tail6_0 = new ModelRenderer(this);
		tail6_0.setRotationPoint(0.0F, 10.5F, 2.0F);
		bipedBody.addChild(tail6_0);
		setRotationAngle(tail6_0, -1.5708F, 0.2618F, 0.0F);
		tail6_0.cubeList.add(new ModelBox(tail6_0, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail6_1 = new ModelRenderer(this);
		tail6_1.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail6_0.addChild(tail6_1);
		setRotationAngle(tail6_1, 0.2618F, 0.0F, 0.0F);
		tail6_1.cubeList.add(new ModelBox(tail6_1, 17, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail6_2 = new ModelRenderer(this);
		tail6_2.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail6_1.addChild(tail6_2);
		setRotationAngle(tail6_2, 0.2618F, 0.0F, 0.0F);
		tail6_2.cubeList.add(new ModelBox(tail6_2, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));

		tail6_3 = new ModelRenderer(this);
		tail6_3.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail6_2.addChild(tail6_3);
		setRotationAngle(tail6_3, 0.2618F, 0.0F, 0.0F);
		tail6_3.cubeList.add(new ModelBox(tail6_3, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail6_4 = new ModelRenderer(this);
		tail6_4.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail6_3.addChild(tail6_4);
		setRotationAngle(tail6_4, 0.2618F, 0.0F, 0.0F);
		tail6_4.cubeList.add(new ModelBox(tail6_4, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail6_5 = new ModelRenderer(this);
		tail6_5.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail6_4.addChild(tail6_5);
		setRotationAngle(tail6_5, 0.2618F, 0.0F, 0.0F);
		tail6_5.cubeList.add(new ModelBox(tail6_5, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));

		tail7_0 = new ModelRenderer(this);
		tail7_0.setRotationPoint(0.0F, 10.5F, 2.0F);
		bipedBody.addChild(tail7_0);
		setRotationAngle(tail7_0, -1.5708F, -0.7854F, 0.0F);
		tail7_0.cubeList.add(new ModelBox(tail7_0, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail7_1 = new ModelRenderer(this);
		tail7_1.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail7_0.addChild(tail7_1);
		setRotationAngle(tail7_1, 0.2618F, 0.0F, 0.0F);
		tail7_1.cubeList.add(new ModelBox(tail7_1, 17, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail7_2 = new ModelRenderer(this);
		tail7_2.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail7_1.addChild(tail7_2);
		setRotationAngle(tail7_2, 0.2618F, 0.0F, 0.0F);
		tail7_2.cubeList.add(new ModelBox(tail7_2, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));

		tail7_3 = new ModelRenderer(this);
		tail7_3.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail7_2.addChild(tail7_3);
		setRotationAngle(tail7_3, 0.2618F, 0.0F, 0.0F);
		tail7_3.cubeList.add(new ModelBox(tail7_3, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail7_4 = new ModelRenderer(this);
		tail7_4.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail7_3.addChild(tail7_4);
		setRotationAngle(tail7_4, 0.2618F, 0.0F, 0.0F);
		tail7_4.cubeList.add(new ModelBox(tail7_4, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail7_5 = new ModelRenderer(this);
		tail7_5.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail7_4.addChild(tail7_5);
		setRotationAngle(tail7_5, 0.2618F, 0.0F, 0.0F);
		tail7_5.cubeList.add(new ModelBox(tail7_5, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));

		tail8_0 = new ModelRenderer(this);
		tail8_0.setRotationPoint(0.0F, 10.5F, 2.0F);
		bipedBody.addChild(tail8_0);
		setRotationAngle(tail8_0, -1.5708F, 0.7854F, 0.0F);
		tail8_0.cubeList.add(new ModelBox(tail8_0, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail8_1 = new ModelRenderer(this);
		tail8_1.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail8_0.addChild(tail8_1);
		setRotationAngle(tail8_1, 0.2618F, 0.0F, 0.0F);
		tail8_1.cubeList.add(new ModelBox(tail8_1, 17, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail8_2 = new ModelRenderer(this);
		tail8_2.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail8_1.addChild(tail8_2);
		setRotationAngle(tail8_2, 0.2618F, 0.0F, 0.0F);
		tail8_2.cubeList.add(new ModelBox(tail8_2, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));

		tail8_3 = new ModelRenderer(this);
		tail8_3.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail8_2.addChild(tail8_3);
		setRotationAngle(tail8_3, 0.2618F, 0.0F, 0.0F);
		tail8_3.cubeList.add(new ModelBox(tail8_3, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));

		tail8_4 = new ModelRenderer(this);
		tail8_4.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail8_3.addChild(tail8_4);
		setRotationAngle(tail8_4, 0.2618F, 0.0F, 0.0F);
		tail8_4.cubeList.add(new ModelBox(tail8_4, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));

		tail8_5 = new ModelRenderer(this);
		tail8_5.setRotationPoint(0.0F, -5.0F, 0.0F);
		tail8_4.addChild(tail8_5);
		setRotationAngle(tail8_5, 0.2618F, 0.0F, 0.0F);
		tail8_5.cubeList.add(new ModelBox(tail8_5, 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
		bipedBody.render(f5);
		bipedRightArm.render(f5);
		bipedLeftArm.render(f5);
		bipedRightLeg.render(f5);
		bipedLeftLeg.render(f5);
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