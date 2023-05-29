
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.block.BlockExplosiveTag;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.event.EventSphericalExplosion;
import net.narutomod.ElementsNarutomodMod;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemShibukiSword extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:shibuki_sword")
	public static final Item block = null;

	public ItemShibukiSword(ElementsNarutomodMod instance) {
		super(instance, 609);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom().setUnlocalizedName("shibuki_sword").setRegistryName("shibuki_sword").setCreativeTab(TabModTab.tab));
	}

	public static class ItemCustom extends ItemSword implements ItemOnBody.Interface {
		public ItemCustom() {
			super(EnumHelper.addToolMaterial("SHIBUKI_SWORD", 1, 100, 4f, 4f, 0));
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.getAttackDamage(), 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.5, 0));
			}
			return multimap;
		}

		@Override
		public void setDamage(ItemStack stack, int damage) {
			super.setDamage(stack, damage > this.getMaxDamage() ? this.getMaxDamage() : damage);
		}

		@Override
		public boolean isShield(ItemStack stack, EntityLivingBase entity) {
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

		public Set<String> getToolClasses(ItemStack stack) {
			HashMap<String, Integer> ret = new HashMap<String, Integer>();
			ret.put("sword", 1);
			return ret.keySet();
		}

		@Override
		public boolean hitEntity(ItemStack itemstack, EntityLivingBase entity, EntityLivingBase sourceentity) {
			super.hitEntity(itemstack, entity, sourceentity);
			if (!entity.world.isRemote && itemstack.getItemDamage() < itemstack.getMaxDamage() && entity instanceof EntityLivingBase) {
				//new EventVanillaExplosion(entity.world, entity, 0, 0, 0, 5, entity.world.getTotalWorldTime() + 20, false);
				new EventSphericalExplosion(entity.world, entity, 0, 0, 0, 5, entity.world.getTotalWorldTime() + 20, 0.0f) {
					@Override
					protected void doOnTick(int currentTick) {
						Entity target = this.getEntity();
						if (target != null) {
							this.x0 = MathHelper.floor(target.posX);
							this.y0 = MathHelper.floor(target.posY + 0.5d * target.height + 0.1d);
							this.z0 = MathHelper.floor(target.posZ);
							if (currentTick == 1) {
								ProcedureAoeCommand.set(target, 0d, 4d)
								 .damageEntitiesCentered(ItemJutsu.causeJutsuDamage(sourceentity, null), 40f);
							}
						}
					}
				};
			}
			return true;
		}

		@Override
		public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
			return (repair.getItem() == new ItemStack(BlockExplosiveTag.block, (int) (1)).getItem());
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.shibuki.general"));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:shibuki_sword", "inventory"));
	}
}
