package net.narutomod.procedure;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.entity.EntityPretaShield;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedurePretaPath extends ElementsNarutomodMod.ModElement {
	public ProcedurePretaPath(ElementsNarutomodMod instance) {
		super(instance, 239);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure PretaPath!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure PretaPath!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		boolean f1 = false;
		if ((!(world.isRemote))) {
			if ((entity instanceof EntityLivingBase
					&& Chakra.pathway((EntityLivingBase) entity).consume(ItemRinnegan.getPretaPathChakraUsage((EntityLivingBase) entity)))) {
				if (entity instanceof EntityLivingBase) {
					((EntityLivingBase) entity).swingArm(EnumHand.MAIN_HAND);
				}
				f1 = (entity.getRidingEntity() instanceof EntityPretaShield.EntityCustom);
				if ((!(f1))) {
					Entity entityToSpawn = new EntityPretaShield.EntityCustom((EntityPlayer) entity);
					world.spawnEntity(entityToSpawn);
				}
			} else if ((entity instanceof EntityPlayer)) {
				Chakra.pathway((EntityPlayer) entity).warningDisplay();
			}
		}
	}
}
