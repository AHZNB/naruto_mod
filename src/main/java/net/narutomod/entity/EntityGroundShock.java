
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityGroundShock extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 325;
	public static final int ENTITYID_RANGED = 326;

	public EntityGroundShock(ElementsNarutomodMod instance) {
		super(instance, 678);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "ground_shock"), ENTITYID).name("ground_shock").tracker(64, 3, true).build());
	}

	public static void execute(World world, int x, int y, int z, int r) {
		world.spawnEntity(new EntityCustom(world, x, y, z, r));
	}

	public static class EntityCustom extends Entity {
		private int radius;
		private List<Entity> entitylist = Lists.newArrayList();

		public EntityCustom(World worldIn) {
			super(worldIn);
			this.setSize(0.01f, 0.01f);
		}

		public EntityCustom(World worldIn, int x, int y, int z, int r) {
			this(worldIn);
			this.setPosition(0.5d + x, y, 0.5d + z);
			this.radius = r;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (!this.world.isRemote) {
				if (this.ticksExisted <= this.radius) {
					BlockPos blockpos = new BlockPos(this);
					int i = this.ticksExisted + 1;
					BlockPos.PooledMutableBlockPos pos1 = BlockPos.PooledMutableBlockPos.retain();
					for (BlockPos pos : BlockPos.getAllInBoxMutable(blockpos.add(-i, -5, -i), blockpos.add(i, 3, i))) {
						IBlockState state = this.world.getBlockState(pos);
						double d0 = pos.getX() - blockpos.getX();
						double d1 = pos.getZ() - blockpos.getZ();
						double d2 = MathHelper.sqrt(d0 * d0 + d1 * d1);
						if ((int)d2 == i && state.isFullBlock() && this.world.isAirBlock(pos1.setPos(pos.getX(), pos.getY()+1, pos.getZ()))) {
							for (Entity entity1 : this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(pos1).grow(0.1d))) {
								if (!(entity1 instanceof EntityFallingBlock) && !this.entitylist.contains(entity1)) {
									this.entitylist.add(entity1);
									entity1.motionY += 0.9d;
									entity1.velocityChanged = true;
									if (entity1 instanceof EntityLivingBase) {
										((EntityLivingBase)entity1).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0, false, false));
										((EntityLivingBase)entity1).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 1, false, false));
									}
								}
							}
							EntityFallingBlock entity = new EntityFallingBlock(this. world, 0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ(), state);
							entity.motionY = 0.4d;
							this.world.spawnEntity(entity);
						}
					}
					pos1.release();
				} else {
					this.setDead();
				}
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}
}
