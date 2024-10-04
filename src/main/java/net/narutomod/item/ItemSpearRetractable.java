
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
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.Set;
import java.util.HashMap;
import javax.annotation.Nullable;
import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSpearRetractable extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:spear_retractable")
	public static final Item block = null;
	private static final String CUSTOM_MODEL_KEY = "CustomRenderedModel";

	public ItemSpearRetractable(ElementsNarutomodMod instance) {
		super(instance, 897);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemSword(EnumHelper.addToolMaterial("SPEAR_RETRACTABLE", 1, 250, 4f, 9f, 0)) {
			@Override
			public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
				Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
				if (slot == EntityEquipmentSlot.MAINHAND) {
					multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
							new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.getAttackDamage(), 0));
					multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
							new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.8, 0));
				}
				return multimap;
			}

			@Override
			public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
				if (!stack.hasTagCompound()) {
					stack.setTagCompound(new NBTTagCompound());
				}
				if (isSelected) {
					if (!worldIn.isRemote && entityIn.ticksExisted % 5 == 0) {
						int customModel = stack.getTagCompound().hasKey(CUSTOM_MODEL_KEY) ? stack.getTagCompound().getInteger(CUSTOM_MODEL_KEY) : -1;
						if (customModel < 6) {
							stack.getTagCompound().setInteger(CUSTOM_MODEL_KEY, ++customModel);
							worldIn.playSound(null, entityIn.posX, entityIn.posY, entityIn.posZ,
							 SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 0.4f, 0.4f + (float)customModel * 0.1f);
						}
					}
				} else if (stack.getTagCompound().hasKey(CUSTOM_MODEL_KEY)) {
					stack.getTagCompound().removeTag(CUSTOM_MODEL_KEY);
				}
			}

			@Override
			public EnumAction getItemUseAction(ItemStack stack) {
				return EnumAction.BLOCK;
			}

			@Override
			public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
				return stack.getItem() == block;
			}
	
			@Override
			public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
				playerIn.setActiveHand(handIn);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
			}

			@Override
			public int getMaxItemUseDuration(ItemStack stack) {
				return 72000;
			}

			@Override
			public Set<String> getToolClasses(ItemStack stack) {
				HashMap<String, Integer> ret = new HashMap<String, Integer>();
				ret.put("sword", 1);
				return ret.keySet();
			}
		}.setUnlocalizedName("spear_retractable").setRegistryName("spear_retractable").setCreativeTab(TabModTab.tab));
	}

	public static void setHurtSelf(ItemStack stack) {
		if (stack.getItem() == block && stack.hasTagCompound()) {
			stack.getTagCompound().setInteger(CUSTOM_MODEL_KEY, 7);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		class MeshDef implements ItemMeshDefinition {
			final ModelResourceLocation[] resources = {
		   	    new ModelResourceLocation("narutomod:spear_retractable_0", "inventory"),
		   	    new ModelResourceLocation("narutomod:spear_retractable_1", "inventory"),
		   	    new ModelResourceLocation("narutomod:spear_retractable_2", "inventory"),
		   	    new ModelResourceLocation("narutomod:spear_retractable_3", "inventory"),
		   	    new ModelResourceLocation("narutomod:spear_retractable_4", "inventory"),
		   	    new ModelResourceLocation("narutomod:spear_retractable_5", "inventory"),
		   	    new ModelResourceLocation("narutomod:spear_retractable_6", "inventory"),
		   	    new ModelResourceLocation("narutomod:spear_retractable_7", "inventory")
			};
	        @Override
	        public ModelResourceLocation getModelLocation(ItemStack stack) {
	            if (stack.hasTagCompound() && stack.getTagCompound().hasKey(CUSTOM_MODEL_KEY)) {
	                int customModel = stack.getTagCompound().getInteger(CUSTOM_MODEL_KEY);
	                if (customModel >= 0 && customModel <= 7) {
	                	return this.resources[customModel];
	                }
	            }
	            return this.resources[0];
	        }
	    }
	    MeshDef meshDef = new MeshDef();
   	    ModelBakery.registerItemVariants(block, meshDef.resources);
	    ModelLoader.setCustomMeshDefinition(block, meshDef);
	}
}
