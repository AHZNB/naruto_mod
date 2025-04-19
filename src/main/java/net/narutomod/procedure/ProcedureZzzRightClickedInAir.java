package net.narutomod.procedure;

import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;

import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Map;
import java.util.List;
import java.util.Comparator;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureZzzRightClickedInAir extends ElementsNarutomodMod.ModElement {
	public ProcedureZzzRightClickedInAir(ElementsNarutomodMod instance) {
		super(instance, 715);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ZzzRightClickedInAir!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure ZzzRightClickedInAir!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure ZzzRightClickedInAir!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure ZzzRightClickedInAir!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure ZzzRightClickedInAir!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ZzzRightClickedInAir!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		double id = 0;
		(entity).extinguish();
		if ((!(world.isRemote))) {
			Entity entity1 = ProcedureUtils.objectEntityLookingAt(entity, 64d).entityHit;
			if (entity1 instanceof EntityLivingBase) {
				id = (double) ((itemstack).hasTagCompound() ? (itemstack).getTagCompound().getDouble("attackerID") : -1);
				if (((id) < 0)) {
					if ((!(entity1 instanceof EntityLiving))) {
						entity1 = entity1.getControllingPassenger();
					}
					if ((entity1 instanceof EntityLiving)) {
						{
							ItemStack _stack = (itemstack);
							if (!_stack.hasTagCompound())
								_stack.setTagCompound(new NBTTagCompound());
							_stack.getTagCompound().setDouble("attackerID", entity1.getEntityId());
						}
						if (entity instanceof EntityPlayer && !entity.world.isRemote) {
							((EntityPlayer) entity).sendStatusMessage(
									new TextComponentString((("set attacker to ") + "" + ((entity1.getDisplayName().getUnformattedText())))),
									(false));
						}
					}
				} else {
					Entity attacker = entity.world.getEntityByID((int) id);
					if ((!(attacker instanceof EntityLiving))) {
						{
							ItemStack _stack = (itemstack);
							if (!_stack.hasTagCompound())
								_stack.setTagCompound(new NBTTagCompound());
							_stack.getTagCompound().setDouble("attackerID", (-1));
						}
					} else if (!attacker.equals(entity1)) {
						{
							ItemStack _stack = (itemstack);
							if (!_stack.hasTagCompound())
								_stack.setTagCompound(new NBTTagCompound());
							_stack.getTagCompound().setDouble("attackerID", (-1));
						}
						if (entity instanceof EntityPlayer && !entity.world.isRemote) {
							((EntityPlayer) entity).sendStatusMessage(
									new TextComponentString((("set target to ") + "" + ((entity1.getDisplayName().getUnformattedText())))), (false));
						}
						{
							List<Entity> _entfound = world.getEntitiesWithinAABB(Entity.class,
									new AxisAlignedBB(x - (50 / 2d), y - (50 / 2d), z - (50 / 2d), x + (50 / 2d), y + (50 / 2d), z + (50 / 2d)), null)
									.stream().sorted(new Object() {
										Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
											return Comparator.comparing((Function<Entity, Double>) (_entcnd -> _entcnd.getDistanceSq(_x, _y, _z)));
										}
									}.compareDistOf(x, y, z)).collect(Collectors.toList());
							for (Entity entityiterator : _entfound) {
								if (entityiterator.getClass() == attacker.getClass()) {
									((EntityLiving) entityiterator).setAttackTarget((EntityLivingBase) entity1);
								}
							}
						}
					}
				}
			}
		}
	}
}
