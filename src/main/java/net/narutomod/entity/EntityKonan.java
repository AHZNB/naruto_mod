
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodModVariables;
import net.narutomod.Chakra;
import net.narutomod.ModConfig;
import net.narutomod.Particles;
import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.init.Biomes;
import net.minecraft.init.SoundEvents;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityKonan extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 482;
	public static final int ENTITYID_RANGED = 483;

	public EntityKonan(ElementsNarutomodMod instance) {
		super(instance, 907);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "konan"), ENTITYID).name("konan").tracker(64, 3, true).egg(-16777216, -10079233).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityReplacementClone.class)
		 .id(new ResourceLocation("narutomod", "konan_clone"), ENTITYID_RANGED).name("konan_clone").tracker(64, 3, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		int i = MathHelper.clamp(ModConfig.SPAWN_WEIGHT_KONAN, 0, 20);
		if (i > 0) {
			EntityRegistry.addSpawn(EntityCustom.class, i, 1, 1, EnumCreatureType.MONSTER, this.spawnBiomes(Biome.REGISTRY));
		}
	}

	private Biome[] spawnBiomes(net.minecraft.util.registry.RegistryNamespaced<ResourceLocation, Biome> in) {
		Iterator<Biome> itr = in.iterator();
		ArrayList<Biome> ls = new ArrayList<Biome>();
		while (itr.hasNext()) {
			Biome biome = itr.next();
			if (biome != Biomes.HELL && biome != Biomes.VOID) {
				ls.add(biome);
			}
		}
		return ls.toArray(new Biome[ls.size()]);
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob, EntityFlying {
		private EntityShikigami.EC wingsEntity;
		private final EntityMoveHelper walkHelper;
		private final EntityClone.AIFlyControl flyHelper;
		private final PathNavigate walkNavigator;
		private final PathNavigateFlying flyNavigator;
		private int shootingTicks;
		private int bindingTicks;
		private final int bindingCD = 600;
		private int lastBindingTime;

		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.setSize(0.525f, 1.75f);
			this.walkHelper = this.moveHelper;
			this.flyHelper = new EntityClone.AIFlyControl(this);
			this.flyHelper.setFacingAttackTarget(true);
			this.walkNavigator = this.navigator;
			this.flyNavigator = new PathNavigateFlying(this, world);
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelectorAkatsuki));
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobe.body));
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.6D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new AIAttackRangedFlying(this, 120, 8.0f, 12.0f, 6.0f, 10.0f));
			this.tasks.addTask(4, new EntityClone.AIFollowSummoner(this, 0.6d, 4f) {
				@Override @Nullable
				protected EntityLivingBase getFollowEntity() {
					return (EntityLivingBase)EntityCustom.this.world.findNearestEntityWithinAABB(EntityNagato.EntityCustom.class,
					 EntityCustom.this.getEntityBoundingBox().grow(256d, 16d, 256d), EntityCustom.this);
				}
			});
			this.tasks.addTask(5, new EntityAIWatchClosest2(this, EntityPlayer.class, 32.0F, 1.0F));
			this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityNinjaMob.Base.class, 24.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !this.entity.isOnSameTeam(this.closestEntity);
				}
			});
			this.tasks.addTask(7, new EntityAIWander(this, 0.5d));
			this.tasks.addTask(8, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive()) {
				if (!this.isWinged()) {
					this.wingsEntity = EntityShikigami.EC.Jutsu.createJutsu(this, 20.0d);
					if (this.wingsEntity != null) {
						this.moveHelper = this.flyHelper;
						this.navigator = this.flyNavigator;
					} else {
						this.resetDefaultHelpers();
					}
				}
			} else if (this.isWinged()) {
				this.wingsEntity.setDead();
				this.resetDefaultHelpers();
			}
			this.getEntityData().setBoolean(NarutomodModVariables.JutsuKey1Pressed, --this.shootingTicks >= 0);
			this.getEntityData().setBoolean(NarutomodModVariables.JutsuKey2Pressed, --this.bindingTicks >= 0);
		}

		private void resetDefaultHelpers() {
			if (this.moveHelper != this.walkHelper) {
				this.moveHelper = this.walkHelper;
			}
			if (this.navigator != this.walkNavigator) {
				this.navigator = this.walkNavigator;
				this.navigator.clearPath();
			}
			if (this.hasNoGravity()) {
				this.setNoGravity(false);
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.FALL) {
				return false;
			}
			if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F && amount <= this.lastDamage) {
				return false;
			}
			Entity attacker = source.getTrueSource();
			if (!this.world.isRemote && !this.isAIDisabled() && source != ProcedureUtils.SPECIAL_DAMAGE
			 && attacker instanceof EntityLivingBase && this.rand.nextInt(4) != 0
			 && Chakra.pathway(this).consume(ItemNinjutsu.REPLACEMENT.chakraUsage)) {
				this.setRevengeTarget((EntityLivingBase)attacker);
				ProcedureOnLivingUpdate.setUntargetable(this, 5);
				EntityReplacementClone clone = new EntityReplacementClone(this, attacker);
				this.world.spawnEntity(clone);
				clone.attackEntityFrom(source, amount);
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		private boolean isWinged() {
			return this.wingsEntity != null && this.wingsEntity.isEntityAlive();
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			if (this.isWinged()) {
				if (!this.wingsEntity.isBinding() && this.rand.nextFloat() < 0.333f) {
					this.bindingTicks = 2;
				} else if (!this.wingsEntity.isShooting()) {
					this.shootingTicks = 60;
				}
			}
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere() && (int)this.posY >= this.world.getSeaLevel() && this.world.canSeeSky(this.getPosition())
			 && this.world.getEntities(EntityCustom.class, EntitySelectors.IS_ALIVE).isEmpty()
			 && !EntityNinjaMob.SpawnData.spawnedRecentlyHere(this, 36000)
			 && this.world.isRaining();
		}

		@Override
		public boolean isNonBoss() {
			return false;
		}
		
		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
		
		@Override
		public void removeTrackingPlayer(EntityPlayerMP player) {
			super.removeTrackingPlayer(player);
			if (this.bossInfo.getPlayers().contains(player)) {
				this.bossInfo.removePlayer(player);
			}
		}

		private void trackAttackedPlayers() {
			Entity entity = this.getAttackingEntity();
			if (entity instanceof EntityPlayerMP || (entity = this.getAttackTarget()) instanceof EntityPlayerMP) {
				this.bossInfo.addPlayer((EntityPlayerMP)entity);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.trackAttackedPlayers();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			if (this.world.isRemote && !this.getEntityData().getBoolean("slimModel")) {
				this.getEntityData().setBoolean("slimModel", true);
			}
		}
	}

	public static class EntityReplacementClone extends ItemNinjutsu.EntityReplacementClone {
		private static final DataParameter<Boolean> IGNITED = EntityDataManager.<Boolean>createKey(EntityReplacementClone.class, DataSerializers.BOOLEAN);
		private final int fuse = 30;
		private int ignitionTime;
		private boolean exploded;

		public EntityReplacementClone(World world) {
			super(world);
		}

		public EntityReplacementClone(EntityLivingBase player, Entity attacker) {
			super(player, attacker);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(player.getMaxHealth());
			this.setHealth(player.getHealth());
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(IGNITED, Boolean.valueOf(false));
		}

		private void ignite() {
			if (!this.world.isRemote) {
				this.dataManager.set(IGNITED, Boolean.valueOf(true));
			}
			this.ignitionTime = this.ticksExisted;
			this.lifeSpan += this.fuse + 1;
		}

		private boolean ignited() {
			return ((Boolean)this.dataManager.get(IGNITED)).booleanValue();
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (IGNITED.equals(key) && this.world.isRemote) {
				this.ignite();
			}
		}

		@Override
		protected void onSetDead() {
			if (!this.world.isRemote && !this.exploded) {
				this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:paperflip")), 0.6f, 0.8f);
				Particles.Renderer particles = new Particles.Renderer(this.world);
				for (int i = 0; i < 200; i++) {
					Vec3d vec = ProcedureUtils.BB.randomPosOnBB(this.getEntityBoundingBox());
					particles.spawnParticles(Particles.Types.PAPER, vec.x, vec.y, vec.z, 1, 0, 0, 0,
					 (this.rand.nextFloat()-0.5f) * 0.3f, 0.0f, (this.rand.nextFloat()-0.5f) * 0.3f);
				}
				particles.send();
			}
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			if (!this.ignited()) {
				this.ignite();
			}
			return super.attackEntityAsMob(entityIn);
		}

		private void explode() {
	    	if (!this.world.isRemote) {
		    	this.dead = true;
		    	this.exploded = true;
		    	EntityLivingBase summoner = this.getSummoner();
		    	boolean grief = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, summoner);
				this.world.newExplosion(summoner, this.posX, this.posY, this.posZ, 8f, grief, grief);
		    	ProcedureAoeCommand.set(this, 0d, 8d)
		    	 .damageEntitiesCentered(ItemJutsu.causeJutsuDamage(this, summoner), 25f + this.rand.nextFloat() * 10f);
	    		this.setDead();
	    	}
		}

		@Override
		public void onUpdate() {
			if (this.isEntityAlive() && this.ignited()) {
				Vec3d vec = ProcedureUtils.BB.randomPosOnBB(this.getEntityBoundingBox());
				Particles.spawnParticle(this.world, Particles.Types.PAPER, vec.x, vec.y, vec.z, 1, 0, 0, 0,
				 (this.rand.nextFloat()-0.5f) * 0.3f, 0.0f, (this.rand.nextFloat()-0.5f) * 0.3f);
				if (this.ignitionTime + 1 == this.ticksExisted) {
					this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
				} else if (this.ticksExisted - this.ignitionTime > this.fuse) {
					this.explode();
				}
			}
			super.onUpdate();
		}

		public float getIgnitionProgress(float partialTick) {
			return ((float)this.ticksExisted + partialTick - this.ignitionTime) / (float)(this.fuse - 2);
		}
	}

	public static class AIAttackRangedFlying<T extends EntityLiving & IRangedAttackMob & EntityFlying> extends EntityAIBase {
	    protected final T entity;
	    private final int attackCooldown;
	    private final float minRadius;
	    private final float maxRadius;
	    private final float minHeight;
	    private final float maxHeight;
	    private int attackTime;

	    public AIAttackRangedFlying(T entityIn, int cooldown, float minXYdistance, float maxXYDistance, float minHeightIn, float maxHeightIn) {
	        this.entity = entityIn;
	        this.attackCooldown = cooldown;
	        this.attackTime = cooldown;
	        this.minRadius = minXYdistance;
	        this.maxRadius = maxXYDistance;
	        this.minHeight = minHeightIn;
	        this.maxHeight = maxHeightIn;
	        this.setMutexBits(3);
	    }		

	    @Override
	    public boolean shouldExecute() {
			return this.entity.getAttackTarget() != null;
		}

	    @Override
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
	        this.attackTime = 0;
	    }

	    @Override
	    public void updateTask() {
	        EntityLivingBase target = this.entity.getAttackTarget();
	        if (target != null) {
				if (!this.isInRange(true, this.entity.posX, this.entity.posY, this.entity.posZ, target)) {
	            	this.setNewPath(target);
	            }
	            if (--this.attackTime <= 0 && this.isInRange(false, this.entity.posX, this.entity.posY, this.entity.posZ, target)) {
					double dx = this.entity.posX - target.posX;
					double dz = this.entity.posZ - target.posZ;
					float f = (float)MathHelper.sqrt(dx * dx + dz * dz) / this.maxRadius;
		            this.entity.attackEntityWithRangedAttack(target, MathHelper.clamp(f, 0.1F, 1.0F));
		            this.attackTime = MathHelper.floor(f * (float)(this.attackCooldown));
	            }
	        }
	    }

	    private boolean isInRange(boolean forFlight, double x, double y, double z, EntityLivingBase target) {
			double dx = x - target.posX;
			double dy = y - target.posY;
			double dz = z - target.posZ;
			double dxz = MathHelper.sqrt(dx * dx + dz * dz);
            return (!forFlight || (float)dxz >= this.minRadius) && (float)dxz <= this.maxRadius
             && (!forFlight || (float)dy >= this.minHeight) && (float)dy <= this.maxHeight
             && target.world.rayTraceBlocks(new Vec3d(x, y + this.entity.getEyeHeight(), z), target.getPositionEyes(1f), false, true, false) == null;
	    }

	    private boolean setNewPath(EntityLivingBase target) {
			List<BlockPos> list = ProcedureUtils.getAllAirBlocks(target.world, target.getEntityBoundingBox()
			 .grow(this.maxRadius, 0, this.maxRadius).expand(0, this.maxHeight, 0).contract(0, -target.height, 0));
			list.sort(new ProcedureUtils.BlockposSorter(this.entity.getPosition()));
			for (BlockPos pos : list) {
				if (this.isInRange(true, 0.5d + pos.getX(), pos.getY(), 0.5d + pos.getZ(), target)
				 && this.entity.getNavigator().setPath(this.entity.getNavigator().getPathToPos(pos), 1.0d)) {
					return true;
				}
			}
			return false;
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntityReplacementClone.class, renderManager -> new RenderClone(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/konan.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelKonan());
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				float f = 0.0625f * 14;
				GlStateManager.scale(f, f, f);
			}

			@Override
			public void transformHeldFull3DItemLayer() {
				GlStateManager.translate(0.0F, 0.1875F, 0.0F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderClone extends EntityClone.ClientRLM.RenderClone<EntityReplacementClone> {
			public RenderClone(RenderManager renderManagerIn) {
				EntityClone.ClientRLM.getInstance().super(renderManagerIn);
			}
	
		    @Override
		    protected void preRenderCallback(EntityReplacementClone entity, float partialTick) {
		    	super.preRenderCallback(entity, partialTick);
		    	if (entity.ignited()) {
			        float f = entity.getIgnitionProgress(partialTick);
			        float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
			        f = MathHelper.clamp(f, 0.0F, 1.0F);
			        f = f * f;
			        f = f * f;
			        float f2 = (1.0F + f * 0.4F) * f1;
			        float f3 = (1.0F + f * 0.1F) / f1;
			        GlStateManager.scale(f2, f3, f2);
		    	}
		    }
	
		    @Override
		    protected int getColorMultiplier(EntityReplacementClone entity, float lightBrightness, float partialTick) {
		    	if (entity.ignited()) {
			        float f = entity.getIgnitionProgress(partialTick);
		            int i = MathHelper.clamp((int)((1f - f) * 255.0F), 1, 255);
		            return i << 24 | 0x00FFFFFF;
		    	}
		    	return 0;
		    }
		}

		// Made with Blockbench 4.12.1
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelKonan extends EntityNinjaMob.ModelNinja {
			public ModelKonan() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.01F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, 0.0F, -4.0F, 8, 2, 8, 0.01F, false));
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 24, 0, -1.5F, -9.0F, -0.5F, 3, 4, 3, 0.0F, false));
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 34, 51, -7.0F, -10.75F, -2.7F, 8, 8, 1, -2.5F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, true));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.2F, true));
			}
		}
	}
}
