
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
//import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
//import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;

import net.narutomod.entity.EntityUnrivaledStrength;
import net.narutomod.block.BlockAmaterasuBlock;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.potion.PotionCorrosion;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.event.EventSetBlocks;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class ItemFutton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:futton")
	public static final Item block = null;
	public static final int ENTITYID = 281;
	public static final ItemJutsu.JutsuEnum MIST = new ItemJutsu.JutsuEnum(0, "futton_mist", 'S', 50d, new EntityBoilingMist.Jutsu());
	public static final ItemJutsu.JutsuEnum STRENGTH = new ItemJutsu.JutsuEnum(1, "unrivaled_strength", 'S', 100d, new EntityUnrivaledStrength.EC.Jutsu());

	public ItemFutton(ElementsNarutomodMod instance) {
		super(instance, 600);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(MIST, STRENGTH));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityBoilingMist.class)
				.id(new ResourceLocation("narutomod", "futton_mist"), ENTITYID).name("futton_mist").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:futton", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.FUTTON, list);
			this.setUnlocalizedName("futton");
			this.setRegistryName("futton");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[MIST.index] = 0;
			this.defaultCooldownMap[STRENGTH.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == MIST) {
				return this.getPower(stack, entity, timeLeft, 0.1f, 30f);
			}
			return this.getPower(stack, entity, timeLeft, 1.0f, 100f);
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			float f = super.getMaxPower(stack, entity);
			if (jutsu == MIST) {
				return Math.min(f, 30.0f);
			} else {
				return Math.min(f, 20.0f);
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
		 	ItemStack stack = entity.getHeldItem(hand);
			if (entity.isCreative() || (ProcedureUtils.hasItemInInventory(entity, ItemKaton.block) 
			 && ProcedureUtils.hasItemInInventory(entity, ItemSuiton.block))) {
			 	return super.onItemRightClick(world, entity, hand);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(TextFormatting.GREEN + net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.futton.musthave") + TextFormatting.RESET);
		}
	}

	public static class EntityBoilingMist extends Entity {
		private final AirPunch airPunch = new AirPunch();
		private EntityLivingBase user;
		private float power;

		public EntityBoilingMist(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		public EntityBoilingMist(EntityLivingBase userIn, float powerIn) {
			this(userIn.world);
			this.setSize(0.01f, 0.01f);
			this.user = userIn;
			this.power = powerIn;
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void onUpdate() {
			if (this.user != null) {
				this.setPosition(this.user.posX, this.user.posY, this.user.posZ);
				this.airPunch.execute(this.user, this.power, this.power * 0.25d);
			}
			if (!this.world.isRemote && this.ticksExisted > (int)(this.power * this.power * 0.5f)) {
				this.setDead();
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public class AirPunch extends ProcedureAirPunch {
			public AirPunch() {
				this.blockHardnessLimit = 1.0f;
				this.particlesDuring = null;
			}

			@Override
			protected void preExecuteParticles(EntityLivingBase player) {
				Vec3d vec0 = player.getLookVec();
				Vec3d vec = vec0.scale(2d).addVector(player.posX, player.posY + 1.5d, player.posZ);
				//Particles.Renderer particles = new Particles.Renderer(player.world);
				for (int i = 1; i <= 50; i++) {
					Vec3d vec1 = vec0.scale(((EntityBoilingMist.this.rand.nextDouble() * 0.8d) + 0.2d) * this.getRange(0) * 0.06d);
					Particles.spawnParticle(player.world, Particles.Types.SMOKE, vec.x, vec.y, vec.z, 1, 0d, 0d, 0d, 
					 vec1.x + (EntityBoilingMist.this.rand.nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 vec1.y + (EntityBoilingMist.this.rand.nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 vec1.z + (EntityBoilingMist.this.rand.nextDouble()-0.5d) * this.getFarRadius(0) * 0.15d,
					 0x20FFFFFF, 80 + EntityBoilingMist.this.rand.nextInt(20), 0, 0, -1, 0);
				}
				//particles.send();
			}

			@Override
			protected void attackEntityFrom(EntityLivingBase player, Entity target) {
				if (target instanceof EntityLivingBase) {
					target.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1f, this.rand.nextFloat() + 0.5f);
					((EntityLivingBase)target).addPotionEffect(new PotionEffect(PotionCorrosion.potion, 200, 15));
				}
			}

			@Override
			protected EntityItem processAffectedBlock(EntityLivingBase player, BlockPos pos, EnumFacing facing) {
				if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(player.world, player)) {
					Material mat = player.world.getBlockState(pos).getMaterial();
					if (mat == Material.ICE || mat == Material.PACKED_ICE) {
						player.world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, this.rand.nextFloat() + 0.5f);
						Map<BlockPos, IBlockState> map = Maps.newHashMap();
						map.put(pos, Blocks.WATER.getDefaultState());
						new EventSetBlocks(player.world, map, 0, 100, true, false);
					} else if (mat == Material.FIRE && player.world.getBlockState(pos).getBlock() != BlockAmaterasuBlock.block) {
						player.world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, this.rand.nextFloat() + 0.5f);
						player.world.setBlockToAir(pos);
					}
				}
				return null;
			}

			@Override
			protected float getBreakChance(BlockPos pos, EntityLivingBase player, double range) {
				return 0.0f;
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				entity.world.spawnEntity(new EntityBoilingMist(entity, power));
				return true;
			}
		}
	}
}
