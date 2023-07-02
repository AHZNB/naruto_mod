package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.block.material.Material;

import net.narutomod.procedure.ProcedureSharinganHelmetTickEvent;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import java.util.HashMap;
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

		public void forceDamage(ItemStack stack, int damage) {
			super.setDamage(stack, damage);
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

		@Override
		public void copyOwner(ItemStack toStack, ItemStack fromStack) {
			super.copyOwner(toStack, fromStack);
			if (toStack.getItem() instanceof Base && fromStack.getItem() instanceof Base) {
				this.setColor(toStack, ((Base)fromStack.getItem()).getColor(fromStack));
			}
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
			if (wearingAny(entity) && ItemJutsu.canTarget(entity) && !entity.isRiding()
			 && attacker instanceof EntityLivingBase && !attacker.world.isRemote) {
			 	if (entity.getRNG().nextFloat() < 0.5f) {
			    	List<BlockPos> list = ProcedureUtils.getAllAirBlocks(entity.world, entity.getEntityBoundingBox().grow(2.5d));
			    	for (int i = 0; i < list.size(); i++) {
			    		BlockPos pos = list.get(entity.getRNG().nextInt(list.size()));
			    		Material material = entity.world.getBlockState(pos.down()).getMaterial();
			    		if ((material.isSolid() || material == material.WATER)
			    		 && attacker.getDistanceSqToCenter(pos) > 6.25d && ProcedureUtils.isSpaceOpenToStandOn(entity, pos)) {
				 			event.setCanceled(true);
				 			entity.setPositionAndUpdate(0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ());
				 			break;
			    		}
			    	}
			 	}
				if (entity instanceof EntityPlayer) {
					this.lockOnTarget(entity, (EntityLivingBase)attacker, 300);
				}
			}
		}

		@SubscribeEvent
		public void onPlayerTick(TickEvent.PlayerTickEvent event) {
			EntityPlayer entity = event.player;
			if (event.phase == TickEvent.Phase.END && this.hasTargetLockOnEntity(entity)) {
				int remaining = this.targetLockTicksRemaining(entity);
				EntityLivingBase target = this.getLockedTarget(entity);
				if (!entity.world.isRemote && (remaining <= 0 || target == null || !target.isEntityAlive() || target.getDistanceSq(entity) > 1024d)) {
					this.unlockOnTarget(entity);
				} else if (target != null) {
					if (entity.world.isRemote) {
						ProcedureOnLivingUpdate.setGlowingFor(target, 3);
					}
					if (entity.getEntityData().getBoolean("shouldTargetLockOnEntity")) {
						Vec3d vec2 = target.getPositionEyes(1f).subtract(entity.getPositionEyes(1f));
						entity.rotationYaw = ProcedureUtils.getYawFromVec(vec2);
						entity.rotationPitch = ProcedureUtils.getPitchFromVec(vec2);
					}
					this.lockOnTarget(entity, target, remaining - 1);
				}
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onMouseEvent(MouseEvent event) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (FMLClientHandler.instance().isGUIOpen(net.minecraft.client.gui.GuiChat.class) || player == null) {
				return;
			}
			if (event.getButton() == 1 && this.hasTargetLockOnEntity(player)) {
				//boolean flag = player.getEntityData().getBoolean("shouldTargetLockOnEntity");
				boolean flag = !event.isButtonstate();
				player.getEntityData().setBoolean("shouldTargetLockOnEntity", !flag);
				ProcedureSync.EntityNBTTag.sendToServer(player, "shouldTargetLockOnEntity", !flag);
			}
		}

		@SubscribeEvent
		public void onEntitySpawn(EntityJoinWorldEvent event) {
			if (event.getEntity() instanceof EntityPlayerMP) {
				this.unlockOnTarget((EntityLivingBase)event.getEntity());
			}
		}

		private void lockOnTarget(EntityLivingBase entity, EntityLivingBase target, int ticks) {
			if (!entity.world.isRemote) {
				entity.getEntityData().setInteger("targetLockOnEntityId", target.getEntityId());
				entity.getEntityData().setInteger("targetLockOnEntityTicksRemaining", ticks);
				if (entity instanceof EntityPlayerMP) {
					ProcedureSync.EntityNBTTag.sendToSelf((EntityPlayerMP)entity, "targetLockOnEntityId", target.getEntityId());
				}
			}
		}
	
		private void unlockOnTarget(EntityLivingBase entity) {
			if (!entity.world.isRemote) {
				entity.getEntityData().removeTag("targetLockOnEntityId");
				entity.getEntityData().removeTag("targetLockOnEntityTicksRemaining");
				entity.getEntityData().removeTag("shouldTargetLockOnEntity");
				if (entity instanceof EntityPlayerMP) {
					ProcedureSync.EntityNBTTag.sendToSelf((EntityPlayerMP)entity, "targetLockOnEntityId");
					ProcedureSync.EntityNBTTag.sendToSelf((EntityPlayerMP)entity, "shouldTargetLockOnEntity");
				}
			}
		}
	
		private boolean hasTargetLockOnEntity(EntityLivingBase entity) {
			return entity.getEntityData().hasKey("targetLockOnEntityId");
		}
	
		@Nullable
		private EntityLivingBase getLockedTarget(EntityLivingBase entity) {
			Entity target = entity.world.getEntityByID(entity.getEntityData().getInteger("targetLockOnEntityId"));
			return target instanceof EntityLivingBase ? (EntityLivingBase)target : null;
		}
	
		private int targetLockTicksRemaining(EntityLivingBase entity) {
			return entity.getEntityData().getInteger("targetLockOnEntityTicksRemaining");
		}
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("SHARINGAN", "narutomod:sharingan_",
		 1024, new int[]{2, 5, 6, 10}, 0, null, 0.0F);
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
