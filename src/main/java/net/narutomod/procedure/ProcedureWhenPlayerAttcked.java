package net.narutomod.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
//import net.minecraft.client.resources.I18n;

import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntityShieldBase;
import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.entity.EntityTailedBeast;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureWhenPlayerAttcked extends ElementsNarutomodMod.ModElement {
	public ProcedureWhenPlayerAttcked(ElementsNarutomodMod instance) {
		super(instance, 59);
	}

	public static void executeProcedure(HashMap<String, Object> dependencies) {
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		Object evtobj = dependencies.get("event");
		if (entity == null) {
			System.err.println("Failed to load dependency entity for procedure ProcedureWhenPlayerAttcked!");
			return;
		}
		if (world == null) {
			System.err.println("Failed to load dependency world for procedure ProcedureWhenPlayerAttcked!");
			return;
		}
		if (!(evtobj instanceof LivingAttackEvent)) {
			System.err.println("Failed to load dependency event for procedure ProcedureWhenPlayerAttcked!");
			return;
		}
		Entity attacker = null;
		LivingAttackEvent evt = (LivingAttackEvent) evtobj;
		if (evt.getSource() != null) {
			attacker = evt.getSource().getTrueSource();
		}
		if (entity instanceof EntityLivingBase) {
			/*if ((ProcedureUtils.isWearingMangekyo((EntityLivingBase) entity)
			 || ((EntityLivingBase) entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemSharingan.helmet)
			 && attacker instanceof EntityLivingBase && !attacker.world.isRemote) {
			 	if (((EntityLivingBase)entity).getRNG().nextFloat() < 0.5f) {
			 		evt.setCanceled(true);
			 		Vec3d vec = entity.getPositionVector().subtract(attacker.getPositionVector()).normalize()
			 		 .rotateYaw((world.rand.nextFloat()-0.5f)*(float)Math.PI);
			 		entity.addVelocity(vec.x, 0.0d, vec.z);
			 		entity.velocityChanged = true;
			 	}
				((EntityLivingBase) attacker).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 1, false, true));
				((EntityLivingBase) attacker).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 300, 1, false, true));
			}*/
			if (entity.getEntityData().getDouble(NarutomodModVariables.InvulnerableTime) > 0.0D) {
				evt.setCanceled(true);
			}
			if (attacker instanceof EntityLivingBase && attacker.getEntityData().getInteger("FearEffect") > 0) {
				evt.setCanceled(true);
			}
			if (((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ItemRinnegan.body
			 && ItemJutsu.isDamageSourceNinjutsu(evt.getSource())) {
				evt.setCanceled(true);
			}
			if (entity.getRidingEntity() instanceof EntitySusanooBase 
			 || entity.getRidingEntity() instanceof EntityShieldBase
			 || entity.getRidingEntity() instanceof EntityTailedBeast.Base
			 || entity.getRidingEntity() instanceof EntityKingOfHell.EntityCustom) {
				evt.setCanceled(true);
				entity.getRidingEntity().attackEntityFrom(evt.getSource(), evt.getAmount());
			}
			//if (entity instanceof EntityPlayer && !entity.equals(attacker))
			//	PlayerTracker.logBattleExp((EntityPlayer) entity, 0.5D);
		}
		/*if (attacker instanceof EntitySusanooBase && attacker.isBeingRidden()) {
			attacker = attacker.getControllingPassenger();
		}
		if (entity instanceof EntityLivingBase && attacker instanceof EntityPlayer && !entity.world.isRemote 
		 && (((EntityPlayer)attacker).inventory.armorInventory.get(3).getItem() == ItemMangekyoSharingan.helmet
		  || ((EntityPlayer)attacker).inventory.armorInventory.get(3).getItem() == ItemMangekyoSharinganEternal.helmet)) {
		  	int i = attacker instanceof EntityPlayer ? ((EntityPlayer) attacker).experienceLevel / 30 : 0;
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, 200, i, false, false));
		}*/
	}

	@SubscribeEvent
	public void onEntityAttacked(LivingAttackEvent event) {
		if (event != null && event.getEntity() != null) {
			Entity entity = event.getEntity();
			World world = entity.world;
			HashMap<String, Object> dependencies = new HashMap<>();
			dependencies.put("world", world);
			dependencies.put("entity", entity);
			dependencies.put("event", event);
			executeProcedure(dependencies);
		}
	}

	@SubscribeEvent
	public void onLivingDamaged(LivingDamageEvent event) {
		EntityLivingBase target = event.getEntityLiving();
		if (target instanceof EntityPlayer && ItemRinnegan.hasRinnesharingan((EntityPlayer)target)) {
			event.setAmount(event.getAmount() * 0.1f);
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
