
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;

import java.util.Iterator;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFlameFormation extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 447;
	public static final int ENTITYID_RANGED = 448;

	public EntityFlameFormation(ElementsNarutomodMod instance) {
		super(instance, 877);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "flame_formation"), ENTITYID).name("flame_formation").tracker(96, 3, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EC.AttackHook());
	}

	public static class EC extends Entity implements ItemJutsu.IJutsu {
		private static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private final int growTime = 20;
		private EntityLivingBase user;
		private final float contactDamage = 20.0f;

		public EC(World world) {
			super(world);
			this.setSize(1.0f, 2.5f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, float size) {
			this(userIn.world);
			this.user = userIn;
			this.setScale(size);
			this.setLocationAndAngles(userIn.posX, userIn.posY, userIn.posZ, 0.0f, 0.0f);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.RAITON;
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(SCALE, Float.valueOf(1.0F));
		}

		public float getScale() {
			return ((Float) this.getDataManager().get(SCALE)).floatValue();
		}

		public void setScale(float scale) {
			double x = this.posX;
			double y = this.posY;
			double z = this.posZ;
			this.setSize(scale, 2.5f * scale);
			this.setPosition(x, y, z);
			if (!this.world.isRemote) {
				this.getDataManager().set(SCALE, Float.valueOf(scale));
			}
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (SCALE.equals(key) && this.world.isRemote) {
				this.setScale(this.getScale());
			}
		}

		@Override
		public AxisAlignedBB getCollisionBoundingBox() {
			return this.getEntityBoundingBox();
		}

		@Override
		public void applyEntityCollision(Entity entityIn) {
			entityIn.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.user), this.contactDamage + this.rand.nextFloat() * 5.0f);
			entityIn.setFire(15);
		}

		@Override
		public void onUpdate() {
			if (!this.world.isRemote && (this.user == null || !this.user.isEntityAlive() || this.ticksExisted > 400)) {
				this.setDead();
			} else {
				for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this.user, this.getEntityBoundingBox().grow(0.2d))) {
					if (this.isEntityOutside(entity)) {
						this.applyEntityCollision(entity);
					}
				}
				if (this.world.isRemote) {
					float size = this.getScale();
					AxisAlignedBB thisaabb = this.getEntityBoundingBox().grow(0.1d);
					Vec3d vec = new Vec3d(thisaabb.minX, thisaabb.minY, thisaabb.minZ);
					for (int i = 0; i < 10; i++) {
						Vec3d vec1 = vec.add(new Vec3d(thisaabb.maxX - thisaabb.minX, 0d, 0d).scale(this.rand.nextFloat()));
						Particles.spawnParticle(this.world, Particles.Types.FLAME, vec1.x, vec1.y, vec1.z,
						 1, 0.0d, 0.0d, 0.0d, 0.0d, 0.09d * size, 0.0d, 0xffffcf00, (int)(size * 5f));
					}
					for (int i = 0; i < 10; i++) {
						Vec3d vec1 = vec.add(new Vec3d(0d, 0d, thisaabb.maxZ - thisaabb.minZ).scale(this.rand.nextFloat()));
						Particles.spawnParticle(this.world, Particles.Types.FLAME, vec1.x, vec1.y, vec1.z,
						 1, 0.0d, 0.0d, 0.0d, 0.0d, 0.09d * size, 0.0d, 0xffffcf00, (int)(size * 5f));
					}
					vec = new Vec3d(thisaabb.maxX, thisaabb.minY, thisaabb.minZ);
					for (int i = 0; i < 10; i++) {
						Vec3d vec1 = vec.add(new Vec3d(0d, 0d, thisaabb.maxZ - thisaabb.minZ).scale(this.rand.nextFloat()));
						Particles.spawnParticle(this.world, Particles.Types.FLAME, vec1.x, vec1.y, vec1.z,
						 1, 0.0d, 0.0d, 0.0d, 0.0d, 0.09d * size, 0.0d, 0xffffcf00, (int)(size * 5f));
					}
					vec = new Vec3d(thisaabb.minX, thisaabb.minY, thisaabb.maxZ);
					for (int i = 0; i < 10; i++) {
						Vec3d vec1 = vec.add(new Vec3d(thisaabb.maxX - thisaabb.minX, 0d, 0d).scale(this.rand.nextFloat()));
						Particles.spawnParticle(this.world, Particles.Types.FLAME, vec1.x, vec1.y, vec1.z,
						 1, 0.0d, 0.0d, 0.0d, 0.0d, 0.09d * size, 0.0d, 0xffffcf00, (int)(size * 5f));
					}
				} else if (this.ticksExisted % 10 == 1) {
					this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:flamethrow")), 
					 2.0f, this.rand.nextFloat() * 0.5f + 0.6f);
				}
			}
		}

		public boolean isEntityOutside(Entity entity) {
			AxisAlignedBB thisaabb = this.getEntityBoundingBox();
			AxisAlignedBB aabb = entity.getEntityBoundingBox();
			return aabb.maxX <= thisaabb.minX || aabb.minX >= thisaabb.maxX || aabb.maxY <= thisaabb.minY || aabb.minY >= thisaabb.maxY || aabb.maxZ <= thisaabb.minZ || aabb.minZ >= thisaabb.maxZ;
		}

		public boolean isEntityInside(Entity entity) {
			AxisAlignedBB aabb = entity.getEntityBoundingBox();
			return aabb.intersect(this.getEntityBoundingBox()).equals(aabb);
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.spawnEntity(new EC(entity, power));
				return true;
			}

			@Override
			public float getPowerupDelay() {
				return 50.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 20.0f;
			}
		}

		public static class AttackHook {
			@SubscribeEvent
			public void onAttacked(LivingAttackEvent event) {
				EntityLivingBase entity = event.getEntityLiving();
				Entity ec = entity.world.findNearestEntityWithinAABB(EC.class, entity.getEntityBoundingBox(), entity);
				if (ec instanceof EC && ((EC)ec).isEntityInside(entity)) {
					Entity attacker = event.getSource().getTrueSource();
					if (attacker != null && ((EC)ec).isEntityOutside(attacker)) {
						event.setCanceled(true);
					}
				}
			}

			@SubscribeEvent
			public void onExplosion(ExplosionEvent.Detonate event) {
				if (!event.getWorld().isRemote) {
					Vec3d vec = event.getExplosion().getPosition();
					float size = (float)ReflectionHelper.getPrivateValue(Explosion.class, event.getExplosion(), 8);
					AxisAlignedBB bb = new AxisAlignedBB(vec.x - size, vec.y - size, vec.z - size, vec.x + size, vec.y + size, vec.z + size);
					for (EC ec : event.getWorld().getEntitiesWithinAABB(EC.class, bb)) {
						EntityLivingBase exploder = event.getExplosion().getExplosivePlacedBy();
						if (!ec.getEntityBoundingBox().contains(vec) && (exploder == null || !ec.isEntityInside(exploder))) {
							Iterator<Entity> itr = event.getAffectedEntities().iterator();
							while (itr.hasNext()) {
								if (ec.isEntityInside(itr.next())) {
									itr.remove();
								}
							}
						}
					}
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/red_flames_128.png");
			private final ModelFlameFormation model = new ModelFlameFormation();

			public RenderCustom(RenderManager renderManager) {
				super(renderManager);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				GlStateManager.pushMatrix();
				this.bindEntityTexture(entity);
				float scale = entity.getScale();
				float f = partialTicks + entity.ticksExisted;
				GlStateManager.translate(x, y, z);
				GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(scale, scale * Math.min(f / entity.growTime, 1f), scale);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.translate(0.0F, f * 0.02F, 0.0F);
				GlStateManager.matrixMode(5888);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				this.model.render(entity, 0.0f, 0.0f, f, 0.0f, 0.0f, 0.0625f);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
	            GlStateManager.matrixMode(5890);
	            GlStateManager.loadIdentity();
	            GlStateManager.matrixMode(5888);
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

		// Made with Blockbench 4.10.3
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelFlameFormation extends ModelBase {
			private final ModelRenderer bone;
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer cube_r3;
			private final ModelRenderer bone2;
			private final ModelRenderer cube_r4;
			private final ModelRenderer cube_r5;
			private final ModelRenderer cube_r6;
			private final ModelRenderer bone3;
			private final ModelRenderer cube_r7;
			private final ModelRenderer cube_r8;
			private final ModelRenderer cube_r9;
			private final ModelRenderer bone4;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			private final ModelRenderer bone5;
			private final ModelRenderer cube_r13;
			private final ModelRenderer cube_r14;
			private final ModelRenderer cube_r15;
			private final ModelRenderer bone6;
			private final ModelRenderer cube_r16;
			private final ModelRenderer cube_r17;
			private final ModelRenderer cube_r18;
			private final ModelRenderer bone7;
			private final ModelRenderer cube_r19;
			private final ModelRenderer cube_r20;
			private final ModelRenderer cube_r21;
			private final ModelRenderer bone8;
			private final ModelRenderer cube_r22;
			private final ModelRenderer cube_r23;
			private final ModelRenderer cube_r24;
			private final ModelRenderer bone9;
			private final ModelRenderer cube_r25;
			private final ModelRenderer cube_r26;
			private final ModelRenderer cube_r27;
			public ModelFlameFormation() {
				textureWidth = 32;
				textureHeight = 32;
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 0, 0, -8.0F, -32.0F, -8.0F, 16, 32, 0, 0.0F, false));
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, -16.0F, 0.0F);
				bone.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.0F, 3.1416F, 0.0F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -8.0F, -16.0F, -8.0F, 16, 32, 0, 0.0F, false));
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(0.0F, -16.0F, 0.0F);
				bone.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.0F, -1.5708F, 0.0F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 0, -8.0F, -16.0F, -8.0F, 16, 32, 0, 0.0F, false));
				cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(0.0F, -16.0F, 0.0F);
				bone.addChild(cube_r3);
				setRotationAngle(cube_r3, 0.0F, 1.5708F, 0.0F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 0, -8.0F, -16.0F, -8.0F, 16, 32, 0, 0.0F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 31, -8.0F, -33.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(0.0F, -48.0F, 0.0F);
				bone2.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.0F, 3.1416F, 0.0F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 31, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.0F, -48.0F, 0.0F);
				bone2.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.0F, -1.5708F, 0.0F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 0, 31, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(0.0F, -48.0F, 0.0F);
				bone2.addChild(cube_r6);
				setRotationAngle(cube_r6, 0.0F, 1.5708F, 0.0F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 31, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 30, -8.0F, -34.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(0.0F, -49.0F, 0.0F);
				bone3.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.0F, 3.1416F, 0.0F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 30, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(0.0F, -49.0F, 0.0F);
				bone3.addChild(cube_r8);
				setRotationAngle(cube_r8, 0.0F, -1.5708F, 0.0F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 0, 30, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(0.0F, -49.0F, 0.0F);
				bone3.addChild(cube_r9);
				setRotationAngle(cube_r9, 0.0F, 1.5708F, 0.0F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 30, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 29, -8.0F, -35.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(0.0F, -50.0F, 0.0F);
				bone4.addChild(cube_r10);
				setRotationAngle(cube_r10, 0.0F, 3.1416F, 0.0F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 29, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(0.0F, -50.0F, 0.0F);
				bone4.addChild(cube_r11);
				setRotationAngle(cube_r11, 0.0F, -1.5708F, 0.0F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 29, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(0.0F, -50.0F, 0.0F);
				bone4.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.0F, 1.5708F, 0.0F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 29, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 28, -8.0F, -36.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(0.0F, -51.0F, 0.0F);
				bone5.addChild(cube_r13);
				setRotationAngle(cube_r13, 0.0F, 3.1416F, 0.0F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 28, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(0.0F, -51.0F, 0.0F);
				bone5.addChild(cube_r14);
				setRotationAngle(cube_r14, 0.0F, -1.5708F, 0.0F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 28, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r15 = new ModelRenderer(this);
				cube_r15.setRotationPoint(0.0F, -51.0F, 0.0F);
				bone5.addChild(cube_r15);
				setRotationAngle(cube_r15, 0.0F, 1.5708F, 0.0F);
				cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 28, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 27, -8.0F, -37.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r16 = new ModelRenderer(this);
				cube_r16.setRotationPoint(0.0F, -52.0F, 0.0F);
				bone6.addChild(cube_r16);
				setRotationAngle(cube_r16, 0.0F, 3.1416F, 0.0F);
				cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 27, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r17 = new ModelRenderer(this);
				cube_r17.setRotationPoint(0.0F, -52.0F, 0.0F);
				bone6.addChild(cube_r17);
				setRotationAngle(cube_r17, 0.0F, -1.5708F, 0.0F);
				cube_r17.cubeList.add(new ModelBox(cube_r17, 0, 27, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r18 = new ModelRenderer(this);
				cube_r18.setRotationPoint(0.0F, -52.0F, 0.0F);
				bone6.addChild(cube_r18);
				setRotationAngle(cube_r18, 0.0F, 1.5708F, 0.0F);
				cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 27, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 26, -8.0F, -38.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r19 = new ModelRenderer(this);
				cube_r19.setRotationPoint(0.0F, -53.0F, 0.0F);
				bone7.addChild(cube_r19);
				setRotationAngle(cube_r19, 0.0F, 3.1416F, 0.0F);
				cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 26, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r20 = new ModelRenderer(this);
				cube_r20.setRotationPoint(0.0F, -53.0F, 0.0F);
				bone7.addChild(cube_r20);
				setRotationAngle(cube_r20, 0.0F, -1.5708F, 0.0F);
				cube_r20.cubeList.add(new ModelBox(cube_r20, 0, 26, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r21 = new ModelRenderer(this);
				cube_r21.setRotationPoint(0.0F, -53.0F, 0.0F);
				bone7.addChild(cube_r21);
				setRotationAngle(cube_r21, 0.0F, 1.5708F, 0.0F);
				cube_r21.cubeList.add(new ModelBox(cube_r21, 0, 26, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 25, -8.0F, -39.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r22 = new ModelRenderer(this);
				cube_r22.setRotationPoint(0.0F, -54.0F, 0.0F);
				bone8.addChild(cube_r22);
				setRotationAngle(cube_r22, 0.0F, 3.1416F, 0.0F);
				cube_r22.cubeList.add(new ModelBox(cube_r22, 0, 25, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r23 = new ModelRenderer(this);
				cube_r23.setRotationPoint(0.0F, -54.0F, 0.0F);
				bone8.addChild(cube_r23);
				setRotationAngle(cube_r23, 0.0F, -1.5708F, 0.0F);
				cube_r23.cubeList.add(new ModelBox(cube_r23, 0, 25, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r24 = new ModelRenderer(this);
				cube_r24.setRotationPoint(0.0F, -54.0F, 0.0F);
				bone8.addChild(cube_r24);
				setRotationAngle(cube_r24, 0.0F, 1.5708F, 0.0F);
				cube_r24.cubeList.add(new ModelBox(cube_r24, 0, 25, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 0, 24, -8.0F, -40.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r25 = new ModelRenderer(this);
				cube_r25.setRotationPoint(0.0F, -55.0F, 0.0F);
				bone9.addChild(cube_r25);
				setRotationAngle(cube_r25, 0.0F, 3.1416F, 0.0F);
				cube_r25.cubeList.add(new ModelBox(cube_r25, 0, 24, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r26 = new ModelRenderer(this);
				cube_r26.setRotationPoint(0.0F, -55.0F, 0.0F);
				bone9.addChild(cube_r26);
				setRotationAngle(cube_r26, 0.0F, -1.5708F, 0.0F);
				cube_r26.cubeList.add(new ModelBox(cube_r26, 0, 24, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
				cube_r27 = new ModelRenderer(this);
				cube_r27.setRotationPoint(0.0F, -55.0F, 0.0F);
				bone9.addChild(cube_r27);
				setRotationAngle(cube_r27, 0.0F, 1.5708F, 0.0F);
				cube_r27.cubeList.add(new ModelBox(cube_r27, 0, 24, -8.0F, 15.0F, -8.0F, 16, 1, 0, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.74f);
				bone.render(f5);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.66f);
				bone2.render(f5);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.58f);
				bone3.render(f5);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);
				bone4.render(f5);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.42f);
				bone5.render(f5);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.34f);
				bone6.render(f5);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.26f);
				bone7.render(f5);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.18f);
				bone8.render(f5);
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.101f);
				bone9.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
