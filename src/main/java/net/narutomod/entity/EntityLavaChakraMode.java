
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.init.SoundEvents;
import net.minecraft.init.MobEffects;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.potion.PotionEffect;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemYooton;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityLavaChakraMode extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 274;
	public static final int ENTITYID_RANGED = 275;

	public EntityLavaChakraMode(ElementsNarutomodMod instance) {
		super(instance, 594);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "lava_chakra_mode"), ENTITYID).name("lava_chakra_mode").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> {
			return new RenderCustom(renderManager);
		});
	}

	public static class EC extends Entity {
		private static final DataParameter<Integer> USERID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		protected static final String LCMEntityIdKey = "LavaChakraModeEntityId";
		private int strengthAmplifier = 9;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase userIn) {
			this(userIn.world);
			this.setUser(userIn);
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
			userIn.getEntityData().setInteger(LCMEntityIdKey, this.getEntityId());
			if (userIn.isPotionActive(MobEffects.STRENGTH)) {
				this.strengthAmplifier += userIn.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier() + 1;
			}
		}

		@Override
		protected void entityInit() {
			this.dataManager.register(USERID, Integer.valueOf(-1));
		}

		private void setUser(EntityLivingBase user) {
			this.getDataManager().set(USERID, Integer.valueOf(user.getEntityId()));
		}

		@Nullable
		private EntityLivingBase getUser() {
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(USERID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase user = this.getUser();
				if (user != null) {
					user.getEntityData().removeTag(LCMEntityIdKey);
				}
			}
		}

		@Override
		public void onUpdate() {
			EntityLivingBase user = this.getUser();
			if (user != null) {
				this.setPosition(user.posX, user.posY, user.posZ);
				if (this.ticksExisted % 20 == 19) {
					if (!Chakra.pathway(user).consume(ItemYooton.CHAKRAMODE.chakraUsage)) {
						this.setDead();
					} else {
						user.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 21, this.strengthAmplifier, false, false));
						user.addPotionEffect(new PotionEffect(MobEffects.SPEED, 21, 16, false, false));
					}
				}
				if (this.rand.nextInt(20) == 0) {
					this.playSound(SoundEvents.BLOCK_LAVA_AMBIENT, 0.5f, this.rand.nextFloat() * 0.6f + 0.6f);
				}
				if (this.world instanceof WorldServer && this.rand.nextInt(10) == 0) {
					((WorldServer)this.world).spawnParticle(EnumParticleTypes.LAVA, user.posX, 
					 user.posY + user.height/2, user.posZ, 1, user.width * 0.5, user.height * 0.5,
					 user.width * 0.5, 0d);
				}
				for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, 
				 user.getEntityBoundingBox().grow(6d))) {
				 	if (!entity.equals(user)) {
						entity.attackEntityFrom(DamageSource.LAVA, 4.0F);
						entity.setFire(15);
				 	}
				}
			}
			if (!this.world.isRemote && (user == null || !user.isEntityAlive() )) {
			 //|| (user instanceof EntityPlayer && EntityBijuManager.cloakLevel((EntityPlayer)user) != 1))) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				//if (entity instanceof EntityPlayer && EntityBijuManager.getTails((EntityPlayer)entity) == 4) {
				 	Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(LCMEntityIdKey));
					if (!(entity1 instanceof EC)) {
						entity.world.spawnEntity(new EC(entity));
						return true;
					} else {
						entity1.setDead();
					}
				//}
				return false;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EC> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/lavacloak1.png");

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}

		@Override
		public void doRender(EC entity, double x, double y, double z, float entityYaw, float pt) {
			EntityLivingBase user = entity.getUser();
			if (user != null) {
				RenderLivingBase userRenderer = (RenderLivingBase)this.renderManager.getEntityRenderObject(user);
				ModelBase model = userRenderer.getMainModel();
				float f = (float)user.ticksExisted + pt;
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
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
					GlStateManager.matrixMode(5888);
					//GlStateManager.disableDepth();
					GlStateManager.enableBlend();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 0.6F);
					GlStateManager.disableLighting();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
					this.renderModel(model, f6, f5, f, f3, f7, f4, user);
		            GlStateManager.matrixMode(5890);
		            GlStateManager.loadIdentity();
		            GlStateManager.matrixMode(5888);
		            GlStateManager.enableLighting();
		            GlStateManager.disableBlend();
		            //GlStateManager.enableDepth();
		            GlStateManager.popMatrix();
				}
			}
		}

		private void renderModel(ModelBase modelIn, float f0, float f1, float f2, float f3, float f4, float f5, Entity entityIn) {
			if (modelIn instanceof ModelBiped) {
				ModelBiped model = (ModelBiped)modelIn;
		        GlStateManager.pushMatrix();
		        if (model.isChild) {
		            float f = 2.0F;
		            GlStateManager.scale(0.75F, 0.75F, 0.75F);
		            GlStateManager.translate(0.0F, 16.0F * f5, 0.0F);
		            model.bipedHead.render(f5);
		            GlStateManager.popMatrix();
		            GlStateManager.pushMatrix();
		            GlStateManager.scale(0.5F, 0.5F, 0.5F);
		            GlStateManager.translate(0.0F, 24.0F * f5, 0.0F);
		            model.bipedBody.render(f5);
		            model.bipedRightArm.render(f5);
		            model.bipedLeftArm.render(f5);
		            model.bipedRightLeg.render(f5);
		            model.bipedLeftLeg.render(f5);
		            model.bipedHeadwear.render(f5);
		        } else {
		            if (entityIn.isSneaking()) {
		                GlStateManager.translate(0.0F, 0.2F, 0.0F);
		            }
		            model.bipedHead.render(f5);
		            model.bipedBody.render(f5);
		            model.bipedRightArm.render(f5);
		            model.bipedLeftArm.render(f5);
		            model.bipedRightLeg.render(f5);
		            model.bipedLeftLeg.render(f5);
		            model.bipedHeadwear.render(f5);
		        }
		        GlStateManager.popMatrix();
			} else {
				modelIn.setRotationAngles(f0, f1, f2, f3, f4, f5, entityIn);
				modelIn.render(entityIn, f0, f1, f2, f3, f4, f5);
			}
		}

		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return texture;
		}
	}
}
