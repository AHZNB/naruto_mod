
package net.narutomod.item;

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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.item.EntityItem;

import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemScytheHidanThrown extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:scythe_hidan_thrown")
	public static final Item block = null;
	public static final int ENTITYID = 417;

	public ItemScytheHidanThrown(ElementsNarutomodMod instance) {
		super(instance, 838);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:scythe_hidan_thrown", "inventory"));
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
			setUnlocalizedName("scythe_hidan_thrown");
			setRegistryName("scythe_hidan_thrown");
			maxStackSize = 1;
			setCreativeTab(null);
		}

		public void setEntity(ItemStack stack, ItemScytheHidan.EntityCustom entity) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger("hidanscytheEntityId", entity.getEntityId());
		}

		@Nullable
		public ItemScytheHidan.EntityCustom getEntity(World world, ItemStack stack) {
			Entity entity = world.getEntityByID(stack.hasTagCompound() ? stack.getTagCompound().getInteger("hidanscytheEntityId") : -1);
			return entity instanceof ItemScytheHidan.EntityCustom ? (ItemScytheHidan.EntityCustom)entity : null;
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				ItemScytheHidan.EntityCustom itemEntity = this.getEntity(world, itemstack);
				if (itemEntity != null && entity.equals(itemEntity.getShooter())) {
			        double d0 = entity.posX - itemEntity.posX;
			        double d1 = entity.getEntityBoundingBox().minY + (double)entity.height / 3d - itemEntity.posY;
			        double d2 = entity.posZ - itemEntity.posZ;
			        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
			        itemEntity.retrieve(d0, d1 + d3 * 0.3D, d2, (float)MathHelper.sqrt(d3) * 0.3F);
				}
			}
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote) {
				ItemScytheHidan.EntityCustom itemEntity = this.getEntity(world, itemstack);
				if (itemEntity == null || !entity.equals(itemEntity.getShooter())) {
					itemstack.shrink(1);
				}
			}
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
					ItemScytheHidan.EntityCustom entity = ((RangedItem)stack.getItem()).getEntity(entityitem.world, stack);
					if (entity != null) {
						entity.setShooter(null);
					}
				}
			}
		}
	}
}
