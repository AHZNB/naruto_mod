
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.entity.EntityNinjaMob;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSamehada extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:samehada")
	public static final Item block = null;
	private static final double CHAKRA_TRANSFER = 50d;

	public ItemSamehada(ElementsNarutomodMod instance) {
		super(instance, 455);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemToolCustom().setUnlocalizedName("samehada")
		 .setRegistryName("samehada").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:samehada", "inventory"));
	}

	public static void applyEffects(EntityLivingBase target, EntityLivingBase attacker) {
		applyEffects(target, attacker, 1.0f);
	}

	public static void applyEffects(EntityLivingBase target, EntityLivingBase attacker, float multiplier) {
		if (Chakra.pathway(target).consume(CHAKRA_TRANSFER * multiplier)) {
			PotionEffect effect = target.getActivePotionEffect(MobEffects.WEAKNESS);
			if (effect == null || effect.getAmplifier() < 3 || effect.getDuration() < 50) {
				target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 3));
			}
			effect = target.getActivePotionEffect(MobEffects.MINING_FATIGUE);
			if (effect == null || effect.getDuration() < 50) {
				target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100, 1));
			}
			effect = target.getActivePotionEffect(MobEffects.SLOWNESS);
			if (effect == null || effect.getDuration() < 50) {
				target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 1));
			}
			if (Chakra.pathway(attacker).consume(-CHAKRA_TRANSFER * multiplier, true)) {
				attacker.heal(4f * multiplier);
			}
		}
	}

	private static class ItemToolCustom extends Item implements ItemOnBody.Interface {
		protected ItemToolCustom() {
			this.setMaxDamage(0);
			this.setMaxStackSize(1);
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.samehada.general"));
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
			if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 11f, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -3.4, 0));
			}
			return multimap;
		}

		@Override
		public boolean canHarvestBlock(IBlockState blockIn) {
			return true;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack, IBlockState par2Block) {
			return 1f;
		}

		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			//applyEffects(target, attacker);
			//target.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 1));
			return true;
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
			stack.damageItem(1, entityLiving);
			return true;
		}

		@Override
		public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
			return true;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (entity instanceof EntityLivingBase && !world.isRemote) {
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 0, 2, false, false));
			}
		}

		@Override
		public EnumAction getItemUseAction(ItemStack stack) {
			return EnumAction.BLOCK;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack stack) {
			return 72000;
		}

		@Override
		public boolean isFull3D() {
			return true;
		}

		@Override
		public int getItemEnchantability() {
			return 0;
		}
	}

	public class AttackHook {
		@SubscribeEvent
		public void onAttack(LivingAttackEvent event) {
			EntityLivingBase target = event.getEntityLiving();
			Entity trueSource = event.getSource().getTrueSource();
			if (trueSource instanceof EntityLivingBase) {
				EntityLivingBase attacker = (EntityLivingBase)trueSource;
				if (attacker.getHeldItemMainhand().getItem() == block) {
					applyEffects(target, attacker);
					target.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 1));
				}
				if (target.isActiveItemStackBlocking() && target.getActiveItemStack().getItem() == block) {
					applyEffects(attacker, target);
				}
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new AttackHook());
	}
}
