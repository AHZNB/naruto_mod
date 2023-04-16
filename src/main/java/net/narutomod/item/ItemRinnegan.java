package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
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
	protected static final UUID RINNESHARINGAN_MODIFIER = UUID.fromString("135da083-a632-483e-85bd-2281f15ca7e0");
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
		return stack.getItem() == helmet || stack.getItem() == ItemTenseigan.helmet
		 ? ((ItemDojutsu.Base)stack.getItem()).isOwner(stack, entity)
		  ? SHINRATENSEI_CHAKRA_USAGE : SHINRATENSEI_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getChibaukutenseiChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet || stack.getItem() == ItemTenseigan.helmet
		 ? ((ItemDojutsu.Base)stack.getItem()).isOwner(stack, entity)
		  ? CHIBAKUTENSEI_CHAKRA_USAGE : CHIBAKUTENSEI_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getNarakaPathChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet || stack.getItem() == ItemTenseigan.helmet
		 ? ((ItemDojutsu.Base)stack.getItem()).isOwner(stack, entity)
		  ? NARAKAPATH_CHAKRA_USAGE : NARAKAPATH_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getPretaPathChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet || stack.getItem() == ItemTenseigan.helmet
		 ? ((ItemDojutsu.Base)stack.getItem()).isOwner(stack, entity)
		  ? PRETAPATH_CHAKRA_USAGE : PRETAPATH_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getAnimalPathChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet || stack.getItem() == ItemTenseigan.helmet
		 ? ((ItemDojutsu.Base)stack.getItem()).isOwner(stack, entity)
		  ? ANIMALPATH_CHAKRA_USAGE : ANIMALPATH_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getOuterPathChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet || stack.getItem() == ItemTenseigan.helmet
		 ? ((ItemDojutsu.Base)stack.getItem()).isOwner(stack, entity)
		  ? OUTERPATH_CHAKRA_USAGE : OUTERPATH_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public static double getTengaishinseiChakraUsage(EntityLivingBase entity) {
		ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		return stack.getItem() == helmet || stack.getItem() == ItemTenseigan.helmet
		 ? ((ItemDojutsu.Base)stack.getItem()).isOwner(stack, entity)
		  ? TENGAISHINSEI_CHAKRA_USAGE : TENGAISHINSEI_CHAKRA_USAGE * 2 : (Double.MAX_VALUE * 0.001d);
	}

	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("RINNEGAN", "narutomod:rinnegan_", 25, new int[]{2, 5, 6, 2}, 0,
		 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:dojutsu")), 5.0F);
		this.elements.items.add(() -> new ItemDojutsu.Base(enuma) {
			@SideOnly(Side.CLIENT)
			@Override
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ItemDojutsu.ClientModel.ModelHelmetSnug model = (ItemDojutsu.ClientModel.ModelHelmetSnug)super.getArmorModel(living, stack, slot, defaultModel);
				model.hornMiddle.showModel = false;
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
		private final ModelRenderer backSpikes;
		private final ModelRenderer bone11;
		private final ModelRenderer bone12;
		private final ModelRenderer bone13;
		private final ModelRenderer bone14;
		private final ModelRenderer bone15;
		private final ModelRenderer bone16;
		private final ModelRenderer bone17;
		private final ModelRenderer bone18;
		private final ModelRenderer bone19;
		private final ModelRenderer bone20;
		private final ModelRenderer bone21;
		private final ModelRenderer bone22;
		private final ModelRenderer bone23;
		private final ModelRenderer bone24;
		private final ModelRenderer bone25;
		private final ModelRenderer bone26;
		private final ModelRenderer bone27;
		private final ModelRenderer bone28;
		private final ModelRenderer bone29;
		private final ModelRenderer bone30;
		private final ModelRenderer bone31;
		private final ModelRenderer bone32;
		private final ModelRenderer bone33;
		private final ModelRenderer bone34;
		private final ModelRenderer bone47;
		private final ModelRenderer bone48;
		private final ModelRenderer bone49;
		private final ModelRenderer bone50;
		private final ModelRenderer bone51;
		private final ModelRenderer bone52;
		private final ModelRenderer bone53;
		private final ModelRenderer bone54;
		private final ModelRenderer bone55;
		private final ModelRenderer bone56;
		private final ModelRenderer bone57;
		private final ModelRenderer bone58;
		private final ModelRenderer bone35;
		private final ModelRenderer bone36;
		private final ModelRenderer bone37;
		private final ModelRenderer bone38;
		private final ModelRenderer bone39;
		private final ModelRenderer bone40;
		private final ModelRenderer bone41;
		private final ModelRenderer bone42;
		private final ModelRenderer bone43;
		private final ModelRenderer bone44;
		private final ModelRenderer bone45;
		private final ModelRenderer bone46;
		private final ModelRenderer bone59;
		private final ModelRenderer bone60;
		private final ModelRenderer bone61;
		private final ModelRenderer bone62;
		private final ModelRenderer bone63;
		private final ModelRenderer bone64;
		private final ModelRenderer bone65;
		private final ModelRenderer bone66;
		private final ModelRenderer bone67;
		private final ModelRenderer bone68;
		private final ModelRenderer bone69;
		private final ModelRenderer bone70;
		private final ModelRenderer bone71;
		private final ModelRenderer bone72;
		private final ModelRenderer bone73;
		private final ModelRenderer bone74;
		private final ModelRenderer bone75;
		private final ModelRenderer bone76;
		private final ModelRenderer bone77;
		private final ModelRenderer bone78;
		private final ModelRenderer bone79;
		private final ModelRenderer bone80;
		private final ModelRenderer bone5;
		private final ModelRenderer bone81;
		private final ModelRenderer bone82;
		private final ModelRenderer bone83;
		private final ModelRenderer bone84;
		private final ModelRenderer bone85;
		private final ModelRenderer bone86;
		private final ModelRenderer bone87;
		private final ModelRenderer bone88;
		private final ModelRenderer bone89;
		private final ModelRenderer bone100;
		private final ModelRenderer bone101;
		private final ModelRenderer bone102;
		private final ModelRenderer bone103;
		private final ModelRenderer bone104;
		private final ModelRenderer bone105;
		private final ModelRenderer bone106;
		private final ModelRenderer bone107;
		private final ModelRenderer bone108;
		private final ModelRenderer bone109;
		private final ModelRenderer bone90;
		private final ModelRenderer bone91;
		private final ModelRenderer bone92;
		private final ModelRenderer bone93;
		private final ModelRenderer bone94;
		private final ModelRenderer bone95;
		private final ModelRenderer bone96;
		private final ModelRenderer bone97;
		private final ModelRenderer bone98;
		private final ModelRenderer bone99;
		private final ModelRenderer bone110;
		private final ModelRenderer bone111;
		private final ModelRenderer bone112;
		private final ModelRenderer bone113;
		private final ModelRenderer bone114;
		private final ModelRenderer bone115;
		private final ModelRenderer bone116;
		private final ModelRenderer bone117;
		private final ModelRenderer bone118;
		private final ModelRenderer bone119;
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
	
			backSpikes = new ModelRenderer(this);
			backSpikes.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.addChild(backSpikes);
			
	
			bone11 = new ModelRenderer(this);
			bone11.setRotationPoint(0.0F, 1.5F, -1.0F);
			backSpikes.addChild(bone11);
			setRotationAngle(bone11, -1.0472F, 0.0F, 0.0F);
			bone11.cubeList.add(new ModelBox(bone11, 60, 16, -0.5F, -4.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone12 = new ModelRenderer(this);
			bone12.setRotationPoint(0.0F, -3.75F, 0.0F);
			bone11.addChild(bone12);
			bone12.cubeList.add(new ModelBox(bone12, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, false));
	
			bone13 = new ModelRenderer(this);
			bone13.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone12.addChild(bone13);
			bone13.cubeList.add(new ModelBox(bone13, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
	
			bone14 = new ModelRenderer(this);
			bone14.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone13.addChild(bone14);
			bone14.cubeList.add(new ModelBox(bone14, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, false));
	
			bone15 = new ModelRenderer(this);
			bone15.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone14.addChild(bone15);
			bone15.cubeList.add(new ModelBox(bone15, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, false));
	
			bone16 = new ModelRenderer(this);
			bone16.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone15.addChild(bone16);
			setRotationAngle(bone16, 0.0436F, 0.0F, 0.0F);
			bone16.cubeList.add(new ModelBox(bone16, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, false));
	
			bone17 = new ModelRenderer(this);
			bone17.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone16.addChild(bone17);
			setRotationAngle(bone17, 0.0436F, 0.0F, 0.0F);
			bone17.cubeList.add(new ModelBox(bone17, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
	
			bone18 = new ModelRenderer(this);
			bone18.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone17.addChild(bone18);
			setRotationAngle(bone18, 0.0436F, 0.0F, 0.0F);
			bone18.cubeList.add(new ModelBox(bone18, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone19 = new ModelRenderer(this);
			bone19.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone18.addChild(bone19);
			setRotationAngle(bone19, 0.0436F, 0.0F, 0.0F);
			bone19.cubeList.add(new ModelBox(bone19, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
	
			bone20 = new ModelRenderer(this);
			bone20.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone19.addChild(bone20);
			setRotationAngle(bone20, 0.0436F, 0.0F, 0.0F);
			bone20.cubeList.add(new ModelBox(bone20, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.2F, false));
	
			bone21 = new ModelRenderer(this);
			bone21.setRotationPoint(0.0F, -0.4F, 0.0F);
			bone20.addChild(bone21);
			setRotationAngle(bone21, 0.0436F, 0.0F, 0.0F);
			bone21.cubeList.add(new ModelBox(bone21, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, false));
	
			bone22 = new ModelRenderer(this);
			bone22.setRotationPoint(0.0F, -0.25F, 0.0F);
			bone21.addChild(bone22);
			setRotationAngle(bone22, 0.0436F, 0.0F, 0.0F);
			bone22.cubeList.add(new ModelBox(bone22, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, false));
	
			bone23 = new ModelRenderer(this);
			bone23.setRotationPoint(-0.25F, 2.5F, -0.75F);
			backSpikes.addChild(bone23);
			setRotationAngle(bone23, -1.1345F, -0.5236F, 0.0F);
			bone23.cubeList.add(new ModelBox(bone23, 60, 16, -0.5F, -4.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone24 = new ModelRenderer(this);
			bone24.setRotationPoint(0.0F, -3.75F, 0.0F);
			bone23.addChild(bone24);
			bone24.cubeList.add(new ModelBox(bone24, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, false));
	
			bone25 = new ModelRenderer(this);
			bone25.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone24.addChild(bone25);
			bone25.cubeList.add(new ModelBox(bone25, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
	
			bone26 = new ModelRenderer(this);
			bone26.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone25.addChild(bone26);
			bone26.cubeList.add(new ModelBox(bone26, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, false));
	
			bone27 = new ModelRenderer(this);
			bone27.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone26.addChild(bone27);
			bone27.cubeList.add(new ModelBox(bone27, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, false));
	
			bone28 = new ModelRenderer(this);
			bone28.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone27.addChild(bone28);
			setRotationAngle(bone28, 0.0436F, 0.0F, 0.0F);
			bone28.cubeList.add(new ModelBox(bone28, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, false));
	
			bone29 = new ModelRenderer(this);
			bone29.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone28.addChild(bone29);
			setRotationAngle(bone29, 0.0436F, 0.0F, 0.0F);
			bone29.cubeList.add(new ModelBox(bone29, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
	
			bone30 = new ModelRenderer(this);
			bone30.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone29.addChild(bone30);
			setRotationAngle(bone30, 0.0436F, 0.0F, 0.0F);
			bone30.cubeList.add(new ModelBox(bone30, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone31 = new ModelRenderer(this);
			bone31.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone30.addChild(bone31);
			setRotationAngle(bone31, 0.0436F, 0.0F, 0.0F);
			bone31.cubeList.add(new ModelBox(bone31, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
	
			bone32 = new ModelRenderer(this);
			bone32.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone31.addChild(bone32);
			setRotationAngle(bone32, 0.0436F, 0.0F, 0.0F);
			bone32.cubeList.add(new ModelBox(bone32, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.2F, false));
	
			bone33 = new ModelRenderer(this);
			bone33.setRotationPoint(0.0F, -0.4F, 0.0F);
			bone32.addChild(bone33);
			setRotationAngle(bone33, 0.0436F, 0.0F, 0.0F);
			bone33.cubeList.add(new ModelBox(bone33, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, false));
	
			bone34 = new ModelRenderer(this);
			bone34.setRotationPoint(0.0F, -0.25F, 0.0F);
			bone33.addChild(bone34);
			setRotationAngle(bone34, 0.0436F, 0.0F, 0.0F);
			bone34.cubeList.add(new ModelBox(bone34, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, false));
	
			bone47 = new ModelRenderer(this);
			bone47.setRotationPoint(0.25F, 2.5F, -0.75F);
			backSpikes.addChild(bone47);
			setRotationAngle(bone47, -1.1345F, 0.5236F, 0.0F);
			bone47.cubeList.add(new ModelBox(bone47, 60, 16, -0.5F, -4.0F, -0.5F, 1, 1, 1, 0.0F, true));
	
			bone48 = new ModelRenderer(this);
			bone48.setRotationPoint(0.0F, -3.75F, 0.0F);
			bone47.addChild(bone48);
			bone48.cubeList.add(new ModelBox(bone48, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, true));
	
			bone49 = new ModelRenderer(this);
			bone49.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone48.addChild(bone49);
			bone49.cubeList.add(new ModelBox(bone49, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
	
			bone50 = new ModelRenderer(this);
			bone50.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone49.addChild(bone50);
			bone50.cubeList.add(new ModelBox(bone50, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, true));
	
			bone51 = new ModelRenderer(this);
			bone51.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone50.addChild(bone51);
			bone51.cubeList.add(new ModelBox(bone51, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, true));
	
			bone52 = new ModelRenderer(this);
			bone52.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone51.addChild(bone52);
			setRotationAngle(bone52, 0.0436F, 0.0F, 0.0F);
			bone52.cubeList.add(new ModelBox(bone52, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, true));
	
			bone53 = new ModelRenderer(this);
			bone53.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone52.addChild(bone53);
			setRotationAngle(bone53, 0.0436F, 0.0F, 0.0F);
			bone53.cubeList.add(new ModelBox(bone53, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
	
			bone54 = new ModelRenderer(this);
			bone54.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone53.addChild(bone54);
			setRotationAngle(bone54, 0.0436F, 0.0F, 0.0F);
			bone54.cubeList.add(new ModelBox(bone54, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, true));
	
			bone55 = new ModelRenderer(this);
			bone55.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone54.addChild(bone55);
			setRotationAngle(bone55, 0.0436F, 0.0F, 0.0F);
			bone55.cubeList.add(new ModelBox(bone55, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
	
			bone56 = new ModelRenderer(this);
			bone56.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone55.addChild(bone56);
			setRotationAngle(bone56, 0.0436F, 0.0F, 0.0F);
			bone56.cubeList.add(new ModelBox(bone56, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.2F, true));
	
			bone57 = new ModelRenderer(this);
			bone57.setRotationPoint(0.0F, -0.4F, 0.0F);
			bone56.addChild(bone57);
			setRotationAngle(bone57, 0.0436F, 0.0F, 0.0F);
			bone57.cubeList.add(new ModelBox(bone57, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, true));
	
			bone58 = new ModelRenderer(this);
			bone58.setRotationPoint(0.0F, -0.25F, 0.0F);
			bone57.addChild(bone58);
			setRotationAngle(bone58, 0.0436F, 0.0F, 0.0F);
			bone58.cubeList.add(new ModelBox(bone58, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, true));
	
			bone35 = new ModelRenderer(this);
			bone35.setRotationPoint(-1.0F, 3.5F, -0.5F);
			backSpikes.addChild(bone35);
			setRotationAngle(bone35, -1.2217F, -0.829F, 0.0F);
			bone35.cubeList.add(new ModelBox(bone35, 60, 16, -0.5F, -4.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone36 = new ModelRenderer(this);
			bone36.setRotationPoint(0.0F, -3.75F, 0.0F);
			bone35.addChild(bone36);
			bone36.cubeList.add(new ModelBox(bone36, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, false));
	
			bone37 = new ModelRenderer(this);
			bone37.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone36.addChild(bone37);
			bone37.cubeList.add(new ModelBox(bone37, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
	
			bone38 = new ModelRenderer(this);
			bone38.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone37.addChild(bone38);
			bone38.cubeList.add(new ModelBox(bone38, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, false));
	
			bone39 = new ModelRenderer(this);
			bone39.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone38.addChild(bone39);
			bone39.cubeList.add(new ModelBox(bone39, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, false));
	
			bone40 = new ModelRenderer(this);
			bone40.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone39.addChild(bone40);
			setRotationAngle(bone40, 0.0436F, 0.0F, 0.0F);
			bone40.cubeList.add(new ModelBox(bone40, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, false));
	
			bone41 = new ModelRenderer(this);
			bone41.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone40.addChild(bone41);
			setRotationAngle(bone41, 0.0436F, 0.0F, 0.0F);
			bone41.cubeList.add(new ModelBox(bone41, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
	
			bone42 = new ModelRenderer(this);
			bone42.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone41.addChild(bone42);
			setRotationAngle(bone42, 0.0436F, 0.0F, 0.0F);
			bone42.cubeList.add(new ModelBox(bone42, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone43 = new ModelRenderer(this);
			bone43.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone42.addChild(bone43);
			setRotationAngle(bone43, 0.0436F, 0.0F, 0.0F);
			bone43.cubeList.add(new ModelBox(bone43, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
	
			bone44 = new ModelRenderer(this);
			bone44.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone43.addChild(bone44);
			setRotationAngle(bone44, 0.0436F, 0.0F, 0.0F);
			bone44.cubeList.add(new ModelBox(bone44, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.2F, false));
	
			bone45 = new ModelRenderer(this);
			bone45.setRotationPoint(0.0F, -0.4F, 0.0F);
			bone44.addChild(bone45);
			setRotationAngle(bone45, 0.0436F, 0.0F, 0.0F);
			bone45.cubeList.add(new ModelBox(bone45, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, false));
	
			bone46 = new ModelRenderer(this);
			bone46.setRotationPoint(0.0F, -0.25F, 0.0F);
			bone45.addChild(bone46);
			setRotationAngle(bone46, 0.0436F, 0.0F, 0.0F);
			bone46.cubeList.add(new ModelBox(bone46, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, false));
	
			bone59 = new ModelRenderer(this);
			bone59.setRotationPoint(1.0F, 3.5F, -0.5F);
			backSpikes.addChild(bone59);
			setRotationAngle(bone59, -1.2217F, 0.829F, 0.0F);
			bone59.cubeList.add(new ModelBox(bone59, 60, 16, -0.5F, -4.0F, -0.5F, 1, 1, 1, 0.0F, true));
	
			bone60 = new ModelRenderer(this);
			bone60.setRotationPoint(0.0F, -3.75F, 0.0F);
			bone59.addChild(bone60);
			bone60.cubeList.add(new ModelBox(bone60, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, true));
	
			bone61 = new ModelRenderer(this);
			bone61.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone60.addChild(bone61);
			bone61.cubeList.add(new ModelBox(bone61, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
	
			bone62 = new ModelRenderer(this);
			bone62.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone61.addChild(bone62);
			bone62.cubeList.add(new ModelBox(bone62, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, true));
	
			bone63 = new ModelRenderer(this);
			bone63.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone62.addChild(bone63);
			bone63.cubeList.add(new ModelBox(bone63, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, true));
	
			bone64 = new ModelRenderer(this);
			bone64.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone63.addChild(bone64);
			setRotationAngle(bone64, 0.0436F, 0.0F, 0.0F);
			bone64.cubeList.add(new ModelBox(bone64, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.15F, true));
	
			bone65 = new ModelRenderer(this);
			bone65.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone64.addChild(bone65);
			setRotationAngle(bone65, 0.0436F, 0.0F, 0.0F);
			bone65.cubeList.add(new ModelBox(bone65, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
	
			bone66 = new ModelRenderer(this);
			bone66.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone65.addChild(bone66);
			setRotationAngle(bone66, 0.0436F, 0.0F, 0.0F);
			bone66.cubeList.add(new ModelBox(bone66, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, true));
	
			bone67 = new ModelRenderer(this);
			bone67.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone66.addChild(bone67);
			setRotationAngle(bone67, 0.0436F, 0.0F, 0.0F);
			bone67.cubeList.add(new ModelBox(bone67, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
	
			bone68 = new ModelRenderer(this);
			bone68.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone67.addChild(bone68);
			setRotationAngle(bone68, 0.0436F, 0.0F, 0.0F);
			bone68.cubeList.add(new ModelBox(bone68, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.2F, true));
	
			bone69 = new ModelRenderer(this);
			bone69.setRotationPoint(0.0F, -0.4F, 0.0F);
			bone68.addChild(bone69);
			setRotationAngle(bone69, 0.0436F, 0.0F, 0.0F);
			bone69.cubeList.add(new ModelBox(bone69, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, true));
	
			bone70 = new ModelRenderer(this);
			bone70.setRotationPoint(0.0F, -0.25F, 0.0F);
			bone69.addChild(bone70);
			setRotationAngle(bone70, 0.0436F, 0.0F, 0.0F);
			bone70.cubeList.add(new ModelBox(bone70, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, true));
	
			bone71 = new ModelRenderer(this);
			bone71.setRotationPoint(0.0F, 9.25F, 1.5F);
			backSpikes.addChild(bone71);
			setRotationAngle(bone71, -2.2689F, 0.0F, 0.0F);
			bone71.cubeList.add(new ModelBox(bone71, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.05F, false));
	
			bone72 = new ModelRenderer(this);
			bone72.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone71.addChild(bone72);
			bone72.cubeList.add(new ModelBox(bone72, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone73 = new ModelRenderer(this);
			bone73.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone72.addChild(bone73);
			bone73.cubeList.add(new ModelBox(bone73, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, false));
	
			bone74 = new ModelRenderer(this);
			bone74.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone73.addChild(bone74);
			bone74.cubeList.add(new ModelBox(bone74, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
	
			bone75 = new ModelRenderer(this);
			bone75.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone74.addChild(bone75);
			setRotationAngle(bone75, -0.0436F, 0.0F, 0.0F);
			bone75.cubeList.add(new ModelBox(bone75, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, false));
	
			bone76 = new ModelRenderer(this);
			bone76.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone75.addChild(bone76);
			setRotationAngle(bone76, -0.0436F, 0.0F, 0.0F);
			bone76.cubeList.add(new ModelBox(bone76, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone77 = new ModelRenderer(this);
			bone77.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone76.addChild(bone77);
			setRotationAngle(bone77, -0.0436F, 0.0F, 0.0F);
			bone77.cubeList.add(new ModelBox(bone77, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
	
			bone78 = new ModelRenderer(this);
			bone78.setRotationPoint(0.0F, -0.55F, 0.0F);
			bone77.addChild(bone78);
			setRotationAngle(bone78, -0.0436F, 0.0F, 0.0F);
			bone78.cubeList.add(new ModelBox(bone78, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
	
			bone79 = new ModelRenderer(this);
			bone79.setRotationPoint(0.0F, -0.65F, 0.0F);
			bone78.addChild(bone79);
			setRotationAngle(bone79, -0.0436F, 0.0F, 0.0F);
			bone79.cubeList.add(new ModelBox(bone79, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, false));
	
			bone80 = new ModelRenderer(this);
			bone80.setRotationPoint(0.0F, -0.3F, 0.0F);
			bone79.addChild(bone80);
			setRotationAngle(bone80, -0.0436F, 0.0F, 0.0F);
			bone80.cubeList.add(new ModelBox(bone80, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, false));
	
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(-1.5F, 9.25F, 1.5F);
			backSpikes.addChild(bone5);
			setRotationAngle(bone5, -2.0944F, -0.3927F, 0.0F);
			bone5.cubeList.add(new ModelBox(bone5, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.05F, false));
	
			bone81 = new ModelRenderer(this);
			bone81.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone5.addChild(bone81);
			bone81.cubeList.add(new ModelBox(bone81, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone82 = new ModelRenderer(this);
			bone82.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone81.addChild(bone82);
			bone82.cubeList.add(new ModelBox(bone82, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, false));
	
			bone83 = new ModelRenderer(this);
			bone83.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone82.addChild(bone83);
			bone83.cubeList.add(new ModelBox(bone83, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
	
			bone84 = new ModelRenderer(this);
			bone84.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone83.addChild(bone84);
			setRotationAngle(bone84, -0.0436F, 0.0F, 0.0F);
			bone84.cubeList.add(new ModelBox(bone84, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, false));
	
			bone85 = new ModelRenderer(this);
			bone85.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone84.addChild(bone85);
			setRotationAngle(bone85, -0.0436F, 0.0F, 0.0F);
			bone85.cubeList.add(new ModelBox(bone85, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone86 = new ModelRenderer(this);
			bone86.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone85.addChild(bone86);
			setRotationAngle(bone86, -0.0436F, 0.0F, 0.0F);
			bone86.cubeList.add(new ModelBox(bone86, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
	
			bone87 = new ModelRenderer(this);
			bone87.setRotationPoint(0.0F, -0.55F, 0.0F);
			bone86.addChild(bone87);
			setRotationAngle(bone87, -0.0436F, 0.0F, 0.0F);
			bone87.cubeList.add(new ModelBox(bone87, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
	
			bone88 = new ModelRenderer(this);
			bone88.setRotationPoint(0.0F, -0.65F, 0.0F);
			bone87.addChild(bone88);
			setRotationAngle(bone88, -0.0436F, 0.0F, 0.0F);
			bone88.cubeList.add(new ModelBox(bone88, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, false));
	
			bone89 = new ModelRenderer(this);
			bone89.setRotationPoint(0.0F, -0.3F, 0.0F);
			bone88.addChild(bone89);
			setRotationAngle(bone89, -0.0436F, 0.0F, 0.0F);
			bone89.cubeList.add(new ModelBox(bone89, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, false));
	
			bone100 = new ModelRenderer(this);
			bone100.setRotationPoint(1.5F, 9.25F, 1.5F);
			backSpikes.addChild(bone100);
			setRotationAngle(bone100, -2.0944F, 0.3927F, 0.0F);
			bone100.cubeList.add(new ModelBox(bone100, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.05F, true));
	
			bone101 = new ModelRenderer(this);
			bone101.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone100.addChild(bone101);
			bone101.cubeList.add(new ModelBox(bone101, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, true));
	
			bone102 = new ModelRenderer(this);
			bone102.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone101.addChild(bone102);
			bone102.cubeList.add(new ModelBox(bone102, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, true));
	
			bone103 = new ModelRenderer(this);
			bone103.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone102.addChild(bone103);
			bone103.cubeList.add(new ModelBox(bone103, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
	
			bone104 = new ModelRenderer(this);
			bone104.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone103.addChild(bone104);
			setRotationAngle(bone104, -0.0436F, 0.0F, 0.0F);
			bone104.cubeList.add(new ModelBox(bone104, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, true));
	
			bone105 = new ModelRenderer(this);
			bone105.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone104.addChild(bone105);
			setRotationAngle(bone105, -0.0436F, 0.0F, 0.0F);
			bone105.cubeList.add(new ModelBox(bone105, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, true));
	
			bone106 = new ModelRenderer(this);
			bone106.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone105.addChild(bone106);
			setRotationAngle(bone106, -0.0436F, 0.0F, 0.0F);
			bone106.cubeList.add(new ModelBox(bone106, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
	
			bone107 = new ModelRenderer(this);
			bone107.setRotationPoint(0.0F, -0.55F, 0.0F);
			bone106.addChild(bone107);
			setRotationAngle(bone107, -0.0436F, 0.0F, 0.0F);
			bone107.cubeList.add(new ModelBox(bone107, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
	
			bone108 = new ModelRenderer(this);
			bone108.setRotationPoint(0.0F, -0.65F, 0.0F);
			bone107.addChild(bone108);
			setRotationAngle(bone108, -0.0436F, 0.0F, 0.0F);
			bone108.cubeList.add(new ModelBox(bone108, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, true));
	
			bone109 = new ModelRenderer(this);
			bone109.setRotationPoint(0.0F, -0.3F, 0.0F);
			bone108.addChild(bone109);
			setRotationAngle(bone109, -0.0436F, 0.0F, 0.0F);
			bone109.cubeList.add(new ModelBox(bone109, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, true));
	
			bone90 = new ModelRenderer(this);
			bone90.setRotationPoint(-3.0F, 9.25F, 1.5F);
			backSpikes.addChild(bone90);
			setRotationAngle(bone90, -1.9199F, -0.7854F, 0.0F);
			bone90.cubeList.add(new ModelBox(bone90, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.05F, false));
	
			bone91 = new ModelRenderer(this);
			bone91.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone90.addChild(bone91);
			bone91.cubeList.add(new ModelBox(bone91, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone92 = new ModelRenderer(this);
			bone92.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone91.addChild(bone92);
			bone92.cubeList.add(new ModelBox(bone92, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, false));
	
			bone93 = new ModelRenderer(this);
			bone93.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone92.addChild(bone93);
			bone93.cubeList.add(new ModelBox(bone93, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
	
			bone94 = new ModelRenderer(this);
			bone94.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone93.addChild(bone94);
			setRotationAngle(bone94, -0.0436F, 0.0F, 0.0F);
			bone94.cubeList.add(new ModelBox(bone94, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, false));
	
			bone95 = new ModelRenderer(this);
			bone95.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone94.addChild(bone95);
			setRotationAngle(bone95, -0.0436F, 0.0F, 0.0F);
			bone95.cubeList.add(new ModelBox(bone95, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
	
			bone96 = new ModelRenderer(this);
			bone96.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone95.addChild(bone96);
			setRotationAngle(bone96, -0.0436F, 0.0F, 0.0F);
			bone96.cubeList.add(new ModelBox(bone96, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, false));
	
			bone97 = new ModelRenderer(this);
			bone97.setRotationPoint(0.0F, -0.55F, 0.0F);
			bone96.addChild(bone97);
			setRotationAngle(bone97, -0.0436F, 0.0F, 0.0F);
			bone97.cubeList.add(new ModelBox(bone97, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, false));
	
			bone98 = new ModelRenderer(this);
			bone98.setRotationPoint(0.0F, -0.65F, 0.0F);
			bone97.addChild(bone98);
			setRotationAngle(bone98, -0.0436F, 0.0F, 0.0F);
			bone98.cubeList.add(new ModelBox(bone98, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, false));
	
			bone99 = new ModelRenderer(this);
			bone99.setRotationPoint(0.0F, -0.3F, 0.0F);
			bone98.addChild(bone99);
			setRotationAngle(bone99, -0.0436F, 0.0F, 0.0F);
			bone99.cubeList.add(new ModelBox(bone99, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, false));
	
			bone110 = new ModelRenderer(this);
			bone110.setRotationPoint(3.0F, 9.25F, 1.5F);
			backSpikes.addChild(bone110);
			setRotationAngle(bone110, -1.9199F, 0.7854F, 0.0F);
			bone110.cubeList.add(new ModelBox(bone110, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.05F, true));
	
			bone111 = new ModelRenderer(this);
			bone111.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone110.addChild(bone111);
			bone111.cubeList.add(new ModelBox(bone111, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, true));
	
			bone112 = new ModelRenderer(this);
			bone112.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone111.addChild(bone112);
			bone112.cubeList.add(new ModelBox(bone112, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, true));
	
			bone113 = new ModelRenderer(this);
			bone113.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone112.addChild(bone113);
			bone113.cubeList.add(new ModelBox(bone113, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
	
			bone114 = new ModelRenderer(this);
			bone114.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone113.addChild(bone114);
			setRotationAngle(bone114, -0.0436F, 0.0F, 0.0F);
			bone114.cubeList.add(new ModelBox(bone114, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.05F, true));
	
			bone115 = new ModelRenderer(this);
			bone115.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone114.addChild(bone115);
			setRotationAngle(bone115, -0.0436F, 0.0F, 0.0F);
			bone115.cubeList.add(new ModelBox(bone115, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, true));
	
			bone116 = new ModelRenderer(this);
			bone116.setRotationPoint(0.0F, -0.75F, 0.0F);
			bone115.addChild(bone116);
			setRotationAngle(bone116, -0.0436F, 0.0F, 0.0F);
			bone116.cubeList.add(new ModelBox(bone116, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.1F, true));
	
			bone117 = new ModelRenderer(this);
			bone117.setRotationPoint(0.0F, -0.55F, 0.0F);
			bone116.addChild(bone117);
			setRotationAngle(bone117, -0.0436F, 0.0F, 0.0F);
			bone117.cubeList.add(new ModelBox(bone117, 60, 16, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.2F, true));
	
			bone118 = new ModelRenderer(this);
			bone118.setRotationPoint(0.0F, -0.65F, 0.0F);
			bone117.addChild(bone118);
			setRotationAngle(bone118, -0.0436F, 0.0F, 0.0F);
			bone118.cubeList.add(new ModelBox(bone118, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.3F, true));
	
			bone119 = new ModelRenderer(this);
			bone119.setRotationPoint(0.0F, -0.3F, 0.0F);
			bone118.addChild(bone119);
			setRotationAngle(bone119, -0.0436F, 0.0F, 0.0F);
			bone119.cubeList.add(new ModelBox(bone119, 60, 16, -0.5F, -0.75F, -0.5F, 1, 1, 1, -0.4F, true));
	
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
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, false));
	
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			setRotationAngle(bipedLeftLeg, 0.0F, 0.0F, -0.0349F);
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, true));
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.5F, true));
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
				backSpikes.showModel = bipedRightArm.showModel && !show;
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
