
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
//import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
//import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundEvent;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityPoisonMist extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 211;
	public static final int ENTITYID_RANGED = 212;

	public EntityPoisonMist(ElementsNarutomodMod instance) {
		super(instance, 526);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "poison_mist"), ENTITYID).name("poison_mist").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private final AirPunch airPunch = new AirPunch();
		private EntityLivingBase user;
		private float power;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase userIn, float powerIn) {
			this(userIn.world);
			this.user = userIn;
			this.power = powerIn;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (this.ticksExisted % 5 == 1) {
					this.playSound((SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:windecho")), 
					 1f, this.power * 0.2f);
				}
				this.airPunch.execute(this.user, this.power, this.power * 0.25d);
			}
			if (!this.world.isRemote && this.ticksExisted > (int)this.power * 2) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public class AirPunch extends ProcedureAirPunch {
			public AirPunch() {
				this.blockHardnessLimit = 1.0f;
				this.particlesDuring = null;
			}

			@Override
			protected void preExecuteParticles(EntityLivingBase player) {
				Vec3d vec = player.getLookVec().scale(2d);
				Particles.Renderer particles = new Particles.Renderer(player.world);
				for (int i = 1; i <= 50; i++) {
					Vec3d vec1 = player.getLookVec().scale(((EC.this.rand.nextDouble() * 0.8d) + 0.2d) * this.getRange(0) * 0.09d);
					particles.spawnParticles(Particles.Types.SMOKE, 
					 player.posX + vec.x, player.posY + 1.5d + vec.y, player.posZ + vec.z, 1, 0d, 0d, 0d, 
					 vec1.x + (EC.this.rand.nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 vec1.y + (EC.this.rand.nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 vec1.z + (EC.this.rand.nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 0xff630065, 80 + EC.this.rand.nextInt(20), 0, 0, -1, 0);
				}
				particles.send();
			}

			@Override
			protected void attackEntityFrom(EntityLivingBase player, Entity target) {
				if (target instanceof EntityLivingBase) {
					((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.WITHER, 300, 2));
				}
			}

			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				return 0.0f;
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.spawnEntity(new EC(entity, power));
				return true;
			}
		}
	}
}
