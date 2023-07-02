
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.util.EnumHelper;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.entity.AbstractClientPlayer;

import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemNinjaArmor extends ElementsNarutomodMod.ModElement {
	private static final ItemArmor.ArmorMaterial ENUMA = EnumHelper.addArmorMaterial("NINJA_ARMOR", "narutomod:sasuke_",
	 100, new int[]{2, 5, 6, 2}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1f);

	public ItemNinjaArmor(ElementsNarutomodMod instance) {
		super(instance, 746);
	}

	public static abstract class Base extends ItemArmor {
		private final ArmorData armorData;

		public Base(Type type, EntityEquipmentSlot equipmentSlotIn) {
			this(type, ENUMA, equipmentSlotIn);
		}

		public Base(Type type, ItemArmor.ArmorMaterial enuma, EntityEquipmentSlot equipmentSlotIn) {
			super(enuma, 0, equipmentSlotIn);
			this.armorData = this.setArmorData(type, equipmentSlotIn);
		}

		protected abstract ArmorData setArmorData(Type type, EntityEquipmentSlot slotIn);

		@Override
		@SideOnly(Side.CLIENT)
		public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
			this.armorData.model.isSneak = living.isSneaking();
			this.armorData.model.isRiding = living.isRiding();
			this.armorData.model.isChild = living.isChild();
			return this.armorData.model;
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
			this.armorData.setSlotVisible();
			return this.armorData.texture;
		}
	}

	public static abstract class ArmorData {
		//private final EntityEquipmentSlot slot;
		protected String texture;
		@SideOnly(Side.CLIENT)
		protected ModelBiped model;

		public ArmorData() {
			//this.slot = slotIn;
			this.init();
		}

		protected void init() { }

		public void setSlotVisible() { }
	}
	
	// Made with Blockbench 3.9.3
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public static class ModelNinjaArmor extends ModelBiped {
		//private final ModelRenderer bipedHead;
		public final ModelRenderer headwear;
		//private final ModelRenderer bipedHeadwear;
		//private final ModelRenderer mask;
		public final ModelRenderer collar;
		//private final ModelRenderer bipedBody;
		public final ModelRenderer shirt;
		//private final ModelRenderer headbandWaist;
		public final ModelRenderer vest;
		public final ModelRenderer vestGroup;
		//private final ModelRenderer LeafVest;
		//private final ModelRenderer SandVest;
		//private final ModelRenderer StoneVest;
		//private final ModelRenderer MistVest;
		//private final ModelRenderer CloudVest;
		//private final ModelRenderer AnbuVest;
		//private final ModelRenderer bipedRightArm;
		public final ModelRenderer shirtRightArm;
		public final ModelRenderer rightArmVestLayer;
		public final ModelRenderer rightShoulder;
		//private final ModelRenderer headbandRightArm;
		//private final ModelRenderer bipedLeftArm;
		public final ModelRenderer shirtLeftArm;
		public final ModelRenderer leftArmVestLayer;
		public final ModelRenderer leftShoulder;
		//private final ModelRenderer headbandLeftArm;
		//private final ModelRenderer bipedRightLeg;
		public final ModelRenderer rightLegLayer;
		public final ModelRenderer stoneCloth;
		public final ModelRenderer rightLegPad;
		//private final ModelRenderer headbandRightLeg;
		//private final ModelRenderer bipedLeftLeg;
		public final ModelRenderer leftLegLayer;
		public final ModelRenderer leftLegPad;
		//private final ModelRenderer headbandLeftLeg;
		private ModelBiped wearerModel;
	
		public ModelNinjaArmor() {
			this(Type.KUMO);
		}

		public ModelNinjaArmor(Type type) {
			textureWidth = 64;
			textureHeight = 64;
	
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));
	
			headwear = new ModelRenderer(this);
			if (type == Type.AME) {
				headwear.setRotationPoint(0.0F, -1.125F, -4.4F);
				bipedHead.addChild(headwear);
				setRotationAngle(headwear, 0.0873F, 0.0F, 0.0F);
				headwear.cubeList.add(new ModelBox(headwear, 39, 9, -2.0F, -1.6F, -0.9F, 4, 3, 2, -0.2F, false));
				ModelRenderer bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-2.1645F, -0.6361F, -0.2913F);
				headwear.addChild(bone2);
				setRotationAngle(bone2, -0.2618F, 0.0F, 0.1309F);
				bone2.cubeList.add(new ModelBox(bone2, 50, 11, -0.5F, -0.1F, -0.5F, 1, 2, 1, -0.1F, false));
				bone2.cubeList.add(new ModelBox(bone2, 54, 11, -0.5F, 1.7F, -0.5F, 1, 2, 1, 0.2F, false));
			} else if (type == Type.SAMURAI) {
				bipedHead.cubeList.add(new ModelBox(headwear, 0, 48, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.26F, false));
				headwear.setRotationPoint(0.0F, 0.0F, -2.925F);
				bipedHead.addChild(headwear);
				setRotationAngle(headwear, 0.6545F, 0.0F, 0.0F);				
				ModelRenderer cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.7071F, -0.775F, 0.0F);
				headwear.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.0F, 0.0F, -0.7854F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 28, 0, -2.0F, -1.975F, -0.975F, 3, 3, 2, 0.3F, false));
			} else if (type == Type.SUNA || type == Type.IWA || type == Type.KUMO) {
				bipedHead.addChild(headwear);
				headwear.cubeList.add(new ModelBox(headwear, 0, 48, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.26F, false));
			} else {
				headwear.showModel = false;
			}

			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			
			collar = new ModelRenderer(this);
			if (type == Type.KONOHA || type == Type.SUNA || type == Type.WAR1 || type == Type.OBITOWAR) {
				collar.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.addChild(collar);
				collar.cubeList.add(new ModelBox(collar, 32, 7, -4.0F, -1.5F, -4.0F, 8, 1, 8, 1.0F, false));
			} else {
				collar.showModel = false;
			}
	
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			
	
			shirt = new ModelRenderer(this);
			shirt.setRotationPoint(0.0F, 24.0F, 0.0F);
			bipedBody.addChild(shirt);
			shirt.cubeList.add(new ModelBox(shirt, 16, 16, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.1F, false));
	
			//headbandWaist = new ModelRenderer(this);
			//headbandWaist.setRotationPoint(0.0F, 24.0F, 0.0F);
			//bipedBody.addChild(headbandWaist);
			//headbandWaist.cubeList.add(new ModelBox(headbandWaist, 4, 3, -4.0F, -19.0F, -2.0F, 8, 8, 4, 0.35F, false));
	
			vest = new ModelRenderer(this);
			vest.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.addChild(vest);
			vest.cubeList.add(new ModelBox(vest, 40, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.35F, false));
			vest.cubeList.add(new ModelBox(vest, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F, false));
			vest.cubeList.add(new ModelBox(vest, 52, 0, 0.1F, 8.3F, 1.75F, 4, 4, 2, -0.5F, false));
	
			vestGroup = new ModelRenderer(this);
			vestGroup.setRotationPoint(0.0F, 0.0F, 0.0F);
			switch (type) {
				case KONOHA:
					vest.addChild(vestGroup);
					vestGroup.cubeList.add(new ModelBox(vestGroup, 26, 0, -4.3F, 2.5F, -3.35F, 4, 5, 3, -0.7F, false));
					vestGroup.cubeList.add(new ModelBox(vestGroup, 26, 0, 0.3F, 2.5F, -3.35F, 4, 5, 3, -0.7F, true));
					break;
				case SUNA:
					vest.addChild(vestGroup);
					vestGroup.cubeList.add(new ModelBox(vestGroup, 26, 0, -4.3F, 5.1F, -3.35F, 4, 5, 3, -0.7F, false));
					vestGroup.cubeList.add(new ModelBox(vestGroup, 26, 0, 0.3F, 5.1F, -3.35F, 4, 5, 3, -0.7F, true));
					break;
				case KIRI:
					vest.addChild(vestGroup);
					vestGroup.cubeList.add(new ModelBox(vestGroup, 48, 8, -4.0F, 10.3F, -2.375F, 8, 3, 0, 0.0F, false));
					vestGroup.cubeList.add(new ModelBox(vestGroup, 48, 8, -4.0F, 10.3F, 2.375F, 8, 3, 0, 0.0F, false));
					break;
				case KUMO:
					vest.addChild(vestGroup);
					vestGroup.cubeList.add(new ModelBox(vestGroup, 40, 9, -4.0F, 12.0F, -2.0F, 8, 3, 4, 0.4F, true));
					break;
				case SAMURAI:
					vest.addChild(vestGroup);
					ModelRenderer flapRight = new ModelRenderer(this);
					flapRight.setRotationPoint(-4.25F, 10.05F, 0.0F);
					vestGroup.addChild(flapRight);
					setRotationAngle(flapRight, 0.0F, 0.0F, -1.309F);
					flapRight.cubeList.add(new ModelBox(flapRight, 25, 50, -7.3F, 0.425F, -2.0F, 7, 1, 4, 0.34F, true));
					ModelRenderer flapLeft = new ModelRenderer(this);
					flapLeft.setRotationPoint(4.25F, 10.05F, 0.0F);
					vestGroup.addChild(flapLeft);
					setRotationAngle(flapLeft, 0.0F, 0.0F, 1.309F);
					flapLeft.cubeList.add(new ModelBox(flapLeft, 25, 50, 0.3F, 0.425F, -2.0F, 7, 1, 4, 0.34F, false));
					break;
				case OTO:
					ModelRenderer neckwear = new ModelRenderer(this);
					neckwear.setRotationPoint(0.0F, 0.0F, 0.0F);
					vest.addChild(neckwear);
					setRotationAngle(neckwear, 0.0873F, 0.0F, 0.0F);
					neckwear.cubeList.add(new ModelBox(neckwear, 32, 6, -4.0F, -0.25F, -3.0F, 8, 2, 6, 0.6F, false));
				case OBITOWAR:
					vest.addChild(vestGroup);
					ModelRenderer bone = new ModelRenderer(this);
					bone.setRotationPoint(0.0F, 10.75F, -2.35F);
					vestGroup.addChild(bone);
					setRotationAngle(bone, -0.2793F, 0.0F, 0.0F);
					bone.cubeList.add(new ModelBox(bone, 24, 52, -4.0F, 0.5F, 0.5F, 8, 8, 4, 0.5F, false));
					ModelRenderer bone3 = new ModelRenderer(this);
					bone3.setRotationPoint(0.0F, 10.75F, 2.35F);
					vestGroup.addChild(bone3);
					setRotationAngle(bone3, 0.2793F, 0.0F, 0.0F);
					bone3.cubeList.add(new ModelBox(bone3, 0, 52, -4.0F, 0.5F, -4.5F, 8, 8, 4, 0.5F, false));
					break;
				default:
					vestGroup.showModel = false;
					break;
			}
		
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			
	
			shirtRightArm = new ModelRenderer(this);
			if (type == Type.IWA) {
				shirtRightArm.showModel = false;
			} else {
				shirtRightArm.setRotationPoint(5.0F, 22.0F, 0.0F);
				bipedRightArm.addChild(shirtRightArm);
				shirtRightArm.cubeList.add(new ModelBox(shirtRightArm, 40, 16, -8.0F, -24.0F, -2.0F, 4, 12, 4, 0.1F, false));
			}
	
			rightArmVestLayer = new ModelRenderer(this);
			rightArmVestLayer.setRotationPoint(5.0F, 22.0F, 0.0F);
			bipedRightArm.addChild(rightArmVestLayer);
			rightArmVestLayer.cubeList.add(new ModelBox(rightArmVestLayer, 48, 48, -8.0F, -24.0F, -2.0F, 4, 12, 4, 0.35F, true));

			rightShoulder = new ModelRenderer(this);
			if (type == Type.SUNA || type == Type.KIRI || type == Type.WAR1 || type == Type.SAMURAI) {
				rightShoulder.setRotationPoint(-4.5F, -25.25F, 0.0F);
				rightArmVestLayer.addChild(rightShoulder);
				setRotationAngle(rightShoulder, 0.0F, 0.0F, -0.3054F);
				rightShoulder.cubeList.add(new ModelBox(rightShoulder, 36, 0, -4.3F, 0.3F, -2.0F, 4, 1, 4, 0.4F, true));
				if (type == Type.WAR1 || type == Type.SAMURAI) {
					ModelRenderer war1RightShoulder = new ModelRenderer(this);
					war1RightShoulder.setRotationPoint(-2.0F, 0.0F, 0.0F);
					rightShoulder.addChild(war1RightShoulder);
					setRotationAngle(war1RightShoulder, 0.0F, 0.0F, -0.3491F);
					war1RightShoulder.cubeList.add(new ModelBox(war1RightShoulder, 36, 0, -4.3F, 0.3F, -2.0F, 4, 1, 4, 0.2F, true));
			
					ModelRenderer rightShoulder3 = new ModelRenderer(this);
					rightShoulder3.setRotationPoint(-2.0F, 0.0F, 0.0F);
					war1RightShoulder.addChild(rightShoulder3);
					setRotationAngle(rightShoulder3, 0.0F, 0.0F, -0.3491F);
					rightShoulder3.cubeList.add(new ModelBox(rightShoulder3, 36, 0, -4.3F, 0.3F, -2.0F, 4, 1, 4, 0.1F, true));
				}
			} else {
				rightShoulder.showModel = false;
			}
	
			//headbandRightArm = new ModelRenderer(this);
			//headbandRightArm.setRotationPoint(-5.0F, -6.0F, 0.0F);
			//bipedRightArm.addChild(headbandRightArm);
			//setRotationAngle(headbandRightArm, 0.0F, 1.5708F, 0.0F);
			//headbandRightArm.cubeList.add(new ModelBox(headbandRightArm, 0, 0, -4.0F, 2.825F, -0.05F, 8, 8, 8, -1.65F, false));
	
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			
			shirtLeftArm = new ModelRenderer(this);
			shirtLeftArm.setRotationPoint(-5.0F, 22.0F, 0.0F);
			bipedLeftArm.addChild(shirtLeftArm);
			shirtLeftArm.cubeList.add(new ModelBox(shirtLeftArm, 40, 16, 4.0F, -24.0F, -2.0F, 4, 12, 4, 0.1F, true));
	
			leftArmVestLayer = new ModelRenderer(this);
			leftArmVestLayer.setRotationPoint(1.0F, 4.0F, 0.0F);
			bipedLeftArm.addChild(leftArmVestLayer);
			leftArmVestLayer.cubeList.add(new ModelBox(leftArmVestLayer, 48, 48, -2.0F, -6.0F, -2.0F, 4, 12, 4, 0.35F, false));
	
			leftShoulder = new ModelRenderer(this);
			if (type == Type.SUNA || type == Type.KIRI || type == Type.WAR1 || type == Type.SAMURAI) {
				leftShoulder.setRotationPoint(-1.5F, -7.25F, 0.0F);
				leftArmVestLayer.addChild(leftShoulder);
				setRotationAngle(leftShoulder, 0.0F, 0.0F, 0.3054F);
				leftShoulder.cubeList.add(new ModelBox(leftShoulder, 36, 0, 0.3F, 0.3F, -2.0F, 4, 1, 4, 0.4F, false));
				if (type == Type.WAR1 || type == Type.SAMURAI) {
					ModelRenderer war1LeftShoulder = new ModelRenderer(this);
					war1LeftShoulder.setRotationPoint(2.0F, 0.0F, 0.0F);
					leftShoulder.addChild(war1LeftShoulder);
					setRotationAngle(war1LeftShoulder, 0.0F, 0.0F, 0.3491F);
					war1LeftShoulder.cubeList.add(new ModelBox(war1LeftShoulder, 36, 0, 0.3F, 0.3F, -2.0F, 4, 1, 4, 0.2F, false));
			
					ModelRenderer leftShoulder3 = new ModelRenderer(this);
					leftShoulder3.setRotationPoint(2.0F, 0.0F, 0.0F);
					war1LeftShoulder.addChild(leftShoulder3);
					setRotationAngle(leftShoulder3, 0.0F, 0.0F, 0.3491F);
					leftShoulder3.cubeList.add(new ModelBox(leftShoulder3, 36, 0, 0.3F, 0.3F, -2.0F, 4, 1, 4, 0.1F, false));
				}
			} else {
				leftShoulder.showModel = false;
			}
	
			//headbandLeftArm = new ModelRenderer(this);
			//headbandLeftArm.setRotationPoint(5.0F, -6.0F, 0.0F);
			//bipedLeftArm.addChild(headbandLeftArm);
			//setRotationAngle(headbandLeftArm, 0.0F, -1.5708F, 0.0F);
			//headbandLeftArm.cubeList.add(new ModelBox(headbandLeftArm, 0, 0, -4.0F, 2.825F, -0.05F, 8, 8, 8, -1.65F, false));
	
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, true));

			rightLegLayer = new ModelRenderer(this);
			rightLegLayer.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedRightLeg.addChild(rightLegLayer);
			rightLegLayer.cubeList.add(new ModelBox(rightLegLayer, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, false));
			rightLegLayer.cubeList.add(new ModelBox(rightLegLayer, 0, 0, -2.85F, 1.0F, -1.0F, 1, 4, 2, 0.0F, false));
	
			stoneCloth = new ModelRenderer(this);
			if (type == Type.IWA) {
				stoneCloth.setRotationPoint(0.0F, 6.0F, 0.0F);
				bipedRightLeg.addChild(stoneCloth);
				setRotationAngle(stoneCloth, 0.0F, 0.0F, 0.1745F);
				stoneCloth.cubeList.add(new ModelBox(stoneCloth, 36, 0, -3.2F, -6.8F, -2.0F, 4, 7, 4, 0.35F, false));
			} else {
				stoneCloth.showModel = false;
			}
	
			rightLegPad = new ModelRenderer(this);
			if (type == Type.WAR1) {
				rightLegPad.setRotationPoint(-2.35F, -2.25F, 0.0F);
				bipedRightLeg.addChild(rightLegPad);
				setRotationAngle(rightLegPad, 0.0F, 0.0F, -1.309F);
				rightLegPad.cubeList.add(new ModelBox(rightLegPad, 36, 0, -4.3F, 0.3F, -2.0F, 4, 1, 4, 0.34F, true));
		
				ModelRenderer rightLegPad1 = new ModelRenderer(this);
				rightLegPad1.setRotationPoint(-2.0F, 0.0F, 0.0F);
				rightLegPad.addChild(rightLegPad1);
				setRotationAngle(rightLegPad1, 0.0F, 0.0F, -0.0873F);
				rightLegPad1.cubeList.add(new ModelBox(rightLegPad1, 36, 0, -4.3F, 0.3F, -2.0F, 4, 1, 4, 0.2F, true));
		
				ModelRenderer rightLegPad2 = new ModelRenderer(this);
				rightLegPad2.setRotationPoint(-2.0F, 0.0F, 0.0F);
				rightLegPad1.addChild(rightLegPad2);
				setRotationAngle(rightLegPad2, 0.0F, 0.0F, -0.0873F);
				rightLegPad2.cubeList.add(new ModelBox(rightLegPad2, 36, 0, -4.3F, 0.3F, -2.0F, 4, 1, 4, 0.1F, true));
			} else {
				rightLegPad.showModel = false;
			}

			//headbandRightLeg = new ModelRenderer(this);
			//headbandRightLeg.setRotationPoint(-8.1F, -16.0F, 0.0F);
			//bipedRightLeg.addChild(headbandRightLeg);
			//setRotationAngle(headbandRightLeg, 0.0F, 1.5708F, 0.0F);
			//headbandRightLeg.cubeList.add(new ModelBox(headbandRightLeg, 0, 0, -4.0F, 13.225F, 3.95F, 8, 8, 8, -1.65F, false));
	
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));
	
			leftLegLayer = new ModelRenderer(this);
			if (type == Type.KIRI || type == Type.KUMO || type == Type.JUMPSUIT || type == Type.SAMURAI) {
				leftLegLayer.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedLeftLeg.addChild(leftLegLayer);
				leftLegLayer.cubeList.add(new ModelBox(leftLegLayer, 0, 32, -0.1F, -12.0F, -2.0F, 4, 12, 4, 0.3F, true));
			} else {
				leftLegLayer.showModel = false;
			}

			leftLegPad = new ModelRenderer(this);
			if (type == Type.WAR1) {
				leftLegPad.setRotationPoint(2.35F, -2.25F, 0.0F);
				bipedLeftLeg.addChild(leftLegPad);
				setRotationAngle(leftLegPad, 0.0F, 0.0F, 1.309F);
				leftLegPad.cubeList.add(new ModelBox(leftLegPad, 36, 0, 0.3F, 0.3F, -2.0F, 4, 1, 4, 0.34F, false));
		
				ModelRenderer leftLegPad1 = new ModelRenderer(this);
				leftLegPad1.setRotationPoint(2.0F, 0.0F, 0.0F);
				leftLegPad.addChild(leftLegPad1);
				setRotationAngle(leftLegPad1, 0.0F, 0.0F, 0.0873F);
				leftLegPad1.cubeList.add(new ModelBox(leftLegPad1, 36, 0, 0.3F, 0.3F, -2.0F, 4, 1, 4, 0.2F, false));
		
				ModelRenderer leftLegPad2 = new ModelRenderer(this);
				leftLegPad2.setRotationPoint(2.0F, 0.0F, 0.0F);
				leftLegPad1.addChild(leftLegPad2);
				setRotationAngle(leftLegPad2, 0.0F, 0.0F, 0.0873F);
				leftLegPad2.cubeList.add(new ModelBox(leftLegPad2, 36, 0, 0.3F, 0.3F, -2.0F, 4, 1, 4, 0.1F, false));
			} else {
				leftLegPad.showModel = false;
			}
			//headbandLeftLeg = new ModelRenderer(this);
			//headbandLeftLeg.setRotationPoint(8.1F, -16.0F, 0.0F);
			//bipedLeftLeg.addChild(headbandLeftLeg);
			//setRotationAngle(headbandLeftLeg, 0.0F, -1.5708F, 0.0F);
			//headbandLeftLeg.cubeList.add(new ModelBox(headbandLeftLeg, 0, 0, -4.0F, 13.225F, 3.95F, 8, 8, 8, -1.65F, false));
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		@Override
		public void setModelAttributes(ModelBase model) {
			super.setModelAttributes(model);
			if (model instanceof ModelBiped) {
				this.wearerModel = (ModelBiped)model;
			}
		}

		@Override
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
			if (entity instanceof AbstractClientPlayer && ((AbstractClientPlayer)entity).getSkinType().equals("slim")) {
				this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
			}
			super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
			if (!(entity instanceof AbstractClientPlayer) && this.wearerModel != null) {
				copyModelAngles(this.wearerModel.bipedLeftArm, this.bipedLeftArm);
				copyModelAngles(this.wearerModel.bipedRightArm, this.bipedRightArm);
			}
		}
	}

	public enum Type {
		KONOHA,
		IWA,
		SUNA,
		KIRI,
		KUMO,
		ANBU,
		JUMPSUIT,
		FISHNET,
		AME,
		WAR1,
		SAMURAI,
		OTO,
		OBITOWAR,
		OTHER
	}
}
