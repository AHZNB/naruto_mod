
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityFutonGreatBreakthrough extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 187;
	public static final int ENTITYID_RANGED = 188;

	public EntityFutonGreatBreakthrough(ElementsNarutomodMod instance) {
		super(instance, 450);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "futon_great_breakthrough"), ENTITYID)
		 .name("futon_great_breakthrough").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		public static final float MAX_RANGE = 64.0f;
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
				if (this.ticksExisted == 1) {
					this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:wind"))),
					 1f, this.power * 0.2f);
				}
				this.airPunch.execute(this.user, this.power, this.power * 0.25d);
			}
			if (!this.world.isRemote && this.ticksExisted > (int)this.power) {
				this.setDead();
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

		public static class AirPunch extends ProcedureAirPunch {
			public AirPunch() {
				this.particlesDuring = null;
			}

			public void execute(EntityLivingBase player, double range, double radius) {
				this.blockHardnessLimit = (float)range / MAX_RANGE;
				super.execute(player, range, radius);
			}

			@Override
			protected void preExecuteParticles(EntityLivingBase player) {
				Vec3d vec0 = player.getLookVec();
				Vec3d vec = vec0.scale(2d).addVector(player.posX, player.posY + 1.5d, player.posZ);
				Particles.Renderer pRender = new Particles.Renderer(player.world);
				for (int i = 1; i <= 50; i++) {
					Vec3d vec1 = vec0.scale((player.getRNG().nextDouble()*0.8d+0.2d) * this.getRange(0) * 0.1d);
					pRender.spawnParticles(Particles.Types.SMOKE, vec.x, vec.y, vec.z, 1, 0d, 0d, 0d, 
					 vec1.x + (player.getRNG().nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 vec1.y + (player.getRNG().nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 vec1.z + (player.getRNG().nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 0x80FFFFFF, 80 + player.getRNG().nextInt(20), (int)(16.0D / (player.getRNG().nextDouble()*0.8D+0.2D)));
				}
				pRender.send();
			}

			@Override
			protected void attackEntityFrom(EntityLivingBase player, Entity target) {
				ProcedureUtils.pushEntity(player, target, this.getRange(0) * 1.6d, 3.0F);
			}

			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				return (1.0F - (float) (Math.sqrt(player.getDistanceSqToCenter(pos)) / MathHelper.clamp(range, 0.0D, 30.0D))) * 0.2f;
				//return 0.0f;
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
