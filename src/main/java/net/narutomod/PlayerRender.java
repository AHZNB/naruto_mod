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

	private static boolean shouldNarutoRun(EntityPlayer player) {
		return !player.capabilities.isFlying 
		 && player.getPositionVector().subtract(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ).lengthSquared() >= 0.125d;
	}

	@SideOnly(Side.CLIENT)
	public class Renderer extends RenderPlayer {
		public Renderer(RenderManager renderManager) {
			this(renderManager, false);
		}

		public Renderer(RenderManager renderManager, boolean useSmallArms) {
			super(renderManager, useSmallArms);
			//this.mainModel = new ModelPlayerCustom(0.0F, useSmallArms);
			this.addLayer(new LayerInventoryItem(this));
			/*Iterator iter = this.layerRenderers.iterator();
			while (iter.hasNext()) {
				LayerRenderer renderer = (LayerRenderer)iter.next();
				if (renderer instanceof net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder) {
					iter.remove();
				}
			}
			this.addLayer(new LayerEntityOnShoulder(renderManager));*/
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
				//boolean flag = shouldNarutoRun(entityIn);
				//if (flag) {
				//	this.doNarutoRunPre();
				//}
				super.renderModel(entityIn, f0, f1, f2, f3, f4, f5);
				//if (flag) {
				//	this.doNarutoRunPost(f5);
				//}
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

		private void doNarutoRunPre() {
			ModelBiped model = this.getMainModel();
			model.bipedRightArm.showModel = false;
			model.bipedLeftArm.showModel = false;
		}

		private void doNarutoRunPost(float scale) {
			ModelBiped model = this.getMainModel();
			model.bipedRightArm.showModel = true;
			model.bipedLeftArm.showModel = true;
			model.bipedRightArm.rotateAngleX = 1.3963f;
			model.bipedLeftArm.rotateAngleX = 1.3963f;
			model.bipedRightArm.render(scale);
			model.bipedLeftArm.render(scale);
		}
		
		@Override
		protected void applyRotations(AbstractClientPlayer entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
			super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
			if (shouldNarutoRun(entityLiving)) {
				this.getMainModel().isSneak = true;
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

	/*@SideOnly(Side.CLIENT)
	public static class LayerEntityOnShoulder implements LayerRenderer<EntityPlayer> {
	    private final RenderManager renderManager;
	    private DataHolder dataHolderLeft;
	    private DataHolder dataHolderRight;
	
	    public LayerEntityOnShoulder(RenderManager renderManagerIn) {
	        this.renderManager = renderManagerIn;
	    }

	    @Nullable
	    private DataHolder getShoulderEntityData(World world, NBTTagCompound compound, @Nullable DataHolder data) {
	    	UUID uuid = compound.getUniqueId("UUID");
	    	if (data == null || !data.entity.getUniqueID().equals(uuid)) {
	    		Entity entity = ProcedureUtils.getEntityFromUUID(world, uuid);
	    		if (entity instanceof EntityLivingBase) {
	    			RenderLivingBase renderer = (RenderLivingBase)this.renderManager.getEntityRenderObject(entity);
	    			ModelBase model = renderer.getMainModel();
	    			ResourceLocation resource = (ResourceLocation)ProcedureUtils.invokeMethodByParameters(renderer, ResourceLocation.class, entity);
	    			return new DataHolder((EntityLivingBase)entity, renderer, model, resource);
	    		}
	    		return null;
	    	} else {
	    		return data;
	    	}
	    }
	
	    public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
	        if (entitylivingbaseIn.getLeftShoulderEntity() != null || entitylivingbaseIn.getRightShoulderEntity() != null) {
	            GlStateManager.enableRescaleNormal();
	            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	            NBTTagCompound nbttagcompound = entitylivingbaseIn.getLeftShoulderEntity();
	            if (!nbttagcompound.hasNoTags()) {
	            	DataHolder dataholder = this.getShoulderEntityData(entitylivingbaseIn.world, nbttagcompound, this.dataHolderLeft);
	            	if (dataholder != null) {
	                	this.renderEntityOnShoulder(entitylivingbaseIn, dataholder, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, true);
	                	this.dataHolderLeft = dataholder;
	            	}
	            }
	            NBTTagCompound nbttagcompound1 = entitylivingbaseIn.getRightShoulderEntity();
	            if (!nbttagcompound1.hasNoTags()) {
	            	DataHolder dataholder = this.getShoulderEntityData(entitylivingbaseIn.world, nbttagcompound1, this.dataHolderRight);
	            	if (dataholder != null) {
	                	this.renderEntityOnShoulder(entitylivingbaseIn, dataholder, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, false);
	                	this.dataHolderRight = dataholder;
	            	}
	            }
	            GlStateManager.disableRescaleNormal();
	        }
	    }
	
	    private void renderEntityOnShoulder(EntityPlayer player, DataHolder data, float p_192864_8_, float p_192864_9_, float p_192864_10_, float p_192864_11_, float p_192864_12_, float p_192864_13_, float p_192864_14_, boolean p_192864_15_) {
	        data.renderer.bindTexture(data.textureLocation);
	        GlStateManager.pushMatrix();
	        float f = player.isSneaking() ? -1.3F : -1.5F;
	        float f1 = p_192864_15_ ? 0.4F : -0.4F;
	        GlStateManager.translate(f1, f, 0.0F);
	        if (data.entity instanceof net.minecraft.entity.passive.EntityParrot) {
	            p_192864_11_ = 0.0F;
	            p_192864_9_ = 0.0F;
	        }
	        data.model.setLivingAnimations(player, p_192864_8_, p_192864_9_, p_192864_10_);
	        data.model.setRotationAngles(p_192864_8_, p_192864_9_, p_192864_11_, p_192864_12_, p_192864_13_, p_192864_14_, player);
	        data.model.render(player, p_192864_8_, p_192864_9_, p_192864_11_, p_192864_12_, p_192864_13_, p_192864_14_);
	        GlStateManager.popMatrix();
	    }
	
	    public boolean shouldCombineTextures() {
	        return false;
	    }
	
	    @SideOnly(Side.CLIENT)
	    class DataHolder {
	        public EntityLivingBase entity;
	        public RenderLivingBase <? extends EntityLivingBase > renderer;
	        public ModelBase model;
	        public ResourceLocation textureLocation;
	
	        public DataHolder(EntityLivingBase entityIn, RenderLivingBase <? extends EntityLivingBase > p_i47463_3_, ModelBase p_i47463_4_, ResourceLocation p_i47463_5_) {
	            this.entity = entityIn;
	            this.renderer = p_i47463_3_;
	            this.model = p_i47463_4_;
	            this.textureLocation = p_i47463_5_;
	        }
	    }
	}

	@SideOnly(Side.CLIENT)
	public class ModelPlayerCustom extends ModelPlayer {
		public ModelPlayerCustom(float modelSize, boolean smallArmsIn) {
			super(modelSize, smallArmsIn);
		}

		@Override
		public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
			super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
			if (shouldNarutoRun((EntityPlayer)entityIn)) {
				this.bipedRightArm.rotateAngleX = 1.3963f;
				this.bipedLeftArm.rotateAngleX = 1.3963f;
			}
		}
	}*/
}
