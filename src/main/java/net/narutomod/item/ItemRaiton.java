
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.client.renderer.entity.RenderManager;

import net.narutomod.entity.EntityLightningArc;
import net.narutomod.entity.EntityLightningBeast;
import net.narutomod.entity.EntityLightningPanther;
import net.narutomod.entity.EntityChidori;
import net.narutomod.entity.EntityFalseDarkness;
import net.narutomod.entity.EntityKirin;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemRaiton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:raiton")
	public static final Item block = null;
	public static final int ENTITYID = 129;
	public static final int ENTITY2ID = 10129;
	public static final ItemJutsu.JutsuEnum CHIDORI = new ItemJutsu.JutsuEnum(0, "chidori", 'A', EntityChidori.CHAKRA_USAGE, new EntityChidori.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum CHAKRAMODE = new ItemJutsu.JutsuEnum(1, "raitonchakramode", 'B', 10d, new EntityChakraMode.Jutsu());
	public static final ItemJutsu.JutsuEnum CHASINGDOG = new ItemJutsu.JutsuEnum(2, "lightning_beast", 'C', 20d, new EntityLightningBeast.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum GIAN = new ItemJutsu.JutsuEnum(3, "false_darkness", 'B', 100d, new EntityFalseDarkness.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum KIRIN = new ItemJutsu.JutsuEnum(4, "kirin", 'S', 1500d, new EntityKirin.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum BLACKPANTHER = new ItemJutsu.JutsuEnum(5, "lightning_panther", 'S', 50d, new EntityLightningPanther.EC.Jutsu());

	public ItemRaiton(ElementsNarutomodMod instance) {
		super(instance, 373);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(CHIDORI, CHAKRAMODE, CHASINGDOG, GIAN, KIRIN, BLACKPANTHER));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityChakraMode.class)
			.id(new ResourceLocation("narutomod", "raitonchakramode"), ENTITYID).name("raitonchakramode").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:raiton", "inventory"));
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.RAITON, list);
			this.setUnlocalizedName("raiton");
			this.setRegistryName("raiton");
			this.setCreativeTab(TabModTab.tab);
			//this.defaultCooldownMap[CHIDORI.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == CHASINGDOG) {
				return this.getPower(stack, entity, timeLeft, 4f, 30f);
			} else if (jutsu == GIAN) {
				return this.getPower(stack, entity, timeLeft, 1f, 150f);
			} else if (jutsu == KIRIN) {
				return this.getPower(stack, entity, timeLeft, 0f, 400f);
			} else if (jutsu == BLACKPANTHER) {
				return this.getPower(stack, entity, timeLeft, 0f, 100f);
			}
			return 1f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float f = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == KIRIN) {
				return Math.min(f, 1.0f);
			} else if (jutsu == BLACKPANTHER) {
				return Math.min(f, 5.0f);
			}
			return f;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			if (!player.world.isRemote) {
				ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
				if (jutsu == KIRIN) {
					EntityKirin.chargingEffects(player, this.getPower(stack, player, timeLeft));
				} else if (jutsu == BLACKPANTHER) {
					EntityLightningArc.spawnAsParticle(player.world, player.posX + this.itemRand.nextGaussian() * 0.3d, 
					  player.posY + this.itemRand.nextDouble() * 1.3d, player.posZ + this.itemRand.nextGaussian() * 0.3d,
					  1.0d, 0d, 0.15d, 0d, 0);
					Particles.spawnParticle(player.world, Particles.Types.SMOKE, player.posX, player.posY, player.posZ,
					  20, 0.3d, 0.0d, 0.3d, 0d, 0.5d, 0d, 0x20000000, 50, 5, 0xF0, player.getEntityId());
				}
			}
			super.onUsingTick(stack, player, timeLeft);
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ActionResult<ItemStack> result = super.onItemRightClick(world, entity, hand);
			if (result.getType() == EnumActionResult.SUCCESS && this.getCurrentJutsu(entity.getHeldItem(hand)) == KIRIN && !world.isRemote) {
				EntityKirin.startWeatherThunder(entity);
			}
			return result;
		}
	}

	public static class EntityChakraMode extends Entity {
		private final double CHAKRA_BURN = CHAKRAMODE.chakraUsage; // per second
		//private int maxCooldown = 400;
		private EntityLivingBase summoner;
		private ItemStack usingItemstack;
		private int strengthAmplifier = 9;
		//private int jutsuIndex;
		//private int duration;

		public EntityChakraMode(World a) {
			super(a);
			this.setSize(0.01f, 0.01f);
		}

		/*public EntityChakraMode(World worldIn, Vec3d fromVec, Vec3d toVec) {
			super(worldIn, fromVec, toVec, 0xC000E5FF, 200, 0.2f);
			this.soundVolume = 2f;
			this.soundPitch = 0.6f;
		}

		public EntityLightning(World world, Vec3d cVec, double length, double xMotion, double yMotion, double zMotion) {
			super(world, cVec, length, xMotion, yMotion, zMotion, 0xC000E5FF);
			this.soundVolume = 0.1f;
			this.soundPitch = 0.3f;
		}*/

		protected EntityChakraMode(EntityLivingBase summonerIn, ItemStack stack) {
			this(summonerIn.world);
			this.summoner = summonerIn;
			this.setPosition(summonerIn.posX, summonerIn.posY, summonerIn.posZ);
			//this.maxCooldown *= Chakra.getChakraModifier(summonerIn);
			if (stack.getItem() instanceof ItemJutsu.Base) {
				//this.jutsuIndex = ((ItemJutsu.Base)stack.getItem()).getCurrentJutsuIndex(stack);
				//((ItemJutsu.Base)stack.getItem()).setCurrentJutsuCooldown(stack, this.duration + this.maxCooldown);
				this.usingItemstack = stack;
			}
			if (summonerIn.isPotionActive(MobEffects.STRENGTH)) {
				this.strengthAmplifier += summonerIn.getActivePotionEffect(MobEffects.STRENGTH).getAmplifier() + 1;
			}
		}

		@Override
		protected void entityInit() {
		}

		@Override
		public void setDead() {
			super.setDead();
			this.setNewCooldown();
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.summoner != null && this.summoner.isEntityAlive()) {
				if (this.ticksExisted % 20 == 2) {
					if (!Chakra.pathway(this.summoner).consume(this.CHAKRA_BURN)) {
						this.setDead();
					}
					this.summoner.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 22, 3, false, false));
					this.summoner.addPotionEffect(new PotionEffect(MobEffects.SPEED, 22, 32, false, false));
					this.summoner.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 22, this.strengthAmplifier, false, false));
					this.summoner.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 22, 6, false, false));
				}
				this.setPosition(this.summoner.posX, this.summoner.posY, this.summoner.posZ);
				if (this.rand.nextInt(8) == 0) {
					this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:electricity")),
					 0.1f, this.rand.nextFloat() * 0.6f + 0.3f);
				}
				EntityLightningArc.spawnAsParticle(this.world, this.posX + this.rand.nextGaussian() * 0.3d, 
				  this.posY + this.rand.nextDouble() * 1.3d, this.posZ + this.rand.nextGaussian() * 0.3d,
				  0.5d, 0d, 0.15d, 0d);
				Particles.spawnParticle(world, Particles.Types.SMOKE, this.posX, this.posY, this.posZ,
				  20, 0.3d, 0.0d, 0.3d, 0d, 0.5d, 0d, 0x2080D0FF, 50, 5, 0xF0, this.summoner.getEntityId());
			} else if (!this.world.isRemote) {
				this.setDead();
			}
		}

		private void setNewCooldown() {
			if (this.usingItemstack != null && this.summoner != null) {
				ItemStack stack = this.summoner instanceof EntityPlayer
				 ? ProcedureUtils.getMatchingItemStack((EntityPlayer)this.summoner, this.usingItemstack)
				 : this.usingItemstack;
				if (stack != null) {
					ItemJutsu.Base item = (ItemJutsu.Base)stack.getItem();
					item.setJutsuCooldown(stack, CHAKRAMODE, (long)((float)this.ticksExisted * item.getModifier(stack, this.summoner)));
				}
			}
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "EntityChakraModeIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ID_KEY));
				if (entity1 instanceof EntityChakraMode) {
					entity1.setDead();
					return false;
				} else {
					entity1 = new EntityChakraMode(entity, stack);
					entity.world.spawnEntity(entity1);
					entity.getEntityData().setInteger(ID_KEY, entity1.getEntityId());
					return true;
				}
			}
		}
	}
}
