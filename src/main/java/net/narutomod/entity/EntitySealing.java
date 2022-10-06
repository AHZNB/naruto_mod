
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;

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
				.name("sealing").tracker(64, 3, true).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderSeal(renderManager));
	}

	public static class EC extends Entity implements IEntityMultiPart {
		private final EntitySittingCircle[] parts = new EntitySittingCircle[4];

		public EC(World world) {
			super(world);
			this.setSize(10f, 0.01f);
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
			return player.startRiding(this);
		}

		@Override
		public double getMountedYOffset() {
			return 0.7d;
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
			if (this.ticksExisted > 600 && !this.world.isRemote) {
				this.setDead();
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			//if (!this.world.isRemote) {
				for (int i = 0; i < this.parts.length; i++) {
					Entity passenger = this.parts[i].getControllingPassenger();
					if (passenger != null) {
						passenger.dismountRidingEntity();
					}
					//this.parts[i].removePassengers();
				}
			//}
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
				RayTraceResult result = ProcedureUtils.objectEntityLookingAt(entity, 10d, true);
				if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && result.sideHit == EnumFacing.UP) {
					entity.world.playSound(null, result.getBlockPos().up(),
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:jutsu")),
					 net.minecraft.util.SoundCategory.NEUTRAL, 1f, 1f);
					entity.world.spawnEntity(new EC(entity.world, result.getBlockPos().up()));
					return true;
				}
				return false;
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
				 || (this.ticksExisted % 20 == 0 && !Chakra.pathway((EntityLivingBase)passenger).consume(10d))) {
					passenger.dismountRidingEntity();
				} else if (!this.world.isRemote) {
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
