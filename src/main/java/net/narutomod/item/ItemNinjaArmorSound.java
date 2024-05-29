
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;

@ElementsNarutomodMod.ModElement.Tag
public class ItemNinjaArmorSound extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_soundhelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:ninja_armor_soundbody")
	public static final Item body = null;

	public ItemNinjaArmorSound(ElementsNarutomodMod instance) {
		super(instance, 863);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.SOUND5, EntityEquipmentSlot.HEAD) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					this.model = new ModelSoundArmor();
					this.texture = "narutomod:textures/sound5armor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					this.model.bipedHeadwear.showModel = false;
				}
			}
		}.setUnlocalizedName("ninja_armor_soundhelmet").setRegistryName("ninja_armor_soundhelmet").setCreativeTab(TabModTab.tab));

		elements.items.add(() -> new ItemNinjaArmor.Base(ItemNinjaArmor.Type.SOUND5, EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ModelSoundArmor model1 = new ModelSoundArmor();
					model1.shirt.showModel = false;
					model1.shirtRightArm.showModel = false;
					model1.shirtLeftArm.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/sound5armor.png";
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible() {
					//this.model.bipedHeadwear.showModel = true;
				}
			}
		}.setUnlocalizedName("ninja_armor_soundbody").setRegistryName("ninja_armor_soundbody").setCreativeTab(TabModTab.tab));

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:ninja_armor_soundhelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:ninja_armor_soundbody", "inventory"));
	}

	public static class ModelSoundArmor extends ItemNinjaArmor.ModelNinjaArmor {
		public ModelSoundArmor() {
			super(ItemNinjaArmor.Type.SOUND5);
			ModelRenderer rope = new ModelRenderer(this);
			rope.setRotationPoint(0.0F, 0.0F, 0.0F);
			vest.addChild(rope);
			setRotationAngle(rope, 0.0F, 3.1416F, 0.0F);
			rope.cubeList.add(new ModelBox(rope, 36, 16, -4.5F, 9.0F, -2.5F, 9, 2, 5, 0.3F, false));
			ModelRenderer cube_r13 = new ModelRenderer(this);
			cube_r13.setRotationPoint(-2.5242F, 5.8793F, 5.6785F);
			rope.addChild(cube_r13);
			setRotationAngle(cube_r13, -0.005F, 0.1752F, -2.0393F);
			cube_r13.cubeList.add(new ModelBox(cube_r13, 40, 19, -1.9311F, -0.9553F, -9.8621F, 7, 2, 2, 0.0F, false));
			ModelRenderer cube_r14 = new ModelRenderer(this);
			cube_r14.setRotationPoint(2.5242F, 5.8793F, 5.6785F);
			rope.addChild(cube_r14);
			setRotationAngle(cube_r14, -0.005F, -0.1752F, 2.0393F);
			cube_r14.cubeList.add(new ModelBox(cube_r14, 40, 19, -5.0689F, -0.9553F, -9.8621F, 7, 2, 2, 0.0F, true));
			ModelRenderer loop2 = new ModelRenderer(this);
			loop2.setRotationPoint(0.8076F, 9.2563F, 6.0F);
			rope.addChild(loop2);
			setRotationAngle(loop2, 0.0F, 0.2618F, -0.3927F);
			loop2.cubeList.add(new ModelBox(loop2, 41, 19, 2.6835F, -1.3692F, -9.6933F, 6, 2, 2, 0.0F, true));
			ModelRenderer cube_r15 = new ModelRenderer(this);
			cube_r15.setRotationPoint(0.0F, 0.0F, 0.0F);
			loop2.addChild(cube_r15);
			setRotationAngle(cube_r15, 0.0F, 0.0F, 1.0385F);
			cube_r15.cubeList.add(new ModelBox(cube_r15, 41, 19, 0.1822F, -3.0071F, -9.6933F, 6, 2, 2, 0.0F, true));
			ModelRenderer cube_r16 = new ModelRenderer(this);
			cube_r16.setRotationPoint(1.8582F, 4.8775F, 0.0F);
			loop2.addChild(cube_r16);
			setRotationAngle(cube_r16, 0.0F, 0.0F, 2.0857F);
			cube_r16.cubeList.add(new ModelBox(cube_r16, 41, 19, -7.1062F, -3.8394F, -9.6933F, 6, 2, 2, 0.0F, true));
			ModelRenderer loop = new ModelRenderer(this);
			loop.setRotationPoint(-0.8076F, 9.2563F, 6.0F);
			rope.addChild(loop);
			setRotationAngle(loop, 0.0F, -0.2618F, 0.3927F);
			loop.cubeList.add(new ModelBox(loop, 41, 19, -8.6835F, -1.3692F, -9.6933F, 6, 2, 2, 0.0F, false));
			ModelRenderer cube_r17 = new ModelRenderer(this);
			cube_r17.setRotationPoint(0.0F, 0.0F, 0.0F);
			loop.addChild(cube_r17);
			setRotationAngle(cube_r17, 0.0F, 0.0F, -1.0385F);
			cube_r17.cubeList.add(new ModelBox(cube_r17, 41, 19, -6.1822F, -3.0071F, -9.6933F, 6, 2, 2, 0.0F, false));
			ModelRenderer cube_r18 = new ModelRenderer(this);
			cube_r18.setRotationPoint(-1.8582F, 4.8775F, 0.0F);
			loop.addChild(cube_r18);
			setRotationAngle(cube_r18, 0.0F, 0.0F, -2.0857F);
			cube_r18.cubeList.add(new ModelBox(cube_r18, 41, 19, 1.1062F, -3.8394F, -9.6933F, 6, 2, 2, 0.0F, false));
		}
	}
}
