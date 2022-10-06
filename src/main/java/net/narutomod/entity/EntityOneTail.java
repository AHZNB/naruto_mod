
package net.narutomod.entity;

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
//import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

//import net.narutomod.item.ItemBijuCloak;
import net.narutomod.item.ItemJiton;
import net.narutomod.item.ItemDoton;
import net.narutomod.item.ItemFuton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityOneTail extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 259;
	public static final int ENTITYID_RANGED = 260;
	private static final float MODELSCALE = 20.0F;
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
			super(EntityCustom.class, 1);
		}

		@Override
		public void setJinchurikiPlayer(@Nullable EntityPlayer player) {
			super.setJinchurikiPlayer(player);
			if (player != null && !ProcedureUtils.hasItemInInventory(player, ItemJiton.block)) {
				ItemStack stack = new ItemStack(ItemJiton.block);
				ItemJiton.setSandType(stack, ItemJiton.Type.SAND);
				ItemHandlerHelper.giveItemToPlayer(player, stack);
				if (!ProcedureUtils.hasItemInInventory(player, ItemFuton.block)) {
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ItemFuton.block));
				}
				if (!ProcedureUtils.hasItemInInventory(player, ItemDoton.block)) {
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ItemDoton.block));
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
			this.setSize(MODELSCALE * 0.4F, MODELSCALE * 1.1F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.4F, MODELSCALE * 1.1F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
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
			return (double)this.height + 0.35D;
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
		//private final ModelRenderer head;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r2;
		private final ModelRenderer cube_r3;
		private final ModelRenderer cube_r4;
		private final ModelRenderer jaw;
		private final ModelRenderer bone3;
		private final ModelRenderer bone4;
		//private final ModelRenderer Body;
		private final ModelRenderer Stomach;
		private final ModelRenderer cube_r5;
		private final ModelRenderer cube_r6;
		private final ModelRenderer cube_r7;
		private final ModelRenderer Upperbody;
		private final ModelRenderer[] Tail = new ModelRenderer[10];
		private final float tailSwayX[] = new float[10];
		private final float tailSwayY[] = new float[10];
		private final float tailSwayZ[] = new float[10];
		private final Random rand = new Random();
		//private final ModelRenderer RightArm;
		private final ModelRenderer LEftarm2;
		private final ModelRenderer cube_r8;
		private final ModelRenderer cube_r9;
		private final ModelRenderer bone;
		private final ModelRenderer cube_r10;
		private final ModelRenderer cube_r11;
		private final ModelRenderer cube_r12;
		private final ModelRenderer cube_r13;
		//private final ModelRenderer LeftArm;
		private final ModelRenderer LEftarm3;
		private final ModelRenderer cube_r14;
		private final ModelRenderer cube_r15;
		private final ModelRenderer bone2;
		private final ModelRenderer cube_r16;
		private final ModelRenderer cube_r17;
		private final ModelRenderer cube_r18;
		private final ModelRenderer cube_r19;
		//private final ModelRenderer RightLeg;
		private final ModelRenderer cube_r20;
		private final ModelRenderer cube_r21;
		private final ModelRenderer cube_r22;
		private final ModelRenderer RightFoot;
		private final ModelRenderer cube_r23;
		private final ModelRenderer cube_r24;
		private final ModelRenderer cube_r25;
		private final ModelRenderer cube_r26;
		private final ModelRenderer cube_r27;
		//private final ModelRenderer LeftLeg;
		private final ModelRenderer cube_r28;
		private final ModelRenderer cube_r29;
		private final ModelRenderer cube_r30;
		private final ModelRenderer LeftFoot;
		private final ModelRenderer cube_r31;
		private final ModelRenderer cube_r32;
		private final ModelRenderer cube_r33;
		private final ModelRenderer cube_r34;
		private final ModelRenderer cube_r35;
		public ModelOneTail() {
			textureWidth = 64;
			textureHeight = 64;
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 11.0F, -2.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 52, 8, -1.5F, -0.72F, -5.4006F, 3, 0, 3, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 28, 25, -3.0F, -2.75F, -2.5F, 6, 3, 4, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 8, 46, -2.0F, -3.9F, -1.5F, 4, 3, 2, 0.0F, false));
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(0.0F, 13.0F, -1.5F);
			bipedHead.addChild(cube_r1);
			setRotationAngle(cube_r1, 0.1309F, 0.0F, 0.0F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 18, 29, -1.0F, -15.4F, -2.2F, 2, 1, 1, 0.0F, false));
			cube_r1.cubeList.add(new ModelBox(cube_r1, 48, 0, -1.5F, -15.5F, -2.1F, 3, 2, 2, 0.0F, false));
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(0.0F, 13.0F, -1.5F);
			bipedHead.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.0873F, 0.0F, 0.0F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 47, 17, -1.5F, -17.5F, -1.3F, 3, 1, 3, 0.0F, false));
			cube_r2.cubeList.add(new ModelBox(cube_r2, 32, 35, -2.0F, -17.3F, -1.8F, 4, 4, 4, 0.0F, false));
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.1F, -0.2346F, -4.0706F);
			bipedHead.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.0F, -1.5708F, 0.0F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 51, 9, -0.3F, -0.4854F, -1.8294F, 2, 0, 2, 0.0F, true));
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(-0.1F, -0.2346F, -4.0706F);
			bipedHead.addChild(cube_r4);
			setRotationAngle(cube_r4, 0.0F, 1.5708F, 0.0F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 51, 9, -1.7F, -0.4854F, -1.8294F, 2, 0, 2, 0.0F, false));
			jaw = new ModelRenderer(this);
			jaw.setRotationPoint(-0.1F, -0.7346F, -2.5706F);
			bipedHead.addChild(jaw);
			jaw.cubeList.add(new ModelBox(jaw, 47, 13, -1.5F, 0.0F, -3.0F, 3, 1, 3, 0.0F, false));
			jaw.cubeList.add(new ModelBox(jaw, 52, 8, -1.5F, 0.8146F, -3.0F, 3, 0, 3, 0.0F, false));
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(-1.1F, -3.6F, -4.2F);
			bipedHead.addChild(bone3);
			setRotationAngle(bone3, 0.3491F, -0.1745F, 0.1745F);
			bone3.cubeList.add(new ModelBox(bone3, 56, 0, -1.1F, -0.6F, -0.8F, 2, 1, 2, 0.0F, false));
			bone3.cubeList.add(new ModelBox(bone3, 0, 6, -1.3F, -0.8F, 0.0F, 1, 1, 2, -0.1F, false));
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(1.1F, -3.6F, -4.2F);
			bipedHead.addChild(bone4);
			setRotationAngle(bone4, 0.3491F, 0.1745F, -0.1745F);
			bone4.cubeList.add(new ModelBox(bone4, 56, 0, -0.9F, -0.6F, -0.8F, 2, 1, 2, 0.0F, true));
			bone4.cubeList.add(new ModelBox(bone4, 0, 6, 0.3F, -0.8F, 0.0F, 1, 1, 2, -0.1F, true));
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 11.0F, -2.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 40, 3, -1.5F, -3.0F, -4.75F, 3, 1, 1, 0.0F, false));
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 11.0F, 0.0F);
			Stomach = new ModelRenderer(this);
			Stomach.setRotationPoint(0.0F, 13.0F, -3.5F);
			bipedBody.addChild(Stomach);
			setRotationAngle(Stomach, -0.2618F, 0.0F, 0.0F);
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
			Stomach.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.1309F, 0.0F, 0.0F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 29, -4.0F, -8.0F, 5.3F, 8, 7, 1, 0.0F, false));
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(0.0F, -12.4F, -3.7F);
			Stomach.addChild(cube_r6);
			setRotationAngle(cube_r6, 0.1309F, 0.0F, 0.0F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 36, 8, -3.0F, 4.7769F, -1.3502F, 6, 7, 1, 0.0F, false));
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(4.5F, -5.5F, 1.2F);
			Stomach.addChild(cube_r7);
			setRotationAngle(cube_r7, 0.1309F, 0.0F, 0.0F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 0, -9.0F, -3.7037F, -4.9076F, 9, 8, 9, 0.0F, false));
			Upperbody = new ModelRenderer(this);
			Upperbody.setRotationPoint(0.0F, 13.0F, -2.5F);
			bipedBody.addChild(Upperbody);
			Upperbody.cubeList.add(new ModelBox(Upperbody, 0, 17, -4.0F, -11.0F, -3.0F, 8, 3, 8, 0.0F, false));
			Upperbody.cubeList.add(new ModelBox(Upperbody, 24, 17, -4.0F, -12.0F, -2.0F, 8, 1, 7, 0.0F, false));
			Upperbody.cubeList.add(new ModelBox(Upperbody, 32, 32, -4.0F, -13.0F, 2.6F, 8, 1, 2, 0.0F, false));
			Upperbody.cubeList.add(new ModelBox(Upperbody, 27, 0, -4.0F, -15.0F, -1.0F, 8, 3, 5, 0.0F, false));
			Tail[0] = new ModelRenderer(this);
			Tail[0].setRotationPoint(0.0F, 10.0F, 3.0F);
			bipedBody.addChild(Tail[0]);
			setRotationAngle(Tail[0], -1.0472F, 0.0F, 0.0F);
			Tail[0].cubeList.add(new ModelBox(Tail[0], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 1.1F, false));
			Tail[1] = new ModelRenderer(this);
			Tail[1].setRotationPoint(0.0F, -3.0F, 0.0F);
			Tail[0].addChild(Tail[1]);
			setRotationAngle(Tail[1], 0.3491F, 0.0F, 0.0F);
			Tail[1].cubeList.add(new ModelBox(Tail[1], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 1.0F, false));
			Tail[2] = new ModelRenderer(this);
			Tail[2].setRotationPoint(0.0F, -3.0F, 0.0F);
			Tail[1].addChild(Tail[2]);
			setRotationAngle(Tail[2], 0.3491F, 0.0F, 0.0F);
			Tail[2].cubeList.add(new ModelBox(Tail[2], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.9F, false));
			Tail[3] = new ModelRenderer(this);
			Tail[3].setRotationPoint(0.0F, -3.0F, 0.0F);
			Tail[2].addChild(Tail[3]);
			setRotationAngle(Tail[3], 0.3491F, 0.0F, 0.0F);
			Tail[3].cubeList.add(new ModelBox(Tail[3], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
			Tail[4] = new ModelRenderer(this);
			Tail[4].setRotationPoint(0.0F, -3.0F, 0.0F);
			Tail[3].addChild(Tail[4]);
			setRotationAngle(Tail[4], 0.3491F, 0.0F, 0.0F);
			Tail[4].cubeList.add(new ModelBox(Tail[4], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
			Tail[5] = new ModelRenderer(this);
			Tail[5].setRotationPoint(0.0F, -3.0F, 0.0F);
			Tail[4].addChild(Tail[5]);
			setRotationAngle(Tail[5], 0.3491F, 0.0F, 0.0F);
			Tail[5].cubeList.add(new ModelBox(Tail[5], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
			Tail[6] = new ModelRenderer(this);
			Tail[6].setRotationPoint(0.0F, -3.0F, 0.0F);
			Tail[5].addChild(Tail[6]);
			setRotationAngle(Tail[6], 0.2618F, 0.0F, 0.0F);
			Tail[6].cubeList.add(new ModelBox(Tail[6], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.5F, false));
			Tail[7] = new ModelRenderer(this);
			Tail[7].setRotationPoint(0.0F, -3.0F, 0.0F);
			Tail[6].addChild(Tail[7]);
			setRotationAngle(Tail[7], 0.2618F, 0.0F, 0.0F);
			Tail[7].cubeList.add(new ModelBox(Tail[7], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
			Tail[8] = new ModelRenderer(this);
			Tail[8].setRotationPoint(0.0F, -3.0F, 0.0F);
			Tail[7].addChild(Tail[8]);
			setRotationAngle(Tail[8], 0.2618F, 0.0F, 0.0F);
			Tail[8].cubeList.add(new ModelBox(Tail[8], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
			Tail[9] = new ModelRenderer(this);
			Tail[9].setRotationPoint(0.0F, -3.0F, 0.0F);
			Tail[8].addChild(Tail[9]);
			setRotationAngle(Tail[9], 0.2618F, 0.0F, 0.0F);
			Tail[9].cubeList.add(new ModelBox(Tail[9], 0, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, -0.4F, false));
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-4.0F, 12.0F, -1.5F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 20, 39, -2.0F, -2.0F, -2.0F, 2, 3, 4, 0.0F, false));
			LEftarm2 = new ModelRenderer(this);
			LEftarm2.setRotationPoint(4.0F, -2.0F, -1.0F);
			bipedRightArm.addChild(LEftarm2);
			setRotationAngle(LEftarm2, 0.0F, 0.0F, 0.0F);
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
			LEftarm2.addChild(cube_r8);
			setRotationAngle(cube_r8, -0.6981F, -0.0436F, 0.0F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 37, -7.2F, 2.6F, 3.3F, 2, 7, 3, 0.0F, false));
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
			LEftarm2.addChild(cube_r9);
			setRotationAngle(cube_r9, 0.1309F, 0.0F, 0.2182F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 10, 37, -5.7F, 1.6F, -0.5F, 2, 6, 3, 0.0F, false));
			bone = new ModelRenderer(this);
			bone.setRotationPoint(-6.25F, 9.25F, -1.75F);
			LEftarm2.addChild(bone);
			setRotationAngle(bone, 0.0F, -0.3054F, 0.0F);
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(6.0F, 4.75F, 1.75F);
			bone.addChild(cube_r10);
			setRotationAngle(cube_r10, 0.3927F, 0.0F, 0.0F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 49, 32, -6.0F, -6.0F, -3.4F, 1, 1, 3, 0.0F, false));
			cube_r11 = new ModelRenderer(this);
			cube_r11.setRotationPoint(6.0F, -9.25F, 1.75F);
			bone.addChild(cube_r11);
			setRotationAngle(cube_r11, 0.48F, 0.2182F, 0.0F);
			cube_r11.cubeList.add(new ModelBox(cube_r11, 27, 49, -6.8F, 7.6F, -10.4F, 1, 1, 3, 0.0F, false));
			cube_r12 = new ModelRenderer(this);
			cube_r12.setRotationPoint(6.0F, -9.25F, 1.75F);
			bone.addChild(cube_r12);
			setRotationAngle(cube_r12, 0.48F, 0.1745F, 0.0F);
			cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 21, -6.8F, 6.8F, -10.3F, 1, 1, 3, 0.0F, false));
			cube_r13 = new ModelRenderer(this);
			cube_r13.setRotationPoint(6.0F, -9.25F, 1.75F);
			bone.addChild(cube_r13);
			setRotationAngle(cube_r13, 0.5672F, 0.2182F, 0.0F);
			cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 17, -6.4F, 4.8F, -11.0F, 1, 1, 3, 0.0F, false));
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(4.0F, 12.0F, -1.5F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 20, 39, 0.0F, -2.0F, -2.0F, 2, 3, 4, 0.0F, true));
			LEftarm3 = new ModelRenderer(this);
			LEftarm3.setRotationPoint(-4.0F, -2.0F, -1.0F);
			bipedLeftArm.addChild(LEftarm3);
			setRotationAngle(LEftarm3, 0.0F, 0.0F, 0.0F);
			cube_r14 = new ModelRenderer(this);
			cube_r14.setRotationPoint(0.0F, 0.0F, 0.0F);
			LEftarm3.addChild(cube_r14);
			setRotationAngle(cube_r14, -0.6981F, 0.0436F, 0.0F);
			cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 37, 5.2F, 2.6F, 3.3F, 2, 7, 3, 0.0F, true));
			cube_r15 = new ModelRenderer(this);
			cube_r15.setRotationPoint(0.0F, 0.0F, 0.0F);
			LEftarm3.addChild(cube_r15);
			setRotationAngle(cube_r15, 0.1309F, 0.0F, -0.2182F);
			cube_r15.cubeList.add(new ModelBox(cube_r15, 10, 37, 3.7F, 1.6F, -0.5F, 2, 6, 3, 0.0F, true));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(6.25F, 9.25F, -1.75F);
			LEftarm3.addChild(bone2);
			setRotationAngle(bone2, 0.0F, 0.3054F, 0.0F);
			cube_r16 = new ModelRenderer(this);
			cube_r16.setRotationPoint(-6.0F, 4.75F, 1.75F);
			bone2.addChild(cube_r16);
			setRotationAngle(cube_r16, 0.3927F, 0.0F, 0.0F);
			cube_r16.cubeList.add(new ModelBox(cube_r16, 49, 32, 5.0F, -6.0F, -3.4F, 1, 1, 3, 0.0F, true));
			cube_r17 = new ModelRenderer(this);
			cube_r17.setRotationPoint(-6.0F, -9.25F, 1.75F);
			bone2.addChild(cube_r17);
			setRotationAngle(cube_r17, 0.48F, -0.2182F, 0.0F);
			cube_r17.cubeList.add(new ModelBox(cube_r17, 27, 49, 5.8F, 7.6F, -10.4F, 1, 1, 3, 0.0F, true));
			cube_r18 = new ModelRenderer(this);
			cube_r18.setRotationPoint(-6.0F, -9.25F, 1.75F);
			bone2.addChild(cube_r18);
			setRotationAngle(cube_r18, 0.48F, -0.1745F, 0.0F);
			cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 21, 5.8F, 6.8F, -10.3F, 1, 1, 3, 0.0F, true));
			cube_r19 = new ModelRenderer(this);
			cube_r19.setRotationPoint(-6.0F, -9.25F, 1.75F);
			bone2.addChild(cube_r19);
			setRotationAngle(cube_r19, 0.5672F, -0.2182F, 0.0F);
			cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 17, 5.4F, 4.8F, -11.0F, 1, 1, 3, 0.0F, true));
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-4.5F, 18.0F, -0.75F);
			cube_r20 = new ModelRenderer(this);
			cube_r20.setRotationPoint(5.5F, 6.0F, -1.75F);
			bipedRightLeg.addChild(cube_r20);
			setRotationAngle(cube_r20, -0.0611F, 0.0F, -0.0436F);
			cube_r20.cubeList.add(new ModelBox(cube_r20, 44, 25, -7.0F, -5.0F, -0.7F, 4, 1, 3, 0.0F, false));
			cube_r21 = new ModelRenderer(this);
			cube_r21.setRotationPoint(5.5F, 6.0F, -1.75F);
			bipedRightLeg.addChild(cube_r21);
			setRotationAngle(cube_r21, -0.1309F, 0.0F, 0.0F);
			cube_r21.cubeList.add(new ModelBox(cube_r21, 18, 32, -8.0F, -4.0F, -1.0F, 5, 3, 4, 0.0F, false));
			cube_r22 = new ModelRenderer(this);
			cube_r22.setRotationPoint(-0.7F, 0.8F, -0.05F);
			bipedRightLeg.addChild(cube_r22);
			setRotationAngle(cube_r22, 1.5272F, 0.0F, 0.0F);
			cube_r22.cubeList.add(new ModelBox(cube_r22, 45, 44, -0.2F, -1.968F, -2.1467F, 2, 3, 3, 0.0F, false));
			RightFoot = new ModelRenderer(this);
			RightFoot.setRotationPoint(5.5F, 6.0F, -1.75F);
			bipedRightLeg.addChild(RightFoot);
			RightFoot.cubeList.add(new ModelBox(RightFoot, 34, 44, -8.0F, -1.0F, 1.0F, 5, 1, 2, 0.0F, false));
			cube_r23 = new ModelRenderer(this);
			cube_r23.setRotationPoint(-1.0F, 0.0F, 0.0F);
			RightFoot.addChild(cube_r23);
			setRotationAngle(cube_r23, 0.0F, -0.0873F, 0.0F);
			cube_r23.cubeList.add(new ModelBox(cube_r23, 0, 47, -3.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F, false));
			cube_r24 = new ModelRenderer(this);
			cube_r24.setRotationPoint(-1.0F, 0.0F, 0.0F);
			RightFoot.addChild(cube_r24);
			setRotationAngle(cube_r24, 0.0F, 0.0873F, 0.0F);
			cube_r24.cubeList.add(new ModelBox(cube_r24, 20, 46, -4.0F, -1.0F, -4.0F, 1, 1, 4, 0.0F, false));
			cube_r25 = new ModelRenderer(this);
			cube_r25.setRotationPoint(-1.0F, 0.0F, 0.0F);
			RightFoot.addChild(cube_r25);
			setRotationAngle(cube_r25, 0.0F, 0.1309F, 0.0F);
			cube_r25.cubeList.add(new ModelBox(cube_r25, 35, 47, -5.0F, -1.0F, -4.0F, 1, 1, 4, 0.0F, false));
			cube_r26 = new ModelRenderer(this);
			cube_r26.setRotationPoint(-1.0F, 0.0F, 0.0F);
			RightFoot.addChild(cube_r26);
			setRotationAngle(cube_r26, 0.0F, 0.1745F, 0.0F);
			cube_r26.cubeList.add(new ModelBox(cube_r26, 43, 38, -6.0F, -1.0F, -4.0F, 1, 1, 5, 0.0F, false));
			cube_r27 = new ModelRenderer(this);
			cube_r27.setRotationPoint(0.0F, 0.0F, 0.0F);
			RightFoot.addChild(cube_r27);
			setRotationAngle(cube_r27, 0.0F, 0.2182F, 0.0F);
			cube_r27.cubeList.add(new ModelBox(cube_r27, 27, 43, -8.0F, -1.0F, -4.0F, 1, 1, 5, 0.0F, false));
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(4.5F, 18.0F, -0.75F);
			cube_r28 = new ModelRenderer(this);
			cube_r28.setRotationPoint(-5.5F, 6.0F, -1.75F);
			bipedLeftLeg.addChild(cube_r28);
			setRotationAngle(cube_r28, -0.0611F, 0.0F, 0.0436F);
			cube_r28.cubeList.add(new ModelBox(cube_r28, 44, 25, 3.0F, -5.0F, -0.7F, 4, 1, 3, 0.0F, true));
			cube_r29 = new ModelRenderer(this);
			cube_r29.setRotationPoint(-5.5F, 6.0F, -1.75F);
			bipedLeftLeg.addChild(cube_r29);
			setRotationAngle(cube_r29, -0.1309F, 0.0F, 0.0F);
			cube_r29.cubeList.add(new ModelBox(cube_r29, 18, 32, 3.0F, -4.0F, -1.0F, 5, 3, 4, 0.0F, true));
			cube_r30 = new ModelRenderer(this);
			cube_r30.setRotationPoint(0.7F, 0.8F, -0.05F);
			bipedLeftLeg.addChild(cube_r30);
			setRotationAngle(cube_r30, 1.5272F, 0.0F, 0.0F);
			cube_r30.cubeList.add(new ModelBox(cube_r30, 45, 44, -1.8F, -1.968F, -2.1467F, 2, 3, 3, 0.0F, true));
			LeftFoot = new ModelRenderer(this);
			LeftFoot.setRotationPoint(-5.5F, 6.0F, -1.75F);
			bipedLeftLeg.addChild(LeftFoot);
			LeftFoot.cubeList.add(new ModelBox(LeftFoot, 34, 44, 3.0F, -1.0F, 1.0F, 5, 1, 2, 0.0F, true));
			cube_r31 = new ModelRenderer(this);
			cube_r31.setRotationPoint(1.0F, 0.0F, 0.0F);
			LeftFoot.addChild(cube_r31);
			setRotationAngle(cube_r31, 0.0F, 0.0873F, 0.0F);
			cube_r31.cubeList.add(new ModelBox(cube_r31, 0, 47, 2.0F, -1.0F, -2.0F, 1, 1, 4, 0.0F, true));
			cube_r32 = new ModelRenderer(this);
			cube_r32.setRotationPoint(1.0F, 0.0F, 0.0F);
			LeftFoot.addChild(cube_r32);
			setRotationAngle(cube_r32, 0.0F, -0.0873F, 0.0F);
			cube_r32.cubeList.add(new ModelBox(cube_r32, 20, 46, 3.0F, -1.0F, -4.0F, 1, 1, 4, 0.0F, true));
			cube_r33 = new ModelRenderer(this);
			cube_r33.setRotationPoint(1.0F, 0.0F, 0.0F);
			LeftFoot.addChild(cube_r33);
			setRotationAngle(cube_r33, 0.0F, -0.1309F, 0.0F);
			cube_r33.cubeList.add(new ModelBox(cube_r33, 35, 47, 4.0F, -1.0F, -4.0F, 1, 1, 4, 0.0F, true));
			cube_r34 = new ModelRenderer(this);
			cube_r34.setRotationPoint(1.0F, 0.0F, 0.0F);
			LeftFoot.addChild(cube_r34);
			setRotationAngle(cube_r34, 0.0F, -0.1745F, 0.0F);
			cube_r34.cubeList.add(new ModelBox(cube_r34, 43, 38, 5.0F, -1.0F, -4.0F, 1, 1, 5, 0.0F, true));
			cube_r35 = new ModelRenderer(this);
			cube_r35.setRotationPoint(0.0F, 0.0F, 0.0F);
			LeftFoot.addChild(cube_r35);
			setRotationAngle(cube_r35, 0.0F, -0.2182F, 0.0F);
			cube_r35.cubeList.add(new ModelBox(cube_r35, 27, 43, 7.0F, -1.0F, -4.0F, 1, 1, 5, 0.0F, true));

			for (int j = 1; j < 10; j++) {
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
			super.render(entity, f0, f1, f2, f3, f4, f5);
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
			bipedHead.rotationPointY += 11.0F;
			bipedHeadwear.rotationPointY += 11.0F;
			bipedRightArm.rotationPointZ += -1.5F;
			bipedRightArm.rotationPointX += 1.0F;
			bipedLeftArm.rotationPointZ += -1.5F;
			bipedLeftArm.rotationPointX += -1.0F;
			bipedRightLeg.rotationPointY += 6.0F;
			bipedLeftLeg.rotationPointY += 6.0F;
			for (int j = 1; j < 10; j++) {
				if (j <= 5) {
					Tail[j].rotateAngleX = 0.2618F + MathHelper.sin((f2 - j) * 0.05F) * tailSwayX[j];
				} else {
					Tail[j].rotateAngleX = 0.2618F + MathHelper.sin((f2 - j) * 0.05F) * tailSwayX[j];
				}
				Tail[j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.05F) * tailSwayZ[j];
				Tail[j].rotateAngleY = MathHelper.sin((f2 - j) * 0.1F) * tailSwayY[j];
			}
			if (((EntityCustom)e).isShooting()) {
				bipedHead.rotateAngleX += -0.5236F;
				bipedHeadwear.rotateAngleX += -0.5236F;
				jaw.rotateAngleX = 0.7854F;
			} else {
				jaw.rotateAngleX = 0.0F;
			}
		}
	}
}
