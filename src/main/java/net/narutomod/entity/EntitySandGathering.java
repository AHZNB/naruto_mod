
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemJiton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySandGathering extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 430;
	public static final int ENTITYID_RANGED = 431;
	private static final float SCALE = 8f;

	public EntitySandGathering(ElementsNarutomodMod instance) {
		super(instance, 858);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "sand_gathering"), ENTITYID).name("sand_gathering").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private static final DataParameter<Integer> DEATH_TICKS = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private EntityLivingBase summoner;
		private ItemJiton.SwarmTarget sandCloud;
		private final int waitTime = 40;
		private boolean riseAgain;
		private double accelX;
		private double accelY;
		private double accelZ;

		public EC(World world) {
			super(world);
			this.setSize(0.375f * SCALE, 0.4375f * SCALE);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase summonerIn) {
			this(summonerIn.world);
			this.summoner = summonerIn;
			Vec3d vec = summonerIn.getLookVec().scale(2d);
			vec = summonerIn.getPositionVector().addVector(vec.x, 3.0d, vec.z);
			this.setPosition(vec.x, vec.y, vec.z);
			this.sandCloud = new ItemJiton.SwarmTarget(this.world, 50, this.getMouthPos(), 
			 this.getEntityBoundingBox(), new Vec3d(0.4d, 0.0d, 0.4d), 0.5f, 0.03f, false, 2f, ItemJiton.Type.IRON.getColor());
		}

		@Override
		protected void entityInit() {
			this.dataManager.register(DEATH_TICKS, Integer.valueOf(0));
		}

		public int getDeathTicks() {
			return ((Integer)this.dataManager.get(DEATH_TICKS)).intValue();
		}

		private void setDeathTicks(int i) {
			this.dataManager.set(DEATH_TICKS, Integer.valueOf(i));
		}

		private Vec3d getMouthPos() {
			return this.summoner != null ? this.summoner.getPositionVector().addVector(0d, 1.5d, 0d) : this.getPositionVector();
		}

		private void updateSandParticles() {
			int i = this.getDeathTicks();
			if (this.sandCloud != null) {
				if (this.sandCloud.shouldRemove()) {
					this.sandCloud = null;
				} else {
					if (i == 0 && this.sandCloud.getTicks() > this.waitTime) {
						this.sandCloud.forceRemove();
					}
					this.sandCloud.onUpdate();
				}
			} else if (!this.world.isRemote && i > 0) {
				this.sandCloud = new ItemJiton.SwarmTarget(this.world, 50, this.getEntityBoundingBox(),
			 	 this.getMouthPos(), new Vec3d(0.2d, -0.1d, 0.2d), 0.5f, 0.03f, true, 2f, ItemJiton.Type.IRON.getColor());
			}
		}

		private void onDeathUpdate() {
			int i = this.getDeathTicks() + 1;
			this.setDeathTicks(i);
			if (i > 2 && this.sandCloud == null) {
				this.setDead();
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.updateSandParticles();
			if (this.motionX != 0d || this.motionY != 0d || this.motionZ != 0d) {
				RayTraceResult result = this.forwardsRaycast(true);
				if (!this.world.isRemote && result != null) {
					this.world.createExplosion(this.summoner, result.hitVec.x, result.hitVec.y, result.hitVec.z, 3f,
					  net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.summoner));
					this.accelX = 0.0d;
					this.accelY = 0.0d;
					this.accelZ = 0.0d;
					this.motionX = 0.0d;
					this.motionZ = 0.0d;
					this.motionY = 0.1d;
					this.riseAgain = true;
				}
			}
			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			this.motionX += this.accelX;
			this.motionY += this.accelY;
			this.motionZ += this.accelZ;
			this.motionX *= 0.98D;
			this.motionY *= 0.98D;
			this.motionZ *= 0.98D;
			if (this.riseAgain && this.summoner != null && this.posY >= this.summoner.posY + 3.0d) {
				this.riseAgain = false;
				this.motionY = 0.0d;
			}
			this.setPosition(this.posX, this.posY, this.posZ);
			if (!this.world.isRemote && (this.summoner == null || this.getDeathTicks() > 0 || this.ticksExisted > 300)) {
				this.onDeathUpdate();
			}
		}

		public void shoot(Vec3d vec) {
			Vec3d vec1 = vec.normalize().scale(0.15d);
			this.accelX = vec1.x;
			this.accelY = vec1.y;
			this.accelZ = vec1.z;
			this.motionX = 0.0d;
			this.motionY = 0.0d;
			this.motionZ = 0.0d;
			this.riseAgain = false;
		}

		protected RayTraceResult forwardsRaycast(boolean includeEntities) {
			return EntityScalableProjectile.forwardsRaycast(this, ProcedureUtils.getMotion(this), includeEntities, false, null);
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "JitonSandGatheringEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ID_KEY));
				if (!(entity1 instanceof EC)) {
					entity1 = new EC(entity);
					entity.world.spawnEntity(entity1);
					entity.getEntityData().setInteger(ID_KEY, entity1.getEntityId());
					return true;
				} else {
					RayTraceResult res = ProcedureUtils.raytraceBlocks(entity, 40d);
					if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
						((EC)entity1).shoot(res.hitVec.subtract(entity1.getPositionVector()));
					}
					return false;
				}
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
		public class CustomRender extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/sandpyramid.png");
			private final ModelSandPyramid model = new ModelSandPyramid();
	
			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.shadowSize = 0.3f * SCALE;
			}
	
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				GlStateManager.pushMatrix();
				this.bindEntityTexture(entity);
				GlStateManager.translate(x, y, z);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				float f = (float)entity.ticksExisted + partialTicks;
				int i = entity.getDeathTicks();
				float scale = i > 0 ? (1.0f - Math.min((partialTicks + i) / 20f, 1.0f)) * SCALE
				 : Math.min(f / ((float)entity.waitTime / SCALE), SCALE);
				GlStateManager.rotate(scale == SCALE ? f * 20.0F : 0.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				this.model.render(entity, 0f, 0f, 0f, 0f, 0f, 0.0625f);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}
	
		// Made with Blockbench 4.8.1
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelSandPyramid extends ModelBase {
			private final ModelRenderer bone;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone2;
			private final ModelRenderer bb_main;
			public ModelSandPyramid() {
				textureWidth = 16;
				textureHeight = 16;
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bone, 0.3927F, 0.0F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 0, 0, -3.0F, -8.0F, 0.0F, 6, 8, 0, 0.0F, false));
				bone.cubeList.add(new ModelBox(bone, 0, 0, -3.0F, -8.0F, 0.0F, 6, 8, 0, -0.05F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bone3, 0.0F, 1.5708F, -0.3927F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 0, -3.0F, -8.0F, 0.0F, 6, 8, 0, 0.0F, false));
				bone3.cubeList.add(new ModelBox(bone3, 0, 0, -3.0F, -8.0F, 0.0F, 6, 8, 0, -0.05F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bone4, 0.0F, -1.5708F, 0.3927F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 0, -3.0F, -8.0F, 0.0F, 6, 8, 0, 0.0F, true));
				bone4.cubeList.add(new ModelBox(bone4, 0, 0, -3.0F, -8.0F, 0.0F, 6, 8, 0, -0.05F, true));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(bone2, -2.7489F, 0.0F, -3.1416F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 0, -3.0F, -8.0F, 0.0F, 6, 8, 0, 0.0F, false));
				bone2.cubeList.add(new ModelBox(bone2, 0, 0, -3.0F, -8.0F, 0.0F, 6, 8, 0, -0.05F, false));
		
				bb_main = new ModelRenderer(this);
				bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.cubeList.add(new ModelBox(bb_main, -6, 10, -3.0F, -7.17F, -3.0F, 6, 0, 6, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				bone.render(f5);
				bone3.render(f5);
				bone4.render(f5);
				bone2.render(f5);
				bb_main.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
