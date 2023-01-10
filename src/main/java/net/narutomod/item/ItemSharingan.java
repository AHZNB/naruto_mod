package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.util.ITooltipFlag;

import net.narutomod.procedure.ProcedureSharinganHelmetTickEvent;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSharingan extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:sharinganhelmet")
	public static final Item helmet = null;
	
	public ItemSharingan(ElementsNarutomodMod instance) {
		super(instance, 56);
	}

	public static class Base extends ItemDojutsu.Base {
		private boolean canDamage;

		public Base(ItemArmor.ArmorMaterial material) {
			super(material);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
			ItemDojutsu.ClientModel.ModelHelmetSnug armorModel = (ItemDojutsu.ClientModel.ModelHelmetSnug)super.getArmorModel(living, stack, slot, defaultModel);
			armorModel.highlightHide = isBlinded(stack);
			return armorModel;
		}

		@Override
		public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
			super.onArmorTick(world, entity, itemstack);
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			{
				HashMap<Object, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				$_dependencies.put("itemstack", itemstack);
				$_dependencies.put("world", world);
				ProcedureSharinganHelmetTickEvent.executeProcedure((HashMap) $_dependencies);
			}
			if (!world.isRemote && entity.ticksExisted % 6 == 1
			 && (itemstack.getItem() != ItemMangekyoSharinganEternal.helmet || !this.isOwner(itemstack, entity))
			 && (entity.getEntityData().getBoolean("amaterasu_active")
			  || entity.getEntityData().getBoolean("susanoo_activated") || entity.getEntityData().getBoolean("kamui_teleport"))) {
			 	((Base)itemstack.getItem()).canDamage = true;
				itemstack.damageItem(this.isOwner(itemstack, entity) ? 3 : 9, entity);
				((Base)itemstack.getItem()).canDamage = false;
			}
		}

		@Override
		public void setDamage(ItemStack stack, int damage) {
			if (this.canDamage) {
				super.setDamage(stack, damage);
			}
		}

		@Override
		public int getDamage(ItemStack stack) {
			int itemDamage = this.getMetadata(stack);
			if (itemDamage > this.getMaxDamage()) {
				itemDamage = this.getMaxDamage();
			}
			return itemDamage;
		}

		@Override
		public void setOwner(ItemStack stack, EntityLivingBase entityIn) {
			super.setOwner(stack, entityIn);
			this.setColor(stack, 1 + entityIn.getRNG().nextInt(0x00FFFFFF) | 0x20000000);
		}

		public void setColor(ItemStack stack, int color) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger("color", color);
		}

		public int getColor(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getInteger("color") : 0;
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			super.addInformation(stack, worldIn, tooltip, flagIn);
			tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("tooltip.sharingan.descr") + TextFormatting.WHITE);
		}
	}

	public static boolean hasAny(EntityPlayer player) {
		return ProcedureUtils.hasAnyItemOfSubtype(player, Base.class);
	}

	public static boolean wearingAny(EntityLivingBase entity) {
		return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof Base;
	}

	public static boolean isBlinded(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("sharingan_blinded") : false;
	}

	public static boolean isBlinded(EntityPlayer entity) {
		if (entity.isCreative()) {
			return false;
		}
		int i = 0;
		List<ItemStack> list = ProcedureUtils.getAllItemsOfSubType(entity, ItemDojutsu.Base.class);
		for (ItemStack stack : list) {
			if (isBlinded(stack)) {
				++i;
			}
		}
		return !list.isEmpty() && i == list.size();
	}

	public class PlayerHook {
		@SubscribeEvent
		public void onAttacked(LivingAttackEvent event) {
			EntityLivingBase entity = event.getEntityLiving();
			Entity attacker = event.getSource().getTrueSource();
			if (wearingAny(entity) && ItemJutsu.canTarget(entity) && attacker instanceof EntityLivingBase && !attacker.world.isRemote) {
			 	if (entity.getRNG().nextFloat() < 0.5f) {
			 		event.setCanceled(true);
			 		Vec3d vec = entity.getPositionVector().subtract(attacker.getPositionVector()).normalize()
			 		 .rotateYaw((entity.getRNG().nextFloat()-0.5f)*(float)Math.PI);
			 		entity.addVelocity(vec.x, 0.0d, vec.z);
			 		entity.velocityChanged = true;
			 	}
				((EntityLivingBase)attacker).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 1, false, true));
				((EntityLivingBase)attacker).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 300, 1, false, true));
			}
		}
	}

	public static class SusanooStats {
		private static final String TAGKEY = "SusanooStats";
		private ItemStack sharinganStack;
		private NBTTagCompound tag;

		public static SusanooStats get(ItemStack stack) {
			return new SusanooStats(stack);
		}
		
		private SusanooStats(ItemStack stack) {
			this.sharinganStack = stack;
			//this.tag = entityIn.getEntityData().getCompoundTag(TAGKEY);
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			this.tag = stack.getTagCompound().getCompoundTag(TAGKEY);
		}

		private void setTag() {
			//this.entity.getEntityData().setTag(TAGKEY, this.tag);
			this.sharinganStack.getTagCompound().setTag(TAGKEY, this.tag);
		}
		
		public SusanooStats setActivated(boolean b) {
			this.tag.setBoolean("activated", b);
			this.setTag();
			return this;
		}

		public SusanooStats setTicks(int i) {
			this.tag.setInteger("ticks", i);
			this.setTag();
			return this;
		}

		public SusanooStats incrementTicks() {
			this.tag.setInteger("ticks", this.tag.getInteger("ticks") + 1);
			this.setTag();
			return this;
		}

		public SusanooStats setCD(int i) {
			this.tag.setInteger("cooldown", i);
			this.setTag();
			return this;
		}

		public SusanooStats setId(int i) {
			this.tag.setInteger("id", i);
			this.setTag();
			return this;
		}

		public SusanooStats setColor(int i) {
			this.tag.setInteger("color", i);
			this.setTag();
			return this;
		}

		public boolean isActivated() {
			return this.tag.getBoolean("activated");
		}

		public int getTicks() {
			return this.tag.getInteger("ticks");
		}

		public int getCD() {
			return this.tag.getInteger("cooldown");
		}

		public int getId() {
			return this.tag.getInteger("id");
		}

		public int getColor() {
			return this.tag.getInteger("color");
		}
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("SHARINGAN", "narutomod:sharingan_", 1024, new int[]{2, 5, 6, 50}, 0, null,
				5.0F);
		this.elements.items.add(() -> new Base(enuma) {
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/sharinganhelmet.png";
			}
		}.setUnlocalizedName("sharinganhelmet").setRegistryName("sharinganhelmet").setCreativeTab(TabModTab.tab));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:sharinganhelmet", "inventory"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerHook());
	}
}
