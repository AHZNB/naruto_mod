
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
public class ItemNinjaArmorFishnets extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_fishnetslegs")
	public static final Item legs = null;

	public ItemNinjaArmorFishnets(ElementsNarutomodMod instance) {
		super(instance, 744);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.FISHNET, EntityEquipmentSlot.LEGS) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemNinjaArmor.ModelNinjaArmor model1 = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.FISHNET);
					model1.rightArmVestLayer.showModel = false;
					model1.leftArmVestLayer.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/fishnetarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedRightArm.showModel = true;
					this.model.bipedLeftArm.showModel = true;
				}
			}
		}.setUnlocalizedName("ninja_armor_fishnetslegs").setRegistryName("ninja_armor_fishnetslegs").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:ninja_armor_fishnetslegs", "inventory"));
	}
}
