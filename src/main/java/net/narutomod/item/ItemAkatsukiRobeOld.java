
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
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAkatsukiRobeOld extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:akatsuki_robe_oldbody")
	public static final Item body = null;

	public ItemAkatsukiRobeOld(ElementsNarutomodMod instance) {
		super(instance, 923);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemRobe.Base(EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ModelRobeNagato model1 = new ModelRobeNagato();
					model1.collar.showModel = true;
					model1.collar2.showModel = true;
					this.model = model1;
					this.texture = "narutomod:textures/robe_nagato.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible(ItemStack stack, Entity entity, EntityEquipmentSlot slot) {
					this.model.bipedHeadwear.showModel = true;
				}
			}
		}.setUnlocalizedName("akatsuki_robe_oldbody").setRegistryName("akatsuki_robe_oldbody").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:akatsuki_robe_oldbody", "inventory"));
	}
	// Made with Blockbench 4.6.1
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelRobeNagato extends ItemRobe.ModelRobe {
		public ModelRobeNagato() {
			super();
			ModelRenderer nagato = new ModelRenderer(this);
			nagato.setRotationPoint(0.0F, -3.0F, 0.0F);
			bipedBody.addChild(nagato);
			nagato.cubeList.add(new ModelBox(nagato, 36, 0, -4.0F, 9.0F, -3.0F, 8, 4, 6, 0.95F, false));
		}
	}
}
