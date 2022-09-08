package net.narutomod.procedure;

import net.narutomod.entity.EntityMightGuy;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.biome.BiomeTaiga;
import net.minecraft.world.biome.BiomeSavanna;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.village.Village;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.block.material.Material;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureCheckVillageSize extends ElementsNarutomodMod.ModElement {
	public ProcedureCheckVillageSize(ElementsNarutomodMod instance) {
		super(instance, 335);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure CheckVillageSize!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure CheckVillageSize!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		boolean f1 = false;
		double x1 = 0;
		double y1 = 0;
		double z1 = 0;
		double radius = 0;
		if (((!(world.isRemote)) && ((((NarutomodModVariables.world_tick) % 400) == 0)
				&& (((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
						? ((EntityPlayerMP) entity).getAdvancements()
								.getProgress(((WorldServer) (entity).world).getAdvancementManager()
										.getAdvancement(new ResourceLocation("narutomod:ninjaachievement")))
								.isDone()
						: false)))) {
			Village village = world.getVillageCollection().getNearestVillage(new BlockPos(entity), 32);
			f1 = ((world.getBiome(new BlockPos(entity)) instanceof BiomeSavanna || world.getBiome(new BlockPos(entity)) instanceof BiomeTaiga)
					&& village != null && village.getNumVillageDoors() >= 20 && village.getNumVillagers() >= 10
					&& world.getEntitiesWithinAABB(EntityMightGuy.EntityCustom.class, new AxisAlignedBB(village.getCenter()).grow(96.0D)).isEmpty());
			if ((f1)) {
				x1 = village.getCenter().getX();
				y1 = village.getCenter().getY();
				z1 = village.getCenter().getZ();
				radius = village.getVillageRadius();
				x1 = (double) ((x1) + ((Math.random() - 0.5) * ((radius) * 2)));
				z1 = (double) ((z1) + ((Math.random() - 0.5) * ((radius) * 2)));
				y1 = (double) 250;
				while ((world.isAirBlock(new BlockPos((int) (x1), (int) (y1), (int) (z1))))) {
					y1 = (double) ((y1) - 1);
				}
				if ((!((world.getBlockState(new BlockPos((int) (x1), (int) (y1), (int) (z1)))).getMaterial() == Material.WATER))) {
					if (!world.isRemote) {
						Entity entityToSpawn = new EntityMightGuy.EntityCustom(world);
						if (entityToSpawn != null) {
							entityToSpawn.setLocationAndAngles((x1), ((y1) + 2), (z1), world.rand.nextFloat() * 360F, 0.0F);
							world.spawnEntity(entityToSpawn);
						}
					}
				}
			}
		}
	}
}
