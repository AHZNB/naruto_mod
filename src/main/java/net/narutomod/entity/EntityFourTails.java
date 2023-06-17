
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
import net.minecraft.util.SoundEvent;
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
	private static final float MODELSCALE = 18.5F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntityFourTails(ElementsNarutomodMod instance) {
		super(instance, 581);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "four_tails"), ENTITYID).name("four_tails")
		 .tracker(96, 3, true).egg(-3407872, -6697984).build());
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
			if (player instanceof EntityPlayer) {
				if (net.narutomod.gui.GuiNinjaScroll.enableJutsu((EntityPlayer)player,
				 (ItemYooton.RangedItem)ItemYooton.block, ItemYooton.CHAKRAMODE, true) != null) {
					if (!ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemKaton.block)) {
						ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, new ItemStack(ItemKaton.block));
					}
					if (!ProcedureUtils.hasItemInInventory((EntityPlayer)player, ItemDoton.block)) {
						ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, new ItemStack(ItemDoton.block));
					}
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
			this.setSize(MODELSCALE * 0.45F, MODELSCALE * 1.09375F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.45F, MODELSCALE * 1.09375F);
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
			this.setSize(this.width, MODELSCALE * (down ? 0.75F : 1.09375F));
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
		public SoundEvent getAmbientSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:fourtails_idle"+(this.rand.nextInt(2)+1)));
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:fourtails_hurt"));
		}

		@Override
		public SoundEvent getDeathSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:fourtails_defeat"));
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityTailedBeast.ClientOnly.Renderer<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/fourtails.png");
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelFourTails(), MODELSCALE * 0.25F);
			}
	
		 	@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
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
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer cube_r6;
			private final ModelRenderer cube_r7;
			private final ModelRenderer cube_r8;
			private final ModelRenderer cube_r9;
			//private final ModelRenderer bipedHead;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			private final ModelRenderer cube_r13;
			private final ModelRenderer cube_r14;
			private final ModelRenderer cube_r15;
			private final ModelRenderer cube_r16;
			private final ModelRenderer nose;
			private final ModelRenderer cube_r17;
			private final ModelRenderer cube_r18;
			private final ModelRenderer cube_r19;
			private final ModelRenderer cube_r20;
			private final ModelRenderer tooth;
			private final ModelRenderer cube_r21;
			private final ModelRenderer tooth2;
			private final ModelRenderer cube_r22;
			private final ModelRenderer horn;
			private final ModelRenderer cube_r23;
			private final ModelRenderer cube_r24;
			private final ModelRenderer hornEnd;
			private final ModelRenderer cube_r25;
			private final ModelRenderer cube_r26;
			private final ModelRenderer horn2;
			private final ModelRenderer cube_r27;
			private final ModelRenderer cube_r28;
			private final ModelRenderer hornEnd2;
			private final ModelRenderer cube_r29;
			private final ModelRenderer cube_r30;
			private final ModelRenderer jaw;
			private final ModelRenderer cube_r31;
			private final ModelRenderer beard;
			private final ModelRenderer cube_r32;
			private final ModelRenderer cube_r33;
			private final ModelRenderer bone;
			private final ModelRenderer cube_r56;
			private final ModelRenderer cube_r57;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer cube_r34;
			private final ModelRenderer cube_r35;
			private final ModelRenderer right4Arm;
			private final ModelRenderer cube_r36;
			private final ModelRenderer cube_r37;
			private final ModelRenderer cube_r38;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer cube_r39;
			private final ModelRenderer cube_r40;
			private final ModelRenderer left4Arm;
			private final ModelRenderer cube_r41;
			private final ModelRenderer cube_r42;
			private final ModelRenderer cube_r43;
			//private final ModelRenderer bipedRightLeg;
			private final ModelRenderer rightLeg;
			private final ModelRenderer cube_r44;
			private final ModelRenderer rightCalf;
			private final ModelRenderer cube_r45;
			private final ModelRenderer cube_r46;
			private final ModelRenderer cube_r47;
			private final ModelRenderer cube_r48;
			private final ModelRenderer cube_r49;
			//private final ModelRenderer bipedLeftLeg;
			private final ModelRenderer leftLeg;
			private final ModelRenderer cube_r50;
			private final ModelRenderer leftCalf;
			private final ModelRenderer cube_r51;
			private final ModelRenderer cube_r52;
			private final ModelRenderer cube_r53;
			private final ModelRenderer cube_r54;
			private final ModelRenderer cube_r55;
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
				eyes.setRotationPoint(0.0F, -8.5F, -8.95F);
				bipedHeadwear.addChild(eyes);
				eyes.cubeList.add(new ModelBox(eyes, 47, 0, -2.0F, -3.35F, -3.25F, 4, 1, 0, 0.0F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 19.5F, 6.75F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 22, 13, -3.0F, -3.75F, -2.05F, 6, 4, 4, 0.0F, false));
		
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, -3.8F, -5.75F);
				bipedBody.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.829F, 0.0F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 29, 39, -2.45F, -3.6734F, -0.7033F, 5, 9, 1, 0.0F, false));
		
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(2.05F, -3.7649F, -1.6454F);
				bipedBody.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.6318F, -0.1841F, 0.2451F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 13, -3.2F, -3.5F, -3.0F, 5, 6, 6, 0.0F, true));
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(-2.05F, -3.7649F, -1.6454F);
				bipedBody.addChild(cube_r3);
				setRotationAngle(cube_r3, 0.6318F, 0.1841F, -0.2451F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 13, -1.8F, -3.5F, -3.0F, 5, 6, 6, 0.0F, false));
		
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(0.0F, -7.8F, -5.75F);
				bipedBody.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.8272F, 0.0668F, -0.0562F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 40, 24, 0.05F, -1.3F, -3.8F, 5, 4, 1, 0.0F, true));
		
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.0F, -7.8F, -5.75F);
				bipedBody.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.8272F, -0.0668F, 0.0562F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 40, 24, -5.05F, -1.3F, -3.8F, 5, 4, 1, 0.0F, false));
		
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(0.0F, -7.8F, -5.75F);
				bipedBody.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.8727F, 0.0F, 0.0F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 0, -5.0F, -2.3F, -3.2F, 10, 6, 7, 0.0F, false));
		
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(2.0F, -12.15F, -5.1F);
				bipedBody.addChild(cube_r7);
				setRotationAngle(cube_r7, -0.0097F, -0.218F, 1.0046F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 18, 21, -1.0F, 0.0F, -5.0F, 6, 5, 5, 0.0F, true));
		
				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(-2.0F, -12.15F, -5.1F);
				bipedBody.addChild(cube_r8);
				setRotationAngle(cube_r8, -0.0097F, 0.218F, -1.0046F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 18, 21, -5.0F, 0.0F, -5.0F, 6, 5, 5, 0.0F, false));
		
				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(0.1F, -12.0F, -6.15F);
				bipedBody.addChild(cube_r9);
				setRotationAngle(cube_r9, -0.2182F, 0.0F, 0.0F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 34, 0, -2.1F, -0.4F, -2.0F, 4, 5, 3, 0.0F, false));
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, -8.5F, -8.95F);
				bipedBody.addChild(bipedHead);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 37, 16, -2.0F, -4.0F, -3.2F, 4, 3, 5, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 41, 39, -2.0F, -1.2F, -2.2F, 4, 2, 2, 0.0F, false));
		
				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(1.4188F, -5.1197F, -0.5952F);
				bipedHead.addChild(cube_r10);
				setRotationAngle(cube_r10, -0.047F, 0.0736F, 1.0018F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 18, 41, 0.4237F, 0.1663F, -1.9346F, 1, 1, 4, 0.0F, true));
		
				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(-1.4188F, -5.1197F, -0.5952F);
				bipedHead.addChild(cube_r11);
				setRotationAngle(cube_r11, -0.047F, -0.0736F, -1.0018F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 18, 41, -1.4237F, 0.1663F, -1.9346F, 1, 1, 4, 0.0F, false));
		
				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(0.0F, -3.3F, -1.0F);
				bipedHead.addChild(cube_r12);
				setRotationAngle(cube_r12, -0.0873F, 0.0F, 0.0F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 8, 40, -1.5F, -1.45F, -1.7F, 3, 1, 4, 0.0F, false));
		
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(0.0F, -1.0F, -3.2F);
				bipedHead.addChild(cube_r13);
				setRotationAngle(cube_r13, 0.2182F, 0.0F, 0.0F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 45, -2.0F, 0.0F, 0.0F, 4, 2, 1, 0.0F, false));
		
				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(0.0F, -1.0F, -3.0F);
				bipedHead.addChild(cube_r14);
				setRotationAngle(cube_r14, 0.0175F, 0.0F, 0.0F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 14, 28, -1.0F, -0.55F, -0.13F, 2, 1, 0, 0.0F, false));
		
				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(0.0F, -2.7F, -2.0F);
				bipedHead.addChild(cube_r15);
				setRotationAngle(cube_r15, -0.1636F, 0.0157F, 0.1363F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 25, 1.1F, -1.3F, -0.25F, 1, 2, 1, 0.0F, true));
		
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(0.0F, -2.7F, -2.0F);
				bipedHead.addChild(cube_r16);
				setRotationAngle(cube_r16, -0.1636F, -0.0157F, -0.1363F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 25, -2.1F, -1.3F, -0.25F, 1, 2, 1, 0.0F, false));
		
				nose = new ModelRenderer(this);
				nose.setRotationPoint(0.0F, -2.1F, -3.1F);
				bipedHead.addChild(nose);
				
		
				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(0.45F, 0.0069F, -0.3071F);
				nose.addChild(cube_r17);
				setRotationAngle(cube_r17, -0.2407F, -0.511F, 0.1195F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 16, 17, 0.0F, -0.5174F, -0.0429F, 1, 1, 1, 0.0F, true));
		
				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(-0.45F, 0.0069F, -0.3071F);
				nose.addChild(cube_r18);
				setRotationAngle(cube_r18, -0.2407F, 0.511F, -0.1195F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 16, 17, -1.0F, -0.5174F, -0.0429F, 1, 1, 1, 0.0F, false));
		
				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(0.0F, 0.4056F, 0.1559F);
				nose.addChild(cube_r19);
				setRotationAngle(cube_r19, -0.2094F, 0.0F, 0.0F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 27, 2, -0.5F, -0.75F, -0.6F, 1, 1, 1, 0.0F, false));
		
				cube_r20 = new ModelRenderer(this);
				cube_r20.setRotationPoint(0.0F, -1.1F, 0.1F);
				nose.addChild(cube_r20);
				setRotationAngle(cube_r20, -0.2182F, 0.0F, 0.0F);
				cube_r20.cubeList.add(new ModelBox(cube_r20, 16, 13, -0.5F, 0.1F, -0.2F, 1, 1, 3, 0.0F, false));
		
				tooth = new ModelRenderer(this);
				tooth.setRotationPoint(-1.2003F, -1.0765F, -2.89F);
				bipedHead.addChild(tooth);
				setRotationAngle(tooth, -0.1309F, 0.0F, 0.0436F);
				tooth.cubeList.add(new ModelBox(tooth, 21, 13, -0.4517F, -0.2341F, -0.5F, 1, 2, 1, -0.2F, false));
		
				cube_r21 = new ModelRenderer(this);
				cube_r21.setRotationPoint(-0.8347F, 1.4705F, -0.36F);
				tooth.addChild(cube_r21);
				setRotationAngle(cube_r21, 0.0F, 0.0F, -0.0436F);
				cube_r21.cubeList.add(new ModelBox(cube_r21, 0, 17, 0.3832F, -0.2F, -0.14F, 1, 1, 1, -0.23F, false));
		
				tooth2 = new ModelRenderer(this);
				tooth2.setRotationPoint(1.2003F, -1.0765F, -2.89F);
				bipedHead.addChild(tooth2);
				setRotationAngle(tooth2, -0.1309F, 0.0F, -0.0436F);
				tooth2.cubeList.add(new ModelBox(tooth2, 21, 13, -0.5483F, -0.2341F, -0.5F, 1, 2, 1, -0.2F, true));
		
				cube_r22 = new ModelRenderer(this);
				cube_r22.setRotationPoint(0.8347F, 1.4705F, -0.36F);
				tooth2.addChild(cube_r22);
				setRotationAngle(cube_r22, 0.0F, 0.0F, 0.0436F);
				cube_r22.cubeList.add(new ModelBox(cube_r22, 0, 17, -1.3832F, -0.2F, -0.14F, 1, 1, 1, -0.23F, true));
		
				horn = new ModelRenderer(this);
				horn.setRotationPoint(-0.8F, -3.8F, -2.6F);
				bipedHead.addChild(horn);
				setRotationAngle(horn, 0.0F, 0.0873F, 0.1745F);
				horn.cubeList.add(new ModelBox(horn, 30, 5, -1.6427F, -0.6221F, -0.78F, 1, 1, 1, 0.0F, false));
		
				cube_r23 = new ModelRenderer(this);
				cube_r23.setRotationPoint(-0.2F, 0.5F, -0.5F);
				horn.addChild(cube_r23);
				setRotationAngle(cube_r23, 0.0F, 0.0F, -0.0436F);
				cube_r23.cubeList.add(new ModelBox(cube_r23, 26, 31, 0.0F, -0.8F, -0.3F, 1, 1, 1, 0.0F, false));
		
				cube_r24 = new ModelRenderer(this);
				cube_r24.setRotationPoint(0.0F, 0.0F, 0.0F);
				horn.addChild(cube_r24);
				setRotationAngle(cube_r24, 0.0F, 0.0F, 0.3054F);
				cube_r24.cubeList.add(new ModelBox(cube_r24, 30, 3, -0.8F, -0.4F, -0.78F, 1, 1, 1, 0.0F, false));
		
				hornEnd = new ModelRenderer(this);
				hornEnd.setRotationPoint(-1.6F, 0.4F, 0.0F);
				horn.addChild(hornEnd);
				setRotationAngle(hornEnd, 0.0F, 0.0873F, 0.0F);
				
		
				cube_r25 = new ModelRenderer(this);
				cube_r25.setRotationPoint(-1.0593F, -1.6324F, 0.0137F);
				hornEnd.addChild(cube_r25);
				setRotationAngle(cube_r25, 0.0F, 0.0F, 0.5672F);
				cube_r25.cubeList.add(new ModelBox(cube_r25, 27, 4, -0.8201F, -0.5586F, -0.73F, 1, 1, 1, -0.1F, false));
		
				cube_r26 = new ModelRenderer(this);
				cube_r26.setRotationPoint(0.1567F, -0.2221F, 0.0137F);
				hornEnd.addChild(cube_r26);
				setRotationAngle(cube_r26, 0.0F, 0.0F, 0.7854F);
				cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 5, -1.9F, -0.7F, -0.73F, 2, 1, 1, 0.0F, false));
		
				horn2 = new ModelRenderer(this);
				horn2.setRotationPoint(0.8F, -3.8F, -2.6F);
				bipedHead.addChild(horn2);
				setRotationAngle(horn2, 0.0F, -0.0873F, -0.1745F);
				horn2.cubeList.add(new ModelBox(horn2, 30, 5, 0.6427F, -0.6221F, -0.78F, 1, 1, 1, 0.0F, true));
		
				cube_r27 = new ModelRenderer(this);
				cube_r27.setRotationPoint(0.2F, 0.5F, -0.5F);
				horn2.addChild(cube_r27);
				setRotationAngle(cube_r27, 0.0F, 0.0F, 0.0436F);
				cube_r27.cubeList.add(new ModelBox(cube_r27, 26, 31, -1.0F, -0.8F, -0.3F, 1, 1, 1, 0.0F, true));
		
				cube_r28 = new ModelRenderer(this);
				cube_r28.setRotationPoint(0.0F, 0.0F, 0.0F);
				horn2.addChild(cube_r28);
				setRotationAngle(cube_r28, 0.0F, 0.0F, -0.3054F);
				cube_r28.cubeList.add(new ModelBox(cube_r28, 30, 3, -0.2F, -0.4F, -0.78F, 1, 1, 1, 0.0F, true));
		
				hornEnd2 = new ModelRenderer(this);
				hornEnd2.setRotationPoint(1.6F, 0.4F, 0.0F);
				horn2.addChild(hornEnd2);
				setRotationAngle(hornEnd2, 0.0F, -0.0873F, 0.0F);
				
		
				cube_r29 = new ModelRenderer(this);
				cube_r29.setRotationPoint(1.0593F, -1.6324F, 0.0137F);
				hornEnd2.addChild(cube_r29);
				setRotationAngle(cube_r29, 0.0F, 0.0F, -0.5672F);
				cube_r29.cubeList.add(new ModelBox(cube_r29, 27, 4, -0.1799F, -0.5586F, -0.73F, 1, 1, 1, -0.1F, true));
		
				cube_r30 = new ModelRenderer(this);
				cube_r30.setRotationPoint(-0.1567F, -0.2221F, 0.0137F);
				hornEnd2.addChild(cube_r30);
				setRotationAngle(cube_r30, 0.0F, 0.0F, -0.7854F);
				cube_r30.cubeList.add(new ModelBox(cube_r30, 0, 5, -0.1F, -0.7F, -0.73F, 2, 1, 1, 0.0F, true));
		
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, -0.9F, -0.9F);
				bipedHead.addChild(jaw);
				setRotationAngle(jaw, 0.3491F, 0.0F, 0.0F);
				jaw.cubeList.add(new ModelBox(jaw, 42, 29, -2.0F, -0.1197F, -2.2598F, 4, 2, 2, 0.0F, false));
		
				cube_r31 = new ModelRenderer(this);
				cube_r31.setRotationPoint(0.0F, -0.2258F, -2.0901F);
				jaw.addChild(cube_r31);
				setRotationAngle(cube_r31, 0.0524F, 0.0F, 0.0F);
				cube_r31.cubeList.add(new ModelBox(cube_r31, 0, 28, -1.0F, -0.34F, -0.049F, 2, 1, 0, 0.0F, false));
		
				beard = new ModelRenderer(this);
				beard.setRotationPoint(0.0F, 1.5803F, -1.0598F);
				jaw.addChild(beard);
				
		
				cube_r32 = new ModelRenderer(this);
				cube_r32.setRotationPoint(0.0F, -0.5F, -0.8F);
				beard.addChild(cube_r32);
				setRotationAngle(cube_r32, 0.0155F, -0.0081F, 0.4799F);
				cube_r32.cubeList.add(new ModelBox(cube_r32, 0, 0, 0.1F, -0.2F, -0.4F, 2, 4, 1, 0.0F, true));
		
				cube_r33 = new ModelRenderer(this);
				cube_r33.setRotationPoint(0.0F, -0.5F, -0.8F);
				beard.addChild(cube_r33);
				setRotationAngle(cube_r33, 0.0155F, 0.0081F, -0.4799F);
				cube_r33.cubeList.add(new ModelBox(cube_r33, 0, 0, -2.1F, -0.2F, -0.4F, 2, 4, 1, 0.0F, false));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.75F);
				beard.addChild(bone);
				setRotationAngle(bone, -0.2182F, 0.0F, 0.0F);
				
				cube_r56 = new ModelRenderer(this);
				cube_r56.setRotationPoint(0.0F, -0.5F, -0.8F);
				bone.addChild(cube_r56);
				setRotationAngle(cube_r56, 0.0155F, -0.0081F, 0.4799F);
				cube_r56.cubeList.add(new ModelBox(cube_r56, 0, 0, 0.1F, -0.2F, -0.4F, 2, 4, 1, 0.0F, true));
		
				cube_r57 = new ModelRenderer(this);
				cube_r57.setRotationPoint(0.0F, -0.5F, -0.8F);
				bone.addChild(cube_r57);
				setRotationAngle(cube_r57, 0.0155F, 0.0081F, -0.4799F);
				cube_r57.cubeList.add(new ModelBox(cube_r57, 0, 0, -2.1F, -0.2F, -0.4F, 2, 4, 1, 0.0F, false));

				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.25F, -9.25F, -6.25F);
				bipedBody.addChild(bipedRightArm);
				
		
				cube_r34 = new ModelRenderer(this);
				cube_r34.setRotationPoint(0.25F, 0.45F, 0.5F);
				bipedRightArm.addChild(cube_r34);
				setRotationAngle(cube_r34, -0.0873F, 0.0F, 0.0F);
				cube_r34.cubeList.add(new ModelBox(cube_r34, 14, 31, -3.85F, 1.4F, -1.7F, 4, 5, 4, 0.0F, false));
		
				cube_r35 = new ModelRenderer(this);
				cube_r35.setRotationPoint(0.25F, 0.45F, 0.5F);
				bipedRightArm.addChild(cube_r35);
				setRotationAngle(cube_r35, 0.0852F, -0.0189F, 0.2174F);
				cube_r35.cubeList.add(new ModelBox(cube_r35, 30, 31, -3.4F, -1.65F, -1.9F, 4, 4, 4, 0.1F, false));
		
				right4Arm = new ModelRenderer(this);
				right4Arm.setRotationPoint(-2.05F, 6.55F, 0.1F);
				bipedRightArm.addChild(right4Arm);
				setRotationAngle(right4Arm, -0.2174F, -0.0189F, -0.0852F);
				
		
				cube_r36 = new ModelRenderer(this);
				cube_r36.setRotationPoint(-1.0F, 7.3199F, -0.3422F);
				right4Arm.addChild(cube_r36);
				setRotationAngle(cube_r36, 0.0F, 0.0F, -0.2182F);
				cube_r36.cubeList.add(new ModelBox(cube_r36, 27, 0, 0.15F, -0.4F, 0.9F, 3, 1, 1, 0.0F, false));
				cube_r36.cubeList.add(new ModelBox(cube_r36, 27, 0, 0.15F, -0.4F, -0.2F, 3, 1, 1, 0.0F, false));
				cube_r36.cubeList.add(new ModelBox(cube_r36, 27, 0, 0.15F, -0.4F, -1.3F, 3, 1, 1, 0.0F, false));
		
				cube_r37 = new ModelRenderer(this);
				cube_r37.setRotationPoint(2.0F, 5.9199F, -1.8422F);
				right4Arm.addChild(cube_r37);
				setRotationAngle(cube_r37, 0.5478F, 0.1149F, -0.1858F);
				cube_r37.cubeList.add(new ModelBox(cube_r37, 14, 25, -1.05F, 0.1F, -0.7F, 1, 2, 1, 0.0F, false));
		
				cube_r38 = new ModelRenderer(this);
				cube_r38.setRotationPoint(2.0F, 5.3199F, 1.6578F);
				right4Arm.addChild(cube_r38);
				setRotationAngle(cube_r38, 0.0F, 0.0F, -0.0436F);
				cube_r38.cubeList.add(new ModelBox(cube_r38, 0, 33, -3.05F, -5.55F, -3.2F, 3, 7, 3, 0.0F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.25F, -9.25F, -6.25F);
				bipedBody.addChild(bipedLeftArm);
				
		
				cube_r39 = new ModelRenderer(this);
				cube_r39.setRotationPoint(-0.25F, 0.45F, 0.5F);
				bipedLeftArm.addChild(cube_r39);
				setRotationAngle(cube_r39, -0.0873F, 0.0F, 0.0F);
				cube_r39.cubeList.add(new ModelBox(cube_r39, 14, 31, -0.15F, 1.4F, -1.7F, 4, 5, 4, 0.0F, true));
		
				cube_r40 = new ModelRenderer(this);
				cube_r40.setRotationPoint(-0.25F, 0.45F, 0.5F);
				bipedLeftArm.addChild(cube_r40);
				setRotationAngle(cube_r40, 0.0852F, 0.0189F, -0.2174F);
				cube_r40.cubeList.add(new ModelBox(cube_r40, 30, 31, -0.6F, -1.65F, -1.9F, 4, 4, 4, 0.1F, true));
		
				left4Arm = new ModelRenderer(this);
				left4Arm.setRotationPoint(2.05F, 6.55F, 0.1F);
				bipedLeftArm.addChild(left4Arm);
				setRotationAngle(left4Arm, -0.2174F, 0.0189F, 0.0852F);
				
		
				cube_r41 = new ModelRenderer(this);
				cube_r41.setRotationPoint(1.0F, 7.3199F, -0.3422F);
				left4Arm.addChild(cube_r41);
				setRotationAngle(cube_r41, 0.0F, 0.0F, 0.2182F);
				cube_r41.cubeList.add(new ModelBox(cube_r41, 27, 0, -3.15F, -0.4F, 0.9F, 3, 1, 1, 0.0F, true));
				cube_r41.cubeList.add(new ModelBox(cube_r41, 27, 0, -3.15F, -0.4F, -0.2F, 3, 1, 1, 0.0F, true));
				cube_r41.cubeList.add(new ModelBox(cube_r41, 27, 0, -3.15F, -0.4F, -1.3F, 3, 1, 1, 0.0F, true));
		
				cube_r42 = new ModelRenderer(this);
				cube_r42.setRotationPoint(-2.0F, 5.9199F, -1.8422F);
				left4Arm.addChild(cube_r42);
				setRotationAngle(cube_r42, 0.5478F, -0.1149F, 0.1858F);
				cube_r42.cubeList.add(new ModelBox(cube_r42, 14, 25, 0.05F, 0.1F, -0.7F, 1, 2, 1, 0.0F, true));
		
				cube_r43 = new ModelRenderer(this);
				cube_r43.setRotationPoint(-2.0F, 5.3199F, 1.6578F);
				left4Arm.addChild(cube_r43);
				setRotationAngle(cube_r43, 0.0F, 0.0F, 0.0436F);
				cube_r43.cubeList.add(new ModelBox(cube_r43, 0, 33, 0.05F, -5.55F, -3.2F, 3, 7, 3, 0.0F, true));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-2.25F, -1.25F, -1.25F);
				bipedBody.addChild(bipedRightLeg);
				setRotationAngle(bipedRightLeg, 0.0F, 0.0F, 0.0F);
				
		
				rightLeg = new ModelRenderer(this);
				rightLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightLeg.addChild(rightLeg);
				setRotationAngle(rightLeg, 0.0873F, -0.48F, -0.3054F);
				
		
				cube_r44 = new ModelRenderer(this);
				cube_r44.setRotationPoint(-0.9937F, 0.103F, 0.6637F);
				rightLeg.addChild(cube_r44);
				setRotationAngle(cube_r44, 0.0872F, 0.0038F, -0.0435F);
				cube_r44.cubeList.add(new ModelBox(cube_r44, 0, 25, -4.05F, -2.3F, -2.2F, 5, 4, 4, 0.0F, false));
		
				rightCalf = new ModelRenderer(this);
				rightCalf.setRotationPoint(-3.9937F, -0.897F, 0.6637F);
				rightLeg.addChild(rightCalf);
				
		
				cube_r45 = new ModelRenderer(this);
				cube_r45.setRotationPoint(-2.0F, 4.5F, 0.0F);
				rightCalf.addChild(cube_r45);
				setRotationAngle(cube_r45, 0.085F, -0.0196F, 0.1824F);
				cube_r45.cubeList.add(new ModelBox(cube_r45, 0, 13, -2.6252F, -0.4893F, 0.597F, 2, 1, 1, 0.0F, false));
		
				cube_r46 = new ModelRenderer(this);
				cube_r46.setRotationPoint(-2.0F, 4.5F, 0.0F);
				rightCalf.addChild(cube_r46);
				setRotationAngle(cube_r46, 0.0852F, -0.0631F, 0.1787F);
				cube_r46.cubeList.add(new ModelBox(cube_r46, 0, 13, -2.6252F, -0.4893F, -0.403F, 2, 1, 1, 0.0F, false));
		
				cube_r47 = new ModelRenderer(this);
				cube_r47.setRotationPoint(-2.0F, 4.5F, 0.0F);
				rightCalf.addChild(cube_r47);
				setRotationAngle(cube_r47, 0.0855F, -0.1066F, 0.175F);
				cube_r47.cubeList.add(new ModelBox(cube_r47, 0, 13, -2.6252F, -0.4893F, -1.403F, 2, 1, 1, 0.0F, false));
		
				cube_r48 = new ModelRenderer(this);
				cube_r48.setRotationPoint(0.0F, 5.4F, -1.6F);
				rightCalf.addChild(cube_r48);
				setRotationAngle(cube_r48, 0.0964F, -0.1924F, 0.2014F);
				cube_r48.cubeList.add(new ModelBox(cube_r48, 0, 15, -3.515F, -0.6262F, -0.3226F, 2, 1, 1, 0.0F, false));
		
				cube_r49 = new ModelRenderer(this);
				cube_r49.setRotationPoint(0.0F, -1.0F, 0.0F);
				rightCalf.addChild(cube_r49);
				setRotationAngle(cube_r49, 0.0832F, -0.0262F, 0.3043F);
				cube_r49.cubeList.add(new ModelBox(cube_r49, 41, 43, -1.0398F, 5.3214F, -1.8743F, 3, 1, 3, 0.0F, false));
				cube_r49.cubeList.add(new ModelBox(cube_r49, 38, 8, -1.0398F, 0.3214F, -1.8743F, 3, 5, 3, 0.0F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(2.25F, -1.25F, -1.25F);
				bipedBody.addChild(bipedLeftLeg);
				setRotationAngle(bipedLeftLeg, 0.0F, 0.0F, 0.0F);
				
		
				leftLeg = new ModelRenderer(this);
				leftLeg.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftLeg.addChild(leftLeg);
				setRotationAngle(leftLeg, 0.0873F, 0.48F, 0.3054F);
				
		
				cube_r50 = new ModelRenderer(this);
				cube_r50.setRotationPoint(0.9937F, 0.103F, 0.6637F);
				leftLeg.addChild(cube_r50);
				setRotationAngle(cube_r50, 0.0872F, -0.0038F, 0.0435F);
				cube_r50.cubeList.add(new ModelBox(cube_r50, 0, 25, -0.95F, -2.3F, -2.2F, 5, 4, 4, 0.0F, true));
		
				leftCalf = new ModelRenderer(this);
				leftCalf.setRotationPoint(3.9937F, -0.897F, 0.6637F);
				leftLeg.addChild(leftCalf);
				
		
				cube_r51 = new ModelRenderer(this);
				cube_r51.setRotationPoint(2.0F, 4.5F, 0.0F);
				leftCalf.addChild(cube_r51);
				setRotationAngle(cube_r51, 0.085F, 0.0196F, -0.1824F);
				cube_r51.cubeList.add(new ModelBox(cube_r51, 0, 13, 0.6252F, -0.4893F, 0.597F, 2, 1, 1, 0.0F, true));
		
				cube_r52 = new ModelRenderer(this);
				cube_r52.setRotationPoint(2.0F, 4.5F, 0.0F);
				leftCalf.addChild(cube_r52);
				setRotationAngle(cube_r52, 0.0852F, 0.0631F, -0.1787F);
				cube_r52.cubeList.add(new ModelBox(cube_r52, 0, 13, 0.6252F, -0.4893F, -0.403F, 2, 1, 1, 0.0F, true));
		
				cube_r53 = new ModelRenderer(this);
				cube_r53.setRotationPoint(2.0F, 4.5F, 0.0F);
				leftCalf.addChild(cube_r53);
				setRotationAngle(cube_r53, 0.0855F, 0.1066F, -0.175F);
				cube_r53.cubeList.add(new ModelBox(cube_r53, 0, 13, 0.6252F, -0.4893F, -1.403F, 2, 1, 1, 0.0F, true));
		
				cube_r54 = new ModelRenderer(this);
				cube_r54.setRotationPoint(0.0F, 5.4F, -1.6F);
				leftCalf.addChild(cube_r54);
				setRotationAngle(cube_r54, 0.0964F, 0.1924F, -0.2014F);
				cube_r54.cubeList.add(new ModelBox(cube_r54, 0, 15, 1.515F, -0.6262F, -0.3226F, 2, 1, 1, 0.0F, true));
		
				cube_r55 = new ModelRenderer(this);
				cube_r55.setRotationPoint(0.0F, -1.0F, 0.0F);
				leftCalf.addChild(cube_r55);
				setRotationAngle(cube_r55, 0.0832F, 0.0262F, -0.3043F);
				cube_r55.cubeList.add(new ModelBox(cube_r55, 41, 43, -1.9602F, 5.3214F, -1.8743F, 3, 1, 3, 0.0F, true));
				cube_r55.cubeList.add(new ModelBox(cube_r55, 38, 8, -1.9602F, 0.3214F, -1.8743F, 3, 5, 3, 0.0F, true));
		
				tails = new ModelRenderer(this);
				tails.setRotationPoint(0.0F, -2.0F, 2.25F);
				bipedBody.addChild(tails);
				//setRotationAngle(tails, -2.0071F, 0.0F, 0.0F);
				
		
				tail[0][0] = new ModelRenderer(this);
				tail[0][0].setRotationPoint(1.5F, 0.0F, 0.0F);
				tails.addChild(tail[0][0]);
				setRotationAngle(tail[0][0], -1.0472F, 1.0472F, 0.0F);
				tail[0][0].cubeList.add(new ModelBox(tail[0][0], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
				tail[0][0].cubeList.add(new ModelBox(tail[0][0], 56, 6, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
		
				tail[0][1] = new ModelRenderer(this);
				tail[0][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][0].addChild(tail[0][1]);
				setRotationAngle(tail[0][1], 0.2618F, 0.0F, 0.0F);
				tail[0][1].cubeList.add(new ModelBox(tail[0][1], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
				tail[0][1].cubeList.add(new ModelBox(tail[0][1], 56, 6, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
		
				tail[0][2] = new ModelRenderer(this);
				tail[0][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][1].addChild(tail[0][2]);
				setRotationAngle(tail[0][2], 0.2618F, 0.0F, 0.0F);
				tail[0][2].cubeList.add(new ModelBox(tail[0][2], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
				tail[0][2].cubeList.add(new ModelBox(tail[0][2], 56, 6, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));
		
				tail[0][3] = new ModelRenderer(this);
				tail[0][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][2].addChild(tail[0][3]);
				setRotationAngle(tail[0][3], 0.2618F, 0.0F, 0.0F);
				tail[0][3].cubeList.add(new ModelBox(tail[0][3], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
				tail[0][3].cubeList.add(new ModelBox(tail[0][3], 56, 6, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));
		
				tail[0][4] = new ModelRenderer(this);
				tail[0][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][3].addChild(tail[0][4]);
				setRotationAngle(tail[0][4], -0.2618F, 0.0F, 0.0F);
				tail[0][4].cubeList.add(new ModelBox(tail[0][4], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
				tail[0][4].cubeList.add(new ModelBox(tail[0][4], 56, 6, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));
		
				tail[0][5] = new ModelRenderer(this);
				tail[0][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[0][4].addChild(tail[0][5]);
				setRotationAngle(tail[0][5], -0.2618F, 0.0F, 0.0F);
				tail[0][5].cubeList.add(new ModelBox(tail[0][5], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
				tail[0][5].cubeList.add(new ModelBox(tail[0][5], 56, 6, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));
		
				tail[1][0] = new ModelRenderer(this);
				tail[1][0].setRotationPoint(0.5F, 0.0F, 0.0F);
				tails.addChild(tail[1][0]);
				setRotationAngle(tail[1][0], -1.0472F, 0.3491F, 0.0F);
				tail[1][0].cubeList.add(new ModelBox(tail[1][0], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
				tail[1][0].cubeList.add(new ModelBox(tail[1][0], 56, 6, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
		
				tail[1][1] = new ModelRenderer(this);
				tail[1][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][0].addChild(tail[1][1]);
				setRotationAngle(tail[1][1], 0.2618F, 0.0F, 0.0F);
				tail[1][1].cubeList.add(new ModelBox(tail[1][1], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
				tail[1][1].cubeList.add(new ModelBox(tail[1][1], 56, 6, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
		
				tail[1][2] = new ModelRenderer(this);
				tail[1][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][1].addChild(tail[1][2]);
				setRotationAngle(tail[1][2], 0.2618F, 0.0F, 0.0F);
				tail[1][2].cubeList.add(new ModelBox(tail[1][2], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
				tail[1][2].cubeList.add(new ModelBox(tail[1][2], 56, 6, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));
		
				tail[1][3] = new ModelRenderer(this);
				tail[1][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][2].addChild(tail[1][3]);
				setRotationAngle(tail[1][3], 0.2618F, 0.0F, 0.0F);
				tail[1][3].cubeList.add(new ModelBox(tail[1][3], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
				tail[1][3].cubeList.add(new ModelBox(tail[1][3], 56, 6, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));
		
				tail[1][4] = new ModelRenderer(this);
				tail[1][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][3].addChild(tail[1][4]);
				setRotationAngle(tail[1][4], -0.2618F, 0.0F, 0.0F);
				tail[1][4].cubeList.add(new ModelBox(tail[1][4], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
				tail[1][4].cubeList.add(new ModelBox(tail[1][4], 56, 6, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));
		
				tail[1][5] = new ModelRenderer(this);
				tail[1][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[1][4].addChild(tail[1][5]);
				setRotationAngle(tail[1][5], -0.2618F, 0.0F, 0.0F);
				tail[1][5].cubeList.add(new ModelBox(tail[1][5], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
				tail[1][5].cubeList.add(new ModelBox(tail[1][5], 56, 6, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));
		
				tail[2][0] = new ModelRenderer(this);
				tail[2][0].setRotationPoint(-0.5F, 0.0F, 0.0F);
				tails.addChild(tail[2][0]);
				setRotationAngle(tail[2][0], -1.0472F, -0.3491F, 0.0F);
				tail[2][0].cubeList.add(new ModelBox(tail[2][0], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
				tail[2][0].cubeList.add(new ModelBox(tail[2][0], 56, 6, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
		
				tail[2][1] = new ModelRenderer(this);
				tail[2][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][0].addChild(tail[2][1]);
				setRotationAngle(tail[2][1], 0.2618F, 0.0F, 0.0F);
				tail[2][1].cubeList.add(new ModelBox(tail[2][1], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
				tail[2][1].cubeList.add(new ModelBox(tail[2][1], 56, 6, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
		
				tail[2][2] = new ModelRenderer(this);
				tail[2][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][1].addChild(tail[2][2]);
				setRotationAngle(tail[2][2], 0.2618F, 0.0F, 0.0F);
				tail[2][2].cubeList.add(new ModelBox(tail[2][2], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
				tail[2][2].cubeList.add(new ModelBox(tail[2][2], 56, 6, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));
		
				tail[2][3] = new ModelRenderer(this);
				tail[2][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][2].addChild(tail[2][3]);
				setRotationAngle(tail[2][3], 0.2618F, 0.0F, 0.0F);
				tail[2][3].cubeList.add(new ModelBox(tail[2][3], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
				tail[2][3].cubeList.add(new ModelBox(tail[2][3], 56, 6, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));
		
				tail[2][4] = new ModelRenderer(this);
				tail[2][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][3].addChild(tail[2][4]);
				setRotationAngle(tail[2][4], -0.2618F, 0.0F, 0.0F);
				tail[2][4].cubeList.add(new ModelBox(tail[2][4], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
				tail[2][4].cubeList.add(new ModelBox(tail[2][4], 56, 6, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));
		
				tail[2][5] = new ModelRenderer(this);
				tail[2][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[2][4].addChild(tail[2][5]);
				setRotationAngle(tail[2][5], -0.2618F, 0.0F, 0.0F);
				tail[2][5].cubeList.add(new ModelBox(tail[2][5], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
				tail[2][5].cubeList.add(new ModelBox(tail[2][5], 56, 6, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));
		
				tail[3][0] = new ModelRenderer(this);
				tail[3][0].setRotationPoint(-1.5F, 0.0F, 0.0F);
				tails.addChild(tail[3][0]);
				setRotationAngle(tail[3][0], -1.0472F, -1.0472F, 0.0F);
				tail[3][0].cubeList.add(new ModelBox(tail[3][0], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.8F, false));
				tail[3][0].cubeList.add(new ModelBox(tail[3][0], 56, 6, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
		
				tail[3][1] = new ModelRenderer(this);
				tail[3][1].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][0].addChild(tail[3][1]);
				setRotationAngle(tail[3][1], 0.2618F, 0.0F, 0.0F);
				tail[3][1].cubeList.add(new ModelBox(tail[3][1], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.7F, false));
				tail[3][1].cubeList.add(new ModelBox(tail[3][1], 56, 6, -0.5F, -2.5F, -2.75F, 1, 2, 1, 0.0F, false));
		
				tail[3][2] = new ModelRenderer(this);
				tail[3][2].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][1].addChild(tail[3][2]);
				setRotationAngle(tail[3][2], 0.2618F, 0.0F, 0.0F);
				tail[3][2].cubeList.add(new ModelBox(tail[3][2], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.6F, false));
				tail[3][2].cubeList.add(new ModelBox(tail[3][2], 56, 6, -0.5F, -2.5F, -2.5F, 1, 2, 1, 0.0F, false));
		
				tail[3][3] = new ModelRenderer(this);
				tail[3][3].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][2].addChild(tail[3][3]);
				setRotationAngle(tail[3][3], 0.2618F, 0.0F, 0.0F);
				tail[3][3].cubeList.add(new ModelBox(tail[3][3], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.4F, false));
				tail[3][3].cubeList.add(new ModelBox(tail[3][3], 56, 6, -0.5F, -2.5F, -2.25F, 1, 2, 1, 0.0F, false));
		
				tail[3][4] = new ModelRenderer(this);
				tail[3][4].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][3].addChild(tail[3][4]);
				setRotationAngle(tail[3][4], -0.2618F, 0.0F, 0.0F);
				tail[3][4].cubeList.add(new ModelBox(tail[3][4], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.2F, false));
				tail[3][4].cubeList.add(new ModelBox(tail[3][4], 56, 6, -0.5F, -2.5F, -2.0F, 1, 2, 1, 0.0F, false));
		
				tail[3][5] = new ModelRenderer(this);
				tail[3][5].setRotationPoint(0.0F, -3.0F, 0.0F);
				tail[3][4].addChild(tail[3][5]);
				setRotationAngle(tail[3][5], -0.2618F, 0.0F, 0.0F);
				tail[3][5].cubeList.add(new ModelBox(tail[3][5], 56, 0, -1.0F, -3.5F, -1.0F, 2, 4, 2, 0.0F, false));
				tail[3][5].cubeList.add(new ModelBox(tail[3][5], 56, 6, -0.5F, -2.5F, -1.75F, 1, 2, 1, 0.0F, false));
	
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
				//bipedHead.rotationPointY += -6.5F;
				bipedHead.setRotationPoint(0.0F, -8.5F, -8.95F);
				bipedRightArm.setRotationPoint(-5.25F, -9.25F, -6.25F);
				bipedLeftArm.setRotationPoint(5.25F, -9.25F, -6.25F);
				bipedRightLeg.setRotationPoint(-2.25F, -1.25F, -1.25F);
				bipedLeftLeg.setRotationPoint(2.25F, -1.25F, -1.25F);
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
				if (!e.onGround) {
					setRotationAngle(rightLeg, 0.383F, -0.3089F, -1.0426F);
					rightCalf.rotateAngleZ = 0.2618F;
					setRotationAngle(leftLeg, 0.383F, 0.3089F, 1.0426F);
					leftCalf.rotateAngleZ = -0.2618F;
				} else {
					setRotationAngle(rightLeg, 0.0873F, -0.48F, -0.3054F);
					rightCalf.rotateAngleZ = 0.0F;
					setRotationAngle(leftLeg, 0.0873F, 0.48F, 0.3054F);
					leftCalf.rotateAngleZ = 0.0F;
				}
				if (((EntityCustom)e).isShooting()) {
					bipedHead.rotateAngleX += -0.5236F;
					jaw.rotateAngleX = 0.7418F;
				} else {
					jaw.rotateAngleX = 0.3491F;
				}
				if (((EntityCustom)e).isFaceDown()) {
					bipedBody.rotationPointZ = 10.0F;
					bipedBody.rotateAngleX = 0.7854F;
					bipedHead.rotateAngleX = -0.2618F;
					bipedRightArm.rotateAngleX = -1.7453F;
					bipedLeftArm.rotateAngleX = -1.7453F;
					bipedRightLeg.rotateAngleX = 0.3927F;
					bipedLeftLeg.rotateAngleX = 0.3927F;
					tails.rotateAngleX = -1.5708F;
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
}
