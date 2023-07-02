package net.narutomod.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;

import net.narutomod.block.BlockPortalBlock;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureYomotsuHirasaka extends ElementsNarutomodMod.ModElement {
	public ProcedureYomotsuHirasaka(ElementsNarutomodMod instance) {
		super(instance, 279);
	}

	public static void executeProcedure(java.util.Map<String, Object> dependencies) {
		EnumFacing enumfacing2;
		BlockPos pos2;
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency 'entity' for procedure ProcedureYomotsuHirasaka!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency 'world' for procedure ProcedureYomotsuHirasaka!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		BlockPos pos1 = new BlockPos(entity.getPositionEyes(1.0F).add(entity.getLookVec().scale(2.0D)));
		EnumFacing enumfacing1 = EnumFacing.getHorizontal(MathHelper.floor((entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3).getOpposite();
		RayTraceResult result = ProcedureUtils.objectEntityLookingAt(entity, 150.0D, true);
		if (result.entityHit != null) {
			enumfacing2 = EnumFacing.getHorizontal(MathHelper.floor((result.entityHit.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3);
			pos2 = new BlockPos(result.hitVec).up();
		} else {
			enumfacing2 = EnumFacing.getHorizontal(MathHelper.floor((entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3);
			for (pos2 = result.getBlockPos(); pos2.getY() <= 0 || !world.isAirBlock(pos2) || !world.isAirBlock(pos2.down()); pos2 = pos2.up());
		}
		world.playSound(null, entity.posX, entity.posY, entity.posZ,
				net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("block.portal.travel")),
				SoundCategory.BLOCKS, 0.3F, 1.0F);
		IBlockState state1 = BlockPortalBlock.block.getDefaultState().withProperty(BlockPortalBlock.BlockCustom.FACING, enumfacing1);
		world.setBlockState(pos1, state1, 3);
		world.setBlockState(pos1.down(), state1, 3);
		BlockPortalBlock.TileEntityCustom te1 = (BlockPortalBlock.TileEntityCustom) world.getTileEntity(pos1);
		BlockPortalBlock.TileEntityCustom te11 = (BlockPortalBlock.TileEntityCustom) world.getTileEntity(pos1.down());
		IBlockState state2 = BlockPortalBlock.block.getDefaultState().withProperty(BlockPortalBlock.BlockCustom.FACING, enumfacing2);
		world.setBlockState(pos2, state2, 3);
		world.setBlockState(pos2.down(), state2, 3);
		BlockPortalBlock.TileEntityCustom te2 = (BlockPortalBlock.TileEntityCustom) world.getTileEntity(pos2);
		BlockPortalBlock.TileEntityCustom te21 = (BlockPortalBlock.TileEntityCustom) world.getTileEntity(pos2.down());
		te1.setPair(pos2);
		te11.setPair(pos2.down());
		te2.setPair(pos1);
		te21.setPair(pos1.down());
	}
}
