
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.potion.PotionCorrosion;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.ElementsNarutomodMod;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppet extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 282;
	public static final int ENTITYID_RANGED = 283;

	public EntityPuppet(ElementsNarutomodMod instance) {
		super(instance, 603);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerHook());
	}

	public static class PlayerHook {
		@SubscribeEvent
		public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.player.world.isRemote) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack(event.player, ItemNinjutsu.block);
				if (stack != null) {
					Base.Jutsu.updatePuppetList(stack.getTagCompound(), null, false);
				}
			}
		}

		@SubscribeEvent
		public void onAttacked(LivingAttackEvent event) {
			EntityLivingBase entity = event.getEntityLiving();
			NBTTagCompound compound = entity.getEntityData();
			if (entity instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemNinjutsu.block);
				compound = stack != null ? stack.getTagCompound() : null;
			}
			if (compound != null && Base.Jutsu.puppetCount(compound) > 0) {
				List<Base> list = Base.Jutsu.getPuppetList(compound, entity.world);
				if (!list.isEmpty()) {
					for (Base puppet : list) {
						if (puppet.getDistanceSq(entity) <= 64.0d) {
							if (event.getSource().getDamageLocation() != null) {
								Vec3d vec = event.getSource().getDamageLocation().subtract(entity.getPositionVector());
								Vec3d vec1 = vec.scale(event.getSource().getImmediateSource() instanceof EntityLivingBase ? 0.5d : 0.8d);
								ProcedureUtils.Vec2f vec2 = ProcedureUtils.getYawPitchFromVec(vec1);
								puppet.setPositionAndRotation(entity.posX + vec1.x, entity.posY + vec1.y, entity.posZ + vec1.z, vec2.x, vec2.y);
								puppet.haltAIfor(20);
								if (vec.lengthVector() <= entity.width * 0.5f + event.getSource().getImmediateSource().width * 0.5f + puppet.width) {
									ProcedureUtils.multiplyVelocity(entity, vec.normalize().scale(-puppet.width));
								}
							}
							event.setCanceled(true);
							puppet.attackEntityFrom(event.getSource(), event.getAmount());
							return;
						}
					}
				}
			}
		}
	}

	public abstract static class Base extends EntityCreature {
		private static final DataParameter<Integer> OWNERID = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> REAL_AGE = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		//private int haltAITicks;

		public Base(World worldIn) {
			super(worldIn);
			this.experienceValue = 0;
			this.enablePersistence();
			//this.setNoAI(true);
			this.navigator = new PathNavigateFlying(this, worldIn);
			this.moveHelper = new FlyHelper(this);
		}

		public Base(EntityLivingBase ownerIn) {
			this(ownerIn.world);
			if (ownerIn instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)ownerIn, ItemNinjutsu.block);
				if (stack != null && ((ItemNinjutsu.RangedItem)stack.getItem())
				 .canActivateJutsu(stack, ItemNinjutsu.PUPPET, (EntityPlayer)ownerIn) == EnumActionResult.SUCCESS) {
					this.setOwner(ownerIn);
				}
			} else {
				this.setOwner(ownerIn);
			}
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(OWNERID, Integer.valueOf(-1));
			this.dataManager.register(REAL_AGE, Integer.valueOf(0));
		}

		private void setAge(int age) {
			this.dataManager.set(REAL_AGE, Integer.valueOf(age));
		}
	
		public int getAge() {
			return ((Integer)this.getDataManager().get(REAL_AGE)).intValue();
		}

		@Nullable
		public EntityLivingBase getOwner() {
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(OWNERID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		protected void setOwner(@Nullable EntityLivingBase newOwner) {
			if (!this.world.isRemote) {
				EntityLivingBase oldOwner = this.getOwner();
				if (oldOwner != newOwner) {
					boolean addedNew = false;
					if (newOwner instanceof EntityPlayer) {
						ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)newOwner, ItemNinjutsu.block);
						if (stack != null && ((ItemNinjutsu.RangedItem)stack.getItem()).canActivateJutsu(stack, ItemNinjutsu.PUPPET, (EntityPlayer)newOwner) == EnumActionResult.SUCCESS
						 && Jutsu.puppetCount(stack.getTagCompound()) < (int)Math.ceil(Math.max(((ItemNinjutsu.RangedItem)stack.getItem()).getXpRatio(stack, ItemNinjutsu.PUPPET) - 0.999f, 0.0f) * 4.95f)) {
							Jutsu.updatePuppetList(stack.getTagCompound(), this, true);
						 	addedNew = true;
						}
					} else if (newOwner != null) {
						Jutsu.updatePuppetList(newOwner.getEntityData(), this, true);
						addedNew = true;
					}
					if (newOwner == null || addedNew) {
						if (oldOwner instanceof EntityPlayer) {
							ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)oldOwner, ItemNinjutsu.block);
							if (stack != null) {
								Jutsu.updatePuppetList(stack.getTagCompound(), this, false);
							}
						} else if (oldOwner != null) {
							Jutsu.updatePuppetList(oldOwner.getEntityData(), this, false);
						}
					}
					this.getDataManager().set(OWNERID, Integer.valueOf(newOwner != null && addedNew ? newOwner.getEntityId() : -1));
				}
			}
		}

		public static boolean canPlayerUseJutsu(EntityPlayer player) {
			ItemStack stack = ProcedureUtils.getMatchingItemStack(player, ItemNinjutsu.block);
			return stack != null && ((ItemNinjutsu.RangedItem)stack.getItem()).canActivateJutsu(stack, ItemNinjutsu.PUPPET, player) == EnumActionResult.SUCCESS;
		}

		protected Vec3d getOffsetToOwner() {
			return new Vec3d(0.0d, 0.0d, 4.0d);
		}

		@Nullable
		protected EnumHandSide chakraStringAttachesTo() {
			return EnumHandSide.RIGHT;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48D);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.6D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			Vec3d vec = this.getOffsetToOwner();
			this.tasks.addTask(4, new AIStayInOffsetOfOwner(this, vec.x, vec.y, vec.z));
			//this.targetTasks.addTask(0, new AICopyOwnerTarget(this));
		}

		@Override
		protected void updateAITasks() {
	    	EntityLivingBase owner = this.getOwner();
	    	//this.setNoGravity(owner != null);
			if (owner != null && this.getVelocity() > 0.1d && this.ticksExisted % 2 == 0) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:wood_click")), 
				 0.6f, this.rand.nextFloat() * 0.2f + 0.6f);
			}
			if (owner == null) {
				this.setAttackTarget(null);
			} else {
				double d = this.getDistanceSq(owner);
				if (!owner.isEntityAlive() || d > 2304d) {
					this.setOwner(null);
				} else if (d > 1600d) {
					Vec3d vec = owner.getPositionVector().subtract(this.getPositionVector()).normalize().scale(0.5d);
					this.addVelocity(vec.x, vec.y, vec.z);
				}
	    	}
	    	if (this.getAttackTarget() != null && !this.getAttackTarget().isEntityAlive()) {
	    		this.setAttackTarget(null);
	    	}
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
	    protected SoundEvent getDeathSound() {
	        return null;
	    }
	
	    @Override
	    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
	        return null;
	    }

	    @Override
	    public boolean isOnSameTeam(Entity entityIn) {
	    	return super.isOnSameTeam(entityIn) || entityIn.equals(this.getOwner());
	    }

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.FALL) {
				return false;
			}
			if (source.getTrueSource() != null && source.getTrueSource().equals(this.getOwner())) {
				return false;
			}
			if (source.isProjectile()) {
				amount *= 0.2f;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			EntityLivingBase owner = this.getOwner();
			return owner != null ? ProcedureUtils.attackEntityAsMob(this, entityIn, DamageSource.causeIndirectDamage(this, owner)) : false;
		}

		/*@Override
		protected boolean processInteract(EntityPlayer player, EnumHand hand) {
			ItemStack stack = player.getHeldItem(hand);
			if (!this.world.isRemote && stack.getItem() == ItemNinjutsu.block
			 && ((ItemNinjutsu.RangedItem)stack.getItem()).canActivateJutsu(stack, ItemNinjutsu.PUPPET, player) == EnumActionResult.SUCCESS) {
				this.setOwner(player.equals(this.getOwner()) ? null : player);
				return true;
			}
			return false;
		}*/

		@Override
		public void onLivingUpdate() {
			EntityLivingBase owner = this.getOwner();
			if (this.getAttackTarget() == null && owner != null) {
		    	RayTraceResult res = ProcedureUtils.objectEntityLookingAt(owner, 60d);
		        this.getLookHelper().setLookPosition(res.hitVec.x, res.hitVec.y, res.hitVec.z, 45.0f, 45.0f);
			}
			this.updateArmSwingProgress();
			super.onLivingUpdate();
		}

		protected void haltAIfor(int ticks) {
			ProcedureOnLivingUpdate.disableAIfor(this, ticks);
		}

	    @Override
	    public void onUpdate() {
	    	this.setAge(this.getAge() + 1);
	    	this.fallDistance = 0f;
	    	this.clearMostPotions(PotionCorrosion.potion, PotionAmaterasuFlame.potion);
	    	super.onUpdate();
	    	
	    	if (this.world.isRemote && this.isAIDisabled()) {
	    		this.motionX *= 0.1d;
	    		this.motionY *= 0.1d;
	    		this.motionZ *= 0.1d;
	    	}
	    }

		@Override
		public void setDead() {
			super.setDead();
			this.setOwner(null);
		}

		private void clearMostPotions(Potion... excludePotions) {
			if (!this.world.isRemote) {
				Iterator<PotionEffect> iterator = this.getActivePotionEffects().iterator();
				while (iterator.hasNext()) {
					PotionEffect effect = iterator.next();
					boolean skip = false;
					for (Potion potion : excludePotions) {
						if (effect.getPotion() == potion) {
							skip = true;
						}
					}
					if (!skip) {
						this.onFinishedPotionEffect(effect);
						iterator.remove();
					}
				}
			}
		}

		@Override
		public Vec3d getLookVec() {
			return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
		}

		public double getVelocity() {
			return MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		}

		protected boolean isMovingForward() {
			return Math.abs(MathHelper.wrapDegrees(ProcedureUtils.getYawFromVec(this.motionX, this.motionZ) - this.rotationYaw)) < 90.0f;
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setAge(compound.getInteger("age"));
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setInteger("age", this.getAge());
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String PUPPET_COUNT = "PuppetControlled";
			
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = ProcedureUtils.objectEntityLookingAt(entity, 5d, 2d).entityHit;
				if (entity1 instanceof Base) {
					EntityLivingBase puppetowner = ((Base)entity1).getOwner();
					boolean flag = puppetowner == null;
					if (flag || entity.equals(puppetowner)) {
						((Base)entity1).setOwner(flag ? entity : null);
						return flag && entity == ((Base)entity1).getOwner();
					}
				}
				return false;
			}

			private static List<Base> getPuppetList(NBTTagCompound compound, World world) {
				List<Base> list = Lists.newArrayList();
				int[] ids = compound.getIntArray(PUPPET_COUNT);
				for (int i = 0; i < ids.length; i++) {
					Entity entity1 = world.getEntityByID(ids[i]);
					if (entity1 instanceof Base && entity1.isEntityAlive()) {
						list.add((Base)entity1);
					}
				}
				return list;
			}

			private static int updatePuppetList(NBTTagCompound compound, @Nullable Base puppet, boolean add1) {
				List<Integer> list = Lists.newArrayList();
				if (puppet != null) {
					int[] ids = compound.getIntArray(PUPPET_COUNT);
					for (int i = 0; i < ids.length; i++) {
						Entity entity1 = puppet.world.getEntityByID(ids[i]);
						if (entity1 instanceof Base && entity1.isEntityAlive() && (add1 || !entity1.equals(puppet))) {
							list.add(ids[i]);
						}
					}
					if (add1 && !list.contains(puppet.getEntityId())) {
						list.add(puppet.getEntityId());
					}
				}
				if (list.isEmpty()) {
					compound.removeTag(PUPPET_COUNT);
				} else {
					compound.setIntArray(PUPPET_COUNT, Ints.toArray(list));
				}
				return list.size();
			}

			private static int puppetCount(NBTTagCompound compound) {
				return compound.getIntArray(PUPPET_COUNT).length;
			}
		}

		public class FlyHelper extends EntityMoveHelper {
			public FlyHelper(Base entityIn) {
				super(entityIn);
			}
				
			@Override
			public void onUpdateMoveHelper() {
				if (this.action == EntityMoveHelper.Action.MOVE_TO) {
	    			this.entity.setNoGravity(true);
					this.action = EntityMoveHelper.Action.WAIT;
					double d0 = this.posX - this.entity.posX;
					double d1 = this.posY - this.entity.posY;
					double d2 = this.posZ - this.entity.posZ;
					double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
					if (d3 < 1.6E-7D) {
						ProcedureUtils.multiplyVelocity(this.entity, 0.0d);
					} else {
						float f = (float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue()) * 0.1f;
						this.entity.motionX += d0 / d3 * f;
						this.entity.motionY += d1 / d3 * f;
						this.entity.motionZ += d2 / d3 * f;
						float f1 = -((float)MathHelper.atan2(this.entity.motionX, this.entity.motionZ)) * (180F / (float)Math.PI);
						//this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f1, 10.0F);
						this.entity.renderYawOffset = this.entity.rotationYaw = f1;
					}
				} else if (this.action == EntityMoveHelper.Action.STRAFE) {
					this.entity.setNoGravity(true);
					super.onUpdateMoveHelper();
				} else {
	    			this.entity.setNoGravity(false);
				}
			}
		}

	    public class AIStayInOffsetOfOwner extends EntityAIBase {
	    	private final Base entity;
	    	private EntityLivingBase owner;
	    	private final Vec3d offsetVec;
	    	
	        public AIStayInOffsetOfOwner(Base entityIn, double offX, double offY, double offZ) {
	        	this.entity = entityIn;
	        	this.offsetVec = new Vec3d(offX, offY, offZ);
	            this.setMutexBits(3);
	        }

	        @Override
	        public boolean shouldExecute() {
	        	EntityLivingBase entitylb = this.entity.getOwner();
		        if (entitylb == null) {
		            return false;
		        } else if (entitylb instanceof EntityPlayer && ((EntityPlayer)entitylb).isSpectator()) {
		            return false;
		        } else if (this.entity.getDistanceSq(entitylb) > 1600d) {
		            return false;
		        } else {
		            this.owner = entitylb;
		            return true;
		        }
	        }

			@Override
		    public boolean shouldContinueExecuting() {
		    	EntityLivingBase entitylb = this.entity.getOwner();
		        return entitylb != null && this.entity.getDistanceSq(entitylb) <= 1600d;
		    }

			@Override
		    public void resetTask() {
		        this.owner = null;
		    }

	        @Override
	        public void updateTask() {
	        	if (this.owner != null) {
	        		Vec3d vec = this.offsetVec.rotateYaw(-this.owner.rotationYaw * (float)Math.PI / 180F).add(this.owner.getPositionVector());
	        		BlockPos pos = new BlockPos(vec);
	        		for (int i = 0; i < 4 && !this.isOpenPath(this.entity.world, pos.up(i)); i++) {
	        			vec = vec.addVector(0d, 1.01d, 0d);
	        		}
       				this.entity.getMoveHelper().setMoveTo(vec.x, vec.y, vec.z, this.entity.getDistance(vec.x, vec.y, vec.z));
	        	}
	        }

	        private boolean isOpenPath(World world, BlockPos pos) {
        		return world.getBlockState(pos).getCollisionBoundingBox(world, pos) == null
        		 && world.getBlockState(pos.up()).getCollisionBoundingBox(world, pos.up()) == null;
	        }
	    }

	    /*public class AIChargeAttack extends EntityAIBase {
	    	private Base attacker;

	        public AIChargeAttack(Base attackerIn) {
	        	this.attacker = attackerIn;
	            this.setMutexBits(1);
	        }
	
	        @Override
	        public boolean shouldExecute() {
	            if (this.attacker.getAttackTarget() != null 
	             && !this.attacker.getMoveHelper().isUpdating() && this.attacker.rand.nextInt(5) == 0) {
	                return this.attacker.getDistanceSq(this.attacker.getAttackTarget()) > 4.0D;
	            }
                return false;
	        }
	
	        @Override
	        public boolean shouldContinueExecuting() {
	            return this.attacker.getMoveHelper().isUpdating()
	             && this.attacker.getAttackTarget() != null && this.attacker.getAttackTarget().isEntityAlive();
	        }
	
	        @Override
	        public void startExecuting() {
	            EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
	            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
	            this.attacker.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 2.0D);
	        }
	
	        @Override
	        public void updateTask() {
	            EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
	            if (this.attacker.getEntityBoundingBox().grow(2d).intersects(entitylivingbase.getEntityBoundingBox())) {
	            	this.attacker.swingArm(EnumHand.MAIN_HAND);
	                this.attacker.attackEntityAsMob(entitylivingbase);
	            } else if (this.attacker.getDistanceSq(entitylivingbase) < 9.0D) {
	                Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
	                this.attacker.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 2.0D);
	            }
	        }
	    }

	    public class AICopyOwnerTarget extends EntityAITarget {
	    	private Base attacker;

	        public AICopyOwnerTarget(Base creature) {
	            super(creature, false);
	            this.attacker = creature;
	        }
	
	        @Override
	        public boolean shouldExecute() {
	        	EntityLivingBase owner = this.attacker.getOwner();
	        	this.target = owner instanceof EntityLiving ? ((EntityLiving)owner).getAttackTarget() : owner != null
	        	 ? owner.getLastAttackedEntity() != null && owner.ticksExisted - owner.getLastAttackedEntityTime() <= 100
	        	 ? owner.getLastAttackedEntity() : null : null;
	            return this.target != null && this.isSuitableTarget(this.target, false);
	        }

	        @Override
	        public boolean shouldContinueExecuting() {
	        	EntityLivingBase owner = this.attacker.getOwner();
	        	return owner != null && owner.isEntityAlive() && super.shouldContinueExecuting();
	        }
	
	        @Override
	        public void startExecuting() {
	            this.attacker.setAttackTarget(this.target);
	            super.startExecuting();
	        }
	    }*/
	}

	public static class AIRidingHurtByTarget extends EntityAITarget {
	    EntityLivingBase attacker;
	    private int timestamp;
		
	    public AIRidingHurtByTarget(EntityCreature theDefendingCreatureIn) {
	        super(theDefendingCreatureIn, false);
	        this.setMutexBits(1);
	    }
		
	    @Override
	    public boolean shouldExecute() {
	        Entity entity = this.taskOwner.getRidingEntity();
	        if (!(entity instanceof EntityLivingBase)) {
	            return false;
	        } else {
	        	this.attacker = ((EntityLivingBase)entity).getRevengeTarget();
		        int i = ((EntityLivingBase)entity).getRevengeTimer();
		        return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
		    }
		}
		
		@Override
		public void startExecuting() {
		    this.taskOwner.setAttackTarget(this.attacker);
		    Entity entity = this.taskOwner.getRidingEntity();
		    if (entity instanceof EntityLivingBase) {
		        this.timestamp = ((EntityLivingBase)entity).getRevengeTimer();
		    }
		    super.startExecuting();
		}
	}

	public static class ClientClass {
		@SideOnly(Side.CLIENT)
		public static abstract class RenderScroll<T extends Entity> extends Render<T> {
			private final ModelScroll model = new ModelScroll();
	
			public RenderScroll(RenderManager renderManager) {
				super(renderManager);
				shadowSize = 0.1f;
			}
	
			@Override
			public void doRender(T bullet, double d, double d1, double d2, float f, float f1) {
				this.bindEntityTexture(bullet);
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) d, (float) d1, (float) d2);
				GlStateManager.scale(2.0f, 2.0f, 2.0f);
				GlStateManager.rotate(-f, 0, 1, 0);
				GlStateManager.rotate(180f - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * f1, 1, 0, 0);
				this.model.render(bullet, 0, 0, f1 + bullet.ticksExisted, 0, 0, 0.0625f);
				GlStateManager.popMatrix();
			}
		}
	
		// Made with Blockbench 4.4.2
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public static class ModelScroll extends ModelBase {
			private final ModelRenderer hinge;
			private final ModelRenderer[] bone = new ModelRenderer[14];
			public ModelScroll() {
				textureWidth = 16;
				textureHeight = 16;
				hinge = new ModelRenderer(this);
				hinge.setRotationPoint(0.0F, -0.85F, 0.0F);
				hinge.cubeList.add(new ModelBox(hinge, 0, 0, -4.0F, -0.5F, -0.5F, 4, 1, 1, 0.1F, false));
				hinge.cubeList.add(new ModelBox(hinge, 0, 0, 0.0F, -0.5F, -0.5F, 4, 1, 1, 0.1F, true));
				bone[0] = new ModelRenderer(this);
				bone[0].setRotationPoint(0.0F, 0.0F, 0.5F);
				setRotationAngle(bone[0], -1.5708F, 0.0F, 0.0F);
				bone[0].cubeList.add(new ModelBox(bone[0], 0, 2, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[1] = new ModelRenderer(this);
				bone[1].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[0].addChild(bone[1]);
				setRotationAngle(bone[1], -1.0472F, 0.0F, 0.0F);
				bone[1].cubeList.add(new ModelBox(bone[1], 0, 3, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[2] = new ModelRenderer(this);
				bone[2].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[1].addChild(bone[2]);
				setRotationAngle(bone[2], -1.0472F, 0.0F, 0.0F);
				bone[2].cubeList.add(new ModelBox(bone[2], 0, 4, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[3] = new ModelRenderer(this);
				bone[3].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[2].addChild(bone[3]);
				setRotationAngle(bone[3], -1.0472F, 0.0F, 0.0F);
				bone[3].cubeList.add(new ModelBox(bone[3], 0, 5, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[4] = new ModelRenderer(this);
				bone[4].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[3].addChild(bone[4]);
				setRotationAngle(bone[4], -1.0472F, 0.0F, 0.0F);
				bone[4].cubeList.add(new ModelBox(bone[4], 0, 6, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[5] = new ModelRenderer(this);
				bone[5].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[4].addChild(bone[5]);
				setRotationAngle(bone[5], -1.0472F, 0.0F, 0.0F);
				bone[5].cubeList.add(new ModelBox(bone[5], 0, 7, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[6] = new ModelRenderer(this);
				bone[6].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[5].addChild(bone[6]);
				setRotationAngle(bone[6], -1.0472F, 0.0F, 0.0F);
				bone[6].cubeList.add(new ModelBox(bone[6], 0, 8, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[7] = new ModelRenderer(this);
				bone[7].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[6].addChild(bone[7]);
				setRotationAngle(bone[7], -1.0472F, 0.0F, 0.0F);
				bone[7].cubeList.add(new ModelBox(bone[7], 0, 9, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[8] = new ModelRenderer(this);
				bone[8].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[7].addChild(bone[8]);
				setRotationAngle(bone[8], -1.0472F, 0.0F, 0.0F);
				bone[8].cubeList.add(new ModelBox(bone[8], 0, 10, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[9] = new ModelRenderer(this);
				bone[9].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[8].addChild(bone[9]);
				setRotationAngle(bone[9], -1.0472F, 0.0F, 0.0F);
				bone[9].cubeList.add(new ModelBox(bone[9], 0, 11, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[10] = new ModelRenderer(this);
				bone[10].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[9].addChild(bone[10]);
				setRotationAngle(bone[10], -1.0472F, 0.0F, 0.0F);
				bone[10].cubeList.add(new ModelBox(bone[10], 0, 12, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[11] = new ModelRenderer(this);
				bone[11].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[10].addChild(bone[11]);
				setRotationAngle(bone[11], -1.0472F, 0.0F, 0.0F);
				bone[11].cubeList.add(new ModelBox(bone[11], 0, 13, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[12] = new ModelRenderer(this);
				bone[12].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[11].addChild(bone[12]);
				setRotationAngle(bone[12], -1.0472F, 0.0F, 0.0F);
				bone[12].cubeList.add(new ModelBox(bone[12], 0, 14, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[13] = new ModelRenderer(this);
				bone[13].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[12].addChild(bone[13]);
				setRotationAngle(bone[13], -1.0472F, 0.0F, 0.0F);
				bone[13].cubeList.add(new ModelBox(bone[13], 0, 15, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
				hinge.render(f5);
				bone[0].render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				for (int i = 1; i < bone.length; i++) {
					bone[i].rotateAngleX = MathHelper.clamp(1.0F - f2 + i, 0.0F, 1.0F) * -1.0472F;
				}
			}
		}
	
		@SideOnly(Side.CLIENT)
		public abstract static class Renderer<T extends Base> extends RenderLiving<T> {
			private static final ResourceLocation FUUIN_TEXTURE = new ResourceLocation("narutomod:textures/fuuin_beam_blue.png");

			public Renderer(RenderManager renderManagerIn, ModelBase model, float shadowsize) {
				super(renderManagerIn, model, shadowsize);
			}

			@Override
			public void doRender(T entity, double x, double y, double z, float entityYaw, float pt) {
				super.doRender(entity, x, y, z, entityYaw, pt);
				EntityLivingBase owner = entity.getOwner();
				if (owner != null) {
					this.renderLine(this.getPosVec(entity, pt).addVector(0d, 1.2d, 0d), this.transform3rdPerson(owner, entity.chakraStringAttachesTo(), pt), pt + entity.ticksExisted);
				}
			}

			@Override
			protected void renderModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
				if (entity.getOwner() == null && entity.onGround) {
					this.mainModel.isRiding = true;
					headPitch = 45.0f;
					GlStateManager.translate(0.0f, 0.55f, 0.0f);
				}
				super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
			}

			private Vec3d transform3rdPerson(EntityLivingBase entity, @Nullable EnumHandSide hand, float pt) {
				ModelBase model = ((RenderLivingBase)this.renderManager.getEntityRenderObject(entity)).getMainModel();
				if (hand != null && model instanceof ModelBiped) {
					ModelRenderer arm = hand == EnumHandSide.RIGHT ? ((ModelBiped)model).bipedRightArm : ((ModelBiped)model).bipedLeftArm;
					return ProcedureUtils.rotateRoll(new Vec3d(0.0d, -0.5825d, 0.0d),
					   (float)-arm.rotateAngleZ).rotatePitch((float)-arm.rotateAngleX).rotateYaw((float)-arm.rotateAngleY)
					   .addVector(0.0586F * (hand == EnumHandSide.RIGHT ? -6F : 6F), 1.02F-(entity.isSneaking()?0.3f:0f), 0.0F)
					   .rotateYaw(-ProcedureUtils.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, pt) * (float)(Math.PI / 180d))
					   .add(this.getPosVec(entity, pt)).addVector(0.0d, 0.275d, 0.0d);
				} else {
					return this.getPosVec(entity, pt).addVector(0d, 1.2d, 0d);
				}
			}
	
			private Vec3d getPosVec(Entity entity, float pt) {
				Vec3d vec1 = new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ);
				return entity.getPositionVector().subtract(vec1).scale(pt).add(vec1);
			}

			private void renderLine(Vec3d from, Vec3d to, float ageInTicks) {
				this.bindTexture(FUUIN_TEXTURE);
				Vec3d vec3d = to.subtract(from);
				float yaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180d / Math.PI));
				float pitch = (float) (-MathHelper.atan2(vec3d.y, MathHelper.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z)) * (180d / Math.PI));
				GlStateManager.pushMatrix();
				GlStateManager.translate(from.x - this.renderManager.viewerPosX, from.y - this.renderManager.viewerPosY, from.z - this.renderManager.viewerPosZ);
				GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
				double d = vec3d.lengthVector();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				GlStateManager.disableLighting();
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.shadeModel(7425);
				float f = ageInTicks * 0.01F;
				float f5 = 0.0F - f;
				float f6 = (float) d / 32.0F - f;
				bufferbuilder.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
				for (int j = 0; j <= 8; j++) {
					float f7 = MathHelper.sin((j % 8) * ((float) Math.PI * 2F) / 8.0F) * 0.008F;
					float f8 = MathHelper.cos((j % 8) * ((float) Math.PI * 2F) / 8.0F) * 0.008F;
					float f9 = (j % 8) / 8.0F;
					bufferbuilder.pos(f7, f8, 0.0D).tex(f9, f5).color(255, 255, 255, 128).endVertex();
					bufferbuilder.pos(f7, f8, d).tex(f9, f6).color(255, 255, 255, 128).endVertex();
				}
				tessellator.draw();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.shadeModel(7424);
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		}
	}
}
