package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Rotation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Mirror;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSpawnGodTree extends ElementsNarutomodMod.ModElement {
	public ProcedureSpawnGodTree(ElementsNarutomodMod instance) {
		super(instance, 464);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure SpawnGodTree!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure SpawnGodTree!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure SpawnGodTree!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SpawnGodTree!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_1"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) x, (int) y, (int) z);
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_2"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) x, (int) (y + 31), (int) z);
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_3"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) x, (int) (y + 60), (int) z);
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_6"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) x, (int) (y + 90), (int) z);
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_4"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x - 23), (int) (y + 60), (int) z);
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_15"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x + 27), (int) (y + 60), (int) z);
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_9"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) x, (int) (y + 60), (int) (z - 23));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_10"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) x, (int) (y + 60), (int) (z + 27));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_12"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) x, (int) (y + 90), (int) (z - 23));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_14"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) x, (int) (y + 90), (int) (z + 27));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_7"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x - 23), (int) (y + 60), (int) (z - 23));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_8"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x - 23), (int) (y + 60), (int) (z + 27));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_5"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x - 23), (int) (y + 90), (int) z);
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_11"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x - 23), (int) (y + 90), (int) (z - 23));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_13"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x - 23), (int) (y + 90), (int) (z + 27));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_15"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x + 27), (int) (y + 60), (int) z);
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_16"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x + 27), (int) (y + 60), (int) (z - 23));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_17"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x + 27), (int) (y + 60), (int) (z + 27));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_19"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x + 27), (int) (y + 90), (int) z);
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_18"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x + 27), (int) (y + 90), (int) (z - 23));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
		if (!world.isRemote) {
			Template template = ((WorldServer) world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
					new ResourceLocation("narutomod", "world_tree_20"));
			if (template != null) {
				BlockPos spawnTo = new BlockPos((int) (x + 27), (int) (y + 90), (int) (z + 27));
				IBlockState iblockstate = world.getBlockState(spawnTo);
				world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
				template.addBlocksToWorldChunk(world, spawnTo, new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
						.setChunk((ChunkPos) null).setReplacedBlock((Block) null).setIgnoreStructureBlock(false).setIgnoreEntities(false));
			}
		}
	}
}
