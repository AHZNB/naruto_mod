
package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityRegistry;

import net.minecraft.world.World;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;

import net.narutomod.item.ItemBijuCloak;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.collect.Maps;
import java.util.UUID;
import java.util.List;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Random;
import net.minecraft.client.renderer.chunk.SetVisibility;

@ElementsNarutomodMod.ModElement.Tag
public abstract class EntityBijuManager<T extends EntityTailedBeast.Base> {
	private static final Map<Class<? extends EntityTailedBeast.Base>, EntityBijuManager> mapByClass = Maps.newHashMap();
	private static final Map<Integer, EntityBijuManager> mapByTailnum = Maps.newHashMap();
	private static final int[] ZERO = {0, 0, 0};
	private UUID vesselUuid;
	private EntityPlayer jinchurikiPlayer;
	private T entity;
	private final Class<T> entityClass;
	private final int tails;
	private int cloakLevel;
	private long cloakCD;
	private final int[] cloakXp = new int[3];
	private int respawnCD;
	private static final Random rand = new Random();

	public static Collection<EntityBijuManager> getBMList() {
		return mapByClass.values();
	}
	
	@Nullable
	private static EntityBijuManager getBijuManagerFrom(EntityPlayer player) {
		for (EntityBijuManager bm : mapByClass.values()) {
			if (player.equals(bm.getJinchurikiPlayer())) {
				return bm;
			}
		}
		return null;
	}

	public static boolean isJinchuriki(EntityPlayer player) {
		return getBijuManagerFrom(player) != null;
	}

	public static boolean isJinchurikiOf(EntityPlayer player, Class<? extends EntityTailedBeast.Base> clazz) {
		EntityBijuManager tb = mapByClass.get(clazz);
		return tb != null && player.equals(tb.getJinchurikiPlayer());
	}

	@Nullable
	public static EntityTailedBeast.Base getBijuOfPlayerInWorld(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getEntityInWorld(player.world) : null;
	}

	public static int availableBijus() {
		int i = 0;
		for (EntityBijuManager bm : mapByClass.values()) {
			if (!bm.isSealed()) {
				++i;
			}
		}
		return i;
	}

	public static int getRandomAvailableBiju() {
		int i = availableBijus();
		if (i > 0) {
			i = rand.nextInt(i);
			int j = 0;
			for (EntityBijuManager bm : mapByClass.values()) {
				if (!bm.isSealed()) {
					if (j == i) {
						return bm.getTails();
					}
					++j;
				}
			}
		}
		return 0;
	}

