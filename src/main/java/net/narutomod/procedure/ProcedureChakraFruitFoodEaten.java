package net.narutomod.procedure;

import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemSharingan;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemKekkeiMora;
import net.narutomod.item.ItemByakugan;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.Entity;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import java.util.Map;
import java.util.Iterator;

@ElementsNarutomodMod.ModElement.Tag
public class ProcedureChakraFruitFoodEaten extends ElementsNarutomodMod.ModElement {
	public ProcedureChakraFruitFoodEaten(ElementsNarutomodMod instance) {
		super(instance, 278);
	}

	public static void executeProcedure(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			System.err.println("Failed to load dependency entity for procedure ChakraFruitFoodEaten!");
			return;
		}
		if (dependencies.get("world") == null) {
			System.err.println("Failed to load dependency world for procedure ChakraFruitFoodEaten!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		World world = (World) dependencies.get("world");
		boolean f1 = false;
		double d = 0;
		double d1 = 0;
		ItemStack stack = ItemStack.EMPTY;
		ItemStack onhead = ItemStack.EMPTY;
		onhead = ((entity instanceof EntityPlayer) ? ((EntityPlayer) entity).inventory.armorInventory.get(3) : ItemStack.EMPTY);
		if (((entity instanceof EntityPlayer)
				? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemRinnegan.helmet, (int) (1)))
				: false)) {
			stack = ProcedureUtils.getItemStackIgnoreDurability(((EntityPlayer) entity).inventory, new ItemStack(ItemRinnegan.helmet));
			{
				ItemStack _stack = (stack);
				if (!_stack.hasTagCompound())
					_stack.setTagCompound(new NBTTagCompound());
				_stack.getTagCompound().setBoolean((NarutomodModVariables.RINNESHARINGAN_ACTIVATED), (true));
			}
			if (entity instanceof EntityPlayerMP) {
				Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
						.getAdvancement(new ResourceLocation("narutomod:rinnesharinganactivated"));
				AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
				if (!_ap.isDone()) {
					Iterator _iterator = _ap.getRemaningCriteria().iterator();
					while (_iterator.hasNext()) {
						String _criterion = (String) _iterator.next();
						((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
					}
				}
			}
			world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
							.getObject(new ResourceLocation("ui.toast.challenge_complete")),
					SoundCategory.NEUTRAL, (float) 1, (float) 1);
			if (entity instanceof EntityPlayer) {
				ItemStack _setstack = new ItemStack(ItemKekkeiMora.block, (int) (1));
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
			}
		} else if (((entity instanceof EntityPlayer)
				? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemTenseigan.helmet, (int) (1)))
				: false)) {
			stack = ProcedureUtils.getItemStackIgnoreDurability(((EntityPlayer) entity).inventory, new ItemStack(ItemTenseigan.helmet));
			{
				ItemStack _stack = (stack);
				if (!_stack.hasTagCompound())
					_stack.setTagCompound(new NBTTagCompound());
				_stack.getTagCompound().setDouble("ByakuganCount", 5);
			}
			{
				ItemStack _stack = (stack);
				if (!_stack.hasTagCompound())
					_stack.setTagCompound(new NBTTagCompound());
				_stack.getTagCompound().setBoolean((NarutomodModVariables.RINNESHARINGAN_ACTIVATED), (true));
			}
			if (entity instanceof EntityPlayerMP) {
				Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
						.getAdvancement(new ResourceLocation("narutomod:tensei_byakugan_activated"));
				AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
				if (!_ap.isDone()) {
					Iterator _iterator = _ap.getRemaningCriteria().iterator();
					while (_iterator.hasNext()) {
						String _criterion = (String) _iterator.next();
						((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
					}
				}
			}
			world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
							.getObject(new ResourceLocation("ui.toast.challenge_complete")),
					SoundCategory.NEUTRAL, (float) 1, (float) 1);
			if (entity instanceof EntityPlayer) {
				ItemStack _setstack = new ItemStack(ItemKekkeiMora.block, (int) (1));
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
			}
		} else {
			if (((entity instanceof EntityPlayer)
					? ((EntityPlayer) entity).inventory.hasItemStack(new ItemStack(ItemByakugan.helmet, (int) (1)))
					: false)) {
				stack = new ItemStack(ItemTenseigan.helmet, (int) (1));
				{
					ItemStack _stack = (stack);
					if (!_stack.hasTagCompound())
						_stack.setTagCompound(new NBTTagCompound());
					_stack.getTagCompound().setDouble("ByakuganCount", 5);
				}
			} else if (ProcedureUtils.hasAnyItemOfSubtype((EntityPlayer) entity, ItemSharingan.Base.class)) {
				stack = new ItemStack(ItemRinnegan.helmet, (int) (1));
			} else if ((Math.random() <= 0.5)) {
				stack = new ItemStack(ItemRinnegan.helmet, (int) (1));
			} else {
				stack = new ItemStack(ItemTenseigan.helmet, (int) (1));
				{
					ItemStack _stack = (stack);
					if (!_stack.hasTagCompound())
						_stack.setTagCompound(new NBTTagCompound());
					_stack.getTagCompound().setDouble("ByakuganCount", 5);
				}
			}
			if (entity instanceof EntityPlayer) {
				ItemStack _setstack = (stack);
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(((EntityPlayer) entity), _setstack);
			}
			if (entity instanceof EntityPlayerMP) {
				Advancement _adv = ((MinecraftServer) ((EntityPlayerMP) entity).mcServer).getAdvancementManager()
						.getAdvancement(new ResourceLocation("narutomod:rinneganawakened"));
				AdvancementProgress _ap = ((EntityPlayerMP) entity).getAdvancements().getProgress(_adv);
				if (!_ap.isDone()) {
					Iterator _iterator = _ap.getRemaningCriteria().iterator();
					while (_iterator.hasNext()) {
						String _criterion = (String) _iterator.next();
						((EntityPlayerMP) entity).getAdvancements().grantCriterion(_adv, _criterion);
					}
				}
			}
			world.playSound((EntityPlayer) null, (entity.posX), (entity.posY), (entity.posZ),
					(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
							.getObject(new ResourceLocation("ui.toast.challenge_complete")),
					SoundCategory.NEUTRAL, (float) 1, (float) 1);
		}
		if ((!(world.isRemote))) {
			d = (double) 100000;
			while (((d) > 0)) {
				d1 = (double) EntityXPOrb.getXPSplit((int) d);
				d = (double) ((d) - (d1));
				world.spawnEntity(new EntityXPOrb(world, entity.posX, entity.posY, entity.posZ, (int) d1) {
					@Override
					public void onCollideWithPlayer(EntityPlayer entityIn) {
						if (!this.world.isRemote && this.delayBeforeCanPickup == 0 && entityIn.xpCooldown == 0) {
							PlayerTracker.addBattleXp(entityIn, this.xpValue);
						}
						super.onCollideWithPlayer(entityIn);
					}
				});
			}
		}
	}
}
