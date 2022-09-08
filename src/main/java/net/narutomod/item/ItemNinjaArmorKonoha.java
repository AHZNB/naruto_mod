
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
public class ItemNinjaArmorKonoha extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_konohahelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_konohabody")
	public static final Item body = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_konohalegs")
	public static final Item legs = null;

	public ItemNinjaArmorKonoha(ElementsNarutomodMod instance) {
		super(instance, 741);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.KONOHA, EntityEquipmentSlot.HEAD) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.KONOHA);
					this.texture = "narutomod:textures/konohaarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = false;
				}
			}
		}.setUnlocalizedName("ninja_armor_konohahelmet").setRegistryName("ninja_armor_konohahelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.KONOHA, EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemNinjaArmor.ModelNinjaArmor model1 = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.KONOHA);
					model1.shirt.showModel = false;
					model1.shirtRightArm.showModel = false;
					model1.shirtLeftArm.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/konohaarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = true;
				}
			}
		}.setUnlocalizedName("ninja_armor_konohabody").setRegistryName("ninja_armor_konohabody").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.KONOHA, EntityEquipmentSlot.LEGS) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemNinjaArmor.ModelNinjaArmor model1 = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.KONOHA);
					model1.vest.showModel = false;
					model1.rightArmVestLayer.showModel = false;
					model1.leftArmVestLayer.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/konohaarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedRightArm.showModel = true;
					this.model.bipedLeftArm.showModel = true;
				}
			}
		}.setUnlocalizedName("ninja_armor_konohalegs").setRegistryName("ninja_armor_konohalegs").setCreativeTab(TabModTab.tab));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:ninja_armor_konohahelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:ninja_armor_konohabody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:ninja_armor_konohalegs", "inventory"));
	}
}
