
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

import net.narutomod.creativetab.TabModTab;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityHidingInAsh;
import net.narutomod.entity.EntityFirestream;

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

	public ItemKaton(ElementsNarutomodMod instance) {
		super(instance, 366);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(GREATFIREBALL, GFANNIHILATION, HIDINGINASH, GREATFLAME));
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

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum je = this.getCurrentJutsu(stack);
			if (je == HIDINGINASH) {
				return this.getPower(stack, entity, timeLeft, 1.0f, 15f);
			} else if (je == GREATFIREBALL) {
				return this.getPower(stack, entity, timeLeft, 0.5f, 30f);
			} else {
				return this.getPower(stack, entity, timeLeft, 1.0f, 30f);
			}
			//float power = 1f + (float)(this.getMaxUseDuration() - timeLeft) / (this.getCurrentJutsu(stack) == HIDINGINASH ? 10 : 20);
			//return Math.min(power, this.getMaxPower(stack, entity));
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			float f = super.getMaxPower(stack, entity);
			if (jutsu == GREATFLAME) {
				return Math.min(f, 30.0f);
			} else if (jutsu == GFANNIHILATION) {
				return Math.min(f, 20.0f);
			} else if (jutsu == GREATFIREBALL) {
				return Math.min(f, 10.0f);
			}
			return f;
		}
	}

	public static class EntityBigFireball extends EntityScalableProjectile.Base {
		private float fullScale = 1f;
		private final int timeToFullscale = 20;
		private int explosionSize;
		private float damage;
		
		public EntityBigFireball(World a) {
			super(a);
			this.setOGSize(0.8F, 0.8F);
		}

		public EntityBigFireball(EntityLivingBase shooter, float fullScale) {
			super(shooter);
			this.setOGSize(0.8F, 0.8F);
			this.fullScale = fullScale;
			this.explosionSize = Math.max((int)fullScale - 1, 0);
			this.damage = fullScale * 10.0f;
			//this.setEntityScale(0.1f);
			Vec3d vec3d = shooter.getLookVec();
			this.setPosition(shooter.posX + vec3d.x, shooter.posY + 1.2D + vec3d.y, shooter.posZ + vec3d.z);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.typeOfHit == RayTraceResult.Type.BLOCK && this.fullScale >= 2.0f && this.ticksInAir <= 15) {
				return;
			}
			if (!this.world.isRemote) {
				if (this.shootingEntity != null) {
					this.shootingEntity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 40d);
				}
				if (result.entityHit != null) {
					if (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntityBigFireball)
						return;
					result.entityHit.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity).setFireDamage(), this.damage);
					result.entityHit.setFire(10);
				}
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
			if (!this.world.isRemote && (this.ticksInAir > 100 || this.isInWater())) {
				this.setDead();
			} else {
				if (!this.world.isRemote && this.ticksAlive <= this.timeToFullscale) {
					this.setEntityScale(1f + (this.fullScale - 1f) * this.ticksAlive / this.timeToFullscale);
				}
				if (this.rand.nextFloat() <= 0.2f) {
					//this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1, this.rand.nextFloat() + 0.5f);
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:flamethrow"))), 
					 1.0f, this.rand.nextFloat() * 0.5f + 0.6f);
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 0.5f) {
					this.createJutsu(entity, entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power);
					//if (entity instanceof EntityPlayer)
					//	ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, (long)(power * 80));
					return true;
				}
				return false;
			}

			public void createJutsu(EntityLivingBase entity, double x, double y, double z, float power) {
				EntityBigFireball entityarrow = new EntityBigFireball(entity, power);
				entityarrow.shoot(x, y, z, 0.95f, 0);
				entity.world.spawnEntity(entityarrow);
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
				GlStateManager.rotate(12f * (partialTicks + entity.ticksExisted), 0.0F, 0.0F, 1.0F);
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
				bufferbuilder.pos(-0.375D, -0.375D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.375D, -0.375D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.375D, 0.375D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.375D, 0.375D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				tessellator.draw();
				GlStateManager.enableLighting();
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
