package net.narutomod.entity;

import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.PlayerTracker;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public abstract class EntitySusanooBase extends EntityMob {
	private static final DataParameter<Integer> OWNER_ID = EntityDataManager.<Integer>createKey(EntitySusanooBase.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> FLAME_COLOR = EntityDataManager.<Integer>createKey(EntitySusanooBase.class, DataSerializers.VARINT);
	public static final double BXP_REQUIRED_L1 = 2000.0d;
	public static final double BXP_REQUIRED_L2 = 6000.0d;
	public static final double BXP_REQUIRED_L3 = 12000.0d;
	public static final double BXP_REQUIRED_L4 = 24000.0d;
	protected double chakraUsage = 50d; // per second
	protected double chakraUsageModifier = 2d;
	protected double playerXp;
	//private EntityLivingBase ownerPlayer = null;
	
	public EntitySusanooBase(World world) {
		super(world);
		this.experienceValue = 5;
		this.isImmuneToFire = true;
		//this.noClip = true;
		this.stepHeight = 0.5F;
		this.setNoAI(true);
		this.enablePersistence();
		this.tasks.addTask(1, new EntityAILookIdle(this));
	}

	public EntitySusanooBase(EntityLivingBase player) {
		this(player.world);
		this.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, 0.0F);
		this.setOwnerPlayer(player);
		if (player instanceof EntityPlayer) {
			this.playerXp = PlayerTracker.getBattleXp((EntityPlayer)player);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MathHelper.sqrt(this.playerXp));
			 //.applyModifier(new AttributeModifier("susanoo.health", 2d * ((EntityPlayer)player).experienceLevel, 0));
			//this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			 //.applyModifier(new AttributeModifier("susanoo.damage", ((EntityPlayer)player).experienceLevel, 0));
		}
		ItemStack helmetstack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if (helmetstack.getItem() instanceof ItemSharingan.Base) {
			if (ProcedureUtils.isOriginalOwner(player, helmetstack)) {
				this.chakraUsageModifier = 1d;
			}
			int color = ((ItemSharingan.Base)helmetstack.getItem()).getColor(helmetstack);
			if (color != 0) {
				this.setFlameColor(color);
			}
		}
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ProcedureUtils.getModifiedSpeed(player) * 0.3d);
		this.setHealth(this.getMaxHealth());
		this.setAlwaysRenderNameTag(false);
		player.startRiding(this);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(OWNER_ID, Integer.valueOf(-1));
		this.dataManager.register(FLAME_COLOR, Integer.valueOf(0x202C183D));
	}

	@Nullable
	public EntityLivingBase getOwnerPlayer() {
		Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(OWNER_ID)).intValue());
		return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
	}

	protected void setOwnerPlayer(EntityLivingBase owner) {
		this.dataManager.set(OWNER_ID, Integer.valueOf(owner.getEntityId()));
	}

	protected void setFlameColor(int color) {
		this.dataManager.set(FLAME_COLOR, Integer.valueOf(color));
	}

	public int getFlameColor() {
		return ((Integer)this.dataManager.get(FLAME_COLOR)).intValue();
	}

	public abstract boolean shouldShowSword();

    public abstract void setShowSword(boolean show);

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEFINED;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.getImmediateSource() instanceof EntityPlayer && source.getImmediateSource().equals(getControllingPassenger()))
			return false;
		if (source.getImmediateSource() instanceof EntityMob && source.getImmediateSource().equals(this))
			return false;
		if (source.getImmediateSource() instanceof net.minecraft.entity.projectile.EntityArrow)
			return false;
		if (source.getImmediateSource() instanceof net.minecraft.entity.projectile.EntityPotion)
			return false;
		if (source == DamageSource.FALL)
			return false;
		if (source == DamageSource.CACTUS)
			return false;
		if (source == DamageSource.DROWN)
			return false;
		if (source == DamageSource.MAGIC)
			return false;
		if (source == DamageSource.WITHER)
			return false;
		if (source == ProcedureUtils.AMATERASU)
			return false;
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		Entity passenger = this.getControllingPassenger();
		if (passenger instanceof EntityLivingBase) {
			float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
			float f2 = 1.0f;
			if (passenger instanceof EntityPlayer) {
				f2 = ((EntityPlayer)passenger).getCooledAttackStrength(0.5f);
				((EntityPlayer)passenger).resetCooldown();
			}
			f *= f2;
			//boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);
			boolean flag = entityIn.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.getOwnerPlayer()), f);
			if (flag && entityIn instanceof EntityLivingBase) {
				ItemStack stack = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
				if (!stack.isEmpty()) {
					stack.getItem().hitEntity(stack, (EntityLivingBase)entityIn, this);
				}
				if (f2 > 0.8f) {
					((EntityLivingBase)entityIn).knockBack(this, f2 * 2.5F, 
					 (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
					this.motionX *= 0.6D;
					this.motionZ *= 0.6D;
				}
			}
			return flag;
		} else {
			return super.attackEntityAsMob(entityIn);
		}
	}

	@Override
	public boolean processInteract(EntityPlayer entity, EnumHand hand) {
		super.processInteract(entity, hand);
		if (!this.world.isRemote && entity.equals(this.getOwnerPlayer())) {
			entity.startRiding(this);
			return true;
		}
		return false;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(ProcedureUtils.MAXHEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		this.getAttributeMap().registerAttribute(EntityPlayer.REACH_DISTANCE);
		this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(7.0D);
	}

	@Override
	public IAttributeInstance getEntityAttribute(IAttribute attribute) {
		return super.getEntityAttribute(attribute == SharedMonsterAttributes.MAX_HEALTH ? ProcedureUtils.MAXHEALTH : attribute);
	}

	@Override
	public void travel(float ti, float tj, float tk) {
		if (this.isBeingRidden()) {
			Entity entity = this.getControllingPassenger();
			this.rotationYaw = entity.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = entity.rotationPitch;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.jumpMovementFactor = this.getAIMoveSpeed();
			this.renderYawOffset = entity.rotationYaw;
			this.rotationYawHead = entity.rotationYaw;
			this.stepHeight = this.height / 3.0F;
			if (entity instanceof EntityLivingBase) {
				this.setAIMoveSpeed((float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				float forward = ((EntityLivingBase) entity).moveForward;
				float strafe = ((EntityLivingBase) entity).moveStrafing;
				super.travel(strafe, 0.0F, forward);
			}
		} else {
			this.jumpMovementFactor = 0.02F;
			super.travel(ti, tj, tk);
		}
	}

	@Override
	public double getMountedYOffset() {
		return 0.35D;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
	}

	@Override
	public boolean canBeSteered() {
		return true;
	}

	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
	}

	@Override
	public void applyEntityCollision(Entity entityIn) {
		if (!this.isRidingSameEntity(entityIn) && !entityIn.noClip && !entityIn.isBeingRidden()) {
            double d0 = entityIn.posX - this.posX;
            double d1 = entityIn.posZ - this.posZ;
            double d2 = MathHelper.absMax(d0, d1);
            if (d2 >= 0.01D) {
                d2 = (double)MathHelper.sqrt(d2);
                d0 = d0 / d2;
                d1 = d1 / d2;
                double d3 = 1.0D / d2;
                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }
                d0 = d0 * d3;
                d1 = d1 * d3;
                d0 = d0 * 0.05D;
                d1 = d1 * 0.05D;
                d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
                entityIn.addVelocity(d0, 0.0D, d1);
            }
		}
	}

	private void clampMotion(double d) {
		if (Math.abs(this.motionX) > d)
			this.motionX = (this.motionX > 0.0D) ? d : -d;
		if (Math.abs(this.motionY) > d)
			this.motionY = (this.motionY > 0.0D) ? d : -d;
		if (Math.abs(this.motionZ) > d)
			this.motionZ = (this.motionZ > 0.0D) ? d : -d;
	}

	protected void consumeChakra() {
		if (this.ticksExisted % 20 == 0) {
			if (!Chakra.pathway(this.getOwnerPlayer()).consume(this.chakraUsage * this.chakraUsageModifier)) {
				this.setDead();
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		EntityLivingBase ownerPlayer = this.getOwnerPlayer();
		boolean flag = ownerPlayer instanceof EntityPlayer;
		if (!this.world.isRemote && (ownerPlayer == null || !ownerPlayer.isEntityAlive() || 
		 (ownerPlayer instanceof EntityPlayerMP && ((EntityPlayerMP)ownerPlayer).hasDisconnected()) ||
		 (!flag && !this.isBeingRidden()))) {
			this.setDead();
		}
		if (flag && !((EntityPlayer)ownerPlayer).isCreative()) {
			ownerPlayer.setSneaking(false);
		}
		if (!this.world.isRemote && flag) {
			this.consumeChakra();
		}

		super.onLivingUpdate();
		
		this.clampMotion(0.05D);

		if (this.ticksExisted % 30 == 0) {
			this.playSound((net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
			 .getObject(new ResourceLocation("block.fire.ambient")), 1.0F, this.rand.nextFloat() * 0.7F + 0.3F);
		}
		for (int i = 0; i < (int) this.height; i++) {
			double d0 = this.posX + (this.rand.nextFloat() - 0.5D) * this.width;
			double d1 = this.posY + this.rand.nextFloat() * this.height;
			double d2 = this.posZ + (this.rand.nextFloat() - 0.5D) * this.width;
			this.world.spawnAlwaysVisibleParticle(Particles.Types.FLAME.getID(), d0, d1, d2, 0.0D, 0.05D, 0.0D, this.getFlameColor(), (int)(this.width * 15f));
		}
	}

    public void attackEntityRanged(double x, double y, double z) {
    }

    public void createBullet(float size) {
    }

    public void killBullet() {
    }
}
