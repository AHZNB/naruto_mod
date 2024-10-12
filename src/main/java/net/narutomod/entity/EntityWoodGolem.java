
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemMokuton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Lists;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWoodGolem extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 276;
	public static final int ENTITYID_RANGED = 277;
	private static final float MODELSCALE = 8.0F;

	public EntityWoodGolem(ElementsNarutomodMod instance) {
		super(instance, 595);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "wood_golem"), ENTITYID).name("wood_golem").tracker(96, 3, true).build());
	}

	public static class EC extends EntityShieldBase implements ItemJutsu.IJutsu {
		protected final int growTime = 30;
		private List<BlockPos> particleArea;
		private double chakraBurn;

		public EC(World world) {
			super(world);
			this.setSize(0.6f * MODELSCALE, 2.0f * MODELSCALE);
			this.setOwnerCanSteer(true, 1.0F);
			this.stepHeight = this.height / 3;
		}

		public EC(EntityLivingBase summonerIn, double chakraUsagePerSec) {
			super(summonerIn);
			this.setSize(0.6f * MODELSCALE, 2.0f * MODELSCALE);
			//this.setPosition(summonerIn.posX, summonerIn.posY - this.height, summonerIn.posZ);
			this.setPosition(summonerIn.posX, summonerIn.posY, summonerIn.posZ);
			this.setOwnerCanSteer(true, 1.0F);
			this.stepHeight = this.height / 3;
			Chakra.Pathway cp = Chakra.pathway(summonerIn);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Chakra.getLevel(summonerIn) * 50d);
			this.setHealth(this.getMaxHealth());
			this.chakraBurn = chakraUsagePerSec;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.MOKUTON;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getAttributeMap().registerAttribute(EntityPlayer.REACH_DISTANCE);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(200.0D);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(18.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		}

		@Override
		public double getMountedYOffset() {
			return this.height * this.getGrowth(this.ticksExisted) + 0.35D;
		}

		@Override
		public double getYOffset() {
			return -0.625d * MODELSCALE;
		}

		private float getGrowth(float ageInTicks) {
			return Math.min(ageInTicks / (float)this.growTime, 1.0f);
		}

		@Override
		public void onLivingUpdate() {
			this.dieOnNoPassengers = !this.isRiding();
			super.onLivingUpdate();
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted <= this.growTime) {
				if (this.particleArea == null) {
					this.particleArea = ProcedureUtils.getNonAirBlocks(this.world, 
					 this.getEntityBoundingBox().offset(0d, -0.5d * this.height, 0d));
				}
				//this.setPosition(this.posX, this.posY + this.height / this.growTime, this.posZ);
				if (this.particleArea != null) {
					for (BlockPos pos : this.particleArea) {
						IBlockState state = this.world.getBlockState(pos);
						if (state.isFullCube() && this.world.isAirBlock(pos.up())) {
							for (int i = 0; i < 10; i++) {
								this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, 0.5d + pos.getX(),
								 1d + pos.getY(), 0.5d + pos.getZ(),
								 this.rand.nextGaussian() * 0.15d, this.rand.nextGaussian() * 0.15d, this.rand.nextGaussian() * 0.15d,
								 Block.getIdFromBlock(state.getBlock()));
							}
						}
					}
				}
			}
			if (this.chakraBurn > 0.0d && this.ticksExisted % 20 == 19) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner != null && !Chakra.pathway(summoner).consume(this.chakraBurn)) {
					this.setDead();
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (!entity.isRiding() || !(entity.getRidingEntity() instanceof EC)) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, 
					 (net.minecraft.util.SoundEvent)net.minecraft.util.SoundEvent.REGISTRY
					 .getObject(new ResourceLocation("narutomod:mokujin_no_jutsu")), SoundCategory.PLAYERS, 1, 1f);
					entity.world.spawnEntity(new EC(entity, ItemMokuton.GOLEM.chakraUsage * 0.05d *
					 ((ItemMokuton.ItemCustom)stack.getItem()).getCurrentJutsuXpModifier(stack, entity)));
					return true;
				}
				return false;
			}
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends RenderLivingBase<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/woodgolem.png");
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelWoodGolem(), 0.5F * MODELSCALE);
			}
	
			@Override
			public boolean shouldRender(EC livingEntity, ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
		 	@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.isBeingRidden() && entity.getControllingPassenger() instanceof EntityLivingBase) {
					this.copyLimbSwing(entity, (EntityLivingBase)entity.getControllingPassenger());
				}
				this.setModelVisibilities(entity);
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
			protected void copyLimbSwing(EC entity, EntityLivingBase rider) {
				entity.swingProgress = rider.swingProgress;
				entity.swingProgressInt = rider.swingProgressInt;
				entity.prevSwingProgress = rider.prevSwingProgress;
				entity.isSwingInProgress = rider.isSwingInProgress;
				entity.swingingHand = rider.swingingHand;
			}
	
			protected void setModelVisibilities(EC entity) {
				if (this.getMainModel() instanceof ModelWoodGolem) {
					ModelWoodGolem model = (ModelWoodGolem)this.getMainModel();
					model.setVisible(true);
					if (entity.isRiding()) {
						model.dragon.showModel = false;
					}
					if (Minecraft.getMinecraft().getRenderViewEntity().equals(entity.getControllingPassenger())
					 && this.renderManager.options.thirdPersonView == 0) {
						model.bipedHead.showModel = false;
						model.bipedHeadwear.showModel = false;
						model.dragon.showModel = false;
					}
				}
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}
	
		// Made with Blockbench 3.9.2
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelWoodGolem extends ModelBiped {
			//private final ModelRenderer bipedHead;
			private final ModelRenderer bone9;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone45;
			private final ModelRenderer HatLayer_r1;
			private final ModelRenderer bone46;
			private final ModelRenderer HatLayer_r2;
			private final ModelRenderer bone44;
			private final ModelRenderer HatLayer_r3;
			private final ModelRenderer HatLayer_r4;
			private final ModelRenderer bone43;
			private final ModelRenderer HatLayer_r5;
			private final ModelRenderer HatLayer_r6;
			private final ModelRenderer bone;
			private final ModelRenderer bone8;
			private final ModelRenderer bone47;
			private final ModelRenderer bone48;
			//private final ModelRenderer bipedHeadwear;
			//private final ModelRenderer bipedBody;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer rightUpperArm;
			private final ModelRenderer rightForeArm;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer leftUpperArm;
			private final ModelRenderer leftForeArm;
			//private final ModelRenderer bipedRightLeg;
			private final ModelRenderer rightThigh;
			private final ModelRenderer rightCalf;
			//private final ModelRenderer bipedLeftLeg;
			private final ModelRenderer leftThigh;
			private final ModelRenderer leftCalf;
			private final ModelRenderer dragon;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone13;
			private final ModelRenderer bone14;
			private final ModelRenderer bone15;
			private final ModelRenderer bone16;
			private final ModelRenderer bone17;
			private final ModelRenderer bone18;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
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
			private final ModelRenderer bone34;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer bone38;
			private final ModelRenderer dragonHead;
			private final ModelRenderer bone39;
			private final ModelRenderer bone50;
			private final ModelRenderer bone49;
			private final ModelRenderer bone51;
			private final ModelRenderer bone40;
			private final ModelRenderer dragonEyes;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
		
			public ModelWoodGolem() {
				textureWidth = 64;
				textureHeight = 64;
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 16, -4.0F, -8.5F, -4.0F, 8, 8, 8, 0.0F, false));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, -8.5F, -4.0F);
				bipedHead.addChild(bone9);
				setRotationAngle(bone9, -0.5236F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 46, 32, -4.0F, -5.0F, 0.0F, 8, 5, 0, 0.0F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -8.5F, -1.5F);
				bipedHead.addChild(bone2);
				setRotationAngle(bone2, -0.6109F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 46, 32, -4.0F, -5.0F, 0.0F, 8, 5, 0, 0.0F, true));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, -7.5F, 0.5F);
				bipedHead.addChild(bone3);
				setRotationAngle(bone3, -0.6981F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 46, 32, -4.0F, -5.0F, 0.0F, 8, 5, 0, 0.0F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -6.5F, 2.0F);
				bipedHead.addChild(bone4);
				setRotationAngle(bone4, -0.7854F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 46, 32, -4.0F, -5.0F, 0.0F, 8, 5, 0, 0.0F, true));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, -4.5F, 2.0F);
				bipedHead.addChild(bone5);
				setRotationAngle(bone5, -0.8727F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 46, 32, -4.0F, -5.0F, 0.0F, 8, 5, 0, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, -2.75F, 2.0F);
				bipedHead.addChild(bone6);
				setRotationAngle(bone6, -0.9599F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 46, 32, -4.0F, -5.0F, 0.0F, 8, 5, 0, 0.0F, true));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, -1.0F, 2.0F);
				bipedHead.addChild(bone7);
				setRotationAngle(bone7, -1.0472F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 46, 32, -4.0F, -5.0F, 0.0F, 8, 5, 0, 0.0F, false));
		
				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(-3.7F, -10.7815F, -3.0149F);
				bipedHead.addChild(bone45);
				setRotationAngle(bone45, -1.0036F, 0.7854F, -1.1781F);
				
		
				HatLayer_r1 = new ModelRenderer(this);
				HatLayer_r1.setRotationPoint(-1.4091F, 0.2641F, -0.1594F);
				bone45.addChild(HatLayer_r1);
				setRotationAngle(HatLayer_r1, 0.1309F, -0.5236F, 0.0F);
				HatLayer_r1.cubeList.add(new ModelBox(HatLayer_r1, 59, 44, -0.725F, -1.775F, -0.625F, 2, 5, 0, 0.0F, false));
		
				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(3.7F, -10.7815F, -3.0149F);
				bipedHead.addChild(bone46);
				setRotationAngle(bone46, -1.0036F, -0.7854F, 1.1781F);
				
		
				HatLayer_r2 = new ModelRenderer(this);
				HatLayer_r2.setRotationPoint(1.4091F, 0.2641F, -0.1594F);
				bone46.addChild(HatLayer_r2);
				setRotationAngle(HatLayer_r2, 0.1309F, 0.5236F, 0.0F);
				HatLayer_r2.cubeList.add(new ModelBox(HatLayer_r2, 59, 44, -1.275F, -1.775F, -0.625F, 2, 5, 0, 0.0F, true));
		
				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(-4.6479F, -9.2565F, 0.5796F);
				bipedHead.addChild(bone44);
				setRotationAngle(bone44, 0.7418F, 1.309F, 0.0F);
				
		
				HatLayer_r3 = new ModelRenderer(this);
				HatLayer_r3.setRotationPoint(-0.3121F, 3.6916F, -1.7356F);
				bone44.addChild(HatLayer_r3);
				setRotationAngle(HatLayer_r3, -0.3927F, -0.3491F, -0.7854F);
				HatLayer_r3.cubeList.add(new ModelBox(HatLayer_r3, 13, 0, -4.5F, -4.5F, 0.0F, 9, 5, 0, 0.0F, false));
		
				HatLayer_r4 = new ModelRenderer(this);
				HatLayer_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone44.addChild(HatLayer_r4);
				setRotationAngle(HatLayer_r4, -0.3054F, -0.3491F, -0.7854F);
				HatLayer_r4.cubeList.add(new ModelBox(HatLayer_r4, 13, 0, -6.425F, -1.225F, -0.8F, 9, 5, 0, 0.0F, true));
		
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(4.6479F, -9.2565F, 0.5796F);
				bipedHead.addChild(bone43);
				setRotationAngle(bone43, 0.7418F, -1.309F, 0.0F);
				
		
				HatLayer_r5 = new ModelRenderer(this);
				HatLayer_r5.setRotationPoint(0.3121F, 3.6916F, -1.7356F);
				bone43.addChild(HatLayer_r5);
				setRotationAngle(HatLayer_r5, -0.3927F, 0.3491F, 0.7854F);
				HatLayer_r5.cubeList.add(new ModelBox(HatLayer_r5, 13, 0, -4.5F, -4.5F, 0.0F, 9, 5, 0, 0.0F, true));
		
				HatLayer_r6 = new ModelRenderer(this);
				HatLayer_r6.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone43.addChild(HatLayer_r6);
				setRotationAngle(HatLayer_r6, -0.3054F, 0.3491F, 0.7854F);
				HatLayer_r6.cubeList.add(new ModelBox(HatLayer_r6, 13, 0, -2.575F, -1.225F, -0.8F, 9, 5, 0, 0.0F, false));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(-1.9F, -2.2F, -4.0F);
				bipedHead.addChild(bone);
				setRotationAngle(bone, 0.2618F, 0.0F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 0, 21, -1.0F, -2.0F, 0.0F, 2, 2, 0, 0.0F, false));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(1.9F, -2.2F, -4.0F);
				bipedHead.addChild(bone8);
				setRotationAngle(bone8, 0.2618F, 0.0F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 21, -1.0F, -2.0F, 0.0F, 2, 2, 0, 0.0F, true));
		
				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(-1.65F, -6.35F, -4.0F);
				bipedHead.addChild(bone47);
				setRotationAngle(bone47, 0.1745F, 0.0F, 0.0F);
				bone47.cubeList.add(new ModelBox(bone47, 0, 18, -2.0F, -2.0F, 0.0F, 4, 3, 0, 0.0F, false));
		
				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(1.65F, -6.35F, -4.0F);
				bipedHead.addChild(bone48);
				setRotationAngle(bone48, 0.1745F, 0.0F, 0.0F);
				bone48.cubeList.add(new ModelBox(bone48, 0, 18, -2.0F, -2.0F, 0.0F, 4, 3, 0, 0.0F, true));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 42, 46, -4.025F, -6.925F, -4.005F, 8, 2, 0, 0.0F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 32, -4.0F, -0.5F, -2.0F, 8, 12, 4, 0.6F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 6, 6, -4.0F, 4.25F, -3.425F, 8, 7, 2, 0.0F, false));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				
		
				rightUpperArm = new ModelRenderer(this);
				rightUpperArm.setRotationPoint(-1.0F, 0.0F, 0.0F);
				bipedRightArm.addChild(rightUpperArm);
				setRotationAngle(rightUpperArm, -1.0472F, -0.5236F, 0.2618F);
				rightUpperArm.cubeList.add(new ModelBox(rightUpperArm, 32, 0, -3.1121F, -2.483F, -1.9353F, 4, 8, 4, 0.0F, false));
		
				rightForeArm = new ModelRenderer(this);
				rightForeArm.setRotationPoint(-1.0F, 5.0F, 0.0F);
				rightUpperArm.addChild(rightForeArm);
				setRotationAngle(rightForeArm, -0.5236F, 0.0F, -0.5236F);
				rightForeArm.cubeList.add(new ModelBox(rightForeArm, 28, 12, -2.1121F, -0.4506F, -2.1854F, 4, 8, 4, 0.0F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				
		
				leftUpperArm = new ModelRenderer(this);
				leftUpperArm.setRotationPoint(1.0F, 0.0F, 0.0F);
				bipedLeftArm.addChild(leftUpperArm);
				setRotationAngle(leftUpperArm, -1.0472F, 0.5236F, -0.2618F);
				leftUpperArm.cubeList.add(new ModelBox(leftUpperArm, 32, 0, -0.8879F, -2.483F, -1.9353F, 4, 8, 4, 0.0F, true));
		
				leftForeArm = new ModelRenderer(this);
				leftForeArm.setRotationPoint(1.0F, 5.0F, 0.0F);
				leftUpperArm.addChild(leftForeArm);
				setRotationAngle(leftForeArm, -0.5236F, 0.0F, 0.5236F);
				leftForeArm.cubeList.add(new ModelBox(leftForeArm, 28, 12, -1.8879F, -0.4506F, -2.1854F, 4, 8, 4, 0.0F, true));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				
		
				rightThigh = new ModelRenderer(this);
				rightThigh.setRotationPoint(-0.1F, -1.0F, 0.0F);
				bipedRightLeg.addChild(rightThigh);
				setRotationAngle(rightThigh, -2.0944F, 0.2618F, -1.4835F);
				rightThigh.cubeList.add(new ModelBox(rightThigh, 20, 44, -1.9F, 0.5076F, -2.0868F, 4, 6, 4, 0.4F, false));
		
				rightCalf = new ModelRenderer(this);
				rightCalf.setRotationPoint(0.0F, 7.0F, -2.2F);
				rightThigh.addChild(rightCalf);
				setRotationAngle(rightCalf, 1.309F, 0.0F, 0.0F);
				rightCalf.cubeList.add(new ModelBox(rightCalf, 44, 8, -1.9F, -0.4981F, 0.0436F, 4, 7, 4, 0.2F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				
		
				leftThigh = new ModelRenderer(this);
				leftThigh.setRotationPoint(0.1F, -1.0F, 0.0F);
				bipedLeftLeg.addChild(leftThigh);
				setRotationAngle(leftThigh, -2.0944F, -0.2618F, 1.4835F);
				leftThigh.cubeList.add(new ModelBox(leftThigh, 20, 44, -2.1F, 0.5076F, -2.0868F, 4, 6, 4, 0.4F, true));
		
				leftCalf = new ModelRenderer(this);
				leftCalf.setRotationPoint(0.0F, 7.0F, -2.2F);
				leftThigh.addChild(leftCalf);
				setRotationAngle(leftCalf, 1.309F, 0.0F, 0.0F);
				leftCalf.cubeList.add(new ModelBox(leftCalf, 44, 8, -2.1F, -0.4981F, 0.0436F, 4, 7, 4, 0.2F, true));
		
				dragon = new ModelRenderer(this);
				dragon.setRotationPoint(0.0F, 0.0F, 0.0F);
				dragon.cubeList.add(new ModelBox(dragon, 44, 19, -4.0F, 15.5F, 3.0F, 4, 4, 4, 0.0F, false));
				dragon.cubeList.add(new ModelBox(dragon, 44, 19, -4.0F, 17.875F, 3.0F, 4, 4, 4, -0.5F, false));
				dragon.cubeList.add(new ModelBox(dragon, 44, 19, -4.0F, 19.875F, 3.0F, 4, 4, 4, -1.0F, false));
				dragon.cubeList.add(new ModelBox(dragon, 44, 19, -4.05F, 21.3F, 3.0F, 4, 4, 4, -1.2F, false));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(-2.0F, 15.0F, 5.0F);
				dragon.addChild(bone10);
				setRotationAngle(bone10, 0.2618F, 0.0F, -0.2618F);
				bone10.cubeList.add(new ModelBox(bone10, 44, 19, -1.8706F, -1.4665F, -1.875F, 4, 4, 4, 0.0F, false));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone10.addChild(bone11);
				setRotationAngle(bone11, 0.2618F, 0.0F, -0.2618F);
				bone11.cubeList.add(new ModelBox(bone11, 44, 19, -1.7543F, -1.3706F, -1.7713F, 4, 4, 4, 0.0F, false));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, 0.2618F, 0.0F, -0.2618F);
				bone12.cubeList.add(new ModelBox(bone12, 44, 19, -1.6667F, -1.2251F, -1.7029F, 4, 4, 4, 0.0F, false));
		
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone12.addChild(bone13);
				setRotationAngle(bone13, 0.2182F, 0.0F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 44, 19, -1.6667F, -1.1555F, -1.6612F, 4, 4, 4, 0.0F, false));
		
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone13.addChild(bone14);
				setRotationAngle(bone14, 0.2618F, 0.0F, 0.2618F);
				bone14.cubeList.add(new ModelBox(bone14, 44, 19, -1.7183F, -1.1407F, -1.6116F, 4, 4, 4, 0.0F, false));
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone14.addChild(bone15);
				setRotationAngle(bone15, 0.0F, 0.0F, 0.3491F);
				bone15.cubeList.add(new ModelBox(bone15, 44, 19, -1.7834F, -1.2285F, -1.6116F, 4, 4, 4, 0.0F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone15.addChild(bone16);
				setRotationAngle(bone16, 0.0F, 0.0F, 0.3491F);
				bone16.cubeList.add(new ModelBox(bone16, 44, 19, -1.8747F, -1.2888F, -1.6116F, 4, 4, 4, 0.0F, false));
		
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone16.addChild(bone17);
				setRotationAngle(bone17, 0.0F, 0.0F, 0.3491F);
				bone17.cubeList.add(new ModelBox(bone17, 44, 19, -1.981F, -1.3143F, -1.6116F, 4, 4, 4, 0.0F, false));
		
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone17.addChild(bone18);
				setRotationAngle(bone18, 0.2618F, 0.0F, 0.3491F);
				bone18.cubeList.add(new ModelBox(bone18, 44, 19, -2.0896F, -1.191F, -1.5467F, 4, 4, 4, 0.0F, false));
		
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone18.addChild(bone19);
				setRotationAngle(bone19, 0.0F, 0.0F, 0.2618F);
				bone19.cubeList.add(new ModelBox(bone19, 44, 19, -2.136F, -1.1613F, -1.5467F, 4, 4, 4, 0.0F, false));
		
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone19.addChild(bone20);
				bone20.cubeList.add(new ModelBox(bone20, 44, 19, -2.136F, -1.1613F, -1.5467F, 4, 4, 4, 0.0F, false));
		
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone20.addChild(bone21);
				bone21.cubeList.add(new ModelBox(bone21, 44, 19, -2.136F, -1.1613F, -1.5467F, 4, 4, 4, 0.0F, false));
		
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone21.addChild(bone22);
				setRotationAngle(bone22, -0.2618F, 0.0F, 0.2618F);
				bone22.cubeList.add(new ModelBox(bone22, 44, 19, -2.1731F, -1.2338F, -1.5934F, 4, 4, 4, 0.0F, false));
		
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone22.addChild(bone23);
				setRotationAngle(bone23, -0.2618F, 0.0F, 0.2618F);
				bone23.cubeList.add(new ModelBox(bone23, 44, 19, -2.2278F, -1.2801F, -1.6541F, 4, 4, 4, 0.0F, false));
		
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone23.addChild(bone24);
				setRotationAngle(bone24, -0.2618F, 0.0F, 0.2618F);
				bone24.cubeList.add(new ModelBox(bone24, 44, 19, -2.2925F, -1.2939F, -1.7206F, 4, 4, 4, 0.0F, false));
		
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone24.addChild(bone25);
				setRotationAngle(bone25, -0.2618F, 0.0F, 0.0F);
				bone25.cubeList.add(new ModelBox(bone25, 44, 19, -2.2925F, -1.3562F, -1.8062F, 4, 4, 4, 0.0F, false));
		
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone25.addChild(bone26);
				setRotationAngle(bone26, 0.0F, 0.0F, 0.2618F);
				bone26.cubeList.add(new ModelBox(bone26, 44, 19, -2.3747F, -1.2684F, -1.8062F, 4, 4, 4, 0.0F, false));
		
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone26.addChild(bone27);
				setRotationAngle(bone27, 0.0F, 0.0F, 0.2618F);
				bone27.cubeList.add(new ModelBox(bone27, 44, 19, -2.4314F, -1.1623F, -1.8062F, 4, 4, 4, 0.0F, false));
		
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone27.addChild(bone28);
				setRotationAngle(bone28, 0.0F, 0.0F, 0.2618F);
				bone28.cubeList.add(new ModelBox(bone28, 44, 19, -2.4587F, -1.0451F, -1.8062F, 4, 4, 4, 0.0F, false));
		
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone28.addChild(bone29);
				setRotationAngle(bone29, -0.2618F, 0.0F, -0.0873F);
				bone29.cubeList.add(new ModelBox(bone29, 44, 19, -2.453F, -1.1321F, -1.8348F, 4, 4, 4, 0.0F, false));
		
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone29.addChild(bone30);
				setRotationAngle(bone30, -0.2618F, 0.0F, 0.1745F);
				bone30.cubeList.add(new ModelBox(bone30, 44, 19, -2.4691F, -1.0925F, -1.8537F, 4, 4, 4, 0.0F, false));
		
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone30.addChild(bone31);
				setRotationAngle(bone31, -0.2618F, 0.0F, 0.1745F);
				bone31.cubeList.add(new ModelBox(bone31, 44, 19, -2.478F, -1.0471F, -1.8612F, 4, 4, 4, 0.0F, false));
		
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone31.addChild(bone32);
				setRotationAngle(bone32, -0.2618F, 0.0F, 0.1745F);
				bone32.cubeList.add(new ModelBox(bone32, 44, 19, -2.479F, -1.0006F, -1.8565F, 4, 4, 4, 0.0F, false));
		
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone32.addChild(bone33);
				setRotationAngle(bone33, -0.2618F, -0.1745F, 0.1745F);
				bone33.cubeList.add(new ModelBox(bone33, 44, 19, -2.4718F, -0.9573F, -1.84F, 4, 4, 4, 0.0F, false));
		
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone33.addChild(bone34);
				setRotationAngle(bone34, -0.2618F, 0.0F, 0.0F);
				bone34.cubeList.add(new ModelBox(bone34, 44, 19, -2.4718F, -1.0002F, -1.8344F, 4, 4, 4, 0.0F, false));
		
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone34.addChild(bone35);
				setRotationAngle(bone35, -0.2618F, 0.0F, 0.0F);
				bone35.cubeList.add(new ModelBox(bone35, 44, 19, -2.4718F, -1.0431F, -1.8401F, 4, 4, 4, 0.0F, false));
		
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone35.addChild(bone36);
				setRotationAngle(bone36, 0.0F, 0.0F, 0.2618F);
				bone36.cubeList.add(new ModelBox(bone36, 44, 19, -2.4668F, -0.9195F, -1.8401F, 4, 4, 4, 0.0F, false));
		
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone36.addChild(bone37);
				setRotationAngle(bone37, -0.2618F, 0.0F, 0.2618F);
				bone37.cubeList.add(new ModelBox(bone37, 44, 19, -2.4301F, -0.8496F, -1.7941F, 4, 4, 4, 0.0F, false));
		
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone37.addChild(bone38);
				setRotationAngle(bone38, -0.2618F, 0.0F, 0.2618F);
				bone38.cubeList.add(new ModelBox(bone38, 44, 19, -2.3765F, -0.8054F, -1.7347F, 4, 4, 4, 0.0F, false));
		
				dragonHead = new ModelRenderer(this);
				dragonHead.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone38.addChild(dragonHead);
				setRotationAngle(dragonHead, -0.2618F, 0.0F, 0.2618F);
				dragonHead.cubeList.add(new ModelBox(dragonHead, 48, 0, -2.3133F, -0.793F, -1.6699F, 4, 4, 4, -0.2F, false));
		
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(-1.6789F, 0.6243F, -0.55F);
				dragonHead.addChild(bone39);
				setRotationAngle(bone39, 0.3491F, 0.0F, -2.0944F);
				bone39.cubeList.add(new ModelBox(bone39, 0, 0, -0.5F, -2.0F, -0.5F, 1, 2, 1, 0.0F, false));
		
				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(-1.6789F, 0.5243F, 1.25F);
				dragonHead.addChild(bone50);
				setRotationAngle(bone50, -0.3491F, 0.0F, -2.0944F);
				bone50.cubeList.add(new ModelBox(bone50, 0, 0, -0.5F, -2.0F, -0.5F, 1, 2, 1, 0.0F, false));
		
				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(-1.3605F, 1.6264F, -0.1965F);
				dragonHead.addChild(bone49);
				setRotationAngle(bone49, 0.1745F, 0.0F, -2.3562F);
				bone49.cubeList.add(new ModelBox(bone49, 0, 4, -0.5F, -3.0F, -0.5F, 1, 3, 1, 0.0F, false));
		
				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(-1.3605F, 1.6264F, 0.9035F);
				dragonHead.addChild(bone51);
				setRotationAngle(bone51, -0.1745F, 0.0F, -2.3562F);
				bone51.cubeList.add(new ModelBox(bone51, 0, 4, -0.5F, -3.0F, -0.5F, 1, 3, 1, 0.0F, false));
		
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(0.0F, -2.0F, 0.0F);
				dragonHead.addChild(bone40);
				bone40.cubeList.add(new ModelBox(bone40, 0, 48, -2.3133F, -0.793F, -1.6699F, 4, 4, 4, -0.4F, false));
		
				dragonEyes = new ModelRenderer(this);
				dragonEyes.setRotationPoint(2.0F, -1.0F, -8.0F);
				bone40.addChild(dragonEyes);
				dragonEyes.cubeList.add(new ModelBox(dragonEyes, 42, 51, -4.3133F, 0.207F, 6.25F, 4, 4, 1, -0.4F, false));
				dragonEyes.cubeList.add(new ModelBox(dragonEyes, 42, 57, -4.3133F, 0.207F, 9.4F, 4, 4, 1, -0.4F, true));
		
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone40.addChild(bone41);
				bone41.cubeList.add(new ModelBox(bone41, 0, 56, -2.3133F, -0.793F, -1.6699F, 4, 4, 4, -0.6F, false));
		
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.0F, -2.0F, 0.0F);
				bone41.addChild(bone42);
				bone42.cubeList.add(new ModelBox(bone42, 16, 56, -2.3133F, -0.793F, -1.6699F, 4, 4, 4, -0.8F, false));
				bone42.cubeList.add(new ModelBox(bone42, 0, 9, 0.8098F, 0.0018F, -0.2068F, 1, 1, 1, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float ageInTicks, float f3, float f4, float f5) {
				//GlStateManager.pushMatrix();
				float f6 = 1.5F * MODELSCALE;
				GlStateManager.translate(0.0F, 1.5F - f6 * ((EC)entity).getGrowth(ageInTicks), 0.0F);
				GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
				super.render(entity, f, f1, ageInTicks, f3, f4, f5);
				//dragon.render(f5);
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				bipedHeadwear.render(f5);
				//if (dragon.showModel) {
				//	dragonEyes.render(f5);
				//}
				GlStateManager.enableLighting();
				//GlStateManager.popMatrix();
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setVisible(boolean visible) {
				super.setVisible(visible);
				dragon.showModel = visible;
			}
	
			@Override
			public void setRotationAngles(float limbSwing, float f1, float f2, float f3, float f4, float f5, Entity entityIn) {
				super.setRotationAngles(limbSwing * 2.0F / entityIn.height, f1, f2, f3, f4, f5, entityIn);
				this.poseSitting(entityIn.isRiding());
			}
	
			private void poseSitting(boolean isSitting) {
				if (isSitting) {
					setRotationAngle(bipedRightArm, 0.0F, 0.0F, 0.0F);
					setRotationAngle(rightUpperArm, -1.0472F, -0.5236F, 0.2618F);
					setRotationAngle(rightForeArm, -0.5236F, 0.0F, -0.5236F);
					setRotationAngle(bipedLeftArm, 0.0F, 0.0F, 0.0F);
					setRotationAngle(leftUpperArm, -1.0472F, 0.5236F, -0.2618F);
					setRotationAngle(leftForeArm, -0.5236F, 0.0F, 0.5236F);
					setRotationAngle(rightThigh, -2.0944F, 0.2618F, -1.4835F);
					setRotationAngle(rightCalf, 1.309F, 0.0F, 0.0F);
					setRotationAngle(leftThigh, -2.0944F, -0.2618F, 1.4835F);
					setRotationAngle(leftCalf, 1.309F, 0.0F, 0.0F);
				} else {
					setRotationAngle(rightUpperArm, 0.0F, -0.5236F, 0.2618F);
					setRotationAngle(rightForeArm, -0.5236F, 0.0F, 0.0F);
					setRotationAngle(leftUpperArm, 0.0F, 0.5236F, -0.2618F);
					setRotationAngle(leftForeArm, -0.5236F, 0.0F, 0.0F);
					setRotationAngle(rightThigh, -0.1745F, 0.3491F, 0.0F);
					setRotationAngle(rightCalf, 0.2618F, 0.0F, 0.0F);
					setRotationAngle(leftThigh, -0.1745F, -0.3491F, 0.0F);
					setRotationAngle(leftCalf, 0.2618F, 0.0F, 0.0F);
				}
			}
		}
	}
}
