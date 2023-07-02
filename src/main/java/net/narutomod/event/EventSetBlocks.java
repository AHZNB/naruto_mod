package net.narutomod.event;

import net.narutomod.block.BlockMud;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.init.Blocks;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.Map;
import java.util.List;

public class EventSetBlocks extends SpecialEvent {
	private List<Template.BlockInfo> blocksList = Lists.newArrayList();
	private int lifespan;
	private int addIndex;
	private int removeIndex;
	
	public EventSetBlocks() {
		super();
	}

	public EventSetBlocks(World worldIn, Map<BlockPos, IBlockState> map, long startTime, int life) {
		this(worldIn, map, startTime, life, true, true);
	}

	public EventSetBlocks(World worldIn, Map<BlockPos, IBlockState> map, long startTime, int life, boolean particlesIn, boolean sounds) {
		super(EnumEventType.SET_BLOCKS, worldIn, null, 0, 0, 0, startTime, particlesIn, sounds);
		if (!worldIn.isRemote) {
			for (Map.Entry<BlockPos, IBlockState> entry : map.entrySet()) {
				this.blocksList.add(new Template.BlockInfo(entry.getKey(), entry.getValue(), new NBTTagCompound()));
			}
			this.lifespan = life;
			this.addIndex = 0;
			this.removeIndex = 0;
		}
	}

	public EventSetBlocks(World worldIn, List<Template.BlockInfo> list, long startTime, int life, boolean particlesIn, boolean sounds) {
		super(EnumEventType.SET_BLOCKS, worldIn, null, 0, 0, 0, startTime, particlesIn, sounds);
		if (!worldIn.isRemote) {
			this.blocksList = Lists.newArrayList(list);
			this.lifespan = life;
			this.addIndex = 0;
			this.removeIndex = 0;
		}
	}

	@Override
	protected void onUpdate() {
		if (!this.shouldExecute())
			return;

		super.onUpdate();

		boolean flag = this.lifespan > 0 && this.world.getTotalWorldTime() > this.startTime + this.lifespan;
		boolean flag2 = this.addIndex < this.blocksList.size();
		if (flag2 || flag) {
			if (flag2) {
				this.onAddTick(this.tick);
			}
			if (flag) {
				this.onRemoveTick(this.tick);
			}
			int j = 0;
			for (int i = this.addIndex; i < this.blocksList.size() && j < 64; i++) {
				Template.BlockInfo blockinfo = this.blocksList.get(i);
				BlockPos pos = blockinfo.pos;
				IBlockState state = blockinfo.blockState;
				if (state.getBlock() != Blocks.AIR || !this.world.isAirBlock(pos)) {
					if (this.sound && j == 0 && this.tick % 5 == 1) {
						SoundType soundtype = state.getBlock().getSoundType();
						this.world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, 
				 		 soundtype.getVolume() * 0.5F, soundtype.getPitch());
				 		 j += 7;
				 	}
				 	if (this.particles) {
						((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, pos.getX()+0.5d,
						 pos.getY(), pos.getZ()+0.5d, 2, 0D, 0D, 0D, 0.15D, Block.getIdFromBlock(state.getBlock()));
						j += 3;
					}
					this.onSetBlock(pos);
					this.world.setBlockState(pos, state, 2);
				}
				++this.addIndex;
				++j;
			}
			if (j >= 64) {
				return;
			}
			if (flag) {
				j = 0;
				for (int i = this.removeIndex; i < this.blocksList.size() && j < 64; i++) {
					Template.BlockInfo blockinfo = this.blocksList.get(i);
					IBlockState state = this.world.getBlockState(blockinfo.pos);
					if (state.getBlock() == blockinfo.blockState.getBlock()
					 || (state.getMaterial() == blockinfo.blockState.getMaterial() && state.getMaterial().isLiquid())) {
					 //|| (state.getMaterial() == Material.WATER && blockinfo.blockState.getMaterial() == Material.WATER)
					 //|| (state.getMaterial() == Material.LAVA && blockinfo.blockState.getMaterial() == Material.LAVA)
					 //|| (state.getMaterial() == BlockMud.MUD && blockinfo.blockState.getMaterial() == BlockMud.MUD)) {
						this.onRemoveBlock(blockinfo.pos);
					}
					++this.removeIndex;
					++j;
				}
				if (j >= 64) {
					return;
				}
			}
		}
		if (this.lifespan == 0 || flag) {
			this.clear();
		}
	}

	public void onAddTick(int tick) {
	}

	public void onRemoveTick(int tick) {
	}

	public void onSetBlock(BlockPos pos) {
		Template.BlockInfo blockinfo = this.blocksList.get(this.addIndex);
		if (blockinfo.tileentityData != null) {
			blockinfo.tileentityData.setInteger("ogstate", Block.getStateId(this.world.getBlockState(pos)));
		}
	}

	public void onRemoveBlock(BlockPos pos) {
		Template.BlockInfo blockinfo = this.blocksList.get(this.removeIndex);
		if (blockinfo.tileentityData != null && blockinfo.tileentityData.hasKey("ogstate")) {
			IBlockState ogstate = Block.getStateById(blockinfo.tileentityData.getInteger("ogstate"));
			this.world.setBlockState(pos, ogstate, 2);
//System.out.println(">>>>>> og block: "+ogstate+", "+pos);
		} else {
			this.world.setBlockToAir(pos);
//System.out.println("====== no og block saved, "+pos);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("lifespan", this.lifespan);
		compound.setInteger("addIndex", this.addIndex);
		compound.setInteger("removeIndex", this.removeIndex);
		NBTTagList taglist = new NBTTagList();
		if (!this.blocksList.isEmpty()) {
			for (Template.BlockInfo blockinfo : this.blocksList) {
				NBTTagCompound newcompound = new NBTTagCompound();
				newcompound.setInteger("x", blockinfo.pos.getX());
				newcompound.setInteger("y", blockinfo.pos.getY());
				newcompound.setInteger("z", blockinfo.pos.getZ());
				newcompound.setInteger("blockstate", Block.getStateId(blockinfo.blockState));
				if (blockinfo.tileentityData != null) {
					newcompound.setTag("extraNBT", blockinfo.tileentityData);
				}
				taglist.appendTag(newcompound);
			}
		}
		if (!taglist.hasNoTags()) {
			compound.setTag("blocksList", taglist);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.lifespan = compound.getInteger("lifespan");
		this.addIndex = compound.getInteger("addIndex");
		this.removeIndex = compound.getInteger("removeIndex");
		if (compound.hasKey("blocksList", 9)) {
			NBTTagList taglist = compound.getTagList("blocksList", 10);
			for (int i = 0; i < taglist.tagCount(); ++i) {
				NBTTagCompound tagcompound = taglist.getCompoundTagAt(i);
				BlockPos pos = new BlockPos(tagcompound.getInteger("x"), tagcompound.getInteger("y"), tagcompound.getInteger("z"));
				IBlockState state = Block.getStateById(tagcompound.getInteger("blockstate"));
				NBTTagCompound extraNBT = (NBTTagCompound)tagcompound.getTag("extraNBT");
				this.blocksList.add(new Template.BlockInfo(pos, state, extraNBT != null ? extraNBT : new NBTTagCompound()));
			}
		}
	}

	@Override
	public String toString() {
		return super.toString()+" + {lifespan:"+this.lifespan+",addIndex:"+this.addIndex+",removeIndex:"+this.removeIndex
		 +",blocksCount:"+this.blocksList.size()+"}";
	}
}
