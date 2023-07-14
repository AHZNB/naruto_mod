
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.potion.PotionChakraEnhancedStrength;
import net.narutomod.potion.PotionReach;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.entity.EntityJinchurikiClone;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Multimap;
import java.util.UUID;
import java.util.Random;
import java.util.List;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBijuCloak extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:biju_cloakhelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:biju_cloakbody")
	public static final Item body = null;
	@GameRegistry.ObjectHolder("narutomod:biju_cloaklegs")
	public static final Item legs = null;

	private final AttributeModifier CLOAK_MODIFIER = new AttributeModifier(UUID.fromString("e884e4a0-7f08-422d-9aac-119972cd764d"), "bijucloak.maxhealth", 180d, 0);
	@SideOnly(Side.CLIENT)
	private ModelBijuCloak[] bijuModel;

	public ItemBijuCloak(ElementsNarutomodMod instance) {
		super(instance, 577);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void init(FMLInitializationEvent event) {
		this.bijuModel = new ModelBijuCloak[10];
		for (int i = 0; i < 10; i++) {
			this.bijuModel[i] = new ModelBijuCloak(i);
		}
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("BIJU_CLOAK", "narutomod:sasuke_",
		 1024, new int[]{1024, 1024, 1024, 1024}, 0, null, 5f);

		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.HEAD) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ModelBijuCloak armorModel = ItemBijuCloak.this.bijuModel[stack.getMetadata()];
				armorModel.isSneak = living.isSneaking();
				armorModel.isRiding = living.isRiding();
				armorModel.isChild = living.isChild();
				int tails = getTails(stack);
				armorModel.earLeft[0].showModel = armorModel.earRight[0].showModel = (tails != 1);
				armorModel.bodyShine = tails == 9 && getCloakLevel(stack) == 2 && getCloakXp(stack) >= 800;
				armorModel.layerShine = true;
				return armorModel;
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (!world.isRemote && entity instanceof EntityPlayer) {
					int cloakLevel = EntityBijuManager.cloakLevel((EntityPlayer)entity);
					if (cloakLevel <= 0) {
						itemstack.shrink(1);
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
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return getTexture(stack);
			}
		}.setUnlocalizedName("biju_cloakhelmet").setRegistryName("biju_cloakhelmet").setCreativeTab(null));

		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ModelBijuCloak armorModel = ItemBijuCloak.this.bijuModel[stack.getMetadata()];
				armorModel.isSneak = living.isSneaking();
				armorModel.isRiding = living.isRiding();
				armorModel.isChild = living.isChild();
				armorModel.bodyShine = getTails(stack) == 9 && getCloakLevel(stack) == 2 && getCloakXp(stack) >= 800;
				armorModel.allTails.showModel = !armorModel.bodyShine;
				armorModel.layerShine = true;
				return armorModel;
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);

				if (entity instanceof EntityPlayer) {
					EntityPlayer livingEntity = (EntityPlayer) entity;
				 	int cloakLevel = EntityBijuManager.cloakLevel(livingEntity);
				 	if (cloakLevel > 0) {
				 		ItemStack helmetStack = livingEntity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				 		ItemStack legStack = livingEntity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
						if (helmetStack.getItem() == helmet && itemstack.getItem() == body && legStack.getItem() == legs) {
					 	 	setWearingFullSet(itemstack, true);

							if (!world.isRemote) {
								setCloakLevel(helmetStack, cloakLevel);
								setCloakLevel(itemstack, cloakLevel);
								setCloakLevel(legStack, cloakLevel);
				 				int wearingTicks = getWearingTicks(livingEntity);
				 				int cloakXp = EntityBijuManager.getCloakXp(livingEntity);
						 	 	wearingTicks = wearingTicks > 0 ? ++wearingTicks : 1;

						 	 	if (wearingTicks <= cloakXp * 5 + 200 && net.narutomod.Chakra.pathway(livingEntity).getAmount() > 0d) {
						 	 		cloakXp += wearingTicks / 20;
									setCloakXp(helmetStack, cloakXp);
									setCloakXp(itemstack, cloakXp);
									setCloakXp(legStack, cloakXp);
									setWearingTicks(livingEntity, wearingTicks);
									if (cloakXp >= 800 || (cloakLevel == 1 && cloakXp >= 400)) {
										revertOriginal(livingEntity, itemstack);
										applyEffects(livingEntity, cloakLevel, getTails(itemstack) != 1 && cloakLevel == 1);
									} else {
										spawnClone(livingEntity, itemstack);
									}
						 	 	} else {
						 	 		if (cloakXp < 400 || (cloakLevel == 2 && cloakXp < 800)) {
						 	 			revertOriginal(livingEntity, itemstack);
						 	 		}
						 	 		EntityBijuManager.toggleBijuCloak(livingEntity);
						 	 		itemstack.shrink(1);
						 	 	}
							}
					 	 } else {
					 	 	setWearingFullSet(itemstack, false);
					 	 }
					} else if (!world.isRemote) {
						itemstack.shrink(1);
					}
				} else if (entity instanceof EntityJinchurikiClone.EntityCustom && !world.isRemote && entity.isEntityAlive()) {
					setWearingTicks(entity, getWearingTicks(entity) + 1);
					int i = getCloakLevel(itemstack);
					applyEffects((EntityLivingBase)entity, i, getTails(itemstack) != 1 && i == 1);
				}
			}

			@Override
			public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
				Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
				if (slot == EntityEquipmentSlot.CHEST && isWearingFullSet(stack)) {
					multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), CLOAK_MODIFIER);
				}
				return multimap;
			}

			@SideOnly(Side.CLIENT)
			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				super.addInformation(stack, worldIn, tooltip, flagIn);
				tooltip.add(I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " + I18n.translateToLocal("entity.jinchuriki_clone.name"));
				int i = getCloakLevel(stack);
				if (i == 2) {
					tooltip.add(I18n.translateToLocal("key.mcreator.specialjutsu3") + ": " + I18n.translateToLocal("entity.tailbeastball.name"));
				}
				tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("tooltip.bijucloak.level"+i));
				tooltip.add("JXP: " + TextFormatting.GREEN + getCloakXp(stack) + TextFormatting.RESET);
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
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return getTexture(stack);
			}
		}.setUnlocalizedName("biju_cloakbody").setRegistryName("biju_cloakbody").setCreativeTab(null));

		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.LEGS) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ModelBijuCloak armorModel = ItemBijuCloak.this.bijuModel[stack.getMetadata()];
				armorModel.isSneak = living.isSneaking();
				armorModel.isRiding = living.isRiding();
				armorModel.isChild = living.isChild();
				armorModel.bodyShine = getTails(stack) == 9 && getCloakLevel(stack) == 2 && getCloakXp(stack) >= 800;
				armorModel.layerShine = true;
				return armorModel;
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (!world.isRemote && entity instanceof EntityPlayer) {
					int cloakLevel = EntityBijuManager.cloakLevel((EntityPlayer)entity);
					if (cloakLevel <= 0) {
						itemstack.shrink(1);
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
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return getTexture(stack);
			}
		}.setUnlocalizedName("biju_cloaklegs").setRegistryName("biju_cloaklegs").setCreativeTab(null));
	}

	private static String getTexture(ItemStack stack) {
		int i = getTails(stack);
		int j = getCloakLevel(stack);
		int k = getCloakXp(stack);
		return i == 1 && j == 1 
		 	? "narutomod:textures/bijucloak_sand.png"
			: j == 2
				? i == 9 && k >= 800
					? k < 4800 
						? "narutomod:textures/bijucloak_kurama.png"
						: "narutomod:textures/bijucloak_kcm2.png"
		 			: "narutomod:textures/bijucloakl2.png" 
		 		: "narutomod:textures/bijucloakl1.png";
	}

	public static void clearCloakItems(EntityPlayer player) {
		player.inventory.clearMatchingItems(helmet, -1, -1, null);
		player.inventory.clearMatchingItems(body, -1, -1, null);
		player.inventory.clearMatchingItems(legs, -1, -1, null);
		player.getEntityData().removeTag("lungeAttackData");
	}

	public static void applyEffects(EntityLivingBase entity, int level) {
		applyEffects(entity, level, true);
	}

	public static void applyEffects(EntityLivingBase entity, int level, boolean smoke) {
		if (smoke) {
			Particles.spawnParticle(entity.world, Particles.Types.SMOKE, entity.posX, entity.posY + 0.8d, entity.posZ, 
			 40, 0.2d, 0.4d, 0.2d, 0d, 0d, 0d, 0x2088001b, 20, 
			 (int)(4.0D / (entity.getRNG().nextDouble() * 0.8D + 0.2D)), 0, entity.getEntityId());
		}
		if (!entity.world.isRemote && entity.ticksExisted % 10 == 4) {
			//entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 5, 0, false, false));
			entity.addPotionEffect(new PotionEffect(PotionChakraEnhancedStrength.potion, 12, level * 32, false, false));
			entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 12, level * 24, false, false));
			entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 12, 5, false, false));
			entity.addPotionEffect(new PotionEffect(PotionReach.potion, 12, level - 1, false, false));
			if (entity.getHealth() < entity.getMaxHealth() && entity.getHealth() > 0.0f) {
				entity.heal((float)level);
			}
			if (level == 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 12, 2, false, false));
			}
		}
		if (!entity.world.isRemote && entity instanceof EntityPlayer) {
			NBTTagCompound compound = entity.getEntityData().hasKey("lungeAttackData") ? entity.getEntityData().getCompoundTag("lungeAttackData") : new NBTTagCompound();
			int attackTime = compound.getInteger("attackTime");
			Entity target = compound.hasKey("targetId") ? entity.world.getEntityByID(compound.getInteger("targetId")) : null;
			if (entity.swingProgressInt == 1) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 15d, 3d);
				if (res != null && res.entityHit instanceof EntityLivingBase && res.entityHit.isEntityAlive()) {
					target = res.entityHit;
					compound.setInteger("targetId", target.getEntityId());
					attackTime = 0;
					entity.rotationYaw = ProcedureUtils.getYawFromVec(target.getPositionVector()
					 .subtract(entity.getPositionVector()));
					double d0 = target.posX - entity.posX;
					double d1 = target.posY - entity.posY;
					double d2 = target.posZ - entity.posZ;
					double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
					ProcedureUtils.setVelocity(entity, d0 * 0.5, d1 * 0.5 + d3 * 0.025d, d2 * 0.5);
				}
			}
			if (attackTime < 12 && target != null && target.getDistanceSq(entity) < 25d) {
				((EntityPlayer)entity).attackTargetEntityWithCurrentItem(target);
				compound.removeTag("targetId");
			}
			compound.setInteger("attackTime", ++attackTime);
			entity.getEntityData().setTag("lungeAttackData", compound);
		}
	}

	private static int getTails(ItemStack stack) {
		return stack.getTagCompound().getInteger("Tails");
	}

	private static void setCloakLevel(ItemStack itemstack, int level) {
	 	if (!itemstack.hasTagCompound()) {
	 		itemstack.setTagCompound(new NBTTagCompound());
	 	}
	 	itemstack.getTagCompound().setInteger("BijuCloakLevel", level);
	}

	private static int getCloakLevel(ItemStack itemstack) {
		return itemstack.hasTagCompound() ? itemstack.getTagCompound().getInteger("BijuCloakLevel") : 0;
	}

	private static void setCloakXp(ItemStack itemstack, int xp) {
	 	if (!itemstack.hasTagCompound()) {
	 		itemstack.setTagCompound(new NBTTagCompound());
	 	}
	 	itemstack.getTagCompound().setInteger("BijuCloakXp", xp);
	}

	private static int getCloakXp(ItemStack itemstack) {
		return itemstack.hasTagCompound() ? itemstack.getTagCompound().getInteger("BijuCloakXp") : 0;
	}

	public static void setWearingTicks(Entity entity, int ticks) {
		ProcedureSync.EntityNBTTag.setAndSync(entity, "WearingBijuCloakTicks", ticks);
	}

	public static int getWearingTicks(Entity entity) {
		return entity.getEntityData().getInteger("WearingBijuCloakTicks");
	}

	private void setWearingFullSet(ItemStack itemstack, boolean b) {
	 	if (!itemstack.hasTagCompound()) {
	 		itemstack.setTagCompound(new NBTTagCompound());
	 	}
	 	itemstack.getTagCompound().setBoolean("WearingFullSetBijuCloak", b);
	}

	private boolean isWearingFullSet(ItemStack itemstack) {
		return itemstack.hasTagCompound() && itemstack.getTagCompound().getBoolean("WearingFullSetBijuCloak");
	}

	private void setClone(ItemStack itemstack, EntityJinchurikiClone.EntityCustom clone) {
	 	if (!itemstack.hasTagCompound()) {
	 		itemstack.setTagCompound(new NBTTagCompound());
	 	}
	 	itemstack.getTagCompound().setInteger("CloneID", clone.getEntityId());
	}

	@Nullable
	private static EntityJinchurikiClone.EntityCustom getClone(World world, ItemStack itemstack) {
		if (hasClone(itemstack)) {
			Entity entity = world.getEntityByID(itemstack.getTagCompound().getInteger("CloneID"));
			return entity instanceof EntityJinchurikiClone.EntityCustom ? (EntityJinchurikiClone.EntityCustom)entity : null;
		}
		return null;
	}

	private static int getCloneId(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("CloneID") ? stack.getTagCompound().getInteger("CloneID") : -1;
	}

	private static boolean hasClone(ItemStack stack) {
		return getCloneId(stack) > 0;
	}

	private void spawnClone(EntityPlayer original, ItemStack stack) {
		if (!original.world.isRemote && !hasClone(stack)) {
			EntityJinchurikiClone.EntityCustom entity = new EntityJinchurikiClone.EntityCustom(original);
			entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(CLOAK_MODIFIER);
			entity.setHealth(original.getHealth());
			original.world.spawnEntity(entity);
			setClone(stack, entity);
		}
	}

	public static void revertOriginal(EntityPlayer player, ItemStack stack) {
		EntityJinchurikiClone.EntityCustom clone = getClone(player.world, stack);
		if (clone != null) {
			clone.setDead();
			stack.getTagCompound().removeTag("CloneID");
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:biju_cloakhelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 1, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 2, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 3, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 4, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 5, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 6, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 7, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 8, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 9, new ModelResourceLocation("narutomod:biju_cloakbody", "inventory"));
		ModelLoader.setCustomModelResourceLocation(legs, 0, new ModelResourceLocation("narutomod:biju_cloaklegs", "inventory"));
	}

	// Made with Blockbench 3.8.4
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelBijuCloak extends ModelBiped {
		//private final ModelRenderer bipedHead;
		private final ModelRenderer earLeft[] = new ModelRenderer[6];
		private final ModelRenderer earRight[] = new ModelRenderer[6];
		private final ModelRenderer sandEar;
		private final ModelRenderer cube_r1;
		//private final ModelRenderer bipedBody;
		private final ModelRenderer allTails;
		private final ModelRenderer tail[][] = new ModelRenderer[9][8];
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
		//private final ModelRenderer bipedRightLeg;
		//private final ModelRenderer bipedLeftLeg;
		private final ModelRenderer bipedBodyWear;
		private final ModelRenderer tailWears;
		private final ModelRenderer tailWear[][] = new ModelRenderer[1][8];
		private final ModelRenderer bipedRightArmWear;
		private final ModelRenderer sandArm;
		private final ModelRenderer bipedLeftArmWear;
		private final ModelRenderer bipedRightLegWear;
		private final ModelRenderer bipedLeftLegWear;
		private final float tailSwayX[][] = new float[9][8];
		private final float tailSwayZ[][] = new float[9][8];
		private final float leftEarSwayX[] = new float[6];
		private final float leftEarSwayZ[] = new float[6];
		private final float rightEarSwayX[] = new float[6];
		private final float rightEarSwayZ[] = new float[6];
		private int[] tailShowMap = { 0, 1, 6, 0x19, 0x1E, 0x1F, 0x1F8, 0x7F, 0x1FE, 0x1FF };
		private boolean bodyShine;
		private boolean layerShine;
		private boolean narutoRunPose;
		private final Random rand = new Random();
	
		public ModelBijuCloak(int tails) {
			textureWidth = 128;
			textureHeight = 64;
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.6F, false));
			earLeft[0] = new ModelRenderer(this);
			earLeft[0].setRotationPoint(3.5F, -8.25F, -0.5F);
			bipedHead.addChild(earLeft[0]);
			setRotationAngle(earLeft[0], -0.5236F, 0.0F, 0.7854F);
			earLeft[0].cubeList.add(new ModelBox(earLeft[0], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.8F, false));
			earLeft[1] = new ModelRenderer(this);
			earLeft[1].setRotationPoint(0.0F, -1.0F, 0.0F);
			earLeft[0].addChild(earLeft[1]);
			setRotationAngle(earLeft[1], 0.0F, 0.0F, -0.1745F);
			earLeft[1].cubeList.add(new ModelBox(earLeft[1], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.7F, false));
			earLeft[2] = new ModelRenderer(this);
			earLeft[2].setRotationPoint(0.0F, -1.0F, 0.0F);
			earLeft[1].addChild(earLeft[2]);
			setRotationAngle(earLeft[2], 0.0F, 0.0F, -0.1745F);
			earLeft[2].cubeList.add(new ModelBox(earLeft[2], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.6F, false));
			earLeft[3] = new ModelRenderer(this);
			earLeft[3].setRotationPoint(0.0F, -1.0F, 0.0F);
			earLeft[2].addChild(earLeft[3]);
			setRotationAngle(earLeft[3], 0.0F, 0.0F, -0.1745F);
			earLeft[3].cubeList.add(new ModelBox(earLeft[3], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.4F, false));
			earLeft[4] = new ModelRenderer(this);
			earLeft[4].setRotationPoint(0.0F, -1.0F, 0.0F);
			earLeft[3].addChild(earLeft[4]);
			setRotationAngle(earLeft[4], 0.0F, 0.0F, -0.1745F);
			earLeft[4].cubeList.add(new ModelBox(earLeft[4], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.2F, false));
			earLeft[5] = new ModelRenderer(this);
			earLeft[5].setRotationPoint(0.0F, -1.0F, 0.0F);
			earLeft[4].addChild(earLeft[5]);
			setRotationAngle(earLeft[5], 0.0F, 0.0F, -0.1745F);
			earLeft[5].cubeList.add(new ModelBox(earLeft[5], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, -0.1F, false));
			earRight[0] = new ModelRenderer(this);
			earRight[0].setRotationPoint(-3.5F, -8.25F, -0.5F);
			bipedHead.addChild(earRight[0]);
			setRotationAngle(earRight[0], -0.5236F, 0.0F, -0.7854F);
			earRight[0].cubeList.add(new ModelBox(earRight[0], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.8F, false));
			earRight[1] = new ModelRenderer(this);
			earRight[1].setRotationPoint(0.0F, -1.0F, 0.0F);
			earRight[0].addChild(earRight[1]);
			setRotationAngle(earRight[1], 0.0F, 0.0F, 0.1745F);
			earRight[1].cubeList.add(new ModelBox(earRight[1], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.7F, false));
			earRight[2] = new ModelRenderer(this);
			earRight[2].setRotationPoint(0.0F, -1.0F, 0.0F);
			earRight[1].addChild(earRight[2]);
			setRotationAngle(earRight[2], 0.0F, 0.0F, 0.1745F);
			earRight[2].cubeList.add(new ModelBox(earRight[2], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.6F, false));
			earRight[3] = new ModelRenderer(this);
			earRight[3].setRotationPoint(0.0F, -1.0F, 0.0F);
			earRight[2].addChild(earRight[3]);
			setRotationAngle(earRight[3], 0.0F, 0.0F, 0.1745F);
			earRight[3].cubeList.add(new ModelBox(earRight[3], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.4F, false));
			earRight[4] = new ModelRenderer(this);
			earRight[4].setRotationPoint(0.0F, -1.0F, 0.0F);
			earRight[3].addChild(earRight[4]);
			setRotationAngle(earRight[4], 0.0F, 0.0F, 0.1745F);
			earRight[4].cubeList.add(new ModelBox(earRight[4], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, 0.2F, false));
			earRight[5] = new ModelRenderer(this);
			earRight[5].setRotationPoint(0.0F, -1.0F, 0.0F);
			earRight[4].addChild(earRight[5]);
			setRotationAngle(earRight[5], 0.0F, 0.0F, 0.1745F);
			earRight[5].cubeList.add(new ModelBox(earRight[5], 32, 0, -0.5F, -1.5F, -0.5F, 1, 2, 1, -0.1F, false));
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 64, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.6F, false));
			sandEar = new ModelRenderer(this);
			sandEar.setRotationPoint(-4.425F, -8.0F, 0.0F);
			bipedHeadwear.addChild(sandEar);
			setRotationAngle(sandEar, 0.0F, 0.0F, -0.2618F);
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
			sandEar.addChild(cube_r1);
			setRotationAngle(cube_r1, -0.7782F, -0.0998F, -0.1434F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 118, 0, -1.0F, -2.8F, -2.0F, 2, 6, 3, 0.0F, false));
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.6F, false));
			allTails = new ModelRenderer(this);
			bipedBody.addChild(allTails);
			tail[0][0] = new ModelRenderer(this);
			tail[0][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			allTails.addChild(tail[0][0]);
			setRotationAngle(tail[0][0], -1.0472F, 0.0F, 0.0F);
			tail[0][0].cubeList.add(new ModelBox(tail[0][0], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[0][1] = new ModelRenderer(this);
			tail[0][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[0][0].addChild(tail[0][1]);
			setRotationAngle(tail[0][1], 0.2618F, 0.0F, 0.0F);
			tail[0][1].cubeList.add(new ModelBox(tail[0][1], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[0][2] = new ModelRenderer(this);
			tail[0][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[0][1].addChild(tail[0][2]);
			setRotationAngle(tail[0][2], 0.2618F, 0.0F, 0.0F);
			tail[0][2].cubeList.add(new ModelBox(tail[0][2], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));
			tail[0][3] = new ModelRenderer(this);
			tail[0][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[0][2].addChild(tail[0][3]);
			setRotationAngle(tail[0][3], 0.2618F, 0.0F, 0.0F);
			tail[0][3].cubeList.add(new ModelBox(tail[0][3], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[0][4] = new ModelRenderer(this);
			tail[0][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[0][3].addChild(tail[0][4]);
			setRotationAngle(tail[0][4], 0.2618F, 0.0F, 0.0F);
			tail[0][4].cubeList.add(new ModelBox(tail[0][4], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[0][5] = new ModelRenderer(this);
			tail[0][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[0][4].addChild(tail[0][5]);
			setRotationAngle(tail[0][5], 0.2618F, 0.0F, 0.0F);
			tail[0][5].cubeList.add(new ModelBox(tail[0][5], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));
			tail[0][6] = new ModelRenderer(this);
			tail[0][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tail[0][5].addChild(tail[0][6]);
			setRotationAngle(tail[0][6], 0.2618F, 0.0F, 0.0F);
			tail[0][6].cubeList.add(new ModelBox(tail[0][6], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.6F, false));
			tail[0][7] = new ModelRenderer(this);
			tail[0][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tail[0][6].addChild(tail[0][7]);
			setRotationAngle(tail[0][7], 0.2618F, 0.0F, 0.0F);
			tail[0][7].cubeList.add(new ModelBox(tail[0][7], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -1.0F, false));
			tail[1][0] = new ModelRenderer(this);
			tail[1][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			allTails.addChild(tail[1][0]);
			setRotationAngle(tail[1][0], -1.0472F, -0.5236F, -0.2618F);
			tail[1][0].cubeList.add(new ModelBox(tail[1][0], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[1][1] = new ModelRenderer(this);
			tail[1][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[1][0].addChild(tail[1][1]);
			setRotationAngle(tail[1][1], 0.2618F, 0.0F, 0.0F);
			tail[1][1].cubeList.add(new ModelBox(tail[1][1], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[1][2] = new ModelRenderer(this);
			tail[1][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[1][1].addChild(tail[1][2]);
			setRotationAngle(tail[1][2], 0.2618F, 0.0F, 0.0F);
			tail[1][2].cubeList.add(new ModelBox(tail[1][2], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));
			tail[1][3] = new ModelRenderer(this);
			tail[1][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[1][2].addChild(tail[1][3]);
			setRotationAngle(tail[1][3], 0.2618F, 0.0F, 0.0F);
			tail[1][3].cubeList.add(new ModelBox(tail[1][3], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[1][4] = new ModelRenderer(this);
			tail[1][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[1][3].addChild(tail[1][4]);
			setRotationAngle(tail[1][4], 0.2618F, 0.0F, 0.0F);
			tail[1][4].cubeList.add(new ModelBox(tail[1][4], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[1][5] = new ModelRenderer(this);
			tail[1][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[1][4].addChild(tail[1][5]);
			setRotationAngle(tail[1][5], 0.2618F, 0.0F, 0.0F);
			tail[1][5].cubeList.add(new ModelBox(tail[1][5], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));
			tail[1][6] = new ModelRenderer(this);
			tail[1][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tail[1][5].addChild(tail[1][6]);
			setRotationAngle(tail[1][6], 0.2618F, 0.0F, 0.0F);
			tail[1][6].cubeList.add(new ModelBox(tail[1][6], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.6F, false));
			tail[1][7] = new ModelRenderer(this);
			tail[1][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tail[1][6].addChild(tail[1][7]);
			setRotationAngle(tail[1][7], 0.2618F, 0.0F, 0.0F);
			tail[1][7].cubeList.add(new ModelBox(tail[1][7], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -1.0F, false));
			tail[2][0] = new ModelRenderer(this);
			tail[2][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			allTails.addChild(tail[2][0]);
			setRotationAngle(tail[2][0], -1.0472F, 0.5236F, 0.2618F);
			tail[2][0].cubeList.add(new ModelBox(tail[2][0], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[2][1] = new ModelRenderer(this);
			tail[2][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[2][0].addChild(tail[2][1]);
			setRotationAngle(tail[2][1], 0.2618F, 0.0F, 0.0F);
			tail[2][1].cubeList.add(new ModelBox(tail[2][1], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[2][2] = new ModelRenderer(this);
			tail[2][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[2][1].addChild(tail[2][2]);
			setRotationAngle(tail[2][2], 0.2618F, 0.0F, 0.0F);
			tail[2][2].cubeList.add(new ModelBox(tail[2][2], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));
			tail[2][3] = new ModelRenderer(this);
			tail[2][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[2][2].addChild(tail[2][3]);
			setRotationAngle(tail[2][3], 0.2618F, 0.0F, 0.0F);
			tail[2][3].cubeList.add(new ModelBox(tail[2][3], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[2][4] = new ModelRenderer(this);
			tail[2][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[2][3].addChild(tail[2][4]);
			setRotationAngle(tail[2][4], 0.2618F, 0.0F, 0.0F);
			tail[2][4].cubeList.add(new ModelBox(tail[2][4], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[2][5] = new ModelRenderer(this);
			tail[2][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[2][4].addChild(tail[2][5]);
			setRotationAngle(tail[2][5], 0.2618F, 0.0F, 0.0F);
			tail[2][5].cubeList.add(new ModelBox(tail[2][5], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));
			tail[2][6] = new ModelRenderer(this);
			tail[2][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tail[2][5].addChild(tail[2][6]);
			setRotationAngle(tail[2][6], 0.2618F, 0.0F, 0.0F);
			tail[2][6].cubeList.add(new ModelBox(tail[2][6], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.6F, false));
			tail[2][7] = new ModelRenderer(this);
			tail[2][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tail[2][6].addChild(tail[2][7]);
			setRotationAngle(tail[2][7], 0.2618F, 0.0F, 0.0F);
			tail[2][7].cubeList.add(new ModelBox(tail[2][7], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -1.0F, false));
			tail[3][0] = new ModelRenderer(this);
			tail[3][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			allTails.addChild(tail[3][0]);
			setRotationAngle(tail[3][0], -1.0472F, -1.0472F, -0.5236F);
			tail[3][0].cubeList.add(new ModelBox(tail[3][0], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[3][1] = new ModelRenderer(this);
			tail[3][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[3][0].addChild(tail[3][1]);
			setRotationAngle(tail[3][1], 0.2618F, 0.0F, 0.0F);
			tail[3][1].cubeList.add(new ModelBox(tail[3][1], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[3][2] = new ModelRenderer(this);
			tail[3][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[3][1].addChild(tail[3][2]);
			setRotationAngle(tail[3][2], 0.2618F, 0.0F, 0.0F);
			tail[3][2].cubeList.add(new ModelBox(tail[3][2], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));
			tail[3][3] = new ModelRenderer(this);
			tail[3][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[3][2].addChild(tail[3][3]);
			setRotationAngle(tail[3][3], 0.2618F, 0.0F, 0.0F);
			tail[3][3].cubeList.add(new ModelBox(tail[3][3], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[3][4] = new ModelRenderer(this);
			tail[3][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[3][3].addChild(tail[3][4]);
			setRotationAngle(tail[3][4], 0.2618F, 0.0F, 0.0F);
			tail[3][4].cubeList.add(new ModelBox(tail[3][4], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[3][5] = new ModelRenderer(this);
			tail[3][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[3][4].addChild(tail[3][5]);
			setRotationAngle(tail[3][5], 0.2618F, 0.0F, 0.0F);
			tail[3][5].cubeList.add(new ModelBox(tail[3][5], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));
			tail[3][6] = new ModelRenderer(this);
			tail[3][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tail[3][5].addChild(tail[3][6]);
			setRotationAngle(tail[3][6], 0.2618F, 0.0F, 0.0F);
			tail[3][6].cubeList.add(new ModelBox(tail[3][6], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.6F, false));
			tail[3][7] = new ModelRenderer(this);
			tail[3][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tail[3][6].addChild(tail[3][7]);
			setRotationAngle(tail[3][7], 0.2618F, 0.0F, 0.0F);
			tail[3][7].cubeList.add(new ModelBox(tail[3][7], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -1.0F, false));
			tail[4][0] = new ModelRenderer(this);
			tail[4][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			allTails.addChild(tail[4][0]);
			setRotationAngle(tail[4][0], -1.0472F, 1.0472F, 0.5236F);
			tail[4][0].cubeList.add(new ModelBox(tail[4][0], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[4][1] = new ModelRenderer(this);
			tail[4][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[4][0].addChild(tail[4][1]);
			setRotationAngle(tail[4][1], 0.2618F, 0.0F, 0.0F);
			tail[4][1].cubeList.add(new ModelBox(tail[4][1], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[4][2] = new ModelRenderer(this);
			tail[4][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[4][1].addChild(tail[4][2]);
			setRotationAngle(tail[4][2], 0.2618F, 0.0F, 0.0F);
			tail[4][2].cubeList.add(new ModelBox(tail[4][2], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));
			tail[4][3] = new ModelRenderer(this);
			tail[4][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[4][2].addChild(tail[4][3]);
			setRotationAngle(tail[4][3], 0.2618F, 0.0F, 0.0F);
			tail[4][3].cubeList.add(new ModelBox(tail[4][3], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[4][4] = new ModelRenderer(this);
			tail[4][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[4][3].addChild(tail[4][4]);
			setRotationAngle(tail[4][4], 0.2618F, 0.0F, 0.0F);
			tail[4][4].cubeList.add(new ModelBox(tail[4][4], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[4][5] = new ModelRenderer(this);
			tail[4][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[4][4].addChild(tail[4][5]);
			setRotationAngle(tail[4][5], 0.2618F, 0.0F, 0.0F);
			tail[4][5].cubeList.add(new ModelBox(tail[4][5], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));
			tail[4][6] = new ModelRenderer(this);
			tail[4][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tail[4][5].addChild(tail[4][6]);
			setRotationAngle(tail[4][6], 0.2618F, 0.0F, 0.0F);
			tail[4][6].cubeList.add(new ModelBox(tail[4][6], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.6F, false));
			tail[4][7] = new ModelRenderer(this);
			tail[4][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tail[4][6].addChild(tail[4][7]);
			setRotationAngle(tail[4][7], 0.2618F, 0.0F, 0.0F);
			tail[4][7].cubeList.add(new ModelBox(tail[4][7], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -1.0F, false));
			tail[5][0] = new ModelRenderer(this);
			tail[5][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			allTails.addChild(tail[5][0]);
			setRotationAngle(tail[5][0], -1.5718F, -0.2618F, 0.0F);
			tail[5][0].cubeList.add(new ModelBox(tail[5][0], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[5][1] = new ModelRenderer(this);
			tail[5][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[5][0].addChild(tail[5][1]);
			setRotationAngle(tail[5][1], 0.2618F, 0.0F, 0.0F);
			tail[5][1].cubeList.add(new ModelBox(tail[5][1], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[5][2] = new ModelRenderer(this);
			tail[5][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[5][1].addChild(tail[5][2]);
			setRotationAngle(tail[5][2], 0.2618F, 0.0F, 0.0F);
			tail[5][2].cubeList.add(new ModelBox(tail[5][2], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));
			tail[5][3] = new ModelRenderer(this);
			tail[5][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[5][2].addChild(tail[5][3]);
			setRotationAngle(tail[5][3], 0.2618F, 0.0F, 0.0F);
			tail[5][3].cubeList.add(new ModelBox(tail[5][3], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[5][4] = new ModelRenderer(this);
			tail[5][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[5][3].addChild(tail[5][4]);
			setRotationAngle(tail[5][4], 0.2618F, 0.0F, 0.0F);
			tail[5][4].cubeList.add(new ModelBox(tail[5][4], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[5][5] = new ModelRenderer(this);
			tail[5][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[5][4].addChild(tail[5][5]);
			setRotationAngle(tail[5][5], 0.2618F, 0.0F, 0.0F);
			tail[5][5].cubeList.add(new ModelBox(tail[5][5], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));
			tail[5][6] = new ModelRenderer(this);
			tail[5][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tail[5][5].addChild(tail[5][6]);
			setRotationAngle(tail[5][6], 0.2618F, 0.0F, 0.0F);
			tail[5][6].cubeList.add(new ModelBox(tail[5][6], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.6F, false));
			tail[5][7] = new ModelRenderer(this);
			tail[5][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tail[5][6].addChild(tail[5][7]);
			setRotationAngle(tail[5][7], 0.2618F, 0.0F, 0.0F);
			tail[5][7].cubeList.add(new ModelBox(tail[5][7], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -1.0F, false));
			tail[6][0] = new ModelRenderer(this);
			tail[6][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			allTails.addChild(tail[6][0]);
			setRotationAngle(tail[6][0], -1.5718F, 0.2618F, 0.0F);
			tail[6][0].cubeList.add(new ModelBox(tail[6][0], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[6][1] = new ModelRenderer(this);
			tail[6][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[6][0].addChild(tail[6][1]);
			setRotationAngle(tail[6][1], 0.2618F, 0.0F, 0.0F);
			tail[6][1].cubeList.add(new ModelBox(tail[6][1], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[6][2] = new ModelRenderer(this);
			tail[6][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[6][1].addChild(tail[6][2]);
			setRotationAngle(tail[6][2], 0.2618F, 0.0F, 0.0F);
			tail[6][2].cubeList.add(new ModelBox(tail[6][2], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));
			tail[6][3] = new ModelRenderer(this);
			tail[6][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[6][2].addChild(tail[6][3]);
			setRotationAngle(tail[6][3], 0.2618F, 0.0F, 0.0F);
			tail[6][3].cubeList.add(new ModelBox(tail[6][3], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[6][4] = new ModelRenderer(this);
			tail[6][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[6][3].addChild(tail[6][4]);
			setRotationAngle(tail[6][4], 0.2618F, 0.0F, 0.0F);
			tail[6][4].cubeList.add(new ModelBox(tail[6][4], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[6][5] = new ModelRenderer(this);
			tail[6][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[6][4].addChild(tail[6][5]);
			setRotationAngle(tail[6][5], 0.2618F, 0.0F, 0.0F);
			tail[6][5].cubeList.add(new ModelBox(tail[6][5], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));
			tail[6][6] = new ModelRenderer(this);
			tail[6][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tail[6][5].addChild(tail[6][6]);
			setRotationAngle(tail[6][6], 0.2618F, 0.0F, 0.0F);
			tail[6][6].cubeList.add(new ModelBox(tail[6][6], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.6F, false));
			tail[6][7] = new ModelRenderer(this);
			tail[6][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tail[6][6].addChild(tail[6][7]);
			setRotationAngle(tail[6][7], 0.2618F, 0.0F, 0.0F);
			tail[6][7].cubeList.add(new ModelBox(tail[6][7], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -1.0F, false));
			tail[7][0] = new ModelRenderer(this);
			tail[7][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			allTails.addChild(tail[7][0]);
			setRotationAngle(tail[7][0], -1.5718F, 0.7854F, 0.0F);
			tail[7][0].cubeList.add(new ModelBox(tail[7][0], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[7][1] = new ModelRenderer(this);
			tail[7][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[7][0].addChild(tail[7][1]);
			setRotationAngle(tail[7][1], 0.2618F, 0.0F, 0.0F);
			tail[7][1].cubeList.add(new ModelBox(tail[7][1], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[7][2] = new ModelRenderer(this);
			tail[7][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[7][1].addChild(tail[7][2]);
			setRotationAngle(tail[7][2], 0.2618F, 0.0F, 0.0F);
			tail[7][2].cubeList.add(new ModelBox(tail[7][2], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));
			tail[7][3] = new ModelRenderer(this);
			tail[7][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[7][2].addChild(tail[7][3]);
			setRotationAngle(tail[7][3], 0.2618F, 0.0F, 0.0F);
			tail[7][3].cubeList.add(new ModelBox(tail[7][3], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[7][4] = new ModelRenderer(this);
			tail[7][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[7][3].addChild(tail[7][4]);
			setRotationAngle(tail[7][4], 0.2618F, 0.0F, 0.0F);
			tail[7][4].cubeList.add(new ModelBox(tail[7][4], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[7][5] = new ModelRenderer(this);
			tail[7][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[7][4].addChild(tail[7][5]);
			setRotationAngle(tail[7][5], 0.2618F, 0.0F, 0.0F);
			tail[7][5].cubeList.add(new ModelBox(tail[7][5], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));
			tail[7][6] = new ModelRenderer(this);
			tail[7][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tail[7][5].addChild(tail[7][6]);
			setRotationAngle(tail[7][6], 0.2618F, 0.0F, 0.0F);
			tail[7][6].cubeList.add(new ModelBox(tail[7][6], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.6F, false));
			tail[7][7] = new ModelRenderer(this);
			tail[7][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tail[7][6].addChild(tail[7][7]);
			setRotationAngle(tail[7][7], 0.2618F, 0.0F, 0.0F);
			tail[7][7].cubeList.add(new ModelBox(tail[7][7], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -1.0F, false));
			tail[8][0] = new ModelRenderer(this);
			tail[8][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			allTails.addChild(tail[8][0]);
			setRotationAngle(tail[8][0], -1.5718F, -0.7854F, 0.0F);
			tail[8][0].cubeList.add(new ModelBox(tail[8][0], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[8][1] = new ModelRenderer(this);
			tail[8][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[8][0].addChild(tail[8][1]);
			setRotationAngle(tail[8][1], 0.2618F, 0.0F, 0.0F);
			tail[8][1].cubeList.add(new ModelBox(tail[8][1], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[8][2] = new ModelRenderer(this);
			tail[8][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[8][1].addChild(tail[8][2]);
			setRotationAngle(tail[8][2], 0.2618F, 0.0F, 0.0F);
			tail[8][2].cubeList.add(new ModelBox(tail[8][2], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.6F, false));
			tail[8][3] = new ModelRenderer(this);
			tail[8][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[8][2].addChild(tail[8][3]);
			setRotationAngle(tail[8][3], 0.2618F, 0.0F, 0.0F);
			tail[8][3].cubeList.add(new ModelBox(tail[8][3], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.3F, false));
			tail[8][4] = new ModelRenderer(this);
			tail[8][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[8][3].addChild(tail[8][4]);
			setRotationAngle(tail[8][4], 0.2618F, 0.0F, 0.0F);
			tail[8][4].cubeList.add(new ModelBox(tail[8][4], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.0F, false));
			tail[8][5] = new ModelRenderer(this);
			tail[8][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tail[8][4].addChild(tail[8][5]);
			setRotationAngle(tail[8][5], 0.2618F, 0.0F, 0.0F);
			tail[8][5].cubeList.add(new ModelBox(tail[8][5], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.3F, false));
			tail[8][6] = new ModelRenderer(this);
			tail[8][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tail[8][5].addChild(tail[8][6]);
			setRotationAngle(tail[8][6], 0.2618F, 0.0F, 0.0F);
			tail[8][6].cubeList.add(new ModelBox(tail[8][6], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.6F, false));
			tail[8][7] = new ModelRenderer(this);
			tail[8][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tail[8][6].addChild(tail[8][7]);
			setRotationAngle(tail[8][7], 0.2618F, 0.0F, 0.0F);
			tail[8][7].cubeList.add(new ModelBox(tail[8][7], 16, 32, -2.0F, -5.5F, -2.0F, 4, 6, 4, -1.0F, false));
			bipedBodyWear = new ModelRenderer(this);
			bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBodyWear.cubeList.add(new ModelBox(bipedBodyWear, 80, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.65F, false));
			bipedBodyWear.cubeList.add(new ModelBox(bipedBodyWear, 80, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.7F, false));
			tailWears = new ModelRenderer(this);
			tailWears.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBodyWear.addChild(tailWears);
			tailWear[0][0] = new ModelRenderer(this);
			tailWear[0][0].setRotationPoint(0.0F, 10.5F, 2.0F);
			tailWears.addChild(tailWear[0][0]);
			setRotationAngle(tailWear[0][0], -1.0472F, 0.0F, 0.0F);
			tailWear[0][0].cubeList.add(new ModelBox(tailWear[0][0], 102, 4, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.01F, false));
			tailWear[0][1] = new ModelRenderer(this);
			tailWear[0][1].setRotationPoint(0.0F, -5.0F, 0.0F);
			tailWear[0][0].addChild(tailWear[0][1]);
			setRotationAngle(tailWear[0][1], 0.2618F, 0.0F, 0.0F);
			tailWear[0][1].cubeList.add(new ModelBox(tailWear[0][1], 102, 4, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.31F, false));
			tailWear[0][2] = new ModelRenderer(this);
			tailWear[0][2].setRotationPoint(0.0F, -5.0F, 0.0F);
			tailWear[0][1].addChild(tailWear[0][2]);
			setRotationAngle(tailWear[0][2], 0.2618F, 0.0F, 0.0F);
			tailWear[0][2].cubeList.add(new ModelBox(tailWear[0][2], 102, 4, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.61F, false));
			tailWear[0][3] = new ModelRenderer(this);
			tailWear[0][3].setRotationPoint(0.0F, -5.0F, 0.0F);
			tailWear[0][2].addChild(tailWear[0][3]);
			setRotationAngle(tailWear[0][3], 0.2618F, 0.0F, 0.0F);
			tailWear[0][3].cubeList.add(new ModelBox(tailWear[0][3], 102, 4, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.31F, false));
			tailWear[0][4] = new ModelRenderer(this);
			tailWear[0][4].setRotationPoint(0.0F, -5.0F, 0.0F);
			tailWear[0][3].addChild(tailWear[0][4]);
			setRotationAngle(tailWear[0][4], 0.2618F, 0.0F, 0.0F);
			tailWear[0][4].cubeList.add(new ModelBox(tailWear[0][4], 102, 4, -2.0F, -5.5F, -2.0F, 4, 6, 4, 0.01F, false));
			tailWear[0][5] = new ModelRenderer(this);
			tailWear[0][5].setRotationPoint(0.0F, -5.0F, 0.0F);
			tailWear[0][4].addChild(tailWear[0][5]);
			setRotationAngle(tailWear[0][5], 0.2618F, 0.0F, 0.0F);
			tailWear[0][5].cubeList.add(new ModelBox(tailWear[0][5], 102, 4, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.29F, false));
			tailWear[0][6] = new ModelRenderer(this);
			tailWear[0][6].setRotationPoint(0.0F, -4.0F, 0.0F);
			tailWear[0][5].addChild(tailWear[0][6]);
			setRotationAngle(tailWear[0][6], 0.2618F, 0.0F, 0.0F);
			tailWear[0][6].cubeList.add(new ModelBox(tailWear[0][6], 102, 4, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.59F, false));
			tailWear[0][7] = new ModelRenderer(this);
			tailWear[0][7].setRotationPoint(0.0F, -3.75F, 0.0F);
			tailWear[0][6].addChild(tailWear[0][7]);
			setRotationAngle(tailWear[0][7], 0.2618F, 0.0F, 0.0F);
			tailWear[0][7].cubeList.add(new ModelBox(tailWear[0][7], 102, 4, -2.0F, -5.5F, -2.0F, 4, 6, 4, -0.99F, false));
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.6F, false));
			bipedRightArmWear = new ModelRenderer(this);
			bipedRightArmWear.setRotationPoint(-5.0F, 2.0F, 0.0F);
			bipedRightArmWear.cubeList.add(new ModelBox(bipedRightArmWear, 104, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.65F, false));
			bipedRightArmWear.cubeList.add(new ModelBox(bipedRightArmWear, 104, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.7F, false));
			sandArm = new ModelRenderer(this);
			if (tails == 1) {
				sandArm.setRotationPoint(-1.6421F, 7.959F, -3.46F);
				bipedRightArmWear.addChild(sandArm);
				ModelRenderer sandHand = new ModelRenderer(this);
				sandHand.setRotationPoint(-2.3579F, 3.041F, 1.46F);
				sandArm.addChild(sandHand);
				ModelRenderer finger = new ModelRenderer(this);
				finger.setRotationPoint(0.0F, 0.0F, 0.0F);
				sandHand.addChild(finger);
				setRotationAngle(finger, 0.0F, 0.0F, 0.1309F);
				ModelRenderer cube_r3 = new ModelRenderer(this);
				cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
				finger.addChild(cube_r3);
				setRotationAngle(cube_r3, -0.0999F, -0.5148F, 0.2009F);
				cube_r3.cubeList.add(new ModelBox(cube_r3, 120, 35, -0.075F, -3.0F, -1.0F, 2, 4, 2, 0.0F, false));
				ModelRenderer cube_r4 = new ModelRenderer(this);
				cube_r4.setRotationPoint(1.1324F, 2.5785F, 0.6538F);
				finger.addChild(cube_r4);
				setRotationAngle(cube_r4, 0.1719F, -0.4971F, -0.3492F);
				cube_r4.cubeList.add(new ModelBox(cube_r4, 120, 42, -1.0F, -2.3F, -1.0F, 2, 4, 2, -0.25F, false));
				ModelRenderer finger3 = new ModelRenderer(this);
				finger3.setRotationPoint(4.9282F, 0.7341F, 1.2245F);
				sandHand.addChild(finger3);
				setRotationAngle(finger3, 0.0F, 0.5672F, 0.0F);
				ModelRenderer cube_r5 = new ModelRenderer(this);
				cube_r5.setRotationPoint(0.9968F, -0.7341F, 0.5755F);
				finger3.addChild(cube_r5);
				setRotationAngle(cube_r5, 0.0999F, -0.5148F, -0.2009F);
				cube_r5.cubeList.add(new ModelBox(cube_r5, 120, 35, -1.925F, -3.0F, -1.0F, 2, 4, 2, 0.0F, true));
				ModelRenderer cube_r6 = new ModelRenderer(this);
				cube_r6.setRotationPoint(-0.1356F, 1.8444F, -0.0783F);
				finger3.addChild(cube_r6);
				setRotationAngle(cube_r6, -0.1719F, -0.4971F, 0.3492F);
				cube_r6.cubeList.add(new ModelBox(cube_r6, 120, 42, -1.0F, -2.3F, -1.0F, 2, 4, 2, -0.25F, true));
				ModelRenderer finger2 = new ModelRenderer(this);
				finger2.setRotationPoint(0.0F, 0.0F, 4.0F);
				sandHand.addChild(finger2);
				setRotationAngle(finger2, 0.0F, 0.0F, 0.1309F);
				ModelRenderer cube_r7 = new ModelRenderer(this);
				cube_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
				finger2.addChild(cube_r7);
				setRotationAngle(cube_r7, 0.0999F, 0.5148F, 0.2009F);
				cube_r7.cubeList.add(new ModelBox(cube_r7, 120, 35, -0.075F, -3.0F, -1.0F, 2, 4, 2, 0.0F, false));
				ModelRenderer cube_r8 = new ModelRenderer(this);
				cube_r8.setRotationPoint(1.1324F, 2.5785F, -0.6538F);
				finger2.addChild(cube_r8);
				setRotationAngle(cube_r8, -0.1719F, 0.4971F, -0.3492F);
				cube_r8.cubeList.add(new ModelBox(cube_r8, 120, 42, -1.0F, -2.3F, -1.0F, 2, 4, 2, -0.25F, false));
				ModelRenderer bump2 = new ModelRenderer(this);
				bump2.setRotationPoint(0.4421F, -2.584F, 3.21F);
				sandArm.addChild(bump2);
				ModelRenderer cube_r9 = new ModelRenderer(this);
				cube_r9.setRotationPoint(0.0F, 0.0F, 0.2F);
				bump2.addChild(cube_r9);
				setRotationAngle(cube_r9, -0.4102F, -0.4102F, -0.7854F);
				cube_r9.cubeList.add(new ModelBox(cube_r9, 95, 0, -2.0F, -2.0F, -2.0F, 4, 4, 4, 0.6F, false));
				ModelRenderer bump = new ModelRenderer(this);
				bump.setRotationPoint(0.4421F, -8.584F, 3.21F);
				sandArm.addChild(bump);
				setRotationAngle(bump, 0.0F, 0.0F, 0.48F);
				ModelRenderer cube_r10 = new ModelRenderer(this);
				cube_r10.setRotationPoint(0.0F, 0.0F, 0.0F);
				bump.addChild(cube_r10);
				setRotationAngle(cube_r10, -0.4102F, -0.4102F, -0.7854F);
				cube_r10.cubeList.add(new ModelBox(cube_r10, 95, 0, -1.9203F, -2.0731F, -1.8318F, 4, 4, 4, 0.6F, false));
				ModelRenderer sparefingers = new ModelRenderer(this);
				sparefingers.setRotationPoint(0.0F, 0.0F, 0.0F);
				sandArm.addChild(sparefingers);
				ModelRenderer cube_r11 = new ModelRenderer(this);
				cube_r11.setRotationPoint(0.0F, 0.0F, 0.0F);
				sparefingers.addChild(cube_r11);
				setRotationAngle(cube_r11, -1.6095F, -1.0268F, 1.3404F);
				cube_r11.cubeList.add(new ModelBox(cube_r11, 120, 42, -0.25F, -2.0F, -1.0F, 2, 4, 2, -0.25F, false));
				ModelRenderer cube_r12 = new ModelRenderer(this);
				cube_r12.setRotationPoint(-1.3089F, -4.5636F, 3.7447F);
				sparefingers.addChild(cube_r12);
				setRotationAngle(cube_r12, 0.1512F, 0.1609F, 0.7314F);
				cube_r12.cubeList.add(new ModelBox(cube_r12, 120, 42, -2.475F, -2.0F, -1.0F, 2, 4, 2, -0.25F, false));
				ModelRenderer cube_r13 = new ModelRenderer(this);
				cube_r13.setRotationPoint(0.5218F, 0.2381F, 6.7784F);
				sparefingers.addChild(cube_r13);
				setRotationAngle(cube_r13, 1.6292F, 0.9065F, 1.702F);
				cube_r13.cubeList.add(new ModelBox(cube_r13, 120, 42, -1.0F, -3.0F, -2.175F, 2, 4, 2, -0.25F, false));
				ModelRenderer cube_r14 = new ModelRenderer(this);
				cube_r14.setRotationPoint(2.5926F, -1.7054F, 6.8658F);
				sparefingers.addChild(cube_r14);
				setRotationAngle(cube_r14, 1.1536F, 0.8762F, 0.7801F);
				cube_r14.cubeList.add(new ModelBox(cube_r14, 120, 42, -2.15F, -5.25F, -0.625F, 2, 4, 2, -0.25F, false));
			}
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 32, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.6F, false));
			bipedLeftArmWear = new ModelRenderer(this);
			bipedLeftArmWear.setRotationPoint(5.0F, 2.0F, 0.0F);
			bipedLeftArmWear.cubeList.add(new ModelBox(bipedLeftArmWear, 96, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.65F, false));
			bipedLeftArmWear.cubeList.add(new ModelBox(bipedLeftArmWear, 112, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.7F, false));
			bipedRightLeg = new ModelRenderer(this);
			bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
			bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.6F, false));
			bipedRightLegWear = new ModelRenderer(this);
			bipedRightLegWear.setRotationPoint(-1.9F, 12.0F, 0.0F);
			bipedRightLegWear.cubeList.add(new ModelBox(bipedRightLegWear, 64, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.65F, false));
			bipedRightLegWear.cubeList.add(new ModelBox(bipedRightLegWear, 64, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.7F, false));
			bipedLeftLeg = new ModelRenderer(this);
			bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
			bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 16, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.6F, false));
			bipedLeftLegWear = new ModelRenderer(this);
			bipedLeftLegWear.setRotationPoint(1.9F, 12.0F, 0.0F);
			bipedLeftLegWear.cubeList.add(new ModelBox(bipedLeftLegWear, 80, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.65F, false));
			bipedLeftLegWear.cubeList.add(new ModelBox(bipedLeftLegWear, 64, 48, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.7F, false));

			for (int i = 1; i < 6; i++) {
				leftEarSwayX[i] = (rand.nextFloat() * 0.2618F + 0.0873F) * (rand.nextBoolean() ? -1F : 1F);
				leftEarSwayZ[i] = (rand.nextFloat() * 0.2618F + 0.0873F) * (rand.nextBoolean() ? -1F : 1F);
				rightEarSwayX[i] = (rand.nextFloat() * 0.2618F + 0.0873F) * (rand.nextBoolean() ? -1F : 1F);
				rightEarSwayZ[i] = (rand.nextFloat() * 0.2618F + 0.0873F) * (rand.nextBoolean() ? -1F : 1F);
			}
			for (int i = 0; i < 9; i++) {
				for (int j = 1; j < 8; j++) {
					tailSwayX[i][j] = (rand.nextFloat() * 0.1745F + 0.1745F) * (rand.nextBoolean() ? -1F : 1F);
					tailSwayZ[i][j] = (rand.nextFloat() * 0.2618F + 0.2618F) * (rand.nextBoolean() ? -1F : 1F);
				}
			}
			this.setModelVisibilities(tails);
		}

		private void setModelVisibilities(int numberoftails) {
			int j = this.tailShowMap[numberoftails];
			for (int i = 0; i < 9; i++) {
				tail[i][0].showModel = (j & (1 << i)) != 0;
			}
			if (numberoftails != 1) {
				sandArm.showModel = false;
			} else {
				earLeft[0].showModel = false;
				earRight[0].showModel = false;
			}
		}

		@Override
		public void render(Entity entity, float f0, float f1, float f2, float f3, float f4, float f5) {
			bipedHeadwear.showModel = false;
			bipedBody.showModel = bipedBody.showModel && !bipedRightLeg.showModel && !bipedLeftLeg.showModel;
			GlStateManager.pushMatrix();
			GlStateManager.depthMask(true);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, f2 * 0.01F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, MathHelper.clamp((float)getWearingTicks(entity) / 80.0F, 0.0F, 1.0F));
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			int k = entity.getBrightnessForRender();
			if (this.bodyShine) {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			} else {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(k % 65536), (float)(k / 65536));
			}
			Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
			super.render(entity, f0, f1, f2, f3, f4, f5);// + MathHelper.sin(f2 * 0.1f) * 0.003125F + 0.00625F);
			Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			if (this.layerShine) {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
			} else {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(k % 65536), (float)(k / 65536));
			}
			if (entity.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}
			this.copyModelAngles(bipedBody, bipedBodyWear);
			this.copyModelAngles(bipedRightArm, bipedRightArmWear);
			this.copyModelAngles(bipedLeftArm, bipedLeftArmWear);
			this.copyModelAngles(bipedRightLeg, bipedRightLegWear);
			this.copyModelAngles(bipedLeftLeg, bipedLeftLegWear);
			bipedHeadwear.showModel = bipedHead.showModel;
			bipedBodyWear.showModel = bipedBody.showModel;
			bipedRightArmWear.showModel = bipedRightArm.showModel;
			bipedLeftArmWear.showModel = bipedLeftArm.showModel;
			bipedRightLegWear.showModel = bipedRightLeg.showModel;
			bipedLeftLegWear.showModel = bipedLeftLeg.showModel;
			bipedHeadwear.render(f5);
			bipedBodyWear.render(f5);
			bipedRightArmWear.render(f5);
			bipedLeftArmWear.render(f5);
			bipedRightLegWear.render(f5);
			bipedLeftLegWear.render(f5);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(false);
			GlStateManager.popMatrix();
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		@Override
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			if (this.narutoRunPose) {
				this.isSneak = true;
			}
			super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
			for (int i = 1; i < 6; i++) {
				earLeft[i].rotateAngleX = -0.1745F + MathHelper.sin(f2 * 0.15F) * leftEarSwayX[i];
				earLeft[i].rotateAngleZ = MathHelper.cos(f2 * 0.15F) * leftEarSwayZ[i];
				earRight[i].rotateAngleX = 0.1745F + MathHelper.sin(f2 * 0.15F) * rightEarSwayX[i];
				earRight[i].rotateAngleZ = MathHelper.cos(f2 * 0.15F) * rightEarSwayZ[i];
			}
			for (int i = 0; i < 9; i++) {
				for (int j = 2; j < 8; j++) {
					tail[i][j].rotateAngleX = 0.2618F + MathHelper.sin(f2 * 0.15F) * tailSwayX[i][j];
					tail[i][j].rotateAngleZ = MathHelper.cos(f2 * 0.15F) * tailSwayZ[i][j];
					if (i == 0) {
						tailWear[i][j].rotateAngleX = tail[i][j].rotateAngleX;
						tailWear[i][j].rotateAngleZ = tail[i][j].rotateAngleZ;
					}
				}
			}
			if (this.narutoRunPose) {
				bipedRightArm.rotateAngleX = 1.4835F;
				bipedRightArm.rotateAngleY = -0.3927F;
				bipedLeftArm.rotateAngleX = 1.4835F;
				bipedLeftArm.rotateAngleY = 0.3927F;
			}
		}

		public void setNarutoRunPose(boolean b) {
			this.narutoRunPose = b;
		}
	}
}
