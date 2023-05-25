
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
//import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
//import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
//import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.entity.EntityTruthSeekerBall;
import net.narutomod.entity.EntityIntonRaiha;
import net.narutomod.entity.EntityRantonKoga;
import net.narutomod.entity.EntityRasenshuriken;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import com.google.common.base.Predicate;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSixPathSenjutsu extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:six_path_senjutsu")
	public static final Item block = null;
	public static final int ENTITYID = 347;
	public static final ItemJutsu.JutsuEnum SHOOT = new ItemJutsu.JutsuEnum(0, "tooltip.6psenjutsu.shoot", 'S', 50d, new ShootTruthSeekerBall());
	public static final ItemJutsu.JutsuEnum SHIELD = new ItemJutsu.JutsuEnum(1, "tooltip.6psenjutsu.shield", 'S', 50d, new TruthSeekerShield());
	public static final ItemJutsu.JutsuEnum THUNDER = new ItemJutsu.JutsuEnum(2, "inton_raiha", 'S', 100d, new EntityIntonRaiha.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum LASER = new ItemJutsu.JutsuEnum(3, "ranton_koga", 'S', 100d, new EntityRantonKoga.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum RASENSHURIKEN = new ItemJutsu.JutsuEnum(4, "tooltip.6psenjutsu.rasenshuriken", 'S', 1000d, new EntityRasenshuriken.EC.TSBVariant());

	public ItemSixPathSenjutsu(ElementsNarutomodMod instance) {
		super(instance, 703);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(SHOOT, SHIELD, THUNDER, LASER, RASENSHURIKEN));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:six_path_senjutsu", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		private static final String SPAWNEDBALLSID = "SpawnedTruthSeekingBallsId";
		private static final String CURRENTBALL = "CurrentTruthSeekingBallIdx";

		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.SIXPATHSENJUTSU, list);
			this.setUnlocalizedName("six_path_senjutsu");
			this.setRegistryName("six_path_senjutsu");
			this.setCreativeTab(TabModTab.tab);
			for (int i = 0; i < list.length; i++) {
				this.defaultCooldownMap[i] = 0;
			}
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			if (this.getCurrentJutsu(stack) == LASER) {
				return 10.0f;
			} else if (this.getCurrentJutsu(stack) == THUNDER) {
				return 6.0f;
			}
			return super.getMaxPower(stack, entity);
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			if (this.getCurrentJutsu(stack) == THUNDER) {
				return this.getPower(stack, entity, timeLeft, 1f, 80f);
			} else if (this.getCurrentJutsu(stack) == LASER) {
				return this.getPower(stack, entity, timeLeft, 1f, 50f);
			}
			return 1f;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!itemstack.hasTagCompound()) {
				itemstack.setTagCompound(new NBTTagCompound());
			}
			if (entity instanceof EntityLivingBase && !world.isRemote) {
				EntityLivingBase livingEntity = (EntityLivingBase)entity;
				if (!ItemRinnegan.wearingRinnesharingan(livingEntity) 
				 && entity instanceof EntityPlayer && !((EntityPlayer)entity).isCreative()) {
				 	entity.getEntityData().setTag("6pSenjutsuItem", itemstack.writeToNBT(new NBTTagCompound()));
					itemstack.shrink(1);
				} else if (livingEntity.getHeldItemMainhand().equals(itemstack) || livingEntity.getHeldItemOffhand().equals(itemstack)) {
					int[] intarray = itemstack.getTagCompound().getIntArray(SPAWNEDBALLSID);
					if (intarray.length < 9) {
						intarray = new int[9];
					}
					boolean flag = false;
					for (int i = 0; i < 9; i++) {
						if (intarray[i] >= 0) {
							Entity entity1 = world.getEntityByID(intarray[i]);
							if (!(entity1 instanceof EntityTruthSeekerBall.EntityCustom)) {
								entity1 = new EntityTruthSeekerBall.EntityCustom(livingEntity, i, itemstack);
								world.spawnEntity(entity1);
								intarray[i] = entity1.getEntityId();
								flag = true;
							} else if (((EntityTruthSeekerBall.EntityCustom)entity1).getHealth() <= 0.0f) {
								intarray[i] = -1;
								flag = true;
							}
						}
					}
					if (flag) {
						itemstack.getTagCompound().setIntArray(SPAWNEDBALLSID, intarray);
					}
					this.sentryDuty(itemstack, livingEntity);
				}
				if (entity.ticksExisted % 40 == 5 && entity instanceof EntityPlayer) {
					ItemStack stack1 = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemFuton.block);
					this.enableJutsu(itemstack, RASENSHURIKEN,
					 stack1 != null && ((ItemFuton.RangedItem)stack1.getItem()).canUseJutsu(stack1, ItemFuton.RASENSHURIKEN, livingEntity));
				}
			}
		}

		@Nullable
		public EntityTruthSeekerBall.EntityCustom getTSB(ItemStack stack, World world, int index) {
			int[] intarray = stack.getTagCompound().getIntArray(SPAWNEDBALLSID);
			if (index >= 0 && intarray[index] >= 0) {
				Entity entity = world.getEntityByID(intarray[index]);
				if (entity instanceof EntityTruthSeekerBall.EntityCustom) {
					EntityTruthSeekerBall.EntityCustom entity1 = (EntityTruthSeekerBall.EntityCustom)entity;
					if (entity1.getHealth() > 0.0f) {
						return entity1;
					}
				}
			}
			return null;
		}

		@Nullable
		public EntityTruthSeekerBall.EntityCustom getNextTSB(ItemStack stack, World world) {
			int i = 0;
			int next = stack.getTagCompound().getInteger(CURRENTBALL);
			for ( ; i < 9; i++) {
				if (++next >= 9) {
					next = 0;
				}
				EntityTruthSeekerBall.EntityCustom entity = this.getTSB(stack, world, next);
				if (entity != null && !entity.isLaunched() && !entity.isShieldOn()) {
					break;
				}
			}
			next = i < 9 ? next : -1;
			stack.getTagCompound().setInteger(CURRENTBALL, next);
			return this.getTSB(stack, world, next);
		}

		private boolean isTargeting(ItemStack stack, Entity entity) {
			for (int i = 0; i < 9; i++) {
				EntityTruthSeekerBall.EntityCustom entity1 = this.getTSB(stack, entity.world, i);
				if (entity1 != null && entity.equals(entity1.getTarget())) {
					return true;
				}
			}
			return false;
		}

		private void sentryDuty(ItemStack stack, EntityLivingBase entity) {
			for (Entity entity1 : entity.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().grow(6.0D),
			 new Predicate<Entity>() {
				public boolean apply(@Nullable Entity p_apply_1_) {
					return p_apply_1_ != null && !p_apply_1_.equals(entity) 
					    && !(p_apply_1_ instanceof net.minecraft.entity.item.EntityItem) 
					    && !(p_apply_1_ instanceof net.minecraft.entity.item.EntityXPOrb)
					    && (!(p_apply_1_ instanceof EntityTruthSeekerBall.EntityCustom)
					     || ((EntityTruthSeekerBall.EntityCustom)p_apply_1_).shootingEntity != entity)
					    && !RangedItem.this.isTargeting(stack, p_apply_1_);
				}
			})) {
				Vec3d vec = entity.getPositionVector().subtract(entity1.getPositionVector());
				float f0 = ProcedureUtils.getYawFromVec(entity1.motionX, entity1.motionZ);
				float f1 = ProcedureUtils.getYawFromVec(vec.x, vec.z);
				if (Math.abs(MathHelper.wrapDegrees(f1 - f0)) <= 15.0f
				 && entity1.motionX * entity1.motionX + entity1.motionZ * entity1.motionZ > 0.045d) {
					EntityTruthSeekerBall.EntityCustom entity2 = this.getNextTSB(stack, entity.world);
					if (entity2 != null) {
						entity2.setTarget(entity1, 10);
					}
				}
			}
		}
	}

	public static class ShootTruthSeekerBall implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (stack.getItem() == block) {
				EntityTruthSeekerBall.EntityCustom entity1 = ((RangedItem)stack.getItem()).getNextTSB(stack, entity.world);
				if (entity1 != null) {
					Vec3d vec = entity.getLookVec();
					entity1.shoot(vec.x, vec.y, vec.z, 0.98f, 0.0f);
					return true;
				}
			}
			return false;
		}
	}

	public static class TruthSeekerShield implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (stack.getItem() == block) {
				EntityTruthSeekerBall.EntityCustom entity1 = ((RangedItem)stack.getItem()).getNextTSB(stack, entity.world);
				if (entity1 != null) {
					entity1.toggleShield();
					return true;
				}
			}
			return false;
		}
	}
}
