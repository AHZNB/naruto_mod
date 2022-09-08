package net.narutomod.procedure;

import net.narutomod.item.ItemTenseiganChakraMode;
import net.narutomod.item.ItemBijuCloak;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureTenseiganBodyTickEvent extends ElementsNarutomodMod.ModElement {
	public ProcedureTenseiganBodyTickEvent(ElementsNarutomodMod instance) {
		super(instance, 694);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure TenseiganBodyTickEvent!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure TenseiganBodyTickEvent!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		if ((!(((entity instanceof EntityLivingBase) ? ((EntityLivingBase) entity).getHeldItemMainhand() : ItemStack.EMPTY)
				.getItem() == new ItemStack(ItemTenseiganChakraMode.block, (int) (1)).getItem()))) {
			((itemstack)).shrink((int) 1);
		} else {
			(entity).extinguish();
			Particles.spawnParticle(entity.world, Particles.Types.SMOKE, entity.posX, entity.posY + 0.8d, entity.posZ, 20, 0.15d, 0.4d, 0.15d, 0d,
					0.1d, 0d, 0x20b5fff5, 20, 5, 0xF0, entity.getEntityId());
			ItemBijuCloak.applyEffects((EntityLivingBase) entity, 2, false);
		}
	}
}
