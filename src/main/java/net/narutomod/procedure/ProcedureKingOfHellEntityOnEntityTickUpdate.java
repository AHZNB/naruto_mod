package net.narutomod.procedure;

import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureKingOfHellEntityOnEntityTickUpdate extends ElementsNarutomodMod.ModElement {
	public ProcedureKingOfHellEntityOnEntityTickUpdate(ElementsNarutomodMod instance) {
		super(instance, 222);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure KingOfHellEntityOnEntityTickUpdate!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure KingOfHellEntityOnEntityTickUpdate!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		boolean f1 = false;
		Particles.spawnParticle(world, Particles.Types.FLAME, entity.posX, entity.posY, entity.posZ, 100, entity.width / 4, 0.2, entity.width / 4, 0,
				0, 0, 0x80404080, 30);
		if (((EntityKingOfHell.EntityCustom) entity).getAge() == 1) {
			world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:KoH_spawn")),
					SoundCategory.NEUTRAL, (float) 1, (float) 1);
		}
	}
}
