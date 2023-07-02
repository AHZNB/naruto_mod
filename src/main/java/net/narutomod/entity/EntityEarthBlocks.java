
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.block.Block;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;

import java.util.Map;
import java.util.List;
import com.google.common.collect.Maps;
import java.util.Iterator;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import com.google.common.collect.Lists;
import java.util.ArrayList;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEarthBlocks extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 152;
	public static final int ENTITYID_RANGED = 153;

	public EntityEarthBlocks(ElementsNarutomodMod instance) {
		super(instance, 410);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(Base.class)
		  .id(new ResourceLocation("narutomod", "earth_blocks"), ENTITYID).name("earth_blocks").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(Base.class, renderManager -> {
			return new RenderEarthBlocks(renderManager);
		});
	}

	public static class Base extends Entity {
		private List<? extends BlockPos> ogList;
		private final Map<Vec3d, IBlockState> blocksMap = Maps.newHashMap();
		private final Map<Entity, Vec3d> entityMap = Maps.newHashMap();
		private int fallTime = 600;
		private int ticksAlive;
		protected int fallTicks;
		private int blocksTotal;
		private boolean breakOnImpact;

		public Base(World world) {
			super(world);
			this.isImmuneToFire = true;
		}

		public Base(World world, List<? extends BlockPos> list) {
			this(world);
			if (list.isEmpty()) {
				return;
			}
			AxisAlignedBB aabb = new AxisAlignedBB(list.get(0));
			for (BlockPos pos : list) {
				double d0 = (double)pos.getX() < aabb.minX ? -1d : ((double)pos.getX() + 1d) > aabb.maxX ? 1d : 0d;
				double d1 = (double)pos.getY() < aabb.minY ? -1d : ((double)pos.getY() + 1d) > aabb.maxY ? 1d : 0d;
				double d2 = (double)pos.getZ() < aabb.minZ ? -1d : ((double)pos.getZ() + 1d) > aabb.maxZ ? 1d : 0d;
				aabb = aabb.expand(d0, d1, d2);
			}
			this.setSize((float)Math.max(aabb.maxX - aabb.minX, aabb.maxZ - aabb.minZ), (float)(aabb.maxY - aabb.minY));
			this.setPosition(aabb.minX + (aabb.maxX - aabb.minX) / 2, aabb.minY, aabb.minZ + (aabb.maxZ - aabb.minZ) / 2);
			for (BlockPos pos : list) {
				IBlockState blockstate = this.world.getBlockState(pos);
				if (!this.isAirOrLiquid(blockstate, pos)) {
					Vec3d vec = new Vec3d(pos).addVector(0.5d, 0.0d, 0.5d).subtract(this.getPositionVector());
					this.blocksMap.put(vec, blockstate);
				}
			}
			this.blocksTotal = this.blocksMap.size();
			for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox())) {
				AxisAlignedBB bb = this.getEntityBoundingBox().intersect(entity.getEntityBoundingBox());
				if (this.getBBVolume(bb) > this.getBBVolume(entity.getEntityBoundingBox()) * 0.66667d) {
					this.entityMap.put(entity, entity.getPositionVector().subtract(this.getPositionVector()));
				}
			}
			this.ogList = new ArrayList(list);
		}

		private boolean isAirOrLiquid(IBlockState blockstate, BlockPos pos) {
			return blockstate.getBlock().isAir(blockstate, this.world, pos) || blockstate.getMaterial().isLiquid();
		}

		@Override
		protected void entityInit() {
		}

		private Vec3d getCenter() {
			AxisAlignedBB bb = this.getEntityBoundingBox();
        	return new Vec3d(bb.minX + (bb.maxX - bb.minX) * 0.5D, bb.minY + (bb.maxY - bb.minY) * 0.5D, bb.minZ + (bb.maxZ - bb.minZ) * 0.5D);
    	}

		@Override
		public boolean canBeCollidedWith() {
			return true;
		}

		@Override
		public boolean canBePushed() {
			return false;
		}

		@Override
		public AxisAlignedBB getCollisionBoundingBox() {
			return this.getEntityBoundingBox();
		}

		public void setFallTime(int ticks) {
			this.fallTime = ticks;
		}

		public void explodeOnImpact(boolean explode) {
			this.breakOnImpact = explode;
		}

		/*@Override
		public void setDead() {
			if (!this.world.isRemote && !this.blocksMap.isEmpty() && this.griefingAllowed()) {
				for (Map.Entry<Vec3d, IBlockState> entry : this.blocksMap.entrySet()) {
					Vec3d vec = entry.getKey().add(this.getPositionVector());
					EntityFallingBlock fb = new EntityFallingBlock(this.world, vec.x, vec.y, vec.z, entry.getValue());
					fb.fallTime = 1;
					this.world.spawnEntity(fb);
				}
			}
			super.setDead();
		}*/

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.world.isRemote && amount > 3f * this.mass()) {
				this.breakOnImpact = true;
				this.onImpact(amount);
				return true;
			}
			return false;
		}

		@Override
	    public void applyEntityCollision(Entity entityIn) {
       		//if (entityIn instanceof Base) {
       		//	if (!this.world.isRemote && this.ticksAlive > 20 && this.mass() > ((Base)entityIn).mass() / 2) {
       		//		entityIn.setNoGravity(false);
       		//	}
       		//} else
       		if (!entityIn.noClip && !entityIn.isBeingRidden()) {
	       		//EnumFacing hitface = BlocksMoveHelper.collideWithEntity(this, entityIn, this.getMotion());
	       		EnumFacing hitface = BlocksMoveHelper.getCollidingSide(this.getCollisionBoundingBox(), entityIn.getEntityBoundingBox());
		        if (hitface != null) {
		        	if (hitface == EnumFacing.UP) {
		        		if (this.motionY > 0d) {
		        			entityIn.setPosition(entityIn.posX, this.getCollisionBoundingBox().maxY, entityIn.posZ);
		        		}
		        	} else if (!this.entityMap.containsKey(entityIn) && 
		        	 (this.getVelocity() > 0.4d || (hitface == EnumFacing.DOWN && entityIn.onGround))) {
		           		entityIn.attackEntityFrom(DamageSource.FALLING_BLOCK, this.getCollisionDamage());
		        	}
		        }
       		}
	    }

	    private Vec3d getMotion() {
	    	return new Vec3d(this.motionX, this.motionY, this.motionZ);
	    }

		private double getVelocity() {
			return this.getMotion().lengthVector();
		}

		private float collisionForce() {
			return (float)(this.getVelocity() * (double)this.mass());
		}

	    private float getCollisionDamage() {
	    	return this.collisionForce() * 0.5f;
	    }

	    private int mass() {
	    	return this.blocksTotal;
	    }

	    private double getBBVolume(AxisAlignedBB bb) {
	    	return (bb.maxX - bb.minX) * (bb.maxY - bb.minY) * (bb.maxZ - bb.minZ);
	    }

	    public Map<Vec3d, IBlockState> getBlocksMap() {
	    	return this.blocksMap;
	    }

	    public List<? extends BlockPos> getBlockposList() {
	    	return this.ogList;
	    }

	    public boolean griefingAllowed() {
	    	return net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, null);
	    }

	    public int getTicksAlive() {
	    	return this.ticksAlive;
	    }

		@SideOnly(Side.CLIENT)
		public void handleServerPacket(ProcedureSync.SPacketEarthBlocks packet) {
			this.blocksTotal = packet.blocks;
			this.blocksMap.put(new Vec3d(packet.x, packet.y, packet.z), Block.getStateById(packet.state));
		}

		public void handleClientPacket(EntityPlayerMP player, ProcedureSync.CPacketEarthBlocks packet) {
			if (packet.op == 0) {
	           	for (Map.Entry<Vec3d, IBlockState> entry : this.blocksMap.entrySet()) {
	           		ProcedureSync.SPacketEarthBlocks.sendToPlayer(player, this, this.blocksTotal, entry.getKey(), entry.getValue());
	           	}
	           	ProcedureSync.ResetBoundingBox.sendToPlayer(this, player);
			} else if (packet.op == 1) {
				this.onImpact(packet.amount);
			}
		}

		@Override
		public void onUpdate() {
			if (this.blocksMap.isEmpty() && !this.world.isRemote) {
				this.setDead();
			} else {
				if (this.blocksMap.isEmpty() && this.firstUpdate) {
					//this.sendRequestToServer();
					ProcedureSync.CPacketEarthBlocks.sendToServer(0, this);
				}
				++this.ticksAlive;
	            this.prevPosX = this.posX;
	            this.prevPosY = this.posY;
	            this.prevPosZ = this.posZ;
	            boolean flag = this.griefingAllowed();
	            if (!this.world.isRemote) {
	            	BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
		            for (Map.Entry<Vec3d, IBlockState> entry : this.blocksMap.entrySet()) {
		            	Vec3d vec = this.getPositionVector().add(entry.getKey());
			            pos.setPos(vec.x, vec.y, vec.z);
			            if (this.ticksAlive < 15 || this.rand.nextInt(this.ticksAlive) == 0) {
							((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST,
							 0.5D+pos.getX(), 0.5D+pos.getY(), 0.5D+pos.getZ(), 5, 0.2d, 0.2d, 0.2d,
							 this.ticksAlive > 15 ? 0.0d : 0.15d, Block.getIdFromBlock(entry.getValue().getBlock()));
			            }
			            if (flag && this.ticksAlive == 1 && this.world.getBlockState(pos).getBlock() == entry.getValue().getBlock()) {
		                   	this.world.setBlockToAir(pos);
		            	}
	            	}
	            	pos.release();
	            }
	            if (!this.hasNoGravity()) {
	                this.motionY -= 0.04D;
	            	++this.fallTicks;
	            } else if (!this.world.isRemote && this.ticksAlive > this.fallTime) {
	            	this.setNoGravity(false);
	            }
	            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
				for (Map.Entry<Entity, Vec3d> entry : this.entityMap.entrySet()) {
					Vec3d vec = this.getPositionVector().add(entry.getValue());
	            	entry.getKey().setPositionAndUpdate(vec.x, vec.y, vec.z);
	            }
           		for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getCollisionBoundingBox())) {
        			this.applyEntityCollision(entity);
           		}
				if (this.onGround && !this.hasNoGravity()) {
					this.onImpact(this.collisionForce());
           		}
           		this.motionX *= 0.98d;
           		this.motionY *= 0.98d;
           		this.motionZ *= 0.98d;
			}
            this.firstUpdate = false;
		}

		@Override
		public void move(MoverType type, double x, double y, double z) {
			if (type != MoverType.SELF) {
				return;
			}
			double dx = x;
			double dy = y;
			double dz = z;
			List<AxisAlignedBB> list = this.world.getCollisionBoxes(this, this.getCollisionBoundingBox().expand(x, y, z));
			ProcedureUtils.CollisionHelper ch = new ProcedureUtils.CollisionHelper(this);
			ch.collideWithAABBs(list, x, y, z);
			x = ch.minX(x);
			y = ch.minY(y);
			z = ch.minZ(z);
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
			this.resetPositionToBB();
			this.collidedHorizontally = dx != x || dz != z;
			this.collidedVertically = dy != y;
			this.collided = this.collidedHorizontally || this.collidedVertically;
			if (!this.world.isRemote) {
				boolean canMoveThrough = true;
				//this.breakOnImpact = this.collisionForce() > 1000.0f;
				if (dx != x || dy != y || dz != z) {
					List<BlockPos> list1 = ch.getHitBlocks();
					float f = BlocksMoveHelper.getBlocksTotalResistance(this.world, list1);
					//float hitarea = (float)list.size() / this.collisionForce();
					float hitarea = f / this.collisionForce() * 0.2f;
					//canMoveThrough = !this.containsUnbreakableBlocks(list) && hitarea < 1.0f;
					canMoveThrough = this.ticksAlive > 20 ? (f < 1000.0f && hitarea < 1.0f) : true;
					double d = MathHelper.clamp(1.0d - hitarea, 0.0d, 0.8d);
					this.motionX *= d;
					this.motionY *= d;
					this.motionZ *= d;
					if (canMoveThrough && !this.world.isRemote) {
				        for (BlockPos pos : list1) {
					    	ProcedureUtils.breakBlockAndDropWithChance(this.world, pos, this.destroyHardness(), 
					         this.collisionForce() * 0.1f, 0.3f, false);
						}
					}
				}
				this.onGround = this.collidedVertically && dy < 0.0D && (!canMoveThrough || !this.griefingAllowed());
				 //&& ch.hitsOnSide[EnumFacing.DOWN.getIndex()] > (int)(this.width * this.width * 0.6f);
				ProcedureSync.EntityState.sendToTracking(this);
			}
		}

		private float destroyHardness() {
			return MathHelper.clamp(MathHelper.sqrt((float)this.mass()), 5f, 50f);
		}

		private boolean containsUnbreakableBlocks(List<AxisAlignedBB> list) {
			for (AxisAlignedBB aabb : list) {
				BlockPos pos = new BlockPos(ProcedureUtils.BB.getCenter(aabb));
				IBlockState blockstate = this.world.getBlockState(pos);
				float hardness = blockstate.getBlockHardness(this.world, pos);
				if (hardness < 0f || hardness > this.destroyHardness()) {
					return true;
				}
			}
			return false;
		}

	    protected void onImpact(float impactDamage) {
			if (this.world.isRemote) {
				ProcedureSync.CPacketEarthBlocks.sendToServer(1, this, impactDamage);
			} else {
	           	for (Map.Entry<Entity, Vec3d> entry : this.entityMap.entrySet()) {
	           		entry.getKey().attackEntityFrom(DamageSource.FALLING_BLOCK, this.getCollisionDamage());
	           	}
	           	boolean flag = this.griefingAllowed();
	           	impactDamage = MathHelper.sqrt(impactDamage) * 0.002236f;
				BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
				for (Map.Entry<Vec3d, IBlockState> entry : this.blocksMap.entrySet()) {
					Vec3d vec = this.getPositionVector().add(entry.getKey());
					vec = new Vec3d(pos.setPos(vec.x, vec.y, vec.z)).addVector(0.5d, 0.0d, 0.5d);
		           	if (this.breakOnImpact) {
		           		if (this.rand.nextFloat() <= 0.3f) {
							this.world.setBlockState(pos, entry.getValue(), 3);
			           		EntityFallingBlock entity = new EntityFallingBlock(this.world, vec.x, vec.y, vec.z, entry.getValue());
			           		this.world.spawnEntity(entity);
			           		entity.motionX = entry.getKey().x * (this.rand.nextDouble() * 0.2d + impactDamage);
		           			entity.motionY = entry.getKey().y * (this.rand.nextDouble() * 0.2d + impactDamage);
			           		entity.motionZ = entry.getKey().z * (this.rand.nextDouble() * 0.2d + impactDamage);
			           		ReflectionHelper.setPrivateValue(EntityFallingBlock.class, entity, !flag, 3);
		           		}
		           	} else {
						((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, vec.x, vec.y+0.5d, vec.z,
						 50, 0.2d, 0.2d, 0.2d, 0.2d, Block.getIdFromBlock(entry.getValue().getBlock()));
						if (flag) {
							this.world.setBlockState(pos, entry.getValue(), 3);
						}
		           	}
				}
				pos.release();
				this.blocksMap.clear();
				this.setDead();
			}
	    }

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			this.ticksAlive = compound.getInteger("ticksAlive");
			this.fallTime = compound.getInteger("fallTime");
			this.setSize(compound.getFloat("width"), compound.getFloat("height"));
			if (compound.hasKey("blocksMap", 9)) {
				NBTTagList nbttaglist = compound.getTagList("blocksMap", 10);
				this.blocksTotal = nbttaglist.tagCount();
				for (int i = 0; i < nbttaglist.tagCount(); ++i) {
					NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
					this.blocksMap.put(new Vec3d(nbttagcompound.getDouble("vecX"), nbttagcompound.getDouble("vecY"),
					  nbttagcompound.getDouble("vecZ")), Block.getStateById(nbttagcompound.getInteger("blockstate")));
				}
			}
			if (compound.hasKey("entityMap", 9)) {
				NBTTagList nbttaglist = compound.getTagList("entityMap", 10);
				for (int i = 0; i < nbttaglist.tagCount(); ++i) {
					NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
					Entity entity = ((WorldServer)this.world).getEntityFromUuid(nbttagcompound.getUniqueId("entityUUID"));
					if (entity != null) {
						this.entityMap.put(entity, new Vec3d(nbttagcompound.getDouble("vecX"), 
						  nbttagcompound.getDouble("vecY"), nbttagcompound.getDouble("vecZ")));
					}
				}
			}
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setInteger("ticksAlive", this.ticksAlive);
			compound.setInteger("fallTime", this.fallTime);
			compound.setFloat("width", this.width);
			compound.setFloat("height", this.height);
			NBTTagList nbttaglist = new NBTTagList();
			for (Map.Entry<Vec3d, IBlockState> entry : this.blocksMap.entrySet()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setDouble("vecX", entry.getKey().x);
				nbttagcompound.setDouble("vecY", entry.getKey().y);
				nbttagcompound.setDouble("vecZ", entry.getKey().z);
				nbttagcompound.setInteger("blockstate", Block.getStateId(entry.getValue()));
				nbttaglist.appendTag(nbttagcompound);
			}
			if (!nbttaglist.hasNoTags()) {
				compound.setTag("blocksMap", nbttaglist);
			}
			NBTTagList nbttaglist2 = new NBTTagList();
			for (Map.Entry<Entity, Vec3d> entry : this.entityMap.entrySet()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setUniqueId("entityUUID", entry.getKey().getUniqueID());
				nbttagcompound.setDouble("vecX", entry.getValue().x);
				nbttagcompound.setDouble("vecY", entry.getValue().y);
				nbttagcompound.setDouble("vecZ", entry.getValue().z);
				nbttaglist2.appendTag(nbttagcompound);
			}
			if (!nbttaglist2.hasNoTags()) {
				compound.setTag("entityMap", nbttaglist2);
			}
		}
	}

	public static class BlocksMoveHelper {
		private static final Predicate NOT_FALLINGBLOCK = new Predicate<Entity>() {
			public boolean apply(@Nullable Entity p) {
				return p != null && p.isEntityAlive() && !(p instanceof EntityFallingBlock);
			}};
		private List<EntityFallingBlock> toMove = Lists.<EntityFallingBlock>newArrayList();
		private AxisAlignedBB boundingBox;
		public double motionX, motionY, motionZ;
		public boolean collided;
		public boolean canMoveThrough;
		private List<Entity> collidedEntities = Lists.<Entity>newArrayList();
		private List<BlockPos> collidedBlocks = Lists.<BlockPos>newArrayList();
		private List<AxisAlignedBB> collidedAABB = Lists.<AxisAlignedBB>newArrayList();

		public BlocksMoveHelper(World worldIn, List<BlockPos> list) {
			if (list == null || list.isEmpty()) {
				return;
			}
			this.boundingBox = new AxisAlignedBB(list.get(0));
			for (BlockPos pos : list) {
				EntityFallingBlock entity = new EntityFallingBlock(worldIn, (double)pos.getX() + 0.5d, (double)pos.getY(), 
				 (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos))/* {
					public BlockPos getOrigin() {
						return (BlockPos)this.dataManager.get(this.ORIGIN);
					}
				}*/;
				entity.setNoGravity(true);
				worldIn.spawnEntity(entity);
				this.toMove.add(entity);
				double d0 = (double)pos.getX() < this.boundingBox.minX ? -1d : ((double)pos.getX() + 1d) > this.boundingBox.maxX ? 1d : 0d;
				double d1 = (double)pos.getY() < this.boundingBox.minY ? -1d : ((double)pos.getY() + 1d) > this.boundingBox.maxY ? 1d : 0d;
				double d2 = (double)pos.getZ() < this.boundingBox.minZ ? -1d : ((double)pos.getZ() + 1d) > this.boundingBox.maxZ ? 1d : 0d;
				this.boundingBox = this.boundingBox.expand(d0, d1, d2);
			}
		}

		private void someSetup() {
			double d0 = this.motionX > 0d ? this.boundingBox.minX-1d : this.motionX < 0d ? this.boundingBox.maxX+1d : (this.boundingBox.minX+(this.boundingBox.maxX-this.boundingBox.minX)/2);
			double d1 = this.motionY > 0d ? this.boundingBox.minY-1d : this.motionY < 0d ? this.boundingBox.maxY+1d : (this.boundingBox.minY+(this.boundingBox.maxY-this.boundingBox.minY)/2);
			double d2 = this.motionZ > 0d ? this.boundingBox.minZ-1d : this.motionZ < 0d ? this.boundingBox.maxZ+1d : (this.boundingBox.minZ+(this.boundingBox.maxZ-this.boundingBox.minZ)/2);
			this.toMove.sort(new ProcedureUtils.EntitySorter(d0, d1, d2));
			List<EntityFallingBlock> list = Lists.<EntityFallingBlock>newArrayList();
			Iterator<EntityFallingBlock> iter = this.toMove.iterator();
			while (iter.hasNext()) {
				EntityFallingBlock entity = iter.next();
				EntityFallingBlock entity1 = new EntityFallingBlock(entity.world)/* {
					public BlockPos getOrigin() {
						return (BlockPos)this.dataManager.get(this.ORIGIN);
					}
				}*/;
				entity1.readFromNBT(entity.writeToNBT(new NBTTagCompound()));
				//entity1.setOrigin(entity.getOrigin());
				//if (entity.isAddedToWorld()) {
					entity.world.removeEntityDangerously(entity);
				//}
				entity1.world.spawnEntity(entity1);
				list.add(entity1);
				iter.remove();
			}
			this.toMove = list;
		}

		public void move(double mX, double mY, double mZ) {
			if (!this.toMove.isEmpty()) {
				this.motionX = mX;
				this.motionY = mY;
				this.motionZ = mZ;

				// EntityFallingBlock has not removed the block yet
				if (this.toMove.get(0).fallTime == 0) {
					return;
				}

				this.someSetup();

				double dX = mX;
				double dY = mY;
				double dZ = mZ;
				this.collidedBlocks.clear();
				this.collidedAABB.clear();
				for (EntityFallingBlock entity : this.toMove) {
					AxisAlignedBB aabb = entity.getEntityBoundingBox().expand(mX, mY, mZ);
					List<AxisAlignedBB> list1 = entity.world.getCollisionBoxes(null, aabb);
					ProcedureUtils.CollisionHelper stat = new ProcedureUtils.CollisionHelper(entity);
					stat.collideWithAABBs(list1, mX, mY, mZ);
					this.addToCollidedBlocks(this.convert2BlockposList(list1));
					dX = stat.minX(dX);
					dY = stat.minY(dY);
					dZ = stat.minZ(dZ);
					
					List<Entity> list2 = entity.world.getEntitiesWithinAABB(EntityFallingBlock.class, aabb, new Predicate<Entity>() {
						public boolean apply(@Nullable Entity p_apply_1_) {
							return p_apply_1_ != null && !BlocksMoveHelper.this.toMove.contains(p_apply_1_);
						}
					});
					ProcedureUtils.CollisionHelper stat2 = new ProcedureUtils.CollisionHelper(entity);
					List<AxisAlignedBB> list3 = this.convert2BoundingboxList(list2);
					stat2.collideWithAABBs(list3, mX, mY, mZ);
					this.addToCollidedAABB(list3);
					dX = stat2.minX(dX);
					dY = stat2.minY(dY);
					dZ = stat2.minZ(dZ);
				}

				for (EntityFallingBlock entity : this.toMove) {
					entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(dX, dY, dZ));
					entity.resetPositionToBB();
					List<Entity> list2 = entity.world.getEntitiesInAABBexcluding(null, entity.getEntityBoundingBox(), NOT_FALLINGBLOCK);
					for (Entity entity1 : list2) {
						this.collideWithEntity(entity, entity1, new Vec3d(dX, dY, dZ));
					}
					this.collidedEntities.addAll(list2);
				}

				this.boundingBox = this.boundingBox.offset(dX, dY, dZ);
				this.collided = mX != dX || mY != dY || mZ != dZ;
				this.canMoveThrough = true;
				if (this.collided) {
					this.canMoveThrough = this.collidedBlocks.size() + this.collidedAABB.size() < (this.toMove.size() / 5);
				}
//System.out.println("--- collidedBlocks="+this.collidedBlocks.size()+", collidedAABB="+this.collidedAABB.size()+", size="+this.toMove.size()+", "+this.toString());
			}
		}

		public float destroyHardness() {
			return MathHelper.clamp(MathHelper.sqrt((float)this.toMove.size()), 0.5f, 50f);
		}

		public double velocity() {
			return new Vec3d(this.motionX, this.motionY, this.motionZ).lengthVector();
		}

		public int mass() {
			return this.toMove.size();
		}

		public float collisionForce() {
			return (float)(this.velocity() * (double)this.mass());
		}

		@Nullable
		public static EnumFacing getCollidingSide(AxisAlignedBB aabb1, AxisAlignedBB aabb2) {
			if (aabb1.intersects(aabb2)) {
				AxisAlignedBB aabbI = aabb1.intersect(aabb2);
				return aabbI.minX == aabb1.minX ? EnumFacing.WEST
				     : aabbI.maxX == aabb1.maxX ? EnumFacing.EAST
				     : aabbI.minZ == aabb1.minZ ? EnumFacing.NORTH
				     : aabbI.maxZ == aabb1.maxZ ? EnumFacing.SOUTH
				     : aabbI.minY == aabb1.minY ? EnumFacing.DOWN
				     : aabbI.maxY == aabb1.maxY ? EnumFacing.UP
				     : null;
			}
			return null;
		}

		public static EnumFacing collideWithEntity(Entity entity, Entity target, Vec3d entityMotion) {
        	AxisAlignedBB bb = entity.getEntityBoundingBox();
            EnumFacing hitFace = getCollidingSide(bb, target.getEntityBoundingBox());
	        if (hitFace == EnumFacing.UP && target.motionY - entityMotion.y < 0d) {
                target.setPosition(target.posX, bb.maxY, target.posZ);
                ProcedureUtils.setVelocity(target, target.motionX, 0d, target.motionZ);
                target.onGround = true;
	        } else if (hitFace == EnumFacing.WEST) {
		       	double d = (entityMotion.x - target.motionX) * 0.1d;
	        	target.setPosition(bb.minX - target.width / 2 + d, target.posY, target.posZ);
	        	ProcedureUtils.setVelocity(target, d, target.motionY, target.motionZ);
		    } else if (hitFace == EnumFacing.EAST) {
		    	double d = (entityMotion.x - target.motionX) * 0.1d;
	        	target.setPosition(bb.maxX + target.width / 2 + d, target.posY, target.posZ);
	        	ProcedureUtils.setVelocity(target, d, target.motionY, target.motionZ);
	        } else if (hitFace == EnumFacing.NORTH) {
	        	double d = (entityMotion.z - target.motionZ) * 0.1d;
	        	target.setPosition(target.posX, target.posY, bb.minZ - target.width / 2 + d);
	        	ProcedureUtils.setVelocity(target, target.motionX, target.motionY, d);
        	} else if (hitFace == EnumFacing.SOUTH) {
        		double d = (entityMotion.z - target.motionZ) * 0.1d;
        		target.setPosition(target.posX, target.posY, bb.maxZ + target.width / 2 + d);
        		ProcedureUtils.setVelocity(target, target.motionX, target.motionY, d);
        	} else if (hitFace == EnumFacing.DOWN && !target.onGround) {
        		double d = (entityMotion.y - target.motionY) * 0.8d;
        		target.setPosition(target.posX, bb.minY - target.height + d, target.posZ);
        		ProcedureUtils.setVelocity(target, target.motionX, d, target.motionZ);
        	}
			return hitFace;
		}

		public static EnumFacing collideWithEntity(Entity entity, Entity target) {
			return collideWithEntity(entity, target, ProcedureUtils.getMotion(entity));
		}

		/*public static List<EnumFacing> getCollidingSides(AxisAlignedBB aabb1, AxisAlignedBB aabb2) {
			List<EnumFacing> list = Lists.newArrayList();
			if (aabb1.intersects(aabb2)) {
				AxisAlignedBB aabbI = aabb1.intersect(aabb2);
				if (aabbI.minX == aabb1.minX) {
					list.add(EnumFacing.WEST);
				}
				if (aabbI.maxX == aabb1.maxX) {
					list.add(EnumFacing.EAST);
				}
				if (aabbI.minZ == aabb1.minZ) {
					list.add(EnumFacing.NORTH);
				}
				if (aabbI.maxZ == aabb1.maxZ) {
					list.add(EnumFacing.SOUTH);
				}
				if (aabbI.minY == aabb1.minY) {
					list.add(EnumFacing.DOWN);
				}
				if (aabbI.maxY == aabb1.maxY) {
					list.add(EnumFacing.UP);
				}
			}
			return list;
		}*/

		public void fall() {
			if (!this.toMove.isEmpty()) {
				//EntityFallingBlock entity = this.toMove.get(0);
				for (int i = this.toMove.size() - 1; i >= 0; i--) {
					this.toMove.get(i).setNoGravity(false);
				}
				//this. move(entity.motionX, entity
			}
		}

		public AxisAlignedBB getBoundingBox() {
			return this.boundingBox;
		}

		public List<Entity> getCollidedEntities() {
			return this.collidedEntities;
		}

		public List<BlockPos> getCollidedBlocks() {
			return this.collidedBlocks;
		}

		public static float getBlocksTotalResistance(World world, List<BlockPos> list) {
			float resistance = 0f;
			for (BlockPos pos : list) {
				resistance += world.getBlockState(pos).getBlock().getExplosionResistance(null);
			}
			return resistance;
		}

		public static List<BlockPos> convert2BlockposList(List<AxisAlignedBB> list) {
			List<BlockPos> newlist = Lists.<BlockPos>newArrayList();
			for (AxisAlignedBB aabb : list) {
				newlist.add(new BlockPos(ProcedureUtils.BB.getCenter(aabb)));
			}
			return newlist;
		}

		public static List<AxisAlignedBB> convert2BoundingboxList(List<Entity> list) {
			List<AxisAlignedBB> newlist = Lists.<AxisAlignedBB>newArrayList();
			for (Entity entity : list) {
				newlist.add(entity.getEntityBoundingBox());
			}
			return newlist;
		}

		private void addToCollidedBlocks(List<BlockPos> list) {
			for (BlockPos pos : list) {
				boolean flag = false;
				for (BlockPos pos2 : this.collidedBlocks) {
					if (pos.equals(pos2)) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					this.collidedBlocks.add(pos);
				}
			}
		}

		private void addToCollidedAABB(List<AxisAlignedBB> list) {
			for (AxisAlignedBB aabb : list) {
				boolean flag = false;
				for (AxisAlignedBB aabb1 : this.collidedAABB) {
					if (aabb.equals(aabb1)) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					this.collidedAABB.add(aabb);
				}
			}
		}

		public String toString() {
			return "-- " 
			 +"collided:"+this.collided
			 //+"cH:"+this.collidedHorizontally+", cV:"+this.collidedVertically+", oG:"+this.onGround
			 +", cMT:"+this.canMoveThrough+", motion:("+this.motionX+","+this.motionY+","+this.motionZ
			 +"), bb:"+this.boundingBox;
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderEarthBlocks extends Render<Base> {
	    public RenderEarthBlocks(RenderManager renderManagerIn) {
	        super(renderManagerIn);
	    }
	
		private void renderBlock(Base entity, IBlockState iblockstate, Vec3d blockvec, double x, double y, double z) {
			if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL) {
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
                GlStateManager.translate(x + blockvec.x - 0.5d, y + blockvec.y, z + blockvec.z - 0.5d);
                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                blockrendererdispatcher.getBlockModelRenderer().renderModel(entity.world, 
                  blockrendererdispatcher.getModelForState(iblockstate), iblockstate, BlockPos.ORIGIN, bufferbuilder, false);
                tessellator.draw();
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
	        }
		}

		private boolean isFullySurrounded(Base entity, Vec3d vec, Vec3d viewer) {
			Vec3d vec1 = vec.add(entity.getPositionVector());
			return (viewer.y <= vec1.y || entity.blocksMap.containsKey(vec.addVector(0d, 1d, 0d))) 
			    && (viewer.y >= vec1.y || entity.blocksMap.containsKey(vec.addVector(0d, -1d, 0d)))
			    && (viewer.x <= vec1.x || entity.blocksMap.containsKey(vec.addVector(1d, 0d, 0d)))
			    && (viewer.x >= vec1.x || entity.blocksMap.containsKey(vec.addVector(-1d, 0d, 0d)))
			    && (viewer.z <= vec1.z || entity.blocksMap.containsKey(vec.addVector(0d, 0d, 1d)))
			    && (viewer.z >= vec1.z || entity.blocksMap.containsKey(vec.addVector(0d, 0d, -1d)));
		}
		
		@Override
	    public void doRender(Base entity, double x, double y, double z, float entityYaw, float partialTicks) {
	        if (!entity.blocksMap.isEmpty() && entity.blocksMap.size() == entity.blocksTotal) {
		        this.shadowSize = 0.8f * entity.width;
                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                EntityPlayer viewer = Minecraft.getMinecraft().player;
                if (viewer.getDistance(entity) < (double)entity.width + 2d) {
		        	for (Map.Entry<Vec3d, IBlockState> entry : entity.blocksMap.entrySet()) {
		            	this.renderBlock(entity, entry.getValue(), entry.getKey(), x, y, z);
		        	}
                } else {
                	Vec3d viewereyes = viewer.getPositionEyes(1f);
		        	for (Map.Entry<Vec3d, IBlockState> entry : entity.blocksMap.entrySet()) {
		        		if (!this.isFullySurrounded(entity, entry.getKey(), viewereyes)) {
		            		this.renderBlock(entity, entry.getValue(), entry.getKey(), x, y, z);
		        		}
		        	}
                }
	        }
	    }

		@Override
	    protected ResourceLocation getEntityTexture(Base entity) {
	        return TextureMap.LOCATION_BLOCKS_TEXTURE;
	    }
	}
}
