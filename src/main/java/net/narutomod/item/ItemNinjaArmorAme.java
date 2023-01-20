
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

@ElementsNarutomodMod.ModElement.Tag
public class ItemNinjaArmorAme extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_amehelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_amelegs")
	public static final Item legs = null;

	public ItemNinjaArmorAme(ElementsNarutomodMod instance) {
		super(instance, 749);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.AME, EntityEquipmentSlot.HEAD) {
			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				super.onArmorTick(world, entity, itemstack);
				entity.removePotionEffect(MobEffects.POISON);
				entity.removePotionEffect(MobEffects.WITHER);
				if (entity.ticksExisted % 20 == 3) {
					entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 21, 0, false, false));
				}
			}
			
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.AME);
					this.texture = "narutomod:textures/amearmor.png";
				}
			}
		}.setUnlocalizedName("ninja_armor_amehelmet").setRegistryName("ninja_armor_amehelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.AME, EntityEquipmentSlot.LEGS) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ItemNinjaArmor.ModelNinjaArmor model1 = new ItemNinjaArmor.ModelNinjaArmor(ItemNinjaArmor.Type.AME);
					model1.vest.showModel = false;
					model1.rightArmVestLayer.showModel = false;
					model1.leftArmVestLayer.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/amearmor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedRightArm.showModel = true;
					this.model.bipedLeftArm.showModel = true;
				}
			}
		}.setUnlocalizedName("ninja_armor_amelegs").setRegistryName("ninja_armor_amelegs").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:ninja_armor_amehelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:ninja_armor_amelegs", "inventory"));
	}
}
