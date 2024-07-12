package net.narutomod.procedure;

import net.narutomod.item.ItemYooton;
import net.narutomod.item.ItemSuiton;
import net.narutomod.item.ItemShikotsumyaku;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemShakuton;
import net.narutomod.item.ItemRanton;
import net.narutomod.item.ItemRaiton;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.item.ItemKaton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemJiton;
import net.narutomod.item.ItemJinton;
import net.narutomod.item.ItemIryoJutsu;
import net.narutomod.item.ItemHyoton;
import net.narutomod.item.ItemFutton;
import net.narutomod.item.ItemFuton;
import net.narutomod.item.ItemDoton;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemBakuton;
import net.narutomod.gui.GuiScrollGenjutsuGui;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
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
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnPlayerPostTick extends ElementsNarutomodMod.ModElement {
	public ProcedureOnPlayerPostTick(ElementsNarutomodMod instance) {
		super(instance, 154);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure OnPlayerPostTick!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure OnPlayerPostTick!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure OnPlayerPostTick!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure OnPlayerPostTick!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure OnPlayerPostTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		ItemStack stack = ItemStack.EMPTY;
		double rand = 0;
		double rngbase = 0;
		boolean achievedMedical = false;
		if (((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).experienceLevel : 0) >= 10)
				&& ((entity.getEntityData().getDouble((NarutomodModVariables.BATTLEXP))) > 0))) {
			if (((!(world.isRemote)) && (!(entity.getEntityData().getBoolean((NarutomodModVariables.FirstGotNinjutsu)))))) {
				entity.getEntityData().setBoolean((NarutomodModVariables.FirstGotNinjutsu), (true));
				if ((!((entity instanceof EntityPlayer)
						? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemNinjutsu.block, (int) (1)))
						: false))) {
					stack = new ItemStack(ItemNinjutsu.block, (int) (1));
					((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
					if (entity instanceof EntityPlayer) {
						ItemStack _setstack = (stack);
						_setstack.setCount(1);
						ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
					}
				}
				if (((((!((entity instanceof EntityPlayer)
						? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemKaton.block, (int) (1)))
						: false))
						&& (!((entity instanceof EntityPlayer)
								? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemSuiton.block, (int) (1)))
								: false)))
						&& ((!((entity instanceof EntityPlayer)
								? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemRaiton.block, (int) (1)))
								: false))
								&& (!((entity instanceof EntityPlayer)
										? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemFuton.block, (int) (1)))
										: false))))
						&& (!((entity instanceof EntityPlayer)
								? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemDoton.block, (int) (1)))
								: false)))) {
					if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:bakuton_acquired")))
									.isDone()
							: false)) {
						stack = new ItemStack(ItemDoton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemRaiton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemBakuton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
					} else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:ranton_acquired")))
									.isDone()
							: false)) {
						stack = new ItemStack(ItemSuiton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemRaiton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemRanton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
					} else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:futton_acquired")))
									.isDone()
							: false)) {
						stack = new ItemStack(ItemSuiton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemKaton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemFutton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
					} else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:jiton_acquired")))
									.isDone()
							: false)) {
						stack = new ItemStack(ItemFuton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemDoton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemJiton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
					} else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:yooton_acquired")))
									.isDone()
							: false)) {
						stack = new ItemStack(ItemDoton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemKaton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemYooton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
					} else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:hyoton_acquired")))
									.isDone()
							: false)) {
						stack = new ItemStack(ItemFuton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemSuiton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemHyoton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
					} else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:shakuton_acquired")))
									.isDone()
							: false)) {
						stack = new ItemStack(ItemKaton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemFuton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemShakuton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
					} else if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:kekkei_tota_awakened")))
									.isDone()
							: false)) {
						stack = new ItemStack(ItemKaton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemDoton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemFuton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						stack = new ItemStack(ItemJinton.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
					} else {
						rand = (double) ((EntityLivingBase) entity).getRNG().nextDouble();
						if (((rand) <= 0.2)) {
							stack = new ItemStack(ItemKaton.block, (int) (1));
						} else if (((rand) <= 0.4)) {
							stack = new ItemStack(ItemSuiton.block, (int) (1));
						} else if (((rand) <= 0.6)) {
							stack = new ItemStack(ItemRaiton.block, (int) (1));
						} else if (((rand) <= 0.8)) {
							stack = new ItemStack(ItemFuton.block, (int) (1));
						} else {
							stack = new ItemStack(ItemDoton.block, (int) (1));
						}
					}
					((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
					if (entity instanceof EntityPlayer) {
						ItemStack _setstack = (stack);
						_setstack.setCount(1);
						ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
					}
					if ((!ItemSharingan.hasAny((EntityPlayer) entity)
							&& (((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
									? ((EntityPlayerMP) entity).getAdvancements()
											.getProgress(((WorldServer) (entity).world).getAdvancementManager()
													.getAdvancement(new ResourceLocation("narutomod:sharinganopened")))
											.isDone()
									: false))) {
						GuiScrollGenjutsuGui.giveGenjutsu((EntityPlayer) entity);
						stack = new ItemStack(ItemSharingan.helmet, (int) (1));
						((ItemDojutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
						entity.getEntityData().setLong(NarutomodModVariables.MostRecentWornDojutsuTime, world.getTotalWorldTime());
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
					} else if (((!((entity instanceof EntityPlayer)
							? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemByakugan.helmet, (int) (1)))
							: false))
							&& (((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
									? ((EntityPlayerMP) entity).getAdvancements()
											.getProgress(((WorldServer) (entity).world).getAdvancementManager()
													.getAdvancement(new ResourceLocation("narutomod:byakuganopened")))
											.isDone()
									: false))) {
						stack = new ItemStack(ItemByakugan.helmet, (int) (1));
						((ItemDojutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
						entity.getEntityData().setLong(NarutomodModVariables.MostRecentWornDojutsuTime, world.getTotalWorldTime());
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
					} else if (((!((entity instanceof EntityPlayer)
							? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemShikotsumyaku.block, (int) (1)))
							: false))
							&& (((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
									? ((EntityPlayerMP) entity).getAdvancements()
											.getProgress(((WorldServer) (entity).world).getAdvancementManager()
													.getAdvancement(new ResourceLocation("narutomod:shikotsumyaku_acquired")))
											.isDone()
									: false))) {
						stack = new ItemStack(ItemShikotsumyaku.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
						((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
					}
				}
				if ((!((entity instanceof EntityPlayer)
						? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemIryoJutsu.block, (int) (1)))
						: false))) {
					achievedMedical = (boolean) (((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:achievementmedicalgenin")))
									.isDone()
							: false);
					if (((achievedMedical) || ((!(entity.getEntityData().getBoolean("MedicalNinjaChecked")))
							&& (((EntityLivingBase) entity).getRNG().nextDouble() <= 0.25)))) {
						stack = new ItemStack(ItemIryoJutsu.block, (int) (1));
						((ItemJutsu.Base) stack.getItem()).setOwner(stack, (EntityLivingBase) entity);
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (stack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						if ((!(achievedMedical))) {
							if (entity instanceof EntityPlayerMP) {
								Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
										.getAdvancement(new ResourceLocation("narutomod:achievementmedicalgenin"));
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
					entity.getEntityData().setBoolean("MedicalNinjaChecked", (true));
				}
			}
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				ProcedureBasicNinjaSkills.executeProcedure($_dependencies);
			}
		}
		if ((((entity.ticksExisted % 20) == 0) && (!(world.isRemote)))) {
			if (ItemDojutsu.hasAnyDojutsu((EntityPlayer) entity)) {
				if ((!ItemSharingan.isWearingMangekyo((EntityPlayer) entity) && (entity.getEntityData().getBoolean("susanoo_activated")))) {
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("entity", entity);
						$_dependencies.put("world", world);
						ProcedureSusanoo.executeProcedure($_dependencies);
					}
				}
				if (ItemSharingan.isBlinded((EntityPlayer) entity)) {
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, (int) 1200, (int) 0, (false), (false)));
				}
			} else if ((((!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemYooton.block)
					&& !ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemRanton.block))
					&& (!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemHyoton.block)
							&& !ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemJiton.block)))
					&& ((!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemShakuton.block)
							&& !ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemBakuton.block))
							&& ((!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemJinton.block)
									&& !ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemFutton.block))
									&& (!ProcedureUtils.hasItemInInventory((EntityPlayer) entity, ItemShikotsumyaku.block)
											&& !EntityBijuManager.isJinchuriki((EntityPlayer) entity)))))) {
				if ((entity.getEntityData().getBoolean("susanoo_activated"))) {
					{
						Map<String, Object> $_dependencies = new HashMap<>();
						$_dependencies.put("entity", entity);
						$_dependencies.put("world", world);
						ProcedureSusanoo.executeProcedure($_dependencies);
					}
				}
				if ((PlayerTracker.Deaths.mostRecentTime((EntityPlayer) entity) < entity.getEntityData()
						.getLong(NarutomodModVariables.MostRecentWornDojutsuTime))) {
					if ((!((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false))) {
						if (entity instanceof EntityLivingBase)
							((EntityLivingBase) entity)
									.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, (int) 1200, (int) 0, (false), (false)));
					}
				} else if ((((entity.getEntityData().getDouble((NarutomodModVariables.BATTLEXP))) >= 300)
						&& (((((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
								? ((EntityPlayerMP) entity).getAdvancements()
										.getProgress(((WorldServer) (entity).world).getAdvancementManager()
												.getAdvancement(new ResourceLocation("narutomod:sharinganopened")))
										.isDone()
								: false))
								&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
										? ((EntityPlayerMP) entity).getAdvancements()
												.getProgress(((WorldServer) (entity).world).getAdvancementManager()
														.getAdvancement(new ResourceLocation("narutomod:byakuganopened")))
												.isDone()
										: false)))
								&& ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
										? ((EntityPlayerMP) entity).getAdvancements()
												.getProgress(((WorldServer) (entity).world).getAdvancementManager()
														.getAdvancement(new ResourceLocation("narutomod:shakuton_acquired")))
												.isDone()
										: false))
										&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
												? ((EntityPlayerMP) entity).getAdvancements()
														.getProgress(((WorldServer) (entity).world).getAdvancementManager()
																.getAdvancement(new ResourceLocation("narutomod:yooton_acquired")))
														.isDone()
												: false))))
								&& (((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
										? ((EntityPlayerMP) entity).getAdvancements()
												.getProgress(((WorldServer) (entity).world).getAdvancementManager()
														.getAdvancement(new ResourceLocation("narutomod:bakuton_acquired")))
												.isDone()
										: false))
										&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
												? ((EntityPlayerMP) entity).getAdvancements()
														.getProgress(((WorldServer) (entity).world).getAdvancementManager()
																.getAdvancement(new ResourceLocation("narutomod:ranton_acquired")))
														.isDone()
												: false)))
										&& (((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
												? ((EntityPlayerMP) entity).getAdvancements()
														.getProgress(((WorldServer) (entity).world).getAdvancementManager()
																.getAdvancement(new ResourceLocation("narutomod:hyoton_acquired")))
														.isDone()
												: false))
												&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
														? ((EntityPlayerMP) entity).getAdvancements()
																.getProgress(((WorldServer) (entity).world).getAdvancementManager()
																		.getAdvancement(new ResourceLocation("narutomod:jiton_acquired")))
																.isDone()
														: false)))
												&& ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
														? ((EntityPlayerMP) entity).getAdvancements()
																.getProgress(((WorldServer) (entity).world).getAdvancementManager()
																		.getAdvancement(new ResourceLocation("narutomod:futton_acquired")))
																.isDone()
														: false))
														&& ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
																? ((EntityPlayerMP) entity).getAdvancements()
																		.getProgress(
																				((WorldServer) (entity).world).getAdvancementManager().getAdvancement(
																						new ResourceLocation("narutomod:shikotsumyaku_acquired")))
																		.isDone()
																: false))
																&& (!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
																		? ((EntityPlayerMP) entity).getAdvancements()
																				.getProgress(((WorldServer) (entity).world).getAdvancementManager()
																						.getAdvancement(new ResourceLocation(
																								"narutomod:kekkei_tota_awakened")))
																				.isDone()
																		: false)))))))
								&& (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).experienceLevel : 0) >= 10)))) {
					if (((((EntityLivingBase) entity).getRNG().nextFloat() <= 0.001)
							&& ((entity instanceof EntityPlayer) && (((ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemDoton.block)
									|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemFuton.block))
									|| (ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemKaton.block)
											|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemSuiton.block)))
									|| ItemJutsu.hasOwnerMatchingItemstack((EntityPlayer) entity, ItemRaiton.block))))) {
						{
							Map<String, Object> $_dependencies = new HashMap<>();
							$_dependencies.put("entity", entity);
							$_dependencies.put("x", x);
							$_dependencies.put("y", y);
							$_dependencies.put("z", z);
							$_dependencies.put("world", world);
							ProcedureKGDistribution.executeProcedure($_dependencies);
						}
					}
				}
			}
		}
		{
			Map<String, Object> $_dependencies = new HashMap<>();
			$_dependencies.put("entity", entity);
			ProcedureSyncInventory.executeProcedure($_dependencies);
		}
		{
			Map<String, Object> $_dependencies = new HashMap<>();
			ProcedureDebug.executeProcedure($_dependencies);
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Entity entity = event.player;
			World world = entity.world;
			int i = (int) entity.posX;
			int j = (int) entity.posY;
			int k = (int) entity.posZ;
			java.util.HashMap<String, Object> dependencies = new java.util.HashMap<>();
			dependencies.put("x", i);
			dependencies.put("y", j);
			dependencies.put("z", k);
			dependencies.put("world", world);
			dependencies.put("entity", entity);
			dependencies.put("event", event);
			this.executeProcedure(dependencies);
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
