
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityHidingInAsh;
import net.narutomod.entity.EntityFirestream;
import net.narutomod.entity.EntityFlameSlice;
import net.narutomod.entity.EntityFlameFormation;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.NarutomodModVariables;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKaton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:katon")
	public static final Item block = null;
	public static final int ENTITYID = 123;
	//public static final int ENTITY2ID = 10123;
	public static final ItemJutsu.JutsuEnum GREATFIREBALL = new ItemJutsu.JutsuEnum(0, "katonfireball", 'C', 30d, new EntityBigFireball.Jutsu());
	public static final ItemJutsu.JutsuEnum GFANNIHILATION = new ItemJutsu.JutsuEnum(1, "tooltip.katon.annihilation", 'B', 50d, new EntityFirestream.EC.Jutsu1());
	public static final ItemJutsu.JutsuEnum HIDINGINASH = new ItemJutsu.JutsuEnum(2, "hiding_in_ash", 'B', 50d, new EntityHidingInAsh.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum GREATFLAME = new ItemJutsu.JutsuEnum(3, "katonfirestream", 'C', 20d, new EntityFirestream.EC.Jutsu2());
	public static final ItemJutsu.JutsuEnum FLAMESLICE = new ItemJutsu.JutsuEnum(4, "flame_slice", 'D', 20d, new EntityFlameSlice.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum BARRIER = new ItemJutsu.JutsuEnum(5, "flame_formation", 'B', 100d, new EntityFlameFormation.EC.Jutsu());

	public ItemKaton(ElementsNarutomodMod instance) {
		super(instance, 366);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(GREATFIREBALL, GFANNIHILATION, HIDINGINASH, GREATFLAME, FLAMESLICE, BARRIER));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityBigFireball.class)
				.id(new ResourceLocation("narutomod", "katonfireball"), ENTITYID).name("katonfireball").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:katon", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.KATON, list);
			this.setRegistryName("katon");
			this.setUnlocalizedName("katon");
			this.setCreativeTab(TabModTab.tab);
			//this.defaultCooldownMap[GREATFIREBALL.index] = 0;
			//this.defaultCooldownMap[1] = 0;
		}
	}

	public static class EntityBigFireball extends EntityScalableProjectile.Base implements ItemJutsu.IJutsu {
		private float fullScale = 1f;
		private final int timeToFullscale = 20;
		private int explosionSize;
		private float damage;
		private boolean guided;
		private Entity target;
		
		public EntityBigFireball(World a) {
			super(a);
			this.setOGSize(0.8F, 0.8F);
		}

		public EntityBigFireball(EntityLivingBase shooter, float fullScale, boolean isGuided) {
			super(shooter);
			this.setOGSize(0.8F, 0.8F);
			this.fullScale = fullScale;
			this.explosionSize = Math.max((int)fullScale - 1, 0);
			this.damage = fullScale * 10.0f;
			this.guided = isGuided;
			//this.setEntityScale(0.1f);
			Vec3d vec3d = shooter.getLookVec();
			this.setPosition(shooter.posX + vec3d.x, shooter.posY + shooter.getEyeHeight() - 0.2d * fullScale + vec3d.y, shooter.posZ + vec3d.z);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.KATON;
		}

		public void setDamage(float amount) {
			this.damage = amount;
		}

		@Override
		public boolean isImmuneToExplosions() {
			return true;
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.typeOfHit == RayTraceResult.Type.BLOCK && this.fullScale >= 2.0f && this.ticksInAir < 15) {
				return;
			}
			if (!this.world.isRemote) {
				if (this.shootingEntity != null) {
					this.shootingEntity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 40d);
				}
				if (result.entityHit != null && (result.entityHit.equals(this.shootingEntity)
				 || (result.entityHit instanceof EntityBigFireball && ((EntityBigFireball)result.entityHit).shootingEntity == this.shootingEntity))) {
					return;
				}
				ProcedureAoeCommand.set(this, 0d, this.fullScale * 0.4f).exclude(this.shootingEntity)
				 .damageEntities(ItemJutsu.causeJutsuDamage(this, this.shootingEntity).setFireDamage(), this.damage).setFire(15);
				boolean flag = ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
				this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, this.explosionSize, flag, false);
				this.setDead();
			}
		}

		@Override
		public void renderParticles() {
			Particles.spawnParticle(this.world, Particles.Types.FLAME, this.posX, this.posY + (this.height / 2.0F), this.posZ,
			  (int)this.fullScale * 2, 0.3d * this.width, 0.3d * this.height, 0.3d * this.width, 0d, 0d, 0d,
			  0xffff0000|((0x40+this.rand.nextInt(0x80))<<8), 30);
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && (this.ticksInAir > (this.guided ? 200 : 100) || this.isInWater())) {
				this.setDead();
			} else {
				if (!this.world.isRemote && this.ticksAlive <= this.timeToFullscale) {
					this.setEntityScale(1f + (this.fullScale - 1f) * this.ticksAlive / this.timeToFullscale);
				}
				if (this.guided && this.shootingEntity != null) {
					Vec3d vec;
					if (this.target == null) {
						this.target = this.shootingEntity instanceof EntityLiving ? ((EntityLiving)this.shootingEntity).getAttackTarget()
						 : ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d, 3d, EntityBigFireball.class).entityHit;
						vec = this.target != null ? this.target.getPositionEyes(1f).subtract(this.getPositionVector())
						 : this.shootingEntity.getLookVec();
					} else {
						vec = this.target.getPositionEyes(1f).subtract(this.getPositionVector());
					}
					this.motionX *= 0.9D;
					this.motionY *= 0.9D;
					this.motionZ *= 0.9D;
					this.shoot(vec.x, vec.y, vec.z, 0.99f, 0f);
				}
				if (this.rand.nextFloat() <= 0.2f) {
					//this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1, this.rand.nextFloat() + 0.5f);
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:flamethrow")),
					 this.fullScale >= 10.0f ? 5.0F : 1.0f, this.rand.nextFloat() * 0.5f + 0.6f);
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 0.5f) {
					this.createJutsu(entity, entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power,
					 stack.getItem() instanceof RangedItem && ((RangedItem)stack.getItem()).getCurrentJutsuXpModifier(stack, entity) <= 0.5f);
					//if (entity instanceof EntityPlayer)
					//	ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, (long)(power * 80));
					return true;
				}
				return false;
			}

			public void createJutsu(EntityLivingBase entity, double x, double y, double z, float power) {
				this.createJutsu(entity, x, y, z, power, false);
			}

			public void createJutsu(EntityLivingBase entity, double x, double y, double z, float power, boolean isGuided) {
				EntityBigFireball entityarrow = new EntityBigFireball(entity, power, isGuided);
				entityarrow.shoot(x, y, z, 0.99f, 0);
				entity.world.spawnEntity(entityarrow);
			}

			@Override
			public float getBasePower() {
				return 0.5f;
			}
	
			@Override
			public float getPowerupDelay() {
				return 30.0f;
			}
	
			@Override
			public float getMaxPower() {
				return 10.0f;
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
			RenderingRegistry.registerEntityRenderingHandler(EntityBigFireball.class, renderManager -> {
				return new RenderBigFireball(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderBigFireball extends Render<EntityBigFireball> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/fireball.png");
	
			public RenderBigFireball(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			public void doRender(EntityBigFireball entity, double x, double y, double z, float entityYaw, float partialTicks) {
				GlStateManager.pushMatrix();
				this.bindEntityTexture(entity);
				float scale = entity.getEntityScale();
				GlStateManager.translate(x, y + 0.375D * scale, z);
				GlStateManager.enableRescaleNormal();
				GlStateManager.scale(scale, scale, scale);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				GlStateManager.rotate(180F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(30F * (partialTicks + entity.ticksExisted), 0.0F, 0.0F, 1.0F);
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
				bufferbuilder.pos(-0.375D, -0.375D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.375D, -0.375D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.375D, 0.375D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.375D, 0.375D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				tessellator.draw();
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.disableRescaleNormal();
				GlStateManager.popMatrix();
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityBigFireball entity) {
				return this.texture;
			}
		}
	}
}
