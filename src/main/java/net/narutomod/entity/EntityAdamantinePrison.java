
package net.narutomod.entity;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Iterator;

@ElementsNarutomodMod.ModElement.Tag
public class EntityAdamantinePrison extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 426;
	public static final int ENTITYID_RANGED = 427;
	private static final float ENTITY_SCALE = 2.5f;

	public EntityAdamantinePrison(ElementsNarutomodMod instance) {
		super(instance, 852);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "adamantine_prison"), ENTITYID).name("adamantine_prison").tracker(64, 3, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EC.LivingHook());
	}

	public static class EC extends EntityShieldBase {
		private final List<EntityLivingBase> entitiesInside = Lists.newArrayList();

		public EC(World world) {
			super(world);
			this.setSize(1.5f * ENTITY_SCALE, 1.5f * ENTITY_SCALE);
			this.dieOnNoPassengers = false;
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase summonerIn) {
			this(summonerIn.world);
			this.setSummoner(summonerIn);
			this.setLocationAndAngles(summonerIn.posX, summonerIn.posY, summonerIn.posZ, 0f, 0f);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5000D);
		}

		@Override
		public AxisAlignedBB getCollisionBoundingBox() {
			return this.getEntityBoundingBox();
		}

		@Override
		public boolean processInitialInteract(EntityPlayer entity, EnumHand hand) {
			return false;
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				ProcedureUtils.poofWithSmoke(this);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted == 1) {
				this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:poof")), 1f, 1f);
			}
			if (this.ticksExisted < 10) {
				Particles.Renderer particles = new Particles.Renderer(this.world);
				for (int i = 0; i < 100; i++) {
					particles.spawnParticles(Particles.Types.SMOKE, this.posX + (this.rand.nextFloat()-0.5f) * this.width,
					 this.posY + this.rand.nextDouble() * this.height, this.posZ + (this.rand.nextFloat()-0.5f) * this.width,
					 1, 0d, 0d, 0d, (this.rand.nextDouble()-0.5d) * 0.6d, this.rand.nextDouble() * 0.1d,
					 (this.rand.nextDouble()-0.5d) * 0.6d, 0xD0FFFFFF, 60);
				}
				particles.send();
			}
			if (this.ticksExisted < 3) {
				for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox())) {
					if (entity instanceof EntityLivingBase
					 && this.getEntityBoundingBox().intersect(entity.getEntityBoundingBox()).equals(entity.getEntityBoundingBox())) {
						this.entitiesInside.add((EntityLivingBase)entity);
					}
				}
			} else {
				Iterator<EntityLivingBase> iter = this.entitiesInside.iterator();
				while (iter.hasNext()) {
					EntityLivingBase entity = iter.next();
					if (!ItemJutsu.canTarget(entity)) {
						iter.remove();
					}
				}
				for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox())) {
					if (entity instanceof EntityLivingBase && !this.entitiesInside.contains(entity) && !ItemJutsu.canTarget(entity)
					 && this.getEntityBoundingBox().intersect(entity.getEntityBoundingBox()).equals(entity.getEntityBoundingBox())) {
						this.entitiesInside.add((EntityLivingBase)entity);
					}
				}
			}
			if (!this.world.isRemote && this.getSummoner() == null) {
				this.setDead();
			}
		}

		@Override
		protected void collideWithEntity(Entity entityIn) {
			this.applyEntityCollision(entityIn);
		}

		@Override
		public void applyEntityCollision(Entity entity) {
			if (!this.isRidingSameEntity(entity) && !entity.isBeingRidden() && !entity.noClip
			 && this.entitiesInside.contains(entity) && ItemJutsu.canTarget(entity)) {
	        	AxisAlignedBB bb1 = this.getEntityBoundingBox();
	        	AxisAlignedBB bb2 = entity.getEntityBoundingBox();
	        	double x = entity.posX;
	        	double y = entity.posY;
	        	double z = entity.posZ;
				if (bb2.minX < bb1.minX) {
			       	entity.motionX *= -0.1d;
			       	x = bb1.minX + entity.width * 0.5;
				}
				if (bb2.maxX > bb1.maxX) {
			    	entity.motionX *= -0.1d;
			    	x = bb1.maxX - entity.width * 0.5;
				}
				if (bb2.minZ < bb1.minZ) {
		        	entity.motionZ *= -0.1d;
		        	z = bb1.minZ + entity.width * 0.5;
				}
				if (bb2.maxZ > bb1.maxZ) {
	        		entity.motionZ *= -0.1d;
	        		z = bb1.maxZ - entity.width * 0.5;
				}
				if (bb2.minY < bb1.minY) {
	        		entity.motionY = 0.0d;
	        		y = bb1.minY;
	        		entity.onGround = true;
				}
				if (bb2.maxY > bb1.maxY) {
	        		entity.motionY *= -0.8d;
	        		y = bb1.maxY - entity.height;
				}
				if (x != entity.posX || y != entity.posY || z != entity.posZ) {
	       			entity.setPosition(x, y, z);
	        		entity.velocityChanged = true;
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "AdamantinePrisonEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY));
				if (entity1 instanceof EC && entity1.isEntityAlive()) {
					entity1.setDead();
					stack.getTagCompound().removeTag(ID_KEY);
					return false;
				} else {
					entity1 = new EC(entity);
					entity.world.spawnEntity(entity1);
					stack.getTagCompound().setInteger(ID_KEY, entity1.getEntityId());
					return true;
				}
			}
		}

		public static class LivingHook {
			@SubscribeEvent
			public void onAttackedInsideDome(LivingAttackEvent event) {
				EntityLivingBase target = event.getEntityLiving();
				if (!target.world.isRemote && !(target instanceof EC) && event.getSource() != ProcedureUtils.SPECIAL_DAMAGE) {
					EC dome = (EC)target.world.findNearestEntityWithinAABB(EC.class, target.getEntityBoundingBox().grow(ENTITY_SCALE), target);
					if (dome != null) {
						Entity attacker = event.getSource().getTrueSource();
						EntityLivingBase summoner = dome.getSummoner();
						if (attacker != null && dome.entitiesInside.contains(target) == !dome.entitiesInside.contains(attacker)) {
							event.setCanceled(true);
							dome.attackEntityFrom(event.getSource(), event.getAmount());
					    }
					}
				}
			}
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/adamintine_prison.png");
			private final ModelAdamantinePrison model = new ModelAdamantinePrison();

			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.shadowSize = 0.0f;
			}

			@Override
			public boolean shouldRender(EC entity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
				return true;
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float pt) {
				if (entity.ticksExisted > 5) {
					GlStateManager.pushMatrix();
					this.bindEntityTexture(entity);
					GlStateManager.translate((float)x, (float)y, (float)z);
					GlStateManager.scale(ENTITY_SCALE, ENTITY_SCALE, ENTITY_SCALE);
					GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.disableCull();
					this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.enableCull();
					GlStateManager.popMatrix();
				}
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 4.7.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelAdamantinePrison extends ModelBase {
			private final ModelRenderer side1;
			private final ModelRenderer octagon;
			private final ModelRenderer octagon_r1;
			private final ModelRenderer octagon_r2;
			private final ModelRenderer octagon_r3;
			private final ModelRenderer octagon6;
			private final ModelRenderer octagon_r4;
			private final ModelRenderer octagon_r5;
			private final ModelRenderer octagon_r6;
			private final ModelRenderer octagon9;
			private final ModelRenderer octagon_r7;
			private final ModelRenderer octagon_r8;
			private final ModelRenderer octagon_r9;
			private final ModelRenderer octagon7;
			private final ModelRenderer octagon_r10;
			private final ModelRenderer octagon_r11;
			private final ModelRenderer octagon_r12;
			private final ModelRenderer octagon8;
			private final ModelRenderer octagon_r13;
			private final ModelRenderer octagon_r14;
			private final ModelRenderer octagon_r15;
			private final ModelRenderer octagon2;
			private final ModelRenderer octagon_r16;
			private final ModelRenderer octagon_r17;
			private final ModelRenderer octagon_r18;
			private final ModelRenderer octagon3;
			private final ModelRenderer octagon_r19;
			private final ModelRenderer octagon_r20;
			private final ModelRenderer octagon_r21;
			private final ModelRenderer octagon4;
			private final ModelRenderer octagon_r22;
			private final ModelRenderer octagon_r23;
			private final ModelRenderer octagon_r24;
			private final ModelRenderer octagon5;
			private final ModelRenderer octagon_r25;
			private final ModelRenderer octagon_r26;
			private final ModelRenderer octagon_r27;
			private final ModelRenderer side2;
			private final ModelRenderer octagon29;
			private final ModelRenderer octagon_r28;
			private final ModelRenderer octagon_r29;
			private final ModelRenderer octagon_r30;
			private final ModelRenderer octagon30;
			private final ModelRenderer octagon_r31;
			private final ModelRenderer octagon_r32;
			private final ModelRenderer octagon_r33;
			private final ModelRenderer octagon31;
			private final ModelRenderer octagon_r34;
			private final ModelRenderer octagon_r35;
			private final ModelRenderer octagon_r36;
			private final ModelRenderer octagon33;
			private final ModelRenderer octagon_r37;
			private final ModelRenderer octagon_r38;
			private final ModelRenderer octagon_r39;
			private final ModelRenderer octagon34;
			private final ModelRenderer octagon_r40;
			private final ModelRenderer octagon_r41;
			private final ModelRenderer octagon_r42;
			private final ModelRenderer octagon35;
			private final ModelRenderer octagon_r43;
			private final ModelRenderer octagon_r44;
			private final ModelRenderer octagon_r45;
			private final ModelRenderer octagon36;
			private final ModelRenderer octagon_r46;
			private final ModelRenderer octagon_r47;
			private final ModelRenderer octagon_r48;
			private final ModelRenderer octagon37;
			private final ModelRenderer octagon_r49;
			private final ModelRenderer octagon_r50;
			private final ModelRenderer octagon_r51;
			private final ModelRenderer side3;
			private final ModelRenderer octagon11;
			private final ModelRenderer octagon_r52;
			private final ModelRenderer octagon_r53;
			private final ModelRenderer octagon_r54;
			private final ModelRenderer octagon12;
			private final ModelRenderer octagon_r55;
			private final ModelRenderer octagon_r56;
			private final ModelRenderer octagon_r57;
			private final ModelRenderer octagon13;
			private final ModelRenderer octagon_r58;
			private final ModelRenderer octagon_r59;
			private final ModelRenderer octagon_r60;
			private final ModelRenderer octagon15;
			private final ModelRenderer octagon_r61;
			private final ModelRenderer octagon_r62;
			private final ModelRenderer octagon_r63;
			private final ModelRenderer octagon16;
			private final ModelRenderer octagon_r64;
			private final ModelRenderer octagon_r65;
			private final ModelRenderer octagon_r66;
			private final ModelRenderer octagon17;
			private final ModelRenderer octagon_r67;
			private final ModelRenderer octagon_r68;
			private final ModelRenderer octagon_r69;
			private final ModelRenderer octagon19;
			private final ModelRenderer octagon_r70;
			private final ModelRenderer octagon_r71;
			private final ModelRenderer octagon_r72;
			private final ModelRenderer octagon20;
			private final ModelRenderer octagon_r73;
			private final ModelRenderer octagon_r74;
			private final ModelRenderer octagon_r75;
			private final ModelRenderer side4;
			private final ModelRenderer octagon18;
			private final ModelRenderer octagon_r76;
			private final ModelRenderer octagon_r77;
			private final ModelRenderer octagon_r78;
			private final ModelRenderer octagon21;
			private final ModelRenderer octagon_r79;
			private final ModelRenderer octagon_r80;
			private final ModelRenderer octagon_r81;
			private final ModelRenderer octagon22;
			private final ModelRenderer octagon_r82;
			private final ModelRenderer octagon_r83;
			private final ModelRenderer octagon_r84;
			private final ModelRenderer octagon24;
			private final ModelRenderer octagon_r85;
			private final ModelRenderer octagon_r86;
			private final ModelRenderer octagon_r87;
			private final ModelRenderer octagon25;
			private final ModelRenderer octagon_r88;
			private final ModelRenderer octagon_r89;
			private final ModelRenderer octagon_r90;
			private final ModelRenderer octagon26;
			private final ModelRenderer octagon_r91;
			private final ModelRenderer octagon_r92;
			private final ModelRenderer octagon_r93;
			private final ModelRenderer octagon27;
			private final ModelRenderer octagon_r94;
			private final ModelRenderer octagon_r95;
			private final ModelRenderer octagon_r96;
			private final ModelRenderer top;
			private final ModelRenderer octagon39;
			private final ModelRenderer octagon_r97;
			private final ModelRenderer octagon_r98;
			private final ModelRenderer octagon_r99;
			private final ModelRenderer octagon40;
			private final ModelRenderer octagon_r100;
			private final ModelRenderer octagon_r101;
			private final ModelRenderer octagon_r102;
			private final ModelRenderer octagon43;
			private final ModelRenderer octagon_r103;
			private final ModelRenderer octagon_r104;
			private final ModelRenderer octagon_r105;
			private final ModelRenderer octagon47;
			private final ModelRenderer octagon_r106;
			private final ModelRenderer octagon_r107;
			private final ModelRenderer octagon_r108;
		
			public ModelAdamantinePrison() {
				textureWidth = 64;
				textureHeight = 64;
		
				side1 = new ModelRenderer(this);
				side1.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				octagon = new ModelRenderer(this);
				octagon.setRotationPoint(0.0F, -4.0F, -12.0F);
				side1.addChild(octagon);
				octagon.cubeList.add(new ModelBox(octagon, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r1 = new ModelRenderer(this);
				octagon_r1.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon.addChild(octagon_r1);
				setRotationAngle(octagon_r1, 0.0F, 2.3562F, 0.0F);
				octagon_r1.cubeList.add(new ModelBox(octagon_r1, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r2 = new ModelRenderer(this);
				octagon_r2.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon.addChild(octagon_r2);
				setRotationAngle(octagon_r2, 0.0F, 1.5708F, 0.0F);
				octagon_r2.cubeList.add(new ModelBox(octagon_r2, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r3 = new ModelRenderer(this);
				octagon_r3.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon.addChild(octagon_r3);
				setRotationAngle(octagon_r3, 0.0F, 0.7854F, 0.0F);
				octagon_r3.cubeList.add(new ModelBox(octagon_r3, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon6 = new ModelRenderer(this);
				octagon6.setRotationPoint(-0.0858F, -16.0F, -13.75F);
				side1.addChild(octagon6);
				setRotationAngle(octagon6, 0.0F, 0.0F, -1.5708F);
				octagon6.cubeList.add(new ModelBox(octagon6, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r4 = new ModelRenderer(this);
				octagon_r4.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon6.addChild(octagon_r4);
				setRotationAngle(octagon_r4, 0.0F, 2.3562F, 0.0F);
				octagon_r4.cubeList.add(new ModelBox(octagon_r4, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r5 = new ModelRenderer(this);
				octagon_r5.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon6.addChild(octagon_r5);
				setRotationAngle(octagon_r5, 0.0F, 1.5708F, 0.0F);
				octagon_r5.cubeList.add(new ModelBox(octagon_r5, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r6 = new ModelRenderer(this);
				octagon_r6.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon6.addChild(octagon_r6);
				setRotationAngle(octagon_r6, 0.0F, 0.7854F, 0.0F);
				octagon_r6.cubeList.add(new ModelBox(octagon_r6, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon9 = new ModelRenderer(this);
				octagon9.setRotationPoint(-0.0858F, -22.0F, -13.75F);
				side1.addChild(octagon9);
				setRotationAngle(octagon9, 0.0F, 0.0F, -1.5708F);
				octagon9.cubeList.add(new ModelBox(octagon9, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r7 = new ModelRenderer(this);
				octagon_r7.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon9.addChild(octagon_r7);
				setRotationAngle(octagon_r7, 0.0F, 2.3562F, 0.0F);
				octagon_r7.cubeList.add(new ModelBox(octagon_r7, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r8 = new ModelRenderer(this);
				octagon_r8.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon9.addChild(octagon_r8);
				setRotationAngle(octagon_r8, 0.0F, 1.5708F, 0.0F);
				octagon_r8.cubeList.add(new ModelBox(octagon_r8, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r9 = new ModelRenderer(this);
				octagon_r9.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon9.addChild(octagon_r9);
				setRotationAngle(octagon_r9, 0.0F, 0.7854F, 0.0F);
				octagon_r9.cubeList.add(new ModelBox(octagon_r9, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon7 = new ModelRenderer(this);
				octagon7.setRotationPoint(-0.0858F, -10.0F, -13.75F);
				side1.addChild(octagon7);
				setRotationAngle(octagon7, 0.0F, 0.0F, -1.5708F);
				octagon7.cubeList.add(new ModelBox(octagon7, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r10 = new ModelRenderer(this);
				octagon_r10.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon7.addChild(octagon_r10);
				setRotationAngle(octagon_r10, 0.0F, 2.3562F, 0.0F);
				octagon_r10.cubeList.add(new ModelBox(octagon_r10, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r11 = new ModelRenderer(this);
				octagon_r11.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon7.addChild(octagon_r11);
				setRotationAngle(octagon_r11, 0.0F, 1.5708F, 0.0F);
				octagon_r11.cubeList.add(new ModelBox(octagon_r11, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r12 = new ModelRenderer(this);
				octagon_r12.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon7.addChild(octagon_r12);
				setRotationAngle(octagon_r12, 0.0F, 0.7854F, 0.0F);
				octagon_r12.cubeList.add(new ModelBox(octagon_r12, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon8 = new ModelRenderer(this);
				octagon8.setRotationPoint(-0.0858F, -4.0F, -13.75F);
				side1.addChild(octagon8);
				setRotationAngle(octagon8, 0.0F, 0.0F, -1.5708F);
				octagon8.cubeList.add(new ModelBox(octagon8, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r13 = new ModelRenderer(this);
				octagon_r13.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon8.addChild(octagon_r13);
				setRotationAngle(octagon_r13, 0.0F, 2.3562F, 0.0F);
				octagon_r13.cubeList.add(new ModelBox(octagon_r13, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r14 = new ModelRenderer(this);
				octagon_r14.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon8.addChild(octagon_r14);
				setRotationAngle(octagon_r14, 0.0F, 1.5708F, 0.0F);
				octagon_r14.cubeList.add(new ModelBox(octagon_r14, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r15 = new ModelRenderer(this);
				octagon_r15.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon8.addChild(octagon_r15);
				setRotationAngle(octagon_r15, 0.0F, 0.7854F, 0.0F);
				octagon_r15.cubeList.add(new ModelBox(octagon_r15, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon2 = new ModelRenderer(this);
				octagon2.setRotationPoint(6.0F, -4.0F, -12.0F);
				side1.addChild(octagon2);
				octagon2.cubeList.add(new ModelBox(octagon2, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r16 = new ModelRenderer(this);
				octagon_r16.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon2.addChild(octagon_r16);
				setRotationAngle(octagon_r16, 0.0F, 2.3562F, 0.0F);
				octagon_r16.cubeList.add(new ModelBox(octagon_r16, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r17 = new ModelRenderer(this);
				octagon_r17.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon2.addChild(octagon_r17);
				setRotationAngle(octagon_r17, 0.0F, 1.5708F, 0.0F);
				octagon_r17.cubeList.add(new ModelBox(octagon_r17, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r18 = new ModelRenderer(this);
				octagon_r18.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon2.addChild(octagon_r18);
				setRotationAngle(octagon_r18, 0.0F, 0.7854F, 0.0F);
				octagon_r18.cubeList.add(new ModelBox(octagon_r18, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon3 = new ModelRenderer(this);
				octagon3.setRotationPoint(12.0F, -4.0F, -12.0F);
				side1.addChild(octagon3);
				octagon3.cubeList.add(new ModelBox(octagon3, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r19 = new ModelRenderer(this);
				octagon_r19.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon3.addChild(octagon_r19);
				setRotationAngle(octagon_r19, 0.0F, 2.3562F, 0.0F);
				octagon_r19.cubeList.add(new ModelBox(octagon_r19, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r20 = new ModelRenderer(this);
				octagon_r20.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon3.addChild(octagon_r20);
				setRotationAngle(octagon_r20, 0.0F, 1.5708F, 0.0F);
				octagon_r20.cubeList.add(new ModelBox(octagon_r20, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r21 = new ModelRenderer(this);
				octagon_r21.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon3.addChild(octagon_r21);
				setRotationAngle(octagon_r21, 0.0F, 0.7854F, 0.0F);
				octagon_r21.cubeList.add(new ModelBox(octagon_r21, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon4 = new ModelRenderer(this);
				octagon4.setRotationPoint(-6.0F, -4.0F, -12.0F);
				side1.addChild(octagon4);
				octagon4.cubeList.add(new ModelBox(octagon4, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r22 = new ModelRenderer(this);
				octagon_r22.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon4.addChild(octagon_r22);
				setRotationAngle(octagon_r22, 0.0F, 2.3562F, 0.0F);
				octagon_r22.cubeList.add(new ModelBox(octagon_r22, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r23 = new ModelRenderer(this);
				octagon_r23.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon4.addChild(octagon_r23);
				setRotationAngle(octagon_r23, 0.0F, 1.5708F, 0.0F);
				octagon_r23.cubeList.add(new ModelBox(octagon_r23, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r24 = new ModelRenderer(this);
				octagon_r24.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon4.addChild(octagon_r24);
				setRotationAngle(octagon_r24, 0.0F, 0.7854F, 0.0F);
				octagon_r24.cubeList.add(new ModelBox(octagon_r24, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon5 = new ModelRenderer(this);
				octagon5.setRotationPoint(-12.0F, -4.0F, -12.0F);
				side1.addChild(octagon5);
				octagon5.cubeList.add(new ModelBox(octagon5, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r25 = new ModelRenderer(this);
				octagon_r25.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon5.addChild(octagon_r25);
				setRotationAngle(octagon_r25, 0.0F, 2.3562F, 0.0F);
				octagon_r25.cubeList.add(new ModelBox(octagon_r25, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r26 = new ModelRenderer(this);
				octagon_r26.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon5.addChild(octagon_r26);
				setRotationAngle(octagon_r26, 0.0F, 1.5708F, 0.0F);
				octagon_r26.cubeList.add(new ModelBox(octagon_r26, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r27 = new ModelRenderer(this);
				octagon_r27.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon5.addChild(octagon_r27);
				setRotationAngle(octagon_r27, 0.0F, 0.7854F, 0.0F);
				octagon_r27.cubeList.add(new ModelBox(octagon_r27, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				side2 = new ModelRenderer(this);
				side2.setRotationPoint(0.0F, 0.0F, 0.0F);
				
		
				octagon29 = new ModelRenderer(this);
				octagon29.setRotationPoint(0.0F, -4.0F, 12.0F);
				side2.addChild(octagon29);
				octagon29.cubeList.add(new ModelBox(octagon29, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r28 = new ModelRenderer(this);
				octagon_r28.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon29.addChild(octagon_r28);
				setRotationAngle(octagon_r28, 0.0F, -2.3562F, 0.0F);
				octagon_r28.cubeList.add(new ModelBox(octagon_r28, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r29 = new ModelRenderer(this);
				octagon_r29.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon29.addChild(octagon_r29);
				setRotationAngle(octagon_r29, 0.0F, -1.5708F, 0.0F);
				octagon_r29.cubeList.add(new ModelBox(octagon_r29, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r30 = new ModelRenderer(this);
				octagon_r30.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon29.addChild(octagon_r30);
				setRotationAngle(octagon_r30, 0.0F, -0.7854F, 0.0F);
				octagon_r30.cubeList.add(new ModelBox(octagon_r30, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon30 = new ModelRenderer(this);
				octagon30.setRotationPoint(-0.0858F, -16.0F, 13.75F);
				side2.addChild(octagon30);
				setRotationAngle(octagon30, 0.0F, 0.0F, -1.5708F);
				octagon30.cubeList.add(new ModelBox(octagon30, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r31 = new ModelRenderer(this);
				octagon_r31.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon30.addChild(octagon_r31);
				setRotationAngle(octagon_r31, 0.0F, -2.3562F, 0.0F);
				octagon_r31.cubeList.add(new ModelBox(octagon_r31, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r32 = new ModelRenderer(this);
				octagon_r32.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon30.addChild(octagon_r32);
				setRotationAngle(octagon_r32, 0.0F, -1.5708F, 0.0F);
				octagon_r32.cubeList.add(new ModelBox(octagon_r32, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r33 = new ModelRenderer(this);
				octagon_r33.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon30.addChild(octagon_r33);
				setRotationAngle(octagon_r33, 0.0F, -0.7854F, 0.0F);
				octagon_r33.cubeList.add(new ModelBox(octagon_r33, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon31 = new ModelRenderer(this);
				octagon31.setRotationPoint(-0.0858F, -22.0F, 13.75F);
				side2.addChild(octagon31);
				setRotationAngle(octagon31, 0.0F, 0.0F, -1.5708F);
				octagon31.cubeList.add(new ModelBox(octagon31, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r34 = new ModelRenderer(this);
				octagon_r34.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon31.addChild(octagon_r34);
				setRotationAngle(octagon_r34, 0.0F, -2.3562F, 0.0F);
				octagon_r34.cubeList.add(new ModelBox(octagon_r34, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r35 = new ModelRenderer(this);
				octagon_r35.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon31.addChild(octagon_r35);
				setRotationAngle(octagon_r35, 0.0F, -1.5708F, 0.0F);
				octagon_r35.cubeList.add(new ModelBox(octagon_r35, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r36 = new ModelRenderer(this);
				octagon_r36.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon31.addChild(octagon_r36);
				setRotationAngle(octagon_r36, 0.0F, -0.7854F, 0.0F);
				octagon_r36.cubeList.add(new ModelBox(octagon_r36, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon33 = new ModelRenderer(this);
				octagon33.setRotationPoint(-0.0858F, -10.0F, 13.75F);
				side2.addChild(octagon33);
				setRotationAngle(octagon33, 0.0F, 0.0F, -1.5708F);
				octagon33.cubeList.add(new ModelBox(octagon33, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r37 = new ModelRenderer(this);
				octagon_r37.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon33.addChild(octagon_r37);
				setRotationAngle(octagon_r37, 0.0F, -2.3562F, 0.0F);
				octagon_r37.cubeList.add(new ModelBox(octagon_r37, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r38 = new ModelRenderer(this);
				octagon_r38.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon33.addChild(octagon_r38);
				setRotationAngle(octagon_r38, 0.0F, -1.5708F, 0.0F);
				octagon_r38.cubeList.add(new ModelBox(octagon_r38, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r39 = new ModelRenderer(this);
				octagon_r39.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon33.addChild(octagon_r39);
				setRotationAngle(octagon_r39, 0.0F, -0.7854F, 0.0F);
				octagon_r39.cubeList.add(new ModelBox(octagon_r39, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon34 = new ModelRenderer(this);
				octagon34.setRotationPoint(-0.0858F, -4.0F, 13.75F);
				side2.addChild(octagon34);
				setRotationAngle(octagon34, 0.0F, 0.0F, -1.5708F);
				octagon34.cubeList.add(new ModelBox(octagon34, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r40 = new ModelRenderer(this);
				octagon_r40.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon34.addChild(octagon_r40);
				setRotationAngle(octagon_r40, 0.0F, -2.3562F, 0.0F);
				octagon_r40.cubeList.add(new ModelBox(octagon_r40, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r41 = new ModelRenderer(this);
				octagon_r41.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon34.addChild(octagon_r41);
				setRotationAngle(octagon_r41, 0.0F, -1.5708F, 0.0F);
				octagon_r41.cubeList.add(new ModelBox(octagon_r41, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r42 = new ModelRenderer(this);
				octagon_r42.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon34.addChild(octagon_r42);
				setRotationAngle(octagon_r42, 0.0F, -0.7854F, 0.0F);
				octagon_r42.cubeList.add(new ModelBox(octagon_r42, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon35 = new ModelRenderer(this);
				octagon35.setRotationPoint(6.0F, -4.0F, 12.0F);
				side2.addChild(octagon35);
				octagon35.cubeList.add(new ModelBox(octagon35, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r43 = new ModelRenderer(this);
				octagon_r43.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon35.addChild(octagon_r43);
				setRotationAngle(octagon_r43, 0.0F, -2.3562F, 0.0F);
				octagon_r43.cubeList.add(new ModelBox(octagon_r43, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r44 = new ModelRenderer(this);
				octagon_r44.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon35.addChild(octagon_r44);
				setRotationAngle(octagon_r44, 0.0F, -1.5708F, 0.0F);
				octagon_r44.cubeList.add(new ModelBox(octagon_r44, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r45 = new ModelRenderer(this);
				octagon_r45.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon35.addChild(octagon_r45);
				setRotationAngle(octagon_r45, 0.0F, -0.7854F, 0.0F);
				octagon_r45.cubeList.add(new ModelBox(octagon_r45, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon36 = new ModelRenderer(this);
				octagon36.setRotationPoint(12.0F, -4.0F, 12.0F);
				side2.addChild(octagon36);
				octagon36.cubeList.add(new ModelBox(octagon36, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r46 = new ModelRenderer(this);
				octagon_r46.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon36.addChild(octagon_r46);
				setRotationAngle(octagon_r46, 0.0F, -2.3562F, 0.0F);
				octagon_r46.cubeList.add(new ModelBox(octagon_r46, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r47 = new ModelRenderer(this);
				octagon_r47.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon36.addChild(octagon_r47);
				setRotationAngle(octagon_r47, 0.0F, -1.5708F, 0.0F);
				octagon_r47.cubeList.add(new ModelBox(octagon_r47, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r48 = new ModelRenderer(this);
				octagon_r48.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon36.addChild(octagon_r48);
				setRotationAngle(octagon_r48, 0.0F, -0.7854F, 0.0F);
				octagon_r48.cubeList.add(new ModelBox(octagon_r48, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon37 = new ModelRenderer(this);
				octagon37.setRotationPoint(-6.0F, -4.0F, 12.0F);
				side2.addChild(octagon37);
				octagon37.cubeList.add(new ModelBox(octagon37, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r49 = new ModelRenderer(this);
				octagon_r49.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon37.addChild(octagon_r49);
				setRotationAngle(octagon_r49, 0.0F, -2.3562F, 0.0F);
				octagon_r49.cubeList.add(new ModelBox(octagon_r49, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r50 = new ModelRenderer(this);
				octagon_r50.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon37.addChild(octagon_r50);
				setRotationAngle(octagon_r50, 0.0F, -1.5708F, 0.0F);
				octagon_r50.cubeList.add(new ModelBox(octagon_r50, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r51 = new ModelRenderer(this);
				octagon_r51.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon37.addChild(octagon_r51);
				setRotationAngle(octagon_r51, 0.0F, -0.7854F, 0.0F);
				octagon_r51.cubeList.add(new ModelBox(octagon_r51, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				side3 = new ModelRenderer(this);
				side3.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(side3, 0.0F, 1.5708F, 0.0F);
				
		
				octagon11 = new ModelRenderer(this);
				octagon11.setRotationPoint(0.0F, -4.0F, -12.0F);
				side3.addChild(octagon11);
				octagon11.cubeList.add(new ModelBox(octagon11, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r52 = new ModelRenderer(this);
				octagon_r52.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon11.addChild(octagon_r52);
				setRotationAngle(octagon_r52, 0.0F, 2.3562F, 0.0F);
				octagon_r52.cubeList.add(new ModelBox(octagon_r52, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r53 = new ModelRenderer(this);
				octagon_r53.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon11.addChild(octagon_r53);
				setRotationAngle(octagon_r53, 0.0F, 1.5708F, 0.0F);
				octagon_r53.cubeList.add(new ModelBox(octagon_r53, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r54 = new ModelRenderer(this);
				octagon_r54.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon11.addChild(octagon_r54);
				setRotationAngle(octagon_r54, 0.0F, 0.7854F, 0.0F);
				octagon_r54.cubeList.add(new ModelBox(octagon_r54, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon12 = new ModelRenderer(this);
				octagon12.setRotationPoint(-0.0858F, -17.75F, -13.75F);
				side3.addChild(octagon12);
				setRotationAngle(octagon12, 0.0F, 0.0F, -1.5708F);
				octagon12.cubeList.add(new ModelBox(octagon12, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r55 = new ModelRenderer(this);
				octagon_r55.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon12.addChild(octagon_r55);
				setRotationAngle(octagon_r55, 0.0F, 2.3562F, 0.0F);
				octagon_r55.cubeList.add(new ModelBox(octagon_r55, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r56 = new ModelRenderer(this);
				octagon_r56.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon12.addChild(octagon_r56);
				setRotationAngle(octagon_r56, 0.0F, 1.5708F, 0.0F);
				octagon_r56.cubeList.add(new ModelBox(octagon_r56, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r57 = new ModelRenderer(this);
				octagon_r57.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon12.addChild(octagon_r57);
				setRotationAngle(octagon_r57, 0.0F, 0.7854F, 0.0F);
				octagon_r57.cubeList.add(new ModelBox(octagon_r57, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon13 = new ModelRenderer(this);
				octagon13.setRotationPoint(-0.0858F, -23.75F, -13.75F);
				side3.addChild(octagon13);
				setRotationAngle(octagon13, 0.0F, 0.0F, -1.5708F);
				octagon13.cubeList.add(new ModelBox(octagon13, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r58 = new ModelRenderer(this);
				octagon_r58.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon13.addChild(octagon_r58);
				setRotationAngle(octagon_r58, 0.0F, 2.3562F, 0.0F);
				octagon_r58.cubeList.add(new ModelBox(octagon_r58, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r59 = new ModelRenderer(this);
				octagon_r59.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon13.addChild(octagon_r59);
				setRotationAngle(octagon_r59, 0.0F, 1.5708F, 0.0F);
				octagon_r59.cubeList.add(new ModelBox(octagon_r59, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r60 = new ModelRenderer(this);
				octagon_r60.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon13.addChild(octagon_r60);
				setRotationAngle(octagon_r60, 0.0F, 0.7854F, 0.0F);
				octagon_r60.cubeList.add(new ModelBox(octagon_r60, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon15 = new ModelRenderer(this);
				octagon15.setRotationPoint(-0.0858F, -11.75F, -13.75F);
				side3.addChild(octagon15);
				setRotationAngle(octagon15, 0.0F, 0.0F, -1.5708F);
				octagon15.cubeList.add(new ModelBox(octagon15, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r61 = new ModelRenderer(this);
				octagon_r61.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon15.addChild(octagon_r61);
				setRotationAngle(octagon_r61, 0.0F, 2.3562F, 0.0F);
				octagon_r61.cubeList.add(new ModelBox(octagon_r61, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r62 = new ModelRenderer(this);
				octagon_r62.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon15.addChild(octagon_r62);
				setRotationAngle(octagon_r62, 0.0F, 1.5708F, 0.0F);
				octagon_r62.cubeList.add(new ModelBox(octagon_r62, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r63 = new ModelRenderer(this);
				octagon_r63.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon15.addChild(octagon_r63);
				setRotationAngle(octagon_r63, 0.0F, 0.7854F, 0.0F);
				octagon_r63.cubeList.add(new ModelBox(octagon_r63, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon16 = new ModelRenderer(this);
				octagon16.setRotationPoint(-0.0858F, -5.75F, -13.75F);
				side3.addChild(octagon16);
				setRotationAngle(octagon16, 0.0F, 0.0F, -1.5708F);
				octagon16.cubeList.add(new ModelBox(octagon16, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r64 = new ModelRenderer(this);
				octagon_r64.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon16.addChild(octagon_r64);
				setRotationAngle(octagon_r64, 0.0F, 2.3562F, 0.0F);
				octagon_r64.cubeList.add(new ModelBox(octagon_r64, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r65 = new ModelRenderer(this);
				octagon_r65.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon16.addChild(octagon_r65);
				setRotationAngle(octagon_r65, 0.0F, 1.5708F, 0.0F);
				octagon_r65.cubeList.add(new ModelBox(octagon_r65, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r66 = new ModelRenderer(this);
				octagon_r66.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon16.addChild(octagon_r66);
				setRotationAngle(octagon_r66, 0.0F, 0.7854F, 0.0F);
				octagon_r66.cubeList.add(new ModelBox(octagon_r66, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon17 = new ModelRenderer(this);
				octagon17.setRotationPoint(6.0F, -4.0F, -12.0F);
				side3.addChild(octagon17);
				octagon17.cubeList.add(new ModelBox(octagon17, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r67 = new ModelRenderer(this);
				octagon_r67.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon17.addChild(octagon_r67);
				setRotationAngle(octagon_r67, 0.0F, 2.3562F, 0.0F);
				octagon_r67.cubeList.add(new ModelBox(octagon_r67, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r68 = new ModelRenderer(this);
				octagon_r68.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon17.addChild(octagon_r68);
				setRotationAngle(octagon_r68, 0.0F, 1.5708F, 0.0F);
				octagon_r68.cubeList.add(new ModelBox(octagon_r68, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r69 = new ModelRenderer(this);
				octagon_r69.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon17.addChild(octagon_r69);
				setRotationAngle(octagon_r69, 0.0F, 0.7854F, 0.0F);
				octagon_r69.cubeList.add(new ModelBox(octagon_r69, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon19 = new ModelRenderer(this);
				octagon19.setRotationPoint(-6.0F, -4.0F, -12.0F);
				side3.addChild(octagon19);
				octagon19.cubeList.add(new ModelBox(octagon19, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r70 = new ModelRenderer(this);
				octagon_r70.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon19.addChild(octagon_r70);
				setRotationAngle(octagon_r70, 0.0F, 2.3562F, 0.0F);
				octagon_r70.cubeList.add(new ModelBox(octagon_r70, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r71 = new ModelRenderer(this);
				octagon_r71.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon19.addChild(octagon_r71);
				setRotationAngle(octagon_r71, 0.0F, 1.5708F, 0.0F);
				octagon_r71.cubeList.add(new ModelBox(octagon_r71, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r72 = new ModelRenderer(this);
				octagon_r72.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon19.addChild(octagon_r72);
				setRotationAngle(octagon_r72, 0.0F, 0.7854F, 0.0F);
				octagon_r72.cubeList.add(new ModelBox(octagon_r72, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon20 = new ModelRenderer(this);
				octagon20.setRotationPoint(-12.0F, -4.0F, -12.0F);
				side3.addChild(octagon20);
				octagon20.cubeList.add(new ModelBox(octagon20, 6, 0, -0.5858F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r73 = new ModelRenderer(this);
				octagon_r73.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon20.addChild(octagon_r73);
				setRotationAngle(octagon_r73, 0.0F, 2.3562F, 0.0F);
				octagon_r73.cubeList.add(new ModelBox(octagon_r73, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r74 = new ModelRenderer(this);
				octagon_r74.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon20.addChild(octagon_r74);
				setRotationAngle(octagon_r74, 0.0F, 1.5708F, 0.0F);
				octagon_r74.cubeList.add(new ModelBox(octagon_r74, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				octagon_r75 = new ModelRenderer(this);
				octagon_r75.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon20.addChild(octagon_r75);
				setRotationAngle(octagon_r75, 0.0F, 0.7854F, 0.0F);
				octagon_r75.cubeList.add(new ModelBox(octagon_r75, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, false));
		
				side4 = new ModelRenderer(this);
				side4.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(side4, 0.0F, -1.5708F, 0.0F);
				
		
				octagon18 = new ModelRenderer(this);
				octagon18.setRotationPoint(0.0F, -4.0F, -12.0F);
				side4.addChild(octagon18);
				octagon18.cubeList.add(new ModelBox(octagon18, 6, 0, -0.4142F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon_r76 = new ModelRenderer(this);
				octagon_r76.setRotationPoint(0.0858F, 0.0F, 0.0F);
				octagon18.addChild(octagon_r76);
				setRotationAngle(octagon_r76, 0.0F, -2.3562F, 0.0F);
				octagon_r76.cubeList.add(new ModelBox(octagon_r76, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon_r77 = new ModelRenderer(this);
				octagon_r77.setRotationPoint(0.0858F, 0.0F, 0.0F);
				octagon18.addChild(octagon_r77);
				setRotationAngle(octagon_r77, 0.0F, -1.5708F, 0.0F);
				octagon_r77.cubeList.add(new ModelBox(octagon_r77, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon_r78 = new ModelRenderer(this);
				octagon_r78.setRotationPoint(0.0858F, 0.0F, 0.0F);
				octagon18.addChild(octagon_r78);
				setRotationAngle(octagon_r78, 0.0F, -0.7854F, 0.0F);
				octagon_r78.cubeList.add(new ModelBox(octagon_r78, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon21 = new ModelRenderer(this);
				octagon21.setRotationPoint(0.0858F, -17.75F, -13.75F);
				side4.addChild(octagon21);
				setRotationAngle(octagon21, 0.0F, 0.0F, 1.5708F);
				octagon21.cubeList.add(new ModelBox(octagon21, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r79 = new ModelRenderer(this);
				octagon_r79.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon21.addChild(octagon_r79);
				setRotationAngle(octagon_r79, 0.0F, -2.3562F, 0.0F);
				octagon_r79.cubeList.add(new ModelBox(octagon_r79, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r80 = new ModelRenderer(this);
				octagon_r80.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon21.addChild(octagon_r80);
				setRotationAngle(octagon_r80, 0.0F, -1.5708F, 0.0F);
				octagon_r80.cubeList.add(new ModelBox(octagon_r80, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r81 = new ModelRenderer(this);
				octagon_r81.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon21.addChild(octagon_r81);
				setRotationAngle(octagon_r81, 0.0F, -0.7854F, 0.0F);
				octagon_r81.cubeList.add(new ModelBox(octagon_r81, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon22 = new ModelRenderer(this);
				octagon22.setRotationPoint(0.0858F, -23.75F, -13.75F);
				side4.addChild(octagon22);
				setRotationAngle(octagon22, 0.0F, 0.0F, 1.5708F);
				octagon22.cubeList.add(new ModelBox(octagon22, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r82 = new ModelRenderer(this);
				octagon_r82.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon22.addChild(octagon_r82);
				setRotationAngle(octagon_r82, 0.0F, -2.3562F, 0.0F);
				octagon_r82.cubeList.add(new ModelBox(octagon_r82, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r83 = new ModelRenderer(this);
				octagon_r83.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon22.addChild(octagon_r83);
				setRotationAngle(octagon_r83, 0.0F, -1.5708F, 0.0F);
				octagon_r83.cubeList.add(new ModelBox(octagon_r83, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r84 = new ModelRenderer(this);
				octagon_r84.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon22.addChild(octagon_r84);
				setRotationAngle(octagon_r84, 0.0F, -0.7854F, 0.0F);
				octagon_r84.cubeList.add(new ModelBox(octagon_r84, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon24 = new ModelRenderer(this);
				octagon24.setRotationPoint(0.0858F, -11.75F, -13.75F);
				side4.addChild(octagon24);
				setRotationAngle(octagon24, 0.0F, 0.0F, 1.5708F);
				octagon24.cubeList.add(new ModelBox(octagon24, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r85 = new ModelRenderer(this);
				octagon_r85.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon24.addChild(octagon_r85);
				setRotationAngle(octagon_r85, 0.0F, -2.3562F, 0.0F);
				octagon_r85.cubeList.add(new ModelBox(octagon_r85, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r86 = new ModelRenderer(this);
				octagon_r86.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon24.addChild(octagon_r86);
				setRotationAngle(octagon_r86, 0.0F, -1.5708F, 0.0F);
				octagon_r86.cubeList.add(new ModelBox(octagon_r86, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r87 = new ModelRenderer(this);
				octagon_r87.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon24.addChild(octagon_r87);
				setRotationAngle(octagon_r87, 0.0F, -0.7854F, 0.0F);
				octagon_r87.cubeList.add(new ModelBox(octagon_r87, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon25 = new ModelRenderer(this);
				octagon25.setRotationPoint(0.0858F, -5.75F, -13.75F);
				side4.addChild(octagon25);
				setRotationAngle(octagon25, 0.0F, 0.0F, 1.5708F);
				octagon25.cubeList.add(new ModelBox(octagon25, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r88 = new ModelRenderer(this);
				octagon_r88.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon25.addChild(octagon_r88);
				setRotationAngle(octagon_r88, 0.0F, -2.3562F, 0.0F);
				octagon_r88.cubeList.add(new ModelBox(octagon_r88, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r89 = new ModelRenderer(this);
				octagon_r89.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon25.addChild(octagon_r89);
				setRotationAngle(octagon_r89, 0.0F, -1.5708F, 0.0F);
				octagon_r89.cubeList.add(new ModelBox(octagon_r89, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon_r90 = new ModelRenderer(this);
				octagon_r90.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon25.addChild(octagon_r90);
				setRotationAngle(octagon_r90, 0.0F, -0.7854F, 0.0F);
				octagon_r90.cubeList.add(new ModelBox(octagon_r90, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, true));
		
				octagon26 = new ModelRenderer(this);
				octagon26.setRotationPoint(-6.0F, -4.0F, -12.0F);
				side4.addChild(octagon26);
				octagon26.cubeList.add(new ModelBox(octagon26, 6, 0, -0.4142F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon_r91 = new ModelRenderer(this);
				octagon_r91.setRotationPoint(0.0858F, 0.0F, 0.0F);
				octagon26.addChild(octagon_r91);
				setRotationAngle(octagon_r91, 0.0F, -2.3562F, 0.0F);
				octagon_r91.cubeList.add(new ModelBox(octagon_r91, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon_r92 = new ModelRenderer(this);
				octagon_r92.setRotationPoint(0.0858F, 0.0F, 0.0F);
				octagon26.addChild(octagon_r92);
				setRotationAngle(octagon_r92, 0.0F, -1.5708F, 0.0F);
				octagon_r92.cubeList.add(new ModelBox(octagon_r92, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon_r93 = new ModelRenderer(this);
				octagon_r93.setRotationPoint(0.0858F, 0.0F, 0.0F);
				octagon26.addChild(octagon_r93);
				setRotationAngle(octagon_r93, 0.0F, -0.7854F, 0.0F);
				octagon_r93.cubeList.add(new ModelBox(octagon_r93, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon27 = new ModelRenderer(this);
				octagon27.setRotationPoint(6.0F, -4.0F, -12.0F);
				side4.addChild(octagon27);
				octagon27.cubeList.add(new ModelBox(octagon27, 6, 0, -0.4142F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon_r94 = new ModelRenderer(this);
				octagon_r94.setRotationPoint(0.0858F, 0.0F, 0.0F);
				octagon27.addChild(octagon_r94);
				setRotationAngle(octagon_r94, 0.0F, -2.3562F, 0.0F);
				octagon_r94.cubeList.add(new ModelBox(octagon_r94, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon_r95 = new ModelRenderer(this);
				octagon_r95.setRotationPoint(0.0858F, 0.0F, 0.0F);
				octagon27.addChild(octagon_r95);
				setRotationAngle(octagon_r95, 0.0F, -1.5708F, 0.0F);
				octagon_r95.cubeList.add(new ModelBox(octagon_r95, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				octagon_r96 = new ModelRenderer(this);
				octagon_r96.setRotationPoint(0.0858F, 0.0F, 0.0F);
				octagon27.addChild(octagon_r96);
				setRotationAngle(octagon_r96, 0.0F, -0.7854F, 0.0F);
				octagon_r96.cubeList.add(new ModelBox(octagon_r96, 6, 0, -0.5F, -22.0F, -1.0F, 1, 26, 2, -0.15F, true));
		
				top = new ModelRenderer(this);
				top.setRotationPoint(0.0F, 0.0F, 0.0F);
				setRotationAngle(top, -1.5708F, 0.0F, 0.0F);
				
		
				octagon39 = new ModelRenderer(this);
				octagon39.setRotationPoint(4.25F, 11.85F, -23.75F);
				top.addChild(octagon39);
				octagon39.cubeList.add(new ModelBox(octagon39, 0, 0, -0.5858F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r97 = new ModelRenderer(this);
				octagon_r97.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon39.addChild(octagon_r97);
				setRotationAngle(octagon_r97, 0.0F, 2.3562F, 0.0F);
				octagon_r97.cubeList.add(new ModelBox(octagon_r97, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r98 = new ModelRenderer(this);
				octagon_r98.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon39.addChild(octagon_r98);
				setRotationAngle(octagon_r98, 0.0F, 1.5708F, 0.0F);
				octagon_r98.cubeList.add(new ModelBox(octagon_r98, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r99 = new ModelRenderer(this);
				octagon_r99.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon39.addChild(octagon_r99);
				setRotationAngle(octagon_r99, 0.0F, 0.7854F, 0.0F);
				octagon_r99.cubeList.add(new ModelBox(octagon_r99, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon40 = new ModelRenderer(this);
				octagon40.setRotationPoint(-0.0858F, -4.4F, -22.0F);
				top.addChild(octagon40);
				setRotationAngle(octagon40, 0.0F, 0.0F, -1.5708F);
				octagon40.cubeList.add(new ModelBox(octagon40, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r100 = new ModelRenderer(this);
				octagon_r100.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon40.addChild(octagon_r100);
				setRotationAngle(octagon_r100, 0.0F, 2.3562F, 0.0F);
				octagon_r100.cubeList.add(new ModelBox(octagon_r100, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r101 = new ModelRenderer(this);
				octagon_r101.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon40.addChild(octagon_r101);
				setRotationAngle(octagon_r101, 0.0F, 1.5708F, 0.0F);
				octagon_r101.cubeList.add(new ModelBox(octagon_r101, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r102 = new ModelRenderer(this);
				octagon_r102.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon40.addChild(octagon_r102);
				setRotationAngle(octagon_r102, 0.0F, 0.7854F, 0.0F);
				octagon_r102.cubeList.add(new ModelBox(octagon_r102, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon43 = new ModelRenderer(this);
				octagon43.setRotationPoint(-0.0858F, 4.1F, -22.0F);
				top.addChild(octagon43);
				setRotationAngle(octagon43, 0.0F, 0.0F, -1.5708F);
				octagon43.cubeList.add(new ModelBox(octagon43, 0, 0, -0.5F, -16.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r103 = new ModelRenderer(this);
				octagon_r103.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon43.addChild(octagon_r103);
				setRotationAngle(octagon_r103, 0.0F, 2.3562F, 0.0F);
				octagon_r103.cubeList.add(new ModelBox(octagon_r103, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r104 = new ModelRenderer(this);
				octagon_r104.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon43.addChild(octagon_r104);
				setRotationAngle(octagon_r104, 0.0F, 1.5708F, 0.0F);
				octagon_r104.cubeList.add(new ModelBox(octagon_r104, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r105 = new ModelRenderer(this);
				octagon_r105.setRotationPoint(0.0F, 12.0F, 0.0F);
				octagon43.addChild(octagon_r105);
				setRotationAngle(octagon_r105, 0.0F, 0.7854F, 0.0F);
				octagon_r105.cubeList.add(new ModelBox(octagon_r105, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon47 = new ModelRenderer(this);
				octagon47.setRotationPoint(-4.25F, 11.85F, -23.75F);
				top.addChild(octagon47);
				octagon47.cubeList.add(new ModelBox(octagon47, 0, 0, -0.5858F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r106 = new ModelRenderer(this);
				octagon_r106.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon47.addChild(octagon_r106);
				setRotationAngle(octagon_r106, 0.0F, 2.3562F, 0.0F);
				octagon_r106.cubeList.add(new ModelBox(octagon_r106, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r107 = new ModelRenderer(this);
				octagon_r107.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon47.addChild(octagon_r107);
				setRotationAngle(octagon_r107, 0.0F, 1.5708F, 0.0F);
				octagon_r107.cubeList.add(new ModelBox(octagon_r107, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
		
				octagon_r108 = new ModelRenderer(this);
				octagon_r108.setRotationPoint(-0.0858F, 0.0F, 0.0F);
				octagon47.addChild(octagon_r108);
				setRotationAngle(octagon_r108, 0.0F, 0.7854F, 0.0F);
				octagon_r108.cubeList.add(new ModelBox(octagon_r108, 0, 0, -0.5F, -28.0F, -1.0F, 1, 32, 2, -0.15F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				side1.render(f5);
				side2.render(f5);
				side3.render(f5);
				side4.render(f5);
				top.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
