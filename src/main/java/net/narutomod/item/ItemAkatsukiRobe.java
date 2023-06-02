
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.init.SoundEvents;

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
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("AKATSUKI_ROBE", "narutomod:sasuke_",
		 100, new int[]{1, 2, 3, 1}, 9, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0f);
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.HEAD) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelAkatsukiRobe();
				}

				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
				if (player.getRNG().nextInt(200) == 0) {
					world.playSound(null, player.posX, player.posY, player.posZ,
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:dingding")),
					 net.minecraft.util.SoundCategory.PLAYERS, 0.8f, player.getRNG().nextFloat() * 0.1f + 0.95f);
				}
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/robe_akatsuki.png";
			}
		}.setUnlocalizedName("akatsuki_robehelmet").setRegistryName("akatsuki_robehelmet").setCreativeTab(TabModTab.tab));
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelAkatsukiRobe();
				}
				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/robe_akatsuki.png";
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
	public class ModelAkatsukiRobe extends ModelBiped {
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
		//private final ModelRenderer bipedBody;
		private final ModelRenderer bone;
		private final ModelRenderer bone8;
		private final ModelRenderer collar;
		private final ModelRenderer bone6;
		private final ModelRenderer collar2;
		private final ModelRenderer bone2;
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
	
		public ModelAkatsukiRobe() {
			textureWidth = 64;
			textureHeight = 64;
	
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
	
			bipedHeadwear = new ModelRenderer(this);
			bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.6F, -4.0F, 8, 8, 8, 2.0F, false));
	
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
	
			collar2 = new ModelRenderer(this);
			collar2.setRotationPoint(0.0F, 0.0F, -1.8F);
			bipedBody.addChild(collar2);
			setRotationAngle(collar2, -0.2182F, 0.0F, 0.0F);
			collar2.cubeList.add(new ModelBox(collar2, 20, 21, -4.0F, -4.0F, 0.0F, 8, 4, 4, 1.4F, false));
	
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 0.35F, 3.25F);
			collar2.addChild(bone2);
			setRotationAngle(bone2, -0.6109F, 0.0F, 0.0F);
			bone2.cubeList.add(new ModelBox(bone2, 0, 22, -4.0F, -6.0F, -1.0F, 8, 5, 2, 1.4F, false));
	
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

		@Override
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			if (e instanceof AbstractClientPlayer && ((AbstractClientPlayer)e).getSkinType().equals("slim")) {
				this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
				this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
			}
			super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
		}
	}
}
