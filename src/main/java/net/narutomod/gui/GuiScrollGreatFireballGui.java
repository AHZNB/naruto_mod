
package net.narutomod.gui;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemKaton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class GuiScrollGreatFireballGui extends ElementsNarutomodMod.ModElement {
	public static int GUIID = 31;

	public GuiScrollGreatFireballGui(ElementsNarutomodMod instance) {
		super(instance, 496);
	}

	public static class GuiContainerMod extends GuiNinjaScroll.GuiContainerMod {
		public GuiContainerMod(World world, int x, int y, int z, EntityPlayer player) {
			super(world, x, y, z, player, GUIID);
		}

		@Override
		protected void handleButtonAction(EntityPlayer player, int buttonID) {
			// security measure to prevent arbitrary chunk generation
			if (player.world.isRemote || !player.world.isBlockLoaded(new BlockPos(this.x, this.y, this.z)))
				return;
			ItemStack stack = GuiNinjaScroll.enableJutsu(player, (ItemKaton.RangedItem)ItemKaton.block, ItemKaton.GREATFIREBALL, true);
			if (stack != null) {
				super.handleButtonAction(player, buttonID);
			}
		}
	}

	public static class GuiWindow extends GuiNinjaScroll.GuiWindow {
		public GuiWindow(World world, int x, int y, int z, EntityPlayer entity) {
			super(new GuiContainerMod(world, x, y, z, entity));
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
			super.drawGuiContainerBackgroundLayer(par1, par2, par3);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/blocks/katon.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 89, this.guiTop + 49, 0, 0, 48, 48, 48, 48);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/si_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft - 4, this.guiTop + 108, 0, 0, 42, 42, 42, 42);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/wei_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 38, this.guiTop + 108, 0, 0, 42, 42, 42, 42);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/shen_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 80, this.guiTop + 108, 0, 0, 42, 42, 42, 42);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/hai_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 122, this.guiTop + 108, 0, 0, 42, 42, 42, 42);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/wu_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 164, this.guiTop + 108, 0, 0, 42, 42, 42, 42);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/yin_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 206, this.guiTop + 108, 0, 0, 42, 42, 42, 42);
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			this.fontRenderer.drawString(ItemKaton.GREATFIREBALL.getName(), 38, 13, -16777216);
		}
	}
}
