
package net.narutomod.entity;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumHandSide;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;

import net.narutomod.item.ItemOnBody;
import net.narutomod.potion.PotionFeatherFalling;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodMod;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Arrays;
import com.google.common.collect.Lists;
import java.io.IOException;

@ElementsNarutomodMod.ModElement.Tag
public class EntityNinjaMob extends ElementsNarutomodMod.ModElement {
	public static final List<Class <? extends Base>> TeamKonoha = Arrays.asList(EntityTenten.EntityCustom.class, EntitySakuraHaruno.EntityCustom.class, EntityIrukaSensei.EntityCustom.class, EntityMightGuy.EntityCustom.class);
	public static final List<Class <? extends Base>> TeamZabuza = Arrays.asList(EntityZabuzaMomochi.EntityCustom.class, EntityHaku.EntityCustom.class);
	public static final List<Class <? extends Base>> TeamItachi = Arrays.asList(EntityItachi.EntityCustom.class, EntityKisameHoshigaki.EntityCustom.class);

	public EntityNinjaMob(ElementsNarutomodMod instance) {
		super(instance, 404);
	}

	public static abstract class Base extends EntityCreature {
		private static final DataParameter<Float> CHAKRA_MAX = EntityDataManager.createKey(Base.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> CHAKRA = EntityDataManager.createKey(Base.class, DataSerializers.FLOAT);
		private final PathwayNinjaMob chakraPathway;
		private static final int inventorySize = 2;
		private final NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(inventorySize, ItemStack.EMPTY);
		public int peacefulTicks;
		private int standStillTicks;

		public Base(World worldIn, int level, double chakraAmountIn) {
			super(worldIn);
			this.setSize(0.6f, 1.8f);
			this.experienceValue = level;
			this.isImmuneToFire = false;
			this.stepHeight = 16f;
			this.moveHelper = new MoveHelper(this);
			this.setNoAI(false);
			this.setCanPickUpLoot(false);
			this.setCustomNameTag(this.getName());
			this.setAlwaysRenderNameTag(true);
			this.chakraPathway = new PathwayNinjaMob(this, chakraAmountIn);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50d + 0.005d * level * level);
			// .applyModifier(new AttributeModifier(NINJA_HEALTH, "ninja.maxhealth", 0.005d * level * level, 0));
			this.setHealth(this.getMaxHealth());
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			PathNavigateGround navi = new NavigateGround(this, worldIn);
			navi.setCanSwim(true);
			return navi;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(CHAKRA_MAX, Float.valueOf(0.0F));
			this.getDataManager().register(CHAKRA, Float.valueOf(0.0F));
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10D);
			//this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64D);
		}

		@Override
		public int getMaxFallHeight() {
			return 12;
		}

		@Override
		public boolean canBeLeashedTo(EntityPlayer player) {
			return player.isCreative();
		}

		public int getNinjaLevel() {
			return this.experienceValue;
		}

		public PathwayNinjaMob getChakraPathway() {
			return this.chakraPathway;
		}

		public double getChakra() {
			return this.chakraPathway.getAmount();
		}

		public boolean consumeChakra(double amount) {
			return this.chakraPathway.consume(amount);
		}

		private void fixOnClientSpawn() {
			if (this.world.isRemote && this.ticksExisted < 20) {
				this.chakraPathway.fixOnClientSpawn();
			}
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			EntityLivingBase target = this.getAttackTarget();
			//if (target != null && (!target.isEntityAlive()
			// || (target.isInvisible() && !ItemSharingan.wearingAny(this) && !ItemByakugan.wearingAny(this)))) {
			//	this.setAttackTarget(null);
			//}
			if (ProcedureUtils.isWeapon(this.getItemFromInventory(0)) || ProcedureUtils.isWeapon(this.getHeldItemMainhand())) {
				boolean flag = this.getRevengeTarget() != null || target != null
				 || (this.getLastAttackedEntity() != null && this.ticksExisted <= this.getLastAttackedEntityTime() + 100);
				if (this.getHeldItemMainhand().isEmpty() == flag) {
					this.swapWithInventory(EntityEquipmentSlot.MAINHAND, 0);
				}
			}
		}

