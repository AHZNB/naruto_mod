package net.narutomod.procedure;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureInstantDamageOnPotionActiveTick extends ElementsNarutomodMod.ModElement {
	public ProcedureInstantDamageOnPotionActiveTick(ElementsNarutomodMod instance) {
		super(instance, 884);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure InstantDamageOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("amplifier") == null) {
			System.err.println("Failed to load dependency amplifier for procedure InstantDamageOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure InstantDamageOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure InstantDamageOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure InstantDamageOnPotionActiveTick!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure InstantDamageOnPotionActiveTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		int amplifier = (int) dependencies.get("amplifier");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		double amp = 0;
		amp = (double) (amplifier);
		entity.hurtResistantTime = 10;
		entity.attackEntityFrom(ItemJutsu.NINJUTSU_DAMAGE.setDamageBypassesArmor(), (float) amp + 1f);
		if ((Math.random() <= 0.1)) {
			world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.generic.explode")),
					SoundCategory.NEUTRAL, (float) 0.1, (float) ((Math.random() * 0.6) + 0.6));
		}
		if (world instanceof WorldServer)
			((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, (int) 10, 0.3, 1, 0.3, 0.1, new int[0]);
	}
}
