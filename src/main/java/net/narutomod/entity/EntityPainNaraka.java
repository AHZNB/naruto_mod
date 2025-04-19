
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemAkatsukiRobe;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPainNaraka extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 502;
	public static final int ENTITYID_RANGED = 503;

	public EntityPainNaraka(ElementsNarutomodMod instance) {
		super(instance, 920);
	}

	@Override
	public void initElements() {
		elements.entities
				.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "pain_naraka"), ENTITYID)
						.name("pain_naraka").tracker(64, 3, true).egg(-16777216, -26368).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob {
		public EntityCustom(World world) {
			super(world, 130, 7000d);
			this.setSize(0.6f, 2.0f);
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
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(14.0D);
		}

		@Override
		protected double meleeReach() {
			return 3.4d;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 0.0F, 24.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && EntityCustom.this.getAttackTarget().posY - EntityCustom.this.posY > 5d;
				}
			});
			this.tasks.addTask(3, new EntityNinjaMob.AIAttackMelee(this, 1.2d, true));
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/pain_naraka.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelPainNaraka());
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				float f = 0.0625f * 15.5f;
				GlStateManager.scale(f, 0.0625f * 16.0f, f);
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
		public class ModelPainNaraka extends EntityNinjaMob.ModelNinja {
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
			private final ModelRenderer eyeLeft;
			private final ModelRenderer bone9;
			private final ModelRenderer bone12;
			private final ModelRenderer bone10;
			private final ModelRenderer bone13;
			private final ModelRenderer bone11;
			private final ModelRenderer bone14;
			public ModelPainNaraka() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 52, -6.0F, -12.0F, -7.55F, 12, 12, 0, -3.5F, false));
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
				eyeRight.setRotationPoint(2.9F, 3.25F, -3.95F);
				bipedHead.addChild(eyeRight);
				eyeRight.cubeList.add(new ModelBox(eyeRight, 40, 52, -11.0F, -12.0F, -5.0F, 12, 12, 0, -4.9F, false));
				eyeLeft = new ModelRenderer(this);
				eyeLeft.setRotationPoint(-2.9F, 3.25F, -3.95F);
				bipedHead.addChild(eyeLeft);
				eyeLeft.cubeList.add(new ModelBox(eyeLeft, 40, 52, -1.0F, -12.0F, -5.0F, 12, 12, 0, -4.9F, true));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F, false));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(-4.0F, -3.75F, -0.3F);
				bipedHeadwear.addChild(bone9);
				setRotationAngle(bone9, -0.1745F, 0.0F, -0.2618F);
				bone9.cubeList.add(new ModelBox(bone9, 0, 0, -0.5F, -3.5F, -0.5F, 1, 4, 1, -0.4F, false));
				bone9.cubeList.add(new ModelBox(bone9, 0, 0, -0.5F, -1.5F, -0.5F, 1, 4, 1, -0.35F, false));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(4.0F, -3.75F, -0.3F);
				bipedHeadwear.addChild(bone12);
				setRotationAngle(bone12, -0.1745F, 0.0F, 0.2618F);
				bone12.cubeList.add(new ModelBox(bone12, 0, 0, -0.5F, -3.5F, -0.5F, 1, 4, 1, -0.4F, true));
				bone12.cubeList.add(new ModelBox(bone12, 0, 0, -0.5F, -1.5F, -0.5F, 1, 4, 1, -0.35F, true));
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(-4.0F, -3.25F, -0.05F);
				bipedHeadwear.addChild(bone10);
				setRotationAngle(bone10, -0.6109F, 0.0F, -0.4363F);
				bone10.cubeList.add(new ModelBox(bone10, 0, 0, -0.5F, -3.5F, -0.5F, 1, 4, 1, -0.4F, false));
				bone10.cubeList.add(new ModelBox(bone10, 0, 0, -0.5F, -1.5F, -0.5F, 1, 4, 1, -0.35F, false));
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(4.0F, -3.25F, -0.05F);
				bipedHeadwear.addChild(bone13);
				setRotationAngle(bone13, -0.6109F, 0.0F, 0.4363F);
				bone13.cubeList.add(new ModelBox(bone13, 0, 0, -0.5F, -3.5F, -0.5F, 1, 4, 1, -0.4F, true));
				bone13.cubeList.add(new ModelBox(bone13, 0, 0, -0.5F, -1.5F, -0.5F, 1, 4, 1, -0.35F, true));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(-4.0F, -2.75F, -0.05F);
				bipedHeadwear.addChild(bone11);
				setRotationAngle(bone11, -1.0036F, 0.0F, -0.6981F);
				bone11.cubeList.add(new ModelBox(bone11, 0, 0, -0.5F, -3.5F, -0.5F, 1, 4, 1, -0.4F, false));
				bone11.cubeList.add(new ModelBox(bone11, 0, 0, -0.5F, -1.5F, -0.5F, 1, 4, 1, -0.35F, false));
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(4.0F, -2.75F, -0.05F);
				bipedHeadwear.addChild(bone14);
				setRotationAngle(bone14, -1.0036F, 0.0F, 0.6981F);
				bone14.cubeList.add(new ModelBox(bone14, 0, 0, -0.5F, -3.5F, -0.5F, 1, 4, 1, -0.4F, true));
				bone14.cubeList.add(new ModelBox(bone14, 0, 0, -0.5F, -1.5F, -0.5F, 1, 4, 1, -0.35F, true));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.2F, false));
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
		}
	}
}
