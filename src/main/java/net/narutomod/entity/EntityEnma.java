
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemAdamantineNyoi;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.Minecraft;
import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketCollectItem;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEnma extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 423;
	public static final int ENTITYID_RANGED = 424;
	private static final float MODEL_SCALE = 1.25F;

	public EntityEnma(ElementsNarutomodMod instance) {
		super(instance, 850);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "enma"), ENTITYID).name("enma").tracker(84, 3, true).egg(-13358295, -3761328).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityStaff.class)
		 .id(new ResourceLocation("narutomod", "enma_staff"), ENTITYID_RANGED).name("enma_staff").tracker(64, 3, true).build());
	}

	public static class EC extends EntitySummonAnimal.Base {
		private final EntityAIWander aiWander = new EntityAIWander(this, 0.8, 50);

		public EC(World world) {
			super(world);
			this.setOGSize(0.6f, 2.0f);
			this.experienceValue = 5000;
			this.stepHeight = 16f;
			this.postScaleFixup();
		}

		public EC(EntityLivingBase entityIn) {
			super(entityIn);
			this.setOGSize(0.6f, 2.0f);
			this.experienceValue = 5000;
			this.stepHeight = 16f;
			this.postScaleFixup();
		}

		@Override
		public float getScale() {
			return MODEL_SCALE;
		}

		@Override
		public SoundEvent getAmbientSound() {
			return null;
		}

		@Override
		public SoundEvent getHurtSound(DamageSource ds) {
			return SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:fourtails_hurt"));
		}

		@Override
		public SoundEvent getDeathSound() {
			return null;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(40D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
		}

		@Override
		public boolean canAttackClass(Class <? extends EntityLivingBase > cls) {
			return !EC.class.isAssignableFrom(cls);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(1, new EntityNinjaMob.AILeapAtTarget(this, 1.4f));
			this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.2f, true) {
				@Override
				protected double getAttackReachSqr(EntityLivingBase attackTarget) {
					double d = 5d + 0.5d * attackTarget.width;
					return d * d;
				}
			});
			this.tasks.addTask(3, new EntityClone.AIFollowSummoner(this, 1.0d, 4.0F) {
				@Override
			    protected EntityLivingBase getFollowEntity() {
			        return EC.this.getSummoner();
			    }
			});
			this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityLivingBase.class, 20.0f) {
				@Override
				public boolean shouldExecute() {
					if (this.entity.getRNG().nextFloat() >= 0.1f) {
						return false;
					} else if (this.entity.getAttackTarget() != null) {
						this.closestEntity = this.entity.getAttackTarget();
					} else {
						this.closestEntity = ProcedureUtils.findNearestEntityWithinAABB(this.entity.world, this.watchedClass,
						 this.entity.getEntityBoundingBox().grow(this.maxDistanceForPlayer), this.entity, new Predicate<Entity>() {
							public boolean apply(@Nullable Entity p_apply_1_) {
								return p_apply_1_ instanceof EntityLivingBase && p_apply_1_.isEntityAlive()
								 && !p_apply_1_.equals(EC.this.getSummoner()) && !(p_apply_1_ instanceof EC)
								 && ProcedureUtils.getModifiedAttackDamage((EntityLivingBase)p_apply_1_) >= 1.0d;
							}
						 });
					}
					return this.closestEntity != null;
				}
			});
			this.tasks.addTask(6, new EntityAILookIdle(this));
		}

		@Override
		protected void dontWander(boolean set) {
			if (!set) {
				this.tasks.addTask(5, this.aiWander);
			} else {
				this.tasks.removeTask(this.aiWander);
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getTrueSource() != null && source.getTrueSource().equals(this.getSummoner())) {
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			int age = this.getAge();
			if (age <= 40) {
				this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, (40f - age) / 40f, this.rand.nextFloat() * 0.4f + 0.8f);
				Particles.Renderer particles = new Particles.Renderer(this.world);
				for (int i = 0; i < 50; i++) {
					particles.spawnParticles(Particles.Types.SMOKE,
					 this.posX, this.posY + this.rand.nextDouble() * this.height, this.posZ, 1, 0d, 0d, 0d,
					 (this.rand.nextDouble()-0.5d) * 0.6d, this.rand.nextDouble() * 0.1d,
					 (this.rand.nextDouble()-0.5d) * 0.6d, 0xD0FFFFFF, 50);
				}
				particles.send();
			}
			this.fallDistance = 0.0f;
			BlockPos pos = new BlockPos(this);
			if (this.world.getBlockState(pos).getMaterial() == Material.WATER
			 && this.world.getBlockState(pos.up()).getMaterial() != Material.WATER) {
				this.motionY = 0.01d;
				this.onGround = true;
			}
			if (!this.world.isRemote && this.getSummoner() == null) {
				this.setDead();
			}
		}

		@Override
		public int getMaxFallHeight() {
			return 30;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "EnmaEntityId";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 50d, 3d);
				Entity entity1 = stack.hasTagCompound() ? entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY)) : null;
				if (res != null && res.entityHit instanceof EC && res.entityHit.equals(entity1)) {
					entity1.setDead();
					entity.world.spawnEntity(new EntityStaff(entity, entity1.posX, entity1.posY, entity1.posZ));
				} else if (!(entity1 instanceof EC) && (!(entity instanceof EntityPlayer) || !ProcedureUtils.hasItemInInventory((EntityPlayer)entity, ItemAdamantineNyoi.block)) && power >= 1.0f) {
					Particles.Renderer particles = new Particles.Renderer(entity.world);
					particles.spawnParticles(Particles.Types.SEAL_FORMULA,
					 entity.posX, entity.posY + 0.015d, entity.posZ, 1, 0d, 0d, 0d, 0d, 0d, 0d, 40, 0, 60);
					for (int i = 0; i < 500; i++) {
						particles.spawnParticles(Particles.Types.SMOKE, entity.posX, entity.posY, entity.posZ, 1, 0d, 0d, 0d,
						 (entity.getRNG().nextDouble()-0.5d) * 0.6d, entity.getRNG().nextDouble() * 0.2d,
						 (entity.getRNG().nextDouble()-0.5d) * 0.6d, 0xD0FFFFFF, 50);
					}
					particles.send();
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
					  SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kuchiyosenojutsu")),
					  net.minecraft.util.SoundCategory.PLAYERS, 1f, 0.8f);
					entity1 = new EC(entity);
					entity1.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0f);
					net.narutomod.event.SpecialEvent.setDelayedSpawnEvent(entity.world, entity1, 0, 0, 0, entity.world.getTotalWorldTime() + 20);
					if (stack.hasTagCompound()) {
						stack.getTagCompound().setInteger(ID_KEY, entity1.getEntityId());
					}
					return true;
				}
				return false;
			}
		}
	}

	public static class EntityStaff extends EntityScalableProjectile.Base {
		private final int wait = 20;

		public EntityStaff(World worldIn) {
			super(worldIn);
			this.setOGSize(0.5f, 0.5f);
		}

		public EntityStaff(EntityLivingBase summonerIn, double x, double y, double z) {
			super(summonerIn);
			this.setOGSize(0.5f, 0.5f);
			this.setLocationAndAngles(x, y, z, (this.rand.nextFloat()-0.5f) * 360f, 0f);
			this.motionY = 0.5d;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksAlive > this.wait && this.shootingEntity != null && !this.onGround) {
				Vec3d vec = this.shootingEntity.getPositionVector().subtract(this.getPositionVector());
				this.shoot(vec.x, vec.y, vec.z, 0.9f, 0f);
			}
		}

		@Override
		public void renderParticles() {
		}

		@Override
		public void onCollideWithPlayer(EntityPlayer entityIn) {
			if (!this.world.isRemote) {
				boolean flag = false;
				boolean flag1 = ProcedureUtils.hasItemInInventory(entityIn, ItemAdamantineNyoi.block);
				boolean flag2 = entityIn.equals(this.shootingEntity) && this.ticksAlive > 15;
				if (flag2 && !flag1) {
				 	ItemStack stack = ItemAdamantineNyoi.createStackBoundTo(entityIn);
					flag = entityIn.inventory.addItemStackToInventory(stack);
				}
				if (flag) {
	            	((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketCollectItem(this.getEntityId(), entityIn.getEntityId(), 1));
				}
				if (flag || (flag2 && flag1)) {
					this.setDead();
				}
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && result.entityHit.equals(this.shootingEntity)) {
				return;
			}
			if (!this.world.isRemote && result.entityHit != null) {
				result.entityHit.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.shootingEntity), 18f);
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> {
				return new RenderLiving(renderManager, new ModelEnma(), 0.5f * MODEL_SCALE) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/enma.png");
					protected ResourceLocation getEntityTexture(Entity entity) {
						return this.texture;
					}
				};
			});
			RenderingRegistry.registerEntityRenderingHandler(EntityStaff.class, renderManager -> {
				return new RenderStaff(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderStaff extends Render<EntityStaff> {
			protected final ItemStack item;
			private final RenderItem itemRenderer;
	
			public RenderStaff(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.item = new ItemStack(ItemAdamantineNyoi.block);
				this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
			}

		    @Override
		    public void doRender(EntityStaff entity, double x, double y, double z, float entityYaw, float partialTicks) {
		    	float scale = entity.getEntityScale();
		        GlStateManager.pushMatrix();
		        GlStateManager.translate((float)x, (float)y, (float)z);
		        GlStateManager.rotate(-ProcedureUtils.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
		        float f = entity.onGround ? entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks : (partialTicks + entity.ticksExisted) * 30.0F;
		        GlStateManager.rotate(f, 1.0F, 0.0F, 0.0F);
		        GlStateManager.translate(0.0F, scale * 0.25F, 0.0F);
		        GlStateManager.scale(scale, scale, scale);
		        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		        this.itemRenderer.renderItem(this.item, ItemCameraTransforms.TransformType.GROUND);
		        GlStateManager.popMatrix();
		    }
		
			@Override
		    protected ResourceLocation getEntityTexture(EntityStaff entity) {
		        return TextureMap.LOCATION_BLOCKS_TEXTURE;
		    }
		}

		// Made with Blockbench 4.7.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelEnma extends ModelBiped {
			//private final ModelRenderer bipedHead;
			private final ModelRenderer hair;
			private final ModelRenderer bone44;
			private final ModelRenderer bone49;
			private final ModelRenderer bone50;
			private final ModelRenderer bone51;
			private final ModelRenderer bone52;
			private final ModelRenderer bone53;
			private final ModelRenderer bone54;
			private final ModelRenderer bone55;
			private final ModelRenderer bone56;
			private final ModelRenderer bone57;
			private final ModelRenderer bone58;
			private final ModelRenderer bone59;
			private final ModelRenderer bone60;
			private final ModelRenderer bone61;
			private final ModelRenderer bone62;
			private final ModelRenderer bone63;
			private final ModelRenderer beard;
			private final ModelRenderer goatee;
			private final ModelRenderer bone26;
			private final ModelRenderer bone27;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			//private final ModelRenderer bipedHeadWear;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer chest;
			private final ModelRenderer bodyLayer;
			private final ModelRenderer belt;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone5;
			private final ModelRenderer bone8;
			private final ModelRenderer bone11;
			private final ModelRenderer tail;
			private final ModelRenderer tail0;
			private final ModelRenderer tail1;
			private final ModelRenderer tail2;
			private final ModelRenderer tail3;
			//private final ModelRenderer bipedRightArm;
			private final ModelRenderer bone;
			private final ModelRenderer bone2;
			//private final ModelRenderer bipedLeftArm;
			private final ModelRenderer bone30;
			private final ModelRenderer bone31;
			//private final ModelRenderer bipedRightLeg;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer rightFoot;
			private final ModelRenderer bone143;
			private final ModelRenderer bone144;
			private final ModelRenderer bone9;
			private final ModelRenderer bone10;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
			private final ModelRenderer bone24;
			private final ModelRenderer bone25;
			//private final ModelRenderer bipedLeftLeg;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer leftFoot;
			private final ModelRenderer bone34;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer bone38;
			private final ModelRenderer bone39;
			private final ModelRenderer bone40;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
			private final ModelRenderer bone43;
			public ModelEnma() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.addChild(hair);
				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(0.0F, -6.0F, -4.1F);
				hair.addChild(bone44);
				setRotationAngle(bone44, 0.1309F, 0.1309F, 0.0F);
				bone44.cubeList.add(new ModelBox(bone44, 0, 16, -2.0F, -2.4957F, 0.1653F, 6, 2, 8, 0.1F, false));
				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(0.0F, -7.5F, -3.35F);
				hair.addChild(bone49);
				setRotationAngle(bone49, -0.2618F, 0.1745F, 0.0F);
				bone49.cubeList.add(new ModelBox(bone49, 0, 16, -2.0F, -2.0F, 0.1F, 6, 2, 8, 0.2F, false));
				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(0.0F, -8.5F, -2.1F);
				hair.addChild(bone50);
				setRotationAngle(bone50, -0.6981F, 0.2182F, 0.0F);
				bone50.cubeList.add(new ModelBox(bone50, 0, 16, -2.0F, -2.0F, 0.1F, 6, 2, 8, 0.3F, false));
				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(0.0F, -9.0F, -0.1F);
				hair.addChild(bone51);
				setRotationAngle(bone51, -1.0472F, 0.2618F, 0.0F);
				bone51.cubeList.add(new ModelBox(bone51, 0, 16, -2.0F, -2.0F, 0.1F, 6, 2, 8, 0.4F, false));
				bone52 = new ModelRenderer(this);
				bone52.setRotationPoint(0.0F, -8.5F, 1.9F);
				hair.addChild(bone52);
				setRotationAngle(bone52, -1.2217F, 0.2182F, 0.0F);
				bone52.cubeList.add(new ModelBox(bone52, 0, 16, -2.0F, -2.0F, 0.1F, 6, 2, 8, 0.5F, false));
				bone53 = new ModelRenderer(this);
				bone53.setRotationPoint(0.0F, -7.0F, 2.9F);
				hair.addChild(bone53);
				setRotationAngle(bone53, -1.3526F, 0.1309F, 0.0F);
				bone53.cubeList.add(new ModelBox(bone53, 0, 16, -2.0F, -2.0F, 0.1F, 6, 2, 8, 0.6F, false));
				bone54 = new ModelRenderer(this);
				bone54.setRotationPoint(0.0F, -3.0F, 3.15F);
				hair.addChild(bone54);
				setRotationAngle(bone54, -1.309F, 0.0436F, 0.0F);
				bone54.cubeList.add(new ModelBox(bone54, 0, 16, -2.0F, -2.0F, 0.1F, 6, 2, 8, 0.3F, false));
				bone55 = new ModelRenderer(this);
				bone55.setRotationPoint(0.0F, 1.0F, 4.15F);
				hair.addChild(bone55);
				setRotationAngle(bone55, -1.4835F, -0.0873F, 0.0F);
				bone55.cubeList.add(new ModelBox(bone55, 0, 16, -2.0F, -2.0F, 0.1F, 6, 2, 8, 0.0F, false));
				bone56 = new ModelRenderer(this);
				bone56.setRotationPoint(0.0F, -6.0F, -4.1F);
				hair.addChild(bone56);
				setRotationAngle(bone56, 0.1309F, -0.1309F, 0.0F);
				bone56.cubeList.add(new ModelBox(bone56, 0, 16, -4.0F, -2.4957F, 0.1653F, 6, 2, 8, 0.1F, true));
				bone57 = new ModelRenderer(this);
				bone57.setRotationPoint(0.0F, -7.5F, -3.35F);
				hair.addChild(bone57);
				setRotationAngle(bone57, -0.2618F, -0.1745F, 0.0F);
				bone57.cubeList.add(new ModelBox(bone57, 0, 16, -4.0F, -2.0F, 0.1F, 6, 2, 8, 0.2F, true));
				bone58 = new ModelRenderer(this);
				bone58.setRotationPoint(0.0F, -8.5F, -2.1F);
				hair.addChild(bone58);
				setRotationAngle(bone58, -0.6981F, -0.2182F, 0.0F);
				bone58.cubeList.add(new ModelBox(bone58, 0, 16, -4.0F, -2.0F, 0.1F, 6, 2, 8, 0.3F, true));
				bone59 = new ModelRenderer(this);
				bone59.setRotationPoint(0.0F, -9.0F, -0.1F);
				hair.addChild(bone59);
				setRotationAngle(bone59, -1.0472F, -0.2618F, 0.0F);
				bone59.cubeList.add(new ModelBox(bone59, 0, 16, -4.0F, -2.0F, 0.1F, 6, 2, 8, 0.4F, true));
				bone60 = new ModelRenderer(this);
				bone60.setRotationPoint(0.0F, -8.5F, 1.9F);
				hair.addChild(bone60);
				setRotationAngle(bone60, -1.2217F, -0.2182F, 0.0F);
				bone60.cubeList.add(new ModelBox(bone60, 0, 16, -4.0F, -2.0F, 0.1F, 6, 2, 8, 0.5F, true));
				bone61 = new ModelRenderer(this);
				bone61.setRotationPoint(0.0F, -7.0F, 2.9F);
				hair.addChild(bone61);
				setRotationAngle(bone61, -1.3526F, -0.1309F, 0.0F);
				bone61.cubeList.add(new ModelBox(bone61, 0, 16, -4.0F, -2.0F, 0.1F, 6, 2, 8, 0.6F, true));
				bone62 = new ModelRenderer(this);
				bone62.setRotationPoint(0.0F, -3.0F, 3.15F);
				hair.addChild(bone62);
				setRotationAngle(bone62, -1.309F, -0.0436F, 0.0F);
				bone62.cubeList.add(new ModelBox(bone62, 0, 16, -4.0F, -2.0F, 0.1F, 6, 2, 8, 0.3F, true));
				bone63 = new ModelRenderer(this);
				bone63.setRotationPoint(0.0F, 1.0F, 4.15F);
				hair.addChild(bone63);
				setRotationAngle(bone63, -1.4835F, 0.0873F, 0.0F);
				bone63.cubeList.add(new ModelBox(bone63, 0, 16, -4.0F, -2.0F, 0.1F, 6, 2, 8, 0.0F, true));
				beard = new ModelRenderer(this);
				beard.setRotationPoint(0.0F, -1.0F, -4.2F);
				bipedHead.addChild(beard);
				goatee = new ModelRenderer(this);
				goatee.setRotationPoint(0.0F, 0.0F, 0.0F);
				beard.addChild(goatee);
				setRotationAngle(goatee, 0.2182F, 0.0F, 0.0F);
				goatee.cubeList.add(new ModelBox(goatee, 0, 57, -4.0F, 0.0F, 0.0F, 8, 3, 4, 0.05F, false));
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(-4.0F, -3.0F, 0.2F);
				beard.addChild(bone26);
				setRotationAngle(bone26, -0.0873F, 0.0F, 0.0873F);
				bone26.cubeList.add(new ModelBox(bone26, 32, 45, -0.061F, -0.6947F, -0.0608F, 3, 3, 3, 0.0F, false));
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, 2.25F, 0.0F);
				bone26.addChild(bone27);
				setRotationAngle(bone27, 0.0436F, 0.0F, -0.0873F);
				bone27.cubeList.add(new ModelBox(bone27, 32, 51, -0.0634F, 0.0499F, -0.063F, 3, 4, 3, 0.0F, false));
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(4.0F, -3.0F, 0.2F);
				beard.addChild(bone28);
				setRotationAngle(bone28, -0.0873F, 0.0F, -0.0873F);
				bone28.cubeList.add(new ModelBox(bone28, 32, 45, -2.939F, -0.6947F, -0.0608F, 3, 3, 3, 0.0F, true));
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(0.0F, 2.25F, 0.0F);
				bone28.addChild(bone29);
				setRotationAngle(bone29, 0.0436F, 0.0F, 0.0873F);
				bone29.cubeList.add(new ModelBox(bone29, 32, 51, -2.9366F, 0.0499F, -0.063F, 3, 4, 3, 0.0F, true));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 24, 8, -4.0F, -6.5F, -4.0F, 8, 2, 8, 0.1F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 24, 26, -4.0F, 6.0F, -0.75F, 8, 6, 4, 0.5F, false));
				chest = new ModelRenderer(this);
				chest.setRotationPoint(0.0F, 5.5F, 3.75F);
				bipedBody.addChild(chest);
				setRotationAngle(chest, 0.2182F, 0.0F, 0.0F);
				chest.cubeList.add(new ModelBox(chest, 24, 36, -4.0F, -5.5F, -4.5F, 8, 5, 4, 0.5F, false));
				bodyLayer = new ModelRenderer(this);
				bodyLayer.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(bodyLayer);
				bodyLayer.cubeList.add(new ModelBox(bodyLayer, 0, 26, -4.0F, 6.5F, -0.75F, 8, 6, 4, 1.25F, false));
				belt = new ModelRenderer(this);
				belt.setRotationPoint(0.0F, 24.0F, 1.25F);
				bodyLayer.addChild(belt);
				belt.cubeList.add(new ModelBox(belt, 24, 0, -4.0F, -15.5F, -2.0F, 8, 1, 4, 0.8F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(-0.25F, -15.25F, -3.0F);
				belt.addChild(bone6);
				setRotationAngle(bone6, -0.2182F, 0.0F, 0.3491F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 16, -1.0F, -1.0F, -0.5F, 2, 4, 1, 0.0F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 2.75F, -0.5F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, 0.0873F, 0.0F, -0.1745F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 0, -1.0F, 0.0F, 0.0F, 2, 4, 1, 0.0F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.25F, -15.25F, -3.0F);
				belt.addChild(bone5);
				setRotationAngle(bone5, -0.2182F, 0.0F, -0.3491F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 16, -1.0F, -1.0F, -0.5F, 2, 4, 1, 0.0F, true));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, 2.75F, -0.5F);
				bone5.addChild(bone8);
				setRotationAngle(bone8, 0.0873F, 0.0F, 0.1745F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 0, -1.0F, 0.0F, 0.0F, 2, 4, 1, 0.0F, true));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, 5.5F, 3.75F);
				bodyLayer.addChild(bone11);
				setRotationAngle(bone11, 0.2182F, 0.0F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 0, 36, -4.0F, -5.75F, -4.5F, 8, 5, 4, 1.25F, false));
				tail = new ModelRenderer(this);
				tail.setRotationPoint(0.0F, 11.0F, 3.25F);
				bipedBody.addChild(tail);
				setRotationAngle(tail, 0.5236F, 0.0F, 0.0F);
				tail.cubeList.add(new ModelBox(tail, 56, 16, -1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F, false));
				tail0 = new ModelRenderer(this);
				tail0.setRotationPoint(0.0F, 3.5F, 0.0F);
				tail.addChild(tail0);
				setRotationAngle(tail0, 0.2618F, 0.0F, 0.0F);
				tail0.cubeList.add(new ModelBox(tail0, 56, 16, -1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F, false));
				tail1 = new ModelRenderer(this);
				tail1.setRotationPoint(0.0F, 3.5F, 0.0F);
				tail0.addChild(tail1);
				setRotationAngle(tail1, 0.2618F, 0.0F, 0.0F);
				tail1.cubeList.add(new ModelBox(tail1, 56, 16, -1.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F, false));
				tail2 = new ModelRenderer(this);
				tail2.setRotationPoint(0.0F, 3.5F, 0.0F);
				tail1.addChild(tail2);
				setRotationAngle(tail2, 0.2618F, 0.0F, 0.0F);
				tail2.cubeList.add(new ModelBox(tail2, 56, 16, -1.0F, 0.0F, -1.0F, 2, 4, 2, -0.1F, false));
				tail3 = new ModelRenderer(this);
				tail3.setRotationPoint(0.0F, 3.5F, 0.0F);
				tail2.addChild(tail3);
				setRotationAngle(tail3, 0.2618F, 0.0F, 0.0F);
				tail3.cubeList.add(new ModelBox(tail3, 56, 16, -1.0F, 0.0F, -1.0F, 2, 4, 2, -0.2F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-6.0F, 2.0F, 0.0F);
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightArm.addChild(bone);
				setRotationAngle(bone, 0.0F, -0.5236F, 0.2618F);
				bone.cubeList.add(new ModelBox(bone, 0, 45, -3.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F, false));
				bone.cubeList.add(new ModelBox(bone, 32, 18, -4.0F, -1.75F, -2.0F, 6, 4, 4, 0.8F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-1.0F, 6.0F, 2.0F);
				bone.addChild(bone2);
				setRotationAngle(bone2, -0.5236F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 44, 41, -2.0F, 0.0F, -4.0F, 4, 8, 4, -0.1F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(6.0F, 2.0F, 0.0F);
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftArm.addChild(bone30);
				setRotationAngle(bone30, 0.0F, 0.5236F, -0.2618F);
				bone30.cubeList.add(new ModelBox(bone30, 0, 45, -1.0F, -2.0F, -2.0F, 4, 8, 4, 0.0F, true));
				bone30.cubeList.add(new ModelBox(bone30, 32, 18, -2.0F, -1.75F, -2.0F, 6, 4, 4, 0.8F, true));
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(1.0F, 6.0F, 2.0F);
				bone30.addChild(bone31);
				setRotationAngle(bone31, -0.5236F, 0.0F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 44, 41, -2.0F, 0.0F, -4.0F, 4, 8, 4, -0.1F, true));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-0.1F, -1.0F, 1.25F);
				bipedRightLeg.addChild(bone3);
				setRotationAngle(bone3, -0.2618F, 0.4363F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 48, 0, -1.9F, 1.0F, -2.0F, 4, 6, 4, 0.5F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, 7.5F, -2.0F);
				bone3.addChild(bone4);
				setRotationAngle(bone4, 0.2618F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 16, 45, -1.9F, -0.1028F, 0.0789F, 4, 6, 4, 0.1F, false));
				rightFoot = new ModelRenderer(this);
				rightFoot.setRotationPoint(0.2F, 5.25F, 2.25F);
				bone4.addChild(rightFoot);
				rightFoot.cubeList.add(new ModelBox(rightFoot, 48, 10, -1.6F, -0.75F, -3.0F, 3, 1, 4, 0.0F, false));
				rightFoot.cubeList.add(new ModelBox(rightFoot, 48, 22, -1.6F, -0.25F, -3.0F, 3, 1, 4, 0.0F, false));
				bone143 = new ModelRenderer(this);
				bone143.setRotationPoint(1.1F, -0.15F, -2.75F);
				rightFoot.addChild(bone143);
				bone143.cubeList.add(new ModelBox(bone143, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
				bone144 = new ModelRenderer(this);
				bone144.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone143.addChild(bone144);
				setRotationAngle(bone144, 0.5236F, 0.0F, 0.0F);
				bone144.cubeList.add(new ModelBox(bone144, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(1.1F, -0.15F, -0.75F);
				rightFoot.addChild(bone9);
				setRotationAngle(bone9, 0.0F, -0.5236F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone9.addChild(bone10);
				setRotationAngle(bone10, 0.5236F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(-0.05F, -0.15F, -2.75F);
				rightFoot.addChild(bone20);
				bone20.cubeList.add(new ModelBox(bone20, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone20.addChild(bone21);
				setRotationAngle(bone21, 0.5236F, 0.0F, 0.0F);
				bone21.cubeList.add(new ModelBox(bone21, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(-1.2F, -0.15F, -2.75F);
				rightFoot.addChild(bone22);
				bone22.cubeList.add(new ModelBox(bone22, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone22.addChild(bone23);
				setRotationAngle(bone23, 0.5236F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(-1.2F, -0.15F, -2.0F);
				rightFoot.addChild(bone24);
				setRotationAngle(bone24, 0.0F, 0.4363F, 0.0F);
				bone24.cubeList.add(new ModelBox(bone24, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, false));
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone24.addChild(bone25);
				setRotationAngle(bone25, 0.5236F, 0.0F, 0.0F);
				bone25.cubeList.add(new ModelBox(bone25, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(0.1F, -1.0F, 1.25F);
				bipedLeftLeg.addChild(bone32);
				setRotationAngle(bone32, -0.2618F, -0.4363F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 48, 0, -2.1F, 1.0F, -2.0F, 4, 6, 4, 0.5F, true));
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, 7.5F, -2.0F);
				bone32.addChild(bone33);
				setRotationAngle(bone33, 0.2618F, 0.0F, 0.0F);
				bone33.cubeList.add(new ModelBox(bone33, 16, 45, -2.1F, -0.1028F, 0.0789F, 4, 6, 4, 0.1F, true));
				leftFoot = new ModelRenderer(this);
				leftFoot.setRotationPoint(-0.2F, 5.25F, 2.25F);
				bone33.addChild(leftFoot);
				leftFoot.cubeList.add(new ModelBox(leftFoot, 48, 10, -1.4F, -0.75F, -3.0F, 3, 1, 4, 0.0F, true));
				leftFoot.cubeList.add(new ModelBox(leftFoot, 48, 22, -1.4F, -0.25F, -3.0F, 3, 1, 4, 0.0F, true));
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(-1.1F, -0.15F, -2.75F);
				leftFoot.addChild(bone34);
				bone34.cubeList.add(new ModelBox(bone34, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone34.addChild(bone35);
				setRotationAngle(bone35, 0.5236F, 0.0F, 0.0F);
				bone35.cubeList.add(new ModelBox(bone35, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(-1.1F, -0.15F, -0.75F);
				leftFoot.addChild(bone36);
				setRotationAngle(bone36, 0.0F, 0.5236F, 0.0F);
				bone36.cubeList.add(new ModelBox(bone36, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone36.addChild(bone37);
				setRotationAngle(bone37, 0.5236F, 0.0F, 0.0F);
				bone37.cubeList.add(new ModelBox(bone37, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(0.05F, -0.15F, -2.75F);
				leftFoot.addChild(bone38);
				bone38.cubeList.add(new ModelBox(bone38, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone38.addChild(bone39);
				setRotationAngle(bone39, 0.5236F, 0.0F, 0.0F);
				bone39.cubeList.add(new ModelBox(bone39, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(1.2F, -0.15F, -2.75F);
				leftFoot.addChild(bone40);
				bone40.cubeList.add(new ModelBox(bone40, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone40.addChild(bone41);
				setRotationAngle(bone41, 0.5236F, 0.0F, 0.0F);
				bone41.cubeList.add(new ModelBox(bone41, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(1.2F, -0.15F, -2.0F);
				leftFoot.addChild(bone42);
				setRotationAngle(bone42, 0.0F, -0.4363F, 0.0F);
				bone42.cubeList.add(new ModelBox(bone42, 24, 21, -0.5F, -0.5F, -2.05F, 1, 1, 2, 0.0F, true));
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(0.0F, 0.0F, -1.9F);
				bone42.addChild(bone43);
				setRotationAngle(bone43, 0.5236F, 0.0F, 0.0F);
				bone43.cubeList.add(new ModelBox(bone43, 24, 18, -0.5F, -0.5F, -1.8F, 1, 1, 2, -0.2F, true));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				float scale = ((EC)entity).getScale();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * scale, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				super.render(entity, f, f1, f2, f3, f4, f5);
				GlStateManager.popMatrix();
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f * 2f / e.height, f1, f2, f3, f4, f5, e);
				tail0.rotateAngleX = 0.2618F + MathHelper.sin(f2 * 0.1f) * 0.0873f;
				tail1.rotateAngleX = 0.2618F + MathHelper.sin(f2 * 0.1f) * 0.0873f;
				tail2.rotateAngleX = 0.2618F + MathHelper.sin(f2 * 0.1f) * 0.0873f;
			}
		}
	}
}
