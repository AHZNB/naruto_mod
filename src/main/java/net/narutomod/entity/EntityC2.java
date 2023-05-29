
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumHand;

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
		public EC(World world) {
			super(world);
			this.setSize(2.0f, 1.2f);
		}

		public EC(EntityLivingBase ownerIn) {
			super(ownerIn);
			this.setSize(2.0f, 1.2f);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
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
			return passenger instanceof EntityPlayer && this.getPassengers().size() < 2;
		}

		@Override
		public double getMountedYOffset() {
			return this.height - 0.5;
		}
	
		@Override
		public Entity getControllingPassenger() {
			return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
		}
	
		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0.4d, 0d, 0d), new Vec3d(-0.5, 0d, 0d) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
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
	    	EntityLivingBase owner = this.getOwner();
	    	if (!this.world.isRemote) {
		    	this.world.createExplosion(owner, entityIn.posX, entityIn.posY, entityIn.posZ,
		    	 10f, net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, owner));
	    		this.setDead();
	    	}
    		entityIn.hurtResistantTime = 10;
	    	return entityIn.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, owner), 40f + this.rand.nextFloat() * 10f);
	    }

	    @Override
	    public void onUpdate() {
	    	super.onUpdate();
	    	if (!this.isBeingRidden() && this.getRemainingLife() > 400) {
	    		this.setRemainingLife(400);
	    	}
	    }

	    /*public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float powerIn) {
				EC c2 = new EC(entity);
				Vec3d vec = entity.getLookVec();
				vec = entity.getPositionVector().addVector(vec.x, 1d, vec.z);
				c2.setPosition(vec.x, vec.y, vec.z);
				entity.world.spawnEntity(c2);
				return true;
			}
	    }*/
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
				super(renderManagerIn, new ModelPhantom(), 0.75F);
			}
	
			@Override
			protected void preRenderCallback(EC entity, float partialTickTime) {
				GlStateManager.scale(3.0F, 3.0F, 3.0F);
				GlStateManager.translate(0.0D, 1.3125D, 0.1875D);
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
