
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
import net.minecraft.world.World;

@ElementsNarutomodMod.ModElement.Tag
public class ItemMaskAnbu1 extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:mask_anbu_1helmet")
	public static final Item helmet = null;
	public static ItemArmor.ArmorMaterial ENUMA = EnumHelper.addArmorMaterial("NINJA_MASK", "narutomod:sasuke_", 25, new int[]{2, 5, 6, 2}, 0, null, 0f);

	public ItemMaskAnbu1(ElementsNarutomodMod instance) {
		super(instance, 809);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemArmor(ENUMA, 0, EntityEquipmentSlot.HEAD) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelAnbuMask();
				}

				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (entity.ticksExisted % 10 == 6 && entity instanceof EntityLivingBase) {
					entity.setAlwaysRenderNameTag(!((EntityLivingBase)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD).equals(itemstack));
				}
			}
			
			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/mask_anbu1.png";
			}
		}.setUnlocalizedName("mask_anbu_1helmet").setRegistryName("mask_anbu_1helmet").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:mask_anbu_1helmet", "inventory"));
	}
	// Made with Blockbench 4.5.2
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelAnbuMask extends ModelBiped {
		//private final ModelRenderer bipedHead;
		private final ModelRenderer mask;
		private final ModelRenderer band;
		private final ModelRenderer bone4;
		private final ModelRenderer cube_r1;
		private final ModelRenderer bone3;
		private final ModelRenderer cube_r2;
		private final ModelRenderer catears;
		private final ModelRenderer bone6;
		private final ModelRenderer cube_r3;
		private final ModelRenderer bone7;
		private final ModelRenderer cube_r4;
		private final ModelRenderer horns;
		private final ModelRenderer bone8;
		private final ModelRenderer cube_r5;
		private final ModelRenderer cube_r6;
		private final ModelRenderer bone5;
		private final ModelRenderer cube_r7;
		private final ModelRenderer cube_r8;
		//private final ModelRenderer bipedHeadwear;
		public ModelAnbuMask() {
			textureWidth = 32;
			textureHeight = 32;
			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
			mask = new ModelRenderer(this);
			mask.setRotationPoint(0.0F, 24.0F, 0.0F);
			bipedHead.addChild(mask);
			mask.cubeList.add(new ModelBox(mask, 0, 1, -4.0F, -32.0F, -4.0F, 8, 8, 8, 0.27F, false));
			band = new ModelRenderer(this);
			band.setRotationPoint(1.4555F, -0.6486F, 4.8886F);
			bipedHead.addChild(band);
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(-2.911F, 0.0F, 0.0F);
			band.addChild(bone4);
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone4.addChild(cube_r1);
			setRotationAngle(cube_r1, 0.1745F, 0.0F, 0.2618F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -1.0F, -4.0F, 0.0F, 2, 8, 0, 0.0F, true));
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(0.0F, 0.0F, 0.0F);
			band.addChild(bone3);
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone3.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.1745F, 0.0F, -0.2618F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 0, -1.0F, -4.0F, 0.0F, 2, 8, 0, 0.0F, false));
			catears = new ModelRenderer(this);
			catears.setRotationPoint(0.0F, 24.0F, 0.0F);
			bipedHead.addChild(catears);
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(2.7309F, -30.5634F, -3.67F);
			catears.addChild(bone6);
			setRotationAngle(bone6, 0.3084F, 0.002F, 0.1379F);
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone6.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.0F, 0.0F, -0.6109F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 24, 0, -1.5F, -1.5F, -0.5F, 3, 3, 1, 0.0F, false));
			bone7 = new ModelRenderer(this);
			bone7.setRotationPoint(-2.7309F, -30.5634F, -3.67F);
			catears.addChild(bone7);
			setRotationAngle(bone7, 0.3084F, -0.002F, -0.1379F);
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone7.addChild(cube_r4);
			setRotationAngle(cube_r4, 0.0F, 0.0F, 0.6109F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 24, 0, -1.5F, -1.5F, -0.5F, 3, 3, 1, 0.0F, true));
			horns = new ModelRenderer(this);
			horns.setRotationPoint(-2.5F, -7.5F, -4.5F);
			bipedHead.addChild(horns);
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(5.0F, 0.0F, 0.0F);
			horns.addChild(bone8);
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone8.addChild(cube_r5);
			setRotationAngle(cube_r5, -0.5213F, -0.6385F, 0.0751F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 23, 22, -0.175F, -0.35F, -1.5F, 1, 1, 3, 0.2F, true));
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(1.8582F, -3.9999F, -1.5468F);
			bone8.addChild(cube_r6);
			setRotationAngle(cube_r6, -1.2194F, -0.6385F, 0.0751F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 23, 22, -0.5F, 0.1F, 0.95F, 1, 1, 3, -0.1F, true));
			bone5 = new ModelRenderer(this);
			bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
			horns.addChild(bone5);
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone5.addChild(cube_r7);
			setRotationAngle(cube_r7, -0.5213F, 0.6385F, -0.0751F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 23, 22, -0.825F, -0.35F, -1.5F, 1, 1, 3, 0.2F, false));
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(-1.8582F, -3.9999F, -1.5468F);
			bone5.addChild(cube_r8);
			setRotationAngle(cube_r8, -1.2194F, 0.6385F, -0.0751F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 23, 22, -0.5F, 0.1F, 0.95F, 1, 1, 3, -0.1F, false));
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 0, 23, -4.0F, -8.0F, -4.01F, 8, 8, 0, 0.0F, true));
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
