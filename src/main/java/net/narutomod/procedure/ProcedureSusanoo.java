package net.narutomod.procedure;

import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.potion.PotionFeatherFalling;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.entity.EntitySusanooWinged;
import net.narutomod.entity.EntitySusanooSkeleton;
import net.narutomod.entity.EntitySusanooClothed;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSusanoo extends ElementsNarutomodMod.ModElement {
	private static final String SUMMONED_SUSANOO = "summonedSusanooID";
	public static final double BASE_CHAKRA_USAGE = 500d;

	public ProcedureSusanoo(ElementsNarutomodMod instance) {
		super(instance, 168);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerHook());
	}

	public static int getSummonedSusanooId(Entity entity) {
		return entity.getEntityData().getInteger(SUMMONED_SUSANOO);
	}

	public static void execute(EntityPlayer player) {
		World world = player.world;
		boolean flag = (player.isCreative() || ProcedureUtils.hasItemInInventory(player, ItemRinnegan.helmet));
		ItemStack helmet = player.inventory.armorInventory.get(3);
		if (!player.getEntityData().getBoolean("susanoo_activated")) {
			if (helmet.hasTagCompound() && !helmet.getTagCompound().getBoolean("sharingan_blinded")) {
				//if (flag || NarutomodModVariables.world_tick < player.getEntityData().getDouble("susanoo_cd") - 2400.0D
				//		|| NarutomodModVariables.world_tick > player.getEntityData().getDouble("susanoo_cd")) {
					if (PlayerTracker.getBattleXp(player) >= EntitySusanooBase.BXP_REQUIRED_L0
					 && Chakra.pathway(player).consume(BASE_CHAKRA_USAGE)) {
						player.getEntityData().setBoolean("susanoo_activated", true);
						player.getEntityData().setDouble("susanoo_cd", NarutomodModVariables.world_tick + 2400.0D);
						EntitySusanooBase entityCustom = new EntitySusanooSkeleton.EntityCustom(player);
						world.spawnEntity(entityCustom);
						player.getEntityData().setInteger(SUMMONED_SUSANOO, entityCustom.getEntityId());
					}
					/*EntitySusanooBase entityCustom;
					player.getEntityData().setBoolean("susanoo_activated", true);
					player.getEntityData().setDouble("susanoo_cd", NarutomodModVariables.world_tick + 2400.0D);
					if (player.experienceLevel < 31) {
						entityCustom = new EntitySusanooSkeleton.EntityCustom(player);
					} else if (player.experienceLevel < 90) {
						entityCustom = new EntitySusanooClothed.EntityCustom(player, player.experienceLevel >= 60);
					} else {
						entityCustom = new EntitySusanooWinged.EntityCustom(player);
					}
					world.spawnEntity(entityCustom);
					player.getEntityData().setInteger(SUMMONED_SUSANOO, entityCustom.getEntityId());*/
				//} else if (!world.isRemote) {
				//	player.sendStatusMessage(new TextComponentString(net.minecraft.client.resources.I18n.format("chattext.cooldown") + 
				//	  String.format("%.2f", (player.getEntityData().getDouble("susanoo_cd") - NarutomodModVariables.world_tick) / 20.0D)), true);
				//}
			}
		} else {
			//double cooldown = (player.getEntityData().getDouble("susanoo_cd") - NarutomodModVariables.world_tick)
			//				* player.getEntityData().getDouble("susanoo_ticks") / 820.0D;
			double cooldown = player.getEntityData().getDouble("susanoo_ticks") * 0.25d;
			cooldown *= ProcedureUtils.getCooldownModifier(player);
			//player.getEntityData().setDouble("susanoo_cd", NarutomodModVariables.world_tick + cooldown);
			player.getEntityData().removeTag("susanoo_activated");
			player.getEntityData().removeTag("susanoo_ticks");
			Entity entitySpawned = world.getEntityByID(getSummonedSusanooId(player));
			player.getEntityData().removeTag(SUMMONED_SUSANOO);
			if (entitySpawned != null) {
				entitySpawned.setDead();
			}
			if (!flag && helmet.getItem() != ItemMangekyoSharinganEternal.helmet) {
				player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (int)cooldown, 3));
				player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int)cooldown, 2));
			}
			player.addPotionEffect(new PotionEffect(PotionFeatherFalling.potion, 60, 5));
		}
	}
	
	public static void executeProcedure(java.util.Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ProcedureSusanoo!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ProcedureSusanoo!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (!(entity instanceof EntityPlayer)) {
			System.err.println("Unauthorized calling of procedure ProcedureSusanoo! (entity not player)");
			return;
		}
		execute((EntityPlayer)entity);
	}

	public static void upgrade(EntityPlayer player) {
		Entity susanoo = player.getRidingEntity();
		double playerXp = PlayerTracker.getBattleXp(player);
		if (susanoo instanceof EntitySusanooBase) {
			if (susanoo instanceof EntitySusanooSkeleton.EntityCustom) {
				boolean fullBody = ((EntitySusanooSkeleton.EntityCustom)susanoo).isFullBody();
				if (!fullBody && playerXp >= EntitySusanooBase.BXP_REQUIRED_L1) {
					if (Chakra.pathway(player).consume(BASE_CHAKRA_USAGE)) {
						susanoo.setDead();
						EntitySusanooBase entityCustom = new EntitySusanooSkeleton.EntityCustom(player, true);
						player.world.spawnEntity(entityCustom);
						player.getEntityData().setInteger(SUMMONED_SUSANOO, entityCustom.getEntityId());
					}
				} else if (fullBody && playerXp >= EntitySusanooBase.BXP_REQUIRED_L2) {
					if (Chakra.pathway(player).consume(BASE_CHAKRA_USAGE)) {
						susanoo.setDead();
						EntitySusanooBase entityCustom = new EntitySusanooClothed.EntityCustom(player, false);
						player.world.spawnEntity(entityCustom);
						player.getEntityData().setInteger(SUMMONED_SUSANOO, entityCustom.getEntityId());
					}
				}
			} else if (susanoo instanceof EntitySusanooClothed.EntityCustom) {
				boolean hasLegs = ((EntitySusanooClothed.EntityCustom)susanoo).hasLegs();
				if (hasLegs && playerXp >= EntitySusanooBase.BXP_REQUIRED_L4) {
					if (Chakra.pathway(player).consume(BASE_CHAKRA_USAGE)) {
						susanoo.setDead();
						EntitySusanooBase entityCustom = new EntitySusanooWinged.EntityCustom(player);
						player.world.spawnEntity(entityCustom);
						player.getEntityData().setInteger(SUMMONED_SUSANOO, entityCustom.getEntityId());
					}
				} else if (!hasLegs && playerXp >= EntitySusanooBase.BXP_REQUIRED_L3) {
					if (Chakra.pathway(player).consume(BASE_CHAKRA_USAGE)) {
						susanoo.setDead();
						EntitySusanooBase entityCustom = new EntitySusanooClothed.EntityCustom(player, true);
						player.world.spawnEntity(entityCustom);
						player.getEntityData().setInteger(SUMMONED_SUSANOO, entityCustom.getEntityId());
					}
				}
			}
		}
	}

	public class PlayerHook {
		private void checkAndRemove(EntityPlayer entity) {
			if (entity.getEntityData().getBoolean("susanoo_activated")) {
				execute(entity);
			}
		}

		@SubscribeEvent
		public void onPlayerChangeDimension(EntityTravelToDimensionEvent event) {
			if (event.getEntity() instanceof EntityPlayer)
				this.checkAndRemove((EntityPlayer)event.getEntity());
		}

		@SubscribeEvent
		public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
			this.checkAndRemove(event.player);
		}

		@SubscribeEvent
		public void onServerDisconnect(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
			this.checkAndRemove(((NetHandlerPlayServer)event.getHandler()).player);
		}
	}

}
