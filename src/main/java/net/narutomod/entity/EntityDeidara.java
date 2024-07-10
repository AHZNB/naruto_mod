
package net.narutomod.entity;

import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.item.ItemNinjaArmorFishnets;
import net.narutomod.item.ItemBakuton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.ModConfig;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.SoundEvents;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityDeidara extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 449;
	public static final int ENTITYID_RANGED = 450;

	public EntityDeidara(ElementsNarutomodMod instance) {
		super(instance, 882);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "deidara"), ENTITYID)
				.name("deidara").tracker(64, 3, true).egg(-16777216, -6750157).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob, IRangedAttackMob {
		private int explosiveCloneCD = 100;
		
		public EntityCustom(World world) {
			super(world, 120, 7000d);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemToInventory(new ItemStack(ItemAkatsukiRobe.helmet), 1);
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobe.body));
			this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemNinjaArmorFishnets.legs));
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 10, true, false,
				new Predicate<EntityPlayer>() {
					public boolean apply(@Nullable EntityPlayer p_apply_1_) {
						return p_apply_1_ != null && (ModConfig.AGGRESSIVE_BOSSES || EntityBijuManager.isJinchuriki(p_apply_1_));
					}
				}));
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new EntityNinjaMob.AIAttackRangedTactical(this, 1.0D, 50, 40.0F));
			this.tasks.addTask(2, new EntityAIWatchClosest2(this, EntityPlayer.class, 32.0F, 1.0F));
			this.tasks.addTask(3, new EntityAIWander(this, 0.5d));
			this.tasks.addTask(4, new EntityAILookIdle(this));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			EntityLivingBase target = this.getAttackTarget();
			if ((this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemAkatsukiRobe.helmet) != (target == null)) {
				this.swapWithInventory(EntityEquipmentSlot.HEAD, 1);
			}
			if (this.explosiveCloneCD > 0) {
				--this.explosiveCloneCD;
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source == DamageSource.FALL) {
				return false;
			}
			if (source.getTrueSource() instanceof EntityLivingBase && this.explosiveCloneCD <= 0 && this.consumeChakra(ItemBakuton.CLONE.chakraUsage * 0.5d)) {
				EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
				this.setRevengeTarget(attacker);
				EntityExplosiveClone.EC clone = new EntityExplosiveClone.EC(this);
				this.world.spawnEntity(clone);
				clone.attackEntityFrom(source, amount);
				this.explosiveCloneCD = 100;
				Vec3d vec3d = this.getPositionVector().subtract(attacker.getPositionVector()).normalize();
				int i = 16;
				BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
				for (Vec3d vec1 = vec3d.scale(i); i > 1; vec1 = vec3d.scale(--i)) {
					int j = 0;
					pos.setPos(attacker.posX - vec1.x, attacker.posY - vec1.y, attacker.posZ - vec1.z);
					while (j < EnumFacing.VALUES.length && (!this.world.getBlockState(pos.down()).isTopSolid() || !this.world.isAirBlock(pos.up()))) {
						pos.setPos(pos.offset(EnumFacing.VALUES[j++]));
					}
					if (j < EnumFacing.VALUES.length) {
						vec3d = new Vec3d(0.5d + pos.getX(), pos.getY(), 0.5d + pos.getZ());
						this.rotationYaw = ProcedureUtils.getYawFromVec(attacker.getPositionVector().subtract(vec3d));
						this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 10, 0, false, false));
						this.setPositionAndUpdate(vec3d.x, vec3d.y, vec3d.z);
						break;
					}
				}
				pos.release();
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void setSwingingArms(boolean swingingArms) {
			ProcedureSync.EntityNBTTag.setAndSync(this, NarutomodModVariables.forceBowPose, swingingArms);
		}

		@Override
		public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
			if (this.consumeChakra(ItemBakuton.CLAY.chakraUsage * 0.5d)) {
				this.setLastAttackedEntity(target);
				for (int i = 0; i < 2; i++) {
					ItemBakuton.CLAY.jutsu.createJutsu(null, this, 1.0f);
				}
			}
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return super.isOnSameTeam(entityIn) || EntityNinjaMob.TeamAkatsuki.contains(entityIn.getClass())
			 || (entityIn instanceof EntityExplosiveClone.EC && this == ((EntityExplosiveClone.EC)entityIn).getSummoner());
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return SoundEvents.ENTITY_ILLUSION_ILLAGER_AMBIENT;
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return SoundEvents.ENTITY_ILLAGER_DEATH;
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
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/deidara256.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelDeidara());
			}

			@Override
			protected void renderLayers(EntityCustom entity, float f0, float f1, float f2, float f3, float f4, float f5, float f6) {
				if (!entity.isInvisible()) {
					super.renderLayers(entity, f0, f1, f2, f3, f4, f5, f6);
				}
			}

			@Override
			protected void preRenderCallback(EntityCustom entity, float partialTickTime) {
				GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
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

		// Made with Blockbench 4.10.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelDeidara extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer cube_r1;
			private final ModelRenderer cube_r2;
			private final ModelRenderer rightBag;
			private final ModelRenderer leftBag;

			public ModelDeidara() {
				textureWidth = 64;
				textureHeight = 32;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -7.6F, -3.6F, 8, 8, 8, 0.7F, false));
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 48, 0, -4.0F, 0.0F, 4.0F, 8, 4, 0, 0.0F, false));
				cube_r1 = new ModelRenderer(this);
				cube_r1.setRotationPoint(0.0F, -3.7F, 1.0F);
				bipedHeadwear.addChild(cube_r1);
				setRotationAngle(cube_r1, 0.1745F, 0.0F, 0.0873F);
				cube_r1.cubeList.add(new ModelBox(cube_r1, 32, 0, -4.0F, -4.0F, -4.0F, 8, 8, 8, 0.7F, false));
				cube_r2 = new ModelRenderer(this);
				cube_r2.setRotationPoint(0.0F, -3.5F, 0.5F);
				bipedHeadwear.addChild(cube_r2);
				setRotationAngle(cube_r2, 0.0873F, 0.0F, -0.0873F);
				cube_r2.cubeList.add(new ModelBox(cube_r2, 32, 0, -4.0F, -4.0F, -4.0F, 8, 8, 8, 0.7F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));		
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				rightBag = new ModelRenderer(this);
				rightBag.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightLeg.addChild(rightBag);
				rightBag.cubeList.add(new ModelBox(rightBag, 24, 0, -4.1F, 0.0F, -2.0F, 2, 4, 4, 0.0F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				leftBag = new ModelRenderer(this);
				leftBag.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftLeg.addChild(leftBag);
				leftBag.cubeList.add(new ModelBox(leftBag, 24, 0, 2.1F, 0.0F, -2.0F, 2, 4, 4, 0.0F, true));
				rightBag.showModel = false;
				leftBag.showModel = false;
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
