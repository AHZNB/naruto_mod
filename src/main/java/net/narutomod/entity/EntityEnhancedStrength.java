
package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;
import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import java.util.Map;
import net.minecraft.init.Enchantments;
import net.minecraft.entity.player.EntityPlayer;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEnhancedStrength extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 215;
	public static final int ENTITYID_RANGED = 216;

	public EntityEnhancedStrength(ElementsNarutomodMod instance) {
		super(instance, 528);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "enhanced_strength"), ENTITYID).name("enhanced_strength").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private EntityLivingBase user;
		private ItemStack enchantedTool = ItemStack.EMPTY;
		private int ogEnchantmentLevel = 0;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn) {
			this(userIn.world);
			this.user = userIn;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
		}

		@Override
		protected void entityInit() {
		}

		private void removeEfficiencyEnhancement() {
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(this.enchantedTool);
//System.out.println(">>> before: "+map.toString());
//System.out.println(">>> enchantedTool: "+enchantedTool+", ogEnchantmentLevel:"+ogEnchantmentLevel);
			if (map.containsKey(Enchantments.EFFICIENCY)) {
				int slot = this.getSlotFor(this.enchantedTool);
System.out.println(">>> in slot: "+slot);
				if (this.ogEnchantmentLevel > 0) {
					map.put(Enchantments.EFFICIENCY, this.ogEnchantmentLevel);
				} else {
					map.remove(Enchantments.EFFICIENCY);
				}
				EnchantmentHelper.setEnchantments(map, this.enchantedTool);
				if (slot >= 0) {
					((EntityPlayer)this.user).inventory.setInventorySlotContents(slot, this.enchantedTool);
				}
			}
		}

		private void incEfficiencyEnchantment() {
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(this.enchantedTool);
			this.ogEnchantmentLevel = map.containsKey(Enchantments.EFFICIENCY) ? map.get(Enchantments.EFFICIENCY) : 0;
			map.put(Enchantments.EFFICIENCY, this.ogEnchantmentLevel + 1);
			EnchantmentHelper.setEnchantments(map, this.enchantedTool);
		}

		private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
			return stack1.getItem() == stack2.getItem() 
			 && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) 
			 && ItemStack.areItemStackTagsEqual(stack1, stack2);
		}

		private int getSlotFor(ItemStack stack) {
			if (this.user instanceof EntityPlayer) {
				int i = 0;
				for (ItemStack stack1 : ((EntityPlayer)this.user).inventory.mainInventory) {
//System.out.println(i+":"+stack1);
					if (!stack1.isEmpty() && this.stackEqualExact(stack, stack1)) {
						return i;
					}
					++i;
				}
				for (ItemStack stack1 : ((EntityPlayer)this.user).inventory.armorInventory) {
					if (!stack1.isEmpty() && this.stackEqualExact(stack, stack1)) {
						return i;
					}
					++i;
				}
				for (ItemStack stack1 : ((EntityPlayer)this.user).inventory.offHandInventory) {
					if (!stack1.isEmpty() && this.stackEqualExact(stack, stack1)) {
						return i;
					}
					++i;
				}
			}
			return -1;
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (this.ticksExisted % 4 == 0) {
					ItemStack stack = this.user.getHeldItemMainhand();
					if (!ItemStack.areItemStacksEqual(stack, this.enchantedTool)) {
						if (!this.enchantedTool.isEmpty()) {
							this.removeEfficiencyEnhancement();
							this.enchantedTool = ItemStack.EMPTY;
						}
						if (!stack.isEmpty()) {
							this.enchantedTool = stack;
							this.incEfficiencyEnchantment();
						}
					}
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "IryoEnhancedStrengthEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ID_KEY));
				if (entity1 instanceof EC) {
					entity1.setDead();
					return false;
				} else {
					entity1 = new EC(entity);
					entity.world.spawnEntity(entity1);
					entity.getEntityData().setInteger(ID_KEY, entity1.getEntityId());
					return true;
				}
			}
		}
	}
}
