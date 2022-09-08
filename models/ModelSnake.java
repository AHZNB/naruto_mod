// Made with Blockbench 4.0.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports

public static class ModelSnake extends ModelBase {
	private final ModelRenderer head1;
	private final ModelRenderer bone57;
	private final ModelRenderer bone58;
	private final ModelRenderer bone59;
	private final ModelRenderer bone60;
	private final ModelRenderer bone61;
	private final ModelRenderer bone62;
	private final ModelRenderer bone63;
	private final ModelRenderer bone64;
	private final ModelRenderer bone65;
	private final ModelRenderer bone66;
	private final ModelRenderer bone67;
	private final ModelRenderer jaw1;
	private final ModelRenderer bone68;
	private final ModelRenderer bone69;
	private final ModelRenderer bone70;
	private final ModelRenderer horns1;
	private final ModelRenderer bone71;
	private final ModelRenderer bone72;
	private final ModelRenderer bone73;
	private final ModelRenderer bone74;
	private final ModelRenderer segment1_0;
	private final ModelRenderer segment1_1;
	private final ModelRenderer segment1_2;
	private final ModelRenderer segment1_3;
	private final ModelRenderer segment1_4;
	private final ModelRenderer segment_0;
	private final ModelRenderer segment_1;
	private final ModelRenderer segment_2;
	private final ModelRenderer segment_3;
	private final ModelRenderer segment_4;
	private final ModelRenderer head;
	private final ModelRenderer bone10;
	private final ModelRenderer bone12;
	private final ModelRenderer bone13;
	private final ModelRenderer bone14;
	private final ModelRenderer bone15;
	private final ModelRenderer bone16;
	private final ModelRenderer bone17;
	private final ModelRenderer bone18;
	private final ModelRenderer bone27;
	private final ModelRenderer bone28;
	private final ModelRenderer bone29;
	private final ModelRenderer jaw;
	private final ModelRenderer bone30;
	private final ModelRenderer bone31;
	private final ModelRenderer bone32;
	private final ModelRenderer horns;
	private final ModelRenderer bone33;
	private final ModelRenderer bone34;
	private final ModelRenderer bone35;
	private final ModelRenderer bone36;
	private final ModelRenderer segment_5;
	private final ModelRenderer segment_6;
	private final ModelRenderer segment_7;
	private final ModelRenderer segment_8;
	private final ModelRenderer segment_9;
	private final ModelRenderer segment_10;
	private final ModelRenderer segment_11;
	private final ModelRenderer segment_12;
	private final ModelRenderer segment_13;
	private final ModelRenderer segment_14;
	private final ModelRenderer segment_15;
	private final ModelRenderer segment_16;
	private final ModelRenderer segment_17;
	private final ModelRenderer segment_18;
	private final ModelRenderer segment_19;
	private final ModelRenderer segment_20;

