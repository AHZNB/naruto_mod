
package net.narutomod.item;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityPuppetHiruko;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBow;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSenbonArm extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:senbon_arm")
	public static final Item block = null;
	public static final int ENTITYID = 406;

	public ItemSenbonArm(ElementsNarutomodMod instance) {
		super(instance, 796);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletsenbon_arm"), ENTITYID).name("entitybulletsenbon_arm").tracker(64, 1, true)
				.build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:senbon_arm", "inventory"));
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			super();
			setMaxDamage(100);
			setFull3D();
			setUnlocalizedName("senbon_arm");
			setRegistryName("senbon_arm");
			maxStackSize = 1;
			setCreativeTab(TabModTab.tab);
		}

		public static void shootItem(EntityLivingBase entity, double x, double y, double z, float power) {
			if (!entity.world.isRemote) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, net.minecraft.util.SoundEvent.REGISTRY
						.getObject(new ResourceLocation("narutomod:bullet")), SoundCategory.NEUTRAL,
						1, 1f / (itemRand.nextFloat() * 0.5f + 1f) + (power / 2));
				EntityArrowCustom entityarrow = new EntityArrowCustom(entity);
				entityarrow.shoot(x, y, z, power * 2.0f, 0.0f);
				entity.world.spawnEntity(entityarrow);
			}
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
			float power = ItemBow.getArrowVelocity(this.getMaxItemUseDuration(itemstack) - timeLeft);
			if (!world.isRemote && power > 0.2f) {
				shootItem(entity, entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power);
				if (entity instanceof EntityPlayer && !((EntityPlayer)entity).isCreative()) {
					itemstack.shrink(1);
				}
			}
			if (entity.getRidingEntity() instanceof EntityPuppetHiruko.EntityCustom) {
				((EntityPuppetHiruko.EntityCustom)entity.getRidingEntity()).raiseLeftArm(false);
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			entity.setActiveHand(hand);
			if (entity.getRidingEntity() instanceof EntityPuppetHiruko.EntityCustom) {
				((EntityPuppetHiruko.EntityCustom)entity.getRidingEntity()).raiseLeftArm(true);
			}
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

	public static class EntityArrowCustom extends EntityThrowable {
		public float power;

		public EntityArrowCustom(World a) {
			super(a);
		}

		public EntityArrowCustom(EntityLivingBase shooter) {
			super(shooter.world, shooter);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null) {
				if (result.entityHit.equals(this.thrower)
				 || (result.entityHit.isBeingRidden() && result.entityHit.getControllingPassenger().equals(this.thrower))
				 || (result.entityHit.isRiding() && result.entityHit.getRidingEntity().equals(this.thrower))
				 || result.entityHit instanceof ItemSenbon.EntityArrowCustom) {
					return;
				}
				result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.thrower), 8.0f);
				this.motionX *= -0.2d;
				this.motionY *= -0.2d;
				this.motionZ *= -0.2d;
			} else if (ProcedureUtils.getVelocity(this) < 0.1d) {
				BlockPos blockpos = result.getBlockPos();
				ReflectionHelper.setPrivateValue(EntityThrowable.class, this, blockpos.getX(), 0); //this.xTile = blockpos.getX();
				ReflectionHelper.setPrivateValue(EntityThrowable.class, this, blockpos.getY(), 1); // this.yTile = blockpos.getY();
				ReflectionHelper.setPrivateValue(EntityThrowable.class, this, blockpos.getZ(), 2); // this.zTile = blockpos.getZ();
		        IBlockState iblockstate = this.world.getBlockState(blockpos);
		        ReflectionHelper.setPrivateValue(EntityThrowable.class, this, iblockstate.getBlock(), 3); //this.inTile = iblockstate.getBlock();
				//ReflectionHelper.setPrivateValue(EntityThrowable.class, this, 900, 8); // this.ticksInGround = 900;
		        this.motionX = 0.0d;
		        this.motionY = 0.0d;
		        this.motionZ = 0.0d;
		        this.posX = result.hitVec.x;
		        this.posY = result.hitVec.y;
		        this.posZ = result.hitVec.z;
		        this.inGround = true;
		        if (iblockstate.getMaterial() != Material.AIR) {
		            iblockstate.getBlock().onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
		        }
			} else {
		        this.playSound(SoundEvents.BLOCK_SNOW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
				this.motionX *= 0.4d;
				this.motionY *= 0.4d;
				this.motionZ *= 0.4d;
		        this.posX = result.hitVec.x;
		        this.posY = result.hitVec.y;
		        this.posZ = result.hitVec.z;
				if (result.sideHit.getAxis() == EnumFacing.Axis.X) {
					this.motionX *= -0.8d;
				}
				if (result.sideHit.getAxis() == EnumFacing.Axis.Y) {
					this.motionY *= -0.8d;
				}
				if (result.sideHit.getAxis() == EnumFacing.Axis.Z) {
					this.motionZ *= -0.8d;
				}
			}
		}

		@Override
		public void onUpdate() {
			if (!this.world.isRemote) {
				ProcedureSync.ResetBoundingBox.sendToTracking(this);
			}
			super.onUpdate();
			if (!this.world.isRemote && this.ticksExisted > 3 && this.ticksExisted < 80) {
				for (int index0 = 0; index0 < 30; index0++) {
					ItemPoisonSenbon.spawnArrow(this, true);
				}
			}
			if (!this.world.isRemote && this.ticksExisted > 200) {
				this.setDead();
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
			RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> new RenderCustom(renderManager));
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
}
