
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import java.util.UUID;
import java.util.List;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemZabuzaSword extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:zabuza_sword")
	public static final Item block = null;

	public ItemZabuzaSword(ElementsNarutomodMod instance) {
		super(instance, 392);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemToolCustom().setUnlocalizedName("zabuza_sword").setRegistryName("zabuza_sword").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:zabuza_sword", "inventory"));
	}

	private static class ItemToolCustom extends Item implements ItemOnBody.Interface {
		private static final UUID REACH_MODIFIER = UUID.fromString("2ea719b4-d3ee-442b-97f6-3a6d704e5102");
		
		protected ItemToolCustom() {
			this.setMaxDamage(300);
			this.setMaxStackSize(1);
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.zabuzasword.general"));
		}

		@Override
		public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
			Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 1f + 22f * this.getDurabilityPercent(stack), 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.4, 0));
				multimap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_MODIFIER, "Tool modifier", 1, 0));
			}
			return multimap;
		}

		@Override
		public boolean canHarvestBlock(IBlockState blockIn) {
			return true;
		}

		private float getDurabilityPercent(ItemStack stack) {
			return 1f - (float)this.getDamage(stack) / (float)this.getMaxDamage();
		}

		@Override
		public float getDestroySpeed(ItemStack stack, IBlockState par2Block) {
			return 10f * this.getDurabilityPercent(stack);
		}

		@Override
		public void setDamage(ItemStack stack, int damage) {
			super.setDamage(stack, damage > this.getMaxDamage() ? this.getMaxDamage() : damage);
		}

		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			stack.damageItem(-3, attacker);
			return true;
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
			if ((double)state.getBlockHardness(worldIn, pos) != 0.0D) {
				stack.damageItem(2, entityLiving);
			}
			return true;
		}

		@Override
		public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
			return false;
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
		public EnumAction getItemUseAction(ItemStack stack) {
			return EnumAction.BLOCK;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack stack) {
			return 72000;
		}

		@Override
		public boolean isFull3D() {
			return true;
		}

		@Override
		public int getItemEnchantability() {
			return 0;
		}
	}
}
