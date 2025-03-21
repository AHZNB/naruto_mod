
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ElementsNarutomodMod.ModElement.Tag
public class EntityGiantBird extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 496;
	public static final int ENTITYID_RANGED = 497;
	private static final float ENTITY_SCALE = 8f;

	public EntityGiantBird(ElementsNarutomodMod instance) {
		super(instance, 917);
	}

	@Override
	public void initElements() {
		elements.entities
				.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "giant_bird"), ENTITYID)
						.name("giant_bird").tracker(80, 3, true).egg(-6711040, -3368704).build());
	}

	public static class EntityCustom extends EntitySummonAnimal.Base implements IMob, EntityFlying {
		private boolean onGroundReally;
		public float prevRotationRoll;
		public float rotationRoll;

		public EntityCustom(World worldIn) {
			super(worldIn);
			this.setOGSize(0.5f, 0.375f);
			this.experienceValue = 500;
			this.postScaleFixup();
			this.stepHeight = this.height;
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
		protected PathNavigate createNavigator(World worldIn) {
			this.moveHelper = new EntityClone.AIFlyControl(this);
			PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, worldIn);
			pathnavigateflying.setCanOpenDoors(false);
			pathnavigateflying.setCanFloat(true);
			pathnavigateflying.setCanEnterDoors(false);
			return pathnavigateflying;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.7D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(500.0D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(50.0D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.7D);
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
		public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			super.processInteract(entity, hand);
			if (this.isSummoner(entity)) {
				entity.startRiding(this);
				return true;
			}
			return false;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAISwimming(this));
			this.tasks.addTask(2, new AISwoopAttack(this, 4.0f, 100));
			this.tasks.addTask(3, new AIHoverOverTarget(this, 16.0f, 32.0f, 2.0d));
			this.tasks.addTask(5, new EntityAILookIdle(this) {
				@Override
		        public boolean shouldExecute() {
		        	return !EntityCustom.this.tasks.isControlFlagDisabled(this.getMutexBits()) && super.shouldExecute();
		        }
			});
		}

		@Override
		public double getMountedYOffset() {
			return (this.onGround ? 0.9375d : 0.3125d) * this.getScale();
		}

		private boolean canEntitySteer(@Nullable Entity passenger) {
			return passenger instanceof EntityPlayer;
		}

		@Override
		public boolean canBeSteered() {
			return this.canEntitySteer(this.getControllingPassenger());
		}

		@Override @Nullable
		public EntityLivingBase getControllingPassenger() {
			Entity passenger = super.getControllingPassenger();
			return passenger instanceof EntityLivingBase && this.isSummoner(passenger) ? (EntityLivingBase)passenger : null;
		}

		@Override
		public void travel(float strafe, float vertical, float forward) {
			EntityLivingBase passenger = this.getControllingPassenger();
			if (this.canEntitySteer(passenger)) {
				++this.lifeSpan;
				this.rotationYaw = passenger.rotationYaw;
				//this.rotationPitch = passenger.rotationPitch;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.renderYawOffset = passenger.renderYawOffset;
				this.rotationYawHead = passenger.getRotationYawHead();
				this.jumpMovementFactor = passenger.getAIMoveSpeed() * 0.5f;
				forward = passenger.moveForward > 0.0f ? 2.0f : this.onGroundReally ? 0.0f : passenger.moveForward < 0f ? 0.3f : 1.0f;
				forward *= (float)this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue();
				this.setAIMoveSpeed(forward);
				if ((!this.onGroundReally || passenger.rotationPitch < 0.0F) && forward > 0.0F) {
					this.motionY = -passenger.rotationPitch * 0.01f;
				}
				strafe = 0.0f;
				vertical = 0.0f;
			} else {
				this.jumpMovementFactor = 0.02f;
			}
			super.travel(strafe, vertical, forward);
		}

		@Override
		protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
			this.onGroundReally = this.onGround;
			if (this.moveForward > 0.0f) {
				this.onGround = false;
			}
			super.updateFallState(y, this.onGround, state, pos);
		}

		@Override
		public void onUpdate() {
			EntityLivingBase owner = this.getSummoner();
			if (owner != null && owner.getHealth() <= 0.0f) {
				this.setDead();
			}
			super.onUpdate();
			if (!this.world.isRemote) {
				this.tasks.setControlFlag(7, !(this.getControllingPassenger() instanceof EntityPlayer));
			}
			double x = this.posX - this.prevPosX;
			double y = this.posY - this.prevPosY;
			double z = this.posZ - this.prevPosZ;
			this.prevRotationRoll = this.rotationRoll;
			this.rotationRoll = this.onGround ? 0.0f : ((this.rotationYaw - this.prevRotationYaw) * 5.0f * MathHelper.sqrt(x * x + y * y + z * z));
			while (this.rotationRoll - this.prevRotationRoll < -180.0F) {
				this.prevRotationRoll -= 360.0F;
			}
			while (this.rotationRoll - this.prevRotationRoll >= 180.0F) {
				this.prevRotationRoll += 360.0F;
			}
			this.fallDistance = 0;
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			float f2 = (float)ProcedureUtils.getVelocity(this);
			boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * f2);
			if (flag && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase)entityIn).knockBack(this, f2 * 2.0f, MathHelper.sin(this.rotationYaw * 0.017453292F), -MathHelper.cos(this.rotationYaw * 0.017453292F));
			}
			this.setLastAttackedEntity(entityIn);
			return flag;
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

	    class AISwoopAttack extends EntityAIBase {
	    	EntityCustom entity;
	    	float speed;
	    	int minInterval;
	    	int attackTime;
	    	int updateTime;
	    	
	        public AISwoopAttack(EntityCustom entityIn, float speedIn, int interval) {
	        	this.entity = entityIn;
	        	this.speed = speedIn;
	        	this.minInterval = interval;
	            this.setMutexBits(1);
	        }
	
	        @Override
	        public boolean shouldExecute() {
	        	if (this.entity.tasks.isControlFlagDisabled(this.getMutexBits())) {
	        		return false;
	        	}
	        	EntityLivingBase target = this.entity.getAttackTarget();
	            if (target != null && target.isEntityAlive() && this.entity.getAge() > this.attackTime + this.minInterval) {
	            	double x = target.posX - this.entity.posX;
	            	double y = target.posY - this.entity.posY;
	            	double z = target.posZ - this.entity.posZ;
	                return x * x + z * z > 256.0d && y < -8.0d && !this.entity.onGround;
	            }
                return false;
	        }
	
	        @Override
	        public boolean shouldContinueExecuting() {
	        	EntityLivingBase target = this.entity.getAttackTarget();
	            return target != null && target.isEntityAlive() && this.updateTime < 30;
	        }
	
	        @Override
	        public void startExecuting() {
	            EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
	            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
	            this.entity.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
	        }

	        @Override
	        public void resetTask() {
	        	this.updateTime = 0;
	        }
	
	        @Override
	        public void updateTask() {
	            EntityLivingBase target = this.entity.getAttackTarget();
	            if (this.entity.getEntityBoundingBox().intersects(target.getEntityBoundingBox().grow(0.5f * this.speed))) {
	                this.entity.attackEntityAsMob(target);
	                this.updateTime = 30;
	            } else {
	                Vec3d vec3d = target.getPositionEyes(1.0F);
	                this.entity.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
	            }
	            this.attackTime = this.entity.getAge();
	            ++this.updateTime;
	        }
	    }
	}

	public static class AIHoverOverTarget<T extends EntityLiving & EntityFlying> extends EntityAIBase {
		protected final T entity;
		private final float hoverHeight;
		private final float hoverRadius;
		private final double speed;
		private final ArrayList<Vec3d> flightPath = new ArrayList<Vec3d>(8);
		private int currentPathPoint;
		private int lastPathGeneratedTime;
		
		public AIHoverOverTarget(T livingFlying, float height, float radius, double speedIn) {
			this.entity = livingFlying;
			this.hoverHeight = height;
			this.hoverRadius = radius;
			this.speed = speedIn;
			for (int i = 0; i < 8; i++) {
				this.flightPath.add(null);
			}
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			if (this.entity.tasks.isControlFlagDisabled(this.getMutexBits())) {
				return false;
			}
			return this.entity.getAttackTarget() != null;
		}

		@Override
		public boolean shouldContinueExecuting() {
			EntityLivingBase target = this.entity.getAttackTarget();
			return target != null && target.isEntityAlive() && this.hasPath();
		}

		@Override
		public void startExecuting() {
			if (!this.hasPath()) {
				this.generateFlightPath(this.entity.getAttackTarget());
			}
			if (this.hasPath()) {
				this.set2ClosestPathPoint();
			}
		}

		@Override
		public void updateTask() {
			EntityLivingBase target = this.entity.getAttackTarget();
//System.out.println(">>>>>> ticksExisted="+entity.ticksExisted+", has target:"+(target != null && target.isEntityAlive())+", hasPath:"+hasPath());
			if (target != null && target.isEntityAlive()) {
				if (this.hasPath() && this.entity.ticksExisted <= this.lastPathGeneratedTime + 40) {
					Vec3d vec = this.flightPath.get(this.currentPathPoint % 8);
					if (this.entity.getDistance(vec.x, vec.y, vec.z) < this.speed * 0.5d * this.entity.width) {
						++this.currentPathPoint;
					} else {
						this.entity.getMoveHelper().setMoveTo(vec.x, vec.y, vec.z, this.speed);
					}
				} else {
					this.generateFlightPath(target);
					this.lastPathGeneratedTime = this.entity.ticksExisted;
				}
			}
		}
		
		private boolean hasPath() {
			return this.flightPath.get(0) != null;
		}

		private boolean generateFlightPath(Entity target) {
			int i;
			ArrayList<Vec3d> newPath = new ArrayList<Vec3d>(8);
			Vec3d centerVec = this.getGroundBelow(target).addVector(0d, this.hoverHeight, 0d);
			for (i = 0; i < 8; i++) {
				newPath.add(new Vec3d(this.hoverRadius, 0d, 0d).rotateYaw(0.7854f * i).add(centerVec));
			}
			for (i = 1; i < 13 && !ProcedureUtils.isSpaceOpenToStandOn(this.entity, new BlockPos(newPath.get(0)));
				newPath.set(0, newPath.get(0).addVector(0d, (i % 2 == 0 ? -1d : 1d) * i++, 0d)));
			if (i >= 13) {
				return false;
			}
			for (i = 0; i < 8; i++) {
				int j = i == newPath.size() - 1 ? 0 : (i + 1);
				Vec3d vec = this.findNextOpenPath(newPath.get(i), newPath.get(j));
				if (vec == null) {
					return false;
				}
				newPath.set(j, vec);
			}
			this.flightPath.clear();
			this.flightPath.addAll(newPath);
			return true;
		}

		@Nullable
		private boolean set2ClosestPathPoint() {
			Vec3d vec = this.entity.getPositionVector();
			ArrayList<Vec3d> tempPath = new ArrayList(this.flightPath);
			tempPath.sort(new ProcedureUtils.VecSorter(vec));
			for (Vec3d vec1 : tempPath) {
				if (this.isDirectPath(vec, vec1)) {
					this.currentPathPoint = this.flightPath.indexOf(vec1);
					return true;
				}
			}
			return false;
		}

		@Nullable
		private Vec3d findNextOpenPath(Vec3d ogVec1, Vec3d ogVec2) {
			Vec3d tvec;
			Vec3d vec2 = ogVec2;
			for (int y = 1; y < 13; vec2 = ogVec2.addVector(0d, y % 2 == 0 ? -y / 2 : (y / 2 + 1), 0d), y++) {
				for (int x = 1; x < 9; vec2 = tvec.addVector((x % 2 == 0 ? -1d : 1d) * x++, 0d, 0d)) {
					tvec = vec2;
					for (int z = 1; z < 9; vec2 = vec2.addVector(0d, 0d, (z % 2 == 0 ? -1d : 1d) * z++)) {
						if (ProcedureUtils.isSpaceOpenToStandOn(this.entity, new BlockPos(vec2)) && this.isDirectPath(ogVec1, vec2)) {
							return vec2;
						}
					}
				}
			}
			return null;
		}

		private boolean isDirectPath(Vec3d fromVec, Vec3d toVec) {
			float f0 = this.entity.width * 0.5f;
			float f1 = this.entity.height;
			AxisAlignedBB fromAabb = new AxisAlignedBB(fromVec.x - f0, fromVec.y, fromVec.z - f0, fromVec.x + f0, fromVec.y + f1, fromVec.z + f0);
			AxisAlignedBB toAabb = new AxisAlignedBB(toVec.x - f0, toVec.y, toVec.z - f0, toVec.x + f0, toVec.y + f1, toVec.z + f0);
			return this.entity.world.rayTraceBlocks(new Vec3d(fromAabb.minX, fromAabb.minY, fromAabb.minZ), new Vec3d(toAabb.minX, toAabb.minY, toAabb.minZ), false, true, false) == null
			 && this.entity.world.rayTraceBlocks(new Vec3d(fromAabb.maxX, fromAabb.minY, fromAabb.minZ), new Vec3d(toAabb.maxX, toAabb.minY, toAabb.minZ), false, true, false) == null
			 && this.entity.world.rayTraceBlocks(new Vec3d(fromAabb.minX, fromAabb.maxY, fromAabb.minZ), new Vec3d(toAabb.minX, toAabb.maxY, toAabb.minZ), false, true, false) == null
			 && this.entity.world.rayTraceBlocks(new Vec3d(fromAabb.minX, fromAabb.minY, fromAabb.maxZ), new Vec3d(toAabb.minX, toAabb.minY, toAabb.maxZ), false, true, false) == null
			 && this.entity.world.rayTraceBlocks(new Vec3d(fromAabb.maxX, fromAabb.maxY, fromAabb.minZ), new Vec3d(toAabb.maxX, toAabb.maxY, toAabb.minZ), false, true, false) == null
			 && this.entity.world.rayTraceBlocks(new Vec3d(fromAabb.maxX, fromAabb.maxY, fromAabb.maxZ), new Vec3d(toAabb.maxX, toAabb.maxY, toAabb.maxZ), false, true, false) == null
			 && this.entity.world.rayTraceBlocks(new Vec3d(fromAabb.maxX, fromAabb.minY, fromAabb.maxZ), new Vec3d(toAabb.maxX, toAabb.minY, toAabb.maxZ), false, true, false) == null
			 && this.entity.world.rayTraceBlocks(new Vec3d(fromAabb.minX, fromAabb.maxY, fromAabb.maxZ), new Vec3d(toAabb.minX, toAabb.maxY, toAabb.maxZ), false, true, false) == null;
		}

		private Vec3d getGroundBelow(Entity target) {
			BlockPos pos = target.getPosition().down();
			while (this.entity.world.getBlockState(pos).getCollisionBoundingBox(this.entity.world, pos) == null) {
				pos = pos.down();
			}
			return new Vec3d(pos.up());
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving<EntityCustom>(renderManager, new ModelBigBird(), 0.5f * ENTITY_SCALE) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/bigbird.png");
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
						((ModelBigBird)this.getMainModel()).body.rotateAngleZ = -ProcedureUtils.interpolateRotation(entity.prevRotationRoll, entity.rotationRoll, partialTicks) * (float)Math.PI / 180F;
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
						super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					}
				};
			});
		}

		// Made with Blockbench 4.12.3
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelBigBird extends ModelBase {
			private final ModelRenderer body;
			private final ModelRenderer head;
			private final ModelRenderer bone36;
			private final ModelRenderer bone2;
			private final ModelRenderer upperBeak;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone13;
			private final ModelRenderer bone12;
			private final ModelRenderer bone11;
			private final ModelRenderer lowerBeak;
			private final ModelRenderer bone8;
			private final ModelRenderer bone9;
			private final ModelRenderer bone10;
			private final ModelRenderer bone14;
			private final ModelRenderer bone15;
			private final ModelRenderer bone16;
			private final ModelRenderer bone20;
			private final ModelRenderer spike3;
			private final ModelRenderer spike4;
			private final ModelRenderer torsoRight;
			private final ModelRenderer torsoLeft;
			private final ModelRenderer wingRight;
			private final ModelRenderer bone34;
			private final ModelRenderer wingTipRight;
			private final ModelRenderer wingLeft;
			private final ModelRenderer bone3;
			private final ModelRenderer wingTipLeft;
			private final ModelRenderer legRight;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
			private final ModelRenderer bone24;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			private final ModelRenderer bone27;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer legLeft;
			private final ModelRenderer bone19;
			private final ModelRenderer bone37;
			private final ModelRenderer bone38;
			private final ModelRenderer bone39;
			private final ModelRenderer bone40;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
			private final ModelRenderer bone43;
			private final ModelRenderer bone44;
			private final ModelRenderer bone45;
			private final ModelRenderer bone46;
			private final ModelRenderer bone47;
			private final ModelRenderer bone48;
			private final ModelRenderer legTop;
			private final ModelRenderer bone17;
			private final ModelRenderer bone51;
			private final ModelRenderer bone52;
			private final ModelRenderer bone53;
			private final ModelRenderer bone54;
			private final ModelRenderer bone55;
			private final ModelRenderer bone56;
			private final ModelRenderer bone57;
			private final ModelRenderer bone58;
			private final ModelRenderer bone59;
			private final ModelRenderer bone60;
			private final ModelRenderer bone61;
			private final ModelRenderer bone62;
			private final ModelRenderer spike;
			private final ModelRenderer spike2;
			private final ModelRenderer tail;
			private final ModelRenderer tail1;
			private final ModelRenderer tail1a;
			private final ModelRenderer tail2;
			private final ModelRenderer tail2a;
			private final ModelRenderer tail3;
			private final ModelRenderer tail3a;
			public ModelBigBird() {
				textureWidth = 64;
				textureHeight = 64;
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 11.0F, 0.0F);
				setRotationAngle(body, -0.7854F, 0.0F, 0.0F);
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 0.0F, 0.0F);
				body.addChild(head);
				setRotationAngle(head, 0.7854F, 0.0F, 0.0F);
				head.cubeList.add(new ModelBox(head, 0, 55, -1.5F, -1.6328F, -3.8875F, 3, 3, 4, -0.1F, false));
				head.cubeList.add(new ModelBox(head, 0, 12, -1.5F, -1.6328F, -5.6875F, 3, 3, 2, -0.1F, false));
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, 0.1328F, -5.1016F);
				head.addChild(bone36);
				setRotationAngle(bone36, -1.0472F, 0.0F, 0.0F);
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone36.addChild(bone2);
				setRotationAngle(bone2, 0.0F, -0.7854F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 11, -5.0F, -6.0F, -5.0F, 10, 11, 10, -3.5F, false));
				upperBeak = new ModelRenderer(this);
				upperBeak.setRotationPoint(0.0F, 0.25F, -6.0F);
				head.addChild(upperBeak);
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, 0.0F, -8.0F);
				upperBeak.addChild(bone4);
				setRotationAngle(bone4, 0.0F, -0.2618F, 0.7854F);
				bone4.cubeList.add(new ModelBox(bone4, 50, -4, 0.0F, -2.0F, 0.0F, 0, 4, 4, 0.0F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 4.0F);
				bone4.addChild(bone5);
				setRotationAngle(bone5, 0.0F, 0.1745F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 50, -1, 0.0F, -2.0F, 0.0F, 0, 4, 5, 0.0F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 0.0F, -8.0F);
				upperBeak.addChild(bone6);
				setRotationAngle(bone6, 0.0F, 0.2618F, -0.7854F);
				bone6.cubeList.add(new ModelBox(bone6, 50, -4, 0.0F, -2.0F, 0.0F, 0, 4, 4, 0.0F, true));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 0.0F, 4.0F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, 0.0F, -0.1745F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 50, -1, 0.0F, -2.0F, 0.0F, 0, 4, 5, 0.0F, true));
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, -1.75F, -2.2F);
				upperBeak.addChild(bone13);
				setRotationAngle(bone13, 0.5236F, 0.0F, 0.0F);
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, 0.0F, -0.05F);
				bone13.addChild(bone12);
				setRotationAngle(bone12, 0.0F, 0.0F, 0.5236F);
				bone12.cubeList.add(new ModelBox(bone12, 50, 4, 0.0F, 0.0F, 0.0F, 0, 3, 4, 0.0F, true));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, 0.0F, -0.05F);
				bone13.addChild(bone11);
				setRotationAngle(bone11, 0.0F, 0.0F, -0.5236F);
				bone11.cubeList.add(new ModelBox(bone11, 50, 4, 0.0F, 0.0F, 0.0F, 0, 3, 4, 0.0F, false));
				lowerBeak = new ModelRenderer(this);
				lowerBeak.setRotationPoint(0.0F, 0.25F, -6.0F);
				head.addChild(lowerBeak);
				setRotationAngle(lowerBeak, -0.5236F, 0.0F, -3.1416F);
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, 0.0F, -8.0F);
				lowerBeak.addChild(bone8);
				setRotationAngle(bone8, 0.0F, -0.2618F, 0.7854F);
				bone8.cubeList.add(new ModelBox(bone8, 50, -4, 0.0F, -2.0F, 0.0F, 0, 4, 4, 0.0F, false));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 0.0F, 4.0F);
				bone8.addChild(bone9);
				setRotationAngle(bone9, 0.0F, 0.1745F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 50, -1, 0.0F, -2.0F, 0.0F, 0, 4, 5, 0.0F, false));
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0F, 0.0F, -8.0F);
				lowerBeak.addChild(bone10);
				setRotationAngle(bone10, 0.0F, 0.2618F, -0.7854F);
				bone10.cubeList.add(new ModelBox(bone10, 50, -4, 0.0F, -2.0F, 0.0F, 0, 4, 4, 0.0F, true));
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, 0.0F, 4.0F);
				bone10.addChild(bone14);
				setRotationAngle(bone14, 0.0F, -0.1745F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 50, -1, 0.0F, -2.0F, 0.0F, 0, 4, 5, 0.0F, true));
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.0F, -1.75F, -2.2F);
				lowerBeak.addChild(bone15);
				setRotationAngle(bone15, 0.5236F, 0.0F, 0.0F);
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, 0.0F, -0.05F);
				bone15.addChild(bone16);
				setRotationAngle(bone16, 0.0F, 0.0F, 0.5236F);
				bone16.cubeList.add(new ModelBox(bone16, 50, 4, 0.0F, 0.0F, 0.0F, 0, 3, 4, 0.0F, true));
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.0F, 0.0F, -0.05F);
				bone15.addChild(bone20);
				setRotationAngle(bone20, 0.0F, 0.0F, -0.5236F);
				bone20.cubeList.add(new ModelBox(bone20, 50, 4, 0.0F, 0.0F, 0.0F, 0, 3, 4, 0.0F, false));
				spike3 = new ModelRenderer(this);
				spike3.setRotationPoint(-0.2881F, -1.2407F, -1.2676F);
				head.addChild(spike3);
				setRotationAngle(spike3, -0.3491F, -0.7854F, 0.0F);
				spike3.cubeList.add(new ModelBox(spike3, 26, 56, -1.0F, -2.0F, -1.0F, 2, 4, 2, -0.3F, false));
				spike4 = new ModelRenderer(this);
				spike4.setRotationPoint(0.0F, 3.25F, 0.0F);
				spike3.addChild(spike4);
				spike4.cubeList.add(new ModelBox(spike4, 26, 56, -1.0F, -2.5F, -1.0F, 2, 4, 2, -0.5F, false));
				torsoRight = new ModelRenderer(this);
				torsoRight.setRotationPoint(0.5391F, 0.5F, -0.4688F);
				body.addChild(torsoRight);
				setRotationAngle(torsoRight, 0.0F, 0.1309F, 0.0F);
				torsoRight.cubeList.add(new ModelBox(torsoRight, 30, 0, -4.4957F, -2.5F, -0.0653F, 4, 5, 12, 0.0F, false));
				torsoLeft = new ModelRenderer(this);
				torsoLeft.setRotationPoint(-0.5391F, 0.5F, -0.4688F);
				body.addChild(torsoLeft);
				setRotationAngle(torsoLeft, 0.0F, -0.1309F, 0.0F);
				torsoLeft.cubeList.add(new ModelBox(torsoLeft, 30, 0, 0.4957F, -2.5F, -0.0653F, 4, 5, 12, 0.0F, true));
				wingRight = new ModelRenderer(this);
				wingRight.setRotationPoint(-2.0F, -0.5F, 2.0F);
				body.addChild(wingRight);
				setRotationAngle(wingRight, 0.0F, 0.0F, 0.7854F);
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(0.0F, 0.0F, 0.0F);
				wingRight.addChild(bone34);
				bone34.cubeList.add(new ModelBox(bone34, 0, 32, -13.0234F, -0.9375F, -1.25F, 13, 2, 2, 0.0F, false));
				bone34.cubeList.add(new ModelBox(bone34, -11, 36, -13.0234F, 0.075F, 0.6875F, 13, 0, 11, 0.001F, false));
				wingTipRight = new ModelRenderer(this);
				wingTipRight.setRotationPoint(-12.9531F, 0.1156F, -1.2656F);
				bone34.addChild(wingTipRight);
				setRotationAngle(wingTipRight, 0.0F, 0.2618F, -2.0944F);
				wingTipRight.cubeList.add(new ModelBox(wingTipRight, 8, 62, -8.9297F, -0.4594F, 0.3156F, 9, 1, 1, 0.25F, false));
				wingTipRight.cubeList.add(new ModelBox(wingTipRight, 9, 62, -16.9297F, -0.4594F, 0.0156F, 8, 1, 1, 0.0F, false));
				wingTipRight.cubeList.add(new ModelBox(wingTipRight, -11, 0, -17.0703F, -0.0406F, 0.9844F, 17, 0, 11, 0.001F, false));
				wingLeft = new ModelRenderer(this);
				wingLeft.setRotationPoint(2.0F, -0.5F, 2.0F);
				body.addChild(wingLeft);
				setRotationAngle(wingLeft, 0.0F, 0.0F, -0.7854F);
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
				wingLeft.addChild(bone3);
				bone3.cubeList.add(new ModelBox(bone3, 0, 32, 0.0234F, -0.9375F, -1.25F, 13, 2, 2, 0.0F, true));
				bone3.cubeList.add(new ModelBox(bone3, -11, 36, 0.0234F, 0.075F, 0.6875F, 13, 0, 11, 0.001F, true));
				wingTipLeft = new ModelRenderer(this);
				wingTipLeft.setRotationPoint(12.9531F, 0.1156F, -1.2656F);
				bone3.addChild(wingTipLeft);
				setRotationAngle(wingTipLeft, 0.0F, -0.2618F, 2.0944F);
				wingTipLeft.cubeList.add(new ModelBox(wingTipLeft, 8, 62, -0.0703F, -0.4594F, 0.3156F, 9, 1, 1, 0.25F, true));
				wingTipLeft.cubeList.add(new ModelBox(wingTipLeft, 9, 62, 8.9297F, -0.4594F, 0.0156F, 8, 1, 1, 0.0F, true));
				wingTipLeft.cubeList.add(new ModelBox(wingTipLeft, -11, 0, 0.0703F, -0.0406F, 0.9844F, 17, 0, 11, 0.001F, true));
				legRight = new ModelRenderer(this);
				legRight.setRotationPoint(-2.6693F, 2.5938F, 7.1771F);
				body.addChild(legRight);
				setRotationAngle(legRight, 0.5236F, 0.0F, 0.2618F);
				legRight.cubeList.add(new ModelBox(legRight, 14, 55, -1.5729F, -1.0938F, -1.3333F, 3, 4, 3, 0.0F, false));
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(-0.2135F, 3.0469F, -0.0833F);
				legRight.addChild(bone21);
				setRotationAngle(bone21, 0.2618F, 0.2618F, -0.1745F);
				bone21.cubeList.add(new ModelBox(bone21, 36, 0, -0.4688F, -1.2031F, -0.3984F, 1, 4, 1, 0.0F, false));
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(0.0688F, 2.7531F, 0.1484F);
				bone21.addChild(bone22);
				setRotationAngle(bone22, -0.2618F, 0.0F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, false));
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(-0.5F, 0.0F, -2.5F);
				bone22.addChild(bone23);
				setRotationAngle(bone23, 0.5236F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 26, 40, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, false));
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone23.addChild(bone24);
				setRotationAngle(bone24, 0.5236F, 0.0F, 0.0F);
				bone24.cubeList.add(new ModelBox(bone24, 31, 43, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, false));
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.0688F, 2.7531F, -0.1016F);
				bone21.addChild(bone31);
				setRotationAngle(bone31, 0.0F, -2.618F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, false));
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(-0.5F, 0.0F, -2.5F);
				bone31.addChild(bone32);
				setRotationAngle(bone32, 0.5236F, 0.0F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 26, 40, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, false));
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone32.addChild(bone33);
				setRotationAngle(bone33, 0.5236F, 0.0F, 0.0F);
				bone33.cubeList.add(new ModelBox(bone33, 31, 43, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, false));
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0688F, 2.7531F, 0.3984F);
				bone21.addChild(bone25);
				setRotationAngle(bone25, -0.1745F, 0.3491F, 0.0F);
				bone25.cubeList.add(new ModelBox(bone25, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, false));
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(-0.5F, 0.0F, -2.5F);
				bone25.addChild(bone26);
				setRotationAngle(bone26, 0.5236F, 0.0F, 0.0F);
				bone26.cubeList.add(new ModelBox(bone26, 26, 40, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, false));
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone26.addChild(bone27);
				setRotationAngle(bone27, 0.5236F, 0.0F, 0.0F);
				bone27.cubeList.add(new ModelBox(bone27, 31, 43, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, false));
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.0688F, 2.7531F, 0.3984F);
				bone21.addChild(bone28);
				setRotationAngle(bone28, -0.1745F, -0.3491F, 0.0F);
				bone28.cubeList.add(new ModelBox(bone28, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, false));
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(-0.5F, 0.0F, -2.5F);
				bone28.addChild(bone29);
				setRotationAngle(bone29, 0.5236F, 0.0F, 0.0F);
				bone29.cubeList.add(new ModelBox(bone29, 26, 40, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, false));
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone29.addChild(bone30);
				setRotationAngle(bone30, 0.5236F, 0.0F, 0.0F);
				bone30.cubeList.add(new ModelBox(bone30, 31, 43, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, false));
				legLeft = new ModelRenderer(this);
				legLeft.setRotationPoint(2.6693F, 2.5938F, 7.1771F);
				body.addChild(legLeft);
				setRotationAngle(legLeft, 0.5236F, 0.0F, -0.2618F);
				legLeft.cubeList.add(new ModelBox(legLeft, 14, 55, -1.4271F, -1.0938F, -1.3333F, 3, 4, 3, 0.0F, true));
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.2135F, 3.0469F, -0.0833F);
				legLeft.addChild(bone19);
				setRotationAngle(bone19, 0.2618F, -0.2618F, 0.1745F);
				bone19.cubeList.add(new ModelBox(bone19, 36, 0, -0.5313F, -1.2031F, -0.3984F, 1, 4, 1, 0.0F, true));
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(-0.0688F, 2.7531F, 0.1484F);
				bone19.addChild(bone37);
				setRotationAngle(bone37, -0.2618F, 0.0F, 0.0F);
				bone37.cubeList.add(new ModelBox(bone37, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, true));
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(0.5F, 0.0F, -2.5F);
				bone37.addChild(bone38);
				setRotationAngle(bone38, 0.5236F, 0.0F, 0.0F);
				bone38.cubeList.add(new ModelBox(bone38, 26, 40, -1.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, true));
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone38.addChild(bone39);
				setRotationAngle(bone39, 0.5236F, 0.0F, 0.0F);
				bone39.cubeList.add(new ModelBox(bone39, 31, 43, -1.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, true));
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(-0.0688F, 2.7531F, -0.1016F);
				bone19.addChild(bone40);
				setRotationAngle(bone40, 0.0F, 2.618F, 0.0F);
				bone40.cubeList.add(new ModelBox(bone40, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, true));
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.5F, 0.0F, -2.5F);
				bone40.addChild(bone41);
				setRotationAngle(bone41, 0.5236F, 0.0F, 0.0F);
				bone41.cubeList.add(new ModelBox(bone41, 26, 40, -1.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, true));
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone41.addChild(bone42);
				setRotationAngle(bone42, 0.5236F, 0.0F, 0.0F);
				bone42.cubeList.add(new ModelBox(bone42, 31, 43, -1.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, true));
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(-0.0688F, 2.7531F, 0.3984F);
				bone19.addChild(bone43);
				setRotationAngle(bone43, -0.1745F, -0.3491F, 0.0F);
				bone43.cubeList.add(new ModelBox(bone43, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, true));
				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(0.5F, 0.0F, -2.5F);
				bone43.addChild(bone44);
				setRotationAngle(bone44, 0.5236F, 0.0F, 0.0F);
				bone44.cubeList.add(new ModelBox(bone44, 26, 40, -1.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, true));
				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone44.addChild(bone45);
				setRotationAngle(bone45, 0.5236F, 0.0F, 0.0F);
				bone45.cubeList.add(new ModelBox(bone45, 31, 43, -1.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, true));
				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(-0.0688F, 2.7531F, 0.3984F);
				bone19.addChild(bone46);
				setRotationAngle(bone46, -0.1745F, 0.3491F, 0.0F);
				bone46.cubeList.add(new ModelBox(bone46, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, true));
				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(0.5F, 0.0F, -2.5F);
				bone46.addChild(bone47);
				setRotationAngle(bone47, 0.5236F, 0.0F, 0.0F);
				bone47.cubeList.add(new ModelBox(bone47, 26, 40, -1.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, true));
				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone47.addChild(bone48);
				setRotationAngle(bone48, 0.5236F, 0.0F, 0.0F);
				bone48.cubeList.add(new ModelBox(bone48, 31, 43, -1.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, true));
				legTop = new ModelRenderer(this);
				legTop.setRotationPoint(-0.1693F, -1.4063F, 8.1771F);
				body.addChild(legTop);
				setRotationAngle(legTop, 0.5236F, 0.0F, -3.1416F);
				legTop.cubeList.add(new ModelBox(legTop, 14, 55, -1.5729F, -1.0938F, -1.3333F, 3, 4, 3, 0.0F, false));
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(-0.2135F, 3.0469F, -0.0833F);
				legTop.addChild(bone17);
				setRotationAngle(bone17, 0.4363F, 0.0F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 36, 0, -0.4688F, -1.2031F, -0.3984F, 1, 4, 1, 0.0F, false));
				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(0.0687F, 2.7531F, -0.1016F);
				bone17.addChild(bone51);
				setRotationAngle(bone51, 1.0472F, 0.0F, 0.0F);
				bone51.cubeList.add(new ModelBox(bone51, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, false));
				bone52 = new ModelRenderer(this);
				bone52.setRotationPoint(-0.5F, 0.0F, -2.5F);
				bone51.addChild(bone52);
				setRotationAngle(bone52, 1.0472F, 0.0F, 0.0F);
				bone52.cubeList.add(new ModelBox(bone52, 26, 40, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, false));
				bone53 = new ModelRenderer(this);
				bone53.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone52.addChild(bone53);
				setRotationAngle(bone53, 1.0472F, 0.0F, 0.0F);
				bone53.cubeList.add(new ModelBox(bone53, 31, 43, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, false));
				bone54 = new ModelRenderer(this);
				bone54.setRotationPoint(0.0687F, 2.7531F, -0.1016F);
				bone17.addChild(bone54);
				setRotationAngle(bone54, 1.5708F, -0.2618F, -0.6981F);
				bone54.cubeList.add(new ModelBox(bone54, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, false));
				bone55 = new ModelRenderer(this);
				bone55.setRotationPoint(-0.5F, 0.0F, -2.5F);
				bone54.addChild(bone55);
				setRotationAngle(bone55, 1.0472F, 0.0F, 0.0F);
				bone55.cubeList.add(new ModelBox(bone55, 26, 40, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, false));
				bone56 = new ModelRenderer(this);
				bone56.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone55.addChild(bone56);
				setRotationAngle(bone56, 1.0472F, 0.0F, 0.0F);
				bone56.cubeList.add(new ModelBox(bone56, 31, 43, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, false));
				bone57 = new ModelRenderer(this);
				bone57.setRotationPoint(0.0687F, 2.7531F, 0.3984F);
				bone17.addChild(bone57);
				setRotationAngle(bone57, 1.0472F, 0.0F, 0.3491F);
				bone57.cubeList.add(new ModelBox(bone57, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, false));
				bone58 = new ModelRenderer(this);
				bone58.setRotationPoint(-0.5F, 0.0F, -2.5F);
				bone57.addChild(bone58);
				setRotationAngle(bone58, 1.0472F, 0.0F, 0.0F);
				bone58.cubeList.add(new ModelBox(bone58, 26, 40, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, false));
				bone59 = new ModelRenderer(this);
				bone59.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone58.addChild(bone59);
				setRotationAngle(bone59, 1.0472F, 0.0F, 0.0F);
				bone59.cubeList.add(new ModelBox(bone59, 31, 43, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, false));
				bone60 = new ModelRenderer(this);
				bone60.setRotationPoint(0.0687F, 2.7531F, 0.3984F);
				bone17.addChild(bone60);
				setRotationAngle(bone60, 1.0472F, 0.0F, -0.3491F);
				bone60.cubeList.add(new ModelBox(bone60, 26, 43, -0.5F, -0.25F, -2.75F, 1, 1, 3, -0.2F, false));
				bone61 = new ModelRenderer(this);
				bone61.setRotationPoint(-0.5F, 0.0F, -2.5F);
				bone60.addChild(bone61);
				setRotationAngle(bone61, 1.0472F, 0.0F, 0.0F);
				bone61.cubeList.add(new ModelBox(bone61, 26, 40, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.3F, false));
				bone62 = new ModelRenderer(this);
				bone62.setRotationPoint(0.0F, 0.0F, -1.4F);
				bone61.addChild(bone62);
				setRotationAngle(bone62, 1.0472F, 0.0F, 0.0F);
				bone62.cubeList.add(new ModelBox(bone62, 31, 43, 0.0F, -0.25F, -1.75F, 1, 1, 2, -0.4F, false));
				spike = new ModelRenderer(this);
				spike.setRotationPoint(0.7119F, -1.2407F, 3.7324F);
				body.addChild(spike);
				setRotationAngle(spike, -0.3491F, 0.7854F, 0.0F);
				spike.cubeList.add(new ModelBox(spike, 26, 56, -1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F, false));
				spike2 = new ModelRenderer(this);
				spike2.setRotationPoint(0.0F, 5.25F, 0.0F);
				spike.addChild(spike2);
				spike2.cubeList.add(new ModelBox(spike2, 26, 56, -1.0F, -2.5F, -1.0F, 2, 4, 2, -0.4F, false));
				tail = new ModelRenderer(this);
				tail.setRotationPoint(0.0781F, 0.3406F, 11.5313F);
				body.addChild(tail);
				setRotationAngle(tail, 0.5236F, 0.0F, 0.0F);
				tail1 = new ModelRenderer(this);
				tail1.setRotationPoint(0.0F, -1.0F, 0.0F);
				tail.addChild(tail1);
				setRotationAngle(tail1, 0.0873F, 0.0F, 0.0F);
				tail1.cubeList.add(new ModelBox(tail1, -8, 47, -4.0F, 0.0F, -1.0F, 8, 0, 8, 0.0F, false));
				tail1a = new ModelRenderer(this);
				tail1a.setRotationPoint(0.0F, 0.0F, 7.0F);
				tail1.addChild(tail1a);
				setRotationAngle(tail1a, -0.2618F, 0.0F, 0.0F);
				tail1a.cubeList.add(new ModelBox(tail1a, 8, 47, -4.0F, 0.0F, 0.0F, 8, 0, 8, 0.0F, false));
				tail2 = new ModelRenderer(this);
				tail2.setRotationPoint(0.0F, 0.0F, 0.0F);
				tail.addChild(tail2);
				setRotationAngle(tail2, 0.0F, 0.3491F, -0.2618F);
				tail2.cubeList.add(new ModelBox(tail2, -8, 47, -4.0F, 0.0F, -1.0F, 8, 0, 8, 0.0F, false));
				tail2a = new ModelRenderer(this);
				tail2a.setRotationPoint(0.0F, 0.0F, 7.0F);
				tail2.addChild(tail2a);
				setRotationAngle(tail2a, -0.2618F, 0.0F, 0.0F);
				tail2a.cubeList.add(new ModelBox(tail2a, 8, 47, -4.0F, 0.0F, 0.0F, 8, 0, 8, 0.0F, false));
				tail3 = new ModelRenderer(this);
				tail3.setRotationPoint(0.0F, 0.0F, 0.0F);
				tail.addChild(tail3);
				setRotationAngle(tail3, -0.0873F, -0.3491F, 0.2618F);
				tail3.cubeList.add(new ModelBox(tail3, -8, 47, -4.0F, 0.0F, -1.0F, 8, 0, 8, 0.0F, false));
				tail3a = new ModelRenderer(this);
				tail3a.setRotationPoint(0.0F, 0.0F, 7.0F);
				tail3.addChild(tail3a);
				setRotationAngle(tail3a, -0.2618F, 0.0F, 0.0F);
				tail3a.cubeList.add(new ModelBox(tail3a, 8, 47, -4.0F, 0.0F, 0.0F, 8, 0, 8, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				body.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
				if (entityIn.onGround) {
					body.setRotationPoint(0.0F, 11.0F, 0.0F);
					setRotationAngle(body, -0.7854F, 0.0F, 0.0F);
					setRotationAngle(head, 0.7854F, 0.0F, 0.0F);
					setRotationAngle(wingRight, 0.0F, 1.5708F, 0.2618F);
					setRotationAngle(wingTipRight, -2.4435F, -2.9671F, -0.0873F);
					setRotationAngle(wingLeft, 0.0F, -1.5708F, -0.2618F);
					setRotationAngle(wingTipLeft, -2.4435F, 2.9671F, 0.0873F);
					setRotationAngle(legRight, 0.5236F, 0.0F, 0.2618F);
					setRotationAngle(legLeft, 0.5236F, 0.0F, -0.2618F);
					tail.rotateAngleX = 0.5236F;
					lowerBeak.rotateAngleX = -0.5236F;
				} else {
					body.setRotationPoint(0.0F, 21.0F, 0.0F);
					setRotationAngle(body, 0.0F, 0.0F, 0.0F);
					setRotationAngle(head, 0.0F, 0.0F, 0.0F);
					float wingFlapAngle = MathHelper.cos(ageInTicks * 0.3F) * 0.65f;
					setRotationAngle(wingRight, 0.0F, 0.0F, wingFlapAngle);
					setRotationAngle(wingLeft, 0.0F, 0.0F, -wingFlapAngle);
					wingFlapAngle = MathHelper.cos((ageInTicks - 3.0f) * 0.3F) * 0.845f;
					setRotationAngle(wingTipRight, 0.0F, 0.2618F, wingFlapAngle - 0.2618F);
					setRotationAngle(wingTipLeft, 0.0F, -0.2618F, -wingFlapAngle + 0.2618F);
					setRotationAngle(legRight, 1.0472F, 0.0F, 0.2618F);
					setRotationAngle(legLeft, 1.0472F, 0.0F, -0.2618F);
					tail.rotateAngleX = 0.0F;
					lowerBeak.rotateAngleX = 0.0F;
				}
				tail1.rotateAngleX = MathHelper.sin(ageInTicks * 0.09F) * 0.09F + 0.0873F;
				tail2.rotateAngleZ = MathHelper.cos(ageInTicks * 0.067F) * 0.09F - 0.2618F;
				tail3.rotateAngleZ = -MathHelper.cos(ageInTicks * 0.067F) * 0.09F + 0.2618F;
				head.rotateAngleY = netHeadYaw * 0.017453292F;
				head.rotateAngleX += headPitch * 0.017453292F;
			}
		}
	}
}
