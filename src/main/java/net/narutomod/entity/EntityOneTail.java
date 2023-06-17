
package net.narutomod.entity;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import net.narutomod.item.ItemJiton;
import net.narutomod.item.ItemDoton;
import net.narutomod.item.ItemFuton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityOneTail extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 259;
	public static final int ENTITYID_RANGED = 260;
	private static final float MODELSCALE = 19.0F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntityOneTail(ElementsNarutomodMod instance) {
		super(instance, 583);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "one_tail"), ENTITYID)
		 .name("one_tail").tracker(96, 3, true).egg(-3355648, -16737844).build());
	}

	public static TailBeastManager getBijuManager() {
		return tailBeastManager;
	}

	public static class TailBeastManager extends EntityBijuManager<EntityCustom> {
		public TailBeastManager() {
			super(EntityCustom.class, 1);
		}

		@Override
		public void setVesselEntity(@Nullable Entity player, boolean dirty) {
			super.setVesselEntity(player, dirty);
			if (player instanceof EntityPlayer && !ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemJiton.block)) {
				ItemStack stack = new ItemStack(ItemJiton.block);
				ItemJiton.setSandType(stack, ItemJiton.Type.SAND);
				ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, stack);
				if (!ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemFuton.block)) {
					ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, new ItemStack(ItemFuton.block));
				}
				if (!ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemDoton.block)) {
					ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, new ItemStack(ItemDoton.block));
				}
			}
		}

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}

	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_onetail";
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
			this.setSize(MODELSCALE * 0.4F, MODELSCALE * 1.0625F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.4F, MODELSCALE * 1.0625F);
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
			this.setSize(this.width, MODELSCALE * (down ? 0.625F : 1.0625F));
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
			return this.isFaceDown() ? 3.0d * 0.0625d * MODELSCALE : (double)this.height + 0.35D;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0.25d * MODELSCALE, 0d, 0d) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
				passenger.setPosition(this.posX + vec2.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec2.z);
			}
		}

		@Override
		public float getFuuinBeamHeight() {
			return this.isFaceDown() ? 3.0f * 0.0625f * MODELSCALE : super.getFuuinBeamHeight();
		}

		@Override
		public SoundEvent getAmbientSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:shukaku_roar"));
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public SoundEvent getDeathSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}
	}

	@SideOnly(Side.CLIENT)
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
		public class RenderCustom extends EntityTailedBeast.ClientOnly.Renderer<EntityCustom> {
			private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/onetail.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelOneTail(), MODELSCALE * 0.5F);
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
		public class ModelOneTail extends ModelBiped {
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer eyes;
			//private final ModelRenderer bipedBody;
			//private final ModelRenderer bipedHead;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer jaw;
			private final ModelRenderer bone3;
			private final ModelRenderer cube_r6;
			private final ModelRenderer bone4;
			private final ModelRenderer cube_r7;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer rightArm;
			private final ModelRenderer cube_r8;
			private final ModelRenderer cube_r9;
			private final ModelRenderer cube_r10;
			private final ModelRenderer bone;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			private final ModelRenderer cube_r13;
			private final ModelRenderer cube_r14;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer leftArm;
			private final ModelRenderer cube_r15;
			private final ModelRenderer cube_r16;
			private final ModelRenderer cube_r17;
			private final ModelRenderer bone2;
			private final ModelRenderer cube_r18;
			private final ModelRenderer cube_r19;
			private final ModelRenderer cube_r20;
			private final ModelRenderer cube_r21;
			private final ModelRenderer stomach;
			private final ModelRenderer cube_r22;
			private final ModelRenderer cube_r23;
			private final ModelRenderer upperbody;
			private final ModelRenderer cube_r24;
			private final ModelRenderer cube_r25;
			//private final ModelRenderer bipedRightLeg;
			private final ModelRenderer cube_r26;
			private final ModelRenderer rightFoot;
			private final ModelRenderer cube_r27;
			private final ModelRenderer cube_r28;
			private final ModelRenderer cube_r29;
			private final ModelRenderer cube_r30;
			private final ModelRenderer cube_r31;
			//private final ModelRenderer bipedLeftLeg;
			private final ModelRenderer cube_r32;
			private final ModelRenderer leftFoot;
			private final ModelRenderer cube_r33;
			private final ModelRenderer cube_r34;
			private final ModelRenderer cube_r35;
			private final ModelRenderer cube_r36;
			private final ModelRenderer cube_r37;

			private final ModelRenderer[] tail = new ModelRenderer[9];
			private final float tailSwayX[] = new float[tail.length];
			private final float tailSwayY[] = new float[tail.length];
			private final float tailSwayZ[] = new float[tail.length];
			private final Random rand = new Random();

			public ModelOneTail() {
				textureWidth = 64;
				textureHeight = 64;

				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 21.0F, 3.0F);
				
		
				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, -9.6F, -7.0F);
				bipedHeadwear.addChild(eyes);
				eyes.cubeList.add(new ModelBox(eyes, 41, 4, -1.5F, -3.0F, -4.7F, 3, 1, 0, 0.0F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 21.0F, 3.0F);
				
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -9.6F, -7.0F);
				bipedBody.addChild(bipedHead);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 52, 8, -1.5F, -0.72F, -5.4006F, 3, 0, 3, 0.0F, false));
		
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, -2.4F, -5.5F);
				bipedHead.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.3054F, 0.0F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 58, 2, -1.0F, 0.55F, -0.2921F, 2, 1, 1, 0.1F, false));
		
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(0.0F, 12.6F, -1.5F);
				bipedHead.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.1309F, 0.0F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 48, 0, -1.5F, -13.9F, -2.1F, 3, 0, 2, 0.0F, false));
				cube_r2.cubeList.add(new ModelBox(cube_r2, 48, 0, -1.5F, -14.9F, -2.1F, 3, 2, 2, 0.0F, false));
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(0.0F, 0.0F, -2.5F);
				bipedHead.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.1745F, 0.0F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 54, -2.0F, -3.2623F, -0.9368F, 4, 4, 3, -0.1F, false));
		
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(0.0F, -4.4193F, -0.8247F);
				bipedHead.addChild(cube_r4);
				setRotationAngle(cube_r4, -0.3491F, 0.0F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 50, 36, -2.0F, -0.0057F, 0.0F, 4, 4, 2, 0.0F, false));
		
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.0F, 13.0F, -1.5F);
				bipedHead.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.0873F, 0.0F, 0.0F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 32, 35, -2.0F, -17.3F, -1.8F, 4, 4, 4, 0.0F, false));
		
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, -0.7346F, -2.3706F);
				bipedHead.addChild(jaw);
				setRotationAngle(jaw, 0.5236F, 0.0F, 0.0F);
				jaw.cubeList.add(new ModelBox(jaw, 47, 13, -1.5F, 0.0F, -2.9F, 3, 1, 3, 0.2F, false));
				jaw.cubeList.add(new ModelBox(jaw, 47, 13, -1.5F, 0.3F, -2.9F, 3, 0, 3, 0.2F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-1.1F, -3.5F, -4.2F);
				bipedHead.addChild(bone3);
				setRotationAngle(bone3, 0.3491F, -0.1745F, 0.1745F);
				bone3.cubeList.add(new ModelBox(bone3, 58, 0, -1.1F, -0.6F, -0.8F, 2, 1, 1, 0.0F, false));
		
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(-0.8F, -0.3F, 0.1F);
				bone3.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.0F, -0.5236F, 0.0F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 6, -0.5F, -0.4F, -0.1F, 1, 1, 2, -0.1F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(1.1F, -3.5F, -4.2F);
				bipedHead.addChild(bone4);
				setRotationAngle(bone4, 0.3491F, 0.1745F, -0.1745F);
				bone4.cubeList.add(new ModelBox(bone4, 58, 0, -0.9F, -0.6F, -0.8F, 2, 1, 1, 0.0F, true));
		
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(0.8F, -0.3F, 0.1F);
				bone4.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.0F, 0.5236F, 0.0F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 6, -0.5F, -0.4F, -0.1F, 1, 1, 2, -0.1F, true));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-4.0F, -9.0F, -4.5F);
				bipedBody.addChild(bipedRightArm);
				
		
				rightArm = new ModelRenderer(this);
				rightArm.setRotationPoint(4.0F, -2.0F, -1.0F);
				bipedRightArm.addChild(rightArm);
				setRotationAngle(rightArm, -0.2182F, 0.0F, 0.0873F);
				
		
				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(-5.4741F, 1.5764F, -0.1412F);
				rightArm.addChild(cube_r8);
				setRotationAngle(cube_r8, 0.1309F, 0.0F, 0.7854F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 20, 39, -0.5F, -2.25F, -2.0F, 2, 3, 4, 0.5F, false));
		
				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
				rightArm.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.1309F, 0.0F, 0.2182F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 10, 37, -5.9032F, 2.6821F, -1.9955F, 2, 6, 3, 0.5F, false));
		
				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(0.0F, 7.0F, 2.0F);
				rightArm.addChild(cube_r10);
				setRotationAngle(cube_r10, -0.2618F, -0.0436F, 0.0F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 37, -7.5298F, 0.4835F, -2.5163F, 2, 7, 3, 0.6F, false));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-5.35F, 9.25F, -1.75F);
				rightArm.addChild(bone);
				setRotationAngle(bone, 0.0F, -0.3054F, 0.0F);
				
		
				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(-0.6F, 3.75F, 0.75F);
				bone.addChild(cube_r11);
				setRotationAngle(cube_r11, 0.8026F, -0.3011F, -0.4101F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 17, -0.029F, -1.1225F, -3.7507F, 1, 1, 3, 0.1F, false));
		
				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(-0.4F, 3.75F, 0.75F);
				bone.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.6953F, 0.3893F, 0.1347F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 17, -1.9419F, -1.0421F, -4.6535F, 1, 1, 3, 0.1F, false));
		
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(-0.6F, 3.75F, 0.75F);
				bone.addChild(cube_r13);
				setRotationAngle(cube_r13, 0.6525F, 0.2185F, -0.0078F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 17, -0.7793F, -1.0421F, -4.4548F, 1, 1, 3, 0.1F, false));
		
				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(-0.6F, 3.75F, 0.75F);
				bone.addChild(cube_r14);
				setRotationAngle(cube_r14, 0.6392F, 0.1139F, -0.0876F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 17, 0.3597F, -1.0421F, -4.536F, 1, 1, 3, 0.1F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(4.0F, -9.0F, -4.5F);
				bipedBody.addChild(bipedLeftArm);
				
		
				leftArm = new ModelRenderer(this);
				leftArm.setRotationPoint(-4.0F, -2.0F, -1.0F);
				bipedLeftArm.addChild(leftArm);
				setRotationAngle(leftArm, -0.2182F, 0.0F, -0.0873F);
				
		
				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(5.4741F, 1.5764F, -0.1412F);
				leftArm.addChild(cube_r15);
				setRotationAngle(cube_r15, 0.1309F, 0.0F, -0.7854F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 20, 39, -1.5F, -2.25F, -2.0F, 2, 3, 4, 0.5F, true));
		
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(0.0F, 0.0F, 0.0F);
				leftArm.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.1309F, 0.0F, -0.2182F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 10, 37, 3.9032F, 2.6821F, -1.9955F, 2, 6, 3, 0.5F, true));
		
				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(0.0F, 7.0F, 2.0F);
				leftArm.addChild(cube_r17);
				setRotationAngle(cube_r17, -0.2618F, 0.0436F, 0.0F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 0, 37, 5.5298F, 0.4835F, -2.5163F, 2, 7, 3, 0.6F, true));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(5.35F, 9.25F, -1.75F);
				leftArm.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.3054F, 0.0F);
				
		
				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(0.6F, 3.75F, 0.75F);
				bone2.addChild(cube_r18);
				setRotationAngle(cube_r18, 0.8026F, 0.3011F, 0.4101F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 17, -0.971F, -1.1225F, -3.7507F, 1, 1, 3, 0.1F, true));
		
				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(0.4F, 3.75F, 0.75F);
				bone2.addChild(cube_r19);
				setRotationAngle(cube_r19, 0.6953F, -0.3893F, -0.1347F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 17, 0.9419F, -1.0421F, -4.6535F, 1, 1, 3, 0.1F, true));
		
				cube_r20 = new ModelRenderer(this);
				cube_r20.setRotationPoint(0.6F, 3.75F, 0.75F);
				bone2.addChild(cube_r20);
				setRotationAngle(cube_r20, 0.6525F, -0.2185F, 0.0078F);
				cube_r20.cubeList.add(new ModelBox(cube_r20, 0, 17, -0.2207F, -1.0421F, -4.4548F, 1, 1, 3, 0.1F, true));
		
				cube_r21 = new ModelRenderer(this);
				cube_r21.setRotationPoint(0.6F, 3.75F, 0.75F);
				bone2.addChild(cube_r21);
				setRotationAngle(cube_r21, 0.6392F, -0.1139F, 0.0876F);
				cube_r21.cubeList.add(new ModelBox(cube_r21, 0, 17, -1.3597F, -1.0421F, -4.536F, 1, 1, 3, 0.1F, true));
		
				stomach = new ModelRenderer(this);
				stomach.setRotationPoint(0.0F, 3.0F, -6.6F);
				bipedBody.addChild(stomach);
				setRotationAngle(stomach, -0.2618F, 0.0F, 0.0F);
				
		
				cube_r22 = new ModelRenderer(this);
				cube_r22.setRotationPoint(0.0F, -12.4F, -3.7F);
				stomach.addChild(cube_r22);
				setRotationAngle(cube_r22, 0.2182F, 0.0F, 0.0F);
				cube_r22.cubeList.add(new ModelBox(cube_r22, 36, 8, -3.0F, 5.0769F, -1.8502F, 6, 7, 1, 0.0F, false));
		
				cube_r23 = new ModelRenderer(this);
				cube_r23.setRotationPoint(4.5F, -5.5F, 1.2F);
				stomach.addChild(cube_r23);
				setRotationAngle(cube_r23, 0.2182F, 0.0F, 0.0F);
				cube_r23.cubeList.add(new ModelBox(cube_r23, 0, 0, -9.0F, -3.7037F, -4.4076F, 9, 8, 9, 0.0F, false));
		
				upperbody = new ModelRenderer(this);
				upperbody.setRotationPoint(0.0F, 3.0F, -5.5F);
				bipedBody.addChild(upperbody);
				upperbody.cubeList.add(new ModelBox(upperbody, 24, 22, -4.0F, -14.8158F, -3.1398F, 8, 3, 8, 0.1F, false));
		
				cube_r24 = new ModelRenderer(this);
				cube_r24.setRotationPoint(0.0F, -15.1581F, 0.4507F);
				upperbody.addChild(cube_r24);
				setRotationAngle(cube_r24, -0.3054F, 0.0F, 0.0F);
				cube_r24.cubeList.add(new ModelBox(cube_r24, 7, 54, -4.0F, -1.0F, -3.3F, 8, 3, 7, -0.05F, false));
		
				cube_r25 = new ModelRenderer(this);
				cube_r25.setRotationPoint(0.0F, 0.0F, 0.0F);
				upperbody.addChild(cube_r25);
				setRotationAngle(cube_r25, 0.1745F, 0.0F, 0.0F);
				cube_r25.cubeList.add(new ModelBox(cube_r25, 0, 17, -4.0F, -12.0F, -0.9F, 8, 5, 8, 0.4F, false));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-4.5F, -3.0F, -2.75F);
				bipedBody.addChild(bipedRightLeg);
				
		
				cube_r26 = new ModelRenderer(this);
				cube_r26.setRotationPoint(5.5F, 6.0F, -1.75F);
				bipedRightLeg.addChild(cube_r26);
				setRotationAngle(cube_r26, -0.1289F, 0.0227F, 0.1731F);
				cube_r26.cubeList.add(new ModelBox(cube_r26, 44, 53, -9.0F, -6.0F, -1.0F, 5, 6, 5, 0.0F, false));
		
				rightFoot = new ModelRenderer(this);
				rightFoot.setRotationPoint(4.75F, 5.75F, -1.75F);
				bipedRightLeg.addChild(rightFoot);
				
		
				cube_r27 = new ModelRenderer(this);
				cube_r27.setRotationPoint(0.0F, -0.1F, 0.0F);
				rightFoot.addChild(cube_r27);
				setRotationAngle(cube_r27, 0.0894F, 0.2173F, 0.0193F);
				cube_r27.cubeList.add(new ModelBox(cube_r27, 27, 43, -8.0F, -0.75F, -4.3F, 1, 1, 5, 0.1F, false));
		
				cube_r28 = new ModelRenderer(this);
				cube_r28.setRotationPoint(-1.0F, -0.1F, 0.0F);
				rightFoot.addChild(cube_r28);
				setRotationAngle(cube_r28, 0.088F, 0.1304F, 0.0115F);
				cube_r28.cubeList.add(new ModelBox(cube_r28, 43, 38, -5.5F, -0.75F, -3.5F, 1, 1, 5, 0.1F, false));
		
				cube_r29 = new ModelRenderer(this);
				cube_r29.setRotationPoint(-1.0F, -0.1F, 0.0F);
				rightFoot.addChild(cube_r29);
				setRotationAngle(cube_r29, 0.0876F, 0.0869F, 0.0076F);
				cube_r29.cubeList.add(new ModelBox(cube_r29, 20, 46, -4.0F, -0.75F, -3.25F, 1, 1, 4, 0.1F, false));
		
				cube_r30 = new ModelRenderer(this);
				cube_r30.setRotationPoint(-1.0F, -0.1F, 0.0F);
				rightFoot.addChild(cube_r30);
				setRotationAngle(cube_r30, 0.1526F, -0.4332F, -0.053F);
				cube_r30.cubeList.add(new ModelBox(cube_r30, 0, 47, -1.95F, -0.8F, -1.1F, 1, 1, 4, 0.1F, false));
		
				cube_r31 = new ModelRenderer(this);
				cube_r31.setRotationPoint(0.0F, 0.0F, 0.0F);
				rightFoot.addChild(cube_r31);
				setRotationAngle(cube_r31, -0.0175F, 0.0F, 0.0F);
				cube_r31.cubeList.add(new ModelBox(cube_r31, 34, 44, -8.0F, -1.0F, 1.35F, 5, 1, 2, 0.2F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(4.5F, -3.0F, -2.75F);
				bipedBody.addChild(bipedLeftLeg);
				
		
				cube_r32 = new ModelRenderer(this);
				cube_r32.setRotationPoint(-5.5F, 6.0F, -1.75F);
				bipedLeftLeg.addChild(cube_r32);
				setRotationAngle(cube_r32, -0.1289F, -0.0227F, -0.1731F);
				cube_r32.cubeList.add(new ModelBox(cube_r32, 44, 53, 4.0F, -6.0F, -1.0F, 5, 6, 5, 0.0F, true));
		
				leftFoot = new ModelRenderer(this);
				leftFoot.setRotationPoint(-4.75F, 5.75F, -1.75F);
				bipedLeftLeg.addChild(leftFoot);
				
		
				cube_r33 = new ModelRenderer(this);
				cube_r33.setRotationPoint(0.0F, -0.1F, 0.0F);
				leftFoot.addChild(cube_r33);
				setRotationAngle(cube_r33, 0.0894F, -0.2173F, -0.0193F);
				cube_r33.cubeList.add(new ModelBox(cube_r33, 27, 43, 7.0F, -0.75F, -4.3F, 1, 1, 5, 0.1F, true));
		
				cube_r34 = new ModelRenderer(this);
				cube_r34.setRotationPoint(1.0F, -0.1F, 0.0F);
				leftFoot.addChild(cube_r34);
				setRotationAngle(cube_r34, 0.088F, -0.1304F, -0.0115F);
				cube_r34.cubeList.add(new ModelBox(cube_r34, 43, 38, 4.5F, -0.75F, -3.5F, 1, 1, 5, 0.1F, true));
		
				cube_r35 = new ModelRenderer(this);
				cube_r35.setRotationPoint(1.0F, -0.1F, 0.0F);
				leftFoot.addChild(cube_r35);
				setRotationAngle(cube_r35, 0.0876F, -0.0869F, -0.0076F);
				cube_r35.cubeList.add(new ModelBox(cube_r35, 20, 46, 3.0F, -0.75F, -3.25F, 1, 1, 4, 0.1F, true));
		
				cube_r36 = new ModelRenderer(this);
				cube_r36.setRotationPoint(1.0F, -0.1F, 0.0F);
				leftFoot.addChild(cube_r36);
				setRotationAngle(cube_r36, 0.1526F, 0.4332F, 0.053F);
				cube_r36.cubeList.add(new ModelBox(cube_r36, 0, 47, 0.95F, -0.8F, -1.1F, 1, 1, 4, 0.1F, true));
		
				cube_r37 = new ModelRenderer(this);
				cube_r37.setRotationPoint(0.0F, 0.0F, 0.0F);
				leftFoot.addChild(cube_r37);
				setRotationAngle(cube_r37, -0.0175F, 0.0F, 0.0F);
				cube_r37.cubeList.add(new ModelBox(cube_r37, 34, 44, 3.0F, -1.0F, 1.35F, 5, 1, 2, 0.2F, true));
		
				tail[0] = new ModelRenderer(this);
				tail[0].setRotationPoint(0.0F, 20.0F, 3.0F);
				setRotationAngle(tail[0], -1.0472F, 0.0F, 0.0F);
				tail[0].cubeList.add(new ModelBox(tail[0], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 2.2F, false));
		
				tail[1] = new ModelRenderer(this);
				tail[1].setRotationPoint(0.0F, -4.0F, 0.0F);
				tail[0].addChild(tail[1]);
				setRotationAngle(tail[1], 0.4363F, 0.0F, 0.2618F);
				tail[1].cubeList.add(new ModelBox(tail[1], 0, 0, -1.0F, -5.5F, -1.0F, 2, 4, 2, 2.0F, false));
		
				tail[2] = new ModelRenderer(this);
				tail[2].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[1].addChild(tail[2]);
				setRotationAngle(tail[2], 0.4363F, 0.0F, 0.2618F);
				tail[2].cubeList.add(new ModelBox(tail[2], 0, 0, -1.0F, -5.5F, -1.0F, 2, 4, 2, 1.8F, false));
		
				tail[3] = new ModelRenderer(this);
				tail[3].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[2].addChild(tail[3]);
				setRotationAngle(tail[3], 0.4363F, 0.0F, 0.2618F);
				tail[3].cubeList.add(new ModelBox(tail[3], 0, 0, -1.0F, -5.5F, -1.0F, 2, 4, 2, 1.6F, false));
		
				tail[4] = new ModelRenderer(this);
				tail[4].setRotationPoint(0.0F, -5.0F, 0.0F);
				tail[3].addChild(tail[4]);
				setRotationAngle(tail[4], 0.4363F, 0.0F, -0.2618F);
				tail[4].cubeList.add(new ModelBox(tail[4], 0, 0, -1.0F, -5.5F, -1.0F, 2, 4, 2, 1.4F, false));
		
				tail[5] = new ModelRenderer(this);
				tail[5].setRotationPoint(0.0F, -5.0F, 0.0F);
				tail[4].addChild(tail[5]);
				setRotationAngle(tail[5], 0.4363F, 0.0F, -0.2618F);
				tail[5].cubeList.add(new ModelBox(tail[5], 0, 0, -1.0F, -5.5F, -1.0F, 2, 4, 2, 1.2F, false));
		
				tail[6] = new ModelRenderer(this);
				tail[6].setRotationPoint(0.0F, -5.25F, 0.0F);
				tail[5].addChild(tail[6]);
				setRotationAngle(tail[6], 0.4363F, 0.0F, -0.2618F);
				tail[6].cubeList.add(new ModelBox(tail[6], 0, 0, -1.0F, -5.0F, -1.0F, 2, 4, 2, 0.6F, false));
		
				tail[7] = new ModelRenderer(this);
				tail[7].setRotationPoint(0.0F, -4.5F, 0.0F);
				tail[6].addChild(tail[7]);
				setRotationAngle(tail[7], 0.4363F, 0.0F, -0.2618F);
				tail[7].cubeList.add(new ModelBox(tail[7], 0, 0, -1.0F, -4.5F, -1.0F, 2, 4, 2, 0.0F, false));
		
				tail[8] = new ModelRenderer(this);
				tail[8].setRotationPoint(0.0F, -4.0F, 0.0F);
				tail[7].addChild(tail[8]);
				setRotationAngle(tail[8], 0.3491F, 0.0F, -0.1745F);
				tail[8].cubeList.add(new ModelBox(tail[8], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, -0.4F, false));

				for (int j = 1; j < tail.length; j++) {
					tailSwayX[j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayZ[j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayY[j] = (rand.nextFloat() * 0.0873F + 0.0873F);
				}
			}

			@Override
			public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F);
				//GlStateManager.translate(0.0F, 0.0F, 0.375F * MODELSCALE);
				GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
				//super.render(entity, f0, f1, f2, f3, f4, f5);
				bipedBody.render(f5);
				tail[0].render(f5);
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
				bipedHead.rotationPointY += -9.6F;
				bipedRightArm.setRotationPoint(-4.0F, -9.0F, -4.5F);
				bipedLeftArm.setRotationPoint(4.0F, -9.0F, -4.5F);
				bipedRightLeg.setRotationPoint(-4.5F, -3.0F, -3.75F);
				bipedLeftLeg.setRotationPoint(4.5F, -3.0F, -3.75F);
				for (int j = 1; j < tail.length; j++) {
					if (j <= 5) {
						tail[j].rotateAngleX = 0.2618F + MathHelper.sin((f2 - j) * 0.05F) * tailSwayX[j];
					} else {
						tail[j].rotateAngleX = 0.2618F + MathHelper.sin((f2 - j) * 0.05F) * tailSwayX[j];
					}
					tail[j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.05F) * tailSwayZ[j];
					tail[j].rotateAngleY = MathHelper.sin((f2 - j) * 0.1F) * tailSwayY[j];
				}
				if (((EntityCustom)e).isShooting()) {
					bipedHead.rotateAngleX += -0.5236F;
					jaw.rotateAngleX = 0.7854F;
				} else {
					jaw.rotateAngleX = 0.0F;
				}
				if (((EntityCustom)e).isFaceDown()) {
					bipedBody.rotationPointZ = 10.0F;
					bipedBody.rotateAngleX = 0.8727F;
					bipedHead.rotateAngleX = -0.2618F;
					bipedRightArm.rotateAngleX = -1.3963F;
					bipedLeftArm.rotateAngleX = -1.3963F;
					bipedRightLeg.rotateAngleX = 0.8727F;
					bipedLeftLeg.rotateAngleX = 0.8727F;
					tail[0].rotationPointZ = 10.0F;
					tail[0].rotateAngleX = -1.4835F;
				} else {
					bipedBody.rotationPointZ = 3.0F;
					bipedBody.rotateAngleX = 0.0F;
					tail[0].rotationPointZ = 3.0F;
					tail[0].rotateAngleX = -1.0472F;
				}
				this.copyModelAngles(bipedBody, bipedHeadwear);
				this.copyModelAngles(bipedHead, eyes);
			}
		}
	}
}
