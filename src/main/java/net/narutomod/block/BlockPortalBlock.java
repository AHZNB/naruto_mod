package net.narutomod.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.Block;
import net.minecraft.init.Items;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.event.EventDelayedCallback;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class BlockPortalBlock extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:portalblock")
	public static final Block block = null;

	public BlockPortalBlock(ElementsNarutomodMod instance) {
		super(instance, 276);
	}

	@Override
	public void initElements() {
		this.elements.blocks.add(() -> new BlockCustom());
		this.elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TileEntityCustom.class, "narutomod:tileentityportalblock");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
				new ModelResourceLocation("narutomod:portalblock", "inventory"));
	}

	public static class BlockCustom extends BlockEndPortal {
		public static final PropertyDirection FACING = BlockHorizontal.FACING;
		protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.3D);
		protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.7D, 1.0D, 1.0D, 1.0D);
		protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.3D, 1.0D, 1.0D);
		protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.7D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

		public BlockCustom() {
			super(Material.PORTAL);
			this.setRegistryName("portalblock");
			this.setUnlocalizedName("portalblock");
			this.setCreativeTab(null);
			this.setBlockUnbreakable();
			this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		}

		@Override
		public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
			if ((EnumFacing) state.getValue(FACING) == EnumFacing.SOUTH)
				return SOUTH_AABB;
			if ((EnumFacing) state.getValue(FACING) == EnumFacing.EAST)
				return EAST_AABB;
			if ((EnumFacing) state.getValue(FACING) == EnumFacing.WEST)
				return WEST_AABB;
			return NORTH_AABB;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
			return (side != EnumFacing.UP && side != EnumFacing.DOWN);
		}

		@Override
		public TileEntity createNewTileEntity(World worldIn, int meta) {
			return new TileEntityCustom(EnumFacing.getFront(meta));
		}

		@Override
		public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
			worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
		}

		@Override
		public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
			super.updateTick(world, pos, state, random);
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityCustom) {
				TileEntityCustom te2 = (TileEntityCustom) te;
				te2.update();
				if (te2.cooldown == 0) {
					world.setBlockToAir(pos);
					return;
				}
			}
			world.scheduleUpdate(pos, this, tickRate(world));
		}

		@Override
		public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof TileEntityCustom)
				((TileEntityCustom) tileentity).teleportEntity(entity);
		}

		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) {
			return Items.AIR;
		}

		@Override
		public IBlockState getStateFromMeta(int meta) {
			EnumFacing enumfacing = EnumFacing.getFront(meta);
			if (enumfacing.getAxis() == EnumFacing.Axis.Y)
				enumfacing = EnumFacing.NORTH;
			return getDefaultState().withProperty(FACING, enumfacing);
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			return ((EnumFacing) state.getValue(FACING)).getIndex();
		}

		@Override
		protected BlockStateContainer createBlockState() {
			return new BlockStateContainer(this, new IProperty[]{FACING});
		}
	}


	public static class TileEntityCustom extends TileEntityEndPortal {
		private static final EventCallback CB = new EventCallback();
		private EnumFacing facing;
		private BlockPos pairPos;
		private int ticksExisted;
		private int cooldown = 10;

		public TileEntityCustom(EnumFacing side) {
			this.facing = side;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean shouldRenderFace(EnumFacing side) {
			return (side == this.facing);
		}

		public void setPair(BlockPos pos) {
			this.pairPos = pos;
		}

		public void update() {
			this.ticksExisted++;
			if (this.cooldown > 0)
				this.cooldown--;
		}

		public void teleportEntity(Entity entity) {
			if (this.pairPos != null && this.cooldown <= 10) {
				TileEntity tileentity = this.world.getTileEntity(this.pairPos);
				if (tileentity instanceof TileEntityCustom) {
					TileEntityCustom te = (TileEntityCustom) tileentity;
					this.cooldown = 20;
					if (!this.world.isRemote) {
						entity.rotationYaw = te.facing.getHorizontalAngle();
						if (entity instanceof EntityPlayerMP) {
							ProcedureUtils.setInvulnerableDimensionChange((EntityPlayerMP)entity);
							new EventDelayedCallback(this.world, 0, 0, 0, entity, this.world.getTotalWorldTime() + 3, CB);
						}
						entity.setPositionAndUpdate(this.pairPos.getX() + 0.5D + te.facing.getFrontOffsetX(),
								this.pairPos.getY() - ((this.world.getBlockState(this.pairPos.down()).getBlock() == block) ? 1.0D : 0.0D),
								this.pairPos.getZ() + 0.5D + te.facing.getFrontOffsetZ());
					}
				}
			}
		}

		public static class EventCallback extends EventDelayedCallback.Callback {
			public EventCallback() {
				super(276);
			}
	
			@Override
			public void execute(World world, int x, int y, int z, @Nullable Entity entity) {
				if (entity instanceof EntityPlayerMP && ((EntityPlayerMP)entity).isInvulnerableDimensionChange()) {
					((EntityPlayerMP)entity).clearInvulnerableDimensionChange();
				}
			}
		}
	}
}
