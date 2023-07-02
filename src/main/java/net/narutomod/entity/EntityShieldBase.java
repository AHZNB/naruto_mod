package net.narutomod.entity;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.CombatRules;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import com.google.common.base.Optional;
import java.util.UUID;

@ElementsNarutomodMod.ModElement.Tag
public abstract class EntityShieldBase extends EntityLivingBase {
	private static final DataParameter<Optional<UUID>> SUMMONER_UUID = EntityDataManager.<Optional<UUID>>createKey(EntityShieldBase.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private EntityLivingBase summoner;
	private boolean ownerCanSteer = false;
	private float steerSpeed;
	protected boolean dieOnNoPassengers = true;
	
	public EntityShieldBase(World world) {
		super(world);
		//this.experienceValue = 0;
		this.isImmuneToFire = true;
		this.setAlwaysRenderNameTag(false);
		//this.setNoAI(true);
		//this.enablePersistence();
	}

	public EntityShieldBase(EntityLivingBase summonerIn) {
		this(summonerIn, summonerIn.posX, summonerIn.posY, summonerIn.posZ);
	}

	public EntityShieldBase(EntityLivingBase summonerIn, double x, double y, double z) {
		this(summonerIn.world);
		this.setSummoner(summonerIn);
		this.setLocationAndAngles(x, y, z, summonerIn.rotationYaw, summonerIn.rotationPitch);
		this.setAlwaysRenderNameTag(false);
		summonerIn.startRiding(this);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(SUMMONER_UUID, Optional.absent());
	}

	private void setSummonerUuid(UUID uuid) {
		this.dataManager.set(SUMMONER_UUID, Optional.fromNullable(uuid));
	}

	private UUID getSummonerUuid() {
		return (UUID)((Optional)this.dataManager.get(SUMMONER_UUID)).orNull();
	}
	
	public void setSummoner(EntityLivingBase player) {
	    this.setSummonerUuid(player.getUniqueID());
	}
	
	@Nullable
	public EntityLivingBase getSummoner() {
	    UUID uuid = this.getSummonerUuid();
	    if (uuid == null) {
	   		return null;
	    } else {
	    	Entity entity = ProcedureUtils.getEntityFromUUID(this.world, uuid);
	        if (entity instanceof EntityLivingBase) {
	        	return (EntityLivingBase)entity;
	        }
		    return null;
	    }
	}

	@Override
	public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
		return null;
	}

	@Override
	public net.minecraft.util.SoundEvent getDeathSound() {
		return null;
	}

	@Override
	protected float getSoundVolume() {
		return 1.0F;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.getImmediateSource() instanceof EntityLivingBase && source.getImmediateSource().equals(this.getControllingPassenger()))
			return false;
		if (source == DamageSource.FALL || source == DamageSource.CACTUS || source == DamageSource.IN_WALL)
			return false;
		float f = this.getHealth();
		boolean flag = super.attackEntityFrom(source, amount);
		EntityLivingBase summoner = this.getSummoner();
		if (flag && summoner != null && !this.isEntityAlive()) {
			summoner.attackEntityFrom(source, CombatRules.getDamageAfterAbsorb(amount, (float)this.getTotalArmorValue(), 0f) - f);
		}
		return flag;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer entity, EnumHand hand) {
		super.processInitialInteract(entity, hand);
		if (!this.world.isRemote && entity.equals(this.getSummoner())) {
			entity.startRiding((Entity) this);
			return true;
		}
		return false;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(ProcedureUtils.MAXHEALTH);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	public IAttributeInstance getEntityAttribute(IAttribute attribute) {
		return super.getEntityAttribute(attribute == SharedMonsterAttributes.MAX_HEALTH ? ProcedureUtils.MAXHEALTH : attribute);
	}

	protected void turnBodyAndHead(Entity passenger) {
		this.rotationYaw = passenger.rotationYaw;
		this.prevRotationYaw = this.rotationYaw;
		this.rotationPitch = passenger.rotationPitch;
		this.setRotation(this.rotationYaw, this.rotationPitch);
		this.renderYawOffset = passenger.rotationYaw;
		this.rotationYawHead = passenger.rotationYaw;
	}

	@Override
	public void travel(float ti, float tj, float tk) {
		if (this.isBeingRidden()) {
			Entity entity = getControllingPassenger();
			this.turnBodyAndHead(entity);
			if (entity instanceof EntityLivingBase && this.ownerCanSteer) {
				this.jumpMovementFactor = ((EntityLivingBase)entity).getAIMoveSpeed() * 0.15F;
				this.setAIMoveSpeed((float)ProcedureUtils.getModifiedSpeed((EntityLivingBase)entity) * this.steerSpeed);
				float forward = ((EntityLivingBase)entity).moveForward;
				float strafe = ((EntityLivingBase)entity).moveStrafing;
				super.travel(strafe, 0.0F, forward);
			}
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

	public void setOwnerCanSteer(boolean canSteer, float speed) {
		this.ownerCanSteer = canSteer;
		this.steerSpeed = speed;
	}

	@Override
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
	}

	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
	}

	private void clampMotion(double d) {
		if (this.getRevengeTarget() != null && this.ticksExisted - this.getRevengeTimer() < 10) {
			if (Math.abs(this.motionX) > d)
				this.motionX = (this.motionX > 0.0D) ? d : -d;
			if (Math.abs(this.motionY) > d)
				this.motionY = (this.motionY > 0.0D) ? d : -d;
			if (Math.abs(this.motionZ) > d)
				this.motionZ = (this.motionZ > 0.0D) ? d : -d;
		}
	}

	@Override
	public void onLivingUpdate() {
		this.clearActivePotions();
		super.onLivingUpdate();
		clampMotion(0.1D);
		EntityLivingBase summoner = this.getSummoner();
		if ((this.getPassengers().isEmpty() && this.dieOnNoPassengers) 
		 || (summoner != null && !summoner.isEntityAlive())) {
			this.setDead();
		}
	}

	@Override
	public void onDeath(DamageSource cause) {
		if (!this.dead) {
			this.dead = true;
			this.world.setEntityState(this, (byte)3);
		}
	}

	@Override
	protected void onDeathUpdate() {
		this.setDead();
	}

	//@Override
	//public Vec3d getLookVec() {
	//	return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
	//}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasUniqueId("summonerUUID")) {
			this.setSummonerUuid(compound.getUniqueId("summonerUUID"));
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		UUID suuid = this.getSummonerUuid();
		if (suuid != null) {
			compound.setUniqueId("summonerUUID", suuid);
		}
	}

	@Override
	public EnumHandSide getPrimaryHand() {
		return EnumHandSide.RIGHT;
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
	}
}
