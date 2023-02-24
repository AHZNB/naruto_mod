
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.ItemHandlerHelper;

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
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemYooton;
import net.narutomod.item.ItemKaton;
import net.narutomod.item.ItemDoton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFourTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 255;
	public static final int ENTITYID_RANGED = 256;
	private static final float MODELSCALE = 20.0F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntityFourTails(ElementsNarutomodMod instance) {
		super(instance, 581);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "four_tails"), ENTITYID).name("four_tails")
		 .tracker(96, 3, true).egg(-3407872, -6697984).build());
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
			super(EntityCustom.class, 4);
		}

		@Override
		public void setVesselEntity(@Nullable Entity player) {
			super.setVesselEntity(player);
			if (player instanceof EntityPlayer && !ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemYooton.block)) {
				ItemStack stack = new ItemStack(ItemYooton.block);
				ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, stack);
				((ItemYooton.RangedItem)stack.getItem()).enableJutsu(stack, ItemYooton.CHAKRAMODE, true);
				if (!ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemKaton.block)) {
					ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, new ItemStack(ItemKaton.block));
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
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_fourtails";
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
			this.setSize(MODELSCALE * 0.4F, MODELSCALE * 1.0F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.4F, MODELSCALE * 1.0F);
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
			this.setSize(this.width, MODELSCALE * (down ? 0.75F : 1.0F));
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
			return this.isFaceDown() ? 2.0d * 0.0625d * MODELSCALE : (double)this.height + 0.35D;
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
			return this.isFaceDown() ? 2.0f * 0.0625f * MODELSCALE : super.getFuuinBeamHeight();
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
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/fourtails.png");

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn, new ModelFourTails(), MODELSCALE * 0.5F);
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
	public class ModelFourTails extends ModelBiped {
		//private final ModelRenderer bipedHeadwear;
		private final ModelRenderer eyes;
		//private final ModelRenderer bipedBody;
		//private final ModelRenderer bipedHead;
		private final ModelRenderer bone;
		private final ModelRenderer cube_r3;
		private final ModelRenderer cube_r5;
		private final ModelRenderer bone2;
		private final ModelRenderer cube_r2;
		private final ModelRenderer cube_r4;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r6;
		private final ModelRenderer tooth1;
		private final ModelRenderer cube_r7;
		private final ModelRenderer tooth2;
		private final ModelRenderer cube_r8;
		private final ModelRenderer Jaw;
		private final ModelRenderer cube_r9;
		private final ModelRenderer cube_r10;
		private final ModelRenderer pec6_r1;
		private final ModelRenderer pec2_r1;
		//private final ModelRenderer bipedRIghtArm;
		private final ModelRenderer bone4;
		private final ModelRenderer cube_r11;
		private final ModelRenderer cube_r12;
		private final ModelRenderer cube_r13;
		private final ModelRenderer cube_r14;
		private final ModelRenderer cube_r15;
		private final ModelRenderer cube_r16;
		private final ModelRenderer cube_r17;
		private final ModelRenderer cube_r18;
		private final ModelRenderer cube_r19;
		//private final ModelRenderer bipedLeftArm;
		private final ModelRenderer bone6;
		private final ModelRenderer cube_r26;
		private final ModelRenderer cube_r27;
		private final ModelRenderer cube_r28;
		private final ModelRenderer cube_r29;
		private final ModelRenderer cube_r30;
		private final ModelRenderer cube_r31;
		private final ModelRenderer cube_r32;
		private final ModelRenderer cube_r33;
		private final ModelRenderer cube_r34;
		//private final ModelRenderer bipedRightLeg;
		private final ModelRenderer cube_r20;
		private final ModelRenderer cube_r21;
		private final ModelRenderer cube_r22;
		private final ModelRenderer cube_r23;
		private final ModelRenderer cube_r24;
		private final ModelRenderer cube_r25;
		//private final ModelRenderer bipedLeftLeg;
		private final ModelRenderer cube_r35;
		private final ModelRenderer cube_r36;
		private final ModelRenderer cube_r37;
		private final ModelRenderer cube_r38;
		private final ModelRenderer cube_r39;
		private final ModelRenderer cube_r40;
		private final ModelRenderer tails;
		private final ModelRenderer[][] tail = new ModelRenderer[4][6];
		private final float tailSwayX[][] = new float[4][6];
		private final float tailSwayY[][] = new float[4][6];
		private final float tailSwayZ[][] = new float[4][6];
		private final Random rand = new Random();

		public ModelFourTails() {
			textureWidth = 64;
			textureHeight = 64;

			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 19.5F, 6.75F);
			
	
			eyes = new ModelRenderer(this);
			eyes.setRotationPoint(0.0F, -6.5F, -9.75F);
			bipedHeadwear.addChild(eyes);
			eyes.cubeList.add(new ModelBox(eyes, 5, 54, -1.9F, -2.5F, -2.25F, 4, 1, 0, 0.0F, false));
	
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 19.5F, 6.75F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 28, -2.5F, -6.5F, -8.75F, 5, 3, 4, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 22, 11, -2.5F, -3.5F, -8.25F, 5, 1, 4, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 38, 20, -2.5F, -2.5F, -7.35F, 5, 1, 3, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 36, 8, -2.5F, -1.5F, -6.35F, 5, 1, 3, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 44, 14, -2.5F, -6.5F, -9.75F, 5, 1, 1, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 44, 12, -2.5F, -5.5F, -9.35F, 5, 1, 1, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 9, -5.05F, -9.8F, -7.95F, 5, 5, 6, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 9, 0.05F, -9.8F, -7.95F, 5, 5, 6, 0.0F, true));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -4.0F, -11.0F, -7.75F, 8, 4, 5, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 18, 16, -3.0F, -8.7F, -4.95F, 6, 8, 4, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 18, 1, -4.1F, -5.5F, -8.75F, 2, 2, 8, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 18, 1, 2.1F, -5.5F, -8.75F, 2, 2, 8, 0.0F, true));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 20, -3.9F, -3.5F, -8.05F, 2, 1, 7, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 20, 1.9F, -3.5F, -8.05F, 2, 1, 7, 0.0F, true));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 30, 0, -3.0F, -7.5F, -1.0F, 6, 7, 1, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 35, -3.0F, -6.5F, 0.0F, 6, 6, 1, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 26, 39, -2.5F, -5.5F, 1.0F, 5, 6, 1, 0.0F, false));
	
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, -6.5F, -9.75F);
			bipedBody.addChild(bipedHead);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 18, 28, -1.9F, -4.0F, -2.2F, 4, 4, 4, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 21, 0, 1.4F, -1.3F, -2.5F, 1, 3, 1, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -2.35F, -1.3F, -2.5F, 1, 3, 1, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 48, 46, -0.9F, -1.3F, -2.4F, 2, 1, 1, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 6, 55, -1.5F, -0.05F, -2.3F, 3, 1, 1, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 53, 27, -2.0F, 1.35F, -2.5F, 1, 1, 1, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 25, 52, -1.75F, 1.85F, -2.0F, 1, 1, 1, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 52, 16, 0.75F, 1.85F, -2.0F, 1, 1, 1, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 51, 52, 1.0F, 1.35F, -2.5F, 1, 1, 1, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 27, 46, -1.5F, -0.3F, -1.75F, 3, 2, 1, 0.0F, false));
	
			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.0F, 11.95F, 2.75F);
			bipedHead.addChild(bone);
			bone.cubeList.add(new ModelBox(bone, 53, 45, -1.2F, -15.7F, -5.5F, 1, 1, 1, 0.0F, false));
			bone.cubeList.add(new ModelBox(bone, 28, 50, -2.2F, -15.5F, -5.4F, 2, 1, 1, 0.0F, false));
	
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(-3.3F, -15.0F, -4.9F);
			bone.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.0F, 0.0F, 0.1745F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 22, 50, -0.2F, -0.8F, -0.5F, 2, 1, 1, 0.0F, false));
	
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(-3.1F, -15.6F, -4.9F);
			bone.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.0F, 0.0F, 1.1781F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 50, -1.4F, -0.25F, -0.5F, 2, 1, 1, -0.1F, false));
	
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 11.95F, 2.75F);
			bipedHead.addChild(bone2);
			bone2.cubeList.add(new ModelBox(bone2, 53, 45, 0.2F, -15.7F, -5.5F, 1, 1, 1, 0.0F, true));
			bone2.cubeList.add(new ModelBox(bone2, 28, 50, 0.2F, -15.5F, -5.4F, 2, 1, 1, 0.0F, true));
	
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(3.3F, -15.0F, -4.9F);
			bone2.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.0F, 0.0F, -0.1745F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 22, 50, -1.8F, -0.8F, -0.5F, 2, 1, 1, 0.0F, true));
	
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(3.1F, -15.6F, -4.9F);
			bone2.addChild(cube_r4);
			setRotationAngle(cube_r4, 0.0F, 0.0F, -1.1781F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 50, -0.6F, -0.25F, -0.5F, 2, 1, 1, -0.1F, true));
	
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(1.7162F, -0.7121F, -2.5F);
			bipedHead.addChild(cube_r1);
			setRotationAngle(cube_r1, 0.0F, 0.0F, 0.7854F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 53, 29, -2.75F, 1.4F, 0.0F, 1, 1, 1, 0.0F, false));
			cube_r1.cubeList.add(new ModelBox(cube_r1, 44, 53, -0.9F, -0.45F, 0.0F, 1, 1, 1, 0.0F, false));
	
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(2.1F, -2.4F, 0.1F);
			bipedHead.addChild(cube_r6);
			setRotationAngle(cube_r6, -0.1745F, 0.0F, 0.0F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 21, 52, -0.7F, -1.0F, -0.5F, 1, 2, 1, 0.0F, true));
			cube_r6.cubeList.add(new ModelBox(cube_r6, 21, 52, -4.3F, -1.0F, -0.5F, 1, 2, 1, 0.0F, false));
	
			tooth1 = new ModelRenderer(this);
			tooth1.setRotationPoint(-1.0F, 0.7F, -3.0F);
			bipedHead.addChild(tooth1);
			setRotationAngle(tooth1, -0.0873F, 0.0F, 0.0F);
			
	
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
			tooth1.addChild(cube_r7);
			setRotationAngle(cube_r7, -0.1745F, 0.1745F, 0.0F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 52, 43, -0.5F, -0.5F, 0.0F, 1, 1, 1, -0.2F, false));
			cube_r7.cubeList.add(new ModelBox(cube_r7, 50, 41, -0.5F, 0.05F, 0.0F, 1, 1, 1, -0.25F, false));
			cube_r7.cubeList.add(new ModelBox(cube_r7, 37, 48, -0.5F, 0.45F, 0.0F, 1, 1, 1, -0.3F, false));
	
			tooth2 = new ModelRenderer(this);
			tooth2.setRotationPoint(1.0F, 0.7F, -3.0F);
			bipedHead.addChild(tooth2);
			setRotationAngle(tooth2, -0.0873F, 0.0F, 0.0F);
			
	
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
			tooth2.addChild(cube_r8);
			setRotationAngle(cube_r8, -0.1745F, -0.1745F, 0.0F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 52, 43, -0.5F, -0.5F, 0.0F, 1, 1, 1, -0.2F, true));
			cube_r8.cubeList.add(new ModelBox(cube_r8, 50, 41, -0.5F, 0.05F, 0.0F, 1, 1, 1, -0.25F, true));
			cube_r8.cubeList.add(new ModelBox(cube_r8, 37, 48, -0.5F, 0.45F, 0.0F, 1, 1, 1, -0.3F, true));
	
			Jaw = new ModelRenderer(this);
			Jaw.setRotationPoint(0.0F, -0.3F, 0.0F);
			bipedHead.addChild(Jaw);
			Jaw.cubeList.add(new ModelBox(Jaw, 30, 28, -1.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F, false));
			Jaw.cubeList.add(new ModelBox(Jaw, 0, 28, 0.5F, 0.0F, -1.0F, 1, 2, 1, 0.0F, false));
			Jaw.cubeList.add(new ModelBox(Jaw, 0, 48, -1.5021F, 1.6088F, -2.6F, 3, 1, 1, 0.0F, false));
	
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(0.0F, 3.0F, -2.0F);
			Jaw.addChild(cube_r9);
			setRotationAngle(cube_r9, 0.0F, 0.0F, -0.7854F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 20, -1.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F, false));
	
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(-1.0F, 5.5F, -4.0F);
			Jaw.addChild(cube_r10);
			setRotationAngle(cube_r10, 0.2182F, 0.0F, 0.0F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 36, 45, -0.5F, -3.5F, 3.0F, 3, 1, 2, 0.0F, false));
	
			pec6_r1 = new ModelRenderer(this);
			pec6_r1.setRotationPoint(1.5F, -4.5699F, -9.2086F);
			bipedBody.addChild(pec6_r1);
			setRotationAngle(pec6_r1, 0.6545F, 0.0F, 0.0F);
			pec6_r1.cubeList.add(new ModelBox(pec6_r1, 49, 8, -1.5F, 3.0699F, -0.5414F, 2, 2, 1, -0.1F, true));
			pec6_r1.cubeList.add(new ModelBox(pec6_r1, 49, 8, -3.5F, 3.0699F, -0.5414F, 2, 2, 1, -0.1F, false));
			pec6_r1.cubeList.add(new ModelBox(pec6_r1, 47, 35, -1.6F, 1.0699F, -0.7914F, 3, 2, 1, -0.2F, true));
			pec6_r1.cubeList.add(new ModelBox(pec6_r1, 47, 35, -4.4F, 1.0699F, -0.7914F, 3, 2, 1, -0.2F, false));
	
			pec2_r1 = new ModelRenderer(this);
			pec2_r1.setRotationPoint(1.5F, -4.5F, -9.25F);
			bipedBody.addChild(pec2_r1);
			setRotationAngle(pec2_r1, 0.6545F, 0.0F, 0.0F);
			pec2_r1.cubeList.add(new ModelBox(pec2_r1, 47, 38, -1.5F, -1.0F, -0.5F, 3, 2, 1, -0.05F, true));
			pec2_r1.cubeList.add(new ModelBox(pec2_r1, 47, 38, -4.5F, -1.0F, -0.5F, 3, 2, 1, -0.05F, false));
	
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.25F, -7.25F, -6.25F);
			bipedBody.addChild(bipedRightArm);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 44, 29, -2.65F, -0.55F, -1.6F, 3, 3, 3, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 42, -3.05F, -0.55F, -0.5F, 3, 3, 3, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 38, 39, -3.05F, -0.55F, -1.6F, 3, 3, 3, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 13, 52, -0.25F, 9.75F, -5.5F, 1, 2, 1, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 9, 51, -0.25F, 8.85F, -5.5F, 1, 2, 1, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 51, 48, -1.35F, 9.75F, -6.0F, 1, 2, 1, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 5, 51, -1.35F, 9.05F, -6.0F, 1, 2, 1, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 33, 51, -2.45F, 9.75F, -6.0F, 1, 2, 1, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 51, 0, -2.45F, 9.15F, -6.0F, 1, 2, 1, 0.0F, false));
	
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(0.2F, -2.45F, -0.1F);
			bipedRightArm.addChild(bone4);
			setRotationAngle(bone4, 0.0F, 0.0F, -0.6109F);
			bone4.cubeList.add(new ModelBox(bone4, 18, 15, -3.9F, -0.1F, -1.5F, 4, 3, 4, 0.0F, false));
	
			cube_r11 = new ModelRenderer(this);
			cube_r11.setRotationPoint(2.75F, 10.75F, 3.1F);
			bipedRightArm.addChild(cube_r11);
			setRotationAngle(cube_r11, 0.0F, -0.7418F, 0.0F);
			cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 9, -6.5F, -2.5F, -4.1F, 1, 1, 2, 0.0F, false));
			cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 12, -6.5F, -2.5F, -4.9F, 1, 1, 2, 0.0F, false));
	
			cube_r12 = new ModelRenderer(this);
			cube_r12.setRotationPoint(-3.05F, 8.75F, -5.0F);
			bipedRightArm.addChild(cube_r12);
			setRotationAngle(cube_r12, -0.6545F, 0.6545F, 0.0F);
			cube_r12.cubeList.add(new ModelBox(cube_r12, 51, 24, -0.5F, -1.2F, -0.1F, 1, 2, 1, 0.0F, false));
	
			cube_r13 = new ModelRenderer(this);
			cube_r13.setRotationPoint(-1.25F, 8.75F, -5.0F);
			bipedRightArm.addChild(cube_r13);
			setRotationAngle(cube_r13, -0.6545F, 0.0F, 0.0F);
			cube_r13.cubeList.add(new ModelBox(cube_r13, 37, 51, -1.2F, -1.0F, -0.5F, 1, 2, 1, 0.0F, false));
			cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 52, -0.1F, -1.0F, -0.5F, 1, 2, 1, 0.0F, false));
	
			cube_r14 = new ModelRenderer(this);
			cube_r14.setRotationPoint(-3.45F, 10.15F, -5.1F);
			bipedRightArm.addChild(cube_r14);
			setRotationAngle(cube_r14, 0.0F, 0.4363F, 0.0F);
			cube_r14.cubeList.add(new ModelBox(cube_r14, 48, 50, -0.2F, -0.9F, -0.3F, 1, 2, 1, 0.0F, false));
			cube_r14.cubeList.add(new ModelBox(cube_r14, 51, 19, -0.2F, -0.4F, -0.3F, 1, 2, 1, 0.0F, false));
	
			cube_r15 = new ModelRenderer(this);
			cube_r15.setRotationPoint(1.45F, 9.25F, -5.0F);
			bipedRightArm.addChild(cube_r15);
			setRotationAngle(cube_r15, 0.0F, -0.3054F, 0.0F);
			cube_r15.cubeList.add(new ModelBox(cube_r15, 36, 12, -0.2F, 0.5F, -0.5F, 1, 2, 1, 0.0F, false));
			cube_r15.cubeList.add(new ModelBox(cube_r15, 44, 50, -0.2F, -1.0F, -0.5F, 1, 2, 1, 0.0F, false));
	
			cube_r16 = new ModelRenderer(this);
			cube_r16.setRotationPoint(0.25F, 8.75F, -5.0F);
			bipedRightArm.addChild(cube_r16);
			setRotationAngle(cube_r16, -0.6109F, 0.0F, -0.5236F);
			cube_r16.cubeList.add(new ModelBox(cube_r16, 17, 52, -0.8F, -1.6F, -0.5F, 1, 2, 1, 0.0F, false));
	
			cube_r17 = new ModelRenderer(this);
			cube_r17.setRotationPoint(-3.35F, 7.9553F, -3.4708F);
			bipedRightArm.addChild(cube_r17);
			setRotationAngle(cube_r17, -0.5672F, 0.0F, 0.0F);
			cube_r17.cubeList.add(new ModelBox(cube_r17, 16, 11, 1.0F, 0.0F, -1.2F, 2, 1, 3, 0.0F, false));
	
			cube_r18 = new ModelRenderer(this);
			cube_r18.setRotationPoint(-1.35F, 5.65F, -1.2F);
			bipedRightArm.addChild(cube_r18);
			setRotationAngle(cube_r18, -0.5672F, 0.0F, 0.0F);
			cube_r18.cubeList.add(new ModelBox(cube_r18, 44, 0, -1.4F, -1.0F, -1.9F, 2, 5, 3, 0.0F, false));
			cube_r18.cubeList.add(new ModelBox(cube_r18, 12, 44, -1.0F, -1.0F, -1.9F, 2, 5, 3, 0.0F, false));
	
			cube_r19 = new ModelRenderer(this);
			cube_r19.setRotationPoint(-1.45F, 3.5387F, 0.7532F);
			bipedRightArm.addChild(cube_r19);
			setRotationAngle(cube_r19, -0.3491F, 0.0F, 0.0F);
			cube_r19.cubeList.add(new ModelBox(cube_r19, 34, 24, -1.3F, -2.0F, -2.9F, 2, 4, 4, 0.0F, false));
			cube_r19.cubeList.add(new ModelBox(cube_r19, 36, 12, -0.9F, -2.0F, -2.9F, 2, 4, 4, 0.0F, false));
	
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.25F, -7.25F, -6.25F);
			bipedBody.addChild(bipedLeftArm);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 44, 29, -0.35F, -0.55F, -1.6F, 3, 3, 3, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 42, 0.05F, -0.55F, -0.5F, 3, 3, 3, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 38, 39, 0.05F, -0.55F, -1.6F, 3, 3, 3, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 13, 52, -0.75F, 9.75F, -5.5F, 1, 2, 1, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 9, 51, -0.75F, 8.85F, -5.5F, 1, 2, 1, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 51, 48, 0.35F, 9.75F, -6.0F, 1, 2, 1, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 5, 51, 0.35F, 9.05F, -6.0F, 1, 2, 1, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 33, 51, 1.45F, 9.75F, -6.0F, 1, 2, 1, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 51, 0, 1.45F, 9.15F, -6.0F, 1, 2, 1, 0.0F, true));
	
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(-0.2F, -2.45F, -0.1F);
			bipedLeftArm.addChild(bone6);
			setRotationAngle(bone6, 0.0F, 0.0F, 0.6109F);
			bone6.cubeList.add(new ModelBox(bone6, 18, 15, -0.1F, -0.1F, -1.5F, 4, 3, 4, 0.0F, true));
	
			cube_r26 = new ModelRenderer(this);
			cube_r26.setRotationPoint(-2.75F, 10.75F, 3.1F);
			bipedLeftArm.addChild(cube_r26);
			setRotationAngle(cube_r26, 0.0F, 0.7418F, 0.0F);
			cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 9, 5.5F, -2.5F, -4.1F, 1, 1, 2, 0.0F, true));
			cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 12, 5.5F, -2.5F, -4.9F, 1, 1, 2, 0.0F, true));
	
			cube_r27 = new ModelRenderer(this);
			cube_r27.setRotationPoint(3.05F, 8.75F, -5.0F);
			bipedLeftArm.addChild(cube_r27);
			setRotationAngle(cube_r27, -0.6545F, -0.6545F, 0.0F);
			cube_r27.cubeList.add(new ModelBox(cube_r27, 51, 24, -0.5F, -1.2F, -0.1F, 1, 2, 1, 0.0F, true));
	
			cube_r28 = new ModelRenderer(this);
			cube_r28.setRotationPoint(1.25F, 8.75F, -5.0F);
			bipedLeftArm.addChild(cube_r28);
			setRotationAngle(cube_r28, -0.6545F, 0.0F, 0.0F);
			cube_r28.cubeList.add(new ModelBox(cube_r28, 37, 51, 0.2F, -1.0F, -0.5F, 1, 2, 1, 0.0F, true));
			cube_r28.cubeList.add(new ModelBox(cube_r28, 0, 52, -0.9F, -1.0F, -0.5F, 1, 2, 1, 0.0F, true));
	
			cube_r29 = new ModelRenderer(this);
			cube_r29.setRotationPoint(3.45F, 10.15F, -5.1F);
			bipedLeftArm.addChild(cube_r29);
			setRotationAngle(cube_r29, 0.0F, -0.4363F, 0.0F);
			cube_r29.cubeList.add(new ModelBox(cube_r29, 48, 50, -0.8F, -0.9F, -0.3F, 1, 2, 1, 0.0F, true));
			cube_r29.cubeList.add(new ModelBox(cube_r29, 51, 19, -0.8F, -0.4F, -0.3F, 1, 2, 1, 0.0F, true));
	
			cube_r30 = new ModelRenderer(this);
			cube_r30.setRotationPoint(-1.45F, 9.25F, -5.0F);
			bipedLeftArm.addChild(cube_r30);
			setRotationAngle(cube_r30, 0.0F, 0.3054F, 0.0F);
			cube_r30.cubeList.add(new ModelBox(cube_r30, 36, 12, -0.8F, 0.5F, -0.5F, 1, 2, 1, 0.0F, true));
			cube_r30.cubeList.add(new ModelBox(cube_r30, 44, 50, -0.8F, -1.0F, -0.5F, 1, 2, 1, 0.0F, true));
	
			cube_r31 = new ModelRenderer(this);
			cube_r31.setRotationPoint(-0.25F, 8.75F, -5.0F);
			bipedLeftArm.addChild(cube_r31);
			setRotationAngle(cube_r31, -0.6109F, 0.0F, 0.5236F);
			cube_r31.cubeList.add(new ModelBox(cube_r31, 17, 52, -0.2F, -1.6F, -0.5F, 1, 2, 1, 0.0F, true));
	
			cube_r32 = new ModelRenderer(this);
			cube_r32.setRotationPoint(3.35F, 7.9553F, -3.4708F);
			bipedLeftArm.addChild(cube_r32);
			setRotationAngle(cube_r32, -0.5672F, 0.0F, 0.0F);
			cube_r32.cubeList.add(new ModelBox(cube_r32, 16, 11, -3.0F, 0.0F, -1.2F, 2, 1, 3, 0.0F, true));
	
			cube_r33 = new ModelRenderer(this);
			cube_r33.setRotationPoint(1.35F, 5.65F, -1.2F);
			bipedLeftArm.addChild(cube_r33);
			setRotationAngle(cube_r33, -0.5672F, 0.0F, 0.0F);
			cube_r33.cubeList.add(new ModelBox(cube_r33, 44, 0, -0.6F, -1.0F, -1.9F, 2, 5, 3, 0.0F, true));
			cube_r33.cubeList.add(new ModelBox(cube_r33, 12, 44, -1.0F, -1.0F, -1.9F, 2, 5, 3, 0.0F, true));
	
			cube_r34 = new ModelRenderer(this);
			cube_r34.setRotationPoint(1.45F, 3.5387F, 0.7532F);
			bipedLeftArm.addChild(cube_r34);
			setRotationAngle(cube_r34, -0.3491F, 0.0F, 0.0F);
			cube_r34.cubeList.add(new ModelBox(cube_r34, 34, 24, -0.7F, -2.0F, -2.9F, 2, 4, 4, 0.0F, true));
			cube_r34.cubeList.add(new ModelBox(cube_r34, 36, 12, -1.1F, -2.0F, -2.9F, 2, 4, 4, 0.0F, true));
	
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-1.5F, 0.0F, 0.0F);
			bipedBody.addChild(bipedRightLeg);
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 42, 24, -6.5F, 3.5F, -3.85F, 3, 1, 3, 0.0F, false));
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 14, 36, -6.5F, -1.1F, -3.85F, 3, 5, 3, 0.0F, false));
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 33, 48, -6.3F, 3.0F, -4.15F, 1, 1, 2, 0.0F, false));
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 6, 48, -5.2F, 3.0F, -4.15F, 1, 1, 2, 0.0F, false));
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 11, 23, -4.1F, 3.0F, -4.15F, 1, 1, 2, 0.0F, false));
	
			cube_r20 = new ModelRenderer(this);
			cube_r20.setRotationPoint(-2.5F, 3.5F, -3.15F);
			bipedRightLeg.addChild(cube_r20);
			setRotationAngle(cube_r20, 0.0F, -0.6981F, 0.0F);
			cube_r20.cubeList.add(new ModelBox(cube_r20, 0, 23, -0.5F, -0.5F, -0.2F, 1, 1, 2, 0.0F, false));
	
			cube_r21 = new ModelRenderer(this);
			cube_r21.setRotationPoint(-7.0F, 3.5F, -3.15F);
			bipedRightLeg.addChild(cube_r21);
			setRotationAngle(cube_r21, 0.0F, 0.5672F, 0.0F);
			cube_r21.cubeList.add(new ModelBox(cube_r21, 39, 48, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, false));
	
			cube_r22 = new ModelRenderer(this);
			cube_r22.setRotationPoint(-2.2F, 3.4F, -6.25F);
			bipedRightLeg.addChild(cube_r22);
			setRotationAngle(cube_r22, 0.0F, -0.4363F, 0.0873F);
			cube_r22.cubeList.add(new ModelBox(cube_r22, 11, 20, 0.6F, -0.3F, 0.8F, 1, 1, 2, 0.0F, false));
	
			cube_r23 = new ModelRenderer(this);
			cube_r23.setRotationPoint(-6.0F, 4.0F, -3.75F);
			bipedRightLeg.addChild(cube_r23);
			setRotationAngle(cube_r23, 0.2182F, 0.0F, 0.0F);
			cube_r23.cubeList.add(new ModelBox(cube_r23, 9, 42, 1.9F, -1.1F, -2.2F, 1, 1, 2, 0.0F, false));
			cube_r23.cubeList.add(new ModelBox(cube_r23, 19, 44, 0.8F, -1.1F, -2.2F, 1, 1, 2, 0.0F, false));
			cube_r23.cubeList.add(new ModelBox(cube_r23, 48, 16, -0.3F, -1.1F, -2.2F, 1, 1, 2, 0.0F, false));
	
			cube_r24 = new ModelRenderer(this);
			cube_r24.setRotationPoint(-7.7113F, 3.6739F, -4.2028F);
			bipedRightLeg.addChild(cube_r24);
			setRotationAngle(cube_r24, 0.2182F, 0.5672F, -0.0873F);
			cube_r24.cubeList.add(new ModelBox(cube_r24, 48, 43, -0.5F, -0.5F, -1.0F, 1, 1, 2, 0.0F, false));
	
			cube_r25 = new ModelRenderer(this);
			cube_r25.setRotationPoint(-2.5F, 0.7F, -2.05F);
			bipedRightLeg.addChild(cube_r25);
			setRotationAngle(cube_r25, 0.0F, 0.0F, -0.1745F);
			cube_r25.cubeList.add(new ModelBox(cube_r25, 31, 33, -2.0F, -1.8F, 0.5F, 5, 3, 3, 0.0F, false));
	
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.5F, 0.0F, 0.0F);
			bipedBody.addChild(bipedLeftLeg);
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 42, 24, 3.5F, 3.5F, -3.85F, 3, 1, 3, 0.0F, true));
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 14, 36, 3.5F, -1.1F, -3.85F, 3, 5, 3, 0.0F, true));
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 33, 48, 5.3F, 3.0F, -4.15F, 1, 1, 2, 0.0F, true));
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 6, 48, 4.2F, 3.0F, -4.15F, 1, 1, 2, 0.0F, true));
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 11, 23, 3.1F, 3.0F, -4.15F, 1, 1, 2, 0.0F, true));
	
			cube_r35 = new ModelRenderer(this);
			cube_r35.setRotationPoint(2.5F, 3.5F, -3.15F);
			bipedLeftLeg.addChild(cube_r35);
			setRotationAngle(cube_r35, 0.0F, 0.6981F, 0.0F);
			cube_r35.cubeList.add(new ModelBox(cube_r35, 0, 23, -0.5F, -0.5F, -0.2F, 1, 1, 2, 0.0F, true));
	
			cube_r36 = new ModelRenderer(this);
			cube_r36.setRotationPoint(7.0F, 3.5F, -3.15F);
			bipedLeftLeg.addChild(cube_r36);
			setRotationAngle(cube_r36, 0.0F, -0.5672F, 0.0F);
			cube_r36.cubeList.add(new ModelBox(cube_r36, 39, 48, -0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F, true));
	
			cube_r37 = new ModelRenderer(this);
			cube_r37.setRotationPoint(2.2F, 3.4F, -6.25F);
			bipedLeftLeg.addChild(cube_r37);
			setRotationAngle(cube_r37, 0.0F, 0.4363F, -0.0873F);
			cube_r37.cubeList.add(new ModelBox(cube_r37, 11, 20, -1.6F, -0.3F, 0.8F, 1, 1, 2, 0.0F, true));
	
			cube_r38 = new ModelRenderer(this);
			cube_r38.setRotationPoint(6.0F, 4.0F, -3.75F);
			bipedLeftLeg.addChild(cube_r38);
			setRotationAngle(cube_r38, 0.2182F, 0.0F, 0.0F);
			cube_r38.cubeList.add(new ModelBox(cube_r38, 9, 42, -2.9F, -1.1F, -2.2F, 1, 1, 2, 0.0F, true));
			cube_r38.cubeList.add(new ModelBox(cube_r38, 19, 44, -1.8F, -1.1F, -2.2F, 1, 1, 2, 0.0F, true));
			cube_r38.cubeList.add(new ModelBox(cube_r38, 48, 16, -0.7F, -1.1F, -2.2F, 1, 1, 2, 0.0F, true));
	
			cube_r39 = new ModelRenderer(this);
			cube_r39.setRotationPoint(7.7113F, 3.6739F, -4.2028F);
			bipedLeftLeg.addChild(cube_r39);
			setRotationAngle(cube_r39, 0.2182F, -0.5672F, 0.0873F);
			cube_r39.cubeList.add(new ModelBox(cube_r39, 48, 43, -0.5F, -0.5F, -1.0F, 1, 1, 2, 0.0F, true));
	
			cube_r40 = new ModelRenderer(this);
			cube_r40.setRotationPoint(2.5F, 0.7F, -2.05F);
			bipedLeftLeg.addChild(cube_r40);
			setRotationAngle(cube_r40, 0.0F, 0.0F, 0.1745F);
			cube_r40.cubeList.add(new ModelBox(cube_r40, 31, 33, -3.0F, -1.8F, 0.5F, 5, 3, 3, 0.0F, true));
	
			tails = new ModelRenderer(this);
			tails.setRotationPoint(0.0F, -2.0F, 2.25F);
			bipedBody.addChild(tails);
			//setRotationAngle(tails, -2.0071F, 0.0F, 0.0F);
			
	
			tail[0][0] = new ModelRenderer(this);
			tail[0][0].setRotationPoint(1.5F, 0.0F, 0.0F);
			tails.addChild(tail[0][0]);
			setRotationAngle(tail[0][0], -1.0472F, 1.0472F, 0.0F);
			tail[0][0].cubeList.add(new ModelBox(tail[0][0], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
			tail[0][0].cubeList.add(new ModelBox(tail[0][0], 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
	
			tail[0][1] = new ModelRenderer(this);
			tail[0][1].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[0][0].addChild(tail[0][1]);
			setRotationAngle(tail[0][1], 0.2618F, 0.0F, 0.0F);
			tail[0][1].cubeList.add(new ModelBox(tail[0][1], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
			tail[0][1].cubeList.add(new ModelBox(tail[0][1], 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
	
			tail[0][2] = new ModelRenderer(this);
			tail[0][2].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[0][1].addChild(tail[0][2]);
			setRotationAngle(tail[0][2], 0.2618F, 0.0F, 0.0F);
			tail[0][2].cubeList.add(new ModelBox(tail[0][2], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
			tail[0][2].cubeList.add(new ModelBox(tail[0][2], 0, 61, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));
	
			tail[0][3] = new ModelRenderer(this);
			tail[0][3].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[0][2].addChild(tail[0][3]);
			setRotationAngle(tail[0][3], 0.2618F, 0.0F, 0.0F);
			tail[0][3].cubeList.add(new ModelBox(tail[0][3], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
			tail[0][3].cubeList.add(new ModelBox(tail[0][3], 0, 61, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));
	
			tail[0][4] = new ModelRenderer(this);
			tail[0][4].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[0][3].addChild(tail[0][4]);
			setRotationAngle(tail[0][4], -0.2618F, 0.0F, 0.0F);
			tail[0][4].cubeList.add(new ModelBox(tail[0][4], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
			tail[0][4].cubeList.add(new ModelBox(tail[0][4], 0, 61, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));
	
			tail[0][5] = new ModelRenderer(this);
			tail[0][5].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[0][4].addChild(tail[0][5]);
			setRotationAngle(tail[0][5], -0.2618F, 0.0F, 0.0F);
			tail[0][5].cubeList.add(new ModelBox(tail[0][5], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
			tail[0][5].cubeList.add(new ModelBox(tail[0][5], 0, 61, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));
	
			tail[1][0] = new ModelRenderer(this);
			tail[1][0].setRotationPoint(0.5F, 0.0F, 0.0F);
			tails.addChild(tail[1][0]);
			setRotationAngle(tail[1][0], -1.0472F, 0.3491F, 0.0F);
			tail[1][0].cubeList.add(new ModelBox(tail[1][0], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
			tail[1][0].cubeList.add(new ModelBox(tail[1][0], 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
	
			tail[1][1] = new ModelRenderer(this);
			tail[1][1].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[1][0].addChild(tail[1][1]);
			setRotationAngle(tail[1][1], 0.2618F, 0.0F, 0.0F);
			tail[1][1].cubeList.add(new ModelBox(tail[1][1], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
			tail[1][1].cubeList.add(new ModelBox(tail[1][1], 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
	
			tail[1][2] = new ModelRenderer(this);
			tail[1][2].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[1][1].addChild(tail[1][2]);
			setRotationAngle(tail[1][2], 0.2618F, 0.0F, 0.0F);
			tail[1][2].cubeList.add(new ModelBox(tail[1][2], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
			tail[1][2].cubeList.add(new ModelBox(tail[1][2], 0, 61, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));
	
			tail[1][3] = new ModelRenderer(this);
			tail[1][3].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[1][2].addChild(tail[1][3]);
			setRotationAngle(tail[1][3], 0.2618F, 0.0F, 0.0F);
			tail[1][3].cubeList.add(new ModelBox(tail[1][3], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
			tail[1][3].cubeList.add(new ModelBox(tail[1][3], 0, 61, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));
	
			tail[1][4] = new ModelRenderer(this);
			tail[1][4].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[1][3].addChild(tail[1][4]);
			setRotationAngle(tail[1][4], -0.2618F, 0.0F, 0.0F);
			tail[1][4].cubeList.add(new ModelBox(tail[1][4], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
			tail[1][4].cubeList.add(new ModelBox(tail[1][4], 0, 61, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));
	
			tail[1][5] = new ModelRenderer(this);
			tail[1][5].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[1][4].addChild(tail[1][5]);
			setRotationAngle(tail[1][5], -0.2618F, 0.0F, 0.0F);
			tail[1][5].cubeList.add(new ModelBox(tail[1][5], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
			tail[1][5].cubeList.add(new ModelBox(tail[1][5], 0, 61, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));
	
			tail[2][0] = new ModelRenderer(this);
			tail[2][0].setRotationPoint(-0.5F, 0.0F, 0.0F);
			tails.addChild(tail[2][0]);
			setRotationAngle(tail[2][0], -1.0472F, -0.3491F, 0.0F);
			tail[2][0].cubeList.add(new ModelBox(tail[2][0], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
			tail[2][0].cubeList.add(new ModelBox(tail[2][0], 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
	
			tail[2][1] = new ModelRenderer(this);
			tail[2][1].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[2][0].addChild(tail[2][1]);
			setRotationAngle(tail[2][1], 0.2618F, 0.0F, 0.0F);
			tail[2][1].cubeList.add(new ModelBox(tail[2][1], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
			tail[2][1].cubeList.add(new ModelBox(tail[2][1], 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
	
			tail[2][2] = new ModelRenderer(this);
			tail[2][2].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[2][1].addChild(tail[2][2]);
			setRotationAngle(tail[2][2], 0.2618F, 0.0F, 0.0F);
			tail[2][2].cubeList.add(new ModelBox(tail[2][2], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
			tail[2][2].cubeList.add(new ModelBox(tail[2][2], 0, 61, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));
	
			tail[2][3] = new ModelRenderer(this);
			tail[2][3].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[2][2].addChild(tail[2][3]);
			setRotationAngle(tail[2][3], 0.2618F, 0.0F, 0.0F);
			tail[2][3].cubeList.add(new ModelBox(tail[2][3], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
			tail[2][3].cubeList.add(new ModelBox(tail[2][3], 0, 61, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));
	
			tail[2][4] = new ModelRenderer(this);
			tail[2][4].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[2][3].addChild(tail[2][4]);
			setRotationAngle(tail[2][4], -0.2618F, 0.0F, 0.0F);
			tail[2][4].cubeList.add(new ModelBox(tail[2][4], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
			tail[2][4].cubeList.add(new ModelBox(tail[2][4], 0, 61, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));
	
			tail[2][5] = new ModelRenderer(this);
			tail[2][5].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[2][4].addChild(tail[2][5]);
			setRotationAngle(tail[2][5], -0.2618F, 0.0F, 0.0F);
			tail[2][5].cubeList.add(new ModelBox(tail[2][5], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
			tail[2][5].cubeList.add(new ModelBox(tail[2][5], 0, 61, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));
	
			tail[3][0] = new ModelRenderer(this);
			tail[3][0].setRotationPoint(-1.5F, 0.0F, 0.0F);
			tails.addChild(tail[3][0]);
			setRotationAngle(tail[3][0], -1.0472F, -1.0472F, 0.0F);
			tail[3][0].cubeList.add(new ModelBox(tail[3][0], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
			tail[3][0].cubeList.add(new ModelBox(tail[3][0], 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
	
			tail[3][1] = new ModelRenderer(this);
			tail[3][1].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[3][0].addChild(tail[3][1]);
			setRotationAngle(tail[3][1], 0.2618F, 0.0F, 0.0F);
			tail[3][1].cubeList.add(new ModelBox(tail[3][1], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
			tail[3][1].cubeList.add(new ModelBox(tail[3][1], 0, 61, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
	
			tail[3][2] = new ModelRenderer(this);
			tail[3][2].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[3][1].addChild(tail[3][2]);
			setRotationAngle(tail[3][2], 0.2618F, 0.0F, 0.0F);
			tail[3][2].cubeList.add(new ModelBox(tail[3][2], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
			tail[3][2].cubeList.add(new ModelBox(tail[3][2], 0, 61, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));
	
			tail[3][3] = new ModelRenderer(this);
			tail[3][3].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[3][2].addChild(tail[3][3]);
			setRotationAngle(tail[3][3], 0.2618F, 0.0F, 0.0F);
			tail[3][3].cubeList.add(new ModelBox(tail[3][3], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
			tail[3][3].cubeList.add(new ModelBox(tail[3][3], 0, 61, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));
	
			tail[3][4] = new ModelRenderer(this);
			tail[3][4].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[3][3].addChild(tail[3][4]);
			setRotationAngle(tail[3][4], -0.2618F, 0.0F, 0.0F);
			tail[3][4].cubeList.add(new ModelBox(tail[3][4], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
			tail[3][4].cubeList.add(new ModelBox(tail[3][4], 0, 61, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));
	
			tail[3][5] = new ModelRenderer(this);
			tail[3][5].setRotationPoint(0.0F, -3.0F, 0.0F);
			tail[3][4].addChild(tail[3][5]);
			setRotationAngle(tail[3][5], -0.2618F, 0.0F, 0.0F);
			tail[3][5].cubeList.add(new ModelBox(tail[3][5], 0, 55, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
			tail[3][5].cubeList.add(new ModelBox(tail[3][5], 0, 61, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));

			for (int i = 0; i < 4; i++) {
				for (int j = 1; j < 6; j++) {
					tailSwayX[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayZ[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayY[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F);
				}
			}
		}

		@Override
		public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE, 0.0F);
			//GlStateManager.translate(0.0F, 0.0F, 0.375F * MODELSCALE);
			GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
			bipedBody.render(f5);
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
			bipedHead.rotationPointY += -6.5F;
			bipedRightArm.setRotationPoint(-5.25F, -7.25F, -6.25F);
			bipedLeftArm.setRotationPoint(5.25F, -7.25F, -6.25F);
			bipedRightLeg.setRotationPoint(-1.5F, 0.0F, 0.0F);
			bipedLeftLeg.setRotationPoint(1.5F, 0.0F, 0.0F);
			for (int i = 0; i < 4; i++) {
				for (int j = 1; j < 6; j++) {
					if (j <= 3) {
						tail[i][j].rotateAngleX = 0.2618F + MathHelper.sin((f2 - j) * 0.05F) * tailSwayX[i][j];
					} else {
						tail[i][j].rotateAngleX = -0.2618F + MathHelper.sin((f2 - j) * 0.05F) * tailSwayX[i][j];
					}
					tail[i][j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.05F) * tailSwayZ[i][j];
					//Tail[i][j].rotateAngleY = MathHelper.sin((f2 - j) * 0.1F) * tailSwayY[i][j];
				}
			}
			if (((EntityCustom)e).isShooting()) {
				bipedHead.rotateAngleX += -0.5236F;
				Jaw.rotateAngleX = 0.7854F;
			} else {
				Jaw.rotateAngleX = 0.0F;
			}
			if (((EntityCustom)e).isFaceDown()) {
				bipedBody.rotationPointZ = 10.0F;
				bipedBody.rotateAngleX = 0.6981F;
				bipedHead.rotateAngleX = -0.2618F;
				bipedRightArm.rotateAngleX = -1.2217F;
				bipedLeftArm.rotateAngleX = -1.2217F;
				bipedRightLeg.rotateAngleX = 0.7854F;
				bipedLeftLeg.rotateAngleX = 0.7854F;
				tails.rotateAngleX = -2.0071F;
			} else {
				bipedBody.rotationPointZ = 6.75F;
				bipedBody.rotateAngleX = 0.0F;
				tails.rotateAngleX = 0.0F;
			}
			this.copyModelAngles(bipedBody, bipedHeadwear);
			this.copyModelAngles(bipedHead, eyes);
		}
	}
}
