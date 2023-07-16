
package net.narutomod.item;

import net.narutomod.entity.EntityPuppetHiruko;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
//import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
//import net.minecraftforge.common.crafting.IShapedRecipe;
//import net.minecraftforge.registries.GameData;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
//import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.IRecipe;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
//import net.minecraft.inventory.InventoryCrafting;
//import net.minecraft.potion.PotionUtils;
import net.minecraft.init.Enchantments;
//import net.minecraft.init.Items;
//import net.minecraft.init.PotionTypes;

@ElementsNarutomodMod.ModElement.Tag
public class ItemPoisonSenbon extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:poison_senbon")
	public static final Item block = null;
	public static final int ENTITYID = 401;

	public ItemPoisonSenbon(ElementsNarutomodMod instance) {
		super(instance, 789);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:poison_senbon", "inventory"));
	}

	public static class RangedItem extends ItemSenbon.RangedItem {
		public RangedItem() {
			super();
		}

		@Override
		protected void itemInit() {
			setMaxDamage(0);
			setFull3D();
			setUnlocalizedName("poison_senbon");
			setRegistryName("poison_senbon");
			maxStackSize = 64;
			setCreativeTab(TabModTab.tab);
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				boolean flag = entity.getRidingEntity() instanceof EntityPuppetHiruko.EntityCustom;
				if (flag) {
					for (int i = 0; i < 3; i++) {
						spawnArrow((EntityLivingBase)entity.getRidingEntity(), false);
					}
				} else {
					spawnArrow(entity, false);
				}
				if (!entity.capabilities.isCreativeMode
				 && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemstack) <= 0) {
					entity.inventory.clearMatchingItems(block, -1, flag ? 3 : 1, null);
				}
			}
		}

		/*@Override
		public void onUsingTick(ItemStack itemstack, EntityLivingBase entityLivingBase, int count) {
			if (entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				boolean flag = entity.getRidingEntity() instanceof EntityPuppetHiruko.EntityCustom;
				if (flag) {
					for (int i = 0; i < 3; i++) {
						spawnArrow((EntityLivingBase)entity.getRidingEntity(), false);
					}
				} else {
					spawnArrow(entity, false);
				}
				if (!entity.capabilities.isCreativeMode
				 && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemstack) <= 0) {
					entity.inventory.clearMatchingItems(block, -1, flag ? 3 : 1, null);
				}
			}
			entityLivingBase.resetActiveHand();
		}*/
	}

	public static void spawnArrow(Entity entity, boolean randomDirection) {
		if (!entity.world.isRemote) {
			float power = 1f;
			ItemSenbon.EntityArrowCustom entityarrow = entity instanceof EntityLivingBase
			 ? new ItemSenbon.EntityArrowCustom(entity.world, (EntityLivingBase)entity)
			 : new ItemSenbon.EntityArrowCustom(entity.world, entity.posX, entity.posY, entity.posZ);
			if (randomDirection) {
				entityarrow.shoot(entity.world.rand.nextFloat() * 2.0f - 1.0f, entity.world.rand.nextFloat() * 2.0f - 1.0f,
				 entity.world.rand.nextFloat() * 2.0f - 1.0f, power * 2, 0.0f);
			} else {
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 2,
				 entity instanceof EntityPuppetHiruko.EntityCustom ? 8.0f : 0.0f);
			}
			entityarrow.setPoisened(true);
			ItemSenbon.EntityArrowCustom.spawn(entityarrow);
		}
	}

	public static void spawnArrow(EntityLivingBase entity, Vec3d targetVec) {
		if (!entity.world.isRemote) {
			float power = 1f;
			ItemSenbon.EntityArrowCustom entityarrow = new ItemSenbon.EntityArrowCustom(entity.world, (EntityLivingBase)entity);
			targetVec = targetVec.subtract(entityarrow.getPositionVector());
			entityarrow.shoot(targetVec.x, targetVec.y, targetVec.z, power * 2, 8.0f);
			entityarrow.setPoisened(true);
			ItemSenbon.EntityArrowCustom.spawn(entityarrow);
		}
	}

	/*public static class RecipePoisonSenbon extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IShapedRecipe {
	    @Override
	    public boolean matches(InventoryCrafting inv, World worldIn) {
	        if (inv.getWidth() == 3 && inv.getHeight() == 3) {
	            for (int i = 0; i < inv.getWidth(); ++i) {
	                for (int j = 0; j < inv.getHeight(); ++j) {
	                    ItemStack itemstack = inv.getStackInRowAndColumn(i, j);
	                    if (itemstack.isEmpty()) {
	                        return false;
	                    }
	                    Item item = itemstack.getItem();
	                    if (i == 1 && j == 1) {
	                        if (item != Items.LINGERING_POTION || PotionUtils.getPotionFromItem(itemstack) != PotionTypes.STRONG_POISON) {
	                            return false;
	                        }
	                    } else if (item != ItemSenbon.block) {
	                        return false;
	                    }
	                }
	            }
	            return true;
	        } else {
	            return false;
	        }
	    }
	
	    @Override
	    public ItemStack getCraftingResult(InventoryCrafting inv) {
	        ItemStack itemstack = inv.getStackInRowAndColumn(1, 1);
	        if (itemstack.getItem() != Items.LINGERING_POTION || PotionUtils.getPotionFromItem(itemstack) != PotionTypes.STRONG_POISON) {
	            return ItemStack.EMPTY;
	        } else {
	            return new ItemStack(block, 8);
	        }
	    }
	
	    @Override
	    public ItemStack getRecipeOutput() {
	        return ItemStack.EMPTY;
	    }
	
	    @Override
	    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
	        return NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	    }
	
	    @Override
	    public boolean isDynamic() {
	        return true;
	    }
	
	    @Override
	    public boolean canFit(int width, int height) {
	        return width >= 2 && height >= 2;
	    }

	    @Override
	    public int getRecipeWidth() {
	    	return 3;
	    }

	    @Override
	    public int getRecipeHeight() {
	    	return 3;
	    }
	}

	@Override
	public void init(FMLInitializationEvent event) {
		GameData.register_impl(new RecipePoisonSenbon().setRegistryName(new ResourceLocation("narutomod", "poisonsenbon")));
	}*/
}
