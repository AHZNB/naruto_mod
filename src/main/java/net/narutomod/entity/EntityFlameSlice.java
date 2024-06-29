
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemKaton;
import net.narutomod.item.ItemFuton;
import net.narutomod.item.ItemRaiton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import javax.annotation.Nullable;
import java.util.Random;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFlameSlice extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 445;
	public static final int ENTITYID_RANGED = 446;

	public EntityFlameSlice(ElementsNarutomodMod instance) {
		super(instance, 876);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "flame_slice"), ENTITYID).name("flame_slice").tracker(64, 3, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EC.AttackHook());
	}

	public static class EC extends EntityChakraFlow.Base {
		private int strengthModifier = 2;
		private boolean holdingWeapon;

		public EC(World world) {
			super(world);
		}

		public EC(EntityLivingBase user, ItemStack itemstack) {
			super(user);
			if (itemstack.getItem() == ItemKaton.block) {
				float f = ((ItemKaton.RangedItem)itemstack.getItem()).getCurrentJutsuXpModifier(itemstack, user);
				if (f > 0.0f) {
					f = 1.0f / f;
					if (user instanceof EntityPlayer) {
						f *= PlayerTracker.getNinjaLevel((EntityPlayer)user) / 40d;
					}
					this.strengthModifier = (int)f;
				}
			}
		}

		@Override
		protected void addEffects() {
			if (!this.world.isRemote && this.ticksExisted % 10 == 0) {
				EntityLivingBase user = this.getUser();
				user.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 12, this.strengthModifier + this.ogStrength, false, false));
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				boolean flag = this.isUserHoldingWeapon();
				if (flag) {
					if (!this.holdingWeapon) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:flamethrow")), 0.8F, this.rand.nextFloat() * 0.5f + 0.6f);
					}
					if (this.ticksExisted % 10 == 1 && !net.narutomod.Chakra.pathway(this.getUser()).consume(ItemKaton.FLAMESLICE.chakraUsage * 0.1d)) {
						this.setDead();
					}
				}
				this.holdingWeapon = flag;
			}
		}

		public Random rng() {
			return this.rand;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "FlameSliceEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY));
				if (entity1 instanceof EC) {
					entity1.setDead();
					stack.getTagCompound().removeTag(ID_KEY);
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer)entity).sendStatusMessage(new TextComponentString("Off"), true);
					}
					return false;
				} else {
					if (ItemRaiton.CHAKRAMODE.jutsu.isActivated(entity)) {
						ItemRaiton.CHAKRAMODE.jutsu.deactivate(entity);
					}
					if (ItemRaiton.CHIDORI.jutsu.isActivated(entity)) {
						ItemRaiton.CHIDORI.jutsu.deactivate(entity);
					}
					if (ItemFuton.CHAKRAFLOW.jutsu.isActivated(entity)) {
						ItemFuton.CHAKRAFLOW.jutsu.deactivate(entity);
					}
					entity1 = new EC(entity, stack);
					entity.world.spawnEntity(entity1);
					stack.getTagCompound().setInteger(ID_KEY, entity1.getEntityId());
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer)entity).sendStatusMessage(new TextComponentString("On"), true);
					}
					return true;
				}
			}

			@Override
			public boolean isActivated(EntityLivingBase entity) {
				return this.getData(entity) != null;
			}

			@Override
			public void deactivate(EntityLivingBase entity) {
				JutsuData jd = this.getData(entity);
				if (jd != null) {
					jd.entity.setDead();
					jd.stack.getTagCompound().removeTag(ID_KEY);
				}
			}

			@Override
			@Nullable
			public JutsuData getData(EntityLivingBase entity) {
				if (entity instanceof EntityPlayer) {
					ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemKaton.block);
					if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(ID_KEY)) {
						Entity entity1 = entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY));
						return entity1 instanceof EC ? new JutsuData(entity1, stack) : null;
					}
				}
				return null;
			}
		}

		public static class AttackHook {
			@SubscribeEvent
			public void onLivingAttack(LivingAttackEvent event) {
				Entity attacker = event.getSource().getImmediateSource();
				if (attacker instanceof EntityLivingBase && ItemKaton.FLAMESLICE.jutsu.isActivated((EntityLivingBase)attacker) && !event.getEntityLiving().getEntityData().getBoolean("splashDamageFromFlameSlice")) {
					double d = ProcedureUtils.getReachDistance((EntityLivingBase)attacker);
					for (EntityLivingBase entity : attacker.world.getEntitiesWithinAABB(EntityLivingBase.class, attacker.getEntityBoundingBox().grow(d, 0.25D, d))) {
						if (entity != attacker && entity != event.getEntityLiving() && !attacker.isOnSameTeam(entity) && attacker.getDistanceSq(entity) <= d * d) {
							entity.getEntityData().setBoolean("splashDamageFromFlameSlice", true);
							entity.knockBack(attacker, 0.5F, MathHelper.sin(attacker.rotationYaw * 0.017453292F), -MathHelper.cos(attacker.rotationYaw * 0.017453292F));
							entity.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase)attacker), event.getAmount());
							entity.setFire(15);
							entity.getEntityData().removeTag("splashDamageFromFlameSlice");
						}
					}
					attacker.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ,
					 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:flamethrow")),
					 attacker.getSoundCategory(), 1.0F, ((EntityLivingBase)attacker).getRNG().nextFloat() * 0.5f + 0.6f);
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
		public class RenderCustom extends EntityChakraFlow.RenderCustom<EC> {
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			protected void spawnParticles(EC entity, Vec3d startvec, Vec3d endvec) {
				Vec3d vec = endvec.subtract(startvec);
				Vec3d vec2 = vec.scale(0.04d);
				for (int i = 0; i < 5; i++) {
					Vec3d vec1 = startvec.add(vec.scale(entity.rng().nextDouble() * 0.9d + 0.1d));
					Particles.spawnParticle(entity.world, Particles.Types.FLAME, vec1.x, vec1.y, vec1.z,
					  1, 0.025d, 0.025d, 0.025d, vec2.x, vec2.y, vec2.z, 0xffffcf00);
				}
			}
		}
	}
}
