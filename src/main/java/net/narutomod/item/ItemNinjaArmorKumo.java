
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemNinjaArmorKumo extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_kumohelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_kumobody")
	public static final Item body = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_kumolegs")
	public static final Item legs = null;

	public ItemNinjaArmorKumo(ElementsNarutomodMod instance) {
		super(instance, 640);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.KUMO, EntityEquipmentSlot.HEAD) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.KUMO);
					this.texture = "narutomod:textures/kumoarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = false;
				}
			}
		}.setUnlocalizedName("ninja_armor_kumohelmet").setRegistryName("ninja_armor_kumohelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.KUMO, EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemNinjaArmor.ModelNinjaArmor model1 = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.KUMO);
					model1.shirt.showModel = false;
					model1.shirtRightArm.showModel = false;
					model1.shirtLeftArm.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/kumoarmor.png";
				}
			}
		}.setUnlocalizedName("ninja_armor_kumobody").setRegistryName("ninja_armor_kumobody").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.KUMO, EntityEquipmentSlot.LEGS) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.KUMO);
					((ItemNinjaArmor.ModelNinjaArmor)this.model).vest.showModel = false;
					this.texture = "narutomod:textures/kumoarmor.png";
				}
			}
		}.setUnlocalizedName("ninja_armor_kumolegs").setRegistryName("ninja_armor_kumolegs").setCreativeTab(TabModTab.tab));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:ninja_armor_kumohelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:ninja_armor_kumobody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:ninja_armor_kumolegs", "inventory"));
	}
}
