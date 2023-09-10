package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.Minecraft;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.RenderLivingBase;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPretaShield extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 44;
	public static final int ENTITYID_RANGED = 45;
	
	public EntityPretaShield(ElementsNarutomodMod instance) {
		super(instance, 238);
	}

	public void initElements() {
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "pretashieldentity"), ENTITYID).name("pretashieldentity").tracker(64, 1, true).build());
	}

	public static class EntityCustom extends EntityShieldBase {
		public EntityCustom(World world) {
			super(world);
			this.setSize(1.2F, 2.2F);
			this.setOwnerCanSteer(true, 0.1F);
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(1.2F, 2.2F);
			this.setOwnerCanSteer(true, 0.1F);
		}

		private void weakenEntity(EntityLivingBase entity, float amount) {
			int duration = 200;
			int amplifier = (int) Math.ceil((amount / 4.0F));
			if (entity.isPotionActive(MobEffects.WEAKNESS) && amount > 0.1F) {
				PotionEffect effect = entity.getActivePotionEffect(MobEffects.WEAKNESS);
				amplifier += effect.getAmplifier();
				duration = effect.getDuration() + 60;
			}
			entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, duration, amplifier));
			entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 4));
			entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 4));
			EntityLivingBase summoner = this.getSummoner();
			summoner.heal(amount / 2.0F);
			if (summoner instanceof EntityPlayer) {
				((EntityPlayer)summoner).sendStatusMessage(new TextComponentString(amount
				  + " damage from " + entity.getDisplayName().getFormattedText()
				  + " absorbed, weakening it by " + entity.getActivePotionEffect(MobEffects.WEAKNESS).getAmplifier() + " for "
				  + (entity.getActivePotionEffect(MobEffects.WEAKNESS).getDuration() / 20) + " secs"), true);
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			//if (source.getDamageType().equals(ItemJutsu.JUTSU_TYPE)) {
			if (ItemJutsu.isDamageSourceNinjutsu(source)) {
				if (!this.world.isRemote) {
					if (source.getTrueSource() instanceof EntityLivingBase) {
						this.weakenEntity((EntityLivingBase)source.getTrueSource(), amount);
					}
					if (source.getImmediateSource() != null && !(source.getImmediateSource() instanceof EntityLivingBase)) {
						source.getImmediateSource().setDead();
					}
					EntityLivingBase summoner = this.getSummoner();
					if (summoner != null && amount > 0f) {
						Chakra.pathway(summoner).consume((double)-amount, true);
					}
				}
				return false;
			} else {
				if (this.absorbEntityChakra(source.getTrueSource(), amount)) {
					return false;
				}
				return super.attackEntityFrom(source, amount);
			}
		}

		/*@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("shield.health", 1024.0D, 0));
		}*/

		@Override
		public void onDeathUpdate() {
			this.setDead();
		}

		@Override
		public void onLivingUpdate() {
			super.onLivingUpdate();
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner != null) {
					ItemStack stack = summoner.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
					if (stack.getItem() != ItemRinnegan.helmet && stack.getItem() != ItemTenseigan.helmet) {
						this.setDead();
					}
				} else {
					this.setDead();
				}
			}
		}

		private boolean absorbEntityChakra(Entity entity, float amount) {
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				if (entity instanceof EntitySusanooBase) {
					//float amount = 10f;
					entity.attackEntityFrom(DamageSource.MAGIC, amount);
					summoner.heal(amount / 2.0F);
					return true;
				}
				if (entity instanceof EntityLivingBase && summoner != null) {
					Chakra.Pathway chakra = Chakra.pathway((EntityLivingBase)entity);
					if (chakra.getAmount() > 0.0d) {
						double d = Math.min(chakra.getAmount(), amount);
						chakra.consume(d);
						this.weakenEntity((EntityLivingBase)entity, (float)d);
						Chakra.pathway(summoner).consume(-d, true);
						return true;
					}
				}
				/*if (entity instanceof EntityNinjaMob.Base) {
					this.weakenEntity((EntityLivingBase)entity, amount);
					((EntityNinjaMob.Base)entity).consumeChakra((double)amount);
					if (this.getSummoner() instanceof EntityPlayer && !this.world.isRemote) {
						Chakra.pathway((EntityPlayer)this.getSummoner()).consume((double)-amount, true);
					}
				}
				if (entity instanceof EntityPlayer 
				 && PlayerTracker.isNinja((EntityPlayer)entity) && ((EntityPlayer)entity).experienceLevel >= 10) {
					this.weakenEntity((EntityLivingBase)entity, amount);
					if (!this.world.isRemote) {
						Chakra.pathway((EntityPlayer)entity).consume((double)amount);
						if (this.getSummoner() instanceof EntityPlayer) {
							Chakra.pathway((EntityPlayer)this.getSummoner()).consume((double)-amount, true);
						}
					}
				}*/
			}
			return false;
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			if (!entity.equals(this.getSummoner())) {
				this.absorbEntityChakra(entity, 20f);
				//super.collideWithEntity(entity);
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends RenderLivingBase<EntityCustom> {
			private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/electric_armor.png");
			private final DynamicTexture TEXTURE_BRIGHTNESS = new DynamicTexture(16, 16);
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelPretaShield(), 0.1F);
		        int[] aint = TEXTURE_BRIGHTNESS.getTextureData();
		        for (int i = 0; i < 256; ++i) {
		            aint[i] = -1;
		        }
		        TEXTURE_BRIGHTNESS.updateDynamicTexture();
			}
	
			@Override
		    protected boolean setBrightness(EntityCustom entitylivingbaseIn, float partialTicks, boolean combineTextures) {
		        float f = entitylivingbaseIn.getBrightness();
		        int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
		        boolean flag = (i >> 24 & 255) > 0;
		        boolean flag1 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
		        if (!flag && !flag1) {
		            return false;
		        } else if (!flag && !combineTextures) {
		            return false;
		        } else {
		            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		            GlStateManager.enableTexture2D();
		            GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
		            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		            GlStateManager.enableTexture2D();
		            GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
		            this.brightnessBuffer.position(0);
		            if (flag1) {
		                this.brightnessBuffer.put(0.6F);
		                this.brightnessBuffer.put(0.6F);
		                this.brightnessBuffer.put(0.6F);
		                this.brightnessBuffer.put(0.6F);
		            } else {
		                float f1 = (float)(i >> 24 & 255) / 255.0F;
		                float f2 = (float)(i >> 16 & 255) / 255.0F;
		                float f3 = (float)(i >> 8 & 255) / 255.0F;
		                float f4 = (float)(i & 255) / 255.0F;
		                this.brightnessBuffer.put(f2);
		                this.brightnessBuffer.put(f3);
		                this.brightnessBuffer.put(f4);
		                this.brightnessBuffer.put(1.0F - f1);
		            }
		            this.brightnessBuffer.flip();
		            GlStateManager.glTexEnv(8960, 8705, this.brightnessBuffer);
		            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
		            GlStateManager.enableTexture2D();
		            GlStateManager.bindTexture(TEXTURE_BRIGHTNESS.getGlTextureId());
		            GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
		            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
		            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		            return true;
		        }
		    }
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return TEXTURE;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelPretaShield extends ModelBase {
			private final ModelRenderer bone;
	
			public ModelPretaShield() {
				this.textureWidth = 32;
				this.textureHeight = 32;
				this.bone = new ModelRenderer(this);
				this.bone.setRotationPoint(0.0F, 24.0F, 0.0F);
				this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -8.0F, -33.0F, -8.0F, 16, 32, 16, 2.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.depthMask(true);
				if (entity.ticksExisted < 30) {
					float scale = entity.ticksExisted / 30.0F;
					GlStateManager.scale(scale, scale, scale);
				}
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				float f = entity.ticksExisted;
				GlStateManager.translate(f * 0.04F, f * 0.02F, 0.0F);
				GlStateManager.matrixMode(5888);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.2F);
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				//GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				//(Minecraft.getMinecraft()).entityRenderer.setupFogColor(true);
				this.bone.render(f5);
				//(Minecraft.getMinecraft()).entityRenderer.setupFogColor(false);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.disableAlpha();
				GlStateManager.depthMask(false);
				GlStateManager.popMatrix();
			}
		}
	}
}
