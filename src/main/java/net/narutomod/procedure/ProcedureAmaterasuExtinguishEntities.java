package net.narutomod.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.block.material.Material;

import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.block.BlockAmaterasuBlock;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAmaterasuExtinguishEntities extends ElementsNarutomodMod.ModElement {
	public ProcedureAmaterasuExtinguishEntities(ElementsNarutomodMod instance) {
		super(instance, 95);
	}

	public static void one(Entity entity) {
		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).removePotionEffect(PotionAmaterasuFlame.potion);
			entity.extinguish();
			/*
			 * Collection<PotionEffect> effects =
			 * ((EntityLivingBase)entity).getActivePotionEffects(); for (PotionEffect effect
			 * : effects) { if (effect.getPotion() == PotionAmaterasuFlame.potion) {
			 * ((EntityLivingBase)entity).removePotionEffect(PotionAmaterasuFlame.potion); }
			 * }
			 */
		}
	}

	public static void executeProcedure(final Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure ProcedureAmaterasuExtinguishEntities!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure ProcedureAmaterasuExtinguishEntities!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure ProcedureAmaterasuExtinguishEntities!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ProcedureAmaterasuExtinguishEntities!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		for (int x_area = 15; x_area > -15; --x_area) {
			for (int y_area = 15; y_area > -15; --y_area) {
				for (int z_area = 15; z_area > -15; --z_area) {
					BlockPos pos = new BlockPos(x + x_area, y + y_area, z + z_area);
					if (world.getBlockState(pos).getMaterial() == Material.FIRE
							|| world.getBlockState(pos).getMaterial() == BlockAmaterasuBlock.AMATERASU) {
						world.setBlockToAir(pos);
					}
				}
			}
		}
		if (!world.isRemote) {
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null,
					(new AxisAlignedBB((double) x, (double) y, (double) z, (double) x, (double) y, (double) z)).grow(15));
			for (int i = 0; i < list.size(); ++i)
				one(list.get(i));
		}
	}
}
