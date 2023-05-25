package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.Minecraft;

import net.narutomod.procedure.ProcedureRenderView;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.PlayerTracker;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEightTrigrams extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 56;
	public static final int ENTITYID_RANGED = 57;
	private static final int VIEW_DISTANCE = 2;
	
	public EntityEightTrigrams(ElementsNarutomodMod instance) {
		super(instance, 260);
	}

	@Override
	public void initElements() {
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "eighttrigramsentity"), ENTITYID).name("eighttrigramsentity")
		 .tracker(64, 1, true).build());
	}

	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityCustom.AttackHook());
	}
	
	public static class EntityCustom extends Entity implements ProcedureSync.RenderDistance.IHandler {
		private static final String JUTSUACTIVEKEY = "HakkeRokujuuyonshouActive";
		private EntityLivingBase ownerPlayer;
		private boolean canDie;
		private final Map<EntityPlayer, Integer> pMap = Maps.newHashMap();
		public final double effectRadius;
		public final int effectDuration;
		
		public EntityCustom(World world) {
			super(world);
			this.setSize(0.1F, 0.1F);
			this.isImmuneToFire = true;
			this.ignoreFrustumCheck = true;
			this.setEntityInvulnerable(true);
			this.canDie = false;
			this.ownerPlayer = null;
			this.effectRadius = 16d;
			this.effectDuration = 240;
		}

		public EntityCustom(EntityLivingBase userIn) {
			this(userIn.world);
			this.setOwnerPlayer(userIn);
			this.setLocationAndAngles(userIn.posX, userIn.posY, userIn.posZ, 0.0f, 0.0f);
		}

		@Override
		protected void entityInit() {
		}

		public EntityLivingBase getOwnerPlayer() {
			return this.ownerPlayer;
		}

		public void setOwnerPlayer(EntityLivingBase player) {
			this.ownerPlayer = player;
			player.getEntityData().setBoolean(JUTSUACTIVEKEY, true);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return false;
		}

		@Override
		public void setDead() {
			if (this.canDie) {
				if (this.ownerPlayer != null) {
					this.ownerPlayer.getEntityData().removeTag(JUTSUACTIVEKEY);
				}
				super.setDead();
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				if (this.ticksExisted == 2) {
					//ProcedureRenderView.setFogColor(this, this.effectRadius, true, 0.0F, 0.0F, 0.0F);
					for (EntityPlayer player : this.world.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().grow(this.effectRadius))) {
						if (!this.pMap.containsKey(player)) {
							ProcedureRenderView.changeFog(player, 1, 100, 0, 0.0F, 0.0F, 0.0F, 0.0F);
							ProcedureSync.RenderDistance.sendToSelf((EntityPlayerMP)player, VIEW_DISTANCE, this);
						}
					}
				}
				if (this.ticksExisted > 3 && this.ticksExisted < 20) {
					ProcedureAoeCommand.set(this, 0.0D, this.effectRadius).exclude(this.ownerPlayer).effect(MobEffects.SLOWNESS, 15, 4)
					 .effect(MobEffects.WEAKNESS, 15, 255).effect(MobEffects.MINING_FATIGUE, 15, 5);
				}
				if (this.ownerPlayer instanceof EntityPlayer) {
					((EntityPlayer)this.ownerPlayer).sendStatusMessage(new TextComponentString(I18n.translateToLocal("tooltip.byakugan.jutsu2")), true);
					if (this.ticksExisted % 40 == 4) {
						this.ownerPlayer.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 
						 50, (int)(PlayerTracker.getNinjaLevel((EntityPlayer)this.ownerPlayer) + this.ticksExisted) / 30));
						this.ownerPlayer.addPotionEffect(new PotionEffect(MobEffects.HASTE, 50, 3));
					}
				}
				if (this.ticksExisted == 100) {
					//ProcedureRenderView.setFogColor(this, 128.0D, false, 0.0F, 0.0F, 0.0F);
					for (Map.Entry<EntityPlayer, Integer> entry : this.pMap.entrySet()) {
						ProcedureSync.RenderDistance.sendToSelf((EntityPlayerMP)entry.getKey(), entry.getValue(), null);
		            }
				}
			}
			if (this.ticksExisted > this.effectDuration
			 || (!this.world.isRemote && (this.ownerPlayer == null || !this.ownerPlayer.isEntityAlive()))) {
				this.canDie = true;
				this.setDead();
			}
		}

		private void resetRenderDistance(EntityPlayer player) {
			if (!this.world.isRemote && this.pMap.containsKey(player)) {
				ProcedureSync.RenderDistance.sendToSelf((EntityPlayerMP)player, this.pMap.get(player), null);
				this.pMap.remove(player);
			}
		}

		@Override
		public void handleClientPacket(EntityPlayer player, int oldChunkDistance) {
			this.pMap.put(player, oldChunkDistance);
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class AttackHook {
			@SubscribeEvent
			public void onAttacked(LivingHurtEvent event) {
				Entity source = event.getSource().getImmediateSource();
				if (source != null && source.getEntityData().getBoolean(JUTSUACTIVEKEY)) {
					Chakra.pathway(event.getEntityLiving()).consume(0.125f);
					event.getEntityLiving().getEntityData().setBoolean("TempData_disableKnockback", true);
				}
			}

			@SubscribeEvent
			public void onLivingDeath(LivingDeathEvent event) {
				Entity entity = event.getEntity();
				if (entity instanceof EntityPlayer) {
					EntityCustom jutsuEntity = (EntityCustom)entity.world.findNearestEntityWithinAABB(EntityCustom.class, entity.getEntityBoundingBox().grow(32d), entity);
					if (jutsuEntity != null) {
						jutsuEntity.resetRenderDistance((EntityPlayer)entity);
					}
				}
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderEightTrigrams(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderEightTrigrams extends Render<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/eight_trigrams.png");
			protected ModelBase mainModel;
			
			public RenderEightTrigrams(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.mainModel = new ModelEightTrigrams();
			}

			@Override
			public boolean shouldRender(EntityCustom livingEntity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.disableCull();
				GlStateManager.translate(x, y + 0.10000000149011612D, z - 0.4000000059604645D);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.mainModel.render(entity, 0.0F, 0.0F, partialTicks + entity.ticksExisted, 0.0F, 0.0F, 1.0F);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.disableAlpha();
				GlStateManager.enableCull();
				GlStateManager.popMatrix();
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelEightTrigrams extends ModelBase {
			private final ModelRenderer bb_main;
			
			public ModelEightTrigrams() {
				this.textureWidth = 64;
				this.textureHeight = 16;
				this.bb_main = new ModelRenderer(this);
				this.bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 0, -8.0F, 0.0F, -8.0F, 16, 0, 16, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				this.bb_main.render(f5);
			}
		}
	}
}
