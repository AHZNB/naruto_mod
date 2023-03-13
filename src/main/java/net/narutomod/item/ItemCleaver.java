
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityLightningArc;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.Random;
import javax.annotation.Nullable;
import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemCleaver extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:cleaver")
	public static final Item block = null;
	public static final int ENTITYID = 33;

	public ItemCleaver(ElementsNarutomodMod instance) {
		super(instance, 780);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "entity_cleaver"), ENTITYID).name("entity_cleaver").tracker(64, 1, true)
				.build());
	}

	public static class ItemCustom extends ItemSword implements ItemOnBody.Interface {
		public ItemCustom() {
			super(EnumHelper.addToolMaterial("CLEAVER", 1, 1000, 8f, 11f, 0));
			this.setUnlocalizedName("cleaver");
			this.setRegistryName("cleaver");
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.getAttackDamage(), 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.0, 0));
			}
			return multimap;
		}

		public Set<String> getToolClasses(ItemStack stack) {
			HashMap<String, Integer> ret = new HashMap<String, Integer>();
			ret.put("sword", 1);
			return ret.keySet();
		}

		@Override
		public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
			return (repair.getItem() == new ItemStack(Items.IRON_INGOT, (int) (1)).getItem());
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.cleaver.descr"));
		}

		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			if (this.canUseRaiton(attacker) && this.itemRand.nextInt(3) == 0) {
				EntityLightningArc.onStruck(target, ItemJutsu.causeJutsuDamage(attacker, attacker), 2f);
			}
			return super.hitEntity(stack, target, attacker);
		}

		private boolean canUseRaiton(EntityLivingBase entity) {
			if (entity instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemRaiton.block);
				return stack != null && (((EntityPlayer)entity).isCreative() || ((ItemJutsu.Base)stack.getItem()).canUseAnyJutsu(stack));
			}
			return false;
		}
	
		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityLivingBase) {
				if (((EntityLivingBase)entity).getHeldItemMainhand().equals(itemstack)) {
					boolean flag = this.canUseRaiton((EntityLivingBase)entity);
					EntityCustom entity1 = this.getEntity(itemstack, world);
					if (entity1 == null && flag) {
						entity1 = new EntityCustom((EntityLivingBase)entity);
						world.spawnEntity(entity1);
						this.setEntity(itemstack, entity1);
					} else if (entity1 != null && !flag) {
						entity1.setDead();
						this.setEntity(itemstack, null);
					}
				}
			}
		}

		private void setEntity(ItemStack itemstack, @Nullable EntityCustom entity) {
			if (!itemstack.hasTagCompound()) {
				itemstack.setTagCompound(new NBTTagCompound());
			}
			if (entity == null) {
				itemstack.getTagCompound().removeTag("BladeEntityId");
			} else {
				itemstack.getTagCompound().setInteger("BladeEntityId", entity.getEntityId());
			}
		}
	
		@Nullable
		public EntityCustom getEntity(ItemStack itemstack, World world) {
			if (itemstack.hasTagCompound()) {
				Entity entity = world.getEntityByID(itemstack.getTagCompound().getInteger("BladeEntityId"));
				return (entity instanceof EntityCustom && entity.isEntityAlive()) ? (EntityCustom) entity : null;
			}
			return null;
		}
	
		@Override
		public boolean isShield(ItemStack stack, EntityLivingBase entity) {
			return stack.getItem() == block;
		}
	
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			entity.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, entity.getHeldItem(hand));
		}
	
		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BLOCK;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:cleaver", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
	}

	public static class EntityCustom extends Entity {
		private static final DataParameter<Integer> OWNER_ID = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		protected EntityLivingBase summoner;

		public EntityCustom(World a) {
			super(a);
		}

		protected EntityCustom(EntityLivingBase summonerIn) {
			this(summonerIn.world);
			this.setOwner(summonerIn);
			this.setPositionToSummoner();
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(OWNER_ID, Integer.valueOf(0));
		}

		public EntityLivingBase getOwner() {
			if (!this.world.isRemote) {
				return this.summoner;
			}
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(OWNER_ID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		protected void setOwner(EntityLivingBase entity) {
			this.getDataManager().set(OWNER_ID, Integer.valueOf(entity.getEntityId()));
			this.summoner = entity;
		}

		public boolean isHoldingWeapon(EnumHand hand) {
			return this.getOwner() != null && this.getOwner().getHeldItem(hand).getItem() == block;
		}

		@Override
		public void onUpdate() {
			if (this.summoner != null) {
				this.setPositionToSummoner();
			}
			if (!this.world.isRemote && (this.getOwner() == null || !this.isHoldingWeapon(EnumHand.MAIN_HAND))) {
				this.setDead();
			} else if (this.rand.nextFloat() < 0.01f) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:electricity")),
				  0.1f, this.rand.nextFloat() * 0.5f + 0.4f);
			}
		}

		protected void setPositionToSummoner() {
			EntityLivingBase entity = this.summoner;
			this.setPosition(entity.posX, entity.posY, entity.posZ);
		}

		public Random getRNG() {
			return this.rand;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EntityCustom> {
		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}

		private Vec3d transform3rdPerson(Vec3d startvec, Vec3d angles, EntityLivingBase entity, EnumHandSide side) {
			return ProcedureUtils.rotateRoll(startvec, (float)-angles.z)
			   .rotateYaw((float)-angles.y).rotatePitch((float)-angles.x)
			   .addVector(0.0625F * (side==EnumHandSide.RIGHT?-5:5), 1.5F-(entity.isSneaking()?0.3f:0f), -0.05F)
			   .rotateYaw(-entity.renderYawOffset * (float)(Math.PI / 180d))
			   .addVector(entity.posX, entity.posY, entity.posZ);
		}

		@Override
		public void doRender(EntityCustom entity, double x, double y, double z, float f, float partialTicks) {
			EntityLivingBase user = entity.getOwner();
			if (user != null) {
				RenderLivingBase<?> renderer = (RenderLivingBase<?>)this.renderManager.getEntityRenderObject(user);
				ModelRenderer rightarmModel = ((ModelBiped)renderer.getMainModel()).bipedRightArm;
				Vec3d rightarmAngles = new Vec3d(rightarmModel.rotateAngleX, rightarmModel.rotateAngleY, rightarmModel.rotateAngleZ);
				ModelRenderer leftarmModel = ((ModelBiped)renderer.getMainModel()).bipedLeftArm;
				Vec3d leftarmAngles = new Vec3d(leftarmModel.rotateAngleX, leftarmModel.rotateAngleY, leftarmModel.rotateAngleZ);
				EnumHandSide mainhandside = user.getPrimaryHand();
				Vec3d mainarmAngles = mainhandside == EnumHandSide.RIGHT ? rightarmAngles : leftarmAngles;
				Vec3d offarmAngles = mainhandside == EnumHandSide.RIGHT ? leftarmAngles : rightarmAngles;
				boolean flag1 = entity.isHoldingWeapon(EnumHand.MAIN_HAND);
				if (flag1 && entity.getRNG().nextFloat() < 0.01f) {
					Vec3d vec0 = this.transform3rdPerson(new Vec3d(0d, -1.0d, 0.2d), mainarmAngles, user, mainhandside);
					Vec3d vec1 = this.transform3rdPerson(new Vec3d(0d, -1.0d, 2.0d), mainarmAngles, user, mainhandside)
					 .subtract(vec0).scale(0.2);
					vec0 = vec0.add(vec1);
					EntityLightningArc.spawnAsParticle(entity.world, vec0.x, vec0.y, vec0.z, 0.01d, vec1.x, vec1.y, vec1.z);
				}
			}
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return null;
		}
	}
}
