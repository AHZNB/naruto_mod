
package net.narutomod.entity;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;

@ElementsNarutomodMod.ModElement.Tag
public class EntityToadShima extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 461;
	public static final int ENTITYID_RANGED = 462;
	private static final float MODEL_SCALE = 0.4f;

	public EntityToadShima(ElementsNarutomodMod instance) {
		super(instance, 893);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "toad_shima"), ENTITYID).name("toad_shima").tracker(64, 3, true).egg(-3881852, -8564327).build());
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
			int age = this.getAge();
			EntityLivingBase summoner = this.getSummoner();
			if (summoner != null && age == 1) {
				this.startRiding(summoner);
			}
			if (age == 3) {
				ProcedureUtils.poofWithSmoke(this);
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderShima(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderShima extends EntityToad.RenderCustom<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/toadshima.png");
			private final ResourceLocation cloak_texture = new ResourceLocation("narutomod:textures/toadcloaks.png");
			private final EntityToadFukasaku.Renderer.ModelToadCloak modelCloak = new EntityToadFukasaku.Renderer.ModelToadCloak();

			public RenderShima(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelToadSkinny());
			}

			@Override
			protected void renderLayers(EntityCustom entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
				float scale = entity.getScale();
				this.bindTexture(this.cloak_texture);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 1.5F - 1.5F * scale, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.disableCull();
				this.modelCloak.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
				GlStateManager.enableCull();
				GlStateManager.popMatrix();
			}
			
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 4.10.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelToadSkinny extends EntityToad.ModelToad {
			private final ModelRenderer hairShima;
			private final ModelRenderer bone12;
			private final ModelRenderer bone9;
			private final ModelRenderer bone15;
			private final ModelRenderer bone16;

			public ModelToadSkinny() {
				textureWidth = 64;
				textureHeight = 64;
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 9.58F, -2.464F);
				head.cubeList.add(new ModelBox(head, 2, 21, -4.5F, -4.58F, -6.036F, 9, 5, 6, 0.0F, false));
				hairShima = new ModelRenderer(this);
				hairShima.setRotationPoint(0.0F, -5.4781F, -1.7283F);
				head.addChild(hairShima);
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.05F, 0.6481F, -5.0577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -1.95F, 0.6481F, -5.0577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -3.95F, 0.6481F, -4.8077F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.05F, 0.8981F, -4.8077F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.8F, 0.8981F, -4.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 3.05F, 1.1481F, -2.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 3.05F, 1.3981F, -0.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.8F, 1.6481F, 1.4423F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.8F, 1.3981F, 1.6923F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -0.95F, 1.6481F, 1.9423F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -2.95F, 1.3981F, 1.6923F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.7F, 1.6481F, 1.4423F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.95F, 1.3981F, -0.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.95F, 1.1481F, -2.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.7F, 0.8981F, -4.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -3.95F, -0.1019F, -4.3077F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.2F, -0.6019F, -2.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -2.45F, -1.1019F, -2.8077F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.55F, -1.1019F, -2.8077F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.8F, -1.1019F, -0.8077F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.05F, -0.3519F, 1.1923F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -2.7F, -1.1019F, -0.8077F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -1.95F, -0.3519F, 1.1923F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -0.95F, -1.1019F, -3.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -0.95F, -1.6019F, -2.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -0.95F, -1.6019F, -0.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.05F, -1.1019F, 0.6923F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -1.95F, -1.1019F, 0.6923F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -4.2F, -0.6019F, -0.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -1.95F, -0.1019F, -4.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 0.05F, -0.1019F, -4.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.05F, -0.1019F, -4.3077F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.3F, -0.6019F, -2.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.3F, -0.6019F, -0.5577F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, 2.05F, 0.1481F, 0.9423F, 2, 2, 2, 0.1F, false));
				hairShima.cubeList.add(new ModelBox(hairShima, 0, 19, -3.95F, 0.1481F, 0.9423F, 2, 2, 2, 0.1F, false));
				neck = new ModelRenderer(this);
				neck.setRotationPoint(0.0F, -4.58F, -0.1279F);
				head.addChild(neck);
				setRotationAngle(neck, -0.9163F, 0.0F, 0.0F);
				neck.cubeList.add(new ModelBox(neck, 27, 0, -4.5F, 0.0F, -0.0081F, 9, 4, 5, -0.03F, false));
				browRight = new ModelRenderer(this);
				browRight.showModel = false;
				browLeft = new ModelRenderer(this);
				browLeft.showModel = false;
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.04F, 0.4997F, -1.1917F);
				head.addChild(jaw);
				setRotationAngle(jaw, 0.0436F, 0.0F, 0.0F);
				jaw.cubeList.add(new ModelBox(jaw, 28, 26, -4.54F, -0.0901F, -4.8784F, 9, 2, 6, 0.0F, false));
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 9.58F, -2.464F);
				setRotationAngle(body, -0.2618F, 0.0F, 0.0F);
				chest = new ModelRenderer(this);
				chest.setRotationPoint(0.25F, 3.7695F, -0.9502F);
				body.addChild(chest);
				setRotationAngle(chest, -0.7854F, 0.0F, 0.0F);
				ModelRenderer bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(-0.2F, -7.5397F, 2.1327F);
				chest.addChild(bone6);
				setRotationAngle(bone6, -0.2182F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -4.55F, 0.1397F, -0.0173F, 9, 9, 9, 0.0F, false));
				ModelRenderer bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(-0.2F, -1.5397F, 3.8827F);
				chest.addChild(bone11);
				ModelRenderer chest_r1 = new ModelRenderer(this);
				chest_r1.setRotationPoint(0.0F, -5.9657F, -1.8845F);
				bone11.addChild(chest_r1);
				setRotationAngle(chest_r1, 0.2443F, 0.0F, 0.0F);
				chest_r1.cubeList.add(new ModelBox(chest_r1, 26, 34, -4.55F, 0.1154F, -4.8982F, 9, 6, 5, -0.01F, false));
				ModelRenderer bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 1.1343F, -5.1845F);
				bone11.addChild(bone5);
				setRotationAngle(bone5, 0.4363F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 54, -4.5F, -0.7806F, 0.5558F, 9, 3, 3, 0.0F, false));
				bunda = new ModelRenderer(this);
				bunda.setRotationPoint(-0.242F, -5.4646F, 10.7942F);
				chest.addChild(bunda);
				setRotationAngle(bunda, -0.6545F, 0.0F, 0.0F);
				bunda.cubeList.add(new ModelBox(bunda, 0, 32, -4.508F, 0.15F, -0.1F, 9, 8, 4, -0.1F, false));
				armRight = new ModelRenderer(this);
				armRight.setRotationPoint(-5.05F, 3.7695F, 1.1398F);
				body.addChild(armRight);
				setRotationAngle(armRight, -0.2182F, 0.0F, 0.2618F);
				armRight.cubeList.add(new ModelBox(armRight, 56, 0, -1.076F, -1.7778F, -0.9648F, 2, 8, 2, 0.2F, false));
				forearmRight = new ModelRenderer(this);
				forearmRight.setRotationPoint(-1.11F, 6.298F, -0.1F);
				armRight.addChild(forearmRight);
				forearmRight.cubeList.add(new ModelBox(forearmRight, 0, 0, 0.038F, 0.0862F, -0.8328F, 2, 6, 2, 0.1F, false));
				handRight = new ModelRenderer(this);
				handRight.setRotationPoint(1.26F, 6.478F, 0.152F);
				forearmRight.addChild(handRight);
				setRotationAngle(handRight, 0.5236F, 0.2618F, 0.0F);
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(-0.8537F, -0.2173F, 0.496F);
				handRight.addChild(bone12);
				setRotationAngle(bone12, 0.0F, 0.2618F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, false));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(-0.1037F, -0.2173F, 0.496F);
				handRight.addChild(bone9);
				bone9.cubeList.add(new ModelBox(bone9, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, false));
				ModelRenderer bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.6463F, -0.2173F, 0.496F);
				handRight.addChild(bone13);
				setRotationAngle(bone13, 0.0F, -0.2618F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, false));
				armLeft = new ModelRenderer(this);
				armLeft.setRotationPoint(5.05F, 3.7695F, 1.1398F);
				body.addChild(armLeft);
				setRotationAngle(armLeft, -0.2182F, 0.0F, -0.2618F);
				armLeft.cubeList.add(new ModelBox(armLeft, 56, 0, -0.924F, -1.7778F, -0.9648F, 2, 8, 2, 0.2F, true));
				forearmLeft = new ModelRenderer(this);
				forearmLeft.setRotationPoint(1.11F, 6.298F, -0.1F);
				armLeft.addChild(forearmLeft);
				forearmLeft.cubeList.add(new ModelBox(forearmLeft, 0, 0, -2.038F, 0.0862F, -0.8328F, 2, 6, 2, 0.1F, true));
				handLeft = new ModelRenderer(this);
				handLeft.setRotationPoint(-1.26F, 6.478F, 0.152F);
				forearmLeft.addChild(handLeft);
				setRotationAngle(handLeft, 0.5236F, -0.2618F, 0.0F);
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.8537F, -0.2173F, 0.496F);
				handLeft.addChild(bone15);
				setRotationAngle(bone15, 0.0F, -0.2618F, 0.0F);
				bone15.cubeList.add(new ModelBox(bone15, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, true));
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.1037F, -0.2173F, 0.496F);
				handLeft.addChild(bone16);
				bone16.cubeList.add(new ModelBox(bone16, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, true));
				ModelRenderer bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(-0.6463F, -0.2173F, 0.496F);
				handLeft.addChild(bone17);
				setRotationAngle(bone17, 0.0F, 0.2618F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 39, 45, -1.0F, -1.0F, -3.75F, 2, 2, 4, -0.4F, true));
				legRight = new ModelRenderer(this);
				legRight.setRotationPoint(-4.677F, 19.8471F, 0.9223F);
				setRotationAngle(legRight, 0.2618F, 0.5236F, 0.0F);
				thighRight = new ModelRenderer(this);
				thighRight.setRotationPoint(0.241F, 1.0282F, 0.8872F);
				legRight.addChild(thighRight);
				setRotationAngle(thighRight, -0.6981F, 0.0F, 0.0F);
				thighRight.cubeList.add(new ModelBox(thighRight, 26, 9, -1.901F, -1.6142F, -9.4876F, 3, 3, 10, 0.2F, false));
				legLowerRight = new ModelRenderer(this);
				legLowerRight.setRotationPoint(-0.0653F, -4.0517F, -5.8381F);
				legRight.addChild(legLowerRight);
				setRotationAngle(legLowerRight, -0.5236F, 0.0F, 0.0F);
				ModelRenderer legLowerRight3_r1 = new ModelRenderer(this);
				legLowerRight3_r1.setRotationPoint(-0.1735F, 1.045F, -0.854F);
				legLowerRight.addChild(legLowerRight3_r1);
				setRotationAngle(legLowerRight3_r1, -0.7418F, 0.0F, 0.0F);
				legLowerRight3_r1.cubeList.add(new ModelBox(legLowerRight3_r1, 0, 45, -0.8772F, -2.0266F, -0.0999F, 2, 2, 7, 0.2F, false));
				footRight = new ModelRenderer(this);
				footRight.setRotationPoint(-0.0107F, 5.1235F, 4.6603F);
				legLowerRight.addChild(footRight);
				setRotationAngle(footRight, 0.2182F, 0.0F, 0.0F);
				ModelRenderer bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-0.396F, -0.0341F, -0.0512F);
				footRight.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.1745F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));
				ModelRenderer bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, -0.25F, -4.0F);
				bone2.addChild(bone7);
				setRotationAngle(bone7, 0.2618F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, false));
				ModelRenderer bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.104F, -0.0341F, -0.0512F);
				footRight.addChild(bone3);
				bone3.cubeList.add(new ModelBox(bone3, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));
				ModelRenderer bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, -0.25F, -4.0F);
				bone3.addChild(bone14);
				setRotationAngle(bone14, 0.2618F, 0.0F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, false));
				ModelRenderer bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.604F, -0.0341F, -0.0512F);
				footRight.addChild(bone8);
				setRotationAngle(bone8, 0.0F, -0.1745F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));
				ModelRenderer bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0F, -0.25F, -4.0F);
				bone8.addChild(bone10);
				setRotationAngle(bone10, 0.2618F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, false));
				legLeft = new ModelRenderer(this);
				legLeft.setRotationPoint(4.677F, 19.8471F, 0.9223F);
				setRotationAngle(legLeft, 0.2618F, -0.5236F, 0.0F);
				thighLeft = new ModelRenderer(this);
				thighLeft.setRotationPoint(-0.241F, 1.0282F, 0.8872F);
				legLeft.addChild(thighLeft);
				setRotationAngle(thighLeft, -0.6981F, 0.0F, 0.0F);
				thighLeft.cubeList.add(new ModelBox(thighLeft, 26, 9, -1.099F, -1.6142F, -9.4876F, 3, 3, 10, 0.2F, true));
				legLowerLeft = new ModelRenderer(this);
				legLowerLeft.setRotationPoint(0.0653F, -4.0517F, -5.8381F);
				legLeft.addChild(legLowerLeft);
				setRotationAngle(legLowerLeft, -0.5236F, 0.0F, 0.0F);
				ModelRenderer legLowerLeft3_r1 = new ModelRenderer(this);
				legLowerLeft3_r1.setRotationPoint(0.1735F, 1.045F, -0.854F);
				legLowerLeft.addChild(legLowerLeft3_r1);
				setRotationAngle(legLowerLeft3_r1, -0.7418F, 0.0F, 0.0F);
				legLowerLeft3_r1.cubeList.add(new ModelBox(legLowerLeft3_r1, 0, 45, -1.1228F, -2.0266F, -0.0999F, 2, 2, 7, 0.2F, true));
				footLeft = new ModelRenderer(this);
				footLeft.setRotationPoint(0.0107F, 5.1235F, 4.6603F);
				legLowerLeft.addChild(footLeft);
				setRotationAngle(footLeft, 0.2182F, 0.0F, 0.0F);
				ModelRenderer bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.396F, -0.0341F, -0.0512F);
				footLeft.addChild(bone19);
				setRotationAngle(bone19, 0.0F, -0.1745F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
				ModelRenderer bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(0.0F, -0.25F, -4.0F);
				bone19.addChild(bone20);
				setRotationAngle(bone20, 0.2618F, 0.0F, 0.0F);
				bone20.cubeList.add(new ModelBox(bone20, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, true));
				ModelRenderer bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(-0.104F, -0.0341F, -0.0512F);
				footLeft.addChild(bone22);
				bone22.cubeList.add(new ModelBox(bone22, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
				ModelRenderer bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, -0.25F, -4.0F);
				bone22.addChild(bone23);
				setRotationAngle(bone23, 0.2618F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, true));
				ModelRenderer bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(-0.604F, -0.0341F, -0.0512F);
				footLeft.addChild(bone24);
				setRotationAngle(bone24, 0.0F, 0.1745F, 0.0F);
				bone24.cubeList.add(new ModelBox(bone24, 11, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
				ModelRenderer bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0F, -0.25F, -4.0F);
				bone24.addChild(bone25);
				setRotationAngle(bone25, 0.2618F, 0.0F, 0.0F);
				bone25.cubeList.add(new ModelBox(bone25, 25, 45, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.4F, true));
			}

			@Override
			public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
				boolean mouthOpen = ((EntityCustom)e).getMouthOpen();
				if (mouthOpen) {
					setRotationAngle(armRight, -1.0472F, 0.0F, 0.2618F);
					setRotationAngle(forearmRight, 0.0F, 0.0F, -1.3963F);
					setRotationAngle(handRight, 0.0F, 0.2618F, -0.3491F);
					setRotationAngle(bone12, 0.5236F, 0.2618F, 0.0F);
					setRotationAngle(bone9, 0.5236F, 0.0F, 0.0F);
					setRotationAngle(armLeft, -1.0472F, 0.0F, -0.2618F);
					setRotationAngle(forearmLeft, 0.0F, 0.0F, 1.3963F);
					setRotationAngle(handLeft, 0.0F, -0.2618F, 0.3491F);
					setRotationAngle(bone15, 0.5236F, -0.2618F, 0.0F);
					setRotationAngle(bone16, 0.5236F, 0.0F, 0.0F);
				} else {
					setRotationAngle(armRight, -0.2182F, 0.0F, 0.2618F);
					setRotationAngle(forearmRight, 0.0F, 0.0F, -0.5236F);
					setRotationAngle(handRight, 0.5236F, 0.2618F, 0.0F);
					setRotationAngle(bone12, 0.0F, 0.2618F, 0.0F);
					setRotationAngle(bone9, 0.0F, 0.0F, 0.0F);
					setRotationAngle(armLeft, -0.2182F, 0.0F, -0.2618F);
					setRotationAngle(forearmLeft, 0.0F, 0.0F, 0.5236F);
					setRotationAngle(handLeft, 0.5236F, -0.2618F, 0.0F);
					setRotationAngle(bone15, 0.0F, -0.2618F, 0.0F);
					setRotationAngle(bone16, 0.0F, 0.0F, 0.0F);
				}
				if (this.swingProgress > 0.0F || mouthOpen) {
					this.armRight.showModel = true;
					this.armLeft.showModel = true;
				} else {
					this.armRight.showModel = false;
					this.armLeft.showModel = false;
				}
				if (e.onGround || this.jumpProgress == 0.0F) {
					this.legRight.showModel = false;
					this.legLeft.showModel = false;
				} else {
					this.legRight.showModel = true;
					this.legLeft.showModel = true;
				}
			}
		}
	}
}
