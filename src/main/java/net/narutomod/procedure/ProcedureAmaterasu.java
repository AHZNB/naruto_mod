package net.narutomod.procedure;

import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.block.BlockAmaterasuBlock;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAmaterasu extends ElementsNarutomodMod.ModElement {
	public ProcedureAmaterasu(ElementsNarutomodMod instance) {
		super(instance, 78);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure Amaterasu!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure Amaterasu!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure Amaterasu!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure Amaterasu!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure Amaterasu!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure Amaterasu!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		double i = 0;
		double cd_modifier = 0;
		double cooldown = 0;
		double chakraAmount = 0;
		double chakraUsage = 0;
		ItemStack eye = ItemStack.EMPTY;
		eye = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
		if (((eye).hasTagCompound() && (eye).getTagCompound().getBoolean("sharingan_blinded"))) {
			entity.getEntityData().setBoolean("amaterasu_active", (false));
			return;
		}
		cooldown = (double) (entity.getEntityData().getDouble("amaterasu_cd"));
		if ((is_pressed)) {
			chakraAmount = Chakra.pathway((EntityPlayer) entity).getAmount();
			chakraUsage = ItemMangekyoSharingan.getAmaterasuChakraUsage((EntityLivingBase) entity);
			if ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)
					|| ((chakraAmount) >= ((chakraUsage) * 1.25)))) {
				cd_modifier = ProcedureUtils.getCooldownModifier((EntityPlayer) entity);
				if ((!(entity.getEntityData().getBoolean("amaterasu_active")))) {
					if ((!(entity.isSneaking()))) {
						world.playSound((EntityPlayer) null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
								.getObject(new ResourceLocation("narutomod:amaterasu2")), SoundCategory.NEUTRAL, (float) 1, (float) 1);
					}
					cooldown = (double) ((NarutomodModVariables.world_tick) + ((cd_modifier) * 300));
					Chakra.pathway((EntityPlayer) entity).consume(chakraUsage);
				}
				entity.getEntityData().setBoolean("amaterasu_active", (true));
				if ((((cooldown) - (NarutomodModVariables.world_tick)) < 2000)) {
					cooldown = (double) ((cooldown) + ((cd_modifier) * 10));
				}
				entity.getEntityData().setDouble("amaterasu_cd", (cooldown));
				Chakra.pathway((EntityPlayer) entity).consume(chakraUsage * 0.25d);
				RayTraceResult t = ProcedureUtils.objectEntityLookingAt(entity, 30d);
				i = (double) (PlayerTracker.getNinjaLevel((EntityPlayer) entity) / 15);
				if (t.typeOfHit == RayTraceResult.Type.ENTITY) {
					entity = t.entityHit;
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity)
								.addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, (int) 10000, (int) (i), (false), (false)));
				} else {
					x = (int) t.getBlockPos().getX() + t.sideHit.getDirectionVec().getX();
					y = (int) t.getBlockPos().getY() + t.sideHit.getDirectionVec().getY();
					z = (int) t.getBlockPos().getZ() + t.sideHit.getDirectionVec().getZ();
					BlockAmaterasuBlock.placeBlock(world, new BlockPos(x, y, z), (int) i);
				}
			}
		} else {
			if ((entity.isSneaking())) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("world", world);
					$_dependencies
							.put("x",
									(entity.world.rayTraceBlocks(entity.getPositionEyes(1f), entity.getPositionEyes(1f).addVector(
											entity.getLook(1f).x * 50, entity.getLook(1f).y * 50, entity.getLook(1f).z * 50), false, false, true)
											.getBlockPos().getX()));
					$_dependencies
							.put("y",
									(entity.world.rayTraceBlocks(entity.getPositionEyes(1f), entity.getPositionEyes(1f).addVector(
											entity.getLook(1f).x * 50, entity.getLook(1f).y * 50, entity.getLook(1f).z * 50), false, false, true)
											.getBlockPos().getY()));
					$_dependencies
							.put("z",
									(entity.world.rayTraceBlocks(entity.getPositionEyes(1f), entity.getPositionEyes(1f).addVector(
											entity.getLook(1f).x * 50, entity.getLook(1f).y * 50, entity.getLook(1f).z * 50), false, false, true)
											.getBlockPos().getZ()));
					ProcedureAmaterasuExtinguishEntities.executeProcedure($_dependencies);
				}
			} else if (((entity.getEntityData().getBoolean("amaterasu_active"))
					&& (!((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)))) {
				i = (double) (((cooldown) - (NarutomodModVariables.world_tick)) * 0.5);
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (int) (i), (int) 2));
				if (entity instanceof EntityLivingBase)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) ((i) * 6), (int) 0));
			}
			entity.getEntityData().setBoolean("amaterasu_active", (false));
		}
	}
}
