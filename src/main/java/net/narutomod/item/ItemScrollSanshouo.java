
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityPuppetSanshouo;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.SoundEvents;

@ElementsNarutomodMod.ModElement.Tag
public class ItemScrollSanshouo extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:scroll_sanshouo")
	public static final Item block = null;
	public static final int ENTITYID = 387;

	public ItemScrollSanshouo(ElementsNarutomodMod instance) {
		super(instance, 766);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletscroll_sanshouo"), ENTITYID).name("entitybulletscroll_sanshouo")
				.tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:scroll_sanshouo", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> {
			return new RenderCustom(renderManager);
		});
	}

	public static class RangedItem extends Item implements ItemOnBody.Interface {
		public RangedItem() {
			super();
			this.setMaxDamage((int)EntityPuppetSanshouo.EntityCustom.MAXHEALTH);
			this.setFull3D();
			this.setUnlocalizedName("scroll_sanshouo");
			this.setRegistryName("scroll_sanshouo");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public EnumActionResult onItemUse(EntityPlayer entity, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
			if (!world.isRemote && world.getBlockState(pos).isTopSolid() && facing == EnumFacing.UP) {
				ItemStack stack = entity.getHeldItem(hand);
				if (!stack.hasTagCompound() || stack.getTagCompound().getBoolean("sealed")) {
					world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_CLOTH_PLACE,
							SoundCategory.NEUTRAL, 1, 1f / (itemRand.nextFloat() * 0.5f + 1f) + 0.5f);
					EntityArrowCustom entityarrow = new EntityArrowCustom(entity, this.getMaxDamage() - this.getDamage(stack));
					entityarrow.setLocationAndAngles(0.5d + pos.getX(), 1.1d + pos.getY(), 0.5d + pos.getZ(), entity.rotationYaw, 0f);
					world.spawnEntity(entityarrow);
					if (!stack.hasTagCompound()) {
						stack.setTagCompound(new NBTTagCompound());
					}
					stack.getTagCompound().setBoolean("sealed", false);
				}
			}
			return EnumActionResult.PASS;
		}

		@Override
		public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
			if (target instanceof EntityPuppetSanshouo.EntityCustom && !playerIn.world.isRemote) {
				ItemStack stack1 = playerIn.getHeldItem(hand);
				if (stack1.hasTagCompound() && !stack1.getTagCompound().getBoolean("sealed")) {
					ProcedureUtils.poofWithSmoke(target);
					this.setDamage(stack1, (int)(target.getMaxHealth() - target.getHealth()));
					target.setDead();
					stack1.getTagCompound().setBoolean("sealed", true);
					return true;
				}
			}
			return false;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			entity.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, entity.getHeldItem(hand));
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.NONE;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}
	}

	public static class EntityArrowCustom extends Entity {
		private final int openScrollTime = 30;
		private EntityLivingBase summoner;
		private float puppetHealth;
		
		public EntityArrowCustom(World a) {
			super(a);
			this.setSize(1.0f, 0.2f);
		}

		public EntityArrowCustom(EntityLivingBase summonerIn, float health) {
			this(summonerIn.world);
			this.summoner = summonerIn;
			this.puppetHealth = health;
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote && this.summoner == null) {
				this.setDead();
			} else if (this.ticksExisted > this.openScrollTime) {
				if (this.summoner != null) {
					EntityLivingBase entity = new EntityPuppetSanshouo.EntityCustom(this.summoner, this.posX, this.posY, this.posZ);
					this.world.spawnEntity(entity);
					entity.setHealth(this.puppetHealth);
					ProcedureUtils.poofWithSmoke(entity);
				}
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}
	
	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EntityArrowCustom> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/scroll_sanshouo.png");
		private final ModelScrollSanshouo model = new ModelScrollSanshouo();

		public RenderCustom(RenderManager renderManager) {
			super(renderManager);
			shadowSize = 0.1f;
		}

		@Override
		public void doRender(EntityArrowCustom bullet, double d, double d1, double d2, float f, float f1) {
			this.bindEntityTexture(bullet);
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) d, (float) d1, (float) d2);
			GlStateManager.scale(2.0f, 2.0f, 2.0f);
			GlStateManager.rotate(-f, 0, 1, 0);
			GlStateManager.rotate(180f - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * f1, 1, 0, 0);
			this.model.render(bullet, 0, 0, f1 + bullet.ticksExisted, 0, 0, 0.0625f);
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityArrowCustom entity) {
			return texture;
		}
	}

	// Made with Blockbench 4.4.2
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	@SideOnly(Side.CLIENT)
	public class ModelScrollSanshouo extends ModelBase {
		private final ModelRenderer hinge;
		private final ModelRenderer[] bone = new ModelRenderer[14];
		public ModelScrollSanshouo() {
			textureWidth = 16;
			textureHeight = 16;
			hinge = new ModelRenderer(this);
			hinge.setRotationPoint(0.0F, -0.85F, 0.0F);
			hinge.cubeList.add(new ModelBox(hinge, 0, 0, -4.0F, -0.5F, -0.5F, 4, 1, 1, 0.1F, false));
			hinge.cubeList.add(new ModelBox(hinge, 0, 0, 0.0F, -0.5F, -0.5F, 4, 1, 1, 0.1F, true));
			bone[0] = new ModelRenderer(this);
			bone[0].setRotationPoint(0.0F, 0.0F, 0.5F);
			setRotationAngle(bone[0], -1.5708F, 0.0F, 0.0F);
			bone[0].cubeList.add(new ModelBox(bone[0], 0, 2, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[1] = new ModelRenderer(this);
			bone[1].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[0].addChild(bone[1]);
			setRotationAngle(bone[1], -1.0472F, 0.0F, 0.0F);
			bone[1].cubeList.add(new ModelBox(bone[1], 0, 3, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[2] = new ModelRenderer(this);
			bone[2].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[1].addChild(bone[2]);
			setRotationAngle(bone[2], -1.0472F, 0.0F, 0.0F);
			bone[2].cubeList.add(new ModelBox(bone[2], 0, 4, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[3] = new ModelRenderer(this);
			bone[3].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[2].addChild(bone[3]);
			setRotationAngle(bone[3], -1.0472F, 0.0F, 0.0F);
			bone[3].cubeList.add(new ModelBox(bone[3], 0, 5, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[4] = new ModelRenderer(this);
			bone[4].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[3].addChild(bone[4]);
			setRotationAngle(bone[4], -1.0472F, 0.0F, 0.0F);
			bone[4].cubeList.add(new ModelBox(bone[4], 0, 6, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[5] = new ModelRenderer(this);
			bone[5].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[4].addChild(bone[5]);
			setRotationAngle(bone[5], -1.0472F, 0.0F, 0.0F);
			bone[5].cubeList.add(new ModelBox(bone[5], 0, 7, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[6] = new ModelRenderer(this);
			bone[6].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[5].addChild(bone[6]);
			setRotationAngle(bone[6], -1.0472F, 0.0F, 0.0F);
			bone[6].cubeList.add(new ModelBox(bone[6], 0, 8, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[7] = new ModelRenderer(this);
			bone[7].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[6].addChild(bone[7]);
			setRotationAngle(bone[7], -1.0472F, 0.0F, 0.0F);
			bone[7].cubeList.add(new ModelBox(bone[7], 0, 9, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[8] = new ModelRenderer(this);
			bone[8].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[7].addChild(bone[8]);
			setRotationAngle(bone[8], -1.0472F, 0.0F, 0.0F);
			bone[8].cubeList.add(new ModelBox(bone[8], 0, 10, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[9] = new ModelRenderer(this);
			bone[9].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[8].addChild(bone[9]);
			setRotationAngle(bone[9], -1.0472F, 0.0F, 0.0F);
			bone[9].cubeList.add(new ModelBox(bone[9], 0, 11, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[10] = new ModelRenderer(this);
			bone[10].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[9].addChild(bone[10]);
			setRotationAngle(bone[10], -1.0472F, 0.0F, 0.0F);
			bone[10].cubeList.add(new ModelBox(bone[10], 0, 12, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[11] = new ModelRenderer(this);
			bone[11].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[10].addChild(bone[11]);
			setRotationAngle(bone[11], -1.0472F, 0.0F, 0.0F);
			bone[11].cubeList.add(new ModelBox(bone[11], 0, 13, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[12] = new ModelRenderer(this);
			bone[12].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[11].addChild(bone[12]);
			setRotationAngle(bone[12], -1.0472F, 0.0F, 0.0F);
			bone[12].cubeList.add(new ModelBox(bone[12], 0, 14, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
			bone[13] = new ModelRenderer(this);
			bone[13].setRotationPoint(0.0F, 1.0F, 0.0F);
			bone[12].addChild(bone[13]);
			setRotationAngle(bone[13], -1.0472F, 0.0F, 0.0F);
			bone[13].cubeList.add(new ModelBox(bone[13], 0, 15, -4.0F, 0.0F, 0.0F, 8, 1, 0, 0.0F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
			hinge.render(f5);
			bone[0].render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		@Override
		public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
			super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
			for (int i = 1; i < bone.length; i++) {
				bone[i].rotateAngleX = MathHelper.clamp(1.0F - f2 + i, 0.0F, 1.0F) * -1.0472F;
			}
		}
	}
}
