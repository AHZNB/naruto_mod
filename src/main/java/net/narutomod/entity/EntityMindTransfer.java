
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.world.GameType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemInton;
import net.narutomod.PlayerInput;
import net.narutomod.PlayerRender;
import net.narutomod.PlayerTracker;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityMindTransfer extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 288;
	public static final int ENTITYID_RANGED = 289;

	public EntityMindTransfer(ElementsNarutomodMod instance) {
		super(instance, 607);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "mind_transfer"), ENTITYID).name("mind_transfer")
		 .tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityDuplicate.class)
		 .id(new ResourceLocation("narutomod", "mind_transfer_self"), ENTITYID_RANGED).name("mind_transfer_self")
		 .tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements PlayerInput.Hook.IHandler {
		private EntityLivingBase user;
		private EntityLivingBase target;
		private EntityDuplicate clone;
		private Vec3d targetVec;
		private Vec3d motion2Target;
		private double chakraBurn;
		//private GameType targetGamemode = GameType.SURVIVAL;
		private final int move2TargetTime = 60;
		private PlayerInput.Hook userInput = new PlayerInput.Hook();

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, EntityLivingBase targetIn, double chakraBurnIn) {
			this(userIn.world);
			this.user = userIn;
			this.target = targetIn;
			this.targetVec = target.getPositionVector().add(Vec3d.ZERO);
			this.motion2Target = this.targetVec.subtract(userIn.getPositionVector()).scale(1d / this.move2TargetTime);
			//if (targetIn instanceof EntityPlayerMP) {
			//	this.targetGamemode = ((EntityPlayerMP)targetIn).interactionManager.getGameType();
			//}
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
			this.chakraBurn = chakraBurnIn;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				if (this.user != null) {
					this.user.getEntityData().removeTag(Jutsu.ECENTITYID);
					if (this.clone != null) {
						this.user.rotationYaw = this.clone.rotationYaw;
						this.user.setPositionAndUpdate(this.clone.posX, this.clone.posY, this.clone.posZ);
					}
					if (this.user instanceof EntityPlayer) {
						PlayerRender.setSkinCloneTarget((EntityPlayer)this.user, null);
						PlayerInput.Hook.copyInputFrom((EntityPlayerMP)this.user, this, false);
						//((EntityPlayerMP)this.user).setSpectatingEntity(null);
						this.spectate((EntityPlayerMP)this.user, null);
					}
					//ProcedureOnLivingUpdate.setNoClip(this.user, false);
					this.user.setNoGravity(false);
					this.user.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 600, 32));
					this.user.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 600, 1));
					this.user.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 600, 4));
					//this.user.removeActivePotionEffect(MobEffects.INVISIBILITY);
				}
				if (this.target != null) {
					if (this.target instanceof EntityPlayerMP) {
						this.spectate((EntityPlayerMP)this.target, null);
						this.target.setHealth(this.user.getHealth());
					}
					PlayerInput.Hook.haltTargetInput(this.target, false);
				}
				if (this.clone != null) {
					if (this.user != null) {
						this.user.setHealth(this.clone.getHealth());
					}
					this.clone.setDead();
				}
			}
		}

		@Override
		public void onUpdate() {
			if (this.user instanceof EntityPlayer && this.user.isEntityAlive() 
			 && this.target != null && this.target.isEntityAlive()
			 && Chakra.pathway(this.user).consume(this.chakraBurn)) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (this.ticksExisted == 1) {
					this.clone = new EntityDuplicate(this.user);
					this.world.spawnEntity(this.clone);
					this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:mindtransfer")), 1f, 1f);
				}
				if (this.clone != null && this.clone.getHealth() < this.clone.getMaxHealth() * 0.2F) {
					this.setDead();
				}
				if (this.ticksExisted <= this.move2TargetTime) {
					if (this.ticksExisted == 1) {
						this.user.setNoGravity(true);
						ProcedureOnLivingUpdate.setNoClip(this.user, true);
					}
					this.user.motionX = this.motion2Target.x;
					this.user.motionY = this.motion2Target.y;
					this.user.motionZ = this.motion2Target.z;
					this.user.velocityChanged = true;
					this.user.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 2, 0, false, false));
				} else if (this.user.getEntityBoundingBox().intersects(this.target.getEntityBoundingBox())) {
					if (this.target instanceof EntityPlayer) {
						if (this.ticksExisted == this.move2TargetTime + 1) {
							this.user.copyLocationAndAnglesFrom(this.target);
							PlayerRender.setSkinCloneTarget((EntityPlayer)this.user, (EntityPlayer)this.target);
							ProcedureOnLivingUpdate.setNoClip(this.user, false);
							PlayerInput.Hook.haltTargetInput(this.target, true);
							this.user.setHealth(this.target.getHealth());
							this.user.setNoGravity(false);
							//((EntityPlayerMP)this.target).setGameType(GameType.SPECTATOR);
						}
						//((EntityPlayerMP)this.target).setSpectatingEntity(this.user);
						this.spectate((EntityPlayerMP)this.target, this.user);
					} else {
						if (this.ticksExisted == this.move2TargetTime + 1) {
							ProcedureOnLivingUpdate.setNoClip(this.user, false);
							PlayerInput.Hook.copyInputFrom((EntityPlayerMP)this.user, this, true);
							PlayerInput.Hook.haltTargetInput(this.target, true);
							//this.user.setHealth(this.target.getHealth());
							this.user.setNoGravity(false);
						}
						if (this.userInput.hasNewMovementInput()) {
							this.userInput.handleMovement(this.target);
						}
						if (this.userInput.hasNewMouseEvent()) {
							this.userInput.handleMouseEvent(this.target);
						}
						this.spectate((EntityPlayerMP)this.user, this.target);
					}
				} else {
					this.setDead();
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		private void spectate(EntityPlayerMP spectator, @Nullable Entity targetEntity) {
			ProcedureOnLivingUpdate.setNoClip(spectator, targetEntity != null, spectator == this.user);
			spectator.setSpectatingEntity(targetEntity);
			if (targetEntity != null) {
				spectator.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 2, 0, false, false));
				spectator.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 10d);
				//spectator.setPositionAndUpdate(targetEntity.posX, targetEntity.posY + targetEntity.height + 1.0d, targetEntity.posZ);
				spectator.setPositionAndUpdate(targetEntity.posX, targetEntity.posY, targetEntity.posZ);
			}
		}

		@Override
		public void handlePacket(@Nullable PlayerInput.Hook.MovementPacket movementPacket, @Nullable PlayerInput.Hook.MousePacket mousePacket) {
			if (movementPacket != null) {
				this.userInput.copyMovementInput(movementPacket);
			}
			if (mousePacket != null) {
				this.userInput.copyMouseInput(mousePacket);
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ECENTITYID = "MindTransferEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ECENTITYID));
				if (entity1 instanceof EC) {
					entity1.setDead();
				} else {
					RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 30d);
					if (res != null && (res.entityHit instanceof EntityLiving || res.entityHit instanceof EntityPlayer)) {
						double d = 1.0d;
						if (entity instanceof EntityPlayer) {
							double d1 = PlayerTracker.getNinjaLevel((EntityPlayer)entity);
							if (res.entityHit instanceof EntityPlayer) {
								d = Math.max(PlayerTracker.getNinjaLevel((EntityPlayer)res.entityHit) / d1, 1.0d);
							} else if (res.entityHit instanceof EntityNinjaMob.Base) {
								d = Math.max(((EntityNinjaMob.Base)res.entityHit).getNinjaLevel() / d1, 1.0d);
							} else {
								d = Math.max(((EntityLivingBase)res.entityHit).getHealth() / entity.getHealth(), 1.0d);
							}
						}
						entity1 = new EC(entity, (EntityLivingBase)res.entityHit, ItemInton.MBTRANSFER.chakraUsage * d * 0.005d);
						entity.world.spawnEntity(entity1);
						entity.getEntityData().setInteger(ECENTITYID, entity1.getEntityId());
						return true;
					}
				}
				return false;
			}
		}

		public static class PlayerHook {
			@SubscribeEvent
			public void onChangeDimension(EntityTravelToDimensionEvent event) {
				Entity entity = event.getEntity();
				if (entity instanceof EntityLivingBase) {
					int i = entity.getEntityData().getInteger(Jutsu.ECENTITYID);
					if (i > 0) {
						Entity entity1 = entity.world.getEntityByID(i);
						if (entity1 instanceof EC) {
							entity1.setDead();
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

	public static class EntityDuplicate extends EntityClone.Base {
		public EntityDuplicate(World world) {
			super(world);
			this.setNoAI(true);
		}

		public EntityDuplicate(EntityLivingBase user) {
			super(user);
			this.setNoAI(true);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(user.getMaxHealth());
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR)
			 .setBaseValue(user.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue());
			this.setHealth(user.getHealth());
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EC.PlayerHook());
	}
}
