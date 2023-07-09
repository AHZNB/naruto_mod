
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.inventory.EntityEquipmentSlot;

import net.narutomod.potion.PotionParalysis;
import net.narutomod.item.ItemGourd;
import net.narutomod.item.ItemJiton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySandBind extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 206;
	public static final int ENTITYID_RANGED = 207;

	public EntitySandBind(ElementsNarutomodMod instance) {
		super(instance, 521);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "sand_bind"), ENTITYID).name("sand_bind").tracker(64, 3, true).build());
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
		private EntityLivingBase user;
		private EntityLivingBase targetEntity;
		private ItemJiton.SwarmTarget sandTarget;
		private Vec3d capturedVec;
		private int funeralTime;
		private final float funeralDamage = 4f; // per tick for 20 ticks
		private static final int MAXTIME = 600;

		public EC(World world) {
			super(world);
			this.setSize(0.2f, 0.2f);
			this.isImmuneToFire = true;
			this.funeralTime = -1;
		}

		public EC(EntityLivingBase userIn, EntityLivingBase targetIn, ItemJiton.Type sandType) {
			this(userIn.world);
			this.user = userIn;
			this.targetEntity = targetIn;
			Vec3d vec = this.getGourdMouthPos();
			this.setPosition(vec.x, vec.y, vec.z);
			this.sandTarget = new ItemJiton.SwarmTarget(this.world, 100, vec, 
			 this.getTargetVector(), new Vec3d(0.1d, 0.4d, 0.1d), 0.95f, 0.03f, false, 2f, sandType.getColor());
		}

		@Override
		protected void entityInit() {
		}

		private Vec3d getGourdMouthPos() {
			if (this.user != null) {
				return ItemGourd.getMouthPos(this.user);
			}
			return this.getPositionVector();
		}

		private AxisAlignedBB getTargetVector() {
			//return this.targetEntity.getPositionVector().addVector(0d, this.targetEntity.height * 0.6667f, 0d);
			return this.targetEntity.getEntityBoundingBox();
		}

		private boolean isTargetCaptured() {
			if (!ItemJutsu.canTarget(this.targetEntity)) {
				this.capturedVec = null;
				return false;
			}
			boolean flag = this.capturedVec != null;
			if (!flag && this.getEntityBoundingBox().intersects(this.targetEntity.getEntityBoundingBox())) {
				AxisAlignedBB bb = this.getEntityBoundingBox().intersect(this.targetEntity.getEntityBoundingBox());
				flag = bb.equals(this.targetEntity.getEntityBoundingBox())
				 && this.getEntityBoundingBox().getAverageEdgeLength() < this.targetEntity.getEntityBoundingBox().getAverageEdgeLength() * 2.0d;
			}
			if (flag && this.capturedVec == null) {
				this.capturedVec = this.targetEntity.getPositionVector();
			} else if (!flag) {
				this.capturedVec = null;
			}
			return flag;
		}

		public void sandFuneral() {
			this.funeralTime = 20;
		}

		private boolean canFuneral() {
			return this.funeralTime < 0 && this.targetEntity != null && this.targetEntity.isEntityAlive() && this.isTargetCaptured();
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.sandTarget != null && !this.sandTarget.shouldRemove()) {
				this.sandTarget.forceRemove();
				if (this.user != null) {
					ItemStack stack = this.user.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
					if (stack.getItem() == ItemGourd.body) {
						stack.attemptDamageItem(20, this.rand, null);
					}
				}
			}
		}

		private void attackTargetEntity(float amount) {
			//if (this.targetEntity instanceof MultiPartEntityPart
			// && ((MultiPartEntityPart)this.targetEntity).parent instanceof Entity) {
			//	((Entity)((MultiPartEntityPart)this.targetEntity).parent).hurtResistantTime = 0;
			//} else {
				this.targetEntity.hurtResistantTime = 10;
			//}
			this.targetEntity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.user)
			 .setDamageBypassesArmor(), amount);
		}

		private void holdTarget() {
			//EntityLivingBase entity = this.targetEntity;
			//if (entity instanceof MultiPartEntityPart && ((MultiPartEntityPart)entity).parent instanceof EntityLivingBase) {
			//	entity = (EntityLivingBase)((MultiPartEntityPart)entity).parent;
			//}
			this.targetEntity.addPotionEffect(new PotionEffect(PotionParalysis.potion, 2, 0, false, false));
			this.targetEntity.setPositionAndUpdate(this.capturedVec.x, this.capturedVec.y, this.capturedVec.z);
		}

		@Override
		public void onUpdate() {
			if (this.user != null && this.user.isEntityAlive() 
			 && this.sandTarget != null && !this.sandTarget.shouldRemove() && this.targetEntity != null) {
				if (this.targetEntity.isEntityAlive() && this.funeralTime != 0 && this.ticksExisted < MAXTIME) {
					if (this.isTargetCaptured()) {
						if (this.funeralTime > 0) {
							this.sandTarget.setTarget(this.getTargetVector(), 0.95f, 0.03f, false);
							this.attackTargetEntity(this.funeralDamage);
							--this.funeralTime;
						} else {
							this.sandTarget.setTarget(this.getTargetVector(), this.getEntityBoundingBox().getAverageEdgeLength() < this.targetEntity.getEntityBoundingBox().getAverageEdgeLength() * 2.0d ? 0.0f : 0.3f, 0.0f, false);
						}
						this.holdTarget();
					} else {
						this.sandTarget.setTarget(this.getTargetVector(), 2.0f, 0.03f, false);
					}
				} else {
					this.sandTarget.setTarget(this.getGourdMouthPos(), 0.8f, 0.02f, true);
				}
				this.sandTarget.onUpdate();
				this.setEntityBoundingBox(this.sandTarget.getBorders());
				this.resetPositionToBB();
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		@Override
		public void setEntityBoundingBox(AxisAlignedBB bb) {
			super.setEntityBoundingBox(bb);
			this.width = (float)Math.min(bb.maxX - bb.minX, bb.maxZ - bb.minZ);
			this.height = (float)(bb.maxY - bb.minY);
		}

		@Override
		public void resetPositionToBB() {
			super.resetPositionToBB();
			if (!this.world.isRemote && this.isAddedToWorld()) {
				ProcedureSync.ResetBoundingBox.sendToTracking(this);
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
				RayTraceResult result = ProcedureUtils.objectEntityLookingAt(entity, 30d, 2d, true);
				if (result != null) {
					if (result.entityHit instanceof EC) {
						result.entityHit.ticksExisted = EC.MAXTIME;
						return false;
					}
					if (result.entityHit instanceof EntityLivingBase) {
						for (EC ec : entity.world.getEntitiesWithinAABB(EC.class, entity.getEntityBoundingBox().grow(30d))) {
							if (result.entityHit.equals(ec.targetEntity)) {
								return false;
							}
						}
						EC entity1 = new EC(entity, (EntityLivingBase)result.entityHit, ItemJiton.getSandType(stack));
						entity.world.spawnEntity(entity1);
						return true;
					}
				}
				return false;
			}
		}
	}

	public static boolean sandFuneral(EntityLivingBase attacker) {
		RayTraceResult res = ProcedureUtils.objectEntityLookingAt(attacker, 50, true);
		if (res != null && res.entityHit instanceof EC && ((EC)res.entityHit).canFuneral()
		 && Chakra.pathway(attacker).consume(50d)) {
			attacker.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ,
			 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:sabakusoso")),
			 net.minecraft.util.SoundCategory.PLAYERS, 1f, 1f);
			((EC)res.entityHit).sandFuneral();
			return true;
		}
		return false;
	}
}
