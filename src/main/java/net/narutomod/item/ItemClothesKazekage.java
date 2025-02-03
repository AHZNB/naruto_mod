
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
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemClothesHokage.ModelRobeHokage model1 = new ItemClothesHokage.ModelRobeHokage();
					model1.veil.showModel = true;
					model1.collar.showModel = false;
					model1.collar2.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/robe_kazekage.png";
				}
			}
		}.setUnlocalizedName("clothes_kazekagehelmet").setRegistryName("clothes_kazekagehelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemRobe.Base(EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemClothesHokage.ModelRobeHokage model1 = new ItemClothesHokage.ModelRobeHokage();
					model1.veil.showModel = false;
					model1.collar.showModel = true;
					model1.collar2.showModel = true;
					this.model = model1;
					this.texture = "narutomod:textures/robe_kazekage.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = true;
				}
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
