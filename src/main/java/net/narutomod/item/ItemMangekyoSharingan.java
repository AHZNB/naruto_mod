package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.procedure.ProcedureAmaterasu;
import net.narutomod.procedure.ProcedureSusanoo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class ItemMangekyoSharingan extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:mangekyosharinganhelmet")
	public static final Item helmet = null;
	private static final double AMATERASU_CHAKRA_USAGE = 100d;
	
	public ItemMangekyoSharingan(ElementsNarutomodMod instance) {
		super(instance, 69);
	}

	public static double getAmaterasuChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet || stack.getItem() == ItemMangekyoSharinganEternal.helmet
		 ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? AMATERASU_CHAKRA_USAGE 
		 : AMATERASU_CHAKRA_USAGE * 3 : (Double.MAX_VALUE * 0.001d);
	}

	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("MANGEKYOSHARINGAN", "narutomod:mangekyosharingan_sasuke_", 1024,
				new int[]{2, 5, 6, 10}, 0, null, 1.0F);
		this.elements.items.add(() -> new ItemSharingan.Base(enuma) {
			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				super.onArmorTick(world, entity, itemstack);
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 2, 2, false, false));
				}
			}

			@Override
			public boolean isMangekyo() {
				return true;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/mangekyosharinganhelmet_sasuke.png";
			}

			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				super.addInformation(stack, worldIn, tooltip, flagIn);
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " + TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.amaterasu.jutsu1"));
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + TextFormatting.GRAY + I18n.translateToLocal("entity.susanooclothed.name"));
			}

			@Override
			public String getItemStackDisplayName(ItemStack stack) {
				return TextFormatting.RED + super.getItemStackDisplayName(stack) + TextFormatting.WHITE;
			}

			@Override
			public boolean onJutsuKey1(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
				Map<String, Object> $_dependencies = Maps.newHashMap();
				$_dependencies.put("is_pressed", is_pressed);
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", entity.world);
				$_dependencies.put("x", (int)entity.posX);
				$_dependencies.put("y", (int)entity.posY);
				$_dependencies.put("z", (int)entity.posZ);
				ProcedureAmaterasu.executeProcedure($_dependencies);
				return true;
			}

			@Override
			public boolean onJutsuKey2(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
				if (!is_pressed) {
					Map<String, Object> $_dependencies = Maps.newHashMap();
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", entity.world);
					ProcedureSusanoo.executeProcedure($_dependencies);
				}
				return true;
			}

			@Override
			public boolean onSwitchJutsuKey(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
				if (entity.getRidingEntity() instanceof EntitySusanooBase) {
					if (!is_pressed) {
						ProcedureSusanoo.upgrade(entity);
					}
					return true;
				}
				return false;
			}
		}.setUnlocalizedName("mangekyosharinganhelmet").setRegistryName("mangekyosharinganhelmet").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:mangekyosharinganhelmet", "inventory"));
	}
}
