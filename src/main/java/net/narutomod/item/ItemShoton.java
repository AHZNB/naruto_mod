
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityCrystalArmor;
import net.narutomod.entity.EntityCrystalPrison;
import net.narutomod.entity.EntityCrystalThorns;
import net.narutomod.entity.EntityCrystalRay;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemShoton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:shoton")
	public static final Item block = null;
	public static final int ENTITYID = 471;
	public static final ItemJutsu.JutsuEnum ARMOR = new ItemJutsu.JutsuEnum(0, "crystal_armor", 'S', 150, 20d, new EntityCrystalArmor.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum PRISON = new ItemJutsu.JutsuEnum(1, "crystal_prison", 'S', 150, 100d, new EntityCrystalPrison.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum THORNS = new ItemJutsu.JutsuEnum(2, "crystal_thorns", 'S', 150, 2d, new EntityCrystalThorns.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum RAY = new ItemJutsu.JutsuEnum(3, "crystal_ray", 'S', 300, 500d, new EntityCrystalRay.EC.Jutsu());

	public ItemShoton(ElementsNarutomodMod instance) {
		super(instance, 900);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(ARMOR, PRISON, THORNS, RAY));
		//elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityPrison.class)
		//		.id(new ResourceLocation("narutomod", "shoton_prison"), ENTITYID).name("shoton_prison").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:shoton", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.SHOTON, list);
			setUnlocalizedName("shoton");
			setRegistryName("shoton");
			setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[ARMOR.index] = 0;
			this.defaultCooldownMap[PRISON.index] = 0;
			this.defaultCooldownMap[THORNS.index] = 0;
			this.defaultCooldownMap[RAY.index] = 0;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == THORNS) {
				if (!player.world.isRemote && player instanceof EntityPlayer
				 && this.canActivateJutsu(stack, THORNS, (EntityPlayer)player) == EnumActionResult.SUCCESS) {
					this.executeJutsu(stack, player, 0.99f + ((float)this.getMaxUseDuration() - timeLeft) * 0.01f);
				}
				return;
			}
			super.onUsingTick(stack, player, timeLeft);
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			if (entity.isCreative() || ProcedureUtils.hasItemInInventory(entity, ItemDoton.block)) {
				return super.onItemRightClick(world, entity, hand);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, entity.getHeldItem(hand));
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(TextFormatting.GREEN + net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.shoton.musthave") + TextFormatting.RESET);
		}
	}

}
