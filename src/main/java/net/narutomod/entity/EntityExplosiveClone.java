
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EntitySelectors;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.ElementsNarutomodMod;

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

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
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

	public static class EC extends EntityClone.Base {
		private static final DataParameter<Boolean> IGNITED = EntityDataManager.<Boolean>createKey(EC.class, DataSerializers.BOOLEAN);
		private final int fuse = 30;
		private int ignitionTime;

		public EC(World world) {
			super(world);
			this.stepHeight = 16f;
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}

		public EC(EntityLivingBase user) {
			super(user);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48d);
			double d = ProcedureUtils.getModifiedSpeed(user) * 4.0d;
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(d);
			this.stepHeight = 16f;
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
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
			return this.ignited() ? false : super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean attackEntityAsMob(Entity entityIn) {
			this.ignite();
			return super.attackEntityAsMob(entityIn);
		}

		private void explode() {
	    	if (!this.world.isRemote) {
		    	this.dead = true;
		    	EntityLivingBase summoner = this.getSummoner();
				this.world.createExplosion(summoner, this.posX, this.posY, this.posZ, 8f,
		    	 net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, summoner));
		    	ProcedureAoeCommand.set(this, 0d, 8d)
		    	 .damageEntitiesCentered(ItemJutsu.causeJutsuDamage(this, summoner), 50f);
	    		this.setDead();
	    	}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
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
			this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:poof")), 1.0F, 1.0F);
			((WorldServer)this.world).spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX, this.posY+this.height/2, 
			  this.posZ, 200, this.width * 0.5d, this.height * 0.3d, this.width * 0.5d, 0.02d);
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (!entity.isSneaking()) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
					  .getObject(new ResourceLocation("narutomod:kagebunshin")), SoundCategory.NEUTRAL, 1.0F, 1.0F);
					entity.world.spawnEntity(new EC(entity));
					return true;
				} else {
					for (EC clone : entity.world.getEntities(EC.class, EntitySelectors.IS_ALIVE)) {
						clone.setDead();
					}
					return false;
				}
			}
		}
	}
}
