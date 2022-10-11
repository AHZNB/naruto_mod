
package net.narutomod.entity;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.PlayerInput;
import net.narutomod.PlayerRender;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.init.MobEffects;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityTransformationJutsu extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 383;
	public static final int ENTITYID_RANGED = 384;

	public EntityTransformationJutsu(ElementsNarutomodMod instance) {
		super(instance, 762);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "transformation_jutsu"), ENTITYID)
		 .name("transformation_jutsu").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements PlayerInput.Hook.IHandler {
		private EntityLivingBase user;
		private EntityLivingBase target;
		private EntityLivingBase clone;
		private double chakraBurnPerSec;
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
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
			this.chakraBurnPerSec = chakraBurnIn;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void setDead() {
			if (!this.world.isRemote) {
				if (this.user != null) {
					ProcedureUtils.poofWithSmoke(this.user);
					this.user.getEntityData().removeTag(Jutsu.ECENTITYID);
					if (this.user instanceof EntityPlayer) {
						PlayerRender.setSkinCloneTarget((EntityPlayer)this.user, null);
						PlayerInput.Hook.copyInputFrom((EntityPlayerMP)this.user, this, false);
						this.spectate((EntityPlayerMP)this.user, null);
					}
				}
				if (this.clone != null) {
					if (this.user != null) {
						this.user.setHealth(this.clone.getHealth());
					}
					this.clone.setDead();
				}
			}
			super.setDead();
		}

		@Override
		public void onUpdate() {
			if (this.user instanceof EntityPlayer && this.user.isEntityAlive()
			 && (this.ticksExisted % 20 > 0 || Chakra.pathway(this.user).consume(this.chakraBurnPerSec))) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (this.ticksExisted == 1) {
					if (this.target instanceof EntityPlayer) {
						PlayerRender.setSkinCloneTarget((EntityPlayer)this.user, (EntityPlayer)this.target, false);
					} else {
						this.clone = (EntityLivingBase)EntityList.newEntity(this.target.getClass(), this.world);
						this.clone.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.user.getMaxHealth());
						this.clone.setHealth(this.user.getHealth());
						this.clone.copyLocationAndAnglesFrom(this.user);
						this.world.spawnEntity(this.clone);
						PlayerInput.Hook.copyInputFrom((EntityPlayerMP)this.user, this, true);
						PlayerInput.Hook.haltTargetInput(this.clone, true);
					}
					ProcedureUtils.poofWithSmoke(this.user);
				} else if (this.clone != null) {
					if (this.userInput.hasNewMovementInput()) {
						this.userInput.handleMovement(this.clone);
					}
					if (this.userInput.hasNewMouseEvent()) {
						this.userInput.handleMouseEvent(this.clone);
					}
					this.spectate((EntityPlayerMP)this.user, this.clone);
				}
				if (this.clone != null && this.clone.getHealth() < this.clone.getMaxHealth() * 0.2F) {
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
			private static final String ECENTITYID = "TransformationEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ECENTITYID));
				if (entity1 instanceof EC) {
					entity1.setDead();
				} else {
					RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 30d);
					if (res != null && res.entityHit instanceof EntityLivingBase) {
						entity1 = new EC(entity, (EntityLivingBase)res.entityHit, ItemNinjutsu.TRANSFORM.chakraUsage * 0.1d);
						entity.world.spawnEntity(entity1);
						entity.getEntityData().setInteger(ECENTITYID, entity1.getEntityId());
						return true;
					}
				}
				return false;
			}
		}
	}
}
