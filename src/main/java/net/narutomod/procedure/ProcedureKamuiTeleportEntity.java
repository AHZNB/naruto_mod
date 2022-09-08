package net.narutomod.procedure;

import net.minecraftforge.common.DimensionManager;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.world.Teleporter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;

import net.narutomod.world.WorldKamuiDimension;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureKamuiTeleportEntity extends ElementsNarutomodMod.ModElement {
	// private static double playerOverworldY;
	public ProcedureKamuiTeleportEntity(ElementsNarutomodMod instance) {
		super(instance, 146);
	}

	public static void eEntity(Entity entity, int x, int z, int dimid) {
		if (!entity.world.isRemote) {
			if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, dimid)) {
				return;
			}
			if (entity instanceof EntityPlayerMP) {
				if (!entity.isRiding() && !entity.isBeingRidden()) {
					class TeleporterDirect extends Teleporter {
						public TeleporterDirect(WorldServer worldserver) {
							super(worldserver);
						}

						@Override
						public void placeInPortal(Entity entity, float yawrotation) {
						}

						@Override
						public boolean placeInExistingPortal(Entity entity, float yawrotation) {
							return true;
						}

						@Override
						public boolean makePortal(Entity entity) {
							return true;
						}
					}
					EntityPlayerMP _player = (EntityPlayerMP) entity;
					if (_player.dimension == 0) {
						// playerOverworldY = _player.posY;
						_player.getEntityData().setDouble("lastOverworldY", _player.posY);
					} else if (_player.dimension != WorldKamuiDimension.DIMID) {
						// playerOverworldY = DimensionManager.getWorld(0).getSpawnPoint().getY();
						_player.getEntityData().setDouble("lastOverworldY", DimensionManager.getWorld(0).getSpawnPoint().getY());
					}
					_player.mcServer.getPlayerList().transferPlayerToDimension(_player, dimid, new TeleporterDirect(_player.getServerWorld()));
					double y = dimid == WorldKamuiDimension.DIMID
					  ? DimensionManager.getWorld(dimid).getSpawnPoint().getY() : _player.getEntityData().getDouble("lastOverworldY");
					_player.connection.setPlayerLocation(x, y + 1, z, _player.rotationYaw, _player.rotationPitch);
				}
			} else {
				if (entity.dimension == 0) {
					entity.getEntityData().setDouble("lastOverworldY", entity.posY);
				} else if (entity.dimension != WorldKamuiDimension.DIMID) {
					entity.getEntityData().setDouble("lastOverworldY", DimensionManager.getWorld(0).getSpawnPoint().getY());
				}
				entity = entity.changeDimension(dimid);
				if (entity != null) {
					double y = dimid == WorldKamuiDimension.DIMID ? DimensionManager.getWorld(dimid).getSpawnPoint().getY() 
					                                              : entity.getEntityData().getDouble("lastOverworldY");
					entity.setPositionAndUpdate(x, y + 1, z);
				}
			}
		}
	}

	public static int eBlock(World world, BlockPos pos, int dimid) {
		int i = 0;
		List<ItemStack> drops = world.getBlockState(pos).getBlock().getDrops(world, pos, world.getBlockState(pos), 1);
		for (ItemStack drop : drops) {
			EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), drop);
			entityitem.setDefaultPickupDelay();
			world.spawnEntity(entityitem);
			entityitem.changeDimension(dimid);
			i++;
		}
		world.setBlockToAir(pos);
		return i;
	}
}
