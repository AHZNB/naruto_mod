
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.minecraft.client.renderer.GlStateManager;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAkatsukiRobe extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:akatsuki_robehelmet")
	public static final Item helmet = null;
	@GameRegistry.ObjectHolder("narutomod:akatsuki_robebody")
	public static final Item body = null;

	public ItemAkatsukiRobe(ElementsNarutomodMod instance) {
		super(instance, 740);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemRobe.Base(EntityEquipmentSlot.HEAD) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ModelAkatsukiRobe model1 = new ModelAkatsukiRobe();
					model1.veil.showModel = true;
					model1.collar.showModel = false;
					model1.collar2.showModel = false;
					this.model = model1;
					this.texture = "narutomod:textures/robe_akatsuki_open.png";
				}
			}
		}.setUnlocalizedName("akatsuki_robehelmet").setRegistryName("akatsuki_robehelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemRobe.Base(EntityEquipmentSlot.CHEST) {
			@Override
			protected ItemNinjaArmor.ArmorData setArmorData(ItemNinjaArmor.Type type, EntityEquipmentSlot slotIn) {
				return new Armor4Slot();
			}

			class Armor4Slot extends ItemNinjaArmor.ArmorData {
				private final String textureOpened = "narutomod:textures/robe_akatsuki_open.png";
				private final String textureClosed = "narutomod:textures/robe_akatsuki_closed.png";
				private final String texturePartial = "narutomod:textures/robe_akatsuki_half.png";

				@SideOnly(Side.CLIENT)
				@Override
				protected void init() {
					ModelAkatsukiRobe model1 = new ModelAkatsukiRobe();
					model1.veil.showModel = false;
					model1.collar.showModel = true;
					model1.collar2.showModel = true;
					this.model = model1;
					this.texture = this.textureOpened;
				}
				@SideOnly(Side.CLIENT)
				@Override
				public void setSlotVisible(ItemStack stack, Entity entity, EntityEquipmentSlot slot) {
					this.model.bipedHeadwear.showModel = true;
					((ModelAkatsukiRobe)this.model).collar.rotateAngleX = 0.0f;
					((ModelAkatsukiRobe)this.model).collar2.rotateAngleX = 0.0f;
					if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("halfOff")) {
						this.model.bipedRightArm.showModel = false;
						this.texture = this.texturePartial;
					} else if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("collarOpen")) {
						this.texture = this.textureOpened;
					} else {
						((ModelAkatsukiRobe)this.model).collar.rotateAngleX = 0.0436f;
						((ModelAkatsukiRobe)this.model).collar2.rotateAngleX = 0.0436f;
						this.texture = this.textureClosed;
					}
				}
			}

			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				ModelAkatsukiRobe model = (ModelAkatsukiRobe)super.getArmorModel(living, stack, slot, defaultModel);
				float modifier = stack.hasTagCompound() ? stack.getTagCompound().getFloat("customWidth") : 0f;
				model.widthModifier = modifier > 0.0f ? modifier : 1.0f;
				return model;
			}
		}.setUnlocalizedName("akatsuki_robebody").setRegistryName("akatsuki_robebody").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(helmet, 0, new ModelResourceLocation("narutomod:akatsuki_robehelmet", "inventory"));
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:akatsuki_robebody", "inventory"));
	}

	// Made with Blockbench 4.3.1
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelAkatsukiRobe extends ItemRobe.ModelRobe {
		//private final ModelRenderer bipedHead;
		private final ModelRenderer Hat;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r2;
		private final ModelRenderer cube_r3;
		private final ModelRenderer cube_r4;
		private final ModelRenderer cube_r5;
		private final ModelRenderer cube_r6;
		private final ModelRenderer cube_r7;
		private final ModelRenderer cube_r8;
		private final ModelRenderer cube_r9;
		private final ModelRenderer cube_r10;
		private final ModelRenderer cube_r11;
		private final ModelRenderer cube_r12;
		private final ModelRenderer cube_r13;
		private final ModelRenderer cube_r14;
		private final ModelRenderer cube_r15;
		private final ModelRenderer cube_r16;
		private final ModelRenderer Bell_r1;
		private final ModelRenderer Bell_r2;
		//private final ModelRenderer bipedHeadwear;
		public final ModelRenderer veil;
		//private final ModelRenderer bipedBody;
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
		public float widthModifier = 1.0F;
	
		public ModelAkatsukiRobe() {
			super();

			bipedHead = new ModelRenderer(this);
			bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				
			Hat = new ModelRenderer(this);
			Hat.setRotationPoint(0.0F, -5.5F, 0.0F);
			bipedHead.addChild(Hat);
			setRotationAngle(Hat, -0.0436F, 0.0F, 0.0F);
			
	
			Bell_r1 = new ModelRenderer(this);
			Bell_r1.setRotationPoint(-3.825F, 0.0F, -9.0F);
			Hat.addChild(Bell_r1);
			setRotationAngle(Bell_r1, 0.0F, 2.0071F, 0.0F);
			Bell_r1.cubeList.add(new ModelBox(Bell_r1, 52, 16, 0.0F, 0.35F, -1.0F, 0, 7, 2, 0.0F, false));
	
			Bell_r2 = new ModelRenderer(this);
			Bell_r2.setRotationPoint(-3.825F, 0.0F, -9.0F);
			Hat.addChild(Bell_r2);
			setRotationAngle(Bell_r2, 0.2182F, 0.3927F, 0.0F);
			Bell_r2.cubeList.add(new ModelBox(Bell_r2, 52, 24, 0.0F, -0.225F, -1.175F, 0, 2, 2, 0.0F, false));
	
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r1);
			setRotationAngle(cube_r1, 0.9599F, 0.3927F, 0.0F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.9599F, 0.7854F, 0.0F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.9599F, 1.1781F, 0.0F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r4);
			setRotationAngle(cube_r4, 0.9599F, 1.5708F, 0.0F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.9599F, 1.9635F, 0.0F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r6);
			setRotationAngle(cube_r6, 0.9599F, 2.3562F, 0.0F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r7);
			setRotationAngle(cube_r7, 0.9599F, 2.7489F, 0.0F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r8);
			setRotationAngle(cube_r8, 0.9599F, 3.1416F, 0.0F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r9);
			setRotationAngle(cube_r9, 0.9599F, -2.7489F, 0.0F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r10);
			setRotationAngle(cube_r10, 0.9599F, -2.3562F, 0.0F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r11 = new ModelRenderer(this);
			cube_r11.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r11);
			setRotationAngle(cube_r11, 0.9599F, -1.9635F, 0.0F);
			cube_r11.cubeList.add(new ModelBox(cube_r11, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r12 = new ModelRenderer(this);
			cube_r12.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r12);
			setRotationAngle(cube_r12, 0.9599F, -1.5708F, 0.0F);
			cube_r12.cubeList.add(new ModelBox(cube_r12, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r13 = new ModelRenderer(this);
			cube_r13.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r13);
			setRotationAngle(cube_r13, 0.9599F, -1.1781F, 0.0F);
			cube_r13.cubeList.add(new ModelBox(cube_r13, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r14 = new ModelRenderer(this);
			cube_r14.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r14);
			setRotationAngle(cube_r14, 0.9599F, -0.7854F, 0.0F);
			cube_r14.cubeList.add(new ModelBox(cube_r14, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r15 = new ModelRenderer(this);
			cube_r15.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r15);
			setRotationAngle(cube_r15, 0.9599F, -0.3927F, 0.0F);
			cube_r15.cubeList.add(new ModelBox(cube_r15, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			cube_r16 = new ModelRenderer(this);
			cube_r16.setRotationPoint(0.0F, -6.5F, 0.0F);
			Hat.addChild(cube_r16);
			setRotationAngle(cube_r16, 0.9599F, 0.0F, 0.0F);
			cube_r16.cubeList.add(new ModelBox(cube_r16, 56, 16, -2.0F, 0.5F, 0.0F, 4, 12, 0, 0.0F, false));
	
			veil = new ModelRenderer(this);
			veil.setRotationPoint(0.0F, -0.8F, 0.0F);
			bipedHeadwear.addChild(veil);
			veil.cubeList.add(new ModelBox(veil, 32, 0, -4.0F, -7.8F, -4.0F, 8, 8, 8, 2.0F, false));
		}

		@Override
		public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			if (this.widthModifier != 1.0F) {
				GlStateManager.pushMatrix();
				GlStateManager.scale(this.widthModifier, 1.0F, this.widthModifier);
			}
			super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			if (this.widthModifier != 1.0F) {
				GlStateManager.popMatrix();
			}
		}
	}
}
