
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
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(40D);
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
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/dog.png");
					@Override
					protected ResourceLocation getEntityTexture(Entity entity) {
						return this.texture;
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
			private final ModelRenderer bone3;
			private final ModelRenderer headLeft;
			private final ModelRenderer bone4;
			private final ModelRenderer body;
			private final ModelRenderer tail;
			private final ModelRenderer upperBody;
			private final ModelRenderer leg0;
			private final ModelRenderer bone2;
			private final ModelRenderer leg6;
			private final ModelRenderer leg8;
			private final ModelRenderer foot0;
			private final ModelRenderer leg1;
			private final ModelRenderer bone7;
			private final ModelRenderer leg7;
			private final ModelRenderer leg9;
			private final ModelRenderer foot1;
			private final ModelRenderer leg2;
			private final ModelRenderer bone;
			private final ModelRenderer leg4;
			private final ModelRenderer foot2;
			private final ModelRenderer leg3;
			private final ModelRenderer bone5;
			private final ModelRenderer leg5;
			private final ModelRenderer foot3;
			
			public ModelDog2head() {
				textureWidth = 64;
				textureHeight = 32;
	
				headRight = new ModelRenderer(this);
				headRight.setRotationPoint(-3.5F, 13.5F, -7.0F);
				setRotationAngle(headRight, 0.0F, 0.2618F, 0.0F);
				headRight.cubeList.add(new ModelBox(headRight, 0, 1, -2.0F, -2.5F, -2.0F, 5, 5, 4, 0.0F, false));
				headRight.cubeList.add(new ModelBox(headRight, 0, 10, -1.0F, -0.5156F, -5.0F, 3, 3, 4, 0.0F, false));
				headRight.cubeList.add(new ModelBox(headRight, 16, 11, 0.0539F, -1.5F, -4.0F, 1, 2, 1, 0.0F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.5F, -1.4F, -4.75F);
				headRight.addChild(bone3);
				setRotationAngle(bone3, 0.0F, 0.0F, -3.1416F);
				bone3.cubeList.add(new ModelBox(bone3, 40, 15, -5.0F, -2.5F, 0.0F, 10, 4, 0, -2.7F, false));
		
				headLeft = new ModelRenderer(this);
				headLeft.setRotationPoint(3.5F, 13.5F, -7.0F);
				setRotationAngle(headLeft, 0.0F, -0.2618F, 0.0F);
				headLeft.cubeList.add(new ModelBox(headLeft, 0, 1, -3.0F, -2.5F, -2.0F, 5, 5, 4, 0.0F, false));
				headLeft.cubeList.add(new ModelBox(headLeft, 0, 10, -2.0F, -0.5156F, -5.0F, 3, 3, 4, 0.0F, false));
				headLeft.cubeList.add(new ModelBox(headLeft, 16, 11, -1.0F, -1.5F, -4.0F, 1, 2, 1, 0.0F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-0.5F, -1.4F, -4.75F);
				headLeft.addChild(bone4);
				setRotationAngle(bone4, 0.0F, 0.0F, -3.1416F);
				bone4.cubeList.add(new ModelBox(bone4, 40, 15, -5.0F, -2.5F, 0.0F, 10, 4, 0, -2.7F, false));
		
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 10.5F, 0.0F);
				setRotationAngle(body, 1.3963F, 0.0F, 0.0F);
				body.cubeList.add(new ModelBox(body, 18, 14, -3.0F, 0.0F, -6.0F, 6, 9, 6, 0.0F, false));
		
				tail = new ModelRenderer(this);
				tail.setRotationPoint(0.0F, 8.5F, -1.0F);
				body.addChild(tail);
				setRotationAngle(tail, -0.5236F, 0.0F, 0.0F);
				tail.cubeList.add(new ModelBox(tail, 9, 18, -1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));
		
				upperBody = new ModelRenderer(this);
				upperBody.setRotationPoint(0.0F, 14.0F, 2.0F);
				setRotationAngle(upperBody, -1.5708F, 0.0F, 0.0F);
				upperBody.cubeList.add(new ModelBox(upperBody, 21, 0, -4.0F, 2.0F, -4.0F, 8, 6, 7, 0.0F, false));
		
				leg0 = new ModelRenderer(this);
				leg0.setRotationPoint(-2.5F, 13.0F, 7.0F);
				
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg0.addChild(bone2);
				setRotationAngle(bone2, -0.1745F, 0.0F, 0.1745F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 18, -1.0F, 0.0F, -1.0F, 2, 5, 2, 0.5F, false));
		
				leg6 = new ModelRenderer(this);
				leg6.setRotationPoint(-1.0F, 5.5F, -1.5F);
				bone2.addChild(leg6);
				setRotationAngle(leg6, 1.0472F, 0.0F, -0.1745F);
				leg6.cubeList.add(new ModelBox(leg6, 0, 18, 0.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F, false));
		
				leg8 = new ModelRenderer(this);
				leg8.setRotationPoint(0.0F, 6.0F, 2.0F);
				leg6.addChild(leg8);
				setRotationAngle(leg8, -1.1345F, 0.0F, 0.0F);
				leg8.cubeList.add(new ModelBox(leg8, 0, 18, 0.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F, false));
		
				foot0 = new ModelRenderer(this);
				foot0.setRotationPoint(1.0F, 3.5F, -2.5F);
				leg8.addChild(foot0);
				setRotationAngle(foot0, 0.2182F, 0.0F, 0.0F);
				foot0.cubeList.add(new ModelBox(foot0, 0, 28, -1.0F, 0.0F, 0.0F, 2, 1, 2, 0.1F, false));
		
				leg1 = new ModelRenderer(this);
				leg1.setRotationPoint(2.5F, 13.0F, 7.0F);
				
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg1.addChild(bone7);
				setRotationAngle(bone7, -0.1745F, 0.0F, -0.1745F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 18, -1.0F, 0.0F, -1.0F, 2, 5, 2, 0.5F, true));
		
				leg7 = new ModelRenderer(this);
				leg7.setRotationPoint(1.0F, 5.5F, -1.5F);
				bone7.addChild(leg7);
				setRotationAngle(leg7, 1.0472F, 0.0F, 0.1745F);
				leg7.cubeList.add(new ModelBox(leg7, 0, 18, -2.0F, 0.0F, 0.0F, 2, 6, 2, 0.0F, true));
		
				leg9 = new ModelRenderer(this);
				leg9.setRotationPoint(0.0F, 6.0F, 2.0F);
				leg7.addChild(leg9);
				setRotationAngle(leg9, -1.1345F, 0.0F, 0.0F);
				leg9.cubeList.add(new ModelBox(leg9, 0, 18, -2.0F, 0.0F, -2.0F, 2, 4, 2, 0.0F, true));
		
				foot1 = new ModelRenderer(this);
				foot1.setRotationPoint(-1.0F, 3.5F, -2.5F);
				leg9.addChild(foot1);
				setRotationAngle(foot1, 0.2182F, 0.0F, 0.0F);
				foot1.cubeList.add(new ModelBox(foot1, 0, 28, -1.0F, 0.0F, 0.0F, 2, 1, 2, 0.1F, true));
		
				leg2 = new ModelRenderer(this);
				leg2.setRotationPoint(-3.0F, 13.0F, -4.0F);
				
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg2.addChild(bone);
				setRotationAngle(bone, 0.2618F, -0.2618F, 0.1309F);
				bone.cubeList.add(new ModelBox(bone, 0, 18, -1.0F, 0.0F, -1.0F, 2, 6, 2, 0.1F, false));
		
				leg4 = new ModelRenderer(this);
				leg4.setRotationPoint(-1.0F, 6.0F, 1.0F);
				bone.addChild(leg4);
				setRotationAngle(leg4, -0.5236F, 0.0873F, -0.0873F);
				leg4.cubeList.add(new ModelBox(leg4, 0, 18, 0.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F, false));
		
				foot2 = new ModelRenderer(this);
				foot2.setRotationPoint(1.0F, 5.5F, -2.5F);
				leg4.addChild(foot2);
				setRotationAngle(foot2, 0.2182F, 0.0F, -0.0436F);
				foot2.cubeList.add(new ModelBox(foot2, 0, 28, -1.0F, 0.0F, 0.0F, 2, 1, 2, 0.1F, false));
		
				leg3 = new ModelRenderer(this);
				leg3.setRotationPoint(3.0F, 13.0F, -4.0F);
				
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
				leg3.addChild(bone5);
				setRotationAngle(bone5, 0.2618F, 0.2618F, -0.1309F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 18, -1.0F, 0.0F, -1.0F, 2, 6, 2, 0.1F, true));
		
				leg5 = new ModelRenderer(this);
				leg5.setRotationPoint(1.0F, 6.0F, 1.0F);
				bone5.addChild(leg5);
				setRotationAngle(leg5, -0.5236F, -0.0873F, 0.0873F);
				leg5.cubeList.add(new ModelBox(leg5, 0, 18, -2.0F, 0.0F, -2.0F, 2, 6, 2, 0.0F, true));
		
				foot3 = new ModelRenderer(this);
				foot3.setRotationPoint(-1.0F, 5.5F, -2.5F);
				leg5.addChild(foot3);
				setRotationAngle(foot3, 0.2182F, 0.0F, 0.0436F);
				foot3.cubeList.add(new ModelBox(foot3, 0, 28, -1.0F, 0.0F, 0.0F, 2, 1, 2, 0.1F, true));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0f, 1.5f - ENTITY_SCALE * 1.5f, 0.0f);
				GlStateManager.scale(ENTITY_SCALE, ENTITY_SCALE, ENTITY_SCALE);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				headRight.render(f5);
				headLeft.render(f5);
				body.render(f5);
				upperBody.render(f5);
				leg0.render(f5);
				leg1.render(f5);
				leg2.render(f5);
				leg3.render(f5);
				GlStateManager.disableBlend();
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
				this.leg0.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
				this.leg1.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
				this.leg2.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
				this.leg3.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
				this.tail.rotateAngleY = f2 * 0.2f;
			}
		}
	}
}
