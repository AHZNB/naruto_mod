
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.block.BlockLightSource;
import net.narutomod.item.ItemFuton;
import net.narutomod.item.ItemSenjutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureLightSourceSetBlock;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.event.EventSphericalExplosion;
import net.narutomod.Particles;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityRasenshuriken extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 344;
	public static final int ENTITYID_RANGED = 345;

	public EntityRasenshuriken(ElementsNarutomodMod instance) {
		super(instance, 698);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "rasenshuriken"), ENTITYID).name("rasenshuriken").tracker(96, 3, true).build());
	}

	public static class EC extends EntityScalableProjectile.Base implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> IMPACT_TICKS = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> BALL_COLOR = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		//private final int s = 50;
		//private int[] randomStartTick = new int[s];
		private final int growTime = 20;
		private float fullScale;
		private Vec3d impactVec;
		private RayTraceResult targetTrace;
		protected float impactDamageMultiplier = 2.0f;
		private DamageSource damageSource;

		public EC(World a) {
			super(a);
			this.setOGSize(2.5F, 0.5F);
			this.isImmuneToFire = true;
			this.damageSource = ItemJutsu.NINJUTSU_DAMAGE.setDamageBypassesArmor();
		}

		public EC(EntityLivingBase shooter, float scale) {
			super(shooter);
			this.setOGSize(2.5F, 0.5F);
			this.setPosition(shooter.posX, shooter.posY + shooter.height + 0.5D, shooter.posZ);
			this.fullScale = scale;
			this.setEntityScale(0.1f);
			this.isImmuneToFire = true;
			this.damageSource = ItemJutsu.causeJutsuDamage(this, shooter).setDamageBypassesArmor();
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.isDamageSourceSenjutsu(this.damageSource) ? ItemJutsu.JutsuEnum.Type.SENJUTSU : ItemJutsu.JutsuEnum.Type.FUTON;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(IMPACT_TICKS, Integer.valueOf(0));
			this.getDataManager().register(BALL_COLOR, Integer.valueOf(0x20A9DEFF));
		}

		public int getImpactTicks() {
			return ((Integer)this.getDataManager().get(IMPACT_TICKS)).intValue();
		}

		protected void setImpactTicks(int ticks) {
			this.getDataManager().set(IMPACT_TICKS, Integer.valueOf(ticks));
		}

		private int getBallColor() {
			return ((Integer)this.getDataManager().get(BALL_COLOR)).intValue();
		}

		protected void setBallColor(int color) {
			this.getDataManager().set(BALL_COLOR, Integer.valueOf(color));
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.shootingEntity != null) {
				ProcedureSync.EntityNBTTag.removeAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.updateInFlightRotations();
			if (this.getImpactTicks() > 0) {
				this.onImpactUpdate();
				return;
			}
			if (!this.world.isRemote && this.ticksAlive == 1 && this.shootingEntity instanceof EntityPlayer) {
				ProcedureSync.EntityNBTTag.setAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose, true);
			}
			if (!this.world.isRemote && this.shootingEntity != null) {
				if (this.ticksAlive < this.growTime) {
					this.setEntityScale(this.fullScale * (this.ticksAlive + 1) / this.growTime);
					this.setPosition(this.shootingEntity.posX, this.shootingEntity.posY + this.shootingEntity.height + 0.5d, this.shootingEntity.posZ);
				} else if (this.targetTrace == null || this.targetTrace.entityHit == null) {
					if (this.getDistance(this.shootingEntity) < 48d) {
						RayTraceResult rt = ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d, 3d);
						if (!this.equals(rt.entityHit) && !this.shootingEntity.equals(rt.entityHit)) {
							this.targetTrace = rt;
						}
					}
				}
				if (this.targetTrace != null) {
					this.motionX *= 0.9d;
					this.motionY *= 0.9d;
					this.motionZ *= 0.9d;
					this.shoot(this.targetTrace.hitVec.x - this.posX, this.targetTrace.hitVec.y - this.posY, this.targetTrace.hitVec.z - this.posZ, 0.99f, 0f);
				}
			}
			if (this.fullScale >= 4.0f) {
				ProcedureLightSourceSetBlock.execute(this.world, MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));
			}
			if (this.ticksAlive % 80 == 79) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:wind")), 1, 1f);
			}
			if (this.ticksInAir > 200 || (!this.world.isRemote && this.shootingEntity == null && !this.isLaunched())) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if ((result.typeOfHit == RayTraceResult.Type.BLOCK
			  && this.world.getBlockState(result.getBlockPos()).getBlock() == BlockLightSource.block)
			 || (result.entityHit != null && result.entityHit.equals(this.shootingEntity))
			 || (result.typeOfHit == RayTraceResult.Type.BLOCK && this.fullScale > 1.0f && this.ticksInAir < 15)) {
				return;
			}
			if (!this.world.isRemote && this.shootingEntity != null) {
				ProcedureSync.EntityNBTTag.removeAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose);
			}
			this.setImpactTicks(1);
			this.haltMotion();
			this.impactVec = result.hitVec;
		}

		protected Vec3d getImpactVec() {
			return this.impactVec;
		}

		protected void doImpactDamage() {
			ProcedureAoeCommand.set(this.world, this.impactVec.x, this.impactVec.y, this.impactVec.z, 0d, this.width/2)
			  .exclude(this.shootingEntity).exclude(EntityTruthSeekerBall.EntityCustom.class).resetHurtResistanceTime()
			  .damageEntities(this.damageSource, this.fullScale * this.impactDamageMultiplier)
			  .motion(0d, 0d, 0d);
		}

		private void onImpactUpdate() {
			int impactTicks = this.getImpactTicks() + 1;
			this.setImpactTicks(impactTicks);
			if (impactTicks <= 3) {
				this.setOGSize(0.5f, 0.5f);
			}
			if (!this.world.isRemote) {
				if (impactTicks % 4 == 0) {
					this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvent.REGISTRY
					  .getObject(new ResourceLocation("narutomod:rasenshuriken_explode")), SoundCategory.NEUTRAL, 5, 1f);
				}
				float scale = this.getEntityScale() * (impactTicks <= 20 ? 1.15f : 1.001f);
				double d = (this.height * scale / this.getEntityScale() - this.height) / 2;
				this.setEntityScale(scale);
				this.setPosition(this.impactVec.x, this.posY - d, this.impactVec.z);
				this.doImpactDamage();
				new EventSphericalExplosion(this.world, null, (int)Math.floor(this.impactVec.x), (int)this.impactVec.y, 
				  (int)Math.floor(this.impactVec.z), (int) Math.ceil(this.width/2) + 1, 0, 0f, false, false);
				Particles.Renderer particles = new Particles.Renderer(this.world);
				for (int i = 0; i < 300; i++) {
					particles.spawnParticles(Particles.Types.SMOKE, this.posX, this.posY+this.height*0.5, this.posZ,
					  1, 1d, 0d, 1d, (this.rand.nextDouble()-0.5d) * this.fullScale * 4.0d,
					  0.5d * this.rand.nextGaussian(), 4.0d * (this.rand.nextDouble()-0.5d) * this.fullScale,
					  0x10FFFFFF, (int)(scale * 16f), 20);
				}
				particles.send();
				if (impactTicks >= 200) {
					this.setDead();
				}
			}
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void renderParticles() {
			if (this.world.isRemote && this.getImpactTicks() == 0) {
				Particles.Renderer particles = new Particles.Renderer(this.world);
				for (int i = 0; i < this.growTime * 10; i++) {
					particles.spawnParticles(Particles.Types.SMOKE, this.posX, this.posY+this.height*0.5, this.posZ, 1, 1d, 0d, 1d, 
					  0.6d * this.rand.nextGaussian(), 0.1d * this.rand.nextGaussian(), 0.6d * this.rand.nextGaussian(), 0x10FFFFFF,
					  (int)(this.getEntityScale() * 12), 0);
				}
				particles.send();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.fullScale = compound.getFloat("fullScale");
			int i = compound.getInteger("impactTicks");
			this.setImpactTicks(i);
			if (i > 0) {
				this.impactVec = new Vec3d(compound.getDouble("impactVecX"), compound.getDouble("impactVecY"), compound.getDouble("impactVecZ"));
			}
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setFloat("fullScale", this.fullScale);
			int i = this.getImpactTicks();
			compound.setInteger("impactTicks", i);
			if (i > 0) {
				compound.setDouble("impactVecX", this.impactVec.x);
				compound.setDouble("impactVecY", this.impactVec.y);
				compound.setDouble("impactVecZ", this.impactVec.z);
			}
		}

		private static EC create(EntityLivingBase entity, float power, boolean isSenjutsu) {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
				  SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rasenshuriken")),
				  SoundCategory.PLAYERS, 5, 1f);
			EC entity1 = new EC(entity, power);
			if (isSenjutsu) {
				entity1.damageSource = ItemJutsu.causeSenjutsuDamage(entity1, entity).setDamageBypassesArmor();
			}
			entity.world.spawnEntity(entity1);
			return entity1;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if ((stack.getItem() == ItemFuton.block && power >= 0.1f) || (stack.getItem() == ItemSenjutsu.block && power >= 2.0f)) {
					EC.create(entity, power, stack.getItem() == ItemSenjutsu.block);
					return true;
				}
				return false;
			}

			@Override
			public float getBasePower() {
				return 0.0f;
			}
	
			@Override
			public float getPowerupDelay() {
				return 300.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 2.0f;
			}
		}

		public static class SageModeVairant extends Jutsu {
			@Override
			public float getBasePower() {
				return 1.9f;
			}
	
			@Override
			public float getPowerupDelay() {
				return 300.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 6.0f;
			}
		}

		public static class TSBVariant implements ItemJutsu.IJutsuCallback {
			private static final float multiplier = 8.0f;
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				EC entity1 = EC.create(entity, 4.0f, true);
				entity1.setBallColor(0xE0101010);
				entity1.impactDamageMultiplier = this.multiplier;
				return true;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderRasenshuriken(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderRasenshuriken extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/rasenshuriken.png");
			protected ModelBase mainModel;

			public RenderRasenshuriken(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.mainModel = new ModelRasenshuriken();
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				float scale = entity.getEntityScale();
				float f = (float)entity.ticksExisted + partialTicks;
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + (0.25F * scale), z);
				GlStateManager.rotate(-ProcedureUtils.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationRoll + (entity.rotationRoll - entity.prevRotationRoll) * partialTicks, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				this.mainModel.render(entity, 0.0F, 0.0F, f, 0.0F, 0.0F, 0.0625F);
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 3.5.4
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelRasenshuriken extends ModelBase {
			private final ModelRenderer flaps;
			private final ModelRenderer bone3;
			private final ModelRenderer bone6;
			private final ModelRenderer bone5;
			private final ModelRenderer bone4;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer bone34;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer ball;
			private final ModelRenderer hexadecagon;
			private final ModelRenderer hexadecagon_r1;
			private final ModelRenderer hexadecagon_r2;
			private final ModelRenderer hexadecagon_r3;
			private final ModelRenderer hexadecagon_r4;
			private final ModelRenderer hexadecagon9;
			private final ModelRenderer hexadecagon_r5;
			private final ModelRenderer hexadecagon_r6;
			private final ModelRenderer hexadecagon_r7;
			private final ModelRenderer hexadecagon_r8;
			private final ModelRenderer hexadecagon10;
			private final ModelRenderer hexadecagon_r9;
			private final ModelRenderer hexadecagon_r10;
			private final ModelRenderer hexadecagon_r11;
			private final ModelRenderer hexadecagon_r12;
			private final ModelRenderer hexadecagon11;
			private final ModelRenderer hexadecagon_r13;
			private final ModelRenderer hexadecagon_r14;
			private final ModelRenderer hexadecagon_r15;
			private final ModelRenderer hexadecagon_r16;
			private final ModelRenderer hexadecagon12;
			private final ModelRenderer hexadecagon_r17;
			private final ModelRenderer hexadecagon_r18;
			private final ModelRenderer hexadecagon_r19;
			private final ModelRenderer hexadecagon_r20;
			private final ModelRenderer hexadecagon13;
			private final ModelRenderer hexadecagon_r21;
			private final ModelRenderer hexadecagon_r22;
			private final ModelRenderer hexadecagon_r23;
			private final ModelRenderer hexadecagon_r24;
			private final ModelRenderer hexadecagon14;
			private final ModelRenderer hexadecagon_r25;
			private final ModelRenderer hexadecagon_r26;
			private final ModelRenderer hexadecagon_r27;
			private final ModelRenderer hexadecagon_r28;
			private final ModelRenderer hexadecagon15;
			private final ModelRenderer hexadecagon_r29;
			private final ModelRenderer hexadecagon_r30;
			private final ModelRenderer hexadecagon_r31;
			private final ModelRenderer hexadecagon_r32;

			public ModelRasenshuriken() {
				textureWidth = 32;
				textureHeight = 32;

				flaps = new ModelRenderer(this);
				flaps.setRotationPoint(0.0F, 0.0F, 0.0F);

				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone3);
				setRotationAngle(bone3, 0.0F, 0.0F, 1.5708F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone6);
				setRotationAngle(bone6, -1.5708F, 0.0F, 1.5708F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone5);
				setRotationAngle(bone5, 3.1416F, 0.0F, 1.5708F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone4);
				setRotationAngle(bone4, 1.5708F, 0.0F, 1.5708F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone28);
				setRotationAngle(bone28, -0.0436F, -0.0436F, 0.0436F);


				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone28.addChild(bone29);
				setRotationAngle(bone29, 0.0F, 0.0F, 1.5708F);
				bone29.cubeList.add(new ModelBox(bone29, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone28.addChild(bone30);
				setRotationAngle(bone30, -1.5708F, 0.0F, 1.5708F);
				bone30.cubeList.add(new ModelBox(bone30, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone28.addChild(bone31);
				setRotationAngle(bone31, 3.1416F, 0.0F, 1.5708F);
				bone31.cubeList.add(new ModelBox(bone31, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone28.addChild(bone32);
				setRotationAngle(bone32, 1.5708F, 0.0F, 1.5708F);
				bone32.cubeList.add(new ModelBox(bone32, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, 0.0F, 0.0F);
				flaps.addChild(bone33);
				setRotationAngle(bone33, 0.0436F, 0.0436F, -0.0436F);


				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone33.addChild(bone34);
				setRotationAngle(bone34, 0.0F, 0.0F, 1.5708F);
				bone34.cubeList.add(new ModelBox(bone34, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone33.addChild(bone35);
				setRotationAngle(bone35, -1.5708F, 0.0F, 1.5708F);
				bone35.cubeList.add(new ModelBox(bone35, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone33.addChild(bone36);
				setRotationAngle(bone36, 3.1416F, 0.0F, 1.5708F);
				bone36.cubeList.add(new ModelBox(bone36, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone33.addChild(bone37);
				setRotationAngle(bone37, 1.5708F, 0.0F, 1.5708F);
				bone37.cubeList.add(new ModelBox(bone37, 0, 6, 0.0F, -16.0F, -5.0F, 0, 16, 10, 0.0F, false));

				ball = new ModelRenderer(this);
				ball.setRotationPoint(0.0F, 0.0F, 0.0F);


				hexadecagon = new ModelRenderer(this);
				hexadecagon.setRotationPoint(0.0F, -0.0029F, 0.0019F);
				ball.addChild(hexadecagon);
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 0, 0, -1.0F, -1.0025F, -5.0019F, 2, 2, 10, 0.0F, false));
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 6, 0, -1.0F, -4.9971F, -0.9965F, 2, 10, 2, 0.0F, false));

				hexadecagon_r1 = new ModelRenderer(this);
				hexadecagon_r1.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon.addChild(hexadecagon_r1);
				setRotationAngle(hexadecagon_r1, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r2 = new ModelRenderer(this);
				hexadecagon_r2.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon.addChild(hexadecagon_r2);
				setRotationAngle(hexadecagon_r2, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r3 = new ModelRenderer(this);
				hexadecagon_r3.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon.addChild(hexadecagon_r3);
				setRotationAngle(hexadecagon_r3, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r4 = new ModelRenderer(this);
				hexadecagon_r4.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon.addChild(hexadecagon_r4);
				setRotationAngle(hexadecagon_r4, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon9 = new ModelRenderer(this);
				hexadecagon9.setRotationPoint(0.0F, -0.0029F, 0.0019F);
				ball.addChild(hexadecagon9);
				setRotationAngle(hexadecagon9, 0.0F, -0.3927F, 0.0F);
				hexadecagon9.cubeList.add(new ModelBox(hexadecagon9, 0, 0, -1.0F, -1.0025F, -5.0019F, 2, 2, 10, 0.0F, false));
				hexadecagon9.cubeList.add(new ModelBox(hexadecagon9, 6, 0, -1.0F, -4.9971F, -0.9965F, 2, 10, 2, 0.0F, false));

				hexadecagon_r5 = new ModelRenderer(this);
				hexadecagon_r5.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon9.addChild(hexadecagon_r5);
				setRotationAngle(hexadecagon_r5, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r6 = new ModelRenderer(this);
				hexadecagon_r6.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon9.addChild(hexadecagon_r6);
				setRotationAngle(hexadecagon_r6, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r7 = new ModelRenderer(this);
				hexadecagon_r7.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon9.addChild(hexadecagon_r7);
				setRotationAngle(hexadecagon_r7, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r7.cubeList.add(new ModelBox(hexadecagon_r7, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r8 = new ModelRenderer(this);
				hexadecagon_r8.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon9.addChild(hexadecagon_r8);
				setRotationAngle(hexadecagon_r8, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r8.cubeList.add(new ModelBox(hexadecagon_r8, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon10 = new ModelRenderer(this);
				hexadecagon10.setRotationPoint(0.0F, -0.0029F, 0.0019F);
				ball.addChild(hexadecagon10);
				setRotationAngle(hexadecagon10, 0.0F, -0.7854F, 0.0F);
				hexadecagon10.cubeList.add(new ModelBox(hexadecagon10, 0, 0, -1.0F, -1.0025F, -5.0019F, 2, 2, 10, 0.0F, false));
				hexadecagon10.cubeList.add(new ModelBox(hexadecagon10, 6, 0, -1.0F, -4.9971F, -0.9965F, 2, 10, 2, 0.0F, false));

				hexadecagon_r9 = new ModelRenderer(this);
				hexadecagon_r9.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon10.addChild(hexadecagon_r9);
				setRotationAngle(hexadecagon_r9, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r10 = new ModelRenderer(this);
				hexadecagon_r10.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon10.addChild(hexadecagon_r10);
				setRotationAngle(hexadecagon_r10, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r11 = new ModelRenderer(this);
				hexadecagon_r11.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon10.addChild(hexadecagon_r11);
				setRotationAngle(hexadecagon_r11, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r11.cubeList.add(new ModelBox(hexadecagon_r11, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r12 = new ModelRenderer(this);
				hexadecagon_r12.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon10.addChild(hexadecagon_r12);
				setRotationAngle(hexadecagon_r12, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r12.cubeList.add(new ModelBox(hexadecagon_r12, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon11 = new ModelRenderer(this);
				hexadecagon11.setRotationPoint(0.0F, -0.0029F, 0.0019F);
				ball.addChild(hexadecagon11);
				setRotationAngle(hexadecagon11, 0.0F, -1.1781F, 0.0F);
				hexadecagon11.cubeList.add(new ModelBox(hexadecagon11, 0, 0, -1.0F, -1.0025F, -5.0019F, 2, 2, 10, 0.0F, false));
				hexadecagon11.cubeList.add(new ModelBox(hexadecagon11, 6, 0, -1.0F, -4.9971F, -0.9965F, 2, 10, 2, 0.0F, false));

				hexadecagon_r13 = new ModelRenderer(this);
				hexadecagon_r13.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon11.addChild(hexadecagon_r13);
				setRotationAngle(hexadecagon_r13, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r14 = new ModelRenderer(this);
				hexadecagon_r14.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon11.addChild(hexadecagon_r14);
				setRotationAngle(hexadecagon_r14, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r15 = new ModelRenderer(this);
				hexadecagon_r15.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon11.addChild(hexadecagon_r15);
				setRotationAngle(hexadecagon_r15, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r15.cubeList.add(new ModelBox(hexadecagon_r15, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r16 = new ModelRenderer(this);
				hexadecagon_r16.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon11.addChild(hexadecagon_r16);
				setRotationAngle(hexadecagon_r16, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r16.cubeList.add(new ModelBox(hexadecagon_r16, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon12 = new ModelRenderer(this);
				hexadecagon12.setRotationPoint(0.0F, -0.0029F, 0.0019F);
				ball.addChild(hexadecagon12);
				setRotationAngle(hexadecagon12, 0.0F, -1.5708F, 0.0F);
				hexadecagon12.cubeList.add(new ModelBox(hexadecagon12, 0, 0, -1.0F, -1.0025F, -5.0019F, 2, 2, 10, 0.0F, false));
				hexadecagon12.cubeList.add(new ModelBox(hexadecagon12, 6, 0, -1.0F, -4.9971F, -0.9965F, 2, 10, 2, 0.0F, false));

				hexadecagon_r17 = new ModelRenderer(this);
				hexadecagon_r17.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon12.addChild(hexadecagon_r17);
				setRotationAngle(hexadecagon_r17, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r18 = new ModelRenderer(this);
				hexadecagon_r18.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon12.addChild(hexadecagon_r18);
				setRotationAngle(hexadecagon_r18, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r19 = new ModelRenderer(this);
				hexadecagon_r19.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon12.addChild(hexadecagon_r19);
				setRotationAngle(hexadecagon_r19, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r19.cubeList.add(new ModelBox(hexadecagon_r19, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r20 = new ModelRenderer(this);
				hexadecagon_r20.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon12.addChild(hexadecagon_r20);
				setRotationAngle(hexadecagon_r20, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r20.cubeList.add(new ModelBox(hexadecagon_r20, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon13 = new ModelRenderer(this);
				hexadecagon13.setRotationPoint(0.0F, -0.0029F, 0.0019F);
				ball.addChild(hexadecagon13);
				setRotationAngle(hexadecagon13, 0.0F, -1.9635F, 0.0F);
				hexadecagon13.cubeList.add(new ModelBox(hexadecagon13, 0, 0, -1.0F, -1.0025F, -5.0019F, 2, 2, 10, 0.0F, false));
				hexadecagon13.cubeList.add(new ModelBox(hexadecagon13, 6, 0, -1.0F, -4.9971F, -0.9965F, 2, 10, 2, 0.0F, false));

				hexadecagon_r21 = new ModelRenderer(this);
				hexadecagon_r21.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon13.addChild(hexadecagon_r21);
				setRotationAngle(hexadecagon_r21, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r22 = new ModelRenderer(this);
				hexadecagon_r22.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon13.addChild(hexadecagon_r22);
				setRotationAngle(hexadecagon_r22, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r23 = new ModelRenderer(this);
				hexadecagon_r23.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon13.addChild(hexadecagon_r23);
				setRotationAngle(hexadecagon_r23, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r23.cubeList.add(new ModelBox(hexadecagon_r23, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r24 = new ModelRenderer(this);
				hexadecagon_r24.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon13.addChild(hexadecagon_r24);
				setRotationAngle(hexadecagon_r24, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r24.cubeList.add(new ModelBox(hexadecagon_r24, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon14 = new ModelRenderer(this);
				hexadecagon14.setRotationPoint(0.0F, -0.0029F, 0.0019F);
				ball.addChild(hexadecagon14);
				setRotationAngle(hexadecagon14, 0.0F, -2.3562F, 0.0F);
				hexadecagon14.cubeList.add(new ModelBox(hexadecagon14, 0, 0, -1.0F, -1.0025F, -5.0019F, 2, 2, 10, 0.0F, false));
				hexadecagon14.cubeList.add(new ModelBox(hexadecagon14, 6, 0, -1.0F, -4.9971F, -0.9965F, 2, 10, 2, 0.0F, false));

				hexadecagon_r25 = new ModelRenderer(this);
				hexadecagon_r25.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon14.addChild(hexadecagon_r25);
				setRotationAngle(hexadecagon_r25, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r26 = new ModelRenderer(this);
				hexadecagon_r26.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon14.addChild(hexadecagon_r26);
				setRotationAngle(hexadecagon_r26, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r27 = new ModelRenderer(this);
				hexadecagon_r27.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon14.addChild(hexadecagon_r27);
				setRotationAngle(hexadecagon_r27, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r27.cubeList.add(new ModelBox(hexadecagon_r27, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r28 = new ModelRenderer(this);
				hexadecagon_r28.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon14.addChild(hexadecagon_r28);
				setRotationAngle(hexadecagon_r28, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r28.cubeList.add(new ModelBox(hexadecagon_r28, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon15 = new ModelRenderer(this);
				hexadecagon15.setRotationPoint(0.0F, -0.0029F, 0.0019F);
				ball.addChild(hexadecagon15);
				setRotationAngle(hexadecagon15, 0.0F, -2.7489F, 0.0F);
				hexadecagon15.cubeList.add(new ModelBox(hexadecagon15, 0, 0, -1.0F, -1.0025F, -5.0019F, 2, 2, 10, 0.0F, false));
				hexadecagon15.cubeList.add(new ModelBox(hexadecagon15, 6, 0, -1.0F, -4.9971F, -0.9965F, 2, 10, 2, 0.0F, false));

				hexadecagon_r29 = new ModelRenderer(this);
				hexadecagon_r29.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon15.addChild(hexadecagon_r29);
				setRotationAngle(hexadecagon_r29, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r30 = new ModelRenderer(this);
				hexadecagon_r30.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon15.addChild(hexadecagon_r30);
				setRotationAngle(hexadecagon_r30, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, 6, 0, -1.0F, -5.0F, -0.9946F, 2, 10, 2, 0.0F, false));
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r31 = new ModelRenderer(this);
				hexadecagon_r31.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon15.addChild(hexadecagon_r31);
				setRotationAngle(hexadecagon_r31, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r31.cubeList.add(new ModelBox(hexadecagon_r31, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));

				hexadecagon_r32 = new ModelRenderer(this);
				hexadecagon_r32.setRotationPoint(0.0F, 0.0029F, -0.0019F);
				hexadecagon15.addChild(hexadecagon_r32);
				setRotationAngle(hexadecagon_r32, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r32.cubeList.add(new ModelBox(hexadecagon_r32, 0, 0, -1.0F, -1.0054F, -5.0F, 2, 2, 10, 0.0F, false));
			}

			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				ball.rotateAngleY = -f2 * 0.8F;
				ball.rotateAngleX = f2 * 0.6F;
				int impactTicks = ((EC)entity).getImpactTicks();
				if (impactTicks == 0) {
					int i = ((EC)entity).getBallColor();
					float alpha = (i >> 24 & 0xFF) / 255.0F;
					GlStateManager.color((i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, alpha);
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
							alpha > 0.8f ? GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA : GlStateManager.DestFactor.ONE);
				} else {
					GlStateManager.color(1.0F, 1.0F, 1.0F, 0.2F);
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
				}
				ball.render(f5);
				if (impactTicks == 0) {
					flaps.rotateAngleY = f2 * 0.6F;
					float f6 = (float)Math.sin(f2 * 0.2F) * 0.1F;
					flaps.rotateAngleX = f6;
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					float scale = 1.5f + f6 * 2.0F;
					//GlStateManager.enableRescaleNormal();
					GlStateManager.scale(scale, scale, scale);
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
					flaps.render(f5);
				}
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
