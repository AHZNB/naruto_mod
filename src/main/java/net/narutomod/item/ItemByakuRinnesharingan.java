
package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;

@ElementsNarutomodMod.ModElement.Tag
public class ItemByakuRinnesharingan extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:byakurinnesharinganhelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:byakurinnesharinganbody")
	public static final Item body = null;
	@GameRegistry.ObjectHolder("narutomod:byakurinnesharinganlegs")
	public static final Item legs = null;
	@GameRegistry.ObjectHolder("narutomod:byakurinnesharinganboots")
	public static final Item boots = null;
	public ItemByakuRinnesharingan(ElementsNarutomodMod instance) {
		super(instance, 277);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("BYAKURINNESHARINGAN", "narutomod:sasuke_", 0, new int[]{0, 0, 0, 0}, 0, null,
				0f);
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.HEAD) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@SideOnly(Side.CLIENT)
			private ModelHelmetSnug helmentModel;

			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelBiped();
				}

				if (this.helmentModel == null) {
					this.helmentModel = new ModelHelmetSnug();
				}

				this.armorModel.bipedHead = this.helmentModel.bb_main;
				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/byakurinnesharingan_helmet.png";
			}
		}.setUnlocalizedName("byakurinnesharinganhelmet").setRegistryName("byakurinnesharinganhelmet").setCreativeTab(null));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:byakurinnesharinganhelmet", "inventory"));
	}
	// Made with Blockbench
	// Paste this code into your mod.
	public static class ModelHelmetSnug extends ModelBase {
		private final ModelRenderer bb_main;
		public ModelHelmetSnug() {
			textureWidth = 32;
			textureHeight = 16;
			bb_main = new ModelRenderer(this);
			bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
			bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.1F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			bb_main.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
