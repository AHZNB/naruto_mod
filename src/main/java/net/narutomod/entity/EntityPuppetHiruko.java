
package net.narutomod.entity;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.item.ItemScrollHiruko;
import net.narutomod.item.ItemSenbon;
import net.narutomod.item.ItemPoisonSenbon;
import net.narutomod.item.ItemSenbonArm;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import java.util.Random;
import javax.vecmath.Vector3f;
import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppetHiruko extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 389;
	public static final int ENTITYID_RANGED = 390;

	public EntityPuppetHiruko(ElementsNarutomodMod instance) {
		super(instance, 768);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "puppet_hiruko"), ENTITYID).name("puppet_hiruko").tracker(64, 3, true).build());
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
		this.elements.addNetworkMessage(PlayerHook.Message.Handler.class, PlayerHook.Message.class, Side.SERVER);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerHook());
	}

	public static class EntityCustom extends EntityShieldBase {
		// POSE: 0-idle; 1-attack; 2-defend
		private static final DataParameter<Integer> POSE = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Boolean> ROBE_OFF = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		public static final float MAXHEALTH = 100.0f;
		private int poseProgressEnd = 14;
		private int poseProgress = -1;
		private Object model;
		private boolean shouldBlock;
		private boolean maskOff;
		private boolean raiseLeftArm;

		public EntityCustom(World world) {
			super(world);
			this.setSize(1.25f, 1.5f);
			this.stepHeight = 1.0f;
			this.dieOnNoPassengers = false;
		}

		public EntityCustom(EntityLivingBase summonerIn, double x, double y, double z) {
			super(summonerIn, x, y, z);
			this.setSize(1.25f, 1.5f);
			this.stepHeight = 1.0f;
			this.dieOnNoPassengers = false;
			this.setHealth(this.getMaxHealth());
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(POSE, Integer.valueOf(0));
			this.dataManager.register(ROBE_OFF, Boolean.valueOf(false));
		}

		private void setPose(int pose) {
			int prevPose = this.getPose();
			this.dataManager.set(POSE, Integer.valueOf(pose));
			this.poseProgress = 0;
			this.poseProgressEnd = pose == 1 ? 7 : pose == 2 ? 3 : 14;
			if (pose != 0 && pose != prevPose) {
				this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hiruko_tail")),
				 1f, this.rand.nextFloat() * 0.4f + 0.7f);
			}
		}
	
		public int getPose() {
			return ((Integer)this.getDataManager().get(POSE)).intValue();
		}

		private void takeRobeOff(boolean b) {
			this.dataManager.set(ROBE_OFF, Boolean.valueOf(b));
		}
	
		public boolean isRobeOff() {
			return ((Boolean)this.getDataManager().get(ROBE_OFF)).booleanValue();
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (POSE.equals(key) && this.world.isRemote) {
				this.poseProgress = 0;
				int pose = this.getPose();
				this.poseProgressEnd = pose == 1 ? 7 : pose == 2 ? 3 : 14;
			}
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getAttributeMap().registerAttribute(EntityPlayer.REACH_DISTANCE);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAXHEALTH);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(12.0D);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(6.0D);
		}

		@Override
		public float getEyeHeight() {
			return this.isRobeOff() ? 0.4375f : 0.9375f;
		}

		@Override
		public boolean processInitialInteract(EntityPlayer entity, EnumHand hand) {
			if (!this.world.isRemote && entity.getHeldItem(hand).getItem() != ItemScrollHiruko.block) {
				return entity.startRiding(this);
			}
			return false;
		}

		@Override
		protected boolean canFitPassenger(Entity passenger) {
			if (passenger instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)passenger, ItemNinjutsu.block);
				if (stack != null && ((ItemNinjutsu.RangedItem)stack.getItem())
				 .canActivateJutsu(stack, ItemNinjutsu.PUPPET, (EntityPlayer)passenger) == EnumActionResult.SUCCESS) {
					return super.canFitPassenger(passenger);
				}
			}
			return false;
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			super.attackEntityAsMob(entityIn);
			return ProcedureUtils.attackEntityAsMob(this, entityIn);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.isProjectile()) {
				amount *= 0.4f;
			}
			if (this.shouldBlock) {
				amount *= 0.2f;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.setOwnerCanSteer(this.isBeingRidden(), this.isRobeOff() ? 1.5f : 0.5f);
			Entity controllingRider = this.getControllingPassenger();
			if (controllingRider != null) {
				controllingRider.setInvisible(true);
			}
		}

		@Override
		public void onLivingUpdate() {
			if (!this.world.isRemote) {
				if (this.shouldBlock) {
					this.setPose(2);
				} else {
					EntityLivingBase entity = this.getSummoner();
					if (entity != null && entity.swingProgressInt == -1) {
						this.setPose(1);
					}
				}
				if (!this.isRobeOff()) {
					this.takeRobeOff(this.isRiderHoldingSenbonArm());
				}
			}
			this.maskOff = this.isRiderHoldingSenbon();
			this.updateTailSwingProgress();
			super.onLivingUpdate();
		}

		private void updateTailSwingProgress() {
			if (this.poseProgress >= 0) {
				++this.poseProgress;
				if (this.poseProgress > this.poseProgressEnd) {
					this.poseProgress = -1;
					if (!this.world.isRemote && this.getPose() != 0) {
						this.setPose(0);
					}
				}
			}
		}

		private boolean isRiderHoldingSenbon() {
			Entity rider = this.getControllingPassenger();
			return rider instanceof EntityLivingBase
			 && (((EntityLivingBase)rider).getHeldItemMainhand().getItem() == ItemSenbon.block
			  || ((EntityLivingBase)rider).getHeldItemMainhand().getItem() == ItemPoisonSenbon.block);
		}

		private boolean isRiderHoldingSenbonArm() {
			Entity rider = this.getControllingPassenger();
			return rider instanceof EntityLivingBase
			 && (((EntityLivingBase)rider).getHeldItemMainhand().getItem() == ItemSenbonArm.block
			  || ((EntityLivingBase)rider).getHeldItemOffhand().getItem() == ItemSenbonArm.block);
		}

		private boolean hasSenbonArmInRiderInventory() {
			Entity rider = this.getControllingPassenger();
			return rider instanceof EntityPlayer
			 && ProcedureUtils.hasItemInInventory((EntityPlayer)rider, ItemSenbonArm.block);
		}

		public void raiseLeftArm(boolean b) {
			this.raiseLeftArm = b;
		}
	}

	public class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends RenderLivingBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/hiruko.png");
			private ModelPuppetHiruko model;
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelPuppetHiruko(), 0.5F);
			}
	
		 	@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.model == null) {
					entity.model = this.mainModel = this.model = new ModelPuppetHiruko();
				} else {
					this.mainModel = this.model = (ModelPuppetHiruko)entity.model;
				}
				if (entity.isBeingRidden() && entity.getControllingPassenger() instanceof EntityLivingBase) {
					this.copyLimbSwing(entity, (EntityLivingBase)entity.getControllingPassenger());
				}
				this.setModelVisibilities(entity);
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				if (entity.isRobeOff()) {
					GlStateManager.scale(1.25F, 1.25F, 1.25F);
				}
			}
	
			protected void copyLimbSwing(EntityCustom entity, EntityLivingBase rider) {
				entity.swingProgress = rider.swingProgress;
				entity.swingProgressInt = rider.swingProgressInt;
				entity.prevSwingProgress = rider.prevSwingProgress;
				entity.isSwingInProgress = rider.isSwingInProgress;
				entity.swingingHand = rider.swingingHand;
			}
	
			protected void setModelVisibilities(EntityCustom entity) {
				this.model.setVisible(true);
				this.model.body.showModel = true;
				if (entity.isRobeOff()) {
					this.model.mask.showModel = false;
					this.model.hair.showModel = true;
					this.model.hat.showModel = false;
					this.model.robe.showModel = false;
				} else {
					this.model.mask.showModel = !entity.maskOff;
					this.model.hair.showModel = false;
					this.model.hat.showModel = true;
					this.model.robe.showModel = true;
				}
				this.model.torpedo.showModel = entity.hasSenbonArmInRiderInventory();
				if (this.renderManager.renderViewEntity.equals(entity.getControllingPassenger())
				 && this.renderManager.options.thirdPersonView == 0) {
					this.model.body.showModel = false;
					this.model.bipedRightLeg.showModel = false;
					this.model.bipedLeftLeg.showModel = false;
				}
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}
	
		// Made with Blockbench 4.4.2
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelPuppetHiruko extends ModelBiped {
			private final ModelRenderer body;
			//private final ModelRenderer bipedHead;
			private final ModelRenderer jaw;
			private final ModelRenderer jawMid;
			private final ModelRenderer mask;
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer hair;
			private final ModelRenderer bone16;
			private final ModelRenderer bone17;
			private final ModelRenderer bone18;
			private final ModelRenderer bone19;
			private final ModelRenderer bone33;
			private final ModelRenderer bone34;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			private final ModelRenderer bone27;
			private final ModelRenderer bone28;
			private final ModelRenderer bone20;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
			private final ModelRenderer bone24;
			private final ModelRenderer bone8;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone13;
			private final ModelRenderer hat;
			private final ModelRenderer Bell_r1;
			private final ModelRenderer Bell_r2;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer cube_r6;
			private final ModelRenderer cube_r7;
			private final ModelRenderer cube_r8;
			private final ModelRenderer cube_r9;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			private final ModelRenderer cube_r13;
			private final ModelRenderer cube_r14;
			private final ModelRenderer cube_r15;
			private final ModelRenderer cube_r16;
			private final ModelRenderer veil;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer robe;
			private final ModelRenderer bone21;
			private final ModelRenderer rightArm;
			private final ModelRenderer rightUpperArm;
			private final ModelRenderer rightForeArm;
			private final ModelRenderer leftArm;
			private final ModelRenderer leftUpperArm;
			private final ModelRenderer leftForeArm;
			private final ModelRenderer backShield;
			private final ModelRenderer bone4;
			private final ModelRenderer bone3;
			private final ModelRenderer bone6;
			private final ModelRenderer bone9;
			private final ModelRenderer bone7;
			private final ModelRenderer bone5;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer bipedRightUpperArm;
			private final ModelRenderer bipedRightForeArm;
			//private final ModelRenderer bipedRightUpperArm2;
			//private final ModelRenderer bipedRightForeArm2;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer bipedLeftUpperArm;
			private final ModelRenderer bipedLeftForeArm;
			private final ModelRenderer torpedo;
			//private final ModelRenderer bipedLeftUpperArm2;
			//private final ModelRenderer bipedLeftForeArm2;
			//private final ModelRenderer bipedRightLeg;
			private final ModelRenderer rightThigh;
			private final ModelRenderer calfRight;
			//private final ModelRenderer bipedLeftLeg;
			private final ModelRenderer leftThigh;
			private final ModelRenderer calfLeft;
			private final ModelRenderer[] tail = new ModelRenderer[24];
			private final ModelRenderer tailEnd;
			private final ModelRenderer bone;
			private final ModelRenderer bone14;
			private final ModelRenderer bone2;
			private final ModelRenderer bone15;
			private final Vector3f[] tailSway = new Vector3f[10];
			private final Vector3f[][] tailPoseRobeOn = {
				{
					new Vector3f(),
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F),
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), 
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F)
				},
				{
					new Vector3f(),
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F),
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), 
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F),
					new Vector3f(0.1745F, 0.0F, 0.0F), new Vector3f(0.0873F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F)
				},
				{
					new Vector3f(),
					new Vector3f(0.2618F, -0.5236F, -0.0873F), new Vector3f(0.2618F, -0.5236F, -0.0873F), new Vector3f(0.2618F, -0.5236F, -0.0873F), 
					new Vector3f(0.2618F, -0.5236F, -0.1745F), new Vector3f(0.2618F, 0.0F, -0.1745F), new Vector3f(0.2618F, 0.0F, -0.1745F), 
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), 
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), 
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), 
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), 
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), 
					new Vector3f(0.1745F, 0.0F, 0.0F), new Vector3f(0.0873F, 0.0F, 0.0F)
				}
			};
			private final Vector3f[][] tailPoseRobeOff = {
				{
					new Vector3f(),
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F),
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.1745F, 0.0F, 0.0F), new Vector3f(0.0873F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F)
				},
				{
					new Vector3f(),
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F),
					new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.1745F, 0.0F, 0.0F), new Vector3f(0.0873F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F),
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F), 
					new Vector3f(0.0436F, 0.0F, 0.0F), new Vector3f(0.0436F, 0.0F, 0.0F)
				},
				{
					new Vector3f(),
					new Vector3f(0.2618F, -0.5236F, 0.0F), new Vector3f(0.2618F, -0.5236F, 0.0F), new Vector3f(0.2618F, -0.5236F, 0.0F), 
					new Vector3f(0.2618F, -0.2618F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F), new Vector3f(0.3491F, 0.0F, 0.0F), 
					new Vector3f(0.3491F, 0.0F, 0.0F), new Vector3f(0.3491F, 0.0F, 0.0F), new Vector3f(0.3491F, 0.0F, 0.0F), 
					new Vector3f(0.3491F, 0.0F, 0.0F), new Vector3f(0.3491F, -0.0873F, 0.0F), new Vector3f(0.2618F, -0.0873F, 0.0F), 
					new Vector3f(0.2618F, -0.0873F, 0.0F), new Vector3f(0.2618F, -0.0873F, 0.0F), new Vector3f(0.2618F, -0.0873F, 0.0F), 
					new Vector3f(0.2618F, -0.0873F, 0.0F), new Vector3f(0.1745F, -0.0873F, 0.0F), new Vector3f(0.1745F, -0.0873F, 0.0F), 
					new Vector3f(0.2618F, -0.0873F, 0.0F), new Vector3f(0.2618F, -0.0873F, 0.0F), new Vector3f(0.2618F, -0.0873F, 0.0F), 
					new Vector3f(0.2618F, -0.0873F, 0.0F), new Vector3f(0.2618F, 0.0F, 0.0F)
				}
			};
			private final Random rand = new Random();
	
			public ModelPuppetHiruko() {
				textureWidth = 128;
				textureHeight = 128;
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 15.0F, 0.0F);
				setRotationAngle(body, 1.0472F, 0.0F, 0.0F);
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -12.0F, 0.0F);
				body.addChild(bipedHead);
				setRotationAngle(bipedHead, -1.0472F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 44, 18, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 64, -0.5F, -1.0F, -3.9F, 1, 1, 4, -0.01F, false));
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, -2.0F, 0.0F);
				setRotationAngle(jaw, 0.2618F, 0.0F, 0.0F);
				bipedHead.addChild(jaw);
				jaw.cubeList.add(new ModelBox(jaw, 0, 74, 1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));
				jaw.cubeList.add(new ModelBox(jaw, 0, 74, -3.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, true));
				jawMid = new ModelRenderer(this);
				jawMid.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(jawMid, 0.1309F, 0.0F, 0.0F);
				jaw.addChild(jawMid);
				jawMid.cubeList.add(new ModelBox(jawMid, 12, 74, -1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));
				mask = new ModelRenderer(this);
				mask.setRotationPoint(0.0F, 24.0F, 0.0F);
				bipedHead.addChild(mask);
				mask.cubeList.add(new ModelBox(mask, 68, 10, -4.0F, -27.0F, -4.0F, 8, 4, 8, 0.25F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, -12.0F, 0.0F);
				body.addChild(bipedHeadwear);
				setRotationAngle(bipedHeadwear, -1.0472F, 0.0F, 0.0F);
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.addChild(hair);
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, -6.0F, 3.75F);
				hair.addChild(bone16);
				setRotationAngle(bone16, 1.0472F, 0.0F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone16.addChild(bone17);
				setRotationAngle(bone17, 0.5236F, 0.0F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone17.addChild(bone18);
				setRotationAngle(bone18, 0.5236F, 0.0F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone18.addChild(bone19);
				setRotationAngle(bone19, 0.2618F, 0.0F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(-1.5F, -5.0F, 3.75F);
				hair.addChild(bone33);
				setRotationAngle(bone33, 1.0472F, 0.0F, -0.7854F);
				bone33.cubeList.add(new ModelBox(bone33, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone33.addChild(bone34);
				setRotationAngle(bone34, 0.5236F, 0.0F, 0.0F);
				bone34.cubeList.add(new ModelBox(bone34, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone34.addChild(bone35);
				setRotationAngle(bone35, 0.5236F, 0.0F, 0.0F);
				bone35.cubeList.add(new ModelBox(bone35, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone35.addChild(bone36);
				setRotationAngle(bone36, 0.2618F, 0.0F, 0.0F);
				bone36.cubeList.add(new ModelBox(bone36, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(1.5F, -5.0F, 3.75F);
				hair.addChild(bone29);
				setRotationAngle(bone29, 1.0472F, 0.0F, 0.7854F);
				bone29.cubeList.add(new ModelBox(bone29, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone29.addChild(bone30);
				setRotationAngle(bone30, 0.5236F, 0.0F, 0.0F);
				bone30.cubeList.add(new ModelBox(bone30, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone30.addChild(bone31);
				setRotationAngle(bone31, 0.5236F, 0.0F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone31.addChild(bone32);
				setRotationAngle(bone32, 0.2618F, 0.0F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(-0.75F, -5.75F, 3.75F);
				hair.addChild(bone25);
				setRotationAngle(bone25, 1.0472F, 0.0F, -0.4363F);
				bone25.cubeList.add(new ModelBox(bone25, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone25.addChild(bone26);
				setRotationAngle(bone26, 0.5236F, 0.0F, 0.0F);
				bone26.cubeList.add(new ModelBox(bone26, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone26.addChild(bone27);
				setRotationAngle(bone27, 0.5236F, 0.0F, 0.0F);
				bone27.cubeList.add(new ModelBox(bone27, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone27.addChild(bone28);
				setRotationAngle(bone28, 0.2618F, 0.0F, 0.0F);
				bone28.cubeList.add(new ModelBox(bone28, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.75F, -5.75F, 3.75F);
				hair.addChild(bone20);
				setRotationAngle(bone20, 1.0472F, 0.0F, 0.4363F);
				bone20.cubeList.add(new ModelBox(bone20, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone20.addChild(bone22);
				setRotationAngle(bone22, 0.5236F, 0.0F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone22.addChild(bone23);
				setRotationAngle(bone23, 0.5236F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone23.addChild(bone24);
				setRotationAngle(bone24, 0.2618F, 0.0F, 0.0F);
				bone24.cubeList.add(new ModelBox(bone24, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, -8.5F, -4.25F);
				hair.addChild(bone8);
				bone8.cubeList.add(new ModelBox(bone8, 68, 0, -0.5F, 0.25F, 0.25F, 1, 2, 8, 0.05F, false));
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(2.5F, -8.5F, -4.25F);
				hair.addChild(bone10);
				setRotationAngle(bone10, 0.0F, 0.0F, 0.2618F);
				bone10.cubeList.add(new ModelBox(bone10, 68, 0, -0.5F, 0.4F, 0.25F, 1, 2, 8, 0.05F, false));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(-2.5F, -8.5F, -4.25F);
				hair.addChild(bone11);
				setRotationAngle(bone11, 0.0F, 0.0F, -0.2618F);
				bone11.cubeList.add(new ModelBox(bone11, 68, 0, -0.5F, 0.4F, 0.25F, 1, 2, 8, 0.05F, false));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(-4.5F, -6.5F, -4.25F);
				hair.addChild(bone12);
				setRotationAngle(bone12, 0.0F, 0.0F, -0.7854F);
				bone12.cubeList.add(new ModelBox(bone12, 68, 0, -0.25F, 0.0F, 0.25F, 1, 2, 8, 0.05F, false));
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(4.5F, -6.5F, -4.25F);
				hair.addChild(bone13);
				setRotationAngle(bone13, 0.0F, 0.0F, 0.7854F);
				bone13.cubeList.add(new ModelBox(bone13, 68, 0, -0.75F, 0.0F, 0.25F, 1, 2, 8, 0.05F, true));
				hat = new ModelRenderer(this);
				hat.setRotationPoint(0.0F, -5.5F, 0.0F);
				bipedHeadwear.addChild(hat);
				setRotationAngle(hat, -0.0436F, 0.0F, 0.0F);
				Bell_r1 = new ModelRenderer(this);
				Bell_r1.setRotationPoint(-3.825F, 0.0F, -9.0F);
				hat.addChild(Bell_r1);
				setRotationAngle(Bell_r1, 0.0F, 2.0071F, 0.0F);
				Bell_r1.cubeList.add(new ModelBox(Bell_r1, 116, 16, 0.0F, 0.35F, -1.0F, 0, 7, 2, 0.0F, false));
				Bell_r2 = new ModelRenderer(this);
				Bell_r2.setRotationPoint(-3.825F, 0.0F, -9.0F);
				hat.addChild(Bell_r2);
				setRotationAngle(Bell_r2, 0.2182F, 0.3927F, 0.0F);
				Bell_r2.cubeList.add(new ModelBox(Bell_r2, 116, 24, 0.0F, -0.225F, -1.175F, 0, 2, 2, 0.0F, false));
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.9599F, 0.3927F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.9599F, 0.7854F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r3);
				setRotationAngle(cube_r3, 0.9599F, 1.1781F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.9599F, 1.5708F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.9599F, 1.9635F, 0.0F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.9599F, 2.3562F, 0.0F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.9599F, 2.7489F, 0.0F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r8);
				setRotationAngle(cube_r8, 0.9599F, 3.1416F, 0.0F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.9599F, -2.7489F, 0.0F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r10);
				setRotationAngle(cube_r10, 0.9599F, -2.3562F, 0.0F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r11);
				setRotationAngle(cube_r11, 0.9599F, -1.9635F, 0.0F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.9599F, -1.5708F, 0.0F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r13);
				setRotationAngle(cube_r13, 0.9599F, -1.1781F, 0.0F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r14);
				setRotationAngle(cube_r14, 0.9599F, -0.7854F, 0.0F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r15);
				setRotationAngle(cube_r15, 0.9599F, -0.3927F, 0.0F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(0.0F, -6.5F, 0.0F);
				hat.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.9599F, 0.0F, 0.0F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 120, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
				veil = new ModelRenderer(this);
				veil.setRotationPoint(0.0F, 5.5F, 0.0F);
				hat.addChild(veil);
				setRotationAngle(veil, 0.0436F, 0.0F, 0.0F);
				veil.cubeList.add(new ModelBox(veil, 96, 0, -4.0F, -8.6F, -4.0F, 8, 8, 8, 2.0F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, -12.0F, 0.0F);
				body.addChild(bipedBody);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 48, 34, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				robe = new ModelRenderer(this);
				robe.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(robe);
				robe.cubeList.add(new ModelBox(robe, 0, 0, -7.0F, 0.0F, -6.0F, 14, 14, 12, 0.05F, false));
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, 0.0F, -6.0F);
				robe.addChild(bone21);
				setRotationAngle(bone21, 0.5236F, 0.0F, 0.0F);
				bone21.cubeList.add(new ModelBox(bone21, 0, 26, -7.0F, 0.0F, -10.0F, 14, 18, 10, 0.05F, false));
				bone21.cubeList.add(new ModelBox(bone21, 40, 0, -7.0F, 16.0F, -0.75F, 14, 2, 4, 0.05F, false));
				rightArm = new ModelRenderer(this);
				rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				robe.addChild(rightArm);	
				rightUpperArm = new ModelRenderer(this);
				rightUpperArm.setRotationPoint(0.0F, 0.0F, 0.0F);
				rightArm.addChild(rightUpperArm);
				setRotationAngle(rightUpperArm, -1.0472F, 0.0F, 0.0F);
				rightUpperArm.cubeList.add(new ModelBox(rightUpperArm, 0, 64, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.55F, false));
				rightForeArm = new ModelRenderer(this);
				rightForeArm.setRotationPoint(-1.0F, 4.0F, 2.0F);
				rightUpperArm.addChild(rightForeArm);
				setRotationAngle(rightForeArm, -0.2618F, 0.0F, 0.0F);
				rightForeArm.cubeList.add(new ModelBox(rightForeArm, 16, 64, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.55F, false));
				leftArm = new ModelRenderer(this);
				leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				robe.addChild(leftArm);		
				leftUpperArm = new ModelRenderer(this);
				leftUpperArm.setRotationPoint(0.0F, 0.0F, 0.0F);
				leftArm.addChild(leftUpperArm);
				setRotationAngle(leftUpperArm, -1.0472F, 0.0F, 0.0F);
				leftUpperArm.cubeList.add(new ModelBox(leftUpperArm, 0, 64, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.55F, true));
				leftForeArm = new ModelRenderer(this);
				leftForeArm.setRotationPoint(1.0F, 4.0F, 2.0F);
				leftUpperArm.addChild(leftForeArm);
				setRotationAngle(leftForeArm, -0.2618F, 0.0F, 0.0F);
				leftForeArm.cubeList.add(new ModelBox(leftForeArm, 16, 64, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.55F, true));
				backShield = new ModelRenderer(this);
				backShield.setRotationPoint(0.0F, 3.0F, 3.0F);
				bipedBody.addChild(backShield);
				setRotationAngle(backShield, 0.0873F, 0.0F, 0.0F);
				backShield.cubeList.add(new ModelBox(backShield, 0, 0, -2.0F, 0.0F, -1.0F, 4, 8, 2, 0.5F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-2.5F, 5.0F, 1.5F);
				backShield.addChild(bone4);
				setRotationAngle(bone4, 0.0F, -0.5236F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 26, -2.5F, -5.0F, -2.5F, 2, 8, 2, 0.5F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(2.5F, 5.0F, 1.5F);
				backShield.addChild(bone3);
				setRotationAngle(bone3, 0.0F, 0.5236F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 26, 0.5F, -5.0F, -2.5F, 2, 8, 2, 0.5F, true));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 8.5F, 1.5F);
				backShield.addChild(bone6);
				setRotationAngle(bone6, -0.5236F, 0.0F, 0.0F);
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone6.addChild(bone9);
				setRotationAngle(bone9, -0.0873F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 40, 6, -2.0F, 0.5F, -2.5F, 4, 4, 2, 0.5F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(-2.5F, 0.25F, 0.0F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, 0.0F, -0.4625F, -0.2618F);
				bone7.cubeList.add(new ModelBox(bone7, 46, 60, -2.45F, 0.25F, -2.52F, 2, 4, 2, 0.5F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(2.5F, 0.25F, 0.0F);
				bone6.addChild(bone5);
				setRotationAngle(bone5, 0.0F, 0.4625F, 0.2618F);
				bone5.cubeList.add(new ModelBox(bone5, 46, 60, 0.45F, 0.25F, -2.52F, 2, 4, 2, 0.5F, true));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, -10.0F, 0.0F);
				body.addChild(bipedRightArm);
				bipedRightUpperArm = new ModelRenderer(this);
				bipedRightUpperArm.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightArm.addChild(bipedRightUpperArm);
				setRotationAngle(bipedRightUpperArm, -1.0472F, 0.0F, 0.0F);
				bipedRightUpperArm.cubeList.add(new ModelBox(bipedRightUpperArm, 16, 54, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));
				bipedRightForeArm = new ModelRenderer(this);
				bipedRightForeArm.setRotationPoint(-1.0F, 4.0F, 2.0F);
				bipedRightUpperArm.addChild(bipedRightForeArm);
				setRotationAngle(bipedRightForeArm, -0.2618F, 0.0F, 0.0F);
				bipedRightForeArm.cubeList.add(new ModelBox(bipedRightForeArm, 44, 50, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.0F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, -10.0F, 0.0F);
				body.addChild(bipedLeftArm);
				bipedLeftUpperArm = new ModelRenderer(this);
				bipedLeftUpperArm.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftArm.addChild(bipedLeftUpperArm);
				setRotationAngle(bipedLeftUpperArm, -1.0472F, 0.0F, 0.0F);
				bipedLeftUpperArm.cubeList.add(new ModelBox(bipedLeftUpperArm, 16, 54, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, true));
				bipedLeftForeArm = new ModelRenderer(this);
				bipedLeftForeArm.setRotationPoint(1.0F, 4.0F, 2.0F);
				bipedLeftUpperArm.addChild(bipedLeftForeArm);
				setRotationAngle(bipedLeftForeArm, -0.2618F, 0.0F, 0.0F);
				bipedLeftForeArm.cubeList.add(new ModelBox(bipedLeftForeArm, 44, 50, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.0F, true));
				torpedo = new ModelRenderer(this);
				torpedo.setRotationPoint(-6.0F, 18.0F, -2.0F);
				bipedLeftForeArm.addChild(torpedo);
				torpedo.cubeList.add(new ModelBox(torpedo, 44, 66, 4.0F, -18.0F, -2.0F, 4, 6, 4, 0.5F, true));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 15.0F, 0.0F);
				rightThigh = new ModelRenderer(this);
				rightThigh.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightLeg.addChild(rightThigh);
				setRotationAngle(rightThigh, -0.7854F, 0.6545F, 0.0F);
				rightThigh.cubeList.add(new ModelBox(rightThigh, 0, 54, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));
				calfRight = new ModelRenderer(this);
				calfRight.setRotationPoint(0.0F, 6.0F, -2.0F);
				rightThigh.addChild(calfRight);
				setRotationAngle(calfRight, 0.7854F, 0.0F, 0.0F);
				calfRight.cubeList.add(new ModelBox(calfRight, 52, 6, -2.0F, 0.0F, 0.0F, 4, 6, 4, 0.0F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 15.0F, 0.0F);
				leftThigh = new ModelRenderer(this);
				leftThigh.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftLeg.addChild(leftThigh);
				setRotationAngle(leftThigh, -0.7854F, -0.6545F, 0.0F);
				leftThigh.cubeList.add(new ModelBox(leftThigh, 0, 54, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, true));
				calfLeft = new ModelRenderer(this);
				calfLeft.setRotationPoint(0.0F, 6.0F, -2.0F);
				leftThigh.addChild(calfLeft);
				setRotationAngle(calfLeft, 0.7854F, 0.0F, 0.0F);
				calfLeft.cubeList.add(new ModelBox(calfLeft, 52, 6, -2.0F, 0.0F, 0.0F, 4, 6, 4, 0.0F, true));
				tail[0] = new ModelRenderer(this);
				tail[0].setRotationPoint(0.0F, 15.0F, 0.0F);
				setRotationAngle(tail[0], 0.7854F, 0.0F, 0.0F);
				tail[0].cubeList.add(new ModelBox(tail[0], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[1] = new ModelRenderer(this);
				tail[1].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[0].addChild(tail[1]);
				tail[1].cubeList.add(new ModelBox(tail[1], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[2] = new ModelRenderer(this);
				tail[2].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[1].addChild(tail[2]);
				tail[2].cubeList.add(new ModelBox(tail[2], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[3] = new ModelRenderer(this);
				tail[3].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[2].addChild(tail[3]);
				tail[3].cubeList.add(new ModelBox(tail[3], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[4] = new ModelRenderer(this);
				tail[4].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[3].addChild(tail[4]);
				tail[4].cubeList.add(new ModelBox(tail[4], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[5] = new ModelRenderer(this);
				tail[5].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[4].addChild(tail[5]);
				tail[5].cubeList.add(new ModelBox(tail[5], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[6] = new ModelRenderer(this);
				tail[6].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[5].addChild(tail[6]);
				tail[6].cubeList.add(new ModelBox(tail[6], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[7] = new ModelRenderer(this);
				tail[7].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[6].addChild(tail[7]);
				tail[7].cubeList.add(new ModelBox(tail[7], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[8] = new ModelRenderer(this);
				tail[8].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[7].addChild(tail[8]);
				tail[8].cubeList.add(new ModelBox(tail[8], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[9] = new ModelRenderer(this);
				tail[9].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[8].addChild(tail[9]);
				tail[9].cubeList.add(new ModelBox(tail[9], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
	
				tail[10] = new ModelRenderer(this);
				tail[10].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[9].addChild(tail[10]);
				tail[10].cubeList.add(new ModelBox(tail[10], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[11] = new ModelRenderer(this);
				tail[11].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[10].addChild(tail[11]);
				tail[11].cubeList.add(new ModelBox(tail[11], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[12] = new ModelRenderer(this);
				tail[12].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[11].addChild(tail[12]);
				tail[12].cubeList.add(new ModelBox(tail[12], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[13] = new ModelRenderer(this);
				tail[13].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[12].addChild(tail[13]);
				tail[13].cubeList.add(new ModelBox(tail[13], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[14] = new ModelRenderer(this);
				tail[14].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[13].addChild(tail[14]);
				tail[14].cubeList.add(new ModelBox(tail[14], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[15] = new ModelRenderer(this);
				tail[15].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[14].addChild(tail[15]);
				tail[15].cubeList.add(new ModelBox(tail[15], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[16] = new ModelRenderer(this);
				tail[16].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[15].addChild(tail[16]);
				tail[16].cubeList.add(new ModelBox(tail[16], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[17] = new ModelRenderer(this);
				tail[17].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[16].addChild(tail[17]);
				tail[17].cubeList.add(new ModelBox(tail[17], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[18] = new ModelRenderer(this);
				tail[18].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[17].addChild(tail[18]);
				tail[18].cubeList.add(new ModelBox(tail[18], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[19] = new ModelRenderer(this);
				tail[19].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[18].addChild(tail[19]);
				tail[19].cubeList.add(new ModelBox(tail[19], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[20] = new ModelRenderer(this);
				tail[20].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[19].addChild(tail[20]);
				tail[20].cubeList.add(new ModelBox(tail[20], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[21] = new ModelRenderer(this);
				tail[21].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[20].addChild(tail[21]);
				tail[21].cubeList.add(new ModelBox(tail[21], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[22] = new ModelRenderer(this);
				tail[22].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[21].addChild(tail[22]);
				tail[22].cubeList.add(new ModelBox(tail[22], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				tail[23] = new ModelRenderer(this);
				tail[23].setRotationPoint(0.0F, 0.0F, 4.0F);
				tail[22].addChild(tail[23]);
				tail[23].cubeList.add(new ModelBox(tail[23], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				
				tailEnd = new ModelRenderer(this);
				tailEnd.setRotationPoint(0.0F, 0.0F, 4.0F);
				//tail[23].addChild(tailEnd);
				tail[9].addChild(tailEnd);
				tail[10].showModel = false;
				setRotationAngle(tailEnd, 0.2618F, 0.0F, 0.0F);
				tailEnd.cubeList.add(new ModelBox(tailEnd, 58, 58, -2.0F, -0.5F, 0.0F, 4, 1, 2, 0.0F, false));
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.5F, 2.0F);
				tailEnd.addChild(bone);
				setRotationAngle(bone, 0.2618F, 0.0F, 0.0F);
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone.addChild(bone14);
				setRotationAngle(bone14, 0.0F, 0.7854F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 56, 50, -1.5F, 0.0F, -1.5F, 3, 1, 3, 0.0F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -0.5F, 2.0F);
				tailEnd.addChild(bone2);
				setRotationAngle(bone2, -0.2618F, 0.0F, 0.0F);
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.0F, 1.0F, 0.0F);
				bone2.addChild(bone15);
				setRotationAngle(bone15, 0.0F, 0.7854F, 0.0F);
				bone15.cubeList.add(new ModelBox(bone15, 60, 54, -1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F, false));
	
				for (int j = 1; j < tailSway.length; j++) {
					tailSway[j] = new Vector3f((rand.nextFloat() - 0.5f) * 0.2618F,
					 (rand.nextFloat() - 0.5f) * 0.5236F, (rand.nextFloat() - 0.5f) * 0.0436F);
				}
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				body.render(f5);
				bipedRightLeg.render(f5);
				bipedLeftLeg.render(f5);
				tail[0].render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				EntityCustom entity = (EntityCustom)e;
				float pt = f2 - entity.ticksExisted;
				int pose = entity.getPose();
				Vector3f[][] tailPose = tailPoseRobeOn;
				if (entity.isRobeOff()) {
					tailPose = tailPoseRobeOff;
					body.rotateAngleX = 1.8326F;
					bipedHead.rotateAngleX += -1.5708F;
					setRotationAngle(bipedRightUpperArm, -0.5236F, 0.2618F, 1.3963F);
					bipedRightForeArm.rotateAngleX = -1.0472F;
					setRotationAngle(bipedLeftUpperArm, -0.5236F, entity.raiseLeftArm ? -1.5708F : -0.2618F, -1.3963F);
					bipedLeftForeArm.rotateAngleX = -1.0472F;
					rightThigh.rotateAngleY = 1.309F;
					leftThigh.rotateAngleY = -1.309F;
					tail[0].rotateAngleX = 1.5708F;
				} else {
					body.rotateAngleX = 1.0472F;
					bipedHead.rotateAngleX += -1.0472F;
					setRotationAngle(bipedRightUpperArm, -1.0472F, 0.0F, 0.0F);
					bipedRightForeArm.rotateAngleX = -0.2618F;
					setRotationAngle(bipedLeftUpperArm, -1.0472F, 0.0F, 0.0F);
					bipedLeftForeArm.rotateAngleX = -0.2618F;
					rightThigh.rotateAngleY = 0.6545F;
					leftThigh.rotateAngleY = -0.6545F;
					tail[0].rotateAngleX = 0.7854F;
				}
				if (entity.poseProgress >= 0) {
					switch (pose) {
					case 0:
						int j = MathHelper.clamp((int)(((float)entity.poseProgressEnd - (float)entity.poseProgress - pt + 1f) / (float)entity.poseProgressEnd * 14.0f), 0, 14);
						ModelRenderer mr = tail[8 + j];
						if (mr.childModels == null || !mr.childModels.contains(tailEnd)) {
							mr.addChild(tailEnd);
						}
						tail[9 + j].showModel = false;
						if (tail[9 + j].childModels != null) {
							tail[9 + j].childModels.remove(tailEnd);
						}
						break;
					case 1:
					case 2:
						j = MathHelper.clamp((int)(((float)entity.poseProgress + pt) / (float)entity.poseProgressEnd * 14.0f), 0, 14);
						if (tail[8 + j].childModels != null) {
							tail[8 + j].childModels.remove(tailEnd);
						}
						mr = tail[9 + j];
						if (mr.childModels == null || !mr.childModels.contains(tailEnd)) {
							mr.addChild(tailEnd);
						}
						if (j < 14) {
							tail[10 + j].showModel = false;
						}
						float f9 = (float)(entity.poseProgressEnd - entity.poseProgress + 1);
						for (int i = 9 + j; i > 0; i--) {
							float f6 = tail[i-1].rotateAngleX;
							float f7 = tail[i-1].rotateAngleY;
							float f8 = tail[i-1].rotateAngleZ;
							if (i == 1) {
								f6 = 0.0F;
								f7 = 0.0F;
								f8 = 0.0F;
							}
							f6 += (tailPose[pose][i].x - f6) / f9;
							f7 += (tailPose[pose][i].y - f7) / f9;
							f8 += (tailPose[pose][i].z - f8) / f9;
							this.setRotationAngle(tail[i], f6, f7, f8);
							tail[i].showModel = true;
						}
						break;
					}
				}
				if (pose == 2 || entity.poseProgress < 0) {
					for (int j = 1; j < 10; j++) {
						tail[j].rotateAngleX = tailPose[pose][j].x + MathHelper.sin((f2 - j) * 0.1F) * tailSway[j].x;
						tail[j].rotateAngleZ = tailPose[pose][j].z + MathHelper.cos((f2 - j) * 0.1F) * tailSway[j].z;
						tail[j].rotateAngleY = tailPose[pose][j].y + MathHelper.sin((f2 - j) * 0.1F) * tailSway[j].y;
					}
				}
				this.bipedHead.rotationPointY = -12.0F;
				this.copyModelAngles(bipedHead, bipedHeadwear);
				this.setRotationAngle(rightArm, bipedRightArm.rotateAngleX, bipedRightArm.rotateAngleY, bipedRightArm.rotateAngleZ);
				this.setRotationAngle(leftArm, bipedLeftArm.rotateAngleX, bipedLeftArm.rotateAngleY, bipedLeftArm.rotateAngleZ);
				bipedRightLeg.rotationPointY = 15.0F;
				bipedLeftLeg.rotationPointY = 15.0F;
			}
		}
	}

	public static class PlayerHook {
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void onMouseRightButton(MouseEvent event) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (Minecraft.getMinecraft().currentScreen == null && player.getRidingEntity() instanceof EntityCustom
			 && event.getButton() == 1 && !((EntityCustom)player.getRidingEntity()).isRiderHoldingSenbon()
			 && !((EntityCustom)player.getRidingEntity()).isRiderHoldingSenbonArm()) {
				NarutomodMod.PACKET_HANDLER.sendToServer(new Message(event.isButtonstate()));
			}
		}
		
		public static class Message implements IMessage {
			boolean pressed;
	
			public Message() {
			}
	
			public Message(boolean var1) {
				this.pressed = var1;
			}
	
			public static class Handler implements IMessageHandler<Message, IMessage> {
				@Override
				public IMessage onMessage(Message message, MessageContext context) {
					EntityPlayerMP entity = context.getServerHandler().player;
					entity.getServerWorld().addScheduledTask(() -> {
						if (entity.world.isBlockLoaded(new BlockPos(entity.posX, entity.posY, entity.posZ))
						 && entity.getRidingEntity() instanceof EntityCustom) {
							((EntityCustom)entity.getRidingEntity()).shouldBlock = message.pressed;
						}
					});
					return null;
				}
			}
	
			public void toBytes(ByteBuf buf) {
				buf.writeBoolean(this.pressed);
			}
	
			public void fromBytes(ByteBuf buf) {
				this.pressed = buf.readBoolean();
			}
		}
	}
}
