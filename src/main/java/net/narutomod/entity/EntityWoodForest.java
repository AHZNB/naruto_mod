
package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemMokuton;
import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWoodForest extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 514;
	public static final int ENTITYID_RANGED = 515;

	public EntityWoodForest(ElementsNarutomodMod instance) {
		super(instance, 929);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "wood_forest"), ENTITYID).name("wood_forest").tracker(64, 3, true).build());
	}

	public static class EC extends ItemMokuton.WoodSegment implements ItemJutsu.IJutsu {
		private int lifespan = 600;
		private EC lastSegment;
		private float diameter;
		private EntityLivingBase user;
		private EntityLivingBase target;
		private Vec3d targetVec;
		private int reachedCount;
		private boolean inGround;
		private List<EntityLivingBase> potentialTargets = Lists.newArrayList();
		private List<EntityLivingBase> targetList = Lists.newArrayList();
		private List<EntityLivingBase> capturedTargets = Lists.newArrayList();
		private EC rootSegment;
		private float health;

		public EC(World world) {
			super(world);
		}

		public EC(EntityLivingBase userIn, BlockPos pos, float diameterIn) {
			this(userIn.world);
			this.setParent(this);
			this.setLocationAndAngles(0.5d + pos.getX(), 0.5d + pos.getY(), 0.5d + pos.getZ(), 0f, 0f);
			this.setPositionAndRotationFromParent(1f);
			this.lastSegment = this;
			this.diameter = diameterIn;
			this.user = userIn;
			this.health = diameterIn * 0.5f;
		}

		public EC(EC segment, float yawOffset, float pitchOffset) {
			super(segment, yawOffset, pitchOffset);
			this.user = segment.user;
			this.target = segment.target;
			this.targetVec = segment.targetVec;
			this.inGround = this.isOnGround(this.getPositionVector());
		}

		public EC(EC segment, double offsetX, double offsetY, double offsetZ, float yawOffset, float pitchOffset) {
			super(segment, offsetX, offsetY, offsetZ, yawOffset, pitchOffset);
			this.user = segment.user;
			this.target = segment.target;
			this.targetVec = segment.targetVec;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.MOKUTON;
		}

		public void setLifespan(int ticks) {
			this.lifespan = ticks;
		}

		@Override @Nullable
		protected EC getParent() {
			Entity entity = super.getParent();
			return entity instanceof EC ? (EC)entity : null;
		}	

		protected boolean isOnGround(Vec3d vec) {
			BlockPos pos = new BlockPos(vec);
			if (!this.world.isAirBlock(pos)) {
				AxisAlignedBB aabb = this.world.getBlockState(pos).getCollisionBoundingBox(this.world, pos);
				if (aabb != net.minecraft.block.Block.NULL_AABB && aabb.offset(pos).contains(vec)) {
					return true;
				}
			}
			return false;		
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted == 1 && this.rand.nextFloat() < 0.05f) {
				this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodgrow")),
				 1.0f, this.rand.nextFloat() * 0.4f + 0.6f);
			}
			EC parent = this.getParent();
			if (parent != null && this.ticksExisted < this.lifespan) {
				if (!this.world.isRemote) {
					int index = this.getIndex();
					if (index == 0) {
						if (this.ticksExisted == 1) {
							for (int i = 0; i < (int)(this.diameter * 0.6); i++) {
								Vec3d vec = this.getPositionVector().add(new Vec3d(0, 1, this.rand.nextFloat() * this.diameter * 0.5f).rotateYaw(this.rand.nextFloat() * (float)Math.PI * 2));
								vec = new Vec3d(vec.x, (double)ProcedureUtils.getTopSolidBlockY(this.world, new BlockPos(vec)) - 0.5d, vec.z).subtract(this.getPositionVector());
								EC segment = new EC(this, vec.x, vec.y, vec.z, (this.rand.nextFloat()-0.5f) * 360f, this.rand.nextFloat() * 30f);
								segment.setLifespan(this.lifespan - this.ticksExisted * 2);
								segment.lastSegment = segment;
								segment.setSize(Math.max(this.diameter * 0.03f + 0.3f, 0.6f), Math.max(this.diameter * 0.03f + 0.3f, 0.6f));
								this.world.spawnEntity(segment);
							}
						}
						this.potentialTargets = this.world.getEntitiesWithinAABB(EntityLivingBase.class,
						 this.getEntityBoundingBox().grow(this.diameter, 0, this.diameter).expand(0, this.diameter, 0).expand(0, -5, 0), (p) -> {
							return ItemJutsu.canTarget(p) && (!(p instanceof EntityPlayer) || !((EntityPlayer)p).isCreative())
							 && (parent.user == null || (parent.user != p && !parent.user.isOnSameTeam(p)));
						});
					}
					if (index > 0 && index < 15 && this.lastSegment != null && this.ticksExisted > 1) {
						boolean hasTarget = this.lastSegment.hasLivingTarget();
						if (this.ticksExisted <= 18 || (hasTarget && this.ticksExisted < 40)) {
							if (!hasTarget && parent.rand.nextInt(8) == 7 && this.width > 0.32f) {
								EC segment = new EC(this.lastSegment, (this.rand.nextFloat()-0.5f) * 360f, (this.rand.nextFloat()-0.5f) * 120f);
								segment.setSize(this.width * 0.6f, this.height * 0.6f);
								segment.setLifespan(this.lifespan - this.ticksExisted * 2);
								segment.lastSegment = segment;
								segment.rootSegment = segment;
								this.world.spawnEntity(segment);
							}
							ProcedureUtils.Vec2f vec2f = new ProcedureUtils.Vec2f((this.rand.nextFloat()-0.5f) * 30f, (this.rand.nextFloat()-0.5f) * 30f);
							if (hasTarget) {
								int captureCount = MathHelper.ceil(this.lastSegment.target.width * 4f / this.height);
								Vec3d vec = this.lastSegment.target.getPositionVector().addVector(0, this.lastSegment.target.height/2, 0);
								double d = this.lastSegment.getDistance(vec.x, vec.y, vec.z);
								float f2 = this.height + 0.5f;
								float f1 = f2 / (this.lastSegment.target.width + MathHelper.clamp((float)d, f2, 4.4f));
								vec2f = ProcedureUtils.getYawPitchFromVec(vec.subtract(this.lastSegment.getPositionVector()))
								 .add(-this.lastSegment.rotationYaw, 90.0f - this.lastSegment.rotationPitch).scale(f1);
								if ((float)d < this.lastSegment.target.width + this.height) {
									++this.reachedCount;
								}
								if (this.lastSegment.targetVec == null && this.reachedCount > 0) {
									if (this.reachedCount >= captureCount) {
										this.lastSegment.targetVec = this.lastSegment.target.getPositionVector();
										parent.capturedTargets.add(this.lastSegment.target);
									} else if (this.lastSegment.target instanceof EntityLivingBase) {
										((EntityLivingBase)this.lastSegment.target).addPotionEffect(new PotionEffect(PotionHeaviness.potion, 10, (int)((float)this.reachedCount * 6 / captureCount), false, false));
									}
								}
							} else if (index != 1 || this.ticksExisted > 3) {
								parent.potentialTargets.sort(new ProcedureUtils.EntitySorter(this.lastSegment));
								for (int i = 0; i < parent.potentialTargets.size(); i++) {
									EntityLivingBase entity = parent.potentialTargets.get(i);
									if (!parent.targetList.contains(entity)) {
										parent.targetList.add(entity);
										this.lastSegment.target = entity;
										break;
									}
								}
							}
							this.lastSegment = new EC(this.lastSegment, vec2f.x, vec2f.y);
							this.lastSegment.setSize(this.width, this.height);
							this.lastSegment.setLifespan(this.lifespan - this.ticksExisted * 2);
							this.lastSegment.rootSegment = this;
							this.world.spawnEntity(this.lastSegment);
						}
					}
					if (index > 16 && this.ticksExisted < 5 && !this.inGround && parent.diameter > 25f && !this.hasLivingTarget()) {
						BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
						for (pos.setPos(this); !this.world.isAirBlock(pos); pos.move(EnumFacing.random(this.rand), this.rand.nextInt(Math.max((int)this.width+1, 2))));
						new net.narutomod.event.EventSetBlocks(this.world,
						 ImmutableMap.of(pos.toImmutable(), Blocks.LEAVES.getStateFromMeta(0)), 0, this.lifespan - this.ticksExisted, false, false);
						pos.release();
					}
					if (this.ticksExisted < 3) {
						for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox())) {
							if (entity != this.user) {
							 	entity.getEntityData().setBoolean("TempData_disableKnockback", true);
								entity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.user), 2.0f * parent.diameter);
							}
						}
					}
					if (this.targetVec != null && this.targetTargetable() && this.ticksExisted < this.lifespan - 40) {
						this.target.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.user).setDamageBypassesArmor(), 6.0f);
						this.target.setPositionAndUpdate(this.targetVec.x, this.targetVec.y, this.targetVec.z);
					}
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		private boolean hasLivingTarget() {
			return this.target != null && this.target.isEntityAlive();
		}

		private boolean targetTargetable() {
			if (!ItemJutsu.canTarget(this.target)) {
				this.target = null;
				return false;
			}
			return true;
		}

		public List<EntityLivingBase> getCapturedTargets() {
			return this.capturedTargets;
		}

		/*@Override
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.world.isRemote) {
				this.health -= amount;
				if (this.health <= 0.0f) {
					
				}
			}
			return false;
		}*/

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 10.0f) {
					RayTraceResult res = ProcedureUtils.raytraceBlocks(entity, power * 0.5 + 20);
					if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK) {
						entity.world.spawnEntity(new EC(entity, res.getBlockPos(), power));
						ItemJutsu.setCurrentJutsuCooldown(stack, entity, 200 + (int)(power * 12));
						return true;
					}
				}
				return false;
			}

			@Override
			public float getBasePower() {
				return 9.0f;
			}
	
			@Override
			public float getPowerupDelay() {
				return 25.0f;
			}
			
			@Override
			public float getMaxPower(ItemStack stack, EntityLivingBase entity) {
				float modifier = 1.0f / ((ItemJutsu.Base)stack.getItem()).getModifier(stack, entity);
				return Math.min(modifier * modifier * 19.49f, 100.0f);
			}
		}
	}
}
