// Made with Blockbench 3.9.2
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelWoodGolem extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer HatLayer_r1;
	private final ModelRenderer HatLayer_r2;
	private final ModelRenderer bone45;
	private final ModelRenderer HatLayer_r3;
	private final ModelRenderer bone47;
	private final ModelRenderer HatLayer_r4;
	private final ModelRenderer bone48;
	private final ModelRenderer HatLayer_r5;
	private final ModelRenderer bone46;
	private final ModelRenderer HatLayer_r6;
	private final ModelRenderer bone44;
	private final ModelRenderer HatLayer_r7;
	private final ModelRenderer bone43;
	private final ModelRenderer HatLayer_r8;
	private final ModelRenderer bipedHeadWear;
	private final ModelRenderer bipedBody;
	private final ModelRenderer dragon;
	private final ModelRenderer cube_r1;
	private final ModelRenderer dragonhorns;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer bone10;
	private final ModelRenderer bone11;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer bone14;
	private final ModelRenderer bone15;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer bone18;
	private final ModelRenderer bone19;
	private final ModelRenderer bone20;
	private final ModelRenderer bone21;
	private final ModelRenderer bone22;
	private final ModelRenderer bone23;
	private final ModelRenderer bone24;
	private final ModelRenderer bone25;
	private final ModelRenderer bone26;
	private final ModelRenderer bone27;
	private final ModelRenderer bone28;
	private final ModelRenderer bone29;
	private final ModelRenderer bone30;
	private final ModelRenderer bone31;
	private final ModelRenderer bone32;
	private final ModelRenderer bone33;
	private final ModelRenderer bone34;
	private final ModelRenderer bone35;
	private final ModelRenderer bone36;
	private final ModelRenderer bone37;
	private final ModelRenderer bone38;
	private final ModelRenderer bone39;
	private final ModelRenderer bone40;
	private final ModelRenderer bone41;
	private final ModelRenderer bone42;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer bone7;
	private final ModelRenderer bone8;

	public ModelWoodGolem() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 16, -4.0F, -8.5F, -4.0F, 8, 8, 8, 0.0F, false));

		HatLayer_r1 = new ModelRenderer(this);
		HatLayer_r1.setRotationPoint(0.0F, -5.0119F, 5.1599F);
		bipedHead.addChild(HatLayer_r1);
		setRotationAngle(HatLayer_r1, 1.0036F, 3.1416F, 0.0F);
		HatLayer_r1.cubeList.add(new ModelBox(HatLayer_r1, 46, 32, -4.0F, -1.25F, -1.625F, 8, 5, 0, 0.0F, false));

		HatLayer_r2 = new ModelRenderer(this);
		HatLayer_r2.setRotationPoint(0.0F, -10.0978F, -1.4304F);
		bipedHead.addChild(HatLayer_r2);
		setRotationAngle(HatLayer_r2, -0.4363F, 0.0F, 0.0F);
		HatLayer_r2.cubeList.add(new ModelBox(HatLayer_r2, 46, 32, -4.0F, -2.45F, -1.725F, 8, 5, 0, 0.0F, false));

		bone45 = new ModelRenderer(this);
		bone45.setRotationPoint(-3.7F, -10.7815F, -3.0149F);
		bipedHead.addChild(bone45);
		setRotationAngle(bone45, -1.0036F, 0.7854F, -1.1781F);

		HatLayer_r3 = new ModelRenderer(this);
		HatLayer_r3.setRotationPoint(-1.4091F, 0.2641F, -0.1594F);
		bone45.addChild(HatLayer_r3);
		setRotationAngle(HatLayer_r3, 0.1309F, -0.5236F, 0.0F);
		HatLayer_r3.cubeList.add(new ModelBox(HatLayer_r3, 59, 44, -0.725F, -1.775F, -0.625F, 2, 5, 0, 0.0F, false));

		bone47 = new ModelRenderer(this);
		bone47.setRotationPoint(-3.7F, -10.7815F, -3.0149F);
		bipedHead.addChild(bone47);
		setRotationAngle(bone47, -1.0036F, 0.7854F, -1.1781F);

		HatLayer_r4 = new ModelRenderer(this);
		HatLayer_r4.setRotationPoint(-9.8033F, 0.1704F, 2.2299F);
		bone47.addChild(HatLayer_r4);
		setRotationAngle(HatLayer_r4, 0.2618F, 1.1345F, 0.0F);
		HatLayer_r4.cubeList.add(new ModelBox(HatLayer_r4, 59, 44, -0.9F, -1.45F, -0.8F, 2, 5, 0, 0.0F, false));

		bone48 = new ModelRenderer(this);
		bone48.setRotationPoint(3.7F, -10.7815F, -3.0149F);
		bipedHead.addChild(bone48);
		setRotationAngle(bone48, -1.0036F, -0.7854F, 1.1781F);

		HatLayer_r5 = new ModelRenderer(this);
		HatLayer_r5.setRotationPoint(9.8033F, 0.1704F, 2.2299F);
		bone48.addChild(HatLayer_r5);
		setRotationAngle(HatLayer_r5, 0.2618F, -1.1345F, 0.0F);
		HatLayer_r5.cubeList.add(new ModelBox(HatLayer_r5, 59, 44, -1.25F, -1.45F, -1.175F, 2, 5, 0, 0.0F, true));

		bone46 = new ModelRenderer(this);
		bone46.setRotationPoint(3.7F, -10.7815F, -3.0149F);
		bipedHead.addChild(bone46);
		setRotationAngle(bone46, -1.0036F, -0.7854F, 1.1781F);

		HatLayer_r6 = new ModelRenderer(this);
		HatLayer_r6.setRotationPoint(1.4091F, 0.2641F, -0.1594F);
		bone46.addChild(HatLayer_r6);
		setRotationAngle(HatLayer_r6, 0.1309F, 0.5236F, 0.0F);
		HatLayer_r6.cubeList.add(new ModelBox(HatLayer_r6, 59, 44, -1.275F, -1.775F, -0.625F, 2, 5, 0, 0.0F, true));

		bone44 = new ModelRenderer(this);
		bone44.setRotationPoint(-4.7479F, -9.2565F, 0.5796F);
		bipedHead.addChild(bone44);
		setRotationAngle(bone44, 0.7418F, 1.309F, 0.0F);

		HatLayer_r7 = new ModelRenderer(this);
		HatLayer_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone44.addChild(HatLayer_r7);
		setRotationAngle(HatLayer_r7, -0.3054F, -0.3491F, -0.7854F);
		HatLayer_r7.cubeList.add(new ModelBox(HatLayer_r7, 13, 0, -6.425F, -1.225F, -0.8F, 9, 5, 0, 0.0F, true));

		bone43 = new ModelRenderer(this);
		bone43.setRotationPoint(4.7479F, -9.2565F, 0.5796F);
		bipedHead.addChild(bone43);
		setRotationAngle(bone43, 0.7418F, -1.309F, 0.0F);

		HatLayer_r8 = new ModelRenderer(this);
		HatLayer_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone43.addChild(HatLayer_r8);
		setRotationAngle(HatLayer_r8, -0.3054F, 0.3491F, 0.7854F);
		HatLayer_r8.cubeList.add(new ModelBox(HatLayer_r8, 13, 0, -2.575F, -1.225F, -0.8F, 9, 5, 0, 0.0F, false));

		bipedHeadWear = new ModelRenderer(this);
		bipedHeadWear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHeadWear.cubeList
				.add(new ModelBox(bipedHeadWear, 42, 46, -4.025F, -6.925F, -4.005F, 8, 2, 0, 0.0F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 32, -4.0F, -0.5F, -2.0F, 8, 12, 4, 0.5F, false));
		bipedBody.cubeList.add(new ModelBox(bipedBody, 6, 6, -4.0F, 4.25F, -3.425F, 8, 7, 2, 0.0F, false));

		dragon = new ModelRenderer(this);
		dragon.setRotationPoint(-2.0F, 15.0F, 5.0F);
		bipedBody.addChild(dragon);
		dragon.cubeList.add(new ModelBox(dragon, 44, 19, -2.0F, -1.5F, -2.0F, 4, 4, 4, 0.0F, false));
		dragon.cubeList.add(new ModelBox(dragon, 44, 19, -2.0F, 0.875F, -2.0F, 4, 4, 4, -0.5F, false));
		dragon.cubeList.add(new ModelBox(dragon, 44, 19, -2.0F, 2.875F, -2.0F, 4, 4, 4, -1.0F, false));

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, 6.3F, 0.0F);
		dragon.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 0.0F, 1.5708F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 44, 19, -3.05F, -2.0F, -2.0F, 4, 4, 4, -1.2F, false));

		dragonhorns = new ModelRenderer(this);
		dragonhorns.setRotationPoint(2.0F, 9.0F, -5.0F);
		dragon.addChild(dragonhorns);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-7.05F, -27.1F, -5.075F);
		dragonhorns.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.3491F, 0.0F, -0.7418F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 9, -0.325F, 1.75F, -0.25F, 1, 1, 1, 0.0F, false));
		cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 13, -0.825F, 0.0F, -0.5F, 2, 2, 1, 0.0F, false));

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-9.2942F, -31.752F, 1.5839F);
		dragonhorns.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.2618F, 0.3927F, -0.4363F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 4, -0.5F, -1.5F, -0.5F, 1, 3, 1, 0.0F, true));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-8.7301F, -31.566F, 0.6281F);
		dragonhorns.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0873F, 0.3054F, -0.4363F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 0, -0.3F, -1.0F, -0.85F, 1, 2, 1, 0.0F, true));

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-10.2471F, -30.4593F, 1.5035F);
		dragonhorns.addChild(cube_r5);
		setRotationAngle(cube_r5, -0.3054F, -0.3054F, -0.6981F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 4, -0.5F, -2.0F, -0.45F, 1, 3, 1, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(-10.3259F, -29.7405F, 0.5F);
		dragonhorns.addChild(cube_r6);
		setRotationAngle(cube_r6, 0.0F, -0.3054F, -0.6981F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.0F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.0F, -2.0F, 0.0F);
		dragon.addChild(bone10);
		setRotationAngle(bone10, 0.2618F, 0.0F, -0.2618F);
		bone10.cubeList.add(new ModelBox(bone10, 44, 19, -1.8706F, -1.4665F, -1.875F, 4, 4, 4, 0.0F, false));

		bone11 = new ModelRenderer(this);
		bone11.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone10.addChild(bone11);
		setRotationAngle(bone11, 0.2618F, 0.0F, -0.2618F);
		bone11.cubeList.add(new ModelBox(bone11, 44, 19, -1.7543F, -1.3706F, -1.7713F, 4, 4, 4, 0.0F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone11.addChild(bone12);
		setRotationAngle(bone12, 0.2618F, 0.0F, -0.2618F);
		bone12.cubeList.add(new ModelBox(bone12, 44, 19, -1.6667F, -1.2251F, -1.7029F, 4, 4, 4, 0.0F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone12.addChild(bone13);
		setRotationAngle(bone13, 0.2182F, 0.0F, 0.0F);
		bone13.cubeList.add(new ModelBox(bone13, 44, 19, -1.6667F, -1.1555F, -1.6612F, 4, 4, 4, 0.0F, false));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone13.addChild(bone14);
		setRotationAngle(bone14, 0.2618F, 0.0F, 0.2618F);
		bone14.cubeList.add(new ModelBox(bone14, 44, 19, -1.7183F, -1.1407F, -1.6116F, 4, 4, 4, 0.0F, false));

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone14.addChild(bone15);
		setRotationAngle(bone15, 0.0F, 0.0F, 0.3491F);
		bone15.cubeList.add(new ModelBox(bone15, 44, 19, -1.7834F, -1.2285F, -1.6116F, 4, 4, 4, 0.0F, false));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone15.addChild(bone16);
		setRotationAngle(bone16, 0.0F, 0.0F, 0.3491F);
		bone16.cubeList.add(new ModelBox(bone16, 44, 19, -1.8747F, -1.2888F, -1.6116F, 4, 4, 4, 0.0F, false));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone16.addChild(bone17);
		setRotationAngle(bone17, 0.0F, 0.0F, 0.3491F);
		bone17.cubeList.add(new ModelBox(bone17, 44, 19, -1.981F, -1.3143F, -1.6116F, 4, 4, 4, 0.0F, false));

		bone18 = new ModelRenderer(this);
		bone18.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone17.addChild(bone18);
		setRotationAngle(bone18, 0.2618F, 0.0F, 0.3491F);
		bone18.cubeList.add(new ModelBox(bone18, 44, 19, -2.0896F, -1.191F, -1.5467F, 4, 4, 4, 0.0F, false));

		bone19 = new ModelRenderer(this);
		bone19.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone18.addChild(bone19);
		setRotationAngle(bone19, 0.0F, 0.0F, 0.2618F);
		bone19.cubeList.add(new ModelBox(bone19, 44, 19, -2.136F, -1.1613F, -1.5467F, 4, 4, 4, 0.0F, false));

		bone20 = new ModelRenderer(this);
		bone20.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone19.addChild(bone20);
		bone20.cubeList.add(new ModelBox(bone20, 44, 19, -2.136F, -1.1613F, -1.5467F, 4, 4, 4, 0.0F, false));

		bone21 = new ModelRenderer(this);
		bone21.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone20.addChild(bone21);
		bone21.cubeList.add(new ModelBox(bone21, 44, 19, -2.136F, -1.1613F, -1.5467F, 4, 4, 4, 0.0F, false));

		bone22 = new ModelRenderer(this);
		bone22.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone21.addChild(bone22);
		setRotationAngle(bone22, -0.2618F, 0.0F, 0.2618F);
		bone22.cubeList.add(new ModelBox(bone22, 44, 19, -2.1731F, -1.2338F, -1.5934F, 4, 4, 4, 0.0F, false));

		bone23 = new ModelRenderer(this);
		bone23.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone22.addChild(bone23);
		setRotationAngle(bone23, -0.2618F, 0.0F, 0.2618F);
		bone23.cubeList.add(new ModelBox(bone23, 44, 19, -2.2278F, -1.2801F, -1.6541F, 4, 4, 4, 0.0F, false));

		bone24 = new ModelRenderer(this);
		bone24.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone23.addChild(bone24);
		setRotationAngle(bone24, -0.2618F, 0.0F, 0.2618F);
		bone24.cubeList.add(new ModelBox(bone24, 44, 19, -2.2925F, -1.2939F, -1.7206F, 4, 4, 4, 0.0F, false));

		bone25 = new ModelRenderer(this);
		bone25.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone24.addChild(bone25);
		setRotationAngle(bone25, -0.2618F, 0.0F, 0.0F);
		bone25.cubeList.add(new ModelBox(bone25, 44, 19, -2.2925F, -1.3562F, -1.8062F, 4, 4, 4, 0.0F, false));

		bone26 = new ModelRenderer(this);
		bone26.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone25.addChild(bone26);
		setRotationAngle(bone26, 0.0F, 0.0F, 0.2618F);
		bone26.cubeList.add(new ModelBox(bone26, 44, 19, -2.3747F, -1.2684F, -1.8062F, 4, 4, 4, 0.0F, false));

		bone27 = new ModelRenderer(this);
		bone27.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone26.addChild(bone27);
		setRotationAngle(bone27, 0.0F, 0.0F, 0.2618F);
		bone27.cubeList.add(new ModelBox(bone27, 44, 19, -2.4314F, -1.1623F, -1.8062F, 4, 4, 4, 0.0F, false));

		bone28 = new ModelRenderer(this);
		bone28.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone27.addChild(bone28);
		setRotationAngle(bone28, 0.0F, 0.0F, 0.2618F);
		bone28.cubeList.add(new ModelBox(bone28, 44, 19, -2.4587F, -1.0451F, -1.8062F, 4, 4, 4, 0.0F, false));

		bone29 = new ModelRenderer(this);
		bone29.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone28.addChild(bone29);
		setRotationAngle(bone29, -0.2618F, 0.0F, -0.0873F);
		bone29.cubeList.add(new ModelBox(bone29, 44, 19, -2.453F, -1.1321F, -1.8348F, 4, 4, 4, 0.0F, false));

		bone30 = new ModelRenderer(this);
		bone30.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone29.addChild(bone30);
		setRotationAngle(bone30, -0.2618F, 0.0F, 0.1745F);
		bone30.cubeList.add(new ModelBox(bone30, 44, 19, -2.4691F, -1.0925F, -1.8537F, 4, 4, 4, 0.0F, false));

		bone31 = new ModelRenderer(this);
		bone31.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone30.addChild(bone31);
		setRotationAngle(bone31, -0.2618F, 0.0F, 0.1745F);
		bone31.cubeList.add(new ModelBox(bone31, 44, 19, -2.478F, -1.0471F, -1.8612F, 4, 4, 4, 0.0F, false));

		bone32 = new ModelRenderer(this);
		bone32.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone31.addChild(bone32);
		setRotationAngle(bone32, -0.2618F, 0.0F, 0.1745F);
		bone32.cubeList.add(new ModelBox(bone32, 44, 19, -2.479F, -1.0006F, -1.8565F, 4, 4, 4, 0.0F, false));

		bone33 = new ModelRenderer(this);
		bone33.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone32.addChild(bone33);
		setRotationAngle(bone33, -0.2618F, 0.0F, 0.1745F);
		bone33.cubeList.add(new ModelBox(bone33, 44, 19, -2.4718F, -0.9573F, -1.84F, 4, 4, 4, 0.0F, false));

		bone34 = new ModelRenderer(this);
		bone34.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone33.addChild(bone34);
		setRotationAngle(bone34, -0.2618F, 0.0F, 0.0F);
		bone34.cubeList.add(new ModelBox(bone34, 44, 19, -2.4718F, -1.0002F, -1.8344F, 4, 4, 4, 0.0F, false));

		bone35 = new ModelRenderer(this);
		bone35.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone34.addChild(bone35);
		setRotationAngle(bone35, -0.2618F, 0.0F, 0.0F);
		bone35.cubeList.add(new ModelBox(bone35, 44, 19, -2.4718F, -1.0431F, -1.8401F, 4, 4, 4, 0.0F, false));

		bone36 = new ModelRenderer(this);
		bone36.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone35.addChild(bone36);
		setRotationAngle(bone36, 0.0F, 0.0F, 0.2618F);
		bone36.cubeList.add(new ModelBox(bone36, 44, 19, -2.4668F, -0.9195F, -1.8401F, 4, 4, 4, 0.0F, false));

		bone37 = new ModelRenderer(this);
		bone37.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone36.addChild(bone37);
		setRotationAngle(bone37, -0.2618F, 0.0F, 0.2618F);
		bone37.cubeList.add(new ModelBox(bone37, 44, 19, -2.4301F, -0.8496F, -1.7941F, 4, 4, 4, 0.0F, false));

		bone38 = new ModelRenderer(this);
		bone38.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone37.addChild(bone38);
		setRotationAngle(bone38, -0.2618F, 0.0F, 0.2618F);
		bone38.cubeList.add(new ModelBox(bone38, 44, 19, -2.3765F, -0.8054F, -1.7347F, 4, 4, 4, 0.0F, false));

		bone39 = new ModelRenderer(this);
		bone39.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone38.addChild(bone39);
		setRotationAngle(bone39, -0.2618F, 0.0F, 0.2618F);
		bone39.cubeList.add(new ModelBox(bone39, 48, 0, -2.3133F, -0.793F, -1.6699F, 4, 4, 4, -0.2F, false));

		bone40 = new ModelRenderer(this);
		bone40.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone39.addChild(bone40);
		bone40.cubeList.add(new ModelBox(bone40, 0, 48, -2.3133F, -0.793F, -1.6699F, 4, 4, 4, -0.4F, false));
		bone40.cubeList.add(new ModelBox(bone40, 42, 57, -2.3133F, -0.793F, 1.3401F, 4, 4, 1, -0.4F, true));
		bone40.cubeList.add(new ModelBox(bone40, 42, 51, -2.3133F, -0.793F, -1.68F, 4, 4, 1, -0.4F, false));

		bone41 = new ModelRenderer(this);
		bone41.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone40.addChild(bone41);
		bone41.cubeList.add(new ModelBox(bone41, 0, 56, -2.3133F, -0.793F, -1.6699F, 4, 4, 4, -0.6F, false));

		bone42 = new ModelRenderer(this);
		bone42.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone41.addChild(bone42);
		bone42.cubeList.add(new ModelBox(bone42, 16, 56, -2.3133F, -0.793F, -1.6699F, 4, 4, 4, -0.8F, false));

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-6.0F, 2.0F, 0.0F);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedRightArm.addChild(bone);
		setRotationAngle(bone, 0.0F, -0.5236F, 0.2618F);
		bone.cubeList.add(new ModelBox(bone, 32, 0, -3.1121F, -2.483F, -1.9353F, 4, 8, 4, 0.0F, false));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-1.0F, 5.0F, 0.0F);
		bone.addChild(bone2);
		setRotationAngle(bone2, -0.5236F, 0.0F, 0.0F);
		bone2.cubeList.add(new ModelBox(bone2, 28, 12, -2.1121F, -0.4506F, -2.1854F, 4, 8, 4, 0.0F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(6.0F, 2.0F, 0.0F);

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedLeftArm.addChild(bone5);
		setRotationAngle(bone5, 0.0F, 0.5236F, -0.2618F);
		bone5.cubeList.add(new ModelBox(bone5, 32, 0, -0.8879F, -2.483F, -1.9353F, 4, 8, 4, 0.0F, true));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(1.0F, 5.0F, 0.0F);
		bone5.addChild(bone6);
		setRotationAngle(bone6, -0.5236F, 0.0F, 0.0F);
		bone6.cubeList.add(new ModelBox(bone6, 28, 12, -1.8879F, -0.4506F, -2.1854F, 4, 8, 4, 0.0F, true));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-0.1F, -1.0F, 0.0F);
		bipedRightLeg.addChild(bone3);
		setRotationAngle(bone3, -0.1745F, 0.3491F, 0.0F);
		bone3.cubeList.add(new ModelBox(bone3, 20, 44, -1.9F, 0.5076F, -2.0868F, 4, 6, 4, 0.5F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, 8.0F, -2.0F);
		bone3.addChild(bone4);
		setRotationAngle(bone4, 0.2618F, 0.0F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 44, 8, -1.9F, -1.4981F, 0.0436F, 4, 7, 4, 0.2F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.1F, -1.0F, 0.0F);
		bipedLeftLeg.addChild(bone7);
		setRotationAngle(bone7, -0.1745F, -0.3491F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 20, 44, -2.1F, 0.5076F, -2.0868F, 4, 6, 4, 0.5F, true));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(0.0F, 8.0F, -2.0F);
		bone7.addChild(bone8);
		setRotationAngle(bone8, 0.2618F, 0.0F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 44, 8, -2.1F, -1.4981F, 0.0436F, 4, 7, 4, 0.2F, true));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
		bipedHeadWear.render(f5);
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