package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.EntityLiving;

import net.narutomod.procedure.*;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityBeamBase;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.PlayerRender;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Multimap;
import java.util.UUID;
import java.util.Random;
import java.util.List;
import net.minecraft.util.EntityDamageSource;

@ElementsNarutomodMod.ModElement.Tag
public class ItemEightGates extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:eightgates")
	public static final Item block = null;
	public static final int ENTITYID = 67;
	public static final int ENTITYID2 = 1067;
	public static final int ENTITYID3 = 1167;
	private static final ResourceLocation HIRUDORA_TEXTURE = new ResourceLocation("narutomod:textures/WhiteTiger.png");
	private static final ResourceLocation SEKIZO_TEXTURE = new ResourceLocation("narutomod:textures/longcube_white.png");
	private static final ResourceLocation NGDRAGON_TEXTURE = new ResourceLocation("narutomod:textures/dragon_red.png");
	private static final int NGD_SUSPEND_TIME = 20;
	private static Random rng = new Random();
	
	public ItemEightGates(ElementsNarutomodMod instance) {
		super(instance, 282);
	}

	public void initElements() {
		this.elements.items.add(() -> new RangedItem());
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntitySekizo.class)
				.id(new ResourceLocation("narutomod", "entitysekizo"), ENTITYID).name("entitysekizo").tracker(64, 1, true).build());
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityNGDragon.class)
				.id(new ResourceLocation("narutomod", "entityngdragon"), ENTITYID2).name("entityngdragon").tracker(96, 1, true).build());
		this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityHirudora.class)
				.id(new ResourceLocation("narutomod", "entityhirudora"), ENTITYID3).name("entityhirudora").tracker(64, 1, true).build());
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:eightgates", "inventory"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new RangedItem.AttackHook());
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.OFF_HAND);
	}
	
	public static class Properties {
		final int gate;
		final String name;
		final int xpRequired;
		final int particles;
		final int particleColor;
		final int strength;
		final int speed;
		final int resistance;
		final int health;
		final float damage;
		final boolean canFly;
		
		protected Properties(int gt, String nm, int xp, int pt, int col, int str, int spd, int res, int hth, float dmg, boolean fly) {
			if (gt >= 0 && gt <= 8) {
				this.gate = gt;
				this.name = nm;
				this.xpRequired = xp;
				this.particles = pt;
				this.particleColor = col;
				this.strength = str;
				this.speed = spd;
				this.resistance = res;
				this.health = hth;
				this.damage = dmg;
				this.canFly = fly;
			} else {
				throw new IllegalArgumentException("Eight gates dude! Can't be negative or greater than 8.");
			}
		}

		public void activate(EntityLivingBase entity) {
			if (this.gate >= 1 && this.gate <= 8 && !entity.world.isRemote) {
				if (this.particles > 0) {
					Particles.spawnParticle(entity.world, Particles.Types.SMOKE, entity.posX, entity.posY + 0.8d, entity.posZ, 
					 this.particles, 0.2d, 0.4d, 0.2d, 0d, 0.1d, 0d, this.particleColor, 40, 5, 0xF0, entity.getEntityId());
				}
				entity.fallDistance = 0;
				entity.removePotionEffect(MobEffects.SATURATION);
				if (entity.ticksExisted % 10 == 0) {
					entity.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 42, 3, false, false));
					entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 12, 8, false, false));
					entity.addPotionEffect(new PotionEffect(MobEffects.HASTE, 12, 3, false, false));
					entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 12, this.strength, false, false));
					entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 12, this.resistance, false, false));
					entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 12, this.speed, false, false));
					if (entity.getHealth() > 0.0f && (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isCreative())) {
						if (this.damage >= 0.0f) {
							if (entity.ticksExisted % 80 == 0) {
								entity.hurtResistantTime = 10;
								entity.attackEntityFrom(ProcedureUtils.SPECIAL_DAMAGE, this.damage * 8);
							}
						} else {
							entity.setHealth(entity.getHealth() - this.damage);
						}
					}
				}
				if (this.canFly && entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.allowFlying) {
					((EntityPlayer) entity).capabilities.allowFlying = true;
					((EntityPlayer) entity).sendPlayerAbilities();
				}
			}
		}

		public void deActivate(EntityLivingBase entity) {
			if (!entity.world.isRemote && entity instanceof EntityPlayer) {
				PlayerRender.setColorMultiplier((EntityPlayer)entity, 0);
			}
			if (!entity.world.isRemote && this.gate > 1 && (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isCreative())) {
				if (this.gate == 8) {
					ProcedureUtils.setDeathAnimations(entity, 2, 200);
				}
				entity.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 400, 2, false, false));
				entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, this.gate * 600, (this.gate - 2) * 2));
				entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.gate * 600, this.gate - 2));
				if (this.canFly && entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					player.capabilities.allowFlying = false;
					player.capabilities.isFlying = false;
					player.sendPlayerAbilities();
				}
				//float f = entity.getMaxHealth();
				//if (f > 0.0f && entity.getHealth() > f) {
				//	entity.setHealth(f);
				//}
			}
		}
	}

	public static void logBattleXP(EntityPlayer player) {
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.getItem() != block) {
			stack = player.getHeldItemOffhand();
		}
		if (stack.getItem() == block && ((RangedItem)block).getMaxOpenableGate(stack) < 1.0f) {
			((RangedItem)block).addBattleXP(stack, 1);
		}
	}

	public static void addBattleXP(EntityPlayer player, int add) {
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.getItem() != block) {
			stack = player.getHeldItemOffhand();
		}
		if (stack.getItem() == block) {
			((RangedItem)block).addBattleXP(stack, add);
		}
	}

	private static void closeGates(EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		if (stack.getItem() != block) {
			stack = entity.getHeldItemOffhand();
		}
		if (stack.getItem() == block) {
			((RangedItem)stack.getItem()).closeGates(stack, entity);
		}
	}

	public static int getGatesOpened(EntityLivingBase entity) {
		ItemStack stack = entity.getHeldItemMainhand();
		if (stack.getItem() != block) {
			stack = entity.getHeldItemOffhand();
		}
		if (stack.getItem() == block) {
			return (int)((RangedItem)stack.getItem()).getGateOpened(stack);
		}
		return 0;
	}

	public static class RangedItem extends Item {
		private final UUID GATE_MODIFIER = UUID.fromString("f6944d0f-5c81-45db-9261-6a9ad9fe4840");
		private static final String GATE_KEY = "gateOpened";
		private static final String SEKIZO_KEY = "sekizoPunchCount";
		private static final String OWNER_KEY = "ownerUUID";
		private static final String XP_KEY = "battleExperience";
		private final Properties GATE[] = {new Properties(0, "", 0, 0, 0, 0, 0, 0, 0, 0f, false),
			new Properties(1, I18n.translateToLocal("chattext.eightgates.gate1"), 220, 0, 0, 3, 2, 0, 10, -1f, false),
			new Properties(2, I18n.translateToLocal("chattext.eightgates.gate2"), 240, 0, 0, 4, 16, 0, 40, -5f, false),
			new Properties(3, I18n.translateToLocal("chattext.eightgates.gate3"), 280, 20, 0x10FFFFFF, 5, 32, 1, 60, -3f, false),
			new Properties(4, I18n.translateToLocal("chattext.eightgates.gate4"), 360, 25, 0x18FFFFFF, 7, 64, 2, 60, 0.4f, false),
			new Properties(5, I18n.translateToLocal("chattext.eightgates.gate5"), 520, 30, 0x20FFFFFF, 15, 68, 2, 60, 0.6f, false),
			new Properties(6, I18n.translateToLocal("chattext.eightgates.gate6"), 840, 30, 0x3000FF00, 31, 72, 3, 60, 0.8f, false),
			new Properties(7, I18n.translateToLocal("chattext.eightgates.gate7"), 1480, 30, 0x300000FF, 84, 76, 3, 60, 1.0f, false),
			new Properties(8, I18n.translateToLocal("chattext.eightgates.gate8"), 2760, 30, 0x30FF0000, 349, 80, 4, 60, 1.2f, true)};
						
		public RangedItem() {
			this.setMaxDamage(0);
			this.setFull3D();
			this.setUnlocalizedName("eightgates");
			this.setRegistryName("eightgates");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		private int getUseCount(ItemStack stack, int timeLeft) {
			return this.getMaxItemUseDuration(stack) - timeLeft;
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayer && !entityLivingBase.isSneaking()) {
				EntityPlayer entity = (EntityPlayer) entityLivingBase;
				switch ((int) this.getGateOpened(itemstack)) {
					case 8 :
						Entity bullet = new EntityNGDragon(entity);
						//((EntityNGDragon) bullet).shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, 1.2F, 0.0F);
						world.playSound(null, entity.posX, entity.posY, entity.posZ,
						  SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:yagai")),
						  SoundCategory.NEUTRAL, 2.0F, 1.0F);
						world.spawnEntity(bullet);
						if (!entity.isCreative()) {
							//ProcedureUtils.setDeathAnimations(entity, 2, 200);
							entity.getCooldownTracker().setCooldown(itemstack.getItem(), 200);
						}
						entity.sendStatusMessage(new TextComponentString(I18n.translateToLocal("entity.entityngdragon.name")), true);
						break;
					case 7 :
						this.attackHirudora(entity);
						entity.sendStatusMessage(new TextComponentString(I18n.translateToLocal("entity.entityhirudora.name")), true);
						if (!entity.isCreative()) {
							entity.getCooldownTracker().setCooldown(itemstack.getItem(), 400);
						}
						break;
				}
			}
		}

		public void attackHirudora(EntityLivingBase attacker) {
			EntityHirudora bullet = new EntityHirudora(attacker);
			//bullet.shoot(x, y, z, 1.2F, 0.0F);
			attacker.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ,
			 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hirudora")),
			 SoundCategory.NEUTRAL, 2.0F, 1.0F);
			attacker.world.spawnEntity(bullet);
		}

		private int getSekizoPunchNum(ItemStack stack, int tick) {
			if (!stack.hasTagCompound()) 
				stack.setTagCompound(new NBTTagCompound());
			int punch = stack.getTagCompound().getInteger(SEKIZO_KEY);
			//int lastTick = stack.getTagCompound().getInteger(SEKIZO_KEY+"_lastTick");
			//int i = tick - lastTick;
			//punch = (i <= 20 && i > 0 && punch < 4) ? punch + 1 : 0;
			//stack.getTagCompound().setInteger(SEKIZO_KEY, punch);
			//stack.getTagCompound().setInteger(SEKIZO_KEY+"_lastTick", tick);
			int i = punch >= 0 && punch < 4 ? punch + 1 : -1;
			stack.getTagCompound().setInteger(SEKIZO_KEY, i);
			return punch;
		}

		public int attackSekizo(ItemStack itemstack, EntityLivingBase attacker) {
			World world = attacker.world;
			int punchnum = this.getSekizoPunchNum(itemstack, attacker.ticksExisted);
			if (punchnum >= 0) {
				EntitySekizo bullet = new EntitySekizo(attacker);
				bullet.shoot(30, (float) ProcedureUtils.getModifiedAttackDamage(attacker) * 1.0F * (float) Math.pow(2d, punchnum));
				world.playSound(null, attacker.posX, attacker.posY, attacker.posZ,
				 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:sekizo")),
				 SoundCategory.NEUTRAL, 2.0F, 1.0F);
				world.spawnEntity(bullet);
				//this.setSekizoPunchNum(itemstack, attacker.ticksExisted);
			}
			return punchnum;
		}

		public void attackAsakujaku(EntityLivingBase attacker) {
			Vec3d vec3d1 = attacker.getLookVec();
			this.attackAsakujaku(attacker, vec3d1.x, vec3d1.y, vec3d1.z);
		}

		public void attackAsakujaku(EntityLivingBase attacker, double x, double y, double z) {
			World world = attacker.world;
			Vec3d vec3d = attacker.getPositionEyes(1.0F);
			for (int i = 0; i < 10; i++) {
				Entity bullet = new EntitySmallFireball(world, attacker, x, y, z) {
					@Override
					public void onUpdate() {
						super.onUpdate();
						if (this.ticksExisted > 12) {
							this.setDead();
						}
					}
					@Override
					public void onImpact(RayTraceResult result) {
						if (!this.world.isRemote) {
							if (result.entityHit != null) {
								if (result.entityHit.equals(this.shootingEntity) || result.entityHit instanceof EntitySmallFireball) {
									return;
								}
								result.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity),
								 (float)ProcedureUtils.getModifiedAttackDamage(attacker) * 0.5f);
								result.entityHit.setFire(10);
							}
							boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
							this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, 2, false, flag);
							this.setDead();
						}
					}
					@Override
					protected float getMotionFactor() {
						return 1.1f;
					}
				};
				bullet.setPosition(vec3d.x, vec3d.y, vec3d.z);
				world.spawnEntity(bullet);
			}
		}

		@Override
		public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
			if (!attacker.world.isRemote) {
				int gateOpened = (int)this.getGateOpened(itemstack);
				switch (gateOpened) {
					case 6 :
						this.attackAsakujaku(attacker);
						attacker.sendStatusMessage(new TextComponentString(
							I18n.translateToLocal("entity.entityasakujaku.name")), true);
						break;
					case 8 :
						int k = this.attackSekizo(itemstack, attacker);
						if (k >= 0) {
							attacker.sendStatusMessage(new TextComponentString(
							 I18n.translateToLocalFormatted("entity.entitysekizo.name", k+1)), true);
							break;
						}
					case 7:
						if (attacker.equals(target)) {
							target = ProcedureUtils.objectEntityLookingAt(attacker, 18d + 5d * (gateOpened - 7), 3d).entityHit;
							if (target instanceof EntityLivingBase) {
								Vec3d vec = target.getPositionVector().subtract(attacker.getPositionVector()).normalize();
								attacker.rotationYaw = ProcedureUtils.getYawFromVec(vec);
								attacker.rotationPitch = ProcedureUtils.getPitchFromVec(vec);
								attacker.setPositionAndUpdate(target.posX - vec.x, target.posY - vec.y + 0.5d, target.posZ - vec.z);
								attacker.attackTargetEntityWithCurrentItem(target);
							} else {
								return true;
							}
						}
				}
			}
			return super.onLeftClickEntity(itemstack, attacker, target);
		}

		public static class AttackHook {
			@SubscribeEvent
			public void onLivingAttack(LivingAttackEvent event) {
				if (event.getSource().getTrueSource() instanceof EntityLivingBase
				 && event.getSource().getTrueSource() == event.getSource().getImmediateSource()) {
					EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
					int gateOpened = getGatesOpened(attacker);
					EntityLivingBase target = event.getEntityLiving();
					if (gateOpened >= 5) {
						if (gateOpened >= 7) {
							target.world.playSound(null, target.posX, target.posY, target.posZ,
							 SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 1.0F, target.getRNG().nextFloat() * 0.5F + 0.5F);
						}
						Vec3d vec = attacker.getPositionVector().subtract(target.getPositionVector()).normalize();
						for (int i = 1, j = 25; i <= j; i++) {
							Vec3d vec1 = vec.scale(0.06d * i);
							Particles.spawnParticle(attacker.world, Particles.Types.SONIC_BOOM, target.posX, target.posY+1.4d, target.posZ,
							 1, 0d, 0d, 0d, vec1.x, vec1.y, vec1.z, 0x00ffffff | ((int)((1f-(float)i/j)*0x40)<<24),
							 i * 2, (int)(5f * (1f + ((float)i/j) * 0.5f)));
						}
					}
					if (gateOpened >= 2) {
						ProcedureUtils.pushEntity(attacker, target, 10,
						 0.2f * gateOpened + (!(attacker instanceof EntityPlayer) && !(target instanceof EntityPlayer) ? 1.0f : 2.0f));
					}
				}
			}

			@SubscribeEvent
			public void onDeath(LivingDeathEvent event) {
				ItemEightGates.closeGates(event.getEntityLiving());
				//ProcedureUtils.clearDeathAnimations(entity);
			}
		}

		//private float getMaxOpenableGate(EntityLivingBase entity) {
		//	return entity instanceof EntityPlayer ? Math.min((float) ((EntityPlayer) entity).experienceLevel / 10, 8f) : 7f;
		//}

		private float getMaxOpenableGate(ItemStack stack) {
			/*EntityLivingBase owner = this.getOwner(stack);
			if (owner == null) {
				return 0f;
			}
			if (!(owner instanceof EntityPlayer)) {
				return 7f;
			} else if (((EntityPlayer)owner).isCreative()) {
				return 8f;
			}*/
			int xp = this.getBattleXP(stack);
			for (int i = 8; i > 0; i--) {
				if (xp >= this.GATE[i].xpRequired) {
					return (float)i;
				}
			}
			return 0f;
		}

		/*private float getMaxOpenableGate(ItemStack stack, EntityLivingBase entity) {
			if (!(entity instanceof EntityPlayer)) {
				return 7f;
			}
			return this.getMaxOpenableGate(stack);
		}*/

		public float getGateOpened(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getFloat(GATE_KEY) : 0f;
		}

		private void setGateOpened(ItemStack stack, EntityLivingBase entity, float gate) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			gate = MathHelper.clamp(gate, 0f, 
			 entity instanceof EntityPlayer ? ((EntityPlayer)entity).isCreative() ? 8f : this.getMaxOpenableGate(stack) : 7f);
			stack.getTagCompound().setFloat(GATE_KEY, gate);
		}

		private void setBattleXp(ItemStack stack, int xp) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setInteger(XP_KEY, xp);
		}

		private void addBattleXP(ItemStack stack, int add) {
			this.setBattleXp(stack, this.getBattleXP(stack) + add);
		}

		private int getBattleXP(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getInteger(XP_KEY) : 0;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
			if (player.isSneaking()) {
				float increments = 0.05f;
				float gateOpened = this.getGateOpened(stack);
				if (gateOpened >= 4f) {
					for (int i = 0; i < (int) gateOpened * 10; i++) {
						Particles.spawnParticle(player.world, Particles.Types.SMOKE, player.posX, player.posY, player.posZ,
						 1, 1d, 0d, 1d, (itemRand.nextDouble()-0.5d) * 2.0d, 0.5d,
						 (itemRand.nextDouble()-0.5d) * 2.0d, 0x10FFFFFF, 30, 0);
					}
					if (gateOpened < 4f + increments) {
						player.world.playSound(null, player.posX, player.posY, player.posZ,
						 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:opengate")),
						 SoundCategory.NEUTRAL, 1, 1);
					}
					if (gateOpened >= 6f) {
						ProcedureAoeCommand.set(player, 0d, gateOpened * 2f).exclude(player).knockback(2f);
						for (Entity entity2 : ProcedureAoeCommand.getInstance().getEntitiesList()) {
							if (entity2 instanceof ItemJutsu.IJutsu && !(entity2 instanceof EntityLivingBase)) {
								entity2.setDead();
							}
						}
						ProcedureUtils.purgeHarmfulEffects(player);
						player.extinguish();
					}
					if (gateOpened >= 8f - increments && gateOpened < 8f) {
						player.world.playSound(null, player.posX, player.posY, player.posZ, 
						 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:eightgatesrelease")),
						 SoundCategory.NEUTRAL, 2f, 1f);
					}
					if (gateOpened >= 4f + increments && player.ticksExisted % 10 == 0) {
						player.world.playSound(null, player.posX, player.posY, player.posZ, 
						 SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:explosion")),
						 SoundCategory.NEUTRAL, 0.1f, 0.9f - this.itemRand.nextFloat() * 0.3f);
					}
				}
				if (player instanceof EntityPlayer) {
					if (gateOpened >= 3f && PlayerRender.getColorMultiplier((EntityPlayer)player) == 0) {
						PlayerRender.setColorMultiplier((EntityPlayer)player, 0xB0B00000);
					}
					((EntityPlayer) player).sendStatusMessage(new TextComponentString(this.GATE[(int) gateOpened].name), true);
				}
				//if (gateOpened < 8.0f) {
				//	player.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 4d);
				//}
				this.setGateOpened(stack, player, gateOpened + increments);
			}
		}

		public EntityLivingBase getOwner(ItemStack stack) {
			UUID id = ProcedureUtils.getOwnerId(stack);
			return id == null ? null : ProcedureUtils.searchLivingMatchingId(id);
		}

		protected void setOwner(ItemStack stack, EntityLivingBase owner) {
			ProcedureUtils.setOriginalOwner(owner, stack);
			stack.setStackDisplayName(stack.getDisplayName() + " (" + owner.getName() + ")");
		}

		private boolean isOwner(ItemStack stack, EntityLivingBase entity) {
			if (ProcedureUtils.getOwnerId(stack) == null) {
				this.setOwner(stack, entity);
			}
			return ProcedureUtils.isOriginalOwner(entity, stack) || (entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative());
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (/* !world.isRemote && */ entity instanceof EntityLivingBase) {
				EntityLivingBase player = (EntityLivingBase) entity;
				if (!this.isOwner(itemstack, player)) {
					//itemstack.shrink(1);
					return;
				}
				float gateOpened = this.getGateOpened(itemstack);
				if (player.getHeldItemMainhand().equals(itemstack) || player.getHeldItemOffhand().equals(itemstack)) {
					this.GATE[(int) gateOpened].activate(player);
					if (gateOpened >= 1f && gateOpened >= this.getMaxOpenableGate(itemstack) && entity.ticksExisted % 40 == 8) {
						this.addBattleXP(itemstack, 1);
					}
				} else {
					this.closeGates(itemstack, player);
				}
			}
		}

		private void closeGates(ItemStack itemstack, EntityLivingBase player) {
			float gateOpened = this.getGateOpened(itemstack);
			if (gateOpened > 0f) {
				this.setGateOpened(itemstack, player, 0);
				this.GATE[(int) gateOpened].deActivate(player);
				itemstack.getTagCompound().removeTag(SEKIZO_KEY);
				if (player instanceof EntityPlayer && !((EntityPlayer) player).isCreative()) {
					((EntityPlayer) player).getCooldownTracker().setCooldown(itemstack.getItem(), (int) gateOpened * 200);
				}
			}
		}
		
		private static int inc = 0;
		private String printAttributeModifiers(ItemStack stack) {
			++inc;
			EntityLivingBase owner = this.getOwner(stack);
			if (owner != null) {
				IAttributeInstance iattributeinstance = owner.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
				if (iattributeinstance != null) {
					if (!iattributeinstance.getModifiers().isEmpty())
						for (AttributeModifier attributemodifier : iattributeinstance.getModifiers())
							return (inc + "-" + attributemodifier.toString());
					else
						return (inc + "-empty");
				} else
					return (inc + "-null");
			}
			return "";
		}

		@Override
		public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
			Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
			int gateOpened = (int) this.getGateOpened(stack);
			if ((slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND) && gateOpened > 0) {
				double health = (double) this.GATE[gateOpened].health;
				multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(GATE_MODIFIER, "8gates.maxhealth", health, 0));
			}
			return multimap;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(I18n.translateToLocal("tooltip.eightgates.opengates"));
			int max = (int)this.getMaxOpenableGate(itemstack);
			for (int i = 1; i <= 8; i++) {
				list.add((i <= max ? TextFormatting.GRAY : TextFormatting.DARK_GRAY) 
				 + this.GATE[i].name + " " + TextFormatting.ITALIC 
				 + I18n.translateToLocalFormatted("tooltip.eightgates.requiredxp", this.GATE[i].xpRequired) + TextFormatting.RESET);
			}
			list.add(TextFormatting.GREEN + I18n.translateToLocalFormatted("tooltip.eightgates.currentxp", this.getBattleXP(itemstack)) + TextFormatting.WHITE);
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ItemStack stack = entity.getHeldItem(hand);
			if (this.isOwner(stack, entity)) {
				entity.setActiveHand(hand);
				return new ActionResult(EnumActionResult.SUCCESS, stack);
			}
			return new ActionResult(EnumActionResult.FAIL, stack);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BOW;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}

		@Override
		public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
			return false;
		}
	}

	public static class EntitySekizo extends EntityBeamBase.Base {
		private AirPunch airPunch = new AirPunch();
		private double range;
		public float damage;
		
		public EntitySekizo(World a) {
			super(a);
		}

		public EntitySekizo(EntityLivingBase shooter) {
			super(shooter);
		}

		public void shoot(double range, float dmg) {
			super.shoot(range);
			this.range = range;
			this.damage = dmg;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksAlive == 10 && !this.world.isRemote) {
				this.airPunch.execute(this.shootingEntity, this.range, 5d);
			}
			if (this.ticksAlive > 60) {
				this.setDead();
			}
		}
		
		protected class AirPunch extends ProcedureAirPunch {
			@Override
			protected void attackEntityFrom(Entity player, Entity target) {
				super.attackEntityFrom(player, target);
				DamageSource ds = player instanceof EntityPlayer ? DamageSource.causePlayerDamage((EntityPlayer)player)
				 : player instanceof EntityLivingBase ? DamageSource.causeMobDamage((EntityLivingBase)player)
				 : new EntityDamageSource(player.getName(), player);
				target.attackEntityFrom(ds.setDamageBypassesArmor(), EntitySekizo.this.damage);
			}

			@Override
			protected float getBreakChance(BlockPos pos, Entity player, double range) {
				this.blockDropChance = 0.2F;
				return 1.0F;
			}
		}
	}

	public static class EntityHirudora extends EntityScalableProjectile.Base {
		private float fullScale = 6f;
		private final float damageMultiplier = 3.0f;
		//private float realMotionFactor;
		
		public EntityHirudora(World a) {
			super(a);
			this.setOGSize(1.0F, 0.5F);
		}

		public EntityHirudora(EntityLivingBase shooter) {
			super(shooter);
			this.setOGSize(1.0F, 0.5F);
			this.setWaitPosition();
		}

		private void setWaitPosition() {
			//Vec3d vec = this.shootingEntity.getLookVec().add(this.shootingEntity.getPositionVector());
			Vec3d vec = this.shootingEntity.getPositionVector().addVector(0d, 0.5d, 0d);
			float yaw = this.shootingEntity.rotationYawHead;
			float pitch = this.shootingEntity.rotationPitch;
			if (this.shootingEntity instanceof EntityLiving && ((EntityLiving)this.shootingEntity).getAttackTarget() != null) {
				 ProcedureUtils.Vec2f vec1 = ProcedureUtils.getYawPitchFromVec(((EntityLiving)this.shootingEntity)
				  .getAttackTarget().getPositionVector().subtract(vec));
				 yaw = vec1.x;
				 pitch = vec1.y;
			}
			this.setLocationAndAngles(vec.x, vec.y, vec.z, yaw, pitch);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null) {
				if (this.ticksAlive <= NGD_SUSPEND_TIME) {
					this.setWaitPosition();
					this.setEntityScale(1.0F + (this.fullScale - 1f) * this.ticksAlive / (float) NGD_SUSPEND_TIME);
				} else {
					if (!this.isLaunched()) {
						Vec3d vec = this.shootingEntity instanceof EntityLiving && ((EntityLiving)this.shootingEntity).getAttackTarget() != null
						 ? ((EntityLiving)this.shootingEntity).getAttackTarget().getPositionVector().subtract(this.getPositionVector())
						 : this.shootingEntity.getLookVec();
						this.shoot(vec.x, vec.y, vec.z, 1.2f, 0f);
					} else {
						this.setEntityScale(this.getEntityScale() * 1.05f);
					}
					if (this.ticksInAir % 10 == 0) {
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:roar")),
						 2f, this.rand.nextFloat() * 0.4f + 0.7f);
					}
				}
			}
			if (!this.world.isRemote && (this.ticksInAir > 30 || this.shootingEntity == null || !this.shootingEntity.isEntityAlive()))
				this.setDead();
		}

		@Override
		public void renderParticles() {
			for (int i = 0; i < 100; i++) {
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY + this.height * 0.5F, this.posZ, 
				 1, 0.0F, this.height * 0.5F, 0.0F, (this.rand.nextDouble() - 0.5D) * 1.5D,
				 (this.rand.nextDouble() - 0.5F) * 1.5D, (this.rand.nextDouble() - 0.5F) * 1.5D, 0x20FFFFFF, 30 + this.rand.nextInt(20), 0);
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && result.entityHit.equals(this.shootingEntity))
				return;
			if (result.typeOfHit == RayTraceResult.Type.BLOCK && this.ticksInAir <= 15) {
				return;
			}
			if (!this.world.isRemote && this.shootingEntity != null) {
				ProcedureAoeCommand.set(this, 0.0D, 0.5d * this.getEntityScale()).exclude(this.shootingEntity)
				 .damageEntities(DamageSource.causeIndirectDamage(this, this.shootingEntity).setDamageBypassesArmor(),
				 (float) ProcedureUtils.getModifiedAttackDamage(this.shootingEntity) * this.damageMultiplier);
				this.shootingEntity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 40d);
				this.world.newExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, 70.0F, false,
						ForgeEventFactory.getMobGriefingEvent(this.world, (EntityLivingBase) this.shootingEntity));
				this.setDead();
			}
		}

		@Override
		protected void checkOnGround() {
		}
	}

	public static class EntityNGDragon extends EntityScalableProjectile.Base {
		private final float fullScale = 6f;
		public float prevLimbSwingAmount;
		public float limbSwingAmount;
		public float limbSwing;

		public EntityNGDragon(World a) {
			super(a);
			this.setOGSize(1.0F, 1.0F);
		}

		public EntityNGDragon(EntityLivingBase shooter) {
			super(shooter);
			this.setOGSize(1.0F, 1.0F);
			this.setWaitPosition();
		}

		private void setWaitPosition() {
			this.setLocationAndAngles(this.shootingEntity.posX, this.shootingEntity.posY, this.shootingEntity.posZ, 
			 this.shootingEntity.rotationYaw, this.shootingEntity.rotationPitch);
		}

		@Override
		public void onKillCommand() {
		}

		private void updateLimbSwing() {
			this.prevLimbSwingAmount = this.limbSwingAmount;
	        double d5 = this.posX - this.prevPosX;
	        double d7 = this.posZ - this.prevPosZ;
	        double d9 = this.posY - this.prevPosY;
	        float f10 = MathHelper.sqrt(d5 * d5 + d9 * d9 + d7 * d7) * 4.0F;
	        if (f10 > 1.0F) {
	            f10 = 1.0F;
	        }
	        this.limbSwingAmount += (f10 - this.limbSwingAmount) * 0.4F;
	        this.limbSwing += this.limbSwingAmount;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null) {
				this.shootingEntity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 40.0D);
				if (this.ticksAlive <= NGD_SUSPEND_TIME) {
					this.setWaitPosition();
					this.setEntityScale(this.fullScale * MathHelper.clamp(this.ticksAlive / (float) NGD_SUSPEND_TIME, 1f / this.fullScale, 1.0F));
				} else {
					if (!this.isLaunched()) {
						Vec3d vec = this.shootingEntity instanceof EntityLiving && ((EntityLiving)this.shootingEntity).getAttackTarget() != null
						 ? ((EntityLiving)this.shootingEntity).getAttackTarget().getPositionVector().subtract(this.getPositionVector())
						 : this.shootingEntity.getLookVec();
						this.shoot(vec.x, vec.y, vec.z, 1.2f, 0f);
						this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:dragon_roar")), 2f, 1f);
					}
					this.shootingEntity.setPositionAndUpdate(this.posX, this.posY + 0.5d, this.posZ);
				}
			}
			this.updateLimbSwing();
			if (!this.world.isRemote && (this.ticksInAir > 30 || this.shootingEntity == null || !this.shootingEntity.isEntityAlive()))
				this.setDead();
		}

		@Override
		public void renderParticles() {
			if (this.isLaunched()) {
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY + (this.height / 2.0F), this.posZ,
				 200, this.width / 2.0F, this.height / 2.0F, this.width / 2.0F, 0.0D, 0.0D, 0.0D, 0x80800000, 40, 0);
			} else {
				Particles.spawnParticle(this.world, Particles.Types.FLAME, this.posX, this.posY + (this.height / 2.0F), this.posZ,
				 100, 0.0D, 0.0D, 0.0D, (this.rand.nextFloat() - 0.5F) * 0.8D, (this.rand.nextFloat() - 0.6F) * 1.0F,
				 (this.rand.nextFloat() - 0.5F) * 0.8D, 0x40FF0000, 60);
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (result.entityHit != null && result.entityHit.equals(this.shootingEntity))
				return;
			if (!this.world.isRemote) {
				if (this.shootingEntity != null) {
					float f = (float)ProcedureUtils.getModifiedAttackDamage(this.shootingEntity);
					DamageSource ds = DamageSource.causeIndirectDamage(this, this.shootingEntity).setDamageBypassesArmor().setDamageIsAbsolute();
					if (result.entityHit instanceof EntityLivingBase) {
						result.entityHit.attackEntityFrom(ds, f * 64f);
						ProcedureUtils.pushEntity(new Vec3d(this.posX, this.posY, this.posZ), result.entityHit, 30.0D, 2.0F);
					}
					ProcedureAoeCommand.set(this, 0.0D, 5.0D).exclude(this.shootingEntity)
					 .damageEntities(ds, f * 16f);
				}
				this.world.newExplosion(this, this.posX, this.posY, this.posZ, 10.0F, false,
				 ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity));
				if (EntityScalableProjectile.forwardsRaycastBlocks(this) != null) {
					this.setDead();
				}
			}
			this.motionX *= 0.4f;
			this.motionY *= 0.4f;
			this.motionZ *= 0.4f;
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote && this.shootingEntity != null) {
				closeGates(this.shootingEntity);
			}
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
			return Math.max(blockStateIn.getBlock().getExplosionResistance(worldIn, pos, this, explosionIn) - 1999f, 0.1f);
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
			RenderingRegistry.registerEntityRenderingHandler(EntitySekizo.class, renderManager -> new RenderSekizo(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntityNGDragon.class, renderManager -> new RenderNGDragon(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntityHirudora.class, renderManager -> new RenderHirudora(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderHirudora extends Render<EntityHirudora> {
			//private final ModelWhiteTiger model = new ModelWhiteTiger();
			private final ModelHirudora model = new ModelHirudora();
	
			public RenderHirudora(RenderManager renderManager) {
				super(renderManager);
				this.shadowSize = 0.1F;
			}
	
			@Override
			public void doRender(EntityHirudora entity, double x, double y, double z, float yaw, float pt) {
				float scale = entity.getEntityScale();
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) x, (float) y + scale, (float) z);
				GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt - 180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				this.model.render(entity, 0.0F, 0.0F, pt + entity.ticksExisted, 0.0F, 0.0F, 0.0625F);
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityHirudora entity) {
				return HIRUDORA_TEXTURE;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class RenderSekizo extends EntityBeamBase.Renderer<EntitySekizo> {
			public RenderSekizo(RenderManager renderManager) {
				super(renderManager);
			}
	
			@Override
			public EntityBeamBase.Model getMainModel(EntitySekizo entity, float pt) {
				float length = MathHelper.clamp(entity.getBeamLength() * ((float)entity.ticksAlive + pt) / 30f, 1f, entity.getBeamLength());
				ModelLongCube model = new ModelLongCube(length);
				model.scale = 1.0F + length * 0.15F;
				return model;
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntitySekizo entity) {
				return SEKIZO_TEXTURE;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class RenderNGDragon extends Render<EntityNGDragon> {
			private final ModelNightguyDragon model = new ModelNightguyDragon();
	
			public RenderNGDragon(RenderManager renderManager) {
				super(renderManager);
				this.shadowSize = 0.1F;
			}
	
			@Override
			public void doRender(EntityNGDragon entity, double x, double y, double z, float yaw, float pt) {
				float f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * pt;
				float f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - pt);
				this.model.setRotationAngles(f6, f5, (float)entity.ticksExisted + pt, 0f, 0f, 0.0625F, entity);
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				float scale = entity.getEntityScale();
				GlStateManager.translate((float) x, (float) y + scale, (float) z + scale);
				GlStateManager.rotate(-entity.prevRotationYaw - (entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * pt - 180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				GlStateManager.color(1.0F, 1.0F, 1.0F, scale / entity.fullScale * 0.8F);
				//GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityNGDragon entity) {
				return NGDRAGON_TEXTURE;
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelLongCube extends EntityBeamBase.Model {
			private final ModelRenderer bone;
			private final ModelRenderer bb_main;
			protected float scale = 1.0F;
			
			public ModelLongCube(float length) {
				this.textureWidth = 32;
				this.textureHeight = 32;
				this.bone = new ModelRenderer(this);
				this.bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				int len = (int)(16f * length);
				this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -0.5F, -16.0F, -0.5F, 1, len, 1, 0.0F, false));
				this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -1.0F, -16.0F, -1.0F, 2, len, 2, 0.0F, false));
				this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -1.5F, -16.0F, -1.5F, 3, len, 3, 0.0F, false));
				this.bone.cubeList.add(new ModelBox(this.bone, 0, 0, -2.0F, -16.0F, -2.0F, 4, len, 4, 0.0F, false));
				this.bb_main = new ModelRenderer(this);
				this.bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 0, -4.0F, -16.0F, -4.0F, 8, len, 8, 0.0F, false));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, (this.scale - 1.0F) * 1.5F, 0.0F);
				GlStateManager.scale(this.scale, this.scale, this.scale);
				GlStateManager.color(0f, 0f, 0f, 0.3f);
				this.bone.render(f5);
				GlStateManager.color(1f, 1f, 1f, 0.3f);
				this.bb_main.render(f5);
				GlStateManager.popMatrix();
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelNightguyDragon extends ModelBase {
			private final ModelRenderer head;
			private final ModelRenderer teethUpper;
			private final ModelRenderer bone;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer jaw;
			private final ModelRenderer teethLower;
			private final ModelRenderer[] hornRight = new ModelRenderer[7];
			private final ModelRenderer[] hornLeft = new ModelRenderer[7];
			private final ModelRenderer[] whiskerLeft = new ModelRenderer[6];
			private final ModelRenderer[] whiskerRight = new ModelRenderer[6];
			private final ModelRenderer spine;
			private final ModelRenderer spine2;
			private final ModelRenderer spine3;
			private final ModelRenderer spine4;
			private final ModelRenderer spine5;
			private final ModelRenderer spine6;
			private final ModelRenderer spine7;
			private final ModelRenderer spine8;
			private final ModelRenderer eyes;
			
			public ModelNightguyDragon() {
				this.textureWidth = 256;
				this.textureHeight = 256;
	
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 0.0F, 0.0F);
				head.cubeList.add(new ModelBox(head, 176, 44, -6.0F, 6.0F, -26.0F, 12, 5, 16, 1.0F, false));
				head.cubeList.add(new ModelBox(head, 112, 30, -8.0F, -1.0F, -11.0F, 16, 16, 16, 1.0F, false));
				head.cubeList.add(new ModelBox(head, 112, 0, 2.0F, 4.0F, -28.0F, 4, 4, 6, 0.0F, true));
				head.cubeList.add(new ModelBox(head, 112, 0, -6.0F, 4.0F, -28.0F, 4, 4, 6, 0.0F, false));
		
				teethUpper = new ModelRenderer(this);
				teethUpper.setRotationPoint(0.0F, 24.0F, 0.0F);
				head.addChild(teethUpper);
				teethUpper.cubeList.add(new ModelBox(teethUpper, 152, 146, -6.0F, -12.0F, -26.0F, 12, 2, 16, 0.5F, false));
		
				bone = new ModelRenderer(this);
				bone.setRotationPoint(9.0F, 7.0F, -11.0F);
				head.addChild(bone);
				setRotationAngle(bone, 0.0F, -0.7854F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 0, 200, 0.0F, -8.0F, 0.0F, 10, 16, 0, 0.0F, false));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(-9.0F, 7.0F, -11.0F);
				head.addChild(bone2);
				setRotationAngle(bone2, 0.0F, 0.7854F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 200, -10.0F, -8.0F, 0.0F, 10, 16, 0, 0.0F, true));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, -2.0F, -11.0F);
				head.addChild(bone3);
				setRotationAngle(bone3, -0.8727F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 50, -8.0F, -10.0F, 0.0F, 16, 10, 0, 0.0F, false));
	
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, 11.0F, -9.0F);
				head.addChild(jaw);
				setRotationAngle(jaw, 0.7854F, 0.0F, 0.0F);
				jaw.cubeList.add(new ModelBox(jaw, 176, 65, -6.0F, 0.0F, -16.75F, 12, 4, 16, 1.0F, false));
		
				teethLower = new ModelRenderer(this);
				teethLower.setRotationPoint(0.0F, 13.0F, 9.0F);
				jaw.addChild(teethLower);
				teethLower.cubeList.add(new ModelBox(teethLower, 112, 144, -6.0F, -16.0F, -25.75F, 12, 2, 16, 0.5F, false));
		
				hornRight[0] = new ModelRenderer(this);
				hornRight[0].setRotationPoint(-6.0F, -2.0F, -13.0F);
				head.addChild(hornRight[0]);
				setRotationAngle(hornRight[0], 0.0873F, -0.5236F, 0.0F);
				hornRight[0].cubeList.add(new ModelBox(hornRight[0], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 1.0F, false));
		
				hornRight[1] = new ModelRenderer(this);
				hornRight[1].setRotationPoint(0.0F, 0.0F, 7.0F);
				hornRight[0].addChild(hornRight[1]);
				setRotationAngle(hornRight[1], 0.0873F, 0.0873F, 0.0F);
				hornRight[1].cubeList.add(new ModelBox(hornRight[1], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.7F, false));
		
				hornRight[2] = new ModelRenderer(this);
				hornRight[2].setRotationPoint(0.0F, 0.0F, 7.0F);
				hornRight[1].addChild(hornRight[2]);
				setRotationAngle(hornRight[2], 0.0873F, 0.0873F, 0.0F);
				hornRight[2].cubeList.add(new ModelBox(hornRight[2], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.4F, false));
		
				hornRight[3] = new ModelRenderer(this);
				hornRight[3].setRotationPoint(0.0F, 0.0F, 6.5F);
				hornRight[2].addChild(hornRight[3]);
				setRotationAngle(hornRight[3], 0.0873F, 0.0873F, 0.0F);
				hornRight[3].cubeList.add(new ModelBox(hornRight[3], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.1F, false));
		
				hornRight[4] = new ModelRenderer(this);
				hornRight[4].setRotationPoint(0.0F, 0.0F, 6.0F);
				hornRight[3].addChild(hornRight[4]);
				setRotationAngle(hornRight[4], 0.0873F, 0.0873F, 0.0F);
				hornRight[4].cubeList.add(new ModelBox(hornRight[4], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, -0.2F, false));
		
				hornRight[5] = new ModelRenderer(this);
				hornRight[5].setRotationPoint(0.0F, 0.0F, 5.5F);
				hornRight[4].addChild(hornRight[5]);
				setRotationAngle(hornRight[5], 0.0873F, 0.0873F, 0.0F);
				hornRight[5].cubeList.add(new ModelBox(hornRight[5], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, -0.5F, false));
		
				hornRight[6] = new ModelRenderer(this);
				hornRight[6].setRotationPoint(0.0F, 0.0F, 5.0F);
				hornRight[5].addChild(hornRight[6]);
				setRotationAngle(hornRight[6], 0.0873F, 0.0873F, 0.0F);
				hornRight[6].cubeList.add(new ModelBox(hornRight[6], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, -0.8F, false));
		
				hornLeft[0] = new ModelRenderer(this);
				hornLeft[0].setRotationPoint(6.0F, -2.0F, -13.0F);
				head.addChild(hornLeft[0]);
				setRotationAngle(hornLeft[0], 0.0873F, 0.5236F, 0.0F);
				hornLeft[0].cubeList.add(new ModelBox(hornLeft[0], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 1.0F, true));
		
				hornLeft[1] = new ModelRenderer(this);
				hornLeft[1].setRotationPoint(0.0F, 0.0F, 7.0F);
				hornLeft[0].addChild(hornLeft[1]);
				setRotationAngle(hornLeft[1], 0.0873F, -0.0873F, 0.0F);
				hornLeft[1].cubeList.add(new ModelBox(hornLeft[1], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.7F, true));
		
				hornLeft[2] = new ModelRenderer(this);
				hornLeft[2].setRotationPoint(0.0F, 0.0F, 7.0F);
				hornLeft[1].addChild(hornLeft[2]);
				setRotationAngle(hornLeft[2], 0.0873F, -0.0873F, 0.0F);
				hornLeft[2].cubeList.add(new ModelBox(hornLeft[2], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.4F, true));
		
				hornLeft[3] = new ModelRenderer(this);
				hornLeft[3].setRotationPoint(0.0F, 0.0F, 6.5F);
				hornLeft[2].addChild(hornLeft[3]);
				setRotationAngle(hornLeft[3], 0.0873F, -0.0873F, 0.0F);
				hornLeft[3].cubeList.add(new ModelBox(hornLeft[3], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, 0.1F, true));
		
				hornLeft[4] = new ModelRenderer(this);
				hornLeft[4].setRotationPoint(0.0F, 0.0F, 6.0F);
				hornLeft[3].addChild(hornLeft[4]);
				setRotationAngle(hornLeft[4], 0.0873F, -0.0873F, 0.0F);
				hornLeft[4].cubeList.add(new ModelBox(hornLeft[4], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, -0.2F, true));
		
				hornLeft[5] = new ModelRenderer(this);
				hornLeft[5].setRotationPoint(0.0F, 0.0F, 5.5F);
				hornLeft[4].addChild(hornLeft[5]);
				setRotationAngle(hornLeft[5], 0.0873F, -0.0873F, 0.0F);
				hornLeft[5].cubeList.add(new ModelBox(hornLeft[5], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, -0.5F, true));
		
				hornLeft[6] = new ModelRenderer(this);
				hornLeft[6].setRotationPoint(0.0F, 0.0F, 5.0F);
				hornLeft[5].addChild(hornLeft[6]);
				setRotationAngle(hornLeft[6], 0.0873F, -0.0873F, 0.0F);
				hornLeft[6].cubeList.add(new ModelBox(hornLeft[6], 0, 0, -1.0F, -2.0F, 0.0F, 2, 4, 6, -0.8F, true));
		
				whiskerLeft[0] = new ModelRenderer(this);
				whiskerLeft[0].setRotationPoint(6.0F, 6.0F, -24.0F);
				head.addChild(whiskerLeft[0]);
				setRotationAngle(whiskerLeft[0], 0.0F, 1.0472F, 0.0F);
				whiskerLeft[0].cubeList.add(new ModelBox(whiskerLeft[0], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.8F, true));
		
				whiskerLeft[1] = new ModelRenderer(this);
				whiskerLeft[1].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[0].addChild(whiskerLeft[1]);
				setRotationAngle(whiskerLeft[1], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[1].cubeList.add(new ModelBox(whiskerLeft[1], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.6F, true));
		
				whiskerLeft[2] = new ModelRenderer(this);
				whiskerLeft[2].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[1].addChild(whiskerLeft[2]);
				setRotationAngle(whiskerLeft[2], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[2].cubeList.add(new ModelBox(whiskerLeft[2], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.4F, true));
		
				whiskerLeft[3] = new ModelRenderer(this);
				whiskerLeft[3].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[2].addChild(whiskerLeft[3]);
				setRotationAngle(whiskerLeft[3], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[3].cubeList.add(new ModelBox(whiskerLeft[3], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.2F, true));
		
				whiskerLeft[4] = new ModelRenderer(this);
				whiskerLeft[4].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[3].addChild(whiskerLeft[4]);
				setRotationAngle(whiskerLeft[4], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[4].cubeList.add(new ModelBox(whiskerLeft[4], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.0F, true));
		
				whiskerLeft[5] = new ModelRenderer(this);
				whiskerLeft[5].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerLeft[4].addChild(whiskerLeft[5]);
				setRotationAngle(whiskerLeft[5], -0.0873F, -0.1745F, 0.0F);
				whiskerLeft[5].cubeList.add(new ModelBox(whiskerLeft[5], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, -0.2F, true));
		
				whiskerRight[0] = new ModelRenderer(this);
				whiskerRight[0].setRotationPoint(-6.0F, 6.0F, -24.0F);
				head.addChild(whiskerRight[0]);
				setRotationAngle(whiskerRight[0], 0.0F, -1.0472F, 0.0F);
				whiskerRight[0].cubeList.add(new ModelBox(whiskerRight[0], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.8F, false));
		
				whiskerRight[1] = new ModelRenderer(this);
				whiskerRight[1].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[0].addChild(whiskerRight[1]);
				setRotationAngle(whiskerRight[1], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[1].cubeList.add(new ModelBox(whiskerRight[1], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.6F, false));
		
				whiskerRight[2] = new ModelRenderer(this);
				whiskerRight[2].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[1].addChild(whiskerRight[2]);
				setRotationAngle(whiskerRight[2], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[2].cubeList.add(new ModelBox(whiskerRight[2], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.4F, false));
		
				whiskerRight[3] = new ModelRenderer(this);
				whiskerRight[3].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[2].addChild(whiskerRight[3]);
				setRotationAngle(whiskerRight[3], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[3].cubeList.add(new ModelBox(whiskerRight[3], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.2F, false));
		
				whiskerRight[4] = new ModelRenderer(this);
				whiskerRight[4].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[3].addChild(whiskerRight[4]);
				setRotationAngle(whiskerRight[4], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[4].cubeList.add(new ModelBox(whiskerRight[4], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, 0.0F, false));
		
				whiskerRight[5] = new ModelRenderer(this);
				whiskerRight[5].setRotationPoint(0.0F, 0.0F, 6.0F);
				whiskerRight[4].addChild(whiskerRight[5]);
				setRotationAngle(whiskerRight[5], -0.0873F, 0.1745F, 0.0F);
				whiskerRight[5].cubeList.add(new ModelBox(whiskerRight[5], 0, 0, -1.0F, -1.0F, 0.0F, 2, 2, 6, -0.2F, false));
		
				spine = new ModelRenderer(this);
				spine.setRotationPoint(0.0F, 6.5F, 7.0F);
				setRotationAngle(spine, 0.0F, -0.7854F, 0.0F);
				spine.cubeList.add(new ModelBox(spine, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
				spine.cubeList.add(new ModelBox(spine, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));
		
				spine2 = new ModelRenderer(this);
				spine2.setRotationPoint(0.0F, 0.0F, 11.0F);
				spine.addChild(spine2);
				setRotationAngle(spine2, 0.0F, 0.7854F, 0.0F);
				spine2.cubeList.add(new ModelBox(spine2, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
				spine2.cubeList.add(new ModelBox(spine2, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));
		
				spine3 = new ModelRenderer(this);
				spine3.setRotationPoint(0.0F, 0.0F, 11.0F);
				spine2.addChild(spine3);
				setRotationAngle(spine3, 0.0F, 0.7854F, 0.0F);
				spine3.cubeList.add(new ModelBox(spine3, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
				spine3.cubeList.add(new ModelBox(spine3, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));
		
				spine4 = new ModelRenderer(this);
				spine4.setRotationPoint(0.0F, 0.0F, 11.0F);
				spine3.addChild(spine4);
				setRotationAngle(spine4, 0.0F, 0.3927F, 0.0F);
				spine4.cubeList.add(new ModelBox(spine4, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
				spine4.cubeList.add(new ModelBox(spine4, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));
		
				spine5 = new ModelRenderer(this);
				spine5.setRotationPoint(0.0F, 0.0F, 11.0F);
				spine4.addChild(spine5);
				setRotationAngle(spine5, 0.0F, -0.7854F, 0.0F);
				spine5.cubeList.add(new ModelBox(spine5, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 2.0F, false));
				spine5.cubeList.add(new ModelBox(spine5, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 1.0F, false));
		
				spine6 = new ModelRenderer(this);
				spine6.setRotationPoint(0.0F, 0.0F, 11.0F);
				spine5.addChild(spine6);
				setRotationAngle(spine6, 0.0F, -0.7854F, 0.0F);
				spine6.cubeList.add(new ModelBox(spine6, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 1.0F, false));
				spine6.cubeList.add(new ModelBox(spine6, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 0.5F, false));
		
				spine7 = new ModelRenderer(this);
				spine7.setRotationPoint(0.0F, 0.0F, 11.0F);
				spine6.addChild(spine7);
				setRotationAngle(spine7, 0.0F, -0.7854F, 0.0F);
				spine7.cubeList.add(new ModelBox(spine7, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 0.5F, false));
				spine7.cubeList.add(new ModelBox(spine7, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 0.25F, false));
		
				spine8 = new ModelRenderer(this);
				spine8.setRotationPoint(0.0F, 0.0F, 11.0F);
				spine7.addChild(spine8);
				setRotationAngle(spine8, 0.0F, 0.7854F, 0.0F);
				spine8.cubeList.add(new ModelBox(spine8, 192, 104, -5.0F, -4.5F, 0.0F, 10, 10, 10, 0.0F, false));
				spine8.cubeList.add(new ModelBox(spine8, 48, 0, -1.0F, -10.5F, 2.0F, 2, 4, 6, 0.0F, false));
		
				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, 0.0F, 0.0F);
				eyes.cubeList.add(new ModelBox(eyes, 130, 50, -6.6F, 2.6F, -12.1F, 3, 2, 0, 0.0F, false));
				eyes.cubeList.add(new ModelBox(eyes, 130, 50, 3.6F, 2.6F, -12.1F, 3, 2, 0, 0.0F, true));
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				this.head.render(f5);
				this.spine.render(f5);
				this.eyes.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float limbSwing, float f1, float ageInTicks, float f3, float f4, float f5, Entity e) {
				super.setRotationAngles(limbSwing, f1, ageInTicks, f3, f4, f5, e);
				for (int i = 1; i < hornRight.length; i++) {
					hornRight[i].rotateAngleX = 0.0873F + MathHelper.sin((ageInTicks - i) * 0.2F) * 0.0873F;
					hornRight[i].rotateAngleY = MathHelper.cos((ageInTicks - i) * 0.3F) * 0.0873F;
					hornLeft[i].rotateAngleX = 0.0873F + MathHelper.sin((ageInTicks - i) * 0.2F) * 0.0873F;
					hornLeft[i].rotateAngleY = -MathHelper.cos((ageInTicks - i) * 0.3F) * 0.0873F;
				}
				for (int i = 2; i < whiskerRight.length; i++) {
					whiskerLeft[i].rotateAngleZ = 0.0873F * ageInTicks;
					whiskerRight[i].rotateAngleZ = -0.0873F * ageInTicks;
				}
				if (((EntityNGDragon)e).getTicksAlive() > NGD_SUSPEND_TIME) {
					float f6 = MathHelper.cos(limbSwing * 0.6662F);
					float f7 = MathHelper.sin(limbSwing * 0.6662F);
					spine.rotateAngleY = f6 * 0.1F * f1;
					spine2.rotateAngleY = f6 * 0.1F * f1;
					spine3.rotateAngleY = f7 * 0.1F * f1;
					spine4.rotateAngleY = f7 * 0.1F * f1;
					spine5.rotateAngleY = f6 * 0.1F * f1;
					spine6.rotateAngleY = f6 * 0.1F * f1;
					spine7.rotateAngleY = f7 * 0.1F * f1;
					spine8.rotateAngleY = f7 * 0.1F * f1;
					//spine.rotateAngleX += f7 * 0.05F * f1;
					//spine2.rotateAngleX += f7 * 0.05F * f1;
					//spine3.rotateAngleX += f6 * 0.05F * f1;
					//spine4.rotateAngleX = f6 * 0.05F * f1;
					//spine5.rotateAngleX = f7 * 0.05F * f1;
					//spine6.rotateAngleX = f7 * 0.05F * f1;
					//spine7.rotateAngleX = f6 * 0.05F * f1;
					//spine8.rotateAngleX = f6 * 0.05F * f1;
				}
			}
		}
	
		@SideOnly(Side.CLIENT)
		public class ModelHirudora extends ModelBase {
			private final ModelRenderer head;
			private final ModelRenderer neck;
			private final ModelRenderer[] neck1 = new ModelRenderer[12];
			private final ModelRenderer forehead;
			private final ModelRenderer bone14;
			private final ModelRenderer bone15;
			private final ModelRenderer bone16;
			private final ModelRenderer nose;
			private final ModelRenderer bone3;
			private final ModelRenderer bone12;
			private final ModelRenderer bone4;
			private final ModelRenderer bone13;
			private final ModelRenderer bone2;
			private final ModelRenderer cheekRight;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer cheekLeft;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer browLeft;
			private final ModelRenderer browRight;
			private final ModelRenderer jaw;
			private final ModelRenderer leftear;
			private final ModelRenderer rightear;
			private final ModelRenderer eyes;
		
			public ModelHirudora() {
				textureWidth = 64;
				textureHeight = 64;
		
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 0.0F, 0.0F);
				head.cubeList.add(new ModelBox(head, 12, 12, -4.0F, 8.0F, -8.0F, 8, 8, 4, 0.0F, false));
		
				neck = new ModelRenderer(this);
				neck.setRotationPoint(0.0F, 11.75F, -3.5F);
				head.addChild(neck);
				setRotationAngle(neck, -0.1309F, 0.0F, 0.0F);
				neck.cubeList.add(new ModelBox(neck, 0, 32, -4.0F, -4.5F, -2.0F, 8, 9, 6, -0.2F, false));
		
				forehead = new ModelRenderer(this);
				forehead.setRotationPoint(0.0F, 0.0F, 0.0F);
				head.addChild(forehead);
				
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.0F, 8.0F, -8.0F);
				forehead.addChild(bone14);
				setRotationAngle(bone14, 0.3927F, 0.0F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 32, 10, -2.0F, 0.0F, 0.0F, 4, 2, 4, 0.0F, false));
		
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(-2.0F, 8.0F, -8.0F);
				forehead.addChild(bone15);
				setRotationAngle(bone15, 0.3491F, 0.2618F, -0.1745F);
				bone15.cubeList.add(new ModelBox(bone15, 0, 18, -2.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F, false));
		
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(2.0F, 8.0F, -8.0F);
				forehead.addChild(bone16);
				setRotationAngle(bone16, 0.3491F, -0.2618F, 0.1745F);
				bone16.cubeList.add(new ModelBox(bone16, 0, 18, 0.0F, 0.0F, 0.0F, 2, 2, 4, 0.0F, true));
		
				nose = new ModelRenderer(this);
				nose.setRotationPoint(0.0F, 12.25F, -7.75F);
				head.addChild(nose);
				nose.cubeList.add(new ModelBox(nose, 16, 24, -1.0F, -1.5F, -4.5F, 2, 1, 1, 0.0F, false));
		
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(1.25F, 0.25F, -4.0F);
				nose.addChild(bone3);
				setRotationAngle(bone3, 0.1452F, 0.2555F, 0.2256F);
				bone3.cubeList.add(new ModelBox(bone3, 3, 24, -1.5F, -1.5F, 0.0F, 3, 3, 4, 0.0F, false));
		
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, -0.25F, 4.75F);
				bone3.addChild(bone12);
				setRotationAngle(bone12, 0.3927F, 0.1745F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 8, 4, -1.5F, -1.5F, -0.5F, 3, 3, 1, 0.0F, false));
		
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(-1.25F, 0.25F, -4.0F);
				nose.addChild(bone4);
				setRotationAngle(bone4, 0.1452F, -0.2555F, -0.2256F);
				bone4.cubeList.add(new ModelBox(bone4, 3, 24, -1.5F, -1.5F, 0.0F, 3, 3, 4, 0.0F, true));
		
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, -0.25F, 4.75F);
				bone4.addChild(bone13);
				setRotationAngle(bone13, 0.3927F, -0.1745F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 8, 4, -1.5F, -1.5F, -0.5F, 3, 3, 1, 0.0F, true));
		
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.0F, -1.25F, -4.5F);
				nose.addChild(bone2);
				setRotationAngle(bone2, 0.5672F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 16, 0, -1.5F, 0.0F, 0.0F, 3, 2, 5, 0.0F, false));
		
				cheekRight = new ModelRenderer(this);
				cheekRight.setRotationPoint(-3.75F, 10.25F, -8.5F);
				head.addChild(cheekRight);
				
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 0.0F);
				cheekRight.addChild(bone5);
				setRotationAngle(bone5, 0.3927F, -0.4363F, 0.1309F);
				bone5.cubeList.add(new ModelBox(bone5, 0, 4, 0.0F, 0.0F, 0.0F, 1, 4, 3, 0.0F, false));
		
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.25F, 4.0F, -0.5F);
				cheekRight.addChild(bone6);
				setRotationAngle(bone6, -0.3927F, -0.5672F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 11, 0.0F, -3.0F, 0.0F, 1, 3, 3, 0.0F, false));
		
				cheekLeft = new ModelRenderer(this);
				cheekLeft.setRotationPoint(3.75F, 10.25F, -8.5F);
				head.addChild(cheekLeft);
				
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 0.0F, 0.0F);
				cheekLeft.addChild(bone7);
				setRotationAngle(bone7, 0.3927F, 0.4363F, -0.1309F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 4, -1.0F, 0.0F, 0.0F, 1, 4, 3, 0.0F, true));
		
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(-0.25F, 4.0F, -0.5F);
				cheekLeft.addChild(bone8);
				setRotationAngle(bone8, -0.3927F, 0.5672F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 11, -1.0F, -3.0F, 0.0F, 1, 3, 3, 0.0F, true));
		
				browLeft = new ModelRenderer(this);
				browLeft.setRotationPoint(3.5F, 8.65F, -8.0F);
				head.addChild(browLeft);
				setRotationAngle(browLeft, 0.0873F, -0.1745F, -0.0873F);
				browLeft.cubeList.add(new ModelBox(browLeft, 0, 0, -2.0F, -0.5F, 0.0F, 2, 1, 2, 0.0F, false));
		
				browRight = new ModelRenderer(this);
				browRight.setRotationPoint(-3.5F, 8.65F, -8.0F);
				head.addChild(browRight);
				setRotationAngle(browRight, 0.0873F, 0.1745F, 0.0873F);
				browRight.cubeList.add(new ModelBox(browRight, 0, 0, 0.0F, -0.5F, 0.0F, 2, 1, 2, 0.0F, true));
		
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.0F, 13.5F, -7.75F);
				head.addChild(jaw);
				setRotationAngle(jaw, 0.5236F, 0.0F, 0.0F);
				jaw.cubeList.add(new ModelBox(jaw, 20, 24, -3.0F, 0.0F, -4.0F, 6, 2, 4, -0.2F, false));
				jaw.cubeList.add(new ModelBox(jaw, 36, 0, -3.0F, 1.25F, -3.75F, 6, 0, 3, -0.2F, false));
		
				leftear = new ModelRenderer(this);
				leftear.setRotationPoint(3.25F, 9.75F, -6.75F);
				head.addChild(leftear);
				setRotationAngle(leftear, 0.9599F, 0.8727F, 0.2618F);
				leftear.cubeList.add(new ModelBox(leftear, 32, 0, -1.0F, -2.0F, 0.0F, 1, 4, 4, -0.3F, false));
		
				rightear = new ModelRenderer(this);
				rightear.setRotationPoint(-3.25F, 9.75F, -6.75F);
				head.addChild(rightear);
				setRotationAngle(rightear, 0.9599F, -0.8727F, -0.2618F);
				rightear.cubeList.add(new ModelBox(rightear, 32, 0, 0.0F, -2.0F, 0.0F, 1, 4, 4, -0.3F, true));
		
				eyes = new ModelRenderer(this);
				eyes.setRotationPoint(0.0F, 0.0F, 0.0F);
				eyes.cubeList.add(new ModelBox(eyes, 32, 8, -3.0F, 8.5F, -8.1F, 6, 2, 0, 0.0F, false));
	
				for (int i = 0; i < neck1.length; i++) {
					neck1[i] = new ModelRenderer(this);
					neck1[i].setRotationPoint(0.0F, 0.0F, 1.5F + 5.0F * i);
					neck1[i].cubeList.add(new ModelBox(neck1[i], 0, 32, -4.0F, 7.7F, -2.0F, 8, 9, 6, 0.2F * i, false));
				}
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.7F);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.head.render(f5);
				if (f2 > (float)NGD_SUSPEND_TIME) {
					for (int i = 0; i < neck1.length; i++) {
						if (f2 - (float)NGD_SUSPEND_TIME > (float)i * (4.0F - 0.4F * i) + 3.0F) {
							GlStateManager.color(1.0F, 1.0F, 1.0F, 0.64F - 0.06F * i);
							this.neck1[i].render(f5);
						}
					}
				}
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.eyes.render(f5);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				//GlStateManager.disableAlpha();
			}
		
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
