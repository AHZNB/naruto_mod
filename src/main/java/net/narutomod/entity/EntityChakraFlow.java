
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.MobEffects;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureUtils;

@ElementsNarutomodMod.ModElement.Tag
public class EntityChakraFlow extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 150;
	public static final int ENTITYID_RANGED = 151;

	public EntityChakraFlow(ElementsNarutomodMod instance) {
		super(instance, 408);
	}

	public static abstract class Base extends Entity {
		private static final DataParameter<Integer> USER_ID = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private EntityLivingBase user;
		protected int ogStrength;

		public Base(World world) {
			super(world);
			this.setSize(0.1f, 0.1f);
		}

		public Base(EntityLivingBase user) {
			this(user.world);
			this.setPosition(user.posX, user.posY, user.posZ);
			this.setUser(user);
			this.setAlwaysRenderNameTag(false);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(USER_ID, Integer.valueOf(-1));
		}

		public EntityLivingBase getUser() {
			if (!this.world.isRemote) {
				return this.user;
			}
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(USER_ID)).intValue());
			return (entity instanceof EntityLivingBase) ? (EntityLivingBase)entity : null;
		}

		protected void setUser(EntityLivingBase userIn) {
			this.getDataManager().set(USER_ID, Integer.valueOf(userIn.getEntityId()));
			this.user = userIn;
		}

		public boolean isUserHoldingWeapon() {
			if (this.getUser() != null) {
				return isHoldingWeapon(this.getUser());
			}
			return false;
		}

		protected abstract void addEffects();

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (isHoldingWeapon(this.user)) {
					if (this.ogStrength == 0) {
						++this.ogStrength;
						if (this.user.isPotionActive(MobEffects.STRENGTH)) {
							this.ogStrength += this.user.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier();
						}
					}
					this.addEffects();
				} else {
					this.ogStrength = 0;
				}
			}
			if (!this.world.isRemote && (this.user == null || !this.user.isEntityAlive())) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}

	public static boolean isHoldingWeapon(EntityLivingBase entity) {
		return ProcedureUtils.isWeapon(entity.getHeldItemMainhand());
	}

	@SideOnly(Side.CLIENT)
	public static class RenderCustom<T extends Base> extends Render<T> {
		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}

		private Vec3d transform3rdPerson(Vec3d startvec, Vec3d angles, EntityLivingBase entity, float pt) {
			return ProcedureUtils.rotateRoll(startvec, (float)-angles.z)
			   .rotateYaw((float)-angles.y).rotatePitch((float)-angles.x)
			   .addVector(0.0625F * -5, 1.5F-(entity.isSneaking()?0.3f:0f), -0.05F)
			   .rotateYaw((-entity.prevRenderYawOffset - (entity.renderYawOffset - entity.prevRenderYawOffset) * pt) * (float)(Math.PI / 180d))
			   //.addVector(entity.posX, entity.posY, entity.posZ);
			   .addVector(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt);
		}

		protected void spawnParticles(T entity, Vec3d startvec, Vec3d endvec) {
		}

		@Override
		public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
			EntityLivingBase user = entity.getUser();
			if (user != null && entity.isUserHoldingWeapon()) {
				ItemStack stack = user.getHeldItemMainhand();
				Vec3d startVec = stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomChakraFlowStartVec", 10)
				 ? new Vec3d(stack.getTagCompound().getCompoundTag("CustomChakraFlowStartVec").getDouble("x"),
				             stack.getTagCompound().getCompoundTag("CustomChakraFlowStartVec").getDouble("y"),
				             stack.getTagCompound().getCompoundTag("CustomChakraFlowStartVec").getDouble("z"))
				 : new Vec3d(0d, -0.725d, 0.1d);
				Vec3d endVec = stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomChakraFlowEndVec", 10)
				 ? new Vec3d(stack.getTagCompound().getCompoundTag("CustomChakraFlowEndVec").getDouble("x"),
				             stack.getTagCompound().getCompoundTag("CustomChakraFlowEndVec").getDouble("y"),
				             stack.getTagCompound().getCompoundTag("CustomChakraFlowEndVec").getDouble("z"))
				 : new Vec3d(0d, -0.725d, 1.5d);
				RenderLivingBase<?> renderer = (RenderLivingBase<?>)this.renderManager.getEntityRenderObject(user);
				ModelRenderer armModel = ((ModelBiped)renderer.getMainModel()).bipedRightArm;
				Vec3d armAngles = new Vec3d(armModel.rotateAngleX, armModel.rotateAngleY, armModel.rotateAngleZ);
				Vec3d vec0 = this.transform3rdPerson(startVec, armAngles, user, partialTicks);
				Vec3d vec1 = this.transform3rdPerson(endVec, armAngles, user, partialTicks);
				this.spawnParticles(entity, vec0, vec1);
			}
		}

		@Override
		protected ResourceLocation getEntityTexture(T entity) {
			return null;
		}
	}
}
