
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.entity.EntitySandBullet;
import net.narutomod.entity.EntitySandGathering;
import net.narutomod.entity.EntityPuppet3rdKazekage;
import net.narutomod.entity.EntityPuppet;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
public class ItemScroll3rdKazekage extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:scroll_3rd_kazekage")
	public static final Item block = null;
	public static final int ENTITYID = 436;
	public static final ItemJutsu.JutsuEnum SANDBULLET = new ItemJutsu.JutsuEnum(0, "sand_bullet", 'S', 100, 20d, new EntitySandBullet.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum GATHERING = new ItemJutsu.JutsuEnum(1, "sand_gathering", 'S', 200, 100d, new EntitySandGathering.EC.Jutsu());

	public ItemScroll3rdKazekage(ElementsNarutomodMod instance) {
		super(instance, 867);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(SANDBULLET, GATHERING));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityScroll.class)
				.id(new ResourceLocation("narutomod", "entitybulletscroll_3rd_kazekage"), ENTITYID).name("entitybulletscroll_3rd_kazekage")
				.tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:scroll_3rd_kazekage", "inventory"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.OTHER, list);
			this.setMaxDamage((int)EntityPuppet3rdKazekage.EntityCustom.MAXHEALTH);
			this.setUnlocalizedName("scroll_3rd_kazekage");
			this.setRegistryName("scroll_3rd_kazekage");
			this.setCreativeTab(TabModTab.tab);
		}

		public static boolean useItem(ItemStack stack, EntityLivingBase entity, BlockPos pos) {
			if (!stack.hasTagCompound()
			 || (!stack.getTagCompound().getBoolean("isScrollOpening") && stack.getTagCompound().getInteger("puppetId") == 0)) {
				if (!entity.world.isRemote) {
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_CLOTH_PLACE,
							SoundCategory.NEUTRAL, 1, 1f / (entity.getRNG().nextFloat() * 0.5f + 1f) + 0.5f);
					EntityScroll entity1 = new EntityScroll(entity, stack.getMaxDamage() - stack.getItemDamage(), stack);
					entity1.setLocationAndAngles(0.5d + pos.getX(), 1.1d + pos.getY(), 0.5d + pos.getZ(), entity.rotationYaw, 0f);
					entity.world.spawnEntity(entity1);
					if (!stack.hasTagCompound()) {
						stack.setTagCompound(new NBTTagCompound());
					}
					stack.getTagCompound().setBoolean("isScrollOpening", true);
				}
				return true;
			}
			return false;
		}

		public static boolean isScrollOpening(ItemStack stack) {
			return stack.hasTagCompound() && stack.getTagCompound().getBoolean("isScrollOpening");
		}

		@Override
		public EnumActionResult onItemUse(EntityPlayer entity, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
			if (world.getBlockState(pos).isTopSolid() && facing == EnumFacing.UP && useItem(entity.getHeldItem(hand), entity, pos)) {
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.PASS;
		}

		public static void interactWithEntity(ItemStack stack, EntityLivingBase playerIn, EntityLivingBase target) {
			if (target instanceof EntityPuppet3rdKazekage.EntityCustom && !playerIn.world.isRemote) {
				if (stack.hasTagCompound() && stack.getTagCompound().getInteger("puppetId") > 0) {
					ProcedureUtils.poofWithSmoke(target);
					stack.setItemDamage((int)(target.getMaxHealth() - target.getHealth()));
					target.setDead();
					stack.getTagCompound().setInteger("puppetId", 0);
				}
			}
		}

		@Override
		public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
			if (target instanceof EntityPuppet3rdKazekage.EntityCustom && !playerIn.world.isRemote) {
				ItemStack stack1 = playerIn.getHeldItem(hand);
				if (stack1.hasTagCompound() && stack1.getTagCompound().getInteger("puppetId") > 0) {
					ProcedureUtils.poofWithSmoke(target);
					this.setDamage(stack1, (int)(target.getMaxHealth() - target.getHealth()));
					target.setDead();
					stack1.getTagCompound().setInteger("puppetId", 0);
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
			EntityPuppet3rdKazekage.EntityCustom puppet = this.getPuppetEntity(itemstack, attacker.world);
			if (attacker.equals(target)) {
				target = ProcedureUtils.objectEntityLookingAt(attacker, 50d, 3d, puppet == null || puppet.getAttackTarget() == null ? puppet : null).entityHit;
			}
			if (target != null && target.equals(puppet)) {
				puppet.setAttackTarget(null);
				return true;
			}
			if (target instanceof EntityLivingBase && puppet != null) {
				puppet.setAttackTarget((EntityLivingBase)target);
			}
			return super.onLeftClickEntity(itemstack, attacker, target);
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			super.onUsingTick(stack, player, timeLeft);
			if (!player.world.isRemote) {
				EntityPuppet3rdKazekage.EntityCustom puppet = this.getPuppetEntity(stack, player.world);
				if (puppet != null && puppet.isEntityAlive() && this.getCurrentJutsu(stack) == SANDBULLET
				 && this.getPower(stack, player, timeLeft) < this.getMaxPower(stack, player)) {
					EntitySandBullet.addPos(ItemJiton.Type.IRON, puppet, this.getPower(stack, player, timeLeft),
					 puppet.getPositionVector().addVector(0d, 1.5d, 0d));
					puppet.setMouthOpen(true);
				}
			}
		}

		@Override
		public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(stack, world, entity, par4, par5);
			if (!world.isRemote) {
				if (this.getXpRatio(stack, SANDBULLET) < 1.0f) {
					this.addJutsuXp(stack, SANDBULLET, this.getRequiredXp(stack, SANDBULLET) - this.getJutsuXp(stack, SANDBULLET));
				}
				if (this.getXpRatio(stack, GATHERING) < 1.0f) {
					this.addJutsuXp(stack, GATHERING, this.getRequiredXp(stack, GATHERING) - this.getJutsuXp(stack, GATHERING));
				}
				if (entity.ticksExisted % 20 == 3) {
					EntityPuppet3rdKazekage.EntityCustom puppet = this.getPuppetEntity(stack, world);
					if (puppet != null && puppet.isEntityAlive() && entity.equals(puppet.getSummoner())) {
						this.enableAllJutsus(stack, true);
						this.setDamage(stack, (int)(puppet.getMaxHealth() - puppet.getHealth()));
						EntitySandBullet.updateSwarms(puppet);
					} else {
						this.enableAllJutsus(stack, false);
					}
				}
			}
		}

		@Override
		public EnumActionResult canActivateJutsu(ItemStack stack, ItemJutsu.JutsuEnum jutsuIn, EntityPlayer entity) {
			EnumActionResult res = super.canActivateJutsu(stack, jutsuIn, entity);
			if (res == EnumActionResult.SUCCESS && EntityPuppet.Base.canPlayerUseJutsu(entity)) {
				EntityPuppet3rdKazekage.EntityCustom puppet = this.getPuppetEntity(stack, entity.world);
				return puppet != null && puppet.isEntityAlive() ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
			}
			return res;
		}

		@Override
		public boolean executeJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			EntityPuppet3rdKazekage.EntityCustom puppet = this.getPuppetEntity(stack, entity.world);
			if (puppet != null) {
				ItemJutsu.JutsuEnum jutsuEnum = this.getCurrentJutsu(stack);
				Chakra.Pathway pw = Chakra.pathway(entity);
				double d = jutsuEnum.chakraUsage * power;
				if (power <= 0f || pw.getAmount() < d) {
					return false;
				}
				if (jutsuEnum.jutsu.createJutsu(stack, puppet, power)) {
					pw.consume(d);
					return true;
				}
			}
			return false;
		}

		public EntityPuppet3rdKazekage.EntityCustom getPuppetEntity(ItemStack stack, World world) {
			if (stack.hasTagCompound() && stack.getTagCompound().getInteger("puppetId") > 0) {
				Entity entity = world.getEntityByID(stack.getTagCompound().getInteger("puppetId"));
				return entity instanceof EntityPuppet3rdKazekage.EntityCustom ? (EntityPuppet3rdKazekage.EntityCustom)entity : null;
			}
			return null;
		}

		@Override
		public int getMaxDamage() {
			return (int)EntityPuppet3rdKazekage.EntityCustom.MAXHEALTH;
		}
	}

	public static class EntityScroll extends Entity {
		private final int openScrollTime = 30;
		private EntityLivingBase summoner;
		private float puppetHealth;
		private ItemStack scrollStack;
		
		public EntityScroll(World a) {
			super(a);
			this.setSize(1.0f, 0.2f);
		}

		public EntityScroll(EntityLivingBase summonerIn, float health, ItemStack stack) {
			this(summonerIn.world);
			this.summoner = summonerIn;
			this.puppetHealth = health;
			this.scrollStack = stack;
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
					EntityPuppet3rdKazekage.EntityCustom entity = new EntityPuppet3rdKazekage.EntityCustom(this.summoner, ItemNinjutsu.PUPPET.chakraUsage);
					entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.summoner.rotationYaw, 0f);
					entity.onInitialSpawn(this.world.getDifficultyForLocation(this.getPosition()), null);
					this.world.spawnEntity(entity);
					entity.setHealth(this.puppetHealth);
					ProcedureUtils.poofWithSmoke(entity);
					if (this.scrollStack != null) {
						ItemStack stack = this.summoner instanceof EntityPlayer
						 ? ProcedureUtils.getMatchingItemStack((EntityPlayer)this.summoner, this.scrollStack)
						 : this.scrollStack;
						if (!stack.hasTagCompound()) {
							stack.setTagCompound(new NBTTagCompound());
						}
						stack.getTagCompound().setInteger("puppetId", entity.getEntityId());
						stack.getTagCompound().removeTag("isScrollOpening");
					}
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
			RenderingRegistry.registerEntityRenderingHandler(EntityScroll.class, renderManager -> {
				return new RenderCustom(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityPuppet.ClientClass.RenderScroll<EntityScroll> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/scroll_3rdkazekage.png");
	
			public RenderCustom(RenderManager renderManager) {
				super(renderManager);
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityScroll entity) {
				return this.texture;
			}
		}

	}
}
