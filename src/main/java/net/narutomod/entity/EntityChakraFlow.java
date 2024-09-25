
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.MobEffects;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;

@ElementsNarutomodMod.ModElement.Tag
public class EntityChakraFlow extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 150;
	public static final int ENTITYID_RANGED = 151;

	public EntityChakraFlow(ElementsNarutomodMod instance) {
		super(instance, 408);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(Base.Message.Handler.class, Base.Message.class, Side.CLIENT);
	}

	public static abstract class Base extends Entity {
		private static final DataParameter<Integer> USER_ID = EntityDataManager.<Integer>createKey(Base.class, DataSerializers.VARINT);
		protected static final UUID DAMAGE_MODIFIER = UUID.fromString("ef834eb3-67d2-48cf-8d75-1530ee1ed81f");
		private EntityLivingBase user;
		protected double damageModifier = 6d;
		private ItemStack lastHeldWeapon;

		public Base(World world) {
			super(world);
			this.setSize(0.1f, 0.1f);
		}

		public Base(EntityLivingBase user) {
			this(user.world);
			this.setPosition(user.posX, user.posY, user.posZ);
			this.setUser(user);
			this.setAlwaysRenderNameTag(false);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(USER_ID, Integer.valueOf(-1));
		}

		public EntityLivingBase getUser() {
			if (this.user != null) {
				return this.user;
			}
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(USER_ID)).intValue());
			return (entity instanceof EntityLivingBase) ? (EntityLivingBase)entity : null;
		}

		protected void setUser(EntityLivingBase userIn) {
			this.getDataManager().set(USER_ID, Integer.valueOf(userIn.getEntityId()));
			this.user = userIn;
		}

		public boolean isUserHoldingWeapon() {
			return this.getUser() != null && isHoldingWeapon(this.getUser());
		}

		protected void addEffects() {
			EntityLivingBase user = this.getUser();
			if (user != null && this.ticksExisted % 10 == 0) {
				IAttributeInstance aInstance = user.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
				if (aInstance != null) {
					AttributeModifier attributemodifier = aInstance.getModifier(DAMAGE_MODIFIER);
					if (attributemodifier == null || attributemodifier.getAmount() != this.damageModifier) {
						if (attributemodifier != null) {
							aInstance.removeModifier(attributemodifier);
						}
						aInstance.applyModifier(new AttributeModifier(DAMAGE_MODIFIER, "chakraflow.damage", this.damageModifier, 0));
					}
				}
			}
		}

		protected void removeEffects() {
			EntityLivingBase user = this.getUser();
			if (user != null) {
				IAttributeInstance aInstance = user.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
				if (aInstance != null) {
					aInstance.removeModifier(DAMAGE_MODIFIER);
				}
			}
		}

		protected void addEnchantment(ItemStack stack) {
			Message.send(this.user, true, stack);
		}

		protected void removeEnchantment(ItemStack stack) {
			if (this.user instanceof EntityPlayer) {
				ItemStack stack1 = ProcedureUtils.getMatchingItemStack((EntityPlayer)this.user, stack);
				if (stack1 != null) {
					stack = stack1;
				}
			}
			stack = stack.copy();
			ProcedureUtils.addFakeEnchantmentEffect(stack);
			Message.send(this.user, false, stack);
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				if (this.isUserHoldingWeapon()) {
					this.addEffects();
					ItemStack stack = this.user.getHeldItemMainhand();
					if (this.lastHeldWeapon == null || !ItemStack.areItemStacksEqual(stack, this.lastHeldWeapon)) {
						this.addEnchantment(stack);
						if (this.lastHeldWeapon != null) {
							this.removeEnchantment(this.lastHeldWeapon);
						}
						this.lastHeldWeapon = stack.copy();
					}
				} else {
					this.removeEffects();
					if (this.lastHeldWeapon != null) {
						this.removeEnchantment(this.lastHeldWeapon);
						this.lastHeldWeapon = null;
					}
				}
			}
			if (!this.world.isRemote && (this.user == null || !this.user.isEntityAlive())) {
				this.setDead();
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			this.removeEffects();
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Message implements IMessage {
			int id;
			boolean add;
			ItemStack stack;
	
			public Message() {}
	
			public Message(EntityLivingBase entity, boolean b, ItemStack stackIn) {
				this.id = entity.getEntityId();
				this.add = b;
				this.stack = stackIn;
			}
	
			public static void send(EntityLivingBase entity, boolean isAdding, ItemStack stackIn) {
				NarutomodMod.PACKET_HANDLER.sendToAllTracking(new Message(entity, isAdding, stackIn), entity);
				if (entity instanceof EntityPlayerMP) {
					NarutomodMod.PACKET_HANDLER.sendTo(new Message(entity, isAdding, stackIn), (EntityPlayerMP)entity);
				}
			}
			
			public static class Handler implements IMessageHandler<Message, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(Message message, MessageContext context) {
					Minecraft.getMinecraft().addScheduledTask(() -> {
						Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.id);
						if (entity instanceof EntityLivingBase) {
							ItemStack stack = message.stack;
							if (entity instanceof EntityPlayer) {
								ItemStack stack1 = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, message.stack);
								if (stack1 != null) {
									stack = stack1;
								}
							}
							if (message.add) {
								ProcedureUtils.addFakeEnchantmentEffect(stack);
							} else {
								ProcedureUtils.removeFakeEnchantmentEffect(stack);
							}
						}
					});
					return null;
				}
			}
	
			public void toBytes(ByteBuf buf) {
				PacketBuffer pbuf = new PacketBuffer(buf);
				pbuf.writeInt(this.id);
				pbuf.writeBoolean(this.add);
				pbuf.writeItemStack(this.stack);
			}
	
			public void fromBytes(ByteBuf buf) {
				PacketBuffer pbuf = new PacketBuffer(buf);
				this.id = pbuf.readInt();
				this.add = pbuf.readBoolean();
				try {
					this.stack = pbuf.readItemStack();
				} catch (Exception e) {
					new IOException("EntityChakraFlow@Message packet: ", e);
				}
			}
		}
	}

	public static boolean isHoldingWeapon(EntityLivingBase entity) {
		return ProcedureUtils.isWeapon(entity.getHeldItemMainhand());
	}

	@SideOnly(Side.CLIENT)
	public static abstract class RenderCustom<T extends Base> extends Render<T> {
		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}

		@Override
		public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
			return true;
		}

		private Vec3d transform3rdPerson(Vec3d startvec, Vec3d angles, EntityLivingBase entity, float pt) {
			return new ProcedureUtils.RotationMatrix().rotateZ((float)-angles.z).rotateY((float)-angles.y).rotateX((float)-angles.x)
			 .transform(startvec).addVector(0.0625F * -5, 1.5F-(entity.isSneaking()?0.3f:0f), -0.05F)
			 .rotateYaw((-entity.prevRenderYawOffset - (entity.renderYawOffset - entity.prevRenderYawOffset) * pt) * (float)(Math.PI / 180d))
			 .addVector(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt);
		}

		protected abstract void spawnParticles(EntityLivingBase user, Vec3d startvec, Vec3d endvec, float partialTicks);

		@Override
		public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
			EntityLivingBase user = entity.getUser();
			if (user != null && entity.isUserHoldingWeapon() && (this.renderManager.options.thirdPersonView != 0 || user != this.renderManager.renderViewEntity)) {
				Render renderer = this.renderManager.getEntityRenderObject(user);
				if (renderer instanceof RenderLivingBase && ((RenderLivingBase)renderer).getMainModel() instanceof ModelBiped) {
					ModelRenderer armModel = ((ModelBiped)((RenderLivingBase)renderer).getMainModel()).bipedRightArm;
					ItemStack stack = user.getHeldItemMainhand();
					Vec3d startVec = stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomChakraFlowStartVec", 10)
					 ? new Vec3d(stack.getTagCompound().getCompoundTag("CustomChakraFlowStartVec").getDouble("x"),
					             stack.getTagCompound().getCompoundTag("CustomChakraFlowStartVec").getDouble("y"),
					             stack.getTagCompound().getCompoundTag("CustomChakraFlowStartVec").getDouble("z"))
					 : new Vec3d(0d, -0.725d, 0.1d);
					Vec3d endVec = stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomChakraFlowEndVec", 10)
					 ? new Vec3d(stack.getTagCompound().getCompoundTag("CustomChakraFlowEndVec").getDouble("x"),
					             stack.getTagCompound().getCompoundTag("CustomChakraFlowEndVec").getDouble("y"),
					             stack.getTagCompound().getCompoundTag("CustomChakraFlowEndVec").getDouble("z"))
					 : new Vec3d(0d, -0.85d, 0.9d);
					Vec3d armAngles = new Vec3d(armModel.rotateAngleX, armModel.rotateAngleY, armModel.rotateAngleZ);
					Vec3d vec0 = this.transform3rdPerson(startVec, armAngles, user, partialTicks);
					Vec3d vec1 = this.transform3rdPerson(endVec, armAngles, user, partialTicks);
					this.spawnParticles(user, vec0, vec1, partialTicks);
				}
			}
		}

		@Override
		protected ResourceLocation getEntityTexture(T entity) {
			return null;
		}
	}
}
