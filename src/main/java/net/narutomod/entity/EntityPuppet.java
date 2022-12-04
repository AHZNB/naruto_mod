
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.item.ItemNinjutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import net.minecraft.util.EnumActionResult;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppet extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 282;
	public static final int ENTITYID_RANGED = 283;

	public EntityPuppet(ElementsNarutomodMod instance) {
		super(instance, 603);
	}

	public abstract static class Base extends EntityCreature {
		private static final DataParameter<Integer> OWNERID = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> REAL_AGE = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);

		public Base(World worldIn) {
			super(worldIn);
			this.experienceValue = 0;
			this.enablePersistence();
			this.setNoAI(true);
			//this.navigator = new PathNavigateFlying(this, worldIn);
			//this.moveHelper = new FlyHelper(this);
		}

		public Base(EntityLivingBase ownerIn) {
			this(ownerIn.world);
			if (ownerIn instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)ownerIn, ItemNinjutsu.block);
				if (stack != null && ((ItemNinjutsu.RangedItem)stack.getItem())
				 .canActivateJutsu(stack, ItemNinjutsu.PUPPET, (EntityPlayer)ownerIn) == EnumActionResult.SUCCESS) {
					this.setOwner(ownerIn);
				}
			}
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(OWNERID, Integer.valueOf(-1));
			this.dataManager.register(REAL_AGE, Integer.valueOf(0));
		}

		private void setAge(int age) {
			this.dataManager.set(REAL_AGE, Integer.valueOf(age));
		}
	
		public int getAge() {
			return ((Integer)this.getDataManager().get(REAL_AGE)).intValue();
		}

		@Nullable
		protected EntityLivingBase getOwner() {
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(OWNERID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		protected void setOwner(@Nullable EntityLivingBase player) {
			this.getDataManager().set(OWNERID, Integer.valueOf(player != null ? player.getEntityId() : -1));
			this.setNoAI(player == null);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.targetTasks.addTask(0, new AICopyOwnerTarget(this));
		}

		@Override
		protected boolean canDespawn() {
			return false;
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48D);
		}

	    @Override
	    protected SoundEvent getDeathSound() {
	        return null;
	    }
	
	    @Override
	    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
	        return null;
	    }

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.FALL) {
				return false;
			}
			if (source.isProjectile()) {
				amount *= 0.2f;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		protected boolean processInteract(EntityPlayer player, EnumHand hand) {
			ItemStack stack = player.getHeldItem(hand);
			if (!this.world.isRemote && stack.getItem() == ItemNinjutsu.block 
			 && ItemNinjutsu.getCurrentJutsu(stack) == ItemNinjutsu.PUPPET) {
				this.setOwner(player);
				return true;
			}
			return false;
		}

	    @Override
	    public void onUpdate() {
	    	this.setAge(this.getAge() + 1);
	    	this.fallDistance = 0f;
	    	this.clearActivePotions();
	    	super.onUpdate();
	    }

		@Override
		public Vec3d getLookVec() {
			return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setAge(compound.getInteger("age"));
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setInteger("age", this.getAge());
		}

		public class FlyHelper extends EntityMoveHelper {
			public FlyHelper(Base entityIn) {
				super(entityIn);
			}
				
			@Override
			public void onUpdateMoveHelper() {
				if (this.action == EntityMoveHelper.Action.MOVE_TO) {
					this.action = EntityMoveHelper.Action.WAIT;
					double d0 = this.posX - this.entity.posX;
					double d1 = this.posY - this.entity.posY;
					double d2 = this.posZ - this.entity.posZ;
					double d3 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
					if (d3 < 1.6E-7D) {
						ProcedureUtils.multiplyVelocity(this.entity, 0.0d);
					} else {
						float f = this.entity.onGround 
						 ? (float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue())
						 : (float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue());
						this.entity.motionX = d0 / d3 * f;
						this.entity.motionY = d1 / d3 * f;
						this.entity.motionZ = d2 / d3 * f;
						float f1 = -((float)MathHelper.atan2(this.entity.motionX, this.entity.motionZ)) * (180F / (float)Math.PI);
						//this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f1, 10.0F);
						this.entity.renderYawOffset = this.entity.rotationYaw = f1;
					}
				}
			}
		}

	    public class AIChargeAttack extends EntityAIBase {
	    	private Base attacker;

	        public AIChargeAttack(Base attackerIn) {
	        	this.attacker = attackerIn;
	            this.setMutexBits(1);
	        }
	
	        @Override
	        public boolean shouldExecute() {
	            if (this.attacker.getAttackTarget() != null 
	             && !this.attacker.getMoveHelper().isUpdating() && this.attacker.rand.nextInt(5) == 0) {
	                return this.attacker.getDistanceSq(this.attacker.getAttackTarget()) > 4.0D;
	            }
                return false;
	        }
	
	        @Override
	        public boolean shouldContinueExecuting() {
	            return this.attacker.getMoveHelper().isUpdating()
	             && this.attacker.getAttackTarget() != null && this.attacker.getAttackTarget().isEntityAlive();
	        }
	
	        @Override
	        public void startExecuting() {
	            EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
	            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
	            this.attacker.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 2.0D);
	        }
	
	        //@Override
	        //public void resetTask() {
	        //}
	
	        @Override
	        public void updateTask() {
	            EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
	            if (this.attacker.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox())) {
	                this.attacker.attackEntityAsMob(entitylivingbase);
	            } else if (this.attacker.getDistanceSq(entitylivingbase) < 9.0D) {
	                Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
	                this.attacker.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 2.0D);
	            }
	        }
	    }

	    public class AICopyOwnerTarget extends EntityAITarget {
	    	private Base attacker;

	        public AICopyOwnerTarget(Base creature) {
	            super(creature, false);
	            this.attacker = creature;
	        }
	
	        public boolean shouldExecute() {
	        	EntityLivingBase owner = this.attacker.getOwner();
	        	this.target = owner instanceof EntityLiving
	        	 ? ((EntityLiving)owner).getAttackTarget() 
	        	 : owner != null ? owner.getRevengeTarget() != null
	        	 ? owner.getRevengeTarget() : owner.getLastAttackedEntity() : null;
	            return this.target != null && this.isSuitableTarget(this.target, false);
	        }
	
	        public void startExecuting() {
	            this.attacker.setAttackTarget(this.target);
	            super.startExecuting();
	        }
	    }
	}

	@SideOnly(Side.CLIENT)
	public abstract static class Renderer<T extends Base> extends RenderLiving<T> {
		public Renderer(RenderManager renderManagerIn, ModelBase model, float shadowsize) {
			super(renderManagerIn, model, shadowsize);
			this.addLayer(new LayerChakraStrings(this));
		}
	}

	@SideOnly(Side.CLIENT)
	public static class LayerChakraStrings implements LayerRenderer<Base> {
		private final ResourceLocation FUUIN_TEXTURE = new ResourceLocation("narutomod:textures/fuuin_beam.png");
		private final RenderLiving renderer;

		public LayerChakraStrings(RenderLiving rendererIn) {
			this.renderer = rendererIn;
		}

	 	@Override
		public void doRenderLayer(Base entity, float _1, float _2, float pt, float _3, float _4, float _5, float _6) {
			EntityLivingBase owner = entity.getOwner();
			if (owner != null) {
				float f = ((float) entity.ticksExisted + pt) * 0.01F;
				float offset = entity.height * 0.1F;
				double dx = owner.lastTickPosX + (owner.posX - owner.lastTickPosX) * pt - (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt); ;
				double dy = owner.lastTickPosY + (owner.posY - owner.lastTickPosY) * pt - ((entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt) + offset);
				double dz = owner.lastTickPosZ + (owner.posZ - owner.lastTickPosZ) * pt - (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt);
				double dxz = MathHelper.sqrt(dx * dx + dz * dz);
				double max_l = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				float rot_y = (float) -Math.atan2(dx, dz) * 180.0F / (float) Math.PI - ProcedureUtils.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, pt);
				float rot_x = (float) -Math.atan2(dy, dxz) * 180.0F / (float) Math.PI;
				this.renderer.bindTexture(FUUIN_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, -offset + 0.5F, 0.0F);
				GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(rot_y, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(rot_x - 90.0F, 1.0F, 0.0F, 0.0F);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				RenderHelper.disableStandardItemLighting();
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.shadeModel(7425);
				float f5 = 0.0F - f;
				float f6 = (float) max_l / 32.0F - f;
				bufferbuilder.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
				for (int j = 0; j <= 8; j++) {
					float f7 = MathHelper.sin((j % 8) * ((float) Math.PI * 2F) / 8.0F) * 0.008F;
					float f8 = MathHelper.cos((j % 8) * ((float) Math.PI * 2F) / 8.0F) * 0.008F;
					float f9 = (j % 8) / 8.0F;
					bufferbuilder.pos(f7, f8, 0.0D).tex(f9, f5).color(255, 255, 255, 128).endVertex();
					bufferbuilder.pos(f7, f8, (float) max_l).tex(f9, f6).color(255, 255, 255, 128).endVertex();
				}
				tessellator.draw();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.shadeModel(7424);
				RenderHelper.enableStandardItemLighting();
				GlStateManager.popMatrix();
			}
		}

	 	@Override
		public boolean shouldCombineTextures() {
			return false;
		}
	}
}
