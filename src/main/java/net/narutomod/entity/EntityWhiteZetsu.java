
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.init.Biomes;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;

import net.narutomod.procedure.ProcedureWhiteZetsuEntityEntityDies;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemKunai;
import net.narutomod.ModConfig;
import net.narutomod.ElementsNarutomodMod;

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

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			final ModelBiped model = new ModelBiped(0f, 0f, 64, 64) {
				{
					bipedBody.cubeList.add(new ModelBox(bipedBody, 16, 32, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.3F, false));
					bipedRightArm.cubeList.add(new ModelBox(bipedRightArm, 40, 32, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.3F, false));
					bipedLeftArm.cubeList.add(new ModelBox(bipedLeftArm, 40, 32, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.3F, true));
					bipedRightLeg.cubeList.add(new ModelBox(bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, false));
					bipedLeftLeg.cubeList.add(new ModelBox(bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.3F, true));
				}
			};
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				RenderBiped customRender = new RenderBiped<EntityCustom>(renderManager, model, 0.5f) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/zetsu_white_256.png");
					@Override
					public void doRender(EntityCustom entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
						int playerId = entityIn.getPlayerId();
						if (playerId >= 0 && entityIn.getHealth() > 1f) {
							Entity entity = entityIn.world.getEntityByID(playerId);
							if (entity instanceof EntityLiving) {
								Render render = this.renderManager.getEntityRenderObject(entity);
								if (render instanceof RenderBiped) {
									this.mainModel = ((RenderBiped)render).getMainModel();
								}
							}
						}
						super.doRender(entityIn, x, y, z, entityYaw, partialTicks);
						this.mainModel = model;
					}
					@Override
					protected ResourceLocation getEntityTexture(EntityCustom entity) {
						int playerId = entity.getPlayerId();
						if (playerId >= 0 && entity.getHealth() > 1f) {
							Entity player = entity.world.getEntityByID(playerId);
							if (player instanceof AbstractClientPlayer) {
								return ((AbstractClientPlayer) player).getLocationSkin();
							} else if (player instanceof EntityLiving) {
								Render render = this.renderManager.getEntityRenderObject(player);
								if (render instanceof RenderBiped) {
									return ProcedureUtils.invokeMethodByParameters(render, ResourceLocation.class, player);
								}
							}
						}
						return this.texture;
					}
					@Override
					protected void preRenderCallback(EntityCustom entityIn, float partialTickTime) {
						//if (((EntityCustom) entitylivingbaseIn).getPlayerId() >= 0 && entitylivingbaseIn.getHealth() > 1f) {
						//	Entity player = entitylivingbaseIn.world.getEntityByID(((EntityCustom) entitylivingbaseIn).getPlayerId());
						//	if (player instanceof AbstractClientPlayer)
								GlStateManager.scale(0.9375F, 0.9375F, 0.9375F);
						//}
					}
				};
				customRender.addLayer(new net.minecraft.client.renderer.entity.layers.LayerBipedArmor(customRender) {
					protected void initArmor() {
						this.modelLeggings = new ModelBiped(0.5f);
						this.modelArmor = new ModelBiped(1);
					}
				});
				return customRender;
			});
		}
	}

	public static class EntityCustom extends EntityMob {
		private static final DataParameter<Integer> PLAYER_ID = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		
		public EntityCustom(World world) {
			super(world);
			this.setSize(0.6f, 1.8f);
			this.experienceValue = 25;
			this.isImmuneToFire = false;
			this.setNoAI(!true);
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemKunai.block, (int) (1)));
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(PLAYER_ID, Integer.valueOf(-1));
		}

		public int getPlayerId() {
			return ((Integer) this.getDataManager().get(PLAYER_ID)).intValue();
		}

		protected void setPlayerId(int id) {
			this.getDataManager().set(PLAYER_ID, Integer.valueOf(id));
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, (float) 50) {
				@Override
				public void updateTask() {
					super.updateTask();
					World world = this.entity.world;
					if (EntityCustom.this.getPlayerId() < 0 && this.closestEntity instanceof EntityPlayer
					 && !((EntityPlayer)this.closestEntity).isCreative() && world.playerEntities.size() > 1) {
						for (int i = 0; i < 10; i++) {
							EntityPlayer entity1 = world.playerEntities.get(rand.nextInt(world.playerEntities.size()));
							if (!entity1.equals(this.closestEntity)) {
								EntityCustom.this.setPlayerId(entity1.getEntityId());
								this.entity.setCustomNameTag(entity1.getName());
								return;
							}
						}
					}
				}
			});
			this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false, false));
			this.tasks.addTask(3, new EntityAIAttackMelee(this, 1, true));
			this.tasks.addTask(4, new EntityAIWander(this, 0.5));
			this.tasks.addTask(5, new EntityAILookIdle(this));
		}

		@Override
		public EnumCreatureAttribute getCreatureAttribute() {
			return EnumCreatureAttribute.UNDEFINED;
		}

		@Override
		protected Item getDropItem() {
			return null;
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
			int x = (int) this.posX;
			int y = (int) this.posY;
			int z = (int) this.posZ;
			Entity entity = this;
			{
				java.util.HashMap<String, Object> $_dependencies = new java.util.HashMap<>();
				$_dependencies.put("x", x);
				$_dependencies.put("y", y);
				$_dependencies.put("z", z);
				$_dependencies.put("world", world);
				ProcedureWhiteZetsuEntityEntityDies.executeProcedure($_dependencies);
			}
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			if (this.getEntityAttribute(SharedMonsterAttributes.ARMOR) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.5D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
			if (this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null)
				this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25D);
			if (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
				this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5D);
		}
	}
}
