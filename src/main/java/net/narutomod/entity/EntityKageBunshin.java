
package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Chakra;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityKageBunshin extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 139;
	public static final int ENTITYID_RANGED = 140;
	public static final String OGCLONE_KEY = "I_am_clone_ogCloneIDKey";
	public static final PlayerEventHook playerEventHook = new PlayerEventHook();
	private static final Map<Integer, EC> UNLOADED_EC = Maps.newHashMap();

	public EntityKageBunshin(ElementsNarutomodMod instance) {
		super(instance, 388);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		  .id(new ResourceLocation("narutomod", "kage_bunshin"), ENTITYID).name("kage_bunshin").tracker(64, 3, true).build());
	}

	public static boolean isPlayerClone(EntityPlayer player) {
		return getOriginalClone(player) != null;
	}

	@Nullable
	private static EC getCloneByID(World world, int id) {
		Entity entity = world.getEntityByID(id);
		if (entity == null) {
			entity = UNLOADED_EC.get(id);
		}
		if (!(entity instanceof EC)) {
			UNLOADED_EC.remove(id);
			return null;
		}
		return (EC)entity;
	}

	@Nullable
	public static EC getOriginalClone(EntityLivingBase player) {
		if (player.getEntityData().hasKey(OGCLONE_KEY)) {
			EC entity = getCloneByID(player.world, player.getEntityData().getInteger(OGCLONE_KEY));
			if (entity != null && entity.isEntityAlive()) {
				return entity;
			}
			player.getEntityData().removeTag(OGCLONE_KEY);
		}
		return null;
	}

	public static class EC extends EntityClone.Base {
		//private final NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
		//private InventoryPlayer summonerInventory;
		private boolean isOriginal;
		private DamageSource deathCause;

		public EC(World world) {
			super(world);
			this.stepHeight = 16f;
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}

		public EC(EntityLivingBase user) {
			super(user);
			this.stepHeight = 16f;
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("bunshin.followRange", 32, 0));
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ProcedureUtils.getModifiedSpeed(user) * 4.0d);
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR)
			 .setBaseValue(user.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue());
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}

		@Nullable
		private EC getOriginal() {
			EntityLivingBase summoner = this.getSummoner();
			if (summoner != null) {
				return getOriginalClone(summoner);
			}
			return null;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(2, new EntityClone.AIFollowSummoner(this, 0.8d, 4.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !EC.this.isOriginal;
				}
			});
		}

		@Override
		public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			if (entity.equals(this.getSummoner())) {
				if (this.isOriginal) {
					this.cancelCloneControl();
					if (!this.world.isRemote) 
						entity.sendStatusMessage(new TextComponentString("You are now the original"), false);
					return true;
				}
				//EC original = this.getOriginal();
				if (this.getOriginal() != null) {
					//original.cancelCloneControl();
					return false;
				}
				this.controlClone();
				/*this.summonerInventory = new InventoryPlayer(entity);
				this.summonerInventory.copyInventory(entity.inventory);
				for (int i = 0; i < 36; i++) {
					if (i != entity.inventory.currentItem)
						entity.inventory.removeStackFromSlot(i);
				}*/
				if (!this.world.isRemote) 
					entity.sendStatusMessage(new TextComponentString("You are now clone("+this.getEntityId()+")"), false);
				return true;
			}
			return super.processInteract(entity, hand);
		}

		private void poof(Entity entity) {
			ProcedureUtils.poofWithSmoke(entity);
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				boolean flag = false;
				boolean flag2 = false;
				if (this.isOriginal) {
					this.poof(summoner);
					summoner.rotationYaw = this.rotationYaw;
					summoner.setPositionAndUpdate(this.posX, this.posY, this.posZ);
					if (summoner.isEntityAlive()) {
						this.cancelCloneControl();
						summoner.attackEntityFrom(this.deathCause != null ? this.deathCause : DamageSource.GENERIC, Float.MAX_VALUE);
					} else {
						flag = true;
						flag2 = true;
					}
				} else {
					this.poof(this);
					flag = true;
				}
				if (flag && summoner != null) {
					Jutsu.updateClones(summoner, false);
					Chakra.pathway(summoner).consume(-Chakra.pathway(this).getAmount() * 0.9d, false);
					if (summoner.getHealth() > 0.0f || flag2) {
						summoner.setHealth(summoner.getHealth() + this.getHealth() * 0.9f);
					}
				}
			}
		}

		private void controlClone() {
			this.isOriginal = true;
			this.shouldDefendSummoner = false;
			this.getSummoner().getEntityData().setInteger(OGCLONE_KEY, this.getEntityId());
		}

		private void cancelCloneControl() {
			this.getSummoner().getEntityData().removeTag(OGCLONE_KEY);
			this.isOriginal = false;
			this.shouldDefendSummoner = true;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getImmediateSource() instanceof EntityLivingBase && source.getImmediateSource().equals(this.getSummoner())) {
				if (this.isOriginal) {
					this.cancelCloneControl();
				}
				this.setDead();
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
	    protected void onDeathUpdate() {
	    	if (++this.deathTime == 20) {
	            this.setDead();
	    	}
	    }

	    @Override
	    public void onDeath(DamageSource cause) {
	    	super.onDeath(cause);
	    	this.deathCause = cause;
	    }

		@Override
		public void onAddedToWorld() {
			super.onAddedToWorld();
			if (!this.world.isRemote) {
				UNLOADED_EC.remove(this.getEntityId());
			}
		}
		
		@Override
	    public void onRemovedFromWorld() {
	    	super.onRemovedFromWorld();
	    	if (!this.world.isRemote) {
	    		int i = this.getEntityId();
	    		if (!this.isDead) {
	    			if (!UNLOADED_EC.containsKey(i)) {
	    				UNLOADED_EC.put(i, this);
	    			}
	    		} else {
	    			UNLOADED_EC.remove(i);
	    		}
	    	}
	    }

		@Override
		protected float getWaterSlowDown() {
			return 1.0f;
		}

		@Override
		protected void updateArmSwingProgress() {
			super.updateArmSwingProgress();
			for (ItemStack stack : this.getHeldEquipment()) {
				if (!stack.isEmpty()) {
					stack.updateAnimation(this.world, this, 0, false);
				}
			}
		}

	    @Override
	    public void onUpdate() {
	    	super.onUpdate();
	    	if (!this.world.isRemote && this.ticksExisted == 15) {
		    	ItemStack stack = this.getHeldItemMainhand();
		    	if (stack.getItem() instanceof ItemJutsu.Base) {
		    		for (ItemJutsu.JutsuEnum je : ((ItemJutsu.Base)stack.getItem()).getActivatedJutsus(stack)) {
		    			je.jutsu.createJutsu(stack, this, je.jutsu.getPower(stack));
		    		}
		    	}
		    	stack = this.getHeldItemOffhand();
		    	if (stack.getItem() instanceof ItemJutsu.Base) {
		    		for (ItemJutsu.JutsuEnum je : ((ItemJutsu.Base)stack.getItem()).getActivatedJutsus(stack)) {
		    			je.jutsu.createJutsu(stack, this, je.jutsu.getPower(stack));
		    		}
		    	}
	    	}
	    }

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "KageBunshinEntityId";
			private static final UUID MAXHEALTH = UUID.fromString("308fe1ce-1850-4b1a-803c-ed265df4e3ce");

			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (entity instanceof EntityPlayer && entity.isSneaking()) {
					if (isPlayerClone((EntityPlayer)entity)) {
						entity.getEntityData().setFloat("HealthB4Kill", entity.getHealth());
						entity.onKillCommand();
					} else {
						removeAllClones(entity);
					}
					return false;
				}
				if (!(entity instanceof EntityPlayer) || Chakra.pathway((EntityPlayer)entity).getAmount() >= 200d) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
					  .getObject(new ResourceLocation("narutomod:kagebunshin")), SoundCategory.NEUTRAL, 1.0F, 1.0F);
					updateClones(entity, true);
					return true;
				}
				return false;
			}

			private static int updateClones(EntityLivingBase entity, boolean add1) {
				List<Integer> clones = Lists.newArrayList();
				int[] ids = entity.getEntityData().getIntArray(ID_KEY);
				for (int i = 0; i < ids.length; i++) {
					EC ec = getCloneByID(entity.world, ids[i]);
					if (ec != null && ec.isEntityAlive())
						clones.add(ids[i]);
				}
				if (add1) {
					EC newClone = new EC(entity);
					newClone.setPosition(newClone.posX + (entity.getRNG().nextBoolean() ? -0.1d : 0.1d), newClone.posY, newClone.posZ + (entity.getRNG().nextBoolean() ? -0.1d : 0.1d));
					entity.world.spawnEntity(newClone);
					clones.add(newClone.getEntityId());
					Chakra.Pathway chakra = Chakra.pathway(entity);
					double d = chakra.getAmount() / (clones.size()+1);
					chakra.consume(d);
					Chakra.pathway(newClone).setMax(d).consume(-d);
				}
				entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(MAXHEALTH);
				if (clones.size() > 0) {
					entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
					// .applyModifier(new AttributeModifier(MAXHEALTH, "maxhealth.modifier", 1d / (clones.size()+1) - 1d, 2));
					 .applyModifier(new AttributeModifier(MAXHEALTH, "maxhealth.modifier",
					 entity.getHealth() * (clones.size() + (add1 ? 0 : 2)) / (clones.size()+1) - entity.getMaxHealth(), 0));
				}
				if (entity.getHealth() > entity.getMaxHealth()) {
					entity.setHealth(entity.getMaxHealth());
				}
				if (add1) {
					for (Integer i : clones) {
						EC e = getCloneByID(entity.world, i.intValue());
						e.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(entity.getHealth());
						if (e.ticksExisted < 2 || e.getHealth() > e.getMaxHealth()) {
							e.setHealth(e.getMaxHealth());
						}
					}
				}
				if (clones.isEmpty()) {
					entity.getEntityData().removeTag(ID_KEY);
				} else {
					entity.getEntityData().setIntArray(ID_KEY, Ints.toArray(clones));
				}
				return clones.size();
			}

			private static boolean hasClones(EntityLivingBase entity) {
				return entity.getEntityData().hasKey(ID_KEY);
			}

			private static void removeAllClones(EntityLivingBase entity) {
				List<EC> clones = Lists.newArrayList();
				int[] ids = entity.getEntityData().getIntArray(ID_KEY);
				for (int i = 0; i < ids.length; i++) {
					EC e = getCloneByID(entity.world, ids[i]);
					if (e != null && e.isEntityAlive()) {
						clones.add((EC)e);
					}
				}
				for (EC e : clones) {
					e.setDead();
				}
				entity.getEntityData().removeTag(ID_KEY);
			}
		}
	}

	public static class PlayerEventHook {
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void onDeath(LivingDeathEvent event) {
			EntityLivingBase entity = event.getEntityLiving();
			if (entity instanceof EntityPlayer) {
				if (isPlayerClone((EntityPlayer)entity)) {
					event.setCanceled(true);
					this.revertClone(entity);
				} else if (EC.Jutsu.hasClones(entity)) {
					EC.Jutsu.removeAllClones(entity);
				}
			}
		}

		@SubscribeEvent
		public void onPlayerChangeDimension(EntityTravelToDimensionEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer) {
				if (isPlayerClone((EntityPlayer)entity)) {
					event.setCanceled(true);
					((EntityPlayer)entity).sendStatusMessage(new TextComponentString("You are a clone, you can't travel to another dimension."), false);
				} else if (EC.Jutsu.hasClones((EntityPlayer)entity)) {
					EC.Jutsu.removeAllClones((EntityPlayer)entity);
				}
			}
		}

		@SubscribeEvent
		public void onPlayerSleep(PlayerSleepInBedEvent event) {
			EntityPlayer entity = event.getEntityPlayer();
//System.out.println("isPlayerClone:"+isPlayerClone(entity));
			if (!entity.world.isRemote && isPlayerClone(entity)) {
				event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
				entity.sendStatusMessage(new TextComponentString("You are a clone, you can't sleep."), false);
			}			
		}

		private void revertClone(EntityLivingBase entity) {
			EC clone = getOriginalClone(entity);
			if (clone != null) {
				clone.setDead();
				entity.isDead = false;
				if (entity.getEntityData().hasKey("HealthB4Kill")) {
					entity.setHealth(entity.getHealth() + entity.getEntityData().getFloat("HealthB4Kill"));
					entity.getEntityData().removeTag("HealthB4Kill");
				}
				entity.clearActivePotions();
				entity.getEntityData().setInteger("ForceExtinguish", 3);
			}
			//entity.getEntityData().removeTag(OGCLONE_KEY);
		}

		@SubscribeEvent
		public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
			if (isPlayerClone(event.player)) {
				event.player.isDead = true;
				this.revertClone(event.player);
			}
			if (EC.Jutsu.hasClones(event.player)) {
				EC.Jutsu.removeAllClones(event.player);
			}
		}

		@SubscribeEvent
		public void onServerDisconnect(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
			EntityPlayer player = ((net.minecraft.network.NetHandlerPlayServer)event.getHandler()).player;
			if (isPlayerClone(player)) {
				player.isDead = true;
				this.revertClone(player);
			}
			if (EC.Jutsu.hasClones(player)) {
				EC.Jutsu.removeAllClones(player);
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(playerEventHook);
	}
}
