package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.Minecraft;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAshBones extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:ashbones")
	public static final Item block = null;
	public static final int ENTITYID = 63;
	
	public ItemAshBones(ElementsNarutomodMod instance) {
		super(instance, 271);
	}

	public void initElements() {
		this.elements.items.add(() -> new RangedItem());
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletashbones"), ENTITYID).name("entitybulletashbones").tracker(64, 1, true)
				.build());
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:ashbones", "inventory"));
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			this.setMaxDamage(0);
			this.setFull3D();
			this.setUnlocalizedName("ashbones");
			this.setRegistryName("ashbones");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				float power = 1.0F;
				EntityArrowCustom entityarrow = new EntityArrowCustom(world, entity);
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 2.0F, 0.0F);
				entityarrow.setSilent(true);
				entityarrow.setIsCritical(false);
				entityarrow.setDamage(2.0D);
				entityarrow.setKnockbackStrength(0);
				itemstack.damageItem(1, entity);
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				world.playSound(null, x, y, z, net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("narutomod:hand_shoot")),
						SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.5F + 1.0F) + power / 2.0F);
				entityarrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
				world.spawnEntity(entityarrow);
				if (!entity.isCreative())
					entity.getCooldownTracker().setCooldown(itemstack.getItem(), 80);
			}
		}

		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			hitLivingEntity(target);
			return true;
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

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!(entity instanceof EntityPlayer) || (!((EntityPlayer) entity).isCreative()
			 && !ItemByakugan.wearingRinnesharingan((EntityPlayer)entity) 
			 && !ItemRinnegan.wearingRinnesharingan((EntityPlayer)entity)))
				itemstack.shrink(1);
		}

		@Override
		public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
			return false;
		}
	}

	public static class EntityArrowCustom extends EntityArrow {
		public EntityArrowCustom(World a) {
			super(a);
		}

		public EntityArrowCustom(World worldIn, double x, double y, double z) {
			super(worldIn, x, y, z);
		}

		public EntityArrowCustom(World worldIn, EntityLivingBase shooter) {
			super(worldIn, shooter);
		}

		@Override
		protected void arrowHit(EntityLivingBase entity) {
			super.arrowHit(entity);
			entity.setArrowCountInEntity(entity.getArrowCountInEntity() - 1);
			hitLivingEntity(entity);
		}

		@Override
		protected ItemStack getArrowStack() {
			return new ItemStack(block);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			for (int i = 0; i < 5; i++) {
				this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
			}
			if (!this.world.isRemote) {
				for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class,
				 this.getEntityBoundingBox().grow(0.75d), EntitySelectors.getTeamCollisionPredicate(this))) {
					if (!entity.equals(this.shootingEntity)) {
						hitLivingEntity(entity);
					}
				}
			}
		}
	}

	protected static void hitLivingEntity(Entity entity) {
		if (!entity.world.isRemote && entity.getEntityData().getDouble("deathAnimationType") != 1.0d) {
			entity.getEntityData().setDouble("deathAnimationType", 1.0D);
			entity.getEntityData().setDouble(NarutomodModVariables.DeathAnimationTime, NarutomodModVariables.DeathAnimation_slowDust);
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
			RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityArrowCustom> {
			protected final Item item;
			private final RenderItem itemRenderer;
	
			public RenderCustom(RenderManager renderManager) {
				super(renderManager);
				this.shadowSize = 0.1F;
				this.item = block;
				this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
			}
	
			@Override
			public void doRender(EntityArrowCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
		        GlStateManager.pushMatrix();
		        GlStateManager.translate((float)x, (float)y, (float)z);
		        GlStateManager.enableRescaleNormal();
		        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90F, 0.0F, 1.0F, 0.0F);
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
}
