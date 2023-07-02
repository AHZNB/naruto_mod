
package net.narutomod.entity;

//import net.minecraftforge.fml.relauncher.SideOnly;
//import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;

import net.narutomod.block.BlockAmaterasuBlock;
import net.narutomod.block.BlockWaterStill;
import net.narutomod.item.ItemSuiton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Maps;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWaterPrison extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 156;
	public static final int ENTITYID_RANGED = 157;
	private static final Map<EntityLivingBase, EntityLivingBase> trappedMap = Maps.newHashMap();

	public EntityWaterPrison(ElementsNarutomodMod instance) {
		super(instance, 412);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "water_prison"), ENTITYID).name("water_prison").tracker(64, 3, true).build());
	}

	public static boolean isEntityTrapped(EntityLivingBase entity) {
		return trappedMap.containsValue(entity);
	}

	public static boolean isEntityTrappedBy(EntityLivingBase trapper, EntityLivingBase target) {
		EntityLivingBase entity1 = trappedMap.get(trapper);
		return entity1 != null ? entity1.equals(target) : false;
	}

	public static boolean isEntityTrapping(EntityLivingBase trapper) {
		return trappedMap.containsKey(trapper);
	}

	public static class EC extends Entity {
		private EntityLivingBase user;
		private EntityLivingBase target;
		private AxisAlignedBB realBB;
		private int totalWaterBlocks;
		private int duration;
		private final double chakraBurn = ItemSuiton.WATERPRISON.chakraUsage * 0.1d; // per second

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase userIn, EntityLivingBase targetIn, int durationIn) {
			this(userIn.world);
			this.user = userIn;
			this.target = targetIn;
			this.duration = durationIn;
			if (!this.world.isAirBlock(new BlockPos(targetIn).down())) {
				targetIn.setPositionAndUpdate(targetIn.posX, targetIn.posY + 0.5d, targetIn.posZ);
			}
			this.setPosition(targetIn.posX, targetIn.posY, targetIn.posZ);
			int minX = (int) Math.floor(targetIn.getEntityBoundingBox().minX - 0.5d);
			int maxX = (int) Math.ceil(targetIn.getEntityBoundingBox().maxX + 0.5d);
			int minZ = (int) Math.floor(targetIn.getEntityBoundingBox().minZ - 0.5d);
			int maxZ = (int) Math.ceil(targetIn.getEntityBoundingBox().maxZ + 0.5d);
			int minY = (int) Math.floor(targetIn.getEntityBoundingBox().minY - 0.5d);
			int maxY = (int) Math.ceil(targetIn.getEntityBoundingBox().maxY + 0.5d);
			this.realBB = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
			for (BlockPos pos : BlockPos.getAllInBoxMutable(minX, minY, minZ, maxX, maxY, maxZ)) {
				IBlockState blockstate = this.world.getBlockState(pos);
				if (blockstate.getMaterial() == Material.AIR
				 || (blockstate.getMaterial() == Material.FIRE && blockstate.getBlock() != BlockAmaterasuBlock.block)
				 || blockstate.getBlock() instanceof BlockBush) {
					this.world.setBlockState(pos, BlockWaterStill.block.getDefaultState(), 3);
					++this.totalWaterBlocks;
				}
			}
			//for (BlockPos pos : ProcedureUtils.getAllAirBlocks(this.world, this.realBB)) {
			//	this.world.setBlockState(pos, BlockWaterStill.block.getDefaultState(), 3);
			//	++this.totalWaterBlocks;
			//}
			trappedMap.put(userIn, targetIn);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				this.playSound(this.getSplashSound(), 1, 1f);
				for (BlockPos pos : BlockPos.getAllInBoxMutable((int)this.realBB.minX, (int)this.realBB.minY, (int)this.realBB.minZ,
				 (int)this.realBB.maxX, (int)this.realBB.maxY, (int)this.realBB.maxZ)) {
					if (this.world.getBlockState(pos).getMaterial() == Material.WATER) {
						this.world.setBlockToAir(pos);
					}
				}
				Map<BlockPos, IBlockState> map = Maps.newHashMap();
				for (BlockPos pos : ProcedureUtils.getAllAirBlocks(this.world, this.realBB.contract(0d, this.realBB.maxY-this.realBB.minY-1, 0d))) {
					map.put(pos, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(1)));
				}
				new net.narutomod.event.EventSetBlocks(this.world, map, 0, 10, false, false);
				trappedMap.remove(this.user);
			}
		}

		private boolean enoughWaterRemaining() {
			int remaining = 0;
			for (BlockPos pos : BlockPos.getAllInBoxMutable((int)this.realBB.minX, (int)this.realBB.minY, (int)this.realBB.minZ,
			 (int)this.realBB.maxX, (int)this.realBB.maxY, (int)this.realBB.maxZ)) {
				if (this.world.getBlockState(pos).getBlock() == BlockWaterStill.block) {
					++remaining;
				}
			}
			return remaining >= this.totalWaterBlocks * 2 / 3;
		}
		
		@Override
		public void onUpdate() {
			if (this.user != null && this.user.isEntityAlive() && ItemJutsu.canTarget(this.target)
			 && this.user.getDistance(this.target) <= 4d && ProcedureUtils.isEntityInFOV(this.user, this.target)
			 && this.enoughWaterRemaining() && this.ticksExisted <= this.duration) {
				this.target.setPositionAndUpdate(this.posX, this.posY, this.posZ);
				for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.realBB)) {
					if (!entity.equals(this.target)) {
						Vec3d vec = entity.getPositionVector().subtract(this.getPositionVector()).normalize().scale(0.2d);
						ProcedureUtils.setVelocity(entity, entity.motionX + vec.x, entity.motionY, entity.motionZ + vec.z);
					}
				}
				if (this.ticksExisted % 20 == 0) {
					net.narutomod.Chakra.pathway(this.user).consume(this.chakraBurn);
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
			if (this.user != null) {
				if (this.isDead) {
					ProcedureSync.EntityNBTTag.removeAndSync(this.user, NarutomodModVariables.forceBowPose);
				} else if (!this.world.isRemote && this.ticksExisted == 1) {
					ProcedureSync.EntityNBTTag.setAndSync(this.user, NarutomodModVariables.forceBowPose, true);
				}
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			this.ticksExisted = compound.getInteger("ticksExisted");
			this.duration = compound.getInteger("duration");
			this.totalWaterBlocks = compound.getInteger("totalWaterBlocks");
			this.realBB = new AxisAlignedBB(compound.getDouble("minX"), compound.getDouble("minY"), compound.getDouble("minZ"),
			 compound.getDouble("maxX"), compound.getDouble("maxY"), compound.getDouble("maxZ"));
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setInteger("ticksExisted", this.ticksExisted);
			compound.setInteger("duration", this.duration);
			compound.setInteger("totalWaterBlocks", this.totalWaterBlocks);
			compound.setDouble("minX", this.realBB.minX);
			compound.setDouble("minY", this.realBB.minY);
			compound.setDouble("minZ", this.realBB.minZ);
			compound.setDouble("maxX", this.realBB.maxX);
			compound.setDouble("maxY", this.realBB.maxY);
			compound.setDouble("maxZ", this.realBB.maxZ);
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				return this.createJutsu(entity) != null;
			}

			public Entity createJutsu(EntityLivingBase entity, EntityLivingBase target) {
				return this.createJutsu(entity, target, 3600);
			}

			public Entity createJutsu(EntityLivingBase entity, EntityLivingBase target, int duration) {
				if (entity.getDistance(target) <= 4.0d && target.width <= 2.0f && target.height <= 3.0f) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
					  net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:suironojutsu")),
					  SoundCategory.NEUTRAL, 1, 1f);
					Entity entitytospawn = new EC(entity, (EntityLivingBase)target, duration);
					entity.world.spawnEntity(entitytospawn);
					return entitytospawn;
				}
				return null;
			}

			public Entity createJutsu(EntityLivingBase entity) {
				Entity target = entity instanceof EntityPlayer 
				  ? ProcedureUtils.objectEntityLookingAt(entity, 4d).entityHit
				  : entity instanceof EntityLiving
				  ? ((EntityLiving)entity).getAttackTarget() : null;
				if (target instanceof EntityLivingBase) {
					return this.createJutsu(entity, (EntityLivingBase)target);
				}
				return null;
			}
		}
	}
}
