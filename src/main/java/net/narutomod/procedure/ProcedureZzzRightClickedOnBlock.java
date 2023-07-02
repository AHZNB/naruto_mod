package net.narutomod.procedure;

import net.narutomod.entity.EntityToad;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;

import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Map;
import java.util.List;
import java.util.Comparator;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureZzzRightClickedOnBlock extends ElementsNarutomodMod.ModElement {
	public ProcedureZzzRightClickedOnBlock(ElementsNarutomodMod instance) {
		super(instance, 713);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure ZzzRightClickedOnBlock!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure ZzzRightClickedOnBlock!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure ZzzRightClickedOnBlock!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure ZzzRightClickedOnBlock!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ZzzRightClickedOnBlock!");
			return;
		}
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		double id = 0;
		id = (double) ((itemstack).hasTagCompound() ? (itemstack).getTagCompound().getDouble("attackerID") : -1);
		if (((id) < 0)) {
			{
				List<Entity> _entfound = world
						.getEntitiesWithinAABB(Entity.class,
								new AxisAlignedBB(x - (64 / 2d), y - (64 / 2d), z - (64 / 2d), x + (64 / 2d), y + (64 / 2d), z + (64 / 2d)), null)
						.stream().sorted(new Object() {
							Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
								return Comparator.comparing((Function<Entity, Double>) (_entcnd -> _entcnd.getDistanceSq(_x, _y, _z)));
							}
						}.compareDistOf(x, y, z)).collect(Collectors.toList());
				for (Entity entityiterator : _entfound) {
					if ((entityiterator instanceof EntityLiving)) {
						if ((entityiterator instanceof EntityToad.EntityCustom)) {
							((EntityToad.EntityCustom) entityiterator).getToadNavigator().setNavigateTarget(0.5d + x, y + 1, 0.5d + z);
						} else {
							((EntityLiving) entityiterator).getNavigator().tryMoveToXYZ(0.5d + x, y + 1, 0.5d + z, 1.0f);
						}
					}
				}
			}
		} else {
			Entity attacker = world.getEntityByID((int) id);
			if ((attacker instanceof EntityLiving)) {
				{
					ItemStack _stack = (itemstack);
					if (!_stack.hasTagCompound())
						_stack.setTagCompound(new NBTTagCompound());
					_stack.getTagCompound().setDouble("attackerID", (-1));
				}
				if ((attacker instanceof EntityToad.EntityCustom)) {
					((EntityToad.EntityCustom) attacker).getToadNavigator().setNavigateTarget(0.5d + x, y + 1, 0.5d + z);
				} else {
					((EntityLiving) attacker).getNavigator().tryMoveToXYZ(0.5d + x, y + 1, 0.5d + z, 1.0f);
				}
			}
		}
	}
}
