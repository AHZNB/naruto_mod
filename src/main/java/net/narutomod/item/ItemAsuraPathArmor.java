package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;

import net.narutomod.procedure.ProcedureAsuraPathArmorBodyTickEvent;
import net.narutomod.ElementsNarutomodMod;

import java.util.HashMap;
import java.util.UUID;
import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAsuraPathArmor extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:asurapatharmorbody")
	public static final Item body = null;
	private static final AttributeModifier HEALTH_MODIFIER = new AttributeModifier(UUID.fromString("92f6ba90-c2b1-460d-a88e-5dd725be3c17"), "Asura bonus", 40d, 0);

	public ItemAsuraPathArmor(ElementsNarutomodMod instance) {
		super(instance, 210);
	}

	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("ASURAPATHARMOR", "narutomod:sasuke_", 1024, new int[]{2, 5, 1024, 2}, 0, null,
				5.0F);
		this.elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@SideOnly(Side.CLIENT)
			@Override
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelArmorCustom();
				}

				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/asura_path.png";
			}

			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				int x = (int) entity.posX;
				int y = (int) entity.posY;
				int z = (int) entity.posZ;
				HashMap<Object, Object> $_dependencies = new HashMap<>();
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", world);
				$_dependencies.put("itemstack", itemstack);
				ProcedureAsuraPathArmorBodyTickEvent.executeProcedure((HashMap) $_dependencies);
			}

			@Override
			public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
				Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
				if (equipmentSlot == EntityEquipmentSlot.CHEST) {
					multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), HEALTH_MODIFIER);
				}
				return multimap;
			}

			@Override
			public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
				return false;
			}
		}.setUnlocalizedName("asurapatharmorbody").setRegistryName("asurapatharmorbody").setCreativeTab(null));
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:asurapatharmorbody", "inventory"));
	}
	
	public static class ModelArmorCustom extends ModelBiped {
		//private final ModelRenderer bodywear;
		private final ModelRenderer tail;
		private final ModelRenderer tail2;
		private final ModelRenderer tail3;
		private final ModelRenderer tail4;
		private final ModelRenderer tail5;
		private final ModelRenderer tail6;
		private final ModelRenderer tail7;
		private final ModelRenderer tail8;
		private final ModelRenderer tail9;
		private final ModelRenderer tail10;
		private final ModelRenderer tail11;
		//private final ModelRenderer rightArmwear;
		//private final ModelRenderer leftArmwear;
		public ModelArmorCustom() {
			this.textureWidth = 64;
			this.textureHeight = 32;
			
			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.05F, false));
			bipedBody.cubeList.add(new ModelBox(bipedBody, 0, 0, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F, false));
	
			tail = new ModelRenderer(this);
			tail.setRotationPoint(0.0F, 9.0F, 2.0F);
			bipedBody.addChild(tail);
			setRotationAngle(tail, -0.7854F, 0.0F, 0.0F);
			tail.cubeList.add(new ModelBox(tail, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail2 = new ModelRenderer(this);
			tail2.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail.addChild(tail2);
			setRotationAngle(tail2, 0.2618F, 0.0F, 0.0F);
			tail2.cubeList.add(new ModelBox(tail2, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail3 = new ModelRenderer(this);
			tail3.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail2.addChild(tail3);
			setRotationAngle(tail3, 0.2618F, 0.0F, 0.0F);
			tail3.cubeList.add(new ModelBox(tail3, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail4 = new ModelRenderer(this);
			tail4.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail3.addChild(tail4);
			setRotationAngle(tail4, 0.2618F, 0.0F, 0.0F);
			tail4.cubeList.add(new ModelBox(tail4, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail5 = new ModelRenderer(this);
			tail5.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail4.addChild(tail5);
			setRotationAngle(tail5, 0.2618F, 0.0F, 0.0F);
			tail5.cubeList.add(new ModelBox(tail5, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail6 = new ModelRenderer(this);
			tail6.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail5.addChild(tail6);
			setRotationAngle(tail6, 0.2618F, 0.0F, 0.0F);
			tail6.cubeList.add(new ModelBox(tail6, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail7 = new ModelRenderer(this);
			tail7.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail6.addChild(tail7);
			setRotationAngle(tail7, 0.2618F, 0.0F, 0.0F);
			tail7.cubeList.add(new ModelBox(tail7, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail8 = new ModelRenderer(this);
			tail8.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail7.addChild(tail8);
			setRotationAngle(tail8, 0.2618F, 0.0F, 0.0F);
			tail8.cubeList.add(new ModelBox(tail8, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail9 = new ModelRenderer(this);
			tail9.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail8.addChild(tail9);
			setRotationAngle(tail9, 0.2618F, 0.0F, 0.0F);
			tail9.cubeList.add(new ModelBox(tail9, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail10 = new ModelRenderer(this);
			tail10.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail9.addChild(tail10);
			setRotationAngle(tail10, 0.2618F, 0.0F, 0.0F);
			tail10.cubeList.add(new ModelBox(tail10, 48, 0, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			tail11 = new ModelRenderer(this);
			tail11.setRotationPoint(0.0F, -4.0F, 0.0F);
			tail10.addChild(tail11);
			tail11.cubeList.add(new ModelBox(tail11, 48, 4, -4.0F, -4.0F, 0.0F, 8, 4, 0, 0.1F, false));
	
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			setRotationAngle(bipedRightArm, -0.2618F, 0.0F, 0.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 0, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.05F, false));
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
	
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			setRotationAngle(bipedLeftArm, 0.2618F, 0.0F, 0.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 0, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.05F, true));
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, true));
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
