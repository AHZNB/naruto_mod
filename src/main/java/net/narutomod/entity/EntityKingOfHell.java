package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.potion.PotionEffect;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityKingOfHell extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 38;
	
	public EntityKingOfHell(ElementsNarutomodMod instance) {
		super(instance, 221);
	}

	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "kingofhell"), ENTITYID).name("kingofhell").tracker(64, 1, true).build());
	}

	public static class EntityCustom extends EntityShieldBase {
		private static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Float> RYO = EntityDataManager.<Float>createKey(EntityCustom.class, DataSerializers.FLOAT);
		private EntityLivingBase healingPlayer;
		private int deathTicks;
		private double chakraUsage = Double.MAX_VALUE;

		public EntityCustom(World world) {
			super(world);
			this.setSize(3.0F, 4.5F);
			this.swingProgress = 0.0F;
			this.swingProgressInt = 0;
			this.dieOnNoPassengers = false;
		}

		public EntityCustom(EntityLivingBase player, double chakraburn) {
			this(player.world);
			this.setSummoner(player);
			Vec3d vec = player.getLookVec().scale(4d).add(player.getPositionEyes(1f));
			for (; !this.world.getBlockState(new BlockPos(vec)).isTopSolid(); vec = vec.subtract(0d, 1d, 0d));
			for (; this.world.getBlockState(new BlockPos(vec).up()).isTopSolid(); vec = vec.addVector(0d, 1d, 0d));
			this.rotationYaw = this.rotationYawHead = this.renderYawOffset = player.rotationYawHead - 180.0F;
			this.setLocationAndAngles(vec.x, new BlockPos(vec).up().getY(), vec.z, this.rotationYaw, 0.0f);
			this.chakraUsage = chakraburn;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(AGE, Integer.valueOf(0));
			this.getDataManager().register(RYO, Float.valueOf(0f));
		}

		public int getAge() {
			return ((Integer) this.getDataManager().get(AGE)).intValue();
		}

		protected void setAge(int age) {
			this.getDataManager().set(AGE, Integer.valueOf(age));
		}

		@Override
		public void setRenderYawOffset(float offset) {
			super.setRenderYawOffset(offset);
			this.getDataManager().set(RYO, Float.valueOf(offset));
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (RYO.equals(key) && this.world.isRemote) {
				this.renderYawOffset = ((Float)this.getDataManager().get(RYO)).floatValue();
			}
		}		

		@Override
		public void onKillCommand() {
		}

		@Override
		public AxisAlignedBB getCollisionBoundingBox() {
			return this.getEntityBoundingBox();
		}

		@Override
		public boolean canBePushed() {
			return false;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return false;
		}

		@Override
		protected void turnBodyAndHead(Entity passenger) {
		}

		public void setHealingEntity(EntityLivingBase entity) {
			this.healingPlayer = entity;
			this.toggleArmSwing();
			this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:KoH_spawn")), 1.0F, 1.0F);
		}

		@Override
		public boolean processInitialInteract(EntityPlayer entity, EnumHand hand) {
			EntityLivingBase summoner = this.getSummoner();
			if (this.healingPlayer == null && summoner != null 
			 && (entity.equals(summoner) || summoner.isOnSameTeam(entity))) {
				this.setHealingEntity(entity);
				return true;
			}
			return super.processInitialInteract(entity, hand);
		}

		private int getArmSwingAnimationEnd() {
			return 30;
		}

		@Override
		public void swingArm(EnumHand hand) {
			if (!this.isSwingInProgress) {
				this.isSwingInProgress = true;
				if (this.world instanceof WorldServer) {
					((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketAnimation(this, 0));
				}
			}
		}

		private void toggleArmSwing() {
			this.swingArm(EnumHand.MAIN_HAND);
		}

		@Override
		protected void updateArmSwingProgress() {
			int i = getArmSwingAnimationEnd();
			if (this.isSwingInProgress) {
				this.swingProgressInt++;
				if (this.swingProgressInt == i / 2) {
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

		private void rejuvenatePlayer() {
			if (this.healingPlayer != null) {
				if (this.healingPlayer.startRiding(this)) {
					this.world.setEntityState(this.healingPlayer, (byte) 35);
					this.healingPlayer.clearActivePotions();
					this.healingPlayer.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 160, 4));
				}
				this.healingPlayer.heal(0.3F);
				this.healingPlayer.setSneaking(false);
				if (this.healingPlayer.getHealth() >= this.healingPlayer.getMaxHealth()) {
					this.healingPlayer.dismountRidingEntity();
					this.healingPlayer = null;
				}
			}
		}

		private void rejuvenateSummoningPlayer() {
			if (!this.isSwingInProgress && this.swingProgressInt == 0) {
				this.healingPlayer = this.getSummoner();
				this.toggleArmSwing();
			}
		}

		@Override
		public void onEntityUpdate() {
			int age = this.getAge() + 1;
			this.setAge(age);
			this.updateArmSwingProgress();
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner == null || (this.ticksExisted % 20 == 0 && !Chakra.pathway(summoner).consume(this.chakraUsage))) {
					this.setHealth(0.0F);
				} else {
					if (this.isArmsOpen()) {
						if (this.healingPlayer != null) {
							this.rejuvenatePlayer();
						} else {
							this.toggleArmSwing();
						}
					}
					if (summoner != null) {
						if (summoner.getHealth() <= 0.0F) {
							this.setHealth(0.0F);
						} else if (summoner.getHealth() < 4.0F) {
							this.rejuvenateSummoningPlayer();
						}
					}
				}
				if (age < 5) {
					this.setRenderYawOffset(this.renderYawOffset);
				}
				if (age == 1) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:KoH_spawn")), 1f, 1f);
				}
			}
			super.onEntityUpdate();
			Particles.spawnParticle(this.world, Particles.Types.FLAME, this.posX, this.posY, this.posZ, 100,
			 this.width * 0.25, 0.2, this.width * 0.25, 0, 0, 0, 0x80404080, 30);
		}

		@Override
		protected void onDeathUpdate() {
			this.deathTicks++;
			if (this.deathTicks == 1)
				this.healingPlayer = null;
			if (this.deathTicks > 60 && !this.world.isRemote)
				this.setDead();
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1024.0D);
		}

		@Override
		public double getMountedYOffset() {
			return 0.2D;
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setInteger("age", this.getAge());
			compound.setDouble("chakraUsage", this.chakraUsage);
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setAge(compound.getInteger("age"));
			this.chakraUsage = compound.getDouble("chakraUsage");
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager ->
				new RenderLivingBase<EntityCustom>(renderManager, new ModelKingofhell(), 4.8F) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/kingofhell.png");
					@Override
					protected ResourceLocation getEntityTexture(EntityCustom entity) {
						return this.texture;
					}
					@Override
					protected boolean canRenderName(EntityCustom entity) {
						return false;
					}
				});
		}

		@SideOnly(Side.CLIENT)
		public class ModelKingofhell extends ModelBase {
			private final ModelRenderer head;
			private final ModelRenderer bone14;
			private final ModelRenderer mask_right;
			private final ModelRenderer mask_left;
			private final ModelRenderer crown;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone13;
			private final ModelRenderer collarOuter;
			private final ModelRenderer bone15;
			private final ModelRenderer bone2;
			private final ModelRenderer bone16;
			private final ModelRenderer bone9;
			private final ModelRenderer bone3;
			private final ModelRenderer bone17;
			private final ModelRenderer collarInner;
			private final ModelRenderer bone19;
			private final ModelRenderer bone;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			private final ModelRenderer bone4;
			private final ModelRenderer bone22;
		
			public ModelKingofhell() {
				textureWidth = 144;
				textureHeight = 144;
		
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 24.0F, 0.0F);
				head.cubeList.add(new ModelBox(head, 0, 0, -8.0F, -24.0F, -8.0F, 16, 32, 16, 0.0F, false));
				head.cubeList.add(new ModelBox(head, 48, 0, -2.5F, -28.5F, -2.5F, 5, 5, 5, 0.0F, false));
				head.cubeList.add(new ModelBox(head, 68, 32, -10.0F, -6.0F, -9.0F, 20, 14, 18, 0.0F, false));
		
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, -0.1F, -8.0F);
				head.addChild(bone14);
				setRotationAngle(bone14, 0.5236F, 0.0F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 0, 112, -6.0F, -12.0F, 0.0F, 12, 12, 16, 0.0F, false));
		
				mask_right = new ModelRenderer(this);
				mask_right.setRotationPoint(-8.0F, 4.0F, -8.0F);
				head.addChild(mask_right);
				setRotationAngle(mask_right, 0.0F, 0.0873F, 0.0F);
				mask_right.cubeList.add(new ModelBox(mask_right, 68, 0, 0.0F, -15.0F, 0.0F, 8, 19, 0, 0.0F, false));
		
				mask_left = new ModelRenderer(this);
				mask_left.setRotationPoint(8.0F, 4.0F, -8.0F);
				head.addChild(mask_left);
				setRotationAngle(mask_left, 0.0F, -0.0873F, 0.0F);
				mask_left.cubeList.add(new ModelBox(mask_left, 68, 0, -8.0F, -15.0F, 0.0F, 8, 19, 0, 0.0F, true));
		
				crown = new ModelRenderer(this);
				crown.setRotationPoint(0.0F, -19.0F, 0.0F);
				head.addChild(crown);
				crown.cubeList.add(new ModelBox(crown, 72, 2, -9.0F, -9.0F, -9.0F, 18, 12, 18, 0.0F, false));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-5.0F, 2.0F, -8.5F);
				crown.addChild(bone5);
				setRotationAngle(bone5, 0.0F, -0.4363F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 3, -0.5F, -8.0F, -0.5F, 1, 8, 0, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, -7.0F, 0.0F);
				bone5.addChild(bone6);
				setRotationAngle(bone6, -0.7854F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -0.5845F, -8.2961F, -1.0524F, 1, 8, 0, 0.0F, false));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, -7.0F, 0.0F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, -0.7854F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 0, -0.5F, -8.0F, -1.6F, 1, 8, 0, 0.0F, false));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, -8.0F, -0.2F);
				bone7.addChild(bone8);
				setRotationAngle(bone8, -0.5236F, 0.0F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 0, -0.5F, -7.2929F, -1.2071F, 1, 8, 0, 0.0F, false));
				bone8.cubeList.add(new ModelBox(bone8, 0, 0, -0.5F, -14.364F, -1.2071F, 1, 8, 0, 0.0F, false));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(4.75F, 2.0F, -8.5F);
				crown.addChild(bone10);
				setRotationAngle(bone10, 0.0F, 0.4363F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 0, 3, -0.5F, -8.0F, -0.5F, 1, 8, 0, 0.0F, true));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, -7.0F, 0.0F);
				bone10.addChild(bone11);
				setRotationAngle(bone11, -0.7854F, 0.0F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 0, 0, -0.4155F, -8.2961F, -1.0524F, 1, 8, 0, 0.0F, true));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, -7.0F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, -0.7854F, 0.0F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 0, 0, -0.5F, -8.0F, -1.6F, 1, 8, 0, 0.0F, true));
		
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, -8.0F, -0.2F);
				bone12.addChild(bone13);
				setRotationAngle(bone13, -0.5236F, 0.0F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 0, 0, -0.5F, -7.2929F, -1.2071F, 1, 8, 0, 0.0F, true));
				bone13.cubeList.add(new ModelBox(bone13, 0, 0, -0.5F, -14.364F, -1.2071F, 1, 8, 0, 0.0F, true));
		
				collarOuter = new ModelRenderer(this);
				collarOuter.setRotationPoint(0.0F, -4.0F, 0.0F);
				head.addChild(collarOuter);
				setRotationAngle(collarOuter, -0.2618F, 0.0F, 0.0F);
				
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(-4.0F, -3.7848F, 10.4826F);
				collarOuter.addChild(bone15);
				setRotationAngle(bone15, 0.0F, -0.6981F, -0.3491F);
				bone15.cubeList.add(new ModelBox(bone15, 24, 48, -8.0F, -21.0F, 0.0F, 8, 30, 0, 0.0F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -6.0F, 0.0F);
				bone15.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.5236F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 48, 0.0F, -15.0F, 0.0F, 12, 30, 0, 0.0F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(-8.0F, 0.0F, 0.0F);
				bone15.addChild(bone16);
				setRotationAngle(bone16, 0.0F, -0.5236F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 40, 48, -12.0F, -21.0F, 0.0F, 12, 32, 0, 0.0F, false));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(4.0F, -3.7848F, 10.4826F);
				collarOuter.addChild(bone9);
				setRotationAngle(bone9, 0.0F, 0.6981F, 0.3491F);
				bone9.cubeList.add(new ModelBox(bone9, 24, 48, 0.0F, -21.0F, 0.0F, 8, 30, 0, 0.0F, true));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, -6.0F, 0.0F);
				bone9.addChild(bone3);
				setRotationAngle(bone3, 0.0F, -0.5236F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 48, -12.0F, -15.0F, 0.0F, 12, 30, 0, 0.0F, true));
		
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(8.0F, 0.0F, 0.0F);
				bone9.addChild(bone17);
				setRotationAngle(bone17, 0.0F, 0.5236F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 40, 48, 0.0F, -21.0F, 0.0F, 12, 32, 0, 0.0F, true));
		
				collarInner = new ModelRenderer(this);
				collarInner.setRotationPoint(0.0F, -4.0F, 0.0F);
				head.addChild(collarInner);
				setRotationAngle(collarInner, -0.2618F, 0.0F, 0.0F);
				
		
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(-4.0F, -3.6368F, 10.1654F);
				collarInner.addChild(bone19);
				setRotationAngle(bone19, 0.0F, -0.6981F, -0.3491F);
				bone19.cubeList.add(new ModelBox(bone19, 24, 80, -8.0F, -21.0F, 0.0F, 8, 30, 0, 0.0F, false));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, -6.0F, 0.0F);
				bone19.addChild(bone);
				setRotationAngle(bone, 0.0F, 0.5236F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 0, 80, 0.0F, -15.0F, 0.0F, 12, 30, 0, 0.0F, false));
		
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(-8.0F, 0.0F, 0.0F);
				bone19.addChild(bone20);
				setRotationAngle(bone20, 0.0F, -0.5236F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 40, 80, -12.0F, -21.0F, 0.0F, 12, 32, 0, 0.0F, false));
		
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(4.0F, -3.6368F, 10.1654F);
				collarInner.addChild(bone21);
				setRotationAngle(bone21, 0.0F, 0.6981F, 0.3491F);
				bone21.cubeList.add(new ModelBox(bone21, 24, 80, 0.0F, -21.0F, 0.0F, 8, 30, 0, 0.0F, true));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -6.0F, 0.0F);
				bone21.addChild(bone4);
				setRotationAngle(bone4, 0.0F, -0.5236F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 80, -12.0F, -15.0F, 0.0F, 12, 30, 0, 0.0F, true));
		
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(8.0F, 0.0F, 0.0F);
				bone21.addChild(bone22);
				setRotationAngle(bone22, 0.0F, 0.5236F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 40, 80, 0.0F, -21.0F, 0.0F, 12, 32, 0, 0.0F, true));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
				int popoutend = 60;
				float scale = 3.0F;
				float translate = scale;
				GlStateManager.pushMatrix();
				if (((EntityCustom) entity).getAge() <= popoutend)
					translate = (float) ((EntityCustom) entity).getAge() / (float) popoutend * scale;
				else if (((EntityCustom) entity).deathTicks > 0)
					translate = (1.0F - (float) ((EntityCustom) entity).deathTicks / (float) popoutend) * scale;
				GlStateManager.translate(0.0F, 1.5F - 1.5F * translate, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				this.head.render(f5);
				GlStateManager.popMatrix();
			}
	
			@Override
			public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
				//this.head.rotateAngleY = netHeadYaw * 0.017453292F;
				this.mask_right.rotateAngleY = 0.0873F;
				this.mask_left.rotateAngleY = -0.0873F;
				if (this.swingProgress > 0.0F) {
					this.mask_right.rotateAngleY += MathHelper.sin(this.swingProgress * (float) Math.PI) * 2.0F;
					this.mask_left.rotateAngleY -= MathHelper.sin(this.swingProgress * (float) Math.PI) * 2.0F;
				}
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}		
	}
}
