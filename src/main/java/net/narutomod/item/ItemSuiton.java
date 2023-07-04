
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.BlockLiquid;

import net.narutomod.entity.*;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.procedure.ProcedureRenderView;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.UUID;
import java.util.Random;
import com.google.common.collect.ImmutableMap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSuiton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:suiton")
	public static final Item block = null;
	public static final int ENTITYID = 125;
	public static final int ENTITY2ID = 10125;
	public static final int ENTITY3ID = 11125;
	public static final ItemJutsu.JutsuEnum HIDINGINMIST = new ItemJutsu.JutsuEnum(0, "suitonmist", 'D', 100d, new EntityMist.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERBULLET = new ItemJutsu.JutsuEnum(1, "suitonstream", 'C', 10d, new EntityStream.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERDRAGON = new ItemJutsu.JutsuEnum(2, "water_dragon", 'B', 50d, new EntityWaterDragon.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERPRISON = new ItemJutsu.JutsuEnum(3, "water_prison", 'C', 200d, new EntityWaterPrison.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERSHARK = new ItemJutsu.JutsuEnum(4, "suiton_shark", 'B', 75d, new EntitySuitonShark.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WATERSHOCK = new ItemJutsu.JutsuEnum(5, "water_shockwave", 'B', 30d, new EntityWaterShockwave.EC.Jutsu());

	public ItemSuiton(ElementsNarutomodMod instance) {
		super(instance, 368);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(HIDINGINMIST, WATERBULLET, WATERDRAGON, WATERPRISON, WATERSHARK, WATERSHOCK));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityStream.class)
				.id(new ResourceLocation("narutomod", "suitonstream"), ENTITYID).name("suitonstream").tracker(64, 1, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityMist.class)
				.id(new ResourceLocation("narutomod", "suitonmist"), ENTITY2ID).name("suitonmist").tracker(64, 1, true).build());
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

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == WATERSHARK || jutsu == WATERDRAGON) {
				return this.getPower(stack, entity, timeLeft, 0.9f, 150);
			} else if (jutsu == WATERSHOCK) {
				return this.getPower(stack, entity, timeLeft, 5f, 50);
			} else if (jutsu == WATERBULLET) {
				return this.getPower(stack, entity, timeLeft, 5f, 20);
			}
			return 1f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float ret = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return jutsu == WATERSHOCK ? Math.min(ret, 25f)
			     : jutsu == WATERBULLET ? Math.min(ret, 30f)
			     : jutsu == WATERSHARK ? Math.min(ret, 5f)
			     : jutsu == WATERDRAGON ? Math.min(ret, 5f)
			     : ret;
		}
	}

	public static class EntityMist extends Entity {
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

	public static class EntityStream extends EntityBeamBase.Base {
		private final AirPunch stream = new AirPunch();
		private final int maxLife = 100;
		private final float damageModifier = 0.5f;
		private float power;

		public EntityStream(World a) {
			super(a);
		}

		public EntityStream(EntityLivingBase shooter, float scale) {
			super(shooter);
			this.power = scale;
		}

		public void shoot() {
			if (this.shootingEntity != null) {
				Vec3d vec3d = this.shootingEntity.getLookVec();
				Vec3d vec3d1 = vec3d.addVector(this.shootingEntity.posX,
						this.shootingEntity.posY + this.shootingEntity.getEyeHeight() - 0.2d, this.shootingEntity.posZ);
				this.setPositionAndRotation(vec3d1.x, vec3d1.y, vec3d1.z, this.shootingEntity.rotationYaw, this.shootingEntity.rotationPitch);
				vec3d1 = vec3d.scale(this.power);
				this.shoot(vec3d1.x, vec3d1.y, vec3d1.z);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null) {
				if (this.ticksAlive == 1) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:waterblast")), 0.5f, this.power / 30f);
				}
				this.shoot();
				//if (this.ticksAlive % 2 == 1) {
					this.stream.execute2((EntityLivingBase)this.shootingEntity, (double)this.power, 0.5d);
				//}
			}
			if (this.ticksAlive > this.maxLife) {
				this.setDead();
			}
		}

		public class AirPunch extends ProcedureAirPunch {
			public AirPunch() {
				this.blockDropChance = 0.4F;
				this.blockHardnessLimit = 5f;
				this.particlesPre = EnumParticleTypes.WATER_DROP;
				this.particlesDuring = EnumParticleTypes.WATER_WAKE;
			}

			@Override
			protected void preExecuteParticles(EntityLivingBase player) {
				double range = this.getRange(0);
				Particles.Renderer particles = new Particles.Renderer(player.world);
				for (int i = 1, j = (int)(range * 5d); i < j; i++) {
					Vec3d vec = EntityStream.this.getPositionVector().addVector((this.rand.nextDouble()-0.5d) * 0.25d,
					 this.rand.nextDouble() * 0.25d, (this.rand.nextDouble()-0.5d) * 0.25d);
					Vec3d vec3d = player.getLookVec().scale(range * (this.rand.nextDouble() * 0.5d + 0.5d) * 0.4d);
					particles.spawnParticles(Particles.Types.WATER_SPLASH, vec.x, vec.y, vec.z,
					 1, 0, 0, 0, vec3d.x, vec3d.y, vec3d.z, 10 + this.rand.nextInt(15));
				}
				particles.send();
			}

			@Override
			protected void attackEntityFrom(EntityLivingBase player, Entity target) {
				target.attackEntityFrom(ItemJutsu.causeJutsuDamage(EntityStream.this, player),
						EntityStream.this.power * EntityStream.this.damageModifier);
			}

			@Override
			protected EntityItem processAffectedBlock(EntityLivingBase player, BlockPos pos, EnumFacing facing) {
				EntityItem ret = super.processAffectedBlock(player, pos, facing);
				if (ret != null && player.world.isAirBlock(pos.up())) {
					new net.narutomod.event.EventSetBlocks(player.world, ImmutableMap.of(pos.up(),
					 Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, Integer.valueOf(1))),
					 0, 10, false, false);
				}
				return ret;
			}

			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				return 1.0F - (float) ((Math.sqrt(player.getDistanceSqToCenter(pos))) / range);
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 5.0f) {
					EntityStream entityarrow = new EntityStream(entity, power);
					entityarrow.shoot();
					entity.world.spawnEntity(entityarrow);
					return true;
				}
				return false;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityStream.class, renderManager -> new RenderStream(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderStream extends EntityBeamBase.Renderer<EntityStream> {
			private final ResourceLocation TEXTURE = new ResourceLocation("minecraft:textures/blocks/water_flow.png");

			public RenderStream(RenderManager renderManager) {
				super(renderManager);
			}

			@Override
			public EntityBeamBase.Model getMainModel(EntityStream entity) {
				float f = entity.ticksAlive >= entity.maxLife - 10
						? Math.max((float)(entity.maxLife - entity.ticksAlive) / 10f, 0f) : Math.min((float)entity.ticksAlive / 10f, 1f);
				return new ModelLongCube(entity.getBeamLength() * f);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityStream entity) {
				return TEXTURE;
			}
		}

		// Made with Blockbench 3.5.4
		// Exported for Minecraft version 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelLongCube extends EntityBeamBase.Model {
			private final ModelRenderer bone;
			protected float scale = 1.0F;

			public ModelLongCube(float length) {
				this.textureWidth = 32;
				this.textureHeight = 1024;
				this.bone = new ModelRenderer(this);
				this.bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -4.0F, -16.0F, -4.0F, 8, (int) (16f * length), 8, 0.0F, false));
			}

			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, (this.scale - 1.0F) * 1.5F + 1F, 0.0F);
				GlStateManager.scale(this.scale, this.scale, this.scale);
				GlStateManager.color(1f, 1f, 1f, 1f);
				this.bone.render(f5);
				GlStateManager.popMatrix();
			}
		}
	}
}
