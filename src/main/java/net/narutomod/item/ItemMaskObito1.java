
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.world.World;

@ElementsNarutomodMod.ModElement.Tag
public class ItemMaskObito1 extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:mask_obito_1helmet")
	public static final Item helmet = null;
	@SideOnly(Side.CLIENT)
	private ModelBiped maskModel;

	public ItemMaskObito1(ElementsNarutomodMod instance) {
		super(instance, 813);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		this.maskModel = new ItemMaskAnbu1.ModelAnbuMask();
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemArmor(ItemMaskAnbu1.ENUMA, 0, EntityEquipmentSlot.HEAD) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ModelBiped armorModel = ItemMaskObito1.this.maskModel;
				armorModel.isSneak = living.isSneaking();
				armorModel.isRiding = living.isRiding();
				armorModel.isChild = living.isChild();
				return armorModel;
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (entity.ticksExisted % 10 == 6 && entity instanceof EntityLivingBase) {
					entity.setAlwaysRenderNameTag(!((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD).equals(itemstack));
				}
			}
			
			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/mask_obito1.png";
			}
		}.setUnlocalizedName("mask_obito_1helmet").setRegistryName("mask_obito_1helmet").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:mask_obito_1helmet", "inventory"));
	}
}
