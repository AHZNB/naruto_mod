
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityHiraishin;
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
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
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
import java.util.UUID;
import java.util.Iterator;
import javax.vecmath.Vector4d;
import javax.annotation.Nullable;

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
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", 6d, 0));
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
					entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
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
			if (ownerUuid != null) {
				UUID lastMarkerUuid = stack.getTagCompound().hasUniqueId("lastMarkerUuid")
				 ? stack.getTagCompound().getUniqueId("lastMarkerUuid") : null;
				if (!ownerUuid.equals(entity.getUniqueID())) {
					if (lastMarkerUuid != null && !lastMarkerUuid.equals(entity.getUniqueID())) {
						EntityHiraishin.updateServerMarkerMap(ownerUuid, lastMarkerUuid, null);
					}
					EntityHiraishin.updateServerMarkerMap(ownerUuid, entity.getUniqueID(), new Vector4d(entity.posX, entity.posY, entity.posZ, entity.dimension));
					stack.getTagCompound().setUniqueId("lastMarkerUuid", entity.getUniqueID());
				} else if (lastMarkerUuid != null) {
					EntityHiraishin.updateServerMarkerMap(ownerUuid, lastMarkerUuid, null);
					stack.getTagCompound().removeTag("lastMarkerUuid");
				}
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
		private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityCustom.class, DataSerializers.ITEM_STACK);
		private boolean noUpdate;
		private int pickupDelay = 60;
		private int lastCollideTime;
		
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
		protected ItemStack getArrowStack() {
			return this.getItem().copy();
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				UUID uuid = this.getOwnerId();
				if (uuid != null) {
					EntityHiraishin.updateServerMarkerMap(uuid, this.getUniqueID(), null);
				}
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.inGround && this.getOwnerId() != null
			 && (int)ReflectionHelper.getPrivateValue(EntityArrow.class, this, 12) > 1198) {
				ReflectionHelper.setPrivateValue(EntityArrow.class, this, 1, 12); // this.ticksInGround
			}
			if (!this.world.isRemote && !this.isDead) {
				EntityPlayerMP owner = this.getOwner();
				if (owner == null) {
					this.noUpdate = false;
				} else if (!this.noUpdate) {
					Vec3d vec = this.getPositionVector();
					Vector4d vec4d = new Vector4d(vec.x, vec.y, vec.z, this.dimension);
					EntityHiraishin.updateServerMarkerMap(owner.getUniqueID(), this.getUniqueID(), vec4d);
					if (this.inGround) {
						this.noUpdate = true;
					}
				}
			}
		}

		@Override
		public void onCollideWithPlayer(EntityPlayer entityIn) {
			if (!this.world.isRemote && this.inGround && this.arrowShake <= 0) {
				if (this.timeInGround - this.lastCollideTime > 20) {
					this.pickupDelay = 60;
				}
				if (this.pickupDelay <= 0) {
					super.onCollideWithPlayer(entityIn);
				}
				if (this.pickupDelay > 0) {
					--this.pickupDelay;
				}
				this.lastCollideTime = this.timeInGround;
			}
		}

		@Override
		protected void onHit(RayTraceResult result) {
			if (result.entityHit == null || !this.world.isRemote) {
				super.onHit(result);
			}
		}

		@Override
		protected void arrowHit(EntityLivingBase living) {
			super.arrowHit(living);
			living.setArrowCountInEntity(living.getArrowCountInEntity() - 1);
			if (living instanceof EntityPlayer) {
				ItemHandlerHelper.giveItemToPlayer((EntityPlayer)living, this.getItem());
			} else {
				EntityPlayerMP owner = this.getOwner();
				if (owner != null) {
					RayTraceResult rtr = living.getEntityBoundingBox()
					 .calculateIntercept(this.getPositionVector(), living.getPositionVector()
					 .addVector(0d, 0.5d * living.height, 0d));
					if (rtr != null) {
						rtr.entityHit = living;
						rtr.typeOfHit = RayTraceResult.Type.ENTITY;
						this.world.spawnEntity(new EntityHiraishin.EC(owner, rtr));
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
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		private static Renderer instance;

		public Renderer() {
			instance = this;
		}
		
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
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

			public ItemStack getStackToRender() {
				return new ItemStack(this.item);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return TextureMap.LOCATION_BLOCKS_TEXTURE;
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
				entityarrow.shoot(vec.x, 0d, vec.z, 0.4f, 0);
				entityarrow.setDamage(7);
				entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
				player.world.spawnEntity(entityarrow);
				UUID ownerUuid = RangedItem.getOwnerUuid(stack);
				if (ownerUuid != null && !ownerUuid.equals(player.getUniqueID())) {
					EntityHiraishin.updateServerMarkerMap(ownerUuid, player.getUniqueID(), null);
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
	}
}
