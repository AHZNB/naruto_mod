
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityTenseiBakuSilver;
import net.narutomod.entity.EntityTenseiBakuGold;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.potion.PotionFlight;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemTenseiganChakraMode extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:tenseigan_chakra_mode")
	public static final Item block = null;
	public static final int ENTITYID = 339;
	public static final ItemJutsu.JutsuEnum CHAKRAORBS = new ItemJutsu.JutsuEnum(0, "tenseigangun", 'S', 10d, new EntityOrbs.Jutsu());
	public static final ItemJutsu.JutsuEnum SILVERBLAST = new ItemJutsu.JutsuEnum(1, "tensei_baku_silver", 'S', 50d, new EntityTenseiBakuSilver.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum GOLDBLAST = new ItemJutsu.JutsuEnum(2, "tensei_baku_gold", 'S', 50d, new EntityTenseiBakuGold.EC.Jutsu());

	public ItemTenseiganChakraMode(ElementsNarutomodMod instance) {
		super(instance, 695);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(CHAKRAORBS, SILVERBLAST, GOLDBLAST));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityOrbs.class)
				.id(new ResourceLocation("narutomod", "tenseigangun"), ENTITYID).name("tenseigangun")
				.tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:tenseigan_chakra_mode", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.TENSEIGAN, list);
			this.setUnlocalizedName("tenseigan_chakra_mode");
			this.setRegistryName("tenseigan_chakra_mode");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[CHAKRAORBS.index] = 0;
			this.defaultCooldownMap[SILVERBLAST.index] = 0;
			this.defaultCooldownMap[GOLDBLAST.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return jutsu == SILVERBLAST ? this.getPower(stack, entity, timeLeft, 10.0f, 20.0f)
			     : jutsu == GOLDBLAST ? this.getPower(stack, entity, timeLeft, 10.0f, 5.0f)
			     : 1.0F;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float f = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return jutsu == GOLDBLAST ? Math.min(f, 200.0f)
			     : jutsu == SILVERBLAST ? Math.min(f, 60.0f)
			     : f;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemTenseigan.helmet
					? super.onItemRightClick(world, entity, hand)
					: new ActionResult<ItemStack>(EnumActionResult.FAIL, entity.getHeldItem(hand));
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityPlayer) {
				EntityPlayer livingEntity = (EntityPlayer) entity;
				if (!livingEntity.isCreative() && !livingEntity.getCooldownTracker().hasCooldown(block)) {
					ItemStack eyestack = ProcedureUtils.getMatchingItemStack(livingEntity, ItemTenseigan.helmet);
					ItemStack stack1 = livingEntity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
					ItemStack stack2 = livingEntity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
					if (eyestack == null) {
						if (stack1.getItem() == ItemTenseigan.body) {
							stack1.shrink(1);
						}
						if (stack2.getItem() == ItemTenseigan.legs) {
							stack2.shrink(1);
						}
						itemstack.shrink(1);
					} else if (livingEntity.getHeldItemMainhand().equals(itemstack)) {
						livingEntity.addPotionEffect(new PotionEffect(PotionFlight.potion, 2, 0, false, false));
						if (!livingEntity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).equals(eyestack)) {
							ProcedureUtils.swapItemToSlot(livingEntity, EntityEquipmentSlot.HEAD, eyestack);
						}
						if (stack1.getItem() != ItemTenseigan.body) {
							ItemStack stack3 = ProcedureUtils.getMatchingItemStack(livingEntity, ItemTenseigan.body);
							if (stack3 == null) {
								for (int i = 0; i < 1000; i++) {
									Particles.spawnParticle(world, Particles.Types.SMOKE, entity.posX, entity.posY + 0.8d, entity.posZ,
											1, 0.0d, 0.0d, 0.0d, (this.itemRand.nextDouble() - 0.5d) * 1.0d, (this.itemRand.nextDouble() - 0.5d) * 1.0d,
											(this.itemRand.nextDouble() - 0.5d) * 1.0d, 0x20b5fff5, 30, 0, 0xF0, entity.getEntityId());
								}
								entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
										SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:charging_chakra")),
										net.minecraft.util.SoundCategory.PLAYERS, 1.0F, 1.0F);
								stack3 = new ItemStack(ItemTenseigan.body);
								stack3.setItemDamage(itemstack.getTagCompound().getInteger("ChestArmorDamage"));
							}
							ProcedureUtils.swapItemToSlot(livingEntity, EntityEquipmentSlot.CHEST, stack3);
						} else if (stack1.getItemDamage() != itemstack.getTagCompound().getInteger("ChestArmorDamage")) {
							itemstack.getTagCompound().setInteger("ChestArmorDamage", stack1.getItemDamage());
						}
						if (stack2.getItem() != ItemTenseigan.legs) {
							ItemStack stack3 = ProcedureUtils.getMatchingItemStack(livingEntity, ItemTenseigan.legs);
							if (stack3 == null) {
								stack3 = new ItemStack(ItemTenseigan.legs);
								stack3.setItemDamage(itemstack.getTagCompound().getInteger("LegArmorDamage"));
							}
							ProcedureUtils.swapItemToSlot(livingEntity, EntityEquipmentSlot.LEGS, stack3);
						} else if (stack2.getItemDamage() != itemstack.getTagCompound().getInteger("LegArmorDamage")) {
							itemstack.getTagCompound().setInteger("LegArmorDamage", stack2.getItemDamage());
						}
						if (stack1.getItem() == ItemTenseigan.body && stack2.getItem() == ItemTenseigan.legs
								&& (stack1.getItemDamage() >= stack1.getMaxDamage() || stack2.getItemDamage() >= stack2.getMaxDamage())) {
							livingEntity.getCooldownTracker().setCooldown(block, 3600);
							stack1.shrink(1);
							stack2.shrink(1);
							itemstack.getTagCompound().setInteger("ChestArmorDamage", 0);
							itemstack.getTagCompound().setInteger("LegArmorDamage", 0);
						}
					}
				}
			}
		}

		public boolean isOnCooldown(EntityLivingBase entity) {
			return entity instanceof EntityPlayer && ((EntityPlayer) entity).getCooldownTracker().hasCooldown(block);
		}
	}

	public static class EntityOrbs extends EntityScalableProjectile.Base {
		private final int explosionSize = 5;
		private final float damage = 30.0F;

		public EntityOrbs(World a) {
			super(a);
			this.setOGSize(1.0F, 1.0F);
			this.setEntityScale(0.5F);
		}

		public EntityOrbs(EntityLivingBase shooter) {
			super(shooter);
			this.setOGSize(1.0F, 1.0F);
			this.setEntityScale(0.5F);
			Vec3d vec3d = shooter.getLookVec().rotateYaw((this.rand.nextFloat() - 0.5F) * 60.0F);
			this.setPosition(shooter.posX + vec3d.x, shooter.posY + 1.2D + vec3d.y, shooter.posZ + vec3d.z);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote) {
				//if (this.shootingEntity != null) {
				//	this.shootingEntity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 40d);
				//}
				if (result.entityHit != null) {
					if (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntityOrbs) {
						return;
					}
					if (result.entityHit instanceof EntityLivingBase) {
						Chakra.pathway((EntityLivingBase) result.entityHit).consume(1.0F);
					}
				}
				boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
				this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, this.explosionSize, false, flag);
				ProcedureAoeCommand.set(this, 0.0D, 3.0D).exclude(this.shootingEntity)
						.damageEntities(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity), this.damage);
				this.setDead();
			}
		}

		@Override
		protected RayTraceResult forwardsRaycast(boolean includeEntities, boolean ignoreExcludedEntity, @Nullable Entity excludedEntity) {
			RayTraceResult res = ProjectileHelper.forwardsRaycast(this, includeEntities, ignoreExcludedEntity, excludedEntity);
			return res != null && res.entityHit instanceof EntityOrbs && ((EntityOrbs) res.entityHit).shootingEntity != null
					&& ((EntityOrbs) res.entityHit).shootingEntity.equals(this.shootingEntity) ? null : res;
		}

		@Override
		public void renderParticles() {
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted == 5) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:throwpunch")),
						0.1F, this.rand.nextFloat() * 0.6f + 0.5f);
			}
			if (this.ticksInAir > 100) {
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
				EntityOrbs entityarrow = new EntityOrbs(entity);
				entityarrow.shoot(x, y, z, 0.95f, 0);
				entity.world.spawnEntity(entityarrow);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityOrbs.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityOrbs> {
			private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/white_orb.png");
			private final float red = 0.592F;
			private final float green = 0.984F;
			private final float blue = 0.91F;

			public RenderCustom(RenderManager renderManager) {
				super(renderManager);
				this.shadowSize = 0.1f;
			}

			@Override
			public void doRender(EntityOrbs entity, double x, double y, double z, float entityYaw, float partialTicks) {
				float f = Math.min(((float) entity.ticksExisted + partialTicks) / 10.0F, 1.0F);
				f *= f;
				float r = this.red * f;
				float g = this.green * f;
				float b = this.blue * f;
				GlStateManager.pushMatrix();
				this.bindEntityTexture(entity);
				float scale = entity.getEntityScale();
				GlStateManager.translate(x, y + 0.5d * scale, z);
				GlStateManager.enableRescaleNormal();
				GlStateManager.scale(scale, scale, scale);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				GlStateManager.rotate(180F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
				//GlStateManager.rotate(9f * entity.ticksExisted, 0.0F, 0.0F, 1.0F);
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
				if (f > 0.5F) {
					bufferbuilder.pos(-0.6D, -0.6D, 0.0D).tex(0.0D, 1.0D).color(r, g, b, 0.25F).normal(0.0F, 1.0F, 0.0F).endVertex();
					bufferbuilder.pos(0.6D, -0.6D, 0.0D).tex(1.0D, 1.0D).color(r, g, b, 0.25F).normal(0.0F, 1.0F, 0.0F).endVertex();
					bufferbuilder.pos(0.6D, 0.6D, 0.0D).tex(1.0D, 0.0D).color(r, g, b, 0.25F).normal(0.0F, 1.0F, 0.0F).endVertex();
					bufferbuilder.pos(-0.6D, 0.6D, 0.0D).tex(0.0D, 0.0D).color(r, g, b, 0.25F).normal(0.0F, 1.0F, 0.0F).endVertex();
				}
				bufferbuilder.pos(-0.5D, -0.5D, 0.0D).tex(0.0D, 1.0D).color(r, g, b, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.5D, -0.5D, 0.0D).tex(1.0D, 1.0D).color(r, g, b, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(0.5D, 0.5D, 0.0D).tex(1.0D, 0.0D).color(r, g, b, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos(-0.5D, 0.5D, 0.0D).tex(0.0D, 0.0D).color(r, g, b, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
				tessellator.draw();
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.disableRescaleNormal();
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityOrbs entity) {
				return TEXTURE;
			}
		}
	}
}
