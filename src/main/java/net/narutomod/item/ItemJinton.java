
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;

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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodModVariables;
import net.narutomod.Particles;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityBeamBase;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureSync;

import java.util.List;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class ItemJinton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:jinton")
	public static final Item block = null;
	public static final int ENTITYID = 124;
	public static final int ENTITY2ID = 10124;
	private static final int MIN_PLAYER_XP = 70;
	public static final ItemJutsu.JutsuEnum BEAM = new ItemJutsu.JutsuEnum(0, "jintonbeam", 'S', MIN_PLAYER_XP*10, 600d, new EntityBeam.Jutsu());
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

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityCube.WorldHook());
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
			float max = entity instanceof EntityPlayer ? (float)(PlayerTracker.getNinjaLevel((EntityPlayer)entity)-MIN_PLAYER_XP+10)/10 : 6;
			return MathHelper.clamp(max, 0f, this.getCurrentJutsu(stack).jutsu.getMaxPower());
		}

		private float getUsePercent(int timeLeft) {
			float f = (float)(this.getMaxUseDuration() - timeLeft);
			return Math.min(f / (400f + f), 1.0f);
		}

		@Override
		public float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
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

	public static class EntityBeam extends EntityBeamBase.Base implements ItemJutsu.IJutsu {
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
			this.updatePosition();
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.JINTON;
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
				Vec3d vec = this.shootingEntity.getPositionEyes(1f).subtract(0, 0.2f, 0).add(this.shootingEntity.getLookVec().scale(0.5d));
				this.setPosition(vec.x, vec.y, vec.z);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null) {
				if (this.ticksAlive == 1) {
					ProcedureSync.EntityNBTTag.setAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose, true);
				}
				float scale = this.getScale();
				Vec3d vec3d = shootingEntity.getLookVec();
				if (this.ticksAlive >= this.wait) {
					vec3d = vec3d.scale(MathHelper.sqrt(scale) * 10f);
				}
				this.shoot(vec3d.x, vec3d.y, vec3d.z);
				if (this.ticksAlive >= this.wait + 10) {
					this.beam.execute2(this.shootingEntity, this.getBeamLength(), scale / 2);
				}
			}
			if (!this.world.isRemote && this.ticksAlive > this.wait + 60) {
				this.setDead();
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (this.shootingEntity != null) {
				ProcedureSync.EntityNBTTag.removeAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose);
			}
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
			protected void attackEntityFrom(Entity player, Entity target) {
				double d = this.getFarRadius(0) / target.getEntityBoundingBox().getAverageEdgeLength() * 0.2d;
				float f = target instanceof EntityLivingBase ? ((EntityLivingBase)target).getMaxHealth() * (float)d : Float.MAX_VALUE;
				attackEntityWithJutsu(EntityBeam.this, player, target, f);
			}

			@Override
			protected float getBreakChance(BlockPos pos, Entity player, double range) {
				return 1.0F;
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (entity instanceof EntityPlayer) {
					//power = Math.min(power / 2 + 0.5f, 10f);
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

			@Override
			public float getPowerupDelay() {
				return 100.0f;
			}

			@Override
			public float getMaxPower() {
				return 10.0f;
			}
		}
	}

	private static void attackEntityWithJutsu(Entity projectile, Entity attacker, Entity target, float amount) {
		if (!ItemJutsu.canTarget(target)) {
			return;
		}
		if (target instanceof EntityLivingBase) {
			target.hurtResistantTime = 10;
			target.attackEntityFrom(ItemJutsu.causeJutsuDamage(projectile, attacker).setDamageBypassesArmor().setDamageIsAbsolute(), amount);
		} else if (!target.attackEntityFrom(ItemJutsu.causeJutsuDamage(projectile, attacker).setDamageBypassesArmor().setDamageIsAbsolute(), amount)) {
			target.onKillCommand();
		}
	}

	public static class EntityCube extends EntityScalableProjectile.Base implements ItemJutsu.IJutsu {
		private final int wait = 60;
		private final int growTime = 30;
		private final int idleTime = 40;
		private final int shrinkTime = 40;
		private float fullScale = 1f;
		
		public EntityCube(World a) {
			super(a);
			this.setOGSize(0.5F, 0.5F);
			this.setEntityScale(0.01F);
			this.isImmuneToFire = true;
		}

		public EntityCube(EntityLivingBase shooter, float scale) {
			super(shooter);
			this.setOGSize(0.5F, 0.5F);
			this.setEntityScale(0.01F);
			this.fullScale = scale;
			this.setWaitPosition(shooter);
			this.isImmuneToFire = true;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.JINTON;
		}

		private void setWaitPosition(EntityLivingBase shooter) {
			Vec3d vec3d = shooter.getLookVec().scale(0.5);
			this.setPosition(shooter.posX + vec3d.x, shooter.posY + shooter.getEyeHeight() - 0.5d + vec3d.y, shooter.posZ + vec3d.z);
			this.setEntityScale(Math.min((float)this.ticksAlive / this.wait, 0.5f));
		}

		public void shoot(RayTraceResult result) {
			double d0 = result.entityHit != null ? result.entityHit.posX : result.hitVec.x;
			double d1 = result.entityHit != null ? result.entityHit.posY + result.entityHit.height/2 : result.hitVec.y;
			double d2 = result.entityHit != null ? result.entityHit.posZ : result.hitVec.z;
			this.setPosition(d0, d1, d2);
			ProcedureAoeCommand.set(this.world, d0, d1, d2, 0d, this.fullScale * 0.2f).exclude(this.shootingEntity).effect(MobEffects.SLOWNESS, 5, 5, false);
		}

		private void destroyBlocksAndEntitiesInAABB(AxisAlignedBB bb) {
			if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity)) {
				for (BlockPos pos : ProcedureUtils.getNonAirBlocks(this.world, bb)) {
					if (this.rand.nextFloat() < 0.2f) {
						this.world.setBlockToAir(pos);
					}
				}
			}
			for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, bb)) {
				double d = ProcedureUtils.BB.getVolume(bb.intersect(entity.getEntityBoundingBox()))
				 / ProcedureUtils.BB.getVolume(entity.getEntityBoundingBox()) * 0.025d;
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
				if (this.ticksAlive == 1) {
					ProcedureSync.EntityNBTTag.setAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose, true);
				}
				if (this.ticksAlive <= this.wait) {
					this.setWaitPosition(this.shootingEntity);
					if (this.ticksAlive == this.wait) {
						this.shoot(ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d, true, this));
					}
				} else {
					ProcedureAoeCommand.set(this, 0d, this.width/2).exclude(this.shootingEntity).motion(0d, 0d, 0d);
					if (this.ticksAlive <= this.wait + this.growTime) {
						this.setEntityScale(this.fullScale * (this.ticksAlive - this.wait) / (float) this.growTime);
					} else if (this.ticksAlive <= idle) {
						if (this.ticksAlive == this.wait + this.growTime + 1) {
							this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:groundshock")), 1f, 0.8f);
						}
						if (idle - this.ticksAlive > 1) {
							Particles.Renderer particles = new Particles.Renderer(this.world);
							for (int i = 0; i < 50; i++) {
								particles.spawnParticles(Particles.Types.SMOKE, this.posX, this.posY + 0.5d * this.height, this.posZ,
								 1, 0d, 0d, 0d, (this.rand.nextFloat()-0.5f) * 0.2f * this.fullScale,
								 (this.rand.nextFloat()-0.5f) * 0.2f * this.fullScale, (this.rand.nextFloat()-0.5f) * 0.2f * this.fullScale,
								 0xB0FFFFFF, (int)(this.fullScale * 4f), idle - this.ticksAlive - 1, 0xF0, -1, 2, -10);
							}
							particles.send();
						}
						this.destroyBlocksAndEntitiesInAABB(this.getEntityBoundingBox());
					} else {
						if (this.shootingEntity.getEntityData().getBoolean(NarutomodModVariables.forceBowPose)) {
							ProcedureSync.EntityNBTTag.removeAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose);
						}
						Particles.spawnParticle(this.world, Particles.Types.FALLING_DUST, this.posX, this.posY + 0.25d * this.fullScale, this.posZ,
						  (int)(this.fullScale * 6), this.fullScale * 0.1F, this.fullScale * 0.1F, this.fullScale * 0.1F, 0D, 0D, 0D, 0xC0A0A0A0);
					}
				}
			}
			if (!this.world.isRemote
			 && (this.ticksAlive > idle + this.shrinkTime || this.shootingEntity == null || !this.shootingEntity.isEntityAlive())) {
				this.setDead();
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (this.shootingEntity != null) {
				ProcedureSync.EntityNBTTag.removeAndSync(this.shootingEntity, NarutomodModVariables.forceBowPose);
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
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
				  .getObject(new ResourceLocation("narutomod:genkaihakurinojutsu")), SoundCategory.PLAYERS, 1, 1f);
				entity.world.spawnEntity(new EntityCube(entity, power * 2f));
				return true;
			}

			@Override
			public float getPowerupDelay() {
				return 100.0f;
			}

			@Override
			public float getMaxPower() {
				return 25.0f;
			}
		}

		public static class WorldHook {
			@SubscribeEvent
			public void onGetCollisionBoxes(GetCollisionBoxesEvent event) {
				//if (event.getWorld().isRemote && event.getEntity() == null) {
					for (EntityCube ec : event.getWorld().getEntitiesWithinAABB(EntityCube.class, event.getAabb().grow(10.0D))) {
						if (ec != event.getEntity() && ec.getEntityBoundingBox().intersects(event.getAabb())
						 && ec.getTicksAlive() >= ec.wait + ec.growTime && ec.getTicksAlive() - ec.wait - ec.growTime <= ec.idleTime) {
							event.getCollisionBoxesList().clear();
							float f = ec.fullScale * 0.025f;
							AxisAlignedBB bb = ec.getEntityBoundingBox();
							List<AxisAlignedBB> list = Lists.newArrayList(
								new AxisAlignedBB(bb.minX, bb.minY + f, bb.minZ + f, bb.minX + f, bb.maxY - f, bb.maxZ - f),
								new AxisAlignedBB(bb.minX + f, bb.minY, bb.minZ + f, bb.maxX - f, bb.minY + f, bb.maxZ - f),
								new AxisAlignedBB(bb.minX + f, bb.minY + f, bb.minZ, bb.maxX - f, bb.maxY - f, bb.minZ + f),
								new AxisAlignedBB(bb.maxX - f, bb.minY + f, bb.minZ + f, bb.maxX, bb.maxY - f, bb.maxZ - f),
								new AxisAlignedBB(bb.minX + f, bb.maxY - f, bb.minZ + f, bb.maxX - f, bb.maxY, bb.maxZ - f),
								new AxisAlignedBB(bb.minX + f, bb.minY + f, bb.maxZ - f, bb.maxX - f, bb.maxY - f, bb.maxZ));
							for (AxisAlignedBB axisalignedbb : list) {
								if (axisalignedbb.intersects(event.getAabb())) {
									event.getCollisionBoxesList().add(axisalignedbb);
								}
							}
						}
					}
				//}
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
			RenderingRegistry.registerEntityRenderingHandler(EntityBeam.class, renderManager -> new RenderBeam(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntityCube.class, renderManager -> new RenderCube(renderManager));
		}
	
		@SideOnly(Side.CLIENT)
		public class RenderBeam extends Render<EntityBeam> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/longcube_white.png");
			private final ModelCube model = new ModelCube();
	
			public RenderBeam(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			public boolean shouldRender(EntityBeam livingEntity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
			@Override
			public void doRender(EntityBeam bullet, double x, double y, double z, float yaw, float pt) {
				float age = (float)bullet.ticksExisted + pt;
				float f = Math.max(age - (float)bullet.wait, 0.0f);
				float f1 = bullet.prevBeamLength + (bullet.getBeamLength() - bullet.prevBeamLength) * pt;
				float length = MathHelper.clamp(f1 * f / 10f, 0.6f, f1);
				float scale = f > 0.0f ? bullet.getScale() * length / f1 : 0.6f;
				f = age * 0.01F;
				this.bindEntityTexture(bullet);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);
				GlStateManager.rotate(ProcedureUtils.interpolateRotation(bullet.prevRotationYaw, bullet.rotationYaw, pt), 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(90.0F - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * pt, 1.0F, 0.0F, 0.0F);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.shadeModel(0x1D01);
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				if (age <= (float)bullet.wait) {
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
				} else {
					GlStateManager.depthMask(false);
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				}
				this.renderSphere(bullet, length, scale);
				float f5 = 0.0F - f;
				float f6 = length / 32.0F - f;
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
				for (int i = 12, j = 0; j <= i; j++) {
					float f7 = MathHelper.sin((j % i) * ((float) Math.PI * 2F) / i) * scale * 0.5F;
					float f8 = MathHelper.cos((j % i) * ((float) Math.PI * 2F) / i) * scale * 0.5F;
					float f9 = (float)(j % i) / i;
					bufferbuilder.pos(f7, 0.0F, f8).tex(f9, f5).color(1.0f, 1.0f, 1.0f, 0.2f).endVertex();
				}
				for (int i = 12, j = 0; j <= i; j++) {
					float f7 = MathHelper.sin((j % i) * ((float) Math.PI * 2F) / i) * scale * 0.5F;
					float f8 = MathHelper.cos((j % i) * ((float) Math.PI * 2F) / i) * scale * 0.5F;
					float f9 = (float)(j % i) / i;
					bufferbuilder.pos(f7, 0.0F, f8).tex(f9, f5).color(1.0f, 1.0f, 1.0f, 0.2f).endVertex();
					bufferbuilder.pos(0.0F, length, 0.0F).tex(f9, f6).color(1.0f, 1.0f, 1.0f, 0.2f).endVertex();
				}
				tessellator.draw();
				GlStateManager.depthMask(true);
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.shadeModel(0x1D00);
				GlStateManager.popMatrix();
			}

			private void renderSphere(Entity entity, float length, float scale) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 0.2F + length * 0.4F - 0.24F, 0.0F);
				GlStateManager.scale(scale * 0.8F, -length * 2 + 0.6F, scale * 0.8F);
				this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityBeam entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderCube extends Render<EntityCube> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/white_square.png");
			private final ModelCube model = new ModelCube();
	
			public RenderCube(RenderManager renderManager) {
				super(renderManager);
				this.shadowSize = 0.1F;
			}
	
			@Override
			public void doRender(EntityCube entity, double x, double y, double z, float yaw, float pt) {
				float age = pt + entity.getTicksAlive();
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				float scale = entity.getEntityScale() * 0.5F;
				GlStateManager.translate((float) x, (float) y + 0.5F * scale, (float) z);
				GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt - 180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				if (age <= (float)entity.wait) {
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
				} else {
					GlStateManager.depthMask(false);
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					//GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
					// age > (float)entity.wait + entity.growTime && age <= (float)entity.wait + entity.growTime + entity.idleTime
					// ? GlStateManager.DestFactor.ONE : GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				}
				this.model.render(entity, 0.0F, 0.0F, age, 0.0F, 0.0F, 0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableLighting();
				GlStateManager.depthMask(true);
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCube entity) {
				return this.texture;
			}
		}
		
		@SideOnly(Side.CLIENT)
		public class ModelCube extends ModelBase {
			private final ModelRenderer cube;
			private final ModelRenderer sphere;
			private final ModelRenderer hexadecagon;
			private final ModelRenderer hexadecagon_r1;
			private final ModelRenderer hexadecagon_r2;
			private final ModelRenderer hexadecagon_r3;
			private final ModelRenderer hexadecagon_r4;
			private final ModelRenderer hexadecagon2;
			private final ModelRenderer hexadecagon_r5;
			private final ModelRenderer hexadecagon_r6;
			private final ModelRenderer hexadecagon_r7;
			private final ModelRenderer hexadecagon_r8;
			private final ModelRenderer hexadecagon6;
			private final ModelRenderer hexadecagon_r9;
			private final ModelRenderer hexadecagon_r10;
			private final ModelRenderer hexadecagon_r11;
			private final ModelRenderer hexadecagon_r12;
			private final ModelRenderer hexadecagon7;
			private final ModelRenderer hexadecagon_r13;
			private final ModelRenderer hexadecagon_r14;
			private final ModelRenderer hexadecagon_r15;
			private final ModelRenderer hexadecagon_r16;
			private final ModelRenderer hexadecagon8;
			private final ModelRenderer hexadecagon_r17;
			private final ModelRenderer hexadecagon_r18;
			private final ModelRenderer hexadecagon_r19;
			private final ModelRenderer hexadecagon_r20;
			private final ModelRenderer hexadecagon3;
			private final ModelRenderer hexadecagon_r21;
			private final ModelRenderer hexadecagon_r22;
			private final ModelRenderer hexadecagon_r23;
			private final ModelRenderer hexadecagon_r24;
			private final ModelRenderer hexadecagon4;
			private final ModelRenderer hexadecagon_r25;
			private final ModelRenderer hexadecagon_r26;
			private final ModelRenderer hexadecagon_r27;
			private final ModelRenderer hexadecagon_r28;
			private final ModelRenderer hexadecagon5;
			private final ModelRenderer hexadecagon_r29;
			private final ModelRenderer hexadecagon_r30;
			private final ModelRenderer hexadecagon_r31;
			private final ModelRenderer hexadecagon_r32;
		
			public ModelCube() {
				textureWidth = 64;
				textureHeight = 64;
		
				cube = new ModelRenderer(this);
				cube.setRotationPoint(0.0F, 0.0F, 0.0F);
				cube.cubeList.add(new ModelBox(cube, 0, 0, -8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F, false));
		
				sphere = new ModelRenderer(this);
				sphere.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				hexadecagon = new ModelRenderer(this);
				hexadecagon.setRotationPoint(0.0F, -0.0014F, 0.001F);
				sphere.addChild(hexadecagon);
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 0, 0, -0.5F, -0.5013F, -2.501F, 1, 1, 0, 0.0F, false));
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, 0, 0, -0.5F, -0.5013F, 2.499F, 1, 1, 0, 0.0F, false));
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, -1, -1, -0.5F, 2.5014F, -0.4982F, 1, 0, 1, 0.0F, false));
				hexadecagon.cubeList.add(new ModelBox(hexadecagon, -1, -1, -0.5F, -2.4986F, -0.4982F, 1, 0, 1, 0.0F, false));
		
				hexadecagon_r1 = new ModelRenderer(this);
				hexadecagon_r1.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon.addChild(hexadecagon_r1);
				setRotationAngle(hexadecagon_r1, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r2 = new ModelRenderer(this);
				hexadecagon_r2.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon.addChild(hexadecagon_r2);
				setRotationAngle(hexadecagon_r2, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r3 = new ModelRenderer(this);
				hexadecagon_r3.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon.addChild(hexadecagon_r3);
				setRotationAngle(hexadecagon_r3, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r4 = new ModelRenderer(this);
				hexadecagon_r4.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon.addChild(hexadecagon_r4);
				setRotationAngle(hexadecagon_r4, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon2 = new ModelRenderer(this);
				hexadecagon2.setRotationPoint(0.0F, -0.0014F, 0.001F);
				sphere.addChild(hexadecagon2);
				setRotationAngle(hexadecagon2, 0.0F, 0.3927F, 0.0F);
				hexadecagon2.cubeList.add(new ModelBox(hexadecagon2, 0, 0, -0.5F, -0.5013F, -2.501F, 1, 1, 0, 0.0F, false));
				hexadecagon2.cubeList.add(new ModelBox(hexadecagon2, 0, 0, -0.5F, -0.5013F, 2.499F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r5 = new ModelRenderer(this);
				hexadecagon_r5.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon2.addChild(hexadecagon_r5);
				setRotationAngle(hexadecagon_r5, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r6 = new ModelRenderer(this);
				hexadecagon_r6.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon2.addChild(hexadecagon_r6);
				setRotationAngle(hexadecagon_r6, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r7 = new ModelRenderer(this);
				hexadecagon_r7.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon2.addChild(hexadecagon_r7);
				setRotationAngle(hexadecagon_r7, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r7.cubeList.add(new ModelBox(hexadecagon_r7, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r7.cubeList.add(new ModelBox(hexadecagon_r7, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r8 = new ModelRenderer(this);
				hexadecagon_r8.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon2.addChild(hexadecagon_r8);
				setRotationAngle(hexadecagon_r8, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r8.cubeList.add(new ModelBox(hexadecagon_r8, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r8.cubeList.add(new ModelBox(hexadecagon_r8, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon6 = new ModelRenderer(this);
				hexadecagon6.setRotationPoint(0.0F, -0.0014F, 0.001F);
				sphere.addChild(hexadecagon6);
				setRotationAngle(hexadecagon6, 0.0F, -0.3927F, 0.0F);
				hexadecagon6.cubeList.add(new ModelBox(hexadecagon6, 0, 0, -0.5F, -0.5013F, -2.501F, 1, 1, 0, 0.0F, false));
				hexadecagon6.cubeList.add(new ModelBox(hexadecagon6, 0, 0, -0.5F, -0.5013F, 2.499F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r9 = new ModelRenderer(this);
				hexadecagon_r9.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon6.addChild(hexadecagon_r9);
				setRotationAngle(hexadecagon_r9, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r10 = new ModelRenderer(this);
				hexadecagon_r10.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon6.addChild(hexadecagon_r10);
				setRotationAngle(hexadecagon_r10, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r11 = new ModelRenderer(this);
				hexadecagon_r11.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon6.addChild(hexadecagon_r11);
				setRotationAngle(hexadecagon_r11, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r11.cubeList.add(new ModelBox(hexadecagon_r11, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r11.cubeList.add(new ModelBox(hexadecagon_r11, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r12 = new ModelRenderer(this);
				hexadecagon_r12.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon6.addChild(hexadecagon_r12);
				setRotationAngle(hexadecagon_r12, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r12.cubeList.add(new ModelBox(hexadecagon_r12, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r12.cubeList.add(new ModelBox(hexadecagon_r12, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon7 = new ModelRenderer(this);
				hexadecagon7.setRotationPoint(0.0F, -0.0014F, 0.001F);
				sphere.addChild(hexadecagon7);
				setRotationAngle(hexadecagon7, 0.0F, -0.7854F, 0.0F);
				hexadecagon7.cubeList.add(new ModelBox(hexadecagon7, 0, 0, -0.5F, -0.5013F, -2.501F, 1, 1, 0, 0.0F, false));
				hexadecagon7.cubeList.add(new ModelBox(hexadecagon7, 0, 0, -0.5F, -0.5013F, 2.499F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r13 = new ModelRenderer(this);
				hexadecagon_r13.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon7.addChild(hexadecagon_r13);
				setRotationAngle(hexadecagon_r13, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r14 = new ModelRenderer(this);
				hexadecagon_r14.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon7.addChild(hexadecagon_r14);
				setRotationAngle(hexadecagon_r14, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r15 = new ModelRenderer(this);
				hexadecagon_r15.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon7.addChild(hexadecagon_r15);
				setRotationAngle(hexadecagon_r15, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r15.cubeList.add(new ModelBox(hexadecagon_r15, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r15.cubeList.add(new ModelBox(hexadecagon_r15, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r16 = new ModelRenderer(this);
				hexadecagon_r16.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon7.addChild(hexadecagon_r16);
				setRotationAngle(hexadecagon_r16, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r16.cubeList.add(new ModelBox(hexadecagon_r16, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r16.cubeList.add(new ModelBox(hexadecagon_r16, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon8 = new ModelRenderer(this);
				hexadecagon8.setRotationPoint(0.0F, -0.0014F, 0.001F);
				sphere.addChild(hexadecagon8);
				setRotationAngle(hexadecagon8, 0.0F, -1.1781F, 0.0F);
				hexadecagon8.cubeList.add(new ModelBox(hexadecagon8, 0, 0, -0.5F, -0.5013F, -2.501F, 1, 1, 0, 0.0F, false));
				hexadecagon8.cubeList.add(new ModelBox(hexadecagon8, 0, 0, -0.5F, -0.5013F, 2.499F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r17 = new ModelRenderer(this);
				hexadecagon_r17.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon8.addChild(hexadecagon_r17);
				setRotationAngle(hexadecagon_r17, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r18 = new ModelRenderer(this);
				hexadecagon_r18.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon8.addChild(hexadecagon_r18);
				setRotationAngle(hexadecagon_r18, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r19 = new ModelRenderer(this);
				hexadecagon_r19.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon8.addChild(hexadecagon_r19);
				setRotationAngle(hexadecagon_r19, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r19.cubeList.add(new ModelBox(hexadecagon_r19, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r19.cubeList.add(new ModelBox(hexadecagon_r19, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r20 = new ModelRenderer(this);
				hexadecagon_r20.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon8.addChild(hexadecagon_r20);
				setRotationAngle(hexadecagon_r20, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r20.cubeList.add(new ModelBox(hexadecagon_r20, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r20.cubeList.add(new ModelBox(hexadecagon_r20, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon3 = new ModelRenderer(this);
				hexadecagon3.setRotationPoint(0.0F, -0.0014F, 0.001F);
				sphere.addChild(hexadecagon3);
				setRotationAngle(hexadecagon3, 0.0F, 0.7854F, 0.0F);
				hexadecagon3.cubeList.add(new ModelBox(hexadecagon3, 0, 0, -0.5F, -0.5013F, -2.501F, 1, 1, 0, 0.0F, false));
				hexadecagon3.cubeList.add(new ModelBox(hexadecagon3, 0, 0, -0.5F, -0.5013F, 2.499F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r21 = new ModelRenderer(this);
				hexadecagon_r21.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon3.addChild(hexadecagon_r21);
				setRotationAngle(hexadecagon_r21, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r22 = new ModelRenderer(this);
				hexadecagon_r22.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon3.addChild(hexadecagon_r22);
				setRotationAngle(hexadecagon_r22, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r23 = new ModelRenderer(this);
				hexadecagon_r23.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon3.addChild(hexadecagon_r23);
				setRotationAngle(hexadecagon_r23, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r23.cubeList.add(new ModelBox(hexadecagon_r23, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r23.cubeList.add(new ModelBox(hexadecagon_r23, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r24 = new ModelRenderer(this);
				hexadecagon_r24.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon3.addChild(hexadecagon_r24);
				setRotationAngle(hexadecagon_r24, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r24.cubeList.add(new ModelBox(hexadecagon_r24, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r24.cubeList.add(new ModelBox(hexadecagon_r24, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon4 = new ModelRenderer(this);
				hexadecagon4.setRotationPoint(0.0F, -0.0014F, 0.001F);
				sphere.addChild(hexadecagon4);
				setRotationAngle(hexadecagon4, 0.0F, 1.1781F, 0.0F);
				hexadecagon4.cubeList.add(new ModelBox(hexadecagon4, 0, 0, -0.5F, -0.5013F, -2.501F, 1, 1, 0, 0.0F, false));
				hexadecagon4.cubeList.add(new ModelBox(hexadecagon4, 0, 0, -0.5F, -0.5013F, 2.499F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r25 = new ModelRenderer(this);
				hexadecagon_r25.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon4.addChild(hexadecagon_r25);
				setRotationAngle(hexadecagon_r25, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r26 = new ModelRenderer(this);
				hexadecagon_r26.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon4.addChild(hexadecagon_r26);
				setRotationAngle(hexadecagon_r26, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r27 = new ModelRenderer(this);
				hexadecagon_r27.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon4.addChild(hexadecagon_r27);
				setRotationAngle(hexadecagon_r27, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r27.cubeList.add(new ModelBox(hexadecagon_r27, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r27.cubeList.add(new ModelBox(hexadecagon_r27, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r28 = new ModelRenderer(this);
				hexadecagon_r28.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon4.addChild(hexadecagon_r28);
				setRotationAngle(hexadecagon_r28, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r28.cubeList.add(new ModelBox(hexadecagon_r28, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r28.cubeList.add(new ModelBox(hexadecagon_r28, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon5 = new ModelRenderer(this);
				hexadecagon5.setRotationPoint(0.0F, -0.0014F, 0.001F);
				sphere.addChild(hexadecagon5);
				setRotationAngle(hexadecagon5, 0.0F, 1.5708F, 0.0F);
				hexadecagon5.cubeList.add(new ModelBox(hexadecagon5, 0, 0, -0.5F, -0.5013F, -2.501F, 1, 1, 0, 0.0F, false));
				hexadecagon5.cubeList.add(new ModelBox(hexadecagon5, 0, 0, -0.5F, -0.5013F, 2.499F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r29 = new ModelRenderer(this);
				hexadecagon_r29.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon5.addChild(hexadecagon_r29);
				setRotationAngle(hexadecagon_r29, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r30 = new ModelRenderer(this);
				hexadecagon_r30.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon5.addChild(hexadecagon_r30);
				setRotationAngle(hexadecagon_r30, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, -1, -1, -0.5F, -2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, -1, -1, -0.5F, 2.5F, -0.4973F, 1, 0, 1, 0.0F, false));
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r31 = new ModelRenderer(this);
				hexadecagon_r31.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon5.addChild(hexadecagon_r31);
				setRotationAngle(hexadecagon_r31, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r31.cubeList.add(new ModelBox(hexadecagon_r31, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r31.cubeList.add(new ModelBox(hexadecagon_r31, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
		
				hexadecagon_r32 = new ModelRenderer(this);
				hexadecagon_r32.setRotationPoint(0.0F, 0.0014F, -0.001F);
				hexadecagon5.addChild(hexadecagon_r32);
				setRotationAngle(hexadecagon_r32, 0.7854F, 0.0F, 0.0F);
				hexadecagon_r32.cubeList.add(new ModelBox(hexadecagon_r32, 0, 0, -0.5F, -0.5027F, 2.5F, 1, 1, 0, 0.0F, false));
				hexadecagon_r32.cubeList.add(new ModelBox(hexadecagon_r32, 0, 0, -0.5F, -0.5027F, -2.5F, 1, 1, 0, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				float alpha = 1.0f;
				if (entity instanceof EntityCube) {
					EntityCube ec = ((EntityCube)entity);
					alpha = 1f - MathHelper.clamp((float)(f2 - ec.wait - ec.growTime) / (ec.idleTime - 2), 0f, 1f);
				}
				GlStateManager.alphaFunc(0x204, 0.001f);
				GlStateManager.color(1f, 1f, 1f, alpha);
				sphere.render(f5);
				if (entity instanceof EntityCube) {
					EntityCube ec = ((EntityCube)entity);
					alpha = 1f - MathHelper.clamp((float)(f2 - ec.wait - ec.growTime - ec.idleTime) / ec.shrinkTime, 0f, 1f);
					GlStateManager.color(1f, 1f, 1f, 0.2f * alpha);
					cube.render(f5);
				}
				GlStateManager.alphaFunc(0x204, 0.1f);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
