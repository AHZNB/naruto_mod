
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
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWoodPrison extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 228;
	public static final int ENTITYID_RANGED = 229;

	public EntityWoodPrison(ElementsNarutomodMod instance) {
		super(instance, 542);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "wood_prison"), ENTITYID).name("wood_prison").tracker(64, 3, true).build());
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
		private BlockPos blockpos;
		private Map<EntityLivingBase, Vec3d> entityMap = Maps.newHashMap();
		private BlockPos tpos[] = new BlockPos[4];
		private int tx, ty, tz, tr;
		private int radius;
		private int tHeight;
		private final BlockPos plist[][] = {
			{ new BlockPos(1, 0, 1), new BlockPos(-1, 0, 1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, -1) },
			{ new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0) },
			{ new BlockPos(0, 0, -1), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, 0, 1) } };

		public EC(World worldIn) {
			super(worldIn);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = false;
		}

		public EC(World worldIn, Vec3d targetVecIn, float power) {
			this(worldIn);
			this.setPosition(targetVecIn.x, targetVecIn.y, targetVecIn.z);
			this.blockpos = new BlockPos(targetVecIn);
			this.tpos[0] = this.blockpos;
			//this.radius = (int)(targetIn.width * 0.5 + 1);
			//this.tHeight = (int)(targetIn.height + 1);
			this.radius = MathHelper.ceil(power * 0.5f);
			this.tHeight = MathHelper.ceil(power - 0.5f);
			for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class,
			 new AxisAlignedBB(this.posX - this.radius, this.posY, this.posZ - this.radius, 
			 this.posX + this.radius, this.posY + this.tHeight, this.posZ + this.radius))) {
				this.entityMap.put(entity, entity.getPositionVector());
			}
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.blockpos != null) {
				for (Map.Entry<EntityLivingBase, Vec3d> entry : this.entityMap.entrySet()) {
					entry.getKey().setPositionAndUpdate(entry.getValue().x, entry.getValue().y, entry.getValue().z);
					entry.getKey().addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 1200, 2));
				}
				Map<BlockPos, IBlockState> map = Maps.newHashMap();
				for (BlockPos pos : BlockPos.getAllInBoxMutable(this.blockpos.add(-this.radius, 0, -this.radius), 
				 this.blockpos.add(this.radius, this.tHeight, this.radius))) {
					if (this.world.isAirBlock(pos)) {
						map.put(pos.toImmutable(), pos.getY() - this.blockpos.getY() == this.tHeight 
						 ? Blocks.WOODEN_SLAB.getDefaultState() : Blocks.OAK_FENCE.getDefaultState());
					}
				}
				/*for (int i = 0; i < 4; i++) {
					if (i == 0) {
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
					for (BlockPos pos : this.tpos) {
						if (pos != null && this.world.isAirBlock(pos)) {
							map.put(pos, this.ty == this.tHeight ? Blocks.WOODEN_SLAB.getDefaultState() : Blocks.OAK_FENCE.getDefaultState());
						}
					}
				}*/
				if (!map.isEmpty()) {
					new net.narutomod.event.EventSetBlocks(this.world, map, 0, 1200, true, false) {
						@Override
						public void onAddTick(int tick) {
							if (tick % 20 == 1) {
								EC.this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:woodspawn"))),
								 1f, EC.this.rand.nextFloat() * 0.4f + 0.8f);
							}
						}
					};
				}
				this.setDead();
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		private void setBlocks(Map<BlockPos, IBlockState> map) {
			if (!map.isEmpty()) {
				new net.narutomod.event.EventSetBlocks(this.world, map, 0, 1200, true, true);
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
				RayTraceResult result = ProcedureUtils.raytraceBlocks(entity, 20d);
				if (result != null) {
					entity.world.spawnEntity(new EC(entity.world, result.hitVec, power));
					return true;
				}
				return false;
			}
		}
	}
}
