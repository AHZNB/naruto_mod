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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.text.translation.I18n;

import net.narutomod.world.WorldKamuiDimension;
import net.narutomod.procedure.ProcedureGrabEntity;
import net.narutomod.procedure.ProcedureKamuiJikukanIdo;
import net.narutomod.procedure.ProcedureSusanoo;
import net.narutomod.procedure.ProcedureWhenPlayerAttcked;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ItemMangekyoSharinganObito extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:mangekyosharinganobitohelmet")
	public static final Item helmet = null;
	public static final double INTANGIBLE_CHAKRA_USAGE = 1d; // per tick
	public static final double TELEPORT_CHAKRA_USAGE = 20d; // per tick
	
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
						ProcedureWhenPlayerAttcked.setInvulnerable(entity, 2);
						//entity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 2.0d);
					}
				}
			}

			@Override
			public ItemSharingan.Type getSubType() {
				return ItemSharingan.Type.KAMUI;
			}

			@Override
			public boolean isMangekyo() {
				return true;
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

			@Override
			public boolean onJutsuKey1(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
				Map<String, Object> $_dependencies = Maps.newHashMap();
				$_dependencies.put("is_pressed", is_pressed);
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", entity.world);
				if (entity.world.provider.getDimension() == WorldKamuiDimension.DIMID && !entity.isSneaking()) {
					ProcedureGrabEntity.executeProcedure($_dependencies);
				} else {
					$_dependencies.put("x", (int)entity.posX);
					$_dependencies.put("y", (int)entity.posY);
					$_dependencies.put("z", (int)entity.posZ);
					ProcedureKamuiJikukanIdo.executeProcedure($_dependencies);
				}
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
		}.setUnlocalizedName("mangekyosharinganobitohelmet").setRegistryName("mangekyosharinganobitohelmet").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:mangekyosharinganobitohelmet", "inventory"));
	}
}
