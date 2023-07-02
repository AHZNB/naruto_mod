
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;

import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureYomotsuHirasaka;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityTruthSeekerBall;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.ImmutableMap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKekkeiMora extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:kekkei_mora")
	public static final Item block = null;
	public static final int ENTITYID = 368;
	public static final ItemJutsu.JutsuEnum EIGHTYGODS = new ItemJutsu.JutsuEnum(0, "entity80gods", 'S', 10d, new Entity80Gods.Jutsu());
	public static final ItemJutsu.JutsuEnum PORTAL = new ItemJutsu.JutsuEnum(1, "tooltip.byakurinnesharingan.jutsu2", 'S', 10d, new YomotsuHirasaka());
	public static final ItemJutsu.JutsuEnum BIGBALL = new ItemJutsu.JutsuEnum(2, "tooltip.kekkeimora.expansivetsb", 'S', 10d, new ExpansiveTSB());
	public static final ItemJutsu.JutsuEnum ASHBONE = new ItemJutsu.JutsuEnum(3, "item.ashbones.name", 'S', 10d, new AshBone());
	public static final ItemJutsu.JutsuEnum PULSE = new ItemJutsu.JutsuEnum(4, "tooltip.byakurinnesharingan.pulse", 'S', 10d, new ChakraPulse());

	public ItemKekkeiMora(ElementsNarutomodMod instance) {
		super(instance, 731);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(EIGHTYGODS, PORTAL, BIGBALL, ASHBONE, PULSE));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(Entity80Gods.class)
				.id(new ResourceLocation("narutomod", "entity80gods"), ENTITYID).name("entity80gods").tracker(64, 1, true)
				.build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kekkei_mora", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(Entity80Gods.class, renderManager -> {
			return new Render80Gods(renderManager);
		});
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.KEKKEIMORA, list);
			this.setUnlocalizedName("kekkei_mora");
			this.setRegistryName("kekkei_mora");
			this.setCreativeTab(TabModTab.tab);
			for (int i = 0; i < list.length; i++) {
				this.defaultCooldownMap[i] = 0;
			}
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == PULSE) {
				return this.getPower(stack, entity, timeLeft, 10f, 20f);
			}
			return 1f;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
			if (this.getCurrentJutsu(stack) == EIGHTYGODS) {
				if (!player.world.isRemote && player.ticksExisted % 4 == 1) {
					this.executeJutsu(stack, player, 1.0f);
				}
				return;
			}
			super.onUsingTick(stack, player, count);
		}
	}

	public static class YomotsuHirasaka implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			ProcedureYomotsuHirasaka.executeProcedure(ImmutableMap.of("world", entity.world, "entity", entity));
			return true;
		}
	}

	public static class ExpansiveTSB implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			EntityTruthSeekerBall.EntityCustom etsb = new EntityTruthSeekerBall.EntityCustom(entity, 9, stack);
			entity.world.spawnEntity(etsb);
			etsb.setMaxScale(25f);
			((RangedItem)stack.getItem()).setCurrentJutsuCooldown(stack, 1200);
			return true;
		}
	}

	public static class AshBone implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (entity.getHeldItemMainhand().getItem() != ItemAshBones.block) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
				 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:bonecrack")),
				 net.minecraft.util.SoundCategory.PLAYERS, 0.5f, 1f);
				ItemStack itemstack = new ItemStack(ItemAshBones.block);
				if (entity instanceof EntityPlayer) {
					ProcedureUtils.swapItemToSlot((EntityPlayer)entity, EntityEquipmentSlot.MAINHAND, itemstack);
				} else {
					entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemstack);
				}
				return true;
			}
			return false;
		}
	}

	public static class ChakraPulse implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
			 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:dojutsu")),
			 net.minecraft.util.SoundCategory.NEUTRAL, 1f, 1f);
			for (int i = 0; i < 1000; i++) {
				Particles.spawnParticle(entity.world, Particles.Types.SMOKE, entity.posX, entity.posY + 1.4d, entity.posZ,
				 1, 1d, 0d, 1d, entity.getRNG().nextGaussian(), 1d, entity.getRNG().nextGaussian(), 0x10FFFFFF, 30, 0);
			}
			ProcedureAoeCommand.set(entity, 0d, power / 2).exclude(entity).knockback(3f);
			ProcedureUtils.purgeHarmfulEffects(entity);
			entity.extinguish();
			return true;
		}
	}

	public static class Entity80Gods extends EntityScalableProjectile.Base {
		public final float explosionStrength = 6.0F;

		public Entity80Gods(World a) {
			super(a);
			this.setOGSize(0.5F, 0.25F);
			this.setEntityScale(2.0f);
		}

		public Entity80Gods(EntityLivingBase shooter) {
			super(shooter);
			this.setOGSize(0.5F, 0.25F);
			this.setEntityScale(2.0f);
			Vec3d vec = shooter.getLookVec().scale(1.8d)
			 .rotateYaw((this.rand.nextFloat()-0.5f) * 90f * 0.01745329f)
			 .rotatePitch((this.rand.nextFloat()-0.5f) * 60f * 0.01745329f)
			 .addVector(shooter.posX, shooter.posY+1.2d, shooter.posZ);
			this.setLocationAndAngles(vec.x, vec.y, vec.z, shooter.rotationYawHead, shooter.rotationPitch);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.setEntityScale(this.getEntityScale() + 1.0f);
			if (!this.world.isRemote && this.ticksAlive > 10) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote) {
				if (result.entityHit != null) {
					if (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof Entity80Gods) {
						return;
					}
					if (result.entityHit instanceof EntityLivingBase) {
						//result.entityHit.onKillCommand();
						result.entityHit.hurtResistantTime = 10;
						result.entityHit.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.shootingEntity), 500f);
					}
				}
				this.setDead();
				this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, this.explosionStrength, false,
				 net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity));
			}
		}

		@Override
		public void renderParticles() {
			/*for (int i = 0; i < 100; i++) {
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY + this.height * 0.5F, this.posZ, 
				 1, 0.0F, this.height * 0.5F, 0.0F, (this.rand.nextDouble() - 0.5D) * 1.5D,
				 (this.rand.nextDouble() - 0.5F) * 1.5D, (this.rand.nextDouble() - 0.5F) * 1.5D, 0x20FFFFFF, 30 + this.rand.nextInt(20), 0);
			}*/
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public boolean isImmuneToExplosions() {
			return true;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
				 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:throwpunch")),
				 net.minecraft.util.SoundCategory.NEUTRAL, 1.0F, entity.getRNG().nextFloat() * 0.6f + 0.6f);
				Vec3d vec = entity.getLookVec();
				Entity80Gods entity1 = new Entity80Gods(entity);
				entity.world.spawnEntity(entity1);
				entity1.shoot(vec.x, vec.y, vec.z, 1.25f, 0.1f);
				return true;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class Render80Gods extends Render<Entity80Gods> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/armfist.png");
		private final ModelArmFist mainModel = new ModelArmFist();

		public Render80Gods(RenderManager renderManager) {
			super(renderManager);
			this.shadowSize = 0.1F;
		}

		@Override
		public void doRender(Entity80Gods entity, double x, double y, double z, float entityYaw, float pt) {
			this.bindEntityTexture(entity);
			GlStateManager.pushMatrix();
			GlStateManager.disableCull();
			float scale = entity.getEntityScale();
			GlStateManager.translate((float) x, (float) y, (float) z);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(-entity.prevRotationYaw - MathHelper.wrapDegrees(entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt - 180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			this.mainModel.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(Entity80Gods entity) {
			return texture;
		}
	}

	@SideOnly(Side.CLIENT)
	public class ModelArmFist extends ModelBase {
		private final ModelRenderer bb_main;
	
		public ModelArmFist() {
			textureWidth = 32;
			textureHeight = 32;
	
			bb_main = new ModelRenderer(this);
			bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
			bb_main.cubeList.add(new ModelBox(bb_main, 0, 12, -2.0F, -4.0F, -1.0F, 4, 4, 8, 0.0F, false));
			bb_main.cubeList.add(new ModelBox(bb_main, 0, 12, -2.0F, -4.0F, -2.0F, 4, 4, 8, 0.1F, false));
			bb_main.cubeList.add(new ModelBox(bb_main, 0, 12, -2.0F, -4.0F, -3.0F, 4, 4, 8, 0.2F, false));
			bb_main.cubeList.add(new ModelBox(bb_main, 0, 0, -2.0F, -4.0F, -4.0F, 4, 4, 8, 0.3F, false));
		}
	
		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			bb_main.render(f5);
		}
	
		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}

