
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.potion.PotionInstantDamage;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;

@ElementsNarutomodMod.ModElement.Tag
public class EntityC4 extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 451;
	public static final int ENTITYID_RANGED = 452;

	public EntityC4(ElementsNarutomodMod instance) {
		super(instance, 883);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class).id(new ResourceLocation("narutomod", "c_4"), ENTITYID)
				.name("c_4").tracker(88, 3, true).build());
	}

	public static class EC extends EntityClone.Base implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> TICKS_ALIVE = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> START_GROW_TIME = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Boolean> IGNITED = EntityDataManager.<Boolean>createKey(EC.class, DataSerializers.BOOLEAN);
		private final float finalSize = 6.0f;
		private final int growTicks = 60;
		private final int fuse = 50;
		private final float blastRadius = 56.0f;
		private int damageTicks = 100;
		private int damagePerTick = 2;
		private int ignitionTime;
		private float nextStepDistance = this.finalSize;
		private int deathTicks;

		public EC(World world) {
			super(world);
			this.stepHeight = this.finalSize * 1.8f / 3.0f;
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}

		public EC(EntityLivingBase user) {
			super(user);
			this.setScale(0.1f);
			this.stepHeight = this.finalSize * 1.8f / 3.0f;
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.BAKUTON;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(TICKS_ALIVE, Integer.valueOf(0));
			this.dataManager.register(START_GROW_TIME, Integer.valueOf(Integer.MAX_VALUE / 10));
			this.dataManager.register(IGNITED, Boolean.valueOf(false));
		}

		public int getTicksAlive() {
	    	return ((Integer)this.dataManager.get(TICKS_ALIVE)).intValue();
		}

		protected void setTicksAlive(int ticks) {
			this.dataManager.set(TICKS_ALIVE, Integer.valueOf(ticks));
		}

		public int getStartGrowTime() {
	    	return ((Integer)this.dataManager.get(START_GROW_TIME)).intValue();
		}

		protected void setStartGrowTime(int ticks) {
			this.dataManager.set(START_GROW_TIME, Integer.valueOf(ticks));
		}

		private void ignite() {
			this.dataManager.set(IGNITED, Boolean.valueOf(true));
		}

		private boolean ignited() {
			return ((Boolean)this.dataManager.get(IGNITED)).booleanValue();
		}

		public void setExplosionDamage(int ticks, int damage) {
			this.damageTicks = ticks;
			this.damagePerTick = damage;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0d);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0d);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5d);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0d);
		}

		@Override
		protected void initEntityAI() {
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0d, true) {
				@Override
				public boolean shouldExecute() {
					return EC.this.getTicksAlive() > EC.this.getStartGrowTime() + EC.this.growTicks && super.shouldExecute();
				}
				@Override
				protected double getAttackReachSqr(EntityLivingBase attackTarget) {
					return 1024.0d;
				}
			});
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return this.ignited() || source == DamageSource.FALL ? false : super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			this.ignite();
			return true;
		}

		private void explode() {
	    	if (!this.world.isRemote) {
		    	this.dead = true;
		    	EntityLivingBase summoner = this.getSummoner();
				if (summoner != null) {
					this.world.playSound(null, summoner.posX, summoner.posY, summoner.posZ, 
					 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:katsu")), SoundCategory.NEUTRAL, 1f, 1f);
				}
		    	ProcedureAoeCommand.set(this, 0d, this.blastRadius).exclude(summoner).effect(PotionInstantDamage.potion, this.damageTicks / 20, this.damagePerTick - 1, false);
				Particles.spawnParticle(this.world, Particles.Types.EXPANDING_SPHERE, this.posX, this.posY, this.posZ, 1,
				 0d, 0d, 0d, 0d, 0d, 0d, (int)(this.blastRadius * 10f), 10, 0x3080ffff);
	    		this.setDead();
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (this.world.isRemote) {
				Particles.spawnParticle(this.world, Particles.Types.FALLING_DUST, this.posX, this.posY + 0.5d * this.height, this.posZ,
				 300, 0.5d * this.width, 0.3d * this.height, 0.5d * this.width, 0d, 0d, 0d, 0xFEE0E0E0, 0, 40 + this.rand.nextInt(60));
			}
		}

		@Override
		protected void onDeathUpdate() {
			++this.deathTicks;
			if (this.deathTicks == 1) {
				this.ignite();
			}
		}

		@Override
		public void onUpdate() {
			int ticksAlive = this.getTicksAlive();
			this.clearActivePotions();
			super.onUpdate();
			if (this.ignited()) {
				if (ticksAlive - this.ignitionTime > this.fuse) {
					this.explode();
				} else if (this.world.isRemote) {
					Vec3d vec = new Vec3d((0.5f + this.rand.nextFloat() * 0.5f) * (this.rand.nextBoolean() ? -1f : 1f), (this.rand.nextFloat()-0.5f) * 0.5f, (0.5f + this.rand.nextFloat() * 0.5f) * (this.rand.nextBoolean() ? -1f : 1f));
					Vec3d vec2 = this.getPositionVector().addVector(0d, this.rand.nextFloat() * this.height, 0d);
					for (int i = 0; i < 100; i++) {
						Vec3d vec1 = vec.scale(this.rand.nextFloat());
						Particles.spawnParticle(this.world, Particles.Types.SMOKE, vec2.x, vec2.y, vec2.z, 1, 0d, 0d, 0d, vec1.x, vec1.y, vec1.z, 0x8FFFFFFF, 30 + this.rand.nextInt(30), (int)(4.0f / (this.rand.nextFloat() * 0.8f + 0.2f)));
					}
				} else if (this.rand.nextFloat() < 0.4f) {
					this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0F, this.rand.nextFloat() * 0.3f + 0.7f);
				}
			} else {
				EntityLivingBase summoner = this.getSummoner();
				if (!this.world.isRemote) {
					int startGrowTime = this.getStartGrowTime();
					if (ticksAlive < startGrowTime && this.onGround) {
						startGrowTime = ticksAlive;
						this.setStartGrowTime(ticksAlive);
					}
					if (ticksAlive > startGrowTime && ticksAlive - startGrowTime <= this.growTicks) {
						this.setScale(0.1f + (this.finalSize - 0.1f) * (float)(ticksAlive - startGrowTime) / (float)this.growTicks);
					}
					if (summoner != null && ticksAlive <= this.growTicks && ticksAlive % 40 == 1) {
						this.world.playSound(null, summoner.posX, summoner.posY, summoner.posZ, SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rocks")), SoundCategory.NEUTRAL, 0.6F, this.rand.nextFloat() * 0.3f + 0.8f);
					}
				} else {
					if (summoner != null && ticksAlive <= this.growTicks) {
						Vec3d vec = summoner.getLookVec();
						Vec3d vec1 = summoner.getPositionEyes(1f).add(vec);
						for (int i = 0; i < 10; i++) {
							Particles.spawnParticle(this.world, Particles.Types.CLAY_SPIT, vec1.x, vec1.y, vec1.z, 1, 0d, 0d, 0d, vec.x * 0.5d, vec.y * 0.5d, vec.z * 0.5d, 0xFEE0E0E0, 100 + this.rand.nextInt(50), this.getEntityId());
						}
					}
				}
				if (ticksAlive == 0 && summoner != null) {
					Vec3d vec = summoner.getLookVec().scale(0.5d);
					this.motionX = vec.x;
					this.motionY = vec.y;
					this.motionZ = vec.z;
				}
				this.ignitionTime = ticksAlive;
			}
			this.setTicksAlive(ticksAlive + 1);
		}

		public float getIgnitionProgress(float partialTick) {
			return ((float)this.getTicksAlive() + partialTick - this.ignitionTime) / (float)(this.fuse - 2);
		}

		@Override
		public void move(MoverType type, double x, double y, double z) {
			super.move(type, x, y, z);
			if (this.distanceWalkedOnStepModified > this.nextStepDistance) {
				this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 2.0F, this.rand.nextFloat() * 0.4f + 0.7f);
				this.nextStepDistance = this.distanceWalkedOnStepModified + this.finalSize + 1.0f;
			}
		}

		@Override
		protected void playStepSound(BlockPos pos, net.minecraft.block.Block blockIn) {
		}

		@Override
		public boolean canBePushed() {
			return false;
		}

		@Override
		protected void collideWithEntity(Entity entityIn) {
			this.applyEntityCollision(entityIn);
		}

		@Override
		public void applyEntityCollision(Entity entity) {
			if (entity.height >= this.height * 0.9f && entity.width >= this.width * 0.9f) {
				super.applyEntityCollision(entity);
			} else if (!this.isRidingSameEntity(entity) && !entity.noClip && !entity.isBeingRidden()) {
				double d2 = entity.posX - this.posX;
				double d3 = entity.posZ - this.posZ;
				double d4 = MathHelper.absMax(d2, d3);
				if (d4 >= 0.01D) {
					d4 = (double)MathHelper.sqrt(d4);
					d2 /= d4;
					d3 /= d4;
					double d5 = d4 >= 1.0D ? 1.0D / d4 : 1.0D;
					d2 *= d5 * 0.05d;
					d3 *= d5 * 0.05d;
					entity.motionX = d2;
					entity.motionZ = d3;
					entity.isAirBorne = true;
				}
			}
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setTicksAlive(compound.getInteger("ticksAlive"));
			this.setStartGrowTime(compound.getInteger("startGrowTime"));
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setInteger("ticksAlive", this.getTicksAlive());
			compound.setInteger("startGrowTime", this.getStartGrowTime());
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends EntityClone.ClientRLM.RenderClone<EC> {
			public CustomRender(RenderManager renderManagerIn) {
				EntityClone.ClientRLM.getInstance().super(renderManagerIn);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.getTicksAlive() < entity.getStartGrowTime()) {
					return;
				}
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
		    @Override
		    protected void preRenderCallback(EC entity, float partialTick) {
		    	super.preRenderCallback(entity, partialTick);
		    	if (entity.ignited()) {
			        float f = entity.getIgnitionProgress(partialTick);
			        float f2 = 1.0F + f * 3.0F - 0.1F * (entity.ticksExisted % 2);
			        float f3 = 1.0F + f * 0.05F;
			        GlStateManager.scale(f2, f3, f2);
		    	}
		    }
	
		    @Override
		    protected int getColorMultiplier(EC entity, float lightBrightness, float partialTick) {
		    	int ticksAlive = entity.getTicksAlive();
		    	int startGrowTime = entity.getStartGrowTime();
		    	if (ticksAlive - startGrowTime <= entity.growTicks) {
			        float f = (partialTick + (float)ticksAlive - (float)startGrowTime) / (float)entity.growTicks;
		            int i = MathHelper.clamp((int)(f * f * f * f * f * 255.0F), 1, 255);
		            return i << 24 | 0x00E0E0E0;
		    	}
		    	return 0;
		    }
		}
	}
}
