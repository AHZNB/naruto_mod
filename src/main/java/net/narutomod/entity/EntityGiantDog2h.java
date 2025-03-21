
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.pathfinding.PathNavigate;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityGiantDog2h extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 181;
	public static final int ENTITYID_RANGED = 182;
	private static final float ENTITY_SCALE = 10f;
	
	public EntityGiantDog2h(ElementsNarutomodMod instance) {
		super(instance, 446);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "giant_dog_2h"), ENTITYID).name("giant_dog_2h")
		 .tracker(64, 3, true).egg(-11388356, -9088173).build());
	}

	public static class EntityCustom extends EntitySummonAnimal.Base implements IMob, EntityTailedBeast.ICollisionData {
		private static final DataParameter<Boolean> ONE_HEAD = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private int splitTicks;
		private EntityCustom child;
		private final ProcedureUtils.CollisionHelper collisionData;

		public EntityCustom(World world) {
			super(world);
			this.collisionData = new ProcedureUtils.CollisionHelper(this);
			this.setOGSize(0.5f, 0.85f);
			this.experienceValue = 500;
			this.postScaleFixup();
			this.stepHeight = this.height / 3;
		}

		public EntityCustom(EntityLivingBase player, double maxHealth) {
			super(player);
			this.collisionData = new ProcedureUtils.CollisionHelper(this);
			this.setOGSize(0.5f, 0.85f);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
			this.experienceValue = 500;
			RayTraceResult res = ProcedureUtils.raytraceBlocks(player, 4.0);
			double x = res != null ? 0.5d + res.getBlockPos().getX() : player.getPositionEyes(1f).add(player.getLookVec().scale(4)).x;
			double z = res != null ? 0.5d + res.getBlockPos().getZ() : player.getPositionEyes(1f).add(player.getLookVec().scale(4)).z;
			this.setPosition(x, player.posY, z);
			this.rotationYaw = player.rotationYaw - 180.0f;
			this.rotationYawHead = this.rotationYaw;
			this.postScaleFixup();
			this.stepHeight = this.height / 3;
		}

		public EntityCustom(EntityLivingBase player) {
			this(player, 400d);
		}

		@Override
		public float getScale() {
			return ENTITY_SCALE;
		}

		@Override
		public void entityInit() {
			super.entityInit();
			this.dataManager.register(ONE_HEAD, Boolean.valueOf(false));
		}

		private void setOneHead(boolean b) {
			this.dataManager.set(ONE_HEAD, Boolean.valueOf(b));
		}
	
		public boolean isOneHead() {
			return ((Boolean)this.getDataManager().get(ONE_HEAD)).booleanValue();
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			this.moveHelper = new EntityTailedBeast.MoveHelper(this);
			return new EntityTailedBeast.NavigateGround(this, worldIn);
		}

		@Override
		public SoundEvent getAmbientSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.wolf.growl"));
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.wolf.hurt"));
		}

		@Override
		public SoundEvent getDeathSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.wolf.death"));
		}

		@Override
		protected float getSoundVolume() {
			return 2.0F;
		}

		@Override
		protected float getSoundPitch() {
			return this.rand.nextFloat() * 0.4f + 0.4f;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			//this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.7D);
			//this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000D);
			//this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(50.0D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0);
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

		@Override
		public ProcedureUtils.CollisionHelper getCollisionData() {
			return this.collisionData;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityTailedBeast.AILeapAtTarget(this, 24.0d));
			this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.4f, true) {
				@Override
				public boolean shouldExecute() {
					return !(this.attacker.getControllingPassenger() instanceof EntityPlayer) && super.shouldExecute();
				}
			});
			this.tasks.addTask(3, new EntityAILookIdle(this));
		}

		@Override
		protected void onDeathUpdate() {
			if (!this.world.isRemote) {
				if (this.getMaxHealth() <= 100.0f || this.getSummoner() == null || this.getAge() > this.lifeSpan) {
					this.setDead();
				} else {
					++this.splitTicks;
					if (this.splitTicks < 20) {
						this.setNoAI(true);
					} else {
						float maxhp = this.getMaxHealth() * 0.5f;
						this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxhp);
						this.setHealth(maxhp);
						this.setNoAI(false);
						this.setOneHead(true);
						this.child = this.createChild(maxhp);
						if (this.child != null) {
							this.child.copyLocationAndAnglesFrom(this);
							this.child.rotationYawHead = this.rotationYawHead;
							this.world.spawnEntity(this.child);
							this.child.setOneHead(true);
						}
					}
				}
			}
