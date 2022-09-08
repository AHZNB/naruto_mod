
package net.narutomod.world;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.DimensionManager;

import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.World;
import net.minecraft.world.DimensionType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReportedException;
import net.minecraft.init.Blocks;
import net.minecraft.init.Biomes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.CrashReport;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockFalling;

import net.narutomod.procedure.ProcedureKamuiDimensionPlayerEntersDimension;
import net.narutomod.block.BlockKamuiBlock;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.List;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class WorldKamuiDimension extends ElementsNarutomodMod.ModElement {
	public static int DIMID = 3;
	public static final boolean NETHER_TYPE = false;
	public static DimensionType dtype;
	
	public WorldKamuiDimension(ElementsNarutomodMod instance) {
		super(instance, 115);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if (DimensionManager.isDimensionRegistered(DIMID)) {
			DIMID = DimensionManager.getNextFreeDimId();
			System.err.println("Dimension ID for dimension kamuidimension is already registered. Falling back to ID: " + DIMID);
		}
		dtype = DimensionType.register("kamuidimension", "_kamuidimension", DIMID, WorldProviderMod.class, true);
		DimensionManager.registerDimension(DIMID, dtype);
	}

	public static class WorldProviderMod extends WorldProvider {
		@Override
		public void init() {
			this.biomeProvider = new BiomeProviderCustom(this.world.getSeed());
			this.nether = NETHER_TYPE;
		}

		@Override
		public void calculateInitialWeather() {
		}

		@Override
		public void updateWeather() {
		}

		@Override
		public boolean canDoLightning(net.minecraft.world.chunk.Chunk chunk) {
			return false;
		}

		@Override
		public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk) {
			return false;
		}

		@Override
		public DimensionType getDimensionType() {
			return dtype;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public Vec3d getFogColor(float par1, float par2) {
			return new Vec3d(0, 0, 0);
		}

		@Override
		public IChunkGenerator createChunkGenerator() {
			return new ChunkProviderModded(this.world, this.world.getSeed() - DIMID);
		}

		@Override
		public boolean isSurfaceWorld() {
			return false;
		}

		@Override
		public boolean canRespawnHere() {
			return false;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean doesXZShowFog(int par1, int par2) {
			return false;
		}

		@Override
		public WorldSleepResult canSleepAt(EntityPlayer player, BlockPos pos) {
			return WorldSleepResult.DENY;
		}

		@Override
		protected void generateLightBrightnessTable() {
			float f = 0.12f;
			// modified 5-22-2020
			for (int i = 0; i <= 15; ++i) {
				float f1 = 1 - (float) i / 15f;
				this.lightBrightnessTable[i] = (1 - f1) / (f1 * 3 + 1) * (1 - f) + f;
			}
		}

		@Override
		public boolean doesWaterVaporize() {
			return false;
		}

		@Override
		public BlockPos getSpawnPoint() { // modified 5-22-2020
			WorldInfo info = this.world.getWorldInfo();
			BlockPos pos = new BlockPos(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
			//if (info.getSpawnY() < ChunkProviderModded.SEALEVEL + 1 || info.getSpawnY() > ChunkProviderModded.SEALEVEL + 10) {
				pos = new BlockPos(info.getSpawnX(), 69, info.getSpawnZ());
				this.setSpawnPoint(pos);
			//}
			return pos;
		}

		@Override
		public void onPlayerAdded(EntityPlayerMP entity) {
			int x = (int) entity.posX;
			int y = (int) entity.posY;
			int z = (int) entity.posZ;
			{
				java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("x", x);
				$_dependencies.put("z", z);
				$_dependencies.put("world", world);
				ProcedureKamuiDimensionPlayerEntersDimension.executeProcedure($_dependencies);
			}
		}
	}

	public static class ChunkProviderModded implements IChunkGenerator {
		private static final IBlockState STONE = BlockKamuiBlock.block.getDefaultState();
		private static final IBlockState AIR = Blocks.AIR.getDefaultState();
		private static final int SEALEVEL = 63;
		private final World world;
		private Random random;
		private final NoiseGeneratorSimplex islandNoise;
		private final NoiseGeneratorOctaves perlinnoise1;
		private final NoiseGeneratorOctaves perlinnoise2;
		private final NoiseGeneratorOctaves perlinnoise3;
		private final NoiseGeneratorPerlin height;
		private Biome[] biomesForGeneration;
		private double[] buffer;
		private double[] pnr;
		private double[] ar;
		private double[] br;
		private double[] depthbuff = new double[256];
		private WorldGenerator islandGen;

		public ChunkProviderModded(World worldIn, long seed) {
			worldIn.setSeaLevel(SEALEVEL);
			this.world = worldIn;
			this.random = new Random(seed);
			this.perlinnoise1 = new NoiseGeneratorOctaves(this.random, 16);
			this.perlinnoise2 = new NoiseGeneratorOctaves(this.random, 16);
			this.perlinnoise3 = new NoiseGeneratorOctaves(this.random, 8);
			this.height = new NoiseGeneratorPerlin(this.random, 4);
			this.islandNoise = new NoiseGeneratorSimplex(this.random);
			this.islandGen = new WorldGenerator() {
				public boolean generate(World worldIn, Random rand, BlockPos position) {
					// modified 5-22-2020
					float f = (rand.nextInt(5) + 10);
					for (int i = 0; (i + position.getY()) > 0.0F; i--) {
						for (int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); j++) {
							for (int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); k++)
								this.setBlockAndNotifyAdequately(worldIn, position.add(j, i, k), STONE);
						}
					}
					return true;
				}
			};
		}

		@Override
		public Chunk generateChunk(int x, int z) {
			this.random.setSeed((long) x * 535358712L + (long) z * 347539041L);
			ChunkPrimer chunkprimer = new ChunkPrimer();
			this.setBlocksInChunk(x, z, chunkprimer);
			this.biomesForGeneration = this.world.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 16, z * 16, 16, 16);
			this.replaceBiomeBlocks(x, z, chunkprimer, this.biomesForGeneration);
			Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
			byte[] abyte = chunk.getBiomeArray();
			for (int i = 0; i < abyte.length; ++i)
				abyte[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);
			chunk.generateSkylightMap();
			return chunk;
		}

		@Override
		public void populate(int x, int z) {
			BlockFalling.fallInstantly = true;
			//net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.random, x, z, false);
			int i = x * 16;
			int j = z * 16;
			BlockPos blockpos = new BlockPos(i, 0, j);
			float f = this.getIslandHeightValue(x, z, 1, 1);
			if (f < -10.0F && this.random.nextInt(4) == 0) {
				// modified 6-6-2020
				this.islandGen.generate(this.world, this.random,
						blockpos.add(this.random.nextInt(16) + 8, 55 + this.random.nextInt(16), this.random.nextInt(16) + 8));
				if (this.random.nextInt(4) == 0)
					this.islandGen.generate(this.world, this.random,
							blockpos.add(this.random.nextInt(16) + 8, 55 + this.random.nextInt(16), this.random.nextInt(16) + 8));
			}
			//Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
			//net.minecraftforge.common.MinecraftForge.EVENT_BUS
			//		.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Pre(this.world, this.random, blockpos));
			//biome.decorate(this.world, this.random, new BlockPos(i, 0, j));
			//net.minecraftforge.common.MinecraftForge.EVENT_BUS
			//		.post(new net.minecraftforge.event.terraingen.DecorateBiomeEvent.Post(this.world, this.random, blockpos));
			//if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.world, this.random, x, z, false,
			//		net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS))
			//	WorldEntitySpawner.performWorldGenSpawning(this.world, biome, i + 8, j + 8, 16, 16, this.random);
			//net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, this.random, x, z, false);
			BlockFalling.fallInstantly = false;
		}

		@Override
		public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
			return Lists.newArrayList(); // modified 5-22-2020
		}

		@Override
		public void recreateStructures(Chunk chunkIn, int x, int z) {
		}

		@Override
		public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
			return false;
		}

		@Override
		public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
			return null;
		}

		@Override
		public boolean generateStructures(Chunk chunkIn, int x, int z) {
			return false;
		}

		private double[] getHeights(double[] p_185963_1_, int p_185963_2_, int p_185963_3_, int p_185963_4_, int p_185963_5_, int p_185963_6_,
				int p_185963_7_) {
			net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField event = new net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField(
					this, p_185963_1_, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_);
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
			if (event.getResult() == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
				return event.getNoisefield();
			if (p_185963_1_ == null) {
				p_185963_1_ = new double[p_185963_5_ * p_185963_6_ * p_185963_7_];
			}
			double d0 = 684.412D;
			double d1 = 684.412D;
			d0 = d0 * 2.0D;
			this.pnr = this.perlinnoise3.generateNoiseOctaves(this.pnr, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_,
					d0 / 80.0D, 4.277575000000001D, d0 / 80.0D);
			this.ar = this.perlinnoise1.generateNoiseOctaves(this.ar, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_,
					d0, 684.412D, d0);
			this.br = this.perlinnoise2.generateNoiseOctaves(this.br, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_,
					d0, 684.412D, d0);
			int i = p_185963_2_ / 2;
			int j = p_185963_4_ / 2;
			int k = 0;
			for (int l = 0; l < p_185963_5_; ++l) {
				for (int i1 = 0; i1 < p_185963_7_; ++i1) {
					float f = this.getIslandHeightValue(i, j, l, i1);
					for (int j1 = 0; j1 < p_185963_6_; ++j1) {
						double d2 = this.ar[k] / 512.0D;
						double d3 = this.br[k] / 512.0D;
						double d5 = (this.pnr[k] / 10.0D + 1.0D) / 2.0D;
						double d4;
						if (d5 < 0.0D) {
							d4 = d2;
						} else if (d5 > 1.0D) {
							d4 = d3;
						} else {
							d4 = d2 + (d3 - d2) * d5;
						}
						d4 = d4 - 8.0D;
						d4 = d4 + (double) f;
						int k1 = 2;
						if (j1 > p_185963_6_ / 2 - k1) {
							double d6 = (double) ((float) (j1 - (p_185963_6_ / 2 - k1)) / 64.0F);
							d6 = MathHelper.clamp(d6, 0.0D, 1.0D);
							d4 = d4 * (1.0D - d6) + -3000.0D * d6;
						}
						k1 = 8;
						if (j1 < k1) {
							double d7 = (double) ((float) (k1 - j1) / ((float) k1 - 1.0F));
							d4 = d4 * (1.0D - d7) + -30.0D * d7;
						}
						p_185963_1_[k] = d4;
						++k;
					}
				}
			}
			return p_185963_1_;
		}

		private float getIslandHeightValue(int p_185960_1_, int p_185960_2_, int p_185960_3_, int p_185960_4_) {
			float f = (float) (p_185960_1_ * 2 + p_185960_3_);
			float f1 = (float) (p_185960_2_ * 2 + p_185960_4_);
			float f2 = 100.0F - MathHelper.sqrt(f * f + f1 * f1) * 8.0F;
			if (f2 > 80.0F) {
				f2 = 80.0F;
			}
			if (f2 < -100.0F) {
				f2 = -100.0F;
			}
			for (int i = -12; i <= 12; ++i) {
				for (int j = -12; j <= 12; ++j) {
					long k = (long) (p_185960_1_ + i);
					long l = (long) (p_185960_2_ + j);
					if (k * k + l * l > 4096L && this.islandNoise.getValue((double) k, (double) l) < -0.8999999761581421D) {
						float f3 = (MathHelper.abs((float) k) * 3439.0F + MathHelper.abs((float) l) * 147.0F) % 13.0F + 9.0F;
						f = (float) (p_185960_3_ - i * 2);
						f1 = (float) (p_185960_4_ - j * 2);
						float f4 = 100.0F - MathHelper.sqrt(f * f + f1 * f1) * f3;
						if (f4 > 80.0F) {
							f4 = 80.0F;
						}
						if (f4 < -100.0F) {
							f4 = -100.0F;
						}
						if (f4 > f2) {
							f2 = f4;
						}
					}
				}
			}
			return f2;
		}

		/**
		 * Generates a bare-bones chunk of nothing but stone or ocean blocks, formed,
		 * but featureless.
		 */
		public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
			// modified 5-22-2020
			/*
			 * int i = 2; int j = 3; int k = 33; int l = 3; this.buffer =
			 * this.getHeights(this.buffer, x * 2, 0, z * 2, 3, 33, 3); for (int i1 = 0; i1
			 * < 2; ++i1) { for (int j1 = 0; j1 < 2; ++j1) { for (int k1 = 0; k1 < 32; ++k1)
			 * { double d0 = 0.25D; double d1 = this.buffer[((i1 + 0) * 3 + j1 + 0) * 33 +
			 * k1 + 0]; double d2 = this.buffer[((i1 + 0) * 3 + j1 + 1) * 33 + k1 + 0];
			 * double d3 = this.buffer[((i1 + 1) * 3 + j1 + 0) * 33 + k1 + 0]; double d4 =
			 * this.buffer[((i1 + 1) * 3 + j1 + 1) * 33 + k1 + 0]; double d5 =
			 * (this.buffer[((i1 + 0) * 3 + j1 + 0) * 33 + k1 + 1] - d1) * 0.25D; double d6
			 * = (this.buffer[((i1 + 0) * 3 + j1 + 1) * 33 + k1 + 1] - d2) * 0.25D; double
			 * d7 = (this.buffer[((i1 + 1) * 3 + j1 + 0) * 33 + k1 + 1] - d3) * 0.25D;
			 * double d8 = (this.buffer[((i1 + 1) * 3 + j1 + 1) * 33 + k1 + 1] - d4) *
			 * 0.25D; for (int l1 = 0; l1 < 4; ++l1) { double d9 = 0.125D; double d10 = d1;
			 * double d11 = d2; double d12 = (d3 - d1) * 0.125D; double d13 = (d4 - d2) *
			 * 0.125D; for (int i2 = 0; i2 < 8; ++i2) { double d14 = 0.125D; double d15 =
			 * d10; double d16 = (d11 - d10) * 0.125D; for (int j2 = 0; j2 < 8; ++j2) {
			 * IBlockState iblockstate = AIR; if (d15 > 0.0D) { iblockstate = STONE; } int
			 * k2 = i2 + i1 * 8; int l2 = l1 + k1 * 4; int i3 = j2 + j1 * 8;
			 * primer.setBlockState(k2, l2, i3, iblockstate); d15 += d16; } d10 += d12; d11
			 * += d13; } d1 += d5; d2 += d6; d3 += d7; d4 += d8; } } } }
			 */
		}

		private void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, Biome[] biomesIn) {
			this.depthbuff = this.height.getRegion(this.depthbuff, (double) (x * 16), (double) (z * 16), 16, 16, 0.0625, 0.0625, 1.0);
			for (int i = 0; i < 16; i++)
				for (int j = 0; j < 16; j++)
					generateBiomeTerrain(this.world, this.random, primer, x * 16 + i, z * 16 + j, this.depthbuff[j + i * 16], biomesIn[j + i * 16]);
		}

		// modified 5-22-2020
		private void generateBiomeTerrain(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal, Biome biome) {
			/*
			 * int i = SEALEVEL; IBlockState iblockstate = biome.topBlock; IBlockState
			 * iblockstate1 = biome.fillerBlock; int j = -1; int k = (int) (noiseVal / 3.0 +
			 * 3 + rand.nextDouble() / 4f); int l = x & 15; int i1 = z & 15; for (int j1 =
			 * 255; j1 >= 0; --j1) { IBlockState iblockstate2 =
			 * chunkPrimerIn.getBlockState(i1, j1, l); if (iblockstate2.getMaterial() ==
			 * Material.AIR) { j = -1; } else if (iblockstate2.getBlock() ==
			 * STONE.getBlock()) { if (j == -1) { if (k <= 0) { iblockstate = AIR;
			 * iblockstate1 = STONE; } j = k; if (j1 >= i - 1) {
			 * chunkPrimerIn.setBlockState(i1, j1, l, iblockstate); } else if (j1 < i - 7 -
			 * k) { iblockstate1 = STONE; } else { chunkPrimerIn.setBlockState(i1, j1, l,
			 * iblockstate1); } } else if (j > 0) { j--; chunkPrimerIn.setBlockState(i1, j1,
			 * l, iblockstate1); } } }
			 */
		}
	}

	public static class GenLayerBiomesCustom extends GenLayer {
		private Biome[] allowedBiomes = {Biome.REGISTRY.getObject(new ResourceLocation("void")),};

		public GenLayerBiomesCustom(long seed) {
			super(seed);
		}

		@Override
		public int[] getInts(int x, int z, int width, int depth) {
			int[] dest = IntCache.getIntCache(width * depth);
			for (int dz = 0; dz < depth; dz++) {
				for (int dx = 0; dx < width; dx++) {
					this.initChunkSeed(dx + x, dz + z);
					dest[(dx + dz * width)] = Biome.getIdForBiome(this.allowedBiomes[nextInt(this.allowedBiomes.length)]);
				}
			}
			return dest;
		}
	}

	public static class BiomeProviderCustom extends BiomeProvider {
		private GenLayer genBiomes;
		private GenLayer biomeIndexLayer;
		private BiomeCache biomeCache;

		public BiomeProviderCustom() {
			this.biomeCache = new BiomeCache(this);
		}

		public BiomeProviderCustom(long seed) {
			this.biomeCache = new BiomeCache(this);
			GenLayer[] agenlayer = makeTheWorld(seed);
			this.genBiomes = agenlayer[0];
			this.biomeIndexLayer = agenlayer[1];
		}

		private GenLayer[] makeTheWorld(long seed) {
			GenLayer biomes = new GenLayerBiomesCustom(1);
			biomes = new GenLayerZoom(1000, biomes);
			biomes = new GenLayerZoom(1001, biomes);
			biomes = new GenLayerZoom(1002, biomes);
			biomes = new GenLayerZoom(1003, biomes);
			biomes = new GenLayerZoom(1004, biomes);
			biomes = new GenLayerZoom(1005, biomes);
			GenLayer genlayervoronoizoom = new GenLayerVoronoiZoom(10, biomes);
			biomes.initWorldGenSeed(seed);
			genlayervoronoizoom.initWorldGenSeed(seed);
			return new GenLayer[]{biomes, genlayervoronoizoom};
		}

		public BiomeProviderCustom(World world) {
			this(world.getSeed());
		}

		@Override
		public void cleanupCache() {
			this.biomeCache.cleanupCache();
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			return this.getBiome(pos, null);
		}

		@Override
		public Biome getBiome(BlockPos pos, Biome defaultBiome) {
			return this.biomeCache.getBiome(pos.getX(), pos.getZ(), defaultBiome);
		}

		@Override
		public Biome[] getBiomes(Biome[] oldBiomeList, int x, int z, int width, int depth) {
			return this.getBiomes(oldBiomeList, x, z, width, depth, true);
		}

		@Override /**
					 * Returns an array of biomes for the location input.
					 */
		public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
			IntCache.resetIntCache();
			if (biomes == null || biomes.length < width * height) {
				biomes = new Biome[width * height];
			}
			int[] aint = this.genBiomes.getInts(x, z, width, height);
			try {
				for (int i = 0; i < width * height; ++i) {
					biomes[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
				}
				return biomes;
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
				crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
				crashreportcategory.addCrashSection("x", Integer.valueOf(x));
				crashreportcategory.addCrashSection("z", Integer.valueOf(z));
				crashreportcategory.addCrashSection("w", Integer.valueOf(width));
				crashreportcategory.addCrashSection("h", Integer.valueOf(height));
				throw new ReportedException(crashreport);
			}
		}

		@Override /**
					 * Gets a list of biomes for the specified blocks.
					 */
		public Biome[] getBiomes(@Nullable Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
			IntCache.resetIntCache();
			if (listToReuse == null || listToReuse.length < width * length) {
				listToReuse = new Biome[width * length];
			}
			if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0) {
				Biome[] abiome = this.biomeCache.getCachedBiomes(x, z);
				System.arraycopy(abiome, 0, listToReuse, 0, width * length);
				return listToReuse;
			} else {
				int[] aint = this.biomeIndexLayer.getInts(x, z, width, length);
				for (int i = 0; i < width * length; ++i) {
					listToReuse[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
				}
				return listToReuse;
			}
		}

		@Override /**
					 * checks given Chunk's Biomes against List of allowed ones
					 */
		public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
			IntCache.resetIntCache();
			int i = x - radius >> 2;
			int j = z - radius >> 2;
			int k = x + radius >> 2;
			int l = z + radius >> 2;
			int i1 = k - i + 1;
			int j1 = l - j + 1;
			int[] aint = this.genBiomes.getInts(i, j, i1, j1);
			try {
				for (int k1 = 0; k1 < i1 * j1; ++k1) {
					Biome biome = Biome.getBiome(aint[k1]);
					if (!allowed.contains(biome)) {
						return false;
					}
				}
				return true;
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
				crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
				crashreportcategory.addCrashSection("x", Integer.valueOf(x));
				crashreportcategory.addCrashSection("z", Integer.valueOf(z));
				crashreportcategory.addCrashSection("radius", Integer.valueOf(radius));
				crashreportcategory.addCrashSection("allowed", allowed);
				throw new ReportedException(crashreport);
			}
		}

		@Override
		@Nullable
		public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
			IntCache.resetIntCache();
			int i = x - range >> 2;
			int j = z - range >> 2;
			int k = x + range >> 2;
			int l = z + range >> 2;
			int i1 = k - i + 1;
			int j1 = l - j + 1;
			int[] aint = this.genBiomes.getInts(i, j, i1, j1);
			BlockPos blockpos = null;
			int k1 = 0;
			for (int l1 = 0; l1 < i1 * j1; ++l1) {
				int i2 = i + l1 % i1 << 2;
				int j2 = j + l1 / i1 << 2;
				Biome biome = Biome.getBiome(aint[l1]);
				if (biomes.contains(biome) && (blockpos == null || random.nextInt(k1 + 1) == 0)) {
					blockpos = new BlockPos(i2, 0, j2);
					++k1;
				}
			}
			return blockpos;
		}
	}
}
