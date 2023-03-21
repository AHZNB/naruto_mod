package net.narutomod;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import net.narutomod.entity.EntityNinjaMob;
import net.narutomod.item.ItemIryoJutsu;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemEightGates;
import net.narutomod.item.ItemJutsu;

import java.util.UUID;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class PlayerTracker extends ElementsNarutomodMod.ModElement {
	private static final String BATTLEXP = NarutomodModVariables.BATTLEXP;
	private static final String KEEPXP_RULE = "keepNinjaXp";
	public static final String FORCE_DOJUTSU_DROP_RULE = "forceDojutsuDropOnDeath";
	private static final String FORCE_SEND = "forceSendBattleXP2self";
	private static final String UPDATE_HEALTH = "forceUpdateHealth";

	public PlayerTracker(ElementsNarutomodMod instance) {
		super(instance, 181);
	}

	public static boolean isNinja(EntityPlayer player) {
		return player.getEntityData().getDouble(BATTLEXP) > 0.0d;
	}

	public static double getBattleXp(EntityPlayer player) {
		return player.getEntityData().getDouble(BATTLEXP);
	}

	public static double getNinjaLevel(EntityPlayer player) {
		return MathHelper.sqrt(getBattleXp(player));
	}

	public static void addBattleXp(EntityPlayerMP entity, double xp) {
		addBattleXp(entity, xp, true);
	}

	private static void addBattleXp(EntityPlayer entity, double xp, boolean sendMessage) {
		entity.getEntityData().setDouble(BATTLEXP, Math.min(getBattleXp(entity) + xp, 100000.0d));
		if (entity instanceof EntityPlayerMP) {
			sendBattleXPToSelf((EntityPlayerMP)entity);
			if (sendMessage) {
				entity.sendStatusMessage(new TextComponentString(
				 net.minecraft.util.text.translation.I18n.translateToLocal("chattext.ninjaexperience")+
				 String.format("%.1f", getBattleXp(entity))), true);
			}
		}
	}

	private static void logBattleExp(EntityPlayer entity, double xp) {
		if (entity instanceof EntityPlayerMP 
		 && ProcedureUtils.advancementAchieved((EntityPlayerMP)entity, "narutomod:ninjaachievement")) {
			addBattleXp((EntityPlayerMP)entity, xp);
			ItemEightGates.logBattleXP(entity);
			ItemJutsu.logBattleXP(entity);
			//EntityTracker.getOrCreate(entity).lastLoggedXpTime = entity.ticksExisted;
			entity.getEntityData().setInteger("lastLoggedXpTime", entity.ticksExisted);
		}
	}

	private static void sendBattleXPToSelf(EntityPlayerMP player) {
		ProcedureSync.EntityNBTTag.sendToSelf(player, BATTLEXP, getBattleXp(player));
	}

	public static class Deaths {
		private static final List<Deaths> deadPlayers = Lists.newArrayList();
		private final UUID playerId;
		private final double x;
		private final double y;
		private final double z;
		private final long time;
		private final Team team;
		private final double lastXp;
		
		private Deaths(EntityPlayer player) {
			this.playerId = player.getUniqueID();
			this.x = player.posX;
			this.y = player.posY;
			this.z = player.posZ;
			this.time = player.world.getTotalWorldTime();
			this.team = player.getTeam();
			this.lastXp = getBattleXp(player);
		}

		public static void log(EntityPlayer entity) {
			Iterator<Deaths> iter = deadPlayers.iterator();
			while (iter.hasNext()) {
				Deaths death = iter.next();
				if (death.playerId.equals(entity.getUniqueID())) {
					iter.remove();
				}
			}
			deadPlayers.add(new Deaths(entity));
			if (!entity.world.getGameRules().getBoolean(KEEPXP_RULE)) {
				entity.getEntityData().setDouble(BATTLEXP, 0.0D);
				if (entity instanceof EntityPlayerMP) {
					sendBattleXPToSelf((EntityPlayerMP)entity);
				}
			}
		}
	
		public static void clear() {
			deadPlayers.clear();
		}
	
		public static Deaths mostRecent() {
			if (!deadPlayers.isEmpty())
				return deadPlayers.get(deadPlayers.size());
			return null;
		}
	
		public static boolean hasRecentNearby(EntityPlayer player, double distance, double timeframe) {
			return hasRecentNearby(player, distance, timeframe, true);
		}

		public static boolean hasRecentNearby(EntityPlayer player, double distance, double timeframe, boolean checkTeam) {
			if (deadPlayers.isEmpty())
				return false;
			for (int i = deadPlayers.size(); --i >= 0;) {
				Deaths deadguy = deadPlayers.get(i);
				if (!deadguy.playerId.equals(player.getUniqueID())) {
					double d0 = deadguy.x - player.posX;
					double d1 = deadguy.y - player.posY;
					double d2 = deadguy.z - player.posZ;
					double d3 = d0 * d0 + d1 * d1 + d2 * d2;
					if (d3 < distance * distance && player.world.getTotalWorldTime() - deadguy.time <= timeframe
					 && (!checkTeam || player.isOnScoreboardTeam(deadguy.team))) {
						return true;
					}
				}
			}
			return false;
		}
	
		public static boolean hasRecentMatching(EntityPlayer player, double timeframe) {
			if (!deadPlayers.isEmpty()) {
				for (int i = deadPlayers.size(); --i >= 0;) {
					Deaths deadguy = deadPlayers.get(i);
					if (deadguy.playerId.equals(player.getUniqueID()))
						return true;
				}
			}
			return false;
		}
	
		public static long mostRecentTime(EntityPlayer player) {
			if (!deadPlayers.isEmpty()) {
				for (int i = deadPlayers.size(); --i >= 0;) {
					Deaths deadguy = deadPlayers.get(i);
					if (deadguy.playerId.equals(player.getUniqueID()))
						return deadguy.time;
				}
			}
			return 0L;
		}

		public static double getXpBeforeDeath(EntityPlayer player) {
			if (!deadPlayers.isEmpty()) {
				for (int i = deadPlayers.size(); --i >= 0;) {
					Deaths deadguy = deadPlayers.get(i);
					if (deadguy.playerId.equals(player.getUniqueID()))
						return deadguy.lastXp;
				}
			}
			return 0.0d;
		}
	}

	public class PlayerHook {
		private final Map<Integer, Map<String, Object>> persistentDataMap = Maps.newHashMap();
		private final UUID hp_uuid = UUID.fromString("84d6711b-c26d-4dfa-b0c5-1ff54395f4de");
		
		@SubscribeEvent
		public void onTick(TickEvent.PlayerTickEvent event) {
			if (event.phase == TickEvent.Phase.END && event.player instanceof EntityPlayerMP) {
				double d = getBattleXp(event.player) * 0.005d;
				if (d > 0d) {
					IAttributeInstance maxHealthAttr = event.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
					AttributeModifier attr = maxHealthAttr.getModifier(hp_uuid);
					if (attr == null || (int)attr.getAmount() / 2 != (int)d / 2) {
						if (attr != null) {
							maxHealthAttr.removeModifier(hp_uuid);
						}
						maxHealthAttr.applyModifier(new AttributeModifier(hp_uuid, "ninja.maxhealth", d, 0));
						event.player.setHealth(event.player.getHealth() + 0.1f);
					}
				}
				if (event.player.getEntityData().getBoolean(FORCE_SEND)) {
					event.player.getEntityData().removeTag(FORCE_SEND);
					sendBattleXPToSelf((EntityPlayerMP)event.player);
				}
				if (event.player.getEntityData().getBoolean(UPDATE_HEALTH)) {
					event.player.getEntityData().removeTag(UPDATE_HEALTH);
					event.player.setHealth(event.player.getHealth());
				}
			}
		}

		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void onDeath(LivingDeathEvent event) {
			Entity entity = event.getEntityLiving();
			if (entity instanceof EntityPlayerMP) {
				Deaths.log((EntityPlayer) entity);
			}
		}

		private boolean isOffCooldown(Entity entity) {
			//int i = entity.ticksExisted - EntityTracker.getOrCreate(entity).lastLoggedXpTime;
			int i = entity.ticksExisted - entity.getEntityData().getInteger("lastLoggedXpTime");
			return i < 0 || i > 20;
		}

		@SubscribeEvent(priority = EventPriority.LOW)
		public void onDamaged(LivingDamageEvent event) {
			Entity targetEntity = event.getEntity();
			Entity sourceEntity = event.getSource().getTrueSource();
			float amount = event.getAmount();
			if (!targetEntity.equals(sourceEntity) && amount > 0f) {
				if (this.isOffCooldown(targetEntity) && targetEntity instanceof EntityPlayer && amount < ((EntityPlayer)targetEntity).getHealth()) {
					double bxp = getBattleXp((EntityPlayer)targetEntity);
					logBattleExp((EntityPlayer)targetEntity, bxp < 1d ? 1d : (amount / MathHelper.sqrt(MathHelper.sqrt(bxp))));
				}
				if (sourceEntity instanceof EntityPlayer) {
					double xp = 0.0d;
					if (targetEntity instanceof EntityLivingBase && this.isOffCooldown(sourceEntity)) {
						EntityLivingBase target = (EntityLivingBase)targetEntity;
						int resistance = target.isPotionActive(MobEffects.RESISTANCE) 
						 ? target.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() : 1;
						double x = MathHelper.sqrt(target.getMaxHealth() * ProcedureUtils.getModifiedAttackDamage(target)
						 * MathHelper.sqrt(ProcedureUtils.getArmorValue(target)+1d)) * resistance;
						xp = Math.min(x * Math.min(amount / target.getMaxHealth(), 1f) * 0.5d, 50d);
//System.out.println(">>> target:"+target.getName()+", x="+x+", amount="+amount+", maxhp="+target.getMaxHealth()+", xp="+xp);
					}
					if (xp > 0d) {
						logBattleExp((EntityPlayer)sourceEntity, xp);
					}
				}
			}
		}

		@SubscribeEvent
		public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.player.world.isRemote) {
				event.player.setAlwaysRenderNameTag(true);
				//sendBattleXPToSelf((EntityPlayerMP)event.player);
				event.player.getEntityData().setBoolean(FORCE_SEND, true);
				event.player.getEntityData().setBoolean(UPDATE_HEALTH, true);
			}
		}

		@SubscribeEvent
		public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.player instanceof EntityPlayerMP) {
				//sendBattleXPToSelf((EntityPlayerMP)event.player);
				event.player.getEntityData().setBoolean(FORCE_SEND, true);
			}
		}

		@SubscribeEvent
		public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
			Integer i = Integer.valueOf(event.player.getEntityId());
			if (this.persistentDataMap.containsKey(i)) {
				Map<String, Object> map = this.persistentDataMap.get(i);
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					if (entry.getValue() instanceof Boolean) {
						event.player.getEntityData().setBoolean(entry.getKey(), ((Boolean)entry.getValue()).booleanValue());
					} else if (entry.getValue() instanceof Integer) {
						event.player.getEntityData().setInteger(entry.getKey(), ((Integer)entry.getValue()).intValue());
					} else if (entry.getValue() instanceof Float) {
						event.player.getEntityData().setFloat(entry.getKey(), ((Float)entry.getValue()).floatValue());
					} else if (entry.getValue() instanceof Double) {
						event.player.getEntityData().setDouble(entry.getKey(), ((Double)entry.getValue()).doubleValue());
					}
				}
				this.persistentDataMap.remove(i);
			}
		}

		@SubscribeEvent
		public void onClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
			EntityPlayer oldPlayer = event.getOriginal();
			Map<String, Object> map = Maps.newHashMap();
			map.put(BATTLEXP, Double.valueOf(getBattleXp(oldPlayer)));
			map.put(FORCE_SEND, Boolean.valueOf(true));
			map.put("MedicalNinjaChecked", Boolean.valueOf(oldPlayer.getEntityData().getBoolean("MedicalNinjaChecked")));
			if (event.isWasDeath()) {
				map.put("ForceExtinguish", Integer.valueOf(5));
			} else {
				map.put(NarutomodModVariables.FirstGotNinjutsu,
				 Boolean.valueOf(oldPlayer.getEntityData().getBoolean(NarutomodModVariables.FirstGotNinjutsu)));
			}
			this.persistentDataMap.put(Integer.valueOf(oldPlayer.getEntityId()), map);
		}

		@SubscribeEvent
		public void onWorldLoad(WorldEvent.Load event) {
			World world = event.getWorld();
			if (!world.isRemote && !world.getGameRules().hasRule(KEEPXP_RULE)) {
				world.getGameRules().addGameRule(KEEPXP_RULE, "false", net.minecraft.world.GameRules.ValueType.BOOLEAN_VALUE);
			}
			if (!world.isRemote && !world.getGameRules().hasRule(FORCE_DOJUTSU_DROP_RULE)) {
				world.getGameRules().addGameRule(FORCE_DOJUTSU_DROP_RULE, "false", net.minecraft.world.GameRules.ValueType.BOOLEAN_VALUE);
			}
		}
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerHook());
	}
}
