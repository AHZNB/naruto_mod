package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.World;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureByakuganHelmetTickEvent;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodModVariables;

import javax.annotation.Nullable;
import java.util.List;
import java.util.HashMap;
import com.google.common.collect.Multimap;
import java.util.UUID;

@ElementsNarutomodMod.ModElement.Tag
public class ItemByakugan extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:byakuganhelmet")
	public static final Item helmet = null;
	private static final String RINNESHARINGAN_KEY = NarutomodModVariables.RINNESHARINGAN_ACTIVATED;
	private static final String TENSEIGANEVOLVEDTIME = NarutomodModVariables.tenseiganEvolvedTime;
	private final UUID RINNESHARINGAN_MODIFIER = UUID.fromString("c69907b2-2687-47ab-aca0-49898cd38463");
	private static final double BYAKUGAN_CHAKRA_USAGE = 10d; //per half sec
	private static final double ROKUJUYONSHO_CHAKRA_USAGE = 100d;
	private static final double KAITEN_CHAKRA_USAGE = 4d; // per tick
	private static final double KUSHO_CHAKRA_USAGE = 0.5d; // x pressDuration
	
	public ItemByakugan(ElementsNarutomodMod instance) {
		super(instance, 98);
	}

	public static double getByakuganChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? BYAKUGAN_CHAKRA_USAGE 
		 : BYAKUGAN_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getRokujuyonshoChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet && ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? ROKUJUYONSHO_CHAKRA_USAGE 
		 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getKaitenChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet && ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? KAITEN_CHAKRA_USAGE 
		 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getKushoChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet && ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? KUSHO_CHAKRA_USAGE 
		 : (Double.MAX_VALUE * 0.001d);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("BYAKUGAN", "narutomod:byakugan_", 25, new int[]{2, 5, 6, 50}, 9, null, 0.0F);
		
		this.elements.items.add(() -> new ItemDojutsu.Base(enuma) {
			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return isRinnesharinganActivated(stack) 
				 ? "narutomod:textures/byakurinnesharingan_helmet.png" : "narutomod:textures/byakuganhelmet.png";
			}

			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				super.onArmorTick(world, entity, itemstack);
				this.isOwner(itemstack, entity);
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				HashMap<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				$_dependencies.put("itemstack", itemstack);
				ProcedureByakuganHelmetTickEvent.executeProcedure($_dependencies);
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (!world.isRemote && entity instanceof EntityLivingBase && this.isOwner(itemstack, (EntityLivingBase)entity)
				 && itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey(TENSEIGANEVOLVEDTIME)) {
					double d = itemstack.getTagCompound().getDouble(TENSEIGANEVOLVEDTIME);
					if (entity.ticksExisted % 20 == 0) {
						d -= 20.0d;
						itemstack.getTagCompound().setDouble(TENSEIGANEVOLVEDTIME, d);
					}
					if (d <= 0.0d && entity instanceof EntityPlayerMP) {
						ItemStack oldstack = itemstack.copy();
						ItemStack newstack = new ItemStack(ItemTenseigan.helmet);
						((ItemDojutsu.Base)newstack.getItem()).setOwner(newstack, (EntityLivingBase)entity);
						newstack.getTagCompound().setDouble("ByakuganCount", itemstack.getTagCompound().getDouble("ByakuganCount"));
						((EntityPlayer)entity).inventory.setInventorySlotContents(getSlotId((EntityPlayer)entity, itemstack), newstack);
						oldstack.getTagCompound().removeTag("ByakuganCount");
						oldstack.getTagCompound().removeTag(TENSEIGANEVOLVEDTIME);
						ItemHandlerHelper.giveItemToPlayer((EntityPlayer)entity, oldstack);
						ProcedureUtils.grantAdvancement((EntityPlayerMP)entity, "narutomod:tenseigan_achieved", true);
					}
				}
			}

			@Override
			public void setOwner(ItemStack stack, EntityLivingBase entityIn) {
				super.setOwner(stack, entityIn);
				stack.getTagCompound().setDouble("ByakuganCount", 1.0d);
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
			public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
				Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
				if (slot == EntityEquipmentSlot.HEAD && isRinnesharinganActivated(stack)) {
					multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(),
					 new AttributeModifier(RINNESHARINGAN_MODIFIER, "byakurinnesharingan.maxhealth", 380d, 0));
				}
				return multimap;
			}

			@SideOnly(Side.CLIENT)
			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				super.addInformation(stack, worldIn, tooltip, flagIn);
				if (isRinnesharinganActivated(stack)) {
					tooltip.add(TextFormatting.RED + I18n.translateToLocal("advancements.rinnesharinganactivated.title") + TextFormatting.WHITE);
				}
				tooltip.add(I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " + I18n.translateToLocal("tooltip.byakugan.jutsu1") + " (NXP:500)");
				if (isRinnesharinganActivated(stack)) {
					tooltip.add(I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + I18n.translateToLocal("tooltip.byakurinnesharingan.jutsu2"));
				} else {
					tooltip.add(I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + I18n.translateToLocal("tooltip.byakugan.jutsu2") + " (NXP:1000)");
				}
				tooltip.add(I18n.translateToLocal("key.mcreator.specialjutsu3") + ": " + I18n.translateToLocal("entity.hakkeshokeiten.name") + " (NXP:1500)");
				if (stack.hasTagCompound()) {
					double d = stack.getTagCompound().getDouble(TENSEIGANEVOLVEDTIME);
					if (d > 0.0d) {
						tooltip.add(I18n.translateToLocal(TextFormatting.AQUA + I18n.translateToLocal("tooltip.byakugan.tenseigantime")
						 + (long)(d / 20d) + TextFormatting.WHITE));
					}
				}
			}
		}.setUnlocalizedName("byakuganhelmet").setRegistryName("byakuganhelmet").setCreativeTab(TabModTab.tab));
	}

	private static int getSlotId(EntityPlayer entity, ItemStack stack) {
		for (int i = 0; i < entity.inventory.getSizeInventory(); i++) {
			ItemStack stack1 = entity.inventory.getStackInSlot(i);
			if (stack != null && stack.equals(stack1)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean wearingAny(EntityLivingBase entity) {
		return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == helmet;
	}

	public static boolean isRinnesharinganActivated(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(RINNESHARINGAN_KEY);
	}

	public static boolean wearingRinnesharingan(EntityPlayer player) {
		ItemStack itemstack = player.inventory.armorInventory.get(3);
		return itemstack.getItem() == helmet && isRinnesharinganActivated(itemstack);
	}

	public static boolean hasRinnesharingan(EntityPlayer player) {
		ItemStack stack = ProcedureUtils.getItemStackIgnoreDurability(player.inventory, new ItemStack(helmet));
		return (stack != null && isRinnesharinganActivated(stack));
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:byakuganhelmet", "inventory"));
	}
}
