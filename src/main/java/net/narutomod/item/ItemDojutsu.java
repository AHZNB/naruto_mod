
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import java.util.UUID;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemDojutsu extends ElementsNarutomodMod.ModElement {
	public ItemDojutsu(ElementsNarutomodMod instance) {
		super(instance, 447);
	}

	public abstract static class Base extends ItemArmor {
		@SideOnly(Side.CLIENT)
		private ModelBiped armorModel;

		public Base(ItemArmor.ArmorMaterial material) {
			super(material, 0, EntityEquipmentSlot.HEAD);
		}

		@Override
		public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
			super.onArmorTick(world, entity, itemstack);
			if (!this.isOwner(itemstack, entity) && !entity.isCreative()) {
				UUID uuid = ProcedureUtils.getOwnerId(itemstack);
				if (uuid != null && !uuid.equals(entity.getEntityData().getUniqueId("lastWornForeignDojutsu"))) {
					entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 1200, 0, false, false));
					entity.getEntityData().setUniqueId("lastWornForeignDojutsu", uuid);
				}
			}
			entity.getEntityData().setLong(NarutomodModVariables.MostRecentWornDojutsuTime, world.getTotalWorldTime());
		}

		@SideOnly(Side.CLIENT)
		@Override
		public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
			if (this.armorModel == null) {
				this.armorModel = new ClientModel().new ModelHelmetSnug();
			}

			this.armorModel.isSneak = living.isSneaking();
			this.armorModel.isRiding = living.isRiding();
			this.armorModel.isChild = living.isChild();
			return this.armorModel;
		}

		public boolean isOwner(ItemStack stack, EntityLivingBase entity) {
			return ProcedureUtils.isOriginalOwner(entity, stack);
		}

		@Nullable
		public EntityLivingBase getOwner(ItemStack stack, World world) {
			UUID uuid = ProcedureUtils.getOwnerId(stack);
			Entity entity = uuid != null ? ProcedureUtils.getEntityFromUUID(world, uuid) : null;
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		public void copyOwner(ItemStack toStack, ItemStack fromStack) {
			UUID uuid = ProcedureUtils.getOwnerId(fromStack);
			if (uuid != null) {
				ProcedureUtils.setOriginalOwner(toStack, uuid);
				toStack.setStackDisplayName(fromStack.getDisplayName());
			}
		}
		
		public void setOwner(ItemStack stack, EntityLivingBase entityIn) {
			ProcedureUtils.setOriginalOwner(entityIn, stack);
			stack.setStackDisplayName(entityIn.getName() + "'s " + stack.getDisplayName());
		}

		@Override
		public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
			if (ProcedureUtils.getOwnerId(stack) == null && entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative()) {
				this.setOwner(stack, (EntityLivingBase)entity);
				stack.setStackDisplayName(stack.getDisplayName() + " (creative)");
			}
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("ench", 9)) {
				stack.getTagCompound().removeTag("ench");
			}
			super.onUpdate(stack, world, entity, par4, par5);
		}
	}

	public static boolean hasAnyDojutsu(EntityPlayer player) {
		return ProcedureUtils.hasAnyItemOfSubtype(player, Base.class);
	}

	public static boolean wearingAnyDojutsu(EntityLivingBase entity) {
		return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof Base;
	}

	public static long getMostRecentWornTime(EntityLivingBase entity) {
		return entity.getEntityData().getLong(NarutomodModVariables.MostRecentWornDojutsuTime);
	}

	public static class ClientModel {
		@SideOnly(Side.CLIENT)
		public class ModelHelmetSnug extends ModelBiped {
			protected final ModelRenderer hornRight;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			protected final ModelRenderer hornLeft;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			protected final ModelRenderer hornMiddle;
			private final ModelRenderer bone8;
			private final ModelRenderer bone9;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer highlight;
			private final ModelRenderer forehead;
			protected boolean headHide;
			protected boolean headwearHide;
			protected boolean headwearShine;
			protected boolean highlightHide;
			protected boolean foreheadHide;
	
			public ModelHelmetSnug() {
				this.textureWidth = 64;
				this.textureHeight = 16;

				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.05F, false));

				hornRight = new ModelRenderer(this);
				hornRight.setRotationPoint(-2.5F, -6.0F, -4.0F);
				bipedHead.addChild(hornRight);
				setRotationAngle(hornRight, 0.5236F, 0.3491F, -0.1309F);
				hornRight.cubeList.add(new ModelBox(hornRight, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -1.0F, 0.0F);
				hornRight.addChild(bone2);
				setRotationAngle(bone2, -0.1745F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone2.addChild(bone3);
				setRotationAngle(bone3, -0.1745F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -0.9F, 0.0F);
				bone3.addChild(bone4);
				setRotationAngle(bone4, -0.1745F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
		
				hornLeft = new ModelRenderer(this);
				hornLeft.setRotationPoint(2.5F, -6.0F, -4.0F);
				bipedHead.addChild(hornLeft);
				setRotationAngle(hornLeft, 0.5236F, -0.3491F, 0.1309F);
				hornLeft.cubeList.add(new ModelBox(hornLeft, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
		
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, -1.0F, 0.0F);
				hornLeft.addChild(bone5);
				setRotationAngle(bone5, -0.1745F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, true));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone5.addChild(bone6);
				setRotationAngle(bone6, -0.1745F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, true));
		
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, -0.9F, 0.0F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, -0.1745F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, true));

				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));

				hornMiddle = new ModelRenderer(this);
				hornMiddle.setRotationPoint(0.0F, -6.25F, -4.0F);
				bipedHeadwear.addChild(hornMiddle);
				setRotationAngle(hornMiddle, 0.5236F, 0.0F, 0.0F);
				hornMiddle.cubeList.add(new ModelBox(hornMiddle, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, -1.0F, 0.0F);
				hornMiddle.addChild(bone8);
				setRotationAngle(bone8, -0.0873F, 0.0F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.2F, false));
		
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, -1.0F, 0.0F);
				bone8.addChild(bone9);
				setRotationAngle(bone9, -0.0873F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.1F, false));
		
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.0F, -0.9F, 0.0F);
				bone9.addChild(bone10);
				setRotationAngle(bone10, -0.0873F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, 0.0F, false));
		
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, -0.8F, 0.0F);
				bone10.addChild(bone11);
				setRotationAngle(bone11, -0.0873F, 0.0F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 0, 4, -0.5F, -1.0F, -0.5F, 1, 1, 1, -0.15F, false));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, -0.6F, 0.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, -0.0873F, 0.0F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 0, 4, -0.5F, -0.9F, -0.5F, 1, 1, 1, -0.3F, false));

				this.highlight = new ModelRenderer(this);
				this.highlight.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.highlight.cubeList.add(new ModelBox(this.highlight, 24, 0, -4.0F, -8.0F, -4.2F, 8, 8, 0, 0.0F, false));
				//this.bipedHead.addChild(this.highlight);
				this.forehead = new ModelRenderer(this);
				this.forehead.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.forehead.cubeList.add(new ModelBox(this.forehead, 0, 0, -1.94F, -6.62F, -4.25F, 4, 4, 0, 0.0F, false));
			}
	
			@Override
			public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.alphaFunc(0x204, 0.01f);
				if (entityIn.isSneaking()) {
					GlStateManager.translate(0.0F, 0.2F, 0.0F);
				}
				if (!this.headHide) {
					this.bipedHead.render(scale);
				}
				if (!this.headwearHide) {
					if (this.headwearShine) {
						GlStateManager.disableLighting();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
					}
					this.bipedHeadwear.render(scale);
					if (this.headwearShine) {
						int i = entityIn.getBrightnessForRender();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(i % 65536), (float)(i / 65536));
						GlStateManager.enableLighting();
					}
				}
				if (this.bipedHead.showModel) {
					if (!this.highlightHide) {
						GlStateManager.disableLighting();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
						this.copyModelAngles(this.bipedHead, this.highlight);
						this.highlight.render(scale);
						int i = entityIn.getBrightnessForRender();
						OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(i % 65536), (float)(i / 65536));
						GlStateManager.enableLighting();
					}
					if (!this.foreheadHide) {
						this.copyModelAngles(this.bipedHead, this.forehead);
						this.forehead.render(scale);
					}
				}
				GlStateManager.alphaFunc(0x204, 0.1f);
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
