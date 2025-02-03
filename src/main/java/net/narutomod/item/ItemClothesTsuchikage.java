
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
public class ItemClothesTsuchikage extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:clothes_tsuchikagehelmet")
	public static final Item helmet = null;

	public ItemClothesTsuchikage(ElementsNarutomodMod instance) {
		super(instance, 820);
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
					this.texture = "narutomod:textures/robe_tsuchikage.png";
				}
			}
		}.setUnlocalizedName("clothes_tsuchikagehelmet").setRegistryName("clothes_tsuchikagehelmet").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:clothes_tsuchikagehelmet", "inventory"));
	}
}
