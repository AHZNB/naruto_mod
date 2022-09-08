
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.init.MobEffects;
import net.minecraft.world.World;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Blocks;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityClone;
import net.narutomod.entity.EntityKageBunshin;
import net.narutomod.entity.EntityRasengan;
import net.narutomod.entity.EntityLimboClone;
import net.narutomod.entity.EntitySealingChains;
import net.narutomod.entity.EntityPuppet;
import net.narutomod.entity.EntityKikaichu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;

import javax.annotation.Nullable;
//import com.google.common.collect.ImmutableMap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemNinjutsu extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ninjutsu")
	public static final Item block = null;
	public static final int ENTITYID = 133;
	public static final ItemJutsu.JutsuEnum REPLACEMENT = new ItemJutsu.JutsuEnum(0, "replacementclone", 'D', 30d, new EntityReplacementClone.Jutsu());
	public static final ItemJutsu.JutsuEnum KAGEBUNSHIN = new ItemJutsu.JutsuEnum(1, "kage_bunshin", 'B', new EntityKageBunshin.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum RASENGAN = new ItemJutsu.JutsuEnum(2, "rasengan", 'A', 150d, new EntityRasengan.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum LIMBOCLONE = new ItemJutsu.JutsuEnum(3, "limbo_clone", 'S', EntityLimboClone.CHAKRA_USAGE, new EntityLimboClone.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SEALINGCHAIN = new ItemJutsu.JutsuEnum(4, "sealing_chains", 'A', 50d, new EntitySealingChains.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum PUPPET = new ItemJutsu.JutsuEnum(5, "tooltip.ninjutsu.puppetjutsu", 'C', 0.25d, new PuppetJutsu());
	public static final ItemJutsu.JutsuEnum BUGSWARM = new ItemJutsu.JutsuEnum(6, "bugball", 'C', 100d, new EntityKikaichu.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum INVISABILITY = new ItemJutsu.JutsuEnum(7, "tooltip.ninjutsu.hidingincamouflage", 'A', 20d, new HidingWithCamouflage());

	public ItemNinjutsu(ElementsNarutomodMod instance) {
		super(instance, 377);
	}
	
	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(REPLACEMENT, KAGEBUNSHIN, RASENGAN, LIMBOCLONE, SEALINGCHAIN, PUPPET, BUGSWARM, INVISABILITY));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityReplacementClone.class)
			.id(new ResourceLocation("narutomod", "replacementclone"), ENTITYID).name("replacementclone")
			.tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:ninjutsu", "inventory"));
	}

	public static boolean isJutsuEnabled(@Nullable ItemStack stack, ItemJutsu.JutsuEnum jutsu) {
		return stack != null && stack.getItem() == block && ((RangedItem)stack.getItem()).isJutsuEnabled(stack, jutsu);
	}

	public static ItemJutsu.JutsuEnum getCurrentJutsu(ItemStack stack) {
		return ((RangedItem)block).getCurrentJutsu(stack);
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.NINJUTSU, list);
			this.setUnlocalizedName("ninjutsu");
			this.setRegistryName("ninjutsu");
			this.setCreativeTab(TabModTab.tab);
			//this.defaultCooldownMap[REPLACEMENT.index] = 0;
			//this.defaultCooldownMap[KAGEBUNSHIN.index] = 0;
			//this.defaultCooldownMap[RASENGAN.index] = 0;
			//this.defaultCooldownMap[LIMBOCLONE.index] = 0;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float f = super.getMaxPower(stack, entity);
			if (this.getCurrentJutsu(stack) == RASENGAN) {
				return Math.min(f, 3.0f);
			}
			return f;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == RASENGAN) {
				return this.getPower(stack, entity, timeLeft, 0.0f, 200f);
			} else if (jutsu == BUGSWARM) {
				return this.getPower(stack, entity, timeLeft, 0.0f, 100f);
			}
			return 1f;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity.ticksExisted % 20 == 0
			 && entity instanceof EntityLivingBase && INVISABILITY.jutsu.isActivated(itemstack)) {
				if (Chakra.pathway((EntityLivingBase)entity).consume(INVISABILITY.chakraUsage)) {
					((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 21, 0, false, false));
				} else {
					HidingWithCamouflage.deactivate(itemstack);
				}
			}
		}
	}

	public static class EntityReplacementClone extends EntityClone.Base {
		public EntityReplacementClone(World world) {
			super(world);
			this.setNoAI(true);
		}

		public EntityReplacementClone(EntityLivingBase player, @Nullable Entity attacker) {
			super(player);
			if (attacker != null) {
				Vec3d vec3d = player.getPositionVector().subtract(attacker.getPositionVector()).normalize().scale(5.0d);
				player.rotationYaw = ProcedureUtils.getYawFromVec(vec3d.x, vec3d.z);
				player.setPositionAndUpdate(attacker.posX - vec3d.x, attacker.posY, attacker.posZ - vec3d.z);
			} else {
				Vec3d vec3d = player.getLookVec().scale(5);
				player.rotationYaw = (player.rotationYaw + 180f) % 360f;
				player.setPositionAndUpdate(player.posX + vec3d.x, player.posY, player.posZ + vec3d.z);
			}
		}
		
		@Override
		public void setDead() {
			super.setDead();
			this.world.playSound(null, this.posX, this.posY, this.posZ, (SoundEvent) SoundEvent.REGISTRY
			  .getObject(new ResourceLocation("narutomod:poof")), SoundCategory.NEUTRAL, 1.0F, 1.0F);
			if (!this.world.isRemote && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
				BlockPos pos = new BlockPos(this).up();
				this.world.setBlockState(pos, Blocks.LOG.getDefaultState(), 3);
				EntityFallingBlock fe = new EntityFallingBlock(this.world,
				 0.5d+pos.getX(), (double)pos.getY(), 0.5d+pos.getZ(), this.world.getBlockState(pos)) {
					@Override
					public void onUpdate() {
						ReflectionHelper.setPrivateValue(EntityFallingBlock.class, this, true, 3);
						super.onUpdate();
						if (this.onGround && this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")) {
							this.entityDropItem(new ItemStack(Blocks.LOG, 1, Blocks.LOG.damageDropped(Blocks.LOG.getDefaultState())), 0.0F);
						}
					}
				};
				fe.motionY = 0.15d;
				this.world.spawnEntity(fe);
			}
			for (int i = 0; i < 300; i++) {
				this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + this.rand.nextGaussian() * 0.5d, 
				  this.posY + 1d + this.rand.nextDouble() * 0.8d, this.posZ + this.rand.nextGaussian() * 0.5d, 0.0D, 0.0D, 0.0D);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted > 40) {
				this.setDead();
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String JUTSULASTUSEKEY = "ReplacementJutsuLastUse";
			private static final int COOLDOWN = 100;

			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (!stack.hasTagCompound()) {
					stack.setTagCompound(new NBTTagCompound());
				}
				if (!stack.getTagCompound().hasKey(JUTSULASTUSEKEY)) {
					stack.getTagCompound().setLong(JUTSULASTUSEKEY, entity.world.getTotalWorldTime());
				} else {
					stack.getTagCompound().removeTag(JUTSULASTUSEKEY);
				}
				return false;
			}
			
			@Override
			public boolean isActivated(ItemStack stack) {
				return stack.hasTagCompound() ? stack.getTagCompound().hasKey(JUTSULASTUSEKEY) : false;
			}

			public static class Hook {
				@SubscribeEvent
				public void onAttacked(LivingAttackEvent event) {
					EntityLivingBase entity = event.getEntityLiving();
					if (entity instanceof EntityPlayer && !entity.world.isRemote && event.getSource() != DamageSource.OUT_OF_WORLD) {
						ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, block);
						if (stack != null && REPLACEMENT.jutsu.isActivated(stack)) {
							long l = entity.world.getTotalWorldTime();
							if (l > stack.getTagCompound().getLong(JUTSULASTUSEKEY) + COOLDOWN 
							 && Chakra.pathway(entity).consume(REPLACEMENT.chakraUsage)) {
								event.setCanceled(true);
								stack.getTagCompound().setLong(JUTSULASTUSEKEY, l);
								entity.world.spawnEntity(new EntityReplacementClone(entity, event.getSource().getTrueSource()));
							}
						}
					}
				}
			}
		}
	}

	public static class PuppetJutsu implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (ProcedureUtils.objectEntityLookingAt(entity, 4d).entityHit instanceof EntityPuppet.Base) {
				return true;
			}
			return false;
		}
	}

	public static class HidingWithCamouflage implements ItemJutsu.IJutsuCallback {
		private static final String JUTSUACTIVEKEY = "HidingWithCamouflageActive";
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			boolean flag = this.isActivated(stack);
			stack.getTagCompound().setBoolean(JUTSUACTIVEKEY, !flag);
			return !flag;
		}

		@Override
		public boolean isActivated(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getBoolean(JUTSUACTIVEKEY) : false;
		}

		public static void deactivate(ItemStack stack) {
			stack.getTagCompound().setBoolean(JUTSUACTIVEKEY, false);
		}

		public static class Hook {
			@SubscribeEvent
			public void onSetAttackTarget(LivingSetAttackTargetEvent event) {
				EntityLivingBase entity = event.getEntityLiving();
				if (entity instanceof EntityLiving) {
					EntityLivingBase target = event.getTarget();
					if (target != null && target.isInvisible()
					 && !ItemSharingan.wearingAny(entity) && !ItemByakugan.wearingAny(entity)) {
						((EntityLiving)entity).setAttackTarget(null);
					}
				}
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityReplacementClone.Jutsu.Hook());
		MinecraftForge.EVENT_BUS.register(new HidingWithCamouflage.Hook());
	}
}
