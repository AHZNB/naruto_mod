package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.potion.PotionEffect;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureTotsukaSwordToolInHandTick;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemTotsukaSword;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySusanooClothed extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 36;
	public static final int ENTITYID_RANGED = 37;
	private static final float MODELSCALE = 4.0F;

	public EntitySusanooClothed(ElementsNarutomodMod instance) {
		super(instance, 219);
	}

	public void initElements() {
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "susanooclothed"), ENTITYID).name("susanooclothed").tracker(64, 1, true).build());
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityMagatama.class)
		 .id(new ResourceLocation("narutomod", "yasaka_magatama"), ENTITYID_RANGED).name("yasaka_magatama").tracker(64, 1, true).build());
	}

	public static class EntityCustom extends EntitySusanooBase {
		private static final DataParameter<Boolean> HAS_LEGS = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Boolean> SHOW_SWORD = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private static final AttributeModifier SWORD_REACH = new AttributeModifier("susanoo.swordReachExtension", 2.0D, 0);
		private static final AttributeModifier SWORD_ATTACK = new AttributeModifier("susanoo.swordAttackDamage", 1.2D, 1);
		private int lifeSpan;
		private EntityMagatama bulletEntity;

		public EntityCustom(World world) {
			super(world);
			this.setSize(MODELSCALE * 0.8F, MODELSCALE * (this.hasLegs() ? 2.0F : 1.25F));
			this.getEntityData().setDouble("entityModelScale", (double)MODELSCALE);
			this.lifeSpan = Integer.MAX_VALUE;
			this.chakraUsage = this.hasLegs() ? 70d : 60d;
		}

		public EntityCustom(EntityLivingBase entity, boolean fullBody) {
			super(entity);
			this.setLegs(fullBody);
			this.getEntityData().setDouble("entityModelScale", (double)MODELSCALE);
			//this.setFlameColor(0x20b83dba);
			if (this.hasLegs()) {
				this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(new AttributeModifier("susanoo.reachExtension", 3.0D, 0));
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier("susanoo.speedboost", 0.2D, 0));
			}
			if (!(entity instanceof EntityPlayer)) {
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier("susanoo.maxhealth", 400.0d, 0));
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(new AttributeModifier("susanoo.damage", 40.0d, 0));
				this.setFlameColor(0x20ec1c24);
				this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemTotsukaSword.block));
				this.setNoAI(false);
			} else {
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				 .applyModifier(new AttributeModifier("susanoo.maxhealth", this.hasLegs() ? 10d : 3d, 2));
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
				 .setBaseValue(Math.min(this.playerXp, this.hasLegs() ? EntitySusanooBase.BXP_REQUIRED_L4 : EntitySusanooBase.BXP_REQUIRED_L3) * 0.003d);
			}
			this.setHealth(this.getMaxHealth());
			this.chakraUsage = this.hasLegs() ? 70d : 60d;
			this.stepHeight = this.height / 3.0F;
			this.lifeSpan = Integer.MAX_VALUE;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(HAS_LEGS, Boolean.valueOf(false));
			this.getDataManager().register(SWINGING_ARMS, Boolean.valueOf(false));
			this.getDataManager().register(SHOW_SWORD, Boolean.valueOf(false));
		}

		@Override
		protected void initEntityAI() {
			//this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			//this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false, false));
			this.tasks.addTask(1, new EntitySusanooBase.AIAttackRangedAndMoveTowardsTarget(this, 1.0D, 40, 4F));
			this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.5D, true) {
				@Override
				protected double getAttackReachSqr(EntityLivingBase attackTarget) {
					return ProcedureUtils.getReachDistanceSq(EntityCustom.this);
					 //+ (EntityCustom.this.getHeldItemMainhand().getItem() == ItemTotsukaSword.block ? 16.0d : 0d);
				}
			});
		}

		public boolean hasLegs() {
			return ((Boolean) this.getDataManager().get(HAS_LEGS)).booleanValue();
		}

		protected void setLegs(boolean hasLegs) {
			this.getDataManager().set(HAS_LEGS, Boolean.valueOf(hasLegs));
			this.setSize(MODELSCALE * 0.8F, MODELSCALE * (hasLegs ? 2.0F : 1.25F));
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (HAS_LEGS.equals(key) && this.world.isRemote) {
				this.setSize(MODELSCALE * 0.8F, MODELSCALE * (this.hasLegs() ? 2.0F : 1.25F));
			}
		}

		@Override
		public boolean shouldShowSword() {
			return ((Boolean) this.getDataManager().get(SHOW_SWORD)).booleanValue();
		}

		@Override
	    public void setShowSword(boolean show) {
	    	if (show) {
				this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(SWORD_REACH);
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(SWORD_ATTACK);
	    	} else {
	    		this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).removeModifier(SWORD_REACH);
	    		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(SWORD_ATTACK);
	    	}
	    	this.getDataManager().set(SHOW_SWORD, Boolean.valueOf(show));
	    }
	    
	    protected void setOwnerPlayer(EntityPlayer entity) {
			super.setOwnerPlayer(entity);
		}

		public void setLifeSpan(int ticks) {
			this.lifeSpan = ticks;
		}

		@Override
		public double getMountedYOffset() {
			if (this.hasLegs())
				return (double)MODELSCALE;
			return super.getMountedYOffset();
		}

		@Override
		protected void showHeldWeapons() {
			super.showHeldWeapons();
			EntityLivingBase owner = this.getOwnerPlayer();
			if (owner instanceof EntityPlayer) {
				ItemStack ownerheldstack = owner.getHeldItemMainhand();
				ItemStack thisHeldstack = this.getHeldItemMainhand();
				if (ownerheldstack.getItem() == ItemTotsukaSword.block) {
				 	if (thisHeldstack.getItem() != ItemTotsukaSword.block) {
						this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ownerheldstack.copy());
						this.setShowSword(false);
				 	}
				} else if (thisHeldstack.getItem() == ItemTotsukaSword.block) {
					this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
			}
			if (this.getHeldItemMainhand().getItem() == ItemTotsukaSword.block) {
				HashMap<String, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", this);
				$_dependencies.put("itemstack", this.getHeldItemMainhand());
				$_dependencies.put("world", this.world);
				ProcedureTotsukaSwordToolInHandTick.executeProcedure($_dependencies);
			}
		}

		@Override
		public void onEntityUpdate() {
			//if (this.hasLegs() && this.height < MODELSCALE * 2f) {
			//	this.setSize(this.width, MODELSCALE * 2f);
			//}
			super.onEntityUpdate();
			if (this.lifeSpan-- <= 0) {
				this.setDead();
			}
			this.showHeldWeapons();
			EntityLivingBase owner = this.getOwnerPlayer();
			if (!this.world.isRemote && !this.isAIDisabled() 
			 && owner instanceof EntityLiving && this.ticksExisted % 20 == 0) {
				this.setAttackTarget(((EntityLiving)owner).getAttackTarget());
			}
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			if (!this.world.isRemote && entity instanceof EntityLivingBase && !entity.equals(this.getOwnerPlayer())) {
				if (this.getOwnerPlayer() != null 
				 && (this.getOwnerPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemMangekyoSharingan.helmet ||
				     this.getOwnerPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemMangekyoSharinganEternal.helmet))
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, 200, this.hasLegs() ? 2 : 1, false, false));
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
	    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
	    	if (this.bulletEntity == null) {
	    		this.createBullet(MODELSCALE * 0.5f);
	    	}
	    	Vec3d vec = target.getPositionEyes(1f).subtract(this.bulletEntity.getPositionVector());
	    	this.attackEntityRanged(vec.x, vec.y, vec.z);
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
	    	this.setSwingingArms(true);
	    	if (this.bulletEntity == null) {
	    		this.bulletEntity = new EntityMagatama(this, this.getFlameColor(), size);
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

	    @Override
	    public void setDead() {
	    	super.setDead();
	    	this.killBullet();
	    }
	}

	public static class EntityMagatama extends EntityScalableProjectile.Base {
		private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityMagatama.class, DataSerializers.VARINT);
		private int explosionSize;
		private float damage;

		public EntityMagatama(World a) {
			super(a);
			this.setOGSize(1.0F, 1.0F);
		}

		public EntityMagatama(EntityLivingBase shooter, int color, float scale) {
			super(shooter);
			this.setOGSize(1.0F, 1.0F);
			this.setEntityScale(scale);
			this.setColor(color);
			this.explosionSize = (int)(scale * 3);
			this.damage = scale * 20.0f;
			this.setIdlePosition();
			this.prevRotationYaw = this.rotationYaw;
			this.prevRotationPitch = this.rotationPitch;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(COLOR, Integer.valueOf(0xFFFFFFFF));
		}

		protected int getColor() {
			return ((Integer)this.dataManager.get(COLOR)).intValue();
		}

		protected void setColor(int color) {
			this.dataManager.set(COLOR, Integer.valueOf(color));
		}

		private void setIdlePosition() {
			if (this.shootingEntity != null) {
				Vec3d vec0 = this.shootingEntity.getLookVec().scale(this.getEntityScale() + 0.5d);
				Vec3d vec1 = vec0.add(this.shootingEntity.getPositionEyes(1f).subtract(0d, this.height, 0d));
				this.setLocationAndAngles(vec1.x, vec1.y, vec1.z, 
				 ProcedureUtils.getYawFromVec(vec0.x, vec0.z), ProcedureUtils.getPitchFromVec(vec0.x, vec0.y, vec0.z));
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote) {
				if (result.entityHit != null 
				 && (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntityMagatama)) {
					return;
				}
				boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
				this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, this.explosionSize, flag, flag);
				ProcedureAoeCommand.set(this, 0.0D, 3.0D).exclude(this.shootingEntity)
				  .damageEntities(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), this.damage);
				this.setDead();
			}
		}

		@Override
		public void renderParticles() {
			float scale = this.getEntityScale();
			Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY + this.height / 2.0F, this.posZ,
			 (int)(scale * 10), 0.3d * this.width, 0.3d * this.height, 0.3d * this.width, 0d, 0d, 0d, 
			 this.getColor(), 10 + (int)(scale * 10), (int)(4.0D / (this.rand.nextDouble() * 0.8D + 0.2D)), 0xF0);
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void setDead() {
			super.setDead();
			if (this.shootingEntity instanceof EntityCustom) {
				((EntityCustom)this.shootingEntity).setSwingingArms(false);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.isLaunched()) {
				this.setIdlePosition();
			}
			if (this.ticksAlive % 12 == 1) {
				this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:magatama_spin"))),
				 1f, this.rand.nextFloat()*0.4f + 0.8f);
			}
			if (this.ticksInAir > 100) {
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderSusanooClothed(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntityMagatama.class, renderManager -> new RenderMagatama(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderSusanooClothed extends RenderLiving<EntityCustom> {
			private final ResourceLocation MAIN_TEXTURE = new ResourceLocation("narutomod:textures/susanoo_clothed.png");
			private final ResourceLocation FLAME_TEXTURE = new ResourceLocation("narutomod:textures/gas256.png");

			public RenderSusanooClothed(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelSusanooClothed(), 0.6F * MODELSCALE);
				this.addLayer(new net.minecraft.client.renderer.entity.layers.LayerHeldItem(this));
			}

			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.isBeingRidden() && entity.getControllingPassenger() instanceof AbstractClientPlayer) {
					AbstractClientPlayer passenger = (AbstractClientPlayer) entity.getControllingPassenger();
					this.copyLimbSwing(entity, passenger);
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
				ModelSusanooClothed model = (ModelSusanooClothed) this.getMainModel();
				model.setVisible(true);
				if (!entity.hasLegs()) {
					model.bipedLeftLeg.showModel = false;
					model.bipedRightLeg.showModel = false;
				}
				if (this.renderManager.renderViewEntity.equals(entity.getControllingPassenger())
				 && this.renderManager.options.thirdPersonView == 0) {
					model.bipedBody.showModel = false;
					model.bipedHead.showModel = false;
					model.bipedHeadwear.showModel = false;
				}
				model.rightArmPose = entity.getHeldItemMainhand().isEmpty() ? ModelBiped.ArmPose.EMPTY : ModelBiped.ArmPose.ITEM;
				model.sword.showModel = entity.shouldShowSword();
			}

			@Override
			protected void renderModel(EntityCustom entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
				if (this.bindEntityTexture(entitylivingbaseIn)) {
					ModelSusanooClothed model = (ModelSusanooClothed)this.getMainModel();
					model.renderFlame = false;
					this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
					this.bindTexture(FLAME_TEXTURE);
					model.renderFlame = true;
					this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor * 0.99f);
				}
			}

			@Override
			protected void renderLayers(EntityCustom entity, float f0, float f1, float f2, float f3, float f4, float f5, float f6) {
				float offset = 1.5F - 1.5F * MODELSCALE / (entity.hasLegs() ? 1 : 2);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, offset, 0.0F);
				GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
				super.renderLayers(entity, f0, f1, f2, f3, f4, f5, f6);
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return MAIN_TEXTURE;
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderMagatama extends Render<EntityMagatama> {
			private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/yasaka_magatama.png");

			public RenderMagatama(RenderManager renderManager) {
				super(renderManager);
				shadowSize = 0.1f;
			}

			@Override
			public void doRender(EntityMagatama entity, double x, double y, double z, float entityYaw, float pt) {
				this.bindEntityTexture(entity);
				float scale = entity.getEntityScale();
				int color = entity.getColor();
				float alpha = (color >> 24 & 0xFF) / 255.0F;
				float red = (color >> 16 & 0xFF) / 255.0F;
				float green = (color >> 8 & 0xFF) / 255.0F;
				float blue = (color & 0xFF) / 255.0F;
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);
				GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				if (ProcedureUtils.getVelocity(entity) > 0.001d) {
					GlStateManager.rotate(((float)entity.ticksExisted + pt), 0.0F, 0.0F, 1.0F);
				}
				GlStateManager.rotate(-30f * ((float)entity.ticksExisted + pt), 0.0F, 1.0F, 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
				bufferbuilder.pos(-0.5D, 0.5D, -0.5D).tex(0.0D, 1.0D).color(red, green, blue, 1.0f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.5D, 0.5D, -0.5D).tex(1.0D, 1.0D).color(red, green, blue, 1.0f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.5D, 0.5D, 0.5D).tex(1.0D, 0.0D).color(red, green, blue, 1.0f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.5D, 0.5D, 0.5D).tex(0.0D, 0.0D).color(red, green, blue, 1.0f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.49D, 0.51D, -0.49D).tex(0.0D, 1.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.49D, 0.51D, -0.49D).tex(1.0D, 1.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.49D, 0.51D, 0.49D).tex(1.0D, 0.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.49D, 0.51D, 0.49D).tex(0.0D, 0.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.49D, 0.49D, -0.49D).tex(0.0D, 1.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.49D, 0.49D, -0.49D).tex(1.0D, 1.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.49D, 0.49D, 0.49D).tex(1.0D, 0.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.49D, 0.49D, 0.49D).tex(0.0D, 0.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.48D, 0.52D, -0.48D).tex(0.0D, 1.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.48D, 0.52D, -0.48D).tex(1.0D, 1.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.48D, 0.52D, 0.48D).tex(1.0D, 0.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.48D, 0.52D, 0.48D).tex(0.0D, 0.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.48D, 0.48D, -0.48D).tex(0.0D, 1.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.48D, 0.48D, -0.48D).tex(1.0D, 1.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.48D, 0.48D, 0.48D).tex(1.0D, 0.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.48D, 0.48D, 0.48D).tex(0.0D, 0.0D).color(red, green, blue, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.45D, 0.53D, -0.45D).tex(0.0D, 1.0D).color(1.0F, 1.0F, 0.0F, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.45D, 0.53D, -0.45D).tex(1.0D, 1.0D).color(1.0F, 1.0F, 0.0F, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.45D, 0.53D, 0.45D).tex(1.0D, 0.0D).color(1.0F, 1.0F, 0.0F, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.45D, 0.53D, 0.45D).tex(0.0D, 0.0D).color(1.0F, 1.0F, 0.0F, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.45D, 0.47D, -0.45D).tex(0.0D, 1.0D).color(1.0F, 1.0F, 0.0F, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.45D, 0.47D, -0.45D).tex(1.0D, 1.0D).color(1.0F, 1.0F, 0.0F, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.45D, 0.47D, 0.45D).tex(1.0D, 0.0D).color(1.0F, 1.0F, 0.0F, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.45D, 0.47D, 0.45D).tex(0.0D, 0.0D).color(1.0F, 1.0F, 0.0F, 0.5f).normal(0.0F, 1.0F, 0.0F).endVertex();
				tessellator.draw();
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityMagatama entity) {
				return TEXTURE;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelSusanooClothed extends ModelBiped {
			private final float maxAlpha = 0.8f;
			private final float modelscale = MODELSCALE;
			//private final ModelRenderer bipedHead;
			private final ModelRenderer Chin;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer Hat;
			private final ModelRenderer cube_r3;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer cube_r6;
			private final ModelRenderer cube_r7;
			private final ModelRenderer cube_r8;
			private final ModelRenderer cube_r9;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			private final ModelRenderer Midhorn;
			private final ModelRenderer cube_r13;
			//private final ModelRenderer bipedHeadwear;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer Coat;
			private final ModelRenderer Cloak;
			private final ModelRenderer Cloak2;
			private final ModelRenderer bottomr;
			private final ModelRenderer cube_r14;
			private final ModelRenderer cube_r15;
			private final ModelRenderer bottoml;
			private final ModelRenderer cube_r16;
			private final ModelRenderer cube_r17;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer bone3;
			private final ModelRenderer spikes;
			private final ModelRenderer Shoulderspike;
			private final ModelRenderer Shoulderspike3;
			private final ModelRenderer bone;
			private final ModelRenderer bone2;
			private final ModelRenderer cube_r18;
			private final ModelRenderer sword;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer bone4;
			private final ModelRenderer spikes2;
			private final ModelRenderer Shoulderspike2;
			private final ModelRenderer Shoulderspike4;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer cube_r19;
			//private final ModelRenderer bipedRightLeg;
			private final ModelRenderer bone5;
			private final ModelRenderer cube_r20;
			//private final ModelRenderer bipedLeftLeg;
			private final ModelRenderer bone6;
			private final ModelRenderer cube_r21;
			private boolean renderFlame;

			public ModelSusanooClothed() {
				super(0.0F, 0.0F, 128, 128);
				this.leftArmPose = ModelBiped.ArmPose.EMPTY;
				this.rightArmPose = ModelBiped.ArmPose.EMPTY;

				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bipedHead, 0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -7.5F, -4.0F, 8, 8, 8, -0.8F, false));
		
				Chin = new ModelRenderer(this);
				Chin.setRotationPoint(0.0F, 24.5F, 0.0F);
				bipedHead.addChild(Chin);
				
		
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, -24.0445F, -2.4358F);
				Chin.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.0873F, 0.0F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 62, 0, -2.0F, -1.15F, -0.9F, 4, 1, 3, -0.2F, false));
		
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(0.0F, -22.9413F, -3.0381F);
				Chin.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.1745F, 0.0F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 23, 18, -1.0F, -1.9F, -0.05F, 2, 2, 1, -0.2F, false));
		
				Hat = new ModelRenderer(this);
				Hat.setRotationPoint(0.0023F, -3.2067F, -1.5353F);
				bipedHead.addChild(Hat);
				
		
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(-0.0023F, -2.7933F, -2.4647F);
				Hat.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.2182F, 0.0F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 41, 0, -1.5F, -1.825F, 0.35F, 3, 3, 1, 0.0F, false));
		
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(2.6902F, -3.7943F, -2.3505F);
				Hat.addChild(cube_r4);
				setRotationAngle(cube_r4, -0.2182F, 0.0F, -0.5672F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 16, 60, -3.25F, -1.0F, -0.5F, 5, 2, 3, -0.5F, true));
		
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(-2.6948F, -3.7943F, -2.3505F);
				Hat.addChild(cube_r5);
				setRotationAngle(cube_r5, -0.2182F, 0.0F, 0.5672F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 16, 60, -1.75F, -1.0F, -0.5F, 5, 2, 3, -0.5F, false));
		
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(-0.0023F, -2.0433F, -2.4647F);
				Hat.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.1745F, 0.0F, 0.0F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 16, -0.5F, -0.33F, -0.09F, 1, 4, 1, -0.1F, false));
		
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(-0.0023F, -4.2549F, -1.0964F);
				Hat.addChild(cube_r7);
				setRotationAngle(cube_r7, -0.2182F, 0.0F, 0.0F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 25, 9, -4.525F, -1.0F, -1.2332F, 9, 3, 7, -0.9F, false));
		
				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(4.4977F, -0.7933F, 1.5353F);
				Hat.addChild(cube_r8);
				setRotationAngle(cube_r8, 0.0F, 0.0F, 0.0873F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 18, 33, -8.525F, -2.75F, -4.1F, 1, 9, 8, -0.8F, true));
		
				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(-4.5023F, -0.7933F, 1.5353F);
				Hat.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.0F, 0.0F, -0.0873F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 18, 33, 7.525F, -2.75F, -4.1F, 1, 9, 8, -0.8F, false));
		
				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(0.0727F, 0.2601F, 4.7365F);
				Hat.addChild(cube_r10);
				setRotationAngle(cube_r10, -0.0873F, 3.1416F, 0.0F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 52, 26, -4.4F, -4.6F, -0.5F, 9, 9, 1, -0.9F, false));
		
				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(5.0168F, -6.0109F, -1.9532F);
				Hat.addChild(cube_r11);
				setRotationAngle(cube_r11, -0.1745F, 0.1745F, 0.6545F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 33, -1.075F, 0.725F, -0.4F, 2, 2, 2, -0.5F, true));
		
				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(-5.0213F, -6.0109F, -1.9532F);
				Hat.addChild(cube_r12);
				setRotationAngle(cube_r12, -0.1745F, -0.1745F, -0.6545F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 33, -0.925F, 0.725F, -0.4F, 2, 2, 2, -0.5F, false));
		
				Midhorn = new ModelRenderer(this);
				Midhorn.setRotationPoint(0.0281F, -5.2452F, -1.5647F);
				Hat.addChild(Midhorn);
				setRotationAngle(Midhorn, -0.2618F, -0.0436F, 0.0F);
				
		
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(-0.0303F, 0.0F, 0.0F);
				Midhorn.addChild(cube_r13);
				setRotationAngle(cube_r13, -0.0524F, 0.0F, -0.7854F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 32, 62, -3.2197F, -1.75F, -0.1F, 5, 5, 1, -0.8F, false));
		
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 74, 16, -4.0F, -5.675F, -4.05F, 8, 3, 0, -0.8F, false));

				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 28, 18, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 16, -4.5F, 2.0F, -2.5F, 9, 10, 5, 0.0F, false));

				Coat = new ModelRenderer(this);
				Coat.setRotationPoint(0.0F, 20.9167F, 0.0F);
				bipedBody.addChild(Coat);


				Cloak = new ModelRenderer(this);
				Cloak.setRotationPoint(-3.9098F, -15.9076F, 0.0F);
				Coat.addChild(Cloak);


				Cloak2 = new ModelRenderer(this);
				Cloak2.setRotationPoint(3.9098F, -15.9076F, 0.0F);
				Coat.addChild(Cloak2);


				bottomr = new ModelRenderer(this);
				bottomr.setRotationPoint(-2.9495F, 11.7705F, 0.0F);
				bipedBody.addChild(bottomr);
				setRotationAngle(bottomr, 3.1416F, 0.0F, 0.8727F);


				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(-1.9755F, -1.2705F, 0.0F);
				bottomr.addChild(cube_r14);
				setRotationAngle(cube_r14, 0.0F, 0.0F, 0.6109F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 59, 57, 1.0F, -1.15F, -2.5F, 3, 1, 5, 0.0F, false));

				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(2.3224F, 1.9135F, 0.0F);
				bottomr.addChild(cube_r15);
				setRotationAngle(cube_r15, 0.0F, 0.0F, 0.3054F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 52, 36, -4.9719F, -1.643F, -2.5F, 6, 2, 5, 0.0F, false));

				bottoml = new ModelRenderer(this);
				bottoml.setRotationPoint(2.9495F, 11.7705F, 0.0F);
				bipedBody.addChild(bottoml);
				setRotationAngle(bottoml, 3.1416F, 0.0F, -0.8727F);


				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(1.9755F, -1.2705F, 0.0F);
				bottoml.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.0F, 0.0F, -0.6109F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 59, 57, -4.0F, -1.15F, -2.5F, 3, 1, 5, 0.0F, true));

				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(-2.3224F, 1.9135F, 0.0F);
				bottoml.addChild(cube_r17);
				setRotationAngle(cube_r17, 0.0F, 0.0F, -0.3054F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 52, 36, -1.0281F, -1.643F, -2.5F, 6, 2, 5, 0.0F, true));

				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);


				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-1.0F, 0.0F, 0.0F);
				bipedRightArm.addChild(bone3);
				setRotationAngle(bone3, -0.0873F, -0.5236F, 0.1745F);
				bone3.cubeList.add(new ModelBox(bone3, 52, 16, -2.5F, -2.2F, -2.5F, 5, 5, 5, 0.0F, false));
				bone3.cubeList.add(new ModelBox(bone3, 50, 0, -2.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F, false));

				spikes = new ModelRenderer(this);
				spikes.setRotationPoint(6.0F, 22.0F, 0.0F);
				bone3.addChild(spikes);


				Shoulderspike = new ModelRenderer(this);
				Shoulderspike.setRotationPoint(-7.5F, -24.0F, 0.0F);
				spikes.addChild(Shoulderspike);
				setRotationAngle(Shoulderspike, 0.0F, 0.0F, 1.0472F);
				Shoulderspike.cubeList.add(new ModelBox(Shoulderspike, 30, 50, -2.0F, 0.0F, -3.0F, 4, 0, 6, 0.0F, false));

				Shoulderspike3 = new ModelRenderer(this);
				Shoulderspike3.setRotationPoint(-8.75F, -21.75F, 0.0F);
				spikes.addChild(Shoulderspike3);
				setRotationAngle(Shoulderspike3, 0.0F, 0.0F, 0.7854F);
				Shoulderspike3.cubeList.add(new ModelBox(Shoulderspike3, 30, 50, -2.0F, 0.0F, -3.0F, 4, 0, 6, 0.0F, false));

				bone = new ModelRenderer(this);
				bone.setRotationPoint(-2.0F, 5.9F, 2.0F);
				bone3.addChild(bone);
				setRotationAngle(bone, -0.2618F, 0.0F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 48, 50, 0.0F, 0.0F, -4.0F, 4, 8, 4, -0.1F, false));

				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(11.65F, 19.85F, -2.95F);
				bone.addChild(bone2);


				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(-11.5F, -16.5F, -0.5F);
				bone2.addChild(cube_r18);
				setRotationAngle(cube_r18, 0.0F, 0.7854F, 0.0F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 49, 0, -2.225F, -1.5F, 1.325F, 1, 3, 1, 0.0F, false));
				cube_r18.cubeList.add(new ModelBox(cube_r18, 49, 0, -1.5F, -1.5F, 0.5F, 1, 3, 1, 0.0F, false));
				cube_r18.cubeList.add(new ModelBox(cube_r18, 49, 0, -0.65F, -1.5F, -0.225F, 1, 3, 1, 0.0F, false));

				sword = new ModelRenderer(this);
				sword.setRotationPoint(0.0F, 0.0F, 1.0F);
				bone.addChild(sword);
				sword.cubeList.add(new ModelBox(sword, 76, 0, 1.5F, 4.0F, -6.0F, 1, 2, 8, -0.2F, false));
				sword.cubeList.add(new ModelBox(sword, 74, 0, 2.0F, 3.0F, -26.0F, 0, 4, 20, 0.0F, false));
				sword.cubeList.add(new ModelBox(sword, 77, 0, 1.5F, 2.5F, -8.4F, 1, 5, 2, 0.3F, false));
				sword.cubeList.add(new ModelBox(sword, 87, 0, 1.5F, 3.5F, -6.15F, 1, 3, 1, -0.1F, false));

				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);


				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(1.0F, 0.0F, 0.0F);
				bipedLeftArm.addChild(bone4);
				setRotationAngle(bone4, -0.0873F, 0.5236F, -0.1745F);
				bone4.cubeList.add(new ModelBox(bone4, 52, 16, -2.5F, -2.2F, -2.5F, 5, 5, 5, 0.0F, true));
				bone4.cubeList.add(new ModelBox(bone4, 50, 0, -2.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F, true));

				spikes2 = new ModelRenderer(this);
				spikes2.setRotationPoint(-6.0F, 22.0F, 0.0F);
				bone4.addChild(spikes2);


				Shoulderspike2 = new ModelRenderer(this);
				Shoulderspike2.setRotationPoint(7.5F, -24.0F, 0.0F);
				spikes2.addChild(Shoulderspike2);
				setRotationAngle(Shoulderspike2, 0.0F, 0.0F, -1.0472F);
				Shoulderspike2.cubeList.add(new ModelBox(Shoulderspike2, 30, 50, -2.0F, 0.0F, -3.0F, 4, 0, 6, 0.0F, true));

				Shoulderspike4 = new ModelRenderer(this);
				Shoulderspike4.setRotationPoint(8.75F, -21.75F, 0.0F);
				spikes2.addChild(Shoulderspike4);
				setRotationAngle(Shoulderspike4, 0.0F, 0.0F, -0.7854F);
				Shoulderspike4.cubeList.add(new ModelBox(Shoulderspike4, 30, 50, -2.0F, 0.0F, -3.0F, 4, 0, 6, 0.0F, true));

				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(2.0F, 5.9F, 2.0F);
				bone4.addChild(bone7);
				setRotationAngle(bone7, -0.2618F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 48, 50, -4.0F, 0.0F, -4.0F, 4, 8, 4, -0.1F, true));

				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(-11.65F, 19.85F, -2.95F);
				bone7.addChild(bone8);


				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(11.5F, -16.5F, -0.5F);
				bone8.addChild(cube_r19);
				setRotationAngle(cube_r19, 0.0F, -0.7854F, 0.0F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 49, 0, 1.225F, -1.5F, 1.325F, 1, 3, 1, 0.0F, true));
				cube_r19.cubeList.add(new ModelBox(cube_r19, 49, 0, 0.5F, -1.5F, 0.5F, 1, 3, 1, 0.0F, true));
				cube_r19.cubeList.add(new ModelBox(cube_r19, 49, 0, -0.35F, -1.5F, -0.225F, 1, 3, 1, 0.0F, true));

				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 50, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));

				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-0.1F, 6.0F, -1.0F);
				bipedRightLeg.addChild(bone5);
				setRotationAngle(bone5, 0.0F, 0.0F, -0.5236F);


				cube_r20 = new ModelRenderer(this);
				cube_r20.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone5.addChild(cube_r20);
				setRotationAngle(cube_r20, -0.7854F, -0.6545F, 0.0F);
				cube_r20.cubeList.add(new ModelBox(cube_r20, 0, 4, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.1F, false));

				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 50, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, true));

				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.1F, 6.0F, -1.0F);
				bipedLeftLeg.addChild(bone6);
				setRotationAngle(bone6, 0.0F, 0.0F, 0.5236F);


				cube_r21 = new ModelRenderer(this);
				cube_r21.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone6.addChild(cube_r21);
				setRotationAngle(cube_r21, -0.7854F, 0.6545F, 0.0F);
				cube_r21.cubeList.add(new ModelBox(cube_r21, 0, 4, -1.0F, -1.0F, -1.0F, 2, 2, 2, 0.1F, true));
			}

		/*public ModelSusanooClothed() {
			super(0.0F, 0.0F, 64, 64);
			this.leftArmPose = ModelBiped.ArmPose.EMPTY;
			this.rightArmPose = ModelBiped.ArmPose.ITEM;
			this.bipedBody.cubeList.add(new ModelBox(this.bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F, false));
			this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));
			this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));
			this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
			this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
		}
*/

			@Override
			public void render(Entity entity, float f, float f1, float age, float f3, float f4, float f5) {
				float translate = 1.5F - 1.5F * this.modelscale / (((EntityCustom) entity).hasLegs() ? 1 : 2);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, translate, 0.0F);
				GlStateManager.scale(this.modelscale, this.modelscale, this.modelscale);
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
				//GlStateManager.color(1f, 1f, 1f, Math.min(age/60f, 1f));
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				super.render(entity, f, f1, age, f3, f4, f5);
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
