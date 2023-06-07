
package net.narutomod.item;

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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;

import net.narutomod.entity.EntitySpike;
import net.narutomod.entity.EntityFingerBone;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.minecraftforge.items.ItemHandlerHelper;

@ElementsNarutomodMod.ModElement.Tag
public class ItemShikotsumyaku extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:shikotsumyaku")
	public static final Item block = null;
	public static final int ENTITYID = 318;
	public static final ItemJutsu.JutsuEnum LARCH = new ItemJutsu.JutsuEnum(0, "tooltip.shikotsumyaku.dancelarch", 'S', 150, 100d, new LarchDance());
	public static final ItemJutsu.JutsuEnum WILLOW = new ItemJutsu.JutsuEnum(1, "tooltip.shikotsumyaku.dancewillow", 'S', 150, 100d, new WillowDance());
	public static final ItemJutsu.JutsuEnum CAMELLIA = new ItemJutsu.JutsuEnum(2, "tooltip.shikotsumyaku.dancecamellia", 'S', 150, 100d, new CamelliaDance());
	public static final ItemJutsu.JutsuEnum BULLETS = new ItemJutsu.JutsuEnum(3, "finger_bone", 'S', 150, 5d, new EntityFingerBone.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum CFLOWER = new ItemJutsu.JutsuEnum(4, "tooltip.shikotsumyaku.danceclementisflower", 'S', 400, 500d, new ClementisFlower());
	public static final ItemJutsu.JutsuEnum BRACKEN = new ItemJutsu.JutsuEnum(5, "entitybrackendance", 'S', 400, 20d, new EntityBrackenDance.Jutsu());

	public ItemShikotsumyaku(ElementsNarutomodMod instance) {
		super(instance, 659);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(LARCH, WILLOW, CAMELLIA, BULLETS, CFLOWER, BRACKEN));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityBrackenDance.class)
		 .id(new ResourceLocation("narutomod", "entitybrackendance"), ENTITYID).name("entitybrackendance").tracker(64, 1, true)
		.build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:shikotsumyaku", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityBrackenDance.class, renderManager -> {
			return new EntitySpike.Renderer<EntityBrackenDance>(renderManager) {
				@Override
				protected ResourceLocation getEntityTexture(EntityBrackenDance entity) {
					return new ResourceLocation("narutomod:textures/spike_bone.png");
				}
			};
		});
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.SHIKOTSUMYAKU, list);
			setUnlocalizedName("shikotsumyaku");
			setRegistryName("shikotsumyaku");
			setCreativeTab(TabModTab.tab);
			for (int i = 0; i < list.length; i++) {
				this.defaultCooldownMap[i] = 0;
			}
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			 if (this.getCurrentJutsu(stack) == BRACKEN) {
				return this.getPower(stack, entity, timeLeft, 0.5f, 10f);
			 }
			return 1f;
		}
	}

	public static class LarchDance implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) 
				  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:bonecrack"))),
				  SoundCategory.PLAYERS, 1f, 1f);
			ItemStack cheststack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (!ItemBoneArmor.isLarchActive(cheststack)) {
				if (cheststack.getItem() != ItemBoneArmor.body) {
					cheststack = new ItemStack(ItemBoneArmor.body);
					ItemBoneArmor.setLarchActive(cheststack, true);
					if (entity instanceof EntityPlayer) {
						ProcedureUtils.swapItemToSlot((EntityPlayer)entity, EntityEquipmentSlot.CHEST, cheststack);
					} else {
						entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, cheststack);
					}
				} else {
					ItemBoneArmor.setLarchActive(cheststack, true);
				}
				return true;
			} else {
				ItemBoneArmor.setLarchActive(cheststack, false);
			}
			return false;
		}
	}

	public static class WillowDance implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) 
				  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:bonecrack"))),
				  SoundCategory.PLAYERS, 1f, 1f);
			ItemStack cheststack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (!ItemBoneArmor.isWillowActive(cheststack)) {
				if (cheststack.getItem() != ItemBoneArmor.body) {
					cheststack = new ItemStack(ItemBoneArmor.body);
					ItemBoneArmor.setWillowActive(cheststack, true);
					if (entity instanceof EntityPlayer) {
						ProcedureUtils.swapItemToSlot((EntityPlayer)entity, EntityEquipmentSlot.CHEST, cheststack);
					} else {
						entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, cheststack);
					}
				} else {
					ItemBoneArmor.setWillowActive(cheststack, true);
				}
				return true;
			} else {
				ItemBoneArmor.setWillowActive(cheststack, false);
			}
			return false;
		}
	}

	public static class CamelliaDance implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (entity.getHeldItemMainhand().getItem() != ItemBoneSword.block) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) 
					  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:bonecrack"))),
					  SoundCategory.PLAYERS, 1f, 1f);
				ItemStack itemstack = new ItemStack(ItemBoneSword.block);
				if (entity instanceof EntityPlayer) {
					ProcedureUtils.swapItemToSlot((EntityPlayer)entity, EntityEquipmentSlot.MAINHAND, itemstack);
				} else {
					entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, itemstack);
				}
				return true;
			}
			return false;
		}
	}

	public static class ClementisFlower implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			/*entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) 
				  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:bonecrack"))),
				  SoundCategory.PLAYERS, 1f, 1f);
			ItemStack cheststack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (!ItemBoneArmor.isClementisFlowerActive(cheststack)) {
				if (cheststack.getItem() != ItemBoneArmor.body) {
					cheststack = new ItemStack(ItemBoneArmor.body);
					ItemBoneArmor.setClementisFlowerActive(cheststack, true);
					if (entity instanceof EntityPlayer) {
						ProcedureUtils.swapItemToSlot((EntityPlayer)entity, EntityEquipmentSlot.CHEST, cheststack);
					} else {
						entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, cheststack);
					}
				} else {
					ItemBoneArmor.setClementisFlowerActive(cheststack, true);
				}
				return true;
			} else {
				ItemBoneArmor.setClementisFlowerActive(cheststack, false);
			}
			return false;*/
			if (entity instanceof EntityPlayer) {
				if (!ProcedureUtils.hasItemInInventory((EntityPlayer)entity, ItemBoneDrill.block)) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) 
						  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:bonecrack"))),
						  SoundCategory.PLAYERS, 1f, 1f);
					ItemHandlerHelper.giveItemToPlayer((EntityPlayer)entity, new ItemStack(ItemBoneDrill.block));
					ItemJutsu.setCurrentJutsuCooldown(stack, (EntityPlayer)entity, 1200);
					return true;
				}
			} else if (entity.getHeldItemMainhand().getItem() != ItemBoneDrill.block) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, (SoundEvent) 
					  SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:bonecrack"))),
					  SoundCategory.PLAYERS, 1f, 1f);
				entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemBoneDrill.block));
				return true;
			}
			return false;
		}
	}

	public static class EntityBrackenDance extends EntitySpike.Base {
		private final int growTime = 8;
		private final float maxScale = 2.0f;
		private final float damage = 20.0f;

		public EntityBrackenDance(World worldIn) {
			super(worldIn);
			this.setColor(0xFFFFFFFF);
		}

		public EntityBrackenDance(EntityLivingBase userIn, float damageIn) {
			super(userIn, 0xFFFFFFFF);
			//this.damage = damageIn;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksAlive <= this.growTime) {
				this.setEntityScale(MathHelper.clamp(this.maxScale * (float)this.ticksAlive / this.growTime, 0.0f, this.maxScale));
				for (EntityLivingBase entity : 
				 this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(1d, 0d, 1d))) {
					if (!entity.equals(this.shootingEntity)) {
						entity.hurtResistantTime = 10;
						entity.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this.shootingEntity),
						 this.damage * (1f - (float)(this.ticksAlive - 1) / this.growTime));
					}
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				World world = entity.world;
				Vec3d vec3d = entity.getPositionEyes(1f);
				Vec3d vec3d2 = vec3d.add(entity.getLookVec().scale(30d));
				RayTraceResult res = world.rayTraceBlocks(vec3d, vec3d2, false, true, true);
				if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK && res.sideHit == EnumFacing.UP) {
					float f = MathHelper.sqrt(power * 9f / 5f);
					for (int i = 0; i < Math.round(power); i++) {
						EntityBrackenDance entity1 = new EntityBrackenDance(entity, power);
						Vec3d vec = res.hitVec.addVector((entity.getRNG().nextDouble() - 0.5d) * f, 0d, (entity.getRNG().nextDouble() - 0.5d) * f);
						for (; !world.getBlockState(new BlockPos(vec)).isTopSolid(); vec = vec.subtract(0d, 1d, 0d));
						for (; world.getBlockState(new BlockPos(vec).up()).isTopSolid(); vec = vec.addVector(0d, 1d, 0d));
						entity1.setLocationAndAngles(vec.x, vec.y + 0.5d, vec.z, entity.getRNG().nextFloat() * 360f, (entity.getRNG().nextFloat() - 0.5f) * 60f);
						world.spawnEntity(entity1);
						world.playSound(null, entity1.posX, entity1.posY, entity1.posZ, (SoundEvent)
						 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:bonecrack")),
						 SoundCategory.NEUTRAL, 5f, entity.getRNG().nextFloat() * 0.4f + 0.4f);
					}
					return true;
				}
				return false;
			}
		}
	}

}
