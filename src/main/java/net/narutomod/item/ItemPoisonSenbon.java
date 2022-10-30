
package net.narutomod.item;

import net.narutomod.entity.EntityPuppetHiruko;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Enchantments;

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
}