	public static boolean anyBijuAddedToWorld() {
		for (EntityBijuManager bm : mapByClass.values()) {
			if (bm.isAddedToWorld()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isBijuAddedToWorld(int tails) {
		EntityBijuManager bm = mapByTailnum.get(tails);
		return bm != null && bm.isAddedToWorld();
	}

	public static void unsetPlayerAsJinchuriki(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		if (bm != null) {
			bm.setJinchurikiPlayer(null, true);
		}
	}

	public static boolean setPlayerAsJinchurikiByTails(EntityPlayer player, int tailnum) {
		EntityBijuManager bm = mapByTailnum.get(tailnum);
		if (bm != null && !bm.isSealed()) {
			bm.setJinchurikiPlayer(player, true);
			return true;
		}
		return false;
	}

	public static void revokeJinchurikiByTails(int tailnum) {
		EntityBijuManager bm = mapByTailnum.get(tailnum);
		if (bm != null) {
			bm.setJinchurikiPlayer(null, true);
		}
	}

	public static void revokeAllJinchuriki() {
		for (EntityBijuManager bm : mapByClass.values()) {
			bm.setJinchurikiPlayer(null, true);
		}
	}

	@Nullable
	public static EntityTailedBeast.Base getEntityByTails(int tailnum) {
		EntityBijuManager bm = mapByTailnum.get(tailnum);
		return bm != null ? bm.getEntity() : null;
	}

	public static int getTails(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getTails() : 0;
	}

	public static String getNameOfJinchurikisBiju(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getEntityLocalizedName() : null;
	}

	public static void toggleBijuCloak(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		if (bm != null) {
			bm.toggleBijuCloak();
		}
	}

	public static int increaseCloakLevel(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.increaseCloakLevel() : 0;
	}

	public static int cloakLevel(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getCloakLevel() : 0;
	}

	public static void addCloakXp(EntityPlayer player, int xp) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		if (bm != null) {
			bm.addCloakXp(xp);
		}
	}

	public static int getCloakXp(EntityPlayer player) {
		EntityBijuManager bm = getBijuManagerFrom(player);
		return bm != null ? bm.getCloakXp() : 0;
	}

	public static List<String> listJinchuriki() {
		List<String> list = Lists.newArrayList();
		for (EntityBijuManager bm : mapByClass.values()) {
			list.add(bm.toString());
		}
		return list;
	}

	public EntityBijuManager(Class<T> clazz, int tailnum) {
		this.entityClass = clazz;
		this.tails = tailnum;
		this.setCloakXPs(ZERO);
		mapByClass.put(clazz, this);
		mapByTailnum.put(tailnum, this);
	}

	public void onAddedToWorld(T entityIn) {
		this.onAddedToWorld(entityIn, true);
	}

	public void onAddedToWorld(T entityIn, boolean dirty) {
		this.entity = entityIn;
		if (dirty) {
			this.markDirty();
		}
	}

	public void onRemovedFromWorld(T entityIn) {
		this.onRemovedFromWorld(entityIn, true);
	}

	public void onRemovedFromWorld(T entityIn, boolean dirty) {
		this.entity = null;
		if (dirty) {
			this.markDirty();
		}
	}
	
	public boolean isAddedToWorld() {
		return this.entity != null;
	}

	public boolean isAddedToWorld(World world) {
		return this.entity != null && this.entity.world == world;
	}

	public void loadEntityFromNBT(NBTTagCompound compound) {
		this.entity.readFromNBT(compound);
	}

	public boolean isSealed() {
		return this.hasJinchuriki();
	}

	public boolean hasJinchuriki() {
		return this.vesselUuid != null;
	}

	public void setVesselUuid(@Nullable UUID uuid) {
		this.vesselUuid = uuid;
	}

	@Nullable
	public EntityPlayer getJinchurikiPlayer() {
		return this.jinchurikiPlayer;
	}

	public void setJinchurikiPlayer(@Nullable EntityPlayer player) {
		this.setJinchurikiPlayer(player, true);
	}

	public void setJinchurikiPlayer(@Nullable EntityPlayer player, boolean dirty) {
		if (player == null) {
			this.setVesselUuid(null);
			if (this.getCloakLevel() != 0) {
				this.toggleBijuCloak();
			}
			this.setCloakXPs(ZERO);
		} else {
			this.setVesselUuid(player.getUniqueID());
		}
		this.jinchurikiPlayer = player;
		if (dirty) {
			this.markDirty();
		}
	}

	public void verifyJinchuriki(EntityPlayer player) {
		if (player.getUniqueID().equals(this.vesselUuid)) {
			this.setJinchurikiPlayer(player, true);
			System.out.println(this.toString());
		}
	}

	public int[] getCloakXPs() {
		return this.cloakXp;
	}

	public void setCloakXPs(int[] xps) {
		this.cloakXp[0] = xps[0];
		this.cloakXp[1] = xps[1];
		this.cloakXp[2] = xps[2];
	}

	private void saveAndResetWearingTicks(int level) {
		if (level < 3 || this.isAddedToWorld()) {
			int i = level == 3 ? this.entity.getAge() : ItemBijuCloak.getWearingTicks(this.jinchurikiPlayer);
			this.cloakXp[level-1] += i / 20;
			if (level < 3) {
				ItemBijuCloak.setWearingTicks(this.jinchurikiPlayer, 0);
			}
			this.cloakCD += i + (int)((float)level * 2f * i / Math.max(MathHelper.sqrt(MathHelper.sqrt((float)this.cloakXp[level-1])) - 3f, 1f));
			this.markDirty();
		}
	}

	public void addCloakXp(int xp) {
		if (this.cloakLevel >= 1 && this.cloakLevel <= 3) {
			this.cloakXp[this.cloakLevel-1] += xp;
			this.markDirty();
		}
	}

	public int getCloakXp() {
		return this.cloakLevel >= 1 && this.cloakLevel <= 3 ? this.cloakXp[this.cloakLevel-1] : 0;
	}

	public long getCloakCD() {
		return this.cloakCD;
	}

	public void setCloakCD(long time) {
		this.cloakCD = time;
	}

	public void toggleBijuCloak() {
		if (this.jinchurikiPlayer != null) {
			Chakra.Pathway cp = Chakra.pathway(this.jinchurikiPlayer);
			int i = this.cloakLevel <= 0 ? 1 : 0;
			if (i == 1) {
				long l = this.jinchurikiPlayer.world.getTotalWorldTime();
				if (l < this.cloakCD && !this.jinchurikiPlayer.isCreative()) {
					if (!this.jinchurikiPlayer.world.isRemote) {
						this.jinchurikiPlayer.sendStatusMessage(
						 new TextComponentTranslation("chattext.cooldown.formatted", (this.cloakCD - l) / 20L), true);
					}
					return;
				}
				this.cloakCD = l;
				cp.consume(-5000d - this.getCloakXp(), true);
				if (this.jinchurikiPlayer.inventory.armorInventory.get(3).getItem() != ItemBijuCloak.helmet) {
					ItemStack stack = new ItemStack(ItemBijuCloak.helmet);
					stack.setTagCompound(new NBTTagCompound());
					stack.getTagCompound().setInteger("Tails", this.tails);
					ProcedureUtils.swapItemToSlot(this.jinchurikiPlayer, EntityEquipmentSlot.HEAD, stack);
				}
				if (this.jinchurikiPlayer.inventory.armorInventory.get(2).getItem() != ItemBijuCloak.body) {
					ItemStack stack = new ItemStack(ItemBijuCloak.body, 1, this.tails);
					stack.setTagCompound(new NBTTagCompound());
					stack.getTagCompound().setInteger("Tails", this.tails);
					ProcedureUtils.swapItemToSlot(this.jinchurikiPlayer, EntityEquipmentSlot.CHEST, stack);
				}
				if (this.jinchurikiPlayer.inventory.armorInventory.get(1).getItem() != ItemBijuCloak.legs) {
					ItemStack stack = new ItemStack(ItemBijuCloak.legs);
					stack.setTagCompound(new NBTTagCompound());
					stack.getTagCompound().setInteger("Tails", this.tails);
					ProcedureUtils.swapItemToSlot(this.jinchurikiPlayer, EntityEquipmentSlot.LEGS, stack);
				}
			} else {
				this.saveAndResetWearingTicks(this.cloakLevel);
				this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.helmet, -1, -1, null);
				this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.body, -1, -1, null);
				this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.legs, -1, -1, null);
				T biju = this.getEntityInWorld(this.jinchurikiPlayer.world);
				if (biju != null && !biju.isDead) {
					biju.setDead();
				}
				if (cp.isFull()) {
					cp.consume(cp.getAmount() - cp.getMax());
				}
			}
			this.cloakLevel = i;
		}
	}

	public int increaseCloakLevel() {
		if (this.cloakLevel < 3) {
			if ((this.cloakLevel == 1 && this.cloakXp[0] > 3600) || (this.cloakLevel == 2 && this.cloakXp[1] > 4800)) {
				Chakra.pathway(this.jinchurikiPlayer).consume(-10000d, true);
				this.saveAndResetWearingTicks(this.cloakLevel++);
			}
		} else {
			this.cloakLevel = 3;
		}
		if (this.cloakLevel == 3 && this.jinchurikiPlayer != null) {
			this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.helmet, -1, -1, null);
			this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.body, -1, -1, null);
			this.jinchurikiPlayer.inventory.clearMatchingItems(ItemBijuCloak.legs, -1, -1, null);
			try {
				T biju = this.entityClass.getConstructor(EntityPlayer.class).newInstance(this.jinchurikiPlayer);
				biju.forceSpawn = true;
				biju.setLifeSpan(this.cloakXp[2] * 5 + 200);
				this.jinchurikiPlayer.world.spawnEntity(biju);
				biju.forceSpawn = false;
			} catch (Exception exception) {
	            throw new Error(exception);
	        }
		}
		return this.cloakLevel;
	}

	public int getCloakLevel() {
		return this.cloakLevel;
	}

	@Nullable
	public Vec3d locateEntity() {
		return this.entity != null ? this.entity.getPositionVector() : null;
	}

	public int getTails() {
		return this.tails;
	}

	@Nullable
	public T getEntity() {
		return this.entity;
	}
	
	@Nullable
	public T getEntityInWorld(World world) {
		if (this.entity != null) {
			Entity entity1 = world.getEntityByID(this.entity.getEntityId());
			return entity1 != null && entity1.getClass() == this.entityClass ? (T)entity1 : null;
		}
		return null;
	}

	public abstract void markDirty();

	public String getEntityLocalizedName() {
		return I18n.translateToLocal("entity." + EntityRegistry.instance().getEntry(this.entityClass).getName() + ".name");
	}

	public String toString() {
		EntityPlayer jinchuriki = this.getJinchurikiPlayer();
		return " >>>> " + (jinchuriki != null ? jinchuriki.getName() : this.vesselUuid) + " is the " + this.getEntityLocalizedName() + " jinchuriki.";
	}

	public interface ITailBeast {
		void fuuinIntoPlayer(EntityPlayer player, int fuuinTime);
		boolean isFuuinInProgress();
		void cancelFuuin();
		void incFuuinProgress(int i);
		float getFuuinProgress();
	}
}
