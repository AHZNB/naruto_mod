
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
import net.minecraft.init.Items;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;
import java.util.HashMap;
import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemClaw extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:claw")
	public static final Item block = null;
	public ItemClaw(ElementsNarutomodMod instance) {
		super(instance, 792);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemSword(EnumHelper.addToolMaterial("CLAW", 0, 250, 6f, 4f, 0)) {
			@Override
			public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
				Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
				if (slot == EntityEquipmentSlot.MAINHAND) {
					multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
							new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.getAttackDamage(), 0));
					multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
							new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 1, 0));
				}
				return multimap;
			}

			@Override
			public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
				super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
				this.setCustomChakraFlowVecs(stack);
			}
				
			private void setCustomChakraFlowVecs(ItemStack stack) {
				NBTTagCompound compound = stack.getTagCompound();
				if (compound == null) {
					compound = new NBTTagCompound();
					stack.setTagCompound(compound);
				}
				if (!compound.hasKey("CustomChakraFlowStartVec")) {
					NBTTagCompound cmp1 = new NBTTagCompound();
					cmp1.setDouble("x", 0d);
					cmp1.setDouble("y", -0.875d);
					cmp1.setDouble("z", 0d);
					compound.setTag("CustomChakraFlowStartVec", cmp1);
				}
				if (!compound.hasKey("CustomChakraFlowEndVec")) {
					NBTTagCompound cmp2 = new NBTTagCompound();
					cmp2.setDouble("x", 0d);
					cmp2.setDouble("y", -1.375d);
					cmp2.setDouble("z", 0d);
					compound.setTag("CustomChakraFlowEndVec", cmp2);
				}
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
		}.setUnlocalizedName("claw").setRegistryName("claw").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:claw", "inventory"));
	}
}
