
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.init.SoundEvents;

@ElementsNarutomodMod.ModElement.Tag
public class ItemClothesHokage extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:clothes_hokagehelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:clothes_hokagebody")
	public static final Item body = null;

	public ItemClothesHokage(ElementsNarutomodMod instance) {
		super(instance, 816);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("CLOTHES_HOKAGE", "narutomod:sasuke_", 100, new int[]{2, 5, 6, 2}, 0,
				SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0f);
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.HEAD) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelRobe();
				}

				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/robe_hokage.png";
			}
		}.setUnlocalizedName("clothes_hokagehelmet").setRegistryName("clothes_hokagehelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelRobe();
				}

				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/robe_hokage.png";
			}
		}.setUnlocalizedName("clothes_hokagebody").setRegistryName("clothes_hokagebody").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:clothes_hokagehelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:clothes_hokagebody", "inventory"));
	}
	// Made with Blockbench 4.5.2
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelRobe extends ModelBiped {
		//private final ModelRenderer bipedHead;
		private final ModelRenderer hatKage;
		private final ModelRenderer pipes2;
		private final ModelRenderer pipe_r1;
		private final ModelRenderer pipe_r2;
		private final ModelRenderer pipes;
		private final ModelRenderer pipe_r3;
		private final ModelRenderer pipe_r4;
		private final ModelRenderer hhat2;
		private final ModelRenderer bone22;
		private final ModelRenderer cube_r17;
		private final ModelRenderer bone24;
		private final ModelRenderer cube_r18;
		private final ModelRenderer hhat;
		private final ModelRenderer logo3;
		private final ModelRenderer logo2;
		private final ModelRenderer cube_r19;
		private final ModelRenderer logo;
		private final ModelRenderer cube_r20;
		private final ModelRenderer bone23;
		private final ModelRenderer cube_r21;
		private final ModelRenderer bone21;
		private final ModelRenderer cube_r22;
		//private final ModelRenderer bipedHeadwear;
		//private final ModelRenderer bipedBody;
		private final ModelRenderer bone;
		private final ModelRenderer bone8;
		private final ModelRenderer collar;
		private final ModelRenderer bone6;
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
		public ModelRobe() {
			textureWidth = 64;
			textureHeight = 64;

			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);

			hatKage = new ModelRenderer(this);
			hatKage.setRotationPoint(0.0F, -0.075F, 0.0F);
			bipedHead.addChild(hatKage);
			pipes2 = new ModelRenderer(this);
			pipes2.setRotationPoint(4.771F, -8.9446F, 0.0F);
			hatKage.addChild(pipes2);
			pipe_r1 = new ModelRenderer(this);
			pipe_r1.setRotationPoint(0.0F, 0.0F, -1.7125F);
			pipes2.addChild(pipe_r1);
			setRotationAngle(pipe_r1, -1.5708F, 1.0472F, 2.138F);
			pipe_r1.cubeList.add(new ModelBox(pipe_r1, 30, 0, -2.0F, 0.0F, -1.0F, 4, 0, 2, 0.0F, false));
			pipe_r2 = new ModelRenderer(this);
			pipe_r2.setRotationPoint(0.879F, -0.0304F, 2.0F);
			pipes2.addChild(pipe_r2);
			setRotationAngle(pipe_r2, -1.5708F, -1.0472F, 2.138F);
			pipe_r2.cubeList.add(new ModelBox(pipe_r2, 30, 0, -2.0F, 0.575F, -0.275F, 4, 0, 2, 0.0F, false));
			pipes = new ModelRenderer(this);
			pipes.setRotationPoint(3.121F, -9.8946F, 0.0F);
			hatKage.addChild(pipes);
			pipe_r3 = new ModelRenderer(this);
			pipe_r3.setRotationPoint(0.0F, 0.0F, -1.7125F);
			pipes.addChild(pipe_r3);
			setRotationAngle(pipe_r3, -1.5708F, 1.0472F, 2.138F);
			pipe_r3.cubeList.add(new ModelBox(pipe_r3, 30, 0, -2.0F, 0.0F, -1.0F, 4, 0, 2, 0.0F, false));
			pipe_r4 = new ModelRenderer(this);
			pipe_r4.setRotationPoint(0.879F, -0.0304F, 2.0F);
			pipes.addChild(pipe_r4);
			setRotationAngle(pipe_r4, -1.5708F, -1.0472F, 2.138F);
			pipe_r4.cubeList.add(new ModelBox(pipe_r4, 30, 0, -2.0F, 0.575F, -0.275F, 4, 0, 2, 0.0F, false));
			hhat2 = new ModelRenderer(this);
			hhat2.setRotationPoint(0.0F, -8.75F, 0.0F);
			hatKage.addChild(hhat2);
			setRotationAngle(hhat2, 0.0F, 2.3562F, 0.0F);
			bone22 = new ModelRenderer(this);
			bone22.setRotationPoint(0.866F, -0.25F, -4.0354F);
			hhat2.addChild(bone22);
			setRotationAngle(bone22, 0.0F, 3.1416F, 0.0F);
			cube_r17 = new ModelRenderer(this);
			cube_r17.setRotationPoint(0.866F, 2.5F, 3.392F);
			bone22.addChild(cube_r17);
			setRotationAngle(cube_r17, -0.6589F, 0.0F, 0.0F);
			cube_r17.cubeList.add(new ModelBox(cube_r17, -10, 0, -7.5F, -0.0361F, -9.9375F, 15, 0, 10, 0.0F, false));
			bone24 = new ModelRenderer(this);
			bone24.setRotationPoint(0.866F, -0.25F, 11.6146F);
			hhat2.addChild(bone24);
			cube_r18 = new ModelRenderer(this);
			cube_r18.setRotationPoint(-0.866F, 2.5F, -4.2037F);
			bone24.addChild(cube_r18);
			setRotationAngle(cube_r18, -0.6589F, 0.0F, 0.0F);
			cube_r18.cubeList.add(new ModelBox(cube_r18, -10, 0, -7.5F, -0.0361F, -9.9375F, 15, 0, 10, 0.0F, false));
			hhat = new ModelRenderer(this);
			hhat.setRotationPoint(0.0F, -8.75F, 0.0F);
			hatKage.addChild(hhat);
			setRotationAngle(hhat, 0.0F, 0.7854F, 0.0F);
			logo3 = new ModelRenderer(this);
			logo3.setRotationPoint(0.15F, 32.85F, -0.1F);
			hhat.addChild(logo3);
			logo2 = new ModelRenderer(this);
			logo2.setRotationPoint(0.0F, -33.7014F, -3.6204F);
			logo3.addChild(logo2);
			setRotationAngle(logo2, 0.0F, 1.5708F, 0.0F);
			cube_r19 = new ModelRenderer(this);
			cube_r19.setRotationPoint(-3.5459F, 3.0514F, 7.4279F);
			logo2.addChild(cube_r19);
			setRotationAngle(cube_r19, -0.6589F, 0.0F, 0.0F);
			cube_r19.cubeList.add(new ModelBox(cube_r19, 23, 16, -0.5F, -0.0361F, -8.9375F, 8, 0, 9, 0.0F, false));
			logo = new ModelRenderer(this);
			logo.setRotationPoint(0.866F, -33.15F, -4.1104F);
			logo3.addChild(logo);
			setRotationAngle(logo, 0.0F, 3.1416F, 0.0F);
			cube_r20 = new ModelRenderer(this);
			cube_r20.setRotationPoint(0.866F, 2.5F, 3.392F);
			logo.addChild(cube_r20);
			setRotationAngle(cube_r20, -0.6589F, 0.0F, 0.0F);
			cube_r20.cubeList.add(new ModelBox(cube_r20, 39, 16, -7.5F, -0.0361F, -8.9375F, 8, 0, 9, 0.0F, false));
			bone23 = new ModelRenderer(this);
			bone23.setRotationPoint(0.866F, -0.25F, -4.0354F);
			hhat.addChild(bone23);
			setRotationAngle(bone23, 0.0F, 3.1416F, 0.0F);
			cube_r21 = new ModelRenderer(this);
			cube_r21.setRotationPoint(0.866F, 2.5F, 3.392F);
			bone23.addChild(cube_r21);
			setRotationAngle(cube_r21, -0.6589F, 0.0F, 0.0F);
			cube_r21.cubeList.add(new ModelBox(cube_r21, -10, 0, -7.5F, -0.0361F, -9.9375F, 15, 0, 10, 0.0F, false));
			bone21 = new ModelRenderer(this);
			bone21.setRotationPoint(0.866F, -0.25F, 11.6146F);
			hhat.addChild(bone21);
			cube_r22 = new ModelRenderer(this);
			cube_r22.setRotationPoint(-0.866F, 2.5F, -4.2037F);
			bone21.addChild(cube_r22);
			setRotationAngle(cube_r22, -0.6589F, 0.0F, 0.0F);
			cube_r22.cubeList.add(new ModelBox(cube_r22, -10, 0, -7.5F, -0.0361F, -9.9375F, 15, 0, 10, 0.0F, false));

			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -7.85F, -4.0F, 8, 8, 8, 1.0F, false));
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.addChild(bone);
			setRotationAngle(bone, 0.1222F, 0.0F, 0.0F);
			bone.cubeList.add(new ModelBox(bone, 40, 32, -4.0F, 0.0F, -2.0F, 8, 20, 4, 0.6F, false));
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.addChild(bone8);
			setRotationAngle(bone8, -0.1222F, 0.0F, 0.0F);
			bone8.cubeList.add(new ModelBox(bone8, 16, 32, -4.0F, 0.0F, -2.0F, 8, 20, 4, 0.6F, false));
			collar = new ModelRenderer(this);
			collar.setRotationPoint(0.0F, 0.0F, -1.8F);
			bipedBody.addChild(collar);
			setRotationAngle(collar, -0.2182F, 0.0F, 0.0F);
			collar.cubeList.add(new ModelBox(collar, 40, 56, -4.0F, -4.0F, 0.0F, 8, 4, 4, 1.5F, false));
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(0.0F, 0.35F, 3.25F);
			collar.addChild(bone6);
			setRotationAngle(bone6, -0.6109F, 0.0F, 0.0F);
			bone6.cubeList.add(new ModelBox(bone6, 20, 57, -4.0F, -6.0F, -1.0F, 8, 5, 2, 1.5F, false));
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, false));
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.5F, true));
		}


		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
