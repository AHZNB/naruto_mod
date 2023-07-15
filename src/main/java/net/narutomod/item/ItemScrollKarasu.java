
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityPuppetKarasu;
import net.narutomod.entity.EntityPuppet;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

@ElementsNarutomodMod.ModElement.Tag
public class ItemScrollKarasu extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:scroll_karasu")
	public static final Item block = null;
	public static final int ENTITYID = 388;

	public ItemScrollKarasu(ElementsNarutomodMod instance) {
		super(instance, 767);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletscroll_karasu"), ENTITYID).name("entitybulletscroll_karasu").tracker(64, 1, true)
				.build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:scroll_karasu", "inventory"));
	}

	public static class RangedItem extends Item implements ItemOnBody.Interface {
		public RangedItem() {
			super();
			this.setMaxDamage((int)EntityPuppetKarasu.EntityCustom.MAXHEALTH);
			this.setFull3D();
			this.setUnlocalizedName("scroll_karasu");
			this.setRegistryName("scroll_karasu");
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
			if (target instanceof EntityPuppetKarasu.EntityCustom && !playerIn.world.isRemote) {
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
			return EnumAction.BOW;
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
					EntityPuppetKarasu.EntityCustom entity = new EntityPuppetKarasu.EntityCustom(this.summoner);
					entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.summoner.rotationYaw, 0f);
					entity.setHealth(this.puppetHealth);
					this.world.spawnEntity(entity);
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

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityArrowCustom.class, renderManager -> {
				return new RenderCustom(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityPuppet.ClientClass.RenderScroll<EntityArrowCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/scroll_karasu.png");
	
			public RenderCustom(RenderManager renderManager) {
				super(renderManager);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityArrowCustom entity) {
				return this.texture;
			}
		}
	}
}
