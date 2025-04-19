package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketAnimation;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemSenjutsu;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.potion.PotionCorrosion;
import net.narutomod.potion.PotionInstantDamage;
import net.narutomod.potion.PotionParalysis;
import net.narutomod.procedure.ProcedureUtils;

import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPretaShield extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 44;
	public static final int ENTITYID_RANGED = 45;
	
	public EntityPretaShield(ElementsNarutomodMod instance) {
		super(instance, 238);
	}

	public void initElements() {
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "pretashield"), ENTITYID).name("pretashield").tracker(64, 1, true).build());
	}

	public static class EntityCustom extends EntityShieldBase {
		private int lifeSpan = Integer.MAX_VALUE - 1;
		private double absorbedSageChakra;
		private final double sageChakraLimit = 1000.0d;
		
		public EntityCustom(World world) {
			super(world);
			this.setSize(2.0F, 2.2F);
			this.setOwnerCanSteer(true, 0.1F);
			this.effectivePotions.addAll(Lists.newArrayList(PotionAmaterasuFlame.potion, PotionCorrosion.potion, PotionInstantDamage.potion));
		}

		public EntityCustom(EntityLivingBase player) {
			super(player);
			this.setSize(2.0F, 2.2F);
			this.setOwnerCanSteer(true, 0.1F);
			this.effectivePotions.addAll(Lists.newArrayList(PotionAmaterasuFlame.potion, PotionCorrosion.potion, PotionInstantDamage.potion));
		}

		@Override
		public boolean shouldRiderBeStill() {
			return false;
		}

		public void setLifeSpan(int i) {
			this.lifeSpan = i;
		}

		private void weakenEntity(EntityLivingBase entity, float amount) {
			PotionEffect effect = entity.getActivePotionEffect(MobEffects.MINING_FATIGUE);
			if (effect == null || effect.getAmplifier() < 4) {
				entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 4));
			}
			effect = entity.getActivePotionEffect(MobEffects.SLOWNESS);
			if (effect == null || effect.getAmplifier() < 4) {
				entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 4));
			}
			effect = entity.getActivePotionEffect(MobEffects.WEAKNESS);
			int amplifier = (int)Math.ceil(amount * 0.25F) + (effect != null ? effect.getAmplifier() : 0);
			if (effect == null || effect.getAmplifier() < amplifier) {
				entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, amplifier));
			}
			/*EntityLivingBase summoner = this.getSummoner();
			summoner.heal(amount / 2.0F);
			if (summoner instanceof EntityPlayer) {
				((EntityPlayer)summoner).sendStatusMessage(new TextComponentString(amount
				  + " damage from " + entity.getDisplayName().getFormattedText()
				  + " absorbed, weakening it by " + entity.getActivePotionEffect(MobEffects.WEAKNESS).getAmplifier() + " for "
				  + (entity.getActivePotionEffect(MobEffects.WEAKNESS).getDuration() / 20) + " secs"), true);
			}*/
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getImmediateSource() != null && this.absorbEntityChakra(source.getImmediateSource(), amount)) {
				if (this.world instanceof WorldServer) {
					((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketAnimation(this, 1));
				}
				return false;
			}
			if (ItemJutsu.isDamageSourceNinjutsu(source)) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner != null) {
					Chakra.pathway(summoner).consume((double)-amount * 5, true);
					if (source instanceof ProcedureUtils.JutsuEffectDamageSource) {
						this.removePotionEffect(((ProcedureUtils.JutsuEffectDamageSource)source).getPotion());
					}
					if (this.world instanceof WorldServer) {
						((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketAnimation(this, 1));
					}
					return false;
				}
			}
			return super.attackEntityFrom(source, amount);
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
			if (this.ticksExisted == 1) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kamui")), 0.6f, 1f);
			}
			super.onLivingUpdate();
			if (!this.world.isRemote) {
				if (this.ticksExisted > this.lifeSpan || this.getSummoner() == null) {
					this.setDead();
				}
			}
		}

		private boolean absorbEntityChakra(Entity entity, float amount) {
			boolean ret = false;
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner != null) {
					if (entity instanceof ItemJutsu.IJutsu) {
						entity.setDead();
						ret = true;
					} else if (entity instanceof EntitySusanooBase) {
						entity.attackEntityFrom(DamageSource.MAGIC, amount);
						ret = true;
					} else if (entity instanceof EntityLivingBase) {
						Chakra.Pathway chakra = Chakra.pathway((EntityLivingBase)entity);
						if (chakra.getAmount() > 0.0d) {
							amount = Math.min((float)chakra.getAmount(), amount);
							chakra.consume((double)amount);
							this.weakenEntity((EntityLivingBase)entity, amount);
							ret = true;
						}
					}
					if (ret) {
						Chakra.pathway(summoner).consume((double)-amount, true);
						summoner.heal(amount * 0.01F);
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:charging_chakra")),
						 0.6F, this.rand.nextFloat() * 0.6F + 0.8F);
					}
				}
			}
			return ret;
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			EntityLivingBase summoner = this.getSummoner();
			if (!entity.equals(summoner) && this.absorbEntityChakra(entity, 10f)) {
				if (entity instanceof EntityLivingBase && ItemSenjutsu.isSageModeActivated((EntityLivingBase)entity)
				 && !ItemSenjutsu.canUseSageMode(summoner)) {
					this.absorbedSageChakra += 10.0d;
					if (this.absorbedSageChakra >= this.sageChakraLimit) {
						ProcedureUtils.setDeathAnimations(summoner, 1, 100);
						summoner.addPotionEffect(new PotionEffect(PotionParalysis.potion, 100, 1, false, false));
						this.setDead();
					}
				}
			}
		}

		@Override
		public void applyEntityCollision(Entity entityIn) {
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void handleStatusUpdate(byte id) {
			super.handleStatusUpdate(id);
			this.hurtTime = 0;
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/electric_armor.png");
			private final DynamicTexture brightness = new DynamicTexture(16, 16);
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelSphere(), 0.1F);
		        int[] aint = this.brightness.getTextureData();
		        for (int i = 0; i < 256; ++i) {
		            aint[i] = -1;
		        }
		        this.brightness.updateDynamicTexture();
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
		                this.brightnessBuffer.put(0.5F);
		                this.brightnessBuffer.put(0.5F);
		                this.brightnessBuffer.put(0.5F);
		                this.brightnessBuffer.put(0.5F);
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
		            GlStateManager.bindTexture(this.brightness.getGlTextureId());
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
				return this.texture;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelSphere extends ModelBase {
			private final ModelRenderer bone;
			private final ModelRenderer hexadecagon;
			private final ModelRenderer hexadecagon_r1;
			private final ModelRenderer hexadecagon_r2;
			private final ModelRenderer hexadecagon_r3;
			private final ModelRenderer hexadecagon_r4;
			private final ModelRenderer hexadecagon_r5;
			private final ModelRenderer hexadecagon_r6;
			private final ModelRenderer hexadecagon_r7;
			private final ModelRenderer hexadecagon_r8;
			private final ModelRenderer hexadecagon_r9;
			private final ModelRenderer hexadecagon_r10;
			private final ModelRenderer hexadecagon_r11;
			private final ModelRenderer hexadecagon_r12;
			private final ModelRenderer hexadecagon_r13;
			private final ModelRenderer hexadecagon_r14;
			private final ModelRenderer hexadecagon_r15;
			private final ModelRenderer hexadecagon8;
			private final ModelRenderer hexadecagon_r16;
			private final ModelRenderer hexadecagon_r17;
			private final ModelRenderer hexadecagon_r18;
			private final ModelRenderer hexadecagon_r19;
			private final ModelRenderer hexadecagon_r20;
			private final ModelRenderer hexadecagon_r21;
			private final ModelRenderer hexadecagon_r22;
			private final ModelRenderer hexadecagon_r23;
			private final ModelRenderer hexadecagon_r24;
			private final ModelRenderer hexadecagon_r25;
			private final ModelRenderer hexadecagon_r26;
			private final ModelRenderer hexadecagon_r27;
			private final ModelRenderer hexadecagon_r28;
			private final ModelRenderer hexadecagon_r29;
			private final ModelRenderer hexadecagon7;
			private final ModelRenderer hexadecagon_r30;
			private final ModelRenderer hexadecagon_r31;
			private final ModelRenderer hexadecagon_r32;
			private final ModelRenderer hexadecagon_r33;
			private final ModelRenderer hexadecagon_r34;
			private final ModelRenderer hexadecagon_r35;
			private final ModelRenderer hexadecagon_r36;
			private final ModelRenderer hexadecagon_r37;
			private final ModelRenderer hexadecagon_r38;
			private final ModelRenderer hexadecagon_r39;
			private final ModelRenderer hexadecagon_r40;
			private final ModelRenderer hexadecagon_r41;
			private final ModelRenderer hexadecagon_r42;
			private final ModelRenderer hexadecagon_r43;
			private final ModelRenderer hexadecagon6;
			private final ModelRenderer hexadecagon_r44;
			private final ModelRenderer hexadecagon_r45;
			private final ModelRenderer hexadecagon_r46;
			private final ModelRenderer hexadecagon_r47;
			private final ModelRenderer hexadecagon_r48;
			private final ModelRenderer hexadecagon_r49;
			private final ModelRenderer hexadecagon_r50;
			private final ModelRenderer hexadecagon_r51;
			private final ModelRenderer hexadecagon_r52;
			private final ModelRenderer hexadecagon_r53;
			private final ModelRenderer hexadecagon_r54;
			private final ModelRenderer hexadecagon_r55;
			private final ModelRenderer hexadecagon_r56;
			private final ModelRenderer hexadecagon_r57;
			private final ModelRenderer hexadecagon5;
			private final ModelRenderer hexadecagon_r58;
			private final ModelRenderer hexadecagon_r59;
			private final ModelRenderer hexadecagon_r60;
			private final ModelRenderer hexadecagon_r61;
			private final ModelRenderer hexadecagon_r62;
			private final ModelRenderer hexadecagon_r63;
			private final ModelRenderer hexadecagon_r64;
			private final ModelRenderer hexadecagon_r65;
			private final ModelRenderer hexadecagon_r66;
			private final ModelRenderer hexadecagon_r67;
			private final ModelRenderer hexadecagon_r68;
			private final ModelRenderer hexadecagon_r69;
			private final ModelRenderer hexadecagon_r70;
			private final ModelRenderer hexadecagon_r71;
			private final ModelRenderer hexadecagon4;
			private final ModelRenderer hexadecagon_r72;
			private final ModelRenderer hexadecagon_r73;
			private final ModelRenderer hexadecagon_r74;
			private final ModelRenderer hexadecagon_r75;
			private final ModelRenderer hexadecagon_r76;
			private final ModelRenderer hexadecagon_r77;
			private final ModelRenderer hexadecagon_r78;
			private final ModelRenderer hexadecagon_r79;
			private final ModelRenderer hexadecagon_r80;
			private final ModelRenderer hexadecagon_r81;
			private final ModelRenderer hexadecagon_r82;
			private final ModelRenderer hexadecagon_r83;
			private final ModelRenderer hexadecagon_r84;
			private final ModelRenderer hexadecagon_r85;
			private final ModelRenderer hexadecagon3;
			private final ModelRenderer hexadecagon_r86;
			private final ModelRenderer hexadecagon_r87;
			private final ModelRenderer hexadecagon_r88;
			private final ModelRenderer hexadecagon_r89;
			private final ModelRenderer hexadecagon_r90;
			private final ModelRenderer hexadecagon_r91;
			private final ModelRenderer hexadecagon_r92;
			private final ModelRenderer hexadecagon_r93;
			private final ModelRenderer hexadecagon_r94;
			private final ModelRenderer hexadecagon_r95;
			private final ModelRenderer hexadecagon_r96;
			private final ModelRenderer hexadecagon_r97;
			private final ModelRenderer hexadecagon_r98;
			private final ModelRenderer hexadecagon_r99;
			private final ModelRenderer hexadecagon2;
			private final ModelRenderer hexadecagon_r100;
			private final ModelRenderer hexadecagon_r101;
			private final ModelRenderer hexadecagon_r102;
			private final ModelRenderer hexadecagon_r103;
			private final ModelRenderer hexadecagon_r104;
			private final ModelRenderer hexadecagon_r105;
			private final ModelRenderer hexadecagon_r106;
			private final ModelRenderer hexadecagon_r107;
			private final ModelRenderer hexadecagon_r108;
			private final ModelRenderer hexadecagon_r109;
			private final ModelRenderer hexadecagon_r110;
			private final ModelRenderer hexadecagon_r111;
			private final ModelRenderer hexadecagon_r112;
			private final ModelRenderer hexadecagon_r113;
		
			public ModelSphere() {
				textureWidth = 2;
				textureHeight = 2;
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				hexadecagon = new ModelRenderer(this);
				hexadecagon.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(hexadecagon);
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r1 = new ModelRenderer(this);
				hexadecagon_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r1);
				setRotationAngle(hexadecagon_r1, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r2 = new ModelRenderer(this);
				hexadecagon_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r2);
				setRotationAngle(hexadecagon_r2, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r3 = new ModelRenderer(this);
				hexadecagon_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r3);
				setRotationAngle(hexadecagon_r3, 1.1781F, 0.0F, 0.0F);
				hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r4 = new ModelRenderer(this);
				hexadecagon_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r4);
				setRotationAngle(hexadecagon_r4, 1.5708F, 0.0F, 0.0F);
				hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r5 = new ModelRenderer(this);
				hexadecagon_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r5);
				setRotationAngle(hexadecagon_r5, 1.9635F, 0.0F, 0.0F);
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r6 = new ModelRenderer(this);
				hexadecagon_r6.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r6);
				setRotationAngle(hexadecagon_r6, 2.3562F, 0.0F, 0.0F);
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r7 = new ModelRenderer(this);
				hexadecagon_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r7);
				setRotationAngle(hexadecagon_r7, 2.7489F, 0.0F, 0.0F);
				hexadecagon_r7.cubeList.add(new ModelBox(hexadecagon_r7, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r8 = new ModelRenderer(this);
				hexadecagon_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r8);
				setRotationAngle(hexadecagon_r8, 3.1416F, 0.0F, 0.0F);
				hexadecagon_r8.cubeList.add(new ModelBox(hexadecagon_r8, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r9 = new ModelRenderer(this);
				hexadecagon_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r9);
				setRotationAngle(hexadecagon_r9, 2.7489F, 0.0F, 3.1416F);
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r10 = new ModelRenderer(this);
				hexadecagon_r10.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r10);
				setRotationAngle(hexadecagon_r10, 2.3562F, 0.0F, -3.1416F);
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r11 = new ModelRenderer(this);
				hexadecagon_r11.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r11);
				setRotationAngle(hexadecagon_r11, 1.9635F, 0.0F, 3.1416F);
				hexadecagon_r11.cubeList.add(new ModelBox(hexadecagon_r11, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r12 = new ModelRenderer(this);
				hexadecagon_r12.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r12);
				setRotationAngle(hexadecagon_r12, 1.5708F, 0.0F, 3.1416F);
				hexadecagon_r12.cubeList.add(new ModelBox(hexadecagon_r12, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r13 = new ModelRenderer(this);
				hexadecagon_r13.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r13);
				setRotationAngle(hexadecagon_r13, 1.1781F, 0.0F, -3.1416F);
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r14 = new ModelRenderer(this);
				hexadecagon_r14.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r14);
				setRotationAngle(hexadecagon_r14, 0.7854F, 0.0F, 3.1416F);
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r15 = new ModelRenderer(this);
				hexadecagon_r15.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r15);
				setRotationAngle(hexadecagon_r15, 0.3927F, 0.0F, -3.1416F);
				hexadecagon_r15.cubeList.add(new ModelBox(hexadecagon_r15, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon8 = new ModelRenderer(this);
				hexadecagon8.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(hexadecagon8);
				setRotationAngle(hexadecagon8, 0.0F, 0.0F, 0.3927F);
				
		
				hexadecagon_r16 = new ModelRenderer(this);
				hexadecagon_r16.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r16);
				setRotationAngle(hexadecagon_r16, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r16.cubeList.add(new ModelBox(hexadecagon_r16, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r17 = new ModelRenderer(this);
				hexadecagon_r17.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r17);
				setRotationAngle(hexadecagon_r17, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r18 = new ModelRenderer(this);
				hexadecagon_r18.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r18);
				setRotationAngle(hexadecagon_r18, 1.1781F, 0.0F, 0.0F);
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r19 = new ModelRenderer(this);
				hexadecagon_r19.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r19);
				setRotationAngle(hexadecagon_r19, 1.5708F, 0.0F, 0.0F);
				hexadecagon_r19.cubeList.add(new ModelBox(hexadecagon_r19, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r20 = new ModelRenderer(this);
				hexadecagon_r20.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r20);
				setRotationAngle(hexadecagon_r20, 1.9635F, 0.0F, 0.0F);
				hexadecagon_r20.cubeList.add(new ModelBox(hexadecagon_r20, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r21 = new ModelRenderer(this);
				hexadecagon_r21.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r21);
				setRotationAngle(hexadecagon_r21, 2.3562F, 0.0F, 0.0F);
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r22 = new ModelRenderer(this);
				hexadecagon_r22.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r22);
				setRotationAngle(hexadecagon_r22, 2.7489F, 0.0F, 0.0F);
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r23 = new ModelRenderer(this);
				hexadecagon_r23.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r23);
				setRotationAngle(hexadecagon_r23, 2.7489F, 0.0F, 3.1416F);
				hexadecagon_r23.cubeList.add(new ModelBox(hexadecagon_r23, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r24 = new ModelRenderer(this);
				hexadecagon_r24.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r24);
				setRotationAngle(hexadecagon_r24, 2.3562F, 0.0F, 3.1416F);
				hexadecagon_r24.cubeList.add(new ModelBox(hexadecagon_r24, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r25 = new ModelRenderer(this);
				hexadecagon_r25.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r25);
				setRotationAngle(hexadecagon_r25, 1.9635F, 0.0F, -3.1416F);
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r26 = new ModelRenderer(this);
				hexadecagon_r26.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r26);
				setRotationAngle(hexadecagon_r26, 1.5708F, 0.0F, -3.1416F);
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r27 = new ModelRenderer(this);
				hexadecagon_r27.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r27);
				setRotationAngle(hexadecagon_r27, 1.1781F, 0.0F, 3.1416F);
				hexadecagon_r27.cubeList.add(new ModelBox(hexadecagon_r27, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r28 = new ModelRenderer(this);
				hexadecagon_r28.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r28);
				setRotationAngle(hexadecagon_r28, 0.7854F, 0.0F, -3.1416F);
				hexadecagon_r28.cubeList.add(new ModelBox(hexadecagon_r28, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r29 = new ModelRenderer(this);
				hexadecagon_r29.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r29);
				setRotationAngle(hexadecagon_r29, 0.3927F, 0.0F, -3.1416F);
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon7 = new ModelRenderer(this);
				hexadecagon7.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(hexadecagon7);
				setRotationAngle(hexadecagon7, 0.0F, 0.0F, 0.7854F);
				
		
				hexadecagon_r30 = new ModelRenderer(this);
				hexadecagon_r30.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r30);
				setRotationAngle(hexadecagon_r30, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r31 = new ModelRenderer(this);
				hexadecagon_r31.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r31);
				setRotationAngle(hexadecagon_r31, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r31.cubeList.add(new ModelBox(hexadecagon_r31, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r32 = new ModelRenderer(this);
				hexadecagon_r32.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r32);
				setRotationAngle(hexadecagon_r32, 1.1781F, 0.0F, 0.0F);
				hexadecagon_r32.cubeList.add(new ModelBox(hexadecagon_r32, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r33 = new ModelRenderer(this);
				hexadecagon_r33.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r33);
				setRotationAngle(hexadecagon_r33, 1.5708F, 0.0F, 0.0F);
				hexadecagon_r33.cubeList.add(new ModelBox(hexadecagon_r33, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r34 = new ModelRenderer(this);
				hexadecagon_r34.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r34);
				setRotationAngle(hexadecagon_r34, 1.9635F, 0.0F, 0.0F);
				hexadecagon_r34.cubeList.add(new ModelBox(hexadecagon_r34, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r35 = new ModelRenderer(this);
				hexadecagon_r35.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r35);
				setRotationAngle(hexadecagon_r35, 2.3562F, 0.0F, 0.0F);
				hexadecagon_r35.cubeList.add(new ModelBox(hexadecagon_r35, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r36 = new ModelRenderer(this);
				hexadecagon_r36.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r36);
				setRotationAngle(hexadecagon_r36, 2.7489F, 0.0F, 0.0F);
				hexadecagon_r36.cubeList.add(new ModelBox(hexadecagon_r36, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r37 = new ModelRenderer(this);
				hexadecagon_r37.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r37);
				setRotationAngle(hexadecagon_r37, 2.7489F, 0.0F, 3.1416F);
				hexadecagon_r37.cubeList.add(new ModelBox(hexadecagon_r37, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r38 = new ModelRenderer(this);
				hexadecagon_r38.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r38);
				setRotationAngle(hexadecagon_r38, 2.3562F, 0.0F, 3.1416F);
				hexadecagon_r38.cubeList.add(new ModelBox(hexadecagon_r38, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r39 = new ModelRenderer(this);
				hexadecagon_r39.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r39);
				setRotationAngle(hexadecagon_r39, 1.9635F, 0.0F, -3.1416F);
				hexadecagon_r39.cubeList.add(new ModelBox(hexadecagon_r39, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r40 = new ModelRenderer(this);
				hexadecagon_r40.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r40);
				setRotationAngle(hexadecagon_r40, 1.5708F, 0.0F, -3.1416F);
				hexadecagon_r40.cubeList.add(new ModelBox(hexadecagon_r40, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r41 = new ModelRenderer(this);
				hexadecagon_r41.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r41);
				setRotationAngle(hexadecagon_r41, 1.1781F, 0.0F, 3.1416F);
				hexadecagon_r41.cubeList.add(new ModelBox(hexadecagon_r41, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r42 = new ModelRenderer(this);
				hexadecagon_r42.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r42);
				setRotationAngle(hexadecagon_r42, 0.7854F, 0.0F, 3.1416F);
				hexadecagon_r42.cubeList.add(new ModelBox(hexadecagon_r42, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r43 = new ModelRenderer(this);
				hexadecagon_r43.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r43);
				setRotationAngle(hexadecagon_r43, 0.3927F, 0.0F, -3.1416F);
				hexadecagon_r43.cubeList.add(new ModelBox(hexadecagon_r43, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon6 = new ModelRenderer(this);
				hexadecagon6.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(hexadecagon6);
				setRotationAngle(hexadecagon6, 0.0F, 0.0F, 1.1781F);
				
		
				hexadecagon_r44 = new ModelRenderer(this);
				hexadecagon_r44.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r44);
				setRotationAngle(hexadecagon_r44, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r44.cubeList.add(new ModelBox(hexadecagon_r44, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r45 = new ModelRenderer(this);
				hexadecagon_r45.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r45);
				setRotationAngle(hexadecagon_r45, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r45.cubeList.add(new ModelBox(hexadecagon_r45, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r46 = new ModelRenderer(this);
				hexadecagon_r46.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r46);
				setRotationAngle(hexadecagon_r46, 1.1781F, 0.0F, 0.0F);
				hexadecagon_r46.cubeList.add(new ModelBox(hexadecagon_r46, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r47 = new ModelRenderer(this);
				hexadecagon_r47.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r47);
				setRotationAngle(hexadecagon_r47, 1.5708F, 0.0F, 0.0F);
				hexadecagon_r47.cubeList.add(new ModelBox(hexadecagon_r47, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r48 = new ModelRenderer(this);
				hexadecagon_r48.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r48);
				setRotationAngle(hexadecagon_r48, 1.9635F, 0.0F, 0.0F);
				hexadecagon_r48.cubeList.add(new ModelBox(hexadecagon_r48, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r49 = new ModelRenderer(this);
				hexadecagon_r49.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r49);
				setRotationAngle(hexadecagon_r49, 2.3562F, 0.0F, 0.0F);
				hexadecagon_r49.cubeList.add(new ModelBox(hexadecagon_r49, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r50 = new ModelRenderer(this);
				hexadecagon_r50.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r50);
				setRotationAngle(hexadecagon_r50, 2.7489F, 0.0F, 0.0F);
				hexadecagon_r50.cubeList.add(new ModelBox(hexadecagon_r50, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r51 = new ModelRenderer(this);
				hexadecagon_r51.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r51);
				setRotationAngle(hexadecagon_r51, 2.7489F, 0.0F, 3.1416F);
				hexadecagon_r51.cubeList.add(new ModelBox(hexadecagon_r51, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r52 = new ModelRenderer(this);
				hexadecagon_r52.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r52);
				setRotationAngle(hexadecagon_r52, 2.3562F, 0.0F, 3.1416F);
				hexadecagon_r52.cubeList.add(new ModelBox(hexadecagon_r52, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r53 = new ModelRenderer(this);
				hexadecagon_r53.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r53);
				setRotationAngle(hexadecagon_r53, 1.9635F, 0.0F, 3.1416F);
				hexadecagon_r53.cubeList.add(new ModelBox(hexadecagon_r53, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r54 = new ModelRenderer(this);
				hexadecagon_r54.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r54);
				setRotationAngle(hexadecagon_r54, 1.5708F, 0.0F, -3.1416F);
				hexadecagon_r54.cubeList.add(new ModelBox(hexadecagon_r54, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r55 = new ModelRenderer(this);
				hexadecagon_r55.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r55);
				setRotationAngle(hexadecagon_r55, 1.1781F, 0.0F, -3.1416F);
				hexadecagon_r55.cubeList.add(new ModelBox(hexadecagon_r55, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r56 = new ModelRenderer(this);
				hexadecagon_r56.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r56);
				setRotationAngle(hexadecagon_r56, 0.7854F, 0.0F, -3.1416F);
				hexadecagon_r56.cubeList.add(new ModelBox(hexadecagon_r56, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r57 = new ModelRenderer(this);
				hexadecagon_r57.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r57);
				setRotationAngle(hexadecagon_r57, 0.3927F, 0.0F, -3.1416F);
				hexadecagon_r57.cubeList.add(new ModelBox(hexadecagon_r57, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon5 = new ModelRenderer(this);
				hexadecagon5.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(hexadecagon5);
				setRotationAngle(hexadecagon5, 0.0F, 0.0F, 1.5708F);
				
		
				hexadecagon_r58 = new ModelRenderer(this);
				hexadecagon_r58.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r58);
				setRotationAngle(hexadecagon_r58, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r58.cubeList.add(new ModelBox(hexadecagon_r58, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r59 = new ModelRenderer(this);
				hexadecagon_r59.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r59);
				setRotationAngle(hexadecagon_r59, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r59.cubeList.add(new ModelBox(hexadecagon_r59, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r60 = new ModelRenderer(this);
				hexadecagon_r60.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r60);
				setRotationAngle(hexadecagon_r60, 1.1781F, 0.0F, 0.0F);
				hexadecagon_r60.cubeList.add(new ModelBox(hexadecagon_r60, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r61 = new ModelRenderer(this);
				hexadecagon_r61.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r61);
				setRotationAngle(hexadecagon_r61, 1.5708F, 0.0F, 0.0F);
				hexadecagon_r61.cubeList.add(new ModelBox(hexadecagon_r61, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r62 = new ModelRenderer(this);
				hexadecagon_r62.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r62);
				setRotationAngle(hexadecagon_r62, 1.9635F, 0.0F, 0.0F);
				hexadecagon_r62.cubeList.add(new ModelBox(hexadecagon_r62, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r63 = new ModelRenderer(this);
				hexadecagon_r63.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r63);
				setRotationAngle(hexadecagon_r63, 2.3562F, 0.0F, 0.0F);
				hexadecagon_r63.cubeList.add(new ModelBox(hexadecagon_r63, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r64 = new ModelRenderer(this);
				hexadecagon_r64.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r64);
				setRotationAngle(hexadecagon_r64, 2.7489F, 0.0F, 0.0F);
				hexadecagon_r64.cubeList.add(new ModelBox(hexadecagon_r64, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r65 = new ModelRenderer(this);
				hexadecagon_r65.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r65);
				setRotationAngle(hexadecagon_r65, 2.7489F, 0.0F, 3.1416F);
				hexadecagon_r65.cubeList.add(new ModelBox(hexadecagon_r65, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r66 = new ModelRenderer(this);
				hexadecagon_r66.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r66);
				setRotationAngle(hexadecagon_r66, 2.3562F, 0.0F, -3.1416F);
				hexadecagon_r66.cubeList.add(new ModelBox(hexadecagon_r66, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r67 = new ModelRenderer(this);
				hexadecagon_r67.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r67);
				setRotationAngle(hexadecagon_r67, 1.9635F, 0.0F, 3.1416F);
				hexadecagon_r67.cubeList.add(new ModelBox(hexadecagon_r67, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r68 = new ModelRenderer(this);
				hexadecagon_r68.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r68);
				setRotationAngle(hexadecagon_r68, 1.5708F, 0.0F, 3.1416F);
				hexadecagon_r68.cubeList.add(new ModelBox(hexadecagon_r68, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r69 = new ModelRenderer(this);
				hexadecagon_r69.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r69);
				setRotationAngle(hexadecagon_r69, 1.1781F, 0.0F, 3.1416F);
				hexadecagon_r69.cubeList.add(new ModelBox(hexadecagon_r69, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r70 = new ModelRenderer(this);
				hexadecagon_r70.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r70);
				setRotationAngle(hexadecagon_r70, 0.7854F, 0.0F, 3.1416F);
				hexadecagon_r70.cubeList.add(new ModelBox(hexadecagon_r70, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r71 = new ModelRenderer(this);
				hexadecagon_r71.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r71);
				setRotationAngle(hexadecagon_r71, 0.3927F, 0.0F, -3.1416F);
				hexadecagon_r71.cubeList.add(new ModelBox(hexadecagon_r71, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon4 = new ModelRenderer(this);
				hexadecagon4.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(hexadecagon4);
				setRotationAngle(hexadecagon4, 0.0F, 0.0F, -1.1781F);
				
		
				hexadecagon_r72 = new ModelRenderer(this);
				hexadecagon_r72.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r72);
				setRotationAngle(hexadecagon_r72, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r72.cubeList.add(new ModelBox(hexadecagon_r72, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r73 = new ModelRenderer(this);
				hexadecagon_r73.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r73);
				setRotationAngle(hexadecagon_r73, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r73.cubeList.add(new ModelBox(hexadecagon_r73, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r74 = new ModelRenderer(this);
				hexadecagon_r74.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r74);
				setRotationAngle(hexadecagon_r74, 1.1781F, 0.0F, 0.0F);
				hexadecagon_r74.cubeList.add(new ModelBox(hexadecagon_r74, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r75 = new ModelRenderer(this);
				hexadecagon_r75.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r75);
				setRotationAngle(hexadecagon_r75, 1.5708F, 0.0F, 0.0F);
				hexadecagon_r75.cubeList.add(new ModelBox(hexadecagon_r75, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r76 = new ModelRenderer(this);
				hexadecagon_r76.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r76);
				setRotationAngle(hexadecagon_r76, 1.9635F, 0.0F, 0.0F);
				hexadecagon_r76.cubeList.add(new ModelBox(hexadecagon_r76, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r77 = new ModelRenderer(this);
				hexadecagon_r77.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r77);
				setRotationAngle(hexadecagon_r77, 2.3562F, 0.0F, 0.0F);
				hexadecagon_r77.cubeList.add(new ModelBox(hexadecagon_r77, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r78 = new ModelRenderer(this);
				hexadecagon_r78.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r78);
				setRotationAngle(hexadecagon_r78, 2.7489F, 0.0F, 0.0F);
				hexadecagon_r78.cubeList.add(new ModelBox(hexadecagon_r78, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r79 = new ModelRenderer(this);
				hexadecagon_r79.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r79);
				setRotationAngle(hexadecagon_r79, 2.7489F, 0.0F, -3.1416F);
				hexadecagon_r79.cubeList.add(new ModelBox(hexadecagon_r79, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r80 = new ModelRenderer(this);
				hexadecagon_r80.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r80);
				setRotationAngle(hexadecagon_r80, 2.3562F, 0.0F, -3.1416F);
				hexadecagon_r80.cubeList.add(new ModelBox(hexadecagon_r80, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r81 = new ModelRenderer(this);
				hexadecagon_r81.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r81);
				setRotationAngle(hexadecagon_r81, 1.9635F, 0.0F, -3.1416F);
				hexadecagon_r81.cubeList.add(new ModelBox(hexadecagon_r81, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r82 = new ModelRenderer(this);
				hexadecagon_r82.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r82);
				setRotationAngle(hexadecagon_r82, 1.5708F, 0.0F, 3.1416F);
				hexadecagon_r82.cubeList.add(new ModelBox(hexadecagon_r82, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r83 = new ModelRenderer(this);
				hexadecagon_r83.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r83);
				setRotationAngle(hexadecagon_r83, 1.1781F, 0.0F, 3.1416F);
				hexadecagon_r83.cubeList.add(new ModelBox(hexadecagon_r83, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r84 = new ModelRenderer(this);
				hexadecagon_r84.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r84);
				setRotationAngle(hexadecagon_r84, 0.7854F, 0.0F, -3.1416F);
				hexadecagon_r84.cubeList.add(new ModelBox(hexadecagon_r84, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r85 = new ModelRenderer(this);
				hexadecagon_r85.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r85);
				setRotationAngle(hexadecagon_r85, 0.3927F, 0.0F, -3.1416F);
				hexadecagon_r85.cubeList.add(new ModelBox(hexadecagon_r85, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon3 = new ModelRenderer(this);
				hexadecagon3.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(hexadecagon3);
				setRotationAngle(hexadecagon3, 0.0F, 0.0F, -0.7854F);
				
		
				hexadecagon_r86 = new ModelRenderer(this);
				hexadecagon_r86.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r86);
				setRotationAngle(hexadecagon_r86, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r86.cubeList.add(new ModelBox(hexadecagon_r86, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r87 = new ModelRenderer(this);
				hexadecagon_r87.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r87);
				setRotationAngle(hexadecagon_r87, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r87.cubeList.add(new ModelBox(hexadecagon_r87, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r88 = new ModelRenderer(this);
				hexadecagon_r88.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r88);
				setRotationAngle(hexadecagon_r88, 1.1781F, 0.0F, 0.0F);
				hexadecagon_r88.cubeList.add(new ModelBox(hexadecagon_r88, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r89 = new ModelRenderer(this);
				hexadecagon_r89.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r89);
				setRotationAngle(hexadecagon_r89, 1.5708F, 0.0F, 0.0F);
				hexadecagon_r89.cubeList.add(new ModelBox(hexadecagon_r89, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r90 = new ModelRenderer(this);
				hexadecagon_r90.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r90);
				setRotationAngle(hexadecagon_r90, 1.9635F, 0.0F, 0.0F);
				hexadecagon_r90.cubeList.add(new ModelBox(hexadecagon_r90, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r91 = new ModelRenderer(this);
				hexadecagon_r91.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r91);
				setRotationAngle(hexadecagon_r91, 2.3562F, 0.0F, 0.0F);
				hexadecagon_r91.cubeList.add(new ModelBox(hexadecagon_r91, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r92 = new ModelRenderer(this);
				hexadecagon_r92.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r92);
				setRotationAngle(hexadecagon_r92, 2.7489F, 0.0F, 0.0F);
				hexadecagon_r92.cubeList.add(new ModelBox(hexadecagon_r92, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r93 = new ModelRenderer(this);
				hexadecagon_r93.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r93);
				setRotationAngle(hexadecagon_r93, 2.7489F, 0.0F, 3.1416F);
				hexadecagon_r93.cubeList.add(new ModelBox(hexadecagon_r93, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r94 = new ModelRenderer(this);
				hexadecagon_r94.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r94);
				setRotationAngle(hexadecagon_r94, 2.3562F, 0.0F, 3.1416F);
				hexadecagon_r94.cubeList.add(new ModelBox(hexadecagon_r94, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r95 = new ModelRenderer(this);
				hexadecagon_r95.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r95);
				setRotationAngle(hexadecagon_r95, 1.9635F, 0.0F, -3.1416F);
				hexadecagon_r95.cubeList.add(new ModelBox(hexadecagon_r95, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r96 = new ModelRenderer(this);
				hexadecagon_r96.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r96);
				setRotationAngle(hexadecagon_r96, 1.5708F, 0.0F, -3.1416F);
				hexadecagon_r96.cubeList.add(new ModelBox(hexadecagon_r96, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r97 = new ModelRenderer(this);
				hexadecagon_r97.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r97);
				setRotationAngle(hexadecagon_r97, 1.1781F, 0.0F, -3.1416F);
				hexadecagon_r97.cubeList.add(new ModelBox(hexadecagon_r97, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r98 = new ModelRenderer(this);
				hexadecagon_r98.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r98);
				setRotationAngle(hexadecagon_r98, 0.7854F, 0.0F, -3.1416F);
				hexadecagon_r98.cubeList.add(new ModelBox(hexadecagon_r98, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r99 = new ModelRenderer(this);
				hexadecagon_r99.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r99);
				setRotationAngle(hexadecagon_r99, 0.3927F, 0.0F, -3.1416F);
				hexadecagon_r99.cubeList.add(new ModelBox(hexadecagon_r99, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon2 = new ModelRenderer(this);
				hexadecagon2.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.addChild(hexadecagon2);
				setRotationAngle(hexadecagon2, 0.0F, 0.0F, -0.3927F);
				
		
				hexadecagon_r100 = new ModelRenderer(this);
				hexadecagon_r100.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r100);
				setRotationAngle(hexadecagon_r100, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r100.cubeList.add(new ModelBox(hexadecagon_r100, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r101 = new ModelRenderer(this);
				hexadecagon_r101.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r101);
				setRotationAngle(hexadecagon_r101, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r101.cubeList.add(new ModelBox(hexadecagon_r101, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r102 = new ModelRenderer(this);
				hexadecagon_r102.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r102);
				setRotationAngle(hexadecagon_r102, 1.1781F, 0.0F, 0.0F);
				hexadecagon_r102.cubeList.add(new ModelBox(hexadecagon_r102, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r103 = new ModelRenderer(this);
				hexadecagon_r103.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r103);
				setRotationAngle(hexadecagon_r103, 1.5708F, 0.0F, 0.0F);
				hexadecagon_r103.cubeList.add(new ModelBox(hexadecagon_r103, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r104 = new ModelRenderer(this);
				hexadecagon_r104.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r104);
				setRotationAngle(hexadecagon_r104, 1.9635F, 0.0F, 0.0F);
				hexadecagon_r104.cubeList.add(new ModelBox(hexadecagon_r104, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r105 = new ModelRenderer(this);
				hexadecagon_r105.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r105);
				setRotationAngle(hexadecagon_r105, 2.3562F, 0.0F, 0.0F);
				hexadecagon_r105.cubeList.add(new ModelBox(hexadecagon_r105, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r106 = new ModelRenderer(this);
				hexadecagon_r106.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r106);
				setRotationAngle(hexadecagon_r106, 2.7489F, 0.0F, 0.0F);
				hexadecagon_r106.cubeList.add(new ModelBox(hexadecagon_r106, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r107 = new ModelRenderer(this);
				hexadecagon_r107.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r107);
				setRotationAngle(hexadecagon_r107, 2.7489F, 0.0F, 3.1416F);
				hexadecagon_r107.cubeList.add(new ModelBox(hexadecagon_r107, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r108 = new ModelRenderer(this);
				hexadecagon_r108.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r108);
				setRotationAngle(hexadecagon_r108, 2.3562F, 0.0F, -3.1416F);
				hexadecagon_r108.cubeList.add(new ModelBox(hexadecagon_r108, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r109 = new ModelRenderer(this);
				hexadecagon_r109.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r109);
				setRotationAngle(hexadecagon_r109, 1.9635F, 0.0F, 3.1416F);
				hexadecagon_r109.cubeList.add(new ModelBox(hexadecagon_r109, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r110 = new ModelRenderer(this);
				hexadecagon_r110.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r110);
				setRotationAngle(hexadecagon_r110, 1.5708F, 0.0F, -3.1416F);
				hexadecagon_r110.cubeList.add(new ModelBox(hexadecagon_r110, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r111 = new ModelRenderer(this);
				hexadecagon_r111.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r111);
				setRotationAngle(hexadecagon_r111, 1.1781F, 0.0F, -3.1416F);
				hexadecagon_r111.cubeList.add(new ModelBox(hexadecagon_r111, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r112 = new ModelRenderer(this);
				hexadecagon_r112.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r112);
				setRotationAngle(hexadecagon_r112, 0.7854F, 0.0F, 3.1416F);
				hexadecagon_r112.cubeList.add(new ModelBox(hexadecagon_r112, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r113 = new ModelRenderer(this);
				hexadecagon_r113.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r113);
				setRotationAngle(hexadecagon_r113, 0.3927F, 0.0F, -3.1416F);
				hexadecagon_r113.cubeList.add(new ModelBox(hexadecagon_r113, 0, 0, -0.5F, -0.5F, -2.5F, 1, 1, 0, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.depthMask(true);
				float scale = Math.min(f2 / 20.0F, 1.0F);
				GlStateManager.translate(0.0F, 0.5F, 0.0D);
				GlStateManager.scale(8.0F, 8.0F, 8.0F);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.translate(f2 * 0.04F, f2 * 0.02F, 0.0F);
				GlStateManager.matrixMode(5888);
				GlStateManager.enableBlend();
				GlStateManager.alphaFunc(0x204, 0.001f);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F * scale);
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				//GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				//(Minecraft.getMinecraft()).entityRenderer.setupFogColor(true);
				this.bone.render(f5);
				//(Minecraft.getMinecraft()).entityRenderer.setupFogColor(false);
				GlStateManager.alphaFunc(0x204, 0.1f);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				//GlStateManager.disableAlpha();
				//GlStateManager.depthMask(false);
				GlStateManager.popMatrix();
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
