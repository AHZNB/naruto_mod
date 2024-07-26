
package net.narutomod.potion;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;

import java.util.Map;
import java.util.HashMap;

@ElementsNarutomodMod.ModElement.Tag
public class PotionHeaviness extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:heaviness")
	public static final Potion potion = null;
	@GameRegistry.ObjectHolder("narutomod:heaviness")
	public static final PotionType potionType = null;

	public PotionHeaviness(ElementsNarutomodMod instance) {
		super(instance, 436);
	}

	@Override
	public void initElements() {
		elements.potions.add(() -> new PotionCustom());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ForgeRegistries.POTION_TYPES.register(new PotionTypeCustom());
	}

	public static class PotionTypeCustom extends PotionType {
		public PotionTypeCustom() {
			super(new PotionEffect[]{new PotionEffect(potion, 3600)});
			setRegistryName("heaviness");
		}
	}

	public static class PotionCustom extends Potion {
		private final ResourceLocation potionIcon;
		public PotionCustom() {
			super(true, -10066330);
			setRegistryName("heaviness");
			setPotionName("effect.heaviness");
			potionIcon = new ResourceLocation("narutomod:textures/mob_effect/heaviness.png");
			this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "fedf4303-bc45-4ad8-80e8-2237e9c90a18", -0.15D, 2);
		}

		@Override
		public boolean isInstant() {
			return true;
		}

		@Override
		public boolean shouldRenderInvText(PotionEffect effect) {
			return true;
		}

		@Override
		public boolean shouldRenderHUD(PotionEffect effect) {
			return true;
		}

		@Override
		public void performEffect(EntityLivingBase entity, int amplifier) {
			if (entity.isPotionActive(MobEffects.JUMP_BOOST)) {
				entity.removePotionEffect(MobEffects.JUMP_BOOST);
			}
			entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 2, -2 - amplifier, false, false));
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
			if (mc.currentScreen != null) {
				mc.getTextureManager().bindTexture(potionIcon);
				Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
			mc.getTextureManager().bindTexture(potionIcon);
			Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
		}

		@Override
		public boolean isReady(int duration, int amplifier) {
			return true;
		}
	}
}
