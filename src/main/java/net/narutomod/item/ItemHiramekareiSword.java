
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.potion.PotionReach;
import net.narutomod.entity.EntityChakraFlow;
import net.narutomod.Particles;
import net.narutomod.Chakra;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Random;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemHiramekareiSword extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:hiramekarei_sword")
	public static final Item block = null;
	public static final int ENTITYID = 292;

	public ItemHiramekareiSword(ElementsNarutomodMod instance) {
		super(instance, 615);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityEffects.class)
				.id(new ResourceLocation("narutomod", "entitybullethiramekarei_sword"), ENTITYID).name("entitybullethiramekarei_sword")
				.tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:hiramekarei", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityEffects.class, renderManager -> {
			return new RenderEffects(renderManager);
		});
	}

	public static class RangedItem extends Item implements ItemOnBody.Interface {
		public RangedItem() {
			super();
			this.setMaxDamage(0);
			this.setFull3D();
			this.setUnlocalizedName("hiramekarei_sword");
			this.setRegistryName("hiramekarei_sword");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
			Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				NBTTagCompound cmp = this.effectActive(stack) ? stack.getTagCompound().getCompoundTag("EffectEntityActive") : null;
				double strength = cmp != null ? cmp.getDouble("strength") : 6d;
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
				 new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", strength, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
				 new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
				if (cmp != null) {
					multimap.put(EntityPlayer.REACH_DISTANCE.getName(), 
					 new AttributeModifier(UUID.fromString(PotionReach.REACH_MODIFIER), "Ranged item modifier", 4d, 0));
				}
			}
			return multimap;
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.hiramekarei.general"));
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				EntityEffects entity1 = new EntityEffects(entity);
				world.spawnEntity(entity1);
				if (!itemstack.hasTagCompound()) {
					itemstack.setTagCompound(new NBTTagCompound());
				}
				NBTTagCompound cmp = new NBTTagCompound();
				cmp.setInteger("Id", entity1.getEntityId());
				cmp.setDouble("strength", Chakra.getLevel(entity) * 0.5d);
				itemstack.getTagCompound().setTag("EffectEntityActive", cmp);
				if (!entity.isCreative()) {
					entity.getCooldownTracker().setCooldown(itemstack.getItem(), 500);
				}
			}
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			EntityEffects effectentity = this.getEntity(world, itemstack);
			if (effectentity == null || !effectentity.isEntityAlive()) {
				if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("EffectEntityActive")) {
					itemstack.getTagCompound().removeTag("EffectEntityActive");
				}
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			entity.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, entity.getHeldItem(hand));
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BOW;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}

		@Nullable
		private EntityEffects getEntity(World world, ItemStack stack) {
			if (this.effectActive(stack)) {
				NBTTagCompound cmp = stack.getTagCompound().getCompoundTag("EffectEntityActive");
				Entity entity = world.getEntityByID(cmp.getInteger("Id"));
				return entity instanceof EntityEffects ? (EntityEffects)entity : null;
			}
			return null;
		}

		public boolean effectActive(ItemStack itemstack) {
			return itemstack.hasTagCompound() ? itemstack.getTagCompound().hasKey("EffectEntityActive", 10) : false;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public boolean hasEffect(ItemStack itemstack) {
			return this.effectActive(itemstack);
		}
	}

	public static class EntityEffects extends EntityChakraFlow.Base {
		private final int duration = 200;

		public EntityEffects(World a) {
			super(a);
		}

		public EntityEffects(EntityLivingBase user) {
			super(user);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted > this.duration) {
				this.setDead();
			}
		}

		@Override
		protected void addEffects() {
		}

		private Random getRNG() {
			return this.rand;
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderEffects extends EntityChakraFlow.RenderCustom<EntityEffects> {
		public RenderEffects(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}

		@Override
		protected void spawnParticles(EntityEffects entity, Vec3d startvec, Vec3d endvec) {
			Vec3d vec = endvec.subtract(startvec);
			EntityLivingBase user = entity.getUser();
			int userid = user != null ? user.getEntityId() : -1;
			for (int i = 0; i < 50; i++) {
				Vec3d vec1 = vec.scale(entity.getRNG().nextDouble() * 0.4d + 0.6d);
				Particles.spawnParticle(entity.world, Particles.Types.SMOKE, startvec.x, startvec.y, startvec.z, 1,
				 0.08d, 0.2d, 0.08d, vec1.x, vec1.y, vec1.z, 0x206AD1FF, 40, 5, 0xF0, userid);
			}
		}
	}
}
