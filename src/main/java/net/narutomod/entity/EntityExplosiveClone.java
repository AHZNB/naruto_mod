
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import net.narutomod.item.ItemJutsu;
import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class EntityExplosiveClone extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 239;
	public static final int ENTITYID_RANGED = 240;

	public EntityExplosiveClone(ElementsNarutomodMod instance) {
		super(instance, 560);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "explosive_clone"), ENTITYID).name("explosive_clone").tracker(64, 3, true).build());
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
		public class CustomRender extends EntityClone.ClientRLM.RenderClone<EC> {
			public CustomRender(RenderManager renderManagerIn) {
				EntityClone.ClientRLM.getInstance().super(renderManagerIn);
			}
	
		    @Override
		    protected void preRenderCallback(EC entity, float partialTick) {
		    	super.preRenderCallback(entity, partialTick);
		    	if (entity.ignited()) {
			        float f = entity.getIgnitionProgress(partialTick);
			        float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
			        f = MathHelper.clamp(f, 0.0F, 1.0F);
			        f = f * f;
			        f = f * f;
			        float f2 = (1.0F + f * 0.4F) * f1;
			        float f3 = (1.0F + f * 0.1F) / f1;
			        GlStateManager.scale(f2, f3, f2);
		    	}
		    }
	
		    @Override
		    protected int getColorMultiplier(EC entity, float lightBrightness, float partialTick) {
		    	if (entity.ignited()) {
			        float f = entity.getIgnitionProgress(partialTick);
		            int i = MathHelper.clamp((int)((1f - f) * 255.0F), 1, 255);
		            return i << 24 | 0x00FFFFFF;
		    	}
		    	return 0;
		    }
		}
	}

	public static class EC extends EntityClone.Base implements ItemJutsu.IJutsu {
		private static final DataParameter<Boolean> IGNITED = EntityDataManager.<Boolean>createKey(EC.class, DataSerializers.BOOLEAN);
		private final int fuse = 30;
		private int ignitionTime;
		private boolean exploded;

		public EC(World world) {
			super(world);
			this.stepHeight = 16f;
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}

		public EC(EntityLivingBase user) {
			super(user);
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(user.getMaxHealth());
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ProcedureUtils.getModifiedSpeed(user) * 4.0d);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48d);
			this.setHealth(this.getMaxHealth());
			this.stepHeight = 16f;
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.BAKUTON;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(IGNITED, Boolean.valueOf(false));
		}

		private void ignite() {
			this.dataManager.set(IGNITED, Boolean.valueOf(true));
		}

		private boolean ignited() {
			return ((Boolean)this.dataManager.get(IGNITED)).booleanValue();
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(2, new EntityClone.AIFollowSummoner(this, 0.8d, 4.0F));
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getImmediateSource() instanceof EntityPlayer && source.getImmediateSource() == this.getSummoner()) {
				this.setDead();
				return false;
			}
			return this.ignited() ? false : super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			this.ignite();
			if (entityIn instanceof EntityLivingBase) {
				((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(PotionHeaviness.potion, this.fuse, 3, false, false));
			}
			return super.attackEntityAsMob(entityIn);
		}

		private void explode() {
	    	if (!this.world.isRemote) {
		    	this.dead = true;
		    	this.exploded = true;
		    	EntityLivingBase summoner = this.getSummoner();
				this.world.createExplosion(summoner, this.posX, this.posY, this.posZ, 8f,
		    	 net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, summoner));
		    	ProcedureAoeCommand.set(this, 0d, 8d)
		    	 .damageEntitiesCentered(ItemJutsu.causeJutsuDamage(this, summoner), 45f + this.rand.nextFloat() * 10f);
	    		this.setDead();
	    	}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && !this.exploded) {
				this.poof();
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.isEntityAlive()) {
				if (this.ignited()) {
					if (this.ignitionTime + 1 == this.ticksExisted) {
						this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
						EntityLivingBase summoner = this.getSummoner();
						if (summoner != null) {
							this.world.playSound(null, summoner.posX, summoner.posY, summoner.posZ, 
							 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:katsu")), 
							 SoundCategory.NEUTRAL, 1f, 1f);
						}
					} else if (this.ticksExisted - this.ignitionTime > this.fuse) {
						this.explode();
					}
				} else {
					this.ignitionTime = this.ticksExisted;
				}
			}
		}

		public float getIgnitionProgress(float partialTick) {
			return ((float)this.ticksExisted + partialTick - this.ignitionTime) / (float)(this.fuse - 2);
		}

		private void poof() {
			//this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:poof")), 1.0F, 1.0F);
			this.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.6F, 0.7F);
			Particles.Renderer particles = new Particles.Renderer(this.world);
			for (int i = 0; i < 200; i++) {
				float scale = 3.0f + this.rand.nextFloat() * 3.0f;
				particles.spawnParticles(Particles.Types.SMOKE, this.posX + (this.rand.nextFloat()-0.5f) * 0.6f,
				 this.posY + this.rand.nextFloat() * 1.8f, this.posZ + (this.rand.nextFloat()-0.5f) * 0.6f,
				 1, 0d, 0d, 0d, 0d, 0d, 0d, -1, (int)(scale * 10f), 0, 0, -1, -6 - this.rand.nextInt(8));
				 //(int) (8.0f / (this.rand.nextFloat() * 0.8f + 0.2f) * scale));
			}
			particles.send();
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (!entity.isSneaking()) {
					this.createJutsu(entity);
					return true;
				} else {
					for (EC clone : entity.world.getEntities(EC.class, EntitySelectors.IS_ALIVE)) {
						if (entity.equals(clone.getSummoner())) {
							clone.setDead();
						}
					}
					return false;
				}
			}

			public EC createJutsu(EntityLivingBase entity) {
				EC entity1 = new EC(entity);
				if (entity.getRevengeTarget() != null) {
					EntityLivingBase attacker = entity.getRevengeTarget();
					List<BlockPos> list = ProcedureUtils.getAllAirBlocks(entity.world, attacker.getEntityBoundingBox().grow(16d, 8d, 16d));
					list.sort(new ProcedureUtils.BlockposSorter(entity.getPosition()));
					for (int i = list.size() - 1; i >= 0; --i) {
						BlockPos pos = list.get(i);
						Vec3d vec = new Vec3d(0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ());
						if (entity.getDistance(vec.x, vec.y, vec.z) <= 16d && entity.world.getBlockState(pos.down()).isTopSolid() && entity.world.isAirBlock(pos.up())
						 && entity.world.rayTraceBlocks(vec.addVector(0d, entity.getEyeHeight(), 0d), attacker.getPositionEyes(1f), false, true, false) == null) {
							float angle = MathHelper.wrapDegrees(ProcedureUtils.getYawFromVec(vec.subtract(attacker.getPositionVector())) - ProcedureUtils.getYawFromVec(entity.getPositionVector().subtract(attacker.getPositionVector()))); 
							if (angle > 135.0f || angle < -135.0f) {
								entity.rotationYaw = ProcedureUtils.getYawFromVec(attacker.getPositionVector().subtract(vec));
								entity.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 5, 0, false, false));
								entity.setInvisible(true);
								entity.setPositionAndUpdate(vec.x, vec.y, vec.z);
								break;
							}
						}
					}
				} else {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
					  .getObject(new ResourceLocation("narutomod:kagebunshin")), SoundCategory.NEUTRAL, 1.0F, 1.0F);
				}
				entity.world.spawnEntity(entity1);
				return entity1;
			}
		}
	}
}
