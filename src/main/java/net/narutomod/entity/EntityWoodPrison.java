
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;

import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;

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
		private int radius;
		private int tHeight;

		public EC(World worldIn) {
			super(worldIn);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = false;
		}

		public EC(World worldIn, Vec3d targetVecIn, float power) {
			this(worldIn);
			this.setPosition(targetVecIn.x, targetVecIn.y, targetVecIn.z);
			this.blockpos = new BlockPos(targetVecIn);
			this.radius = MathHelper.ceil(power * 0.5f);
			this.tHeight = MathHelper.ceil(power - 0.5f);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.blockpos != null) {
				for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class,
				 new AxisAlignedBB(this.posX - this.radius, this.posY, this.posZ - this.radius, 
				 this.posX + this.radius, this.posY + this.tHeight, this.posZ + this.radius))) {
				 	entity.addPotionEffect(new PotionEffect(PotionHeaviness.potion, 100, 3, false, false));
					entity.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 1200, 2, false, false));
				}
				Map<BlockPos, IBlockState> map = Maps.newHashMap();
				for (BlockPos pos : Iterables.concat(BlockPos.getAllInBoxMutable(this.blockpos.add(-this.radius, -3, -this.radius), this.blockpos.add(-this.radius, this.tHeight, this.radius)),
				 BlockPos.getAllInBoxMutable(this.blockpos.add(-this.radius, -3, -this.radius), this.blockpos.add(this.radius, this.tHeight, -this.radius)),
				 BlockPos.getAllInBoxMutable(this.blockpos.add(this.radius, -3, -this.radius), this.blockpos.add(this.radius, this.tHeight, this.radius)),
				 BlockPos.getAllInBoxMutable(this.blockpos.add(-this.radius, -3, this.radius), this.blockpos.add(this.radius, this.tHeight, this.radius)),
 				 BlockPos.getAllInBoxMutable(this.blockpos.add(-this.radius, this.tHeight, -this.radius), this.blockpos.add(this.radius, this.tHeight, this.radius)) )) {
					if (this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos) == null) {
						map.put(pos.toImmutable(), pos.getY() - this.blockpos.getY() == this.tHeight 
						 ? Blocks.WOODEN_SLAB.getDefaultState() : Blocks.OAK_FENCE.getDefaultState());
					}
				}
				if (!map.isEmpty()) {
					new net.narutomod.event.EventSetBlocks(this.world, map, 0, 1200, true, false) {
						@Override
						public void onAddTick(int tick) {
							if (tick % 30 == 1) {
								EC.this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodspawn")),
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

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				RayTraceResult result = ProcedureUtils.raytraceBlocks(entity, Math.max(2d * power, 20d));
				if (result != null && result.typeOfHit != RayTraceResult.Type.MISS) {
					entity.world.spawnEntity(new EC(entity.world, result.hitVec, power));
					return true;
				}
				return false;
			}
		}
	}
}
