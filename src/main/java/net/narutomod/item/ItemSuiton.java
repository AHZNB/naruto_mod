
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.entity.*;
import net.narutomod.procedure.ProcedureRenderView;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.UUID;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSuiton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:suiton")
	public static final Item block = null;
	public static final int ENTITYID = 125;
	public static final int ENTITY2ID = 10125;
	public static final int ENTITY3ID = 11125;
	public static final ItemJutsu.JutsuEnum HIDINGINMIST = new ItemJutsu.JutsuEnum(0, "suitonmist", 'D', 100d, new EntityMist.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERBULLET = new ItemJutsu.JutsuEnum(1, "water_stream", 'C', 10d, new EntityWaterStream.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERDRAGON = new ItemJutsu.JutsuEnum(2, "water_dragon", 'B', 50d, new EntityWaterDragon.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERPRISON = new ItemJutsu.JutsuEnum(3, "water_prison", 'C', 200d, new EntityWaterPrison.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERSHARK = new ItemJutsu.JutsuEnum(4, "suiton_shark", 'B', 75d, new EntitySuitonShark.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERSHOCK = new ItemJutsu.JutsuEnum(5, "water_shockwave", 'B', 30d, new EntityWaterShockwave.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERBOMB = new ItemJutsu.JutsuEnum(6, "water_canonball", 'C', 30d, new EntityWaterCanonball.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum ACIDSPIT = new ItemJutsu.JutsuEnum(7, "acid_scattering", 'A', 20d, new EntityAcidScattering.EC.Jutsu());

	public ItemSuiton(ElementsNarutomodMod instance) {
		super(instance, 368);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(HIDINGINMIST, WATERBULLET, WATERDRAGON, WATERPRISON, WATERSHARK, WATERSHOCK, WATERBOMB, ACIDSPIT));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityMist.class)
				.id(new ResourceLocation("narutomod", "suitonmist"), ENTITYID).name("suitonmist").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:suiton", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.SUITON, list);
			this.setRegistryName("suiton");
			this.setUnlocalizedName("suiton");
			this.setCreativeTab(TabModTab.tab);
			//this.defaultCooldownMap[HIDINGINMIST.index] = 0;
			//this.defaultCooldownMap[WATERBULLET.index] = 0;
		}
	}

	public static class EntityMist extends Entity implements ItemJutsu.IJutsu {
		private static final UUID FOLLOW_MODIFIER = UUID.fromString("7c3e5536-e32d-4ef7-8cf2-e5ef57f9d48f");
		private final float density = 1.0f;
		private final int buildTime = 200;
		private final int DISSIPATE = 120;
		private int idleTime;
		private int dissipateTime;
		private double radius;
		private EntityLivingBase user;

		public EntityMist(World world) {
			super(world);
			this.setSize(0f, 0f);
		}

		public EntityMist(World world, double x, double y, double z, double r) {
			this(world);
			this.setPosition(x, y, z);
			this.radius = r;
			this.idleTime = this.buildTime + (world.containsAnyLiquid(new AxisAlignedBB(x-20, y-10, z-20, x+20, y+10, z+20)) ? 800 : 400);
			this.dissipateTime = this.idleTime + this.DISSIPATE;
		}

		public EntityMist(EntityLivingBase userIn) {
			this(userIn.world, userIn.posX, userIn.posY, userIn.posZ,
					userIn instanceof EntityPlayer ? Math.min(1.5d*((EntityPlayer)userIn).experienceLevel, 60d) : 32);
			this.user = userIn;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void setDead() {
			super.setDead();
			ProcedureRenderView.setFogDensity(this, 255d, false, 0f);
			for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(255))) {
				if (entity instanceof EntityLiving) {
					entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(FOLLOW_MODIFIER);
				}
				//if (!entity.equals(this.user) && this.user instanceof EntityPlayerMP) {
				//	ProcedureSync.SetGlowing.send((EntityPlayerMP)this.user, entity, false);
				//}
			}
		}

		@Override
		public void onUpdate() {
			if (!this.world.isRemote) {
				for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(this.radius+100))) {
					if (entity.equals(this.user) && !(this.user instanceof EntityPlayer)) {
						continue;
					}
					double d0 = 1f;
					double d1 = this.getDistance(entity) - this.radius;
					if (this.ticksExisted <= this.buildTime) {
						d0 = (double)this.ticksExisted / this.buildTime;
					} else if (this.ticksExisted > this.idleTime && this.ticksExisted < this.dissipateTime) {
						d0 = (double)(this.dissipateTime - this.ticksExisted) / (this.dissipateTime - this.idleTime);
					}
					if (entity instanceof EntityPlayer) {
						d0 = d0 * this.density / Math.max(d1, 1d);
						ProcedureRenderView.setFogDensity(entity, (float)d0, 20);
					} else if (entity instanceof EntityLiving) {
						IAttributeInstance aInstance = entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
						aInstance.removeModifier(FOLLOW_MODIFIER);
						double d2 = aInstance.getAttributeValue() - 2d;
						d0 = d0 * d2 - MathHelper.clamp(d1, 0d, d2);
						aInstance.applyModifier(new AttributeModifier(FOLLOW_MODIFIER, "suiton.followModifier", -d0, 0));
					}
					if (!entity.equals(this.user) && this.user instanceof EntityPlayerMP) {
						ProcedureSync.SetGlowing.send((EntityPlayerMP)this.user, entity, 5);
					}
				}
				if (this.ticksExisted >= this.dissipateTime) {
					this.setDead();
				}
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			this.ticksExisted = compound.getInteger("age");
			this.radius = compound.getDouble("radius");
			this.idleTime = compound.getInteger("idleTime");
			this.dissipateTime = compound.getInteger("dissipateTime");
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setInteger("age", this.ticksExisted);
			compound.setDouble("radius", this.radius);
			compound.setInteger("idleTime", this.idleTime);
			compound.setInteger("dissipateTime", this.dissipateTime);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.SUITON;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
						SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:kirigakurenojutsu"))),
						SoundCategory.PLAYERS, 5, 1f);
				entity.world.spawnEntity(new EntityMist(entity));
				return true;
			}
		}
	}
}
