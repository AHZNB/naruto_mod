
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.client.renderer.entity.RenderBat;

import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityCrow extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 119;
	public static final int ENTITYID_RANGED = 120;

	public EntityCrow(ElementsNarutomodMod instance) {
		super(instance, 339);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "crow"), ENTITYID)
				.name("crow").tracker(64, 3, true).egg(-16777216, -13421773).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new RenderBat(renderManager) {
				protected ResourceLocation getEntityTexture(EntityBat entity) {
					return new ResourceLocation("narutomod:textures/crow.png");
				}
			};
		});
	}

	public static class EntityCustom extends EntityBat {
		private int lifeSpan;
		private boolean isReal;

		public EntityCustom(World world) {
			super(world);
			this.isReal = false;
			this.lifeSpan = 200 + this.rand.nextInt(100);
			this.isImmuneToFire = true;
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:crow_call"));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		public boolean canBeCollidedWith() {
			return this.isReal;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			return this.isReal && super.attackEntityFrom(source, amount);
		}

		@Override
		public void onUpdate() {
			this.noClip = true;
			super.onUpdate();
			this.noClip = false;
			if (--this.lifeSpan <= 0)
				this.setDead();
		}
	}
}
