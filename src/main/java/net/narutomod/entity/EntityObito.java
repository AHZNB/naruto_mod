
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemGunbai;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemMaskObito1;
import net.narutomod.item.ItemMaskObitoWar;
import net.narutomod.item.ItemNinjaArmorObitoWar;
import net.narutomod.procedure.ProcedureOnLivingUpdate;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityObito extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 510;
	public static final int ENTITYID_RANGED = 511;

	public EntityObito(ElementsNarutomodMod instance) {
		super(instance, 925);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "obito"), ENTITYID).name("obito").tracker(64, 3, true).egg(0xff4b4566, 0xffdee1e0).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityTobi.class)
		 .id(new ResourceLocation("narutomod", "tobi"), ENTITYID_RANGED).name("tobi").tracker(64, 3, true).egg(-16777216, -6750208).build());
	}

	public static class EntityTobi extends EntityCustom {
		private final List<EntityLivingBase> avoidEntitiesList = Lists.newArrayList();

		public EntityTobi(World world) {
			super(world);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemMaskObito1.helmet));
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobe.body));
			return livingdata;
		}

		@Override
		protected void initEntityAI() {
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true) {
				@Override
				protected void alertOthers() {
					double d0 = this.getTargetDistance();
					for (EntityNinjaMob.Base entityninja : this.taskOwner.world.getEntitiesWithinAABB(EntityNinjaMob.Base.class, this.taskOwner.getEntityBoundingBox().grow(d0, 10.0D, d0))) {
						if (this.taskOwner != entityninja && this.taskOwner.isOnSameTeam(entityninja)
						 && entityninja.getAttackTarget() == null && !entityninja.isOnSameTeam(this.taskOwner.getRevengeTarget())) {
							this.setEntityAttackTarget(entityninja, this.taskOwner.getRevengeTarget());
						}
					}
				}
			});
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityLivingBase.class, (p)-> {
				return p != null && EntityTobi.this.avoidEntitiesList.contains(p);
			}, 16f, 1.25d, 1.5d));
		}

		@Override
		public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
			if (livingBase != null && this.getRevengeTarget() != livingBase && !this.avoidEntitiesList.contains(livingBase)) {
				this.avoidEntitiesList.add(livingBase);
			}
			super.setRevengeTarget(livingBase);
		}
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private final double intangibleChakraUsage = 0.5d;
		private int intangibleTime;
		private boolean gunbaiOnRetrieval;

		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.setSize(0.6f, 1.8f);
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelectorAkatsuki));
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemMaskObitoWar.helmet));
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemNinjaArmorObitoWar.body));
			this.setItemToInventory(new ItemStack(ItemGunbai.block), 0);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			return new EntityNinjaMob.NavigateGround(this, worldIn) {
				@Override
				public void onUpdateNavigation() {
					if (this.getTargetPosition() != null && this.entity.noClip) {
						++this.totalTicks;
						Vec3d vec = new Vec3d(this.getTargetPosition().up()).subtract(this.entity.getPositionVector()).normalize().scale(0.15d); 
						this.entity.motionX += vec.x;
						this.entity.motionY += vec.y;
						this.entity.motionZ += vec.z;
					} else {
						super.onUpdateNavigation();
					}
				}
				@Override
				protected void checkForStuck(Vec3d positionVec3) {
					boolean flag = this.noPath();
					super.checkForStuck(positionVec3);
					if (!flag && this.noPath() && !this.entity.noClip) {
						EntityCustom.this.setIntangible();
					}
				}
			};
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
		}

		@Override
		protected double meleeReach() {
			return 3.4d;
		}

		@Override
		protected void initEntityAI() {
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 0.0F, 24.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.getAttackTarget().posY - EntityCustom.this.posY > 3d;
				}
			});
			this.tasks.addTask(3, new EntityNinjaMob.AIAttackMelee(this, 1.2d, true) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.getDistance(EntityCustom.this.getAttackTarget()) < 6d;
				}
				@Override
				public boolean shouldContinueExecuting() {
					return super.shouldContinueExecuting() && EntityCustom.this.getDistance(EntityCustom.this.getAttackTarget()) < 6d;
				}
			});
			this.tasks.addTask(4, new EntityAIAttackRanged(this, 1.0d, 20, 20f) {
				@Override
				public void startExecuting() {
					super.startExecuting();
					EntityCustom.this.setSwingingArms(true);
				}
				@Override
				public void resetTask() {
					super.resetTask();
					EntityCustom.this.setSwingingArms(false);
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
			ItemStack stack = this.getHeldItemMainhand();
			if (stack.getItem() == ItemGunbai.block && ((ItemGunbai.RangedItem)stack.getItem()).isThrown(stack)) {
				ItemGunbai.EntityCustom entity = ((ItemGunbai.RangedItem)stack.getItem()).getGunbaiEntity(this.world, stack);
				if (entity != null && entity.inGround()) {
					this.standStillFor(40);
					entity.retrieve(this);
					this.gunbaiOnRetrieval = true;
				}
			} else {
				super.updateAITasks();
				if (stack.getItem() == ItemGunbai.block && this.isHandActive() && stack.getMaxItemUseDuration() - this.getItemInUseCount() > 10) {
					this.resetActiveHand();
				}
			}
		}

		private boolean setIntangible() {
			int i = (int)(Math.min(this.getChakra(), this.intangibleChakraUsage * 20) / this.intangibleChakraUsage);
			if (i > 0) {
				this.consumeChakra(this.intangibleChakraUsage * i);
				ProcedureOnLivingUpdate.setNoClip(this, true);
				ProcedureOnLivingUpdate.setUntargetable(this, i);
				this.motionY = 0.0d;
				this.intangibleTime = i;
				return true;
			}
			return false;
		}
		
		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (this.noClip || source == DamageSource.FALL) {
				return false;
			}
			ItemStack stack = this.getHeldItemMainhand();
			if (stack.getItem() == ItemGunbai.block && !((ItemGunbai.RangedItem)stack.getItem()).isThrown(stack)
			 && source.getImmediateSource() instanceof ItemJutsu.IJutsu && amount > 29f) {
			 	if (!this.isActiveItemStackBlocking()) {
					this.setActiveHand(EnumHand.MAIN_HAND);
					this.activeItemStackUseCount -= 5;
			 	}
				this.faceEntity(source.getImmediateSource(), 90f, 90f);
			} else if (this.setIntangible()) {
				if (source.getTrueSource() instanceof EntityLivingBase) {
					this.setRevengeTarget((EntityLivingBase)source.getTrueSource());
				}
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			if (this.noClip) {
				return false;
			}
			return super.attackEntityAsMob(entityIn);
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			ItemStack stack = this.getHeldItemMainhand();
			if (this.rand.nextFloat() > 0.8f && stack.getItem() == ItemGunbai.block && !((ItemGunbai.RangedItem)stack.getItem()).isThrown(stack)) {
				this.swingArm(EnumHand.MAIN_HAND);
				((ItemGunbai.RangedItem)stack.getItem()).throwItemAt(stack, this, target);
			} else {
				this.swingArm(EnumHand.OFF_HAND);
				EntityWoodCutting.EC.Jutsu.createJutsu(this, target);
			}
		}

		@Override
		public void onLivingUpdate() {
			super.onLivingUpdate();
			if (this.gunbaiOnRetrieval && this.getHeldItemMainhand().getItem() == ItemGunbai.block) {
				ItemStack stack = this.getHeldItemMainhand();
				for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D))) {
					if (entity instanceof ItemGunbai.EntityCustom && this.equals(((ItemGunbai.EntityCustom)entity).getShooter())) {
						((ItemGunbai.RangedItem)stack.getItem()).resetThrown(stack);
						entity.setDead();
						this.gunbaiOnRetrieval = false;
					}
				}
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (--this.intangibleTime <= 0 && !this.world.isRemote && ProcedureOnLivingUpdate.isNoClip(this)) {
				ProcedureOnLivingUpdate.setNoClip(this, false);
			}
		}

		@Override
		protected void onExternalConsumeChakra(double amount) {
			//System.out.println("------ external consume chakra. before="+getChakra()+", new attempt="+amount);
		}

		@Override
		public boolean canBeCollidedWith() {
			return !this.noClip && super.canBeCollidedWith();
		}

		@Override
		public boolean canBePushed() {
			return !this.noClip && super.canBePushed();
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return this.getEntityString().equals("narutomod:tobi") ? SoundEvents.ENTITY_ILLUSION_ILLAGER_AMBIENT : null;
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return SoundEvents.ENTITY_ILLAGER_DEATH;
		}

		@Override
		protected float getSoundPitch() {
			return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.4F;
		}

		@Override
		public int getTalkInterval() {
			return 30;
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/obito.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelObito());
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				float f = 0.0625F * 15.0F;
				GlStateManager.scale(f, f, f);
				if (this.mainModel instanceof ModelObito) {
					((ModelObito)this.mainModel).hair.showModel = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() != ItemMaskObitoWar.helmet;
				}
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
		public class ModelObito extends EntityNinjaMob.ModelNinja {
			//private final ModelRenderer bipedHead;
			private final ModelRenderer hair;
			private final ModelRenderer bone1;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer eyeRight;
			//private final ModelRenderer eyeLeft;
			//private final ModelRenderer bipedHeadwear;
			//private final ModelRenderer bipedBody;
			//private final ModelRenderer bipedRightArm;
			//private final ModelRenderer bipedLeftArm;
			//private final ModelRenderer bipedRightLeg;
			//private final ModelRenderer bipedLeftLeg;
			public ModelObito() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, -1.0F, 0.0F);
				bipedHead.addChild(hair);
				bone1 = new ModelRenderer(this);
				bone1.setRotationPoint(-2.0F, -5.0F, 0.0F);
				hair.addChild(bone1);
				setRotationAngle(bone1, 0.0F, 0.0F, -0.5236F);
				bone1.cubeList.add(new ModelBox(bone1, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-2.0F, -4.5F, -2.0F);
				hair.addChild(bone2);
				setRotationAngle(bone2, 0.3491F, 0.0F, -0.3491F);
				bone2.cubeList.add(new ModelBox(bone2, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-2.0F, -4.5F, 2.0F);
				hair.addChild(bone3);
				setRotationAngle(bone3, -0.3491F, 0.0F, -0.3491F);
				bone3.cubeList.add(new ModelBox(bone3, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -5.0F, -2.0F);
				hair.addChild(bone4);
				setRotationAngle(bone4, 0.5236F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, -5.0F, 2.0F);
				hair.addChild(bone5);
				setRotationAngle(bone5, -0.5236F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(2.0F, -5.0F, 0.0F);
				hair.addChild(bone6);
				setRotationAngle(bone6, 0.0F, 0.0F, 0.5236F);
				bone6.cubeList.add(new ModelBox(bone6, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, true));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(2.0F, -4.5F, 2.0F);
				hair.addChild(bone7);
				setRotationAngle(bone7, -0.3491F, 0.0F, 0.3491F);
				bone7.cubeList.add(new ModelBox(bone7, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, true));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(2.0F, -4.5F, -2.0F);
				hair.addChild(bone8);
				setRotationAngle(bone8, 0.3491F, 0.0F, 0.3491F);
				bone8.cubeList.add(new ModelBox(bone8, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, true));
				eyeRight = new ModelRenderer(this);
				eyeRight.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.addChild(eyeRight);
				eyeRight.cubeList.add(new ModelBox(eyeRight, 40, 52, -7.65F, -8.56F, -9.05F, 12, 12, 0, -5.0F, false));
				//eyeLeft = new ModelRenderer(this);
				//eyeLeft.setRotationPoint(-2.9F, 3.25F, -3.95F);
				//bipedHead.addChild(eyeLeft);
				//eyeLeft.cubeList.add(new ModelBox(eyeLeft, 40, 52, -1.0F, -12.0F, -5.0F, 12, 12, 0, -4.9F, true));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, true));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				setRotationAngle(bipedRightArm, -0.3927F, 0.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				setRotationAngle(bipedLeftArm, 0.3927F, 0.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
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
