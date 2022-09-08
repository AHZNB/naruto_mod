
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.Item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.item.EnumAction;

import net.narutomod.entity.EntityGroundShock;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Set;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKabutowariHammer extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:kabutowari_hammer")
	public static final Item block = null;

	public ItemKabutowariHammer(ElementsNarutomodMod instance) {
		super(instance, 675);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemPickaxe(EnumHelper.addToolMaterial("KABUTOWARI_HAMMER", 5, 0, 20f, 8f, 0)) {
			{
				this.attackSpeed = -3f;
			}
			public Set<String> getToolClasses(ItemStack stack) {
				HashMap<String, Integer> ret = new HashMap<String, Integer>();
				ret.put("pickaxe", 5);
				return ret.keySet();
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int slot, boolean par5) {
				super.onUpdate(itemstack, world, entity, slot, par5);
				if (!world.isRemote && entity instanceof EntityPlayer) {
					EntityPlayer living = (EntityPlayer)entity;
					ItemStack axestack = ProcedureUtils.getMatchingItemStack(living, ItemKabutowariAxe.block);
					if (axestack == null) {
						itemstack.shrink(1);
					} else if (!living.getHeldItemOffhand().equals(itemstack) 
					 && !living.getHeldItemMainhand().equals(itemstack)) {
						axestack.shrink(1);
						living.replaceItemInInventory(slot, new ItemStack(ItemKabutowari.block));
						living.inventory.markDirty();
					}
					int i = entity.getEntityData().getInteger("KabutowariHammerSlamTime");
					if (i > 0) {
						entity.getEntityData().setInteger("KabutowariHammerSlamTime", i - 1);
					} else {
						entity.getEntityData().removeTag("KabutowariHammerSlamTime");
					}
				}
			}

			@Override
			public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
				if (!entity.world.isRemote && !entity.onGround && entity.getEntityData().getInteger("KabutowariHammerSlamTime") == 0) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, 
					 SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4f, entity.getRNG().nextFloat() * 0.4f + 0.8f);
					EntityGroundShock.execute(entity.world, MathHelper.floor(entity.posX), (int)entity.posY, MathHelper.floor(entity.posZ), 10);
					entity.getEntityData().setInteger("KabutowariHammerSlamTime", 15);
					return true;
				}
				return super.onEntitySwing(entity, stack);
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
		}.setUnlocalizedName("kabutowari_hammer").setRegistryName("kabutowari_hammer").setCreativeTab(null));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kabutowari_hammer", "inventory"));
	}
}
