
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;

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

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderAltCamView(renderManager));
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

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
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
