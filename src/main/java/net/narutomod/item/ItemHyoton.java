
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntitySpike;
import net.narutomod.entity.EntityIceSpear;
import net.narutomod.entity.EntityIceDome;
import net.narutomod.entity.EntityIcePrison;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.EntityTracker;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemHyoton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:hyoton")
	public static final Item block = null;
	public static final int ENTITYID = 219;
	public static final ItemJutsu.JutsuEnum KILLSPIKES = new ItemJutsu.JutsuEnum(0, "ice_spike", 'S', 150, 20d, new EntityIceSpike.Jutsu());
	public static final ItemJutsu.JutsuEnum ICESPEARS = new ItemJutsu.JutsuEnum(1, "ice_spear", 'S', 150, 20d, new EntityIceSpear.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum ICEDOME = new ItemJutsu.JutsuEnum(2, "ice_dome", 'S', 200, 100d, new EntityIceDome.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum ICEPRISON = new ItemJutsu.JutsuEnum(3, "ice_prison", 'S', 150, 50d, new EntityIcePrison.EC.Jutsu());

	public ItemHyoton(ElementsNarutomodMod instance) {
		super(instance, 531);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(KILLSPIKES, ICESPEARS, ICEDOME, ICEPRISON));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityIceSpike.class)
		 .id(new ResourceLocation("narutomod", "ice_spike"), ENTITYID).name("ice_spike").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:hyoton", "inventory"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RangedItem.DamageHook());
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.HYOTON, list);
			setUnlocalizedName("hyoton");
			setRegistryName("hyoton");
			setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[KILLSPIKES.index] = 0;
			this.defaultCooldownMap[ICESPEARS.index] = 0;
			this.defaultCooldownMap[ICEDOME.index] = 0;
			this.defaultCooldownMap[ICEPRISON.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == KILLSPIKES) {
				return this.getPower(stack, entity, timeLeft, 1f, 10f);
			} else if (jutsu == ICESPEARS) {
				return this.getPower(stack, entity, timeLeft, 1f, 40f);
			}
			return 1f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float f = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == KILLSPIKES) {
				return Math.min(f, 300f);
			} else if (jutsu == ICESPEARS) {
				return Math.min(f, 50f);
			}
			return f;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			if (entity.isCreative() || (ProcedureUtils.hasItemInInventory(entity, ItemFuton.block) 
			 && ProcedureUtils.hasItemInInventory(entity, ItemSuiton.block))) { 
				return super.onItemRightClick(world, entity, hand);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, entity.getHeldItem(hand));
		}

		private void setlastTickPos(Entity entity, BlockPos pos) {
			NBTTagCompound compound = entity.getEntityData().getCompoundTag("lastTickBlockPos");
			if (compound == null) {
				compound = new NBTTagCompound();
			}
			compound.setInteger("X", pos.getX());
			compound.setInteger("Y", pos.getY());
			compound.setInteger("Z", pos.getZ());
			entity.getEntityData().setTag("lastTickBlockPos", compound);
		}

		private BlockPos getLastTickPos(Entity entity) {
			NBTTagCompound compound = entity.getEntityData().getCompoundTag("lastTickBlockPos");
			return compound != null
			 ? new BlockPos(compound.getInteger("X"), compound.getInteger("Y"), compound.getInteger("Z"))
			 : BlockPos.ORIGIN;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityLivingBase) {
				//BlockPos pos = new BlockPos(entity);
				//EntityTracker.SessionDataHolder edh = EntityTracker.getOrCreate(entity);
				BlockPos pos = entity.getPosition();
				EntityLivingBase living = (EntityLivingBase)entity;
				living.extinguish();
				//if (!pos.equals(edh.prevBlockPos)) {
				//	edh.prevBlockPos = pos;
				if (!pos.equals(this.getLastTickPos(entity))) {
					this.setlastTickPos(entity, pos);
					EnchantmentFrostWalker.freezeNearby(living, world, pos, 1);
				}
				if (living.ticksExisted % 20 == 3) {
					living.addPotionEffect(new PotionEffect(MobEffects.SPEED, 22, 3, false, false));
					if (entity instanceof EntityPlayer && !ProcedureUtils.hasItemInInventory((EntityPlayer)entity, ItemIceSenbon.block)) {
						ItemHandlerHelper.giveItemToPlayer((EntityPlayer)entity, new ItemStack(ItemIceSenbon.block));
					}
				}
			}
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(TextFormatting.GREEN + net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.hyoton.musthave") + TextFormatting.RESET);
		}

		public static class DamageHook {
			@SubscribeEvent
			public void onDamage(LivingAttackEvent event) {
				EntityLivingBase entity = event.getEntityLiving();
				DamageSource source = event.getSource();
				if (source == DamageSource.IN_WALL && entity.isInsideOfMaterial(Material.ICE)
				 && entity instanceof EntityPlayer && ProcedureUtils.hasItemInInventory((EntityPlayer)entity, block)) {
					event.setCanceled(true);
				}
			}
		}
	}

	public static class EntityIceSpike extends EntitySpike.Base {
		private final int growTime = 10;
		private final float maxScale = 3.0f;
		private final float damage = 20.0f;
		private EntityLivingBase user;

		public EntityIceSpike(World worldIn) {
			super(worldIn);
			this.setColor(0xC0FFFFFF);
			this.isImmuneToFire = true;
		}

		public EntityIceSpike(EntityLivingBase userIn) {
			this(userIn.world);
			this.user = userIn;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksAlive <= this.growTime) {
				this.setEntityScale(MathHelper.clamp(this.maxScale * (float)this.ticksAlive / this.growTime, 0.0f, this.maxScale));
				for (EntityLivingBase entity : 
				 this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(1d, 0d, 1d))) {
					if (!entity.equals(this.user)) {
						entity.hurtResistantTime = 10;
						entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.user),
						 this.damage * (1f - (float)(this.ticksAlive - 1) / this.growTime));
					}
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				World world = entity.world;
				Vec3d vec3d = entity.getPositionEyes(1f);
				Vec3d vec3d2 = vec3d.add(entity.getLookVec().scale(30d));
				RayTraceResult res = world.rayTraceBlocks(vec3d, vec3d2, false, true, true);
				if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK && res.sideHit == EnumFacing.UP) {
					world.playSound(null, entity.posX, entity.posY, entity.posZ, (net.minecraft.util.SoundEvent)
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:spiked")),
					 net.minecraft.util.SoundCategory.NEUTRAL, 5f, entity.getRNG().nextFloat() * 0.4f + 0.8f);
					float f = MathHelper.sqrt(power * 9f / 5f);
					for (int i = 0; i < Math.round(power); i++) {
						EntityIceSpike entity1 = new EntityIceSpike(entity);
						Vec3d vec = res.hitVec.addVector((entity.getRNG().nextDouble() - 0.5d) * f, 0d, (entity.getRNG().nextDouble() - 0.5d) * f);
						for (; !world.getBlockState(new BlockPos(vec)).isTopSolid(); vec = vec.subtract(0d, 1d, 0d));
						for (; world.getBlockState(new BlockPos(vec).up()).isTopSolid(); vec = vec.addVector(0d, 1d, 0d));
						entity1.setLocationAndAngles(vec.x, vec.y + 0.5d, vec.z, entity.getRNG().nextFloat() * 360f, (entity.getRNG().nextFloat() - 0.5f) * 60f);
						world.spawnEntity(entity1);
					}
					return true;
				}
				return false;
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
			RenderingRegistry.registerEntityRenderingHandler(EntityIceSpike.class, renderManager -> new CustomRender(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends EntitySpike.Renderer<EntityIceSpike> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/spike_ice.png");
	
			public CustomRender(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityIceSpike entity) {
				return this.texture;
			}
		}
	}
}
