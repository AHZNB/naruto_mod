
package net.narutomod.entity;

import net.narutomod.item.ItemClaw;
import net.narutomod.item.ItemKunaiBlade;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPuppetHundred extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 441;
	public static final int ENTITYID_RANGED = 442;

	public EntityPuppetHundred(ElementsNarutomodMod instance) {
		super(instance, 871);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "puppet_hundred"), ENTITYID).name("puppet_hundred")
				.tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityScroll.class)
				.id(new ResourceLocation("narutomod", "puppet_hundred_scroll"), ENTITYID_RANGED).name("puppet_hundred_scroll")
				.tracker(64, 3, true).build());
	}

	public static class EntityCustom extends EntityPuppet.Base {
		private static final DataParameter<Float> MODEL_SCALE = EntityDataManager.<Float>createKey(EntityCustom.class, DataSerializers.FLOAT);
		public static final float MAXHEALTH = 50.0f;
		private static final Vec3d offsetToOwner = new Vec3d(0.0d, 3.0d, 2.0d);
		public final int style;

		public EntityCustom(World worldIn) {
			super(worldIn);
			//this.setSize(0.6f, 2.0f);
			this.setEntityScale(0.9375f + (float)Math.abs(this.rand.nextGaussian()) * 0.5f);
			this.style = this.rand.nextInt(3);
		}

		public EntityCustom(EntityLivingBase ownerIn) {
			super(ownerIn);
			//this.setEntityScale(this.rand.nextFloat() + 0.9f);
			this.setEntityScale(0.9375f + (float)Math.abs(this.rand.nextGaussian()) * 0.5f);
			Vec3d vec = ownerIn.getLookVec();
			vec = ownerIn.getPositionVector().addVector(vec.x, 1d, vec.z);
			this.setLocationAndAngles(vec.x, vec.y, vec.z, ownerIn.rotationYaw, 0f);
			this.style = this.rand.nextInt(3);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			livingdata = super.onInitialSpawn(difficulty, livingdata);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(this.rand.nextBoolean() ? ItemClaw.block : ItemKunaiBlade.block));
			return livingdata;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(MODEL_SCALE, Float.valueOf(1.0F));
		}

		public float getEntityScale() {
			return ((Float)this.getDataManager().get(MODEL_SCALE)).floatValue();
		}

		public void setEntityScale(float scale) {
			if (!this.world.isRemote) {
				this.getDataManager().set(MODEL_SCALE, Float.valueOf(scale));
				this.setSize(0.6f * scale, 2.0f * scale);
			}
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (MODEL_SCALE.equals(key) && this.world.isRemote) {
				float scale = this.getEntityScale();
				this.setSize(0.6f * scale, 2.0f * scale);
			}
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAXHEALTH);
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected Vec3d getOffsetToOwner() {
			return this.offsetToOwner;
		}

		@Override
		@Nullable
		protected net.minecraft.util.EnumHandSide chakraStringAttachesTo() {
			return null;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 2.0d, true));
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setEntityScale(compound.getFloat("scale"));
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setFloat("scale", this.getEntityScale());
		}
	}

	public static class EntityScroll extends Entity {
		private final int openScrollTime = 30;
		private EntityLivingBase summoner;
		private final EntityCustom[] puppetEntity = new EntityCustom[50];
		private int spawnedPuppets;
		
		public EntityScroll(World a) {
			super(a);
			this.setSize(1.0f, 0.2f);
			for (int i = 0; i < this.puppetEntity.length; i++) {
				this.puppetEntity[i] = null;
			}
		}

		public EntityScroll(EntityLivingBase summonerIn) {
			this(summonerIn.world);
			this.summoner = summonerIn;
		}

		@Override
		protected void entityInit() {
		}

		protected EntityCustom getPuppetEntity(int index) {
			return this.puppetEntity[index];
		}

		protected int getMaxPuppets() {
			return this.puppetEntity.length;
		}

		protected int getSpawnedPuppets() {
			return this.spawnedPuppets;
		}

		protected boolean allPuppetsSpawned() {
			return this.spawnedPuppets == this.puppetEntity.length;
		}

		protected boolean allPuppetsDead() {
			for (int i = 0; i < this.puppetEntity.length; i++) {
				if (this.puppetEntity[i] != null && this.puppetEntity[i].isEntityAlive() && this.puppetEntity[i].getOwner() != null) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void setDead() {
			super.setDead();
			if (this.spawnedPuppets > 0) {
				for (int i = 0; i < this.spawnedPuppets; i++) {
					if (this.puppetEntity[i].isEntityAlive()) {
						ProcedureUtils.poofWithSmoke(this.puppetEntity[i]);
						this.puppetEntity[i].setDead();
					}
				}
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && (this.summoner == null || (this.allPuppetsSpawned() && this.allPuppetsDead()))) {
				this.setDead();
			} else if (this.ticksExisted > this.openScrollTime) {
				if (this.allPuppetsSpawned()) {
					if (this.summoner != null) {
						this.setPosition(this.summoner.posX, this.summoner.posY, this.summoner.posZ);
					}
					this.setInvisible(true);
				} else if (this.summoner != null && this.spawnedPuppets < this.puppetEntity.length) {
					this.puppetEntity[this.spawnedPuppets] = new EntityCustom(this.summoner);
					this.puppetEntity[this.spawnedPuppets].setLocationAndAngles(this.posX, this.posY, this.posZ, this.summoner.rotationYaw, 0f);
					this.puppetEntity[this.spawnedPuppets].onInitialSpawn(this.world.getDifficultyForLocation(this.getPosition()), null);
					this.world.spawnEntity(this.puppetEntity[this.spawnedPuppets]);
					if (this.summoner instanceof EntityLiving) {
						this.puppetEntity[this.spawnedPuppets].setAttackTarget(((EntityLiving)this.summoner).getAttackTarget());
					}
					ProcedureUtils.poofWithSmoke(this.puppetEntity[this.spawnedPuppets]);
					++this.spawnedPuppets;
				}
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntityScroll.class, renderManager -> new RenderScroll(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityPuppet.ClientClass.Renderer<EntityCustom> {
			private final ResourceLocation[] texture = {
			 new ResourceLocation("narutomod:textures/puppet_hundred1.png"),
			 new ResourceLocation("narutomod:textures/puppet_hundred2.png"),
			 new ResourceLocation("narutomod:textures/puppet_hundred3.png")
			};
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelPuppetHundred(), 0.5f);
				this.addLayer(new LayerHeldItem(this));
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				float f = entity.getEntityScale();
				GlStateManager.scale(f, f, f);
				for (int i = 0; i < ((ModelPuppetHundred)this.mainModel).hair.length; i++) {
					((ModelPuppetHundred)this.mainModel).hair[i].showModel = false;
				}
				((ModelPuppetHundred)this.mainModel).hair[entity.style].showModel = true;
			}
			
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture[entity.style];
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderScroll extends EntityPuppet.ClientClass.RenderScroll<EntityScroll> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/scroll_hundred.png");
	
			public RenderScroll(RenderManager renderManager) {
				super(renderManager);
			}

			@Override
			public void doRender(EntityScroll entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.isInvisible()) {
					return;
				}
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityScroll entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelPuppetHundred extends ModelBiped {
			//private final ModelRenderer bipedHead;
			private final ModelRenderer jaw;
			//private final ModelRenderer bipedHeadwear;
			private final ModelRenderer[] hair = new ModelRenderer[3];
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer bone9;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone13;
			private final ModelRenderer bone14;
			//private final ModelRenderer hair2;
			private final ModelRenderer cube_r7;
			private final ModelRenderer cube_r8;
			//private final ModelRenderer hair3;
			private final ModelRenderer cube_r9;
			private final ModelRenderer cube_r10;
			private final ModelRenderer cube_r11;
			private final ModelRenderer cube_r12;
			//private final ModelRenderer bipedBody;
			private final ModelRenderer collar;
			private final ModelRenderer collar1;
			private final ModelRenderer collar2;
			private final ModelRenderer collar3;
			private final ModelRenderer collar4;
			private final ModelRenderer collar5;
			private final ModelRenderer collar6;
			private final ModelRenderer collar7;
			private final ModelRenderer collar8;
			private final ModelRenderer collar9;
			private final ModelRenderer collar10;
			private final ModelRenderer collar11;
			private final ModelRenderer collar12;
			private final ModelRenderer collar13;
			private final ModelRenderer collar14;
			private final ModelRenderer collar15;
			private final ModelRenderer collar16;
			private final ModelRenderer bone;
			private final ModelRenderer bone2;
			//private final ModelRenderer bipedRightLeg;
			//private final ModelRenderer bipedLeftLeg;
			//private final ModelRenderer bipedRightArm;
			//private final ModelRenderer bipedLeftArm;
			private int style;
			
			public ModelPuppetHundred() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, -1.0F, 0.0F);
				bipedHead.addChild(jaw);
				jaw.cubeList.add(new ModelBox(jaw, 50, 24, -1.5F, -1.0F, -4.01F, 3, 2, 4, 0.0F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				hair[0] = new ModelRenderer(this);
				hair[0].setRotationPoint(0.0F, -5.5F, -5.5F);
				bipedHeadwear.addChild(hair[0]);
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-1.0F, -0.75F, -0.25F);
				hair[0].addChild(bone3);
				setRotationAngle(bone3, -0.4363F, -0.0436F, -0.1745F);
				bone3.cubeList.add(new ModelBox(bone3, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-1.0F, -2.0F, 1.0F);
				hair[0].addChild(bone4);
				setRotationAngle(bone4, -0.7854F, -0.1309F, -0.3054F);
				bone4.cubeList.add(new ModelBox(bone4, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-1.0F, -3.0F, 3.0F);
				hair[0].addChild(bone5);
				setRotationAngle(bone5, -1.0472F, -0.2182F, -0.3491F);
				bone5.cubeList.add(new ModelBox(bone5, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(-1.25F, -3.25F, 5.0F);
				hair[0].addChild(bone6);
				setRotationAngle(bone6, -1.309F, -0.1309F, -0.3054F);
				bone6.cubeList.add(new ModelBox(bone6, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(-1.25F, -4.25F, 9.75F);
				hair[0].addChild(bone7);
				setRotationAngle(bone7, -2.618F, -0.0436F, -0.0873F);
				bone7.cubeList.add(new ModelBox(bone7, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(-1.25F, -2.0F, 10.5F);
				hair[0].addChild(bone8);
				setRotationAngle(bone8, -2.9671F, 0.0F, -0.0436F);
				bone8.cubeList.add(new ModelBox(bone8, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(1.0F, -0.75F, -0.25F);
				hair[0].addChild(bone9);
				setRotationAngle(bone9, -0.4363F, 0.0436F, 0.1745F);
				bone9.cubeList.add(new ModelBox(bone9, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(1.0F, -2.0F, 1.0F);
				hair[0].addChild(bone10);
				setRotationAngle(bone10, -0.7854F, 0.1309F, 0.3054F);
				bone10.cubeList.add(new ModelBox(bone10, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(1.0F, -3.0F, 3.0F);
				hair[0].addChild(bone11);
				setRotationAngle(bone11, -1.0472F, 0.2182F, 0.3491F);
				bone11.cubeList.add(new ModelBox(bone11, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(1.25F, -3.25F, 5.0F);
				hair[0].addChild(bone12);
				setRotationAngle(bone12, -1.309F, 0.1309F, 0.3054F);
				bone12.cubeList.add(new ModelBox(bone12, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(1.25F, -4.25F, 9.75F);
				hair[0].addChild(bone13);
				setRotationAngle(bone13, -2.618F, 0.0436F, 0.0873F);
				bone13.cubeList.add(new ModelBox(bone13, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, true));
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(1.25F, -2.0F, 10.5F);
				hair[0].addChild(bone14);
				setRotationAngle(bone14, -2.9671F, 0.0F, 0.0436F);
				bone14.cubeList.add(new ModelBox(bone14, 32, 11, -4.0F, -6.0F, 0.25F, 8, 6, 7, -0.5F, false));
				hair[1] = new ModelRenderer(this);
				hair[1].setRotationPoint(0.0F, 0.0F, 1.75F);
				bipedHeadwear.addChild(hair[1]);
				setRotationAngle(hair[1], 0.5236F, 0.0F, 0.0F);
				cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(0.0F, -5.5F, 0.75F);
				hair[1].addChild(cube_r7);
				setRotationAngle(cube_r7, 0.0F, 0.2618F, 0.0873F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 32, 11, -4.0F, -4.0F, -1.0F, 8, 6, 7, 0.2F, true));
				cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(0.0F, -6.5F, 0.75F);
				hair[1].addChild(cube_r8);
				setRotationAngle(cube_r8, 0.0F, -0.2618F, -0.0873F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 32, 11, -4.0F, -3.0F, -1.0F, 8, 6, 7, 0.2F, true));
				hair[2] = new ModelRenderer(this);
				hair[2].setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.addChild(hair[2]);
				setRotationAngle(hair[2], -0.2618F, 0.0F, 0.0F);
				cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(1.0F, -9.75F, 1.0F);
				hair[2].addChild(cube_r9);
				setRotationAngle(cube_r9, -0.5236F, 0.0F, 0.5236F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 32, 11, -4.0F, -3.0F, -3.5F, 8, 6, 7, -1.0F, false));
				cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(1.0F, -9.75F, -0.5F);
				hair[2].addChild(cube_r10);
				setRotationAngle(cube_r10, 0.0F, 0.0F, 0.3491F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 32, 11, -4.0F, -3.0F, -3.5F, 8, 6, 7, -1.0F, true));
				cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(-1.0F, -9.75F, 1.0F);
				hair[2].addChild(cube_r11);
				setRotationAngle(cube_r11, -0.5236F, 0.0F, -0.5236F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 32, 11, -4.0F, -3.0F, -3.5F, 8, 6, 7, -1.0F, false));
				cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(-1.0F, -9.75F, -0.5F);
				hair[2].addChild(cube_r12);
				setRotationAngle(cube_r12, 0.0F, 0.0F, -0.3491F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 32, 11, -4.0F, -3.0F, -3.5F, 8, 6, 7, -1.0F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				collar = new ModelRenderer(this);
				collar.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(collar);
				collar1 = new ModelRenderer(this);
				collar1.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar1);
				setRotationAngle(collar1, -1.0472F, 0.0F, 0.0F);
				collar1.cubeList.add(new ModelBox(collar1, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
				collar2 = new ModelRenderer(this);
				collar2.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar2);
				setRotationAngle(collar2, -1.0908F, 0.0F, 0.0873F);
				collar2.cubeList.add(new ModelBox(collar2, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
				collar3 = new ModelRenderer(this);
				collar3.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar3);
				setRotationAngle(collar3, -1.1345F, 0.0F, -0.0873F);
				collar3.cubeList.add(new ModelBox(collar3, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
				collar4 = new ModelRenderer(this);
				collar4.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar4);
				setRotationAngle(collar4, -1.1781F, 0.0F, 0.0873F);
				collar4.cubeList.add(new ModelBox(collar4, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
				collar5 = new ModelRenderer(this);
				collar5.setRotationPoint(0.0F, -0.116F, -2.634F);
				collar.addChild(collar5);
				setRotationAngle(collar5, -1.2217F, 0.0F, -0.0873F);
				collar5.cubeList.add(new ModelBox(collar5, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
				collar6 = new ModelRenderer(this);
				collar6.setRotationPoint(0.0F, -0.116F, -2.634F);
				collar.addChild(collar6);
				setRotationAngle(collar6, -1.2654F, 0.0F, 0.0873F);
				collar6.cubeList.add(new ModelBox(collar6, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
				collar7 = new ModelRenderer(this);
				collar7.setRotationPoint(0.0F, -0.116F, -2.634F);
				collar.addChild(collar7);
				setRotationAngle(collar7, -1.309F, 0.0F, -0.0873F);
				collar7.cubeList.add(new ModelBox(collar7, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
				collar8 = new ModelRenderer(this);
				collar8.setRotationPoint(0.0F, -0.116F, -2.634F);
				collar.addChild(collar8);
				setRotationAngle(collar8, -1.3526F, 0.0F, 0.0873F);
				collar8.cubeList.add(new ModelBox(collar8, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
				collar9 = new ModelRenderer(this);
				collar9.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar9);
				setRotationAngle(collar9, -1.3963F, 0.0F, -0.0873F);
				collar9.cubeList.add(new ModelBox(collar9, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
				collar10 = new ModelRenderer(this);
				collar10.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar10);
				setRotationAngle(collar10, -1.4399F, 0.0F, 0.0873F);
				collar10.cubeList.add(new ModelBox(collar10, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
				collar11 = new ModelRenderer(this);
				collar11.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar11);
				setRotationAngle(collar11, -1.4835F, 0.0F, -0.0873F);
				collar11.cubeList.add(new ModelBox(collar11, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
				collar12 = new ModelRenderer(this);
				collar12.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar12);
				setRotationAngle(collar12, -1.5272F, 0.0F, 0.0873F);
				collar12.cubeList.add(new ModelBox(collar12, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
				collar13 = new ModelRenderer(this);
				collar13.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar13);
				setRotationAngle(collar13, -1.5708F, 0.0F, -0.0873F);
				collar13.cubeList.add(new ModelBox(collar13, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
				collar14 = new ModelRenderer(this);
				collar14.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar14);
				setRotationAngle(collar14, -1.6144F, 0.0F, 0.0873F);
				collar14.cubeList.add(new ModelBox(collar14, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
				collar15 = new ModelRenderer(this);
				collar15.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar15);
				setRotationAngle(collar15, -1.6581F, 0.0F, -0.0873F);
				collar15.cubeList.add(new ModelBox(collar15, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, true));
				collar16 = new ModelRenderer(this);
				collar16.setRotationPoint(0.0F, -0.116F, -2.884F);
				collar.addChild(collar16);
				setRotationAngle(collar16, -1.7017F, 0.0F, 0.0F);
				collar16.cubeList.add(new ModelBox(collar16, 32, 0, -7.0F, -10.0F, 0.0F, 14, 10, 1, 0.0F, false));
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(bone);
				setRotationAngle(bone, -0.0873F, 0.0F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 16, 36, -4.0F, 0.0F, -2.0F, 8, 24, 4, 0.5F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(bone2);
				setRotationAngle(bone2, 0.0873F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 40, 36, -4.0F, 0.0F, -2.0F, 8, 24, 4, 0.5F, false));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedBody.addChild(bipedRightLeg);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedBody.addChild(bipedLeftLeg);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 48, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}

			@Override
			public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.bipedHead.render(scale);
				this.bipedBody.render(scale);
				this.bipedRightArm.render(scale);
				this.bipedLeftArm.render(scale);
				this.bipedHeadwear.render(scale);
				GlStateManager.disableBlend();
			}

			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(0f, 0f, f2, f3, f4, f5, e);
				double d = ((EntityCustom)e).getVelocity();
				if (d > 0.001d && ((EntityCustom)e).isMovingForward()) {
					float fa = MathHelper.clamp((float)d * 2.5F, 0.0F, 1.0F);
					bipedBody.rotateAngleX += fa * 1.0472F;
					collar.rotateAngleX = fa * -0.2618F;
					if (this.swingProgress <= 0.0F && rightArmPose == ModelBiped.ArmPose.EMPTY) {
						bipedRightArm.rotateAngleZ += fa * 1.3963F;
					}
					if (leftArmPose == ModelBiped.ArmPose.EMPTY) {
						bipedLeftArm.rotateAngleZ += fa * -1.3963F;
					}
					bipedRightLeg.rotateAngleX = 0.0F;
					bipedLeftLeg.rotateAngleX = 0.0F;
				}
			}
		}
	}
}
