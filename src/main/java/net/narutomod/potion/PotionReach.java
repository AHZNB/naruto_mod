
package net.narutomod.potion;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.Minecraft;

import net.narutomod.ElementsNarutomodMod;
import net.minecraft.entity.player.EntityPlayer;

@ElementsNarutomodMod.ModElement.Tag
public class PotionReach extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:reach")
	public static final Potion potion = null;
	public static final String REACH_MODIFIER = "0a0f65da-11f0-4219-9dc9-dab0efa743c0";

	public PotionReach(ElementsNarutomodMod instance) {
		super(instance, 407);
	}

	@Override
	public void initElements() {
		elements.potions.add(() -> new PotionCustom());
	}

	public static class PotionCustom extends Potion {
		private final ResourceLocation potionIcon;

		public PotionCustom() {
			super(false, -1);
			this.setBeneficial();
			this.setRegistryName("reach");
			this.setPotionName("effect.reach");
			this.potionIcon = new ResourceLocation("narutomod:textures/mob_effect/reach.png");
			this.registerPotionAttributeModifier(EntityPlayer.REACH_DISTANCE, REACH_MODIFIER, 1d, 0);
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
