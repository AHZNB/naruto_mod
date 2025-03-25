
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.potion.*;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityMechWalker extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 504;
	public static final int ENTITYID_RANGED = 505;
	private static final float MODELSCALE = 1.5F;

	public EntityMechWalker(ElementsNarutomodMod instance) {
		super(instance, 921);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "mech_walker"), ENTITYID).name("mech_walker").tracker(64, 3, true).build());
	}

	public static class EntityCustom extends EntityShieldBase {
		public static final float MAXHEALTH = 300.0f;
		private final float driveSpeed = 0.5F;

		public EntityCustom(World world) {
			super(world);
			this.setSize(1.0f * MODELSCALE, 1.625f * MODELSCALE);
			this.stepHeight = MODELSCALE * 1.5f;
			this.dieOnNoPassengers = false;
			this.setAlwaysRenderNameTag(false);
			this.effectivePotions.addAll(Lists.newArrayList(PotionAmaterasuFlame.potion, PotionCorrosion.potion, PotionInstantDamage.potion));
		}

		public EntityCustom(EntityLivingBase summonerIn) {
			this(summonerIn, summonerIn.posX, summonerIn.posY, summonerIn.posZ);
		}

		public EntityCustom(EntityLivingBase summonerIn, double x, double y, double z) {
			this(summonerIn.world);
			this.setSummoner(summonerIn);
			this.setLocationAndAngles(x, y, z, summonerIn.rotationYaw, summonerIn.rotationPitch);
			this.setHealth(this.getMaxHealth());
		}

		@Override
		protected void playStepSound(BlockPos pos, Block blockIn) {
			this.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 0.5f, 0.8f);
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return null;
		}

		@Override
		public SoundEvent getDeathSound() {
			return SoundEvents.BLOCK_METAL_BREAK;
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAXHEALTH);
			//this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3D);
		}

		@Override
		public boolean shouldRiderSit() {
			return false;
		}

		@Override
		public double getMountedYOffset() {
			return 0.4375d * MODELSCALE + 0.35d;
		}

		@Override
		protected void addPassenger(Entity passenger) {
			super.addPassenger(passenger);
			if (passenger instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)passenger, ItemNinjutsu.block);
				boolean flag = stack != null && ((ItemNinjutsu.RangedItem)stack.getItem())
				 .canActivateJutsu(stack, ItemNinjutsu.PUPPET, (EntityPlayer)passenger) == EnumActionResult.SUCCESS;
				this.setOwnerCanSteer(flag, this.driveSpeed);
			} else if (passenger instanceof EntityNagato.EntityCustom) {
				this.setOwnerCanSteer(true, this.driveSpeed);
			}
		}

		@Override
		public boolean processInitialInteract(EntityPlayer entity, EnumHand hand) {
			if (!this.world.isRemote) {// && entity.getHeldItem(hand).getItem() != ItemScrollSanshouo.block) {
				entity.startRiding(this);
				return true;
			}
			return false;
		}

		@Override
		protected void turnBodyAndHead(Entity passenger) {
			this.renderYawOffset = passenger instanceof EntityLivingBase ? ((EntityLivingBase)passenger).renderYawOffset : passenger.rotationYaw;
			this.rotationYaw = this.renderYawOffset;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = passenger.rotationPitch;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.rotationYawHead = this.rotationYaw;
		}

		private int getArmSwingAnimationEnd() {
			return 20;
		}

		@Override
		public void swingArm(EnumHand hand) {
			if (!this.isSwingInProgress) {
				this.isSwingInProgress = true;
				if (!this.world.isRemote) {
					this.world.setEntityState(this, (byte)102);
				}
			}
		}

		private void toggleArmSwing() {
			this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:grill_open")), 0.8F, this.isArmsOpen() ? 0.3F : 0.6F);
			this.swingArm(EnumHand.MAIN_HAND);
		}

		@Override
		protected void updateArmSwingProgress() {
			int i = getArmSwingAnimationEnd();
			if (this.isSwingInProgress) {
				this.swingProgressInt++;
				if (this.swingProgressInt == i / 2) {
					this.isSwingInProgress = false;
				}
				if (this.swingProgressInt >= i) {
					this.swingProgressInt = 0;
					this.isSwingInProgress = false;
				}
			}
			this.prevSwingProgress = this.swingProgress;
			this.swingProgress = (float) this.swingProgressInt / (float) i;
		}

		private boolean isArmsOpen() {
			return (!this.isSwingInProgress && this.swingProgressInt == this.getArmSwingAnimationEnd() / 2);
		}

		@Override
		public void onLivingUpdate() {
			this.updateArmSwingProgress();
			super.onLivingUpdate();
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.ticksExisted % 20 == 1) {
				Entity passenger = this.getControllingPassenger();
				if (passenger instanceof EntityLivingBase && this.canBeSteered()
				 && !Chakra.pathway((EntityLivingBase)passenger).consume(ItemNinjutsu.PUPPET.chakraUsage * 20)) {
					this.setOwnerCanSteer(false, 0);
					this.world.setEntityState(this, (byte)101);
				}
			}
			//this.setAge(this.getAge() + 1);
		}

		@Override
		public void applyEntityCollision(Entity entityIn) {
			if (!this.isRidingSameEntity(entityIn) && !entityIn.noClip && !this.noClip) {
                double d0 = entityIn.posX - this.posX;
                double d1 = entityIn.posZ - this.posZ;
                double d2 = MathHelper.absMax(d0, d1);
                if (d2 >= 0.01D) {
                    d2 = (double)MathHelper.sqrt(d2);
                    d0 = d0 / d2;
                    d1 = d1 / d2;
                    double d3 = 1.0D / d2;
                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }
                    d0 = d0 * d3;
                    d1 = d1 * d3;
                    d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                    d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
                    if (!this.isBeingRidden()) {
                        this.addVelocity(-d0 * 0.05d, 0.0D, -d1 * 0.05d);
                    }
                    if (!entityIn.isBeingRidden()) {
	                    double d4 = this.getPositionVector().subtract(this.prevPosX, this.prevPosY, this.prevPosZ).lengthVector();
	                    if (d4 < 0.05d) {
	                    	d4 = 0.05d;
	                    }
                        entityIn.addVelocity(d0 * d4, d4 * 0.5d, d1 * d4);
                    }
                }
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void handleStatusUpdate(byte id) {
			if (id == 101) {
				this.setOwnerCanSteer(false, 0);
			} else if (id == 102) {
				this.swingArm(EnumHand.MAIN_HAND);
			} else {
				super.handleStatusUpdate(id);
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLivingBase<EntityCustom>(renderManager, new ModelMech(), 0.5f * MODELSCALE) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/mechwalker.png");
					@Override
					protected ResourceLocation getEntityTexture(EntityCustom entity) {
						return this.texture;
					}
					@Override
					public float prepareScale(EntityCustom entity, float partialTicks) {
						super.prepareScale(entity, partialTicks);
						GlStateManager.translate(0.0F, 1.5F - (1.5F * MODELSCALE), 0.0F);
						GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
						return 0.0625F;
					}
					@Override
					protected boolean canRenderName(EntityCustom entity) {
						return false;
					}
				};
			});
		}

		// Made with Blockbench 4.12.3
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelMech extends ModelBase {
			private final ModelRenderer body;
			private final ModelRenderer grillRight;
			private final ModelRenderer bone48;
			private final ModelRenderer grillLeft;
			private final ModelRenderer bone50;
			private final ModelRenderer armHolderRight;
			private final ModelRenderer bone2;
			private final ModelRenderer bone47;
			private final ModelRenderer armHolderLeft;
			private final ModelRenderer bone49;
			private final ModelRenderer bone51;
			private final ModelRenderer backboard;
			private final ModelRenderer bone5;
			private final ModelRenderer bone;
			private final ModelRenderer bone3;
			private final ModelRenderer bone10;
			private final ModelRenderer bone16;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer bone38;
			private final ModelRenderer bone22;
			private final ModelRenderer bone27;
			private final ModelRenderer bone28;
			private final ModelRenderer bone30;
			private final ModelRenderer bone39;
			private final ModelRenderer bone40;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer bone34;
			private final ModelRenderer bone43;
			private final ModelRenderer bone44;
			private final ModelRenderer bone45;
			private final ModelRenderer bone46;
			private final ModelRenderer leg1;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer bone4;
			private final ModelRenderer bone9;
			private final ModelRenderer bone6;
			private final ModelRenderer leg2;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone13;
			private final ModelRenderer bone14;
			private final ModelRenderer bone15;
			private final ModelRenderer leg3;
			private final ModelRenderer bone17;
			private final ModelRenderer bone18;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			private final ModelRenderer leg4;
			private final ModelRenderer bone23;
			private final ModelRenderer bone24;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			private final ModelRenderer bone29;
			
			public ModelMech() {
				textureWidth = 64;
				textureHeight = 64;
		
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 22.0F, 0.0F);
				body.cubeList.add(new ModelBox(body, 0, 0, -8.0F, -12.0F, -8.0F, 16, 12, 16, 0.0F, false));
		
				grillRight = new ModelRenderer(this);
				grillRight.setRotationPoint(0.0F, 0.0F, 0.0F);
				body.addChild(grillRight);
				setRotationAngle(grillRight, 0.0F, 0.4625F, 0.0F);
				grillRight.cubeList.add(new ModelBox(grillRight, 28, 51, -3.6614F, -7.0F, -10.6848F, 9, 7, 0, 0.0F, false));
		
				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(1.3386F, -7.0F, -10.6848F);
				grillRight.addChild(bone48);
				setRotationAngle(bone48, -0.7767F, 0.0F, 0.0F);
				bone48.cubeList.add(new ModelBox(bone48, 26, 44, -8.0F, -7.0F, 0.0F, 12, 7, 0, 0.0F, false));
		
				grillLeft = new ModelRenderer(this);
				grillLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
				body.addChild(grillLeft);
				setRotationAngle(grillLeft, 0.0F, -0.4625F, 0.0F);
				grillLeft.cubeList.add(new ModelBox(grillLeft, 28, 51, -5.3386F, -7.0F, -10.6848F, 9, 7, 0, 0.0F, true));
		
				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(-1.3386F, -7.0F, -10.6848F);
				grillLeft.addChild(bone50);
				setRotationAngle(bone50, -0.7767F, 0.0F, 0.0F);
				bone50.cubeList.add(new ModelBox(bone50, 26, 44, -4.0F, -7.0F, 0.0F, 12, 7, 0, 0.0F, true));
		
				armHolderRight = new ModelRenderer(this);
				armHolderRight.setRotationPoint(-9.0F, -6.0F, 0.0F);
				body.addChild(armHolderRight);
				armHolderRight.cubeList.add(new ModelBox(armHolderRight, 0, 0, 0.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -4.0F, 0.0F);
				armHolderRight.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.0F, 0.2618F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 0, 0.0F, -4.0F, -2.0F, 4, 4, 4, -0.01F, false));
		
				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(0.0F, -4.0F, 0.0F);
				bone2.addChild(bone47);
				setRotationAngle(bone47, 0.0F, 0.0F, 0.1745F);
				bone47.cubeList.add(new ModelBox(bone47, 0, 0, 0.0F, -4.0F, -2.0F, 4, 4, 4, -0.02F, false));
		
				armHolderLeft = new ModelRenderer(this);
				armHolderLeft.setRotationPoint(9.0F, -6.0F, 0.0F);
				body.addChild(armHolderLeft);
				armHolderLeft.cubeList.add(new ModelBox(armHolderLeft, 0, 0, -4.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, true));
		
				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(0.0F, -4.0F, 0.0F);
				armHolderLeft.addChild(bone49);
				setRotationAngle(bone49, 0.0F, 0.0F, -0.2618F);
				bone49.cubeList.add(new ModelBox(bone49, 0, 0, -4.0F, -4.0F, -2.0F, 4, 4, 4, -0.01F, true));
		
				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(0.0F, -4.0F, 0.0F);
				bone49.addChild(bone51);
				setRotationAngle(bone51, 0.0F, 0.0F, -0.1745F);
				bone51.cubeList.add(new ModelBox(bone51, 0, 0, -4.0F, -4.0F, -2.0F, 4, 4, 4, -0.02F, true));
		
				backboard = new ModelRenderer(this);
				backboard.setRotationPoint(0.0F, -12.0F, -2.0F);
				body.addChild(backboard);
				backboard.cubeList.add(new ModelBox(backboard, 0, 50, -16.0F, -16.0F, 8.0F, 12, 8, 2, 0.0F, false));
				backboard.cubeList.add(new ModelBox(backboard, 0, 50, 4.0F, -16.0F, 8.0F, 12, 8, 2, 0.0F, true));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, -6.0F, 9.0F);
				backboard.addChild(bone5);
				setRotationAngle(bone5, 0.0F, 0.0F, 1.5708F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 50, -6.0F, -4.0F, -1.0F, 12, 8, 2, 0.0F, false));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-14.0F, -12.0F, 8.0F);
				backboard.addChild(bone);
				bone.cubeList.add(new ModelBox(bone, 48, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, -0.5F);
				bone.addChild(bone3);
				setRotationAngle(bone3, -0.0436F, 0.0F, -0.2618F);
				bone3.cubeList.add(new ModelBox(bone3, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, false));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone3.addChild(bone10);
				setRotationAngle(bone10, -0.0436F, 0.0F, -0.2618F);
				bone10.cubeList.add(new ModelBox(bone10, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone10.addChild(bone16);
				setRotationAngle(bone16, 0.0F, 0.0F, -0.2618F);
				bone16.cubeList.add(new ModelBox(bone16, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, false));
		
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(14.0F, -12.0F, 8.0F);
				backboard.addChild(bone35);
				bone35.cubeList.add(new ModelBox(bone35, 48, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, true));
		
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, 0.0F, -0.5F);
				bone35.addChild(bone36);
				setRotationAngle(bone36, -0.0436F, 0.0F, 0.2618F);
				bone36.cubeList.add(new ModelBox(bone36, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, true));
		
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone36.addChild(bone37);
				setRotationAngle(bone37, -0.0436F, 0.0F, 0.2618F);
				bone37.cubeList.add(new ModelBox(bone37, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, true));
		
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone37.addChild(bone38);
				setRotationAngle(bone38, 0.0F, 0.0F, 0.2618F);
				bone38.cubeList.add(new ModelBox(bone38, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, true));
		
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(-10.0F, -12.0F, 8.0F);
				backboard.addChild(bone22);
				bone22.cubeList.add(new ModelBox(bone22, 48, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
		
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, 0.0F, -0.5F);
				bone22.addChild(bone27);
				setRotationAngle(bone27, -0.0436F, 0.0F, -0.1745F);
				bone27.cubeList.add(new ModelBox(bone27, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, false));
		
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone27.addChild(bone28);
				setRotationAngle(bone28, -0.0436F, 0.0F, -0.2618F);
				bone28.cubeList.add(new ModelBox(bone28, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, false));
		
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone28.addChild(bone30);
				setRotationAngle(bone30, 0.0F, 0.0F, -0.2618F);
				bone30.cubeList.add(new ModelBox(bone30, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, false));
		
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(10.0F, -12.0F, 8.0F);
				backboard.addChild(bone39);
				bone39.cubeList.add(new ModelBox(bone39, 48, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, true));
		
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(0.0F, 0.0F, -0.5F);
				bone39.addChild(bone40);
				setRotationAngle(bone40, -0.0436F, 0.0F, 0.1745F);
				bone40.cubeList.add(new ModelBox(bone40, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, true));
		
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone40.addChild(bone41);
				setRotationAngle(bone41, -0.0436F, 0.0F, 0.2618F);
				bone41.cubeList.add(new ModelBox(bone41, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, true));
		
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone41.addChild(bone42);
				setRotationAngle(bone42, 0.0F, 0.0F, 0.2618F);
				bone42.cubeList.add(new ModelBox(bone42, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, true));
		
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(-6.0F, -12.0F, 8.0F);
				backboard.addChild(bone31);
				bone31.cubeList.add(new ModelBox(bone31, 48, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, false));
		
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(0.0F, 0.0F, -0.5F);
				bone31.addChild(bone32);
				setRotationAngle(bone32, -0.0436F, 0.0F, -0.0873F);
				bone32.cubeList.add(new ModelBox(bone32, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, false));
		
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone32.addChild(bone33);
				setRotationAngle(bone33, -0.0436F, 0.0F, -0.2618F);
				bone33.cubeList.add(new ModelBox(bone33, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, false));
		
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone33.addChild(bone34);
				setRotationAngle(bone34, 0.0F, 0.0F, -0.2618F);
				bone34.cubeList.add(new ModelBox(bone34, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, false));
		
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(6.0F, -12.0F, 8.0F);
				backboard.addChild(bone43);
				bone43.cubeList.add(new ModelBox(bone43, 48, 0, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.0F, true));
		
				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(0.0F, 0.0F, -0.5F);
				bone43.addChild(bone44);
				setRotationAngle(bone44, -0.0436F, 0.0F, 0.0873F);
				bone44.cubeList.add(new ModelBox(bone44, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, true));
		
				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone44.addChild(bone45);
				setRotationAngle(bone45, -0.0436F, 0.0F, 0.2618F);
				bone45.cubeList.add(new ModelBox(bone45, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, true));
		
				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(0.0F, 5.0F, 0.0F);
				bone45.addChild(bone46);
				setRotationAngle(bone46, 0.0F, 0.0F, 0.2618F);
				bone46.cubeList.add(new ModelBox(bone46, 48, 0, -0.5F, -0.5F, -0.5F, 1, 6, 1, -0.4F, true));
		
				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(0.0F, -5.0F, 0.0F);
				body.addChild(leg1);
				setRotationAngle(leg1, 0.0F, -0.5236F, 0.0F);
				leg1.cubeList.add(new ModelBox(leg1, 0, 28, -19.0F, -10.0F, -3.0F, 6, 16, 6, 0.0F, false));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(-8.0F, 1.0F, 0.0F);
				leg1.addChild(bone7);
				setRotationAngle(bone7, 0.0F, 0.0F, 0.5236F);
				bone7.cubeList.add(new ModelBox(bone7, 24, 28, -8.0F, -2.0F, -2.0F, 10, 4, 4, -0.1F, false));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(-8.0F, -2.0F, 0.0F);
				bone7.addChild(bone8);
				setRotationAngle(bone8, 0.0F, 0.0F, 1.0472F);
				bone8.cubeList.add(new ModelBox(bone8, 24, 36, 0.0F, 0.0F, -2.0F, 8, 4, 4, 0.0F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-19.0F, 6.0F, -3.0F);
				leg1.addChild(bone4);
				setRotationAngle(bone4, -0.3491F, 0.0F, 0.3491F);
				bone4.cubeList.add(new ModelBox(bone4, 18, 28, 0.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F, false));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(-13.0F, 6.0F, -3.0F);
				leg1.addChild(bone9);
				setRotationAngle(bone9, -0.3491F, 0.0F, -0.3491F);
				bone9.cubeList.add(new ModelBox(bone9, 18, 28, -2.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F, true));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(-19.0F, 6.0F, 3.0F);
				leg1.addChild(bone6);
				setRotationAngle(bone6, 0.3491F, 0.0F, 0.3491F);
				bone6.cubeList.add(new ModelBox(bone6, 18, 28, 0.0F, -1.0F, -2.0F, 2, 2, 2, 0.0F, false));
		
				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(0.0F, -5.0F, 0.0F);
				body.addChild(leg2);
				setRotationAngle(leg2, 0.0F, 0.5236F, 0.0F);
				leg2.cubeList.add(new ModelBox(leg2, 0, 28, 13.0F, -10.0F, -3.0F, 6, 16, 6, 0.0F, true));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(8.0F, 1.0F, 0.0F);
				leg2.addChild(bone11);
				setRotationAngle(bone11, 0.0F, 0.0F, -0.5236F);
				bone11.cubeList.add(new ModelBox(bone11, 24, 28, -2.0F, -2.0F, -2.0F, 10, 4, 4, -0.1F, true));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(8.0F, -2.0F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, 0.0F, 0.0F, -1.0472F);
				bone12.cubeList.add(new ModelBox(bone12, 24, 36, -8.0F, 0.0F, -2.0F, 8, 4, 4, 0.0F, true));
		
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(19.0F, 6.0F, -3.0F);
				leg2.addChild(bone13);
				setRotationAngle(bone13, -0.3491F, 0.0F, -0.3491F);
				bone13.cubeList.add(new ModelBox(bone13, 18, 28, -2.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F, true));
		
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(13.0F, 6.0F, -3.0F);
				leg2.addChild(bone14);
				setRotationAngle(bone14, -0.3491F, 0.0F, 0.3491F);
				bone14.cubeList.add(new ModelBox(bone14, 18, 28, 0.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F, false));
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(19.0F, 6.0F, 3.0F);
				leg2.addChild(bone15);
				setRotationAngle(bone15, 0.3491F, 0.0F, -0.3491F);
				bone15.cubeList.add(new ModelBox(bone15, 18, 28, -2.0F, -1.0F, -2.0F, 2, 2, 2, 0.0F, true));
		
				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(0.0F, -5.0F, 0.0F);
				body.addChild(leg3);
				setRotationAngle(leg3, 0.0F, 0.5236F, 0.0F);
				leg3.cubeList.add(new ModelBox(leg3, 0, 28, -19.0F, -10.0F, -3.0F, 6, 16, 6, 0.0F, false));
		
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(-8.0F, 1.0F, 0.0F);
				leg3.addChild(bone17);
				setRotationAngle(bone17, 0.0F, 0.0F, 0.5236F);
				bone17.cubeList.add(new ModelBox(bone17, 24, 28, -8.0F, -2.0F, -2.0F, 10, 4, 4, -0.1F, false));
		
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(-8.0F, -2.0F, 0.0F);
				bone17.addChild(bone18);
				setRotationAngle(bone18, 0.0F, 0.0F, 1.0472F);
				bone18.cubeList.add(new ModelBox(bone18, 24, 36, 0.0F, 0.0F, -2.0F, 8, 4, 4, 0.0F, false));
		
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(-19.0F, 6.0F, -3.0F);
				leg3.addChild(bone19);
				setRotationAngle(bone19, -0.3491F, 0.0F, 0.3491F);
				bone19.cubeList.add(new ModelBox(bone19, 18, 28, 0.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F, false));
		
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(-13.0F, 6.0F, 3.0F);
				leg3.addChild(bone20);
				setRotationAngle(bone20, 0.3491F, 0.0F, -0.3491F);
				bone20.cubeList.add(new ModelBox(bone20, 18, 28, -2.0F, -1.0F, -2.0F, 2, 2, 2, 0.0F, true));
		
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(-19.0F, 6.0F, 3.0F);
				leg3.addChild(bone21);
				setRotationAngle(bone21, 0.3491F, 0.0F, 0.3491F);
				bone21.cubeList.add(new ModelBox(bone21, 18, 28, 0.0F, -1.0F, -2.0F, 2, 2, 2, 0.0F, false));
		
				leg4 = new ModelRenderer(this);
				leg4.setRotationPoint(0.0F, -5.0F, 0.0F);
				body.addChild(leg4);
				setRotationAngle(leg4, 0.0F, -0.5236F, 0.0F);
				leg4.cubeList.add(new ModelBox(leg4, 0, 28, 13.0F, -10.0F, -3.0F, 6, 16, 6, 0.0F, true));
		
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(8.0F, 1.0F, 0.0F);
				leg4.addChild(bone23);
				setRotationAngle(bone23, 0.0F, 0.0F, -0.5236F);
				bone23.cubeList.add(new ModelBox(bone23, 24, 28, -2.0F, -2.0F, -2.0F, 10, 4, 4, -0.1F, true));
		
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(8.0F, -2.0F, 0.0F);
				bone23.addChild(bone24);
				setRotationAngle(bone24, 0.0F, 0.0F, -1.0472F);
				bone24.cubeList.add(new ModelBox(bone24, 24, 36, -8.0F, 0.0F, -2.0F, 8, 4, 4, 0.0F, true));
		
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(19.0F, 6.0F, -3.0F);
				leg4.addChild(bone25);
				setRotationAngle(bone25, -0.3491F, 0.0F, -0.3491F);
				bone25.cubeList.add(new ModelBox(bone25, 18, 28, -2.0F, -1.0F, 0.0F, 2, 2, 2, 0.0F, true));
		
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(13.0F, 6.0F, 3.0F);
				leg4.addChild(bone26);
				setRotationAngle(bone26, 0.3491F, 0.0F, 0.3491F);
				bone26.cubeList.add(new ModelBox(bone26, 18, 28, 0.0F, -1.0F, -2.0F, 2, 2, 2, 0.0F, false));
		
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(19.0F, 6.0F, 3.0F);
				leg4.addChild(bone29);
				setRotationAngle(bone29, 0.3491F, 0.0F, -0.3491F);
				bone29.cubeList.add(new ModelBox(bone29, 18, 28, -2.0F, -1.0F, -2.0F, 2, 2, 2, 0.0F, true));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				body.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float limbSwing, float limbSwingAmount, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(limbSwing, limbSwingAmount, f2, f3, f4, f5, e);
		        this.leg1.rotateAngleY = -0.5236F + MathHelper.cos(limbSwing * 0.5F) * 0.4F * limbSwingAmount;
		        this.leg2.rotateAngleY = 0.5236F - MathHelper.cos(limbSwing * 0.5F + (float)Math.PI) * 0.4F * limbSwingAmount;
		        this.leg3.rotateAngleY = 0.5236F + MathHelper.cos(limbSwing * 0.5F + (float)Math.PI) * 0.4F * limbSwingAmount;
		        this.leg4.rotateAngleY = -0.5236F - MathHelper.cos(limbSwing * 0.5F) * 0.4F * limbSwingAmount;
				this.grillRight.rotateAngleY = 0.4625F;
				this.grillLeft.rotateAngleY = -0.4625F;
				if (this.swingProgress > 0.0F) {
					this.grillRight.rotateAngleY += MathHelper.sin(this.swingProgress * (float) Math.PI) * 0.6545F;
					this.grillLeft.rotateAngleY -= MathHelper.sin(this.swingProgress * (float) Math.PI) * 0.6545F;
				}
			}
		}
	}
}
