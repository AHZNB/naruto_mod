
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemScrollSanshouo;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppetSanshouo extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 381;
	public static final int ENTITYID_RANGED = 382;
	private static final float MODELSCALE = 2.0F;

	public EntityPuppetSanshouo(ElementsNarutomodMod instance) {
		super(instance, 739);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "puppet_sanshouo"), ENTITYID)
		 .name("puppet_sanshouo").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new RenderLivingBase<EntityCustom>(renderManager, new ModelSanShouo(), 0.5f * MODELSCALE) {
				private final ResourceLocation texture = new ResourceLocation("narutomod:textures/sanshouo.png");
				@Override
				protected ResourceLocation getEntityTexture(EntityCustom entity) {
					return this.texture;
				}
				@Override
				public float prepareScale(EntityCustom entity, float partialTicks) {
					super.prepareScale(entity, partialTicks);
					GlStateManager.translate(0.0F, 1.5F - (1.5F * MODELSCALE), 0.0F);
					GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
					return 0.0625F;
				}
				@Override
				protected boolean canRenderName(EntityCustom entity) {
					return false;
				}
			};
		});
	}

	public static class EntityCustom extends EntityShieldBase {
		public static final float MAXHEALTH = 200.0f;
		private static final DataParameter<Integer> REAL_AGE = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private final float driveSpeed = 5.0F;

		public EntityCustom(World world) {
			super(world);
			this.setSize(2.0f * MODELSCALE, 1.0f * MODELSCALE);
			this.stepHeight = MODELSCALE * 1.5f;
			this.dieOnNoPassengers = false;
			this.setAlwaysRenderNameTag(false);
			//this.setOwnerCanSteer(true, this.driveSpeed);
		}

		public EntityCustom(EntityLivingBase summonerIn) {
			this(summonerIn, summonerIn.posX, summonerIn.posY, summonerIn.posZ);
		}

		public EntityCustom(EntityLivingBase summonerIn, double x, double y, double z) {
			this(summonerIn.world);
			this.setSummoner(summonerIn);
			this.setLocationAndAngles(x, y, z, summonerIn.rotationYaw, summonerIn.rotationPitch);
			this.setHealth(this.getMaxHealth());
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(REAL_AGE, Integer.valueOf(0));
		}

		private void setAge(int age) {
			this.dataManager.set(REAL_AGE, Integer.valueOf(age));
		}
	
		public int getAge() {
			return ((Integer)this.getDataManager().get(REAL_AGE)).intValue();
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAXHEALTH);
			//this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3D);
		}

		@Override
		protected boolean canFitPassenger(Entity passenger) {
			return this.getPassengers().size() < 2;
		}

		@Override
		public boolean shouldRiderSit() {
			return true;
		}

		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0d, 0.1875d * MODELSCALE, 0.125d * MODELSCALE), new Vec3d(0d, 0.1875d * MODELSCALE, -0.1875d * MODELSCALE) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-this.rotationYaw * 0.017453292F);
				passenger.setPosition(this.posX + vec2.x, this.posY + vec2.y + passenger.getYOffset(), this.posZ + vec2.z);
			}
		}

		@Override
		public boolean processInitialInteract(EntityPlayer entity, EnumHand hand) {
			if (!this.world.isRemote && entity.getHeldItem(hand).getItem() != ItemScrollSanshouo.block) {
				entity.startRiding(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.isProjectile()) {
				amount *= 0.2f;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			int age = this.getAge();
			//if (age == 0 && !this.world.isRemote) {
			//	ProcedureUtils.poofWithSmoke(this);
			//}
			Entity controllingRider = this.getControllingPassenger();
			if (controllingRider instanceof EntityPlayer && this.ticksExisted % 10 == 3) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)controllingRider, ItemNinjutsu.block);
				boolean flag = stack != null && ((ItemNinjutsu.RangedItem)stack.getItem())
				 .canActivateJutsu(stack, ItemNinjutsu.PUPPET, (EntityPlayer)controllingRider) == EnumActionResult.SUCCESS;
				this.setOwnerCanSteer(flag, this.driveSpeed);
			}
			this.setAge(age + 1);
		}

		@Override
		public void applyEntityCollision(Entity entityIn) {
//System.out.println(">>> speed:"+this.getPositionVector().subtract(this.prevPosX, this.prevPosY, this.prevPosZ).lengthVector());
			if (!this.isRidingSameEntity(entityIn) && !entityIn.noClip && !this.noClip) {
                double d0 = entityIn.posX - this.posX;
                double d1 = entityIn.posZ - this.posZ;
                double d2 = MathHelper.absMax(d0, d1);
                if (d2 >= 0.01D) {
                    d2 = (double)MathHelper.sqrt(d2);
                    d0 = d0 / d2;
                    d1 = d1 / d2;
                    double d3 = 1.0D / d2;
                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }
                    d0 = d0 * d3;
                    d1 = d1 * d3;
                    d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                    d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
                    if (!this.isBeingRidden()) {
                        this.addVelocity(-d0 * 0.05d, 0.0D, -d1 * 0.05d);
                    }
                    if (!entityIn.isBeingRidden()) {
	                    double d4 = this.getPositionVector().subtract(this.prevPosX, this.prevPosY, this.prevPosZ).lengthVector();
	                    if (d4 < 0.05d) {
	                    	d4 = 0.05d;
	                    }
                        entityIn.addVelocity(d0 * d4, d4 * 0.5d, d1 * d4);
                        if (d4 >= 0.1d) {
                        	entityIn.attackEntityFrom(DamageSource.FLY_INTO_WALL, (float)d4 * 10.0f);
                        }
                    }
                }
			}
		}

		/*@Override
		public void setDead() {
			if (!this.world.isRemote) {
				ProcedureUtils.poofWithSmoke(this);
			}
			super.setDead();
		}

		@Override
		protected void onDeathUpdate() {
			this.setDead();
		}*/
	}

	// Made with Blockbench 4.3.1
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelSanShouo extends ModelBase {
		private final ModelRenderer body;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r2;
		private final ModelRenderer cube_r3;
		private final ModelRenderer cube_r4;
		private final ModelRenderer cube_r5;
		private final ModelRenderer cube_r6;
		private final ModelRenderer cube_r7;
		private final ModelRenderer shield;
		private final ModelRenderer bone37;
		private final ModelRenderer bone41;
		private final ModelRenderer bone3;
		private final ModelRenderer bone34;
		private final ModelRenderer bone38;
		private final ModelRenderer bone39;
		private final ModelRenderer bone40;
		private final ModelRenderer bone35;
		private final ModelRenderer bodyFront;
		private final ModelRenderer cube_r8;
		private final ModelRenderer cube_r9;
		private final ModelRenderer cube_r10;
		private final ModelRenderer cube_r11;
		private final ModelRenderer cube_r12;
		private final ModelRenderer cube_r13;
		private final ModelRenderer cube_r14;
		private final ModelRenderer cube_r15;
		private final ModelRenderer head;
		private final ModelRenderer cube_r16;
		private final ModelRenderer cube_r17;
		private final ModelRenderer cube_r18;
		private final ModelRenderer cube_r19;
		private final ModelRenderer cube_r20;
		private final ModelRenderer cube_r21;
		private final ModelRenderer cube_r22;
		private final ModelRenderer cube_r23;
		private final ModelRenderer snout;
		private final ModelRenderer cube_r24;
		private final ModelRenderer cube_r25;
		private final ModelRenderer cube_r26;
		private final ModelRenderer cube_r27;
		private final ModelRenderer cube_r28;
		private final ModelRenderer cube_r29;
		private final ModelRenderer cube_r30;
		private final ModelRenderer cube_r31;
		private final ModelRenderer cube_r32;
		private final ModelRenderer cube_r33;
		private final ModelRenderer cube_r34;
		private final ModelRenderer cube_r35;
		private final ModelRenderer bone;
		private final ModelRenderer cube_r36;
		private final ModelRenderer cube_r37;
		private final ModelRenderer cube_r38;
		private final ModelRenderer cube_r39;
		private final ModelRenderer cube_r40;
		private final ModelRenderer bone79;
		private final ModelRenderer bone80;
		private final ModelRenderer bone81;
		private final ModelRenderer bone97;
		private final ModelRenderer bone98;
		private final ModelRenderer bone99;
		private final ModelRenderer bone100;
		private final ModelRenderer bone101;
		private final ModelRenderer bone82;
		private final ModelRenderer bone95;
		private final ModelRenderer bone96;
		private final ModelRenderer bone102;
		private final ModelRenderer bone103;
		private final ModelRenderer bone104;
		private final ModelRenderer bone105;
		private final ModelRenderer bone106;
		private final ModelRenderer bone107;
		private final ModelRenderer bone108;
		private final ModelRenderer bone83;
		private final ModelRenderer bone84;
		private final ModelRenderer bone85;
		private final ModelRenderer bone86;
		private final ModelRenderer bone109;
		private final ModelRenderer bone110;
		private final ModelRenderer bone111;
		private final ModelRenderer bone87;
		private final ModelRenderer bone88;
		private final ModelRenderer bone89;
		private final ModelRenderer bone90;
		private final ModelRenderer bone112;
		private final ModelRenderer bone113;
		private final ModelRenderer bone114;
		private final ModelRenderer bone91;
		private final ModelRenderer bone92;
		private final ModelRenderer bone93;
		private final ModelRenderer bone94;
		private final ModelRenderer bone115;
		private final ModelRenderer bone116;
		private final ModelRenderer bone117;
		private final ModelRenderer leg1;
		private final ModelRenderer bone36;
		private final ModelRenderer bone42;
		private final ModelRenderer bone4;
		private final ModelRenderer foot1;
		private final ModelRenderer bone5;
		private final ModelRenderer bone6;
		private final ModelRenderer bone8;
		private final ModelRenderer bone9;
		private final ModelRenderer bone2;
		private final ModelRenderer bone7;
		private final ModelRenderer leg2;
		private final ModelRenderer bone10;
		private final ModelRenderer bone43;
		private final ModelRenderer bone11;
		private final ModelRenderer foot2;
		private final ModelRenderer bone12;
		private final ModelRenderer bone13;
		private final ModelRenderer bone14;
		private final ModelRenderer bone15;
		private final ModelRenderer bone16;
		private final ModelRenderer bone17;
		private final ModelRenderer leg3;
		private final ModelRenderer bone18;
		private final ModelRenderer bone19;
		private final ModelRenderer foot3;
		private final ModelRenderer bone20;
		private final ModelRenderer bone21;
		private final ModelRenderer bone22;
		private final ModelRenderer bone23;
		private final ModelRenderer bone24;
		private final ModelRenderer bone25;
		private final ModelRenderer leg4;
		private final ModelRenderer bone26;
		private final ModelRenderer bone27;
		private final ModelRenderer foot4;
		private final ModelRenderer bone28;
		private final ModelRenderer bone29;
		private final ModelRenderer bone30;
		private final ModelRenderer bone31;
		private final ModelRenderer bone32;
		private final ModelRenderer bone33;
		private final ModelRenderer[] tail = new ModelRenderer[6];
		private final ModelRenderer cube_r41;
		private final ModelRenderer cube_r42;
		private final ModelRenderer cube_r43;
		private final ModelRenderer cube_r44;
		private final ModelRenderer cube_r45;
		private final ModelRenderer cube_r46;
		private final ModelRenderer cube_r47;
		private final ModelRenderer cube_r48;
		//private final ModelRenderer tail[1];
		private final ModelRenderer cube_r49;
		private final ModelRenderer cube_r50;
		private final ModelRenderer cube_r51;
		private final ModelRenderer cube_r52;
		private final ModelRenderer cube_r53;
		private final ModelRenderer cube_r54;
		private final ModelRenderer cube_r55;
		//private final ModelRenderer tail[2];
		private final ModelRenderer cube_r56;
		private final ModelRenderer cube_r57;
		private final ModelRenderer cube_r58;
		private final ModelRenderer cube_r59;
		private final ModelRenderer cube_r60;
		private final ModelRenderer cube_r61;
		private final ModelRenderer cube_r62;
		private final ModelRenderer cube_r63;
		//private final ModelRenderer tail[3];
		private final ModelRenderer cube_r64;
		private final ModelRenderer cube_r65;
		private final ModelRenderer cube_r66;
		private final ModelRenderer cube_r67;
		private final ModelRenderer cube_r68;
		private final ModelRenderer cube_r69;
		private final ModelRenderer cube_r70;
		private final ModelRenderer cube_r71;
		//private final ModelRenderer tail[4];
		private final ModelRenderer cube_r72;
		private final ModelRenderer cube_r73;
		private final ModelRenderer cube_r74;
		private final ModelRenderer cube_r75;
		private final ModelRenderer cube_r76;
		private final ModelRenderer cube_r77;
		private final ModelRenderer cube_r78;
		private final ModelRenderer cube_r79;
		//private final ModelRenderer tail[5];
		private final ModelRenderer cube_r80;
		private final ModelRenderer cube_r81;
		private final ModelRenderer cube_r82;
		private final ModelRenderer cube_r83;
		private final ModelRenderer cube_r84;
		private final ModelRenderer cube_r85;
		private final ModelRenderer cube_r86;
		private final ModelRenderer cube_r87;
		public ModelSanShouo() {
			textureWidth = 128;
			textureHeight = 128;
			body = new ModelRenderer(this);
			body.setRotationPoint(0.0F, 17.25F, 0.0F);
			setRotationAngle(body, 1.5708F, 0.0F, 0.0F);
			body.cubeList.add(new ModelBox(body, 28, 100, -2.0F, -6.0F, -5.0F, 4, 12, 0, 0.12F, false));
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(0.0F, 5.5F, 0.0F);
			body.addChild(cube_r1);
			setRotationAngle(cube_r1, 3.1416F, 0.7854F, 3.1416F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 116, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(0.0F, 5.5F, 0.0F);
			body.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.0F, -1.5708F, 0.0F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 20, 101, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.0F, 5.5F, 0.0F);
			body.addChild(cube_r3);
			setRotationAngle(cube_r3, 3.1416F, -0.7854F, 3.1416F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 8, 116, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(0.0F, 5.5F, 0.0F);
			body.addChild(cube_r4);
			setRotationAngle(cube_r4, 3.1416F, 0.0F, 3.1416F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 16, 116, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(0.0F, 5.5F, 0.0F);
			body.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.0F, -0.7854F, 0.0F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 24, 76, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(0.0F, 5.5F, 0.0F);
			body.addChild(cube_r6);
			setRotationAngle(cube_r6, 0.0F, 1.5708F, 0.0F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 24, 113, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(0.0F, 5.5F, 0.0F);
			body.addChild(cube_r7);
			setRotationAngle(cube_r7, 0.0F, 0.7854F, 0.0F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 28, 88, -2.0F, -11.5F, -5.0F, 4, 12, 0, 0.12F, false));
			shield = new ModelRenderer(this);
			shield.setRotationPoint(0.0F, -8.5F, 2.0F);
			body.addChild(shield);
			setRotationAngle(shield, -0.2618F, 0.0F, 0.0F);
			shield.cubeList.add(new ModelBox(shield, 0, 0, -5.0F, -0.5F, 0.0F, 10, 1, 14, 0.0F, false));
			bone37 = new ModelRenderer(this);
			bone37.setRotationPoint(5.0F, -0.5F, 14.0F);
			shield.addChild(bone37);
			setRotationAngle(bone37, -0.1309F, 0.1745F, 0.0F);
			bone41 = new ModelRenderer(this);
			bone41.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone37.addChild(bone41);
			setRotationAngle(bone41, 0.0F, 0.0F, 0.7854F);
			bone41.cubeList.add(new ModelBox(bone41, 0, 15, 0.0F, 0.0F, -14.0F, 4, 1, 14, 0.0F, true));
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(4.0F, 0.0F, 0.0F);
			bone41.addChild(bone3);
			setRotationAngle(bone3, 0.0F, 0.2618F, 0.2618F);
			bone3.cubeList.add(new ModelBox(bone3, 0, 15, 0.0F, 0.0F, -14.0F, 4, 1, 14, 0.0F, true));
			bone34 = new ModelRenderer(this);
			bone34.setRotationPoint(-5.0F, -0.5F, 14.0F);
			shield.addChild(bone34);
			setRotationAngle(bone34, -0.1309F, -0.1745F, 0.0F);
			bone38 = new ModelRenderer(this);
			bone38.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone34.addChild(bone38);
			setRotationAngle(bone38, 0.0F, 0.0F, -0.7854F);
			bone38.cubeList.add(new ModelBox(bone38, 0, 15, -4.0F, 0.0F, -14.0F, 4, 1, 14, 0.0F, false));
			bone39 = new ModelRenderer(this);
			bone39.setRotationPoint(-4.0F, 0.0F, 0.0F);
			bone38.addChild(bone39);
			setRotationAngle(bone39, 0.0F, -0.2618F, -0.2618F);
			bone39.cubeList.add(new ModelBox(bone39, 0, 15, -4.0F, 0.0F, -14.0F, 4, 1, 14, 0.0F, false));
			bone40 = new ModelRenderer(this);
			bone40.setRotationPoint(-5.0F, 0.0F, 14.0F);
			shield.addChild(bone40);
			setRotationAngle(bone40, 0.0F, -0.4363F, 0.0F);
			bone40.cubeList.add(new ModelBox(bone40, 34, 10, -0.0055F, -0.5483F, -2.0117F, 4, 1, 2, 0.0F, false));
			bone35 = new ModelRenderer(this);
			bone35.setRotationPoint(5.0F, 0.0F, 14.0F);
			shield.addChild(bone35);
			setRotationAngle(bone35, 0.0F, 0.4363F, 0.0F);
			bone35.cubeList.add(new ModelBox(bone35, 34, 10, -3.9945F, -0.5483F, -2.0117F, 4, 1, 2, 0.0F, true));
			bodyFront = new ModelRenderer(this);
			bodyFront.setRotationPoint(0.0F, -5.25F, 0.0F);
			body.addChild(bodyFront);
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r8);
			setRotationAngle(cube_r8, 3.098F, 0.7854F, 3.1416F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 54, 45, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r9);
			setRotationAngle(cube_r9, -0.0436F, -1.5708F, 0.0F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 54, 54, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r10);
			setRotationAngle(cube_r10, 3.098F, -0.7854F, 3.1416F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 56, 9, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r11 = new ModelRenderer(this);
			cube_r11.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r11);
			setRotationAngle(cube_r11, 3.098F, 0.0F, 3.1416F);
			cube_r11.cubeList.add(new ModelBox(cube_r11, 56, 18, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r12 = new ModelRenderer(this);
			cube_r12.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r12);
			setRotationAngle(cube_r12, -0.0436F, -0.7854F, 0.0F);
			cube_r12.cubeList.add(new ModelBox(cube_r12, 56, 27, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r13 = new ModelRenderer(this);
			cube_r13.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r13);
			setRotationAngle(cube_r13, -0.0436F, 1.5708F, 0.0F);
			cube_r13.cubeList.add(new ModelBox(cube_r13, 40, 58, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r14 = new ModelRenderer(this);
			cube_r14.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r14);
			setRotationAngle(cube_r14, -0.0436F, 0.7854F, 0.0F);
			cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 59, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r15 = new ModelRenderer(this);
			cube_r15.setRotationPoint(0.0F, 0.75F, 0.0F);
			bodyFront.addChild(cube_r15);
			setRotationAngle(cube_r15, -0.0436F, 0.0F, 0.0F);
			cube_r15.cubeList.add(new ModelBox(cube_r15, 10, 59, -2.5F, -8.0F, -5.0F, 5, 9, 0, 0.12F, false));
			head = new ModelRenderer(this);
			head.setRotationPoint(0.0F, -6.5F, 0.0F);
			bodyFront.addChild(head);
			cube_r16 = new ModelRenderer(this);
			cube_r16.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r16);
			setRotationAngle(cube_r16, 2.9671F, -0.7854F, 3.1416F);
			cube_r16.cubeList.add(new ModelBox(cube_r16, 66, 64, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
			cube_r17 = new ModelRenderer(this);
			cube_r17.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r17);
			setRotationAngle(cube_r17, -0.1745F, -1.5708F, 0.0F);
			cube_r17.cubeList.add(new ModelBox(cube_r17, 36, 67, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
			cube_r18 = new ModelRenderer(this);
			cube_r18.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r18);
			setRotationAngle(cube_r18, 2.9671F, 0.7854F, 3.1416F);
			cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 68, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
			cube_r19 = new ModelRenderer(this);
			cube_r19.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r19);
			setRotationAngle(cube_r19, 2.9671F, 0.0F, 3.1416F);
			cube_r19.cubeList.add(new ModelBox(cube_r19, 10, 68, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
			cube_r20 = new ModelRenderer(this);
			cube_r20.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r20);
			setRotationAngle(cube_r20, -0.1745F, 0.7854F, 0.0F);
			cube_r20.cubeList.add(new ModelBox(cube_r20, 20, 69, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
			cube_r21 = new ModelRenderer(this);
			cube_r21.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r21);
			setRotationAngle(cube_r21, -0.1745F, 1.5708F, 0.0F);
			cube_r21.cubeList.add(new ModelBox(cube_r21, 70, 0, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
			cube_r22 = new ModelRenderer(this);
			cube_r22.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r22);
			setRotationAngle(cube_r22, -0.1745F, -0.7854F, 0.0F);
			cube_r22.cubeList.add(new ModelBox(cube_r22, 70, 32, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
			cube_r23 = new ModelRenderer(this);
			cube_r23.setRotationPoint(0.0F, 0.0F, 0.0F);
			head.addChild(cube_r23);
			setRotationAngle(cube_r23, -0.1745F, 0.0F, 0.0F);
			cube_r23.cubeList.add(new ModelBox(cube_r23, 70, 38, -2.5F, -4.0F, -4.75F, 5, 6, 0, 0.0F, false));
			snout = new ModelRenderer(this);
			snout.setRotationPoint(0.0F, -5.25F, 0.0F);
			head.addChild(snout);
			cube_r24 = new ModelRenderer(this);
			cube_r24.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r24);
			setRotationAngle(cube_r24, 2.5831F, 1.1781F, -3.1416F);
			cube_r24.cubeList.add(new ModelBox(cube_r24, 46, 24, -0.5F, -2.2F, -4.03F, 1, 4, 0, 0.0F, false));
			cube_r25 = new ModelRenderer(this);
			cube_r25.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r25);
			setRotationAngle(cube_r25, 2.5831F, -1.1781F, -3.1416F);
			cube_r25.cubeList.add(new ModelBox(cube_r25, 46, 24, -0.5F, -2.2F, -4.03F, 1, 4, 0, 0.0F, false));
			cube_r26 = new ModelRenderer(this);
			cube_r26.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r26);
			setRotationAngle(cube_r26, 2.5831F, 0.3927F, 3.1416F);
			cube_r26.cubeList.add(new ModelBox(cube_r26, 46, 76, -0.5F, -2.2F, -4.03F, 1, 4, 0, 0.0F, true));
			cube_r27 = new ModelRenderer(this);
			cube_r27.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r27);
			setRotationAngle(cube_r27, 2.5831F, -0.3927F, -3.1416F);
			cube_r27.cubeList.add(new ModelBox(cube_r27, 46, 76, -0.5F, -2.2F, -4.03F, 1, 4, 0, 0.0F, false));
			cube_r28 = new ModelRenderer(this);
			cube_r28.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r28);
			setRotationAngle(cube_r28, -0.5236F, 1.5708F, 0.0F);
			cube_r28.cubeList.add(new ModelBox(cube_r28, 32, 25, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
			cube_r29 = new ModelRenderer(this);
			cube_r29.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r29);
			setRotationAngle(cube_r29, -0.4363F, 0.7854F, 0.0F);
			cube_r29.cubeList.add(new ModelBox(cube_r29, 66, 20, -2.0F, -2.7F, -3.85F, 4, 4, 0, 0.15F, false));
			cube_r30 = new ModelRenderer(this);
			cube_r30.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r30);
			setRotationAngle(cube_r30, 2.618F, 0.7854F, 3.1416F);
			cube_r30.cubeList.add(new ModelBox(cube_r30, 66, 70, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
			cube_r31 = new ModelRenderer(this);
			cube_r31.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r31);
			setRotationAngle(cube_r31, 2.618F, 0.0F, 3.1416F);
			cube_r31.cubeList.add(new ModelBox(cube_r31, 46, 71, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
			cube_r32 = new ModelRenderer(this);
			cube_r32.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r32);
			setRotationAngle(cube_r32, -0.5236F, -1.5708F, 0.0F);
			cube_r32.cubeList.add(new ModelBox(cube_r32, 54, 71, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
			cube_r33 = new ModelRenderer(this);
			cube_r33.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r33);
			setRotationAngle(cube_r33, 2.618F, -0.7854F, 3.1416F);
			cube_r33.cubeList.add(new ModelBox(cube_r33, 44, 0, -2.0F, -2.45F, -3.85F, 4, 4, 0, 0.15F, false));
			cube_r34 = new ModelRenderer(this);
			cube_r34.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r34);
			setRotationAngle(cube_r34, -0.4363F, -0.7854F, 0.0F);
			cube_r34.cubeList.add(new ModelBox(cube_r34, 66, 28, -2.0F, -2.7F, -3.85F, 4, 4, 0, 0.15F, false));
			cube_r35 = new ModelRenderer(this);
			cube_r35.setRotationPoint(0.0F, 2.0F, 0.0F);
			snout.addChild(cube_r35);
			setRotationAngle(cube_r35, -0.4363F, 0.0F, 0.0F);
			cube_r35.cubeList.add(new ModelBox(cube_r35, 82, 4, -2.0F, -2.7F, -3.85F, 4, 4, 0, 0.15F, false));
			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.0F, -2.015F, 2.1F);
			snout.addChild(bone);
			setRotationAngle(bone, -0.2618F, 0.0F, 0.0F);
			cube_r36 = new ModelRenderer(this);
			cube_r36.setRotationPoint(-1.591F, -0.067F, -0.709F);
			bone.addChild(cube_r36);
			setRotationAngle(cube_r36, -0.8727F, -0.9163F, 0.0F);
			cube_r36.cubeList.add(new ModelBox(cube_r36, 0, 22, -0.7172F, 0.0333F, 0.1148F, 2, 0, 1, 0.0F, true));
			cube_r37 = new ModelRenderer(this);
			cube_r37.setRotationPoint(1.591F, -0.067F, -0.509F);
			bone.addChild(cube_r37);
			setRotationAngle(cube_r37, -0.8727F, 0.9163F, 0.0F);
			cube_r37.cubeList.add(new ModelBox(cube_r37, -1, 22, -1.1414F, 0.1275F, 0.0037F, 2, 0, 1, 0.0F, false));
			cube_r38 = new ModelRenderer(this);
			cube_r38.setRotationPoint(2.5056F, 0.2094F, 0.4F);
			bone.addChild(cube_r38);
			setRotationAngle(cube_r38, 0.0436F, 0.0F, 1.0472F);
			cube_r38.cubeList.add(new ModelBox(cube_r38, 44, 4, -0.4F, 0.25F, -3.5F, 1, 0, 2, 0.0F, false));
			cube_r39 = new ModelRenderer(this);
			cube_r39.setRotationPoint(-2.5056F, 0.2094F, 0.4F);
			bone.addChild(cube_r39);
			setRotationAngle(cube_r39, 0.0436F, 0.0F, -1.0472F);
			cube_r39.cubeList.add(new ModelBox(cube_r39, 44, 4, -0.6F, 0.25F, -3.5F, 1, 0, 2, 0.0F, true));
			cube_r40 = new ModelRenderer(this);
			cube_r40.setRotationPoint(0.0F, -1.085F, -2.1F);
			bone.addChild(cube_r40);
			setRotationAngle(cube_r40, 0.0F, 0.0F, 0.0F);
			cube_r40.cubeList.add(new ModelBox(cube_r40, 17, 15, -2.5F, 1.085F, -2.5F, 5, 0, 5, 0.0F, false));
			bone79 = new ModelRenderer(this);
			bone79.setRotationPoint(0.0F, -6.0F, -2.35F);
			snout.addChild(bone79);
			bone80 = new ModelRenderer(this);
			bone80.setRotationPoint(0.0F, 0.0F, -0.65F);
			bone79.addChild(bone80);
			bone81 = new ModelRenderer(this);
			bone81.setRotationPoint(-3.65F, 11.0F, -0.75F);
			bone80.addChild(bone81);
			setRotationAngle(bone81, -0.0873F, 0.1309F, 0.0F);
			bone81.cubeList.add(new ModelBox(bone81, 12, 90, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone97 = new ModelRenderer(this);
			bone97.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone81.addChild(bone97);
			setRotationAngle(bone97, 0.0F, 0.0F, 0.3054F);
			bone97.cubeList.add(new ModelBox(bone97, 0, 86, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone98 = new ModelRenderer(this);
			bone98.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone97.addChild(bone98);
			setRotationAngle(bone98, 0.0F, 0.0F, 0.3054F);
			bone98.cubeList.add(new ModelBox(bone98, 0, 95, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone99 = new ModelRenderer(this);
			bone99.setRotationPoint(3.65F, 11.0F, -0.75F);
			bone80.addChild(bone99);
			setRotationAngle(bone99, -0.0873F, -0.1309F, 0.0F);
			bone99.cubeList.add(new ModelBox(bone99, 12, 90, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone100 = new ModelRenderer(this);
			bone100.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone99.addChild(bone100);
			setRotationAngle(bone100, 0.0F, 0.0F, -0.3054F);
			bone100.cubeList.add(new ModelBox(bone100, 0, 86, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone101 = new ModelRenderer(this);
			bone101.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone100.addChild(bone101);
			setRotationAngle(bone101, 0.0F, 0.0F, -0.3054F);
			bone101.cubeList.add(new ModelBox(bone101, 0, 95, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone82 = new ModelRenderer(this);
			bone82.setRotationPoint(0.0F, 12.0F, -1.4F);
			bone80.addChild(bone82);
			setRotationAngle(bone82, -0.0436F, 0.0F, 0.0F);
			bone82.cubeList.add(new ModelBox(bone82, 0, 75, -2.5F, -4.0F, -0.5F, 5, 4, 1, 0.0F, false));
			bone95 = new ModelRenderer(this);
			bone95.setRotationPoint(-0.5F, -4.0F, 0.0F);
			bone82.addChild(bone95);
			setRotationAngle(bone95, -0.0436F, 0.0F, 0.0F);
			bone95.cubeList.add(new ModelBox(bone95, 0, 86, -1.5F, -3.0F, -0.5F, 4, 3, 1, 0.0F, false));
			bone96 = new ModelRenderer(this);
			bone96.setRotationPoint(-1.0F, -3.0F, 0.0F);
			bone95.addChild(bone96);
			setRotationAngle(bone96, -0.0873F, 0.0F, 0.0F);
			bone96.cubeList.add(new ModelBox(bone96, 1, 87, 0.5F, -3.0F, -0.5F, 2, 3, 1, 0.0F, false));
			bone102 = new ModelRenderer(this);
			bone102.setRotationPoint(0.0F, 0.0F, -0.45F);
			bone79.addChild(bone102);
			setRotationAngle(bone102, 0.0873F, 0.0F, 0.0F);
			bone103 = new ModelRenderer(this);
			bone103.setRotationPoint(-4.05F, 11.0F, -0.75F);
			bone102.addChild(bone103);
			setRotationAngle(bone103, -0.0873F, 0.1309F, 0.0F);
			bone103.cubeList.add(new ModelBox(bone103, 12, 90, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone104 = new ModelRenderer(this);
			bone104.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone103.addChild(bone104);
			setRotationAngle(bone104, 0.0F, 0.0F, 0.3054F);
			bone104.cubeList.add(new ModelBox(bone104, 0, 21, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone105 = new ModelRenderer(this);
			bone105.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone104.addChild(bone105);
			setRotationAngle(bone105, 0.0F, 0.0F, 0.3054F);
			bone105.cubeList.add(new ModelBox(bone105, 5, 95, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone106 = new ModelRenderer(this);
			bone106.setRotationPoint(4.05F, 11.0F, -0.75F);
			bone102.addChild(bone106);
			setRotationAngle(bone106, -0.0873F, -0.1309F, 0.0F);
			bone106.cubeList.add(new ModelBox(bone106, 12, 90, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone107 = new ModelRenderer(this);
			bone107.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone106.addChild(bone107);
			setRotationAngle(bone107, 0.0F, 0.0F, -0.3054F);
			bone107.cubeList.add(new ModelBox(bone107, 0, 19, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone108 = new ModelRenderer(this);
			bone108.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone107.addChild(bone108);
			setRotationAngle(bone108, 0.0F, 0.0F, -0.3054F);
			bone108.cubeList.add(new ModelBox(bone108, 5, 95, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone83 = new ModelRenderer(this);
			bone83.setRotationPoint(0.0F, 0.0F, -0.5F);
			bone79.addChild(bone83);
			setRotationAngle(bone83, 0.2182F, 0.0F, 0.0F);
			bone84 = new ModelRenderer(this);
			bone84.setRotationPoint(-4.0F, 11.0F, -0.75F);
			bone83.addChild(bone84);
			setRotationAngle(bone84, 0.0F, -0.1309F, 0.0F);
			bone84.cubeList.add(new ModelBox(bone84, 6, 90, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone85 = new ModelRenderer(this);
			bone85.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone84.addChild(bone85);
			setRotationAngle(bone85, 0.0F, 0.0F, 0.3054F);
			bone85.cubeList.add(new ModelBox(bone85, 0, 90, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone86 = new ModelRenderer(this);
			bone86.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone85.addChild(bone86);
			setRotationAngle(bone86, 0.0F, 0.0F, 0.3054F);
			bone86.cubeList.add(new ModelBox(bone86, 12, 85, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone109 = new ModelRenderer(this);
			bone109.setRotationPoint(4.0F, 11.0F, -0.75F);
			bone83.addChild(bone109);
			setRotationAngle(bone109, 0.0F, 0.1309F, 0.0F);
			bone109.cubeList.add(new ModelBox(bone109, 6, 90, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone110 = new ModelRenderer(this);
			bone110.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone109.addChild(bone110);
			setRotationAngle(bone110, 0.0F, 0.0F, -0.3054F);
			bone110.cubeList.add(new ModelBox(bone110, 0, 90, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone111 = new ModelRenderer(this);
			bone111.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone110.addChild(bone111);
			setRotationAngle(bone111, 0.0F, 0.0F, -0.3054F);
			bone111.cubeList.add(new ModelBox(bone111, 12, 85, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone87 = new ModelRenderer(this);
			bone87.setRotationPoint(0.0F, 0.0F, -0.3F);
			bone79.addChild(bone87);
			setRotationAngle(bone87, 0.3491F, 0.0F, 0.0F);
			bone88 = new ModelRenderer(this);
			bone88.setRotationPoint(-4.0F, 11.0F, -0.75F);
			bone87.addChild(bone88);
			setRotationAngle(bone88, 0.0F, -0.1309F, 0.0F);
			bone88.cubeList.add(new ModelBox(bone88, 12, 80, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone89 = new ModelRenderer(this);
			bone89.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone88.addChild(bone89);
			setRotationAngle(bone89, 0.0F, 0.0F, 0.3054F);
			bone89.cubeList.add(new ModelBox(bone89, 12, 75, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone90 = new ModelRenderer(this);
			bone90.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone89.addChild(bone90);
			setRotationAngle(bone90, 0.0F, 0.0F, 0.3054F);
			bone90.cubeList.add(new ModelBox(bone90, 6, 85, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone112 = new ModelRenderer(this);
			bone112.setRotationPoint(4.0F, 11.0F, -0.75F);
			bone87.addChild(bone112);
			setRotationAngle(bone112, 0.0F, 0.1309F, 0.0F);
			bone112.cubeList.add(new ModelBox(bone112, 12, 80, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone113 = new ModelRenderer(this);
			bone113.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone112.addChild(bone113);
			setRotationAngle(bone113, 0.0F, 0.0F, -0.3054F);
			bone113.cubeList.add(new ModelBox(bone113, 12, 75, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone114 = new ModelRenderer(this);
			bone114.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone113.addChild(bone114);
			setRotationAngle(bone114, 0.0F, 0.0F, -0.3054F);
			bone114.cubeList.add(new ModelBox(bone114, 6, 85, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone91 = new ModelRenderer(this);
			bone91.setRotationPoint(0.0F, 0.0F, -0.1F);
			bone79.addChild(bone91);
			setRotationAngle(bone91, 0.48F, 0.0F, 0.0F);
			bone91.cubeList.add(new ModelBox(bone91, 12, 95, -1.5F, 2.5F, 0.15F, 3, 2, 0, 0.0F, false));
			bone92 = new ModelRenderer(this);
			bone92.setRotationPoint(-4.0F, 11.0F, -0.75F);
			bone91.addChild(bone92);
			setRotationAngle(bone92, 0.0F, -0.1309F, 0.0F);
			bone92.cubeList.add(new ModelBox(bone92, 0, 85, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone93 = new ModelRenderer(this);
			bone93.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone92.addChild(bone93);
			setRotationAngle(bone93, 0.0F, 0.0F, 0.3054F);
			bone93.cubeList.add(new ModelBox(bone93, 6, 80, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone94 = new ModelRenderer(this);
			bone94.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone93.addChild(bone94);
			setRotationAngle(bone94, 0.0F, 0.0F, 0.3054F);
			bone94.cubeList.add(new ModelBox(bone94, 0, 80, 0.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, false));
			bone115 = new ModelRenderer(this);
			bone115.setRotationPoint(4.0F, 11.0F, -0.75F);
			bone91.addChild(bone115);
			setRotationAngle(bone115, 0.0F, 0.1309F, 0.0F);
			bone115.cubeList.add(new ModelBox(bone115, 0, 85, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone116 = new ModelRenderer(this);
			bone116.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone115.addChild(bone116);
			setRotationAngle(bone116, 0.0F, 0.0F, -0.3054F);
			bone116.cubeList.add(new ModelBox(bone116, 6, 80, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			bone117 = new ModelRenderer(this);
			bone117.setRotationPoint(0.0F, -4.0F, 0.0F);
			bone116.addChild(bone117);
			setRotationAngle(bone117, 0.0F, 0.0F, -0.3054F);
			bone117.cubeList.add(new ModelBox(bone117, 0, 80, -2.0F, -4.0F, -0.5F, 2, 4, 1, 0.0F, true));
			leg1 = new ModelRenderer(this);
			leg1.setRotationPoint(-3.0F, -5.0F, -3.0F);
			body.addChild(leg1);
			bone36 = new ModelRenderer(this);
			bone36.setRotationPoint(0.0F, 0.0F, 0.0F);
			leg1.addChild(bone36);
			setRotationAngle(bone36, -0.5236F, 0.0F, -1.0472F);
			bone36.cubeList.add(new ModelBox(bone36, 0, 15, -1.0F, -9.0F, -1.0F, 2, 10, 2, 0.0F, false));
			bone42 = new ModelRenderer(this);
			bone42.setRotationPoint(0.0F, -10.0F, 0.0F);
			bone36.addChild(bone42);
			bone42.cubeList.add(new ModelBox(bone42, 85, 24, -1.0F, -0.8F, -1.1F, 2, 2, 2, 0.25F, false));
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(0.0F, -10.0F, -2.0F);
			bone36.addChild(bone4);
			setRotationAngle(bone4, 1.309F, 0.0F, 0.0F);
			bone4.cubeList.add(new ModelBox(bone4, 0, 0, -1.0F, -9.0F, -1.0F, 2, 10, 2, 0.0F, false));
			foot1 = new ModelRenderer(this);
			foot1.setRotationPoint(0.0F, -9.0F, 0.0F);
			bone4.addChild(foot1);
			setRotationAngle(foot1, 1.0472F, 0.6981F, 0.6981F);
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(0.0F, 0.0F, 1.0F);
			foot1.addChild(bone5);
			bone5.cubeList.add(new ModelBox(bone5, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, false));
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone5.addChild(bone6);
			setRotationAngle(bone6, 0.2618F, 0.0F, 0.0F);
			bone6.cubeList.add(new ModelBox(bone6, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, false));
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(0.75F, 0.0F, 1.0F);
			foot1.addChild(bone8);
			setRotationAngle(bone8, 0.0F, 0.0873F, 0.0F);
			bone8.cubeList.add(new ModelBox(bone8, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, false));
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone8.addChild(bone9);
			setRotationAngle(bone9, 0.2618F, 0.0F, 0.0F);
			bone9.cubeList.add(new ModelBox(bone9, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, false));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(-0.75F, 0.0F, 1.0F);
			foot1.addChild(bone2);
			setRotationAngle(bone2, 0.0F, -0.0873F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, false));
			bone7 = new ModelRenderer(this);
			bone7.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone2.addChild(bone7);
			setRotationAngle(bone7, 0.2618F, 0.0F, 0.0F);
			bone7.cubeList.add(new ModelBox(bone7, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, false));
			leg2 = new ModelRenderer(this);
			leg2.setRotationPoint(3.0F, -5.0F, -3.0F);
			body.addChild(leg2);
			bone10 = new ModelRenderer(this);
			bone10.setRotationPoint(0.0F, 0.0F, 0.0F);
			leg2.addChild(bone10);
			setRotationAngle(bone10, -0.5236F, 0.0F, 1.0472F);
			bone10.cubeList.add(new ModelBox(bone10, 0, 15, -1.0F, -9.0F, -1.0F, 2, 10, 2, 0.0F, true));
			bone43 = new ModelRenderer(this);
			bone43.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone10.addChild(bone43);
			bone43.cubeList.add(new ModelBox(bone43, 85, 24, -1.0F, -11.0F, -1.0F, 2, 2, 2, 0.25F, false));
			bone11 = new ModelRenderer(this);
			bone11.setRotationPoint(0.0F, -10.0F, -2.0F);
			bone10.addChild(bone11);
			setRotationAngle(bone11, 1.309F, 0.0F, 0.0F);
			bone11.cubeList.add(new ModelBox(bone11, 0, 0, -1.0F, -9.0F, -0.8F, 2, 10, 2, 0.0F, true));
			foot2 = new ModelRenderer(this);
			foot2.setRotationPoint(0.0F, -9.0F, 0.0F);
			bone11.addChild(foot2);
			setRotationAngle(foot2, 1.0472F, -0.6981F, -0.6981F);
			bone12 = new ModelRenderer(this);
			bone12.setRotationPoint(0.0F, 0.0F, 1.0F);
			foot2.addChild(bone12);
			bone12.cubeList.add(new ModelBox(bone12, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, true));
			bone13 = new ModelRenderer(this);
			bone13.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone12.addChild(bone13);
			setRotationAngle(bone13, 0.2618F, 0.0F, 0.0F);
			bone13.cubeList.add(new ModelBox(bone13, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, true));
			bone14 = new ModelRenderer(this);
			bone14.setRotationPoint(-0.75F, 0.0F, 1.0F);
			foot2.addChild(bone14);
			setRotationAngle(bone14, 0.0F, -0.0873F, 0.0F);
			bone14.cubeList.add(new ModelBox(bone14, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, true));
			bone15 = new ModelRenderer(this);
			bone15.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone14.addChild(bone15);
			setRotationAngle(bone15, 0.2618F, 0.0F, 0.0F);
			bone15.cubeList.add(new ModelBox(bone15, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, true));
			bone16 = new ModelRenderer(this);
			bone16.setRotationPoint(0.75F, 0.0F, 1.0F);
			foot2.addChild(bone16);
			setRotationAngle(bone16, 0.0F, 0.0873F, 0.0F);
			bone16.cubeList.add(new ModelBox(bone16, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, true));
			bone17 = new ModelRenderer(this);
			bone17.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone16.addChild(bone17);
			setRotationAngle(bone17, 0.2618F, 0.0F, 0.0F);
			bone17.cubeList.add(new ModelBox(bone17, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, true));
			leg3 = new ModelRenderer(this);
			leg3.setRotationPoint(-3.0F, 4.0F, -3.0F);
			body.addChild(leg3);
			bone18 = new ModelRenderer(this);
			bone18.setRotationPoint(0.0F, 0.0F, 0.0F);
			leg3.addChild(bone18);
			setRotationAngle(bone18, -0.5236F, 0.0F, -2.0944F);
			bone18.cubeList.add(new ModelBox(bone18, 0, 15, -1.0F, -9.0F, -1.0F, 2, 10, 2, 0.0F, false));
			bone18.cubeList.add(new ModelBox(bone18, 85, 24, -1.0F, -11.0F, -1.0F, 2, 2, 2, 0.25F, false));
			bone19 = new ModelRenderer(this);
			bone19.setRotationPoint(0.0F, -10.2F, -2.0F);
			bone18.addChild(bone19);
			setRotationAngle(bone19, 1.309F, 0.0F, 0.0F);
			bone19.cubeList.add(new ModelBox(bone19, 0, 0, -1.0F, -9.0F, -1.0F, 2, 10, 2, 0.0F, false));
			foot3 = new ModelRenderer(this);
			foot3.setRotationPoint(0.0F, -9.0F, 0.0F);
			bone19.addChild(foot3);
			setRotationAngle(foot3, 1.0472F, 0.6981F, 0.6981F);
			bone20 = new ModelRenderer(this);
			bone20.setRotationPoint(0.0F, 0.0F, 1.0F);
			foot3.addChild(bone20);
			bone20.cubeList.add(new ModelBox(bone20, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, false));
			bone21 = new ModelRenderer(this);
			bone21.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone20.addChild(bone21);
			setRotationAngle(bone21, 0.2618F, 0.0F, 0.0F);
			bone21.cubeList.add(new ModelBox(bone21, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, false));
			bone22 = new ModelRenderer(this);
			bone22.setRotationPoint(0.75F, 0.0F, 1.0F);
			foot3.addChild(bone22);
			setRotationAngle(bone22, 0.0F, 0.0873F, 0.0F);
			bone22.cubeList.add(new ModelBox(bone22, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, false));
			bone23 = new ModelRenderer(this);
			bone23.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone22.addChild(bone23);
			setRotationAngle(bone23, 0.2618F, 0.0F, 0.0F);
			bone23.cubeList.add(new ModelBox(bone23, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, false));
			bone24 = new ModelRenderer(this);
			bone24.setRotationPoint(-0.75F, 0.0F, 1.0F);
			foot3.addChild(bone24);
			setRotationAngle(bone24, 0.0F, -0.0873F, 0.0F);
			bone24.cubeList.add(new ModelBox(bone24, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, false));
			bone25 = new ModelRenderer(this);
			bone25.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone24.addChild(bone25);
			setRotationAngle(bone25, 0.2618F, 0.0F, 0.0F);
			bone25.cubeList.add(new ModelBox(bone25, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, false));
			leg4 = new ModelRenderer(this);
			leg4.setRotationPoint(3.0F, 4.0F, -3.0F);
			body.addChild(leg4);
			bone26 = new ModelRenderer(this);
			bone26.setRotationPoint(0.0F, 0.0F, 0.0F);
			leg4.addChild(bone26);
			setRotationAngle(bone26, -0.5236F, 0.0F, 2.0944F);
			bone26.cubeList.add(new ModelBox(bone26, 0, 15, -1.0F, -9.0F, -1.0F, 2, 10, 2, 0.0F, true));
			bone26.cubeList.add(new ModelBox(bone26, 85, 24, -1.0F, -11.0F, -1.0F, 2, 2, 2, 0.25F, false));
			bone27 = new ModelRenderer(this);
			bone27.setRotationPoint(0.0F, -10.2F, -2.0F);
			bone26.addChild(bone27);
			setRotationAngle(bone27, 1.309F, 0.0F, 0.0F);
			bone27.cubeList.add(new ModelBox(bone27, 0, 0, -1.0F, -9.0F, -1.0F, 2, 10, 2, 0.0F, true));
			foot4 = new ModelRenderer(this);
			foot4.setRotationPoint(0.0F, -9.0F, 0.0F);
			bone27.addChild(foot4);
			setRotationAngle(foot4, 1.0472F, -0.6981F, -0.6981F);
			bone28 = new ModelRenderer(this);
			bone28.setRotationPoint(0.0F, 0.0F, 1.0F);
			foot4.addChild(bone28);
			bone28.cubeList.add(new ModelBox(bone28, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, true));
			bone29 = new ModelRenderer(this);
			bone29.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone28.addChild(bone29);
			setRotationAngle(bone29, 0.2618F, 0.0F, 0.0F);
			bone29.cubeList.add(new ModelBox(bone29, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, true));
			bone30 = new ModelRenderer(this);
			bone30.setRotationPoint(-0.75F, 0.0F, 1.0F);
			foot4.addChild(bone30);
			setRotationAngle(bone30, 0.0F, -0.0873F, 0.0F);
			bone30.cubeList.add(new ModelBox(bone30, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, true));
			bone31 = new ModelRenderer(this);
			bone31.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone30.addChild(bone31);
			setRotationAngle(bone31, 0.2618F, 0.0F, 0.0F);
			bone31.cubeList.add(new ModelBox(bone31, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, true));
			bone32 = new ModelRenderer(this);
			bone32.setRotationPoint(0.75F, 0.0F, 1.0F);
			foot4.addChild(bone32);
			setRotationAngle(bone32, 0.0F, 0.0873F, 0.0F);
			bone32.cubeList.add(new ModelBox(bone32, 4, 8, -0.5F, -0.5F, -2.0F, 1, 1, 4, 0.0F, true));
			bone33 = new ModelRenderer(this);
			bone33.setRotationPoint(0.0F, 0.0F, 1.75F);
			bone32.addChild(bone33);
			setRotationAngle(bone33, 0.2618F, 0.0F, 0.0F);
			bone33.cubeList.add(new ModelBox(bone33, 8, 0, -0.5F, -0.5F, 0.0F, 1, 1, 2, -0.2F, true));
			tail[0] = new ModelRenderer(this);
			tail[0].setRotationPoint(0.0F, 5.5F, 0.0F);
			body.addChild(tail[0]);
			cube_r41 = new ModelRenderer(this);
			cube_r41.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r41);
			setRotationAngle(cube_r41, -3.098F, 0.7854F, -3.1416F);
			cube_r41.cubeList.add(new ModelBox(cube_r41, 22, 20, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r42 = new ModelRenderer(this);
			cube_r42.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r42);
			setRotationAngle(cube_r42, 0.0436F, -1.5708F, 0.0F);
			cube_r42.cubeList.add(new ModelBox(cube_r42, 44, 49, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r43 = new ModelRenderer(this);
			cube_r43.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r43);
			setRotationAngle(cube_r43, -3.098F, -0.7854F, -3.1416F);
			cube_r43.cubeList.add(new ModelBox(cube_r43, 0, 50, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r44 = new ModelRenderer(this);
			cube_r44.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r44);
			setRotationAngle(cube_r44, -3.098F, 0.0F, -3.1416F);
			cube_r44.cubeList.add(new ModelBox(cube_r44, 10, 50, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r45 = new ModelRenderer(this);
			cube_r45.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r45);
			setRotationAngle(cube_r45, 0.0436F, -0.7854F, 0.0F);
			cube_r45.cubeList.add(new ModelBox(cube_r45, 52, 0, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r46 = new ModelRenderer(this);
			cube_r46.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r46);
			setRotationAngle(cube_r46, 0.0436F, 1.5708F, 0.0F);
			cube_r46.cubeList.add(new ModelBox(cube_r46, 20, 52, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r47 = new ModelRenderer(this);
			cube_r47.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r47);
			setRotationAngle(cube_r47, 0.0436F, 0.7854F, 0.0F);
			cube_r47.cubeList.add(new ModelBox(cube_r47, 30, 52, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
			cube_r48 = new ModelRenderer(this);
			cube_r48.setRotationPoint(0.0F, -1.0F, 0.0F);
			tail[0].addChild(cube_r48);
			setRotationAngle(cube_r48, 0.0436F, 0.0F, 0.0F);
			cube_r48.cubeList.add(new ModelBox(cube_r48, 52, 36, -2.5F, -1.0F, -5.0F, 5, 9, 0, 0.12F, false));
			tail[1] = new ModelRenderer(this);
			tail[1].setRotationPoint(0.0F, 6.5F, 0.0F);
			tail[0].addChild(tail[1]);
			setRotationAngle(tail[1], -0.1309F, 0.0F, -0.2618F);
			tail[1].cubeList.add(new ModelBox(tail[1], 52, 36, -2.5F, -0.5F, -4.6F, 5, 9, 0, 0.12F, false));
			cube_r49 = new ModelRenderer(this);
			cube_r49.setRotationPoint(0.0F, 0.5F, 0.0F);
			tail[1].addChild(cube_r49);
			setRotationAngle(cube_r49, 3.1416F, 0.7854F, -3.1416F);
			cube_r49.cubeList.add(new ModelBox(cube_r49, 22, 20, -2.5F, -1.0F, -4.65F, 5, 9, 0, 0.12F, false));
			cube_r50 = new ModelRenderer(this);
			cube_r50.setRotationPoint(0.0F, 0.5F, 0.0F);
			tail[1].addChild(cube_r50);
			setRotationAngle(cube_r50, 0.0F, -1.5708F, 0.0F);
			cube_r50.cubeList.add(new ModelBox(cube_r50, 44, 49, -2.5F, -1.0F, -4.65F, 5, 9, 0, 0.12F, false));
			cube_r51 = new ModelRenderer(this);
			cube_r51.setRotationPoint(0.0F, 0.5F, 0.0F);
			tail[1].addChild(cube_r51);
			setRotationAngle(cube_r51, 3.1416F, -0.7854F, -3.1416F);
			cube_r51.cubeList.add(new ModelBox(cube_r51, 0, 50, -2.5F, -1.0F, -4.65F, 5, 9, 0, 0.12F, false));
			cube_r52 = new ModelRenderer(this);
			cube_r52.setRotationPoint(0.0F, 0.5F, 0.0F);
			tail[1].addChild(cube_r52);
			setRotationAngle(cube_r52, 3.1416F, 0.0F, -3.1416F);
			cube_r52.cubeList.add(new ModelBox(cube_r52, 10, 50, -2.5F, -1.0F, -4.65F, 5, 9, 0, 0.12F, false));
			cube_r53 = new ModelRenderer(this);
			cube_r53.setRotationPoint(0.0F, 0.5F, 0.0F);
			tail[1].addChild(cube_r53);
			setRotationAngle(cube_r53, 0.0F, -0.7854F, 0.0F);
			cube_r53.cubeList.add(new ModelBox(cube_r53, 52, 0, -2.5F, -1.0F, -4.65F, 5, 9, 0, 0.12F, false));
			cube_r54 = new ModelRenderer(this);
			cube_r54.setRotationPoint(0.0F, 0.5F, 0.0F);
			tail[1].addChild(cube_r54);
			setRotationAngle(cube_r54, 0.0F, 1.5708F, 0.0F);
			cube_r54.cubeList.add(new ModelBox(cube_r54, 20, 52, -2.5F, -1.0F, -4.65F, 5, 9, 0, 0.12F, false));
			cube_r55 = new ModelRenderer(this);
			cube_r55.setRotationPoint(0.0F, 0.5F, 0.0F);
			tail[1].addChild(cube_r55);
			setRotationAngle(cube_r55, 0.0F, 0.7854F, 0.0F);
			cube_r55.cubeList.add(new ModelBox(cube_r55, 30, 52, -2.5F, -1.0F, -4.65F, 5, 9, 0, 0.12F, false));
			tail[2] = new ModelRenderer(this);
			tail[2].setRotationPoint(0.0F, 8.25F, 0.0F);
			tail[1].addChild(tail[2]);
			setRotationAngle(tail[2], 0.2618F, 0.0F, -0.2618F);
			cube_r56 = new ModelRenderer(this);
			cube_r56.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[2].addChild(cube_r56);
			setRotationAngle(cube_r56, -3.0543F, -0.7854F, -3.1416F);
			cube_r56.cubeList.add(new ModelBox(cube_r56, 0, 30, -2.5F, -1.7462F, -4.7066F, 5, 10, 0, 0.0F, false));
			cube_r57 = new ModelRenderer(this);
			cube_r57.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[2].addChild(cube_r57);
			setRotationAngle(cube_r57, 0.0873F, -1.5708F, 0.0F);
			cube_r57.cubeList.add(new ModelBox(cube_r57, 10, 30, -2.5F, -1.7462F, -4.7066F, 5, 10, 0, 0.0F, false));
			cube_r58 = new ModelRenderer(this);
			cube_r58.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[2].addChild(cube_r58);
			setRotationAngle(cube_r58, -3.0543F, 0.7854F, -3.1416F);
			cube_r58.cubeList.add(new ModelBox(cube_r58, 20, 30, -2.5F, -1.7462F, -4.7066F, 5, 10, 0, 0.0F, false));
			cube_r59 = new ModelRenderer(this);
			cube_r59.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[2].addChild(cube_r59);
			setRotationAngle(cube_r59, -3.0543F, 0.0F, -3.1416F);
			cube_r59.cubeList.add(new ModelBox(cube_r59, 30, 30, -2.5F, -1.7462F, -4.7066F, 5, 10, 0, 0.0F, false));
			cube_r60 = new ModelRenderer(this);
			cube_r60.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[2].addChild(cube_r60);
			setRotationAngle(cube_r60, 0.0873F, 0.7854F, 0.0F);
			cube_r60.cubeList.add(new ModelBox(cube_r60, 32, 15, -2.5F, -1.7462F, -4.7066F, 5, 10, 0, 0.0F, false));
			cube_r61 = new ModelRenderer(this);
			cube_r61.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[2].addChild(cube_r61);
			setRotationAngle(cube_r61, 0.0873F, 1.5708F, 0.0F);
			cube_r61.cubeList.add(new ModelBox(cube_r61, 34, 0, -2.5F, -1.7462F, -4.7066F, 5, 10, 0, 0.0F, false));
			cube_r62 = new ModelRenderer(this);
			cube_r62.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[2].addChild(cube_r62);
			setRotationAngle(cube_r62, 0.0873F, -0.7854F, 0.0F);
			cube_r62.cubeList.add(new ModelBox(cube_r62, 0, 40, -2.5F, -1.7462F, -4.7066F, 5, 10, 0, 0.0F, false));
			cube_r63 = new ModelRenderer(this);
			cube_r63.setRotationPoint(0.0F, 0.25F, 0.0F);
			tail[2].addChild(cube_r63);
			setRotationAngle(cube_r63, 0.0873F, 0.0F, 0.0F);
			cube_r63.cubeList.add(new ModelBox(cube_r63, 10, 40, -2.5F, -1.7462F, -4.7066F, 5, 10, 0, 0.0F, false));
			tail[3] = new ModelRenderer(this);
			tail[3].setRotationPoint(0.0F, 8.5F, 0.5F);
			tail[2].addChild(tail[3]);
			setRotationAngle(tail[3], 0.2618F, 0.0F, -0.2618F);
			cube_r64 = new ModelRenderer(this);
			cube_r64.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[3].addChild(cube_r64);
			setRotationAngle(cube_r64, -3.0107F, 0.0F, -3.1416F);
			cube_r64.cubeList.add(new ModelBox(cube_r64, 62, 36, -2.0F, -1.5F, -3.85F, 4, 8, 0, 0.0F, false));
			cube_r65 = new ModelRenderer(this);
			cube_r65.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[3].addChild(cube_r65);
			setRotationAngle(cube_r65, -3.0107F, -0.7854F, 3.1416F);
			cube_r65.cubeList.add(new ModelBox(cube_r65, 62, 0, -2.0F, -1.5F, -3.85F, 4, 8, 0, 0.0F, false));
			cube_r66 = new ModelRenderer(this);
			cube_r66.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[3].addChild(cube_r66);
			setRotationAngle(cube_r66, -3.0107F, 0.7854F, 3.1416F);
			cube_r66.cubeList.add(new ModelBox(cube_r66, 28, 61, -2.0F, -1.5F, -3.85F, 4, 8, 0, 0.0F, false));
			cube_r67 = new ModelRenderer(this);
			cube_r67.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[3].addChild(cube_r67);
			setRotationAngle(cube_r67, 0.1309F, 1.5708F, 0.0F);
			cube_r67.cubeList.add(new ModelBox(cube_r67, 20, 61, -2.0F, -1.5F, -3.85F, 4, 8, 0, 0.0F, false));
			cube_r68 = new ModelRenderer(this);
			cube_r68.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[3].addChild(cube_r68);
			setRotationAngle(cube_r68, 0.1309F, -1.5708F, 0.0F);
			cube_r68.cubeList.add(new ModelBox(cube_r68, 58, 63, -2.0F, -1.5F, -3.85F, 4, 8, 0, 0.0F, false));
			cube_r69 = new ModelRenderer(this);
			cube_r69.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[3].addChild(cube_r69);
			setRotationAngle(cube_r69, 0.1309F, 0.7854F, 0.0F);
			cube_r69.cubeList.add(new ModelBox(cube_r69, 50, 63, -2.0F, -1.5F, -3.85F, 4, 8, 0, 0.0F, false));
			cube_r70 = new ModelRenderer(this);
			cube_r70.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[3].addChild(cube_r70);
			setRotationAngle(cube_r70, 0.1309F, -0.7854F, 0.0F);
			cube_r70.cubeList.add(new ModelBox(cube_r70, 64, 44, -2.0F, -1.5F, -3.85F, 4, 8, 0, 0.0F, false));
			cube_r71 = new ModelRenderer(this);
			cube_r71.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[3].addChild(cube_r71);
			setRotationAngle(cube_r71, 0.1309F, 0.0F, 0.0F);
			cube_r71.cubeList.add(new ModelBox(cube_r71, 66, 8, -2.0F, -1.5F, -3.85F, 4, 8, 0, 0.0F, false));
			tail[4] = new ModelRenderer(this);
			tail[4].setRotationPoint(0.0F, 6.0F, 0.0F);
			tail[3].addChild(tail[4]);
			setRotationAngle(tail[4], 0.2618F, 0.0F, -0.2618F);
			cube_r72 = new ModelRenderer(this);
			cube_r72.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[4].addChild(cube_r72);
			setRotationAngle(cube_r72, -2.9671F, 0.0F, -3.1416F);
			cube_r72.cubeList.add(new ModelBox(cube_r72, 52, 96, -2.0F, -1.5F, -2.85F, 4, 8, 0, 0.0F, false));
			cube_r73 = new ModelRenderer(this);
			cube_r73.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[4].addChild(cube_r73);
			setRotationAngle(cube_r73, -2.9671F, -0.7854F, 3.1416F);
			cube_r73.cubeList.add(new ModelBox(cube_r73, 36, 104, -2.0F, -1.5F, -2.85F, 4, 8, 0, 0.0F, false));
			cube_r74 = new ModelRenderer(this);
			cube_r74.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[4].addChild(cube_r74);
			setRotationAngle(cube_r74, -2.9671F, 0.7854F, 3.1416F);
			cube_r74.cubeList.add(new ModelBox(cube_r74, 60, 80, -2.0F, -1.5F, -2.85F, 4, 8, 0, 0.0F, false));
			cube_r75 = new ModelRenderer(this);
			cube_r75.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[4].addChild(cube_r75);
			setRotationAngle(cube_r75, 0.1745F, 1.5708F, 0.0F);
			cube_r75.cubeList.add(new ModelBox(cube_r75, 44, 104, -2.0F, -1.5F, -2.85F, 4, 8, 0, 0.0F, false));
			cube_r76 = new ModelRenderer(this);
			cube_r76.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[4].addChild(cube_r76);
			setRotationAngle(cube_r76, 0.1745F, -1.5708F, 0.0F);
			cube_r76.cubeList.add(new ModelBox(cube_r76, 60, 88, -2.0F, -1.5F, -2.85F, 4, 8, 0, 0.0F, false));
			cube_r77 = new ModelRenderer(this);
			cube_r77.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[4].addChild(cube_r77);
			setRotationAngle(cube_r77, 0.1745F, 0.7854F, 0.0F);
			cube_r77.cubeList.add(new ModelBox(cube_r77, 52, 104, -2.0F, -1.5F, -2.85F, 4, 8, 0, 0.0F, false));
			cube_r78 = new ModelRenderer(this);
			cube_r78.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[4].addChild(cube_r78);
			setRotationAngle(cube_r78, 0.1745F, -0.7854F, 0.0F);
			cube_r78.cubeList.add(new ModelBox(cube_r78, 60, 96, -2.0F, -1.5F, -2.85F, 4, 8, 0, 0.0F, false));
			cube_r79 = new ModelRenderer(this);
			cube_r79.setRotationPoint(0.0F, 0.0F, -0.5F);
			tail[4].addChild(cube_r79);
			setRotationAngle(cube_r79, 0.1745F, 0.0F, 0.0F);
			cube_r79.cubeList.add(new ModelBox(cube_r79, 60, 104, -2.0F, -1.5F, -2.85F, 4, 8, 0, 0.0F, false));
			tail[5] = new ModelRenderer(this);
			tail[5].setRotationPoint(0.0F, 7.0F, 0.0F);
			tail[4].addChild(tail[5]);
			setRotationAngle(tail[5], 0.2618F, 0.0F, -0.2618F);
			cube_r80 = new ModelRenderer(this);
			cube_r80.setRotationPoint(0.0F, 0.5F, -0.5F);
			tail[5].addChild(cube_r80);
			setRotationAngle(cube_r80, -2.8798F, 0.0F, -3.1416F);
			cube_r80.cubeList.add(new ModelBox(cube_r80, 36, 80, -2.0F, -1.5F, -1.35F, 4, 8, 0, 0.0F, false));
			cube_r81 = new ModelRenderer(this);
			cube_r81.setRotationPoint(0.0F, 0.5F, -0.5F);
			tail[5].addChild(cube_r81);
			setRotationAngle(cube_r81, -2.8798F, -0.7854F, 3.1416F);
			cube_r81.cubeList.add(new ModelBox(cube_r81, 36, 88, -2.0F, -1.5F, -1.35F, 4, 8, 0, 0.0F, false));
			cube_r82 = new ModelRenderer(this);
			cube_r82.setRotationPoint(0.0F, 0.5F, -0.5F);
			tail[5].addChild(cube_r82);
			setRotationAngle(cube_r82, -2.8798F, 0.7854F, 3.1416F);
			cube_r82.cubeList.add(new ModelBox(cube_r82, 44, 80, -2.0F, -1.5F, -1.35F, 4, 8, 0, 0.0F, false));
			cube_r83 = new ModelRenderer(this);
			cube_r83.setRotationPoint(0.0F, 0.5F, -0.5F);
			tail[5].addChild(cube_r83);
			setRotationAngle(cube_r83, 0.2618F, 1.5708F, 0.0F);
			cube_r83.cubeList.add(new ModelBox(cube_r83, 44, 88, -2.0F, -1.5F, -1.35F, 4, 8, 0, 0.0F, false));
			cube_r84 = new ModelRenderer(this);
			cube_r84.setRotationPoint(0.0F, 0.5F, -0.5F);
			tail[5].addChild(cube_r84);
			setRotationAngle(cube_r84, 0.2618F, -1.5708F, 0.0F);
			cube_r84.cubeList.add(new ModelBox(cube_r84, 36, 96, -2.0F, -1.5F, -1.35F, 4, 8, 0, 0.0F, false));
			cube_r85 = new ModelRenderer(this);
			cube_r85.setRotationPoint(0.0F, 0.5F, -0.5F);
			tail[5].addChild(cube_r85);
			setRotationAngle(cube_r85, 0.2618F, 0.7854F, 0.0F);
			cube_r85.cubeList.add(new ModelBox(cube_r85, 52, 80, -2.0F, -1.5F, -1.35F, 4, 8, 0, 0.0F, false));
			cube_r86 = new ModelRenderer(this);
			cube_r86.setRotationPoint(0.0F, 0.5F, -0.5F);
			tail[5].addChild(cube_r86);
			setRotationAngle(cube_r86, 0.2618F, -0.7854F, 0.0F);
			cube_r86.cubeList.add(new ModelBox(cube_r86, 44, 96, -2.0F, -1.5F, -1.35F, 4, 8, 0, 0.0F, false));
			cube_r87 = new ModelRenderer(this);
			cube_r87.setRotationPoint(0.0F, 0.5F, -0.5F);
			tail[5].addChild(cube_r87);
			setRotationAngle(cube_r87, 0.2618F, 0.0F, 0.0F);
			cube_r87.cubeList.add(new ModelBox(cube_r87, 52, 88, -2.0F, -1.5F, -1.35F, 4, 8, 0, 0.0F, false));
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
		public void setRotationAngles(float limbSwing, float limbSwingAmount, float f2, float f3, float f4, float f5, Entity e) {
			super.setRotationAngles(limbSwing, limbSwingAmount, f2, f3, f4, f5, e);
	        this.leg1.rotateAngleZ = MathHelper.cos(limbSwing * 0.5F) * 0.5F * limbSwingAmount;
	        this.leg2.rotateAngleZ = -MathHelper.cos(limbSwing * 0.5F + (float)Math.PI) * 0.5F * limbSwingAmount;
	        this.leg3.rotateAngleZ = MathHelper.cos(limbSwing * 0.5F + (float)Math.PI) * 0.5F * limbSwingAmount;
	        this.leg4.rotateAngleZ = -MathHelper.cos(limbSwing * 0.5F) * 0.5F * limbSwingAmount;
	        for (int i = 1; i < this.tail.length; ++i) {
	            this.tail[i].rotateAngleZ = MathHelper.cos(limbSwing * 0.4F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.04F * (float)(1 + Math.abs(i - 2));
	        }
		}
	}
}
