
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTenTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 179;
	public static final int ENTITYID_RANGED = 180;
	private static final float MODELSCALE = 36.0F;
	private static final double TARGET_RANGE = 108.0D;
	private static final TailBeastManager BIJU_MANAGER = new TailBeastManager();

	public EntityTenTails(ElementsNarutomodMod instance) {
		super(instance, 444);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		  .id(new ResourceLocation("narutomod", "ten_tails"), ENTITYID).name("ten_tails").tracker(96, 3, true)
		  .egg(-13421773, -16777216).build());
	}

	public static TailBeastManager getBijuManager() {
		return BIJU_MANAGER;
	}

	public static class TailBeastManager extends EntityBijuManager<EntityCustom> {
		public TailBeastManager() {
			super(EntityCustom.class, 10);
		}

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}
	
	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_tentails"; 
		private static Save instance = null;

		public Save() {
			super(DATA_NAME);
		}

		public Save(String name) {
			super(name);
		}

	 	@Override
	 	public Save loadData() {
	 		instance = null;
	 		return this.getInstance();
	 	}

	 	@Override
	 	public void resetData() {
	 		super.resetData();
	 		instance = null;
	 	}

	 	public static Save getInstance() {
	 		if (instance == null) {
	 			MapStorage storage = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0).getMapStorage();
	 			instance = (Save) storage.getOrLoadData(Save.class, DATA_NAME);
	 			if (instance == null) {
	 				instance = new Save(); 
	 				storage.setData(DATA_NAME, instance);
	 			} 
	 		} 
	 		return instance;
	 	}

	 	@Override
	 	protected EntityBijuManager getBijuManager() {
	 		return BIJU_MANAGER;
	 	}

	 	@Override
	 	protected EntityTailedBeast.Base createEntity(World world) {
	 		return new EntityCustom(world);
	 	}
	}

	public static class EntityCustom extends EntityTailedBeast.Base {
		public EntityCustom(World world) {
			super(world);
			this.setSize(MODELSCALE * 0.2F, MODELSCALE * 0.72F);
			this.experienceValue = 16000;
			this.stepHeight = this.height / 3.0F;
			this.setAngerLevel(2);
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.2F, MODELSCALE * 0.72F);
			this.experienceValue = 16000;
			this.stepHeight = this.height / 3.0F;
			this.setAngerLevel(2);
		}

		@Override
		public float getModelScale() {
			return MODELSCALE;
		}

		@Override
		public EntityBijuManager getBijuManager() {
			return BIJU_MANAGER;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.8D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100000.0D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1000.0D);
			//this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(40.0D);
		}

		@Override
		public SoundEvent getAmbientSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:MonsterGrowl"));
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return null;
		}

		@Override
		public SoundEvent getDeathSound() {
			return null;
		}

		@Override
		protected float getSoundPitch() {
			return this.rand.nextFloat() * 0.4F + 0.5F;
		}

		@Override
		public void onEntityUpdate() {
			super.onEntityUpdate();
			EntityPlayer jinchuriki = this.getBijuManager().getJinchurikiPlayer();
			if (!this.world.isRemote && jinchuriki != null && jinchuriki.equals(this.getControllingPassenger())
			 && !ItemRinnegan.wearingRinnesharingan(jinchuriki)) {
				this.setDead();
			}
			if (this.getAge() == 1) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodspawn")),
				 10f, this.rand.nextFloat() * 0.6f + 0.6f);
			}
			if (!this.world.isRemote && this.isFuuinInProgress()) {
				this.setTransparency(1.0f - this.getFuuinProgress());
				Entity entity = this.getTargetVessel();
				if (entity != null && entity.getDistance(this) > 10d) {
					ProcedureUtils.addVelocity(entity, this.getPositionVector().subtract(entity.getPositionVector()).scale(0.005d));
				}
			}
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			if (!this.world.isRemote && entity instanceof EntityLivingBase && !this.isRidingSameEntity(entity) && !this.isOnSameTeam(entity))
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 5, false, false));
			super.collideWithEntity(entity);
		}

		@Override
		public void onAddedToWorld() {
			boolean flag = BIJU_MANAGER.getHasLived();
			super.onAddedToWorld();
			if (!this.world.isRemote && !flag) {
				BIJU_MANAGER.setHasLived(!this.spawnedBySpawner);
			}
		}

		@Override
		public Vec3d getPositionMouth() {
			return Vec3d.fromPitchYaw(this.rotationPitch, this.rotationYawHead)
			 .scale(0.625d * MODELSCALE).addVector(this.posX, this.posY + 0.5d * MODELSCALE, this.posZ);
		}

		/*@Override
		public Vec3d getPositionEyes(float partialTicks) {
			float pitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
			float headyaw = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
			return Vec3d.fromPitchYaw(pitch, headyaw).scale(MODELSCALE * 0.5f).add(super.getPositionEyes(partialTicks));
		}*/
	}

	public static class CoffinSealJutsu implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (ItemRinnegan.wearingRinnegan(entity) || ItemTenseigan.isWearing(entity)) {
				Entity entity1 = ProcedureUtils.objectEntityLookingAt(entity, 20d).entityHit;
				if (entity1 instanceof EntityCustom) {
					EntityCustom jubi = (EntityCustom)entity1;
					if (jubi.getHealth() >= jubi.getMaxHealth() * 0.1f) {
						jubi.setHealth(jubi.getMaxHealth() * 0.1f - 1000f);
					}
					jubi.fuuinIntoVessel(entity, 300);
					return true;
				}
			}
			return false;
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderTenTails(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderTenTails extends EntityTailedBeast.ClientOnly.Renderer<EntityCustom> {
			//private final ModelTenTails tentailsModel = new ModelTenTails();
			//private final ModelTenTailsV1 tentailsModelV1 = new ModelTenTailsV1();
			//private final ModelGedoMazo gedomazoModel = new ModelGedoMazo();
			private final ResourceLocation main_texture = new ResourceLocation("narutomod:textures/tentails.png");
			private final ResourceLocation v1_texture = new ResourceLocation("narutomod:textures/tentailsl1.png");
	
			public RenderTenTails(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelTenTailsV1(), MODELSCALE * 0.5F);
			}
	
		 	@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.v1_texture;
			}
		}
	
		// Made with Blockbench 3.7.4
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelTenTailsV1 extends ModelBiped {
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer eyes;
			//private final ModelRenderer bipedBody;
			//private final ModelRenderer bipedHead;
			private final ModelRenderer upperTeeth;
			private final ModelRenderer jaw;
			private final ModelRenderer bone5;
			private final ModelRenderer bone22;
			private final ModelRenderer bone8;
			private final ModelRenderer lowerTeeth;
			private final ModelRenderer bone;
			private final ModelRenderer bone3;
			private final ModelRenderer bone10;
			private final ModelRenderer bone83;
			private final ModelRenderer hump;
			private final ModelRenderer spike19;
			private final ModelRenderer bone89;
			private final ModelRenderer bone90;
			private final ModelRenderer bone91;
			private final ModelRenderer spike2;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer spike3;
			private final ModelRenderer bone38;
			private final ModelRenderer bone39;
			private final ModelRenderer bone40;
			private final ModelRenderer spike4;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
			private final ModelRenderer bone43;
			private final ModelRenderer bone11;
			private final ModelRenderer spike5;
			private final ModelRenderer bone44;
			private final ModelRenderer bone45;
			private final ModelRenderer bone46;
			private final ModelRenderer spike6;
			private final ModelRenderer bone47;
			private final ModelRenderer bone48;
			private final ModelRenderer bone49;
			private final ModelRenderer spike7;
			private final ModelRenderer bone50;
			private final ModelRenderer bone51;
			private final ModelRenderer bone52;
			private final ModelRenderer spike8;
			private final ModelRenderer bone53;
			private final ModelRenderer bone54;
			private final ModelRenderer bone55;
			private final ModelRenderer bone12;
			private final ModelRenderer spike9;
			private final ModelRenderer bone56;
			private final ModelRenderer bone57;
			private final ModelRenderer bone58;
			private final ModelRenderer spike10;
			private final ModelRenderer bone59;
			private final ModelRenderer bone60;
			private final ModelRenderer bone61;
			private final ModelRenderer spike11;
			private final ModelRenderer bone62;
			private final ModelRenderer bone63;
			private final ModelRenderer bone64;
			private final ModelRenderer spike12;
			private final ModelRenderer bone65;
			private final ModelRenderer bone66;
			private final ModelRenderer bone67;
			private final ModelRenderer bone13;
			private final ModelRenderer spike13;
			private final ModelRenderer bone68;
			private final ModelRenderer bone69;
			private final ModelRenderer bone70;
			private final ModelRenderer spike14;
			private final ModelRenderer bone71;
			private final ModelRenderer bone72;
			private final ModelRenderer bone73;
			private final ModelRenderer spike15;
			private final ModelRenderer bone74;
			private final ModelRenderer bone75;
			private final ModelRenderer bone76;
			private final ModelRenderer spike16;
			private final ModelRenderer bone77;
			private final ModelRenderer bone78;
			private final ModelRenderer bone79;
			private final ModelRenderer bone14;
			private final ModelRenderer bone16;
			private final ModelRenderer spike17;
			private final ModelRenderer bone80;
			private final ModelRenderer bone81;
			private final ModelRenderer bone82;
			private final ModelRenderer bone7;
			private final ModelRenderer bone9;
			private final ModelRenderer bone34;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer cube_r17;
			private final ModelRenderer cube_r16;
			private final ModelRenderer rightHand;
			private final ModelRenderer bone143;
			private final ModelRenderer bone144;
			private final ModelRenderer bone18;
			private final ModelRenderer bone19;
			private final ModelRenderer bone2;
			private final ModelRenderer bone6;
			private final ModelRenderer bone15;
			private final ModelRenderer bone17;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer leftHand;
			private final ModelRenderer bone24;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			private final ModelRenderer bone27;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer[][] Tail = new ModelRenderer[10][10];
			private final float tailSwayX[][] = new float[10][10];
			private final float tailSwayZ[][] = new float[10][10];
			private final Random rand = new Random();
		
			public ModelTenTailsV1() {
				textureWidth = 64;
				textureHeight = 64;
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 21.5F, 5.0F);
				
		
				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, -5.5F, -7.0F);
				bipedHeadwear.addChild(eyes);
				eyes.cubeList.add(new ModelBox(eyes, 0, 26, -1.5F, -3.5F, -6.05F, 3, 3, 0, 0.0F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 21.5F, 5.0F);
				
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -5.5F, -7.0F);
				bipedBody.addChild(bipedHead);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 45, 0, -1.5F, -3.5F, -6.0F, 3, 3, 6, 0.0F, false));
		
				upperTeeth = new ModelRenderer(this);
				upperTeeth.setRotationPoint(0.0F, -0.5F, -6.0F);
				bipedHead.addChild(upperTeeth);
				setRotationAngle(upperTeeth, 1.2217F, 0.0F, 0.0F);
				upperTeeth.cubeList.add(new ModelBox(upperTeeth, 39, 0, -1.5F, 0.0F, 0.0F, 3, 3, 3, 0.0F, false));
		
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(-2.5F, -0.5F, -0.5F);
				bipedHead.addChild(jaw);
				setRotationAngle(jaw, 0.5236F, 0.0F, 0.0F);
				
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 1.0F);
				jaw.addChild(bone5);
				setRotationAngle(bone5, 0.0F, -0.1745F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 50, 9, 0.0F, 0.0F, -6.0F, 1, 2, 6, 0.0F, false));
		
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(5.0F, 0.0F, 1.0F);
				jaw.addChild(bone22);
				setRotationAngle(bone22, 0.0F, 0.1745F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 50, 9, -1.0F, 0.0F, -6.0F, 1, 2, 6, 0.0F, true));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(2.5F, 2.0F, 1.0F);
				jaw.addChild(bone8);
				bone8.cubeList.add(new ModelBox(bone8, 46, 17, -1.5F, -2.0F, -6.0F, 3, 2, 6, 0.0F, false));
		
				lowerTeeth = new ModelRenderer(this);
				lowerTeeth.setRotationPoint(2.5F, 0.0F, -5.0F);
				jaw.addChild(lowerTeeth);
				setRotationAngle(lowerTeeth, -0.6981F, 0.0F, 0.0F);
				lowerTeeth.cubeList.add(new ModelBox(lowerTeeth, 44, 9, -1.5F, -1.75F, 0.0F, 3, 2, 3, 0.0F, false));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-1.0F, -2.0F, -1.0F);
				bipedHead.addChild(bone);
				setRotationAngle(bone, 0.0F, -0.1745F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 46, 25, -1.5F, -1.5F, -4.75F, 2, 3, 6, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(1.0F, -2.0F, -1.0F);
				bipedHead.addChild(bone3);
				setRotationAngle(bone3, 0.0F, 0.1745F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 46, 25, -0.5F, -1.5F, -4.75F, 2, 3, 6, 0.0F, true));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(1.0F, -3.4F, 0.5F);
				bipedHead.addChild(bone10);
				setRotationAngle(bone10, -0.5236F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 24, 36, -3.5F, 0.134F, -0.5F, 5, 4, 3, 0.0F, false));
		
				bone83 = new ModelRenderer(this);
				bone83.setRotationPoint(0.0F, -5.5F, -5.0F);
				bipedBody.addChild(bone83);
				setRotationAngle(bone83, -0.5236F, 0.0F, 0.0F);
				bone83.cubeList.add(new ModelBox(bone83, 0, 0, -3.5F, -2.5F, -3.0F, 7, 6, 7, 0.0F, false));
		
				hump = new ModelRenderer(this);
				hump.setRotationPoint(0.0F, -3.0F, 0.5F);
				bone83.addChild(hump);
				setRotationAngle(hump, 0.0F, -0.7854F, 0.0F);
				hump.cubeList.add(new ModelBox(hump, 21, 0, -3.0F, -0.5F, -3.0F, 6, 1, 6, 0.0F, false));
		
				spike19 = new ModelRenderer(this);
				spike19.setRotationPoint(2.6F, 0.05F, -2.5F);
				hump.addChild(spike19);
				setRotationAngle(spike19, 1.5708F, -0.8727F, -0.8727F);
				spike19.cubeList.add(new ModelBox(spike19, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone89 = new ModelRenderer(this);
				bone89.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike19.addChild(bone89);
				setRotationAngle(bone89, -0.0873F, 0.0F, 0.0873F);
				bone89.cubeList.add(new ModelBox(bone89, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone90 = new ModelRenderer(this);
				bone90.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone89.addChild(bone90);
				setRotationAngle(bone90, -0.0873F, 0.0F, 0.0873F);
				bone90.cubeList.add(new ModelBox(bone90, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone91 = new ModelRenderer(this);
				bone91.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone90.addChild(bone91);
				setRotationAngle(bone91, -0.0873F, 0.0F, 0.0873F);
				bone91.cubeList.add(new ModelBox(bone91, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				spike2 = new ModelRenderer(this);
				spike2.setRotationPoint(-2.6F, 0.05F, -2.5F);
				hump.addChild(spike2);
				setRotationAngle(spike2, 1.5708F, 0.8727F, 0.8727F);
				spike2.cubeList.add(new ModelBox(spike2, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike2.addChild(bone35);
				setRotationAngle(bone35, -0.0873F, 0.0F, -0.0873F);
				bone35.cubeList.add(new ModelBox(bone35, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone35.addChild(bone36);
				setRotationAngle(bone36, -0.0873F, 0.0F, -0.0873F);
				bone36.cubeList.add(new ModelBox(bone36, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone36.addChild(bone37);
				setRotationAngle(bone37, -0.0873F, 0.0F, -0.0873F);
				bone37.cubeList.add(new ModelBox(bone37, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike3 = new ModelRenderer(this);
				spike3.setRotationPoint(-2.6F, 0.05F, 2.5F);
				hump.addChild(spike3);
				setRotationAngle(spike3, -1.5708F, -0.8727F, 0.8727F);
				spike3.cubeList.add(new ModelBox(spike3, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike3.addChild(bone38);
				setRotationAngle(bone38, 0.0873F, 0.0F, -0.0873F);
				bone38.cubeList.add(new ModelBox(bone38, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone38.addChild(bone39);
				setRotationAngle(bone39, 0.0873F, 0.0F, -0.0873F);
				bone39.cubeList.add(new ModelBox(bone39, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone39.addChild(bone40);
				setRotationAngle(bone40, 0.0873F, 0.0F, -0.0873F);
				bone40.cubeList.add(new ModelBox(bone40, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike4 = new ModelRenderer(this);
				spike4.setRotationPoint(2.6F, 0.05F, 2.5F);
				hump.addChild(spike4);
				setRotationAngle(spike4, -1.5708F, 0.8727F, -0.8727F);
				spike4.cubeList.add(new ModelBox(spike4, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike4.addChild(bone41);
				setRotationAngle(bone41, 0.0873F, 0.0F, 0.0873F);
				bone41.cubeList.add(new ModelBox(bone41, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone41.addChild(bone42);
				setRotationAngle(bone42, 0.0873F, 0.0F, 0.0873F);
				bone42.cubeList.add(new ModelBox(bone42, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone42.addChild(bone43);
				setRotationAngle(bone43, 0.0873F, 0.0F, 0.0873F);
				bone43.cubeList.add(new ModelBox(bone43, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, -1.0F, 0.0F);
				hump.addChild(bone11);
				setRotationAngle(bone11, 0.0F, -0.5236F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 15, 23, -2.5F, -0.5F, -2.5F, 5, 1, 5, 0.0F, false));
		
				spike5 = new ModelRenderer(this);
				spike5.setRotationPoint(2.1F, 0.05F, -2.0F);
				bone11.addChild(spike5);
				setRotationAngle(spike5, 1.5708F, -0.8727F, -0.8727F);
				spike5.cubeList.add(new ModelBox(spike5, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike5.addChild(bone44);
				setRotationAngle(bone44, -0.0873F, 0.0F, 0.0873F);
				bone44.cubeList.add(new ModelBox(bone44, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone44.addChild(bone45);
				setRotationAngle(bone45, -0.0873F, 0.0F, 0.0873F);
				bone45.cubeList.add(new ModelBox(bone45, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone45.addChild(bone46);
				setRotationAngle(bone46, -0.0873F, 0.0F, 0.0873F);
				bone46.cubeList.add(new ModelBox(bone46, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				spike6 = new ModelRenderer(this);
				spike6.setRotationPoint(-2.1F, 0.05F, -2.0F);
				bone11.addChild(spike6);
				setRotationAngle(spike6, 1.5708F, 0.8727F, 0.8727F);
				spike6.cubeList.add(new ModelBox(spike6, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike6.addChild(bone47);
				setRotationAngle(bone47, -0.0873F, 0.0F, -0.0873F);
				bone47.cubeList.add(new ModelBox(bone47, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone47.addChild(bone48);
				setRotationAngle(bone48, -0.0873F, 0.0F, -0.0873F);
				bone48.cubeList.add(new ModelBox(bone48, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone48.addChild(bone49);
				setRotationAngle(bone49, -0.0873F, 0.0F, -0.0873F);
				bone49.cubeList.add(new ModelBox(bone49, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike7 = new ModelRenderer(this);
				spike7.setRotationPoint(-2.1F, 0.05F, 2.0F);
				bone11.addChild(spike7);
				setRotationAngle(spike7, -1.5708F, -0.8727F, 0.8727F);
				spike7.cubeList.add(new ModelBox(spike7, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike7.addChild(bone50);
				setRotationAngle(bone50, 0.0873F, 0.0F, -0.0873F);
				bone50.cubeList.add(new ModelBox(bone50, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone50.addChild(bone51);
				setRotationAngle(bone51, 0.0873F, 0.0F, -0.0873F);
				bone51.cubeList.add(new ModelBox(bone51, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone52 = new ModelRenderer(this);
				bone52.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone51.addChild(bone52);
				setRotationAngle(bone52, 0.0873F, 0.0F, -0.0873F);
				bone52.cubeList.add(new ModelBox(bone52, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike8 = new ModelRenderer(this);
				spike8.setRotationPoint(2.1F, 0.05F, 2.0F);
				bone11.addChild(spike8);
				setRotationAngle(spike8, -1.5708F, 0.8727F, -0.8727F);
				spike8.cubeList.add(new ModelBox(spike8, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone53 = new ModelRenderer(this);
				bone53.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike8.addChild(bone53);
				setRotationAngle(bone53, 0.0873F, 0.0F, 0.0873F);
				bone53.cubeList.add(new ModelBox(bone53, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone54 = new ModelRenderer(this);
				bone54.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone53.addChild(bone54);
				setRotationAngle(bone54, 0.0873F, 0.0F, 0.0873F);
				bone54.cubeList.add(new ModelBox(bone54, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone55 = new ModelRenderer(this);
				bone55.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone54.addChild(bone55);
				setRotationAngle(bone55, 0.0873F, 0.0F, 0.0873F);
				bone55.cubeList.add(new ModelBox(bone55, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, 0.0F, -0.5236F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 28, 7, -2.0F, -0.5F, -2.0F, 4, 1, 4, 0.0F, false));
		
				spike9 = new ModelRenderer(this);
				spike9.setRotationPoint(1.6F, 0.05F, -1.5F);
				bone12.addChild(spike9);
				setRotationAngle(spike9, 1.5708F, -0.8727F, -0.8727F);
				spike9.cubeList.add(new ModelBox(spike9, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone56 = new ModelRenderer(this);
				bone56.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike9.addChild(bone56);
				setRotationAngle(bone56, -0.0873F, 0.0F, 0.0873F);
				bone56.cubeList.add(new ModelBox(bone56, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone57 = new ModelRenderer(this);
				bone57.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone56.addChild(bone57);
				setRotationAngle(bone57, -0.0873F, 0.0F, 0.0873F);
				bone57.cubeList.add(new ModelBox(bone57, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone58 = new ModelRenderer(this);
				bone58.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone57.addChild(bone58);
				setRotationAngle(bone58, -0.0873F, 0.0F, 0.0873F);
				bone58.cubeList.add(new ModelBox(bone58, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				spike10 = new ModelRenderer(this);
				spike10.setRotationPoint(-1.6F, 0.05F, -1.5F);
				bone12.addChild(spike10);
				setRotationAngle(spike10, 1.5708F, 0.8727F, 0.8727F);
				spike10.cubeList.add(new ModelBox(spike10, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone59 = new ModelRenderer(this);
				bone59.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike10.addChild(bone59);
				setRotationAngle(bone59, -0.0873F, 0.0F, -0.0873F);
				bone59.cubeList.add(new ModelBox(bone59, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone60 = new ModelRenderer(this);
				bone60.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone59.addChild(bone60);
				setRotationAngle(bone60, -0.0873F, 0.0F, -0.0873F);
				bone60.cubeList.add(new ModelBox(bone60, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone61 = new ModelRenderer(this);
				bone61.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone60.addChild(bone61);
				setRotationAngle(bone61, -0.0873F, 0.0F, -0.0873F);
				bone61.cubeList.add(new ModelBox(bone61, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike11 = new ModelRenderer(this);
				spike11.setRotationPoint(-1.6F, 0.05F, 1.5F);
				bone12.addChild(spike11);
				setRotationAngle(spike11, -1.5708F, -0.8727F, 0.8727F);
				spike11.cubeList.add(new ModelBox(spike11, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone62 = new ModelRenderer(this);
				bone62.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike11.addChild(bone62);
				setRotationAngle(bone62, 0.0873F, 0.0F, -0.0873F);
				bone62.cubeList.add(new ModelBox(bone62, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone63 = new ModelRenderer(this);
				bone63.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone62.addChild(bone63);
				setRotationAngle(bone63, 0.0873F, 0.0F, -0.0873F);
				bone63.cubeList.add(new ModelBox(bone63, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone64 = new ModelRenderer(this);
				bone64.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone63.addChild(bone64);
				setRotationAngle(bone64, 0.0873F, 0.0F, -0.0873F);
				bone64.cubeList.add(new ModelBox(bone64, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike12 = new ModelRenderer(this);
				spike12.setRotationPoint(1.6F, 0.05F, 1.5F);
				bone12.addChild(spike12);
				setRotationAngle(spike12, -1.5708F, 0.8727F, -0.8727F);
				spike12.cubeList.add(new ModelBox(spike12, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone65 = new ModelRenderer(this);
				bone65.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike12.addChild(bone65);
				setRotationAngle(bone65, 0.0873F, 0.0F, 0.0873F);
				bone65.cubeList.add(new ModelBox(bone65, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone66 = new ModelRenderer(this);
				bone66.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone65.addChild(bone66);
				setRotationAngle(bone66, 0.0873F, 0.0F, 0.0873F);
				bone66.cubeList.add(new ModelBox(bone66, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone67 = new ModelRenderer(this);
				bone67.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone66.addChild(bone67);
				setRotationAngle(bone67, 0.0873F, 0.0F, 0.0873F);
				bone67.cubeList.add(new ModelBox(bone67, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone12.addChild(bone13);
				setRotationAngle(bone13, 0.0F, -0.5236F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 12, 13, -1.5F, -0.5F, -1.5F, 3, 1, 3, 0.0F, false));
		
				spike13 = new ModelRenderer(this);
				spike13.setRotationPoint(0.85F, 0.3F, -0.75F);
				bone13.addChild(spike13);
				setRotationAngle(spike13, 1.5708F, -0.8727F, -0.8727F);
				spike13.cubeList.add(new ModelBox(spike13, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone68 = new ModelRenderer(this);
				bone68.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike13.addChild(bone68);
				setRotationAngle(bone68, -0.0873F, 0.0F, 0.0873F);
				bone68.cubeList.add(new ModelBox(bone68, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone69 = new ModelRenderer(this);
				bone69.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone68.addChild(bone69);
				setRotationAngle(bone69, -0.0873F, 0.0F, 0.0873F);
				bone69.cubeList.add(new ModelBox(bone69, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone70 = new ModelRenderer(this);
				bone70.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone69.addChild(bone70);
				setRotationAngle(bone70, -0.0873F, 0.0F, 0.0873F);
				bone70.cubeList.add(new ModelBox(bone70, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				spike14 = new ModelRenderer(this);
				spike14.setRotationPoint(-0.85F, 0.3F, -0.75F);
				bone13.addChild(spike14);
				setRotationAngle(spike14, 1.5708F, 0.8727F, 0.8727F);
				spike14.cubeList.add(new ModelBox(spike14, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone71 = new ModelRenderer(this);
				bone71.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike14.addChild(bone71);
				setRotationAngle(bone71, -0.0873F, 0.0F, -0.0873F);
				bone71.cubeList.add(new ModelBox(bone71, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone72 = new ModelRenderer(this);
				bone72.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone71.addChild(bone72);
				setRotationAngle(bone72, -0.0873F, 0.0F, -0.0873F);
				bone72.cubeList.add(new ModelBox(bone72, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone73 = new ModelRenderer(this);
				bone73.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone72.addChild(bone73);
				setRotationAngle(bone73, -0.0873F, 0.0F, -0.0873F);
				bone73.cubeList.add(new ModelBox(bone73, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike15 = new ModelRenderer(this);
				spike15.setRotationPoint(-0.85F, 0.3F, 0.75F);
				bone13.addChild(spike15);
				setRotationAngle(spike15, -1.5708F, -0.8727F, 0.8727F);
				spike15.cubeList.add(new ModelBox(spike15, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
		
				bone74 = new ModelRenderer(this);
				bone74.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike15.addChild(bone74);
				setRotationAngle(bone74, 0.0873F, 0.0F, -0.0873F);
				bone74.cubeList.add(new ModelBox(bone74, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
		
				bone75 = new ModelRenderer(this);
				bone75.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone74.addChild(bone75);
				setRotationAngle(bone75, 0.0873F, 0.0F, -0.0873F);
				bone75.cubeList.add(new ModelBox(bone75, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, true));
		
				bone76 = new ModelRenderer(this);
				bone76.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone75.addChild(bone76);
				setRotationAngle(bone76, 0.0873F, 0.0F, -0.0873F);
				bone76.cubeList.add(new ModelBox(bone76, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, true));
		
				spike16 = new ModelRenderer(this);
				spike16.setRotationPoint(0.85F, 0.3F, 0.75F);
				bone13.addChild(spike16);
				setRotationAngle(spike16, -1.5708F, 0.8727F, -0.8727F);
				spike16.cubeList.add(new ModelBox(spike16, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone77 = new ModelRenderer(this);
				bone77.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike16.addChild(bone77);
				setRotationAngle(bone77, 0.0873F, 0.0F, 0.0873F);
				bone77.cubeList.add(new ModelBox(bone77, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone78 = new ModelRenderer(this);
				bone78.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone77.addChild(bone78);
				setRotationAngle(bone78, 0.0873F, 0.0F, 0.0873F);
				bone78.cubeList.add(new ModelBox(bone78, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone79 = new ModelRenderer(this);
				bone79.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone78.addChild(bone79);
				setRotationAngle(bone79, 0.0873F, 0.0F, 0.0873F);
				bone79.cubeList.add(new ModelBox(bone79, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone13.addChild(bone14);
				setRotationAngle(bone14, 0.0F, -0.5236F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 12, 17, -1.0F, -0.5F, -1.0F, 2, 1, 2, 0.0F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone14.addChild(bone16);
				setRotationAngle(bone16, 0.0F, -0.5236F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 0, 19, -0.5F, -0.5F, -0.5F, 1, 1, 1, 0.1F, false));
		
				spike17 = new ModelRenderer(this);
				spike17.setRotationPoint(0.0F, 0.05F, 0.0F);
				bone16.addChild(spike17);
				setRotationAngle(spike17, 0.0F, -0.7854F, 0.0F);
				spike17.cubeList.add(new ModelBox(spike17, 21, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
		
				bone80 = new ModelRenderer(this);
				bone80.setRotationPoint(0.0F, -0.5F, 0.0F);
				spike17.addChild(bone80);
				setRotationAngle(bone80, -0.0873F, 0.0F, 0.0873F);
				bone80.cubeList.add(new ModelBox(bone80, 21, 2, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
		
				bone81 = new ModelRenderer(this);
				bone81.setRotationPoint(0.0F, -0.5F, 0.0F);
				bone80.addChild(bone81);
				setRotationAngle(bone81, -0.0873F, 0.0F, 0.0873F);
				bone81.cubeList.add(new ModelBox(bone81, 21, 0, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.3F, false));
		
				bone82 = new ModelRenderer(this);
				bone82.setRotationPoint(0.0F, -0.25F, 0.0F);
				bone81.addChild(bone82);
				setRotationAngle(bone82, -0.0873F, 0.0F, 0.0873F);
				bone82.cubeList.add(new ModelBox(bone82, 4, 19, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.4F, false));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, -2.5F, 4.0F);
				bone83.addChild(bone7);
				setRotationAngle(bone7, -0.3491F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 20, 13, -3.5F, 0.0F, 0.0F, 7, 6, 4, 0.0F, false));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(-3.5F, 0.5F, -2.7F);
				bone83.addChild(bone9);
				setRotationAngle(bone9, 0.0F, 0.2618F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 0, 13, -2.0F, -2.5F, 0.0F, 2, 5, 8, 0.0F, false));
		
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(3.5F, 0.5F, -2.7F);
				bone83.addChild(bone34);
				setRotationAngle(bone34, 0.0F, -0.2618F, 0.0F);
				bone34.cubeList.add(new ModelBox(bone34, 0, 13, 0.0F, -2.5F, 0.0F, 2, 5, 8, 0.0F, true));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.5F, -5.0F, -4.5F);
				bipedBody.addChild(bipedRightArm);
				
		
				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(1.0F, -1.0F, -1.0F);
				bipedRightArm.addChild(cube_r17);
				setRotationAngle(cube_r17, 0.0F, 0.48F, 1.0472F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 0, 32, -1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F, false));
		
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(-1.5F, 6.0F, 0.0F);
				cube_r17.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.0F, 0.0F, -1.0472F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 12, 36, 0.0F, 0.0F, -1.5F, 3, 6, 3, 0.0F, false));
		
				rightHand = new ModelRenderer(this);
				rightHand.setRotationPoint(1.7F, 5.5F, 0.0F);
				cube_r16.addChild(rightHand);
				setRotationAngle(rightHand, 0.4363F, 0.0F, 0.0F);
				rightHand.cubeList.add(new ModelBox(rightHand, 36, 36, -1.6F, 0.0F, -3.0F, 3, 1, 4, 0.0F, false));
				rightHand.cubeList.add(new ModelBox(rightHand, 30, 23, -1.6F, 0.5F, -3.0F, 3, 1, 4, 0.0F, false));
		
				bone143 = new ModelRenderer(this);
				bone143.setRotationPoint(1.1F, 0.6F, -1.75F);
				rightHand.addChild(bone143);
				bone143.cubeList.add(new ModelBox(bone143, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone144 = new ModelRenderer(this);
				bone144.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone143.addChild(bone144);
				setRotationAngle(bone144, 0.5236F, 0.0F, 0.0F);
				bone144.cubeList.add(new ModelBox(bone144, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(1.1F, 0.6F, -0.75F);
				rightHand.addChild(bone18);
				setRotationAngle(bone18, 0.0F, -1.0472F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone18.addChild(bone19);
				setRotationAngle(bone19, 0.5236F, 0.0F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-0.05F, 0.6F, -1.75F);
				rightHand.addChild(bone2);
				bone2.cubeList.add(new ModelBox(bone2, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone2.addChild(bone6);
				setRotationAngle(bone6, 0.5236F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(-1.2F, 0.6F, -1.75F);
				rightHand.addChild(bone15);
				bone15.cubeList.add(new ModelBox(bone15, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone15.addChild(bone17);
				setRotationAngle(bone17, 0.5236F, 0.0F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(-1.2F, 0.6F, -1.0F);
				rightHand.addChild(bone20);
				setRotationAngle(bone20, 0.0F, 0.4363F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
		
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone20.addChild(bone21);
				setRotationAngle(bone21, 0.5236F, 0.0F, 0.0F);
				bone21.cubeList.add(new ModelBox(bone21, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.5F, -5.0F, -4.5F);
				bipedBody.addChild(bipedLeftArm);
				
		
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(-1.0F, -1.0F, -1.0F);
				bipedLeftArm.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.0F, -0.48F, -1.0472F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 32, -1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F, true));
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(1.5F, 6.0F, 0.0F);
				cube_r2.addChild(cube_r3);
				setRotationAngle(cube_r3, 0.0F, 0.0F, 1.0472F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 12, 36, -3.0F, 0.0F, -1.5F, 3, 6, 3, 0.0F, true));
		
				leftHand = new ModelRenderer(this);
				leftHand.setRotationPoint(-1.7F, 5.5F, 0.0F);
				cube_r3.addChild(leftHand);
				setRotationAngle(leftHand, 0.4363F, 0.0F, 0.0F);
				leftHand.cubeList.add(new ModelBox(leftHand, 36, 36, -1.4F, 0.0F, -3.0F, 3, 1, 4, 0.0F, true));
				leftHand.cubeList.add(new ModelBox(leftHand, 30, 23, -1.4F, 0.5F, -3.0F, 3, 1, 4, 0.0F, true));
		
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(-1.1F, 0.6F, -1.75F);
				leftHand.addChild(bone24);
				bone24.cubeList.add(new ModelBox(bone24, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone24.addChild(bone25);
				setRotationAngle(bone25, 0.5236F, 0.0F, 0.0F);
				bone25.cubeList.add(new ModelBox(bone25, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(-1.1F, 0.6F, -0.75F);
				leftHand.addChild(bone26);
				setRotationAngle(bone26, 0.0F, 1.0472F, 0.0F);
				bone26.cubeList.add(new ModelBox(bone26, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone26.addChild(bone27);
				setRotationAngle(bone27, 0.5236F, 0.0F, 0.0F);
				bone27.cubeList.add(new ModelBox(bone27, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.05F, 0.6F, -1.75F);
				leftHand.addChild(bone28);
				bone28.cubeList.add(new ModelBox(bone28, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone28.addChild(bone29);
				setRotationAngle(bone29, 0.5236F, 0.0F, 0.0F);
				bone29.cubeList.add(new ModelBox(bone29, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(1.2F, 0.6F, -1.75F);
				leftHand.addChild(bone30);
				bone30.cubeList.add(new ModelBox(bone30, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone30.addChild(bone31);
				setRotationAngle(bone31, 0.5236F, 0.0F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(1.2F, 0.6F, -1.0F);
				leftHand.addChild(bone32);
				setRotationAngle(bone32, 0.0F, -0.4363F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 0, 3, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
		
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone32.addChild(bone33);
				setRotationAngle(bone33, 0.5236F, 0.0F, 0.0F);
				bone33.cubeList.add(new ModelBox(bone33, 0, 0, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
		
				Tail[0][0] = new ModelRenderer(this);
				Tail[0][0].setRotationPoint(-0.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[0][0], -1.1345F, -0.2618F, 0.0F);
				Tail[0][0].cubeList.add(new ModelBox(Tail[0][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[0][1] = new ModelRenderer(this);
				Tail[0][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][0].addChild(Tail[0][1]);
				setRotationAngle(Tail[0][1], 0.1745F, 0.0F, 0.0F);
				Tail[0][1].cubeList.add(new ModelBox(Tail[0][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[0][2] = new ModelRenderer(this);
				Tail[0][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][1].addChild(Tail[0][2]);
				setRotationAngle(Tail[0][2], 0.1745F, 0.0F, 0.0F);
				Tail[0][2].cubeList.add(new ModelBox(Tail[0][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[0][3] = new ModelRenderer(this);
				Tail[0][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][2].addChild(Tail[0][3]);
				setRotationAngle(Tail[0][3], 0.1745F, 0.0F, 0.0F);
				Tail[0][3].cubeList.add(new ModelBox(Tail[0][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[0][4] = new ModelRenderer(this);
				Tail[0][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][3].addChild(Tail[0][4]);
				setRotationAngle(Tail[0][4], 0.1745F, 0.0F, 0.0F);
				Tail[0][4].cubeList.add(new ModelBox(Tail[0][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[0][5] = new ModelRenderer(this);
				Tail[0][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][4].addChild(Tail[0][5]);
				setRotationAngle(Tail[0][5], 0.1745F, 0.0F, 0.0F);
				Tail[0][5].cubeList.add(new ModelBox(Tail[0][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[0][6] = new ModelRenderer(this);
				Tail[0][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][5].addChild(Tail[0][6]);
				setRotationAngle(Tail[0][6], 0.1745F, 0.0F, 0.0F);
				Tail[0][6].cubeList.add(new ModelBox(Tail[0][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[0][7] = new ModelRenderer(this);
				Tail[0][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][6].addChild(Tail[0][7]);
				setRotationAngle(Tail[0][7], -0.1745F, 0.0F, 0.0F);
				Tail[0][7].cubeList.add(new ModelBox(Tail[0][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[0][8] = new ModelRenderer(this);
				Tail[0][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][7].addChild(Tail[0][8]);
				setRotationAngle(Tail[0][8], -0.1745F, 0.0F, 0.0F);
				Tail[0][8].cubeList.add(new ModelBox(Tail[0][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[0][9] = new ModelRenderer(this);
				Tail[0][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[0][8].addChild(Tail[0][9]);
				setRotationAngle(Tail[0][9], -0.1745F, 0.0F, 0.0F);
				Tail[0][9].cubeList.add(new ModelBox(Tail[0][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[1][0] = new ModelRenderer(this);
				Tail[1][0].setRotationPoint(0.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[1][0], -1.309F, 0.2618F, 0.0F);
				Tail[1][0].cubeList.add(new ModelBox(Tail[1][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[1][1] = new ModelRenderer(this);
				Tail[1][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][0].addChild(Tail[1][1]);
				setRotationAngle(Tail[1][1], 0.2618F, 0.0F, 0.0F);
				Tail[1][1].cubeList.add(new ModelBox(Tail[1][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[1][2] = new ModelRenderer(this);
				Tail[1][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][1].addChild(Tail[1][2]);
				setRotationAngle(Tail[1][2], 0.2618F, 0.0F, 0.0F);
				Tail[1][2].cubeList.add(new ModelBox(Tail[1][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[1][3] = new ModelRenderer(this);
				Tail[1][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][2].addChild(Tail[1][3]);
				setRotationAngle(Tail[1][3], 0.2618F, 0.0F, 0.0F);
				Tail[1][3].cubeList.add(new ModelBox(Tail[1][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[1][4] = new ModelRenderer(this);
				Tail[1][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][3].addChild(Tail[1][4]);
				setRotationAngle(Tail[1][4], 0.2618F, 0.0F, 0.0F);
				Tail[1][4].cubeList.add(new ModelBox(Tail[1][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[1][5] = new ModelRenderer(this);
				Tail[1][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][4].addChild(Tail[1][5]);
				setRotationAngle(Tail[1][5], 0.2618F, 0.0F, 0.0F);
				Tail[1][5].cubeList.add(new ModelBox(Tail[1][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[1][6] = new ModelRenderer(this);
				Tail[1][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][5].addChild(Tail[1][6]);
				setRotationAngle(Tail[1][6], 0.2618F, 0.0F, 0.0F);
				Tail[1][6].cubeList.add(new ModelBox(Tail[1][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[1][7] = new ModelRenderer(this);
				Tail[1][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][6].addChild(Tail[1][7]);
				setRotationAngle(Tail[1][7], 0.2618F, 0.0F, 0.0F);
				Tail[1][7].cubeList.add(new ModelBox(Tail[1][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[1][8] = new ModelRenderer(this);
				Tail[1][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][7].addChild(Tail[1][8]);
				setRotationAngle(Tail[1][8], -0.1745F, 0.0F, 0.0F);
				Tail[1][8].cubeList.add(new ModelBox(Tail[1][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[1][9] = new ModelRenderer(this);
				Tail[1][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[1][8].addChild(Tail[1][9]);
				setRotationAngle(Tail[1][9], -0.1745F, 0.0F, 0.0F);
				Tail[1][9].cubeList.add(new ModelBox(Tail[1][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[2][0] = new ModelRenderer(this);
				Tail[2][0].setRotationPoint(0.75F, 21.5F, 5.0F);
				setRotationAngle(Tail[2][0], -1.1345F, 0.7854F, 0.0F);
				Tail[2][0].cubeList.add(new ModelBox(Tail[2][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[2][1] = new ModelRenderer(this);
				Tail[2][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][0].addChild(Tail[2][1]);
				setRotationAngle(Tail[2][1], 0.2618F, 0.0F, 0.0F);
				Tail[2][1].cubeList.add(new ModelBox(Tail[2][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[2][2] = new ModelRenderer(this);
				Tail[2][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][1].addChild(Tail[2][2]);
				setRotationAngle(Tail[2][2], 0.2618F, 0.0F, 0.0F);
				Tail[2][2].cubeList.add(new ModelBox(Tail[2][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[2][3] = new ModelRenderer(this);
				Tail[2][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][2].addChild(Tail[2][3]);
				setRotationAngle(Tail[2][3], 0.2618F, 0.0F, 0.0F);
				Tail[2][3].cubeList.add(new ModelBox(Tail[2][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[2][4] = new ModelRenderer(this);
				Tail[2][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][3].addChild(Tail[2][4]);
				setRotationAngle(Tail[2][4], 0.2618F, 0.0F, 0.0F);
				Tail[2][4].cubeList.add(new ModelBox(Tail[2][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[2][5] = new ModelRenderer(this);
				Tail[2][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][4].addChild(Tail[2][5]);
				setRotationAngle(Tail[2][5], 0.2618F, 0.0F, 0.0F);
				Tail[2][5].cubeList.add(new ModelBox(Tail[2][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[2][6] = new ModelRenderer(this);
				Tail[2][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][5].addChild(Tail[2][6]);
				setRotationAngle(Tail[2][6], 0.2618F, 0.0F, 0.0F);
				Tail[2][6].cubeList.add(new ModelBox(Tail[2][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[2][7] = new ModelRenderer(this);
				Tail[2][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][6].addChild(Tail[2][7]);
				setRotationAngle(Tail[2][7], 0.2618F, 0.0F, 0.0F);
				Tail[2][7].cubeList.add(new ModelBox(Tail[2][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[2][8] = new ModelRenderer(this);
				Tail[2][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][7].addChild(Tail[2][8]);
				setRotationAngle(Tail[2][8], -0.1745F, 0.0F, 0.0F);
				Tail[2][8].cubeList.add(new ModelBox(Tail[2][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[2][9] = new ModelRenderer(this);
				Tail[2][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[2][8].addChild(Tail[2][9]);
				setRotationAngle(Tail[2][9], -0.1745F, 0.0F, 0.0F);
				Tail[2][9].cubeList.add(new ModelBox(Tail[2][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[3][0] = new ModelRenderer(this);
				Tail[3][0].setRotationPoint(1.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[3][0], -1.3963F, 1.0472F, 0.0F);
				Tail[3][0].cubeList.add(new ModelBox(Tail[3][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[3][1] = new ModelRenderer(this);
				Tail[3][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][0].addChild(Tail[3][1]);
				setRotationAngle(Tail[3][1], 0.2618F, 0.0F, 0.0F);
				Tail[3][1].cubeList.add(new ModelBox(Tail[3][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[3][2] = new ModelRenderer(this);
				Tail[3][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][1].addChild(Tail[3][2]);
				setRotationAngle(Tail[3][2], 0.2618F, 0.0F, 0.0F);
				Tail[3][2].cubeList.add(new ModelBox(Tail[3][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[3][3] = new ModelRenderer(this);
				Tail[3][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][2].addChild(Tail[3][3]);
				setRotationAngle(Tail[3][3], 0.2618F, 0.0F, 0.0F);
				Tail[3][3].cubeList.add(new ModelBox(Tail[3][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[3][4] = new ModelRenderer(this);
				Tail[3][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][3].addChild(Tail[3][4]);
				setRotationAngle(Tail[3][4], 0.2618F, 0.0F, 0.0F);
				Tail[3][4].cubeList.add(new ModelBox(Tail[3][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[3][5] = new ModelRenderer(this);
				Tail[3][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][4].addChild(Tail[3][5]);
				setRotationAngle(Tail[3][5], 0.2618F, 0.0F, 0.0F);
				Tail[3][5].cubeList.add(new ModelBox(Tail[3][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[3][6] = new ModelRenderer(this);
				Tail[3][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][5].addChild(Tail[3][6]);
				setRotationAngle(Tail[3][6], 0.2618F, 0.0F, 0.0F);
				Tail[3][6].cubeList.add(new ModelBox(Tail[3][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[3][7] = new ModelRenderer(this);
				Tail[3][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][6].addChild(Tail[3][7]);
				setRotationAngle(Tail[3][7], 0.2618F, 0.0F, 0.0F);
				Tail[3][7].cubeList.add(new ModelBox(Tail[3][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[3][8] = new ModelRenderer(this);
				Tail[3][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][7].addChild(Tail[3][8]);
				setRotationAngle(Tail[3][8], -0.1745F, 0.0F, 0.0F);
				Tail[3][8].cubeList.add(new ModelBox(Tail[3][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[3][9] = new ModelRenderer(this);
				Tail[3][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[3][8].addChild(Tail[3][9]);
				setRotationAngle(Tail[3][9], -0.1745F, 0.0F, 0.0F);
				Tail[3][9].cubeList.add(new ModelBox(Tail[3][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[4][0] = new ModelRenderer(this);
				Tail[4][0].setRotationPoint(1.75F, 21.5F, 5.0F);
				setRotationAngle(Tail[4][0], -1.6581F, 1.309F, 0.0F);
				Tail[4][0].cubeList.add(new ModelBox(Tail[4][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[4][1] = new ModelRenderer(this);
				Tail[4][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][0].addChild(Tail[4][1]);
				setRotationAngle(Tail[4][1], 0.2618F, 0.0F, 0.0F);
				Tail[4][1].cubeList.add(new ModelBox(Tail[4][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[4][2] = new ModelRenderer(this);
				Tail[4][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][1].addChild(Tail[4][2]);
				setRotationAngle(Tail[4][2], 0.2618F, 0.0F, 0.0F);
				Tail[4][2].cubeList.add(new ModelBox(Tail[4][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[4][3] = new ModelRenderer(this);
				Tail[4][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][2].addChild(Tail[4][3]);
				setRotationAngle(Tail[4][3], 0.2618F, 0.0F, 0.0F);
				Tail[4][3].cubeList.add(new ModelBox(Tail[4][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[4][4] = new ModelRenderer(this);
				Tail[4][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][3].addChild(Tail[4][4]);
				setRotationAngle(Tail[4][4], 0.2618F, 0.0F, 0.0F);
				Tail[4][4].cubeList.add(new ModelBox(Tail[4][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[4][5] = new ModelRenderer(this);
				Tail[4][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][4].addChild(Tail[4][5]);
				setRotationAngle(Tail[4][5], 0.2618F, 0.0F, 0.0F);
				Tail[4][5].cubeList.add(new ModelBox(Tail[4][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[4][6] = new ModelRenderer(this);
				Tail[4][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][5].addChild(Tail[4][6]);
				setRotationAngle(Tail[4][6], 0.2618F, 0.0F, 0.0F);
				Tail[4][6].cubeList.add(new ModelBox(Tail[4][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[4][7] = new ModelRenderer(this);
				Tail[4][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][6].addChild(Tail[4][7]);
				setRotationAngle(Tail[4][7], 0.2618F, 0.0F, 0.0F);
				Tail[4][7].cubeList.add(new ModelBox(Tail[4][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[4][8] = new ModelRenderer(this);
				Tail[4][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][7].addChild(Tail[4][8]);
				setRotationAngle(Tail[4][8], -0.1745F, 0.0F, 0.0F);
				Tail[4][8].cubeList.add(new ModelBox(Tail[4][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[4][9] = new ModelRenderer(this);
				Tail[4][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[4][8].addChild(Tail[4][9]);
				setRotationAngle(Tail[4][9], -0.1745F, 0.0F, 0.0F);
				Tail[4][9].cubeList.add(new ModelBox(Tail[4][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[5][0] = new ModelRenderer(this);
				Tail[5][0].setRotationPoint(2.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[5][0], -1.9199F, 1.5708F, 0.0F);
				Tail[5][0].cubeList.add(new ModelBox(Tail[5][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[5][1] = new ModelRenderer(this);
				Tail[5][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][0].addChild(Tail[5][1]);
				setRotationAngle(Tail[5][1], 0.2618F, 0.0F, 0.0F);
				Tail[5][1].cubeList.add(new ModelBox(Tail[5][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[5][2] = new ModelRenderer(this);
				Tail[5][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][1].addChild(Tail[5][2]);
				setRotationAngle(Tail[5][2], 0.2618F, 0.0F, 0.0F);
				Tail[5][2].cubeList.add(new ModelBox(Tail[5][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[5][3] = new ModelRenderer(this);
				Tail[5][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][2].addChild(Tail[5][3]);
				setRotationAngle(Tail[5][3], 0.2618F, 0.0F, 0.0F);
				Tail[5][3].cubeList.add(new ModelBox(Tail[5][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[5][4] = new ModelRenderer(this);
				Tail[5][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][3].addChild(Tail[5][4]);
				setRotationAngle(Tail[5][4], 0.2618F, 0.0F, 0.0F);
				Tail[5][4].cubeList.add(new ModelBox(Tail[5][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[5][5] = new ModelRenderer(this);
				Tail[5][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][4].addChild(Tail[5][5]);
				setRotationAngle(Tail[5][5], 0.2618F, 0.0F, 0.0F);
				Tail[5][5].cubeList.add(new ModelBox(Tail[5][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[5][6] = new ModelRenderer(this);
				Tail[5][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][5].addChild(Tail[5][6]);
				setRotationAngle(Tail[5][6], 0.2618F, 0.0F, 0.0F);
				Tail[5][6].cubeList.add(new ModelBox(Tail[5][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[5][7] = new ModelRenderer(this);
				Tail[5][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][6].addChild(Tail[5][7]);
				setRotationAngle(Tail[5][7], 0.2618F, 0.0F, 0.0F);
				Tail[5][7].cubeList.add(new ModelBox(Tail[5][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[5][8] = new ModelRenderer(this);
				Tail[5][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][7].addChild(Tail[5][8]);
				setRotationAngle(Tail[5][8], -0.1745F, 0.0F, 0.0F);
				Tail[5][8].cubeList.add(new ModelBox(Tail[5][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[5][9] = new ModelRenderer(this);
				Tail[5][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[5][8].addChild(Tail[5][9]);
				setRotationAngle(Tail[5][9], -0.1745F, 0.0F, 0.0F);
				Tail[5][9].cubeList.add(new ModelBox(Tail[5][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[6][0] = new ModelRenderer(this);
				Tail[6][0].setRotationPoint(-0.75F, 21.5F, 5.0F);
				setRotationAngle(Tail[6][0], -1.1345F, -0.7854F, 0.0F);
				Tail[6][0].cubeList.add(new ModelBox(Tail[6][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[6][1] = new ModelRenderer(this);
				Tail[6][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][0].addChild(Tail[6][1]);
				setRotationAngle(Tail[6][1], 0.2618F, 0.0F, 0.0F);
				Tail[6][1].cubeList.add(new ModelBox(Tail[6][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[6][2] = new ModelRenderer(this);
				Tail[6][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][1].addChild(Tail[6][2]);
				setRotationAngle(Tail[6][2], 0.2618F, 0.0F, 0.0F);
				Tail[6][2].cubeList.add(new ModelBox(Tail[6][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[6][3] = new ModelRenderer(this);
				Tail[6][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][2].addChild(Tail[6][3]);
				setRotationAngle(Tail[6][3], 0.2618F, 0.0F, 0.0F);
				Tail[6][3].cubeList.add(new ModelBox(Tail[6][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[6][4] = new ModelRenderer(this);
				Tail[6][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][3].addChild(Tail[6][4]);
				setRotationAngle(Tail[6][4], 0.2618F, 0.0F, 0.0F);
				Tail[6][4].cubeList.add(new ModelBox(Tail[6][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[6][5] = new ModelRenderer(this);
				Tail[6][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][4].addChild(Tail[6][5]);
				setRotationAngle(Tail[6][5], 0.2618F, 0.0F, 0.0F);
				Tail[6][5].cubeList.add(new ModelBox(Tail[6][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[6][6] = new ModelRenderer(this);
				Tail[6][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][5].addChild(Tail[6][6]);
				setRotationAngle(Tail[6][6], 0.2618F, 0.0F, 0.0F);
				Tail[6][6].cubeList.add(new ModelBox(Tail[6][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[6][7] = new ModelRenderer(this);
				Tail[6][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][6].addChild(Tail[6][7]);
				setRotationAngle(Tail[6][7], 0.2618F, 0.0F, 0.0F);
				Tail[6][7].cubeList.add(new ModelBox(Tail[6][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[6][8] = new ModelRenderer(this);
				Tail[6][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][7].addChild(Tail[6][8]);
				setRotationAngle(Tail[6][8], -0.1745F, 0.0F, 0.0F);
				Tail[6][8].cubeList.add(new ModelBox(Tail[6][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[6][9] = new ModelRenderer(this);
				Tail[6][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[6][8].addChild(Tail[6][9]);
				setRotationAngle(Tail[6][9], -0.1745F, 0.0F, 0.0F);
				Tail[6][9].cubeList.add(new ModelBox(Tail[6][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[7][0] = new ModelRenderer(this);
				Tail[7][0].setRotationPoint(-1.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[7][0], -1.3963F, -1.0472F, 0.0F);
				Tail[7][0].cubeList.add(new ModelBox(Tail[7][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[7][1] = new ModelRenderer(this);
				Tail[7][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][0].addChild(Tail[7][1]);
				setRotationAngle(Tail[7][1], 0.2618F, 0.0F, 0.0F);
				Tail[7][1].cubeList.add(new ModelBox(Tail[7][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[7][2] = new ModelRenderer(this);
				Tail[7][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][1].addChild(Tail[7][2]);
				setRotationAngle(Tail[7][2], 0.2618F, 0.0F, 0.0F);
				Tail[7][2].cubeList.add(new ModelBox(Tail[7][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[7][3] = new ModelRenderer(this);
				Tail[7][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][2].addChild(Tail[7][3]);
				setRotationAngle(Tail[7][3], 0.2618F, 0.0F, 0.0F);
				Tail[7][3].cubeList.add(new ModelBox(Tail[7][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[7][4] = new ModelRenderer(this);
				Tail[7][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][3].addChild(Tail[7][4]);
				setRotationAngle(Tail[7][4], 0.2618F, 0.0F, 0.0F);
				Tail[7][4].cubeList.add(new ModelBox(Tail[7][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[7][5] = new ModelRenderer(this);
				Tail[7][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][4].addChild(Tail[7][5]);
				setRotationAngle(Tail[7][5], 0.2618F, 0.0F, 0.0F);
				Tail[7][5].cubeList.add(new ModelBox(Tail[7][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[7][6] = new ModelRenderer(this);
				Tail[7][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][5].addChild(Tail[7][6]);
				setRotationAngle(Tail[7][6], 0.2618F, 0.0F, 0.0F);
				Tail[7][6].cubeList.add(new ModelBox(Tail[7][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[7][7] = new ModelRenderer(this);
				Tail[7][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][6].addChild(Tail[7][7]);
				setRotationAngle(Tail[7][7], 0.2618F, 0.0F, 0.0F);
				Tail[7][7].cubeList.add(new ModelBox(Tail[7][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[7][8] = new ModelRenderer(this);
				Tail[7][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][7].addChild(Tail[7][8]);
				setRotationAngle(Tail[7][8], -0.1745F, 0.0F, 0.0F);
				Tail[7][8].cubeList.add(new ModelBox(Tail[7][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[7][9] = new ModelRenderer(this);
				Tail[7][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[7][8].addChild(Tail[7][9]);
				setRotationAngle(Tail[7][9], -0.1745F, 0.0F, 0.0F);
				Tail[7][9].cubeList.add(new ModelBox(Tail[7][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[8][0] = new ModelRenderer(this);
				Tail[8][0].setRotationPoint(-1.75F, 21.5F, 5.0F);
				setRotationAngle(Tail[8][0], -1.6581F, -1.309F, 0.0F);
				Tail[8][0].cubeList.add(new ModelBox(Tail[8][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[8][1] = new ModelRenderer(this);
				Tail[8][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][0].addChild(Tail[8][1]);
				setRotationAngle(Tail[8][1], 0.2618F, 0.0F, 0.0F);
				Tail[8][1].cubeList.add(new ModelBox(Tail[8][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[8][2] = new ModelRenderer(this);
				Tail[8][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][1].addChild(Tail[8][2]);
				setRotationAngle(Tail[8][2], 0.2618F, 0.0F, 0.0F);
				Tail[8][2].cubeList.add(new ModelBox(Tail[8][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[8][3] = new ModelRenderer(this);
				Tail[8][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][2].addChild(Tail[8][3]);
				setRotationAngle(Tail[8][3], 0.2618F, 0.0F, 0.0F);
				Tail[8][3].cubeList.add(new ModelBox(Tail[8][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[8][4] = new ModelRenderer(this);
				Tail[8][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][3].addChild(Tail[8][4]);
				setRotationAngle(Tail[8][4], 0.2618F, 0.0F, 0.0F);
				Tail[8][4].cubeList.add(new ModelBox(Tail[8][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[8][5] = new ModelRenderer(this);
				Tail[8][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][4].addChild(Tail[8][5]);
				setRotationAngle(Tail[8][5], 0.2618F, 0.0F, 0.0F);
				Tail[8][5].cubeList.add(new ModelBox(Tail[8][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[8][6] = new ModelRenderer(this);
				Tail[8][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][5].addChild(Tail[8][6]);
				setRotationAngle(Tail[8][6], 0.2618F, 0.0F, 0.0F);
				Tail[8][6].cubeList.add(new ModelBox(Tail[8][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[8][7] = new ModelRenderer(this);
				Tail[8][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][6].addChild(Tail[8][7]);
				setRotationAngle(Tail[8][7], 0.2618F, 0.0F, 0.0F);
				Tail[8][7].cubeList.add(new ModelBox(Tail[8][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[8][8] = new ModelRenderer(this);
				Tail[8][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][7].addChild(Tail[8][8]);
				setRotationAngle(Tail[8][8], -0.1745F, 0.0F, 0.0F);
				Tail[8][8].cubeList.add(new ModelBox(Tail[8][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[8][9] = new ModelRenderer(this);
				Tail[8][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[8][8].addChild(Tail[8][9]);
				setRotationAngle(Tail[8][9], -0.1745F, 0.0F, 0.0F);
				Tail[8][9].cubeList.add(new ModelBox(Tail[8][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				Tail[9][0] = new ModelRenderer(this);
				Tail[9][0].setRotationPoint(-2.25F, 21.5F, 5.0F);
				setRotationAngle(Tail[9][0], -1.9199F, -1.5708F, 0.0F);
				Tail[9][0].cubeList.add(new ModelBox(Tail[9][0], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[9][1] = new ModelRenderer(this);
				Tail[9][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][0].addChild(Tail[9][1]);
				setRotationAngle(Tail[9][1], 0.2618F, 0.0F, 0.0F);
				Tail[9][1].cubeList.add(new ModelBox(Tail[9][1], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[9][2] = new ModelRenderer(this);
				Tail[9][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][1].addChild(Tail[9][2]);
				setRotationAngle(Tail[9][2], 0.2618F, 0.0F, 0.0F);
				Tail[9][2].cubeList.add(new ModelBox(Tail[9][2], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
		
				Tail[9][3] = new ModelRenderer(this);
				Tail[9][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][2].addChild(Tail[9][3]);
				setRotationAngle(Tail[9][3], 0.2618F, 0.0F, 0.0F);
				Tail[9][3].cubeList.add(new ModelBox(Tail[9][3], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[9][4] = new ModelRenderer(this);
				Tail[9][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][3].addChild(Tail[9][4]);
				setRotationAngle(Tail[9][4], 0.2618F, 0.0F, 0.0F);
				Tail[9][4].cubeList.add(new ModelBox(Tail[9][4], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[9][5] = new ModelRenderer(this);
				Tail[9][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][4].addChild(Tail[9][5]);
				setRotationAngle(Tail[9][5], 0.2618F, 0.0F, 0.0F);
				Tail[9][5].cubeList.add(new ModelBox(Tail[9][5], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[9][6] = new ModelRenderer(this);
				Tail[9][6].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][5].addChild(Tail[9][6]);
				setRotationAngle(Tail[9][6], 0.2618F, 0.0F, 0.0F);
				Tail[9][6].cubeList.add(new ModelBox(Tail[9][6], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
		
				Tail[9][7] = new ModelRenderer(this);
				Tail[9][7].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][6].addChild(Tail[9][7]);
				setRotationAngle(Tail[9][7], 0.2618F, 0.0F, 0.0F);
				Tail[9][7].cubeList.add(new ModelBox(Tail[9][7], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.3F, false));
		
				Tail[9][8] = new ModelRenderer(this);
				Tail[9][8].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][7].addChild(Tail[9][8]);
				setRotationAngle(Tail[9][8], -0.1745F, 0.0F, 0.0F);
				Tail[9][8].cubeList.add(new ModelBox(Tail[9][8], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
		
				Tail[9][9] = new ModelRenderer(this);
				Tail[9][9].setRotationPoint(0.0F, -3.0F, 0.0F);
				Tail[9][8].addChild(Tail[9][9]);
				setRotationAngle(Tail[9][9], -0.1745F, 0.0F, 0.0F);
				Tail[9][9].cubeList.add(new ModelBox(Tail[9][9], 0, 13, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
	
				for (int i = 0; i < 10; i++) {
					for (int j = 0; j < 10; j++) {
						tailSwayX[i][j] = (rand.nextFloat() * 0.1309F + 0.1309F) * (rand.nextBoolean() ? -1F : 1F);
						tailSwayZ[i][j] = (rand.nextFloat() * 0.1309F + 0.1309F) * (rand.nextBoolean() ? -1F : 1F);
					}
				}
			}
		
			@Override
			public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
				bipedLeftLeg.showModel = false;
				bipedRightLeg.showModel = false;
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F); //0.375F * MODELSCALE);
				GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
				//super.render(entity, f0, f1, f2, f3, f4, f5);
				bipedBody.render(f5);
				for (int i = 0; i < 10; i++) {
					Tail[i][0].render(f5);
				}
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				bipedHeadwear.render(f5);
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f0, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f0 * 2.0F / e.height, f1, f2, f3, f4, f5, e);
				bipedHead.rotationPointY += -5.5F;
				bipedRightArm.setRotationPoint(-5.5F, -5.0F, -4.5F);
				bipedLeftArm.setRotationPoint(5.5F, -5.0F, -4.5F);
				for (int i = 0; i < 10; i++) {
					for (int j = 1; j < 10; j++) {
						Tail[i][j].rotateAngleX = (j < 8 ? 0.2618F : -0.1745F) + MathHelper.sin((f2 - j) * 0.1F) * tailSwayX[i][j];
						Tail[i][j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.1F) * tailSwayZ[i][j];
					}
				}
				if (((EntityCustom)e).isShooting()) {
					bipedHead.rotateAngleX -= 0.3491F;
					jaw.rotateAngleX = 0.5236F;
				} else {
					jaw.rotateAngleX = -0.3491F;
				}
				this.copyModelAngles(bipedBody, bipedHeadwear);
				this.copyModelAngles(bipedHead, eyes);
			}
		}
	}
}
