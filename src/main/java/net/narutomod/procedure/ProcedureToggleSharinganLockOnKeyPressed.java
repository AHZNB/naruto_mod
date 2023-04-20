package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import java.util.Map;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
@ElementsNarutomodMod.ModElement.Tag
public class ProcedureToggleSharinganLockOnKeyPressed extends ElementsNarutomodMod.ModElement {
	public ProcedureToggleSharinganLockOnKeyPressed(ElementsNarutomodMod instance) {
		super(instance, 842);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if(dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ToggleSharinganLockOnKeyPressed!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (entity instanceof EntityPlayer) {
			if(entity.getEntityData().getBoolean("sharinganLockToggled") == false) {
				entity.getEntityData().setBoolean("sharinganLockToggled", true);
			} else {
				entity.getEntityData().setBoolean("sharinganLockToggled", false);
			}
			((EntityPlayer) entity).sendStatusMessage(new TextComponentString("Sharingan Lock Toggled is: " + entity.getEntityData().getBoolean("sharinganLockToggled")) ? "ON" : "OFF", (true));
		}
	}
}