
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWoodCutting extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 512;
	public static final int ENTITYID_RANGED = 513;

	public EntityWoodCutting(ElementsNarutomodMod instance) {
		super(instance, 926);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "wood_cutting"), ENTITYID).name("wood_cutting").tracker(64, 3, true).build());
	}

	public static class EC extends EntitySpike.Base implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> TARGETID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Float> REL_X = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> REL_Y = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> REL_Z = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> REL_YAW = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> YAW = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> PITCH = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private int inTargetTime;
		private final float baseImpactDamage = 20.0f;
		private final float baseSkewerDamage = 50.0f;
		
		public EC(World world) {
			super(world);
			this.setOGSize(0.25f, 1.825f);
			this.maxInGroundTime = 200;
		}

		public EC(EntityLivingBase userIn) {
			super(userIn);
			this.setOGSize(0.25f, 1.825f);
			this.maxInGroundTime = 200;
			this.setNoGravity(false);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.MOKUTON;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(TARGETID, Integer.valueOf(-1));
			this.getDataManager().register(REL_X, Float.valueOf(0.0F));
			this.getDataManager().register(REL_Y, Float.valueOf(0.0F));
			this.getDataManager().register(REL_Z, Float.valueOf(0.0F));
			this.getDataManager().register(REL_YAW, Float.valueOf(0.0F));
			this.getDataManager().register(YAW, Float.valueOf(0.0F));
			this.getDataManager().register(PITCH, Float.valueOf(0.0F));
		}

		@Nullable
		public EntityLivingBase getTarget() {
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(TARGETID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		protected void setTarget(@Nullable EntityLivingBase target, Vec3d offset, float relYaw, float yaw, float pitch) {
			if (!this.world.isRemote) {
				if (target != null) {
					this.getDataManager().set(TARGETID, Integer.valueOf(target.getEntityId()));
					this.getDataManager().set(REL_X, Float.valueOf((float)offset.x));
					this.getDataManager().set(REL_Y, Float.valueOf((float)offset.y));
					this.getDataManager().set(REL_Z, Float.valueOf((float)offset.z));
					this.getDataManager().set(REL_YAW, Float.valueOf(relYaw));
					this.getDataManager().set(YAW, Float.valueOf(yaw));
					this.getDataManager().set(PITCH, Float.valueOf(pitch));
					this.inTargetTime = this.ticksAlive;
				} else {
					this.getDataManager().set(TARGETID, Integer.valueOf(-1));
				}
			}
		}

		public Vec3d getRelativeVector() {
			return new Vec3d(((Float)this.getDataManager().get(REL_X)).floatValue(), ((Float)this.getDataManager().get(REL_Y)).floatValue(), ((Float)this.getDataManager().get(REL_Z)).floatValue());
		}

		public float getRelYaw() {
			return ((Float)this.getDataManager().get(REL_YAW)).floatValue();
		}

		public float getYaw() {
			return ((Float)this.getDataManager().get(YAW)).floatValue();
		}

		public float getPitch() {
			return ((Float)this.getDataManager().get(PITCH)).floatValue();
		}

		protected void setMaxInGroundTime(int ticks) {
			this.maxInGroundTime = ticks;
		}

		@Override
		protected void checkOnGround() {
			EntityLivingBase target = this.getTarget();
			if (target != null) {
				this.onGround = true;
				this.setPosition(target.posX, target.posY + target.height * 0.5, target.posZ);
			} else {
				this.onGround = false;
				super.checkOnGround();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote 
			 && result.entityHit instanceof EntityLivingBase && !result.entityHit.equals(this.shootingEntity)) {
				if (result.entityHit.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity).setProjectile(), this.baseImpactDamage)) {
					Vec3d vec = this.getPositionVector().addVector(this.motionX, this.motionY, this.motionZ).subtract(result.entityHit.getPositionVector());
					float relYaw = ((EntityLivingBase)result.entityHit).renderYawOffset;
					float yaw = MathHelper.wrapDegrees(this.rotationYaw - relYaw);
					this.setTarget((EntityLivingBase)result.entityHit, vec, relYaw, yaw, this.rotationPitch);
				} else if (!result.entityHit.noClip) {
					this.motionX *= -0.1d;
					this.motionY *= -0.1d;
					this.motionZ *= -0.1d;
					this.rotationYaw += 180.0F;
					this.prevRotationYaw += 180.0F;
				}
			}
		}

		@Override
		public AxisAlignedBB getCollisionBoundingBox() {
			return this.getTarget() != null ? null : super.getCollisionBoundingBox();
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.inTargetTime > 0 && this.ticksAlive == this.inTargetTime + 40) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hand_press")), 1.0f, 0.9f);
				EntityLivingBase target = this.getTarget();
				if (target != null && target.isEntityAlive()) {
					for (int i = 0; i < 5 + this.rand.nextInt(5); i++) {
						EC entity = new EC(this.world);
						entity.setMaxInGroundTime(60);
						entity.setTarget(target, this.getPositionVector().subtract(target.getPositionVector()),
						 target.renderYawOffset, (this.rand.nextFloat()-0.5f) * 360f - target.renderYawOffset, 15f + this.rand.nextFloat() * 100f);
						entity.setLocationAndAngles(this.posX, this.posY, this.posZ, (this.rand.nextFloat()-0.5f) * 360f, 15f + this.rand.nextFloat() * 100f);
						entity.setEntityScale(0.4f + this.rand.nextFloat() * 0.4f);
						this.world.spawnEntity(entity);
					}
					target.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity).setDamageBypassesArmor(), this.baseSkewerDamage * (1f + this.rand.nextFloat() * 0.5f));
				}
				this.setDead();
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Vec3d vec = entity.getLookVec();
				Vec3d vec1 = entity.getPositionEyes(1f).add(vec);
				Vec3d vec2 = vec1.add(vec);
		 		entity.swingArm(EnumHand.MAIN_HAND);
				this.createJutsu(entity.world, entity, vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z, 2.0f, 0.0f);
				return true;
			}

			public static void createJutsu(EntityLivingBase attacker, EntityLivingBase target) {
				Vec3d vec = attacker.getPositionEyes(1f);
				Vec3d vec1 = vec.add(attacker.getLookVec());
				Vec3d vec2 = target.getPositionVector().addVector(0, target.height * 0.5, 0).subtract(vec);
				vec2 = vec2.addVector(0, MathHelper.sqrt(vec2.x * vec2.x + vec2.z * vec2.z) * 0.1d, 0).add(vec1);
				createJutsu(attacker.world, attacker, vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z, 2.0f, 0.0f);
			}

			public static void createJutsu(World world, @Nullable EntityLivingBase shooter,
			 double fromX, double fromY, double fromZ, double toX, double toY, double toZ, float speed, float inaccuracy) {
				world.playSound(null, fromX, fromY, fromZ, SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodgrow")),
				 SoundCategory.NEUTRAL, 0.6f, world.rand.nextFloat() * 0.4f + 1.2f);
				EC entity1 = shooter != null ? new EC(shooter) : new EC(world);
				entity1.setPosition(fromX, fromY, fromZ);
				entity1.shoot(toX - fromX, toY - fromY, toZ - fromZ, speed, inaccuracy);
				world.spawnEntity(entity1);
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends EntitySpike.ClientSide.Renderer<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/spike_wood.png");
	
			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float pt) {
				EntityLivingBase target = entity.getTarget();
				if (target != null) {
					Vec3d vec = entity.getRelativeVector();
					if (!vec.equals(Vec3d.ZERO)) {
						float relYaw = entity.getRelYaw();
						float yaw = entity.getYaw();
						float pitch = entity.getPitch();
						float f = ProcedureUtils.interpolateRotation(target.prevRenderYawOffset, target.renderYawOffset, pt);
						vec = vec.rotateYaw(-(f - relYaw) * (float)Math.PI / 180F);
						vec = new Vec3d(target.lastTickPosX + (target.posX - target.lastTickPosX) * pt + vec.x, target.lastTickPosY + (target.posY - target.lastTickPosY) * pt + vec.y, target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * pt + vec.z);
						x = vec.x - this.renderManager.viewerPosX;
						y = vec.y - this.renderManager.viewerPosY;
						z = vec.z - this.renderManager.viewerPosZ;
						entity.prevRotationYaw = yaw + f;
						entity.prevRotationPitch = pitch;
						entity.setLocationAndAngles(vec.x, vec.y, vec.z, yaw + f, pitch);
					}
				}
				super.doRender(entity, x, y, z, entityYaw, pt);
			}

			@Override
			protected void prepareScale(float scale) {
				GlStateManager.scale(scale * 0.5f, scale, scale * 0.5f);
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}
	}
}
