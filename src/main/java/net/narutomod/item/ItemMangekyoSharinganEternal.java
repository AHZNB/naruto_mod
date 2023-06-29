package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;

import net.narutomod.world.WorldKamuiDimension;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemMangekyoSharinganEternal extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:mangekyosharinganeternalhelmet")
	public static final Item helmet = null;
	
	public ItemMangekyoSharinganEternal(ElementsNarutomodMod instance) {
		super(instance, 204);
	}

	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("MANGEKYOSHARINGANETERNAL", "narutomod:sasuke_",
		 1024, new int[]{2, 5, 6, 10}, 0, null, 2.0F);
		this.elements.items.add(() -> new ItemSharingan.Base(enuma) {
			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				super.onArmorTick(world, entity, itemstack);
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 2, 2, false, false));
					entity.capabilities.allowFlying = entity.isCreative() || entity.dimension == WorldKamuiDimension.DIMID;
					entity.sendPlayerAbilities();
					if (entity.getEntityData().getBoolean("kamui_teleport")) {
						Chakra.pathway(entity).consume(ItemMangekyoSharinganObito.getTeleportChakraUsage(entity));
					}
					if (entity.getEntityData().getBoolean("kamui_intangible")) {
						Chakra.pathway(entity).consume(ItemMangekyoSharinganObito.getIntangibleChakraUsage(entity));
						entity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 2.0d);
					}
				}
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/mangekyosharinganhelmet_eternal.png";
			}

			@Override
			public int getMaxDamage() {
				return 0;
			}

			@Override
			public boolean isDamageable() {
				return false;
			}

			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				super.addInformation(stack, worldIn, tooltip, flagIn);
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.amaterasu.jutsu1"));
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + TextFormatting.GRAY + I18n.translateToLocal("entity.susanooclothed.name"));
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu3") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.kamui.jutsu1"));
			}

			@Override
			public String getItemStackDisplayName(ItemStack stack) {
				return TextFormatting.RED + super.getItemStackDisplayName(stack) + TextFormatting.WHITE;
			}
		}.setUnlocalizedName("mangekyosharinganeternalhelmet").setRegistryName("mangekyosharinganeternalhelmet").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:mangekyosharinganeternalhelmet", "inventory"));
	}
}
