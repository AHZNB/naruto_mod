
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.entity.EntityMindTransfer;
import net.narutomod.entity.EntityShadowImitation;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.potion.PotionParalysis;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemInton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:inton")
	public static final Item block = null;
	public static final int ENTITYID = 172;
	public static final ItemJutsu.JutsuEnum GENJUTSU = new ItemJutsu.JutsuEnum(0, "genjutsu", 'B', 300d, new Genjutsu());
	public static final ItemJutsu.JutsuEnum MBTRANSFER = new ItemJutsu.JutsuEnum(1, "mind_transfer", 'C', 300d, new EntityMindTransfer.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SHADOW_IMITATION = new ItemJutsu.JutsuEnum(2, "shadow_imitation", 'B', 50d, new EntityShadowImitation.EC.Jutsu());

	public ItemInton(ElementsNarutomodMod instance) {
		super(instance, 441);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(GENJUTSU, MBTRANSFER, SHADOW_IMITATION));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:inton", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.INTON, list);
			this.setUnlocalizedName("inton");
			this.setRegistryName("inton");
			this.setCreativeTab(TabModTab.tab);
		}
	}

	public static class Genjutsu implements ItemJutsu.IJutsuCallback {
		private final double maxRange = 30.0d;
		private final int duration = 200;
		private final int cooldown = 1200;

		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			Entity target = ProcedureUtils.objectEntityLookingAt(entity, this.maxRange).entityHit;
			if (target instanceof EntityLivingBase) {
				entity.world.playSound(null, target.posX, target.posY, target.posZ,
				  (SoundEvent) SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:genjutsu")), SoundCategory.NEUTRAL, 1f, 1f);
				((EntityLivingBase)target).addPotionEffect(new PotionEffect(PotionParalysis.potion, this.duration, 1, false, false));
				((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, this.duration + 40, 0, false, true));
				((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, this.duration, 0, false, true));
				if (target instanceof EntityPlayerMP) {
					ProcedureSync.MobAppearanceParticle.send((EntityPlayerMP)target, entity.getEntityId());
				}
				if (entity instanceof EntityPlayer) {
					ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, this.cooldown);
				}
				return true;
			}
			return false;
		}
	}
}
