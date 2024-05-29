
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelBiped;

@ElementsNarutomodMod.ModElement.Tag
public class ItemClothesKazekage extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:clothes_kazekagehelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:clothes_kazekagebody")
	public static final Item body = null;

	public ItemClothesKazekage(ElementsNarutomodMod instance) {
		super(instance, 817);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemRobe.Base(EntityEquipmentSlot.HEAD) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ItemClothesHokage.ModelRobeHokage();
				}
				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/robe_kazekage.png";
			}
		}.setUnlocalizedName("clothes_kazekagehelmet").setRegistryName("clothes_kazekagehelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemRobe.Base(EntityEquipmentSlot.CHEST) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ItemClothesHokage.ModelRobeHokage();
				}
				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/robe_kazekage.png";
			}
		}.setUnlocalizedName("clothes_kazekagebody").setRegistryName("clothes_kazekagebody").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:clothes_kazekagehelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:clothes_kazekagebody", "inventory"));
	}
}
