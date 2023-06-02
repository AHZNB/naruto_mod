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
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemByakugan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.PlayerTracker;
import net.narutomod.Particles;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;

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

	public static class EntityCustom extends EntityShieldBase {
		private static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntityCustom.class, DataSerializers.FLOAT);
		private final int matureTime = 10;
		private float maxScale;

		public EntityCustom(World world) {
			super(world);
			this.setSize(3.0F, 2.0F);
			this.setOwnerCanSteer(true, 0.5F);
		}

		public EntityCustom(EntityPlayer player) {
			super(player);
			double d = PlayerTracker.getNinjaLevel(player);
			this.maxScale = MathHelper.clamp((float)d / 40f, 1f, 10f);
			this.setScale(0.1f);
			this.setOwnerCanSteer(true, 0.5F);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
			  .applyModifier(new AttributeModifier("shield.health", d + player.getMaxHealth(), 0));
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

		protected void setScale(float scale) {
			this.setSize(3.0F * scale, 2.0F * scale);
			this.getDataManager().set(SCALE, Float.valueOf(scale));
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (SCALE.equals(key) && this.world.isRemote) {
				float scale = this.getScale();
				this.setSize(3.0F * scale, 2.0F * scale);
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			Entity attacker = source.getTrueSource();
			if (attacker instanceof EntityLivingBase && !attacker.equals(this.getSummoner()) && !this.equals(attacker)) {
				attacker.attackEntityFrom(source, amount);
			}
			return super.attackEntityFrom(source, amount);
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
			super.onLivingUpdate();
			if (!this.world.isRemote) {
				if (summoner == null || !Chakra.pathway(summoner).consume(ItemByakugan.getKaitenChakraUsage(summoner))) {
					this.setDead();
				} else {
					float maturity = this.getMaturity();
					float scale = maturity * this.maxScale;
					this.setScale(scale);
					Particles.Renderer particles = new Particles.Renderer(this.world);
					for (int i = 0; i < (int)(scale * maturity * 30f); i++) {
						particles.spawnParticles(Particles.Types.SMOKE, summoner.posX, summoner.posY, summoner.posZ,
						 1, 1d, 0d, 1d, (this.rand.nextDouble()-0.5d) * scale * 2.0d, 0.4d,
						 (this.rand.nextDouble()-0.5d) * scale * 2.0d, 0x10FFFFFF, 30 + (int)(scale * 5), 0);
					}
					particles.send();
					if (maturity >= 0.9f) {
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
				 summoner instanceof EntityPlayer ? (float)PlayerTracker.getNinjaLevel((EntityPlayer)summoner) / 4.0F + 10F : 10F);
			}
		}

		private void breakBlocks(List<? extends BlockPos> list) {
			EntityLivingBase summoner = this.getSummoner();
			for (BlockPos pos : list) {
				if (this.world.getBlockState(pos).getMaterial().isLiquid()) {
					this.world.setBlockToAir(pos);
				} else if (summoner instanceof EntityPlayer && PlayerTracker.getNinjaLevel((EntityPlayer)summoner) >= 70d) {
					ProcedureUtils.breakBlockAndDropWithChance(this.world, pos, 5.0F, 1.0F, 0.1F);
				}
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner instanceof EntityPlayer) {
					double cooldown = ProcedureUtils.getCooldownModifier((EntityPlayer)summoner) * this.ticksExisted * 5;
					ItemStack _stack = summoner.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
					if (_stack.getItem() == ItemByakugan.helmet) {
						if (!_stack.hasTagCompound()) {
							_stack.setTagCompound(new NBTTagCompound());
						}
						_stack.getTagCompound().setDouble("HakkeshoKaitenCD", NarutomodModVariables.world_tick + cooldown);
						((EntityPlayer)summoner).getFoodStats()
						 .setFoodLevel(((EntityPlayer)summoner).getFoodStats().getFoodLevel() - (this.ticksExisted / 60 + 1));
					}
				}
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/kaiten.png");
			private final ModelKaiten model = new ModelKaiten();
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float partialTicks) {
				float f2 = partialTicks + entity.ticksExisted;
				float scale = entity.getScale() * 3.0F;
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)x, (float)y, (float)z);
				//GlStateManager.color(1f, 1f, 1f, 0.f);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.rotate(f2 * 30.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.model.render(entity, 0f, 0f, f2, 0f, 0f, 0.0625f);
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				//GlStateManager.color(1f, 1f, 1f, 1f);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityCustom entity) {
				return this.texture;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelKaiten extends ModelBase {
			private final ModelRenderer ball;
			private final ModelRenderer hexadecagon;
			private final ModelRenderer hexadecagon_r1;
			private final ModelRenderer hexadecagon_r2;
			private final ModelRenderer hexadecagon_r3;
			private final ModelRenderer hexadecagon_r4;
			private final ModelRenderer hexadecagon_r5;
			private final ModelRenderer hexadecagon_r6;
			private final ModelRenderer hexadecagon_r7;
			private final ModelRenderer hexadecagon2;
			private final ModelRenderer hexadecagon_r8;
			private final ModelRenderer hexadecagon_r9;
			private final ModelRenderer hexadecagon_r10;
			private final ModelRenderer hexadecagon_r11;
			private final ModelRenderer hexadecagon_r12;
			private final ModelRenderer hexadecagon_r13;
			private final ModelRenderer hexadecagon_r14;
			private final ModelRenderer hexadecagon3;
			private final ModelRenderer hexadecagon_r15;
			private final ModelRenderer hexadecagon_r16;
			private final ModelRenderer hexadecagon_r17;
			private final ModelRenderer hexadecagon_r18;
			private final ModelRenderer hexadecagon_r19;
			private final ModelRenderer hexadecagon_r20;
			private final ModelRenderer hexadecagon_r21;
			private final ModelRenderer hexadecagon4;
			private final ModelRenderer hexadecagon_r22;
			private final ModelRenderer hexadecagon_r23;
			private final ModelRenderer hexadecagon_r24;
			private final ModelRenderer hexadecagon_r25;
			private final ModelRenderer hexadecagon_r26;
			private final ModelRenderer hexadecagon_r27;
			private final ModelRenderer hexadecagon_r28;
			private final ModelRenderer hexadecagon5;
			private final ModelRenderer hexadecagon_r29;
			private final ModelRenderer hexadecagon_r30;
			private final ModelRenderer hexadecagon_r31;
			private final ModelRenderer hexadecagon_r32;
			private final ModelRenderer hexadecagon_r33;
			private final ModelRenderer hexadecagon_r34;
			private final ModelRenderer hexadecagon_r35;
			private final ModelRenderer hexadecagon6;
			private final ModelRenderer hexadecagon_r36;
			private final ModelRenderer hexadecagon_r37;
			private final ModelRenderer hexadecagon_r38;
			private final ModelRenderer hexadecagon_r39;
			private final ModelRenderer hexadecagon_r40;
			private final ModelRenderer hexadecagon_r41;
			private final ModelRenderer hexadecagon_r42;
			private final ModelRenderer hexadecagon7;
			private final ModelRenderer hexadecagon_r43;
			private final ModelRenderer hexadecagon_r44;
			private final ModelRenderer hexadecagon_r45;
			private final ModelRenderer hexadecagon_r46;
			private final ModelRenderer hexadecagon_r47;
			private final ModelRenderer hexadecagon_r48;
			private final ModelRenderer hexadecagon_r49;
			private final ModelRenderer hexadecagon8;
			private final ModelRenderer hexadecagon_r50;
			private final ModelRenderer hexadecagon_r51;
			private final ModelRenderer hexadecagon_r52;
			private final ModelRenderer hexadecagon_r53;
			private final ModelRenderer hexadecagon_r54;
			private final ModelRenderer hexadecagon_r55;
			private final ModelRenderer hexadecagon_r56;
		
			public ModelKaiten() {
				textureWidth = 24;
				textureHeight = 24;
		
				ball = new ModelRenderer(this);
				ball.setRotationPoint(0.0F, 0.0F, 0.0F);
				
				hexadecagon = new ModelRenderer(this);
				hexadecagon.setRotationPoint(0.0F, 0.0F, 0.0F);
				ball.addChild(hexadecagon);
		
				hexadecagon_r1 = new ModelRenderer(this);
				hexadecagon_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r1);
				setRotationAngle(hexadecagon_r1, -2.3562F, 0.0F, 0.0F);
				hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r2 = new ModelRenderer(this);
				hexadecagon_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r2);
				setRotationAngle(hexadecagon_r2, -1.9635F, 0.0F, 0.0F);
				hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r3 = new ModelRenderer(this);
				hexadecagon_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r3);
				setRotationAngle(hexadecagon_r3, -1.5708F, 0.0F, 0.0F);
				hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r4 = new ModelRenderer(this);
				hexadecagon_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r4);
				setRotationAngle(hexadecagon_r4, -1.1781F, 0.0F, 0.0F);
				hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r5 = new ModelRenderer(this);
				hexadecagon_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r5);
				setRotationAngle(hexadecagon_r5, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r6 = new ModelRenderer(this);
				hexadecagon_r6.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r6);
				setRotationAngle(hexadecagon_r6, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r7 = new ModelRenderer(this);
				hexadecagon_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon.addChild(hexadecagon_r7);
				setRotationAngle(hexadecagon_r7, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r7.cubeList.add(new ModelBox(hexadecagon_r7, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon2 = new ModelRenderer(this);
				hexadecagon2.setRotationPoint(0.0F, 0.0F, 0.0F);
				ball.addChild(hexadecagon2);
				setRotationAngle(hexadecagon2, 0.0F, -0.3927F, 0.0F);
				
		
				hexadecagon_r8 = new ModelRenderer(this);
				hexadecagon_r8.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r8);
				setRotationAngle(hexadecagon_r8, -2.3562F, 0.0F, 0.0F);
				hexadecagon_r8.cubeList.add(new ModelBox(hexadecagon_r8, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r9 = new ModelRenderer(this);
				hexadecagon_r9.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r9);
				setRotationAngle(hexadecagon_r9, -1.9635F, 0.0F, 0.0F);
				hexadecagon_r9.cubeList.add(new ModelBox(hexadecagon_r9, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r10 = new ModelRenderer(this);
				hexadecagon_r10.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r10);
				setRotationAngle(hexadecagon_r10, -1.5708F, 0.0F, 0.0F);
				hexadecagon_r10.cubeList.add(new ModelBox(hexadecagon_r10, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r11 = new ModelRenderer(this);
				hexadecagon_r11.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r11);
				setRotationAngle(hexadecagon_r11, -1.1781F, 0.0F, 0.0F);
				hexadecagon_r11.cubeList.add(new ModelBox(hexadecagon_r11, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r12 = new ModelRenderer(this);
				hexadecagon_r12.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r12);
				setRotationAngle(hexadecagon_r12, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r12.cubeList.add(new ModelBox(hexadecagon_r12, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r13 = new ModelRenderer(this);
				hexadecagon_r13.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r13);
				setRotationAngle(hexadecagon_r13, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r13.cubeList.add(new ModelBox(hexadecagon_r13, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r14 = new ModelRenderer(this);
				hexadecagon_r14.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon2.addChild(hexadecagon_r14);
				setRotationAngle(hexadecagon_r14, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r14.cubeList.add(new ModelBox(hexadecagon_r14, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon3 = new ModelRenderer(this);
				hexadecagon3.setRotationPoint(0.0F, 0.0F, 0.0F);
				ball.addChild(hexadecagon3);
				setRotationAngle(hexadecagon3, 0.0F, -0.7854F, 0.0F);
				
		
				hexadecagon_r15 = new ModelRenderer(this);
				hexadecagon_r15.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r15);
				setRotationAngle(hexadecagon_r15, -2.3562F, 0.0F, 0.0F);
				hexadecagon_r15.cubeList.add(new ModelBox(hexadecagon_r15, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r16 = new ModelRenderer(this);
				hexadecagon_r16.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r16);
				setRotationAngle(hexadecagon_r16, -1.9635F, 0.0F, 0.0F);
				hexadecagon_r16.cubeList.add(new ModelBox(hexadecagon_r16, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r17 = new ModelRenderer(this);
				hexadecagon_r17.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r17);
				setRotationAngle(hexadecagon_r17, -1.5708F, 0.0F, 0.0F);
				hexadecagon_r17.cubeList.add(new ModelBox(hexadecagon_r17, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r18 = new ModelRenderer(this);
				hexadecagon_r18.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r18);
				setRotationAngle(hexadecagon_r18, -1.1781F, 0.0F, 0.0F);
				hexadecagon_r18.cubeList.add(new ModelBox(hexadecagon_r18, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r19 = new ModelRenderer(this);
				hexadecagon_r19.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r19);
				setRotationAngle(hexadecagon_r19, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r19.cubeList.add(new ModelBox(hexadecagon_r19, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r20 = new ModelRenderer(this);
				hexadecagon_r20.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r20);
				setRotationAngle(hexadecagon_r20, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r20.cubeList.add(new ModelBox(hexadecagon_r20, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r21 = new ModelRenderer(this);
				hexadecagon_r21.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon3.addChild(hexadecagon_r21);
				setRotationAngle(hexadecagon_r21, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r21.cubeList.add(new ModelBox(hexadecagon_r21, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon4 = new ModelRenderer(this);
				hexadecagon4.setRotationPoint(0.0F, 0.0F, 0.0F);
				ball.addChild(hexadecagon4);
				setRotationAngle(hexadecagon4, 0.0F, -1.1781F, 0.0F);
				
		
				hexadecagon_r22 = new ModelRenderer(this);
				hexadecagon_r22.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r22);
				setRotationAngle(hexadecagon_r22, -2.3562F, 0.0F, 0.0F);
				hexadecagon_r22.cubeList.add(new ModelBox(hexadecagon_r22, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r23 = new ModelRenderer(this);
				hexadecagon_r23.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r23);
				setRotationAngle(hexadecagon_r23, -1.9635F, 0.0F, 0.0F);
				hexadecagon_r23.cubeList.add(new ModelBox(hexadecagon_r23, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r24 = new ModelRenderer(this);
				hexadecagon_r24.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r24);
				setRotationAngle(hexadecagon_r24, -1.5708F, 0.0F, 0.0F);
				hexadecagon_r24.cubeList.add(new ModelBox(hexadecagon_r24, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r25 = new ModelRenderer(this);
				hexadecagon_r25.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r25);
				setRotationAngle(hexadecagon_r25, -1.1781F, 0.0F, 0.0F);
				hexadecagon_r25.cubeList.add(new ModelBox(hexadecagon_r25, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r26 = new ModelRenderer(this);
				hexadecagon_r26.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r26);
				setRotationAngle(hexadecagon_r26, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r26.cubeList.add(new ModelBox(hexadecagon_r26, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r27 = new ModelRenderer(this);
				hexadecagon_r27.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r27);
				setRotationAngle(hexadecagon_r27, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r27.cubeList.add(new ModelBox(hexadecagon_r27, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r28 = new ModelRenderer(this);
				hexadecagon_r28.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon4.addChild(hexadecagon_r28);
				setRotationAngle(hexadecagon_r28, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r28.cubeList.add(new ModelBox(hexadecagon_r28, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon5 = new ModelRenderer(this);
				hexadecagon5.setRotationPoint(0.0F, 0.0F, 0.0F);
				ball.addChild(hexadecagon5);
				setRotationAngle(hexadecagon5, 0.0F, -1.5708F, 0.0F);
				
		
				hexadecagon_r29 = new ModelRenderer(this);
				hexadecagon_r29.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r29);
				setRotationAngle(hexadecagon_r29, -2.3562F, 0.0F, 0.0F);
				hexadecagon_r29.cubeList.add(new ModelBox(hexadecagon_r29, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r30 = new ModelRenderer(this);
				hexadecagon_r30.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r30);
				setRotationAngle(hexadecagon_r30, -1.9635F, 0.0F, 0.0F);
				hexadecagon_r30.cubeList.add(new ModelBox(hexadecagon_r30, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r31 = new ModelRenderer(this);
				hexadecagon_r31.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r31);
				setRotationAngle(hexadecagon_r31, -1.5708F, 0.0F, 0.0F);
				hexadecagon_r31.cubeList.add(new ModelBox(hexadecagon_r31, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r32 = new ModelRenderer(this);
				hexadecagon_r32.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r32);
				setRotationAngle(hexadecagon_r32, -1.1781F, 0.0F, 0.0F);
				hexadecagon_r32.cubeList.add(new ModelBox(hexadecagon_r32, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r33 = new ModelRenderer(this);
				hexadecagon_r33.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r33);
				setRotationAngle(hexadecagon_r33, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r33.cubeList.add(new ModelBox(hexadecagon_r33, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r34 = new ModelRenderer(this);
				hexadecagon_r34.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r34);
				setRotationAngle(hexadecagon_r34, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r34.cubeList.add(new ModelBox(hexadecagon_r34, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r35 = new ModelRenderer(this);
				hexadecagon_r35.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon5.addChild(hexadecagon_r35);
				setRotationAngle(hexadecagon_r35, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r35.cubeList.add(new ModelBox(hexadecagon_r35, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon6 = new ModelRenderer(this);
				hexadecagon6.setRotationPoint(0.0F, 0.0F, 0.0F);
				ball.addChild(hexadecagon6);
				setRotationAngle(hexadecagon6, 0.0F, -1.9635F, 0.0F);
				
		
				hexadecagon_r36 = new ModelRenderer(this);
				hexadecagon_r36.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r36);
				setRotationAngle(hexadecagon_r36, -2.3562F, 0.0F, 0.0F);
				hexadecagon_r36.cubeList.add(new ModelBox(hexadecagon_r36, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r37 = new ModelRenderer(this);
				hexadecagon_r37.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r37);
				setRotationAngle(hexadecagon_r37, -1.9635F, 0.0F, 0.0F);
				hexadecagon_r37.cubeList.add(new ModelBox(hexadecagon_r37, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r38 = new ModelRenderer(this);
				hexadecagon_r38.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r38);
				setRotationAngle(hexadecagon_r38, -1.5708F, 0.0F, 0.0F);
				hexadecagon_r38.cubeList.add(new ModelBox(hexadecagon_r38, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r39 = new ModelRenderer(this);
				hexadecagon_r39.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r39);
				setRotationAngle(hexadecagon_r39, -1.1781F, 0.0F, 0.0F);
				hexadecagon_r39.cubeList.add(new ModelBox(hexadecagon_r39, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r40 = new ModelRenderer(this);
				hexadecagon_r40.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r40);
				setRotationAngle(hexadecagon_r40, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r40.cubeList.add(new ModelBox(hexadecagon_r40, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r41 = new ModelRenderer(this);
				hexadecagon_r41.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r41);
				setRotationAngle(hexadecagon_r41, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r41.cubeList.add(new ModelBox(hexadecagon_r41, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r42 = new ModelRenderer(this);
				hexadecagon_r42.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon6.addChild(hexadecagon_r42);
				setRotationAngle(hexadecagon_r42, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r42.cubeList.add(new ModelBox(hexadecagon_r42, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon7 = new ModelRenderer(this);
				hexadecagon7.setRotationPoint(0.0F, 0.0F, 0.0F);
				ball.addChild(hexadecagon7);
				setRotationAngle(hexadecagon7, 0.0F, -2.3562F, 0.0F);
				
		
				hexadecagon_r43 = new ModelRenderer(this);
				hexadecagon_r43.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r43);
				setRotationAngle(hexadecagon_r43, -2.3562F, 0.0F, 0.0F);
				hexadecagon_r43.cubeList.add(new ModelBox(hexadecagon_r43, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r44 = new ModelRenderer(this);
				hexadecagon_r44.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r44);
				setRotationAngle(hexadecagon_r44, -1.9635F, 0.0F, 0.0F);
				hexadecagon_r44.cubeList.add(new ModelBox(hexadecagon_r44, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r45 = new ModelRenderer(this);
				hexadecagon_r45.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r45);
				setRotationAngle(hexadecagon_r45, -1.5708F, 0.0F, 0.0F);
				hexadecagon_r45.cubeList.add(new ModelBox(hexadecagon_r45, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r46 = new ModelRenderer(this);
				hexadecagon_r46.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r46);
				setRotationAngle(hexadecagon_r46, -1.1781F, 0.0F, 0.0F);
				hexadecagon_r46.cubeList.add(new ModelBox(hexadecagon_r46, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r47 = new ModelRenderer(this);
				hexadecagon_r47.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r47);
				setRotationAngle(hexadecagon_r47, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r47.cubeList.add(new ModelBox(hexadecagon_r47, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r48 = new ModelRenderer(this);
				hexadecagon_r48.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r48);
				setRotationAngle(hexadecagon_r48, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r48.cubeList.add(new ModelBox(hexadecagon_r48, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r49 = new ModelRenderer(this);
				hexadecagon_r49.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon7.addChild(hexadecagon_r49);
				setRotationAngle(hexadecagon_r49, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r49.cubeList.add(new ModelBox(hexadecagon_r49, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon8 = new ModelRenderer(this);
				hexadecagon8.setRotationPoint(0.0F, 0.0F, 0.0F);
				ball.addChild(hexadecagon8);
				setRotationAngle(hexadecagon8, 0.0F, -2.7489F, 0.0F);
				
		
				hexadecagon_r50 = new ModelRenderer(this);
				hexadecagon_r50.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r50);
				setRotationAngle(hexadecagon_r50, -2.3562F, 0.0F, 0.0F);
				hexadecagon_r50.cubeList.add(new ModelBox(hexadecagon_r50, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r51 = new ModelRenderer(this);
				hexadecagon_r51.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r51);
				setRotationAngle(hexadecagon_r51, -1.9635F, 0.0F, 0.0F);
				hexadecagon_r51.cubeList.add(new ModelBox(hexadecagon_r51, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r52 = new ModelRenderer(this);
				hexadecagon_r52.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r52);
				setRotationAngle(hexadecagon_r52, -1.5708F, 0.0F, 0.0F);
				hexadecagon_r52.cubeList.add(new ModelBox(hexadecagon_r52, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r53 = new ModelRenderer(this);
				hexadecagon_r53.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r53);
				setRotationAngle(hexadecagon_r53, -1.1781F, 0.0F, 0.0F);
				hexadecagon_r53.cubeList.add(new ModelBox(hexadecagon_r53, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r54 = new ModelRenderer(this);
				hexadecagon_r54.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r54);
				setRotationAngle(hexadecagon_r54, -0.7854F, 0.0F, 0.0F);
				hexadecagon_r54.cubeList.add(new ModelBox(hexadecagon_r54, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r55 = new ModelRenderer(this);
				hexadecagon_r55.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r55);
				setRotationAngle(hexadecagon_r55, -0.3927F, 0.0F, 0.0F);
				hexadecagon_r55.cubeList.add(new ModelBox(hexadecagon_r55, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
		
				hexadecagon_r56 = new ModelRenderer(this);
				hexadecagon_r56.setRotationPoint(0.0F, 0.0F, 0.0F);
				hexadecagon8.addChild(hexadecagon_r56);
				setRotationAngle(hexadecagon_r56, 0.3927F, 0.0F, 0.0F);
				hexadecagon_r56.cubeList.add(new ModelBox(hexadecagon_r56, 0, 0, -2.0F, -10.0F, -2.0F, 4, 20, 4, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				ball.render(f5);
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}

	/*@SideOnly(Side.CLIENT)
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
	}*/
}
