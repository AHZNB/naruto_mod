
package net.narutomod.gui;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemSuiton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class GuiScrollWaterPrisonGui extends ElementsNarutomodMod.ModElement {
	public static int GUIID = 37;

	public GuiScrollWaterPrisonGui(ElementsNarutomodMod instance) {
		super(instance, 507);
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
			ItemStack stack = GuiNinjaScroll.enableJutsu(player, (ItemSuiton.RangedItem)ItemSuiton.block, ItemSuiton.WATERPRISON, true);
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
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/blocks/suiton.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 89, this.guiTop + 49, 0, 0, 48, 48, 48, 48);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/si_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 0, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/wei_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 32, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/wu_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 64, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/mao_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 96, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/wei_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 128, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/wu_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 160, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/mao_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 192, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			this.fontRenderer.drawString(ItemSuiton.WATERPRISON.getName(), 38, 13, -16777216);
		}
	}
}
