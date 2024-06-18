
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.Set;
import java.util.HashMap;

import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKunaiBlade extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:kunai_blade")
	public static final Item block = null;
	public ItemKunaiBlade(ElementsNarutomodMod instance) {
		super(instance, 870);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemSword(EnumHelper.addToolMaterial("KUNAI_BLADE", 0, 250, 0f, 8f, 14)) {
			@Override
			public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
				Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
				if (slot == EntityEquipmentSlot.MAINHAND) {
					multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
							new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.getAttackDamage(), 0));
					multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
							new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4, 0));
				}
				return multimap;
			}

			public Set<String> getToolClasses(ItemStack stack) {
				HashMap<String, Integer> ret = new HashMap<String, Integer>();
				ret.put("sword", 0);
				return ret.keySet();
			}

			@Override
			public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
				return (repair.getItem() == new ItemStack(Items.IRON_INGOT, (int) (1)).getItem());
			}
		}.setUnlocalizedName("kunai_blade").setRegistryName("kunai_blade").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kunai_blade", "inventory"));
	}
}
