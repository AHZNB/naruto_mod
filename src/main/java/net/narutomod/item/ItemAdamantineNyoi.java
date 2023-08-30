
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.entity.EntityAdamantinePrison;
import net.narutomod.entity.EntityScalableProjectile;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.Particles;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Multimap;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ItemAdamantineNyoi extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:adamantine_nyoi")
	public static final Item block = null;
	public static final int ENTITYID = 425;
	public static final ItemJutsu.JutsuEnum WEAPON = new ItemJutsu.JutsuEnum(0, "tooltip.adamantinenyoi.block", 'D', new RangedItem.Jutsu());
	public static final ItemJutsu.JutsuEnum EXTEND = new ItemJutsu.JutsuEnum(1, "tooltip.adamantinenyoi.extend", 'D', 50d, new EntityExtend.Jutsu());
	public static final ItemJutsu.JutsuEnum PRISON = new ItemJutsu.JutsuEnum(2, "adamantine_prison", 'D', 50d, new EntityAdamantinePrison.EC.Jutsu());

	public ItemAdamantineNyoi(ElementsNarutomodMod instance) {
		super(instance, 851);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(WEAPON, EXTEND, PRISON));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityExtend.class)
				.id(new ResourceLocation("narutomod", "adamantine_nyoi"), ENTITYID).name("adamantine_nyoi")
				.tracker(96, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:adamantine_nyoi", "inventory"));
	}

	public static ItemStack createStackBoundTo(EntityPlayer player) {
		ItemStack stack = ProcedureUtils.getMatchingItemStack(player, ItemSummoningContract.block);
		if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("AdamantineNyoiItemstack", 10)) {
			stack = new ItemStack(stack.getTagCompound().getCompoundTag("AdamantineNyoiItemstack"));
		} else {
			stack = new ItemStack(block);
			RangedItem item = (RangedItem)stack.getItem();
			item.setOwner(stack, player);
			item.setIsAffinity(stack, true);
			item.addJutsuXp(stack, WEAPON, item.getRequiredXp(stack, WEAPON));
			item.addJutsuXp(stack, EXTEND, item.getRequiredXp(stack, EXTEND));
			item.addJutsuXp(stack, PRISON, item.getRequiredXp(stack, PRISON));
		}
		stack.getTagCompound().setLong("1stGottenTime", player.world.getTotalWorldTime());
		return stack;
	}

	public static class RangedItem extends ItemJutsu.Base implements ItemOnBody.Interface {
		private static final UUID REACH_MODIFIER = UUID.fromString("2181075f-90e8-4444-9143-788f588ef58f");
		private static final float DAMAGE = 18.0f;

		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.OTHER, list);
			this.setUnlocalizedName("adamantine_nyoi");
			this.setRegistryName("adamantine_nyoi");
			this.setCreativeTab(null);
			this.defaultCooldownMap[WEAPON.index] = 0;
			this.defaultCooldownMap[EXTEND.index] = 0;
			this.defaultCooldownMap[PRISON.index] = 0;
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", DAMAGE - 1, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
				multimap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(REACH_MODIFIER, "Tool modifier", 1.5d, 0));
			}
			return multimap;
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			super.onUsingTick(stack, player, timeLeft);
			if (this.getCurrentJutsu(stack) == EXTEND && !player.world.isRemote) {
				float power = this.getPower(stack, player, timeLeft);
				if (power >= this.getMaxPower(stack, player)) {
					player.stopActiveHand();
				} else {
					EntityExtend entity = this.getStaffEntity(player.world, stack);
					if (entity == null) {
						entity = new EntityExtend(player);
						player.world.spawnEntity(entity);
						this.setStaffEntity(stack, entity);
					}
					entity.setEntityScale(power);
				}
			}
		}

		@Override
		protected void onUsingEffects(EntityLivingBase player) {
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && world.getTotalWorldTime() > itemstack.getTagCompound().getLong("1stGottenTime") + 6000) {
				if (entity instanceof EntityPlayer) {
					ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)entity, ItemSummoningContract.block);
					if (stack != null && stack.hasTagCompound()) {
						stack.getTagCompound().setTag("AdamantineNyoiItemstack", itemstack.writeToNBT(new NBTTagCompound()));
					}
				}
				ProcedureUtils.poofWithSmoke(entity);
				itemstack.shrink(1);
			}
		}

		private void setStaffEntity(ItemStack stack, EntityExtend entity) {
			if (stack.hasTagCompound()) {
				stack.getTagCompound().setInteger("staffEntityId", entity.getEntityId());
			}
		}

		@Nullable
		private EntityExtend getStaffEntity(World world, ItemStack stack) {
			Entity entity = world.getEntityByID(stack.getTagCompound().getInteger("staffEntityId"));
			return entity instanceof EntityExtend && !entity.isDead ? (EntityExtend)entity : null;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			return this.getCurrentJutsu(stack) == EXTEND ? this.getPower(stack, entity, timeLeft, 1.0f, 30f) : 1.0f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			float ret = super.getMaxPower(stack, entity);
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			return jutsu == EXTEND ? Math.min(ret, 15f) : ret;
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return this.getCurrentJutsu(itemstack) == WEAPON ? EnumAction.BLOCK : EnumAction.BOW;
		}

		@Override
		public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
			return stack.getItem() == block && this.getCurrentJutsu(stack) == WEAPON;
		}

		@Override
		public ItemOnBody.BodyPart showOnBody() {
			return ItemOnBody.BodyPart.LEFT_ARM;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				return false;
			}
		}
	}

	public static class EntityExtend extends EntityScalableProjectile.Base {
		private static final DataParameter<Integer> FRONT = EntityDataManager.<Integer>createKey(EntityExtend.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> SHOOTERID = EntityDataManager.<Integer>createKey(EntityExtend.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> SEGMENT = EntityDataManager.<Integer>createKey(EntityExtend.class, DataSerializers.VARINT);
		private final EntityExtend[] segment = new EntityExtend[60];
		private final int lifeSpan = 300;
		private final float lengthMultiplier = 2.0f;
		private double renderTick;
		private boolean checked;
		private final float damage = RangedItem.DAMAGE;
		private final ProcedureUtils.CollisionHelper collisionhelper = new ProcedureUtils.CollisionHelper(this);

		public EntityExtend(World a) {
			super(a);
			this.setOGSize(0.125f, 0.125f);
			this.setNoGravity(true);
		}

		public EntityExtend(EntityLivingBase shooter) {
			super(shooter);
			this.setShooter(shooter);
			this.setOGSize(0.125f, 0.125f);
			this.setFront(this);
			this.setSegmentIndex(0);
			this.segment[0] = this;
			for (int i = 1; i < this.segment.length; i++) {
				this.segment[i] = new EntityExtend(this.world);
				this.segment[i].setSegmentIndex(i);
				this.segment[i].setFront(this);
			}
			this.rotationYaw = shooter.rotationYaw;
			this.rotationPitch = shooter.rotationPitch;
			this.setSegmentPosition();
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.getDataManager().register(FRONT, Integer.valueOf(-1));
			this.getDataManager().register(SHOOTERID, Integer.valueOf(-1));
			this.getDataManager().register(SEGMENT, Integer.valueOf(-1));
		}

	    @Nullable
	    public EntityExtend getFront() {
        	Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(FRONT)).intValue());
        	return entity instanceof EntityExtend ? (EntityExtend)entity : null;
	    }

	    public void setFront(EntityExtend entity) {
	        this.dataManager.set(FRONT, Integer.valueOf(entity.getEntityId()));
	    }

		private void setShooter(EntityLivingBase shooter) {
			this.getDataManager().set(SHOOTERID, Integer.valueOf(shooter.getEntityId()));
		}

		public EntityLivingBase getShooter() {
			if (!this.world.isRemote) {
				return this.shootingEntity;
			}
			Entity entity = this.world.getEntityByID(((Integer)this.getDataManager().get(SHOOTERID)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

	    private void setSegmentIndex(int i) {
	    	this.getDataManager().set(SEGMENT, Integer.valueOf(i));
	    }

	    private int getSegmentIndex() {
	    	return ((Integer)this.getDataManager().get(SEGMENT)).intValue();
	    }

	    private float updateRotation(float p_75652_1_, float p_75652_2_, float p_75652_3_) {
	        float f = MathHelper.wrapDegrees(p_75652_2_ - p_75652_1_);
	        if (f > p_75652_3_) {
	            f = p_75652_3_;
	        }
	        if (f < -p_75652_3_) {
	            f = -p_75652_3_;
	        }
	        return p_75652_1_ + f;
	    }

		private void setSegmentPosition() {
			EntityLivingBase shooter = this.getShooter();
			if (!this.world.isRemote && shooter != null) {
				float scale = this.getEntityScale();
				Vec3d vec0 = shooter.getLookVec();
				Vec3d frontLook = Vec3d.fromPitchYaw(this.updateRotation(this.rotationPitch, ProcedureUtils.getPitchFromVec(vec0), 5.0f),
				 this.updateRotation(this.rotationYaw, ProcedureUtils.getYawFromVec(vec0), 5.0f));
				Vec3d frontVec = frontLook.add(shooter.getPositionVector().addVector(0d, 1.1d - scale * 0.0625f, 0d));
				if (this.segment[0] != null) {
					for (int i = 1; i < this.segment.length; i++) {
						this.segment[i].checked = false;
					}
					for (int i = 1; i < this.segment.length; i++) {
						Vec3d vec = frontLook.scale(scale * this.lengthMultiplier * 3.75f * i / this.segment.length).add(frontVec);
						this.segment[i].setEntityScale(scale);
						if (this.segment[i].isAddedToWorld() && this.ticksAlive < this.lifeSpan - 30 && !this.segment[i].checked) {
							this.segment[i].checked = true;
							Vec3d vec1 = vec.subtract(this.segment[i].getPositionVector());
							this.segment[i].collisionhelper.collideWithAll(vec1.x, vec1.y, vec1.z, new Predicate<Entity>() {
								@Override
								public boolean apply(@Nullable Entity p_apply_1_) {
									return ItemJutsu.canTarget(p_apply_1_) && !(p_apply_1_ instanceof EntityExtend)
									 && !p_apply_1_.equals(shooter);
								}
							});
							Vec3d vec2 = this.segment[i].collisionhelper.getUpdatedMotion();
							float f = MathHelper.sqrt((float)vec2.lengthVector() * scale);
							for (Map.Entry<Entity, EnumFacing> entry : this.segment[i].collisionhelper.getEntitiesHitMap().entrySet()) {
								entry.getKey().attackEntityFrom(DamageSource.causeIndirectDamage(this, shooter), f * this.damage * 1.75f);
								ProcedureUtils.CollisionHelper.reposHitEntity(this.segment[i].getEntityBoundingBox().offset(vec2), entry.getKey(), entry.getValue());
								entry.getKey().addVelocity(vec2.x, vec2.y, vec2.z);
								entry.getKey().velocityChanged = true;
							}
							if (this.segment[i].collisionhelper.anyBlockHits()) {
								if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, shooter)) {
									for (BlockPos pos : this.segment[i].collisionhelper.getHitBlocks()) {
										float f1 = this.world.getBlockState(pos).getBlockHardness(this.world, pos);
										if (f1 >= 0.0f && f1 <= f) {
											this.world.destroyBlock(pos, this.rand.nextFloat() < 0.1f);
										}
									}
								}
								for (EnumFacing face : EnumFacing.VALUES) {
									if (this.segment[i].collisionhelper.hitOnSide(face)) {
										vec2 = vec2.addVector(-0.5d * face.getDirectionVec().getX(),
										 -0.5d * face.getDirectionVec().getY(), -0.5d * face.getDirectionVec().getZ());
									}
								}
								frontLook = this.segment[i].getPositionVector().add(vec2).subtract(frontVec).normalize();
								frontVec = frontLook.add(shooter.getPositionVector().addVector(0d, 1.1d - scale * 0.0625f, 0d));
								i = 1;
								continue;
							}
						}
						this.segment[i].setLocationAndAngles(vec.x, vec.y, vec.z, this.rotationYaw, this.rotationPitch);
					}
				}
				this.setLocationAndAngles(frontVec.x, frontVec.y, frontVec.z,
				 ProcedureUtils.getYawFromVec(frontLook), ProcedureUtils.getPitchFromVec(frontLook));
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				if (this.getSegmentIndex() == 0) {
					this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:poof")), 1f, 1f);
				}
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY+this.height/2, this.posZ, 5,
				 this.width * 0.5d, this.height * 0.3d, this.width * 0.5d, 0d, 0d, 0d, 0xD0FFFFFF, (int)(this.getEntityScale() * 10));
				if (this.segment[0] != null) {
					for (int i = 1; i < this.segment.length; i++) {
						this.segment[i].setDead();
					}
				}
			}
		}

		@Override
		public void onUpdate() {
			EntityExtend front = this.getFront();
			boolean isFront = this.equals(front);
			if (!this.world.isRemote && isFront && !this.segment[1].isAddedToWorld()) {
				for (int i = 1; i < this.segment.length; i++) {
					this.world.spawnEntity(this.segment[i]);
				}
			}
			super.onUpdate();
			if (isFront && this.ticksAlive >= this.lifeSpan - 30) {
				float f = this.getEntityScale();
				this.setEntityScale(f - (f - 1f) * (1f - (float)(this.lifeSpan - this.ticksAlive) / 30f));
			}
			if (isFront) {
				this.setSegmentPosition();
			}
			if (!this.world.isRemote && (this.ticksAlive >= this.lifeSpan || front == null || front.isDead)) {
				this.setDead();
			}
		}

		@Override
		public void onImpact(RayTraceResult result) {
		}

		@Override
		public boolean isImmuneToExplosions() {
			return true;
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				((RangedItem)stack.getItem()).setCurrentJutsuCooldown(stack, 300);
				return true;
			}
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
			RenderingRegistry.registerEntityRenderingHandler(EntityExtend.class, renderManager -> {
				return new RenderCustom(renderManager);
			});
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EntityExtend> {
			protected final ItemStack item;
			private final RenderItem itemRenderer;
	
			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.item = new ItemStack(block);
				this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
			}

			@Override
			public boolean shouldRender(EntityExtend livingEntity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
				return true;
			}
	
		    @Override
		    public void doRender(EntityExtend entity, double x, double y, double z, float entityYaw, float partialTicks) {
		    	EntityExtend entity1 = entity.getFront();
		    	float offset = 1.0F;
		    	if (entity1 != null) {
		    		double d = (double)entity.world.getTotalWorldTime() + partialTicks;
		    		if (d <= entity1.renderTick) {
		    			return;
		    		}
		    		entity1.renderTick = d;
		    		if (!entity.equals(entity1)) {
			    		entity = entity1;
			    		x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - this.renderManager.viewerPosX;
			    		y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - this.renderManager.viewerPosY;
			    		z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - this.renderManager.viewerPosZ;
		    		}
		    	} else {
		    		int i = entity.getSegmentIndex();
		    		if (i != entity.segment.length / 2 && i != entity.segment.length - 1) {
		    			return;
		    		}
		    		offset = 1.0F - (float)i / entity.segment.length * 2;
		    	}
		    	float scale = entity.getEntityScale();
		        GlStateManager.pushMatrix();
		        GlStateManager.translate((float)x, (float)y, (float)z);
		        GlStateManager.rotate(-ProcedureUtils.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks), 0.0F, 1.0F, 0.0F);
		        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
		        GlStateManager.translate(0.0F, scale * 0.0625F, scale * entity.lengthMultiplier * 1.875F * offset);
		        GlStateManager.scale(scale, scale, scale * entity.lengthMultiplier);
		        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		        this.itemRenderer.renderItem(this.item, ItemCameraTransforms.TransformType.GROUND);
		        GlStateManager.popMatrix();
		    }
		
			@Override
		    protected ResourceLocation getEntityTexture(EntityExtend entity) {
		        return TextureMap.LOCATION_BLOCKS_TEXTURE;
		    }
		}
	}
}
