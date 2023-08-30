
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.Particles;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import java.util.List;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import com.google.common.base.Predicates;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Constructor;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySnake extends ElementsNarutomodMod.ModElement {
	public EntitySnake(ElementsNarutomodMod instance) {
		super(instance, 686);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(ServerMessage.Handler.class, ServerMessage.class, Side.CLIENT);
		//this.elements.addNetworkMessage(ReplyMessage.Handler.class, ReplyMessage.class, Side.SERVER);
	}

	public static abstract class EntityCustom extends EntitySummonAnimal.Base implements IEntityMultiPart {
		private static final DataParameter<Integer> PHASE = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private SnakeSegment[] parts = new SnakeSegment[22];
		private List<ProcedureUtils.Vec2f> partRot = Lists.newArrayList();
		private final PathNavigate altNavigator;
		private final PathNavigate mainNavigator;
		private final EntityMoveHelper altMoveHelper;
		private final EntityMoveHelper mainMoveHelper;
		private final PhaseManager phaseManager;
		private boolean needsSync;
		private boolean prevOnGround;
		private boolean defensive;
		private final EntityAIWander wanderAI = new EntityAIWander(this, 0.8d, 10) {
			@Override
			public boolean shouldExecute() {
				Phase phase = EntityCustom.this.getPhaseManager().getPhase().getType();
				return phase != Phase.DEFENSIVE && phase != Phase.RIDING && super.shouldExecute();
			}
			@Override
			@Nullable
			protected Vec3d getPosition() {
				float f = EntityCustom.this.getScale();
				Vec3d vec = this.entity.getPositionVector();
				while (vec != null && vec.distanceTo(this.entity.getPositionVector()) < 4.0d + f) {
					vec = RandomPositionGenerator.findRandomTarget(this.entity, 4 + (int)(f * 3), 6 + (int)f);
				}
				return vec;
			}
		};
		
		public EntityCustom(World worldIn) {
			super(worldIn);
			this.setOGSize(0.3f, 0.25f);
			this.isImmuneToFire = false;
			this.setNoAI(!true);
			this.dontWander(false);
			//this.enablePersistence();
			this.ignoreFrustumCheck = true;
			this.mainNavigator = this.navigator;
			this.altNavigator = new NavigateSwim(this, worldIn);
			this.moveHelper = new MoveHelper(this);
			this.mainMoveHelper = this.moveHelper;
			this.altMoveHelper = new EntityNinjaMob.SwimHelper(this);
			this.prevRotationYawHead = this.prevRotationYaw = this.rotationYawHead = this.rotationYaw = 0.0f;
			this.phaseManager = new PhaseManager(this);
			float f = this.getScale();
			this.parts[0] = new SnakeSegment(this, "head", this.ogWidth * f, this.ogWidth * f);
			for (int i = 1; i < this.parts.length; i++) {
				this.parts[i] = new SnakeSegment(this, "segment"+i, this.ogHeight * f, this.ogHeight * f);
				this.partRot.add(ProcedureUtils.Vec2f.ZERO);
			}
		}

		public EntityCustom(EntityLivingBase summonerIn) {
			this(summonerIn.world);
			this.setSummoner(summonerIn);
			this.phaseManager.setPhase(Phase.DEFENSIVE);
			this.dontWander(true);
		}

		@Override
		public void entityInit() {
			super.entityInit();
			this.getDataManager().register(PHASE, Integer.valueOf(Phase.DEFENSIVE.getID()));
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (SCALE.equals(key) && this.world.isRemote) {
				this.resizeSegments(this.getScale());
			} else if (PHASE.equals(key) && this.world.isRemote) {
				this.phaseManager.setPhase(this.getPhase());
			}
		}

		private Phase getPhase() {
			return Phase.getPhaseFromId(((Integer)this.getDataManager().get(PHASE)).intValue());
		}

		private void setPhase(Phase phase) {
			this.getDataManager().set(PHASE, Integer.valueOf(phase.getID()));
		}

		protected PhaseManager getPhaseManager() {
			return this.phaseManager;
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			PathNavigateGround navi = new Navigate(this, worldIn);
			navi.setCanSwim(true);
			return navi;
		}

		@Override
		public Entity[] getParts() {
			return this.parts;
		}

		@Override
		public SoundEvent getAmbientSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:snake_hiss"));
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return SoundEvents.ENTITY_GENERIC_HURT;
		}

		@Override
		public SoundEvent getDeathSound() {
			return SoundEvents.ENTITY_GENERIC_DEATH;
		}

	    @Override
	    protected void playStepSound(BlockPos pos, Block blockIn) {
	        this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.1F * this.getScale(), this.rand.nextFloat() * 0.4f + 0.9f);
	    }

	    @Override
	    protected SoundEvent getSwimSound() {
	    	return SoundEvents.BLOCK_WATER_AMBIENT;
	    }

		@Override
		protected float getSoundVolume() {
			return this.getScale() * 0.2f;
		}

		protected void postScaleFixup() {
			float f = this.getScale();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5D * f);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D + f * 0.05);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8D * f * f);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.3333D * f);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(13D + 3D * f);
			super.postScaleFixup();
			//this.setSize(this.ogWidth * f * 1.2f, this.ogHeight * f);
			this.resizeSegments(f);
			//this.setHealth(this.getMaxHealth());
			this.experienceValue = (int)(f * 10);
		}

		private void resizeSegments(float scale) {
			this.parts[0].setSize(this.ogWidth * scale, this.ogWidth * scale);
			for (int i = 0; i < this.parts.length; i++) {
				this.parts[i].setSize(this.ogHeight * scale, this.ogHeight * scale);
			}
		}

		@Override
		public boolean isPushedByWater() {
			return false;
		}

		@Override
		public boolean canBreatheUnderwater() {
			return true;
		}

		@Override
		protected float getWaterSlowDown() {
			return 1.0F;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			//this.tasks.addTask(1, new EntityAISwimming(this));
			this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0d, false));
			this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 5.0f, 0.1f) {
				@Override
				public boolean shouldExecute() {
					this.maxDistanceForPlayer = 3.8f + 4.0f * this.entity.width;
					if (!this.entity.isRiding() && this.entity.getAttackTarget() == null && this.entity.getRNG().nextFloat() < 0.1f) {
						this.closestEntity = this.entity.world.getClosestPlayer(this.entity.posX, this.entity.posY,
						 this.entity.posZ, (double)this.maxDistanceForPlayer,
						 Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.notRiding(this.entity)));
						return this.closestEntity != null;
					}
					return false;
				}
				@Override
				public boolean shouldContinueExecuting() {
					return !this.entity.isRiding() && this.closestEntity.isEntityAlive()
					 && this.entity.getDistanceSq(this.closestEntity) <= (double)(this.maxDistanceForPlayer * this.maxDistanceForPlayer);
				}
				@Override
				public void startExecuting() {
					//ReflectionHelper.setPrivateValue(EntityAIWatchClosest.class, this, 100 + this.entity.getRNG().nextInt(40), 3);
					EntityCustom.this.defensive = true;
				}
				@Override
				public void resetTask() {
					super.resetTask();
					EntityCustom.this.defensive = false;
				}
			});
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		}

		@Override
		protected void dontWander(boolean dont) {
			if (dont) {
				this.tasks.removeTask(this.wanderAI);
			} else {
				this.tasks.addTask(4, this.wanderAI);
			}
		}

		@Override
		protected void updateAITasks() {
			if (this.isInWater() && this.navigator != this.altNavigator) {
				this.navigator = this.altNavigator;
				//this.moveHelper = this.altMoveHelper;
			} else if (!this.isInWater() && this.navigator != this.mainNavigator) {
				this.navigator = this.mainNavigator;
				//this.moveHelper = this.mainMoveHelper;
			}
			//this.phaseManager.setPhase(this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() 
			// ? Phase.AGGRESIVE : this.defensive ? Phase.DEFENSIVE : Phase.ROAMING);
			this.phaseManager.setPhase(this.ageTicks < 20 ? Phase.DEFENSIVE
			 : this.isRiding() ? Phase.RIDING
			 : this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() 
			 ? Phase.AGGRESIVE : this.defensive && !this.isInWater() ? Phase.DEFENSIVE : Phase.ROAMING);
			
			super.updateAITasks();
		}

		@Override
		public boolean couldBreakBlocks() {
			return this.world.getGameRules().getBoolean("mobGriefing") && this.getScale() >= 4f;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.syncParts();
			this.phaseManager.getPhase().onUpdate();

			this.parts[0].onUpdate();
			Vec3d vec2 = Vec3d.ZERO;
			float f = this.getScale() * 0.0625f;
			float f0 = MathHelper.wrapDegrees((this.rotationYawHead - this.renderYawOffset) * 0.5F + this.renderYawOffset) * 0.017453292F;
			float f0a = MathHelper.wrapDegrees(this.rotationYawHead) * 0.017453292F;
			float f1 = this.rotationPitch * 0.017453292F;
			Vec3d vec3 = getOffsetPoint((float)vec2.x, (float)vec2.y, (float)vec2.z, f1 == 0.0f ? -0.2618f : f1 * 0.5f, -f0, 5.0f * f);
			vec3 = getOffsetPoint((float)vec3.x, (float)vec3.y, (float)vec3.z, f1 == 0.0f ? 0.2618f : f1, -f0a, 3.0f * f)
			 .add(this.getPositionVector()).addVector(0d, this.phaseManager.getPhase().getYOffset() * f, 0d);
			this.parts[0].setLocationAndAngles(vec3.x, vec3.y, vec3.z, this.rotationYawHead, this.rotationPitch);
			
			f0 = this.renderYawOffset;
			for (int i = 1; i < this.parts.length; i++) {
				this.parts[i].onUpdate();
				ProcedureUtils.Vec2f pr = this.partRot.get(i-1);
				f0 = MathHelper.wrapDegrees(f0 - pr.x);
				f1 = pr.y * 0.017453292F;
				vec3 = getOffsetPoint((float)vec2.x, (float)vec2.y, (float)vec2.z, -f1, -f0 * 0.017453292F, -2.0f * f)
				 .add(this.getPositionVector()).addVector(0d, this.phaseManager.getPhase().getYOffset() * f, 0d);
				this.parts[i].setLocationAndAngles(vec3.x, vec3.y, vec3.z, pr.x, pr.y);
				this.collideWithEntities(this.parts[i].getEntityBoundingBox());
				vec2 = getOffsetPoint((float)vec2.x, (float)vec2.y, (float)vec2.z, -f1, -f0 * 0.017453292F, -4.0f * f);
			}
		}

		private ProcedureUtils.Vec2f getMovementOffsets(int index, float partialTicks) {
			index %= this.parts.length;
			ProcedureUtils.Vec2f vec = new ProcedureUtils.Vec2f(this.parts[index].prevRotationYaw, this.parts[index].prevRotationPitch);
			return new ProcedureUtils.Vec2f(this.parts[index].rotationYaw, this.parts[index].rotationPitch)
			 .subtract(vec).scale(partialTicks).add(vec);
		}

		private float yOffset2Pitch(float yoffset, float xlen) {
			return (float)Math.asin(MathHelper.clamp(yoffset / xlen, -1.0F, 1.0F)) * 180.0f / (float)Math.PI;
		}

		@Override
		public boolean canSitOnShoulder() {
			return this.getScale() <= 1.0f;
		}
		
		private void collideWithEntities(AxisAlignedBB boundingbox) {
			double d0 = (boundingbox.minX + boundingbox.maxX) / 2.0D;
			double d1 = (boundingbox.minZ + boundingbox.maxZ) / 2.0D;
			for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, boundingbox)) {
				if (!entity.equals(this)) {
					this.applyEntityCollision(d0, d1, entity);
				}
			}
		}

		@Override
		public boolean canBePushed() {
			return false;
		}

		@Override
		protected void collideWithEntity(Entity entityIn) {
			this.applyEntityCollision(this.posX, this.posZ, entityIn);
		}

		private void applyEntityCollision(double centerX, double centerZ, Entity entity) {
			if (!this.isRidingSameEntity(entity) && !entity.noClip && !entity.isBeingRidden()) {
				double d2 = entity.posX - centerX;
				double d3 = entity.posZ - centerZ;
				double d4 = MathHelper.absMax(d2, d3);
				if (d4 >= 0.01D) {
					d4 = (double)MathHelper.sqrt(d4);
					d2 /= d4;
					d3 /= d4;
					double d5 = d4 >= 1.0D ? 1.0D / d4 : 1.0D;
					d2 *= d5 * 0.05d;
					d3 *= d5 * 0.05d;
					//entity.addVelocity(d2, 0.0D, d3);
					entity.motionX = d2;
					entity.motionZ = d3;
					entity.isAirBorne = true;
                }
			}
		}
		
		@Override
		public void applyEntityCollision(Entity entityIn) {
			this.applyEntityCollision(this.posX, this.posZ, entityIn);
		}
		
		@Override
		public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
			int i = part.partName.equals("head") ? 0
			 : part.partName.substring(0, 7).equals("segment") ? Integer.decode(part.partName.substring(7)) : this.parts.length;
			return this.attackEntityFrom(source, damage * (1.0f - (float)i / this.parts.length));
		}

		@Override
		public void fall(float distance, float damageMultiplier) {
			super.fall(distance / this.getScale(), damageMultiplier);
		}

		//@Override
		//public boolean isOnLadder() {
		//	return this.collidedHorizontally;
		//}

		@Override
		public World getWorld() {
			return this.world;
		}

		private void syncParts() {
			if (!this.world.isRemote) {
				if (this.needsSync) {
					ServerMessage.sendToTracking(this);
					this.needsSync = false;
				}
				if (this.prevOnGround != this.onGround) {
					ProcedureSync.EntityState.sendToTracking(this);
					this.prevOnGround = this.onGround;
				}
				if (this.ticksExisted == 1) {
					for (int i = 0; i < this.parts.length; i++) {
						this.parts[i].setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0f, 0.0f);
					}
				}
			}
		}

		@Override
		public float getEyeHeight() {
			return super.getEyeHeight() + (float)this.phaseManager.getPhase().getYOffset() * this.getScale() * 0.0625f;
		}

		@Override
		public double getMountedYOffset() {
			return 0.34375d * this.getScale();
		}

		@Override
		public boolean shouldRiderSit() {
			return false;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			float f = this.getScale();
			/*Vec3d vec[] = { new Vec3d(0.0d, 0.0d, 0.5d * f) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-(this.rotationYawHead - this.renderYawOffset) * 0.017453292F)
				 .rotateYaw(-this.renderYawOffset * 0.017453292F).add(this.getPositionVector());
				passenger.setPosition(vec2.x, vec2.y + this.getMountedYOffset(), vec2.z);
			}*/
			if (this.isPassenger(passenger)) {
				Vec3d vec2 = this.parts[0].getPositionVector().addVector(0d, 0.0625d * f + this.parts[0].height, 0d);
				passenger.setPosition(vec2.x, vec2.y, vec2.z);
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				for (int i = 0; i < this.parts.length; i++) {
					Particles.spawnParticle(this.world, Particles.Types.SMOKE,
					 this.parts[i].posX, this.parts[i].posY+this.parts[i].height/2, this.parts[i].posZ, 30,
					 this.parts[i].width * 0.5d, this.parts[i].height * 0.3d, this.parts[i].width * 0.5d,
					 0d, 0d, 0d, 0xD0FFFFFF, 20 + (int)(this.getScale() * 5));
				}
			}
		}
		
		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.phaseManager.setPhase(Phase.getPhaseFromId(compound.getInteger("phase")));
			if (compound.hasKey("multiparts", 9)) {
				NBTTagList taglist = compound.getTagList("multiparts", 10);
				for (int i = 0; i < taglist.tagCount(); i++) {
					NBTTagCompound tag = taglist.getCompoundTagAt(i);
					this.parts[i+1].readFromNBT(tag);
					this.partRot.set(i, new ProcedureUtils.Vec2f(this.parts[i+1].rotationYaw, this.parts[i+1].rotationPitch));
				}
				this.needsSync = true;
			}
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setInteger("phase", this.getPhase().getID());
			NBTTagList taglist = new NBTTagList();
			for (int i = 1; i < this.parts.length; i++) {
				NBTTagCompound tag = new NBTTagCompound();
				this.parts[i].writeToNBT(tag);
				taglist.appendTag(tag);
			}
			if (!taglist.hasNoTags()) {
				compound.setTag("multiparts", taglist);
			}
		}

	    @SideOnly(Side.CLIENT)
	    @Override
	    public boolean isInRangeToRenderDist(double distance) {
	    	double d0 = this.getEntityBoundingBox().getAverageEdgeLength();
	    	if (d0 < 1.0d) {
	    		d0 = 1.0d;
	    	}
	    	d0 = d0 * 64.0d * Entity.getRenderDistanceWeight();
	        return distance < d0 * d0;
	    }
	}

	public static class SnakeSegment extends MultiPartEntityPart {
		private final EntityCustom head;

		public SnakeSegment(EntityCustom parentIn, String partName, float width, float height) {
			super(parentIn, partName, width, height);
			this.head = parentIn;
		}

		@Override
		protected void setSize(float width, float height) {
			super.setSize(width, height);
		}

		@Override
		public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
			return this.head.processInitialInteract(player, hand);
		}
	}

	static class Navigate extends EntitySummonAnimal.NavigateGround {
		private BlockPos targetPosition;

		public Navigate(EntityCustom entityLivingIn, World worldIn) {
			super(entityLivingIn, worldIn);
		}

		@Override
	    public Path getPathToPos(BlockPos pos) {
	        this.targetPosition = pos;
	        return super.getPathToPos(pos);
	    }
	
		@Override
	    public Path getPathToEntityLiving(Entity entityIn) {
	        this.targetPosition = new BlockPos(entityIn);
	        return super.getPathToEntityLiving(entityIn);
	    }
	
		@Override
	    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
	    	Path path = this.getPathToEntityLiving(entityIn);
	        if (path != null) {
	            return this.setPath(path, speedIn);
	        } else {
	            this.targetPosition = new BlockPos(entityIn);
	            this.speed = speedIn;
	            return true;
	        }
	    }
	
	    @Override
	    public void clearPath() {
	    	super.clearPath();
	    	this.targetPosition = null;
	    }
		
		@Override
	    public void onUpdateNavigation() {
	        if (!this.noPath()) {
	            super.onUpdateNavigation();
	        } else {
	            if (this.targetPosition != null) {
	                double d0 = (double)(this.entity.width * this.entity.width);
	                double d1 = (double)this.targetPosition.getY() - this.entity.posY;
	                double d2 = this.entity.getDistanceSqToCenter(new BlockPos(this.targetPosition.getX(),
	                 MathHelper.floor(this.entity.posY), this.targetPosition.getZ()));
	                //double d3 = this.targetPosition.distanceSq(this.entity.getPosition());
	                //if (d3 >= 1.0d && (d2 >= d0 || (d1 <= 8d * this.entity.height && d1 >= -12d * this.entity.height))) {
	                if (d2 >= d0 || (d1 <= 8d * this.entity.height && d1 >= -12d * this.entity.height)) {
	                	this.entity.getMoveHelper().setMoveTo((double)this.targetPosition.getX() + 0.5d,
	                	 (double)this.targetPosition.getY(), (double)this.targetPosition.getZ() + 0.5d, this.speed);
	                } else {
	                    this.targetPosition = null;
	                }
	            }
	        }
	    }

	    @Override
	    public boolean canEntityStandOnPos(BlockPos pos) {
	    	return canStandOn(this.world, pos);
	    }
	}

	private static boolean canStandOn(World world, BlockPos pos) {
		IBlockState blockstate = world.getBlockState(pos);
		if (blockstate.getMaterial() == Material.WATER) {
			return true;
		}
		IBlockState blockstate2 = world.getBlockState(pos.down());
		return !blockstate.isFullBlock() && blockstate.getMaterial() != Material.LAVA
		 && blockstate.getMaterial() != Material.FIRE && blockstate2.isFullBlock() && blockstate2.getMaterial() != Material.ICE;
	}

	static class NavigateSwim extends PathNavigateSwimmer {
		private BlockPos targetPosition;

		public NavigateSwim(EntityCustom entityLivingIn, World worldIn) {
			super(entityLivingIn, worldIn);
		}

		@Override
	    public Path getPathToPos(BlockPos pos) {
	        this.targetPosition = pos;
	        return super.getPathToPos(pos);
	    }
	
		@Override
	    public Path getPathToEntityLiving(Entity entityIn) {
	        this.targetPosition = new BlockPos(entityIn);
	        return super.getPathToEntityLiving(entityIn);
	    }
	
		@Override
	    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
	    	Path path = this.getPathToEntityLiving(entityIn);
	        if (path != null) {
	            return this.setPath(path, speedIn);
	        } else {
	            this.targetPosition = new BlockPos(entityIn);
	            this.speed = speedIn;
	            return true;
	        }
	    }
	
		@Override
	    public void onUpdateNavigation() {
	        if (!this.noPath()) {
	            super.onUpdateNavigation();
	        } else {
	            if (this.targetPosition != null) {
	                double d0 = (double)(this.entity.width * this.entity.width);
	                double d1 = (double)this.targetPosition.getY() - this.entity.posY;
	                if (this.entity.getDistanceSqToCenter(this.targetPosition) >= d0 && d1 <= (double)this.entity.height * 4) {
	                	this.entity.getMoveHelper().setMoveTo((double)this.targetPosition.getX(), 
	                	 (double)this.targetPosition.getY(), (double)this.targetPosition.getZ(), this.speed);
	                } else {
	                    this.targetPosition = null;
	                }
	            }
	        }
	    }

	    @Override
	    public boolean canEntityStandOnPos(BlockPos pos) {
	    	return canStandOn(this.world, pos);
	    }
	}

	static class MoveHelper extends EntityMoveHelper {
		private int strafe;

		MoveHelper(EntityCustom entityIn) {
			super(entityIn);
		}

		@Override
		public void onUpdateMoveHelper() {
			if (this.isUpdating()) {
	            this.action = EntityMoveHelper.Action.WAIT;
	            Vec3d vec0 = new Vec3d(this.posX, this.posY, this.posZ).subtract(this.entity.getPositionVector());
	            double d = vec0.lengthSquared();
	            if (d < 0.25d * this.entity.width * this.entity.width) {
	                this.entity.setMoveForward(0.0F);
	                return;
	            }
	            float f = (float)(this.speed * ProcedureUtils.getModifiedSpeed(this.entity));
	            if (d > 2.25d * this.entity.width * this.entity.width) {
	            	vec0 = vec0.rotateYaw(MathHelper.sin(0.3f * this.strafe++) * 0.5236F);
	            }
	            float f9 = (float)(MathHelper.atan2(vec0.z, vec0.x) * 180D / Math.PI) - 90.0F;
	            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f9, 30.0F);
	            if (this.entity.isInWater()) {
	            	Vec3d vec1 = vec0.normalize().scale(f);
					this.entity.motionX = vec1.x;
					this.entity.motionY = vec1.y;
					this.entity.motionZ = vec1.z;
	            } else {
	            	this.entity.setAIMoveSpeed(f);
	            }
	            if (this.entity.collidedHorizontally) {
	             //&& vec0.y > 0.01d) {
	                this.entity.motionY = 0.2d + 0.2d * this.entity.height;
	            }
			} else {
				//super.onUpdateMoveHelper();
				this.entity.setMoveForward(0.0F);
				this.entity.setMoveVertical(0.0F);
			}
		}
	}

	public enum Phase {
		DEFENSIVE(0, PhaseDefensive.class),
		ROAMING(1, PhaseRoaming.class),
		AGGRESIVE(2, PhaseAggresive.class),
		RIDING(3, PhaseRiding.class);

		private final int id;
		private final Class<? extends PhaseBase> clazz;
		private static final Map<Integer, Phase> PHASES = Maps.newHashMap();
		
		static {
			for (Phase phase : values())
				PHASES.put(Integer.valueOf(phase.getID()), phase);
		}

		Phase(int i, Class<? extends PhaseBase> classIn) {
			this.id = i;
			this.clazz = classIn;
		}

	    public PhaseBase createPhase(PhaseManager phaseManager) {
	        try {
	            Constructor <? extends PhaseBase> constructor = this.getConstructor();
	            return constructor.newInstance(phaseManager);
	        } catch (Exception exception) {
	            throw new Error(exception);
	        }
	    }
	
	    private Constructor <? extends PhaseBase> getConstructor() throws NoSuchMethodException {
	        return this.clazz.getConstructor(PhaseManager.class);
	    }
		
		public int getID() {
			return this.id;
		}

		public static Phase getPhaseFromId(int i) {
			return PHASES.get(Integer.valueOf(i));
		}
	}

	public static class PhaseManager {
		private final EntityCustom entity;
		private PhaseBase phase;

		public PhaseManager(EntityCustom entityIn) {
			this.entity = entityIn;
		}

		public void setPhase(Phase phaseIn) {
			if (this.phase == null || phaseIn != this.phase.getType()) {
				if (this.phase == null || !this.phase.setNextPhase(phaseIn)) {
					this.phase = phaseIn.createPhase(this);
					this.entity.setPhase(phaseIn);
				}
			}
		}

		public PhaseBase getPhase() {
			return this.phase;
		}
	}

	public static abstract class PhaseBase {
		protected final PhaseManager manager;
		protected final EntityCustom entity;
		protected Phase nextPhase;

		public PhaseBase(PhaseManager phaseManager) {
			this.manager = phaseManager;
			this.entity = phaseManager.entity;
		}

		public abstract Phase getType();
		
		public void onUpdate() {
			if (this.nextPhase != null && this.isPhaseFinished()) {
				this.manager.setPhase(this.nextPhase);
				this.nextPhase = null;
			}
		}

		protected boolean setNextPhase(Phase phase) {
			if (!this.isPhaseFinished()) {
				this.finishPhase();
				this.nextPhase = phase;
				return true;
			}
			return false;
		}

		protected void finishPhase() {
		}

		protected boolean isPhaseFinished() {
			return true;
		}

		protected double getYOffset() {
			return 0.0d;
		}
	}

	public static class PhaseRoaming extends PhaseBase {
		private Vec3d lastVec;
		private float prevYaw;
		private final List<Vec3d> lastPivots = Lists.newArrayList();

		public PhaseRoaming(PhaseManager phaseManager) {
			super(phaseManager);
			this.lastVec = this.entity.getPositionVector();
			this.prevYaw = this.entity.renderYawOffset;
			/*Vec3d vec = Vec3d.ZERO;
			float f = entityIn.getScale() * 0.0625f * 4.0f;
			float f0 = this.prevYaw;
			for (int i = 0; i < entityIn.partRot.size(); i++) {
				this.lastPivots.add(vec);
				f0 = MathHelper.wrapDegrees(f0 - entityIn.partRot.get(i).x);
				vec = getOffsetPoint((float)vec.x, (float)vec.y, (float)vec.z,
				 -entityIn.partRot.get(i).y * 0.017453292F, -f0 * 0.017453292F, -f);
			}*/
		}

		@Override
		public Phase getType() {
			return Phase.ROAMING;
		}

		/*@Override
		public boolean onUpdate() {
			if (super.onUpdate()) {
				return true;
			}
			float scale = this.entity.getScale();
			Vec3d vec3 = this.entity.getPositionVector().subtract(this.entity.lastTickPosX, this.entity.lastTickPosY, this.entity.lastTickPosZ);
			ProcedureUtils.Vec2f vec0 = new ProcedureUtils.Vec2f(this.entity.renderYawOffset,
			 this.entity.yOffset2Pitch((float)vec3.y, 4.0F * scale * 0.0625F));
			ProcedureUtils.Vec2f vec1 = new ProcedureUtils.Vec2f(this.entity.prevRenderYawOffset, 0.0f);
			ProcedureUtils.Vec2f vec = vec0.subtract(vec1);
			double d4 = vec3.lengthVector();
			if (d4 > 0.1d) {
				this.entity.partRot.add(0, vec);
				this.entity.partRot.remove(this.entity.parts.length - 1);
			} else {
				this.entity.partRot.set(0, this.entity.partRot.get(0).add(vec));
			}
			return false;
		}*/

		@Override
		public void onUpdate() {
			Vec3d cposvec = this.entity.getPositionVector();
			float slength = this.entity.getScale() * 4.0F * 0.0625F;
			ProcedureUtils.Vec2f vec0 = new ProcedureUtils.Vec2f(this.entity.renderYawOffset,
			 this.entity.yOffset2Pitch((float)(cposvec.y - this.entity.prevPosY), slength));
			ProcedureUtils.Vec2f vec = vec0.subtract(new ProcedureUtils.Vec2f(this.prevYaw, 0.0f));
			Vec3d vec4 = cposvec.subtract(this.lastVec);
			double d4 = vec4.lengthVector();
//String s = ">>>["+(this.entity.world.isRemote?"client":"server")+"] vec0="+vec0+", vec1="+vec1+", vec="+vec;
			if (d4 >= slength) {
				this.entity.partRot.add(0, vec);
				this.entity.partRot.remove(this.entity.parts.length - 1);
				this.lastVec = vec4.scale(slength / d4).add(this.lastVec);
			} else {
				int i = 0;
				float f0 = 0.0f;
				do {
					vec0 = this.entity.partRot.get(i);
					f0 = MathHelper.wrapDegrees(vec0.x + vec.x);
				} while ((f0 > 90.0f || f0 < -90.0f) && ++i < this.entity.partRot.size());
				if (i < this.entity.partRot.size()) {
					this.entity.partRot.set(i, new ProcedureUtils.Vec2f(f0, vec0.y));
				}
				vec0 = this.entity.partRot.get(0);
				this.entity.partRot.set(0, new ProcedureUtils.Vec2f(vec0.x, MathHelper.wrapDegrees(vec0.y+vec.y)));
			}
//System.out.println(s+", part0:"+this.entity.partRot.get(0));
			this.prevYaw = this.entity.renderYawOffset;
			super.onUpdate();
		}

		/*@Override
		public boolean onUpdate() {
			if (super.onUpdate()) {
				return true;
			}
			float f = this.entity.getScale() * 0.0625f * 4.0f;
			Vec3d vec0 = this.entity.getPositionVector();
			Vec3d pivotNew = vec0.subtract(this.lastVec);
			for (double d = pivotNew.lengthVector(); d >= (double)f; d -= (double)f) {
				this.entity.partRot.add(0, ProcedureUtils.Vec2f.ZERO);
				this.entity.partRot.remove(this.entity.parts.length - 1);
			}
			float f0 = this.prevYaw;
System.out.println(">>> lastPivots:"+lastPivots);
			for (int i = 0; i < this.entity.partRot.size(); i++) {
				Vec3d vec4 = pivotNew.subtract(this.lastPivots.get(i));
System.out.println("    pivotNew"+i+":"+pivotNew);
				this.lastPivots.set(i, pivotNew);
				f0 = MathHelper.wrapDegrees(f0 - this.entity.partRot.get(i).x);
				float f1 = ProcedureUtils.getYawFromVec(vec4.x, vec4.z) - f0;
				double d0 = vec4.lengthVector() * MathHelper.sin(f1 * (float)Math.PI / 180f);
				f1 = -(float)(Math.asin(d0 / (double)f) * 180d / Math.PI);
				this.entity.partRot.set(i, this.entity.partRot.get(i).add(f1, 0.0f));
				f1 = MathHelper.wrapDegrees(f0 - f1);
				pivotNew = getOffsetPoint((float)pivotNew.x, (float)pivotNew.y, (float)pivotNew.z,
				 0.0f, -f1 * 0.017453292F, -f);
			}
			ProcedureUtils.Vec2f vec = new ProcedureUtils.Vec2f(this.entity.renderYawOffset,
			 this.entity.yOffset2Pitch((float)(vec0.y - this.entity.prevPosY), f))
			 .subtract(new ProcedureUtils.Vec2f(this.prevYaw, 0.0f));
			this.entity.partRot.set(0, this.entity.partRot.get(0).add(vec));
			this.prevYaw = this.entity.renderYawOffset;
			this.lastVec = vec0;
			return false;
		}*/
	}

	public static class PhaseAggresive extends PhaseRoaming {
		public PhaseAggresive(PhaseManager phaseManager) {
			super(phaseManager);
		}

		@Override
		public Phase getType() {
			return Phase.AGGRESIVE;
		}
	}

	public static class PhaseDefensive extends PhaseBase {
		private static final ProcedureUtils.Vec2f[] STARTROTATIONS = {
			new ProcedureUtils.Vec2f(0.0f, 60.0f), new ProcedureUtils.Vec2f(0.0f, 105.0f), new ProcedureUtils.Vec2f(0.0f, 135.0f),
			new ProcedureUtils.Vec2f(0.0f, 90.0f), new ProcedureUtils.Vec2f(0.0f, 45.0f), new ProcedureUtils.Vec2f(0.0f, 0.0f),
			new ProcedureUtils.Vec2f(60.0f, 0.0f), new ProcedureUtils.Vec2f(60.0f, 0.0f), new ProcedureUtils.Vec2f(45.0f, 0.0f),
			new ProcedureUtils.Vec2f(30.0f, 0.0f), new ProcedureUtils.Vec2f(30.0f, 0.0f), new ProcedureUtils.Vec2f(30.0f, 0.0f),
			new ProcedureUtils.Vec2f(30.0f, 0.0f), new ProcedureUtils.Vec2f(30.0f, 0.0f), new ProcedureUtils.Vec2f(30.0f, 0.0f),
			new ProcedureUtils.Vec2f(30.0f, 0.0f), new ProcedureUtils.Vec2f(15.0f, 0.0f), new ProcedureUtils.Vec2f(30.0f, 0.0f),
			new ProcedureUtils.Vec2f(15.0f, 0.0f), new ProcedureUtils.Vec2f(30.0f, 0.0f), new ProcedureUtils.Vec2f(30.0f, 0.0f)
		};
		private static final ProcedureUtils.Vec2f[] ENDROTATIONS = {
			new ProcedureUtils.Vec2f(0.0f, 0.0f), new ProcedureUtils.Vec2f(0.0f, -45.0f), new ProcedureUtils.Vec2f(0.0f, -15.0f),
			new ProcedureUtils.Vec2f(0.0f, 15.0f), new ProcedureUtils.Vec2f(0.0f, 45.0f)
		};
		private final ProcedureUtils.Vec2f[] rotationOffsets = new ProcedureUtils.Vec2f[STARTROTATIONS.length];
		private final int transitionTime = 20;
		private int startTime2Finish = transitionTime;
		private int endTime2Finish = -1;

		public PhaseDefensive(PhaseManager phaseManager) {
			super(phaseManager);
			for (int i = 0; i < STARTROTATIONS.length && i < this.entity.partRot.size(); i++) {
				this.rotationOffsets[i] = STARTROTATIONS[i].subtract(this.entity.partRot.get(i));
			}
		}

		@Override
		public Phase getType() {
			return Phase.DEFENSIVE;
		}

		@Override
		public void onUpdate() {
			if (this.endTime2Finish > 0) {
				--this.endTime2Finish;
				float f = (float)this.endTime2Finish / this.transitionTime;
				for (int i = 0; i < ENDROTATIONS.length; i++) {
					this.entity.partRot.set(i, ENDROTATIONS[i].subtract(this.rotationOffsets[i].scale(f)));
				}
			} else if (this.startTime2Finish > 0 && (this.startTime2Finish < this.transitionTime || this.entity.onGround)) {
				--this.startTime2Finish;
				float f = (float)this.startTime2Finish / this.transitionTime;
				for (int i = 0; i < this.entity.partRot.size(); i++) {
					this.entity.partRot.set(i, STARTROTATIONS[i].subtract(this.rotationOffsets[i].scale(f)));
				}
			}
			if (this.startTime2Finish != this.transitionTime) {
				//this.entity.motionY = 0.0d;
				this.entity.motionX = 0.0d;
				this.entity.motionZ = 0.0d;
			}
			super.onUpdate();
		}

		@Override
		protected void finishPhase() {
			if (this.endTime2Finish < 0) {
				this.endTime2Finish = this.startTime2Finish > 0 ? this.transitionTime - this.startTime2Finish : this.transitionTime;
				for (int i = 0; i < ENDROTATIONS.length; i++) {
					this.rotationOffsets[i] = ENDROTATIONS[i].subtract(STARTROTATIONS[i]);
				}
			}
		}

		@Override
		protected boolean isPhaseFinished() {
			return this.nextPhase == Phase.RIDING || this.endTime2Finish == 0;
		}

		@Override
		protected double getYOffset() {
			//float f = this.endTime2Finish >= 0 ? 1.0f - 0.5f * ((float)this.endTime2Finish / this.transitionTime)
			// : (0.5f - 0.5f * ((float)this.startTime2Finish / this.transitionTime));
			//return 17.0D * (f > 0.5F ? 1.0F - f : f) * 2.0F;
			return 17.0F * (this.endTime2Finish >= 0 ? (float)this.endTime2Finish / this.transitionTime
			 : (1.0F - (float)this.startTime2Finish / this.transitionTime));
		}
	}

	public static class PhaseRiding extends PhaseBase {
		private static final ProcedureUtils.Vec2f[][] RIDINGROTATIONS = {
			{	new ProcedureUtils.Vec2f(0.0f, 0.0f), new ProcedureUtils.Vec2f(0.0f, 30.0f), new ProcedureUtils.Vec2f(0.0f, 60.0f),
				new ProcedureUtils.Vec2f(0.0f, 90.0f), new ProcedureUtils.Vec2f(0.0f, 120.0f), new ProcedureUtils.Vec2f(0.0f, 150.0f),
				new ProcedureUtils.Vec2f(0.0f, 180.0f), new ProcedureUtils.Vec2f(-45.0f, 180.0f), new ProcedureUtils.Vec2f(-30.0f, 180.0f),
				new ProcedureUtils.Vec2f(-30.0f, 180.0f), new ProcedureUtils.Vec2f(-45.0f, 180.0f), new ProcedureUtils.Vec2f(-30.0f, 180.0f),
				new ProcedureUtils.Vec2f(-30.0f, 195.0f), new ProcedureUtils.Vec2f(0.0f, 210.0f), new ProcedureUtils.Vec2f(15.0f, 225.0f),
				new ProcedureUtils.Vec2f(0.0f, 240.0f), new ProcedureUtils.Vec2f(0.0f, 255.0f), new ProcedureUtils.Vec2f(0.0f, 270.0f),
				new ProcedureUtils.Vec2f(0.0f, 255.0f), new ProcedureUtils.Vec2f(0.0f, 240.0f), new ProcedureUtils.Vec2f(0.0f, 225.0f)
			}, {
				new ProcedureUtils.Vec2f(0.0f, 0.0f), new ProcedureUtils.Vec2f(0.0f, 30.0f), new ProcedureUtils.Vec2f(0.0f, 60.0f),
				new ProcedureUtils.Vec2f(0.0f, 90.0f), new ProcedureUtils.Vec2f(0.0f, 120.0f), new ProcedureUtils.Vec2f(0.0f, 150.0f),
				new ProcedureUtils.Vec2f(0.0f, 180.0f), new ProcedureUtils.Vec2f(45.0f, 180.0f), new ProcedureUtils.Vec2f(30.0f, 180.0f),
				new ProcedureUtils.Vec2f(30.0f, 180.0f), new ProcedureUtils.Vec2f(45.0f, 180.0f), new ProcedureUtils.Vec2f(30.0f, 180.0f),
				new ProcedureUtils.Vec2f(30.0f, 195.0f), new ProcedureUtils.Vec2f(0.0f, 210.0f), new ProcedureUtils.Vec2f(-15.0f, 225.0f),
				new ProcedureUtils.Vec2f(0.0f, 240.0f), new ProcedureUtils.Vec2f(0.0f, 255.0f), new ProcedureUtils.Vec2f(0.0f, 270.0f),
				new ProcedureUtils.Vec2f(0.0f, 255.0f), new ProcedureUtils.Vec2f(0.0f, 240.0f), new ProcedureUtils.Vec2f(0.0f, 225.0f)
			}
		};
		private final int transitionTime = 20;

		public PhaseRiding(PhaseManager phaseManager) {
			super(phaseManager);
		}

		@Override
		public Phase getType() {
			return Phase.RIDING;
		}

		@Override
		public void onUpdate() {
			if (this.entity.isRiding()) {
				ProcedureUtils.Vec2f[] rotations = RIDINGROTATIONS[this.entity.getRidingEntity().getPassengers().indexOf(this.entity)];
				for (int i = 0; i < this.entity.partRot.size(); i++) {
					this.entity.partRot.set(i, rotations[i]);
				}
			}
			super.onUpdate();
		}
	}

	public static class ServerMessage implements IMessage {
		int id;
		float[] x = new float[21];
		float[] y = new float[21];

		public ServerMessage() { }

		public ServerMessage(EntityCustom entity) {
			this.id = entity.getEntityId();
			for (int i = 0; i < 21 && i < entity.partRot.size(); i++) {
				ProcedureUtils.Vec2f vec = entity.partRot.get(i);
				this.x[i] = vec.x;
				this.y[i] = vec.y;
			}
		}

		public static void sendToTracking(EntityCustom entity) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new ServerMessage(entity), entity);
		}

		public static class Handler implements IMessageHandler<ServerMessage, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(ServerMessage message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					Entity entity = mc.world.getEntityByID(message.id);
					if (entity instanceof EntityCustom) {
						for (int i = 0; i < ((EntityCustom)entity).partRot.size(); i++) {
							((EntityCustom)entity).partRot.set(i, new ProcedureUtils.Vec2f(message.x[i], message.y[i]));
						}
						//NarutomodMod.PACKET_HANDLER.sendToServer(new ReplyMessage(entity.getEntityId()));
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			for (int i = 0; i < 21; i++) {
				buf.writeFloat(this.x[i]);
				buf.writeFloat(this.y[i]);
			}
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			for (int i = 0; i < 21; i++) {
				this.x[i] = buf.readFloat();
				this.y[i] = buf.readFloat();
			}
		}
	}

	/*public static class ReplyMessage implements IMessage {
		int id;
		
		public ReplyMessage() { }

		public ReplyMessage(int i) {
			this.id = i;
		}

		public void toBytes(ByteBuf buf) { 
			buf.writeInt(this.id);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
		}

		public static class Handler implements IMessageHandler<ReplyMessage, IMessage> {
			@Override
			public IMessage onMessage(ReplyMessage message, MessageContext context) {
				WorldServer world = context.getServerHandler().player.getServerWorld();
				world.addScheduledTask(() -> {
					Entity entity = world.getEntityByID(message.id);
					if (entity instanceof EntityCustom) {
						((EntityCustom)entity).needsSync = false;
					}
				});
				return null;
			}
		}
	}*/

	public static Vec3d getOffsetPoint(float x, float y, float z, float rotateX, float rotateY, float offset) {
		return new Vec3d((double)-x - Math.sin((double)rotateY) * Math.cos((double)rotateX) * offset,
		 (double)-y + Math.sin((double)rotateX) * offset,
		 (double)-z - Math.cos((double)rotateY) * Math.cos((double)rotateX) * offset)
		 .scale(-1.0d);
	}

	@SideOnly(Side.CLIENT)
	public static class RenderSnake<T extends EntityCustom> extends RenderLiving<T> {
		public RenderSnake(RenderManager renderManagerIn) {
			super(renderManagerIn, new ModelSnake(), 0.3f);
		}

		@Override
		protected ResourceLocation getEntityTexture(T entity) {
			return null;
		}
	}

	// Made with Blockbench 4.0.4
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public static class ModelSnake extends ModelBase {
		private final ModelRenderer headNeck;
		private final ModelRenderer head;
		private final ModelRenderer bone2;
		private final ModelRenderer bone3;
		private final ModelRenderer bone4;
		private final ModelRenderer bone5;
		private final ModelRenderer bone6;
		private final ModelRenderer bone7;
		private final ModelRenderer bone8;
		private final ModelRenderer bone9;
		private final ModelRenderer bone11;
		private final ModelRenderer bone19;
		private final ModelRenderer bone20;
		private final ModelRenderer jaw;
		private final ModelRenderer bone21;
		private final ModelRenderer bone22;
		private final ModelRenderer bone23;
		private final ModelRenderer horns;
		private final ModelRenderer bone24;
		private final ModelRenderer bone25;
		private final ModelRenderer bone26;
		private final ModelRenderer bone37;
		private final ModelRenderer segment[] = new ModelRenderer[21];
		private float partialTicks;

		public ModelSnake() {
			textureWidth = 32;
			textureHeight = 32;

			headNeck = new ModelRenderer(this);
			headNeck.setRotationPoint(0.0F, 22.0F, 0.0F);
			headNeck.cubeList.add(new ModelBox(headNeck, 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
			head = new ModelRenderer(this);
			head.setRotationPoint(0.0F, 0.0F, -5.0F);
			headNeck.addChild(head);
			head.cubeList.add(new ModelBox(head, 16, 0, -2.5F, -2.0F, 0.0F, 5, 4, 1, 0.1F, false));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(1.4F, -0.7F, -5.35F);
			head.addChild(bone2);
			setRotationAngle(bone2, 0.7854F, 0.0F, 0.6109F);
			bone2.cubeList.add(new ModelBox(bone2, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(0.0F, -0.5F, 3.0F);
			bone2.addChild(bone3);
			setRotationAngle(bone3, -0.9599F, 0.0F, 0.0F);
			bone3.cubeList.add(new ModelBox(bone3, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(-1.4F, -0.7F, -5.35F);
			head.addChild(bone4);
			setRotationAngle(bone4, 0.7854F, 0.0F, -0.6109F);
			bone4.cubeList.add(new ModelBox(bone4, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(0.0F, -0.5F, 3.0F);
			bone4.addChild(bone5);
			setRotationAngle(bone5, -0.9599F, 0.0F, 0.0F);
			bone5.cubeList.add(new ModelBox(bone5, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(0.0F, -1.0F, 0.0F);
			head.addChild(bone6);
			setRotationAngle(bone6, 0.0436F, 0.0873F, 0.0F);
			bone6.cubeList.add(new ModelBox(bone6, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));
			bone7 = new ModelRenderer(this);
			bone7.setRotationPoint(0.0F, -1.0F, 0.0F);
			head.addChild(bone7);
			setRotationAngle(bone7, 0.0436F, -0.0873F, 0.0F);
			bone7.cubeList.add(new ModelBox(bone7, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(-0.15F, -1.1F, -2.5F);
			head.addChild(bone8);
			setRotationAngle(bone8, 0.5236F, 0.2618F, 0.0F);
			bone8.cubeList.add(new ModelBox(bone8, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(0.15F, -1.1F, -2.5F);
			head.addChild(bone9);
			setRotationAngle(bone9, 0.5236F, -0.2618F, 0.0F);
			bone9.cubeList.add(new ModelBox(bone9, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));
			bone11 = new ModelRenderer(this);
			bone11.setRotationPoint(2.6F, 0.1F, -3.95F);
			head.addChild(bone11);
			setRotationAngle(bone11, 0.0F, 0.2618F, 0.0F);
			bone11.cubeList.add(new ModelBox(bone11, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
			bone11.cubeList.add(new ModelBox(bone11, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));
			bone19 = new ModelRenderer(this);
			bone19.setRotationPoint(-2.65F, 0.1F, -3.95F);
			head.addChild(bone19);
			setRotationAngle(bone19, 0.0F, -0.2618F, 0.0F);
			bone19.cubeList.add(new ModelBox(bone19, 10, 19, 0.05F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
			bone19.cubeList.add(new ModelBox(bone19, 0, 19, 0.05F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));
			bone20 = new ModelRenderer(this);
			bone20.setRotationPoint(1.6F, 1.8F, -5.95F);
			head.addChild(bone20);
			bone20.cubeList.add(new ModelBox(bone20, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
			bone20.cubeList.add(new ModelBox(bone20, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));
			jaw = new ModelRenderer(this);
			jaw.setRotationPoint(0.0F, 0.5F, 0.0F);
			head.addChild(jaw);
			setRotationAngle(jaw, 0.0F, 0.0F, 0.0F);
			bone21 = new ModelRenderer(this);
			bone21.setRotationPoint(3.0F, 0.9F, 0.0F);
			jaw.addChild(bone21);
			setRotationAngle(bone21, 0.0F, 0.2182F, 0.0F);
			bone21.cubeList.add(new ModelBox(bone21, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));
			bone22 = new ModelRenderer(this);
			bone22.setRotationPoint(-3.0F, 0.9F, 0.0F);
			jaw.addChild(bone22);
			setRotationAngle(bone22, 0.0F, -0.2182F, 0.0F);
			bone22.cubeList.add(new ModelBox(bone22, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));
			bone23 = new ModelRenderer(this);
			bone23.setRotationPoint(0.0F, -0.2F, -5.5F);
			jaw.addChild(bone23);
			setRotationAngle(bone23, 3.1416F, 3.1416F, 0.0F);
			bone23.cubeList.add(new ModelBox(bone23, 0, 1, 1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, false));
			bone23.cubeList.add(new ModelBox(bone23, 0, 1, -1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, true));
			horns = new ModelRenderer(this);
			horns.setRotationPoint(0.0F, 0.6F, 0.0F);
			head.addChild(horns);
			bone24 = new ModelRenderer(this);
			bone24.setRotationPoint(-2.3F, -2.5F, -1.6F);
			horns.addChild(bone24);
			setRotationAngle(bone24, 0.2618F, -0.5236F, 0.0F);
			bone24.cubeList.add(new ModelBox(bone24, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.15F, false));
			bone24.cubeList.add(new ModelBox(bone24, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.1F, false));
			bone24.cubeList.add(new ModelBox(bone24, 28, 0, -0.5F, -0.5F, 2.0F, 1, 1, 1, 0.0F, false));
			bone24.cubeList.add(new ModelBox(bone24, 28, 0, -0.5F, -0.5F, 2.9F, 1, 1, 1, -0.1F, false));
			bone24.cubeList.add(new ModelBox(bone24, 28, 0, -0.5F, -0.5F, 3.6F, 1, 1, 1, -0.2F, false));
			bone24.cubeList.add(new ModelBox(bone24, 28, 0, -0.5F, -0.5F, 4.1F, 1, 1, 1, -0.3F, false));
			bone25 = new ModelRenderer(this);
			bone25.setRotationPoint(-1.2F, -2.5F, -1.2F);
			horns.addChild(bone25);
			setRotationAngle(bone25, 0.4363F, -0.3491F, 0.0F);
			bone25.cubeList.add(new ModelBox(bone25, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
			bone25.cubeList.add(new ModelBox(bone25, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, false));
			bone25.cubeList.add(new ModelBox(bone25, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
			bone25.cubeList.add(new ModelBox(bone25, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));
			bone26 = new ModelRenderer(this);
			bone26.setRotationPoint(1.2F, -2.5F, -1.2F);
			horns.addChild(bone26);
			setRotationAngle(bone26, 0.4363F, 0.3491F, 0.0F);
			bone26.cubeList.add(new ModelBox(bone26, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
			bone26.cubeList.add(new ModelBox(bone26, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, true));
			bone26.cubeList.add(new ModelBox(bone26, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
			bone26.cubeList.add(new ModelBox(bone26, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));
			bone37 = new ModelRenderer(this);
			bone37.setRotationPoint(2.3F, -2.5F, -1.6F);
			horns.addChild(bone37);
			setRotationAngle(bone37, 0.2618F, 0.5236F, 0.0F);
			bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.15F, true));
			bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.1F, true));
			bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 2.0F, 1, 1, 1, 0.0F, true));
			bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 2.9F, 1, 1, 1, -0.1F, true));
			bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 3.6F, 1, 1, 1, -0.2F, true));
			bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 4.1F, 1, 1, 1, -0.3F, true));

			for (int i = 0; i < 21; i++) {
				segment[i] = new ModelRenderer(this);
				segment[i].cubeList.add(new ModelBox(segment[i], 0, 0, -2.5F, -2.0F, -1.0F, 5, 4, 6, i >= 12 ? (11 - i) * 0.2F : 0.0F, false));
				/*if (i == 0) {
					segment[i].setRotationPoint(headNeck.rotationPointX, headNeck.rotationPointY, headNeck.rotationPointZ);
				} else {
					segment[i].setRotationPoint(0.0F, 0.0F, 4.0F);
					segment[i-1].addChild(segment[i]);
				}*/
			}
		}

		@Override
		public void setLivingAnimations(EntityLivingBase entityIn, float limbSwing, float limbSwingAmount, float partialTicksIn) {
			this.partialTicks = partialTicksIn;
		}

		@Override
		public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			//horns.showModel = false;
			EntityCustom entity = (EntityCustom)entityIn;
			headNeck.rotateAngleY = netHeadYaw * 0.5F * 0.017453292F;
			head.rotateAngleY = netHeadYaw * 0.5F * 0.017453292F;
			if (headPitch == 0.0F) {
				headNeck.rotateAngleX = -0.2618F;
				head.rotateAngleX = 0.2618F;
			} else {
				headNeck.rotateAngleX = headPitch * 0.5F * 0.017453292F;
				head.rotateAngleX = headPitch * 0.5F * 0.017453292F;
			}
			if (entity.getPhase() == Phase.AGGRESIVE) {
				headNeck.rotateAngleX -= 0.2618F;
				head.rotateAngleX += 0.1745F;
				jaw.rotateAngleX = 0.5236F;
			} else {
				jaw.rotateAngleX = 0.0F;
			}
			float mscale = entity.getScale();
			float yOffset = 0.0F;
			if (entity.phaseManager.getPhase().getType() == Phase.DEFENSIVE) {
				yOffset = (float)entity.phaseManager.getPhase().getYOffset() * mscale * 0.0625F;
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 1.5F - 1.5F * mscale - yOffset, 0.0F);
			GlStateManager.scale(mscale, mscale, mscale);
			headNeck.render(scale);
			Vec3d vec = new Vec3d(headNeck.rotationPointX, headNeck.rotationPointY, headNeck.rotationPointZ);
			float f0 = 0.0f;
			for (int i = 1; i < entity.parts.length; i++) {
				segment[i-1].setRotationPoint((float)vec.x, (float)vec.y, (float)vec.z);
				ProcedureUtils.Vec2f vec0 = entity.getMovementOffsets(i, this.partialTicks);
				f0 = MathHelper.wrapDegrees(f0 - vec0.x);
				this.setRotationAngle(segment[i-1], -vec0.y * 0.017453292F, f0 * 0.017453292F, 0.0F);
				segment[i-1].render(scale);
				vec = this.getNextSegmentRotationPoint(segment[i-1], 4.0F);
				//entity.partPos[i] = this.getNextSegmentRotationPoint(segment[i], 2.0F)
				// .subtract(this.getNextSegmentRotationPoint(headNeck, -headNeck.rotationPointZ)).scale(scale)
				// .rotatePitch((entityIn.rotationPitch - 180.0F) * 0.017453292F)
				// .rotateYaw(-entity.renderYawOffset * 0.017453292F)
				// .add(entityIn.getPositionVector());
			}
			GlStateManager.popMatrix();
		}

		private Vec3d getNextSegmentRotationPoint(ModelRenderer modelrenderer, float offset) {
			return getOffsetPoint(modelrenderer.rotationPointX, modelrenderer.rotationPointY, modelrenderer.rotationPointZ,
			 modelrenderer.rotateAngleX, modelrenderer.rotateAngleY, offset);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

	}
}
