
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.SoundEvents;

import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityMeltingJutsu;
import net.narutomod.entity.EntityLavaChakraMode;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemYooton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:yooton")
	public static final Item block = null;
	public static final int ENTITYID = 270;
	public static final ItemJutsu.JutsuEnum ROCKS = new ItemJutsu.JutsuEnum(0, "magmaball", 'S', 200, 40d, new EntityMagmaBall.Jutsu());
	public static final ItemJutsu.JutsuEnum STREAM = new ItemJutsu.JutsuEnum(1, "melting_jutsu", 'S', 200, 50d, new EntityMeltingJutsu.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum CHAKRAMODE = new ItemJutsu.JutsuEnum(2, "lava_chakra_mode", 'S', 250, 10d, new EntityLavaChakraMode.EC.Jutsu());

	public ItemYooton(ElementsNarutomodMod instance) {
		super(instance, 592);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(ROCKS, STREAM, CHAKRAMODE));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityMagmaBall.class)
				.id(new ResourceLocation("narutomod", "magmaball"), ENTITYID).name("magmaball").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:yooton", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityMagmaBall.class, renderManager -> {
			return new RenderCustom(renderManager);
		});
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RangedItem.DamageHook());
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.YOOTON, list);
			this.setUnlocalizedName("yooton");
			this.setRegistryName("yooton");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[ROCKS.index] = 0;
			this.defaultCooldownMap[STREAM.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == ROCKS) {
				return this.getPower(stack, entity, timeLeft, 1.0f, 50f);
			} else if (jutsu == STREAM) {
				return this.getPower(stack, entity, timeLeft, 0.5f, 200f);
			}
			return 1f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float mp = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == ROCKS) {
				return Math.min(mp, 20f);
			} else if (jutsu == STREAM) {
				return Math.min(mp, 10f);
			}
			return mp;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityLivingBase) {
				((EntityLivingBase)entity).extinguish();
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			if (entity.isCreative() || (ProcedureUtils.hasItemInInventory(entity, ItemDoton.block) 
			 && ProcedureUtils.hasItemInInventory(entity, ItemKaton.block))) {
				return super.onItemRightClick(world, entity, hand);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, entity.getHeldItem(hand));
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(TextFormatting.GREEN + net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.yooton.musthave") + TextFormatting.RESET);
		}

		public static class DamageHook {
			@SubscribeEvent
			public void onDamage(LivingAttackEvent event) {
				EntityLivingBase entity = event.getEntityLiving();
				DamageSource source = event.getSource();
				if ((source == DamageSource.LAVA || source == DamageSource.IN_FIRE || source == DamageSource.HOT_FLOOR)
				 && entity instanceof EntityPlayer && ProcedureUtils.hasItemInInventory((EntityPlayer)entity, block)) {
					event.setCanceled(true);
				}
			}
		}
	}

	public static class EntityMagmaBall extends EntityScalableProjectile.Base {
		private int explosionSize;
		private float damage;

		public EntityMagmaBall(World a) {
			super(a);
			this.setOGSize(1.0F, 1.0F);
		}

		public EntityMagmaBall(EntityLivingBase shooter, float scale) {
			super(shooter);
			this.setOGSize(1.0F, 1.0F);
			scale *= 1.2F;
			this.setEntityScale(scale);
			this.explosionSize = Math.max((int)scale - 1, 0);
			this.damage = scale * 20f;
			Vec3d vec3d = shooter.getLookVec();
			this.setPosition(shooter.posX + vec3d.x, shooter.posY + 1.2D + vec3d.y, shooter.posZ + vec3d.z);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.typeOfHit == RayTraceResult.Type.BLOCK && this.getEntityScale() >= 2.0f && this.ticksInAir <= 15) {
				return;
			}
			if (!this.world.isRemote) {
				if (this.shootingEntity != null) {
					this.shootingEntity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 40d);
				}
				if (result.entityHit != null) {
					if (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntityMagmaBall)
						return;
					result.entityHit.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity).setFireDamage(), this.damage);
					result.entityHit.setFire(10);
				}
				boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
				this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, this.explosionSize, flag, flag);
				this.setDead();
			}
		}

		@Override
		public void renderParticles() {
			float scale = this.getEntityScale();
			Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY + this.height / 2.0F, this.posZ,
			 (int)(scale * 10), 0.3d * this.width, 0.3d * this.height, 0.3d * this.width, 0d, 0d, 0d, 
			 0x80f50000|(this.rand.nextInt(0x60)<<8), 10 + (int)(scale * 10), (int)(4.0D / (this.rand.nextDouble() * 0.8D + 0.2D)), 0xF0);
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.rand.nextFloat() <= 0.2f) {
				this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1, this.rand.nextFloat() + 0.5f);
			}
			if (this.ticksInAir > 100 || this.isInWater()) {
				this.setDead();
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				this.createJutsu(entity, entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power);
				return true;
			}

			public void createJutsu(EntityLivingBase entity, double x, double y, double z, float power) {
				EntityMagmaBall entityarrow = new EntityMagmaBall(entity, power);
				entityarrow.shoot(x, y, z, 1.05f, 0);
				entity.world.spawnEntity(entityarrow);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EntityMagmaBall> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/magmaball.png");

		public RenderCustom(RenderManager renderManager) {
			super(renderManager);
			this.shadowSize = 0.1f;
		}

		@Override
		public void doRender(EntityMagmaBall entity, double x, double y, double z, float entityYaw, float partialTicks) {
			this.bindEntityTexture(entity);
			GlStateManager.pushMatrix();
			float scale = entity.getEntityScale();
			GlStateManager.translate(x, y + 0.5d * scale, z);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(scale, scale, scale);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.rotate(180F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(9f * entity.ticksExisted, 0.0F, 0.0F, 1.0F);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
			bufferbuilder.pos(-0.5D, -0.5D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, -0.5D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, 0.5D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(-0.5D, 0.5D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityMagmaBall entity) {
			return this.texture;
		}
	}
}
