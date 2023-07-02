
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;

import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityAltCamView extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 60;
	public static final int ENTITYID_RANGED = 61;

	public EntityAltCamView(ElementsNarutomodMod instance) {
		super(instance, 268);
	}

	public void initElements() {
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "altcamviewentity"), ENTITYID).name("altcamviewentity").tracker(64, 1, true).build());
	}

	public static class EntityCustom extends Entity {
		private EntityPlayer mainViewer;

		public EntityCustom(World world) {
			super(world);
			this.setSize(0.1F, 0.1F);
			this.setNoGravity(true);
			this.setEntityInvulnerable(true);
			this.isImmuneToFire = true;
			this.noClip = true;
		}

		public EntityCustom(EntityPlayer player) {
			this(player.world);
			this.copyLocationAndAnglesFrom(player);
			this.mainViewer = player;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return false;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void onUpdate() {
			if (this.mainViewer instanceof EntityPlayerSP) {
				EntityPlayerSP player = (EntityPlayerSP)this.mainViewer;
	            player.moveStrafing = player.movementInput.moveStrafe;
	            player.moveForward = player.movementInput.moveForward;
	            player.setJumping(player.movementInput.jump);
	            player.prevRenderArmYaw = player.renderArmYaw;
	            player.prevRenderArmPitch = player.renderArmPitch;
	            player.renderArmPitch = (float)((double)player.renderArmPitch + (double)(player.rotationPitch - player.renderArmPitch) * 0.5D);
	            player.renderArmYaw = (float)((double)player.renderArmYaw + (double)(player.rotationYaw - player.renderArmYaw) * 0.5D);

	            double minY = player.getEntityBoundingBox().minY;
	            double d0 = player.posX - (double)ReflectionHelper.getPrivateValue(EntityPlayerSP.class, player, 4);//player.lastReportedPosX;
	            double d1 = minY - (double)ReflectionHelper.getPrivateValue(EntityPlayerSP.class, player, 5);//player.lastReportedPosY;
	            double d2 = player.posZ - (double)ReflectionHelper.getPrivateValue(EntityPlayerSP.class, player, 6);//player.lastReportedPosZ;
	            double d3 = (double)(player.rotationYaw - (float)ReflectionHelper.getPrivateValue(EntityPlayerSP.class, player, 7));//player.lastReportedYaw);
	            double d4 = (double)(player.rotationPitch - (float)ReflectionHelper.getPrivateValue(EntityPlayerSP.class, player, 8));//player.lastReportedPitch);
	            int positionUpdateTicks = (int)ReflectionHelper.getPrivateValue(EntityPlayerSP.class, player, 12) + 1;
	            ReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, positionUpdateTicks, 12);//player.positionUpdateTicks;
	            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || positionUpdateTicks >= 20;
	            boolean flag3 = d3 != 0.0D || d4 != 0.0D;
	            if (player.isRiding()) {
	                player.connection.sendPacket(new CPacketPlayer.PositionRotation(player.motionX, -999.0D, player.motionZ, player.rotationYaw, player.rotationPitch, player.onGround));
	                flag2 = false;
	            } else if (flag2 && flag3) {
	                player.connection.sendPacket(new CPacketPlayer.PositionRotation(player.posX, minY, player.posZ, player.rotationYaw, player.rotationPitch, player.onGround));
	            } else if (flag2) {
	                player.connection.sendPacket(new CPacketPlayer.Position(player.posX, minY, player.posZ, player.onGround));
	            } else if (flag3) {
	                player.connection.sendPacket(new CPacketPlayer.Rotation(player.rotationYaw, player.rotationPitch, player.onGround));
	            } else if ((boolean)ReflectionHelper.getPrivateValue(EntityPlayerSP.class, player, 9) != player.onGround) {
	                player.connection.sendPacket(new CPacketPlayer(player.onGround));
	            }
	            if (flag2) {
	                //player.lastReportedPosX = player.posX;
	                ReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, player.posX, 4);
	                //player.lastReportedPosY = axisalignedbb.minY;
	                ReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, minY, 5);
	                //player.lastReportedPosZ = player.posZ;
	                ReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, player.posZ, 6);
	                //player.positionUpdateTicks = 0;
	                ReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, 0, 12);
	            }
	            if (flag3) {
	                //player.lastReportedYaw = player.rotationYaw;
	                ReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, player.rotationYaw, 7);
	                //player.lastReportedPitch = player.rotationPitch;
	                ReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, player.rotationPitch, 8);
	            }
	            //player.prevOnGround = player.onGround;
	            ReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, player.onGround, 9);
	            //player.autoJumpEnabled = player.mc.gameSettings.autoJump;
	            ReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, Minecraft.getMinecraft().gameSettings.autoJump, 30);
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderAltCamView(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderAltCamView extends Render<EntityCustom> {
			public RenderAltCamView(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return null;
			}
		}
	}
}
