package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;

import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import java.util.Map;
import com.google.common.collect.Maps;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.glu.GLU;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySpecialEffect extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 50;
	public static final int ENTITYID_RANGED = 51;
	private static final int DEFAULT_DURATION = 600;
	
	public EntitySpecialEffect(ElementsNarutomodMod instance) {
		super(instance, 242);
	}

	public void initElements() {
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		  .id(new ResourceLocation("narutomod", "specialeffectentity"), ENTITYID).name("specialeffectentity").tracker(128, 1, true).build());
	}

	public enum Type {
		ROTATING_LINES_COLOR_END(0),
		EXPANDING_SPHERES_FADE_TO_BLACK(1);

		private final int id;
		private static final Map<Integer, Type> TYPES = Maps.newHashMap();
		
		static {
			for (Type type : values())
				TYPES.put(Integer.valueOf(type.getID()), type);
		}

		Type(int i) {
			this.id = i;
		}

		public int getID() {
			return this.id;
		}

		public static Type getTypeFromId(int i) {
			return TYPES.get(Integer.valueOf(i));
		}
	}
	
	public static EntityCustom spawn(World worldIn, Type type, int color, float radius, int lifespan, double x, double y, double z) {
		EntityCustom entity = new EntityCustom(worldIn, type, color, radius, lifespan);
		entity.setPosition(x, y, z);
		worldIn.spawnEntity(entity);
		return entity;
	}
	
	public static class EntityCustom extends Entity {
		private static final DataParameter<Integer> TYPEID = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Float> RADIUS = EntityDataManager.<Float>createKey(EntityCustom.class, DataSerializers.FLOAT);
		private static final DataParameter<Integer> LIFESPAN = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		
		public EntityCustom(World world) {
			super(world);
			this.setSize(1.0F, 1.0F);
			// this.setNoGravity(true);
			// this.setEntityInvulnerable(true);
			this.ignoreFrustumCheck = true;
			this.isImmuneToFire = true;
		}

		public EntityCustom(World world, float radius, int lifespan) {
			this(world, Type.ROTATING_LINES_COLOR_END, 0xFF00FF, radius, lifespan);
		}

		public EntityCustom(World world, Type type, int color, float radius, int lifespan) {
			this(world);
			this.setEffectType(type);
			this.setRadius(radius);
			this.setLifespan(lifespan);
			this.setColor(color);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(TYPEID, Integer.valueOf(0));
			this.getDataManager().register(AGE, Integer.valueOf(0));
			this.getDataManager().register(RADIUS, Float.valueOf(200f));
			this.getDataManager().register(LIFESPAN, Integer.valueOf(600));
			this.getDataManager().register(COLOR, Integer.valueOf(0xFF00FF));
		}

		private Type getEffectType() {
			return Type.getTypeFromId(((Integer)this.getDataManager().get(TYPEID)).intValue());
		}

		private void setEffectType(Type type) {
			this.getDataManager().set(TYPEID, Integer.valueOf(type.getID()));
		}

		public int getAge() {
			return ((Integer) this.getDataManager().get(AGE)).intValue();
		}

		protected void setAge(int age) {
			this.getDataManager().set(AGE, Integer.valueOf(age));
		}

		public float getRadius() {
			return ((Float) this.getDataManager().get(RADIUS)).floatValue();
		}

		protected void setRadius(float r) {
			this.getDataManager().set(RADIUS, Float.valueOf(r));
		}

		public int getLifespan() {
			return ((Integer) this.getDataManager().get(LIFESPAN)).intValue();
		}

		protected void setLifespan(int lifespan) {
			this.getDataManager().set(LIFESPAN, Integer.valueOf(lifespan));
		}

		public int getColor() {
			return ((Integer)this.getDataManager().get(COLOR)).intValue();
		}

		protected void setColor(int color) {
			this.getDataManager().set(COLOR, Integer.valueOf(color));
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return false;
		}

		@Override
		public void setDead() {
			if (this.getAge() >= this.getLifespan() || this.getAge() < 0)
				this.isDead = true;
		}

		@Override
		public void onUpdate() {
			this.lastTickPosX = this.posX;
			this.lastTickPosY = this.posY;
			this.lastTickPosZ = this.posZ;
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			// super.onUpdate();
			this.setAge(this.getAge() + 1);
			//if (this.getAge() == 2)
			//	this.setPositionAndUpdate(this.posX, this.posY - this.height / 2, this.posZ);
			this.rotationYaw += 30.0F;
			this.setDead();
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return true;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			this.setEffectType(Type.getTypeFromId(compound.getInteger("type")));
			this.setAge(compound.getInteger("age"));
			this.setRadius(compound.getFloat("radius"));
			this.setLifespan(compound.getInteger("lifespan"));
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setInteger("type", this.getEffectType().getID());
			compound.setInteger("age", this.getAge());
			compound.setFloat("radius", this.getRadius());
			compound.setInteger("lifespan", this.getLifespan());
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderSpecialEffect(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderSpecialEffect extends Render<EntityCustom> {
			private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/white_square.png");
			public int sphereIdOutside;
			public int sphereIdInside;
			public final Sphere sphere;
	
			public RenderSpecialEffect(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.sphere = new Sphere();
				this.initSphere();
			}
	
			private void initSphere() {
				this.sphere.setDrawStyle(GLU.GLU_FILL);
				this.sphere.setNormals(GLU.GLU_SMOOTH);
				this.sphere.setOrientation(GLU.GLU_OUTSIDE);
				this.sphereIdOutside = GLAllocation.generateDisplayLists(1);
				GlStateManager.glNewList(this.sphereIdOutside, 0x1300);
				this.bindTexture(TEXTURE);
				this.sphere.draw(1.0F, 32, 32);
				GlStateManager.glEndList();	
				this.sphere.setOrientation(GLU.GLU_INSIDE);
				this.sphereIdInside = GLAllocation.generateDisplayLists(1);
				GlStateManager.glNewList(this.sphereIdInside, 0x1300);
				this.bindTexture(TEXTURE);
				this.sphere.draw(1.0F, 32, 32);
				GlStateManager.glEndList();
			}
	
			@Override
			public boolean shouldRender(EntityCustom livingEntity, ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				switch (entity.getEffectType()) {
					case ROTATING_LINES_COLOR_END:
						this.renderRotatingLines(entity, x, y, z, entityYaw, partialTicks);
						break;
					case EXPANDING_SPHERES_FADE_TO_BLACK:
						this.renderExpandingSphere(entity, x, y, z, entityYaw, partialTicks);
						break;
				}
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
			private void renderRotatingLines(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + entity.height / 2, z);
				GlStateManager.rotate(entityYaw, 0.0F, 1.0F, 0.0F);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				RenderHelper.disableStandardItemLighting();
				float f = (entity.getAge() + partialTicks) / entity.getLifespan();
				float f1 = f;// 0.0F;
				// if (f > 0.5F)
				// f1 = (f - 0.5F) / 0.5F;
				Random random = new Random(432L);
				GlStateManager.disableTexture2D();
				GlStateManager.shadeModel(7425);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
				GlStateManager.disableAlpha();
				GlStateManager.enableCull();//GlStateManager.disableCull();
				GlStateManager.depthMask(false);
				float r = entity.getRadius();
				int j = entity.getColor();
				int red = j >> 16 & 0xFF;
				int green = j >> 8 & 0xFF;
				int blue = j & 0xFF;
				// for (int i = 0; i < (f + f * f) / 2.0F * 60.0F; i++) {
				for (int i = 0; i < 120.0F; i++) {
					GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(random.nextFloat() * 360.0F + f * 90.0F, 0.0F, 0.0F, 1.0F);
					float f2 = (random.nextFloat() + f1) * 0.5F * r;
					float f3 = (random.nextFloat() + f1) * 0.12F * r;
					bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
					bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int) (255.0F * (1.0F - f1))).endVertex();
					bufferbuilder.pos(-0.866D * f3, f2, (-0.5F * f3)).color(red, green, blue, 0).endVertex();
					bufferbuilder.pos(0.866D * f3, f2, (-0.5F * f3)).color(red, green, blue, 0).endVertex();
					bufferbuilder.pos(0.0D, f2, (1.0F * f3)).color(red, green, blue, 0).endVertex();
					bufferbuilder.pos(-0.866D * f3, f2, (-0.5F * f3)).color(red, green, blue, 0).endVertex();
					tessellator.draw();
				}
				GlStateManager.depthMask(true);
				GlStateManager.disableCull();//GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.shadeModel(7424);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableTexture2D();
				GlStateManager.enableAlpha();
				RenderHelper.enableStandardItemLighting();
				GlStateManager.popMatrix();
			}
	
			private void renderExpandingSphere(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				int life = entity.getLifespan();
				float maxscale = entity.getRadius();
				float age = (float)entity.getAge() + partialTicks;
				float f = 1.0f;
				if (age > 0.6f * life) {
					f = 1.0f - (age - 0.6f * life) / (0.4f * life);
				}
				GlStateManager.pushMatrix();
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.depthMask(false);
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xF0, 0xF0);
				for (int i = 0; age - i > 0f; i++)  {
					if (age <= maxscale || i > age - maxscale) {
						float scale = (age - i) * 0.7f;
						float c = 1.0F - 0.05F * i;
						GlStateManager.pushMatrix();
						GlStateManager.color(c, c, c, 0.101F);
						GlStateManager.translate(x, y, z);
						GlStateManager.scale(scale, scale, scale);
						GlStateManager.callList(this.sphereIdOutside);
						GlStateManager.callList(this.sphereIdInside);
						GlStateManager.popMatrix();
					}
				}
				GlStateManager.enableLighting();
				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
				GlStateManager.disableAlpha();
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return null;
			}
		}
	}
}
