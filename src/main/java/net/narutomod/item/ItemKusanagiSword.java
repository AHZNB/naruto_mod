
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.network.play.server.SPacketCollectItem;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Multimap;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKusanagiSword extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:kusanagi_sword")
	public static final Item block = null;
	public static final int ENTITYID = 346;

	public ItemKusanagiSword(ElementsNarutomodMod instance) {
		super(instance, 702);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletkusanagi_sword"), ENTITYID).name("entitybulletkusanagi_sword").tracker(64, 1, true)
				.build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kusanagi_sword", "inventory"));
	}

	public static class RangedItem extends Item implements ItemOnBody.Interface {
		public RangedItem() {
			super();
			this.setMaxDamage(0);
			this.setFull3D();
			this.setUnlocalizedName("kusanagi_sword");
			this.setRegistryName("kusanagi_sword");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", (double) 19, 0));
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
				EntityCustom entityarrow = new EntityCustom(entity);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, net.minecraft.util.SoundEvent.REGISTRY
				 .getObject(new ResourceLocation("entity.arrow.shoot")), SoundCategory.NEUTRAL,
				 1, 1f / (itemRand.nextFloat() * 0.5f + 1f) + (power / 2));
				world.spawnEntity(entityarrow);
				itemstack.shrink(1);
			}
		}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean hasEffect(ItemStack itemstack) {
			return itemstack.hasTagCompound() ? itemstack.getTagCompound().getBoolean("inAir") : false;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.kusanagi.description"));
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

	public static class EntityCustom extends EntityScalableProjectile.Base {
		private Entity target;
		private int targetCD;
		private final double maxRange = 30d;
		
		public EntityCustom(World a) {
			super(a);
			this.setOGSize(0.25F, 0.25F);
			this.setWaterSlowdown(0.8f);
			this.isImmuneToFire = true;
			this.maxInGroundTime = Integer.MAX_VALUE - 1;
		}

		public EntityCustom(EntityLivingBase shooter) {
			super(shooter);
			this.setOGSize(0.25F, 0.25F);
			Vec3d vec = shooter.getLookVec().scale(2d).add(shooter.getPositionEyes(1f));
			this.setLocationAndAngles(vec.x, vec.y, vec.z, shooter.rotationYawHead, shooter.rotationPitch);
			this.setWaterSlowdown(0.8f);
			this.isImmuneToFire = true;
			this.maxInGroundTime = Integer.MAX_VALUE - 1;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null && !this.onGround) {
				double d = this.getDistance(this.shootingEntity);
				if (d > this.maxRange + 10) {
					this.target = this.shootingEntity;
				} else if (d <= this.maxRange && this.target == this.shootingEntity) {
					this.target = null;
				}
				if (this.ticksInAir > 200) {
					this.target = this.shootingEntity;
				}
				if (this.target != null && this.target.isEntityAlive()) {
					if (this.target.getDistance(this.shootingEntity) <= this.maxRange) {
						Vec3d vec = this.target.getPositionEyes(1f).subtract(this.getPositionVector());
						this.motionX *= 0.1d;
						this.motionY *= 0.1d;
						this.motionZ *= 0.1d;
						this.shoot(vec.x, vec.y, vec.z, 9.5f, 0f);
					} else {
						this.target = null;
						this.haltMotion();
					}
				} else {
					if (--this.targetCD <= 0) {
						this.target = this.shootingEntity instanceof EntityLiving ? ((EntityLiving)this.shootingEntity).getAttackTarget()
						 : ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 30d, this).entityHit;
					}
					Vec3d vec = this.target != null ? this.target.getPositionEyes(1f).subtract(this.getPositionVector())
					 : this.shootingEntity.getLookVec();
					this.shoot(vec.x, vec.y, vec.z, 0.6f, 0f);
				}
			}
			if (!this.world.isRemote
			 && (this.shootingEntity == null || !this.shootingEntity.isEntityAlive())) {
				this.setNoGravity(false);
				this.shootingEntity = null;
			}
		}

		@Override
		public void renderParticles() {
		}

		@Override
		public void onCollideWithPlayer(EntityPlayer entityIn) {
			if (!this.world.isRemote) {
				boolean flag = false;
				if ((this.shootingEntity == null && this.onGround)
				 || (entityIn.equals(this.shootingEntity) && this.ticksExisted > 15)) {
					flag = entityIn.inventory.addItemStackToInventory(new ItemStack(block));
				}
				if (flag) {
	            	((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketCollectItem(this.getEntityId(), entityIn.getEntityId(), 1));
					this.setDead();
				}
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && result.entityHit.equals(this.shootingEntity)) {
				return;
			}
			if (!this.world.isRemote) {
				if (result.entityHit != null) {
					result.entityHit.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.shootingEntity).setDamageBypassesArmor(), 20f);
					if (result.entityHit.equals(this.target)) {
						this.target = null;
						this.haltMotion();
						this.targetCD = 10;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager, Minecraft.getMinecraft().getRenderItem()));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityCustom> {
			protected final ItemStack itemstack;
			private final RenderItem itemRenderer;

			public RenderCustom(RenderManager renderManagerIn, RenderItem itemRendererIn) {
				super(renderManagerIn);
				this.itemstack = new ItemStack(block);
				this.itemstack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
				this.itemstack.getTagCompound().setBoolean("inAir", true);
				this.itemRenderer = itemRendererIn;
			}

			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float pt) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) x, (float) y, (float) z);
				GlStateManager.rotate(-entity.prevRotationYaw - MathHelper.wrapDegrees(entity.rotationYaw - entity.prevRotationYaw) * pt + 180F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-entity.prevRotationPitch - (entity.rotationPitch - entity.prevRotationPitch) * pt + 90F, 1.0F, 0.0F, 0.0F);
				GlStateManager.enableRescaleNormal();
				this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				this.itemRenderer.renderItem(this.itemstack, ItemCameraTransforms.TransformType.GROUND);
				GlStateManager.enableLighting();
				GlStateManager.disableRescaleNormal();
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return TextureMap.LOCATION_BLOCKS_TEXTURE;
			}
		}
	}
}
