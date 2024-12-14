
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemShoton;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityCrystalArmor extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 474;
	public static final int ENTITYID_RANGED = 475;

	public EntityCrystalArmor(ElementsNarutomodMod instance) {
		super(instance, 902);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "crystal_armor"), ENTITYID).name("crystal_armor").tracker(64, 3, true).build());
	}

	public static class EC extends EntityShieldBase implements ItemJutsu.IJutsu {
		protected static final String ENTITYID_KEY = "CrystalArmorEntityId";
		private int strengthAmplifier = 9;

		public EC(World world) {
			super(world);
			this.setSize(0.7f, 1.9f);
			this.dieOnNoPassengers = false;
		}

		public EC(EntityLivingBase userIn) {
			this(userIn.world);
			this.setSummoner(userIn);
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Math.max(userIn.getMaxHealth() * 0.5f, 20.0f));
			this.setHealth(this.getMaxHealth());
			userIn.getEntityData().setInteger(ENTITYID_KEY, this.getEntityId());
			if (userIn.isPotionActive(MobEffects.STRENGTH)) {
				this.strengthAmplifier += userIn.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier() + 1;
			}
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.SHOTON;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.0D);
		}

		@Override
		public boolean processInitialInteract(EntityPlayer entity, EnumHand hand) {
			return false;
		}
	
		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase user = this.getSummoner();
				if (user != null) {
					user.getEntityData().removeTag(ENTITYID_KEY);
				}
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ice_shoot_small")),
				 0.8f, this.rand.nextFloat() * 0.4f + 0.9f);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			EntityLivingBase user = this.getSummoner();
			if (user != null) {
				this.setPosition(user.posX, user.posY, user.posZ);
				if (this.ticksExisted % 20 == 19) {
					if (!Chakra.pathway(user).consume(ItemShoton.ARMOR.chakraUsage)) {
						this.setDead();
					} else {
						user.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 22, this.strengthAmplifier, false, false));
					}
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		@Override
		public boolean canBeCollidedWith() {
			return false;
		}

		@Override
		public boolean canBePushed() {
			return false;
		}

		@Override
		protected void collideWithNearbyEntities() {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			 	Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ENTITYID_KEY));
				if (!(entity1 instanceof EC)) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
					 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ice_shoot_small")),
					 SoundCategory.PLAYERS, 0.8f, entity.getRNG().nextFloat() * 0.4f + 0.6f);
					entity.world.spawnEntity(new EC(entity));
					return true;
				} else {
					entity1.setDead();
				}
				return false;
			}
		}

		public static class PlayerEventHook {
			@SubscribeEvent
			public void onHurt(LivingHurtEvent event) {
				EntityLivingBase entity = event.getEntityLiving();
				if (!entity.world.isRemote && entity.getEntityData().hasKey(ENTITYID_KEY)) {
				 	Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ENTITYID_KEY));
					if (entity1 instanceof EC) {
						entity1.playSound(SoundEvents.BLOCK_GLASS_BREAK, 0.8f, entity.getRNG().nextFloat() * 0.4f + 0.9f);
						entity1.attackEntityFrom(event.getSource(), event.getAmount() * 0.8f);
						event.setAmount(event.getAmount() * 0.2f);
					}
				}
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EC.PlayerEventHook());
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
		public class RenderCustom extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/crystal_blade.png");
			private final ModelBiped model = new ModelCrystalArmor();
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float pt) {
				EntityLivingBase user = entity.getSummoner();
				if (user != null) {
					RenderLivingBase userRenderer = (RenderLivingBase)this.renderManager.getEntityRenderObject(user);
					float f = (float)entity.ticksExisted + pt;
		            float f1 = ProcedureUtils.interpolateRotation(user.prevRenderYawOffset, user.renderYawOffset, pt);
		            float f2 = ProcedureUtils.interpolateRotation(user.prevRotationYawHead, user.rotationYawHead, pt);
		            float f3 = f2 - f1;
	                float f5 = user.prevLimbSwingAmount + (user.limbSwingAmount - user.prevLimbSwingAmount) * pt;
	                float f6 = user.limbSwing - user.limbSwingAmount * (1.0F - pt);
		            float f7 = user.prevRotationPitch + (user.rotationPitch - user.prevRotationPitch) * pt;
					x = user.lastTickPosX + (user.posX - user.lastTickPosX) * pt - this.renderManager.viewerPosX;
					y = user.lastTickPosY + (user.posY - user.lastTickPosY) * pt - this.renderManager.viewerPosY;
					z = user.lastTickPosZ + (user.posZ - user.lastTickPosZ) * pt - this.renderManager.viewerPosZ;
					this.bindEntityTexture(entity);
					if (!user.equals(this.renderManager.renderViewEntity) || this.renderManager.options.thirdPersonView != 0) {
						if (user.isSneaking()) {
							y -= 0.125F;
						}
						GlStateManager.pushMatrix();
						GlStateManager.translate(x, y, z);
						float f4 = userRenderer.prepareScale(user, pt);
						//GlStateManager.scale(1.1F, 1.1F, 1.1F);
						GlStateManager.rotate(f1 - 180F, 0.0F, 1.0F, 0.0F);
						//GlStateManager.rotate(180F, 1.0F, 0.0F, 0.0F);
						GlStateManager.disableCull();
						GlStateManager.enableBlend();
						GlStateManager.disableLighting();
						GlStateManager.color(1.0F, 1.0F, 1.0F, Math.min(f * 0.035F, 0.7F));
						GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
						this.renderModel(userRenderer.getMainModel(), f6, f5, f, f3, f7, f4, user);
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			            GlStateManager.enableLighting();
			            GlStateManager.disableBlend();
			            GlStateManager.enableCull();
			            GlStateManager.popMatrix();
					}
				}
			}
	
			private void renderModel(ModelBase modelIn, float f0, float f1, float f2, float f3, float f4, float f5, Entity entityIn) {
				if (modelIn instanceof ModelBiped) {
					ModelBiped userModel = (ModelBiped)modelIn;
					ModelBase.copyModelAngles(userModel.bipedHead, this.model.bipedHead);
					ModelBase.copyModelAngles(userModel.bipedBody, this.model.bipedBody);
					ModelBase.copyModelAngles(userModel.bipedRightArm, this.model.bipedRightArm);
					ModelBase.copyModelAngles(userModel.bipedLeftArm, this.model.bipedLeftArm);
					ModelBase.copyModelAngles(userModel.bipedRightLeg, this.model.bipedRightLeg);
					ModelBase.copyModelAngles(userModel.bipedLeftLeg, this.model.bipedLeftLeg);
			        GlStateManager.pushMatrix();
		            if (entityIn.isSneaking()) {
		                GlStateManager.translate(0.0F, 0.2F, 0.0F);
		            }
		            this.model.bipedHead.render(f5);
		            this.model.bipedBody.render(f5);
		            this.model.bipedRightArm.render(f5);
		            this.model.bipedLeftArm.render(f5);
		            this.model.bipedRightLeg.render(f5);
		            this.model.bipedLeftLeg.render(f5);
		        	GlStateManager.popMatrix();
				} else {
					modelIn.setRotationAngles(f0, f1, f2, f3, f4, f5, entityIn);
					modelIn.render(entityIn, f0, f1, f2, f3, f4, f5);
				}
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelCrystalArmor extends ModelBiped {
			private final ModelRenderer blade;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer bone;
			private final ModelRenderer bone16;
			private final ModelRenderer bone15;
			private final ModelRenderer bone17;
			private final ModelRenderer bone18;
		
			public ModelCrystalArmor() {
				textureWidth = 64;
				textureHeight = 64;
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F, false));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F, false));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));
		
				blade = new ModelRenderer(this);
				blade.setRotationPoint(-4.5F, 4.0F, 0.0F);
				bipedRightArm.addChild(blade);
				blade.cubeList.add(new ModelBox(blade, 0, 32, 1.5F, -6.0F, -2.0F, 4, 12, 4, 0.55F, false));
				blade.cubeList.add(new ModelBox(blade, 3, 50, -1.0F, -2.0F, -0.5F, 2, 0, 1, 0.0F, false));
				blade.cubeList.add(new ModelBox(blade, 4, 55, 1.0F, -2.0F, -0.5F, 0, 6, 1, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(1.0F, 1.0F, 0.5F);
				blade.addChild(bone3);
				setRotationAngle(bone3, 0.0F, -0.2618F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 56, -2.0F, -3.0F, 0.0F, 2, 6, 0, 0.0F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(1.0F, 1.0F, -0.5F);
				blade.addChild(bone4);
				setRotationAngle(bone4, 0.0F, 0.2618F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 56, -2.0F, -3.0F, 0.0F, 2, 6, 0, 0.0F, false));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-1.0F, 4.0F, 0.0F);
				blade.addChild(bone5);
				setRotationAngle(bone5, 0.0F, 0.0F, -0.0873F);
				bone5.cubeList.add(new ModelBox(bone5, 4, 55, 2.0F, 0.0F, -0.5F, 0, 4, 1, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(2.0F, 1.0F, 0.5F);
				bone5.addChild(bone6);
				setRotationAngle(bone6, 0.0F, -0.2618F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 56, -2.0F, -1.0F, 0.0F, 2, 4, 0, 0.0F, false));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(2.0F, 1.0F, -0.5F);
				bone5.addChild(bone7);
				setRotationAngle(bone7, 0.0F, 0.2618F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 56, -2.0F, -1.0F, 0.0F, 2, 4, 0, 0.0F, false));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, 4.0F, 0.0F);
				bone5.addChild(bone8);
				
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(2.0F, 0.0F, 0.0F);
				bone8.addChild(bone);
				setRotationAngle(bone, 0.0F, 0.0F, -0.0218F);
				bone.cubeList.add(new ModelBox(bone, 4, 50, 0.0F, 0.0F, -0.5F, 0, 5, 1, 0.0F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.05F, 0.0F, 0.0F);
				bone8.addChild(bone16);
				setRotationAngle(bone16, 0.1047F, 0.0F, -0.0262F);
				
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone16.addChild(bone15);
				setRotationAngle(bone15, 0.0F, 0.2618F, 0.0F);
				bone15.cubeList.add(new ModelBox(bone15, 0, 50, 0.0F, 0.0F, 0.0F, 2, 6, 0, 0.0F, false));
		
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.05F, 0.0F, 0.0F);
				bone8.addChild(bone17);
				setRotationAngle(bone17, -0.1047F, 0.0F, -0.0262F);
				
		
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone17.addChild(bone18);
				setRotationAngle(bone18, 0.0F, -0.2618F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 0, 50, 0.0F, 0.0F, 0.0F, 2, 6, 0, 0.0F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, true));
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
