
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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

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
import net.minecraft.init.SoundEvents;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFlameSlice extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 445;
	public static final int ENTITYID_RANGED = 446;
	private static final int FLAME_COLOR = 0xffffcf00;

	public EntityFlameSlice(ElementsNarutomodMod instance) {
		super(instance, 876);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "flame_slice"), ENTITYID).name("flame_slice").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntitySweepParticle.class)
		 .id(new ResourceLocation("narutomod", "flame_slice_particle"), ENTITYID_RANGED).name("flame_slice_particle")
		 .tracker(64, 3, true).build());
	}

	public static class EC extends EntityChakraFlow.Base implements ItemJutsu.IJutsu {
		private boolean holdingWeapon;
		private int ticksSinceLastSwing;

		public EC(World world) {
			super(world);
		}

		public EC(EntityLivingBase user, ItemStack itemstack) {
			super(user);
			if (itemstack.getItem() == ItemKaton.block) {
				float f = ((ItemKaton.RangedItem)itemstack.getItem()).getCurrentJutsuXpModifier(itemstack, user);
				if (f > 0.0f) {
					this.damageModifier = (1.0f / f) * 3;
				}
			}
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.RAITON;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				boolean flag = this.isUserHoldingWeapon();
				if (flag) {
					EntityLivingBase user = this.getUser();
					if (user instanceof EntityPlayer && user.swingProgressInt == 1) {
						this.world.spawnEntity(new EntitySweepParticle(user, 8.0f));
						double d = ProcedureUtils.getReachDistance(user);
						float damage = (float)ProcedureUtils.getModifiedAttackDamage(user) * this.getCooledAttackStrength(user, 0.5f);
						this.ticksSinceLastSwing = 0;
						Entity directTarget = ProcedureUtils.objectEntityLookingAt(user, 4d, this).entityHit;
						for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, user.getEntityBoundingBox().grow(d, 0.25D, d))) {
							if (entity != user && entity != directTarget && !user.isOnSameTeam(entity) && user.getDistanceSq(entity) <= d * d) {
								entity.knockBack(user, 0.5F, MathHelper.sin(user.rotationYaw * 0.017453292F), -MathHelper.cos(user.rotationYaw * 0.017453292F));
								entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)user), damage);
								entity.setFire(15);
							}
						}
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:flamethrow")), 1.0F, this.rand.nextFloat() * 0.4f + 0.9f);
					}
					if (!this.holdingWeapon) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:flamethrow")), 0.8F, this.rand.nextFloat() * 0.5f + 0.6f);
					} else if (this.rand.nextFloat() < 0.05f) {
						this.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 0.4f, this.rand.nextFloat() * 0.4f + 0.7f);
					}
					if (this.ticksExisted % 10 == 1 && !net.narutomod.Chakra.pathway(this.getUser()).consume(ItemKaton.FLAMESLICE.chakraUsage * 0.1d)) {
						this.setDead();
					}
				}
				this.holdingWeapon = flag;
			}
			++this.ticksSinceLastSwing;
		}

	    public float getCooledAttackStrength(EntityLivingBase entity, float adjustTicks) {
	    	float f = (float)(1.0D / ProcedureUtils.getAttackSpeed(entity) * 20.0D);
	        float f2 = MathHelper.clamp(((float)this.ticksSinceLastSwing + adjustTicks) / f, 0.0F, 1.0F);
	        return 0.2F + f2 * f2 * 0.8F;
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
	}

	public static class EntitySweepParticle extends EntitySweep.Base {
		public EntitySweepParticle(World world) {
			super(world);
			this.maxAge = 6;
		}

		public EntitySweepParticle(EntityLivingBase shooter, float scale) {
			super(shooter, FLAME_COLOR, scale);
			Vec3d vec = shooter.getPositionEyes(1f);
			this.setLocationAndAngles(vec.x, vec.y, vec.z, shooter.rotationYaw, 0.0f);
			this.maxAge = 6;
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
			RenderingRegistry.registerEntityRenderingHandler(EntitySweepParticle.class, renderManager -> new RenderSweep(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityChakraFlow.RenderCustom<EC> {
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			protected void spawnParticles(EntityLivingBase entity, Vec3d startvec, Vec3d endvec, float partialTicks) {
				if (entity.getSwingProgress(partialTicks) == 0.0f) {
					Vec3d vec = endvec.subtract(startvec);
					Vec3d vec2 = vec.scale(0.04d);
					for (int i = 0; i < 3; i++) {
						Vec3d vec1 = startvec.add(vec.scale(entity.getRNG().nextDouble() * 0.9d + 0.1d));
						Particles.spawnParticle(entity.world, Particles.Types.FLAME, vec1.x, vec1.y, vec1.z,
						  1, 0.025d, 0.025d, 0.025d, vec2.x, vec2.y, vec2.z, FLAME_COLOR);
					}
				}
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderSweep extends EntitySweep.Renderer.RenderCustom {
			public RenderSweep(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			protected void renderParticles(EntitySweep.Base entity, Vec3d entityVec, Vec3d relVec, int color, float scale) {
				Vec3d vec1 = entityVec.add(relVec);
				Vec3d vec2 = relVec.scale(0.05d);
				Particles.spawnParticle(entity.world, Particles.Types.FLAME, vec1.x, vec1.y, vec1.z,
				 1, 0d, 0d, 0d, vec2.x, vec2.y, vec2.z, FLAME_COLOR, (int)(scale * (entity.rand().nextFloat() * 3.0f + 1.0f)));
			}
		}
	}
}
