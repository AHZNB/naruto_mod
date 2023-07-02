
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityAdamantinePrison;
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
	public static final ItemJutsu.JutsuEnum PRISON = new ItemJutsu.JutsuEnum(2, "adamantine_prison", 'D', 10d, new EntityAdamantinePrison.EC.Jutsu());

	public ItemAdamantineNyoi(ElementsNarutomodMod instance) {
		super(instance, 851);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(WEAPON, EXTEND, PRISON));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityExtend.class)
				.id(new ResourceLocation("narutomod", "adamantine_nyoi"), ENTITYID).name("adamantine_nyoi")
				.tracker(96, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:adamantine_nyoi", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base implements ItemOnBody.Interface {
		private static final UUID REACH_MODIFIER = UUID.fromString("2181075f-90e8-4444-9143-788f588ef58f");

		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.OTHER, list);
			this.setUnlocalizedName("adamantine_nyoi");
			this.setRegistryName("adamantine_nyoi");
			this.setCreativeTab(TabModTab.tab);
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
			return this.getCurrentJutsu(stack) == EXTEND ? this.getPower(stack, entity, timeLeft, 1.0f, 30f) : 1.0f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float ret = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return jutsu == EXTEND ? Math.min(ret, 15f) : ret;
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return this.getCurrentJutsu(itemstack) == WEAPON ? EnumAction.BLOCK : EnumAction.BOW;
		}

		@Override
		public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
			return stack.getItem() == block && this.getCurrentJutsu(stack) == WEAPON;
		}

		@Override
		public ItemOnBody.BodyPart showOnBody() {
			return ItemOnBody.BodyPart.LEFT_ARM;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				return false;
			}
		}
	}

	public static class EntityExtend extends EntityScalableProjectile.Base {
		private static final DataParameter<Optional<UUID>> FRONT = EntityDataManager.<Optional<UUID>>createKey(EntityExtend.class, DataSerializers.OPTIONAL_UNIQUE_ID);
		private static final DataParameter<Integer> SHOOTERID = EntityDataManager.<Integer>createKey(EntityExtend.class, DataSerializers.VARINT);
		private EntityExtend tailEnd;
		private final int lifeSpan = 300;
		private final float lengthMultiplier = 2.0f;
		private double renderTick;

		public EntityExtend(World a) {
			super(a);
			this.setOGSize(0.125f, 0.125f);
			this.setNoGravity(true);
		}

		public EntityExtend(EntityLivingBase shooter) {
			super(shooter);
			this.setShooter(shooter);
			this.setOGSize(0.125f, 0.125f);
			this.setIdlePosition();
			this.setFront(this);
		}

		private EntityExtend(EntityExtend parent) {
			this(parent.world);
			this.setFront(parent);
			parent.tailEnd = this;
			float scale = parent.getEntityScale();
			Vec3d vec2 = parent.getLookVec().scale(scale * this.lengthMultiplier * 3.75f).add(parent.getPositionVector());
			this.setEntityScale(scale);
			this.setLocationAndAngles(vec2.x, vec2.y, vec2.z, parent.rotationYaw - 180f, -parent.rotationPitch);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(FRONT, Optional.absent());
			this.getDataManager().register(SHOOTERID, Integer.valueOf(-1));
		}

	    @Nullable
	    public UUID getFrontUuid() {
	        return (UUID)((Optional)this.dataManager.get(FRONT)).orNull();
	    }
	
	    public void setFront(EntityExtend entity) {
	        this.dataManager.set(FRONT, Optional.fromNullable(entity.getUniqueID()));
	    }

		private void setShooter(EntityLivingBase shooter) {
			this.getDataManager().set(SHOOTERID, Integer.valueOf(shooter.getEntityId()));
		}

		public EntityLivingBase getShooter() {
			if (!this.world.isRemote) {
				return this.shootingEntity;
			}
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(SHOOTERID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
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
			EntityLivingBase shooter = this.getShooter();
			float scale = this.getEntityScale();
			if (shooter != null) {
				Vec3d vec = shooter.getLookVec().add(shooter.getPositionVector().addVector(0d, 1.1d - scale * 0.0625f, 0d));
				this.setLocationAndAngles(vec.x, vec.y, vec.z, shooter.rotationYaw, shooter.rotationPitch);
			}
			if (this.tailEnd != null) {
				Vec3d vec = this.getLookVec().scale(scale * this.lengthMultiplier * 3.75f).add(this.getPositionVector());
				this.tailEnd.setEntityScale(scale);
				this.tailEnd.setLocationAndAngles(vec.x, vec.y, vec.z, this.rotationYaw - 180f, -this.rotationPitch);
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
			boolean isFront = this.equals(front);
			if (isFront && this.tailEnd == null && !this.world.isRemote) {
				this.world.spawnEntity(new EntityExtend(this));
			}
			super.onUpdate();
			if (isFront && this.ticksAlive >= this.lifeSpan - 30) {
				float f = this.getEntityScale();
				this.setEntityScale(f - (f - 1f) * (1f - (float)(this.lifeSpan - this.ticksAlive) / 30f));
			}
			if (isFront) {
				this.setIdlePosition();
			}
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
			public boolean shouldRender(EntityExtend livingEntity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
		    @Override
		    public void doRender(EntityExtend entity, double x, double y, double z, float entityYaw, float partialTicks) {
		    	EntityExtend entity1 = entity.getFront();
		    	if (entity1 != null) {
		    		double d = (double)entity.world.getTotalWorldTime() + partialTicks;
		    		if (d <= entity1.renderTick) {
		    			return;
		    		}
		    		entity1.renderTick = d;
		    		if (!entity.equals(entity1)) {
			    		entity = entity1;
			    		x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - this.renderManager.viewerPosX;
			    		y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - this.renderManager.viewerPosY;
			    		z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - this.renderManager.viewerPosZ;
		    		}
		    	}
		    	float scale = entity.getEntityScale();
		        GlStateManager.pushMatrix();
		        GlStateManager.translate((float)x, (float)y, (float)z);
		        GlStateManager.rotate(-ProcedureUtils.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
		        GlStateManager.translate(0.0F, scale * 0.0625F, scale * entity.lengthMultiplier * 1.875F);
		        GlStateManager.scale(scale, scale, scale * entity.lengthMultiplier);
		        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		        this.itemRenderer.renderItem(this.item, ItemCameraTransforms.TransformType.GROUND);
		        GlStateManager.popMatrix();
		    }
		
			@Override
		    protected ResourceLocation getEntityTexture(EntityExtend entity) {
		        return TextureMap.LOCATION_BLOCKS_TEXTURE;
		    }
		}
	}
}
