package net.narutomod.procedure;

import net.narutomod.item.ItemYoton;
import net.narutomod.item.ItemYooton;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemSuiton;
import net.narutomod.item.ItemShikotsumyaku;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemShakuton;
import net.narutomod.item.ItemSenjutsu;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemRanton;
import net.narutomod.item.ItemRaiton;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.item.ItemMokuton;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemKaton;
import net.narutomod.item.ItemJiton;
import net.narutomod.item.ItemJinton;
import net.narutomod.item.ItemIryoJutsu;
import net.narutomod.item.ItemInton;
import net.narutomod.item.ItemHyoton;
import net.narutomod.item.ItemGourd;
import net.narutomod.item.ItemFutton;
import net.narutomod.item.ItemFuton;
import net.narutomod.item.ItemEightGates;
import net.narutomod.item.ItemDoton;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemBakuton;
import net.narutomod.item.ItemAsuraPathArmor;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnPlayerDeath extends ElementsNarutomodMod.ModElement {
	public ProcedureOnPlayerDeath(ElementsNarutomodMod instance) {
		super(instance, 729);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure OnPlayerDeath!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack stack = ItemStack.EMPTY;
		boolean keepInventory = false;
		if ((entity instanceof EntityPlayerMP)) {
			keepInventory = (boolean) entity.world.getGameRules().getBoolean("keepInventory");
			if (((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemRinnegan.helmet, (int) (1)))
					: false)) {
				stack = ProcedureUtils.getItemStackIgnoreDurability(((EntityPlayer) entity).inventory, new ItemStack(ItemRinnegan.helmet));
				if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId("KoH_id")) {
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).setHealth((float) 2);
					if (dependencies.get("event") != null) {
						Object _obj = dependencies.get("event");
						if (_obj instanceof net.minecraftforge.fml.common.eventhandler.Event) {
							net.minecraftforge.fml.common.eventhandler.Event _evt = (net.minecraftforge.fml.common.eventhandler.Event) _obj;
							if (_evt.isCancelable())
								_evt.setCanceled(true);
						}
					}
				} else if ((!(keepInventory))) {
					if (entity instanceof EntityPlayer)
						((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemRinnegan.helmet, (int) (1)).getItem(), -1, (int) (-1),
								null);
				}
			}
			if (((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemTenseigan.helmet, (int) (1)))
					: false)) {
				stack = ProcedureUtils.getItemStackIgnoreDurability(((EntityPlayer) entity).inventory, new ItemStack(ItemTenseigan.helmet));
				if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId("KoH_id")) {
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).setHealth((float) 2);
					if (dependencies.get("event") != null) {
						Object _obj = dependencies.get("event");
						if (_obj instanceof net.minecraftforge.fml.common.eventhandler.Event) {
							net.minecraftforge.fml.common.eventhandler.Event _evt = (net.minecraftforge.fml.common.eventhandler.Event) _obj;
							if (_evt.isCancelable())
								_evt.setCanceled(true);
						}
					}
				} else if ((!(keepInventory))) {
					if (entity instanceof EntityPlayer)
						((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem(), -1, (int) (-1),
								null);
				}
			}
			if ((((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)))
					: false) && (!(keepInventory)))) {
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)).getItem(), -1,
							(int) 1, null);
			}
			if (((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemByakugan.helmet, (int) (1)))
					: false)) {
				stack = ProcedureUtils.getItemStackIgnoreDurability(((EntityPlayer) entity).inventory, new ItemStack(ItemByakugan.helmet));
				{
					ItemStack _stack = (stack);
					if (!_stack.hasTagCompound())
						_stack.setTagCompound(new NBTTagCompound());
					_stack.getTagCompound().setBoolean((NarutomodModVariables.RINNESHARINGAN_ACTIVATED), (false));
				}
			}
			if (entity instanceof EntityPlayer)
				((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemAsuraPathArmor.body, (int) (1)).getItem(), -1, (int) (-1),
						null);
			if ((!(keepInventory))) {
				if (EntityBijuManager.isJinchuriki((EntityPlayer) entity)) {
					EntityBijuManager.unsetPlayerAsJinchuriki((EntityPlayer) entity);
				}
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemMokuton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemEightGates.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemRaiton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemFuton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemNinjutsu.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemDoton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemYoton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemInton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemKaton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemJinton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemSuiton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemBakuton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemHyoton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemJiton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemShakuton.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemYooton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemRanton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemFutton.block, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemShikotsumyaku.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemGourd.body, (int) (1)).getItem(), -1, (int) (-1), null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemIryoJutsu.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
				if (entity instanceof EntityPlayer)
					((EntityPlayer) entity).inventory.clearMatchingItems(new ItemStack(ItemSenjutsu.block, (int) (1)).getItem(), -1, (int) (-1),
							null);
			} else {
				if ((EntityBijuManager.cloakLevel((EntityPlayer) entity) > 0)) {
					EntityBijuManager.toggleBijuCloak((EntityPlayer) entity);
				}
				if (entity.world.getGameRules().getBoolean(PlayerTracker.FORCE_DOJUTSU_DROP_RULE)) {
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemByakugan.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						((stack)).shrink((int) 1);
					}
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemSharingan.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						((stack)).shrink((int) 1);
					}
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemMangekyoSharingan.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						((stack)).shrink((int) 1);
					}
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer) entity, ItemMangekyoSharinganObito.helmet);
					if (stack != null) {
						((EntityPlayer) entity).dropItem(stack.copy(), true, true);
						((stack)).shrink((int) 1);
					}
				}
			}
			ProcedureSync.EntityNBTTag.removeAndSync(entity, NarutomodModVariables.forceBowPose);
			entity.getEntityData().setBoolean((NarutomodModVariables.FirstGotNinjutsu), (false));
			entity.getEntityData().setBoolean("susanoo_activated", (false));
			entity.getEntityData().setInteger("ForceExtinguish", 5);
			entity.setNoGravity(false);
		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		if (event != null && event.getEntity() != null) {
			Entity entity = event.getEntity();
			int i = (int) entity.posX;
			int j = (int) entity.posY;
			int k = (int) entity.posZ;
			World world = entity.world;
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
