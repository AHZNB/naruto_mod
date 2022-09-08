
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
//import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

//import net.narutomod.item.ItemBijuCloak;
//import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class EntityNineTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 249;
	public static final int ENTITYID_RANGED = 250;
	private static final float MODELSCALE = 10.0F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntityNineTails(ElementsNarutomodMod instance) {
		super(instance, 576);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "nine_tails"), ENTITYID).name("nine_tails")
				.tracker(96, 3, true).egg(-39424, -13421773).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
	}

	public static TailBeastManager getBijuManager() {
		return tailBeastManager;
	}

	public static class TailBeastManager extends EntityBijuManager<EntityCustom> {
		public TailBeastManager() {
			super(EntityCustom.class, 9);
		}

		/*@Override
		public void toggleBijuCloak() {
			super.toggleBijuCloak();
			EntityPlayer jinchuriki = this.getJinchurikiPlayer();
			if (this.getCloakLevel() > 0) {
				if (jinchuriki.inventory.armorInventory.get(3).getItem() != ItemBijuCloak.helmet) {
					ProcedureUtils.swapItemToSlot(jinchuriki, EntityEquipmentSlot.HEAD, new ItemStack(ItemBijuCloak.helmet));
				}
				if (jinchuriki.inventory.armorInventory.get(2).getItem() != ItemBijuCloak.body) {
					ProcedureUtils.swapItemToSlot(jinchuriki, EntityEquipmentSlot.CHEST, new ItemStack(ItemBijuCloak.body, 1, 9));
				}
				if (jinchuriki.inventory.armorInventory.get(1).getItem() != ItemBijuCloak.legs) {
					ProcedureUtils.swapItemToSlot(jinchuriki, EntityEquipmentSlot.LEGS, new ItemStack(ItemBijuCloak.legs));
				}
			} else if (jinchuriki != null) {
				jinchuriki.inventory.clearMatchingItems(ItemBijuCloak.helmet, -1, -1, null);
				jinchuriki.inventory.clearMatchingItems(ItemBijuCloak.body, -1, -1, null);
				jinchuriki.inventory.clearMatchingItems(ItemBijuCloak.legs, -1, -1, null);
				EntityCustom entity = this.getEntityInWorld(jinchuriki.world);
				if (entity != null) {
					entity.setDead();
				}
			}
		}

		@Override
		public int increaseCloakLevel() {
			int ret = super.increaseCloakLevel();
			if (ret == 3) {
				EntityPlayer jinchuriki = this.getJinchurikiPlayer();
				jinchuriki.inventory.clearMatchingItems(ItemBijuCloak.helmet, -1, -1, null);
				jinchuriki.inventory.clearMatchingItems(ItemBijuCloak.body, -1, -1, null);
				jinchuriki.inventory.clearMatchingItems(ItemBijuCloak.legs, -1, -1, null);
				Entity biju = new EntityCustom(jinchuriki, true);
				biju.forceSpawn = true;
				jinchuriki.world.spawnEntity(biju);
				biju.forceSpawn = false;
			}
			return ret;
		}*/

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}

	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_ninetails"; 
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
	 		return tailBeastManager;
	 	}

	 	@Override
	 	protected EntityTailedBeast.Base createEntity(World world) {
	 		return new EntityCustom(world);
	 	}
	}

	public static class EntityCustom extends EntityTailedBeast.Base {
		private static final DataParameter<Boolean> KCM = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);

		public EntityCustom(World world) {
			super(world);
			this.setSize(MODELSCALE * 0.6F, MODELSCALE * 2.1F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			this(player, false);
		}

		public EntityCustom(EntityPlayer player, boolean is_kcm) {
			super(player);
			this.setSize(MODELSCALE * 0.6F, MODELSCALE * 2.1F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
			this.getDataManager().set(KCM, Boolean.valueOf(is_kcm));
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(KCM, Boolean.valueOf(false));
		}

		public boolean isKCM() {
			return ((Boolean)this.getDataManager().get(KCM)).booleanValue();
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.8D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10000.0D);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(30.0D);
		}

		@Override
		public EntityBijuManager getBijuManager() {
			return tailBeastManager;
		}

		@Override
		public double getMountedYOffset() {
			return 1.5625d * MODELSCALE;
		}

		@Override
		public boolean shouldRiderSit() {
			return false;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0.0d, 0.125d * MODELSCALE, 0.1875d * MODELSCALE) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotatePitch(-this.rotationPitch * 0.017453292F)
				 .rotateYaw(-this.rotationYaw * 0.017453292F).add(this.getPositionVector());
				passenger.setPosition(vec2.x, vec2.y + this.getMountedYOffset() + passenger.getYOffset(), vec2.z);
			}
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kyuubi_howl"));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return null;
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kyuubi_death"));
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends EntityTailedBeast.Renderer<EntityCustom> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/ninetails.png");
		private final ResourceLocation KCM_TEXTURE = new ResourceLocation("narutomod:textures/ninetailskcm.png");

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn, new ModelNineTails(), MODELSCALE * 0.5F);
		}

		@Override
		protected void renderModel(EntityCustom entity, float f0, float f1, float f2, float f3, float f4, float f5) {
			if (entity.isKCM()) {
				GlStateManager.enableBlend();
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.8f);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			}
			super.renderModel(entity, f0, f1, f2, f3, f4, f5);
		}

	 	@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return entity.isKCM() ? KCM_TEXTURE : TEXTURE;
		}
	}

	// Made with Blockbench 3.8.4
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelNineTails extends ModelBiped {
		//private final ModelRenderer bipedHead;
		private final ModelRenderer snout;
		private final ModelRenderer cube_r9;
		private final ModelRenderer cube_r2;
		private final ModelRenderer cube_r10;
		private final ModelRenderer cube_r3;
		private final ModelRenderer cube_r13;
		private final ModelRenderer jaw;
		private final ModelRenderer earRight;
		private final ModelRenderer cube_r5;
		private final ModelRenderer earLeft;
		private final ModelRenderer cube_r4;
		//private final ModelRenderer bipedHeadwear;
		private final ModelRenderer bone;
		//private final ModelRenderer bipedBody;
		private final ModelRenderer waist;
		private final ModelRenderer chest;
		//private final ModelRenderer bipedRightArm;
		private final ModelRenderer upperArmRight;
		private final ModelRenderer lowerArmRight;
		private final ModelRenderer rightHand;
		private final ModelRenderer bone5;
		private final ModelRenderer bone6;
		private final ModelRenderer bone11;
		private final ModelRenderer bone12;
		private final ModelRenderer bone14;
		private final ModelRenderer bone15;
		private final ModelRenderer bone7;
		private final ModelRenderer bone8;
		private final ModelRenderer bone9;
		private final ModelRenderer bone10;
		//private final ModelRenderer bipedLeftArm;
		private final ModelRenderer upperArmLeft;
		private final ModelRenderer lowerArmLeft;
		private final ModelRenderer leftHand;
		private final ModelRenderer bone2;
		private final ModelRenderer bone3;
		private final ModelRenderer bone4;
		private final ModelRenderer bone13;
		private final ModelRenderer bone16;
		private final ModelRenderer bone17;
		private final ModelRenderer bone18;
		private final ModelRenderer bone19;
		private final ModelRenderer bone20;
		private final ModelRenderer bone21;
		//private final ModelRenderer bipedRightLeg;
		private final ModelRenderer upperLegRight;
		private final ModelRenderer lowerLegRight;
		private final ModelRenderer rightFoot;
		private final ModelRenderer bone25;
		private final ModelRenderer bone26;
		private final ModelRenderer bone27;
		private final ModelRenderer bone28;
		private final ModelRenderer bone29;
		private final ModelRenderer bone30;
		private final ModelRenderer bone31;
		private final ModelRenderer bone32;
		private final ModelRenderer bone33;
		private final ModelRenderer bone34;
		//private final ModelRenderer bipedLeftLeg;
		private final ModelRenderer upperLegLeft;
		private final ModelRenderer lowerLegLeft;
		private final ModelRenderer leftFoot;
		private final ModelRenderer bone22;
		private final ModelRenderer bone23;
		private final ModelRenderer bone24;
		private final ModelRenderer bone35;
		private final ModelRenderer bone36;
		private final ModelRenderer bone37;
		private final ModelRenderer bone38;
		private final ModelRenderer bone39;
		private final ModelRenderer bone40;
		private final ModelRenderer bone41;
		private final ModelRenderer[][] Tail = new ModelRenderer[9][8];
		private final float tailSwayX[][] = new float[9][2];
		private final float tailSwayZ[][] = new float[9][2];
		private final Random rand = new Random();

		public ModelNineTails() {
			textureWidth = 64;
			textureHeight = 64;

			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, -1.0F, -8.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 52, -4.0F, -8.0F, -4.0F, 8, 8, 3, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 14, 53, -3.0F, -7.5F, -1.0F, 6, 8, 3, 0.0F, false));
	
			snout = new ModelRenderer(this);
			snout.setRotationPoint(0.0F, -2.75F, -2.75F);
			bipedHead.addChild(snout);
			
	
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(-2.0F, -0.7676F, -5.8181F);
			snout.addChild(cube_r9);
			setRotationAngle(cube_r9, 0.0F, -0.1745F, 0.0F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 12, 0.0F, -0.4483F, -0.3093F, 2, 3, 6, 0.0F, true));
	
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(2.0F, -0.7676F, -5.8181F);
			snout.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.0F, 0.1745F, 0.0F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 12, -2.0F, -0.4483F, -0.3093F, 2, 3, 6, 0.0F, false));
	
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(-2.0F, -0.7676F, -5.8181F);
			snout.addChild(cube_r10);
			setRotationAngle(cube_r10, 0.2618F, -0.1745F, 0.0F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 0, 0.0F, -0.4824F, -0.5681F, 2, 2, 6, 0.0F, true));
	
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(2.0F, -0.7676F, -5.8181F);
			snout.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.2618F, 0.1745F, 0.0F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 0, -2.0F, -0.4824F, -0.5681F, 2, 2, 6, 0.0F, false));
	
			cube_r13 = new ModelRenderer(this);
			cube_r13.setRotationPoint(0.0F, -0.75F, -5.25F);
			snout.addChild(cube_r13);
			setRotationAngle(cube_r13, 0.2793F, 0.0F, 0.0F);
			cube_r13.cubeList.add(new ModelBox(cube_r13, 16, 14, -1.5F, -0.5388F, -1.5257F, 3, 3, 6, 0.0F, false));
	
			jaw = new ModelRenderer(this);
			jaw.setRotationPoint(0.0F, -1.0F, -3.5F);
			bipedHead.addChild(jaw);
			jaw.cubeList.add(new ModelBox(jaw, 0, 22, -2.0F, 0.0F, -5.0F, 4, 1, 5, 0.0F, false));
	
			earRight = new ModelRenderer(this);
			earRight.setRotationPoint(-3.25F, -6.0F, -4.0F);
			bipedHead.addChild(earRight);
			setRotationAngle(earRight, 0.2618F, 0.0F, 0.0F);
			earRight.cubeList.add(new ModelBox(earRight, 52, 16, -1.0F, -1.0152F, -0.4236F, 3, 3, 2, 0.0F, true));
	
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(-1.0F, 2.0F, 1.75F);
			earRight.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.0F, -0.2618F, 0.0F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 36, 16, -0.0151F, -3.0152F, -0.173F, 2, 3, 3, 0.0F, true));
			cube_r5.cubeList.add(new ModelBox(cube_r5, 20, 24, -0.0151F, -3.0152F, 2.577F, 2, 3, 3, -0.2F, true));
			cube_r5.cubeList.add(new ModelBox(cube_r5, 30, 24, -0.0151F, -3.0152F, 4.827F, 2, 3, 3, -0.4F, true));
			cube_r5.cubeList.add(new ModelBox(cube_r5, 40, 24, -0.0151F, -3.0152F, 6.827F, 2, 3, 3, -0.6F, true));
			cube_r5.cubeList.add(new ModelBox(cube_r5, 50, 24, -0.0151F, -3.0152F, 8.327F, 2, 3, 3, -0.8F, true));
	
			earLeft = new ModelRenderer(this);
			earLeft.setRotationPoint(3.25F, -6.0F, -4.0F);
			bipedHead.addChild(earLeft);
			setRotationAngle(earLeft, 0.2618F, 0.0F, 0.0F);
			earLeft.cubeList.add(new ModelBox(earLeft, 52, 16, -2.0F, -1.0152F, -0.4236F, 3, 3, 2, 0.0F, false));
	
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(1.0F, 2.0F, 1.75F);
			earLeft.addChild(cube_r4);
			setRotationAngle(cube_r4, 0.0F, 0.2618F, 0.0F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 36, 16, -1.9849F, -3.0152F, -0.173F, 2, 3, 3, 0.0F, false));
			cube_r4.cubeList.add(new ModelBox(cube_r4, 20, 24, -1.9849F, -3.0152F, 2.577F, 2, 3, 3, -0.2F, false));
			cube_r4.cubeList.add(new ModelBox(cube_r4, 30, 24, -1.9849F, -3.0152F, 4.827F, 2, 3, 3, -0.4F, false));
			cube_r4.cubeList.add(new ModelBox(cube_r4, 40, 24, -1.9849F, -3.0152F, 6.827F, 2, 3, 3, -0.6F, false));
			cube_r4.cubeList.add(new ModelBox(cube_r4, 50, 24, -1.9849F, -3.0152F, 8.327F, 2, 3, 3, -0.8F, false));
	
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, -1.0F, -8.0F);
			
	
			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.0F, -5.25F, -4.35F);
			bipedHeadwear.addChild(bone);
			setRotationAngle(bone, 0.2618F, 0.0F, 0.0F);
			bone.cubeList.add(new ModelBox(bone, 0, 9, -4.0F, -1.0F, 0.0F, 8, 2, 0, 0.0F, false));
	
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, -1.0F, 0.0F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 27, 48, -5.0F, 9.0F, -2.0F, 10, 5, 5, 0.0F, false));
	
			waist = new ModelRenderer(this);
			waist.setRotationPoint(-1.0F, 9.0F, 0.0F);
			bipedBody.addChild(waist);
			setRotationAngle(waist, 0.5236F, 0.0F, 0.0F);
			waist.cubeList.add(new ModelBox(waist, 30, 53, -5.0F, -4.634F, -2.5F, 12, 6, 5, 0.0F, false));
	
			chest = new ModelRenderer(this);
			chest.setRotationPoint(-1.0F, 2.0F, -5.0F);
			bipedBody.addChild(chest);
			setRotationAngle(chest, 0.7854F, 0.0F, 0.0F);
			chest.cubeList.add(new ModelBox(chest, 24, 0, -6.0F, -3.2929F, -3.7071F, 14, 8, 6, 0.0F, false));
	
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-6.0F, 1.0F, -7.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 32, -1.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));
	
			upperArmRight = new ModelRenderer(this);
			upperArmRight.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedRightArm.addChild(upperArmRight);
			setRotationAngle(upperArmRight, 0.0F, 0.0F, 0.3491F);
			upperArmRight.cubeList.add(new ModelBox(upperArmRight, 0, 32, -3.6579F, -1.0603F, -2.0F, 4, 12, 4, 0.0F, true));
	
			lowerArmRight = new ModelRenderer(this);
			lowerArmRight.setRotationPoint(-5.5F, 8.2791F, -0.1201F);
			bipedRightArm.addChild(lowerArmRight);
			setRotationAngle(lowerArmRight, -0.5236F, 0.0F, -0.2618F);
			lowerArmRight.cubeList.add(new ModelBox(lowerArmRight, 48, 32, -1.2588F, -0.1635F, -1.517F, 3, 12, 3, 0.0F, true));
	
			rightHand = new ModelRenderer(this);
			rightHand.setRotationPoint(0.75F, 10.9709F, -0.6299F);
			lowerArmRight.addChild(rightHand);
			setRotationAngle(rightHand, 1.5708F, 0.5236F, 1.5708F);
			
	
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
			rightHand.addChild(bone5);
			bone5.cubeList.add(new ModelBox(bone5, 0, 48, 0.483F, 0.3365F, -0.7588F, 3, 1, 1, 0.0F, false));
	
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(3.2F, 0.8F, -0.2F);
			bone5.addChild(bone6);
			setRotationAngle(bone6, 0.0F, 0.0F, 0.5236F);
			bone6.cubeList.add(new ModelBox(bone6, 0, 50, -0.2635F, -0.467F, -0.5588F, 3, 1, 1, 0.0F, false));
			bone6.cubeList.add(new ModelBox(bone6, 12, 48, 1.9865F, -0.717F, -0.5588F, 2, 1, 1, -0.2F, false));
	
			bone11 = new ModelRenderer(this);
			bone11.setRotationPoint(0.0F, 0.0F, 0.25F);
			rightHand.addChild(bone11);
			setRotationAngle(bone11, 0.0F, -0.1745F, 0.0F);
			bone11.cubeList.add(new ModelBox(bone11, 0, 48, 0.4307F, 0.3365F, -0.8388F, 3, 1, 1, 0.0F, false));
	
			bone12 = new ModelRenderer(this);
			bone12.setRotationPoint(3.2F, 0.8F, -0.3F);
			bone11.addChild(bone12);
			setRotationAngle(bone12, 0.0F, 0.0F, 0.5236F);
			bone12.cubeList.add(new ModelBox(bone12, 0, 50, -0.2088F, -0.4909F, -0.5388F, 3, 1, 1, 0.0F, false));
			bone12.cubeList.add(new ModelBox(bone12, 12, 48, 2.0412F, -0.7409F, -0.5388F, 2, 1, 1, -0.2F, false));
	
			bone14 = new ModelRenderer(this);
			bone14.setRotationPoint(0.5F, 0.9F, -0.25F);
			rightHand.addChild(bone14);
			setRotationAngle(bone14, -1.5708F, -0.7854F, 0.5236F);
			bone14.cubeList.add(new ModelBox(bone14, 0, 48, -0.0489F, -0.5603F, -0.5088F, 3, 1, 1, 0.0F, false));
	
			bone15 = new ModelRenderer(this);
			bone15.setRotationPoint(2.6622F, -0.0175F, -0.0239F);
			bone14.addChild(bone15);
			setRotationAngle(bone15, 0.0F, 0.0F, 0.7854F);
			bone15.cubeList.add(new ModelBox(bone15, 0, 50, -0.2712F, -0.5654F, -0.485F, 3, 1, 1, 0.0F, false));
			bone15.cubeList.add(new ModelBox(bone15, 12, 48, 1.9788F, -0.8154F, -0.485F, 2, 1, 1, -0.2F, false));
	
			bone7 = new ModelRenderer(this);
			bone7.setRotationPoint(0.0F, 0.0F, -0.5F);
			rightHand.addChild(bone7);
			setRotationAngle(bone7, 0.0F, 0.1745F, 0.0F);
			bone7.cubeList.add(new ModelBox(bone7, 0, 48, 0.5206F, 0.3365F, -0.671F, 3, 1, 1, 0.0F, false));
	
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(3.3F, 0.8F, -0.1F);
			bone7.addChild(bone8);
			setRotationAngle(bone8, 0.0F, 0.0F, 0.5236F);
			bone8.cubeList.add(new ModelBox(bone8, 0, 50, -0.2309F, -0.4358F, -0.571F, 3, 1, 1, 0.0F, false));
			bone8.cubeList.add(new ModelBox(bone8, 12, 48, 2.0191F, -0.6858F, -0.571F, 2, 1, 1, -0.2F, false));
	
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(0.0F, 0.0F, -1.0F);
			rightHand.addChild(bone9);
			setRotationAngle(bone9, 0.0F, 0.3491F, 0.0F);
			bone9.cubeList.add(new ModelBox(bone9, 0, 48, 0.5424F, 0.3365F, -0.578F, 3, 1, 1, 0.0F, false));
	
			bone10 = new ModelRenderer(this);
			bone10.setRotationPoint(3.3F, 0.8F, 0.0F);
			bone9.addChild(bone10);
			setRotationAngle(bone10, 0.0F, 0.0F, 0.5236F);
			bone10.cubeList.add(new ModelBox(bone10, 0, 50, -0.212F, -0.4467F, -0.578F, 3, 1, 1, 0.0F, false));
			bone10.cubeList.add(new ModelBox(bone10, 12, 48, 2.038F, -0.6967F, -0.578F, 2, 1, 1, -0.2F, false));
	
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(6.0F, 1.0F, -7.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 32, 0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F, false));
	
			upperArmLeft = new ModelRenderer(this);
			upperArmLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedLeftArm.addChild(upperArmLeft);
			setRotationAngle(upperArmLeft, 0.0F, 0.0F, -0.3491F);
			upperArmLeft.cubeList.add(new ModelBox(upperArmLeft, 0, 32, -0.3421F, -1.0603F, -2.0F, 4, 12, 4, 0.0F, false));
	
			lowerArmLeft = new ModelRenderer(this);
			lowerArmLeft.setRotationPoint(5.5F, 8.2791F, -0.1201F);
			bipedLeftArm.addChild(lowerArmLeft);
			setRotationAngle(lowerArmLeft, -0.5236F, 0.0F, 0.2618F);
			lowerArmLeft.cubeList.add(new ModelBox(lowerArmLeft, 48, 32, -1.7412F, -0.1635F, -1.517F, 3, 12, 3, 0.0F, false));
	
			leftHand = new ModelRenderer(this);
			leftHand.setRotationPoint(-0.75F, 10.9709F, -0.6299F);
			lowerArmLeft.addChild(leftHand);
			setRotationAngle(leftHand, 1.5708F, -0.5236F, -1.5708F);
			
	
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
			leftHand.addChild(bone2);
			bone2.cubeList.add(new ModelBox(bone2, 0, 48, -3.483F, 0.3365F, -0.7588F, 3, 1, 1, 0.0F, false));
	
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(-3.2F, 0.8F, -0.2F);
			bone2.addChild(bone3);
			setRotationAngle(bone3, 0.0F, 0.0F, -0.5236F);
			bone3.cubeList.add(new ModelBox(bone3, 0, 50, -2.7365F, -0.467F, -0.5588F, 3, 1, 1, 0.0F, false));
			bone3.cubeList.add(new ModelBox(bone3, 12, 48, -3.9865F, -0.717F, -0.5588F, 2, 1, 1, -0.2F, false));
	
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(0.0F, 0.0F, 0.25F);
			leftHand.addChild(bone4);
			setRotationAngle(bone4, 0.0F, 0.1745F, 0.0F);
			bone4.cubeList.add(new ModelBox(bone4, 0, 48, -3.4307F, 0.3365F, -0.8388F, 3, 1, 1, 0.0F, false));
	
			bone13 = new ModelRenderer(this);
			bone13.setRotationPoint(-3.2F, 0.8F, -0.3F);
			bone4.addChild(bone13);
			setRotationAngle(bone13, 0.0F, 0.0F, -0.5236F);
			bone13.cubeList.add(new ModelBox(bone13, 0, 50, -2.7912F, -0.4909F, -0.5388F, 3, 1, 1, 0.0F, false));
			bone13.cubeList.add(new ModelBox(bone13, 12, 48, -4.0412F, -0.7409F, -0.5388F, 2, 1, 1, -0.2F, false));
	
			bone16 = new ModelRenderer(this);
			bone16.setRotationPoint(-0.5F, 0.9F, -0.25F);
			leftHand.addChild(bone16);
			setRotationAngle(bone16, -1.5708F, 0.7854F, -0.5236F);
			bone16.cubeList.add(new ModelBox(bone16, 0, 48, -2.9511F, -0.5603F, -0.5088F, 3, 1, 1, 0.0F, false));
	
			bone17 = new ModelRenderer(this);
			bone17.setRotationPoint(-2.6622F, -0.0175F, -0.0239F);
			bone16.addChild(bone17);
			setRotationAngle(bone17, 0.0F, 0.0F, -0.7854F);
			bone17.cubeList.add(new ModelBox(bone17, 0, 50, -2.7288F, -0.5654F, -0.485F, 3, 1, 1, 0.0F, false));
			bone17.cubeList.add(new ModelBox(bone17, 12, 48, -3.9788F, -0.8154F, -0.485F, 2, 1, 1, -0.2F, false));
	
			bone18 = new ModelRenderer(this);
			bone18.setRotationPoint(0.0F, 0.0F, -0.5F);
			leftHand.addChild(bone18);
			setRotationAngle(bone18, 0.0F, -0.1745F, 0.0F);
			bone18.cubeList.add(new ModelBox(bone18, 0, 48, -3.5206F, 0.3365F, -0.671F, 3, 1, 1, 0.0F, false));
	
			bone19 = new ModelRenderer(this);
			bone19.setRotationPoint(-3.3F, 0.8F, -0.1F);
			bone18.addChild(bone19);
			setRotationAngle(bone19, 0.0F, 0.0F, -0.5236F);
			bone19.cubeList.add(new ModelBox(bone19, 0, 50, -2.7691F, -0.4358F, -0.571F, 3, 1, 1, 0.0F, false));
			bone19.cubeList.add(new ModelBox(bone19, 12, 48, -4.0191F, -0.6858F, -0.571F, 2, 1, 1, -0.2F, false));
	
			bone20 = new ModelRenderer(this);
			bone20.setRotationPoint(0.0F, 0.0F, -1.0F);
			leftHand.addChild(bone20);
			setRotationAngle(bone20, 0.0F, -0.3491F, 0.0F);
			bone20.cubeList.add(new ModelBox(bone20, 0, 48, -3.5424F, 0.3365F, -0.578F, 3, 1, 1, 0.0F, false));
	
			bone21 = new ModelRenderer(this);
			bone21.setRotationPoint(-3.3F, 0.8F, 0.0F);
			bone20.addChild(bone21);
			setRotationAngle(bone21, 0.0F, 0.0F, -0.5236F);
			bone21.cubeList.add(new ModelBox(bone21, 0, 50, -2.788F, -0.4467F, -0.578F, 3, 1, 1, 0.0F, false));
			bone21.cubeList.add(new ModelBox(bone21, 12, 48, -4.038F, -0.6967F, -0.578F, 2, 1, 1, -0.2F, false));
	
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.5F);
			
	
			upperLegRight = new ModelRenderer(this);
			upperLegRight.setRotationPoint(-2.1F, 0.0F, 1.0F);
			bipedRightLeg.addChild(upperLegRight);
			setRotationAngle(upperLegRight, -0.5236F, 0.0F, 1.5708F);
			upperLegRight.cubeList.add(new ModelBox(upperLegRight, 16, 32, -1.0F, 0.0F, -3.0F, 4, 12, 4, 0.5F, false));
	
			lowerLegRight = new ModelRenderer(this);
			lowerLegRight.setRotationPoint(0.0F, 11.0F, -1.0F);
			upperLegRight.addChild(lowerLegRight);
			setRotationAngle(lowerLegRight, 0.0F, 0.0F, -2.0944F);
			lowerLegRight.cubeList.add(new ModelBox(lowerLegRight, 16, 32, -2.5F, -0.134F, -2.0F, 4, 12, 4, 0.0F, false));
	
			rightFoot = new ModelRenderer(this);
			rightFoot.setRotationPoint(0.25F, 11.25F, -0.25F);
			lowerLegRight.addChild(rightFoot);
			setRotationAngle(rightFoot, 0.0F, 3.1416F, 0.5236F);
			
	
			bone25 = new ModelRenderer(this);
			bone25.setRotationPoint(0.0F, 0.0F, 0.0F);
			rightFoot.addChild(bone25);
			bone25.cubeList.add(new ModelBox(bone25, 0, 50, 0.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));
	
			bone26 = new ModelRenderer(this);
			bone26.setRotationPoint(2.8F, 1.0F, 0.0F);
			bone25.addChild(bone26);
			setRotationAngle(bone26, 0.0F, 0.0F, 0.5236F);
			bone26.cubeList.add(new ModelBox(bone26, 0, 48, -0.2F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			bone26.cubeList.add(new ModelBox(bone26, 12, 48, 2.05F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));
	
			bone27 = new ModelRenderer(this);
			bone27.setRotationPoint(0.0F, 0.0F, 0.25F);
			rightFoot.addChild(bone27);
			setRotationAngle(bone27, 0.0F, -0.1745F, 0.0F);
			bone27.cubeList.add(new ModelBox(bone27, 0, 50, 0.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));
	
			bone28 = new ModelRenderer(this);
			bone28.setRotationPoint(2.7F, 1.0F, 0.0F);
			bone27.addChild(bone28);
			setRotationAngle(bone28, 0.0F, 0.0F, 0.5236F);
			bone28.cubeList.add(new ModelBox(bone28, 0, 48, -0.1F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			bone28.cubeList.add(new ModelBox(bone28, 12, 48, 2.15F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));
	
			bone29 = new ModelRenderer(this);
			bone29.setRotationPoint(0.0F, 0.0F, 0.75F);
			rightFoot.addChild(bone29);
			setRotationAngle(bone29, -0.5236F, -0.5236F, 0.0F);
			bone29.cubeList.add(new ModelBox(bone29, 0, 50, 0.0F, 0.366F, 0.0F, 3, 1, 1, 0.0F, false));
	
			bone30 = new ModelRenderer(this);
			bone30.setRotationPoint(2.7F, 0.9F, 0.5F);
			bone29.addChild(bone30);
			setRotationAngle(bone30, 0.0F, 0.0F, 0.5236F);
			bone30.cubeList.add(new ModelBox(bone30, 0, 48, -0.167F, -0.55F, -0.5F, 3, 1, 1, 0.0F, false));
			bone30.cubeList.add(new ModelBox(bone30, 12, 48, 2.083F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));
	
			bone31 = new ModelRenderer(this);
			bone31.setRotationPoint(0.0F, 0.0F, -0.5F);
			rightFoot.addChild(bone31);
			setRotationAngle(bone31, 0.0F, 0.1745F, 0.0F);
			bone31.cubeList.add(new ModelBox(bone31, 0, 50, 0.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));
	
			bone32 = new ModelRenderer(this);
			bone32.setRotationPoint(2.8F, 0.95F, 0.0F);
			bone31.addChild(bone32);
			setRotationAngle(bone32, 0.0F, 0.0F, 0.5236F);
			bone32.cubeList.add(new ModelBox(bone32, 0, 48, -0.2F, -0.45F, -0.5F, 3, 1, 1, 0.0F, false));
			bone32.cubeList.add(new ModelBox(bone32, 12, 48, 2.05F, -0.75F, -0.5F, 2, 1, 1, -0.2F, false));
	
			bone33 = new ModelRenderer(this);
			bone33.setRotationPoint(0.0F, 0.0F, -1.0F);
			rightFoot.addChild(bone33);
			setRotationAngle(bone33, 0.0F, 0.3491F, 0.0F);
			bone33.cubeList.add(new ModelBox(bone33, 0, 50, 0.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));
	
			bone34 = new ModelRenderer(this);
			bone34.setRotationPoint(2.8F, 1.0F, 0.0F);
			bone33.addChild(bone34);
			setRotationAngle(bone34, 0.0F, 0.0F, 0.5236F);
			bone34.cubeList.add(new ModelBox(bone34, 0, 48, -0.2F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			bone34.cubeList.add(new ModelBox(bone34, 12, 48, 2.05F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));
	
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.5F);
			
	
			upperLegLeft = new ModelRenderer(this);
			upperLegLeft.setRotationPoint(2.1F, 0.0F, 1.0F);
			bipedLeftLeg.addChild(upperLegLeft);
			setRotationAngle(upperLegLeft, -0.5236F, 0.0F, -1.5708F);
			upperLegLeft.cubeList.add(new ModelBox(upperLegLeft, 16, 32, -3.0F, 0.0F, -3.0F, 4, 12, 4, 0.5F, true));
	
			lowerLegLeft = new ModelRenderer(this);
			lowerLegLeft.setRotationPoint(0.0F, 11.0F, -1.0F);
			upperLegLeft.addChild(lowerLegLeft);
			setRotationAngle(lowerLegLeft, 0.0F, 0.0F, 2.0944F);
			lowerLegLeft.cubeList.add(new ModelBox(lowerLegLeft, 16, 32, -1.5F, -0.134F, -2.0F, 4, 12, 4, 0.0F, true));
	
			leftFoot = new ModelRenderer(this);
			leftFoot.setRotationPoint(-0.25F, 11.25F, -0.25F);
			lowerLegLeft.addChild(leftFoot);
			setRotationAngle(leftFoot, 0.0F, 3.1416F, -0.5236F);
			
	
			bone22 = new ModelRenderer(this);
			bone22.setRotationPoint(0.0F, 0.0F, 0.0F);
			leftFoot.addChild(bone22);
			bone22.cubeList.add(new ModelBox(bone22, 0, 50, -3.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));
	
			bone23 = new ModelRenderer(this);
			bone23.setRotationPoint(-2.8F, 1.0F, 0.0F);
			bone22.addChild(bone23);
			setRotationAngle(bone23, 0.0F, 0.0F, -0.5236F);
			bone23.cubeList.add(new ModelBox(bone23, 0, 48, -2.8F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			bone23.cubeList.add(new ModelBox(bone23, 12, 48, -4.05F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));
	
			bone24 = new ModelRenderer(this);
			bone24.setRotationPoint(0.0F, 0.0F, 0.25F);
			leftFoot.addChild(bone24);
			setRotationAngle(bone24, 0.0F, 0.1745F, 0.0F);
			bone24.cubeList.add(new ModelBox(bone24, 0, 50, -3.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));
	
			bone35 = new ModelRenderer(this);
			bone35.setRotationPoint(-2.7F, 1.0F, 0.0F);
			bone24.addChild(bone35);
			setRotationAngle(bone35, 0.0F, 0.0F, -0.5236F);
			bone35.cubeList.add(new ModelBox(bone35, 0, 48, -2.9F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			bone35.cubeList.add(new ModelBox(bone35, 12, 48, -4.15F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));
	
			bone36 = new ModelRenderer(this);
			bone36.setRotationPoint(0.0F, 0.0F, 0.75F);
			leftFoot.addChild(bone36);
			setRotationAngle(bone36, -0.5236F, 0.5236F, 0.0F);
			bone36.cubeList.add(new ModelBox(bone36, 0, 50, -3.0F, 0.366F, 0.0F, 3, 1, 1, 0.0F, false));
	
			bone37 = new ModelRenderer(this);
			bone37.setRotationPoint(-2.7F, 0.9F, 0.5F);
			bone36.addChild(bone37);
			setRotationAngle(bone37, 0.0F, 0.0F, -0.5236F);
			bone37.cubeList.add(new ModelBox(bone37, 0, 48, -2.833F, -0.55F, -0.5F, 3, 1, 1, 0.0F, false));
			bone37.cubeList.add(new ModelBox(bone37, 12, 48, -4.083F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));
	
			bone38 = new ModelRenderer(this);
			bone38.setRotationPoint(0.0F, 0.0F, -0.5F);
			leftFoot.addChild(bone38);
			setRotationAngle(bone38, 0.0F, -0.1745F, 0.0F);
			bone38.cubeList.add(new ModelBox(bone38, 0, 50, -3.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));
	
			bone39 = new ModelRenderer(this);
			bone39.setRotationPoint(-2.8F, 0.95F, 0.0F);
			bone38.addChild(bone39);
			setRotationAngle(bone39, 0.0F, 0.0F, -0.5236F);
			bone39.cubeList.add(new ModelBox(bone39, 0, 48, -2.8F, -0.45F, -0.5F, 3, 1, 1, 0.0F, false));
			bone39.cubeList.add(new ModelBox(bone39, 12, 48, -4.05F, -0.75F, -0.5F, 2, 1, 1, -0.2F, false));
	
			bone40 = new ModelRenderer(this);
			bone40.setRotationPoint(0.0F, 0.0F, -1.0F);
			leftFoot.addChild(bone40);
			setRotationAngle(bone40, 0.0F, -0.3491F, 0.0F);
			bone40.cubeList.add(new ModelBox(bone40, 0, 50, -3.0F, 0.5F, -0.5F, 3, 1, 1, 0.0F, false));
	
			bone41 = new ModelRenderer(this);
			bone41.setRotationPoint(-2.8F, 1.0F, 0.0F);
			bone40.addChild(bone41);
			setRotationAngle(bone41, 0.0F, 0.0F, -0.5236F);
			bone41.cubeList.add(new ModelBox(bone41, 0, 48, -2.8F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			bone41.cubeList.add(new ModelBox(bone41, 12, 48, -4.05F, -0.8F, -0.5F, 2, 1, 1, -0.2F, false));

			Tail[0][0] = new ModelRenderer(this);
			Tail[0][0].setRotationPoint(4.0F, 13.0F, 4.0F);
			setRotationAngle(Tail[0][0], -0.5236F, 0.0F, 1.4835F);
			Tail[0][0].cubeList.add(new ModelBox(Tail[0][0], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[0][1] = new ModelRenderer(this);
			Tail[0][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[0][0].addChild(Tail[0][1]);
			setRotationAngle(Tail[0][1], 0.0F, 0.0F, -0.1745F);
			Tail[0][1].cubeList.add(new ModelBox(Tail[0][1], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.3F, false));
			Tail[0][2] = new ModelRenderer(this);
			Tail[0][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[0][1].addChild(Tail[0][2]);
			setRotationAngle(Tail[0][2], 0.0F, 0.0F, -0.1745F);
			Tail[0][2].cubeList.add(new ModelBox(Tail[0][2], 32, 31, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.6F, false));
			Tail[0][3] = new ModelRenderer(this);
			Tail[0][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[0][2].addChild(Tail[0][3]);
			setRotationAngle(Tail[0][3], 0.0F, 0.0F, -0.1745F);
			Tail[0][3].cubeList.add(new ModelBox(Tail[0][3], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.8F, false));
			Tail[0][4] = new ModelRenderer(this);
			Tail[0][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[0][3].addChild(Tail[0][4]);
			setRotationAngle(Tail[0][4], 0.0F, 0.0F, -0.1745F);
			Tail[0][4].cubeList.add(new ModelBox(Tail[0][4], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.4F, false));
			Tail[0][5] = new ModelRenderer(this);
			Tail[0][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[0][4].addChild(Tail[0][5]);
			setRotationAngle(Tail[0][5], 0.0F, 0.0F, -0.1745F);
			Tail[0][5].cubeList.add(new ModelBox(Tail[0][5], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[0][6] = new ModelRenderer(this);
			Tail[0][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[0][5].addChild(Tail[0][6]);
			setRotationAngle(Tail[0][6], 0.0F, 0.0F, -0.1745F);
			Tail[0][6].cubeList.add(new ModelBox(Tail[0][6], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.4F, false));
			Tail[0][7] = new ModelRenderer(this);
			Tail[0][7].setRotationPoint(0.0F, -6.0F, 0.0F);
			Tail[0][6].addChild(Tail[0][7]);
			setRotationAngle(Tail[0][7], 0.0F, 0.0F, -0.1745F);
			Tail[0][7].cubeList.add(new ModelBox(Tail[0][7], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.8F, false));
			Tail[1][0] = new ModelRenderer(this);
			Tail[1][0].setRotationPoint(3.0F, 13.0F, 4.0F);
			setRotationAngle(Tail[1][0], -0.7854F, 0.0F, 1.1345F);
			Tail[1][0].cubeList.add(new ModelBox(Tail[1][0], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[1][1] = new ModelRenderer(this);
			Tail[1][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[1][0].addChild(Tail[1][1]);
			setRotationAngle(Tail[1][1], 0.0F, 0.0F, -0.1745F);
			Tail[1][1].cubeList.add(new ModelBox(Tail[1][1], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.3F, false));
			Tail[1][2] = new ModelRenderer(this);
			Tail[1][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[1][1].addChild(Tail[1][2]);
			setRotationAngle(Tail[1][2], 0.0F, 0.0F, -0.1745F);
			Tail[1][2].cubeList.add(new ModelBox(Tail[1][2], 32, 31, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.6F, false));
			Tail[1][3] = new ModelRenderer(this);
			Tail[1][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[1][2].addChild(Tail[1][3]);
			setRotationAngle(Tail[1][3], 0.0F, 0.0F, -0.1745F);
			Tail[1][3].cubeList.add(new ModelBox(Tail[1][3], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.8F, false));
			Tail[1][4] = new ModelRenderer(this);
			Tail[1][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[1][3].addChild(Tail[1][4]);
			setRotationAngle(Tail[1][4], 0.0F, 0.0F, -0.1745F);
			Tail[1][4].cubeList.add(new ModelBox(Tail[1][4], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.4F, false));
			Tail[1][5] = new ModelRenderer(this);
			Tail[1][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[1][4].addChild(Tail[1][5]);
			setRotationAngle(Tail[1][5], 0.0F, 0.0F, -0.1745F);
			Tail[1][5].cubeList.add(new ModelBox(Tail[1][5], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[1][6] = new ModelRenderer(this);
			Tail[1][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[1][5].addChild(Tail[1][6]);
			setRotationAngle(Tail[1][6], 0.0F, 0.0F, -0.1745F);
			Tail[1][6].cubeList.add(new ModelBox(Tail[1][6], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.4F, false));
			Tail[1][7] = new ModelRenderer(this);
			Tail[1][7].setRotationPoint(0.0F, -6.0F, 0.0F);
			Tail[1][6].addChild(Tail[1][7]);
			setRotationAngle(Tail[1][7], 0.0F, 0.0F, -0.1745F);
			Tail[1][7].cubeList.add(new ModelBox(Tail[1][7], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.8F, false));
			Tail[2][0] = new ModelRenderer(this);
			Tail[2][0].setRotationPoint(2.0F, 13.0F, 4.0F);
			setRotationAngle(Tail[2][0], -1.0472F, 0.0F, 0.7854F);
			Tail[2][0].cubeList.add(new ModelBox(Tail[2][0], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[2][1] = new ModelRenderer(this);
			Tail[2][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[2][0].addChild(Tail[2][1]);
			setRotationAngle(Tail[2][1], 0.0F, 0.0F, -0.1745F);
			Tail[2][1].cubeList.add(new ModelBox(Tail[2][1], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.3F, false));
			Tail[2][2] = new ModelRenderer(this);
			Tail[2][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[2][1].addChild(Tail[2][2]);
			setRotationAngle(Tail[2][2], 0.0F, 0.0F, -0.1745F);
			Tail[2][2].cubeList.add(new ModelBox(Tail[2][2], 32, 31, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.6F, false));
			Tail[2][3] = new ModelRenderer(this);
			Tail[2][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[2][2].addChild(Tail[2][3]);
			setRotationAngle(Tail[2][3], 0.0F, 0.0F, -0.1745F);
			Tail[2][3].cubeList.add(new ModelBox(Tail[2][3], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.8F, false));
			Tail[2][4] = new ModelRenderer(this);
			Tail[2][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[2][3].addChild(Tail[2][4]);
			setRotationAngle(Tail[2][4], 0.0F, 0.0F, -0.1745F);
			Tail[2][4].cubeList.add(new ModelBox(Tail[2][4], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.4F, false));
			Tail[2][5] = new ModelRenderer(this);
			Tail[2][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[2][4].addChild(Tail[2][5]);
			setRotationAngle(Tail[2][5], 0.0F, 0.0F, -0.1745F);
			Tail[2][5].cubeList.add(new ModelBox(Tail[2][5], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[2][6] = new ModelRenderer(this);
			Tail[2][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[2][5].addChild(Tail[2][6]);
			setRotationAngle(Tail[2][6], 0.0F, 0.0F, -0.1745F);
			Tail[2][6].cubeList.add(new ModelBox(Tail[2][6], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.4F, false));
			Tail[2][7] = new ModelRenderer(this);
			Tail[2][7].setRotationPoint(0.0F, -6.0F, 0.0F);
			Tail[2][6].addChild(Tail[2][7]);
			setRotationAngle(Tail[2][7], 0.0F, 0.0F, -0.1745F);
			Tail[2][7].cubeList.add(new ModelBox(Tail[2][7], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.8F, false));
			Tail[3][0] = new ModelRenderer(this);
			Tail[3][0].setRotationPoint(1.0F, 13.0F, 4.0F);
			setRotationAngle(Tail[3][0], -1.309F, 0.0F, 0.4363F);
			Tail[3][0].cubeList.add(new ModelBox(Tail[3][0], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[3][1] = new ModelRenderer(this);
			Tail[3][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[3][0].addChild(Tail[3][1]);
			setRotationAngle(Tail[3][1], 0.0F, 0.0F, -0.1745F);
			Tail[3][1].cubeList.add(new ModelBox(Tail[3][1], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.3F, false));
			Tail[3][2] = new ModelRenderer(this);
			Tail[3][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[3][1].addChild(Tail[3][2]);
			setRotationAngle(Tail[3][2], 0.0F, 0.0F, -0.1745F);
			Tail[3][2].cubeList.add(new ModelBox(Tail[3][2], 32, 31, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.6F, false));
			Tail[3][3] = new ModelRenderer(this);
			Tail[3][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[3][2].addChild(Tail[3][3]);
			setRotationAngle(Tail[3][3], 0.0F, 0.0F, -0.1745F);
			Tail[3][3].cubeList.add(new ModelBox(Tail[3][3], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.8F, false));
			Tail[3][4] = new ModelRenderer(this);
			Tail[3][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[3][3].addChild(Tail[3][4]);
			setRotationAngle(Tail[3][4], 0.0F, 0.0F, -0.1745F);
			Tail[3][4].cubeList.add(new ModelBox(Tail[3][4], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.4F, false));
			Tail[3][5] = new ModelRenderer(this);
			Tail[3][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[3][4].addChild(Tail[3][5]);
			setRotationAngle(Tail[3][5], 0.0F, 0.0F, -0.1745F);
			Tail[3][5].cubeList.add(new ModelBox(Tail[3][5], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[3][6] = new ModelRenderer(this);
			Tail[3][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[3][5].addChild(Tail[3][6]);
			setRotationAngle(Tail[3][6], 0.0F, 0.0F, -0.1745F);
			Tail[3][6].cubeList.add(new ModelBox(Tail[3][6], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.4F, false));
			Tail[3][7] = new ModelRenderer(this);
			Tail[3][7].setRotationPoint(0.0F, -6.0F, 0.0F);
			Tail[3][6].addChild(Tail[3][7]);
			setRotationAngle(Tail[3][7], 0.0F, 0.0F, -0.1745F);
			Tail[3][7].cubeList.add(new ModelBox(Tail[3][7], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.8F, false));
			Tail[4][0] = new ModelRenderer(this);
			Tail[4][0].setRotationPoint(0.0F, 13.0F, 4.0F);
			setRotationAngle(Tail[4][0], -1.5708F, 0.0F, 0.0F);
			Tail[4][0].cubeList.add(new ModelBox(Tail[4][0], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[4][1] = new ModelRenderer(this);
			Tail[4][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[4][0].addChild(Tail[4][1]);
			setRotationAngle(Tail[4][1], 0.1745F, 0.0F, 0.0F);
			Tail[4][1].cubeList.add(new ModelBox(Tail[4][1], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.3F, false));
			Tail[4][2] = new ModelRenderer(this);
			Tail[4][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[4][1].addChild(Tail[4][2]);
			setRotationAngle(Tail[4][2], 0.1745F, 0.0F, 0.0F);
			Tail[4][2].cubeList.add(new ModelBox(Tail[4][2], 32, 31, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.6F, false));
			Tail[4][3] = new ModelRenderer(this);
			Tail[4][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[4][2].addChild(Tail[4][3]);
			setRotationAngle(Tail[4][3], 0.1745F, 0.0F, 0.0F);
			Tail[4][3].cubeList.add(new ModelBox(Tail[4][3], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.8F, false));
			Tail[4][4] = new ModelRenderer(this);
			Tail[4][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[4][3].addChild(Tail[4][4]);
			setRotationAngle(Tail[4][4], 0.1745F, 0.0F, 0.0F);
			Tail[4][4].cubeList.add(new ModelBox(Tail[4][4], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.4F, false));
			Tail[4][5] = new ModelRenderer(this);
			Tail[4][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[4][4].addChild(Tail[4][5]);
			setRotationAngle(Tail[4][5], 0.1745F, 0.0F, 0.0F);
			Tail[4][5].cubeList.add(new ModelBox(Tail[4][5], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, false));
			Tail[4][6] = new ModelRenderer(this);
			Tail[4][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[4][5].addChild(Tail[4][6]);
			setRotationAngle(Tail[4][6], 0.1745F, 0.0F, 0.0F);
			Tail[4][6].cubeList.add(new ModelBox(Tail[4][6], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.4F, false));
			Tail[4][7] = new ModelRenderer(this);
			Tail[4][7].setRotationPoint(0.0F, -6.0F, 0.0F);
			Tail[4][6].addChild(Tail[4][7]);
			setRotationAngle(Tail[4][7], 0.1745F, 0.0F, 0.0F);
			Tail[4][7].cubeList.add(new ModelBox(Tail[4][7], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.8F, false));
			Tail[5][0] = new ModelRenderer(this);
			Tail[5][0].setRotationPoint(-1.0F, 13.0F, 4.0F);
			setRotationAngle(Tail[5][0], -1.309F, 0.0F, -0.4363F);
			Tail[5][0].cubeList.add(new ModelBox(Tail[5][0], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, true));
			Tail[5][1] = new ModelRenderer(this);
			Tail[5][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[5][0].addChild(Tail[5][1]);
			setRotationAngle(Tail[5][1], 0.0F, 0.0F, 0.1745F);
			Tail[5][1].cubeList.add(new ModelBox(Tail[5][1], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.3F, true));
			Tail[5][2] = new ModelRenderer(this);
			Tail[5][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[5][1].addChild(Tail[5][2]);
			setRotationAngle(Tail[5][2], 0.0F, 0.0F, 0.1745F);
			Tail[5][2].cubeList.add(new ModelBox(Tail[5][2], 32, 31, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.6F, true));
			Tail[5][3] = new ModelRenderer(this);
			Tail[5][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[5][2].addChild(Tail[5][3]);
			setRotationAngle(Tail[5][3], 0.0F, 0.0F, 0.1745F);
			Tail[5][3].cubeList.add(new ModelBox(Tail[5][3], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.8F, true));
			Tail[5][4] = new ModelRenderer(this);
			Tail[5][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[5][3].addChild(Tail[5][4]);
			setRotationAngle(Tail[5][4], 0.0F, 0.0F, 0.1745F);
			Tail[5][4].cubeList.add(new ModelBox(Tail[5][4], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.4F, true));
			Tail[5][5] = new ModelRenderer(this);
			Tail[5][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[5][4].addChild(Tail[5][5]);
			setRotationAngle(Tail[5][5], 0.0F, 0.0F, 0.1745F);
			Tail[5][5].cubeList.add(new ModelBox(Tail[5][5], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, true));
			Tail[5][6] = new ModelRenderer(this);
			Tail[5][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[5][5].addChild(Tail[5][6]);
			setRotationAngle(Tail[5][6], 0.0F, 0.0F, 0.1745F);
			Tail[5][6].cubeList.add(new ModelBox(Tail[5][6], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.4F, true));
			Tail[5][7] = new ModelRenderer(this);
			Tail[5][7].setRotationPoint(0.0F, -6.0F, 0.0F);
			Tail[5][6].addChild(Tail[5][7]);
			setRotationAngle(Tail[5][7], 0.0F, 0.0F, 0.1745F);
			Tail[5][7].cubeList.add(new ModelBox(Tail[5][7], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.8F, true));
			Tail[6][0] = new ModelRenderer(this);
			Tail[6][0].setRotationPoint(-2.0F, 13.0F, 4.0F);
			setRotationAngle(Tail[6][0], -1.0472F, 0.0F, -0.7854F);
			Tail[6][0].cubeList.add(new ModelBox(Tail[6][0], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, true));
			Tail[6][1] = new ModelRenderer(this);
			Tail[6][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[6][0].addChild(Tail[6][1]);
			setRotationAngle(Tail[6][1], 0.0F, 0.0F, 0.1745F);
			Tail[6][1].cubeList.add(new ModelBox(Tail[6][1], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.3F, true));
			Tail[6][2] = new ModelRenderer(this);
			Tail[6][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[6][1].addChild(Tail[6][2]);
			setRotationAngle(Tail[6][2], 0.0F, 0.0F, 0.1745F);
			Tail[6][2].cubeList.add(new ModelBox(Tail[6][2], 32, 31, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.6F, true));
			Tail[6][3] = new ModelRenderer(this);
			Tail[6][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[6][2].addChild(Tail[6][3]);
			setRotationAngle(Tail[6][3], 0.0F, 0.0F, 0.1745F);
			Tail[6][3].cubeList.add(new ModelBox(Tail[6][3], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.8F, true));
			Tail[6][4] = new ModelRenderer(this);
			Tail[6][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[6][3].addChild(Tail[6][4]);
			setRotationAngle(Tail[6][4], 0.0F, 0.0F, 0.1745F);
			Tail[6][4].cubeList.add(new ModelBox(Tail[6][4], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.4F, true));
			Tail[6][5] = new ModelRenderer(this);
			Tail[6][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[6][4].addChild(Tail[6][5]);
			setRotationAngle(Tail[6][5], 0.0F, 0.0F, 0.1745F);
			Tail[6][5].cubeList.add(new ModelBox(Tail[6][5], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, true));
			Tail[6][6] = new ModelRenderer(this);
			Tail[6][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[6][5].addChild(Tail[6][6]);
			setRotationAngle(Tail[6][6], 0.0F, 0.0F, 0.1745F);
			Tail[6][6].cubeList.add(new ModelBox(Tail[6][6], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.4F, true));
			Tail[6][7] = new ModelRenderer(this);
			Tail[6][7].setRotationPoint(0.0F, -6.0F, 0.0F);
			Tail[6][6].addChild(Tail[6][7]);
			setRotationAngle(Tail[6][7], 0.0F, 0.0F, 0.1745F);
			Tail[6][7].cubeList.add(new ModelBox(Tail[6][7], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.8F, true));
			Tail[7][0] = new ModelRenderer(this);
			Tail[7][0].setRotationPoint(-3.0F, 13.0F, 4.0F);
			setRotationAngle(Tail[7][0], -0.7854F, 0.0F, -1.1345F);
			Tail[7][0].cubeList.add(new ModelBox(Tail[7][0], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, true));
			Tail[7][1] = new ModelRenderer(this);
			Tail[7][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[7][0].addChild(Tail[7][1]);
			setRotationAngle(Tail[7][1], 0.0F, 0.0F, 0.1745F);
			Tail[7][1].cubeList.add(new ModelBox(Tail[7][1], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.3F, true));
			Tail[7][2] = new ModelRenderer(this);
			Tail[7][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[7][1].addChild(Tail[7][2]);
			setRotationAngle(Tail[7][2], 0.0F, 0.0F, 0.1745F);
			Tail[7][2].cubeList.add(new ModelBox(Tail[7][2], 32, 31, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.6F, true));
			Tail[7][3] = new ModelRenderer(this);
			Tail[7][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[7][2].addChild(Tail[7][3]);
			setRotationAngle(Tail[7][3], 0.0F, 0.0F, 0.1745F);
			Tail[7][3].cubeList.add(new ModelBox(Tail[7][3], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.8F, true));
			Tail[7][4] = new ModelRenderer(this);
			Tail[7][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[7][3].addChild(Tail[7][4]);
			setRotationAngle(Tail[7][4], 0.0F, 0.0F, 0.1745F);
			Tail[7][4].cubeList.add(new ModelBox(Tail[7][4], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.4F, true));
			Tail[7][5] = new ModelRenderer(this);
			Tail[7][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[7][4].addChild(Tail[7][5]);
			setRotationAngle(Tail[7][5], 0.0F, 0.0F, 0.1745F);
			Tail[7][5].cubeList.add(new ModelBox(Tail[7][5], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, true));
			Tail[7][6] = new ModelRenderer(this);
			Tail[7][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[7][5].addChild(Tail[7][6]);
			setRotationAngle(Tail[7][6], 0.0F, 0.0F, 0.1745F);
			Tail[7][6].cubeList.add(new ModelBox(Tail[7][6], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.4F, true));
			Tail[7][7] = new ModelRenderer(this);
			Tail[7][7].setRotationPoint(0.0F, -6.0F, 0.0F);
			Tail[7][6].addChild(Tail[7][7]);
			setRotationAngle(Tail[7][7], 0.0F, 0.0F, 0.1745F);
			Tail[7][7].cubeList.add(new ModelBox(Tail[7][7], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.8F, true));
			Tail[8][0] = new ModelRenderer(this);
			Tail[8][0].setRotationPoint(-4.0F, 13.0F, 4.0F);
			setRotationAngle(Tail[8][0], -0.5236F, 0.0F, -1.4835F);
			Tail[8][0].cubeList.add(new ModelBox(Tail[8][0], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, true));
			Tail[8][1] = new ModelRenderer(this);
			Tail[8][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[8][0].addChild(Tail[8][1]);
			setRotationAngle(Tail[8][1], 0.0F, 0.0F, 0.1745F);
			Tail[8][1].cubeList.add(new ModelBox(Tail[8][1], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.3F, true));
			Tail[8][2] = new ModelRenderer(this);
			Tail[8][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[8][1].addChild(Tail[8][2]);
			setRotationAngle(Tail[8][2], 0.0F, 0.0F, 0.1745F);
			Tail[8][2].cubeList.add(new ModelBox(Tail[8][2], 32, 31, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.6F, true));
			Tail[8][3] = new ModelRenderer(this);
			Tail[8][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[8][2].addChild(Tail[8][3]);
			setRotationAngle(Tail[8][3], 0.0F, 0.0F, 0.1745F);
			Tail[8][3].cubeList.add(new ModelBox(Tail[8][3], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.8F, true));
			Tail[8][4] = new ModelRenderer(this);
			Tail[8][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[8][3].addChild(Tail[8][4]);
			setRotationAngle(Tail[8][4], 0.0F, 0.0F, 0.1745F);
			Tail[8][4].cubeList.add(new ModelBox(Tail[8][4], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.4F, true));
			Tail[8][5] = new ModelRenderer(this);
			Tail[8][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[8][4].addChild(Tail[8][5]);
			setRotationAngle(Tail[8][5], 0.0F, 0.0F, 0.1745F);
			Tail[8][5].cubeList.add(new ModelBox(Tail[8][5], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, 0.0F, true));
			Tail[8][6] = new ModelRenderer(this);
			Tail[8][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			Tail[8][5].addChild(Tail[8][6]);
			setRotationAngle(Tail[8][6], 0.0F, 0.0F, 0.1745F);
			Tail[8][6].cubeList.add(new ModelBox(Tail[8][6], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.4F, true));
			Tail[8][7] = new ModelRenderer(this);
			Tail[8][7].setRotationPoint(0.0F, -6.0F, 0.0F);
			Tail[8][6].addChild(Tail[8][7]);
			setRotationAngle(Tail[8][7], 0.0F, 0.0F, 0.1745F);
			Tail[8][7].cubeList.add(new ModelBox(Tail[8][7], 32, 32, -2.0F, -8.0F, -2.0F, 4, 8, 4, -0.8F, true));

			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 2; j++) {
					tailSwayX[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayZ[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
				}
			}
		}

		@Override
		public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F);
			GlStateManager.translate(0.0F, 0.0F, 0.375F * MODELSCALE);
			GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
			super.render(entity, f0, f1, f2, f3, f4, f5);
			for (int i = 0; i < 9; i++) {
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
			bipedHead.rotationPointY += -1.0F;
			bipedHeadwear.rotationPointY += -1.0F;
			bipedRightArm.rotationPointZ += -7.0F;
			bipedRightArm.rotationPointX += -1.0F;
			bipedLeftArm.rotationPointZ += -7.0F;
			bipedLeftArm.rotationPointX += 1.0F;
			for (int i = 0; i < 9; i++) {
				for (int j = 1; j < 8; j++) {
					if (j == 4) {
						Tail[i][j].rotateAngleX = 0.1745F + MathHelper.sin((f2 - j) * 0.2F) * tailSwayX[i][0];
						Tail[i][j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.2F) * tailSwayZ[i][1];
					} else if (j < 4) {
						Tail[i][j].rotateAngleX = MathHelper.sin((f2 - j) * 0.2F) * tailSwayX[i][0];
						Tail[i][j].rotateAngleZ = -0.1745F + MathHelper.cos((f2 - j) * 0.2F) * tailSwayZ[i][1];
					} else {
						Tail[i][j].rotateAngleX = MathHelper.sin((f2 - j) * 0.2F) * tailSwayX[i][0];
						Tail[i][j].rotateAngleZ = 0.1745F + MathHelper.cos((f2 - j) * 0.2F) * tailSwayZ[i][1];
					}
					//Tail[i][j].rotateAngleY += ((EntityLivingBase)e).getRNG().nextFloat() * MathHelper.sqrt((float)j) * 0.0015625F * (i % 2 == 0 ? -1F : 1F);
				}
			}
			if (!e.onGround) {
				upperLegRight.rotateAngleY = -0.5236F;
				upperLegRight.rotateAngleZ = 1.0472F;
				lowerLegRight.rotateAngleZ = -1.5708F;
				rightFoot.rotateAngleZ = 0.0F;
				upperLegLeft.rotateAngleY = 0.5236F;
				upperLegLeft.rotateAngleZ = -1.0472F;
				lowerLegLeft.rotateAngleZ = 1.5708F;
				leftFoot.rotateAngleZ = 0.0F;
			} else {
				upperLegRight.rotateAngleY = 0.0F;
				upperLegRight.rotateAngleZ = 1.5708F;
				lowerLegRight.rotateAngleZ = -2.0944F;
				rightFoot.rotateAngleZ = 0.5236F;
				upperLegLeft.rotateAngleY = 0.0F;
				upperLegLeft.rotateAngleZ = -1.5708F;
				lowerLegLeft.rotateAngleZ = 2.0944F;
				leftFoot.rotateAngleZ = -0.5236F;
			}
			if (((EntityCustom)e).isShooting()) {
				//bipedHead.rotateAngleX = -0.5236F;
				//bipedHeadwear.rotateAngleX = -0.5236F;
				snout.rotateAngleX = -0.2618F;
				jaw.rotateAngleX = 1.0472F;
			} else {
				snout.rotateAngleX = 0.0F;
				jaw.rotateAngleX = 0.0F;
			}
		}
	}
}
