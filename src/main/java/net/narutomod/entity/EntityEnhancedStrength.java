
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;

import net.narutomod.item.ItemIryoJutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.potion.PotionChakraEnhancedStrength;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEnhancedStrength extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 215;
	public static final int ENTITYID_RANGED = 216;

	public EntityEnhancedStrength(ElementsNarutomodMod instance) {
		super(instance, 528);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "enhanced_strength"), ENTITYID).name("enhanced_strength").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> USER_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private EntityLivingBase user;
		private int amplifier;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, int power) {
			this(userIn.world);
			this.setUser(userIn);
			this.amplifier = power;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
			this.setAlwaysRenderNameTag(false);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.IRYO;
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(USER_ID, Integer.valueOf(-1));
		}

		public EntityLivingBase getUser() {
			if (!this.world.isRemote) {
				return this.user;
			}
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(USER_ID)).intValue());
			return (entity instanceof EntityLivingBase) ? (EntityLivingBase)entity : null;
		}

		protected void setUser(EntityLivingBase userIn) {
			this.getDataManager().set(USER_ID, Integer.valueOf(userIn.getEntityId()));
			this.user = userIn;
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.user != null) {
				ItemStack stack = null;
				if (this.user instanceof EntityPlayer) {
					stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)this.user, ItemIryoJutsu.block);
				} else {
					stack = this.user.getHeldItemMainhand();
					if (stack.getItem() != ItemIryoJutsu.block) {
						stack = this.user.getHeldItemOffhand();
						if (stack.getItem() != ItemIryoJutsu.block) {
							stack = null;
						}
					}
				}
				if (stack != null && stack.hasTagCompound()) {
					stack.getTagCompound().removeTag(Jutsu.ID_KEY);
				}
			}
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				ItemStack stack = this.user.getHeldItemMainhand();
				if (this.ticksExisted % 10 == 2 && (stack.isEmpty() || stack.getItem() == ItemIryoJutsu.block)) {
					this.user.addPotionEffect(new PotionEffect(PotionChakraEnhancedStrength.potion, 12, this.amplifier, true, false));
				}
			} else if (!this.world.isRemote) {
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
			private static final String ID_KEY = "IryoEnhancedStrengthEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY));
				if (entity1 instanceof EC) {
					entity1.setDead();
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer)entity).sendStatusMessage(new TextComponentString("Off"), true);
					}
					return false;
				} else {
					entity1 = new EC(entity, (int)Chakra.getLevel(entity) / 2);
					entity.world.spawnEntity(entity1);
					if (!stack.hasTagCompound()) {
						stack.setTagCompound(new NBTTagCompound());
					}
					stack.getTagCompound().setInteger(ID_KEY, entity1.getEntityId());
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer)entity).sendStatusMessage(new TextComponentString("On"), true);
					}
					return true;
				}
			}

			@Override
			public boolean isActivated(ItemStack stack) {
				return stack.hasTagCompound() && stack.getTagCompound().hasKey(ID_KEY);
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EC> {
			private final Vec3d startVec = new Vec3d(-0.0625d, -0.6875d, 0.0d);
			private final Vec3d endVec = new Vec3d(-0.0625d, -0.4d, 0.0d);

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public boolean shouldRender(EC livingEntity, ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
			private Vec3d transform3rdPerson(Vec3d startvec, Vec3d angles, EntityLivingBase entity, float pt) {
				return new ProcedureUtils.RotationMatrix().rotateZ((float)-angles.z).rotateY((float)-angles.y).rotateX((float)-angles.x)
				 .transform(startvec).addVector(0.0625F * -5F, 1.375F-(entity.isSneaking()?0.2f:0f), 0.0F)
				 .rotateYaw((-entity.prevRenderYawOffset - (entity.renderYawOffset - entity.prevRenderYawOffset) * pt) * (float)(Math.PI / 180d))
				 .addVector(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt);
			}
	
			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				EntityLivingBase user = entity.getUser();
				if (user != null && user.isPotionActive(PotionChakraEnhancedStrength.potion) && (this.renderManager.options.thirdPersonView != 0 || user != this.renderManager.renderViewEntity)) {
					Render renderer = this.renderManager.getEntityRenderObject(user);
					if (renderer instanceof RenderLivingBase && ((RenderLivingBase)renderer).getMainModel() instanceof ModelBiped) {
						ModelRenderer armModel = ((ModelBiped)((RenderLivingBase)renderer).getMainModel()).bipedRightArm;
						Vec3d armAngles = new Vec3d(armModel.rotateAngleX, armModel.rotateAngleY, armModel.rotateAngleZ);
						Vec3d vec0 = this.transform3rdPerson(this.startVec, armAngles, user, partialTicks);
						Vec3d vec1 = this.transform3rdPerson(this.endVec, armAngles, user, partialTicks);
						this.spawnParticles(user, vec0, vec1, partialTicks);
					}
				}
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return null;
			}

			protected void spawnParticles(EntityLivingBase user, Vec3d startvec, Vec3d endvec, float partialTicks) {
				Vec3d vec = endvec.subtract(startvec).scale(0.8d);
				Particles.spawnParticle(user.world, Particles.Types.SMOKE, startvec.x, startvec.y, startvec.z, 
				  10, 0.075d, 0.05d, 0.075d, vec.x, vec.y, vec.z, 0x106AD1FF, 10, 3, 0xF0);
			}
		}
	}
}
