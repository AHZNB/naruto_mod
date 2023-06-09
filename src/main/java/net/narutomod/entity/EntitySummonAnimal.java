
package net.narutomod.entity;

//import net.minecraftforge.fml.relauncher.SideOnly;
//import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.item.Item;
import net.minecraft.block.material.Material;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNodeType;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.List;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySummonAnimal extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 364;
	public static final int ENTITYID_RANGED = 365;

	public EntitySummonAnimal(ElementsNarutomodMod instance) {
		super(instance, 726);
	}

	public static abstract class Base extends EntityCreature {
		protected static final List<Material> canBreakList = Lists.newArrayList(Material.WOOD, Material.CACTUS,
		 Material.GLASS, Material.LEAVES, Material.PLANTS, Material.SNOW, Material.VINE, Material.WEB);
		protected static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(Base.class, DataSerializers.FLOAT);
		protected static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(Base.class, DataSerializers.OPTIONAL_UNIQUE_ID);
		protected int ageTicks;
		protected int lifeSpan = 1200;
		protected float ogWidth;
		protected float ogHeight;

		public Base(World w) {
			super(w);
			//this.dontWander(false);
			this.enablePersistence();
		}

		public Base(EntityLivingBase summonerIn) {
			this(summonerIn.world);
			//this.postScaleFixup();
			this.setSummoner(summonerIn);
			this.dontWander(true);
			this.enablePersistence();
		}

		@Override
		public void entityInit() {
			super.entityInit();
			this.getDataManager().register(SCALE, Float.valueOf(1.0f));
			this.getDataManager().register(AGE, Integer.valueOf(0));
			this.getDataManager().register(OWNER_UNIQUE_ID, Optional.absent());
		}

		public void setScale(float f) {
			this.getDataManager().set(SCALE, Float.valueOf(f));
			this.postScaleFixup();
		}

		public float getScale() {
			return ((Float)this.getDataManager().get(SCALE)).floatValue();
		}

		private void setAge(int age) {
			this.getDataManager().set(AGE, Integer.valueOf(age));
			this.ageTicks = age;
		}

		public int getAge() {
			return this.world.isRemote ? ((Integer)this.getDataManager().get(AGE)).intValue() : this.ageTicks;
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (SCALE.equals(key) && this.world.isRemote) {
				float scale = this.getScale();
				this.setSize(this.ogWidth * scale, this.ogHeight * scale);
			}
		}

	    @Nullable
	    public UUID getOwnerId() {
	        return (UUID)((Optional)this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
	    }
	
	    public void setOwnerId(@Nullable UUID p_184754_1_) {
	        this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
	    }

	    public boolean hasSummoner() {
	    	return this.getOwnerId() != null;
	    }

		protected void setOGSize(float width, float height) {
			this.ogWidth = width;
			this.ogHeight = height;
			this.setSize(width, height);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(ProcedureUtils.MAXHEALTH);
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		}

		@Override
		public IAttributeInstance getEntityAttribute(IAttribute attribute) {
			return super.getEntityAttribute(attribute == SharedMonsterAttributes.MAX_HEALTH ? ProcedureUtils.MAXHEALTH : attribute);
		}

		protected void postScaleFixup() {
			float f = this.getScale();
			float f1 = this.ogHeight * f;
			this.setSize(this.ogWidth * f, f1);
			if (f1 > 2.0f) {
				this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(f1 - 2.0f);
			}
			this.setHealth(this.getMaxHealth());
		}

	    public void setSummoner(EntityLivingBase player) {
	        this.setOwnerId(player.getUniqueID());
	    }
	
	    @Nullable
	    public EntityLivingBase getSummoner() {
	        UUID uuid = this.getOwnerId();
	        if (uuid == null) {
	        	return null;
	        } else {
	        	Entity entity = ProcedureUtils.getEntityFromUUID(this.world, uuid);
	        	if (entity instanceof EntityLivingBase) {
	        		return (EntityLivingBase)entity;
	        	}
		        return null;
	        }
	    }

	    public boolean isSummoner(Entity entityIn) {
	        return entityIn.getUniqueID().equals(this.getOwnerId());
	    }

	    @Override
	    public boolean isOnSameTeam(Entity entityIn) {
	    	return this.isSummoner(entityIn) || super.isOnSameTeam(entityIn);
	    }

		@Override
		protected boolean canDespawn() {
			return false;
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			EntityLivingBase owner = this.getSummoner();
			if (owner != null) {
				EntityLivingBase target = owner.getRevengeTarget();
				if (target == null) {
					target = owner.getLastAttackedEntity();
				}
				if (target != null && !target.equals(this)) {
					this.setAttackTarget(target);
				} 
				target = this.getAttackTarget();
				if (target != null) {
					if (!target.isEntityAlive()) {
						this.setAttackTarget(null);
					}
				/*} else if (owner.getEntityData().hasKey(ItemSummoningContract.SUMMON_RALLY)) {
					int[] ia = owner.getEntityData().getIntArray(ItemSummoningContract.SUMMON_RALLY);
					if (ia.length == 3) {
						BlockPos pos = new BlockPos(ia[0], ia[1], ia[2]);
						this. getNavigator().
					}*/
				}
			}
		}

		protected abstract void dontWander(boolean set);

		@Override
		public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			if (!this.world.isRemote && this.isSummoner(entity)) {
				return this.canSitOnShoulder() ? this.startRiding(entity) : entity.startRiding(this);
			}
			return super.processInteract(entity, hand);
		}

		public boolean canSitOnShoulder() {
			return false;
		}

		@Override
		public boolean shouldDismountInWater(Entity rider) {
			return false;
		}

		@Nullable
		public Entity getControllingPassenger() {
			return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
		}

		@Override
		public boolean startRiding(Entity entityIn) {
			if (entityIn instanceof EntityPlayer && this.world instanceof WorldServer) {
				if (entityIn.getPassengers().size() < 2 && this.startRiding(entityIn, true)) {
					((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketSetPassengers(entityIn));
					return true;
				}
				return false;
			}
			return super.startRiding(entityIn);
		}

		@Override
		public void setPosition(double x, double y, double z) {
			if (this.getRidingEntity() instanceof EntityPlayer) {
				EntityLivingBase riding = (EntityLivingBase)this.getRidingEntity();
				Vec3d vec[] = { new Vec3d(0.4d, riding.getMountedYOffset(), 0.0d), new Vec3d(-0.4d, riding.getMountedYOffset(), 0.0d) };
				Vec3d vec1 = vec[riding.getPassengers().indexOf(this)]
				 .rotateYaw(-riding.renderYawOffset * 0.017453292F).add(riding.getPositionVector());
				x = vec1.x;
				y = vec1.y;
				z = vec1.z;
			}
			super.setPosition(x, y, z);
		}

		public boolean couldBreakBlocks() {
			return false;
		}

		@Override
		public void move(MoverType type, double x, double y, double z) {
			if (this.couldBreakBlocks()) {
				for (BlockPos pos : ProcedureUtils.getNonAirBlocks(this.world, this.getEntityBoundingBox().grow(0.5d).expand(x, y, z), true)) {
					if (canBreakList.contains(this.world.getBlockState(pos).getMaterial())) {
						this.world.destroyBlock(pos, this.rand.nextFloat() < 0.3f);
						x *= 0.98d;
						y *= 0.98d;
						z *= 0.98d;
					}
				}
			}
			super.move(type, x, y, z);
		}

		@Override
		public void travel(float ti, float tj, float tk) {
			if (this.isRiding()) {
				Entity entity = this.getRidingEntity();
				float f = this.rotationYawHead - this.rotationYaw;
				this.rotationYaw = entity.rotationYaw;
				this.rotationPitch = entity.rotationPitch;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.rotationYawHead = MathHelper.wrapDegrees(this.rotationYaw + f);
				this.renderYawOffset = entity.rotationYaw;
				super.travel(0.0F, 0.0F, 0.0F);
			} else {
				super.travel(ti, tj, tk);
			}
		}

		@Override
		public Vec3d getLookVec() {
			return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			return ProcedureUtils.attackEntityAsMob(this, entityIn);
		}

		@Override
		public boolean canBePushed() {
			return super.canBePushed() && this.getScale() < 4.0f;
		}

		@Override
		protected boolean canFitPassenger(Entity passenger) {
			EntityLivingBase owner = this.getSummoner();
			return this.getScale() >= 4.0f && this.getPassengers().size() < 1
			 && owner != null && (passenger.equals(owner) || passenger.isOnSameTeam(owner));
		}

		@Override
		protected void collideWithEntity(Entity entityIn) {
			this.applyEntityCollision(entityIn);
		}

		@Override
		public void applyEntityCollision(Entity entity) {
			if (this.getScale() < 4.0f) {
				super.applyEntityCollision(entity);
			} else if (!this.isRidingSameEntity(entity) && !entity.noClip && !entity.isBeingRidden()) {
				double d2 = entity.posX - this.posX;
				double d3 = entity.posZ - this.posZ;
				double d4 = MathHelper.absMax(d2, d3);
				if (d4 >= 0.01D) {
					d4 = (double)MathHelper.sqrt(d4);
					d2 /= d4;
					d3 /= d4;
					double d5 = d4 >= 1.0D ? 1.0D / d4 : 1.0D;
					d2 *= d5 * 0.05d;
					d3 *= d5 * 0.05d;
					entity.motionX = d2;
					entity.motionZ = d3;
					entity.isAirBorne = true;
                }
			}
		}

		@Override
		public boolean canBeLeashedTo(EntityPlayer player) {
			return player == this.getSummoner() && !this.getLeashed();
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:poof")), 2.0F, 1.0F);
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY+this.height/2, this.posZ,
				 300, this.width * 0.5d, this.height * 0.3d, this.width * 0.5d, 0d, 0d, 0d, 0xD0FFFFFF,
				 15 + (int)(this.getScale() * 5) + this.rand.nextInt(16));
			}
		}
		
		@Override
		protected void onDeathUpdate() {
			this.setDead();
		}

		@Override
		public void onLivingUpdate() {
			this.updateArmSwingProgress();
			super.onLivingUpdate();
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			int age = this.getAge();
			if (!this.world.isRemote) {
				EntityLivingBase owner = this.getSummoner();
				if (owner != null && owner.getHealth() <= 0.0f) {
					this.onDeathUpdate();
				}
				if (this instanceof IMob && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
					this.setDead();
				}
				if (age > this.lifeSpan) {
					this.onDeathUpdate();
				}
			}
			//if (this.isRiding()) {
			//	this.rotationYaw = this.getRidingEntity().rotationYaw;
			//}
			this.setAge(age + 1);
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setAge(compound.getInteger("ageTicks"));
			this.lifeSpan = compound.getInteger("lifeSpan");
			this.setScale(compound.getFloat("scale"));
			String s = compound.getString("OwnerUUID");
			if (!s.isEmpty()) {
				this.setOwnerId(UUID.fromString(s));
			}
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setInteger("ageTicks", this.getAge());
			compound.setInteger("lifeSpan", this.lifeSpan);
			compound.setFloat("scale", this.getScale());
			compound.setString("OwnerUUID", this.getOwnerId() == null ? "" : this.getOwnerId().toString());
		}
	}

	public static class NavigateGround extends PathNavigateGround {
		private Base navigatingEntity;

		public NavigateGround(Base entityLivingIn, World worldIn) {
			super(entityLivingIn, worldIn);
			this.navigatingEntity = entityLivingIn;
		}

		@Override
		protected PathFinder getPathFinder() {
			this.nodeProcessor = new WalkNodeProcessor() {
				@Override
				protected PathNodeType getPathNodeTypeRaw(IBlockAccess world, int x, int y, int z) {
					PathNodeType nodetype = super.getPathNodeTypeRaw(world, x, y, z);
					if (nodetype != PathNodeType.OPEN && NavigateGround.this.navigatingEntity.couldBreakBlocks()
					 && Base.canBreakList.contains(world.getBlockState(new BlockPos(x, y, z)).getMaterial())) {
						return PathNodeType.OPEN;
					}
					return nodetype;
				}
			};
			return new PathFinder(this.nodeProcessor);
		}
	}
}
