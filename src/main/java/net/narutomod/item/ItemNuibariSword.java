
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.world.WorldServer;
import net.minecraft.network.play.server.SPacketCollectItem;

import net.narutomod.procedure.ProcedureSync;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import java.util.Map;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import java.util.Iterator;

@ElementsNarutomodMod.ModElement.Tag
public class ItemNuibariSword extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:nuibari_sword")
	public static final Item block = null;
	public static final int ENTITYID = 290;

	public ItemNuibariSword(ElementsNarutomodMod instance) {
		super(instance, 613);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletnuibari_sword"), ENTITYID).name("entitybulletnuibari_sword").tracker(64, 1, true)
				.build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:nuibari_sword", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new RenderCustom(renderManager, Minecraft.getMinecraft().getRenderItem());
		});
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EntityCustom.KnockbackHook());
	}

	public static class RangedItem extends Item implements ItemOnBody.Interface {
		public RangedItem() {
			super();
			this.setMaxDamage(0);
			this.setFull3D();
			this.setUnlocalizedName("nuibari_sword");
			this.setRegistryName("nuibari_sword");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", 8d, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.0, 0));
			}
			return multimap;
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.nuibari.general"));
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				int slotID = getSlotId(entity);
				float f = net.minecraft.item.ItemBow.getArrowVelocity(this.getMaxItemUseDuration(itemstack) - timeLeft);
				EntityCustom entityarrow = new EntityCustom(world, entity);
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, f * 2.0f, 0);
				//entityarrow.setSilent(true);
				entityarrow.setDamage(16);
				//entityarrow.setKnockbackStrength(0);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
						SoundCategory.NEUTRAL, 1, 1f / (itemRand.nextFloat() * 0.5f + 1f) + f);
				world.spawnEntity(entityarrow);
				ItemStack newstack = new ItemStack(ItemNuibariThrown.block);
				((ItemNuibariThrown.RangedItem)newstack.getItem()).setEntity(newstack, entityarrow);
				entity.replaceItemInInventory(slotID, newstack);
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

	private static int getSlotId(EntityPlayer entity) {
		for (int i = 0; i < entity.inventory.mainInventory.size(); i++) {
			ItemStack stack = entity.inventory.mainInventory.get(i);
			if (stack != null && stack.getItem() == block) {
				return i;
			}
		}
		if (entity.getHeldItemOffhand().getItem() == block) {
			return 99;
		}
		return -1;
	}

	public static class EntityCustom extends EntityThrowable {
		private static final String SKEWERED_TIME = "NuibariSkeweredTime";
		private static final DataParameter<Integer> SHOOTERID = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> OTHERENTITYID = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Boolean> CLEARLIST = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private List<EntityLivingBase> skeweredEntities = Lists.newArrayList();
		private double damage;
		private float health = 15.0F;

		public EntityCustom(World a) {
			super(a);
			this.setSize(0.5F, 0.5F);
		}

		public EntityCustom(World worldIn, EntityLivingBase shooter) {
			super(worldIn, shooter);
			this.setShooter(shooter);
			this.setSize(0.5F, 0.5F);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(SHOOTERID, Integer.valueOf(-1));
			this.dataManager.register(OTHERENTITYID, Integer.valueOf(-1));
			this.dataManager.register(CLEARLIST, Boolean.valueOf(false));
		}

		public void setShooter(@Nullable EntityLivingBase shooter) {
			if (shooter != null) {
				this.dataManager.set(SHOOTERID, Integer.valueOf(shooter.getEntityId()));
			} else {
				this.dataManager.set(SHOOTERID, Integer.valueOf(-1));
				this.thrower = null;
			}
		}

		@Nullable
		public EntityLivingBase getShooter() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(SHOOTERID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void skewerEntity(EntityLivingBase entity) {
			this.clearSkewerList(false);
			this.dataManager.set(OTHERENTITYID, Integer.valueOf(entity.getEntityId()));
		}

		@Nullable
		private EntityLivingBase getLastSkeweredEntity() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(OTHERENTITYID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void clearSkewerList(boolean clear) {
			if (clear) {
				this.dataManager.set(OTHERENTITYID, Integer.valueOf(-1));
				for (EntityLivingBase entity : this.skeweredEntities) {
					ProcedureSync.EntityNBTTag.removeAndSync(entity, SKEWERED_TIME);
				}
			}
			this.dataManager.set(CLEARLIST, Boolean.valueOf(clear));
		}

		private boolean shouldClearSkewerList() {
			return ((Boolean)this.dataManager.get(CLEARLIST)).booleanValue();
		}

		@Override
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (OTHERENTITYID.equals(key)) {
				EntityLivingBase entity = this.getLastSkeweredEntity();
				if (entity != null) {
					this.skeweredEntities.add(entity);
				}
			} else if (CLEARLIST.equals(key)) {
				if (this.shouldClearSkewerList()) {
					this.skeweredEntities.clear();
				}
			}
		}

		@Override
		protected void onImpact(RayTraceResult raytraceResultIn) {
			Entity entity = raytraceResultIn.entityHit;
			if (entity != null) {
				if (!entity.equals(this.thrower)) {
					entity.getEntityData().setBoolean("TempData_disableKnockback", true);
					float f = MathHelper.sqrt(this.getVelocitySq()) * (float)this.getDamage();
					if (entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.thrower), f)
					 || entity.equals(this.getLastSkeweredEntity())) {
						if (entity instanceof EntityLivingBase) {
							if (entity.isEntityAlive() && entity.getEntityBoundingBox().getAverageEdgeLength() < 2.0d
							 && !entity.equals(this.getLastSkeweredEntity())) {
								this.skewerEntity((EntityLivingBase)entity);
							}
							this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
							this.motionX *= 0.85d;
							this.motionY *= 0.85d;
							this.motionZ *= 0.85d;
						}
					} else {
						this.motionX *= -0.1d;
						this.motionY *= -0.1d;
						this.motionZ *= -0.1d;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						ReflectionHelper.setPrivateValue(EntityThrowable.class, this, 0, 9); //this.ticksInAir = 0;
					}
				}
			} else {
				BlockPos blockpos = raytraceResultIn.getBlockPos();
				ReflectionHelper.setPrivateValue(EntityThrowable.class, this, blockpos.getX(), 0); //this.xTile = blockpos.getX();
				ReflectionHelper.setPrivateValue(EntityThrowable.class, this, blockpos.getY(), 1); // this.yTile = blockpos.getY();
				ReflectionHelper.setPrivateValue(EntityThrowable.class, this, blockpos.getZ(), 2); // this.zTile = blockpos.getZ();
	            IBlockState iblockstate = this.world.getBlockState(blockpos);
	            ReflectionHelper.setPrivateValue(EntityThrowable.class, this, iblockstate.getBlock(), 3); //this.inTile = iblockstate.getBlock();
	            //ReflectionHelper.setPrivateValue(EntityArrow.class, this, iblockstate.getBlock().getMetaFromState(iblockstate), 6); //this.inData = this.inTile.getMetaFromState(iblockstate);
	            this.motionX = (double)((float)(raytraceResultIn.hitVec.x - this.posX));
	            this.motionY = (double)((float)(raytraceResultIn.hitVec.y - this.posY));
	            this.motionZ = (double)((float)(raytraceResultIn.hitVec.z - this.posZ));
	            float f2 = MathHelper.sqrt(this.getVelocitySq());
	            this.posX -= this.motionX / (double)f2 * 0.05D;
	            this.posY -= this.motionY / (double)f2 * 0.05D;
	            this.posZ -= this.motionZ / (double)f2 * 0.05D;
	            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
	            this.inGround = true;
	            this.throwableShake = 7;
				this.health = 15.0F;
	            if (iblockstate.getMaterial() != Material.AIR) {
	                iblockstate.getBlock().onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
	            }
			}
		}

	    @Override
	    protected float getGravityVelocity() {
	        return 0.05F;
	    }

		private double getVelocitySq() {
			return this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
		}

		protected Vec3d getNeedleEyePos(float pt) {
			Vec3d vec0 = new Vec3d(this.lastTickPosX + (this.posX - this.lastTickPosX) * pt, this.lastTickPosY + (this.posY - this.lastTickPosY) * pt, this.lastTickPosZ + (this.posZ - this.lastTickPosZ) * pt);
			float f0 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * pt;
			float f1 = -this.prevRotationPitch - (this.rotationPitch - this.prevRotationPitch) * pt - 90F;
			return new Vec3d(0d, 2.08d, 0d).rotatePitch(-f1 * (float)Math.PI / 180F).rotateYaw(f0 * (float)Math.PI / 180F).add(vec0);
		}

		public void retrieve(double x, double y, double z, float speed) {
			this.inGround = false;
			this.shoot(x, y, z, speed, 0f);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.doBlockCollisions();
			if (this.thrower != null && !this.thrower.isEntityAlive()) {
				this.setShooter(null);
			}
			if (this.inGround) {
				if ((int)ReflectionHelper.getPrivateValue(EntityThrowable.class, this, 8) > 1198) { // this.ticksInGround
					ReflectionHelper.setPrivateValue(EntityThrowable.class, this, 1000, 8);
				}
			} else if (this.thrower != null && this.getDistance(this.thrower) > 50d) {
				this.motionX *= -0.4d;
				this.motionZ *= -0.4d;
			}
			if (!this.world.isRemote) {
            	ProcedureSync.EntityPositionAndRotation.sendToTracking(this);
			}
			Entity lastEntity = this;
			Iterator<EntityLivingBase> iter = this.skeweredEntities.iterator();
			while (iter.hasNext()) {
				EntityLivingBase entity = iter.next();
				int i = entity.getEntityData().getInteger(SKEWERED_TIME);
				if (this.isTargetable(entity) && i < 300) {
					if (!(entity instanceof EntityPlayer)) {
						entity.getEntityData().setInteger(SKEWERED_TIME, ++i);
					}
					double d = lastEntity.getDistance(entity);
					if (d > 2d) {
						Vec3d vec = lastEntity.getPositionVector().subtract(entity.getPositionVector())
						 .normalize().scale(0.2d * d / 2d);
						entity.addVelocity(vec.x, vec.y, vec.z);
						entity.velocityChanged = true;
					}
					lastEntity = entity;
				} else {
					iter.remove();
					entity.getEntityData().removeTag(SKEWERED_TIME);
				}
			}
		}

		private boolean isTargetable(@Nullable Entity targetIn) {
			return ItemJutsu.canTarget(targetIn);
		}

		@Override
		public void onCollideWithPlayer(EntityPlayer entityIn) {
			if (!this.world.isRemote && this.throwableShake <= 0) {
				boolean flag = false;
				if (this.thrower == null && this.inGround) {
					flag = entityIn.inventory.addItemStackToInventory(this.getArrowStack());
				} else if (entityIn.equals(this.thrower) && this.ticksExisted > 15) {
					flag = entityIn.replaceItemInInventory(ItemNuibariThrown.getSlotId(entityIn), this.getArrowStack());
				}
				if (flag) {
	            	((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketCollectItem(this.getEntityId(), entityIn.getEntityId(), 1));
					this.setDead();
				}
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				this.clearSkewerList(true);
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getTrueSource() instanceof EntityLivingBase) {
				if (!this.inGround) {
					Vec3d vec3d = source.getTrueSource().getLookVec();
					this.motionX = vec3d.x;
					this.motionY = vec3d.y;
					this.motionZ = vec3d.z;
					this.markVelocityChanged();
					return true;
				} else {
					this.health -= amount;
					if (this.health <= 0.0F) {
						this.clearSkewerList(true);
					}
				}
			}
			return false;
		}

		@Override
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}

		protected ItemStack getArrowStack() {
			return new ItemStack(block);
		}

	    public void setDamage(double damageIn) {
	        this.damage = damageIn;
	    }
	
	    public double getDamage() {
	        return this.damage;
	    }

		public static class KnockbackHook {
			@SubscribeEvent
			public void onKnockback(LivingKnockBackEvent event) {
				if (event.getEntity().getEntityData().getBoolean("TempData_disableKnockback")) {
					event.setCanceled(true);
					event.getEntity().getEntityData().removeTag("TempData_disableKnockback");
				}
			}
		}
	}
	
	/*public static class EntityCustom extends EntityArrow {
		private static final DataParameter<Integer> SHOOTERID = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> OTHERENTITYID = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private static final DataParameter<Boolean> CLEARLIST = EntityDataManager.<Boolean>createKey(EntityCustom.class, DataSerializers.BOOLEAN);
		private List<EntityLivingBase> skeweredEntities = Lists.newArrayList();

		public EntityCustom(World a) {
			super(a);
		}

		public EntityCustom(World worldIn, double x, double y, double z) {
			super(worldIn, x, y, z);
		}

		public EntityCustom(World worldIn, EntityLivingBase shooter) {
			super(worldIn, shooter);
			this.setShooter(shooter);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(SHOOTERID, Integer.valueOf(-1));
			this.dataManager.register(OTHERENTITYID, Integer.valueOf(-1));
			this.dataManager.register(CLEARLIST, Boolean.valueOf(false));
		}

		public void setShooter(@Nullable EntityLivingBase shooter) {
			if (shooter != null) {
				this.dataManager.set(SHOOTERID, Integer.valueOf(shooter.getEntityId()));
			} else {
				this.dataManager.set(SHOOTERID, Integer.valueOf(-1));
				this.shootingEntity = null;
			}
		}

		@Nullable
		public EntityLivingBase getShooter() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(SHOOTERID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void skewerEntity(EntityLivingBase entity) {
			this.clearSkewerList(false);
			this.dataManager.set(OTHERENTITYID, Integer.valueOf(entity.getEntityId()));
		}

		@Nullable
		private EntityLivingBase getLastSkeweredEntity() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(OTHERENTITYID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void clearSkewerList(boolean clear) {
			this.dataManager.set(CLEARLIST, Boolean.valueOf(clear));
		}

		private boolean shouldClearSkewerList() {
			return ((Boolean)this.dataManager.get(CLEARLIST)).booleanValue();
		}

		@Nullable
		public void notifyDataManagerChange(DataParameter<?> key) {
			super.notifyDataManagerChange(key);
			if (OTHERENTITYID.equals(key)) {
				EntityLivingBase entity = this.getLastSkeweredEntity();
				if (entity != null) {
					this.skeweredEntities.add(entity);
				}
			} else if (CLEARLIST.equals(key)) {
				if (this.shouldClearSkewerList()) {
					this.skeweredEntities.clear();
				}
			}
		}

		@Override
		protected void onHit(RayTraceResult raytraceResultIn) {
			Entity entity = raytraceResultIn.entityHit;
			if (entity != null) {
				entity.getEntityData().setBoolean("TempData_damageFromThrownNuibari", true);
				float f = MathHelper.sqrt(this.getVelocitySq()) * (float)this.getDamage();
				if (!entity.equals(this.shootingEntity) 
				 && (entity.attackEntityFrom(DamageSource.causeArrowDamage(this, this.shootingEntity), f) ||
				     entity.equals(this.getLastSkeweredEntity()))) {
					if (entity instanceof EntityLivingBase) {
						if (entity.isEntityAlive() && entity.getEntityBoundingBox().getAverageEdgeLength() < 2.0d
						 && !entity.equals(this.getLastSkeweredEntity())) {
							this.skewerEntity((EntityLivingBase)entity);
						}
						this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
						this.motionX *= 0.85d;
						this.motionY *= 0.85d;
						this.motionZ *= 0.85d;
					}
				} else {
					this.motionX *= -0.1d;
					this.motionY *= -0.1d;
					this.motionZ *= -0.1d;
					this.rotationYaw += 180.0F;
					this.prevRotationYaw += 180.0F;
					ReflectionHelper.setPrivateValue(EntityArrow.class, this, 0, 13); //this.ticksInAir = 0;
				}
			} else {
				BlockPos blockpos = raytraceResultIn.getBlockPos();
				ReflectionHelper.setPrivateValue(EntityArrow.class, this, blockpos.getX(), 2); //this.xTile = blockpos.getX();
				ReflectionHelper.setPrivateValue(EntityArrow.class, this, blockpos.getY(), 3); // this.yTile = blockpos.getY();
				ReflectionHelper.setPrivateValue(EntityArrow.class, this, blockpos.getZ(), 4); // this.zTile = blockpos.getZ();
	            IBlockState iblockstate = this.world.getBlockState(blockpos);
	            ReflectionHelper.setPrivateValue(EntityArrow.class, this, iblockstate.getBlock(), 5); //this.inTile = iblockstate.getBlock();
	            ReflectionHelper.setPrivateValue(EntityArrow.class, this, iblockstate.getBlock().getMetaFromState(iblockstate), 6); //this.inData = this.inTile.getMetaFromState(iblockstate);
	            this.motionX = (double)((float)(raytraceResultIn.hitVec.x - this.posX));
	            this.motionY = (double)((float)(raytraceResultIn.hitVec.y - this.posY));
	            this.motionZ = (double)((float)(raytraceResultIn.hitVec.z - this.posZ));
	            float f2 = MathHelper.sqrt(this.getVelocitySq());
	            this.posX -= this.motionX / (double)f2 * 0.05D;
	            this.posY -= this.motionY / (double)f2 * 0.05D;
	            this.posZ -= this.motionZ / (double)f2 * 0.05D;
	            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
	            this.inGround = true;
	            this.arrowShake = 7;
	            this.setIsCritical(false);
	            if (iblockstate.getMaterial() != Material.AIR) {
	                iblockstate.getBlock().onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
	            }
			}
		}

		private double getVelocitySq() {
			return this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
		}

		protected Vec3d getNeedleEyePos(float pt) {
			Vec3d vec0 = new Vec3d(this.lastTickPosX + (this.posX - this.lastTickPosX) * pt, this.lastTickPosY + (this.posY - this.lastTickPosY) * pt, this.lastTickPosZ + (this.posZ - this.lastTickPosZ) * pt);
			float f0 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * pt;
			float f1 = -this.prevRotationPitch - (this.rotationPitch - this.prevRotationPitch) * pt - 90F;
			return new Vec3d(0d, 2.08d, 0d).rotatePitch(-f1 * (float)Math.PI / 180F).rotateYaw(f0 * (float)Math.PI / 180F).add(vec0);
		}

		public void retrieve(double x, double y, double z, float speed) {
			this.inGround = false;
			this.shoot(x, y, z, speed, 0f);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.shootingEntity != null && !this.shootingEntity.isEntityAlive()) {
				this.setShooter(null);
			}
			if (this.inGround) {
				if ((int)ReflectionHelper.getPrivateValue(EntityArrow.class, this, 12) > 1198) { // this.ticksInGround
					ReflectionHelper.setPrivateValue(EntityArrow.class, this, 1000, 12);
				}
			} else if (this.shootingEntity != null && this.getDistance(this.shootingEntity) > 50d) {
				this.motionX *= -0.4d;
				//this.motionY *= -0.4d;
				this.motionZ *= -0.4d;
			}
			Entity lastEntity = this;
			Iterator<EntityLivingBase> iter = this.skeweredEntities.iterator();
			while (iter.hasNext()) {
				EntityLivingBase entity = iter.next();
				if (entity.isEntityAlive()) {
					double d = lastEntity.getDistance(entity);
					if (d > 2d) {
						Vec3d vec = lastEntity.getPositionVector().subtract(entity.getPositionVector())
						 .normalize().scale(0.2d * d / 2d);
						entity.addVelocity(vec.x, vec.y, vec.z);
						entity.velocityChanged = true;
					}
					lastEntity = entity;
				} else {
					iter.remove();
				}
			}
		}

		@Override
		public void onCollideWithPlayer(EntityPlayer entityIn) {
			if (!this.world.isRemote && this.inGround && this.arrowShake <= 0) {
				boolean flag = false;
				if (this.shootingEntity == null) {
					flag = entityIn.inventory.addItemStackToInventory(this.getArrowStack());
				} else if (entityIn.equals(this.shootingEntity)) {
					flag = entityIn.replaceItemInInventory(ItemNuibariThrown.getSlotId(entityIn), this.getArrowStack());
				}
				if (flag) {
					entityIn.onItemPickup(this, 1);
					this.setDead();
				}
			}
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (source.getTrueSource() instanceof EntityLivingBase) {
				if (!this.inGround) {
					Vec3d vec3d = source.getTrueSource().getLookVec();
					this.motionX = vec3d.x;
					this.motionY = vec3d.y;
					this.motionZ = vec3d.z;
					this.markVelocityChanged();
					return true;
				} else {
					this.clearSkewerList(true);
				}
			}
			return false;
		}

		@Override
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}

		@Override
		protected ItemStack getArrowStack() {
			return new ItemStack(block);
		}

		public static class KnockbackHook {
			@SubscribeEvent
			public void onKnockback(LivingKnockBackEvent event) {
				if (event.getEntity().getEntityData().getBoolean("TempData_damageFromThrownNuibari")) {
					event.setCanceled(true);
					event.getEntity().getEntityData().removeTag("TempData_damageFromThrownNuibari");
				}
			}
		}
	}*/

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EntityCustom> {
		protected final Item item;
		private final RenderItem itemRenderer;

		public RenderCustom(RenderManager renderManagerIn, RenderItem itemRendererIn) {
			super(renderManagerIn);
			this.item = block;
			this.itemRenderer = itemRendererIn;
		}

		@Override
		public boolean shouldRender(EntityCustom entity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
			return true;
		}

		@Override
		public void doRender(EntityCustom entity, double x, double y, double z, float entityYaw, float pt) {
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x, (float) y, (float) z);
			GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * pt, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-entity.prevRotationPitch - (entity.rotationPitch - entity.prevRotationPitch) * pt - 90F, 1.0F, 0.0F, 0.0F);
			GlStateManager.enableRescaleNormal();
			this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			this.itemRenderer.renderItem(this.getStackToRender(entity), ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();

			this.renderLineToShooter(entity, pt);
		}

		private void renderLineToShooter(EntityCustom entity, float pt) {
			Vec3d vec1;
			Vec3d vec0 = entity.getNeedleEyePos(pt);
			for (EntityLivingBase otherEntity : entity.skeweredEntities) {
				vec1 = this.getPosVec(otherEntity, pt).addVector(0d, 1d, 0d);
				this.renderLine(vec0, vec1);
				vec0 = vec1;
			}
			EntityLivingBase shooter = entity.getShooter();
			if (shooter != null) {
				this.renderLine(vec0, this.getPosVec(shooter, pt).addVector(0d, 1d, 0d));
			}
		}

		private Vec3d getPosVec(Entity entity, float pt) {
			//return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pt, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pt, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pt);
			Vec3d vec1 = new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ);
			return entity.getPositionVector().subtract(vec1).scale(pt).add(vec1);
		}

		private void renderLine(Vec3d from, Vec3d to) {
			Vec3d vec3d = to.subtract(from);
			float yaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180d / Math.PI));
			float pitch = (float) (-MathHelper.atan2(vec3d.y, MathHelper.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z)) * (180d / Math.PI));
			GlStateManager.pushMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.glLineWidth(1.0f);
			GlStateManager.translate(from.x - this.renderManager.viewerPosX, from.y - this.renderManager.viewerPosY, from.z - this.renderManager.viewerPosZ);
			GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
			double d = vec3d.lengthVector();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.disableLighting();
			bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(150, 150, 150, 100).endVertex();
			bufferbuilder.pos(0.0D, 0.0D, d).color(150, 150, 150, 100).endVertex();
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			GlStateManager.popMatrix();
		}

		private ItemStack getStackToRender(EntityCustom entityIn) {
			return new ItemStack(this.item);
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return TextureMap.LOCATION_BLOCKS_TEXTURE;
		}
	}
}
