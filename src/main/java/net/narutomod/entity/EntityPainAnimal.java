
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.procedure.ProcedureOnLivingUpdate;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPainAnimal extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 494;
	public static final int ENTITYID_RANGED = 495;

	public EntityPainAnimal(ElementsNarutomodMod instance) {
		super(instance, 916);
	}

	@Override
	public void initElements() {
		elements.entities
				.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "pain_animal"), ENTITYID)
						.name("pain_animal").tracker(64, 3, true).egg(-16777216, -26368).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private boolean wasStandingStill;
		private EntitySummonAnimal.Base summonedEntity;
		private Path summonedEntityPath;
		private double summonedEntitySpeed;
		
		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.setSize(0.525f, 1.75f);
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelectorAkatsuki));
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setCustomNameTag(net.minecraft.util.text.translation.I18n.translateToLocal("entity.pain.name"));
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobe.body));
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedTactical(this, 1.0d, 60, 24.0F));
			this.tasks.addTask(2, new EntityAIWatchClosest(this, null, 48.0F, 1.0F) {
				@Override
				public boolean shouldExecute() {
					if (EntityCustom.this.getAttackTarget() != null) {
						this.closestEntity = EntityCustom.this.getAttackTarget();
						return true;
					}
					return false;
				}
			});
			this.tasks.addTask(3, new EntityAIWatchClosest2(this, EntityPlayer.class, 32.0F, 1.0F));
			this.tasks.addTask(4, new EntityAIWander(this, 0.5d) {
				@Override
				public boolean shouldExecute() {
					return !this.entity.isRiding() && super.shouldExecute();
				}
				@Override
				public boolean shouldContinueExecuting() {
					return !this.entity.isRiding() && super.shouldContinueExecuting();
				}
			});
			this.tasks.addTask(5, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.summonedEntity != null && this.summonedEntity.isEntityAlive() && this.summonedEntity.isAddedToWorld()
			 && this.getRidingEntity() != this.summonedEntity) {
				this.startRiding(this. summonedEntity, true);
			}
			if (this.isRiding() && this.getRidingEntity() == this.summonedEntity
			 && this.summonedEntity.getNavigator() instanceof EntityTailedBeast.NavigateGround) {
				this.summonedEntityPath = this.summonedEntity.getNavigator().getPath();
				this.summonedEntitySpeed = ((EntityTailedBeast.NavigateGround)this.summonedEntity.getNavigator()).getSpeed();
				this.moveHelper.read(this.summonedEntity.getMoveHelper());
			} else {
				this.summonedEntityPath = null;
			}
			boolean isStandingStill = this.isStandingStill();
			if (isStandingStill != this.wasStandingStill) {
				this.setSneaking(isStandingStill);
				ProcedureOnLivingUpdate.forceBowPose(this, isStandingStill);
			}
			this.wasStandingStill = isStandingStill;
		}

		@Override
		public void onLivingUpdate() {
			super.onLivingUpdate();
			if (this.summonedEntityPath != null) {
				this.summonedEntity.getNavigator().setPath(this.summonedEntityPath, this.summonedEntitySpeed);
			}
		}

		@Override
		public void onUpdate() {
			if (this.world.isRemote && !this.getEntityData().getBoolean("slimModel")) {
				this.getEntityData().setBoolean("slimModel", true);
			}
			super.onUpdate();
			if (!this.world.isRemote) {
				this.tasks.setControlFlag(7, !(this.getRidingEntity() instanceof EntitySummonAnimal.Base));
			}
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			if (distanceFactor > 0.4f && EntitySummonAnimal.getAllSummons(this, null).isEmpty()
			 && Chakra.pathway(this).consume(ItemRinnegan.ANIMALPATH_CHAKRA_USAGE)) {
				Particles.Renderer particles = new Particles.Renderer(this.world);
				particles.spawnParticles(Particles.Types.SEAL_FORMULA, this.posX, this.posY + 0.015d, this.posZ, 1, 0d, 0d, 0d, 0d, 0d, 0d, 200, 0, 60);
				for (int i = 0; i < 500; i++) {
					particles.spawnParticles(Particles.Types.SMOKE, this.posX, this.posY + 0.015d, this.posZ, 1, 0d, 0d, 0d,
					 (this.rand.nextDouble()-0.5d) * 0.8d, this.rand.nextDouble() * 0.6d + 0.2d, (this.rand.nextDouble()-0.5d) * 0.8d,
					 0xD0FFFFFF, 480, (int)(16.0d / (this.rand.nextDouble() * 0.8d + 0.2d)));
				}
				particles.send();
				this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kuchiyosenojutsu")), 1f, 1.3f);
				this.summonedEntity = this.rand.nextInt(3) == 0 ? new EntityGiantDog2h.EntityCustom(this)
				 : this.rand.nextBoolean() ? new EntityGiantChameleon.EntityCustom(this) : new EntityGiantBird.EntityCustom(this);
				//this.summonedEntity = new EntityGiantChameleon.EntityCustom(this);
				this.summonedEntity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f);
				net.narutomod.event.SpecialEvent.setDelayedSpawnEvent(this.world, this.summonedEntity, 0, 0, 0, this.world.getTotalWorldTime() + 20);
				this.rotationPitch = 60.0f;
				this.standStillFor(21);
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/pain_animal.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelPainAnimal());
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

		// Made with Blockbench 4.12.3
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelPainAnimal extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer eyeRight;
			private final ModelRenderer eyeLeft;
			private final ModelRenderer spikes;
			private final ModelRenderer hair1;
			private final ModelRenderer bone;
			private final ModelRenderer bone2;
			private final ModelRenderer hair2;
			private final ModelRenderer hair3;

			public ModelPainAnimal() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 52, -6.0F, -11.7F, -7.55F, 12, 12, 0, -3.5F, false));
				eyeRight = new ModelRenderer(this);
				eyeRight.setRotationPoint(3.2F, 3.35F, -4.05F);
				bipedHead.addChild(eyeRight);
				eyeRight.cubeList.add(new ModelBox(eyeRight, 40, 52, -11.0F, -12.0F, -5.0F, 12, 12, 0, -5.0F, false));
				eyeLeft = new ModelRenderer(this);
				eyeLeft.setRotationPoint(-3.2F, 3.35F, -4.05F);
				bipedHead.addChild(eyeLeft);
				eyeLeft.cubeList.add(new ModelBox(eyeLeft, 40, 52, -1.0F, -12.0F, -5.0F, 12, 12, 0, -5.0F, true));
				spikes = new ModelRenderer(this);
				spikes.setRotationPoint(-3.25F, -1.35F, -3.6F);
				bipedHead.addChild(spikes);
				spikes.cubeList.add(new ModelBox(spikes, 0, 0, 5.5F, -1.0F, -1.0F, 1, 1, 1, -0.35F, true));
				spikes.cubeList.add(new ModelBox(spikes, 0, 0, 0.0F, 0.15F, -1.0F, 1, 1, 1, -0.35F, false));
				spikes.cubeList.add(new ModelBox(spikes, 0, 0, 5.5F, 0.15F, -1.0F, 1, 1, 1, -0.35F, true));
				spikes.cubeList.add(new ModelBox(spikes, 0, 0, 0.0F, -0.4F, -1.0F, 1, 1, 1, -0.35F, false));
				spikes.cubeList.add(new ModelBox(spikes, 0, 0, 5.5F, -0.4F, -1.0F, 1, 1, 1, -0.35F, true));
				spikes.cubeList.add(new ModelBox(spikes, 0, 0, 0.0F, -1.0F, -1.0F, 1, 1, 1, -0.35F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -7.75F, -4.0F, 8, 8, 8, 0.5F, false));
				hair1 = new ModelRenderer(this);
				hair1.setRotationPoint(0.0F, -8.0F, 0.0F);
				bipedHeadwear.addChild(hair1);
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-1.0F, 0.0F, 1.0F);
				hair1.addChild(bone);
				setRotationAngle(bone, 0.0F, 0.0F, 0.2618F);
				bone.cubeList.add(new ModelBox(bone, 24, 1, -7.0F, -2.0F, -1.0F, 8, 2, 0, 0.0F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(1.0F, 0.0F, 1.0F);
				hair1.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.0F, -0.2618F);
				bone2.cubeList.add(new ModelBox(bone2, 24, 1, -1.0F, -2.0F, -1.0F, 8, 2, 0, 0.0F, true));
				hair2 = new ModelRenderer(this);
				hair2.setRotationPoint(0.0F, -8.0F, 0.0F);
				bipedHeadwear.addChild(hair2);
				setRotationAngle(hair2, 0.0F, 0.3491F, 0.0F);
				hair2.cubeList.add(new ModelBox(hair2, 24, 1, -8.0F, -2.0F, 0.0F, 8, 2, 0, 0.0F, false));
				hair2.cubeList.add(new ModelBox(hair2, 24, 1, 0.0F, -2.0F, 0.0F, 8, 2, 0, 0.0F, true));
				hair3 = new ModelRenderer(this);
				hair3.setRotationPoint(0.0F, -8.0F, 0.0F);
				bipedHeadwear.addChild(hair3);
				setRotationAngle(hair3, 0.0F, -0.3491F, 0.0F);
				hair3.cubeList.add(new ModelBox(hair3, 24, 1, -8.0F, -2.0F, 0.0F, 8, 2, 0, 0.0F, false));
				hair3.cubeList.add(new ModelBox(hair3, 24, 1, 0.0F, -2.0F, 0.0F, 8, 2, 0, 0.0F, true));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				setRotationAngle(bipedRightArm, -0.3927F, 0.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 41, 16, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 41, 32, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				setRotationAngle(bipedLeftArm, 0.3927F, 0.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 41, 16, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, true));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 41, 32, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.2F, true));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				setRotationAngle(bipedRightLeg, 0.3927F, 0.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				setRotationAngle(bipedLeftLeg, -0.3927F, 0.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, true));
			}
		}
	}
}
