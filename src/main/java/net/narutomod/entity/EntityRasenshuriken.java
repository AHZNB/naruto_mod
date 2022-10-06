
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
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

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> {
			return new RenderRasenshuriken(renderManager);
		});
	}

	public static class EC extends EntityScalableProjectile.Base {
		private static final DataParameter<Integer> IMPACT_TICKS = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> BALL_COLOR = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		//private final int s = 50;
		//private int[] randomStartTick = new int[s];
		private final int growTime = 20;
		private float fullScale;
		private Vec3d impactVec;
		protected float impactDamageMultiplier = 2.0f;

		public EC(World a) {
			super(a);
			this.setOGSize(2.5F, 0.5F);
			this.isImmuneToFire = true;
			//for (int i = 0; i < this.s; i++ ) 
			//	this.randomStartTick[i] = this.rand.nextInt(50);
		}

		public EC(EntityLivingBase shooter, float scale) {
			super(shooter);
			this.setOGSize(2.5F, 0.5F);
			this.setPosition(shooter.posX, shooter.posY + shooter.height + 0.5D, shooter.posZ);
			this.fullScale = scale;
			this.setEntityScale(0.1f);
			this.isImmuneToFire = true;
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

		protected void doImpactDamage() {
			ProcedureAoeCommand.set(this.world, this.impactVec.x, this.impactVec.y, this.impactVec.z, 0d, this.width/2)
			  .exclude(this.shootingEntity).resetHurtResistanceTime()
			  .damageEntities(ItemJutsu.causeJutsuDamage(this, this.shootingEntity).setDamageBypassesArmor(),
			   this.fullScale * this.impactDamageMultiplier)
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
					this.world.playSound(null, this.posX, this.posY, this.posZ, (SoundEvent)SoundEvent.REGISTRY
					  .getObject(new ResourceLocation("narutomod:rasenshuriken_explode")), SoundCategory.NEUTRAL, 5, 1f);
				}
				float scale = this.getEntityScale() * (impactTicks <= 20 ? 1.15f : 1.001f);
				double d = (this.height * scale / this.getEntityScale() - this.height) / 2;
				this.setEntityScale(scale);
				this.setPosition(this.impactVec.x, this.posY - d, this.impactVec.z);
				this.doImpactDamage();
				new EventSphericalExplosion(this.world, null, (int)Math.floor(this.impactVec.x), (int)this.impactVec.y, 
				  (int)Math.floor(this.impactVec.z), (int) Math.ceil(this.width/2) + 1, 0, 0f, true, false);
				if (impactTicks >= 200) {
					this.setDead();
				}
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.getImpactTicks() > 0) {
				this.onImpactUpdate();
				return;
			}
			if (!this.world.isRemote && this.ticksAlive == 1 && this.shootingEntity instanceof EntityPlayer) {
				//PlayerRender.forceBowPose((EntityPlayer)this.shootingEntity, EnumHandSide.RIGHT, true);
				ProcedureSync.EntityNBTTag.setAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose, true);
			}
			if (!this.world.isRemote && this.shootingEntity != null) {
				if (this.ticksAlive < this.growTime) {
					this.setEntityScale(this.fullScale * (this.ticksAlive + 1) / this.growTime);
					this.setPosition(this.shootingEntity.posX, this.shootingEntity.posY + this.shootingEntity.height + 0.5d, this.shootingEntity.posZ);
				/*} else if (this.ticksAlive == this.growTime && this.shootingEntity != null) {
					Vec3d vec3d = this.shootingEntity.getLookVec();
					this.shoot(vec3d.x, vec3d.y, vec3d.z, 0.95f, 0f);*/
				} else if (this.getDistance(this.shootingEntity) < 48d) {
					RayTraceResult rt = ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d);
					if (!this.equals(rt.entityHit) && !this.shootingEntity.equals(rt.entityHit)) {
						this.shoot(rt.hitVec.x - this.posX, rt.hitVec.y - this.posY, rt.hitVec.z - this.posZ, 0.95f, 0f);
					}
				}
			}
			for (int i = 0; i < Math.min(this.ticksAlive, this.growTime) * 10; i++) {
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY, this.posZ, 1, 1d, 0d, 1d, 
				  0.6d * this.rand.nextGaussian(), 0.1d * this.rand.nextGaussian(), 0.6d * this.rand.nextGaussian(), 0x10FFFFFF,
				  (int)(this.fullScale * 12), 0);
			}
			if (this.fullScale >= 4.0f) {
				ProcedureLightSourceSetBlock.execute(this.world, MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));
			}
			if (this.ticksAlive % 80 == 79) {
				this.world.playSound(null, this.posX, this.posY, this.posZ, 
				  (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:wind")), SoundCategory.NEUTRAL, 1, 1f);
			}
			if (this.ticksInAir > 200 || (!this.world.isRemote && this.shootingEntity == null && !this.isLaunched())) {
				this.setDead();
				if (this.shootingEntity != null) {
					ProcedureSync.EntityNBTTag.removeAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose);
				}
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if ((result.typeOfHit == RayTraceResult.Type.BLOCK
			  && this.world.getBlockState(result.getBlockPos()).getBlock() == BlockLightSource.block)
			 || (result.entityHit != null && result.entityHit.equals(this.shootingEntity))) {
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

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void renderParticles() {
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

		private static EC create(EntityLivingBase entity, float power) {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) 
				  SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rasenshuriken")),
				  SoundCategory.PLAYERS, 5, 1f);
			EC entity1 = new EC(entity, power);
			entity.world.spawnEntity(entity1);
			return entity1;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if ((stack.getItem() == ItemFuton.block && power >= 0.1f) || (stack.getItem() == ItemSenjutsu.block && power >= 2.0f)) {
					EC.create(entity, power);
					return true;
				}
				return false;
			}
		}

		public static class TSBVariant implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				EC entity1 = EC.create(entity, 4.0f);
				entity1.setBallColor(0xE0101010);
				entity1.impactDamageMultiplier = 8.0f;
				return true;
			}
		}
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
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			//for (int i = 0; i < entity.s /*/ (scale > 40f ? (int)(scale/4f) : 1)*/; i++) {
				//GlStateManager.rotate(entity.world.rand.nextFloat() * 90f, 0f, 1f, 0f);
				//GlStateManager.rotate(entity.world.rand.nextFloat() * 0.2f, 1f, 0f, 0f);
				//this.mainModel.render(entity, 0.0F, 0.0F, partialTicks + entity.ticksExisted + entity.randomStartTick[i], 0.0F, 0.0F, 0.0625F);
				this.mainModel.render(entity, 0.0F, 0.0F, f, 0.0F, 0.0F, 0.0625F);
			//}
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			//super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return texture;
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
		private final ModelRenderer bone2;
		private final ModelRenderer bone8;
		private final ModelRenderer bone9;
		private final ModelRenderer bone10;
		private final ModelRenderer bone11;
		private final ModelRenderer bone12;
		private final ModelRenderer bone13;
		private final ModelRenderer bone14;
		private final ModelRenderer bone15;
		private final ModelRenderer bone16;
		private final ModelRenderer bone17;
		private final ModelRenderer bone18;
		private final ModelRenderer bone19;
		private final ModelRenderer bone20;
		private final ModelRenderer bone21;
		private final ModelRenderer bone22;
		private final ModelRenderer bone23;
		private final ModelRenderer bone24;
		private final ModelRenderer bone25;
		private final ModelRenderer bone26;
		private final ModelRenderer bone27;

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
			
	
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone2);
			bone2.cubeList.add(new ModelBox(bone2, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone8);
			setRotationAngle(bone8, -0.2618F, -0.2618F, 0.2618F);
			bone8.cubeList.add(new ModelBox(bone8, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone9);
			setRotationAngle(bone9, -0.5236F, -0.5236F, 0.5236F);
			bone9.cubeList.add(new ModelBox(bone9, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone10 = new ModelRenderer(this);
			bone10.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone10);
			setRotationAngle(bone10, -0.7854F, -0.7854F, 0.7854F);
			bone10.cubeList.add(new ModelBox(bone10, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone11 = new ModelRenderer(this);
			bone11.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone11);
			setRotationAngle(bone11, -1.0472F, -1.0472F, 1.0472F);
			bone11.cubeList.add(new ModelBox(bone11, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone12 = new ModelRenderer(this);
			bone12.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone12);
			setRotationAngle(bone12, -1.309F, -1.309F, 1.309F);
			bone12.cubeList.add(new ModelBox(bone12, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone13 = new ModelRenderer(this);
			bone13.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone13);
			setRotationAngle(bone13, -1.8326F, -1.8326F, 1.8326F);
			bone13.cubeList.add(new ModelBox(bone13, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone14 = new ModelRenderer(this);
			bone14.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone14);
			setRotationAngle(bone14, -2.0944F, -2.0944F, 2.0944F);
			bone14.cubeList.add(new ModelBox(bone14, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone15 = new ModelRenderer(this);
			bone15.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone15);
			setRotationAngle(bone15, -2.3562F, -2.3562F, 2.3562F);
			bone15.cubeList.add(new ModelBox(bone15, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone16 = new ModelRenderer(this);
			bone16.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone16);
			setRotationAngle(bone16, -2.618F, -2.618F, 2.618F);
			bone16.cubeList.add(new ModelBox(bone16, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone17 = new ModelRenderer(this);
			bone17.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone17);
			setRotationAngle(bone17, -2.8798F, -2.8798F, 2.8798F);
			bone17.cubeList.add(new ModelBox(bone17, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone18 = new ModelRenderer(this);
			bone18.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone18);
			setRotationAngle(bone18, 2.8798F, 2.8798F, -2.8798F);
			bone18.cubeList.add(new ModelBox(bone18, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone19 = new ModelRenderer(this);
			bone19.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone19);
			setRotationAngle(bone19, 2.618F, 2.618F, -2.618F);
			bone19.cubeList.add(new ModelBox(bone19, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone20 = new ModelRenderer(this);
			bone20.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone20);
			setRotationAngle(bone20, 2.3562F, 2.3562F, -2.3562F);
			bone20.cubeList.add(new ModelBox(bone20, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone21 = new ModelRenderer(this);
			bone21.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone21);
			setRotationAngle(bone21, 2.0944F, 2.0944F, -2.0944F);
			bone21.cubeList.add(new ModelBox(bone21, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone22 = new ModelRenderer(this);
			bone22.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone22);
			setRotationAngle(bone22, 1.8326F, 1.8326F, -1.8326F);
			bone22.cubeList.add(new ModelBox(bone22, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone23 = new ModelRenderer(this);
			bone23.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone23);
			setRotationAngle(bone23, 1.309F, 1.309F, -1.309F);
			bone23.cubeList.add(new ModelBox(bone23, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone24 = new ModelRenderer(this);
			bone24.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone24);
			setRotationAngle(bone24, 1.0472F, 1.0472F, -1.0472F);
			bone24.cubeList.add(new ModelBox(bone24, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone25 = new ModelRenderer(this);
			bone25.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone25);
			setRotationAngle(bone25, 0.7854F, 0.7854F, -0.7854F);
			bone25.cubeList.add(new ModelBox(bone25, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone26 = new ModelRenderer(this);
			bone26.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone26);
			setRotationAngle(bone26, 0.5236F, 0.5236F, -0.5236F);
			bone26.cubeList.add(new ModelBox(bone26, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
	
			bone27 = new ModelRenderer(this);
			bone27.setRotationPoint(0.0F, 0.0F, 0.0F);
			ball.addChild(bone27);
			setRotationAngle(bone27, 0.2618F, 0.2618F, -0.2618F);
			bone27.cubeList.add(new ModelBox(bone27, 0, 0, -3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			GlStateManager.pushMatrix();
			ball.rotateAngleY = -f2 * 0.8F;
			ball.rotateAngleX = f2 * 0.6F;
			if (((EC)entity).getImpactTicks() == 0) {
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
			if (((EC)entity).getImpactTicks() == 0) {
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
