package net.narutomod.procedure;

import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.entity.EntitySusanooWinged;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureKagutsuchiSwordToolInUseTick extends ElementsNarutomodMod.ModElement {
	public ProcedureKagutsuchiSwordToolInUseTick(ElementsNarutomodMod instance) {
		super(instance, 237);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure KagutsuchiSwordToolInUseTick!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure KagutsuchiSwordToolInUseTick!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double renderYawOffset = 0;
		double x1 = 0;
		double z1 = 0;
		double entity_scale = 0;
		double random = 0;
		double y1 = 0;
		if (((!(entity instanceof EntitySusanooWinged.EntityCustom))
				&& (!((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
						.getItem() == new ItemStack(ItemMangekyoSharingan.helmet, (int) (1)).getItem())
						|| (((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY)
								.getItem() == new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1)).getItem()))))) {
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, (int) 1, (int) 4));
		}
		if ((Math.random() < 0.05)) {
			world.playSound((EntityPlayer) null, (entity.posX), ((entity.posY) + ((entity_scale) * 0.9)), (entity.posZ),
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("block.fire.ambient")),
					SoundCategory.NEUTRAL, (float) 0.9, (float) ((Math.random() * 0.7) + 0.3));
		}
		if ((!(entity instanceof EntityPlayer))) {
			entity_scale = (double) (entity.getEntityData().getDouble("entityModelScale"));
		} else {
			entity_scale = (double) 1;
			if (((entity.ticksExisted % 20) == 0)) {
				Chakra.pathway((EntityPlayer) entity).consume(20.0d);
			}
		}
		renderYawOffset = (double) ((EntityLivingBase) entity).renderYawOffset;
		for (int index0 = 0; index0 < (int) ((entity_scale)); index0++) {
			random = (double) Math.random();
			x1 = (double) ((entity.posX) - (Math.sin((((renderYawOffset) + 90) * 0.0174533)) * ((entity_scale) * 0.38)));
			x1 = (double) ((x1) - (Math.sin(((renderYawOffset) * 0.0174533)) * ((random) * ((entity_scale) * 1.6))));
			z1 = (double) ((entity.posZ) + (Math.cos((((renderYawOffset) + 90) * 0.0174533)) * ((entity_scale) * 0.38)));
			z1 = (double) ((z1) + (Math.cos(((renderYawOffset) * 0.0174533)) * ((random) * ((entity_scale) * 1.6))));
			y1 = (double) ((entity.posY) + (((Math.sin((0.0174533 * 30)) * (random)) + 0.9) * (entity_scale)));
			if (((entity_scale) >= 4)) {
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, ((x1) + (Math.random() - 0.5)), (y1), ((z1) + (Math.random() - 0.5)), 0.01, 0.01,
						0.01);
			} else {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (x1), (y1), (z1), 0, 0.01, 0);
			}
		}
	}
}
