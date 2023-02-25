
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemRanton;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityLaserCircus extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 279;
	public static final int ENTITYID_RANGED = 280;
	private static final String LCTAG = "LaserCircusRingEntityId";


	public EntityLaserCircus(ElementsNarutomodMod instance) {
		super(instance, 598);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "laser_circus"), ENTITYID).name("laser_circus").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityRing.class)
		 .id(new ResourceLocation("narutomod", "laser_ring"), ENTITYID_RANGED).name("laser_ring").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityRing.class, renderManager -> new RenderRing(renderManager));
	}

	public static class EC extends Entity {
		private EntityLivingBase summoner;
		private int duration;
		private ItemStack rantonstack;

		public EC(World a) {
			super(a);
			this.setSize(0.01f, 0.01f);
		}

		protected EC(EntityLivingBase summonerIn, float powerIn, ItemStack stack) {
			this(summonerIn.world);
			this.setSize(0.01f, 0.01f);
			this.summoner = summonerIn;
			this.duration = (int)(powerIn * 20);
			this.rantonstack = stack;
			this.setIdlePosition();
		}

		@Override
		protected void entityInit() {
		}

		private void setIdlePosition() {
			if (this.summoner != null) {
				Vec3d vec = this.summoner.getLookVec();
				vec = this.summoner.getPositionVector().addVector(vec.x, 1.2d, vec.z);
				this.setPosition(vec.x, vec.y, vec.z);
			}
		}

		@Override
		public void onUpdate() {
			//super.onUpdate();
			if (this.summoner != null && this.summoner.isEntityAlive() && this.ticksExisted <= this.duration) {
				this.setIdlePosition();
				if (this.ticksExisted % 10 == 0) {
					this.playSound(SoundEvent.REGISTRY
					 .getObject(new ResourceLocation("narutomod:electricity")), 1.0f, this.rand.nextFloat() * 0.6f + 0.6f);
				}
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(this.summoner, 25d);
				if (res != null) {
					this.setLightningAt(res.hitVec);
				}
			} else if (!this.world.isRemote) {
				this.setDead();
				if (this.rantonstack != null) {
					Entity entity = this.world.getEntityByID(this.rantonstack.getTagCompound().getInteger(LCTAG));
					if (entity instanceof EntityRing) {
						entity.setDead();
					}
				}
			}
		}

		private void setLightningAt(Vec3d targetVec) {
			EntityLightningArc.Base entity2 = new EntityLightningArc.Base(this.world,
			 this.getPositionVector(), targetVec, 0xc00000ff, 10, 0.1f);
			entity2.setDamage(ItemJutsu.causeJutsuDamage(this, this.summoner), this.getDamage(), this.summoner);
			this.world.spawnEntity(entity2);
		}

		private float getDamage() {
			float f = Math.max(((ItemJutsu.Base)this.rantonstack.getItem()).getXpRatio(this.rantonstack, ItemRanton.LASERCIRCUS), 1f);
			return this.rand.nextFloat() * f * 20f;
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
				entity.world.spawnEntity(new EC(entity, power, stack));
				return true;
			}
		}
	}

	public static boolean ringSpawned(World world, ItemStack stack) {
		return stack.hasTagCompound() && world.getEntityByID(stack.getTagCompound().getInteger(LCTAG)) instanceof EntityRing;
	}

	public static class EntityRing extends Entity {
		private static final DataParameter<Integer> USERID = EntityDataManager.<Integer>createKey(EntityRing.class, DataSerializers.VARINT);

		public EntityRing(World worldIn) {
			super(worldIn);
			this.setSize(1.0F, 1.0F);
		}

		public EntityRing(EntityLivingBase userIn, ItemStack stack) {
			this(userIn.world);
			this.setSize(1.0F, 1.0F);
			this.setUser(userIn);
			this.setIdlePosition();
			stack.getTagCompound().setInteger(LCTAG, this.getEntityId());
			this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:lasercircus"))), 1.0f, 1.0f);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(USERID, Integer.valueOf(-1));
		}

		private void setUser(EntityLivingBase user) {
			this.getDataManager().set(USERID, Integer.valueOf(user.getEntityId()));
		}

		protected EntityLivingBase getUser() {
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(USERID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void setIdlePosition() {
			EntityLivingBase user = this.getUser();
			if (user != null) {
				Vec3d vec = user.getLookVec();
				vec = user.getPositionVector().addVector(vec.x, 0.6d, vec.z);
				this.setLocationAndAngles(vec.x, vec.y, vec.z, user.rotationYaw, 0f);
			}
		}

		@Override
		public void onUpdate() {
			if (this.getUser() != null) {
				this.setIdlePosition();
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderRing extends Render<EntityRing> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/ring_lightning.png");

		public RenderRing(RenderManager renderManager) {
			super(renderManager);
			shadowSize = 0.1f;
		}

		@Override
		public void doRender(EntityRing entity, double x, double y, double z, float entityYaw, float partialTicks) {
			this.bindEntityTexture(entity);
			GlStateManager.pushMatrix();
			float ageInTicks = (float)entity.ticksExisted + partialTicks;
			float scale = (1f - (ageInTicks % 5f) / 5f) * 3.0F;
			GlStateManager.translate(x, y + 0.5d, z);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(scale, scale, scale);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.rotate(-entity.rotationYaw, 0.0F, 1.0F, 0.0F);
			//GlStateManager.rotate(user.rot, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(9f * ageInTicks, 0.0F, 0.0F, 1.0F);
			GlStateManager.disableCull();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			int alpha = (int)(255F * Math.min(ageInTicks / 30F, 1.0F));
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
			bufferbuilder.pos(-0.5D, -0.5D, 0.0D).tex(0.0D, 1.0D).color(255, 255, 255, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, -0.5D, 0.0D).tex(1.0D, 1.0D).color(255, 255, 255, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, 0.5D, 0.0D).tex(1.0D, 0.0D).color(255, 255, 255, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(-0.5D, 0.5D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.enableCull();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityRing entity) {
			return TEXTURE;
		}
	}
}
