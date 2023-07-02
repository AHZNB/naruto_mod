
package net.narutomod.item;

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
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;

//import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import java.util.Set;
import java.util.HashMap;
import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemChokuto extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:chokuto")
	public static final Item block = null;

	public ItemChokuto(ElementsNarutomodMod instance) {
		super(instance, 700);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom().setUnlocalizedName("chokuto").setRegistryName("chokuto").setCreativeTab(TabModTab.tab));
	}

	public static class ItemCustom extends ItemSword implements ItemOnBody.Interface {
		public ItemCustom() {
			super(EnumHelper.addToolMaterial("CHOKUTO", 1, 2000, 4f, 5f, 2));
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.getAttackDamage(), 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3, 0));
			}
			return multimap;
		}

		/*@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityLivingBase 
			 && entity.getRidingEntity() instanceof EntitySusanooBase) {
				EntitySusanooBase susanoo = (EntitySusanooBase)entity.getRidingEntity();
				ItemStack stack = susanoo.getHeldItemMainhand();
				if (((EntityLivingBase)entity).getHeldItemMainhand().equals(itemstack)) {
					if (!susanoo.shouldShowSword()) {
						susanoo.setShowSword(true);
					}
				} else if (susanoo.shouldShowSword()) {
					susanoo.setShowSword(false);
				}
			}
		}*/

		@Override
		public boolean isShield(ItemStack stack, EntityLivingBase entity) {
			return stack.getItem() == block;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
			playerIn.setActiveHand(handIn);
			//return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
			return super.onItemRightClick(worldIn, playerIn, handIn);
		}
	
		@Override
		public EnumAction getItemUseAction(ItemStack stack) {
			return EnumAction.BLOCK;
		}
	
		@Override
		public int getMaxItemUseDuration(ItemStack stack) {
			return 72000;
		}

		public Set<String> getToolClasses(ItemStack stack) {
			HashMap<String, Integer> ret = new HashMap<String, Integer>();
			ret.put("sword", 1);
			return ret.keySet();
		}

		@Override
		public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
			return (repair.getItem() == new ItemStack(Items.IRON_INGOT, (int) (1)).getItem());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:chokuto", "inventory"));
	}
}
