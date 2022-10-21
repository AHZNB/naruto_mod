
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.Set;
import java.util.List;
import java.util.HashMap;

import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemYaguraStaff extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:yagura_staff")
	public static final Item block = null;
	public ItemYaguraStaff(ElementsNarutomodMod instance) {
		super(instance, 768);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemSword(EnumHelper.addToolMaterial("YAGURA_STAFF", 0, 100000, 10f, 12f, 0)) {
			@Override
			public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
				Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
				if (slot == EntityEquipmentSlot.MAINHAND) {
					multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
							new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.getAttackDamage(), 0));
					multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
							new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 2, 0));
				}
				return multimap;
			}

			public Set<String> getToolClasses(ItemStack stack) {
				HashMap<String, Integer> ret = new HashMap<String, Integer>();
				ret.put("sword", 0);
				return ret.keySet();
			}

			@Override
			public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
				super.addInformation(itemstack, world, list, flag);
				list.add("Weapon of the 4th Mizukage Yagura");
			}
		}.setUnlocalizedName("yagura_staff").setRegistryName("yagura_staff").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:yagura_staff", "inventory"));
	}
}
