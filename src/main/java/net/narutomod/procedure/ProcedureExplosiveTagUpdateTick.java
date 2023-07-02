package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.event.ForgeEventFactory;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.EntityLiving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.IProperty;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureExplosiveTagUpdateTick extends ElementsNarutomodMod.ModElement {
	public ProcedureExplosiveTagUpdateTick(ElementsNarutomodMod instance) {
		super(instance, 429);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure ExplosiveTagUpdateTick!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure ExplosiveTagUpdateTick!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure ExplosiveTagUpdateTick!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ExplosiveTagUpdateTick!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		boolean f1 = false;
		boolean f2 = false;
		if ((!(world.isRemote))) {
			AxisAlignedBB bb = new AxisAlignedBB((double) x - 1, (double) y - 0.1, (double) z - 1, (double) x + 2, (double) y + 1.1, (double) z + 2);
			if (((!world.getEntitiesWithinAABB(EntityLiving.class, bb, IMob.MOB_SELECTOR).isEmpty())
					|| world.getNearestAttackablePlayer(new BlockPos(x, y, z), 1.5, 1) != null)) {
				world.setBlockToAir(new BlockPos((int) x, (int) y, (int) z));
				world.createExplosion(null, x, y, z, 4f, ForgeEventFactory.getMobGriefingEvent(world, null));
			}
			if (((((((new Object() {
				public EnumFacing getEnumFacing(BlockPos pos) {
					try {
						IBlockState _bs = world.getBlockState(pos);
						for (IProperty<?> prop : _bs.getProperties().keySet()) {
							if (prop.getName().equals("facing"))
								return _bs.getValue((PropertyDirection) prop);
						}
						return EnumFacing.NORTH;
					} catch (Exception e) {
						return EnumFacing.NORTH;
					}
				}
			}.getEnumFacing(new BlockPos((int) x, (int) y, (int) z))) == EnumFacing.UP)
					&& (world.isAirBlock(new BlockPos((int) x, (int) (y - 1), (int) z)))) || (((new Object() {
						public EnumFacing getEnumFacing(BlockPos pos) {
							try {
								IBlockState _bs = world.getBlockState(pos);
								for (IProperty<?> prop : _bs.getProperties().keySet()) {
									if (prop.getName().equals("facing"))
										return _bs.getValue((PropertyDirection) prop);
								}
								return EnumFacing.NORTH;
							} catch (Exception e) {
								return EnumFacing.NORTH;
							}
						}
					}.getEnumFacing(new BlockPos((int) x, (int) y, (int) z))) == EnumFacing.DOWN)
							&& (world.isAirBlock(new BlockPos((int) x, (int) (y + 1), (int) z)))))
					|| ((((new Object() {
						public EnumFacing getEnumFacing(BlockPos pos) {
							try {
								IBlockState _bs = world.getBlockState(pos);
								for (IProperty<?> prop : _bs.getProperties().keySet()) {
									if (prop.getName().equals("facing"))
										return _bs.getValue((PropertyDirection) prop);
								}
								return EnumFacing.NORTH;
							} catch (Exception e) {
								return EnumFacing.NORTH;
							}
						}
					}.getEnumFacing(new BlockPos((int) x, (int) y, (int) z))) == EnumFacing.NORTH)
							&& (world.isAirBlock(new BlockPos((int) x, (int) y, (int) (z + 1))))) || (((new Object() {
								public EnumFacing getEnumFacing(BlockPos pos) {
									try {
										IBlockState _bs = world.getBlockState(pos);
										for (IProperty<?> prop : _bs.getProperties().keySet()) {
											if (prop.getName().equals("facing"))
												return _bs.getValue((PropertyDirection) prop);
										}
										return EnumFacing.NORTH;
									} catch (Exception e) {
										return EnumFacing.NORTH;
									}
								}
							}.getEnumFacing(new BlockPos((int) x, (int) y, (int) z))) == EnumFacing.SOUTH)
									&& (world.isAirBlock(new BlockPos((int) x, (int) y, (int) (z - 1)))))))
					|| ((((new Object() {
						public EnumFacing getEnumFacing(BlockPos pos) {
							try {
								IBlockState _bs = world.getBlockState(pos);
								for (IProperty<?> prop : _bs.getProperties().keySet()) {
									if (prop.getName().equals("facing"))
										return _bs.getValue((PropertyDirection) prop);
								}
								return EnumFacing.NORTH;
							} catch (Exception e) {
								return EnumFacing.NORTH;
							}
						}
					}.getEnumFacing(new BlockPos((int) x, (int) y, (int) z))) == EnumFacing.WEST)
							&& (world.isAirBlock(new BlockPos((int) (x + 1), (int) y, (int) z)))) || (((new Object() {
								public EnumFacing getEnumFacing(BlockPos pos) {
									try {
										IBlockState _bs = world.getBlockState(pos);
										for (IProperty<?> prop : _bs.getProperties().keySet()) {
											if (prop.getName().equals("facing"))
												return _bs.getValue((PropertyDirection) prop);
										}
										return EnumFacing.NORTH;
									} catch (Exception e) {
										return EnumFacing.NORTH;
									}
								}
							}.getEnumFacing(new BlockPos((int) x, (int) y, (int) z))) == EnumFacing.EAST)
									&& (world.isAirBlock(new BlockPos((int) (x - 1), (int) y, (int) z))))))) {
				world.getBlockState(new BlockPos((int) x, (int) y, (int) z)).getBlock().dropBlockAsItem(world,
						new BlockPos((int) x, (int) y, (int) z), world.getBlockState(new BlockPos((int) x, (int) y, (int) z)), 1);
				world.setBlockToAir(new BlockPos((int) x, (int) y, (int) z));
			}
		}
	}
}
