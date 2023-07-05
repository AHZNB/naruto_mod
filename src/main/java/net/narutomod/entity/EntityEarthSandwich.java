
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemDoton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import java.util.Iterator;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEarthSandwich extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 177;
	public static final int ENTITYID_RANGED = 178;

	public EntityEarthSandwich(ElementsNarutomodMod instance) {
		super(instance, 443);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		  .id(new ResourceLocation("narutomod", "earth_sandwich"), ENTITYID).name("earth_sandwich").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private final float ogWidth = 0.875F;
		private final float ogHeight = 0.375F;
		private final int growTime = 30;
		private final Map<EntityLivingBase, Vec3d> caughtEntities = Maps.newHashMap();
		private EntityLivingBase user;

		public EC(World world) {
			super(world);
			this.setSize(this.ogWidth, this.ogHeight);
		}

		public EC(EntityLivingBase userIn, Vec3d atVec, float heightIn) {
			this(userIn.world);
			heightIn /= this.ogHeight;
			this.setScale(heightIn);
			this.setLocationAndAngles(atVec.x, atVec.y, atVec.z, userIn.rotationYawHead, 0f);
			this.user = userIn;
		}

		@Override
		public void entityInit() {
			this.getDataManager().register(SCALE, Float.valueOf(1.0f));
			this.getDataManager().register(AGE, Integer.valueOf(0));
		}

		public void setScale(float f) {
			this.getDataManager().set(SCALE, Float.valueOf(f));
			this.setSize(this.ogWidth * f, this.ogHeight * f);
		}

		public float getScale() {
			return ((Float)this.getDataManager().get(SCALE)).floatValue();
		}

		public void setAge(int age) {
			this.getDataManager().set(AGE, Integer.valueOf(age));
		}

		public int getAge() {
			return ((Integer)this.getDataManager().get(AGE)).intValue();
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (SCALE.equals(key) && this.world.isRemote) {
				float scale = this.getScale();
				this.setSize(this.ogWidth * scale, this.ogHeight * scale);
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				Particles.spawnParticle(this.world, Particles.Types.BLOCK_DUST, this.posX, this.posY + this.height * 0.5,
				 this.posZ, (int)(this.getScale() * 500), 0.25d * this.width, 0.3d * this.height, 0.25d * this.width,
				 0d, 0d, 0d, Block.getIdFromBlock(Blocks.DIRT), 40);
			}
		}

		@Override
		public void onUpdate() {
			int age = this.getAge();
    		if (age == 0) {
    			this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rocks")), 2.0F, 0.8F);
    		}
			if (!this.world.isRemote) {
				if (age == this.growTime / 2) {
					for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox())) {
						if (this.getEntityBoundingBox().intersect(entity.getEntityBoundingBox()).equals(entity.getEntityBoundingBox())
						 && ItemJutsu.canTarget(entity) /*&& !entity.equals(this.user)*/) {
							this.caughtEntities.put(entity, entity.getPositionVector());
						}
					}
				}
				float f = this.getScale();
				if (!this.caughtEntities.isEmpty()) {
					Iterator<Map.Entry<EntityLivingBase, Vec3d>> iter = this.caughtEntities.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<EntityLivingBase, Vec3d> entry = iter.next();
						EntityLivingBase entity = entry.getKey();
						if (ItemJutsu.canTarget(entity)) {
						 	Vec3d vec = entry.getValue();
							entity.setPositionAndUpdate(vec.x, vec.y, vec.z);
							if (age > this.growTime - 5) {
								entity.attackEntityFrom(DamageSource.IN_WALL, age > this.growTime ? 3f : (f * f * 0.5f));
							}
						} else if (!entity.isEntityAlive() || !entity.getEntityBoundingBox().intersects(this.getEntityBoundingBox())) {
							iter.remove();
						}
					}
				}
				if (age < this.growTime) {
					((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST,
					 this.posX, this.posY, this.posZ, (int)(f * f * 6f), this.width * 0.2, 0.5d, this.width * 0.2,
					 0.15d, Block.getIdFromBlock(Blocks.DIRT));
				}
				if (age > 600) {
					this.setDead();
				}
			}
			this.setAge(age + 1);
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			this.setScale(compound.getFloat("scale"));
			this.setAge(compound.getInteger("age"));
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setFloat("scale", this.getScale());
			compound.setInteger("age", this.getAge());
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 2f) {
					RayTraceResult rt = ProcedureUtils.raytraceBlocks(entity, 32d);
					if (rt != null && rt.typeOfHit == RayTraceResult.Type.BLOCK) {
						if (power >= 8f) {
							entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
							 .getObject(new ResourceLocation("narutomod:sando_no_jutsu")), SoundCategory.NEUTRAL, 5, 1f);
						} else {
							entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
							 .getObject(new ResourceLocation("narutomod:jutsu")), SoundCategory.NEUTRAL, 1, 1f);
						}
						entity.world.spawnEntity(new EC(entity, rt.hitVec, power));
						return true;
					}
				}
				return false;
			}
		}
	}

	/*public static class EC extends Entity {
		private ItemDoton.EntityEarthWall[] wall = new ItemDoton.EntityEarthWall[2];
		private float[] orientation = new float[2];
		private EntityEarthBlocks.BlocksMoveHelper[] moveHelper = new EntityEarthBlocks.BlocksMoveHelper[2];
		private int moveTick;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EC(EntityLivingBase user, Entity target, double widthIn) {
			this(user.world);
			this.setPosition(target.posX, target.posY, target.posZ);
			float yaw = this.facing(user.rotationYaw);
			Vec3d[] vec1 = {target.getPositionVector().add(Vec3d.fromPitchYaw(0f, yaw - 90f).scale(widthIn)),
			                target.getPositionVector().add(Vec3d.fromPitchYaw(0f, yaw + 90f).scale(widthIn))};
			for (int i = 0; i < 2; i++) {
				this.wall[i] = new ItemDoton.EntityEarthWall(user.world, vec1[i].x, vec1[i].y, vec1[i].z,
				  yaw + 90f, widthIn, widthIn, widthIn * 0.6d, false);
				this.world.spawnEntity(this.wall[i]);
			}
			this.orientation[0] = yaw + 90f;
			this.orientation[1] = yaw - 90f;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				for (int i = 0; i < 2; i++) {
					if (this.wall[i] != null && !this.wall[i].isDead) {
						this.wall[i].setDead();
					}
					if (this.moveHelper[i] != null) {
						this.moveHelper[i].fall();
					}
				}
			}
		}

		private float facing(float yaw) {
			return MathHelper.wrapDegrees((float)EnumFacing.fromAngle(yaw).getHorizontalIndex() * 90f);
		}

		@Override
		public void onUpdate() {
			if (this.wall[0] != null && this.wall[1] != null) {
				if (this.wall[0].isDone() && this.wall[1].isDone()) {
					if (this.moveTick == 0) {
						this.moveTick = this.ticksExisted;
						for (int i = 0; i < 2; i++) {
							this.moveHelper[i] = new EntityEarthBlocks.BlocksMoveHelper(this.world, this.wall[i].getAllBlocks());
							Vec3d vec = Vec3d.fromPitchYaw(0f, this.orientation[i]).scale(0.15d);
							this.moveHelper[i].move(vec.x, vec.y, vec.z);
						}
					}
					if (this.moveTick > 0 && this.ticksExisted <= this.moveTick + 100) {
						for (int i = 0; i < 2; i++) {
							this.moveHelper[i].move(this.moveHelper[i].motionX, this.moveHelper[i].motionY, this.moveHelper[i].motionZ);
							if (this.moveHelper[i].collided) {
					            for (BlockPos pos : this.moveHelper[i].getCollidedBlocks()) {
						        	ProcedureUtils.breakBlockAndDropWithChance(this.world, pos, this.moveHelper[i].destroyHardness(), 1f, 0.1f, false);
			           			}
							}
							for (Entity entity : this.moveHelper[i].getCollidedEntities()) {
								entity.attackEntityFrom(DamageSource.FALLING_BLOCK, this.moveHelper[i].collisionForce() * 4f);
							}
						}
					}
					if (this.moveTick > 0 && this.ticksExisted > this.moveTick + 100) {
						this.setDead();
					}
				}
			} else if (!this.world.isRemote) {
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

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power >= 2f) {
					RayTraceResult rt = ProcedureUtils.objectEntityLookingAt(entity, 30d);
					if (rt != null && rt.entityHit != null) {
						if (power >= 8f) {
							entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) SoundEvent.REGISTRY
							 .getObject(new ResourceLocation(("narutomod:sando_no_jutsu"))), SoundCategory.NEUTRAL, 5, 1f);
						} else {
							entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) SoundEvent.REGISTRY
							 .getObject(new ResourceLocation(("narutomod:jutsu"))), SoundCategory.NEUTRAL, 1, 1f);
						}
						entity.world.spawnEntity(new EC(entity, rt.entityHit, (double)power));
						return true;
					}
				}
				return false;
			}
		}
	}*/

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/dirt.png");
			private final ModelEarthMound model = new ModelEarthMound();
	
			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.shadowSize = 0.0f;
			}
	
			@Override
			public boolean shouldRender(EC entity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float pt) {
				float scale = entity.getScale();
				GlStateManager.pushMatrix();
				this.bindEntityTexture(entity);
				GlStateManager.translate(x, y, z);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entityYaw, 0.0F, 1.0F, 0.0F);
				float f = Math.min(((float)entity.getAge() + pt) / (float)entity.growTime, 1.0F);
				//GlStateManager.rotate(f * 90.0F, 0.0F, 0.0F, 1.0F);
				this.model.half1.rotateAngleZ = f * (float)Math.PI * 0.5F;
				this.model.half2.rotateAngleZ = -f * (float)Math.PI * 0.5F;
				this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelEarthMound extends ModelBase {
			private final ModelRenderer half1;
			private final ModelRenderer slice0;
			private final ModelRenderer base0;
			private final ModelRenderer insideA0;
			private final ModelRenderer insideB0;
			private final ModelRenderer bone4;
			private final ModelRenderer bone6;
			private final ModelRenderer bone8;
			private final ModelRenderer bone9;
			private final ModelRenderer bone44;
			private final ModelRenderer bone57;
			private final ModelRenderer bone58;
			private final ModelRenderer bone59;
			private final ModelRenderer bone60;
			private final ModelRenderer bone61;
			private final ModelRenderer bone62;
			private final ModelRenderer bone63;
			private final ModelRenderer bone64;
			private final ModelRenderer bone65;
			private final ModelRenderer bone66;
			private final ModelRenderer bone67;
			private final ModelRenderer bone68;
			private final ModelRenderer bone69;
			private final ModelRenderer bone70;
			private final ModelRenderer bone71;
			private final ModelRenderer bone72;
			private final ModelRenderer bone73;
			private final ModelRenderer slice1;
			private final ModelRenderer insideB1;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone5;
			private final ModelRenderer bone7;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone13;
			private final ModelRenderer bone14;
			private final ModelRenderer bone15;
			private final ModelRenderer bone16;
			private final ModelRenderer bone17;
			private final ModelRenderer bone18;
			private final ModelRenderer bone19;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
			private final ModelRenderer bone24;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			private final ModelRenderer bone27;
			private final ModelRenderer slice2;
			private final ModelRenderer insideB2;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer bone34;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer bone38;
			private final ModelRenderer bone39;
			private final ModelRenderer bone40;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
			private final ModelRenderer bone43;
			private final ModelRenderer bone45;
			private final ModelRenderer bone46;
			private final ModelRenderer bone47;
			private final ModelRenderer bone48;
			private final ModelRenderer bone49;
			private final ModelRenderer bone50;
			private final ModelRenderer slice3;
			private final ModelRenderer insideB3;
			private final ModelRenderer bone51;
			private final ModelRenderer bone52;
			private final ModelRenderer bone53;
			private final ModelRenderer bone54;
			private final ModelRenderer bone55;
			private final ModelRenderer bone56;
			private final ModelRenderer bone74;
			private final ModelRenderer bone75;
			private final ModelRenderer bone76;
			private final ModelRenderer bone77;
			private final ModelRenderer bone78;
			private final ModelRenderer bone79;
			private final ModelRenderer bone80;
			private final ModelRenderer bone81;
			private final ModelRenderer bone82;
			private final ModelRenderer bone83;
			private final ModelRenderer bone84;
			private final ModelRenderer bone85;
			private final ModelRenderer bone86;
			private final ModelRenderer bone87;
			private final ModelRenderer bone88;
			private final ModelRenderer bone89;
			private final ModelRenderer slice4;
			private final ModelRenderer insideB4;
			private final ModelRenderer bone90;
			private final ModelRenderer bone91;
			private final ModelRenderer bone92;
			private final ModelRenderer bone93;
			private final ModelRenderer bone94;
			private final ModelRenderer bone95;
			private final ModelRenderer bone96;
			private final ModelRenderer bone97;
			private final ModelRenderer bone98;
			private final ModelRenderer bone99;
			private final ModelRenderer bone100;
			private final ModelRenderer bone101;
			private final ModelRenderer bone102;
			private final ModelRenderer bone103;
			private final ModelRenderer bone104;
			private final ModelRenderer bone105;
			private final ModelRenderer bone106;
			private final ModelRenderer bone107;
			private final ModelRenderer bone108;
			private final ModelRenderer bone109;
			private final ModelRenderer bone110;
			private final ModelRenderer bone111;
			private final ModelRenderer slice5;
			private final ModelRenderer insideB5;
			private final ModelRenderer bone112;
			private final ModelRenderer bone113;
			private final ModelRenderer bone114;
			private final ModelRenderer bone115;
			private final ModelRenderer bone116;
			private final ModelRenderer bone117;
			private final ModelRenderer bone118;
			private final ModelRenderer bone119;
			private final ModelRenderer bone120;
			private final ModelRenderer bone121;
			private final ModelRenderer bone122;
			private final ModelRenderer bone123;
			private final ModelRenderer bone124;
			private final ModelRenderer bone125;
			private final ModelRenderer bone126;
			private final ModelRenderer bone127;
			private final ModelRenderer bone128;
			private final ModelRenderer bone129;
			private final ModelRenderer bone130;
			private final ModelRenderer bone131;
			private final ModelRenderer bone132;
			private final ModelRenderer bone133;
			private final ModelRenderer slice6;
			private final ModelRenderer insideB6;
			private final ModelRenderer bone134;
			private final ModelRenderer bone135;
			private final ModelRenderer bone136;
			private final ModelRenderer bone137;
			private final ModelRenderer bone138;
			private final ModelRenderer bone139;
			private final ModelRenderer bone140;
			private final ModelRenderer bone141;
			private final ModelRenderer bone142;
			private final ModelRenderer bone143;
			private final ModelRenderer bone144;
			private final ModelRenderer bone145;
			private final ModelRenderer bone146;
			private final ModelRenderer bone147;
			private final ModelRenderer bone148;
			private final ModelRenderer bone149;
			private final ModelRenderer bone150;
			private final ModelRenderer bone151;
			private final ModelRenderer bone152;
			private final ModelRenderer bone153;
			private final ModelRenderer bone154;
			private final ModelRenderer bone155;
			private final ModelRenderer half2;
			private final ModelRenderer slice7;
			private final ModelRenderer base1;
			private final ModelRenderer insideA1;
			private final ModelRenderer insideB7;
			private final ModelRenderer bone156;
			private final ModelRenderer bone157;
			private final ModelRenderer bone158;
			private final ModelRenderer bone159;
			private final ModelRenderer bone160;
			private final ModelRenderer bone161;
			private final ModelRenderer bone162;
			private final ModelRenderer bone163;
			private final ModelRenderer bone164;
			private final ModelRenderer bone165;
			private final ModelRenderer bone166;
			private final ModelRenderer bone167;
			private final ModelRenderer bone168;
			private final ModelRenderer bone169;
			private final ModelRenderer bone170;
			private final ModelRenderer bone171;
			private final ModelRenderer bone172;
			private final ModelRenderer bone173;
			private final ModelRenderer bone174;
			private final ModelRenderer bone175;
			private final ModelRenderer bone176;
			private final ModelRenderer bone177;
			private final ModelRenderer slice8;
			private final ModelRenderer insideB8;
			private final ModelRenderer bone178;
			private final ModelRenderer bone179;
			private final ModelRenderer bone180;
			private final ModelRenderer bone181;
			private final ModelRenderer bone182;
			private final ModelRenderer bone183;
			private final ModelRenderer bone184;
			private final ModelRenderer bone185;
			private final ModelRenderer bone186;
			private final ModelRenderer bone187;
			private final ModelRenderer bone188;
			private final ModelRenderer bone189;
			private final ModelRenderer bone190;
			private final ModelRenderer bone191;
			private final ModelRenderer bone192;
			private final ModelRenderer bone193;
			private final ModelRenderer bone194;
			private final ModelRenderer bone195;
			private final ModelRenderer bone196;
			private final ModelRenderer bone197;
			private final ModelRenderer bone198;
			private final ModelRenderer bone199;
			private final ModelRenderer slice9;
			private final ModelRenderer insideB9;
			private final ModelRenderer bone200;
			private final ModelRenderer bone201;
			private final ModelRenderer bone202;
			private final ModelRenderer bone203;
			private final ModelRenderer bone204;
			private final ModelRenderer bone205;
			private final ModelRenderer bone206;
			private final ModelRenderer bone207;
			private final ModelRenderer bone208;
			private final ModelRenderer bone209;
			private final ModelRenderer bone210;
			private final ModelRenderer bone211;
			private final ModelRenderer bone212;
			private final ModelRenderer bone213;
			private final ModelRenderer bone214;
			private final ModelRenderer bone215;
			private final ModelRenderer bone216;
			private final ModelRenderer bone217;
			private final ModelRenderer bone218;
			private final ModelRenderer bone219;
			private final ModelRenderer bone220;
			private final ModelRenderer bone221;
			private final ModelRenderer slice10;
			private final ModelRenderer insideB10;
			private final ModelRenderer bone222;
			private final ModelRenderer bone223;
			private final ModelRenderer bone224;
			private final ModelRenderer bone225;
			private final ModelRenderer bone226;
			private final ModelRenderer bone227;
			private final ModelRenderer bone228;
			private final ModelRenderer bone229;
			private final ModelRenderer bone230;
			private final ModelRenderer bone231;
			private final ModelRenderer bone232;
			private final ModelRenderer bone233;
			private final ModelRenderer bone234;
			private final ModelRenderer bone235;
			private final ModelRenderer bone236;
			private final ModelRenderer bone237;
			private final ModelRenderer bone238;
			private final ModelRenderer bone239;
			private final ModelRenderer bone240;
			private final ModelRenderer bone241;
			private final ModelRenderer bone242;
			private final ModelRenderer bone243;
			private final ModelRenderer slice11;
			private final ModelRenderer insideB11;
			private final ModelRenderer bone244;
			private final ModelRenderer bone245;
			private final ModelRenderer bone246;
			private final ModelRenderer bone247;
			private final ModelRenderer bone248;
			private final ModelRenderer bone249;
			private final ModelRenderer bone250;
			private final ModelRenderer bone251;
			private final ModelRenderer bone252;
			private final ModelRenderer bone253;
			private final ModelRenderer bone254;
			private final ModelRenderer bone255;
			private final ModelRenderer bone256;
			private final ModelRenderer bone257;
			private final ModelRenderer bone258;
			private final ModelRenderer bone259;
			private final ModelRenderer bone260;
			private final ModelRenderer bone261;
			private final ModelRenderer bone262;
			private final ModelRenderer bone263;
			private final ModelRenderer bone264;
			private final ModelRenderer bone265;
			private final ModelRenderer slice12;
			private final ModelRenderer insideB12;
			private final ModelRenderer bone266;
			private final ModelRenderer bone267;
			private final ModelRenderer bone268;
			private final ModelRenderer bone269;
			private final ModelRenderer bone270;
			private final ModelRenderer bone271;
			private final ModelRenderer bone272;
			private final ModelRenderer bone273;
			private final ModelRenderer bone274;
			private final ModelRenderer bone275;
			private final ModelRenderer bone276;
			private final ModelRenderer bone277;
			private final ModelRenderer bone278;
			private final ModelRenderer bone279;
			private final ModelRenderer bone280;
			private final ModelRenderer bone281;
			private final ModelRenderer bone282;
			private final ModelRenderer bone283;
			private final ModelRenderer bone284;
			private final ModelRenderer bone285;
			private final ModelRenderer bone286;
			private final ModelRenderer bone287;
			private final ModelRenderer slice13;
			private final ModelRenderer insideB13;
			private final ModelRenderer bone288;
			private final ModelRenderer bone289;
			private final ModelRenderer bone290;
			private final ModelRenderer bone291;
			private final ModelRenderer bone292;
			private final ModelRenderer bone293;
			private final ModelRenderer bone294;
			private final ModelRenderer bone295;
			private final ModelRenderer bone296;
			private final ModelRenderer bone297;
			private final ModelRenderer bone298;
			private final ModelRenderer bone299;
			private final ModelRenderer bone300;
			private final ModelRenderer bone301;
			private final ModelRenderer bone302;
			private final ModelRenderer bone303;
			private final ModelRenderer bone304;
			private final ModelRenderer bone305;
			private final ModelRenderer bone306;
			private final ModelRenderer bone307;
			private final ModelRenderer bone308;
			private final ModelRenderer bone309;
		
			public ModelEarthMound() {
				textureWidth = 32;
				textureHeight = 32;
		
				half1 = new ModelRenderer(this);
				half1.setRotationPoint(0.0F, 1.0F, 0.0F);
				//setRotationAngle(half1, 0.0F, 0.0F, 1.5708F);
				
		
				slice0 = new ModelRenderer(this);
				slice0.setRotationPoint(0.0F, 0.0F, 0.0F);
				half1.addChild(slice0);
				
		
				base0 = new ModelRenderer(this);
				base0.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(base0);
				base0.cubeList.add(new ModelBox(base0, 24, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
				base0.cubeList.add(new ModelBox(base0, 24, 0, -1.0F, -1.0F, 5.0F, 2, 2, 2, 0.0F, false));
		
				insideA0 = new ModelRenderer(this);
				insideA0.setRotationPoint(1.0F, 0.0F, 0.0F);
				base0.addChild(insideA0);
				insideA0.cubeList.add(new ModelBox(insideA0, 24, 20, -2.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, false));
				insideA0.cubeList.add(new ModelBox(insideA0, 24, 16, -2.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, false));
				insideA0.cubeList.add(new ModelBox(insideA0, 24, 12, -2.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				insideA0.cubeList.add(new ModelBox(insideA0, 12, 24, -2.0F, -1.0F, 3.0F, 2, 2, 2, 0.0F, false));
				insideA0.cubeList.add(new ModelBox(insideA0, 24, 8, -2.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				insideB0 = new ModelRenderer(this);
				insideB0.setRotationPoint(1.0F, 0.0F, 0.0F);
				slice0.addChild(insideB0);
				insideB0.cubeList.add(new ModelBox(insideB0, 0, 24, -4.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, false));
				insideB0.cubeList.add(new ModelBox(insideB0, 18, 22, -4.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				insideB0.cubeList.add(new ModelBox(insideB0, 6, 22, -4.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone4);
				setRotationAngle(bone4, 0.0F, 0.2618F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone6);
				setRotationAngle(bone6, 0.0F, 0.5236F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone8);
				setRotationAngle(bone8, 0.0F, 0.7854F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone9);
				setRotationAngle(bone9, 0.0F, 1.0472F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone44);
				setRotationAngle(bone44, 0.0F, 1.309F, 0.0F);
				bone44.cubeList.add(new ModelBox(bone44, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone57 = new ModelRenderer(this);
				bone57.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone57);
				setRotationAngle(bone57, 0.0F, 1.5708F, 0.0F);
				bone57.cubeList.add(new ModelBox(bone57, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone58 = new ModelRenderer(this);
				bone58.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone58);
				setRotationAngle(bone58, 0.0F, 1.8326F, 0.0F);
				bone58.cubeList.add(new ModelBox(bone58, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone59 = new ModelRenderer(this);
				bone59.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone59);
				setRotationAngle(bone59, 0.0F, 2.0944F, 0.0F);
				bone59.cubeList.add(new ModelBox(bone59, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone60 = new ModelRenderer(this);
				bone60.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone60);
				setRotationAngle(bone60, 0.0F, 2.3562F, 0.0F);
				bone60.cubeList.add(new ModelBox(bone60, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone61 = new ModelRenderer(this);
				bone61.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone61);
				setRotationAngle(bone61, 0.0F, 2.618F, 0.0F);
				bone61.cubeList.add(new ModelBox(bone61, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone62 = new ModelRenderer(this);
				bone62.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB0.addChild(bone62);
				setRotationAngle(bone62, 0.0F, 2.8798F, 0.0F);
				bone62.cubeList.add(new ModelBox(bone62, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone63 = new ModelRenderer(this);
				bone63.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone63);
				setRotationAngle(bone63, 0.0F, 0.2618F, 0.0F);
				bone63.cubeList.add(new ModelBox(bone63, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone64 = new ModelRenderer(this);
				bone64.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone64);
				setRotationAngle(bone64, 0.0F, 0.5236F, 0.0F);
				bone64.cubeList.add(new ModelBox(bone64, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone65 = new ModelRenderer(this);
				bone65.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone65);
				setRotationAngle(bone65, 0.0F, 0.7854F, 0.0F);
				bone65.cubeList.add(new ModelBox(bone65, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone66 = new ModelRenderer(this);
				bone66.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone66);
				setRotationAngle(bone66, 0.0F, 1.0472F, 0.0F);
				bone66.cubeList.add(new ModelBox(bone66, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone67 = new ModelRenderer(this);
				bone67.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone67);
				setRotationAngle(bone67, 0.0F, 1.309F, 0.0F);
				bone67.cubeList.add(new ModelBox(bone67, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone68 = new ModelRenderer(this);
				bone68.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone68);
				setRotationAngle(bone68, 0.0F, 1.5708F, 0.0F);
				bone68.cubeList.add(new ModelBox(bone68, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone69 = new ModelRenderer(this);
				bone69.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone69);
				setRotationAngle(bone69, 0.0F, 1.8326F, 0.0F);
				bone69.cubeList.add(new ModelBox(bone69, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone70 = new ModelRenderer(this);
				bone70.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone70);
				setRotationAngle(bone70, 0.0F, 2.0944F, 0.0F);
				bone70.cubeList.add(new ModelBox(bone70, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone71 = new ModelRenderer(this);
				bone71.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone71);
				setRotationAngle(bone71, 0.0F, 2.3562F, 0.0F);
				bone71.cubeList.add(new ModelBox(bone71, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone72 = new ModelRenderer(this);
				bone72.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone72);
				setRotationAngle(bone72, 0.0F, 2.618F, 0.0F);
				bone72.cubeList.add(new ModelBox(bone72, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone73 = new ModelRenderer(this);
				bone73.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice0.addChild(bone73);
				setRotationAngle(bone73, 0.0F, 2.8798F, 0.0F);
				bone73.cubeList.add(new ModelBox(bone73, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				slice1 = new ModelRenderer(this);
				slice1.setRotationPoint(0.0F, 0.0F, 0.0F);
				half1.addChild(slice1);
				setRotationAngle(slice1, 0.0F, 0.0F, -0.2618F);
				
		
				insideB1 = new ModelRenderer(this);
				insideB1.setRotationPoint(1.0F, 0.0F, 0.0F);
				slice1.addChild(insideB1);
				insideB1.cubeList.add(new ModelBox(insideB1, 0, 24, -4.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, false));
				insideB1.cubeList.add(new ModelBox(insideB1, 18, 22, -4.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				insideB1.cubeList.add(new ModelBox(insideB1, 6, 22, -4.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.2618F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone3);
				setRotationAngle(bone3, 0.0F, 0.5236F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone5);
				setRotationAngle(bone5, 0.0F, 0.7854F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone7);
				setRotationAngle(bone7, 0.0F, 1.0472F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone10);
				setRotationAngle(bone10, 0.0F, 1.309F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone11);
				setRotationAngle(bone11, 0.0F, 1.5708F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone12);
				setRotationAngle(bone12, 0.0F, 1.8326F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone13);
				setRotationAngle(bone13, 0.0F, 2.0944F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone14);
				setRotationAngle(bone14, 0.0F, 2.3562F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone15);
				setRotationAngle(bone15, 0.0F, 2.618F, 0.0F);
				bone15.cubeList.add(new ModelBox(bone15, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB1.addChild(bone16);
				setRotationAngle(bone16, 0.0F, 2.8798F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone17);
				setRotationAngle(bone17, 0.0F, 0.2618F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone18);
				setRotationAngle(bone18, 0.0F, 0.5236F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone19);
				setRotationAngle(bone19, 0.0F, 0.7854F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone20);
				setRotationAngle(bone20, 0.0F, 1.0472F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone21);
				setRotationAngle(bone21, 0.0F, 1.309F, 0.0F);
				bone21.cubeList.add(new ModelBox(bone21, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone22);
				setRotationAngle(bone22, 0.0F, 1.5708F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone23);
				setRotationAngle(bone23, 0.0F, 1.8326F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone24);
				setRotationAngle(bone24, 0.0F, 2.0944F, 0.0F);
				bone24.cubeList.add(new ModelBox(bone24, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone25);
				setRotationAngle(bone25, 0.0F, 2.3562F, 0.0F);
				bone25.cubeList.add(new ModelBox(bone25, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone26);
				setRotationAngle(bone26, 0.0F, 2.618F, 0.0F);
				bone26.cubeList.add(new ModelBox(bone26, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice1.addChild(bone27);
				setRotationAngle(bone27, 0.0F, 2.8798F, 0.0F);
				bone27.cubeList.add(new ModelBox(bone27, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				slice2 = new ModelRenderer(this);
				slice2.setRotationPoint(0.0F, 0.0F, 0.0F);
				half1.addChild(slice2);
				setRotationAngle(slice2, 0.0F, 0.0F, -0.5236F);
				
		
				insideB2 = new ModelRenderer(this);
				insideB2.setRotationPoint(1.0F, 0.0F, 0.0F);
				slice2.addChild(insideB2);
				insideB2.cubeList.add(new ModelBox(insideB2, 0, 24, -4.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, false));
				insideB2.cubeList.add(new ModelBox(insideB2, 18, 22, -4.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				insideB2.cubeList.add(new ModelBox(insideB2, 6, 22, -4.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, false));
		
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone28);
				setRotationAngle(bone28, 0.0F, 0.2618F, 0.0F);
				bone28.cubeList.add(new ModelBox(bone28, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone29);
				setRotationAngle(bone29, 0.0F, 0.5236F, 0.0F);
				bone29.cubeList.add(new ModelBox(bone29, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone30);
				setRotationAngle(bone30, 0.0F, 0.7854F, 0.0F);
				bone30.cubeList.add(new ModelBox(bone30, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone31);
				setRotationAngle(bone31, 0.0F, 1.0472F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone32);
				setRotationAngle(bone32, 0.0F, 1.309F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone33);
				setRotationAngle(bone33, 0.0F, 1.5708F, 0.0F);
				bone33.cubeList.add(new ModelBox(bone33, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone34);
				setRotationAngle(bone34, 0.0F, 1.8326F, 0.0F);
				bone34.cubeList.add(new ModelBox(bone34, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone35);
				setRotationAngle(bone35, 0.0F, 2.0944F, 0.0F);
				bone35.cubeList.add(new ModelBox(bone35, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone36);
				setRotationAngle(bone36, 0.0F, 2.3562F, 0.0F);
				bone36.cubeList.add(new ModelBox(bone36, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone37);
				setRotationAngle(bone37, 0.0F, 2.618F, 0.0F);
				bone37.cubeList.add(new ModelBox(bone37, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB2.addChild(bone38);
				setRotationAngle(bone38, 0.0F, 2.8798F, 0.0F);
				bone38.cubeList.add(new ModelBox(bone38, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone39);
				setRotationAngle(bone39, 0.0F, 0.2618F, 0.0F);
				bone39.cubeList.add(new ModelBox(bone39, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone40);
				setRotationAngle(bone40, 0.0F, 0.5236F, 0.0F);
				bone40.cubeList.add(new ModelBox(bone40, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone41);
				setRotationAngle(bone41, 0.0F, 0.7854F, 0.0F);
				bone41.cubeList.add(new ModelBox(bone41, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone42);
				setRotationAngle(bone42, 0.0F, 1.0472F, 0.0F);
				bone42.cubeList.add(new ModelBox(bone42, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone43);
				setRotationAngle(bone43, 0.0F, 1.309F, 0.0F);
				bone43.cubeList.add(new ModelBox(bone43, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone45);
				setRotationAngle(bone45, 0.0F, 1.5708F, 0.0F);
				bone45.cubeList.add(new ModelBox(bone45, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone46);
				setRotationAngle(bone46, 0.0F, 1.8326F, 0.0F);
				bone46.cubeList.add(new ModelBox(bone46, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone47);
				setRotationAngle(bone47, 0.0F, 2.0944F, 0.0F);
				bone47.cubeList.add(new ModelBox(bone47, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone48);
				setRotationAngle(bone48, 0.0F, 2.3562F, 0.0F);
				bone48.cubeList.add(new ModelBox(bone48, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone49);
				setRotationAngle(bone49, 0.0F, 2.618F, 0.0F);
				bone49.cubeList.add(new ModelBox(bone49, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice2.addChild(bone50);
				setRotationAngle(bone50, 0.0F, 2.8798F, 0.0F);
				bone50.cubeList.add(new ModelBox(bone50, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				slice3 = new ModelRenderer(this);
				slice3.setRotationPoint(0.0F, 0.0F, 0.0F);
				half1.addChild(slice3);
				setRotationAngle(slice3, 0.0F, 0.0F, -0.7854F);
				
		
				insideB3 = new ModelRenderer(this);
				insideB3.setRotationPoint(1.0F, 0.0F, 0.0F);
				slice3.addChild(insideB3);
				insideB3.cubeList.add(new ModelBox(insideB3, 0, 24, -4.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, false));
				insideB3.cubeList.add(new ModelBox(insideB3, 18, 22, -4.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				insideB3.cubeList.add(new ModelBox(insideB3, 6, 22, -4.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, false));
		
				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone51);
				setRotationAngle(bone51, 0.0F, 0.2618F, 0.0F);
				bone51.cubeList.add(new ModelBox(bone51, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone52 = new ModelRenderer(this);
				bone52.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone52);
				setRotationAngle(bone52, 0.0F, 0.5236F, 0.0F);
				bone52.cubeList.add(new ModelBox(bone52, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone53 = new ModelRenderer(this);
				bone53.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone53);
				setRotationAngle(bone53, 0.0F, 0.7854F, 0.0F);
				bone53.cubeList.add(new ModelBox(bone53, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone54 = new ModelRenderer(this);
				bone54.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone54);
				setRotationAngle(bone54, 0.0F, 1.0472F, 0.0F);
				bone54.cubeList.add(new ModelBox(bone54, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone55 = new ModelRenderer(this);
				bone55.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone55);
				setRotationAngle(bone55, 0.0F, 1.309F, 0.0F);
				bone55.cubeList.add(new ModelBox(bone55, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone56 = new ModelRenderer(this);
				bone56.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone56);
				setRotationAngle(bone56, 0.0F, 1.5708F, 0.0F);
				bone56.cubeList.add(new ModelBox(bone56, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone74 = new ModelRenderer(this);
				bone74.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone74);
				setRotationAngle(bone74, 0.0F, 1.8326F, 0.0F);
				bone74.cubeList.add(new ModelBox(bone74, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone75 = new ModelRenderer(this);
				bone75.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone75);
				setRotationAngle(bone75, 0.0F, 2.0944F, 0.0F);
				bone75.cubeList.add(new ModelBox(bone75, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone76 = new ModelRenderer(this);
				bone76.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone76);
				setRotationAngle(bone76, 0.0F, 2.3562F, 0.0F);
				bone76.cubeList.add(new ModelBox(bone76, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone77 = new ModelRenderer(this);
				bone77.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone77);
				setRotationAngle(bone77, 0.0F, 2.618F, 0.0F);
				bone77.cubeList.add(new ModelBox(bone77, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone78 = new ModelRenderer(this);
				bone78.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB3.addChild(bone78);
				setRotationAngle(bone78, 0.0F, 2.8798F, 0.0F);
				bone78.cubeList.add(new ModelBox(bone78, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone79 = new ModelRenderer(this);
				bone79.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone79);
				setRotationAngle(bone79, 0.0F, 0.2618F, 0.0F);
				bone79.cubeList.add(new ModelBox(bone79, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone80 = new ModelRenderer(this);
				bone80.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone80);
				setRotationAngle(bone80, 0.0F, 0.5236F, 0.0F);
				bone80.cubeList.add(new ModelBox(bone80, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone81 = new ModelRenderer(this);
				bone81.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone81);
				setRotationAngle(bone81, 0.0F, 0.7854F, 0.0F);
				bone81.cubeList.add(new ModelBox(bone81, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone82 = new ModelRenderer(this);
				bone82.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone82);
				setRotationAngle(bone82, 0.0F, 1.0472F, 0.0F);
				bone82.cubeList.add(new ModelBox(bone82, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone83 = new ModelRenderer(this);
				bone83.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone83);
				setRotationAngle(bone83, 0.0F, 1.309F, 0.0F);
				bone83.cubeList.add(new ModelBox(bone83, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone84 = new ModelRenderer(this);
				bone84.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone84);
				setRotationAngle(bone84, 0.0F, 1.5708F, 0.0F);
				bone84.cubeList.add(new ModelBox(bone84, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone85 = new ModelRenderer(this);
				bone85.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone85);
				setRotationAngle(bone85, 0.0F, 1.8326F, 0.0F);
				bone85.cubeList.add(new ModelBox(bone85, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone86 = new ModelRenderer(this);
				bone86.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone86);
				setRotationAngle(bone86, 0.0F, 2.0944F, 0.0F);
				bone86.cubeList.add(new ModelBox(bone86, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone87 = new ModelRenderer(this);
				bone87.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone87);
				setRotationAngle(bone87, 0.0F, 2.3562F, 0.0F);
				bone87.cubeList.add(new ModelBox(bone87, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone88 = new ModelRenderer(this);
				bone88.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone88);
				setRotationAngle(bone88, 0.0F, 2.618F, 0.0F);
				bone88.cubeList.add(new ModelBox(bone88, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone89 = new ModelRenderer(this);
				bone89.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice3.addChild(bone89);
				setRotationAngle(bone89, 0.0F, 2.8798F, 0.0F);
				bone89.cubeList.add(new ModelBox(bone89, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				slice4 = new ModelRenderer(this);
				slice4.setRotationPoint(0.0F, 0.0F, 0.0F);
				half1.addChild(slice4);
				setRotationAngle(slice4, 0.0F, 0.0F, -1.0472F);
				
		
				insideB4 = new ModelRenderer(this);
				insideB4.setRotationPoint(1.0F, 0.0F, 0.0F);
				slice4.addChild(insideB4);
				insideB4.cubeList.add(new ModelBox(insideB4, 0, 24, -4.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, false));
				insideB4.cubeList.add(new ModelBox(insideB4, 18, 22, -4.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				insideB4.cubeList.add(new ModelBox(insideB4, 6, 22, -4.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, false));
		
				bone90 = new ModelRenderer(this);
				bone90.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone90);
				setRotationAngle(bone90, 0.0F, 0.2618F, 0.0F);
				bone90.cubeList.add(new ModelBox(bone90, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone91 = new ModelRenderer(this);
				bone91.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone91);
				setRotationAngle(bone91, 0.0F, 0.5236F, 0.0F);
				bone91.cubeList.add(new ModelBox(bone91, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone92 = new ModelRenderer(this);
				bone92.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone92);
				setRotationAngle(bone92, 0.0F, 0.7854F, 0.0F);
				bone92.cubeList.add(new ModelBox(bone92, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone93 = new ModelRenderer(this);
				bone93.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone93);
				setRotationAngle(bone93, 0.0F, 1.0472F, 0.0F);
				bone93.cubeList.add(new ModelBox(bone93, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone94 = new ModelRenderer(this);
				bone94.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone94);
				setRotationAngle(bone94, 0.0F, 1.309F, 0.0F);
				bone94.cubeList.add(new ModelBox(bone94, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone95 = new ModelRenderer(this);
				bone95.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone95);
				setRotationAngle(bone95, 0.0F, 1.5708F, 0.0F);
				bone95.cubeList.add(new ModelBox(bone95, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone96 = new ModelRenderer(this);
				bone96.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone96);
				setRotationAngle(bone96, 0.0F, 1.8326F, 0.0F);
				bone96.cubeList.add(new ModelBox(bone96, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone97 = new ModelRenderer(this);
				bone97.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone97);
				setRotationAngle(bone97, 0.0F, 2.0944F, 0.0F);
				bone97.cubeList.add(new ModelBox(bone97, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone98 = new ModelRenderer(this);
				bone98.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone98);
				setRotationAngle(bone98, 0.0F, 2.3562F, 0.0F);
				bone98.cubeList.add(new ModelBox(bone98, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone99 = new ModelRenderer(this);
				bone99.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone99);
				setRotationAngle(bone99, 0.0F, 2.618F, 0.0F);
				bone99.cubeList.add(new ModelBox(bone99, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone100 = new ModelRenderer(this);
				bone100.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB4.addChild(bone100);
				setRotationAngle(bone100, 0.0F, 2.8798F, 0.0F);
				bone100.cubeList.add(new ModelBox(bone100, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone101 = new ModelRenderer(this);
				bone101.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone101);
				setRotationAngle(bone101, 0.0F, 0.2618F, 0.0F);
				bone101.cubeList.add(new ModelBox(bone101, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone102 = new ModelRenderer(this);
				bone102.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone102);
				setRotationAngle(bone102, 0.0F, 0.5236F, 0.0F);
				bone102.cubeList.add(new ModelBox(bone102, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone103 = new ModelRenderer(this);
				bone103.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone103);
				setRotationAngle(bone103, 0.0F, 0.7854F, 0.0F);
				bone103.cubeList.add(new ModelBox(bone103, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone104 = new ModelRenderer(this);
				bone104.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone104);
				setRotationAngle(bone104, 0.0F, 1.0472F, 0.0F);
				bone104.cubeList.add(new ModelBox(bone104, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone105 = new ModelRenderer(this);
				bone105.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone105);
				setRotationAngle(bone105, 0.0F, 1.309F, 0.0F);
				bone105.cubeList.add(new ModelBox(bone105, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone106 = new ModelRenderer(this);
				bone106.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone106);
				setRotationAngle(bone106, 0.0F, 1.5708F, 0.0F);
				bone106.cubeList.add(new ModelBox(bone106, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone107 = new ModelRenderer(this);
				bone107.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone107);
				setRotationAngle(bone107, 0.0F, 1.8326F, 0.0F);
				bone107.cubeList.add(new ModelBox(bone107, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone108 = new ModelRenderer(this);
				bone108.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone108);
				setRotationAngle(bone108, 0.0F, 2.0944F, 0.0F);
				bone108.cubeList.add(new ModelBox(bone108, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone109 = new ModelRenderer(this);
				bone109.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone109);
				setRotationAngle(bone109, 0.0F, 2.3562F, 0.0F);
				bone109.cubeList.add(new ModelBox(bone109, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone110 = new ModelRenderer(this);
				bone110.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone110);
				setRotationAngle(bone110, 0.0F, 2.618F, 0.0F);
				bone110.cubeList.add(new ModelBox(bone110, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone111 = new ModelRenderer(this);
				bone111.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice4.addChild(bone111);
				setRotationAngle(bone111, 0.0F, 2.8798F, 0.0F);
				bone111.cubeList.add(new ModelBox(bone111, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				slice5 = new ModelRenderer(this);
				slice5.setRotationPoint(0.0F, 0.0F, 0.0F);
				half1.addChild(slice5);
				setRotationAngle(slice5, 0.0F, 0.0F, -1.309F);
				
		
				insideB5 = new ModelRenderer(this);
				insideB5.setRotationPoint(1.0F, 0.0F, 0.0F);
				slice5.addChild(insideB5);
				insideB5.cubeList.add(new ModelBox(insideB5, 0, 24, -4.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, false));
				insideB5.cubeList.add(new ModelBox(insideB5, 18, 22, -4.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				insideB5.cubeList.add(new ModelBox(insideB5, 6, 22, -4.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, false));
		
				bone112 = new ModelRenderer(this);
				bone112.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone112);
				setRotationAngle(bone112, 0.0F, 0.2618F, 0.0F);
				bone112.cubeList.add(new ModelBox(bone112, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone113 = new ModelRenderer(this);
				bone113.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone113);
				setRotationAngle(bone113, 0.0F, 0.5236F, 0.0F);
				bone113.cubeList.add(new ModelBox(bone113, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone114 = new ModelRenderer(this);
				bone114.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone114);
				setRotationAngle(bone114, 0.0F, 0.7854F, 0.0F);
				bone114.cubeList.add(new ModelBox(bone114, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone115 = new ModelRenderer(this);
				bone115.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone115);
				setRotationAngle(bone115, 0.0F, 1.0472F, 0.0F);
				bone115.cubeList.add(new ModelBox(bone115, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone116 = new ModelRenderer(this);
				bone116.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone116);
				setRotationAngle(bone116, 0.0F, 1.309F, 0.0F);
				bone116.cubeList.add(new ModelBox(bone116, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone117 = new ModelRenderer(this);
				bone117.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone117);
				setRotationAngle(bone117, 0.0F, 1.5708F, 0.0F);
				bone117.cubeList.add(new ModelBox(bone117, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone118 = new ModelRenderer(this);
				bone118.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone118);
				setRotationAngle(bone118, 0.0F, 1.8326F, 0.0F);
				bone118.cubeList.add(new ModelBox(bone118, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone119 = new ModelRenderer(this);
				bone119.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone119);
				setRotationAngle(bone119, 0.0F, 2.0944F, 0.0F);
				bone119.cubeList.add(new ModelBox(bone119, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone120 = new ModelRenderer(this);
				bone120.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone120);
				setRotationAngle(bone120, 0.0F, 2.3562F, 0.0F);
				bone120.cubeList.add(new ModelBox(bone120, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone121 = new ModelRenderer(this);
				bone121.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone121);
				setRotationAngle(bone121, 0.0F, 2.618F, 0.0F);
				bone121.cubeList.add(new ModelBox(bone121, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone122 = new ModelRenderer(this);
				bone122.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB5.addChild(bone122);
				setRotationAngle(bone122, 0.0F, 2.8798F, 0.0F);
				bone122.cubeList.add(new ModelBox(bone122, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone123 = new ModelRenderer(this);
				bone123.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone123);
				setRotationAngle(bone123, 0.0F, 0.2618F, 0.0F);
				bone123.cubeList.add(new ModelBox(bone123, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone124 = new ModelRenderer(this);
				bone124.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone124);
				setRotationAngle(bone124, 0.0F, 0.5236F, 0.0F);
				bone124.cubeList.add(new ModelBox(bone124, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone125 = new ModelRenderer(this);
				bone125.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone125);
				setRotationAngle(bone125, 0.0F, 0.7854F, 0.0F);
				bone125.cubeList.add(new ModelBox(bone125, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone126 = new ModelRenderer(this);
				bone126.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone126);
				setRotationAngle(bone126, 0.0F, 1.0472F, 0.0F);
				bone126.cubeList.add(new ModelBox(bone126, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone127 = new ModelRenderer(this);
				bone127.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone127);
				setRotationAngle(bone127, 0.0F, 1.309F, 0.0F);
				bone127.cubeList.add(new ModelBox(bone127, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone128 = new ModelRenderer(this);
				bone128.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone128);
				setRotationAngle(bone128, 0.0F, 1.5708F, 0.0F);
				bone128.cubeList.add(new ModelBox(bone128, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone129 = new ModelRenderer(this);
				bone129.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone129);
				setRotationAngle(bone129, 0.0F, 1.8326F, 0.0F);
				bone129.cubeList.add(new ModelBox(bone129, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone130 = new ModelRenderer(this);
				bone130.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone130);
				setRotationAngle(bone130, 0.0F, 2.0944F, 0.0F);
				bone130.cubeList.add(new ModelBox(bone130, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone131 = new ModelRenderer(this);
				bone131.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone131);
				setRotationAngle(bone131, 0.0F, 2.3562F, 0.0F);
				bone131.cubeList.add(new ModelBox(bone131, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone132 = new ModelRenderer(this);
				bone132.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone132);
				setRotationAngle(bone132, 0.0F, 2.618F, 0.0F);
				bone132.cubeList.add(new ModelBox(bone132, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone133 = new ModelRenderer(this);
				bone133.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice5.addChild(bone133);
				setRotationAngle(bone133, 0.0F, 2.8798F, 0.0F);
				bone133.cubeList.add(new ModelBox(bone133, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				slice6 = new ModelRenderer(this);
				slice6.setRotationPoint(0.0F, 0.0F, 0.0F);
				half1.addChild(slice6);
				setRotationAngle(slice6, 0.0F, 0.0F, -1.5708F);
				
		
				insideB6 = new ModelRenderer(this);
				insideB6.setRotationPoint(1.0F, 0.0F, 0.0F);
				slice6.addChild(insideB6);
				insideB6.cubeList.add(new ModelBox(insideB6, 0, 24, -4.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, false));
				insideB6.cubeList.add(new ModelBox(insideB6, 18, 22, -4.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, false));
				insideB6.cubeList.add(new ModelBox(insideB6, 6, 22, -4.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, false));
		
				bone134 = new ModelRenderer(this);
				bone134.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone134);
				setRotationAngle(bone134, 0.0F, 0.2618F, 0.0F);
				bone134.cubeList.add(new ModelBox(bone134, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone135 = new ModelRenderer(this);
				bone135.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone135);
				setRotationAngle(bone135, 0.0F, 0.5236F, 0.0F);
				bone135.cubeList.add(new ModelBox(bone135, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone136 = new ModelRenderer(this);
				bone136.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone136);
				setRotationAngle(bone136, 0.0F, 0.7854F, 0.0F);
				bone136.cubeList.add(new ModelBox(bone136, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone137 = new ModelRenderer(this);
				bone137.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone137);
				setRotationAngle(bone137, 0.0F, 1.0472F, 0.0F);
				bone137.cubeList.add(new ModelBox(bone137, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone138 = new ModelRenderer(this);
				bone138.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone138);
				setRotationAngle(bone138, 0.0F, 1.309F, 0.0F);
				bone138.cubeList.add(new ModelBox(bone138, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone139 = new ModelRenderer(this);
				bone139.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone139);
				setRotationAngle(bone139, 0.0F, 1.5708F, 0.0F);
				bone139.cubeList.add(new ModelBox(bone139, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone140 = new ModelRenderer(this);
				bone140.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone140);
				setRotationAngle(bone140, 0.0F, 1.8326F, 0.0F);
				bone140.cubeList.add(new ModelBox(bone140, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone141 = new ModelRenderer(this);
				bone141.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone141);
				setRotationAngle(bone141, 0.0F, 2.0944F, 0.0F);
				bone141.cubeList.add(new ModelBox(bone141, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone142 = new ModelRenderer(this);
				bone142.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone142);
				setRotationAngle(bone142, 0.0F, 2.3562F, 0.0F);
				bone142.cubeList.add(new ModelBox(bone142, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone143 = new ModelRenderer(this);
				bone143.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone143);
				setRotationAngle(bone143, 0.0F, 2.618F, 0.0F);
				bone143.cubeList.add(new ModelBox(bone143, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone144 = new ModelRenderer(this);
				bone144.setRotationPoint(-1.0F, 0.0F, 0.0F);
				insideB6.addChild(bone144);
				setRotationAngle(bone144, 0.0F, 2.8798F, 0.0F);
				bone144.cubeList.add(new ModelBox(bone144, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, false));
		
				bone145 = new ModelRenderer(this);
				bone145.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone145);
				setRotationAngle(bone145, 0.0F, 0.2618F, 0.0F);
				bone145.cubeList.add(new ModelBox(bone145, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone146 = new ModelRenderer(this);
				bone146.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone146);
				setRotationAngle(bone146, 0.0F, 0.5236F, 0.0F);
				bone146.cubeList.add(new ModelBox(bone146, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone147 = new ModelRenderer(this);
				bone147.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone147);
				setRotationAngle(bone147, 0.0F, 0.7854F, 0.0F);
				bone147.cubeList.add(new ModelBox(bone147, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone148 = new ModelRenderer(this);
				bone148.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone148);
				setRotationAngle(bone148, 0.0F, 1.0472F, 0.0F);
				bone148.cubeList.add(new ModelBox(bone148, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone149 = new ModelRenderer(this);
				bone149.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone149);
				setRotationAngle(bone149, 0.0F, 1.309F, 0.0F);
				bone149.cubeList.add(new ModelBox(bone149, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone150 = new ModelRenderer(this);
				bone150.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone150);
				setRotationAngle(bone150, 0.0F, 1.5708F, 0.0F);
				bone150.cubeList.add(new ModelBox(bone150, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone151 = new ModelRenderer(this);
				bone151.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone151);
				setRotationAngle(bone151, 0.0F, 1.8326F, 0.0F);
				bone151.cubeList.add(new ModelBox(bone151, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone152 = new ModelRenderer(this);
				bone152.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone152);
				setRotationAngle(bone152, 0.0F, 2.0944F, 0.0F);
				bone152.cubeList.add(new ModelBox(bone152, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone153 = new ModelRenderer(this);
				bone153.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone153);
				setRotationAngle(bone153, 0.0F, 2.3562F, 0.0F);
				bone153.cubeList.add(new ModelBox(bone153, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone154 = new ModelRenderer(this);
				bone154.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone154);
				setRotationAngle(bone154, 0.0F, 2.618F, 0.0F);
				bone154.cubeList.add(new ModelBox(bone154, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				bone155 = new ModelRenderer(this);
				bone155.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice6.addChild(bone155);
				setRotationAngle(bone155, 0.0F, 2.8798F, 0.0F);
				bone155.cubeList.add(new ModelBox(bone155, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, false));
		
				half2 = new ModelRenderer(this);
				half2.setRotationPoint(0.0F, 1.0F, 0.0F);
				//setRotationAngle(half2, 0.0F, 0.0F, -1.5708F);
				
		
				slice7 = new ModelRenderer(this);
				slice7.setRotationPoint(0.0F, 0.0F, 0.0F);
				half2.addChild(slice7);
				
		
				base1 = new ModelRenderer(this);
				base1.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(base1);
				base1.cubeList.add(new ModelBox(base1, 24, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
				base1.cubeList.add(new ModelBox(base1, 24, 0, -1.0F, -1.0F, 5.0F, 2, 2, 2, 0.0F, true));
		
				insideA1 = new ModelRenderer(this);
				insideA1.setRotationPoint(-1.0F, 0.0F, 0.0F);
				base1.addChild(insideA1);
				insideA1.cubeList.add(new ModelBox(insideA1, 24, 20, 0.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, true));
				insideA1.cubeList.add(new ModelBox(insideA1, 24, 16, 0.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, true));
				insideA1.cubeList.add(new ModelBox(insideA1, 24, 12, 0.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
				insideA1.cubeList.add(new ModelBox(insideA1, 12, 24, 0.0F, -1.0F, 3.0F, 2, 2, 2, 0.0F, true));
				insideA1.cubeList.add(new ModelBox(insideA1, 24, 8, 0.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				insideB7 = new ModelRenderer(this);
				insideB7.setRotationPoint(-1.0F, 0.0F, 0.0F);
				slice7.addChild(insideB7);
				insideB7.cubeList.add(new ModelBox(insideB7, 0, 24, 2.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, true));
				insideB7.cubeList.add(new ModelBox(insideB7, 18, 22, 2.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
				insideB7.cubeList.add(new ModelBox(insideB7, 6, 22, 2.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, true));
		
				bone156 = new ModelRenderer(this);
				bone156.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone156);
				setRotationAngle(bone156, 0.0F, -0.2618F, 0.0F);
				bone156.cubeList.add(new ModelBox(bone156, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone157 = new ModelRenderer(this);
				bone157.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone157);
				setRotationAngle(bone157, 0.0F, -0.5236F, 0.0F);
				bone157.cubeList.add(new ModelBox(bone157, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone158 = new ModelRenderer(this);
				bone158.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone158);
				setRotationAngle(bone158, 0.0F, -0.7854F, 0.0F);
				bone158.cubeList.add(new ModelBox(bone158, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone159 = new ModelRenderer(this);
				bone159.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone159);
				setRotationAngle(bone159, 0.0F, -1.0472F, 0.0F);
				bone159.cubeList.add(new ModelBox(bone159, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone160 = new ModelRenderer(this);
				bone160.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone160);
				setRotationAngle(bone160, 0.0F, -1.309F, 0.0F);
				bone160.cubeList.add(new ModelBox(bone160, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone161 = new ModelRenderer(this);
				bone161.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone161);
				setRotationAngle(bone161, 0.0F, -1.5708F, 0.0F);
				bone161.cubeList.add(new ModelBox(bone161, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone162 = new ModelRenderer(this);
				bone162.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone162);
				setRotationAngle(bone162, 0.0F, -1.8326F, 0.0F);
				bone162.cubeList.add(new ModelBox(bone162, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone163 = new ModelRenderer(this);
				bone163.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone163);
				setRotationAngle(bone163, 0.0F, -2.0944F, 0.0F);
				bone163.cubeList.add(new ModelBox(bone163, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone164 = new ModelRenderer(this);
				bone164.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone164);
				setRotationAngle(bone164, 0.0F, -2.3562F, 0.0F);
				bone164.cubeList.add(new ModelBox(bone164, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone165 = new ModelRenderer(this);
				bone165.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone165);
				setRotationAngle(bone165, 0.0F, -2.618F, 0.0F);
				bone165.cubeList.add(new ModelBox(bone165, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone166 = new ModelRenderer(this);
				bone166.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB7.addChild(bone166);
				setRotationAngle(bone166, 0.0F, -2.8798F, 0.0F);
				bone166.cubeList.add(new ModelBox(bone166, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone167 = new ModelRenderer(this);
				bone167.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone167);
				setRotationAngle(bone167, 0.0F, -0.2618F, 0.0F);
				bone167.cubeList.add(new ModelBox(bone167, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone168 = new ModelRenderer(this);
				bone168.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone168);
				setRotationAngle(bone168, 0.0F, -0.5236F, 0.0F);
				bone168.cubeList.add(new ModelBox(bone168, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone169 = new ModelRenderer(this);
				bone169.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone169);
				setRotationAngle(bone169, 0.0F, -0.7854F, 0.0F);
				bone169.cubeList.add(new ModelBox(bone169, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone170 = new ModelRenderer(this);
				bone170.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone170);
				setRotationAngle(bone170, 0.0F, -1.0472F, 0.0F);
				bone170.cubeList.add(new ModelBox(bone170, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone171 = new ModelRenderer(this);
				bone171.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone171);
				setRotationAngle(bone171, 0.0F, -1.309F, 0.0F);
				bone171.cubeList.add(new ModelBox(bone171, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone172 = new ModelRenderer(this);
				bone172.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone172);
				setRotationAngle(bone172, 0.0F, -1.5708F, 0.0F);
				bone172.cubeList.add(new ModelBox(bone172, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone173 = new ModelRenderer(this);
				bone173.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone173);
				setRotationAngle(bone173, 0.0F, -1.8326F, 0.0F);
				bone173.cubeList.add(new ModelBox(bone173, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone174 = new ModelRenderer(this);
				bone174.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone174);
				setRotationAngle(bone174, 0.0F, -2.0944F, 0.0F);
				bone174.cubeList.add(new ModelBox(bone174, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone175 = new ModelRenderer(this);
				bone175.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone175);
				setRotationAngle(bone175, 0.0F, -2.3562F, 0.0F);
				bone175.cubeList.add(new ModelBox(bone175, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone176 = new ModelRenderer(this);
				bone176.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone176);
				setRotationAngle(bone176, 0.0F, -2.618F, 0.0F);
				bone176.cubeList.add(new ModelBox(bone176, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone177 = new ModelRenderer(this);
				bone177.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice7.addChild(bone177);
				setRotationAngle(bone177, 0.0F, -2.8798F, 0.0F);
				bone177.cubeList.add(new ModelBox(bone177, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				slice8 = new ModelRenderer(this);
				slice8.setRotationPoint(0.0F, 0.0F, 0.0F);
				half2.addChild(slice8);
				setRotationAngle(slice8, 0.0F, 0.0F, 0.2618F);
				
		
				insideB8 = new ModelRenderer(this);
				insideB8.setRotationPoint(-1.0F, 0.0F, 0.0F);
				slice8.addChild(insideB8);
				insideB8.cubeList.add(new ModelBox(insideB8, 0, 24, 2.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, true));
				insideB8.cubeList.add(new ModelBox(insideB8, 18, 22, 2.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
				insideB8.cubeList.add(new ModelBox(insideB8, 6, 22, 2.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, true));
		
				bone178 = new ModelRenderer(this);
				bone178.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone178);
				setRotationAngle(bone178, 0.0F, -0.2618F, 0.0F);
				bone178.cubeList.add(new ModelBox(bone178, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone179 = new ModelRenderer(this);
				bone179.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone179);
				setRotationAngle(bone179, 0.0F, -0.5236F, 0.0F);
				bone179.cubeList.add(new ModelBox(bone179, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone180 = new ModelRenderer(this);
				bone180.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone180);
				setRotationAngle(bone180, 0.0F, -0.7854F, 0.0F);
				bone180.cubeList.add(new ModelBox(bone180, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone181 = new ModelRenderer(this);
				bone181.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone181);
				setRotationAngle(bone181, 0.0F, -1.0472F, 0.0F);
				bone181.cubeList.add(new ModelBox(bone181, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone182 = new ModelRenderer(this);
				bone182.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone182);
				setRotationAngle(bone182, 0.0F, -1.309F, 0.0F);
				bone182.cubeList.add(new ModelBox(bone182, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone183 = new ModelRenderer(this);
				bone183.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone183);
				setRotationAngle(bone183, 0.0F, -1.5708F, 0.0F);
				bone183.cubeList.add(new ModelBox(bone183, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone184 = new ModelRenderer(this);
				bone184.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone184);
				setRotationAngle(bone184, 0.0F, -1.8326F, 0.0F);
				bone184.cubeList.add(new ModelBox(bone184, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone185 = new ModelRenderer(this);
				bone185.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone185);
				setRotationAngle(bone185, 0.0F, -2.0944F, 0.0F);
				bone185.cubeList.add(new ModelBox(bone185, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone186 = new ModelRenderer(this);
				bone186.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone186);
				setRotationAngle(bone186, 0.0F, -2.3562F, 0.0F);
				bone186.cubeList.add(new ModelBox(bone186, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone187 = new ModelRenderer(this);
				bone187.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone187);
				setRotationAngle(bone187, 0.0F, -2.618F, 0.0F);
				bone187.cubeList.add(new ModelBox(bone187, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone188 = new ModelRenderer(this);
				bone188.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB8.addChild(bone188);
				setRotationAngle(bone188, 0.0F, -2.8798F, 0.0F);
				bone188.cubeList.add(new ModelBox(bone188, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone189 = new ModelRenderer(this);
				bone189.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone189);
				setRotationAngle(bone189, 0.0F, -0.2618F, 0.0F);
				bone189.cubeList.add(new ModelBox(bone189, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone190 = new ModelRenderer(this);
				bone190.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone190);
				setRotationAngle(bone190, 0.0F, -0.5236F, 0.0F);
				bone190.cubeList.add(new ModelBox(bone190, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone191 = new ModelRenderer(this);
				bone191.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone191);
				setRotationAngle(bone191, 0.0F, -0.7854F, 0.0F);
				bone191.cubeList.add(new ModelBox(bone191, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone192 = new ModelRenderer(this);
				bone192.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone192);
				setRotationAngle(bone192, 0.0F, -1.0472F, 0.0F);
				bone192.cubeList.add(new ModelBox(bone192, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone193 = new ModelRenderer(this);
				bone193.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone193);
				setRotationAngle(bone193, 0.0F, -1.309F, 0.0F);
				bone193.cubeList.add(new ModelBox(bone193, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone194 = new ModelRenderer(this);
				bone194.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone194);
				setRotationAngle(bone194, 0.0F, -1.5708F, 0.0F);
				bone194.cubeList.add(new ModelBox(bone194, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone195 = new ModelRenderer(this);
				bone195.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone195);
				setRotationAngle(bone195, 0.0F, -1.8326F, 0.0F);
				bone195.cubeList.add(new ModelBox(bone195, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone196 = new ModelRenderer(this);
				bone196.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone196);
				setRotationAngle(bone196, 0.0F, -2.0944F, 0.0F);
				bone196.cubeList.add(new ModelBox(bone196, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone197 = new ModelRenderer(this);
				bone197.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone197);
				setRotationAngle(bone197, 0.0F, -2.3562F, 0.0F);
				bone197.cubeList.add(new ModelBox(bone197, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone198 = new ModelRenderer(this);
				bone198.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone198);
				setRotationAngle(bone198, 0.0F, -2.618F, 0.0F);
				bone198.cubeList.add(new ModelBox(bone198, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone199 = new ModelRenderer(this);
				bone199.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice8.addChild(bone199);
				setRotationAngle(bone199, 0.0F, -2.8798F, 0.0F);
				bone199.cubeList.add(new ModelBox(bone199, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				slice9 = new ModelRenderer(this);
				slice9.setRotationPoint(0.0F, 0.0F, 0.0F);
				half2.addChild(slice9);
				setRotationAngle(slice9, 0.0F, 0.0F, 0.5236F);
				
		
				insideB9 = new ModelRenderer(this);
				insideB9.setRotationPoint(-1.0F, 0.0F, 0.0F);
				slice9.addChild(insideB9);
				insideB9.cubeList.add(new ModelBox(insideB9, 0, 24, 2.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, true));
				insideB9.cubeList.add(new ModelBox(insideB9, 18, 22, 2.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
				insideB9.cubeList.add(new ModelBox(insideB9, 6, 22, 2.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, true));
		
				bone200 = new ModelRenderer(this);
				bone200.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone200);
				setRotationAngle(bone200, 0.0F, -0.2618F, 0.0F);
				bone200.cubeList.add(new ModelBox(bone200, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone201 = new ModelRenderer(this);
				bone201.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone201);
				setRotationAngle(bone201, 0.0F, -0.5236F, 0.0F);
				bone201.cubeList.add(new ModelBox(bone201, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone202 = new ModelRenderer(this);
				bone202.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone202);
				setRotationAngle(bone202, 0.0F, -0.7854F, 0.0F);
				bone202.cubeList.add(new ModelBox(bone202, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone203 = new ModelRenderer(this);
				bone203.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone203);
				setRotationAngle(bone203, 0.0F, -1.0472F, 0.0F);
				bone203.cubeList.add(new ModelBox(bone203, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone204 = new ModelRenderer(this);
				bone204.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone204);
				setRotationAngle(bone204, 0.0F, -1.309F, 0.0F);
				bone204.cubeList.add(new ModelBox(bone204, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone205 = new ModelRenderer(this);
				bone205.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone205);
				setRotationAngle(bone205, 0.0F, -1.5708F, 0.0F);
				bone205.cubeList.add(new ModelBox(bone205, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone206 = new ModelRenderer(this);
				bone206.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone206);
				setRotationAngle(bone206, 0.0F, -1.8326F, 0.0F);
				bone206.cubeList.add(new ModelBox(bone206, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone207 = new ModelRenderer(this);
				bone207.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone207);
				setRotationAngle(bone207, 0.0F, -2.0944F, 0.0F);
				bone207.cubeList.add(new ModelBox(bone207, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone208 = new ModelRenderer(this);
				bone208.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone208);
				setRotationAngle(bone208, 0.0F, -2.3562F, 0.0F);
				bone208.cubeList.add(new ModelBox(bone208, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone209 = new ModelRenderer(this);
				bone209.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone209);
				setRotationAngle(bone209, 0.0F, -2.618F, 0.0F);
				bone209.cubeList.add(new ModelBox(bone209, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone210 = new ModelRenderer(this);
				bone210.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB9.addChild(bone210);
				setRotationAngle(bone210, 0.0F, -2.8798F, 0.0F);
				bone210.cubeList.add(new ModelBox(bone210, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone211 = new ModelRenderer(this);
				bone211.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone211);
				setRotationAngle(bone211, 0.0F, -0.2618F, 0.0F);
				bone211.cubeList.add(new ModelBox(bone211, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone212 = new ModelRenderer(this);
				bone212.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone212);
				setRotationAngle(bone212, 0.0F, -0.5236F, 0.0F);
				bone212.cubeList.add(new ModelBox(bone212, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone213 = new ModelRenderer(this);
				bone213.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone213);
				setRotationAngle(bone213, 0.0F, -0.7854F, 0.0F);
				bone213.cubeList.add(new ModelBox(bone213, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone214 = new ModelRenderer(this);
				bone214.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone214);
				setRotationAngle(bone214, 0.0F, -1.0472F, 0.0F);
				bone214.cubeList.add(new ModelBox(bone214, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone215 = new ModelRenderer(this);
				bone215.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone215);
				setRotationAngle(bone215, 0.0F, -1.309F, 0.0F);
				bone215.cubeList.add(new ModelBox(bone215, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone216 = new ModelRenderer(this);
				bone216.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone216);
				setRotationAngle(bone216, 0.0F, -1.5708F, 0.0F);
				bone216.cubeList.add(new ModelBox(bone216, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone217 = new ModelRenderer(this);
				bone217.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone217);
				setRotationAngle(bone217, 0.0F, -1.8326F, 0.0F);
				bone217.cubeList.add(new ModelBox(bone217, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone218 = new ModelRenderer(this);
				bone218.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone218);
				setRotationAngle(bone218, 0.0F, -2.0944F, 0.0F);
				bone218.cubeList.add(new ModelBox(bone218, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone219 = new ModelRenderer(this);
				bone219.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone219);
				setRotationAngle(bone219, 0.0F, -2.3562F, 0.0F);
				bone219.cubeList.add(new ModelBox(bone219, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone220 = new ModelRenderer(this);
				bone220.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone220);
				setRotationAngle(bone220, 0.0F, -2.618F, 0.0F);
				bone220.cubeList.add(new ModelBox(bone220, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone221 = new ModelRenderer(this);
				bone221.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice9.addChild(bone221);
				setRotationAngle(bone221, 0.0F, -2.8798F, 0.0F);
				bone221.cubeList.add(new ModelBox(bone221, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				slice10 = new ModelRenderer(this);
				slice10.setRotationPoint(0.0F, 0.0F, 0.0F);
				half2.addChild(slice10);
				setRotationAngle(slice10, 0.0F, 0.0F, 0.7854F);
				
		
				insideB10 = new ModelRenderer(this);
				insideB10.setRotationPoint(-1.0F, 0.0F, 0.0F);
				slice10.addChild(insideB10);
				insideB10.cubeList.add(new ModelBox(insideB10, 0, 24, 2.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, true));
				insideB10.cubeList.add(new ModelBox(insideB10, 18, 22, 2.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
				insideB10.cubeList.add(new ModelBox(insideB10, 6, 22, 2.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, true));
		
				bone222 = new ModelRenderer(this);
				bone222.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone222);
				setRotationAngle(bone222, 0.0F, -0.2618F, 0.0F);
				bone222.cubeList.add(new ModelBox(bone222, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone223 = new ModelRenderer(this);
				bone223.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone223);
				setRotationAngle(bone223, 0.0F, -0.5236F, 0.0F);
				bone223.cubeList.add(new ModelBox(bone223, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone224 = new ModelRenderer(this);
				bone224.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone224);
				setRotationAngle(bone224, 0.0F, -0.7854F, 0.0F);
				bone224.cubeList.add(new ModelBox(bone224, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone225 = new ModelRenderer(this);
				bone225.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone225);
				setRotationAngle(bone225, 0.0F, -1.0472F, 0.0F);
				bone225.cubeList.add(new ModelBox(bone225, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone226 = new ModelRenderer(this);
				bone226.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone226);
				setRotationAngle(bone226, 0.0F, -1.309F, 0.0F);
				bone226.cubeList.add(new ModelBox(bone226, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone227 = new ModelRenderer(this);
				bone227.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone227);
				setRotationAngle(bone227, 0.0F, -1.5708F, 0.0F);
				bone227.cubeList.add(new ModelBox(bone227, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone228 = new ModelRenderer(this);
				bone228.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone228);
				setRotationAngle(bone228, 0.0F, -1.8326F, 0.0F);
				bone228.cubeList.add(new ModelBox(bone228, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone229 = new ModelRenderer(this);
				bone229.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone229);
				setRotationAngle(bone229, 0.0F, -2.0944F, 0.0F);
				bone229.cubeList.add(new ModelBox(bone229, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone230 = new ModelRenderer(this);
				bone230.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone230);
				setRotationAngle(bone230, 0.0F, -2.3562F, 0.0F);
				bone230.cubeList.add(new ModelBox(bone230, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone231 = new ModelRenderer(this);
				bone231.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone231);
				setRotationAngle(bone231, 0.0F, -2.618F, 0.0F);
				bone231.cubeList.add(new ModelBox(bone231, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone232 = new ModelRenderer(this);
				bone232.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB10.addChild(bone232);
				setRotationAngle(bone232, 0.0F, -2.8798F, 0.0F);
				bone232.cubeList.add(new ModelBox(bone232, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone233 = new ModelRenderer(this);
				bone233.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone233);
				setRotationAngle(bone233, 0.0F, -0.2618F, 0.0F);
				bone233.cubeList.add(new ModelBox(bone233, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone234 = new ModelRenderer(this);
				bone234.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone234);
				setRotationAngle(bone234, 0.0F, -0.5236F, 0.0F);
				bone234.cubeList.add(new ModelBox(bone234, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone235 = new ModelRenderer(this);
				bone235.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone235);
				setRotationAngle(bone235, 0.0F, -0.7854F, 0.0F);
				bone235.cubeList.add(new ModelBox(bone235, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone236 = new ModelRenderer(this);
				bone236.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone236);
				setRotationAngle(bone236, 0.0F, -1.0472F, 0.0F);
				bone236.cubeList.add(new ModelBox(bone236, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone237 = new ModelRenderer(this);
				bone237.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone237);
				setRotationAngle(bone237, 0.0F, -1.309F, 0.0F);
				bone237.cubeList.add(new ModelBox(bone237, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone238 = new ModelRenderer(this);
				bone238.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone238);
				setRotationAngle(bone238, 0.0F, -1.5708F, 0.0F);
				bone238.cubeList.add(new ModelBox(bone238, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone239 = new ModelRenderer(this);
				bone239.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone239);
				setRotationAngle(bone239, 0.0F, -1.8326F, 0.0F);
				bone239.cubeList.add(new ModelBox(bone239, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone240 = new ModelRenderer(this);
				bone240.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone240);
				setRotationAngle(bone240, 0.0F, -2.0944F, 0.0F);
				bone240.cubeList.add(new ModelBox(bone240, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone241 = new ModelRenderer(this);
				bone241.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone241);
				setRotationAngle(bone241, 0.0F, -2.3562F, 0.0F);
				bone241.cubeList.add(new ModelBox(bone241, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone242 = new ModelRenderer(this);
				bone242.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone242);
				setRotationAngle(bone242, 0.0F, -2.618F, 0.0F);
				bone242.cubeList.add(new ModelBox(bone242, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone243 = new ModelRenderer(this);
				bone243.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice10.addChild(bone243);
				setRotationAngle(bone243, 0.0F, -2.8798F, 0.0F);
				bone243.cubeList.add(new ModelBox(bone243, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				slice11 = new ModelRenderer(this);
				slice11.setRotationPoint(0.0F, 0.0F, 0.0F);
				half2.addChild(slice11);
				setRotationAngle(slice11, 0.0F, 0.0F, 1.0472F);
				
		
				insideB11 = new ModelRenderer(this);
				insideB11.setRotationPoint(-1.0F, 0.0F, 0.0F);
				slice11.addChild(insideB11);
				insideB11.cubeList.add(new ModelBox(insideB11, 0, 24, 2.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, true));
				insideB11.cubeList.add(new ModelBox(insideB11, 18, 22, 2.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
				insideB11.cubeList.add(new ModelBox(insideB11, 6, 22, 2.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, true));
		
				bone244 = new ModelRenderer(this);
				bone244.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone244);
				setRotationAngle(bone244, 0.0F, -0.2618F, 0.0F);
				bone244.cubeList.add(new ModelBox(bone244, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone245 = new ModelRenderer(this);
				bone245.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone245);
				setRotationAngle(bone245, 0.0F, -0.5236F, 0.0F);
				bone245.cubeList.add(new ModelBox(bone245, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone246 = new ModelRenderer(this);
				bone246.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone246);
				setRotationAngle(bone246, 0.0F, -0.7854F, 0.0F);
				bone246.cubeList.add(new ModelBox(bone246, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone247 = new ModelRenderer(this);
				bone247.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone247);
				setRotationAngle(bone247, 0.0F, -1.0472F, 0.0F);
				bone247.cubeList.add(new ModelBox(bone247, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone248 = new ModelRenderer(this);
				bone248.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone248);
				setRotationAngle(bone248, 0.0F, -1.309F, 0.0F);
				bone248.cubeList.add(new ModelBox(bone248, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone249 = new ModelRenderer(this);
				bone249.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone249);
				setRotationAngle(bone249, 0.0F, -1.5708F, 0.0F);
				bone249.cubeList.add(new ModelBox(bone249, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone250 = new ModelRenderer(this);
				bone250.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone250);
				setRotationAngle(bone250, 0.0F, -1.8326F, 0.0F);
				bone250.cubeList.add(new ModelBox(bone250, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone251 = new ModelRenderer(this);
				bone251.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone251);
				setRotationAngle(bone251, 0.0F, -2.0944F, 0.0F);
				bone251.cubeList.add(new ModelBox(bone251, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone252 = new ModelRenderer(this);
				bone252.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone252);
				setRotationAngle(bone252, 0.0F, -2.3562F, 0.0F);
				bone252.cubeList.add(new ModelBox(bone252, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone253 = new ModelRenderer(this);
				bone253.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone253);
				setRotationAngle(bone253, 0.0F, -2.618F, 0.0F);
				bone253.cubeList.add(new ModelBox(bone253, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone254 = new ModelRenderer(this);
				bone254.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB11.addChild(bone254);
				setRotationAngle(bone254, 0.0F, -2.8798F, 0.0F);
				bone254.cubeList.add(new ModelBox(bone254, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone255 = new ModelRenderer(this);
				bone255.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone255);
				setRotationAngle(bone255, 0.0F, -0.2618F, 0.0F);
				bone255.cubeList.add(new ModelBox(bone255, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone256 = new ModelRenderer(this);
				bone256.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone256);
				setRotationAngle(bone256, 0.0F, -0.5236F, 0.0F);
				bone256.cubeList.add(new ModelBox(bone256, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone257 = new ModelRenderer(this);
				bone257.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone257);
				setRotationAngle(bone257, 0.0F, -0.7854F, 0.0F);
				bone257.cubeList.add(new ModelBox(bone257, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone258 = new ModelRenderer(this);
				bone258.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone258);
				setRotationAngle(bone258, 0.0F, -1.0472F, 0.0F);
				bone258.cubeList.add(new ModelBox(bone258, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone259 = new ModelRenderer(this);
				bone259.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone259);
				setRotationAngle(bone259, 0.0F, -1.309F, 0.0F);
				bone259.cubeList.add(new ModelBox(bone259, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone260 = new ModelRenderer(this);
				bone260.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone260);
				setRotationAngle(bone260, 0.0F, -1.5708F, 0.0F);
				bone260.cubeList.add(new ModelBox(bone260, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone261 = new ModelRenderer(this);
				bone261.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone261);
				setRotationAngle(bone261, 0.0F, -1.8326F, 0.0F);
				bone261.cubeList.add(new ModelBox(bone261, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone262 = new ModelRenderer(this);
				bone262.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone262);
				setRotationAngle(bone262, 0.0F, -2.0944F, 0.0F);
				bone262.cubeList.add(new ModelBox(bone262, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone263 = new ModelRenderer(this);
				bone263.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone263);
				setRotationAngle(bone263, 0.0F, -2.3562F, 0.0F);
				bone263.cubeList.add(new ModelBox(bone263, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone264 = new ModelRenderer(this);
				bone264.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone264);
				setRotationAngle(bone264, 0.0F, -2.618F, 0.0F);
				bone264.cubeList.add(new ModelBox(bone264, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone265 = new ModelRenderer(this);
				bone265.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice11.addChild(bone265);
				setRotationAngle(bone265, 0.0F, -2.8798F, 0.0F);
				bone265.cubeList.add(new ModelBox(bone265, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				slice12 = new ModelRenderer(this);
				slice12.setRotationPoint(0.0F, 0.0F, 0.0F);
				half2.addChild(slice12);
				setRotationAngle(slice12, 0.0F, 0.0F, 1.309F);
				
		
				insideB12 = new ModelRenderer(this);
				insideB12.setRotationPoint(-1.0F, 0.0F, 0.0F);
				slice12.addChild(insideB12);
				insideB12.cubeList.add(new ModelBox(insideB12, 0, 24, 2.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, true));
				insideB12.cubeList.add(new ModelBox(insideB12, 18, 22, 2.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
				insideB12.cubeList.add(new ModelBox(insideB12, 6, 22, 2.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, true));
		
				bone266 = new ModelRenderer(this);
				bone266.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone266);
				setRotationAngle(bone266, 0.0F, -0.2618F, 0.0F);
				bone266.cubeList.add(new ModelBox(bone266, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone267 = new ModelRenderer(this);
				bone267.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone267);
				setRotationAngle(bone267, 0.0F, -0.5236F, 0.0F);
				bone267.cubeList.add(new ModelBox(bone267, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone268 = new ModelRenderer(this);
				bone268.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone268);
				setRotationAngle(bone268, 0.0F, -0.7854F, 0.0F);
				bone268.cubeList.add(new ModelBox(bone268, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone269 = new ModelRenderer(this);
				bone269.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone269);
				setRotationAngle(bone269, 0.0F, -1.0472F, 0.0F);
				bone269.cubeList.add(new ModelBox(bone269, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone270 = new ModelRenderer(this);
				bone270.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone270);
				setRotationAngle(bone270, 0.0F, -1.309F, 0.0F);
				bone270.cubeList.add(new ModelBox(bone270, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone271 = new ModelRenderer(this);
				bone271.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone271);
				setRotationAngle(bone271, 0.0F, -1.5708F, 0.0F);
				bone271.cubeList.add(new ModelBox(bone271, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone272 = new ModelRenderer(this);
				bone272.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone272);
				setRotationAngle(bone272, 0.0F, -1.8326F, 0.0F);
				bone272.cubeList.add(new ModelBox(bone272, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone273 = new ModelRenderer(this);
				bone273.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone273);
				setRotationAngle(bone273, 0.0F, -2.0944F, 0.0F);
				bone273.cubeList.add(new ModelBox(bone273, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone274 = new ModelRenderer(this);
				bone274.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone274);
				setRotationAngle(bone274, 0.0F, -2.3562F, 0.0F);
				bone274.cubeList.add(new ModelBox(bone274, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone275 = new ModelRenderer(this);
				bone275.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone275);
				setRotationAngle(bone275, 0.0F, -2.618F, 0.0F);
				bone275.cubeList.add(new ModelBox(bone275, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone276 = new ModelRenderer(this);
				bone276.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB12.addChild(bone276);
				setRotationAngle(bone276, 0.0F, -2.8798F, 0.0F);
				bone276.cubeList.add(new ModelBox(bone276, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone277 = new ModelRenderer(this);
				bone277.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone277);
				setRotationAngle(bone277, 0.0F, -0.2618F, 0.0F);
				bone277.cubeList.add(new ModelBox(bone277, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone278 = new ModelRenderer(this);
				bone278.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone278);
				setRotationAngle(bone278, 0.0F, -0.5236F, 0.0F);
				bone278.cubeList.add(new ModelBox(bone278, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone279 = new ModelRenderer(this);
				bone279.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone279);
				setRotationAngle(bone279, 0.0F, -0.7854F, 0.0F);
				bone279.cubeList.add(new ModelBox(bone279, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone280 = new ModelRenderer(this);
				bone280.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone280);
				setRotationAngle(bone280, 0.0F, -1.0472F, 0.0F);
				bone280.cubeList.add(new ModelBox(bone280, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone281 = new ModelRenderer(this);
				bone281.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone281);
				setRotationAngle(bone281, 0.0F, -1.309F, 0.0F);
				bone281.cubeList.add(new ModelBox(bone281, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone282 = new ModelRenderer(this);
				bone282.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone282);
				setRotationAngle(bone282, 0.0F, -1.5708F, 0.0F);
				bone282.cubeList.add(new ModelBox(bone282, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone283 = new ModelRenderer(this);
				bone283.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone283);
				setRotationAngle(bone283, 0.0F, -1.8326F, 0.0F);
				bone283.cubeList.add(new ModelBox(bone283, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone284 = new ModelRenderer(this);
				bone284.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone284);
				setRotationAngle(bone284, 0.0F, -2.0944F, 0.0F);
				bone284.cubeList.add(new ModelBox(bone284, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone285 = new ModelRenderer(this);
				bone285.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone285);
				setRotationAngle(bone285, 0.0F, -2.3562F, 0.0F);
				bone285.cubeList.add(new ModelBox(bone285, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone286 = new ModelRenderer(this);
				bone286.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone286);
				setRotationAngle(bone286, 0.0F, -2.618F, 0.0F);
				bone286.cubeList.add(new ModelBox(bone286, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone287 = new ModelRenderer(this);
				bone287.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice12.addChild(bone287);
				setRotationAngle(bone287, 0.0F, -2.8798F, 0.0F);
				bone287.cubeList.add(new ModelBox(bone287, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				slice13 = new ModelRenderer(this);
				slice13.setRotationPoint(0.0F, 0.0F, 0.0F);
				half2.addChild(slice13);
				setRotationAngle(slice13, 0.0F, 0.0F, 1.5708F);
				
		
				insideB13 = new ModelRenderer(this);
				insideB13.setRotationPoint(-1.0F, 0.0F, 0.0F);
				slice13.addChild(insideB13);
				insideB13.cubeList.add(new ModelBox(insideB13, 0, 24, 2.0F, -1.0F, 1.0F, 2, 2, 2, 0.0F, true));
				insideB13.cubeList.add(new ModelBox(insideB13, 18, 22, 2.0F, -1.0F, -1.0F, 2, 2, 2, 0.0F, true));
				insideB13.cubeList.add(new ModelBox(insideB13, 6, 22, 2.0F, -1.0F, -3.0F, 2, 2, 2, 0.0F, true));
		
				bone288 = new ModelRenderer(this);
				bone288.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone288);
				setRotationAngle(bone288, 0.0F, -0.2618F, 0.0F);
				bone288.cubeList.add(new ModelBox(bone288, 12, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone289 = new ModelRenderer(this);
				bone289.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone289);
				setRotationAngle(bone289, 0.0F, -0.5236F, 0.0F);
				bone289.cubeList.add(new ModelBox(bone289, 0, 20, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone290 = new ModelRenderer(this);
				bone290.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone290);
				setRotationAngle(bone290, 0.0F, -0.7854F, 0.0F);
				bone290.cubeList.add(new ModelBox(bone290, 18, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone291 = new ModelRenderer(this);
				bone291.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone291);
				setRotationAngle(bone291, 0.0F, -1.0472F, 0.0F);
				bone291.cubeList.add(new ModelBox(bone291, 18, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone292 = new ModelRenderer(this);
				bone292.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone292);
				setRotationAngle(bone292, 0.0F, -1.309F, 0.0F);
				bone292.cubeList.add(new ModelBox(bone292, 18, 10, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone293 = new ModelRenderer(this);
				bone293.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone293);
				setRotationAngle(bone293, 0.0F, -1.5708F, 0.0F);
				bone293.cubeList.add(new ModelBox(bone293, 18, 6, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone294 = new ModelRenderer(this);
				bone294.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone294);
				setRotationAngle(bone294, 0.0F, -1.8326F, 0.0F);
				bone294.cubeList.add(new ModelBox(bone294, 6, 18, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone295 = new ModelRenderer(this);
				bone295.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone295);
				setRotationAngle(bone295, 0.0F, -2.0944F, 0.0F);
				bone295.cubeList.add(new ModelBox(bone295, 18, 2, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone296 = new ModelRenderer(this);
				bone296.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone296);
				setRotationAngle(bone296, 0.0F, -2.3562F, 0.0F);
				bone296.cubeList.add(new ModelBox(bone296, 12, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone297 = new ModelRenderer(this);
				bone297.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone297);
				setRotationAngle(bone297, 0.0F, -2.618F, 0.0F);
				bone297.cubeList.add(new ModelBox(bone297, 0, 16, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone298 = new ModelRenderer(this);
				bone298.setRotationPoint(1.0F, 0.0F, 0.0F);
				insideB13.addChild(bone298);
				setRotationAngle(bone298, 0.0F, -2.8798F, 0.0F);
				bone298.cubeList.add(new ModelBox(bone298, 6, 14, -1.0F, -1.0F, -5.0F, 2, 2, 2, 0.0F, true));
		
				bone299 = new ModelRenderer(this);
				bone299.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone299);
				setRotationAngle(bone299, 0.0F, -0.2618F, 0.0F);
				bone299.cubeList.add(new ModelBox(bone299, 12, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone300 = new ModelRenderer(this);
				bone300.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone300);
				setRotationAngle(bone300, 0.0F, -0.5236F, 0.0F);
				bone300.cubeList.add(new ModelBox(bone300, 12, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone301 = new ModelRenderer(this);
				bone301.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone301);
				setRotationAngle(bone301, 0.0F, -0.7854F, 0.0F);
				bone301.cubeList.add(new ModelBox(bone301, 12, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone302 = new ModelRenderer(this);
				bone302.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone302);
				setRotationAngle(bone302, 0.0F, -1.0472F, 0.0F);
				bone302.cubeList.add(new ModelBox(bone302, 12, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone303 = new ModelRenderer(this);
				bone303.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone303);
				setRotationAngle(bone303, 0.0F, -1.309F, 0.0F);
				bone303.cubeList.add(new ModelBox(bone303, 0, 12, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone304 = new ModelRenderer(this);
				bone304.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone304);
				setRotationAngle(bone304, 0.0F, -1.5708F, 0.0F);
				bone304.cubeList.add(new ModelBox(bone304, 6, 10, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone305 = new ModelRenderer(this);
				bone305.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone305);
				setRotationAngle(bone305, 0.0F, -1.8326F, 0.0F);
				bone305.cubeList.add(new ModelBox(bone305, 0, 8, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone306 = new ModelRenderer(this);
				bone306.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone306);
				setRotationAngle(bone306, 0.0F, -2.0944F, 0.0F);
				bone306.cubeList.add(new ModelBox(bone306, 6, 6, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone307 = new ModelRenderer(this);
				bone307.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone307);
				setRotationAngle(bone307, 0.0F, -2.3562F, 0.0F);
				bone307.cubeList.add(new ModelBox(bone307, 6, 2, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone308 = new ModelRenderer(this);
				bone308.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone308);
				setRotationAngle(bone308, 0.0F, -2.618F, 0.0F);
				bone308.cubeList.add(new ModelBox(bone308, 0, 4, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
		
				bone309 = new ModelRenderer(this);
				bone309.setRotationPoint(0.0F, 0.0F, 0.0F);
				slice13.addChild(bone309);
				setRotationAngle(bone309, 0.0F, -2.8798F, 0.0F);
				bone309.cubeList.add(new ModelBox(bone309, 0, 0, -1.0F, -1.0F, -7.0F, 2, 2, 2, 0.0F, true));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				half1.render(f5);
				half2.render(f5);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
