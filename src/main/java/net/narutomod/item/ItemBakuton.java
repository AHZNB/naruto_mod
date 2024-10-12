
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.entity.EntityC1;
import net.narutomod.entity.EntityC2;
import net.narutomod.entity.EntityC3;
import net.narutomod.entity.EntityC4;
import net.narutomod.entity.EntityClone;
import net.narutomod.entity.EntityExplosiveClone;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.potion.PotionChakraEnhancedStrength;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBakuton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:bakuton")
	public static final Item block = null;
	public static final int ENTITYID = 230;
	public static final ItemJutsu.JutsuEnum JIRAIKEN = new ItemJutsu.JutsuEnum(0, "tooltip.bakuton.jiraiken", 'S', 150, 30d, new Jiraiken());
	public static final ItemJutsu.JutsuEnum CLAY = new ItemJutsu.JutsuEnum(1, "c_1", 'S', 200, 75d, new ExplosiveClay.Jutsu());
	public static final ItemJutsu.JutsuEnum CLONE = new ItemJutsu.JutsuEnum(2, "explosive_clone", 'S', 200, 150d, new EntityExplosiveClone.EC.Jutsu());

	public ItemBakuton(ElementsNarutomodMod instance) {
		super(instance, 543);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(JIRAIKEN, CLAY, CLONE));
		//elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityArrowCustom.class)
		// .id(new ResourceLocation("narutomod", "entitybulletbakuton"), ENTITYID).name("entitybulletbakuton").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:bakuton", "inventory"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
	}

	public static ItemJutsu.JutsuEnum getCurrentJutsu(ItemStack stack) {
		return ((RangedItem)block).getCurrentJutsu(stack);
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.BAKUTON, list);
			this.setUnlocalizedName("bakuton");
			this.setRegistryName("bakuton");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[JIRAIKEN.index] = 0;
			this.defaultCooldownMap[CLAY.index] = 0;
			this.defaultCooldownMap[CLONE.index] = 0;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
			if (player instanceof EntityPlayer && !player.world.isRemote && this.getCurrentJutsu(stack) == CLAY) {
				((EntityPlayer)player).sendStatusMessage(
				 new TextComponentString("C-" + Math.max(1, (int)this.getPower(stack, player, count))), true);
			} else {
				super.onUsingTick(stack, player, count);
			}
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			if (entity.isCreative() || (ProcedureUtils.hasItemInInventory(entity, ItemDoton.block) 
			 && ProcedureUtils.hasItemInInventory(entity, ItemRaiton.block))) {
				return super.onItemRightClick(world, entity, hand);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, entity.getHeldItem(hand));
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (entity.ticksExisted % 10 == 2 && entity instanceof EntityLivingBase && JIRAIKEN.jutsu.isActivated(itemstack)) {
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(
				 PotionChakraEnhancedStrength.potion, 12, (int)(((Jiraiken)JIRAIKEN.jutsu).getPower(itemstack) * 19), false, false));
			}
		}

		@Override
		public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
			if (attacker.equals(target)) {
				target = ProcedureUtils.objectEntityLookingAt(attacker, 50d, 3d, ExplosiveClay.class).entityHit;
			}
			if (target instanceof EntityLivingBase) {
				attacker.setRevengeTarget((EntityLivingBase)target);
			}
			return super.onLeftClickEntity(itemstack, attacker, target);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(TextFormatting.GREEN + net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.bakuton.musthave") + TextFormatting.RESET);
		}
	}

	public static class Jiraiken implements ItemJutsu.IJutsuCallback {
		//private boolean activated = false;
		//protected float power;
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float powerIn) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			boolean flag = !entity.isPotionActive(PotionChakraEnhancedStrength.potion);
			stack.getTagCompound().setBoolean("isJiraikenActivated", flag);
			stack.getTagCompound().setFloat("JiraikenPower", powerIn);
			//this.power = powerIn;
			//return this.activated = !entity.isPotionActive(PotionChakraEnhancedStrength.potion);
			return flag;
		}

		@Override
		public boolean isActivated(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("isJiraikenActivated") : false;
		}

		@Override
		public float getPower(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getFloat("JiraikenPower") : 0f;
		}

		@Override
		public float getBasePower() {
			return 0.2f;
		}

		@Override
		public float getPowerupDelay() {
			return 200.0f;
		}

		@Override
		public float getMaxPower() {
			return 10.0f;
		}
	}

	public abstract static class ExplosiveClay extends EntityCreature implements ItemJutsu.IJutsu {
		private EntityLivingBase owner;
		private int lifeSpan = 600;
		private float explosionSize = 3.0f;
		private final EntityAITarget aiCopyOwnerTarget = new AICopyOwnerTarget(this);
		private final EntityAITarget aiHurtByTarget = new EntityAIHurtByTarget(this, false, new Class[0]);
		private boolean targetTasksEnabled;

		public ExplosiveClay(World world) {
			super(world);
			//this.setSize(0.4F, 0.8F);
			this.isImmuneToFire = true;
			this.moveHelper = new EntityClone.AIFlyControl(this);
			this.setTargetTasks();
		}

		public ExplosiveClay(EntityLivingBase ownerIn) {
			this(ownerIn.world);
			this.owner = ownerIn;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.BAKUTON;
		}

		@Override
		protected PathNavigate createNavigator(World worldIn) {
			PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, worldIn);
			pathnavigateflying.setCanOpenDoors(false);
			pathnavigateflying.setCanFloat(true);
			pathnavigateflying.setCanEnterDoors(true);
			return pathnavigateflying;
		}

		@Override
		protected void initEntityAI() {
			super.initEntityAI();
			this.tasks.addTask(0, new EntityAISwimming(this));
			this.tasks.addTask(1, new AIChargeAttack());
			this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
		}

		protected void setTargetTasks() {
			if (!this.targetTasksEnabled) {
				this.targetTasks.addTask(1, this.aiCopyOwnerTarget);
				this.targetTasks.addTask(2, this.aiHurtByTarget);
				this.targetTasksEnabled = true;
			}
		}

		protected void clearTargetTasks() {
			if (this.targetTasksEnabled) {
				this.targetTasks.removeTask(this.aiCopyOwnerTarget);
				this.targetTasks.removeTask(this.aiHurtByTarget);
				this.setAttackTarget(null);
				this.targetTasksEnabled = false;
			}
		}

		@Override
		protected boolean canDespawn() {
			return false;
		}

		@Override
		protected Item getDropItem() {
			return null;
		}

		public EntityLivingBase getOwner() {
			return this.owner;
		}

		public int getRemainingLife() {
			return this.lifeSpan - this.ticksExisted;
		}

		public void setRemainingLife(int ticks) {
			this.lifeSpan = this.ticksExisted + ticks;
		}

		protected void setExplosionSize(float size) {
			this.explosionSize = size;
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48D);
		}

	    @Override
	    protected SoundEvent getDeathSound() {
	        return null;
	    }
	
	    @Override
	    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
	        return null;
	    }

	    @Override
	    protected void playStepSound(BlockPos pos, Block blockIn) {
	    }

	    @Override
	    public boolean isOnSameTeam(Entity entityIn) {
	    	return super.isOnSameTeam(entityIn) || entityIn.equals(this.owner);
	    }

	    @Override
	    public boolean attackEntityAsMob(Entity entityIn) {
	    	EntityLivingBase owner = this.getOwner();
	    	if (!this.world.isRemote) {
		    	this.world.createExplosion(owner, this.posX, this.posY, this.posZ,
			     this.explosionSize, net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, owner));
	    		this.setDead();
	    		return true;
	    	}
	    	return false;
	    }

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.isExplosion() || source == DamageSource.FALL) {
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		protected void onDeathUpdate() {
			if (!this.world.isRemote) {
		    	this.world.createExplosion(owner, this.posX, this.posY, this.posZ,
			     this.explosionSize, net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, owner));
			}
    		this.setDead();
		}

		private void poof() {
			this.playSound(net.minecraft.init.SoundEvents.ENTITY_SLIME_SQUISH, 0.6F, 0.7F);
			Particles.Renderer particles = new Particles.Renderer(this.world);
			for (int i = 0; i < 200; i++) {
				float scale = (1.667f + this.rand.nextFloat() * 1.667f) * this.height;
				particles.spawnParticles(Particles.Types.SMOKE, this.posX + (this.rand.nextFloat()-0.5f) * this.width,
				 this.posY + this.rand.nextFloat() * this.height, this.posZ + (this.rand.nextFloat()-0.5f) * this.width,
				 1, 0d, 0d, 0d, 0d, 0d, 0d, -1, (int)(scale * 10f), 0, 0, -1, -6 - this.rand.nextInt(8));
			}
			particles.send();
		}

	    @Override
	    public void onUpdate() {
	    	this.fallDistance = 0f;
	    	this.clearActivePotions();
	    	super.onUpdate();
	    	this.setNoGravity(true);
	    	if (this.ticksExisted > this.lifeSpan && !this.world.isRemote) {
	    		this.poof();
	    		this.setDead();
	    	}
	    }

	    public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float powerIn) {
				Entity ec;
				Vec3d vec = entity.getLookVec();
				vec = entity.getPositionVector().addVector(vec.x, 1d, vec.z);
				if (powerIn < 1f) {
					return false;
				} else if (powerIn < 2f) {
					ec = new EntityC1.EC(entity);
				} else if (powerIn < 3f) {
					ec = new EntityC2.EC(entity);
					ProcedureUtils.poofWithSmoke(entity.world, vec.x, vec.y, vec.z, ec.width, ec.height);
				} else if (powerIn < 4f) {
					ec = new EntityC3.EC(entity);
				} else if (powerIn <= this.getMaxPower()) {
					ec = new EntityC4.EC(entity);
					float f = ((RangedItem)stack.getItem()).getXpRatio(stack, CLAY);
					((EntityC4.EC)ec).setExplosionDamage(100, (int)(2.0f * f));
				} else {
					return false;
				}
				ec.setLocationAndAngles(vec.x, vec.y, vec.z, entity.rotationYaw, 0f);
				ec.setRotationYawHead(entity.rotationYaw);
				entity.world.spawnEntity(ec);
				return true;
			}

			@Override
			public float getBasePower() {
				return 0.9f;
			}

			@Override
			public float getPowerupDelay() {
				return 150.0f;
			}

			@Override
			public float getMaxPower() {
				return 4.1f;
			}
	    }
	
	    class AIChargeAttack extends EntityAIBase {
	        public AIChargeAttack() {
	            this.setMutexBits(1);
	        }
	
	        @Override
	        public boolean shouldExecute() {
	        	EntityLivingBase target = ExplosiveClay.this.getAttackTarget();
	            if (target != null
	             && !ExplosiveClay.this.getMoveHelper().isUpdating() && ExplosiveClay.this.rand.nextInt(5) == 0) {
	                return ExplosiveClay.this.getDistanceSq(target) > 4.0D;
	            }
                return false;
	        }
	
	        @Override
	        public boolean shouldContinueExecuting() {
	            return //ExplosiveClay.this.getMoveHelper().isUpdating() &&
	             ExplosiveClay.this.getAttackTarget() != null && ExplosiveClay.this.getAttackTarget().isEntityAlive();
	        }
	
	        @Override
	        public void startExecuting() {
	            EntityLivingBase entitylivingbase = ExplosiveClay.this.getAttackTarget();
	            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
	            ExplosiveClay.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 2.0D);
	        }
	
	        @Override
	        public void updateTask() {
	            EntityLivingBase target = ExplosiveClay.this.getAttackTarget();
	            if (ExplosiveClay.this.getEntityBoundingBox().intersects(target.getEntityBoundingBox().grow(target.width * 0.5f))) {
	                ExplosiveClay.this.attackEntityAsMob(target);
	            } else { //if (ExplosiveClay.this.getDistanceSq(entitylivingbase) < 9.0D) {
	                Vec3d vec3d = target.getPositionEyes(1.0F);
	                ExplosiveClay.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 2.0D);
	            }
	        }
	    }

	    class AICopyOwnerTarget extends EntityAITarget {
	        public AICopyOwnerTarget(EntityCreature creature) {
	            super(creature, false);
	        }
	
	        @Override
	        public boolean shouldExecute() {
	            if (ExplosiveClay.this.owner != null && ExplosiveClay.this.owner.isEntityAlive()) {
		            if (ExplosiveClay.this.owner instanceof EntityLiving) {
            			this.target = ((EntityLiving)ExplosiveClay.this.owner).getAttackTarget();
		            } else if (ExplosiveClay.this.owner.getRevengeTarget() != null) {
		            	this.target = ExplosiveClay.this.owner.getRevengeTarget();
		            } else {
		            	this.target = ExplosiveClay.this.owner.getLastAttackedEntity();
		            }
	            }
	            return this.target != null && this.isSuitableTarget(this.target, false);
	        }

	        @Override
	        public boolean shouldContinueExecuting() {
	        	if (!ExplosiveClay.this.owner.isEntityAlive()) {
	        		return false;
	        	} else if (ExplosiveClay.this.owner instanceof EntityLiving && ((EntityLiving)ExplosiveClay.this.owner).isAIDisabled()) {
	        		return false;
	        	}
	        	return super.shouldContinueExecuting();
	        }
	
	        @Override
	        public void startExecuting() {
	            ExplosiveClay.this.setAttackTarget(this.target);
	            super.startExecuting();
	        }
	    }
	}
}
