
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
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.SoundEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumHandSide;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;

import net.narutomod.entity.EntityLightningArc;
import net.narutomod.entity.EntityFalseDarkness;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.Random;
import com.google.common.collect.Multimap;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKibaBlades extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:kiba_blades")
	public static final Item block = null;
	public static final int ENTITYID = 317;

	public ItemKibaBlades(ElementsNarutomodMod instance) {
		super(instance, 632);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletkiba_blades"), ENTITYID).name("entitybulletkiba_blades").tracker(64, 1, true)
				.build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kiba_blades", "inventory"));
	}

	public static void setAsMain(ItemStack stack) {
		if (stack.getItem() == block) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound compound = new NBTTagCompound();
			long l = new Random().nextLong();
			compound.setLong("id", l);
			stack.getTagCompound().setTag("id", compound);
			stack.getTagCompound().setInteger("id1", compound.hashCode());
		}
	}

	public static class RangedItem extends Item implements ItemOnBody.Interface {
		public RangedItem() {
			super();
			this.setMaxDamage(0);
			this.setFull3D();
			this.setUnlocalizedName("kiba_blades");
			this.setRegistryName("kiba_blades");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", 12d, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
			}
			return multimap;
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.kibablades.general"));
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
			if (!world.isRemote && this.getMaxItemUseDuration(itemstack) - timeLeft >= 20) {
				new EntityFalseDarkness.EC.Jutsu().createJutsu(itemstack, entity, 2f);
			}
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			if (this.getMaxItemUseDuration(stack) - timeLeft >= 20) {
				if (player.world.isRemote) {
					Particles.spawnParticle(player.world, Particles.Types.SMOKE, player.posX, player.posY, player.posZ, 
					 40, 0.2d, 0d, 0.2d, 0d, 0.5d, 0d, 0x206AD1FF, 40, 5, 0xF0, player.getEntityId());
				}
				if (timeLeft % 10 == 0) {
					player.world.playSound(null, player.posX, player.posY, player.posZ,
					 (net.minecraft.util.SoundEvent)net.minecraft.util.SoundEvent.REGISTRY
					 .getObject(new ResourceLocation("narutomod:charging_chakra")),
					 net.minecraft.util.SoundCategory.PLAYERS, 0.05F, itemRand.nextFloat() + 0.5F);
				}
			}
		}

		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			if (this.itemRand.nextInt(5) == 0) {
				EntityLightningArc.onStruck(target, ItemJutsu.causeJutsuDamage(attacker, attacker), 2f);
			}
			return true;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int inventorySlot, boolean isCurrentItem) {
			super.onUpdate(itemstack, world, entity, inventorySlot, isCurrentItem);
			if (!world.isRemote && entity instanceof EntityLivingBase) {
				if (entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative() && isCurrentItem && !this.isMain(itemstack)) {
					setAsMain(itemstack);
				}
				ItemStack mainhandStack = ((EntityLivingBase)entity).getHeldItemMainhand();
				ItemStack offhandStack = ((EntityLivingBase)entity).getHeldItemOffhand();
				boolean inMainHand = mainhandStack.equals(itemstack);
				boolean inOffHand = offhandStack.equals(itemstack);
				boolean ismain = this.isMain(itemstack);
//System.out.println(">>>>>> inMainHand:"+inMainHand+", inOffHand:"+inOffHand+", slot:"+inventorySlot+", isCurrentItem:"+isCurrentItem+", isMain:"+ismain);
				if (inMainHand) {
					EntityCustom entity1 = this.getEntity(itemstack, world);
					if (entity1 == null) {
						entity1 = new EntityCustom((EntityLivingBase)entity);
						world.spawnEntity(entity1);
						this.setEntity(itemstack, entity1);
					}
					if (entity instanceof EntityPlayer && offhandStack.getItem() != block) {
						if (ismain) {
							ProcedureUtils.swapItemToSlot((EntityPlayer)entity, EntityEquipmentSlot.OFFHAND, new ItemStack(block));
						} else {
							itemstack.shrink(1);
						}
					}
				} else if (inOffHand) {
					if (entity instanceof EntityPlayer) {
						if (ismain) {
							ProcedureUtils.swapItemToSlot((EntityPlayer)entity, EntityEquipmentSlot.MAINHAND, itemstack);
						} else if (mainhandStack.getItem() != block) {
							itemstack.shrink(1);
						}
					}
				} else if (!ismain) {
					itemstack.shrink(1);
				}
			}
		}

		private void setEntity(ItemStack itemstack, EntityCustom entity) {
			if (!itemstack.hasTagCompound()) {
				itemstack.setTagCompound(new NBTTagCompound());
			}
			itemstack.getTagCompound().setInteger("BladeEntityId", entity.getEntityId());
		}

		@Nullable
		public EntityCustom getEntity(ItemStack itemstack, World world) {
			if (itemstack.hasTagCompound()) {
				Entity entity = world.getEntityByID(itemstack.getTagCompound().getInteger("BladeEntityId"));
				return (entity instanceof EntityCustom && entity.isEntityAlive()) ? (EntityCustom) entity : null;
			}
			return null;
		}

		private boolean isMain(ItemStack stack) {
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("id", 10)) {
				NBTTagCompound compound = stack.getTagCompound().getCompoundTag("id");
//System.out.println("++++++ hash:"+compound.hashCode()+", id1:"+stack.getTagCompound().getInteger("id1"));
				return compound.hashCode() == stack.getTagCompound().getInteger("id1");
			}
			return false;
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
			if (!this.world.isRemote && (this.summoner == null || !this.isHoldingWeapon(EnumHand.MAIN_HAND))) {
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
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
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
					boolean flag2 = entity.isHoldingWeapon(EnumHand.OFF_HAND);
					if (flag1 && entity.getRNG().nextFloat() < 0.01f) {
						Vec3d vec0 = this.transform3rdPerson(new Vec3d(0d, -0.725d, 0.2d), mainarmAngles, user, mainhandside);
						Vec3d vec1 = this.transform3rdPerson(new Vec3d(0d, -0.725d, 1.6d), mainarmAngles, user, mainhandside)
								.subtract(vec0).scale(0.2);
						vec0 = vec0.add(vec1);
						EntityLightningArc.spawnAsParticle(entity.world, vec0.x, vec0.y, vec0.z, 0.01d, vec1.x, vec1.y, vec1.z);
					}
					if (flag2 && entity.getRNG().nextFloat() < 0.01f) {
						Vec3d vec0 = this.transform3rdPerson(new Vec3d(0d, -0.725d, 0.2d), offarmAngles, user, mainhandside.opposite());
						Vec3d vec1 = this.transform3rdPerson(new Vec3d(0d, -0.725d, 1.6d), offarmAngles, user, mainhandside.opposite())
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
}
