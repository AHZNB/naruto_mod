package net.narutomod.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockFire;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.potion.PotionEffect;

import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class BlockAmaterasuBlock extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:amaterasublock")
	public static final Block block = null;
	public static final Material AMATERASU = new MaterialImmortalFire();
	private static final int AGING_DELAY = ModConfig.AMATERASU_BLOCK_DURATION;

	public BlockAmaterasuBlock(ElementsNarutomodMod instance) {
		super(instance, 269);
	}

	public void initElements() {
		this.elements.blocks.add(() -> new BlockCustom().setRegistryName("amaterasublock"));
		this.elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TileEntityCustom.class, "narutomod:tileentityamaterasublock");
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(final ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
				new ModelResourceLocation("narutomod:amaterasublock", "inventory"));
	}

	public static void placeBlock(World world, BlockPos pos, int strength) {
		if (world.isAirBlock(pos)) {
			world.setBlockState(pos, block.getDefaultState(), 3);
			BlockCustom.setLevel(world, pos, strength);
		}
	}

	public static class BlockCustom extends BlockFire implements ITileEntityProvider {
		private final Material altMaterial;
		private BlockPos blockPos;
		private World world;
		private int burnLevel;

		public BlockCustom() {
			super();
			// this.setRegistryName("amaterasublock");
			this.setUnlocalizedName("amaterasublock");
			this.setHardness(100.0f);
			this.setLightLevel(1.0f);
			this.setBlockUnbreakable();
			this.setMoreFireInfo();
			this.altMaterial = AMATERASU;
			this.blockPos = null;
			this.burnLevel = 0;
		}

		private void setMoreFireInfo() {
			this.setFireInfo(Blocks.PLANKS, 5, 30);
			this.setFireInfo(Blocks.DOUBLE_WOODEN_SLAB, 5, 30);
			this.setFireInfo(Blocks.WOODEN_SLAB, 5, 30);
			this.setFireInfo(Blocks.OAK_FENCE_GATE, 5, 30);
			this.setFireInfo(Blocks.SPRUCE_FENCE_GATE, 5, 30);
			this.setFireInfo(Blocks.BIRCH_FENCE_GATE, 5, 30);
			this.setFireInfo(Blocks.JUNGLE_FENCE_GATE, 5, 30);
			this.setFireInfo(Blocks.DARK_OAK_FENCE_GATE, 5, 30);
			this.setFireInfo(Blocks.ACACIA_FENCE_GATE, 5, 30);
			this.setFireInfo(Blocks.OAK_FENCE, 5, 30);
			this.setFireInfo(Blocks.SPRUCE_FENCE, 5, 30);
			this.setFireInfo(Blocks.BIRCH_FENCE, 5, 30);
			this.setFireInfo(Blocks.JUNGLE_FENCE, 5, 30);
			this.setFireInfo(Blocks.DARK_OAK_FENCE, 5, 30);
			this.setFireInfo(Blocks.ACACIA_FENCE, 5, 30);
			this.setFireInfo(Blocks.OAK_STAIRS, 5, 30);
			this.setFireInfo(Blocks.BIRCH_STAIRS, 5, 30);
			this.setFireInfo(Blocks.SPRUCE_STAIRS, 5, 30);
			this.setFireInfo(Blocks.JUNGLE_STAIRS, 5, 30);
			this.setFireInfo(Blocks.ACACIA_STAIRS, 5, 30);
			this.setFireInfo(Blocks.DARK_OAK_STAIRS, 5, 30);
			this.setFireInfo(Blocks.LOG, 5, 10);
			this.setFireInfo(Blocks.LOG2, 5, 10);
			this.setFireInfo(Blocks.LEAVES, 30, 90);
			this.setFireInfo(Blocks.LEAVES2, 30, 90);
			this.setFireInfo(Blocks.BOOKSHELF, 30, 30);
			this.setFireInfo(Blocks.TNT, 15, 150);
			this.setFireInfo(Blocks.TALLGRASS, 60, 150);
			this.setFireInfo(Blocks.DOUBLE_PLANT, 60, 150);
			this.setFireInfo(Blocks.YELLOW_FLOWER, 60, 150);
			this.setFireInfo(Blocks.RED_FLOWER, 60, 150);
			this.setFireInfo(Blocks.DEADBUSH, 60, 150);
			this.setFireInfo(Blocks.WOOL, 30, 90);
			this.setFireInfo(Blocks.VINE, 15, 150);
			this.setFireInfo(Blocks.COAL_BLOCK, 5, 10);
			this.setFireInfo(Blocks.HAY_BLOCK, 60, 30);
			this.setFireInfo(Blocks.CARPET, 60, 30);
			this.setFireInfo(Blocks.ACACIA_DOOR, 5, 30);
			this.setFireInfo(Blocks.BIRCH_DOOR, 5, 30);
			this.setFireInfo(Blocks.DARK_OAK_DOOR, 5, 30);
			this.setFireInfo(Blocks.JUNGLE_DOOR, 5, 30);
			this.setFireInfo(Blocks.OAK_DOOR, 5, 30);
			this.setFireInfo(Blocks.SPRUCE_DOOR, 5, 30);
			this.setFireInfo(Blocks.ACTIVATOR_RAIL, 1, 5);
			this.setFireInfo(Blocks.BED, 5, 20);
			this.setFireInfo(Blocks.BLACK_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.BLACK_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.BONE_BLOCK, 1, 5);
			this.setFireInfo(Blocks.BOOKSHELF, 5, 10);
			this.setFireInfo(Blocks.BREWING_STAND, 1, 5);
			this.setFireInfo(Blocks.BRICK_BLOCK, 1, 5);
			this.setFireInfo(Blocks.BRICK_STAIRS, 1, 5);
			this.setFireInfo(Blocks.BROWN_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.BROWN_MUSHROOM, 5, 20);
			this.setFireInfo(Blocks.BROWN_MUSHROOM_BLOCK, 5, 20);
			this.setFireInfo(Blocks.BROWN_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.CACTUS, 5, 20);
			this.setFireInfo(Blocks.CAKE, 5, 20);
			this.setFireInfo(Blocks.CARROTS, 0, 20);
			this.setFireInfo(Blocks.CHEST, 2, 20);
			this.setFireInfo(Blocks.CHORUS_FLOWER, 5, 20);
			this.setFireInfo(Blocks.CHORUS_PLANT, 5, 20);
			this.setFireInfo(Blocks.CLAY, 1, 5);
			this.setFireInfo(Blocks.COAL_BLOCK, 1, 5);
			this.setFireInfo(Blocks.COAL_ORE, 0, 5);
			this.setFireInfo(Blocks.COBBLESTONE, 1, 5);
			this.setFireInfo(Blocks.COCOA, 5, 20);
			this.setFireInfo(Blocks.CONCRETE, 1, 5);
			this.setFireInfo(Blocks.CRAFTING_TABLE, 4, 20);
			this.setFireInfo(Blocks.CYAN_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.CYAN_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.DAYLIGHT_DETECTOR, 1, 5);
			this.setFireInfo(Blocks.DAYLIGHT_DETECTOR_INVERTED, 1, 5);
			this.setFireInfo(Blocks.DETECTOR_RAIL, 1, 5);
			this.setFireInfo(Blocks.DIAMOND_BLOCK, 1, 5);
			this.setFireInfo(Blocks.DIAMOND_ORE, 0, 5);
			this.setFireInfo(Blocks.DIRT, 1, 5);
			this.setFireInfo(Blocks.DISPENSER, 1, 5);
			this.setFireInfo(Blocks.DOUBLE_STONE_SLAB, 1, 5);
			this.setFireInfo(Blocks.DOUBLE_STONE_SLAB2, 1, 5);
			this.setFireInfo(Blocks.DRAGON_EGG, 0, 5);
			this.setFireInfo(Blocks.DROPPER, 1, 5);
			this.setFireInfo(Blocks.EMERALD_BLOCK, 1, 5);
			this.setFireInfo(Blocks.EMERALD_ORE, 0, 5);
			this.setFireInfo(Blocks.ENCHANTING_TABLE, 2, 5);
			this.setFireInfo(Blocks.END_BRICKS, 1, 5);
			this.setFireInfo(Blocks.END_GATEWAY, 0, 2);
			this.setFireInfo(Blocks.END_ROD, 4, 10);
			this.setFireInfo(Blocks.END_STONE, 1, 5);
			this.setFireInfo(Blocks.ENDER_CHEST, 5, 20);
			this.setFireInfo(Blocks.FARMLAND, 1, 5);
			this.setFireInfo(Blocks.FLOWER_POT, 1, 5);
			this.setFireInfo(Blocks.FROSTED_ICE, 1, 5);
			this.setFireInfo(Blocks.FURNACE, 1, 5);
			this.setFireInfo(Blocks.GLASS, 1, 5);
			this.setFireInfo(Blocks.GLASS_PANE, 1, 5);
			this.setFireInfo(Blocks.GLOWSTONE, 1, 5);
			this.setFireInfo(Blocks.GOLD_BLOCK, 1, 5);
			this.setFireInfo(Blocks.GOLD_ORE, 0, 5);
			this.setFireInfo(Blocks.GOLDEN_RAIL, 1, 20);
			this.setFireInfo(Blocks.GRASS, 4, 20);
			this.setFireInfo(Blocks.GRASS_PATH, 1, 5);
			this.setFireInfo(Blocks.GRAVEL, 1, 5);
			this.setFireInfo(Blocks.GRAY_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.GRAY_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.GREEN_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.GREEN_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.HARDENED_CLAY, 1, 5);
			this.setFireInfo(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 0, 5);
			this.setFireInfo(Blocks.HOPPER, 0, 5);
			this.setFireInfo(Blocks.ICE, 1, 5);
			this.setFireInfo(Blocks.IRON_BARS, 1, 5);
			this.setFireInfo(Blocks.IRON_BLOCK, 1, 5);
			this.setFireInfo(Blocks.IRON_DOOR, 1, 5);
			this.setFireInfo(Blocks.IRON_ORE, 0, 5);
			this.setFireInfo(Blocks.IRON_TRAPDOOR, 1, 5);
			this.setFireInfo(Blocks.JUKEBOX, 4, 20);
			this.setFireInfo(Blocks.LADDER, 4, 20);
			this.setFireInfo(Blocks.LAPIS_BLOCK, 1, 5);
			this.setFireInfo(Blocks.LAPIS_ORE, 0, 5);
			this.setFireInfo(Blocks.LEVER, 4, 20);
			this.setFireInfo(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.LIGHT_BLUE_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 0, 5);
			this.setFireInfo(Blocks.LIME_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.LIME_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.LIT_FURNACE, 4, 5);
			this.setFireInfo(Blocks.LIT_PUMPKIN, 4, 20);
			this.setFireInfo(Blocks.LIT_REDSTONE_LAMP, 1, 5);
			this.setFireInfo(Blocks.LIT_REDSTONE_ORE, 0, 5);
			this.setFireInfo(Blocks.MAGENTA_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.MAGENTA_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.MAGMA, 1, 5);
			this.setFireInfo(Blocks.MELON_BLOCK, 4, 20);
			this.setFireInfo(Blocks.MELON_STEM, 4, 20);
			this.setFireInfo(Blocks.MOB_SPAWNER, 1, 5);
			this.setFireInfo(Blocks.MONSTER_EGG, 1, 5);
			this.setFireInfo(Blocks.MOSSY_COBBLESTONE, 1, 5);
			this.setFireInfo(Blocks.MYCELIUM, 1, 5);
			this.setFireInfo(Blocks.NETHER_BRICK, 1, 5);
			this.setFireInfo(Blocks.NETHER_BRICK_FENCE, 1, 5);
			this.setFireInfo(Blocks.NETHER_BRICK_STAIRS, 1, 5);
			this.setFireInfo(Blocks.NETHER_WART, 1, 5);
			this.setFireInfo(Blocks.NETHER_WART_BLOCK, 1, 5);
			this.setFireInfo(Blocks.NETHERRACK, 1, 5);
			this.setFireInfo(Blocks.NOTEBLOCK, 4, 5);
			this.setFireInfo(Blocks.OBSIDIAN, 0, 3);
			this.setFireInfo(Blocks.ORANGE_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.ORANGE_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.PACKED_ICE, 1, 5);
			this.setFireInfo(Blocks.PINK_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.PINK_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.PISTON, 1, 5);
			this.setFireInfo(Blocks.PISTON_EXTENSION, 1, 5);
			this.setFireInfo(Blocks.PISTON_HEAD, 1, 5);
			this.setFireInfo(Blocks.POTATOES, 5, 20);
			this.setFireInfo(Blocks.POWERED_COMPARATOR, 1, 5);
			this.setFireInfo(Blocks.POWERED_REPEATER, 1, 5);
			this.setFireInfo(Blocks.PRISMARINE, 1, 5);
			this.setFireInfo(Blocks.PUMPKIN, 5, 20);
			this.setFireInfo(Blocks.PUMPKIN_STEM, 5, 20);
			this.setFireInfo(Blocks.PURPLE_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.PURPLE_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.PURPUR_BLOCK, 1, 5);
			this.setFireInfo(Blocks.PURPUR_DOUBLE_SLAB, 1, 5);
			this.setFireInfo(Blocks.PURPUR_PILLAR, 1, 5);
			this.setFireInfo(Blocks.PURPUR_SLAB, 1, 5);
			this.setFireInfo(Blocks.PURPUR_STAIRS, 1, 5);
			this.setFireInfo(Blocks.QUARTZ_BLOCK, 1, 5);
			this.setFireInfo(Blocks.QUARTZ_ORE, 0, 5);
			this.setFireInfo(Blocks.QUARTZ_STAIRS, 1, 5);
			this.setFireInfo(Blocks.RAIL, 1, 5);
			this.setFireInfo(Blocks.RED_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.RED_MUSHROOM, 5, 20);
			this.setFireInfo(Blocks.RED_MUSHROOM_BLOCK, 5, 20);
			this.setFireInfo(Blocks.RED_NETHER_BRICK, 1, 5);
			this.setFireInfo(Blocks.RED_SANDSTONE, 1, 5);
			this.setFireInfo(Blocks.RED_SANDSTONE_STAIRS, 1, 5);
			this.setFireInfo(Blocks.RED_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.REDSTONE_BLOCK, 1, 5);
			this.setFireInfo(Blocks.REDSTONE_LAMP, 1, 5);
			this.setFireInfo(Blocks.REDSTONE_ORE, 0, 5);
			this.setFireInfo(Blocks.REDSTONE_TORCH, 2, 20);
			this.setFireInfo(Blocks.REDSTONE_WIRE, 1, 5);
			this.setFireInfo(Blocks.REEDS, 2, 5);
			this.setFireInfo(Blocks.SAND, 1, 5);
			this.setFireInfo(Blocks.SANDSTONE, 1, 5);
			this.setFireInfo(Blocks.SANDSTONE_STAIRS, 1, 5);
			this.setFireInfo(Blocks.SAPLING, 5, 20);
			this.setFireInfo(Blocks.SEA_LANTERN, 2, 5);
			this.setFireInfo(Blocks.SILVER_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.SILVER_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.SKULL, 2, 5);
			this.setFireInfo(Blocks.SLIME_BLOCK, 1, 5);
			this.setFireInfo(Blocks.SNOW, 1, 5);
			this.setFireInfo(Blocks.SNOW_LAYER, 1, 5);
			this.setFireInfo(Blocks.SOUL_SAND, 1, 5);
			this.setFireInfo(Blocks.SPONGE, 2, 5);
			this.setFireInfo(Blocks.STAINED_GLASS, 1, 5);
			this.setFireInfo(Blocks.STAINED_GLASS_PANE, 1, 5);
			this.setFireInfo(Blocks.STAINED_HARDENED_CLAY, 1, 5);
			this.setFireInfo(Blocks.STANDING_BANNER, 5, 20);
			this.setFireInfo(Blocks.STANDING_SIGN, 5, 20);
			this.setFireInfo(Blocks.STICKY_PISTON, 1, 5);
			this.setFireInfo(Blocks.STONE, 1, 5);
			this.setFireInfo(Blocks.STONE_BRICK_STAIRS, 1, 5);
			this.setFireInfo(Blocks.STONE_BUTTON, 2, 5);
			this.setFireInfo(Blocks.STONE_PRESSURE_PLATE, 2, 5);
			this.setFireInfo(Blocks.STONE_SLAB, 1, 5);
			this.setFireInfo(Blocks.STONE_SLAB2, 1, 5);
			this.setFireInfo(Blocks.STONE_STAIRS, 1, 5);
			this.setFireInfo(Blocks.STONEBRICK, 1, 5);
			this.setFireInfo(Blocks.TORCH, 5, 20);
			this.setFireInfo(Blocks.TRAPDOOR, 5, 20);
			this.setFireInfo(Blocks.TRAPPED_CHEST, 4, 20);
			this.setFireInfo(Blocks.TRIPWIRE, 2, 5);
			this.setFireInfo(Blocks.TRIPWIRE_HOOK, 2, 5);
			this.setFireInfo(Blocks.UNLIT_REDSTONE_TORCH, 5, 20);
			this.setFireInfo(Blocks.UNPOWERED_COMPARATOR, 1, 5);
			this.setFireInfo(Blocks.UNPOWERED_REPEATER, 1, 5);
			this.setFireInfo(Blocks.VINE, 5, 20);
			this.setFireInfo(Blocks.WALL_BANNER, 5, 20);
			this.setFireInfo(Blocks.WALL_SIGN, 5, 20);
			this.setFireInfo(Blocks.WATERLILY, 5, 20);
			this.setFireInfo(Blocks.WEB, 5, 20);
			this.setFireInfo(Blocks.WHEAT, 60, 150);
			this.setFireInfo(Blocks.WHITE_GLAZED_TERRACOTTA, 1, 5);
			this.setFireInfo(Blocks.WHITE_SHULKER_BOX, 4, 10);
			this.setFireInfo(Blocks.WOODEN_BUTTON, 5, 20);
			this.setFireInfo(Blocks.WOODEN_PRESSURE_PLATE, 5, 20);
			this.setFireInfo(Blocks.WOODEN_SLAB, 5, 20);
			this.setFireInfo(Blocks.YELLOW_GLAZED_TERRACOTTA, 0, 5);
			this.setFireInfo(Blocks.YELLOW_SHULKER_BOX, 4, 10);
		}

		@Override
		public Material getMaterial(IBlockState state) {
			if (this.blockPos != null && this.world.isRemote) {
				World world = this.world;
				if (world.getBlockState(this.blockPos.up()).getBlock().getBlockHardness(state, world, this.blockPos) != 100.0f
				 && world.getBlockState(this.blockPos.east()).getBlock().getBlockHardness(state, world, this.blockPos) != 100.0f
				 && world.getBlockState(this.blockPos.west()).getBlock().getBlockHardness(state, world, this.blockPos) != 100.0f
				 && world.getBlockState(this.blockPos.north()).getBlock().getBlockHardness(state, world, this.blockPos) != 100.0f
				 && world.getBlockState(this.blockPos.south()).getBlock().getBlockHardness(state, world, this.blockPos) != 100.0f) {
					return super.getMaterial(state);
				}
			}
			return this.altMaterial;
		}

		@Override
		public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
			return false;
		}

		@Override
		public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
			if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP)
					&& !this.canCatchFire(worldIn, pos.down(), EnumFacing.UP)) {
				return state.withProperty(NORTH, this.canCatchFire(worldIn, pos.north(), EnumFacing.SOUTH))
						.withProperty(EAST, this.canCatchFire(worldIn, pos.east(), EnumFacing.WEST))
						.withProperty(SOUTH, this.canCatchFire(worldIn, pos.south(), EnumFacing.NORTH))
						.withProperty(WEST, this.canCatchFire(worldIn, pos.west(), EnumFacing.EAST))
						.withProperty(UPPER, this.canCatchFire(worldIn, pos.up(), EnumFacing.DOWN));
			}
			return this.getDefaultState();
		}

		@Override
		public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
			if (!worldIn.isAreaLoaded(pos, 2)) {
				return;
			}
			if (!this.canPlaceBlockAt(worldIn, pos)) {
				worldIn.setBlockToAir(pos);
			}
			if (!pos.equals(this.blockPos)) {
				this.blockPos = pos;
				this.world = worldIn;
			}
			//Block block = worldIn.getBlockState(pos.down()).getBlock();
			//boolean flag = block.isFireSource(worldIn, pos.down(), EnumFacing.UP);
			int i = ((Integer) state.getValue(AGE)).intValue();
			if (i < 15) {
				state = state.withProperty(AGE, Integer.valueOf(i + rand.nextInt(AGING_DELAY+1) / AGING_DELAY));
				worldIn.setBlockState(pos, state, 4);
			}
			worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(10));
			/*if (!flag) {
				if (!this.canNeighborCatchFire(worldIn, pos)) {
					if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP)) {
						worldIn.setBlockToAir(pos);
					}
					return;
				}
				if (!this.canCatchFire(worldIn, pos.down(), EnumFacing.UP) && i >= 15) {
					worldIn.setBlockToAir(pos);
					return;
				}
			}*/
			//if (!this.canCatchFire(worldIn, pos.down(), EnumFacing.UP) && i >= 15) {
			if (!worldIn.getBlockState(pos.down()).getBlock().isFireSource(worldIn, pos.down(), EnumFacing.UP) && i >= 15) {
				worldIn.setBlockToAir(pos);
				return;
			}
			if (!worldIn.getGameRules().getBoolean("doFireTick")) {
				return;
			}
			this.tryCatchFire(worldIn, pos, pos.east(), 500, rand, i, EnumFacing.WEST);
			this.tryCatchFire(worldIn, pos, pos.west(), 500, rand, i, EnumFacing.EAST);
			this.tryCatchFire(worldIn, pos, pos.up(), 450, rand, i, EnumFacing.DOWN);
			this.tryCatchFire(worldIn, pos, pos.north(), 500, rand, i, EnumFacing.SOUTH);
			this.tryCatchFire(worldIn, pos, pos.south(), 500, rand, i, EnumFacing.NORTH);
			for (int k = -1; k <= 1; ++k) {
				for (int l = -1; l <= 1; ++l) {
					for (int i2 = 0; i2 <= 4; ++i2) {
						if (k != 0 || i2 != 0 || l != 0) {
							int j1 = 300;
							if (i2 > 1) {
								j1 += (i2 - 1) * 100;
							}
							BlockPos blockpos = pos.add(k, i2, l);
							int k2 = this.getNeighborEncouragement(worldIn, blockpos);
							if (k2 > 0) {
								int l2 = (k2 + 40 + worldIn.getDifficulty().getDifficultyId() * 7) / (i + 30);
								if (l2 > 0 && rand.nextInt(j1) <= l2 && !this.canDie(worldIn, blockpos)) {
									int i3 = i + rand.nextInt(AGING_DELAY * 2 + 1) / (AGING_DELAY * 2);
									if (i3 > 15) {
										i3 = 15;
									}
									worldIn.setBlockState(blockpos, state.withProperty(AGE, Integer.valueOf(i3)), 3);
									setLevel(worldIn, blockpos, this.getLevel(worldIn, pos));
								}
							}
						}
					}
				}
			}
		}

		@Override
		protected boolean canDie(World worldIn, BlockPos pos) {
			return false;
		}

		private void tryCatchFire(World worldIn, BlockPos ogPos, BlockPos atPos, int chance, Random random, int age, EnumFacing face) {
			if (random.nextInt(chance) < this.getFlammability(worldIn.getBlockState(atPos).getBlock())) {
				IBlockState iblockstate = worldIn.getBlockState(atPos);
				int j = age + random.nextInt(AGING_DELAY+1) / AGING_DELAY;
				if (j > 15) j = 15;
				worldIn.setBlockState(atPos, this.getDefaultState().withProperty(AGE, Integer.valueOf(j)), 3);
				setLevel(worldIn, atPos, this.getLevel(worldIn, ogPos));
				if (iblockstate.getBlock() == Blocks.TNT) {
					Blocks.TNT.onBlockDestroyedByPlayer(worldIn, atPos, iblockstate.withProperty(BlockTNT.EXPLODE, Boolean.valueOf(true)));
				}
			}
		}

		private boolean canNeighborCatchFire(World worldIn, BlockPos pos) {
			for (final EnumFacing enumfacing : EnumFacing.values()) {
				if (this.canCatchFire(worldIn, pos.offset(enumfacing), enumfacing.getOpposite())) {
					return true;
				}
			}
			return false;
		}

		private int getNeighborEncouragement(World worldIn, BlockPos pos) {
			if (!worldIn.isAirBlock(pos)) {
				return 0;
			}
			int i = 0;
			for (EnumFacing enumfacing : EnumFacing.values()) {
				i = Math.max(this.getEncouragement(worldIn.getBlockState(pos.offset(enumfacing)).getBlock()), i);
			}
			return i;
		}

		@Override
		public int getEncouragement(Block blockIn) {
			Integer integer = super.getEncouragement(blockIn);
			return (integer != null)
					? integer
					: (blockIn.getBlockHardness(null, null, null) >= 0.0f && blockIn.getBlockHardness(null, null, null) < 100.0f) ? 5 : 0;
		}

		@Override
		public boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face) {
			return this.getEncouragement(world.getBlockState(pos).getBlock()) > 0;
		}

		@Override
		public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
			if (!worldIn.isRemote && entityIn instanceof EntityLivingBase) {
				int amp = this.getLevel(worldIn, pos);
				((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, 10000, amp, false, false));
			}
			entityIn.setFire(500);
		}

		@Override
		public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
			this.onEntityWalk(worldIn, pos, entityIn);
		}

		@Override
		public TileEntity createNewTileEntity(World worldIn, int meta) {
			return new TileEntityCustom();
		}

		public static void setLevel(World worldIn, BlockPos pos, int level) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof TileEntityCustom) {
				((TileEntityCustom)tileEntity).setLevel(level);
			}
		}

		public int getLevel(World worldIn, BlockPos pos) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			return tileEntity instanceof TileEntityCustom ? ((TileEntityCustom)tileEntity).getLevel() : 0;
		}
	}

	public static class TileEntityCustom extends TileEntity {
		private int level;

		public void setLevel(int l) {
			this.level = l;
			this.getTileData().setInteger("amaterasuLevel", l);
		}

		public int getLevel() {
			if (this.level == 0) {
				this.level = this.getTileData().getInteger("amaterasuLevel");
			}
			return this.level;
		}
	}

	public static class MaterialImmortalFire extends Material {
		public MaterialImmortalFire() {
			super(MapColor.AIR);
		}

		@Override
		public boolean isSolid() {
			return true;
		}

		@Override
		public boolean blocksLight() {
			return false;
		}

		@Override
		public boolean blocksMovement() {
			return true;
		}

		@Override
		public EnumPushReaction getMobilityFlag() {
			return EnumPushReaction.DESTROY;
		}
	}
}
