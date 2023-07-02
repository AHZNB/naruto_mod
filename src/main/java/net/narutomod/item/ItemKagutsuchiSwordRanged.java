package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;

import net.narutomod.procedure.ProcedureKagutsuchiSwordToolInUseTick;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.procedure.ProcedureSusanoo;
import net.narutomod.potion.PotionAmaterasuFlame;
import net.narutomod.entity.EntitySusanooWinged;
import net.narutomod.block.BlockAmaterasuBlock;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod;

import java.util.HashMap;
import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemKagutsuchiSwordRanged extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:kagutsuchiswordranged")
	public static final Item block = null;
	public static final int ENTITYID = 55;
	public static final int ENTITYID2 = 1055;
	
	public ItemKagutsuchiSwordRanged(ElementsNarutomodMod instance) {
		super(instance, 257);
	}

	public void initElements() {
		this.elements.items.add(() -> new RangedItem());
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityBlackFireball.class)
				.id(new ResourceLocation("narutomod", "entitykagutsuchiswordfireball"), ENTITYID).name("entitykagutsuchiswordfireball")
				.tracker(64, 1, true).build());
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityBigBlackFireball.class)
				.id(new ResourceLocation("narutomod", "entitykagutsuchiswordbigfireball"), ENTITYID2).name("entitykagutsuchiswordbigfireball")
				.tracker(64, 1, true).build());
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:kagutsuchiswordranged", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityBlackFireball.class, renderManager -> new RenderBlackFireball(renderManager, 2.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityBigBlackFireball.class, renderManager -> new RenderBlackFireball(renderManager, 6.0F));
	}
	
	public static class RangedItem extends Item {
		public int ticksUsed;
		
		public RangedItem() {
			this.setMaxDamage(0);
			this.setFull3D();
			this.setUnlocalizedName("kagutsuchiswordranged");
			this.setRegistryName("kagutsuchiswordranged");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
			this.ticksUsed = 0;
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "kagutsuchi.damage", 1.5D, 1));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "kagutsuchi.speed", -2.4D, 0));
			}
			return multimap;
		}

		private boolean isHolderRidingSusanoo(EntityLivingBase entity) {
			return (entity.isRiding() && entity.getRidingEntity() instanceof EntitySusanooWinged.EntityCustom);
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entitylb, int timeLeft) {
			if (!world.isRemote && entitylb instanceof EntityPlayer) {
				EntityPlayer entity = (EntityPlayer) entitylb;
				world.playSound(null, entity.posX, entity.posY, entity.posZ,
						(net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.blaze.shoot")),
						SoundCategory.NEUTRAL, 1.0F, 1.0F);
				Vec3d vec3d = entity.getPositionEyes(1.0F);
				Vec3d[] vec3d1 = new Vec3d[3];
				vec3d1[0] = vec3d.add(entity.getLookVec().scale(20.0D));
				vec3d1[1] = vec3d.add(Vec3d.fromPitchYaw(entity.rotationPitch, entity.rotationYaw - 20.0F).scale(20.0D));
				vec3d1[2] = vec3d.add(Vec3d.fromPitchYaw(entity.rotationPitch, entity.rotationYaw + 20.0F).scale(20.0D));
				if (this.isHolderRidingSusanoo(entity)) {
					Vec3d vec3d2 = vec3d.add(entity.getLookVec().scale(4.0D));
					for (int i = 0; i < 3; i++) {
						EntityBigBlackFireball entityfireball = new EntityBigBlackFireball(world, (EntityLivingBase) entity.getRidingEntity(),
								vec3d1[i].x - vec3d2.x, vec3d1[i].y - vec3d2.y, vec3d1[i].z - vec3d2.z);
						entityfireball.posX = vec3d2.x;
						entityfireball.posY = vec3d2.y;
						entityfireball.posZ = vec3d2.z;
						world.spawnEntity(entityfireball);
					}
				} else {
					for (int i = 0; i < 3; i++) {
						Vec3d vec3d2 = vec3d.add(entity.getLookVec().scale(2.0D));
						EntityBlackFireball entityfireball = new EntityBlackFireball(world, entity, vec3d1[i].x - vec3d2.x, vec3d1[i].y - vec3d2.y,
								vec3d1[i].z - vec3d2.z);
						entityfireball.posX = vec3d2.x;
						entityfireball.posY = vec3d2.y;
						entityfireball.posZ = vec3d2.z;
						world.spawnEntity(entityfireball);
					}
				}
				if (!entity.isCreative())
					entity.getCooldownTracker().setCooldown(itemstack.getItem(), 200);
			}
		}

		@Override
		public boolean hitEntity(ItemStack itemstack, EntityLivingBase target, EntityLivingBase attacker) {
			super.hitEntity(itemstack, target, attacker);
			EntityPlayer holder = (attacker instanceof EntityPlayer) ? (EntityPlayer) attacker : null;
			target.addPotionEffect(new PotionEffect(PotionAmaterasuFlame.potion, 10000, (holder != null) ? (holder.experienceLevel / 30 + 1) : 0));
			return true;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			this.ticksUsed++;
			if (entity instanceof EntityLivingBase) {
				if (((EntityLivingBase) entity).getHeldItemMainhand().equals(itemstack)) {
					HashMap<String, Object> $_dependencies = new HashMap<>();
					$_dependencies.put("entity", entity);
					$_dependencies.put("itemstack", itemstack);
					$_dependencies.put("world", world);
					ProcedureKagutsuchiSwordToolInUseTick.executeProcedure($_dependencies);
				}
			}
			int susanooId = ProcedureSusanoo.getSummonedSusanooId(entity);
			if (!world.isRemote && this.ticksUsed > 5 && entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative()
			 && (susanooId <= 0 || !(world.getEntityByID(susanooId) instanceof EntitySusanooWinged.EntityCustom))) {
				itemstack.shrink(1);
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			entity.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, entity.getHeldItem(hand));
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BOW;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}
	}

	public static class EntityBlackFireball extends EntityFireball {
		public EntityBlackFireball(World a) {
			super(a);
			this.setSize(1.2F, 1.2F);
		}

		public EntityBlackFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
			super(worldIn, x, y, z, accelX, accelY, accelZ);
			this.setSize(1.2F, 1.2F);
		}

		public EntityBlackFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
			super(worldIn, shooter, accelX, accelY, accelZ);
			this.setSize(1.2F, 1.2F);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.isDead)
				for (int i = 0; i < 2; i++)
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + ((this.rand.nextFloat() - 0.5F) * this.width),
							this.posY + (this.rand.nextFloat() * this.height), this.posZ + ((this.rand.nextFloat() - 0.5F) * this.width), 0.0D, 0.1D,
							0.0D, new int[0]);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntityBlackFireball))
				return;
			if (!this.world.isRemote) {
				for (int j = -this.rand.nextInt(3); j <= this.rand.nextInt(3); j++) {
					for (int i = -this.rand.nextInt(3); i <= this.rand.nextInt(3); i++) {
						for (int k = -this.rand.nextInt(3); k <= this.rand.nextInt(3); k++) {
							BlockAmaterasuBlock.placeBlock(this.world, new BlockPos((int)this.posX + i, (int)this.posY + j, (int)this.posZ + k),
							 this.shootingEntity instanceof EntityPlayer ? (int)(PlayerTracker.getNinjaLevel((EntityPlayer)this.shootingEntity) / 30) : 2);
						}
					}
				}
				ProcedureAoeCommand.set(this.world, this.posX, this.posY, this.posZ, 0.0D, 3.0D).effect(PotionAmaterasuFlame.potion, 1000,
						((this.shootingEntity instanceof EntityPlayer) ? ((EntityPlayer) this.shootingEntity).experienceLevel : 0) / 30 + 1);
				this.setDead();
			}
		}

		@Override
		protected boolean isFireballFiery() {
			return false;
		}
	}

	public static class EntityBigBlackFireball extends EntityFireball {
		public EntityBigBlackFireball(World a) {
			super(a);
			this.setSize(3.6F, 3.6F);
		}

		public EntityBigBlackFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
			super(worldIn, x, y, z, accelX, accelY, accelZ);
			this.setSize(3.6F, 3.6F);
		}

		public EntityBigBlackFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
			super(worldIn, shooter, accelX, accelY, accelZ);
			this.setSize(3.6F, 3.6F);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.isDead)
				for (int i = 0; i < 5; i++)
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + ((this.rand.nextFloat() - 0.5F) * this.width),
							this.posY + (this.rand.nextFloat() * this.height), this.posZ + ((this.rand.nextFloat() - 0.5F) * this.width), 0.0D, 0.1D,
							0.0D, new int[0]);
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntityBigBlackFireball))
				return;
			if (!this.world.isRemote) {
				for (int j = -this.rand.nextInt(4); j <= this.rand.nextInt(4); j++) {
					for (int i = -this.rand.nextInt(4); i <= this.rand.nextInt(4); i++) {
						for (int k = -this.rand.nextInt(4); k <= this.rand.nextInt(4); k++) {
							BlockAmaterasuBlock.placeBlock(this.world, new BlockPos((int)this.posX + i,
							 (int)this.posY + j, (int)this.posZ + k), 15);
						}
					}
				}
				EntityPlayer player = this.shootingEntity instanceof EntitySusanooWinged.EntityCustom
						&& this.shootingEntity.getControllingPassenger() instanceof EntityPlayer
								? (EntityPlayer) this.shootingEntity.getControllingPassenger()
								: null;
				ProcedureAoeCommand.set(this.world, this.posX, this.posY, this.posZ, 0.0D, 4.0D)
						.effect(PotionAmaterasuFlame.potion, 1000, ((player != null) ? player.experienceLevel : 0) / 30 + 1)
						.damageEntities(this.shootingEntity, 100.0F);
				this.setDead();
			}
		}

		@Override
		protected boolean isFireballFiery() {
			return false;
		}
	}

	public static class RenderBlackFireball extends Render<EntityFireball> {
		protected float scale;

		public RenderBlackFireball(RenderManager renderManagerIn, float scaleIn) {
			super(renderManagerIn);
			this.scale = scaleIn;
		}

		@Override
		public void doRender(EntityFireball entity, double x, double y, double z, float entityYaw, float partialTicks) {
			GlStateManager.pushMatrix();
			this.bindEntityTexture(entity);
			GlStateManager.translate((float) x, (float) y, (float) z);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(this.scale, this.scale, this.scale);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F,
					0.0F);
			if (this.renderOutlines) {
				GlStateManager.enableColorMaterial();
				GlStateManager.enableOutlineMode(this.getTeamColor(entity));
			}
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
			bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
			tessellator.draw();
			if (this.renderOutlines) {
				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();
			}
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityFireball entity) {
			return new ResourceLocation("narutomod:textures/black_fireball.png");
		}
	}
}
