
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemKunai;
import net.narutomod.item.ItemWhiteZetsuFlesh;
import net.narutomod.Chakra;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityWhiteZetsu extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 70;
	public static final int ENTITYID_RANGED = 71;
	
	public EntityWhiteZetsu(ElementsNarutomodMod instance) {
		super(instance, 290);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "whitezetsu"), ENTITYID).name("whitezetsu").tracker(64, 3, true).egg(-16724788, -1).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		int i = MathHelper.clamp(ModConfig.SPAWN_WEIGHT_WHITEZETSU, 0, 20);
		if (i > 0) {
			EntityRegistry.addSpawn(EntityCustom.class, i, 1, 1, EnumCreatureType.MONSTER, 
				Biomes.EXTREME_HILLS, Biomes.FOREST, Biomes.TAIGA, Biomes.SWAMPLAND, Biomes.BEACH, Biomes.JUNGLE,
				Biomes.BIRCH_FOREST, Biomes.ROOFED_FOREST, Biomes.REDWOOD_TAIGA, Biomes.SAVANNA, Biomes.MESA,
				Biomes.MUTATED_FOREST, Biomes.MUTATED_TAIGA, Biomes.MUTATED_SWAMPLAND, Biomes.MUTATED_JUNGLE,
				Biomes.MUTATED_BIRCH_FOREST, Biomes.MUTATED_ROOFED_FOREST, Biomes.MUTATED_REDWOOD_TAIGA, Biomes.MUTATED_SAVANNA);
		}
	}

	public static class EntityCustom extends EntityClone._Base implements IMob {
		private final ItemStack kunaiStack = new ItemStack(ItemKunai.block, 1);
		private ItemStack oldHeldStack = ItemStack.EMPTY;
		private double collectedChakra;

		public EntityCustom(World world) {
			super(world);
			this.experienceValue = 25;
			this.shouldDefendSummoner = false;
			this.moveHelper = new EntityNinjaMob.MoveHelper(this);
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(5.0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6D);
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false, false));
			this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 64f, 0.05f) {
				@Override
				public void updateTask() {
					super.updateTask();
					World world = this.entity.world;
					if (EntityCustom.this.getSummoner() == null && this.closestEntity instanceof EntityPlayer) {
						for (int i = 0; i < 10; i++) {
							EntityLivingBase entity1;
							if (world.playerEntities.size() > 1) {
								entity1 = world.playerEntities.get(rand.nextInt(world.playerEntities.size()));
							} else {
								List<EntityLiving> list = world.getEntitiesWithinAABB(EntityLiving.class, EntityCustom.this.getEntityBoundingBox().grow(64, 32, 64), (p)-> {
									return p instanceof EntityZombie || p instanceof EntityVillager || p instanceof EntityEnderman
									 || p instanceof EntityNinjaMerchant.Base;
								});
								entity1 = !list.isEmpty() ? list.get(rand.nextInt(list.size())) : null;
							}
							if (entity1 != null && !entity1.equals(this.closestEntity)) {
								EntityCustom.this.setSummoner(entity1);
								return;
							}
						}
					}
				}
			});
			this.tasks.addTask(3, new EntityAIWander(this, 0.5));
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			if (this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive()) {
				ItemStack stack = this.getHeldItemMainhand();
				if (!ProcedureUtils.isWeapon(stack)) {
					this.oldHeldStack = stack; 
					this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, this.kunaiStack);
				}
			} else if (this.getHeldItemMainhand().getItem() != this.oldHeldStack.getItem()) {
				this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, this.oldHeldStack);
			}
		}

		@Override
		protected boolean canDropLoot() {
			return true;
		}

		@Override
		protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
			if (this.rand.nextFloat() <= 0.1f) {
				this.entityDropItem(new ItemStack(ItemWhiteZetsuFlesh.block, 1), 0.0f);
			}
			if (this.rand.nextFloat() >= 0.6f) {
				this.entityDropItem(this.kunaiStack, 0.0f);
			}
		}

		@Override
		public net.minecraft.util.SoundEvent getAmbientSound() {
			return null;
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return SoundEvents.ENTITY_WITCH_HURT;
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return SoundEvents.ENTITY_WITCH_DEATH;
		}

		@Override
		protected float getSoundVolume() {
			return 1.0F;
		}

		@Override
		public void onDeath(DamageSource source) {
			super.onDeath(source);
			if (!this.world.isRemote && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
				BlockPos pos = new BlockPos(this).down();
				IBlockState blockstate = this.world.getBlockState(pos);
				if (blockstate.getMaterial() != Material.AIR && blockstate.getBlockHardness(this.world, pos) >= 0) {
					if (blockstate.getMaterial() != Material.GRASS) {
						this.world.setBlockState(pos, Blocks.GRASS.getDefaultState(), 3);
					}
					WorldGenAbstractTree worldgenabstracttree = this.world.getBiome(pos).getRandomTreeFeature(this.rand);
					if (worldgenabstracttree != null) {
						worldgenabstracttree.setDecorationDefaults();
						worldgenabstracttree.generate(this.world, this.rand, pos.up());
					}
				}
			}
		}

		@Override
		public void onDamageTo(EntityLivingBase target, float amount) {
			super.onDamageTo(target, amount);
			Chakra.Pathway chakra = Chakra.pathway(target);
			double d = chakra.getAmount() >= 80d ? 80d : chakra.getAmount();
			if (d > 0d) {
				chakra.consume(d);
				this.collectedChakra += d;
			}
		}

		@Override
		public void onDamagedBy(EntityLivingBase attacker, float amount) {
			super.onDamagedBy(attacker, amount);
			if (this.getDistance(attacker) < 5d) {
				Chakra.Pathway chakra = Chakra.pathway(attacker);
				double d = chakra.getAmount() >= amount ? amount : chakra.getAmount();
				if (d > 0d) {
					chakra.consume(d);
					this.collectedChakra += d;
				}
			}
		}

	    protected boolean isValidLightLevel() {
	        BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
	        if (this.world.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)) {
	            return false;
	        } else {
	            int i = this.world.getLightFromNeighbors(blockpos);
	            if (this.world.isThundering()) {
	                int j = this.world.getSkylightSubtracted();
	                this.world.setSkylightSubtracted(10);
	                i = this.world.getLightFromNeighbors(blockpos);
	                this.world.setSkylightSubtracted(j);
	            }
	            return i <= this.rand.nextInt(8);
	        }
	    }

		@Override
		public boolean getCanSpawnHere() {
			return this.world.getDifficulty() != EnumDifficulty.PEACEFUL
			 && (this.world.getVillageCollection().getNearestVillage(new BlockPos(this), 24) != null || this.isValidLightLevel())
			 && super.getCanSpawnHere();
		}

		@Override
		protected boolean shouldDespawn() {
			return this.world.getDifficulty() == EnumDifficulty.PEACEFUL || this.getHealth() <= 0.0f;
		}

		@Override
		public boolean isOnSameTeam(Entity entityIn) {
			return EntityNinjaMob.TeamAkatsuki.contains(entityIn.getClass());
		}

		@Override
		public double getCollectedNinjaXP() {
			return 0d;
		}

		@Override
		public int getCollectXPpoints() {
			return 0;
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> new RenderCustom(renderManager));
		}
			
		@SideOnly(Side.CLIENT)
		public class RenderCustom extends EntityClone.ClientRLM.RenderClone<EntityCustom> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/zetsu_white_256.png");
			private final ModelZetsuWhite altModel = new ModelZetsuWhite();

			public RenderCustom(RenderManager renderManager) {
				EntityClone.ClientRLM.getInstance().super(renderManager);
			}

			@Override
			public void doRender(EntityCustom entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entityIn.getSummoner() == null) {
					this.mainModel = this.altModel;
				}
				super.doRender(entityIn, x, y, z, entityYaw, partialTicks);
			}
			
			@Override
			public ResourceLocation getEntityTexture(EntityCustom entity) {
				if (entity.getSummoner() == null) {
					return this.texture;
				} else {
					return super.getEntityTexture(entity);
				}
			}
			
			@Override
			protected void preRenderCallback(EntityCustom entityIn, float partialTickTime) {
				if (entityIn.getSummoner() == null) {
					GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
				} else {
					super.preRenderCallback(entityIn, partialTickTime);
				}
			}

		    @Override
		    protected boolean canRenderName(EntityCustom entity) {
		    	EntityLivingBase summoner = entity.getSummoner();
		    	return (summoner instanceof EntityNinjaMerchant.Base || summoner instanceof EntityPlayer) && super.canRenderName(entity);
		    }
		}

		@SideOnly(Side.CLIENT)
		class ModelZetsuWhite extends EntityNinjaMob.ModelNinja {
			private final ModelRenderer hair;
			private final ModelRenderer bone1;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			
			ModelZetsuWhite() {
				textureWidth = 64;
				textureHeight = 64;
		
				bipedHead = new ModelRenderer(this);
				bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
			
				bipedHeadwear = new ModelRenderer(this);
				bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedHeadwear.cubeList.add(new ModelBox(bipedHeadwear, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.4F, false));
			
				hair = new ModelRenderer(this);
				hair.setRotationPoint(0.0F, -1.0F, 0.0F);
				bipedHeadwear.addChild(hair);
					
			
				bone1 = new ModelRenderer(this);
				bone1.setRotationPoint(-2.0F, -5.0F, 0.0F);
				hair.addChild(bone1);
				setRotationAngle(bone1, 0.0F, 0.0F, -0.5236F);
				bone1.cubeList.add(new ModelBox(bone1, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
			
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-2.0F, -4.5F, -2.0F);
				hair.addChild(bone2);
				setRotationAngle(bone2, 0.3491F, 0.0F, -0.3491F);
				bone2.cubeList.add(new ModelBox(bone2, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
			
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(-2.0F, -4.5F, 2.0F);
				hair.addChild(bone3);
				setRotationAngle(bone3, -0.3491F, 0.0F, -0.3491F);
				bone3.cubeList.add(new ModelBox(bone3, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
			
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, -5.0F, -2.0F);
				hair.addChild(bone4);
				setRotationAngle(bone4, 0.5236F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
			
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, -5.0F, 2.0F);
				hair.addChild(bone5);
				setRotationAngle(bone5, -0.5236F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, false));
			
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(2.0F, -5.0F, 0.0F);
				hair.addChild(bone6);
				setRotationAngle(bone6, 0.0F, 0.0F, 0.5236F);
				bone6.cubeList.add(new ModelBox(bone6, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, true));
			
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(2.0F, -4.5F, 2.0F);
				hair.addChild(bone7);
				setRotationAngle(bone7, -0.3491F, 0.0F, 0.3491F);
				bone7.cubeList.add(new ModelBox(bone7, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, true));
			
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(2.0F, -4.5F, -2.0F);
				hair.addChild(bone8);
				setRotationAngle(bone8, 0.3491F, 0.0F, 0.3491F);
				bone8.cubeList.add(new ModelBox(bone8, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.2F, true));
			
				bipedBody = new ModelRenderer(this);
				bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F, false));
				bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.3F, false));
			
				bipedRightArm = new ModelRenderer(this);
				bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, false));
			
				bipedLeftArm = new ModelRenderer(this);
				bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F, true));
			
				bipedRightLeg = new ModelRenderer(this);
				bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, false));
				bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, false));
			
				bipedLeftLeg = new ModelRenderer(this);
				bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F, true));
				bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, true));
			}
		}
	}
}
