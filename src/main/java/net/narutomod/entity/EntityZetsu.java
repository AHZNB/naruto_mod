
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Iterator;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityZetsu extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 508;
	public static final int ENTITYID_RANGED = 509;

	public EntityZetsu(ElementsNarutomodMod instance) {
		super(instance, 924);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "zetsu"), ENTITYID)
				.name("zetsu").tracker(64, 3, true).egg(-16777216, -13408768).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private final double treeChakraUsage = 60.0d;
		private final List<EntityWoodForest.EC> spawnedTrees = Lists.newArrayList();
		private int deathTicks;
		
		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.setSize(0.6f, 1.8f);
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelectorAkatsuki));
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			ItemStack stack = new ItemStack(ItemAkatsukiRobe.body);
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setBoolean("collarOpen", true);
			stack.getTagCompound().setFloat("customWidth", 1.25f);
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
		}

		@Override
		protected double meleeReach() {
			return 3.4d;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false) {
				@Override
				public boolean shouldContinueExecuting() {
					return super.shouldContinueExecuting() && !EntityCustom.this.isCaptured(this.taskOwner.getAttackTarget());
				}
			});
			this.tasks.addTask(0, new EntityAISwimming(this));
			//this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 1.0F));
			//this.tasks.addTask(4, new EntityNinjaMob.AIAttackMelee(this, 1.2d, true));
			this.tasks.addTask(4, new EntityNinjaMob.AIAttackRangedTactical(this, 0.8d, 60, 20f));
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
		public void setSwingingArms(boolean swingingArms) {
			ProcedureOnLivingUpdate.forceBowPose(this, swingingArms);
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			if (!this.isCaptured(target) && this.consumeChakra(this.treeChakraUsage)) {
				Vec3d vec = target.getPositionVector().add(new Vec3d(target.motionX, target.motionY, target.motionZ).scale(10));
				EntityWoodForest.EC ecEntity = new EntityWoodForest.EC(this, 
				 ProcedureUtils.getGroundBelow(this.world, MathHelper.floor(vec.x), MathHelper.floor(vec.y), MathHelper.floor(vec.z)), 4f + this.rand.nextFloat() * 8f);
				ecEntity.setLifespan(300);
				this.world.spawnEntity(ecEntity);
				this.spawnedTrees.add(ecEntity);
			}
		}

		public boolean isCaptured(EntityLivingBase target) {
			boolean captured = false;
			Iterator<EntityWoodForest.EC> iter = this.spawnedTrees.iterator();
			while (!captured && iter.hasNext()) {
				EntityWoodForest.EC ecEntity = iter.next();
				if (ecEntity.isDead) {
					iter.remove();
				} else if (ecEntity.getCapturedTargets().contains(target)) {
					captured = true;
				}
			}
			return captured;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.ticksExisted % 20 == 2) {
				float health = this.getHealth();
				if (health > 0.0f && health < this.getMaxHealth()) {
					this.heal(1.0f);
				}
			}
		}

		@Override
		protected void collideWithNearbyEntities() {
			super.collideWithNearbyEntities();
			if (this.isEntityAlive()) {
				if (!this.world.getEntitiesWithinAABB(EntityWoodForest.EC.class, this.getEntityBoundingBox()).isEmpty()) {
					if (!ProcedureOnLivingUpdate.isNoClip(this)) {
						ProcedureOnLivingUpdate.setNoClip(this, true);
					}
				} else if (ProcedureOnLivingUpdate.isNoClip(this)) {
					ProcedureOnLivingUpdate.setNoClip(this, false);
				}
			}
		}

		@Override
		protected void onDeathUpdate() {
			if (this.onGround) {
				++this.deathTicks;
				ProcedureOnLivingUpdate.setNoClip(this, true);
				this.motionY -= 0.04d;
			}
			if (this.deathTicks == 100) {
				this.setDead();
			}
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere() && (int)this.posY >= this.world.getSeaLevel() && this.world.canSeeSky(this.getPosition())
			 && this.world.getEntities(EntityCustom.class, EntitySelectors.IS_ALIVE).isEmpty()
			 && !EntityNinjaMob.SpawnData.spawnedRecentlyHere(this, 36000);
			 //&& this.rand.nextInt(5) == 0;
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/zetsu.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelZetsu());
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				float f = 0.0625F * 15.0F;
				GlStateManager.scale(f, f, f);
				boolean flag = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty();
				((ModelZetsu)this.mainModel).flytrapRight.showModel = !flag;
				((ModelZetsu)this.mainModel).flytrapLeft.showModel = !flag;
				((ModelZetsu)this.mainModel).flaps.showModel = flag;
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
		public class ModelZetsu extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer hair;
			private final ModelRenderer bone1;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer flytrapRight;
			private final ModelRenderer bone16;
			private final ModelRenderer bone;
			private final ModelRenderer bone14;
			private final ModelRenderer bone9;
			private final ModelRenderer bone15;
			private final ModelRenderer flytrapLeft;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone13;
			private final ModelRenderer bone17;
			private final ModelRenderer flaps;
			private final ModelRenderer bone18;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer bone23;
			public ModelZetsu() {
				textureWidth = 64;
				textureHeight = 96;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, -1.0F, 0.0F);
				bipedHeadwear.addChild(hair);
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
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.3F, false));
				flytrapRight = new ModelRenderer(this);
				flytrapRight.setRotationPoint(-4.0F, 2.0F, 0.0F);
				bipedBody.addChild(flytrapRight);
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(-4.55F, -13.1F, 0.0F);
				flytrapRight.addChild(bone16);
				setRotationAngle(bone16, 0.0F, 0.0F, 0.0175F);
				bone16.cubeList.add(new ModelBox(bone16, 35, 66, 0.0F, -8.0F, -3.0F, 8, 8, 6, 0.0F, false));
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 2.0F, -2.0F);
				flytrapRight.addChild(bone);
				setRotationAngle(bone, 0.3491F, 0.2618F, -0.2618F);
				bone.cubeList.add(new ModelBox(bone, 32, 40, 0.0F, -16.0F, 0.0F, 8, 16, 8, 0.0F, false));
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, -16.0F, 0.0F);
				bone.addChild(bone14);
				setRotationAngle(bone14, -0.2618F, 0.0F, 0.2618F);
				bone14.cubeList.add(new ModelBox(bone14, 8, 56, 0.0F, -8.0F, 0.0F, 8, 8, 8, 0.0F, false));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 2.0F, 2.0F);
				flytrapRight.addChild(bone9);
				setRotationAngle(bone9, 0.3491F, 2.8798F, -0.2618F);
				bone9.cubeList.add(new ModelBox(bone9, 32, 40, -8.0F, -16.0F, 0.0F, 8, 16, 8, 0.0F, true));
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.0F, -16.0F, 0.0F);
				bone9.addChild(bone15);
				setRotationAngle(bone15, -0.2618F, 0.0F, -0.2618F);
				bone15.cubeList.add(new ModelBox(bone15, 8, 56, -8.0F, -8.0F, 0.0F, 8, 8, 8, 0.0F, true));
				flytrapLeft = new ModelRenderer(this);
				flytrapLeft.setRotationPoint(4.0F, 2.0F, 0.0F);
				bipedBody.addChild(flytrapLeft);
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(4.55F, -13.1F, 0.0F);
				flytrapLeft.addChild(bone10);
				setRotationAngle(bone10, 0.0F, 0.0F, -0.0175F);
				bone10.cubeList.add(new ModelBox(bone10, 35, 66, -8.0F, -8.0F, -3.0F, 8, 8, 6, 0.0F, true));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, 2.0F, -2.0F);
				flytrapLeft.addChild(bone11);
				setRotationAngle(bone11, 0.3491F, -0.2618F, 0.2618F);
				bone11.cubeList.add(new ModelBox(bone11, 32, 40, -8.0F, -16.0F, 0.0F, 8, 16, 8, 0.0F, true));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, -16.0F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, -0.2618F, 0.0F, -0.2618F);
				bone12.cubeList.add(new ModelBox(bone12, 8, 56, -8.0F, -8.0F, 0.0F, 8, 8, 8, 0.0F, true));
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, 2.0F, 2.0F);
				flytrapLeft.addChild(bone13);
				setRotationAngle(bone13, 0.3491F, -2.8798F, 0.2618F);
				bone13.cubeList.add(new ModelBox(bone13, 32, 40, 0.0F, -16.0F, 0.0F, 8, 16, 8, 0.0F, false));
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, -16.0F, 0.0F);
				bone13.addChild(bone17);
				setRotationAngle(bone17, -0.2618F, 0.0F, 0.2618F);
				bone17.cubeList.add(new ModelBox(bone17, 8, 56, 0.0F, -8.0F, 0.0F, 8, 8, 8, 0.0F, false));
				flaps = new ModelRenderer(this);
				flaps.setRotationPoint(-2.0F, 8.0F, 0.0F);
				bipedBody.addChild(flaps);
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone18);
				setRotationAngle(bone18, 0.0F, 0.0F, -2.3562F);
				bone18.cubeList.add(new ModelBox(bone18, 56, 16, -2.0F, -12.0F, 0.0F, 4, 12, 0, 0.0F, false));
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone21);
				setRotationAngle(bone21, -0.5236F, 0.0F, -2.0944F);
				bone21.cubeList.add(new ModelBox(bone21, 56, 16, -2.0F, -12.0F, 0.0F, 4, 12, 0, 0.0F, false));
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone22);
				setRotationAngle(bone22, -1.0472F, 0.0F, -1.8326F);
				bone22.cubeList.add(new ModelBox(bone22, 56, 16, -2.0F, -12.0F, 0.0F, 4, 12, 0, 0.0F, false));
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone19);
				setRotationAngle(bone19, -0.7854F, -0.2618F, -2.618F);
				bone19.cubeList.add(new ModelBox(bone19, 56, 16, -2.0F, -12.0F, 0.0F, 4, 12, 0, 0.0F, false));
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone20);
				setRotationAngle(bone20, 0.4363F, 0.0F, -2.5307F);
				bone20.cubeList.add(new ModelBox(bone20, 56, 16, -2.0F, -12.0F, 0.0F, 4, 12, 0, 0.0F, false));
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone23);
				setRotationAngle(bone23, 0.8727F, 0.0F, -2.8798F);
				bone23.cubeList.add(new ModelBox(bone23, 56, 16, -2.0F, -12.0F, 0.0F, 4, 12, 0, 0.0F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, true));
			}
		}
	}
}
