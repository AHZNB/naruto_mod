
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.block.Block;

import net.narutomod.entity.EntityEarthSpears;
import net.narutomod.entity.EntitySwampPit;
import net.narutomod.entity.EntityEarthSandwich;
import net.narutomod.entity.EntityEarthGolem;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.Chakra;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class ItemDoton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:doton")
	public static final Item block = null;
	public static final int ENTITYID = 134;
	public static final int ENTITY2ID = 10134;
	private static final List<Material> earthenMaterials = Arrays.asList(Material.GROUND, Material.ROCK, Material.SAND, Material.CLAY);
	public static final ItemJutsu.JutsuEnum HIDINGINROCK = new ItemJutsu.JutsuEnum(0, "entityhidinginrock", 'C', 10d, new EntityHidingInRock.Jutsu());
	public static final ItemJutsu.JutsuEnum EARTHWALL = new ItemJutsu.JutsuEnum(1, "entityearthwall", 'B', 20d, new EntityEarthWall.Jutsu());
	public static final ItemJutsu.JutsuEnum SANDWICH = new ItemJutsu.JutsuEnum(2, "earth_sandwich", 'B', 100d, new EntityEarthSandwich.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SWAMPPIT = new ItemJutsu.JutsuEnum(3, "swamp_pit", 'A', 100d, new EntitySwampPit.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SPEARS = new ItemJutsu.JutsuEnum(4, "earth_spears", 'C', 20d, new EntityEarthSpears.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum GOLEM = new ItemJutsu.JutsuEnum(5, "earth_golem", 'B', 100d, new EntityEarthGolem.EC.Jutsu());

	public ItemDoton(ElementsNarutomodMod instance) {
		super(instance, 378);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(HIDINGINROCK, EARTHWALL, SANDWICH, SWAMPPIT, SPEARS, GOLEM));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityEarthWall.class)
				.id(new ResourceLocation("narutomod", "entityearthwall"), ENTITYID).name("entityearthwall").tracker(64, 1, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityHidingInRock.class)
				.id(new ResourceLocation("narutomod", "entityhidinginrock"), ENTITY2ID).name("entityhidinginrock").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:doton", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.DOTON, list);
			this.setUnlocalizedName("doton");
			this.setRegistryName("doton");
			this.setCreativeTab(TabModTab.tab);
			//this.defaultCooldownMap[HIDINGINROCK.index] = 0;
			//this.defaultCooldownMap[EARTHWALL.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			float base = 2f;
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == EARTHWALL) {
				return this.getPower(stack, entity, timeLeft, base, 15f);
				//return Math.min(base + (float)(this.getMaxUseDuration() - timeLeft) / 10, this.getMaxPower(stack, entity));
			} else if (jutsu == SANDWICH) {
				return this.getPower(stack, entity, timeLeft, base, 150f);
				//return Math.min(base + (float)(this.getMaxUseDuration() - timeLeft) / 50, this.getMaxPower(stack, entity));
			} else if (jutsu == SWAMPPIT) {
				return this.getPower(stack, entity, timeLeft, 1f, 100f);
				//return MathHelper.floor(Math.min(1f + (float)(this.getMaxUseDuration() - timeLeft) / 20, this.getMaxPower(stack, entity)));
			} else if (jutsu == SPEARS) {
				return this.getPower(stack, entity, timeLeft, 0.5f, 20f);
			} else if (jutsu == GOLEM) {
				return this.getPower(stack, entity, timeLeft, 0.0f, 150f);
			}
			return base;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float f = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == EARTHWALL) {
				return Math.min(f, 50f);
			} else if (jutsu == SANDWICH) {
				return Math.min(f, 20f);
			} else if (jutsu == GOLEM) {
				return Math.min(f, 5f);
			} else if (jutsu == SWAMPPIT) {
				return Math.min(f, 30f);
			} else if (jutsu == SPEARS) {
				return Math.min(f, 100f);
			}
			return f;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
			if (this.getCurrentJutsu(stack) != HIDINGINROCK) {
				super.onUsingTick(stack, player, count);
			}
		}
	}

	private static boolean isEarthenMaterial(Material material) {
		return earthenMaterials.contains(material);
	}

	public static class EntityHidingInRock extends Entity {
		private final int waitTime = 60;
		private EntityLivingBase user;

		public EntityHidingInRock(World worldIn) {
			super(worldIn);
			this.setSize(0.01f, 0.01f);
		}

		public EntityHidingInRock(EntityLivingBase userIn) {
			this(userIn.world);
			this.user = userIn;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
		}
		
		@Override
		protected void entityInit() {
		}

		@Override
		public void setDead() {
			super.setDead();
			if (this.user != null) {
				this.user.getEntityData().removeTag(Jutsu.ID_KEY);
			}
			if (this.isUserIntangible()) {
				this.setUserIntangible(false);
			}
		}

		private boolean isUserInEarth() {
			BlockPos pos = new BlockPos(this.user);
			return isEarthenMaterial(this.world.getBlockState(pos).getMaterial())
			 || isEarthenMaterial(this.world.getBlockState(pos.up()).getMaterial());
		}

		private boolean isUserIntangible() {
			return this.user != null && ProcedureOnLivingUpdate.isNoClip(this.user);
		}

		private void setUserIntangible(boolean intangibleIn) {
			ProcedureOnLivingUpdate.setNoClip(this.user, intangibleIn);
			if (this.user instanceof EntityPlayer && !this.world.isRemote) {
				String string = net.minecraft.util.text.translation.I18n.translateToLocal("chattext.intangible");
				((EntityPlayer)this.user).sendStatusMessage(new TextComponentString(string + intangibleIn), true);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.user != null && this.user.isEntityAlive()) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				boolean flag = this.ticksExisted % 20 != 0;
				boolean flag1 = this.user instanceof EntityPlayer && (flag 
				 || Chakra.pathway((EntityPlayer)this.user).getAmount() >= HIDINGINROCK.chakraUsage);
				if (this.ticksExisted > this.waitTime && !this.isUserInEarth() || !flag1) {
					this.setDead();
				} else {
					this.setUserIntangible(true);
					if (!flag && flag1) {
						Chakra.pathway((EntityPlayer)this.user).consume(HIDINGINROCK.chakraUsage);
					}
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "HidingInRockIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (!ProcedureOnLivingUpdate.isNoClip(entity)) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
					 .getObject(new ResourceLocation("narutomod:jutsu")), SoundCategory.NEUTRAL, 1, 1f);
					entity.world.spawnEntity(new EntityHidingInRock(entity));
					entity.getEntityData().setBoolean(ID_KEY, true);
					return true;
				}
				return false;
			}

			@Override
			public boolean isActivated(EntityLivingBase entity) {
				return entity.getEntityData().getBoolean(ID_KEY);
			}
		}
	}

	public static class EntityEarthWall extends Entity {
		private final int blockChunk = 128;
		private final int duration = 200;
		private double wallHeight;
		private List<BlockPos> affectedList;
		private List<BlockPos> tempList;
		private final List<BlockPos> allBlocks;
		private boolean dieOnDone;
		private int vindex;
		private int removeTime;
		
		public EntityEarthWall(World a) {
			super(a);
			this.setSize(0.01f, 0.01f);
			this.affectedList = Lists.<BlockPos>newArrayList();
			this.allBlocks = Lists.<BlockPos>newArrayList();
			this.dieOnDone = true;
		}

		// x, y, z is the center point of the wall
		// yaw is the rotation angle the wall is facing
		// set autoIn to true to self kill after wall building finished
		public EntityEarthWall(World worldIn, double x, double y, double z, float yaw, double widthIn, double heightIn, double thickness, boolean autoIn) {
			this(worldIn);
			this.dieOnDone = autoIn;
			this.wallHeight = heightIn;
			if (thickness < 1d)
				thickness = 1d;
			thickness = (thickness - 1d) / 2d;
			widthIn /= 2d;
			this.setPosition(x, y, z);
			Vec3d vec = new Vec3d(x, y, z);
			Vec3d vec3d = vec.add(Vec3d.fromPitchYaw(0f, yaw - 90f).scale(widthIn));
			Vec3d vec3d1 = vec.add(Vec3d.fromPitchYaw(0f, yaw + 90f).scale(widthIn));
			for (AxisAlignedBB aabb : this.world.getCollisionBoxes(null, this.getEntityBoundingBox().grow(widthIn, this.wallHeight, widthIn))) {
				BlockPos pos = new BlockPos(ProcedureUtils.BB.getCenter(aabb));
				if (autoIn) {
					RayTraceResult r = aabb.grow(thickness, this.wallHeight, thickness).calculateIntercept(vec3d, vec3d1);
					if (r != null && this.isNeighborEarthenMaterial(pos) && this.world.isAirBlock(pos.up())) {
						this.affectedList.add(pos);
					}
				} else {
					RayTraceResult r = aabb.grow(thickness, 0d, thickness).expand(0d, this.wallHeight, 0d).calculateIntercept(vec3d, vec3d1);
					if (r != null && this.isNeighborEarthenMaterial(pos) && (aabb.maxY == y || !this.world.getBlockState(pos.up()).isTopSolid())) {
						this.affectedList.add(pos);
					}
				}
			}
			this.tempList = Lists.newArrayList();
		}

		public EntityEarthWall(World worldIn, double x, double y, double z, float yaw, double widthIn) {
			this(worldIn, x, y, z, yaw, widthIn, widthIn * 0.6d, widthIn * 0.25d, true);
		}

		private boolean isNeighborEarthenMaterial(BlockPos pos) {
			return this.getNeightborEarthenBlock(pos).getBlock() != Blocks.AIR;
		}

		private IBlockState getNeightborEarthenBlock(BlockPos pos) {
			IBlockState bstate = Blocks.AIR.getDefaultState();
			if (isEarthenMaterial(this.world.getBlockState(pos).getMaterial())) {
				bstate = this.world.getBlockState(pos);
			} else for (EnumFacing face : EnumFacing.values()) {
				if (bstate.getBlock() == Blocks.AIR && isEarthenMaterial(this.world.getBlockState(pos.offset(face)).getMaterial()))
					bstate = this.world.getBlockState(pos.offset(face));
			}
			if (bstate.getBlock() instanceof BlockOre || bstate.getBlock() instanceof BlockRedstoneOre || bstate.getBlock() == Blocks.BEDROCK) {
				bstate = Blocks.STONE.getDefaultState();
			}
			return bstate;
		}

		@Override
		protected void entityInit() {
		}

		private void moveUpEntitiesInAABB(AxisAlignedBB aabb, double offset) {
			for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(null, aabb)) {
				entity.setPositionAndUpdate(entity.posX, entity.posY + offset + 1.5d, entity.posZ);
			}
		}

		@Override
		public void onUpdate() {
			if (!this.affectedList.isEmpty()) {
				if (this.ticksExisted % 30 == 1) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:rocks"))), 
					 5.0f, (this.rand.nextFloat() * 0.5f) + 0.3f);
				}
				BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
				for (int i = 0; i < this.blockChunk && this.vindex < (int)this.wallHeight; ) {
					Iterator<BlockPos> iter = this.affectedList.iterator();
					while (i < this.blockChunk && iter.hasNext()) {
						pos.setPos(iter.next().up());
						iter.remove();
						this.tempList.add(pos.toImmutable());
						if (pos.getY() < 255 && this.world.isAirBlock(pos)) {
							this.moveUpEntitiesInAABB(new AxisAlignedBB(pos), 1d);
							IBlockState state = this.getNeightborEarthenBlock(pos.down());
							((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST,
							 pos.getX()+0.5d, pos.getY(), pos.getZ()+0.5d, 5, 0D, 0D, 0D, 0.15D,
							 Block.getIdFromBlock(state.getBlock()));
							this.world.setBlockState(pos, state, 3);
							this.allBlocks.add(pos.toImmutable());
							++i;
						}
					}
					if (this.affectedList.isEmpty()) {
						this.affectedList = Lists.newArrayList(this.tempList);
						//this.allBlocks.addAll(this.tempList);
						this.tempList.clear();
						++this.vindex;
					}
				}
				pos.release();
				if (this.vindex >= (int)this.wallHeight) {
					this.affectedList.clear();
					//this.removeTime = this.ticksExisted + 1200;
				}
			} else if (!this.world.isRemote && this.dieOnDone) {
				this.removeTime = 1200;
				this.setDead();
			}
			/*if (!this.affectedList.isEmpty()) {
				if (this.ticksExisted <= (int)this.wallHeight) {
					//List<BlockPos> list = Lists.<BlockPos>newArrayList();
					Map<BlockPos, IBlockState> map = Maps.newHashMap();
					Iterator<BlockPos> iter = this.affectedList.iterator();
					while (iter.hasNext()) {
						BlockPos pos = iter.next();
						iter.remove();
						if (!this.world.isAirBlock(pos.up())) {
							if (!this.isBlockBreakable(pos.up())) {
								continue;
							}
							ProcedureUtils.breakBlockAndDropWithChance(this.world, pos.up(), 5f, 1f, 0.3f, false);
						} else {
							this.moveUpEntitiesInAABB(new AxisAlignedBB(pos.up()), 1d);
						}
						IBlockState state = this.getNeightborEarthenBlock(pos);
						//this.world.playSound(null, pos, state.getBlock().getSoundType().getBreakSound(), SoundCategory.BLOCKS, 
						// 1.0f, (this.rand.nextFloat() * 0.5f) + 0.4f);
						//this.world.setBlockState(pos.up(), state, 3);
						map.put(pos.up(), state);
						//list.add(pos.up());
					}
					new net.narutomod.event.EventSetBlocks(this.world, map, 0, 
					 this.duration + (int)this.wallHeight - this.ticksExisted);
					this.affectedList = Lists.newArrayList(map.keySet());
					this.allBlocks.addAll(this.affectedList);
				}
			}
			if (!this.world.isRemote && this.ticksExisted > this.duration + (int)this.wallHeight && this.auto) {
				this.setDead();
			}*/
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && !this.allBlocks.isEmpty()) {
				//this.allBlocks.sort(new ProcedureUtils.BlockposSorter(new BlockPos(this).up((int)this.wallHeight+1)));
				Map<BlockPos, IBlockState> map = Maps.newHashMap();
				for (BlockPos pos : this.allBlocks) {
					map.put(pos, Blocks.AIR.getDefaultState());
				}
				this.allBlocks.clear();
				new net.narutomod.event.EventSetBlocks(this.world, map, this.world.getTotalWorldTime() + this.removeTime, 0, false, false);
			}
		}

		public boolean isBlockBreakable(BlockPos pos) {
			IBlockState blockstate = this.world.getBlockState(pos);
			float hardness = blockstate.getBlockHardness(this.world, pos);
			return !blockstate.isTopSolid() && (blockstate.getMaterial().isLiquid() || (hardness >= 0f && hardness <= 5.0f));
		}

		public boolean isDone() {
			return !this.world.isRemote && (this.affectedList.isEmpty() || this.vindex > (int)this.wallHeight);
		}

		public List<BlockPos> getAllBlocks() {
			return this.allBlocks;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 5f) {
					RayTraceResult rt = ProcedureUtils.raytraceBlocks(entity, 30d);
					if (rt != null && rt.typeOfHit == RayTraceResult.Type.BLOCK) {
						entity.world.spawnEntity(new EntityEarthWall(
						  entity.world, rt.hitVec.x, rt.hitVec.y, rt.hitVec.z, entity.rotationYaw, (double)power));
						return true;
					}
				}
				return false;
			}
		}
	}
}
