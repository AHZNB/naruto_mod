
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.block.material.Material;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import java.util.Random;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTenTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 179;
	public static final int ENTITYID_RANGED = 180;
	private static final float MODELSCALE = 48.0F;
	private static final TailBeastManager BIJU_MANAGER = new TailBeastManager();

	public EntityTenTails(ElementsNarutomodMod instance) {
		super(instance, 444);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		  .id(new ResourceLocation("narutomod", "ten_tails"), ENTITYID).name("ten_tails").tracker(128, 3, true)
		  .egg(-13421773, -16777216).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntitySplit.class)
		  .id(new ResourceLocation("narutomod", "ten_tails_split"), ENTITYID_RANGED).name("ten_tails_split").tracker(64, 3, true)
		  .egg(-13421773, -16777216).build());
	}

	public static TailBeastManager getBijuManager() {
		return BIJU_MANAGER;
	}

	public static class TailBeastManager extends EntityBijuManager<EntityCustom> {
		public TailBeastManager() {
			super(EntityCustom.class, 10);
		}

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}
	
	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_tentails"; 
		private static Save instance = null;

		public Save() {
			super(DATA_NAME);
		}

		public Save(String name) {
			super(name);
		}

	 	@Override
	 	public Save loadData() {
	 		instance = null;
	 		return this.getInstance();
	 	}

	 	@Override
	 	public void resetData() {
	 		super.resetData();
	 		instance = null;
	 	}

	 	public static Save getInstance() {
	 		if (instance == null) {
	 			MapStorage storage = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
	 			instance = (Save) storage.getOrLoadData(Save.class, DATA_NAME);
	 			if (instance == null) {
	 				instance = new Save(); 
	 				storage.setData(DATA_NAME, instance);
	 			} 
	 		} 
	 		return instance;
	 	}

	 	@Override
	 	protected EntityBijuManager getBijuManager() {
	 		return BIJU_MANAGER;
	 	}

	 	@Override
	 	protected EntityTailedBeast.Base createEntity(World world) {
	 		return new EntityCustom(world);
	 	}
	}

	public static class EntityCustom extends EntityTailedBeast.Base {
		private final List<EntitySplit> cloneList = Lists.newArrayList();
		private boolean canSeal;

		public EntityCustom(World world) {
			super(world);
			this.setSize(MODELSCALE * 0.2F, MODELSCALE * 0.72F);
			this.experienceValue = 16000;
			this.stepHeight = this.height / 3.0F;
			this.setAngerLevel(2);
			this.canBreakList.addAll(Lists.newArrayList(Material.IRON, Material.ANVIL, Material.TNT));
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.2F, MODELSCALE * 0.72F);
			this.experienceValue = 16000;
			this.stepHeight = this.height / 3.0F;
			this.setAngerLevel(2);
		}

		@Override
		public float getModelScale() {
			return MODELSCALE;
		}

		@Override
		public EntityBijuManager getBijuManager() {
			return BIJU_MANAGER;
		}

		@Override
		public void setFaceDown(boolean down) {
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.8D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100000.0D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1000.0D);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(40.0D);
		}

		@Override
		public double getBijudamaMinRange() {
			return 80.0d;
		}

		@Override
		public double getTargetRange() {
			return 128.0d;
		}

		@Override
		public SoundEvent getAmbientSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:MonsterGrowl"));
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
		protected float getSoundPitch() {
			return this.rand.nextFloat() * 0.4F + 0.5F;
		}

		protected SoundEvent getSpawnSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodspawn"));
		}

		protected float getSpawnSoundVolume() {
			return 10.0f;
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.mouthShootingJutsu != null && this.mouthShootingJutsu.isDead) {
				this.setSwingingArms(false);
				this.mouthShootingJutsu = null;
			}
			if (this.getRevengeTarget() != null && this.ticksExisted % 10 == 0 && this.cloneList.size() < 20) {
				EntitySplit clone = new EntitySplit(this);
				clone.setLocationAndAngles(this.posX, this.posY + this.rand.nextDouble() * 20.0d, this.posZ, this.rotationYaw, this.rotationPitch);
				Vec3d vec = Vec3d.fromPitchYaw(0.0f, this.rotationYaw).rotateYaw((this.rand.nextFloat()-0.5f) * 1.57f).scale(this.rand.nextDouble() * 0.6d);
				clone.motionX = vec.x;
				clone.motionY = vec.y;
				clone.motionZ = vec.z;
				this.world.spawnEntity(clone);
				this.cloneList.add(clone);
			}
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			if (!this.isAIDisabled() && (this.mouthShootingJutsu == null || this.mouthShootingJutsu.isDead)
			 && distanceFactor < 1.0f && distanceFactor > (float)(ProcedureUtils.getReachDistance(this) * 0.6d / this.getBijudamaMinRange())) {
				this.setSwingingArms(true);
				this.mouthShootingJutsu = EntityNineTails.EntityBeam.shoot(this, 2.0f, 1.4f);
			} else {
				super.attackEntityWithRangedAttack(target, distanceFactor);
			}
		}

		@Override
		public void onEntityUpdate() {
			super.onEntityUpdate();
			EntityPlayer jinchuriki = this.getBijuManager().getJinchurikiPlayer();
			if (!this.world.isRemote && jinchuriki != null && jinchuriki.equals(this.getControllingPassenger())
			 && !ItemRinnegan.wearingRinnesharingan(jinchuriki)) {
				this.setDead();
			}
			if (this.getAge() == 1 && this.getSpawnSound() != null) {
				this.playSound(this.getSpawnSound(), this.getSpawnSoundVolume(), this.rand.nextFloat() * 0.6f + 0.6f);
			}
			if (!this.world.isRemote && this.isFuuinInProgress()) {
				this.setTransparency(1.0f - this.getFuuinProgress());
				Entity entity = this.getTargetVessel();
				if (entity != null && entity.getDistance(this) > 10d) {
					ProcedureUtils.addVelocity(entity, this.getPositionVector().subtract(entity.getPositionVector()).scale(0.005d));
				}
			}
		}

		@Override
		public boolean canBeSealed() {
			return this.canSeal && !this.getBijuManager().isSealed();
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || entityIn instanceof EntitySplit;
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			if (!this.world.isRemote && entity instanceof EntityLivingBase && !this.isRidingSameEntity(entity) && !this.isOnSameTeam(entity))
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 5, false, false));
			super.collideWithEntity(entity);
		}

		@Override
		public void onAddedToWorld() {
			boolean flag = BIJU_MANAGER.getHasLived();
			super.onAddedToWorld();
			if (!this.world.isRemote && !flag) {
				BIJU_MANAGER.setHasLived(!this.spawnedBySpawner);
			}
		}

		@Override
		public Vec3d getPositionMouth() {
			return Vec3d.fromPitchYaw(this.rotationPitch, this.rotationYawHead)
			 .scale(0.625d * MODELSCALE).addVector(this.posX, this.posY + 0.5d * MODELSCALE, this.posZ);
		}

		@Override
		public Vec3d getPositionEyes(float partialTicks) {
			float pitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
			float headyaw = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
			return Vec3d.fromPitchYaw(pitch, headyaw).scale(MODELSCALE * 0.4f).add(super.getPositionEyes(partialTicks));
		}
	}

	public static class CoffinSealJutsu implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (ItemRinnegan.wearingRinnegan(entity) || ItemTenseigan.isWearing(entity)) {
				Entity entity1 = ProcedureUtils.objectEntityLookingAt(entity, 20d).entityHit;
				if (entity1 instanceof EntityCustom) {
					EntityCustom jubi = (EntityCustom)entity1;
					if (jubi.getHealth() >= jubi.getMaxHealth() * 0.1f) {
						jubi.setHealth(jubi.getMaxHealth() * 0.1f - 1000f);
					}
					jubi.canSeal = true;
					jubi.fuuinIntoVessel(entity, 300);
					jubi.setFaceDown(true);
					return true;
				}
			}
			return false;
		}
	}

	public static class EntitySplit extends EntityTailedBeast.Base {
		private static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(EntitySplit.class, DataSerializers.FLOAT);
		private static final BijuManager bijuManager = new BijuManager();
		private EntityCustom parent;
		
		public EntitySplit(World worldIn) {
			super(worldIn);
			this.setAngerLevel(1);
			this.enableBoss(false);
			this.setAlwaysRenderNameTag(false);
			if (!worldIn.isRemote) {
				float f = (float)this.rand.nextGaussian() * 0.6667f;
				this.setEntityScale(f * f + 0.8f);
			}
		}

		public EntitySplit(EntityCustom parentIn) {
			this(parentIn.world);
			this.parent = parentIn;
		}

		@Override
		public float getModelScale() {
			return this.getEntityScale();
		}

		@Override
		public EntityBijuManager getBijuManager() {
			return this.bijuManager;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SCALE, Float.valueOf(1.0F));
		}

		public float getEntityScale() {
			return ((Float)this.getDataManager().get(SCALE)).floatValue();
		}

		private void setEntityScale(float scale) {
			this.setSize((scale < 2.0f ? 0.66f : 1.1f) * scale, 2.625f * scale);
			if (!this.world.isRemote) {
				this.getDataManager().set(SCALE, Float.valueOf(scale));
			}
			this.fixEntityAttributes();
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (SCALE.equals(key) && this.world.isRemote) {
				this.setEntityScale(this.getEntityScale());
			}
		}

		@Override
		public void setFaceDown(boolean down) {
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.8D);
		}

		private void fixEntityAttributes() {
			float scale = this.getEntityScale();
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D * scale);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D * scale);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(2.5D * this.width);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(0, new AIParentHurtByTarget(this));
			this.tasks.addTask(3, new EntityClone.AIFollowSummoner(this, 0.8D, 0.5F * MODELSCALE) {
				@Override
				protected EntityLivingBase getFollowEntity() {
					return EntitySplit.this.parent;
				}
			});
		}

		@Override
		protected void updateAITasks() {
			if (this.getMeleeTime() <= 0) {
				this.setMeleeTime(80);
			}
			super.updateAITasks();
			if (this.getAttackTarget() != null && !this.isShooting()) {
				this.setSwingingArms(true);
			} else if (this.getAttackTarget() == null && this.isShooting()) {
				this.setSwingingArms(false);
			}
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			return ProcedureUtils.attackEntityAsMob(this, entityIn);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (this.parent != null && source.getTrueSource() == this.parent) {
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void setDead() {
			super.setDead();
			ProcedureUtils.poofWithSmoke(this);
			if (this.parent != null && this.parent.isEntityAlive()) {
				this.parent.cloneList.remove(this);
			}
		}

		@Override
		protected void onDeathUpdate() {
			if (!this.world.isRemote) {
				this.setDead();
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.getAge() == 1 && this.parent != null && this.parent.getSpawnSound() != null) {
				this.playSound(this.parent.getSpawnSound(), 2.0f, this.rand.nextFloat() * 0.6f + 0.6f);
			}
			if (!this.world.isRemote && (this.parent == null || !this.parent.isEntityAlive())) {
				this.setDead();
			}
		}

		@Override
		public SoundEvent getAmbientSound() {
			return null;
		}

		@Override
		public boolean couldBreakBlocks() {
			return this.getEntityScale() >= 4.0f && super.couldBreakBlocks();
		}

		@Override
		public boolean canBeSealed() {
			return false;
		}

		@Override
		public boolean isNonBoss() {
			return true;
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			if (entityIn instanceof EntitySplit) {
				return true;
			}
			if (this.parent != null) {
				return entityIn == this.parent || entityIn == this.parent.getSummoningPlayer() || entityIn == this.parent.getBijuManager().getJinchurikiPlayer();
			}
			return super.isOnSameTeam(entityIn);
		}

		class AIParentHurtByTarget extends EntityAITarget {
		    EntitySplit splitClone;
		    EntityLivingBase attacker;
		    private int timestamp;
		
		    public AIParentHurtByTarget(EntitySplit theDefendingTameableIn) {
		        super(theDefendingTameableIn, false);
		        this.splitClone = theDefendingTameableIn;
		        this.setMutexBits(1);
		    }
		
		    @Override
		    public boolean shouldExecute() {
		        if (this.splitClone.parent == null) {
		            return false;
		        } else {
		            EntityCustom parent = this.splitClone.parent;
	                this.attacker = parent.getRevengeTarget();
	                int i = parent.getRevengeTimer();
	                return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
		        }
		    }
		
		    @Override
		    public void startExecuting() {
		        this.splitClone.setAttackTarget(this.attacker);
	            EntityCustom parent = this.splitClone.parent;
		        if (parent != null) {
		            this.timestamp = parent.getRevengeTimer();
		        }
		        super.startExecuting();
		    }
		}

		static class BijuManager extends EntityBijuManager<EntitySplit> {
			public BijuManager() {
				super(EntitySplit.class, 11);
			}

			@Override
			public void onAddedToWorld(EntitySplit entityIn, boolean dirty) {
			}
	
			@Override
			public void markDirty() {
			}
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderTenTails(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntitySplit.class, renderManager -> new RenderSplit(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderTenTails extends EntityTailedBeast.ClientOnly.Renderer<EntityCustom> {
			//private final ModelTenTails tentailsModel = new ModelTenTails();
			//private final ModelTenTailsV1 tentailsModelV1 = new ModelTenTailsV1();
			private final ResourceLocation main_texture = new ResourceLocation("narutomod:textures/tentails.png");
			private final ResourceLocation v1_texture = new ResourceLocation("narutomod:textures/tentailsl1.png");
	
			public RenderTenTails(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelTenTailsV1(), MODELSCALE * 0.5F);
			}
	
		 	@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.v1_texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderSplit extends RenderLiving<EntitySplit> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/tentails_minion.png");

			public RenderSplit(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelTenTailsSplit(), 0.5F);
			}

			@Override
			public void doRender(EntitySplit entity, double x, double y, double z, float entityYaw, float partialTicks) {
				this.shadowSize = 0.5f * entity.getEntityScale();
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			@Override
			protected void renderModel(EntitySplit entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
				float scale = entity.getEntityScale();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * scale, 0.0F);
				if (scale < 2.0f) {
					GlStateManager.scale(scale * 0.6f, scale, scale * 0.6f);
					((ModelTenTailsSplit)this.mainModel).rightBlade.showModel = true;
					((ModelTenTailsSplit)this.mainModel).leftBlade.showModel = true;
					((ModelTenTailsSplit)this.mainModel).rightHammer.showModel = false;
					((ModelTenTailsSplit)this.mainModel).leftHammer.showModel = false;
				} else {
					GlStateManager.scale(scale, scale, scale);
					((ModelTenTailsSplit)this.mainModel).rightBlade.showModel = false;
					((ModelTenTailsSplit)this.mainModel).leftBlade.showModel = false;
					((ModelTenTailsSplit)this.mainModel).rightHammer.showModel = true;
					((ModelTenTailsSplit)this.mainModel).leftHammer.showModel = true;
				}
				super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
			}

		 	@Override
			protected ResourceLocation getEntityTexture(EntitySplit entity) {
				return this.texture;
			}
		}
	
		// Made with Blockbench 3.7.4
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelTenTailsV1 extends ModelBiped {
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer eyes;
			//private final ModelRenderer bipedBody;
			//private final ModelRenderer bipedHead;
			private final ModelRenderer upperTeeth;
			private final ModelRenderer jaw;
			private final ModelRenderer bone5;
			private final ModelRenderer bone22;
			private final ModelRenderer bone8;
			private final ModelRenderer lowerTeeth;
			private final ModelRenderer spike18;
			private final ModelRenderer bone4;
			private final ModelRenderer bone23;
			private final ModelRenderer bone83;
			private final ModelRenderer bone;
			private final ModelRenderer bone3;
			private final ModelRenderer bone10;
			private final ModelRenderer torso;
			private final ModelRenderer hump;
			private final ModelRenderer spike19;
			private final ModelRenderer bone89;
			private final ModelRenderer bone90;
			private final ModelRenderer bone91;
			private final ModelRenderer spike2;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer spike3;
			private final ModelRenderer bone38;
			private final ModelRenderer bone39;
			private final ModelRenderer bone40;
			private final ModelRenderer spike4;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
			private final ModelRenderer bone43;
			private final ModelRenderer bone11;
			private final ModelRenderer spike5;
			private final ModelRenderer bone44;
			private final ModelRenderer bone45;
			private final ModelRenderer bone46;
			private final ModelRenderer spike6;
			private final ModelRenderer bone47;
			private final ModelRenderer bone48;
			private final ModelRenderer bone49;
			private final ModelRenderer spike7;
			private final ModelRenderer bone50;
			private final ModelRenderer bone51;
			private final ModelRenderer bone52;
			private final ModelRenderer spike8;
			private final ModelRenderer bone53;
			private final ModelRenderer bone54;
			private final ModelRenderer bone55;
			private final ModelRenderer bone12;
			private final ModelRenderer spike9;
			private final ModelRenderer bone56;
			private final ModelRenderer bone57;
			private final ModelRenderer bone58;
			private final ModelRenderer spike10;
			private final ModelRenderer bone59;
			private final ModelRenderer bone60;
			private final ModelRenderer bone61;
			private final ModelRenderer spike11;
			private final ModelRenderer bone62;
			private final ModelRenderer bone63;
			private final ModelRenderer bone64;
			private final ModelRenderer spike12;
			private final ModelRenderer bone65;
			private final ModelRenderer bone66;
			private final ModelRenderer bone67;
			private final ModelRenderer bone13;
			private final ModelRenderer spike13;
			private final ModelRenderer bone68;
			private final ModelRenderer bone69;
			private final ModelRenderer bone70;
			private final ModelRenderer spike14;
			private final ModelRenderer bone71;
			private final ModelRenderer bone72;
			private final ModelRenderer bone73;
			private final ModelRenderer spike15;
			private final ModelRenderer bone74;
			private final ModelRenderer bone75;
			private final ModelRenderer bone76;
			private final ModelRenderer spike16;
			private final ModelRenderer bone77;
			private final ModelRenderer bone78;
			private final ModelRenderer bone79;
			private final ModelRenderer bone14;
			private final ModelRenderer bone16;
			private final ModelRenderer spike17;
			private final ModelRenderer bone80;
			private final ModelRenderer bone81;
			private final ModelRenderer bone82;
			private final ModelRenderer bone7;
			private final ModelRenderer bone9;
			private final ModelRenderer bone34;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer cube_r17;
			private final ModelRenderer cube_r16;
			private final ModelRenderer rightHand;
			private final ModelRenderer bone143;
			private final ModelRenderer bone144;
			private final ModelRenderer bone18;
			private final ModelRenderer bone19;
			private final ModelRenderer bone2;
			private final ModelRenderer bone6;
			private final ModelRenderer bone15;
			private final ModelRenderer bone17;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer leftHand;
			private final ModelRenderer bone24;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			private final ModelRenderer bone27;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer[][] Tail = new ModelRenderer[10][10];
			private final float tailSwayX[][] = new float[10][10];
			private final float tailSwayZ[][] = new float[10][10];
			private final Random rand = new Random();
		
			public ModelTenTailsV1() {
				textureWidth = 64;
				textureHeight = 64;
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 21.5F, 5.0F);
				
		
				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, -5.5F, -7.0F);
				bipedHeadwear.addChild(eyes);
				eyes.cubeList.add(new ModelBox(eyes, 0, 52, -6.0F, -8.0F, -10.55F, 12, 12, 0, -4.5F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 21.5F, 5.0F);
				
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -5.5F, -7.0F);
				bipedBody.addChild(bipedHead);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 45, 0, -1.5F, -3.5F, -6.0F, 3, 3, 6, 0.0F, false));
		
				upperTeeth = new ModelRenderer(this);
				upperTeeth.setRotationPoint(0.0F, -0.5F, -6.0F);
				bipedHead.addChild(upperTeeth);
				setRotationAngle(upperTeeth, 1.2217F, 0.0F, 0.0F);
				upperTeeth.cubeList.add(new ModelBox(upperTeeth, 39, 0, -1.5F, 0.0F, 0.0F, 3, 3, 3, 0.0F, false));
		
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(-2.5F, -0.5F, -0.5F);
				bipedHead.addChild(jaw);
				setRotationAngle(jaw, -0.3491F, 0.0F, 0.0F);
				
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-0.55F, 0.0F, 1.0F);
				jaw.addChild(bone5);
				setRotationAngle(bone5, 0.0F, -0.2618F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 50, 9, 0.0F, 0.0F, -6.0F, 1, 2, 6, 0.0F, false));
		
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(5.55F, 0.0F, 1.0F);
				jaw.addChild(bone22);
				setRotationAngle(bone22, 0.0F, 0.2618F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 50, 9, -1.0F, 0.0F, -6.0F, 1, 2, 6, 0.0F, true));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(2.5F, 2.0F, 1.0F);
				jaw.addChild(bone8);
				bone8.cubeList.add(new ModelBox(bone8, 46, 17, -1.5F, -2.0F, -6.0F, 3, 2, 6, 0.0F, false));
		
				lowerTeeth = new ModelRenderer(this);
				lowerTeeth.setRotationPoint(2.5F, 0.0F, -5.0F);
				jaw.addChild(lowerTeeth);
				setRotationAngle(lowerTeeth, -0.6981F, 0.0F, 0.0F);
				lowerTeeth.cubeList.add(new ModelBox(lowerTeeth, 44, 9, -1.5F, -1.75F, 0.0F, 3, 2, 3, 0.0F, false));
		
				spike18 = new ModelRenderer(this);
				spike18.setRotationPoint(2.5F, 1.7F, -4.25F);
				jaw.addChild(spike18);
				setRotationAngle(spike18, 2.7213F, -0.7831F, 0.3124F);
				spike18.cubeList.add(new ModelBox(spike18, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike18.addChild(bone4);
				setRotationAngle(bone4, -0.0873F, 0.0F, -0.0873F);
				bone4.cubeList.add(new ModelBox(bone4, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone4.addChild(bone23);
				setRotationAngle(bone23, -0.0873F, 0.0F, -0.0873F);
				bone23.cubeList.add(new ModelBox(bone23, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone83 = new ModelRenderer(this);
				bone83.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone23.addChild(bone83);
				setRotationAngle(bone83, -0.0873F, 0.0F, -0.0873F);
				bone83.cubeList.add(new ModelBox(bone83, 4, 19, -0.5F, -1.05F, -0.5F, 1, 1, 1, -0.4F, true));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-1.5F, -2.0F, -1.0F);
				bipedHead.addChild(bone);
				setRotationAngle(bone, 0.0F, -0.2618F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 46, 25, -1.4F, -1.5F, -4.75F, 2, 3, 6, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(1.4F, -2.0F, -1.0F);
				bipedHead.addChild(bone3);
				setRotationAngle(bone3, 0.0F, 0.2618F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 46, 25, -0.5F, -1.5F, -4.75F, 2, 3, 6, 0.0F, true));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(1.0F, -3.15F, 0.6F);
				bipedHead.addChild(bone10);
				setRotationAngle(bone10, -0.4363F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 24, 36, -3.5F, 0.134F, -0.5F, 5, 4, 3, 0.2F, false));
		
				torso = new ModelRenderer(this);
				torso.setRotationPoint(0.0F, -5.5F, -5.0F);
				bipedBody.addChild(torso);
				setRotationAngle(torso, -0.5236F, 0.0F, 0.0F);
				torso.cubeList.add(new ModelBox(torso, 0, 0, -3.5F, -2.5F, -3.0F, 7, 6, 7, 0.0F, false));
		
				hump = new ModelRenderer(this);
				hump.setRotationPoint(0.0F, -3.0F, 0.5F);
				torso.addChild(hump);
				setRotationAngle(hump, 0.0F, -0.7854F, 0.0F);
				hump.cubeList.add(new ModelBox(hump, 21, 0, -3.0F, -0.5F, -3.0F, 6, 1, 6, 0.0F, false));
		
				spike19 = new ModelRenderer(this);
				spike19.setRotationPoint(2.6F, 0.05F, -2.5F);
				hump.addChild(spike19);
				setRotationAngle(spike19, 1.5708F, -0.8727F, -0.8727F);
				spike19.cubeList.add(new ModelBox(spike19, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone89 = new ModelRenderer(this);
				bone89.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike19.addChild(bone89);
				setRotationAngle(bone89, -0.0873F, 0.0F, 0.0873F);
				bone89.cubeList.add(new ModelBox(bone89, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone90 = new ModelRenderer(this);
				bone90.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone89.addChild(bone90);
				setRotationAngle(bone90, -0.0873F, 0.0F, 0.0873F);
				bone90.cubeList.add(new ModelBox(bone90, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone91 = new ModelRenderer(this);
				bone91.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone90.addChild(bone91);
				setRotationAngle(bone91, -0.0873F, 0.0F, 0.0873F);
				bone91.cubeList.add(new ModelBox(bone91, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				spike2 = new ModelRenderer(this);
				spike2.setRotationPoint(-2.6F, 0.05F, -2.5F);
				hump.addChild(spike2);
				setRotationAngle(spike2, 1.5708F, 0.8727F, 0.8727F);
				spike2.cubeList.add(new ModelBox(spike2, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike2.addChild(bone35);
				setRotationAngle(bone35, -0.0873F, 0.0F, -0.0873F);
				bone35.cubeList.add(new ModelBox(bone35, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone35.addChild(bone36);
				setRotationAngle(bone36, -0.0873F, 0.0F, -0.0873F);
				bone36.cubeList.add(new ModelBox(bone36, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone36.addChild(bone37);
				setRotationAngle(bone37, -0.0873F, 0.0F, -0.0873F);
				bone37.cubeList.add(new ModelBox(bone37, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike3 = new ModelRenderer(this);
				spike3.setRotationPoint(-2.6F, 0.05F, 2.5F);
				hump.addChild(spike3);
				setRotationAngle(spike3, -1.5708F, -0.8727F, 0.8727F);
				spike3.cubeList.add(new ModelBox(spike3, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike3.addChild(bone38);
				setRotationAngle(bone38, 0.0873F, 0.0F, -0.0873F);
				bone38.cubeList.add(new ModelBox(bone38, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone38.addChild(bone39);
				setRotationAngle(bone39, 0.0873F, 0.0F, -0.0873F);
				bone39.cubeList.add(new ModelBox(bone39, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone39.addChild(bone40);
				setRotationAngle(bone40, 0.0873F, 0.0F, -0.0873F);
				bone40.cubeList.add(new ModelBox(bone40, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike4 = new ModelRenderer(this);
				spike4.setRotationPoint(2.6F, 0.05F, 2.5F);
				hump.addChild(spike4);
				setRotationAngle(spike4, -1.5708F, 0.8727F, -0.8727F);
				spike4.cubeList.add(new ModelBox(spike4, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike4.addChild(bone41);
				setRotationAngle(bone41, 0.0873F, 0.0F, 0.0873F);
				bone41.cubeList.add(new ModelBox(bone41, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone41.addChild(bone42);
				setRotationAngle(bone42, 0.0873F, 0.0F, 0.0873F);
				bone42.cubeList.add(new ModelBox(bone42, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone42.addChild(bone43);
				setRotationAngle(bone43, 0.0873F, 0.0F, 0.0873F);
				bone43.cubeList.add(new ModelBox(bone43, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, -1.0F, 0.0F);
				hump.addChild(bone11);
				setRotationAngle(bone11, 0.0F, -0.5236F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 15, 23, -2.5F, -0.5F, -2.5F, 5, 1, 5, 0.0F, false));
		
				spike5 = new ModelRenderer(this);
				spike5.setRotationPoint(2.1F, 0.05F, -2.0F);
				bone11.addChild(spike5);
				setRotationAngle(spike5, 1.5708F, -0.8727F, -0.8727F);
				spike5.cubeList.add(new ModelBox(spike5, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike5.addChild(bone44);
				setRotationAngle(bone44, -0.0873F, 0.0F, 0.0873F);
				bone44.cubeList.add(new ModelBox(bone44, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone44.addChild(bone45);
				setRotationAngle(bone45, -0.0873F, 0.0F, 0.0873F);
				bone45.cubeList.add(new ModelBox(bone45, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone45.addChild(bone46);
				setRotationAngle(bone46, -0.0873F, 0.0F, 0.0873F);
				bone46.cubeList.add(new ModelBox(bone46, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				spike6 = new ModelRenderer(this);
				spike6.setRotationPoint(-2.1F, 0.05F, -2.0F);
				bone11.addChild(spike6);
				setRotationAngle(spike6, 1.5708F, 0.8727F, 0.8727F);
				spike6.cubeList.add(new ModelBox(spike6, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike6.addChild(bone47);
				setRotationAngle(bone47, -0.0873F, 0.0F, -0.0873F);
				bone47.cubeList.add(new ModelBox(bone47, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone47.addChild(bone48);
				setRotationAngle(bone48, -0.0873F, 0.0F, -0.0873F);
				bone48.cubeList.add(new ModelBox(bone48, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone48.addChild(bone49);
				setRotationAngle(bone49, -0.0873F, 0.0F, -0.0873F);
				bone49.cubeList.add(new ModelBox(bone49, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike7 = new ModelRenderer(this);
				spike7.setRotationPoint(-2.1F, 0.05F, 2.0F);
				bone11.addChild(spike7);
				setRotationAngle(spike7, -1.5708F, -0.8727F, 0.8727F);
				spike7.cubeList.add(new ModelBox(spike7, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike7.addChild(bone50);
				setRotationAngle(bone50, 0.0873F, 0.0F, -0.0873F);
				bone50.cubeList.add(new ModelBox(bone50, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone50.addChild(bone51);
				setRotationAngle(bone51, 0.0873F, 0.0F, -0.0873F);
				bone51.cubeList.add(new ModelBox(bone51, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone52 = new ModelRenderer(this);
				bone52.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone51.addChild(bone52);
				setRotationAngle(bone52, 0.0873F, 0.0F, -0.0873F);
				bone52.cubeList.add(new ModelBox(bone52, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike8 = new ModelRenderer(this);
				spike8.setRotationPoint(2.1F, 0.05F, 2.0F);
				bone11.addChild(spike8);
				setRotationAngle(spike8, -1.5708F, 0.8727F, -0.8727F);
				spike8.cubeList.add(new ModelBox(spike8, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone53 = new ModelRenderer(this);
				bone53.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike8.addChild(bone53);
				setRotationAngle(bone53, 0.0873F, 0.0F, 0.0873F);
				bone53.cubeList.add(new ModelBox(bone53, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone54 = new ModelRenderer(this);
				bone54.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone53.addChild(bone54);
				setRotationAngle(bone54, 0.0873F, 0.0F, 0.0873F);
				bone54.cubeList.add(new ModelBox(bone54, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone55 = new ModelRenderer(this);
				bone55.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone54.addChild(bone55);
				setRotationAngle(bone55, 0.0873F, 0.0F, 0.0873F);
				bone55.cubeList.add(new ModelBox(bone55, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, 0.0F, -0.5236F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 28, 7, -2.0F, -0.5F, -2.0F, 4, 1, 4, 0.0F, false));
		
				spike9 = new ModelRenderer(this);
				spike9.setRotationPoint(1.6F, 0.05F, -1.5F);
				bone12.addChild(spike9);
				setRotationAngle(spike9, 1.5708F, -0.8727F, -0.8727F);
				spike9.cubeList.add(new ModelBox(spike9, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone56 = new ModelRenderer(this);
				bone56.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike9.addChild(bone56);
				setRotationAngle(bone56, -0.0873F, 0.0F, 0.0873F);
				bone56.cubeList.add(new ModelBox(bone56, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone57 = new ModelRenderer(this);
				bone57.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone56.addChild(bone57);
				setRotationAngle(bone57, -0.0873F, 0.0F, 0.0873F);
				bone57.cubeList.add(new ModelBox(bone57, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone58 = new ModelRenderer(this);
				bone58.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone57.addChild(bone58);
				setRotationAngle(bone58, -0.0873F, 0.0F, 0.0873F);
				bone58.cubeList.add(new ModelBox(bone58, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				spike10 = new ModelRenderer(this);
				spike10.setRotationPoint(-1.6F, 0.05F, -1.5F);
				bone12.addChild(spike10);
				setRotationAngle(spike10, 1.5708F, 0.8727F, 0.8727F);
				spike10.cubeList.add(new ModelBox(spike10, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone59 = new ModelRenderer(this);
				bone59.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike10.addChild(bone59);
				setRotationAngle(bone59, -0.0873F, 0.0F, -0.0873F);
				bone59.cubeList.add(new ModelBox(bone59, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone60 = new ModelRenderer(this);
				bone60.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone59.addChild(bone60);
				setRotationAngle(bone60, -0.0873F, 0.0F, -0.0873F);
				bone60.cubeList.add(new ModelBox(bone60, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone61 = new ModelRenderer(this);
				bone61.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone60.addChild(bone61);
				setRotationAngle(bone61, -0.0873F, 0.0F, -0.0873F);
				bone61.cubeList.add(new ModelBox(bone61, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike11 = new ModelRenderer(this);
				spike11.setRotationPoint(-1.6F, 0.05F, 1.5F);
				bone12.addChild(spike11);
				setRotationAngle(spike11, -1.5708F, -0.8727F, 0.8727F);
				spike11.cubeList.add(new ModelBox(spike11, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone62 = new ModelRenderer(this);
				bone62.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike11.addChild(bone62);
				setRotationAngle(bone62, 0.0873F, 0.0F, -0.0873F);
				bone62.cubeList.add(new ModelBox(bone62, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone63 = new ModelRenderer(this);
				bone63.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone62.addChild(bone63);
				setRotationAngle(bone63, 0.0873F, 0.0F, -0.0873F);
				bone63.cubeList.add(new ModelBox(bone63, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone64 = new ModelRenderer(this);
				bone64.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone63.addChild(bone64);
				setRotationAngle(bone64, 0.0873F, 0.0F, -0.0873F);
				bone64.cubeList.add(new ModelBox(bone64, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike12 = new ModelRenderer(this);
				spike12.setRotationPoint(1.6F, 0.05F, 1.5F);
				bone12.addChild(spike12);
				setRotationAngle(spike12, -1.5708F, 0.8727F, -0.8727F);
				spike12.cubeList.add(new ModelBox(spike12, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone65 = new ModelRenderer(this);
				bone65.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike12.addChild(bone65);
				setRotationAngle(bone65, 0.0873F, 0.0F, 0.0873F);
				bone65.cubeList.add(new ModelBox(bone65, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone66 = new ModelRenderer(this);
				bone66.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone65.addChild(bone66);
				setRotationAngle(bone66, 0.0873F, 0.0F, 0.0873F);
				bone66.cubeList.add(new ModelBox(bone66, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone67 = new ModelRenderer(this);
				bone67.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone66.addChild(bone67);
				setRotationAngle(bone67, 0.0873F, 0.0F, 0.0873F);
				bone67.cubeList.add(new ModelBox(bone67, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone12.addChild(bone13);
				setRotationAngle(bone13, 0.0F, -0.5236F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 12, 13, -1.5F, -0.5F, -1.5F, 3, 1, 3, 0.0F, false));
		
				spike13 = new ModelRenderer(this);
				spike13.setRotationPoint(0.85F, 0.3F, -0.75F);
				bone13.addChild(spike13);
				setRotationAngle(spike13, 1.5708F, -0.8727F, -0.8727F);
				spike13.cubeList.add(new ModelBox(spike13, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone68 = new ModelRenderer(this);
				bone68.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike13.addChild(bone68);
				setRotationAngle(bone68, -0.0873F, 0.0F, 0.0873F);
				bone68.cubeList.add(new ModelBox(bone68, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone69 = new ModelRenderer(this);
				bone69.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone68.addChild(bone69);
				setRotationAngle(bone69, -0.0873F, 0.0F, 0.0873F);
				bone69.cubeList.add(new ModelBox(bone69, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone70 = new ModelRenderer(this);
				bone70.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone69.addChild(bone70);
				setRotationAngle(bone70, -0.0873F, 0.0F, 0.0873F);
				bone70.cubeList.add(new ModelBox(bone70, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				spike14 = new ModelRenderer(this);
				spike14.setRotationPoint(-0.85F, 0.3F, -0.75F);
				bone13.addChild(spike14);
				setRotationAngle(spike14, 1.5708F, 0.8727F, 0.8727F);
				spike14.cubeList.add(new ModelBox(spike14, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone71 = new ModelRenderer(this);
				bone71.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike14.addChild(bone71);
				setRotationAngle(bone71, -0.0873F, 0.0F, -0.0873F);
				bone71.cubeList.add(new ModelBox(bone71, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone72 = new ModelRenderer(this);
				bone72.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone71.addChild(bone72);
				setRotationAngle(bone72, -0.0873F, 0.0F, -0.0873F);
				bone72.cubeList.add(new ModelBox(bone72, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone73 = new ModelRenderer(this);
				bone73.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone72.addChild(bone73);
				setRotationAngle(bone73, -0.0873F, 0.0F, -0.0873F);
				bone73.cubeList.add(new ModelBox(bone73, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike15 = new ModelRenderer(this);
				spike15.setRotationPoint(-0.85F, 0.3F, 0.75F);
				bone13.addChild(spike15);
				setRotationAngle(spike15, -1.5708F, -0.8727F, 0.8727F);
				spike15.cubeList.add(new ModelBox(spike15, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone74 = new ModelRenderer(this);
				bone74.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike15.addChild(bone74);
				setRotationAngle(bone74, 0.0873F, 0.0F, -0.0873F);
				bone74.cubeList.add(new ModelBox(bone74, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone75 = new ModelRenderer(this);
				bone75.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone74.addChild(bone75);
				setRotationAngle(bone75, 0.0873F, 0.0F, -0.0873F);
				bone75.cubeList.add(new ModelBox(bone75, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone76 = new ModelRenderer(this);
				bone76.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone75.addChild(bone76);
				setRotationAngle(bone76, 0.0873F, 0.0F, -0.0873F);
				bone76.cubeList.add(new ModelBox(bone76, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike16 = new ModelRenderer(this);
				spike16.setRotationPoint(0.85F, 0.3F, 0.75F);
				bone13.addChild(spike16);
				setRotationAngle(spike16, -1.5708F, 0.8727F, -0.8727F);
				spike16.cubeList.add(new ModelBox(spike16, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone77 = new ModelRenderer(this);
				bone77.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike16.addChild(bone77);
				setRotationAngle(bone77, 0.0873F, 0.0F, 0.0873F);
				bone77.cubeList.add(new ModelBox(bone77, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone78 = new ModelRenderer(this);
				bone78.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone77.addChild(bone78);
				setRotationAngle(bone78, 0.0873F, 0.0F, 0.0873F);
				bone78.cubeList.add(new ModelBox(bone78, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone79 = new ModelRenderer(this);
				bone79.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone78.addChild(bone79);
				setRotationAngle(bone79, 0.0873F, 0.0F, 0.0873F);
				bone79.cubeList.add(new ModelBox(bone79, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone13.addChild(bone14);
				setRotationAngle(bone14, 0.0F, -0.5236F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 12, 17, -1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone14.addChild(bone16);
				setRotationAngle(bone16, 0.0F, -0.5236F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 0, 19, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.1F, false));
		
				spike17 = new ModelRenderer(this);
				spike17.setRotationPoint(0.0F, 0.05F, 0.0F);
				bone16.addChild(spike17);
				setRotationAngle(spike17, 0.0F, -0.7854F, 0.0F);
				spike17.cubeList.add(new ModelBox(spike17, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone80 = new ModelRenderer(this);
				bone80.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike17.addChild(bone80);
				setRotationAngle(bone80, -0.0873F, 0.0F, 0.0873F);
				bone80.cubeList.add(new ModelBox(bone80, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone81 = new ModelRenderer(this);
				bone81.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone80.addChild(bone81);
				setRotationAngle(bone81, -0.0873F, 0.0F, 0.0873F);
				bone81.cubeList.add(new ModelBox(bone81, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone82 = new ModelRenderer(this);
				bone82.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone81.addChild(bone82);
				setRotationAngle(bone82, -0.0873F, 0.0F, 0.0873F);
				bone82.cubeList.add(new ModelBox(bone82, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, -2.5F, 4.0F);
				torso.addChild(bone7);
				setRotationAngle(bone7, -0.3491F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 20, 13, -3.5F, 0.0F, 0.0F, 7, 6, 4, 0.0F, false));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(-3.5F, 0.5F, -2.7F);
				torso.addChild(bone9);
				setRotationAngle(bone9, 0.0F, 0.2618F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 0, 13, -2.0F, -2.5F, 0.0F, 2, 5, 8, 0.0F, false));
		
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(3.5F, 0.5F, -2.7F);
				torso.addChild(bone34);
				setRotationAngle(bone34, 0.0F, -0.2618F, 0.0F);
				bone34.cubeList.add(new ModelBox(bone34, 0, 13, 0.0F, -2.5F, 0.0F, 2, 5, 8, 0.0F, true));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.5F, -5.0F, -4.5F);
				bipedBody.addChild(bipedRightArm);
				
		
				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(1.0F, -1.0F, -1.0F);
				bipedRightArm.addChild(cube_r17);
				setRotationAngle(cube_r17, 0.0F, 0.48F, 1.0472F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 0, 32, -1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F, false));
		
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(-1.5F, 6.0F, 0.0F);
				cube_r17.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.0F, 0.0F, -1.0472F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 12, 36, 0.0F, 0.0F, -1.5F, 3, 6, 3, 0.0F, false));
		
				rightHand = new ModelRenderer(this);
				rightHand.setRotationPoint(1.7F, 5.5F, 0.0F);
				cube_r16.addChild(rightHand);
				setRotationAngle(rightHand, 0.4363F, 0.0F, 0.0F);
				rightHand.cubeList.add(new ModelBox(rightHand, 36, 36, -1.6F, 0.0F, -3.0F, 3, 1, 4, 0.0F, false));
				rightHand.cubeList.add(new ModelBox(rightHand, 30, 23, -1.6F, 0.5F, -3.0F, 3, 1, 4, 0.0F, false));
		
				bone143 = new ModelRenderer(this);
				bone143.setRotationPoint(1.1F, 0.6F, -1.75F);
				rightHand.addChild(bone143);
				bone143.cubeList.add(new ModelBox(bone143, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone144 = new ModelRenderer(this);
				bone144.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone143.addChild(bone144);
				setRotationAngle(bone144, 0.5236F, 0.0F, 0.0F);
				bone144.cubeList.add(new ModelBox(bone144, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(1.1F, 0.6F, -0.75F);
				rightHand.addChild(bone18);
				setRotationAngle(bone18, 0.0F, -1.0472F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone18.addChild(bone19);
				setRotationAngle(bone19, 0.5236F, 0.0F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-0.05F, 0.6F, -1.75F);
				rightHand.addChild(bone2);
				bone2.cubeList.add(new ModelBox(bone2, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone2.addChild(bone6);
				setRotationAngle(bone6, 0.5236F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(-1.2F, 0.6F, -1.75F);
				rightHand.addChild(bone15);
				bone15.cubeList.add(new ModelBox(bone15, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone15.addChild(bone17);
				setRotationAngle(bone17, 0.5236F, 0.0F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(-1.2F, 0.6F, -1.0F);
				rightHand.addChild(bone20);
				setRotationAngle(bone20, 0.0F, 0.4363F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone20.addChild(bone21);
				setRotationAngle(bone21, 0.5236F, 0.0F, 0.0F);
				bone21.cubeList.add(new ModelBox(bone21, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.5F, -5.0F, -4.5F);
				bipedBody.addChild(bipedLeftArm);
				
		
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(-1.0F, -1.0F, -1.0F);
				bipedLeftArm.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.0F, -0.48F, -1.0472F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 32, -1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F, true));
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(1.5F, 6.0F, 0.0F);
				cube_r2.addChild(cube_r3);
				setRotationAngle(cube_r3, 0.0F, 0.0F, 1.0472F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 12, 36, -3.0F, 0.0F, -1.5F, 3, 6, 3, 0.0F, true));
		
				leftHand = new ModelRenderer(this);
				leftHand.setRotationPoint(-1.7F, 5.5F, 0.0F);
				cube_r3.addChild(leftHand);
				setRotationAngle(leftHand, 0.4363F, 0.0F, 0.0F);
				leftHand.cubeList.add(new ModelBox(leftHand, 36, 36, -1.4F, 0.0F, -3.0F, 3, 1, 4, 0.0F, true));
				leftHand.cubeList.add(new ModelBox(leftHand, 30, 23, -1.4F, 0.5F, -3.0F, 3, 1, 4, 0.0F, true));
		
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(-1.1F, 0.6F, -1.75F);
				leftHand.addChild(bone24);
				bone24.cubeList.add(new ModelBox(bone24, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone24.addChild(bone25);
				setRotationAngle(bone25, 0.5236F, 0.0F, 0.0F);
				bone25.cubeList.add(new ModelBox(bone25, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(-1.1F, 0.6F, -0.75F);
				leftHand.addChild(bone26);
				setRotationAngle(bone26, 0.0F, 1.0472F, 0.0F);
				bone26.cubeList.add(new ModelBox(bone26, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone26.addChild(bone27);
				setRotationAngle(bone27, 0.5236F, 0.0F, 0.0F);
				bone27.cubeList.add(new ModelBox(bone27, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.05F, 0.6F, -1.75F);
				leftHand.addChild(bone28);
				bone28.cubeList.add(new ModelBox(bone28, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone28.addChild(bone29);
				setRotationAngle(bone29, 0.5236F, 0.0F, 0.0F);
				bone29.cubeList.add(new ModelBox(bone29, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(1.2F, 0.6F, -1.75F);
				leftHand.addChild(bone30);
				bone30.cubeList.add(new ModelBox(bone30, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone30.addChild(bone31);
				setRotationAngle(bone31, 0.5236F, 0.0F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(1.2F, 0.6F, -1.0F);
				leftHand.addChild(bone32);
				setRotationAngle(bone32, 0.0F, -0.4363F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone32.addChild(bone33);
				setRotationAngle(bone33, 0.5236F, 0.0F, 0.0F);
				bone33.cubeList.add(new ModelBox(bone33, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				Tail[0][0] = new ModelRenderer(this);
				Tail[0][0].setRotationPoint(-0.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[0][0], -1.1345F, -0.2618F, 0.0F);
				Tail[0][0].cubeList.add(new ModelBox(Tail[0][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[0][1] = new ModelRenderer(this);
				Tail[0][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][0].addChild(Tail[0][1]);
				setRotationAngle(Tail[0][1], 0.1745F, 0.0F, 0.0F);
				Tail[0][1].cubeList.add(new ModelBox(Tail[0][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[0][2] = new ModelRenderer(this);
				Tail[0][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][1].addChild(Tail[0][2]);
				setRotationAngle(Tail[0][2], 0.1745F, 0.0F, 0.0F);
				Tail[0][2].cubeList.add(new ModelBox(Tail[0][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[0][3] = new ModelRenderer(this);
				Tail[0][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][2].addChild(Tail[0][3]);
				setRotationAngle(Tail[0][3], 0.1745F, 0.0F, 0.0F);
				Tail[0][3].cubeList.add(new ModelBox(Tail[0][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[0][4] = new ModelRenderer(this);
				Tail[0][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][3].addChild(Tail[0][4]);
				setRotationAngle(Tail[0][4], 0.1745F, 0.0F, 0.0F);
				Tail[0][4].cubeList.add(new ModelBox(Tail[0][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[0][5] = new ModelRenderer(this);
				Tail[0][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][4].addChild(Tail[0][5]);
				setRotationAngle(Tail[0][5], 0.1745F, 0.0F, 0.0F);
				Tail[0][5].cubeList.add(new ModelBox(Tail[0][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[0][6] = new ModelRenderer(this);
				Tail[0][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][5].addChild(Tail[0][6]);
				setRotationAngle(Tail[0][6], 0.1745F, 0.0F, 0.0F);
				Tail[0][6].cubeList.add(new ModelBox(Tail[0][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[0][7] = new ModelRenderer(this);
				Tail[0][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][6].addChild(Tail[0][7]);
				setRotationAngle(Tail[0][7], -0.1745F, 0.0F, 0.0F);
				Tail[0][7].cubeList.add(new ModelBox(Tail[0][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[0][8] = new ModelRenderer(this);
				Tail[0][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][7].addChild(Tail[0][8]);
				setRotationAngle(Tail[0][8], -0.1745F, 0.0F, 0.0F);
				Tail[0][8].cubeList.add(new ModelBox(Tail[0][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[0][9] = new ModelRenderer(this);
				Tail[0][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][8].addChild(Tail[0][9]);
				setRotationAngle(Tail[0][9], -0.1745F, 0.0F, 0.0F);
				Tail[0][9].cubeList.add(new ModelBox(Tail[0][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[1][0] = new ModelRenderer(this);
				Tail[1][0].setRotationPoint(0.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[1][0], -1.309F, 0.2618F, 0.0F);
				Tail[1][0].cubeList.add(new ModelBox(Tail[1][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[1][1] = new ModelRenderer(this);
				Tail[1][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][0].addChild(Tail[1][1]);
				setRotationAngle(Tail[1][1], 0.2618F, 0.0F, 0.0F);
				Tail[1][1].cubeList.add(new ModelBox(Tail[1][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[1][2] = new ModelRenderer(this);
				Tail[1][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][1].addChild(Tail[1][2]);
				setRotationAngle(Tail[1][2], 0.2618F, 0.0F, 0.0F);
				Tail[1][2].cubeList.add(new ModelBox(Tail[1][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[1][3] = new ModelRenderer(this);
				Tail[1][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][2].addChild(Tail[1][3]);
				setRotationAngle(Tail[1][3], 0.2618F, 0.0F, 0.0F);
				Tail[1][3].cubeList.add(new ModelBox(Tail[1][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[1][4] = new ModelRenderer(this);
				Tail[1][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][3].addChild(Tail[1][4]);
				setRotationAngle(Tail[1][4], 0.2618F, 0.0F, 0.0F);
				Tail[1][4].cubeList.add(new ModelBox(Tail[1][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[1][5] = new ModelRenderer(this);
				Tail[1][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][4].addChild(Tail[1][5]);
				setRotationAngle(Tail[1][5], 0.2618F, 0.0F, 0.0F);
				Tail[1][5].cubeList.add(new ModelBox(Tail[1][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[1][6] = new ModelRenderer(this);
				Tail[1][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][5].addChild(Tail[1][6]);
				setRotationAngle(Tail[1][6], 0.2618F, 0.0F, 0.0F);
				Tail[1][6].cubeList.add(new ModelBox(Tail[1][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[1][7] = new ModelRenderer(this);
				Tail[1][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][6].addChild(Tail[1][7]);
				setRotationAngle(Tail[1][7], 0.2618F, 0.0F, 0.0F);
				Tail[1][7].cubeList.add(new ModelBox(Tail[1][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[1][8] = new ModelRenderer(this);
				Tail[1][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][7].addChild(Tail[1][8]);
				setRotationAngle(Tail[1][8], -0.1745F, 0.0F, 0.0F);
				Tail[1][8].cubeList.add(new ModelBox(Tail[1][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[1][9] = new ModelRenderer(this);
				Tail[1][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][8].addChild(Tail[1][9]);
				setRotationAngle(Tail[1][9], -0.1745F, 0.0F, 0.0F);
				Tail[1][9].cubeList.add(new ModelBox(Tail[1][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[2][0] = new ModelRenderer(this);
				Tail[2][0].setRotationPoint(0.75F, 21.5F, 5.0F);
				setRotationAngle(Tail[2][0], -1.1345F, 0.7854F, 0.0F);
				Tail[2][0].cubeList.add(new ModelBox(Tail[2][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[2][1] = new ModelRenderer(this);
				Tail[2][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][0].addChild(Tail[2][1]);
				setRotationAngle(Tail[2][1], 0.2618F, 0.0F, 0.0F);
				Tail[2][1].cubeList.add(new ModelBox(Tail[2][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[2][2] = new ModelRenderer(this);
				Tail[2][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][1].addChild(Tail[2][2]);
				setRotationAngle(Tail[2][2], 0.2618F, 0.0F, 0.0F);
				Tail[2][2].cubeList.add(new ModelBox(Tail[2][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[2][3] = new ModelRenderer(this);
				Tail[2][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][2].addChild(Tail[2][3]);
				setRotationAngle(Tail[2][3], 0.2618F, 0.0F, 0.0F);
				Tail[2][3].cubeList.add(new ModelBox(Tail[2][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[2][4] = new ModelRenderer(this);
				Tail[2][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][3].addChild(Tail[2][4]);
				setRotationAngle(Tail[2][4], 0.2618F, 0.0F, 0.0F);
				Tail[2][4].cubeList.add(new ModelBox(Tail[2][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[2][5] = new ModelRenderer(this);
				Tail[2][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][4].addChild(Tail[2][5]);
				setRotationAngle(Tail[2][5], 0.2618F, 0.0F, 0.0F);
				Tail[2][5].cubeList.add(new ModelBox(Tail[2][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[2][6] = new ModelRenderer(this);
				Tail[2][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][5].addChild(Tail[2][6]);
				setRotationAngle(Tail[2][6], 0.2618F, 0.0F, 0.0F);
				Tail[2][6].cubeList.add(new ModelBox(Tail[2][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[2][7] = new ModelRenderer(this);
				Tail[2][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][6].addChild(Tail[2][7]);
				setRotationAngle(Tail[2][7], 0.2618F, 0.0F, 0.0F);
				Tail[2][7].cubeList.add(new ModelBox(Tail[2][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[2][8] = new ModelRenderer(this);
				Tail[2][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][7].addChild(Tail[2][8]);
				setRotationAngle(Tail[2][8], -0.1745F, 0.0F, 0.0F);
				Tail[2][8].cubeList.add(new ModelBox(Tail[2][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[2][9] = new ModelRenderer(this);
				Tail[2][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][8].addChild(Tail[2][9]);
				setRotationAngle(Tail[2][9], -0.1745F, 0.0F, 0.0F);
				Tail[2][9].cubeList.add(new ModelBox(Tail[2][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[3][0] = new ModelRenderer(this);
				Tail[3][0].setRotationPoint(1.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[3][0], -1.3963F, 1.0472F, 0.0F);
				Tail[3][0].cubeList.add(new ModelBox(Tail[3][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[3][1] = new ModelRenderer(this);
				Tail[3][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][0].addChild(Tail[3][1]);
				setRotationAngle(Tail[3][1], 0.2618F, 0.0F, 0.0F);
				Tail[3][1].cubeList.add(new ModelBox(Tail[3][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[3][2] = new ModelRenderer(this);
				Tail[3][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][1].addChild(Tail[3][2]);
				setRotationAngle(Tail[3][2], 0.2618F, 0.0F, 0.0F);
				Tail[3][2].cubeList.add(new ModelBox(Tail[3][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[3][3] = new ModelRenderer(this);
				Tail[3][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][2].addChild(Tail[3][3]);
				setRotationAngle(Tail[3][3], 0.2618F, 0.0F, 0.0F);
				Tail[3][3].cubeList.add(new ModelBox(Tail[3][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[3][4] = new ModelRenderer(this);
				Tail[3][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][3].addChild(Tail[3][4]);
				setRotationAngle(Tail[3][4], 0.2618F, 0.0F, 0.0F);
				Tail[3][4].cubeList.add(new ModelBox(Tail[3][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[3][5] = new ModelRenderer(this);
				Tail[3][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][4].addChild(Tail[3][5]);
				setRotationAngle(Tail[3][5], 0.2618F, 0.0F, 0.0F);
				Tail[3][5].cubeList.add(new ModelBox(Tail[3][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[3][6] = new ModelRenderer(this);
				Tail[3][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][5].addChild(Tail[3][6]);
				setRotationAngle(Tail[3][6], 0.2618F, 0.0F, 0.0F);
				Tail[3][6].cubeList.add(new ModelBox(Tail[3][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[3][7] = new ModelRenderer(this);
				Tail[3][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][6].addChild(Tail[3][7]);
				setRotationAngle(Tail[3][7], 0.2618F, 0.0F, 0.0F);
				Tail[3][7].cubeList.add(new ModelBox(Tail[3][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[3][8] = new ModelRenderer(this);
				Tail[3][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][7].addChild(Tail[3][8]);
				setRotationAngle(Tail[3][8], -0.1745F, 0.0F, 0.0F);
				Tail[3][8].cubeList.add(new ModelBox(Tail[3][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[3][9] = new ModelRenderer(this);
				Tail[3][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][8].addChild(Tail[3][9]);
				setRotationAngle(Tail[3][9], -0.1745F, 0.0F, 0.0F);
				Tail[3][9].cubeList.add(new ModelBox(Tail[3][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[4][0] = new ModelRenderer(this);
				Tail[4][0].setRotationPoint(1.75F, 21.5F, 5.0F);
				setRotationAngle(Tail[4][0], -1.6581F, 1.309F, 0.0F);
				Tail[4][0].cubeList.add(new ModelBox(Tail[4][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[4][1] = new ModelRenderer(this);
				Tail[4][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][0].addChild(Tail[4][1]);
				setRotationAngle(Tail[4][1], 0.2618F, 0.0F, 0.0F);
				Tail[4][1].cubeList.add(new ModelBox(Tail[4][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[4][2] = new ModelRenderer(this);
				Tail[4][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][1].addChild(Tail[4][2]);
				setRotationAngle(Tail[4][2], 0.2618F, 0.0F, 0.0F);
				Tail[4][2].cubeList.add(new ModelBox(Tail[4][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[4][3] = new ModelRenderer(this);
				Tail[4][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][2].addChild(Tail[4][3]);
				setRotationAngle(Tail[4][3], 0.2618F, 0.0F, 0.0F);
				Tail[4][3].cubeList.add(new ModelBox(Tail[4][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[4][4] = new ModelRenderer(this);
				Tail[4][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][3].addChild(Tail[4][4]);
				setRotationAngle(Tail[4][4], 0.2618F, 0.0F, 0.0F);
				Tail[4][4].cubeList.add(new ModelBox(Tail[4][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[4][5] = new ModelRenderer(this);
				Tail[4][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][4].addChild(Tail[4][5]);
				setRotationAngle(Tail[4][5], 0.2618F, 0.0F, 0.0F);
				Tail[4][5].cubeList.add(new ModelBox(Tail[4][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[4][6] = new ModelRenderer(this);
				Tail[4][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][5].addChild(Tail[4][6]);
				setRotationAngle(Tail[4][6], 0.2618F, 0.0F, 0.0F);
				Tail[4][6].cubeList.add(new ModelBox(Tail[4][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[4][7] = new ModelRenderer(this);
				Tail[4][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][6].addChild(Tail[4][7]);
				setRotationAngle(Tail[4][7], 0.2618F, 0.0F, 0.0F);
				Tail[4][7].cubeList.add(new ModelBox(Tail[4][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[4][8] = new ModelRenderer(this);
				Tail[4][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][7].addChild(Tail[4][8]);
				setRotationAngle(Tail[4][8], -0.1745F, 0.0F, 0.0F);
				Tail[4][8].cubeList.add(new ModelBox(Tail[4][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[4][9] = new ModelRenderer(this);
				Tail[4][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][8].addChild(Tail[4][9]);
				setRotationAngle(Tail[4][9], -0.1745F, 0.0F, 0.0F);
				Tail[4][9].cubeList.add(new ModelBox(Tail[4][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[5][0] = new ModelRenderer(this);
				Tail[5][0].setRotationPoint(2.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[5][0], -1.9199F, 1.5708F, 0.0F);
				Tail[5][0].cubeList.add(new ModelBox(Tail[5][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[5][1] = new ModelRenderer(this);
				Tail[5][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][0].addChild(Tail[5][1]);
				setRotationAngle(Tail[5][1], 0.2618F, 0.0F, 0.0F);
				Tail[5][1].cubeList.add(new ModelBox(Tail[5][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[5][2] = new ModelRenderer(this);
				Tail[5][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][1].addChild(Tail[5][2]);
				setRotationAngle(Tail[5][2], 0.2618F, 0.0F, 0.0F);
				Tail[5][2].cubeList.add(new ModelBox(Tail[5][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[5][3] = new ModelRenderer(this);
				Tail[5][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][2].addChild(Tail[5][3]);
				setRotationAngle(Tail[5][3], 0.2618F, 0.0F, 0.0F);
				Tail[5][3].cubeList.add(new ModelBox(Tail[5][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[5][4] = new ModelRenderer(this);
				Tail[5][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][3].addChild(Tail[5][4]);
				setRotationAngle(Tail[5][4], 0.2618F, 0.0F, 0.0F);
				Tail[5][4].cubeList.add(new ModelBox(Tail[5][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[5][5] = new ModelRenderer(this);
				Tail[5][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][4].addChild(Tail[5][5]);
				setRotationAngle(Tail[5][5], 0.2618F, 0.0F, 0.0F);
				Tail[5][5].cubeList.add(new ModelBox(Tail[5][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[5][6] = new ModelRenderer(this);
				Tail[5][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][5].addChild(Tail[5][6]);
				setRotationAngle(Tail[5][6], 0.2618F, 0.0F, 0.0F);
				Tail[5][6].cubeList.add(new ModelBox(Tail[5][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[5][7] = new ModelRenderer(this);
				Tail[5][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][6].addChild(Tail[5][7]);
				setRotationAngle(Tail[5][7], 0.2618F, 0.0F, 0.0F);
				Tail[5][7].cubeList.add(new ModelBox(Tail[5][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[5][8] = new ModelRenderer(this);
				Tail[5][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][7].addChild(Tail[5][8]);
				setRotationAngle(Tail[5][8], -0.1745F, 0.0F, 0.0F);
				Tail[5][8].cubeList.add(new ModelBox(Tail[5][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[5][9] = new ModelRenderer(this);
				Tail[5][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][8].addChild(Tail[5][9]);
				setRotationAngle(Tail[5][9], -0.1745F, 0.0F, 0.0F);
				Tail[5][9].cubeList.add(new ModelBox(Tail[5][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[6][0] = new ModelRenderer(this);
				Tail[6][0].setRotationPoint(-0.75F, 21.5F, 5.0F);
				setRotationAngle(Tail[6][0], -1.1345F, -0.7854F, 0.0F);
				Tail[6][0].cubeList.add(new ModelBox(Tail[6][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[6][1] = new ModelRenderer(this);
				Tail[6][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][0].addChild(Tail[6][1]);
				setRotationAngle(Tail[6][1], 0.2618F, 0.0F, 0.0F);
				Tail[6][1].cubeList.add(new ModelBox(Tail[6][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[6][2] = new ModelRenderer(this);
				Tail[6][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][1].addChild(Tail[6][2]);
				setRotationAngle(Tail[6][2], 0.2618F, 0.0F, 0.0F);
				Tail[6][2].cubeList.add(new ModelBox(Tail[6][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[6][3] = new ModelRenderer(this);
				Tail[6][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][2].addChild(Tail[6][3]);
				setRotationAngle(Tail[6][3], 0.2618F, 0.0F, 0.0F);
				Tail[6][3].cubeList.add(new ModelBox(Tail[6][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[6][4] = new ModelRenderer(this);
				Tail[6][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][3].addChild(Tail[6][4]);
				setRotationAngle(Tail[6][4], 0.2618F, 0.0F, 0.0F);
				Tail[6][4].cubeList.add(new ModelBox(Tail[6][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[6][5] = new ModelRenderer(this);
				Tail[6][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][4].addChild(Tail[6][5]);
				setRotationAngle(Tail[6][5], 0.2618F, 0.0F, 0.0F);
				Tail[6][5].cubeList.add(new ModelBox(Tail[6][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[6][6] = new ModelRenderer(this);
				Tail[6][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][5].addChild(Tail[6][6]);
				setRotationAngle(Tail[6][6], 0.2618F, 0.0F, 0.0F);
				Tail[6][6].cubeList.add(new ModelBox(Tail[6][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[6][7] = new ModelRenderer(this);
				Tail[6][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][6].addChild(Tail[6][7]);
				setRotationAngle(Tail[6][7], 0.2618F, 0.0F, 0.0F);
				Tail[6][7].cubeList.add(new ModelBox(Tail[6][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[6][8] = new ModelRenderer(this);
				Tail[6][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][7].addChild(Tail[6][8]);
				setRotationAngle(Tail[6][8], -0.1745F, 0.0F, 0.0F);
				Tail[6][8].cubeList.add(new ModelBox(Tail[6][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[6][9] = new ModelRenderer(this);
				Tail[6][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][8].addChild(Tail[6][9]);
				setRotationAngle(Tail[6][9], -0.1745F, 0.0F, 0.0F);
				Tail[6][9].cubeList.add(new ModelBox(Tail[6][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[7][0] = new ModelRenderer(this);
				Tail[7][0].setRotationPoint(-1.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[7][0], -1.3963F, -1.0472F, 0.0F);
				Tail[7][0].cubeList.add(new ModelBox(Tail[7][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[7][1] = new ModelRenderer(this);
				Tail[7][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][0].addChild(Tail[7][1]);
				setRotationAngle(Tail[7][1], 0.2618F, 0.0F, 0.0F);
				Tail[7][1].cubeList.add(new ModelBox(Tail[7][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[7][2] = new ModelRenderer(this);
				Tail[7][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][1].addChild(Tail[7][2]);
				setRotationAngle(Tail[7][2], 0.2618F, 0.0F, 0.0F);
				Tail[7][2].cubeList.add(new ModelBox(Tail[7][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[7][3] = new ModelRenderer(this);
				Tail[7][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][2].addChild(Tail[7][3]);
				setRotationAngle(Tail[7][3], 0.2618F, 0.0F, 0.0F);
				Tail[7][3].cubeList.add(new ModelBox(Tail[7][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[7][4] = new ModelRenderer(this);
				Tail[7][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][3].addChild(Tail[7][4]);
				setRotationAngle(Tail[7][4], 0.2618F, 0.0F, 0.0F);
				Tail[7][4].cubeList.add(new ModelBox(Tail[7][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[7][5] = new ModelRenderer(this);
				Tail[7][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][4].addChild(Tail[7][5]);
				setRotationAngle(Tail[7][5], 0.2618F, 0.0F, 0.0F);
				Tail[7][5].cubeList.add(new ModelBox(Tail[7][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[7][6] = new ModelRenderer(this);
				Tail[7][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][5].addChild(Tail[7][6]);
				setRotationAngle(Tail[7][6], 0.2618F, 0.0F, 0.0F);
				Tail[7][6].cubeList.add(new ModelBox(Tail[7][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[7][7] = new ModelRenderer(this);
				Tail[7][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][6].addChild(Tail[7][7]);
				setRotationAngle(Tail[7][7], 0.2618F, 0.0F, 0.0F);
				Tail[7][7].cubeList.add(new ModelBox(Tail[7][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[7][8] = new ModelRenderer(this);
				Tail[7][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][7].addChild(Tail[7][8]);
				setRotationAngle(Tail[7][8], -0.1745F, 0.0F, 0.0F);
				Tail[7][8].cubeList.add(new ModelBox(Tail[7][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[7][9] = new ModelRenderer(this);
				Tail[7][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][8].addChild(Tail[7][9]);
				setRotationAngle(Tail[7][9], -0.1745F, 0.0F, 0.0F);
				Tail[7][9].cubeList.add(new ModelBox(Tail[7][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[8][0] = new ModelRenderer(this);
				Tail[8][0].setRotationPoint(-1.75F, 21.5F, 5.0F);
				setRotationAngle(Tail[8][0], -1.6581F, -1.309F, 0.0F);
				Tail[8][0].cubeList.add(new ModelBox(Tail[8][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[8][1] = new ModelRenderer(this);
				Tail[8][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][0].addChild(Tail[8][1]);
				setRotationAngle(Tail[8][1], 0.2618F, 0.0F, 0.0F);
				Tail[8][1].cubeList.add(new ModelBox(Tail[8][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[8][2] = new ModelRenderer(this);
				Tail[8][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][1].addChild(Tail[8][2]);
				setRotationAngle(Tail[8][2], 0.2618F, 0.0F, 0.0F);
				Tail[8][2].cubeList.add(new ModelBox(Tail[8][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[8][3] = new ModelRenderer(this);
				Tail[8][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][2].addChild(Tail[8][3]);
				setRotationAngle(Tail[8][3], 0.2618F, 0.0F, 0.0F);
				Tail[8][3].cubeList.add(new ModelBox(Tail[8][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[8][4] = new ModelRenderer(this);
				Tail[8][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][3].addChild(Tail[8][4]);
				setRotationAngle(Tail[8][4], 0.2618F, 0.0F, 0.0F);
				Tail[8][4].cubeList.add(new ModelBox(Tail[8][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[8][5] = new ModelRenderer(this);
				Tail[8][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][4].addChild(Tail[8][5]);
				setRotationAngle(Tail[8][5], 0.2618F, 0.0F, 0.0F);
				Tail[8][5].cubeList.add(new ModelBox(Tail[8][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[8][6] = new ModelRenderer(this);
				Tail[8][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][5].addChild(Tail[8][6]);
				setRotationAngle(Tail[8][6], 0.2618F, 0.0F, 0.0F);
				Tail[8][6].cubeList.add(new ModelBox(Tail[8][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[8][7] = new ModelRenderer(this);
				Tail[8][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][6].addChild(Tail[8][7]);
				setRotationAngle(Tail[8][7], 0.2618F, 0.0F, 0.0F);
				Tail[8][7].cubeList.add(new ModelBox(Tail[8][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[8][8] = new ModelRenderer(this);
				Tail[8][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][7].addChild(Tail[8][8]);
				setRotationAngle(Tail[8][8], -0.1745F, 0.0F, 0.0F);
				Tail[8][8].cubeList.add(new ModelBox(Tail[8][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[8][9] = new ModelRenderer(this);
				Tail[8][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][8].addChild(Tail[8][9]);
				setRotationAngle(Tail[8][9], -0.1745F, 0.0F, 0.0F);
				Tail[8][9].cubeList.add(new ModelBox(Tail[8][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[9][0] = new ModelRenderer(this);
				Tail[9][0].setRotationPoint(-2.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[9][0], -1.9199F, -1.5708F, 0.0F);
				Tail[9][0].cubeList.add(new ModelBox(Tail[9][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[9][1] = new ModelRenderer(this);
				Tail[9][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][0].addChild(Tail[9][1]);
				setRotationAngle(Tail[9][1], 0.2618F, 0.0F, 0.0F);
				Tail[9][1].cubeList.add(new ModelBox(Tail[9][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[9][2] = new ModelRenderer(this);
				Tail[9][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][1].addChild(Tail[9][2]);
				setRotationAngle(Tail[9][2], 0.2618F, 0.0F, 0.0F);
				Tail[9][2].cubeList.add(new ModelBox(Tail[9][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[9][3] = new ModelRenderer(this);
				Tail[9][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][2].addChild(Tail[9][3]);
				setRotationAngle(Tail[9][3], 0.2618F, 0.0F, 0.0F);
				Tail[9][3].cubeList.add(new ModelBox(Tail[9][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[9][4] = new ModelRenderer(this);
				Tail[9][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][3].addChild(Tail[9][4]);
				setRotationAngle(Tail[9][4], 0.2618F, 0.0F, 0.0F);
				Tail[9][4].cubeList.add(new ModelBox(Tail[9][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[9][5] = new ModelRenderer(this);
				Tail[9][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][4].addChild(Tail[9][5]);
				setRotationAngle(Tail[9][5], 0.2618F, 0.0F, 0.0F);
				Tail[9][5].cubeList.add(new ModelBox(Tail[9][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[9][6] = new ModelRenderer(this);
				Tail[9][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][5].addChild(Tail[9][6]);
				setRotationAngle(Tail[9][6], 0.2618F, 0.0F, 0.0F);
				Tail[9][6].cubeList.add(new ModelBox(Tail[9][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[9][7] = new ModelRenderer(this);
				Tail[9][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][6].addChild(Tail[9][7]);
				setRotationAngle(Tail[9][7], 0.2618F, 0.0F, 0.0F);
				Tail[9][7].cubeList.add(new ModelBox(Tail[9][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[9][8] = new ModelRenderer(this);
				Tail[9][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][7].addChild(Tail[9][8]);
				setRotationAngle(Tail[9][8], -0.1745F, 0.0F, 0.0F);
				Tail[9][8].cubeList.add(new ModelBox(Tail[9][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[9][9] = new ModelRenderer(this);
				Tail[9][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][8].addChild(Tail[9][9]);
				setRotationAngle(Tail[9][9], -0.1745F, 0.0F, 0.0F);
				Tail[9][9].cubeList.add(new ModelBox(Tail[9][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
	
				for (int i = 0; i < 10; i++) {
					for (int j = 0; j < 10; j++) {
						tailSwayX[i][j] = (rand.nextFloat() * 0.1309F + 0.1309F) * (rand.nextBoolean() ? -1F : 1F);
						tailSwayZ[i][j] = (rand.nextFloat() * 0.1309F + 0.1309F) * (rand.nextBoolean() ? -1F : 1F);
					}
				}
			}
		
			@Override
			public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
				bipedLeftLeg.showModel = false;
				bipedRightLeg.showModel = false;
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F); //0.375F * MODELSCALE);
				GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
				//super.render(entity, f0, f1, f2, f3, f4, f5);
				bipedBody.render(f5);
				for (int i = 0; i < 10; i++) {
					Tail[i][0].render(f5);
				}
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				bipedHeadwear.render(f5);
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f0, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f0 * 2.0F / e.height, f1, f2, f3, f4, f5, e);
				bipedHead.rotationPointY += -5.5F;
				bipedRightArm.setRotationPoint(-5.5F, -5.0F, -4.5F);
				bipedLeftArm.setRotationPoint(5.5F, -5.0F, -4.5F);
				for (int i = 0; i < 10; i++) {
					for (int j = 1; j < 10; j++) {
						Tail[i][j].rotateAngleX = (j < 8 ? 0.2618F : -0.1745F) + MathHelper.sin((f2 - j) * 0.1F) * tailSwayX[i][j];
						Tail[i][j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.1F) * tailSwayZ[i][j];
					}
				}
				if (((EntityCustom)e).isShooting()) {
					bipedHead.rotateAngleX -= 0.3491F;
					jaw.rotateAngleX = 0.5236F;
				} else {
					jaw.rotateAngleX = -0.3491F;
				}
				this.copyModelAngles(bipedBody, bipedHeadwear);
				this.copyModelAngles(bipedHead, eyes);
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelTenTailsSplit extends ModelBiped {
			//private final ModelRenderer bipedHead;
			private final ModelRenderer head;
			private final ModelRenderer head_r1;
			private final ModelRenderer head_r2;
			private final ModelRenderer jaw;
			private final ModelRenderer head_r3;
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer horn1;
			private final ModelRenderer head_r4;
			private final ModelRenderer spike41;
			private final ModelRenderer body_r1;
			private final ModelRenderer body_r2;
			private final ModelRenderer body_r3;
			private final ModelRenderer body_r4;
			private final ModelRenderer spike42;
			private final ModelRenderer body_r5;
			private final ModelRenderer body_r6;
			private final ModelRenderer body_r7;
			private final ModelRenderer body_r8;
			private final ModelRenderer spike43;
			private final ModelRenderer body_r9;
			private final ModelRenderer body_r10;
			private final ModelRenderer body_r11;
			private final ModelRenderer body_r12;
			private final ModelRenderer body_r13;
			private final ModelRenderer horn2;
			private final ModelRenderer head_r5;
			private final ModelRenderer spike44;
			private final ModelRenderer body_r14;
			private final ModelRenderer body_r15;
			private final ModelRenderer body_r16;
			private final ModelRenderer body_r17;
			private final ModelRenderer spike45;
			private final ModelRenderer body_r18;
			private final ModelRenderer body_r19;
			private final ModelRenderer body_r20;
			private final ModelRenderer body_r21;
			private final ModelRenderer spike46;
			private final ModelRenderer body_r22;
			private final ModelRenderer body_r23;
			private final ModelRenderer body_r24;
			private final ModelRenderer body_r25;
			private final ModelRenderer body_r26;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer chest;
			private final ModelRenderer body_r27;
			private final ModelRenderer backSpikes;
			private final ModelRenderer spike3;
			private final ModelRenderer body_r28;
			private final ModelRenderer body_r29;
			private final ModelRenderer body_r30;
			private final ModelRenderer body_r31;
			private final ModelRenderer spike5;
			private final ModelRenderer body_r32;
			private final ModelRenderer body_r33;
			private final ModelRenderer body_r34;
			private final ModelRenderer body_r35;
			private final ModelRenderer spike7;
			private final ModelRenderer body_r36;
			private final ModelRenderer body_r37;
			private final ModelRenderer body_r38;
			private final ModelRenderer body_r39;
			private final ModelRenderer body_r40;
			private final ModelRenderer spike12;
			private final ModelRenderer body_r41;
			private final ModelRenderer body_r42;
			private final ModelRenderer body_r43;
			private final ModelRenderer body_r44;
			private final ModelRenderer spike13;
			private final ModelRenderer body_r45;
			private final ModelRenderer body_r46;
			private final ModelRenderer body_r47;
			private final ModelRenderer body_r48;
			private final ModelRenderer spike14;
			private final ModelRenderer body_r49;
			private final ModelRenderer body_r50;
			private final ModelRenderer body_r51;
			private final ModelRenderer body_r52;
			private final ModelRenderer body_r53;
			private final ModelRenderer spike27;
			private final ModelRenderer body_r54;
			private final ModelRenderer body_r55;
			private final ModelRenderer body_r56;
			private final ModelRenderer body_r57;
			private final ModelRenderer spike28;
			private final ModelRenderer body_r58;
			private final ModelRenderer body_r59;
			private final ModelRenderer body_r60;
			private final ModelRenderer body_r61;
			private final ModelRenderer spike29;
			private final ModelRenderer body_r62;
			private final ModelRenderer body_r63;
			private final ModelRenderer body_r64;
			private final ModelRenderer body_r65;
			private final ModelRenderer body_r66;
			private final ModelRenderer spike30;
			private final ModelRenderer body_r67;
			private final ModelRenderer body_r68;
			private final ModelRenderer body_r69;
			private final ModelRenderer body_r70;
			private final ModelRenderer spike31;
			private final ModelRenderer body_r71;
			private final ModelRenderer body_r72;
			private final ModelRenderer body_r73;
			private final ModelRenderer body_r74;
			private final ModelRenderer spike32;
			private final ModelRenderer body_r75;
			private final ModelRenderer body_r76;
			private final ModelRenderer body_r77;
			private final ModelRenderer body_r78;
			private final ModelRenderer body_r79;
			private final ModelRenderer spike38;
			private final ModelRenderer body_r80;
			private final ModelRenderer body_r81;
			private final ModelRenderer body_r82;
			private final ModelRenderer body_r83;
			private final ModelRenderer spike39;
			private final ModelRenderer body_r84;
			private final ModelRenderer body_r85;
			private final ModelRenderer body_r86;
			private final ModelRenderer body_r87;
			private final ModelRenderer spike40;
			private final ModelRenderer body_r88;
			private final ModelRenderer body_r89;
			private final ModelRenderer body_r90;
			private final ModelRenderer body_r91;
			private final ModelRenderer body_r92;
			private final ModelRenderer spike18;
			private final ModelRenderer body_r93;
			private final ModelRenderer body_r94;
			private final ModelRenderer body_r95;
			private final ModelRenderer body_r96;
			private final ModelRenderer spike19;
			private final ModelRenderer body_r97;
			private final ModelRenderer body_r98;
			private final ModelRenderer body_r99;
			private final ModelRenderer body_r100;
			private final ModelRenderer spike20;
			private final ModelRenderer body_r101;
			private final ModelRenderer body_r102;
			private final ModelRenderer body_r103;
			private final ModelRenderer body_r104;
			private final ModelRenderer body_r105;
			private final ModelRenderer spike9;
			private final ModelRenderer body_r106;
			private final ModelRenderer body_r107;
			private final ModelRenderer body_r108;
			private final ModelRenderer body_r109;
			private final ModelRenderer spike10;
			private final ModelRenderer body_r110;
			private final ModelRenderer body_r111;
			private final ModelRenderer body_r112;
			private final ModelRenderer body_r113;
			private final ModelRenderer spike11;
			private final ModelRenderer body_r114;
			private final ModelRenderer body_r115;
			private final ModelRenderer body_r116;
			private final ModelRenderer body_r117;
			private final ModelRenderer body_r118;
			private final ModelRenderer spike21;
			private final ModelRenderer body_r119;
			private final ModelRenderer body_r120;
			private final ModelRenderer body_r121;
			private final ModelRenderer body_r122;
			private final ModelRenderer spike22;
			private final ModelRenderer body_r123;
			private final ModelRenderer body_r124;
			private final ModelRenderer body_r125;
			private final ModelRenderer body_r126;
			private final ModelRenderer spike23;
			private final ModelRenderer body_r127;
			private final ModelRenderer body_r128;
			private final ModelRenderer body_r129;
			private final ModelRenderer body_r130;
			private final ModelRenderer body_r131;
			private final ModelRenderer spike15;
			private final ModelRenderer body_r132;
			private final ModelRenderer body_r133;
			private final ModelRenderer body_r134;
			private final ModelRenderer body_r135;
			private final ModelRenderer spike16;
			private final ModelRenderer body_r136;
			private final ModelRenderer body_r137;
			private final ModelRenderer body_r138;
			private final ModelRenderer body_r139;
			private final ModelRenderer spike17;
			private final ModelRenderer body_r140;
			private final ModelRenderer body_r141;
			private final ModelRenderer body_r142;
			private final ModelRenderer body_r143;
			private final ModelRenderer body_r144;
			private final ModelRenderer spike24;
			private final ModelRenderer body_r145;
			private final ModelRenderer body_r146;
			private final ModelRenderer body_r147;
			private final ModelRenderer body_r148;
			private final ModelRenderer spike25;
			private final ModelRenderer body_r149;
			private final ModelRenderer body_r150;
			private final ModelRenderer body_r151;
			private final ModelRenderer body_r152;
			private final ModelRenderer spike26;
			private final ModelRenderer body_r153;
			private final ModelRenderer body_r154;
			private final ModelRenderer body_r155;
			private final ModelRenderer body_r156;
			private final ModelRenderer body_r157;
			private final ModelRenderer spike4;
			private final ModelRenderer body_r158;
			private final ModelRenderer body_r159;
			private final ModelRenderer body_r160;
			private final ModelRenderer body_r161;
			private final ModelRenderer spike6;
			private final ModelRenderer body_r162;
			private final ModelRenderer body_r163;
			private final ModelRenderer body_r164;
			private final ModelRenderer body_r165;
			private final ModelRenderer spike8;
			private final ModelRenderer body_r166;
			private final ModelRenderer body_r167;
			private final ModelRenderer body_r168;
			private final ModelRenderer body_r169;
			private final ModelRenderer body_r170;
			private final ModelRenderer waist;
			private final ModelRenderer body_r171;
			private final ModelRenderer body_r172;
			private final ModelRenderer body_r173;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer rightArm;
			private final ModelRenderer spike33;
			private final ModelRenderer body_r174;
			private final ModelRenderer body_r175;
			private final ModelRenderer body_r176;
			private final ModelRenderer body_r177;
			private final ModelRenderer spike36;
			private final ModelRenderer body_r178;
			private final ModelRenderer body_r179;
			private final ModelRenderer body_r180;
			private final ModelRenderer body_r181;
			private final ModelRenderer spike37;
			private final ModelRenderer body_r182;
			private final ModelRenderer body_r183;
			private final ModelRenderer body_r184;
			private final ModelRenderer body_r185;
			private final ModelRenderer body_r186;
			private final ModelRenderer right4rm;
			private final ModelRenderer leftArm_r1;
			private final ModelRenderer rightBlade;
			private final ModelRenderer leftArm_r2;
			private final ModelRenderer leftArm_r3;
			private final ModelRenderer leftArm_r4;
			private final ModelRenderer leftArm_r5;
			private final ModelRenderer leftArm_r6;
			private final ModelRenderer leftArm_r7;
			private final ModelRenderer leftArm_r8;
			private final ModelRenderer leftArm_r9;
			private final ModelRenderer leftArm_r10;
			private final ModelRenderer leftArm_r11;
			private final ModelRenderer leftArm_r12;
			private final ModelRenderer leftArm_r13;
			private final ModelRenderer rightHammer;
			private final ModelRenderer leftArm_r14;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer leftArm;
			private final ModelRenderer spike2;
			private final ModelRenderer body_r187;
			private final ModelRenderer body_r188;
			private final ModelRenderer body_r189;
			private final ModelRenderer body_r190;
			private final ModelRenderer spike34;
			private final ModelRenderer body_r191;
			private final ModelRenderer body_r192;
			private final ModelRenderer body_r193;
			private final ModelRenderer body_r194;
			private final ModelRenderer spike35;
			private final ModelRenderer body_r195;
			private final ModelRenderer body_r196;
			private final ModelRenderer body_r197;
			private final ModelRenderer body_r198;
			private final ModelRenderer body_r199;
			private final ModelRenderer left4rm;
			private final ModelRenderer rightArm_r1;
			private final ModelRenderer leftBlade;
			private final ModelRenderer rightArm_r2;
			private final ModelRenderer rightArm_r3;
			private final ModelRenderer rightArm_r4;
			private final ModelRenderer rightArm_r5;
			private final ModelRenderer rightArm_r6;
			private final ModelRenderer rightArm_r7;
			private final ModelRenderer rightArm_r8;
			private final ModelRenderer rightArm_r9;
			private final ModelRenderer rightArm_r10;
			private final ModelRenderer rightArm_r11;
			private final ModelRenderer rightArm_r12;
			private final ModelRenderer rightArm_r13;
			private final ModelRenderer leftHammer;
			private final ModelRenderer rightArm_r14;
			//private final ModelRenderer bipedRightLeg;
			private final ModelRenderer rightThigh;
			private final ModelRenderer rightLeg;
			private final ModelRenderer rightLeg_r1;
			private final ModelRenderer rightLeg_r2;
			//private final ModelRenderer bipedLeftLeg;
			private final ModelRenderer leftThigh;
			private final ModelRenderer leftLeg;
			private final ModelRenderer leftLeg_r1;
			private final ModelRenderer leftLeg_r2;
		
			public ModelTenTailsSplit() {
				textureWidth = 128;
				textureHeight = 128;
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -6.8F, -5.0F);
				
		
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.addChild(head);
				setRotationAngle(head, 0.0873F, 0.0F, -0.0097F);
				
		
				head_r1 = new ModelRenderer(this);
				head_r1.setRotationPoint(-0.0097F, -2.1372F, -1.7749F);
				head.addChild(head_r1);
				setRotationAngle(head_r1, 0.5236F, 0.0F, 0.0F);
				head_r1.cubeList.add(new ModelBox(head_r1, 61, 66, -5.0F, -2.5F, -3.5F, 10, 5, 7, 0.125F, false));
		
				head_r2 = new ModelRenderer(this);
				head_r2.setRotationPoint(0.1903F, 30.0195F, -5.0822F);
				head.addChild(head_r2);
				setRotationAngle(head_r2, 0.0436F, 0.0F, 0.0F);
				head_r2.cubeList.add(new ModelBox(head_r2, 0, 110, -5.2F, -40.7F, -3.6F, 10, 8, 10, 0.0F, false));
		
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.1903F, -0.9805F, -5.0822F);
				head.addChild(jaw);
				setRotationAngle(jaw, 0.3054F, 0.0F, 0.0F);
				
		
				head_r3 = new ModelRenderer(this);
				head_r3.setRotationPoint(0.0F, 31.0F, 0.0F);
				jaw.addChild(head_r3);
				setRotationAngle(head_r3, 0.1309F, 0.0F, 0.0F);
				head_r3.cubeList.add(new ModelBox(head_r3, 72, 11, -5.2F, -32.8F, -0.6F, 10, 4, 5, 0.1F, false));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, -6.8F, -5.0F);
				
				horn1 = new ModelRenderer(this);
				horn1.setRotationPoint(2.9903F, -5.9805F, -9.0822F);
				bipedHeadwear.addChild(horn1);
				setRotationAngle(horn1, 0.7453F, -0.0347F, 0.127F);
				
		
				head_r4 = new ModelRenderer(this);
				head_r4.setRotationPoint(-0.2283F, -3.5493F, 0.388F);
				horn1.addChild(head_r4);
				setRotationAngle(head_r4, 0.0F, -0.7854F, 0.0F);
				head_r4.cubeList.add(new ModelBox(head_r4, 28, 61, -1.5F, -3.0F, -1.5F, 3, 6, 3, 0.0F, false));
		
				spike41 = new ModelRenderer(this);
				spike41.setRotationPoint(-0.0403F, -5.9195F, 0.5822F);
				horn1.addChild(spike41);
				setRotationAngle(spike41, -1.212F, 0.7922F, -2.8273F);
				
		
				body_r1 = new ModelRenderer(this);
				body_r1.setRotationPoint(0.0F, -1.093F, 0.7473F);
				spike41.addChild(body_r1);
				setRotationAngle(body_r1, -0.1302F, 0.1718F, -0.0528F);
				body_r1.cubeList.add(new ModelBox(body_r1, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r2 = new ModelRenderer(this);
				body_r2.setRotationPoint(0.5F, -1.093F, 0.7473F);
				spike41.addChild(body_r2);
				setRotationAngle(body_r2, -0.1294F, -0.1311F, -0.0134F);
				body_r2.cubeList.add(new ModelBox(body_r2, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r3 = new ModelRenderer(this);
				body_r3.setRotationPoint(-0.1F, -0.293F, 0.7473F);
				spike41.addChild(body_r3);
				setRotationAngle(body_r3, 0.2185F, 0.1665F, 0.0677F);
				body_r3.cubeList.add(new ModelBox(body_r3, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r4 = new ModelRenderer(this);
				body_r4.setRotationPoint(0.7F, -0.193F, 0.7473F);
				spike41.addChild(body_r4);
				setRotationAngle(body_r4, 0.2188F, -0.1744F, -0.0077F);
				body_r4.cubeList.add(new ModelBox(body_r4, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				spike42 = new ModelRenderer(this);
				spike42.setRotationPoint(0.2789F, -0.4551F, 2.6757F);
				spike41.addChild(spike42);
				setRotationAngle(spike42, 0.4136F, -0.0775F, -0.0884F);
				
		
				body_r5 = new ModelRenderer(this);
				body_r5.setRotationPoint(-0.4F, -0.9919F, -0.0215F);
				spike42.addChild(body_r5);
				setRotationAngle(body_r5, -0.1302F, 0.1718F, -0.0528F);
				body_r5.cubeList.add(new ModelBox(body_r5, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r6 = new ModelRenderer(this);
				body_r6.setRotationPoint(0.3F, -0.9919F, -0.0215F);
				spike42.addChild(body_r6);
				setRotationAngle(body_r6, -0.1302F, -0.1744F, -0.0077F);
				body_r6.cubeList.add(new ModelBox(body_r6, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r7 = new ModelRenderer(this);
				body_r7.setRotationPoint(-0.4F, -0.0919F, -0.0215F);
				spike42.addChild(body_r7);
				setRotationAngle(body_r7, 0.2185F, 0.1665F, 0.0677F);
				body_r7.cubeList.add(new ModelBox(body_r7, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r8 = new ModelRenderer(this);
				body_r8.setRotationPoint(0.3F, -0.0919F, -0.0215F);
				spike42.addChild(body_r8);
				setRotationAngle(body_r8, 0.2188F, -0.1744F, -0.0077F);
				body_r8.cubeList.add(new ModelBox(body_r8, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				spike43 = new ModelRenderer(this);
				spike43.setRotationPoint(-0.1F, 0.0F, 1.6F);
				spike42.addChild(spike43);
				setRotationAngle(spike43, 0.4102F, -0.095F, -0.0483F);
				
		
				body_r9 = new ModelRenderer(this);
				body_r9.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike43.addChild(body_r9);
				setRotationAngle(body_r9, -0.1327F, 0.2583F, -0.0645F);
				body_r9.cubeList.add(new ModelBox(body_r9, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r10 = new ModelRenderer(this);
				body_r10.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike43.addChild(body_r10);
				setRotationAngle(body_r10, -0.1314F, -0.2176F, -0.0019F);
				body_r10.cubeList.add(new ModelBox(body_r10, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r11 = new ModelRenderer(this);
				body_r11.setRotationPoint(0.0043F, -0.0543F, 0.6131F);
				spike43.addChild(body_r11);
				setRotationAngle(body_r11, 0.1809F, -0.1241F, 0.8093F);
				body_r11.cubeList.add(new ModelBox(body_r11, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, false));
		
				body_r12 = new ModelRenderer(this);
				body_r12.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike43.addChild(body_r12);
				setRotationAngle(body_r12, 0.2226F, 0.2517F, 0.0872F);
				body_r12.cubeList.add(new ModelBox(body_r12, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r13 = new ModelRenderer(this);
				body_r13.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike43.addChild(body_r13);
				setRotationAngle(body_r13, 0.2188F, -0.1744F, -0.0077F);
				body_r13.cubeList.add(new ModelBox(body_r13, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				horn2 = new ModelRenderer(this);
				horn2.setRotationPoint(-2.9903F, -5.9805F, -9.0822F);
				bipedHeadwear.addChild(horn2);
				setRotationAngle(horn2, 0.7453F, 0.0347F, -0.127F);
				
		
				head_r5 = new ModelRenderer(this);
				head_r5.setRotationPoint(0.2283F, -3.5493F, 0.388F);
				horn2.addChild(head_r5);
				setRotationAngle(head_r5, 0.0F, 0.7854F, 0.0F);
				head_r5.cubeList.add(new ModelBox(head_r5, 28, 61, -1.5F, -3.0F, -1.5F, 3, 6, 3, 0.0F, true));
		
				spike44 = new ModelRenderer(this);
				spike44.setRotationPoint(0.0403F, -5.9195F, 0.5822F);
				horn2.addChild(spike44);
				setRotationAngle(spike44, -1.212F, -0.7922F, 2.8273F);
				
		
				body_r14 = new ModelRenderer(this);
				body_r14.setRotationPoint(0.0F, -1.093F, 0.7473F);
				spike44.addChild(body_r14);
				setRotationAngle(body_r14, -0.1302F, -0.1718F, 0.0528F);
				body_r14.cubeList.add(new ModelBox(body_r14, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r15 = new ModelRenderer(this);
				body_r15.setRotationPoint(-0.5F, -1.093F, 0.7473F);
				spike44.addChild(body_r15);
				setRotationAngle(body_r15, -0.1294F, 0.1311F, 0.0134F);
				body_r15.cubeList.add(new ModelBox(body_r15, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r16 = new ModelRenderer(this);
				body_r16.setRotationPoint(0.1F, -0.293F, 0.7473F);
				spike44.addChild(body_r16);
				setRotationAngle(body_r16, 0.2185F, -0.1665F, -0.0677F);
				body_r16.cubeList.add(new ModelBox(body_r16, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r17 = new ModelRenderer(this);
				body_r17.setRotationPoint(-0.7F, -0.193F, 0.7473F);
				spike44.addChild(body_r17);
				setRotationAngle(body_r17, 0.2188F, 0.1744F, 0.0077F);
				body_r17.cubeList.add(new ModelBox(body_r17, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				spike45 = new ModelRenderer(this);
				spike45.setRotationPoint(-0.2789F, -0.4551F, 2.6757F);
				spike44.addChild(spike45);
				setRotationAngle(spike45, 0.4136F, 0.0775F, 0.0884F);
				
		
				body_r18 = new ModelRenderer(this);
				body_r18.setRotationPoint(0.4F, -0.9919F, -0.0215F);
				spike45.addChild(body_r18);
				setRotationAngle(body_r18, -0.1302F, -0.1718F, 0.0528F);
				body_r18.cubeList.add(new ModelBox(body_r18, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r19 = new ModelRenderer(this);
				body_r19.setRotationPoint(-0.3F, -0.9919F, -0.0215F);
				spike45.addChild(body_r19);
				setRotationAngle(body_r19, -0.1302F, 0.1744F, 0.0077F);
				body_r19.cubeList.add(new ModelBox(body_r19, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r20 = new ModelRenderer(this);
				body_r20.setRotationPoint(0.4F, -0.0919F, -0.0215F);
				spike45.addChild(body_r20);
				setRotationAngle(body_r20, 0.2185F, -0.1665F, -0.0677F);
				body_r20.cubeList.add(new ModelBox(body_r20, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r21 = new ModelRenderer(this);
				body_r21.setRotationPoint(-0.3F, -0.0919F, -0.0215F);
				spike45.addChild(body_r21);
				setRotationAngle(body_r21, 0.2188F, 0.1744F, 0.0077F);
				body_r21.cubeList.add(new ModelBox(body_r21, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				spike46 = new ModelRenderer(this);
				spike46.setRotationPoint(0.1F, 0.0F, 1.6F);
				spike45.addChild(spike46);
				setRotationAngle(spike46, 0.4102F, 0.095F, 0.0483F);
				
		
				body_r22 = new ModelRenderer(this);
				body_r22.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike46.addChild(body_r22);
				setRotationAngle(body_r22, -0.1327F, -0.2583F, 0.0645F);
				body_r22.cubeList.add(new ModelBox(body_r22, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r23 = new ModelRenderer(this);
				body_r23.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike46.addChild(body_r23);
				setRotationAngle(body_r23, -0.1314F, 0.2176F, 0.0019F);
				body_r23.cubeList.add(new ModelBox(body_r23, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r24 = new ModelRenderer(this);
				body_r24.setRotationPoint(-0.0043F, -0.0543F, 0.6131F);
				spike46.addChild(body_r24);
				setRotationAngle(body_r24, 0.1809F, 0.1241F, -0.8093F);
				body_r24.cubeList.add(new ModelBox(body_r24, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, true));
		
				body_r25 = new ModelRenderer(this);
				body_r25.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike46.addChild(body_r25);
				setRotationAngle(body_r25, 0.2226F, -0.2517F, -0.0872F);
				body_r25.cubeList.add(new ModelBox(body_r25, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r26 = new ModelRenderer(this);
				body_r26.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike46.addChild(body_r26);
				setRotationAngle(body_r26, 0.2188F, 0.1744F, 0.0077F);
				body_r26.cubeList.add(new ModelBox(body_r26, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, -6.8F, -4.0F);
				
		
				chest = new ModelRenderer(this);
				chest.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(chest);
				setRotationAngle(chest, 0.3054F, 0.0F, 0.0F);
				
		
				body_r27 = new ModelRenderer(this);
				body_r27.setRotationPoint(0.2F, 29.05F, 0.0F);
				chest.addChild(body_r27);
				setRotationAngle(body_r27, 0.0436F, 0.0F, 0.0F);
				body_r27.cubeList.add(new ModelBox(body_r27, 36, 13, -7.2F, -32.4F, -3.4F, 14, 8, 8, 0.0F, false));
		
				backSpikes = new ModelRenderer(this);
				backSpikes.setRotationPoint(0.2F, 28.8F, 0.0F);
				chest.addChild(backSpikes);
				
		
				spike3 = new ModelRenderer(this);
				spike3.setRotationPoint(5.25F, -31.0F, 2.5F);
				backSpikes.addChild(spike3);
				setRotationAngle(spike3, 0.471F, 0.347F, 0.2527F);
				
		
				body_r28 = new ModelRenderer(this);
				body_r28.setRotationPoint(-0.2F, -1.093F, 0.7473F);
				spike3.addChild(body_r28);
				setRotationAngle(body_r28, -0.1302F, 0.1718F, -0.0528F);
				body_r28.cubeList.add(new ModelBox(body_r28, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r29 = new ModelRenderer(this);
				body_r29.setRotationPoint(0.8F, -1.093F, 0.7473F);
				spike3.addChild(body_r29);
				setRotationAngle(body_r29, -0.1302F, -0.1744F, -0.0077F);
				body_r29.cubeList.add(new ModelBox(body_r29, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r30 = new ModelRenderer(this);
				body_r30.setRotationPoint(-0.2F, -0.093F, 0.7473F);
				spike3.addChild(body_r30);
				setRotationAngle(body_r30, 0.2185F, 0.1665F, 0.0677F);
				body_r30.cubeList.add(new ModelBox(body_r30, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r31 = new ModelRenderer(this);
				body_r31.setRotationPoint(0.8F, -0.093F, 0.7473F);
				spike3.addChild(body_r31);
				setRotationAngle(body_r31, 0.2188F, -0.1744F, -0.0077F);
				body_r31.cubeList.add(new ModelBox(body_r31, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				spike5 = new ModelRenderer(this);
				spike5.setRotationPoint(0.2789F, -0.4551F, 2.6757F);
				spike3.addChild(spike5);
				setRotationAngle(spike5, 0.4011F, -0.1294F, 0.0324F);
				
		
				body_r32 = new ModelRenderer(this);
				body_r32.setRotationPoint(-0.4F, -0.9919F, -0.0215F);
				spike5.addChild(body_r32);
				setRotationAngle(body_r32, -0.1302F, 0.1718F, -0.0528F);
				body_r32.cubeList.add(new ModelBox(body_r32, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r33 = new ModelRenderer(this);
				body_r33.setRotationPoint(0.3F, -0.9919F, -0.0215F);
				spike5.addChild(body_r33);
				setRotationAngle(body_r33, -0.1302F, -0.1744F, -0.0077F);
				body_r33.cubeList.add(new ModelBox(body_r33, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r34 = new ModelRenderer(this);
				body_r34.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike5.addChild(body_r34);
				setRotationAngle(body_r34, 0.2185F, 0.1665F, 0.0677F);
				body_r34.cubeList.add(new ModelBox(body_r34, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r35 = new ModelRenderer(this);
				body_r35.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike5.addChild(body_r35);
				setRotationAngle(body_r35, 0.2188F, -0.1744F, -0.0077F);
				body_r35.cubeList.add(new ModelBox(body_r35, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				spike7 = new ModelRenderer(this);
				spike7.setRotationPoint(-0.1F, 0.0F, 1.6F);
				spike5.addChild(spike7);
				setRotationAngle(spike7, 0.4102F, -0.095F, -0.0483F);
				
		
				body_r36 = new ModelRenderer(this);
				body_r36.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike7.addChild(body_r36);
				setRotationAngle(body_r36, -0.1327F, 0.2583F, -0.0645F);
				body_r36.cubeList.add(new ModelBox(body_r36, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r37 = new ModelRenderer(this);
				body_r37.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike7.addChild(body_r37);
				setRotationAngle(body_r37, -0.1314F, -0.2176F, -0.0019F);
				body_r37.cubeList.add(new ModelBox(body_r37, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r38 = new ModelRenderer(this);
				body_r38.setRotationPoint(0.0043F, -0.0543F, 0.6131F);
				spike7.addChild(body_r38);
				setRotationAngle(body_r38, 0.1809F, -0.1241F, 0.8093F);
				body_r38.cubeList.add(new ModelBox(body_r38, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, false));
		
				body_r39 = new ModelRenderer(this);
				body_r39.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike7.addChild(body_r39);
				setRotationAngle(body_r39, 0.2226F, 0.2517F, 0.0872F);
				body_r39.cubeList.add(new ModelBox(body_r39, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r40 = new ModelRenderer(this);
				body_r40.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike7.addChild(body_r40);
				setRotationAngle(body_r40, 0.2188F, -0.1744F, -0.0077F);
				body_r40.cubeList.add(new ModelBox(body_r40, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				spike12 = new ModelRenderer(this);
				spike12.setRotationPoint(2.25F, -32.0F, 2.5F);
				backSpikes.addChild(spike12);
				setRotationAngle(spike12, 0.7057F, 0.1119F, 0.1343F);
				
		
				body_r41 = new ModelRenderer(this);
				body_r41.setRotationPoint(-0.2F, -1.093F, 0.7473F);
				spike12.addChild(body_r41);
				setRotationAngle(body_r41, -0.1302F, 0.1718F, -0.0528F);
				body_r41.cubeList.add(new ModelBox(body_r41, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r42 = new ModelRenderer(this);
				body_r42.setRotationPoint(0.8F, -1.093F, 0.7473F);
				spike12.addChild(body_r42);
				setRotationAngle(body_r42, -0.1302F, -0.1744F, -0.0077F);
				body_r42.cubeList.add(new ModelBox(body_r42, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r43 = new ModelRenderer(this);
				body_r43.setRotationPoint(-0.2F, -0.093F, 0.7473F);
				spike12.addChild(body_r43);
				setRotationAngle(body_r43, 0.2185F, 0.1665F, 0.0677F);
				body_r43.cubeList.add(new ModelBox(body_r43, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r44 = new ModelRenderer(this);
				body_r44.setRotationPoint(0.8F, -0.093F, 0.7473F);
				spike12.addChild(body_r44);
				setRotationAngle(body_r44, 0.2188F, -0.1744F, -0.0077F);
				body_r44.cubeList.add(new ModelBox(body_r44, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				spike13 = new ModelRenderer(this);
				spike13.setRotationPoint(0.2789F, -0.4551F, 2.6757F);
				spike12.addChild(spike13);
				setRotationAngle(spike13, 0.4011F, -0.1294F, 0.0324F);
				
		
				body_r45 = new ModelRenderer(this);
				body_r45.setRotationPoint(-0.4F, -0.9919F, -0.0215F);
				spike13.addChild(body_r45);
				setRotationAngle(body_r45, -0.1302F, 0.1718F, -0.0528F);
				body_r45.cubeList.add(new ModelBox(body_r45, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r46 = new ModelRenderer(this);
				body_r46.setRotationPoint(0.3F, -0.9919F, -0.0215F);
				spike13.addChild(body_r46);
				setRotationAngle(body_r46, -0.1302F, -0.1744F, -0.0077F);
				body_r46.cubeList.add(new ModelBox(body_r46, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r47 = new ModelRenderer(this);
				body_r47.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike13.addChild(body_r47);
				setRotationAngle(body_r47, 0.2185F, 0.1665F, 0.0677F);
				body_r47.cubeList.add(new ModelBox(body_r47, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r48 = new ModelRenderer(this);
				body_r48.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike13.addChild(body_r48);
				setRotationAngle(body_r48, 0.2188F, -0.1744F, -0.0077F);
				body_r48.cubeList.add(new ModelBox(body_r48, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				spike14 = new ModelRenderer(this);
				spike14.setRotationPoint(-0.1F, 0.0F, 1.6F);
				spike13.addChild(spike14);
				setRotationAngle(spike14, 0.4102F, -0.095F, -0.0483F);
				
		
				body_r49 = new ModelRenderer(this);
				body_r49.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike14.addChild(body_r49);
				setRotationAngle(body_r49, -0.1327F, 0.2583F, -0.0645F);
				body_r49.cubeList.add(new ModelBox(body_r49, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r50 = new ModelRenderer(this);
				body_r50.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike14.addChild(body_r50);
				setRotationAngle(body_r50, -0.1314F, -0.2176F, -0.0019F);
				body_r50.cubeList.add(new ModelBox(body_r50, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r51 = new ModelRenderer(this);
				body_r51.setRotationPoint(0.0043F, -0.0543F, 0.6131F);
				spike14.addChild(body_r51);
				setRotationAngle(body_r51, 0.1809F, -0.1241F, 0.8093F);
				body_r51.cubeList.add(new ModelBox(body_r51, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, false));
		
				body_r52 = new ModelRenderer(this);
				body_r52.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike14.addChild(body_r52);
				setRotationAngle(body_r52, 0.2226F, 0.2517F, 0.0872F);
				body_r52.cubeList.add(new ModelBox(body_r52, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r53 = new ModelRenderer(this);
				body_r53.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike14.addChild(body_r53);
				setRotationAngle(body_r53, 0.2188F, -0.1744F, -0.0077F);
				body_r53.cubeList.add(new ModelBox(body_r53, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				spike27 = new ModelRenderer(this);
				spike27.setRotationPoint(-0.5F, -28.5F, 3.0F);
				backSpikes.addChild(spike27);
				setRotationAngle(spike27, 0.3077F, 0.0119F, 0.0495F);
				
		
				body_r54 = new ModelRenderer(this);
				body_r54.setRotationPoint(-0.2F, -1.093F, 0.7473F);
				spike27.addChild(body_r54);
				setRotationAngle(body_r54, -0.1302F, 0.1718F, -0.0528F);
				body_r54.cubeList.add(new ModelBox(body_r54, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r55 = new ModelRenderer(this);
				body_r55.setRotationPoint(0.8F, -1.093F, 0.7473F);
				spike27.addChild(body_r55);
				setRotationAngle(body_r55, -0.1302F, -0.1744F, -0.0077F);
				body_r55.cubeList.add(new ModelBox(body_r55, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r56 = new ModelRenderer(this);
				body_r56.setRotationPoint(-0.2F, -0.093F, 0.7473F);
				spike27.addChild(body_r56);
				setRotationAngle(body_r56, 0.2185F, 0.1665F, 0.0677F);
				body_r56.cubeList.add(new ModelBox(body_r56, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r57 = new ModelRenderer(this);
				body_r57.setRotationPoint(0.8F, -0.093F, 0.7473F);
				spike27.addChild(body_r57);
				setRotationAngle(body_r57, 0.2188F, -0.1744F, -0.0077F);
				body_r57.cubeList.add(new ModelBox(body_r57, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				spike28 = new ModelRenderer(this);
				spike28.setRotationPoint(0.2789F, -0.4551F, 2.6757F);
				spike27.addChild(spike28);
				setRotationAngle(spike28, 0.4011F, -0.1294F, 0.0324F);
				
		
				body_r58 = new ModelRenderer(this);
				body_r58.setRotationPoint(-0.4F, -0.9919F, -0.0215F);
				spike28.addChild(body_r58);
				setRotationAngle(body_r58, -0.1302F, 0.1718F, -0.0528F);
				body_r58.cubeList.add(new ModelBox(body_r58, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r59 = new ModelRenderer(this);
				body_r59.setRotationPoint(0.3F, -0.9919F, -0.0215F);
				spike28.addChild(body_r59);
				setRotationAngle(body_r59, -0.1302F, -0.1744F, -0.0077F);
				body_r59.cubeList.add(new ModelBox(body_r59, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r60 = new ModelRenderer(this);
				body_r60.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike28.addChild(body_r60);
				setRotationAngle(body_r60, 0.2185F, 0.1665F, 0.0677F);
				body_r60.cubeList.add(new ModelBox(body_r60, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r61 = new ModelRenderer(this);
				body_r61.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike28.addChild(body_r61);
				setRotationAngle(body_r61, 0.2188F, -0.1744F, -0.0077F);
				body_r61.cubeList.add(new ModelBox(body_r61, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				spike29 = new ModelRenderer(this);
				spike29.setRotationPoint(-0.1F, 0.0F, 1.6F);
				spike28.addChild(spike29);
				setRotationAngle(spike29, 0.4102F, -0.095F, -0.0483F);
				
		
				body_r62 = new ModelRenderer(this);
				body_r62.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike29.addChild(body_r62);
				setRotationAngle(body_r62, -0.1327F, 0.2583F, -0.0645F);
				body_r62.cubeList.add(new ModelBox(body_r62, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r63 = new ModelRenderer(this);
				body_r63.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike29.addChild(body_r63);
				setRotationAngle(body_r63, -0.1314F, -0.2176F, -0.0019F);
				body_r63.cubeList.add(new ModelBox(body_r63, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r64 = new ModelRenderer(this);
				body_r64.setRotationPoint(0.0043F, -0.0543F, 0.6131F);
				spike29.addChild(body_r64);
				setRotationAngle(body_r64, 0.1809F, -0.1241F, 0.8093F);
				body_r64.cubeList.add(new ModelBox(body_r64, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, false));
		
				body_r65 = new ModelRenderer(this);
				body_r65.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike29.addChild(body_r65);
				setRotationAngle(body_r65, 0.2226F, 0.2517F, 0.0872F);
				body_r65.cubeList.add(new ModelBox(body_r65, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r66 = new ModelRenderer(this);
				body_r66.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike29.addChild(body_r66);
				setRotationAngle(body_r66, 0.2188F, -0.1744F, -0.0077F);
				body_r66.cubeList.add(new ModelBox(body_r66, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				spike30 = new ModelRenderer(this);
				spike30.setRotationPoint(-0.5F, -23.5F, 5.0F);
				backSpikes.addChild(spike30);
				setRotationAngle(spike30, 0.2204F, 0.0119F, 0.0495F);
				
		
				body_r67 = new ModelRenderer(this);
				body_r67.setRotationPoint(-0.2F, -1.093F, 0.7473F);
				spike30.addChild(body_r67);
				setRotationAngle(body_r67, -0.1302F, 0.1718F, -0.0528F);
				body_r67.cubeList.add(new ModelBox(body_r67, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r68 = new ModelRenderer(this);
				body_r68.setRotationPoint(0.8F, -1.093F, 0.7473F);
				spike30.addChild(body_r68);
				setRotationAngle(body_r68, -0.1302F, -0.1744F, -0.0077F);
				body_r68.cubeList.add(new ModelBox(body_r68, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r69 = new ModelRenderer(this);
				body_r69.setRotationPoint(-0.2F, -0.093F, 0.7473F);
				spike30.addChild(body_r69);
				setRotationAngle(body_r69, 0.2185F, 0.1665F, 0.0677F);
				body_r69.cubeList.add(new ModelBox(body_r69, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r70 = new ModelRenderer(this);
				body_r70.setRotationPoint(0.8F, -0.093F, 0.7473F);
				spike30.addChild(body_r70);
				setRotationAngle(body_r70, 0.2188F, -0.1744F, -0.0077F);
				body_r70.cubeList.add(new ModelBox(body_r70, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				spike31 = new ModelRenderer(this);
				spike31.setRotationPoint(0.2789F, -0.4551F, 2.6757F);
				spike30.addChild(spike31);
				setRotationAngle(spike31, 0.4011F, -0.1294F, 0.0324F);
				
		
				body_r71 = new ModelRenderer(this);
				body_r71.setRotationPoint(-0.4F, -0.9919F, -0.0215F);
				spike31.addChild(body_r71);
				setRotationAngle(body_r71, -0.1302F, 0.1718F, -0.0528F);
				body_r71.cubeList.add(new ModelBox(body_r71, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r72 = new ModelRenderer(this);
				body_r72.setRotationPoint(0.3F, -0.9919F, -0.0215F);
				spike31.addChild(body_r72);
				setRotationAngle(body_r72, -0.1302F, -0.1744F, -0.0077F);
				body_r72.cubeList.add(new ModelBox(body_r72, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r73 = new ModelRenderer(this);
				body_r73.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike31.addChild(body_r73);
				setRotationAngle(body_r73, 0.2185F, 0.1665F, 0.0677F);
				body_r73.cubeList.add(new ModelBox(body_r73, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r74 = new ModelRenderer(this);
				body_r74.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike31.addChild(body_r74);
				setRotationAngle(body_r74, 0.2188F, -0.1744F, -0.0077F);
				body_r74.cubeList.add(new ModelBox(body_r74, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				spike32 = new ModelRenderer(this);
				spike32.setRotationPoint(-0.1F, 0.0F, 1.6F);
				spike31.addChild(spike32);
				setRotationAngle(spike32, 0.4102F, -0.095F, -0.0483F);
				
		
				body_r75 = new ModelRenderer(this);
				body_r75.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike32.addChild(body_r75);
				setRotationAngle(body_r75, -0.1327F, 0.2583F, -0.0645F);
				body_r75.cubeList.add(new ModelBox(body_r75, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r76 = new ModelRenderer(this);
				body_r76.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike32.addChild(body_r76);
				setRotationAngle(body_r76, -0.1314F, -0.2176F, -0.0019F);
				body_r76.cubeList.add(new ModelBox(body_r76, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r77 = new ModelRenderer(this);
				body_r77.setRotationPoint(0.0043F, -0.0543F, 0.6131F);
				spike32.addChild(body_r77);
				setRotationAngle(body_r77, 0.1809F, -0.1241F, 0.8093F);
				body_r77.cubeList.add(new ModelBox(body_r77, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, false));
		
				body_r78 = new ModelRenderer(this);
				body_r78.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike32.addChild(body_r78);
				setRotationAngle(body_r78, 0.2226F, 0.2517F, 0.0872F);
				body_r78.cubeList.add(new ModelBox(body_r78, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r79 = new ModelRenderer(this);
				body_r79.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike32.addChild(body_r79);
				setRotationAngle(body_r79, 0.2188F, -0.1744F, -0.0077F);
				body_r79.cubeList.add(new ModelBox(body_r79, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				spike38 = new ModelRenderer(this);
				spike38.setRotationPoint(-0.5F, -16.5F, 5.5F);
				backSpikes.addChild(spike38);
				setRotationAngle(spike38, 0.0895F, 0.0119F, 0.0495F);
				
		
				body_r80 = new ModelRenderer(this);
				body_r80.setRotationPoint(-0.2F, -1.093F, 0.7473F);
				spike38.addChild(body_r80);
				setRotationAngle(body_r80, -0.1302F, 0.1718F, -0.0528F);
				body_r80.cubeList.add(new ModelBox(body_r80, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r81 = new ModelRenderer(this);
				body_r81.setRotationPoint(0.8F, -1.093F, 0.7473F);
				spike38.addChild(body_r81);
				setRotationAngle(body_r81, -0.1302F, -0.1744F, -0.0077F);
				body_r81.cubeList.add(new ModelBox(body_r81, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r82 = new ModelRenderer(this);
				body_r82.setRotationPoint(-0.2F, -0.093F, 0.7473F);
				spike38.addChild(body_r82);
				setRotationAngle(body_r82, 0.2185F, 0.1665F, 0.0677F);
				body_r82.cubeList.add(new ModelBox(body_r82, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r83 = new ModelRenderer(this);
				body_r83.setRotationPoint(0.8F, -0.093F, 0.7473F);
				spike38.addChild(body_r83);
				setRotationAngle(body_r83, 0.2188F, -0.1744F, -0.0077F);
				body_r83.cubeList.add(new ModelBox(body_r83, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				spike39 = new ModelRenderer(this);
				spike39.setRotationPoint(0.2789F, -0.4551F, 2.6757F);
				spike38.addChild(spike39);
				setRotationAngle(spike39, 0.4011F, -0.1294F, 0.0324F);
				
		
				body_r84 = new ModelRenderer(this);
				body_r84.setRotationPoint(-0.4F, -0.9919F, -0.0215F);
				spike39.addChild(body_r84);
				setRotationAngle(body_r84, -0.1302F, 0.1718F, -0.0528F);
				body_r84.cubeList.add(new ModelBox(body_r84, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r85 = new ModelRenderer(this);
				body_r85.setRotationPoint(0.3F, -0.9919F, -0.0215F);
				spike39.addChild(body_r85);
				setRotationAngle(body_r85, -0.1302F, -0.1744F, -0.0077F);
				body_r85.cubeList.add(new ModelBox(body_r85, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r86 = new ModelRenderer(this);
				body_r86.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike39.addChild(body_r86);
				setRotationAngle(body_r86, 0.2185F, 0.1665F, 0.0677F);
				body_r86.cubeList.add(new ModelBox(body_r86, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r87 = new ModelRenderer(this);
				body_r87.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike39.addChild(body_r87);
				setRotationAngle(body_r87, 0.2188F, -0.1744F, -0.0077F);
				body_r87.cubeList.add(new ModelBox(body_r87, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				spike40 = new ModelRenderer(this);
				spike40.setRotationPoint(-0.1F, 0.0F, 1.6F);
				spike39.addChild(spike40);
				setRotationAngle(spike40, 0.4102F, -0.095F, -0.0483F);
				
		
				body_r88 = new ModelRenderer(this);
				body_r88.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike40.addChild(body_r88);
				setRotationAngle(body_r88, -0.1327F, 0.2583F, -0.0645F);
				body_r88.cubeList.add(new ModelBox(body_r88, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r89 = new ModelRenderer(this);
				body_r89.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike40.addChild(body_r89);
				setRotationAngle(body_r89, -0.1314F, -0.2176F, -0.0019F);
				body_r89.cubeList.add(new ModelBox(body_r89, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r90 = new ModelRenderer(this);
				body_r90.setRotationPoint(0.0043F, -0.0543F, 0.6131F);
				spike40.addChild(body_r90);
				setRotationAngle(body_r90, 0.1809F, -0.1241F, 0.8093F);
				body_r90.cubeList.add(new ModelBox(body_r90, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, false));
		
				body_r91 = new ModelRenderer(this);
				body_r91.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike40.addChild(body_r91);
				setRotationAngle(body_r91, 0.2226F, 0.2517F, 0.0872F);
				body_r91.cubeList.add(new ModelBox(body_r91, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r92 = new ModelRenderer(this);
				body_r92.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike40.addChild(body_r92);
				setRotationAngle(body_r92, 0.2188F, -0.1744F, -0.0077F);
				body_r92.cubeList.add(new ModelBox(body_r92, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				spike18 = new ModelRenderer(this);
				spike18.setRotationPoint(-2.65F, -32.0F, 2.5F);
				backSpikes.addChild(spike18);
				setRotationAngle(spike18, 0.7057F, -0.1119F, -0.1343F);
				
		
				body_r93 = new ModelRenderer(this);
				body_r93.setRotationPoint(0.2F, -1.093F, 0.7473F);
				spike18.addChild(body_r93);
				setRotationAngle(body_r93, -0.1302F, -0.1718F, 0.0528F);
				body_r93.cubeList.add(new ModelBox(body_r93, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r94 = new ModelRenderer(this);
				body_r94.setRotationPoint(-0.8F, -1.093F, 0.7473F);
				spike18.addChild(body_r94);
				setRotationAngle(body_r94, -0.1302F, 0.1744F, 0.0077F);
				body_r94.cubeList.add(new ModelBox(body_r94, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r95 = new ModelRenderer(this);
				body_r95.setRotationPoint(0.2F, -0.093F, 0.7473F);
				spike18.addChild(body_r95);
				setRotationAngle(body_r95, 0.2185F, -0.1665F, -0.0677F);
				body_r95.cubeList.add(new ModelBox(body_r95, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r96 = new ModelRenderer(this);
				body_r96.setRotationPoint(-0.8F, -0.093F, 0.7473F);
				spike18.addChild(body_r96);
				setRotationAngle(body_r96, 0.2188F, 0.1744F, 0.0077F);
				body_r96.cubeList.add(new ModelBox(body_r96, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				spike19 = new ModelRenderer(this);
				spike19.setRotationPoint(-0.2789F, -0.4551F, 2.6757F);
				spike18.addChild(spike19);
				setRotationAngle(spike19, 0.4011F, 0.1294F, -0.0324F);
				
		
				body_r97 = new ModelRenderer(this);
				body_r97.setRotationPoint(0.4F, -0.9919F, -0.0215F);
				spike19.addChild(body_r97);
				setRotationAngle(body_r97, -0.1302F, -0.1718F, 0.0528F);
				body_r97.cubeList.add(new ModelBox(body_r97, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r98 = new ModelRenderer(this);
				body_r98.setRotationPoint(-0.3F, -0.9919F, -0.0215F);
				spike19.addChild(body_r98);
				setRotationAngle(body_r98, -0.1302F, 0.1744F, 0.0077F);
				body_r98.cubeList.add(new ModelBox(body_r98, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r99 = new ModelRenderer(this);
				body_r99.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike19.addChild(body_r99);
				setRotationAngle(body_r99, 0.2185F, -0.1665F, -0.0677F);
				body_r99.cubeList.add(new ModelBox(body_r99, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r100 = new ModelRenderer(this);
				body_r100.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike19.addChild(body_r100);
				setRotationAngle(body_r100, 0.2188F, 0.1744F, 0.0077F);
				body_r100.cubeList.add(new ModelBox(body_r100, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				spike20 = new ModelRenderer(this);
				spike20.setRotationPoint(0.1F, 0.0F, 1.6F);
				spike19.addChild(spike20);
				setRotationAngle(spike20, 0.4102F, 0.095F, 0.0483F);
				
		
				body_r101 = new ModelRenderer(this);
				body_r101.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike20.addChild(body_r101);
				setRotationAngle(body_r101, -0.1327F, -0.2583F, 0.0645F);
				body_r101.cubeList.add(new ModelBox(body_r101, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r102 = new ModelRenderer(this);
				body_r102.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike20.addChild(body_r102);
				setRotationAngle(body_r102, -0.1314F, 0.2176F, 0.0019F);
				body_r102.cubeList.add(new ModelBox(body_r102, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r103 = new ModelRenderer(this);
				body_r103.setRotationPoint(-0.0043F, -0.0543F, 0.6131F);
				spike20.addChild(body_r103);
				setRotationAngle(body_r103, 0.1809F, 0.1241F, -0.8093F);
				body_r103.cubeList.add(new ModelBox(body_r103, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, true));
		
				body_r104 = new ModelRenderer(this);
				body_r104.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike20.addChild(body_r104);
				setRotationAngle(body_r104, 0.2226F, -0.2517F, -0.0872F);
				body_r104.cubeList.add(new ModelBox(body_r104, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r105 = new ModelRenderer(this);
				body_r105.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike20.addChild(body_r105);
				setRotationAngle(body_r105, 0.2188F, 0.1744F, 0.0077F);
				body_r105.cubeList.add(new ModelBox(body_r105, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				spike9 = new ModelRenderer(this);
				spike9.setRotationPoint(4.25F, -26.0F, 4.0F);
				backSpikes.addChild(spike9);
				setRotationAngle(spike9, 0.2036F, 0.2616F, 0.234F);
				
		
				body_r106 = new ModelRenderer(this);
				body_r106.setRotationPoint(-0.2F, -1.093F, 0.7473F);
				spike9.addChild(body_r106);
				setRotationAngle(body_r106, -0.1302F, 0.1718F, -0.0528F);
				body_r106.cubeList.add(new ModelBox(body_r106, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r107 = new ModelRenderer(this);
				body_r107.setRotationPoint(0.8F, -1.093F, 0.7473F);
				spike9.addChild(body_r107);
				setRotationAngle(body_r107, -0.1302F, -0.1744F, -0.0077F);
				body_r107.cubeList.add(new ModelBox(body_r107, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r108 = new ModelRenderer(this);
				body_r108.setRotationPoint(-0.2F, -0.093F, 0.7473F);
				spike9.addChild(body_r108);
				setRotationAngle(body_r108, 0.2185F, 0.1665F, 0.0677F);
				body_r108.cubeList.add(new ModelBox(body_r108, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r109 = new ModelRenderer(this);
				body_r109.setRotationPoint(0.8F, -0.093F, 0.7473F);
				spike9.addChild(body_r109);
				setRotationAngle(body_r109, 0.2188F, -0.1744F, -0.0077F);
				body_r109.cubeList.add(new ModelBox(body_r109, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				spike10 = new ModelRenderer(this);
				spike10.setRotationPoint(0.2789F, -0.4551F, 2.6757F);
				spike9.addChild(spike10);
				setRotationAngle(spike10, 0.4011F, -0.1294F, 0.0324F);
				
		
				body_r110 = new ModelRenderer(this);
				body_r110.setRotationPoint(-0.4F, -0.9919F, -0.0215F);
				spike10.addChild(body_r110);
				setRotationAngle(body_r110, -0.1302F, 0.1718F, -0.0528F);
				body_r110.cubeList.add(new ModelBox(body_r110, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r111 = new ModelRenderer(this);
				body_r111.setRotationPoint(0.3F, -0.9919F, -0.0215F);
				spike10.addChild(body_r111);
				setRotationAngle(body_r111, -0.1302F, -0.1744F, -0.0077F);
				body_r111.cubeList.add(new ModelBox(body_r111, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r112 = new ModelRenderer(this);
				body_r112.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike10.addChild(body_r112);
				setRotationAngle(body_r112, 0.2185F, 0.1665F, 0.0677F);
				body_r112.cubeList.add(new ModelBox(body_r112, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r113 = new ModelRenderer(this);
				body_r113.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike10.addChild(body_r113);
				setRotationAngle(body_r113, 0.2188F, -0.1744F, -0.0077F);
				body_r113.cubeList.add(new ModelBox(body_r113, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				spike11 = new ModelRenderer(this);
				spike11.setRotationPoint(-0.1F, 0.0F, 1.6F);
				spike10.addChild(spike11);
				setRotationAngle(spike11, 0.4102F, -0.095F, -0.0483F);
				
		
				body_r114 = new ModelRenderer(this);
				body_r114.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike11.addChild(body_r114);
				setRotationAngle(body_r114, -0.1327F, 0.2583F, -0.0645F);
				body_r114.cubeList.add(new ModelBox(body_r114, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r115 = new ModelRenderer(this);
				body_r115.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike11.addChild(body_r115);
				setRotationAngle(body_r115, -0.1314F, -0.2176F, -0.0019F);
				body_r115.cubeList.add(new ModelBox(body_r115, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r116 = new ModelRenderer(this);
				body_r116.setRotationPoint(0.0043F, -0.0543F, 0.6131F);
				spike11.addChild(body_r116);
				setRotationAngle(body_r116, 0.1809F, -0.1241F, 0.8093F);
				body_r116.cubeList.add(new ModelBox(body_r116, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, false));
		
				body_r117 = new ModelRenderer(this);
				body_r117.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike11.addChild(body_r117);
				setRotationAngle(body_r117, 0.2226F, 0.2517F, 0.0872F);
				body_r117.cubeList.add(new ModelBox(body_r117, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r118 = new ModelRenderer(this);
				body_r118.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike11.addChild(body_r118);
				setRotationAngle(body_r118, 0.2188F, -0.1744F, -0.0077F);
				body_r118.cubeList.add(new ModelBox(body_r118, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				spike21 = new ModelRenderer(this);
				spike21.setRotationPoint(-4.65F, -26.0F, 4.0F);
				backSpikes.addChild(spike21);
				setRotationAngle(spike21, 0.2036F, -0.2616F, -0.234F);
				
		
				body_r119 = new ModelRenderer(this);
				body_r119.setRotationPoint(0.2F, -1.093F, 0.7473F);
				spike21.addChild(body_r119);
				setRotationAngle(body_r119, -0.1302F, -0.1718F, 0.0528F);
				body_r119.cubeList.add(new ModelBox(body_r119, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r120 = new ModelRenderer(this);
				body_r120.setRotationPoint(-0.8F, -1.093F, 0.7473F);
				spike21.addChild(body_r120);
				setRotationAngle(body_r120, -0.1302F, 0.1744F, 0.0077F);
				body_r120.cubeList.add(new ModelBox(body_r120, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r121 = new ModelRenderer(this);
				body_r121.setRotationPoint(0.2F, -0.093F, 0.7473F);
				spike21.addChild(body_r121);
				setRotationAngle(body_r121, 0.2185F, -0.1665F, -0.0677F);
				body_r121.cubeList.add(new ModelBox(body_r121, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r122 = new ModelRenderer(this);
				body_r122.setRotationPoint(-0.8F, -0.093F, 0.7473F);
				spike21.addChild(body_r122);
				setRotationAngle(body_r122, 0.2188F, 0.1744F, 0.0077F);
				body_r122.cubeList.add(new ModelBox(body_r122, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				spike22 = new ModelRenderer(this);
				spike22.setRotationPoint(-0.2789F, -0.4551F, 2.6757F);
				spike21.addChild(spike22);
				setRotationAngle(spike22, 0.4011F, 0.1294F, -0.0324F);
				
		
				body_r123 = new ModelRenderer(this);
				body_r123.setRotationPoint(0.4F, -0.9919F, -0.0215F);
				spike22.addChild(body_r123);
				setRotationAngle(body_r123, -0.1302F, -0.1718F, 0.0528F);
				body_r123.cubeList.add(new ModelBox(body_r123, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r124 = new ModelRenderer(this);
				body_r124.setRotationPoint(-0.3F, -0.9919F, -0.0215F);
				spike22.addChild(body_r124);
				setRotationAngle(body_r124, -0.1302F, 0.1744F, 0.0077F);
				body_r124.cubeList.add(new ModelBox(body_r124, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r125 = new ModelRenderer(this);
				body_r125.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike22.addChild(body_r125);
				setRotationAngle(body_r125, 0.2185F, -0.1665F, -0.0677F);
				body_r125.cubeList.add(new ModelBox(body_r125, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r126 = new ModelRenderer(this);
				body_r126.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike22.addChild(body_r126);
				setRotationAngle(body_r126, 0.2188F, 0.1744F, 0.0077F);
				body_r126.cubeList.add(new ModelBox(body_r126, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				spike23 = new ModelRenderer(this);
				spike23.setRotationPoint(0.1F, 0.0F, 1.6F);
				spike22.addChild(spike23);
				setRotationAngle(spike23, 0.4102F, 0.095F, 0.0483F);
				
		
				body_r127 = new ModelRenderer(this);
				body_r127.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike23.addChild(body_r127);
				setRotationAngle(body_r127, -0.1327F, -0.2583F, 0.0645F);
				body_r127.cubeList.add(new ModelBox(body_r127, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r128 = new ModelRenderer(this);
				body_r128.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike23.addChild(body_r128);
				setRotationAngle(body_r128, -0.1314F, 0.2176F, 0.0019F);
				body_r128.cubeList.add(new ModelBox(body_r128, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r129 = new ModelRenderer(this);
				body_r129.setRotationPoint(-0.0043F, -0.0543F, 0.6131F);
				spike23.addChild(body_r129);
				setRotationAngle(body_r129, 0.1809F, 0.1241F, -0.8093F);
				body_r129.cubeList.add(new ModelBox(body_r129, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, true));
		
				body_r130 = new ModelRenderer(this);
				body_r130.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike23.addChild(body_r130);
				setRotationAngle(body_r130, 0.2226F, -0.2517F, -0.0872F);
				body_r130.cubeList.add(new ModelBox(body_r130, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r131 = new ModelRenderer(this);
				body_r131.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike23.addChild(body_r131);
				setRotationAngle(body_r131, 0.2188F, 0.1744F, 0.0077F);
				body_r131.cubeList.add(new ModelBox(body_r131, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				spike15 = new ModelRenderer(this);
				spike15.setRotationPoint(2.25F, -21.0F, 5.5F);
				backSpikes.addChild(spike15);
				setRotationAngle(spike15, -0.0526F, 0.347F, 0.2527F);
				
		
				body_r132 = new ModelRenderer(this);
				body_r132.setRotationPoint(-0.2F, -1.093F, 0.7473F);
				spike15.addChild(body_r132);
				setRotationAngle(body_r132, -0.1302F, 0.1718F, -0.0528F);
				body_r132.cubeList.add(new ModelBox(body_r132, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r133 = new ModelRenderer(this);
				body_r133.setRotationPoint(0.8F, -1.093F, 0.7473F);
				spike15.addChild(body_r133);
				setRotationAngle(body_r133, -0.1302F, -0.1744F, -0.0077F);
				body_r133.cubeList.add(new ModelBox(body_r133, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r134 = new ModelRenderer(this);
				body_r134.setRotationPoint(-0.2F, -0.093F, 0.7473F);
				spike15.addChild(body_r134);
				setRotationAngle(body_r134, 0.2185F, 0.1665F, 0.0677F);
				body_r134.cubeList.add(new ModelBox(body_r134, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r135 = new ModelRenderer(this);
				body_r135.setRotationPoint(0.8F, -0.093F, 0.7473F);
				spike15.addChild(body_r135);
				setRotationAngle(body_r135, 0.2188F, -0.1744F, -0.0077F);
				body_r135.cubeList.add(new ModelBox(body_r135, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				spike16 = new ModelRenderer(this);
				spike16.setRotationPoint(0.2789F, -0.4551F, 2.6757F);
				spike15.addChild(spike16);
				setRotationAngle(spike16, 0.4011F, -0.1294F, 0.0324F);
				
		
				body_r136 = new ModelRenderer(this);
				body_r136.setRotationPoint(-0.4F, -0.9919F, -0.0215F);
				spike16.addChild(body_r136);
				setRotationAngle(body_r136, -0.1302F, 0.1718F, -0.0528F);
				body_r136.cubeList.add(new ModelBox(body_r136, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r137 = new ModelRenderer(this);
				body_r137.setRotationPoint(0.3F, -0.9919F, -0.0215F);
				spike16.addChild(body_r137);
				setRotationAngle(body_r137, -0.1302F, -0.1744F, -0.0077F);
				body_r137.cubeList.add(new ModelBox(body_r137, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r138 = new ModelRenderer(this);
				body_r138.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike16.addChild(body_r138);
				setRotationAngle(body_r138, 0.2185F, 0.1665F, 0.0677F);
				body_r138.cubeList.add(new ModelBox(body_r138, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r139 = new ModelRenderer(this);
				body_r139.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike16.addChild(body_r139);
				setRotationAngle(body_r139, 0.2188F, -0.1744F, -0.0077F);
				body_r139.cubeList.add(new ModelBox(body_r139, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				spike17 = new ModelRenderer(this);
				spike17.setRotationPoint(-0.1F, 0.0F, 1.6F);
				spike16.addChild(spike17);
				setRotationAngle(spike17, 0.4102F, -0.095F, -0.0483F);
				
		
				body_r140 = new ModelRenderer(this);
				body_r140.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike17.addChild(body_r140);
				setRotationAngle(body_r140, -0.1327F, 0.2583F, -0.0645F);
				body_r140.cubeList.add(new ModelBox(body_r140, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r141 = new ModelRenderer(this);
				body_r141.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike17.addChild(body_r141);
				setRotationAngle(body_r141, -0.1314F, -0.2176F, -0.0019F);
				body_r141.cubeList.add(new ModelBox(body_r141, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r142 = new ModelRenderer(this);
				body_r142.setRotationPoint(0.0043F, -0.0543F, 0.6131F);
				spike17.addChild(body_r142);
				setRotationAngle(body_r142, 0.1809F, -0.1241F, 0.8093F);
				body_r142.cubeList.add(new ModelBox(body_r142, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, false));
		
				body_r143 = new ModelRenderer(this);
				body_r143.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike17.addChild(body_r143);
				setRotationAngle(body_r143, 0.2226F, 0.2517F, 0.0872F);
				body_r143.cubeList.add(new ModelBox(body_r143, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r144 = new ModelRenderer(this);
				body_r144.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike17.addChild(body_r144);
				setRotationAngle(body_r144, 0.2188F, -0.1744F, -0.0077F);
				body_r144.cubeList.add(new ModelBox(body_r144, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				spike24 = new ModelRenderer(this);
				spike24.setRotationPoint(-2.65F, -21.0F, 5.5F);
				backSpikes.addChild(spike24);
				setRotationAngle(spike24, -0.0526F, -0.347F, -0.2527F);
				
		
				body_r145 = new ModelRenderer(this);
				body_r145.setRotationPoint(0.2F, -1.093F, 0.7473F);
				spike24.addChild(body_r145);
				setRotationAngle(body_r145, -0.1302F, -0.1718F, 0.0528F);
				body_r145.cubeList.add(new ModelBox(body_r145, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r146 = new ModelRenderer(this);
				body_r146.setRotationPoint(-0.8F, -1.093F, 0.7473F);
				spike24.addChild(body_r146);
				setRotationAngle(body_r146, -0.1302F, 0.1744F, 0.0077F);
				body_r146.cubeList.add(new ModelBox(body_r146, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r147 = new ModelRenderer(this);
				body_r147.setRotationPoint(0.2F, -0.093F, 0.7473F);
				spike24.addChild(body_r147);
				setRotationAngle(body_r147, 0.2185F, -0.1665F, -0.0677F);
				body_r147.cubeList.add(new ModelBox(body_r147, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r148 = new ModelRenderer(this);
				body_r148.setRotationPoint(-0.8F, -0.093F, 0.7473F);
				spike24.addChild(body_r148);
				setRotationAngle(body_r148, 0.2188F, 0.1744F, 0.0077F);
				body_r148.cubeList.add(new ModelBox(body_r148, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				spike25 = new ModelRenderer(this);
				spike25.setRotationPoint(-0.2789F, -0.4551F, 2.6757F);
				spike24.addChild(spike25);
				setRotationAngle(spike25, 0.4011F, 0.1294F, -0.0324F);
				
		
				body_r149 = new ModelRenderer(this);
				body_r149.setRotationPoint(0.4F, -0.9919F, -0.0215F);
				spike25.addChild(body_r149);
				setRotationAngle(body_r149, -0.1302F, -0.1718F, 0.0528F);
				body_r149.cubeList.add(new ModelBox(body_r149, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r150 = new ModelRenderer(this);
				body_r150.setRotationPoint(-0.3F, -0.9919F, -0.0215F);
				spike25.addChild(body_r150);
				setRotationAngle(body_r150, -0.1302F, 0.1744F, 0.0077F);
				body_r150.cubeList.add(new ModelBox(body_r150, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r151 = new ModelRenderer(this);
				body_r151.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike25.addChild(body_r151);
				setRotationAngle(body_r151, 0.2185F, -0.1665F, -0.0677F);
				body_r151.cubeList.add(new ModelBox(body_r151, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r152 = new ModelRenderer(this);
				body_r152.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike25.addChild(body_r152);
				setRotationAngle(body_r152, 0.2188F, 0.1744F, 0.0077F);
				body_r152.cubeList.add(new ModelBox(body_r152, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				spike26 = new ModelRenderer(this);
				spike26.setRotationPoint(0.1F, 0.0F, 1.6F);
				spike25.addChild(spike26);
				setRotationAngle(spike26, 0.4102F, 0.095F, 0.0483F);
				
		
				body_r153 = new ModelRenderer(this);
				body_r153.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike26.addChild(body_r153);
				setRotationAngle(body_r153, -0.1327F, -0.2583F, 0.0645F);
				body_r153.cubeList.add(new ModelBox(body_r153, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r154 = new ModelRenderer(this);
				body_r154.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike26.addChild(body_r154);
				setRotationAngle(body_r154, -0.1314F, 0.2176F, 0.0019F);
				body_r154.cubeList.add(new ModelBox(body_r154, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r155 = new ModelRenderer(this);
				body_r155.setRotationPoint(-0.0043F, -0.0543F, 0.6131F);
				spike26.addChild(body_r155);
				setRotationAngle(body_r155, 0.1809F, 0.1241F, -0.8093F);
				body_r155.cubeList.add(new ModelBox(body_r155, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, true));
		
				body_r156 = new ModelRenderer(this);
				body_r156.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike26.addChild(body_r156);
				setRotationAngle(body_r156, 0.2226F, -0.2517F, -0.0872F);
				body_r156.cubeList.add(new ModelBox(body_r156, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r157 = new ModelRenderer(this);
				body_r157.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike26.addChild(body_r157);
				setRotationAngle(body_r157, 0.2188F, 0.1744F, 0.0077F);
				body_r157.cubeList.add(new ModelBox(body_r157, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				spike4 = new ModelRenderer(this);
				spike4.setRotationPoint(-5.65F, -31.0F, 2.5F);
				backSpikes.addChild(spike4);
				setRotationAngle(spike4, 0.471F, -0.347F, -0.2527F);
				
		
				body_r158 = new ModelRenderer(this);
				body_r158.setRotationPoint(0.2F, -1.093F, 0.7473F);
				spike4.addChild(body_r158);
				setRotationAngle(body_r158, -0.1302F, -0.1718F, 0.0528F);
				body_r158.cubeList.add(new ModelBox(body_r158, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r159 = new ModelRenderer(this);
				body_r159.setRotationPoint(-0.8F, -1.093F, 0.7473F);
				spike4.addChild(body_r159);
				setRotationAngle(body_r159, -0.1302F, 0.1744F, 0.0077F);
				body_r159.cubeList.add(new ModelBox(body_r159, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r160 = new ModelRenderer(this);
				body_r160.setRotationPoint(0.2F, -0.093F, 0.7473F);
				spike4.addChild(body_r160);
				setRotationAngle(body_r160, 0.2185F, -0.1665F, -0.0677F);
				body_r160.cubeList.add(new ModelBox(body_r160, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r161 = new ModelRenderer(this);
				body_r161.setRotationPoint(-0.8F, -0.093F, 0.7473F);
				spike4.addChild(body_r161);
				setRotationAngle(body_r161, 0.2188F, 0.1744F, 0.0077F);
				body_r161.cubeList.add(new ModelBox(body_r161, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				spike6 = new ModelRenderer(this);
				spike6.setRotationPoint(-0.2789F, -0.4551F, 2.6757F);
				spike4.addChild(spike6);
				setRotationAngle(spike6, 0.4011F, 0.1294F, -0.0324F);
				
		
				body_r162 = new ModelRenderer(this);
				body_r162.setRotationPoint(0.4F, -0.9919F, -0.0215F);
				spike6.addChild(body_r162);
				setRotationAngle(body_r162, -0.1302F, -0.1718F, 0.0528F);
				body_r162.cubeList.add(new ModelBox(body_r162, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r163 = new ModelRenderer(this);
				body_r163.setRotationPoint(-0.3F, -0.9919F, -0.0215F);
				spike6.addChild(body_r163);
				setRotationAngle(body_r163, -0.1302F, 0.1744F, 0.0077F);
				body_r163.cubeList.add(new ModelBox(body_r163, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r164 = new ModelRenderer(this);
				body_r164.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike6.addChild(body_r164);
				setRotationAngle(body_r164, 0.2185F, -0.1665F, -0.0677F);
				body_r164.cubeList.add(new ModelBox(body_r164, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r165 = new ModelRenderer(this);
				body_r165.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike6.addChild(body_r165);
				setRotationAngle(body_r165, 0.2188F, 0.1744F, 0.0077F);
				body_r165.cubeList.add(new ModelBox(body_r165, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				spike8 = new ModelRenderer(this);
				spike8.setRotationPoint(0.1F, 0.0F, 1.6F);
				spike6.addChild(spike8);
				setRotationAngle(spike8, 0.4102F, 0.095F, 0.0483F);
				
		
				body_r166 = new ModelRenderer(this);
				body_r166.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike8.addChild(body_r166);
				setRotationAngle(body_r166, -0.1327F, -0.2583F, 0.0645F);
				body_r166.cubeList.add(new ModelBox(body_r166, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r167 = new ModelRenderer(this);
				body_r167.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike8.addChild(body_r167);
				setRotationAngle(body_r167, -0.1314F, 0.2176F, 0.0019F);
				body_r167.cubeList.add(new ModelBox(body_r167, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r168 = new ModelRenderer(this);
				body_r168.setRotationPoint(-0.0043F, -0.0543F, 0.6131F);
				spike8.addChild(body_r168);
				setRotationAngle(body_r168, 0.1809F, 0.1241F, -0.8093F);
				body_r168.cubeList.add(new ModelBox(body_r168, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, true));
		
				body_r169 = new ModelRenderer(this);
				body_r169.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike8.addChild(body_r169);
				setRotationAngle(body_r169, 0.2226F, -0.2517F, -0.0872F);
				body_r169.cubeList.add(new ModelBox(body_r169, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r170 = new ModelRenderer(this);
				body_r170.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike8.addChild(body_r170);
				setRotationAngle(body_r170, 0.2188F, 0.1744F, 0.0077F);
				body_r170.cubeList.add(new ModelBox(body_r170, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				waist = new ModelRenderer(this);
				waist.setRotationPoint(0.2F, 28.8F, 0.0F);
				chest.addChild(waist);
				
		
				body_r171 = new ModelRenderer(this);
				body_r171.setRotationPoint(0.0F, 1.0F, -2.0F);
				waist.addChild(body_r171);
				setRotationAngle(body_r171, 0.0175F, 0.0F, 0.0F);
				body_r171.cubeList.add(new ModelBox(body_r171, 9, 30, -6.2F, -23.4F, -4.6F, 12, 11, 1, 0.5F, false));
		
				body_r172 = new ModelRenderer(this);
				body_r172.setRotationPoint(0.0F, 0.0F, 0.0F);
				waist.addChild(body_r172);
				setRotationAngle(body_r172, -0.0175F, 0.0F, 0.0F);
				body_r172.cubeList.add(new ModelBox(body_r172, 9, 30, -6.2F, -23.4F, 3.6F, 12, 11, 1, 0.5F, false));
		
				body_r173 = new ModelRenderer(this);
				body_r173.setRotationPoint(0.0F, 1.0F, -1.0F);
				waist.addChild(body_r173);
				setRotationAngle(body_r173, -0.0175F, 0.0F, 0.0F);
				body_r173.cubeList.add(new ModelBox(body_r173, 36, 34, -7.2F, -24.4F, -4.4F, 14, 11, 8, 1.0F, false));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-7.2F, 0.4F, 0.0F);
				bipedBody.addChild(bipedRightArm);
				
		
				rightArm = new ModelRenderer(this);
				rightArm.setRotationPoint(0.0F, -1.0F, 0.0F);
				bipedRightArm.addChild(rightArm);
				setRotationAngle(rightArm, -0.1745F, -0.2618F, 0.4363F);
				rightArm.cubeList.add(new ModelBox(rightArm, 0, 61, -5.6095F, -2.0845F, -4.2F, 7, 13, 7, 0.0F, false));
		
				spike33 = new ModelRenderer(this);
				spike33.setRotationPoint(-4.0F, -1.1F, 0.5F);
				rightArm.addChild(spike33);
				setRotationAngle(spike33, 1.3519F, -0.4689F, -0.6415F);
				
		
				body_r174 = new ModelRenderer(this);
				body_r174.setRotationPoint(0.2F, -1.093F, 0.7473F);
				spike33.addChild(body_r174);
				setRotationAngle(body_r174, -0.1302F, -0.1718F, 0.0528F);
				body_r174.cubeList.add(new ModelBox(body_r174, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r175 = new ModelRenderer(this);
				body_r175.setRotationPoint(-0.8F, -1.093F, 0.7473F);
				spike33.addChild(body_r175);
				setRotationAngle(body_r175, -0.1302F, 0.1744F, 0.0077F);
				body_r175.cubeList.add(new ModelBox(body_r175, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r176 = new ModelRenderer(this);
				body_r176.setRotationPoint(0.2F, -0.093F, 0.7473F);
				spike33.addChild(body_r176);
				setRotationAngle(body_r176, 0.2185F, -0.1665F, -0.0677F);
				body_r176.cubeList.add(new ModelBox(body_r176, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				body_r177 = new ModelRenderer(this);
				body_r177.setRotationPoint(-0.8F, -0.093F, 0.7473F);
				spike33.addChild(body_r177);
				setRotationAngle(body_r177, 0.2188F, 0.1744F, 0.0077F);
				body_r177.cubeList.add(new ModelBox(body_r177, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, true));
		
				spike36 = new ModelRenderer(this);
				spike36.setRotationPoint(-0.2789F, -0.4551F, 2.6757F);
				spike33.addChild(spike36);
				setRotationAngle(spike36, 0.3575F, 0.1294F, -0.0324F);
				
		
				body_r178 = new ModelRenderer(this);
				body_r178.setRotationPoint(0.4F, -0.9919F, -0.0215F);
				spike36.addChild(body_r178);
				setRotationAngle(body_r178, -0.1302F, -0.1718F, 0.0528F);
				body_r178.cubeList.add(new ModelBox(body_r178, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r179 = new ModelRenderer(this);
				body_r179.setRotationPoint(-0.3F, -0.9919F, -0.0215F);
				spike36.addChild(body_r179);
				setRotationAngle(body_r179, -0.1302F, 0.1744F, 0.0077F);
				body_r179.cubeList.add(new ModelBox(body_r179, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r180 = new ModelRenderer(this);
				body_r180.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike36.addChild(body_r180);
				setRotationAngle(body_r180, 0.2185F, -0.1665F, -0.0677F);
				body_r180.cubeList.add(new ModelBox(body_r180, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				body_r181 = new ModelRenderer(this);
				body_r181.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike36.addChild(body_r181);
				setRotationAngle(body_r181, 0.2188F, 0.1744F, 0.0077F);
				body_r181.cubeList.add(new ModelBox(body_r181, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, true));
		
				spike37 = new ModelRenderer(this);
				spike37.setRotationPoint(0.1F, 0.0F, 1.6F);
				spike36.addChild(spike37);
				setRotationAngle(spike37, 0.2793F, 0.095F, 0.0483F);
				
		
				body_r182 = new ModelRenderer(this);
				body_r182.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike37.addChild(body_r182);
				setRotationAngle(body_r182, -0.1327F, -0.2583F, 0.0645F);
				body_r182.cubeList.add(new ModelBox(body_r182, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r183 = new ModelRenderer(this);
				body_r183.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike37.addChild(body_r183);
				setRotationAngle(body_r183, -0.1314F, 0.2176F, 0.0019F);
				body_r183.cubeList.add(new ModelBox(body_r183, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r184 = new ModelRenderer(this);
				body_r184.setRotationPoint(-0.0043F, -0.0543F, 0.6131F);
				spike37.addChild(body_r184);
				setRotationAngle(body_r184, 0.1809F, 0.1241F, -0.8093F);
				body_r184.cubeList.add(new ModelBox(body_r184, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, true));
		
				body_r185 = new ModelRenderer(this);
				body_r185.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike37.addChild(body_r185);
				setRotationAngle(body_r185, 0.2226F, -0.2517F, -0.0872F);
				body_r185.cubeList.add(new ModelBox(body_r185, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				body_r186 = new ModelRenderer(this);
				body_r186.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike37.addChild(body_r186);
				setRotationAngle(body_r186, 0.2188F, 0.1744F, 0.0077F);
				body_r186.cubeList.add(new ModelBox(body_r186, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, true));
		
				right4rm = new ModelRenderer(this);
				right4rm.setRotationPoint(-2.3095F, 10.5252F, 0.0F);
				rightArm.addChild(right4rm);
				setRotationAngle(right4rm, -0.2618F, 0.0F, -0.0873F);
				right4rm.cubeList.add(new ModelBox(right4rm, 40, 53, -3.3F, -0.1098F, -4.2F, 7, 13, 7, 0.5F, false));
		
				leftArm_r1 = new ModelRenderer(this);
				leftArm_r1.setRotationPoint(10.0F, 19.0F, 0.0F);
				right4rm.addChild(leftArm_r1);
				setRotationAngle(leftArm_r1, 0.0F, 0.0F, -0.2182F);
				leftArm_r1.cubeList.add(new ModelBox(leftArm_r1, 74, 0, -11.5F, -8.5098F, -4.2F, 7, 3, 7, 0.45F, false));
		
				rightBlade = new ModelRenderer(this);
				rightBlade.setRotationPoint(10.5F, 19.5F, 0.0F);
				right4rm.addChild(rightBlade);
				
		
				leftArm_r2 = new ModelRenderer(this);
				leftArm_r2.setRotationPoint(-25.104F, -21.2064F, -0.1F);
				rightBlade.addChild(leftArm_r2);
				setRotationAngle(leftArm_r2, 0.0F, 0.0F, 1.7453F);
				leftArm_r2.cubeList.add(new ModelBox(leftArm_r2, 0, 0, -11.8F, 1.8902F, -0.5F, 2, 1, 0, 0.316F, false));
				leftArm_r2.cubeList.add(new ModelBox(leftArm_r2, 0, 0, -15.5665F, 1.3612F, -0.5F, 6, 0, 0, 0.316F, false));
		
				leftArm_r3 = new ModelRenderer(this);
				leftArm_r3.setRotationPoint(-24.4725F, -21.2275F, -0.4F);
				rightBlade.addChild(leftArm_r3);
				setRotationAngle(leftArm_r3, 0.0F, 0.0F, 2.138F);
				leftArm_r3.cubeList.add(new ModelBox(leftArm_r3, 0, 0, -13.6F, 7.8902F, -0.2F, 5, 0, 0, 0.32F, false));
				leftArm_r3.cubeList.add(new ModelBox(leftArm_r3, 0, 0, -12.3F, 7.3902F, -0.2F, 3, 0, 0, 0.318F, false));
				leftArm_r3.cubeList.add(new ModelBox(leftArm_r3, 0, 0, -11.2F, 6.8902F, -0.2F, 3, 1, 0, 0.316F, false));
		
				leftArm_r4 = new ModelRenderer(this);
				leftArm_r4.setRotationPoint(-24.5725F, -21.3275F, -0.4F);
				rightBlade.addChild(leftArm_r4);
				setRotationAngle(leftArm_r4, 0.0F, 0.0F, 1.9199F);
				leftArm_r4.cubeList.add(new ModelBox(leftArm_r4, 0, 0, -9.6F, 4.8902F, -0.2F, 4, 1, 0, 0.322F, false));
		
				leftArm_r5 = new ModelRenderer(this);
				leftArm_r5.setRotationPoint(-25.144F, -20.7533F, -0.1F);
				rightBlade.addChild(leftArm_r5);
				setRotationAngle(leftArm_r5, 0.0F, 0.0F, 1.7017F);
				leftArm_r5.cubeList.add(new ModelBox(leftArm_r5, 0, 0, -10.7F, 0.8902F, -0.5F, 5, 2, 0, 0.318F, false));
		
				leftArm_r6 = new ModelRenderer(this);
				leftArm_r6.setRotationPoint(-24.2813F, -21.0108F, -0.3F);
				rightBlade.addChild(leftArm_r6);
				setRotationAngle(leftArm_r6, 0.0F, 0.0F, 1.6581F);
				leftArm_r6.cubeList.add(new ModelBox(leftArm_r6, 0, 0, -6.7F, 3.6902F, -0.3F, 4, 1, 0, 0.34F, false));
		
				leftArm_r7 = new ModelRenderer(this);
				leftArm_r7.setRotationPoint(-25.244F, -20.6533F, -0.1F);
				rightBlade.addChild(leftArm_r7);
				setRotationAngle(leftArm_r7, 0.0F, 0.0F, 1.3963F);
				leftArm_r7.cubeList.add(new ModelBox(leftArm_r7, 0, 0, -6.7F, -1.2098F, -0.5F, 4, 3, 0, 0.34F, false));
		
				leftArm_r8 = new ModelRenderer(this);
				leftArm_r8.setRotationPoint(-25.244F, -20.6533F, -0.1F);
				rightBlade.addChild(leftArm_r8);
				setRotationAngle(leftArm_r8, 0.0F, 0.0F, 1.5272F);
				leftArm_r8.cubeList.add(new ModelBox(leftArm_r8, 0, 0, -6.7F, -0.2098F, -0.5F, 4, 3, 0, 0.338F, false));
		
				leftArm_r9 = new ModelRenderer(this);
				leftArm_r9.setRotationPoint(-25.3057F, -19.7964F, -0.65F);
				rightBlade.addChild(leftArm_r9);
				setRotationAngle(leftArm_r9, 0.0F, 0.0F, 1.4399F);
				leftArm_r9.cubeList.add(new ModelBox(leftArm_r9, 12, 46, -4.7F, 2.2902F, -0.45F, 8, 1, 1, -0.1F, false));
		
				leftArm_r10 = new ModelRenderer(this);
				leftArm_r10.setRotationPoint(-25.2962F, -20.5193F, -0.05F);
				rightBlade.addChild(leftArm_r10);
				setRotationAngle(leftArm_r10, 0.0F, 0.0F, 1.3963F);
				leftArm_r10.cubeList.add(new ModelBox(leftArm_r10, 0, 0, -2.7F, -1.3098F, -0.55F, 6, 4, 0, 0.35F, false));
		
				leftArm_r11 = new ModelRenderer(this);
				leftArm_r11.setRotationPoint(-20.1887F, -11.3593F, -0.05F);
				rightBlade.addChild(leftArm_r11);
				setRotationAngle(leftArm_r11, 0.0F, 0.0F, 0.9163F);
				leftArm_r11.cubeList.add(new ModelBox(leftArm_r11, 0, 21, -8.3F, -2.1098F, -0.55F, 5, 5, 0, 0.4F, false));
		
				leftArm_r12 = new ModelRenderer(this);
				leftArm_r12.setRotationPoint(-18.6583F, -10.7756F, 0.0F);
				rightBlade.addChild(leftArm_r12);
				setRotationAngle(leftArm_r12, 0.0F, 0.0F, 0.7418F);
				leftArm_r12.cubeList.add(new ModelBox(leftArm_r12, 83, 84, -4.5F, -2.1098F, -0.6F, 7, 5, 0, 0.45F, false));
		
				leftArm_r13 = new ModelRenderer(this);
				leftArm_r13.setRotationPoint(-11.7F, -9.0F, 0.0F);
				rightBlade.addChild(leftArm_r13);
				setRotationAngle(leftArm_r13, 0.0F, 0.0F, 0.0873F);
				leftArm_r13.cubeList.add(new ModelBox(leftArm_r13, 45, 73, -6.3F, -3.1098F, -0.6F, 6, 6, 0, 0.5F, false));
		
				rightHammer = new ModelRenderer(this);
				rightHammer.setRotationPoint(10.0F, 19.0F, 0.0F);
				right4rm.addChild(rightHammer);
				
		
				leftArm_r14 = new ModelRenderer(this);
				leftArm_r14.setRotationPoint(0.0F, 0.0F, 0.0F);
				rightHammer.addChild(leftArm_r14);
				setRotationAngle(leftArm_r14, 0.0F, 0.0F, -0.0175F);
				leftArm_r14.cubeList.add(new ModelBox(leftArm_r14, 0, 89, -12.3F, -12.1066F, -7.2F, 7, 7, 1, 0.5F, false));
				leftArm_r14.cubeList.add(new ModelBox(leftArm_r14, 88, 65, -12.3F, -12.1066F, 4.8F, 7, 7, 1, 0.5F, false));
				leftArm_r14.cubeList.add(new ModelBox(leftArm_r14, 71, 20, -11.3F, -11.1066F, -5.2F, 5, 5, 9, 0.5F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(7.2F, 0.4F, 0.0F);
				bipedBody.addChild(bipedLeftArm);
				
		
				leftArm = new ModelRenderer(this);
				leftArm.setRotationPoint(0.0F, -1.0F, 0.0F);
				bipedLeftArm.addChild(leftArm);
				setRotationAngle(leftArm, -0.1745F, 0.2618F, -0.4363F);
				leftArm.cubeList.add(new ModelBox(leftArm, 0, 61, -1.3905F, -2.0845F, -4.2F, 7, 13, 7, 0.0F, true));
		
				spike2 = new ModelRenderer(this);
				spike2.setRotationPoint(4.0F, -1.1F, 0.5F);
				leftArm.addChild(spike2);
				setRotationAngle(spike2, 1.3519F, 0.4689F, 0.6415F);
				
		
				body_r187 = new ModelRenderer(this);
				body_r187.setRotationPoint(-0.2F, -1.093F, 0.7473F);
				spike2.addChild(body_r187);
				setRotationAngle(body_r187, -0.1302F, 0.1718F, -0.0528F);
				body_r187.cubeList.add(new ModelBox(body_r187, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r188 = new ModelRenderer(this);
				body_r188.setRotationPoint(0.8F, -1.093F, 0.7473F);
				spike2.addChild(body_r188);
				setRotationAngle(body_r188, -0.1302F, -0.1744F, -0.0077F);
				body_r188.cubeList.add(new ModelBox(body_r188, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r189 = new ModelRenderer(this);
				body_r189.setRotationPoint(-0.2F, -0.093F, 0.7473F);
				spike2.addChild(body_r189);
				setRotationAngle(body_r189, 0.2185F, 0.1665F, 0.0677F);
				body_r189.cubeList.add(new ModelBox(body_r189, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				body_r190 = new ModelRenderer(this);
				body_r190.setRotationPoint(0.8F, -0.093F, 0.7473F);
				spike2.addChild(body_r190);
				setRotationAngle(body_r190, 0.2188F, -0.1744F, -0.0077F);
				body_r190.cubeList.add(new ModelBox(body_r190, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, 0.0F, false));
		
				spike34 = new ModelRenderer(this);
				spike34.setRotationPoint(0.2789F, -0.4551F, 2.6757F);
				spike2.addChild(spike34);
				setRotationAngle(spike34, 0.3575F, -0.1294F, 0.0324F);
				
		
				body_r191 = new ModelRenderer(this);
				body_r191.setRotationPoint(-0.4F, -0.9919F, -0.0215F);
				spike34.addChild(body_r191);
				setRotationAngle(body_r191, -0.1302F, 0.1718F, -0.0528F);
				body_r191.cubeList.add(new ModelBox(body_r191, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r192 = new ModelRenderer(this);
				body_r192.setRotationPoint(0.3F, -0.9919F, -0.0215F);
				spike34.addChild(body_r192);
				setRotationAngle(body_r192, -0.1302F, -0.1744F, -0.0077F);
				body_r192.cubeList.add(new ModelBox(body_r192, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r193 = new ModelRenderer(this);
				body_r193.setRotationPoint(-0.4F, 0.0081F, -0.0215F);
				spike34.addChild(body_r193);
				setRotationAngle(body_r193, 0.2185F, 0.1665F, 0.0677F);
				body_r193.cubeList.add(new ModelBox(body_r193, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				body_r194 = new ModelRenderer(this);
				body_r194.setRotationPoint(0.4F, 0.0081F, -0.0215F);
				spike34.addChild(body_r194);
				setRotationAngle(body_r194, 0.2188F, -0.1744F, -0.0077F);
				body_r194.cubeList.add(new ModelBox(body_r194, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.4F, false));
		
				spike35 = new ModelRenderer(this);
				spike35.setRotationPoint(-0.1F, 0.0F, 1.6F);
				spike34.addChild(spike35);
				setRotationAngle(spike35, 0.2793F, -0.095F, -0.0483F);
				
		
				body_r195 = new ModelRenderer(this);
				body_r195.setRotationPoint(-0.2F, -0.8919F, -0.0215F);
				spike35.addChild(body_r195);
				setRotationAngle(body_r195, -0.1327F, 0.2583F, -0.0645F);
				body_r195.cubeList.add(new ModelBox(body_r195, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r196 = new ModelRenderer(this);
				body_r196.setRotationPoint(0.2F, -0.8919F, -0.0215F);
				spike35.addChild(body_r196);
				setRotationAngle(body_r196, -0.1314F, -0.2176F, -0.0019F);
				body_r196.cubeList.add(new ModelBox(body_r196, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r197 = new ModelRenderer(this);
				body_r197.setRotationPoint(0.0043F, -0.0543F, 0.6131F);
				spike35.addChild(body_r197);
				setRotationAngle(body_r197, 0.1809F, -0.1241F, 0.8093F);
				body_r197.cubeList.add(new ModelBox(body_r197, 94, 122, -1.0F, -1.0F, -1.0F, 2, 2, 3, -0.8F, false));
		
				body_r198 = new ModelRenderer(this);
				body_r198.setRotationPoint(-0.2F, -0.4919F, -0.0215F);
				spike35.addChild(body_r198);
				setRotationAngle(body_r198, 0.2226F, 0.2517F, 0.0872F);
				body_r198.cubeList.add(new ModelBox(body_r198, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				body_r199 = new ModelRenderer(this);
				body_r199.setRotationPoint(0.2F, -0.4919F, -0.0215F);
				spike35.addChild(body_r199);
				setRotationAngle(body_r199, 0.2188F, -0.1744F, -0.0077F);
				body_r199.cubeList.add(new ModelBox(body_r199, 94, 122, -1.0F, -0.5F, -1.5F, 2, 2, 3, -0.6F, false));
		
				left4rm = new ModelRenderer(this);
				left4rm.setRotationPoint(2.3095F, 10.5252F, 0.0F);
				leftArm.addChild(left4rm);
				setRotationAngle(left4rm, -0.2618F, 0.0F, 0.0873F);
				left4rm.cubeList.add(new ModelBox(left4rm, 40, 53, -3.7F, -0.1098F, -4.2F, 7, 13, 7, 0.5F, true));
		
				rightArm_r1 = new ModelRenderer(this);
				rightArm_r1.setRotationPoint(-10.0F, 19.0F, 0.0F);
				left4rm.addChild(rightArm_r1);
				setRotationAngle(rightArm_r1, 0.0F, 0.0F, 0.2182F);
				rightArm_r1.cubeList.add(new ModelBox(rightArm_r1, 74, 0, 4.5F, -8.5098F, -4.2F, 7, 3, 7, 0.45F, true));
		
				leftBlade = new ModelRenderer(this);
				leftBlade.setRotationPoint(-10.5F, 19.5F, 0.0F);
				left4rm.addChild(leftBlade);
				
		
				rightArm_r2 = new ModelRenderer(this);
				rightArm_r2.setRotationPoint(25.104F, -21.2064F, -0.1F);
				leftBlade.addChild(rightArm_r2);
				setRotationAngle(rightArm_r2, 0.0F, 0.0F, -1.7453F);
				rightArm_r2.cubeList.add(new ModelBox(rightArm_r2, 0, 0, 9.8F, 1.8902F, -0.5F, 2, 1, 0, 0.316F, true));
				rightArm_r2.cubeList.add(new ModelBox(rightArm_r2, 0, 0, 9.5665F, 1.3612F, -0.5F, 6, 0, 0, 0.316F, true));
		
				rightArm_r3 = new ModelRenderer(this);
				rightArm_r3.setRotationPoint(24.4725F, -21.2275F, -0.4F);
				leftBlade.addChild(rightArm_r3);
				setRotationAngle(rightArm_r3, 0.0F, 0.0F, -2.138F);
				rightArm_r3.cubeList.add(new ModelBox(rightArm_r3, 0, 0, 8.6F, 7.8902F, -0.2F, 5, 0, 0, 0.32F, true));
				rightArm_r3.cubeList.add(new ModelBox(rightArm_r3, 0, 0, 9.3F, 7.3902F, -0.2F, 3, 0, 0, 0.318F, true));
				rightArm_r3.cubeList.add(new ModelBox(rightArm_r3, 0, 0, 8.2F, 6.8902F, -0.2F, 3, 1, 0, 0.316F, true));
		
				rightArm_r4 = new ModelRenderer(this);
				rightArm_r4.setRotationPoint(24.5725F, -21.3275F, -0.4F);
				leftBlade.addChild(rightArm_r4);
				setRotationAngle(rightArm_r4, 0.0F, 0.0F, -1.9199F);
				rightArm_r4.cubeList.add(new ModelBox(rightArm_r4, 0, 0, 5.6F, 4.8902F, -0.2F, 4, 1, 0, 0.322F, true));
		
				rightArm_r5 = new ModelRenderer(this);
				rightArm_r5.setRotationPoint(25.144F, -20.7533F, -0.1F);
				leftBlade.addChild(rightArm_r5);
				setRotationAngle(rightArm_r5, 0.0F, 0.0F, -1.7017F);
				rightArm_r5.cubeList.add(new ModelBox(rightArm_r5, 0, 0, 5.7F, 0.8902F, -0.5F, 5, 2, 0, 0.318F, true));
		
				rightArm_r6 = new ModelRenderer(this);
				rightArm_r6.setRotationPoint(24.2813F, -21.0108F, -0.3F);
				leftBlade.addChild(rightArm_r6);
				setRotationAngle(rightArm_r6, 0.0F, 0.0F, -1.6581F);
				rightArm_r6.cubeList.add(new ModelBox(rightArm_r6, 0, 0, 2.7F, 3.6902F, -0.3F, 4, 1, 0, 0.34F, true));
		
				rightArm_r7 = new ModelRenderer(this);
				rightArm_r7.setRotationPoint(25.244F, -20.6533F, -0.1F);
				leftBlade.addChild(rightArm_r7);
				setRotationAngle(rightArm_r7, 0.0F, 0.0F, -1.3963F);
				rightArm_r7.cubeList.add(new ModelBox(rightArm_r7, 0, 0, 2.7F, -1.2098F, -0.5F, 4, 3, 0, 0.34F, true));
		
				rightArm_r8 = new ModelRenderer(this);
				rightArm_r8.setRotationPoint(25.244F, -20.6533F, -0.1F);
				leftBlade.addChild(rightArm_r8);
				setRotationAngle(rightArm_r8, 0.0F, 0.0F, -1.5272F);
				rightArm_r8.cubeList.add(new ModelBox(rightArm_r8, 0, 0, 2.7F, -0.2098F, -0.5F, 4, 3, 0, 0.338F, true));
		
				rightArm_r9 = new ModelRenderer(this);
				rightArm_r9.setRotationPoint(25.3057F, -19.7964F, -0.65F);
				leftBlade.addChild(rightArm_r9);
				setRotationAngle(rightArm_r9, 0.0F, 0.0F, -1.4399F);
				rightArm_r9.cubeList.add(new ModelBox(rightArm_r9, 12, 46, -3.3F, 2.2902F, -0.45F, 8, 1, 1, -0.1F, true));
		
				rightArm_r10 = new ModelRenderer(this);
				rightArm_r10.setRotationPoint(25.2962F, -20.5193F, -0.05F);
				leftBlade.addChild(rightArm_r10);
				setRotationAngle(rightArm_r10, 0.0F, 0.0F, -1.3963F);
				rightArm_r10.cubeList.add(new ModelBox(rightArm_r10, 0, 0, -3.3F, -1.3098F, -0.55F, 6, 4, 0, 0.35F, true));
		
				rightArm_r11 = new ModelRenderer(this);
				rightArm_r11.setRotationPoint(20.1887F, -11.3593F, -0.05F);
				leftBlade.addChild(rightArm_r11);
				setRotationAngle(rightArm_r11, 0.0F, 0.0F, -0.9163F);
				rightArm_r11.cubeList.add(new ModelBox(rightArm_r11, 0, 21, 3.3F, -2.1098F, -0.55F, 5, 5, 0, 0.4F, true));
		
				rightArm_r12 = new ModelRenderer(this);
				rightArm_r12.setRotationPoint(18.6583F, -10.7756F, 0.0F);
				leftBlade.addChild(rightArm_r12);
				setRotationAngle(rightArm_r12, 0.0F, 0.0F, -0.7418F);
				rightArm_r12.cubeList.add(new ModelBox(rightArm_r12, 83, 84, -2.5F, -2.1098F, -0.6F, 7, 5, 0, 0.45F, true));
		
				rightArm_r13 = new ModelRenderer(this);
				rightArm_r13.setRotationPoint(11.7F, -9.0F, 0.0F);
				leftBlade.addChild(rightArm_r13);
				setRotationAngle(rightArm_r13, 0.0F, 0.0F, -0.0873F);
				rightArm_r13.cubeList.add(new ModelBox(rightArm_r13, 45, 73, 0.3F, -3.1098F, -0.6F, 6, 6, 0, 0.5F, true));
		
				leftHammer = new ModelRenderer(this);
				leftHammer.setRotationPoint(-10.0F, 19.0F, 0.0F);
				left4rm.addChild(leftHammer);
				
		
				rightArm_r14 = new ModelRenderer(this);
				rightArm_r14.setRotationPoint(0.0F, 0.0F, 0.0F);
				leftHammer.addChild(rightArm_r14);
				setRotationAngle(rightArm_r14, 0.0F, 0.0F, 0.0175F);
				rightArm_r14.cubeList.add(new ModelBox(rightArm_r14, 0, 89, 5.3F, -12.1066F, -7.2F, 7, 7, 1, 0.5F, true));
				rightArm_r14.cubeList.add(new ModelBox(rightArm_r14, 88, 65, 5.3F, -12.1066F, 4.8F, 7, 7, 1, 0.5F, true));
				rightArm_r14.cubeList.add(new ModelBox(rightArm_r14, 71, 20, 6.3F, -11.1066F, -5.2F, 5, 5, 9, 0.5F, true));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-4.28F, 7.6F, 0.0F);
				
		
				rightThigh = new ModelRenderer(this);
				rightThigh.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightLeg.addChild(rightThigh);
				setRotationAngle(rightThigh, 0.1309F, 0.0F, 0.0F);
				
		
				rightLeg = new ModelRenderer(this);
				rightLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
				rightThigh.addChild(rightLeg);
				setRotationAngle(rightLeg, -0.1745F, 0.2618F, 0.0F);
				
		
				rightLeg_r1 = new ModelRenderer(this);
				rightLeg_r1.setRotationPoint(-0.22F, 8.4F, 0.0F);
				rightLeg.addChild(rightLeg_r1);
				setRotationAngle(rightLeg_r1, 0.0436F, 0.0F, 0.0F);
				rightLeg_r1.cubeList.add(new ModelBox(rightLeg_r1, 52, 78, -3.0F, -0.65F, -4.45F, 6, 9, 6, -0.4F, false));
		
				rightLeg_r2 = new ModelRenderer(this);
				rightLeg_r2.setRotationPoint(0.28F, 1.4F, 0.0F);
				rightLeg.addChild(rightLeg_r2);
				setRotationAngle(rightLeg_r2, -0.2618F, 0.0F, 0.0F);
				rightLeg_r2.cubeList.add(new ModelBox(rightLeg_r2, 27, 73, -3.5F, -1.35F, -2.4F, 6, 9, 6, 0.2F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(4.28F, 7.6F, 0.0F);
				
		
				leftThigh = new ModelRenderer(this);
				leftThigh.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftLeg.addChild(leftThigh);
				setRotationAngle(leftThigh, 0.1309F, 0.0F, 0.0F);
				
		
				leftLeg = new ModelRenderer(this);
				leftLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
				leftThigh.addChild(leftLeg);
				setRotationAngle(leftLeg, -0.1745F, -0.2618F, 0.0F);
				
		
				leftLeg_r1 = new ModelRenderer(this);
				leftLeg_r1.setRotationPoint(0.22F, 8.4F, 0.0F);
				leftLeg.addChild(leftLeg_r1);
				setRotationAngle(leftLeg_r1, 0.0436F, 0.0F, 0.0F);
				leftLeg_r1.cubeList.add(new ModelBox(leftLeg_r1, 52, 78, -3.0F, -0.65F, -4.45F, 6, 9, 6, -0.4F, true));
		
				leftLeg_r2 = new ModelRenderer(this);
				leftLeg_r2.setRotationPoint(-0.28F, 1.4F, 0.0F);
				leftLeg.addChild(leftLeg_r2);
				setRotationAngle(leftLeg_r2, -0.2618F, 0.0F, 0.0F);
				leftLeg_r2.cubeList.add(new ModelBox(leftLeg_r2, 27, 73, -2.5F, -1.35F, -2.4F, 6, 9, 6, 0.2F, true));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				bipedHead.render(f5);
				bipedHeadwear.render(f5);
				bipedBody.render(f5);
				bipedRightLeg.render(f5);
				bipedLeftLeg.render(f5);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}

			@Override
		    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		        limbSwing *= 2.0F / entityIn.height;
		        this.bipedHead.rotateAngleY = netHeadYaw * 0.017453292F;		
	            this.bipedHead.rotateAngleX = headPitch * 0.017453292F;		
		        this.bipedBody.rotateAngleY = 0.0F;
		        this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		        this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		        this.bipedRightArm.rotateAngleZ = 0.0F;
		        this.bipedLeftArm.rotateAngleZ = 0.0F;
		        this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		        this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		        this.bipedRightArm.rotateAngleY = 0.0F;
		        this.bipedRightArm.rotateAngleZ = 0.0F;		
		        if (this.swingProgress > 0.0F) {
		            EnumHandSide enumhandside = this.getMainHand(entityIn);
		            ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
		            float f1 = this.swingProgress;
		            this.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float)Math.PI * 2F)) * 0.2F;
		            if (enumhandside == EnumHandSide.LEFT) {
		                this.bipedBody.rotateAngleY *= -1.0F;
		            }
		            f1 = 1.0F - this.swingProgress;
		            f1 = f1 * f1;
		            f1 = f1 * f1;
		            f1 = 1.0F - f1;
		            float f2 = MathHelper.sin(f1 * (float)Math.PI);
		            float f3 = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;
		            modelrenderer.rotateAngleX = (float)((double)modelrenderer.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
		            modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY;
		            modelrenderer.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
		        }
		        this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		        this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		        this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		        this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		        copyModelAngles(this.bipedHead, this.bipedHeadwear);
		        this.jaw.rotateAngleX = ((EntitySplit)entityIn).isShooting() ? 0.3054F : 0.0F;
		    }
		}
	}
}
