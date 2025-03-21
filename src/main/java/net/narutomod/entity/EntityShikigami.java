
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.potion.PotionFlight;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketChangeGameState;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityShikigami extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 480;
	public static final int ENTITYID_RANGED = 481;

	public EntityShikigami(ElementsNarutomodMod instance) {
		super(instance, 906);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "shikigami"), ENTITYID).name("shikigami").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityPaperArrow.class)
				.id(new ResourceLocation("narutomod", "shikigami_bullet"), ENTITYID_RANGED).name("shikigami_bullet").tracker(64, 3, true).build());
	}
	
	public static class EC extends EntityShieldBase {
		protected static final String ENTITYID_KEY = "ShikigamiWingsEntityId";
		private final int waitTime = 100;
		private double chakraUsage;
		private boolean jutsuKey2Pressed;
		private boolean isShooting;
		private EntityPaperBind.EC bindEntity;
		
		public EC(World a) {
			super(a);
			this.setSize(0.5f, 1.7f);
			this.dieOnNoPassengers = false;
		}

		public EC(EntityLivingBase userIn, double chakraUsageIn) {
			this(userIn.world);
			this.setSummoner(userIn);
			this.chakraUsage = chakraUsageIn;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Math.max(userIn.getMaxHealth() * 0.5f, 20.0f));
			this.setHealth(this.getMaxHealth());
			userIn.getEntityData().setInteger(ENTITYID_KEY, this.getEntityId());
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
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:paperflip")), 0.6f, 0.8f);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			EntityLivingBase user = this.getSummoner();
			if (user != null && user.isEntityAlive()) {
				this.setPosition(user.posX, user.posY, user.posZ);
				if (!this.world.isRemote) {
					if (this.ticksExisted < this.waitTime && this.rand.nextFloat() < 0.6667f) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:paperflip")), 0.8f, this.rand.nextFloat() * 0.4f + 0.9f);
					}
					if (this.ticksExisted % 20 == 2) {
						if (Chakra.pathway(user).consume(this.chakraUsage)) {
							user.addPotionEffect(new PotionEffect(PotionFlight.potion, 25, 1, false, false));
						} else {
							this.setDead();
						}
					}
					Chakra.Pathway chakra = Chakra.pathway(user);
					this.isShooting = this.ticksExisted > this.waitTime
					 && user.getEntityData().getBoolean(NarutomodModVariables.JutsuKey1Pressed) && chakra.consume(this.chakraUsage * 0.05d);
					if (this.isShooting) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:paperflip")), 0.5f, this.rand.nextFloat() * 0.4f + 0.9f);
						Vec3d shootvec = user.getLookVec();
						if (user instanceof EntityLiving && ((EntityLiving)user).getAttackTarget() != null) {
							shootvec = ((EntityLiving)user).getAttackTarget().getPositionEyes(1f).subtract(user.getPositionEyes(1f));
							shootvec = shootvec.addVector(0, MathHelper.sqrt(shootvec.x * shootvec.x + shootvec.z * shootvec.z) * 0.1d, 0);
						}
						for (int i = 0; i < 3; i++) {
							Vec3d vec = new Vec3d((this.rand.nextFloat()-0.5f) * 5f, 0.8f + this.rand.nextFloat() * 0.4f, 0.0f)
							 .rotateYaw(-(float)Math.toRadians(user.renderYawOffset)).add(user.getPositionVector());
							EntityPaperArrow entityarrow = new EntityPaperArrow(user);
							entityarrow.setPosition(vec.x, vec.y, vec.z);
							entityarrow.shoot(shootvec.x, shootvec.y, shootvec.z, 2.0f, 0.05f);
							this.world.spawnEntity(entityarrow);
						}
					}
					if (this.ticksExisted > this.waitTime) {
						boolean newPressed = user.getEntityData().getBoolean(NarutomodModVariables.JutsuKey2Pressed);
						if (this.jutsuKey2Pressed && !newPressed && chakra.getAmount() >= this.chakraUsage) {
							RayTraceResult targetRT = user instanceof EntityLiving && ((EntityLiving)user).getAttackTarget() != null
							 ? new RayTraceResult(((EntityLiving)user).getAttackTarget())
							 : ProcedureUtils.objectEntityLookingAt(user, 30d, 3d, true, true, EntityShikigami.EC.class);
							if (targetRT != null && targetRT.entityHit != null) {
								EntityPaperBind.EC entity1 = EntityPaperBind.EC.Jutsu.createJutsu(user, targetRT.entityHit);
								if (entity1 != null) {
									chakra.consume(this.chakraUsage);
									this.bindEntity = entity1;
								}
							}
						}
						this.jutsuKey2Pressed = newPressed;
					}
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		public boolean isShooting() {
			return this.isShooting;
		}

		public boolean isBinding() {
			return this.bindEntity != null && this.bindEntity.isEntityAlive();
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
					this.createJutsu(entity, ItemNinjutsu.SHIKIGAMI.chakraUsage);
					return true;
				} else {
					entity1.setDead();
				}
				return false;
			}

			@Nullable
			public static EC createJutsu(EntityLivingBase entity, double chakraUsage) {
				if (Chakra.pathway(entity).getAmount() >= chakraUsage) {
					EC entity1 = new EC(entity, chakraUsage);
					entity.world.spawnEntity(entity1);
					return entity1;
				}
				return null;
			}
		}
	}

	public static class EntityPaperArrow extends EntityArrow implements ItemJutsu.IJutsu {
		public EntityPaperArrow(World a) {
			super(a);
		}

		public EntityPaperArrow(EntityLivingBase shooter) {
			super(shooter.world, shooter);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.NINJUTSU;
		}

		@Override
		protected void onHit(RayTraceResult raytraceResultIn) {
			Entity entity = raytraceResultIn.entityHit;
			if (entity != null) {
				float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
				int i = MathHelper.ceil((double)f * this.getDamage());
				if (this.getIsCritical()) {
					i += this.rand.nextInt(i / 2 + 2);
				}
				entity.hurtResistantTime = 10;
				if (entity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), (float)i)) {
					if (this.shootingEntity != null && entity != this.shootingEntity && entity instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
						((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
					}
					this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
				} else {
					this.motionX *= -0.1D;
					this.motionY *= -0.1D;
					this.motionZ *= -0.1D;
					this.rotationYaw += 180.0F;
					this.prevRotationYaw += 180.0F;
					ReflectionHelper.setPrivateValue(EntityArrow.class, this, 0, 13); //this.ticksInAir = 0;
				}
			} else {
				super.onHit(raytraceResultIn);
			}
		}

		@Override
		protected ItemStack getArrowStack() {
			return ItemStack.EMPTY;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.timeInGround > 40) {
				this.setDead();
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
			RenderingRegistry.registerEntityRenderingHandler(EntityPaperArrow.class, renderManager -> new RenderPaperArrow(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/paperwings.png");
			private final ModelPaperWings model = new ModelPaperWings();
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float pt) {
				EntityLivingBase user = entity.getSummoner();
				if (user != null && (!user.isInvisible() || !user.isInvisibleToPlayer(Minecraft.getMinecraft().player))) {
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
					if (user.isSneaking()) {
						y -= 0.125F;
					}
					GlStateManager.pushMatrix();
					GlStateManager.translate(x, y, z);
					float f4 = userRenderer.prepareScale(user, pt);
					GlStateManager.rotate(f1 - 180F, 0.0F, 1.0F, 0.0F);
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					GlStateManager.translate(f * 0.01, 0.0F, 0.0F);
					GlStateManager.matrixMode(5888);
					GlStateManager.disableCull();
					this.renderModel(userRenderer.getMainModel(), f6, f5, f, f3, f7, f4, user, entity);
		            GlStateManager.enableCull();
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					GlStateManager.matrixMode(5888);
		            GlStateManager.popMatrix();
				}
			}
	
			private void renderModel(ModelBase modelIn, float f0, float f1, float f2, float f3, float f4, float f5, Entity userIn, EC entity) {
				if (modelIn instanceof ModelBiped) {
					ModelBiped userModel = (ModelBiped)modelIn;
					ModelBase.copyModelAngles(userModel.bipedHead, this.model.bipedHead);
					ModelBase.copyModelAngles(userModel.bipedBody, this.model.bipedBody);
					ModelBase.copyModelAngles(userModel.bipedRightArm, this.model.bipedRightArm);
					ModelBase.copyModelAngles(userModel.bipedLeftArm, this.model.bipedLeftArm);
					ModelBase.copyModelAngles(userModel.bipedRightLeg, this.model.bipedRightLeg);
					ModelBase.copyModelAngles(userModel.bipedLeftLeg, this.model.bipedLeftLeg);
			        //GlStateManager.pushMatrix();
		            if (userIn.isSneaking()) {
		                GlStateManager.translate(0.0F, 0.2F, 0.0F);
		            }
		            float progress = f2 / (float)entity.waitTime;
		            if (progress <= 1.0F) {
						this.model.leftWing.rotateAngleY = 0.0F;
						this.model.rightWing.rotateAngleY = 0.0F;
		            	this.model.setRotationAngle(this.model.rightJoint2, -1.0472F, -0.7854F, 0.0F);
		            	this.model.setRotationAngle(this.model.leftJoint2, -1.0472F, 0.7854F, 0.0F);
						int i = (int)((float)this.model.rightBone.length * Math.min(progress, 1.0F));
						for (int j = 0; j < this.model.rightBone.length; j++) {
							this.model.rightBone[j].showModel = j <= i;
							this.model.leftBone[j].showModel = j <= i;
						}
			            this.model.bipedHead.render(f5);
			            this.model.bipedRightArm.render(f5);
			            this.model.bipedLeftArm.render(f5);
			            this.model.bipedRightLeg.render(f5);
			            this.model.bipedLeftLeg.render(f5);
		            } else {
						float wingFlapAngle = MathHelper.cos((f2 - 3.0f) * 0.3F) * 0.845f;
						this.model.leftJoint2.rotateAngleY = -wingFlapAngle + 0.5236f;
						this.model.rightJoint2.rotateAngleY = wingFlapAngle - 0.5236f;
						wingFlapAngle = MathHelper.cos((f2 - 3.0f) * 0.3F) * 0.65f;
						this.model.leftJoint2.rotateAngleX = -0.7854f + wingFlapAngle;
						this.model.rightJoint2.rotateAngleX = -0.7854f + wingFlapAngle;
						wingFlapAngle = MathHelper.cos(f2 * 0.3F) * 0.65f;
						this.model.leftWing.rotateAngleY = -wingFlapAngle;
						this.model.rightWing.rotateAngleY = wingFlapAngle;
						this.model.chest.showModel = !(this.renderManager.renderViewEntity == userIn && this.renderManager.options.thirdPersonView == 0);
		            }
		            this.model.bipedBody.render(f5);
		        	//GlStateManager.popMatrix();
				} else {
					modelIn.setRotationAngles(f0, f1, f2, f3, f4, f5, userIn);
					modelIn.render(userIn, f0, f1, f2, f3, f4, f5);
				}
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderPaperArrow extends Render<EntityPaperArrow> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/paper_arrow.png");
			private final ModelPaperArrow model = new ModelPaperArrow();
	
			public RenderPaperArrow(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
		    @Override
		    public void doRender(EntityPaperArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
		        this.bindEntityTexture(entity);
		        GlStateManager.pushMatrix();
		        GlStateManager.translate((float)x, (float)y, (float)z);
		        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 180F, 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
		        GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		        GlStateManager.disableCull();
		        this.model.render(entity, 0.0F, 0.0F, partialTicks + entity.ticksExisted, 0.0F, 0.0F, 0.0625F);
		        GlStateManager.enableCull();
		        GlStateManager.popMatrix();
		    }
		
			@Override
		    protected ResourceLocation getEntityTexture(EntityPaperArrow entity) {
		        return this.texture;
		    }
		}

		@SideOnly(Side.CLIENT)
		public class ModelPaperWings extends ModelBiped {
			private final ModelRenderer rightWing;
			private final ModelRenderer rightJoint1;
			private final ModelRenderer rightJoint2;
			private final ModelRenderer[] rightBone = new ModelRenderer[14];
			private final ModelRenderer leftWing;
			private final ModelRenderer leftJoint1;
			private final ModelRenderer leftJoint2;
			private final ModelRenderer[] leftBone = new ModelRenderer[14];
			private final ModelRenderer chest;
			
			public ModelPaperWings() {
				textureWidth = 128;
				textureHeight = 64;

				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 4, 0, -4.0F, -8.0F, -4.0F, 4, 8, 8, 0.25F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 100, 2, 0.0F, -8.0F, -4.0F, 4, 8, 8, 0.25F, true));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 95, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.3F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				rightWing = new ModelRenderer(this);
				rightWing.setRotationPoint(-3.84F, 0.1435F, 1.6884F);
				bipedBody.addChild(rightWing);
				
		
				rightJoint1 = new ModelRenderer(this);
				rightJoint1.setRotationPoint(0.0F, 0.0F, 0.0F);
				rightWing.addChild(rightJoint1);
				setRotationAngle(rightJoint1, 0.0F, -1.0472F, 0.5236F);
				
		
				rightBone[0] = new ModelRenderer(this);
				rightBone[0].setRotationPoint(4.5282F, 7.5F, 8.1154F);
				rightJoint1.addChild(rightBone[0]);
				rightBone[0].cubeList.add(new ModelBox(rightBone[0], 0, 20, -4.4622F, -7.6435F, -8.7273F, 6, 12, 4, 0.0F, false));
		
				rightBone[1] = new ModelRenderer(this);
				rightBone[1].setRotationPoint(4.5282F, 7.5F, 8.1154F);
				rightJoint1.addChild(rightBone[1]);
				rightBone[1].cubeList.add(new ModelBox(rightBone[1], 3, 5, -4.4622F, -7.6435F, -8.7273F, 6, 12, 8, -0.2F, false));
		
				rightBone[2] = new ModelRenderer(this);
				rightBone[2].setRotationPoint(4.5282F, 7.5F, 8.1154F);
				rightJoint1.addChild(rightBone[2]);
				rightBone[2].cubeList.add(new ModelBox(rightBone[2], 2, 19, -4.4622F, -7.6435F, -8.7273F, 6, 12, 12, -0.4F, false));
		
				rightBone[3] = new ModelRenderer(this);
				rightBone[3].setRotationPoint(4.5282F, 7.5F, 8.1154F);
				rightJoint1.addChild(rightBone[3]);
				rightBone[3].cubeList.add(new ModelBox(rightBone[3], 13, 0, -4.4622F, -7.6435F, -8.7273F, 6, 12, 16, -0.7F, false));
		
				rightBone[4] = new ModelRenderer(this);
				rightBone[4].setRotationPoint(4.5282F, 7.5F, 8.1154F);
				rightJoint1.addChild(rightBone[4]);
				rightBone[4].cubeList.add(new ModelBox(rightBone[4], 71, 0, -4.4622F, -7.6435F, -8.7273F, 6, 12, 20, -1.2F, false));
		
				rightJoint2 = new ModelRenderer(this);
				rightJoint2.setRotationPoint(2.959F, 4.9778F, 14.9858F);
				rightJoint1.addChild(rightJoint2);
				setRotationAngle(rightJoint2, -1.0472F, -0.7854F, 0.0F);
				
		
				rightBone[5] = new ModelRenderer(this);
				rightBone[5].setRotationPoint(1.5897F, 0.6998F, 0.1128F);
				rightJoint2.addChild(rightBone[5]);
				rightBone[5].cubeList.add(new ModelBox(rightBone[5], 27, 25, -3.434F, -5.1998F, 0.2895F, 4, 11, 8, 0.0F, false));
		
				rightBone[6] = new ModelRenderer(this);
				rightBone[6].setRotationPoint(1.5897F, 0.6998F, 0.1128F);
				rightJoint2.addChild(rightBone[6]);
				rightBone[6].cubeList.add(new ModelBox(rightBone[6], 63, 0, -3.434F, -5.1998F, 0.2895F, 4, 11, 12, -0.15F, false));
		
				rightBone[7] = new ModelRenderer(this);
				rightBone[7].setRotationPoint(1.5897F, 0.6998F, 0.1128F);
				rightJoint2.addChild(rightBone[7]);
				rightBone[7].cubeList.add(new ModelBox(rightBone[7], 47, 6, -3.434F, -5.1998F, 0.2895F, 4, 11, 16, -0.3F, false));
		
				rightBone[8] = new ModelRenderer(this);
				rightBone[8].setRotationPoint(1.5897F, 0.6998F, 0.1128F);
				rightJoint2.addChild(rightBone[8]);
				rightBone[8].cubeList.add(new ModelBox(rightBone[8], 7, 0, -3.434F, -5.1998F, 0.2895F, 4, 11, 20, -0.45F, false));
		
				rightBone[9] = new ModelRenderer(this);
				rightBone[9].setRotationPoint(1.5897F, 0.6998F, 0.1128F);
				rightJoint2.addChild(rightBone[9]);
				setRotationAngle(rightBone[9], -0.0349F, 0.0F, 0.0F);
				rightBone[9].cubeList.add(new ModelBox(rightBone[9], 72, 7, -3.434F, -5.1998F, 0.2895F, 4, 11, 24, -0.6F, false));
		
				rightBone[10] = new ModelRenderer(this);
				rightBone[10].setRotationPoint(1.0897F, 1.1998F, 0.1128F);
				rightJoint2.addChild(rightBone[10]);
				setRotationAngle(rightBone[10], -0.0698F, 0.0F, 0.0F);
				rightBone[10].cubeList.add(new ModelBox(rightBone[10], 1, 1, -2.434F, -4.1998F, 1.2895F, 3, 9, 27, 0.0F, false));
		
				rightBone[11] = new ModelRenderer(this);
				rightBone[11].setRotationPoint(-0.4103F, 0.6998F, 0.1128F);
				rightJoint2.addChild(rightBone[11]);
				setRotationAngle(rightBone[11], -0.1047F, 0.0F, 0.0F);
				rightBone[11].cubeList.add(new ModelBox(rightBone[11], 64, 4, -0.434F, -2.1998F, 1.2895F, 2, 7, 30, 0.0F, false));
		
				rightBone[12] = new ModelRenderer(this);
				rightBone[12].setRotationPoint(0.0897F, 1.1998F, 0.1128F);
				rightJoint2.addChild(rightBone[12]);
				setRotationAngle(rightBone[12], -0.1396F, 0.0F, 0.0F);
				rightBone[12].cubeList.add(new ModelBox(rightBone[12], 26, 3, -0.434F, -1.1998F, 1.2895F, 1, 5, 32, 0.0F, false));
		
				rightBone[13] = new ModelRenderer(this);
				rightBone[13].setRotationPoint(0.5897F, 0.6998F, 0.6128F);
				rightJoint2.addChild(rightBone[13]);
				setRotationAngle(rightBone[13], -0.1745F, 0.0F, 0.0F);
				rightBone[13].cubeList.add(new ModelBox(rightBone[13], 0, -34, -0.434F, 0.8002F, 1.2895F, 0, 3, 34, 0.0F, false));
		
				leftWing = new ModelRenderer(this);
				leftWing.setRotationPoint(3.84F, 0.1435F, 1.6884F);
				bipedBody.addChild(leftWing);
				
		
				leftJoint1 = new ModelRenderer(this);
				leftJoint1.setRotationPoint(0.0F, 0.0F, 0.0F);
				leftWing.addChild(leftJoint1);
				setRotationAngle(leftJoint1, 0.0F, 1.0472F, -0.5236F);
				
		
				leftBone[0] = new ModelRenderer(this);
				leftBone[0].setRotationPoint(-4.5282F, 7.5F, 8.1154F);
				leftJoint1.addChild(leftBone[0]);
				leftBone[0].cubeList.add(new ModelBox(leftBone[0], 0, 20, -1.5378F, -7.6435F, -8.7273F, 6, 12, 4, 0.0F, true));
		
				leftBone[1] = new ModelRenderer(this);
				leftBone[1].setRotationPoint(-4.5282F, 7.5F, 8.1154F);
				leftJoint1.addChild(leftBone[1]);
				leftBone[1].cubeList.add(new ModelBox(leftBone[1], 3, 5, -1.5378F, -7.6435F, -8.7273F, 6, 12, 8, -0.2F, true));
		
				leftBone[2] = new ModelRenderer(this);
				leftBone[2].setRotationPoint(-4.5282F, 7.5F, 8.1154F);
				leftJoint1.addChild(leftBone[2]);
				leftBone[2].cubeList.add(new ModelBox(leftBone[2], 2, 19, -1.5378F, -7.6435F, -8.7273F, 6, 12, 12, -0.4F, true));
		
				leftBone[3] = new ModelRenderer(this);
				leftBone[3].setRotationPoint(-4.5282F, 7.5F, 8.1154F);
				leftJoint1.addChild(leftBone[3]);
				leftBone[3].cubeList.add(new ModelBox(leftBone[3], 13, 0, -1.5378F, -7.6435F, -8.7273F, 6, 12, 16, -0.7F, true));
		
				leftBone[4] = new ModelRenderer(this);
				leftBone[4].setRotationPoint(-4.5282F, 7.5F, 8.1154F);
				leftJoint1.addChild(leftBone[4]);
				leftBone[4].cubeList.add(new ModelBox(leftBone[4], 71, 0, -1.5378F, -7.6435F, -8.7273F, 6, 12, 20, -1.2F, true));
		
				leftJoint2 = new ModelRenderer(this);
				leftJoint2.setRotationPoint(-2.959F, 4.9778F, 14.9858F);
				leftJoint1.addChild(leftJoint2);
				setRotationAngle(leftJoint2, -1.0472F, 0.7854F, 0.0F);
				
		
				leftBone[5] = new ModelRenderer(this);
				leftBone[5].setRotationPoint(-1.5897F, 0.6998F, 0.1128F);
				leftJoint2.addChild(leftBone[5]);
				leftBone[5].cubeList.add(new ModelBox(leftBone[5], 27, 25, -0.566F, -5.1998F, 0.2895F, 4, 11, 8, 0.0F, true));
		
				leftBone[6] = new ModelRenderer(this);
				leftBone[6].setRotationPoint(-1.5897F, 0.6998F, 0.1128F);
				leftJoint2.addChild(leftBone[6]);
				leftBone[6].cubeList.add(new ModelBox(leftBone[6], 63, 0, -0.566F, -5.1998F, 0.2895F, 4, 11, 12, -0.15F, true));
		
				leftBone[7] = new ModelRenderer(this);
				leftBone[7].setRotationPoint(-1.5897F, 0.6998F, 0.1128F);
				leftJoint2.addChild(leftBone[7]);
				leftBone[7].cubeList.add(new ModelBox(leftBone[7], 47, 6, -0.566F, -5.1998F, 0.2895F, 4, 11, 16, -0.3F, true));
		
				leftBone[8] = new ModelRenderer(this);
				leftBone[8].setRotationPoint(-1.5897F, 0.6998F, 0.1128F);
				leftJoint2.addChild(leftBone[8]);
				leftBone[8].cubeList.add(new ModelBox(leftBone[8], 7, 0, -0.566F, -5.1998F, 0.2895F, 4, 11, 20, -0.45F, true));
		
				leftBone[9] = new ModelRenderer(this);
				leftBone[9].setRotationPoint(-1.5897F, 0.6998F, 0.1128F);
				leftJoint2.addChild(leftBone[9]);
				setRotationAngle(leftBone[9], -0.0349F, 0.0F, 0.0F);
				leftBone[9].cubeList.add(new ModelBox(leftBone[9], 72, 7, -0.566F, -5.1998F, 0.2895F, 4, 11, 24, -0.6F, true));
		
				leftBone[10] = new ModelRenderer(this);
				leftBone[10].setRotationPoint(-1.0897F, 1.1998F, 0.1128F);
				leftJoint2.addChild(leftBone[10]);
				setRotationAngle(leftBone[10], -0.0698F, 0.0F, 0.0F);
				leftBone[10].cubeList.add(new ModelBox(leftBone[10], 1, 1, -0.566F, -4.1998F, 1.2895F, 3, 9, 27, 0.0F, true));
		
				leftBone[11] = new ModelRenderer(this);
				leftBone[11].setRotationPoint(0.4103F, 0.6998F, 0.1128F);
				leftJoint2.addChild(leftBone[11]);
				setRotationAngle(leftBone[11], -0.1047F, 0.0F, 0.0F);
				leftBone[11].cubeList.add(new ModelBox(leftBone[11], 64, 4, -1.566F, -2.1998F, 1.2895F, 2, 7, 30, 0.0F, true));
		
				leftBone[12] = new ModelRenderer(this);
				leftBone[12].setRotationPoint(-0.0897F, 1.1998F, 0.1128F);
				leftJoint2.addChild(leftBone[12]);
				setRotationAngle(leftBone[12], -0.1396F, 0.0F, 0.0F);
				leftBone[12].cubeList.add(new ModelBox(leftBone[12], 26, 3, -0.566F, -1.1998F, 1.2895F, 1, 5, 32, 0.0F, true));
		
				leftBone[13] = new ModelRenderer(this);
				leftBone[13].setRotationPoint(-0.5897F, 0.6998F, 0.6128F);
				leftJoint2.addChild(leftBone[13]);
				setRotationAngle(leftBone[13], -0.1745F, 0.0F, 0.0F);
				leftBone[13].cubeList.add(new ModelBox(leftBone[13], 0, -34, 0.434F, 0.8002F, 1.2895F, 0, 3, 34, 0.0F, true));
		
				chest = new ModelRenderer(this);
				chest.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(chest);
				chest.cubeList.add(new ModelBox(chest, 52, 0, -4.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
				chest.cubeList.add(new ModelBox(chest, 108, 13, 0.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, true));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 22, 9, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
		
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 31, 19, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, true));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 71, 13, -2.1F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 105, 13, -1.9F, 0.0F, -2.0F, 4, 12, 4, 0.25F, true));
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelPaperArrow extends ModelBase {
			private final ModelRenderer bone;
		
			public ModelPaperArrow() {
				textureWidth = 32;
				textureHeight = 32;
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, -16, 4, -3.0F, 0.0F, -8.0F, 6, 0, 16, 0.0F, false));
				bone.cubeList.add(new ModelBox(bone, 0, -16, 0.0F, 0.0F, -8.0F, 0, 2, 16, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				bone.render(f5);
			}
		}
	}
}

