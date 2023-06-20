
package net.narutomod.item;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;

import net.narutomod.entity.EntityToad;
import net.narutomod.entity.EntitySnake;
import net.narutomod.entity.EntitySlug;
import net.narutomod.entity.EntityEnma;
import net.narutomod.entity.EntityGamabunta;
import net.narutomod.entity.EntityManda;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.gui.GuiScrollFireStreamGui;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSummoningContract extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:summoning_contract")
	public static final Item block = null;
	public static final int ENTITYID = 353;
	public static final int ENTITY2ID = 354;
	public static final int ENTITY3ID = 333;
	public static final int ENTITY4ID = 334;
	public static final String SUMMON_RALLY = "SummonRallyPoint";
	public static final ItemJutsu.JutsuEnum SUMMONTOAD = new ItemJutsu.JutsuEnum(0, "toad_summon", 'C', 100d, new EntityGenericToad.Jutsu());
	public static final ItemJutsu.JutsuEnum SUMMONSNAKE = new ItemJutsu.JutsuEnum(1, "snake_summon", 'C', 100d, new EntityGenericSnake.Jutsu());
	public static final ItemJutsu.JutsuEnum SUMMONSLUG = new ItemJutsu.JutsuEnum(2, "slug", 'C', 100d, new EntitySlug.Jutsu());
	public static final ItemJutsu.JutsuEnum SUMMONENMA = new ItemJutsu.JutsuEnum(3, "enma", 'C', 100d, new EntityEnma.EC.Jutsu());

	public ItemSummoningContract(ElementsNarutomodMod instance) {
		super(instance, 718);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new ItemCustom(SUMMONTOAD, SUMMONSNAKE, SUMMONSLUG, SUMMONENMA));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityGenericToad.class)
		 .id(new ResourceLocation("narutomod", "toad_summon"), ENTITYID).name("toad_summon").tracker(96, 3, true).egg(-1, -1).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityGenericSnake.class)
		 .id(new ResourceLocation("narutomod", "snake_summon"), ENTITY2ID).name("snake_summon").tracker(96, 3, true).egg(-1, -1).build());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:summoning_contract", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityGenericToad.class, renderManager -> {
			return new EntityToad.RenderCustom<EntityGenericToad>(renderManager) {
				private final ResourceLocation texture = new ResourceLocation("narutomod:textures/toad1.png");
				@Override
				protected void renderModel(EntityGenericToad entity, float f1, float f2, float f3, float f4, float f5, float f6) {
					((EntityToad.ModelToad)this.mainModel).showPipe(false);
					super.renderModel(entity, f1, f2, f3, f4, f5, f6);
				}
				@Override
				protected ResourceLocation getEntityTexture(EntityGenericToad entity) {
					return texture;
				}
			};
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityGenericSnake.class, renderManager -> {
			return new EntitySnake.RenderSnake<EntityGenericSnake>(renderManager) {
				private final ResourceLocation texture1 = new ResourceLocation("narutomod:textures/snake_white.png");
				private final ResourceLocation texture2 = new ResourceLocation("narutomod:textures/snake_blue.png");
				@Override
				protected ResourceLocation getEntityTexture(EntityGenericSnake entity) {
					return entity.getScale() > 4.0f ? texture2 : texture1;
				}
			};
		});
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
	}

	public static class ItemCustom extends ItemJutsu.Base implements ItemOnBody.Interface {
		public ItemCustom(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.KUCHIYOSE, list);
			this.setUnlocalizedName("summoning_contract");
			this.setRegistryName("summoning_contract");
			this.setCreativeTab(TabModTab.tab);
			//this.defaultCooldownMap[SUMMONTOAD.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return jutsu == SUMMONENMA ? this.getPower(stack, entity, timeLeft, 0.0f, 200)
			 : this.getPower(stack, entity, timeLeft, 0.0f, 80);
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float ret = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return jutsu == SUMMONENMA ? Math.min(ret, 1.0f) : ret;
		}

		@Override
		protected boolean executeJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			return power >= 0.1f ? super.executeJutsu(stack, entity, power) : false;
		}

		@Override
		public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
			if (attacker.equals(target)) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(attacker, 50d, 3d);
				target = res.entityHit;
				/*if (res.typeOfHit == RayTraceResult.Type.BLOCK) {
					int[] ia = {res.getBlockPos().getX(), res.getBlockPos().getY(), res.getBlockPos().getZ()};
					attacker.getEntityData().setIntArray(SUMMON_RALLY, ia);
				}*/
			}
			if (target instanceof EntityLivingBase) {
				attacker.setRevengeTarget((EntityLivingBase)target);
			}
			return super.onLeftClickEntity(itemstack, attacker, target);
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!this.isAnyJutsuEnabled(itemstack) && itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("Type", 8)) {
				String type = itemstack.getTagCompound().getString("Type");
				this.enableJutsu(itemstack, SUMMONTOAD, type.equalsIgnoreCase("toad"));
				this.enableJutsu(itemstack, SUMMONSNAKE, type.equalsIgnoreCase("snake"));
				this.enableJutsu(itemstack, SUMMONSLUG, type.equalsIgnoreCase("slug"));
				this.enableJutsu(itemstack, SUMMONENMA, type.equalsIgnoreCase("enma"));
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			if (this.isJutsuEnabled(itemstack, SUMMONTOAD)) {
				list.add(TextFormatting.BLUE + new TextComponentTranslation("entity.toad.name").getUnformattedComponentText() + " "
				 + new TextComponentTranslation("item.summoning_contract.name").getUnformattedComponentText() + TextFormatting.RESET);
			} else if (this.isJutsuEnabled(itemstack, SUMMONSNAKE)) {
				list.add(TextFormatting.BLUE + new TextComponentTranslation("entity.snake.name").getUnformattedComponentText() + " "
				 + new TextComponentTranslation("item.summoning_contract.name").getUnformattedComponentText() + TextFormatting.RESET);
			} else if (this.isJutsuEnabled(itemstack, SUMMONSLUG)) {
				list.add(TextFormatting.BLUE + new TextComponentTranslation("entity.slug.name").getUnformattedComponentText() + " "
				 + new TextComponentTranslation("item.summoning_contract.name").getUnformattedComponentText() + TextFormatting.RESET);
			} else if (this.isJutsuEnabled(itemstack, SUMMONENMA)) {
				list.add(TextFormatting.BLUE + new TextComponentTranslation("entity.enma.name").getUnformattedComponentText() + " "
				 + new TextComponentTranslation("item.summoning_contract.name").getUnformattedComponentText() + TextFormatting.RESET);
			}
			super.addInformation(itemstack, world, list, flag);
		}
	}

	public static class EntityGenericToad extends EntityToad.EntityCustom {
		public EntityGenericToad(World w) {
			super(w);
			this.postScaleFixup();
		}

		public EntityGenericToad(EntityLivingBase summonerIn, float size) {
			super(summonerIn);
			this.setScale(size);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			EntityLivingBase owner = this.getSummoner();
			if (owner != null && !owner.isRiding() && this.getAge() == 1 && this.getScale() >= 4.0f) {
				owner.startRiding(this);
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Particles.spawnParticle(entity.world, Particles.Types.SEAL_FORMULA,
				 entity.posX, entity.posY + 0.015d, entity.posZ, 1, 0d, 0d, 0d, 0d, 0d, 0d, (int)(power * 40), 0, 60);
				for (int i = 0; i < 500; i++) {
					Particles.spawnParticle(entity.world, Particles.Types.SMOKE,
					 entity.posX, entity.posY + 0.015d, entity.posZ, 1, 0d, 0d, 0d,
					 (entity.getRNG().nextDouble()-0.5d) * 0.8d, entity.getRNG().nextDouble() * 0.6d + 0.2d, (entity.getRNG().nextDouble()-0.5d) * 0.8d,
					 0xD0FFFFFF, (int)(power * 30));
				}
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
				  net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kuchiyosenojutsu")),
				  net.minecraft.util.SoundCategory.PLAYERS, 1f, 0.8f);
				EntityToad.EntityCustom entity1 = power >= 16.0f ? new EntityGamabunta.EntityCustom(entity)
				 : new EntityGenericToad(entity, power);
				entity1.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0f);
				net.narutomod.event.SpecialEvent.setDelayedSpawnEvent(entity.world, entity1, 0, 0, 0, entity.world.getTotalWorldTime() + 20);
				return true;
			}
		}
	}

	public static class EntityGenericSnake extends EntitySnake.EntityCustom {
		public EntityGenericSnake(World w) {
			super(w);
			this.postScaleFixup();
		}

		public EntityGenericSnake(EntityLivingBase entity, float size) {
			super(entity);
			this.setScale(size);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			EntityLivingBase owner = this.getSummoner();
			if (owner != null && !owner.isRiding() && this.getAge() == 1 && this.getScale() >= 4.0f) {
				owner.startRiding(this);
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Particles.spawnParticle(entity.world, Particles.Types.SEAL_FORMULA,
				 entity.posX, entity.posY + 0.015d, entity.posZ, 1, 0d, 0d, 0d, 0d, 0d, 0d, (int)(power * 40), 0, 60);
				for (int i = 0; i < 500; i++) {
					Particles.spawnParticle(entity.world, Particles.Types.SMOKE,
					 entity.posX, entity.posY + 0.015d, entity.posZ, 1, 0d, 0d, 0d,
					 (entity.getRNG().nextDouble()-0.5d) * 0.8d, entity.getRNG().nextDouble() * 0.6d + 0.2d, (entity.getRNG().nextDouble()-0.5d) * 0.8d,
					 0xD0FFFFFF, (int)(power * 30));
				}
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
				  net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:kuchiyosenojutsu"))),
				  net.minecraft.util.SoundCategory.PLAYERS, 1f, 0.8f);
				EntitySnake.EntityCustom entity1 = power >= 18.0f ? new EntityManda.EntityCustom(entity)
				 : new EntityGenericSnake(entity, power);
				entity1.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0f);
				net.narutomod.event.SpecialEvent.setDelayedSpawnEvent(entity.world, entity1, 0, 0, 0, entity.world.getTotalWorldTime() + 20);
				return true;
			}
		}
	}
}