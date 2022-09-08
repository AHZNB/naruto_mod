
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.EnumAction;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Set;
import net.minecraft.inventory.EntityEquipmentSlot;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKabutowariAxe extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:kabutowari_axe")
	public static final Item block = null;

	public ItemKabutowariAxe(ElementsNarutomodMod instance) {
		super(instance, 676);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemToolCustom().setUnlocalizedName("kabutowari_axe")
		 .setRegistryName("kabutowari_axe").setCreativeTab(null));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kabutowari_axe", "inventory"));
	}

	private static class ItemToolCustom extends ItemTool {
		private static final Set<Block> effective_items_set = com.google.common.collect.Sets
				.newHashSet(new Block[]{Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN,
						Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE});

		protected ItemToolCustom() {
			super(EnumHelper.addToolMaterial("KABUTOWARI_AXE", 5, 0, 20f, 9f, 0), effective_items_set);
			this.attackDamage = 9f;
			this.attackSpeed = -3f;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int slot, boolean par5) {
			super.onUpdate(itemstack, world, entity, slot, par5);
			if (!world.isRemote && entity instanceof EntityPlayer) {
				EntityPlayer living = (EntityPlayer)entity;
				ItemStack hammerstack = ProcedureUtils.getMatchingItemStack(living, ItemKabutowariHammer.block);
				if (hammerstack == null) {
					itemstack.shrink(1);
				} else if (!living.getHeldItemOffhand().equals(itemstack) 
				 && !living.getHeldItemMainhand().equals(itemstack)) {
				 	hammerstack.shrink(1);
					living.replaceItemInInventory(slot, new ItemStack(ItemKabutowari.block));
					living.inventory.markDirty();
				}
			}
		}

		@Override
		public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
			return true;
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

		@Override
		public float getDestroySpeed(ItemStack stack, IBlockState state) {
			Material material = state.getMaterial();
			return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE
					? super.getDestroySpeed(stack, state)
					: this.efficiency;
		}

		@Override
		public boolean hasCustomEntity(ItemStack stack) {
			return true;
		}

		@Override
		public Entity createEntity(World world, Entity location, ItemStack itemstack) {
			EntityItem entityitem = new EntityItem(world, location.posX, location.posY, location.posZ, new ItemStack(ItemKabutowari.block));
			entityitem.motionX = location.motionX;
			entityitem.motionY = location.motionY;
			entityitem.motionZ = location.motionZ;
			entityitem.setPickupDelay(40);
			return entityitem;
		}
	}

	@SubscribeEvent
	public void onCrit(CriticalHitEvent event) {
		if (event.isVanillaCritical() && event.getEntityLiving().getHeldItemMainhand().getItem() == block) {
			event.setDamageModifier(3.0f);
			if (event.getTarget() instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase)event.getTarget();
				if (living.isActiveItemStackBlocking()) {
					ItemStack stack = living.getActiveItemStack();
					if (stack.isItemStackDamageable()) {
						stack.damageItem(stack.getMaxDamage() + 1, living);
					}
				} else {
					for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
						if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && slot != EntityEquipmentSlot.HEAD) {
							ItemStack stack = living.getItemStackFromSlot(slot);
							if (!stack.isEmpty() && stack.isItemStackDamageable()) {
								stack.damageItem(stack.getMaxDamage() + 1, living);
								return;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
