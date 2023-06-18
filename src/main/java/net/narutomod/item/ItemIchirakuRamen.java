
package net.narutomod.item;

import net.narutomod.procedure.ProcedureIchirakuRamenFoodEaten;
import net.narutomod.ElementsNarutomodMod;

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.Map;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemIchirakuRamen extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ichiraku_ramen")
	public static final Item block = null;
	public ItemIchirakuRamen(ElementsNarutomodMod instance) {
		super(instance, 783);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemFoodCustom());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:ichiraku_ramen", "inventory"));
	}
	public static class ItemFoodCustom extends ItemFood {
		public ItemFoodCustom() {
			super(20, 100f, false);
			setUnlocalizedName("ichiraku_ramen");
			setRegistryName("ichiraku_ramen");
			setCreativeTab(CreativeTabs.FOOD);
			setMaxStackSize(2);
		}

		@Override
		public int getMaxItemUseDuration(ItemStack stack) {
			return 140;
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack) {
			return EnumAction.EAT;
		}

		@Override
		protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entity) {
			super.onFoodEaten(itemStack, world, entity);
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				ProcedureIchirakuRamenFoodEaten.executeProcedure($_dependencies);
			}
		}
	}
}
