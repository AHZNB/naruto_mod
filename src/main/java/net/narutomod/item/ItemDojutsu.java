
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
			ModelBiped armorModel = new ClientModel().new ModelHelmetSnug();
			armorModel.isSneak = living.isSneaking();
			armorModel.isRiding = living.isRiding();
			armorModel.isChild = living.isChild();
			return armorModel;
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
			private final ModelRenderer highlight;
			private final ModelRenderer forehead;
			protected boolean headwearHide;
			protected boolean headwearShine;
			protected boolean highlightHide;
			protected boolean foreheadHide;
	
			public ModelHelmetSnug() {
				this.textureWidth = 64;
				this.textureHeight = 16;
				this.bipedHead = new ModelRenderer(this);
				this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedHead.cubeList.add(new ModelBox(this.bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.05F, false));
				this.bipedHeadwear = new ModelRenderer(this);
				this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.2F, false));
				this.highlight = new ModelRenderer(this);
				this.highlight.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.highlight.cubeList.add(new ModelBox(this.highlight, 24, 0, -4.0F, -8.0F, -4.2F, 8, 8, 0, 0.0F, false));
				//this.bipedHead.addChild(this.highlight);
				this.forehead = new ModelRenderer(this);
				this.forehead.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.forehead.cubeList.add(new ModelBox(this.forehead, 0, 0, -1.94F, -6.62F, -4.25F, 4, 4, 0, 0.0F, false));
			}
	
			/*@Override
			public void render(Entity entityIn, float f0, float f1, float f2, float f3, float f4, float f5) {
				super.render(entityIn, f0, f1, f2, f3, f4, f5);
				if (this.bipedHead.showModel) {
					GlStateManager.pushMatrix();
					GlStateManager.disableLighting();
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
					if (entityIn.isSneaking()) {
						GlStateManager.translate(0.0F, 0.2F, 0.0F);
					}
					this.copyModelAngles(this.bipedHead, this.highlight);
					this.highlight.render(f5);
					GlStateManager.enableLighting();
					GlStateManager.popMatrix();
				}
			}*/
			@Override
			public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
				this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
				GlStateManager.pushMatrix();
				GlStateManager.alphaFunc(0x204, 0.01f);
				if (entityIn.isSneaking()) {
					GlStateManager.translate(0.0F, 0.2F, 0.0F);
				}
				this.bipedHead.render(scale);
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
				GlStateManager.popMatrix();
			}
		}
	}
}
