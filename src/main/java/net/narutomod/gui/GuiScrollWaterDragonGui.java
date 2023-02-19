
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
public class GuiScrollWaterDragonGui extends ElementsNarutomodMod.ModElement {
	public static int GUIID = 36;

	public GuiScrollWaterDragonGui(ElementsNarutomodMod instance) {
		super(instance, 505);
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
			ItemStack stack = GuiNinjaScroll.enableJutsu(player, (ItemSuiton.RangedItem)ItemSuiton.block, ItemSuiton.WATERDRAGON, true);
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
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/chou_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft - 8, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/shen_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 24, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/mao_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 56, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/zi_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 88, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/hai_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 120, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/you_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 152, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/chou_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 184, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/wu_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 216, this.guiTop + 116, 0, 0, 32, 32, 32, 32);
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			this.fontRenderer.drawString(ItemSuiton.WATERDRAGON.getName(), 38, 13, -16777216);
		}
	}
}
