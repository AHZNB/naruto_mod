
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;

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

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderGamabunta(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderGamabunta extends EntityToad.RenderCustom<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/gamabunta.png");

			public RenderGamabunta(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelGamabunta());
			}
			
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelGamabunta extends EntityToad.ModelToad {
			private final ModelRenderer pipe;

			public ModelGamabunta() {
				super();
				browLeft.cubeList.add(new ModelBox(browLeft, 51, 35, -1.71F, -0.5F, 0.25F, 4, 1, 1, 0.31F, true));
				pipe = new ModelRenderer(this);
				pipe.setRotationPoint(4.1917F, -0.5133F, -4.8393F);
				head.addChild(pipe);
				setRotationAngle(pipe, 0.2618F, -0.8727F, 0.0F);
				pipe.cubeList.add(new ModelBox(pipe, 0, 4, -1.8662F, -2.0667F, -6.0098F, 2, 1, 2, 0.0F, false));
				pipe.cubeList.add(new ModelBox(pipe, 0, 0, -1.8662F, -0.8167F, -6.0098F, 2, 2, 2, 0.0F, false));
				pipe.cubeList.add(new ModelBox(pipe, 0, 7, -1.3662F, -1.3167F, -5.5098F, 1, 1, 1, 0.0F, false));
				pipe.cubeList.add(new ModelBox(pipe, 52, 52, -1.3662F, -0.3167F, -4.0098F, 1, 1, 5, 0.0F, false));
			}
		}
	}
}
