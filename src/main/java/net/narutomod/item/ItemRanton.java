
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.RayTraceResult;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityLightningArc;
import net.narutomod.entity.EntityLaserCircus;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemRanton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:ranton")
	public static final Item block = null;
	public static final int ENTITYID = 278;
	public static final ItemJutsu.JutsuEnum CLOUD = new ItemJutsu.JutsuEnum(0, "rantoncloud", 'S', 1d, new EntityRaiunkuha.Jutsu());
	public static final ItemJutsu.JutsuEnum LASERCIRCUS = new ItemJutsu.JutsuEnum(1, "laser_circus", 'S', 100d, new EntityLaserCircus.EC.Jutsu());

	public ItemRanton(ElementsNarutomodMod instance) {
		super(instance, 597);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(CLOUD, LASERCIRCUS));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityRaiunkuha.class)
				.id(new ResourceLocation("narutomod", "rantoncloud"), ENTITYID).name("rantoncloud").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:ranton", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.RANTON, list);
			this.setUnlocalizedName("ranton");
			this.setRegistryName("ranton");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[CLOUD.index] = 0;
			this.defaultCooldownMap[LASERCIRCUS.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			if (this.getCurrentJutsu(stack) == LASERCIRCUS) {
				return this.getPower(stack, entity, timeLeft, 0.1f, 50f);
			}
			return 1.0f;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
		 	ItemStack stack = entity.getHeldItem(hand);
			if (entity.isCreative() || (ProcedureUtils.hasItemInInventory(entity, ItemRaiton.block) 
			 && ProcedureUtils.hasItemInInventory(entity, ItemSuiton.block))) {
			 	ActionResult<ItemStack> result = super.onItemRightClick(world, entity, hand);
				if (!world.isRemote && result.getType() == EnumActionResult.SUCCESS 
				 && this.getCurrentJutsu(stack) == LASERCIRCUS && !EntityLaserCircus.ringSpawned(world, stack)) {
					world.spawnEntity(new EntityLaserCircus.EntityRing(entity, stack));
				}
				return result;
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(TextFormatting.GREEN + net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.ranton.musthave") + TextFormatting.RESET);
		}
	}

	public static class EntityRaiunkuha extends Entity {
		private final double chakrUsage = CLOUD.chakraUsage;
		private EntityLivingBase summoner;
		private float damageMultiplier;

		public EntityRaiunkuha(World a) {
			super(a);
			this.setSize(0.01f, 0.01f);
		}

		protected EntityRaiunkuha(EntityLivingBase summonerIn, ItemStack stack) {
			this(summonerIn.world);
			this.setSize(0.01f, 0.01f);
			this.summoner = summonerIn;
			this.damageMultiplier = Math.max(((ItemJutsu.Base)stack.getItem()).getXpRatio(stack, CLOUD), 1f);
			this.setPosition(summonerIn.posX, summonerIn.posY, summonerIn.posZ);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			//super.onUpdate();
			if (this.summoner != null && this.summoner.isEntityAlive() && Chakra.pathway(this.summoner).consume(this.chakrUsage)) {
				this.setPosition(this.summoner.posX, this.summoner.posY, this.summoner.posZ);
				if (this.rand.nextInt(20) == 0) {
					this.playSound(SoundEvent.REGISTRY
					 .getObject(new ResourceLocation("narutomod:electricity")), 0.1f, this.rand.nextFloat() * 0.6f + 0.3f);
				}
				EntityLightningArc.spawnAsParticle(this.world, this.posX + (this.rand.nextDouble()-0.5d) * 2.0d,
				  this.posY + this.rand.nextDouble() * 1.6d, this.posZ + (this.rand.nextDouble()-0.5d) * 2.0d, 1.2d, 0d, 0d, 0d);
				Particles.spawnParticle(world, Particles.Types.SMOKE, this.posX, this.posY + 0.9d, this.posZ,
				  100, 0.4d, 0.6d, 0.4d, 0d, 0d, 0d, 0xff303030, 30, 0, 0, this.summoner.getEntityId(), 0);
				for (EntityLivingBase entity1 : 
				 this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.summoner.getEntityBoundingBox().grow(4d))) {
					if (!entity1.equals(this.summoner) && entity1.isEntityAlive()) {
						this.setLightningOn(entity1);
					}
				}
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(this.summoner, 10d);
				if (res != null && res.entityHit instanceof EntityLivingBase) {
					this.setLightningOn((EntityLivingBase)res.entityHit);
				}
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		private void setLightningOn(EntityLivingBase entity) {
			EntityLightningArc.Base entity2 = new EntityLightningArc.Base(this.world,
			 this.getPositionVector().addVector(0d, 1d, 0d), entity.getPositionVector().addVector(0d, entity.height/2, 0d),
			 0xc00000ff, 1, 0f);
			entity2.setDamage(ItemJutsu.causeJutsuDamage(this, this.summoner), this.getDamage(), this.summoner, 0);
			this.world.spawnEntity(entity2);
		}

		private float getDamage() {
			return this.rand.nextFloat() * this.damageMultiplier * 10f;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String RaiunkuhaID = "RaiunkuhaEntityId";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(stack.getTagCompound().getInteger(RaiunkuhaID));
				if (entity1 instanceof EntityRaiunkuha) {
					entity1.setDead();
					stack.getTagCompound().removeTag(RaiunkuhaID);
					return false;
				} else {
					entity1 = new EntityRaiunkuha(entity, stack);
					entity.world.spawnEntity(entity1);
					stack.getTagCompound().setInteger(RaiunkuhaID, entity1.getEntityId());
					return true;
				}
			}
		}
	}
}
