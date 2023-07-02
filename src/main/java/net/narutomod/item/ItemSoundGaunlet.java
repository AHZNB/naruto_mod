
package net.narutomod.item;

import net.narutomod.potion.PotionParalysis;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSoundGaunlet extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:sound_gaunlet")
	public static final Item block = null;
	public static final int ENTITYID = 397;

	public ItemSoundGaunlet(ElementsNarutomodMod instance) {
		super(instance, 777);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:sound_gaunlet", "inventory"));
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			super();
			this.setMaxDamage(200);
			this.setFull3D();
			this.setUnlocalizedName("sound_gaunlet");
			this.setRegistryName("sound_gaunlet");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
			if (net.narutomod.Chakra.pathway(entity).consume(40d)) {
				Vec3d vec0 = entity.getPositionEyes(1f);
				Vec3d vec1 = entity.getLookVec();
				Vec3d vec2 = vec1.add(vec0);
				entity.world.playSound(null, vec2.x, vec2.y, vec2.z,
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:highpitch")),
					 SoundCategory.NEUTRAL, 1.0f, 1.0f + this.itemRand.nextFloat() * 0.3f);
				for (int i = 1, j = 20; i <= j; i++) {
					float f = (float)i / j;
					Vec3d vec3 = vec1.scale(1.5d * f);
					Particles.spawnParticle(entity.world, Particles.Types.SONIC_BOOM, vec2.x, vec2.y, vec2.z,
					 1, 0d, 0d, 0d, vec3.x, vec3.y, vec3.z, 0x00ffffff | ((int)((1f - f) * 0x20) << 24),
					 i * 2, (int)(5f * (1f + f * 0.5f)));
				}
				vec1 = vec1.scale(15d);
				vec2 = vec1.add(vec0);
				for (EntityLivingBase entity1 : entity.world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().expand(vec1.x, vec1.y, vec1.z))) {
					double d = vec0.distanceTo(entity1.getPositionVector());
					if (!entity1.equals(entity) && ItemJutsu.canTarget(entity1)
					 && entity1.getEntityBoundingBox().grow(4d * d / 15d).calculateIntercept(vec0, vec2) != null) {
						double d1 = (20d - d) * 0.5d / entity1.height;
						entity1.addPotionEffect(new PotionEffect(PotionParalysis.potion, (int)d1, 0));
						entity1.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int)d1 + 5, 0));
						entity1.hurtResistantTime = 10;
						entity1.attackEntityFrom(ItemJutsu.causeJutsuDamage(entity, null).setDamageBypassesArmor(), (float)d1 * 3f);
					}
				}
				if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isCreative()) {
					stack.damageItem(1, entity);
				}
			}
			return false;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			entity.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, entity.getHeldItem(hand));
		}

		@Override
		public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
			return stack.getItem() == block;
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BLOCK;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}
	}
}
