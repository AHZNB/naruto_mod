
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemGourd;
import net.narutomod.item.ItemJiton;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import java.util.Random;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySandLevitation extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 208;
	public static final int ENTITYID_RANGED = 209;
	private static final float CLOUD_SCALE = 2f;

	public EntitySandLevitation(ElementsNarutomodMod instance) {
		super(instance, 522);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
			.id(new ResourceLocation("narutomod", "sand_levitation"), ENTITYID).name("sand_levitation").tracker(64, 3, true).build());
	}

	public static class EC extends Entity {
		private static final DataParameter<Boolean> DEAD = EntityDataManager.<Boolean>createKey(EC.class, DataSerializers.BOOLEAN);
		private static final DataParameter<Integer> SUMMONER_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private ItemJiton.SwarmTarget sandCloud;
		private boolean firstRidden;
		private final int waitTime = 40;

		public EC(World world) {
			super(world);
			this.setSize(1.0f * CLOUD_SCALE, 0.25f * CLOUD_SCALE);
			this.isImmuneToFire = true;
		}

		public EC(EntityPlayer summonerIn) {
			this(summonerIn.world);
			this.setSummoner(summonerIn);
			Vec3d vec = summonerIn.getLookVec().scale(2d);
			vec = summonerIn.getPositionVector().addVector(vec.x, 0d, vec.z);
			this.setPosition(vec.x, vec.y, vec.z);
			this.sandCloud = new ItemJiton.SwarmTarget(this.world, 15, this.getGourdMouthPos(), 
			 vec, new Vec3d(0.1d, 0.4d, 0.1d), 0.5f, 0.03f, false, 2f, this.getSandType().getColor());
		}

		@Override
		protected void entityInit() {
			this.dataManager.register(DEAD, Boolean.valueOf(false));
			this.dataManager.register(SUMMONER_ID, Integer.valueOf(-1));
		}

		@Nullable
		public EntityPlayer getSummoner() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(SUMMONER_ID)).intValue());
			return entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
		}

		private void setSummoner(EntityPlayer summonerIn) {
			this.dataManager.set(SUMMONER_ID, Integer.valueOf(summonerIn.getEntityId()));
		}

		private ItemJiton.Type getSandType() {
			EntityPlayer summoner = this.getSummoner();
			if (summoner != null) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack(summoner, ItemJiton.block);
				if (stack != null) {
					return ItemJiton.getSandType(stack);
				}
			}
			return ItemJiton.Type.IRON;
		}

		public boolean getIsDead() {
			return ((Boolean)this.dataManager.get(DEAD)).booleanValue();
		}

		protected void setIsDead(boolean b) {
			this.dataManager.set(DEAD, Boolean.valueOf(b));
		}

		private Vec3d getGourdMouthPos() {
			EntityPlayer summoner = this.getSummoner();
			if (summoner != null) {
				return ItemGourd.getMouthPos(summoner);
			}
			return this.getPositionVector();
		}

		@Override
		public boolean processInitialInteract(EntityPlayer entity, EnumHand hand) {
			super.processInitialInteract(entity, hand);
			if (!this.world.isRemote) {
				entity.startRiding(this);
				this.firstRidden = true;
				return true;
			}
			return false;
		}

		@Override
		protected boolean canFitPassenger(Entity passenger) {
			return passenger instanceof EntityPlayer && this.getPassengers().size() < 3;
		}

		@Override
		public double getMountedYOffset() {
			return 0.35d + this.height;
		}
	
		@Override
		public boolean shouldRiderSit() {
			return false;
		}
	
		@Override
		public Entity getControllingPassenger() {
			return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
		}
	
		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec[] = { new Vec3d(0.3d, 0d, 0d), new Vec3d(-0.5, 0d, 0.4d), new Vec3d(-0.5, 0d, -0.4d) };
			if (this.isPassenger(passenger)) {
				int i = this.getPassengers().indexOf(passenger);
				Vec3d vec2 = vec[i].rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
				passenger.setPosition(this.posX + vec2.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec2.z);
			}
		}

		@Override
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}

		public boolean isSummonerControlling() {
			EntityPlayer summoner = this.getSummoner();
			return summoner != null && summoner.equals(this.getControllingPassenger());
		}

		private void updateSandParticles() {
			if (this.sandCloud != null) {
				if (this.sandCloud.shouldRemove()) {
					this.sandCloud = null;
				} else {
					if (!this.firstRidden) {
						if (this.sandCloud.getTicks() > this.waitTime) {
							this.sandCloud.forceRemove();
							EntityPlayer summoner = this.getSummoner();
							if (summoner != null && !summoner.isRiding()) {
								summoner.startRiding(this);
								this.firstRidden = true;
							}
						}
					} else if (!this.isSummonerControlling()) {
						this.sandCloud.setTarget(this.getGourdMouthPos(), true);
					}
					this.sandCloud.onUpdate();
				}
			} else if (this.firstRidden && !this.isSummonerControlling()) {
				this.sandCloud = new ItemJiton.SwarmTarget(this.world, 15, this.getPositionVector(), 
			 	 this.getGourdMouthPos(), new Vec3d(0.1d, 0.2d, 0.1d), 0.5f, 0.03f, true, 2f, this.getSandType().getColor());
			 	 this.setIsDead(true);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.updateSandParticles();
			if (this.isSummonerControlling()) {
				EntityPlayer rider = this.getSummoner();
				if (this.world.isRemote || Chakra.pathway(rider).consume(ItemJiton.SANDFLY.chakraUsage)) {
					this.rotationYaw = rider.rotationYaw;
					this.prevRotationYaw = this.rotationYaw;
					this.motionX *= 0.9d;
					this.motionY *= 0.9d;
					this.motionZ *= 0.9d;
					this.isAirBorne = true;
					float up = rider.moveForward > 0.0F ? -rider.rotationPitch / 45.0f : 0f;
					float strafe = rider.moveStrafing;
					float forward = rider.moveForward;
					this.moveRelative(strafe, up, forward, rider.isSprinting() ? 0.1f : 0.04f);
					this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
					rider.fallDistance = 0.0f;
				} else {
					rider.dismountRidingEntity();
				}
			} else if ((this.firstRidden && this.sandCloud == null) 
			 || (!this.world.isRemote && this.getSummoner() == null)) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "JitonSandLevitationEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (entity instanceof EntityPlayer) {
					Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ID_KEY));
					if (!(entity1 instanceof EC)) {
						entity1 = new EC((EntityPlayer)entity);
						entity.world.spawnEntity(entity1);
						entity.getEntityData().setInteger(ID_KEY, entity1.getEntityId());
						return true;
					}
				}
				return false;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends Render<EC> {
			private final ResourceLocation IRON_TEXTURE = new ResourceLocation("narutomod:textures/gray_dark.png");
			private final ResourceLocation SAND_TEXTURE = new ResourceLocation("minecraft:textures/blocks/sand.png");
			private final ModelSandCloud model = new ModelSandCloud();
	
			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.shadowSize = 0.5f * CLOUD_SCALE;
			}
	
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (!entity.getIsDead()) {
					GlStateManager.pushMatrix();
					this.bindEntityTexture(entity);
					GlStateManager.translate(x, y, z);
					GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
					float scale = MathHelper.clamp((float)entity.ticksExisted / ((float)entity.waitTime / CLOUD_SCALE), 0.0f, CLOUD_SCALE);
					GlStateManager.scale(scale, scale, scale);
					this.model.render(entity, 0f, 0f, 0f, 0f, 0f, 0.0625f);
					GlStateManager.popMatrix();
				}
			}
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return entity.getSandType() == ItemJiton.Type.SAND ? SAND_TEXTURE : IRON_TEXTURE;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelSandCloud extends ModelBase {
			private final ModelRenderer bb_main;
			private final Random rand = new Random();
		
			public ModelSandCloud() {
				textureWidth = 16;
				textureHeight = 16;
		
				bb_main = new ModelRenderer(this);
				bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -3.0F, 7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -3.0F, 7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -3.0F, 7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -3.0F, 7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -3.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -3.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -3.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -8.0F, -3.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -3.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -3.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -3.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 7.0F, -3.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 7.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 7.0F, -3.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 7.0F, -3.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -3.0F, -8.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -3.0F, -8.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -3.0F, -8.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -3.0F, -8.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -8.0F, -3.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -8.0F, -3.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -8.0F, -3.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -3.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -3.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -3.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -3.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -3.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -3.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -3.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -3.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -3.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -3.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -3.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -3.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -3.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -3.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -3.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -3.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -3.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -3.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -4.0F, -1.9F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -4.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -4.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -4.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -4.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -4.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -4.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -4.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -4.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -4.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -4.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 6.0F, -2.0F, -1.9F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 5.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -2.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -2.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -2.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -2.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -2.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, 6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -2.0F, -7.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -2.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, 5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -2.0F, -6.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -2.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -2.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -2.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -2.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -6.0F, -2.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -2.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -2.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -2.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -7.0F, -2.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -5.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -1.0F, -1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 4.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -1.0F, 0.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, 4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -1.0F, -1.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, -5.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 0.0F, -1.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -1.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -1.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -1.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -1.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -1.0F, 2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -1.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -1.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -1.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -3.0F, -1.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -1.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -1.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 2.0F, -1.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -1.0F, -3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 1.0F, -1.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), 3.0F, -1.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -1.0F, -2.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -1.0F, -4.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -2.0F, -1.0F, 3.0F, 1, 1, 1, 0.0F, false));
				bb_main.cubeList.add(new ModelBox(bb_main, rand.nextInt(13), rand.nextInt(15), -4.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				bb_main.render(f5);
			}
		}
	}
}
