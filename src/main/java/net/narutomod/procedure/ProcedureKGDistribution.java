package net.narutomod.procedure;

import net.narutomod.item.ItemYooton;
import net.narutomod.item.ItemSuiton;
import net.narutomod.item.ItemShikotsumyaku;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemShakuton;
import net.narutomod.item.ItemRanton;
import net.narutomod.item.ItemRaiton;
import net.narutomod.item.ItemKaton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemJiton;
import net.narutomod.item.ItemJinton;
import net.narutomod.item.ItemHyoton;
import net.narutomod.item.ItemFutton;
import net.narutomod.item.ItemFuton;
import net.narutomod.item.ItemDoton;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemBakuton;
import net.narutomod.gui.GuiScrollGenjutsuGui;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import java.util.Map;
import java.util.Iterator;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureKGDistribution extends ElementsNarutomodMod.ModElement {
	public ProcedureKGDistribution(ElementsNarutomodMod instance) {
		super(instance, 847);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure KGDistribution!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure KGDistribution!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure KGDistribution!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure KGDistribution!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure KGDistribution!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		ItemStack stack = ItemStack.EMPTY;
		double rngbase = 0;
		double sharingan_weight = 0;
		double byakugan_weight = 0;
		double shikotsumyaku_weight = 0;
		double yooton_weight = 0;
		double shakuton_weight = 0;
		double hyoton_weight = 0;
		double jiton_weight = 0;
		double bakuton_weight = 0;
		double ranton_weight = 0;
		double futton_weight = 0;
		double kekkeitota_weight = 0;
		double jinchuriki_weight = 0;
		sharingan_weight = (double) 10;
		byakugan_weight = (double) 10;
		shikotsumyaku_weight = (double) 10;
		yooton_weight = (double) 10;
		shakuton_weight = (double) 10;
		hyoton_weight = (double) 10;
		jiton_weight = (double) 10;
		bakuton_weight = (double) 10;
		ranton_weight = (double) 10;
		futton_weight = (double) 10;
		kekkeitota_weight = (double) 5;
		jinchuriki_weight = (double) 5;
		rngbase = (double) ((ModConfig.SPAWN_AS_JINCHURIKI && EntityBijuManager.availableBijus() > 0 ? jinchuriki_weight : 0)
				+ (((((((((((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight)) + (yooton_weight)) + (shakuton_weight))
						+ (hyoton_weight)) + (jiton_weight)) + (bakuton_weight)) + (ranton_weight)) + (futton_weight)) + (kekkeitota_weight)));
		if ((((EntityLivingBase) entity).getRNG().nextDouble() <= ((byakugan_weight) / (rngbase)))) {
			stack = new ItemStack(ItemByakugan.helmet, (int) (1));
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:byakuganopened")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:byakuganopened"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
		} else if ((((EntityLivingBase) entity).getRNG().nextDouble() <= ((sharingan_weight) / ((rngbase) - (byakugan_weight))))) {
			GuiScrollGenjutsuGui.giveGenjutsu((EntityPlayer) entity);
			stack = new ItemStack(ItemSharingan.helmet, (int) (1));
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:sharinganopened")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:sharinganopened"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
		} else if ((((EntityLivingBase) entity).getRNG()
				.nextDouble() <= ((shikotsumyaku_weight) / ((rngbase) - ((sharingan_weight) + (byakugan_weight)))))) {
			stack = new ItemStack(ItemShikotsumyaku.block, (int) (1));
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:shikotsumyaku_acquired")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:shikotsumyaku_acquired"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
		} else if (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemDoton.block)
				|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemKaton.block))
				&& (((EntityLivingBase) entity).getRNG()
						.nextDouble() <= (((yooton_weight) / ((rngbase) - (((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight))))
								/ 0.4)))) {
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:yooton_acquired")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:yooton_acquired"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemKaton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemKaton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemDoton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemDoton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			stack = new ItemStack(ItemYooton.block, (int) (1));
		} else if (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemFuton.block)
				|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemKaton.block))
				&& (((EntityLivingBase) entity).getRNG().nextDouble() <= (((shakuton_weight)
						/ ((rngbase) - ((((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight)) + (yooton_weight)))) / 0.4)))) {
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:shakuton_acquired")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:shakuton_acquired"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemFuton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemFuton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemKaton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemKaton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			stack = new ItemStack(ItemShakuton.block, (int) (1));
		} else if (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemFuton.block)
				|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemSuiton.block))
				&& (((EntityLivingBase) entity).getRNG().nextDouble() <= (((hyoton_weight)
						/ ((rngbase) - (((((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight)) + (yooton_weight)) + (shakuton_weight))))
						/ 0.4)))) {
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:hyoton_acquired")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:hyoton_acquired"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemFuton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemFuton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemSuiton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemSuiton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			stack = new ItemStack(ItemHyoton.block, (int) (1));
		} else if (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemFuton.block)
				|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemDoton.block))
				&& (((EntityLivingBase) entity).getRNG().nextDouble() <= (((jiton_weight)
						/ ((rngbase) - ((((((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight)) + (yooton_weight)) + (shakuton_weight))
								+ (hyoton_weight))))
						/ 0.4)))) {
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:jiton_acquired")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:jiton_acquired"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemFuton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemFuton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemDoton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemDoton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			stack = new ItemStack(ItemJiton.block, (int) (1));
		} else if (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemRaiton.block)
				|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemDoton.block))
				&& (((EntityLivingBase) entity).getRNG().nextDouble() <= (((bakuton_weight)
						/ ((rngbase) - (((((((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight)) + (yooton_weight)) + (shakuton_weight))
								+ (hyoton_weight)) + (jiton_weight))))
						/ 0.4)))) {
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:bakuton_acquired")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:bakuton_acquired"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemDoton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemDoton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemRaiton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemRaiton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			stack = new ItemStack(ItemBakuton.block, (int) (1));
		} else if (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemRaiton.block)
				|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemSuiton.block))
				&& (((EntityLivingBase) entity).getRNG()
						.nextDouble() <= (((ranton_weight) / ((rngbase)
								- ((((((((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight)) + (yooton_weight)) + (shakuton_weight))
										+ (hyoton_weight)) + (jiton_weight)) + (bakuton_weight))))
								/ 0.4)))) {
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:ranton_acquired")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:ranton_acquired"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemRaiton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemRaiton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemSuiton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemSuiton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			stack = new ItemStack(ItemRanton.block, (int) (1));
		} else if (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemSuiton.block)
				|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemKaton.block))
				&& (((EntityLivingBase) entity).getRNG()
						.nextDouble() <= (((futton_weight) / ((rngbase)
								- (((((((((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight)) + (yooton_weight)) + (shakuton_weight))
										+ (hyoton_weight)) + (jiton_weight)) + (bakuton_weight)) + (ranton_weight))))
								/ 0.4)))) {
			if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
					? ((EntityPlayerMP) entity).getAdvancements()
							.getProgress(((WorldServer) (entity).world).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:futton_acquired")))
							.isDone()
					: false))) {
				if (entity instanceof EntityPlayerMP) {
					Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
							.getAdvancement(new ResourceLocation("narutomod:futton_acquired"));
					AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemaningCriteria().iterator();
						while (_iterator.hasNext()) {
							String _criterion = (String) _iterator.next();
							((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
						}
					}
				}
				world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemSuiton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemSuiton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemKaton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemKaton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			stack = new ItemStack(ItemFutton.block, (int) (1));
		} else if (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemKaton.block)
				|| (ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemDoton.block)
						|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemFuton.block)))
				&& (((EntityLivingBase) entity).getRNG()
						.nextDouble() <= (((kekkeitota_weight) / ((rngbase)
								- ((((((((((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight)) + (yooton_weight)) + (shakuton_weight))
										+ (hyoton_weight)) + (jiton_weight)) + (bakuton_weight)) + (ranton_weight)) + (futton_weight))))
								/ 0.6)))) {
			if (entity instanceof EntityPlayerMP) {
				Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
						.getAdvancement(new ResourceLocation("narutomod:kekkei_tota_awakened"));
				AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
				if (!_ap.isDone()) {
					Iterator _iterator = _ap.getRemaningCriteria().iterator();
					while (_iterator.hasNext()) {
						String _criterion = (String) _iterator.next();
						((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
					}
				}
			}
			world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
					.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemKaton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemKaton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemDoton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemDoton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			if ((!((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemFuton.block, (int) (1)))
					: false))) {
				stack = new ItemStack(ItemFuton.block, (int) (1));
				((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
				if (entity instanceof EntityPlayer) {
					ItemStack _setstack = (stack);
					_setstack.setCount(1);
					ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
				}
			}
			stack = new ItemStack(ItemJinton.block, (int) (1));
		} else {
			stack = new ItemStack(Blocks.AIR, (int) (1));
			if (((rngbase) > (((((((((((sharingan_weight) + (byakugan_weight)) + (shikotsumyaku_weight)) + (yooton_weight)) + (shakuton_weight))
					+ (hyoton_weight)) + (jiton_weight)) + (bakuton_weight)) + (ranton_weight)) + (futton_weight)) + (kekkeitota_weight)))) {
				int tails = EntityBijuManager.getRandomAvailableBiju();
				if (EntityBijuManager.setVesselByTails(entity, tails)) {
					world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
							.getObject(new ResourceLocation("ui.toast.challenge_complete")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
					{
						MinecraftServer mcserv = FMLCommonHandler.instance().getMinecraftServerInstance();
						if (mcserv != null)
							mcserv.getPlayerList()
									.sendMessage(new TextComponentString(
											net.minecraft.util.text.translation.I18n.translateToLocalFormatted("chattext.biju.playerisjinchuriki",
													entity.getName(), EntityBijuManager.getNameOfJinchurikisBiju((EntityPlayer) entity))));
					}
				}
			}
		}
		if (stack.getItem() instanceof ItemDojutsu.Base) {
			((ItemDojutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
			entity.getEntityData().setLong(NarutomodModVariables.MostRecentWornDojutsuTime, world.getTotalWorldTime());
		} else if (stack.getItem() instanceof ItemJutsu.Base) {
			((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
			((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
		}
		if (entity instanceof EntityPlayer) {
			ItemStack _setstack = (stack);
			_setstack.setCount(1);
			ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
		}
	}
}
