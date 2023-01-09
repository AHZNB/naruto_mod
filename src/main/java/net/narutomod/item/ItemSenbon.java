
package net.narutomod.item;

import net.narutomod.entity.EntityPuppetHiruko;
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

import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.PotionEffect;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSenbon extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:senbon")
	public static final Item block = null;
	public static final int ENTITYID = 391;

	public ItemSenbon(ElementsNarutomodMod instance) {
		super(instance, 770);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletsenbon"), ENTITYID).name("entitybulletsenbon").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:senbon", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> {
			return new RenderCustom(renderManager);
		});
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			super();
			this.itemInit();
		}

		protected void itemInit() {
			setMaxDamage(0);
			setFull3D();
			setUnlocalizedName("senbon");
			setRegistryName("senbon");
			maxStackSize = 64;
			setCreativeTab(TabModTab.tab);
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				boolean flag = entity.getRidingEntity() instanceof EntityPuppetHiruko.EntityCustom;
				if (flag) {
					for (int i = 0; i < 3; i++) {
						spawnArrow((EntityLivingBase)entity.getRidingEntity(), false);
					}
				} else {
					spawnArrow(entity, false);
				}
				if (!entity.capabilities.isCreativeMode
				 && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemstack) <= 0) {
					entity.inventory.clearMatchingItems(block, -1, flag ? 3 : 1, null);
				}
			}
		}

		/*@Override
		public void onUsingTick(ItemStack itemstack, EntityLivingBase entityLivingBase, int count) {
			if (entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				boolean flag = entity.getRidingEntity() instanceof EntityPuppetHiruko.EntityCustom;
				if (flag) {
					for (int i = 0; i < 3; i++) {
						spawnArrow((EntityLivingBase)entity.getRidingEntity(), false);
					}
				} else {
					spawnArrow(entity, false);
				}
				if (!entity.capabilities.isCreativeMode
				 && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemstack) <= 0) {
					entity.inventory.clearMatchingItems(block, -1, flag ? 3 : 1, null);
				}
			}
			entityLivingBase.resetActiveHand();
		}*/

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

	public static class EntityArrowCustom extends EntityArrow {
		private boolean poisened;
		
		public EntityArrowCustom(World a) {
			super(a);
		}

		public EntityArrowCustom(World worldIn, double x, double y, double z) {
			super(worldIn, x, y, z);
		}

		public EntityArrowCustom(World worldIn, EntityLivingBase shooter) {
			super(worldIn, shooter);
		}

		public void setPoisened(boolean b) {
			this.poisened = b;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.timeInGround > 400) {
				this.setDead();
			}
		}

		@Override
		protected void onHit(RayTraceResult rtr) {
			if (rtr.entityHit != null) {
				rtr.entityHit.hurtResistantTime = 10;
			}
			if (!this.world.isRemote) {
				this.world.playSound(null, rtr.hitVec.x, rtr.hitVec.y, rtr.hitVec.z,
				 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:senbon_impact")),
				 SoundCategory.NEUTRAL, 0.1f, 0.4f + this.rand.nextFloat() * 0.6f);
			}
			super.onHit(rtr);
		}

		@Override
		protected void arrowHit(EntityLivingBase entity) {
			super.arrowHit(entity);
			entity.setArrowCountInEntity(entity.getArrowCountInEntity() - 1);
			if (this.poisened) {
				entity.addPotionEffect(new PotionEffect(MobEffects.POISON, 1200, 1));
			}
		}

		@Override
		protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
			Entity entity = super.findEntityOnPath(start, end);
			return entity != null && entity.isRiding() && entity.getRidingEntity().equals(this.shootingEntity) ? null : entity;
		}

		@Override
		protected ItemStack getArrowStack() {
			return new ItemStack(block);
		}

		protected static void spawn(EntityArrowCustom entityarrow) {
			float power = 1f;
			entityarrow.setSilent(true);
			entityarrow.setIsCritical(false);
			entityarrow.setDamage(3.0f);
			entityarrow.setKnockbackStrength(0);
			entityarrow.world.playSound(null, entityarrow.posX, entityarrow.posY, entityarrow.posZ,
			 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:senbon"))),
			 SoundCategory.NEUTRAL, 1, 1f / (entityarrow.world.rand.nextFloat() * 0.5f + 1f) + (power / 2));
			entityarrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
			entityarrow.world.spawnEntity(entityarrow);
		}
	}

	public static void spawnArrow(Entity entity, boolean randomDirection) {
		if (!entity.world.isRemote) {
			float power = 1f;
			EntityArrowCustom entityarrow = entity instanceof EntityLivingBase
			 ? new EntityArrowCustom(entity.world, (EntityLivingBase)entity)
			 : new EntityArrowCustom(entity.world, entity.posX, entity.posY, entity.posZ);
			if (randomDirection) {
				entityarrow.shoot(entity.world.rand.nextFloat() * 2.0f - 1.0f, entity.world.rand.nextFloat() * 2.0f - 1.0f,
				 entity.world.rand.nextFloat() * 2.0f - 1.0f, power * 2, 0.0f);
			} else {
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 2,
				 entity instanceof EntityPuppetHiruko.EntityCustom ? 8.0f : 0.0f);
			}
			EntityArrowCustom.spawn(entityarrow);
		}
	}

	public static void spawnArrow(EntityLivingBase entity, Vec3d targetVec) {
		if (!entity.world.isRemote) {
			float power = 1f;
			EntityArrowCustom entityarrow = new EntityArrowCustom(entity.world, (EntityLivingBase)entity);
			targetVec = targetVec.subtract(entityarrow.getPositionVector());
			entityarrow.shoot(targetVec.x, targetVec.y, targetVec.z, power * 2, 8.0f);
			EntityArrowCustom.spawn(entityarrow);
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EntityArrowCustom> {
		protected final Item item;
		private final RenderItem itemRenderer;

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn);
			this.item = block;
			this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
		}

	    @Override
	    public void doRender(EntityArrowCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
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
	        this.itemRenderer.renderItem(this.getStackToRender(entity), ItemCameraTransforms.TransformType.GROUND);
	        if (this.renderOutlines) {
	            GlStateManager.disableOutlineMode();
	            GlStateManager.disableColorMaterial();
	        }
	        GlStateManager.disableRescaleNormal();
	        GlStateManager.popMatrix();
	        super.doRender(entity, x, y, z, entityYaw, partialTicks);
	    }
	
	    public ItemStack getStackToRender(EntityArrowCustom entityIn) {
	        return new ItemStack(this.item);
	    }
	
		@Override
	    protected ResourceLocation getEntityTexture(EntityArrowCustom entity) {
	        return TextureMap.LOCATION_BLOCKS_TEXTURE;
	    }
	}
}
