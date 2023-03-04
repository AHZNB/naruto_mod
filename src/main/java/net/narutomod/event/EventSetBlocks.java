package net.narutomod.event;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.block.SoundType;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumParticleTypes;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import java.util.Map;
import java.util.List;

public class EventSetBlocks extends SpecialEvent {
	//private Map<BlockPos, IBlockState> blocksMap;
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
			//this.blocksMap = Maps.newHashMap(map);
			for (Map.Entry<BlockPos, IBlockState> entry : map.entrySet()) {
				this.blocksList.add(new Template.BlockInfo(entry.getKey(), entry.getValue(), null));
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
		/*boolean flag2 = this.addIndex < this.blocksMap.size();
		if (this.blocksMap != null && (flag2 || flag)) {
			if (flag2) {
				this.onAddTick(this.tick);
			}
			if (flag) {
				this.onRemoveTick(this.tick);
			}
			int i = 0;
			int j = 0;
			for (Map.Entry<BlockPos, IBlockState> entry : this.blocksMap.entrySet()) {
				if (j < 64) {
					BlockPos pos = entry.getKey();
					IBlockState state = entry.getValue();
					if (i == this.addIndex) {
						if (state.getBlock() != Blocks.AIR || !this.world.isAirBlock(pos)) {
							if (this.sound && j == 0 && this.tick % 5 == 1) {
								SoundType soundtype = state.getBlock().getSoundType();
								this.world.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, 
						 		 soundtype.getVolume() * 0.5F, soundtype.getPitch());
						 		 j += 7;
						 	}
						 	if (this.particles) {
								((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST,
								 pos.getX()+0.5d, pos.getY(), pos.getZ()+0.5d, 2, 0D, 0D, 0D, 0.15D,
								 Block.getIdFromBlock(state.getBlock()));
								 j += 7;
							}
							this.onSetBlock(pos);
							this.world.setBlockState(pos, state, 2);
						}
						++this.addIndex;
						++j;
					}
					if (flag && i == this.removeIndex) {
						if (!this.world.isAirBlock(pos)) {
							this.onRemoveBlock(pos);
							this.world.setBlockToAir(pos);
						}
						++this.removeIndex;
						++j;
					}
				} else {
					return;
				}
				++i;
			}
		}*/
		boolean flag2 = this.addIndex < this.blocksList.size();
		if (this.blocksList != null && (flag2 || flag)) {
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
					BlockPos pos = blockinfo.pos;
					if (!this.world.isAirBlock(pos)) {
						this.onRemoveBlock(pos);
						this.world.setBlockToAir(pos);
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
	}

	public void onRemoveBlock(BlockPos pos) {
	}

	/*@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("lifespan", this.lifespan);
		compound.setInteger("addIndex", this.addIndex);
		compound.setInteger("removeIndex", this.removeIndex);
		NBTTagList taglist = new NBTTagList();
		if (this.blocksMap != null && !this.blocksMap.isEmpty()) {
			for (Map.Entry<BlockPos, IBlockState> entry : this.blocksMap.entrySet()) {
				NBTTagCompound newcompound = new NBTTagCompound();
				newcompound.setInteger("x", entry.getKey().getX());
				newcompound.setInteger("y", entry.getKey().getY());
				newcompound.setInteger("z", entry.getKey().getZ());
				newcompound.setInteger("blockstate", Block.getStateId(entry.getValue()));
				taglist.appendTag(newcompound);
			}
		}
		if (!taglist.hasNoTags()) {
			compound.setTag("blocksMap", taglist);
		}
	}*/

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("lifespan", this.lifespan);
		compound.setInteger("addIndex", this.addIndex);
		compound.setInteger("removeIndex", this.removeIndex);
		NBTTagList taglist = new NBTTagList();
		if (this.blocksList != null && !this.blocksList.isEmpty()) {
			for (Template.BlockInfo blockinfo : this.blocksList) {
				NBTTagCompound newcompound = new NBTTagCompound();
				newcompound.setInteger("x", blockinfo.pos.getX());
				newcompound.setInteger("y", blockinfo.pos.getY());
				newcompound.setInteger("z", blockinfo.pos.getZ());
				newcompound.setInteger("blockstate", Block.getStateId(blockinfo.blockState));
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
			//this.blocksMap = Maps.newHashMap();
			for (int i = 0; i < taglist.tagCount(); ++i) {
				NBTTagCompound tagcompound = taglist.getCompoundTagAt(i);
				BlockPos pos = new BlockPos(tagcompound.getInteger("x"), tagcompound.getInteger("y"), tagcompound.getInteger("z"));
				IBlockState state = Block.getStateById(tagcompound.getInteger("blockstate"));
				//this.blocksMap.put(pos, state);
				this.blocksList.add(new Template.BlockInfo(pos, state, null));
			}
		}
	}

	@Override
	public String toString() {
		return super.toString()+" + {lifespan:"+this.lifespan+",addIndex:"+this.addIndex+",removeIndex:"+this.removeIndex
		 +",blocksCount:"+(this.blocksList!=null?this.blocksList.size():"nul")+"}";
	}
}
