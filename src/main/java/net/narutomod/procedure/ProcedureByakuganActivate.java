package net.narutomod.procedure;

import net.narutomod.item.ItemByakugan;
import net.narutomod.gui.overlay.OverlayByakuganView;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureByakuganActivate extends ElementsNarutomodMod.ModElement {
	public ProcedureByakuganActivate(ElementsNarutomodMod instance) {
		super(instance, 107);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure ByakuganActivate!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ByakuganActivate!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure ByakuganActivate!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure ByakuganActivate!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure ByakuganActivate!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ByakuganActivate!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		boolean activated = false;
		double chakraAmount = 0;
		activated = (boolean) (entity.getEntityData().getBoolean("byakugan_activated"));
		chakraAmount = Chakra.pathway((EntityPlayer) entity).getAmount();
		if (((is_pressed) && ((chakraAmount) >= ItemByakugan.getByakuganChakraUsage((EntityLivingBase) entity) * 2))) {
			if ((!(activated))) {
				OverlayByakuganView.sendCustomData(entity, true, 110f);
				entity.getEntityData().setBoolean("byakugan_activated", (true));
				entity.getEntityData().setDouble("byakugan_fov", 110);
				world.playSound((EntityPlayer) null, x, y, z,
						(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:byakugan")),
						SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
		} else if ((activated)) {
			entity.getEntityData().setBoolean("byakugan_activated", (false));
			OverlayByakuganView.sendCustomData(entity, false, 0f);
		}
	}
}
