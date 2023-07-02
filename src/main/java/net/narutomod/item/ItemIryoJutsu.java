
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.entity.EntityPoisonMist;
import net.narutomod.entity.EntityCellularActivation;
import net.narutomod.entity.EntityEnhancedStrength;
import net.narutomod.potion.PotionChakraEnhancedStrength;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Particles;
import net.narutomod.Chakra;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemIryoJutsu extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:iryo_jutsu")
	public static final Item block = null;
	public static final int ENTITYID = 210;
	public static final ItemJutsu.JutsuEnum HEALING = new ItemJutsu.JutsuEnum(0, "healingjutsu", 'A', 0.25d, new HealingJutsu());
	public static final ItemJutsu.JutsuEnum POISONMIST = new ItemJutsu.JutsuEnum(1, "poison_mist", 'B', 20d, new EntityPoisonMist.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum MEDMODE = new ItemJutsu.JutsuEnum(2, "cellular_activation", 'A', 20d, new EntityCellularActivation.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum POWERMODE = new ItemJutsu.JutsuEnum(3, "enhanced_strength", 'A', 30d, new ChakraEnhancedStrength());

	public ItemIryoJutsu(ElementsNarutomodMod instance) {
		super(instance, 523);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(HEALING, POISONMIST, MEDMODE, POWERMODE));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:iryo_jutsu", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.IRYO, list);
			this.setUnlocalizedName("iryo_jutsu");
			this.setRegistryName("iryo_jutsu");
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			if (this.getCurrentJutsu(stack) == POISONMIST) {
				return this.getPower(stack, entity, timeLeft, 5f, 15f);
			}
			return 1.0f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			float f = super.getMaxPower(stack, entity);
			if (jutsu == POISONMIST) {
				return Math.min(f, 35.0f);
			}
			return f;
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
			if (!world.isRemote) {
				float power = this.getPower(stack, entity, timeLeft);
				if (this.executeJutsu(stack, entity, power)) {
					if (this.getCurrentJutsu(stack) != HEALING || timeLeft < this.getMaxUseDuration() - 200) {
						this.addCurrentJutsuXp(stack, 1);
					}
				}
			}
		}

		private float xpModifier(EntityLivingBase player, ItemStack stack) {
			return (float)Chakra.getLevel(player) 
			 * (player instanceof EntityPlayer && ((EntityPlayer)player).isCreative() 
			  ? 1f : (float)this.getCurrentJutsuXp(stack) / (float)this.getCurrentJutsuRequiredXp(stack));
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
			if (this.getCurrentJutsu(stack) == HEALING) {
				if (!player.world.isRemote) {
					this.executeJutsu(stack, player, this.xpModifier(player, stack) / 15f);
				}
				return;
			}
			super.onUsingTick(stack, player, count);
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityPlayer && POWERMODE.jutsu.isActivated(itemstack)
			 && entity.ticksExisted % 10 == 2) {
				((EntityPlayer)entity).addPotionEffect(new PotionEffect(PotionChakraEnhancedStrength.potion,
				 12, (int)Chakra.getLevel((EntityPlayer)entity) / 2, true, false));
			}
		}
	}

	public static class HealingJutsu implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			EntityLivingBase target = null;
			RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 3d);
			if (res != null) {
				if (res.entityHit instanceof EntityLivingBase) {
					target = (EntityLivingBase)res.entityHit;
				} else if ((int)res.hitVec.x == (int)entity.posX 
				 && (int)res.hitVec.y == (int)entity.posY && (int)res.hitVec.z == (int)entity.posZ) {
					target = entity;
				}
			}
			if (target != null) {
				this.createJutsu(entity, target, power);
				return true;
			}
			return false;
		}

		public void createJutsu(EntityLivingBase entity, EntityLivingBase target, float power) {
			if (entity.ticksExisted % 3 == 0) {
				entity.world.playSound(null, target.posX, target.posY, target.posZ, 
				 (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:windecho")),
				 SoundCategory.NEUTRAL, 0.5f, MathHelper.sin((float)entity.ticksExisted * 0.1f) * 0.8f + 1.5f);
			}
			Particles.spawnParticle(entity.world, Particles.Types.SMOKE, target.posX, target.posY+target.height/2,
			 target.posZ, 10, 0d, 0d, 0d, 0d, 0d, 0d, 0x0000fff6|((0x20+target.getRNG().nextInt(0x20))<<24),
			 10 + target.getRNG().nextInt(25), 0, 0xF0, -1, 0);
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 6, false, false));
			target.heal(power * 0.02f);
		}

		public static class PlayerHook {
			@SubscribeEvent
			public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
				EntityPlayer player = event.getEntityPlayer();
				ItemStack stack = player.getHeldItemMainhand();
				if (stack.getItem() == block && ((RangedItem)block).getCurrentJutsu(stack) == HEALING) {
					event.setCanceled(true);
					event.setCancellationResult(EnumActionResult.PASS);
				}
			}
		}
	}

	public static class ChakraEnhancedStrength implements ItemJutsu.IJutsuCallback {
		//private boolean activated = false;
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			boolean flag = !entity.isPotionActive(PotionChakraEnhancedStrength.potion);
			stack.getTagCompound().setBoolean("isChakraEnhancedStrengthActive", flag);
			return flag;
		}

		@Override
		public boolean isActivated(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("isChakraEnhancedStrengthActive") : false;
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new HealingJutsu.PlayerHook());
	}
}
