
package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.util.EnumHelper;

import net.minecraft.item.ItemArmor;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;

@ElementsNarutomodMod.ModElement.Tag
public class ItemRobe extends ElementsNarutomodMod.ModElement {
	public ItemRobe(ElementsNarutomodMod instance) {
		super(instance, 865);
	}

	public static abstract class Base extends ItemNinjaArmor.Base {
		@Deprecated
		@SideOnly(Side.CLIENT)
		public ModelBiped armorModel;
		
		public Base(EntityEquipmentSlot equipmentSlotIn) {
			super(ItemNinjaArmor.Type.ROBE, equipmentSlotIn);
		}

		@Override
		protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
			return new Armor4Slot(slotIn);
		}

		class Armor4Slot extends ItemNinjaArmor.ArmorData {
			EntityEquipmentSlot slot;
			
			Armor4Slot(EntityEquipmentSlot slotIn) {
				this.slot = slotIn;
			}
			
			@SideOnly(Side.CLIENT)
			@Override
			protected void init() {
				ModelRobe model1 = new ModelRobe();
				if (this.slot != EntityEquipmentSlot.CHEST) {
					model1.collar.showModel = false;
					model1.collar2.showModel = false;
				}
				this.model = model1;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static class ModelRobe extends ModelBiped {
		//private final ModelRenderer bipedHead;
		//private final ModelRenderer bipedHeadwear;
		//private final ModelRenderer bipedBody;
		public final ModelRenderer collar;
		private final ModelRenderer bone2;
		public final ModelRenderer collar2;
		private final ModelRenderer bone;
		private final ModelRenderer bone8;
		//private final ModelRenderer bipedRightArm;
		private final ModelRenderer rightNormal;
		private final ModelRenderer rightSmall;
		//private final ModelRenderer bipedLeftArm;
		private final ModelRenderer leftNormal;
		private final ModelRenderer leftSmall;
		private ModelBiped wearerModel;

		public ModelRobe() {
			textureWidth = 64;
			textureHeight = 64;

			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			collar = new ModelRenderer(this);
			collar.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.addChild(collar);
			collar.cubeList.add(new ModelBox(collar, 0, 0, -4.0F, -5.0F, -3.0F, 8, 4, 6, 1.5F, false));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 0.5F, 4.5F);
			collar.addChild(bone2);
			setRotationAngle(bone2, -0.7854F, 0.0F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 0, 20, -4.0F, 1.5F, -2.5F, 8, 1, 1, 1.5F, false));
			collar2 = new ModelRenderer(this);
			collar2.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.addChild(collar2);
			collar2.cubeList.add(new ModelBox(collar2, 0, 10, -4.0F, -5.0F, -3.0F, 8, 4, 6, 1.45F, false));
	
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

			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			rightNormal = new ModelRenderer(this);
			rightNormal.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedRightArm.addChild(rightNormal);
			rightNormal.cubeList.add(new ModelBox(rightNormal, 0, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));
			rightSmall = new ModelRenderer(this);
			rightSmall.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedRightArm.addChild(rightSmall);
			rightSmall.cubeList.add(new ModelBox(rightSmall, 1, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.5F, false));
			
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			leftNormal = new ModelRenderer(this);
			leftNormal.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedLeftArm.addChild(leftNormal);
			leftNormal.cubeList.add(new ModelBox(leftNormal, 0, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));
			leftSmall = new ModelRenderer(this);
			leftSmall.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedLeftArm.addChild(leftSmall);
			leftSmall.cubeList.add(new ModelBox(leftSmall, 1, 32, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.5F, true));
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
			if ((entity instanceof AbstractClientPlayer && ((AbstractClientPlayer)entity).getSkinType().equals("slim"))
			 || entity.getEntityData().getBoolean("slimModel")) {
				this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				this.rightNormal.showModel = false;
				this.rightSmall.showModel = true;
				this.leftNormal.showModel = false;
				this.leftSmall.showModel = true;
			} else {
				this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				this.rightNormal.showModel = true;
				this.rightSmall.showModel = false;
				this.leftNormal.showModel = true;
				this.leftSmall.showModel = false;
			}
			super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
			if (!(entity instanceof AbstractClientPlayer) && this.wearerModel != null) {
				copyModelAngles(this.wearerModel.bipedLeftArm, this.bipedLeftArm);
				copyModelAngles(this.wearerModel.bipedRightArm, this.bipedRightArm);
			}
		}
	}
}
