
package net.narutomod.item;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.Minecraft;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureUpdateworldtick;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.PlayerTracker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemJutsu extends ElementsNarutomodMod.ModElement {
	public static final String NINJUTSU_TYPE = "ninjutsu_damage";
	public static final String SENJUTSU_TYPE = "senjutsu_damage";
	public static final DamageSource NINJUTSU_DAMAGE = new DamageSource(NINJUTSU_TYPE);
	public static final DamageSource SENJUTSU_DAMAGE = new DamageSource(SENJUTSU_TYPE);

	public ItemJutsu(ElementsNarutomodMod instance) {
		super(instance, 369);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new Base.EquipmentHook());
	}
	
	public static DamageSource causeJutsuDamage(Entity source, @Nullable EntityLivingBase indirectEntityIn) {
		return new EntityDamageSourceIndirect(NINJUTSU_TYPE, source, indirectEntityIn);
	}

	public static DamageSource causeSenjutsuDamage(Entity source, @Nullable EntityLivingBase indirectEntityIn) {
		return new EntityDamageSourceIndirect(SENJUTSU_TYPE, source, indirectEntityIn).setDamageIsAbsolute();
	}

	public static boolean isDamageSourceNinjutsu(DamageSource source) {
		return source.getDamageType().equals(NINJUTSU_TYPE);
	}

	public static boolean canTarget(@Nullable Entity targetIn) {
		return targetIn != null && targetIn.isEntityAlive() 
		 //&& !targetIn.getEntityData().getBoolean("kamui_intangible")
		 && !ProcedureOnLivingUpdate.isUntargetable(targetIn)
		 && (!(targetIn instanceof EntityPlayer) || !((EntityPlayer)targetIn).isSpectator());
	}

	public static void setCurrentJutsuCooldown(ItemStack stack, EntityLivingBase player, long cd) {
		if (stack.getItem() instanceof Base) {
			((Base)stack.getItem()).setCurrentJutsuCooldown(stack, (long)((double)cd * ((Base)stack.getItem()).getModifier(stack, player)));
		}
	}

	public static void setCurrentJutsuCooldown(ItemStack stack, long cd) {
		if (stack.getItem() instanceof Base) {
			((Base)stack.getItem()).setCurrentJutsuCooldown(stack, cd);
		}
	}

	public static void logBattleXP(EntityPlayer player) {
		ItemStack stack = player.getHeldItemMainhand();
		if (!(stack.getItem() instanceof Base)) {
			stack = player.getHeldItemOffhand();
		}
		if (stack.getItem() instanceof Base) {
			Base baseitem = (Base)stack.getItem();
			if (baseitem.getCurrentJutsuXp(stack) < baseitem.getCurrentJutsuRequiredXp(stack)) {
				baseitem.addCurrentJutsuXp(stack, 1);
			}
		}
	}

	public static void addBattleXP(EntityPlayer player, int xp) {
		ItemStack stack = player.getHeldItemMainhand();
		if (!(stack.getItem() instanceof Base)) {
			stack = player.getHeldItemOffhand();
		}
		if (stack.getItem() instanceof Base) {
			((Base)stack.getItem()).addCurrentJutsuXp(stack, xp);
		}
	}

	public static ItemJutsu.JutsuEnum getCurrentJutsu(ItemStack stack) {
		return ((Base)stack.getItem()).getCurrentJutsu(stack);
	}

	public static double getMaxPower(EntityLivingBase entity, double jutsuCkakraUsage) {
		return Chakra.pathway(entity).getAmount() / jutsuCkakraUsage * 0.9999d;
	}
	
	public abstract static class Base extends Item {
		private static final String JUTSU_INDEX_KEY = "JutsuIndexKey";
		private static final String CDMAP_KEY = "JutsuCDMapKey";
		private static final String XPMAP_KEY = "JutsuExperienceMapKey";
		private static final String OWNER_ID_KEY = "OwnerIdKey";
		private static final String AFFINITY_KEY = "IsNatureAffinityKey";
		private final ImmutableList<JutsuEnum> jutsuList;
		protected final long[] defaultCooldownMap;
		private final int[] jutsuXpMap;
	
		public Base(JutsuEnum.Type typeIn, JutsuEnum... jutsuListIn) {
			super();
			if (jutsuListIn.length > 0) {
				this.setMaxDamage(0);
				this.setFull3D();
				this.maxStackSize = 1;
				this.defaultCooldownMap = new long[jutsuListIn.length];
				this.jutsuXpMap = new int[jutsuListIn.length];
				for (int i = 0; i < jutsuListIn.length; i++) {
					this.defaultCooldownMap[i] = -1;
					this.jutsuXpMap[i] = 0;
					jutsuListIn[i].setType(typeIn);
				}
				this.jutsuList = ImmutableList.copyOf(jutsuListIn);
			} else {
				throw new IllegalArgumentException("Empty jutsu list!");
			}
		}

		protected boolean executeJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			JutsuEnum jutsuEnum = this.getCurrentJutsu(stack);
			Chakra.Pathway pw = Chakra.pathway(entity);
			double d = jutsuEnum.chakraUsage * power;
			if (power <= 0f || pw.getAmount() < d) {
				return false;
			}
			if (jutsuEnum.jutsu.createJutsu(stack, entity, power)) {
				//this.addCurrentJutsuXp(stack, (int)power);
				pw.consume(d);
				return true;
			}
			return false;
		}

		protected abstract float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft);

		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft, float basePower, float powerupDelay) {
			//boolean flag = entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative();
			//int i = flag ? this.getCurrentJutsuRequiredXp(stack) : this.getCurrentJutsuXp(stack);
			//float xpmodifier = i != 0 ? (float)this.getCurrentJutsuRequiredXp(stack) / (float)i : 0f;
			//float xpmodifier = this.getCurrentJutsuXpModifier(stack, entity);
			float f = powerupDelay * this.getModifier(stack, entity);
			return f > 0f ? Math.min(basePower + (float)(this.getMaxUseDuration() - timeLeft) / f, this.getMaxPower(stack, entity)) : 0f;
		}

		public float getModifier(ItemStack stack, EntityLivingBase entity) {
			return (float)Chakra.getChakraModifier(entity) * this.getCurrentJutsuXpModifier(stack, entity);
		}

		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			//return (float)(Chakra.pathway(entity).getAmount() / this.getCurrentJutsu(stack).chakraUsage);
			return (float)ItemJutsu.getMaxPower(entity, this.getCurrentJutsu(stack).chakraUsage);
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			if (!(player instanceof EntityPlayer) || PlayerTracker.isNinja((EntityPlayer)player)) {
				if (player instanceof EntityPlayer && !player.world.isRemote) {
					((EntityPlayer)player).sendStatusMessage(
						new TextComponentString(String.format("%.1f", this.getPower(stack, player, timeLeft))), true);
				}
			}
			this.onUsingEffects(player);
		}

		protected void onUsingEffects(EntityLivingBase player) {
			if (!player.world.isRemote) {
				Particles.spawnParticle(player.world, Particles.Types.SMOKE, player.posX, player.posY, player.posZ, 
				 40, 0.2d, 0d, 0.2d, 0d, 0.5d, 0d, 0x106AD1FF, 40, 5, 0xF0, player.getEntityId());
			}
			if (player.ticksExisted % 10 == 0) {
				player.world.playSound(null, player.posX, player.posY, player.posZ,
				 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:charging_chakra")),
				 net.minecraft.util.SoundCategory.PLAYERS, 0.05F, itemRand.nextFloat() + 0.5F);
			}
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
			if (!world.isRemote) {
				float power = this.getPower(itemstack, entity, timeLeft);
				if (this.executeJutsu(itemstack, entity, power)) {
					this.addCurrentJutsuXp(itemstack, 1);
				}
			}
		}

		private void resetJutsuMaps(ItemStack stack) {
			this.resetCooldownMap(stack);
			this.resetJutsuXpMap(stack);
		}

		private void resetJutsuXpMap(ItemStack stack) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setIntArray(XPMAP_KEY, this.jutsuXpMap);
		}
	
		private void resetCooldownMap(ItemStack stack) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			for (int i = 0; i < this.defaultCooldownMap.length; i++) 
				stack.getTagCompound().setLong(CDMAP_KEY+i, this.defaultCooldownMap[i]);
		}

		private void validateMapTags(ItemStack stack, int index) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			if (index >= 0 && !stack.getTagCompound().hasKey(CDMAP_KEY+index))
				stack.getTagCompound().setLong(CDMAP_KEY+index, this.defaultCooldownMap[index]);
			if (!stack.getTagCompound().hasKey(XPMAP_KEY))
				stack.getTagCompound().setIntArray(XPMAP_KEY, this.jutsuXpMap);
		}

		private int[] getJutsuXpMap(ItemStack stack) {
			this.validateMapTags(stack, -1);
			int[] xpmap = stack.getTagCompound().getIntArray(XPMAP_KEY);
			if (xpmap.length < this.jutsuList.size()) {
				int[] map2 = xpmap;
				xpmap = new int[this.jutsuList.size()];
				for (int i = 0; i < map2.length; i++) {
					xpmap[i] = map2[i];
				}
			}
			return xpmap;
		}

		private int getJutsuXp(ItemStack stack, int index) {
			return this.getJutsuXpMap(stack)[index];
		}

		public int getJutsuXp(ItemStack stack, JutsuEnum jutsuIn) {
			return this.jutsuList.contains(jutsuIn) ? this.getJutsuXp(stack, jutsuIn.index) : 0;
		}

		public int getCurrentJutsuXp(ItemStack stack) {
			return this.getJutsuXp(stack, this.getCurrentJutsuIndex(stack));
		}

		private void addJutsuXp(ItemStack stack, int index, int xp) {
			int[] xpmap = this.getJutsuXpMap(stack);
			xpmap[index] += xp;
			stack.getTagCompound().setIntArray(XPMAP_KEY, xpmap);
		}

		public void addJutsuXp(ItemStack stack, JutsuEnum jutsuIn, int xp) {
			if (this.jutsuList.contains(jutsuIn)) {
				this.addJutsuXp(stack, jutsuIn.index,
				 Math.min(this.getRequiredXp(stack, jutsuIn.index) * 3 - this.getJutsuXp(stack, jutsuIn.index), xp));
			}
		}

		public void addCurrentJutsuXp(ItemStack stack, int xp) {
			this.addJutsuXp(stack, this.getCurrentJutsuIndex(stack),
			 Math.min(this.getCurrentJutsuRequiredXp(stack) * 3 - this.getCurrentJutsuXp(stack), xp));
		}

		private int getRequiredXp(ItemStack stack, int index) {
			int requiredXp = this.jutsuList.get(index).requiredXP;
			return this.isAffinity(stack) ? requiredXp : (int)((float)requiredXp * 2.5f);
		}

		public int getRequiredXp(ItemStack stack, JutsuEnum jutsuIn) {
			return this.jutsuList.contains(jutsuIn) ? this.getRequiredXp(stack, jutsuIn.index) : -1;
		}

		public float getXpRatio(ItemStack stack, JutsuEnum jutsuIn) {
			return this.jutsuList.contains(jutsuIn) ?
			 (float)this.getJutsuXp(stack, jutsuIn.index) / (float)this.getRequiredXp(stack, jutsuIn.index) : 0;
		}

		public int getCurrentJutsuRequiredXp(ItemStack stack) {
			return this.getRequiredXp(stack, this.getCurrentJutsuIndex(stack));
		}
	
		public float getCurrentJutsuXpModifier(ItemStack stack, EntityLivingBase entity) {
			boolean flag = !(entity instanceof EntityPlayer) || ((EntityPlayer)entity).isCreative();
			int j = this.getCurrentJutsuRequiredXp(stack);
			int i = flag ? j : this.getCurrentJutsuXp(stack);
			return i != 0 ? (float)j / (float)i : 0f;
		}

		public boolean canUseAnyJutsu(ItemStack stack) {
			for (int i = 0; i < this.jutsuList.size(); i++) {
				if (this.getJutsuXp(stack, i) >= this.getRequiredXp(stack, i)) {
					return true;
				}
			}
			return false;
		}

		protected long getCurrentJutsuCooldown(ItemStack stack) {
			return this.getJutsuCooldown(stack, this.getCurrentJutsuIndex(stack));
		}
		
		private long getJutsuCooldown(ItemStack stack, int index) {
			this.validateMapTags(stack, index);
			return stack.getTagCompound().getLong(CDMAP_KEY+index);
		}

		public void setCurrentJutsuCooldown(ItemStack stack, long cd) {
			this.setJutsuCooldown(stack, this.getCurrentJutsuIndex(stack), cd);
		}

		private void setJutsuCooldown(ItemStack stack, int index, long cd) {
			this.validateMapTags(stack, index);
			stack.getTagCompound().setLong(CDMAP_KEY+index, ProcedureUpdateworldtick.getTotalWorldTime() + cd);
		}

		public void setJutsuCooldown(ItemStack stack, JutsuEnum jutsuIn, long cd) {
			if (this.jutsuList.contains(jutsuIn) && this.isJutsuEnabled(stack, jutsuIn.index)) {
				this.setJutsuCooldown(stack, jutsuIn.index, cd);
			}
		}

		private void enableJutsu(ItemStack stack, int index, boolean enable) {
			long l = this.getJutsuCooldown(stack, index);
			stack.getTagCompound().setLong(CDMAP_KEY+index, enable ? l < 0 ? 0 : l : -1);
		}

		public void enableJutsu(ItemStack stack, JutsuEnum jutsuIn, boolean enable) {
			if (this.jutsuList.contains(jutsuIn)) {
				this.enableJutsu(stack, jutsuIn.index, enable);
			} else {
				System.err.println("Justu ["+jutsuIn.getName()+"] does not belong in "+this);
			}
		}

		private boolean isJutsuEnabled(ItemStack stack, int index) {
			return this.getJutsuCooldown(stack, index) >= 0;
		}

		public boolean isJutsuEnabled(ItemStack stack, JutsuEnum jutsuIn) {
			return this.jutsuList.contains(jutsuIn) && this.isJutsuEnabled(stack, jutsuIn.index);
		}

		public boolean isAnyJutsuEnabled(ItemStack stack) {
			for (JutsuEnum je : this.jutsuList) {
				if (this.isJutsuEnabled(stack, je.index)) {
					return true;
				}
			}
			return false;
		}

		public List<JutsuEnum> getActivatedJutsus(ItemStack stack) {
			List<JutsuEnum> list = Lists.newArrayList();
			for (JutsuEnum je : this.jutsuList) {
				if (je.jutsu.isActivated(stack)) {
					list.add(je);
				}
			}
			return list;
		}

		private boolean canUseJutsu(ItemStack stack, int index, @Nullable EntityLivingBase entity) {
			return (entity != null && this.isOwner(stack, entity) && this.isJutsuEnabled(stack, index)) ||
			       (entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative());
		}

		protected boolean canUseJutsu(ItemStack stack, JutsuEnum jutsuIn, @Nullable EntityLivingBase entity) {
			return this.jutsuList.contains(jutsuIn) && this.canUseJutsu(stack, jutsuIn.index, entity);
		}
		
		protected boolean canUseCurrentJutsu(ItemStack stack, @Nullable EntityLivingBase entity) {
			return this.canUseJutsu(stack, this.getCurrentJutsuIndex(stack), entity);
		}

		protected int getCurrentJutsuIndex(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getInteger(JUTSU_INDEX_KEY) : 0;
		}

		protected JutsuEnum getCurrentJutsu(ItemStack stack) {
			return this.jutsuList.get(this.getCurrentJutsuIndex(stack));
		}
	
		private void setNextJutsu(ItemStack stack, EntityLivingBase entity) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			int i = 0;
			int next = this.getCurrentJutsuIndex(stack);
			for ( ; i < this.jutsuList.size(); i++) {
				++next;
				if (next >= this.jutsuList.size())
					next = 0;
				if (this.canUseJutsu(stack, next, entity))
					break;
			}
			if (i < this.jutsuList.size()) {
				stack.getTagCompound().setInteger(JUTSU_INDEX_KEY, next);
				if (entity instanceof EntityPlayer && !entity.world.isRemote)
					((EntityPlayer) entity).sendStatusMessage(new TextComponentString(this.jutsuList.get(next).getName()), true);
			}
		}

		public void setIsAffinity(ItemStack stack, boolean b) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setBoolean(AFFINITY_KEY, b);
		}

		private boolean isAffinity(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getBoolean(AFFINITY_KEY) : false;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(new TextComponentTranslation("tooltip.general.shift").getUnformattedComponentText());
			for (JutsuEnum j : this.jutsuList) {
				if (this.canUseJutsu(itemstack, j.index, Minecraft.getMinecraft().player)) {
					list.add((this.getCurrentJutsuIndex(itemstack) == j.index ? ">" : " ")
					 +(j.index+1) + ": " + j.getName() + " (XP: " + TextFormatting.GREEN
					 + this.getJutsuXp(itemstack, j.index) + TextFormatting.GRAY + "/" + this.getRequiredXp(itemstack, j.index) + ")");
				}
			}
		}
	
		public void setOwner(ItemStack stack, EntityLivingBase owner) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setUniqueId(OWNER_ID_KEY, owner.getUniqueID());
			stack.setStackDisplayName(stack.getDisplayName() + " (" + owner.getName() + ")");
		}

		@Nullable
		protected UUID getOwnerUuid(ItemStack stack) {
			return stack.hasTagCompound() && stack.getTagCompound().hasUniqueId(OWNER_ID_KEY) 
			 ? stack.getTagCompound().getUniqueId(OWNER_ID_KEY) : null;
		}

		@Nullable
		private EntityLivingBase getOwner(ItemStack stack) {
			UUID uuid = this.getOwnerUuid(stack);
			if (uuid != null) {
			//if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId(OWNER_ID_KEY)) {
				//Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(stack.getTagCompound().getUniqueId(OWNER_ID_KEY));
				Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(uuid);
				return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
			}
			return null;
		}

		protected boolean isOwner(ItemStack stack, EntityLivingBase entity) {
			if (!stack.hasTagCompound() || !stack.getTagCompound().hasUniqueId(OWNER_ID_KEY)) {
				this.setOwner(stack, entity);
				this.resetJutsuMaps(stack);
			}
			return stack.getTagCompound().getUniqueId(OWNER_ID_KEY).equals(entity.getUniqueID());
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase livingEntity = (EntityLivingBase) entity;
				if (!this.isOwner(itemstack, livingEntity)) {
					return;
				}
			}
		}

		public static class EquipmentHook {
			@SubscribeEvent
			public void onEquipmentChange(LivingEquipmentChangeEvent event) {
				EntityLivingBase entity = event.getEntityLiving();
				ItemStack stack = event.getTo();
				if (entity instanceof EntityPlayer && !entity.world.isRemote
				 && event.getSlot().getSlotType() == EntityEquipmentSlot.Type.HAND && stack.getItem() instanceof Base) {
					if (event.getSlot() == EntityEquipmentSlot.MAINHAND || !(entity.getHeldItemMainhand().getItem() instanceof Base)) {
						((EntityPlayer)entity).sendStatusMessage(new TextComponentString(ItemJutsu.getCurrentJutsu(stack).getName()), true);
					}
				}
			}
		}

		public static void switchNextJutsu(ItemStack stack, EntityLivingBase entity) {
			if (stack.getItem() instanceof Base) {
				((Base)stack.getItem()).setNextJutsu(stack, entity);
			}
		}

		public EnumActionResult canActivateJutsu(ItemStack stack, JutsuEnum jutsuIn, EntityPlayer entity) {
			if (!entity.isCreative()) {
				if (!this.jutsuList.contains(jutsuIn) || !this.canUseJutsu(stack, jutsuIn.index, entity)) {
					return EnumActionResult.FAIL;
				}
				if (this.getJutsuXp(stack, jutsuIn.index) < this.getRequiredXp(stack, jutsuIn.index)
				 || !PlayerTracker.isNinja(entity)) {
					return EnumActionResult.FAIL;
				}
				long cd = this.getJutsuCooldown(stack, jutsuIn.index);
				if (cd > entity.world.getTotalWorldTime()) {
					return EnumActionResult.PASS;
				} else if (cd < 0) {
					return EnumActionResult.FAIL;
				}
			}
			return EnumActionResult.SUCCESS;
		}
	
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ItemStack stack = entity.getHeldItem(hand);
			EnumActionResult res = this.canActivateJutsu(stack, this.getCurrentJutsu(stack), entity);
			if (res == EnumActionResult.PASS && !world.isRemote) {
				entity.sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", 
				 (this.getCurrentJutsuCooldown(stack) - world.getTotalWorldTime()) / 20), true);
			} else if (res == EnumActionResult.SUCCESS) {
				entity.setActiveHand(hand);
			}
			return new ActionResult<ItemStack>(res, stack);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BOW;
		}
	
		protected int getMaxUseDuration() {
			return 72000;
		}
	
		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return this.getMaxUseDuration();
		}

		@Override
		public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
			return false;
		}

		@Override
		public int getMaxDamage() {
			return 0;
		}

		@Override
		public boolean isDamageable() {
			return false;
		}
	}

	public interface IJutsu {
	}
	
	public interface IJutsuCallback {
		boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power);
		
		default boolean isActivated(ItemStack stack) {
			return false;
		}
		
		default boolean isActivated(EntityLivingBase entity) {
			return false;
		}
		
		default void deactivate(EntityLivingBase entity) {
		}
		
		default float getPower(ItemStack stack) {
			return 0.0f;
		}
		
		default JutsuData getData(EntityLivingBase entity) {
			return null;
		}

		public static class JutsuData {
			public final Entity entity;
			public final ItemStack stack;

			public JutsuData(Entity entityIn, ItemStack stackIn) {
				this.entity = entityIn;
				this.stack = stackIn;
			}
		}
	}

	public static class JutsuEnum {
		public final int index;
		public final String unlocalizedName;
		public final char rank;
		public final int requiredXP;
		public final double chakraUsage;
		public final IJutsuCallback jutsu;
		private Type type;
		public float basePower = 0f;
		public float powerUpDelay = 50f;
		private static final List<JutsuEnum> jutsuList = Lists.newArrayList();
	
		public JutsuEnum(int idx, String string, int xp, IJutsuCallback jutsuIn) {
			this(idx, string, xp, 0d, jutsuIn);
		}

		public JutsuEnum(int idx, String string, char rankIn, IJutsuCallback jutsuIn) {
			this(idx, string, rankIn, 0d, jutsuIn);
		}

		public JutsuEnum(int idx, String string, char rankIn, double chakraUsageIn, IJutsuCallback jutsuIn) {
			this(idx, string, rankIn, 
			 //rankIn=='S' ? 30 : rankIn=='A' ? 25 : rankIn=='B' ? 20 : rankIn=='C' ? 15 : rankIn=='D' ? 10 : 90, chakraUsageIn, jutsuIn);
			 rankIn=='S' ? 400 : rankIn=='A' ? 250 : rankIn=='B' ? 200 : rankIn=='C' ? 150 : rankIn=='D' ? 100 : 900, chakraUsageIn, jutsuIn);
		}

		public JutsuEnum(int idx, String string, int xp, double chakraUsageIn, IJutsuCallback jutsuIn) {
			this(idx, string, ' ', xp, chakraUsageIn, jutsuIn);
		}

		public JutsuEnum(int idx, String string, char rankIn, int xp, double chakraUsageIn, IJutsuCallback jutsuIn) {
			this.index = idx;
			this.unlocalizedName = string;
			this.rank = (rankIn=='S' || rankIn=='A' || rankIn=='B' || rankIn=='C' || rankIn=='D') ? rankIn : 0;
			this.requiredXP = xp;
			this.chakraUsage = chakraUsageIn;
			this.jutsu = jutsuIn;
			JutsuEnum.jutsuList.add(this);
		}
		
		public String getName() {
			String s = this.unlocalizedName;
			if (!s.contains(".")) {
				s = "entity." + s + ".name";
			}
			return net.minecraft.util.text.translation.I18n.translateToLocal(s);
		}
		
		public static ImmutableList<JutsuEnum> getJutsuList() {
			return ImmutableList.copyOf(JutsuEnum.jutsuList);
		}

		public static ImmutableList<JutsuEnum> getJutsuList(char rankIn) {
			List<JutsuEnum> list = Lists.newArrayList();
			for (JutsuEnum je : JutsuEnum.jutsuList) {
				if (je.rank == rankIn) {
					list.add(je);
				}
			}
			return ImmutableList.copyOf(list);
		}

		public Type getType() {
			return this.type;
		}

		private JutsuEnum setType(Type typeIn) {
			this.type = typeIn;
			return this;
		}

		public String toString() {
			return "\nJutsu - " + this.type + ": " + this.getName() + ", rank:" + this.rank;
		}

		public enum Type {
			NINJUTSU,
			DOTON,
			FUTON,
			KATON,
			RAITON,
			SUITON,
			INTON,
			YOTON,
			JINTON,
			MOKUTON,
			JITON,
			IRYO,
			HYOTON,
			BAKUTON,
			SHAKUTON,
			BYAKUGAN,
			SHARINGAN,
			RINNEGAN,
			RANTON,
			FUTTON,
			YOOTON,
			SHIKOTSUMYAKU,
			KUCHIYOSE,
			TENSEIGAN,
			SENJUTSU,
			SIXPATHSENJUTSU,
			KEKKEIMORA,
			OTHER;
		}
	}
}


