
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
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
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

import net.narutomod.procedure.ProcedureWhiteZetsuEntityEntityDies;
import net.narutomod.item.ItemKunai;
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
		Biome[] spawnBiomes = {Biome.REGISTRY.getObject(new ResourceLocation("extreme_hills")),
				Biome.REGISTRY.getObject(new ResourceLocation("forest")), Biome.REGISTRY.getObject(new ResourceLocation("taiga")),
				Biome.REGISTRY.getObject(new ResourceLocation("swampland")), Biome.REGISTRY.getObject(new ResourceLocation("beaches")),
				Biome.REGISTRY.getObject(new ResourceLocation("desert_hills")), Biome.REGISTRY.getObject(new ResourceLocation("forest_hills")),
				Biome.REGISTRY.getObject(new ResourceLocation("taiga_hills")), Biome.REGISTRY.getObject(new ResourceLocation("jungle")),
				Biome.REGISTRY.getObject(new ResourceLocation("jungle_hills")), Biome.REGISTRY.getObject(new ResourceLocation("jungle_edge")),
				Biome.REGISTRY.getObject(new ResourceLocation("birch_forest")), Biome.REGISTRY.getObject(new ResourceLocation("birch_forest_hills")),
				Biome.REGISTRY.getObject(new ResourceLocation("roofed_forest")), Biome.REGISTRY.getObject(new ResourceLocation("redwood_taiga")),
				Biome.REGISTRY.getObject(new ResourceLocation("redwood_taiga_hills")),
				Biome.REGISTRY.getObject(new ResourceLocation("extreme_hills_with_trees")), Biome.REGISTRY.getObject(new ResourceLocation("savanna")),
				Biome.REGISTRY.getObject(new ResourceLocation("mesa")), Biome.REGISTRY.getObject(new ResourceLocation("mesa_rock")),
				Biome.REGISTRY.getObject(new ResourceLocation("mesa_clear_rock")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_extreme_hills")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_forest")), Biome.REGISTRY.getObject(new ResourceLocation("mutated_taiga")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_swampland")), Biome.REGISTRY.getObject(new ResourceLocation("mutated_jungle")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_jungle_edge")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_birch_forest")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_birch_forest_hills")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_roofed_forest")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_redwood_taiga")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_redwood_taiga_hills")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_extreme_hills_with_trees")),
				Biome.REGISTRY.getObject(new ResourceLocation("mutated_savanna")),};
		EntityRegistry.addSpawn(EntityCustom.class, 10, 1, 1, EnumCreatureType.MONSTER, spawnBiomes);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			RenderBiped customRender = new RenderBiped(renderManager, new ModelBiped(0f, 0f, 64, 64), 0.5f) {
				@Override
				protected ResourceLocation getEntityTexture(Entity entity) {
					int playerId = ((EntityCustom) entity).getPlayerId();
					if (playerId >= 0 && ((EntityLiving) entity).getHealth() > 1f) {
						Entity player = entity.world.getEntityByID(playerId);
						if (player instanceof AbstractClientPlayer)
							return ((AbstractClientPlayer) player).getLocationSkin();
					}
					return new ResourceLocation("narutomod:textures/zetsu_white.png");
				}
				@Override
				protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
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
			// modified 6-27-2020 ------------------------------------------------------------------
			this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, (float) 50) {
				@Override
				public void updateTask() {
					super.updateTask();
					if (EntityCustom.this.getPlayerId() < 0 && this.closestEntity != null && !((EntityPlayer)this.closestEntity).isCreative()) {
						EntityCustom.this.setPlayerId(this.closestEntity.getEntityId());
						this.entity.setCustomNameTag(this.closestEntity.getName());
					}
				}
			}); // ---------------------------------------------------------------------------------
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
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}

		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds) {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.witch.hurt"));
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound() {
			return (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.witch.death"));
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
