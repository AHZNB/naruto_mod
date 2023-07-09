package net.narutomod.procedure;

import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;
import net.minecraft.init.Items;

import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemJutsu;
import net.narutomod.PlayerTracker;
import net.narutomod.PlayerRender;
import net.narutomod.Particles;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.base.Predicates;
import com.google.common.base.Predicate;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureUtils extends ElementsNarutomodMod.ModElement {
	//public static final net.minecraft.entity.ai.attributes.IAttribute REACH_DISTANCE = 
	//  new RangedAttribute(null, "modded.reachDistance", 7.0, 0.0, 128.0).setShouldWatch(true);
	public static final IAttribute MAXHEALTH = (new RangedAttribute(null, "modded.maxHealth", 20.0D, Float.MIN_VALUE, 1048576.0D)).setDescription("Max Modded Health").setShouldWatch(true);
	private static final Random RNG = new Random();
	public static final DamageSource AMATERASU = new DamageSource(ItemJutsu.NINJUTSU_TYPE).setFireDamage();
	public static final DamageSource SPECIAL_DAMAGE = new DamageSource("wither").setDamageBypassesArmor().setDamageIsAbsolute();
	
	public ProcedureUtils(ElementsNarutomodMod instance) {
		super(instance, 177);
	}

	public static double rngGaussian() {
		return RNG.nextGaussian();
	}

	public static boolean rngBoolean() {
		return RNG.nextBoolean();
	}

	public static int rngInt(int bound) {
		return RNG.nextInt(bound);
	}

	public static Random rng() {
		return RNG;
	}

	public static double name2Id(String string) {
		long id = 0L;
		for (int i = 0; i < string.length() - 2 && i < 8; ++i) {
			id = id << 8 | (long) string.charAt(i);
		}
		return id;
	}

	@Nullable
	public static EntityLivingBase searchLivingMatchingId(UUID id) {
		/*MinecraftServer mcserv = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (mcserv != null) {
			for (int i = 0; i < mcserv.getPlayerList().getPlayers().size(); ++i) {
				EntityPlayerMP player = (EntityPlayerMP) mcserv.getPlayerList().getPlayers().get(i);
				if (name2Id(player.getDisplayName().getFormattedText()) != id)
					continue;
				return player;
			}
		}*/
		MinecraftServer mcserv = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (mcserv != null) {
			Entity entity = mcserv.getEntityFromUuid(id);
			if (entity instanceof EntityLivingBase) {
				return (EntityLivingBase)entity;
			}
		}
		return null;
	}

	@Nullable
	public static EntityPlayerMP getPlayerMatchingUuid(UUID id) {
		MinecraftServer mcserv = FMLCommonHandler.instance().getMinecraftServerInstance();
		return mcserv != null ? mcserv.getPlayerList().getPlayerByUUID(id) : null;
	}

	@Nullable
	public static Entity getEntityFromUUID(World world, UUID uuid) {
		if (world instanceof WorldServer) {
			return ((WorldServer)world).getEntityFromUuid(uuid);
		}
        for (Entity entity : world.loadedEntityList) {
            if (uuid.equals(entity.getUniqueID())) {
                return entity;
            }
        }
        return null;
	}

	@Nullable
	public static EntityLivingBase GetLivingByUuid(World world, UUID uuid) {
		for (EntityLivingBase entity : world.getEntities(EntityLivingBase.class, EntitySelectors.NOT_SPECTATING)) {
			if (entity.getUniqueID().equals(uuid)) {
				return entity;
			}
		}
		return null;
	}

	public static void setOriginalOwner(ItemStack stack, UUID uuid) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setUniqueId("player_id", uuid);
	}

	public static void setOriginalOwner(EntityLivingBase entity, ItemStack stack) {
		setOriginalOwner(stack, entity.getUniqueID());
	}

	public static UUID getOwnerId(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasUniqueId("player_id") 
		 ? stack.getTagCompound().getUniqueId("player_id") : null;
	}

	public static boolean isOriginalOwner(EntityLivingBase entity, ItemStack stack) {
		//return getOwnerId(stack) == name2Id(entity.getDisplayName().getFormattedText());
		return entity.getUniqueID().equals(getOwnerId(stack));
	}

	public static ItemStack getMatchingItemStack(EntityPlayer entity, Item item) {
		return getItemStackIgnoreDurability(entity.inventory, new ItemStack(item));
	}
	
	public static ItemStack getItemStackIgnoreDurability(InventoryPlayer inventory, ItemStack itemStackIn) {
		List<NonNullList<ItemStack>> allInv = Arrays.<NonNullList<ItemStack>>asList(inventory.mainInventory, inventory.armorInventory,
				inventory.offHandInventory);
		for (List<ItemStack> list : allInv) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				ItemStack itemstack = (ItemStack) iterator.next();
				//if (!itemstack.isEmpty() && itemstack.isItemEqualIgnoreDurability(itemStackIn))
				if (!itemstack.isEmpty() && itemstack.getItem() == itemStackIn.getItem()) {
					return itemstack;
				}
			}
		}
		return null;
	}

	public static boolean hasItemInMainInventory(EntityPlayer player, Item itemIn) {
		return getItemInMainInventory(player, itemIn) != null;
	}

	@Nullable
	public static ItemStack getItemInMainInventory(EntityPlayer player, Item itemIn) {
		for (ItemStack stack : player.inventory.mainInventory) {
			if (!stack.isEmpty() && stack.getItem() == itemIn) {
				return stack;
			}
		}
		return null;
	}

	public static ItemStack getMatchingItemStack(EntityPlayer player, ItemStack itemStackIn) {
		List<NonNullList<ItemStack>> allInv = Arrays.<NonNullList<ItemStack>>asList(player.inventory.mainInventory,
		 player.inventory.armorInventory, player.inventory.offHandInventory);
		for (List<ItemStack> list : allInv) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				ItemStack itemstack = (ItemStack) iterator.next();
				if (!itemstack.isEmpty() && ItemStack.areItemStacksEqual(itemstack, itemStackIn)) {
					return itemstack;
				}
			}
		}
		return null;
	}

	public static boolean hasAnyItemOfSubtype(EntityPlayer player, Class<? extends Item> itemType) {
		List<NonNullList<ItemStack>> allInv = Arrays.<NonNullList<ItemStack>>asList(player.inventory.mainInventory, 
		 player.inventory.armorInventory, player.inventory.offHandInventory);
		for (List<ItemStack> list : allInv) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				ItemStack itemstack = (ItemStack) iterator.next();
				if (!itemstack.isEmpty() && itemType.isAssignableFrom(itemstack.getItem().getClass())) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<ItemStack> getAllItemsOfSubType(EntityPlayer player, Class<? extends Item> itemType) {
		List<NonNullList<ItemStack>> allInv = Arrays.<NonNullList<ItemStack>>asList(player.inventory.mainInventory, 
		 player.inventory.armorInventory, player.inventory.offHandInventory);
		List<ItemStack> itemlist = Lists.newArrayList();
		for (List<ItemStack> list : allInv) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				ItemStack itemstack = (ItemStack) iterator.next();
				if (!itemstack.isEmpty() && itemType.isAssignableFrom(itemstack.getItem().getClass())) {
					itemlist.add(itemstack);
				}
			}
		}
		return itemlist;
	}

	public static boolean hasItemStackIgnoreDurability(InventoryPlayer inventory, ItemStack itemStackIn) {
		return getItemStackIgnoreDurability(inventory, itemStackIn) != null;
	}

	public static boolean hasItemInInventory(EntityPlayer player, Item item) {
		return hasItemStackIgnoreDurability(player.inventory, new ItemStack(item));
	}

	private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() 
		 && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) 
		 && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	public static int getSlotFor(ItemStack stack, EntityPlayer player) {
		int i = 0;
		for (ItemStack stack1 : player.inventory.mainInventory) {
			if (!stack1.isEmpty() && stackEqualExact(stack, stack1)) {
				return i;
			}
			++i;
		}
		for (ItemStack stack1 : player.inventory.armorInventory) {
			if (!stack1.isEmpty() && stackEqualExact(stack, stack1)) {
				return i;
			}
			++i;
		}
		for (ItemStack stack1 : player.inventory.offHandInventory) {
			if (!stack1.isEmpty() && stackEqualExact(stack, stack1)) {
				return i;
			}
			++i;
		}
		return -1;
	}

	public static int getAvailableSlotsInMainInventory(EntityPlayer player) {
		int i = 0;
		for (ItemStack stack1 : player.inventory.mainInventory) {
			if (stack1.isEmpty()) {
				++i;
			}
		}
		return i;
	}

	public static boolean attackEntityAsMob(EntityLivingBase attacker, Entity entityIn) {
		int i = 0;
		float f = (float) getModifiedAttackDamage(attacker);
		if (entityIn instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(attacker.getHeldItemMainhand(), ((EntityLivingBase) entityIn).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(attacker);
		}
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(attacker), f);
		if (flag) {
			if (i > 0 && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn).knockBack(attacker, (float) i * 0.5F, (double) MathHelper.sin(attacker.rotationYaw * 0.017453292F),
						(double) (-MathHelper.cos(attacker.rotationYaw * 0.017453292F)));
				attacker.motionX *= 0.6D;
				attacker.motionZ *= 0.6D;
			}
			int j = EnchantmentHelper.getFireAspectModifier(attacker);
			if (j > 0)
				entityIn.setFire(j * 4);
			if (entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entityIn;
				ItemStack itemstack = attacker.getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
				if (!itemstack.isEmpty() && !itemstack1.isEmpty()
						&& itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, attacker)
						&& itemstack1.getItem().isShield(itemstack1, entityplayer)) {
					float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(attacker) * 0.05F;
					if (RNG.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
						attacker.world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}
			if (entityIn instanceof EntityLivingBase) {
				ItemStack itemstack1 = attacker.getHeldItemMainhand();
				if (!itemstack1.isEmpty()) {
					itemstack1.getItem().hitEntity(itemstack1, (EntityLivingBase)entityIn, attacker);
				}
				EnchantmentHelper.applyThornEnchantments((EntityLivingBase) entityIn, attacker);
			}
			EnchantmentHelper.applyArthropodEnchantments(attacker, entityIn);
			attacker.setLastAttackedEntity(entityIn);
		}
		return flag;
	}

	public static Vec3d getMotion(Entity entity) {
		return new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
	}

	public static double getVelocity(Entity entity) {
		return getMotion(entity).lengthVector();
	}

	public static void setVelocity(Entity target, double motionX, double motionY, double motionZ) {
		target.velocityChanged = target.motionX != motionX || target.motionY != motionY || target.motionZ != motionZ;
		target.motionX = motionX;
		target.motionY = motionY;
		target.motionZ = motionZ;
	}

	public static void addVelocity(Entity target, Vec3d vec) {
		setVelocity(target, target.motionX + vec.x, target.motionY + vec.y, target.motionZ + vec.z);
	}

	public static void multiplyVelocity(Entity target, double mul) {
		setVelocity(target, target.motionX * mul, target.motionY * mul, target.motionZ * mul);
	}

	public static void multiplyVelocity(Entity target, double mulX, double mulY, double mulZ) {
		setVelocity(target, target.motionX * mulX, target.motionY * mulY, target.motionZ * mulZ);
	}

	public static Vec3d pushEntity(Entity attacker, Entity target, double range, float multiplier) {
		return pushEntity(attacker.getPositionVector(), target, range, multiplier);
	}

	public static Vec3d pushEntity(Vec3d src, Entity target, double range, float multiplier) {
		Vec3d vec3d = target.getPositionVector().subtract(src);
		if (vec3d.lengthVector() > range) {
			return Vec3d.ZERO;
		}
		multiplier *= (float)(range - vec3d.lengthVector()) * 0.1f;
		double d = Math.sqrt(2d / (target instanceof EntityLivingBase ? target.height : target.getEntityBoundingBox().getAverageEdgeLength()));
		vec3d = vec3d.normalize().scale(d);
		if (target.onGround && vec3d.y < d * 0.6d)
			vec3d = vec3d.addVector(0d, d * 0.6d, 0d);
		vec3d = vec3d.scale(multiplier).addVector(target.motionX, target.motionY, target.motionZ);
		setVelocity(target, vec3d.x, vec3d.y, vec3d.z);
		return vec3d;
	}

	public static void pullEntity(Vec3d src, Entity target, float multiplier) {
		//Vec3d vec3d = new Vec3d(target.posX - src.x, target.posY - src.y, target.posZ - src.z);
		Vec3d vec3d = src.subtract(target.getPositionVector());
		multiplier *= (float) vec3d.lengthVector() * 0.1f;
		vec3d = vec3d.normalize().scale(multiplier);
		setVelocity(target, vec3d.x, vec3d.y, vec3d.z);
	}

	public static void removeAllEnchantments(ItemStack stack) {
		if (!stack.isEmpty()) {
			NBTTagList nbttaglist = stack.getEnchantmentTagList();
			if (!nbttaglist.hasNoTags()) {
				for (int i = 0; i < nbttaglist.tagCount(); ++i) {
					nbttaglist.removeTag(i);
				}
			}
			if (stack.hasTagCompound()) {
				stack.getTagCompound().removeTag("ench");
			}
		}
	}

	public static boolean purgeHarmfulEffects(EntityLivingBase entity) {
		List<PotionEffect> list = Lists.newArrayList();
		for (PotionEffect effect : entity.getActivePotionEffects()) {
			if (effect.getPotion().isBadEffect())
				list.add(effect);
		}
		for (PotionEffect effect : list) {
			entity.removePotionEffect(effect.getPotion());
		}
		return list.isEmpty();
	}

	public static UUID getUniqueId(ItemStack stack, String key) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound().hasUniqueId(key) ? stack.getTagCompound().getUniqueId(key) : null;
	}

	public static void removeUniqueIdTag(ItemStack stack, String key) {
		if (stack.hasTagCompound()) {
			stack.getTagCompound().removeTag(key + "Most");
			stack.getTagCompound().removeTag(key + "Least");
		}
	}

	public static void swapItemToSlot(EntityPlayer entity, EntityEquipmentSlot slot, ItemStack itemstack) {
		ItemStack itemstack1 = entity.getItemStackFromSlot(slot);
		//ItemStack itemstack2 = getItemStackIgnoreDurability(entity.inventory, itemstack);
		ItemStack itemstack2 = getMatchingItemStack(entity, itemstack);
		if (itemstack2 != null && !itemstack2.isEmpty()) {
			itemstack = itemstack2.copy();
			itemstack2.shrink(1);
		}
		if (!itemstack1.isEmpty()) {
			//if (itemstack1.getItem() == itemstack.getItem()) return;
			if (ItemStack.areItemStacksEqual(itemstack, itemstack1))
				return;
			if (itemstack1.getMaxStackSize() > 1) {
				ItemHandlerHelper.giveItemToPlayer(entity, itemstack1);
			} else {
				entity.addItemStackToInventory(itemstack1);
			}
			entity.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(slot));
		}
		entity.setItemStackToSlot(slot, itemstack);
	}

	public static boolean isWearingAnySharingan(EntityLivingBase entity) {
		return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == ItemSharingan.helmet || isWearingMangekyo(entity);
	}

	public static boolean isWearingMangekyo(EntityLivingBase entity) {
		Item item = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem();
		return item == ItemMangekyoSharingan.helmet || item == ItemMangekyoSharinganObito.helmet || item == ItemMangekyoSharinganEternal.helmet;
	}

	public static boolean isEntityInFOV(EntityLivingBase looker, Entity entityIn) {
		double yaw = -MathHelper.atan2(entityIn.posX - looker.posX, entityIn.posZ - looker.posZ) * (180d / Math.PI);
		return Math.abs(MathHelper.wrapDegrees(yaw - looker.rotationYawHead)) < 85d && looker.canEntityBeSeen(entityIn);
	}
	
	public static RayTraceResult raytraceBlocks(Entity entity, double distance) {
		Vec3d vec1 = entity.getPositionEyes(1f);
		Vec3d vec2 = vec1.add(entity.getLookVec().scale(distance));
		return entity.world.rayTraceBlocks(vec1, vec2, false, false, true);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range) {
		return objectEntityLookingAt(entity, range, false, false, (Predicate)null);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, double bbGrow) {
		return objectEntityLookingAt(entity, range, bbGrow, false, false, (Predicate)null);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, double bbGrow, boolean trackall) {
		return objectEntityLookingAt(entity, range, bbGrow, trackall, false, (Predicate)null);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, @Nullable Entity excludeEntity) {
		return objectEntityLookingAt(entity, range, false, false, excludeEntity);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, double bbgrow, @Nullable Entity excludeEntity) {
		return objectEntityLookingAt(entity, range, bbgrow, false, false, excludeEntity);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, double bbgrow, Class <? extends Entity> excludeClazz) {
		return objectEntityLookingAt(entity, range, bbgrow, false, false, excludeClazz);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, boolean trackall) {
		return objectEntityLookingAt(entity, range, trackall, false, (Predicate)null);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, boolean trackall, boolean stopOnLiquid) {
		return objectEntityLookingAt(entity, range, trackall, stopOnLiquid, (Predicate)null);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, boolean trackall, boolean stopOnLiquid, @Nullable Entity excludeEntity) {
		return objectEntityLookingAt(entity, range, 0.0d, trackall, stopOnLiquid, excludeEntity);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, double bbgrow, boolean trackall, boolean stopOnLiquid, @Nullable Entity excludeEntity) {
		return objectEntityLookingAt(entity, range, bbgrow, trackall, stopOnLiquid, new Predicate<Entity>() {
			public boolean apply(@Nullable Entity p_apply_1_) {
				return p_apply_1_ != null && !p_apply_1_.equals(excludeEntity);
			}
		});
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, double bbgrow, boolean trackall, boolean stopOnLiquid, Class <? extends Entity> excludeClazz) {
		return objectEntityLookingAt(entity, range, bbgrow, trackall, stopOnLiquid, new Predicate<Entity>() {
			public boolean apply(@Nullable Entity p_apply_1_) {
				return p_apply_1_ != null && !excludeClazz.isAssignableFrom(p_apply_1_.getClass());
			}
		});
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, boolean trackall, boolean stopOnLiquid, @Nullable Predicate<Entity> filter) {
		return objectEntityLookingAt(entity, range, 0.0d, trackall, stopOnLiquid, filter);
	}

	public static RayTraceResult objectEntityLookingAt(Entity entity, double range, double bbGrow, boolean trackall, boolean stopOnLiquid, @Nullable Predicate<Entity> filter) {
		double d0 = range;
		double d1 = d0;
		Vec3d vec3d = entity.getPositionEyes(1f);
		Vec3d vec3d1 = entity.getLookVec().scale(d0);
		Vec3d vec3d2 = vec3d.add(vec3d1);
		RayTraceResult objectMouseOver = entity.world.rayTraceBlocks(vec3d, vec3d2, stopOnLiquid, false, true);
		if (objectMouseOver != null) {
			d1 = objectMouseOver.hitVec.distanceTo(vec3d);
		}
		List<Entity> list = entity.world.getEntitiesInAABBexcluding(entity,
				entity.getEntityBoundingBox().expand(vec3d1.x, vec3d1.y, vec3d1.z).grow(1.0D),
				Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
					public boolean apply(@Nullable Entity p_apply_1_) {
						return p_apply_1_ != null 
						 && (trackall || (p_apply_1_.canBeCollidedWith() && !p_apply_1_.noClip)) 
						 && (filter == null || filter.apply(p_apply_1_));
					}
				}));
		double d2 = d1;
		RayTraceResult entityTrace = null;
		for (int j = 0; j < list.size(); ++j) {
			Entity entity1 = list.get(j);
			if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity())
				continue;
			AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(bbGrow * vec3d.distanceTo(entity1.getPositionVector()) / 32d);
			RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);
			if (axisalignedbb.contains(vec3d)) {
				if (d2 >= 0.0) {
					entityTrace = raytraceresult != null ? raytraceresult : new RayTraceResult(vec3d, EnumFacing.UP, null);
					entityTrace.typeOfHit = RayTraceResult.Type.ENTITY;
					entityTrace.entityHit = entity1;
					d2 = 0.0;
				}
			} else if (raytraceresult != null) {
				double d3 = vec3d.distanceTo(raytraceresult.hitVec);
				if (d3 < d2 || d2 == 0.0) {
					entityTrace = raytraceresult;
					entityTrace.typeOfHit = RayTraceResult.Type.ENTITY;
					entityTrace.entityHit = entity1;
					d2 = d3;
				}
			}
		}
		if (entityTrace != null && (d2 < d1 || objectMouseOver == null)) {
			objectMouseOver = entityTrace;
		}
		return objectMouseOver;
	}

	public static EntityItem breakBlockAndDropWithChance(World world, BlockPos pos, float hardnessLimit, float breakChance, float dropChance) {
		return breakBlockAndDropWithChance(world, pos, hardnessLimit, breakChance, dropChance, true);
	}

	@Nullable
	public static EntityItem breakBlockAndDropWithChance(World world, BlockPos pos, float hardnessLimit, float breakChance, float dropChance, boolean sound) {
		EntityItem entityToSpawn = null;
		IBlockState blockstate = world.getBlockState(pos);
		float blockHardness = blockstate.getBlockHardness(world, pos);
		if (!world.isAirBlock(pos) && blockHardness >= 0.0f && blockHardness <= hardnessLimit && RNG.nextFloat() <= breakChance) {
			if (sound) {
				SoundType type = blockstate.getBlock().getSoundType();
				world.playSound(null, pos, type.getBreakSound(), SoundCategory.BLOCKS, (type.getVolume() + 1.0f) / 2.0f, type.getPitch() * 0.8f);
			}
			if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, null)) {
				world.setBlockToAir(pos);
				if (!world.isRemote && Math.random() <= (double) dropChance) {
					Item item = blockstate.getBlock().getItemDropped(blockstate, RNG, 0);
					if (item != Items.AIR) {
						entityToSpawn = new EntityItem(world, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), new ItemStack(item, 1));
						entityToSpawn.setDefaultPickupDelay();
						world.spawnEntity(entityToSpawn);
					}
				}
			}
			if (world instanceof WorldServer) {
				((WorldServer)world).spawnParticle(EnumParticleTypes.BLOCK_DUST, 0.5D+pos.getX(), 0.5D+pos.getY(), 0.5D+pos.getZ(),
				 8, 0.2d, 0.2d, 0.2d, 0.15d, Block.getIdFromBlock(blockstate.getBlock()));
			}
		}
		return entityToSpawn;
	}

	public static <T extends Entity> T findNearestSensibleEntityWithinAABB(World world, Class<? extends T> entityType, AxisAlignedBB aabb,
			EntityLivingBase closestTo) {
		return findNearestEntityWithinAABB(world, entityType, aabb, closestTo, 
		 Predicates.and(EntitySelectors.CAN_AI_TARGET, new Predicate<Entity>() {
			public boolean apply(@Nullable Entity p_apply_1_) {
				return p_apply_1_ != null && p_apply_1_.isEntityAlive() && p_apply_1_.canBeCollidedWith() && closestTo.canEntityBeSeen(p_apply_1_);
			}
		}));
	}

	public static <T extends Entity> T findNearestEntityWithinAABB(World world, Class<? extends T> entityType, AxisAlignedBB aabb,
			Entity closestTo, @Nullable Predicate<? super T> filter) {
		List<T> list = world.<T>getEntitiesWithinAABB(entityType, aabb, filter);
		T t = null;
		double d0 = Double.MAX_VALUE;
		for (int j2 = 0; j2 < list.size(); ++j2) {
			T t1 = list.get(j2);
			if (t1 != closestTo && EntitySelectors.NOT_SPECTATING.apply(t1)) {
				double d2 = closestTo.getDistanceSq(t1);
				if (d2 <= d0) {
					t = t1;
					d0 = d2;
				}
			}
		}
		return t;
	}

	public static List<BlockPos> getBlocksOfType(World world, AxisAlignedBB bb, Block block) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
        int j2 = MathHelper.floor(bb.minX);
        int k2 = MathHelper.ceil(bb.maxX);
        int l2 = MathHelper.floor(bb.minY);
        int i3 = MathHelper.ceil(bb.maxY);
        int j3 = MathHelper.floor(bb.minZ);
        int k3 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos mutableblockpos = BlockPos.PooledMutableBlockPos.retain();
        for (int l3 = j2; l3 < k2; ++l3) {
            for (int i4 = l2; i4 < i3; ++i4) {
                for (int j4 = j3; j4 < k3; ++j4) {
					if (world.getBlockState(mutableblockpos.setPos(l3, i4, j4)).getBlock() == block) {
						list.add(mutableblockpos.toImmutable());
					}
                }
            }
        }
        mutableblockpos.release();
        return list;
	}

	public static List<BlockPos> getNonAirBlocks(World world, AxisAlignedBB bb, float hardnessLimit, boolean hasCollisionbox) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
        int j2 = MathHelper.floor(bb.minX);
        int k2 = MathHelper.ceil(bb.maxX);
        int l2 = MathHelper.floor(bb.minY);
        int i3 = MathHelper.ceil(bb.maxY);
        int j3 = MathHelper.floor(bb.minZ);
        int k3 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
        for (int l3 = j2; l3 < k2; ++l3) {
            for (int i4 = l2; i4 < i3; ++i4) {
                for (int j4 = j3; j4 < k3; ++j4) {
					IBlockState blockstate = world.getBlockState(pos.setPos(l3, i4, j4));
					float hardness = blockstate.getBlockHardness(world, pos);
					if (!world.isAirBlock(pos) 
					 && (hardnessLimit < 0f || (hardness >= 0f && hardness <= hardnessLimit))
					 && (!hasCollisionbox || blockstate.getCollisionBoundingBox(world, pos) != Block.NULL_AABB)) {
						list.add(pos.toImmutable());
					}
                }
            }
        }
        pos.release();
        return list;
	}

	public static List<BlockPos> getNonAirBlocks(World world, AxisAlignedBB bb, boolean hasCollisionbox) {
		return getNonAirBlocks(world, bb, 1000f, hasCollisionbox);
	}

	public static List<BlockPos> getNonAirBlocks(World world, AxisAlignedBB bb) {
		return getNonAirBlocks(world, bb, 1000f, false);
	}

	// gets all block bounding boxes excpet air, including liquids
	public static List<AxisAlignedBB> getBoundingBoxes(World world, AxisAlignedBB bb) {
		List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
        for (BlockPos pos : getNonAirBlocks(world, bb)) {
        	list.add(world.getBlockState(pos).getBoundingBox(world, pos).offset(pos));
        }
        return list;
	}

	public static List<BlockPos> getAllAirBlocks(World world, AxisAlignedBB bb) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
        int j2 = MathHelper.floor(bb.minX);
        int k2 = MathHelper.ceil(bb.maxX);
        int l2 = MathHelper.floor(bb.minY);
        int i3 = MathHelper.ceil(bb.maxY);
        int j3 = MathHelper.floor(bb.minZ);
        int k3 = MathHelper.ceil(bb.maxZ);
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
        for (int l3 = j2; l3 < k2; ++l3) {
            for (int i4 = l2; i4 < i3; ++i4) {
                for (int j4 = j3; j4 < k3; ++j4) {
					if (world.isAirBlock(pos.setPos(l3, i4, j4))) {
						list.add(pos.toImmutable());
						//list.add(new BlockPos.MutableBlockPos(pos));
					}
                }
            }
        }
        pos.release();
        return list;
	}

	@Nullable
	public static BlockPos getNearestAirBlock(World world, AxisAlignedBB bb, BlockPos closestTo) {
		List<BlockPos> list = getAllAirBlocks(world, bb);
		if (!list.isEmpty()) {
			list.sort(new BlockposSorter(closestTo));
			return list.get(0);
		}
		return null;
	}

	@Nullable
	public static BlockPos getNearestNonAirBlock(World world, AxisAlignedBB bb, BlockPos closestTo, float hardnessLimit, boolean hasCollisionbox) {
		List<BlockPos> list = getNonAirBlocks(world, bb, hardnessLimit, hasCollisionbox);
		if (!list.isEmpty()) {
			list.sort(new BlockposSorter(closestTo));
			return list.get(0);
		}
		return null;
	}

	public static boolean isSpaceOpenToStandOn(EntityLivingBase entity, BlockPos pos) {
		AxisAlignedBB bb = entity.getEntityBoundingBox();
		Vec3d vec = new Vec3d(0.5d+pos.getX(), pos.getY(), 0.5d+pos.getZ()).subtract(entity.posX, bb.minY, entity.posZ);
		return entity.world.getCollisionBoxes(null, bb.contract(0d, -0.1d, 0d).grow(0.5d, 0d, 0.5d).offset(vec)).isEmpty();
	}

	public static String animateString(String string, int type, boolean returnToBlack) {
		int stringLength = string.length();
		if (stringLength < 1) {
			return "";
		}
		String outputString = "";
		if (type == 0) {
			long l = Minecraft.getSystemTime();
			for (int i = 0; i < stringLength; ++i)
				outputString = ((long) i + l / 10L) % 188L == 0L 
				 ? outputString + TextFormatting.WHITE + string.substring(i, i + 1)
				 : ((long) i + l / 10L) % 188L == 1L
				 	? outputString + TextFormatting.YELLOW + string.substring(i, i + 1)
				 	: ((long) i + l / 10L) % 188L == 187L
				 		? outputString + TextFormatting.YELLOW + string.substring(i, i + 1)
				 		: outputString + TextFormatting.GOLD + string.substring(i, i + 1);
		} else if (type == 1)
			outputString = TextFormatting.fromColorIndex((int) (Minecraft.getSystemTime() / 80L % 15L) + (returnToBlack ? 0 : 1)) + string;
		if (returnToBlack)
			return outputString + TextFormatting.BLACK;
		return outputString + TextFormatting.WHITE;
	}

	public static boolean advancementAchieved(EntityPlayerMP player, String advancementName) {
		Advancement adv = ((WorldServer)player.world).getAdvancementManager().getAdvancement(new ResourceLocation(advancementName));
		return adv != null ? player.getAdvancements().getProgress(adv).isDone() : false;
	}

	public static void grantAdvancement(EntityPlayerMP player, String advancementName, boolean playToast) {
		Advancement _adv = ((MinecraftServer) player.mcServer).getAdvancementManager()
				.getAdvancement(new ResourceLocation(advancementName));
		AdvancementProgress _ap = player.getAdvancements().getProgress(_adv);
		if (!_ap.isDone()) {
			Iterator _iterator = _ap.getRemaningCriteria().iterator();
			while (_iterator.hasNext()) {
				String _criterion = (String) _iterator.next();
				player.getAdvancements().grantCriterion(_adv, _criterion);
			}
			if (playToast) {
				player.world.playSound(null, player.posX, player.posY, player.posZ, (SoundEvent) 
				 SoundEvent.REGISTRY.getObject(new ResourceLocation("ui.toast.challenge_complete")), 
				 SoundCategory.NEUTRAL, (float) 1, (float) 1);
			}
		}
	}

	public static boolean isWeapon(ItemStack stack) {
		return !stack.isEmpty() && stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND)
		 .containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
	}

	public static double getFollowRange(EntityLivingBase entity) {
		IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
		return attribute != null ? attribute.getAttributeValue() : 0d;
	}

	public static double getModifiedSpeed(EntityLivingBase entity) {
		IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		return attribute != null ? attribute.getAttributeValue() : 0f;
	}

	public static double getAttackSpeed(EntityLivingBase entity) {
		IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
		return attribute != null ? attribute.getAttributeValue() : 0f;
	}

	public static double getModifiedAttackDamage(EntityLivingBase entity) {
		IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		return attribute != null ? attribute.getAttributeValue() : 0d;
	}

	public static double getPunchDamage(EntityLivingBase entity) {
		IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		if (attribute != null) {
			double d = attribute.getAttributeValue();
			ItemStack stack = entity.getHeldItemMainhand();
			if (isWeapon(stack)) {
				Collection<AttributeModifier> modifiers = stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND)
			 	 .get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
			 	for (AttributeModifier modifier : modifiers) {
					attribute.removeModifier(modifier);
			 	}
				d = attribute.getAttributeValue();
				for (AttributeModifier modifier : modifiers) {
					attribute.applyModifier(modifier);
				}
			}
			return d;
		}
		return 0d;
	}

	public static double getMainhandItemDamage(EntityLivingBase entity) {
		return getModifiedAttackDamage(entity) - getPunchDamage(entity);
	}
	
	public static double getArmorValue(EntityLivingBase entity) {
		IAttributeInstance attribute = entity.getEntityAttribute(SharedMonsterAttributes.ARMOR);
		return attribute != null ? attribute.getAttributeValue() : 0f;
	}

	public static double getReachDistance(EntityLivingBase entity) {
		IAttributeInstance attribute = entity.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
		return attribute != null ? attribute.getAttributeValue() : (entity.width * 2);
	}

	public static double getReachDistanceSq(EntityLivingBase entity) {
		double d = getReachDistance(entity);
		return d * d;
	}

	public static void setDeathAnimations(EntityLivingBase entity, int type, int duration) {
		if ((entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) || !entity.isEntityAlive()) {
			return;
		}
		if (entity.getEntityData().getDouble(NarutomodModVariables.DeathAnimationTime) <= 0.0D) {
			entity.getEntityData().setDouble("deathAnimationType", (double) type);
			entity.getEntityData().setDouble(NarutomodModVariables.DeathAnimationTime, (double) duration);
			if (type == 2 && entity instanceof EntityPlayer) {
				PlayerRender.setColorMultiplier((EntityPlayer)entity, 0x30000000);
			}
		}
	}

	public static void clearDeathAnimations(EntityLivingBase entity) {
		if ((int)entity.getEntityData().getDouble("deathAnimationType") == 2 && entity instanceof EntityPlayer) {
			PlayerRender.setColorMultiplier((EntityPlayer)entity, 0);
		}
		entity.getEntityData().removeTag("deathAnimationType");
		entity.getEntityData().removeTag(NarutomodModVariables.DeathAnimationTime);
	}

	public static Field getFieldByIndex(Class clazz, Class matchClazz, int fieldIndex) {
			//throws IndexOutOfBoundsException, NoSuchFieldException {
		if (clazz == matchClazz) {
			try {
				Field[] fields = clazz.getDeclaredFields();
				if (fields.length > fieldIndex) {
		            fields[fieldIndex].setAccessible(true);
					return fields[fieldIndex];
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			throw new IndexOutOfBoundsException(matchClazz + " does not have " + (fieldIndex+1) + " declared fields");
		} else {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw new RuntimeException("no matching class " + matchClazz);
            } else {
                return getFieldByIndex(superClass, matchClazz, fieldIndex);
            }
		}
	}

	public static <T extends Object> T invokeMethodByParameters(Object parent, Class<? extends T> returnType, Object... params) {
	 //throws IndexOutOfBoundsException, NoSuchMethodException {
		try {
			for (Method method : parent.getClass().getDeclaredMethods()) {
				boolean match = true;
				Class[] clazz1 = method.getParameterTypes();
				if (!method.getReturnType().equals(returnType) || clazz1.length != params.length) {
					match = false;
				} else {
					for (int i = 0; i < clazz1.length; i++) {
						if (!clazz1[i].isAssignableFrom(params[i].getClass())) {
							match = false;
						}
					}
				}
				if (match) {
	            	method.setAccessible(true);
					return (T)method.invoke(parent, params);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		throw new IllegalArgumentException(parent + " does not have methods matching parameters: " + params + ", returns: " + returnType);
	}

	public static void setInvulnerableDimensionChange(EntityPlayerMP player) {
		try {
			Field invulnerableDimensionChangeField = getFieldByIndex(player.getClass(), EntityPlayerMP.class, 25);
			//if (invulnerableDimensionChangeField.getType() == Boolean.class) {
				invulnerableDimensionChangeField.setBoolean(player, true);
			//}
		} catch (Exception e) {
			throw new RuntimeException("Trying to set EntityPlayerMP@invulnerableDimensionChange");
		}
	}

	public static void poofWithSmoke(Entity entity) {
		entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
		 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:poof")), SoundCategory.NEUTRAL, 1f, 1f);
		Particles.spawnParticle(entity.world, Particles.Types.SMOKE, entity.posX, entity.posY+entity.height/2, entity.posZ,
		 300, entity.width * 0.5d, entity.height * 0.3d, entity.width * 0.5d, 0d, 0d, 0d, 0xD0FFFFFF, 30);
	}

	public static float subtractDegreesWrap(float cur, float prev) {
		while (cur - prev < -180.0F) {
			prev -= 360.0F;
		}
		while (cur - prev >= 180.0F) {
			prev += 360.0F;
		}
		return cur - prev;
	}

	public static float interpolateRotation(float prev, float cur, float pt) {
		float f = cur - prev;
		while (f < -180.0F) {
			f += 360.0F;
		}
		while (f >= 180.0F) {
			f -= 360.0F;
		}
		return prev + f * pt;
	}

    public static Vec3d rotateRoll(Vec3d vec3d, float roll) {
        float f = MathHelper.cos(roll);
        float f1 = MathHelper.sin(roll);
        double d0 = vec3d.x * (double)f - vec3d.y * (double)f1;
        double d1 = vec3d.y * (double)f + vec3d.x * (double)f1;
        double d2 = vec3d.z;
        return new Vec3d(d0, d1, d2);
    }

	public static float getYawFromVec(double x, double z) {
		return (float) (-MathHelper.atan2(x, z) * (180d / Math.PI));
	}

	public static float getYawFromVec(Vec3d vec) {
		return (float) (-MathHelper.atan2(vec.x, vec.z) * (180d / Math.PI));
	}

	public static float getPitchFromVec(double x, double y, double z) {
		float f = MathHelper.sqrt(x * x + z * z);
		return (float) (-MathHelper.atan2(y, f) * (180d / Math.PI));
	}

	public static float getPitchFromVec(Vec3d vec) {
		return getPitchFromVec(vec.x, vec.y, vec.z);
	}

	public static Vec2f getYawPitchFromVec(Vec3d vec3d) {
		return new Vec2f(getYawFromVec(vec3d.x, vec3d.z), getPitchFromVec(vec3d.x, vec3d.y, vec3d.z));
	}

	public static double getCDModifier(double modifier) {
		return 1.0d / (0.5d + 0.02d * modifier);
	}
	
	public static double getCooldownModifier(EntityPlayer player) {
		return getCDModifier(PlayerTracker.getNinjaLevel(player));
	}

	public static double modifiedCooldown(double cooldown, EntityPlayer player) {
		return cooldown * getCooldownModifier(player);
	}

	public static boolean isPlayerDisconnected(Entity entity) {
		return entity instanceof EntityPlayerMP && ((EntityPlayerMP)entity).hasDisconnected();
	}

	public static void sendChatAll(String string) {
		MinecraftServer mcserv = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (mcserv != null) {
			mcserv.getPlayerList().sendMessage(new TextComponentString(string));
		}
	}

	public static void sendChat(EntityPlayer player, String string) {
		player.sendMessage(new TextComponentString(string));
	}

	public static void sendMessageToAllNear(String string, double x, double y, double z, double radius, int dimension) {
		MinecraftServer mcserv = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (mcserv != null) {
	        for (int i = 0; i < mcserv.getPlayerList().getCurrentPlayerCount(); ++i) {
	            EntityPlayerMP entityplayermp = mcserv.getPlayerList().getPlayers().get(i);
	            if (entityplayermp.dimension == dimension) {
	                double d0 = x - entityplayermp.posX;
	                double d1 = y - entityplayermp.posY;
	                double d2 = z - entityplayermp.posZ;
	                if (d0 * d0 + d1 * d1 + d2 * d2 < radius * radius) {
	                    entityplayermp.sendMessage(new TextComponentString(string));
	                }
	            }
	        }
		}
	}

    public static class BlockposSorter implements Comparator<BlockPos> {
        private final BlockPos pos;
	
        public BlockposSorter(BlockPos posIn) {
            this.pos = posIn;
        }
	
    	@Override
        public int compare(BlockPos p_compare_1_, BlockPos p_compare_2_) {
            double d0 = this.pos.distanceSq(p_compare_1_);
            double d1 = this.pos.distanceSq(p_compare_2_);
            if (d0 < d1) {
                return -1;
            } else {
                return d0 > d1 ? 1 : 0;
            }
        }
    }

    public static class EntitySorter implements Comparator<Entity> {
        private final double x;
        private final double y;
        private final double z;
	
        public EntitySorter(Entity entityIn) {
        	this(entityIn.posX, entityIn.posY, entityIn.posZ);
        }

        public EntitySorter(double xIn, double yIn, double zIn) {
        	this.x = xIn;
        	this.y = yIn;
        	this.z = zIn;
        }
	
    	@Override
        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = p_compare_1_.getDistanceSq(this.x, this.y, this.z); //this.entity.getDistanceSq(p_compare_1_);
            double d1 = p_compare_2_.getDistanceSq(this.x, this.y, this.z); //this.entity.getDistanceSq(p_compare_2_);
            if (d0 < d1) {
                return -1;
            } else {
                return d0 > d1 ? 1 : 0;
            }
        }
    }

    public static class VecSorter implements Comparator<Vec3d> {
    	private final Vec3d vec;
    	private final boolean reverse;

    	public VecSorter(Vec3d vecIn) {
    		this(vecIn, false);
    	}

    	public VecSorter(Vec3d vecIn, boolean isReversed) {
    		this.vec = vecIn;
    		this.reverse = isReversed;
    	}

    	@Override
    	public int compare(Vec3d vec1, Vec3d vec2) {
    		double d0 = this.vec.squareDistanceTo(vec1);
    		double d1 = this.vec.squareDistanceTo(vec2);
            if (d0 < d1) {
                return this.reverse ? 1 : -1;
            } else {
                return d0 > d1 ? this.reverse ? -1 : 1 : 0;
            }
    	}
    }

    public static class RayTraceResultSorter implements Comparator<RayTraceResult> {
    	private final Vec3d vec;

    	public RayTraceResultSorter(Vec3d vecIn) {
    		this.vec = vecIn;
    	}

    	@Override
    	public int compare(RayTraceResult res1, RayTraceResult res2) {
    		double d0 = this.vec.squareDistanceTo(res1.hitVec);
    		double d1 = this.vec.squareDistanceTo(res2.hitVec);
            if (d0 < d1) {
                return -1;
            } else {
                return d0 > d1 ? 1 : 0;
            }
    	}
    }

    public static class BB {
	    public static double calculateInvXOffset(AxisAlignedBB main, AxisAlignedBB other, double offsetX) {
	        if (other.maxY > main.minY && other.minY < main.maxY && other.maxZ > main.minZ && other.minZ < main.maxZ) {
	            if (offsetX > 0.0D && other.maxX <= main.maxX) {
	                double d1 = main.maxX - other.maxX;
	                if (d1 < offsetX) {
	                    offsetX = d1;
	                }
	            } else if (offsetX < 0.0D && other.minX >= main.minX) {
	                double d0 = main.minX - other.minX;
	                if (d0 > offsetX) {
	                    offsetX = d0;
	                }
	            }
	        }
            return offsetX;
	    }

	    public static double calculateInvYOffset(AxisAlignedBB main, AxisAlignedBB other, double offsetY) {
	        if (other.maxX > main.minX && other.minX < main.maxX && other.maxZ > main.minZ && other.minZ < main.maxZ) {
	            if (offsetY > 0.0D && other.maxY <= main.maxY) {
	                double d1 = main.maxY - other.maxY;
	                if (d1 < offsetY) {
	                    offsetY = d1;
	                }
	            } else if (offsetY < 0.0D && other.minY >= main.minY) {
	                double d0 = main.minY - other.minY;
	                if (d0 > offsetY) {
	                    offsetY = d0;
	                }
	            }
	        }
            return offsetY;
	    }
	
	    public static double calculateInvZOffset(AxisAlignedBB main, AxisAlignedBB other, double offsetZ) {
	        if (other.maxX > main.minX && other.minX < main.maxX && other.maxY > main.minY && other.minY < main.maxY) {
	            if (offsetZ > 0.0D && other.maxZ <= main.maxZ) {
	                double d1 = main.maxZ - other.maxZ;
	                if (d1 < offsetZ) {
	                    offsetZ = d1;
	                }
	            } else if (offsetZ < 0.0D && other.minZ >= main.minZ) {
	                double d0 = main.minZ - other.minZ;
	                if (d0 > offsetZ) {
	                    offsetZ = d0;
	                }
	            }
	        }
            return offsetZ;
	    }

	    public static double getCenterX(AxisAlignedBB aabb) {
	    	return aabb.minX + (aabb.maxX - aabb.minX) * 0.5d;
	    }

	    public static double getCenterY(AxisAlignedBB aabb) {
	    	return aabb.minY + (aabb.maxY - aabb.minY) * 0.5d;
	    }

	    public static double getCenterZ(AxisAlignedBB aabb) {
	    	return aabb.minZ + (aabb.maxZ - aabb.minZ) * 0.5d;
	    }

	    public static Vec3d getCenter(AxisAlignedBB aabb) {
	    	return new Vec3d(getCenterX(aabb), getCenterY(aabb), getCenterZ(aabb));
	    }

	    public static double getVolume(AxisAlignedBB aabb) {
	    	return (aabb.maxX - aabb.minX) * (aabb.maxY - aabb.minY) * (aabb.maxZ - aabb.minZ);
	    }
    }

	public static class CollisionHelper {
		private Entity entity;
		public double dx;
		public double dy;
		public double dz;
		private final List<AxisAlignedBB>[] hitsList = new List[6];
		private final Map<Entity, EnumFacing> entitiesList = Maps.newHashMap();

		public CollisionHelper(Entity entityIn) {
			this.entity = entityIn;
			for (int i = 0; i < this.hitsList.length; i++) {
				this.hitsList[i] = Lists.newArrayList();
			}
		}

		public void collideWithAABBs(List<AxisAlignedBB> list, double x, double y, double z) {
			this.dx = x;
			this.dy = y;
			this.dz = z;
			for (int i = 0; i < this.hitsList.length; i++) {
				this.hitsList[i].clear();
			}
	       	if (x != 0.0D) for (AxisAlignedBB aabb : list) {
	       		double d = aabb.calculateXOffset(this.entity.getEntityBoundingBox(), x);
		    	if (Math.abs(d) < Math.abs(this.dx)) this.dx = d;
		    	if (d != x) this.hitsList[(x > 0d ? EnumFacing.EAST : EnumFacing.WEST).getIndex()].add(aabb);
	       	}
		    if (y != 0.0D) for (AxisAlignedBB aabb : list) {
	       		double d = aabb.calculateYOffset(this.entity.getEntityBoundingBox(), y);
		    	if (Math.abs(d) < Math.abs(this.dy)) this.dy = d;
		    	if (d != y) this.hitsList[(y > 0d ? EnumFacing.UP : EnumFacing.DOWN).getIndex()].add(aabb);
		    }
		    if (z != 0.0D) for (AxisAlignedBB aabb : list) {
	       		double d = aabb.calculateZOffset(this.entity.getEntityBoundingBox(), z);
		    	if (Math.abs(d) < Math.abs(this.dz)) this.dz = d;
		    	if (d != z) this.hitsList[(z > 0d ? EnumFacing.SOUTH : EnumFacing.NORTH).getIndex()].add(aabb);
		    }
		}

		public void collideWithEntities(double x, double y, double z, @Nullable Predicate<Entity > predicate) {
			this.entitiesList.clear();
			Vec3d vec3d = new Vec3d(this.entity.posX, this.entity.posY + this.entity.height / 2, this.entity.posZ);
			Vec3d vec3d2 = vec3d.addVector(x, y, z);
			for (Entity entity1 : this.entity.world.getEntitiesInAABBexcluding(this.entity,
			 this.entity.getEntityBoundingBox().expand(x, y, z), predicate)) {
				if (entity1.canBeCollidedWith() && ItemJutsu.canTarget(entity1)) {
					RayTraceResult res = entity1.getEntityBoundingBox().grow(this.entity.width / 2,
					 this.entity.height / 2, this.entity.width / 2).calculateIntercept(vec3d, vec3d2);
					if (res != null) {
						this.entitiesList.put(entity1, res.sideHit.getOpposite());
					}
				}
			}
		}

		public void collideWithAll(double x, double y, double z, @Nullable Predicate<Entity > predicate) {
			this.collideWithAABBs(this.entity.world.getCollisionBoxes(null, this.entity.getEntityBoundingBox().expand(x, y, z)), x, y, z);
			this.collideWithEntities(this.dx, this.dy, this.dz, predicate);
		}

		public AxisAlignedBB getUpdateEntityBoundingBox() {
			return this.entity.getEntityBoundingBox().offset(this.dx, this.dy, this.dz);
		}

		public Vec3d getUpdatedMotion() {
			return new Vec3d(this.dx, this.dy, this.dz);
		}

		public double minX(double x) {
			return Math.signum(x) != Math.signum(this.dx) ? 0d : Math.abs(x) < Math.abs(this.dx) ? x : this.dx;
		}

		public double minY(double y) {
			return Math.signum(y) != Math.signum(this.dy) ? 0d : Math.abs(y) < Math.abs(this.dy) ? y : this.dy;
		}

		public double minZ(double z) {
			return Math.signum(z) != Math.signum(this.dz) ? 0d : Math.abs(z) < Math.abs(this.dz) ? z : this.dz;
		}

		public double distanceToOrigin() {
			return MathHelper.sqrt(this.dx * this.dx + this.dy * this.dy + this.dz * this.dz);
		}

		@Nullable
		public BlockPos nearestHitOnSide(EnumFacing face) {
			double d = 40000d;
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			for (AxisAlignedBB aabb : this.hitsList[face.getIndex()]) {
				Vec3d vec = BB.getCenter(aabb);
				Vec3i veci = face.getDirectionVec();
				Vec3d vec1 = new Vec3d(vec.x * Math.abs(veci.getX()), vec.y * Math.abs(veci.getY()), vec.z * Math.abs(veci.getZ()));
				Vec3d vec2 = BB.getCenter(this.entity.getEntityBoundingBox());
				Vec3d vec3 = new Vec3d(vec2.x * Math.abs(veci.getX()), vec2.y * Math.abs(veci.getY()), vec2.z * Math.abs(veci.getZ()));
				double d1 = vec1.squareDistanceTo(vec3);
				if (d1 < d) {
					pos.setPos(vec.x, vec.y, vec.z);
					d = d1;
				}
			}
			return d == 40000d ? null : pos.toImmutable();
		}

		public List<BlockPos> hitsOnSide(EnumFacing face) {
			List<BlockPos> newlist = Lists.<BlockPos>newArrayList();
			for (AxisAlignedBB aabb : this.hitsList[face.getIndex()]) {
				newlist.add(new BlockPos(BB.getCenter(aabb)));
			}
			return newlist;
		}

		public boolean hitOnSide(EnumFacing face) {
			return !this.hitsList[face.getIndex()].isEmpty();
		}

		public boolean hitOnAxis(EnumFacing.Axis axis) {
			for (int i = 0; i < this.hitsList.length; i++) {
				if (!this.hitsList[i].isEmpty() && EnumFacing.getFront(i).getAxis() == axis) {
					return true;
				}
			}
			return false;
		}

		public boolean hitOnPlane(EnumFacing.Plane plane) {
			for (int i = 0; i < this.hitsList.length; i++) {
				if (!this.hitsList[i].isEmpty() && EnumFacing.getFront(i).getAxis().getPlane() == plane) {
					return true;
				}
			}
			return false;
		}

		public List<BlockPos> getHitBlocks() {
			List<BlockPos> newlist = Lists.<BlockPos>newArrayList();
			for (List<AxisAlignedBB> list : this.hitsList) {
				for (AxisAlignedBB aabb : list) {
					newlist.add(new BlockPos(BB.getCenter(aabb)));
				}
			}
			return newlist;
		}

		public boolean anyBlockHits() {
			for (List<AxisAlignedBB> list : this.hitsList) {
				if (!list.isEmpty()) {
					return true;
				}
			}
			return false;
		}

		@Nullable
		public BlockPos nearestHit() {
			List<BlockPos> list = this.getHitBlocks();
			if (!list.isEmpty()) {
				list.sort(new BlockposSorter(new BlockPos(this.entity.posX, this.entity.posY + this.entity.height * 0.5, this.entity.posZ)));
				return list.get(0);
			}
			return null;
		}

		public Set<Entity> getEntitiesHit() {
			return this.entitiesList.keySet();
		}

		public Map<Entity, EnumFacing> getEntitiesHitMap() {
			return this.entitiesList;
		}

		public static void reposHitEntity(AxisAlignedBB referenceBB, Entity hitEntity, EnumFacing side) {
			if (side == EnumFacing.WEST) {
				hitEntity.posX = referenceBB.minX - hitEntity.width * 0.5;
			} else if (side == EnumFacing.EAST) {
				hitEntity.posX = referenceBB.maxX + hitEntity.width * 0.5;
			} else if (side == EnumFacing.NORTH) {
				hitEntity.posZ = referenceBB.minZ - hitEntity.width * 0.5;
			} else if (side == EnumFacing.SOUTH) {
				hitEntity.posZ = referenceBB.maxZ + hitEntity.width * 0.5;
			} else if (side == EnumFacing.UP) {
				hitEntity.posY = referenceBB.maxY;
			} else {
				hitEntity.posY = referenceBB.minY - hitEntity.height;
			}
			hitEntity.setPosition(hitEntity.posX, hitEntity.posY, hitEntity.posZ);
		}
	}

    public static class Vec2f {
	    public static final Vec2f ZERO = new Vec2f(0.0F, 0.0F);
	    public final float x;
	    public final float y;
	
	    public Vec2f(float xIn, float yIn) {
	        this.x = xIn;
	        this.y = yIn;
	    }

	    public boolean equals(Vec2f vec) {
	    	return vec == this || (wrapDegrees(vec.x) == wrapDegrees(this.x) && wrapDegrees(vec.y) == wrapDegrees(this.y));
	    }

	    public Vec2f wrapDegrees() {
	        return new Vec2f(wrapDegrees(this.x), wrapDegrees(this.y));
	    }

	    public Vec2f add(float f0, float f1) {
	    	return new Vec2f(wrapDegrees(this.x + f0), wrapDegrees(this.y + f1));
	    }

	    public Vec2f add(Vec2f vec) {
	    	return this.add(vec.x, vec.y);
	    }

	    public Vec2f subtract(float f0, float f1) {
	    	return new Vec2f(wrapDegrees(this.x - f0), wrapDegrees(this.y - f1));
	    }

	    public Vec2f subtract(Vec2f vec) {
	    	return this.subtract(vec.x, vec.y);
	    }

	    public Vec2f scale(float f0) {
	    	return new Vec2f(wrapDegrees(this.x * f0), wrapDegrees(this.y * f0));
	    }

	    public float lengthVector() {
	    	return (float)MathHelper.sqrt((double)this.x * (double)this.x + (double)this.y * (double)this.y);
	    }

	    public Vec2f rad2Deg() {
	    	return this.scale(180.0f / (float)Math.PI);
	    }

	    public static float wrapDegrees(float f0) {
	        while (f0 >= 180.0D) {
	            f0 -= 360.0D;
	        }
	        while (f0 < -180.0D) {
	            f0 += 360.0D;
	        }
	        return f0;
	    }

	    @Override
	    public String toString() {
	    	return "("+this.x+", "+this.y+")";
	    }
    }
}
