
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.entity.*;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemYoton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:yoton")
	public static final Item block = null;
	public static final int ENTITYID = 149;
	public static final ItemJutsu.JutsuEnum MULTISIZE = new ItemJutsu.JutsuEnum(0, "biggerme", 'B', 50d, new EntityBiggerMe.Jutsu());
	public static final ItemJutsu.JutsuEnum FUUIN = new ItemJutsu.JutsuEnum(1, "sealing", 'S', 100d, new EntitySealing.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SEALINGCHAIN = new ItemJutsu.JutsuEnum(2, "sealing_chains", 'A', 50d, new EntitySealingChains.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SEALING9D = new ItemJutsu.JutsuEnum(3, "tooltip.phantom9sealing.name", 'S', 100d, new EntityGedoStatue.Sealing9Jutsu());
	public static final ItemJutsu.JutsuEnum SEALING10 = new ItemJutsu.JutsuEnum(4, "tooltip.10coffinseal.name", 'S', 100d, new EntityTenTails.CoffinSealJutsu());

	public ItemYoton(ElementsNarutomodMod instance) {
		super(instance, 406);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(MULTISIZE, FUUIN, SEALINGCHAIN, SEALING9D, SEALING10));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityBiggerMe.class)
				.id(new ResourceLocation("narutomod", "biggerme"), ENTITYID).name("biggerme").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:yoton", "inventory"));
		//this.elements.addNetworkMessage(MessageHandler.class, Message.class, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerRenderHook());
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.YOTON, list);
			this.setUnlocalizedName("yoton");
			this.setRegistryName("yoton");
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			if (this.getCurrentJutsu(stack) == MULTISIZE) {
				return this.getPower(stack, entity, timeLeft, 2f, 50f);
			}
			return 1.0f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			return Math.min(super.getMaxPower(stack, entity), 10f);
		}
	}

	public static class EntityBiggerMe extends EntityClone.Base {
		private final int growTime = 40;
		private float scale;

		public EntityBiggerMe(World a) {
			super(a);
		}

		public EntityBiggerMe(EntityLivingBase user, float scaleIn) {
			super(user);
			//this.setScale(1f);
			this.scale = scaleIn;
			this.stepHeight = scaleIn * this.height / 3.0F;
			this.setNoAI(true);
			double d = MathHelper.sqrt((4d * scaleIn * scaleIn) + (this.height * this.height));
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).applyModifier(new AttributeModifier("biggerme.reach", d, 0));
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(new AttributeModifier("biggerme.damage", scaleIn * scaleIn, 0));
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(user.getHealth() * scaleIn);
			this.setHealth(this.getMaxHealth());
			this.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 999999, (int)scaleIn, false, false));
			user.startRiding(this);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(EntityPlayer.REACH_DISTANCE);
			this.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(2.0D);
		}

		@Override
		public double getMountedYOffset() {
			return this.height - 1.85d;
		}

		@Override
		public boolean shouldRiderSit() {
			return false;
		}

		@Override
		public boolean canBeSteered() {
			return true;
		}

		@Override
		public Entity getControllingPassenger() {
			return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getTrueSource() != null && source.getTrueSource().equals(this.getSummoner())) {
				return false;
			}
			if (this.getControllingPassenger() != null) {
				return this.getControllingPassenger().attackEntityFrom(source, amount);
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public void travel(float ti, float tj, float tk) {
			if (this.isBeingRidden() && this.canPassengerSteer()) {
				Entity entity = this.getControllingPassenger();
				this.rotationYaw = entity.rotationYaw;
				this.prevRotationYaw = this.rotationYaw;
				this.rotationPitch = entity.rotationPitch;
				this.setRotation(this.rotationYaw, this.rotationPitch);
				this.jumpMovementFactor = this.getAIMoveSpeed() * 0.15F;
				this.renderYawOffset = entity.rotationYaw;
				this.rotationYawHead = entity.rotationYaw;
				this.stepHeight = this.height / 3.0F;
				if (entity instanceof EntityLivingBase) {
					this.checkJump((EntityLivingBase)entity);
					this.setAIMoveSpeed((float) ProcedureUtils.getModifiedSpeed(this) * 0.5F);
					float forward = ((EntityLivingBase) entity).moveForward;
					float strafe = ((EntityLivingBase) entity).moveStrafing;
					super.travel(strafe, 0.0F, forward);
				}
			} else {
				this.jumpMovementFactor = 0.02F;
				super.travel(ti, tj, tk);
			}
		}

		private void checkJump(EntityLivingBase entity) {
			if (this.world.isRemote) {
				if ((boolean)ReflectionHelper.getPrivateValue(EntityLivingBase.class, entity, 49) && this.onGround) {
					this.jump();
					ReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, false, 49);
				}
			}
		}

		@Override
		public void onUpdate() {
			if (!this.world.isRemote && this.ticksExisted <= this.growTime) {
				this.setScale(1f + (this.scale - 1f) * this.ticksExisted / this.growTime);
			}
			if (!this.isBeingRidden()) {
		        for (int k = 0; k < 500; ++k) {
				    this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
				      this.posX + this.rand.nextGaussian() * 0.5d * this.width, 
				      this.posY + this.rand.nextDouble() * this.height, 
				      this.posZ + this.rand.nextGaussian() * 0.5d * this.width, 
				      this.rand.nextGaussian() * 0.02D, 
				      this.rand.nextGaussian() * 0.02D, 
				      this.rand.nextGaussian() * 0.02D);
				}
				this.setDead();
			}
			super.onUpdate();
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.spawnEntity(new EntityBiggerMe(entity, power));
				return true;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityBiggerMe.class, renderManager -> new RenderBiggerMe(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderBiggerMe extends EntityClone.ClientRLM.RenderClone<EntityBiggerMe> {
			public RenderBiggerMe(RenderManager renderManager) {
				EntityClone.ClientRLM.getInstance().super(renderManager);
			}

			@Override
			public void doRender(EntityBiggerMe entity, double x, double y, double z, float entityYaw, float partialTicks) {
				Entity passenger = entity.getControllingPassenger();
				if (entity.isBeingRidden() && passenger instanceof AbstractClientPlayer) {
					this.copyLimbSwing(entity, (AbstractClientPlayer)passenger);
				}
				if (!Minecraft.getMinecraft().getRenderViewEntity().equals(passenger) || this.renderManager.options.thirdPersonView != 0) {
					super.doRender(entity, x, y, z, entityYaw, partialTicks);
				}
			}

			private void copyLimbSwing(EntityBiggerMe entity, AbstractClientPlayer rider) {
				entity.swingProgress = rider.swingProgress;
				entity.swingProgressInt = rider.swingProgressInt;
				entity.prevSwingProgress = rider.prevSwingProgress;
				entity.isSwingInProgress = rider.isSwingInProgress;
				entity.swingingHand = rider.swingingHand;
			}
		}
	}

	public class PlayerRenderHook {
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void onPlayerRender(RenderPlayerEvent.Pre event) {
			if (event.getEntityPlayer().getRidingEntity() instanceof EntityBiggerMe) {
				event.setCanceled(true);
			}
		}
	}
}
