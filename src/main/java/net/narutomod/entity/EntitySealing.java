
package net.narutomod.entity;

import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySealing extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 385;
	public static final int ENTITYID_RANGED = 386;

	public EntitySealing(ElementsNarutomodMod instance) {
		super(instance, 765);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class).id(new ResourceLocation("narutomod", "sealing"), ENTITYID)
				.name("sealing").tracker(96, 3, true).build());
	}

	public static class EC extends Entity implements IEntityMultiPart {
		private final EntitySittingCircle[] parts = new EntitySittingCircle[4];
		private final AxisAlignedBB tableBB = new AxisAlignedBB(-1.3d, 0.0d, -1.3d, 1.3d, 0.9d, 1.3d);
		private EntityTailedBeast.Base bijuEntity;
		//private EntityPlayer jinchurikiEntity;
		private int ticks2Death = 600;

		public EC(World world) {
			super(world);
			this.setSize(13.0f, 0.01f);
			this.isImmuneToFire = true;
			for (int i = 0; i < this.parts.length; i++) {
				this.parts[i] = new EntitySittingCircle(this, "circle"+i);
			}
		}

		public EC(World worldIn, BlockPos pos) {
			this(worldIn);
			double x = 0.5d + pos.getX();
			double y = pos.getY();
			double z = 0.5d + pos.getZ();
			this.setLocationAndAngles(x, y, z, 0f, 0f);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public Entity[] getParts() {
			return this.parts;
		}

		@Override
		public World getWorld() {
			return this.world;
		}

		@Override
		public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
			return false;
		}

		@Override
		public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
			if (!this.world.isRemote && !EntityBijuManager.isJinchuriki(player)) {
				AxisAlignedBB bb = this.tableBB.offset(this.posX, this.posY, this.posZ);
				Vec3d vec = player.getPositionEyes(1f);
				if (bb.calculateIntercept(vec, vec.add(player.getLookVec().scale(4d))) != null) {
					//this.jinchurikiEntity = player;
					return player.startRiding(this);
				}
			}
			return false;
		}

		@Override
		public double getMountedYOffset() {
			return 0.7d;
		}

		@Override @Nullable
		public Entity getControllingPassenger() {
			return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
		}

		@Override
		public void setDead() {
			super.setDead();
			for (int i = 0; i < this.parts.length; i++) {
				this.parts[i].removePassengers();
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			for (int i = 0; i < this.parts.length; i++) {
				this.parts[i].onUpdate();
			}
			this.parts[0].setLocationAndAngles(this.posX - 5d, this.posY + 0.005d, this.posZ, 0f, 0f);
			this.parts[1].setLocationAndAngles(this.posX, this.posY + 0.005d, this.posZ + 5d, 0f, 0f);
			this.parts[2].setLocationAndAngles(this.posX + 5d, this.posY + 0.005d, this.posZ, 0f, 0f);
			this.parts[3].setLocationAndAngles(this.posX, this.posY + 0.005d, this.posZ - 5d, 0f, 0f);
			if (this.bijuEntity == null) {
				for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox())) {
					if (entity instanceof EntityTailedBeast.Base && !((EntityTailedBeast.Base)entity).getBijuManager().isSealed()) {
						this.bijuEntity = (EntityTailedBeast.Base)entity;
					}
				}
			}
			if (!this.world.isRemote && this.bijuEntity != null) {
				if (this.bijuEntity.getBijuManager().isSealed()) {
					this.ticks2Death = 0;
				} else if (this.isBeingRidden() && this.getSealersCount() > 0) {
					if (!this.bijuEntity.isFuuinInProgress()) {
						this.bijuEntity.fuuinIntoVessel(this.getControllingPassenger(), 36000);
					} else {
						this.bijuEntity.incFuuinProgress(this.getSealersCount() - 1);
						this.sendSealingProgress(this.bijuEntity.getFuuinProgress());
						this.ticks2Death = 600;
					}
				} else if (this.bijuEntity.isFuuinInProgress()) {
					this.bijuEntity.cancelFuuin();
					this.ticks2Death = 0;
				}
			}
			if (!this.world.isRemote && --this.ticks2Death <= 0) {
				this.setDead();
			}
		}

		public boolean isSealingInProgress() {
			return this.bijuEntity != null && this.bijuEntity.isFuuinInProgress();
		}

		public int getSealersCount() {
			int j = 0;
			for (int i = 0; i < this.parts.length; i++) {
				j += this.parts[i].isBeingRidden() ? 1 : 0;
			}
			return j;
		}

		public void sendSealingProgress(float progress) {
			Entity entity = this.getControllingPassenger();
			if (entity instanceof EntityPlayer) {
				((EntityPlayer)entity).sendStatusMessage(new TextComponentString(String.format("%.1f", progress*100)+"%"), true);
			}
			for (int i = 0; i < this.parts.length; i++) {
				entity = this.parts[i].getControllingPassenger();
				if (entity instanceof EntityPlayer) {
					((EntityPlayer)entity).sendStatusMessage(new TextComponentString(String.format("%.1f", progress*100)+"%"), true);
				}
			}
		}

		@Override
		public boolean canBeCollidedWith() {
			return true;
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
				RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(entity, 10d, true);
				if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK && rtr.sideHit == EnumFacing.UP) {
					for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(rtr.getBlockPos().add(-6, 0, -6), rtr.getBlockPos().add(6, 0, 6))) {
						if (pos.distanceSq(rtr.getBlockPos()) < 49.0d) {
							if (!entity.world.getBlockState(pos).isFullBlock() || !entity.world.isAirBlock(pos.up(2))
							 || !this.isTorchOrAir(entity.world, pos.up(), rtr.getBlockPos())) {
								return false;
							}
						}
					}
					entity.world.playSound(null, rtr.getBlockPos().up(),
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:jutsu")),
					 net.minecraft.util.SoundCategory.NEUTRAL, 1f, 1f);
					entity.world.spawnEntity(new EC(entity.world, rtr.getBlockPos().up()));
					return true;
				}
				return false;
			}

			private boolean isTorchOrAir(World world, BlockPos pos, BlockPos centerPos) {
				BlockPos[] torchPos = { new BlockPos(-2, 1, 1), new BlockPos(-1, 1, 2), new BlockPos(1, 1, 2), new BlockPos(2, 1, 1),
				 new BlockPos(2, 1, -1), new BlockPos(1, 1, -2), new BlockPos(-1, 1, -2), new BlockPos(-2, 1, -1) };
				IBlockState blockstate = world.getBlockState(pos);
				for (BlockPos pos1 : torchPos) {
					if (pos.equals(centerPos.add(pos1))) {
						return blockstate.getBlock() == Blocks.TORCH;
					}
				}
				return blockstate.getBlock().isAir(blockstate, world, pos);
			}
		}
	}

	public static class EntitySittingCircle extends MultiPartEntityPart {
		private final EC ec;
		
		public EntitySittingCircle(IEntityMultiPart parent, String partName) {
			super(parent, partName, 1.0f, 0.01f);
			this.ec = (EC)parent;
		}

		@Override
		public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
			return player.startRiding(this);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.isBeingRidden()) {
				Entity passenger = this.getControllingPassenger();
				this.world.updateEntity(passenger);
				if (!(passenger instanceof EntityLivingBase) || passenger.isSneaking()
				 || (!this.world.isRemote && this.ticksExisted % 20 == 0 && this.ec.isSealingInProgress()
				  && !Chakra.pathway((EntityLivingBase)passenger).consume(10d))) {
					passenger.dismountRidingEntity();
				}
				if (!this.world.isRemote) {
					ProcedureSync.MultiPartsSetPassengers.sendToTracking(this.ec, this.getEntityId());
				}
			}
			++this.ticksExisted;
		}

		@Override
		public Entity getControllingPassenger() {
			return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
		}

		@Override
		public double getMountedYOffset() {
			return -0.25d;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderSeal(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderSeal extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/sealing_circle.png");
			protected ModelBase mainModel;

			public RenderSeal(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.mainModel = new ModelSeal();
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.disableCull();
				GlStateManager.translate(x, y + 0.01D, z);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				float f = partialTicks + entity.ticksExisted;
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F, 1.0F, Math.min(f / 60f, 1.0f));
				this.mainModel.render(entity, 0.0F, 0.0F, f, 0.0F, 0.0F, 0.85F);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.disableAlpha();
				GlStateManager.enableCull();
				GlStateManager.popMatrix();
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelSeal extends ModelBase {
			private final ModelRenderer bb_main;

			public ModelSeal() {
				this.textureWidth = 64;
				this.textureHeight = 16;
				this.bb_main = new ModelRenderer(this);
				this.bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 0, -8.0F, 0.0F, -8.0F, 16, 0, 16, 0.0F, false));
				this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 0, -1.5F, -1.0F, -1.5F, 3, 1, 3, 0.0F, false));
				this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 4, -1.5F, -1.2F, -1.5F, 3, 1, 3, -0.1F, false));
			}

			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				this.bb_main.render(f5);
			}
		}
	}
}
