
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.block.state.IBlockState;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class EntityIcePrison extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 226;
	public static final int ENTITYID_RANGED = 227;

	public EntityIcePrison(ElementsNarutomodMod instance) {
		super(instance, 538);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "ice_prison"), ENTITYID).name("ice_prison").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
	}

	@SideOnly(Side.CLIENT)
	public class CustomRender extends Render<EC> {
		public CustomRender(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}
		@Override
		public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
		}
		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return null;
		}
	}

	public static class EC extends Entity {
		private EntityLivingBase user;
		private EntityLivingBase target;
		private BlockPos blockpos;
		private BlockPos tpos[] = new BlockPos[4];
		private int tx, ty, tz, tr;
		private int radius;
		private int tHeight;
		private final BlockPos plist[][] = {
			{ new BlockPos(1, 0, 1), new BlockPos(-1, 0, 1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, -1) },
			{ new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0) },
			{ new BlockPos(0, 0, -1), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, 0, 1) } };

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, EntityLivingBase targetIn) {
			this(userIn.world);
			this.user = userIn;
			this.target = targetIn;
			this.setPosition(targetIn.posX, targetIn.posY, targetIn.posZ);
			this.blockpos = new BlockPos(this);
			this.tpos[0] = this.blockpos;
			this.radius = (int)(targetIn.width * 0.5 + 1);
			this.tHeight = (int)(targetIn.height + 1);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.user != null && ItemJutsu.canTarget(this.target)) {
				this.target.setPositionAndUpdate(this.posX, this.posY + 0.5d, this.posZ);
				Map<BlockPos, IBlockState> map = Maps.newHashMap();
				for (BlockPos pos : this.tpos) {
					if (pos != null && this.world.isAirBlock(pos)) {
						map.put(pos, Blocks.ICE.getDefaultState());
					}
				}
				if (!map.isEmpty()) {
					new net.narutomod.event.EventSetBlocks(this.world, map, 0, 1200, false, false);
				}
				int i = this.ticksExisted % 4;
				if (i == 0) {
					this.target.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 600, 1));
					this.tz++;
					if (this.tz > (int) Math.round(this.tr / 1.41421356D)) {
						this.tz = 0;
						++this.tr;
					}
					if (this.tr > this.radius) {
						this.tr = 0;
						this.ty++;
					}
					if (this.ty > this.tHeight) {
						this.setDead();
						return;
					}
					this.tx = (int) Math.round(Math.sqrt(this.tr * this.tr - this.tz * this.tz));
				}
				this.tpos[0] = this.blockpos.add(this.plist[0][i].getX() * this.tx, this.ty, this.plist[0][i].getZ() * this.tz);
				this.tpos[1] = this.blockpos.add(this.plist[0][i].getX() * this.tz, this.ty, this.plist[0][i].getZ() * this.tx);
				this.tpos[2] = this.tpos[0].add(this.plist[1][i]);
				//this.tpos[3] = this.tpos[0].add(this.plist[2][i]);
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				RayTraceResult result = ProcedureUtils.objectEntityLookingAt(entity, 10d, true);
				if (result != null && result.entityHit instanceof EntityLivingBase) {
					entity.world.playSound(null, result.entityHit.posX, result.entityHit.posY, result.entityHit.posZ, 
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:ice_shoot")),
					 net.minecraft.util.SoundCategory.NEUTRAL, 1f, entity.getRNG().nextFloat() * 0.4f + 0.8f);
					entity.world.spawnEntity(new EC(entity, (EntityLivingBase)result.entityHit));
					return true;
				}
				return false;
			}
		}
	}
}
