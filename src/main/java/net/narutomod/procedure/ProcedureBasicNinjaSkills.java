package net.narutomod.procedure;

import net.narutomod.block.BlockMud;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.block.material.Material;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureBasicNinjaSkills extends ElementsNarutomodMod.ModElement {
	public ProcedureBasicNinjaSkills(ElementsNarutomodMod instance) {
		super(instance, 220);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure BasicNinjaSkills!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure BasicNinjaSkills!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		boolean f1 = false;
		if (((entity instanceof EntityPlayer) && ((EntityPlayer) entity).isSpectator())) {
			return;
		}
		if ((!(world.isRemote))) {
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, (int) 2, (int) 1, (false), (false)));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, (int) 2, (int) 1, (false), (false)));
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, (int) 2, (int) 1, (false), (false)));
		}
		if (((((world.getBlockState(new BlockPos((int) Math.floor((entity.posX)), (int) (entity.posY), (int) Math.floor((entity.posZ)))))
				.getMaterial() == Material.WATER)
				&& (!((world.getBlockState(new BlockPos((int) Math.floor((entity.posX)), (int) ((entity.posY) + 1), (int) Math.floor((entity.posZ)))))
						.getMaterial() == Material.WATER)))
				&& (!(entity.isSneaking())))) {
			entity.motionY = 0.01D;
			entity.onGround = true;
			entity.fallDistance = (float) (0);
		}
		RayTraceResult r = ProcedureUtils.raytraceBlocks(entity, 1d);
		f1 = (!entity.onGround && entity.rotationPitch < 0 && r != null && r.typeOfHit == RayTraceResult.Type.BLOCK
				&& world.getBlockState(r.getBlockPos()).isFullCube());
		if ((f1)) {
			f1 = entity.isInWater() || entity.isInLava();
			if ((f1)) {
				entity.motionY = 0.3;
			} else {
				if ((!((world.getBlockState(new BlockPos((int) Math.floor((entity.posX)), (int) (entity.posY), (int) Math.floor((entity.posZ)))))
						.getBlock() == BlockMud.block.getDefaultState().getBlock()))) {
					entity.motionY = 0.6d - (world.getBlockState(r.getBlockPos()).getBlock().slipperiness - 0.6) * 2;
				}
			}
		}
		if (entity.fallDistance > 4f)
			entity.fallDistance -= 0.75f;
	}
}
