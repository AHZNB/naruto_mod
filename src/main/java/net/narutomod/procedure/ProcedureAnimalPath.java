package net.narutomod.procedure;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.entity.EntityGiantDog2h;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureAnimalPath extends ElementsNarutomodMod.ModElement {
	public ProcedureAnimalPath(ElementsNarutomodMod instance) {
		super(instance, 224);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure AnimalPath!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure AnimalPath!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double summoned_id = 0;
		double x = 0;
		double z = 0;
		double h = 0;
		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase) entity).swingArm(EnumHand.MAIN_HAND);
		}
		summoned_id = (double) (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
				.hasTagCompound()
						? ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
								.getTagCompound().getDouble("SummonedAnimal_id")
						: -1);
		if (((summoned_id) <= 0)) {
			if ((!(world.isRemote))) {
				if (Chakra.pathway((EntityPlayer) entity).consume(ItemRinnegan.getAnimalPathChakraUsage((EntityLivingBase) entity))) {
					world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
							(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
									.getObject(new ResourceLocation("narutomod:kuchiyosenojutsu")),
							SoundCategory.NEUTRAL, (float) 2, (float) 0.8);
					EntityGiantDog2h.EntityCustom entityToSpawn = new EntityGiantDog2h.EntityCustom((EntityPlayer) entity);
					x = entityToSpawn.posX;
					z = entityToSpawn.posZ;
					h = entity.height;
					Particles.spawnParticle(world, Particles.Types.SEAL_FORMULA, x, entityToSpawn.posY + 0.015d, z, 1, 0d, 0d, 0d, 0d, 0d, 0d, 200, 0,
							60);
					if (world instanceof WorldServer)
						((WorldServer) world).spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, entity.posY + (h / 2), z, 300, 4, h, 4, 1,
								new int[0]);
					entity.world.spawnEntity(entityToSpawn);
					summoned_id = entityToSpawn.getEntityId();
					{
						ItemStack _stack = ((entity instanceof EntityPlayer)
								? ((EntityPlayer) entity).inventory.armorInventory.get(3)
								: ItemStack.EMPTY);
						if (!_stack.hasTagCompound())
							_stack.setTagCompound(new NBTTagCompound());
						_stack.getTagCompound().setDouble("SummonedAnimal_id", (summoned_id));
					}
				} else {
					Chakra.pathway((EntityPlayer) entity).warningDisplay();
				}
			}
		} else {
			{
				ItemStack _stack = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
				if (!_stack.hasTagCompound())
					_stack.setTagCompound(new NBTTagCompound());
				_stack.getTagCompound().setDouble("SummonedAnimal_id", 0);
			}
			entity = world.getEntityByID((int) summoned_id);
			if ((entity instanceof EntityGiantDog2h.EntityCustom)) {
				if (world instanceof WorldServer)
					((WorldServer) world).spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, entity.posX, entity.posY + 15, entity.posZ, 200, 4, 15, 4,
							1, new int[0]);
				(entity).world.removeEntity(entity);
			}
		}
	}
}
