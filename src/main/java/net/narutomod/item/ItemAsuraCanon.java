package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.init.Items;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;

import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAsuraCanon extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:asuracanon")
	public static final Item block = null;
	public static final int ENTITYID = 31;
	
	public ItemAsuraCanon(ElementsNarutomodMod instance) {
		super(instance, 214);
	}

	public void initElements() {
		this.elements.items.add(() -> new RangedItem());
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCanonball.class)
				.id(new ResourceLocation("narutomod", "entitybulletasuracanon"), ENTITYID).name("entitybulletasuracanon").tracker(64, 1, true)
				.build());
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:asuracanon", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCanonball.class, renderManager -> new RenderSnowball(renderManager,
				(new ItemStack(Items.FIREWORK_CHARGE, 1)).getItem(), Minecraft.getMinecraft().getRenderItem()));
	}
	
	public static class RangedItem extends Item {
		public RangedItem() {
			this.setMaxDamage(100);
			this.setFull3D();
			this.setUnlocalizedName("asuracanon");
			this.setRegistryName("asuracanon");
			this.maxStackSize = 1;
			this.setCreativeTab(null);
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				float power = 1.0F;
				EntityCanonball entityarrow = new EntityCanonball(world, entity);
				entityarrow.explosivePower = (getMaxItemUseDuration(itemstack) - timeLeft) / 15.0F + 1.0F;
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, power * 2.0F, 0.0F);
				itemstack.damageItem(1, entity);
				world.playSound(null, entity.posX, entity.posY, entity.posZ,
						(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.blaze.shoot")),
						SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.5F + 1.0F) + power / 2.0F);
				world.spawnEntity(entityarrow);
			}
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

	public static class EntityCanonball extends EntityThrowable {
		public static float explosivePower = 1.0F;

		public EntityCanonball(World worldIn) {
			super(worldIn);
		}

		public EntityCanonball(World worldIn, EntityLivingBase throwerIn) {
			this(worldIn, throwerIn.posX, throwerIn.posY + throwerIn.getEyeHeight() - 0.4D, throwerIn.posZ);
			this.thrower = throwerIn;
		}

		public EntityCanonball(World worldIn, double x, double y, double z) {
			super(worldIn, x, y, z);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (!this.world.isRemote) {
				this.world.newExplosion(this, this.posX, this.posY, this.posZ, this.explosivePower, false,
						ForgeEventFactory.getMobGriefingEvent(this.world, this.thrower));
				this.setDead();
			}
		}
	}
}
