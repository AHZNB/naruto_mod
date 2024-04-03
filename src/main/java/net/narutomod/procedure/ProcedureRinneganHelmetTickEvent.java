package net.narutomod.procedure;

import net.narutomod.potion.PotionFlight;
import net.narutomod.item.ItemTenseiganChakraMode;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemSixPathSenjutsu;
import net.narutomod.item.ItemSageStaff;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.item.ItemBlackReceiver;
import net.narutomod.item.ItemAsuraPathArmor;
import net.narutomod.item.ItemAsuraCanon;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import java.util.Map;
import java.util.Iterator;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureRinneganHelmetTickEvent extends ElementsNarutomodMod.ModElement {
	public ProcedureRinneganHelmetTickEvent(ElementsNarutomodMod instance) {
		super(instance, 39);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure RinneganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure RinneganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure RinneganHelmetTickEvent!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		World world = (World) dependencies.get("world");
		boolean isRinnesharingan = false;
		ItemStack stack1 = ItemStack.EMPTY;
		entity.fallDistance = (float) (0);
		isRinnesharingan = (boolean) ((itemstack).hasTagCompound()
				&& (itemstack).getTagCompound().getBoolean((NarutomodModVariables.RINNESHARINGAN_ACTIVATED)));
		if ((!(world.isRemote))) {
			if (((isRinnesharingan) && (!(((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY)
					.getItem() == new ItemStack(ItemTenseiganChakraMode.block, (int) (1)).getItem())))) {
				ProcedureUtils.purgeHarmfulEffects((EntityLivingBase) entity);
				if (((entity.ticksExisted % 20) == 2)) {
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionFlight.potion, (int) 22, (int) 1, (false), (false)));
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SATURATION, (int) 22, (int) 1, (false), (false)));
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, (int) 22, (int) 9, (false), (false)));
				}
				if (((((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHealth() : -1) < ((entity instanceof EntityLivingBase)
						? ((EntityLivingBase) entity).getMaxHealth()
						: -1)) && (((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHealth() : -1) > 0))) {
					((EntityLivingBase) entity).heal(1.0f);
				}
				if ((!(((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(2) : ItemStack.EMPTY)
						.getItem() == new ItemStack(ItemRinnegan.body, (int) (1)).getItem()))) {
					ProcedureUtils.swapItemToSlot((EntityPlayer) entity, EntityEquipmentSlot.CHEST, new ItemStack(ItemRinnegan.body));
				}
				if ((!(((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(1) : ItemStack.EMPTY)
						.getItem() == new ItemStack(ItemRinnegan.legs, (int) (1)).getItem()))) {
					ProcedureUtils.swapItemToSlot((EntityPlayer) entity, EntityEquipmentSlot.LEGS, new ItemStack(ItemRinnegan.legs));
				}
				if ((!(entity.getEntityData().getBoolean("hasAnyGuiOpen")))) {
					if ((!((entity instanceof EntityPlayer)
							? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemSixPathSenjutsu.block, (int) (1)))
							: false))) {
						if (entity.getEntityData().hasKey("6pSenjutsuItem", 10)) {
							stack1 = new ItemStack(entity.getEntityData().getCompoundTag("6pSenjutsuItem"));
							entity.getEntityData().removeTag("6pSenjutsuItem");
						} else {
							stack1 = new ItemStack(ItemSixPathSenjutsu.block, (int) (1));
						}
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack1);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
					}
					if ((!((entity instanceof EntityPlayer)
							? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemSageStaff.block, (int) (1)))
							: false))) {
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = new ItemStack(ItemSageStaff.block, (int) (1));
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
					}
				}
			} else {
				if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
						? ((EntityPlayerMP) entity).getAdvancements()
								.getProgress(((WorldServer) (entity).world).getAdvancementManager()
										.getAdvancement(new ResourceLocation("narutomod:rinneganawakened")))
								.isDone()
						: false)) {
					stack1 = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemNinjutsu.block);
					if (((stack1 != null && ProcedureUtils.isOriginalOwner((EntityLivingBase) entity, stack1))
							&& (!((ItemNinjutsu.RangedItem) stack1.getItem()).isJutsuEnabled(stack1, ItemNinjutsu.LIMBOCLONE)
									&& !((ItemNinjutsu.RangedItem) stack1.getItem()).isJutsuEnabled(stack1, ItemNinjutsu.AMENOTEJIKARA)))) {
						((ItemNinjutsu.RangedItem) stack1.getItem()).enableJutsu(stack1,
								ProcedureUtils.rngBoolean() ? ItemNinjutsu.LIMBOCLONE : ItemNinjutsu.AMENOTEJIKARA, true);
					}
				}
				if (((entity.ticksExisted % 20) == 2)) {
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, (int) 22, (int) 2, (false), (false)));
				}
				if (entity.equals(EntityTenTails.getBijuManager().getJinchurikiPlayer())) {
					{
						ItemStack _stack = (itemstack);
						if (!_stack.hasTagCompound())
							_stack.setTagCompound(new NBTTagCompound());
						_stack.getTagCompound().setBoolean((NarutomodModVariables.RINNESHARINGAN_ACTIVATED), (true));
					}
					if ((((itemstack).getItem() == new ItemStack(ItemRinnegan.helmet, (int) (1)).getItem())
							&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
									? ((EntityPlayerMP) entity).getAdvancements()
											.getProgress(((WorldServer) (entity).world).getAdvancementManager()
													.getAdvancement(new ResourceLocation("narutomod:rinnesharinganactivated")))
											.isDone()
									: false)))) {
						if (entity instanceof EntityPlayerMP) {
							Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:rinnesharinganactivated"));
							AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
							if (!_ap.isDone()) {
								Iterator _iterator = _ap.getRemaningCriteria().iterator();
								while (_iterator.hasNext()) {
									String _criterion = (String) _iterator.next();
									((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
								}
							}
						}
						world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
								(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
										.getObject(new ResourceLocation("ui.toast.challenge_complete")),
								SoundCategory.NEUTRAL, (float) 1, (float) 1);
					} else if ((((itemstack).getItem() == new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem())
							&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
									? ((EntityPlayerMP) entity).getAdvancements()
											.getProgress(((WorldServer) (entity).world).getAdvancementManager()
													.getAdvancement(new ResourceLocation("narutomod:tensei_byakugan_activated")))
											.isDone()
									: false)))) {
						if (entity instanceof EntityPlayerMP) {
							Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:tensei_byakugan_activated"));
							AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
							if (!_ap.isDone()) {
								Iterator _iterator = _ap.getRemaningCriteria().iterator();
								while (_iterator.hasNext()) {
									String _criterion = (String) _iterator.next();
									((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
								}
							}
						}
						world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
								(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
										.getObject(new ResourceLocation("ui.toast.challenge_complete")),
								SoundCategory.NEUTRAL, (float) 1, (float) 1);
					}
				} else {
					if ((((itemstack).getItem() == new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem())
							&& (ItemTenseigan.canUseChakraMode(itemstack, (EntityPlayer) entity) && (!((entity instanceof EntityPlayer)
									? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemTenseiganChakraMode.block, (int) (1)))
									: false))))) {
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = new ItemStack(ItemTenseiganChakraMode.block, (int) (1));
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
					}
					if (((!(((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY)
							.getItem() == new ItemStack(ItemTenseiganChakraMode.block, (int) (1)).getItem()))
							&& (((itemstack).hasTagCompound() ? (itemstack).getTagCompound().getDouble("which_path") : -1) == 1))) {
						if ((!(((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(2) : ItemStack.EMPTY)
								.getItem() == new ItemStack(ItemAsuraPathArmor.body, (int) (1)).getItem()))) {
							ProcedureUtils.swapItemToSlot((EntityPlayer) entity, EntityEquipmentSlot.CHEST, new ItemStack(ItemAsuraPathArmor.body));
							ProcedureUtils.swapItemToSlot((EntityPlayer) entity, EntityEquipmentSlot.OFFHAND, new ItemStack(ItemAsuraCanon.block));
						}
					} else {
						if (entity instanceof EntityPlayer)
							((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemAsuraPathArmor.body, (int) (1)).getItem(), -1,
									(int) (-1), null);
						if (entity instanceof EntityPlayer)
							((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemAsuraCanon.block, (int) (1)).getItem(), -1,
									(int) (-1), null);
					}
					if ((!((entity instanceof EntityPlayer)
							? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemBlackReceiver.block, (int) (1)))
							: false))) {
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = new ItemStack(ItemBlackReceiver.block, (int) (1));
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
					}
				}
			}
			if (((entity.ticksExisted % 20) == 2)) {
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, (int) 22, (int) 4, (false), (false)));
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, (int) 230, (int) 0, (false), (false)));
			}
		}
		if (((entity.getEntityData().getBoolean("chibakutensei_active")) && ((entity.ticksExisted % 20) == 2))) {
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionFlight.potion, (int) 200, (int) 1, (false), (false)));
		}
	}
}
