
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.entity.EntityBijuManager;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityRasengan;
import net.narutomod.entity.EntityRasenshuriken;
import net.narutomod.entity.EntityBuddha1000;
import net.narutomod.entity.EntitySnake8Heads;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.gui.overlay.OverlayChakraDisplay;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.UUID;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSenjutsu extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:senjutsu")
	public static final Item block = null;
	public static final int ENTITYID = 355;
	private static final String SAGEMODEACTIVATEDKEY = "SageModeActivated";
	private static final String SAGECHAKRADEPLETIONAMOUNT = "SageChakraDepletionAmount";
	public static final ItemJutsu.JutsuEnum SAGEMODE = new ItemJutsu.JutsuEnum(0, "tooltip.senjutsu.sagemode", 'S', 10d, new SageMode());
	public static final ItemJutsu.JutsuEnum RASENGAN = new ItemJutsu.JutsuEnum(1, "tooltip.senjutsu.rasengan", 'S', ItemNinjutsu.RASENGAN.chakraUsage, new EntityRasengan.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum RASENSHURIKEN = new ItemJutsu.JutsuEnum(2, "tooltip.senjutsu.rasenshuriken", 'S', ItemFuton.RASENSHURIKEN.chakraUsage, new EntityRasenshuriken.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WOODBUDDHA = new ItemJutsu.JutsuEnum(3, "buddha_1000", 'S', 5000d, new EntityBuddha1000.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SNAKE8H = new ItemJutsu.JutsuEnum(4, "snake_8_heads", 'S', 3000d, new EntitySnake8Heads.EC.Jutsu());
	private static final Random RAND = new Random();

	public ItemSenjutsu(ElementsNarutomodMod instance) {
		super(instance, 710);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(SAGEMODE, RASENGAN, RASENSHURIKEN, WOODBUDDHA, SNAKE8H));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntitySitPlatform.class)
		 .id(new ResourceLocation("narutomod", "entitybulletsenjutsu"), ENTITYID).name("entitybulletsenjutsu").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:senjutsu", "inventory"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EventHook());
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
	}

	public static class RangedItem extends ItemJutsu.Base implements ItemOnBody.Interface {
		private static final String TYPEKEY = "SageType";
		private static final Map<IAttribute, AttributeModifier> buffMap = ImmutableMap.<IAttribute, AttributeModifier>builder()
			.put(EntityPlayer.REACH_DISTANCE, new AttributeModifier(UUID.fromString("c3ee1250-8b80-4668-b58a-33af5ea73ee6"), "sagemode.reach", 2.0d, 0))
			.put(SharedMonsterAttributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("6d6202e1-9aac-4c3d-ba0c-6684bdd58868"), "sagemode.damage", 60.0d, 0))
			.put(SharedMonsterAttributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("33b7fa14-828a-4964-b014-b61863526589"), "sagemode.damagespeed", 2.0d, 1))
			.put(SharedMonsterAttributes.MOVEMENT_SPEED, new AttributeModifier(UUID.fromString("74f3ab51-a73f-45e3-a4c4-aae6974b6414"), "sagemode.movement", 1.5d, 1))
			.put(SharedMonsterAttributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("70e0acc2-cf75-4bbd-a21a-753088324a59"), "sagemode.health", 80.0d, 0))
			.build();

		@SideOnly(Side.CLIENT)
		private ModelBiped armorModel;

		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.SENJUTSU, list);
			this.setUnlocalizedName("senjutsu");
			this.setRegistryName("senjutsu");
			this.setCreativeTab(TabModTab.tab);
			//this.defaultCooldownMap[SAGEMODE.index] = 0;
		}

		public void setSageType(ItemStack stack, Type type) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger(TYPEKEY, type.getID());
		}

		public Type getSageType(ItemStack stack) {
			return stack.hasTagCompound() ? Type.getTypeFromId(stack.getTagCompound().getInteger(TYPEKEY)) : Type.NONE;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			float f = super.getMaxPower(stack, entity);
			if (jutsu == RASENGAN) {
				return Math.min(f, 6.0f);
			} else if (jutsu == RASENSHURIKEN) {
				return Math.min(f, 6.0f);
			}
			return Math.min(f, 100.0f);
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == RASENGAN) {
				return this.getPower(stack, entity, timeLeft, 2.9f, 200f);
			} else if (jutsu == RASENSHURIKEN) {
				return this.getPower(stack, entity, timeLeft, 1.9f, 300f);
			} else if (jutsu == SAGEMODE) {
				return this.getPower(stack, entity, timeLeft, 0f, 20f);
			}
			return 1f;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityLivingBase) {
				Type sageType = this.getSageType(itemstack);
				if (sageType == Type.NONE) {
					Type forcedType = itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Type", 8)
					 ? Type.getTypeFromName(itemstack.getTagCompound().getString("Type")) 
					 : entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative() ? Type.random() : Type.NONE;
					if (forcedType != Type.NONE) {
						this.setSageType(itemstack, forcedType);
						this.enableJutsu(itemstack, SAGEMODE, true);
						sageType = forcedType;
					} else {
						return;
					}
				}
				EntityLivingBase living = (EntityLivingBase)entity;
				boolean flag = isSageModeActivated(itemstack);
				boolean flag1 = living.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).hasModifier(buffMap.get(SharedMonsterAttributes.MAX_HEALTH));
				if (flag && !flag1) {
					for (Map.Entry<IAttribute, AttributeModifier> entry : buffMap.entrySet()) {
						IAttributeInstance attr = living.getEntityAttribute(entry.getKey());
						if (attr != null) {
							attr.applyModifier(entry.getValue());
						}
					}
					if (entity instanceof EntityPlayer) {
						itemstack.getTagCompound().setInteger("prevFoodStat", ((EntityPlayer)entity).getFoodStats().getFoodLevel());
					}
				} else if (!flag && flag1) {
					for (Map.Entry<IAttribute, AttributeModifier> entry : buffMap.entrySet()) {
						IAttributeInstance attr = living.getEntityAttribute(entry.getKey());
						if (attr != null) {
							attr.removeModifier(entry.getValue().getID());
						}
					}
					if (entity instanceof EntityPlayer) {
						((EntityPlayer)entity).getFoodStats().setFoodLevel(itemstack.getTagCompound().getInteger("prevFoodStat") - 5);
					}
				}
				if (flag) {
					Chakra.Pathway cp = Chakra.pathway(living);
					if (cp.getAmount() < itemstack.getTagCompound().getDouble(SAGECHAKRADEPLETIONAMOUNT)) {
						deactivateSageMode(itemstack, living);
					} else if (living.ticksExisted % 20 == 10) {
						living.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 22, 0, false, false));
						cp.consume(50d);
					}
				}
				if (entity.ticksExisted % 40 == 5 && entity instanceof EntityPlayer) {
					ItemStack stack1 = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemNinjutsu.block);
					this.enableJutsu(itemstack, RASENGAN,
					 stack1 != null && ((ItemNinjutsu.RangedItem)stack1.getItem()).canUseJutsu(stack1, ItemNinjutsu.RASENGAN, living));
					stack1 = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemFuton.block);
					this.enableJutsu(itemstack, RASENSHURIKEN,
					 stack1 != null && ((ItemFuton.RangedItem)stack1.getItem()).canUseJutsu(stack1, ItemFuton.RASENSHURIKEN, living));
					stack1 = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemMokuton.block);
					this.enableJutsu(itemstack, WOODBUDDHA,
					 stack1 != null && ((ItemMokuton.ItemCustom)stack1.getItem()).canUseJutsu(stack1, ItemMokuton.GOLEM, living));
					this.enableJutsu(itemstack, SNAKE8H, sageType == Type.SNAKE);
				}
			}
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
			if (this.getCurrentJutsu(itemstack) == SAGEMODE) {
				if (!world.isRemote && entity.getRidingEntity() instanceof EntitySitPlatform) {
					entity.dismountRidingEntity();
					super.onPlayerStoppedUsing(itemstack, world, entity, timeLeft);
				}
			} else {
				super.onPlayerStoppedUsing(itemstack, world, entity, timeLeft);
			}
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			if (!player.world.isRemote && this.getCurrentJutsu(stack) == SAGEMODE
			 && !(player.getRidingEntity() instanceof EntitySitPlatform)) {
				player.resetActiveHand();
			} else {
				super.onUsingTick(stack, player, timeLeft);
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ItemStack stack = entity.getHeldItem(hand);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == SAGEMODE && isSageModeActivated(stack)) {
				//deactivateSageMode(stack, entity);
				return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
			}
			if (jutsu != SAGEMODE && !isSageModeActivated(stack)) {
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			}
			ActionResult<ItemStack> res = super.onItemRightClick(world, entity, hand);
			if (jutsu == SAGEMODE && res.getType() == EnumActionResult.SUCCESS && !world.isRemote) {
				if (EntityBijuManager.cloakLevel(entity) > 0 && EntityBijuManager.getCloakXp(entity, 1) < 800) {
					entity.sendStatusMessage(new TextComponentTranslation("chattext.senjutsu.denied", 
				 	 EntityBijuManager.getNameOfJinchurikisBiju(entity)), true);
					return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
				}
				entity.world.spawnEntity(new EntitySitPlatform(entity));
			}
			return res;
		}

		@Override
		public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
			if (attacker.equals(target) && this.getSageType(itemstack) == Type.TOAD && isSageModeActivated(itemstack)) {
				target = ProcedureUtils.objectEntityLookingAt(attacker, ProcedureUtils.getReachDistance(attacker), 3d).entityHit;
				if (target == null) {
					target = ProcedureUtils.objectEntityLookingAt(attacker, ProcedureUtils.getReachDistance(attacker), 4.5d).entityHit;
					if (target != null) {
						attacker.attackTargetEntityWithCurrentItem(target);
					}
				}
			}
			return super.onLeftClickEntity(itemstack, attacker, target);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			Type type = this.getSageType(itemstack);
			if (type != Type.NONE) {
				list.add(TextFormatting.BLUE + new TextComponentTranslation("tooltip.senjutsu.type").getUnformattedComponentText()
				 + type.getLocalizedName() + TextFormatting.RESET);
			}
			super.addInformation(itemstack, world, list, flag);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
			if (isSageModeActivated(stack)) {
				if (this.armorModel == null) {
					this.armorModel = new Renderer().new ModelHelmetSnug();
				}
				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}
			return null;
		}

		@Override
		public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
			switch (this.getSageType(stack)) {
				case TOAD:
					return "narutomod:textures/sagetoadhelmet.png";
				case SNAKE:
					return "narutomod:textures/sagesnakehelmet.png";
				case SLUG:
					return "narutomod:textures/sageslughelmet.png";
				default:
					return null;
			}
		}

		@Override
		public boolean showSkinLayer() {
			return true;
		}

		@Override
		public ItemOnBody.BodyPart showOnBody() {
			return ItemOnBody.BodyPart.NONE;
		}
	}

	public static boolean isSageModeActivated(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean(SAGEMODEACTIVATEDKEY);
	}

	public static boolean isSageModeActivated(EntityPlayer entity) {
		ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, block);
		return stack != null ? isSageModeActivated(stack) : false;
	}

	public static void deactivateSageMode(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, block);
			if (stack != null && isSageModeActivated(stack)) {
				deactivateSageMode(stack, entity);
			}
		}
	}

	private static void deactivateSageMode(ItemStack stack, EntityLivingBase entity) {
		if (stack.hasTagCompound()) {
			Chakra.Pathway cp = Chakra.pathway(entity);
			double d = stack.getTagCompound().getDouble(SAGECHAKRADEPLETIONAMOUNT);
			if (d > 0.0d && cp.getAmount() > d) {
				cp.consume(cp.getAmount() - d);
			}
			stack.getTagCompound().removeTag(SAGEMODEACTIVATEDKEY);
			stack.getTagCompound().removeTag(SAGECHAKRADEPLETIONAMOUNT);
		}
		if (entity instanceof EntityPlayerMP) {
			OverlayChakraDisplay.ShowFlamesMessage.send((EntityPlayerMP)entity, false);
		}
	}

	public static class EventHook {
		@SubscribeEvent
		public void onDeath(LivingDeathEvent event) {
			deactivateSageMode(event.getEntityLiving());
		}

		@SubscribeEvent
		public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.player.world.isRemote) {
				deactivateSageMode(event.player);
			}
		}
	}

	public static class SageMode implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (power >= 100.0f) {
				Chakra.Pathway cp = Chakra.pathway(entity);
				stack.getTagCompound().setDouble(SAGECHAKRADEPLETIONAMOUNT, cp.getAmount());
				float f = stack.getItem() == block && ((RangedItem)stack.getItem()).getCurrentJutsu(stack) == SAGEMODE
				 ? ((RangedItem)stack.getItem()).getCurrentJutsuXpModifier(stack, entity) : 1.0f;
				cp.consume(-0.6f / f, true);
				stack.getTagCompound().setBoolean(SAGEMODEACTIVATEDKEY, true);
				if (entity instanceof EntityPlayerMP) {
					OverlayChakraDisplay.ShowFlamesMessage.send((EntityPlayerMP)entity, true);
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean isActivated(ItemStack stack) {
			return isSageModeActivated(stack);
		}
	}

	public static class EntitySitPlatform extends Entity {
		public EntitySitPlatform(World w) {
			super(w);
			this.setSize(1.0f, 0.01f);
		}

		public EntitySitPlatform(EntityLivingBase entity) {
			this(entity.world);
			this.setLocationAndAngles(entity.posX, entity.posY+0.1d, entity.posZ, entity.rotationYaw, 0f);
			entity.startRiding(this);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public double getMountedYOffset() {
			return -0.25d;
		}

		@Override
		public void onUpdate() {
			this.move(MoverType.SELF, 0.0d, this.motionY, 0.0d);
			this.motionY = this.onGround ? 0.0D : this.motionY - 0.08D;
			this.motionY *= 0.98D;
			if (!this.world.isRemote && !this.isBeingRidden()) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}

	public enum Type {
		NONE("none", 0),
		TOAD("toad", 1),
		SNAKE("snake", 2),
		SLUG("slug", 3);

		private final String name;
		private final int id;
		private static final Map<Integer, Type> TYPES_BY_ID = Maps.newHashMap();
		private static final Map<String, Type> TYPES_BY_NAME = Maps.newHashMap();

		static {
			for (Type type : values()) {
				TYPES_BY_ID.put(Integer.valueOf(type.getID()), type);
				TYPES_BY_NAME.put(type.name, type);
			}
		}
		
		Type(String s, int i) {
			this.name = s;
			this.id = i;
		}

		public String getLocalizedName() {
			return new TextComponentTranslation("entity."+this.name+".name").getUnformattedComponentText();
		}

		public int getID() {
			return this.id;
		}

		public static Type random() {
			return getTypeFromId(1 + RAND.nextInt(3));
		}

		public static Type getTypeFromId(int id) {
			return TYPES_BY_ID.containsKey(Integer.valueOf(id)) ? TYPES_BY_ID.get(Integer.valueOf(id)) : Type.NONE;
		}

		public static Type getTypeFromName(String s) {
			return TYPES_BY_NAME.containsKey(s) ? TYPES_BY_NAME.get(s) : Type.NONE;
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
			RenderingRegistry.registerEntityRenderingHandler(EntitySitPlatform.class, renderManager -> {
				return new Render(renderManager) {
					@Override
					public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
					}
					@Override
					protected ResourceLocation getEntityTexture(Entity entity) {
						return null;
					}
				};
			});
		}

		@SideOnly(Side.CLIENT)
		public class ModelHelmetSnug extends ModelBiped {
			private final ModelRenderer highlight;
	
			public ModelHelmetSnug() {
				textureWidth = 64;
				textureHeight = 16;
				this.bipedHead = new ModelRenderer(this);
				this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.02F, false));
				this.highlight = new ModelRenderer(this);
				this.highlight.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.highlight.cubeList.add(new ModelBox(this.highlight, 24, 0, -4.0F, -8.0F, -4.15F, 8, 8, 0, 0.0F, false));
				this.bipedHeadwear = new ModelRenderer(this);
				this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F, false));
			}
	
			@Override
			public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				this.bipedBody.showModel = false;
				this.bipedLeftArm.showModel = false;
				this.bipedLeftLeg.showModel = false;
				this.bipedRightArm.showModel = false;
				this.bipedRightLeg.showModel = false;
				GlStateManager.enableBlend();
				super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
				GlStateManager.disableBlend();
			}
		}
	}
}
