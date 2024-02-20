package net.narutomod.entity;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureTotsukaSwordToolInHandTick;
import net.narutomod.procedure.ProcedureKagutsuchiSwordToolInUseTick;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemKamuiShuriken;
import net.narutomod.item.ItemKagutsuchiSwordRanged;
import net.narutomod.item.ItemTotsukaSword;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySusanooWinged extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 42;
	public static final int ENTITYID_RANGED = 43;
	private static final float MODELSCALE = 8.0F;
	
	public EntitySusanooWinged(ElementsNarutomodMod instance) {
		super(instance, 232);
	}

	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "susanoowinged"), ENTITYID).name("susanoowinged").tracker(96, 1, true).build());
	}

	public static class EntityCustom extends EntitySusanooBase {
		private static final DataParameter<Float> WINGSWING = EntityDataManager.<Float>createKey(EntityCustom.class, DataSerializers.FLOAT);
		private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Boolean> SHOW_SWORD = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Float> MOTION_X = EntityDataManager.<Float>createKey(EntityCustom.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> MOTION_Z = EntityDataManager.<Float>createKey(EntityCustom.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> HEAD_YAW = EntityDataManager.<Float>createKey(EntityCustom.class, DataSerializers.FLOAT);
		private static final AttributeModifier SWORD_REACH = new AttributeModifier("susanoo.swordReachExtension", 2.0D, 0);
		private static final AttributeModifier SWORD_ATTACK = new AttributeModifier("susanoo.swordAttackDamage", 1.2D, 1);
		private final ItemStack kagutsuchi = new ItemStack(ItemKagutsuchiSwordRanged.block);
		private final ItemStack kamuiShuriken = new ItemStack(ItemKamuiShuriken.block);
		private boolean isWingExtending;
		private boolean isWingDetracting;
		private int wingSwingProgressInt;
		private EntitySusanooClothed.EntityMagatama bulletEntity;

		public EntityCustom(World world) {
			super(world);
			this.setSize(MODELSCALE * 0.8f, MODELSCALE * 2.0f);
			this.getEntityData().setDouble("entityModelScale", (double)MODELSCALE);
			this.chakraUsage = 90d;
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			this.setSize(MODELSCALE * 0.8f, MODELSCALE * 2.0f);
			this.stepHeight = this.height / 3.0F;
			//this.setFlameColor(0x20b83dba);
			this.chakraUsage = 90d;
			this.wingSwingProgressInt = 0;
			this.isWingDetracting = false;
			this.isWingExtending = false;
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(new AttributeModifier("susanoo.reachExtension", 12.0D, 0));
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier("susanoo.speedboost", 0.5D, 0));
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("susanoo.maxhealth", 43d, 2));
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(this.playerXp * 0.003d);
			this.getEntityData().setDouble("entityModelScale", (double)MODELSCALE);
			Item helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem();
			if (helmet == ItemMangekyoSharingan.helmet || helmet == ItemMangekyoSharinganEternal.helmet) {
				//this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemKagutsuchiSwordRanged.block));
				ItemHandlerHelper.giveItemToPlayer(player, kagutsuchi);
				//this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(new AttributeModifier("susanoo.sword.damage", 300d, 0));
			}
			if (helmet == ItemMangekyoSharinganObito.helmet || helmet == ItemMangekyoSharinganEternal.helmet) {
				//this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ItemKamuiShuriken.block));
				ItemHandlerHelper.giveItemToPlayer(player, kamuiShuriken);
			}
			this.setHealth(this.getMaxHealth());
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(WINGSWING, Float.valueOf(0.0F));
			this.dataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
			this.dataManager.register(SHOW_SWORD, Boolean.valueOf(false));
			this.dataManager.register(MOTION_X, Float.valueOf(0.0F));
			this.dataManager.register(MOTION_Z, Float.valueOf(0.0F));
			this.dataManager.register(HEAD_YAW, Float.valueOf(0.0F));
		}

		private void setWingSwingProgress(float f) {
			this.dataManager.set(WINGSWING, Float.valueOf(f));
		}

		private float getWingSwingProgress() {
			return ((Float)this.dataManager.get(WINGSWING)).floatValue();
		}

		protected void setOwnerPlayer(EntityPlayer entity) {
			super.setOwnerPlayer(entity);
		}

		@Override
		public boolean shouldShowSword() {
			return ((Boolean) this.getDataManager().get(SHOW_SWORD)).booleanValue();
		}

		@Override
	    public void setShowSword(boolean show) {
	    	if (this.getHeldItemMainhand().isEmpty()) {
		    	if (show) {
	    			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
					this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(SWORD_REACH);
					this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(SWORD_ATTACK);
		    	} else {
		    		this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(SWORD_REACH);
		    		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(SWORD_ATTACK);
		    	}
		    	this.getDataManager().set(SHOW_SWORD, Boolean.valueOf(show));
	    	}
	    }

	    private void setMotionXZ(float x, float z, float headYaw) {
	    	this.dataManager.set(MOTION_X, Float.valueOf(x));
	    	this.dataManager.set(MOTION_Z, Float.valueOf(z));
	    	this.dataManager.set(HEAD_YAW, Float.valueOf(headYaw));
	    }

	    private ProcedureUtils.Vec2f getMotionXZ() {
	    	return new ProcedureUtils.Vec2f(((Float)this.dataManager.get(MOTION_X)).floatValue(),
	    	 ((Float)this.dataManager.get(MOTION_Z)).floatValue());
	    }

	    private float getHeadYaw() {
	    	return ((Float)this.dataManager.get(HEAD_YAW)).floatValue();
	    }

		@Override
		public double getMountedYOffset() {
			return 14.0D;
		}

		protected int getWingSwingAnimationEnd() {
			return 60;
		}

		protected void extendWings() {
			this.isWingDetracting = false;
			this.isWingExtending = true;
		}

		protected void detractWings() {
			this.isWingExtending = false;
			this.isWingDetracting = true;
		}

		protected void updateWingSwing() {
			int i = this.getWingSwingAnimationEnd();
			if (this.isWingExtending) {
				this.wingSwingProgressInt++;
				if (this.wingSwingProgressInt >= i) {
					this.wingSwingProgressInt = i;
					this.isWingExtending = false;
				}
			}
			if (this.isWingDetracting) {
				this.wingSwingProgressInt--;
				if (this.wingSwingProgressInt <= 0) {
					this.wingSwingProgressInt = 0;
					this.isWingDetracting = false;
				}
			}
			this.setWingSwingProgress((float) this.wingSwingProgressInt / (float) i);
		}

		@Override
		public void setDead() {
			if (this.getOwnerPlayer() instanceof EntityPlayer) {
				((EntityPlayer)this.getOwnerPlayer()).inventory.clearMatchingItems(ItemKagutsuchiSwordRanged.block, -1, -1, null);
				((EntityPlayer)this.getOwnerPlayer()).inventory.clearMatchingItems(ItemKamuiShuriken.block, -1, -1, null);
			}
			super.setDead();
	    	this.killBullet();
		}

		@Override
		protected void showHeldWeapons() {
			super.showHeldWeapons();
			EntityLivingBase owner = this.getOwnerPlayer();
			if (owner != null) {
				ItemStack ownerheldstack = owner.getHeldItemMainhand();
				ItemStack thisHeldstack = this.getHeldItemMainhand();
				if (ownerheldstack.getItem() == ItemTotsukaSword.block) {
				 	if (thisHeldstack.getItem() != ItemTotsukaSword.block) {
				 		thisHeldstack = ownerheldstack.copy();
						this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, thisHeldstack);
						this.setShowSword(false);
				 	}
				} else if (ownerheldstack.getItem() == kagutsuchi.getItem()) {
					if (thisHeldstack.getItem() != kagutsuchi.getItem()) {
						this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, kagutsuchi);
						thisHeldstack = kagutsuchi;
						this.setShowSword(false);
					}
				} else if (!thisHeldstack.isEmpty()) {
					this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
				if (ownerheldstack.getItem() == kamuiShuriken.getItem()) {
					if (this.getHeldItemOffhand().getItem() != kamuiShuriken.getItem()) {
						this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, kamuiShuriken);
					}
				} else {
					this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
				}
				if (thisHeldstack.getItem() == ItemTotsukaSword.block) {
					HashMap<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", this);
					$_dependencies.put("itemstack", this.getHeldItemMainhand());
					$_dependencies.put("world", this.world);
					ProcedureTotsukaSwordToolInHandTick.executeProcedure($_dependencies);
				} else if (thisHeldstack.getItem() == kagutsuchi.getItem()) {
					HashMap<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", this);
					$_dependencies.put("itemstack", thisHeldstack);
					$_dependencies.put("world", this.world);
					ProcedureKagutsuchiSwordToolInUseTick.executeProcedure($_dependencies);
				}
			}
		}

		@Override
		public void onEntityUpdate() {
			this.showHeldWeapons();
			this.updateWingSwing();
			super.onEntityUpdate();
		}

		@Override
		public void travel(float ti, float tj, float tk) {
			if (this.isBeingRidden()) {
				EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
				if ((!this.onGround || entity.rotationPitch < 0.0F) && entity.moveForward > 0.0F) {
					this.motionY -= entity.rotationPitch / 45.0D;
				}
				if (!this.onGround) {
					this.extendWings();
				} else {
					this.detractWings();
				}
			}
			super.travel(ti, tj, tk);
			this.setMotionXZ((float)(this.posX - this.prevPosX), (float)(this.posZ - this.prevPosZ), this.rotationYawHead);
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			if (!this.world.isRemote && entity instanceof EntityLivingBase && !entity.equals(this.getOwnerPlayer())) {
				if (this.getOwnerPlayer() != null 
				 && this.getOwnerPlayer().getHeldItemMainhand().getItem() == ItemKagutsuchiSwordRanged.block)
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, 200, 2, false, false));
			}
			super.collideWithEntity(entity);
		}

	    @SideOnly(Side.CLIENT)
	    public boolean isSwingingArms() {
	        return ((Boolean)this.dataManager.get(SWINGING_ARMS)).booleanValue();
	    }
	
	    @Override
	    public void setSwingingArms(boolean swingingArms) {
	        this.dataManager.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
	    }

	    @Override
	    public void attackEntityRanged(double x, double y, double z) {
	    	if (this.bulletEntity == null) {
	    		this.createBullet(MODELSCALE * 0.5f);
	    	}
	    	this.bulletEntity.shoot(x, y, z, 0.99f, 0.0f);
	    	this.bulletEntity = null;
	    	this.setSwingingArms(false);
	    }

	    @Override
	    public void createBullet(float size) {
	    	this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
	    	this.setSwingingArms(true);
	    	if (this.bulletEntity == null) {
	    		this.bulletEntity = new EntitySusanooClothed.EntityMagatama(this, this.getFlameColor(), size);
	    		this.world.spawnEntity(this.bulletEntity);
	    	} else if (this.bulletEntity.getEntityScale() != size) {
	    		this.bulletEntity.setEntityScale(size);
	    	}
	    }

	    @Override
	    public void killBullet() {
	    	if (this.bulletEntity != null) {
	    		this.bulletEntity.setDead();
	    		this.bulletEntity = null;
	    	}
	    	this.setSwingingArms(false);
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderSusanooWinged(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderSusanooWinged extends RenderLiving<EntityCustom> {
			private final ResourceLocation mainTexture = new ResourceLocation("narutomod:textures/susanoo_winged.png");
			private final ResourceLocation flameTexture = new ResourceLocation("narutomod:textures/gas256.png");
	
			public RenderSusanooWinged(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelSusanooWinged(), MODELSCALE * 0.5F);
				this.addLayer(new LayerHeldItem(this));
			}
	
			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.isBeingRidden() && entity.getControllingPassenger() instanceof AbstractClientPlayer) {
					this.copyLimbSwing(entity, (AbstractClientPlayer) entity.getControllingPassenger());
				}
				this.setModelVisibilities(entity);
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
			private void copyLimbSwing(EntityCustom entity, AbstractClientPlayer rider) {
				entity.swingProgress = rider.swingProgress;
				entity.swingProgressInt = rider.swingProgressInt;
				entity.prevSwingProgress = rider.prevSwingProgress;
				entity.isSwingInProgress = rider.isSwingInProgress;
				entity.swingingHand = rider.swingingHand;
			}
	
			private void setModelVisibilities(EntityCustom entity) {
				ModelSusanooWinged model = (ModelSusanooWinged) this.getMainModel();
				model.wingSwingProgress = entity.getWingSwingProgress();
				model.setVisible(true);
				if (Minecraft.getMinecraft().getRenderViewEntity().equals(entity.getControllingPassenger())
						&& this.renderManager.options.thirdPersonView == 0) {
					model.bipedHead.showModel = false;
					model.bipedHeadwear.showModel = false;
				}
				model.rightArmPose = entity.getHeldItemMainhand().isEmpty() ? ModelBiped.ArmPose.EMPTY : ModelBiped.ArmPose.ITEM;
				model.sword.showModel = entity.shouldShowSword();
			}
	
			@Override
			protected void renderModel(EntityCustom entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
	            if (this.bindEntityTexture(entity)) {
					if (!entity.onGround) {
						limbSwingAmount = 0f;
						if (this.isMovingTowardsLookDirection(entity)) {
							headPitch += this.getFlyingBodyRotationAmount(entity) * -90f;
						}
					}
	            	ModelSusanooWinged model = (ModelSusanooWinged)this.getMainModel();
					model.renderFlame = false;
		            this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		            this.bindTexture(this.flameTexture);
					model.renderFlame = true;
		            this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor * 0.99f);
	            }
			}
	
			@Override
			protected void renderLayers(EntityCustom entity, float f0, float limbSwingAmount, float f2, float f3, float f4, float headPitch, float f6) {
				if (!entity.onGround) {
					limbSwingAmount = 0f;
					if (this.isMovingTowardsLookDirection(entity)) {
						headPitch += this.getFlyingBodyRotationAmount(entity) * -90f;
					}
				}
				float modelscale = MODELSCALE;
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * modelscale, 0.0F);
				GlStateManager.scale(modelscale, modelscale, modelscale);
				super.renderLayers(entity, f0, limbSwingAmount, f2, f3, f4, headPitch, f6);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected void applyRotations(EntityCustom entity, float p_77043_2_, float rotationYaw, float partialTicks) {
				super.applyRotations(entity, p_77043_2_, rotationYaw, partialTicks);
				if (!entity.onGround && this.isMovingTowardsLookDirection(entity)) {
			        float f0 = this.getFlyingBodyRotationAmount(entity);
			        float f1 = (1f - MathHelper.cos(f0 * 1.5708f)) * entity.height * 0.75f;
			        float f2 = MathHelper.sin(f0 * 1.5708f) * entity.height * 0.75f;
			        GlStateManager.translate(0f, f1, f2);
			        GlStateManager.rotate(f0 * -90f, 1.0F, 0.0F, 0.0F);
		        }
			}
	
			private float getFlyingBodyRotationAmount(EntityCustom entity) {
	            return MathHelper.clamp(entity.getMotionXZ().lengthVector() * 1.2F, 0.0F, 1.0F);
			}
	
			private boolean isMovingTowardsLookDirection(EntityCustom entity) {
				ProcedureUtils.Vec2f vec = entity.getMotionXZ();
	            float f2 = ProcedureUtils.getYawFromVec(vec.x, vec.y);
	            float f3 = Math.abs(MathHelper.wrapDegrees(f2 - entity.getHeadYaw()));
	            return vec.lengthVector() > 1.0E-6f && f3 < 90f;
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.mainTexture;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelSusanooWinged extends ModelBiped {
			private final ModelRenderer chin_r1;
			private final ModelRenderer bipedHead_r1;
			private final ModelRenderer bipedHead_r2;
			private final ModelRenderer hair;
			private final ModelRenderer Hair_r1;
			private final ModelRenderer Hair_r2;
			private final ModelRenderer Hair_r3;
			private final ModelRenderer Hair_r4;
			private final ModelRenderer Nose;
			private final ModelRenderer bridge_r1;
			private final ModelRenderer HeadDecor;
			private final ModelRenderer Flame1;
			private final ModelRenderer flame2_r1;
			private final ModelRenderer flame3_r1;
			private final ModelRenderer flame3_r2;
			private final ModelRenderer flame2_r2;
			private final ModelRenderer Flame2;
			private final ModelRenderer Flame3;
			private final ModelRenderer Flame4;
			private final ModelRenderer flame3_r3;
			private final ModelRenderer flame4_r1;
			private final ModelRenderer flame4_r2;
			private final ModelRenderer flame3_r4;
			private final ModelRenderer Horn1;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer Flame5;
			private final ModelRenderer Horn2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			//private final ModelRenderer bipedHeadwear;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer BeltPads1;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer bone9;
			private final ModelRenderer BeltPads2;
			private final ModelRenderer bone6;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer rightShoulderPad;
			private final ModelRenderer Shoulderpadlr_r1;
			private final ModelRenderer Shoulderpadll_r1;
			private final ModelRenderer sword;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer leftShoulderPad;
			private final ModelRenderer Shoulderpadlr_r2;
			private final ModelRenderer Shoulderpadll_r2;
			//private final ModelRenderer bipedRightLeg;
			private final ModelRenderer rightDress;
			private final ModelRenderer Dressb_r1;
			private final ModelRenderer Dressf_r1;
			private final ModelRenderer BeltPads3;
			private final ModelRenderer bone18;
			private final ModelRenderer beltpadtr_r1;
			private final ModelRenderer bone19;
			private final ModelRenderer beltpadtr_r2;
			private final ModelRenderer bone20;
			private final ModelRenderer beltpadtr_r3;
			//private final ModelRenderer bipedLeftLeg;
			private final ModelRenderer leftDress;
			private final ModelRenderer Dressb_r2;
			private final ModelRenderer Dressf_r2;
			private final ModelRenderer BeltPads4;
			private final ModelRenderer bone21;
			private final ModelRenderer beltpadtr_r4;
			private final ModelRenderer bone22;
			private final ModelRenderer beltpadtr_r5;
			private final ModelRenderer bone23;
			private final ModelRenderer beltpadtr_r6;
			private final ModelRenderer rightWing;
			private final ModelRenderer bone3;
			private final ModelRenderer rightClaw;
			private final ModelRenderer finger5_r1;
			private final ModelRenderer finger4_r1;
			private final ModelRenderer finger3_r1;
			private final ModelRenderer finger2_r1;
			private final ModelRenderer thumb_r1;
			private final ModelRenderer flap1;
			private final ModelRenderer flap2;
			private final ModelRenderer flap3;
			private final ModelRenderer flap4;
			private final ModelRenderer flap5;
			private final ModelRenderer flap6;
			private final ModelRenderer flap7;
			private final ModelRenderer flap8;
			private final ModelRenderer leftWing;
			private final ModelRenderer bone2;
			private final ModelRenderer leftClaw;
			private final ModelRenderer finger5_r2;
			private final ModelRenderer finger4_r2;
			private final ModelRenderer finger3_r2;
			private final ModelRenderer finger2_r2;
			private final ModelRenderer thumb_r2;
			private final ModelRenderer flap9;
			private final ModelRenderer flap10;
			private final ModelRenderer flap11;
			private final ModelRenderer flap12;
			private final ModelRenderer flap13;
			private final ModelRenderer flap14;
			private final ModelRenderer flap15;
			private final ModelRenderer flap16;
			private final float modelScale = MODELSCALE;
			private final float maxAlpha = 0.9f;
			private boolean renderFlame;
			public float wingSwingProgress;
		
			public ModelSusanooWinged() {
				textureWidth = 128;
				textureHeight = 128;
				this.leftArmPose = ModelBiped.ArmPose.EMPTY;
				this.rightArmPose = ModelBiped.ArmPose.ITEM;
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 3, 3, -4.0F, -8.0F, -4.0F, 8, 8, 7, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 33, 73, -3.5F, -2.1F, -3.8F, 7, 2, 0, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 40, 74, -3.5F, -5.1F, -3.8F, 3, 2, 0, 0.0F, false));
				bipedHead.cubeList.add(new ModelBox(bipedHead, 40, 74, 0.5F, -5.1F, -3.8F, 3, 2, 0, 0.0F, false));
		
				chin_r1 = new ModelRenderer(this);
				chin_r1.setRotationPoint(-1.0F, 1.0F, -2.5F);
				bipedHead.addChild(chin_r1);
				setRotationAngle(chin_r1, 0.3054F, 0.0F, 0.0F);
				chin_r1.cubeList.add(new ModelBox(chin_r1, 32, 25, -1.5F, -1.5F, -1.15F, 5, 1, 4, 0.0F, false));
		
				bipedHead_r1 = new ModelRenderer(this);
				bipedHead_r1.setRotationPoint(3.75F, -1.85F, -2.55F);
				bipedHead.addChild(bipedHead_r1);
				setRotationAngle(bipedHead_r1, 0.0F, -1.5708F, 0.0F);
				bipedHead_r1.cubeList.add(new ModelBox(bipedHead_r1, 40, 74, -1.0F, -1.5F, 0.0F, 3, 3, 0, 0.0F, true));
		
				bipedHead_r2 = new ModelRenderer(this);
				bipedHead_r2.setRotationPoint(-2.0F, -4.1F, -3.8F);
				bipedHead.addChild(bipedHead_r2);
				setRotationAngle(bipedHead_r2, 0.0F, 1.5708F, 0.0F);
				bipedHead_r2.cubeList.add(new ModelBox(bipedHead_r2, 40, 74, -3.25F, 0.75F, -1.75F, 3, 3, 0, 0.0F, false));
		
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, 0.1634F, 3.0945F);
				bipedHead.addChild(hair);
				
		
				Hair_r1 = new ModelRenderer(this);
				Hair_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
				hair.addChild(Hair_r1);
				setRotationAngle(Hair_r1, -1.2654F, 0.0F, 0.0F);
				Hair_r1.cubeList.add(new ModelBox(Hair_r1, 0, 85, -5.0F, -1.5F, -0.25F, 10, 3, 0, 0.0F, false));
		
				Hair_r2 = new ModelRenderer(this);
				Hair_r2.setRotationPoint(0.0F, -1.4142F, 1.4142F);
				hair.addChild(Hair_r2);
				setRotationAngle(Hair_r2, -0.7854F, 0.0F, 0.0F);
				Hair_r2.cubeList.add(new ModelBox(Hair_r2, 0, 85, -5.0F, -1.5F, 0.0F, 10, 3, 0, 0.0F, false));
		
				Hair_r3 = new ModelRenderer(this);
				Hair_r3.setRotationPoint(0.0F, -5.546F, -2.6362F);
				hair.addChild(Hair_r3);
				setRotationAngle(Hair_r3, -1.0036F, 0.0F, 0.0F);
				Hair_r3.cubeList.add(new ModelBox(Hair_r3, 0, 98, -5.0F, -3.6175F, -4.5583F, 10, 0, 10, 0.0F, false));
				Hair_r3.cubeList.add(new ModelBox(Hair_r3, 0, 75, -5.0F, -9.3675F, -4.5583F, 10, 8, 10, -2.0F, false));
				Hair_r3.cubeList.add(new ModelBox(Hair_r3, 0, 75, -5.0F, -5.6175F, -4.5583F, 10, 8, 10, 0.0F, false));
		
				Hair_r4 = new ModelRenderer(this);
				Hair_r4.setRotationPoint(0.0F, -6.9828F, 0.4412F);
				hair.addChild(Hair_r4);
				setRotationAngle(Hair_r4, -1.0036F, 0.0F, 0.0F);
				Hair_r4.cubeList.add(new ModelBox(Hair_r4, 0, 75, -5.0F, -4.0F, -5.0F, 10, 8, 10, -1.0F, false));
		
				Nose = new ModelRenderer(this);
				Nose.setRotationPoint(0.0F, 24.0F, 0.0F);
				bipedHead.addChild(Nose);
				Nose.cubeList.add(new ModelBox(Nose, 0, 0, -0.5F, -27.5F, -7.0F, 1, 1, 3, 0.0F, false));
				Nose.cubeList.add(new ModelBox(Nose, 0, 4, -1.5F, -27.5F, -4.3F, 3, 1, 1, 0.0F, false));
		
				bridge_r1 = new ModelRenderer(this);
				bridge_r1.setRotationPoint(0.0F, -28.0F, -3.8F);
				Nose.addChild(bridge_r1);
				setRotationAngle(bridge_r1, -0.6109F, 0.0F, 0.0F);
				bridge_r1.cubeList.add(new ModelBox(bridge_r1, 5, 0, -0.5F, -0.8F, -0.5F, 1, 2, 1, 0.0F, false));
		
				HeadDecor = new ModelRenderer(this);
				HeadDecor.setRotationPoint(0.0F, 24.0F, 0.0F);
				bipedHead.addChild(HeadDecor);
				
		
				Flame1 = new ModelRenderer(this);
				Flame1.setRotationPoint(-5.75F, -29.0F, -3.0F);
				HeadDecor.addChild(Flame1);
				setRotationAngle(Flame1, 0.7418F, -2.4871F, -1.2654F);
				Flame1.cubeList.add(new ModelBox(Flame1, 0, 60, -1.0F, -3.5F, -1.0F, 2, 5, 0, 0.0F, false));
		
				flame2_r1 = new ModelRenderer(this);
				flame2_r1.setRotationPoint(2.75F, 0.0F, -1.0F);
				Flame1.addChild(flame2_r1);
				setRotationAngle(flame2_r1, -0.48F, -0.1745F, 2.3126F);
				flame2_r1.cubeList.add(new ModelBox(flame2_r1, 0, 18, -0.25F, -4.0F, -1.0F, 2, 6, 0, 0.0F, false));
		
				flame3_r1 = new ModelRenderer(this);
				flame3_r1.setRotationPoint(1.7206F, -0.7186F, -1.2151F);
				Flame1.addChild(flame3_r1);
				setRotationAngle(flame3_r1, -0.2182F, -0.3491F, 0.4363F);
				flame3_r1.cubeList.add(new ModelBox(flame3_r1, 0, 60, -0.5F, -2.0F, 0.25F, 1, 5, 0, 0.0F, false));
		
				flame3_r2 = new ModelRenderer(this);
				flame3_r2.setRotationPoint(-1.9379F, 0.15F, -0.5178F);
				Flame1.addChild(flame3_r2);
				setRotationAngle(flame3_r2, -0.0873F, -0.3491F, -0.7854F);
				flame3_r2.cubeList.add(new ModelBox(flame3_r2, 0, 60, -0.75F, -3.0F, -0.5F, 1, 5, 0, 0.0F, false));
		
				flame2_r2 = new ModelRenderer(this);
				flame2_r2.setRotationPoint(-1.9379F, 0.15F, -0.5178F);
				Flame1.addChild(flame2_r2);
				setRotationAngle(flame2_r2, -0.2182F, -0.3491F, -0.3927F);
				flame2_r2.cubeList.add(new ModelBox(flame2_r2, 0, 60, 0.0F, -3.0F, -0.5F, 1, 5, 0, 0.0F, false));
		
				Flame2 = new ModelRenderer(this);
				Flame2.setRotationPoint(0.0F, 36.0F, 0.0F);
				HeadDecor.addChild(Flame2);
				
		
				Flame3 = new ModelRenderer(this);
				Flame3.setRotationPoint(-1.0F, -33.75F, -2.75F);
				HeadDecor.addChild(Flame3);
				setRotationAngle(Flame3, -0.5236F, 0.0F, -0.2182F);
				Flame3.cubeList.add(new ModelBox(Flame3, 12, 62, -1.0F, -3.0F, 0.0F, 2, 5, 0, 0.0F, false));
		
				Flame4 = new ModelRenderer(this);
				Flame4.setRotationPoint(5.75F, -29.0F, -3.0F);
				HeadDecor.addChild(Flame4);
				setRotationAngle(Flame4, 0.7418F, 2.4871F, 1.2654F);
				Flame4.cubeList.add(new ModelBox(Flame4, 0, 60, -1.0F, -3.5F, -1.0F, 2, 5, 0, 0.0F, true));
		
				flame3_r3 = new ModelRenderer(this);
				flame3_r3.setRotationPoint(-2.75F, 0.0F, -1.0F);
				Flame4.addChild(flame3_r3);
				setRotationAngle(flame3_r3, -0.48F, 0.1745F, -2.3126F);
				flame3_r3.cubeList.add(new ModelBox(flame3_r3, 0, 18, -1.75F, -4.0F, -1.0F, 2, 6, 0, 0.0F, true));
		
				flame4_r1 = new ModelRenderer(this);
				flame4_r1.setRotationPoint(-1.7206F, -0.7186F, -1.2151F);
				Flame4.addChild(flame4_r1);
				setRotationAngle(flame4_r1, -0.2182F, 0.3491F, -0.4363F);
				flame4_r1.cubeList.add(new ModelBox(flame4_r1, 0, 60, -0.5F, -2.0F, 0.25F, 1, 5, 0, 0.0F, true));
		
				flame4_r2 = new ModelRenderer(this);
				flame4_r2.setRotationPoint(1.9379F, 0.15F, -0.5178F);
				Flame4.addChild(flame4_r2);
				setRotationAngle(flame4_r2, -0.0873F, 0.3491F, 0.7854F);
				flame4_r2.cubeList.add(new ModelBox(flame4_r2, 0, 60, -0.25F, -3.0F, -0.5F, 1, 5, 0, 0.0F, true));
		
				flame3_r4 = new ModelRenderer(this);
				flame3_r4.setRotationPoint(1.9379F, 0.15F, -0.5178F);
				Flame4.addChild(flame3_r4);
				setRotationAngle(flame3_r4, -0.2182F, 0.3491F, 0.3927F);
				flame3_r4.cubeList.add(new ModelBox(flame3_r4, 0, 60, -1.0F, -3.0F, -0.5F, 1, 5, 0, 0.0F, true));
		
				Horn1 = new ModelRenderer(this);
				Horn1.setRotationPoint(0.0F, 0.0F, 0.0F);
				HeadDecor.addChild(Horn1);
				
		
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(-2.25F, -31.75F, -4.0F);
				Horn1.addChild(cube_r1);
				setRotationAngle(cube_r1, -0.6109F, 0.0F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 51, 63, -1.25F, -1.0F, -0.5F, 2, 2, 2, 0.0F, false));
		
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(-2.5F, -32.6104F, -5.2287F);
				Horn1.addChild(cube_r2);
				setRotationAngle(cube_r2, -1.0472F, 0.0F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 51, 67, -0.5F, -0.95F, -0.5F, 1, 1, 2, 0.0F, false));
		
				Flame5 = new ModelRenderer(this);
				Flame5.setRotationPoint(1.0F, -33.75F, -2.75F);
				HeadDecor.addChild(Flame5);
				setRotationAngle(Flame5, -0.5236F, 0.0F, 0.2182F);
				Flame5.cubeList.add(new ModelBox(Flame5, 12, 62, -1.0F, -3.0F, 0.0F, 2, 5, 0, 0.0F, true));
		
				Horn2 = new ModelRenderer(this);
				Horn2.setRotationPoint(0.0F, 0.0F, 0.0F);
				HeadDecor.addChild(Horn2);
				
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(2.25F, -31.75F, -4.0F);
				Horn2.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.6109F, 0.0F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 51, 63, -0.75F, -1.0F, -0.5F, 2, 2, 2, 0.0F, true));
		
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(2.5F, -32.6104F, -5.2287F);
				Horn2.addChild(cube_r4);
				setRotationAngle(cube_r4, -1.0472F, 0.0F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 51, 67, -0.5F, -0.95F, -0.5F, 1, 1, 2, 0.0F, true));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 60, 0, -2.95F, -5.0F, -4.1F, 6, 1, 0, 0.0F, false));
		
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 28, 30, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 24, 18, -4.5F, 10.0F, -2.5F, 9, 2, 5, 0.0F, false));
		
				BeltPads1 = new ModelRenderer(this);
				BeltPads1.setRotationPoint(4.0F, 10.5F, 0.0F);
				bipedBody.addChild(BeltPads1);
				
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(-8.0F, 1.0F, 0.0F);
				BeltPads1.addChild(bone7);
				setRotationAngle(bone7, 0.0F, 0.0F, 2.8362F);
				bone7.cubeList.add(new ModelBox(bone7, 86, 55, -1.1F, 0.875F, -2.0F, 1, 3, 4, 0.0F, true));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(-6.5F, 0.9F, -2.0F);
				BeltPads1.addChild(bone8);
				setRotationAngle(bone8, -0.7011F, 1.3355F, 2.3663F);
				bone8.cubeList.add(new ModelBox(bone8, 62, 55, -1.1F, 1.0F, -1.25F, 1, 3, 3, 0.0F, true));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(-6.5F, 0.9F, 2.0F);
				BeltPads1.addChild(bone9);
				setRotationAngle(bone9, 0.7011F, -1.3355F, 2.3663F);
				bone9.cubeList.add(new ModelBox(bone9, 74, 55, -1.1F, 1.0F, -1.75F, 1, 3, 3, 0.0F, true));
		
				BeltPads2 = new ModelRenderer(this);
				BeltPads2.setRotationPoint(-4.0F, 10.5F, 0.0F);
				bipedBody.addChild(BeltPads2);
				
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(8.0F, 1.0F, 0.0F);
				BeltPads2.addChild(bone6);
				setRotationAngle(bone6, 0.0F, 0.0F, -2.8362F);
				bone6.cubeList.add(new ModelBox(bone6, 86, 55, 0.1F, 0.925F, -2.0F, 1, 3, 4, 0.0F, false));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(6.5F, 0.9F, -2.0F);
				BeltPads2.addChild(bone10);
				setRotationAngle(bone10, -0.7011F, -1.3355F, -2.3663F);
				bone10.cubeList.add(new ModelBox(bone10, 62, 55, 0.1F, 1.0F, -1.25F, 1, 3, 3, 0.0F, false));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(6.5F, 0.9F, 2.0F);
				BeltPads2.addChild(bone11);
				setRotationAngle(bone11, 0.7011F, 1.3355F, -2.3663F);
				bone11.cubeList.add(new ModelBox(bone11, 74, 55, 0.1F, 1.0F, -1.75F, 1, 3, 3, 0.0F, false));
		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 49, 0, -2.5F, 5.0F, -1.5F, 3, 5, 3, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 34, -3.0F, -2.0F, -2.0F, 4, 7, 4, 0.0F, false));
		
				rightShoulderPad = new ModelRenderer(this);
				rightShoulderPad.setRotationPoint(-3.5F, 0.5F, 1.5F);
				bipedRightArm.addChild(rightShoulderPad);
				
		
				Shoulderpadlr_r1 = new ModelRenderer(this);
				Shoulderpadlr_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
				rightShoulderPad.addChild(Shoulderpadlr_r1);
				setRotationAngle(Shoulderpadlr_r1, 0.0F, -2.7925F, 0.1745F);
				Shoulderpadlr_r1.cubeList.add(new ModelBox(Shoulderpadlr_r1, 38, 46, -0.5F, -3.5F, -2.25F, 1, 7, 4, 0.05F, true));
		
				Shoulderpadll_r1 = new ModelRenderer(this);
				Shoulderpadll_r1.setRotationPoint(0.0F, 0.0F, -3.0F);
				rightShoulderPad.addChild(Shoulderpadll_r1);
				setRotationAngle(Shoulderpadll_r1, 0.0F, -0.3491F, 0.1745F);
				Shoulderpadll_r1.cubeList.add(new ModelBox(Shoulderpadll_r1, 38, 46, -0.5F, -3.5F, -2.25F, 1, 7, 4, 0.05F, false));
		
				sword = new ModelRenderer(this);
				sword.setRotationPoint(-3.0F, 3.85F, 3.0F);
				bipedRightArm.addChild(sword);
				sword.cubeList.add(new ModelBox(sword, 76, 0, 1.5F, 4.0F, -6.0F, 1, 2, 8, -0.2F, false));
				sword.cubeList.add(new ModelBox(sword, 74, 0, 2.0F, 3.0F, -26.0F, 0, 4, 20, 0.0F, false));
				sword.cubeList.add(new ModelBox(sword, 77, 0, 1.5F, 2.55F, -8.4F, 1, 5, 2, 0.3F, false));
				sword.cubeList.add(new ModelBox(sword, 87, 0, 1.5F, 3.55F, -6.15F, 1, 3, 1, -0.1F, false));
	
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 49, 0, -0.5F, 5.0F, -1.5F, 3, 5, 3, 0.0F, true));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 34, -1.0F, -2.0F, -2.0F, 4, 7, 4, 0.0F, true));
		
				leftShoulderPad = new ModelRenderer(this);
				leftShoulderPad.setRotationPoint(3.5F, 0.5F, 1.5F);
				bipedLeftArm.addChild(leftShoulderPad);
				
		
				Shoulderpadlr_r2 = new ModelRenderer(this);
				Shoulderpadlr_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
				leftShoulderPad.addChild(Shoulderpadlr_r2);
				setRotationAngle(Shoulderpadlr_r2, 0.0F, 2.7925F, -0.1745F);
				Shoulderpadlr_r2.cubeList.add(new ModelBox(Shoulderpadlr_r2, 38, 46, -0.5F, -3.5F, -2.25F, 1, 7, 4, 0.05F, false));
		
				Shoulderpadll_r2 = new ModelRenderer(this);
				Shoulderpadll_r2.setRotationPoint(0.0F, 0.0F, -3.0F);
				leftShoulderPad.addChild(Shoulderpadll_r2);
				setRotationAngle(Shoulderpadll_r2, 0.0F, 0.3491F, -0.1745F);
				Shoulderpadll_r2.cubeList.add(new ModelBox(Shoulderpadll_r2, 38, 46, -0.5F, -3.5F, -2.25F, 1, 7, 4, 0.05F, true));
		
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 47, 16, -1.5F, 9.0F, -1.5F, 3, 3, 3, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 45, -2.0F, 0.0F, -2.0F, 4, 9, 4, 0.0F, false));
		
				rightDress = new ModelRenderer(this);
				rightDress.setRotationPoint(1.9F, 0.0F, 1.0F);
				bipedRightLeg.addChild(rightDress);
				rightDress.cubeList.add(new ModelBox(rightDress, 27, 0, -4.0F, 0.0F, -2.5F, 4, 6, 3, 0.0F, false));
		
				Dressb_r1 = new ModelRenderer(this);
				Dressb_r1.setRotationPoint(0.0F, 2.9753F, 0.6304F);
				rightDress.addChild(Dressb_r1);
				setRotationAngle(Dressb_r1, -0.0436F, -3.1416F, 0.0F);
				Dressb_r1.cubeList.add(new ModelBox(Dressb_r1, 36, 9, 0.0F, -3.0F, -0.5F, 4, 6, 1, 0.0F, true));
		
				Dressf_r1 = new ModelRenderer(this);
				Dressf_r1.setRotationPoint(0.0F, 0.0F, -2.0F);
				rightDress.addChild(Dressf_r1);
				setRotationAngle(Dressf_r1, -0.0436F, 0.0F, 0.0F);
				Dressf_r1.cubeList.add(new ModelBox(Dressf_r1, 36, 9, -4.0F, 0.0F, -1.0F, 4, 6, 1, 0.0F, false));
		
				BeltPads3 = new ModelRenderer(this);
				BeltPads3.setRotationPoint(-1.1F, -0.5F, 0.0F);
				bipedRightLeg.addChild(BeltPads3);
				
		
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(-1.0F, -1.0F, 0.0F);
				BeltPads3.addChild(bone18);
				setRotationAngle(bone18, 0.0F, 0.0F, -2.8362F);
				
		
				beltpadtr_r1 = new ModelRenderer(this);
				beltpadtr_r1.setRotationPoint(-1.6125F, -2.2473F, 0.0F);
				bone18.addChild(beltpadtr_r1);
				setRotationAngle(beltpadtr_r1, 0.0F, 0.0F, -0.1309F);
				beltpadtr_r1.cubeList.add(new ModelBox(beltpadtr_r1, 87, 41, 0.5125F, -4.5527F, -2.0F, 1, 6, 4, 0.0F, true));
		
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.5F, -0.9F, -2.0F);
				BeltPads3.addChild(bone19);
				setRotationAngle(bone19, 0.7011F, 1.3355F, -2.3663F);
				
		
				beltpadtr_r2 = new ModelRenderer(this);
				beltpadtr_r2.setRotationPoint(-0.6F, -4.0F, 0.5F);
				bone19.addChild(beltpadtr_r2);
				setRotationAngle(beltpadtr_r2, 0.0F, 0.0F, -0.0436F);
				beltpadtr_r2.cubeList.add(new ModelBox(beltpadtr_r2, 62, 42, -0.5F, -3.0F, -1.5F, 1, 6, 3, 0.0F, true));
		
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.5F, -0.9F, 2.0F);
				BeltPads3.addChild(bone20);
				setRotationAngle(bone20, -0.7011F, -1.3355F, -2.3663F);
				
		
				beltpadtr_r3 = new ModelRenderer(this);
				beltpadtr_r3.setRotationPoint(-0.6F, -4.0F, 0.0F);
				bone20.addChild(beltpadtr_r3);
				setRotationAngle(beltpadtr_r3, 0.0F, 0.0F, -0.0873F);
				beltpadtr_r3.cubeList.add(new ModelBox(beltpadtr_r3, 75, 42, -0.5F, -3.0F, -2.0F, 1, 6, 3, 0.0F, true));
		
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 47, 16, -1.5F, 9.0F, -1.5F, 3, 3, 3, 0.0F, true));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 45, -2.0F, 0.0F, -2.0F, 4, 9, 4, 0.0F, true));
		
				leftDress = new ModelRenderer(this);
				leftDress.setRotationPoint(-1.9F, 0.0F, 1.0F);
				bipedLeftLeg.addChild(leftDress);
				leftDress.cubeList.add(new ModelBox(leftDress, 27, 0, 0.0F, 0.0F, -2.5F, 4, 6, 3, 0.0F, true));
		
				Dressb_r2 = new ModelRenderer(this);
				Dressb_r2.setRotationPoint(0.0F, 2.9753F, 0.6304F);
				leftDress.addChild(Dressb_r2);
				setRotationAngle(Dressb_r2, -0.0436F, 3.1416F, 0.0F);
				Dressb_r2.cubeList.add(new ModelBox(Dressb_r2, 36, 9, -4.0F, -3.0F, -0.5F, 4, 6, 1, 0.0F, false));
		
				Dressf_r2 = new ModelRenderer(this);
				Dressf_r2.setRotationPoint(0.0F, 0.0F, -2.0F);
				leftDress.addChild(Dressf_r2);
				setRotationAngle(Dressf_r2, -0.0436F, 0.0F, 0.0F);
				Dressf_r2.cubeList.add(new ModelBox(Dressf_r2, 36, 9, 0.0F, 0.0F, -1.0F, 4, 6, 1, 0.0F, true));
		
				BeltPads4 = new ModelRenderer(this);
				BeltPads4.setRotationPoint(1.1F, -0.5F, 0.0F);
				bipedLeftLeg.addChild(BeltPads4);
				
		
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(1.0F, -1.0F, 0.0F);
				BeltPads4.addChild(bone21);
				setRotationAngle(bone21, 0.0F, 0.0F, 2.8362F);
				
		
				beltpadtr_r4 = new ModelRenderer(this);
				beltpadtr_r4.setRotationPoint(1.6125F, -2.2473F, 0.0F);
				bone21.addChild(beltpadtr_r4);
				setRotationAngle(beltpadtr_r4, 0.0F, 0.0F, 0.1309F);
				beltpadtr_r4.cubeList.add(new ModelBox(beltpadtr_r4, 87, 41, -1.5125F, -4.6027F, -2.0F, 1, 6, 4, 0.0F, false));
		
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(-0.5F, -0.9F, -2.0F);
				BeltPads4.addChild(bone22);
				setRotationAngle(bone22, 0.7011F, -1.3355F, 2.3663F);
				
		
				beltpadtr_r5 = new ModelRenderer(this);
				beltpadtr_r5.setRotationPoint(0.6F, -4.0F, 0.0F);
				bone22.addChild(beltpadtr_r5);
				setRotationAngle(beltpadtr_r5, 0.0F, 0.0F, 0.0436F);
				beltpadtr_r5.cubeList.add(new ModelBox(beltpadtr_r5, 62, 42, -0.5F, -3.0F, -1.0F, 1, 6, 3, 0.0F, false));
		
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(-0.5F, -0.9F, 2.0F);
				BeltPads4.addChild(bone23);
				setRotationAngle(bone23, -0.7011F, 1.3355F, 2.3663F);
				
		
				beltpadtr_r6 = new ModelRenderer(this);
				beltpadtr_r6.setRotationPoint(0.6F, -4.0F, 0.0F);
				bone23.addChild(beltpadtr_r6);
				setRotationAngle(beltpadtr_r6, 0.0F, 0.0F, 0.0873F);
				beltpadtr_r6.cubeList.add(new ModelBox(beltpadtr_r6, 75, 42, -0.5F, -3.0F, -2.0F, 1, 6, 3, 0.0F, false));
		
				rightWing = new ModelRenderer(this);
				rightWing.setRotationPoint(-1.5F, 2.5F, 2.0F);
				setRotationAngle(rightWing, 0.0F, 0.0F, -0.4363F);
				rightWing.cubeList.add(new ModelBox(rightWing, 4, 6, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, 1.0F);
				rightWing.addChild(bone3);
				setRotationAngle(bone3, -0.2618F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 16, 34, -3.5F, -15.5F, 0.0F, 4, 16, 1, 0.0F, false));
		
				rightClaw = new ModelRenderer(this);
				rightClaw.setRotationPoint(1.0523F, -12.7159F, -0.1F);
				bone3.addChild(rightClaw);
				setRotationAngle(rightClaw, 0.0F, 0.3054F, 1.2217F);
				
		
				finger5_r1 = new ModelRenderer(this);
				finger5_r1.setRotationPoint(-3.6024F, 1.2574F, 0.2148F);
				rightClaw.addChild(finger5_r1);
				setRotationAngle(finger5_r1, -0.1309F, 0.829F, 0.6981F);
				finger5_r1.cubeList.add(new ModelBox(finger5_r1, 0, 6, -0.2499F, -0.6416F, -1.5148F, 1, 1, 2, 0.0F, false));
		
				finger4_r1 = new ModelRenderer(this);
				finger4_r1.setRotationPoint(-1.1831F, 0.5692F, 0.3951F);
				rightClaw.addChild(finger4_r1);
				setRotationAngle(finger4_r1, -0.7418F, 0.0436F, 0.1309F);
				finger4_r1.cubeList.add(new ModelBox(finger4_r1, 0, 0, 0.5F, -0.5F, -2.3951F, 1, 1, 3, 0.0F, false));
		
				finger3_r1 = new ModelRenderer(this);
				finger3_r1.setRotationPoint(-1.1831F, 0.5692F, 0.3951F);
				rightClaw.addChild(finger3_r1);
				setRotationAngle(finger3_r1, -0.7418F, 0.0F, -0.0436F);
				finger3_r1.cubeList.add(new ModelBox(finger3_r1, 0, 0, -0.5F, -0.5F, -2.5951F, 1, 1, 3, 0.0F, false));
		
				finger2_r1 = new ModelRenderer(this);
				finger2_r1.setRotationPoint(-2.0204F, 0.5145F, 0.0264F);
				rightClaw.addChild(finger2_r1);
				setRotationAngle(finger2_r1, -0.7418F, 0.0F, -0.3491F);
				finger2_r1.cubeList.add(new ModelBox(finger2_r1, 0, 0, -0.7319F, -0.5F, -2.1264F, 1, 1, 3, 0.0F, false));
		
				thumb_r1 = new ModelRenderer(this);
				thumb_r1.setRotationPoint(1.8898F, 1.2159F, -0.2763F);
				rightClaw.addChild(thumb_r1);
				setRotationAngle(thumb_r1, -0.0436F, -0.2618F, 0.6109F);
				thumb_r1.cubeList.add(new ModelBox(thumb_r1, 0, 6, -0.9421F, 0.2F, -1.0F, 1, 1, 2, 0.0F, false));
		
				flap1 = new ModelRenderer(this);
				flap1.setRotationPoint(-1.5F, -14.5F, 0.5F);
				bone3.addChild(flap1);
				flap1.cubeList.add(new ModelBox(flap1, 26, 46, -1.0F, 0.0F, -0.5F, 2, 16, 1, 0.2F, false));
		
				flap2 = new ModelRenderer(this);
				flap2.setRotationPoint(-1.5F, -12.5F, 0.5F);
				bone3.addChild(flap2);
				flap2.cubeList.add(new ModelBox(flap2, 26, 46, -1.0F, 0.0F, -0.5F, 2, 16, 1, 0.2F, false));
		
				flap3 = new ModelRenderer(this);
				flap3.setRotationPoint(-1.5F, -10.5F, 0.5F);
				bone3.addChild(flap3);
				flap3.cubeList.add(new ModelBox(flap3, 26, 46, -1.0F, 0.0F, -0.5F, 2, 16, 1, 0.2F, false));
		
				flap4 = new ModelRenderer(this);
				flap4.setRotationPoint(-1.5F, -8.5F, 0.5F);
				bone3.addChild(flap4);
				flap4.cubeList.add(new ModelBox(flap4, 26, 46, -1.0F, 0.0F, -0.5F, 2, 16, 1, 0.2F, false));
		
				flap5 = new ModelRenderer(this);
				flap5.setRotationPoint(-1.5F, -6.5F, 0.5F);
				bone3.addChild(flap5);
				flap5.cubeList.add(new ModelBox(flap5, 26, 46, -1.0F, -1.0F, -0.5F, 2, 16, 1, 0.2F, false));
		
				flap6 = new ModelRenderer(this);
				flap6.setRotationPoint(-1.5F, -4.5F, 0.5F);
				bone3.addChild(flap6);
				flap6.cubeList.add(new ModelBox(flap6, 32, 46, -1.0F, 0.0F, -0.5F, 2, 14, 1, 0.2F, false));
		
				flap7 = new ModelRenderer(this);
				flap7.setRotationPoint(-1.5F, -2.5F, 0.5F);
				bone3.addChild(flap7);
				flap7.cubeList.add(new ModelBox(flap7, 48, 46, -1.0F, 0.0F, -0.5F, 2, 13, 1, 0.1F, false));
		
				flap8 = new ModelRenderer(this);
				flap8.setRotationPoint(-0.5F, -0.5F, 0.5F);
				bone3.addChild(flap8);
				flap8.cubeList.add(new ModelBox(flap8, 48, 46, -1.0F, 0.0F, -0.5F, 2, 13, 1, 0.1F, false));
		
				leftWing = new ModelRenderer(this);
				leftWing.setRotationPoint(1.5F, 2.5F, 2.0F);
				setRotationAngle(leftWing, 0.0F, 0.0F, 0.4363F);
				leftWing.cubeList.add(new ModelBox(leftWing, 4, 6, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F, true));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 1.0F);
				leftWing.addChild(bone2);
				setRotationAngle(bone2, -0.2618F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 16, 34, -0.5F, -15.5F, 0.0F, 4, 16, 1, 0.0F, true));
		
				leftClaw = new ModelRenderer(this);
				leftClaw.setRotationPoint(-1.0523F, -12.7159F, -0.1F);
				bone2.addChild(leftClaw);
				setRotationAngle(leftClaw, 0.0F, -0.3054F, -1.2217F);
				
		
				finger5_r2 = new ModelRenderer(this);
				finger5_r2.setRotationPoint(3.6024F, 1.2574F, 0.2148F);
				leftClaw.addChild(finger5_r2);
				setRotationAngle(finger5_r2, -0.1309F, -0.829F, -0.6981F);
				finger5_r2.cubeList.add(new ModelBox(finger5_r2, 0, 6, -0.7501F, -0.6416F, -1.5148F, 1, 1, 2, 0.0F, true));
		
				finger4_r2 = new ModelRenderer(this);
				finger4_r2.setRotationPoint(1.1831F, 0.5692F, 0.3951F);
				leftClaw.addChild(finger4_r2);
				setRotationAngle(finger4_r2, -0.7418F, -0.0436F, -0.1309F);
				finger4_r2.cubeList.add(new ModelBox(finger4_r2, 0, 0, -1.5F, -0.5F, -2.3951F, 1, 1, 3, 0.0F, true));
		
				finger3_r2 = new ModelRenderer(this);
				finger3_r2.setRotationPoint(1.1831F, 0.5692F, 0.3951F);
				leftClaw.addChild(finger3_r2);
				setRotationAngle(finger3_r2, -0.7418F, 0.0F, 0.0436F);
				finger3_r2.cubeList.add(new ModelBox(finger3_r2, 0, 0, -0.5F, -0.5F, -2.5951F, 1, 1, 3, 0.0F, true));
		
				finger2_r2 = new ModelRenderer(this);
				finger2_r2.setRotationPoint(2.0204F, 0.5145F, 0.0264F);
				leftClaw.addChild(finger2_r2);
				setRotationAngle(finger2_r2, -0.7418F, 0.0F, 0.3491F);
				finger2_r2.cubeList.add(new ModelBox(finger2_r2, 0, 0, -0.2681F, -0.5F, -2.1264F, 1, 1, 3, 0.0F, true));
		
				thumb_r2 = new ModelRenderer(this);
				thumb_r2.setRotationPoint(-1.8898F, 1.2159F, -0.2763F);
				leftClaw.addChild(thumb_r2);
				setRotationAngle(thumb_r2, -0.0436F, 0.2618F, -0.6109F);
				thumb_r2.cubeList.add(new ModelBox(thumb_r2, 0, 6, -0.0579F, 0.2F, -1.0F, 1, 1, 2, 0.0F, true));
		
				flap9 = new ModelRenderer(this);
				flap9.setRotationPoint(1.5F, -14.5F, 0.5F);
				bone2.addChild(flap9);
				flap9.cubeList.add(new ModelBox(flap9, 26, 46, -1.0F, 0.0F, -0.5F, 2, 16, 1, 0.2F, true));
		
				flap10 = new ModelRenderer(this);
				flap10.setRotationPoint(1.5F, -12.5F, 0.5F);
				bone2.addChild(flap10);
				flap10.cubeList.add(new ModelBox(flap10, 26, 46, -1.0F, 0.0F, -0.5F, 2, 16, 1, 0.2F, true));
		
				flap11 = new ModelRenderer(this);
				flap11.setRotationPoint(1.5F, -10.5F, 0.5F);
				bone2.addChild(flap11);
				flap11.cubeList.add(new ModelBox(flap11, 26, 46, -1.0F, 0.0F, -0.5F, 2, 16, 1, 0.2F, true));
		
				flap12 = new ModelRenderer(this);
				flap12.setRotationPoint(1.5F, -8.5F, 0.5F);
				bone2.addChild(flap12);
				flap12.cubeList.add(new ModelBox(flap12, 26, 46, -1.0F, 0.0F, -0.5F, 2, 16, 1, 0.2F, true));
		
				flap13 = new ModelRenderer(this);
				flap13.setRotationPoint(1.5F, -6.5F, 0.5F);
				bone2.addChild(flap13);
				flap13.cubeList.add(new ModelBox(flap13, 26, 46, -1.0F, -1.0F, -0.5F, 2, 16, 1, 0.2F, true));
		
				flap14 = new ModelRenderer(this);
				flap14.setRotationPoint(1.5F, -4.5F, 0.5F);
				bone2.addChild(flap14);
				flap14.cubeList.add(new ModelBox(flap14, 32, 46, -1.0F, 0.0F, -0.5F, 2, 14, 1, 0.2F, true));
		
				flap15 = new ModelRenderer(this);
				flap15.setRotationPoint(1.5F, -2.5F, 0.5F);
				bone2.addChild(flap15);
				flap15.cubeList.add(new ModelBox(flap15, 48, 46, -1.0F, 0.0F, -0.5F, 2, 13, 1, 0.1F, true));
		
				flap16 = new ModelRenderer(this);
				flap16.setRotationPoint(0.5F, -0.5F, 0.5F);
				bone2.addChild(flap16);
				flap16.cubeList.add(new ModelBox(flap16, 48, 46, -1.0F, 0.0F, -0.5F, 2, 13, 1, 0.1F, true));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float age, float f3, float f4, float f5) {
				if (this.wingSwingProgress > 0.0F) {
					this.rightWing.rotateAngleZ = -0.4363F + this.wingSwingProgress * -30.0F * 0.017453292F;
					this.flap1.rotateAngleZ = this.wingSwingProgress * 140.0F * 0.017453292F;
					this.flap2.rotateAngleZ = this.wingSwingProgress * 135.0F * 0.017453292F;
					this.flap3.rotateAngleZ = this.wingSwingProgress * 130.0F * 0.017453292F;
					this.flap4.rotateAngleZ = this.wingSwingProgress * 125.0F * 0.017453292F;
					this.flap5.rotateAngleZ = this.wingSwingProgress * 120.0F * 0.017453292F;
					this.flap6.rotateAngleZ = this.wingSwingProgress * 115.0F * 0.017453292F;
					this.flap7.rotateAngleZ = this.wingSwingProgress * 110.0F * 0.017453292F;
					this.flap8.rotateAngleZ = this.wingSwingProgress * 105.0F * 0.017453292F;
					this.leftWing.rotateAngleZ = 0.4363F + this.wingSwingProgress * 30.0F * 0.017453292F;
					this.flap9.rotateAngleZ = this.wingSwingProgress * -140.0F * 0.017453292F;
					this.flap10.rotateAngleZ = this.wingSwingProgress * -135.0F * 0.017453292F;
					this.flap11.rotateAngleZ = this.wingSwingProgress * -130.0F * 0.017453292F;
					this.flap12.rotateAngleZ = this.wingSwingProgress * -125.0F * 0.017453292F;
					this.flap13.rotateAngleZ = this.wingSwingProgress * -120.0F * 0.017453292F;
					this.flap14.rotateAngleZ = this.wingSwingProgress * -115.0F * 0.017453292F;
					this.flap15.rotateAngleZ = this.wingSwingProgress * -110.0F * 0.017453292F;
					this.flap16.rotateAngleZ = this.wingSwingProgress * -105.0F * 0.017453292F;
				}
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * this.modelScale, 0.0F);
				GlStateManager.scale(this.modelScale, this.modelScale, this.modelScale);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				if (this.renderFlame) {
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					GlStateManager.translate(0.0F, age * 0.01F, 0.0F);
					GlStateManager.matrixMode(5888);
					bipedHeadwear.showModel = false;
				}
				int color = ((EntityCustom)entity).getFlameColor();
				float red = (float)(color >> 16 & 0xFF) / 255.0F;
				float green = (float)(color >> 8 & 0xFF) / 255.0F;
				float blue = (float)(color & 0xFF) / 255.0F;
				GlStateManager.color(red, green, blue, this.maxAlpha * Math.min(age/60f, 1f));
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				super.render(entity, f, f1, age, f3, f4, f5);
				rightWing.render(f5);
				leftWing.render(f5);
				GlStateManager.color(1f, 1f, 1f, 1f);
				bipedHeadwear.render(f5);
				GlStateManager.enableLighting();
				if (this.renderFlame) {
					GlStateManager.matrixMode(5890);
					GlStateManager.loadIdentity();
					GlStateManager.matrixMode(5888);
					bipedHeadwear.showModel = true;
				}
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float limbSwing, float f1, float f2, float f3, float f4, float f5, Entity entityIn) {
				super.setRotationAngles(limbSwing * 2.0F / entityIn.height, f1, f2, f3, f4, f5, entityIn);
				if (((EntityCustom)entityIn).isSwingingArms()) {
					this.bipedLeftArm.rotateAngleY = 0.1F + this.bipedHead.rotateAngleY;
					this.bipedLeftArm.rotateAngleX = -((float)Math.PI / 2F) + this.bipedHead.rotateAngleX;
				}
			}
		}
	}
}