		@Override
		public void onUpdate() {
			this.fixOnClientSpawn();
			super.onUpdate();
			BlockPos pos = new BlockPos(this);
			if (this.navigator instanceof PathNavigateGround
			 && this.world.getBlockState(pos).getMaterial() == Material.WATER
			 && this.world.getBlockState(pos.up()).getMaterial() != Material.WATER) {
				this.motionY = 0.01d;
				this.onGround = true;
			}
			if (!this.world.isRemote && this.isEntityAlive()) {
				if (this.ticksExisted % 200 == 1) {
					this.addPotionEffect(new PotionEffect(PotionFeatherFalling.potion, 201, 1, false, false));
				}
				this.chakraPathway.onUpdate();
				if (this instanceof IMob && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
					this.setDead();
				}
			}
		}

		@Override
		public void travel(float strafe, float vertical, float forward) {
			if (this.standStillTicks > 0) {
				vertical = forward = strafe = 0.0f;
				//this.motionX = this.motionZ = 0.0f;
				--this.standStillTicks;
			}
			super.travel(strafe, vertical, forward);
		}

		public void standStillFor(int ticks) {
			this.standStillTicks = ticks;
			StandStillMessage.sendToTracking(this);
		}

		@Override
		public void onLivingUpdate() {
			this.decrementAnimations();
			super.onLivingUpdate();
		}

		@Override
		protected float getWaterSlowDown() {
			return 0.98f;
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			return ProcedureUtils.attackEntityAsMob(this, entityIn);
		}

		protected void decrementAnimations() {
			this.updateArmSwingProgress();
			for (ItemStack stack : this.getHeldEquipment()) {
				if (!stack.isEmpty())
					stack.updateAnimation(this.world, this, 0, false);
			}
			for (ItemStack stack : this.inventory) {
				if (!stack.isEmpty())
					stack.updateAnimation(this.world, this, 0, false);
			}
		}

		public ItemStack getItemFromInventory(int slotno) {
			return slotno >= 0 && slotno < this.inventory.size() ? this.inventory.get(slotno) : ItemStack.EMPTY;
		}

		public void setItemToInventory(ItemStack stack, int slotno) {
			if (slotno >= 0 && slotno < this.inventory.size()) {
				this.inventory.set(slotno, stack);
			}
		}

		public void swapWithInventory(EntityEquipmentSlot slot, int slotno) {
			ItemStack stack = this.inventory.get(slotno);
			this.inventory.set(slotno, this.getItemStackFromSlot(slot));
			this.setItemStackToSlot(slot, stack);
			InventoryMessage.sendToTracking(this);
		}

