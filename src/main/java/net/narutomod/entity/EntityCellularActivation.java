
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.item.ItemIryoJutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityCellularActivation extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 213;
	public static final int ENTITYID_RANGED = 214;

	public EntityCellularActivation(ElementsNarutomodMod instance) {
		super(instance, 527);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "cellular_activation"), ENTITYID).name("cellular_activation").tracker(64, 3, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EC.UserHook());
	}

	public static class EC extends Entity {
		private static final DataParameter<Integer> USER_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> REDUCTION = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private final double chakraBurn = ItemIryoJutsu.MEDMODE.chakraUsage;

		public EC(World worldIn) {
			super(worldIn);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase userIn) {
			this(userIn.world);
			this.setUser(userIn);
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
		}

		@Override
		protected void entityInit() {
			this.dataManager.register(USER_ID, Integer.valueOf(-1));
			this.dataManager.register(REDUCTION, Integer.valueOf(0));
		}

		private void setUser(EntityLivingBase user) {
			this.getDataManager().set(USER_ID, Integer.valueOf(user.getEntityId()));
		}

		@Nullable
		protected EntityLivingBase getUser() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(USER_ID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void setReductionAmount(int amount) {
			this.getDataManager().set(REDUCTION, Integer.valueOf(amount));
		}

		private int getReductionAmount() {
			return ((Integer)this.dataManager.get(REDUCTION)).intValue();
		}

		@Override
		public void onUpdate() {
			EntityLivingBase user = this.getUser();
			if (user != null) {
				this.setPosition(user.posX, user.posY, user.posZ);
				/*if (this.user.getHealth() < this.user.getMaxHealth()) {
					if (Chakra.pathway(this.user).consume(this.chakraBurn)) {
						this.user.heal(0.1f);
						Particles.spawnParticle(this.world, Particles.Types.SUSPENDED, this.user.posX, this.user.posY, this.user.posZ,
						 50, this.user.width/3, this.user.height/2, this.user.width/3, 0d, 0.05d, 0d, 0x8000fff6);
					}
				}*/
				int i = this.getReductionAmount();
				if (i > 0) {
					this.setReductionAmount(i - 1);
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		public void reduceDamage(LivingHurtEvent event) {
			EntityLivingBase user = this.getUser();
			if (user != null) {
				int i = user instanceof EntityPlayer ? (int)PlayerTracker.getNinjaLevel((EntityPlayer)user)
				 : user instanceof EntityNinjaMob.Base ? ((EntityNinjaMob.Base)user).getNinjaLevel()
				 : 1;
				if (i >= 10) {
					Chakra.Pathway cp = Chakra.pathway(user);
					float reduction = event.getAmount() * (1f - 1f/(float)(i - 8));
					this.setReductionAmount(this.getReductionAmount() + (int)reduction);
					double chakrausage = this.chakraBurn * reduction;
					if (chakrausage > cp.getAmount()) {
						reduction *= (float)(cp.getAmount() / chakrausage);
						chakrausage = cp.getAmount();
					}
					if (reduction > 0f && cp.consume(chakrausage)) {
						event.setAmount(event.getAmount() - reduction);
					}
				}
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean isInRangeToRenderDist(double distance) {
			double d = 68.5d * this.getRenderDistanceWeight();
			return distance < d * d;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "IryoCellularActivationEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ID_KEY));
				if (entity1 instanceof EC) {
					entity1.setDead();
					return false;
				} else {
					entity1 = new EC(entity);
					entity.world.spawnEntity(entity1);
					entity.getEntityData().setInteger(ID_KEY, entity1.getEntityId());
					return true;
				}
			}
		}

		public static class UserHook {
			@SubscribeEvent
			public void onUserDamaged(LivingHurtEvent event) {
				EntityLivingBase entity = event.getEntityLiving();
				Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(EC.Jutsu.ID_KEY));
				if (entity1 instanceof EC && entity1.isEntityAlive()) {
					((EC)entity1).reduceDamage(event);
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EC> {
			public RenderCustom(RenderManager rendermanager) {
				super(rendermanager);
			}
	
			@Override
			public boolean shouldRender(EC livingEntity, ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				EntityLivingBase user = entity.getUser();
				if (user != null && entity.getReductionAmount() > 0) {
					x = user.lastTickPosX + (user.posX - user.lastTickPosX) * partialTicks;
					y = user.lastTickPosY + (user.posY - user.lastTickPosY) * partialTicks;
					z = user.lastTickPosZ + (user.posZ - user.lastTickPosZ) * partialTicks;
					Particles.spawnParticle(entity.world, Particles.Types.SMOKE, x, y+user.height/2, z,
					 1, 0d, 0d, 0d, 0d, 0d, 0d, 0x0000fff6|((0x10+user.getRNG().nextInt(0x20))<<24),
					 10 + user.getRNG().nextInt(25), 5, 0xF0, -1);
				}
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return null;
			}
		}
	}
}
