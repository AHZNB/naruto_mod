
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
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemSuiton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySixTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 267;
	public static final int ENTITYID_RANGED = 268;
	private static final float MODELSCALE = 22.0F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntitySixTails(ElementsNarutomodMod instance) {
		super(instance, 588);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "six_tails"), ENTITYID).name("six_tails").tracker(96, 3, true).egg(-2500097, -1).build());
	}

	public static TailBeastManager getBijuManager() {
		return tailBeastManager;
	}

	public static class TailBeastManager extends EntityBijuManager<EntityCustom> {
		public TailBeastManager() {
			super(EntityCustom.class, 6);
		}

		@Override
		public void setVesselEntity(@Nullable Entity player) {
			super.setVesselEntity(player);
			if (player instanceof EntityPlayer && !ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemSuiton.block)) {
				ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, new ItemStack(ItemSuiton.block));
			}
		}

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}

	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_sixtails";
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
			this.setSize(MODELSCALE * 0.3F, MODELSCALE * 0.9F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.3F, MODELSCALE * 0.9F);
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
			this.setSize(this.width, MODELSCALE * (down ? 0.5F : 0.9F));
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
			return this.isFaceDown() ? 2.0d * 0.0625d * MODELSCALE : (double)this.height - 5.0D;
		}

		@Override
		public boolean shouldRiderSit() {
			return true;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0.3d * MODELSCALE, 0d, 0d) };
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
			private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/sixtails.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelSixTails(), MODELSCALE * 0.5F);
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
		public class ModelSixTails extends ModelBiped {
			//private final ModelRenderer Head;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer[][] horn = new ModelRenderer[2][5];
			private final float hornSwayX[][] = new float[2][5];
			private final float hornSwayZ[][] = new float[2][5];
			//private final ModelRenderer Body;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer tails;
			private final ModelRenderer[][] tail = new ModelRenderer[6][6];
			private final float tailSwayX[][] = new float[6][6];
			private final float tailSwayY[][] = new float[6][6];
			private final float tailSwayZ[][] = new float[6][6];
			private final Random rand = new Random();
			//private final ModelRenderer RightArm;
			private final ModelRenderer bone;
			private final ModelRenderer cube_r6;
			private final ModelRenderer cube_r7;
			private final ModelRenderer cube_r8;
			//private final ModelRenderer LeftArm;
			private final ModelRenderer bone5;
			private final ModelRenderer cube_r9;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			//private final ModelRenderer RightLeg;
			private final ModelRenderer cube_r12;
			private final ModelRenderer cube_r13;
			private final ModelRenderer RightLeg1;
			private final ModelRenderer cube_r14;
			private final ModelRenderer cube_r15;
			private final ModelRenderer Legdetail;
			//private final ModelRenderer LeftLeg;
			private final ModelRenderer cube_r16;
			private final ModelRenderer cube_r17;
			private final ModelRenderer RightLeg4;
			private final ModelRenderer cube_r18;
			private final ModelRenderer cube_r19;
			private final ModelRenderer Legdetail2;

			public ModelSixTails() {
				textureWidth = 64;
				textureHeight = 64;

				bipedHeadwear.showModel = false;

				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 22.75F, 3.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 17, 16, -3.0F, -6.7456F, -4.9001F, 6, 4, 5, 0.0F, false));

				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, -1.25F, -2.4F);
				bipedBody.addChild(cube_r1);
				setRotationAngle(cube_r1, -0.0873F, 0.0F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -3.5F, -2.5F, -3.6F, 7, 5, 6, 0.0F, false));

				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(0.0F, -6.7456F, -2.4001F);
				bipedBody.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.1309F, 0.0F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 11, -3.0F, -3.6F, -2.5F, 6, 5, 5, -0.2F, false));

				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -8.75F, -2.0F);
				bipedBody.addChild(bipedHead);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 22, -3.0F, -4.8F, -2.1F, 6, 5, 3, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 20, 0, -3.0F, -4.8F, -3.1F, 6, 5, 1, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 17, 11, -3.0F, -3.4F, -4.6F, 6, 3, 2, 0.0F, false));

				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(0.0F, -3.8499F, -3.4747F);
				bipedHead.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.829F, 0.0F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 31, 9, -3.0F, -0.9F, -0.4F, 6, 2, 1, 0.0F, false));

				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(0.0F, -0.4907F, -3.3866F);
				bipedHead.addChild(cube_r4);
				setRotationAngle(cube_r4, 1.0472F, 0.0F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 26, 6, -3.0F, -1.0F, -0.7F, 6, 2, 1, 0.0F, false));

				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.0F, -1.0169F, 0.9617F);
				bipedHead.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.1745F, 0.0F, 0.0F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 18, 25, -3.0F, -3.7F, -0.3F, 6, 5, 1, 0.0F, false));

				horn[0][0] = new ModelRenderer(this);
				horn[0][0].setRotationPoint(-2.5F, -4.75F, -2.5F);
				bipedHead.addChild(horn[0][0]);
				setRotationAngle(horn[0][0], 0.0F, 0.0F, -0.4363F);
				horn[0][0].cubeList.add(new ModelBox(horn[0][0], 0, 0, 0.0F, -0.45F, -0.5F, 1, 1, 1, 0.0F, false));
				horn[0][1] = new ModelRenderer(this);
				horn[0][1].setRotationPoint(0.0F, -0.25F, 0.0F);
				horn[0][0].addChild(horn[0][1]);
				setRotationAngle(horn[0][1], 0.0F, 0.0F, 0.0873F);
				horn[0][1].cubeList.add(new ModelBox(horn[0][1], 0, 0, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, false));
				horn[0][2] = new ModelRenderer(this);
				horn[0][2].setRotationPoint(0.0F, -0.5F, 0.0F);
				horn[0][1].addChild(horn[0][2]);
				setRotationAngle(horn[0][2], 0.0873F, 0.0F, 0.0F);
				horn[0][2].cubeList.add(new ModelBox(horn[0][2], 0, 0, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.1F, false));
				horn[0][3] = new ModelRenderer(this);
				horn[0][3].setRotationPoint(0.0F, -0.5F, 0.0F);
				horn[0][2].addChild(horn[0][3]);
				setRotationAngle(horn[0][3], 0.0873F, 0.0F, 0.0F);
				horn[0][3].cubeList.add(new ModelBox(horn[0][3], 0, 0, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.15F, false));
				horn[0][4] = new ModelRenderer(this);
				horn[0][4].setRotationPoint(0.0F, -0.6499F, 0.0253F);
				horn[0][3].addChild(horn[0][4]);
				setRotationAngle(horn[0][4], 0.0F, 0.0F, -0.0873F);
				horn[0][4].cubeList.add(new ModelBox(horn[0][4], 0, 2, 0.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, false));
				horn[1][0] = new ModelRenderer(this);
				horn[1][0].setRotationPoint(2.5F, -4.75F, -2.5F);
				bipedHead.addChild(horn[1][0]);
				setRotationAngle(horn[1][0], 0.0F, 0.0F, 0.4363F);
				horn[1][0].cubeList.add(new ModelBox(horn[1][0], 0, 0, -1.0F, -0.45F, -0.5F, 1, 1, 1, 0.0F, true));
				horn[1][1] = new ModelRenderer(this);
				horn[1][1].setRotationPoint(0.0F, -0.25F, 0.0F);
				horn[1][0].addChild(horn[1][1]);
				setRotationAngle(horn[1][1], 0.0F, 0.0F, -0.0873F);
				horn[1][1].cubeList.add(new ModelBox(horn[1][1], 0, 0, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, true));
				horn[1][2] = new ModelRenderer(this);
				horn[1][2].setRotationPoint(0.0F, -0.5F, 0.0F);
				horn[1][1].addChild(horn[1][2]);
				setRotationAngle(horn[1][2], -0.0873F, 0.0F, 0.0F);
				horn[1][2].cubeList.add(new ModelBox(horn[1][2], 0, 0, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.1F, true));
				horn[1][3] = new ModelRenderer(this);
				horn[1][3].setRotationPoint(0.0F, -0.5F, 0.0F);
				horn[1][2].addChild(horn[1][3]);
				setRotationAngle(horn[1][3], -0.0873F, 0.0F, 0.0F);
				horn[1][3].cubeList.add(new ModelBox(horn[1][3], 0, 0, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.15F, true));
				horn[1][4] = new ModelRenderer(this);
				horn[1][4].setRotationPoint(0.0F, -0.6499F, 0.0253F);
				horn[1][3].addChild(horn[1][4]);
				setRotationAngle(horn[1][4], 0.0F, 0.0F, 0.0873F);
				horn[1][4].cubeList.add(new ModelBox(horn[1][4], 0, 2, -1.0F, -0.75F, -0.5F, 1, 1, 1, -0.05F, true));

				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-3.0F, -6.0F, -1.5F);
				bipedBody.addChild(bipedRightArm);


				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightArm.addChild(bone);
				setRotationAngle(bone, 0.0F, 0.3491F, 0.0F);


				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(-0.287F, 0.2867F, -1.7153F);
				bone.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.2618F, -0.5672F, 0.0F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 12, 31, -1.0F, -0.9F, -1.5F, 2, 2, 3, -0.4F, false));

				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(-0.9F, 0.25F, -0.3F);
				bone.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.1309F, 0.0F, 0.0F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 30, 35, -0.8F, -1.2F, -1.2F, 2, 2, 2, -0.4F, false));

				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(-0.5869F, -0.0397F, 0.43F);
				bone.addChild(cube_r8);
				setRotationAngle(cube_r8, 0.1309F, -0.5672F, -0.0873F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 34, 16, -1.1F, -1.0F, -1.5F, 2, 2, 2, -0.4F, false));

				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(3.0F, -6.0F, -1.5F);
				bipedBody.addChild(bipedLeftArm);


				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftArm.addChild(bone5);
				setRotationAngle(bone5, 0.0F, -0.3491F, 0.0F);


				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(0.287F, 0.2867F, -1.7153F);
				bone5.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.2618F, 0.5672F, 0.0F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 12, 31, -1.0F, -0.9F, -1.5F, 2, 2, 3, -0.4F, true));

				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(0.9F, 0.25F, -0.3F);
				bone5.addChild(cube_r10);
				setRotationAngle(cube_r10, 0.1309F, 0.0F, 0.0F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 30, 35, -1.2F, -1.2F, -1.2F, 2, 2, 2, -0.4F, true));

				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(0.5869F, -0.0397F, 0.43F);
				bone5.addChild(cube_r11);
				setRotationAngle(cube_r11, 0.1309F, 0.5672F, 0.0873F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 34, 16, -0.9F, -1.0F, -1.5F, 2, 2, 2, -0.4F, true));

				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-2.75F, -3.75F, -3.0F);
				bipedBody.addChild(bipedRightLeg);


				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(-3.0464F, 4.5F, -0.8128F);
				bipedRightLeg.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.0F, -0.2618F, 0.0F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 22, 32, -1.1F, -0.5F, -1.5F, 2, 1, 3, -0.1F, false));

				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(-3.1083F, 4.151F, -0.8684F);
				bipedRightLeg.addChild(cube_r13);
				setRotationAngle(cube_r13, -0.1745F, -0.1745F, 0.7418F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 33, 12, -0.5F, -0.5F, -1.5F, 1, 1, 3, -0.1F, false));

				RightLeg1 = new ModelRenderer(this);
				RightLeg1.setRotationPoint(0.5F, 3.6667F, 2.5333F);
				bipedRightLeg.addChild(RightLeg1);
				setRotationAngle(RightLeg1, 0.48F, -0.2618F, 0.0436F);


				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(-2.7866F, -3.3303F, -0.9362F);
				RightLeg1.addChild(cube_r14);
				setRotationAngle(cube_r14, -0.3927F, 0.3491F, 0.6545F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 29, 28, -1.0F, -2.5F, -1.4F, 3, 4, 3, -0.1F, false));

				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(-3.0F, -1.2667F, -1.4333F);
				RightLeg1.addChild(cube_r15);
				setRotationAngle(cube_r15, -0.5236F, 0.0F, 0.0F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 30, -1.5F, -1.7F, -2.0F, 3, 3, 3, -0.1F, false));

				Legdetail = new ModelRenderer(this);
				Legdetail.setRotationPoint(-3.0F, -1.2667F, -1.4333F);
				RightLeg1.addChild(Legdetail);


				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(2.75F, -3.75F, -3.0F);
				bipedBody.addChild(bipedLeftLeg);


				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(3.0464F, 4.5F, -0.8128F);
				bipedLeftLeg.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.0F, 0.2618F, 0.0F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 22, 32, -0.9F, -0.5F, -1.5F, 2, 1, 3, -0.1F, true));

				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(3.1083F, 4.151F, -0.8684F);
				bipedLeftLeg.addChild(cube_r17);
				setRotationAngle(cube_r17, -0.1745F, 0.1745F, -0.7418F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 33, 12, -0.5F, -0.5F, -1.5F, 1, 1, 3, -0.1F, true));

				RightLeg4 = new ModelRenderer(this);
				RightLeg4.setRotationPoint(-0.5F, 3.6667F, 2.5333F);
				bipedLeftLeg.addChild(RightLeg4);
				setRotationAngle(RightLeg4, 0.48F, 0.2618F, -0.0436F);


				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(2.7866F, -3.3303F, -0.9362F);
				RightLeg4.addChild(cube_r18);
				setRotationAngle(cube_r18, -0.3927F, -0.3491F, -0.6545F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 29, 28, -2.0F, -2.5F, -1.4F, 3, 4, 3, -0.1F, true));

				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(3.0F, -1.2667F, -1.4333F);
				RightLeg4.addChild(cube_r19);
				setRotationAngle(cube_r19, -0.5236F, 0.0F, 0.0F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 30, -1.5F, -1.7F, -2.0F, 3, 3, 3, -0.1F, true));

				Legdetail2 = new ModelRenderer(this);
				Legdetail2.setRotationPoint(3.0F, -1.2667F, -1.4333F);
				RightLeg4.addChild(Legdetail2);

				tails = new ModelRenderer(this);
				tails.setRotationPoint(0.0F, 22.75F, 3.0F);
				//setRotationAngle(tails, -1.0472F, 0.0F, 0.0F);

				tail[0][0] = new ModelRenderer(this);
				tail[0][0].setRotationPoint(1.25F, 0.0F, 0.0F);
				tails.addChild(tail[0][0]);
				setRotationAngle(tail[0][0], -1.2217F, 1.309F, 0.0F);
				tail[0][0].cubeList.add(new ModelBox(tail[0][0], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

				tail[0][1] = new ModelRenderer(this);
				tail[0][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][0].addChild(tail[0][1]);
				setRotationAngle(tail[0][1], 0.2618F, 0.0F, 0.0F);
				tail[0][1].cubeList.add(new ModelBox(tail[0][1], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

				tail[0][2] = new ModelRenderer(this);
				tail[0][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][1].addChild(tail[0][2]);
				setRotationAngle(tail[0][2], 0.2618F, 0.0F, 0.0F);
				tail[0][2].cubeList.add(new ModelBox(tail[0][2], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

				tail[0][3] = new ModelRenderer(this);
				tail[0][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][2].addChild(tail[0][3]);
				setRotationAngle(tail[0][3], 0.2618F, 0.0F, 0.0F);
				tail[0][3].cubeList.add(new ModelBox(tail[0][3], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

				tail[0][4] = new ModelRenderer(this);
				tail[0][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][3].addChild(tail[0][4]);
				setRotationAngle(tail[0][4], 0.2618F, 0.0F, 0.0F);
				tail[0][4].cubeList.add(new ModelBox(tail[0][4], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[0][5] = new ModelRenderer(this);
				tail[0][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][4].addChild(tail[0][5]);
				setRotationAngle(tail[0][5], 0.2618F, 0.0F, 0.0F);
				tail[0][5].cubeList.add(new ModelBox(tail[0][5], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[1][0] = new ModelRenderer(this);
				tail[1][0].setRotationPoint(0.75F, 0.0F, 0.0F);
				tails.addChild(tail[1][0]);
				setRotationAngle(tail[1][0], -0.7854F, 0.7854F, 0.0F);
				tail[1][0].cubeList.add(new ModelBox(tail[1][0], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

				tail[1][1] = new ModelRenderer(this);
				tail[1][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][0].addChild(tail[1][1]);
				setRotationAngle(tail[1][1], 0.2618F, 0.0F, 0.0F);
				tail[1][1].cubeList.add(new ModelBox(tail[1][1], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

				tail[1][2] = new ModelRenderer(this);
				tail[1][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][1].addChild(tail[1][2]);
				setRotationAngle(tail[1][2], 0.2618F, 0.0F, 0.0F);
				tail[1][2].cubeList.add(new ModelBox(tail[1][2], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

				tail[1][3] = new ModelRenderer(this);
				tail[1][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][2].addChild(tail[1][3]);
				setRotationAngle(tail[1][3], 0.2618F, 0.0F, 0.0F);
				tail[1][3].cubeList.add(new ModelBox(tail[1][3], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

				tail[1][4] = new ModelRenderer(this);
				tail[1][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][3].addChild(tail[1][4]);
				setRotationAngle(tail[1][4], -0.2618F, 0.0F, 0.0F);
				tail[1][4].cubeList.add(new ModelBox(tail[1][4], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[1][5] = new ModelRenderer(this);
				tail[1][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][4].addChild(tail[1][5]);
				setRotationAngle(tail[1][5], -0.2618F, 0.0F, 0.0F);
				tail[1][5].cubeList.add(new ModelBox(tail[1][5], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[2][0] = new ModelRenderer(this);
				tail[2][0].setRotationPoint(0.25F, 0.0F, 0.0F);
				tails.addChild(tail[2][0]);
				setRotationAngle(tail[2][0], -1.0472F, 0.2618F, 0.0F);
				tail[2][0].cubeList.add(new ModelBox(tail[2][0], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

				tail[2][1] = new ModelRenderer(this);
				tail[2][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][0].addChild(tail[2][1]);
				setRotationAngle(tail[2][1], 0.2618F, 0.0F, 0.0F);
				tail[2][1].cubeList.add(new ModelBox(tail[2][1], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

				tail[2][2] = new ModelRenderer(this);
				tail[2][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][1].addChild(tail[2][2]);
				setRotationAngle(tail[2][2], 0.2618F, 0.0F, 0.0F);
				tail[2][2].cubeList.add(new ModelBox(tail[2][2], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

				tail[2][3] = new ModelRenderer(this);
				tail[2][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][2].addChild(tail[2][3]);
				setRotationAngle(tail[2][3], 0.2618F, 0.0F, 0.0F);
				tail[2][3].cubeList.add(new ModelBox(tail[2][3], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

				tail[2][4] = new ModelRenderer(this);
				tail[2][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][3].addChild(tail[2][4]);
				setRotationAngle(tail[2][4], 0.2618F, 0.0F, 0.0F);
				tail[2][4].cubeList.add(new ModelBox(tail[2][4], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[2][5] = new ModelRenderer(this);
				tail[2][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][4].addChild(tail[2][5]);
				setRotationAngle(tail[2][5], -0.2618F, 0.0F, 0.0F);
				tail[2][5].cubeList.add(new ModelBox(tail[2][5], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[3][0] = new ModelRenderer(this);
				tail[3][0].setRotationPoint(-0.25F, 0.0F, 0.0F);
				tails.addChild(tail[3][0]);
				setRotationAngle(tail[3][0], -0.7854F, -0.2618F, 0.0F);
				tail[3][0].cubeList.add(new ModelBox(tail[3][0], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

				tail[3][1] = new ModelRenderer(this);
				tail[3][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][0].addChild(tail[3][1]);
				setRotationAngle(tail[3][1], -0.2618F, 0.0F, 0.0F);
				tail[3][1].cubeList.add(new ModelBox(tail[3][1], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

				tail[3][2] = new ModelRenderer(this);
				tail[3][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][1].addChild(tail[3][2]);
				setRotationAngle(tail[3][2], 0.2618F, 0.0F, 0.0F);
				tail[3][2].cubeList.add(new ModelBox(tail[3][2], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

				tail[3][3] = new ModelRenderer(this);
				tail[3][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][2].addChild(tail[3][3]);
				setRotationAngle(tail[3][3], 0.2618F, 0.0F, 0.0F);
				tail[3][3].cubeList.add(new ModelBox(tail[3][3], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

				tail[3][4] = new ModelRenderer(this);
				tail[3][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][3].addChild(tail[3][4]);
				setRotationAngle(tail[3][4], 0.2618F, 0.0F, 0.0F);
				tail[3][4].cubeList.add(new ModelBox(tail[3][4], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[3][5] = new ModelRenderer(this);
				tail[3][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][4].addChild(tail[3][5]);
				setRotationAngle(tail[3][5], -0.2618F, 0.0F, 0.0F);
				tail[3][5].cubeList.add(new ModelBox(tail[3][5], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[4][0] = new ModelRenderer(this);
				tail[4][0].setRotationPoint(-0.75F, 0.0F, 0.0F);
				tails.addChild(tail[4][0]);
				setRotationAngle(tail[4][0], -0.8727F, -0.7854F, 0.0F);
				tail[4][0].cubeList.add(new ModelBox(tail[4][0], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

				tail[4][1] = new ModelRenderer(this);
				tail[4][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[4][0].addChild(tail[4][1]);
				setRotationAngle(tail[4][1], 0.2618F, 0.0F, 0.0F);
				tail[4][1].cubeList.add(new ModelBox(tail[4][1], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

				tail[4][2] = new ModelRenderer(this);
				tail[4][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[4][1].addChild(tail[4][2]);
				setRotationAngle(tail[4][2], 0.2618F, 0.0F, 0.0F);
				tail[4][2].cubeList.add(new ModelBox(tail[4][2], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

				tail[4][3] = new ModelRenderer(this);
				tail[4][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[4][2].addChild(tail[4][3]);
				setRotationAngle(tail[4][3], -0.2618F, 0.0F, 0.0F);
				tail[4][3].cubeList.add(new ModelBox(tail[4][3], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

				tail[4][4] = new ModelRenderer(this);
				tail[4][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[4][3].addChild(tail[4][4]);
				setRotationAngle(tail[4][4], -0.2618F, 0.0F, 0.0F);
				tail[4][4].cubeList.add(new ModelBox(tail[4][4], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[4][5] = new ModelRenderer(this);
				tail[4][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[4][4].addChild(tail[4][5]);
				setRotationAngle(tail[4][5], -0.2618F, 0.0F, 0.0F);
				tail[4][5].cubeList.add(new ModelBox(tail[4][5], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[5][0] = new ModelRenderer(this);
				tail[5][0].setRotationPoint(-1.25F, 0.0F, 0.0F);
				tails.addChild(tail[5][0]);
				setRotationAngle(tail[5][0], -1.2217F, -1.309F, 0.0F);
				tail[5][0].cubeList.add(new ModelBox(tail[5][0], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));

				tail[5][1] = new ModelRenderer(this);
				tail[5][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[5][0].addChild(tail[5][1]);
				setRotationAngle(tail[5][1], 0.2618F, 0.0F, 0.0F);
				tail[5][1].cubeList.add(new ModelBox(tail[5][1], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.15F, false));

				tail[5][2] = new ModelRenderer(this);
				tail[5][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[5][1].addChild(tail[5][2]);
				setRotationAngle(tail[5][2], 0.2618F, 0.0F, 0.0F);
				tail[5][2].cubeList.add(new ModelBox(tail[5][2], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.1F, false));

				tail[5][3] = new ModelRenderer(this);
				tail[5][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[5][2].addChild(tail[5][3]);
				setRotationAngle(tail[5][3], 0.2618F, 0.0F, 0.0F);
				tail[5][3].cubeList.add(new ModelBox(tail[5][3], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.05F, false));

				tail[5][4] = new ModelRenderer(this);
				tail[5][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[5][3].addChild(tail[5][4]);
				setRotationAngle(tail[5][4], -0.2618F, 0.0F, 0.0F);
				tail[5][4].cubeList.add(new ModelBox(tail[5][4], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				tail[5][5] = new ModelRenderer(this);
				tail[5][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[5][4].addChild(tail[5][5]);
				setRotationAngle(tail[5][5], -0.2618F, 0.0F, 0.0F);
				tail[5][5].cubeList.add(new ModelBox(tail[5][5], 34, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));

				for (int i = 0; i < 2; i++) {
					for (int j = 1; j < 5; j++) {
						hornSwayX[i][j] = (rand.nextFloat() * 0.1745F + 0.0873F) * (rand.nextBoolean() ? -1F : 1F);
						hornSwayZ[i][j] = (rand.nextFloat() * 0.1745F + 0.0873F) * (rand.nextBoolean() ? -1F : 1F);
					}
				}
				for (int i = 0; i < 6; i++) {
					for (int j = 1; j < 6; j++) {
						tailSwayX[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
						tailSwayZ[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
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
				tails.render(f5);
				//super.render(entity, f0, f1, f2, f3, f4, f5);
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
				bipedHead.rotationPointY += -8.75F;
				bipedRightArm.setRotationPoint(-3.0F, -6.0F, -1.5F);
				bipedLeftArm.setRotationPoint(3.0F, -6.0F, -1.5F);
				bipedRightLeg.setRotationPoint(-2.75F, -3.75F, -3.0F);
				bipedLeftLeg.setRotationPoint(2.75F, -3.75F, -3.0F);
				for (int i = 0; i < 2; i++) {
					for (int j = 1; j < 5; j++) {
						horn[i][j].rotateAngleX = -0.1745F + MathHelper.sin(f2 * 0.1F) * hornSwayX[i][j];
						horn[i][j].rotateAngleZ = MathHelper.cos(f2 * 0.1F) * hornSwayZ[i][j];
					}
				}
				for (int i = 0; i < 6; i++) {
					for (int j = 1; j < 6; j++) {
						tail[i][j].rotateAngleX = 0.2618F + MathHelper.sin((f2 - j) * 0.05F) * tailSwayX[i][j];
						tail[i][j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.05F) * tailSwayZ[i][j];
						tail[i][j].rotateAngleY = MathHelper.sin((f2 - j) * 0.05F) * tailSwayY[i][j];
					}
				}
				if (((EntityCustom) e).isFaceDown()) {
					bipedBody.rotationPointZ = 8.0F;
					bipedBody.rotateAngleX = 1.1345F;
					bipedHead.rotateAngleX = -0.2618F;
					bipedRightArm.rotateAngleX = -0.7854F;
					bipedLeftArm.rotateAngleX = -0.7854F;
					tails.rotationPointZ = 8.0F;
					tails.rotateAngleX = -1.0472F;
				} else {
					bipedBody.rotationPointZ = 3.0F;
					bipedBody.rotateAngleX = 0.0F;
					tails.rotationPointZ = 3.0F;
					tails.rotateAngleX = 0.0F;
				}
			}
		}
	}
}
