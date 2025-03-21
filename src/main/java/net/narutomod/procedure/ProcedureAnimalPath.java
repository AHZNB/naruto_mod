package net.narutomod.procedure;

import net.narutomod.item.ItemRinnegan;
import net.narutomod.entity.EntitySummonAnimal;
import net.narutomod.entity.EntityGiantDog2h;
import net.narutomod.entity.EntityGiantChameleon;
import net.narutomod.entity.EntityGiantBird;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
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
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure AnimalPath!");
			return;
		}
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure AnimalPath!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure AnimalPath!");
			return;
		}
		boolean is_pressed = (boolean) dependencies.get("is_pressed");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double summoned_id = 0;
		double x = 0;
		double z = 0;
		double h = 0;
		double pressedTime = 0;
		double d = 0;
		pressedTime = (double) (entity.getEntityData().getDouble("AnimalPathPressedTime"));
		if ((is_pressed)) {
			pressedTime = (double) ((pressedTime) + 1);
			d = (double) ((pressedTime) % 60);
			if (((d) < 20)) {
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer) entity).sendStatusMessage(
							new TextComponentString(net.minecraft.util.text.translation.I18n.translateToLocal("entity.giant_dog_2h.name")), (true));
				}
			} else if (((d) < 40)) {
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer) entity).sendStatusMessage(
							new TextComponentString(net.minecraft.util.text.translation.I18n.translateToLocal("entity.giant_chameleon.name")),
							(true));
				}
			} else {
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer) entity).sendStatusMessage(
							new TextComponentString(net.minecraft.util.text.translation.I18n.translateToLocal("entity.giant_bird.name")), (true));
				}
			}
		} else {
			if (entity instanceof EntityLivingBase) {
				((EntityLivingBase) entity).swingArm(EnumHand.MAIN_HAND);
			}
			if ((!(world.isRemote))) {
				d = (double) ((pressedTime) % 60);
				EntitySummonAnimal.Base entityToSpawn = d < 20
						? new EntityGiantDog2h.EntityCustom((EntityLivingBase) entity)
						: d < 40
								? new EntityGiantChameleon.EntityCustom((EntityLivingBase) entity)
								: new EntityGiantBird.EntityCustom((EntityLivingBase) entity);
				if (EntitySummonAnimal.getAllSummons(entity, entityToSpawn.getClass()).isEmpty()) {
					if (Chakra.pathway((EntityPlayer) entity).consume(ItemRinnegan.getAnimalPathChakraUsage((EntityLivingBase) entity))) {
						world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
								(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
										.getObject(new ResourceLocation("narutomod:kuchiyosenojutsu")),
								SoundCategory.NEUTRAL, (float) 2, (float) 0.8);
						x = entityToSpawn.posX;
						z = entityToSpawn.posZ;
						h = entity.height;
						Particles.spawnParticle(world, Particles.Types.SEAL_FORMULA, x, entityToSpawn.posY + 0.015d, z, 1, 0d, 0d, 0d, 0d, 0d, 0d,
								200, 0, 60);
						if (world instanceof WorldServer)
							((WorldServer) world).spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, entity.posY + (h / 2), z, 300, 4, h, 4, 1,
									new int[0]);
						entity.world.spawnEntity(entityToSpawn);
						summoned_id = entityToSpawn.getEntityId();
					} else {
						Chakra.pathway((EntityPlayer) entity).warningDisplay();
					}
				} else {
					EntitySummonAnimal.unSummonAll(entity, entityToSpawn.getClass());
				}
			}
			pressedTime = (double) 0;
		}
		entity.getEntityData().setDouble("AnimalPathPressedTime", (pressedTime));
	}
}
