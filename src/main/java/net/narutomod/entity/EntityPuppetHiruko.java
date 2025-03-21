
package net.narutomod.entity;

import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.potion.PotionCorrosion;
import net.narutomod.potion.PotionInstantDamage;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.item.ItemScrollHiruko;
import net.narutomod.item.ItemSenbon;
import net.narutomod.item.ItemPoisonSenbon;
import net.narutomod.item.ItemSenbonArm;
import net.narutomod.Chakra;
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
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.inventory.EntityEquipmentSlot;
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
import javax.annotation.Nullable;
import io.netty.buffer.ByteBuf;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppetHiruko extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 389;

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
		private static final DataParameter<Boolean> AKATSUKI = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		public static final float MAXHEALTH = 240.0f;
		private int poseProgressEnd = 14;
		private int poseProgress = -1;
		private Object model;
		private boolean shouldBlock;
		private boolean maskOff;
		private boolean raiseLeftArm;

		public EntityCustom(World world) {
			super(world);
			this.setSize(1.4f, 1.7f);
			this.stepHeight = 4.0f;
			this.isImmuneToFire = false;
			this.dieOnNoPassengers = false;
			this.effectivePotions.addAll(Lists.newArrayList(PotionAmaterasuFlame.potion, PotionCorrosion.potion, PotionInstantDamage.potion));
		}

		public EntityCustom(EntityLivingBase summonerIn, double x, double y, double z) {
			super(summonerIn, x, y, z);
			this.setSize(1.4f, 1.7f);
			this.stepHeight = 4.0f;
			this.isImmuneToFire = false;
			this.dieOnNoPassengers = false;
			this.effectivePotions.addAll(Lists.newArrayList(PotionAmaterasuFlame.potion, PotionCorrosion.potion, PotionInstantDamage.potion));
			this.setHealth(this.getMaxHealth());
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(POSE, Integer.valueOf(0));
			this.dataManager.register(ROBE_OFF, Boolean.valueOf(false));
			this.dataManager.register(AKATSUKI, Boolean.valueOf(false));
		}

		private void setPose(int pose) {
			if (pose != this.getPose()) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hiruko_tail")),
				 1f, this.rand.nextFloat() * 0.4f + 0.7f);
			}
			this.poseProgress = 0;
			this.poseProgressEnd = pose == 1 ? 6 : pose == 2 ? 3 : 14;
			if (!this.world.isRemote) {
				this.dataManager.set(POSE, Integer.valueOf(pose));
			}
		}
	
		public int getPose() {
			return ((Integer)this.getDataManager().get(POSE)).intValue();
		}

		protected void takeRobeOff(boolean b) {
			this.dataManager.set(ROBE_OFF, Boolean.valueOf(b));
		}
	
		public boolean isRobeOff() {
			return ((Boolean)this.getDataManager().get(ROBE_OFF)).booleanValue();
		}

		public void setAkatsuki(boolean b) {
			this.dataManager.set(AKATSUKI, Boolean.valueOf(b));
		}
	
		public boolean isAkatsuki() {
			return ((Boolean)this.getDataManager().get(AKATSUKI)).booleanValue();
		}

		public boolean wearsAkatsukiRobe() {
			Entity entity = this.getControllingPassenger();
			return (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ItemAkatsukiRobe.body) || this.isAkatsuki();
		}

		public boolean wearsHat() {
			Entity entity = this.getControllingPassenger();
			return (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemAkatsukiRobe.helmet) || this.isAkatsuki();
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (POSE.equals(key) && this.world.isRemote) {
				this.poseProgress = 0;
				int pose = this.getPose();
				this.poseProgressEnd = pose == 1 ? 6 : pose == 2 ? 3 : 14;
			}
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getAttributeMap().registerAttribute(EntityPlayer.REACH_DISTANCE);
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAXHEALTH);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(6.0D);
		}

		@Override
		public float getEyeHeight() {
			return this.isRobeOff() ? 0.4375f : 1.0f;
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
			return this.hasPuppetJutsu(passenger) && super.canFitPassenger(passenger);
		}

		@Override
		public boolean shouldRiderSit() {
			return true;
		}

		@Override
		protected void damageEntity(DamageSource source, float amount) {
			if (this.shouldBlock) {
				this.playSound(SoundEvent.REGISTRY
				 .getObject(new ResourceLocation("narutomod:ting")), 0.6f, this.rand.nextFloat() * 0.6f + 0.8f);
				amount *= source.isExplosion() ? 0.9f - this.rand.nextFloat() * 0.2f : (this.rand.nextFloat() * 0.2f);
			}
			super.damageEntity(source, amount);
		}

		protected void blockAttack(boolean b) {
			this.shouldBlock = b;
		}

		protected boolean isBlocking() {
			return this.shouldBlock;
		}

		private boolean hasPuppetJutsu(@Nullable Entity controllingRider) {
			if (controllingRider instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)controllingRider, ItemNinjutsu.block);
				return stack != null && ((ItemNinjutsu.RangedItem)stack.getItem())
				 .canActivateJutsu(stack, ItemNinjutsu.PUPPET, (EntityPlayer)controllingRider) == EnumActionResult.SUCCESS;
			} else {
				return controllingRider instanceof EntitySasori.EntityCustom;
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			boolean robeOff = this.isRobeOff();
			if (this.isAkatsuki() && !robeOff && !this.maskOff && this.rand.nextInt(200) == 0) {
				this.playSound(SoundEvent.REGISTRY
				 .getObject(new ResourceLocation("narutomod:dingding")), 0.8f, this.rand.nextFloat() * 0.1f + 0.95f);
			}
			Entity passenger = this.getControllingPassenger();
			this.setOwnerCanSteer(this.hasPuppetJutsu(passenger), robeOff ? 1.5f : 0.5f);
			if (!this.world.isRemote && this.isBeingRidden() && this.ticksExisted % 20 == 1 && passenger instanceof EntityLivingBase
			 && !Chakra.pathway((EntityLivingBase)passenger).consume(ItemNinjutsu.PUPPET.chakraUsage * 10)) {
				passenger.dismountRidingEntity();
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

		public boolean hasSenbonArmInRiderInventory() {
			Entity rider = this.getControllingPassenger();
			if (rider instanceof EntityPlayer) {
				return ProcedureUtils.hasItemInInventory((EntityPlayer)rider, ItemSenbonArm.block);
			} else if (rider instanceof EntityNinjaMob.Base) {
				for (int i = 0; i < ((EntityNinjaMob.Base)rider).getInventorySize(); i++) {
					if (((EntityNinjaMob.Base)rider).getItemFromInventory(i).getItem() == ItemSenbonArm.block) {
						return true;
					}
				}
				return this.isRiderHoldingSenbonArm();
			}
			return false;
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
				} else {
					GlStateManager.scale(1.125F, 1.125F, 1.125F);
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
					this.model.robeAkatsuki.showModel = false;
					this.model.backShield.showModel = true;
				} else {
					boolean akatsuki = entity.wearsAkatsukiRobe();
					this.model.mask.showModel = !entity.maskOff;
					this.model.hat.showModel = entity.wearsHat() && !entity.maskOff;
					this.model.hair.showModel = !this.model.hat.showModel;
					this.model.robe.showModel = !akatsuki;
					this.model.robeAkatsuki.showModel = akatsuki;
					this.model.backShield.showModel = false;
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
			private final Random rand = new Random();
			private final ModelRenderer body;
			private final ModelRenderer head;
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
			private final ModelRenderer robeAkatsuki;
			private final ModelRenderer bone37;
			private final ModelRenderer rightArm2;
			private final ModelRenderer rightUpperArm2;
			private final ModelRenderer rightForeArm2;
			private final ModelRenderer leftArm2;
			private final ModelRenderer leftUpperArm2;
			private final ModelRenderer leftForeArm2;
			private final ModelRenderer backShield;
			private final ModelRenderer bone4;
			private final ModelRenderer bone42;
			private final ModelRenderer bone3;
			private final ModelRenderer bone43;
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
			private final ModelRenderer[][] tail = new ModelRenderer[30][2];
			private final Vector3f[] tailSway = new Vector3f[10];
			private final float[][][][] tailPose = { // float[2][3][30][3]
				{ // robe on
					{
						{ 0.7854F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F },
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F },
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }
					},
					{
						{ 0.7854F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F },
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.1745F, 0.0F, 0.0F }, { 0.1745F, 0.0F, 0.0F },
						{ 0.0873F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }
					},
					{
						{ 0.7854F, 0.0F, 0.0F }, 
						{ 0.2618F, -0.5236F, -0.0873F }, { 0.2618F, -0.5236F, -0.0873F }, { 0.2618F, -0.5236F, -0.0873F },
						{ 0.2618F, -0.5236F, -0.1745F }, { 0.2618F, 0.0F, -0.1745F }, { 0.2618F, 0.0F, -0.1745F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, 
						{ 0.1745F, 0.0F, 0.0F }, { 0.0873F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }
					}
				},
				{ // robe off
					{
						{ 1.5708F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F },
						{ 0.2618F, 0.0F, 0.0F }, { 0.1745F, 0.0F, 0.0F }, { 0.0873F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F },
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }
					},
					{
						{ 1.5708F, 0.0F, 0.0F }, 
						{ 0.2618F, 0.0F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.1745F, 0.0F, 0.0F },
						{ 0.1745F, 0.0F, 0.0F }, { 0.1745F, 0.0F, 0.0F }, { 0.0873F, 0.0F, 0.0F }, 
						{ 0.0873F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F },
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, 
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F },
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F },
						{ 0.0436F, 0.0F, 0.0F }, { 0.0436F, 0.0F, 0.0F }
					},
					{
						{ 1.5708F, 0.0F, 0.0F }, 
						{ 0.2618F, -0.5236F, 0.0F }, { 0.2618F, -0.5236F, 0.0F }, { 0.2618F, -0.5236F, 0.0F },
						{ 0.2618F, -0.2618F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.3491F, 0.0F, 0.0F },
						{ 0.3491F, 0.0F, 0.0F }, { 0.3491F, 0.0F, 0.0F }, { 0.3491F, 0.0F, 0.0F }, 
						{ 0.3491F, 0.0F, 0.0F }, { 0.3491F, -0.0873F, 0.0F }, { 0.2618F, -0.0873F, 0.0F }, 
						{ 0.2618F, -0.0873F, 0.0F }, { 0.2618F, -0.0873F, 0.0F }, { 0.2618F, -0.0873F, 0.0F }, 
						{ 0.2618F, -0.0873F, 0.0F }, { 0.1745F, -0.0873F, 0.0F }, { 0.1745F, -0.0873F, 0.0F }, 
						{ 0.2618F, -0.0873F, 0.0F }, { 0.2618F, -0.0873F, 0.0F }, { 0.2618F, -0.0873F, 0.0F }, 
						{ 0.2618F, -0.0873F, 0.0F }, { 0.2618F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }, 
						{ 0.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 0.0F }
					}
				}
			};
	
			public ModelPuppetHiruko() {
				textureWidth = 128;
				textureHeight = 128;
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 15.0F, 0.0F);
				setRotationAngle(body, 1.0472F, 0.0F, 0.0F);
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, -12.0F, 0.0F);
				body.addChild(head);
				setRotationAngle(head, -1.0472F, 0.0F, 0.0F);
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				head.addChild(bipedHead);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 44, 18, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 32, 64, -0.5F, -1.0F, -3.9F, 1, 1, 4, -0.01F, false));
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, -2.0F, 0.0F);
				bipedHead.addChild(jaw);
				setRotationAngle(jaw, 0.2618F, 0.0F, 0.0F);
				jaw.cubeList.add(new ModelBox(jaw, 0, 74, 1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));
				jaw.cubeList.add(new ModelBox(jaw, 0, 74, -3.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, true));
				jawMid = new ModelRenderer(this);
				jawMid.setRotationPoint(0.0F, 0.0F, 0.0F);
				jaw.addChild(jawMid);
				setRotationAngle(jawMid, 0.1309F, 0.0F, 0.0F);
				jawMid.cubeList.add(new ModelBox(jawMid, 12, 74, -1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));
				mask = new ModelRenderer(this);
				mask.setRotationPoint(0.0F, 24.0F, 0.0F);
				bipedHead.addChild(mask);
				mask.cubeList.add(new ModelBox(mask, 68, 10, -4.0F, -27.0F, -4.0F, 8, 4, 8, 0.25F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				head.addChild(bipedHeadwear);
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.addChild(hair);
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, -6.0F, 4.25F);
				hair.addChild(bone16);
				setRotationAngle(bone16, 1.0472F, 0.0F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone16.addChild(bone17);
				setRotationAngle(bone17, 0.5236F, 0.0F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone17.addChild(bone18);
				setRotationAngle(bone18, 0.5236F, 0.0F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.1F, false));
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone18.addChild(bone19);
				setRotationAngle(bone19, 0.2618F, 0.0F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 74, 0, -0.5F, -0.1F, 0.0F, 1, 0, 2, 0.0F, false));
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(-1.5F, -5.0F, 4.0F);
				hair.addChild(bone33);
				setRotationAngle(bone33, 1.0472F, 0.0F, -0.7854F);
				bone33.cubeList.add(new ModelBox(bone33, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone33.addChild(bone34);
				setRotationAngle(bone34, 0.5236F, 0.0F, 0.0F);
				bone34.cubeList.add(new ModelBox(bone34, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone34.addChild(bone35);
				setRotationAngle(bone35, 0.5236F, 0.0F, 0.0F);
				bone35.cubeList.add(new ModelBox(bone35, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.1F, false));
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone35.addChild(bone36);
				setRotationAngle(bone36, 0.2618F, 0.0F, 0.0F);
				bone36.cubeList.add(new ModelBox(bone36, 74, 0, -0.5F, -0.1F, 0.0F, 1, 0, 2, 0.0F, false));
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(1.5F, -5.0F, 4.0F);
				hair.addChild(bone29);
				setRotationAngle(bone29, 1.0472F, 0.0F, 0.7854F);
				bone29.cubeList.add(new ModelBox(bone29, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone29.addChild(bone30);
				setRotationAngle(bone30, 0.5236F, 0.0F, 0.0F);
				bone30.cubeList.add(new ModelBox(bone30, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone30.addChild(bone31);
				setRotationAngle(bone31, 0.5236F, 0.0F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.1F, false));
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(0.0F, -0.1F, 2.0F);
				bone31.addChild(bone32);
				setRotationAngle(bone32, 0.2618F, 0.0F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.0F, false));
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(-0.75F, -5.75F, 4.25F);
				hair.addChild(bone25);
				setRotationAngle(bone25, 1.0472F, 0.0F, -0.4363F);
				bone25.cubeList.add(new ModelBox(bone25, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone25.addChild(bone26);
				setRotationAngle(bone26, 0.5236F, 0.0F, 0.0F);
				bone26.cubeList.add(new ModelBox(bone26, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone26.addChild(bone27);
				setRotationAngle(bone27, 0.5236F, 0.0F, 0.0F);
				bone27.cubeList.add(new ModelBox(bone27, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.1F, false));
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone27.addChild(bone28);
				setRotationAngle(bone28, 0.2618F, 0.0F, 0.0F);
				bone28.cubeList.add(new ModelBox(bone28, 74, 0, -0.5F, -0.1F, 0.0F, 1, 0, 2, 0.0F, false));
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.75F, -5.75F, 4.25F);
				hair.addChild(bone20);
				setRotationAngle(bone20, 1.0472F, 0.0F, 0.4363F);
				bone20.cubeList.add(new ModelBox(bone20, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone20.addChild(bone22);
				setRotationAngle(bone22, 0.5236F, 0.0F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.2F, false));
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone22.addChild(bone23);
				setRotationAngle(bone23, 0.5236F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 74, 0, -0.5F, 0.0F, 0.0F, 1, 0, 2, 0.1F, false));
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(0.0F, 0.0F, 2.0F);
				bone23.addChild(bone24);
				setRotationAngle(bone24, 0.2618F, 0.0F, 0.0F);
				bone24.cubeList.add(new ModelBox(bone24, 74, 0, -0.5F, -0.1F, 0.0F, 1, 0, 2, 0.0F, false));
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
				bipedBody.cubeList.add(new ModelBox(bipedBody, 48, 34, -4.0F, 1.0F, -2.0F, 8, 12, 4, 1.0F, false));
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
				rightArm.setRotationPoint(-6.0F, 2.0F, 0.0F);
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
				leftArm.setRotationPoint(6.0F, 2.0F, 0.0F);
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
				robeAkatsuki = new ModelRenderer(this);
				robeAkatsuki.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(robeAkatsuki);
				robeAkatsuki.cubeList.add(new ModelBox(robeAkatsuki, 0, 102, -7.0F, 0.0F, -6.0F, 14, 14, 12, 0.05F, false));
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, 0.0F, -6.0F);
				robeAkatsuki.addChild(bone37);
				setRotationAngle(bone37, 0.5236F, 0.0F, 0.0F);
				bone37.cubeList.add(new ModelBox(bone37, 52, 100, -7.0F, 0.0F, -10.0F, 14, 18, 10, 0.05F, false));
				bone37.cubeList.add(new ModelBox(bone37, 0, 96, -7.0F, 16.0F, -0.75F, 14, 2, 4, 0.05F, false));
				rightArm2 = new ModelRenderer(this);
				rightArm2.setRotationPoint(-6.0F, 2.0F, 0.0F);
				robeAkatsuki.addChild(rightArm2);
				rightUpperArm2 = new ModelRenderer(this);
				rightUpperArm2.setRotationPoint(0.0F, 0.0F, 0.0F);
				rightArm2.addChild(rightUpperArm2);
				setRotationAngle(rightUpperArm2, -1.0472F, 0.0F, 0.0F);
				rightUpperArm2.cubeList.add(new ModelBox(rightUpperArm2, 0, 86, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.55F, false));
				rightForeArm2 = new ModelRenderer(this);
				rightForeArm2.setRotationPoint(-1.0F, 4.0F, 2.0F);
				rightUpperArm2.addChild(rightForeArm2);
				setRotationAngle(rightForeArm2, -0.2618F, 0.0F, 0.0F);
				rightForeArm2.cubeList.add(new ModelBox(rightForeArm2, 16, 86, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.55F, false));
				leftArm2 = new ModelRenderer(this);
				leftArm2.setRotationPoint(6.0F, 2.0F, 0.0F);
				robeAkatsuki.addChild(leftArm2);
				leftUpperArm2 = new ModelRenderer(this);
				leftUpperArm2.setRotationPoint(0.0F, 0.0F, 0.0F);
				leftArm2.addChild(leftUpperArm2);
				setRotationAngle(leftUpperArm2, -1.0472F, 0.0F, 0.0F);
				leftUpperArm2.cubeList.add(new ModelBox(leftUpperArm2, 0, 86, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.55F, true));
				leftForeArm2 = new ModelRenderer(this);
				leftForeArm2.setRotationPoint(1.0F, 4.0F, 2.0F);
				leftUpperArm2.addChild(leftForeArm2);
				setRotationAngle(leftForeArm2, -0.2618F, 0.0F, 0.0F);
				leftForeArm2.cubeList.add(new ModelBox(leftForeArm2, 16, 86, -2.0F, 0.0F, -4.0F, 4, 6, 4, 0.55F, true));
				backShield = new ModelRenderer(this);
				backShield.setRotationPoint(0.0F, 2.0F, 3.0F);
				bipedBody.addChild(backShield);
				setRotationAngle(backShield, 0.0873F, 0.0F, 0.0F);
				backShield.cubeList.add(new ModelBox(backShield, 0, 0, -2.0F, 0.0F, -1.0F, 4, 8, 2, 1.0F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-3.15F, 5.0F, 1.35F);
				backShield.addChild(bone4);
				setRotationAngle(bone4, 0.0F, -0.5236F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 26, -2.5F, -5.0F, -2.5F, 2, 8, 2, 1.0F, false));
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(-1.5F, 4.0F, -1.5F);
				bone4.addChild(bone42);
				setRotationAngle(bone42, 0.0F, 0.0F, -0.2618F);
				bone42.cubeList.add(new ModelBox(bone42, 0, 2, -1.0F, -9.2F, -1.0F, 1, 8, 2, 1.0F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(3.15F, 5.0F, 1.35F);
				backShield.addChild(bone3);
				setRotationAngle(bone3, 0.0F, 0.5236F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 26, 0.5F, -5.0F, -2.5F, 2, 8, 2, 1.0F, true));
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(1.5F, 4.0F, -1.5F);
				bone3.addChild(bone43);
				setRotationAngle(bone43, 0.0F, 0.0F, 0.2618F);
				bone43.cubeList.add(new ModelBox(bone43, 0, 2, 0.0F, -9.2F, -1.0F, 1, 8, 2, 1.0F, true));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 8.5F, 1.5F);
				backShield.addChild(bone6);
				setRotationAngle(bone6, -0.5236F, 0.0F, 0.0F);
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 0.6F, 0.1F);
				bone6.addChild(bone9);
				setRotationAngle(bone9, -0.0873F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 40, 6, -2.0F, 0.5F, -2.5F, 4, 4, 2, 1.0F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(-3.0F, 1.0F, 0.0F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, 0.0F, -0.4625F, -0.2618F);
				bone7.cubeList.add(new ModelBox(bone7, 46, 60, -2.45F, 0.25F, -2.52F, 2, 4, 2, 1.0F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(3.0F, 1.0F, 0.0F);
				bone6.addChild(bone5);
				setRotationAngle(bone5, 0.0F, 0.4625F, 0.2618F);
				bone5.cubeList.add(new ModelBox(bone5, 46, 60, 0.45F, 0.25F, -2.52F, 2, 4, 2, 1.0F, true));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-6.0F, -10.0F, 0.0F);
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
				bipedLeftArm.setRotationPoint(6.0F, -10.0F, 0.0F);
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

				tail[0][0] = new ModelRenderer(this);
				tail[0][0].setRotationPoint(0.0F, 15.0F, 0.0F);
				tail[0][0].cubeList.add(new ModelBox(tail[0][0], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
				//tail[0][1] = new ModelRenderer(this);
				for (int i = 1; i < tail.length; i++) {
					tail[i][0] = new ModelRenderer(this);
					tail[i][0].setRotationPoint(0.0F, 0.0F, 4.0F);
					tail[i-1][0].addChild(tail[i][0]);
					tail[i][0].cubeList.add(new ModelBox(tail[i][0], 32, 56, -2.0F, -0.5F, 0.0F, 4, 1, 4, 0.0F, false));
					tail[i][1] = new ModelRenderer(this);
					tail[i][1].setRotationPoint(0.0F, 0.0F, 4.0F);
					tail[i][0].addChild(tail[i][1]);
					setRotationAngle(tail[i][1], 0.2618F, 0.0F, 0.0F);
					tail[i][1].cubeList.add(new ModelBox(tail[i][1], 58, 58, -2.0F, -0.5F, 0.0F, 4, 1, 2, 0.0F, false));
					ModelRenderer bone = new ModelRenderer(this);
					bone.setRotationPoint(0.0F, 0.5F, 2.0F);
					tail[i][1].addChild(bone);
					setRotationAngle(bone, 0.2618F, 0.0F, 0.0F);
					ModelRenderer bone14 = new ModelRenderer(this);
					bone14.setRotationPoint(0.0F, -1.0F, 0.0F);
					bone.addChild(bone14);
					setRotationAngle(bone14, 0.0F, 0.7854F, 0.0F);
					bone14.cubeList.add(new ModelBox(bone14, 56, 50, -1.5F, 0.0F, -1.5F, 3, 1, 3, 0.0F, false));
					ModelRenderer bone2 = new ModelRenderer(this);
					bone2.setRotationPoint(0.0F, -0.5F, 2.0F);
					tail[i][1].addChild(bone2);
					setRotationAngle(bone2, -0.2618F, 0.0F, 0.0F);
					ModelRenderer bone15 = new ModelRenderer(this);
					bone15.setRotationPoint(0.0F, 1.0F, 0.0F);
					bone2.addChild(bone15);
					setRotationAngle(bone15, 0.0F, 0.7854F, 0.0F);
					bone15.cubeList.add(new ModelBox(bone15, 60, 54, -1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F, false));
				}
				tail[10][0].showModel = false;
				for (int j = 1; j < tailSway.length; j++) {
					tailSway[j] = new Vector3f((rand.nextFloat() - 0.5f) * 0.2618F,
					 (rand.nextFloat() - 0.5f) * 0.5236F, (rand.nextFloat() - 0.5f) * 0.0436F);
				}
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.translate(0.0f, 0.0f, 0.25f);
				body.render(f5);
				bipedRightLeg.render(f5);
				bipedLeftLeg.render(f5);
				tail[0][0].render(f5);
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
				boolean robeOff = entity.isRobeOff();
				int robeIdx = robeOff ? 1 : 0;
				if (robeOff) {
					body.rotateAngleX = 1.8326F;
					head.rotateAngleX = -1.5708F;
					setRotationAngle(bipedRightUpperArm, -0.5236F, 0.2618F, 1.3963F);
					bipedRightForeArm.rotateAngleX = -1.0472F;
					setRotationAngle(bipedLeftUpperArm, -0.5236F, entity.raiseLeftArm ? -1.5708F : -0.2618F, -1.3963F);
					bipedLeftForeArm.rotateAngleX = -1.0472F;
					rightThigh.rotateAngleY = 1.309F;
					leftThigh.rotateAngleY = -1.309F;
				} else {
					body.rotateAngleX = 1.0472F;
					head.rotateAngleX = -1.0472F;
					setRotationAngle(bipedRightUpperArm, -1.0472F, 0.0F, 0.0F);
					bipedRightForeArm.rotateAngleX = -0.2618F;
					setRotationAngle(bipedLeftUpperArm, -1.0472F, 0.0F, 0.0F);
					bipedLeftForeArm.rotateAngleX = -0.2618F;
					rightThigh.rotateAngleY = 0.6545F;
					leftThigh.rotateAngleY = -0.6545F;
				}
				bipedRightArm.rotationPointX = rightArm.rotationPointX;
				bipedLeftArm.rotationPointX = leftArm.rotationPointX;
				tail[0][0].rotateAngleX = tailPose[robeIdx][pose][0][0];
				if (entity.poseProgress >= 0) {
					switch (pose) {
					case 0:
						int j = MathHelper.clamp((int)(((float)entity.poseProgressEnd - (float)entity.poseProgress - pt + 1f) / (float)entity.poseProgressEnd * 13f), 0, 13);
						tail[10+j][0].showModel = false;
						for (int i = 1; i < tail.length; i++) {
							tail[i][1].showModel = false;
						}
						tail[9+j][1].showModel = true;
						break;
					case 1:
					case 2:
						int segments = pose == 1 ? 19 : 13;
						float f9 = Math.min(((float)entity.poseProgress + pt) / (float)entity.poseProgressEnd, 1.0F);
						j = (int)(f9 * (float)segments);
						for (int i = 1; i < tail.length; i++) {
							tail[i][1].showModel = false;
						}
						tail[10+j][1].showModel = true;
						if (j < 19) {
							tail[11+j][0].showModel = false;
						}
						for (int i = 10 + j; i > 0; i--) {
							if (i < 25) { // temp fix to the stack overflow bug
								tail[i][0].rotateAngleX = (tailPose[robeIdx][pose][i][0]) * f9;
								tail[i][0].rotateAngleY = (tailPose[robeIdx][pose][i][1]) * f9;
								tail[i][0].rotateAngleZ = (tailPose[robeIdx][pose][i][2]) * f9;
							}
							tail[i][0].showModel = true;
						}
						break;
					}
				}
				if (pose == 2 || entity.poseProgress < 0) {
					for (int i = 1; i < tailSway.length; i++) {
						tail[i][0].rotateAngleX = tailPose[robeIdx][pose][i][0] + MathHelper.sin((f2 - i) * 0.1F) * tailSway[i].x;
						tail[i][0].rotateAngleZ = tailPose[robeIdx][pose][i][2] + MathHelper.cos((f2 - i) * 0.1F) * tailSway[i].z;
						tail[i][0].rotateAngleY = tailPose[robeIdx][pose][i][1] + MathHelper.sin((f2 - i) * 0.1F) * tailSway[i].y;
					}
				}
				this.copyModelAngles(bipedHead, bipedHeadwear);
				this.setRotationAngle(rightArm, bipedRightArm.rotateAngleX, bipedRightArm.rotateAngleY, bipedRightArm.rotateAngleZ);
				this.setRotationAngle(leftArm, bipedLeftArm.rotateAngleX, bipedLeftArm.rotateAngleY, bipedLeftArm.rotateAngleZ);
				this.setRotationAngle(rightArm2, bipedRightArm.rotateAngleX, bipedRightArm.rotateAngleY, bipedRightArm.rotateAngleZ);
				this.setRotationAngle(leftArm2, bipedLeftArm.rotateAngleX, bipedLeftArm.rotateAngleY, bipedLeftArm.rotateAngleZ);
				bipedRightLeg.rotationPointY = 15.0F;
				bipedLeftLeg.rotationPointY = 15.0F;
			}
		}
	}

	public static class PlayerHook {
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void onRiderRender(RenderLivingEvent.Pre event) {
			if (event.getEntity().getRidingEntity() instanceof EntityCustom
			 && event.getEntity().width <= 1.4f && event.getEntity().height <= 1.95f) {
				event.setCanceled(true);
				//event.getEntity().setInvisible(true);
			}
		}

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
							((EntityCustom)entity.getRidingEntity()).blockAttack(message.pressed);
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
