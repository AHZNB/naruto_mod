
package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import net.narutomod.block.BlockWaterStill;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWaterShockwave extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 197;
	public static final int ENTITYID_RANGED = 198;

	public EntityWaterShockwave(ElementsNarutomodMod instance) {
		super(instance, 456);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "water_shockwave"), ENTITYID).name("water_shockwave").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private EntityLivingBase user;
		private int radius;
		private boolean buildUpPhase;
		private final List<BlockPos> domeBlocks = Lists.<BlockPos>newArrayList();
		private boolean shouldDie;
		private int deathTicks;
		private static final AttributeModifier SWIM_SPEED_MODIFIER = new AttributeModifier("watershockwave.swimspeed", 1.2d, 0);

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase userIn, float power) {
			this(userIn.world);
			this.user = userIn;
			this.radius = (int)power;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
			this.buildUpPhase = true;
		}

		@Override
		protected void entityInit() {
		}

		public void onDeathUpdate() {
			if (this.user != null) {
				this.user.getEntityAttribute(EntityLivingBase.SWIM_SPEED).removeModifier(SWIM_SPEED_MODIFIER);
			}
			if (this.deathTicks == 0) {
				for (BlockPos pos : this.domeBlocks) {
					this.world.setBlockState(pos, this.rand.nextInt(3) == 0
					 ? Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(1))
					 : Blocks.AIR.getDefaultState(), 3);
				}
			} else if (this.deathTicks % 5 == 0) {
				for (BlockPos pos : this.domeBlocks) {
					this.world.setBlockToAir(pos);
				}
			} else if (this.deathTicks > 30) {
				this.setDead();
				this.domeBlocks.clear();
			}
			++this.deathTicks;
		}

		@Override
		public void onUpdate() {
			if (this.shouldDie) {
				this.onDeathUpdate();
				return;
			}
			if (this.user != null && this.user.isEntityAlive()) {
				if (this.buildUpPhase) {
					this.user.setPositionAndUpdate(this.posX, this.posY, this.posZ);
					List<BlockPos> list = this.getAirBlocksInRadius();
					if (!list.isEmpty()) {
						for (int i = 0; i < this.radius * this.radius && i < list.size(); i++) {
							BlockPos pos = list.get(i);
							this.world.setBlockState(pos, BlockWaterStill.block.getDefaultState(), 3);
							this.domeBlocks.add(pos);
						}
					} else {
						this.buildUpPhase = false;
					}
				} else {
					this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
					List<BlockPos> list = this.getAirBlocksInRadius();
					if (list.size() > this.domeBlocks.size() / 2) {
						this.shouldDie = true;
						return;
					}
					for (int i = 0; i < list.size() && i < 512; i++) {
						BlockPos pos = list.get(i);
						this.world.setBlockState(pos, BlockWaterStill.block.getDefaultState(), 3);
						if (!this.domeContains(pos)) {
							this.domeBlocks.add(pos);
						}
					}
					Iterator<BlockPos> iter = this.domeBlocks.iterator();
					double d = (double)this.radius + 0.5d;
					d *= d;
					for (int i = 0; iter.hasNext() && i < 512; ) {
						BlockPos pos = iter.next();
						if (pos.distanceSqToCenter(this.posX, this.posY, this.posZ) > d) {
							this.world.setBlockToAir(pos);
							iter.remove();
							++i;
						}
					}
				}
				for (Entity entity : 
				 this.world.getEntitiesWithinAABBExcludingEntity(this.user, this.getEntityBoundingBox().grow(this.radius))) {
					if (!(entity instanceof EntitySuitonShark.EC) && BlockWaterStill.isInsideBlock(entity, false)) {
						ProcedureUtils.multiplyVelocity(entity, 0.8d);
					}
				}
				this.user.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 2, 0));
				this.user.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 210, 0));
				if (!this.buildUpPhase) {
					IAttributeInstance attrInstance = this.user.getEntityAttribute(EntityLivingBase.SWIM_SPEED);
					if (!attrInstance.hasModifier(SWIM_SPEED_MODIFIER)) {
						attrInstance.applyModifier(SWIM_SPEED_MODIFIER);
					}
				}
			} else if (!this.world.isRemote) {
				this.shouldDie = true;
			}
		}

		private List<BlockPos> getAirBlocksInRadius() {
			List<BlockPos> list = ProcedureUtils.getAllAirBlocks(this.world, this.getEntityBoundingBox().grow(this.radius));
			list.sort(new ProcedureUtils.BlockposSorter(new BlockPos(this)));
			Iterator<BlockPos> iter = list.iterator();
			double d = (double)this.radius + 0.5d;
			d *= d;
			while (iter.hasNext()) {
				BlockPos pos = iter.next();
				if (pos.distanceSqToCenter(this.posX, this.posY, this.posZ) > d) {
					iter.remove();
				}
			}
			return list;
		}

		private boolean domeContains(BlockPos pos) {
			for (BlockPos pos1 : this.domeBlocks) {
				if (pos1.equals(pos))
					return true;
			}
			return false;
		}

		public void setShouldDie() {
			this.shouldDie = true;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			if (compound.hasUniqueId("userUUID") && this.world instanceof WorldServer) {
				this.user = (EntityLivingBase)((WorldServer)this.world).getEntityFromUuid(compound.getUniqueId("userUUID"));
			}
			this.buildUpPhase = compound.getBoolean("buildUpPhase");
			this.radius = compound.getInteger("radius");
			if (compound.hasKey("domeBlocks", 9)) {
				NBTTagList nbttaglist = compound.getTagList("domeBlocks", 10);
				for (int i = 0; i < nbttaglist.tagCount(); ++i) {
					NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
					this.domeBlocks.add(new BlockPos(nbttagcompound.getInteger("blockPosX"), 
					 nbttagcompound.getInteger("blockPosY"), nbttagcompound.getInteger("blockPosZ")));
				}
			}
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			if (this.user != null) {
				compound.setUniqueId("userUUID", this.user.getUniqueID());
			}
			compound.setBoolean("buildUpPhase", this.buildUpPhase);
			compound.setInteger("radius", this.radius);
			NBTTagList nbttaglist = new NBTTagList();
			for (BlockPos pos : this.domeBlocks) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setInteger("blockPosX", pos.getX());
				nbttagcompound.setInteger("blockPosY", pos.getY());
				nbttagcompound.setInteger("blockPosZ", pos.getZ());
				nbttaglist.appendTag(nbttagcompound);
			}
			if (!nbttaglist.hasNoTags()) {
				compound.setTag("domeBlocks", nbttaglist);
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "WaterShockwaveEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ID_KEY));
				if (entity1 instanceof EC) {
					((EC)entity1).shouldDie = true;
					return false;
				} else {
					entity1 = this.createJutsu(entity, power);
					entity.getEntityData().setInteger(ID_KEY, entity1.getEntityId());
					return true;
				}
			}

			public EC createJutsu(EntityLivingBase entity, float power) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (net.minecraft.util.SoundEvent) 
				  net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:daibakusuishoha")),
				  net.minecraft.util.SoundCategory.PLAYERS, 2, 1f);
				EC entity1 = new EC(entity, power);
				entity.world.spawnEntity(entity1);
				return entity1;
			}
		}
	}
}
