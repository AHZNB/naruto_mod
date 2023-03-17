
package net.narutomod.entity;

//import net.minecraftforge.fml.relauncher.SideOnly;
//import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
//import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
//import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.block.BlockMud;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySwampPit extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 191;
	public static final int ENTITYID_RANGED = 192;

	public EntitySwampPit(ElementsNarutomodMod instance) {
		super(instance, 452);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "swamp_pit"), ENTITYID).name("swamp_pit").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private BlockPos center;
		private int radius;
		private int offsetY;
		
		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase summonerIn, BlockPos centerPos, int radiusIn) {
			this(summonerIn.world);
			this.center = centerPos.add(0, radiusIn, 0);
			this.radius = radiusIn;
			int y = 0;
			for (int j = 0; j > -radiusIn * 2 && this.center.getY() + j > 0; j--) {
				if (j == y) {
					for (int i = -radiusIn; i <= radiusIn; i++) {
						for (int k = -radiusIn; k <= radiusIn; k++) {
							if (y >= j && this.world.isAirBlock(this.center.add(i, j, k))) {
								y--;
							}
						}
					}
				}
			}
			this.offsetY = y;
			this.setPosition(centerPos.getX() + 0.5d, centerPos.getY(), centerPos.getZ() + 0.5d);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.center != null) {
				for (int i = 0; i < this.radius; i++) {
					Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY + 1d, this.posZ, 
					 100, (double)this.radius/2, 0d, (double)this.radius/2, 0.0d, 0.0d, 0.0d, 0x801c120d, 25);
				}
				Map<BlockPos, IBlockState> map = Maps.newHashMap();
				for (int i = -this.radius; i <= this.radius; i++) {
					for (int k = -this.radius; k <= this.radius; k++) {
						if (1 - this.ticksExisted > this.offsetY) {
							//this.world.setBlockToAir(this.center.add(i, 1 - this.ticksExisted, k));
							map.put(this.center.add(i, 1 - this.ticksExisted, k), Blocks.AIR.getDefaultState());
						} else {
							//this.world.setBlockState(this.center.add(i, 1 - this.ticksExisted, k), BlockMud.block.getDefaultState(), 3);
							map.put(this.center.add(i, 1 - this.ticksExisted, k), BlockMud.block.getDefaultState());
						}
					}
				}
				new net.narutomod.event.EventSetBlocks(this.world, map, 0, 600, false, false);
			}
			if (!this.world.isRemote && this.ticksExisted >= this.radius - this.offsetY) {
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
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				RayTraceResult rtr = ProcedureUtils.raytraceBlocks(entity, 50d);
				if (rtr != null && rtr.typeOfHit != RayTraceResult.Type.MISS) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
					 .getObject(new ResourceLocation("narutomod:yominuma")), SoundCategory.PLAYERS, 1, 1f);
					entity.world.spawnEntity(new EC(entity, rtr.getBlockPos(), (int)power));
					return true;
				}
				return false;
			}
		}
	}
}
