
package net.narutomod.block;

import net.narutomod.procedure.ProcedureMeteorBlockDestroyedByExplosion;
import net.narutomod.procedure.ProcedureMeteorBlockAdded;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Explosion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.Block;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class BlockMeteor extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:meteor")
	public static final Block block = null;
	public BlockMeteor(ElementsNarutomodMod instance) {
		super(instance, 30);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("meteor"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation("narutomod:meteor", "inventory"));
	}
	public static class BlockCustom extends BlockFalling {
		public BlockCustom() {
			super(Material.ROCK);
			setUnlocalizedName("meteor");
			setSoundType(SoundType.GROUND);
			setHardness(64000F);
			setResistance(64000F);
			setLightLevel(1F);
			setLightOpacity(0);
			setCreativeTab(null);
			setBlockUnbreakable();
		}

		@Override
		public boolean isFullCube(IBlockState state) {
			return false;
		}

		@Override
		public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
			return new AxisAlignedBB(-16, -12, -16, 32, 36, 32);
		}

		@Override
		public boolean isOpaqueCube(IBlockState state) {
			return false;
		}

		@Override
		public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
			super.onBlockAdded(world, pos, state);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				$_dependencies.put("world", world);
				ProcedureMeteorBlockAdded.executeProcedure($_dependencies);
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
			super.randomDisplayTick(state, world, pos, random);
			EntityPlayer entity = Minecraft.getMinecraft().player;
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			int i = x;
			int j = y;
			int k = z;
			if (true)
				for (int l = 0; l < 200; ++l) {
					double d0 = (double) ((float) i + 0.5) + (double) (random.nextFloat() - 0.5) * 1.9D;
					double d1 = ((double) ((float) j + 0.7) + (double) (random.nextFloat() - 0.5) * 1.9D) + 0.5;
					double d2 = (double) ((float) k + 0.5) + (double) (random.nextFloat() - 0.5) * 1.9D;
					world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, d0, d1, d2, 0, 0, 0);
				}
		}

		@Override
		public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion e) {
			super.onBlockDestroyedByExplosion(world, pos, e);
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			{
				Map<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				$_dependencies.put("world", world);
				ProcedureMeteorBlockDestroyedByExplosion.executeProcedure($_dependencies);
			}
		}
	}
}
