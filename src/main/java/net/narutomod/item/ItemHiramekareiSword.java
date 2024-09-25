
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.potion.PotionReach;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityChakraFlow;
import net.narutomod.entity.EntitySweep;
import net.narutomod.procedure.ProcedureUtils;
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
				double strength = cmp != null ? cmp.getDouble("strength") : 8d;
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
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP && entityLivingBase.getHeldItemMainhand().equals(itemstack)) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				EntityEffects entity1 = new EntityEffects(entity);
				world.spawnEntity(entity1);
				entity1.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hiramekarei_release")), 1.0F, 1.0f);
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
		public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
			return true;
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
		private int ticksSinceLastSwing;

		public EntityEffects(World a) {
			super(a);
		}

		public EntityEffects(EntityLivingBase user) {
			super(user);
		}

		@Override
		public boolean isUserHoldingWeapon() {
			EntityLivingBase user = this.getUser();
			return user != null && user.getHeldItemMainhand().getItem() == block;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				if (this.ticksExisted > this.duration || !this.isUserHoldingWeapon()) {
					this.setDead();
				} else {
					EntityLivingBase user = this.getUser();
					if (user instanceof EntityPlayer && user.swingProgressInt == 1) {
						double d = ProcedureUtils.getReachDistance(user);
						Vec3d vec = user.getPositionEyes(1f);
						for (int i = 2; i < 8; i++) {
							EntitySweep.Base sweepParticle = new EntitySweep.Base(user, 0xB06AD1FF, (float)d * 2 - 1.2f * i);
							sweepParticle.setLocationAndAngles(vec.x, vec.y, vec.z, user.rotationYaw, 0.0f);
							this.world.spawnEntity(sweepParticle);
						}
						float damage = (float)ProcedureUtils.getModifiedAttackDamage(user) * this.getCooledAttackStrength(user, 0.5f);
						this.ticksSinceLastSwing = 0;
						Entity directTarget = ProcedureUtils.objectEntityLookingAt(user, 4d, this).entityHit;
						for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, user.getEntityBoundingBox().grow(d, 0.25D, d))) {
							if (entity != user && !user.isOnSameTeam(entity) && user.getDistanceSq(entity) <= d * d) {
								entity.knockBack(user, 1.5F, MathHelper.sin(user.rotationYaw * 0.017453292F), -MathHelper.cos(user.rotationYaw * 0.017453292F));
								if (entity != directTarget) {
									entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)user), damage);
								}
							}
						}
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hiramekarei_release")), 1.0F, this.rand.nextFloat() * 0.3f + 1.2f);
					}
				}
			}
			++this.ticksSinceLastSwing;
		}

	    public float getCooledAttackStrength(EntityLivingBase entity, float adjustTicks) {
	    	float f = (float)(1.0D / ProcedureUtils.getAttackSpeed(entity) * 20.0D);
	        float f2 = MathHelper.clamp(((float)this.ticksSinceLastSwing + adjustTicks) / f, 0.0F, 1.0F);
	        return 0.2F + f2 * f2 * 0.8F;
	    }

		@Override
		protected void addEffects() {
		}

		@Override
		protected void removeEffects() {
		}

		private Random getRNG() {
			return this.rand;
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityEffects.class, renderManager -> new RenderEffects(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderEffects extends EntityChakraFlow.RenderCustom<EntityEffects> {
			public RenderEffects(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}
	
			@Override
			protected void spawnParticles(EntityLivingBase user, Vec3d startvec, Vec3d endvec, float partialTicks) {
				Vec3d vec = endvec.subtract(startvec);
				for (int i = 0; i < 50; i++) {
					Vec3d vec1 = vec.scale(user.getRNG().nextDouble() * 0.6667d + 1.0d);
					Particles.spawnParticle(user.world, Particles.Types.SMOKE, startvec.x, startvec.y, startvec.z, 1,
					 0.08d, 0.2d, 0.08d, vec1.x, vec1.y, vec1.z, 0x206AD1FF, 40, 5, 0xF0, user.getEntityId());
				}
			}
		}
	}
}
