
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
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemKaton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTwoTails extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 265;
	public static final int ENTITYID_RANGED = 266;
	private static final float MODELSCALE = 10.0F;
	private static final TailBeastManager tailBeastManager = new TailBeastManager();

	public EntityTwoTails(ElementsNarutomodMod instance) {
		super(instance, 587);
	}

	@Override
	public void initElements() {
		elements.entities
				.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "two_tails"), ENTITYID)
						.name("two_tails").tracker(96, 3, true).egg(-16737793, -16777216).build());
	}

	public static TailBeastManager getBijuManager() {
		return tailBeastManager;
	}

	public static class TailBeastManager extends EntityBijuManager<EntityCustom> {
		public TailBeastManager() {
			super(EntityCustom.class, 2);
		}

		@Override
		public void setVesselEntity(@Nullable Entity player) {
			super.setVesselEntity(player);
			if (player instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)player, ItemKaton.block);
				if (stack == null) {
					stack = new ItemStack(ItemKaton.block);
					((ItemKaton.RangedItem)stack.getItem()).setOwner(stack, (EntityPlayer)player);
					ItemHandlerHelper.giveItemToPlayer((EntityPlayer)player, stack);
				}
				if (stack != null) {
					((ItemKaton.RangedItem)stack.getItem()).enableJutsu(stack, ItemKaton.GREATFIREBALL, true);
				}
			}
		}

		@Override
		public void markDirty() {
			Save.getInstance().markDirty();
		}
	}

	public static class Save extends EntityTailedBeast.SaveBase {
		private static final String DATA_NAME = net.narutomod.NarutomodMod.MODID + "_twotails";
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
			this.setSize(MODELSCALE * 1.0F, MODELSCALE * 1.6F);
			this.experienceValue = 12000;
			this.stepHeight = this.height / 3.0F;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 1.0F, MODELSCALE * 1.6F);
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
			this.setSize(this.width, MODELSCALE * (down ? 1.125F : 1.6F));
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
			return this.isFaceDown() ? 8.0d * 0.0625d * MODELSCALE : (double)this.height + 0.35D;
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
			return this.isFaceDown() ? 8.0f * 0.0625f * MODELSCALE : super.getFuuinBeamHeight();
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

		@Override
		public void onUpdate() {
			super.onUpdate();
			for (int i = 0; i < 8; i++) {
				double d0 = this.posX + (this.rand.nextFloat() - 0.5D) * (this.width + 2.0D);
				double d1 = this.posY + this.height + this.rand.nextFloat() * 6.0f - 3.0f;
				double d2 = this.posZ + (this.rand.nextFloat() - 0.5D) * (this.width + 2.0D);
				this.world.spawnAlwaysVisibleParticle(Particles.Types.FLAME.getID(), d0, d1, d2, 0.0D, 0.0D, 0.0D, 0x201e61b5, (int)(this.width * 10f));
			}
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager, new ModelTwoTails()));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityTailedBeast.ClientOnly.Renderer<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/twotails.png");

			public RenderCustom(RenderManager renderManagerIn, ModelTwoTails modelIn) {
				super(renderManagerIn, modelIn, MODELSCALE * 0.5F);
				this.addLayer(new LayerFlames(this, modelIn));
			}

			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				((ModelTwoTails)this.getMainModel()).setFlamedVisible(false);
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class LayerFlames implements LayerRenderer<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/twotailsflames.png");
			private final RenderCustom renderer;
			private final ModelTwoTails renderModel;

			public LayerFlames(RenderCustom rendererIn, ModelTwoTails modelIn) {
				this.renderer = rendererIn;
				this.renderModel = modelIn;
			}

			@Override
			public void doRenderLayer(EntityCustom entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
									  float netHeadYaw, float headPitch, float scale) {
				this.renderModel.setFlamedVisible(true);
				GlStateManager.pushMatrix();
				GlStateManager.depthMask(true);
				this.renderer.bindTexture(this.texture);
				//GlStateManager.scale(0.984375F, 0.984375F, 0.984375F);
				//GlStateManager.translate(0.0F, -0.1F, 0.1F);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				//float f = entitylivingbaseIn.ticksExisted + partialTicks;
				GlStateManager.translate(0.0F, ageInTicks * 0.01F, 0.0F);
				GlStateManager.matrixMode(5888);
				GlStateManager.enableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				//GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				this.renderModel.setModelAttributes(this.renderer.getMainModel());
				Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
				this.renderModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
				this.renderModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
				Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
				GlStateManager.enableLighting();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.disableBlend();
				GlStateManager.depthMask(false);
				GlStateManager.popMatrix();
				this.renderModel.setFlamedVisible(false);
			}

			@Override
			public boolean shouldCombineTextures() {
				return false;
			}
		}

		// Made with Blockbench 3.8.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelTwoTails extends ModelQuadruped {
			private final ModelRenderer highlight;
			private final ModelRenderer eyes;
			//private final ModelRenderer body;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			//private final ModelRenderer head;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer ears;
			private final ModelRenderer rightEar;
			private final ModelRenderer cube_r6;
			private final ModelRenderer leftEar;
			private final ModelRenderer cube_r7;
			private final ModelRenderer hair;
			private final ModelRenderer hair4;
			private final ModelRenderer cube_r8;
			private final ModelRenderer hair3;
			private final ModelRenderer cube_r9;
			private final ModelRenderer hair2;
			private final ModelRenderer cube_r10;
			private final ModelRenderer hair7;
			private final ModelRenderer cube_r11;
			private final ModelRenderer hair8;
			private final ModelRenderer cube_r12;
			private final ModelRenderer hair9;
			private final ModelRenderer cube_r13;
			private final ModelRenderer hair10;
			private final ModelRenderer cube_r14;
			private final ModelRenderer cube_r15;
			private final ModelRenderer hair5;
			private final ModelRenderer cube_r16;
			private final ModelRenderer hair6;
			private final ModelRenderer cube_r17;
			private final ModelRenderer topMouth;
			private final ModelRenderer cube_r18;
			private final ModelRenderer cube_r19;
			private final ModelRenderer cube_r20;
			private final ModelRenderer nose;
			private final ModelRenderer cube_r21;
			private final ModelRenderer upperTeeth;
			private final ModelRenderer cube_r22;
			private final ModelRenderer cube_r23;
			private final ModelRenderer cube_r24;
			private final ModelRenderer cube_r25;
			private final ModelRenderer cube_r26;
			private final ModelRenderer jaw;
			private final ModelRenderer cube_r27;
			private final ModelRenderer cube_r28;
			private final ModelRenderer cube_r29;
			private final ModelRenderer cube_r30;
			private final ModelRenderer cube_r31;
			private final ModelRenderer cube_r32;
			private final ModelRenderer bone;
			private final ModelRenderer cube_r33;
			private final ModelRenderer cube_r34;
			private final ModelRenderer bone2;
			private final ModelRenderer cube_r35;
			private final ModelRenderer cube_r36;
			private final ModelRenderer bone3;
			private final ModelRenderer cube_r37;
			private final ModelRenderer cube_r38;
			private final ModelRenderer bone4;
			private final ModelRenderer cube_r39;
			private final ModelRenderer cube_r40;
			private final ModelRenderer lowerTeeth;
			private final ModelRenderer cube_r41;
			//private final ModelRenderer leg1;
			private final ModelRenderer joint7;
			private final ModelRenderer cube_r42;
			private final ModelRenderer joint8;
			private final ModelRenderer cube_r43;
			private final ModelRenderer foot1;
			private final ModelRenderer cube_r44;
			private final ModelRenderer cube_r45;
			private final ModelRenderer cube_r46;
			private final ModelRenderer cube_r47;
			//private final ModelRenderer leg2;
			private final ModelRenderer joint2;
			private final ModelRenderer cube_r48;
			private final ModelRenderer joint5;
			private final ModelRenderer cube_r49;
			private final ModelRenderer foot2;
			private final ModelRenderer cube_r50;
			private final ModelRenderer cube_r51;
			private final ModelRenderer cube_r52;
			private final ModelRenderer cube_r53;
			//private final ModelRenderer leg3;
			private final ModelRenderer joint3;
			private final ModelRenderer cube_r54;
			private final ModelRenderer joint4;
			private final ModelRenderer cube_r55;
			private final ModelRenderer foot3;
			private final ModelRenderer cube_r56;
			private final ModelRenderer cube_r57;
			private final ModelRenderer cube_r58;
			private final ModelRenderer cube_r59;
			//private final ModelRenderer leg4;
			private final ModelRenderer joint6;
			private final ModelRenderer cube_r60;
			private final ModelRenderer joint9;
			private final ModelRenderer cube_r61;
			private final ModelRenderer foot4;
			private final ModelRenderer cube_r62;
			private final ModelRenderer cube_r63;
			private final ModelRenderer cube_r64;
			private final ModelRenderer cube_r65;
			private final ModelRenderer[][] tail = new ModelRenderer[2][8];
			private final ModelRenderer bodyFlamed;
			private final ModelRenderer cube_r66;
			private final ModelRenderer cube_r67;
			private final ModelRenderer cube_r68;
			private final ModelRenderer headFlamed;
			private final ModelRenderer cube_r69;
			private final ModelRenderer cube_r70;
			private final ModelRenderer ears2;
			private final ModelRenderer rightEar2;
			private final ModelRenderer cube_r71;
			private final ModelRenderer leftEar2;
			private final ModelRenderer cube_r72;
			private final ModelRenderer hair11;
			private final ModelRenderer hair12;
			private final ModelRenderer cube_r73;
			private final ModelRenderer hair13;
			private final ModelRenderer cube_r74;
			private final ModelRenderer hair14;
			private final ModelRenderer cube_r75;
			private final ModelRenderer hair15;
			private final ModelRenderer cube_r76;
			private final ModelRenderer hair16;
			private final ModelRenderer cube_r77;
			private final ModelRenderer hair17;
			private final ModelRenderer cube_r78;
			private final ModelRenderer hair18;
			private final ModelRenderer cube_r79;
			private final ModelRenderer cube_r80;
			private final ModelRenderer hair19;
			private final ModelRenderer cube_r81;
			private final ModelRenderer hair20;
			private final ModelRenderer cube_r82;
			private final ModelRenderer topMouth2;
			private final ModelRenderer cube_r83;
			private final ModelRenderer cube_r84;
			private final ModelRenderer cube_r85;
			private final ModelRenderer nose2;
			private final ModelRenderer cube_r86;
			private final ModelRenderer jaw2;
			private final ModelRenderer cube_r87;
			private final ModelRenderer cube_r88;
			private final ModelRenderer cube_r89;
			private final ModelRenderer cube_r90;
			private final ModelRenderer cube_r91;
			private final ModelRenderer cube_r92;
			private final ModelRenderer bone5;
			private final ModelRenderer cube_r93;
			private final ModelRenderer cube_r94;
			private final ModelRenderer bone6;
			private final ModelRenderer cube_r95;
			private final ModelRenderer cube_r96;
			private final ModelRenderer bone7;
			private final ModelRenderer cube_r97;
			private final ModelRenderer cube_r98;
			private final ModelRenderer bone8;
			private final ModelRenderer cube_r99;
			private final ModelRenderer cube_r100;
			private final ModelRenderer leg1Flamed;
			private final ModelRenderer joint10;
			private final ModelRenderer cube_r101;
			private final ModelRenderer joint11;
			private final ModelRenderer cube_r102;
			private final ModelRenderer foot5;
			private final ModelRenderer cube_r103;
			private final ModelRenderer cube_r104;
			private final ModelRenderer cube_r105;
			private final ModelRenderer cube_r106;
			private final ModelRenderer leg2Flamed;
			private final ModelRenderer joint12;
			private final ModelRenderer cube_r107;
			private final ModelRenderer joint13;
			private final ModelRenderer cube_r108;
			private final ModelRenderer foot6;
			private final ModelRenderer cube_r109;
			private final ModelRenderer cube_r110;
			private final ModelRenderer cube_r111;
			private final ModelRenderer cube_r112;
			private final ModelRenderer leg3Flamed;
			private final ModelRenderer joint14;
			private final ModelRenderer cube_r113;
			private final ModelRenderer joint15;
			private final ModelRenderer cube_r114;
			private final ModelRenderer foot7;
			private final ModelRenderer cube_r115;
			private final ModelRenderer cube_r116;
			private final ModelRenderer cube_r117;
			private final ModelRenderer cube_r118;
			private final ModelRenderer leg4Flamed;
			private final ModelRenderer joint16;
			private final ModelRenderer cube_r119;
			private final ModelRenderer joint17;
			private final ModelRenderer cube_r120;
			private final ModelRenderer foot8;
			private final ModelRenderer cube_r121;
			private final ModelRenderer cube_r122;
			private final ModelRenderer cube_r123;
			private final ModelRenderer cube_r124;
			private final ModelRenderer[][] tailFlamed = new ModelRenderer[2][8];

			private final Random rand = new Random();
			private final float tailSwayX[][] = new float[2][8];
			private final float tailSwayY[][] = new float[2][8];
			private final float tailSwayZ[][] = new float[2][8];

			public ModelTwoTails() {
				super(12, 0.0F);
				textureWidth = 128;
				textureHeight = 128;

				highlight = new ModelRenderer(this);
				highlight.setRotationPoint(0.0F, 3.0F, 0.0F);


				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, 0.0F, -6.0F);
				highlight.addChild(eyes);
				eyes.cubeList.add(new ModelBox(eyes, 76, 0, -4.0F, -3.0F, -9.8F, 8, 2, 0, 0.0F, false));

				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 3.0F, 0.0F);


				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(6.0F, 8.0F, 4.25F);
				body.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.0F, 0.0F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 31, -13.0F, -12.0F, 5.0F, 14, 12, 17, 0.0F, false));

				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(6.0F, 8.0F, -8.0F);
				body.addChild(cube_r2);
				setRotationAngle(cube_r2, -0.0436F, 0.0F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 48, 46, -14.0F, -14.0F, 14.0F, 16, 14, 14, 0.0F, false));

				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(6.0F, 8.0F, -13.5F);
				body.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.0873F, 0.0F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 0, -15.0F, -16.0F, 5.0F, 18, 15, 16, 0.0F, false));

				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 0.0F, -6.0F);
				body.addChild(head);


				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(6.0F, 2.75F, 4.0F);
				head.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.0F, 0.0F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 45, 43, -11.0F, -8.0F, -14.0F, 10, 2, 1, 0.0F, false));

				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(6.0F, 9.75F, 4.25F);
				head.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.0F, 0.0F, 0.0F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 57, 20, -11.0F, -15.0F, -14.0F, 10, 10, 11, 0.0F, false));

				ears = new ModelRenderer(this);
				ears.setRotationPoint(11.75F, 5.25F, 0.25F);
				head.addChild(ears);


				rightEar = new ModelRenderer(this);
				rightEar.setRotationPoint(-12.75F, 6.0F, -11.0F);
				ears.addChild(rightEar);
				setRotationAngle(rightEar, -0.5744F, -0.3332F, -0.468F);
				rightEar.cubeList.add(new ModelBox(rightEar, 0, 8, 3.5F, -18.5F, -8.25F, 2, 6, 1, 0.0F, false));
				rightEar.cubeList.add(new ModelBox(rightEar, 0, 0, 4.3284F, -18.5F, -8.25F, 2, 6, 1, 0.0F, false));

				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(16.4047F, -0.6456F, 0.0F);
				rightEar.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.0F, 0.0F, -0.7854F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 28, 70, 3.5F, -21.75F, -8.25F, 2, 2, 1, 0.0F, false));

				leftEar = new ModelRenderer(this);
				leftEar.setRotationPoint(-10.75F, 6.0F, -11.0F);
				ears.addChild(leftEar);
				setRotationAngle(leftEar, -0.5744F, 0.3332F, 0.468F);
				leftEar.cubeList.add(new ModelBox(leftEar, 0, 8, -5.5F, -18.5F, -8.25F, 2, 6, 1, 0.0F, true));
				leftEar.cubeList.add(new ModelBox(leftEar, 0, 0, -6.3284F, -18.5F, -8.25F, 2, 6, 1, 0.0F, true));

				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(-16.4047F, -0.6456F, 0.0F);
				leftEar.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.0F, 0.0F, 0.7854F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 28, 70, -5.5F, -21.75F, -8.25F, 2, 2, 1, 0.0F, true));

				hair = new ModelRenderer(this);
				hair.setRotationPoint(11.75F, 5.25F, 0.25F);
				head.addChild(hair);


				hair4 = new ModelRenderer(this);
				hair4.setRotationPoint(3.5F, 10.25F, -19.25F);
				hair.addChild(hair4);
				setRotationAngle(hair4, -1.1345F, 0.0436F, -1.0036F);
				hair4.cubeList.add(new ModelBox(hair4, 28, 66, -1.6651F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, false));
				hair4.cubeList.add(new ModelBox(hair4, 42, 60, -0.8367F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, false));

				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				hair4.addChild(cube_r8);
				setRotationAngle(cube_r8, 0.0F, 0.0F, 0.7854F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 70, 0, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

				hair3 = new ModelRenderer(this);
				hair3.setRotationPoint(-27.0F, 10.25F, -19.25F);
				hair.addChild(hair3);
				setRotationAngle(hair3, -1.1345F, -0.0436F, 1.0036F);
				hair3.cubeList.add(new ModelBox(hair3, 28, 66, -0.3349F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, true));
				hair3.cubeList.add(new ModelBox(hair3, 42, 60, -1.1633F, -21.6317F, -16.75F, 2, 3, 1, 0.0F, true));

				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				hair3.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.0F, 0.0F, -0.7854F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 70, 0, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));

				hair2 = new ModelRenderer(this);
				hair2.setRotationPoint(3.5F, 8.25F, -10.5F);
				hair.addChild(hair2);
				setRotationAngle(hair2, -0.829F, 0.0436F, -0.9163F);
				hair2.cubeList.add(new ModelBox(hair2, 0, 60, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, false));
				hair2.cubeList.add(new ModelBox(hair2, 6, 42, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, false));

				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				hair2.addChild(cube_r10);
				setRotationAngle(cube_r10, 0.0F, 0.0F, 0.7854F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 70, 3, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

				hair7 = new ModelRenderer(this);
				hair7.setRotationPoint(-27.0F, 8.25F, -10.5F);
				hair.addChild(hair7);
				setRotationAngle(hair7, -0.829F, -0.0436F, 0.9163F);
				hair7.cubeList.add(new ModelBox(hair7, 0, 60, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, true));
				hair7.cubeList.add(new ModelBox(hair7, 6, 42, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, true));

				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				hair7.addChild(cube_r11);
				setRotationAngle(cube_r11, 0.0F, 0.0F, -0.7854F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 70, 3, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));

				hair8 = new ModelRenderer(this);
				hair8.setRotationPoint(5.75F, 5.25F, -10.5F);
				hair.addChild(hair8);
				setRotationAngle(hair8, -0.829F, 0.0436F, -0.9163F);
				hair8.cubeList.add(new ModelBox(hair8, 45, 33, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, false));
				hair8.cubeList.add(new ModelBox(hair8, 10, 36, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, false));

				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				hair8.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.0F, 0.0F, 0.7854F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 66, 41, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

				hair9 = new ModelRenderer(this);
				hair9.setRotationPoint(-29.25F, 5.25F, -10.5F);
				hair.addChild(hair9);
				setRotationAngle(hair9, -0.829F, -0.0436F, 0.9163F);
				hair9.cubeList.add(new ModelBox(hair9, 45, 33, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, true));
				hair9.cubeList.add(new ModelBox(hair9, 10, 36, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, true));

				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				hair9.addChild(cube_r13);
				setRotationAngle(cube_r13, 0.0F, 0.0F, -0.7854F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 66, 41, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));

				hair10 = new ModelRenderer(this);
				hair10.setRotationPoint(-9.0F, -34.75F, 12.0F);
				hair.addChild(hair10);
				setRotationAngle(hair10, -1.1781F, 0.0F, 0.0F);
				hair10.cubeList.add(new ModelBox(hair10, 9, 8, -3.3358F, 24.2726F, 14.5601F, 2, 5, 1, 0.0F, false));
				hair10.cubeList.add(new ModelBox(hair10, 9, 0, -4.1642F, 24.2726F, 14.5601F, 2, 5, 1, 0.0F, false));

				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(12.8063F, 39.8289F, 31.3101F);
				hair10.addChild(cube_r14);
				setRotationAngle(cube_r14, 0.0F, 0.0F, -0.7854F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 45, -0.6569F, -23.3431F, -14.6549F, 2, 2, 1, 0.0F, false));
				cube_r14.cubeList.add(new ModelBox(cube_r14, 45, 38, -3.1318F, -25.8179F, -14.6549F, 2, 2, 1, 0.0F, false));
				cube_r14.cubeList.add(new ModelBox(cube_r14, 35, 62, -2.6433F, -23.8315F, -15.3642F, 2, 2, 1, 0.0F, false));
				cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 65, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(-18.3063F, 39.8289F, 31.3101F);
				hair10.addChild(cube_r15);
				setRotationAngle(cube_r15, 0.0F, 0.0F, 0.7854F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 45, 38, 1.1318F, -25.8179F, -14.6549F, 2, 2, 1, 0.0F, true));
				cube_r15.cubeList.add(new ModelBox(cube_r15, 35, 62, 0.6433F, -23.8315F, -15.3642F, 2, 2, 1, 0.0F, true));

				hair5 = new ModelRenderer(this);
				hair5.setRotationPoint(4.25F, 9.75F, -10.5F);
				hair.addChild(hair5);
				setRotationAngle(hair5, -0.829F, -0.3491F, -0.9163F);
				hair5.cubeList.add(new ModelBox(hair5, 52, 0, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, false));
				hair5.cubeList.add(new ModelBox(hair5, 0, 39, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, false));

				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				hair5.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.0F, 0.0F, 0.7854F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 68, 17, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, false));

				hair6 = new ModelRenderer(this);
				hair6.setRotationPoint(-27.75F, 9.75F, -10.5F);
				hair.addChild(hair6);
				setRotationAngle(hair6, -0.829F, 0.3491F, 0.9163F);
				hair6.cubeList.add(new ModelBox(hair6, 52, 0, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, 0.0F, true));
				hair6.cubeList.add(new ModelBox(hair6, 0, 39, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, 0.0F, true));

				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				hair6.addChild(cube_r17);
				setRotationAngle(cube_r17, 0.0F, 0.0F, -0.7854F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 68, 17, -1.0F, -23.0F, -16.75F, 2, 2, 1, 0.0F, true));

				topMouth = new ModelRenderer(this);
				topMouth.setRotationPoint(-0.25F, 20.5F, 14.25F);
				head.addChild(topMouth);


				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(7.5815F, -13.5F, -19.9805F);
				topMouth.addChild(cube_r18);
				setRotationAngle(cube_r18, 0.0873F, 0.1309F, 0.0F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 88, 0, -5.0F, -8.0F, -10.0F, 2, 3, 7, 0.0F, true));

				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(-7.0815F, -13.5F, -19.9805F);
				topMouth.addChild(cube_r19);
				setRotationAngle(cube_r19, 0.0873F, -0.1309F, 0.0F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 88, 0, 3.0F, -8.0F, -10.0F, 2, 3, 7, 0.0F, false));

				cube_r20 = new ModelRenderer(this);
				cube_r20.setRotationPoint(0.25F, -20.4088F, -30.0418F);
				topMouth.addChild(cube_r20);
				setRotationAngle(cube_r20, 0.1745F, 0.0F, 0.0F);
				cube_r20.cubeList.add(new ModelBox(cube_r20, 69, 7, -3.0F, -0.1988F, -0.1005F, 6, 3, 7, 0.0F, false));

				nose = new ModelRenderer(this);
				nose.setRotationPoint(6.25F, -13.5F, -20.75F);
				topMouth.addChild(nose);


				cube_r21 = new ModelRenderer(this);
				cube_r21.setRotationPoint(0.0F, 0.0F, 0.0F);
				nose.addChild(cube_r21);
				setRotationAngle(cube_r21, 0.0873F, 0.0F, 0.0F);
				cube_r21.cubeList.add(new ModelBox(cube_r21, 55, 46, -7.0F, -8.0F, -9.0F, 2, 1, 1, 0.0F, false));

				upperTeeth = new ModelRenderer(this);
				upperTeeth.setRotationPoint(0.0F, -0.5F, -9.25F);
				topMouth.addChild(upperTeeth);


				cube_r22 = new ModelRenderer(this);
				cube_r22.setRotationPoint(3.556F, -17.25F, -1.7657F);
				upperTeeth.addChild(cube_r22);
				setRotationAngle(cube_r22, 3.1416F, 0.0F, -3.1416F);
				cube_r22.cubeList.add(new ModelBox(cube_r22, 0, 31, 0.807F, -0.5F, 18.4824F, 1, 2, 0, 0.0F, false));

				cube_r23 = new ModelRenderer(this);
				cube_r23.setRotationPoint(16.2956F, -17.0F, -19.249F);
				upperTeeth.addChild(cube_r23);
				setRotationAngle(cube_r23, 0.0F, -1.5272F, 0.0F);
				cube_r23.cubeList.add(new ModelBox(cube_r23, 45, 46, -1.807F, -0.5F, 18.4824F, 5, 1, 0, 0.0F, false));

				cube_r24 = new ModelRenderer(this);
				cube_r24.setRotationPoint(-15.7956F, -17.0F, -19.249F);
				upperTeeth.addChild(cube_r24);
				setRotationAngle(cube_r24, 0.0F, 1.5272F, 0.0F);
				cube_r24.cubeList.add(new ModelBox(cube_r24, 45, 47, -3.193F, -0.5F, 18.4824F, 5, 1, 0, 0.0F, false));

				cube_r25 = new ModelRenderer(this);
				cube_r25.setRotationPoint(0.556F, -17.0F, -1.7657F);
				upperTeeth.addChild(cube_r25);
				setRotationAngle(cube_r25, 3.1416F, 0.0F, -3.1416F);
				cube_r25.cubeList.add(new ModelBox(cube_r25, 9, 6, -1.193F, -0.5F, 18.4824F, 3, 1, 0, 0.0F, false));

				cube_r26 = new ModelRenderer(this);
				cube_r26.setRotationPoint(-0.444F, -17.25F, -1.7657F);
				upperTeeth.addChild(cube_r26);
				setRotationAngle(cube_r26, 3.1416F, 0.0F, -3.1416F);
				cube_r26.cubeList.add(new ModelBox(cube_r26, 14, 31, 0.807F, -0.5F, 18.4824F, 1, 2, 0, 0.0F, false));

				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(-0.0749F, 3.6113F, -9.4922F);
				head.addChild(jaw);
				setRotationAngle(jaw, 0.5236F, 0.0F, 0.0F);


				cube_r27 = new ModelRenderer(this);
				cube_r27.setRotationPoint(6.0749F, 8.1425F, -10.4545F);
				jaw.addChild(cube_r27);
				setRotationAngle(cube_r27, 0.1745F, 0.0F, 0.0F);
				cube_r27.cubeList.add(new ModelBox(cube_r27, 20, 62, -9.0F, -6.0F, 9.0F, 6, 1, 3, 0.0F, false));

				cube_r28 = new ModelRenderer(this);
				cube_r28.setRotationPoint(6.0749F, 9.3049F, 2.6281F);
				jaw.addChild(cube_r28);
				setRotationAngle(cube_r28, -0.3054F, 0.0F, 0.0F);
				cube_r28.cubeList.add(new ModelBox(cube_r28, 0, 34, -9.0F, -6.0F, -10.0F, 6, 1, 1, 0.0F, false));

				cube_r29 = new ModelRenderer(this);
				cube_r29.setRotationPoint(6.0749F, 5.9394F, 5.9459F);
				jaw.addChild(cube_r29);
				setRotationAngle(cube_r29, 0.0873F, 0.0F, 0.0F);
				cube_r29.cubeList.add(new ModelBox(cube_r29, 0, 31, -9.0F, -6.0F, -10.0F, 6, 1, 2, 0.0F, false));

				cube_r30 = new ModelRenderer(this);
				cube_r30.setRotationPoint(-6.8275F, 5.6387F, 4.6908F);
				jaw.addChild(cube_r30);
				setRotationAngle(cube_r30, 0.0873F, -0.0873F, 0.0F);
				cube_r30.cubeList.add(new ModelBox(cube_r30, 0, 0, 3.0F, -6.0F, -10.0F, 1, 1, 7, 0.0F, false));

				cube_r31 = new ModelRenderer(this);
				cube_r31.setRotationPoint(6.9773F, 5.6387F, 4.6908F);
				jaw.addChild(cube_r31);
				setRotationAngle(cube_r31, 0.0873F, 0.0873F, 0.0F);
				cube_r31.cubeList.add(new ModelBox(cube_r31, 0, 8, -4.0F, -6.0F, -10.0F, 1, 1, 7, 0.0F, false));

				cube_r32 = new ModelRenderer(this);
				cube_r32.setRotationPoint(6.0749F, 5.6387F, 4.9922F);
				jaw.addChild(cube_r32);
				setRotationAngle(cube_r32, 0.0873F, 0.0F, 0.0F);
				cube_r32.cubeList.add(new ModelBox(cube_r32, 69, 84, -9.0F, -6.0F, -10.0F, 6, 1, 7, 0.0F, false));

				bone = new ModelRenderer(this);
				bone.setRotationPoint(8.2288F, -3.2172F, 4.8801F);
				jaw.addChild(bone);


				cube_r33 = new ModelRenderer(this);
				cube_r33.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(cube_r33);
				setRotationAngle(cube_r33, 0.1809F, 0.1538F, -0.7744F);
				cube_r33.cubeList.add(new ModelBox(cube_r33, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

				cube_r34 = new ModelRenderer(this);
				cube_r34.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone.addChild(cube_r34);
				setRotationAngle(cube_r34, 0.0564F, 0.0308F, -0.7859F);
				cube_r34.cubeList.add(new ModelBox(cube_r34, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(9.2288F, -3.2172F, 4.8801F);
				jaw.addChild(bone2);


				cube_r35 = new ModelRenderer(this);
				cube_r35.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone2.addChild(cube_r35);
				setRotationAngle(cube_r35, 0.1809F, 0.1538F, -0.7744F);
				cube_r35.cubeList.add(new ModelBox(cube_r35, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

				cube_r36 = new ModelRenderer(this);
				cube_r36.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone2.addChild(cube_r36);
				setRotationAngle(cube_r36, 0.0564F, 0.0308F, -0.7859F);
				cube_r36.cubeList.add(new ModelBox(cube_r36, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(10.2288F, -3.2172F, 4.8801F);
				jaw.addChild(bone3);


				cube_r37 = new ModelRenderer(this);
				cube_r37.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone3.addChild(cube_r37);
				setRotationAngle(cube_r37, 0.1809F, 0.1538F, -0.7744F);
				cube_r37.cubeList.add(new ModelBox(cube_r37, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

				cube_r38 = new ModelRenderer(this);
				cube_r38.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone3.addChild(cube_r38);
				setRotationAngle(cube_r38, 0.0564F, 0.0308F, -0.7859F);
				cube_r38.cubeList.add(new ModelBox(cube_r38, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(11.2288F, -3.2172F, 4.8801F);
				jaw.addChild(bone4);


				cube_r39 = new ModelRenderer(this);
				cube_r39.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone4.addChild(cube_r39);
				setRotationAngle(cube_r39, 0.1809F, 0.1538F, -0.7744F);
				cube_r39.cubeList.add(new ModelBox(cube_r39, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

				cube_r40 = new ModelRenderer(this);
				cube_r40.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone4.addChild(cube_r40);
				setRotationAngle(cube_r40, 0.0564F, 0.0308F, -0.7859F);
				cube_r40.cubeList.add(new ModelBox(cube_r40, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, 0.0F, false));

				lowerTeeth = new ModelRenderer(this);
				lowerTeeth.setRotationPoint(-18.1751F, 15.1387F, -4.0078F);
				jaw.addChild(lowerTeeth);
				setRotationAngle(lowerTeeth, -3.1416F, -1.5272F, 3.1416F);
				lowerTeeth.cubeList.add(new ModelBox(lowerTeeth, 60, 15, -2.1529F, -15.5F, -20.6758F, 4, 1, 0, 0.0F, false));
				lowerTeeth.cubeList.add(new ModelBox(lowerTeeth, 52, 15, -1.9348F, -15.5F, -15.6805F, 4, 1, 0, 0.0F, false));

				cube_r41 = new ModelRenderer(this);
				cube_r41.setRotationPoint(16.4522F, -15.25F, -18.291F);
				lowerTeeth.addChild(cube_r41);
				setRotationAngle(cube_r41, 0.0F, -1.5272F, 0.0F);
				cube_r41.cubeList.add(new ModelBox(cube_r41, 52, 14, -3.193F, -0.25F, 18.4824F, 5, 1, 0, 0.0F, false));

				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(-4.75F, 1.0F, -1.0F);
				body.addChild(leg1);


				joint7 = new ModelRenderer(this);
				joint7.setRotationPoint(0.0F, -2.0F, -1.0F);
				leg1.addChild(joint7);
				setRotationAngle(joint7, 0.1745F, 0.0F, 0.1745F);


				cube_r42 = new ModelRenderer(this);
				cube_r42.setRotationPoint(2.0F, 12.0F, -11.25F);
				joint7.addChild(cube_r42);
				setRotationAngle(cube_r42, -0.0436F, 0.0F, 0.0F);
				cube_r42.cubeList.add(new ModelBox(cube_r42, 28, 66, -8.0F, -12.0F, 7.0F, 6, 14, 8, 0.0F, false));

				joint8 = new ModelRenderer(this);
				joint8.setRotationPoint(-2.875F, 15.25F, 0.75F);
				joint7.addChild(joint8);
				setRotationAngle(joint8, -0.517F, -0.0869F, -0.1515F);


				cube_r43 = new ModelRenderer(this);
				cube_r43.setRotationPoint(4.375F, 5.75F, -14.75F);
				joint8.addChild(cube_r43);
				setRotationAngle(cube_r43, 0.0F, 0.0F, 0.0F);
				cube_r43.cubeList.add(new ModelBox(cube_r43, 0, 82, -7.0F, -8.0F, 11.0F, 5, 10, 5, 0.0F, false));

				foot1 = new ModelRenderer(this);
				foot1.setRotationPoint(-0.125F, 5.9375F, 0.4375F);
				joint8.addChild(foot1);
				setRotationAngle(foot1, 0.3491F, 0.0F, 0.0F);


				cube_r44 = new ModelRenderer(this);
				cube_r44.setRotationPoint(6.5F, 2.0625F, -22.4375F);
				foot1.addChild(cube_r44);
				setRotationAngle(cube_r44, 0.0F, 0.0F, 0.0F);
				cube_r44.cubeList.add(new ModelBox(cube_r44, 6, 39, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

				cube_r45 = new ModelRenderer(this);
				cube_r45.setRotationPoint(8.0F, 2.0625F, -22.4375F);
				foot1.addChild(cube_r45);
				setRotationAngle(cube_r45, 0.0F, 0.0F, 0.0F);
				cube_r45.cubeList.add(new ModelBox(cube_r45, 12, 42, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

				cube_r46 = new ModelRenderer(this);
				cube_r46.setRotationPoint(5.0F, 2.0625F, -22.4375F);
				foot1.addChild(cube_r46);
				setRotationAngle(cube_r46, 0.0F, 0.0F, 0.0F);
				cube_r46.cubeList.add(new ModelBox(cube_r46, 12, 45, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

				cube_r47 = new ModelRenderer(this);
				cube_r47.setRotationPoint(4.5F, 1.8125F, -15.1875F);
				foot1.addChild(cube_r47);
				setRotationAngle(cube_r47, 0.0F, 0.0F, 0.0F);
				cube_r47.cubeList.add(new ModelBox(cube_r47, 76, 74, -7.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, false));

				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(4.75F, 1.0F, -1.0F);
				body.addChild(leg2);


				joint2 = new ModelRenderer(this);
				joint2.setRotationPoint(0.0F, -2.0F, -1.0F);
				leg2.addChild(joint2);
				setRotationAngle(joint2, 0.1745F, 0.0F, -0.1745F);


				cube_r48 = new ModelRenderer(this);
				cube_r48.setRotationPoint(-2.0F, 12.0F, -11.25F);
				joint2.addChild(cube_r48);
				setRotationAngle(cube_r48, -0.0436F, 0.0F, 0.0F);
				cube_r48.cubeList.add(new ModelBox(cube_r48, 28, 66, 2.0F, -12.0F, 7.0F, 6, 14, 8, 0.0F, true));

				joint5 = new ModelRenderer(this);
				joint5.setRotationPoint(2.875F, 15.25F, 0.75F);
				joint2.addChild(joint5);
				setRotationAngle(joint5, -0.517F, 0.0869F, 0.1515F);


				cube_r49 = new ModelRenderer(this);
				cube_r49.setRotationPoint(-4.375F, 5.75F, -14.75F);
				joint5.addChild(cube_r49);
				setRotationAngle(cube_r49, 0.0F, 0.0F, 0.0F);
				cube_r49.cubeList.add(new ModelBox(cube_r49, 0, 82, 2.0F, -8.0F, 11.0F, 5, 10, 5, 0.0F, true));

				foot2 = new ModelRenderer(this);
				foot2.setRotationPoint(0.125F, 5.9375F, 0.4375F);
				joint5.addChild(foot2);
				setRotationAngle(foot2, 0.3491F, 0.0F, 0.0F);


				cube_r50 = new ModelRenderer(this);
				cube_r50.setRotationPoint(-6.5F, 2.0625F, -22.4375F);
				foot2.addChild(cube_r50);
				setRotationAngle(cube_r50, 0.0F, 0.0F, 0.0F);
				cube_r50.cubeList.add(new ModelBox(cube_r50, 6, 39, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

				cube_r51 = new ModelRenderer(this);
				cube_r51.setRotationPoint(-8.0F, 2.0625F, -22.4375F);
				foot2.addChild(cube_r51);
				setRotationAngle(cube_r51, 0.0F, 0.0F, 0.0F);
				cube_r51.cubeList.add(new ModelBox(cube_r51, 12, 42, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

				cube_r52 = new ModelRenderer(this);
				cube_r52.setRotationPoint(-5.0F, 2.0625F, -22.4375F);
				foot2.addChild(cube_r52);
				setRotationAngle(cube_r52, 0.0F, 0.0F, 0.0F);
				cube_r52.cubeList.add(new ModelBox(cube_r52, 12, 45, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

				cube_r53 = new ModelRenderer(this);
				cube_r53.setRotationPoint(-4.5F, 1.8125F, -15.1875F);
				foot2.addChild(cube_r53);
				setRotationAngle(cube_r53, 0.0F, 0.0F, 0.0F);
				cube_r53.cubeList.add(new ModelBox(cube_r53, 76, 74, 2.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, true));

				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(-5.5F, 5.0F, 21.5F);
				body.addChild(leg3);


				joint3 = new ModelRenderer(this);
				joint3.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg3.addChild(joint3);
				setRotationAngle(joint3, -1.5708F, 0.0F, 0.0F);


				cube_r54 = new ModelRenderer(this);
				cube_r54.setRotationPoint(2.25F, 17.5F, -2.75F);
				joint3.addChild(cube_r54);
				setRotationAngle(cube_r54, 0.7854F, 0.0F, 0.0F);
				cube_r54.cubeList.add(new ModelBox(cube_r54, 0, 60, -8.0F, -14.3536F, 10.182F, 6, 14, 8, 0.2F, false));

				joint4 = new ModelRenderer(this);
				joint4.setRotationPoint(1.25F, 17.5F, -2.75F);
				joint3.addChild(joint4);


				cube_r55 = new ModelRenderer(this);
				cube_r55.setRotationPoint(-10.0F, 27.5103F, 8.3355F);
				joint4.addChild(cube_r55);
				setRotationAngle(cube_r55, -1.1781F, 0.0F, 0.0F);
				cube_r55.cubeList.add(new ModelBox(cube_r55, 56, 74, 3.0F, -24.6324F, -38.7863F, 5, 11, 5, 0.0F, false));

				foot3 = new ModelRenderer(this);
				foot3.setRotationPoint(-4.5F, -16.9625F, 16.4375F);
				joint4.addChild(foot3);
				setRotationAngle(foot3, 1.5708F, 0.0F, 0.0F);


				cube_r56 = new ModelRenderer(this);
				cube_r56.setRotationPoint(6.5F, 2.0625F, -22.4375F);
				foot3.addChild(cube_r56);
				setRotationAngle(cube_r56, 0.0F, 0.0F, 0.0F);
				cube_r56.cubeList.add(new ModelBox(cube_r56, 6, 39, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

				cube_r57 = new ModelRenderer(this);
				cube_r57.setRotationPoint(8.0F, 2.0625F, -22.4375F);
				foot3.addChild(cube_r57);
				setRotationAngle(cube_r57, 0.0F, 0.0F, 0.0F);
				cube_r57.cubeList.add(new ModelBox(cube_r57, 12, 42, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

				cube_r58 = new ModelRenderer(this);
				cube_r58.setRotationPoint(5.0F, 2.0625F, -22.4375F);
				foot3.addChild(cube_r58);
				setRotationAngle(cube_r58, 0.0F, 0.0F, 0.0F);
				cube_r58.cubeList.add(new ModelBox(cube_r58, 12, 45, -7.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, false));

				cube_r59 = new ModelRenderer(this);
				cube_r59.setRotationPoint(4.5F, 1.8125F, -15.1875F);
				foot3.addChild(cube_r59);
				setRotationAngle(cube_r59, 0.0F, 0.0F, 0.0F);
				cube_r59.cubeList.add(new ModelBox(cube_r59, 76, 74, -7.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, false));

				leg4 = new ModelRenderer(this);
				leg4.setRotationPoint(5.5F, 5.0F, 21.5F);
				body.addChild(leg4);


				joint6 = new ModelRenderer(this);
				joint6.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg4.addChild(joint6);
				setRotationAngle(joint6, -1.5708F, 0.0F, 0.0F);


				cube_r60 = new ModelRenderer(this);
				cube_r60.setRotationPoint(-2.25F, 17.5F, -2.75F);
				joint6.addChild(cube_r60);
				setRotationAngle(cube_r60, 0.7854F, 0.0F, 0.0F);
				cube_r60.cubeList.add(new ModelBox(cube_r60, 0, 60, 2.0F, -14.3536F, 10.182F, 6, 14, 8, 0.2F, true));

				joint9 = new ModelRenderer(this);
				joint9.setRotationPoint(-1.25F, 17.5F, -2.75F);
				joint6.addChild(joint9);


				cube_r61 = new ModelRenderer(this);
				cube_r61.setRotationPoint(10.0F, 27.5103F, 8.3355F);
				joint9.addChild(cube_r61);
				setRotationAngle(cube_r61, -1.1781F, 0.0F, 0.0F);
				cube_r61.cubeList.add(new ModelBox(cube_r61, 56, 74, -8.0F, -24.6324F, -38.7863F, 5, 11, 5, 0.0F, true));

				foot4 = new ModelRenderer(this);
				foot4.setRotationPoint(4.5F, -16.9625F, 16.4375F);
				joint9.addChild(foot4);
				setRotationAngle(foot4, 1.5708F, 0.0F, 0.0F);


				cube_r62 = new ModelRenderer(this);
				cube_r62.setRotationPoint(-6.5F, 2.0625F, -22.4375F);
				foot4.addChild(cube_r62);
				setRotationAngle(cube_r62, 0.0F, 0.0F, 0.0F);
				cube_r62.cubeList.add(new ModelBox(cube_r62, 6, 39, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

				cube_r63 = new ModelRenderer(this);
				cube_r63.setRotationPoint(-8.0F, 2.0625F, -22.4375F);
				foot4.addChild(cube_r63);
				setRotationAngle(cube_r63, 0.0F, 0.0F, 0.0F);
				cube_r63.cubeList.add(new ModelBox(cube_r63, 12, 42, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

				cube_r64 = new ModelRenderer(this);
				cube_r64.setRotationPoint(-5.0F, 2.0625F, -22.4375F);
				foot4.addChild(cube_r64);
				setRotationAngle(cube_r64, 0.0F, 0.0F, 0.0F);
				cube_r64.cubeList.add(new ModelBox(cube_r64, 12, 45, 6.0F, -2.0F, 15.0F, 1, 2, 1, 0.0F, true));

				cube_r65 = new ModelRenderer(this);
				cube_r65.setRotationPoint(-4.5F, 1.8125F, -15.1875F);
				foot4.addChild(cube_r65);
				setRotationAngle(cube_r65, 0.0F, 0.0F, 0.0F);
				cube_r65.cubeList.add(new ModelBox(cube_r65, 76, 74, 2.0F, -2.0F, 8.0F, 5, 2, 8, 0.0F, true));

				tail[0][0] = new ModelRenderer(this);
				tail[0][0].setRotationPoint(-3.0F, 0.0F, 26.0F);
				body.addChild(tail[0][0]);
				setRotationAngle(tail[0][0], -1.309F, -0.7854F, 0.0F);
				tail[0][0].cubeList.add(new ModelBox(tail[0][0], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.0F, false));

				tail[0][1] = new ModelRenderer(this);
				tail[0][1].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[0][0].addChild(tail[0][1]);
				setRotationAngle(tail[0][1], 0.2618F, 0.0F, 0.0873F);
				tail[0][1].cubeList.add(new ModelBox(tail[0][1], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.2F, false));

				tail[0][2] = new ModelRenderer(this);
				tail[0][2].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[0][1].addChild(tail[0][2]);
				setRotationAngle(tail[0][2], 0.2618F, 0.0F, 0.0873F);
				tail[0][2].cubeList.add(new ModelBox(tail[0][2], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.4F, false));

				tail[0][3] = new ModelRenderer(this);
				tail[0][3].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[0][2].addChild(tail[0][3]);
				setRotationAngle(tail[0][3], 0.2618F, 0.0F, 0.0873F);
				tail[0][3].cubeList.add(new ModelBox(tail[0][3], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.5F, false));

				tail[0][4] = new ModelRenderer(this);
				tail[0][4].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[0][3].addChild(tail[0][4]);
				setRotationAngle(tail[0][4], -0.2618F, 0.0F, 0.0873F);
				tail[0][4].cubeList.add(new ModelBox(tail[0][4], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.3F, false));

				tail[0][5] = new ModelRenderer(this);
				tail[0][5].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[0][4].addChild(tail[0][5]);
				setRotationAngle(tail[0][5], -0.2618F, 0.0F, 0.0873F);
				tail[0][5].cubeList.add(new ModelBox(tail[0][5], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.1F, false));

				tail[0][6] = new ModelRenderer(this);
				tail[0][6].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[0][5].addChild(tail[0][6]);
				setRotationAngle(tail[0][6], -0.2618F, 0.0F, 0.0873F);
				tail[0][6].cubeList.add(new ModelBox(tail[0][6], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.3F, false));

				tail[0][7] = new ModelRenderer(this);
				tail[0][7].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[0][6].addChild(tail[0][7]);
				setRotationAngle(tail[0][7], -0.2618F, 0.0F, 0.0873F);
				tail[0][7].cubeList.add(new ModelBox(tail[0][7], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.8F, false));

				tail[1][0] = new ModelRenderer(this);
				tail[1][0].setRotationPoint(3.0F, 0.0F, 26.0F);
				body.addChild(tail[1][0]);
				setRotationAngle(tail[1][0], -1.309F, 0.7854F, 0.0F);
				tail[1][0].cubeList.add(new ModelBox(tail[1][0], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.0F, true));

				tail[1][1] = new ModelRenderer(this);
				tail[1][1].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[1][0].addChild(tail[1][1]);
				setRotationAngle(tail[1][1], 0.2618F, 0.0F, -0.0873F);
				tail[1][1].cubeList.add(new ModelBox(tail[1][1], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.2F, true));

				tail[1][2] = new ModelRenderer(this);
				tail[1][2].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[1][1].addChild(tail[1][2]);
				setRotationAngle(tail[1][2], 0.2618F, 0.0F, -0.0873F);
				tail[1][2].cubeList.add(new ModelBox(tail[1][2], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.4F, true));

				tail[1][3] = new ModelRenderer(this);
				tail[1][3].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[1][2].addChild(tail[1][3]);
				setRotationAngle(tail[1][3], 0.2618F, 0.0F, -0.0873F);
				tail[1][3].cubeList.add(new ModelBox(tail[1][3], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.5F, true));

				tail[1][4] = new ModelRenderer(this);
				tail[1][4].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[1][3].addChild(tail[1][4]);
				setRotationAngle(tail[1][4], -0.2618F, 0.0F, -0.0873F);
				tail[1][4].cubeList.add(new ModelBox(tail[1][4], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.3F, true));

				tail[1][5] = new ModelRenderer(this);
				tail[1][5].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[1][4].addChild(tail[1][5]);
				setRotationAngle(tail[1][5], -0.2618F, 0.0F, -0.0873F);
				tail[1][5].cubeList.add(new ModelBox(tail[1][5], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.1F, true));

				tail[1][6] = new ModelRenderer(this);
				tail[1][6].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[1][5].addChild(tail[1][6]);
				setRotationAngle(tail[1][6], -0.2618F, 0.0F, -0.0873F);
				tail[1][6].cubeList.add(new ModelBox(tail[1][6], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.3F, true));

				tail[1][7] = new ModelRenderer(this);
				tail[1][7].setRotationPoint(0.0F, -6.0F, 0.0F);
				tail[1][6].addChild(tail[1][7]);
				setRotationAngle(tail[1][7], -0.2618F, 0.0F, -0.0873F);
				tail[1][7].cubeList.add(new ModelBox(tail[1][7], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.8F, true));

				bodyFlamed = new ModelRenderer(this);
				bodyFlamed.setRotationPoint(0.0F, 3.0F, 0.0F);


				cube_r66 = new ModelRenderer(this);
				cube_r66.setRotationPoint(6.0F, 8.0F, 4.25F);
				bodyFlamed.addChild(cube_r66);
				setRotationAngle(cube_r66, 0.0F, 0.0F, 0.0F);
				cube_r66.cubeList.add(new ModelBox(cube_r66, 0, 31, -13.0F, -12.0F, 5.0F, 14, 12, 17, -0.01F, false));

				cube_r67 = new ModelRenderer(this);
				cube_r67.setRotationPoint(6.0F, 8.0F, -8.0F);
				bodyFlamed.addChild(cube_r67);
				setRotationAngle(cube_r67, -0.0436F, 0.0F, 0.0F);
				cube_r67.cubeList.add(new ModelBox(cube_r67, 48, 46, -14.0F, -14.0F, 14.0F, 16, 14, 14, -0.01F, false));

				cube_r68 = new ModelRenderer(this);
				cube_r68.setRotationPoint(6.0F, 8.0F, -13.5F);
				bodyFlamed.addChild(cube_r68);
				setRotationAngle(cube_r68, -0.0873F, 0.0F, 0.0F);
				cube_r68.cubeList.add(new ModelBox(cube_r68, 0, 0, -15.0F, -16.0F, 5.0F, 18, 15, 16, -0.01F, false));

				headFlamed = new ModelRenderer(this);
				headFlamed.setRotationPoint(0.0F, 0.0F, -6.0F);
				bodyFlamed.addChild(headFlamed);


				cube_r69 = new ModelRenderer(this);
				cube_r69.setRotationPoint(6.0F, 2.75F, 4.0F);
				headFlamed.addChild(cube_r69);
				setRotationAngle(cube_r69, 0.0F, 0.0F, 0.0F);
				cube_r69.cubeList.add(new ModelBox(cube_r69, 45, 43, -11.0F, -8.0F, -14.0F, 10, 2, 1, -0.01F, false));

				cube_r70 = new ModelRenderer(this);
				cube_r70.setRotationPoint(6.0F, 9.75F, 4.25F);
				headFlamed.addChild(cube_r70);
				setRotationAngle(cube_r70, 0.0F, 0.0F, 0.0F);
				cube_r70.cubeList.add(new ModelBox(cube_r70, 57, 20, -11.0F, -15.0F, -14.0F, 10, 10, 11, -0.01F, false));

				ears2 = new ModelRenderer(this);
				ears2.setRotationPoint(11.75F, 5.25F, 0.25F);
				headFlamed.addChild(ears2);


				rightEar2 = new ModelRenderer(this);
				rightEar2.setRotationPoint(-12.75F, 6.0F, -11.0F);
				ears2.addChild(rightEar2);
				setRotationAngle(rightEar2, -0.5744F, -0.3332F, -0.468F);
				rightEar2.cubeList.add(new ModelBox(rightEar2, 0, 8, 3.5F, -18.5F, -8.25F, 2, 6, 1, -0.01F, false));
				rightEar2.cubeList.add(new ModelBox(rightEar2, 0, 0, 4.3284F, -18.5F, -8.25F, 2, 6, 1, -0.01F, false));

				cube_r71 = new ModelRenderer(this);
				cube_r71.setRotationPoint(16.4047F, -0.6456F, 0.0F);
				rightEar2.addChild(cube_r71);
				setRotationAngle(cube_r71, 0.0F, 0.0F, -0.7854F);
				cube_r71.cubeList.add(new ModelBox(cube_r71, 28, 70, 3.5F, -21.75F, -8.25F, 2, 2, 1, -0.01F, false));

				leftEar2 = new ModelRenderer(this);
				leftEar2.setRotationPoint(-10.75F, 6.0F, -11.0F);
				ears2.addChild(leftEar2);
				setRotationAngle(leftEar2, -0.5744F, 0.3332F, 0.468F);
				leftEar2.cubeList.add(new ModelBox(leftEar2, 0, 8, -5.5F, -18.5F, -8.25F, 2, 6, 1, -0.01F, true));
				leftEar2.cubeList.add(new ModelBox(leftEar2, 0, 0, -6.3284F, -18.5F, -8.25F, 2, 6, 1, -0.01F, true));

				cube_r72 = new ModelRenderer(this);
				cube_r72.setRotationPoint(-16.4047F, -0.6456F, 0.0F);
				leftEar2.addChild(cube_r72);
				setRotationAngle(cube_r72, 0.0F, 0.0F, 0.7854F);
				cube_r72.cubeList.add(new ModelBox(cube_r72, 28, 70, -5.5F, -21.75F, -8.25F, 2, 2, 1, -0.01F, true));

				hair11 = new ModelRenderer(this);
				hair11.setRotationPoint(11.75F, 5.25F, 0.25F);
				headFlamed.addChild(hair11);


				hair12 = new ModelRenderer(this);
				hair12.setRotationPoint(3.5F, 10.25F, -19.25F);
				hair11.addChild(hair12);
				setRotationAngle(hair12, -1.1345F, 0.0436F, -1.0036F);
				hair12.cubeList.add(new ModelBox(hair12, 28, 66, -1.6651F, -21.6317F, -16.75F, 2, 3, 1, -0.01F, false));
				hair12.cubeList.add(new ModelBox(hair12, 42, 60, -0.8367F, -21.6317F, -16.75F, 2, 3, 1, -0.01F, false));

				cube_r73 = new ModelRenderer(this);
				cube_r73.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				hair12.addChild(cube_r73);
				setRotationAngle(cube_r73, 0.0F, 0.0F, 0.7854F);
				cube_r73.cubeList.add(new ModelBox(cube_r73, 70, 0, -1.0F, -23.0F, -16.75F, 2, 2, 1, -0.01F, false));

				hair13 = new ModelRenderer(this);
				hair13.setRotationPoint(-27.0F, 10.25F, -19.25F);
				hair11.addChild(hair13);
				setRotationAngle(hair13, -1.1345F, -0.0436F, 1.0036F);
				hair13.cubeList.add(new ModelBox(hair13, 28, 66, -0.3349F, -21.6317F, -16.75F, 2, 3, 1, -0.01F, true));
				hair13.cubeList.add(new ModelBox(hair13, 42, 60, -1.1633F, -21.6317F, -16.75F, 2, 3, 1, -0.01F, true));

				cube_r74 = new ModelRenderer(this);
				cube_r74.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				hair13.addChild(cube_r74);
				setRotationAngle(cube_r74, 0.0F, 0.0F, -0.7854F);
				cube_r74.cubeList.add(new ModelBox(cube_r74, 70, 0, -1.0F, -23.0F, -16.75F, 2, 2, 1, -0.01F, true));

				hair14 = new ModelRenderer(this);
				hair14.setRotationPoint(3.5F, 8.25F, -10.5F);
				hair11.addChild(hair14);
				setRotationAngle(hair14, -0.829F, 0.0436F, -0.9163F);
				hair14.cubeList.add(new ModelBox(hair14, 0, 60, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, -0.01F, false));
				hair14.cubeList.add(new ModelBox(hair14, 6, 42, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, -0.01F, false));

				cube_r75 = new ModelRenderer(this);
				cube_r75.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				hair14.addChild(cube_r75);
				setRotationAngle(cube_r75, 0.0F, 0.0F, 0.7854F);
				cube_r75.cubeList.add(new ModelBox(cube_r75, 70, 3, -1.0F, -23.0F, -16.75F, 2, 2, 1, -0.01F, false));

				hair15 = new ModelRenderer(this);
				hair15.setRotationPoint(-27.0F, 8.25F, -10.5F);
				hair11.addChild(hair15);
				setRotationAngle(hair15, -0.829F, -0.0436F, 0.9163F);
				hair15.cubeList.add(new ModelBox(hair15, 0, 60, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, -0.01F, true));
				hair15.cubeList.add(new ModelBox(hair15, 6, 42, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, -0.01F, true));

				cube_r76 = new ModelRenderer(this);
				cube_r76.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				hair15.addChild(cube_r76);
				setRotationAngle(cube_r76, 0.0F, 0.0F, -0.7854F);
				cube_r76.cubeList.add(new ModelBox(cube_r76, 70, 3, -1.0F, -23.0F, -16.75F, 2, 2, 1, -0.01F, true));

				hair16 = new ModelRenderer(this);
				hair16.setRotationPoint(5.75F, 5.25F, -10.5F);
				hair11.addChild(hair16);
				setRotationAngle(hair16, -0.829F, 0.0436F, -0.9163F);
				hair16.cubeList.add(new ModelBox(hair16, 45, 33, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, -0.01F, false));
				hair16.cubeList.add(new ModelBox(hair16, 10, 36, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, -0.01F, false));

				cube_r77 = new ModelRenderer(this);
				cube_r77.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				hair16.addChild(cube_r77);
				setRotationAngle(cube_r77, 0.0F, 0.0F, 0.7854F);
				cube_r77.cubeList.add(new ModelBox(cube_r77, 66, 41, -1.0F, -23.0F, -16.75F, 2, 2, 1, -0.01F, false));

				hair17 = new ModelRenderer(this);
				hair17.setRotationPoint(-29.25F, 5.25F, -10.5F);
				hair11.addChild(hair17);
				setRotationAngle(hair17, -0.829F, -0.0436F, 0.9163F);
				hair17.cubeList.add(new ModelBox(hair17, 45, 33, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, -0.01F, true));
				hair17.cubeList.add(new ModelBox(hair17, 10, 36, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, -0.01F, true));

				cube_r78 = new ModelRenderer(this);
				cube_r78.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				hair17.addChild(cube_r78);
				setRotationAngle(cube_r78, 0.0F, 0.0F, -0.7854F);
				cube_r78.cubeList.add(new ModelBox(cube_r78, 66, 41, -1.0F, -23.0F, -16.75F, 2, 2, 1, -0.01F, true));

				hair18 = new ModelRenderer(this);
				hair18.setRotationPoint(-9.0F, -34.75F, 12.0F);
				hair11.addChild(hair18);
				setRotationAngle(hair18, -1.1781F, 0.0F, 0.0F);
				hair18.cubeList.add(new ModelBox(hair18, 9, 8, -3.3358F, 24.2726F, 14.5601F, 2, 5, 1, -0.01F, false));
				hair18.cubeList.add(new ModelBox(hair18, 9, 0, -4.1642F, 24.2726F, 14.5601F, 2, 5, 1, -0.01F, false));

				cube_r79 = new ModelRenderer(this);
				cube_r79.setRotationPoint(12.8063F, 39.8289F, 31.3101F);
				hair18.addChild(cube_r79);
				setRotationAngle(cube_r79, 0.0F, 0.0F, -0.7854F);
				cube_r79.cubeList.add(new ModelBox(cube_r79, 0, 45, -0.6569F, -23.3431F, -14.6549F, 2, 2, 1, -0.01F, false));
				cube_r79.cubeList.add(new ModelBox(cube_r79, 45, 38, -3.1318F, -25.8179F, -14.6549F, 2, 2, 1, -0.01F, false));
				cube_r79.cubeList.add(new ModelBox(cube_r79, 35, 62, -2.6433F, -23.8315F, -15.3642F, 2, 2, 1, -0.01F, false));
				cube_r79.cubeList.add(new ModelBox(cube_r79, 0, 65, -1.0F, -23.0F, -16.75F, 2, 2, 1, -0.01F, false));

				cube_r80 = new ModelRenderer(this);
				cube_r80.setRotationPoint(-18.3063F, 39.8289F, 31.3101F);
				hair18.addChild(cube_r80);
				setRotationAngle(cube_r80, 0.0F, 0.0F, 0.7854F);
				cube_r80.cubeList.add(new ModelBox(cube_r80, 45, 38, 1.1318F, -25.8179F, -14.6549F, 2, 2, 1, -0.01F, true));
				cube_r80.cubeList.add(new ModelBox(cube_r80, 35, 62, 0.6433F, -23.8315F, -15.3642F, 2, 2, 1, -0.01F, true));

				hair19 = new ModelRenderer(this);
				hair19.setRotationPoint(4.25F, 9.75F, -10.5F);
				hair11.addChild(hair19);
				setRotationAngle(hair19, -0.829F, -0.3491F, -0.9163F);
				hair19.cubeList.add(new ModelBox(hair19, 52, 0, -1.6651F, -21.6317F, -16.75F, 2, 4, 1, -0.01F, false));
				hair19.cubeList.add(new ModelBox(hair19, 0, 39, -0.8367F, -21.6317F, -16.75F, 2, 5, 1, -0.01F, false));

				cube_r81 = new ModelRenderer(this);
				cube_r81.setRotationPoint(-15.8072F, -6.0753F, 0.0F);
				hair19.addChild(cube_r81);
				setRotationAngle(cube_r81, 0.0F, 0.0F, 0.7854F);
				cube_r81.cubeList.add(new ModelBox(cube_r81, 68, 17, -1.0F, -23.0F, -16.75F, 2, 2, 1, -0.01F, false));

				hair20 = new ModelRenderer(this);
				hair20.setRotationPoint(-27.75F, 9.75F, -10.5F);
				hair11.addChild(hair20);
				setRotationAngle(hair20, -0.829F, 0.3491F, 0.9163F);
				hair20.cubeList.add(new ModelBox(hair20, 52, 0, -0.3349F, -21.6317F, -16.75F, 2, 4, 1, -0.01F, true));
				hair20.cubeList.add(new ModelBox(hair20, 0, 39, -1.1633F, -21.6317F, -16.75F, 2, 5, 1, -0.01F, true));

				cube_r82 = new ModelRenderer(this);
				cube_r82.setRotationPoint(15.8072F, -6.0753F, 0.0F);
				hair20.addChild(cube_r82);
				setRotationAngle(cube_r82, 0.0F, 0.0F, -0.7854F);
				cube_r82.cubeList.add(new ModelBox(cube_r82, 68, 17, -1.0F, -23.0F, -16.75F, 2, 2, 1, -0.01F, true));

				topMouth2 = new ModelRenderer(this);
				topMouth2.setRotationPoint(-0.25F, 20.5F, 14.25F);
				headFlamed.addChild(topMouth2);


				cube_r83 = new ModelRenderer(this);
				cube_r83.setRotationPoint(7.5815F, -13.5F, -19.9805F);
				topMouth2.addChild(cube_r83);
				setRotationAngle(cube_r83, 0.0873F, 0.1309F, 0.0F);
				cube_r83.cubeList.add(new ModelBox(cube_r83, 88, 0, -5.0F, -8.0F, -10.0F, 2, 3, 7, -0.01F, true));

				cube_r84 = new ModelRenderer(this);
				cube_r84.setRotationPoint(-7.0815F, -13.5F, -19.9805F);
				topMouth2.addChild(cube_r84);
				setRotationAngle(cube_r84, 0.0873F, -0.1309F, 0.0F);
				cube_r84.cubeList.add(new ModelBox(cube_r84, 88, 0, 3.0F, -8.0F, -10.0F, 2, 3, 7, -0.01F, false));

				cube_r85 = new ModelRenderer(this);
				cube_r85.setRotationPoint(0.25F, -20.4088F, -30.0418F);
				topMouth2.addChild(cube_r85);
				setRotationAngle(cube_r85, 0.1745F, 0.0F, 0.0F);
				cube_r85.cubeList.add(new ModelBox(cube_r85, 69, 7, -3.0F, -0.1988F, -0.1005F, 6, 3, 7, -0.01F, false));

				nose2 = new ModelRenderer(this);
				nose2.setRotationPoint(6.25F, -13.5F, -20.75F);
				topMouth2.addChild(nose2);


				cube_r86 = new ModelRenderer(this);
				cube_r86.setRotationPoint(0.0F, 0.0F, 0.0F);
				nose2.addChild(cube_r86);
				setRotationAngle(cube_r86, 0.0873F, 0.0F, 0.0F);
				cube_r86.cubeList.add(new ModelBox(cube_r86, 55, 46, -7.0F, -8.0F, -9.0F, 2, 1, 1, -0.01F, false));

				jaw2 = new ModelRenderer(this);
				jaw2.setRotationPoint(-0.0749F, 3.6113F, -9.4922F);
				headFlamed.addChild(jaw2);
				setRotationAngle(jaw2, 0.5236F, 0.0F, 0.0F);


				cube_r87 = new ModelRenderer(this);
				cube_r87.setRotationPoint(6.0749F, 8.1425F, -10.4545F);
				jaw2.addChild(cube_r87);
				setRotationAngle(cube_r87, 0.1745F, 0.0F, 0.0F);
				cube_r87.cubeList.add(new ModelBox(cube_r87, 20, 62, -9.0F, -6.0F, 9.0F, 6, 1, 3, -0.01F, false));

				cube_r88 = new ModelRenderer(this);
				cube_r88.setRotationPoint(6.0749F, 9.3049F, 2.6281F);
				jaw2.addChild(cube_r88);
				setRotationAngle(cube_r88, -0.3054F, 0.0F, 0.0F);
				cube_r88.cubeList.add(new ModelBox(cube_r88, 0, 34, -9.0F, -6.0F, -10.0F, 6, 1, 1, -0.01F, false));

				cube_r89 = new ModelRenderer(this);
				cube_r89.setRotationPoint(6.0749F, 5.9394F, 5.9459F);
				jaw2.addChild(cube_r89);
				setRotationAngle(cube_r89, 0.0873F, 0.0F, 0.0F);
				cube_r89.cubeList.add(new ModelBox(cube_r89, 0, 31, -9.0F, -6.0F, -10.0F, 6, 1, 2, -0.01F, false));

				cube_r90 = new ModelRenderer(this);
				cube_r90.setRotationPoint(-6.8275F, 5.6387F, 4.6908F);
				jaw2.addChild(cube_r90);
				setRotationAngle(cube_r90, 0.0873F, -0.0873F, 0.0F);
				cube_r90.cubeList.add(new ModelBox(cube_r90, 0, 0, 3.0F, -6.0F, -10.0F, 1, 1, 7, -0.01F, false));

				cube_r91 = new ModelRenderer(this);
				cube_r91.setRotationPoint(6.9773F, 5.6387F, 4.6908F);
				jaw2.addChild(cube_r91);
				setRotationAngle(cube_r91, 0.0873F, 0.0873F, 0.0F);
				cube_r91.cubeList.add(new ModelBox(cube_r91, 0, 8, -4.0F, -6.0F, -10.0F, 1, 1, 7, -0.01F, false));

				cube_r92 = new ModelRenderer(this);
				cube_r92.setRotationPoint(6.0749F, 5.6387F, 4.9922F);
				jaw2.addChild(cube_r92);
				setRotationAngle(cube_r92, 0.0873F, 0.0F, 0.0F);
				cube_r92.cubeList.add(new ModelBox(cube_r92, 69, 84, -9.0F, -6.0F, -10.0F, 6, 1, 7, -0.01F, false));

				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(8.2288F, -3.2172F, 4.8801F);
				jaw2.addChild(bone5);


				cube_r93 = new ModelRenderer(this);
				cube_r93.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone5.addChild(cube_r93);
				setRotationAngle(cube_r93, 0.1809F, 0.1538F, -0.7744F);
				cube_r93.cubeList.add(new ModelBox(cube_r93, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, -0.01F, false));

				cube_r94 = new ModelRenderer(this);
				cube_r94.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone5.addChild(cube_r94);
				setRotationAngle(cube_r94, 0.0564F, 0.0308F, -0.7859F);
				cube_r94.cubeList.add(new ModelBox(cube_r94, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, -0.01F, false));

				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(9.2288F, -3.2172F, 4.8801F);
				jaw2.addChild(bone6);


				cube_r95 = new ModelRenderer(this);
				cube_r95.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone6.addChild(cube_r95);
				setRotationAngle(cube_r95, 0.1809F, 0.1538F, -0.7744F);
				cube_r95.cubeList.add(new ModelBox(cube_r95, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, -0.01F, false));

				cube_r96 = new ModelRenderer(this);
				cube_r96.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone6.addChild(cube_r96);
				setRotationAngle(cube_r96, 0.0564F, 0.0308F, -0.7859F);
				cube_r96.cubeList.add(new ModelBox(cube_r96, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, -0.01F, false));

				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(10.2288F, -3.2172F, 4.8801F);
				jaw2.addChild(bone7);


				cube_r97 = new ModelRenderer(this);
				cube_r97.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone7.addChild(cube_r97);
				setRotationAngle(cube_r97, 0.1809F, 0.1538F, -0.7744F);
				cube_r97.cubeList.add(new ModelBox(cube_r97, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, -0.01F, false));

				cube_r98 = new ModelRenderer(this);
				cube_r98.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone7.addChild(cube_r98);
				setRotationAngle(cube_r98, 0.0564F, 0.0308F, -0.7859F);
				cube_r98.cubeList.add(new ModelBox(cube_r98, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, -0.01F, false));

				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(11.2288F, -3.2172F, 4.8801F);
				jaw2.addChild(bone8);


				cube_r99 = new ModelRenderer(this);
				cube_r99.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone8.addChild(cube_r99);
				setRotationAngle(cube_r99, 0.1809F, 0.1538F, -0.7744F);
				cube_r99.cubeList.add(new ModelBox(cube_r99, 40, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, -0.01F, false));

				cube_r100 = new ModelRenderer(this);
				cube_r100.setRotationPoint(0.0181F, 1.7427F, -0.2458F);
				bone8.addChild(cube_r100);
				setRotationAngle(cube_r100, 0.0564F, 0.0308F, -0.7859F);
				cube_r100.cubeList.add(new ModelBox(cube_r100, 44, 64, -9.0F, -6.0F, -10.0F, 1, 1, 1, -0.01F, false));

				leg1Flamed = new ModelRenderer(this);
				leg1Flamed.setRotationPoint(-4.75F, 1.0F, -1.0F);
				bodyFlamed.addChild(leg1Flamed);


				joint10 = new ModelRenderer(this);
				joint10.setRotationPoint(0.0F, -2.0F, -1.0F);
				leg1Flamed.addChild(joint10);
				setRotationAngle(joint10, 0.1745F, 0.0F, 0.1745F);


				cube_r101 = new ModelRenderer(this);
				cube_r101.setRotationPoint(2.0F, 12.0F, -11.25F);
				joint10.addChild(cube_r101);
				setRotationAngle(cube_r101, -0.0436F, 0.0F, 0.0F);
				cube_r101.cubeList.add(new ModelBox(cube_r101, 28, 66, -8.0F, -12.0F, 7.0F, 6, 14, 8, -0.01F, false));

				joint11 = new ModelRenderer(this);
				joint11.setRotationPoint(-2.875F, 15.25F, 0.75F);
				joint10.addChild(joint11);
				setRotationAngle(joint11, -0.517F, -0.0869F, -0.1515F);


				cube_r102 = new ModelRenderer(this);
				cube_r102.setRotationPoint(4.375F, 5.75F, -14.75F);
				joint11.addChild(cube_r102);
				setRotationAngle(cube_r102, 0.0F, 0.0F, 0.0F);
				cube_r102.cubeList.add(new ModelBox(cube_r102, 0, 82, -7.0F, -8.0F, 11.0F, 5, 10, 5, -0.01F, false));

				foot5 = new ModelRenderer(this);
				foot5.setRotationPoint(-0.125F, 5.9375F, 0.4375F);
				joint11.addChild(foot5);
				setRotationAngle(foot5, 0.3491F, 0.0F, 0.0F);


				cube_r103 = new ModelRenderer(this);
				cube_r103.setRotationPoint(6.5F, 2.0625F, -22.4375F);
				foot5.addChild(cube_r103);
				setRotationAngle(cube_r103, 0.0F, 0.0F, 0.0F);
				cube_r103.cubeList.add(new ModelBox(cube_r103, 6, 39, -7.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, false));

				cube_r104 = new ModelRenderer(this);
				cube_r104.setRotationPoint(8.0F, 2.0625F, -22.4375F);
				foot5.addChild(cube_r104);
				setRotationAngle(cube_r104, 0.0F, 0.0F, 0.0F);
				cube_r104.cubeList.add(new ModelBox(cube_r104, 12, 42, -7.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, false));

				cube_r105 = new ModelRenderer(this);
				cube_r105.setRotationPoint(5.0F, 2.0625F, -22.4375F);
				foot5.addChild(cube_r105);
				setRotationAngle(cube_r105, 0.0F, 0.0F, 0.0F);
				cube_r105.cubeList.add(new ModelBox(cube_r105, 12, 45, -7.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, false));

				cube_r106 = new ModelRenderer(this);
				cube_r106.setRotationPoint(4.5F, 1.8125F, -15.1875F);
				foot5.addChild(cube_r106);
				setRotationAngle(cube_r106, 0.0F, 0.0F, 0.0F);
				cube_r106.cubeList.add(new ModelBox(cube_r106, 76, 74, -7.0F, -2.0F, 8.0F, 5, 2, 8, -0.01F, false));

				leg2Flamed = new ModelRenderer(this);
				leg2Flamed.setRotationPoint(4.75F, 1.0F, -1.0F);
				bodyFlamed.addChild(leg2Flamed);


				joint12 = new ModelRenderer(this);
				joint12.setRotationPoint(0.0F, -2.0F, -1.0F);
				leg2Flamed.addChild(joint12);
				setRotationAngle(joint12, 0.1745F, 0.0F, -0.1745F);


				cube_r107 = new ModelRenderer(this);
				cube_r107.setRotationPoint(-2.0F, 12.0F, -11.25F);
				joint12.addChild(cube_r107);
				setRotationAngle(cube_r107, -0.0436F, 0.0F, 0.0F);
				cube_r107.cubeList.add(new ModelBox(cube_r107, 28, 66, 2.0F, -12.0F, 7.0F, 6, 14, 8, -0.01F, true));

				joint13 = new ModelRenderer(this);
				joint13.setRotationPoint(2.875F, 15.25F, 0.75F);
				joint12.addChild(joint13);
				setRotationAngle(joint13, -0.517F, 0.0869F, 0.1515F);


				cube_r108 = new ModelRenderer(this);
				cube_r108.setRotationPoint(-4.375F, 5.75F, -14.75F);
				joint13.addChild(cube_r108);
				setRotationAngle(cube_r108, 0.0F, 0.0F, 0.0F);
				cube_r108.cubeList.add(new ModelBox(cube_r108, 0, 82, 2.0F, -8.0F, 11.0F, 5, 10, 5, -0.01F, true));

				foot6 = new ModelRenderer(this);
				foot6.setRotationPoint(0.125F, 5.9375F, 0.4375F);
				joint13.addChild(foot6);
				setRotationAngle(foot6, 0.3491F, 0.0F, 0.0F);


				cube_r109 = new ModelRenderer(this);
				cube_r109.setRotationPoint(-6.5F, 2.0625F, -22.4375F);
				foot6.addChild(cube_r109);
				setRotationAngle(cube_r109, 0.0F, 0.0F, 0.0F);
				cube_r109.cubeList.add(new ModelBox(cube_r109, 6, 39, 6.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, true));

				cube_r110 = new ModelRenderer(this);
				cube_r110.setRotationPoint(-8.0F, 2.0625F, -22.4375F);
				foot6.addChild(cube_r110);
				setRotationAngle(cube_r110, 0.0F, 0.0F, 0.0F);
				cube_r110.cubeList.add(new ModelBox(cube_r110, 12, 42, 6.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, true));

				cube_r111 = new ModelRenderer(this);
				cube_r111.setRotationPoint(-5.0F, 2.0625F, -22.4375F);
				foot6.addChild(cube_r111);
				setRotationAngle(cube_r111, 0.0F, 0.0F, 0.0F);
				cube_r111.cubeList.add(new ModelBox(cube_r111, 12, 45, 6.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, true));

				cube_r112 = new ModelRenderer(this);
				cube_r112.setRotationPoint(-4.5F, 1.8125F, -15.1875F);
				foot6.addChild(cube_r112);
				setRotationAngle(cube_r112, 0.0F, 0.0F, 0.0F);
				cube_r112.cubeList.add(new ModelBox(cube_r112, 76, 74, 2.0F, -2.0F, 8.0F, 5, 2, 8, -0.01F, true));

				leg3Flamed = new ModelRenderer(this);
				leg3Flamed.setRotationPoint(-5.5F, 5.0F, 21.5F);
				bodyFlamed.addChild(leg3Flamed);


				joint14 = new ModelRenderer(this);
				joint14.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg3Flamed.addChild(joint14);
				setRotationAngle(joint14, -1.5708F, 0.0F, 0.0F);


				cube_r113 = new ModelRenderer(this);
				cube_r113.setRotationPoint(2.25F, 17.5F, -2.75F);
				joint14.addChild(cube_r113);
				setRotationAngle(cube_r113, 0.7854F, 0.0F, 0.0F);
				cube_r113.cubeList.add(new ModelBox(cube_r113, 0, 60, -8.0F, -14.3536F, 10.182F, 6, 14, 8, 0.19F, false));

				joint15 = new ModelRenderer(this);
				joint15.setRotationPoint(1.25F, 17.5F, -2.75F);
				joint14.addChild(joint15);


				cube_r114 = new ModelRenderer(this);
				cube_r114.setRotationPoint(-10.0F, 27.5103F, 8.3355F);
				joint15.addChild(cube_r114);
				setRotationAngle(cube_r114, -1.1781F, 0.0F, 0.0F);
				cube_r114.cubeList.add(new ModelBox(cube_r114, 56, 74, 3.0F, -24.6324F, -38.7863F, 5, 11, 5, -0.01F, false));

				foot7 = new ModelRenderer(this);
				foot7.setRotationPoint(-4.5F, -16.9625F, 16.4375F);
				joint15.addChild(foot7);
				setRotationAngle(foot7, 1.5708F, 0.0F, 0.0F);


				cube_r115 = new ModelRenderer(this);
				cube_r115.setRotationPoint(6.5F, 2.0625F, -22.4375F);
				foot7.addChild(cube_r115);
				setRotationAngle(cube_r115, 0.0F, 0.0F, 0.0F);
				cube_r115.cubeList.add(new ModelBox(cube_r115, 6, 39, -7.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, false));

				cube_r116 = new ModelRenderer(this);
				cube_r116.setRotationPoint(8.0F, 2.0625F, -22.4375F);
				foot7.addChild(cube_r116);
				setRotationAngle(cube_r116, 0.0F, 0.0F, 0.0F);
				cube_r116.cubeList.add(new ModelBox(cube_r116, 12, 42, -7.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, false));

				cube_r117 = new ModelRenderer(this);
				cube_r117.setRotationPoint(5.0F, 2.0625F, -22.4375F);
				foot7.addChild(cube_r117);
				setRotationAngle(cube_r117, 0.0F, 0.0F, 0.0F);
				cube_r117.cubeList.add(new ModelBox(cube_r117, 12, 45, -7.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, false));

				cube_r118 = new ModelRenderer(this);
				cube_r118.setRotationPoint(4.5F, 1.8125F, -15.1875F);
				foot7.addChild(cube_r118);
				setRotationAngle(cube_r118, 0.0F, 0.0F, 0.0F);
				cube_r118.cubeList.add(new ModelBox(cube_r118, 76, 74, -7.0F, -2.0F, 8.0F, 5, 2, 8, -0.01F, false));

				leg4Flamed = new ModelRenderer(this);
				leg4Flamed.setRotationPoint(5.5F, 5.0F, 21.5F);
				bodyFlamed.addChild(leg4Flamed);


				joint16 = new ModelRenderer(this);
				joint16.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg4Flamed.addChild(joint16);
				setRotationAngle(joint16, -1.5708F, 0.0F, 0.0F);


				cube_r119 = new ModelRenderer(this);
				cube_r119.setRotationPoint(-2.25F, 17.5F, -2.75F);
				joint16.addChild(cube_r119);
				setRotationAngle(cube_r119, 0.7854F, 0.0F, 0.0F);
				cube_r119.cubeList.add(new ModelBox(cube_r119, 0, 60, 2.0F, -14.3536F, 10.182F, 6, 14, 8, 0.19F, true));

				joint17 = new ModelRenderer(this);
				joint17.setRotationPoint(-1.25F, 17.5F, -2.75F);
				joint16.addChild(joint17);


				cube_r120 = new ModelRenderer(this);
				cube_r120.setRotationPoint(10.0F, 27.5103F, 8.3355F);
				joint17.addChild(cube_r120);
				setRotationAngle(cube_r120, -1.1781F, 0.0F, 0.0F);
				cube_r120.cubeList.add(new ModelBox(cube_r120, 56, 74, -8.0F, -24.6324F, -38.7863F, 5, 11, 5, -0.01F, true));

				foot8 = new ModelRenderer(this);
				foot8.setRotationPoint(4.5F, -16.9625F, 16.4375F);
				joint17.addChild(foot8);
				setRotationAngle(foot8, 1.5708F, 0.0F, 0.0F);


				cube_r121 = new ModelRenderer(this);
				cube_r121.setRotationPoint(-6.5F, 2.0625F, -22.4375F);
				foot8.addChild(cube_r121);
				setRotationAngle(cube_r121, 0.0F, 0.0F, 0.0F);
				cube_r121.cubeList.add(new ModelBox(cube_r121, 6, 39, 6.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, true));

				cube_r122 = new ModelRenderer(this);
				cube_r122.setRotationPoint(-8.0F, 2.0625F, -22.4375F);
				foot8.addChild(cube_r122);
				setRotationAngle(cube_r122, 0.0F, 0.0F, 0.0F);
				cube_r122.cubeList.add(new ModelBox(cube_r122, 12, 42, 6.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, true));

				cube_r123 = new ModelRenderer(this);
				cube_r123.setRotationPoint(-5.0F, 2.0625F, -22.4375F);
				foot8.addChild(cube_r123);
				setRotationAngle(cube_r123, 0.0F, 0.0F, 0.0F);
				cube_r123.cubeList.add(new ModelBox(cube_r123, 12, 45, 6.0F, -2.0F, 15.0F, 1, 2, 1, -0.01F, true));

				cube_r124 = new ModelRenderer(this);
				cube_r124.setRotationPoint(-4.5F, 1.8125F, -15.1875F);
				foot8.addChild(cube_r124);
				setRotationAngle(cube_r124, 0.0F, 0.0F, 0.0F);
				cube_r124.cubeList.add(new ModelBox(cube_r124, 76, 74, 2.0F, -2.0F, 8.0F, 5, 2, 8, -0.01F, true));

				tailFlamed[0][0] = new ModelRenderer(this);
				tailFlamed[0][0].setRotationPoint(-3.0F, 0.0F, 26.0F);
				bodyFlamed.addChild(tailFlamed[0][0]);
				setRotationAngle(tailFlamed[0][0], -1.309F, -0.7854F, 0.0F);
				tailFlamed[0][0].cubeList.add(new ModelBox(tailFlamed[0][0], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.01F, false));

				tailFlamed[0][1] = new ModelRenderer(this);
				tailFlamed[0][1].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[0][0].addChild(tailFlamed[0][1]);
				setRotationAngle(tailFlamed[0][1], 0.2618F, 0.0F, 0.0873F);
				tailFlamed[0][1].cubeList.add(new ModelBox(tailFlamed[0][1], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.19F, false));

				tailFlamed[0][2] = new ModelRenderer(this);
				tailFlamed[0][2].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[0][1].addChild(tailFlamed[0][2]);
				setRotationAngle(tailFlamed[0][2], 0.2618F, 0.0F, 0.0873F);
				tailFlamed[0][2].cubeList.add(new ModelBox(tailFlamed[0][2], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.39F, false));

				tailFlamed[0][3] = new ModelRenderer(this);
				tailFlamed[0][3].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[0][2].addChild(tailFlamed[0][3]);
				setRotationAngle(tailFlamed[0][3], 0.2618F, 0.0F, 0.0873F);
				tailFlamed[0][3].cubeList.add(new ModelBox(tailFlamed[0][3], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.49F, false));

				tailFlamed[0][4] = new ModelRenderer(this);
				tailFlamed[0][4].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[0][3].addChild(tailFlamed[0][4]);
				setRotationAngle(tailFlamed[0][4], -0.2618F, 0.0F, 0.0873F);
				tailFlamed[0][4].cubeList.add(new ModelBox(tailFlamed[0][4], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.29F, false));

				tailFlamed[0][5] = new ModelRenderer(this);
				tailFlamed[0][5].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[0][4].addChild(tailFlamed[0][5]);
				setRotationAngle(tailFlamed[0][5], -0.2618F, 0.0F, 0.0873F);
				tailFlamed[0][5].cubeList.add(new ModelBox(tailFlamed[0][5], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.09F, false));

				tailFlamed[0][6] = new ModelRenderer(this);
				tailFlamed[0][6].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[0][5].addChild(tailFlamed[0][6]);
				setRotationAngle(tailFlamed[0][6], -0.2618F, 0.0F, 0.0873F);
				tailFlamed[0][6].cubeList.add(new ModelBox(tailFlamed[0][6], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.31F, false));

				tailFlamed[0][7] = new ModelRenderer(this);
				tailFlamed[0][7].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[0][6].addChild(tailFlamed[0][7]);
				setRotationAngle(tailFlamed[0][7], -0.2618F, 0.0F, 0.0873F);
				tailFlamed[0][7].cubeList.add(new ModelBox(tailFlamed[0][7], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.81F, false));

				tailFlamed[1][0] = new ModelRenderer(this);
				tailFlamed[1][0].setRotationPoint(3.0F, 0.0F, 26.0F);
				bodyFlamed.addChild(tailFlamed[1][0]);
				setRotationAngle(tailFlamed[1][0], -1.309F, 0.7854F, 0.0F);
				tailFlamed[1][0].cubeList.add(new ModelBox(tailFlamed[1][0], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.01F, true));

				tailFlamed[1][1] = new ModelRenderer(this);
				tailFlamed[1][1].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[1][0].addChild(tailFlamed[1][1]);
				setRotationAngle(tailFlamed[1][1], 0.2618F, 0.0F, -0.0873F);
				tailFlamed[1][1].cubeList.add(new ModelBox(tailFlamed[1][1], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.19F, true));

				tailFlamed[1][2] = new ModelRenderer(this);
				tailFlamed[1][2].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[1][1].addChild(tailFlamed[1][2]);
				setRotationAngle(tailFlamed[1][2], 0.2618F, 0.0F, -0.0873F);
				tailFlamed[1][2].cubeList.add(new ModelBox(tailFlamed[1][2], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.39F, true));

				tailFlamed[1][3] = new ModelRenderer(this);
				tailFlamed[1][3].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[1][2].addChild(tailFlamed[1][3]);
				setRotationAngle(tailFlamed[1][3], 0.2618F, 0.0F, -0.0873F);
				tailFlamed[1][3].cubeList.add(new ModelBox(tailFlamed[1][3], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.49F, true));

				tailFlamed[1][4] = new ModelRenderer(this);
				tailFlamed[1][4].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[1][3].addChild(tailFlamed[1][4]);
				setRotationAngle(tailFlamed[1][4], -0.2618F, 0.0F, -0.0873F);
				tailFlamed[1][4].cubeList.add(new ModelBox(tailFlamed[1][4], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.29F, true));

				tailFlamed[1][5] = new ModelRenderer(this);
				tailFlamed[1][5].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[1][4].addChild(tailFlamed[1][5]);
				setRotationAngle(tailFlamed[1][5], -0.2618F, 0.0F, -0.0873F);
				tailFlamed[1][5].cubeList.add(new ModelBox(tailFlamed[1][5], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, 0.09F, true));

				tailFlamed[1][6] = new ModelRenderer(this);
				tailFlamed[1][6].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[1][5].addChild(tailFlamed[1][6]);
				setRotationAngle(tailFlamed[1][6], -0.2618F, 0.0F, -0.0873F);
				tailFlamed[1][6].cubeList.add(new ModelBox(tailFlamed[1][6], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.31F, true));

				tailFlamed[1][7] = new ModelRenderer(this);
				tailFlamed[1][7].setRotationPoint(0.0F, -6.0F, 0.0F);
				tailFlamed[1][6].addChild(tailFlamed[1][7]);
				setRotationAngle(tailFlamed[1][7], -0.2618F, 0.0F, -0.0873F);
				tailFlamed[1][7].cubeList.add(new ModelBox(tailFlamed[1][7], 52, 0, -3.0F, -7.0F, -3.0F, 6, 8, 6, -0.81F, true));

				for (int i = 0; i < 2; i++) {
					for (int j = 1; j < 8; j++) {
						tailSwayX[i][j] = (rand.nextFloat() * 0.2618F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
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
				//GlStateManager.enableBlend();
				//GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				//super.render(entity, f0, f1, f2, f3, f4, f5);
				body.render(f5);
				bodyFlamed.render(f5);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableLighting();
				highlight.render(f5);
				GlStateManager.enableLighting();
				//GlStateManager.disableBlend();
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
				body.rotateAngleX = 0.0F;
				for (int i = 0; i < 2; i++) {
					for (int j = 1; j < 8; j++) {
						tailFlamed[i][j].rotateAngleX = tail[i][j].rotateAngleX = MathHelper.sin((f2 - j) * 0.1F) * tailSwayX[i][j];
						tailFlamed[i][j].rotateAngleZ = tail[i][j].rotateAngleZ = MathHelper.cos((f2 - j) * 0.1F) * tailSwayZ[i][j];
						tailFlamed[i][j].rotateAngleY = tail[i][j].rotateAngleY = MathHelper.sin((f2 - j) * 0.1F) * tailSwayY[i][j];
					}
				}
				if (((EntityCustom) e).isShooting()) {
					head.rotateAngleX += -0.1745F;
					jaw.rotateAngleX = 0.7854F;
					jaw2.rotateAngleX = 0.7854F;
				} else {
					jaw.rotateAngleX = 0.0F;
					jaw2.rotateAngleX = 0.0F;
				}
				if (((EntityCustom) e).isFaceDown()) {
					body.rotationPointY = 12.0F;
					head.rotateAngleX = 0.3491F;
					leg1.rotateAngleX = -0.8727F;
					leg2.rotateAngleX = -0.8727F;
					leg3.rotateAngleX = 1.5708F;
					leg4.rotateAngleX = 1.5708F;
				} else {
					body.rotationPointY = 3.0F;
				}
				this.copyModelAngles(body, highlight);
				this.copyModelAngles(body, bodyFlamed);
				this.copyModelAngles(head, eyes);
				this.copyModelAngles(head, headFlamed);
				this.copyModelAngles(leg1, leg1Flamed);
				this.copyModelAngles(leg2, leg2Flamed);
				this.copyModelAngles(leg3, leg3Flamed);
				this.copyModelAngles(leg4, leg4Flamed);
			}

			public void setFlamedVisible(boolean visible) {
				bodyFlamed.showModel = visible;
				body.showModel = !visible;
				highlight.showModel = !visible;
			}
		}
	}
}
