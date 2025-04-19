
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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.*;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.potion.PotionParalysis;
import net.narutomod.Chakra;

import javax.annotation.Nullable;
import java.util.List;
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
	public static final ItemJutsu.JutsuEnum AMENOTEJIKARA = new ItemJutsu.JutsuEnum(4, "item.ninjutsu.amenotejikara", 'S', 50d, new Amenotejikara());
	public static final ItemJutsu.JutsuEnum PUPPET = new ItemJutsu.JutsuEnum(5, "tooltip.ninjutsu.puppetjutsu", 'C', 0.5d, new EntityPuppet.Base.Jutsu());
	public static final ItemJutsu.JutsuEnum BUGSWARM = new ItemJutsu.JutsuEnum(6, "bugball", 'C', 100d, new EntityKikaichu.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum INVISABILITY = new ItemJutsu.JutsuEnum(7, "tooltip.ninjutsu.hidingincamouflage", 'A', 100d, new HidingWithCamouflage());
	public static final ItemJutsu.JutsuEnum TRANSFORM = new ItemJutsu.JutsuEnum(8, "transformation_jutsu", 'D', 50d, new EntityTransformationJutsu.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum HIRAISHIN = new ItemJutsu.JutsuEnum(9, "hiraishin", 'S', 10d, new EntityHiraishin.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SHIKIGAMI = new ItemJutsu.JutsuEnum(10, "shikigami", 'B', 50d, new EntityShikigami.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum MULTICLONE = new ItemJutsu.JutsuEnum(11, "kage_bunshin_multi", 'A', new EntityKageBunshin.EC.Jutsu2());

	public ItemNinjutsu(ElementsNarutomodMod instance) {
		super(instance, 377);
	}
	
	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(REPLACEMENT, KAGEBUNSHIN, RASENGAN, LIMBOCLONE, AMENOTEJIKARA, PUPPET, BUGSWARM, INVISABILITY, TRANSFORM, HIRAISHIN, SHIKIGAMI, MULTICLONE));
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
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ActionResult<ItemStack> ares = super.onItemRightClick(world, entity, hand);
			ItemStack stack = entity.getHeldItem(hand);
			if (!world.isRemote && ares.getType() == EnumActionResult.SUCCESS && this.getCurrentJutsu(stack) == AMENOTEJIKARA) {
				Amenotejikara.setTarget(stack, ProcedureUtils.objectEntityLookingAt(entity, 40d).entityHit);
			}
			return ares;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity.ticksExisted % 20 == 0
			 && entity instanceof EntityLivingBase && INVISABILITY.jutsu.isActivated(itemstack)) {
				if (Chakra.pathway((EntityLivingBase)entity).consume(INVISABILITY.chakraUsage * 0.2d)) {
					((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 21, 0, false, false));
				} else {
					HidingWithCamouflage.deactivate(itemstack);
				}
			}
		}

		@Override
		public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
			if (attacker.equals(target)) {
				target = ProcedureUtils.objectEntityLookingAt(attacker, 50d, 3d).entityHit;
			}
			if (target instanceof EntityLivingBase) {
				attacker.setRevengeTarget((EntityLivingBase)target);
			}
			return super.onLeftClickEntity(itemstack, attacker, target);
		}
	}

	public static class EntityReplacementClone extends EntityClone.Base implements ItemJutsu.IJutsu {
		protected int lifeSpan = 40;
		
		public EntityReplacementClone(World world) {
			super(world);
			this.setNoAI(true);
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}

		public EntityReplacementClone(EntityLivingBase player, Entity attacker) {
			super(player);
			List<BlockPos> list = ProcedureUtils.getAllAirBlocks(player.world, attacker.getEntityBoundingBox().grow(8));
			list.sort(new ProcedureUtils.BlockposSorter(player.getPosition()));
			for (int i = list.size() - 1; i >= 0; --i) {
				BlockPos pos = list.get(i);
				Vec3d vec = new Vec3d(0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ());
				if (attacker.getDistance(vec.x, vec.y, vec.z) <= 8d && player.world.isAirBlock(pos.up())
				 && (player.world.getBlockState(pos.down()).isTopSolid() || (!player.onGround && !attacker.onGround))
				 && player.world.rayTraceBlocks(vec.addVector(0d, player.getEyeHeight(), 0d), attacker.getPositionEyes(1f), false, true, false) == null) {
					float angle = MathHelper.wrapDegrees(ProcedureUtils.getYawFromVec(vec.subtract(attacker.getPositionVector())) - ProcedureUtils.getYawFromVec(player.getPositionVector().subtract(attacker.getPositionVector()))); 
					if (angle > 135.0f || angle < -135.0f) {
						player.rotationYaw = ProcedureUtils.getYawFromVec(attacker.getPositionVector().subtract(vec));
						player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 5, 0, false, false));
						player.setInvisible(true);
						player.setPositionAndUpdate(vec.x, vec.y, vec.z);
						break;
					}
				}
			}
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}
		
		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.NINJUTSU;
		}

		protected void onSetDead() {
			this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvent.REGISTRY
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
		public void setDead() {
			super.setDead();
			this.onSetDead();
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.ticksExisted > this.lifeSpan) {
				this.setDead();
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String JUTSULASTUSEKEY = "ReplacementJutsuLastUse";
			private static final int COOLDOWN = 200;

			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (!stack.hasTagCompound()) {
					stack.setTagCompound(new NBTTagCompound());
				}
				boolean flag = stack.getTagCompound().hasKey(JUTSULASTUSEKEY);
				if (!flag) {
					stack.getTagCompound().setLong(JUTSULASTUSEKEY, entity.world.getTotalWorldTime());
				} else {
					stack.getTagCompound().removeTag(JUTSULASTUSEKEY);
				}
				if (entity instanceof EntityPlayer && !entity.world.isRemote) {
					((EntityPlayer)entity).sendStatusMessage(new TextComponentString(flag ? "Off" : "On"), true);
				}
				return false;
			}
			
			@Override
			public boolean isActivated(ItemStack stack) {
				return stack.hasTagCompound() ? stack.getTagCompound().hasKey(JUTSULASTUSEKEY) : false;
			}

			public static class Hook {
				@SubscribeEvent
				public void onAttacked(LivingHurtEvent event) {
					EntityLivingBase entity = event.getEntityLiving();
					Entity attacker = event.getSource().getTrueSource();
					if (entity instanceof EntityPlayer && !entity.world.isRemote && !entity.isPotionActive(PotionParalysis.potion)
					 && event.getSource() != DamageSource.OUT_OF_WORLD && attacker instanceof EntityLivingBase && !attacker.equals(entity)) {
						ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, block);
						if (stack != null && REPLACEMENT.jutsu.isActivated(stack)) {
							long l = entity.world.getTotalWorldTime();
							if (l > stack.getTagCompound().getLong(JUTSULASTUSEKEY) + COOLDOWN 
							 && Chakra.pathway(entity).consume(REPLACEMENT.chakraUsage)) {
								event.setCanceled(true);
								stack.getTagCompound().setLong(JUTSULASTUSEKEY, l);
								ProcedureOnLivingUpdate.setUntargetable(entity, 5);
								EntityReplacementClone clone = new EntityReplacementClone(entity, attacker);
								entity.world.spawnEntity(clone);
								clone.attackEntityFrom(event.getSource(), event.getAmount());
							}
						}
					}
				}
			}
		}
	}

	public static class HidingWithCamouflage implements ItemJutsu.IJutsuCallback {
		private static final String JUTSUACTIVEKEY = "HidingWithCamouflageActive";
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			boolean flag = entity instanceof EntityPlayer ? this.isActivated(stack) : false;
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

	public static class Amenotejikara implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(entity, 40d);
			if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos pos = entity.world.isAirBlock(rtr.getBlockPos().up()) && entity.world.isAirBlock(rtr.getBlockPos().up(2))
				 ? rtr.getBlockPos().up() : rtr.getBlockPos().offset(rtr.sideHit);
				Entity target = this.getTarget(stack, entity.world);
				if (target == null) {
					target = entity;
				}
				ProcedureOnLivingUpdate.setUntargetable(target, 10);
				entity.world.playSound(null, 0.5d + pos.getX(), pos.getY(), 0.5d + pos.getZ(), SoundEvent.REGISTRY
				  .getObject(new ResourceLocation("narutomod:swoosh")), SoundCategory.NEUTRAL, 0.8f, entity.getRNG().nextFloat() * 0.4f + 0.8f);
				target.setPositionAndUpdate(0.5d + pos.getX(), pos.getY(), 0.5d + pos.getZ());
				setTarget(stack, null);
				return true;
			} else if (rtr.entityHit != null) {
				Entity target = this.getTarget(stack, entity.world);
				if (target == null || target.equals(rtr.entityHit)) {
					target = entity;
				}
				double x = target.posX;
				double y = target.posY;
				double z = target.posZ;
				ProcedureOnLivingUpdate.setUntargetable(target, 10);
				ProcedureOnLivingUpdate.setUntargetable(rtr.entityHit, 10);
				entity.world.playSound(null, x, y, z, SoundEvent.REGISTRY
				  .getObject(new ResourceLocation("narutomod:swoosh")), SoundCategory.NEUTRAL, 0.8f, entity.getRNG().nextFloat() * 0.4f + 0.8f);
				entity.world.playSound(null, rtr.entityHit.posX, rtr.entityHit.posY, rtr.entityHit.posZ, SoundEvent.REGISTRY
				  .getObject(new ResourceLocation("narutomod:swoosh")), SoundCategory.NEUTRAL, 0.8f, entity.getRNG().nextFloat() * 0.4f + 0.8f);
				target.setPositionAndUpdate(rtr.entityHit.posX, rtr.entityHit.posY, rtr.entityHit.posZ);
				rtr.entityHit.setPositionAndUpdate(x, y, z);
				setTarget(stack, null);
				return true;
			}
			setTarget(stack, null);
			return false;
		}

		public static void setTarget(ItemStack stack, @Nullable Entity entity) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			if (entity == null) {
				stack.getTagCompound().removeTag("amenotejikaraTarget");
			} else {
				stack.getTagCompound().setInteger("amenotejikaraTarget", entity.getEntityId());
			}
		}

		@Nullable
		private Entity getTarget(ItemStack stack, World world) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("amenotejikaraTarget")) {
				return world.getEntityByID(stack.getTagCompound().getInteger("amenotejikaraTarget"));
			}
			return null;
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityReplacementClone.Jutsu.Hook());
		MinecraftForge.EVENT_BUS.register(new HidingWithCamouflage.Hook());
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
	}
}
