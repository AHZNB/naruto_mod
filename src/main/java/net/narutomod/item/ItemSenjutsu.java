
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

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
import java.util.Map;
import java.util.Random;
import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSenjutsu extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:senjutsu")
	public static final Item block = null;
	public static final int ENTITYID = 355;
	private static final String SAGEMODEACTIVATEDKEY = "SageModeActivated";
	private static final String SAGECHAKRADEPLETIONAMOUNT = "SageChakraDepletionAmount";
	public static final ItemJutsu.JutsuEnum SAGEMODE = new ItemJutsu.JutsuEnum(0, "item.sage_mode_armorhelmet.name", 'S', 10d, new SageMode());
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

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
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

	@Override
	public void init(FMLInitializationEvent event) {
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
	}

	public static class RangedItem extends ItemJutsu.Base implements ItemOnBody.Interface {
		private static final String TYPEKEY = "SageType";

		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.SENJUTSU, list);
			this.setUnlocalizedName("senjutsu");
			this.setRegistryName("senjutsu");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[SAGEMODE.index] = 0;
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
				return Math.min(f, 7.0f);
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
			if (this.getSageType(itemstack) == Type.NONE) {
				if (entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative()) {
					this.setSageType(itemstack, Type.random());
				} else {
					return;
				}
			}
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase)entity;
				boolean flag = isSageModeActivated(itemstack);
				boolean flag1 = living.getEntityAttribute(EntityPlayer.REACH_DISTANCE).hasModifier(ItemSageModeArmor.buffMap.get(EntityPlayer.REACH_DISTANCE));
				if (flag && !flag1) {
					for (Map.Entry<IAttribute, AttributeModifier> entry : ItemSageModeArmor.buffMap.entrySet()) {
						living.getEntityAttribute(entry.getKey()).applyModifier(entry.getValue());
					}
					if (entity instanceof EntityPlayer) {
						itemstack.getTagCompound().setInteger("prevFoodStat", ((EntityPlayer)entity).getFoodStats().getFoodLevel());
					}
				} else if (!flag && flag1) {
					for (Map.Entry<IAttribute, AttributeModifier> entry : ItemSageModeArmor.buffMap.entrySet()) {
						living.getEntityAttribute(entry.getKey()).removeModifier(entry.getValue().getID());
					}
					if (entity instanceof EntityPlayer) {
						((EntityPlayer)entity).getFoodStats().setFoodLevel(itemstack.getTagCompound().getInteger("prevFoodStat") - 5);
					}
				}
				if (flag) {
					living.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 3, 0, false, false));
					Chakra.Pathway cp = Chakra.pathway(living);
					if (cp.getAmount() < itemstack.getTagCompound().getDouble(SAGECHAKRADEPLETIONAMOUNT)) {
						deactivateSageMode(itemstack, living);
					} else if (living.ticksExisted % 20 == 10) {
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
			}
			super.onUsingTick(stack, player, timeLeft);
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
				entity.world.spawnEntity(new EntitySitPlatform(entity));
			}
			return res;
		}

		@Override
		public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
			if (attacker.equals(target) && this.getSageType(itemstack) == Type.TOAD && isSageModeActivated(itemstack)) {
				target = ProcedureUtils.objectEntityLookingAt(attacker, ProcedureUtils.getReachDistance(attacker), 2d).entityHit;
				if (target != null) {
					attacker.attackTargetEntityWithCurrentItem(target);
				}
			}
			return super.onLeftClickEntity(itemstack, attacker, target);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			list.add(TextFormatting.BLUE + new TextComponentTranslation("tooltip.senjutsu.type").getUnformattedComponentText()
			 + this.getSageType(itemstack).getName() + TextFormatting.RESET);
			super.addInformation(itemstack, world, list, flag);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
			if (isSageModeActivated(stack)) {
				ModelBiped armorModel = new ModelHelmetSnug();
				armorModel.isSneak = living.isSneaking();
				armorModel.isRiding = living.isRiding();
				armorModel.isChild = living.isChild();
				return armorModel;
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
					return "narutomod:textures/sagewoodhelmet.png";
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

	private static void deactivateSageMode(ItemStack stack, EntityLivingBase entity) {
		if (stack.hasTagCompound()) {
			stack.getTagCompound().setBoolean(SAGEMODEACTIVATEDKEY, false);
			stack.getTagCompound().setDouble(SAGECHAKRADEPLETIONAMOUNT, 0.0d);
		}
		if (entity instanceof EntityPlayerMP) {
			OverlayChakraDisplay.ShowFlamesMessage.send((EntityPlayerMP)entity, false);
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
		TOAD("entity.toad.name", 1),
		SNAKE("entity.snake.name", 2),
		SLUG("entity.slug.name", 3);

		private final String name;
		private final int id;
		private static final Map<Integer, Type> TYPES = Maps.newHashMap();

		static {
			for (Type type : values())
				TYPES.put(Integer.valueOf(type.getID()), type);
		}
		
		Type(String s, int i) {
			this.name = s;
			this.id = i;
		}

		public String getName() {
			return new TextComponentTranslation(this.name).getUnformattedComponentText();
		}

		public int getID() {
			return this.id;
		}

		public static Type random() {
			return getTypeFromId(1 + RAND.nextInt(3));
		}

		public static Type getTypeFromId(int id) {
			return TYPES.get(Integer.valueOf(id));
		}
	}

	@SideOnly(Side.CLIENT)
	public static class ModelHelmetSnug extends ModelBiped {
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