	public ModelSnake() {
		textureWidth = 32;
		textureHeight = 32;

		head1 = new ModelRenderer(this);
		head1.setRotationPoint(0.0F, -1.5F, -21.0F);
		head1.cubeList.add(new ModelBox(head1, 16, 0, -2.5F, -2.5F, 0.0F, 5, 4, 1, 0.1F, false));

		bone57 = new ModelRenderer(this);
		bone57.setRotationPoint(1.4F, -1.2F, -5.35F);
		head1.addChild(bone57);
		setRotationAngle(bone57, 0.7854F, 0.0F, 0.6109F);
		bone57.cubeList.add(new ModelBox(bone57, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));

		bone58 = new ModelRenderer(this);
		bone58.setRotationPoint(0.0F, -0.5F, 3.0F);
		bone57.addChild(bone58);
		setRotationAngle(bone58, -0.9599F, 0.0F, 0.0F);
		bone58.cubeList.add(new ModelBox(bone58, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));

		bone59 = new ModelRenderer(this);
		bone59.setRotationPoint(-1.4F, -1.2F, -5.35F);
		head1.addChild(bone59);
		setRotationAngle(bone59, 0.7854F, 0.0F, -0.6109F);
		bone59.cubeList.add(new ModelBox(bone59, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));

		bone60 = new ModelRenderer(this);
		bone60.setRotationPoint(0.0F, -0.5F, 3.0F);
		bone59.addChild(bone60);
		setRotationAngle(bone60, -1.0036F, 0.0F, 0.0F);
		bone60.cubeList.add(new ModelBox(bone60, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));

		bone61 = new ModelRenderer(this);
		bone61.setRotationPoint(0.0F, -1.5F, 0.0F);
		head1.addChild(bone61);
		setRotationAngle(bone61, 0.0436F, 0.0873F, 0.0F);
		bone61.cubeList.add(new ModelBox(bone61, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));

		bone62 = new ModelRenderer(this);
		bone62.setRotationPoint(0.0F, -1.5F, 0.0F);
		head1.addChild(bone62);
		setRotationAngle(bone62, 0.0436F, -0.0873F, 0.0F);
		bone62.cubeList.add(new ModelBox(bone62, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));

		bone63 = new ModelRenderer(this);
		bone63.setRotationPoint(-0.15F, -1.6F, -2.5F);
		head1.addChild(bone63);
		setRotationAngle(bone63, 0.5236F, 0.2618F, 0.0F);
		bone63.cubeList.add(new ModelBox(bone63, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));

		bone64 = new ModelRenderer(this);
		bone64.setRotationPoint(0.15F, -1.6F, -2.5F);
		head1.addChild(bone64);
		setRotationAngle(bone64, 0.5236F, -0.2618F, 0.0F);
		bone64.cubeList.add(new ModelBox(bone64, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));

		bone65 = new ModelRenderer(this);
		bone65.setRotationPoint(2.6F, -0.4F, -3.95F);
		head1.addChild(bone65);
		setRotationAngle(bone65, 0.0F, 0.2618F, 0.0F);
		bone65.cubeList.add(new ModelBox(bone65, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
		bone65.cubeList.add(new ModelBox(bone65, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));

		bone66 = new ModelRenderer(this);
		bone66.setRotationPoint(-2.6F, -0.4F, -3.95F);
		head1.addChild(bone66);
		setRotationAngle(bone66, 0.0F, -0.2618F, 0.0F);
		bone66.cubeList.add(new ModelBox(bone66, 10, 19, 0.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
		bone66.cubeList.add(new ModelBox(bone66, 0, 19, 0.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));

		bone67 = new ModelRenderer(this);
		bone67.setRotationPoint(1.6F, 1.3F, -5.95F);
		head1.addChild(bone67);
		bone67.cubeList.add(new ModelBox(bone67, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
		bone67.cubeList.add(new ModelBox(bone67, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));

		jaw1 = new ModelRenderer(this);
		jaw1.setRotationPoint(0.0F, 0.0F, 0.0F);
		head1.addChild(jaw1);
		setRotationAngle(jaw1, 0.5236F, 0.0F, 0.0F);

		bone68 = new ModelRenderer(this);
		bone68.setRotationPoint(3.0F, 0.9F, 0.0F);
		jaw1.addChild(bone68);
		setRotationAngle(bone68, 0.0F, 0.2182F, 0.0F);
		bone68.cubeList.add(new ModelBox(bone68, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));

		bone69 = new ModelRenderer(this);
		bone69.setRotationPoint(-3.0F, 0.9F, 0.0F);
		jaw1.addChild(bone69);
		setRotationAngle(bone69, 0.0F, -0.2182F, 0.0F);
		bone69.cubeList.add(new ModelBox(bone69, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));

		bone70 = new ModelRenderer(this);
		bone70.setRotationPoint(0.0F, 0.3F, 0.0F);
		jaw1.addChild(bone70);
		bone70.cubeList.add(new ModelBox(bone70, 0, 0, 1.2F, -1.0F, -6.0F, 0, 1, 1, 0.1F, false));
		bone70.cubeList.add(new ModelBox(bone70, 0, 0, -1.2F, -1.0F, -6.0F, 0, 1, 1, 0.1F, true));

		horns1 = new ModelRenderer(this);
		horns1.setRotationPoint(0.0F, 0.0F, 0.0F);
		head1.addChild(horns1);

		bone71 = new ModelRenderer(this);
		bone71.setRotationPoint(-2.3F, -2.5F, -1.6F);
		horns1.addChild(bone71);
		setRotationAngle(bone71, 0.5236F, -0.6981F, 0.0F);
		bone71.cubeList.add(new ModelBox(bone71, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
		bone71.cubeList.add(new ModelBox(bone71, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
		bone71.cubeList.add(new ModelBox(bone71, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
		bone71.cubeList.add(new ModelBox(bone71, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
		bone71.cubeList.add(new ModelBox(bone71, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));

		bone72 = new ModelRenderer(this);
		bone72.setRotationPoint(-1.2F, -2.5F, -1.2F);
		horns1.addChild(bone72);
		setRotationAngle(bone72, 0.6109F, -0.3491F, 0.0F);
		bone72.cubeList.add(new ModelBox(bone72, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F, false));
		bone72.cubeList.add(new ModelBox(bone72, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.1F, false));
		bone72.cubeList.add(new ModelBox(bone72, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
		bone72.cubeList.add(new ModelBox(bone72, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));

		bone73 = new ModelRenderer(this);
		bone73.setRotationPoint(1.2F, -2.5F, -1.2F);
		horns1.addChild(bone73);
		setRotationAngle(bone73, 0.6109F, 0.3491F, 0.0F);
		bone73.cubeList.add(new ModelBox(bone73, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F, true));
		bone73.cubeList.add(new ModelBox(bone73, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.1F, true));
		bone73.cubeList.add(new ModelBox(bone73, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
		bone73.cubeList.add(new ModelBox(bone73, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));

		bone74 = new ModelRenderer(this);
		bone74.setRotationPoint(2.3F, -2.5F, -1.6F);
		horns1.addChild(bone74);
		setRotationAngle(bone74, 0.5236F, 0.6981F, 0.0F);
		bone74.cubeList.add(new ModelBox(bone74, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
		bone74.cubeList.add(new ModelBox(bone74, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
		bone74.cubeList.add(new ModelBox(bone74, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
		bone74.cubeList.add(new ModelBox(bone74, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
		bone74.cubeList.add(new ModelBox(bone74, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));

		segment1_0 = new ModelRenderer(this);
		segment1_0.setRotationPoint(0.0F, -0.5F, 1.0F);
		head1.addChild(segment1_0);
		segment1_0.cubeList.add(new ModelBox(segment1_0, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment1_1 = new ModelRenderer(this);
		segment1_1.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment1_0.addChild(segment1_1);
		setRotationAngle(segment1_1, 0.0F, -0.5236F, 0.0F);
		segment1_1.cubeList.add(new ModelBox(segment1_1, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment1_2 = new ModelRenderer(this);
		segment1_2.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment1_1.addChild(segment1_2);
		setRotationAngle(segment1_2, 0.0F, 0.5236F, 0.0F);
		segment1_2.cubeList.add(new ModelBox(segment1_2, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment1_3 = new ModelRenderer(this);
		segment1_3.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment1_2.addChild(segment1_3);
		setRotationAngle(segment1_3, 0.0F, 0.5236F, 0.0F);
		segment1_3.cubeList.add(new ModelBox(segment1_3, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment1_4 = new ModelRenderer(this);
		segment1_4.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment1_3.addChild(segment1_4);
		setRotationAngle(segment1_4, 0.0F, -0.5236F, 0.0F);
		segment1_4.cubeList.add(new ModelBox(segment1_4, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment_0 = new ModelRenderer(this);
		segment_0.setRotationPoint(0.0F, -2.0F, 0.0F);
		setRotationAngle(segment_0, -0.7854F, 0.0F, 0.0F);
		segment_0.cubeList.add(new ModelBox(segment_0, 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));

		segment_1 = new ModelRenderer(this);
		segment_1.setRotationPoint(0.0F, 0.0F, -4.0F);
		segment_0.addChild(segment_1);
		setRotationAngle(segment_1, -0.7854F, 0.0F, 0.0F);
		segment_1.cubeList.add(new ModelBox(segment_1, 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));

		segment_2 = new ModelRenderer(this);
		segment_2.setRotationPoint(0.0F, 0.0F, -4.0F);
		segment_1.addChild(segment_2);
		setRotationAngle(segment_2, -0.2618F, 0.0F, 0.0F);
		segment_2.cubeList.add(new ModelBox(segment_2, 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));

		segment_3 = new ModelRenderer(this);
		segment_3.setRotationPoint(0.0F, 0.0F, -4.0F);
		segment_2.addChild(segment_3);
		setRotationAngle(segment_3, 0.7854F, 0.0F, 0.0F);
		segment_3.cubeList.add(new ModelBox(segment_3, 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));

		segment_4 = new ModelRenderer(this);
		segment_4.setRotationPoint(0.0F, 0.0F, -4.0F);
		segment_3.addChild(segment_4);
		setRotationAngle(segment_4, 0.7854F, 0.0F, 0.0F);
		segment_4.cubeList.add(new ModelBox(segment_4, 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.5F, -5.0F);
		segment_4.addChild(head);
		setRotationAngle(head, 0.5236F, 0.0F, 0.0F);
		head.cubeList.add(new ModelBox(head, 16, 0, -2.5F, -2.5F, 0.0F, 5, 4, 1, 0.1F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(1.4F, -1.2F, -5.35F);
		head.addChild(bone10);
		setRotationAngle(bone10, 0.7854F, 0.0F, 0.6109F);
		bone10.cubeList.add(new ModelBox(bone10, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));

		bone12 = new ModelRenderer(this);
		bone12.setRotationPoint(0.0F, -0.5F, 3.0F);
		bone10.addChild(bone12);
		setRotationAngle(bone12, -0.9599F, 0.0F, 0.0F);
		bone12.cubeList.add(new ModelBox(bone12, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));

		bone13 = new ModelRenderer(this);
		bone13.setRotationPoint(-1.4F, -1.2F, -5.35F);
		head.addChild(bone13);
		setRotationAngle(bone13, 0.7854F, 0.0F, -0.6109F);
		bone13.cubeList.add(new ModelBox(bone13, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));

		bone14 = new ModelRenderer(this);
		bone14.setRotationPoint(0.0F, -0.5F, 3.0F);
		bone13.addChild(bone14);
		setRotationAngle(bone14, -1.0036F, 0.0F, 0.0F);
		bone14.cubeList.add(new ModelBox(bone14, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));

		bone15 = new ModelRenderer(this);
		bone15.setRotationPoint(0.0F, -1.5F, 0.0F);
		head.addChild(bone15);
		setRotationAngle(bone15, 0.0436F, 0.0873F, 0.0F);
		bone15.cubeList.add(new ModelBox(bone15, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));

		bone16 = new ModelRenderer(this);
		bone16.setRotationPoint(0.0F, -1.5F, 0.0F);
		head.addChild(bone16);
		setRotationAngle(bone16, 0.0436F, -0.0873F, 0.0F);
		bone16.cubeList.add(new ModelBox(bone16, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));

		bone17 = new ModelRenderer(this);
		bone17.setRotationPoint(-0.15F, -1.6F, -2.5F);
		head.addChild(bone17);
		setRotationAngle(bone17, 0.5236F, 0.2618F, 0.0F);
		bone17.cubeList.add(new ModelBox(bone17, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));

		bone18 = new ModelRenderer(this);
		bone18.setRotationPoint(0.15F, -1.6F, -2.5F);
		head.addChild(bone18);
		setRotationAngle(bone18, 0.5236F, -0.2618F, 0.0F);
		bone18.cubeList.add(new ModelBox(bone18, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));

		bone27 = new ModelRenderer(this);
		bone27.setRotationPoint(2.6F, -0.4F, -3.95F);
		head.addChild(bone27);
		setRotationAngle(bone27, 0.0F, 0.2618F, 0.0F);
		bone27.cubeList.add(new ModelBox(bone27, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
		bone27.cubeList.add(new ModelBox(bone27, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));

		bone28 = new ModelRenderer(this);
		bone28.setRotationPoint(-2.6F, -0.4F, -3.95F);
		head.addChild(bone28);
		setRotationAngle(bone28, 0.0F, -0.2618F, 0.0F);
		bone28.cubeList.add(new ModelBox(bone28, 10, 19, 0.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
		bone28.cubeList.add(new ModelBox(bone28, 0, 19, 0.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));

		bone29 = new ModelRenderer(this);
		bone29.setRotationPoint(1.6F, 1.3F, -5.95F);
		head.addChild(bone29);
		bone29.cubeList.add(new ModelBox(bone29, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
		bone29.cubeList.add(new ModelBox(bone29, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));

		jaw = new ModelRenderer(this);
		jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.addChild(jaw);
		setRotationAngle(jaw, 0.5236F, 0.0F, 0.0F);

		bone30 = new ModelRenderer(this);
		bone30.setRotationPoint(3.0F, 0.9F, 0.0F);
		jaw.addChild(bone30);
		setRotationAngle(bone30, 0.0F, 0.2182F, 0.0F);
		bone30.cubeList.add(new ModelBox(bone30, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));

		bone31 = new ModelRenderer(this);
		bone31.setRotationPoint(-3.0F, 0.9F, 0.0F);
		jaw.addChild(bone31);
		setRotationAngle(bone31, 0.0F, -0.2182F, 0.0F);
		bone31.cubeList.add(new ModelBox(bone31, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));

		bone32 = new ModelRenderer(this);
		bone32.setRotationPoint(0.0F, 0.3F, 0.0F);
		jaw.addChild(bone32);
		bone32.cubeList.add(new ModelBox(bone32, 0, 0, 1.2F, -1.0F, -6.0F, 0, 1, 1, 0.1F, false));
		bone32.cubeList.add(new ModelBox(bone32, 0, 0, -1.2F, -1.0F, -6.0F, 0, 1, 1, 0.1F, true));

		horns = new ModelRenderer(this);
		horns.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.addChild(horns);

		bone33 = new ModelRenderer(this);
		bone33.setRotationPoint(-2.3F, -2.5F, -1.6F);
		horns.addChild(bone33);
		setRotationAngle(bone33, 0.5236F, -0.6981F, 0.0F);
		bone33.cubeList.add(new ModelBox(bone33, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
		bone33.cubeList.add(new ModelBox(bone33, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
		bone33.cubeList.add(new ModelBox(bone33, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
		bone33.cubeList.add(new ModelBox(bone33, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
		bone33.cubeList.add(new ModelBox(bone33, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));

		bone34 = new ModelRenderer(this);
		bone34.setRotationPoint(-1.2F, -2.5F, -1.2F);
		horns.addChild(bone34);
		setRotationAngle(bone34, 0.6109F, -0.3491F, 0.0F);
		bone34.cubeList.add(new ModelBox(bone34, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F, false));
		bone34.cubeList.add(new ModelBox(bone34, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.1F, false));
		bone34.cubeList.add(new ModelBox(bone34, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
		bone34.cubeList.add(new ModelBox(bone34, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));

		bone35 = new ModelRenderer(this);
		bone35.setRotationPoint(1.2F, -2.5F, -1.2F);
		horns.addChild(bone35);
		setRotationAngle(bone35, 0.6109F, 0.3491F, 0.0F);
		bone35.cubeList.add(new ModelBox(bone35, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F, true));
		bone35.cubeList.add(new ModelBox(bone35, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.1F, true));
		bone35.cubeList.add(new ModelBox(bone35, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
		bone35.cubeList.add(new ModelBox(bone35, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));

		bone36 = new ModelRenderer(this);
		bone36.setRotationPoint(2.3F, -2.5F, -1.6F);
		horns.addChild(bone36);
		setRotationAngle(bone36, 0.5236F, 0.6981F, 0.0F);
		bone36.cubeList.add(new ModelBox(bone36, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
		bone36.cubeList.add(new ModelBox(bone36, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
		bone36.cubeList.add(new ModelBox(bone36, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
		bone36.cubeList.add(new ModelBox(bone36, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
		bone36.cubeList.add(new ModelBox(bone36, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));

		segment_5 = new ModelRenderer(this);
		segment_5.setRotationPoint(0.0F, -2.0F, 0.0F);
		setRotationAngle(segment_5, 0.0F, 0.5236F, 0.0F);
		segment_5.cubeList.add(new ModelBox(segment_5, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment_6 = new ModelRenderer(this);
		segment_6.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_5.addChild(segment_6);
		setRotationAngle(segment_6, 0.0F, 0.5236F, 0.0F);
		segment_6.cubeList.add(new ModelBox(segment_6, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment_7 = new ModelRenderer(this);
		segment_7.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_6.addChild(segment_7);
		setRotationAngle(segment_7, 0.0F, -0.5236F, 0.0F);
		segment_7.cubeList.add(new ModelBox(segment_7, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment_8 = new ModelRenderer(this);
		segment_8.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_7.addChild(segment_8);
		setRotationAngle(segment_8, 0.0F, -0.5236F, 0.0F);
		segment_8.cubeList.add(new ModelBox(segment_8, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment_9 = new ModelRenderer(this);
		segment_9.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_8.addChild(segment_9);
		setRotationAngle(segment_9, 0.0F, -0.5236F, 0.0F);
		segment_9.cubeList.add(new ModelBox(segment_9, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment_10 = new ModelRenderer(this);
		segment_10.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_9.addChild(segment_10);
		setRotationAngle(segment_10, 0.0F, -0.5236F, 0.0F);
		segment_10.cubeList.add(new ModelBox(segment_10, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment_11 = new ModelRenderer(this);
		segment_11.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_10.addChild(segment_11);
		setRotationAngle(segment_11, 0.0F, -0.5236F, 0.0F);
		segment_11.cubeList.add(new ModelBox(segment_11, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, 0.0F, false));

		segment_12 = new ModelRenderer(this);
		segment_12.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_11.addChild(segment_12);
		setRotationAngle(segment_12, 0.0F, -0.5236F, 0.0F);
		segment_12.cubeList.add(new ModelBox(segment_12, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, -0.2F, false));

		segment_13 = new ModelRenderer(this);
		segment_13.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_12.addChild(segment_13);
		setRotationAngle(segment_13, 0.0873F, -0.5236F, 0.0F);
		segment_13.cubeList.add(new ModelBox(segment_13, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, -0.4F, false));

		segment_14 = new ModelRenderer(this);
		segment_14.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_13.addChild(segment_14);
		setRotationAngle(segment_14, 0.0873F, -0.5236F, 0.0F);
		segment_14.cubeList.add(new ModelBox(segment_14, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, -0.6F, false));

		segment_15 = new ModelRenderer(this);
		segment_15.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_14.addChild(segment_15);
		setRotationAngle(segment_15, 0.0873F, -0.5236F, 0.0F);
		segment_15.cubeList.add(new ModelBox(segment_15, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, -0.8F, false));

		segment_16 = new ModelRenderer(this);
		segment_16.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_15.addChild(segment_16);
		setRotationAngle(segment_16, 0.0873F, -0.5236F, 0.0F);
		segment_16.cubeList.add(new ModelBox(segment_16, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, -1.0F, false));

		segment_17 = new ModelRenderer(this);
		segment_17.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_16.addChild(segment_17);
		setRotationAngle(segment_17, 0.0873F, -0.5236F, 0.0F);
		segment_17.cubeList.add(new ModelBox(segment_17, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, -1.2F, false));

		segment_18 = new ModelRenderer(this);
		segment_18.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_17.addChild(segment_18);
		setRotationAngle(segment_18, 0.0F, -0.5236F, 0.0F);
		segment_18.cubeList.add(new ModelBox(segment_18, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, -1.4F, false));

		segment_19 = new ModelRenderer(this);
		segment_19.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_18.addChild(segment_19);
		setRotationAngle(segment_19, 0.0873F, -0.5236F, 0.0F);
		segment_19.cubeList.add(new ModelBox(segment_19, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, -1.6F, false));

		segment_20 = new ModelRenderer(this);
		segment_20.setRotationPoint(0.0F, 0.0F, 4.0F);
		segment_19.addChild(segment_20);
		setRotationAngle(segment_20, 0.0873F, -0.5236F, 0.0F);
		segment_20.cubeList.add(new ModelBox(segment_20, 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, -1.8F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		head1.render(f5);
		segment_0.render(f5);
		segment_5.render(f5);
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