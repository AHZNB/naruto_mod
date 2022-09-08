
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.entity.AbstractClientPlayer;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSteamArmor extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:steam_armorhelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:steam_armorbody")
	public static final Item body = null;
	@GameRegistry.ObjectHolder("narutomod:steam_armorlegs")
	public static final Item legs = null;
	//@GameRegistry.ObjectHolder("narutomod:steam_armorboots")
	//public static final Item boots = null;
	
	public ItemSteamArmor(ElementsNarutomodMod instance) {
		super(instance, 605);
	}

	public static boolean isWearingFullSet(EntityLivingBase entity) {
		return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == helmet 
		 && entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == body
		 && entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == legs;
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("STEAM_ARMOR", "narutomod:sasuke_", 50, new int[]{2, 5, 6, 2}, 9,
				(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("")), 1.5f)
				.setRepairItem(new ItemStack(Items.LEATHER));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.HEAD) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ModelBiped armorModel = new ModelSteamArmor();
				//armorModel.bipedHead = new ModelSteamArmor().bipedHead;
				//armorModel.bipedHeadwear = new ModelSteamArmor().bipedHeadwear;
				armorModel.isSneak = living.isSneaking();
				armorModel.isRiding = living.isRiding();
				armorModel.isChild = living.isChild();
				return armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/steamarmor.png";
			}
		}.setUnlocalizedName("steam_armorhelmet").setRegistryName("steam_armorhelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ModelBiped armorModel = new ModelSteamArmor();
				//armorModel.bipedBody = new ModelSteamArmor().bipedBody;
				//armorModel.bipedLeftArm = new ModelSteamArmor().bipedLeftArm;
				//armorModel.bipedRightArm = new ModelSteamArmor().bipedRightArm;
				armorModel.isSneak = living.isSneaking();
				armorModel.isRiding = living.isRiding();
				armorModel.isChild = living.isChild();
				return armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/steamarmor.png";
			}
		}.setUnlocalizedName("steam_armorbody").setRegistryName("steam_armorbody").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.LEGS) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ModelBiped armorModel = new ModelSteamArmor();
				//armorModel.bipedLeftLeg = new ModelSteamArmor().bipedLeftLeg;
				//armorModel.bipedRightLeg = new ModelSteamArmor().bipedRightLeg;
				armorModel.isSneak = living.isSneaking();
				armorModel.isRiding = living.isRiding();
				armorModel.isChild = living.isChild();
				return armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/steamarmor.png";
			}
		}.setUnlocalizedName("steam_armorlegs").setRegistryName("steam_armorlegs").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:steam_armorhelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:steam_armorbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:steam_armorlegs", "inventory"));
	}
	// Made with Blockbench 3.9.2
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelSteamArmor extends ModelBiped {
		//private final ModelRenderer bipedHead;
		private final ModelRenderer Hat;
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
		private final ModelRenderer cube_r17;
		private final ModelRenderer cube_r18;
		private final ModelRenderer cube_r19;
		private final ModelRenderer cube_r20;
		private final ModelRenderer cube_r21;
		private final ModelRenderer cube_r22;
		private final ModelRenderer cube_r23;
		private final ModelRenderer cube_r24;
		private final ModelRenderer cube_r25;
		private final ModelRenderer cube_r26;
		private final ModelRenderer cube_r27;
		private final ModelRenderer cube_r28;
		private final ModelRenderer cube_r29;
		private final ModelRenderer cube_r30;
		private final ModelRenderer cube_r31;
		private final ModelRenderer cube_r32;
		//private final ModelRenderer bipedHeadwear;
		//private final ModelRenderer bipedBody;
		private final ModelRenderer engine;
		private final ModelRenderer bone27;
		private final ModelRenderer bone28;
		private final ModelRenderer cube_r33;
		private final ModelRenderer cube_r34;
		private final ModelRenderer cube_r35;
		private final ModelRenderer cube_r36;
		private final ModelRenderer bone29;
		private final ModelRenderer cube_r37;
		private final ModelRenderer cube_r38;
		private final ModelRenderer cube_r39;
		private final ModelRenderer bone30;
		private final ModelRenderer cube_r40;
		private final ModelRenderer cube_r41;
		private final ModelRenderer cube_r42;
		private final ModelRenderer bone31;
		private final ModelRenderer cube_r43;
		private final ModelRenderer cube_r44;
		private final ModelRenderer cube_r45;
		private final ModelRenderer cube_r46;
		private final ModelRenderer bone34;
		private final ModelRenderer cube_r47;
		private final ModelRenderer cube_r48;
		private final ModelRenderer cube_r49;
		private final ModelRenderer bone35;
		private final ModelRenderer cube_r50;
		private final ModelRenderer cube_r51;
		private final ModelRenderer cube_r52;
		private final ModelRenderer cube_r53;
		private final ModelRenderer cube_r54;
		private final ModelRenderer bone36;
		private final ModelRenderer cube_r55;
		private final ModelRenderer cube_r56;
		private final ModelRenderer cube_r57;
		private final ModelRenderer cube_r58;
		private final ModelRenderer bone37;
		private final ModelRenderer cube_r59;
		private final ModelRenderer cube_r60;
		private final ModelRenderer cube_r61;
		private final ModelRenderer cube_r62;
		private final ModelRenderer bone26;
		private final ModelRenderer bone22;
		private final ModelRenderer cube_r63;
		private final ModelRenderer cube_r64;
		private final ModelRenderer cube_r65;
		private final ModelRenderer cube_r66;
		private final ModelRenderer bone23;
		private final ModelRenderer cube_r67;
		private final ModelRenderer cube_r68;
		private final ModelRenderer cube_r69;
		private final ModelRenderer cube_r70;
		private final ModelRenderer cube_r71;
		private final ModelRenderer bone24;
		private final ModelRenderer cube_r72;
		private final ModelRenderer cube_r73;
		private final ModelRenderer cube_r74;
		private final ModelRenderer bone25;
		private final ModelRenderer cube_r75;
		private final ModelRenderer cube_r76;
		private final ModelRenderer cube_r77;
		private final ModelRenderer cube_r78;
		private final ModelRenderer bone32;
		private final ModelRenderer cube_r79;
		private final ModelRenderer cube_r80;
		private final ModelRenderer bone33;
		private final ModelRenderer cube_r81;
		private final ModelRenderer cube_r82;
		private final ModelRenderer cube_r83;
		private final ModelRenderer bone38;
		private final ModelRenderer cube_r84;
		private final ModelRenderer cube_r85;
		private final ModelRenderer cube_r86;
		private final ModelRenderer bone39;
		private final ModelRenderer cube_r87;
		private final ModelRenderer cube_r88;
		private final ModelRenderer cube_r89;
		private final ModelRenderer bone40;
		private final ModelRenderer cube_r90;
		private final ModelRenderer cube_r91;
		private final ModelRenderer bone41;
		private final ModelRenderer cube_r92;
		private final ModelRenderer cube_r93;
		private final ModelRenderer cube_r94;
		private final ModelRenderer bone42;
		private final ModelRenderer cube_r95;
		private final ModelRenderer cube_r96;
		private final ModelRenderer cube_r97;
		private final ModelRenderer bone43;
		private final ModelRenderer cube_r98;
		private final ModelRenderer cube_r99;
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
		//private final ModelRenderer bipedRightLeg;
		private final ModelRenderer bone21;
		private final ModelRenderer cube_r100;
		private final ModelRenderer cube_r101;
		private final ModelRenderer cube_r102;
		private final ModelRenderer cube_r103;
		private final ModelRenderer cube_r104;
		private final ModelRenderer bone16;
		private final ModelRenderer cube_r105;
		private final ModelRenderer cube_r106;
		private final ModelRenderer cube_r107;
		private final ModelRenderer bone19;
		private final ModelRenderer cube_r108;
		private final ModelRenderer cube_r109;
		private final ModelRenderer cube_r110;
		//private final ModelRenderer bipedLeftLeg;
		private final ModelRenderer bone20;
		private final ModelRenderer cube_r111;
		private final ModelRenderer cube_r112;
		private final ModelRenderer cube_r113;
		private final ModelRenderer cube_r114;
		private final ModelRenderer cube_r115;
		private final ModelRenderer bone17;
		private final ModelRenderer cube_r116;
		private final ModelRenderer cube_r117;
		private final ModelRenderer cube_r118;
		private final ModelRenderer bone18;
		private final ModelRenderer cube_r119;
		private final ModelRenderer cube_r120;
		private final ModelRenderer cube_r121;
	
		public ModelSteamArmor() {
			textureWidth = 64;
			textureHeight = 64;
	
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.1F, false));
	
			Hat = new ModelRenderer(this);
			Hat.setRotationPoint(0.0F, -4.9F, 0.0F);
			bipedHead.addChild(Hat);
			setRotationAngle(Hat, -0.0436F, 0.0F, 0.0F);
			
	
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r1);
			setRotationAngle(cube_r1, 0.0F, 1.5708F, -0.9599F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r2);
			setRotationAngle(cube_r2, -2.1817F, 0.0F, 3.1416F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.0F, -1.5708F, 0.9599F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r4);
			setRotationAngle(cube_r4, -2.1817F, 0.7854F, -3.1416F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.9599F, 0.7854F, 0.0F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r6);
			setRotationAngle(cube_r6, -2.1817F, -0.7854F, -3.1416F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r7);
			setRotationAngle(cube_r7, 0.9599F, -0.7854F, 0.0F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r8);
			setRotationAngle(cube_r8, 0.9599F, 2.7489F, 0.0F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r9);
			setRotationAngle(cube_r9, 0.9599F, -2.7489F, 0.0F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r10);
			setRotationAngle(cube_r10, 0.9599F, -1.9635F, 0.0F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r11 = new ModelRenderer(this);
			cube_r11.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r11);
			setRotationAngle(cube_r11, 0.9599F, -1.1781F, 0.0F);
			cube_r11.cubeList.add(new ModelBox(cube_r11, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r12 = new ModelRenderer(this);
			cube_r12.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r12);
			setRotationAngle(cube_r12, 0.9599F, 1.9635F, 0.0F);
			cube_r12.cubeList.add(new ModelBox(cube_r12, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r13 = new ModelRenderer(this);
			cube_r13.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r13);
			setRotationAngle(cube_r13, 0.9599F, 1.1781F, 0.0F);
			cube_r13.cubeList.add(new ModelBox(cube_r13, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r14 = new ModelRenderer(this);
			cube_r14.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r14);
			setRotationAngle(cube_r14, 0.9599F, 0.3927F, 0.0F);
			cube_r14.cubeList.add(new ModelBox(cube_r14, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r15 = new ModelRenderer(this);
			cube_r15.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r15);
			setRotationAngle(cube_r15, 0.9599F, -0.3927F, 0.0F);
			cube_r15.cubeList.add(new ModelBox(cube_r15, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r16 = new ModelRenderer(this);
			cube_r16.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r16);
			setRotationAngle(cube_r16, 0.9599F, -2.9452F, 0.0F);
			cube_r16.cubeList.add(new ModelBox(cube_r16, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r17 = new ModelRenderer(this);
			cube_r17.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r17);
			setRotationAngle(cube_r17, 0.9599F, -2.5525F, 0.0F);
			cube_r17.cubeList.add(new ModelBox(cube_r17, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r18 = new ModelRenderer(this);
			cube_r18.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r18);
			setRotationAngle(cube_r18, 0.9599F, -2.1598F, 0.0F);
			cube_r18.cubeList.add(new ModelBox(cube_r18, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r19 = new ModelRenderer(this);
			cube_r19.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r19);
			setRotationAngle(cube_r19, 0.9599F, -1.7671F, 0.0F);
			cube_r19.cubeList.add(new ModelBox(cube_r19, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r20 = new ModelRenderer(this);
			cube_r20.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r20);
			setRotationAngle(cube_r20, 0.9599F, -1.3744F, 0.0F);
			cube_r20.cubeList.add(new ModelBox(cube_r20, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r21 = new ModelRenderer(this);
			cube_r21.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r21);
			setRotationAngle(cube_r21, 0.9599F, -0.9817F, 0.0F);
			cube_r21.cubeList.add(new ModelBox(cube_r21, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r22 = new ModelRenderer(this);
			cube_r22.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r22);
			setRotationAngle(cube_r22, 0.9599F, -0.589F, 0.0F);
			cube_r22.cubeList.add(new ModelBox(cube_r22, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r23 = new ModelRenderer(this);
			cube_r23.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r23);
			setRotationAngle(cube_r23, 0.9599F, 2.9452F, 0.0F);
			cube_r23.cubeList.add(new ModelBox(cube_r23, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r24 = new ModelRenderer(this);
			cube_r24.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r24);
			setRotationAngle(cube_r24, 0.9599F, 2.5525F, 0.0F);
			cube_r24.cubeList.add(new ModelBox(cube_r24, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r25 = new ModelRenderer(this);
			cube_r25.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r25);
			setRotationAngle(cube_r25, 0.9599F, 2.1598F, 0.0F);
			cube_r25.cubeList.add(new ModelBox(cube_r25, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r26 = new ModelRenderer(this);
			cube_r26.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r26);
			setRotationAngle(cube_r26, 0.9599F, 1.7671F, 0.0F);
			cube_r26.cubeList.add(new ModelBox(cube_r26, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r27 = new ModelRenderer(this);
			cube_r27.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r27);
			setRotationAngle(cube_r27, 0.9599F, 1.3744F, 0.0F);
			cube_r27.cubeList.add(new ModelBox(cube_r27, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r28 = new ModelRenderer(this);
			cube_r28.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r28);
			setRotationAngle(cube_r28, 0.9599F, 0.9817F, 0.0F);
			cube_r28.cubeList.add(new ModelBox(cube_r28, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r29 = new ModelRenderer(this);
			cube_r29.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r29);
			setRotationAngle(cube_r29, 0.9599F, 0.589F, 0.0F);
			cube_r29.cubeList.add(new ModelBox(cube_r29, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r30 = new ModelRenderer(this);
			cube_r30.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r30);
			setRotationAngle(cube_r30, 0.9599F, 0.1963F, 0.0F);
			cube_r30.cubeList.add(new ModelBox(cube_r30, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r31 = new ModelRenderer(this);
			cube_r31.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r31);
			setRotationAngle(cube_r31, 0.9599F, -0.1963F, 0.0F);
			cube_r31.cubeList.add(new ModelBox(cube_r31, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			cube_r32 = new ModelRenderer(this);
			cube_r32.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r32);
			setRotationAngle(cube_r32, 0.9599F, 0.0F, 0.0F);
			cube_r32.cubeList.add(new ModelBox(cube_r32, 60, 16, -1.0F, 0.5F, 0.0F, 2, 12, 0, 0.0F, false));
	
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));
	
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.1F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F, false));
	
			engine = new ModelRenderer(this);
			engine.setRotationPoint(0.0F, 3.05F, 2.275F);
			bipedBody.addChild(engine);
			
	
			bone27 = new ModelRenderer(this);
			bone27.setRotationPoint(0.0F, 0.0F, 0.0F);
			engine.addChild(bone27);
			
	
			bone28 = new ModelRenderer(this);
			bone28.setRotationPoint(-1.8604F, -18.3681F, 2.225F);
			bone27.addChild(bone28);
			bone28.cubeList.add(new ModelBox(bone28, 60, 62, 1.3604F, 19.8349F, -3.225F, 1, 1, 2, 0.0F, true));
	
			cube_r33 = new ModelRenderer(this);
			cube_r33.setRotationPoint(2.0137F, 20.5643F, -9.725F);
			bone28.addChild(cube_r33);
			setRotationAngle(cube_r33, 0.0F, 0.0F, 0.3927F);
			cube_r33.cubeList.add(new ModelBox(cube_r33, 60, 62, -1.5F, -0.5F, 6.5F, 1, 1, 2, 0.0F, true));
	
			cube_r34 = new ModelRenderer(this);
			cube_r34.setRotationPoint(-0.7033F, 19.2462F, -2.725F);
			bone28.addChild(cube_r34);
			setRotationAngle(cube_r34, 0.0F, 0.0F, 1.5708F);
			cube_r34.cubeList.add(new ModelBox(cube_r34, 60, 62, -1.425F, -1.05F, -0.5F, 1, 1, 2, 0.0F, true));
	
			cube_r35 = new ModelRenderer(this);
			cube_r35.setRotationPoint(0.0F, 19.0918F, -2.725F);
			bone28.addChild(cube_r35);
			setRotationAngle(cube_r35, 0.0F, 0.0F, 1.1781F);
			cube_r35.cubeList.add(new ModelBox(cube_r35, 60, 62, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, true));
	
			cube_r36 = new ModelRenderer(this);
			cube_r36.setRotationPoint(0.4365F, 19.7451F, -2.725F);
			bone28.addChild(cube_r36);
			setRotationAngle(cube_r36, 0.0F, 0.0F, 0.7854F);
			cube_r36.cubeList.add(new ModelBox(cube_r36, 60, 62, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, true));
	
			bone29 = new ModelRenderer(this);
			bone29.setRotationPoint(-2.1489F, -21.7016F, 2.225F);
			bone27.addChild(bone29);
			
	
			cube_r37 = new ModelRenderer(this);
			cube_r37.setRotationPoint(2.3022F, 19.4116F, -9.725F);
			bone29.addChild(cube_r37);
			setRotationAngle(cube_r37, 0.0F, 0.0F, -0.3927F);
			cube_r37.cubeList.add(new ModelBox(cube_r37, 60, 62, -1.5F, -0.5F, 6.5F, 1, 1, 2, 0.0F, true));
	
			cube_r38 = new ModelRenderer(this);
			cube_r38.setRotationPoint(0.2885F, 20.8841F, -2.725F);
			bone29.addChild(cube_r38);
			setRotationAngle(cube_r38, 0.0F, 0.0F, -1.1781F);
			cube_r38.cubeList.add(new ModelBox(cube_r38, 60, 62, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, true));
	
			cube_r39 = new ModelRenderer(this);
			cube_r39.setRotationPoint(0.7251F, 20.2308F, -2.725F);
			bone29.addChild(cube_r39);
			setRotationAngle(cube_r39, 0.0F, 0.0F, -0.7854F);
			cube_r39.cubeList.add(new ModelBox(cube_r39, 60, 62, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, true));
	
			bone30 = new ModelRenderer(this);
			bone30.setRotationPoint(1.5239F, -21.7016F, 2.225F);
			bone27.addChild(bone30);
			bone30.cubeList.add(new ModelBox(bone30, 60, 62, -2.0239F, 19.141F, -3.225F, 1, 1, 2, 0.0F, false));
	
			cube_r40 = new ModelRenderer(this);
			cube_r40.setRotationPoint(-1.6772F, 19.4116F, -9.725F);
			bone30.addChild(cube_r40);
			setRotationAngle(cube_r40, 0.0F, 0.0F, 0.3927F);
			cube_r40.cubeList.add(new ModelBox(cube_r40, 60, 62, 0.5F, -0.5F, 6.5F, 1, 1, 2, 0.0F, false));
	
			cube_r41 = new ModelRenderer(this);
			cube_r41.setRotationPoint(0.3365F, 20.8841F, -2.725F);
			bone30.addChild(cube_r41);
			setRotationAngle(cube_r41, 0.0F, 0.0F, 1.1781F);
			cube_r41.cubeList.add(new ModelBox(cube_r41, 60, 62, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, false));
	
			cube_r42 = new ModelRenderer(this);
			cube_r42.setRotationPoint(-0.1001F, 20.2308F, -2.725F);
			bone30.addChild(cube_r42);
			setRotationAngle(cube_r42, 0.0F, 0.0F, 0.7854F);
			cube_r42.cubeList.add(new ModelBox(cube_r42, 60, 62, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, false));
	
			bone31 = new ModelRenderer(this);
			bone31.setRotationPoint(1.8604F, -18.3681F, 2.225F);
			bone27.addChild(bone31);
			
	
			cube_r43 = new ModelRenderer(this);
			cube_r43.setRotationPoint(-2.0137F, 20.5643F, -9.725F);
			bone31.addChild(cube_r43);
			setRotationAngle(cube_r43, 0.0F, 0.0F, -0.3927F);
			cube_r43.cubeList.add(new ModelBox(cube_r43, 60, 62, 0.5F, -0.5F, 6.5F, 1, 1, 2, 0.0F, false));
	
			cube_r44 = new ModelRenderer(this);
			cube_r44.setRotationPoint(0.7033F, 19.2462F, -2.725F);
			bone31.addChild(cube_r44);
			setRotationAngle(cube_r44, 0.0F, 0.0F, -1.5708F);
			cube_r44.cubeList.add(new ModelBox(cube_r44, 60, 62, 0.425F, -1.05F, -0.5F, 1, 1, 2, 0.0F, false));
	
			cube_r45 = new ModelRenderer(this);
			cube_r45.setRotationPoint(0.0F, 19.0918F, -2.725F);
			bone31.addChild(cube_r45);
			setRotationAngle(cube_r45, 0.0F, 0.0F, -1.1781F);
			cube_r45.cubeList.add(new ModelBox(cube_r45, 60, 62, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, false));
	
			cube_r46 = new ModelRenderer(this);
			cube_r46.setRotationPoint(-0.4365F, 19.7451F, -2.725F);
			bone31.addChild(cube_r46);
			setRotationAngle(cube_r46, 0.0F, 0.0F, -0.7854F);
			cube_r46.cubeList.add(new ModelBox(cube_r46, 60, 62, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, false));
	
			bone34 = new ModelRenderer(this);
			bone34.setRotationPoint(0.1172F, 2.4572F, 2.2325F);
			bone27.addChild(bone34);
			setRotationAngle(bone34, 0.0873F, 0.0F, 0.0F);
			
	
			cube_r47 = new ModelRenderer(this);
			cube_r47.setRotationPoint(2.0616F, -1.6016F, 0.0F);
			bone34.addChild(cube_r47);
			setRotationAngle(cube_r47, -0.3927F, 0.0F, -1.1781F);
			cube_r47.cubeList.add(new ModelBox(cube_r47, 50, 61, -0.5F, -0.5F, -1.0F, 1, 1, 3, 0.15F, false));
	
			cube_r48 = new ModelRenderer(this);
			cube_r48.setRotationPoint(1.1421F, -1.2448F, 0.2392F);
			bone34.addChild(cube_r48);
			setRotationAngle(cube_r48, -0.3927F, 0.0F, -0.7854F);
			cube_r48.cubeList.add(new ModelBox(cube_r48, 50, 61, -0.5F, 0.125F, -1.0F, 1, 1, 3, 0.15F, false));
	
			cube_r49 = new ModelRenderer(this);
			cube_r49.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone34.addChild(cube_r49);
			setRotationAngle(cube_r49, -0.3927F, 0.0F, -0.3927F);
			cube_r49.cubeList.add(new ModelBox(cube_r49, 50, 61, 0.35F, -0.5F, -1.0F, 1, 1, 3, 0.15F, false));
	
			bone35 = new ModelRenderer(this);
			bone35.setRotationPoint(-0.2344F, 0.0F, 0.0F);
			bone34.addChild(bone35);
			
	
			cube_r50 = new ModelRenderer(this);
			cube_r50.setRotationPoint(-2.0616F, -1.6016F, 0.0F);
			bone35.addChild(cube_r50);
			setRotationAngle(cube_r50, -0.3927F, 0.0F, 1.1781F);
			cube_r50.cubeList.add(new ModelBox(cube_r50, 50, 61, -0.5F, -0.5F, -1.0F, 1, 1, 3, 0.15F, true));
	
			cube_r51 = new ModelRenderer(this);
			cube_r51.setRotationPoint(-2.0332F, -1.5041F, 0.0861F);
			bone35.addChild(cube_r51);
			setRotationAngle(cube_r51, -0.3927F, 0.0F, 1.5708F);
			cube_r51.cubeList.add(new ModelBox(cube_r51, 50, 61, -1.5F, -0.275F, -1.0F, 1, 1, 3, 0.15F, true));
	
			cube_r52 = new ModelRenderer(this);
			cube_r52.setRotationPoint(0.1172F, -0.1458F, 0.0F);
			bone35.addChild(cube_r52);
			setRotationAngle(cube_r52, -0.3927F, 0.0F, 0.0F);
			cube_r52.cubeList.add(new ModelBox(cube_r52, 50, 61, -0.5F, -0.5F, -1.0F, 1, 1, 3, 0.15F, true));
	
			cube_r53 = new ModelRenderer(this);
			cube_r53.setRotationPoint(-1.1421F, -1.2448F, 0.2392F);
			bone35.addChild(cube_r53);
			setRotationAngle(cube_r53, -0.3927F, 0.0F, 0.7854F);
			cube_r53.cubeList.add(new ModelBox(cube_r53, 50, 61, -0.5F, 0.125F, -1.0F, 1, 1, 3, 0.15F, true));
	
			cube_r54 = new ModelRenderer(this);
			cube_r54.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone35.addChild(cube_r54);
			setRotationAngle(cube_r54, -0.3927F, 0.0F, 0.3927F);
			cube_r54.cubeList.add(new ModelBox(cube_r54, 50, 61, -1.35F, -0.5F, -1.0F, 1, 1, 3, 0.15F, true));
	
			bone36 = new ModelRenderer(this);
			bone36.setRotationPoint(1.3939F, -8.9128F, -0.7865F);
			bone34.addChild(bone36);
			
	
			cube_r55 = new ModelRenderer(this);
			cube_r55.setRotationPoint(0.6676F, 5.5063F, 0.7865F);
			bone36.addChild(cube_r55);
			setRotationAngle(cube_r55, 0.3927F, 0.0F, 1.1781F);
			cube_r55.cubeList.add(new ModelBox(cube_r55, 38, 60, -0.5F, -0.5F, -1.0F, 1, 1, 3, 0.15F, false));
	
			cube_r56 = new ModelRenderer(this);
			cube_r56.setRotationPoint(0.6393F, 5.4088F, 0.8726F);
			bone36.addChild(cube_r56);
			setRotationAngle(cube_r56, 0.3927F, 0.0F, 1.5708F);
			cube_r56.cubeList.add(new ModelBox(cube_r56, 50, 61, 0.5F, -0.725F, -1.0F, 1, 1, 3, 0.15F, false));
	
			cube_r57 = new ModelRenderer(this);
			cube_r57.setRotationPoint(-0.2519F, 5.1495F, 1.0256F);
			bone36.addChild(cube_r57);
			setRotationAngle(cube_r57, 0.3927F, 0.0F, 0.7854F);
			cube_r57.cubeList.add(new ModelBox(cube_r57, 50, 61, -0.5F, -1.125F, -1.0F, 1, 1, 3, 0.15F, false));
	
			cube_r58 = new ModelRenderer(this);
			cube_r58.setRotationPoint(-1.3939F, 3.9047F, 0.7865F);
			bone36.addChild(cube_r58);
			setRotationAngle(cube_r58, 0.3927F, 0.0F, 0.3927F);
			cube_r58.cubeList.add(new ModelBox(cube_r58, 50, 61, 0.35F, -0.5F, -1.0F, 1, 1, 3, 0.15F, false));
	
			bone37 = new ModelRenderer(this);
			bone37.setRotationPoint(-1.6283F, 2.5985F, 0.7865F);
			bone36.addChild(bone37);
			
	
			cube_r59 = new ModelRenderer(this);
			cube_r59.setRotationPoint(-2.0616F, 2.9078F, 0.0F);
			bone37.addChild(cube_r59);
			setRotationAngle(cube_r59, 0.3927F, 0.0F, -1.1781F);
			cube_r59.cubeList.add(new ModelBox(cube_r59, 38, 60, -0.5F, -0.5F, -1.0F, 1, 1, 3, 0.15F, true));
	
			cube_r60 = new ModelRenderer(this);
			cube_r60.setRotationPoint(0.1172F, 1.452F, 0.0F);
			bone37.addChild(cube_r60);
			setRotationAngle(cube_r60, 0.3927F, 0.0F, 0.0F);
			cube_r60.cubeList.add(new ModelBox(cube_r60, 38, 60, -0.5F, -0.5F, -1.0F, 1, 1, 3, 0.15F, true));
	
			cube_r61 = new ModelRenderer(this);
			cube_r61.setRotationPoint(-1.1421F, 2.551F, 0.2392F);
			bone37.addChild(cube_r61);
			setRotationAngle(cube_r61, 0.3927F, 0.0F, -0.7854F);
			cube_r61.cubeList.add(new ModelBox(cube_r61, 50, 61, -0.5F, -1.125F, -1.0F, 1, 1, 3, 0.15F, true));
	
			cube_r62 = new ModelRenderer(this);
			cube_r62.setRotationPoint(0.0F, 1.3063F, 0.0F);
			bone37.addChild(cube_r62);
			setRotationAngle(cube_r62, 0.3927F, 0.0F, -0.3927F);
			cube_r62.cubeList.add(new ModelBox(cube_r62, 50, 61, -1.35F, -0.5F, -1.0F, 1, 1, 3, 0.15F, true));
	
			bone26 = new ModelRenderer(this);
			bone26.setRotationPoint(-0.0657F, -0.7475F, 5.2898F);
			engine.addChild(bone26);
			setRotationAngle(bone26, 0.1745F, 0.0F, 0.0F);
			
	
			bone22 = new ModelRenderer(this);
			bone22.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone26.addChild(bone22);
			setRotationAngle(bone22, -0.0873F, 0.0F, 0.0F);
			
	
			cube_r63 = new ModelRenderer(this);
			cube_r63.setRotationPoint(2.2445F, 1.2983F, -0.1486F);
			bone22.addChild(cube_r63);
			setRotationAngle(cube_r63, 0.3927F, 0.0F, -1.1781F);
			cube_r63.cubeList.add(new ModelBox(cube_r63, 50, 61, -0.5221F, -0.3867F, -1.7504F, 1, 1, 3, 0.15F, false));
	
			cube_r64 = new ModelRenderer(this);
			cube_r64.setRotationPoint(2.2161F, 1.3958F, -0.2347F);
			bone22.addChild(cube_r64);
			setRotationAngle(cube_r64, 0.3927F, 0.0F, -1.5708F);
			cube_r64.cubeList.add(new ModelBox(cube_r64, 50, 61, 0.476F, -0.1702F, -1.7469F, 1, 1, 3, 0.15F, false));
	
			cube_r65 = new ModelRenderer(this);
			cube_r65.setRotationPoint(1.325F, 1.6551F, -0.3877F);
			bone22.addChild(cube_r65);
			setRotationAngle(cube_r65, 0.3927F, 0.0F, -0.7854F);
			cube_r65.cubeList.add(new ModelBox(cube_r65, 50, 61, -0.5169F, 0.2455F, -1.7534F, 1, 1, 3, 0.15F, false));
	
			cube_r66 = new ModelRenderer(this);
			cube_r66.setRotationPoint(0.1829F, 2.8999F, -0.1486F);
			bone22.addChild(cube_r66);
			setRotationAngle(cube_r66, 0.3927F, 0.0F, -0.3927F);
			cube_r66.cubeList.add(new ModelBox(cube_r66, 50, 61, 0.3408F, -0.3747F, -1.7554F, 1, 1, 3, 0.15F, false));
	
			bone23 = new ModelRenderer(this);
			bone23.setRotationPoint(-0.0515F, 2.5727F, -0.258F);
			bone22.addChild(bone23);
			
	
			cube_r67 = new ModelRenderer(this);
			cube_r67.setRotationPoint(-2.0616F, -1.2744F, 0.1094F);
			bone23.addChild(cube_r67);
			setRotationAngle(cube_r67, 0.3927F, 0.0F, 1.1781F);
			cube_r67.cubeList.add(new ModelBox(cube_r67, 50, 61, -0.4779F, -0.3867F, -1.7504F, 1, 1, 3, 0.15F, true));
	
			cube_r68 = new ModelRenderer(this);
			cube_r68.setRotationPoint(-2.0332F, -1.1768F, 0.0233F);
			bone23.addChild(cube_r68);
			setRotationAngle(cube_r68, 0.3927F, 0.0F, 1.5708F);
			cube_r68.cubeList.add(new ModelBox(cube_r68, 50, 61, -1.476F, -0.1702F, -1.7469F, 1, 1, 3, 0.15F, true));
	
			cube_r69 = new ModelRenderer(this);
			cube_r69.setRotationPoint(0.1172F, 0.1814F, 0.1094F);
			bone23.addChild(cube_r69);
			setRotationAngle(cube_r69, 0.3927F, 0.0F, 0.0F);
			cube_r69.cubeList.add(new ModelBox(cube_r69, 50, 61, -0.5F, -0.373F, -1.7561F, 1, 1, 3, 0.15F, true));
	
			cube_r70 = new ModelRenderer(this);
			cube_r70.setRotationPoint(-1.1421F, -0.9176F, -0.1298F);
			bone23.addChild(cube_r70);
			setRotationAngle(cube_r70, 0.3927F, 0.0F, 0.7854F);
			cube_r70.cubeList.add(new ModelBox(cube_r70, 50, 61, -0.4831F, 0.2455F, -1.7534F, 1, 1, 3, 0.15F, true));
	
			cube_r71 = new ModelRenderer(this);
			cube_r71.setRotationPoint(0.0F, 0.3272F, 0.1094F);
			bone23.addChild(cube_r71);
			setRotationAngle(cube_r71, 0.3927F, 0.0F, 0.3927F);
			cube_r71.cubeList.add(new ModelBox(cube_r71, 50, 61, -1.3408F, -0.3747F, -1.7554F, 1, 1, 3, 0.15F, true));
	
			bone24 = new ModelRenderer(this);
			bone24.setRotationPoint(1.5769F, -6.2902F, 0.6285F);
			bone22.addChild(bone24);
			
	
			cube_r72 = new ModelRenderer(this);
			cube_r72.setRotationPoint(0.6676F, 5.8335F, -0.677F);
			bone24.addChild(cube_r72);
			setRotationAngle(cube_r72, -0.3927F, 0.0F, 1.1781F);
			cube_r72.cubeList.add(new ModelBox(cube_r72, 31, 60, -0.4779F, -0.5964F, -1.7434F, 1, 1, 3, 0.15F, false));
	
			cube_r73 = new ModelRenderer(this);
			cube_r73.setRotationPoint(-0.2519F, 5.4767F, -0.9162F);
			bone24.addChild(cube_r73);
			setRotationAngle(cube_r73, -0.3927F, 0.0F, 0.7854F);
			cube_r73.cubeList.add(new ModelBox(cube_r73, 50, 61, -0.4831F, -1.2142F, -1.7404F, 1, 1, 3, 0.15F, false));
	
			cube_r74 = new ModelRenderer(this);
			cube_r74.setRotationPoint(-1.3939F, 4.2319F, -0.677F);
			bone24.addChild(cube_r74);
			setRotationAngle(cube_r74, -0.3927F, 0.0F, 0.3927F);
			cube_r74.cubeList.add(new ModelBox(cube_r74, 50, 61, 0.3592F, -0.5844F, -1.7384F, 1, 1, 3, 0.15F, false));
	
			bone25 = new ModelRenderer(this);
			bone25.setRotationPoint(-1.6283F, 2.5985F, -0.7865F);
			bone24.addChild(bone25);
			
	
			cube_r75 = new ModelRenderer(this);
			cube_r75.setRotationPoint(-2.0616F, 3.235F, 0.1094F);
			bone25.addChild(cube_r75);
			setRotationAngle(cube_r75, -0.3927F, 0.0F, -1.1781F);
			cube_r75.cubeList.add(new ModelBox(cube_r75, 31, 60, -0.5221F, -0.5714F, -1.7434F, 1, 1, 3, 0.15F, true));
	
			cube_r76 = new ModelRenderer(this);
			cube_r76.setRotationPoint(0.1172F, 1.7792F, 0.1094F);
			bone25.addChild(cube_r76);
			setRotationAngle(cube_r76, -0.3927F, 0.0F, 0.0F);
			cube_r76.cubeList.add(new ModelBox(cube_r76, 31, 60, -0.5F, -0.5827F, -1.7377F, 1, 1, 3, 0.15F, true));
	
			cube_r77 = new ModelRenderer(this);
			cube_r77.setRotationPoint(-1.1421F, 2.8783F, -0.1298F);
			bone25.addChild(cube_r77);
			setRotationAngle(cube_r77, -0.3927F, 0.0F, -0.7854F);
			cube_r77.cubeList.add(new ModelBox(cube_r77, 50, 61, -0.5169F, -1.2142F, -1.7404F, 1, 1, 3, 0.15F, true));
	
			cube_r78 = new ModelRenderer(this);
			cube_r78.setRotationPoint(0.0F, 1.6335F, 0.1094F);
			bone25.addChild(cube_r78);
			setRotationAngle(cube_r78, -0.3927F, 0.0F, -0.3927F);
			cube_r78.cubeList.add(new ModelBox(cube_r78, 50, 61, -1.3592F, -0.5844F, -1.7384F, 1, 1, 3, 0.15F, true));
	
			bone32 = new ModelRenderer(this);
			bone32.setRotationPoint(-0.0407F, -0.4743F, 6.025F);
			engine.addChild(bone32);
			setRotationAngle(bone32, -0.0873F, 3.1416F, 0.0F);
			
	
			cube_r79 = new ModelRenderer(this);
			cube_r79.setRotationPoint(4.843F, 10.5171F, 6.925F);
			bone32.addChild(cube_r79);
			setRotationAngle(cube_r79, -0.9163F, 0.0F, -0.6109F);
			cube_r79.cubeList.add(new ModelBox(cube_r79, 56, 56, 1.4863F, -0.2707F, -14.6512F, 1, 1, 3, 0.0F, false));
	
			cube_r80 = new ModelRenderer(this);
			cube_r80.setRotationPoint(2.9058F, 11.0372F, 6.925F);
			bone32.addChild(cube_r80);
			setRotationAngle(cube_r80, -0.9163F, 0.0F, -0.2618F);
			cube_r80.cubeList.add(new ModelBox(cube_r80, 56, 56, -0.5062F, -0.2686F, -14.6484F, 1, 1, 3, 0.0F, false));
	
			bone33 = new ModelRenderer(this);
			bone33.setRotationPoint(10.9914F, 2.835F, 6.925F);
			bone32.addChild(bone33);
			setRotationAngle(bone33, 0.0F, 0.0F, -0.7854F);
			
	
			cube_r81 = new ModelRenderer(this);
			cube_r81.setRotationPoint(1.4643F, -1.0533F, 0.0F);
			bone33.addChild(cube_r81);
			setRotationAngle(cube_r81, -0.9163F, 0.0F, -0.6981F);
			cube_r81.cubeList.add(new ModelBox(cube_r81, 56, 56, -0.5239F, -0.2814F, -14.6651F, 1, 1, 3, 0.0F, false));
	
			cube_r82 = new ModelRenderer(this);
			cube_r82.setRotationPoint(-1.9523F, 0.9192F, 0.0F);
			bone33.addChild(cube_r82);
			setRotationAngle(cube_r82, -0.9163F, 0.0F, -0.3491F);
			cube_r82.cubeList.add(new ModelBox(cube_r82, 56, 56, -0.5217F, -0.2765F, -14.6587F, 1, 1, 3, 0.0F, false));
	
			cube_r83 = new ModelRenderer(this);
			cube_r83.setRotationPoint(-5.8346F, 1.779F, 0.0F);
			bone33.addChild(cube_r83);
			setRotationAngle(cube_r83, -0.9163F, 0.0F, -0.1745F);
			cube_r83.cubeList.add(new ModelBox(cube_r83, 56, 56, 1.4804F, -0.2743F, -14.6559F, 1, 1, 3, 0.0F, false));
	
			bone38 = new ModelRenderer(this);
			bone38.setRotationPoint(-3.8351F, 10.36F, 6.925F);
			bone32.addChild(bone38);
			
	
			cube_r84 = new ModelRenderer(this);
			cube_r84.setRotationPoint(-3.501F, -1.5933F, 0.0F);
			bone38.addChild(cube_r84);
			setRotationAngle(cube_r84, -0.9163F, 0.0F, 0.6981F);
			cube_r84.cubeList.add(new ModelBox(cube_r84, 56, 56, -0.4846F, -0.2715F, -14.6522F, 1, 1, 3, 0.0F, true));
	
			cube_r85 = new ModelRenderer(this);
			cube_r85.setRotationPoint(-1.0F, 0.0F, 0.0F);
			bone38.addChild(cube_r85);
			setRotationAngle(cube_r85, -0.9163F, 0.0F, 0.4363F);
			cube_r85.cubeList.add(new ModelBox(cube_r85, 56, 56, -0.4899F, -0.2694F, -14.6495F, 1, 1, 3, 0.0F, true));
	
			cube_r86 = new ModelRenderer(this);
			cube_r86.setRotationPoint(2.8107F, 1.0211F, 0.0F);
			bone38.addChild(cube_r86);
			setRotationAngle(cube_r86, -0.9163F, 0.0F, 0.0873F);
			cube_r86.cubeList.add(new ModelBox(cube_r86, 56, 56, -0.4979F, -0.2681F, -14.6478F, 1, 1, 3, 0.0F, true));
	
			bone39 = new ModelRenderer(this);
			bone39.setRotationPoint(-6.225F, -7.525F, 0.0F);
			bone38.addChild(bone39);
			setRotationAngle(bone39, 0.0F, 0.0F, 0.7854F);
			
	
			cube_r87 = new ModelRenderer(this);
			cube_r87.setRotationPoint(-0.6393F, -1.5814F, 0.0F);
			bone39.addChild(cube_r87);
			setRotationAngle(cube_r87, -0.9163F, 0.0F, 0.7854F);
			cube_r87.cubeList.add(new ModelBox(cube_r87, 56, 56, -1.676F, 0.9349F, -13.0801F, 1, 1, 3, 0.0F, true));
	
			cube_r88 = new ModelRenderer(this);
			cube_r88.setRotationPoint(1.7438F, -0.1671F, 0.0F);
			bone39.addChild(cube_r88);
			setRotationAngle(cube_r88, -0.9163F, 0.0F, 0.4363F);
			cube_r88.cubeList.add(new ModelBox(cube_r88, 56, 56, -1.1615F, 0.8664F, -13.1693F, 1, 1, 3, 0.0F, true));
	
			cube_r89 = new ModelRenderer(this);
			cube_r89.setRotationPoint(6.5417F, 1.0719F, 0.0F);
			bone39.addChild(cube_r89);
			setRotationAngle(cube_r89, -0.9163F, 0.0F, 0.1745F);
			cube_r89.cubeList.add(new ModelBox(cube_r89, 56, 56, -3.6275F, 0.723F, -13.3561F, 1, 1, 3, 0.0F, true));
	
			bone40 = new ModelRenderer(this);
			bone40.setRotationPoint(4.7664F, -11.5114F, 6.925F);
			bone32.addChild(bone40);
			
	
			cube_r90 = new ModelRenderer(this);
			cube_r90.setRotationPoint(0.0F, 1.2808F, 0.0F);
			bone40.addChild(cube_r90);
			setRotationAngle(cube_r90, 0.9163F, 0.0F, 0.4363F);
			cube_r90.cubeList.add(new ModelBox(cube_r90, 56, 56, -0.4899F, -0.7041F, -14.684F, 1, 1, 3, 0.0F, false));
	
			cube_r91 = new ModelRenderer(this);
			cube_r91.setRotationPoint(-4.7978F, 0.0418F, 0.0F);
			bone40.addChild(cube_r91);
			setRotationAngle(cube_r91, 0.9163F, 0.0F, 0.1745F);
			cube_r91.cubeList.add(new ModelBox(cube_r91, 56, 56, 1.5042F, -0.703F, -14.6855F, 1, 1, 3, 0.0F, false));
	
			bone41 = new ModelRenderer(this);
			bone41.setRotationPoint(6.225F, 7.525F, 0.0F);
			bone40.addChild(bone41);
			setRotationAngle(bone41, 0.0F, 0.0F, 0.7854F);
			
	
			cube_r92 = new ModelRenderer(this);
			cube_r92.setRotationPoint(-0.1181F, 0.3233F, 0.0F);
			bone41.addChild(cube_r92);
			setRotationAngle(cube_r92, 0.9163F, 0.0F, 0.5236F);
			cube_r92.cubeList.add(new ModelBox(cube_r92, 56, 56, 0.5232F, -0.7136F, -14.6717F, 1, 1, 3, 0.0F, false));
	
			cube_r93 = new ModelRenderer(this);
			cube_r93.setRotationPoint(-1.0466F, -0.0136F, 0.0F);
			bone41.addChild(cube_r93);
			setRotationAngle(cube_r93, 0.9163F, 0.0F, 0.3491F);
			cube_r93.cubeList.add(new ModelBox(cube_r93, 56, 56, -0.4783F, -0.7112F, -14.6748F, 1, 1, 3, 0.0F, false));
	
			cube_r94 = new ModelRenderer(this);
			cube_r94.setRotationPoint(-4.9318F, -0.6986F, 0.0F);
			bone41.addChild(cube_r94);
			setRotationAngle(cube_r94, 0.9163F, 0.0F, 0.0F);
			cube_r94.cubeList.add(new ModelBox(cube_r94, 56, 56, -0.4831F, -0.707F, -14.6802F, 1, 1, 3, 0.0F, false));
	
			bone42 = new ModelRenderer(this);
			bone42.setRotationPoint(-8.6015F, 0.0F, 0.0F);
			bone40.addChild(bone42);
			
	
			cube_r95 = new ModelRenderer(this);
			cube_r95.setRotationPoint(-3.501F, 2.8742F, 0.0F);
			bone42.addChild(cube_r95);
			setRotationAngle(cube_r95, 0.9163F, 0.0F, -0.6981F);
			cube_r95.cubeList.add(new ModelBox(cube_r95, 56, 56, -0.5154F, -0.7062F, -14.6813F, 1, 1, 3, 0.0F, true));
	
			cube_r96 = new ModelRenderer(this);
			cube_r96.setRotationPoint(-1.0F, 1.2808F, 0.0F);
			bone42.addChild(cube_r96);
			setRotationAngle(cube_r96, 0.9163F, 0.0F, -0.4363F);
			cube_r96.cubeList.add(new ModelBox(cube_r96, 56, 56, -0.5101F, -0.7041F, -14.684F, 1, 1, 3, 0.0F, true));
	
			cube_r97 = new ModelRenderer(this);
			cube_r97.setRotationPoint(3.7978F, 0.0418F, 0.0F);
			bone42.addChild(cube_r97);
			setRotationAngle(cube_r97, 0.9163F, 0.0F, -0.1745F);
			cube_r97.cubeList.add(new ModelBox(cube_r97, 56, 56, -2.5042F, -0.703F, -14.6855F, 1, 1, 3, 0.0F, true));
	
			bone43 = new ModelRenderer(this);
			bone43.setRotationPoint(-6.225F, 7.525F, 0.0F);
			bone42.addChild(bone43);
			setRotationAngle(bone43, 0.0F, 0.0F, -0.7854F);
			
	
			cube_r98 = new ModelRenderer(this);
			cube_r98.setRotationPoint(0.8252F, 1.0304F, 0.0F);
			bone43.addChild(cube_r98);
			setRotationAngle(cube_r98, 0.9163F, 0.0F, -0.5236F);
			cube_r98.cubeList.add(new ModelBox(cube_r98, 56, 56, -2.0408F, -1.8896F, -13.1391F, 1, 1, 3, 0.0F, true));
	
			cube_r99 = new ModelRenderer(this);
			cube_r99.setRotationPoint(5.636F, -0.1662F, 0.0F);
			bone43.addChild(cube_r99);
			setRotationAngle(cube_r99, 0.9163F, 0.0F, -0.1745F);
			cube_r99.cubeList.add(new ModelBox(cube_r99, 56, 56, -3.6668F, -1.7063F, -13.3779F, 1, 1, 3, 0.0F, true));
	
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.2F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));
	
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.2F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));
	
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F, false));
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, false));
	
			bone21 = new ModelRenderer(this);
			bone21.setRotationPoint(1.9F, 0.475F, 2.25F);
			bipedRightLeg.addChild(bone21);
			
	
			cube_r100 = new ModelRenderer(this);
			cube_r100.setRotationPoint(-4.2328F, -0.9653F, -2.2F);
			bone21.addChild(cube_r100);
			setRotationAngle(cube_r100, 0.2618F, -1.5708F, 0.0F);
			cube_r100.cubeList.add(new ModelBox(cube_r100, 8, 61, -2.0F, -0.5237F, -0.2164F, 4, 3, 0, 0.1F, false));
	
			cube_r101 = new ModelRenderer(this);
			cube_r101.setRotationPoint(-3.9785F, -0.0683F, -0.1979F);
			bone21.addChild(cube_r101);
			setRotationAngle(cube_r101, -0.3054F, 2.3562F, 0.0F);
			cube_r101.cubeList.add(new ModelBox(cube_r101, 11, 61, -0.5F, -1.325F, 0.075F, 1, 3, 0, 0.0F, false));
	
			cube_r102 = new ModelRenderer(this);
			cube_r102.setRotationPoint(-3.9785F, -0.0683F, -4.3021F);
			bone21.addChild(cube_r102);
			setRotationAngle(cube_r102, 0.3054F, -2.3562F, 0.0F);
			cube_r102.cubeList.add(new ModelBox(cube_r102, 8, 61, -0.5F, -1.325F, -0.075F, 1, 3, 0, 0.025F, false));
	
			cube_r103 = new ModelRenderer(this);
			cube_r103.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone21.addChild(cube_r103);
			setRotationAngle(cube_r103, 0.2182F, 0.0F, 0.0F);
			cube_r103.cubeList.add(new ModelBox(cube_r103, 8, 61, -4.0F, -1.475F, 0.0F, 4, 3, 0, 0.1F, false));
	
			cube_r104 = new ModelRenderer(this);
			cube_r104.setRotationPoint(0.0F, 0.0F, -4.5F);
			bone21.addChild(cube_r104);
			setRotationAngle(cube_r104, -0.2182F, 0.0F, 0.0F);
			cube_r104.cubeList.add(new ModelBox(cube_r104, 3, 55, -4.0F, -1.475F, 0.0F, 4, 3, 0, 0.1F, false));
	
			bone16 = new ModelRenderer(this);
			bone16.setRotationPoint(-1.7182F, 2.8219F, -2.2464F);
			bipedRightLeg.addChild(bone16);
			
	
			cube_r105 = new ModelRenderer(this);
			cube_r105.setRotationPoint(0.0F, -2.0F, 1.0F);
			bone16.addChild(cube_r105);
			setRotationAngle(cube_r105, -0.2618F, 1.309F, 0.0F);
			cube_r105.cubeList.add(new ModelBox(cube_r105, 1, 60, -1.2841F, 0.1586F, -0.3536F, 2, 4, 0, 0.0F, false));
	
			cube_r106 = new ModelRenderer(this);
			cube_r106.setRotationPoint(0.0F, -1.0F, 0.0F);
			bone16.addChild(cube_r106);
			setRotationAngle(cube_r106, -0.1745F, 0.3054F, 0.0436F);
			cube_r106.cubeList.add(new ModelBox(cube_r106, 2, 61, -0.4584F, 0.0941F, -0.0499F, 1, 3, 0, 0.0F, false));
	
			cube_r107 = new ModelRenderer(this);
			cube_r107.setRotationPoint(2.2434F, -0.4324F, -0.1019F);
			bone16.addChild(cube_r107);
			setRotationAngle(cube_r107, -0.1745F, 0.0F, 0.0F);
			cube_r107.cubeList.add(new ModelBox(cube_r107, 1, 60, -1.75F, -1.4836F, -0.0695F, 3, 4, 0, 0.0F, false));
	
			bone19 = new ModelRenderer(this);
			bone19.setRotationPoint(0.0F, 0.0F, 4.4929F);
			bone16.addChild(bone19);
			
	
			cube_r108 = new ModelRenderer(this);
			cube_r108.setRotationPoint(0.0F, -2.0F, -1.0F);
			bone19.addChild(cube_r108);
			setRotationAngle(cube_r108, 0.2618F, -1.309F, 0.0F);
			cube_r108.cubeList.add(new ModelBox(cube_r108, 1, 60, -1.2841F, 0.1646F, 0.3536F, 2, 4, 0, 0.0F, false));
	
			cube_r109 = new ModelRenderer(this);
			cube_r109.setRotationPoint(0.0F, -1.0F, 0.0F);
			bone19.addChild(cube_r109);
			setRotationAngle(cube_r109, 0.1745F, -0.3054F, 0.0436F);
			cube_r109.cubeList.add(new ModelBox(cube_r109, 2, 61, -0.4584F, 0.0941F, 0.0499F, 1, 3, 0, 0.0F, false));
	
			cube_r110 = new ModelRenderer(this);
			cube_r110.setRotationPoint(2.2434F, -1.9324F, 0.1019F);
			bone19.addChild(cube_r110);
			setRotationAngle(cube_r110, 0.1745F, 0.0F, 0.0F);
			cube_r110.cubeList.add(new ModelBox(cube_r110, 1, 60, -1.75F, 0.0036F, -0.1263F, 3, 4, 0, 0.0F, false));
	
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F, true));
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, true));
	
			bone20 = new ModelRenderer(this);
			bone20.setRotationPoint(2.0785F, 0.4067F, -2.0521F);
			bipedLeftLeg.addChild(bone20);
			
	
			cube_r111 = new ModelRenderer(this);
			cube_r111.setRotationPoint(-3.9785F, 0.0683F, 4.3021F);
			bone20.addChild(cube_r111);
			setRotationAngle(cube_r111, 0.2182F, 0.0F, 0.0F);
			cube_r111.cubeList.add(new ModelBox(cube_r111, 8, 61, 0.0F, -1.475F, 0.0F, 4, 3, 0, 0.1F, true));
	
			cube_r112 = new ModelRenderer(this);
			cube_r112.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone20.addChild(cube_r112);
			setRotationAngle(cube_r112, 0.3054F, 2.3562F, 0.0F);
			cube_r112.cubeList.add(new ModelBox(cube_r112, 12, 61, -0.5F, -1.35F, -0.075F, 1, 3, 0, 0.025F, true));
	
			cube_r113 = new ModelRenderer(this);
			cube_r113.setRotationPoint(0.2435F, -0.9459F, 2.1021F);
			bone20.addChild(cube_r113);
			setRotationAngle(cube_r113, 0.2618F, 1.5708F, 0.0F);
			cube_r113.cubeList.add(new ModelBox(cube_r113, 8, 61, -2.0F, -0.5237F, -0.2164F, 4, 3, 0, 0.1F, true));
	
			cube_r114 = new ModelRenderer(this);
			cube_r114.setRotationPoint(0.0F, 0.0F, 4.1043F);
			bone20.addChild(cube_r114);
			setRotationAngle(cube_r114, -0.3054F, -2.3562F, 0.0F);
			cube_r114.cubeList.add(new ModelBox(cube_r114, 12, 61, -0.5F, -1.375F, 0.075F, 1, 3, 0, 0.035F, true));
	
			cube_r115 = new ModelRenderer(this);
			cube_r115.setRotationPoint(-3.9785F, 0.0683F, -0.1979F);
			bone20.addChild(cube_r115);
			setRotationAngle(cube_r115, -0.2182F, 0.0F, 0.0F);
			cube_r115.cubeList.add(new ModelBox(cube_r115, 1, 49, 0.0F, -1.475F, 0.0F, 4, 3, 0, 0.1F, true));
	
			bone17 = new ModelRenderer(this);
			bone17.setRotationPoint(1.7182F, 2.8219F, -2.2464F);
			bipedLeftLeg.addChild(bone17);
			
	
			cube_r116 = new ModelRenderer(this);
			cube_r116.setRotationPoint(0.0F, -2.0F, 1.0F);
			bone17.addChild(cube_r116);
			setRotationAngle(cube_r116, -0.2618F, -1.309F, 0.0F);
			cube_r116.cubeList.add(new ModelBox(cube_r116, 1, 60, -0.7159F, 0.1646F, -0.3536F, 2, 4, 0, 0.0F, true));
	
			cube_r117 = new ModelRenderer(this);
			cube_r117.setRotationPoint(0.0F, -0.5F, 0.0F);
			bone17.addChild(cube_r117);
			setRotationAngle(cube_r117, -0.1745F, -0.3054F, -0.0436F);
			cube_r117.cubeList.add(new ModelBox(cube_r117, 2, 61, -0.5208F, -0.4029F, -0.0999F, 1, 3, 0, 0.0F, true));
	
			cube_r118 = new ModelRenderer(this);
			cube_r118.setRotationPoint(-2.2434F, -1.4324F, -0.1019F);
			bone17.addChild(cube_r118);
			setRotationAngle(cube_r118, -0.1745F, 0.0F, 0.0F);
			cube_r118.cubeList.add(new ModelBox(cube_r118, 1, 60, -1.25F, -0.4987F, 0.1042F, 3, 4, 0, 0.0F, true));
	
			bone18 = new ModelRenderer(this);
			bone18.setRotationPoint(0.0F, 0.0F, 4.4929F);
			bone17.addChild(bone18);
			
	
			cube_r119 = new ModelRenderer(this);
			cube_r119.setRotationPoint(0.0F, -2.0F, -1.0F);
			bone18.addChild(cube_r119);
			setRotationAngle(cube_r119, 0.2618F, 1.309F, 0.0F);
			cube_r119.cubeList.add(new ModelBox(cube_r119, 1, 60, -0.7159F, 0.1646F, 0.3536F, 2, 4, 0, 0.0F, true));
	
			cube_r120 = new ModelRenderer(this);
			cube_r120.setRotationPoint(0.0F, -1.0F, 0.0F);
			bone18.addChild(cube_r120);
			setRotationAngle(cube_r120, 0.1745F, 0.3054F, -0.0436F);
			cube_r120.cubeList.add(new ModelBox(cube_r120, 2, 61, -0.5416F, 0.0941F, 0.0499F, 1, 3, 0, 0.0F, true));
	
			cube_r121 = new ModelRenderer(this);
			cube_r121.setRotationPoint(-2.2434F, -1.9324F, 0.1019F);
			bone18.addChild(cube_r121);
			setRotationAngle(cube_r121, 0.1745F, 0.0F, 0.0F);
			cube_r121.cubeList.add(new ModelBox(cube_r121, 1, 60, -1.25F, 0.0036F, -0.1263F, 3, 4, 0, 0.0F, true));
		}

		@Override
		public void render(Entity entityIn, float f0, float f1, float f2, float f3, float f4, float f5) {
			if (this.bipedRightLeg.showModel || this.bipedLeftLeg.showModel) {
				this.engine.showModel = false;
			}
			if (entityIn instanceof AbstractClientPlayer && ((AbstractClientPlayer)entityIn).getSkinType().equals("slim")) {
				this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
			}
			super.render(entityIn, f0, f1, f2, f3, f4, f5);
		}
	
		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
