
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAnbuRobe extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:anbu_robebody")
	public static final Item body = null;

	public ItemAnbuRobe(ElementsNarutomodMod instance) {
		super(instance, 828);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.ROBE, ItemRobe.ENUMA, EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ModelHoody();
					this.texture = "narutomod:textures/robe_hood.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = true;
				}
			}
		}.setUnlocalizedName("anbu_robebody").setRegistryName("anbu_robebody").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:anbu_robebody", "inventory"));
	}
	// Made with Blockbench 4.6.1
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelHoody extends ItemRobe.ModelRobe {
		public ModelHoody() {
			super();
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -7.85F, -4.0F, 8, 8, 8, 1.0F, false));
		}
	}
}
