
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemBakuton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityC2 extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 233;
	public static final int ENTITYID_RANGED = 234;

	public EntityC2(ElementsNarutomodMod instance) {
		super(instance, 545);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "c_2"), ENTITYID).name("c_2").tracker(64, 3, true).build());
	}

	public static class EC extends ItemBakuton.ExplosiveClay {
		protected static final float WIDTH = 3.0F;
		protected static final float HEIGHT = 1.6F;
		private Vec3d forceFlyTo;
		private double forceFlySpeed;

		public EC(World world) {
			super(world);
			this.setSize(WIDTH, HEIGHT);
			this.setExplosionSize(12.0f);
		}

		public EC(EntityLivingBase ownerIn) {
			super(ownerIn);
			this.setSize(WIDTH, HEIGHT);
			this.setExplosionSize(12.0f);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40D);
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.isBeingRidden()) {
				this.clearTargetTasks();
				if (this.forceFlyTo != null) {
					this.moveHelper.setMoveTo(this.forceFlyTo.x, this.forceFlyTo.y, this.forceFlyTo.z, this.forceFlySpeed);
				}
			} else {
				this.setTargetTasks();
			}
		}

		protected void setFlyTo(double x, double y, double z, double speed) {
			if (speed == 0.0d || (x == 0.0d && y == 0.0d && z == 0.0d)) {
				this.forceFlyTo = null;
				this.forceFlySpeed = 0.0d;
			} else {
				this.forceFlyTo = new Vec3d(x, y, z);
				this.forceFlySpeed = speed;
			}
		}

		@Override
		public boolean processInteract(EntityPlayer entity, EnumHand hand) {
			super.processInteract(entity, hand);
			if (!this.world.isRemote) {
				entity.startRiding(this);
				this.setRemainingLife(10000);
				return true;
			}
			return false;
		}

		@Override
		protected boolean canFitPassenger(Entity passenger) {
			if (this.getPassengers().size() == 1) {
				return passenger instanceof EntityPlayer ? this.getPassengers().get(0) instanceof EntityPlayer : true;
			}
			return this.getPassengers().size() < 2;
		}

		@Override
		public double getMountedYOffset() {
			return this.height - 0.35f;
		}
	
		@Override
		public Entity getControllingPassenger() {
			return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
		}
	
		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0d, 0d, 0.4d), new Vec3d(0d, 0d, -0.5d) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-this.rotationYaw * 0.017453292F);
				passenger.setPosition(this.posX + vec2.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec2.z);
			}
		}

		@Override
		public void travel(float strafe, float vertical, float forward) {
			if (this.isBeingRidden() && this.getControllingPassenger() instanceof EntityPlayer) {
				EntityPlayer entity = (EntityPlayer)this.getControllingPassenger();
				this.rotationYaw = entity.rotationYaw;
				this.prevRotationYaw = this.rotationYaw;
				this.rotationPitch = entity.rotationPitch;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.jumpMovementFactor = this.getAIMoveSpeed() * 0.4f;
				this.renderYawOffset = entity.rotationYaw;
				this.rotationYawHead = entity.rotationYaw;
				this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue());
				forward = entity.moveForward == 0f ? this.onGround ? forward : forward + 0.4f 
				 : entity.moveForward < 0f ? forward : entity.moveForward;
				if ((!this.onGround || entity.rotationPitch < 0.0F) && forward > 0.0F)
					this.motionY = -entity.rotationPitch * 0.01f;
				super.travel(0.0F, 0.0F, forward);
			} else {
				this.jumpMovementFactor = 0.02F;
				super.travel(strafe, vertical, forward);
			}
		}

	    @Override
	    public boolean attackEntityAsMob(Entity entityIn) {
	    	if (super.attackEntityAsMob(entityIn)) {
	    		entityIn.hurtResistantTime = 10;
		    	return entityIn.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.getOwner()), 50f + this.rand.nextFloat() * 10f);
	    	}
	    	return false;
	    }

	    @Override
	    public void onUpdate() {
	    	super.onUpdate();
	    	if (!this.isBeingRidden() && this.getRemainingLife() > 400) {
	    		this.setRemainingLife(400);
	    	}
	    }

	    @Override
	    public boolean isImmuneToExplosions() {
	    	return true;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends RenderLiving<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/phantom1.png");
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelPhantom(), 2.5F);
			}
	
			@Override
			protected void preRenderCallback(EC entity, float partialTickTime) {
				GlStateManager.scale(6.0F, 6.0F, 6.0F);
				GlStateManager.translate(0.0D, 1.375D, 0.375D);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}
	
		// Made with Blockbench 3.8.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelPhantom extends ModelBase {
			private final ModelRenderer body;
			private final ModelRenderer leftWingBody;
			private final ModelRenderer leftWing;
			private final ModelRenderer rightWingBody;
			private final ModelRenderer rightWing;
			private final ModelRenderer head;
			private final ModelRenderer tail;
			private final ModelRenderer tailtip;
			public ModelPhantom() {
				textureWidth = 64;
				textureHeight = 64;
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 0.0F, 0.0F);
				body.cubeList.add(new ModelBox(body, 0, 8, -2.5F, -2.0F, -8.0F, 5, 3, 9, 0.0F, false));
				leftWingBody = new ModelRenderer(this);
				leftWingBody.setRotationPoint(2.5F, -2.0F, -8.0F);
				body.addChild(leftWingBody);
				setRotationAngle(leftWingBody, 0.0F, 0.0F, 0.0873F);
				leftWingBody.cubeList.add(new ModelBox(leftWingBody, 23, 12, 0.0F, 0.0F, 0.0F, 6, 2, 9, 0.0F, false));
				leftWing = new ModelRenderer(this);
				leftWing.setRotationPoint(6.0F, 0.0F, 0.0F);
				leftWingBody.addChild(leftWing);
				setRotationAngle(leftWing, 0.0F, 0.0F, 0.1745F);
				leftWing.cubeList.add(new ModelBox(leftWing, 16, 24, 0.0F, 0.0F, 0.0F, 13, 1, 9, 0.0F, false));
				rightWingBody = new ModelRenderer(this);
				rightWingBody.setRotationPoint(-2.5F, -2.0F, -8.0F);
				body.addChild(rightWingBody);
				setRotationAngle(rightWingBody, 0.0F, 0.0F, -0.0873F);
				rightWingBody.cubeList.add(new ModelBox(rightWingBody, 23, 12, -6.0F, 0.0F, 0.0F, 6, 2, 9, 0.0F, true));
				rightWing = new ModelRenderer(this);
				rightWing.setRotationPoint(-6.0F, 0.0F, 0.0F);
				rightWingBody.addChild(rightWing);
				setRotationAngle(rightWing, 0.0F, 0.0F, -0.1745F);
				rightWing.cubeList.add(new ModelBox(rightWing, 16, 24, -13.0F, 0.0F, 0.0F, 13, 1, 9, 0.0F, true));
				head = new ModelRenderer(this);
				head.setRotationPoint(0.5F, 1.0F, -7.0F);
				body.addChild(head);
				head.cubeList.add(new ModelBox(head, 0, 0, -4.0F, -2.0F, -5.0F, 7, 3, 5, 0.0F, false));
				tail = new ModelRenderer(this);
				tail.setRotationPoint(0.5F, -2.0F, 1.0F);
				body.addChild(tail);
				setRotationAngle(tail, -0.0873F, 0.0F, 0.0F);
				tail.cubeList.add(new ModelBox(tail, 3, 20, -2.0F, 0.0F, 0.0F, 3, 2, 6, 0.0F, false));
				tailtip = new ModelRenderer(this);
				tailtip.setRotationPoint(0.0F, 0.5F, 6.0F);
				tail.addChild(tailtip);
				setRotationAngle(tailtip, -0.0873F, 0.0F, 0.0F);
				tailtip.cubeList.add(new ModelBox(tailtip, 4, 29, -1.0F, 0.0F, 0.0F, 1, 1, 6, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				body.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f0, float f1, float ageInTicks, float f3, float f4, float f5, Entity entityIn) {
				float f = ((float)(entityIn.getEntityId() * 3) + ageInTicks) * 0.13F;
				if (ProcedureUtils.getVelocity(entityIn) > 0.1d) {
					this.leftWingBody.rotateAngleZ = MathHelper.cos(f) * 16.0F * ((float)Math.PI / 180F);
					this.leftWing.rotateAngleZ = this.leftWingBody.rotateAngleZ;
					this.rightWingBody.rotateAngleZ = -this.leftWingBody.rotateAngleZ;
					this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;
				} else {
					this.leftWingBody.rotateAngleZ = -0.5236F;
					this.leftWing.rotateAngleZ = 1.0472F;
					this.rightWingBody.rotateAngleZ = 0.5236F;
					this.rightWing.rotateAngleZ = -1.0472F;
				}
				this.tail.rotateAngleX = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
				this.tailtip.rotateAngleX = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
			}
		}
	}
}
