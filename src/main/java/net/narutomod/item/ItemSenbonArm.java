
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

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBow;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;

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

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> {
			return new RenderSnowball(renderManager, new ItemStack(ItemSenbonArm.block, (int) (1)).getItem(),
					Minecraft.getMinecraft().getRenderItem());
		});
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

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			float power = ItemBow.getArrowVelocity(this.getMaxItemUseDuration(itemstack) - timeLeft);
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP && power > 0.2f) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				EntityArrowCustom entityarrow = new EntityArrowCustom(world, entity);
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 3.0f, 2.0f);
				entityarrow.setSilent(true);
				entityarrow.setIsCritical(true);
				entityarrow.setDamage(5);
				entityarrow.setKnockbackStrength(5);
				entityarrow.power = power;
				itemstack.damageItem(1, entity);
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				world.playSound((EntityPlayer) null, (double) x, (double) y, (double) z,
						(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
								.getObject(new ResourceLocation(("narutomod:bullet"))),
						SoundCategory.NEUTRAL, 1, 1f / (itemRand.nextFloat() * 0.5f + 1f) + (power / 2));
				if (entity.capabilities.isCreativeMode) {
					entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
				} else {
					itemstack.shrink(1);
				}
				world.spawnEntity(entityarrow);
			}
			if (entityLivingBase.getRidingEntity() instanceof EntityPuppetHiruko.EntityCustom) {
				((EntityPuppetHiruko.EntityCustom)entityLivingBase.getRidingEntity()).raiseLeftArm(false);
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

	public static class EntityArrowCustom extends EntityTippedArrow {
		public float power;

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
		}

		@Override
		protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
			Entity entity = super.findEntityOnPath(start, end);
			return entity != null && entity.isBeingRidden() && entity.getControllingPassenger().equals(this.shootingEntity)
			 ? null : entity;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.ticksExisted > 10) {
				for (int index0 = 0; index0 < 20; index0++) {
					ItemPoisonSenbon.spawnArrow(this, true);
				}
			}
			if (this.inGround) {
				this.setDead();
			}
		}
	}
}
