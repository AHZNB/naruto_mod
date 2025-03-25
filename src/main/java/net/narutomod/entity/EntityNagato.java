
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemAkatsukiRobeOld;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;

import javax.annotation.Nullable;
import com.google.common.base.Predicate;

@ElementsNarutomodMod.ModElement.Tag
public class EntityNagato extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 506;
	public static final int ENTITYID_RANGED = 507;

	public EntityNagato(ElementsNarutomodMod instance) {
		super(instance, 922);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "nagato"), ENTITYID)
				.name("nagato").tracker(64, 3, true).egg(-13421773, -52).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob {
		private EntityMechWalker.EntityCustom mechEntity;

		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.setSize(0.525f, 1.8f);
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelectorAkatsuki));
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobeOld.body));
			this.mechEntity = new EntityMechWalker.EntityCustom(this);
			this.world.spawnEntity(this.mechEntity);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(1, new EntityAISwimming(this));
			this.tasks.addTask(3, new EntityAIWatchClosest2(this, EntityPlayer.class, 32.0F, 1.0F));
			this.tasks.addTask(4, new EntityAIWander(this, 1));
			this.tasks.addTask(5, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			
			boolean ridingMech = this.getRidingEntity() instanceof EntityMechWalker.EntityCustom;
			if (ridingMech != this.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty()) {
				this.swapWithInventory(EntityEquipmentSlot.CHEST, 1);
			}
			if (!ridingMech) {
				if (this.mechEntity != null) {
					if (this.mechEntity.isAddedToWorld()) {
						this.startRiding(this.mechEntity);
					}
				} else if (this.ticksExisted < 5) {
					double d = 1000;
					EntityMechWalker.EntityCustom closest = null;
					for (EntityMechWalker.EntityCustom entity : this.world.getEntities(EntityMechWalker.EntityCustom.class, new Predicate<EntityMechWalker.EntityCustom>() {
						@Override
						public boolean apply(@Nullable EntityMechWalker.EntityCustom p_apply_1_) {
							return p_apply_1_ != null && p_apply_1_.isSummoner(EntityCustom.this);
						}
					})) {
						double d1 = entity.getDistance(this);
						if (d1 < d) {
							d = d1;
							closest = entity;
						}
					}
					if (closest != null) {
						this.mechEntity = closest;
						this.startRiding(closest);
					}
				}
			} else if (this.mechEntity == null) {
				this.mechEntity = (EntityMechWalker.EntityCustom)this.getRidingEntity();
			}
		}

		//@Override
		//public void onUpdate() {
		//	super.onUpdate();
		//}

		@Override
		protected boolean canBeRidden(Entity entityIn) {
			return entityIn instanceof EntityMechWalker.EntityCustom;
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/nagato.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelNagato());
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				float f = 0.0625f * 14f;
				GlStateManager.scale(f, 0.0625f * 15.0f, f);
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
		public class ModelNagato extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer eyeLeft;
			private final ModelRenderer spikes;
			private final ModelRenderer bone;
			private final ModelRenderer bone15;
			private final ModelRenderer bone17;
			private final ModelRenderer bone18;
			private final ModelRenderer bone16;
			private final ModelRenderer bone9;
			private final ModelRenderer bone13;
			private final ModelRenderer bone10;
			private final ModelRenderer bone14;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone3;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone8;

			public ModelNagato() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				eyeLeft = new ModelRenderer(this);
				eyeLeft.setRotationPoint(-3.2F, 3.25F, -3.95F);
				bipedHead.addChild(eyeLeft);
				eyeLeft.cubeList.add(new ModelBox(eyeLeft, 40, 52, -1.0F, -12.0F, -5.0F, 12, 12, 0, -4.9F, true));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -6.0F, -4.0F, 8, 8, 8, 0.2F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F, false));

				spikes = new ModelRenderer(this);
				spikes.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(spikes);
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 3.0F, 1.5F);
				spikes.addChild(bone);
				setRotationAngle(bone, -0.7854F, 0.0F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(1.0F, 3.75F, 1.5F);
				spikes.addChild(bone15);
				setRotationAngle(bone15, -0.9599F, 0.1309F, 0.0F);
				bone15.cubeList.add(new ModelBox(bone15, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(1.5F, 4.75F, 1.5F);
				spikes.addChild(bone17);
				setRotationAngle(bone17, -1.0472F, 0.4363F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(-1.5F, 4.75F, 1.5F);
				spikes.addChild(bone18);
				setRotationAngle(bone18, -1.0472F, -0.4363F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(-1.0F, 3.75F, 1.5F);
				spikes.addChild(bone16);
				setRotationAngle(bone16, -0.9599F, -0.1309F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(1.5F, 3.0F, 1.5F);
				spikes.addChild(bone9);
				setRotationAngle(bone9, -0.7854F, 0.3491F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(-1.5F, 3.0F, 1.5F);
				spikes.addChild(bone13);
				setRotationAngle(bone13, -0.7854F, -0.3491F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(3.0F, 2.5F, 1.5F);
				spikes.addChild(bone10);
				setRotationAngle(bone10, -0.7854F, 0.6981F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(-3.0F, 2.5F, 1.5F);
				spikes.addChild(bone14);
				setRotationAngle(bone14, -0.7854F, -0.6981F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(2.5F, 3.5F, 1.5F);
				spikes.addChild(bone11);
				setRotationAngle(bone11, -0.8727F, 0.6109F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(-2.5F, 3.5F, 1.5F);
				spikes.addChild(bone12);
				setRotationAngle(bone12, -0.8727F, -0.6109F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(1.0F, 2.0F, 1.5F);
				spikes.addChild(bone3);
				setRotationAngle(bone3, -0.7854F, 0.2618F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(1.0F, 1.0F, 1.5F);
				spikes.addChild(bone19);
				setRotationAngle(bone19, -0.6109F, 0.3491F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(-1.0F, 1.0F, 1.5F);
				spikes.addChild(bone20);
				setRotationAngle(bone20, -0.6109F, -0.3491F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(2.0F, 2.0F, 1.5F);
				spikes.addChild(bone6);
				setRotationAngle(bone6, -0.7854F, 0.5236F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(3.0F, 1.0F, 1.5F);
				spikes.addChild(bone7);
				setRotationAngle(bone7, -0.6981F, 0.7854F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-1.0F, 2.0F, 1.5F);
				spikes.addChild(bone4);
				setRotationAngle(bone4, -0.7854F, -0.2618F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-2.0F, 2.0F, 1.5F);
				spikes.addChild(bone5);
				setRotationAngle(bone5, -0.7854F, -0.5236F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(-3.0F, 1.0F, 1.5F);
				spikes.addChild(bone8);
				setRotationAngle(bone8, -0.6981F, -0.7854F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 60, 16, -0.5F, -24.0F, -0.5F, 1, 24, 1, -0.2F, false));
				
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				setRotationAngle(bipedRightArm, -0.3927F, 0.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.2F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				setRotationAngle(bipedLeftArm, 0.3927F, 0.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.2F, true));
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

			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				//spikes.showModel = entity.getRidingEntity() instanceof EntityMechWalker.EntityCustom;
				super.render(entity, f, f1, f2, f3, f4, f5);
			}

			@Override
			public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
				super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
				if (entityIn.getRidingEntity() instanceof EntityMechWalker.EntityCustom) {
					bipedRightArm.rotateAngleZ = 0.3491F;
					bipedLeftArm.rotateAngleZ = -0.3491F;
					spikes.showModel = true;
				} else {
					spikes.showModel = false;
				}
			}
		}
	}
}
