
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
import net.minecraft.init.Items;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.DamageSource;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.Chakra;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityHidingInAsh;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAirPunch;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.ForgeEventFactory;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKaton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:katon")
	public static final Item block = null;
	public static final int ENTITYID = 123;
	public static final int ENTITY2ID = 10123;
	public static final ItemJutsu.JutsuEnum GREATFIREBALL = new ItemJutsu.JutsuEnum(0, "katonfireball", 'C', 30d, new EntityBigFireball.Jutsu());
	public static final ItemJutsu.JutsuEnum GFANNIHILATION = new ItemJutsu.JutsuEnum(1, "tooltip.katon.annihilation", 'B', 50d, new EntityFireStream.Jutsu1());
	public static final ItemJutsu.JutsuEnum HIDINGINASH = new ItemJutsu.JutsuEnum(2, "hiding_in_ash", 'B', 50d, new EntityHidingInAsh.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum GREATFLAME = new ItemJutsu.JutsuEnum(3, "katonfirestream", 'C', 20d, new EntityFireStream.Jutsu2());

	public ItemKaton(ElementsNarutomodMod instance) {
		super(instance, 366);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(GREATFIREBALL, GFANNIHILATION, HIDINGINASH, GREATFLAME));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityBigFireball.class)
				.id(new ResourceLocation("narutomod", "katonfireball"), ENTITYID).name("katonfireball").tracker(64, 1, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityFireStream.class)
				.id(new ResourceLocation("narutomod", "katonfirestream"), ENTITY2ID).name("katonfirestream").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:katon", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityBigFireball.class, renderManager -> {
			return new RenderBigFireball(renderManager);
		});
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
				return this.getPower(stack, entity, timeLeft, 0.1f, 30f);
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
			if (jutsu == GFANNIHILATION || jutsu == GREATFLAME) {
				return Math.min(f, 30.0f);
			} else if (jutsu == GREATFIREBALL) {
				return Math.min(f, 10.0f);
			}
			return f;
		}
	}

	
	public static class EntityFireStream extends Entity {
		private int wait = 50;
		private int maxLife = 110;
		private FireStream fireStream = new FireStream();
		private EntityLivingBase shooter;
		private double width, range;

		public EntityFireStream(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EntityFireStream(EntityLivingBase shooterIn, double widthIn, double rangeIn) {
			this(shooterIn.world);
			this.shooter = shooterIn;
			this.setIdlePosition();
			this.width = widthIn;
			this.range = rangeIn;
		}

		@Override
		protected void entityInit() {
		}

		protected void setIdlePosition() {
			if (this.shooter != null) {
				Vec3d vec3d = this.shooter.getLookVec();
				this.setPosition(this.shooter.posX + vec3d.x, this.shooter.posY + this.shooter.getEyeHeight() + vec3d.y - 0.2d, this.shooter.posZ + vec3d.z);
			}
		}

		@Override
		public void onUpdate() {
			//super.onUpdate();
			if (!this.world.isRemote && (this.ticksExisted > this.maxLife || this.handleWaterMovement())) {
				this.setDead();
			} else {
				this.setIdlePosition();
				if (!this.world.isRemote && this.ticksExisted > this.wait) {
					if (this.shooter != null) {// && this.ticksExisted % 4 == 1) {
						double d = (double)this.ticksExisted / this.maxLife;
						d = 1.0d - d * d * 0.8d;
						this.fireStream.execute(this.shooter, this.range * d, this.width * d);
					}
					if (this.ticksExisted % 10 == 1) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:flamethrow"))), 
						 1.0f, this.rand.nextFloat() * 0.5f + 0.6f);
					}
				}
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public class FireStream extends ProcedureAirPunch {
			public FireStream() {
				this.particlesDuring = null;
			}
	
			@Override
			protected void attackEntityFrom(EntityLivingBase player, Entity target) {
				if (!(target instanceof EntityFireStream)) {
					//float damage = (float)(this.getRange(0) * player.experienceLevel * 0.1);
					double damage = this.getRange(0) * (player.getRNG().nextDouble() * 0.5d + 0.5d);
					target.attackEntityFrom(ItemJutsu.causeJutsuDamage(EntityFireStream.this, player)
					 .setDamageBypassesArmor().setFireDamage(), (float)damage);
					target.setFire(10);
				}
			}
	
			@Override
			protected void preExecuteParticles(EntityLivingBase player) {
				Vec3d vec3d1 = player.getLookVec();
				double angle = Math.atan(this.getFarRadius(0) / this.getRange(0)) * 180d / Math.PI;
				for (int i = 0; i < (int)(this.getRange(0) * this.getFarRadius(0) * 0.8d); i++) {
					Vec3d vec3d = Vec3d.fromPitchYaw(player.rotationPitch + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d),
					 player.rotationYaw + (float)((this.rand.nextDouble()-0.5d) * angle * 3.0d)).scale(this.getRange(0) * 0.1d);
					Particles.spawnParticle(player.world, Particles.Types.FLAME, player.posX + vec3d1.x,
					  player.posY + player.getEyeHeight() - 0.2d + vec3d1.y, player.posZ + vec3d1.z, 1, 0, 0, 0, 
					  vec3d.x, vec3d.y, vec3d.z, 0xffffcf00, (int)(vec3d.lengthVector()*50d)+this.rand.nextInt(20));
				}
			}
	
			@Override
			protected EntityItem processAffectedBlock(EntityLivingBase player, BlockPos pos, EnumFacing facing) {
				if (ForgeEventFactory.getMobGriefingEvent(player.world, player)
				 && player.getDistanceSq(pos) > 16d && player.getRNG().nextFloat() < 0.1f) {
					for (EnumFacing enumfacing : EnumFacing.values()) {
						if (player.world.isAirBlock(pos.offset(enumfacing))) {
							player.world.setBlockState(pos.offset(enumfacing), Blocks.FIRE.getDefaultState(), 3);
						}
					}
				}
				return null;
			}
	
			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				return -1F;
			}
		}

		public static class Jutsu1 implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) 
				  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:katon_gokamekeku"))),
				  SoundCategory.NEUTRAL, 5, 1f);
				entity.world.spawnEntity(new EntityFireStream((EntityPlayer)entity, power * 0.8, power * 1.5));
				//ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, (long)(power * 200));
				return true;
			}
		}

		public static class Jutsu2 implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				EntityFireStream entity1 = new EntityFireStream((EntityPlayer)entity, power * 0.1f, power);
				entity1.wait = 0;
				entity1.maxLife = (int)(power * 10f);
				entity.world.spawnEntity(entity1);
				//ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, (long)(power * 200));
				return true;
			}
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
				this.createJutsu(entity, entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power);
				//if (entity instanceof EntityPlayer)
				//	ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, (long)(power * 80));
				return true;
			}

			public void createJutsu(EntityLivingBase entity, double x, double y, double z, float power) {
				EntityBigFireball entityarrow = new EntityBigFireball(entity, power);
				entityarrow.shoot(x, y, z, 0.95f, 0);
				entity.world.spawnEntity(entityarrow);
			}
		}
	}

	
	@SideOnly(Side.CLIENT)
	public class RenderBigFireball extends Render<EntityBigFireball> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/fireball.png");

		public RenderBigFireball(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}

		@Override
		public void doRender(EntityBigFireball entity, double x, double y, double z, float entityYaw, float partialTicks) {
			GlStateManager.pushMatrix();
			this.bindEntityTexture(entity);
			float scale = entity.getEntityScale();
			GlStateManager.translate(x, y + 0.1d * scale, z);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(scale, scale, scale);
			//TextureAtlasSprite textureatlassprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Items.FIRE_CHARGE);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			//float f = textureatlassprite.getMinU();
			//float f1 = textureatlassprite.getMaxU();
			//float f2 = textureatlassprite.getMinV();
			//float f3 = textureatlassprite.getMaxV();
			GlStateManager.rotate(180F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
			//bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex((double)f, (double)f3).normal(0.0F, 1.0F, 0.0F).endVertex();
			//bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex((double)f1, (double)f3).normal(0.0F, 1.0F, 0.0F).endVertex();
			//bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex((double)f1, (double)f2).normal(0.0F, 1.0F, 0.0F).endVertex();
			//bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex((double)f, (double)f2).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityBigFireball entity) {
			return TEXTURE;
		}
	}
}
