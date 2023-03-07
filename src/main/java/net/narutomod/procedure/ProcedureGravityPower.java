package net.narutomod.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;
//import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityEarthBlocks;

import java.util.List;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureGravityPower extends ElementsNarutomodMod.ModElement {
	public ProcedureGravityPower(ElementsNarutomodMod instance) {
		super(instance, 229);
	}

	public static EntityEarthBlocks.Base dislodgeBlocks(World world, BlockPos centerPos, int size) {
		if (!world.isRemote) {
			List<BlockPos> affectedBlocks = Lists.newArrayList();
			int x = centerPos.getX();
			int y = centerPos.getY();
			int z = centerPos.getZ();
			for (int j = -size / 2; j < size / 2 + (size % 2); j++) {
				for (int k = -size / 2; k < size / 2 + (size % 2); k++) {
					for (int i = -size / 2; i < size / 2 + (size % 2); i++) {
						BlockPos pos = new BlockPos(x+i, y+j, z+k);
						IBlockState blockstate = world.getBlockState(pos);
						if (!world.isAirBlock(pos) && !blockstate.getMaterial().isLiquid() && blockstate.getBlockHardness(world, pos) >= 0f) {
							affectedBlocks.add(pos);
						}
					}
				}
			}
			if (!affectedBlocks.isEmpty()) {
				EntityEarthBlocks.Base entity = new EntityEarthBlocks.Base(world, affectedBlocks);
				entity.setNoGravity(true);
				//entity.motionX = 0.2D * raytraceres.sideHit.getDirectionVec().getX();
				//entity.motionY = 0.2D * raytraceres.sideHit.getDirectionVec().getY();
				//entity.motionZ = 0.2D * raytraceres.sideHit.getDirectionVec().getZ();
				world.spawnEntity(entity);
				return entity;
			}
		}
		return null;
	}
	
	public static class Obj {
		private EntityEarthBlocks.Base blocksEntity;
		private World world;
		private int size;
		
		public Obj(World worldIn, int sizeIn) {
			this.blocksEntity = null;
			this.world = worldIn;
			this.size = sizeIn;
		}

		public void dislodge(RayTraceResult raytraceres) {
			if (this.blocksEntity == null) {
				this.blocksEntity = dislodgeBlocks(this.world, raytraceres.getBlockPos(), size);
			}
		}

		public EntityEarthBlocks.Base getEntity() {
			return this.blocksEntity;
		}
	}

	public static void executeProcedure(java.util.Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure MCreatorGravityPower!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure MCreatorGravityPower!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure MCreatorGravityPower!");
			return;
		}
		boolean is_pressed = ((Boolean) dependencies.get("is_pressed")).booleanValue();
		World world = (World) dependencies.get("world");
		Entity entity = (Entity) dependencies.get("entity");
		double press_time = entity.getEntityData().getDouble("press_time");
		if (is_pressed) {
			entity.getEntityData().setDouble("press_time", press_time + 1.0D);
		} else {
			entity.getEntityData().setDouble("press_time", 0.0D);
			RayTraceResult rtr = ProcedureUtils.raytraceBlocks(entity, 100.0D);
			if (!world.isRemote && rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
				//new Obj(world, (int) (press_time / 5.0D)).dislodge(rtr);
				Entity entity1 = dislodgeBlocks(world, rtr.getBlockPos(), (int) (press_time / 5.0D));
				if (entity1 != null) {
					entity1.motionX = 0.2D * rtr.sideHit.getDirectionVec().getX();
					entity1.motionY = 0.2D * rtr.sideHit.getDirectionVec().getY();
					entity1.motionZ = 0.2D * rtr.sideHit.getDirectionVec().getZ();
					//world.spawnEntity(entity1);
				}
			}
		}
	}
}
