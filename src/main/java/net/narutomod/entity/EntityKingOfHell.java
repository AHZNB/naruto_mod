package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.PotionEffect;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.Item;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.WorldServer;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.util.SoundEvent;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureKingOfHellEntityOnEntityTickUpdate;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import java.util.HashMap;
//import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class EntityKingOfHell extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 38;
	public static final int ENTITYID_RANGED = 39;
	
	public EntityKingOfHell(ElementsNarutomodMod instance) {
		super(instance, 221);
	}

	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "kingofhellentity"), ENTITYID).name("kingofhellentity").tracker(64, 1, true).build());
		//elements.addNetworkMessage(ToTrackingMessage.Handler.class, ToTrackingMessage.class, Side.CLIENT);
	}

	public static class EntityCustom extends EntityCreature {
		private static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		protected EntityPlayer summoningPlayer;
		private EntityPlayer healingPlayer;
		private int deathTicks;
		private double chakraUsage = Double.MAX_VALUE;

		public EntityCustom(World world) {
			super(world);
			this.setSize(5.0F, 4.8F);
			this.experienceValue = 0;
			this.isImmuneToFire = true;
			this.swingProgress = 0.0F;
			this.swingProgressInt = 0;
			this.setNoAI(true);
			this.enablePersistence();
		}

		public EntityCustom(EntityPlayer player) {
			this(player.world);
			this.summoningPlayer = player;
			RayTraceResult res = ProcedureUtils.raytraceBlocks(player, 4.0D);
			double x = res.getBlockPos().getX();
			double z = res.getBlockPos().getZ();
			this.setPosition(x + 0.5D, player.posY, z + 0.5D);
			this.rotationYaw = this.rotationYawHead = player.rotationYaw - 180.0F;
			this.chakraUsage = ItemRinnegan.getNarakaPathChakraUsage(player);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(AGE, Integer.valueOf(0));
		}

		public int getAge() {
			return ((Integer) this.getDataManager().get(AGE)).intValue();
		}

		protected void setAge(int age) {
			this.getDataManager().set(AGE, Integer.valueOf(age));
		}

		@Override
		public EnumCreatureAttribute getCreatureAttribute() {
			return EnumCreatureAttribute.UNDEFINED;
		}

		@Override
		protected boolean canDespawn() {
			return false;
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		@Override
		public SoundEvent getAmbientSound() {
			return (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public SoundEvent getDeathSound() {
			return (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		public void onKillCommand() {
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return false;
		}

		@Override
		public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			if (this.healingPlayer == null && this.summoningPlayer != null 
			 && (entity.equals(this.summoningPlayer) || this.summoningPlayer.isOnSameTeam(entity))) {
				this.healingPlayer = entity;
				this.toggleArmSwing();
				this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:KoH_spawn")), 1.0F, 1.0F);
				return true;
			}
			return super.processInteract(entity, hand);
		}

		private int getArmSwingAnimationEnd() {
			return 20;
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
			//if (!this.world.isRemote && this.prevSwingProgress != this.swingProgress)
			//	NarutomodMod.PACKET_HANDLER.sendToAllTracking(new ToTrackingMessage(this), this);
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
				this.healingPlayer.heal(0.2F);
				this.healingPlayer.setSneaking(false);
				if (this.healingPlayer.getHealth() >= this.healingPlayer.getMaxHealth()) {
					this.healingPlayer.dismountRidingEntity();
					this.healingPlayer = null;
				}
			}
		}

		private void rejuvenateSummoningPlayer() {
			if (!this.isSwingInProgress && this.swingProgressInt == 0) {
				this.healingPlayer = this.summoningPlayer;
				this.toggleArmSwing();
			}
		}

		@Override
		public void onEntityUpdate() {
			this.setAge(this.getAge() + 1);
			this.updateArmSwingProgress();
			if (!this.world.isRemote) {
				if (this.isArmsOpen()) {
					if (this.healingPlayer != null) {
						this.rejuvenatePlayer();
					} else {
						this.toggleArmSwing();
					}
				}
				if (this.summoningPlayer != null) {
					if (this.summoningPlayer.getHealth() <= 0.0F) {
						this.setHealth(0.0F);
					} else if (this.summoningPlayer.getHealth() < 4.0F) {
						this.rejuvenateSummoningPlayer();
					}
				}
			}
			super.onEntityUpdate();
			{
				HashMap<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", this);
				$_dependencies.put("world", this.world);
				ProcedureKingOfHellEntityOnEntityTickUpdate.executeProcedure($_dependencies);
			}
			if (!this.world.isRemote && (this.summoningPlayer == null || (this.ticksExisted % 20 == 0
			 && !Chakra.pathway(this.summoningPlayer).consume(this.chakraUsage)))) {
				this.setHealth(0.0F);
			}
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
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
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
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setAge(compound.getInteger("age"));
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class,
					renderManager -> new RenderLiving(renderManager, new ModelKingofhell(), 4.8F) {
						protected ResourceLocation getEntityTexture(Entity entity) {
							return new ResourceLocation("narutomod:textures/kingofhell.png");
						}
					});
		}

		@SideOnly(Side.CLIENT)
		public class ModelKingofhell extends ModelBase {
			private final ModelRenderer head;
			private final ModelRenderer bone3;
			private final ModelRenderer mask_right;
			private final ModelRenderer bone4;
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
			private final ModelRenderer bone16;
			private final ModelRenderer bone9;
			private final ModelRenderer bone17;
			private final ModelRenderer collarInner;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
		
			public ModelKingofhell() {
				textureWidth = 144;
				textureHeight = 144;
		
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 24.0F, 0.0F);
				head.cubeList.add(new ModelBox(head, 0, 0, -8.0F, -24.0F, -8.0F, 16, 24, 16, 0.0F, false));
				head.cubeList.add(new ModelBox(head, 39, 117, -2.5F, -28.5F, -2.5F, 5, 5, 5, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-8.0F, 4.0F, -8.0F);
				head.addChild(bone3);
				bone3.cubeList.add(new ModelBox(bone3, 81, 101, -0.1F, -15.0F, -0.1F, 0, 19, 16, 0.0F, false));
		
				mask_right = new ModelRenderer(this);
				mask_right.setRotationPoint(-0.1F, 0.0F, 0.0F);
				bone3.addChild(mask_right);
				setRotationAngle(mask_right, 0.0F, 0.0873F, 0.0F);
				mask_right.cubeList.add(new ModelBox(mask_right, 64, 16, 0.0F, -15.0F, 0.0F, 8, 19, 0, 0.0F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(8.0F, 4.0F, -8.0F);
				head.addChild(bone4);
				bone4.cubeList.add(new ModelBox(bone4, 64, 0, 0.1F, -15.0F, -0.1F, 0, 19, 16, 0.0F, true));
		
				mask_left = new ModelRenderer(this);
				mask_left.setRotationPoint(0.1F, 0.0F, 0.0F);
				bone4.addChild(mask_left);
				setRotationAngle(mask_left, 0.0F, -0.0873F, 0.0F);
				mask_left.cubeList.add(new ModelBox(mask_left, 64, 16, -8.0F, -15.0F, 0.0F, 8, 19, 0, 0.0F, true));
		
				crown = new ModelRenderer(this);
				crown.setRotationPoint(0.0F, 5.0F, 0.0F);
				crown.cubeList.add(new ModelBox(crown, 72, 38, -9.0F, -9.0F, -9.0F, 18, 12, 18, 0.0F, false));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-5.0F, 2.0F, -8.5F);
				crown.addChild(bone5);
				setRotationAngle(bone5, 0.0F, -0.4363F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 127, 28, -0.5F, -8.0F, -0.5F, 1, 8, 0, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, -7.0F, 0.0F);
				bone5.addChild(bone6);
				setRotationAngle(bone6, -0.7854F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 127, 25, -0.5845F, -8.2961F, -1.0524F, 1, 8, 0, 0.0F, false));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, -7.0F, 0.0F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, -0.7854F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 127, 25, -0.5F, -8.0F, -1.6F, 1, 8, 0, 0.0F, false));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, -8.0F, -0.2F);
				bone7.addChild(bone8);
				setRotationAngle(bone8, -0.5236F, 0.0F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 127, 25, -0.5F, -7.2929F, -1.2071F, 1, 8, 0, 0.0F, false));
				bone8.cubeList.add(new ModelBox(bone8, 127, 25, -0.5F, -14.364F, -1.2071F, 1, 8, 0, 0.0F, false));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(4.75F, 2.0F, -8.5F);
				crown.addChild(bone10);
				setRotationAngle(bone10, 0.0F, 0.4363F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 127, 28, -0.5F, -8.0F, -0.5F, 1, 8, 0, 0.0F, true));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, -7.0F, 0.0F);
				bone10.addChild(bone11);
				setRotationAngle(bone11, -0.7854F, 0.0F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 127, 25, -0.4155F, -8.2961F, -1.0524F, 1, 8, 0, 0.0F, true));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, -7.0F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, -0.7854F, 0.0F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 127, 25, -0.5F, -8.0F, -1.6F, 1, 8, 0, 0.0F, true));
		
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, -8.0F, -0.2F);
				bone12.addChild(bone13);
				setRotationAngle(bone13, -0.5236F, 0.0F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 127, 25, -0.5F, -7.2929F, -1.2071F, 1, 8, 0, 0.0F, true));
				bone13.cubeList.add(new ModelBox(bone13, 127, 25, -0.5F, -14.364F, -1.2071F, 1, 8, 0, 0.0F, true));
		
				collarOuter = new ModelRenderer(this);
				collarOuter.setRotationPoint(0.0F, 25.0F, 11.1F);
				setRotationAngle(collarOuter, -0.4363F, 0.0F, 0.0F);
				collarOuter.cubeList.add(new ModelBox(collarOuter, 0, 40, -4.0F, -21.0F, 0.0F, 8, 28, 0, 0.0F, false));
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(-4.0F, 0.0F, 0.0F);
				collarOuter.addChild(bone15);
				setRotationAngle(bone15, 0.0F, -0.6981F, 0.0F);
				bone15.cubeList.add(new ModelBox(bone15, 16, 40, -8.0F, -21.0F, 0.0F, 8, 30, 0, 0.0F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(-8.0F, 0.0F, 0.0F);
				bone15.addChild(bone16);
				setRotationAngle(bone16, 0.0F, -0.5236F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 32, 40, -12.0F, -21.0F, 0.0F, 12, 32, 0, 0.0F, false));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(4.0F, 0.0F, 0.0F);
				collarOuter.addChild(bone9);
				setRotationAngle(bone9, 0.0F, 0.6981F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 16, 40, 0.0F, -21.0F, 0.0F, 8, 30, 0, 0.0F, true));
		
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(8.0F, 0.0F, 0.0F);
				bone9.addChild(bone17);
				setRotationAngle(bone17, 0.0F, 0.5236F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 32, 40, 0.0F, -21.0F, 0.0F, 12, 32, 0, 0.0F, true));
		
				collarInner = new ModelRenderer(this);
				collarInner.setRotationPoint(0.0F, 25.0F, 11.0F);
				setRotationAngle(collarInner, -0.4363F, 0.0F, 0.0F);
				collarInner.cubeList.add(new ModelBox(collarInner, 0, 72, -4.0F, -21.0F, 0.0F, 8, 28, 0, 0.0F, false));
		
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(-4.0F, 0.0F, 0.0F);
				collarInner.addChild(bone19);
				setRotationAngle(bone19, 0.0F, -0.6981F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 16, 72, -8.0F, -21.0F, 0.0F, 8, 30, 0, 0.0F, false));
		
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(-8.0F, 0.0F, 0.0F);
				bone19.addChild(bone20);
				setRotationAngle(bone20, 0.0F, -0.5236F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 32, 72, -12.0F, -21.0F, 0.0F, 12, 32, 0, 0.0F, false));
		
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(4.0F, 0.0F, 0.0F);
				collarInner.addChild(bone21);
				setRotationAngle(bone21, 0.0F, 0.6981F, 0.0F);
				bone21.cubeList.add(new ModelBox(bone21, 16, 72, 0.0F, -21.0F, 0.0F, 8, 30, 0, 0.0F, true));
		
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(8.0F, 0.0F, 0.0F);
				bone21.addChild(bone22);
				setRotationAngle(bone22, 0.0F, 0.5236F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 32, 72, 0.0F, -21.0F, 0.0F, 12, 32, 0, 0.0F, true));
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
				this.crown.render(f5);
				this.collarOuter.render(f5);
				this.collarInner.render(f5);
				GlStateManager.popMatrix();
			}
	
			@Override
			public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor,
					Entity entityIn) {
				this.head.rotateAngleY = netHeadYaw * 0.017453292F;
				this.crown.rotateAngleY = netHeadYaw * 0.017453292F;
				this.collarOuter.rotateAngleY = netHeadYaw * 0.017453292F;
				this.collarInner.rotateAngleY = netHeadYaw * 0.017453292F;
				if (this.swingProgress > 0.0F) {
					this.mask_right.rotateAngleY = MathHelper.sin(this.swingProgress * (float) Math.PI) * 2.0F;
					this.mask_left.rotateAngleY = MathHelper.sin(this.swingProgress * (float) Math.PI) * -2.0F;
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
