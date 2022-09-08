package net.narutomod.procedure;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.WorldServer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.util.Rotation;
import net.minecraft.util.Mirror;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.potion.PotionFlight;
import net.narutomod.entity.EntityChibakuTenseiBall;
//import net.narutomod.block.BlockMeteor;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureMeteorStrike extends ElementsNarutomodMod.ModElement {
	public ProcedureMeteorStrike(ElementsNarutomodMod instance) {
		super(instance, 151);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure MeteorStrike!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure MeteorStrike!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure MeteorStrike!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure MeteorStrike!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure MeteorStrike!");
			return;
		}
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		if (!world.isRemote && entity instanceof EntityLivingBase) {
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(PotionFlight.potion, 300));
			double chakraUsage = ItemRinnegan.getTengaishinseiChakraUsage((EntityLivingBase)entity);
			Entity entity1 = world.findNearestEntityWithinAABB(
			 EntityChibakuTenseiBall.Satellite.class, entity.getEntityBoundingBox().grow(64d, 0d, 64d).expand(0d, 128d, 0d), entity);
			if (entity1 != null && Chakra.pathway((EntityPlayer)entity).consume(chakraUsage * 0.2d)) {
				world.playSound(null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
				 .getObject(new ResourceLocation("narutomod:tengaishinsei")), net.minecraft.util.SoundCategory.NEUTRAL, 5.0F, 1.0F);
				entity1.setNoGravity(false);
				double d0 = (double)x - entity1.posX;
				double d1 = (double)y - entity1.posY;
				double d2 = (double)z - entity1.posZ;
				double a = 0.04d;
				double t = MathHelper.sqrt(Math.abs(d1) * 2d / a);
				entity1.motionX = d0 / t * 1.2d;
				entity1.motionZ = d2 / t * 1.2d;
			} else if (Chakra.pathway((EntityPlayer)entity).consume(chakraUsage)) {
				world.playSound(null, x, y, z, (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
				 .getObject(new ResourceLocation("narutomod:tengaishinsei")), net.minecraft.util.SoundCategory.NEUTRAL, 5.0F, 1.0F);
				/*BlockPos[][] layer = {
					{ new BlockPos(0,0,-1), new BlockPos(-1,0,0), new BlockPos(0,0,0), new BlockPos(1,0,0), new BlockPos(0,0,1) },
					{ new BlockPos(-1,0,-2), new BlockPos(0,0,-2), new BlockPos(1,0,-2), 
					  new BlockPos(-2,0,-1), new BlockPos(-1,0,-1), new BlockPos(1,0,-1), new BlockPos(2,0,-1),
					  new BlockPos(-2,0,0), new BlockPos(2,0,0),
					  new BlockPos(-2,0,1), new BlockPos(-1,0,1), new BlockPos(1,0,1), new BlockPos(2,0,1),
					  new BlockPos(-1,0,2), new BlockPos(0,0,2), new BlockPos(1,0,2) },
					{ new BlockPos(0,0,-3), 
					  new BlockPos(-2,0,-2), new BlockPos(2,0,-2),
					  new BlockPos(-3,0,0), new BlockPos(3,0,0),
					  new BlockPos(-2,0,2), new BlockPos(2,0,2),
					  new BlockPos(0,0,3) },
					{ new BlockPos(-1,0,-3), new BlockPos(1,0,-3),
					  new BlockPos(-3,0,-1), new BlockPos(3,0,-1),
					  new BlockPos(-3,0,1), new BlockPos(3,0,1),
					  new BlockPos(-1,0,3), new BlockPos(1,0,3) }
				};
				entity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 300d);
				for (int y1 = 0; y1 <= 3; y1++) {
					for (int i = 0; i <= y1; i++) {
						for (BlockPos pos : layer[i]) {
							world.setBlockState(pos.add(pos).add(0, y1*2, 0).add(x, y+90, z), BlockMeteor.block.getDefaultState());
						}
					}
				}
				for (int y1 = 2; y1 >= 0; y1--) {
					for (int i = 0; i <= y1; i++) {
						for (BlockPos pos : layer[i]) {
							world.setBlockState(pos.add(pos).add(0, (6-y1)*2, 0).add(x, y+90, z), BlockMeteor.block.getDefaultState());
						}
					}
				}*/
				Template template = ((WorldServer)world).getStructureTemplateManager().getTemplate(world.getMinecraftServer(),
						new ResourceLocation("narutomod", "meteor"));
				if (template != null) {
					BlockPos spawnTo = new BlockPos(x - 10, y + 90, z - 10);
					IBlockState iblockstate = world.getBlockState(spawnTo);
					world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
					template.addBlocksToWorldChunk(world, spawnTo, 
					 new PlacementSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE)
					  .setChunk(null).setReplacedBlock(null).setIgnoreStructureBlock(true).setIgnoreEntities(false));
					entity1 = new EntityChibakuTenseiBall.Satellite((EntityLivingBase)entity, 
					 ProcedureUtils.getNonAirBlocks(world, new AxisAlignedBB(spawnTo).expand(20d, 20d, 20d)));
					world.spawnEntity(entity1);
					((EntityChibakuTenseiBall.Satellite)entity1).setFallTime(5);
				}
			}
		}
	}
}
