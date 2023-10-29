package net.narutomod.procedure;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;
import net.narutomod.entity.EntityEarthBlocks;
import net.narutomod.entity.EntityChibakuTenseiBall;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureBanShoTenin extends ElementsNarutomodMod.ModElement {
	private static final Map<Entity, ProcedurePullAndHold> map = Maps.<Entity, ProcedurePullAndHold>newHashMap();
	private static final double CHAKRA_USAGE = 0.5d; // per tick
	public static final String BSTN_CD = "BanshoTenin_cooldown";
	
	public ProcedureBanShoTenin(ElementsNarutomodMod instance) {
		super(instance, 155);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure ProcedureBanShoTenin!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ProcedureBanShoTenin!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		int cooldown = entity.getEntityData().getInteger(BSTN_CD);
		if ((int)entity.world.getTotalWorldTime() > cooldown) {
			RayTraceResult t = ProcedureUtils.objectEntityLookingAt(entity, 50d);
			Entity grabbedEntity = null;
			ProcedurePullAndHold procedure = map.get(entity);
			if (procedure == null) {
				procedure = new ProcedurePullAndHold();
				map.put(entity, procedure);
			}
			Chakra.Pathway cp = Chakra.pathway((EntityPlayer)entity);
			if (is_pressed) {
				if (cp.getAmount() < CHAKRA_USAGE) {
					is_pressed = false;
					cp.warningDisplay();
				} else if (procedure.getGrabbedEntity() == null) {
					if (t.entityHit != null && !(t.entityHit instanceof EntityChibakuTenseiBall.EntityCustom)
					 && (!(t.entityHit instanceof EntityEarthBlocks.Base) || t.entityHit.ticksExisted > 5)
					 && t.entityHit.height < 24) {
						grabbedEntity = t.entityHit;
						entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
						  SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:BanshoTenin")), 
						  SoundCategory.PLAYERS, 5.0F, 1.0F);
					} else if (entity.isSneaking() && t.typeOfHit == RayTraceResult.Type.BLOCK) {
						entity.world.playSound(null, t.getBlockPos(), SoundEvent.REGISTRY
						  .getObject(new ResourceLocation("narutomod:rocks")), SoundCategory.NEUTRAL, 5.0F, 0.5F);
						EntityEarthBlocks.Base entity1 = ProcedureGravityPower.dislodgeBlocks(entity.world, t.getBlockPos(), 5);
						if (entity1 != null) {
							entity1.motionX = 0.2D * t.sideHit.getDirectionVec().getX();
							entity1.motionY = 0.2D * t.sideHit.getDirectionVec().getY();
							entity1.motionZ = 0.2D * t.sideHit.getDirectionVec().getZ();
						}
						procedure.addEarthBlock(entity1);
						cp.consume(CHAKRA_USAGE);
					}
				}
				if (procedure.getGrabbedEntity() != null) {
					cp.consume(CHAKRA_USAGE);
				}
			} else if (procedure.getGrabbedEntity() != null) {
				cooldown = (int)entity.world.getTotalWorldTime() + 100;
			}
			procedure.execute(is_pressed, entity, grabbedEntity);
		} else {
			if (entity instanceof EntityPlayer && !entity.world.isRemote) {
				((EntityPlayer) entity).sendStatusMessage(
					new TextComponentTranslation("chattext.cooldown.formatted", (new java.text.DecimalFormat(".1").format(
					((float)entity.getEntityData().getInteger(BSTN_CD) - (int)entity.world.getTotalWorldTime()) / 20))), 
					true);
			}
		}
		entity.getEntityData().setInteger(BSTN_CD, cooldown);
	}

	public static List<EntityEarthBlocks.Base> getGrabbedEarthBlocks(Entity entity) {
		if (map.containsKey(entity)) {
			return map.get(entity).getGrabbedEarthBlocks();
		}
		return Lists.newArrayList();
	}

	public class PlayerHook {
		@SubscribeEvent(priority = EventPriority.HIGH)
		public void onDeath(LivingDeathEvent event) {
			Entity entity = event.getEntityLiving();
			if (entity instanceof EntityPlayer && !entity.world.isRemote && map.containsKey(entity)) {
				map.get(entity).reset();
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerHook());
	}
}
