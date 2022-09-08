package net.narutomod.procedure;

import net.narutomod.block.BlockMud;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureMudMobplayerCollidesBlock extends ElementsNarutomodMod.ModElement {
	public ProcedureMudMobplayerCollidesBlock(ElementsNarutomodMod instance) {
		super(instance, 379);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure MudMobplayerCollidesBlock!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure MudMobplayerCollidesBlock!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		double eyepos = 0;
		(entity).extinguish();
		if (entity instanceof EntityLivingBase)
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) 10, (int) 5, (false), (false)));
		ProcedureUtils.setVelocity(entity, entity.motionX * -0.1d, -0.01d, entity.motionZ * -0.1d);
		if ((!(world.isRemote))) {
			eyepos = entity.posY + entity.getEyeHeight();
			if (((world.getBlockState(new BlockPos((int) Math.floor((entity.posX)), (int) (eyepos), (int) Math.floor((entity.posZ)))))
					.getBlock() == BlockMud.block.getDefaultState().getBlock())) {
				ProcedureRenderView.changeFog(entity, 1, 10, 10, 0.12f, 0.08f, 0.06f, 2.0f);
				entity.attackEntityFrom(DamageSource.DROWN, (float) 2);
			}
		}
	}
}
