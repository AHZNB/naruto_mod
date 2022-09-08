
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemJiton;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import net.minecraft.init.SoundEvents;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySandBullet extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 202;
	public static final int ENTITYID_RANGED = 203;
	private static final Random rand = new Random();

	public EntitySandBullet(ElementsNarutomodMod instance) {
		super(instance, 519);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
			.id(new ResourceLocation("narutomod", "sand_bullet"), ENTITYID).name("sand_bullet").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
	}

	@SideOnly(Side.CLIENT)
	public class CustomRender extends Render<EC> {
		public CustomRender(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}
		@Override
		public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
			/*for (int i = 0; i < 10; i++) {
				entity.world.spawnParticle(EnumParticleTypes.SUSPENDED, entity.posX + rand.nextGaussian() * 0.1d, 
				 entity.posY + rand.nextDouble() * 0.2d, entity.posZ + rand.nextGaussian() * 0.1d, 0d, 0d, 0d);
			}*/
		}
		@Override
		protected ResourceLocation getEntityTexture(EC entity) {
			return null;
		}
	}

	public static class EC extends EntityScalableProjectile.Base {
		private int color;

		public EC(World worldIn) {
			super(worldIn);
			this.setOGSize(0.2f, 0.2f);
		}

		public EC(EntityLivingBase shooter, ItemJiton.Type sandType) {
			super(shooter);
			this.setOGSize(0.2f, 0.2f);
			this.setPosition(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ);
			this.color = sandType.getColor();
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksAlive > 80) {
				this.setDead();
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote) {
				if (result.entityHit != null) {
					this.playSound((net.minecraft.util.SoundEvent)net.minecraft.util.SoundEvent.REGISTRY
					 .getObject(new ResourceLocation("narutomod:bullet_impact")), 1f, 0.4f + this.rand.nextFloat() * 0.6f);
					result.entityHit.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity).setProjectile(), 10f);
					ProcedureUtils.pushEntity(this, result.entityHit, 10d, 3.0f);
				}
				this.setDead();
			}
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void renderParticles() {
			if (!this.world.isRemote) {
				Particles.spawnParticle(this. world, Particles.Types.SUSPENDED, this.posX, this.posY+0.1d, this.posZ,
				 100, 0.03d, 0.03d, 0.03d, 0d, 0d, 0d, this.color, 10, 2);
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_SAND_PLACE,
				 net.minecraft.util.SoundCategory.BLOCKS, 0.5f, entity.getRNG().nextFloat() * 0.4f + 0.6f);
				Vec3d vec = entity.getLookVec();
				EC entity1 = new EC(entity, ItemJiton.getSandType(stack));
				entity1.shoot(vec.x, vec.y, vec.z, 1.2f, 0f);
				entity.world.spawnEntity(entity1);
				return true;
			}
		}
	}
}
