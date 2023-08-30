
package net.narutomod.entity;

import net.narutomod.item.ItemKunaiHiraishin;
import net.narutomod.item.ItemKunai3prong;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector2f;
import com.google.common.collect.Maps;
import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
//import net.minecraft.client.renderer.culling.Frustum;
//import net.minecraft.client.renderer.culling.ICamera;

@ElementsNarutomodMod.ModElement.Tag
public class EntityHiraishin extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 419;
	public static final int ENTITYID_RANGED = 420;
	private static final Map<UUID, Map<UUID, Vector4d>> serverMarkerMap = Maps.newHashMap();
	private static final Map<UUID, Vector4d> clientMarkerList = Maps.newHashMap();

	public EntityHiraishin(ElementsNarutomodMod instance) {
		super(instance, 841);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "hiraishin"), ENTITYID).name("hiraishin").tracker(64, 3, true).build());
	}

	public static void updateServerMarkerMap(UUID ownerUuid, UUID kunaiUuid, @Nullable Vector4d vec4d) {
		if (vec4d == null) {
			if (serverMarkerMap.containsKey(ownerUuid)) {
				Map<UUID, Vector4d> map = serverMarkerMap.get(ownerUuid);
				map.remove(kunaiUuid);
				if (map.isEmpty()) {
					serverMarkerMap.remove(ownerUuid);
				}
			}
		} else if (!serverMarkerMap.containsKey(ownerUuid)) {
			Map<UUID, Vector4d> map = Maps.newHashMap();
			map.put(kunaiUuid, vec4d);
			serverMarkerMap.put(ownerUuid, map);
		} else {
			serverMarkerMap.get(ownerUuid).put(kunaiUuid, vec4d);
		}
		EntityPlayerMP owner = ProcedureUtils.getPlayerMatchingUuid(ownerUuid);
		if (owner != null) {
			UpdateMarkerMessage.sendToPlayer(owner, kunaiUuid, vec4d);
		}
	}

	private static void removeAllMarkersFrom(EntityPlayerMP owner) {
		if (serverMarkerMap.containsKey(owner.getUniqueID())) {
			serverMarkerMap.remove(owner.getUniqueID());
			UpdateMarkerMessage.clearClientMarkers(owner);
		}
	}

	public static boolean canUseJutsu(EntityPlayer player) {
		ItemStack stack = ProcedureUtils.getMatchingItemStack(player, ItemNinjutsu.block);
		return stack != null && ((ItemNinjutsu.RangedItem)stack.getItem())
		 .canActivateJutsu(stack, ItemNinjutsu.HIRAISHIN, player) == EnumActionResult.SUCCESS;
	}

	public static class EC extends Entity {
		private static final DataParameter<Optional<UUID>> TARGET_UUID = EntityDataManager.<Optional<UUID>>createKey(EC.class, DataSerializers.OPTIONAL_UNIQUE_ID);
		private static final DataParameter<Float> OFFSET_X = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> OFFSET_Y = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> OFFSET_Z = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> OFFSET_YAW = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> OFFSET_PITCH = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private UUID userUuid;

		public EC(World world) {
			super(world);
			this.setSize(0.6f, 0.05f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, RayTraceResult res) {
			this(userIn.world);
			this.userUuid = userIn.getUniqueID();
			if (res.sideHit.getHorizontalIndex() == -1) {
				this.rotationYaw = 90f * EnumFacing.fromAngle(userIn.rotationYaw).getHorizontalIndex();
				this.rotationPitch = -90f * (res.sideHit.getDirectionVec().getY() - 1);
			} else {
				this.rotationYaw = 90f * res.sideHit.getOpposite().getHorizontalIndex();
				this.rotationPitch = -90f;
			}
			this.setLocationAndAngles(res.hitVec.x, res.hitVec.y, res.hitVec.z, this.rotationYaw, this.rotationPitch);
			if (res.entityHit != null) {
				this.setTargetUuid(res.entityHit.getUniqueID());
				float yaw = res.entityHit instanceof EntityLivingBase ? ((EntityLivingBase)res.entityHit).renderYawOffset : res.entityHit.rotationYaw;
				this.setOffsets(res.hitVec.x - res.entityHit.posX, res.hitVec.y - res.entityHit.posY, res.hitVec.z - res.entityHit.posZ,
				 MathHelper.wrapDegrees(this.rotationYaw - yaw), this.rotationPitch);
			}
		}

		@Override
		protected void entityInit() {
			this.dataManager.register(TARGET_UUID, Optional.absent());
			this.dataManager.register(OFFSET_X, Float.valueOf(0.0f));
			this.dataManager.register(OFFSET_Y, Float.valueOf(0.0f));
			this.dataManager.register(OFFSET_Z, Float.valueOf(0.0f));
			this.dataManager.register(OFFSET_YAW, Float.valueOf(0.0f));
			this.dataManager.register(OFFSET_PITCH, Float.valueOf(0.0f));
		}

		private void setTargetUuid(@Nullable UUID uuid) {
			this.dataManager.set(TARGET_UUID, Optional.fromNullable(uuid));
		}

		@Nullable
		public UUID getTargetUuid() {
			return (UUID)((Optional)this.dataManager.get(TARGET_UUID)).orNull();
		}

		@Nullable
		public Entity getTarget() {
			UUID uuid = this.getTargetUuid();
			return uuid != null ? ProcedureUtils.getEntityFromUUID(this.world, uuid) : null;
		}

		private void setOffsets(double x, double y, double z, float yaw, float pitch) {
			this.dataManager.set(OFFSET_X, Float.valueOf((float)x));
			this.dataManager.set(OFFSET_Y, Float.valueOf((float)y));
			this.dataManager.set(OFFSET_Z, Float.valueOf((float)z));
			this.dataManager.set(OFFSET_YAW, Float.valueOf(yaw));
			this.dataManager.set(OFFSET_PITCH, Float.valueOf(pitch));
		}

		private Vec3d getOffsetVec() {
			return new Vec3d( ((Float)this.getDataManager().get(OFFSET_X)).floatValue(), 
			                  ((Float)this.getDataManager().get(OFFSET_Y)).floatValue(), 
			                  ((Float)this.getDataManager().get(OFFSET_Z)).floatValue() );
		}

		private Vector2f getOffsetRotations() {
			return new Vector2f(((Float)this.getDataManager().get(OFFSET_YAW)).floatValue(), 
			                    ((Float)this.getDataManager().get(OFFSET_PITCH)).floatValue() );
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.userUuid != null) {
				updateServerMarkerMap(this.userUuid, this.getUniqueID(), null);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.userUuid != null) {
				if (((WorldServer)this.world).getEntityFromUuid(this.userUuid) != null) {
					boolean update = false;
					UUID targetUuid = this.getTargetUuid();
					if (targetUuid == null) {
						if (this.ticksExisted == 1) {
							update = true;
						}
					} else {
						Entity target = ((WorldServer)this.world).getEntityFromUuid(targetUuid);
						if (target != null) {
							if (target.isEntityAlive()) {
								this.setPosition(target.posX, target.posY, target.posZ);
								update = true;
							} else {
								this.setDead();
							}
						}
					}
					if (update) {
						updateServerMarkerMap(this.userUuid, this.getUniqueID(), new Vector4d(this.posX, this.posY, this.posZ, this.dimension));
					}
				}
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			if (compound.hasUniqueId("userUuid")) {
				this.userUuid = compound.getUniqueId("userUuid");
			}
			if (compound.hasUniqueId("targetUuid")) {
				this.setTargetUuid(compound.getUniqueId("targetUuid"));
				this.setOffsets(compound.getFloat("offsetX"), compound.getFloat("offsetY"), compound.getFloat("offsetZ"),
				 compound.getFloat("offsetYaw"), compound.getFloat("offsetPitch"));
			}
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			if (this.userUuid != null) {
				compound.setUniqueId("userUuid", this.userUuid);
			}
			UUID uuid = this.getTargetUuid();
			if (uuid != null) {
				compound.setUniqueId("targetUuid", uuid);
				Vec3d vec = this.getOffsetVec();
				Vector2f vec2 = this.getOffsetRotations();
				compound.setFloat("offsetX", (float)vec.x);
				compound.setFloat("offsetY", (float)vec.y);
				compound.setFloat("offsetZ", (float)vec.z);
				compound.setFloat("offsetYaw", vec2.x);
				compound.setFloat("offsetPitch", vec2.y);
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 4d, true);
				if (res != null && res.typeOfHit != RayTraceResult.Type.MISS) {
					if (res.entityHit instanceof EC) {
						res.entityHit.setDead();
					} else if (res.entityHit instanceof EntityItem
					 && ((EntityItem)res.entityHit).getItem().getItem() == ItemKunai3prong.block) {
						ItemStack stack1 = new ItemStack(ItemKunaiHiraishin.block, 1);
						ItemKunaiHiraishin.RangedItem.setOwner(stack1, entity);
						((EntityItem)res.entityHit).setItem(stack1);
					} else if (res.typeOfHit == RayTraceResult.Type.BLOCK) {
						return entity.world.spawnEntity(new EC(entity, res));
					} else if (res.entityHit instanceof EntityLivingBase) {
						EC entity1 = (EC)entity.world.findNearestEntityWithinAABB(EC.class, res.entityHit.getEntityBoundingBox(), res.entityHit);
						if (entity1 != null && entity1.getDistanceSq(res.entityHit) < 0.01d) {
							entity1.setDead();
						} else {
							return entity.world.spawnEntity(new EC(entity, res));
						}
					}
				}
				return false;
			}
		}
	}

	public static class UpdateMarkerMessage implements IMessage {
		String uuid;
		Vector4d vec;

		public UpdateMarkerMessage() {}

		public UpdateMarkerMessage(UUID id, @Nullable Vector4d v4d) {
			this.uuid = id.toString();
			this.vec = v4d;
		}

		public static void sendToPlayer(EntityPlayerMP entity, UUID id, @Nullable Vector4d v4d) {
			NarutomodMod.PACKET_HANDLER.sendTo(new UpdateMarkerMessage(id, v4d), entity);
		}

		public static void clearClientMarkers(EntityPlayerMP entity) {
			NarutomodMod.PACKET_HANDLER.sendTo(new UpdateMarkerMessage(), entity);
		}

		public static class Handler implements IMessageHandler<UpdateMarkerMessage, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(UpdateMarkerMessage message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					if (message.uuid == null) {
						clientMarkerList.clear();
					} else {
						UUID uuid = UUID.fromString(message.uuid);
						if (message.vec != null) {
							clientMarkerList.put(uuid, message.vec);
						} else {
							clientMarkerList.remove(uuid);
						}
					}
				});
				return null;
			}
		}
	
		public void toBytes(ByteBuf buf) {
			if (this.uuid != null) {
				buf.writeBoolean(true);
				ProcedureSync.writeString(buf, this.uuid);
			} else {
				buf.writeBoolean(false);
			}
			if (this.vec != null) {
				buf.writeBoolean(true);
				buf.writeDouble(this.vec.x);
				buf.writeDouble(this.vec.y);
				buf.writeDouble(this.vec.z);
				buf.writeDouble(this.vec.w);
			} else {
				buf.writeBoolean(false);
			}
		}
	
		public void fromBytes(ByteBuf buf) {
			this.uuid = buf.readBoolean() ? ProcedureSync.readString(buf) : null;
			this.vec = buf.readBoolean() ? new Vector4d(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble()) : null;
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
		this.elements.addNetworkMessage(UpdateMarkerMessage.Handler.class, UpdateMarkerMessage.class, Side.CLIENT);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(Renderer.instance);
	}

	public static class Renderer extends EntityRendererRegister {
		private static Renderer instance;
		@SideOnly(Side.CLIENT)
		private RenderCustom renderCustom;

		public Renderer() {
			instance = this;
		}
		
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> {
				this.renderCustom = new RenderCustom(renderManager);
				return this.renderCustom;
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/hiraishin_formula.png");
			protected final ModelBase mainModel;
			private final Item item;
			private final RenderItem itemRenderer;
			
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.mainModel = new ModelFormula();
				this.item = ItemKunaiHiraishin.block;
				this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
			}
	
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				Entity target = entity.getTarget();
				if (!this.renderManager.renderViewEntity.equals(target) || this.renderManager.options.thirdPersonView != 0) {
					float f = partialTicks + entity.ticksExisted;
					float entityPitch = entity.rotationPitch;
					if (target instanceof EntityLivingBase) {
						EntityLivingBase living = (EntityLivingBase)target;
						Vector2f vec2 = entity.getOffsetRotations();
						float targetYaw = ProcedureUtils.interpolateRotation(living.prevRenderYawOffset, living.renderYawOffset, partialTicks);
						Vec3d vec = entity.getOffsetVec().rotateYaw(-targetYaw * 0.0174533F);
						x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks + vec.x - this.renderManager.viewerPosX;
						y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks + vec.y - this.renderManager.viewerPosY;
						z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks + vec.z - this.renderManager.viewerPosZ;
						entityYaw = targetYaw + vec2.x;
						entityPitch = vec2.y;
					}
					this.bindEntityTexture(entity);
					GlStateManager.pushMatrix();
					GlStateManager.disableCull();
					GlStateManager.translate(x, y, z);
					GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.rotate(entityYaw, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(-entityPitch, 1.0F, 0.0F, 0.0F);
					GlStateManager.enableAlpha();
					GlStateManager.enableBlend();
					GlStateManager.disableLighting();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					GlStateManager.color(1.0F, 1.0F, 1.0F, Math.min(f / 40.0F, 1.0F));
					this.mainModel.render(entity, 0.0F, 0.0F, f, 0.0F, 0.0F, 0.0625F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.disableAlpha();
					GlStateManager.enableCull();
					GlStateManager.popMatrix();
				}
				//super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}

			protected void renderMarker(double x, double y, double z, float ageInTicks) {
				double d = MathHelper.sqrt(x * x + y * y + z * z);
				if (d > 2.0D) {
					x = x / d;
					y = y / d + 1.425D;
					z = z / d;
					this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					GlStateManager.pushMatrix();
					GlStateManager.translate(x, y, z);
					GlStateManager.scale(0.2F, 0.2F, 0.2F);
					GlStateManager.translate(0.0F, 0.8F, 0.0F);
					GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
            		GlStateManager.disableLighting();
					this.itemRenderer.renderItem(new ItemStack(this.item), ItemCameraTransforms.TransformType.GROUND);
            		GlStateManager.enableLighting();
					GlStateManager.popMatrix();
					this.renderText(""+(int)d, x, y, z);
				}
			}

		    private void renderText(String str, double x, double y, double z) {
		    	FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
            	GlStateManager.pushMatrix();
            	GlStateManager.translate(x, y + 0.1D, z);
            	GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            	GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            	GlStateManager.scale(-0.0025F, -0.0025F, 0.0025F);
            	GlStateManager.disableLighting();
            	
            	GlStateManager.depthMask(false);
            	GlStateManager.disableDepth();
            	GlStateManager.enableBlend();
            	GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            	int i = fontRenderer.getStringWidth(str) / 2;
            	GlStateManager.disableTexture2D();
            	Tessellator tessellator = Tessellator.getInstance();
            	BufferBuilder bufferbuilder = tessellator.getBuffer();
            	bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            	bufferbuilder.pos((double)(-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.3F).endVertex();
            	bufferbuilder.pos((double)(-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.3F).endVertex();
            	bufferbuilder.pos((double)(i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.3F).endVertex();
            	bufferbuilder.pos((double)(i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.3F).endVertex();
            	tessellator.draw();
            	GlStateManager.enableTexture2D();
            	fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, 0, 0x20FFFFFF);
            	GlStateManager.enableDepth();
            	
            	GlStateManager.depthMask(true);
            	fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, 0, 0xFF00FF00);
            	GlStateManager.enableLighting();
            	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            	GlStateManager.popMatrix();
		    }
		}
	
		// Made with Blockbench 4.6.5
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelFormula extends ModelBase {
			private final ModelRenderer bb_main;
			public ModelFormula() {
				textureWidth = 16;
				textureHeight = 16;
				bb_main = new ModelRenderer(this);
				bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.cubeList.add(new ModelBox(bb_main, -9, 0, -1.5F, 0.0F, -4.5F, 3, 0, 9, 0.1F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				bb_main.render(f5);
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onRenderWorldLast(RenderWorldLastEvent event) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.player != null && PlayerTracker.isNinja(mc.player) && !clientMarkerList.isEmpty()) {
				RenderManager renderManager = mc.getRenderManager();
				if (renderManager != null && renderManager.options != null && renderManager.options.thirdPersonView == 0) {
					//ICamera camera = new Frustum();
					//camera.setPosition(renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ);
					for (Vector4d vec : clientMarkerList.values()) {
						Vec3d vec1 = new Vec3d(vec.x, vec.y, vec.z).subtract(renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ);
						AxisAlignedBB aabb = new AxisAlignedBB(vec.x-0.5d, vec.y, vec.z-0.5d, vec.x+0.5d, vec.y+1.0d, vec.z+0.5d);
						if ((int)vec.w == mc.world.provider.getDimension()) {// && camera.isBoundingBoxInFrustum(aabb.grow(vec1.lengthVector()/20d))) {
							this.renderCustom.renderMarker(vec1.x, vec1.y, vec1.z, (float)mc.world.getTotalWorldTime() + event.getPartialTicks());
						}
					}
				}
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
			EntityPlayer player = event.getEntityPlayer();
			if (PlayerTracker.isNinja(player) && !clientMarkerList.isEmpty() && canUseJutsu(player)) {
				Vec3d vec1 = player.getPositionEyes(1f);
				for (Vector4d vec4d : clientMarkerList.values()) {
					if ((int)vec4d.w == Minecraft.getMinecraft().world.provider.getDimension()) {
						Vec3d vec = new Vec3d(vec4d.x, vec4d.y, vec4d.z);
						double d = vec.subtract(vec1).lengthVector();
						Vec3d vec2 = vec1.add(player.getLookVec().scale(d + 10d));
						AxisAlignedBB aabb = new AxisAlignedBB(vec.x-0.5d, vec.y, vec.z-0.5d, vec.x+0.5d, vec.y+1.0d, vec.z+0.5d);
						if (aabb.grow(d/20d).calculateIntercept(vec1, vec2) != null) {
							Chakra.Pathway chakra = Chakra.pathway(player);
							double chakraUsage = MathHelper.sqrt(d) * 10d;
							if (chakra.getAmount() > chakraUsage) {
								ProcedureOnLivingUpdate.setUntargetable(player, 5);
								ProcedureSync.SoundEffectMessage.sendToServer(vec.x, vec.y, vec.z,
								 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:swoosh")),
								 net.minecraft.util.SoundCategory.NEUTRAL, 0.8f, player.getRNG().nextFloat() * 0.4f + 0.8f);
								player.setPosition(vec.x, vec.y, vec.z);
								ProcedureSync.EntityPositionAndRotation.sendToServer(player);
								Chakra.PathwayPlayer.ConsumeMessage.sendToServer(chakraUsage);
							} else {
								chakra.warningDisplay();
							}
						}
					}
				}
			}
		}
	}
}
