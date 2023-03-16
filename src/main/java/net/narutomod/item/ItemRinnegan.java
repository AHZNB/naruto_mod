package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
//import net.minecraftforge.fml.common.event.FMLInitializationEvent;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.event.entity.living.LivingDamageEvent;
//import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.WorldServer;

import net.narutomod.gui.GuiNinjaScroll;
import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureRinneganHelmetTickEvent;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodModVariables;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemRinnegan extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:rinneganhelmet")
	public static final Item helmet = null;
	@ObjectHolder("narutomod:rinneganbody")
	public static final Item body = null;
	@ObjectHolder("narutomod:rinneganlegs")
	public static final Item legs = null;
	private static final String RINNESHARINGAN_KEY = NarutomodModVariables.RINNESHARINGAN_ACTIVATED;
	private final UUID RINNESHARINGAN_MODIFIER = UUID.fromString("135da083-a632-483e-85bd-2281f15ca7e0");
	private static final double SHINRATENSEI_CHAKRA_USAGE = 10d;
	private static final double CHIBAKUTENSEI_CHAKRA_USAGE = 5000d;
	private static final double NARAKAPATH_CHAKRA_USAGE = 100d;
	private static final double PRETAPATH_CHAKRA_USAGE = 10d;
	private static final double ANIMALPATH_CHAKRA_USAGE = 200d;
	private static final double OUTERPATH_CHAKRA_USAGE = 2000d;
	private static final double TENGAISHINSEI_CHAKRA_USAGE = 5000d;
	
	public ItemRinnegan(ElementsNarutomodMod instance) {
		super(instance, 20);
	}

	public static double getShinratenseiChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() instanceof ItemDojutsu.Base ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? SHINRATENSEI_CHAKRA_USAGE 
		 : SHINRATENSEI_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getChibaukutenseiChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() instanceof ItemDojutsu.Base ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? CHIBAKUTENSEI_CHAKRA_USAGE 
		 : CHIBAKUTENSEI_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getNarakaPathChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() instanceof ItemDojutsu.Base ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? NARAKAPATH_CHAKRA_USAGE 
		 : NARAKAPATH_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getPretaPathChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() instanceof ItemDojutsu.Base ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? PRETAPATH_CHAKRA_USAGE 
		 : PRETAPATH_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getAnimalPathChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() instanceof ItemDojutsu.Base ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? ANIMALPATH_CHAKRA_USAGE 
		 : ANIMALPATH_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getOuterPathChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() instanceof ItemDojutsu.Base ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? OUTERPATH_CHAKRA_USAGE 
		 : OUTERPATH_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getTengaishinseiChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() instanceof ItemDojutsu.Base ? ((ItemDojutsu.Base)helmet).isOwner(stack, entity) ? TENGAISHINSEI_CHAKRA_USAGE 
		 : TENGAISHINSEI_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("RINNEGAN", "narutomod:rinnegan_", 25, new int[]{2, 5, 6, 2}, 0, null, 5.0F);
		this.elements.items.add(() -> new ItemDojutsu.Base(enuma) {
			@SideOnly(Side.CLIENT)
			@Override
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ItemDojutsu.ClientModel.ModelHelmetSnug model = (ItemDojutsu.ClientModel.ModelHelmetSnug)super.getArmorModel(living, stack, slot, defaultModel);
				if (living.ticksExisted % 20 == 6) {
					model.foreheadHide = !isRinnesharinganActivated(stack) || !(living instanceof EntityPlayer) || PlayerTracker.getNinjaLevel((EntityPlayer)living) < 180d;
				}
				return model;
			}
			
			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				if (isRinnesharinganActivated(stack))
					return "narutomod:textures/rinnesharinganhelmet.png";
				return "narutomod:textures/rinneganhelmet.png";
			}

			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				super.onArmorTick(world, entity, itemstack);
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				{
					java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("x", x); 
					$_dependencies.put("y", y);
					$_dependencies.put("z", z);
					$_dependencies.put("itemstack", itemstack);
					$_dependencies.put("world", world);
					ProcedureRinneganHelmetTickEvent.executeProcedure($_dependencies);
				}
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (!world.isRemote && entity.ticksExisted % 20 == 0) {
					UUID uuid = ProcedureUtils.getUniqueId(itemstack, "KoH_id");
					if (uuid != null) {
						Entity koh = ((WorldServer)world).getEntityFromUuid(uuid); 
						if (!(koh instanceof EntityKingOfHell.EntityCustom) || !koh.isEntityAlive()) {
							ProcedureUtils.removeUniqueIdTag(itemstack, "KoH_id");
						}
					}
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer)entity;
						ItemStack helmetStack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
						GuiNinjaScroll.enableJutsu(player, (ItemJutsu.Base)ItemYoton.block,
						 ItemYoton.SEALING9D, helmetStack.getItem() == helmet);
						GuiNinjaScroll.enableJutsu(player, (ItemJutsu.Base)ItemYoton.block,
						 ItemYoton.SEALING10, helmetStack.getItem() == helmet && EntityTenTails.getBijuManager().isAddedToWorld(world));
						if (helmetStack.getItem() != helmet && helmetStack.getItem() != ItemTenseigan.helmet) {
							player.inventory.clearMatchingItems(ItemAsuraCanon.block, -1, -1, null);
						}
					}
				}
			}

			@Override
			public int getMaxDamage() {
				return 0;
			}

			@Override
			public boolean isDamageable() {
				return false;
			}

			@Override
			public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
				Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
				if (slot == EntityEquipmentSlot.HEAD && isRinnesharinganActivated(stack)) {
					multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(),
					 new AttributeModifier(RINNESHARINGAN_MODIFIER, "rinnesharingan.maxhealth", 380d, 0));
				}
				return multimap;
			}

			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				super.addInformation(stack, worldIn, tooltip, flagIn);
				if (isRinnesharinganActivated(stack)) {
					tooltip.add(TextFormatting.RED + I18n.translateToLocal("advancements.rinnesharinganactivated.title") + TextFormatting.WHITE);
				}
				tooltip.add(I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " + I18n.translateToLocal("chattext.shinratensei"));
				tooltip.add(I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + I18n.translateToLocal("tooltip.rinnegan.jutsu2") + " ("
						+ I18n.translateToLocal("tooltip.general.powerupkey") + ")");
				tooltip.add(I18n.translateToLocal("key.mcreator.specialjutsu3") + ": " + I18n.translateToLocal("tooltip.rinnegan.jutsu3"));
			}

			@Override
			public String getItemStackDisplayName(ItemStack stack) {
				return TextFormatting.LIGHT_PURPLE + super.getItemStackDisplayName(stack) + TextFormatting.WHITE;
			}
		}.setUnlocalizedName("rinneganhelmet").setRegistryName("rinneganhelmet").setCreativeTab(TabModTab.tab));

		this.elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST) {
			@SideOnly(Side.CLIENT)
			private ModelSizPathRobe armorModel;

			@SideOnly(Side.CLIENT)
			@Override
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelSizPathRobe();
				}
				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public int getMaxDamage() {
				return 0;
			}

			@Override
			public boolean isDamageable() {
				return false;
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (entity instanceof EntityPlayerMP && !wearingRinnesharingan((EntityPlayer)entity)) {
					((EntityPlayer)entity).inventory.clearMatchingItems(itemstack.getItem(), -1, -1, null);
				}
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/madara_jinchuriki.png";
			}
		}.setUnlocalizedName("rinneganbody").setRegistryName("rinneganbody").setCreativeTab(null));
		
		this.elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.LEGS) {
			private ModelSizPathRobe armorModel;

			@SideOnly(Side.CLIENT)
			@Override
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelSizPathRobe();
				}

				//armorModel.ball[0].showModel = living.getHeldItemMainhand().getItem() != ItemExpandedTruthSeekerBall.block;
				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public int getMaxDamage() {
				return 0;
			}

			@Override
			public boolean isDamageable() {
				return false;
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (entity instanceof EntityPlayerMP && !wearingRinnesharingan((EntityPlayer)entity)) {
					((EntityPlayer)entity).inventory.clearMatchingItems(itemstack.getItem(), -1, -1, null);
				}
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/madara_jinchuriki.png";
			}
		}.setUnlocalizedName("rinneganlegs").setRegistryName("rinneganlegs").setCreativeTab(null));
	}

	public static boolean isRinnesharinganActivated(ItemStack stack) {
		return (stack.hasTagCompound() && stack.getTagCompound().getBoolean(RINNESHARINGAN_KEY));
	}

	public static boolean wearingRinnegan(EntityLivingBase player) {
		return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == helmet;
	}
	
	public static boolean wearingRinnesharingan(EntityLivingBase player) {
		ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return (itemstack.getItem() == helmet || itemstack.getItem() == ItemTenseigan.helmet)
		 && isRinnesharinganActivated(itemstack);
	}

	public static boolean wearingRinnesharinganBody(EntityLivingBase player) {
		return player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == body;
	}

	public static boolean wearingRinnesharinganLeggings(EntityLivingBase player) {
		return player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == legs;
	}

	public static boolean hasRinnesharingan(EntityPlayer player) {
		ItemStack stack1 = ProcedureUtils.getMatchingItemStack(player, helmet);
		ItemStack stack2 = ProcedureUtils.getMatchingItemStack(player, ItemTenseigan.helmet);
		return (stack1 != null && isRinnesharinganActivated(stack1)) || (stack2 != null && isRinnesharinganActivated(stack2));
	}

	/*public class DamageHook {
		@SubscribeEvent
		public void onDamage(LivingDamageEvent event) {
			EntityLivingBase target = event.getEntityLiving();
			if (target instanceof EntityPlayer && hasRinnesharingan((EntityPlayer)target)) {
				event.setAmount(event.getAmount() * 0.1f);
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new DamageHook());
	}*/

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:rinneganhelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:rinneganbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:rinneganlegs", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public class ModelSizPathRobe extends ModelBiped {
		//private final ModelRenderer bipedBody;
		private final ModelRenderer robe;
		//private final ModelRenderer collar;
		//private final ModelRenderer bone6;
		private final ModelRenderer skirtRight;
		private final ModelRenderer bone2;
		private final ModelRenderer bone;
		private final ModelRenderer bone3;
		private final ModelRenderer bone4;
		private final ModelRenderer skirtLeft;
		private final ModelRenderer bone7;
		private final ModelRenderer bone8;
		private final ModelRenderer bone9;
		private final ModelRenderer bone10;
		//private final ModelRenderer bipedRightArm;
		private final ModelRenderer robeRightArm;
		//private final ModelRenderer bipedLeftArm;
		private final ModelRenderer robeLeftArm;
		//private final ModelRenderer bipedRightLeg;
		//private final ModelRenderer bipedLeftLeg;
	
		public ModelSizPathRobe() {
			textureWidth = 64;
			textureHeight = 64;
	
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.05F, false));
	
			robe = new ModelRenderer(this);
			robe.setRotationPoint(0.0F, 24.0F, 0.0F);
			bipedBody.addChild(robe);
			robe.cubeList.add(new ModelBox(robe, 16, 32, -4.0F, -24.0F, -2.0F, 8, 12, 4, 0.25F, false));
	
			//collar = new ModelRenderer(this);
			//collar.setRotationPoint(0.0F, -24.0F, -1.8F);
			//robe.addChild(collar);
			//setRotationAngle(collar, -0.3927F, 0.0F, 0.0F);
			//collar.cubeList.add(new ModelBox(collar, 24, 48, -4.0F, -4.0F, 0.0F, 8, 4, 4, 1.0F, false));
	
			//bone6 = new ModelRenderer(this);
			//bone6.setRotationPoint(0.0F, 1.0F, 3.0F);
			//collar.addChild(bone6);
			//setRotationAngle(bone6, -0.5236F, 0.0F, 0.0F);
			//bone6.cubeList.add(new ModelBox(bone6, 18, 1, -4.0F, -5.0F, -1.0F, 8, 4, 2, 1.0F, false));
	
			skirtRight = new ModelRenderer(this);
			skirtRight.setRotationPoint(0.0F, -0.25F, 0.0F);
			robe.addChild(skirtRight);
			
	
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, -12.0F, -2.0F);
			skirtRight.addChild(bone2);
			setRotationAngle(bone2, -0.1745F, 0.0F, 0.1745F);
			bone2.cubeList.add(new ModelBox(bone2, 0, 48, -4.0F, 0.0F, 0.0F, 4, 8, 0, 0.0F, false));
	
			bone = new ModelRenderer(this);
			bone.setRotationPoint(-4.0F, -12.25F, 0.0F);
			skirtRight.addChild(bone);
			setRotationAngle(bone, -0.1745F, 0.0F, 0.1745F);
			bone.cubeList.add(new ModelBox(bone, 8, 48, 0.0F, 0.0F, -2.1F, 0, 8, 4, 0.0F, false));
	
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(-4.0F, -12.25F, 0.0F);
			skirtRight.addChild(bone3);
			setRotationAngle(bone3, 0.1745F, 0.0F, 0.1745F);
			bone3.cubeList.add(new ModelBox(bone3, 16, 48, 0.0F, 0.0F, -1.85F, 0, 8, 4, 0.0F, false));
	
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(0.0F, -12.0F, 2.0F);
			skirtRight.addChild(bone4);
			setRotationAngle(bone4, 0.1745F, 0.0F, 0.1745F);
			bone4.cubeList.add(new ModelBox(bone4, 0, 56, -4.0F, 0.0F, 0.1F, 4, 8, 0, 0.0F, false));
	
			skirtLeft = new ModelRenderer(this);
			skirtLeft.setRotationPoint(0.0F, -0.25F, 0.0F);
			robe.addChild(skirtLeft);
			
	
			bone7 = new ModelRenderer(this);
			bone7.setRotationPoint(0.0F, -12.0F, -2.0F);
			skirtLeft.addChild(bone7);
			setRotationAngle(bone7, -0.1745F, 0.0F, -0.1745F);
			bone7.cubeList.add(new ModelBox(bone7, 0, 48, 0.0F, 0.0F, 0.0F, 4, 8, 0, 0.0F, true));
	
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(4.0F, -12.25F, 0.0F);
			skirtLeft.addChild(bone8);
			setRotationAngle(bone8, -0.1745F, 0.0F, -0.1745F);
			bone8.cubeList.add(new ModelBox(bone8, 8, 48, 0.0F, 0.0F, -2.1F, 0, 8, 4, 0.0F, true));
	
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(4.0F, -12.25F, 0.0F);
			skirtLeft.addChild(bone9);
			setRotationAngle(bone9, 0.1745F, 0.0F, -0.1745F);
			bone9.cubeList.add(new ModelBox(bone9, 16, 48, 0.0F, 0.0F, -1.85F, 0, 8, 4, 0.0F, true));
	
			bone10 = new ModelRenderer(this);
			bone10.setRotationPoint(0.0F, -12.0F, 2.1F);
			skirtLeft.addChild(bone10);
			setRotationAngle(bone10, 0.1745F, 0.0F, -0.1745F);
			bone10.cubeList.add(new ModelBox(bone10, 0, 56, 0.0F, 0.0F, 0.0F, 4, 8, 0, 0.0F, true));
	
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			setRotationAngle(bipedRightArm, -0.1745F, 0.0F, 0.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.05F, false));
	
			robeRightArm = new ModelRenderer(this);
			robeRightArm.setRotationPoint(5.0F, 22.0F, 0.0F);
			bipedRightArm.addChild(robeRightArm);
			robeRightArm.cubeList.add(new ModelBox(robeRightArm, 40, 32, -8.0F, -24.0F, -2.0F, 4, 12, 4, 0.3F, false));
	
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			setRotationAngle(bipedLeftArm, -0.1745F, 0.0F, 0.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 48, 0, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.05F, true));
	
			robeLeftArm = new ModelRenderer(this);
			robeLeftArm.setRotationPoint(-5.0F, 22.0F, 0.0F);
			bipedLeftArm.addChild(robeLeftArm);
			robeLeftArm.cubeList.add(new ModelBox(robeLeftArm, 40, 32, 4.0F, -24.0F, -2.0F, 4, 12, 4, 0.3F, true));
	
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			setRotationAngle(bipedRightLeg, 0.0F, 0.0F, 0.0349F);
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, false));
	
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			setRotationAngle(bipedLeftLeg, 0.0F, 0.0F, -0.0349F);
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, true));
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, true));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			if (entity instanceof AbstractClientPlayer) {
				if (((AbstractClientPlayer)entity).getSkinType().equals("slim")) {
					this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
					this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
				}
				boolean show = PlayerTracker.getNinjaLevel((AbstractClientPlayer)entity) >= 180d;
				robe.showModel = show;
				robeRightArm.showModel = show;
				robeLeftArm.showModel = show;
			}
			/*if (this.bipedLeftArm.showModel) {
				for (int i = 0; i < 9; i++) {
					ball[i].rotateAngleX = f2 + (float)i;
					ball[i].rotateAngleY = f2 + (float)i;
					ball[i].rotateAngleZ = f2 + (float)i;
				}
			}*/
			super.render(entity, f, f1, f2, f3, f4, f5);
		}
	
		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
