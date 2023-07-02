package net.narutomod.procedure;

import net.narutomod.item.ItemSuiton;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.item.ItemMokuton;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemDoton;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
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
public class ProcedureWhiteZetsuFleshFoodEaten extends ElementsNarutomodMod.ModElement {
	public ProcedureWhiteZetsuFleshFoodEaten(ElementsNarutomodMod instance) {
		super(instance, 244);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure WhiteZetsuFleshFoodEaten!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure WhiteZetsuFleshFoodEaten!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		ItemStack rinneganstack = ItemStack.EMPTY;
		boolean flag = false;
		if ((!(world.isRemote))) {
			if (ProcedureUtils.isWearingMangekyo((EntityLivingBase) entity)) {
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, (int) 10, (int) 4, (false), (false)));
			} else {
				entity.attackEntityFrom(DamageSource.STARVE, (float) ((Math.random() * 220) + 20));
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HUNGER, (int) 300, (int) 1, (false), (false)));
			}
			if ((((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:eternalmangekyoachieved")))
							.isDone()
					: false)
					&& (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
							.getItem() == new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)).getItem()))
					&& (((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:rinneganawakened")))
									.isDone()
							: false))
							&& (!((entity instanceof EntityPlayer)
									? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemRinnegan.helmet, (int) (1)))
									: false)))
							&& (Math.random() < 0.2)))) {
				rinneganstack = new ItemStack(ItemRinnegan.helmet, (int) (1));
				((ItemDojutsu.Base) rinneganstack.getItem()).setOwner(rinneganstack, (EntityLivingBase) entity);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (rinneganstack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:rinneganawakened"));
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
			if ((((((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemSuiton.block, (int) (1)))
					: false)
					&& ((entity instanceof EntityPlayer)
							? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemDoton.block, (int) (1)))
							: false))
					&& (!((entity instanceof EntityPlayer)
							? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemMokuton.block, (int) (1)))
							: false)))
					&& (entity.isEntityAlive()))) {
				if ((world.getGameRules().getBoolean("keepInventory") || world.getGameRules().getBoolean("keepNinjaXp"))) {
					flag = (boolean) (Math.random() < 0.01);
				} else {
					flag = (boolean) (Math.random() < 0.1);
				}
				if ((flag)) {
					if (entity instanceof EntityPlayer) {
						ItemStack _setstack = new ItemStack(ItemMokuton.block, (int) (1));
						_setstack.setCount(1);
						ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
					}
					if (((entity instanceof EntityPlayer)
							? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemNinjutsu.block, (int) (1)))
							: false)) {
						ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemNinjutsu.block);
						((ItemNinjutsu.RangedItem) stack.getItem()).enableJutsu(stack, ItemNinjutsu.KAGEBUNSHIN, true);
					}
					if (entity instanceof EntityPlayerMP) {
						Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
								.getAdvancement(new ResourceLocation("narutomod:mokuton_acquired"));
						AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
						if (!_ap.isDone()) {
							Iterator _iterator = _ap.getRemaningCriteria().iterator();
							while (_iterator.hasNext()) {
								String _criterion = (String) _iterator.next();
								((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
							}
						}
					}
				}
			}
		}
	}
}
