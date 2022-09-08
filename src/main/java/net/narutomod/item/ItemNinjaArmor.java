
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.util.EnumHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.entity.AbstractClientPlayer;

import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemNinjaArmor extends ElementsNarutomodMod.ModElement {
	private static final ItemArmor.ArmorMaterial ENUMA = EnumHelper.addArmorMaterial("NINJA_ARMOR", "narutomod:sasuke_", 5, new int[]{2, 5, 6, 2}, 0, null, 1f);

	public ItemNinjaArmor(ElementsNarutomodMod instance) {
		super(instance, 746);
	}

	public static abstract class Base extends ItemArmor {
		private final ArmorData armorData;

		public Base(Type type, EntityEquipmentSlot equipmentSlotIn) {
			super(ENUMA, 0, equipmentSlotIn);
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
		//private final ModelRenderer bipedHeadwear;
		private final ModelRenderer mask;
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
		public final ModelRenderer StoneCloth;
		//private final ModelRenderer headbandRightLeg;
		//private final ModelRenderer bipedLeftLeg;
		public final ModelRenderer leftLegLayer;
		//private final ModelRenderer headbandLeftLeg;
	
		public ModelNinjaArmor() {
			this(Type.KUMO);
		}

		public ModelNinjaArmor(Type type) {
			textureWidth = 64;
			textureHeight = 64;
	
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 48, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.15F, false));
	
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			
			mask = new ModelRenderer(this);
			if (type == Type.AME) {
				mask.setRotationPoint(0.0F, -1.125F, -4.4F);
				bipedHeadwear.addChild(mask);
				setRotationAngle(mask, 0.0873F, 0.0F, 0.0F);
				mask.cubeList.add(new ModelBox(mask, 39, 9, -2.0F, -1.6F, -0.9F, 4, 3, 2, -0.2F, false));
		
				ModelRenderer bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-2.1645F, -0.6361F, -0.2913F);
				mask.addChild(bone2);
				setRotationAngle(bone2, -0.2618F, 0.0F, 0.1309F);
				bone2.cubeList.add(new ModelBox(bone2, 50, 11, -0.5F, -0.1F, -0.5F, 1, 2, 1, -0.1F, false));
				bone2.cubeList.add(new ModelBox(bone2, 54, 11, -0.5F, 1.7F, -0.5F, 1, 2, 1, 0.2F, false));
			} else {
				mask.showModel = false;
			}

			collar = new ModelRenderer(this);
			if (type == Type.KONOHA || type == Type.SUNA || type == Type.JUMPSUIT) {
				collar.setRotationPoint(0.0F, 24.0F, 0.0F);
				bipedHeadwear.addChild(collar);
				collar.cubeList.add(new ModelBox(collar, 34, 8, -4.0F, -25.1F, -3.1F, 8, 1, 7, 0.8F, false));
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
			vest.setRotationPoint(0.0F, 24.0F, 0.0F);
			bipedBody.addChild(vest);
			vest.cubeList.add(new ModelBox(vest, 40, 32, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.2F, false));
			vest.cubeList.add(new ModelBox(vest, 16, 32, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.4F, false));
			vest.cubeList.add(new ModelBox(vest, 52, 0, 0.1F, -15.7F, 1.75F, 4, 4, 2, -0.5F, false));
	
			vestGroup = new ModelRenderer(this);
			switch (type) {
				case KONOHA:
					vestGroup.setRotationPoint(0.0F, 0.0F, 0.0F);
					vest.addChild(vestGroup);
					vestGroup.cubeList.add(new ModelBox(vestGroup, 26, 0, -4.3F, -21.5F, -3.1F, 4, 5, 3, -0.7F, false));
					vestGroup.cubeList.add(new ModelBox(vestGroup, 26, 0, 0.3F, -21.5F, -3.1F, 4, 5, 3, -0.7F, true));
					break;
				case SUNA:
					vestGroup.setRotationPoint(0.0F, 0.0F, 0.0F);
					vest.addChild(vestGroup);
					vestGroup.cubeList.add(new ModelBox(vestGroup, 26, 0, -4.3F, -18.9F, -3.1F, 4, 5, 3, -0.7F, false));
					vestGroup.cubeList.add(new ModelBox(vestGroup, 26, 0, 0.3F, -18.9F, -3.1F, 4, 5, 3, -0.7F, true));
					break;
				case KIRI:
					vestGroup.setRotationPoint(0.0F, 0.0F, 0.0F);
					vest.addChild(vestGroup);
					vestGroup.cubeList.add(new ModelBox(vestGroup, 16, 32, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.4F, false));
					vestGroup.cubeList.add(new ModelBox(vestGroup, 48, 8, -4.0F, -13.7F, -2.275F, 8, 3, 0, 0.0F, false));
					vestGroup.cubeList.add(new ModelBox(vestGroup, 48, 8, -4.0F, -13.7F, 2.275F, 8, 3, 0, 0.0F, false));
					break;
				case KUMO:
					vestGroup.setRotationPoint(0.0F, 0.0F, 0.0F);
					vest.addChild(vestGroup);
					vestGroup.cubeList.add(new ModelBox(vestGroup, 40, 9, -4.0F, -13.0F, -2.0F, 8, 3, 4, 0.31F, true));
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
			if (type == Type.SUNA || type == Type.KIRI) {
				rightShoulder.setRotationPoint(-7.0F, -24.5F, 0.0F);
				rightArmVestLayer.addChild(rightShoulder);
				setRotationAngle(rightShoulder, 0.0F, 0.0F, -0.3054F);
				rightShoulder.cubeList.add(new ModelBox(rightShoulder, 36, 0, -1.8F, 0.3F, -2.0F, 4, 1, 4, 0.31F, true));
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
			if (type == Type.SUNA || type == Type.KIRI) {
				leftShoulder.setRotationPoint(1.0F, -6.5F, 0.0F);
				leftArmVestLayer.addChild(leftShoulder);
				setRotationAngle(leftShoulder, 0.0F, 0.0F, 0.3054F);
				leftShoulder.cubeList.add(new ModelBox(leftShoulder, 36, 0, -2.2F, 0.3F, -2.0F, 4, 1, 4, 0.31F, false));
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
			rightLegLayer.cubeList.add(new ModelBox(rightLegLayer, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F, false));
			rightLegLayer.cubeList.add(new ModelBox(rightLegLayer, 0, 0, -2.6F, 1.0F, -1.0F, 1, 4, 2, 0.0F, false));
	
			StoneCloth = new ModelRenderer(this);
			if (type == Type.IWA) {
				StoneCloth.setRotationPoint(0.0F, 6.0F, 0.0F);
				bipedRightLeg.addChild(StoneCloth);
				setRotationAngle(StoneCloth, 0.0F, 0.0F, 0.1745F);
				StoneCloth.cubeList.add(new ModelBox(StoneCloth, 36, 0, -3.2F, -6.8F, -2.0F, 4, 7, 4, 0.25F, false));
			} else {
				StoneCloth.showModel = false;
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
			if (type == Type.KIRI || type == Type.KUMO || type == Type.JUMPSUIT) {
				leftLegLayer.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedLeftLeg.addChild(leftLegLayer);
				leftLegLayer.cubeList.add(new ModelBox(leftLegLayer, 0, 32, -0.1F, -12.0F, -2.0F, 4, 12, 4, 0.2F, true));
			} else {
				leftLegLayer.showModel = false;
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
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
			if (entity instanceof AbstractClientPlayer && ((AbstractClientPlayer)entity).getSkinType().equals("slim")) {
				this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
			}
			super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
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
		AME
	}
}
