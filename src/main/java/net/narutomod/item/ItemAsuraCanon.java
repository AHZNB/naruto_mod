package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAsuraCanon extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:asuracanon")
	public static final Item block = null;
	public static final int ENTITYID = 31;
	
	public ItemAsuraCanon(ElementsNarutomodMod instance) {
		super(instance, 214);
	}

	public void initElements() {
		this.elements.items.add(() -> new RangedItem());
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityMissile.class)
		 .id(new ResourceLocation("narutomod", "entitybulletasuracanon"), ENTITYID)
		 .name("entitybulletasuracanon").tracker(64, 1, true).build());
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:asuracanon", "inventory"));
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			this.setMaxDamage(50);
			this.setFull3D();
			this.setUnlocalizedName("asuracanon");
			this.setRegistryName("asuracanon");
			this.maxStackSize = 1;
			this.setCreativeTab(null);
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				EntityMissile entityarrow = new EntityMissile(entity);
				itemstack.damageItem(1, entity);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_BLAZE_SHOOT,
				 SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.5F + 1.0F) + 0.5F);
				world.spawnEntity(entityarrow);
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

	public static class EntityMissile extends EntityScalableProjectile.Base {
		public static float explosivePower = 4.0F;
		private Entity target;

		public EntityMissile(World worldIn) {
			super(worldIn);
			this.setOGSize(0.25F, 0.25F);
			this.isImmuneToFire = true;
		}

		public EntityMissile(EntityLivingBase throwerIn) {
			super(throwerIn);
			this.setOGSize(0.25F, 0.25F);
			this.isImmuneToFire = true;
			Vec3d vec0 = throwerIn.getLookVec();
			Vec3d vec = vec0.add(throwerIn.getPositionEyes(1f));
			this.setLocationAndAngles(vec.x, vec.y, vec.z, throwerIn.rotationYawHead, throwerIn.rotationPitch);
			this.shoot(vec0.x, vec0.y, vec0.z, 0.8f, 0.1f, true);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.target != null) {
				Vec3d vec = this.target.getPositionEyes(1f).subtract(this.getPositionVector());
				this.shoot(vec.x, vec.y, vec.z, this.isInWater() ? 0.85f : 0.95f, 0f, true);
			} else if (this.shootingEntity instanceof EntityLiving) {
				this.target = ((EntityLiving)this.shootingEntity).getAttackTarget();
			} else if (this.shootingEntity != null) {
				this.target = ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d, 2.0d, false, false, new Predicate<Entity>() {
					public boolean apply(@Nullable Entity p_apply_1_) {
						return p_apply_1_ != null && (!(p_apply_1_ instanceof EntityMissile)
						 || !EntityMissile.this.shootingEntity.equals(((EntityMissile)p_apply_1_).shootingEntity));
					}
				}).entityHit;
			}
			if (!this.world.isRemote && this.ticksInAir > 160) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote && (result.entityHit == null || !result.entityHit.equals(this.shootingEntity))) {
				this.world.createExplosion(this, this.posX, this.posY, this.posZ, this.explosivePower,
				 ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity));
				this.setDead();
			}
		}

		@Override
		public void renderParticles() {	
			if (this.world.isRemote) {
				this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX - this.motionX,
				 this.posY - this.motionY, this.posZ - this.motionZ, -this.motionX, -this.motionY, -this.motionZ);
			}
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}
	
	public class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityMissile.class, renderManager -> {
				return new RenderCustom(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityMissile> {
			protected final Item item;
			private final RenderItem itemRenderer;
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.item = ItemBlackReceiver.block;
				this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
			}

		    @Override
		    public void doRender(EntityMissile entity, double x, double y, double z, float entityYaw, float partialTicks) {
		        GlStateManager.pushMatrix();
		        GlStateManager.scale(0.8f, 0.8f, 0.8f);
		        GlStateManager.translate((float)x, (float)y + 0.5f, (float)z);
		        GlStateManager.enableRescaleNormal();
		        GlStateManager.rotate(90F - entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
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
		        //super.doRender(entity, x, y, z, entityYaw, partialTicks);
		    }
		
		    public ItemStack getStackToRender(EntityMissile entityIn) {
		        return new ItemStack(this.item);
		    }
		
			@Override
		    protected ResourceLocation getEntityTexture(EntityMissile entity) {
		        return TextureMap.LOCATION_BLOCKS_TEXTURE;
		    }
		}
	}
}
