
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.event.EventSphericalExplosion;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityC3 extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 235;
	public static final int ENTITYID_RANGED = 236;

	public EntityC3(ElementsNarutomodMod instance) {
		super(instance, 546);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "c_3"), ENTITYID).name("c_3").tracker(64, 3, true).build());
	}

	public static class EC extends EntityLiving {
		private final int growTime = 30;
		private final int fuseTime = 100;
		//private int detonateTicks;
		//private double detonateX;
		//private double detonateY;
		//private double detonateZ;
		private EntitySpecialEffect.EntityCustom effectEntity;
		private EntityLivingBase user;

		public EC(World world) {
			super(world);
			this.setSize(0.6f, 1.8f);
			this.experienceValue = 0;
			this.isImmuneToFire = true;
			this.setNoAI(false);
			this.enablePersistence();
			this.setNoGravity(true);
		}

		public EC(EntityLivingBase userIn) {
			this(userIn.world);
			this.user = userIn;
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
		public SoundEvent getHurtSound(DamageSource ds) {
			return null;
		}

		@Override
		public SoundEvent getDeathSound() {
			return null;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.isExplosion() || source == DamageSource.FALL) {
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			//if (this.detonateTicks > 0) {
			//	this.detonate();
			//}
			if (!this.world.isRemote && this.ticksExisted > this.growTime) {
				if (this.effectEntity == null) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:c3")), 50f, 1f);
					this.effectEntity = EntitySpecialEffect.spawn(this.world, EntitySpecialEffect.Type.ROTATING_LINES_COLOR_END, 
					 0xFFFF00, 30f, 120, this.posX, this.posY, this.posZ);
				} else {
					this.effectEntity.setPosition(this.posX, this.posY, this.posZ);
				}
			}
			if (!this.world.isRemote && this.ticksExisted > this.fuseTime && this.hasNoGravity()) {
				this.setNoGravity(false);
				if (this.user != null) {
					this.world.playSound(null, this.user.posX, this.user.posY, this.user.posZ, 
					 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:katsu")), SoundCategory.NEUTRAL, 1f, 1f);
				}
			}
			if (!this.world.isRemote && !this.hasNoGravity() && this.onGround) {// && this.detonateTicks == 0) {
				new EventSphericalExplosion(this.world, null, (int)this.posX, (int)this.posY + 5, (int)this.posZ, 30, 0, 0.3f);
				ProcedureAoeCommand.set(this, 0d, 30d).damageEntities(ItemJutsu.causeJutsuDamage(this, null), 400f);
				this.setDead();
				if (this.effectEntity != null) {
					this.effectEntity.setLifespan(50);
				}
				//this.detonateTicks++;
				//this.detonateX = this.posX;
				//this.detonateY = this.posY;
				//this.detonateZ = this.posZ;
			}
		}

		/*private void detonate() {
			if (this.detonateTicks > 20) {
				this.setDead();
				if (this.effectEntity != null) {
					this.effectEntity.setLifespan(50);
				}
			} else {
				boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this);
				this.world.newExplosion(this, this.posX + (this.rand.nextDouble()-0.5d) * 30d,
				 this.posY, this.posZ + (this.rand.nextDouble()-0.5d) * 30d, 25f, flag, flag);
			}
			this.detonateTicks++;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends RenderLiving<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/c3.png");
	
			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelC3(), 0.5f);
			}
	
			//@Override
			//public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
			//	super.doRender(entity, x, y, z, entityYaw, partialTicks);
			//}
	
			@Override
			protected void preRenderCallback(EC entity, float partialTickTime) {
				float scale = 0.5f + 7.5f * MathHelper.clamp((float)entity.ticksExisted / entity.growTime, 0f, 1f);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.translate(0d, 0.1d * (scale - 1f), 0d);
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
		public class ModelC3 extends ModelBase {
			private final ModelRenderer body;
			private final ModelRenderer leftWing;
			private final ModelRenderer leftWingTip;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer rightWing;
			private final ModelRenderer rightWingTip;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone13;
			private final ModelRenderer head;
			private final ModelRenderer hump;
			private final ModelRenderer bone2;
			public ModelC3() {
				textureWidth = 64;
				textureHeight = 64;
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 0.0F, 0.0F);
				body.cubeList.add(new ModelBox(body, 0, 22, -5.0F, 4.0F, -5.0F, 10, 10, 10, 0.0F, false));
				body.cubeList.add(new ModelBox(body, 0, 0, -6.0F, 14.0F, -6.0F, 12, 10, 12, 0.0F, false));
				leftWing = new ModelRenderer(this);
				leftWing.setRotationPoint(5.0F, 8.0F, 0.0F);
				body.addChild(leftWing);
				setRotationAngle(leftWing, -0.6981F, 0.0F, 0.0F);
				leftWing.cubeList.add(new ModelBox(leftWing, 0, 42, 0.0F, -2.0F, -2.0F, 4, 6, 4, 0.4F, false));
				leftWingTip = new ModelRenderer(this);
				leftWingTip.setRotationPoint(2.0F, 4.0F, 0.0F);
				leftWing.addChild(leftWingTip);
				setRotationAngle(leftWingTip, 0.0F, 0.0F, 0.7854F);
				leftWingTip.cubeList.add(new ModelBox(leftWingTip, 48, 12, -2.5F, -2.0F, -1.5F, 5, 6, 3, 0.4F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(2.5F, 7.0F, 0.0F);
				leftWingTip.addChild(bone6);
				setRotationAngle(bone6, 0.0F, 0.0F, -0.2618F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.4F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 7.0F, 0.0F);
				leftWingTip.addChild(bone7);
				bone7.cubeList.add(new ModelBox(bone7, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.4F, false));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(-2.5F, 7.0F, 0.0F);
				leftWingTip.addChild(bone8);
				setRotationAngle(bone8, 0.0F, 0.0F, 0.2618F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.4F, false));
				rightWing = new ModelRenderer(this);
				rightWing.setRotationPoint(-5.0F, 8.0F, 0.0F);
				body.addChild(rightWing);
				setRotationAngle(rightWing, -0.8727F, 0.0F, 0.0F);
				rightWing.cubeList.add(new ModelBox(rightWing, 0, 42, -4.0F, -2.0F, -2.0F, 4, 6, 4, 0.4F, true));
				rightWingTip = new ModelRenderer(this);
				rightWingTip.setRotationPoint(-2.0F, 4.0F, 0.0F);
				rightWing.addChild(rightWingTip);
				setRotationAngle(rightWingTip, 0.0F, 0.0F, -0.7854F);
				rightWingTip.cubeList.add(new ModelBox(rightWingTip, 48, 12, -2.5F, -2.0F, -1.5F, 5, 6, 3, 0.4F, true));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(-2.5F, 7.0F, 0.0F);
				rightWingTip.addChild(bone11);
				setRotationAngle(bone11, 0.0F, 0.0F, 0.2618F);
				bone11.cubeList.add(new ModelBox(bone11, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.4F, true));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, 7.0F, 0.0F);
				rightWingTip.addChild(bone12);
				bone12.cubeList.add(new ModelBox(bone12, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.4F, true));
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(2.5F, 7.0F, 0.0F);
				rightWingTip.addChild(bone13);
				setRotationAngle(bone13, 0.0F, 0.0F, -0.2618F);
				bone13.cubeList.add(new ModelBox(bone13, 0, 54, -1.0F, -4.0F, -1.0F, 2, 8, 2, 0.4F, true));
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 4.5F, -4.5F);
				body.addChild(head);
				head.cubeList.add(new ModelBox(head, 44, 31, -2.5F, -3.5F, -2.5F, 5, 6, 5, 0.0F, false));
				hump = new ModelRenderer(this);
				hump.setRotationPoint(0.0F, 4.0F, -0.5F);
				body.addChild(hump);
				hump.cubeList.add(new ModelBox(hump, 30, 22, -4.5F, -2.0F, -3.0F, 9, 2, 7, 0.0F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -2.0F, -0.45F);
				hump.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.0F, 0.7854F);
				bone2.cubeList.add(new ModelBox(bone2, 36, 0, -3.0F, -3.0F, -2.05F, 6, 6, 6, 0.0F, false));
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
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				EC entity = (EC)e;
				if (entity.ticksExisted >= entity.growTime) {
					int extendTicks = entity.fuseTime - entity.growTime;
					float swing = MathHelper.clamp((float)(entity.ticksExisted - entity.growTime) / extendTicks, 0f, 1f);
					this.leftWing.rotateAngleX = (swing - 1f) * 0.6981F;
					this.leftWing.rotateAngleZ = swing * -1.7453F;
					this.leftWingTip.rotateAngleZ = (1f - swing) * 0.7854F;
					this.rightWing.rotateAngleX = (swing - 1f) * 0.8727F;
					this.rightWing.rotateAngleZ = swing * 1.7453F;
					this.rightWingTip.rotateAngleZ = (swing - 1f) * 0.7854F;
				} else {
					this.leftWing.rotateAngleX = -0.6981F;
					this.leftWing.rotateAngleZ = 0.0F;
					this.leftWingTip.rotateAngleZ = 0.7854F;
					this.rightWing.rotateAngleX = -0.8727F;
					this.rightWing.rotateAngleZ = 0.0F;
					this.rightWingTip.rotateAngleZ = -0.7854F;
				}
			}
		}
	}
}
