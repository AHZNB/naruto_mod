
package net.narutomod.creativetab;

import net.narutomod.item.ItemNinjutsu;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.item.ItemStack;
import net.minecraft.creativetab.CreativeTabs;

@ElementsNarutomodMod.ModElement.Tag
public class TabModTab extends ElementsNarutomodMod.ModElement {
	public TabModTab(ElementsNarutomodMod instance) {
		super(instance, 22);
	}

	@Override
	public void initElements() {
		tab = new CreativeTabs("tabmodtab") {
			@SideOnly(Side.CLIENT)
			@Override
			public ItemStack getTabIconItem() {
				return new ItemStack(ItemNinjutsu.block, (int) (1));
			}

			@SideOnly(Side.CLIENT)
			public boolean hasSearchBar() {
				return true;
			}
		}.setBackgroundImageName("item_search.png");
	}
	public static CreativeTabs tab;
}
