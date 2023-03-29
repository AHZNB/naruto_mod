package net.narutomod.procedure;

import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Comparator;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureSharinganHelmetTickEvent extends ElementsNarutomodMod.ModElement {
	public ProcedureSharinganHelmetTickEvent(ElementsNarutomodMod instance) {
		super(instance, 57);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure SharinganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("itemstack") == null) {
			System.err.println("Failed to load dependency itemstack for procedure SharinganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("x") == null) {
			System.err.println("Failed to load dependency x for procedure SharinganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("y") == null) {
			System.err.println("Failed to load dependency y for procedure SharinganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("z") == null) {
			System.err.println("Failed to load dependency z for procedure SharinganHelmetTickEvent!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure SharinganHelmetTickEvent!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
		int x = (int) dependencies.get("x");
		int y = (int) dependencies.get("y");
		int z = (int) dependencies.get("z");
		World world = (World) dependencies.get("world");
		boolean not_my_sharingan = false;
		boolean f1 = false;
		String string = "";
		double player_id = 0;
		double sharingan_id = 0;
		ItemStack mangekyo = ItemStack.EMPTY;
		if ((((((itemstack)).getMaxDamage()) > 0) && (((((itemstack)).getMaxDamage()) - (((itemstack)).getItemDamage())) <= 3))) {
			{
				ItemStack _stack = (itemstack);
				if (!_stack.hasTagCompound())
					_stack.setTagCompound(new NBTTagCompound());
				_stack.getTagCompound().setBoolean("sharingan_blinded", (true));
			}
		}
		if ((!((itemstack).hasTagCompound() && (itemstack).getTagCompound().getBoolean("sharingan_blinded")))) {
			not_my_sharingan = (boolean) !((ItemDojutsu.Base) itemstack.getItem()).isOwner(itemstack, (EntityLivingBase) entity);;
			if ((not_my_sharingan)) {
				if (((((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).experienceLevel : 0) < 10)
						|| ((entity.getEntityData().getDouble((NarutomodModVariables.BATTLEXP))) < 300))) {
					if ((!(world.isRemote))) {
						if (entity instanceof EntityPlayer) {
							ItemStack _setstack = (itemstack);
							_setstack.setCount(1);
							ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
						}
						if (entity instanceof EntityPlayer) {
							((EntityPlayer) entity).inventory.armorInventory.set(3, new ItemStack(Blocks.AIR, (int) (1)));
							if (entity instanceof EntityPlayerMP)
								((EntityPlayerMP) entity).inventory.markDirty();
						}
						/* @ItemStack */
						if (entity instanceof EntityPlayer && !entity.world.isRemote) {
							((EntityPlayer) entity).sendStatusMessage(
									new TextComponentString(net.minecraft.util.text.translation.I18n.translateToLocal("chattext.sharingan.notyours")),
									(false));
						}
					}
					return;
				}
			}
			if ((entity.getEntityData().getBoolean("susanoo_activated"))) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", world);
					ProcedureSusanooSkeletonBodyTickEvent.executeProcedure($_dependencies);
				}
			}
			if ((((entity.ticksExisted % 80) == 70) && (world.isRemote))) {
				{
					List<Entity> _entfound = world
							.getEntitiesWithinAABB(Entity.class,
									new AxisAlignedBB(x - (20 / 2d), y - (20 / 2d), z - (20 / 2d), x + (20 / 2d), y + (20 / 2d), z + (20 / 2d)), null)
							.stream().sorted(new Object() {
								Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
									return Comparator.comparing((Function<Entity, Double>) (_entcnd -> _entcnd.getDistanceSq(_x, _y, _z)));
								}
							}.compareDistOf(x, y, z)).collect(Collectors.toList());
					for (Entity entityiterator : _entfound) {
						if (entityiterator.isInvisible()) {
							ProcedureOnLivingUpdate.setGlowingFor(entityiterator, 75);
						}
					}
				}
			}
			if ((((itemstack).getItem() == new ItemStack(ItemSharingan.helmet, (int) (1)).getItem())
					&& ((entity.getEntityData().getDouble((NarutomodModVariables.BATTLEXP))) >= 1000))) {
				if ((PlayerTracker.Deaths.hasRecentNearby((EntityPlayer) entity, 40D, 6000D) && (!(world.isRemote)))) {
					if ((Math.random() < 0.5)) {
						mangekyo = new ItemStack(ItemMangekyoSharingan.helmet, (int) (1));
					} else {
						mangekyo = new ItemStack(ItemMangekyoSharinganObito.helmet, (int) (1));
					}
					((ItemSharingan.Base) mangekyo.getItem()).copyOwner(mangekyo, itemstack);
					if (entity instanceof EntityPlayer) {
						ItemStack _setstack = (mangekyo);
						_setstack.setCount(1);
						ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
					}
					if (entity instanceof EntityPlayer)
						((EntityPlayer) entity).inventory.clearMatchingItems((itemstack).getItem(), -1, (int) 1, null);
					entity = ((ItemSharingan.Base) mangekyo.getItem()).getOwner(mangekyo, world);
					if ((!(((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
							? ((EntityPlayerMP) entity).getAdvancements()
									.getProgress(((WorldServer) (entity).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:mangekyosharinganopened")))
									.isDone()
							: false))) {
						if (entity instanceof EntityPlayerMP) {
							Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:mangekyosharinganopened"));
							AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
							if (!_ap.isDone()) {
								Iterator _iterator = _ap.getRemaningCriteria().iterator();
								while (_iterator.hasNext()) {
									String _criterion = (String) _iterator.next();
									((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
								}
							}
						}
						if ((entity instanceof EntityPlayerMP)) {
							world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
									(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
											.getObject(new ResourceLocation("ui.toast.challenge_complete")),
									SoundCategory.NEUTRAL, (float) 1, (float) 1);
						}
					}
				}
			}
		} else {
			if (entity instanceof EntityLivingBase)
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, (int) 1200, (int) 0, (false), (false)));
			if ((entity.getEntityData().getBoolean("susanoo_activated"))) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", world);
					ProcedureSusanoo.executeProcedure($_dependencies);
				}
			}
		}
	}
}
