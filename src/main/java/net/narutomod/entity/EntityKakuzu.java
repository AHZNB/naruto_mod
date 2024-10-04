
package net.narutomod.entity;

import net.narutomod.item.ItemAkatsukiRobe;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityKakuzu extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 469;
	public static final int ENTITYID_RANGED = 470;

	public EntityKakuzu(ElementsNarutomodMod instance) {
		super(instance, 899);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "kakuzu"), ENTITYID)
				.name("kakuzu").tracker(64, 3, true).egg(-16777216, -6750157).build());
	}

	public static class EntityCustom extends EntityNinjaMob.Base implements IMob {
		public EntityCustom(World world) {
			super(world, 120, 7000d);
			this.setSize(0.6f, 2.0f);
		}

		@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemAkatsukiRobe.body));
			return super.onInitialSpawn(difficulty, livingdata);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(100D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAISwimming(this));
		}

		@Override
		public boolean getCanSpawnHere() {
			return super.getCanSpawnHere() && (int)this.posY >= this.world.getSeaLevel() && this.world.canSeeSky(this.getPosition())
			 && this.world.getEntities(EntityCustom.class, EntitySelectors.IS_ALIVE).isEmpty()
			 && !EntityNinjaMob.SpawnData.spawnedRecentlyHere(this, 36000);
			 //&& this.rand.nextInt(5) == 0;
		}

		@Override
		public boolean isNonBoss() {
			return false;
		}

		private final BossInfoServer bossInfo = new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

		@Override
		public void removeTrackingPlayer(EntityPlayerMP player) {
			super.removeTrackingPlayer(player);
			if (this.bossInfo.getPlayers().contains(player)) {
				this.bossInfo.removePlayer(player);
			}
		}

		private void trackAttackedPlayers() {
			Entity entity = this.getAttackingEntity();
			if (entity instanceof EntityPlayerMP || (entity = this.getAttackTarget()) instanceof EntityPlayerMP) {
				this.bossInfo.addPlayer((EntityPlayerMP)entity);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.trackAttackedPlayers();
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityNinjaMob.RenderBase<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/kakuzu.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn, new ModelKakuzu());
			}

			@Override
			public void transformHeldFull3DItemLayer() {
				GlStateManager.translate(0.0F, 0.1875F, 0.0F);
			}

			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}

		// Made with Blockbench 4.11.0
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelKakuzu extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer hair;
			private final ModelRenderer jaw;
			private final ModelRenderer bodywear;
			private final ModelRenderer mask1;
			private final ModelRenderer mask2;
			private final ModelRenderer mask3;
			private final ModelRenderer mask4;
			private final ModelRenderer rightForeArm;
			private final ModelRenderer leftForeArm;
			private final ModelRenderer rightLegwear;
			private final ModelRenderer leftLegwear;
			public ModelKakuzu() {
				textureWidth = 64;
				textureHeight = 64;
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.addChild(hair);
				hair.cubeList.add(new ModelBox(hair, 24, -8, -4.0F, 0.0F, -4.0F, 0, 4, 8, 0.0F, false));
				hair.cubeList.add(new ModelBox(hair, 24, -8, 4.0F, 0.0F, -4.0F, 0, 4, 8, 0.0F, true));
				hair.cubeList.add(new ModelBox(hair, 24, 4, -4.0F, 0.0F, 4.0F, 8, 4, 0, 0.0F, false));
				hair.showModel = false;
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, 0.0F, -2.0F);
				bipedHead.addChild(jaw);
				jaw.cubeList.add(new ModelBox(jaw, 52, 16, -1.5F, -1.0F, -2.0F, 3, 1, 2, 0.0F, false));
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F, false));
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bodywear = new ModelRenderer(this);
				bodywear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(bodywear);
				bodywear.cubeList.add(new ModelBox(bodywear, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
				mask1 = new ModelRenderer(this);
				mask1.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(mask1);
				mask1.cubeList.add(new ModelBox(mask1, 0, 57, -1.5F, -1.0F, 0.65F, 7, 7, 0, -1.5F, false));
				mask2 = new ModelRenderer(this);
				mask2.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(mask2);
				mask2.cubeList.add(new ModelBox(mask2, 0, 50, -5.5F, -0.9F, 0.65F, 7, 7, 0, -1.5F, false));
				mask3 = new ModelRenderer(this);
				mask3.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(mask3);
				mask3.cubeList.add(new ModelBox(mask3, 14, 50, -5.6F, 3.6F, 0.65F, 7, 7, 0, -1.5F, false));
				mask4 = new ModelRenderer(this);
				mask4.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.addChild(mask4);
				mask4.cubeList.add(new ModelBox(mask4, 14, 57, -1.6F, 3.5F, 0.65F, 7, 7, 0, -1.5F, false));
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, false));
				rightForeArm = new ModelRenderer(this);
				rightForeArm.setRotationPoint(-1.0F, 4.0F, 0.0F);
				bipedRightArm.addChild(rightForeArm);
				rightForeArm.cubeList.add(new ModelBox(rightForeArm, 40, 26, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, false));
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 6, 4, 0.0F, true));
				leftForeArm = new ModelRenderer(this);
				leftForeArm.setRotationPoint(1.0F, 4.0F, 0.0F);
				bipedLeftArm.addChild(leftForeArm);
				leftForeArm.cubeList.add(new ModelBox(leftForeArm, 40, 26, -2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F, true));
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				rightLegwear = new ModelRenderer(this);
				rightLegwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedRightLeg.addChild(rightLegwear);
				rightLegwear.cubeList.add(new ModelBox(rightLegwear, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, false));
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				leftLegwear = new ModelRenderer(this);
				leftLegwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedLeftLeg.addChild(leftLegwear);
				leftLegwear.cubeList.add(new ModelBox(leftLegwear, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F, true));
			}
		}
	}
}
