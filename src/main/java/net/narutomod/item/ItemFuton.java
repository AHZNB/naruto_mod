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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.PlayerTracker;
import net.narutomod.Particles;
import net.narutomod.entity.EntityRasenshuriken;
import net.narutomod.entity.EntityFutonGreatBreakthrough;
import net.narutomod.entity.EntityFutonVacuum;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityChakraFlow;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.potion.PotionReach;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextFormatting;

@ElementsNarutomodMod.ModElement.Tag
public class ItemFuton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:futon")
	public static final Item block = null;
	public static final int ENTITYID = 132;
	public static final int ENTITY2ID = 10132;
	private static final Random rand = new Random();
	public static final ItemJutsu.JutsuEnum CHAKRAFLOW = new ItemJutsu.JutsuEnum(0, "futonchakraflow", 'D', 20d, new ChakraFlow.Jutsu());
	public static final ItemJutsu.JutsuEnum RASENSHURIKEN = new ItemJutsu.JutsuEnum(1, "rasenshuriken", 'S', 1000d, new EntityRasenshuriken.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum VACUUMS = new ItemJutsu.JutsuEnum(2, "futon_vacuum", 'B', 20d, new EntityFutonVacuum.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum BIGBLOW = new ItemJutsu.JutsuEnum(3, "futon_great_breakthrough", 'C', 20d, new EntityFutonGreatBreakthrough.EC.Jutsu());

	public ItemFuton(ElementsNarutomodMod instance) {
		super(instance, 376);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(CHAKRAFLOW, RASENSHURIKEN, VACUUMS, BIGBLOW));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(ChakraFlow.class)
		  .id(new ResourceLocation("narutomod", "futonchakraflow"), ENTITYID).name("futonchakraflow").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:futon", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(ChakraFlow.class, renderManager -> {
			return new RenderChakraFlow(renderManager);
		});
	}

	public static boolean isJutsuEnabled(@Nullable ItemStack stack, ItemJutsu.JutsuEnum jutsu) {
		return stack != null && stack.getItem() == block && ((RangedItem)stack.getItem()).isJutsuEnabled(stack, jutsu);
	}

	public static class RangedItem extends ItemJutsu.Base {
		//private static final UUID REACH_MODIFIER = UUID.fromString("d86019c3-98a6-46c6-b81c-eadb3e1f15e9");

		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.FUTON, list);
			this.setUnlocalizedName("futon");
			this.setRegistryName("futon");
			this.setCreativeTab(TabModTab.tab);
			//this.defaultCooldownMap[RASENSHURIKEN.index] = 0;
			//this.defaultCooldownMap[CHAKRAFLOW.index] = 0;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			float f = super.getMaxPower(stack, entity);
			if (jutsu == RASENSHURIKEN) {
				return Math.min(f, 2.0f);
			} else if (jutsu == VACUUMS) {
				return Math.min(f, 50.0f);
			} else if (jutsu == BIGBLOW) {
				return Math.min(f, EntityFutonGreatBreakthrough.EC.MAX_RANGE);
			}
			return f;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			if (jutsu == RASENSHURIKEN) {
				//float f = entity instanceof EntityPlayer ? 0.0334f * ((EntityPlayer)entity).experienceLevel : 1;
				//f *= Math.min(0.01f * (this.getMaxUseDuration() - timeLeft), 1f);
				//return Math.min(f, this.getMaxPower(stack, entity));
				return this.getPower(stack, entity, timeLeft, 0f, 300f);
			} else if (jutsu == VACUUMS) {
				return this.getPower(stack, entity, timeLeft, 0f, 20f);
				//return Math.min((float)(this.getMaxUseDuration() - timeLeft) / 5f, this.getMaxPower(stack, entity));
			} else if (jutsu == BIGBLOW) {
				return this.getPower(stack, entity, timeLeft, 5f, 20f);
				//return Math.min(5f + (float)(this.getMaxUseDuration() - timeLeft) / 10f, this.getMaxPower(stack, entity));
			}
			return 1f;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityPlayer && entity.ticksExisted % 10 == 3) {
				ItemStack stack1 = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemNinjutsu.block);
				boolean rasenshurikenEnabled = this.isJutsuEnabled(itemstack, RASENSHURIKEN);
				boolean rasenganEnabled = stack1 != null
				 ? ((ItemNinjutsu.RangedItem)stack1.getItem()).canUseJutsu(stack1, ItemNinjutsu.RASENGAN, (EntityPlayer)entity)
				  && ((ItemNinjutsu.RangedItem)stack1.getItem()).getXpRatio(stack1, ItemNinjutsu.RASENGAN) >= 1.0f : false;
				if (rasenshurikenEnabled && !rasenganEnabled) {
					this.enableJutsu(itemstack, RASENSHURIKEN, false);
				} else if (!rasenshurikenEnabled && rasenganEnabled) {
					this.enableJutsu(itemstack, RASENSHURIKEN, true);
					((EntityPlayer)entity).sendStatusMessage(new TextComponentTranslation("chattext.jutsu.enabled", RASENSHURIKEN.getName()), false);
				}
			}
		}
	}

	public static class ChakraFlow extends EntityChakraFlow.Base {
		private int strengthModifier = 2;

		public ChakraFlow(World world) {
			super(world);
		}

		public ChakraFlow(EntityLivingBase user, ItemStack itemstack) {
			super(user);
			if (itemstack.getItem() == block) {
				float f = ((RangedItem)itemstack.getItem()).getCurrentJutsuXpModifier(itemstack, user);
				if (f > 0.0f) {
					f = 1.0f / f;
					if (user instanceof EntityPlayer) {
						f *= PlayerTracker.getNinjaLevel((EntityPlayer)user) / 30d;
					}
					this.strengthModifier = (int)f;
				}
			}
		}

		@Override
		protected void addEffects() {
			if (!this.world.isRemote && this.ticksExisted % 10 == 0) {
				EntityLivingBase user = this.getUser();
				//int strAmp =  user instanceof EntityPlayer ? (int)(PlayerTracker.getNinjaLevel((EntityPlayer)user) / 20d * this.strengthModifier) : 3;
				user.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 12, this.strengthModifier + this.ogStrength, false, false));
				user.addPotionEffect(new PotionEffect(PotionReach.potion, 12, 0, false, false));
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				EntityLivingBase user = this.getUser();
				if (user != null && this.isUserHoldingWeapon() && this.ticksExisted % 10 == 1 
				 && !net.narutomod.Chakra.pathway(user).consume(CHAKRAFLOW.chakraUsage * 0.1d)) {
					this.setDead();
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "FutonChakraFlowEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY));
				if (entity1 instanceof ChakraFlow) {
					entity1.setDead();
					stack.getTagCompound().removeTag(ID_KEY);
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer)entity).sendStatusMessage(new TextComponentString("Off"), true);
					}
					return false;
				} else {
					if (ItemRaiton.CHAKRAMODE.jutsu.isActivated(entity)) {
						ItemRaiton.CHAKRAMODE.jutsu.deactivate(entity);
					}
					if (ItemRaiton.CHIDORI.jutsu.isActivated(entity)) {
						ItemRaiton.CHIDORI.jutsu.deactivate(entity);
					}
					entity1 = new ChakraFlow(entity, stack);
					entity.world.spawnEntity(entity1);
					stack.getTagCompound().setInteger(ID_KEY, entity1.getEntityId());
					if (entity instanceof EntityPlayer && !entity.world.isRemote) {
						((EntityPlayer)entity).sendStatusMessage(new TextComponentString("On"), true);
					}
					return true;
				}
			}

			@Override
			public boolean isActivated(EntityLivingBase entity) {
				return this.getData(entity) != null;
			}

			@Override
			public void deactivate(EntityLivingBase entity) {
				ItemJutsu.IJutsuCallback.JutsuData jd = this.getData(entity);
				if (jd != null) {
					jd.entity.setDead();
					jd.stack.getTagCompound().removeTag(ID_KEY);
				}
			}

			@Override
			@Nullable
			public ItemJutsu.IJutsuCallback.JutsuData getData(EntityLivingBase entity) {
				if (entity instanceof EntityPlayer) {
					ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, block);
					if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(ID_KEY)) {
						Entity entity1 = entity.world.getEntityByID(stack.getTagCompound().getInteger(ID_KEY));
						return entity1 instanceof ChakraFlow ? new JutsuData(entity1, stack) : null;
					}
				}
				return null;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public class RenderChakraFlow extends EntityChakraFlow.RenderCustom<ChakraFlow> {
		public RenderChakraFlow(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}

		@Override
		protected void spawnParticles(ChakraFlow entity, Vec3d startvec, Vec3d endvec) {
			Vec3d vec = endvec.subtract(startvec).scale(0.2);
			Particles.spawnParticle(entity.world, Particles.Types.SMOKE, startvec.x, startvec.y, startvec.z, 
			  10, 0.05d, 0.05d, 0.05d, vec.x, vec.y, vec.z, 0x086AD1FF, 10, 5, 0xF0);
		}
	}
}
