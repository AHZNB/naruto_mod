
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
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAnbuRobe extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:anbu_robebody")
	public static final Item body = null;

	public ItemAnbuRobe(ElementsNarutomodMod instance) {
		super(instance, 828);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("ANBU_ROBE", "narutomod:sasuke_", 100, new int[]{1, 2, 3, 1}, 0,
				(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("")), 0f);
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.OTHER, enuma, EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ModelRobe();
					this.texture = "narutomod:textures/robe_hood.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = true;
				}
			}
		}.setUnlocalizedName("anbu_robebody").setRegistryName("anbu_robebody").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:anbu_robebody", "inventory"));
	}
	// Made with Blockbench 4.6.1
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelRobe extends ModelBiped {
		//private final ModelRenderer bipedHead;
		//private final ModelRenderer bipedHeadwear;
		//private final ModelRenderer bipedBody;
		private final ModelRenderer bone;
		private final ModelRenderer bone8;
		//private final ModelRenderer collar;
		//private final ModelRenderer bone6;
		//private final ModelRenderer collar2;
		//private final ModelRenderer bone2;
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
		public ModelRobe() {
			textureWidth = 64;
			textureHeight = 64;
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
