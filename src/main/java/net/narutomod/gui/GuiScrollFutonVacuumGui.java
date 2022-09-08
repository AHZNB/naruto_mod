
package net.narutomod.gui;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemFuton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class GuiScrollFutonVacuumGui extends ElementsNarutomodMod.ModElement {
	public static int GUIID = 25;

	public GuiScrollFutonVacuumGui(ElementsNarutomodMod instance) {
		super(instance, 484);
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
			ItemStack stack = ProcedureUtils.getMatchingItemStack(player, ItemFuton.block);
			if (stack == null && PlayerTracker.isNinja(player)) {
				stack = new ItemStack(ItemFuton.block, 1);
				((ItemFuton.RangedItem)stack.getItem()).setOwner(stack, player);
				ItemHandlerHelper.giveItemToPlayer(player, stack);
			}
			if (stack != null) {
				((ItemFuton.RangedItem)stack.getItem()).enableJutsu(stack, ItemFuton.VACUUMS, true);
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
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/blocks/futon.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 89, this.guiTop + 49, 0, 0, 48, 48, 48, 48);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/zi_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 0, this.guiTop + 108, 0, 0, 48, 48, 48, 48);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/mao_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 56, this.guiTop + 108, 0, 0, 48, 48, 48, 48);
			this.mc.renderEngine.bindTexture(new ResourceLocation("narutomod:textures/xu_.png"));
			this.drawModalRectWithCustomSizedTexture(this.guiLeft + 112, this.guiTop + 108, 0, 0, 48, 48, 48, 48);
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			this.fontRenderer.drawString(ItemFuton.VACUUMS.getName(), 38, 13, -16777216);
		}
	}
}
