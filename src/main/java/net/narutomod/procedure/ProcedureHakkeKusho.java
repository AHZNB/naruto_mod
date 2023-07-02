package net.narutomod.procedure;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.block.Block;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemByakugan;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureHakkeKusho extends ElementsNarutomodMod.ModElement {
	private static final double XP_REQUIRED = 500d;
	private static final AirPunch KUSHO = new AirPunch();
	
	public ProcedureHakkeKusho(ElementsNarutomodMod instance) {
		super(instance, 265);
	}
	
	public static class AirPunch extends ProcedureAirPunch {
		@Override
		protected double getRange(int duration) {
			return duration / 3.0D + 5.0D;
		}

		@Override
		protected double getFarRadius(int duration) {
			return duration / 20.0D;
		}

		@Override
		protected void attackEntityFrom(EntityLivingBase player, Entity target) {
			super.attackEntityFrom(player, target);
			if (target instanceof EntityLivingBase && player instanceof EntityPlayer) {
				int strength = ProcedureAirPunch.getPressDuration(player);
				double amount = (strength * ((EntityPlayer)player).experienceLevel / 100.0D + 10.0D) / Math.sqrt(target.getDistance(player));
				target.attackEntityFrom(ItemJutsu.causeJutsuDamage(player, null), (float) amount);
			}
		}

		@Override
		protected EntityItem processAffectedBlock(EntityLivingBase player, BlockPos pos, EnumFacing facing) {
			if (player.world.getGameRules().getBoolean("mobGriefing") && player.world.getBlockState(pos).isFullBlock()
			 && player.world.getBlockState(pos.up()).getCollisionBoundingBox(player.world, pos.up()) == Block.NULL_AABB) {
				EntityFallingBlock entity = new EntityFallingBlock(player.world, 0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ(), player.world.getBlockState(pos));
				entity.motionY = 0.45d;
				player.world.spawnEntity(entity);
			}
			return super.processAffectedBlock(player, pos, facing);
		}

		@Override
		protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
			return player.world.getGameRules().getBoolean("mobGriefing")
			 && player instanceof EntityPlayer && PlayerTracker.getBattleXp((EntityPlayer)player) >= XP_REQUIRED + 850d
					? (1.0F - (float) ((Math.sqrt(player.getDistanceSqToCenter(pos)) - 4.0D) / MathHelper.clamp(range, 0.0D, 30.0D)))
					: 0.0F;
		}
	}
	
	public static void executeProcedure(java.util.Map<String, Object> dependencies) {
		Entity entity = (Entity) dependencies.get("entity");
		if (!(entity instanceof EntityPlayer)) {
			System.err.println("Failed to load dependency entity for procedure MCreatorHakkeKusho!");
			return;
		}
		if (dependencies.get("is_pressed") == null) {
			System.err.println("Failed to load dependency is_pressed for procedure MCreatorHakkeKusho!");
			return;
		}
		EntityPlayer player = (EntityPlayer) entity;
		if (!player.isCreative()
		 && (PlayerTracker.getBattleXp(player) < XP_REQUIRED || !ProcedureUtils.isOriginalOwner(player, player.inventory.armorInventory.get(3))))
			return;
		boolean is_pressed = ((Boolean) dependencies.get("is_pressed")).booleanValue();
		int pressDuration = ProcedureAirPunch.getPressDuration(player);
		Chakra.Pathway cp = Chakra.pathway(player);
		if (!is_pressed && pressDuration > 0) {
			ProcedureSync.SwingMainArm.send(player);
			// player.swingArm(EnumHand.MAIN_HAND);
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:HakkeKusho")),
					SoundCategory.PLAYERS, 1.0F, 1.0F);
			cp.consume(ItemByakugan.getKushoChakraUsage(player) * pressDuration);
		}
		if (cp.getAmount() >= ItemByakugan.getKushoChakraUsage(player) * (pressDuration + 1)) {
			KUSHO.execute(is_pressed, player);
		}
	}
}
