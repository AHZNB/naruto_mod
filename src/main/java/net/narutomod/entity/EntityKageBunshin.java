
package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Chakra;

import java.util.List;
import java.util.UUID;

@ElementsNarutomodMod.ModElement.Tag
public class EntityKageBunshin extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 139;
	public static final int ENTITYID_RANGED = 140;
	public static final String OGCLONE_KEY = "I_am_clone_ogCloneIDKey";
	public static final PlayerEventHook playerEventHook = new PlayerEventHook();

	public EntityKageBunshin(ElementsNarutomodMod instance) {
		super(instance, 388);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		  .id(new ResourceLocation("narutomod", "kage_bunshin"), ENTITYID).name("kage_bunshin").tracker(64, 3, true).build());
	}

	public static boolean isPlayerClone(EntityPlayer player) {
		return player.getEntityData().getInteger(OGCLONE_KEY) > 0;
	}

	public static class EC extends EntityClone.Base {
		//private final NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
		//private InventoryPlayer summonerInventory;
		private boolean isOriginal;
		private double chakra;

		public EC(World world) {
			super(world);
		}

		public EC(EntityLivingBase user) {
			super(user);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("bunshin.followRange", 32, 0));
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ProcedureUtils.getModifiedSpeed(user) * 3.5d);
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR)
			 .setBaseValue(user.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue());
		}

		private EC getOriginal() {
			EntityLivingBase summoner = this.getSummoner();
			if (summoner != null) {
				Entity entity = this.world.getEntityByID(summoner.getEntityData().getInteger(OGCLONE_KEY));
				if (entity instanceof EC)
					return (EC)entity;
			}
			return null;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(2, new EntityClone.AIFollowSummoner(this, 0.6d, 3.0F) {
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
			//Random rand = new Random();
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) SoundEvent.REGISTRY
			  .getObject(new ResourceLocation("narutomod:poof")), SoundCategory.NEUTRAL, 1.0F, 1.0F);
			((WorldServer)entity.world).spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, entity.posX, entity.posY+entity.height/2, 
			  entity.posZ, 200, entity.width * 0.5d, entity.height * 0.3d, entity.width * 0.5d, 0.02d);
			/*} else {
		        for (int k = 0; k < 200; ++k) {
		       		double d2 = rand.nextGaussian() * 0.02D;
				    double d0 = rand.nextGaussian() * 0.02D;
				    double d1 = rand.nextGaussian() * 0.02D;
				    entity.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, 
				      entity.posX + rand.nextGaussian() * 0.5d * entity.width, entity.posY + rand.nextDouble() * entity.height, 
				      entity.posZ + rand.nextGaussian() * 0.5d * entity.width, d2, d0, d1);
				}
			}*/
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				boolean flag = false;
				if (this.isOriginal) {
					this.poof(summoner);
					summoner.rotationYaw = this.rotationYaw;
					summoner.setPositionAndUpdate(this.posX, this.posY, this.posZ);
					if (summoner.isEntityAlive()) {
						summoner.setHealth(0);
					} else {
						flag = true;
					}
				} else {
					this.poof(this);
					flag = true;
				}
				if (flag && summoner != null) {
					Jutsu.updateClones(summoner, false);
					//Chakra.pathway(summoner).consume(-this.chakra * (double)(this.getHealth() / this.getMaxHealth()), true);
					Chakra.pathway(summoner).consume(-this.chakra * 0.8d, false);
				}
			}
		}

		private void controlClone() {
			this.isOriginal = true;
			this.shouldDefendSummoner = false;
			this.getSummoner().getEntityData().setInteger(OGCLONE_KEY, this.getEntityId());
		}

		private void cancelCloneControl() {
			this.getSummoner().getEntityData().setInteger(OGCLONE_KEY, 0);
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
	    public void onUpdate() {
	    	super.onUpdate();
	    	ItemStack stack = this.getHeldItemMainhand();
	    	if (!this.world.isRemote && this.ticksExisted == 1 && stack.getItem() instanceof ItemJutsu.Base) {
	    		for (ItemJutsu.JutsuEnum je : ((ItemJutsu.Base)stack.getItem()).getActivatedJutsus(stack)) {
	    			je.jutsu.createJutsu(stack, this, je.jutsu.getPower(stack));
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
						entity.onKillCommand();
					} else {
						this.removeAllClones(entity);
					}
					return false;
				}
				if (!(entity instanceof EntityPlayer) || Chakra.pathway((EntityPlayer)entity).getAmount() >= 200d) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) SoundEvent.REGISTRY
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
					Entity e = entity.world.getEntityByID(ids[i]);
					if (e instanceof EC && e.isEntityAlive())
						clones.add(ids[i]);
				}
				Chakra.Pathway chakra = entity instanceof EntityPlayer ? Chakra.pathway((EntityPlayer)entity) : null;
				if (add1) {
					Entity newClone = new EC(entity);
					entity.world.spawnEntity(newClone);
					clones.add(newClone.getEntityId());
					if (chakra != null) {
						chakra.consume(chakra.getAmount() / (clones.size()+1));
					}
				}
				entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(MAXHEALTH);
				entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				  .applyModifier(new AttributeModifier(MAXHEALTH, "maxhealth.modifier", 1d / (clones.size()+1) - 1d, 2));
				if (entity.getHealth() > entity.getMaxHealth()) {
					entity.setHealth(entity.getMaxHealth());
				}
				if (add1) {
					for (Integer i : clones) {
						EC e = (EC)entity.world.getEntityByID(i.intValue());
						e.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(entity.getHealth());
						e.setHealth(e.getMaxHealth());
						if (chakra != null)
							e.chakra = chakra.getAmount();
					}
				}
				entity.getEntityData().setIntArray(ID_KEY, Ints.toArray(clones));
				return clones.size();
			}

			private void removeAllClones(EntityLivingBase entity) {
				List<EC> clones = Lists.newArrayList();
				int[] ids = entity.getEntityData().getIntArray(ID_KEY);
				for (int i = 0; i < ids.length; i++) {
					Entity e = entity.world.getEntityByID(ids[i]);
					if (e instanceof EC && e.isEntityAlive()) {
						clones.add((EC)e);
					}
				}
				for (EC e : clones) {
					e.setDead();
				}
			}
		}
	}

	public static class PlayerEventHook {
		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public void onDeath(LivingDeathEvent event) {
			EntityLivingBase entity = event.getEntityLiving();
			if (entity instanceof EntityPlayer && isPlayerClone((EntityPlayer)entity)) {
				Entity clone = entity.world.getEntityByID(entity.getEntityData().getInteger(OGCLONE_KEY));
				if (clone instanceof EC) {
					clone.setDead();
					event.setCanceled(true);
					entity.isDead = false;
					entity.setHealth(((EC)clone).getHealth());
					entity.clearActivePotions();
					entity.getEntityData().setInteger("ForceExtinguish", 3);
				}
				entity.getEntityData().setInteger(OGCLONE_KEY, 0);
			}
		}

		@SubscribeEvent
		public void onPlayerChangeDimension(EntityTravelToDimensionEvent event) {
			Entity entity = event.getEntity();
			if (entity instanceof EntityPlayer && isPlayerClone((EntityPlayer)entity)) {
				event.setCanceled(true);
				((EntityPlayer)entity).sendStatusMessage(new TextComponentString("You are a clone, you can't travel to another dimension."), false);
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
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(playerEventHook);
	}
}
