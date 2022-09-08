
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.Particles;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTenseiBakuSilver extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 340;
	public static final int ENTITYID_RANGED = 341;

	public EntityTenseiBakuSilver(ElementsNarutomodMod instance) {
		super(instance, 696);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "tensei_baku_silver"), ENTITYID).name("tensei_baku_silver").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
	}

	public static class EC extends Entity {
		private static final DataParameter<Integer> USERID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private final AirPunch airPunch = new AirPunch();
		private float power;
		private final int growTime = 20;
		private int duration;

		public EC(World w) {
			super(w);
			this.setSize(2.0f, 2.0f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, float powerIn) {
			this(userIn.world);
			this.setUser(userIn);
			this.power = powerIn;
			this.duration = (int)powerIn * 4 + this.growTime;
			this.setIdlePosition();
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
			EntityLivingBase user = this.getUser();
			if (user != null) {
				this.setIdlePosition();
				if (this.ticksExisted == 1) {
					ProcedureSync.EntityNBTTag.setAndSync(user, NarutomodModVariables.forceBowPose, true);
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:tenseiblastcharge"))), 2.0f, 1.0f);
				} else if (this.ticksExisted >= this.growTime) {
					if (this.ticksExisted % 40 == this.growTime) {
						Vec3d vec = user.getLookVec().scale(this.power * 0.5f).add(this.getPositionVector());
						this.world.playSound(null, vec.x, vec.y, vec.z,
						 SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:wind"))), SoundCategory.NEUTRAL, 4.0f, 1.0f);
					}
					this.airPunch.execute(user, this.power, this.power * 0.1d);
				}
			}
			if (!this.world.isRemote && this.ticksExisted > this.duration) {
				this.setDead();
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase user = this.getUser();
				if (user != null) {
					ProcedureSync.EntityNBTTag.removeAndSync(user, NarutomodModVariables.forceBowPose);
				}
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public class AirPunch extends ProcedureAirPunch {
			private boolean griefing = true;
			
			public AirPunch() {
				this.blockHardnessLimit = 1.0f;
				this.particlesDuring = null;
			}

			@Override
			public void execute(EntityLivingBase player, double range, double radius) {
				this.griefing = world.getGameRules().getBoolean("mobGriefing");
				super.execute(player, range, radius);
			}

			@Override
			protected void preExecuteParticles(EntityLivingBase player) {
				Vec3d vec0 = player.getLookVec();
				Vec3d vec = vec0.scale(2d).addVector(player.posX, player.posY + 1.5d, player.posZ);
				for (int i = 1; i <= 50; i++) {
					Vec3d vec1 = vec0.scale((player.getRNG().nextDouble()*0.8d+0.2d) * this.getRange(0) * 0.125d);
					Particles.spawnParticle(player.world, Particles.Types.SMOKE, vec.x, vec.y, vec.z, 1, 0d, 0d, 0d, 
					 vec1.x + (player.getRNG().nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 vec1.y + (player.getRNG().nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 vec1.z + (player.getRNG().nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 0x80C0C0C0, 80 + player.getRNG().nextInt(20), (int)(16.0D / (player.getRNG().nextDouble()*0.8D+0.2D)));
				}
			}

			@Override
			protected void attackEntityFrom(EntityLivingBase player, Entity target) {
				ProcedureUtils.pushEntity(player, target, this.getRange(0), target instanceof EntityFallingBlock ? 1.0f : 2.0f);
			}

			@Override
			protected EntityItem processAffectedBlock(EntityLivingBase player, BlockPos pos, EnumFacing facing) {
				if (this.griefing && player.getRNG().nextInt(10) == 0) {
					Entity falling = new EntityFallingBlock(player.world, 0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ(), player.world.getBlockState(pos)) {
						{
							this.shouldDropItem = false;
						}
						@Override
						public boolean canBePushed() {
							return !this.isDead;
						}
						@Override
						public void onUpdate() {
							super.onUpdate();
							if (this.fallTime == 5 || ProcedureUtils.getVelocity(this) > 0.1d) {
								this.setNoGravity(false);
							}
						}
					};
					falling.setNoGravity(true);
					player.world.spawnEntity(falling);
				}
				return null;
			}

			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				return 0.0f;
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.spawnEntity(new EC(entity, power));
				return true;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EC> {
		private final ResourceLocation TEXTURE = new ResourceLocation("narutomod:textures/ring_green.png");
		private final ResourceLocation TEXTURE2 = new ResourceLocation("narutomod:textures/white_orb.png");

		public RenderCustom(RenderManager renderManager) {
			super(renderManager);
			shadowSize = 0.1f;
		}

		@Override
		public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
			this.bindEntityTexture(entity);
			float ageInTicks = (float)entity.ticksExisted + partialTicks;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 0.5d, z);
			GlStateManager.enableRescaleNormal();
			float alpha = ageInTicks / (float)entity.growTime;
			if (alpha > 1.0F) {
				alpha = Math.max(1.0F - (alpha - 1.0F) * 0.5F, 0.0F);
			}
			GlStateManager.disableCull();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			
			GlStateManager.pushMatrix();
			GlStateManager.scale(3.0F, 3.0F, 3.0F);
			GlStateManager.rotate(-entity.rotationYaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(9f * ageInTicks, 0.0F, 0.0F, 1.0F);
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
			bufferbuilder.pos(-0.5D, -0.5D, 0.0D).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, -0.5D, 0.0D).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, 0.5D, 0.0D).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(-0.5D, 0.5D, 0.0D).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
			
			this.bindTexture(TEXTURE2);
			alpha = MathHelper.sqrt(1.0F - Math.min(ageInTicks / (float)entity.growTime, 1.0F));
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.rotate(180F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(9f * ageInTicks, 0.0F, 0.0F, 1.0F);
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
			bufferbuilder.pos(-0.5D, -0.5D, 0.0D).tex(0.0D, 1.0D).color(0.0F, 0.0F, 0.0F, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, -0.5D, 0.0D).tex(1.0D, 1.0D).color(0.0F, 0.0F, 0.0F, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, 0.5D, 0.0D).tex(1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(-0.5D, 0.5D, 0.0D).tex(0.0D, 0.0D).color(0.0F, 0.0F, 0.0F, alpha).normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();
			
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.enableCull();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return TEXTURE;
		}
	}
}
