package net.narutomod.procedure;

import net.narutomod.world.WorldKamuiDimension;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.gui.overlay.OverlayByakuganView;
import net.narutomod.PlayerTracker;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureKamuiJikukanIdo extends ElementsNarutomodMod.ModElement {
	public ProcedureKamuiJikukanIdo(ElementsNarutomodMod instance) {
		super(instance, 119);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure KamuiJikukanIdo!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure KamuiJikukanIdo!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure KamuiJikukanIdo!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure KamuiJikukanIdo!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure KamuiJikukanIdo!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure KamuiJikukanIdo!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		boolean f1 = false;
		boolean f2 = false;
		boolean f3 = false;
		double fov = 0;
		double distance = 0;
		double i = 0;
		double timer = 0;
		double chakraAmount = 0;
		double chakraUsage = 0;
		if ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY).hasTagCompound()
				&& ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY).getTagCompound()
						.getBoolean("sharingan_blinded"))) {
			if ((entity.getEntityData().getBoolean("kamui_teleport"))) {
				OverlayByakuganView.sendCustomData(entity, false, 70);
				entity.getEntityData().setBoolean("kamui_teleport", (false));
			}
			return;
		}
		timer = (double) ((entity.getEntityData().getDouble("kamui_timer")) + 1);
		chakraAmount = (double) Chakra.pathway((EntityPlayer) entity).getAmount();
		if ((!(entity.isSneaking()))) {
			if ((entity.getEntityData().getBoolean("kamui_teleport"))) {
				OverlayByakuganView.sendCustomData(entity, false, 70);
				entity.getEntityData().setBoolean("kamui_teleport", (false));
				timer = (double) (-1);
			}
			if ((world.getTotalWorldTime() > entity.getEntityData().getLong("kamui_intangible_cd"))) {
				chakraUsage = (double) ItemMangekyoSharinganObito.getIntangibleChakraUsage((EntityLivingBase) entity);;
				f2 = (boolean) (((is_pressed) && ((timer) <= 600)) && ((chakraAmount) > (chakraUsage)));
				if ((f2)) {
					ProcedureUtils.purgeHarmfulEffects((EntityLivingBase) entity);
					ProcedureOnLivingUpdate.setUntargetable(entity, 3);
					entity.fallDistance = (float) (0);
				}
				if (entity instanceof EntityPlayer) {
					((EntityPlayer) entity).capabilities.allowEdit = (!(f2));
					((EntityPlayer) entity).sendPlayerAbilities();
				}
				if (entity instanceof EntityPlayer) {
					((EntityPlayer) entity).capabilities.isFlying = (f2);
					((EntityPlayer) entity).sendPlayerAbilities();
				}
				ProcedureOnLivingUpdate.setNoClip(entity, f2);
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
							((net.minecraft.util.text.translation.I18n.translateToLocal("chattext.intangible")) + "" + ((f2)))), (true));
				}
				entity.getEntityData().setBoolean("kamui_intangible", (f2));
				if ((!(f2))) {
					if (((timer) > 400)) {
						entity.getEntityData().setLong("kamui_intangible_cd", world.getTotalWorldTime() + (long) timer - 400);
					}
					timer = (double) (-1);
				}
			} else if ((entity instanceof EntityPlayer)) {
				((EntityPlayer) entity).sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted",
						(entity.getEntityData().getLong("kamui_intangible_cd") - world.getTotalWorldTime()) / 20), true);
			}
		} else {
			if ((entity.getEntityData().getBoolean("kamui_intangible"))) {
				ProcedureOnLivingUpdate.setNoClip(entity, is_pressed);
				entity.getEntityData().setBoolean("kamui_intangible", (is_pressed));
				if ((!(is_pressed))) {
					timer = (double) (-1);
				}
			} else {
				RayTraceResult t = ProcedureUtils.objectEntityLookingAt(entity, 100d, true);
				x = (int) t.hitVec.x;
				y = (int) t.hitVec.y;
				z = (int) t.hitVec.z;
				f3 = (boolean) (x == (int) entity.posX && y == (int) entity.posY && z == (int) entity.posZ);
				f1 = (boolean) (t.typeOfHit != RayTraceResult.Type.MISS);
				distance = (double) entity.getDistance(x, y, z);
				if ((is_pressed)) {
					chakraUsage = (double) ItemMangekyoSharinganObito.getTeleportChakraUsage((EntityLivingBase) entity);;
					if ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)
							|| ((f3) || ((chakraAmount) > (chakraUsage))))) {
						entity.getEntityData().setBoolean("kamui_teleport", (true));
						if ((f1)) {
							fov = (double) (70 - (Math.log((distance)) * 15));
							OverlayByakuganView.sendCustomData(entity, false, (float) fov);
						}
						if ((((timer) % 60) == 1)) {
							world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
									.getObject(new ResourceLocation("narutomod:KamuiSFX")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
						}
						Particles.spawnParticle(world, Particles.Types.PORTAL_SPIRAL, t.hitVec.x, t.hitVec.y, t.hitVec.z, 100, 0d, 0d, 0d, 0d, 0d, 0d,
								5, 0x20000000, 30);
					} else {
						timer = (double) (-1);
					}
				} else if ((entity.getEntityData().getBoolean("kamui_teleport"))) {
					entity.getEntityData().setBoolean("kamui_teleport", (false));
					OverlayByakuganView.sendCustomData(entity, false, 70);
					if ((f3)) {
						t.entityHit = entity;
					}
					int dimid = (entity.dimension != WorldKamuiDimension.DIMID) ? WorldKamuiDimension.DIMID : 0;
					f2 = (boolean) (t.entityHit != null);
					if ((f2)) {
						i = t.entityHit.getEntityBoundingBox().getAverageEdgeLength();
						i = (double) (((timer) - 5) / (((distance) * (i)) * (2.01 - (PlayerTracker.getNinjaLevel((EntityPlayer) entity) / 500.1))));
						if (((!(f3)) && ((i) <= 0.99999))) {
							if (((i) > 0)) {
								i = (double) ((i)
										* ((t.entityHit instanceof EntityLivingBase) ? ((EntityLivingBase) t.entityHit).getMaxHealth() : -1));
								t.entityHit.attackEntityFrom(DamageSource.OUT_OF_WORLD.setDamageIsAbsolute(), Math.min((float) i, 1024f));
							}
						} else {
							ProcedureKamuiTeleportEntity.eEntity(t.entityHit, x, z, dimid);
						}
					}
					if ((!(f3))) {
						if (entity instanceof EntityLivingBase)
							((EntityLivingBase) entity)
									.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) ((timer) * 6), (int) 1, (false), (false)));
						if (entity instanceof EntityLivingBase)
							((EntityLivingBase) entity)
									.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) ((timer) * 6), (int) 2, (false), (false)));
					}
					timer = (double) (-1);
				}
			}
		}
		entity.getEntityData().setDouble("kamui_timer", (timer));
	}
}
