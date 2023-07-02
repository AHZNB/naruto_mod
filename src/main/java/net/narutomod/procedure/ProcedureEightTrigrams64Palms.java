package net.narutomod.procedure;

import net.narutomod.item.ItemByakugan;
import net.narutomod.entity.EntityEightTrigrams;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureEightTrigrams64Palms extends ElementsNarutomodMod.ModElement {
	public ProcedureEightTrigrams64Palms(ElementsNarutomodMod instance) {
		super(instance, 261);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure EightTrigrams64Palms!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure EightTrigrams64Palms!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double cooldown = 0;
		boolean f1 = false;
		ItemStack helmetstack = ItemStack.EMPTY;
		helmetstack = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
		f1 = ProcedureUtils.isOriginalOwner((EntityPlayer) entity, helmetstack);
		if (((!(world.isRemote)) && (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)
				|| ((PlayerTracker.getBattleXp((EntityPlayer) entity) >= 1000) && (f1))))) {
			cooldown = (double) ((helmetstack).hasTagCompound() ? (helmetstack).getTagCompound().getDouble("HakkeRokujuuyonshouCD") : -1);
			if ((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).capabilities.isCreativeMode : false)
					|| (((NarutomodModVariables.world_tick) > (cooldown)) || ((NarutomodModVariables.world_tick) < ((cooldown) - 1200))))) {
				if (Chakra.pathway((EntityLivingBase) entity).consume(ItemByakugan.getRokujuyonshoChakraUsage((EntityLivingBase) entity))) {
					world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
							(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
									.getObject(new ResourceLocation("narutomod:HakkeRokujuuyonShou")),
							SoundCategory.NEUTRAL, (float) 1, (float) 1);
					if (entity instanceof EntityLivingBase)
						((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, (int) 240, (int) 3));
					EntityEightTrigrams.EntityCustom entityToSpawn = new EntityEightTrigrams.EntityCustom((EntityLivingBase) entity);
					entity.world.spawnEntity(entityToSpawn);
					cooldown = ProcedureUtils.getCooldownModifier((EntityPlayer) entity);
					{
						ItemStack _stack = (helmetstack);
						if (!_stack.hasTagCompound())
							_stack.setTagCompound(new NBTTagCompound());
						_stack.getTagCompound().setDouble("HakkeRokujuuyonshouCD", ((NarutomodModVariables.world_tick) + ((cooldown) * 1200)));
					}
				} else if ((entity instanceof EntityPlayer)) {
					Chakra.pathway((EntityPlayer) entity).warningDisplay();
				}
			} else {
				if (((!(world.isRemote)) && (entity instanceof EntityPlayer))) {
					((EntityPlayer) entity).sendStatusMessage(
							new TextComponentTranslation("chattext.cooldown.formatted", (cooldown - NarutomodModVariables.world_tick) / 20), true);
				}
			}
		}
	}
}
