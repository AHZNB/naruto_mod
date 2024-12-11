
package net.narutomod.item;

import net.narutomod.entity.EntityFutonGreatBreakthrough;
import net.narutomod.creativetab.TabModTab;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemFoldingFan extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:folding_fan")
	public static final Item block = null;
	public static final int ENTITYID = 352;
	private static final String CUSTOM_MODEL_KEY = "CustomRenderedModel";

	public ItemFoldingFan(ElementsNarutomodMod instance) {
		super(instance, 706);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		class MeshDef implements ItemMeshDefinition {
			final ModelResourceLocation[] resources = {
		   	    new ModelResourceLocation("narutomod:folding_fan_0", "inventory"),
		   	    new ModelResourceLocation("narutomod:folding_fan_1", "inventory")
			};
	        @Override
	        public ModelResourceLocation getModelLocation(ItemStack stack) {
	            if (stack.hasTagCompound() && stack.getTagCompound().getBoolean(CUSTOM_MODEL_KEY)) {
	                return this.resources[0];
	            }
	            return this.resources[1];
	        }
	    }
	    MeshDef meshDef = new MeshDef();
   	    ModelBakery.registerItemVariants(block, meshDef.resources);
	    ModelLoader.setCustomMeshDefinition(block, meshDef);
	}

	public static class RangedItem extends Item implements ItemOnBody.Interface {
		public RangedItem() {
			super();
			setMaxDamage(500);
			setFull3D();
			setUnlocalizedName("folding_fan");
			setRegistryName("folding_fan");
			maxStackSize = 1;
			setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", (double) 4, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
			}
			return multimap;
		}

		@Override
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			if (!isSelected) {
				if (!stack.getTagCompound().getBoolean(CUSTOM_MODEL_KEY)) {
					stack.getTagCompound().setBoolean(CUSTOM_MODEL_KEY, true);
					worldIn.playSound(null, entityIn.posX, entityIn.posY, entityIn.posZ,
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:movement")),
					 net.minecraft.util.SoundCategory.NEUTRAL, 0.6f, 1.6f);
				}
			} else if (stack.getTagCompound().hasKey(CUSTOM_MODEL_KEY)) {
				stack.getTagCompound().removeTag(CUSTOM_MODEL_KEY);
				worldIn.playSound(null, entityIn.posX, entityIn.posY, entityIn.posZ,
				 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:movement")),
				 net.minecraft.util.SoundCategory.NEUTRAL, 0.6f, 0.8f);
			}
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
			if (!world.isRemote) {
				entity.extinguish();
				new EntityFutonGreatBreakthrough.EC.Jutsu().createJutsu(itemstack, entity,
						Math.min(60f, 0.5f * ((float)this.getMaxItemUseDuration(itemstack) - timeLeft)));
				itemstack.damageItem(1, entity);
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
}
