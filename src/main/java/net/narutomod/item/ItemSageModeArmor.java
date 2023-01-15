
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Multimap;
import java.util.UUID;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSageModeArmor extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:sage_mode_armorhelmet")
	public static final Item helmet = null;
	public static final Map<IAttribute, AttributeModifier> buffMap = ImmutableMap.<IAttribute, AttributeModifier>builder()
		.put(EntityPlayer.REACH_DISTANCE, new AttributeModifier(UUID.fromString("c3ee1250-8b80-4668-b58a-33af5ea73ee6"), "sagemode.reach", 2.0d, 0))
		.put(SharedMonsterAttributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("6d6202e1-9aac-4c3d-ba0c-6684bdd58868"), "sagemode.damage", 60.0d, 0))
		.put(SharedMonsterAttributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("33b7fa14-828a-4964-b014-b61863526589"), "sagemode.damagespeed", 2.0d, 1))
		.put(SharedMonsterAttributes.MOVEMENT_SPEED, new AttributeModifier(UUID.fromString("74f3ab51-a73f-45e3-a4c4-aae6974b6414"), "sagemode.movement", 1.5d, 1))
		.put(SharedMonsterAttributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("70e0acc2-cf75-4bbd-a21a-753088324a59"), "sagemode.health", 80.0d, 0))
		.build();

	public ItemSageModeArmor(ElementsNarutomodMod instance) {
		super(instance, 685);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("SAGE_MODE_ARMOR", "narutomod:sasuke_", 1024, new int[]{2, 5, 6, 200}, 0,
				(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("")), 5f);
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.HEAD) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ModelBiped armorModel = new ModelHelmetSnug();
				armorModel.isSneak = living.isSneaking();
				armorModel.isRiding = living.isRiding();
				armorModel.isChild = living.isChild();
				return armorModel;
			}

			@Override
			public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
				Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
				if (slot == EntityEquipmentSlot.HEAD) {
					for (Map.Entry<IAttribute, AttributeModifier> entry : buffMap.entrySet()) {
						multimap.put(entry.getKey().getName(), entry.getValue());
					}
				}
				return multimap;
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
				return "narutomod:textures/sagesnakehelmet.png";
			}
		}.setUnlocalizedName("sage_mode_armorhelmet").setRegistryName("sage_mode_armorhelmet").setCreativeTab(null));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:sage_mode_armorhelmet", "inventory"));
	}
	// Made with Blockbench
	// Paste this code into your mod.
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
			this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
			GlStateManager.pushMatrix();
			if (entityIn.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}
			this.bipedHead.render(scale);
			this.bipedHeadwear.render(scale);
			if (this.bipedHead.showModel) {
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				this.copyModelAngles(this.bipedHead, this.highlight);
				this.highlight.render(scale);
				int i = entityIn.getBrightnessForRender();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(i % 65536), (float)(i / 65536));
				GlStateManager.enableLighting();
			}
			GlStateManager.popMatrix();
		}
	}
}
