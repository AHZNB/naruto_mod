
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.entity.EntityBijuManager;
import net.narutomod.item.ItemJiton;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemGourd extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:gourdbody")
	public static final Item body = null;

	public ItemGourd(ElementsNarutomodMod instance) {
		super(instance, 517);
	}

	@Override
	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial("GOURD", "narutomod:sasuke_",
		 20, new int[]{2, 5, 20, 2}, 0, null, 5f);
		elements.items.add(() -> new ItemArmor(enuma, 0, EntityEquipmentSlot.CHEST) {
			@SideOnly(Side.CLIENT)
			private ModelBiped armorModel;

			@Override
			@SideOnly(Side.CLIENT)
			public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
				if (this.armorModel == null) {
					this.armorModel = new ModelGourd();
				}

				this.armorModel.isSneak = living.isSneaking();
				this.armorModel.isRiding = living.isRiding();
				this.armorModel.isChild = living.isChild();
				return this.armorModel;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				switch (getMaterial(stack)) {
					case IRON:
					default:
						return "narutomod:textures/gourd_iron.png";
					case SAND:
						return "narutomod:textures/gourd_sand.png";
				}
			}

			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				if (!world.isRemote && ProcedureUtils.hasItemInInventory(entity, ItemJiton.block)) {
					entity.extinguish();
					if (entity.ticksExisted % 20 == 3) {
						entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 22, 2, false, false));
						itemstack.setItemDamage(itemstack.getItemDamage() - 1);
					}
					//if (!itemstack.hasTagCompound())
					//	itemstack.setTagCompound(new NBTTagCompound());
					//itemstack.getTagCompound().setUniqueId("LAST_WEARER_ID", entity.getUniqueID());
				}
			}

			@Override
			public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
				super.onUpdate(itemstack, world, entity, par4, par5);
				if (!world.isRemote && entity instanceof EntityPlayer 
				 && !ProcedureUtils.hasItemInInventory((EntityPlayer)entity, ItemJiton.block)) {
					itemstack.shrink(1);
				}
			}

			/*@Override
			public void setDamage(ItemStack stack, int damage) {
				super.setDamage(stack, damage);
				if (this.getDamage(stack) > this.getMaxDamage(stack) && stack.hasTagCompound() 
				 && stack.getTagCompound().hasUniqueId("LAST_WEARER_ID")) {
					Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(stack.getTagCompound().getUniqueId("LAST_WEARER_ID"));
					if (entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer)entity;
						if (ProcedureUtils.hasItemInInventory(player, ItemJiton.block)) {
							player.getCooldownTracker().setCooldown(ItemJiton.block, (int)ProcedureUtils.modifiedCooldown(1200, player));
						}
					}
				}
			}*/
		}.setUnlocalizedName("gourdbody").setRegistryName("gourdbody").setCreativeTab(TabModTab.tab));
	}

	public static void setMaterial(ItemStack stack, ItemJiton.Type type) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("MaterialType", type.getID());
	}

	protected static ItemJiton.Type getMaterial(ItemStack stack) {
		return stack.hasTagCompound() ? ItemJiton.Type.getTypeFromId(stack.getTagCompound().getInteger("MaterialType")) : ItemJiton.Type.IRON;
	}
	
	public static Vec3d getMouthPos(EntityLivingBase lb) {
		if (lb instanceof EntityPlayer && EntityBijuManager.getTails((EntityPlayer)lb) == 1 && EntityBijuManager.cloakLevel((EntityPlayer)lb) > 0) {
			return lb.getPositionVector();
		}
		Vec3d vec = new Vec3d(0.4d, 1.75d, -0.4d);
		double x = lb.posX - (Math.sin((lb.renderYawOffset + 90) * 0.0174533) * vec.x) - (Math.sin(lb.renderYawOffset * 0.0174533) * vec.z);
		double z = lb.posZ + (Math.cos((lb.renderYawOffset + 90) * 0.0174533) * vec.x) + (Math.cos(lb.renderYawOffset * 0.0174533) * vec.z);
		double y = lb.posY + vec.y;
		return new Vec3d(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(body, 0, new ModelResourceLocation("narutomod:gourdbody", "inventory"));
	}
	// Made with Blockbench 3.8.3
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelGourd extends ModelBiped {
		//private final ModelRenderer bipedBody;
		private final ModelRenderer bone2;
		private final ModelRenderer bone4;
		private final ModelRenderer bone3;
		private final ModelRenderer bone9;
		private final ModelRenderer bone6;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r2;
		private final ModelRenderer cube_r3;
		private final ModelRenderer bone8;
		private final ModelRenderer cube_r4;
		private final ModelRenderer cube_r5;
		private final ModelRenderer cube_r6;
		private final ModelRenderer bone7;
		private final ModelRenderer cube_r7;
		private final ModelRenderer cube_r8;
		private final ModelRenderer cube_r9;
		private final ModelRenderer bone11;
		private final ModelRenderer cube_r10;
		private final ModelRenderer cube_r11;
		private final ModelRenderer cube_r12;
		private final ModelRenderer cube_r13;
		private final ModelRenderer cube_r14;
		private final ModelRenderer cube_r15;
		private final ModelRenderer cube_r16;
		private final ModelRenderer cube_r17;
		private final ModelRenderer bone10;
		private final ModelRenderer cube_r18;
		private final ModelRenderer cube_r19;
		private final ModelRenderer cube_r20;
		//private final ModelRenderer bipedRightArm;
		//private final ModelRenderer bipedLeftArm;
		public ModelGourd() {
			textureWidth = 64;
			textureHeight = 64;

			bipedBody = new ModelRenderer(this);
			bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
			
	
			bone2 = new ModelRenderer(this);
			bone2.setRotationPoint(0.0F, 4.0F, 5.0F);
			bipedBody.addChild(bone2);
			bone2.cubeList.add(new ModelBox(bone2, 42, 16, -5.0F, -4.0F, -3.0F, 2, 1, 0, 0.3F, false));
			bone2.cubeList.add(new ModelBox(bone2, 42, 16, -5.0F, -4.0F, -7.0F, 2, 2, 0, 0.3F, false));
			bone2.cubeList.add(new ModelBox(bone2, 42, 16, 2.0F, 4.0F, -7.0F, 2, 2, 0, 0.3F, false));
			bone2.cubeList.add(new ModelBox(bone2, 42, 16, -5.0F, -4.0F, -7.0F, 2, 0, 4, 0.3F, false));
			bone2.cubeList.add(new ModelBox(bone2, 42, 16, 4.0F, 4.0F, -7.0F, 0, 2, 5, 0.3F, false));
	
			bone4 = new ModelRenderer(this);
			bone4.setRotationPoint(-3.0F, -3.5F, -3.0F);
			bone2.addChild(bone4);
			setRotationAngle(bone4, 0.0F, 0.0F, -0.5236F);
			bone4.cubeList.add(new ModelBox(bone4, 42, 16, -2.0F, -0.5F, 0.0F, 2, 5, 0, 0.3F, false));
	
			bone3 = new ModelRenderer(this);
			bone3.setRotationPoint(-3.0F, -4.0F, -7.0F);
			bone2.addChild(bone3);
			setRotationAngle(bone3, 0.0F, 0.0F, -0.6981F);
			bone3.cubeList.add(new ModelBox(bone3, 41, 16, -2.25F, 0.0F, 0.0F, 2, 11, 0, 0.3F, false));
	
			bone9 = new ModelRenderer(this);
			bone9.setRotationPoint(-2.0F, 2.0F, 8.75F);
			bipedBody.addChild(bone9);
			setRotationAngle(bone9, 0.0F, 0.0F, -0.5236F);
			bone9.cubeList.add(new ModelBox(bone9, 24, 0, -1.0F, -9.4F, -1.0F, 2, 1, 2, 0.0F, false));
	
			bone6 = new ModelRenderer(this);
			bone6.setRotationPoint(0.0F, -10.0F, 0.0F);
			bone9.addChild(bone6);
			bone6.cubeList.add(new ModelBox(bone6, 0, 0, -1.0F, 2.0F, -3.0F, 2, 1, 6, 0.42F, false));
	
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(0.0F, 2.5F, 0.0F);
			bone6.addChild(cube_r1);
			setRotationAngle(cube_r1, 0.0F, 2.3562F, 0.0F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));
	
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(0.0F, 2.5F, 0.0F);
			bone6.addChild(cube_r2);
			setRotationAngle(cube_r2, 0.0F, 1.5708F, 0.0F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));
	
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.0F, 2.5F, 0.0F);
			bone6.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.0F, 0.7854F, 0.0F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));
	
			bone8 = new ModelRenderer(this);
			bone8.setRotationPoint(0.0F, -12.5F, 0.0F);
			bone9.addChild(bone8);
			bone8.cubeList.add(new ModelBox(bone8, 6, 1, -2.0F, 6.0F, -5.0F, 4, 5, 10, 0.12F, false));
	
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(0.0F, 10.5F, 0.0F);
			bone8.addChild(cube_r4);
			setRotationAngle(cube_r4, 0.0F, -0.7854F, 0.0F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 6, 1, -2.0F, -4.5F, -5.0F, 4, 5, 10, 0.12F, false));
	
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(0.0F, 10.5F, 0.0F);
			bone8.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.0F, 1.5708F, 0.0F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 6, 1, -2.0F, -4.5F, -5.0F, 4, 5, 10, 0.12F, false));
	
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(0.0F, 10.5F, 0.0F);
			bone8.addChild(cube_r6);
			setRotationAngle(cube_r6, 0.0F, 0.7854F, 0.0F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 6, 1, -2.0F, -4.5F, -5.0F, 4, 5, 10, 0.12F, false));
	
			bone7 = new ModelRenderer(this);
			bone7.setRotationPoint(0.0F, -3.6F, 0.0F);
			bone9.addChild(bone7);
			bone7.cubeList.add(new ModelBox(bone7, 14, 16, -3.0F, 2.0F, -7.0F, 6, 9, 14, -0.17F, false));
	
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(0.0F, 10.5F, 0.0F);
			bone7.addChild(cube_r7);
			setRotationAngle(cube_r7, 0.0F, 0.7854F, 0.0F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 0, 38, -3.0F, -8.5F, -7.0F, 6, 9, 14, -0.17F, false));
	
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(0.0F, 10.5F, 0.0F);
			bone7.addChild(cube_r8);
			setRotationAngle(cube_r8, 0.0F, -1.5708F, 0.0F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 8, 16, -3.0F, -8.5F, -7.0F, 6, 9, 14, -0.17F, false));
	
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(0.0F, 10.5F, 0.0F);
			bone7.addChild(cube_r9);
			setRotationAngle(cube_r9, 0.0F, -0.7854F, 0.0F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 0, 38, -3.0F, -8.5F, -7.0F, 6, 9, 14, -0.17F, false));
	
			bone11 = new ModelRenderer(this);
			bone11.setRotationPoint(0.0F, -1.7F, 0.0F);
			bone9.addChild(bone11);
			
	
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(-4.7374F, 9.0F, -22.2374F);
			bone11.addChild(cube_r10);
			setRotationAngle(cube_r10, -0.8727F, 0.7854F, 0.0F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 0, 16, -14.75F, -11.8F, 8.0997F, 5, 2, 5, 0.3F, false));
	
			cube_r11 = new ModelRenderer(this);
			cube_r11.setRotationPoint(-4.7374F, 9.0F, 4.7374F);
			bone11.addChild(cube_r11);
			setRotationAngle(cube_r11, 0.8727F, -0.7854F, 0.0F);
			cube_r11.cubeList.add(new ModelBox(cube_r11, 0, 16, -2.5F, -2.3F, -5.1497F, 5, 2, 5, 0.3F, false));
	
			cube_r12 = new ModelRenderer(this);
			cube_r12.setRotationPoint(-2.75F, 9.0F, 0.0F);
			bone11.addChild(cube_r12);
			setRotationAngle(cube_r12, 0.0F, -1.5708F, 0.8727F);
			cube_r12.cubeList.add(new ModelBox(cube_r12, 0, 16, -2.5F, 0.75F, -2.6F, 5, 2, 5, 0.3F, true));
	
			cube_r13 = new ModelRenderer(this);
			cube_r13.setRotationPoint(6.75F, 9.0F, 0.0F);
			bone11.addChild(cube_r13);
			setRotationAngle(cube_r13, 0.0F, 1.5708F, -0.8727F);
			cube_r13.cubeList.add(new ModelBox(cube_r13, 0, 16, -2.5F, -2.3F, -5.2F, 5, 2, 5, 0.3F, false));
	
			cube_r14 = new ModelRenderer(this);
			cube_r14.setRotationPoint(4.7374F, 9.0F, -22.2374F);
			bone11.addChild(cube_r14);
			setRotationAngle(cube_r14, -0.8727F, -0.7854F, 0.0F);
			cube_r14.cubeList.add(new ModelBox(cube_r14, 0, 16, 9.75F, -11.75F, 8.1497F, 5, 2, 5, 0.3F, false));
	
			cube_r15 = new ModelRenderer(this);
			cube_r15.setRotationPoint(4.7374F, 9.0F, 4.7374F);
			bone11.addChild(cube_r15);
			setRotationAngle(cube_r15, 0.8727F, 0.7854F, 0.0F);
			cube_r15.cubeList.add(new ModelBox(cube_r15, 0, 16, -2.5F, -2.25F, -5.1497F, 5, 2, 5, 0.3F, false));
	
			cube_r16 = new ModelRenderer(this);
			cube_r16.setRotationPoint(0.0F, 9.0F, -24.25F);
			bone11.addChild(cube_r16);
			setRotationAngle(cube_r16, -0.8727F, 0.0F, 0.0F);
			cube_r16.cubeList.add(new ModelBox(cube_r16, 0, 16, -2.5F, -15.75F, 11.45F, 5, 2, 5, 0.3F, false));
	
			cube_r17 = new ModelRenderer(this);
			cube_r17.setRotationPoint(0.0F, 9.0F, 6.75F);
			bone11.addChild(cube_r17);
			setRotationAngle(cube_r17, 0.8727F, 0.0F, 0.0F);
			cube_r17.cubeList.add(new ModelBox(cube_r17, 0, 16, -2.5F, -2.3F, -5.2F, 5, 2, 5, 0.3F, false));
	
			bone10 = new ModelRenderer(this);
			bone10.setRotationPoint(0.0F, 8.15F, 0.0F);
			bone9.addChild(bone10);
			bone10.cubeList.add(new ModelBox(bone10, 0, 0, -1.0F, 2.0F, -3.0F, 2, 1, 6, 0.42F, false));
	
			cube_r18 = new ModelRenderer(this);
			cube_r18.setRotationPoint(0.0F, 2.5F, 0.0F);
			bone10.addChild(cube_r18);
			setRotationAngle(cube_r18, 0.0F, 2.3562F, 0.0F);
			cube_r18.cubeList.add(new ModelBox(cube_r18, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));
	
			cube_r19 = new ModelRenderer(this);
			cube_r19.setRotationPoint(0.0F, 2.5F, 0.0F);
			bone10.addChild(cube_r19);
			setRotationAngle(cube_r19, 0.0F, 1.5708F, 0.0F);
			cube_r19.cubeList.add(new ModelBox(cube_r19, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));
	
			cube_r20 = new ModelRenderer(this);
			cube_r20.setRotationPoint(0.0F, 2.5F, 0.0F);
			bone10.addChild(cube_r20);
			setRotationAngle(cube_r20, 0.0F, 0.7854F, 0.0F);
			cube_r20.cubeList.add(new ModelBox(cube_r20, 0, 0, -1.0F, -0.5F, -3.0F, 2, 1, 6, 0.42F, false));
	
			bipedRightArm = new ModelRenderer(this);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 48, 48, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
	
			bipedLeftArm = new ModelRenderer(this);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 48, 48, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
		}


		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

	}
}
