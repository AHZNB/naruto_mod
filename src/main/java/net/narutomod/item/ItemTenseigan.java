
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.util.ITooltipFlag;

import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.gui.GuiNinjaScroll;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureRinneganHelmetTickEvent;
import net.narutomod.procedure.ProcedureTenseiganBodyTickEvent;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemTenseigan extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:tenseiganhelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:tenseiganbody")
	public static final Item body = null;
	@GameRegistry.ObjectHolder("narutomod:tenseiganlegs")
	public static final Item legs = null;

	public ItemTenseigan(ElementsNarutomodMod instance) {
		super(instance, 692);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("TENSEIGAN", "narutomod:sasuke_", 5, new int[]{2, 75, 100, 15}, 0,
		 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:dojutsu")), 2.0f);

		elements.items.add(() -> new ItemDojutsu.Base(enuma) {
			@SideOnly(Side.CLIENT)
			@Override
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ItemDojutsu.ClientModel.ModelHelmetSnug armorModel = (ItemDojutsu.ClientModel.ModelHelmetSnug)super.getArmorModel(living, stack, slot, defaultModel);
				armorModel.headwearShine = true;
				armorModel.foreheadHide = !ItemRinnegan.isRinnesharinganActivated(stack);
				Item item = living.getHeldItemMainhand().getItem();
				armorModel.headwearHide = item != ItemTenseiganChakraMode.block || ((ItemTenseiganChakraMode.RangedItem)item).isOnCooldown(living);
				armorModel.headHide = !armorModel.headwearHide;
				armorModel.hornRight.showModel = armorModel.hornLeft.showModel = false;
				return armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/tenseiganhelmet.png";
			}

			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				super.onArmorTick(world, entity, itemstack);
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
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
						if (helmetStack.getItem() != helmet && helmetStack.getItem() != ItemRinnegan.helmet) {
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
				if (slot == EntityEquipmentSlot.HEAD && ItemRinnegan.isRinnesharinganActivated(stack)) {
					multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(),
					 new AttributeModifier(ItemRinnegan.RINNESHARINGAN_MODIFIER, "rinnesharingan.maxhealth", 380d, 0));
				}
				return multimap;
			}

			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				super.addInformation(stack, worldIn, tooltip, flagIn);
				if (ItemRinnegan.isRinnesharinganActivated(stack)) {
					tooltip.add(TextFormatting.RED + I18n.translateToLocal("advancements.rinnesharinganactivated.title") + TextFormatting.WHITE);
				}
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu1") + ": "
				 + TextFormatting.GRAY + I18n.translateToLocal("chattext.shinratensei"));
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu2") + ": "
				 + TextFormatting.GRAY + I18n.translateToLocal("tooltip.rinnegan.jutsu2")
				 + " (" + I18n.translateToLocal("tooltip.general.powerupkey") + ")");
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu3") + ": "
				 + TextFormatting.GRAY + I18n.translateToLocal("tooltip.rinnegan.jutsu3"));
			}

			@Override
			public String getItemStackDisplayName(ItemStack stack) {
				return TextFormatting.AQUA + super.getItemStackDisplayName(stack) + TextFormatting.WHITE;
			}
		}.setUnlocalizedName("tenseiganhelmet").setRegistryName("tenseiganhelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@Override
			@SideOnly(Side.CLIENT)
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
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/tenseigan_chakramode.png";
			}

			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", world);
					$_dependencies.put("itemstack", itemstack);
					ProcedureTenseiganBodyTickEvent.executeProcedure($_dependencies);
				}
			}

			@Override
			public int getDamage(ItemStack stack) {
				int itemDamage = this.getMetadata(stack);
				if (itemDamage > this.getMaxDamage()) {
					itemDamage = this.getMaxDamage();
				}
				return itemDamage;
			}
		}.setUnlocalizedName("tenseiganbody").setRegistryName("tenseiganbody").setCreativeTab(null));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.LEGS) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@Override
			@SideOnly(Side.CLIENT)
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
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				{
					Map<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", world);
					$_dependencies.put("itemstack", itemstack);
					ProcedureTenseiganBodyTickEvent.executeProcedure($_dependencies);
				}
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/tenseigan_chakramode.png";
			}

			@Override
			public int getDamage(ItemStack stack) {
				int itemDamage = this.getMetadata(stack);
				if (itemDamage > this.getMaxDamage()) {
					itemDamage = this.getMaxDamage();
				}
				return itemDamage;
			}
		}.setUnlocalizedName("tenseiganlegs").setRegistryName("tenseiganlegs").setCreativeTab(null));
	}

	public static boolean isWearing(EntityLivingBase player) {
		return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == helmet;
	}

	public static boolean canUseChakraMode(ItemStack stack, EntityPlayer player) {
		return stack.hasTagCompound() && stack.getTagCompound().getDouble("ByakuganCount") >= 5.0d;
	}

	public static boolean isWearingFullArmor(EntityLivingBase entity) {
		return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == helmet
		 && entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == body
		 && entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == legs;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:tenseiganhelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:tenseiganbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:tenseiganlegs", "inventory"));
	}
	// Made with Blockbench 4.1.5
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelSizPathRobe extends ModelBiped {
		//private final ModelRenderer bipedBody;
		private final ModelRenderer robe;
		private final ModelRenderer bone5;
		private final ModelRenderer bone6;
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
		//private final ModelRenderer bipedLeftArm;
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
	
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(0.0F, -24.0F, -1.8F);
			robe.addChild(bone5);
			setRotationAngle(bone5, -0.3927F, 0.0F, 0.0F);
			bone5.cubeList.add(new ModelBox(bone5, 24, 48, -4.0F, -4.0F, 0.0F, 8, 4, 4, 1.0F, false));
	
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(0.0F, 1.0F, 3.0F);
			bone5.addChild(bone6);
			setRotationAngle(bone6, -0.5236F, 0.0F, 0.0F);
			bone6.cubeList.add(new ModelBox(bone6, 18, 1, -4.0F, -6.0F, -1.0F, 8, 5, 2, 1.0F, false));
	
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
			bone.cubeList.add(new ModelBox(bone, 8, 48, 0.0F, 0.0F, -2.0F, 0, 8, 4, 0.0F, false));
	
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(-4.0F, -12.25F, 0.0F);
			skirtRight.addChild(bone3);
			setRotationAngle(bone3, 0.1745F, 0.0F, 0.1745F);
			bone3.cubeList.add(new ModelBox(bone3, 16, 48, 0.0F, 0.0F, -2.0F, 0, 8, 4, 0.0F, false));
	
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(0.0F, -12.0F, 2.0F);
			skirtRight.addChild(bone4);
			setRotationAngle(bone4, 0.1745F, 0.0F, 0.1745F);
			bone4.cubeList.add(new ModelBox(bone4, 0, 56, -4.0F, 0.0F, 0.0F, 4, 8, 0, 0.0F, false));
	
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
			bone8.cubeList.add(new ModelBox(bone8, 8, 48, 0.0F, 0.0F, -2.0F, 0, 8, 4, 0.0F, true));
	
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(4.0F, -12.25F, 0.0F);
			skirtLeft.addChild(bone9);
			setRotationAngle(bone9, 0.1745F, 0.0F, -0.1745F);
			bone9.cubeList.add(new ModelBox(bone9, 16, 48, 0.0F, 0.0F, -2.0F, 0, 8, 4, 0.0F, true));
	
			bone10 = new ModelRenderer(this);
			bone10.setRotationPoint(0.0F, -12.0F, 2.0F);
			skirtLeft.addChild(bone10);
			setRotationAngle(bone10, 0.1745F, 0.0F, -0.1745F);
			bone10.cubeList.add(new ModelBox(bone10, 0, 56, 0.0F, 0.0F, 0.0F, 4, 8, 0, 0.0F, true));
	
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			setRotationAngle(bipedRightArm, -0.1745F, 0.0F, 0.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.05F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.3F, false));
	
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			setRotationAngle(bipedLeftArm, -0.1745F, 0.0F, 0.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 48, 0, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.05F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.3F, true));
	
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
			if (entity instanceof AbstractClientPlayer && ((AbstractClientPlayer)entity).getSkinType().equals("slim")) {
				this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
			}
			if (this.bipedBody.showModel || this.bipedLeftLeg.showModel) {
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			}
			super.render(entity, f, f1, f2, f3, f4, f5);
			if (this.bipedBody.showModel || this.bipedLeftLeg.showModel) {
				int i = entity.getBrightnessForRender();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(i % 65536), (float)(i / 65536));
				GlStateManager.enableLighting();
			}
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

	}
}
