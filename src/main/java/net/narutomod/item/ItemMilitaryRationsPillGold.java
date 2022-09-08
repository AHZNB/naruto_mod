
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemFood;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Chakra;
import net.narutomod.potion.PotionChakraRegeneration;

import java.util.List;
import net.minecraft.util.EnumHand;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.init.MobEffects;

@ElementsNarutomodMod.ModElement.Tag
public class ItemMilitaryRationsPillGold extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:military_rations_pill_gold")
	public static final Item block = null;
	public ItemMilitaryRationsPillGold(ElementsNarutomodMod instance) {
		super(instance, 401);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:military_rations_pill_gold", "inventory"));
	}
	public static class ItemFoodCustom extends ItemFood {
		public ItemFoodCustom() {
			super(14, 0.6f, false);
			setUnlocalizedName("military_rations_pill_gold");
			setRegistryName("military_rations_pill_gold");
			setAlwaysEdible();
			this.setCreativeTab(TabModTab.tab);
			setMaxStackSize(3);
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.mrp.goldtip"));
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.EAT;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ItemStack stack = entity.getHeldItem(hand);
			if (entity.isCreative()) {
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			} else {
				entity.setActiveHand(hand);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}

		@Override
		protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entity) {
			super.onFoodEaten(itemStack, world, entity);
			if (!world.isRemote) {
				Chakra.pathway(entity).consume(-500d, true);
				entity.addPotionEffect(new PotionEffect(PotionChakraRegeneration.potion, 600, 0));
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 2400, 0));
				entity.getCooldownTracker().setCooldown(block, 2400);
			}
		}

		@Override
		public int getMaxItemUseDuration(ItemStack stack) {
			return 200;
		}
	}
}
