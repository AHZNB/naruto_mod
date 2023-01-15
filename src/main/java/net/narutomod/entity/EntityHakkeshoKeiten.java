package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Chakra;

import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;

@ElementsNarutomodMod.ModElement.Tag
public class EntityHakkeshoKeiten extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 58;
	public static final int ENTITYID_RANGED = 59;
	
	public EntityHakkeshoKeiten(ElementsNarutomodMod instance) {
		super(instance, 262);
	}

	public void initElements() {
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "hakkeshokeitenentity"), ENTITYID).name("hakkeshokeitenentity").tracker(64, 1, true)
				.build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends RenderLivingBase<EntityCustom> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/electric_armor.png");

		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn, new ModelKaiten(), 2.0F);
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return new ResourceLocation("narutomod:textures/electric_armor.png");
		}
	}

	public static class EntityCustom extends EntityShieldBase {
		private static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntityCustom.class, DataSerializers.FLOAT);
		private final int matureTime = 10;

		public EntityCustom(World world) {
			super(world);
			this.setSize(3.0F, 3.0F);
			this.setOwnerCanSteer(true, 0.5F);
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			float scale = MathHelper.clamp((float)player.experienceLevel / 30f, 1f, 10f);
			this.setScale(scale);
			this.setSize(3.0F * scale, 3.0F * scale);
			this.setOwnerCanSteer(true, 0.5F);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
			  .applyModifier(new AttributeModifier("shield.health", player.getMaxHealth() + player.experienceLevel, 0));
			this.setHealth(this.getMaxHealth());
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(SCALE, Float.valueOf(1f));
		}

		public float getScale() {
			return ((Float) this.getDataManager().get(SCALE)).floatValue();
		}

		protected void setScale(float s) {
			this.getDataManager().set(SCALE, Float.valueOf(s));
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getTrueSource() instanceof EntityLivingBase 
			 && !source.getTrueSource().equals(this.getSummoner()) && !this.equals(source.getTrueSource())) {
				((EntityLivingBase) source.getTrueSource()).attackEntityFrom(source, amount);
			}
			return false;
		}

		@Override
		public void onDeathUpdate() {
			this.setDead();
		}

		private float getMaturity() {
			return Math.min((float)this.ticksExisted / (float)this.matureTime, 1.0F);
		}

		@Override
		public void onLivingUpdate() {
			EntityLivingBase summoner = this.getSummoner();
			if (summoner != null) {
				summoner.setSneaking(false);
			}
			if (this.world.isRemote) {
				this.setSize(3.0F * this.getScale(), 3.0F * this.getScale());
			}
			super.onLivingUpdate();
			if (!this.world.isRemote) {
				if (summoner == null
				 || !Chakra.pathway(summoner).consume(ItemByakugan.getKaitenChakraUsage(summoner))) {
				 //|| Chakra.pathway((EntityPlayer)this.getSummoner()).getAmount() < CHAKRA_USAGE) {
					this.setDead();
				} else {
					//Chakra.pathway((EntityPlayer)this.getSummoner()).consume(CHAKRA_USAGE);
					if (this.getMaturity() >= 0.9f) {
						this.breakBlocks(ProcedureUtils.getNonAirBlocks(this.world, 
						 this.getEntityBoundingBox().expand(1.0D, 1.0D, 1.0D).expand(-1.0D, 0.0D, -1.0D)));
						ProcedureUtils.purgeHarmfulEffects(summoner);
					}
				}
			}
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			super.collideWithEntity(entity);
			if (!this.world.isRemote && !this.isRidingSameEntity(entity) && this.getMaturity() >= 0.9f) {
				ProcedureUtils.pushEntity(this, entity, 60.0D, 1.0F);
				EntityLivingBase summoner = this.getSummoner();
				entity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, summoner),
				 summoner instanceof EntityPlayer ? ((EntityPlayer)summoner).experienceLevel / 2.0F + 10F : 10F);
			}
		}

		private void breakBlocks(List<? extends BlockPos> list) {
			EntityLivingBase summoner = this.getSummoner();
			for (BlockPos pos : list) {
				if (this.world.getBlockState(pos).getMaterial().isLiquid()) {
					this.world.setBlockToAir(pos);
				} else if (summoner instanceof EntityPlayer && ((EntityPlayer)summoner).experienceLevel >= 45) {
					ProcedureUtils.breakBlockAndDropWithChance(this.world, pos, 5.0F, 1.0F, 0.1F);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class ModelKaiten extends ModelBase {
		private final Random rand = new Random();
		private final ModelRenderer shell;
		private final ModelRenderer bone5;
		private final ModelRenderer bone4;
		private final ModelRenderer bone3;
		private final ModelRenderer bone2;
	
		public ModelKaiten() {
			textureWidth = 32;
			textureHeight = 32;
	
			shell = new ModelRenderer(this);
			shell.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
			shell.addChild(bone5);
			bone5.cubeList.add(new ModelBox(bone5, 0, 0, -8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F, false));
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
			shell.addChild(bone4);
			setRotationAngle(bone4, 0.0F, 0.0F, 0.7854F);
			bone4.cubeList.add(new ModelBox(bone4, 0, 0, -8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F, false));
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
			shell.addChild(bone3);
			setRotationAngle(bone3, 0.0F, -0.7854F, 0.0F);
			bone3.cubeList.add(new ModelBox(bone3, 0, 0, -8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F, false));
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
			shell.addChild(bone2);
			setRotationAngle(bone2, -0.7854F, 0.0F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 0, 0, -8.0F, -8.0F, -8.0F, 16, 16, 16, 0.0F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			GlStateManager.pushMatrix();
			GlStateManager.color(1f, 1f, 1f, 0.3f);
			float scale = ((EntityCustom)entity).getMaturity() * ((EntityCustom)entity).getScale() * 2.5F;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(entity.ticksExisted * 30.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			//core.render(f5);
			//GlStateManager.color(0.66F, 0.87F, 1.0F, 0.3F);
			//for (int i = 0; i < Math.min(10, entity.ticksExisted); i++) {
			//	GlStateManager.rotate(rand.nextFloat() * 30f, 0f, 1f, 0f);
			//	GlStateManager.rotate(rand.nextFloat() * 30f, 1f, 1f, 0f);
				shell.render(f5);
			//}
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}

	/*public class ModelKaitenShield extends ModelBase {
		private final ModelRenderer bone;
		public ModelKaitenShield() {
			this.textureWidth = 32;
			this.textureHeight = 32;
			this.bone = new ModelRenderer(this);
			this.bone.setRotationPoint(0.0F, 8.0F, 0.0F);
			this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -8.0F, -16.0F, -8.0F, 16, 16, 16, 16.0F, false));
		}

		public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
			GlStateManager.pushMatrix();
			GlStateManager.depthMask(true);
			float scale = MathHelper.clamp(entity.ticksExisted / 10.0F, 0.0F, 1.0F);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(entity.ticksExisted * 30.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f = entity.ticksExisted;
			GlStateManager.translate(f * 0.02F, f * 0.02F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
			this.bone.render(f5);
			Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(false);
			GlStateManager.popMatrix();
		}
	}*/
}
