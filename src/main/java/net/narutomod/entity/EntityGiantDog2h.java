
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityGiantDog2h extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 181;
	public static final int ENTITYID_RANGED = 182;
	private static final float ENTITY_SCALE = 8f;
	
	public EntityGiantDog2h(ElementsNarutomodMod instance) {
		super(instance, 446);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "giant_dog_2h"), ENTITYID).name("giant_dog_2h")
		 .tracker(64, 3, true).egg(-11388356, -9088173).build());
	}

	public static class EntityCustom extends EntitySummonAnimal.Base implements IMob {
		private int splitTicks;
		private EntityCustom child;

		public EntityCustom(World world) {
			super(world);
			this.setOGSize(0.6f, 0.85f);
			this.experienceValue = 5000;
			this.stepHeight = this.height / 3;
			this.postScaleFixup();
		}

		public EntityCustom(EntityLivingBase player, double maxHealth) {
			super(player);
			this.setOGSize(0.6f, 0.85f);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
			this.experienceValue = 5000;
			this.stepHeight = this.height / 3;
			RayTraceResult res = ProcedureUtils.raytraceBlocks(player, 4.0);
			double x = res.getBlockPos().getX();
			double z = res.getBlockPos().getZ();
			this.setPosition(x + 0.5, player.posY, z + 0.5);
			this.rotationYaw = player.rotationYaw - 180.0f;
			this.rotationYawHead = this.rotationYaw;
			this.postScaleFixup();
		}

		public EntityCustom(EntityLivingBase player) {
			this(player, 400d);
		}

		@Override
		public float getScale() {
			return ENTITY_SCALE;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAILeapAtTarget(this, 1.0f));
			this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.4f, true));
			this.tasks.addTask(3, new EntityAILookIdle(this));
		}

		@Override
		protected void dontWander(boolean set) {
		}

		@Override
		public SoundEvent getAmbientSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.wolf.growl"));
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.wolf.hurt"));
		}

		@Override
		public SoundEvent getDeathSound() {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.wolf.death"));
		}

		@Override
		protected float getSoundVolume() {
			return 2.0F;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			//this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
			//this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000D);
			//this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(30D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0);
		}

		@Override
		public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			super.processInteract(entity, hand);
			if (this.isSummoner(entity)) {
				entity.startRiding(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 
			 (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() 
			 * (this.rand.nextFloat() * 0.4f + 0.8f));
		}

		@Override
		public double getMountedYOffset() {
			return this.height + 0.35d;
		}

		@Override
		public boolean shouldRiderSit() {
			return false;
		}

		@Override
		public int getMaxFallHeight() {
			return 12;
		}

		@Override
		public void setDead() {
			super.setDead();
			this.poof();
			if (!this.world.isRemote && this.child != null) {
				this.child.setDead();
			}
		}

		@Override
		protected void onDeathUpdate() {
			if (!this.world.isRemote) {
				if (this.getMaxHealth() <= 100.0f) {
					this.setDead();
				} else {
					++this.splitTicks;
					if (this.splitTicks < 20) {
						this.setNoAI(true);
					} else {
						float maxhp = this.getMaxHealth() * 0.5f;
						this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxhp);
						this.setHealth(maxhp);
						this.setNoAI(false);
						this.child = this.createChild(maxhp);
						if (this.child != null) {
							this.child.copyLocationAndAnglesFrom(this);
							this.child.rotationYawHead = this.rotationYawHead;
							this.world.spawnEntity(this.child);
						}
					}
				}
			}
//System.out.println(">>> maxHealth:"+getMaxHealth()+", curHealth:"+getHealth()+", splitTicks:"+splitTicks+", "+this);
		}

		private void poof() {
			if (!this.world.isRemote) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:poof")), 1.0F, 1.0F);
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY+this.height/2, this.posZ, 300,
				 this.width * 0.5d, this.height * 0.3d, this.width * 0.5d, 0d, 0d, 0d, 0xD0FFFFFF, 20 + (int)(ENTITY_SCALE * 5));
			}
		}

		@Override
		public void onEntityUpdate() {
			EntityLivingBase owner = this.getSummoner();
			if (owner != null && owner.getHealth() <= 0.0f) {
				this.setDead();
			}
			super.onEntityUpdate();
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
				this.setDead();
			}
			this.fallDistance = 0;
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			if (!this.world.isRemote && entity instanceof EntityLivingBase && !this.isSummoner((EntityLivingBase)entity)) {
				entity.attackEntityFrom(DamageSource.causeMobDamage(this), 5f);
			}
			super.collideWithEntity(entity);
		}
		
		@Nullable
		private EntityCustom createChild(float health) {
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner != null) {
					return new EntityCustom(summoner, health);
				}
			}
			return null;
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderLiving(renderManager, new ModelDog2head(), 0.5f * ENTITY_SCALE) {
					protected ResourceLocation getEntityTexture(Entity entity) {
						return new ResourceLocation("narutomod:textures/dog.png");
					}
				};
			});
		}

		// Made with Blockbench 3.7.5
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelDog2head extends ModelBase {
			private final ModelRenderer headRight;
			private final ModelRenderer headLeft;
			private final ModelRenderer body;
			private final ModelRenderer upperBody;
			private final ModelRenderer leg0;
			private final ModelRenderer leg6;
			private final ModelRenderer leg1;
			private final ModelRenderer leg7;
			private final ModelRenderer leg2;
			private final ModelRenderer leg4;
			private final ModelRenderer leg3;
			private final ModelRenderer leg5;
			private final ModelRenderer tail;
			
			public ModelDog2head() {
				textureWidth = 64;
				textureHeight = 32;
	
				headRight = new ModelRenderer(this);
				headRight.setRotationPoint(-3.5F, 13.5F, -7.0F);
				setRotationAngle(headRight, 0.0F, 0.2618F, 0.0F);
				headRight.cubeList.add(new ModelBox(headRight, 0, 0, -2.0F, -3.0F, -2.0F, 5, 6, 4, 0.0F, false));
				headRight.cubeList.add(new ModelBox(headRight, 0, 10, -1.0F, -0.0156F, -5.0F, 3, 3, 4, 0.0F, false));
				headRight.cubeList.add(new ModelBox(headRight, 16, 11, 0.0539F, -1.5F, -4.0F, 1, 2, 1, 0.0F, false));
		
				headLeft = new ModelRenderer(this);
				headLeft.setRotationPoint(3.5F, 13.5F, -7.0F);
				setRotationAngle(headLeft, 0.0F, -0.2618F, 0.0F);
				headLeft.cubeList.add(new ModelBox(headLeft, 0, 0, -3.0F, -3.0F, -2.0F, 5, 6, 4, 0.0F, false));
				headLeft.cubeList.add(new ModelBox(headLeft, 0, 10, -2.0F, -0.0156F, -5.0F, 3, 3, 4, 0.0F, false));
				headLeft.cubeList.add(new ModelBox(headLeft, 16, 11, -1.0F, -1.5F, -4.0F, 1, 2, 1, 0.0F, false));
		
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 10.5F, 0.0F);
				setRotationAngle(body, 1.3963F, 0.0F, 0.0F);
				body.cubeList.add(new ModelBox(body, 18, 14, -3.0F, 0.0F, -6.0F, 6, 9, 6, 0.0F, false));
		
				upperBody = new ModelRenderer(this);
				upperBody.setRotationPoint(0.0F, 14.0F, 2.0F);
				setRotationAngle(upperBody, -1.5708F, 0.0F, 0.0F);
				upperBody.cubeList.add(new ModelBox(upperBody, 21, 0, -4.0F, 2.0F, -4.0F, 8, 6, 7, 0.0F, false));
		
				leg0 = new ModelRenderer(this);
				leg0.setRotationPoint(-2.5F, 13.0F, 6.0F);
				setRotationAngle(leg0, 0.2618F, 0.0F, 0.0873F);
				leg0.cubeList.add(new ModelBox(leg0, 0, 18, -1.0F, 0.0F, -1.0F, 2, 6, 2, 0.5F, false));
		
				leg6 = new ModelRenderer(this);
				leg6.setRotationPoint(-1.0F, 6.0F, 1.25F);
				leg0.addChild(leg6);
				setRotationAngle(leg6, -0.2618F, 0.0F, -0.0873F);
				leg6.cubeList.add(new ModelBox(leg6, 0, 18, 0.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F, false));
		
				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(2.5F, 13.0F, 6.0F);
				setRotationAngle(leg1, 0.2618F, 0.0F, -0.0873F);
				leg1.cubeList.add(new ModelBox(leg1, 0, 18, -1.0F, 0.0F, -1.0F, 2, 6, 2, 0.5F, true));
		
				leg7 = new ModelRenderer(this);
				leg7.setRotationPoint(1.0F, 6.0F, 1.25F);
				leg1.addChild(leg7);
				setRotationAngle(leg7, -0.2618F, 0.0F, 0.0873F);
				leg7.cubeList.add(new ModelBox(leg7, 0, 18, -2.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F, true));
		
				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(-2.5F, 13.0F, -4.0F);
				setRotationAngle(leg2, 0.2618F, 0.0F, 0.2618F);
				leg2.cubeList.add(new ModelBox(leg2, 0, 18, -1.0F, 0.0F, -1.0F, 2, 6, 2, 0.0F, false));
		
				leg4 = new ModelRenderer(this);
				leg4.setRotationPoint(-1.0F, 6.0F, 1.0F);
				leg2.addChild(leg4);
				setRotationAngle(leg4, -0.2618F, 0.0F, -0.2618F);
				leg4.cubeList.add(new ModelBox(leg4, 0, 18, 0.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F, false));
		
				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(2.5F, 13.0F, -4.0F);
				setRotationAngle(leg3, 0.2618F, 0.0F, -0.2618F);
				leg3.cubeList.add(new ModelBox(leg3, 0, 18, -1.0F, 0.0F, -1.0F, 2, 6, 2, 0.0F, true));
		
				leg5 = new ModelRenderer(this);
				leg5.setRotationPoint(1.0F, 6.0F, 1.0F);
				leg3.addChild(leg5);
				setRotationAngle(leg5, -0.2618F, 0.0F, 0.2618F);
				leg5.cubeList.add(new ModelBox(leg5, 0, 18, -2.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F, true));
		
				tail = new ModelRenderer(this);
				tail.setRotationPoint(0.0F, 13.0F, 8.0F);
				setRotationAngle(tail, 0.9599F, 0.0F, 0.0F);
				tail.cubeList.add(new ModelBox(tail, 9, 18, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0f, 1.5f - ENTITY_SCALE * 1.5f, 0.0f);
				GlStateManager.scale(ENTITY_SCALE, ENTITY_SCALE, ENTITY_SCALE);
				headRight.render(f5);
				headLeft.render(f5);
				body.render(f5);
				upperBody.render(f5);
				leg0.render(f5);
				leg1.render(f5);
				leg2.render(f5);
				leg3.render(f5);
				tail.render(f5);
				GlStateManager.popMatrix();
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				f *= 2.0f / e.height;
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				this.headRight.rotateAngleY = f3 / (180F / (float) Math.PI);
				this.headRight.rotateAngleX = f4 / (180F / (float) Math.PI);
				this.headLeft.rotateAngleY = f3 / (180F / (float) Math.PI);
				this.headLeft.rotateAngleX = f4 / (180F / (float) Math.PI);
				this.leg0.rotateAngleX = 0.2618F + MathHelper.cos(f * 1.0F) * -1.0F * f1;
				this.leg1.rotateAngleX = 0.2618F + MathHelper.cos(f * 1.0F) * 1.0F * f1;
				this.leg2.rotateAngleX = 0.2618F + MathHelper.cos(f * 1.0F) * 1.0F * f1;
				this.leg3.rotateAngleX = 0.2618F + MathHelper.cos(f * 1.0F) * -1.0F * f1;
				this.tail.rotateAngleZ = f2 * 0.2f;
			}
		}
	}
}
