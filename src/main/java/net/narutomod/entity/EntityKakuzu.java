
package net.narutomod.entity;

import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;

import java.util.Random;
import javax.annotation.Nullable;
//import net.minecraft.network.datasync.DataParameter;
//import net.minecraft.network.datasync.EntityDataManager;
//import net.minecraft.network.datasync.DataSerializers;

@ElementsNarutomodMod.ModElement.Tag
public class EntityKakuzu extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 469;
	public static final int ENTITYID_RANGED = 470;

	public EntityKakuzu(ElementsNarutomodMod instance) {
		super(instance, 899);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "kakuzu"), ENTITYID)
				.name("kakuzu").tracker(64, 3, true).egg(-16777216, -6750157).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob {
		//private static final DataParameter<Integer> ROBE_OFF_TICKS = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private final int robeOffEnd = 20;
		private int robeOffProgress;
		private int robeOffDirection;

		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.setSize(0.6f, 2.0f);
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false, this.playerTargetSelectorAkatsuki));
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobe.body));
			//this.setItemToInventory(new ItemStack(ItemAkatsukiRobe.body), 1);
			return super.onInitialSpawn(difficulty, livingdata);
		}

		/*@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(ROBE_OFF_TICKS, Integer.valueOf(-1));
		}

		private void setRobeOffTicks(int ticks) {
			this.dataManager.set(ROBE_OFF_TICKS, Integer.valueOf(ticks));
		}
	
		public int getRobeOffTicks() {
			return ((Integer)this.getDataManager().get(ROBE_OFF_TICKS)).intValue();
		}*/

		protected void takeOffRobe(boolean off) {
			//this.setRobeOffTicks(off ? 0 : -1);
			if (off) {
				if (this.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ItemAkatsukiRobe.body) {
					this.swapWithInventory(EntityEquipmentSlot.CHEST, 1);
				}
				this.robeOffDirection = 1;
				this.world.setEntityState(this, (byte)103);
			} else {
				this.robeOffDirection = -1;
				this.world.setEntityState(this, (byte)104);
			}
		}

		public boolean isRobeOff() {
			//return this.getRobeOffTicks() >= 0;
			return this.robeOffProgress > this.robeOffEnd / 2;
		}

		@Override
		protected void updateArmSwingProgress() {
			super.updateArmSwingProgress();
			if (this.robeOffDirection != 0) {
				if ((this.robeOffDirection > 0 && this.robeOffProgress < this.robeOffEnd) || (this.robeOffDirection < 0 && this.robeOffProgress > 0)) {
					this.robeOffProgress += this.robeOffDirection;
				} else {
					if (this.robeOffDirection < 0 && this.getItemFromInventory(1).getItem() == ItemAkatsukiRobe.body) {
						this.swapWithInventory(EntityEquipmentSlot.CHEST, 1);
					}
					this.robeOffDirection = 0;
				}
			}
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(2, new EntityNinjaMob.AILeapAtTarget(this, 1.0F));
			this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.2d, true));
			this.tasks.addTask(5, new EntityAIWatchClosest2(this, EntityPlayer.class, 32.0F, 1.0F));
			this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityNinjaMob.Base.class, 24.0F) {
				@Override
				public boolean shouldExecute() {
					return super.shouldExecute() && !this.entity.isOnSameTeam(this.closestEntity);
				}
			});
			this.tasks.addTask(7, new EntityAIWander(this, 0.5d));
			this.tasks.addTask(8, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			//if (this.ticksExisted == 40) {
			//	this.takeOffRobe(true);
			//}
			//if (this.ticksExisted == 100) {
			//	this.takeOffRobe(false);
			//}
			super.updateAITasks();
		}

		@Override
		protected void damageEntity(DamageSource damageSrc, float damageAmount) {
			if (this.isEntityInvulnerable(damageSrc)) {
				return;
			}
			if (damageSrc.getImmediateSource() instanceof ItemJutsu.IJutsu) {
				ItemJutsu.JutsuEnum.Type jutsuType = ((ItemJutsu.IJutsu)damageSrc.getImmediateSource()).getJutsuType();
				if (jutsuType == ItemJutsu.JutsuEnum.Type.RAITON) {
					super.damageEntity(damageSrc, damageAmount);
					return;
				}
			}
			if (damageSrc.isDamageAbsolute() || damageSrc.isUnblockable()) {
				super.damageEntity(damageSrc, damageAmount);
				return;
			}
			super.damageEntity(damageSrc, damageAmount * 0.2f);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void handleStatusUpdate(byte id) {
			if (id == 103) {
				this.takeOffRobe(true);
			} else if (id == 104) {
				this.takeOffRobe(false);
			} else {
				super.handleStatusUpdate(id);
			}
		}

		private float getRobeOffProgress(float partialTicks) {
			return MathHelper.clamp(((float)this.robeOffProgress + (float)this.robeOffDirection * partialTicks) / this.robeOffEnd, 0.0f, 1.0f);
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere() && (int)this.posY >= this.world.getSeaLevel() && this.world.canSeeSky(this.getPosition())
			 && this.world.getEntities(EntityCustom.class, EntitySelectors.IS_ALIVE).isEmpty()
			 && !EntityNinjaMob.SpawnData.spawnedRecentlyHere(this, 36000);
			 //&& this.rand.nextInt(5) == 0;
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || EntityNinjaMob.TeamAkatsuki.contains(entityIn.getClass());
		}

		@Override
		public boolean isNonBoss() {
			return false;
		}

		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

		@Override
		public void removeTrackingPlayer(EntityPlayerMP player) {
			super.removeTrackingPlayer(player);
			if (this.bossInfo.getPlayers().contains(player)) {
				this.bossInfo.removePlayer(player);
			}
		}

		private void trackAttackedPlayers() {
			Entity entity = this.getAttackingEntity();
			if (entity instanceof EntityPlayerMP || (entity = this.getAttackTarget()) instanceof EntityPlayerMP) {
				this.bossInfo.addPlayer((EntityPlayerMP)entity);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.trackAttackedPlayers();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/kakuzu.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelKakuzu());
			}

			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				((ModelKakuzu)this.mainModel).clothesOffProgress = entity.getRobeOffProgress(partialTicks);
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				GlStateManager.scale(0.9375F, 1.0F, 0.9375F);
			}

			@Override
			public void transformHeldFull3DItemLayer() {
				GlStateManager.translate(0.0F, 0.1875F, 0.0F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 4.11.0
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelKakuzu extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer hair;
			private final ModelRenderer jaw;
			private final ModelRenderer mouthThreads;
			private final ModelRenderer[][] mouthThread = new ModelRenderer[25][9];
			private final ModelRenderer bodywear;
			private final ModelRenderer maskWater;
			private final ModelRenderer maskWind;
			private final ModelRenderer maskFire;
			private final ModelRenderer maskLightning;
			private final ModelRenderer backThreads;
			private final ModelRenderer[][] backThread = new ModelRenderer[50][12];
			private final ModelRenderer rightForeArm;
			private final ModelRenderer[] rightArmThread = new ModelRenderer[4];
			private final ModelRenderer[][] rightArmString = new ModelRenderer[6][6];
			private final float[][] rightArmStringSway = new float[rightArmString.length][rightArmString[0].length];
			private final ModelRenderer leftForeArm;
			private final ModelRenderer[] leftArmThread = new ModelRenderer[4];
			private final ModelRenderer[][] leftArmString = new ModelRenderer[6][6];
			private final float[][] leftArmStringSway = new float[leftArmString.length][leftArmString[0].length];
			private final ModelRenderer rightLegwear;
			private final ModelRenderer leftLegwear;
			private final Random rand = new Random();
			private final float[][] mouthHairSway = new float[mouthThread.length][mouthThread[0].length];
			private final float[][] backHairSway = new float[backThread.length][backThread[0].length];
			private float clothesOffProgress;
			
			public ModelKakuzu() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.addChild(hair);
				hair.cubeList.add(new ModelBox(hair, 24, 4, -4.0F, 0.0F, 4.0F, 8, 4, 0, 0.0F, false));
				ModelRenderer bipedHead_r1 = new ModelRenderer(this);
				bipedHead_r1.setRotationPoint(4.0F, 0.0F, 0.0F);
				hair.addChild(bipedHead_r1);
				setRotationAngle(bipedHead_r1, 0.0F, 0.0F, -0.0436F);
				bipedHead_r1.cubeList.add(new ModelBox(bipedHead_r1, 24, -8, 0.0F, 0.0F, -4.0F, 0, 4, 8, 0.0F, true));
				ModelRenderer bipedHead_r2 = new ModelRenderer(this);
				bipedHead_r2.setRotationPoint(-4.0F, 0.0F, 0.0F);
				hair.addChild(bipedHead_r2);
				setRotationAngle(bipedHead_r2, 0.0F, 0.0F, 0.0436F);
				bipedHead_r2.cubeList.add(new ModelBox(bipedHead_r2, 24, -8, 0.0F, 0.0F, -4.0F, 0, 4, 8, 0.0F, false));
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, 0.0F, -2.0F);
				bipedHead.addChild(jaw);
				jaw.cubeList.add(new ModelBox(jaw, 52, 16, -1.5F, -1.0F, -2.0F, 3, 1, 2, 0.0F, false));

				mouthThreads = new ModelRenderer(this);
				mouthThreads.setRotationPoint(0.0F, -0.75F, -0.5F);
				jaw.addChild(mouthThreads);
				setRotationAngle(mouthThreads, -0.1745F, 0.0F, 0.0F);
				for (int i = 0; i < mouthThread.length; i++) {
					for (int j = 0; j < mouthThread[i].length; j++) {
						mouthThread[i][j] = new ModelRenderer(this);
						if (j == 0) {
							mouthThread[i][j].setRotationPoint(0.0F, -0.1F, 0.0F);
							mouthThreads.addChild(mouthThread[i][j]);
							setRotationAngle(mouthThread[i][j], (this.rand.nextFloat()-0.5F) * 0.1309F, (this.rand.nextFloat()-0.5F) * 1.0472F, 0.0F);
						} else {
							mouthThread[i][j].setRotationPoint(0.0F, 0.0F, -1.0F);
							mouthThread[i][j-1].addChild(mouthThread[i][j]);
							setRotationAngle(mouthThread[i][j], 0.1309F, 0.0F, 0.0F);
						}
						mouthThread[i][j].cubeList.add(new ModelBox(mouthThread[i][j], 0, 0, -1.0F, -0.5F, -1.5F, 2, 1, 2, -0.45F, false));
						mouthHairSway[i][j] = (this.rand.nextFloat()-0.5F) * 0.3491F;
					}
				}

				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bodywear = new ModelRenderer(this);
				bodywear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(bodywear);
				bodywear.cubeList.add(new ModelBox(bodywear, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
				maskWater = new ModelRenderer(this);
				maskWater.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(maskWater);
				maskWater.cubeList.add(new ModelBox(maskWater, 0, 57, -1.5F, -1.0F, 0.65F, 7, 7, 0, -1.5F, false));
				maskWind = new ModelRenderer(this);
				maskWind.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(maskWind);
				maskWind.cubeList.add(new ModelBox(maskWind, 0, 50, -5.5F, 3.6F, 0.65F, 7, 7, 0, -1.5F, false));
				maskFire = new ModelRenderer(this);
				maskFire.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(maskFire);
				maskFire.cubeList.add(new ModelBox(maskFire, 14, 50, -5.6F, -0.85F, 0.65F, 7, 7, 0, -1.5F, false));
				maskLightning = new ModelRenderer(this);
				maskLightning.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(maskLightning);
				maskLightning.cubeList.add(new ModelBox(maskLightning, 14, 57, -1.6F, 3.5F, 0.65F, 7, 7, 0, -1.5F, false));

				backThreads = new ModelRenderer(this);
				backThreads.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(backThreads);
				for (int i = 0; i < backThread.length; i++) {
					for (int j = 0; j < backThread[i].length; j++) {
						backThread[i][j] = new ModelRenderer(this);
						if (j == 0) {
							float rotateX = (this.rand.nextFloat()-0.5F) * 1.5708F - 0.6109F;
							float rotateY = (this.rand.nextFloat()-0.5F) * 2.618F;
							float f = MathHelper.sqrt(rotateX * rotateX + rotateY * rotateY);
							backThread[i][j].setRotationPoint(0.0F, 4.0F, f * 1.2F);
							backThreads.addChild(backThread[i][j]);
							setRotationAngle(backThread[i][j], rotateX, rotateY, 0.0F);
						} else {
							backThread[i][j].setRotationPoint(0.0F, 0.0F, 3.0F);
							backThread[i][j-1].addChild(backThread[i][j]);
							setRotationAngle(backThread[i][j], 0.0873F, 0.0F, 0.0F);
						}
						backThread[i][j].cubeList.add(new ModelBox(backThread[i][j], 40, 41, -3.0F, -3.0F, 0.0F, 6, 6, 4, 0.0F, false));
						backThread[i][j].cubeList.add(new ModelBox(backThread[i][j], 40, 41, -3.0F, -3.0F, 0.0F, 6, 6, 4, -1.0F, true));
						backHairSway[i][j] = (0.0873F + this.rand.nextFloat() * 0.0873F) * (j % 2 == 0 ? -1.0F : 1.0F);
					}
				}

				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));
				rightArmThread[0] = new ModelRenderer(this);
				rightArmThread[0].setRotationPoint(-1.0F, 4.0F, 0.0F);
				bipedRightArm.addChild(rightArmThread[0]);
				rightArmThread[0].cubeList.add(new ModelBox(rightArmThread[0], 40, 36, -1.5F, 0.0F, -1.5F, 3, 2, 3, 0.1F, false));
				for (int i = 1; i < rightArmThread.length; i++) {
					rightArmThread[i] = new ModelRenderer(this);
					rightArmThread[i].setRotationPoint(0.0F, 1.5F, 0.0F);
					rightArmThread[i-1].addChild(rightArmThread[i]);
					setRotationAngle(rightArmThread[i], -0.0873F, 0.0F, -0.0873F);
					rightArmThread[i].cubeList.add(new ModelBox(rightArmThread[i], 40, 36, -1.5F, 0.0F, -1.5F, 3, 2, 3, 0.1F, false));
				}
				for (int i = 0; i < rightArmString.length; i++) {
					rightArmString[i][0] = new ModelRenderer(this);
					rightArmString[i][0].setRotationPoint(0.0F, -1.0F, 0.0F);
					rightArmThread[0].addChild(rightArmString[i][0]);
					setRotationAngle(rightArmString[i][0], -0.7854F, (this.rand.nextFloat()-0.5F) * 6.2832F, 0.0F);
					rightArmString[i][0].cubeList.add(new ModelBox(rightArmString[i][0], 10, 0, -0.5F, -0.5F, -0.5F, 1, 4, 1, -0.45F, false));
					for (int j = 1; j < rightArmString[i].length; j++) {
						rightArmString[i][j] = new ModelRenderer(this);
						rightArmString[i][j].setRotationPoint(0.0F, 3.0F, 0.0F);
						rightArmString[i][j-1].addChild(rightArmString[i][j]);
						rightArmString[i][j].cubeList.add(new ModelBox(rightArmString[i][j], 10, 0, -0.5F, -0.5F, -0.5F, 1, 4, 1, -0.45F, false));
						rightArmStringSway[i][j] = this.rand.nextFloat() * 0.2618F;
					}
				}
				ModelRenderer rightForeArm1 = new ModelRenderer(this);
				rightForeArm1.setRotationPoint(0.0F, 1.5F, 0.0F);
				rightArmThread[rightArmThread.length-1].addChild(rightForeArm1);
				rightForeArm1.cubeList.add(new ModelBox(rightForeArm1, 40, 26, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));
				rightForeArm = new ModelRenderer(this);
				rightForeArm.setRotationPoint(-1.0F, 4.0F, 0.0F);
				bipedRightArm.addChild(rightForeArm);
				rightForeArm.cubeList.add(new ModelBox(rightForeArm, 40, 26, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));

				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, true));
				leftArmThread[0] = new ModelRenderer(this);
				leftArmThread[0].setRotationPoint(1.0F, 4.0F, 0.0F);
				bipedLeftArm.addChild(leftArmThread[0]);
				leftArmThread[0].cubeList.add(new ModelBox(leftArmThread[0], 40, 36, -1.5F, 0.0F, -1.5F, 3, 2, 3, 0.1F, true));
				for (int i = 1; i < leftArmThread.length; i++) {
					leftArmThread[i] = new ModelRenderer(this);
					leftArmThread[i].setRotationPoint(0.0F, 1.5F, 0.0F);
					leftArmThread[i-1].addChild(leftArmThread[i]);
					setRotationAngle(leftArmThread[i], 0.0873F, 0.0F, 0.0873F);
					leftArmThread[i].cubeList.add(new ModelBox(leftArmThread[i], 40, 36, -1.5F, 0.0F, -1.5F, 3, 2, 3, 0.1F, true));
				}
				for (int i = 0; i < leftArmString.length; i++) {
					leftArmString[i][0] = new ModelRenderer(this);
					leftArmString[i][0].setRotationPoint(0.0F, -1.0F, 0.0F);
					leftArmThread[0].addChild(leftArmString[i][0]);
					setRotationAngle(leftArmString[i][0], -0.7854F, (this.rand.nextFloat()-0.5F) * 6.2832F, 0.0F);
					leftArmString[i][0].cubeList.add(new ModelBox(leftArmString[i][0], 10, 0, -0.5F, -0.5F, -0.5F, 1, 4, 1, -0.45F, false));
					for (int j = 1; j < leftArmString[i].length; j++) {
						leftArmString[i][j] = new ModelRenderer(this);
						leftArmString[i][j].setRotationPoint(0.0F, 3.0F, 0.0F);
						leftArmString[i][j-1].addChild(leftArmString[i][j]);
						leftArmString[i][j].cubeList.add(new ModelBox(leftArmString[i][j], 10, 0, -0.5F, -0.5F, -0.5F, 1, 4, 1, -0.45F, false));
						leftArmStringSway[i][j] = this.rand.nextFloat() * 0.2618F;
					}
				}
				ModelRenderer leftForeArm1 = new ModelRenderer(this);
				leftForeArm1.setRotationPoint(0.0F, 1.5F, 0.0F);
				leftArmThread[leftArmThread.length-1].addChild(leftForeArm1);
				leftForeArm1.cubeList.add(new ModelBox(leftForeArm1, 40, 26, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, true));
				leftForeArm = new ModelRenderer(this);
				leftForeArm.setRotationPoint(1.0F, 4.0F, 0.0F);
				bipedLeftArm.addChild(leftForeArm);
				leftForeArm.cubeList.add(new ModelBox(leftForeArm, 40, 26, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, true));

				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				rightLegwear = new ModelRenderer(this);
				rightLegwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightLeg.addChild(rightLegwear);
				rightLegwear.cubeList.add(new ModelBox(rightLegwear, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				leftLegwear = new ModelRenderer(this);
				leftLegwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftLeg.addChild(leftLegwear);
				leftLegwear.cubeList.add(new ModelBox(leftLegwear, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, true));
			}

			@Override
			public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
				super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
				if (this.clothesOffProgress > 0.0F) {
					hair.showModel = true;
					bipedHeadwear.showModel = false;
					bodywear.showModel = false;
					mouthThreads.showModel = true;
					backThreads.showModel = true;
					rightForeArm.showModel = false;
					rightArmThread[0].showModel = true;
					leftForeArm.showModel = false;
					leftArmThread[0].showModel = true;
					jaw.rotateAngleX = 0.2618F;
					bipedRightArm.rotationPointY = 3.0F;
					bipedLeftArm.rotationPointY = 3.0F;
					bipedRightArm.rotateAngleZ += 0.7854F;
					bipedLeftArm.rotateAngleZ -= 0.7854F;
					float fsin = MathHelper.sin(ageInTicks * 0.09F);
					float fcos = MathHelper.cos(ageInTicks * 0.09F);
					for (int i = 0; i < mouthThread.length; i++) {
						for (int j = 1; j < mouthThread[i].length; j++) {
							mouthThread[i][j].rotateAngleX = 0.1309F + fcos * mouthHairSway[i][j];
							mouthThread[i][j].rotateAngleY = fsin * mouthHairSway[i][j];
							mouthThread[i][j].showModel = (float)j / (mouthThread[i].length - 1) <= this.clothesOffProgress;
						}
					}
					for (int i = 0; i < backThread.length; i++) {
						for (int j = 1; j < backThread[i].length; j++) {
							backThread[i][j].rotateAngleX = 0.1745F + fcos * backHairSway[i][j];
							backThread[i][j].rotateAngleY = fsin * backHairSway[i][j] * (0.9F + (float)j / backThread[i].length);
							backThread[i][j].showModel = (float)j / (backThread[i].length - 1) <= this.clothesOffProgress;
						}
					}
					fsin = MathHelper.sin(ageInTicks * 0.067F);
					fcos = MathHelper.cos(ageInTicks * 0.067F);
					for (int i = 1; i < rightArmThread.length; i++) {
						rightArmThread[i].rotateAngleX = -0.0873F + fcos * 0.0437F;
						rightArmThread[i].rotateAngleZ = -0.0873F + fsin * 0.0437F;
						leftArmThread[i].rotateAngleX = -0.0873F + fsin * 0.0437F;
						leftArmThread[i].rotateAngleZ = 0.0873F + fcos * 0.0437F;
					}
					for (int i = 0; i < rightArmString.length; i++) {
						for (int j = 1; j < rightArmString[i].length; j++) {
							boolean show = (float)j / (rightArmString[i].length - 1) <= this.clothesOffProgress;
							rightArmString[i][j].rotateAngleX = MathHelper.sin(ageInTicks * 0.2F + i) * rightArmStringSway[i][j];
							rightArmString[i][j].rotateAngleY = MathHelper.cos(ageInTicks * 0.3F + i) * rightArmStringSway[i][j];
							rightArmString[i][j].rotateAngleZ = MathHelper.sin(ageInTicks * 0.4F + i) * rightArmStringSway[i][j];
							rightArmString[i][j].showModel = show;
							leftArmString[i][j].rotateAngleX = MathHelper.sin(ageInTicks * 0.2F + i) * leftArmStringSway[i][j];
							leftArmString[i][j].rotateAngleY = MathHelper.cos(ageInTicks * 0.3F + i) * leftArmStringSway[i][j];
							leftArmString[i][j].rotateAngleZ = MathHelper.sin(ageInTicks * 0.4F + i) * leftArmStringSway[i][j];
							leftArmString[i][j].showModel = show;
						}
					}
				} else {
					hair.showModel = false;
					bipedHeadwear.showModel = true;
					bodywear.showModel = true;
					mouthThreads.showModel = false;
					backThreads.showModel = false;
					rightForeArm.showModel = true;
					rightArmThread[0].showModel = false;
					leftForeArm.showModel = true;
					leftArmThread[0].showModel = false;
					jaw.rotateAngleX = 0.0F;
					bipedRightArm.rotationPointY = 2.0F;
					bipedLeftArm.rotationPointY = 2.0F;
				}
			}
		}
	}
}
