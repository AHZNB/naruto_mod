
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.pathfinding.PathNavigate;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;

//import net.minecraft.entity.ai.EntityAIBase;
//import net.minecraft.entity.EntityCreature;
//import net.minecraft.pathfinding.Path;
//import net.minecraft.util.math.BlockPos;

@ElementsNarutomodMod.ModElement.Tag
public class EntityGiantChameleon extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 484;
	public static final int ENTITYID_RANGED = 485;
	private static final float ENTITY_SCALE = 10f;

	public EntityGiantChameleon(ElementsNarutomodMod instance) {
		super(instance, 909);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "giant_chameleon"), ENTITYID)
		 .name("giant_chameleon").tracker(64, 3, true).egg(-16751053, -16724941).build());
	}

	public static class EntityCustom extends EntitySummonAnimal.Base implements IMob, EntityTailedBeast.ICollisionData {
		private static final DataParameter<Boolean> MOUTH_OPEN = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private final int invisEnd = 40;
		private int prevInvisProgress = invisEnd;
		private int invisProgress = invisEnd;
		private int invisDirection;
		private EntityLivingBase lastTarget;
		private final ProcedureUtils.CollisionHelper collisionData;

		public EntityCustom(World world) {
			super(world);
			this.collisionData = new ProcedureUtils.CollisionHelper(this);
			this.setOGSize(0.5f, 0.5f);
			this.experienceValue = 500;
			this.postScaleFixup();
			this.stepHeight = this.height * 2;
		}

		public EntityCustom(EntityLivingBase player) {
			this(player.world);
			this.setSummoner(player);
			RayTraceResult res = ProcedureUtils.raytraceBlocks(player, 4.0);
			double x = res != null ? 0.5d + res.getBlockPos().getX() : player.getPositionEyes(1f).add(player.getLookVec().scale(4)).x;
			double z = res != null ? 0.5d + res.getBlockPos().getZ() : player.getPositionEyes(1f).add(player.getLookVec().scale(4)).z;
			this.setPosition(x, player.posY, z);
			this.rotationYaw = player.rotationYaw - 180.0f;
			this.rotationYawHead = this.rotationYaw;
		}

		@Override
		public float getScale() {
			return ENTITY_SCALE;
		}

		@Override
		public void entityInit() {
			super.entityInit();
			this.dataManager.register(MOUTH_OPEN, Boolean.valueOf(false));
		}

		private void openMouth(boolean b) {
			this.dataManager.set(MOUTH_OPEN, Boolean.valueOf(b));
		}
	
		public boolean isMouthOpen() {
			return ((Boolean)this.getDataManager().get(MOUTH_OPEN)).booleanValue();
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			this.moveHelper = new EntityTailedBeast.MoveHelper(this);
			return new EntityTailedBeast.NavigateGround(this, worldIn);
		}

		@Override
		public SoundEvent getAmbientSound() {
			return null;
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return null;
		}

		@Override
		public SoundEvent getDeathSound() {
			return null;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			//this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.7D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(500.0D);
			//this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(50.0D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
		}

		@Override
		public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			super.processInteract(entity, hand);
			if (this.isSummoner(entity)) {
				entity.startRiding(this);
				return true;
			}
			return false;
		}

		@Override
		public ProcedureUtils.CollisionHelper getCollisionData() {
			return this.collisionData;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.4f, true) {
				@Override
				public boolean shouldExecute() {
					return !(this.attacker.getControllingPassenger() instanceof EntityPlayer) && super.shouldExecute();
				}
			});
			this.tasks.addTask(2, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if ((this.getAttackTarget() != null) != this.isMouthOpen()) {
				this.openMouth(this.getAttackTarget() != null);
			}
			if (this.getAttackTarget() != this.lastTarget) {
				this.setInvisibleDirection(this.getAttackTarget() == null ? 1 : -1);
			}
			this.lastTarget = this.getAttackTarget();
		}

		@Override
		public double getMountedYOffset() {
			return this.height + 0.35d;
		}

		@Override
		public boolean shouldRiderSit() {
			return false;
		}

		@Override
		public int getMaxFallHeight() {
			return 24;
		}

		@Override @Nullable
		public EntityLivingBase getControllingPassenger() {
			Entity passenger = super.getControllingPassenger();
			return passenger instanceof EntityLivingBase && this.isSummoner(passenger) ? (EntityLivingBase)passenger : null;
		}

		@Override
		public void travel(float strafe, float vertical, float forward) {
			EntityLivingBase passenger = this.getControllingPassenger();
			if (passenger instanceof EntityPlayer) {
				++this.lifeSpan;
				this.rotationYaw = passenger.rotationYaw;
				this.rotationPitch = passenger.rotationPitch;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.renderYawOffset = passenger.renderYawOffset;
				this.rotationYawHead = passenger.getRotationYawHead();
				this.jumpMovementFactor = passenger.getAIMoveSpeed() * 0.15F;
				this.setAIMoveSpeed((float)ProcedureUtils.getModifiedSpeed(this));
				forward = passenger.moveForward;
				strafe = passenger.moveStrafing;
			} else {
				this.jumpMovementFactor = 0.02f;
			}
//if (!this.world.isRemote)
//System.out.println("++++++ noPath?"+navigator.noPath()+", finalPathPoint:("+(navigator.getPath()!=null?navigator.getPath().getFinalPathPoint():"na")+"), strafe="+strafe+", vertical="+vertical+", forward="+forward+", passenger:"+passenger);
			super.travel(strafe, vertical, forward);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				this.tasks.setControlFlag(7, !(this.getControllingPassenger() instanceof EntityPlayer));
			}
			this.updateInvisibleProgress();
			if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
				this.setDead();
			}
			this.fallDistance = 0;
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			if (!this.world.isRemote && this.isBeingRidden() && entity instanceof EntityLivingBase && !this.isSummoner((EntityLivingBase)entity)) {
				this.attackEntityAsMob(entity);
			}
			super.collideWithEntity(entity);
		}

		@Override
		public boolean couldBreakBlocks() {
			return true;
		}

		public void updateInvisibleProgress() {
			this.prevInvisProgress = this.invisProgress;
			if ((this.invisProgress > 0 && this.invisDirection < 0) || (this.invisProgress < this.invisEnd && this.invisDirection > 0)) {
				this.invisProgress += this.invisDirection;
			} else {
				this.invisDirection = 0;
			}
			boolean flag = this.invisProgress == 0 && this.prevInvisProgress == 0;
			if (this.isInvisible() != flag) {
				this.setInvisible(flag);
			}
			if (flag) {
				EntityLivingBase passenger = this.getControllingPassenger();
				if (passenger != null) {
					passenger.setInvisible(true);
				}
			}
		}

		public void setInvisibleDirection(int direction) {
			this.invisDirection = MathHelper.clamp(direction, -1, 1);
			this.world.setEntityState(this, (byte)(110 + this.invisDirection));
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void handleStatusUpdate(byte id) {
			if (id >= 109 && id <= 111) {
				this.invisDirection = id - 110;
			} else {
				super.handleStatusUpdate(id);
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean isInvisibleToPlayer(EntityPlayer player) {
			return player.isSpectator() || this.isSummoner(player) ? false : this.isInvisible();
		}
	}

	/*public static class AIAttackMelee extends EntityAIBase
	{
	    World world;
	    protected EntityCreature attacker;
	    protected int attackTick;
	    double speedTowardsTarget;
	    boolean longMemory;
	    Path path;
	    private int delayCounter;
	    private double targetX;
	    private double targetY;
	    private double targetZ;
	    protected final int attackInterval = 20;
	
	    public AIAttackMelee(EntityCreature creature, double speedIn, boolean useLongMemory)
	    {
	        this.attacker = creature;
	        this.world = creature.world;
	        this.speedTowardsTarget = speedIn;
	        this.longMemory = useLongMemory;
	        this.setMutexBits(3);
	    }
	
	    public boolean shouldExecute()
	    {
	        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
	
	        if (entitylivingbase == null)
	        {
	            return false;
	        }
	        else if (!entitylivingbase.isEntityAlive())
	        {
	            return false;
	        }
	        else
	        {
	            this.path = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
System.out.println(">>>>>> hasPath:"+(path!=null));
	            if (this.path != null)
	            {
	                return true;
	            }
	            else
	            {
	                return this.getAttackReachSqr(entitylivingbase) >= this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
	            }
	        }
	    }
	
	    public boolean shouldContinueExecuting()
	    {
	        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
	
	        if (entitylivingbase == null)
	        {
	            return false;
	        }
	        else if (!entitylivingbase.isEntityAlive())
	        {
	            return false;
	        }
	        else if (!this.longMemory)
	        {
	            return !this.attacker.getNavigator().noPath();
	        }
	        else if (!this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)))
	        {
	            return false;
	        }
	        else
	        {
	            return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).isCreative();
	        }
	    }
	
	    public void startExecuting()
	    {
	        this.attacker.getNavigator().setPath(this.path, this.speedTowardsTarget);
	        this.delayCounter = 0;
	    }
	
	    public void resetTask()
	    {
	        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
	
	        if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer)entitylivingbase).isSpectator() || ((EntityPlayer)entitylivingbase).isCreative()))
	        {
	            this.attacker.setAttackTarget((EntityLivingBase)null);
	        }
	
	        this.attacker.getNavigator().clearPath();
	    }
	
	    public void updateTask()
	    {
	        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
	        this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
	        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
	        --this.delayCounter;
	
	        if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F))
	        {
	            this.targetX = entitylivingbase.posX;
	            this.targetY = entitylivingbase.getEntityBoundingBox().minY;
	            this.targetZ = entitylivingbase.posZ;
	            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
		
	            if (d0 > 1024.0D)
	            {
	                this.delayCounter += 10;
	            }
	            else if (d0 > 256.0D)
	            {
	                this.delayCounter += 5;
	            }
				boolean flag = this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget);
System.out.println("====== tryMoveToEntityLiving suceeded? "+flag+", speed="+speedTowardsTarget+", entity:"+entitylivingbase);
	            if (!flag)
	            {
	                this.delayCounter += 15;
	            }
	        }
	
	        this.attackTick = Math.max(this.attackTick - 1, 0);
	        this.checkAndPerformAttack(entitylivingbase, d0);
	    }
	
	    protected void checkAndPerformAttack(EntityLivingBase p_190102_1_, double p_190102_2_)
	    {
	        double d0 = this.getAttackReachSqr(p_190102_1_);
	
	        if (p_190102_2_ <= d0 && this.attackTick <= 0)
	        {
	            this.attackTick = 20;
	            this.attacker.swingArm(EnumHand.MAIN_HAND);
	            this.attacker.attackEntityAsMob(p_190102_1_);
	        }
	    }
	
	    protected double getAttackReachSqr(EntityLivingBase attackTarget)
	    {
	        return (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width);
	    }
	}*/

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving<EntityCustom>(renderManager, new ModelChameleon(), 0.5f * ENTITY_SCALE) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/chameleon.png");
					@Override
					protected ResourceLocation getEntityTexture(EntityCustom entity) {
						return this.texture;
					}
					@Override
					protected void renderModel(EntityCustom entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
						float partialTicks = ageInTicks - (float)entity.ticksExisted;
						GlStateManager.pushMatrix();
						GlStateManager.translate(0.0f, 1.5f - ENTITY_SCALE * 1.5f, 0.0f);
						GlStateManager.scale(ENTITY_SCALE, ENTITY_SCALE, ENTITY_SCALE);
						GlStateManager.enableBlend();
						GlStateManager.alphaFunc(0x204, 0.0f);
						GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
						float alpha = ((float)entity.prevInvisProgress + (float)(entity.invisProgress - entity.prevInvisProgress) * partialTicks) / (float)entity.invisEnd;
						GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
						super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
						GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
						GlStateManager.alphaFunc(0x204, 0.1f);
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					}
				};
			});
		}

		// Made with Blockbench 4.12.2
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelChameleon extends ModelQuadruped {
			//private final ModelRenderer head;
			private final ModelRenderer upperJaw;
			private final ModelRenderer bone14;
			private final ModelRenderer bone18;
			private final ModelRenderer bone7;
			private final ModelRenderer bone11;
			private final ModelRenderer bone73;
			private final ModelRenderer rightEye;
			private final ModelRenderer cube_r1;
			private final ModelRenderer leftEye;
			private final ModelRenderer cube_r2;
			private final ModelRenderer lowerJaw;
			private final ModelRenderer bone16;
			private final ModelRenderer bone13;
			private final ModelRenderer bone74;
			private final ModelRenderer bone12;
			private final ModelRenderer bone5;
			private final ModelRenderer hornRight;
			private final ModelRenderer hornLeft;
			private final ModelRenderer hornLeft2;
			private final ModelRenderer hornLeft3;
			private final ModelRenderer hornLeft4;
			//private final ModelRenderer body;
			private final ModelRenderer bone9;
			private final ModelRenderer bone10;
			private final ModelRenderer bone61;
			private final ModelRenderer bone8;
			private final ModelRenderer bone3;
			private final ModelRenderer bone6;
			private final ModelRenderer bone4;
			private final ModelRenderer wingRight;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer wingLeft;
			private final ModelRenderer cube_r6;
			private final ModelRenderer cube_r7;
			private final ModelRenderer cube_r8;
			private final ModelRenderer[] tail = new ModelRenderer[8];
			private final Vector3f[] tailPreset1 = { new Vector3f(0.5236F, 0.0F, 0.0F), new Vector3f(0.7854F, 0.0F, 0.0F),
				new Vector3f(0.5236F, 0.0F, 0.0F), new Vector3f(0.5236F, 0.0F, 0.0F), new Vector3f(0.5236F, 0.0F, 0.0F),
				new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.0873F, 0.0F, 0.0F) };
			private final ModelRenderer snakeHead;
			private final ModelRenderer bone;
			private final ModelRenderer bone39;
			private final ModelRenderer bone62;
			private final ModelRenderer bone63;
			private final ModelRenderer snakeJaw;
			private final ModelRenderer bone64;
			private final ModelRenderer bone70;
			private final ModelRenderer bone75;
			private final ModelRenderer bone76;
			//private final ModelRenderer leg1;
			private final ModelRenderer bone27;
			private final ModelRenderer cube_r9;
			private final ModelRenderer bone28;
			private final ModelRenderer cube_r10;
			private final ModelRenderer bone2;
			private final ModelRenderer cube_r11;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer bone34;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			//private final ModelRenderer leg2;
			private final ModelRenderer bone15;
			private final ModelRenderer cube_r12;
			private final ModelRenderer bone17;
			private final ModelRenderer cube_r13;
			private final ModelRenderer bone21;
			private final ModelRenderer cube_r14;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
			private final ModelRenderer bone24;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer bone31;
			//private final ModelRenderer leg3;
			private final ModelRenderer bone38;
			private final ModelRenderer cube_r15;
			private final ModelRenderer bone40;
			private final ModelRenderer cube_r16;
			private final ModelRenderer bone41;
			private final ModelRenderer cube_r17;
			private final ModelRenderer bone42;
			private final ModelRenderer bone43;
			private final ModelRenderer bone44;
			private final ModelRenderer bone45;
			private final ModelRenderer bone46;
			private final ModelRenderer bone47;
			private final ModelRenderer bone48;
			private final ModelRenderer bone49;
			//private final ModelRenderer leg4;
			private final ModelRenderer bone50;
			private final ModelRenderer cube_r18;
			private final ModelRenderer bone51;
			private final ModelRenderer cube_r19;
			private final ModelRenderer bone52;
			private final ModelRenderer cube_r20;
			private final ModelRenderer bone53;
			private final ModelRenderer bone54;
			private final ModelRenderer bone55;
			private final ModelRenderer bone56;
			private final ModelRenderer bone57;
			private final ModelRenderer bone58;
			private final ModelRenderer bone59;
			private final ModelRenderer bone60;

			public ModelChameleon() {
				super(12, 0.0F);
				textureWidth = 64;
				textureHeight = 64;
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 18.85F, 0.0F);
				upperJaw = new ModelRenderer(this);
				upperJaw.setRotationPoint(0.0F, -0.25F, -4.0F);
				head.addChild(upperJaw);
				setRotationAngle(upperJaw, -0.5236F, 0.0F, 0.0F);
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(-2.7F, -1.0F, -0.4F);
				upperJaw.addChild(bone14);
				setRotationAngle(bone14, 0.4363F, -0.1745F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 0, 26, 0.0F, -1.0F, -2.0F, 3, 2, 2, 0.0F, false));
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone14.addChild(bone18);
				setRotationAngle(bone18, 0.0873F, -0.2618F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 8, 49, 0.0F, -1.0F, -2.0F, 3, 2, 2, 0.0F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(2.7F, -1.0F, -0.4F);
				upperJaw.addChild(bone7);
				setRotationAngle(bone7, 0.4363F, 0.1745F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 26, -3.0F, -1.0F, -2.0F, 3, 2, 2, 0.0F, true));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone7.addChild(bone11);
				setRotationAngle(bone11, 0.0873F, 0.2618F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 8, 49, -3.0F, -1.0F, -2.0F, 3, 2, 2, 0.0F, true));
				bone73 = new ModelRenderer(this);
				bone73.setRotationPoint(0.0F, -0.1F, -4.1F);
				upperJaw.addChild(bone73);
				setRotationAngle(bone73, 0.4363F, 0.0F, 0.0F);
				bone73.cubeList.add(new ModelBox(bone73, 22, 4, -1.5F, 0.0F, -0.05F, 3, 2, 2, 0.0F, false));
				rightEye = new ModelRenderer(this);
				rightEye.setRotationPoint(-2.3932F, -1.2041F, -1.1912F);
				upperJaw.addChild(rightEye);
				setRotationAngle(rightEye, 0.0F, -0.3927F, 0.5236F);
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(-1.0F, 1.0F, 1.0F);
				rightEye.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.0F, 0.0F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, 0.0F, -2.0F, -2.0F, 2, 2, 2, -0.2F, false));
				leftEye = new ModelRenderer(this);
				leftEye.setRotationPoint(2.3932F, -1.2041F, -1.1912F);
				upperJaw.addChild(leftEye);
				setRotationAngle(leftEye, 0.0F, 0.3927F, -0.5236F);
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(5.0F, 5.0F, -3.0F);
				leftEye.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.0F, 0.0F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 24, 44, -10.0F, -10.0F, -2.0F, 10, 10, 10, -4.2F, true));
				lowerJaw = new ModelRenderer(this);
				lowerJaw.setRotationPoint(0.0F, -0.25F, -4.0F);
				head.addChild(lowerJaw);
				setRotationAngle(lowerJaw, 0.5236F, 0.0F, 0.0F);
				lowerJaw.cubeList.add(new ModelBox(lowerJaw, 17, 46, -2.0F, -1.35F, -3.25F, 4, 2, 3, 0.2F, false));
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(-2.7F, 1.05F, -0.4F);
				lowerJaw.addChild(bone16);
				setRotationAngle(bone16, -0.0436F, -0.3491F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 44, 34, 0.0F, -1.0F, -3.0F, 3, 2, 3, 0.0F, false));
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(2.7F, 1.05F, -0.4F);
				lowerJaw.addChild(bone13);
				setRotationAngle(bone13, -0.0436F, 0.3491F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 44, 34, -3.0F, -1.0F, -3.0F, 3, 2, 3, 0.0F, true));
				bone74 = new ModelRenderer(this);
				bone74.setRotationPoint(0.0F, 0.92F, -3.75F);
				lowerJaw.addChild(bone74);
				setRotationAngle(bone74, 0.4363F, 0.0F, 0.0F);
				bone74.cubeList.add(new ModelBox(bone74, 40, 21, -1.5F, 0.15F, 0.05F, 3, 1, 1, 0.0F, false));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(-3.05F, -0.2F, 0.2F);
				head.addChild(bone12);
				setRotationAngle(bone12, 0.0F, -0.0873F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 26, 34, 0.0F, -2.0F, -5.0F, 4, 4, 5, 0.0F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(3.05F, -0.2F, 0.2F);
				head.addChild(bone5);
				setRotationAngle(bone5, 0.0F, 0.0873F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 26, 34, -4.0F, -2.0F, -5.0F, 4, 4, 5, 0.0F, true));
				hornRight = new ModelRenderer(this);
				hornRight.setRotationPoint(-1.7F, -1.6F, -1.95F);
				head.addChild(hornRight);
				setRotationAngle(hornRight, -0.7854F, -0.2618F, -0.5236F);
				hornRight.cubeList.add(new ModelBox(hornRight, 30, 0, -1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F, false));
				hornLeft = new ModelRenderer(this);
				hornLeft.setRotationPoint(1.7F, -1.6F, -1.95F);
				head.addChild(hornLeft);
				setRotationAngle(hornLeft, -0.7854F, 0.2618F, 0.5236F);
				hornLeft.cubeList.add(new ModelBox(hornLeft, 48, 21, -1.0F, -3.0F, -1.0F, 2, 4, 2, 0.0F, false));
				hornLeft2 = new ModelRenderer(this);
				hornLeft2.setRotationPoint(0.0F, -2.75F, 0.0F);
				hornLeft.addChild(hornLeft2);
				setRotationAngle(hornLeft2, 0.2618F, 0.0F, 0.3927F);
				hornLeft2.cubeList.add(new ModelBox(hornLeft2, 48, 21, -1.0F, -3.75F, -1.0F, 2, 4, 2, -0.2F, false));
				hornLeft2.cubeList.add(new ModelBox(hornLeft2, 50, 0, -0.15F, -2.0F, -2.0F, 1, 1, 4, -0.3F, false));
				hornLeft2.cubeList.add(new ModelBox(hornLeft2, 50, 0, -0.85F, -3.25F, -2.0F, 1, 1, 4, -0.3F, false));
				hornLeft3 = new ModelRenderer(this);
				hornLeft3.setRotationPoint(0.0F, -3.25F, 0.0F);
				hornLeft2.addChild(hornLeft3);
				setRotationAngle(hornLeft3, -0.4363F, 0.0F, -0.5236F);
				hornLeft3.cubeList.add(new ModelBox(hornLeft3, 48, 21, -1.0F, -3.25F, -1.0F, 2, 4, 2, -0.4F, false));
				hornLeft3.cubeList.add(new ModelBox(hornLeft3, 51, 1, -0.25F, -1.25F, -1.5F, 1, 1, 3, -0.3F, false));
				hornLeft3.cubeList.add(new ModelBox(hornLeft3, 51, 1, -0.7F, -2.5F, -1.5F, 1, 1, 3, -0.3F, false));
				hornLeft4 = new ModelRenderer(this);
				hornLeft4.setRotationPoint(0.0F, -2.75F, 0.0F);
				hornLeft3.addChild(hornLeft4);
				setRotationAngle(hornLeft4, -0.3491F, 0.0F, -0.2618F);
				hornLeft4.cubeList.add(new ModelBox(hornLeft4, 48, 21, -1.0F, -3.25F, -1.0F, 2, 4, 2, -0.6F, false));
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 18.85F, 0.0F);
				body.cubeList.add(new ModelBox(body, 24, 0, -0.5F, -4.6F, 5.5F, 1, 1, 2, 0.0F, false));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 1.3F, 0.5F);
				body.addChild(bone9);
				setRotationAngle(bone9, -0.0873F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 32, 0, -3.0F, -1.5F, 0.0F, 6, 2, 6, 0.0F, false));
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(3.0F, 1.8F, 6.4F);
				body.addChild(bone10);
				setRotationAngle(bone10, 0.0873F, -0.0873F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 3, 26, -3.0F, -3.5F, 0.0F, 3, 4, 7, 0.0F, false));
				bone61 = new ModelRenderer(this);
				bone61.setRotationPoint(-3.0F, 1.8F, 6.4F);
				body.addChild(bone61);
				setRotationAngle(bone61, 0.0873F, 0.0873F, 0.0F);
				bone61.cubeList.add(new ModelBox(bone61, 3, 26, 0.0F, -3.5F, 0.0F, 3, 4, 7, 0.0F, true));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(-3.35F, 0.3F, 10.75F);
				body.addChild(bone8);
				setRotationAngle(bone8, -0.2618F, 0.2618F, -0.5236F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 13, 0.0F, -2.0F, -5.0F, 6, 4, 9, 0.0F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(3.35F, 0.3F, 10.75F);
				body.addChild(bone3);
				setRotationAngle(bone3, -0.2618F, -0.2618F, 0.5236F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 13, -6.0F, -2.0F, -5.0F, 6, 4, 9, 0.0F, true));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(-3.35F, 0.3F, 2.5F);
				body.addChild(bone6);
				setRotationAngle(bone6, 0.2618F, -0.2618F, -0.5236F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, 0.0F, -2.0F, -4.0F, 6, 4, 9, 0.0F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(3.35F, 0.3F, 2.5F);
				body.addChild(bone4);
				setRotationAngle(bone4, 0.2618F, 0.2618F, 0.5236F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 0, -6.0F, -2.0F, -4.0F, 6, 4, 9, 0.0F, true));
				wingRight = new ModelRenderer(this);
				wingRight.setRotationPoint(-2.0927F, -2.9766F, 3.0322F);
				body.addChild(wingRight);
				setRotationAngle(wingRight, 0.0F, -0.3491F, 0.0F);
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(1.7704F, 7.9911F, 5.1063F);
				wingRight.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.9722F, -0.0114F, -0.2253F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 30, 17, 0.0F, -10.0F, -12.0F, 0, 10, 6, 0.0F, false));
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(2.7704F, 2.7911F, 9.1063F);
				wingRight.addChild(cube_r4);
				setRotationAngle(cube_r4, -0.9722F, -0.0114F, -0.2253F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 49, -3.2F, -4.8F, -11.2F, 2, 8, 2, -0.7F, false));
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(2.2927F, 0.7266F, 10.5178F);
				wingRight.addChild(cube_r5);
				setRotationAngle(cube_r5, -0.3177F, -0.0114F, -0.2253F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 16, 37, -3.2F, -2.8F, -11.2F, 2, 6, 2, -0.7F, false));
				wingLeft = new ModelRenderer(this);
				wingLeft.setRotationPoint(2.0927F, -2.9766F, 3.0322F);
				body.addChild(wingLeft);
				setRotationAngle(wingLeft, 0.0F, 0.3491F, 0.0F);
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(-1.7704F, 7.9911F, 5.1063F);
				wingLeft.addChild(cube_r6);
				setRotationAngle(cube_r6, -0.9722F, 0.0114F, 0.2253F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 30, 17, 0.0F, -10.0F, -12.0F, 0, 10, 6, 0.0F, true));
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(-2.7704F, 2.7911F, 9.1063F);
				wingLeft.addChild(cube_r7);
				setRotationAngle(cube_r7, -0.9722F, 0.0114F, 0.2253F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 49, 1.2F, -4.8F, -11.2F, 2, 8, 2, -0.7F, true));
				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(-2.2927F, 0.7266F, 10.5178F);
				wingLeft.addChild(cube_r8);
				setRotationAngle(cube_r8, -0.3177F, 0.0114F, 0.2253F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 16, 37, 1.2F, -2.8F, -11.2F, 2, 6, 2, -0.7F, true));

				tail[0] = new ModelRenderer(this);
				tail[0].setRotationPoint(0.0F, -0.2F, 13.75F);
				body.addChild(tail[0]);
				setRotationAngle(tail[0], 0.5236F, 0.0F, 0.0F);
				tail[0].cubeList.add(new ModelBox(tail[0], 24, 9, -2.55F, -2.05F, -1.3F, 5, 4, 6, -0.2F, false));
				tail[1] = new ModelRenderer(this);
				tail[1].setRotationPoint(-0.05F, 0.0F, 4.5F);
				tail[0].addChild(tail[1]);
				setRotationAngle(tail[1], 0.7854F, 0.0F, 0.0F);
				tail[1].cubeList.add(new ModelBox(tail[1], 24, 9, -2.5F, -2.0F, -1.0F, 5, 4, 6, -0.4F, false));
				tail[2] = new ModelRenderer(this);
				tail[2].setRotationPoint(0.0F, 0.05F, 4.5F);
				tail[1].addChild(tail[2]);
				setRotationAngle(tail[2], 0.5236F, 0.0F, 0.0F);
				tail[2].cubeList.add(new ModelBox(tail[2], 24, 9, -2.5F, -2.0F, -1.25F, 5, 4, 6, -0.7F, false));
				tail[3] = new ModelRenderer(this);
				tail[3].setRotationPoint(0.05F, -0.1F, 3.95F);
				tail[2].addChild(tail[3]);
				setRotationAngle(tail[3], 0.5236F, 0.0F, 0.0F);
				tail[3].cubeList.add(new ModelBox(tail[3], 24, 9, -2.5F, -2.0F, -1.5F, 5, 4, 6, -0.9F, false));
				tail[4] = new ModelRenderer(this);
				tail[4].setRotationPoint(-0.05F, 0.05F, 3.5F);
				tail[3].addChild(tail[4]);
				setRotationAngle(tail[4], 0.5236F, 0.0F, 0.0F);
				tail[4].cubeList.add(new ModelBox(tail[4], 24, 9, -2.5F, -2.0F, -1.5F, 5, 4, 6, -1.0F, false));
				tail[5] = new ModelRenderer(this);
				tail[5].setRotationPoint(0.0F, 0.0F, 3.3F);
				tail[4].addChild(tail[5]);
				setRotationAngle(tail[5], 0.2618F, 0.0F, 0.0F);
				tail[5].cubeList.add(new ModelBox(tail[5], 24, 9, -2.5F, -2.0F, -1.5F, 5, 4, 6, -1.1F, false));
				tail[6] = new ModelRenderer(this);
				tail[6].setRotationPoint(0.0F, 0.0F, 3.0F);
				tail[5].addChild(tail[6]);
				setRotationAngle(tail[6], 0.2618F, 0.0F, 0.0F);
				tail[6].cubeList.add(new ModelBox(tail[6], 24, 9, -2.5F, -2.0F, -1.5F, 5, 4, 6, -1.2F, false));
				tail[7] = new ModelRenderer(this);
				tail[7].setRotationPoint(0.0F, 0.0F, 3.0F);
				tail[6].addChild(tail[7]);
				setRotationAngle(tail[7], 0.0873F, 0.0F, 0.0F);
				tail[7].cubeList.add(new ModelBox(tail[7], 24, 9, -2.51F, -1.96F, -1.54F, 5, 4, 6, -1.3F, false));
				snakeHead = new ModelRenderer(this);
				snakeHead.setRotationPoint(-0.0653F, 0.0138F, 2.8455F);
				tail[7].addChild(snakeHead);
				setRotationAngle(snakeHead, 0.0F, 3.1416F, -3.1416F);
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-0.5792F, -0.2968F, -0.9687F);
				snakeHead.addChild(bone);
				setRotationAngle(bone, -0.1309F, 0.0873F, 3.1416F);
				bone.cubeList.add(new ModelBox(bone, 0, 44, -2.0F, -0.5F, -2.0F, 4, 1, 4, -1.15F, false));
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.0F, 0.6F, -0.65F);
				bone.addChild(bone39);
				setRotationAngle(bone39, -0.1745F, 0.1745F, 0.0436F);
				bone39.cubeList.add(new ModelBox(bone39, 4, 55, -2.0F, -1.1F, -2.9F, 4, 1, 4, -1.15F, false));
				bone62 = new ModelRenderer(this);
				bone62.setRotationPoint(0.5597F, -0.2968F, -0.9687F);
				snakeHead.addChild(bone62);
				setRotationAngle(bone62, -0.1309F, -0.0873F, -3.1416F);
				bone62.cubeList.add(new ModelBox(bone62, 0, 44, -2.0F, -0.5F, -2.0F, 4, 1, 4, -1.15F, true));
				bone63 = new ModelRenderer(this);
				bone63.setRotationPoint(0.0F, 0.6F, -0.65F);
				bone62.addChild(bone63);
				setRotationAngle(bone63, -0.1745F, -0.1745F, -0.0436F);
				bone63.cubeList.add(new ModelBox(bone63, 4, 55, -2.0F, -1.1F, -2.9F, 4, 1, 4, -1.15F, true));
				snakeJaw = new ModelRenderer(this);
				snakeJaw.setRotationPoint(-0.0229F, 0.2494F, 0.0045F);
				snakeHead.addChild(snakeJaw);
				setRotationAngle(snakeJaw, 0.7854F, 0.0F, 0.0F);
				bone64 = new ModelRenderer(this);
				bone64.setRotationPoint(-0.5696F, 0.35F, -0.9232F);
				snakeJaw.addChild(bone64);
				setRotationAngle(bone64, 0.0F, -0.0873F, 0.0F);
				bone64.cubeList.add(new ModelBox(bone64, 40, 8, -2.0F, -1.5F, -2.0F, 4, 3, 4, -1.15F, false));
				bone70 = new ModelRenderer(this);
				bone70.setRotationPoint(-0.05F, 0.0F, -0.55F);
				bone64.addChild(bone70);
				setRotationAngle(bone70, 0.0F, -0.1745F, 0.0F);
				bone70.cubeList.add(new ModelBox(bone70, 0, 37, -2.0F, -1.5F, -2.85F, 4, 3, 4, -1.15F, false));
				bone75 = new ModelRenderer(this);
				bone75.setRotationPoint(0.596F, 0.35F, -0.9232F);
				snakeJaw.addChild(bone75);
				setRotationAngle(bone75, 0.0F, 0.0873F, 0.0F);
				bone75.cubeList.add(new ModelBox(bone75, 40, 8, -2.0F, -1.5F, -2.0F, 4, 3, 4, -1.15F, true));
				bone76 = new ModelRenderer(this);
				bone76.setRotationPoint(0.05F, 0.0F, -0.55F);
				bone75.addChild(bone76);
				setRotationAngle(bone76, 0.0F, 0.1745F, 0.0F);
				bone76.cubeList.add(new ModelBox(bone76, 0, 37, -2.0F, -1.5F, -2.85F, 4, 3, 4, -1.15F, true));

				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(-3.2826F, 17.8674F, 0.7F);
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(-0.9545F, -0.0307F, 1.3F);
				leg1.addChild(bone27);
				setRotationAngle(bone27, 0.0F, 0.3054F, -0.5236F);
				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone27.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.0F, 0.0F, 0.0F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 48, 15, -4.0F, 0.0F, -2.0F, 6, 2, 2, 0.1F, false));
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(-4.0955F, 0.9807F, -0.1F);
				bone27.addChild(bone28);
				setRotationAngle(bone28, 0.0F, -0.3927F, -1.5708F);
				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(0.15F, 0.0F, 0.1F);
				bone28.addChild(cube_r10);
				setRotationAngle(cube_r10, 0.0F, 0.0F, 0.0F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 44, 39, -4.0F, -1.0F, -2.0F, 4, 2, 2, -0.1F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-4.4174F, 6.6326F, 0.25F);
				leg1.addChild(bone2);
				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(1.2F, 0.0F, -0.25F);
				bone2.addChild(cube_r11);
				setRotationAngle(cube_r11, 0.0F, 0.7854F, 0.0F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 16, 26, -1.7F, -1.3F, -1.7F, 3, 1, 3, -0.1F, false));
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(1.1212F, -0.9F, -1.3029F);
				bone2.addChild(bone19);
				setRotationAngle(bone19, 0.1745F, 0.2618F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, false));
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone19.addChild(bone20);
				setRotationAngle(bone20, 0.5236F, 0.0F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(2.1212F, -0.9F, -0.3029F);
				bone2.addChild(bone32);
				setRotationAngle(bone32, 0.1745F, -0.7854F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, false));
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone32.addChild(bone33);
				setRotationAngle(bone33, 0.5236F, 0.0F, 0.0F);
				bone33.cubeList.add(new ModelBox(bone33, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(0.3712F, -0.9F, -0.8029F);
				bone2.addChild(bone34);
				setRotationAngle(bone34, 0.1745F, 0.7854F, 0.0F);
				bone34.cubeList.add(new ModelBox(bone34, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, false));
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone34.addChild(bone35);
				setRotationAngle(bone35, 0.5236F, 0.0F, 0.0F);
				bone35.cubeList.add(new ModelBox(bone35, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.1212F, -0.9F, 0.1971F);
				bone2.addChild(bone36);
				setRotationAngle(bone36, 0.1745F, 1.2217F, 0.0F);
				bone36.cubeList.add(new ModelBox(bone36, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, false));
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone36.addChild(bone37);
				setRotationAngle(bone37, 0.5236F, 0.0F, 0.0F);
				bone37.cubeList.add(new ModelBox(bone37, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(3.2826F, 17.8674F, 0.7F);
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.9545F, -0.0307F, 1.3F);
				leg2.addChild(bone15);
				setRotationAngle(bone15, 0.0F, -0.3054F, 0.5236F);
				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone15.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.0F, 0.0F, 0.0F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 48, 15, -2.0F, 0.0F, -2.0F, 6, 2, 2, 0.1F, true));
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(4.0955F, 0.9807F, -0.1F);
				bone15.addChild(bone17);
				setRotationAngle(bone17, 0.0F, 0.3927F, 1.5708F);
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(-0.15F, 0.0F, 0.1F);
				bone17.addChild(cube_r13);
				setRotationAngle(cube_r13, 0.0F, 0.0F, 0.0F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 44, 39, 0.0F, -1.0F, -2.0F, 4, 2, 2, -0.1F, true));
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(4.4174F, 6.6326F, 0.25F);
				leg2.addChild(bone21);
				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(-1.2F, 0.0F, -0.25F);
				bone21.addChild(cube_r14);
				setRotationAngle(cube_r14, 0.0F, -0.7854F, 0.0F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 16, 26, -1.3F, -1.3F, -1.7F, 3, 1, 3, -0.1F, true));
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(-1.1212F, -0.9F, -1.3029F);
				bone21.addChild(bone22);
				setRotationAngle(bone22, 0.1745F, -0.2618F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, true));
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone22.addChild(bone23);
				setRotationAngle(bone23, 0.5236F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(-2.1212F, -0.9F, -0.3029F);
				bone21.addChild(bone24);
				setRotationAngle(bone24, 0.1745F, 0.7854F, 0.0F);
				bone24.cubeList.add(new ModelBox(bone24, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, true));
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone24.addChild(bone25);
				setRotationAngle(bone25, 0.5236F, 0.0F, 0.0F);
				bone25.cubeList.add(new ModelBox(bone25, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(-0.3712F, -0.9F, -0.8029F);
				bone21.addChild(bone26);
				setRotationAngle(bone26, 0.1745F, -0.7854F, 0.0F);
				bone26.cubeList.add(new ModelBox(bone26, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, true));
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone26.addChild(bone29);
				setRotationAngle(bone29, 0.5236F, 0.0F, 0.0F);
				bone29.cubeList.add(new ModelBox(bone29, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(-0.1212F, -0.9F, 0.1971F);
				bone21.addChild(bone30);
				setRotationAngle(bone30, 0.1745F, -1.2217F, 0.0F);
				bone30.cubeList.add(new ModelBox(bone30, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, true));
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone30.addChild(bone31);
				setRotationAngle(bone31, 0.5236F, 0.0F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(-3.2826F, 18.3674F, 9.45F);
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(-1.9545F, -0.5307F, 0.55F);
				leg3.addChild(bone38);
				setRotationAngle(bone38, 0.3491F, -0.2618F, -0.5236F);
				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone38.addChild(cube_r15);
				setRotationAngle(cube_r15, 0.0F, 0.0F, 0.0F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 48, 15, -4.0F, 0.0F, -2.0F, 6, 2, 2, 0.1F, false));
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(-4.0955F, 0.8308F, -1.35F);
				bone38.addChild(bone40);
				setRotationAngle(bone40, 0.0F, 0.3927F, -1.309F);
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(0.15F, 0.15F, 1.35F);
				bone40.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.0F, 0.0F, 0.0F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 44, 39, -4.0F, -0.25F, -2.25F, 4, 2, 2, -0.1F, false));
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(-4.0002F, 5.3326F, 0.55F);
				leg3.addChild(bone41);
				setRotationAngle(bone41, 0.0F, 0.5236F, 0.0F);
				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(0.2828F, 0.8F, 0.0F);
				bone41.addChild(cube_r17);
				setRotationAngle(cube_r17, 0.0F, 0.7854F, 0.0F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 16, 26, -1.7F, -1.3F, -1.7F, 3, 1, 3, -0.1F, false));
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.2041F, -0.1F, -1.0529F);
				bone41.addChild(bone42);
				setRotationAngle(bone42, 0.1745F, 0.2618F, 0.0F);
				bone42.cubeList.add(new ModelBox(bone42, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, false));
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone42.addChild(bone43);
				setRotationAngle(bone43, 0.5236F, 0.0F, 0.0F);
				bone43.cubeList.add(new ModelBox(bone43, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(1.2041F, -0.1F, -0.0529F);
				bone41.addChild(bone44);
				setRotationAngle(bone44, 0.1745F, -0.7854F, 0.0F);
				bone44.cubeList.add(new ModelBox(bone44, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, false));
				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone44.addChild(bone45);
				setRotationAngle(bone45, 0.5236F, 0.0F, 0.0F);
				bone45.cubeList.add(new ModelBox(bone45, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(-0.5459F, -0.1F, -0.5529F);
				bone41.addChild(bone46);
				setRotationAngle(bone46, 0.1745F, 0.7854F, 0.0F);
				bone46.cubeList.add(new ModelBox(bone46, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, false));
				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone46.addChild(bone47);
				setRotationAngle(bone47, 0.5236F, 0.0F, 0.0F);
				bone47.cubeList.add(new ModelBox(bone47, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(-0.7959F, -0.1F, 0.4471F);
				bone41.addChild(bone48);
				setRotationAngle(bone48, 0.1745F, 1.2217F, 0.0F);
				bone48.cubeList.add(new ModelBox(bone48, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, false));
				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone48.addChild(bone49);
				setRotationAngle(bone49, 0.5236F, 0.0F, 0.0F);
				bone49.cubeList.add(new ModelBox(bone49, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
				leg4 = new ModelRenderer(this);
				leg4.setRotationPoint(3.2826F, 18.3674F, 9.45F);
				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(1.9545F, -0.5307F, 0.55F);
				leg4.addChild(bone50);
				setRotationAngle(bone50, 0.3491F, 0.2618F, 0.5236F);
				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone50.addChild(cube_r18);
				setRotationAngle(cube_r18, 0.0F, 0.0F, 0.0F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 48, 15, -2.0F, 0.0F, -2.0F, 6, 2, 2, 0.1F, true));
				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(4.0955F, 0.8308F, -1.35F);
				bone50.addChild(bone51);
				setRotationAngle(bone51, 0.0F, -0.3927F, 1.309F);
				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(-0.15F, 0.15F, 1.35F);
				bone51.addChild(cube_r19);
				setRotationAngle(cube_r19, 0.0F, 0.0F, 0.0F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 44, 39, 0.0F, -0.25F, -2.25F, 4, 2, 2, -0.1F, true));
				bone52 = new ModelRenderer(this);
				bone52.setRotationPoint(4.0002F, 5.3326F, 0.55F);
				leg4.addChild(bone52);
				setRotationAngle(bone52, 0.0F, -0.5236F, 0.0F);
				cube_r20 = new ModelRenderer(this);
				cube_r20.setRotationPoint(-0.2828F, 0.8F, 0.0F);
				bone52.addChild(cube_r20);
				setRotationAngle(cube_r20, 0.0F, -0.7854F, 0.0F);
				cube_r20.cubeList.add(new ModelBox(cube_r20, 16, 26, -1.3F, -1.3F, -1.7F, 3, 1, 3, -0.1F, true));
				bone53 = new ModelRenderer(this);
				bone53.setRotationPoint(-0.2041F, -0.1F, -1.0529F);
				bone52.addChild(bone53);
				setRotationAngle(bone53, 0.1745F, -0.2618F, 0.0F);
				bone53.cubeList.add(new ModelBox(bone53, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, true));
				bone54 = new ModelRenderer(this);
				bone54.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone53.addChild(bone54);
				setRotationAngle(bone54, 0.5236F, 0.0F, 0.0F);
				bone54.cubeList.add(new ModelBox(bone54, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
				bone55 = new ModelRenderer(this);
				bone55.setRotationPoint(-1.2041F, -0.1F, -0.0529F);
				bone52.addChild(bone55);
				setRotationAngle(bone55, 0.1745F, 0.7854F, 0.0F);
				bone55.cubeList.add(new ModelBox(bone55, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, true));
				bone56 = new ModelRenderer(this);
				bone56.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone55.addChild(bone56);
				setRotationAngle(bone56, 0.5236F, 0.0F, 0.0F);
				bone56.cubeList.add(new ModelBox(bone56, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
				bone57 = new ModelRenderer(this);
				bone57.setRotationPoint(0.5459F, -0.1F, -0.5529F);
				bone52.addChild(bone57);
				setRotationAngle(bone57, 0.1745F, -0.7854F, 0.0F);
				bone57.cubeList.add(new ModelBox(bone57, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, true));
				bone58 = new ModelRenderer(this);
				bone58.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone57.addChild(bone58);
				setRotationAngle(bone58, 0.5236F, 0.0F, 0.0F);
				bone58.cubeList.add(new ModelBox(bone58, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
				bone59 = new ModelRenderer(this);
				bone59.setRotationPoint(0.7959F, -0.1F, 0.4471F);
				bone52.addChild(bone59);
				setRotationAngle(bone59, 0.1745F, -1.2217F, 0.0F);
				bone59.cubeList.add(new ModelBox(bone59, 48, 31, -0.5F, -0.5F, -1.9F, 1, 1, 2, -0.1F, true));
				bone60 = new ModelRenderer(this);
				bone60.setRotationPoint(0.0F, -0.25F, -1.8F);
				bone59.addChild(bone60);
				setRotationAngle(bone60, 0.5236F, 0.0F, 0.0F);
				bone60.cubeList.add(new ModelBox(bone60, 0, 30, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				f *= 2.0f / e.height;
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				body.rotateAngleX = 0.0F;
				this.wingLeft.rotateAngleZ = MathHelper.cos(f2 * 0.09F) * 0.08F;
				this.wingRight.rotateAngleZ = -MathHelper.cos(f2 * 0.09F) * 0.08F;
				for (int i = 1; i < tail.length; i++) {
					tail[i].rotateAngleX = tailPreset1[i].x + MathHelper.sin(f2 * 0.067F) * 0.025F;
					tail[i].rotateAngleY = tailPreset1[i].y + MathHelper.cos(f2 * 0.067F) * 0.1F;
				}
				int j = (int)f2;
				if (j % (50 + ((EntityCustom)e).getRNG().nextInt(20)) == 0) {
					leftEye.rotateAngleY = 0.2618F + 0.2618F * Math.abs(MathHelper.sin(0.05F * j));
					leftEye.rotateAngleZ = -0.5236F + 0.1745F * Math.abs(MathHelper.cos(0.08F * j));
				}
				if (e instanceof EntityCustom && ((EntityCustom)e).isMouthOpen()) {
					upperJaw.rotateAngleX = -0.5236F;
					lowerJaw.rotateAngleX = 0.5236F;
					snakeJaw.rotateAngleX = 0.7854F;
				} else {
					upperJaw.rotateAngleX = 0.0F;
					lowerJaw.rotateAngleX = 0.0F;
					snakeJaw.rotateAngleX = 0.0F;
				}
			}
		}
	}
}
