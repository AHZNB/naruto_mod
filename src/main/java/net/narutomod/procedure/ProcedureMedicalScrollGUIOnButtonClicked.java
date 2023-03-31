package net.narutomod.procedure;

import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharingan;
import net.narutomod.item.ItemByakugan;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import java.util.function.Supplier;
import java.util.UUID;
import java.util.Map;
import java.util.Iterator;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureMedicalScrollGUIOnButtonClicked extends ElementsNarutomodMod.ModElement {
	public ProcedureMedicalScrollGUIOnButtonClicked(ElementsNarutomodMod instance) {
		super(instance, 202);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure MedicalScrollGUIOnButtonClicked!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure MedicalScrollGUIOnButtonClicked!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		ItemStack stack0 = ItemStack.EMPTY;
		ItemStack stack1 = ItemStack.EMPTY;
		ItemStack newstack = ItemStack.EMPTY;
		String newitemname = "";
		double TenseiganEvolvedTime = 0;
		boolean hasKey = false;
		if ((((entity instanceof EntityPlayerMP) && ((entity).world instanceof WorldServer))
				? ((EntityPlayerMP) entity).getAdvancements()
						.getProgress(((WorldServer) (entity).world).getAdvancementManager()
								.getAdvancement(new ResourceLocation("narutomod:achievementmedicalgenin")))
						.isDone()
				: false)) {
			stack0 = (new Object() {
				public ItemStack getItemStack(int sltid) {
					if (entity instanceof EntityPlayerMP) {
						Container _current = ((EntityPlayerMP) entity).openContainer;
						if (_current instanceof Supplier) {
							Object invobj = ((Supplier) _current).get();
							if (invobj instanceof Map) {
								return ((Slot) ((Map) invobj).get(sltid)).getStack();
							}
						}
					}
					return ItemStack.EMPTY;
				}
			}.getItemStack((int) (0)));
			stack1 = (new Object() {
				public ItemStack getItemStack(int sltid) {
					if (entity instanceof EntityPlayerMP) {
						Container _current = ((EntityPlayerMP) entity).openContainer;
						if (_current instanceof Supplier) {
							Object invobj = ((Supplier) _current).get();
							if (invobj instanceof Map) {
								return ((Slot) ((Map) invobj).get(sltid)).getStack();
							}
						}
					}
					return ItemStack.EMPTY;
				}
			}.getItemStack((int) (1)));
			if (((((stack0).getItem() == new ItemStack(ItemMangekyoSharingan.helmet, (int) (1)).getItem())
					|| ((stack0).getItem() == new ItemStack(ItemMangekyoSharinganObito.helmet, (int) (1)).getItem()))
					&& (((stack1).getItem() == new ItemStack(ItemMangekyoSharingan.helmet, (int) (1)).getItem())
							|| ((stack1).getItem() == new ItemStack(ItemMangekyoSharinganObito.helmet, (int) (1)).getItem())))) {
				UUID owner_uuid = ProcedureUtils.getOwnerId(stack0);
				UUID other_uuid = ProcedureUtils.getOwnerId(stack1);
				System.out.println("-- owner_uuid=" + owner_uuid + ", other_uuid=" + other_uuid);
				if (owner_uuid != null && other_uuid != null && !owner_uuid.equals(other_uuid)) {
					EntityLivingBase owner = ProcedureUtils.searchLivingMatchingId(owner_uuid);
					if ((((owner instanceof EntityPlayerMP) && ((owner).world instanceof WorldServer))
							? ((EntityPlayerMP) owner).getAdvancements()
									.getProgress(((WorldServer) (owner).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:mangekyosharinganopened")))
									.isDone()
							: false)) {
						newstack = new ItemStack(ItemMangekyoSharinganEternal.helmet, (int) (1));
						((ItemSharingan.Base) newstack.getItem()).copyOwner(newstack, stack0);
						if (entity instanceof EntityPlayerMP) {
							Container _current = ((EntityPlayerMP) entity).openContainer;
							if (_current instanceof Supplier) {
								Object invobj = ((Supplier) _current).get();
								if (invobj instanceof Map) {
									ItemStack _setstack = (newstack);
									_setstack.setCount(1);
									((Slot) ((Map) invobj).get((int) (2))).putStack(_setstack);
									_current.detectAndSendChanges();
								}
							}
						}
						if (entity instanceof EntityPlayerMP) {
							Container _current = ((EntityPlayerMP) entity).openContainer;
							if (_current instanceof Supplier) {
								Object invobj = ((Supplier) _current).get();
								if (invobj instanceof Map) {
									((Slot) ((Map) invobj).get((int) (0))).decrStackSize((int) (1));
									_current.detectAndSendChanges();
								}
							}
						}
						if (entity instanceof EntityPlayerMP) {
							Container _current = ((EntityPlayerMP) entity).openContainer;
							if (_current instanceof Supplier) {
								Object invobj = ((Supplier) _current).get();
								if (invobj instanceof Map) {
									((Slot) ((Map) invobj).get((int) (1))).decrStackSize((int) (1));
									_current.detectAndSendChanges();
								}
							}
						}
						world.playSound((EntityPlayer) null, (owner.posX), (owner.posY), (owner.posZ),
								(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
										.getObject(new ResourceLocation("ui.toast.challenge_complete")),
								SoundCategory.NEUTRAL, (float) 1, (float) 1);
						if (owner instanceof EntityPlayerMP) {
							Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) owner).mcServer).getAdvancementManager()
									.getAdvancement(new ResourceLocation("narutomod:eternalmangekyoachieved"));
							AdvancementProgress _ap = ((EntityPlayerMP) owner).getAdvancements().getProgress(_adv);
							if (!_ap.isDone()) {
								Iterator _iterator = _ap.getRemaningCriteria().iterator();
								while (_iterator.hasNext()) {
									String _criterion = (String) _iterator.next();
									((EntityPlayerMP) owner).getAdvancements().grantCriterion(_adv, _criterion);
								}
							}
						}
					}
				}
			}
			if (((((stack0).getItem() == new ItemStack(ItemByakugan.helmet, (int) (1)).getItem())
					|| ((stack0).getItem() == new ItemStack(ItemTenseigan.helmet, (int) (1)).getItem()))
					&& ((stack1).getItem() == new ItemStack(ItemByakugan.helmet, (int) (1)).getItem()))) {
				UUID owner_uuid = ProcedureUtils.getOwnerId(stack0);
				UUID other_uuid = ProcedureUtils.getOwnerId(stack1);
				System.out.println("-- owner_uuid=" + owner_uuid + ", other_uuid=" + other_uuid);
				if (owner_uuid != null && other_uuid != null && !owner_uuid.equals(other_uuid)) {
					EntityLivingBase owner = ProcedureUtils.searchLivingMatchingId(owner_uuid);
					if ((((owner instanceof EntityPlayerMP) && ((owner).world instanceof WorldServer))
							? ((EntityPlayerMP) owner).getAdvancements()
									.getProgress(((WorldServer) (owner).world).getAdvancementManager()
											.getAdvancement(new ResourceLocation("narutomod:byakuganopened")))
									.isDone()
							: false)) {
						{
							ItemStack _stack = (stack0);
							if (!_stack.hasTagCompound())
								_stack.setTagCompound(new NBTTagCompound());
							_stack.getTagCompound().setDouble("ByakuganCount",
									(((stack0).hasTagCompound() ? (stack0).getTagCompound().getDouble("ByakuganCount") : -1) + 1));
						}
						newstack = ((stack0).copy());
						if (((stack0).getItem() == new ItemStack(ItemByakugan.helmet, (int) (1)).getItem())) {
							hasKey = (boolean) newstack.hasTagCompound()
									&& newstack.getTagCompound().hasKey(NarutomodModVariables.tenseiganEvolvedTime);;
							if ((!(hasKey))) {
								TenseiganEvolvedTime = (double) 1728000;
							} else {
								TenseiganEvolvedTime = (double) (((newstack).hasTagCompound()
										? (newstack).getTagCompound().getDouble((NarutomodModVariables.tenseiganEvolvedTime))
										: -1) - 345600);
							}
							{
								ItemStack _stack = (newstack);
								if (!_stack.hasTagCompound())
									_stack.setTagCompound(new NBTTagCompound());
								_stack.getTagCompound().setDouble((NarutomodModVariables.tenseiganEvolvedTime), (TenseiganEvolvedTime));
							}
						}
						if (entity instanceof EntityPlayerMP) {
							Container _current = ((EntityPlayerMP) entity).openContainer;
							if (_current instanceof Supplier) {
								Object invobj = ((Supplier) _current).get();
								if (invobj instanceof Map) {
									ItemStack _setstack = (newstack);
									_setstack.setCount(1);
									((Slot) ((Map) invobj).get((int) (2))).putStack(_setstack);
									_current.detectAndSendChanges();
								}
							}
						}
						if (entity instanceof EntityPlayerMP) {
							Container _current = ((EntityPlayerMP) entity).openContainer;
							if (_current instanceof Supplier) {
								Object invobj = ((Supplier) _current).get();
								if (invobj instanceof Map) {
									((Slot) ((Map) invobj).get((int) (0))).decrStackSize((int) (1));
									_current.detectAndSendChanges();
								}
							}
						}
						if (entity instanceof EntityPlayerMP) {
							Container _current = ((EntityPlayerMP) entity).openContainer;
							if (_current instanceof Supplier) {
								Object invobj = ((Supplier) _current).get();
								if (invobj instanceof Map) {
									((Slot) ((Map) invobj).get((int) (1))).decrStackSize((int) (1));
									_current.detectAndSendChanges();
								}
							}
						}
					}
				}
			}
		}
	}
}
