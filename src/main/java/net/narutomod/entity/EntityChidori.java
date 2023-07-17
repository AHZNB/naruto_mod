
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodModVariables;
import net.narutomod.PlayerTracker;
import net.narutomod.Particles;
import net.narutomod.Chakra;
//import net.narutomod.procedure.ProcedureLightSourceSetBlock;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureRenderView;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemRaiton;
import net.narutomod.item.ItemFuton;
import net.narutomod.item.ItemNinjutsu;
import net.narutomod.block.BlockLightSource;

import javax.annotation.Nullable;
import java.util.List;
import com.google.common.collect.ImmutableMap;

@ElementsNarutomodMod.ModElement.Tag
public class EntityChidori extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 141;
	public static final int ENTITYID_RANGED = 142;
	public static final double CHAKRA_USAGE = 150d;
	private static final double CHAKRA_BURN = 40d; // per second

	public EntityChidori(ElementsNarutomodMod instance) {
		super(instance, 396);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
			.id(new ResourceLocation("narutomod", "chidori"), ENTITYID).name("chidori").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(Spear.class)
			.id(new ResourceLocation("narutomod", "chidori_spear"), ENTITYID_RANGED).name("chidori_spear").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements ProcedureSync.CPacketVec3d.IHandler {
		private static final DataParameter<Integer> OWNER_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private final int growTime = 40;
		protected EntityLivingBase summoner;
		protected Vec3d handPos;
		private float summonerYaw;
		protected int duration;
		private double chakraBurn;
		private int attackTime;
		private int ticksSinceLastSwing;
		private int savedTicksSinceLastSwing;
		private Entity target;

		public EC(World a) {
			super(a);
			this.setSize(0.1f, 0.1f);
		}

		protected EC(EntityLivingBase summonerIn, double chakraBurnPerSec, int durationIn) {
			this(summonerIn.world);
			this.setOwner(summonerIn);
			this.chakraBurn = chakraBurnPerSec;
			this.duration = durationIn;
			this.setPositionToSummoner();
			this.setAlwaysRenderNameTag(false);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(OWNER_ID, Integer.valueOf(0));
		}

		public EntityLivingBase getOwner() {
			if (!this.world.isRemote) {
				return this.summoner;
			}
			Entity entity = this.world.getEntityByID(((Integer) this.getDataManager().get(OWNER_ID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		protected void setOwner(EntityLivingBase entity) {
			this.getDataManager().set(OWNER_ID, Integer.valueOf(entity.getEntityId()));
			this.summoner = entity;
		}

		private float getGrowth() {
			return Math.min((float)this.ticksExisted / (float)this.growTime, 1.0f);
		}

		public boolean isHoldingWeapon(EnumHand hand) {
			return this.getOwner() != null && ProcedureUtils.isWeapon(this.getOwner().getHeldItem(hand));
		}

		public boolean canUse() {
			if (this.getOwner() != null) {
				ItemStack item = this.getOwner().getHeldItemMainhand();
				return item.isEmpty() || item.getItem() instanceof ItemJutsu.Base || this.isHoldingWeapon(EnumHand.MAIN_HAND);
			}
			return false;
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.summoner != null) {
				ProcedureSync.EntityNBTTag.removeAndSync(this.summoner, NarutomodModVariables.forceBowPose);
				if (this.getClass() == EC.class) {
					ItemJutsu.IJutsuCallback.JutsuData jd = ItemRaiton.CHIDORI.jutsu.getData(this.summoner);
					if (jd != null) {
						ItemJutsu.Base item = (ItemJutsu.Base)jd.stack.getItem();
						item.setJutsuCooldown(jd.stack, ItemRaiton.CHIDORI,
						 (long)((float)this.ticksExisted * item.getModifier(jd.stack, this.summoner)) + 100);
						jd.stack.getTagCompound().removeTag(Jutsu.ID_KEY);
					}
				}
			}
		}

		@Override
		public void onUpdate() {
			boolean flag = this.isHoldingWeapon(EnumHand.MAIN_HAND);
			if (!this.world.isRemote && this.summoner != null
			 && flag != !this.summoner.getEntityData().getBoolean(NarutomodModVariables.forceBowPose)) {
				ProcedureSync.EntityNBTTag.setAndSync(this.summoner, NarutomodModVariables.forceBowPose, !flag);
			}
			if (this.summoner != null) {
				this.summoner.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 2, 6, false, false));
				this.setPositionToSummoner();
				if (this.ticksExisted % 20 == 0 && !Chakra.pathway(this.summoner).consume(this.chakraBurn)) {
					this.setDead();
				}
			}
			float f = this.getGrowth();
			if (this.rand.nextFloat() <= f * 0.3f) {
				this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:electricity")),
				  f * 0.5f, this.rand.nextFloat() * 2.0f + 1.0f);
			}
			if (this.ticksExisted > this.growTime / 2) {
				BlockPos pos = this.getPosition();
				if (this.world.isAirBlock(pos)) {
					//ProcedureLightSourceSetBlock.execute(this.world, pos.getX(), pos.getY(), pos.getZ());
					new net.narutomod.event.EventSetBlocks(this.world, ImmutableMap.of(pos, BlockLightSource.block.getDefaultState()), 0, 2, false, false);
				}
			}
			if (this.summoner != null && this.ticksExisted > this.growTime) {
				boolean flag2 = this.summoner instanceof EntityLiving && ((EntityLiving)this.summoner).getAttackTarget() != null;
				if (flag2 || (this.summoner instanceof EntityPlayer && this.summoner.swingProgressInt == 1)) {
					this.target = (flag2 ? new RayTraceResult(((EntityLiving)this.summoner).getAttackTarget())
					 : ProcedureUtils.objectEntityLookingAt(this.summoner, 20d, 1d, this)).entityHit;
					this.savedTicksSinceLastSwing = this.ticksSinceLastSwing;
					this.ticksSinceLastSwing = 0;
					this.attackTime = 0;
				}
				if (this.target instanceof EntityLivingBase && this.attackTime < 12) {
					this.summoner.rotationYaw = ProcedureUtils.getYawFromVec(this.target.getPositionVector()
					 .subtract(this.summoner.getPositionVector()));
					if (!flag && this.attackTime % 6 == 0) {
						this.launchAtTarget((EntityLivingBase)this.target);
					}
					if (this.target.getDistanceSq(this.summoner) < 25d) {
						float damage = flag 
						 ? (float)ProcedureUtils.getMainhandItemDamage(this.summoner) * this.damageMultiplier() * 1.2f
						 : (25f * this.damageMultiplier());
						EntityLightningArc.onStruck(this.target,
						 ItemJutsu.causeJutsuDamage(this, this.summoner), damage * this.getCooledAttackStrength());
						this.target = null;
					}
				}
			}
			if (!this.world.isRemote && (this.summoner == null || !this.summoner.isEntityAlive() || this.ticksExisted > this.duration || !this.canUse())) {
				this.setDead();
			}
			++this.ticksSinceLastSwing;
			++this.attackTime;
		}

		private void launchAtTarget(EntityLivingBase target) {
			if (!ProcedureUtils.isWearingAnySharingan(this.summoner)) {
				ProcedureRenderView.setFOV(this.summoner, 100, 10f);
			}
			double d0 = target.posX - this.summoner.posX;
			double d1 = target.posY - this.summoner.posY;
			double d2 = target.posZ - this.summoner.posZ;
			double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
			ProcedureUtils.setVelocity(this.summoner, d0 * 0.4, d1 * 0.4 + d3 * 0.02d, d2 * 0.4);
		}

		protected void setPositionToSummoner() {
			if (this.handPos != null) {
				this.setPosition(this.handPos.x, this.handPos.y - this.height * 0.5, this.handPos.z);
			} else {
				this.setPosition(this.summoner.posX, this.summoner.posY + 1.4d, this.summoner.posZ);
			}
		}

		public float getCooledAttackStrength() {
			if (this.summoner != null) {
				float f = (float)(1.0D / ProcedureUtils.getAttackSpeed(this.summoner) * 20.0D);
				return MathHelper.clamp((float)this.savedTicksSinceLastSwing / f, 0.0F, 1.0F);
			}
			return 0.0f;
		}

		protected float damageMultiplier() {
			if (this.summoner instanceof EntityPlayer) {
				return MathHelper.clamp((float)PlayerTracker.getNinjaLevel((EntityPlayer)this.summoner) / 40f, 1f, 6f);
			}
			return 1f;
		}

		@Override
		public void handleClientPacket(Vec3d vec) {
			this.handPos = vec;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "ChidoriEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY));
				if (!(entity instanceof EntityKageBunshin.EC) && entity1 instanceof EC) {
					entity1.setDead();
					entity.world.spawnEntity(new Spear(entity, CHAKRA_BURN));
					return true;
				} else if (!entity.isRiding()) {
					if (ItemFuton.CHAKRAFLOW.jutsu.isActivated(entity)) {
						ItemFuton.CHAKRAFLOW.jutsu.deactivate(entity);
					}
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
					 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:chidori")), 
					 SoundCategory.PLAYERS, 1.0F, 1.0F);
					EntityLivingBase entity2 = entity instanceof EntityKageBunshin.EC
					 ? ((EntityKageBunshin.EC)entity).getSummoner() : entity;
					double ninjalevel = entity2 instanceof EntityPlayer ? PlayerTracker.getNinjaLevel((EntityPlayer)entity2)
					 : entity2 instanceof EntityNinjaMob.Base ? ((EntityNinjaMob.Base)entity2).getNinjaLevel() : 0d;
					float f = ((ItemJutsu.Base)stack.getItem()).getCurrentJutsuXpModifier(stack, entity2);
					entity1 = new EC(entity, CHAKRA_BURN, (int)(ninjalevel * 5d / f));
					entity.world.spawnEntity(entity1);
					stack.getTagCompound().setInteger(ID_KEY, entity1.getEntityId());
					return true;
				}
				return false;
			}

			@Override
			public boolean isActivated(ItemStack stack) {
				return stack.getTagCompound().hasKey(ID_KEY);
			}

			@Override
			public boolean isActivated(EntityLivingBase entity) {
				return this.getData(entity) != null;
			}

			@Override
			public void deactivate(EntityLivingBase entity) {
				ItemJutsu.IJutsuCallback.JutsuData jd = this.getData(entity);
				if (jd != null) {
					jd.entity.setDead();
					jd.stack.getTagCompound().removeTag(ID_KEY);
				}
			}

			@Override
			@Nullable
			public ItemJutsu.IJutsuCallback.JutsuData getData(EntityLivingBase entity) {
				if (entity instanceof EntityPlayer) {
					ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemRaiton.block);
					if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(ID_KEY)) {
						Entity entity1 = entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY));
						return entity1 instanceof EC ? new JutsuData(entity1, stack) : null;
					}
				}
				return null;
			}
		}
	}

	public static class Spear extends EC {
		private boolean ryu;

		public Spear(World worldIn) {
			super(worldIn);
		}

		protected Spear(EntityLivingBase summonerIn, double chakraPerSec) {
			super(summonerIn, chakraPerSec, 81);
			if (summonerIn.isSneaking()) {
				this.ryu = true;
			}
		}

		@Override
		public void onUpdate() {
			if (!this.world.isRemote && this.ticksExisted == 1 && this.summoner instanceof EntityPlayer) {
				ProcedureSync.EntityNBTTag.setAndSync(this.summoner, NarutomodModVariables.forceBowPose, true);
			}
			if (this.summoner != null) {
				this.setPositionToSummoner();
				if (this.ticksExisted % 20 == 1 && !Chakra.pathway(this.summoner).consume(CHAKRA_BURN)) {
					this.setDead();
				}
			}
			if (this.rand.nextFloat() <= 0.3f) {
				this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:electricity"))),
				  1f, this.rand.nextFloat() * 2.0f + 1.5f);
			}
			if (this.summoner != null && this.ticksExisted <= this.duration) {
				if (this.ryu) {
					for (Entity entity1 : this.world.getEntitiesWithinAABBExcludingEntity(this.summoner, this.summoner.getEntityBoundingBox().grow(4d))) {
						if (!(entity1 instanceof EntityLightningArc.Base) && this.rand.nextInt(3) == 0) {
							EntityLightningArc.Base entity2 = new EntityLightningArc.Base(this.world,
							 this.summoner.getPositionVector().addVector(0d, 1d, 0d),
							 entity1.getPositionVector().addVector(0d, entity1.height/2, 0d), 0xc00000ff, 1, 0f);
							entity2.setDamage(ItemJutsu.causeJutsuDamage(this, this.summoner), 10f * this.damageMultiplier(), this.summoner);
							this.world.spawnEntity(entity2);
						}
					}
					EntityLightningArc.Base entity = new EntityLightningArc.Base(this.world,
					 this.summoner.getPositionVector().addVector(0d, 1d, 0d), this.rand.nextDouble() * 3d + 1d, 0d, 0d, 0d);
					entity.setDamage(ItemJutsu.causeJutsuDamage(this, this.summoner), 10f * this.damageMultiplier(), this.summoner);
					this.world.spawnEntity(entity);
				} else {
					Vec3d vec0 = this.summoner.getPositionEyes(1f);
					Vec3d vec1 = vec0.add(this.summoner.getLookVec().scale(6d));
					vec0 = this.handPos != null ? this.handPos : vec0.subtract(0d, 0.5d, 0d);
					EntityLightningArc.Base entity = new EntityLightningArc.Base(this.world, vec0, vec1, 0x800000FF, 1, 0f, 0.04f, 0);
					entity.setDamage(ItemJutsu.causeJutsuDamage(this, this.summoner), 10f * this.damageMultiplier(), this.summoner);
					this.world.spawnEntity(entity);
					if (this.rand.nextInt(3) == 0) {
						this.world.spawnEntity(new EntityLightningArc.Base(this.world, vec0, vec1, 0xc00000ff, 1, 0f));
					}
				}
			}
			if (!this.world.isRemote && (this.summoner == null || this.ticksExisted > this.duration || !this.canUse())) {
				this.setDead();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderChidori(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderChidori extends Render<EC> {
			public RenderChidori(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public boolean shouldRender(EC livingEntity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
				return true;
			}

			private Vec3d transform3rdPerson(Vec3d startvec, Vec3d angles, EntityLivingBase entity, EnumHandSide side, float pt) {
				return ProcedureUtils.rotateRoll(startvec, (float)angles.z).rotatePitch((float)-angles.x).rotateYaw((float)-angles.y)
						.addVector(0.0586F * (side==EnumHandSide.RIGHT?-6:6), 1.3F-(entity.isSneaking()?0.3f:0f), -0.05F)
						.rotateYaw(-this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, pt) * (float)(Math.PI / 180d))
						.addVector(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float f, float partialTicks) {
				EntityLivingBase user = entity.getOwner();
				if (user != null) {
					Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
					RenderLivingBase renderer = (RenderLivingBase)this.renderManager.getEntityRenderObject(user);
					ModelRenderer rightarmModel = ((ModelBiped)renderer.getMainModel()).bipedRightArm;
					Vec3d rightarmAngles = new Vec3d(rightarmModel.rotateAngleX, rightarmModel.rotateAngleY, rightarmModel.rotateAngleZ);
					ModelRenderer leftarmModel = ((ModelBiped)renderer.getMainModel()).bipedLeftArm;
					Vec3d leftarmAngles = new Vec3d(leftarmModel.rotateAngleX, leftarmModel.rotateAngleY, leftarmModel.rotateAngleZ);
					EnumHandSide mainhandside = user.getPrimaryHand();
					Vec3d mainarmAngles = mainhandside == EnumHandSide.RIGHT ? rightarmAngles : leftarmAngles;
					Vec3d offarmAngles = mainhandside == EnumHandSide.RIGHT ? leftarmAngles : rightarmAngles;
					boolean flag1 = entity.isHoldingWeapon(EnumHand.MAIN_HAND);
					boolean flag2 = entity.isHoldingWeapon(EnumHand.OFF_HAND);
					if (!flag1) {
						mainarmAngles = this.forceRightArmBowPose(mainarmAngles, user, partialTicks);
						Vec3d vec0 = this.transform3rdPerson(new Vec3d(0d, -0.7d, 0d), mainarmAngles, user, mainhandside, partialTicks);
						Particles.spawnParticle(entity.world, Particles.Types.SMOKE, vec0.x, vec0.y, vec0.z, 1, 0d, 0d, 0d, 0d, 0d, 0d,
								0x20FFFFFF, 5 + user.getRNG().nextInt(55), 5, 0xF0, -1, 0);
						if (!(entity instanceof Spear)) {
							EntityLightningArc.spawnAsParticle(entity.world, vec0.x, vec0.y, vec0.z, entity.getGrowth(), 0d, 0d, 0d, 0xc00000ff, 1);
						}
						if (viewer.equals(user)) {
							ProcedureSync.CPacketVec3d.sendToServer(entity, vec0);
						}
					} else {
						if (flag1 && entity.world.rand.nextFloat() <= 0.01f) {
							Vec3d vec0 = this.transform3rdPerson(new Vec3d(0d, -0.6875d, 0.2d), mainarmAngles, user, mainhandside, partialTicks);
							Vec3d vec1 = this.transform3rdPerson(new Vec3d(0d, -0.6875d, 1.6d), mainarmAngles, user, mainhandside, partialTicks)
									.subtract(vec0).scale(0.2);
							vec0 = vec0.add(vec1);
							EntityLightningArc.spawnAsParticle(entity.world, vec0.x, vec0.y, vec0.z, 0.01d, vec1.x, vec1.y, vec1.z);
							if (viewer.equals(user)) {
								ProcedureSync.CPacketVec3d.sendToServer(entity, vec0);
							}
						}
						if (flag2 && entity.world.rand.nextFloat() <= 0.01f) {
							Vec3d vec0 = this.transform3rdPerson(new Vec3d(0d, -0.6875d, 0.2d), offarmAngles, user, mainhandside.opposite(), partialTicks);
							Vec3d vec1 = this.transform3rdPerson(new Vec3d(0d, -0.6875d, 1.6d), offarmAngles, user, mainhandside.opposite(), partialTicks)
									.subtract(vec0).scale(0.2);
							vec0 = vec0.add(vec1);
							EntityLightningArc.spawnAsParticle(entity.world, vec0.x, vec0.y, vec0.z, 0.01d, vec1.x, vec1.y, vec1.z);
						}
					}
				}
			}

			private Vec3d forceRightArmBowPose(Vec3d ogvec, EntityLivingBase owner, float partialTicks) {
				float f = this.interpolateRotation(owner.prevRenderYawOffset, owner.renderYawOffset, partialTicks);
				float f1 = this.interpolateRotation(owner.prevRotationYawHead, owner.rotationYawHead, partialTicks);
				float f2 = (f1 - f) * 0.017453292F;
				float f7 = (owner.prevRotationPitch + (owner.rotationPitch - owner.prevRotationPitch) * partialTicks) * 0.017453292F;
				return new Vec3d(-((float)Math.PI / 2F) + f7, -0.1F + f2, ogvec.z);
			}

			private float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
				return prevYawOffset + ProcedureUtils.Vec2f.wrapDegrees(yawOffset - prevYawOffset) * partialTicks;
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return null;
			}
		}
	}
}
