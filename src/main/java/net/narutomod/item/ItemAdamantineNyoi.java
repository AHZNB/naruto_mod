
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Multimap;
import com.google.common.base.Optional;
import javax.annotation.Nullable;
import java.util.UUID;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAdamantineNyoi extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:adamantine_nyoi")
	public static final Item block = null;
	public static final int ENTITYID = 425;
	public static final ItemJutsu.JutsuEnum WEAPON = new ItemJutsu.JutsuEnum(0, "tooltip.adamantinenyoi.block", 'D', new RangedItem.Jutsu());
	public static final ItemJutsu.JutsuEnum EXTEND = new ItemJutsu.JutsuEnum(1, "tooltip.adamantinenyoi.extend", 'D', 10d, new EntityExtend.Jutsu());

	public ItemAdamantineNyoi(ElementsNarutomodMod instance) {
		super(instance, 851);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(WEAPON, EXTEND));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityExtend.class)
				.id(new ResourceLocation("narutomod", "adamantine_nyoi"), ENTITYID).name("adamantine_nyoi")
				.tracker(96, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:adamantine_nyoi", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		private static final UUID REACH_MODIFIER = UUID.fromString("2181075f-90e8-4444-9143-788f588ef58f");

		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.OTHER, list);
			setUnlocalizedName("adamantine_nyoi");
			setRegistryName("adamantine_nyoi");
			setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[WEAPON.index] = 0;
			this.defaultCooldownMap[EXTEND.index] = 0;
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", (double) 17, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
				multimap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_MODIFIER, "Tool modifier", 1.5d, 0));
			}
			return multimap;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			super.onUsingTick(stack, player, timeLeft);
			if (this.getCurrentJutsu(stack) == EXTEND && !player.world.isRemote) {
				float power = this.getPower(stack, player, timeLeft);
				if (power >= this.getMaxPower(stack, player)) {
					player.stopActiveHand();
				} else {
					EntityExtend entity = this.getStaffEntity(player.world, stack);
					if (entity == null) {
						entity = new EntityExtend(player);
						player.world.spawnEntity(entity);
						this.setStaffEntity(stack, entity);
					}
					entity.setEntityScale(power);
				}
			}
		}

		@Override
		protected void onUsingEffects(EntityLivingBase player) {
		}

		private void setStaffEntity(ItemStack stack, EntityExtend entity) {
			if (stack.hasTagCompound()) {
				stack.getTagCompound().setInteger("staffEntityId", entity.getEntityId());
			}
		}

		@Nullable
		private EntityExtend getStaffEntity(World world, ItemStack stack) {
			Entity entity = world.getEntityByID(stack.getTagCompound().getInteger("staffEntityId"));
			return entity instanceof EntityExtend && !entity.isDead ? (EntityExtend)entity : null;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			return this.getCurrentJutsu(stack) == EXTEND ? this.getPower(stack, entity, timeLeft, 1.0f, 20f) : 1.0f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float ret = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return jutsu == EXTEND ? Math.min(ret, 30f) : ret;
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return this.getCurrentJutsu(itemstack) == WEAPON ? EnumAction.BLOCK : EnumAction.BOW;
		}

		@Override
		public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
			return stack.getItem() == block && this.getCurrentJutsu(stack) == WEAPON;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				return false;
			}
		}
	}

	public static class EntityExtend extends EntityScalableProjectile.Base {
		protected static final DataParameter<Optional<UUID>> FRONT = EntityDataManager.<Optional<UUID>>createKey(EntityExtend.class, DataSerializers.OPTIONAL_UNIQUE_ID);
		private EntityExtend tailEnd;
		private int lifeSpan = 300;

		public EntityExtend(World a) {
			super(a);
			this.setOGSize(0.125f, 0.125f);
			this.setNoGravity(true);
		}

		public EntityExtend(EntityLivingBase shooter) {
			super(shooter);
			this.setOGSize(0.125f, 0.125f);
			this.setIdlePosition();
			this.setFront(this);
		}

		private EntityExtend(EntityExtend parent) {
			this(parent.world);
			this.setFront(parent);
			parent.tailEnd = this;
			float scale = parent.getEntityScale();
			Vec3d vec2 = parent.getLookVec().scale(scale * 1.5f * 3.75f).add(parent.getPositionVector()).addVector(0d, scale * 0.0625f, 0d);
			this.setEntityScale(scale);
			this.setPositionAndRotation(vec2.x, vec2.y, vec2.z, parent.rotationYaw - 180f, -parent.rotationPitch);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(FRONT, Optional.absent());
		}

	    @Nullable
	    public UUID getFrontUuid() {
	        return (UUID)((Optional)this.dataManager.get(FRONT)).orNull();
	    }
	
	    public void setFront(EntityExtend entity) {
	        this.dataManager.set(FRONT, Optional.fromNullable(entity.getUniqueID()));
	    }

	    @Nullable
	    public EntityExtend getFront() {
	        UUID uuid = this.getFrontUuid();
	        if (uuid == null) {
	        	return null;
	        } else {
	        	Entity entity = ProcedureUtils.getEntityFromUUID(this.world, uuid);
	        	return entity instanceof EntityExtend ? (EntityExtend)entity : null;
	        }
	    }

		private void setIdlePosition() {
			if (this.shootingEntity != null) {
				float scale = this.getEntityScale();
				Vec3d vec0 = this.shootingEntity.getLookVec();
				Vec3d vec1 = this.shootingEntity.getPositionVector().addVector(0d, 1.1d - scale * 0.0625f, 0d);
				Vec3d vec2 = vec0.add(vec1);
				this.setLocationAndAngles(vec2.x, vec2.y, vec2.z, this.shootingEntity.rotationYaw, this.shootingEntity.rotationPitch);
				if (this.tailEnd != null) {
					vec2 = this.getLookVec().scale(scale * 1.5f * 3.75f).add(vec2).addVector(0d, scale * 0.0625f, 0d);
					this.tailEnd.setEntityScale(scale);
					this.tailEnd.setPositionAndRotation(vec2.x, vec2.y, vec2.z, this.shootingEntity.rotationYaw - 180f, -this.shootingEntity.rotationPitch);
				}
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.tailEnd != null) {
				this.tailEnd.setDead();
			}
		}

		@Override
		public void onUpdate() {
			EntityExtend front = this.getFront();
			if (this.equals(front) && this.tailEnd == null && !this.world.isRemote) {
				this.world.spawnEntity(new EntityExtend(this));
			}
			super.onUpdate();
			if (this.shootingEntity != null && this.ticksAlive >= this.lifeSpan - 30) {
				float f = this.getEntityScale();
				this.setEntityScale(f - (f - 1f) * (1f - (float)(this.lifeSpan - this.ticksAlive) / 30f));
			}
			this.setIdlePosition();
			if (!this.world.isRemote && (this.ticksAlive >= this.lifeSpan || front == null || front.isDead)) {
				this.setDead();
			}
		}

		@Override
		public void onImpact(RayTraceResult result) {
			
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			String s = compound.getString("frontUUID");
			if (!s.isEmpty()) {
				this.dataManager.set(FRONT, Optional.fromNullable(UUID.fromString(s)));
			}
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			UUID uuid = this.getFrontUuid();
			compound.setString("frontUUID", uuid == null ? "" : uuid.toString());
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				((RangedItem)stack.getItem()).setCurrentJutsuCooldown(stack, 300);
				return true;
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
			RenderingRegistry.registerEntityRenderingHandler(EntityExtend.class, renderManager -> {
				return new RenderCustom(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityExtend> {
			protected final ItemStack item;
			private final RenderItem itemRenderer;
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.item = new ItemStack(block);
				this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
			}
	
		    @Override
		    public void doRender(EntityExtend entity, double x, double y, double z, float entityYaw, float partialTicks) {
		    	EntityExtend entity1 = entity.getFront();
		    	if (entity1 != null && !entity.equals(entity1)) {
		    		entity = entity1;
		    		x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - this.renderManager.viewerPosX;
		    		y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - this.renderManager.viewerPosY;
		    		z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - this.renderManager.viewerPosZ;
		    	}
		    	float scale = entity.getEntityScale();
		        GlStateManager.pushMatrix();
		        GlStateManager.translate((float)x, (float)y, (float)z);
		        GlStateManager.rotate(-ProcedureUtils.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
		        GlStateManager.translate(0.0F, scale * 0.0625F, scale * 1.5F * 1.875F + 0.125F);
		        GlStateManager.scale(scale, scale, scale * 1.5F);
		        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		        if (this.renderOutlines) {
		            GlStateManager.enableColorMaterial();
		            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		        }
		        this.itemRenderer.renderItem(this.item, ItemCameraTransforms.TransformType.GROUND);
		        if (this.renderOutlines) {
		            GlStateManager.disableOutlineMode();
		            GlStateManager.disableColorMaterial();
		        }
		        GlStateManager.disableRescaleNormal();
		        GlStateManager.popMatrix();
		        super.doRender(entity, x, y, z, entityYaw, partialTicks);
		    }
		
			@Override
		    protected ResourceLocation getEntityTexture(EntityExtend entity) {
		        return TextureMap.LOCATION_BLOCKS_TEXTURE;
		    }
		}
	}
}
