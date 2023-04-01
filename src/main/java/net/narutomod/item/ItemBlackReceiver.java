
package net.narutomod.item;

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
import net.minecraft.util.EntitySelectors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.RenderItem;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Multimap;
//import java.util.Collection;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBlackReceiver extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:black_receiver")
	public static final Item block = null;
	public static final int ENTITYID = 137;

	public ItemBlackReceiver(ElementsNarutomodMod instance) {
		super(instance, 381);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletblack_receiver"), ENTITYID).name("entitybulletblack_receiver")
				.tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:black_receiver", "inventory"));
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			super();
			setMaxDamage(50);
			setFull3D();
			setUnlocalizedName("black_receiver");
			setRegistryName("black_receiver");
			maxStackSize = 1;
			setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", 10d, 0));
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
				EntityArrowCustom entityarrow = new EntityArrowCustom(world, entity);
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 2, 0);
				entityarrow.setSilent(true);
				entityarrow.setIsCritical(true);
				entityarrow.setDamage(10);
				entityarrow.setKnockbackStrength(0);
				itemstack.damageItem(1, entity);
				world.playSound(null, entity.posX, entity.posY, entity.posZ,
				 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hand_shoot")),
				 SoundCategory.NEUTRAL, 1, 1f / (itemRand.nextFloat() * 0.5f + 1f) + (power / 2));
				entityarrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
				world.spawnEntity(entityarrow);
				entity.getCooldownTracker().setCooldown(itemstack.getItem(), 40);
			}
		}

		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			super.hitEntity(stack, target, attacker);
			onHitEntity(target);
			return true;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote) {
				if (entity instanceof EntityPlayer) {
					if (!ProcedureUtils.hasItemInInventory((EntityPlayer)entity, ItemRinnegan.helmet)
					 && !ProcedureUtils.hasItemInInventory((EntityPlayer)entity, ItemTenseigan.helmet)
					 && !((EntityPlayer)entity).isCreative() && entity.ticksExisted % 20 == 7) {
			 			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 1, false, false));
			 		}
				} else if (entity instanceof EntityLiving) {
					((EntityLiving)entity).setNoAI(true);
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

		@Override
		public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
			return false;
		}
	}

	protected static void onHitEntity(EntityLivingBase entity) {
		int amplifier = 1;
		if (entity.isPotionActive(PotionHeaviness.potion)) {
			amplifier += entity.getActivePotionEffect(PotionHeaviness.potion).getAmplifier();
		}
		entity.addPotionEffect(new PotionEffect(PotionHeaviness.potion, 300, amplifier, false, false));
		//if (entity.isPotionActive(MobEffects.JUMP_BOOST) && entity.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() > -5) {
		//	entity.removePotionEffect(MobEffects.JUMP_BOOST);
		//}
		//entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 600, -5, false, false));
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
			onHitEntity(entity);
		}

		@Override
		protected ItemStack getArrowStack() {
			return new ItemStack(block);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class,
				 this.getEntityBoundingBox().grow(0.75d), EntitySelectors.getTeamCollisionPredicate(this))) {
					if (!entity.equals(this.shootingEntity)) {
			 			entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 1, false, false));
					}
				}
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
				GlStateManager.translate((float) x, (float) y, (float) z);
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
