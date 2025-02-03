
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

@ElementsNarutomodMod.ModElement.Tag
public class ItemNinjaArmorJiraiya extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_jiraiyahelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_jiraiyabody")
	public static final Item body = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_jiraiyalegs")
	public static final Item legs = null;

	public ItemNinjaArmorJiraiya(ElementsNarutomodMod instance) {
		super(instance, 864);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.JIRAIYA, EntityEquipmentSlot.HEAD) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.JIRAIYA);
					this.texture = "narutomod:textures/jiraiyaarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = false;
				}
			}
		}.setUnlocalizedName("ninja_armor_jiraiyahelmet").setRegistryName("ninja_armor_jiraiyahelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.JIRAIYA, EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemNinjaArmor.ModelNinjaArmor model1 = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.JIRAIYA);
					model1.shirt.showModel = false;
					model1.shirtRightArm.showModel = false;
					model1.shirtLeftArm.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/jiraiyaarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = true;
				}
			}
		}.setUnlocalizedName("ninja_armor_jiraiyabody").setRegistryName("ninja_armor_jiraiyabody").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.JIRAIYA, EntityEquipmentSlot.LEGS) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.JIRAIYA);
					this.texture = "narutomod:textures/jiraiyaarmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible(ItemStack stack, Entity entity, EntityEquipmentSlot slot) {
					this.model.bipedRightArm.showModel = true;
					this.model.bipedLeftArm.showModel = true;
					if (this.model instanceof ItemNinjaArmor.ModelNinjaArmor) {
						if (entity instanceof EntityLivingBase
						 && ((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ItemRobeJiraiya.body) {
	 						((ItemNinjaArmor.ModelNinjaArmor)this.model).vest.showModel = true;
							((ItemNinjaArmor.ModelNinjaArmor)this.model).rightArmVestLayer.showModel = true;
							((ItemNinjaArmor.ModelNinjaArmor)this.model).leftArmVestLayer.showModel = true;
						} else {
							((ItemNinjaArmor.ModelNinjaArmor)this.model).vest.showModel = false;
							((ItemNinjaArmor.ModelNinjaArmor)this.model).rightArmVestLayer.showModel = false;
							((ItemNinjaArmor.ModelNinjaArmor)this.model).leftArmVestLayer.showModel = false;
						}
					}
				}
			}
		}.setUnlocalizedName("ninja_armor_jiraiyalegs").setRegistryName("ninja_armor_jiraiyalegs").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:ninja_armor_jiraiyahelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:ninja_armor_jiraiyabody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:ninja_armor_jiraiyalegs", "inventory"));
	}
}
