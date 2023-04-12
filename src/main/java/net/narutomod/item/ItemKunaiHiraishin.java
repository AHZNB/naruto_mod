
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Multimap;

import java.util.Map;
import java.util.UUID;
import java.util.Iterator;
import javax.annotation.Nullable;
import javax.vecmath.Vector4d;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKunaiHiraishin extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:kunai_hiraishin")
	public static final Item block = null;
	public static final int ENTITYID = 402;

	public ItemKunaiHiraishin(ElementsNarutomodMod instance) {
		super(instance, 790);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletkunai_hiraishin"), ENTITYID).name("entitybulletkunai_hiraishin")
				.tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kunai_hiraishin", "inventory"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(Renderer.instance);
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			super();
			this.setMaxDamage(100);
			this.setFull3D();
			this.setUnlocalizedName("kunai_hiraishin");
			this.setRegistryName("kunai_hiraishin");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", (double) 3, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
			}
			return multimap;
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				float power = 1f;
				itemstack.damageItem(1, entity);
				EntityCustom entityarrow = new EntityCustom(entity, itemstack);
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 2, 0);
				entityarrow.setSilent(true);
				entityarrow.setIsCritical(false);
				entityarrow.setDamage(7);
				entityarrow.setKnockbackStrength(0);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, net.minecraft.init.SoundEvents.ENTITY_ARROW_SHOOT,
				 SoundCategory.NEUTRAL, 1, 1f / (itemRand.nextFloat() * 0.5f + 1f) + (power / 2));
				if (entity.isCreative()) {
					entityarrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
				} else {
					entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
					itemstack.shrink(1);
				}
				world.spawnEntity(entityarrow);
			}
		}

		public static void setOwner(ItemStack stack, EntityLivingBase owner) {
			if (stack.getItem() == block && !owner.world.isRemote) {
				if (!stack.hasTagCompound()) {
					stack.setTagCompound(new NBTTagCompound());
				}
				stack.getTagCompound().setUniqueId("owner", owner.getUniqueID());
				stack.setStackDisplayName(stack.getDisplayName() + " (" + owner.getName() + ")");
			}
		}

		@Nullable
		public static UUID getOwnerUuid(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getUniqueId("owner") : null;
		}

		@Override
		public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(stack, world, entity, par4, par5);
			if (entity instanceof EntityPlayerMP && ((EntityPlayerMP)entity).isCreative()
			 && (!stack.hasTagCompound() || !stack.getTagCompound().hasUniqueId("owner"))) {
				setOwner(stack, (EntityPlayerMP)entity);
			}
			UUID ownerUuid = getOwnerUuid(stack);
			if (ownerUuid != null && !ownerUuid.equals(entity.getUniqueID())) {
				EntityCustom.updateServerKunaiMap(ownerUuid, entity.getUniqueID(), new Vector4d(entity.posX, entity.posY, entity.posZ, entity.dimension));
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			entity.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, entity.getHeldItem(hand));
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BOW;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}
	}

	public static class EntityCustom extends EntityArrow {
		private static Map<UUID, Map<UUID, Vector4d>> serverKunaiMap = Maps.newHashMap();
		private static Map<UUID, Vector4d> clientKunaiList = Maps.newHashMap();
		private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityCustom.class, DataSerializers.ITEM_STACK);
		private boolean noUpdate;
		
		public EntityCustom(World a) {
			super(a);
		}

		public EntityCustom(EntityPlayer shooter, ItemStack stack) {
			super(shooter.world, shooter);
			this.setItem(stack.copy());
		}

		@Override
		public void entityInit() {
			super.entityInit();
			this.getDataManager().register(ITEM, ItemStack.EMPTY);
		}

	    public ItemStack getItem() {
	        return (ItemStack)this.getDataManager().get(ITEM);
	    }
	
	    public void setItem(ItemStack stack) {
	        this.getDataManager().set(ITEM, stack);
	        this.getDataManager().setDirty(ITEM);
	    }

		@Nullable
		public UUID getOwnerId() {
			ItemStack stack = this.getItem();
			return stack.getItem() == block ? RangedItem.getOwnerUuid(stack) : null;
		}

		@Nullable
		public EntityPlayerMP getOwner() {
			UUID uuid = this.getOwnerId();
			//return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
			return uuid == null ? null : ProcedureUtils.getPlayerMatchingUuid(uuid);
		}

		@Override
		protected void arrowHit(EntityLivingBase entity) {
			super.arrowHit(entity);
			entity.setArrowCountInEntity(entity.getArrowCountInEntity() - 1);
		}

		@Override
		protected ItemStack getArrowStack() {
			return this.getItem().copy();
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				UUID uuid = this.getOwnerId();
				if (uuid != null) {
					updateServerKunaiMap(uuid, this.getUniqueID(), null);
				}
			}
		}

		public static void cleanupServerKunaiMap() {
			Iterator<Map.Entry<UUID, Map<UUID, Vector4d>>> iter = serverKunaiMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<UUID, Map<UUID, Vector4d>> entry = iter.next();
				if (entry.getValue().isEmpty()) {
					iter.remove();
				}
			}
		}

		protected static void updateServerKunaiMap(UUID ownerUuid, UUID kunaiUuid, @Nullable Vector4d vec4d) {
			if (vec4d == null) {
				if (serverKunaiMap.containsKey(ownerUuid)) {
					serverKunaiMap.get(ownerUuid).remove(kunaiUuid);
				}
			} else if (!serverKunaiMap.containsKey(ownerUuid)) {
				Map<UUID, Vector4d> map = Maps.newHashMap();
				map.put(kunaiUuid, vec4d);
				serverKunaiMap.put(ownerUuid, map);
			} else {
				serverKunaiMap.get(ownerUuid).put(kunaiUuid, vec4d);
			}
			EntityPlayerMP owner = ProcedureUtils.getPlayerMatchingUuid(ownerUuid);
			if (owner != null) {
				Message.sendToPlayer(owner, kunaiUuid, vec4d);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && !this.isDead) {
				EntityPlayerMP owner = this.getOwner();
				if (owner == null) {
					this.noUpdate = false;
				} else if (!this.noUpdate) {
					Vec3d vec = this.getPositionVector();
					Vector4d vec4d = new Vector4d(vec.x, vec.y, vec.z, this.dimension);
					updateServerKunaiMap(owner.getUniqueID(), this.getUniqueID(), vec4d);
					if (this.inGround) {
						this.noUpdate = true;
					}
				}
			}
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			if (compound.hasKey("Item")) {
				this.setItem(new ItemStack(compound.getCompoundTag("Item")));
			}
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			if (!this.getItem().isEmpty()) {
				compound.setTag("Item", this.getItem().writeToNBT(new NBTTagCompound()));
			}
		}

		public static class Message implements IMessage {
			int len;
			String uuid;
			Vector4d vec;

			public Message() {}

			public Message(UUID id, @Nullable Vector4d v4d) {
				this.uuid = id.toString();
				this.len = this.uuid.length();
				this.vec = v4d;
			}

			public static void sendToPlayer(EntityPlayerMP entity, UUID id, @Nullable Vector4d v4d) {
				NarutomodMod.PACKET_HANDLER.sendTo(new Message(id, v4d), entity);
			}

			public static class Handler implements IMessageHandler<Message, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(Message message, MessageContext context) {
					Minecraft.getMinecraft().addScheduledTask(() -> {
						UUID uuid = UUID.fromString(message.uuid);
						if (message.vec != null) {
							clientKunaiList.put(uuid, message.vec);
						} else {
							clientKunaiList.remove(uuid);
						}
					});
					return null;
				}
			}
	
			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.len);
				for (int i = 0; i < this.len; i++) {
					buf.writeChar(this.uuid.charAt(i));
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
				this.len = buf.readInt();
				char[] tagArray = new char[this.len];
				for (int i = 0; i < this.len; i++) {
					tagArray[i] = buf.readChar();
				}
				this.uuid = new String(tagArray);
				this.vec = buf.readBoolean() ? new Vector4d(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble()) : null;
			}
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
		this.elements.addNetworkMessage(EntityCustom.Message.Handler.class, EntityCustom.Message.class, Side.CLIENT);
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				this.renderCustom = new RenderCustom(renderManager);
				return this.renderCustom;
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityCustom> {
			protected final Item item;
			private final RenderItem itemRenderer;

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.item = block;
				this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
			}

			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)x, (float)y, (float)z);
				GlStateManager.enableRescaleNormal();
				GlStateManager.rotate(entityYaw - 90.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
				this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				if (this.renderOutlines) {
					GlStateManager.enableColorMaterial();
					GlStateManager.enableOutlineMode(this.getTeamColor(entity));
				}
				this.itemRenderer.renderItem(this.getStackToRender(), ItemCameraTransforms.TransformType.GROUND);
				if (this.renderOutlines) {
					GlStateManager.disableOutlineMode();
					GlStateManager.disableColorMaterial();
				}
				GlStateManager.disableRescaleNormal();
				GlStateManager.popMatrix();
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
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
					this.itemRenderer.renderItem(this.getStackToRender(), ItemCameraTransforms.TransformType.GROUND);
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
            	bufferbuilder.pos((double)(-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            	bufferbuilder.pos((double)(-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            	bufferbuilder.pos((double)(i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            	bufferbuilder.pos((double)(i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
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

			public ItemStack getStackToRender() {
				return new ItemStack(this.item);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return TextureMap.LOCATION_BLOCKS_TEXTURE;
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onRenderWorldLast(RenderWorldLastEvent event) {
			if (!EntityCustom.clientKunaiList.isEmpty()) {
				Minecraft mc = Minecraft.getMinecraft();
				RenderManager renderManager = mc.getRenderManager();
				if (renderManager.options.thirdPersonView == 0) {
					for (Vector4d vec : EntityCustom.clientKunaiList.values()) {
						if ((int)vec.w == mc.world.provider.getDimension()) {
							this.renderCustom.renderMarker(vec.x - renderManager.viewerPosX, vec.y - renderManager.viewerPosY,
							 vec.z - renderManager.viewerPosZ, (float)mc.world.getTotalWorldTime() + event.getPartialTicks());
						}
					}
				}
			}
		}

		@SubscribeEvent
		public void onTossItem(ItemTossEvent event) {
			ItemStack stack = event.getEntityItem().getItem();
			if (stack.getItem() == block) {
				event.setCanceled(true);
				this.playerDropKunai(event.getPlayer(), stack);
			}
		}

		private void playerDropKunai(EntityPlayer player, ItemStack stack) {
			if (!player.world.isRemote) {
				EntityCustom entityarrow = new EntityCustom(player, stack);
				Vec3d vec = player.getLookVec();
				entityarrow.shoot(vec.x, 0d, vec.z, 0.3f, 0);
				entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
				player.world.spawnEntity(entityarrow);
				UUID ownerUuid = RangedItem.getOwnerUuid(stack);
				if (ownerUuid != null && !ownerUuid.equals(player.getUniqueID())) {
					EntityCustom.updateServerKunaiMap(ownerUuid, player.getUniqueID(), null);
				}
			}
		}

		@SubscribeEvent
		public void onPlayerDrops(PlayerDropsEvent event) {
			Iterator<EntityItem> iter = event.getDrops().iterator();
			while (iter.hasNext()) {
				EntityItem itemEntity = iter.next();
				if (itemEntity.getItem().getItem() == block) {
					this.playerDropKunai(event.getEntityPlayer(), itemEntity.getItem());
					iter.remove();
				}
			}
		}

		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
			if (!EntityCustom.clientKunaiList.isEmpty()) {
				Minecraft mc = Minecraft.getMinecraft();
				Vec3d vec1 = event.getEntityPlayer().getPositionEyes(1f);
				for (Vector4d vec4d : EntityCustom.clientKunaiList.values()) {
					if ((int)vec4d.w == mc.world.provider.getDimension()) {
						Vec3d vec = new Vec3d(vec4d.x, vec4d.y, vec4d.z);
						double d = vec.subtract(vec1).lengthVector();
						Vec3d vec2 = vec1.add(event.getEntityPlayer().getLookVec().scale(d + 10d));
						AxisAlignedBB aabb = new AxisAlignedBB(vec.x-0.5d, vec.y, vec.z-0.5d, vec.x+0.5d, vec.y+1.0d, vec.z+0.5d);
						if (aabb.grow(d/20d).calculateIntercept(vec1, vec2) != null) {
							event.getEntityPlayer().setPosition(vec.x, vec.y, vec.z);
							ProcedureSync.ResetBoundingBox.sendToServer(event.getEntityPlayer());
						}
					}
				}
			}
		}
	}
}
