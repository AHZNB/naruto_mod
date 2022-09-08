
package net.narutomod.block;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;

import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class BlockWaterStill extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:water_still")
	public static final Block block = null;

	public BlockWaterStill(ElementsNarutomodMod instance) {
		super(instance, 413);
	}

	@Override
	public void initElements() {
		elements.blocks.add(() -> new BlockCustom().setRegistryName("water_still"));
		elements.items.add(() -> new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
				new ModelResourceLocation("narutomod:water_still", "inventory"));
	}

	public static class BlockCustom extends BlockStaticLiquid {
		public BlockCustom() {
			super(Material.WATER);
			this.setUnlocalizedName("water_still");
			this.setHardness(100f);
			this.setResistance(5f);
			this.setLightOpacity(3);
			this.disableStats();
			this.setCreativeTab(null);
		}

		@Override
		public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		}

		@Override
		public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion) {
			return motion.addVector(0.0d, -1.5d, 0.0d);
		}
	}

	public static boolean isInsideBlock(Entity entityIn) {
		return isInsideBlock(entityIn, true);
	}

	public static boolean isInsideBlock(Entity entityIn, boolean testHead) {
		if (entityIn.getRidingEntity() instanceof EntityBoat) {
			return false;
		}
		double d0 = entityIn.posY + (testHead ? (double)entityIn.getEyeHeight() : 0d);
		BlockPos blockpos = new BlockPos(entityIn.posX, d0, entityIn.posZ);
		IBlockState iblockstate = entityIn.world.getBlockState(blockpos);
		return iblockstate.getBlock() == block;
	}
}
