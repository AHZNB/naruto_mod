
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
import net.minecraft.world.WorldServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
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
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.Particles;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemShakuton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:shakuton")
	public static final Item block = null;
	public static final int ENTITYID = 269;
	public static final ItemJutsu.JutsuEnum ORB = new ItemJutsu.JutsuEnum(0, "scorchorb", 'S', 150, 100d, new EntityScorchBall.Jutsu());
	public static final ItemJutsu.JutsuEnum SHOOT = new ItemJutsu.JutsuEnum(1, "tooltip.shakuton.scorchkill", 'S', 200, 50d, new SetOrbTarget());
	public static final ItemJutsu.JutsuEnum BLAST = new ItemJutsu.JutsuEnum(2, "tooltip.shakuton.scorchblast", 'S', 250, 50d, new SuperSteamBlast());

	public ItemShakuton(ElementsNarutomodMod instance) {
		super(instance, 589);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(ORB, SHOOT, BLAST));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityScorchBall.class)
				.id(new ResourceLocation("narutomod", "scorchorb"), ENTITYID).name("scorchorb").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:shakuton", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		protected static final String spawnedBalls = "SpawnedBallsId";

		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.SHAKUTON, list);
			setUnlocalizedName("shakuton");
			setRegistryName("shakuton");
			setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[ORB.index] = 0;
			this.defaultCooldownMap[SHOOT.index] = 0;
			this.defaultCooldownMap[BLAST.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			return 1f;
		}

		protected void saveSpawnedBall(ItemStack stack, Entity entity) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			int[] oldarray = stack.getTagCompound().getIntArray(spawnedBalls);
			int[] newarray = new int[oldarray.length + 1];
			System.arraycopy(oldarray, 0, newarray, 0, oldarray.length);
			newarray[oldarray.length] = entity.getEntityId();
			stack.getTagCompound().setIntArray(spawnedBalls, newarray);
		}

		@Nullable
		protected EntityScorchBall get1stBallAndPutLast(World world, ItemStack stack) {
			if (stack.hasTagCompound()) {
				int[] balls = stack.getTagCompound().getIntArray(spawnedBalls);
				if (balls.length > 0) {
					Entity entity = world.getEntityByID(balls[0]);
					if (entity instanceof EntityScorchBall) {
						if (balls.length > 1) {
							System.arraycopy(balls, 1, balls, 0, balls.length - 1);
							balls[balls.length - 1] = entity.getEntityId();
						}
						if (entity.isEntityAlive()) {
							return (EntityScorchBall)entity;
						}
					}
				}
			}
			return null;
		}

		protected int getTotalBalls(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getIntArray(spawnedBalls).length : 0;
		}

		protected void clearBalls(ItemStack stack) {
			stack.getTagCompound().removeTag(spawnedBalls);
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase livingEntity = (EntityLivingBase) entity;
				if (!livingEntity.getHeldItemMainhand().equals(itemstack) && !livingEntity.getHeldItemOffhand().equals(itemstack)) {
					this.clearBalls(itemstack);
				}
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			if (entity.isCreative() || (ProcedureUtils.hasItemInInventory(entity, ItemFuton.block) 
			 && ProcedureUtils.hasItemInInventory(entity, ItemKaton.block))) {
				return super.onItemRightClick(world, entity, hand);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, entity.getHeldItem(hand));
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(TextFormatting.GREEN + net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.shakuton.musthave") + TextFormatting.RESET);
		}
	}

	public static class SetOrbTarget implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			EntityScorchBall entity1 = ((RangedItem)block).get1stBallAndPutLast(entity.world, stack);
			if (entity1 != null && entity1.isEntityAlive()) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 30d, 1.5d, EntityScorchBall.class);
				if (res != null && res.entityHit != null) {
					entity1.setTarget(res.entityHit);
					return true;
				}
			}
			return false;
		}
	}

	public static class EntityScorchBall extends EntityScalableProjectile.Base {
		private double idleHeight;
		private Entity target;
		//private final int growTime = 60;
		private final float inititalScale = 0.5f;
		private float maxScale = inititalScale;
		private int targetTime = -1;
		
		public EntityScorchBall(World a) {
			super(a);
			this.setOGSize(1.0F, 1.0F);
		}

		public EntityScorchBall(EntityLivingBase shooter) {
			super(shooter);
			this.setOGSize(1.0F, 1.0F);
			this.setEntityScale(this.inititalScale);
			this.setPosition(shooter.posX, shooter.posY+shooter.height, shooter.posZ);
			this.idleHeight = shooter.getEyeHeight();
		}

		private Vec3d getIdlePosition() {
			if (this.shootingEntity != null) {
				Vec3d vec = Vec3d.fromPitchYaw(0f, this.ticksExisted * 9).addVector(0d, this.idleHeight, 0d);
				return this.shootingEntity.getPositionVector().add(vec);
			}
			return this.getPositionVector();
		}

		public void setNextPosition(Vec3d vec) {
			if (this.getDistance(vec.x, vec.y, vec.z) > 0.5d && this.targetTime >= 0) {
				this.setVelocity(vec.subtract(this.getPositionVector()).normalize().scale(0.6d));
			} else {
				this.setVelocity(vec.subtract(this.getPositionVector()));
				if (vec.equals(this.getIdlePosition()) && this.targetTime >= 0) {
					this.setTarget(null);
				}
			}
		}

		protected void setTarget(@Nullable Entity targetIn) {
			this.target = targetIn;
			this.targetTime = targetIn != null ? 100 : -1;
		}

		protected void setMaxScale(float scale) {
			this.maxScale = scale;
		}

		private void moveGrowAndShoot() {
			if (this.shootingEntity != null) {
				Vec3d vec = this.shootingEntity.getPositionVector().addVector(0d, this.shootingEntity.height + 1.5f, 0d);
				if (this.getDistance(vec.x, vec.y, vec.z) > 0.2d) {
					this.setVelocity(vec.subtract(this.getPositionVector()).normalize().scale(0.1d));
				} else if (this.maxScale > 0) {
					this.setVelocity(Vec3d.ZERO);
					float scale = this.getEntityScale();
					if (scale < this.maxScale) {
						this.setEntityScale(scale * 1.03f);
					} else {
						Vec3d vec2 = this.shootingEntity.getLookVec();
						this.shoot(vec2.x, vec2.y, vec2.z, 0.95f, 0f);
					}
				} else {
					this.setDead();
				}
			}
		}

		private void setVelocity(Vec3d vec) {
			this.motionX = vec.x;
			this.motionY = vec.y;
			this.motionZ = vec.z;
			this.isAirBorne = true;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.isLaunched()) {
				if (this.maxScale != this.getEntityScale()) {
					this.moveGrowAndShoot();
				} else if (this.target != null && this.targetTime > 0) {
					if (this.target.isEntityAlive()) {
						this.setNextPosition(this.target.getPositionEyes(1f));
						--this.targetTime;
					} else {
						this.targetTime = 0;
					}
				} else {
					this.setNextPosition(this.getIdlePosition());
				}
				if (!this.world.isRemote) {
					for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox())) {
						if (!entity.equals(this.shootingEntity) && !entity.equals(this)) {
							entity.hurtResistantTime = 10;
							entity.getEntityData().setBoolean("TempData_disableKnockback", true);
							entity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), 1.5f);
							this.scorchEffects(entity.posX, entity.posY+entity.height/2, entity.posZ, entity.width/2, entity.height/2);
						}
					}
				}
			}
			if (!this.world.isRemote && (this.shootingEntity == null 
			 || (this.shootingEntity.getHeldItemMainhand().getItem() != block
			  && this.shootingEntity.getHeldItemOffhand().getItem() != block))) {
				this.setDead();
			}
		}

		@Override
		protected void checkOnGround() {
			super.checkOnGround();
			if (this.onGround) {
				this.onGround = false;
				this.scorchEffects(this.posX, this.posY, this.posZ, 0.4d, 0.4d);
				this.targetTime = 0;
			}
			if (this.isInWater()) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote) {
				if ((result.entityHit != null && result.entityHit.equals(this.shootingEntity))
				 || (result.typeOfHit == RayTraceResult.Type.BLOCK && this.ticksInAir <= 15)) {
					return;
				}
				boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
				new net.narutomod.event.EventSphericalExplosion(this.world, this.shootingEntity,
				 (int)this.posX, (int)this.posY + 5, (int)this.posZ, (int)this.maxScale, 0, 0.3333f);
				ProcedureAoeCommand.set(this, 0d, this.maxScale)
				 .damageEntitiesCentered(ItemJutsu.causeJutsuDamage(this, this.shootingEntity), this.maxScale * 60f);
				//this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, this.maxScale * 5f, flag, flag);
				this.scorchEffects(this.posX, this.posY, this.posZ, 2.5d * this.maxScale, 1d);
				this.setDead();
			}
		}

		/*@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.world.isRemote && ItemJutsu.isDamageSourceJutsu(source) && source.getImmediateSource() != null) {
				
			}
		}*/

		@Override
		public void renderParticles() {
			if (this.world.isRemote) {
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY + this.height/2, this.posZ, 
				 (int)(this.width*25f), this.width/2, 0d, this.width/2, 0d, 0d, 0d, 0x40ff4e83, 10, 0, 0xF0);
			}
		}

		private void scorchEffects(double x, double y, double z, double dx, double dy) {
			this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1f, this.rand.nextFloat() * 0.6f + 0.7f);
			Particles.spawnParticle(this.world, Particles.Types.SMOKE, x, y, z, (int)(dx * dy * 100d), 
			 dx, dy, dx, 0d, 0d, 0d, 0x40FFFFFF, 15);
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			//private static final String ID_KEY = "JitonSandShieldEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (((RangedItem)block).getTotalBalls(stack) < 20) {
					Entity entity1 = new EntityScorchBall(entity);
					entity.world.spawnEntity(entity1);
					((RangedItem)block).saveSpawnedBall(stack, entity1);
					return true;
				}
				return false;
			}
		}
	}

	public static class SuperSteamBlast implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			int j = ((RangedItem)block).getTotalBalls(stack);
			for (int i = 0; i < j; i++) {
				EntityScorchBall entity1 = ((RangedItem)block).get1stBallAndPutLast(entity.world, stack);
				if (entity1 != null) {
					entity1.setMaxScale(i == 0 ? 0.5f * j : 0f);
				}
			}
			((RangedItem)block).clearBalls(stack);
			return j > 0;
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
			RenderingRegistry.registerEntityRenderingHandler(EntityScorchBall.class, renderManager -> {
				return new RenderCustom(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityScorchBall> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/fireball2.png");
	
			public RenderCustom(RenderManager renderManager) {
				super(renderManager);
				shadowSize = 0.1f;
			}
	
			@Override
			public void doRender(EntityScorchBall entity, double x, double y, double z, float entityYaw, float partialTicks) {
				GlStateManager.pushMatrix();
				this.bindEntityTexture(entity);
				float scale = entity.getEntityScale();
				GlStateManager.translate(x, y + 0.5d * scale, z);
				GlStateManager.enableRescaleNormal();
				GlStateManager.scale(scale, scale, scale);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				GlStateManager.rotate(180F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(9f * (partialTicks + entity.ticksExisted), 0.0F, 0.0F, 1.0F);
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
			protected ResourceLocation getEntityTexture(EntityScorchBall entity) {
				return this.texture;
			}
		}
	}
}
