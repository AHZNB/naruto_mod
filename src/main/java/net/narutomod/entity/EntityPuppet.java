
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumActionResult;
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
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.Render;
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
			this.navigator = new PathNavigateFlying(this, worldIn);
			this.moveHelper = new FlyHelper(this);
		}

		public Base(EntityLivingBase ownerIn) {
			this(ownerIn.world);
			if (ownerIn instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)ownerIn, ItemNinjutsu.block);
				if (stack != null && ((ItemNinjutsu.RangedItem)stack.getItem())
				 .canActivateJutsu(stack, ItemNinjutsu.PUPPET, (EntityPlayer)ownerIn) == EnumActionResult.SUCCESS) {
					this.setOwner(ownerIn);
				}
			} else {
				this.setOwner(ownerIn);
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

		protected Vec3d getOffsetToOwner() {
			return new Vec3d(0.0d, 0.0d, 4.0d);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			Vec3d vec = this.getOffsetToOwner();
			this.tasks.addTask(3, new AIStayInFrontOfOwner(this, vec.x, vec.y, vec.z));
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
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4D);
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
				this.setOwner(player.equals(this.getOwner()) ? null : player);
				return true;
			}
			return false;
		}

		@Override
		public void onLivingUpdate() {
			this.updateArmSwingProgress();
			super.onLivingUpdate();
		}

	    @Override
	    public void onUpdate() {
	    	this.setAge(this.getAge() + 1);
	    	this.fallDistance = 0f;
	    	this.clearActivePotions();
	    	
	    	super.onUpdate();

	    	EntityLivingBase owner = this.getOwner();
	    	this.setNoGravity(owner != null);
			if (owner != null && this.getVelocity() > 0.1d && this.ticksExisted % 2 == 0) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:wood_click"))), 
				 0.6f, this.rand.nextFloat() * 0.6f + 0.6f);
			}
	    	if (!this.world.isRemote && owner != null && this.getDistanceSq(owner) > 1600d) {
	    		this.setOwner(null);
	    	}
	    }

		@Override
		public Vec3d getLookVec() {
			return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
		}

		public double getVelocity() {
			return MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
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
						float f = (float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue()) * 0.1f;
						this.entity.motionX += d0 / d3 * f;
						this.entity.motionY += d1 / d3 * f;
						this.entity.motionZ += d2 / d3 * f;
						float f1 = -((float)MathHelper.atan2(this.entity.motionX, this.entity.motionZ)) * (180F / (float)Math.PI);
						//this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f1, 10.0F);
						this.entity.renderYawOffset = this.entity.rotationYaw = f1;
					}
				}
			}
		}

	    public class AIStayInFrontOfOwner extends EntityAIBase {
	    	private final Base entity;
	    	private EntityLivingBase owner;
	    	private final Vec3d offsetVec;
	    	
	        public AIStayInFrontOfOwner(Base entityIn, double offX, double offY, double offZ) {
	        	this.entity = entityIn;
	        	this.offsetVec = new Vec3d(offX, offY, offZ);
	            this.setMutexBits(3);
	        }

	        @Override
	        public boolean shouldExecute() {
	        	EntityLivingBase entitylb = this.entity.getOwner();
		        if (entitylb == null) {
		            return false;
		        } else if (entitylb instanceof EntityPlayer && ((EntityPlayer)entitylb).isSpectator()) {
		            return false;
		        } else if (this.entity.getDistanceSq(entitylb) > 1600d) {
		            return false;
		        } else {
		            this.owner = entitylb;
		            return true;
		        }
	        }

			@Override
		    public boolean shouldContinueExecuting() {
		        return this.owner != null && this.entity.getDistanceSq(this.owner) <= 1600d;
		    }

			@Override
		    public void resetTask() {
		        this.owner = null;
		    }

	        @Override
	        public void updateTask() {
	        	if (this.owner != null) {
	        		Vec3d vec = this.offsetVec.rotateYaw(-this.owner.rotationYaw * (float)Math.PI / 180F).add(this.owner.getPositionVector());
	        		BlockPos pos = new BlockPos(vec);
	        		for (int i = 0; i < 4 && !this.isOpenPath(this.entity.world, pos.up(i)); i++) {
	        			vec = vec.addVector(0d, 1.01d, 0d);
	        		}
       				this.entity.getMoveHelper().setMoveTo(vec.x, vec.y, vec.z, this.entity.getDistance(vec.x, vec.y, vec.z));
	        	}
	        }

	        private boolean isOpenPath(World world, BlockPos pos) {
        		return world.getBlockState(pos).getCollisionBoundingBox(world, pos) == null
        		 && world.getBlockState(pos.up()).getCollisionBoundingBox(world, pos.up()) == null;
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

	public static class ClientClass {
		@SideOnly(Side.CLIENT)
		public static abstract class RenderScroll<T extends Entity> extends Render<T> {
			private final ModelScroll model = new ModelScroll();
	
			public RenderScroll(RenderManager renderManager) {
				super(renderManager);
				shadowSize = 0.1f;
			}
	
			@Override
			public void doRender(T bullet, double d, double d1, double d2, float f, float f1) {
				this.bindEntityTexture(bullet);
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) d, (float) d1, (float) d2);
				GlStateManager.scale(2.0f, 2.0f, 2.0f);
				GlStateManager.rotate(-f, 0, 1, 0);
				GlStateManager.rotate(180f - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * f1, 1, 0, 0);
				this.model.render(bullet, 0, 0, f1 + bullet.ticksExisted, 0, 0, 0.0625f);
				GlStateManager.popMatrix();
			}
		}
	
		// Made with Blockbench 4.4.2
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public static class ModelScroll extends ModelBase {
			private final ModelRenderer hinge;
			private final ModelRenderer[] bone = new ModelRenderer[14];
			public ModelScroll() {
				textureWidth = 16;
				textureHeight = 16;
				hinge = new ModelRenderer(this);
				hinge.setRotationPoint(0.0F, -0.85F, 0.0F);
				hinge.cubeList.add(new ModelBox(hinge, 0, 0, -4.0F, -0.5F, -0.5F, 4, 1, 1, 0.1F, false));
				hinge.cubeList.add(new ModelBox(hinge, 0, 0, 0.0F, -0.5F, -0.5F, 4, 1, 1, 0.1F, true));
				bone[0] = new ModelRenderer(this);
				bone[0].setRotationPoint(0.0F, 0.0F, 0.5F);
				setRotationAngle(bone[0], -1.5708F, 0.0F, 0.0F);
				bone[0].cubeList.add(new ModelBox(bone[0], 0, 2, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[1] = new ModelRenderer(this);
				bone[1].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[0].addChild(bone[1]);
				setRotationAngle(bone[1], -1.0472F, 0.0F, 0.0F);
				bone[1].cubeList.add(new ModelBox(bone[1], 0, 3, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[2] = new ModelRenderer(this);
				bone[2].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[1].addChild(bone[2]);
				setRotationAngle(bone[2], -1.0472F, 0.0F, 0.0F);
				bone[2].cubeList.add(new ModelBox(bone[2], 0, 4, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[3] = new ModelRenderer(this);
				bone[3].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[2].addChild(bone[3]);
				setRotationAngle(bone[3], -1.0472F, 0.0F, 0.0F);
				bone[3].cubeList.add(new ModelBox(bone[3], 0, 5, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[4] = new ModelRenderer(this);
				bone[4].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[3].addChild(bone[4]);
				setRotationAngle(bone[4], -1.0472F, 0.0F, 0.0F);
				bone[4].cubeList.add(new ModelBox(bone[4], 0, 6, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[5] = new ModelRenderer(this);
				bone[5].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[4].addChild(bone[5]);
				setRotationAngle(bone[5], -1.0472F, 0.0F, 0.0F);
				bone[5].cubeList.add(new ModelBox(bone[5], 0, 7, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[6] = new ModelRenderer(this);
				bone[6].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[5].addChild(bone[6]);
				setRotationAngle(bone[6], -1.0472F, 0.0F, 0.0F);
				bone[6].cubeList.add(new ModelBox(bone[6], 0, 8, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[7] = new ModelRenderer(this);
				bone[7].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[6].addChild(bone[7]);
				setRotationAngle(bone[7], -1.0472F, 0.0F, 0.0F);
				bone[7].cubeList.add(new ModelBox(bone[7], 0, 9, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[8] = new ModelRenderer(this);
				bone[8].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[7].addChild(bone[8]);
				setRotationAngle(bone[8], -1.0472F, 0.0F, 0.0F);
				bone[8].cubeList.add(new ModelBox(bone[8], 0, 10, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[9] = new ModelRenderer(this);
				bone[9].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[8].addChild(bone[9]);
				setRotationAngle(bone[9], -1.0472F, 0.0F, 0.0F);
				bone[9].cubeList.add(new ModelBox(bone[9], 0, 11, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[10] = new ModelRenderer(this);
				bone[10].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[9].addChild(bone[10]);
				setRotationAngle(bone[10], -1.0472F, 0.0F, 0.0F);
				bone[10].cubeList.add(new ModelBox(bone[10], 0, 12, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[11] = new ModelRenderer(this);
				bone[11].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[10].addChild(bone[11]);
				setRotationAngle(bone[11], -1.0472F, 0.0F, 0.0F);
				bone[11].cubeList.add(new ModelBox(bone[11], 0, 13, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[12] = new ModelRenderer(this);
				bone[12].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[11].addChild(bone[12]);
				setRotationAngle(bone[12], -1.0472F, 0.0F, 0.0F);
				bone[12].cubeList.add(new ModelBox(bone[12], 0, 14, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
				bone[13] = new ModelRenderer(this);
				bone[13].setRotationPoint(0.0F, 1.0F, 0.0F);
				bone[12].addChild(bone[13]);
				setRotationAngle(bone[13], -1.0472F, 0.0F, 0.0F);
				bone[13].cubeList.add(new ModelBox(bone[13], 0, 15, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
				hinge.render(f5);
				bone[0].render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				for (int i = 1; i < bone.length; i++) {
					bone[i].rotateAngleX = MathHelper.clamp(1.0F - f2 + i, 0.0F, 1.0F) * -1.0472F;
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
			private static final ResourceLocation FUUIN_TEXTURE = new ResourceLocation("narutomod:textures/fuuin_beam_blue.png");
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
}
