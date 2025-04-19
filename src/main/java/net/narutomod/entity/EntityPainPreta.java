
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;
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
public class EntityPainPreta extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 498;
	public static final int ENTITYID_RANGED = 499;

	public EntityPainPreta(ElementsNarutomodMod instance) {
		super(instance, 918);
	}

	@Override
	public void initElements() {
		elements.entities
				.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "pain_preta"), ENTITYID)
						.name("pain_preta").tracker(64, 3, true).egg(-16777216, -26368).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob {
		private int shieldTime;
		
		public EntityCustom(World world) {
			super(world, 130, 7000d);
			this.setSize(0.6125f, 2.0f);
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

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.world.isRemote && !(this.getRidingEntity() instanceof EntityPretaShield.EntityCustom)
			 && source.getTrueSource() instanceof EntityLivingBase && Chakra.pathway((EntityLivingBase)source.getTrueSource()).getAmount() > 0d) {
				EntityPretaShield.EntityCustom entity = new EntityPretaShield.EntityCustom(this);
				this.world.spawnEntity(entity);
				entity.setLifeSpan(100);
			}
			return super.attackEntityFrom(source, amount);
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/pain_preta.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelPainPreta());
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				float f = 0.0625f * 16.0f;
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
		public class ModelPainPreta extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer eyeRight;
			private final ModelRenderer eyeLeft;
			private final ModelRenderer bone4;
			private final ModelRenderer bone;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			public ModelPainPreta() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				eyeRight = new ModelRenderer(this);
				eyeRight.setRotationPoint(3.0F, 3.05F, -3.95F);
				bipedHead.addChild(eyeRight);
				eyeRight.cubeList.add(new ModelBox(eyeRight, 40, 52, -11.0F, -12.0F, -5.0F, 12, 12, 0, -4.9F, false));
				eyeLeft = new ModelRenderer(this);
				eyeLeft.setRotationPoint(-3.0F, 3.05F, -3.95F);
				bipedHead.addChild(eyeLeft);
				eyeLeft.cubeList.add(new ModelBox(eyeLeft, 40, 52, -1.0F, -12.0F, -5.0F, 12, 12, 0, -4.9F, true));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -3.2F, 4.1F);
				bipedHead.addChild(bone4);
				setRotationAngle(bone4, 0.1745F, 0.0F, 0.0F);
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone4.addChild(bone);
				setRotationAngle(bone, 0.1745F, 0.0F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 24, 0, -4.0F, -0.8F, 0.0F, 8, 8, 0, 0.0F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone4.addChild(bone2);
				setRotationAngle(bone2, 0.1309F, -0.7854F, -0.1309F);
				bone2.cubeList.add(new ModelBox(bone2, 24, 0, -4.0F, -0.8F, 0.0F, 8, 8, 0, 0.0F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone4.addChild(bone3);
				setRotationAngle(bone3, 0.0873F, 0.7854F, 0.1309F);
				bone3.cubeList.add(new ModelBox(bone3, 24, 0, -4.0F, -0.8F, 0.0F, 8, 8, 0, 0.0F, true));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F, false));
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 0, 52, -6.0F, -12.0F, -7.75F, 12, 12, 0, -3.5F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-4.0F, -2.25F, -0.3F);
				bipedHeadwear.addChild(bone5);
				setRotationAngle(bone5, -0.1745F, 0.0F, -0.2618F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 0, -0.5F, -4.5F, -0.5F, 1, 4, 1, -0.4F, false));
				bone5.cubeList.add(new ModelBox(bone5, 0, 0, -0.5F, -2.5F, -0.5F, 1, 4, 1, -0.35F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(4.0F, -2.25F, -0.3F);
				bipedHeadwear.addChild(bone6);
				setRotationAngle(bone6, -0.1745F, 0.0F, 0.2618F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -0.5F, -4.5F, -0.5F, 1, 4, 1, -0.4F, true));
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -0.5F, -2.5F, -0.5F, 1, 4, 1, -0.35F, true));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(-3.25F, -1.25F, -3.5F);
				bipedHeadwear.addChild(bone7);
				bone7.cubeList.add(new ModelBox(bone7, 0, 0, 0.0F, -1.2F, -1.0F, 1, 1, 1, -0.2F, false));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(3.25F, -1.25F, -3.5F);
				bipedHeadwear.addChild(bone8);
				bone8.cubeList.add(new ModelBox(bone8, 0, 0, -1.0F, -1.2F, -1.0F, 1, 1, 1, -0.2F, true));
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
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				if (e.getRidingEntity() instanceof EntityPretaShield.EntityCustom) {
					setRotationAngle(bipedRightArm, -1.5708F, 0.5236F, 0.5236F);
					setRotationAngle(bipedLeftArm, -1.5708F, -0.5236F, -0.5236F);
				}
			}
		}
	}
}
