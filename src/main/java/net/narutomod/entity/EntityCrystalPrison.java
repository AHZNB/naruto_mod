
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityCrystalPrison extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 472;
	public static final int ENTITYID_RANGED = 473;

	public EntityCrystalPrison(ElementsNarutomodMod instance) {
		super(instance, 901);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "crystal_prison"), ENTITYID).name("crystal_prison").tracker(64, 3, true).build());
	}

	public static class EC extends EntitySpike.Base implements ItemJutsu.IJutsu {
		private static final DataParameter<Boolean> SHATTERED = EntityDataManager.<Boolean>createKey(EC.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Float> RAND_YAW = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> RAND_PITCH = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private final int growTime = 10;
		private float maxScale;
		private float health;
		private EntityLivingBase user;
		private Map<EntityLivingBase, ProcedureSync.PositionRotationPacket> trappedEntities = Maps.newHashMap();

		public EC(World worldIn) {
			super(worldIn);
			this.setColor(0xC0FFFFFF);
			this.setRandYawPitch();
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, float scale) {
			this(userIn.world);
			this.user = userIn;
			this.maxScale = scale;
			this.health = scale * 100.0f;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.SHOTON;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(SHATTERED, Boolean.valueOf(false));
			this.dataManager.register(RAND_YAW, Float.valueOf(0f));
			this.dataManager.register(RAND_PITCH, Float.valueOf(0f));
		}

		private boolean isShattered() {
			return ((Boolean)this.dataManager.get(SHATTERED)).booleanValue();
		}

		private void setShattered(boolean shattered) {
			this.dataManager.set(SHATTERED, Boolean.valueOf(shattered));
		}

		private float getRandYaw() {
			return ((Float)this.dataManager.get(RAND_YAW)).floatValue();
		}

		private float getRandPitch() {
			return ((Float)this.dataManager.get(RAND_PITCH)).floatValue();
		}

		private void setRandYawPitch() {
			this.dataManager.set(RAND_YAW, Float.valueOf((this.rand.nextFloat() - 0.5f) * 90f));
			this.dataManager.set(RAND_PITCH, Float.valueOf((this.rand.nextFloat() - 0.5f) * 60f));
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && !this.isShattered()) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ice_shoot_small")),
				 0.8f, this.rand.nextFloat() * 0.4f + 0.8f);
				for (int i = 0; i < this.rand.nextInt(10) + 20; i++) {
					EC entity = new EC(this.world);
					entity.setShattered(true);
					entity.setEntityScale(this.rand.nextFloat() * 0.5f + 0.05f);
					entity.setPositionAndRotation(this.posX + this.width * (this.rand.nextFloat()-0.5f), 
					 this.posY + this.height * this.rand.nextFloat(), this.posZ + this.width * (this.rand.nextFloat()-0.5f),
					 this.getRandYaw(), this.getRandPitch());
					entity.motionX = (this.rand.nextDouble()-0.5d) * 0.05d;
					entity.motionY = 0.0d;
					entity.motionZ = (this.rand.nextDouble()-0.5d) * 0.05d;
					entity.maxInGroundTime = 100;
					this.world.spawnEntity(entity);
				}
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.world.isRemote) {
				this.health -= amount;
				if (this.health <= 0.0f) {
					this.setDead();
				}
				return amount > 0.0f;
			}
			return false;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.isShattered()) {
				if (!this.isLaunched() && !this.hasNoGravity() && !this.onGround) {
					this.rotationYaw += this.getRandYaw();
					this.rotationPitch += this.getRandPitch();
				}
			} else {
				if (!this.world.isRemote && this.ticksAlive <= this.growTime) {
					this.setEntityScale(MathHelper.clamp(this.maxScale * (float)this.ticksAlive / this.growTime, 0.0f, this.maxScale));
					for (EntityLivingBase entity : 
					 this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(1d, 0d, 1d))) {
						if (!this.trappedEntities.containsKey(entity) && ItemJutsu.canTarget(entity)) {
							this.trappedEntities.put(entity, new ProcedureSync.PositionRotationPacket(entity));
						}
					}
				}
				List<EntityLivingBase> removeList = Lists.newArrayList();
				for (Map.Entry<EntityLivingBase, ProcedureSync.PositionRotationPacket> entry : this.trappedEntities.entrySet()) {
					if (entry.getKey().isEntityAlive() && ItemJutsu.canTarget(entry.getKey())) {
						if (entry.getKey() instanceof EntityLiving) {
							ProcedureOnLivingUpdate.disableAIfor((EntityLiving)entry.getKey(), 5);
						} else {
							entry.getKey().rotationYaw = entry.getKey().rotationYawHead = entry.getValue().rotationYaw;
							entry.getKey().rotationPitch = entry.getValue().rotationPitch;
							entry.getKey().setPositionAndUpdate(entry.getValue().posX, entry.getValue().posY, entry.getValue().posZ);
						}
					} else {
						removeList.add(entry.getKey());
					}
				}
				for (EntityLivingBase entity : removeList) {
					this.trappedEntities.remove(entity);
				}
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setShattered(compound.getBoolean("shattered"));
			this.maxScale = compound.getFloat("maxScale");
			this.health = compound.getFloat("health");
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setBoolean("shattered", this.isShattered());
			compound.setFloat("maxScale", this.maxScale);
			compound.setFloat("health", this.health);
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				World world = entity.world;
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 30d, 3d);
				if (res != null && (res.entityHit != null || (res.typeOfHit == RayTraceResult.Type.BLOCK && res.sideHit == EnumFacing.UP))) {
					world.playSound(null, res.hitVec.x, res.hitVec.y, res.hitVec.z,
					 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:spiked")),
					 net.minecraft.util.SoundCategory.NEUTRAL, 5f, entity.getRNG().nextFloat() * 0.4f + 0.8f);
					for (int i = 0; i < (int)(power * 2) + 3; i++) {
						float f1 = entity.getRNG().nextFloat();
						float yaw = entity.getRNG().nextFloat() * 360f;
						Vec3d vec = res.hitVec.add(Vec3d.fromPitchYaw(0f, yaw).scale(f1 * power * 0.5f));
						for (; !world.getBlockState(new BlockPos(vec)).isTopSolid(); vec = vec.subtract(0d, 1d, 0d));
						for (; world.getBlockState(new BlockPos(vec).up()).isTopSolid(); vec = vec.addVector(0d, 1d, 0d));
						float pitch = 30f * f1 + (entity.getRNG().nextFloat()-0.5f) * 20f;
						EC entity1 = new EC(entity, (1.5f - f1) * power * 0.5f + 1f);
						entity1.setLocationAndAngles(vec.x, vec.y + 0.5d, vec.z, yaw + (entity.getRNG().nextFloat()-0.5f) * 60f, pitch);
						world.spawnEntity(entity1);
					}
					return true;
				}
				return false;
			}

			@Override
			public float getPowerupDelay() {
				return 100.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 20.0f;
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/crystal_pink.png");
	
			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelCrystal());
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelCrystal extends ModelBase {
			private final ModelRenderer sides;
			private final ModelRenderer south;
			private final ModelRenderer north;
			private final ModelRenderer west;
			private final ModelRenderer east;
			private final ModelRenderer bottom;
			private final ModelRenderer tip;
			private final ModelRenderer south2;
			private final ModelRenderer north2;
			private final ModelRenderer west2;
			private final ModelRenderer east2;
		
			public ModelCrystal() {
				textureWidth = 64;
				textureHeight = 32;
		
				sides = new ModelRenderer(this);
				sides.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				south = new ModelRenderer(this);
				south.setRotationPoint(0.0F, 0.0F, 4.0F);
				sides.addChild(south);
				setRotationAngle(south, 0.0436F, 0.0F, 0.0F);
				south.cubeList.add(new ModelBox(south, 0, 0, -4.0F, -26.0F, 0.0F, 8, 26, 0, 0.0F, false));
		
				north = new ModelRenderer(this);
				north.setRotationPoint(0.0F, 0.0F, -4.0F);
				sides.addChild(north);
				setRotationAngle(north, -0.0436F, 0.0F, 0.0F);
				north.cubeList.add(new ModelBox(north, 8, 0, -4.0F, -26.0F, 0.0F, 8, 26, 0, 0.0F, false));
		
				west = new ModelRenderer(this);
				west.setRotationPoint(4.0F, 0.0F, 0.0F);
				sides.addChild(west);
				setRotationAngle(west, -0.0436F, -1.5708F, 0.0F);
				west.cubeList.add(new ModelBox(west, 8, 0, -4.0F, -26.0F, 0.0F, 8, 26, 0, 0.0F, false));
		
				east = new ModelRenderer(this);
				east.setRotationPoint(-4.0F, 0.0F, 0.0F);
				sides.addChild(east);
				setRotationAngle(east, 0.0436F, -1.5708F, 0.0F);
				east.cubeList.add(new ModelBox(east, 0, 0, -4.0F, -26.0F, 0.0F, 8, 26, 0, 0.0F, false));
		
				bottom = new ModelRenderer(this);
				bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
				sides.addChild(bottom);
				setRotationAngle(bottom, -1.5708F, 0.0F, 0.0F);
				bottom.cubeList.add(new ModelBox(bottom, 24, 16, -4.0F, -4.0F, 0.0F, 8, 8, 0, 0.0F, false));
		
				tip = new ModelRenderer(this);
				tip.setRotationPoint(0.0F, 0.0F, 0.0F);
				sides.addChild(tip);
				setRotationAngle(tip, 0.0F, 0.7854F, 0.0F);
				
		
				south2 = new ModelRenderer(this);
				south2.setRotationPoint(0.0F, -24.0F, 2.75F);
				tip.addChild(south2);
				setRotationAngle(south2, 0.3491F, 0.0F, 0.0F);
				south2.cubeList.add(new ModelBox(south2, 24, 0, -4.0F, -8.0F, 0.0F, 8, 16, 0, 0.0F, false));
		
				north2 = new ModelRenderer(this);
				north2.setRotationPoint(0.0F, -24.0F, -2.75F);
				tip.addChild(north2);
				setRotationAngle(north2, -0.3491F, 0.0F, 0.0F);
				north2.cubeList.add(new ModelBox(north2, 32, 0, -4.0F, -8.0F, 0.0F, 8, 16, 0, 0.0F, false));
		
				west2 = new ModelRenderer(this);
				west2.setRotationPoint(2.75F, -24.0F, 0.0F);
				tip.addChild(west2);
				setRotationAngle(west2, -0.3491F, -1.5708F, 0.0F);
				west2.cubeList.add(new ModelBox(west2, 32, 0, -4.0F, -8.0F, 0.0F, 8, 16, 0, 0.0F, false));
		
				east2 = new ModelRenderer(this);
				east2.setRotationPoint(-2.75F, -24.0F, 0.0F);
				tip.addChild(east2);
				setRotationAngle(east2, 0.3491F, -1.5708F, 0.0F);
				east2.cubeList.add(new ModelBox(east2, 24, 0, -4.0F, -8.0F, 0.0F, 8, 16, 0, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				sides.render(f5);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
