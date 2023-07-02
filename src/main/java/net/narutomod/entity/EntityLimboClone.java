
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemSixPathSenjutsu;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Chakra;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityLimboClone extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 161;
	public static final int ENTITYID_RANGED = 162;
	public static final double CHAKRA_USAGE = 500d;

	public EntityLimboClone(ElementsNarutomodMod instance) {
		super(instance, 418);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
			.id(new ResourceLocation("narutomod", "limbo_clone"), ENTITYID).name("limbo_clone").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> {
			return new RenderEC(renderManager);
		});
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityAttackedHook());
	}

	public static class EC extends EntityClone._Base {
		private final int lifeSpan = 400;

		public EC(World world) {
			super(world);
			this.setEntityInvulnerable(true);
		}

		public EC(EntityLivingBase user) {
			super(user);
			//this.setInvisible(true);
			this.setEntityInvulnerable(true);
			this.setAlwaysRenderNameTag(false);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
			 .setBaseValue(user instanceof EntityPlayer ? PlayerTracker.getNinjaLevel((EntityPlayer)user)
			 : ProcedureUtils.getModifiedAttackDamage(user));
			this.moveHelper = new EntityClone.AIFlyControl(this);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(2, new EntityClone.AIFollowSummoner(this, 0.8d, 3.0F));
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(1.2D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.0d);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0d);
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, worldIn);
			pathnavigateflying.setCanOpenDoors(false);
			pathnavigateflying.setCanFloat(true);
			pathnavigateflying.setCanEnterDoors(true);
			return pathnavigateflying;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			Entity attacker = source.getTrueSource();
			if (attacker instanceof EntityLivingBase && this.canBeDetectedBy((EntityLivingBase)attacker)) {
				return super.attackEntityFrom(source, amount);
			}
			if (attacker instanceof EntityLivingBase) {
				this.setRevengeTarget((EntityLivingBase)attacker);
			}
			return false;
		}
		
		@Override
		public boolean canBeCollidedWith() {
			return false;
		}
		
		@Override
		public boolean canBePushed() {
			return false;
		}

		private boolean canBeDetectedBy(Entity entity) {
			return entity.equals(this.getSummoner())
			 || (entity instanceof EntityLivingBase && ItemRinnegan.wearingRinnegan((EntityLivingBase)entity))
			 || (entity instanceof EntityPlayer && ProcedureUtils.hasItemInInventory((EntityPlayer)entity, ItemSixPathSenjutsu.block));
		}

		/*@SideOnly(Side.CLIENT)
		@Override
		public boolean isInvisibleToPlayer(EntityPlayer player) {
			return !player.isSpectator() && !player.equals(this.getSummoner()) && !this.canBeDetectedBy(player);
		}

		@Override
		public void setInvisible(boolean invisible) {
			if (invisible) {
				super.setInvisible(invisible);
			}
		}*/

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted > this.lifeSpan) {
				if (this.getSummoner() != null) {
					this.copyLocationAndAnglesFrom(this.getSummoner());
					this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
				}
				if (this.ticksExisted > this.lifeSpan + 2) {
					this.setDead();
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "LimboCloneEntityIds";

			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (!getLimboClones(entity).isEmpty()) {
					return false;
				}
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) SoundEvent.REGISTRY
				  .getObject(new ResourceLocation("narutomod:rinbo_hengoku")), SoundCategory.NEUTRAL, 1.0F, 1.0F);
				int[] ids = new int[2];
				for (int i = 0; i < 2; i++) {
					EC entity1 = new EC(entity);
					entity.world.spawnEntity(entity1);
					ids[i] = entity1.getEntityId();
				}
				entity.getEntityData().setIntArray(ID_KEY, ids);
				if (entity instanceof EntityPlayer) {
					ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, 2000);
				}
				return true;
			}

			public static List<EC> getLimboClones(EntityLivingBase entity) {
				List<EC> list = Lists.newArrayList();
				int[] ids = entity.getEntityData().getIntArray(ID_KEY);
				for (int i = 0; i < ids.length; i++) {
					Entity entity1 = entity.world.getEntityByID(ids[i]);
					if (entity1 instanceof EC) {
						list.add((EC)entity1);
					}
				}
				return list;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderEC extends EntityClone.ClientRLM.RenderClone<EC> {
		public RenderEC(RenderManager renderManager) {
			EntityClone.ClientRLM.getInstance().super(renderManager);
			this.shadowSize = 0.0f;
		}

		@Override
		public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
			if (entity.canBeDetectedBy(this.renderManager.renderViewEntity)) {
				GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
				GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
			}
		}

		@Override
		protected void renderLayers(EC entity, float f0, float f1, float f2, float f3, float f4, float f5, float f6) {
			if (entity.canBeDetectedBy(this.renderManager.renderViewEntity)) {
				GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
				super.renderLayers(entity, f0, f1, f2, f3, f4, f5, f6);
				GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
			}
		}

		@Override
		protected boolean canRenderName(EC entity) {
			return false;
		}
	}

	public class EntityAttackedHook {
		@SubscribeEvent
		public void onAttacked(LivingAttackEvent event) {
			EntityLivingBase entity = event.getEntityLiving();
			List<EC> list = EC.Jutsu.getLimboClones(entity);
			if (!list.isEmpty()) {
				for (EC clone : list) {
					if (clone.getDistanceSq(entity) <= 64.0d) {
						if (event.getSource().getDamageLocation() != null) {
							Vec3d vec = event.getSource().getDamageLocation().subtract(entity.getPositionVector())
							 .scale(event.getSource().getImmediateSource() instanceof EntityLivingBase ? 0.5d : 0.8d);
							ProcedureUtils.Vec2f vec2 = ProcedureUtils.getYawPitchFromVec(vec);
							clone.setPositionAndRotation(entity.posX + vec.x, entity.posY + vec.y, entity.posZ + vec.z, vec2.x, vec2.y);
						}
						event.setCanceled(true);
						clone.attackEntityFrom(event.getSource(), event.getAmount());
						return;
					}
				}
			}
		}
	}
}
