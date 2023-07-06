
package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemGauntletThrown extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:gauntlet_thrown")
	public static final Item block = null;
	public static final int ENTITYID = 428;

	public ItemGauntletThrown(ElementsNarutomodMod instance) {
		super(instance, 853);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:gauntlet_thrown", "inventory"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TossItemHook());
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			super();
			setMaxDamage(0);
			setFull3D();
			setUnlocalizedName("gauntlet_thrown");
			setRegistryName("gauntlet_thrown");
			maxStackSize = 1;
			setCreativeTab(null);
		}

		public void setEntity(ItemStack stack, ItemGaunlet.EntityCustom entity) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger("gauntletEntityId", entity.getEntityId());
		}

		@Nullable
		public ItemGaunlet.EntityCustom getEntity(World world, ItemStack stack) {
			Entity entity = world.getEntityByID(stack.hasTagCompound() ? stack.getTagCompound().getInteger("gauntletEntityId") : -1);
			return entity instanceof ItemGaunlet.EntityCustom ? (ItemGaunlet.EntityCustom)entity : null;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			if (!entity.world.isRemote) {
				ItemGaunlet.EntityCustom itemEntity = this.getEntity(entity.world, stack);
				if (itemEntity != null && entity.equals(itemEntity.getShooter())) {
			        itemEntity.retrieve();
				}
			}
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote) {
				ItemGaunlet.EntityCustom itemEntity = this.getEntity(world, itemstack);
				if (itemEntity == null || !entity.equals(itemEntity.getShooter())) {
					itemstack.shrink(1);
				}
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.gauntletthrown.retrieve"));
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			entity.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, entity.getHeldItem(hand));
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BOW;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}
	}

	protected static int getSlotId(EntityPlayer entity) {
		for (int i = 0; i < entity.inventory.mainInventory.size(); i++) {
			ItemStack stack = entity.inventory.mainInventory.get(i);
			if (stack != null && stack.getItem() == block) {
				return i;
			}
		}
		if (entity.getHeldItemOffhand().getItem() == block) {
			return 99;
		}
		return -1;
	}

	public class TossItemHook {
		@SubscribeEvent
		public void onTossItem(ItemTossEvent event) {
			EntityItem entityitem = event.getEntityItem();
			if (entityitem != null) {
				ItemStack stack = entityitem.getItem();
				if (stack.getItem() == block) {
					event.setCanceled(true);
					ItemGaunlet.EntityCustom entity = ((RangedItem)stack.getItem()).getEntity(entityitem.world, stack);
					if (entity != null) {
						entity.setShooter(null);
					}
				}
			}
		}
	}
}
