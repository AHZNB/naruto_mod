// Made with Blockbench 4.3.1
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelNinjaArmor extends ModelBase {
	private final ModelRenderer bipedHead;
	private final ModelRenderer bipedHeadwear;
	private final ModelRenderer Leaf;
	private final ModelRenderer Sand;
	private final ModelRenderer bipedBody;
	private final ModelRenderer headbandWaist;
	private final ModelRenderer vest;
	private final ModelRenderer LeafVest;
	private final ModelRenderer SandVest;
	private final ModelRenderer StoneVest;
	private final ModelRenderer MistVest;
	private final ModelRenderer MistShoulder1_r1;
	private final ModelRenderer MistShoulder2_r1;
	private final ModelRenderer CloudVest;
	private final ModelRenderer AnbuVest;
	private final ModelRenderer bipedRightArm;
	private final ModelRenderer rightArmVestLayer;
	private final ModelRenderer sandRightShoulder;
	private final ModelRenderer headbandRightArm;
	private final ModelRenderer bipedLeftArm;
	private final ModelRenderer leftArmVestLayer;
	private final ModelRenderer sandLeftShoulder;
	private final ModelRenderer headbandLeftArm;
	private final ModelRenderer bipedRightLeg;
	private final ModelRenderer Headband_r1;
	private final ModelRenderer StoneCloth;
	private final ModelRenderer StoneVest_r1;
	private final ModelRenderer bipedLeftLeg;
	private final ModelRenderer Headband_r2;

	public ModelNinjaArmor() {
		textureWidth = 64;
		textureHeight = 64;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F, false));

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);

		Leaf = new ModelRenderer(this);
		Leaf.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedHeadwear.addChild(Leaf);
		Leaf.cubeList.add(new ModelBox(Leaf, 34, 8, -4.0F, -25.1F, -3.1F, 8, 1, 7, 0.7F, false));

		Sand = new ModelRenderer(this);
		Sand.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedHeadwear.addChild(Sand);
		Sand.cubeList.add(new ModelBox(Sand, 34, 8, -4.0F, -25.1F, -3.1F, 8, 1, 7, 0.7F, false));

		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.1F, false));

		headbandWaist = new ModelRenderer(this);
		headbandWaist.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedBody.addChild(headbandWaist);
		headbandWaist.cubeList.add(new ModelBox(headbandWaist, 4, 3, -4.0F, -19.0F, -2.0F, 8, 8, 4, 0.35F, false));

		vest = new ModelRenderer(this);
		vest.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedBody.addChild(vest);
		vest.cubeList.add(new ModelBox(vest, 40, 32, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.2F, false));
		vest.cubeList.add(new ModelBox(vest, 16, 32, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.35F, false));
		vest.cubeList.add(new ModelBox(vest, 52, 0, 0.1F, -15.7F, 1.2F, 4, 4, 2, -0.5F, false));

		LeafVest = new ModelRenderer(this);
		LeafVest.setRotationPoint(0.0F, 0.0F, 0.0F);
		vest.addChild(LeafVest);
		LeafVest.cubeList.add(new ModelBox(LeafVest, 26, 0, -4.3F, -21.5F, -3.1F, 4, 5, 3, -0.7F, false));
		LeafVest.cubeList.add(new ModelBox(LeafVest, 26, 0, 0.3F, -21.5F, -3.1F, 4, 5, 3, -0.7F, true));

		SandVest = new ModelRenderer(this);
		SandVest.setRotationPoint(0.0F, 0.0F, 0.0F);
		vest.addChild(SandVest);
		SandVest.cubeList.add(new ModelBox(SandVest, 26, 0, -4.3F, -18.9F, -3.1F, 4, 5, 3, -0.7F, false));
		SandVest.cubeList.add(new ModelBox(SandVest, 26, 0, 0.3F, -18.9F, -3.1F, 4, 5, 3, -0.7F, true));

		StoneVest = new ModelRenderer(this);
		StoneVest.setRotationPoint(0.0F, 0.0F, 0.0F);
		vest.addChild(StoneVest);

		MistVest = new ModelRenderer(this);
		MistVest.setRotationPoint(0.0F, 0.0F, 0.0F);
		vest.addChild(MistVest);
		MistVest.cubeList.add(new ModelBox(MistVest, 16, 32, -4.0F, -24.5F, -2.0F, 8, 12, 4, 0.3F, false));
		MistVest.cubeList.add(new ModelBox(MistVest, 48, 8, -4.0F, -13.7F, -2.275F, 8, 3, 0, 0.0F, false));
		MistVest.cubeList.add(new ModelBox(MistVest, 48, 8, -4.0F, -13.7F, 2.275F, 8, 3, 0, 0.0F, false));

		MistShoulder1_r1 = new ModelRenderer(this);
		MistShoulder1_r1.setRotationPoint(6.5687F, -23.7972F, 0.0F);
		MistVest.addChild(MistShoulder1_r1);
		setRotationAngle(MistShoulder1_r1, 0.0F, 0.0F, 0.0873F);
		MistShoulder1_r1.cubeList
				.add(new ModelBox(MistShoulder1_r1, 36, 0, -2.4F, -0.5F, -2.0F, 4, 1, 4, 0.31F, false));

		MistShoulder2_r1 = new ModelRenderer(this);
		MistShoulder2_r1.setRotationPoint(-6.5687F, -23.7972F, 0.0F);
		MistVest.addChild(MistShoulder2_r1);
		setRotationAngle(MistShoulder2_r1, 0.0F, 0.0F, -0.0873F);
		MistShoulder2_r1.cubeList.add(new ModelBox(MistShoulder2_r1, 36, 0, -1.6F, -0.5F, -2.0F, 4, 1, 4, 0.31F, true));

		CloudVest = new ModelRenderer(this);
		CloudVest.setRotationPoint(0.0F, 0.0F, 0.0F);
		vest.addChild(CloudVest);
		CloudVest.cubeList.add(new ModelBox(CloudVest, 40, 9, -4.0F, -13.0F, -2.0F, 8, 3, 4, 0.25F, true));

		AnbuVest = new ModelRenderer(this);
		AnbuVest.setRotationPoint(0.0F, 0.0F, 0.0F);
		vest.addChild(AnbuVest);

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.1F, false));

		rightArmVestLayer = new ModelRenderer(this);
		rightArmVestLayer.setRotationPoint(5.0F, 22.0F, 0.0F);
		bipedRightArm.addChild(rightArmVestLayer);
		rightArmVestLayer.cubeList
				.add(new ModelBox(rightArmVestLayer, 48, 48, -8.0F, -24.0F, -2.0F, 4, 12, 4, 0.3F, true));

		sandRightShoulder = new ModelRenderer(this);
		sandRightShoulder.setRotationPoint(-7.0F, -24.5F, 0.0F);
		rightArmVestLayer.addChild(sandRightShoulder);
		setRotationAngle(sandRightShoulder, 0.0F, 0.0F, -0.3054F);
		sandRightShoulder.cubeList
				.add(new ModelBox(sandRightShoulder, 36, 0, -1.8F, 0.3F, -2.0F, 4, 1, 4, 0.31F, true));

		headbandRightArm = new ModelRenderer(this);
		headbandRightArm.setRotationPoint(-5.0F, -6.0F, 0.0F);
		bipedRightArm.addChild(headbandRightArm);
		setRotationAngle(headbandRightArm, 0.0F, 1.5708F, 0.0F);
		headbandRightArm.cubeList
				.add(new ModelBox(headbandRightArm, 0, 0, -4.0F, 2.825F, -0.05F, 8, 8, 8, -1.65F, false));

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.1F, true));

		leftArmVestLayer = new ModelRenderer(this);
		leftArmVestLayer.setRotationPoint(1.0F, 4.0F, 0.0F);
		bipedLeftArm.addChild(leftArmVestLayer);
		leftArmVestLayer.cubeList
				.add(new ModelBox(leftArmVestLayer, 48, 48, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.3F, false));

		sandLeftShoulder = new ModelRenderer(this);
		sandLeftShoulder.setRotationPoint(1.0F, -6.5F, 0.0F);
		leftArmVestLayer.addChild(sandLeftShoulder);
		setRotationAngle(sandLeftShoulder, 0.0F, 0.0F, 0.3054F);
		sandLeftShoulder.cubeList.add(new ModelBox(sandLeftShoulder, 36, 0, -2.2F, 0.3F, -2.0F, 4, 1, 4, 0.31F, false));

		headbandLeftArm = new ModelRenderer(this);
		headbandLeftArm.setRotationPoint(5.0F, -6.0F, 0.0F);
		bipedLeftArm.addChild(headbandLeftArm);
		setRotationAngle(headbandLeftArm, 0.0F, -1.5708F, 0.0F);
		headbandLeftArm.cubeList
				.add(new ModelBox(headbandLeftArm, 0, 0, -4.0F, 2.825F, -0.05F, 8, 8, 8, -1.65F, false));

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 31, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F, false));
		bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 0, -2.6F, 1.0F, -1.0F, 1, 4, 2, 0.0F, false));

		Headband_r1 = new ModelRenderer(this);
		Headband_r1.setRotationPoint(-8.1F, -16.0F, 0.0F);
		bipedRightLeg.addChild(Headband_r1);
		setRotationAngle(Headband_r1, 0.0F, 1.5708F, 0.0F);
		Headband_r1.cubeList.add(new ModelBox(Headband_r1, 0, 0, -4.0F, 13.225F, 3.95F, 8, 8, 8, -1.65F, false));

		StoneCloth = new ModelRenderer(this);
		StoneCloth.setRotationPoint(0.0F, 6.0F, 0.0F);
		bipedRightLeg.addChild(StoneCloth);

		StoneVest_r1 = new ModelRenderer(this);
		StoneVest_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		StoneCloth.addChild(StoneVest_r1);
		setRotationAngle(StoneVest_r1, 0.0F, 0.0F, 0.1745F);
		StoneVest_r1.cubeList.add(new ModelBox(StoneVest_r1, 36, 0, -3.2F, -6.8F, -2.0F, 4, 7, 4, 0.25F, false));

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));
		bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F, true));

		Headband_r2 = new ModelRenderer(this);
		Headband_r2.setRotationPoint(8.1F, -16.0F, 0.0F);
		bipedLeftLeg.addChild(Headband_r2);
		setRotationAngle(Headband_r2, 0.0F, -1.5708F, 0.0F);
		Headband_r2.cubeList.add(new ModelBox(Headband_r2, 0, 0, -4.0F, 13.225F, 3.95F, 8, 8, 8, -1.65F, false));
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