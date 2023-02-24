
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

import net.narutomod.ElementsNarutomodMod;

import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEightTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 253;
	public static final int ENTITYID_RANGED = 254;
	private static final float MODELSCALE = 10.0F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntityEightTails(ElementsNarutomodMod instance) {
		super(instance, 580);
	}

	@Override
	public void initElements() {
		elements.entities
		 .add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "eight_tails"), ENTITYID)
		 .name("eight_tails").tracker(96, 3, true).egg(-5469059, -5469059).build());
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
			super(EntityCustom.class, 8);
		}

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}

	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_eighttails";
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
		public EntityCustom(World world) {
			super(world);
			this.setSize(MODELSCALE * 0.6F, MODELSCALE * 2.0F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.6F, MODELSCALE * 2.0F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		@Override
		public float getModelScale() {
			return MODELSCALE;
		}

		@Override
		public void setFaceDown(boolean down) {
			super.setFaceDown(down);
			this.setSize(this.width, MODELSCALE * (down ? 1.0F : 2.0F));
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
			return (this.isFaceDown() ? -0.09375d : 1.25d) * MODELSCALE;
		}

		@Override
		public boolean shouldRiderSit() {
			return true;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0.0d, 0.34375d * MODELSCALE, 0.25d * MODELSCALE) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotatePitch(-this.rotationPitch * 0.017453292F)
				 .rotateYaw(-this.rotationYaw * 0.017453292F).add(this.getPositionVector());
				passenger.setPosition(vec2.x, vec2.y + this.getMountedYOffset() + passenger.getYOffset(), vec2.z);
			}
		}

		@Override
		public float getFuuinBeamHeight() {
			return this.isFaceDown() ? 4.0f * 0.0625f * MODELSCALE : super.getFuuinBeamHeight();
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends EntityTailedBeast.Renderer<EntityCustom> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/eighttails.png");

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn, new ModelEightTails(), MODELSCALE * 0.5F);
		}

	 	@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return TEXTURE;
		}
	}

	// Made with Blockbench 3.8.4
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelEightTails extends ModelBiped {
		//private final ModelRenderer bipedHead;
		private final ModelRenderer hornRight1;
		private final ModelRenderer hornRight2;
		private final ModelRenderer hornRight3;
		private final ModelRenderer hornRight4;
		private final ModelRenderer hornRight5;
		private final ModelRenderer hornRight6;
		private final ModelRenderer hornRight13;
		private final ModelRenderer hornRight14;
		private final ModelRenderer hornRight15;
		private final ModelRenderer hornRight16;
		private final ModelRenderer hornRight17;
		private final ModelRenderer hornRight18;
		private final ModelRenderer hornRight19;
		private final ModelRenderer hornRight20;
		private final ModelRenderer hornRight21;
		private final ModelRenderer hornRight22;
		private final ModelRenderer hornRight23;
		private final ModelRenderer hornRight24;
		private final ModelRenderer hornRight7;
		private final ModelRenderer hornRight8;
		private final ModelRenderer hornRight9;
		private final ModelRenderer hornRight10;
		private final ModelRenderer hornRight11;
		private final ModelRenderer hornRight12;
		private final ModelRenderer snout;
		private final ModelRenderer bone3;
		private final ModelRenderer bone6;
		private final ModelRenderer bone;
		private final ModelRenderer bone4;
		private final ModelRenderer bone2;
		private final ModelRenderer bone5;
		private final ModelRenderer jaw;
		private final ModelRenderer bone8;
		//private final ModelRenderer bipedHeadwear;
		private final ModelRenderer eyes;
		//private final ModelRenderer bipedBody;
		private final ModelRenderer chest;
		private final ModelRenderer hump;
		//private final ModelRenderer bipedRightArm;
		private final ModelRenderer upperArmRight;
		private final ModelRenderer foreArmRight;
		//private final ModelRenderer bipedLeftArm;
		private final ModelRenderer upperArmLeft;
		private final ModelRenderer foreArmLeft;
		private final ModelRenderer[][] tail = new ModelRenderer[8][8];
		private final float tailSwayX[][] = new float[8][8];
		private final float tailSwayY[][] = new float[8][8];
		private final float tailSwayZ[][] = new float[8][8];
		private final Random rand = new Random();

		public ModelEightTails() {
			textureWidth = 64;
			textureHeight = 64;

			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 23.5F, 0.0F);
			
	
			eyes = new ModelRenderer(this);
			eyes.setRotationPoint(0.0F, -19.5F, -6.0F);
			bipedHeadwear.addChild(eyes);
			eyes.cubeList.add(new ModelBox(eyes, 32, 18, -3.0F, -8.0F, -3.1F, 6, 2, 0, 0.0F, false));
	
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 23.5F, 0.0F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -7.75F, -11.4F, -4.0F, 8, 12, 8, -0.2F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -0.25F, -11.4F, -4.0F, 8, 12, 8, -0.2F, true));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 18, 48, -7.0F, -9.5F, -4.5F, 7, 8, 1, -0.2F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 18, 48, 0.0F, -9.5F, -4.5F, 7, 8, 1, -0.2F, true));
	
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, -19.5F, -6.0F);
			bipedBody.addChild(bipedHead);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 34, -3.0F, -8.0F, -3.0F, 6, 8, 6, 0.0F, false));
	
			hornRight1 = new ModelRenderer(this);
			hornRight1.setRotationPoint(-3.5F, -7.0F, -1.0F);
			bipedHead.addChild(hornRight1);
			setRotationAngle(hornRight1, 0.2618F, 0.0F, 0.0F);
			hornRight1.cubeList.add(new ModelBox(hornRight1, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
	
			hornRight2 = new ModelRenderer(this);
			hornRight2.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight1.addChild(hornRight2);
			setRotationAngle(hornRight2, 0.0F, -0.3491F, 0.0F);
			hornRight2.cubeList.add(new ModelBox(hornRight2, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, false));
	
			hornRight3 = new ModelRenderer(this);
			hornRight3.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight2.addChild(hornRight3);
			setRotationAngle(hornRight3, 0.0F, -0.3491F, 0.0F);
			hornRight3.cubeList.add(new ModelBox(hornRight3, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, false));
	
			hornRight4 = new ModelRenderer(this);
			hornRight4.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight3.addChild(hornRight4);
			setRotationAngle(hornRight4, 0.0F, -0.3491F, 0.0F);
			hornRight4.cubeList.add(new ModelBox(hornRight4, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, false));
	
			hornRight5 = new ModelRenderer(this);
			hornRight5.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight4.addChild(hornRight5);
			setRotationAngle(hornRight5, 0.0F, -0.3491F, 0.0F);
			hornRight5.cubeList.add(new ModelBox(hornRight5, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, false));
	
			hornRight6 = new ModelRenderer(this);
			hornRight6.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight5.addChild(hornRight6);
			setRotationAngle(hornRight6, 0.0F, -0.3491F, 0.0F);
			hornRight6.cubeList.add(new ModelBox(hornRight6, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, false));
	
			hornRight13 = new ModelRenderer(this);
			hornRight13.setRotationPoint(-1.5F, -7.5F, 1.0F);
			bipedHead.addChild(hornRight13);
			setRotationAngle(hornRight13, 0.0F, 0.5236F, 1.309F);
			hornRight13.cubeList.add(new ModelBox(hornRight13, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
	
			hornRight14 = new ModelRenderer(this);
			hornRight14.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight13.addChild(hornRight14);
			setRotationAngle(hornRight14, 0.0F, 0.0436F, 0.0F);
			hornRight14.cubeList.add(new ModelBox(hornRight14, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, false));
	
			hornRight15 = new ModelRenderer(this);
			hornRight15.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight14.addChild(hornRight15);
			setRotationAngle(hornRight15, 0.0F, 0.0436F, 0.0F);
			hornRight15.cubeList.add(new ModelBox(hornRight15, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, false));
	
			hornRight16 = new ModelRenderer(this);
			hornRight16.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight15.addChild(hornRight16);
			setRotationAngle(hornRight16, 0.0F, 0.0436F, 0.0F);
			hornRight16.cubeList.add(new ModelBox(hornRight16, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, false));
	
			hornRight17 = new ModelRenderer(this);
			hornRight17.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight16.addChild(hornRight17);
			setRotationAngle(hornRight17, 0.0F, 0.0436F, 0.0F);
			hornRight17.cubeList.add(new ModelBox(hornRight17, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, false));
	
			hornRight18 = new ModelRenderer(this);
			hornRight18.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight17.addChild(hornRight18);
			setRotationAngle(hornRight18, 0.0F, 0.0436F, 0.0F);
			hornRight18.cubeList.add(new ModelBox(hornRight18, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, false));
	
			hornRight19 = new ModelRenderer(this);
			hornRight19.setRotationPoint(1.5F, -7.5F, 1.0F);
			bipedHead.addChild(hornRight19);
			setRotationAngle(hornRight19, 0.0F, -0.5236F, -1.309F);
			hornRight19.cubeList.add(new ModelBox(hornRight19, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
	
			hornRight20 = new ModelRenderer(this);
			hornRight20.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight19.addChild(hornRight20);
			setRotationAngle(hornRight20, 0.0F, -0.0436F, 0.0F);
			hornRight20.cubeList.add(new ModelBox(hornRight20, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, true));
	
			hornRight21 = new ModelRenderer(this);
			hornRight21.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight20.addChild(hornRight21);
			setRotationAngle(hornRight21, 0.0F, -0.0436F, 0.0F);
			hornRight21.cubeList.add(new ModelBox(hornRight21, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, true));
	
			hornRight22 = new ModelRenderer(this);
			hornRight22.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight21.addChild(hornRight22);
			setRotationAngle(hornRight22, 0.0F, -0.0436F, 0.0F);
			hornRight22.cubeList.add(new ModelBox(hornRight22, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, true));
	
			hornRight23 = new ModelRenderer(this);
			hornRight23.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight22.addChild(hornRight23);
			setRotationAngle(hornRight23, 0.0F, -0.0436F, 0.0F);
			hornRight23.cubeList.add(new ModelBox(hornRight23, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, true));
	
			hornRight24 = new ModelRenderer(this);
			hornRight24.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight23.addChild(hornRight24);
			setRotationAngle(hornRight24, 0.0F, -0.0436F, 0.0F);
			hornRight24.cubeList.add(new ModelBox(hornRight24, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, true));
	
			hornRight7 = new ModelRenderer(this);
			hornRight7.setRotationPoint(3.5F, -7.0F, -1.0F);
			bipedHead.addChild(hornRight7);
			setRotationAngle(hornRight7, 0.2618F, 0.0F, 0.0F);
			hornRight7.cubeList.add(new ModelBox(hornRight7, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
	
			hornRight8 = new ModelRenderer(this);
			hornRight8.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight7.addChild(hornRight8);
			setRotationAngle(hornRight8, 0.0F, 0.3491F, 0.0F);
			hornRight8.cubeList.add(new ModelBox(hornRight8, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, true));
	
			hornRight9 = new ModelRenderer(this);
			hornRight9.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight8.addChild(hornRight9);
			setRotationAngle(hornRight9, 0.0F, 0.3491F, 0.0F);
			hornRight9.cubeList.add(new ModelBox(hornRight9, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, true));
	
			hornRight10 = new ModelRenderer(this);
			hornRight10.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight9.addChild(hornRight10);
			setRotationAngle(hornRight10, 0.0F, 0.3491F, 0.0F);
			hornRight10.cubeList.add(new ModelBox(hornRight10, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, true));
	
			hornRight11 = new ModelRenderer(this);
			hornRight11.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight10.addChild(hornRight11);
			setRotationAngle(hornRight11, 0.0F, 0.3491F, 0.0F);
			hornRight11.cubeList.add(new ModelBox(hornRight11, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, true));
	
			hornRight12 = new ModelRenderer(this);
			hornRight12.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight11.addChild(hornRight12);
			setRotationAngle(hornRight12, 0.0F, 0.3491F, 0.0F);
			hornRight12.cubeList.add(new ModelBox(hornRight12, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, true));
	
			snout = new ModelRenderer(this);
			snout.setRotationPoint(0.0F, -4.0F, -3.0F);
			bipedHead.addChild(snout);
			
	
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(0.0F, -2.0F, 0.0F);
			snout.addChild(bone3);
			setRotationAngle(bone3, 0.2618F, 0.0F, 0.0F);
			bone3.cubeList.add(new ModelBox(bone3, 46, 44, -1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));
	
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(0.5F, -1.0F, 0.0F);
			snout.addChild(bone6);
			bone6.cubeList.add(new ModelBox(bone6, 24, 20, -2.0F, 0.0F, -4.0F, 3, 3, 4, 0.0F, false));
	
			bone = new ModelRenderer(this);
			bone.setRotationPoint(-3.0F, -2.0F, 0.0F);
			snout.addChild(bone);
			setRotationAngle(bone, 0.2618F, -0.3491F, 0.0F);
			bone.cubeList.add(new ModelBox(bone, 50, 0, 0.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));
	
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(3.0F, -2.0F, 0.0F);
			snout.addChild(bone4);
			setRotationAngle(bone4, 0.2618F, 0.3491F, 0.0F);
			bone4.cubeList.add(new ModelBox(bone4, 50, 0, -2.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, true));
	
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(-3.0F, 2.0F, 0.0F);
			snout.addChild(bone2);
			setRotationAngle(bone2, 0.0F, -0.3491F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 34, 48, 0.0F, -2.0F, -4.0F, 2, 2, 4, 0.0F, false));
	
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(3.0F, 2.0F, 0.0F);
			snout.addChild(bone5);
			setRotationAngle(bone5, 0.0F, 0.3491F, 0.0F);
			bone5.cubeList.add(new ModelBox(bone5, 34, 48, -2.0F, -2.0F, -4.0F, 2, 2, 4, 0.0F, true));
	
			jaw = new ModelRenderer(this);
			jaw.setRotationPoint(0.0F, -2.0F, -3.0F);
			bipedHead.addChild(jaw);
			setRotationAngle(jaw, 0.48F, 0.0F, 0.0F);
			jaw.cubeList.add(new ModelBox(jaw, 24, 0, -1.5F, 0.0F, -4.0F, 3, 2, 4, 0.0F, false));
	
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(0.0F, 2.0F, -2.0F);
			jaw.addChild(bone8);
			setRotationAngle(bone8, -0.2618F, 0.0F, 0.0F);
			bone8.cubeList.add(new ModelBox(bone8, 0, 24, -1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F, false));
	
			chest = new ModelRenderer(this);
			chest.setRotationPoint(1.0F, -12.5F, 4.0F);
			bipedBody.addChild(chest);
			setRotationAngle(chest, 0.5236F, 0.0F, 0.0F);
			chest.cubeList.add(new ModelBox(chest, 0, 0, -9.0F, -11.0F, -8.0F, 8, 12, 8, 0.0F, false));
			chest.cubeList.add(new ModelBox(chest, 0, 0, -1.0F, -11.0F, -8.0F, 8, 12, 8, 0.0F, true));
			chest.cubeList.add(new ModelBox(chest, 0, 48, -9.0F, -10.0F, -8.75F, 8, 8, 1, -0.2F, false));
			chest.cubeList.add(new ModelBox(chest, 0, 48, -1.0F, -10.0F, -8.75F, 8, 8, 1, -0.2F, true));
	
			hump = new ModelRenderer(this);
			hump.setRotationPoint(-1.0F, -6.0F, -2.0F);
			chest.addChild(hump);
			setRotationAngle(hump, -0.5236F, -0.6981F, 0.3491F);
			hump.cubeList.add(new ModelBox(hump, 0, 20, -4.0F, -6.0F, -4.0F, 8, 6, 8, 0.0F, false));
			hump.cubeList.add(new ModelBox(hump, 0, 20, -3.0F, -1.0F, -3.0F, 8, 6, 8, -1.0F, false));
	
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-7.0F, -17.5F, -5.0F);
			bipedBody.addChild(bipedRightArm);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 5, -1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
	
			upperArmRight = new ModelRenderer(this);
			upperArmRight.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedRightArm.addChild(upperArmRight);
			setRotationAngle(upperArmRight, 0.0F, 0.0F, 0.3491F);
			upperArmRight.cubeList.add(new ModelBox(upperArmRight, 32, 0, -6.0F, -2.0F, -3.0F, 6, 12, 6, 0.0F, false));
	
			foreArmRight = new ModelRenderer(this);
			foreArmRight.setRotationPoint(-3.0F, 8.0F, 2.0F);
			upperArmRight.addChild(foreArmRight);
			setRotationAngle(foreArmRight, -0.5236F, 0.0F, -0.5236F);
			foreArmRight.cubeList.add(new ModelBox(foreArmRight, 26, 28, -3.0F, 0.0F, -5.0F, 6, 14, 6, -0.2F, false));
			foreArmRight.cubeList.add(new ModelBox(foreArmRight, 0, 0, -2.75F, -2.0F, -1.0F, 2, 3, 2, 0.0F, false));
			foreArmRight.cubeList.add(new ModelBox(foreArmRight, 0, 0, -2.75F, -4.0F, -1.0F, 2, 3, 2, -0.2F, false));
			foreArmRight.cubeList.add(new ModelBox(foreArmRight, 0, 0, -2.75F, -6.0F, -1.0F, 2, 3, 2, -0.4F, false));
	
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(7.0F, -17.5F, -5.0F);
			bipedBody.addChild(bipedLeftArm);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 5, 0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, true));
	
			upperArmLeft = new ModelRenderer(this);
			upperArmLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedLeftArm.addChild(upperArmLeft);
			setRotationAngle(upperArmLeft, 0.0F, 0.0F, -0.3491F);
			upperArmLeft.cubeList.add(new ModelBox(upperArmLeft, 32, 0, 0.0F, -2.0F, -3.0F, 6, 12, 6, 0.0F, true));
	
			foreArmLeft = new ModelRenderer(this);
			foreArmLeft.setRotationPoint(3.0F, 8.0F, 2.0F);
			upperArmLeft.addChild(foreArmLeft);
			setRotationAngle(foreArmLeft, -0.5236F, 0.0F, 0.5236F);
			foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 26, 28, -3.0F, 0.0F, -5.0F, 6, 14, 6, -0.2F, true));
			foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 0, 0, 0.75F, -2.0F, -1.0F, 2, 3, 2, 0.0F, true));
			foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 0, 0, 0.75F, -4.0F, -1.0F, 2, 3, 2, -0.2F, true));
			foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 0, 0, 0.75F, -6.0F, -1.0F, 2, 3, 2, -0.4F, true));
			
			/*bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 4.0F, -6.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 34, -3.0F, -8.0F, -3.0F, 6, 8, 6, 0.0F, false));
			hornRight1 = new ModelRenderer(this);
			hornRight1.setRotationPoint(-3.5F, -7.0F, -1.0F);
			bipedHead.addChild(hornRight1);
			setRotationAngle(hornRight1, 0.2618F, 0.0F, 0.0F);
			hornRight1.cubeList.add(new ModelBox(hornRight1, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
			hornRight2 = new ModelRenderer(this);
			hornRight2.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight1.addChild(hornRight2);
			setRotationAngle(hornRight2, 0.0F, -0.3491F, 0.0F);
			hornRight2.cubeList.add(new ModelBox(hornRight2, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, false));
			hornRight3 = new ModelRenderer(this);
			hornRight3.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight2.addChild(hornRight3);
			setRotationAngle(hornRight3, 0.0F, -0.3491F, 0.0F);
			hornRight3.cubeList.add(new ModelBox(hornRight3, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, false));
			hornRight4 = new ModelRenderer(this);
			hornRight4.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight3.addChild(hornRight4);
			setRotationAngle(hornRight4, 0.0F, -0.3491F, 0.0F);
			hornRight4.cubeList.add(new ModelBox(hornRight4, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, false));
			hornRight5 = new ModelRenderer(this);
			hornRight5.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight4.addChild(hornRight5);
			setRotationAngle(hornRight5, 0.0F, -0.3491F, 0.0F);
			hornRight5.cubeList.add(new ModelBox(hornRight5, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, false));
			hornRight6 = new ModelRenderer(this);
			hornRight6.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight5.addChild(hornRight6);
			setRotationAngle(hornRight6, 0.0F, -0.3491F, 0.0F);
			hornRight6.cubeList.add(new ModelBox(hornRight6, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, false));
			hornRight13 = new ModelRenderer(this);
			hornRight13.setRotationPoint(-1.5F, -7.5F, 1.0F);
			bipedHead.addChild(hornRight13);
			setRotationAngle(hornRight13, 0.0F, 0.5236F, 1.309F);
			hornRight13.cubeList.add(new ModelBox(hornRight13, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
			hornRight14 = new ModelRenderer(this);
			hornRight14.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight13.addChild(hornRight14);
			setRotationAngle(hornRight14, 0.0F, 0.0436F, 0.0F);
			hornRight14.cubeList.add(new ModelBox(hornRight14, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, false));
			hornRight15 = new ModelRenderer(this);
			hornRight15.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight14.addChild(hornRight15);
			setRotationAngle(hornRight15, 0.0F, 0.0436F, 0.0F);
			hornRight15.cubeList.add(new ModelBox(hornRight15, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, false));
			hornRight16 = new ModelRenderer(this);
			hornRight16.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight15.addChild(hornRight16);
			setRotationAngle(hornRight16, 0.0F, 0.0436F, 0.0F);
			hornRight16.cubeList.add(new ModelBox(hornRight16, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, false));
			hornRight17 = new ModelRenderer(this);
			hornRight17.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight16.addChild(hornRight17);
			setRotationAngle(hornRight17, 0.0F, 0.0436F, 0.0F);
			hornRight17.cubeList.add(new ModelBox(hornRight17, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, false));
			hornRight18 = new ModelRenderer(this);
			hornRight18.setRotationPoint(-1.0F, 0.0F, 0.0F);
			hornRight17.addChild(hornRight18);
			setRotationAngle(hornRight18, 0.0F, 0.0436F, 0.0F);
			hornRight18.cubeList.add(new ModelBox(hornRight18, 0, 20, -1.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, false));
			hornRight19 = new ModelRenderer(this);
			hornRight19.setRotationPoint(1.5F, -7.5F, 1.0F);
			bipedHead.addChild(hornRight19);
			setRotationAngle(hornRight19, 0.0F, -0.5236F, -1.309F);
			hornRight19.cubeList.add(new ModelBox(hornRight19, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
			hornRight20 = new ModelRenderer(this);
			hornRight20.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight19.addChild(hornRight20);
			setRotationAngle(hornRight20, 0.0F, -0.0436F, 0.0F);
			hornRight20.cubeList.add(new ModelBox(hornRight20, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, true));
			hornRight21 = new ModelRenderer(this);
			hornRight21.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight20.addChild(hornRight21);
			setRotationAngle(hornRight21, 0.0F, -0.0436F, 0.0F);
			hornRight21.cubeList.add(new ModelBox(hornRight21, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, true));
			hornRight22 = new ModelRenderer(this);
			hornRight22.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight21.addChild(hornRight22);
			setRotationAngle(hornRight22, 0.0F, -0.0436F, 0.0F);
			hornRight22.cubeList.add(new ModelBox(hornRight22, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, true));
			hornRight23 = new ModelRenderer(this);
			hornRight23.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight22.addChild(hornRight23);
			setRotationAngle(hornRight23, 0.0F, -0.0436F, 0.0F);
			hornRight23.cubeList.add(new ModelBox(hornRight23, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, true));
			hornRight24 = new ModelRenderer(this);
			hornRight24.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight23.addChild(hornRight24);
			setRotationAngle(hornRight24, 0.0F, -0.0436F, 0.0F);
			hornRight24.cubeList.add(new ModelBox(hornRight24, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, true));
			hornRight7 = new ModelRenderer(this);
			hornRight7.setRotationPoint(3.5F, -7.0F, -1.0F);
			bipedHead.addChild(hornRight7);
			setRotationAngle(hornRight7, 0.2618F, 0.0F, 0.0F);
			hornRight7.cubeList.add(new ModelBox(hornRight7, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
			hornRight8 = new ModelRenderer(this);
			hornRight8.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight7.addChild(hornRight8);
			setRotationAngle(hornRight8, 0.0F, 0.3491F, 0.0F);
			hornRight8.cubeList.add(new ModelBox(hornRight8, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.1F, true));
			hornRight9 = new ModelRenderer(this);
			hornRight9.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight8.addChild(hornRight9);
			setRotationAngle(hornRight9, 0.0F, 0.3491F, 0.0F);
			hornRight9.cubeList.add(new ModelBox(hornRight9, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.2F, true));
			hornRight10 = new ModelRenderer(this);
			hornRight10.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight9.addChild(hornRight10);
			setRotationAngle(hornRight10, 0.0F, 0.3491F, 0.0F);
			hornRight10.cubeList.add(new ModelBox(hornRight10, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.3F, true));
			hornRight11 = new ModelRenderer(this);
			hornRight11.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight10.addChild(hornRight11);
			setRotationAngle(hornRight11, 0.0F, 0.3491F, 0.0F);
			hornRight11.cubeList.add(new ModelBox(hornRight11, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.4F, true));
			hornRight12 = new ModelRenderer(this);
			hornRight12.setRotationPoint(1.0F, 0.0F, 0.0F);
			hornRight11.addChild(hornRight12);
			setRotationAngle(hornRight12, 0.0F, 0.3491F, 0.0F);
			hornRight12.cubeList.add(new ModelBox(hornRight12, 0, 20, -0.5F, -1.0F, -1.0F, 2, 2, 2, -0.5F, true));
			snout = new ModelRenderer(this);
			snout.setRotationPoint(0.0F, -4.0F, -3.0F);
			bipedHead.addChild(snout);
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(0.0F, -2.0F, 0.0F);
			snout.addChild(bone3);
			setRotationAngle(bone3, 0.2618F, 0.0F, 0.0F);
			bone3.cubeList.add(new ModelBox(bone3, 46, 44, -1.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(0.5F, -1.0F, 0.0F);
			snout.addChild(bone6);
			bone6.cubeList.add(new ModelBox(bone6, 24, 20, -2.0F, 0.0F, -4.0F, 3, 3, 4, 0.0F, false));
			bone = new ModelRenderer(this);
			bone.setRotationPoint(-3.0F, -2.0F, 0.0F);
			snout.addChild(bone);
			setRotationAngle(bone, 0.2618F, -0.3491F, 0.0F);
			bone.cubeList.add(new ModelBox(bone, 50, 0, 0.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, false));
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(3.0F, -2.0F, 0.0F);
			snout.addChild(bone4);
			setRotationAngle(bone4, 0.2618F, 0.3491F, 0.0F);
			bone4.cubeList.add(new ModelBox(bone4, 50, 0, -2.0F, 0.0F, -4.0F, 2, 2, 4, 0.0F, true));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(-3.0F, 2.0F, 0.0F);
			snout.addChild(bone2);
			setRotationAngle(bone2, 0.0F, -0.3491F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 34, 48, 0.0F, -2.0F, -4.0F, 2, 2, 4, 0.0F, false));
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(3.0F, 2.0F, 0.0F);
			snout.addChild(bone5);
			setRotationAngle(bone5, 0.0F, 0.3491F, 0.0F);
			bone5.cubeList.add(new ModelBox(bone5, 34, 48, -2.0F, -2.0F, -4.0F, 2, 2, 4, 0.0F, true));
			jaw = new ModelRenderer(this);
			jaw.setRotationPoint(0.0F, -2.0F, -3.0F);
			bipedHead.addChild(jaw);
			jaw.cubeList.add(new ModelBox(jaw, 24, 0, -1.5F, 0.0F, -4.0F, 3, 2, 4, 0.0F, false));
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(0.0F, 2.0F, -2.0F);
			jaw.addChild(bone8);
			setRotationAngle(bone8, -0.2618F, 0.0F, 0.0F);
			bone8.cubeList.add(new ModelBox(bone8, 0, 24, -1.0F, 0.0F, -2.0F, 2, 2, 2, 0.0F, false));
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 4.0F, -6.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 18, -3.0F, -8.0F, -3.1F, 6, 2, 0, 0.0F, false));
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 4.0F, 0.0F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -7.75F, 8.1F, -4.0F, 8, 12, 8, -0.2F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -0.25F, 8.1F, -4.0F, 8, 12, 8, -0.2F, true));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 18, 48, -7.0F, 10.0F, -4.5F, 7, 8, 1, -0.2F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 18, 48, 0.0F, 10.0F, -4.5F, 7, 8, 1, -0.2F, true));
			chest = new ModelRenderer(this);
			chest.setRotationPoint(1.0F, 7.0F, 4.0F);
			bipedBody.addChild(chest);
			setRotationAngle(chest, 0.5236F, 0.0F, 0.0F);
			chest.cubeList.add(new ModelBox(chest, 0, 0, -9.0F, -11.0F, -8.0F, 8, 12, 8, 0.0F, false));
			chest.cubeList.add(new ModelBox(chest, 0, 0, -1.0F, -11.0F, -8.0F, 8, 12, 8, 0.0F, true));
			chest.cubeList.add(new ModelBox(chest, 0, 48, -9.0F, -10.0F, -8.75F, 8, 8, 1, -0.2F, false));
			chest.cubeList.add(new ModelBox(chest, 0, 48, -1.0F, -10.0F, -8.75F, 8, 8, 1, -0.2F, true));
			hump = new ModelRenderer(this);
			hump.setRotationPoint(-1.0F, -6.0F, -2.0F);
			chest.addChild(hump);
			setRotationAngle(hump, -0.5236F, -0.6981F, 0.3491F);
			hump.cubeList.add(new ModelBox(hump, 0, 20, -4.0F, -6.0F, -4.0F, 8, 6, 8, 0.0F, false));
			hump.cubeList.add(new ModelBox(hump, 0, 20, -3.0F, -1.0F, -3.0F, 8, 6, 8, -1.0F, false));
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-7.0F, 6.0F, -5.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 5, -1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
			upperArmRight = new ModelRenderer(this);
			upperArmRight.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedRightArm.addChild(upperArmRight);
			setRotationAngle(upperArmRight, 0.0F, 0.0F, 0.3491F);
			upperArmRight.cubeList.add(new ModelBox(upperArmRight, 32, 0, -6.0F, -2.0F, -3.0F, 6, 12, 6, 0.0F, false));
			foreArmRight = new ModelRenderer(this);
			foreArmRight.setRotationPoint(-3.0F, 8.0F, 2.0F);
			upperArmRight.addChild(foreArmRight);
			setRotationAngle(foreArmRight, -0.5236F, 0.0F, -0.5236F);
			foreArmRight.cubeList.add(new ModelBox(foreArmRight, 26, 28, -3.0F, 0.0F, -5.0F, 6, 14, 6, -0.2F, false));
			foreArmRight.cubeList.add(new ModelBox(foreArmRight, 0, 0, -2.75F, -2.0F, -1.0F, 2, 3, 2, 0.0F, false));
			foreArmRight.cubeList.add(new ModelBox(foreArmRight, 0, 0, -2.75F, -4.0F, -1.0F, 2, 3, 2, -0.2F, false));
			foreArmRight.cubeList.add(new ModelBox(foreArmRight, 0, 0, -2.75F, -6.0F, -1.0F, 2, 3, 2, -0.4F, false));
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(7.0F, 6.0F, -5.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 5, 0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, true));
			upperArmLeft = new ModelRenderer(this);
			upperArmLeft.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedLeftArm.addChild(upperArmLeft);
			setRotationAngle(upperArmLeft, 0.0F, 0.0F, -0.3491F);
			upperArmLeft.cubeList.add(new ModelBox(upperArmLeft, 32, 0, 0.0F, -2.0F, -3.0F, 6, 12, 6, 0.0F, true));
			foreArmLeft = new ModelRenderer(this);
			foreArmLeft.setRotationPoint(3.0F, 8.0F, 2.0F);
			upperArmLeft.addChild(foreArmLeft);
			setRotationAngle(foreArmLeft, -0.5236F, 0.0F, 0.5236F);
			foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 26, 28, -3.0F, 0.0F, -5.0F, 6, 14, 6, -0.2F, true));
			foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 0, 0, 0.75F, -2.0F, -1.0F, 2, 3, 2, 0.0F, true));
			foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 0, 0, 0.75F, -4.0F, -1.0F, 2, 3, 2, -0.2F, true));
			foreArmLeft.cubeList.add(new ModelBox(foreArmLeft, 0, 0, 0.75F, -6.0F, -1.0F, 2, 3, 2, -0.4F, true));*/
			
			tail[0][0] = new ModelRenderer(this);
			tail[0][0].setRotationPoint(4.0F, 23.5F, 0.0F);
			setRotationAngle(tail[0][0], -1.4835F, 1.8326F, 0.0F);
			tail[0][0].cubeList.add(new ModelBox(tail[0][0], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));
			tail[0][1] = new ModelRenderer(this);
			tail[0][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[0][0].addChild(tail[0][1]);
			setRotationAngle(tail[0][1], 0.2618F, 0.0F, 0.0F);
			tail[0][1].cubeList.add(new ModelBox(tail[0][1], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));
			tail[0][2] = new ModelRenderer(this);
			tail[0][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[0][1].addChild(tail[0][2]);
			setRotationAngle(tail[0][2], 0.2618F, 0.0F, 0.0F);
			tail[0][2].cubeList.add(new ModelBox(tail[0][2], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));
			tail[0][3] = new ModelRenderer(this);
			tail[0][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[0][2].addChild(tail[0][3]);
			setRotationAngle(tail[0][3], 0.2618F, 0.0F, 0.0F);
			tail[0][3].cubeList.add(new ModelBox(tail[0][3], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));
			tail[0][4] = new ModelRenderer(this);
			tail[0][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[0][3].addChild(tail[0][4]);
			setRotationAngle(tail[0][4], 0.2618F, 0.0F, 0.0F);
			tail[0][4].cubeList.add(new ModelBox(tail[0][4], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));
			tail[0][5] = new ModelRenderer(this);
			tail[0][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[0][4].addChild(tail[0][5]);
			setRotationAngle(tail[0][5], 0.2618F, 0.0F, 0.0F);
			tail[0][5].cubeList.add(new ModelBox(tail[0][5], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));
			tail[0][6] = new ModelRenderer(this);
			tail[0][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[0][5].addChild(tail[0][6]);
			setRotationAngle(tail[0][6], 0.2618F, 0.0F, 0.0F);
			tail[0][6].cubeList.add(new ModelBox(tail[0][6], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));
			tail[0][7] = new ModelRenderer(this);
			tail[0][7].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[0][6].addChild(tail[0][7]);
			setRotationAngle(tail[0][7], 0.2618F, 0.0F, 0.0F);
			tail[0][7].cubeList.add(new ModelBox(tail[0][7], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));
			tail[1][0] = new ModelRenderer(this);
			tail[1][0].setRotationPoint(3.0F, 23.5F, 0.0F);
			setRotationAngle(tail[1][0], -1.4835F, 1.309F, 0.0F);
			tail[1][0].cubeList.add(new ModelBox(tail[1][0], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));
			tail[1][1] = new ModelRenderer(this);
			tail[1][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[1][0].addChild(tail[1][1]);
			setRotationAngle(tail[1][1], 0.2618F, 0.0F, 0.0F);
			tail[1][1].cubeList.add(new ModelBox(tail[1][1], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));
			tail[1][2] = new ModelRenderer(this);
			tail[1][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[1][1].addChild(tail[1][2]);
			setRotationAngle(tail[1][2], 0.2618F, 0.0F, 0.0F);
			tail[1][2].cubeList.add(new ModelBox(tail[1][2], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));
			tail[1][3] = new ModelRenderer(this);
			tail[1][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[1][2].addChild(tail[1][3]);
			setRotationAngle(tail[1][3], 0.2618F, 0.0F, 0.0F);
			tail[1][3].cubeList.add(new ModelBox(tail[1][3], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));
			tail[1][4] = new ModelRenderer(this);
			tail[1][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[1][3].addChild(tail[1][4]);
			setRotationAngle(tail[1][4], 0.2618F, 0.0F, 0.0F);
			tail[1][4].cubeList.add(new ModelBox(tail[1][4], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));
			tail[1][5] = new ModelRenderer(this);
			tail[1][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[1][4].addChild(tail[1][5]);
			setRotationAngle(tail[1][5], 0.2618F, 0.0F, 0.0F);
			tail[1][5].cubeList.add(new ModelBox(tail[1][5], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));
			tail[1][6] = new ModelRenderer(this);
			tail[1][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[1][5].addChild(tail[1][6]);
			setRotationAngle(tail[1][6], 0.2618F, 0.0F, 0.0F);
			tail[1][6].cubeList.add(new ModelBox(tail[1][6], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));
			tail[1][7] = new ModelRenderer(this);
			tail[1][7].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[1][6].addChild(tail[1][7]);
			setRotationAngle(tail[1][7], 0.2618F, 0.0F, 0.0F);
			tail[1][7].cubeList.add(new ModelBox(tail[1][7], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));
			tail[2][0] = new ModelRenderer(this);
			tail[2][0].setRotationPoint(2.0F, 23.5F, 0.0F);
			setRotationAngle(tail[2][0], -1.4835F, 0.7854F, 0.0F);
			tail[2][0].cubeList.add(new ModelBox(tail[2][0], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));
			tail[2][1] = new ModelRenderer(this);
			tail[2][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[2][0].addChild(tail[2][1]);
			setRotationAngle(tail[2][1], 0.2618F, 0.0F, 0.0F);
			tail[2][1].cubeList.add(new ModelBox(tail[2][1], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));
			tail[2][2] = new ModelRenderer(this);
			tail[2][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[2][1].addChild(tail[2][2]);
			setRotationAngle(tail[2][2], 0.2618F, 0.0F, 0.0F);
			tail[2][2].cubeList.add(new ModelBox(tail[2][2], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));
			tail[2][3] = new ModelRenderer(this);
			tail[2][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[2][2].addChild(tail[2][3]);
			setRotationAngle(tail[2][3], 0.2618F, 0.0F, 0.0F);
			tail[2][3].cubeList.add(new ModelBox(tail[2][3], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));
			tail[2][4] = new ModelRenderer(this);
			tail[2][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[2][3].addChild(tail[2][4]);
			setRotationAngle(tail[2][4], 0.2618F, 0.0F, 0.0F);
			tail[2][4].cubeList.add(new ModelBox(tail[2][4], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));
			tail[2][5] = new ModelRenderer(this);
			tail[2][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[2][4].addChild(tail[2][5]);
			setRotationAngle(tail[2][5], 0.2618F, 0.0F, 0.0F);
			tail[2][5].cubeList.add(new ModelBox(tail[2][5], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));
			tail[2][6] = new ModelRenderer(this);
			tail[2][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[2][5].addChild(tail[2][6]);
			setRotationAngle(tail[2][6], 0.2618F, 0.0F, 0.0F);
			tail[2][6].cubeList.add(new ModelBox(tail[2][6], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));
			tail[2][7] = new ModelRenderer(this);
			tail[2][7].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[2][6].addChild(tail[2][7]);
			setRotationAngle(tail[2][7], 0.2618F, 0.0F, 0.0F);
			tail[2][7].cubeList.add(new ModelBox(tail[2][7], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));
			tail[3][0] = new ModelRenderer(this);
			tail[3][0].setRotationPoint(1.0F, 23.5F, 0.0F);
			setRotationAngle(tail[3][0], -1.4835F, 0.2618F, 0.0F);
			tail[3][0].cubeList.add(new ModelBox(tail[3][0], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));
			tail[3][1] = new ModelRenderer(this);
			tail[3][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[3][0].addChild(tail[3][1]);
			setRotationAngle(tail[3][1], 0.2618F, 0.0F, 0.0F);
			tail[3][1].cubeList.add(new ModelBox(tail[3][1], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));
			tail[3][2] = new ModelRenderer(this);
			tail[3][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[3][1].addChild(tail[3][2]);
			setRotationAngle(tail[3][2], 0.2618F, 0.0F, 0.0F);
			tail[3][2].cubeList.add(new ModelBox(tail[3][2], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));
			tail[3][3] = new ModelRenderer(this);
			tail[3][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[3][2].addChild(tail[3][3]);
			setRotationAngle(tail[3][3], 0.2618F, 0.0F, 0.0F);
			tail[3][3].cubeList.add(new ModelBox(tail[3][3], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));
			tail[3][4] = new ModelRenderer(this);
			tail[3][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[3][3].addChild(tail[3][4]);
			setRotationAngle(tail[3][4], 0.2618F, 0.0F, 0.0F);
			tail[3][4].cubeList.add(new ModelBox(tail[3][4], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));
			tail[3][5] = new ModelRenderer(this);
			tail[3][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[3][4].addChild(tail[3][5]);
			setRotationAngle(tail[3][5], 0.2618F, 0.0F, 0.0F);
			tail[3][5].cubeList.add(new ModelBox(tail[3][5], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));
			tail[3][6] = new ModelRenderer(this);
			tail[3][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[3][5].addChild(tail[3][6]);
			setRotationAngle(tail[3][6], 0.2618F, 0.0F, 0.0F);
			tail[3][6].cubeList.add(new ModelBox(tail[3][6], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));
			tail[3][7] = new ModelRenderer(this);
			tail[3][7].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[3][6].addChild(tail[3][7]);
			setRotationAngle(tail[3][7], 0.2618F, 0.0F, 0.0F);
			tail[3][7].cubeList.add(new ModelBox(tail[3][7], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));
			tail[4][0] = new ModelRenderer(this);
			tail[4][0].setRotationPoint(-1.0F, 23.5F, 0.0F);
			setRotationAngle(tail[4][0], -1.4835F, -0.2618F, 0.0F);
			tail[4][0].cubeList.add(new ModelBox(tail[4][0], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));
			tail[4][1] = new ModelRenderer(this);
			tail[4][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[4][0].addChild(tail[4][1]);
			setRotationAngle(tail[4][1], 0.2618F, 0.0F, 0.0F);
			tail[4][1].cubeList.add(new ModelBox(tail[4][1], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));
			tail[4][2] = new ModelRenderer(this);
			tail[4][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[4][1].addChild(tail[4][2]);
			setRotationAngle(tail[4][2], 0.2618F, 0.0F, 0.0F);
			tail[4][2].cubeList.add(new ModelBox(tail[4][2], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));
			tail[4][3] = new ModelRenderer(this);
			tail[4][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[4][2].addChild(tail[4][3]);
			setRotationAngle(tail[4][3], 0.2618F, 0.0F, 0.0F);
			tail[4][3].cubeList.add(new ModelBox(tail[4][3], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));
			tail[4][4] = new ModelRenderer(this);
			tail[4][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[4][3].addChild(tail[4][4]);
			setRotationAngle(tail[4][4], 0.2618F, 0.0F, 0.0F);
			tail[4][4].cubeList.add(new ModelBox(tail[4][4], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));
			tail[4][5] = new ModelRenderer(this);
			tail[4][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[4][4].addChild(tail[4][5]);
			setRotationAngle(tail[4][5], 0.2618F, 0.0F, 0.0F);
			tail[4][5].cubeList.add(new ModelBox(tail[4][5], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));
			tail[4][6] = new ModelRenderer(this);
			tail[4][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[4][5].addChild(tail[4][6]);
			setRotationAngle(tail[4][6], 0.2618F, 0.0F, 0.0F);
			tail[4][6].cubeList.add(new ModelBox(tail[4][6], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));
			tail[4][7] = new ModelRenderer(this);
			tail[4][7].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[4][6].addChild(tail[4][7]);
			setRotationAngle(tail[4][7], 0.2618F, 0.0F, 0.0F);
			tail[4][7].cubeList.add(new ModelBox(tail[4][7], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));
			tail[5][0] = new ModelRenderer(this);
			tail[5][0].setRotationPoint(-2.0F, 23.5F, 0.0F);
			setRotationAngle(tail[5][0], -1.4835F, -0.7854F, 0.0F);
			tail[5][0].cubeList.add(new ModelBox(tail[5][0], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));
			tail[5][1] = new ModelRenderer(this);
			tail[5][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[5][0].addChild(tail[5][1]);
			setRotationAngle(tail[5][1], 0.2618F, 0.0F, 0.0F);
			tail[5][1].cubeList.add(new ModelBox(tail[5][1], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));
			tail[5][2] = new ModelRenderer(this);
			tail[5][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[5][1].addChild(tail[5][2]);
			setRotationAngle(tail[5][2], 0.2618F, 0.0F, 0.0F);
			tail[5][2].cubeList.add(new ModelBox(tail[5][2], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));
			tail[5][3] = new ModelRenderer(this);
			tail[5][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[5][2].addChild(tail[5][3]);
			setRotationAngle(tail[5][3], 0.2618F, 0.0F, 0.0F);
			tail[5][3].cubeList.add(new ModelBox(tail[5][3], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));
			tail[5][4] = new ModelRenderer(this);
			tail[5][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[5][3].addChild(tail[5][4]);
			setRotationAngle(tail[5][4], 0.2618F, 0.0F, 0.0F);
			tail[5][4].cubeList.add(new ModelBox(tail[5][4], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));
			tail[5][5] = new ModelRenderer(this);
			tail[5][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[5][4].addChild(tail[5][5]);
			setRotationAngle(tail[5][5], 0.2618F, 0.0F, 0.0F);
			tail[5][5].cubeList.add(new ModelBox(tail[5][5], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));
			tail[5][6] = new ModelRenderer(this);
			tail[5][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[5][5].addChild(tail[5][6]);
			setRotationAngle(tail[5][6], 0.2618F, 0.0F, 0.0F);
			tail[5][6].cubeList.add(new ModelBox(tail[5][6], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));
			tail[5][7] = new ModelRenderer(this);
			tail[5][7].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[5][6].addChild(tail[5][7]);
			setRotationAngle(tail[5][7], 0.2618F, 0.0F, 0.0F);
			tail[5][7].cubeList.add(new ModelBox(tail[5][7], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));
			tail[6][0] = new ModelRenderer(this);
			tail[6][0].setRotationPoint(-3.0F, 23.5F, 0.0F);
			setRotationAngle(tail[6][0], -1.4835F, -1.309F, 0.0F);
			tail[6][0].cubeList.add(new ModelBox(tail[6][0], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));
			tail[6][1] = new ModelRenderer(this);
			tail[6][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[6][0].addChild(tail[6][1]);
			setRotationAngle(tail[6][1], 0.2618F, 0.0F, 0.0F);
			tail[6][1].cubeList.add(new ModelBox(tail[6][1], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));
			tail[6][2] = new ModelRenderer(this);
			tail[6][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[6][1].addChild(tail[6][2]);
			setRotationAngle(tail[6][2], 0.2618F, 0.0F, 0.0F);
			tail[6][2].cubeList.add(new ModelBox(tail[6][2], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));
			tail[6][3] = new ModelRenderer(this);
			tail[6][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[6][2].addChild(tail[6][3]);
			setRotationAngle(tail[6][3], 0.2618F, 0.0F, 0.0F);
			tail[6][3].cubeList.add(new ModelBox(tail[6][3], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));
			tail[6][4] = new ModelRenderer(this);
			tail[6][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[6][3].addChild(tail[6][4]);
			setRotationAngle(tail[6][4], 0.2618F, 0.0F, 0.0F);
			tail[6][4].cubeList.add(new ModelBox(tail[6][4], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));
			tail[6][5] = new ModelRenderer(this);
			tail[6][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[6][4].addChild(tail[6][5]);
			setRotationAngle(tail[6][5], 0.2618F, 0.0F, 0.0F);
			tail[6][5].cubeList.add(new ModelBox(tail[6][5], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));
			tail[6][6] = new ModelRenderer(this);
			tail[6][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[6][5].addChild(tail[6][6]);
			setRotationAngle(tail[6][6], 0.2618F, 0.0F, 0.0F);
			tail[6][6].cubeList.add(new ModelBox(tail[6][6], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));
			tail[6][7] = new ModelRenderer(this);
			tail[6][7].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[6][6].addChild(tail[6][7]);
			setRotationAngle(tail[6][7], 0.2618F, 0.0F, 0.0F);
			tail[6][7].cubeList.add(new ModelBox(tail[6][7], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));
			tail[7][0] = new ModelRenderer(this);
			tail[7][0].setRotationPoint(-4.0F, 23.5F, 0.0F);
			setRotationAngle(tail[7][0], -1.4835F, -1.8326F, 0.0F);
			tail[7][0].cubeList.add(new ModelBox(tail[7][0], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 1.0F, false));
			tail[7][1] = new ModelRenderer(this);
			tail[7][1].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[7][0].addChild(tail[7][1]);
			setRotationAngle(tail[7][1], 0.2618F, 0.0F, 0.0F);
			tail[7][1].cubeList.add(new ModelBox(tail[7][1], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.9F, false));
			tail[7][2] = new ModelRenderer(this);
			tail[7][2].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[7][1].addChild(tail[7][2]);
			setRotationAngle(tail[7][2], 0.2618F, 0.0F, 0.0F);
			tail[7][2].cubeList.add(new ModelBox(tail[7][2], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.8F, false));
			tail[7][3] = new ModelRenderer(this);
			tail[7][3].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[7][2].addChild(tail[7][3]);
			setRotationAngle(tail[7][3], 0.2618F, 0.0F, 0.0F);
			tail[7][3].cubeList.add(new ModelBox(tail[7][3], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.7F, false));
			tail[7][4] = new ModelRenderer(this);
			tail[7][4].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[7][3].addChild(tail[7][4]);
			setRotationAngle(tail[7][4], 0.2618F, 0.0F, 0.0F);
			tail[7][4].cubeList.add(new ModelBox(tail[7][4], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.6F, false));
			tail[7][5] = new ModelRenderer(this);
			tail[7][5].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[7][4].addChild(tail[7][5]);
			setRotationAngle(tail[7][5], 0.2618F, 0.0F, 0.0F);
			tail[7][5].cubeList.add(new ModelBox(tail[7][5], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.5F, false));
			tail[7][6] = new ModelRenderer(this);
			tail[7][6].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[7][5].addChild(tail[7][6]);
			setRotationAngle(tail[7][6], 0.2618F, 0.0F, 0.0F);
			tail[7][6].cubeList.add(new ModelBox(tail[7][6], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, 0.0F, false));
			tail[7][7] = new ModelRenderer(this);
			tail[7][7].setRotationPoint(0.0F, -7.0F, 0.0F);
			tail[7][6].addChild(tail[7][7]);
			setRotationAngle(tail[7][7], 0.2618F, 0.0F, 0.0F);
			tail[7][7].cubeList.add(new ModelBox(tail[7][7], 44, 18, -2.0F, -7.5F, -2.0F, 4, 8, 4, -0.4F, false));

			for (int i = 0; i < 8; i++) {
				for (int j = 1; j < 8; j++) {
					tailSwayX[i][j] = (rand.nextFloat() * 0.1309F + 0.1309F) * MathHelper.sqrt((float)j) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayZ[i][j] = (rand.nextFloat() * 0.1309F + 0.1309F) * MathHelper.sqrt((float)j) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayY[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F);
				}
			}
		}

		@Override
		public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
			bipedRightLeg.showModel = false;
			bipedLeftLeg.showModel = false;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F);
			GlStateManager.translate(0.0F, 0.0F, 0.375F * MODELSCALE);
			GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
			bipedBody.render(f5);
			//super.render(entity, f0, f1, f2, f3, f4, f5);
			for (int i = 0; i < 8; i++) {
				tail[i][0].render(f5);
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
			bipedHead.rotationPointY += -19.5F;
			//bipedHeadwear.rotationPointY += 4.0F;
			bipedRightArm.rotationPointZ += -5.0F;
			bipedRightArm.rotationPointX += -2.0F;
			bipedLeftArm.rotationPointZ += -5.0F;
			bipedLeftArm.rotationPointX += 2.0F;
			for (int i = 0; i < 8; i++) {
				for (int j = 1; j < 8; j++) {
					tail[i][j].rotateAngleX = 0.2618F + MathHelper.sin((f2 - j) * 0.08F) * tailSwayX[i][j];
					tail[i][j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.08F) * tailSwayZ[i][j];
					tail[i][j].rotateAngleY = MathHelper.sin((f2 - j) * 0.08F) * tailSwayY[i][j];
				}
			}
			if (((EntityCustom)e).isShooting()) {
				bipedHead.rotateAngleX += -0.5236F;
				bipedHeadwear.rotateAngleX += -0.5236F;
				jaw.rotateAngleX = 1.0472F;
			} else {
				jaw.rotateAngleX = 0.0F;
			}
			if (((EntityCustom)e).isFaceDown()) {
				bipedBody.rotationPointZ = 16.0F;
				bipedBody.rotateAngleX = 1.0472F;
				bipedHead.rotateAngleX = -0.2618F;
				bipedRightArm.rotateAngleX = -2.0944F;
				bipedLeftArm.rotateAngleX = -2.0944F;
				for (int i = 0; i < 8; i++) {
					tail[i][0].rotationPointZ = 16.0F;
				}
			} else {
				bipedBody.rotationPointZ = 0.0F;
				bipedBody.rotateAngleX = 0.0F;
				for (int i = 0; i < 8; i++) {
					tail[i][0].rotationPointZ = 0.0F;
				}
			}
			this.copyModelAngles(bipedBody, bipedHeadwear);
			this.copyModelAngles(bipedHead, eyes);
		}
	}
}
