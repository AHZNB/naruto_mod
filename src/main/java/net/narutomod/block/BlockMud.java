
package net.narutomod.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;

import net.narutomod.procedure.ProcedureMudMobplayerCollidesBlock;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.material.MapColor;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeSwamp;

@ElementsNarutomodMod.ModElement.Tag
public class BlockMud extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:mud")
	public static final Block block = null;
	@GameRegistry.ObjectHolder("narutomod:mud")
	public static final Item item = null;
	public static final Material MUD = new MaterialLiquid(MapColor.DIRT);
	private Fluid fluid;

	public BlockMud(ElementsNarutomodMod instance) {
		super(instance, 379);
		fluid = new Fluid("mud", new ResourceLocation("narutomod:blocks/mud_still"), new ResourceLocation("narutomod:blocks/mud_flow"))
				.setLuminosity(0).setDensity(5000).setViscosity(20000).setGaseous(false);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockFluidClassic(fluid, MUD) {
			@Override
			public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
				super.onEntityCollidedWithBlock(world, pos, state, entity);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				{
					java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", world);
					ProcedureMudMobplayerCollidesBlock.executeProcedure($_dependencies);
				}
			}
			@Override
			public boolean causesSuffocation(IBlockState state) {
				return true;
			}
		}.setUnlocalizedName("mud").setRegistryName("mud"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName("mud"));
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		FluidRegistry.registerFluid(fluid);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelBakery.registerItemVariants(item);
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return new ModelResourceLocation("narutomod:mud", "mud");
			}
		});
		ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation("narutomod:mud", "mud");
			}
		});
	}

	@Override
	public void generateWorld(Random random, int chunkX, int chunkZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
		boolean dimensionCriteria = false;
		float chance = 0.1f;
//		if (dimID == 0 && world.getWorldInfo().getTerrainType() != WorldType.FLAT
		if (dimID == 0 && world.getBiome(new BlockPos(chunkX * 16, 64, chunkZ * 16)) instanceof BiomeSwamp)
			dimensionCriteria = true;
		if (!dimensionCriteria || random.nextFloat() > chance)
			return;
		int i = chunkX + random.nextInt(16) + 8;
		int j = random.nextInt(256);
		int k = chunkZ + random.nextInt(16) + 8;
		new WorldGenLakes(block).generate(world, random, new BlockPos(i, j, k));
	}
}
