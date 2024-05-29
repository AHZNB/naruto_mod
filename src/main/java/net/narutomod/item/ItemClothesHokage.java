
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;

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
		elements.items.add(() -> new ItemRobe.Base(EntityEquipmentSlot.HEAD) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelRobeHokage();
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
		elements.items.add(() -> new ItemRobe.Base(EntityEquipmentSlot.CHEST) {
			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelRobeHokage();
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
	public static class ModelRobeHokage extends ItemRobe.ModelRobe {
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
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
		public ModelRobeHokage() {
			super();

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
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