	    protected boolean isValidLightLevel() {
	        BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
	        if (this.world.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)) {
	            return false;
	        } else {
	            int i = this.world.getLightFromNeighbors(blockpos);
	            if (this.world.isThundering()) {
	                int j = this.world.getSkylightSubtracted();
	                this.world.setSkylightSubtracted(10);
	                i = this.world.getLightFromNeighbors(blockpos);
	                this.world.setSkylightSubtracted(j);
	            }
	            return i <= this.rand.nextInt(8);
	        }
	    }

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere() && (this instanceof IMob ? this.world.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevel() : true);
		}

		@Override
		public SoundCategory getSoundCategory() {
			return this instanceof IMob ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return null;
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return this instanceof IMob ? SoundEvents.ENTITY_HOSTILE_HURT : SoundEvents.ENTITY_GENERIC_HURT;
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return this instanceof IMob ? SoundEvents.ENTITY_HOSTILE_DEATH : SoundEvents.ENTITY_GENERIC_DEATH;
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		public Vec3d getLookVec() {
			return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead); 
		}

		protected boolean canSeeInvisible(Entity entityIn) {
			if (entityIn.isInvisible()) {
				double d0 = entityIn.getDistance(entityIn);
				double d1 = ProcedureUtils.getFollowRange(this);
				return d0 <= d1 * (entityIn.equals(this.getRevengeTarget()) 
				 || entityIn.equals(this.getLastAttackedEntity()) || entityIn.equals(this.getAttackTarget()) ? 0.25d : 0.1d);
			}
			return true;
		}

		@Override
		public boolean canEntityBeSeen(Entity entityIn) {
			return super.canEntityBeSeen(entityIn) && this.canSeeInvisible(entityIn);
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setDouble("maxChakra", this.chakraPathway.getMax());
			compound.setDouble("chakra", this.getChakra());
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < this.inventory.size(); i++) {
				ItemStack stack = this.inventory.get(i);
				if (!stack.isEmpty()) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setInteger("slotNo", i);
					stack.writeToNBT(nbttagcompound);
					nbttaglist.appendTag(nbttagcompound);
				}
			}
			compound.setTag("sideInventory", nbttaglist);
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.chakraPathway.setMax(compound.getDouble("maxChakra"));
			this.chakraPathway.set(compound.getDouble("chakra"));
			if (compound.hasKey("sideInventory", 9)) {
				NBTTagList nbttaglist = compound.getTagList("sideInventory", 10);
				for (int i = 0; i < nbttaglist.tagCount(); ++i) {
					NBTTagCompound cmp = nbttaglist.getCompoundTagAt(i);
					int j = cmp.getInteger("slotNo");
					if (j >= 0 && j < this.inventory.size()) {
						this.inventory.set(j, new ItemStack(cmp));
					}
				}
				//InventoryMessage.sendToTracking(this);
			}
		}

		public class PathwayNinjaMob extends Chakra.Pathway<Base> {
			protected PathwayNinjaMob(Base entityIn, double max) {
				super(entityIn);
				this.setMax(max);
				this.set(max);
			}

			private float getMaxFromSync() {
				return ((Float)Base.this.getDataManager().get(Base.CHAKRA_MAX)).floatValue();
			}
	
			@Override
			public double getMax() {
				float f = this.getMaxFromSync();
				return f == 0f ? super.getMax() : (double)f;
			}

			@Override
			public Chakra.Pathway<Base> setMax(double d) {
				Base.this.getDataManager().set(Base.CHAKRA_MAX, Float.valueOf((float)d));
				return super.setMax(d);
			}

			private void fixOnClientSpawn() {
				if ((double)this.getMaxFromSync() != super.getMax()) {
					NarutomodMod.PACKET_HANDLER.sendToServer(new ChakraMessage(Base.this, super.getMax(), super.getAmount()));
				}
			}

			@Override
			public double getAmount() {
				return this.getMaxFromSync() == 0f ? super.getAmount()
				  : (double)((Float)Base.this.getDataManager().get(Base.CHAKRA)).floatValue();
			}

			@Override
			protected void set(double amountIn) {
				Base.this.getDataManager().set(Base.CHAKRA, Float.valueOf((float)amountIn));
				super.set(amountIn);
			}

			@Override
			protected void onUpdate() {
				//Base usr = (Base)this.user;
				if ((this.user.getAttackTarget() == null || !this.user.getAttackTarget().isEntityAlive()) 
				 && (this.user.getAttackingEntity() == null || !this.user.getAttackingEntity().isEntityAlive())) {
					++this.user.peacefulTicks;
					if (this.user.peacefulTicks % 20 == 19) {
						this.consume(-this.getMax() * 0.04d);
						if (this.user.getHealth() < this.user.getMaxHealth()) {
							this.user.setHealth(this.user.getHealth() + 1.0f);
						}
					}
				} else {
					this.user.peacefulTicks = 0;
				}
			}
		}
	}

	public static class AILeapAtTarget extends EntityAIBase {
	    protected EntityLiving leaper;
	    protected EntityLivingBase target;
	    protected float leapStrength;
	
	    public AILeapAtTarget(EntityLiving leapingEntity, float leapStrengthIn) {
	        this.leaper = leapingEntity;
	        this.leapStrength = leapStrengthIn;
	        this.setMutexBits(5);
	    }
	
	    public boolean shouldExecute() {
	        this.target = this.leaper.getAttackTarget();
	        if (this.target == null) {
	            return false;
	        } else {
	            double d0 = this.leaper.getDistance(this.target);
	            if (d0 >= 3.0D && d0 <= this.leapStrength * 12.0d && this.leaper.onGround) {
                    return this.leaper.getRNG().nextInt(5) == 0;
	            } else {
	                return false;
	            }
	        }
	    }
	
	    public boolean shouldContinueExecuting() {
	        return !this.leaper.onGround;
	    }
	
	    public void startExecuting() {
	        double d0 = this.target.posX - this.leaper.posX;
	        double d1 = this.target.posZ - this.leaper.posZ;
	        double d4 = MathHelper.sqrt(d0 * d0 + d1 * d1);
	        double d2 = this.target.posY + (double)this.target.height / 3d - this.leaper.posY + d4 * 0.2d;
	        double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	        if (d3 >= 1.0E-4D) {
	            this.leaper.motionX = d0 / d3 * (double)this.leapStrength;
	            this.leaper.motionZ = d1 / d3 * (double)this.leapStrength;
	        	this.leaper.motionY = d2 / d3 * (double)this.leapStrength;
	        }
	    }
	}	

	public static class AIAttackRangedTactical<T extends EntityCreature & IRangedAttackMob> extends EntityAIBase {
	    protected final T entity;
	    private final double moveSpeedAmp;
	    private int attackCooldown;
	    private final float attackRadius;
	    private final float maxAttackDistance;
	    private int attackTime = -1;
	    private int seeTime;
	    private boolean strafingClockwise;
	    private boolean strafingBackwards;
	    private int strafingTime = -1;
	
	    public AIAttackRangedTactical(T entityIn, double moveSpeed, int cooldown, float maxDistance) {
	        this.entity = entityIn;
	        this.moveSpeedAmp = moveSpeed;
	        this.attackCooldown = cooldown;
	        this.attackRadius = maxDistance;
	        this.maxAttackDistance = maxDistance * maxDistance;
	        this.setMutexBits(3);
	    }
	
	    public boolean shouldExecute() {
	        return this.entity.getAttackTarget() != null;
	    }
	
	    public boolean shouldContinueExecuting() {
	        return this.shouldExecute() || !this.entity.getNavigator().noPath();
	    }
	
	    public void startExecuting() {
	        super.startExecuting();
	        ((IRangedAttackMob)this.entity).setSwingingArms(true);
	    }
	
	    public void resetTask() {
	        super.resetTask();
	        ((IRangedAttackMob)this.entity).setSwingingArms(false);
	        this.seeTime = 0;
	        this.attackTime = -1;
	        this.entity.resetActiveHand();
	    }
	
	    public void updateTask() {
	        EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
	        if (entitylivingbase != null) {
	            double d0 = this.entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
	            boolean flag = this.entity.getEntitySenses().canSee(entitylivingbase);
	            boolean flag1 = this.seeTime > 0;	
	            if (flag != flag1) {
	                this.seeTime = 0;
	            }
	            if (flag) {
	                ++this.seeTime;
	            } else {
	                --this.seeTime;
	            }
	            if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20) {
	                this.entity.getNavigator().clearPath();
	                ++this.strafingTime;
	            } else {
	                this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.moveSpeedAmp);
	                this.strafingTime = -1;
	            }
	            if (this.strafingTime >= 20) {
	                if ((double)this.entity.getRNG().nextFloat() < 0.3D) {
	                    this.strafingClockwise = !this.strafingClockwise;
	                }
	                if ((double)this.entity.getRNG().nextFloat() < 0.3D) {
	                    this.strafingBackwards = !this.strafingBackwards;
	                }
	                this.strafingTime = 0;
	            }
	            if (this.strafingTime > -1) {
	                if (d0 > (double)(this.maxAttackDistance * 0.75F)) {
	                    this.strafingBackwards = false;
	                } else if (d0 < (double)(this.maxAttackDistance * 0.25F)) {
	                    this.strafingBackwards = true;
	                }
	                this.entity.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
	                this.entity.faceEntity(entitylivingbase, 30.0F, 30.0F);
	            } else {
	                this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
	            }
		        if (--this.attackTime == 0) {
		            if (!flag) {
		                return;
		            }
		            float f = MathHelper.sqrt(d0) / this.attackRadius;
		            float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
		            this.entity.attackEntityWithRangedAttack(entitylivingbase, lvt_5_1_);
		            this.attackTime = MathHelper.floor(f * (float)(this.attackCooldown));
		        } else if (this.attackTime < 0) {
		            float f = MathHelper.sqrt(d0) / this.attackRadius;
		            this.attackTime = MathHelper.floor(f * (float)(this.attackCooldown));
		        }
	        }
	    }
	}

	public static class AIAttackRangedJutsu<T extends EntityLiving & IRangedAttackMob> extends EntityAIBase {
	    protected final T entity;
	    private int attackCooldown;
	    private final float attackRadius;
	    private int attackTime;
	    private Vec3d targetPos;
	    private boolean strafingBackwards;
	    private int strafingTime = -1;

	    public AIAttackRangedJutsu(T entityIn, int cooldown, float maxDistance) {
	        this.entity = entityIn;
	        this.attackCooldown = cooldown;
	        this.attackTime = cooldown;
	        this.attackRadius = maxDistance;
	        this.setMutexBits(3);
	    }		

	    @Override
	    public boolean shouldExecute() {
	    	--this.attackTime;
	    	EntityLivingBase target = this.entity.getAttackTarget();
//System.out.println(">>> target:"+(target!=null?target.getName():"null")+", onGround:"+entity.onGround+", attackTime:"+attackTime);
	        if (target != null && this.attackTime <= 0) {
	        	this.attackTime = 0;
	        	Vec3d vec = this.entity.getPositionVector().subtract(target.getPositionVector()).normalize()
	        	 .scale(this.attackRadius).add(this.entity.getPositionVector());
	        	for (double d = vec.y - 3d; d < vec.y + 7d; d += 1.0d) {
	        		BlockPos pos = new BlockPos(vec.x, d, vec.z);
	        		if (this.entity.getNavigator().canEntityStandOnPos(pos)) {
	        			this.targetPos = new Vec3d(pos);
	        			return true;
	        		}
	        	}
	        }
	        return false;
	    }

	    @Override
	    public boolean shouldContinueExecuting() {
	    	return this.targetPos != null && --this.attackTime >= -100;
	    }

	    @Override
	    public void startExecuting() {
	        double d0 = this.targetPos.x - this.entity.posX;
	        double d1 = this.targetPos.z - this.entity.posZ;
	        double d4 = MathHelper.sqrt(d0 * d0 + d1 * d1);
	        double d2 = this.targetPos.y - this.entity.posY + d4 * 0.2d;
	        double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	        if (d3 >= 1.0E-4D) {
	            this.entity.motionX = d0 / d3;
	            this.entity.motionZ = d1 / d3;
	        	this.entity.motionY = d2 / d3;
	        }
	    }

	    @Override
	    public void updateTask() {
	        EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
	        if (entitylivingbase != null) {
	            double d0 = this.entity.getDistance(entitylivingbase);
	            if (d0 < (double)this.attackRadius - 2.0d) {
	                this.entity.getNavigator().clearPath();
	                ++this.strafingTime;
	            } else if (d0 > (double)this.attackRadius + 2.0d) {
	                this.entity.getNavigator().tryMoveToEntityLiving(entitylivingbase, 1.0d);
	                this.strafingTime = -1;
	            } else {
		            float f = (float)d0 / this.attackRadius;
		            this.entity.attackEntityWithRangedAttack(entitylivingbase, MathHelper.clamp(f, 0.1F, 1.0F));
		            this.attackTime = MathHelper.floor(f * (float)(this.attackCooldown));
		            this.targetPos = null;
		            return;
	            }
	            if (this.strafingTime >= 20) {
	                if ((double)this.entity.getRNG().nextFloat() < 0.3D) {
	                    this.strafingBackwards = !this.strafingBackwards;
	                }
	                this.strafingTime = 0;
	            }
	            if (this.strafingTime > -1) {
	                if (d0 > (double)this.attackRadius + 2.0d) {
	                    this.strafingBackwards = false;
	                } else if (d0 < (double)this.attackRadius - 2.0d) {
	                    this.strafingBackwards = true;
	                }
	                this.entity.getMoveHelper().strafe(this.strafingBackwards ? -1.25F : 1.25F, 0.0F);
	                this.entity.faceEntity(entitylivingbase, 30.0F, 30.0F);
	            } else {
	                this.entity.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
	            }
	        }
	    }
	}

	public static class AIDefendEntity extends EntityAITarget {
	    private final EntityLivingBase defendedEntity;

		public AIDefendEntity(Base ninjamob, EntityLivingBase protectedEntity) {
			super(ninjamob, false);
			this.defendedEntity = protectedEntity;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			this.target = this.defendedEntity.getRevengeTarget();
			if (this.target == null) {
				EntityLivingBase living = this.defendedEntity.getLastAttackedEntity();
				if (living != null && this.defendedEntity.ticksExisted - this.defendedEntity.getLastAttackedEntityTime() < 200) {
					this.target = living;
				}
			}
			if (this.target == null && this.defendedEntity instanceof EntityLiving) {
				this.target = ((EntityLiving)this.defendedEntity).getAttackTarget();
			}
			return this.target != null && this.isSuitableTarget(this.target, false);
		}

		@Override
		public void startExecuting() {
			this.taskOwner.setAttackTarget(this.target);
			super.startExecuting();
		}
	}

	public static class NavigateGround extends PathNavigateGround {
		private BlockPos targetPosition;
	
		public NavigateGround(EntityLiving entityLivingIn, World worldIn) {
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
		            if (d2 >= d0 && d1 <= this.entity.stepHeight && d1 >= -12d * this.entity.height) {
		              	this.entity.getMoveHelper().setMoveTo((double)this.targetPosition.getX() + 0.5d,
		               	 (double)this.targetPosition.getY(), (double)this.targetPosition.getZ() + 0.5d, this.speed);
		            } else {
		                this.targetPosition = null;
		            }
		        }
		    }
		}
	}

	public static class MoveHelper extends EntityMoveHelper {
		public MoveHelper(EntityCreature entityIn) {
			super(entityIn);
		}

		@Override
		public void onUpdateMoveHelper() {
			if (this.isUpdating()) {
	            this.action = EntityMoveHelper.Action.WAIT;
	            double d0 = this.posX - this.entity.posX;
	            double d1 = this.posZ - this.entity.posZ;
	            double d2 = this.posY - this.entity.posY;
	            double d3 = d0 * d0 + d2 * d2 + d1 * d1;
	            if (d3 < 2.5E-7D) {
	                this.entity.setMoveForward(0.0F);
	                return;
	            }
	            float f9 = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
	            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f9, 90.0F);
	            this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
	            if (d2 > 0.01d && this.entity.collidedHorizontally) {
	            	this.entity.motionY = 0.42d;
	            } else if (d2 > (double)this.entity.stepHeight && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.entity.width)) {
	                this.entity.getJumpHelper().setJumping();
	                this.action = EntityMoveHelper.Action.JUMPING;
	            }
			} else {
				super.onUpdateMoveHelper();
			}
		}
	}

	public static class SwimHelper extends EntityMoveHelper {
		public SwimHelper(EntityLiving entityIn) {
			super(entityIn);
		}
			
		@Override
		public void onUpdateMoveHelper() {
			if (this.action == EntityMoveHelper.Action.MOVE_TO) {
				this.action = EntityMoveHelper.Action.WAIT;
				double d0 = this.posX - this.entity.posX;
				double d1 = this.posY - this.entity.posY;
				double d2 = this.posZ - this.entity.posZ;
				double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
				if (d3 < 1.6E-7D) {
					ProcedureUtils.multiplyVelocity(this.entity, 0.0d);
				} else {
					float f = (float)(this.speed * ProcedureUtils.getModifiedSpeed(this.entity));
					this.entity.motionX = d0 / d3 * f;
					this.entity.motionY = d1 / d3 * f;
					this.entity.motionZ = d2 / d3 * f;
					float f1 = -((float)MathHelper.atan2(this.entity.motionX, this.entity.motionZ)) * (180F / (float)Math.PI);
					this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f1, 10.0F);
					this.entity.renderYawOffset = this.entity.rotationYaw;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static abstract class RenderBase<T extends Base> extends RenderBiped<T> {
		public RenderBase(RenderManager renderManager, ModelBiped model) {
			super(renderManager, model, 0.5f);
			this.addLayer(EntityClone.ClientRLM.getInstance().new BipedArmorLayer(this));
			this.addLayer(new LayerInventoryItem(this));
		}

		@Override
		public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
			this.setPose(entity);
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

	    private void setPose(T entity) {
	    	ModelBiped model = (ModelBiped)this.getMainModel();
            ItemStack itemstack = entity.getHeldItemMainhand();
            ItemStack itemstack1 = entity.getHeldItemOffhand();
            ModelBiped.ArmPose mainhandpose = ModelBiped.ArmPose.EMPTY;
            ModelBiped.ArmPose offhandpose = ModelBiped.ArmPose.EMPTY;
            if (!itemstack.isEmpty()) {
                mainhandpose = ModelBiped.ArmPose.ITEM;
                if (entity.getItemInUseCount() > 0) {
                    EnumAction enumaction = itemstack.getItemUseAction();
                    if (enumaction == EnumAction.BLOCK) {
                        mainhandpose = ModelBiped.ArmPose.BLOCK;
                    } else if (enumaction == EnumAction.BOW) {
                        mainhandpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }
            if (!itemstack1.isEmpty()) {
                offhandpose = ModelBiped.ArmPose.ITEM;
                if (entity.getItemInUseCount() > 0) {
                    EnumAction enumaction1 = itemstack1.getItemUseAction();
                    if (enumaction1 == EnumAction.BLOCK) {
                        offhandpose = ModelBiped.ArmPose.BLOCK;
                    } else if (enumaction1 == EnumAction.BOW) {
                        offhandpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }
            if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
                model.rightArmPose = mainhandpose;
                model.leftArmPose = offhandpose;
            } else {
                model.rightArmPose = offhandpose;
                model.leftArmPose = mainhandpose;
            }
	    }
	}

	@SideOnly(Side.CLIENT)
	public static class LayerInventoryItem implements LayerRenderer<Base> {
		private final RenderBiped renderer;

		public LayerInventoryItem(RenderBiped rendererIn) {
			this.renderer = rendererIn;
		}

		@Override
		public void doRenderLayer(Base entityIn, float f1, float f2, float f3, float f4, float f5, float f6, float f7) {
			for (int i = 0; i < entityIn.inventory.size(); ++i) {
				ItemStack stack = entityIn.inventory.get(i);
				if (stack.getItem() instanceof ItemOnBody.Interface) {
					ItemOnBody.Interface item = (ItemOnBody.Interface)stack.getItem();
					if (item.showOnBody() != ItemOnBody.BodyPart.NONE) {
						Vec3d offset = item.getOffset();
						GlStateManager.pushMatrix();
						ModelBiped model = (ModelBiped)this.renderer.getMainModel();
						switch (item.showOnBody()) {
							case HEAD:
								model.bipedHead.postRender(0.0625F);
								break;
							case TORSO:
								model.bipedBody.postRender(0.0625F);
								break;
							case RIGHT_ARM:
								model.bipedRightArm.postRender(0.0625F);
								break;
							case LEFT_ARM:
								model.bipedLeftArm.postRender(0.0625F);
								break;
							case RIGHT_LEG:
								model.bipedRightLeg.postRender(0.0625F);
								break;
							case LEFT_LEG:
								model.bipedLeftLeg.postRender(0.0625F);
								break;
						}
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
						GlStateManager.translate(offset.x, -0.25F + offset.y, offset.z);
						GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
						GlStateManager.scale(0.625F, -0.625F, -0.625F);
						Minecraft.getMinecraft().getItemRenderer().renderItem(entityIn, stack, ItemCameraTransforms.TransformType.HEAD);
						GlStateManager.popMatrix();
					}
				}
			}
		}

		@Override
		public boolean shouldCombineTextures() {
			return false;
		}
	}

	public static class ChakraMessage implements IMessage {
		int id;
		double d0;
		double d1;

		public ChakraMessage() {
		}

		public ChakraMessage(Base entity, double max, double chakra) {
			this.id = entity.getEntityId();
			this.d0 = max;
			this.d1 = chakra;
		}

		public static class Handler implements IMessageHandler<ChakraMessage, IMessage> {
			@Override
			public IMessage onMessage(ChakraMessage message, MessageContext context) {
				EntityPlayerMP entity = context.getServerHandler().player;
				entity.getServerWorld().addScheduledTask(() -> {
					World world = entity.world;
					if (!world.isBlockLoaded(new BlockPos(entity.posX, entity.posY, entity.posZ)))
						return;
					Entity entity1 = world.getEntityByID(message.id);
					if (entity1 instanceof Base) {
						((Base)entity1).chakraPathway.setMax(message.d0);
						((Base)entity1).chakraPathway.set(message.d1);
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeDouble(this.d0);
			buf.writeDouble(this.d1);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.d0 = buf.readDouble();
			this.d1 = buf.readDouble();
		}
	}

	public static class InventoryMessage implements IMessage {
		int id;
		List<ItemStack> list;

		public InventoryMessage() {
		}

		public InventoryMessage(Base entity) {
			this.id = entity.getEntityId();
			this.list = entity.inventory;
		}

		public static void sendToTracking(Base entity) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new InventoryMessage(entity), entity);
		}

		public static void sendTo(EntityPlayerMP player, Base entity) {
			NarutomodMod.PACKET_HANDLER.sendTo(new InventoryMessage(entity), player);
		}

		public static class Handler implements IMessageHandler<InventoryMessage, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(InventoryMessage message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					Entity entity = mc.world.getEntityByID(message.id);
					if (entity instanceof Base) {
						for (int i = 0; i < message.list.size() && i < Base.inventorySize; i++) {
							((Base)entity).inventory.set(i, message.list.get(i));
						}
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			PacketBuffer pbuf = new PacketBuffer(buf);
			pbuf.writeInt(this.id);
			int j = this.list.size();
			pbuf.writeInt(j);
			for (int i = 0; i < j; i++) {
				pbuf.writeItemStack(this.list.get(i));
			}
		}

		public void fromBytes(ByteBuf buf) {
			PacketBuffer pbuf = new PacketBuffer(buf);
			this.id = pbuf.readInt();
			int j = pbuf.readInt();
			this.list = Lists.newArrayList();
			try {
				for (int i = 0; i < j; i++) {
					this.list.add(pbuf.readItemStack());
				}
			} catch (Exception e) {
				new IOException("NinjaMob@inventory packet: ", e);
			}
		}
	}

	public static class StandStillMessage implements IMessage {
		int id;
		int ticks;

		public StandStillMessage() { }

		public StandStillMessage(Base entity) {
			this.id = entity.getEntityId();
			this.ticks = entity.standStillTicks;
		}

		public static void sendToTracking(Base entity) {
			NarutomodMod.PACKET_HANDLER.sendToAllTracking(new StandStillMessage(entity), entity);
		}

		public static class Handler implements IMessageHandler<StandStillMessage, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(StandStillMessage message, MessageContext context) {
				Minecraft mc = Minecraft.getMinecraft();
				mc.addScheduledTask(() -> {
					Entity entity = mc.world.getEntityByID(message.id);
					if (entity instanceof Base) {
						((Base)entity).standStillTicks = message.ticks;
					}
				});
				return null;
			}
		}

		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.id);
			buf.writeInt(this.ticks);
		}

		public void fromBytes(ByteBuf buf) {
			this.id = buf.readInt();
			this.ticks = buf.readInt();
		}
	}

	@SubscribeEvent
	public void onSpawnCheck(LivingSpawnEvent.CheckSpawn event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!event.isSpawner() && entity instanceof Base) {
			entity.setPosition(event.getX(), event.getY(), event.getZ());
			if (!((Base)entity).getCanSpawnHere()) {
				event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
			}
		}
	}

	@SubscribeEvent
	public void onTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		if (target instanceof Base) {
			InventoryMessage.sendTo((EntityPlayerMP)event.getEntityPlayer(), (Base)target);
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(ChakraMessage.Handler.class, ChakraMessage.class, Side.SERVER);
		this.elements.addNetworkMessage(InventoryMessage.Handler.class, InventoryMessage.class, Side.CLIENT);
		this.elements.addNetworkMessage(StandStillMessage.Handler.class, StandStillMessage.class, Side.CLIENT);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
