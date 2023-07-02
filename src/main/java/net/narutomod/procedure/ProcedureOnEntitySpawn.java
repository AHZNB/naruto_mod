package net.narutomod.procedure;

import net.narutomod.entity.EntityTailedBeast;
import net.narutomod.entity.EntityGedoStatue;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeHooks;

import net.minecraft.world.World;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureOnEntitySpawn extends ElementsNarutomodMod.ModElement {
	public ProcedureOnEntitySpawn(ElementsNarutomodMod instance) {
		super(instance, 254);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure OnEntitySpawn!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure OnEntitySpawn!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double age = 0;
		boolean f1 = false;
		boolean f2 = false;
		if (entity instanceof EntityTailedBeast.Base) {
			if ((!(world.isRemote))) {
				f1 = (boolean) ((EntityTailedBeast.Base) entity).getBijuManager().isAddedToWorld();
				f2 = (boolean) ((EntityTailedBeast.Base) entity).getBijuManager().isSealed();
				age = (double) ((EntityTailedBeast.Base) entity).getAge();
				if ((((f1) && ((age) < 1)) || (f2))) {
					if (dependencies.get("event") != null) {
						Object _obj = dependencies.get("event");
						if (_obj instanceof net.minecraftforge.fml.common.eventhandler.Event) {
							net.minecraftforge.fml.common.eventhandler.Event _evt = (net.minecraftforge.fml.common.eventhandler.Event) _obj;
							if (_evt.isCancelable())
								_evt.setCanceled(true);
						}
					}
				}
			}
		} else if (entity instanceof EntityGedoStatue.EntityCustom) {
			if ((!(world.isRemote))) {
				f1 = (boolean) EntityGedoStatue.isAddedToWorld(world);
				age = (double) ((EntityGedoStatue.EntityCustom) entity).getAge();
				if (((f1) && ((age) < 1))) {
					if (dependencies.get("event") != null) {
						Object _obj = dependencies.get("event");
						if (_obj instanceof net.minecraftforge.fml.common.eventhandler.Event) {
							net.minecraftforge.fml.common.eventhandler.Event _evt = (net.minecraftforge.fml.common.eventhandler.Event) _obj;
							if (_evt.isCancelable())
								_evt.setCanceled(true);
						}
					}
				}
			}
		}
		if ((entity instanceof EntityPlayerMP)) {
			ProcedureOnLivingUpdate.setNoClip(entity, false);
			if (((world.provider.getDimension()) == (0))) {
				((EntityPlayer) entity).sendStatusMessage(ForgeHooks.newChatWithLinks(I18n.translateToLocal("chattext.intro.message1")), false);
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoin(EntityJoinWorldEvent event) {
		World world = event.getWorld();
		Entity entity = event.getEntity();
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

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