//System.out.println(">>> maxHealth:"+getMaxHealth()+", curHealth:"+getHealth()+", splitTicks:"+splitTicks+", "+this);
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
			super.travel(strafe, vertical, forward);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				this.tasks.setControlFlag(7, !(this.getControllingPassenger() instanceof EntityPlayer));
			}
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

		@Nullable
		private EntityCustom createChild(float health) {
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner != null) {
					return new EntityCustom(summoner, health);
				}
			}
			return null;
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
				return new RenderLiving<EntityCustom>(renderManager, new ModelDog2head(), 0.5f * ENTITY_SCALE) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/dog.png");
					@Override
					protected ResourceLocation getEntityTexture(EntityCustom entity) {
						return this.texture;
					}
					@Override
					protected void renderModel(EntityCustom entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
						GlStateManager.pushMatrix();
						GlStateManager.translate(0.0f, 1.5f - ENTITY_SCALE * 1.5f, 0.0f);
						GlStateManager.scale(ENTITY_SCALE, ENTITY_SCALE, ENTITY_SCALE);
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
						super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					}
				};
			});
		}

		// Made with Blockbench 3.7.5
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelDog2head extends ModelBase {
			private final ModelRenderer headRight;
			private final ModelRenderer bone6;
			private final ModelRenderer bone8;
			private final ModelRenderer bone3;
			private final ModelRenderer jawRight;
			private final ModelRenderer headLeft;
			private final ModelRenderer bone4;
			private final ModelRenderer bone9;
			private final ModelRenderer bone10;
			private final ModelRenderer jawLeft;
			private final ModelRenderer body;
			private final ModelRenderer tail;
			private final ModelRenderer tail2;
			private final ModelRenderer upperBody;
			private final ModelRenderer wingRight;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer wingLeft;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer cube_r6;
			private final ModelRenderer leg0;
			private final ModelRenderer bone2;
			private final ModelRenderer leg6;
			private final ModelRenderer leg8;
			private final ModelRenderer foot0;
			private final ModelRenderer bone27;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer bone34;
			private final ModelRenderer leg1;
			private final ModelRenderer bone7;
			private final ModelRenderer leg7;
			private final ModelRenderer leg9;
			private final ModelRenderer foot1;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer bone38;
			private final ModelRenderer bone39;
			private final ModelRenderer bone40;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
			private final ModelRenderer leg2;
			private final ModelRenderer bone;
			private final ModelRenderer leg4;
			private final ModelRenderer foot2;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone17;
			private final ModelRenderer bone18;
			private final ModelRenderer bone13;
			private final ModelRenderer bone14;
			private final ModelRenderer bone15;
			private final ModelRenderer bone16;
			private final ModelRenderer leg3;
			private final ModelRenderer bone5;
			private final ModelRenderer leg5;
			private final ModelRenderer foot3;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
			private final ModelRenderer bone24;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			
			public ModelDog2head() {
				textureWidth = 64;
				textureHeight = 32;
	
				headRight = new ModelRenderer(this);
				headRight.setRotationPoint(-2.0F, 13.5F, -2.0F);
				setRotationAngle(headRight, 0.0F, 0.2618F, 0.0F);
				headRight.cubeList.add(new ModelBox(headRight, 0, 1, -2.5F, -2.5F, -4.0F, 5, 5, 4, 0.0F, false));
				headRight.cubeList.add(new ModelBox(headRight, 1, 2, -2.5F, -2.5F, -0.25F, 5, 5, 3, 0.0F, false));
				headRight.cubeList.add(new ModelBox(headRight, 0, 10, -1.5F, -0.5156F, -7.0F, 3, 3, 4, 0.0F, false));
				headRight.cubeList.add(new ModelBox(headRight, 10, 10, -0.4461F, -1.5F, -6.0F, 1, 2, 1, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(-1.5F, 1.0F, -5.0F);
				headRight.addChild(bone6);
				setRotationAngle(bone6, 0.0F, -0.6981F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 20, 0, 0.0F, -1.5F, 0.0F, 1, 3, 2, 0.0F, false));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(1.5F, 1.0F, -5.0F);
				headRight.addChild(bone8);
				setRotationAngle(bone8, 0.0F, 0.6981F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 20, 0, -1.0F, -1.5F, 0.0F, 1, 3, 2, 0.0F, true));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(5.0F, -0.6F, -5.85F);
				headRight.addChild(bone3);
				setRotationAngle(bone3, 0.0F, 0.0F, -3.1416F);
				bone3.cubeList.add(new ModelBox(bone3, 44, 14, 3.5F, -2.5F, -2.1F, 6, 6, 0, -3.9F, true));
				bone3.cubeList.add(new ModelBox(bone3, 44, 14, 0.5F, -2.5F, -2.1F, 6, 6, 0, -3.9F, false));
		
				jawRight = new ModelRenderer(this);
				jawRight.setRotationPoint(0.0F, 1.5F, -4.0F);
				headRight.addChild(jawRight);
				setRotationAngle(jawRight, 0.7854F, 0.0F, 0.0F);
				jawRight.cubeList.add(new ModelBox(jawRight, 50, 27, -1.5F, 0.0F, -3.0F, 3, 1, 4, 0.0F, false));
		
				headLeft = new ModelRenderer(this);
				headLeft.setRotationPoint(2.0F, 13.5F, -2.0F);
				setRotationAngle(headLeft, 0.0F, -0.2618F, 0.0F);
				headLeft.cubeList.add(new ModelBox(headLeft, 0, 1, -2.5F, -2.5F, -4.0F, 5, 5, 4, 0.0F, true));
				headLeft.cubeList.add(new ModelBox(headLeft, 1, 2, -2.5F, -2.5F, -0.25F, 5, 5, 3, 0.0F, true));
				headLeft.cubeList.add(new ModelBox(headLeft, 0, 10, -1.5F, -0.5156F, -7.0F, 3, 3, 4, 0.0F, true));
				headLeft.cubeList.add(new ModelBox(headLeft, 10, 10, -0.5539F, -1.5F, -6.0F, 1, 2, 1, 0.0F, true));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(1.5F, 1.0F, -5.0F);
				headLeft.addChild(bone4);
				setRotationAngle(bone4, 0.0F, 0.6981F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 20, 0, -1.0F, -1.5F, 0.0F, 1, 3, 2, 0.0F, true));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(-1.5F, 1.0F, -5.0F);
				headLeft.addChild(bone9);
				setRotationAngle(bone9, 0.0F, -0.6981F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 20, 0, 0.0F, -1.5F, 0.0F, 1, 3, 2, 0.0F, false));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(-5.0F, -0.6F, -5.85F);
				headLeft.addChild(bone10);
				setRotationAngle(bone10, 0.0F, 0.0F, 3.1416F);
				bone10.cubeList.add(new ModelBox(bone10, 44, 14, -9.5F, -2.5F, -2.1F, 6, 6, 0, -3.9F, false));
				bone10.cubeList.add(new ModelBox(bone10, 44, 14, -6.5F, -2.5F, -2.1F, 6, 6, 0, -3.9F, true));
		
				jawLeft = new ModelRenderer(this);
				jawLeft.setRotationPoint(0.0F, 1.5F, -4.0F);
				headLeft.addChild(jawLeft);
				jawLeft.cubeList.add(new ModelBox(jawLeft, 50, 27, -1.5F, 0.0F, -3.0F, 3, 1, 4, 0.0F, true));
		
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 10.5F, 4.0F);
				setRotationAngle(body, 1.3963F, 0.0F, 0.0F);
				body.cubeList.add(new ModelBox(body, 17, 17, -3.0F, 0.0F, -6.0F, 6, 9, 6, 0.0F, false));
		
				tail = new ModelRenderer(this);
				tail.setRotationPoint(0.0F, 8.5F, -1.0F);
				body.addChild(tail);
				setRotationAngle(tail, -0.5236F, 0.0F, 0.0F);
				tail.cubeList.add(new ModelBox(tail, 9, 18, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
		
				tail2 = new ModelRenderer(this);
				tail2.setRotationPoint(0.0F, 7.5F, 0.0F);
				tail.addChild(tail2);
				setRotationAngle(tail2, 0.5236F, 0.0F, 0.0F);
				tail2.cubeList.add(new ModelBox(tail2, 9, 18, -1.0F, -0.5F, -1.0F, 2, 8, 2, -0.2F, false));
		
				upperBody = new ModelRenderer(this);
				upperBody.setRotationPoint(0.0F, 14.0F, 6.0F);
				setRotationAngle(upperBody, -1.5708F, 0.0F, 0.0F);
				upperBody.cubeList.add(new ModelBox(upperBody, 21, 0, -4.0F, 2.0F, -4.0F, 8, 6, 7, 0.0F, false));
		
				wingRight = new ModelRenderer(this);
				wingRight.setRotationPoint(-4.0927F, 5.8734F, -3.9678F);
				upperBody.addChild(wingRight);
				setRotationAngle(wingRight, 1.5708F, 0.7854F, 0.0F);
				
		
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(1.7704F, 7.9911F, 5.1063F);
				wingRight.addChild(cube_r1);
				setRotationAngle(cube_r1, -0.9722F, -0.0114F, -0.2253F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 52, -6, 0.0F, -10.0F, -12.0F, 0, 10, 6, 0.0F, true));
		
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(2.7704F, 2.7911F, 9.1063F);
				wingRight.addChild(cube_r2);
				setRotationAngle(cube_r2, -0.9722F, -0.0114F, -0.2253F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 18, -3.2F, -4.8F, -11.2F, 2, 8, 2, -0.7F, false));
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(2.2927F, 0.7266F, 10.5178F);
				wingRight.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.3177F, -0.0114F, -0.2253F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 19, -3.2F, -2.8F, -11.2F, 2, 6, 2, -0.7F, false));
		
				wingLeft = new ModelRenderer(this);
				wingLeft.setRotationPoint(4.0927F, 5.8734F, -3.9678F);
				upperBody.addChild(wingLeft);
				setRotationAngle(wingLeft, 1.5708F, -0.7854F, 0.0F);
				
		
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(-1.7704F, 7.9911F, 5.1063F);
				wingLeft.addChild(cube_r4);
				setRotationAngle(cube_r4, -0.9722F, 0.0114F, 0.2253F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 52, -6, 0.0F, -10.0F, -12.0F, 0, 10, 6, 0.0F, false));
		
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(-2.7704F, 2.7911F, 9.1063F);
				wingLeft.addChild(cube_r5);
				setRotationAngle(cube_r5, -0.9722F, 0.0114F, 0.2253F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 18, 1.2F, -4.8F, -11.2F, 2, 8, 2, -0.7F, true));
		
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(-2.2927F, 0.7266F, 10.5178F);
				wingLeft.addChild(cube_r6);
				setRotationAngle(cube_r6, -0.3177F, 0.0114F, 0.2253F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 19, 1.2F, -2.8F, -11.2F, 2, 6, 2, -0.7F, true));
		
				leg0 = new ModelRenderer(this);
				leg0.setRotationPoint(-2.5F, 13.0F, 11.0F);
				
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg0.addChild(bone2);
				setRotationAngle(bone2, -0.1745F, 0.0F, 0.1745F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 18, -1.0F, 0.0F, -1.0F, 2, 5, 2, 0.5F, false));
		
				leg6 = new ModelRenderer(this);
				leg6.setRotationPoint(-1.0F, 5.5F, -1.5F);
				bone2.addChild(leg6);
				setRotationAngle(leg6, 1.0472F, 0.0F, -0.1745F);
				leg6.cubeList.add(new ModelBox(leg6, 0, 18, 0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F, false));
		
				leg8 = new ModelRenderer(this);
				leg8.setRotationPoint(0.0F, 6.0F, 2.0F);
				leg6.addChild(leg8);
				setRotationAngle(leg8, -1.1345F, 0.0436F, 0.0F);
				leg8.cubeList.add(new ModelBox(leg8, 0, 18, 0.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F, false));
		
				foot0 = new ModelRenderer(this);
				foot0.setRotationPoint(1.0F, 3.75F, -2.5F);
				leg8.addChild(foot0);
				setRotationAngle(foot0, 0.3054F, 0.0F, -0.0436F);
				foot0.cubeList.add(new ModelBox(foot0, 0, 28, -1.0F, 0.0F, 0.0F, 2, 1, 2, 0.1F, false));
		
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.25F, -0.25F, 0.1F);
				foot0.addChild(bone27);
				setRotationAngle(bone27, 0.2618F, -0.2618F, 0.0873F);
				bone27.cubeList.add(new ModelBox(bone27, 4, 1, 0.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, false));
		
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.5F, 0.25F, -0.9F);
				bone27.addChild(bone28);
				setRotationAngle(bone28, 0.3491F, 0.0F, 0.0F);
				bone28.cubeList.add(new ModelBox(bone28, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
		
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(1.15F, -0.75F, 1.85F);
				foot0.addChild(bone29);
				setRotationAngle(bone29, 0.4363F, -1.8326F, 0.5236F);
				bone29.cubeList.add(new ModelBox(bone29, 4, 1, 0.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, false));
		
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.5F, 0.25F, -0.9F);
				bone29.addChild(bone30);
				setRotationAngle(bone30, 0.4363F, 0.0F, 0.0F);
				bone30.cubeList.add(new ModelBox(bone30, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
		
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(-0.5F, -0.25F, 0.1F);
				foot0.addChild(bone31);
				setRotationAngle(bone31, 0.2618F, 0.0F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 4, 1, 0.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, false));
		
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(0.5F, 0.25F, -0.9F);
				bone31.addChild(bone32);
				setRotationAngle(bone32, 0.3491F, 0.0F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
		
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(-1.25F, -0.15F, 0.35F);
				foot0.addChild(bone33);
				setRotationAngle(bone33, 0.2618F, 0.2618F, -0.0873F);
				bone33.cubeList.add(new ModelBox(bone33, 4, 1, 0.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, false));
		
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(0.5F, 0.25F, -0.9F);
				bone33.addChild(bone34);
				setRotationAngle(bone34, 0.3491F, 0.0F, 0.0F);
				bone34.cubeList.add(new ModelBox(bone34, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
		
				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(2.5F, 13.0F, 11.0F);
				
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg1.addChild(bone7);
				setRotationAngle(bone7, -0.1745F, 0.0F, -0.1745F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 18, -1.0F, 0.0F, -1.0F, 2, 5, 2, 0.5F, true));
		
				leg7 = new ModelRenderer(this);
				leg7.setRotationPoint(1.0F, 5.5F, -1.5F);
				bone7.addChild(leg7);
				setRotationAngle(leg7, 1.0472F, 0.0F, 0.1745F);
				leg7.cubeList.add(new ModelBox(leg7, 0, 18, -2.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F, true));
		
				leg9 = new ModelRenderer(this);
				leg9.setRotationPoint(0.0F, 6.0F, 2.0F);
				leg7.addChild(leg9);
				setRotationAngle(leg9, -1.1345F, -0.0436F, 0.0F);
				leg9.cubeList.add(new ModelBox(leg9, 0, 18, -2.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F, true));
		
				foot1 = new ModelRenderer(this);
				foot1.setRotationPoint(-1.0F, 3.75F, -2.5F);
				leg9.addChild(foot1);
				setRotationAngle(foot1, 0.3054F, 0.0F, 0.0436F);
				foot1.cubeList.add(new ModelBox(foot1, 0, 28, -1.0F, 0.0F, 0.0F, 2, 1, 2, 0.1F, true));
		
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(-0.25F, -0.25F, 0.1F);
				foot1.addChild(bone35);
				setRotationAngle(bone35, 0.2618F, 0.2618F, -0.0873F);
				bone35.cubeList.add(new ModelBox(bone35, 4, 1, -1.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, true));
		
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(-0.5F, 0.25F, -0.9F);
				bone35.addChild(bone36);
				setRotationAngle(bone36, 0.3491F, 0.0F, 0.0F);
				bone36.cubeList.add(new ModelBox(bone36, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
		
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(-1.15F, -0.75F, 1.85F);
				foot1.addChild(bone37);
				setRotationAngle(bone37, 0.4363F, 1.8326F, -0.5236F);
				bone37.cubeList.add(new ModelBox(bone37, 4, 1, -1.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, true));
		
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(-0.5F, 0.25F, -0.9F);
				bone37.addChild(bone38);
				setRotationAngle(bone38, 0.4363F, 0.0F, 0.0F);
				bone38.cubeList.add(new ModelBox(bone38, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
		
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.5F, -0.25F, 0.1F);
				foot1.addChild(bone39);
				setRotationAngle(bone39, 0.2618F, 0.0F, 0.0F);
				bone39.cubeList.add(new ModelBox(bone39, 4, 1, -1.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, true));
		
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(-0.5F, 0.25F, -0.9F);
				bone39.addChild(bone40);
				setRotationAngle(bone40, 0.3491F, 0.0F, 0.0F);
				bone40.cubeList.add(new ModelBox(bone40, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
		
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(1.25F, -0.15F, 0.35F);
				foot1.addChild(bone41);
				setRotationAngle(bone41, 0.2618F, -0.2618F, 0.0873F);
				bone41.cubeList.add(new ModelBox(bone41, 4, 1, -1.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, true));
		
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(-0.5F, 0.25F, -0.9F);
				bone41.addChild(bone42);
				setRotationAngle(bone42, 0.3491F, 0.0F, 0.0F);
				bone42.cubeList.add(new ModelBox(bone42, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
		
				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(-3.0F, 13.0F, 0.0F);
				
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg2.addChild(bone);
				setRotationAngle(bone, 0.2618F, -0.2618F, 0.1309F);
				bone.cubeList.add(new ModelBox(bone, 0, 18, -1.0F, 0.0F, -1.0F, 2, 6, 2, 0.1F, false));
		
				leg4 = new ModelRenderer(this);
				leg4.setRotationPoint(-1.0F, 6.0F, 1.0F);
				bone.addChild(leg4);
				setRotationAngle(leg4, -0.6109F, 0.1745F, -0.0873F);
				leg4.cubeList.add(new ModelBox(leg4, 0, 18, 0.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F, false));
		
				foot2 = new ModelRenderer(this);
				foot2.setRotationPoint(1.0F, 5.75F, -2.5F);
				leg4.addChild(foot2);
				setRotationAngle(foot2, 0.3054F, 0.0F, -0.0436F);
				foot2.cubeList.add(new ModelBox(foot2, 0, 28, -1.0F, 0.0F, 0.0F, 2, 1, 2, 0.1F, false));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.25F, -0.25F, 0.1F);
				foot2.addChild(bone11);
				setRotationAngle(bone11, 0.2618F, -0.2618F, 0.0873F);
				bone11.cubeList.add(new ModelBox(bone11, 4, 1, 0.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, false));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.5F, 0.25F, -0.9F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, 0.3491F, 0.0F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
		
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(1.15F, -0.75F, 1.85F);
				foot2.addChild(bone17);
				setRotationAngle(bone17, 0.4363F, -1.8326F, 0.5236F);
				bone17.cubeList.add(new ModelBox(bone17, 4, 1, 0.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, false));
		
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.5F, 0.25F, -0.9F);
				bone17.addChild(bone18);
				setRotationAngle(bone18, 0.4363F, 0.0F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
		
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(-0.5F, -0.25F, 0.1F);
				foot2.addChild(bone13);
				setRotationAngle(bone13, 0.2618F, 0.0F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 4, 1, 0.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, false));
		
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.5F, 0.25F, -0.9F);
				bone13.addChild(bone14);
				setRotationAngle(bone14, 0.3491F, 0.0F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(-1.25F, -0.15F, 0.35F);
				foot2.addChild(bone15);
				setRotationAngle(bone15, 0.2618F, 0.2618F, -0.0873F);
				bone15.cubeList.add(new ModelBox(bone15, 4, 1, 0.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.5F, 0.25F, -0.9F);
				bone15.addChild(bone16);
				setRotationAngle(bone16, 0.3491F, 0.0F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, false));
		
				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(3.0F, 13.0F, 0.0F);
				
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg3.addChild(bone5);
				setRotationAngle(bone5, 0.2618F, 0.2618F, -0.1309F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 18, -1.0F, 0.0F, -1.0F, 2, 6, 2, 0.1F, true));
		
				leg5 = new ModelRenderer(this);
				leg5.setRotationPoint(1.0F, 6.0F, 1.0F);
				bone5.addChild(leg5);
				setRotationAngle(leg5, -0.6109F, -0.1745F, 0.0873F);
				leg5.cubeList.add(new ModelBox(leg5, 0, 18, -2.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F, true));
		
				foot3 = new ModelRenderer(this);
				foot3.setRotationPoint(-1.0F, 5.75F, -2.5F);
				leg5.addChild(foot3);
				setRotationAngle(foot3, 0.3054F, 0.0F, 0.0436F);
				foot3.cubeList.add(new ModelBox(foot3, 0, 28, -1.0F, 0.0F, 0.0F, 2, 1, 2, 0.1F, true));
		
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(-0.25F, -0.25F, 0.1F);
				foot3.addChild(bone19);
				setRotationAngle(bone19, 0.2618F, 0.2618F, -0.0873F);
				bone19.cubeList.add(new ModelBox(bone19, 4, 1, -1.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, true));
		
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(-0.5F, 0.25F, -0.9F);
				bone19.addChild(bone20);
				setRotationAngle(bone20, 0.3491F, 0.0F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
		
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(-1.15F, -0.75F, 1.85F);
				foot3.addChild(bone21);
				setRotationAngle(bone21, 0.4363F, 1.8326F, -0.5236F);
				bone21.cubeList.add(new ModelBox(bone21, 4, 1, -1.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, true));
		
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(-0.5F, 0.25F, -0.9F);
				bone21.addChild(bone22);
				setRotationAngle(bone22, 0.4363F, 0.0F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
		
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.5F, -0.25F, 0.1F);
				foot3.addChild(bone23);
				setRotationAngle(bone23, 0.2618F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 4, 1, -1.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, true));
		
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(-0.5F, 0.25F, -0.9F);
				bone23.addChild(bone24);
				setRotationAngle(bone24, 0.3491F, 0.0F, 0.0F);
				bone24.cubeList.add(new ModelBox(bone24, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
		
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(1.25F, -0.15F, 0.35F);
				foot3.addChild(bone25);
				setRotationAngle(bone25, 0.2618F, -0.2618F, 0.0873F);
				bone25.cubeList.add(new ModelBox(bone25, 4, 1, -1.0F, 0.0F, -1.0F, 1, 1, 1, -0.1F, true));
		
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(-0.5F, 0.25F, -0.9F);
				bone25.addChild(bone26);
				setRotationAngle(bone26, 0.3491F, 0.0F, 0.0F);
				bone26.cubeList.add(new ModelBox(bone26, 0, 0, -0.5F, -0.25F, -0.75F, 1, 1, 1, -0.2F, true));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				if (((EntityCustom)entity).isOneHead()) {
					headRight.rotationPointX = 0.0F;
					headLeft.showModel = false;
				} else {
					headRight.rotationPointX = -2.0F;
					headRight.rotateAngleY += 0.2618F;
					headLeft.rotateAngleY -= 0.2618F;
					headLeft.showModel = true;
				}
				headRight.render(f5);
				headLeft.render(f5);
				body.render(f5);
				upperBody.render(f5);
				leg0.render(f5);
				leg1.render(f5);
				leg2.render(f5);
				leg3.render(f5);
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
				this.headRight.rotateAngleY = f3 / 180F * (float) Math.PI;
				this.headRight.rotateAngleX = f4 / 180F * (float) Math.PI;
				this.headLeft.rotateAngleY = f3 / 180F * (float) Math.PI;
				this.headLeft.rotateAngleX = f4 / 180F * (float) Math.PI;
				float f6 = MathHelper.cos(f * 1.0F) * f1;
				this.leg0.rotateAngleX = -f6;
				this.leg1.rotateAngleX = f6;
				this.leg2.rotateAngleX = f6;
				this.leg3.rotateAngleX = -f6;
				f6 = MathHelper.sin(f2 * 0.09F) * 0.1F + f6 * 0.6F;
				this.wingRight.rotateAngleY = 0.7854F - f6;
				this.wingLeft.rotateAngleY = -0.7854F + f6;
				this.tail.rotateAngleY = f2 * 0.2f;
				this.tail2.rotateAngleY = f2 * 0.1f;
				if (this.swingProgress > 0.0F) {
					jawRight.rotateAngleX = 1.0472F * (1.0F - this.swingProgress);
					jawLeft.rotateAngleX = 1.0472F * (1.0F - this.swingProgress);
				} else {
					jawRight.rotateAngleX = 0.0F;
					jawLeft.rotateAngleX = 0.0F;
				}
			}
		}
	}
}
