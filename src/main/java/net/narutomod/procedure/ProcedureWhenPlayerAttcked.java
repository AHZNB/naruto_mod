package net.narutomod.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.CombatRules;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.potion.PotionEffect;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

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
		if (!(entity instanceof EntityLivingBase)) {
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
		if (evt.getSource() != null) {
			attacker = evt.getSource().getTrueSource();
		}
		if (!world.isRemote) {
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
			Entity ridingEntity = entity.getRidingEntity();
			if ((ridingEntity instanceof EntitySusanooBase 
			  || ridingEntity instanceof EntityShieldBase
			  || ridingEntity instanceof EntityTailedBeast.Base)
			 && ridingEntity.isEntityAlive() && evt.getSource() != ProcedureUtils.SPECIAL_DAMAGE) {
				evt.setCanceled(true);
				ridingEntity.attackEntityFrom(evt.getSource(), evt.getAmount());
				/*float f = ((EntityLivingBase)ridingEntity).getHealth();
				if (ridingEntity.attackEntityFrom(evt.getSource(), evt.getAmount()) && !ridingEntity.isEntityAlive()) {
					entity.attackEntityFrom(evt.getSource(), CombatRules.getDamageAfterAbsorb(evt.getAmount(),
					 (float)((EntityLivingBase)ridingEntity).getTotalArmorValue(), 0f) - f);
				}*/
			}
			if (ridingEntity instanceof EntityKingOfHell.EntityCustom) {
				evt.setCanceled(true);
				ridingEntity.attackEntityFrom(evt.getSource(), evt.getAmount());
			}
		}
		if (attacker instanceof EntityPlayer && !evt.getSource().getImmediateSource().equals(attacker)) {
			((EntityLivingBase)attacker).setLastAttackedEntity(entity);
		}
	}

	@SubscribeEvent
	public void onLivingAttacked(LivingAttackEvent event) {
		if (event != null && event.getEntityLiving() != null) {
			EntityLivingBase entity = event.getEntityLiving();
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
