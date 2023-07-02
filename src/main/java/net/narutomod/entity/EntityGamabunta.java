
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;

import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityGamabunta extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 356;
	public static final int ENTITYID_RANGED = 357;
	private static final float MODEL_SCALE = 16.0f;

	public EntityGamabunta(ElementsNarutomodMod instance) {
		super(instance, 714);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "gamabunta"), ENTITYID).name("gamabunta")
		 .tracker(128, 3, true).egg(-6266019, -10996173).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new EntityToad.RenderCustom<EntityCustom>(renderManager) {
				private final ResourceLocation texture = new ResourceLocation("narutomod:textures/gamabunta.png");
				@Override
				protected ResourceLocation getEntityTexture(EntityCustom entity) {
					return texture;
				}
			};
		});
	}

	public static class EntityCustom extends EntityToad.EntityCustom {
		public EntityCustom(World world) {
			super(world);
			this.postScaleFixup();
		}

		public EntityCustom(EntityLivingBase summonerIn) {
			super(summonerIn);
			this.postScaleFixup();
			//this.summoner = summonerIn;
			//this.dontWander(true);
		}

		@Override
		public float getScale() {
			return MODEL_SCALE;
		}

		/*@Override
		protected void updateAITasks() {
			super.updateAITasks();
			EntityLivingBase owner = this.getOwner();
			if (owner != null) {
				EntityLivingBase target = owner.getRevengeTarget();
				if (target != null) {
					this.setAttackTarget(target);
				}
				target = this.getAttackTarget();
				if (target != null && !target.isEntityAlive()) {
					this.setAttackTarget(null);
				}
			}
		}*/

		@Override
		public void onUpdate() {
			super.onUpdate();
			EntityLivingBase summoner = this.getSummoner();
			if (summoner != null && !summoner.isRiding() && this.getAge() == 1) {
				summoner.startRiding(this);
			}
		}
	}
}
