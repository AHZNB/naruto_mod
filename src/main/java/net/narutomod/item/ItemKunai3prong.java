
package net.narutomod.item;

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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;

import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKunai3prong extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:kunai_3prong")
	public static final Item block = null;
	public static final int ENTITYID = 418;

	public ItemKunai3prong(ElementsNarutomodMod instance) {
		super(instance, 839);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletkunai_3prong"), ENTITYID).name("entitybulletkunai_3prong").tracker(64, 1, true)
				.build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kunai_3prong", "inventory"));
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			super();
			setMaxDamage(100);
			setFull3D();
			setUnlocalizedName("kunai_3prong");
			setRegistryName("kunai_3prong");
			maxStackSize = 3;
			setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", 6d , 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
			}
			return multimap;
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				float power = ItemBow.getArrowVelocity(this.getMaxItemUseDuration(itemstack) - timeLeft) * 1.5f;
				EntityCustom entityarrow = new EntityCustom(entity);
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 2, 0);
				entityarrow.setSilent(true);
				entityarrow.setIsCritical(false);
				entityarrow.setDamage(7);
				entityarrow.setKnockbackStrength(0);
				itemstack.damageItem(1, entity);
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
		public EntityCustom(World a) {
			super(a);
		}

		public EntityCustom(EntityPlayer shooter) {
			super(shooter.world, shooter);
		}

		@Override
		protected void arrowHit(EntityLivingBase entity) {
			super.arrowHit(entity);
			entity.setArrowCountInEntity(entity.getArrowCountInEntity() - 1);
		}

		@Override
		protected ItemStack getArrowStack() {
			return new ItemStack(block);
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
	}
}
