
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.init.MobEffects;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.entity.EntityBeamBase;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAoeCommand;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemJinton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:jinton")
	public static final Item block = null;
	public static final int ENTITYID = 124;
	public static final int ENTITY2ID = 10124;
	private static final int MIN_PLAYER_XP = 70;
	public static final ItemJutsu.JutsuEnum BEAM = new ItemJutsu.JutsuEnum(0, "jintonbeam", 'S', MIN_PLAYER_XP*10, 500d, new EntityBeam.Jutsu());
	public static final ItemJutsu.JutsuEnum CUBE = new ItemJutsu.JutsuEnum(1, "jintoncube", 'S', MIN_PLAYER_XP*10, 600d, new EntityCube.Jutsu());

	public ItemJinton(ElementsNarutomodMod instance) {
		super(instance, 367);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(BEAM, CUBE));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityBeam.class)
			.id(new ResourceLocation("narutomod", "jintonbeam"), ENTITYID).name("jintonbeam").tracker(64, 1, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCube.class)
			.id(new ResourceLocation("narutomod", "jintoncube"), ENTITY2ID).name("jintoncube").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:jinton", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityBeam.class, renderManager -> {
			return new RenderBeam(renderManager);
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityCube.class, renderManager -> {
			return new RenderCube(renderManager);
		});
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.JINTON, list);
			this.setUnlocalizedName("jinton");
			this.setRegistryName("jinton");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[BEAM.index] = 0;
			this.defaultCooldownMap[CUBE.index] = 0;
		}

		private float getMaxUsablePower(EntityLivingBase entity, ItemStack stack) {
			float max = entity instanceof EntityPlayer ? (float)(PlayerTracker.getNinjaLevel((EntityPlayer)entity)-MIN_PLAYER_XP+5)/5 : 6;
			return MathHelper.clamp(max, 0f, this.getCurrentJutsu(stack) == BEAM ? 10f : 50f);
		}

		private float getUsePercent(int timeLeft) {
			return Math.min((float)(this.getMaxUseDuration() - timeLeft) / 400f, 1.0f);
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			return Math.min(this.getUsePercent(timeLeft) * this.getMaxUsablePower(entity, stack), this.getMaxPower(stack, entity));
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
			if (!world.isRemote) {
				float power = this.getPower(itemstack, entity, timeLeft);
				if (power >= 1f && this.executeJutsu(itemstack, entity, power)
				 && entity instanceof EntityPlayer && !((EntityPlayer)entity).isCreative()) {
					((EntityPlayer)entity).getCooldownTracker().setCooldown(itemstack.getItem(), 
					 (int)(this.getUsePercent(timeLeft) * 12000 * ProcedureUtils.getCooldownModifier(((EntityPlayer)entity))));
				}
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			if (entity.isCreative() || (ProcedureUtils.hasItemInInventory(entity, ItemKaton.block) 
			 && ProcedureUtils.hasItemInInventory(entity, ItemFuton.block)
			 && ProcedureUtils.hasItemInInventory(entity, ItemDoton.block))) {
				return super.onItemRightClick(world, entity, hand);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, entity.getHeldItem(hand));
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(TextFormatting.GREEN + net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.jinton.musthave") + TextFormatting.RESET);
		}
	}

	public static class EntityBeam extends EntityBeamBase.Base {
		private static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(EntityBeam.class, DataSerializers.FLOAT);
		private final AirPunch beam = new AirPunch();
		private final int wait = 60;

		public EntityBeam(World a) {
			super(a);
			this.isImmuneToFire = true;
		}

		public EntityBeam(EntityLivingBase shooter, float scale) {
			super(shooter);
			this.setScale(scale);
			this.isImmuneToFire = true;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SCALE, Float.valueOf(1f));
		}

		public float getScale() {
			return ((Float) this.getDataManager().get(SCALE)).floatValue();
		}

		protected void setScale(float scale) {
			this.getDataManager().set(SCALE, Float.valueOf(scale));
		}

		@Override
		protected void updatePosition() {
			if (this.shootingEntity != null) {
				this.setPosition(this.shootingEntity.posX, this.shootingEntity.posY + this.shootingEntity.getEyeHeight() - 0.2D, 
				  this.shootingEntity.posZ);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null) {
				if (this.ticksAlive < this.wait) {
					Vec3d vec3d = this.shootingEntity.getLookVec();
					this.shoot(vec3d.x, vec3d.y, vec3d.z);
				}
				if (this.ticksAlive >= this.wait) {
					Vec3d vec3d = this.shootingEntity.getLookVec().scale(30d);
					this.shoot(vec3d.x, vec3d.y, vec3d.z);
				}
				if (this.ticksAlive >= this.wait + 2) {
					this.beam.execute2((EntityLivingBase)this.shootingEntity, (double)this.getBeamLength(), (double)this.getScale() / 2);
				}
			}
			if (this.ticksAlive > this.wait + 60)
				this.world.removeEntity(this);
		}

		public class AirPunch extends ProcedureAirPunch {
			public AirPunch() {
				this.blockDropChance = -1.0F;
				this.blockHardnessLimit = 100f;
				//this.particlesPre = EnumParticleTypes.EXPLOSION_NORMAL;
				this.particlesPre = null;
				this.particlesDuring = EnumParticleTypes.SMOKE_LARGE;
			}
			
			@Override
			protected void attackEntityFrom(EntityLivingBase player, Entity target) {
				double d = this.getFarRadius(0) / target.getEntityBoundingBox().getAverageEdgeLength();
				float f = target instanceof EntityLivingBase ? ((EntityLivingBase)target).getMaxHealth() * (float)d : Float.MAX_VALUE;
				attackEntityWithJutsu(EntityBeam.this, player, target, f);
			}

			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				return 1.0F;
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (entity instanceof EntityPlayer) {
					power = Math.min(power / 2 + 0.5f, 10f);
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
					  .getObject(new ResourceLocation("narutomod:genkaihakurinojutsu")), SoundCategory.PLAYERS, 1, 1f);
					Vec3d vec3d = entity.getLookVec();
					EntityBeam entitybeam = new EntityBeam(entity, power);
					entitybeam.shoot(vec3d.x, vec3d.y, vec3d.z);
					entity.world.spawnEntity(entitybeam);
					return true;
				}
				return false;
			}
		}
	}

	private static void attackEntityWithJutsu(Entity projectile, EntityLivingBase attacker, Entity target, float amount) {
		if (target instanceof EntityLivingBase) {
			target.hurtResistantTime = 10;
			target.attackEntityFrom(ItemJutsu.causeJutsuDamage(projectile, attacker)
			  .setDamageBypassesArmor().setDamageIsAbsolute(), amount);
		} else {
			target.onKillCommand();
		}
	}

	public static class EntityCube extends EntityScalableProjectile.Base {
		private final int wait = 60;
		private final int growTime = 30;
		private final int idleTime = 40;
		private final int shrinkTime = 10;
		private float fullScale = 1f;
		
		public EntityCube(World a) {
			super(a);
			this.setOGSize(0.5F, 0.5F);
			this.isImmuneToFire = true;
		}

		public EntityCube(EntityLivingBase shooter, float scale) {
			super(shooter);
			this.setOGSize(0.5F, 0.5F);
			this.fullScale = scale;
			this.setWaitPosition(shooter);
			this.isImmuneToFire = true;
		}

		private void setWaitPosition(EntityLivingBase shooter) {
			Vec3d vec3d = shooter.getLookVec().scale(0.5);
			this.setPosition(shooter.posX + vec3d.x, shooter.posY + shooter.getEyeHeight() - 0.6d + vec3d.y, shooter.posZ + vec3d.z);
			this.setEntityScale(0.5f);
		}

		public void shoot(RayTraceResult result) {
			double d0 = result.entityHit != null ? result.entityHit.posX : result.hitVec.x;
			double d1 = result.entityHit != null ? result.entityHit.posY + result.entityHit.height/2 : result.hitVec.y;
			double d2 = result.entityHit != null ? result.entityHit.posZ : result.hitVec.z;
			this.setPosition(d0, d1, d2);
			ProcedureAoeCommand.set(this.world, d0, d1, d2, 0d, this.fullScale/4).effect(MobEffects.SLOWNESS, 5, 5);
		}

		private void destroyBlocksAndEntitiesInAABB(AxisAlignedBB bb) {
			if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity)) {
		        int j2 = MathHelper.floor(bb.minX);
		        int k2 = MathHelper.ceil(bb.maxX);
		        int l2 = MathHelper.floor(bb.minY);
		        int i3 = MathHelper.ceil(bb.maxY);
		        int j3 = MathHelper.floor(bb.minZ);
		        int k3 = MathHelper.ceil(bb.maxZ);
		        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
		        for (int l3 = j2; l3 < k2; ++l3) {
		            for (int i4 = l2; i4 < i3; ++i4) {
		                for (int j4 = j3; j4 < k3; ++j4) {
							if (!this.world.isAirBlock(pos.setPos(l3, i4, j4))) {
								this.world.setBlockToAir(pos);
							}
		                }
		            }
		        }
		        pos.release();
			}
			for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, bb)) {
				double d = ProcedureUtils.BB.getVolume(bb.intersect(entity.getEntityBoundingBox()))
				 / ProcedureUtils.BB.getVolume(entity.getEntityBoundingBox()) * 0.5D;
				attackEntityWithJutsu(this, this.shootingEntity, entity, 
				 entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getMaxHealth() * (float)d : Float.MAX_VALUE);
			}
		}

		@Override
		public void setEntityScale(float scale) {
			double d = (this.height * scale / this.getEntityScale() - this.height) / 2;
			super.setEntityScale(scale);
			this.setPosition(this.posX, this.posY - d, this.posZ);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			int idle = this.wait + this.growTime + this.idleTime;
			if (this.shootingEntity != null) {
				if (this.ticksAlive < this.wait) {
					this.setWaitPosition(this.shootingEntity);
				} else if (this.ticksAlive == this.wait) {
					this.shoot(ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d, true));
				} else {
					ProcedureAoeCommand.set(this, 0d, this.width/2).motion(0d, 0d, 0d);
					if (this.ticksAlive < this.wait + this.growTime) {
						this.setEntityScale(1.0F + (this.fullScale - 1f) * (this.ticksAlive - this.wait) / (float) this.growTime);
					} else {
						Particles.spawnParticle(this.world, Particles.Types.FALLING_DUST, this.posX, this.posY + (this.height / 2.0F), this.posZ, 
						  (int)(this.fullScale * 6), this.width * 0.2F, this.height * 0.2F, this.width * 0.2F, 0D, 0.1D, 0D, 0xC0A0A0A0, 15, 0);
						if (this.ticksAlive > idle) {
							this.destroyBlocksAndEntitiesInAABB(this.getEntityBoundingBox());
							//this.setEntityScale(this.fullScale * (float)(idle + this.shrinkTime - this.ticksAlive) / (float)this.shrinkTime);
							this.setEntityScale(this.fullScale * (1f - (float)(this.ticksAlive - idle) / this.shrinkTime));
						}
					}
				}
			}
			if (this.ticksAlive > idle + this.shrinkTime || (this.shootingEntity != null && !this.shootingEntity.isEntityAlive())) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void renderParticles() {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				power = power * 2 + 2;
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
				  .getObject(new ResourceLocation("narutomod:genkaihakurinojutsu")), SoundCategory.PLAYERS, 1, 1f);
				entity.world.spawnEntity(new EntityCube(entity, power));
				return true;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderBeam extends EntityBeamBase.Renderer<EntityBeam> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/longcube_white.png");
		
		public RenderBeam(RenderManager renderManager) {
			super(renderManager);
		}

		@Override
		public EntityBeamBase.Model getMainModel(EntityBeam entity) {
			int i = entity.ticksAlive - entity.wait;
			if (i > 0) {
				float length = MathHelper.clamp(entity.getBeamLength() * (float)i / 10f, 1f, entity.getBeamLength());
				float scale = entity.getScale() * 2 * length / entity.getBeamLength();
				ModelLongCube model = new ModelLongCube(length / scale);
				model.scale = scale;
				return model;
			} else {
				return new ModelLongCube(1);
			}
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityBeam entity) {
			return texture;
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCube extends Render<EntityCube> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/longcube_white.png");
		private final ModelCube model = new ModelCube();

		public RenderCube(RenderManager renderManager) {
			super(renderManager);
			this.shadowSize = 0.1F;
		}

		@Override
		public void doRender(EntityCube entity, double x, double y, double z, float yaw, float pt) {
			this.bindEntityTexture(entity);
			GlStateManager.pushMatrix();
			float scale = entity.getEntityScale();
			GlStateManager.translate((float) x, (float) y, (float) z);
			GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt - 180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
			this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityCube entity) {
			return TEXTURE;
		}
	}

	// Made with Blockbench 3.5.4
	// Exported for Minecraft version 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelLongCube extends EntityBeamBase.Model {
		private final ModelRenderer bone;
		private final ModelRenderer bone2;
		protected float scale = 1.0F;

		public ModelLongCube(float length) {
			this.textureWidth = 32;
			this.textureHeight = 32;
			int len = (int)(16f * length);
			this.bone = new ModelRenderer(this);
			this.bone.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -0.5F, -16.0F, -0.5F, 1, len, 1, 0.0F, false));
			this.bone2 = new ModelRenderer(this);
			this.bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.bone2.cubeList.add(new ModelBox(this.bone2, 0, 0, -1.0F, -16.0F, -1.0F, 2, len, 2, 0.0F, false));
			this.bone2.cubeList.add(new ModelBox(this.bone2, 0, 0, -1.5F, -16.0F, -1.5F, 3, len, 3, 0.0F, false));
			this.bone2.cubeList.add(new ModelBox(this.bone2, 0, 0, -2.0F, -16.0F, -2.0F, 4, len, 4, 0.0F, false));
			this.bone2.cubeList.add(new ModelBox(this.bone2, 0, 0, -4.0F, -16.0F, -4.0F, 8, len, 8, 0.0F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, (this.scale - 1.0F) * 1.5F + 1F, 0.0F);
			GlStateManager.scale(this.scale, this.scale, this.scale);
			GlStateManager.color(1f, 1f, 1f, 1f);
			this.bone.render(f5);
			GlStateManager.color(1f, 1f, 1f, 0.3f);
			this.bone2.render(f5);
			GlStateManager.popMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	public class ModelCube extends ModelBase {
		private final ModelRenderer bone;
		private final ModelRenderer bone2;
	
		public ModelCube() {
			this.textureWidth = 32;
			this.textureHeight = 32;
	
			this.bone = new ModelRenderer(this);
			this.bone.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -0.5F, -4.5F, -0.5F, 1, 1, 1, 0.0F, false));
	
			this.bone2 = new ModelRenderer(this);
			this.bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
			this.bone2.cubeList.add(new ModelBox(this.bone2, 0, 0, -1.0F, -5.0F, -1.0F, 2, 2, 2, 0.0F, false));
			this.bone2.cubeList.add(new ModelBox(this.bone2, 0, 0, -1.5F, -5.5F, -1.5F, 3, 3, 3, 0.0F, false));
			this.bone2.cubeList.add(new ModelBox(this.bone2, 0, 0, -2.0F, -6.0F, -2.0F, 4, 4, 4, 0.0F, false));
			this.bone2.cubeList.add(new ModelBox(this.bone2, 0, 16, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
		}
	
		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			GlStateManager.color(1f, 1f, 1f, 1f);
			this.bone.render(f5);
			GlStateManager.color(1f, 1f, 1f, 0.3f);
			this.bone2.render(f5);
		}
	}
}
