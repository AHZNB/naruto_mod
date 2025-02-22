
package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;

import java.util.Iterator;
import java.util.ArrayList;

@ElementsNarutomodMod.ModElement.Tag
public class EntityGuren extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 488;
	public static final int ENTITYID_RANGED = 489;
	public EntityGuren(ElementsNarutomodMod instance) {
		super(instance, 911);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "guren"), ENTITYID)
				.name("guren").tracker(64, 3, true).egg(-16751104, -3355648).build());
	}

	private Biome[] allbiomes(net.minecraft.util.registry.RegistryNamespaced<ResourceLocation, Biome> in) {
		Iterator<Biome> itr = in.iterator();
		ArrayList<Biome> ls = new ArrayList<Biome>();
		while (itr.hasNext())
			ls.add(itr.next());
		return ls.toArray(new Biome[ls.size()]);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new RenderLiving(renderManager, new ModelGuren(), 0.5f) {
				protected ResourceLocation getEntityTexture(Entity entity) {
					return new ResourceLocation("narutomod:textures/guren.png");
				}
			};
		});
	}
	public static class EntityCustom extends EntityMob {
		public EntityCustom(World world) {
			super(world);
			setSize(0.6f, 1.8f);
			experienceValue = 0;
			this.isImmuneToFire = false;
			setNoAI(!true);
			enablePersistence();
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAISwimming(this));
			this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.2, false));
			this.tasks.addTask(4, new EntityAIWander(this, 1));
			this.tasks.addTask(5, new EntityAILookIdle(this));
		}

		@Override
		public EnumCreatureAttribute getCreatureAttribute() {
			return EnumCreatureAttribute.UNDEFINED;
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
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.generic.hurt"));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.generic.death"));
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40D);
			if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}
	}

	// Made with Blockbench 4.12.2
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelGuren extends ModelBase {
		private final ModelRenderer bipedHead;
		private final ModelRenderer bipedHeadwear;
		private final ModelRenderer bone20;
		private final ModelRenderer bone21;
		private final ModelRenderer bone22;
		private final ModelRenderer bone23;
		private final ModelRenderer bipedBody;
		private final ModelRenderer collar;
		private final ModelRenderer bone10;
		private final ModelRenderer bone;
		private final ModelRenderer bone2;
		private final ModelRenderer bone3;
		private final ModelRenderer bone4;
		private final ModelRenderer bone5;
		private final ModelRenderer bone6;
		private final ModelRenderer bone8;
		private final ModelRenderer bone9;
		private final ModelRenderer bone11;
		private final ModelRenderer bone12;
		private final ModelRenderer bone13;
		private final ModelRenderer bone14;
		private final ModelRenderer bone15;
		private final ModelRenderer bone7;
		private final ModelRenderer rope;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r2;
		private final ModelRenderer loop2;
		private final ModelRenderer cube_r3;
		private final ModelRenderer cube_r4;
		private final ModelRenderer loop;
		private final ModelRenderer cube_r5;
		private final ModelRenderer cube_r6;
		private final ModelRenderer bipedRightArm;
		private final ModelRenderer bipedLeftArm;
		private final ModelRenderer bipedRightLeg;
		private final ModelRenderer bone17;
		private final ModelRenderer bone16;
		private final ModelRenderer bipedLeftLeg;
		private final ModelRenderer bone18;
		private final ModelRenderer bone19;
		public ModelGuren() {
			textureWidth = 64;
			textureHeight = 64;
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, 0.0F, -4.0F, 2, 2, 2, 0.0F, false));
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, 2.0F, 0.0F, -4.0F, 2, 2, 2, 0.0F, true));
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone20 = new ModelRenderer(this);
			bone20.setRotationPoint(0.0F, -5.0F, 3.0F);
			bipedHeadwear.addChild(bone20);
			setRotationAngle(bone20, 0.7854F, 0.0F, 0.0F);
			bone20.cubeList.add(new ModelBox(bone20, 16, 52, -3.0F, -3.0F, 0.0F, 6, 6, 6, 0.0F, false));
			bone21 = new ModelRenderer(this);
			bone21.setRotationPoint(0.0F, -5.0F, 3.0F);
			bipedHeadwear.addChild(bone21);
			setRotationAngle(bone21, 0.2618F, -0.5236F, 0.0F);
			bone21.cubeList.add(new ModelBox(bone21, 16, 52, -3.0F, -3.0F, 0.0F, 6, 6, 6, 0.0F, false));
			bone22 = new ModelRenderer(this);
			bone22.setRotationPoint(0.0F, -5.0F, 3.0F);
			bipedHeadwear.addChild(bone22);
			setRotationAngle(bone22, 0.2618F, 0.5236F, 0.0F);
			bone22.cubeList.add(new ModelBox(bone22, 16, 52, -3.0F, -3.0F, 0.0F, 6, 6, 6, 0.0F, true));
			bone23 = new ModelRenderer(this);
			bone23.setRotationPoint(0.0F, -5.0F, 3.0F);
			bipedHeadwear.addChild(bone23);
			setRotationAngle(bone23, -0.5236F, 0.0F, 0.0F);
			bone23.cubeList.add(new ModelBox(bone23, 16, 52, -3.0F, -3.0F, 0.0F, 6, 6, 6, 0.0F, false));
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5F, false));
			collar = new ModelRenderer(this);
			collar.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.addChild(collar);
			bone10 = new ModelRenderer(this);
			bone10.setRotationPoint(0.0F, 0.0F, 0.0F);
			collar.addChild(bone10);
			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.5F, 5.25F, -2.0F);
			bone10.addChild(bone);
			setRotationAngle(bone, 0.5236F, -0.2618F, -0.6109F);
			bone.cubeList.add(new ModelBox(bone, 54, 16, -1.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, false));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 4.5F, -2.0F);
			bone10.addChild(bone2);
			setRotationAngle(bone2, 0.2618F, -0.3491F, -0.6545F);
			bone2.cubeList.add(new ModelBox(bone2, 54, 16, -1.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, false));
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(-1.0F, 4.0F, -2.0F);
			bone10.addChild(bone3);
			setRotationAngle(bone3, 0.0F, 0.0F, -0.6109F);
			bone3.cubeList.add(new ModelBox(bone3, 54, 16, -1.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, false));
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(-2.0F, 3.75F, -2.0F);
			bone10.addChild(bone4);
			setRotationAngle(bone4, -0.2618F, 0.2618F, -0.6109F);
			bone4.cubeList.add(new ModelBox(bone4, 54, 16, -1.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, false));
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(-2.0F, 2.75F, -2.0F);
			bone10.addChild(bone5);
			setRotationAngle(bone5, -0.5236F, 0.5236F, -0.7854F);
			bone5.cubeList.add(new ModelBox(bone5, 54, 16, -2.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, false));
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(-2.0F, 2.75F, -1.0F);
			bone10.addChild(bone6);
			setRotationAngle(bone6, -0.7854F, 0.7854F, -0.9599F);
			bone6.cubeList.add(new ModelBox(bone6, 54, 16, -2.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, false));
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(0.0F, 0.0F, 0.0F);
			collar.addChild(bone8);
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(-0.5F, 5.25F, -2.0F);
			bone8.addChild(bone9);
			setRotationAngle(bone9, 0.5236F, 0.2618F, 0.6109F);
			bone9.cubeList.add(new ModelBox(bone9, 54, 16, 0.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, true));
			bone11 = new ModelRenderer(this);
			bone11.setRotationPoint(0.0F, 4.5F, -2.0F);
			bone8.addChild(bone11);
			setRotationAngle(bone11, 0.2618F, 0.3491F, 0.6545F);
			bone11.cubeList.add(new ModelBox(bone11, 54, 16, 0.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, true));
			bone12 = new ModelRenderer(this);
			bone12.setRotationPoint(1.0F, 4.0F, -2.0F);
			bone8.addChild(bone12);
			setRotationAngle(bone12, 0.0F, 0.0F, 0.6109F);
			bone12.cubeList.add(new ModelBox(bone12, 54, 16, 0.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, true));
			bone13 = new ModelRenderer(this);
			bone13.setRotationPoint(2.0F, 3.75F, -2.0F);
			bone8.addChild(bone13);
			setRotationAngle(bone13, -0.2618F, -0.2618F, 0.6109F);
			bone13.cubeList.add(new ModelBox(bone13, 54, 16, 0.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, true));
			bone14 = new ModelRenderer(this);
			bone14.setRotationPoint(2.0F, 2.75F, -2.0F);
			bone8.addChild(bone14);
			setRotationAngle(bone14, -0.5236F, -0.5236F, 0.7854F);
			bone14.cubeList.add(new ModelBox(bone14, 54, 16, 1.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, true));
			bone15 = new ModelRenderer(this);
			bone15.setRotationPoint(2.0F, 2.75F, -1.0F);
			bone8.addChild(bone15);
			setRotationAngle(bone15, -0.7854F, -0.7854F, 0.9599F);
			bone15.cubeList.add(new ModelBox(bone15, 54, 16, 1.0F, -8.0F, 0.0F, 1, 8, 4, 0.0F, true));
			bone7 = new ModelRenderer(this);
			bone7.setRotationPoint(0.0F, 3.0F, 0.5F);
			collar.addChild(bone7);
			setRotationAngle(bone7, -0.6109F, 0.0F, 0.0F);
			bone7.cubeList.add(new ModelBox(bone7, 38, 48, -2.0F, -8.0F, 0.5F, 4, 8, 1, 0.0F, false));
			rope = new ModelRenderer(this);
			rope.setRotationPoint(0.0F, 9.0F, 0.0F);
			bipedBody.addChild(rope);
			setRotationAngle(rope, 0.0F, 3.1416F, -3.1416F);
			rope.cubeList.add(new ModelBox(rope, 12, 48, -5.0F, -1.0F, 0.8165F, 10, 2, 2, 0.0F, true));
			rope.cubeList.add(new ModelBox(rope, 12, 48, -5.0F, -1.0F, -2.9335F, 10, 2, 2, 0.0F, true));
			rope.cubeList.add(new ModelBox(rope, 20, 48, -5.0F, -1.0F, -0.9335F, 2, 2, 2, 0.0F, true));
			rope.cubeList.add(new ModelBox(rope, 20, 48, 3.0F, -1.0F, -0.9335F, 2, 2, 2, 0.0F, false));
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(-2.5242F, -4.1207F, 5.6785F);
			rope.addChild(cube_r1);
			setRotationAngle(cube_r1, -0.005F, 0.1752F, -2.0393F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 15, 48, -1.9311F, -0.9553F, -9.8621F, 7, 2, 2, 0.0F, false));
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(2.5242F, -4.1207F, 5.6785F);
			rope.addChild(cube_r2);
			setRotationAngle(cube_r2, -0.005F, -0.1752F, 2.0393F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 15, 48, -5.0689F, -0.9553F, -9.8621F, 7, 2, 2, 0.0F, true));
			loop2 = new ModelRenderer(this);
			loop2.setRotationPoint(0.8076F, -0.7437F, 6.0F);
			rope.addChild(loop2);
			setRotationAngle(loop2, 0.0F, 0.2618F, -0.3927F);
			loop2.cubeList.add(new ModelBox(loop2, 16, 48, 2.6835F, -1.3692F, -9.6933F, 6, 2, 2, 0.0F, true));
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
			loop2.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.0F, 0.0F, 1.0385F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 16, 48, 0.1822F, -3.0071F, -9.6933F, 6, 2, 2, 0.0F, true));
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(1.8582F, 4.8775F, 0.0F);
			loop2.addChild(cube_r4);
			setRotationAngle(cube_r4, 0.0F, 0.0F, 2.0857F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 16, 48, -7.1062F, -3.8394F, -9.6933F, 6, 2, 2, 0.0F, true));
			loop = new ModelRenderer(this);
			loop.setRotationPoint(-0.8076F, -0.7437F, 6.0F);
			rope.addChild(loop);
			setRotationAngle(loop, 0.0F, -0.2618F, 0.3927F);
			loop.cubeList.add(new ModelBox(loop, 16, 48, -8.6835F, -1.3692F, -9.6933F, 6, 2, 2, 0.0F, false));
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
			loop.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.0F, 0.0F, -1.0385F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 16, 48, -6.1822F, -3.0071F, -9.6933F, 6, 2, 2, 0.0F, false));
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(-1.8582F, 4.8775F, 0.0F);
			loop.addChild(cube_r6);
			setRotationAngle(cube_r6, 0.0F, 0.0F, -2.0857F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 16, 48, 1.1062F, -3.8394F, -9.6933F, 6, 2, 2, 0.0F, false));
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
			setRotationAngle(bipedRightArm, -0.3927F, 0.0F, 0.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 32, 0, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 48, 0, -2.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
			setRotationAngle(bipedLeftArm, 0.3927F, 0.0F, 0.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 0, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 3, 12, 4, 0.25F, false));
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			setRotationAngle(bipedRightLeg, 0.3927F, 0.0F, 0.0F);
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
			bone17 = new ModelRenderer(this);
			bone17.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedRightLeg.addChild(bone17);
			setRotationAngle(bone17, -0.1745F, 0.0F, 0.1745F);
			bone17.cubeList.add(new ModelBox(bone17, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
			bone16 = new ModelRenderer(this);
			bone16.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedRightLeg.addChild(bone16);
			setRotationAngle(bone16, 0.1745F, 0.0F, 0.1745F);
			bone16.cubeList.add(new ModelBox(bone16, 48, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			setRotationAngle(bipedLeftLeg, -0.3927F, 0.0F, 0.0F);
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
			bone18 = new ModelRenderer(this);
			bone18.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedLeftLeg.addChild(bone18);
			setRotationAngle(bone18, -0.1745F, 0.0F, -0.1745F);
			bone18.cubeList.add(new ModelBox(bone18, 0, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
			bone19 = new ModelRenderer(this);
			bone19.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedLeftLeg.addChild(bone19);
			setRotationAngle(bone19, 0.1745F, 0.0F, -0.1745F);
			bone19.cubeList.add(new ModelBox(bone19, 40, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			bipedHead.render(f5);
			bipedHeadwear.render(f5);
			bipedBody.render(f5);
			bipedRightArm.render(f5);
			bipedLeftArm.render(f5);
			bipedRightLeg.render(f5);
			bipedLeftLeg.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
			this.bipedRightLeg.rotateAngleX = MathHelper.cos(f * 1.0F) * 1.0F * f1;
			this.bipedLeftLeg.rotateAngleX = MathHelper.cos(f * 1.0F) * -1.0F * f1;
			this.bipedHeadwear.rotateAngleY = f3 / (180F / (float) Math.PI);
			this.bipedHeadwear.rotateAngleX = f4 / (180F / (float) Math.PI);
			this.bipedRightArm.rotateAngleX = MathHelper.cos(f * 0.6662F + (float) Math.PI) * f1;
			this.bipedLeftArm.rotateAngleX = MathHelper.cos(f * 0.6662F) * f1;
			this.bipedHead.rotateAngleY = f3 / (180F / (float) Math.PI);
			this.bipedHead.rotateAngleX = f4 / (180F / (float) Math.PI);
		}
	}
}
