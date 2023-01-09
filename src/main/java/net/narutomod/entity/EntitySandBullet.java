
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
import net.minecraft.init.SoundEvents;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemGourd;
import net.narutomod.item.ItemJiton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;

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
			this(shooter, sandType, shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ);
		}

		public EC(EntityLivingBase shooter, ItemJiton.Type sandType, double x, double y, double z) {
			super(shooter);
			this.setOGSize(0.2f, 0.2f);
			this.setPosition(x, y, z);
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
				if (result.entityHit instanceof EntityLivingBase) {
					this.playSound((net.minecraft.util.SoundEvent)net.minecraft.util.SoundEvent.REGISTRY
					 .getObject(new ResourceLocation("narutomod:bullet_impact")), 1f, 0.4f + this.rand.nextFloat() * 0.6f);
					result.entityHit.hurtResistantTime = 10;
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
				 100, 0.03d, 0.03d, 0.03d, 0d, 0d, 0d, this.color, 10, 5);
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				List<ItemJiton.SwarmTarget> list = getStartPosList(stack);
				if (list != null) {
					Iterator<ItemJiton.SwarmTarget> iter = list.iterator();
					while (iter.hasNext()) {
						ItemJiton.SwarmTarget st = iter.next();
						Vec3d vec = st.getTargetPos();
						this.createJutsu(ItemJiton.getSandType(stack), entity, vec.x, vec.y, vec.z);
						st.forceRemove();
						iter.remove();
					}
				} else {
					this.createJutsu(ItemJiton.getSandType(stack), entity, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
				}
				return true;
			}

			public void createJutsu(ItemJiton.Type type, EntityLivingBase entity, double x, double y, double z) {
				entity.world.playSound(null, x, y, z, SoundEvents.BLOCK_SAND_PLACE,
				 net.minecraft.util.SoundCategory.BLOCKS, 0.5f, entity.getRNG().nextFloat() * 0.4f + 0.6f);
				Vec3d vec = entity.getLookVec();
				EC entity1 = new EC(entity, type, x, y, z);
				entity1.shoot(vec.x, vec.y, vec.z, 1.2f, 0.1f);
				entity.world.spawnEntity(entity1);
			}
		}
	}

	private static final Map<ItemStack, List<ItemJiton.SwarmTarget>> posMap = Maps.newHashMap();

	@Nullable
	public static List<ItemJiton.SwarmTarget> getStartPosList(ItemStack stack) {
		List<ItemJiton.SwarmTarget> list = posMap.get(stack);
		if (list != null) {
			return list;
		} else {
			for (Map.Entry<ItemStack, List<ItemJiton.SwarmTarget>> entry : posMap.entrySet()) {
				if (ItemStack.areItemStacksEqual(entry.getKey(), stack)) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	public static void addPos(ItemStack stack, EntityLivingBase entity, float power) {
		List<ItemJiton.SwarmTarget> list = getStartPosList(stack);
		if (list == null) {
			list = Lists.newArrayList();
			posMap.put(stack, list);
		}
		list.add(new ItemJiton.SwarmTarget(entity.world, 10, ItemGourd.getMouthPos(entity), 
		 new Vec3d(entity.posX + (entity.getRNG().nextDouble()-0.5d) * power * 2, entity.posY + entity.getEyeHeight() + (entity.getRNG().nextDouble()-0.5d) * 2d, entity.posZ + (entity.getRNG().nextDouble()-0.5d) * power * 2),
		 new Vec3d(0.1d, 0.2d, 0.1d), 0.5f, 0.01f, false, 0.5f, ItemJiton.getSandType(stack).getColor()));
	}

	public static void updateSwarms(ItemStack stack) {
		List<ItemJiton.SwarmTarget> list = getStartPosList(stack);
		if (list != null && !list.isEmpty()) {
			for (ItemJiton.SwarmTarget st : list) {
				st.onUpdate();
			}
		}
	}
}
