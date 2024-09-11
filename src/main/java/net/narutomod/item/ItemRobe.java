
package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.util.EnumHelper;

import net.minecraft.item.ItemArmor;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

@ElementsNarutomodMod.ModElement.Tag
public class ItemRobe extends ElementsNarutomodMod.ModElement {
	protected static final ItemArmor.ArmorMaterial ENUMA = EnumHelper.addArmorMaterial("NINJA_ROBE", "narutomod:sasuke_",
	 200, new int[]{1, 2, 3, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0f);

	public ItemRobe(ElementsNarutomodMod instance) {
		super(instance, 865);
	}

	public static abstract class Base extends ItemArmor {
		@SideOnly(Side.CLIENT)
		protected ModelBiped armorModel;
		
		public Base(EntityEquipmentSlot equipmentSlotIn) {
			super(ENUMA, 0, equipmentSlotIn);
		}
	}

	public static class ModelRobe extends ModelBiped {
		//private final ModelRenderer bipedHead;
		//private final ModelRenderer bipedHeadwear;
		//private final ModelRenderer bipedBody;
		private final ModelRenderer bone;
		private final ModelRenderer bone8;
		private final ModelRenderer collar;
		private final ModelRenderer bone6;
		private final ModelRenderer collar2;
		private final ModelRenderer bone2;
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
		public ModelRobe() {
			textureWidth = 64;
			textureHeight = 64;

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

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
