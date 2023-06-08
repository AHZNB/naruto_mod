
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.block.Block;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.pathfinding.PathNavigate;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySlug extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 362;
	public static final int ENTITYID_RANGED = 363;

	public EntitySlug(ElementsNarutomodMod instance) {
		super(instance, 720);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "slug"), ENTITYID).name("slug").tracker(128, 3, true).egg(-1, 0xFF3C7593).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
	}

	public static class EntityCustom extends EntitySummonAnimal.Base implements IRangedAttackMob {
		private static final DataParameter<EnumFacing> CLIMBING = EntityDataManager.<EnumFacing>createKey(EntityCustom.class, DataSerializers.FACING);
		private final EntityAIWander aiWander = new EntityAIWander(this, 1.0, 50);
		private int climbingTime;

		public EntityCustom(World world) {
			super(world);
			this.setOGSize(0.75f, 0.75f);
			this.isImmuneToFire = true;
			this.enablePersistence();
			this.postScaleFixup();
			this.dontWander(false);
		}

		public EntityCustom(EntityLivingBase summonerIn, float scale) {
			super(summonerIn);
			this.setOGSize(0.75f, 0.75f);
			this.setScale(scale);
			this.isImmuneToFire = true;
			this.enablePersistence();
		}

		@Override
		public void entityInit() {
			super.entityInit();
			this.getDataManager().register(CLIMBING, EnumFacing.DOWN);
		}

		public void setClimbingSide(EnumFacing facing) {
			this.getDataManager().set(CLIMBING, facing);
		}

		protected EnumFacing getClimbingSide() {
			return (EnumFacing)this.getDataManager().get(CLIMBING);
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			return new PathNavigateClimber(this, worldIn);
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
	    protected void playStepSound(BlockPos pos, Block blockIn) {
	        this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15F, 1.0F);
	    }

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		protected void postScaleFixup() {
			float f = this.getScale();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5D * f);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D * f * f);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D + f * 0.05);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5D * f);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(13D + 3D * f);
			super.postScaleFixup();
			//this.setSize(this.ogWidth * f, this.ogHeight * f);
			//this.setHealth(this.getMaxHealth());
			this.experienceValue = (int)(f * 10);
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedTactical(this, 1.0, 100, 5.5f + 2.5f * f));
		}

		@Override
		public boolean shouldRiderSit() {
			return false;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			float f = this.getScale();
			Vec3d vec[] = { new Vec3d(0.0d, 0.0d, 0.2113d * f) };
			Vec3d vec1[] = { new Vec3d(0.0d, 0.0d, -0.25d * f) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				if (this.isOnLadder()) {
					Vec3d vec2 = vec1[i].rotateYaw(-this.renderYawOffset * 0.017453292F).add(this.getPositionVector());
					passenger.setPosition(vec2.x, vec2.y + 1.1875f * f, vec2.z);
				} else {
					float headYaw = (this.rotationYawHead - this.renderYawOffset) * 0.5f * 0.017453292F;
					Vec3d vec2 = vec[i].rotateYaw(-headYaw).addVector(0d, 0d, 0.3753f * f).rotateYaw(-headYaw)
					 .rotateYaw(-this.renderYawOffset * 0.017453292F).add(this.getPositionVector());
					passenger.setPosition(vec2.x, vec2.y + 0.875f * f, vec2.z);
				}
			}
		}

		@Override
		public boolean canSitOnShoulder() {
			return this.getScale() <= 0.6f;
		}

		@Override
		public boolean isOnLadder() {
			return this.getClimbingSide() != EnumFacing.DOWN;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(3, new EntityAILookIdle(this));
		}

		@Override
		protected void dontWander(boolean set) {
			if (!set) {
				this.tasks.addTask(2, this.aiWander);
			} else {
				this.tasks.removeTask(this.aiWander);
			}
		}

		@Override
		public boolean canAttackClass(Class <? extends EntityLivingBase > cls) {
			return !EntityCustom.class.isAssignableFrom(cls);
		}

		@Override
		public void setAttackTarget(@Nullable EntityLivingBase entityIn) {
			if (entityIn == null || !this.isOnSameTeam(entityIn)) {
				super.setAttackTarget(entityIn);
			}
		}

	    @Override
	    public boolean isOnSameTeam(Entity entityIn) {
	    	return entityIn instanceof EntityCustom || super.isOnSameTeam(entityIn);
	    }

		@Override
		protected void damageEntity(DamageSource damageSrc, float damageAmount) {
			super.damageEntity(damageSrc, damageAmount * 0.1f);
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			Vec3d vec = this.getPositionEyes(1.0f);
			Vec3d vec1 = target.getPositionEyes(1.0f).subtract(vec);
			double dist = vec1.lengthVector();
			vec1 = vec1.normalize();
			vec = vec.add(this.getLookVec().scale(this.getScale()));
			for (int i = 0; i < 300; i++) {
				Vec3d vec2 = vec1.scale(dist * 0.1d * (this.rand.nextDouble() * 0.8d + 0.2d));
				Particles.spawnParticle(this.world, Particles.Types.ACID_SPIT, vec.x, vec.y, vec.z, 1,
				 0d, 0d, 0d, vec2.x, vec2.y, vec2.z, this.getEntityId());
			}
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.fallDistance = 0.0f;
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner != null && !summoner.isRiding() && this.getAge() == 1 && this.getScale() >= 4.0f) {
					summoner.startRiding(this);
				}
				this.checkClimbing();
			}
		}

		private void checkClimbing() {
			EnumFacing side = EnumFacing.DOWN;
			if (this.collidedHorizontally) {
				if (this.climbingTime > (int)(this.getScale() * 4f)) {
					double d0 = 10000d;
		            AxisAlignedBB bb = this.getEntityBoundingBox();
		            for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(MathHelper.floor(bb.minX)-1,
		             MathHelper.floor(bb.minY), MathHelper.floor(bb.minZ)-1, MathHelper.floor(bb.maxX),
		             MathHelper.floor(bb.maxY), MathHelper.floor(bb.maxZ))) {
		             	AxisAlignedBB aabb = new AxisAlignedBB(pos);
	                   	if (aabb.maxY > bb.minY && this.world.getBlockState(pos).isFullBlock()) {
	                   		double d1 = this.getDistanceSqToCenter(pos);
	                   		if (d1 < d0) {
	                   			d0 = d1;
	                   			Vec3d vec = ProcedureUtils.BB.getCenter(aabb)
	                   			 .subtract(this.getPositionVector().addVector(0d, 0.5d * this.height, 0d));
	                   			side = EnumFacing.getFacingFromVector((float)vec.x, (float)vec.y, (float)vec.z);
	                   		}
		                }
		            }
				}
				++this.climbingTime;
			} else {
				this.climbingTime = 0;
			}
			this.setClimbingSide(side);
		}
	}

	public static class Jutsu implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			Particles.spawnParticle(entity.world, Particles.Types.SEAL_FORMULA,
			 entity.posX, entity.posY + 0.015d, entity.posZ, 1, 0d, 0d, 0d, 0d, 0d, 0d, (int)(power * 40), 0, 60);
			for (int i = 0; i < 500; i++) {
				Particles.spawnParticle(entity.world, Particles.Types.SMOKE,
				 entity.posX, entity.posY + 0.015d, entity.posZ, 1, 0d, 0d, 0d,
				 (entity.getRNG().nextDouble()-0.5d) * 0.8d, entity.getRNG().nextDouble() * 0.6d + 0.2d, (entity.getRNG().nextDouble()-0.5d) * 0.8d,
				 0xD0FFFFFF, (int)(power * 30));
			}
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
			  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:kuchiyosenojutsu"))),
			  net.minecraft.util.SoundCategory.PLAYERS, 1f, 0.8f);
			EntityCustom entity1 = new EntityCustom(entity, power);
			entity1.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0f);
			net.narutomod.event.SpecialEvent.setDelayedSpawnEvent(entity.world, entity1, 0, 0, 0, entity.world.getTotalWorldTime() + 20);
			return true;
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends RenderLiving<EntityCustom> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/slug.png");

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn, new ModelSlug(), 0.5f);
		}

		@Override
		public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
			ModelSlug model = (ModelSlug)this.mainModel;
			model.scale = entity.getScale();
			this.shadowSize = 0.5f * model.scale;
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return TEXTURE;
		}
	}

	// Made with Blockbench 4.2.2
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelSlug extends ModelBase {
		private final ModelRenderer body;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r2;
		private final ModelRenderer cube_r3;
		private final ModelRenderer cube_r4;
		private final ModelRenderer cube_r5;
		private final ModelRenderer cube_r6;
		private final ModelRenderer cube_r7;
		private final ModelRenderer bodyFront;
		private final ModelRenderer cube_r8;
		private final ModelRenderer cube_r9;
		private final ModelRenderer cube_r10;
		private final ModelRenderer cube_r11;
		private final ModelRenderer cube_r12;
		private final ModelRenderer cube_r13;
		private final ModelRenderer cube_r14;
		private final ModelRenderer cube_r15;
		private final ModelRenderer cube_r16;
		private final ModelRenderer cube_r17;
		private final ModelRenderer cube_r18;
		private final ModelRenderer cube_r19;
		private final ModelRenderer cube_r20;
		private final ModelRenderer cube_r21;
		private final ModelRenderer cube_r22;
		private final ModelRenderer cube_r23;
		private final ModelRenderer head;
		private final ModelRenderer cube_r24;
		private final ModelRenderer cube_r25;
		private final ModelRenderer cube_r26;
		private final ModelRenderer cube_r27;
		private final ModelRenderer cube_r28;
		private final ModelRenderer cube_r29;
		private final ModelRenderer cube_r30;
		private final ModelRenderer cube_r31;
		private final ModelRenderer cube_r32;
		private final ModelRenderer cube_r33;
		private final ModelRenderer cube_r34;
		private final ModelRenderer cube_r35;
		private final ModelRenderer cube_r36;
		private final ModelRenderer cube_r37;
		private final ModelRenderer cube_r38;
		private final ModelRenderer cube_r39;
		private final ModelRenderer bone3;
		private final ModelRenderer cube_r40;
		private final ModelRenderer cube_r41;
		private final ModelRenderer cube_r42;
		private final ModelRenderer cube_r43;
		private final ModelRenderer cube_r44;
		private final ModelRenderer cube_r45;
		private final ModelRenderer cube_r46;
		private final ModelRenderer cube_r47;
		private final ModelRenderer cube_r48;
		private final ModelRenderer cube_r49;
		private final ModelRenderer cube_r50;
		private final ModelRenderer cube_r51;
		private final ModelRenderer cube_r52;
		private final ModelRenderer cube_r53;
		private final ModelRenderer cube_r54;
		private final ModelRenderer cube_r55;
		private final ModelRenderer face;
		private final ModelRenderer cube_r56;
		private final ModelRenderer cube_r57;
		private final ModelRenderer cube_r58;
		private final ModelRenderer cube_r59;
		private final ModelRenderer HornRight;
		private final ModelRenderer Horn0_1;
		private final ModelRenderer Horn0_2;
		private final ModelRenderer Horn0_3;
		private final ModelRenderer eyeRight;
		private final ModelRenderer HornLeft;
		private final ModelRenderer Horn0_5;
		private final ModelRenderer Horn0_6;
		private final ModelRenderer Horn0_7;
		private final ModelRenderer eyeLeft;
		private final ModelRenderer HornRight2;
		private final ModelRenderer Horn0_9;
		private final ModelRenderer Horn0_10;
		private final ModelRenderer Horn0_11;
		private final ModelRenderer Horn0_12;
		private final ModelRenderer HornLeft2;
		private final ModelRenderer Horn0_13;
		private final ModelRenderer Horn0_14;
		private final ModelRenderer Horn0_15;
		private final ModelRenderer Horn0_16;
		private final ModelRenderer[] tail = new ModelRenderer[3];
		private final ModelRenderer cube_r60;
		private final ModelRenderer cube_r61;
		private final ModelRenderer cube_r62;
		private final ModelRenderer cube_r63;
		private final ModelRenderer cube_r64;
		private final ModelRenderer cube_r65;
		private final ModelRenderer cube_r66;
		private final ModelRenderer cube_r67;
		private final ModelRenderer cube_r68;
		private final ModelRenderer cube_r69;
		private final ModelRenderer cube_r70;
		private final ModelRenderer cube_r71;
		private final ModelRenderer cube_r72;
		private final ModelRenderer cube_r73;
		private final ModelRenderer cube_r74;
		private final ModelRenderer cube_r75;
		//private final ModelRenderer tail[1];
		private final ModelRenderer cube_r76;
		private final ModelRenderer cube_r77;
		private final ModelRenderer cube_r78;
		private final ModelRenderer cube_r79;
		private final ModelRenderer cube_r80;
		private final ModelRenderer cube_r81;
		private final ModelRenderer cube_r82;
		private final ModelRenderer cube_r83;
		private final ModelRenderer cube_r84;
		private final ModelRenderer cube_r85;
		private final ModelRenderer cube_r86;
		private final ModelRenderer cube_r87;
		private final ModelRenderer cube_r88;
		private final ModelRenderer cube_r89;
		private final ModelRenderer cube_r90;
		private final ModelRenderer cube_r91;
		//private final ModelRenderer tail[2];
		private final ModelRenderer cube_r92;
		private final ModelRenderer cube_r93;
		private final ModelRenderer cube_r94;
		private final ModelRenderer cube_r95;
		private final ModelRenderer cube_r96;
		private final ModelRenderer cube_r97;
		private final ModelRenderer cube_r98;
		private final ModelRenderer cube_r99;
		private final ModelRenderer cube_r100;
		private final ModelRenderer cube_r101;
		private final ModelRenderer cube_r102;
		private final ModelRenderer cube_r103;
		private final ModelRenderer cube_r104;
		private final ModelRenderer cube_r105;
		private final ModelRenderer cube_r106;
		private final ModelRenderer cube_r107;
		private float scale = 1.0f;

		public ModelSlug() {
			textureWidth = 128;
			textureHeight = 128;

			body = new ModelRenderer(this);
			body.setRotationPoint(0.0F, 18.0F, 0.0F);
			setRotationAngle(body, 1.5708F, 0.0F, 0.0F);
			body.cubeList.add(new ModelBox(body, 92, 12, -2.0F, -0.75F, -5.75F, 4, 12, 0, 0.12F, false));
	
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(0.0F, 10.75F, -0.75F);
			body.addChild(cube_r1);
			setRotationAngle(cube_r1, 3.1416F, 0.7854F, 3.1416F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 68, 5, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
	
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(0.0F, 10.75F, -0.75F);
			body.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.0F, -1.5708F, 0.0F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 76, 5, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
	
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.0F, 10.75F, -0.75F);
			body.addChild(cube_r3);
			setRotationAngle(cube_r3, 3.1416F, -0.7854F, 3.1416F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 84, 0, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
	
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(0.0F, 10.75F, -0.75F);
			body.addChild(cube_r4);
			setRotationAngle(cube_r4, 3.1416F, 0.0F, 3.1416F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 84, 12, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
	
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(0.0F, 10.75F, -0.75F);
			body.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.0F, -0.7854F, 0.0F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 68, 17, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
	
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(0.0F, 10.75F, -0.75F);
			body.addChild(cube_r6);
			setRotationAngle(cube_r6, 0.0F, 1.5708F, 0.0F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 76, 17, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
	
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(0.0F, 10.75F, -0.75F);
			body.addChild(cube_r7);
			setRotationAngle(cube_r7, 0.0F, 0.7854F, 0.0F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 92, 0, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
	
			bodyFront = new ModelRenderer(this);
			bodyFront.setRotationPoint(0.0F, 0.0F, -0.75F);
			body.addChild(bodyFront);
			setRotationAngle(bodyFront, -0.3927F, 0.0F, 0.0F);
			
	
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r8);
			setRotationAngle(cube_r8, -0.0471F, 2.7489F, 0.0F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 24, 0, -0.5F, -8.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r9);
			setRotationAngle(cube_r9, -0.0471F, -2.7489F, 0.0F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 24, 0, -0.5F, -8.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r10);
			setRotationAngle(cube_r10, -0.0471F, -1.9635F, 0.0F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 24, 0, -0.5F, -8.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r11 = new ModelRenderer(this);
			cube_r11.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r11);
			setRotationAngle(cube_r11, -0.0471F, -1.1781F, 0.0F);
			cube_r11.cubeList.add(new ModelBox(cube_r11, 24, 0, -0.5F, -8.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r12 = new ModelRenderer(this);
			cube_r12.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r12);
			setRotationAngle(cube_r12, -0.0471F, -0.3927F, 0.0F);
			cube_r12.cubeList.add(new ModelBox(cube_r12, 24, 0, -0.5F, -8.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r13 = new ModelRenderer(this);
			cube_r13.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r13);
			setRotationAngle(cube_r13, -0.0471F, 0.3927F, 0.0F);
			cube_r13.cubeList.add(new ModelBox(cube_r13, 24, 0, -0.5F, -8.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r14 = new ModelRenderer(this);
			cube_r14.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r14);
			setRotationAngle(cube_r14, -0.0471F, 1.1781F, 0.0F);
			cube_r14.cubeList.add(new ModelBox(cube_r14, 24, 0, -0.5F, -8.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r15 = new ModelRenderer(this);
			cube_r15.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r15);
			setRotationAngle(cube_r15, -0.0471F, 1.9635F, 0.0F);
			cube_r15.cubeList.add(new ModelBox(cube_r15, 24, 0, -0.5F, -8.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r16 = new ModelRenderer(this);
			cube_r16.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r16);
			setRotationAngle(cube_r16, 3.098F, 0.7854F, 3.1416F);
			cube_r16.cubeList.add(new ModelBox(cube_r16, 40, 36, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r17 = new ModelRenderer(this);
			cube_r17.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r17);
			setRotationAngle(cube_r17, -0.0436F, -1.5708F, 0.0F);
			cube_r17.cubeList.add(new ModelBox(cube_r17, 40, 27, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r18 = new ModelRenderer(this);
			cube_r18.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r18);
			setRotationAngle(cube_r18, 3.098F, -0.7854F, 3.1416F);
			cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 38, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r19 = new ModelRenderer(this);
			cube_r19.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r19);
			setRotationAngle(cube_r19, 3.098F, 0.0F, 3.1416F);
			cube_r19.cubeList.add(new ModelBox(cube_r19, 36, 18, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r20 = new ModelRenderer(this);
			cube_r20.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r20);
			setRotationAngle(cube_r20, -0.0436F, -0.7854F, 0.0F);
			cube_r20.cubeList.add(new ModelBox(cube_r20, 42, 0, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r21 = new ModelRenderer(this);
			cube_r21.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r21);
			setRotationAngle(cube_r21, -0.0436F, 1.5708F, 0.0F);
			cube_r21.cubeList.add(new ModelBox(cube_r21, 42, 9, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r22 = new ModelRenderer(this);
			cube_r22.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r22);
			setRotationAngle(cube_r22, -0.0436F, 0.7854F, 0.0F);
			cube_r22.cubeList.add(new ModelBox(cube_r22, 10, 42, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r23 = new ModelRenderer(this);
			cube_r23.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r23);
			setRotationAngle(cube_r23, -0.0436F, 0.0F, 0.0F);
			cube_r23.cubeList.add(new ModelBox(cube_r23, 20, 42, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			head = new ModelRenderer(this);
			head.setRotationPoint(0.0F, -6.5F, 0.0F);
			bodyFront.addChild(head);
			setRotationAngle(head, -0.2618F, 0.0F, 0.0F);
			
	
			cube_r24 = new ModelRenderer(this);
			cube_r24.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r24);
			setRotationAngle(cube_r24, -0.192F, 2.7489F, 0.0F);
			cube_r24.cubeList.add(new ModelBox(cube_r24, 27, 0, -0.5F, -3.9F, -5.0F, 1, 6, 0, 0.0F, false));
	
			cube_r25 = new ModelRenderer(this);
			cube_r25.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r25);
			setRotationAngle(cube_r25, -0.192F, -2.7489F, 0.0F);
			cube_r25.cubeList.add(new ModelBox(cube_r25, 27, 0, -0.5F, -3.9F, -5.0F, 1, 6, 0, 0.0F, false));
	
			cube_r26 = new ModelRenderer(this);
			cube_r26.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r26);
			setRotationAngle(cube_r26, -0.192F, -1.9635F, 0.0F);
			cube_r26.cubeList.add(new ModelBox(cube_r26, 27, 0, -0.5F, -3.9F, -5.0F, 1, 6, 0, 0.0F, false));
	
			cube_r27 = new ModelRenderer(this);
			cube_r27.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r27);
			setRotationAngle(cube_r27, -0.192F, -1.1781F, 0.0F);
			cube_r27.cubeList.add(new ModelBox(cube_r27, 27, 0, -0.5F, -3.9F, -5.0F, 1, 6, 0, 0.0F, false));
	
			cube_r28 = new ModelRenderer(this);
			cube_r28.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r28);
			setRotationAngle(cube_r28, -0.192F, -0.3927F, 0.0F);
			cube_r28.cubeList.add(new ModelBox(cube_r28, 27, 0, -0.5F, -3.9F, -5.0F, 1, 6, 0, 0.0F, false));
	
			cube_r29 = new ModelRenderer(this);
			cube_r29.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r29);
			setRotationAngle(cube_r29, -0.192F, 0.3927F, 0.0F);
			cube_r29.cubeList.add(new ModelBox(cube_r29, 27, 0, -0.5F, -3.9F, -5.0F, 1, 6, 0, 0.0F, false));
	
			cube_r30 = new ModelRenderer(this);
			cube_r30.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r30);
			setRotationAngle(cube_r30, -0.192F, 1.1781F, 0.0F);
			cube_r30.cubeList.add(new ModelBox(cube_r30, 27, 0, -0.5F, -3.9F, -5.0F, 1, 6, 0, 0.0F, false));
	
			cube_r31 = new ModelRenderer(this);
			cube_r31.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r31);
			setRotationAngle(cube_r31, -0.192F, 1.9635F, 0.0F);
			cube_r31.cubeList.add(new ModelBox(cube_r31, 27, 0, -0.5F, -3.9F, -5.0F, 1, 6, 0, 0.0F, false));
	
			cube_r32 = new ModelRenderer(this);
			cube_r32.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r32);
			setRotationAngle(cube_r32, 2.9671F, -0.7854F, 3.1416F);
			cube_r32.cubeList.add(new ModelBox(cube_r32, 52, 0, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
	
			cube_r33 = new ModelRenderer(this);
			cube_r33.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r33);
			setRotationAngle(cube_r33, -0.1745F, -1.5708F, 0.0F);
			cube_r33.cubeList.add(new ModelBox(cube_r33, 20, 51, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
	
			cube_r34 = new ModelRenderer(this);
			cube_r34.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r34);
			setRotationAngle(cube_r34, 2.9671F, 0.7854F, 3.1416F);
			cube_r34.cubeList.add(new ModelBox(cube_r34, 10, 51, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
	
			cube_r35 = new ModelRenderer(this);
			cube_r35.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r35);
			setRotationAngle(cube_r35, 2.9671F, 0.0F, 3.1416F);
			cube_r35.cubeList.add(new ModelBox(cube_r35, 50, 50, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
	
			cube_r36 = new ModelRenderer(this);
			cube_r36.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r36);
			setRotationAngle(cube_r36, -0.1745F, 0.7854F, 0.0F);
			cube_r36.cubeList.add(new ModelBox(cube_r36, 52, 12, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
	
			cube_r37 = new ModelRenderer(this);
			cube_r37.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r37);
			setRotationAngle(cube_r37, -0.1745F, 1.5708F, 0.0F);
			cube_r37.cubeList.add(new ModelBox(cube_r37, 52, 6, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
	
			cube_r38 = new ModelRenderer(this);
			cube_r38.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r38);
			setRotationAngle(cube_r38, -0.1745F, -0.7854F, 0.0F);
			cube_r38.cubeList.add(new ModelBox(cube_r38, 40, 53, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
	
			cube_r39 = new ModelRenderer(this);
			cube_r39.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r39);
			setRotationAngle(cube_r39, -0.1745F, 0.0F, 0.0F);
			cube_r39.cubeList.add(new ModelBox(cube_r39, 0, 55, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
	
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(0.0F, -5.25F, 0.0F);
			head.addChild(bone3);
			
	
			cube_r40 = new ModelRenderer(this);
			cube_r40.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r40);
			setRotationAngle(cube_r40, -0.5585F, 1.9635F, 0.0F);
			cube_r40.cubeList.add(new ModelBox(cube_r40, 0, 0, -0.5F, -2.4F, -4.1F, 1, 4, 0, 0.0F, false));
	
			cube_r41 = new ModelRenderer(this);
			cube_r41.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r41);
			setRotationAngle(cube_r41, -0.5585F, 2.7489F, 0.0F);
			cube_r41.cubeList.add(new ModelBox(cube_r41, 0, 0, -0.5F, -2.4F, -4.1F, 1, 4, 0, 0.0F, false));
	
			cube_r42 = new ModelRenderer(this);
			cube_r42.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r42);
			setRotationAngle(cube_r42, -0.5585F, -2.7489F, 0.0F);
			cube_r42.cubeList.add(new ModelBox(cube_r42, 0, 0, -0.5F, -2.35F, -4.1F, 1, 4, 0, 0.0F, false));
	
			cube_r43 = new ModelRenderer(this);
			cube_r43.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r43);
			setRotationAngle(cube_r43, -0.5585F, -1.9635F, 0.0F);
			cube_r43.cubeList.add(new ModelBox(cube_r43, 0, 0, -0.5F, -2.35F, -4.1F, 1, 4, 0, 0.0F, false));
	
			cube_r44 = new ModelRenderer(this);
			cube_r44.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r44);
			setRotationAngle(cube_r44, -0.5585F, -1.1781F, 0.0F);
			cube_r44.cubeList.add(new ModelBox(cube_r44, 0, 0, -0.5F, -2.35F, -4.1F, 1, 4, 0, 0.0F, false));
	
			cube_r45 = new ModelRenderer(this);
			cube_r45.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r45);
			setRotationAngle(cube_r45, -0.5585F, -0.3927F, 0.0F);
			cube_r45.cubeList.add(new ModelBox(cube_r45, 0, 0, -0.5F, -2.35F, -4.1F, 1, 4, 0, 0.0F, false));
	
			cube_r46 = new ModelRenderer(this);
			cube_r46.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r46);
			setRotationAngle(cube_r46, -0.5585F, 0.3927F, 0.0F);
			cube_r46.cubeList.add(new ModelBox(cube_r46, 0, 0, -0.5F, -2.35F, -4.1F, 1, 4, 0, 0.0F, false));
	
			cube_r47 = new ModelRenderer(this);
			cube_r47.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r47);
			setRotationAngle(cube_r47, -0.5585F, 1.1781F, 0.0F);
			cube_r47.cubeList.add(new ModelBox(cube_r47, 0, 0, -0.5F, -2.35F, -4.1F, 1, 4, 0, 0.0F, false));
	
			cube_r48 = new ModelRenderer(this);
			cube_r48.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r48);
			setRotationAngle(cube_r48, -0.5323F, 1.5708F, 0.0F);
			cube_r48.cubeList.add(new ModelBox(cube_r48, 60, 37, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
	
			cube_r49 = new ModelRenderer(this);
			cube_r49.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r49);
			setRotationAngle(cube_r49, -0.5323F, 0.7854F, 0.0F);
			cube_r49.cubeList.add(new ModelBox(cube_r49, 60, 33, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
	
			cube_r50 = new ModelRenderer(this);
			cube_r50.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r50);
			setRotationAngle(cube_r50, 2.6093F, 0.7854F, 3.1416F);
			cube_r50.cubeList.add(new ModelBox(cube_r50, 60, 29, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
	
			cube_r51 = new ModelRenderer(this);
			cube_r51.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r51);
			setRotationAngle(cube_r51, 2.6093F, 0.0F, 3.1416F);
			cube_r51.cubeList.add(new ModelBox(cube_r51, 60, 25, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
	
			cube_r52 = new ModelRenderer(this);
			cube_r52.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r52);
			setRotationAngle(cube_r52, -0.5323F, -1.5708F, 0.0F);
			cube_r52.cubeList.add(new ModelBox(cube_r52, 60, 45, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
	
			cube_r53 = new ModelRenderer(this);
			cube_r53.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r53);
			setRotationAngle(cube_r53, 2.6093F, -0.7854F, 3.1416F);
			cube_r53.cubeList.add(new ModelBox(cube_r53, 60, 41, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
	
			cube_r54 = new ModelRenderer(this);
			cube_r54.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r54);
			setRotationAngle(cube_r54, -0.5323F, -0.7854F, 0.0F);
			cube_r54.cubeList.add(new ModelBox(cube_r54, 60, 49, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
	
			cube_r55 = new ModelRenderer(this);
			cube_r55.setRotationPoint(0.0F, 2.0F, 0.0F);
			bone3.addChild(cube_r55);
			setRotationAngle(cube_r55, -0.5323F, 0.0F, 0.0F);
			cube_r55.cubeList.add(new ModelBox(cube_r55, 0, 61, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
	
			face = new ModelRenderer(this);
			face.setRotationPoint(0.0F, -13.1F, 0.0F);
			bone3.addChild(face);
			face.cubeList.add(new ModelBox(face, 1, 74, -7.5F, 6.06F, -7.5F, 15, 10, 15, -5.0F, false));
	
			cube_r56 = new ModelRenderer(this);
			cube_r56.setRotationPoint(0.0F, 9.9F, 0.0F);
			face.addChild(cube_r56);
			setRotationAngle(cube_r56, 0.0F, -0.829F, 0.0F);
			cube_r56.cubeList.add(new ModelBox(cube_r56, 0, 12, -1.0F, 1.25F, -2.3F, 2, 0, 1, 0.0F, false));
	
			cube_r57 = new ModelRenderer(this);
			cube_r57.setRotationPoint(0.0F, 9.9F, 0.0F);
			face.addChild(cube_r57);
			setRotationAngle(cube_r57, 0.0F, -2.3562F, 0.0F);
			cube_r57.cubeList.add(new ModelBox(cube_r57, 0, 12, -1.0F, 1.25F, -2.3F, 2, 0, 1, 0.0F, false));
	
			cube_r58 = new ModelRenderer(this);
			cube_r58.setRotationPoint(0.0F, 9.9F, 0.0F);
			face.addChild(cube_r58);
			setRotationAngle(cube_r58, 0.0F, 2.3562F, 0.0F);
			cube_r58.cubeList.add(new ModelBox(cube_r58, 0, 12, -1.0F, 1.25F, -2.3F, 2, 0, 1, 0.0F, false));
	
			cube_r59 = new ModelRenderer(this);
			cube_r59.setRotationPoint(0.0F, 9.9F, 0.0F);
			face.addChild(cube_r59);
			setRotationAngle(cube_r59, 0.0F, 0.7854F, 0.0F);
			cube_r59.cubeList.add(new ModelBox(cube_r59, 0, 12, -1.0F, 1.25F, -2.3F, 2, 0, 1, 0.0F, false));
	
			HornRight = new ModelRenderer(this);
			HornRight.setRotationPoint(-2.5F, -1.5F, 1.5F);
			bone3.addChild(HornRight);
			setRotationAngle(HornRight, -0.2618F, 0.0F, -0.4363F);
			HornRight.cubeList.add(new ModelBox(HornRight, 62, 2, 0.0F, -0.45F, -0.5F, 1, 1, 1, 0.0F, false));
	
			Horn0_1 = new ModelRenderer(this);
			Horn0_1.setRotationPoint(0.0F, -0.25F, 0.0F);
			HornRight.addChild(Horn0_1);
			setRotationAngle(Horn0_1, -0.2618F, 0.0F, 0.0873F);
			Horn0_1.cubeList.add(new ModelBox(Horn0_1, 62, 2, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, false));
	
			Horn0_2 = new ModelRenderer(this);
			Horn0_2.setRotationPoint(0.0F, -0.5F, 0.0F);
			Horn0_1.addChild(Horn0_2);
			setRotationAngle(Horn0_2, -0.1745F, 0.0F, 0.0F);
			Horn0_2.cubeList.add(new ModelBox(Horn0_2, 62, 2, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.1F, false));
	
			Horn0_3 = new ModelRenderer(this);
			Horn0_3.setRotationPoint(0.0F, -0.5F, 0.0F);
			Horn0_2.addChild(Horn0_3);
			setRotationAngle(Horn0_3, -0.0873F, 0.0F, 0.0F);
			Horn0_3.cubeList.add(new ModelBox(Horn0_3, 62, 2, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.15F, false));
	
			eyeRight = new ModelRenderer(this);
			eyeRight.setRotationPoint(0.0F, -0.6499F, 0.0253F);
			Horn0_3.addChild(eyeRight);
			setRotationAngle(eyeRight, 0.0F, 0.0F, -0.0873F);
			eyeRight.cubeList.add(new ModelBox(eyeRight, 62, 0, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, false));
	
			HornLeft = new ModelRenderer(this);
			HornLeft.setRotationPoint(2.5F, -1.5F, 1.5F);
			bone3.addChild(HornLeft);
			setRotationAngle(HornLeft, -0.2618F, 0.0F, 0.4363F);
			HornLeft.cubeList.add(new ModelBox(HornLeft, 62, 2, -1.0F, -0.45F, -0.5F, 1, 1, 1, 0.0F, true));
	
			Horn0_5 = new ModelRenderer(this);
			Horn0_5.setRotationPoint(0.0F, -0.25F, 0.0F);
			HornLeft.addChild(Horn0_5);
			setRotationAngle(Horn0_5, -0.2618F, 0.0F, -0.0873F);
			Horn0_5.cubeList.add(new ModelBox(Horn0_5, 62, 2, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, true));
	
			Horn0_6 = new ModelRenderer(this);
			Horn0_6.setRotationPoint(0.0F, -0.5F, 0.0F);
			Horn0_5.addChild(Horn0_6);
			setRotationAngle(Horn0_6, -0.1745F, 0.0F, 0.0F);
			Horn0_6.cubeList.add(new ModelBox(Horn0_6, 62, 2, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.1F, true));
	
			Horn0_7 = new ModelRenderer(this);
			Horn0_7.setRotationPoint(0.0F, -0.5F, 0.0F);
			Horn0_6.addChild(Horn0_7);
			setRotationAngle(Horn0_7, -0.0873F, 0.0F, 0.0F);
			Horn0_7.cubeList.add(new ModelBox(Horn0_7, 62, 2, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.15F, true));
	
			eyeLeft = new ModelRenderer(this);
			eyeLeft.setRotationPoint(0.0F, -0.6499F, 0.0253F);
			Horn0_7.addChild(eyeLeft);
			setRotationAngle(eyeLeft, 0.0F, 0.0F, 0.0873F);
			eyeLeft.cubeList.add(new ModelBox(eyeLeft, 62, 0, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, true));
	
			HornRight2 = new ModelRenderer(this);
			HornRight2.setRotationPoint(-1.2102F, -0.9245F, -0.4084F);
			bone3.addChild(HornRight2);
			setRotationAngle(HornRight2, -3.0437F, -0.2648F, 2.8199F);
			
	
			Horn0_9 = new ModelRenderer(this);
			Horn0_9.setRotationPoint(-0.5F, -0.3F, 0.0F);
			HornRight2.addChild(Horn0_9);
			setRotationAngle(Horn0_9, -0.2618F, 0.0F, 0.0873F);
			
	
			Horn0_10 = new ModelRenderer(this);
			Horn0_10.setRotationPoint(0.0F, -0.5F, 0.0F);
			Horn0_9.addChild(Horn0_10);
			setRotationAngle(Horn0_10, -0.1745F, 0.0F, 0.0F);
			Horn0_10.cubeList.add(new ModelBox(Horn0_10, 62, 2, 0.0F, -1.05F, -0.5F, 1, 1, 1, -0.25F, false));
	
			Horn0_11 = new ModelRenderer(this);
			Horn0_11.setRotationPoint(0.0F, -0.5F, 0.0F);
			Horn0_10.addChild(Horn0_11);
			setRotationAngle(Horn0_11, -0.0873F, 0.0F, 0.0F);
			Horn0_11.cubeList.add(new ModelBox(Horn0_11, 62, 2, 0.0F, -0.85F, -0.5F, 1, 1, 1, -0.3F, false));
	
			Horn0_12 = new ModelRenderer(this);
			Horn0_12.setRotationPoint(0.0F, -0.6499F, 0.0253F);
			Horn0_11.addChild(Horn0_12);
			setRotationAngle(Horn0_12, 0.0F, 0.0F, -0.0873F);
			Horn0_12.cubeList.add(new ModelBox(Horn0_12, 62, 2, 0.0F, -0.65F, -0.5F, 1, 1, 1, -0.2F, false));
	
			HornLeft2 = new ModelRenderer(this);
			HornLeft2.setRotationPoint(1.2102F, -0.9245F, -0.4084F);
			bone3.addChild(HornLeft2);
			setRotationAngle(HornLeft2, -3.0437F, 0.2648F, -2.8199F);
			
	
			Horn0_13 = new ModelRenderer(this);
			Horn0_13.setRotationPoint(0.5F, -0.3F, 0.0F);
			HornLeft2.addChild(Horn0_13);
			setRotationAngle(Horn0_13, -0.2618F, 0.0F, -0.0873F);
			
	
			Horn0_14 = new ModelRenderer(this);
			Horn0_14.setRotationPoint(0.0F, -0.5F, 0.0F);
			Horn0_13.addChild(Horn0_14);
			setRotationAngle(Horn0_14, -0.1745F, 0.0F, 0.0F);
			Horn0_14.cubeList.add(new ModelBox(Horn0_14, 62, 2, -1.0F, -1.05F, -0.5F, 1, 1, 1, -0.25F, true));
	
			Horn0_15 = new ModelRenderer(this);
			Horn0_15.setRotationPoint(0.0F, -0.5F, 0.0F);
			Horn0_14.addChild(Horn0_15);
			setRotationAngle(Horn0_15, -0.0873F, 0.0F, 0.0F);
			Horn0_15.cubeList.add(new ModelBox(Horn0_15, 62, 2, -1.0F, -0.85F, -0.5F, 1, 1, 1, -0.3F, true));
	
			Horn0_16 = new ModelRenderer(this);
			Horn0_16.setRotationPoint(0.0F, -0.6499F, 0.0253F);
			Horn0_15.addChild(Horn0_16);
			setRotationAngle(Horn0_16, 0.0F, 0.0F, 0.0873F);
			Horn0_16.cubeList.add(new ModelBox(Horn0_16, 62, 2, -1.0F, -0.65F, -0.5F, 1, 1, 1, -0.2F, true));
	
			tail[0] = new ModelRenderer(this);
			tail[0].setRotationPoint(0.0F, 10.75F, -0.75F);
			body.addChild(tail[0]);
			setRotationAngle(tail[0], 0.0F, 0.0F, -0.3927F);
			
	
			cube_r60 = new ModelRenderer(this);
			cube_r60.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r60);
			setRotationAngle(cube_r60, 0.0471F, -2.7489F, 0.0F);
			cube_r60.cubeList.add(new ModelBox(cube_r60, 16, 0, -0.5F, -1.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r61 = new ModelRenderer(this);
			cube_r61.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r61);
			setRotationAngle(cube_r61, 0.0471F, 2.7489F, 0.0F);
			cube_r61.cubeList.add(new ModelBox(cube_r61, 16, 0, -0.5F, -1.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r62 = new ModelRenderer(this);
			cube_r62.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r62);
			setRotationAngle(cube_r62, 0.0471F, 1.9635F, 0.0F);
			cube_r62.cubeList.add(new ModelBox(cube_r62, 16, 0, -0.5F, -1.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r63 = new ModelRenderer(this);
			cube_r63.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r63);
			setRotationAngle(cube_r63, 0.0471F, 1.1781F, 0.0F);
			cube_r63.cubeList.add(new ModelBox(cube_r63, 16, 0, -0.5F, -1.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r64 = new ModelRenderer(this);
			cube_r64.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r64);
			setRotationAngle(cube_r64, 0.0471F, 0.3927F, 0.0F);
			cube_r64.cubeList.add(new ModelBox(cube_r64, 16, 0, -0.5F, -1.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r65 = new ModelRenderer(this);
			cube_r65.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r65);
			setRotationAngle(cube_r65, 0.0471F, -0.3927F, 0.0F);
			cube_r65.cubeList.add(new ModelBox(cube_r65, 16, 0, -0.5F, -1.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r66 = new ModelRenderer(this);
			cube_r66.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r66);
			setRotationAngle(cube_r66, 0.0471F, -1.1781F, 0.0F);
			cube_r66.cubeList.add(new ModelBox(cube_r66, 16, 0, -0.5F, -1.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r67 = new ModelRenderer(this);
			cube_r67.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r67);
			setRotationAngle(cube_r67, 0.0471F, -1.9635F, 0.0F);
			cube_r67.cubeList.add(new ModelBox(cube_r67, 16, 0, -0.5F, -1.0F, -5.33F, 1, 9, 0, 0.0F, false));
	
			cube_r68 = new ModelRenderer(this);
			cube_r68.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r68);
			setRotationAngle(cube_r68, -3.098F, 0.7854F, -3.1416F);
			cube_r68.cubeList.add(new ModelBox(cube_r68, 16, 24, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r69 = new ModelRenderer(this);
			cube_r69.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r69);
			setRotationAngle(cube_r69, 0.0436F, -1.5708F, 0.0F);
			cube_r69.cubeList.add(new ModelBox(cube_r69, 26, 24, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r70 = new ModelRenderer(this);
			cube_r70.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r70);
			setRotationAngle(cube_r70, -3.098F, -0.7854F, -3.1416F);
			cube_r70.cubeList.add(new ModelBox(cube_r70, 0, 29, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r71 = new ModelRenderer(this);
			cube_r71.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r71);
			setRotationAngle(cube_r71, -3.098F, 0.0F, -3.1416F);
			cube_r71.cubeList.add(new ModelBox(cube_r71, 32, 0, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r72 = new ModelRenderer(this);
			cube_r72.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r72);
			setRotationAngle(cube_r72, 0.0436F, -0.7854F, 0.0F);
			cube_r72.cubeList.add(new ModelBox(cube_r72, 32, 9, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r73 = new ModelRenderer(this);
			cube_r73.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r73);
			setRotationAngle(cube_r73, 0.0436F, 1.5708F, 0.0F);
			cube_r73.cubeList.add(new ModelBox(cube_r73, 10, 33, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r74 = new ModelRenderer(this);
			cube_r74.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r74);
			setRotationAngle(cube_r74, 0.0436F, 0.7854F, 0.0F);
			cube_r74.cubeList.add(new ModelBox(cube_r74, 20, 33, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			cube_r75 = new ModelRenderer(this);
			cube_r75.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r75);
			setRotationAngle(cube_r75, 0.0436F, 0.0F, 0.0F);
			cube_r75.cubeList.add(new ModelBox(cube_r75, 30, 33, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
	
			tail[1] = new ModelRenderer(this);
			tail[1].setRotationPoint(0.0F, 5.75F, 0.0F);
			tail[0].addChild(tail[1]);
			setRotationAngle(tail[1], 0.0F, 0.0F, -0.2618F);
			
	
			cube_r76 = new ModelRenderer(this);
			cube_r76.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r76);
			setRotationAngle(cube_r76, 0.2827F, -2.7489F, 0.0F);
			cube_r76.cubeList.add(new ModelBox(cube_r76, 18, 0, -0.5F, -1.7462F, -4.83F, 1, 8, 0, 0.0F, false));
	
			cube_r77 = new ModelRenderer(this);
			cube_r77.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r77);
			setRotationAngle(cube_r77, 0.2827F, 2.7489F, 0.0F);
			cube_r77.cubeList.add(new ModelBox(cube_r77, 18, 0, -0.5F, -1.7462F, -4.83F, 1, 8, 0, 0.0F, false));
	
			cube_r78 = new ModelRenderer(this);
			cube_r78.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r78);
			setRotationAngle(cube_r78, 0.2827F, 1.9635F, 0.0F);
			cube_r78.cubeList.add(new ModelBox(cube_r78, 18, 0, -0.5F, -1.7462F, -4.83F, 1, 8, 0, 0.0F, false));
	
			cube_r79 = new ModelRenderer(this);
			cube_r79.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r79);
			setRotationAngle(cube_r79, 0.2827F, 1.1781F, 0.0F);
			cube_r79.cubeList.add(new ModelBox(cube_r79, 18, 0, -0.5F, -1.7462F, -4.83F, 1, 8, 0, 0.0F, false));
	
			cube_r80 = new ModelRenderer(this);
			cube_r80.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r80);
			setRotationAngle(cube_r80, 0.2827F, 0.3927F, 0.0F);
			cube_r80.cubeList.add(new ModelBox(cube_r80, 18, 0, -0.5F, -1.7462F, -4.83F, 1, 8, 0, 0.0F, false));
	
			cube_r81 = new ModelRenderer(this);
			cube_r81.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r81);
			setRotationAngle(cube_r81, 0.2827F, -0.3927F, 0.0F);
			cube_r81.cubeList.add(new ModelBox(cube_r81, 18, 0, -0.5F, -1.7462F, -4.83F, 1, 8, 0, 0.0F, false));
	
			cube_r82 = new ModelRenderer(this);
			cube_r82.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r82);
			setRotationAngle(cube_r82, 0.2827F, -1.1781F, 0.0F);
			cube_r82.cubeList.add(new ModelBox(cube_r82, 18, 0, -0.5F, -1.7462F, -4.83F, 1, 8, 0, 0.0F, false));
	
			cube_r83 = new ModelRenderer(this);
			cube_r83.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r83);
			setRotationAngle(cube_r83, 0.2827F, -1.9635F, 0.0F);
			cube_r83.cubeList.add(new ModelBox(cube_r83, 18, 0, -0.5F, -1.7462F, -4.83F, 1, 8, 0, 0.0F, false));
	
			cube_r84 = new ModelRenderer(this);
			cube_r84.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r84);
			setRotationAngle(cube_r84, -0.2618F, 3.1416F, 0.0F);
			cube_r84.cubeList.add(new ModelBox(cube_r84, 0, 47, -2.5F, -1.7462F, 4.6934F, 5, 8, 0, 0.0F, false));
	
			cube_r85 = new ModelRenderer(this);
			cube_r85.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r85);
			setRotationAngle(cube_r85, -0.2618F, 2.3562F, 0.0F);
			cube_r85.cubeList.add(new ModelBox(cube_r85, 50, 34, -2.5F, -1.7462F, 4.6934F, 5, 8, 0, 0.0F, false));
	
			cube_r86 = new ModelRenderer(this);
			cube_r86.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r86);
			setRotationAngle(cube_r86, -0.2618F, 1.5708F, 0.0F);
			cube_r86.cubeList.add(new ModelBox(cube_r86, 40, 45, -2.5F, -1.7462F, 4.6934F, 5, 8, 0, 0.0F, false));
	
			cube_r87 = new ModelRenderer(this);
			cube_r87.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r87);
			setRotationAngle(cube_r87, -0.2618F, 0.7854F, 0.0F);
			cube_r87.cubeList.add(new ModelBox(cube_r87, 30, 42, -2.5F, -1.7462F, 4.6934F, 5, 8, 0, 0.0F, false));
	
			cube_r88 = new ModelRenderer(this);
			cube_r88.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r88);
			setRotationAngle(cube_r88, -0.2618F, -2.3562F, 0.0F);
			cube_r88.cubeList.add(new ModelBox(cube_r88, 50, 26, -2.5F, -1.7462F, 4.6934F, 5, 8, 0, 0.0F, false));
	
			cube_r89 = new ModelRenderer(this);
			cube_r89.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r89);
			setRotationAngle(cube_r89, -0.2618F, -1.5708F, 0.0F);
			cube_r89.cubeList.add(new ModelBox(cube_r89, 30, 50, -2.5F, -1.7462F, 4.6934F, 5, 8, 0, 0.0F, false));
	
			cube_r90 = new ModelRenderer(this);
			cube_r90.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r90);
			setRotationAngle(cube_r90, -0.2618F, -0.7854F, 0.0F);
			cube_r90.cubeList.add(new ModelBox(cube_r90, 46, 18, -2.5F, -1.7462F, 4.6934F, 5, 8, 0, 0.0F, false));
	
			cube_r91 = new ModelRenderer(this);
			cube_r91.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[1].addChild(cube_r91);
			setRotationAngle(cube_r91, -0.2618F, 0.0F, 0.0F);
			cube_r91.cubeList.add(new ModelBox(cube_r91, 50, 42, -2.5F, -1.7462F, 4.6934F, 5, 8, 0, 0.0F, false));
	
			tail[2] = new ModelRenderer(this);
			tail[2].setRotationPoint(0.0F, 6.0F, 0.5F);
			tail[1].addChild(tail[2]);
			setRotationAngle(tail[2], 0.0F, 0.0F, -0.2618F);
			tail[2].cubeList.add(new ModelBox(tail[2], 1, 8, -0.5F, 5.65F, -1.0F, 1, 0, 1, 0.0F, false));
	
			cube_r92 = new ModelRenderer(this);
			cube_r92.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r92);
			setRotationAngle(cube_r92, 0.5585F, 2.7489F, 0.0F);
			cube_r92.cubeList.add(new ModelBox(cube_r92, 20, 0, -0.5F, -1.25F, -3.43F, 1, 6, 0, 0.0F, false));
	
			cube_r93 = new ModelRenderer(this);
			cube_r93.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r93);
			setRotationAngle(cube_r93, 0.5585F, 1.9635F, 0.0F);
			cube_r93.cubeList.add(new ModelBox(cube_r93, 20, 0, -0.5F, -1.25F, -3.43F, 1, 6, 0, 0.0F, false));
	
			cube_r94 = new ModelRenderer(this);
			cube_r94.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r94);
			setRotationAngle(cube_r94, 0.5585F, 1.1781F, 0.0F);
			cube_r94.cubeList.add(new ModelBox(cube_r94, 20, 0, -0.5F, -1.25F, -3.43F, 1, 6, 0, 0.0F, false));
	
			cube_r95 = new ModelRenderer(this);
			cube_r95.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r95);
			setRotationAngle(cube_r95, 0.5585F, 0.3927F, 0.0F);
			cube_r95.cubeList.add(new ModelBox(cube_r95, 20, 0, -0.5F, -1.25F, -3.43F, 1, 6, 0, 0.0F, false));
	
			cube_r96 = new ModelRenderer(this);
			cube_r96.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r96);
			setRotationAngle(cube_r96, 0.5585F, -0.3927F, 0.0F);
			cube_r96.cubeList.add(new ModelBox(cube_r96, 20, 0, -0.5F, -1.25F, -3.43F, 1, 6, 0, 0.0F, false));
	
			cube_r97 = new ModelRenderer(this);
			cube_r97.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r97);
			setRotationAngle(cube_r97, 0.5585F, -1.1781F, 0.0F);
			cube_r97.cubeList.add(new ModelBox(cube_r97, 20, 0, -0.5F, -1.25F, -3.43F, 1, 6, 0, 0.0F, false));
	
			cube_r98 = new ModelRenderer(this);
			cube_r98.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r98);
			setRotationAngle(cube_r98, 0.5585F, -2.7489F, 0.0F);
			cube_r98.cubeList.add(new ModelBox(cube_r98, 20, 0, -0.5F, -1.25F, -3.43F, 1, 6, 0, 0.0F, false));
	
			cube_r99 = new ModelRenderer(this);
			cube_r99.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r99);
			setRotationAngle(cube_r99, 0.5585F, -1.9635F, 0.0F);
			cube_r99.cubeList.add(new ModelBox(cube_r99, 20, 0, -0.5F, -1.25F, -3.43F, 1, 6, 0, 0.0F, false));
	
			cube_r100 = new ModelRenderer(this);
			cube_r100.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r100);
			setRotationAngle(cube_r100, -0.5236F, 3.1416F, 0.0F);
			cube_r100.cubeList.add(new ModelBox(cube_r100, 56, 18, -2.0F, -1.0F, 3.35F, 4, 7, 0, 0.0F, false));
	
			cube_r101 = new ModelRenderer(this);
			cube_r101.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r101);
			setRotationAngle(cube_r101, -0.5236F, 0.7854F, 0.0F);
			cube_r101.cubeList.add(new ModelBox(cube_r101, 10, 57, -2.0F, -1.0F, 3.35F, 4, 7, 0, 0.0F, false));
	
			cube_r102 = new ModelRenderer(this);
			cube_r102.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r102);
			setRotationAngle(cube_r102, -0.5236F, -0.7854F, 0.0F);
			cube_r102.cubeList.add(new ModelBox(cube_r102, 50, 56, -2.0F, -1.0F, 3.35F, 4, 7, 0, 0.0F, false));
	
			cube_r103 = new ModelRenderer(this);
			cube_r103.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r103);
			setRotationAngle(cube_r103, -0.5236F, 1.5708F, 0.0F);
			cube_r103.cubeList.add(new ModelBox(cube_r103, 18, 57, -2.0F, -1.0F, 3.35F, 4, 7, 0, 0.0F, false));
	
			cube_r104 = new ModelRenderer(this);
			cube_r104.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r104);
			setRotationAngle(cube_r104, -0.5236F, -1.5708F, 0.0F);
			cube_r104.cubeList.add(new ModelBox(cube_r104, 59, 56, -2.0F, -1.0F, 3.35F, 4, 7, 0, 0.0F, false));
	
			cube_r105 = new ModelRenderer(this);
			cube_r105.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r105);
			setRotationAngle(cube_r105, -0.5236F, -2.3562F, 0.0F);
			cube_r105.cubeList.add(new ModelBox(cube_r105, 26, 58, -2.0F, -1.0F, 3.35F, 4, 7, 0, 0.0F, false));
	
			cube_r106 = new ModelRenderer(this);
			cube_r106.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r106);
			setRotationAngle(cube_r106, -0.5236F, 2.3562F, 0.0F);
			cube_r106.cubeList.add(new ModelBox(cube_r106, 34, 59, -2.0F, -1.0F, 3.35F, 4, 7, 0, 0.0F, false));
	
			cube_r107 = new ModelRenderer(this);
			cube_r107.setRotationPoint(0.0F, -0.25F, -0.5F);
			tail[2].addChild(cube_r107);
			setRotationAngle(cube_r107, -0.5236F, 0.0F, 0.0F);
			cube_r107.cubeList.add(new ModelBox(cube_r107, 42, 59, -2.0F, -1.0F, 3.35F, 4, 7, 0, 0.0F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 1.5F - 1.5F * this.scale, 0.0F);
			GlStateManager.scale(this.scale, this.scale, this.scale);
			body.render(f5);
			GlStateManager.popMatrix();
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		@Override
		public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
			limbSwing *= ((EntityCustom)entityIn).ogHeight / entityIn.height;
			EnumFacing facing = ((EntityCustom)entityIn).getClimbingSide();
			body.rotateAngleX = facing == EnumFacing.DOWN ? 1.5708F : 0.0F;
			eyeRight.rotateAngleX = headPitch * 0.5F * 0.017453292F;
			Horn0_3.rotateAngleX = -0.0873F + headPitch * 0.5F * 0.017453292F;
			eyeLeft.rotateAngleX = headPitch * 0.017453292F;
			Horn0_7.rotateAngleX = -0.0873F + headPitch * 0.5F * 0.017453292F;
			bodyFront.rotateAngleZ = -netHeadYaw * 0.5F * 0.017453292F;
			head.rotateAngleZ = -netHeadYaw * 0.5F * 0.017453292F;
	        for (int i = 0; i < this.tail.length; ++i) {
	            this.tail[i].rotateAngleZ = MathHelper.cos(limbSwing * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.02F * (float)(1 + Math.abs(i - 2));
	        }
		}
	}
}
