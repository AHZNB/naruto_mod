/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods, you can remove them as they
 * are overrides of the base class ElementsNarutomodMod.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list, it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor, the compilation will fail.
 *
 * If you want to make a plain independent class, create it in
 * "Workspace" -> "Source" menu.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 */
package net.narutomod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.Minecraft;

import net.narutomod.item.ItemOnBody;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class PlayerRender extends ElementsNarutomodMod.ModElement {
	private static final String CLONETARGETID = "SkinCloningTargetId";
	private static final String CLONETARGETLAYERS = "SkinCloningRenderTargetLayers";
	private static final String PLAYERTRANSPARENT = "PlayerRenderTransparent";
	private static final String COLORMULTIPLIER = "SkinColorMultiplier";
	private RenderPlayer playerRenderer;
	/**
	 * Do not remove this constructor
	 */
	public PlayerRender(ElementsNarutomodMod instance) {
		super(instance, 608);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
		this.playerRenderer = new Renderer(renderManager);
		try {
			Field skinMap = ProcedureUtils.getFieldByIndex(renderManager.getClass(), RenderManager.class, 1);
			if (skinMap.getType() == Map.class) {
				Map<String, RenderPlayer> map = (Map<String, RenderPlayer>)skinMap.get(renderManager);
				map.put("default", this.playerRenderer);
				map.put("slim", new Renderer(renderManager, true));
			}
			Field renderer = ProcedureUtils.getFieldByIndex(renderManager.getClass(), RenderManager.class, 2);
			if (renderer.getType() == RenderPlayer.class) {
				renderer.set(renderManager, this.playerRenderer);
			}
		} catch (Exception e) {
			throw new RuntimeException("RenderManager hook");
		}
	}

	public static void setSkinCloneTarget(EntityPlayer entity, @Nullable EntityPlayer target) {
		setSkinCloneTarget(entity, target, true);
	}

	public static void setSkinCloneTarget(EntityPlayer entity, @Nullable EntityPlayer target, boolean renderLayers) {
		if (target != null) {
			ProcedureSync.EntityNBTTag.setAndSync(entity, CLONETARGETID, target.getEntityId());
			ProcedureSync.EntityNBTTag.setAndSync(entity, CLONETARGETLAYERS, renderLayers);
		} else {
			ProcedureSync.EntityNBTTag.removeAndSync(entity, CLONETARGETID);
			ProcedureSync.EntityNBTTag.removeAndSync(entity, CLONETARGETLAYERS);
		}
	}

	@Nullable
	public static boolean hasSkinCloneTarget(EntityPlayer entity) {
		return entity.getEntityData().hasKey(CLONETARGETID);
	}

	public static void setTransparent(EntityPlayer entity, boolean set) {
		if (set) {
			//entity.getEntityData().setBoolean(PLAYERTRANSPARENT, true);
			ProcedureSync.EntityNBTTag.setAndSync(entity, PLAYERTRANSPARENT, true);
		} else {
			//entity.getEntityData().removeTag(PLAYERTRANSPARENT);
			ProcedureSync.EntityNBTTag.removeAndSync(entity, PLAYERTRANSPARENT);
		}
	}

	public static boolean isTransparent(EntityPlayer entity) {
		return entity.getEntityData().getBoolean(PLAYERTRANSPARENT);
	}

	public static void setColorMultiplier(EntityPlayer entity, int color) {
		if ((color >> 24 & 0xFF) == 0) {
			//entity.getEntityData().removeTag(COLORMULTIPLIER);
			ProcedureSync.EntityNBTTag.removeAndSync(entity, COLORMULTIPLIER);
		} else {
			//entity.getEntityData().setInteger(COLORMULTIPLIER, color);
			ProcedureSync.EntityNBTTag.setAndSync(entity, COLORMULTIPLIER, color);
		}
	}

	public static int getColorMultiplier(EntityPlayer entity) {
		return entity.getEntityData().hasKey(COLORMULTIPLIER) ? entity.getEntityData().getInteger(COLORMULTIPLIER) : 0;
	}

	@SideOnly(Side.CLIENT)
	public class Renderer extends RenderPlayer {
		public Renderer(RenderManager renderManager) {
			this(renderManager, false);
		}

		public Renderer(RenderManager renderManager, boolean useSmallArms) {
			super(renderManager, useSmallArms);
			Iterator iter = this.layerRenderers.iterator();
			while (iter.hasNext()) {
				LayerRenderer renderer = (LayerRenderer)iter.next();
				if (renderer instanceof LayerBipedArmor) {
					iter.remove();
				}
			}
			this.addLayer(new LayerArmorCustom(this));
			this.addLayer(new LayerInventoryItem(this));
		}

		@Override
		protected void renderModel(AbstractClientPlayer entityIn, float f0, float f1, float f2, float f3, float f4, float f5) {
			if (isTransparent(entityIn)) {
				if (this.bindEntityTexture(entityIn)) {
					GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
					this.mainModel.render(entityIn, f0, f1, f2, f3, f4, f5);
					GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
				}
			} else {
				ModelBiped model = this.getMainModel();
				if (shouldNarutoRun(entityIn) && model.swingProgress == 0.0f
				 && model.rightArmPose == ModelBiped.ArmPose.EMPTY && model.leftArmPose == ModelBiped.ArmPose.EMPTY) {
					model.bipedRightArm.showModel = false;
					model.bipedLeftArm.showModel = false;
					model.isSneak = true;
					super.renderModel(entityIn, f0, f1, f2, f3, f4, f5);
					model.bipedHead.showModel = false;
					model.bipedHeadwear.showModel = false;
					model.bipedBody.showModel = false;
					model.bipedRightLeg.showModel = false;
					model.bipedLeftLeg.showModel = false;
					model.bipedRightArm.showModel = true;
					model.bipedLeftArm.showModel = false;
					super.renderModel(entityIn, 4.7157f, 1.1345f, f2, f3, f4, f5);
					model.bipedRightArm.showModel = false;
					model.bipedLeftArm.showModel = true;
					super.renderModel(entityIn, 0.0f, 1.1345f, f2, f3, f4, f5);
					model.bipedHead.showModel = true;
					model.bipedHeadwear.showModel = true;
					model.bipedBody.showModel = true;
					model.bipedRightLeg.showModel = true;
					model.bipedLeftLeg.showModel = true;
					model.bipedRightArm.showModel = true;
					model.bipedLeftArm.showModel = true;
				} else {
					super.renderModel(entityIn, f0, f1, f2, f3, f4, f5);
				}
			}
		}

		@Override
		protected void renderLayers(AbstractClientPlayer entity, float f0, float f1, float f2, float f3, float f4, float f5, float f6) {
			if (!entity.isInvisible() || !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player)) {
				AbstractClientPlayer target = this.getSkinCloneTarget(entity);
				if (target != null && entity.getEntityData().getBoolean(CLONETARGETLAYERS)) {
					if (target.isSneaking()) {
						GlStateManager.translate(0.0F, -0.2F, 0.0F);
					}
					if (entity.isSneaking()) {
						GlStateManager.translate(0.0F, 0.2F, 0.0F);
					}
				} else {
					target = entity;
				}
				super.renderLayers(target, f0, f1, f2, f3, f4, f5, f6);
			}
		}

		@Override
		public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
			AbstractClientPlayer target = this.getSkinCloneTarget(entity);
			return target != null ? target.getLocationSkin() : super.getEntityTexture(entity);
		}

		@Override
		protected void renderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq) {
			if (entityIn.getAlwaysRenderNameTag()) {
				AbstractClientPlayer target = this.getSkinCloneTarget(entityIn);
				if (target != null) {
					super.renderEntityName(entityIn, x, y, z, target.getName(), distanceSq);
				} else {
					super.renderEntityName(entityIn, x, y, z, name, distanceSq);
				}
			}
		}

		@Override
		protected int getColorMultiplier(AbstractClientPlayer entityIn, float lightBrightness, float partialTickTime) {
			int color = PlayerRender.getColorMultiplier(entityIn);
			if ((color >> 24 & 0xFF) > 0) {
				return color;
			}
			return super.getColorMultiplier(entityIn, lightBrightness, partialTickTime);
		}

		@Nullable
		private AbstractClientPlayer getSkinCloneTarget(Entity entity) {
			if (entity.getEntityData().hasKey(CLONETARGETID)) {
				Entity target = this.renderManager.world.getEntityByID(entity.getEntityData().getInteger(CLONETARGETID));
				if (target instanceof AbstractClientPlayer) {
					return (AbstractClientPlayer)target;
				} else {
					entity.getEntityData().removeTag(CLONETARGETID);
				}
			}
			return null;
		}
	}

	@SideOnly(Side.CLIENT)
	public static class LayerInventoryItem implements LayerRenderer<AbstractClientPlayer> {
		private final RenderPlayer playerRenderer;
		private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.<String, ResourceLocation>newHashMap();

		public LayerInventoryItem(RenderPlayer playerRendererIn) {
			this.playerRenderer = playerRendererIn;
		}

		@Override
		public void doRenderLayer(AbstractClientPlayer entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			if (!entityIn.isSpectator()) {
				for (int i = 0; i < entityIn.inventory.mainInventory.size(); i++) {
					ItemStack stack = entityIn.inventory.mainInventory.get(i);
					if (stack.getItem() instanceof ItemOnBody.Interface) {
						ItemOnBody.Interface item = (ItemOnBody.Interface)stack.getItem();
						if (item.showSkinLayer()) {
							this.renderSkinLayer(stack, entityIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
						}
						if (item.showOnBody() != ItemOnBody.BodyPart.NONE && i != entityIn.inventory.currentItem) {
							this.renderItemOnBody(stack, entityIn, item.showOnBody());
						}
					}
				}
			}
		}

		private void renderSkinLayer(ItemStack stack, AbstractClientPlayer entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			ModelBiped model = stack.getItem().getArmorModel(entityIn, stack, EntityEquipmentSlot.HEAD, new ModelBiped(1.0F));
			if (model != null) {
				String s = stack.getItem().getArmorTexture(stack, entityIn, EntityEquipmentSlot.HEAD, null);
				if (s != null) {
					ResourceLocation resourcelocation = (ResourceLocation)ARMOR_TEXTURE_RES_MAP.get(s);
					if (resourcelocation == null) {
						resourcelocation = new ResourceLocation(s);
						ARMOR_TEXTURE_RES_MAP.put(s, resourcelocation);
					}
					model.isSneak = this.playerRenderer.getMainModel().isSneak;
					model.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
					this.playerRenderer.bindTexture(resourcelocation);
					model.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
				}
			}
		}

		private void renderItemOnBody(ItemStack stack, AbstractClientPlayer entityIn, ItemOnBody.BodyPart bodypart) {
			Vec3d offset = ((ItemOnBody.Interface)stack.getItem()).getOffset();
			GlStateManager.pushMatrix();
			ModelBiped model = this.playerRenderer.getMainModel();
			switch (bodypart) {
				case HEAD:
					model.bipedHead.postRender(0.0625F);
					break;
				case TORSO:
					model.bipedBody.postRender(0.0625F);
					break;
				case RIGHT_ARM:
					model.bipedRightArm.postRender(0.0625F);
					break;
				case LEFT_ARM:
					model.bipedLeftArm.postRender(0.0625F);
					break;
				case RIGHT_LEG:
					model.bipedRightLeg.postRender(0.0625F);
					break;
				case LEFT_LEG:
					model.bipedLeftLeg.postRender(0.0625F);
					break;
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(offset.x, -0.25F + offset.y, offset.z);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.scale(0.625F, -0.625F, -0.625F);
			Minecraft.getMinecraft().getItemRenderer().renderItem(entityIn, stack, ItemCameraTransforms.TransformType.HEAD);
			GlStateManager.popMatrix();
		}

		@Override
		public boolean shouldCombineTextures() {
			return false;
		}
	}

	private static boolean shouldNarutoRun(EntityPlayer player) {
		return ModConfig.NARUTO_RUN && !player.capabilities.isFlying && !player.isRiding()
		 && player.getPositionVector().subtract(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ).lengthSquared() >= 0.125d;
	}

	@SideOnly(Side.CLIENT)
	public class LayerArmorCustom extends LayerBipedArmor {
		private final Renderer renderer;

		public LayerArmorCustom(Renderer rendererIn) {
			super(rendererIn);
			this.renderer = rendererIn;
		}

		@Override
		public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
	        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
	        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
	        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
	        this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);
		}

	    private void renderArmorLayer(EntityLivingBase entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn) {
	        ItemStack itemstack = entityIn.getItemStackFromSlot(slotIn);	
	        if (itemstack.getItem() instanceof ItemArmor) {
	            ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
	            if (itemarmor.getEquipmentSlot() == slotIn) {
	                ModelBiped t = this.getModelFromSlot(slotIn);
	                t = getArmorModelHook(entityIn, itemstack, slotIn, t);
	                ModelBiped wearerModel = (ModelBiped)this.renderer.getMainModel();
	                t.setModelAttributes(wearerModel);
	                t.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
	                this.setModelSlotVisible(t, slotIn);
	                this.renderer.bindTexture(this.getArmorResource(entityIn, itemstack, slotIn, null));
                    if (itemarmor.hasOverlay(itemstack)) { // Allow this for anything, not only cloth 
	                    int i = itemarmor.getColor(itemstack);
	                    float f = (float)(i >> 16 & 255) / 255.0F;
	                    float f1 = (float)(i >> 8 & 255) / 255.0F;
	                    float f2 = (float)(i & 255) / 255.0F;
	                    GlStateManager.color(f, f1, f2, 1.0F);
                        this.renderArmorModel(t, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	                    this.renderer.bindTexture(this.getArmorResource(entityIn, itemstack, slotIn, "overlay"));
	                }
                    { // Non-colored
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        this.renderArmorModel(t, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    } // Default
                    if (itemstack.hasEffect()) {
                        renderEnchantedGlint(this.renderer, entityIn, t, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
                    }
	            }
	        }
	    }

		private void renderArmorModel(ModelBiped model, Entity entityIn, float f0, float f1, float f2, float f3, float f4, float f5) {
			if (entityIn instanceof EntityPlayer && shouldNarutoRun((EntityPlayer)entityIn) && model.swingProgress == 0.0f
			 && model.rightArmPose == ModelBiped.ArmPose.EMPTY && model.leftArmPose == ModelBiped.ArmPose.EMPTY) {
				boolean showRightArm = model.bipedRightArm.showModel;
				boolean showLeftArm = model.bipedLeftArm.showModel;
				model.bipedRightArm.showModel = false;
				model.bipedLeftArm.showModel = false;
				model.isSneak = true;
				model.render(entityIn, f0, f1, f2, f3, f4, f5);
				if (showLeftArm || showRightArm) {
					model.bipedHead.showModel = false;
					model.bipedHeadwear.showModel = false;
					model.bipedBody.showModel = false;
					model.bipedRightLeg.showModel = false;
					model.bipedLeftLeg.showModel = false;
					if (showRightArm) {
						model.bipedRightArm.showModel = true;
						model.bipedLeftArm.showModel = false;
						model.render(entityIn, 4.7157f, 1.1345f, f2, f3, f4, f5);
					}
					if (showLeftArm) {
						model.bipedRightArm.showModel = false;
						model.bipedLeftArm.showModel = true;
						model.render(entityIn, 0.0f, 1.1345f, f2, f3, f4, f5);
					}
				}
			} else {
				model.render(entityIn, f0, f1, f2, f3, f4, f5);
			}
		}
	}
}
