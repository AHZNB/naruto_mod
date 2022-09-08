package net.narutomod.event;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;

import net.narutomod.Particles;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;

public class EventVanillaExplosion extends SpecialEvent {
    private boolean causesFire;
    private boolean damagesTerrain;
    private float size;
    private List<BlockPos> affectedBlockPositions;
    private int blockIndex;
    private int fireIndex;

	public EventVanillaExplosion() { }

	public EventVanillaExplosion(World worldIn, Entity entityIn, int x, int y, int z, float strengthIn, long startTime, boolean flames) {
		super(EnumEventType.VANILLA_EXPLOSION, worldIn, entityIn, x, y, z, startTime, true, true);
		if (!worldIn.isRemote) {
			this.size = strengthIn;
			this.causesFire = flames;
			this.damagesTerrain = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, entityIn);
			this.affectedBlockPositions = Lists.<BlockPos>newArrayList();
		}
	}

	@Override
	protected void onUpdate() {
		if (!this.shouldExecute())
			return;

		super.onUpdate();

		Entity target = this.getEntity();
		if (target != null && this.blockIndex == 0) {
			this.x0 = MathHelper.floor(target.posX);
			this.y0 = MathHelper.floor(target.posY);
			this.z0 = MathHelper.floor(target.posZ);
		}
		/*if (this.blockIndex == 0) {
			this.doExplosionA();
		}
		this.doExplosionB();*/
		this.world.newExplosion(null, this.x0, this.y0, this.z0, this.size, this.causesFire, this.damagesTerrain);
		this.clear();
	}

	protected void damageEntity(Entity entityIn, float amount) {
		entityIn.attackEntityFrom(DamageSource.causeExplosionDamage((EntityLivingBase)null), amount);
	}

    private void doExplosionA() {
        Set<BlockPos> set = Sets.<BlockPos>newHashSet();
        int i = 16;
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.size * (0.7F + this.world.rand.nextFloat() * 0.6F);
                        double d4 = this.x0;
                        double d6 = this.y0;
                        double d8 = this.z0;
                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            IBlockState iblockstate = this.world.getBlockState(blockpos);
                            if (iblockstate.getMaterial() != Material.AIR) {
                                float f2 = iblockstate.getBlock().getExplosionResistance(world, blockpos, null, null);
                                f -= (f2 + 0.3F) * 0.3F;
                            }
                            if (f > 0.0F) {
                                set.add(blockpos);
                            }
                            d4 += d0 * 0.30000001192092896D;
                            d6 += d1 * 0.30000001192092896D;
                            d8 += d2 * 0.30000001192092896D;
                        }
                    }
                }
            }
        }
        this.affectedBlockPositions.addAll(set);
        float f3 = this.size * 2.0F;
        int k1 = MathHelper.floor(this.x0 - (double)f3 - 1.0D);
        int l1 = MathHelper.floor(this.x0 + (double)f3 + 1.0D);
        int i2 = MathHelper.floor(this.y0 - (double)f3 - 1.0D);
        int i1 = MathHelper.floor(this.y0 + (double)f3 + 1.0D);
        int j2 = MathHelper.floor(this.z0 - (double)f3 - 1.0D);
        int j1 = MathHelper.floor(this.z0 + (double)f3 + 1.0D);
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
        //net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.world, this, list, f3);
        Vec3d vec3d = new Vec3d(this.x0, this.y0, this.z0);
        for (int k2 = 0; k2 < list.size(); ++k2) {
            Entity entity = list.get(k2);
            if (!entity.isImmuneToExplosions()) {
                double d12 = entity.getDistance(this.x0, this.y0, this.z0) / (double)f3;
                if (d12 <= 1.0D) {
                    double d5 = entity.posX - this.x0;
                    double d7 = entity.posY + (double)entity.getEyeHeight() - this.y0;
                    double d9 = entity.posZ - this.z0;
                    double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                    if (d13 != 0.0D) {
                        d5 = d5 / d13;
                        d7 = d7 / d13;
                        d9 = d9 / d13;
                        double d14 = (double)this.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
                        double d10 = (1.0D - d12) * d14;
                        this.damageEntity(entity, (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D)));
                        double d11 = d10;
                        if (entity instanceof EntityLivingBase) {
                            d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase)entity, d10);
                        }
                        entity.motionX += d5 * d11;
                        entity.motionY += d7 * d11;
                        entity.motionZ += d9 * d11;
                        entity.velocityChanged = true;
                    }
                }
            }
        }
    }

    public void doExplosionB() {
    	if (this.sound) {
        	this.world.playSound(null, this.x0, this.y0, this.z0, SoundEvents.ENTITY_GENERIC_EXPLODE,
        	 SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
    	}
    	if (this.particles) {
	        if (this.size >= 2.0F && this.damagesTerrain) {
	            Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.x0, this.y0, this.z0,
	             1, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0xF0FFFFFF, 30);
	        } else {
	        	Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.x0, this.y0, this.z0,
	        	 1, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0xF0FFFFFF, 15);
	        }
    	}
    	boolean done = false;
        if (this.damagesTerrain) {
        	int i = this.blockIndex;
        	for (int j = 0; i < this.affectedBlockPositions.size() && j < 512; i++) {
        		BlockPos blockpos = this.affectedBlockPositions.get(i);
                IBlockState iblockstate = this.world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if (this.particles) {
                    double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
                    double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
                    double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
                    double d3 = d0 - this.x0;
                    double d4 = d1 - this.y0;
                    double d5 = d2 - this.z0;
                    double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 = d3 / d6;
                    d4 = d4 / d6;
                    d5 = d5 / d6;
                    double d7 = 0.5D / (d6 / (double)this.size + 0.1D);
                    d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
                    d3 = d3 * d7;
                    d4 = d4 * d7;
                    d5 = d5 * d7;
                    Particles.spawnParticle(this.world, Particles.Types.SMOKE, (d0 + this.x0) / 2.0D, (d1 + this.y0) / 2.0D,
                     (d2 + this.z0) / 2.0D, 1, 0.0D, 0.0D, 0.0D, d3, d4, d5, 0xF0FFFFFF);
                    Particles.spawnParticle(this.world, Particles.Types.SMOKE, d0, d1, d2, 1, 0.0D, 0.0D, 0.0D, d3, d4, d5, 0xF0000000);
                }
                if (iblockstate.getMaterial() != Material.AIR) {
                    if (block.canDropFromExplosion(null)) {
                        block.dropBlockAsItemWithChance(this.world, blockpos, this.world.getBlockState(blockpos), 1.0F / this.size, 0);
                    }
                    this.world.setBlockToAir(blockpos);
                    ++j;
                }
            }
            this.blockIndex = i;
            if (i >= this.affectedBlockPositions.size()) {
            	done = true;
            }
        }
        if (done) {
        	if (this.causesFire) {
	        	int i = this.fireIndex;
	        	for (int j = 0; i < this.affectedBlockPositions.size() && j < 128; i++) {
	        		BlockPos pos = this.affectedBlockPositions.get(i);
	                if (this.world.getBlockState(pos).getMaterial() == Material.AIR && this.world.getBlockState(pos.down()).isFullBlock() && this.rand.nextInt(3) == 0) {
	                    this.world.setBlockState(pos, Blocks.FIRE.getDefaultState());
	                    ++j;
	                }
	            }
	            this.fireIndex = i;
	            if (i >= this.affectedBlockPositions.size()) {
	            	this.clear();
	            }
	        } else {
            	this.clear();
	        }
        }
    }

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setFloat("strength", this.size);
		compound.setBoolean("flaming", this.causesFire);
		compound.setBoolean("damagesTerrain", this.damagesTerrain);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.size = compound.getFloat("strength");
		this.causesFire = compound.getBoolean("flaming");
		this.damagesTerrain = compound.getBoolean("damagesTerrain");
	}
}
