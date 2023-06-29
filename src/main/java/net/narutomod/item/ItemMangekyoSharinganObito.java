package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.text.translation.I18n;

import net.narutomod.world.WorldKamuiDimension;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemMangekyoSharinganObito extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:mangekyosharinganobitohelmet")
	public static final Item helmet = null;
	private static final double INTANGIBLE_CHAKRA_USAGE = 1d; // per tick
	private static final double TELEPORT_CHAKRA_USAGE = 20d; // per tick
	
	public ItemMangekyoSharinganObito(ElementsNarutomodMod instance) {
		super(instance, 118);
	}

	public static double getIntangibleChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet || stack.getItem() == ItemMangekyoSharinganEternal.helmet 
		 ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? INTANGIBLE_CHAKRA_USAGE 
		 : INTANGIBLE_CHAKRA_USAGE * 3 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getTeleportChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet || stack.getItem() == ItemMangekyoSharinganEternal.helmet
		 ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? TELEPORT_CHAKRA_USAGE 
		 : TELEPORT_CHAKRA_USAGE * 3 : (Double.MAX_VALUE * 0.001d);
	}

	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("MANGEKYOSHARINGANOBITO", "narutomod:mangekyosharingan_obito_", 1024,
				new int[]{2, 5, 6, 10}, 0, null, 1.0F);
		this.elements.items.add(() -> new ItemSharingan.Base(enuma) {
			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				super.onArmorTick(world, entity, itemstack);
				if (!world.isRemote) {
					boolean flag = entity.isCreative() || entity.dimension == WorldKamuiDimension.DIMID;
					if (entity.capabilities.allowFlying != flag) {
						entity.capabilities.allowFlying = flag;
						entity.sendPlayerAbilities();
					}
					if (entity.getEntityData().getBoolean("kamui_teleport")) {
						Chakra.pathway(entity).consume(getTeleportChakraUsage(entity));
					}
					if (entity.getEntityData().getBoolean("kamui_intangible")) {
						Chakra.pathway(entity).consume(getIntangibleChakraUsage(entity));
						entity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 2.0d);
					}
				}
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/mangekyosharinganhelmet_obito.png";
			}

			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				super.addInformation(stack, worldIn, tooltip, flagIn);
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.kamui.jutsu1"));
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + TextFormatting.GRAY + I18n.translateToLocal("entity.susanooclothed.name"));
			}

			@Override
			public String getItemStackDisplayName(ItemStack stack) {
				return TextFormatting.RED + super.getItemStackDisplayName(stack) + TextFormatting.WHITE;
			}
		}.setUnlocalizedName("mangekyosharinganobitohelmet").setRegistryName("mangekyosharinganobitohelmet").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:mangekyosharinganobitohelmet", "inventory"));
	}
}
