
package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

//import java.util.List;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class ItemScytheHidan extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:scythe_hidan")
	public static final Item block = null;
	public static final int ENTITYID = 398;

	public ItemScytheHidan(ElementsNarutomodMod instance) {
		super(instance, 778);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletscythe_hidan"), ENTITYID).name("entitybulletscythe_hidan").tracker(64, 1, true)
				.build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:scythe_hidan", "inventory"));
	}

	public static class RangedItem extends Item implements ItemOnBody.Interface {
		public RangedItem() {
			super();
			this.setMaxDamage(500);
			this.setFull3D();
			this.setUnlocalizedName("scythe_hidan");
			this.setRegistryName("scythe_hidan");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", 11.0d, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
			}
			return multimap;
		}

		/*@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.hidan.general"));
		}*/

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
			if (!world.isRemote) {
				itemstack.damageItem(1, entity);
				if (!itemstack.isEmpty()) {
					float f = net.minecraft.item.ItemBow.getArrowVelocity(this.getMaxItemUseDuration(itemstack) - timeLeft);
					EntityCustom entityarrow = new EntityCustom(world, entity);
					Vec3d vec = entity.getLookVec();
					if (entity instanceof EntityLiving && ((EntityLiving)entity).getAttackTarget() != null) {
						vec = ((EntityLiving)entity).getAttackTarget().getPositionEyes(1f)
						 .subtract(entity.posX, entity.posY + entity.getEyeHeight() - 0.1f, entity.posZ);
						vec = vec.addVector(0d, MathHelper.sqrt(vec.x * vec.x + vec.z * vec.z) * 0.1d, 0d);
					}
					entityarrow.shoot(vec.x, vec.y, vec.z, f * 2.0f, 0);
					//entityarrow.setSilent(true);
					entityarrow.setDamage(12d);
					//entityarrow.setKnockbackStrength(0);
					world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
							SoundCategory.NEUTRAL, 1, 1f / (itemRand.nextFloat() * 0.5f + 1f) + f);
					world.spawnEntity(entityarrow);
					entityarrow.setItemStack(itemstack);
					ItemStack newstack = new ItemStack(ItemScytheHidanThrown.block);
					((ItemScytheHidanThrown.RangedItem)newstack.getItem()).setEntity(newstack, entityarrow);
					if (entity instanceof EntityPlayer) {
						((EntityPlayer)entity).replaceItemInInventory(getSlotId((EntityPlayer)entity), newstack);
					} else if (entity.getHeldItemMainhand().getItem() == block) {
						entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, newstack);
					} else if (entity.getHeldItemOffhand().getItem() == block) {
						entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, newstack);
					}
				}
			}
		}

		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			stack.damageItem(1, attacker);
			return true;
		}

		@Override
		public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
			return true;
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

	public static class EntityCustom extends EntityArrow {
		private static final DataParameter<Integer> SHOOTERID = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private final double chainMaxLength = 32.0d;
		private double damage;
		private EntityLivingBase hitTarget;
		private ItemStack itemstack = ItemStack.EMPTY;

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

		protected void setItemStack(ItemStack stack) {
			this.itemstack = stack.copy();
		}

		@Nullable
		public EntityLivingBase getHitTarget() {
			return this.hitTarget;
		}

		public boolean inGround() {
			return this.inGround;
		}

		@Override
		protected void onHit(RayTraceResult raytraceResultIn) {
			Entity entity = raytraceResultIn.entityHit;
			if (entity != null) {
				if (!entity.equals(this.shootingEntity) && this.isTargetable(entity)) {
					float f = MathHelper.sqrt(this.getVelocitySq()) * (float)this.getDamage() * 0.7f;
					this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					if (entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), f)) {
						this.motionX *= 0.85d;
						this.motionY *= 0.85d;
						this.motionZ *= 0.85d;
						if (entity instanceof EntityLivingBase) {
							this.hitTarget = (EntityLivingBase)entity;
						}
					} else {
						this.motionX *= -0.1d;
						this.motionY *= -0.1d;
						this.motionZ *= -0.1d;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						ReflectionHelper.setPrivateValue(EntityArrow.class, this, 0, 13); //this.ticksInAir = 0;
					}
				}
			} else if (this.arrowShake <= 0) {
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
			return new Vec3d(0d, 2.6d, 0d).rotatePitch(-f1 * (float)Math.PI / 180F).rotateYaw(f0 * (float)Math.PI / 180F).add(vec0);
		}

		protected void retrieve(double x, double y, double z, float speed) {
			this.arrowShake = 7;
			this.inGround = false;
			ReflectionHelper.setPrivateValue(EntityArrow.class, this, 0, 2); //this.xTile = blockpos.getX();
			ReflectionHelper.setPrivateValue(EntityArrow.class, this, 0, 3); // this.yTile = blockpos.getY();
			ReflectionHelper.setPrivateValue(EntityArrow.class, this, 0, 4); // this.zTile = blockpos.getZ();
			this.shoot(x, y, z, speed, 0f);
		}

		public void retrieve(Entity entity) {
	        double d0 = entity.posX - this.posX;
	        double d1 = entity.getEntityBoundingBox().minY + (double)entity.height * 0.333d - this.posY;
	        double d2 = entity.posZ - this.posZ;
	        double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	        this.retrieve(d0, d1 + d3 * 0.3D, d2, (float)MathHelper.sqrt(d3) * 0.3F);
		}

		@Override
		public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
			super.shoot(x, y, z, velocity, inaccuracy);
			this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:chainsound")),
			 1f, this.rand.nextFloat() * 0.3f + 0.8f);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.doBlockCollisions();
			if (this.shootingEntity != null && !this.shootingEntity.isEntityAlive()) {
				this.setShooter(null);
			}
			if (this.inGround && (int)ReflectionHelper.getPrivateValue(EntityArrow.class, this, 12) > 1198) { // this.ticksInGround
				ReflectionHelper.setPrivateValue(EntityArrow.class, this, 1000, 12);
			}
			if (this.shootingEntity != null && this.getDistance(this.shootingEntity) > this.chainMaxLength) {
				double d = this.getDistance(this.shootingEntity) - this.chainMaxLength;
				Vec3d vec = this.getPositionVector().subtract(this.shootingEntity.getPositionVector()).normalize();
				if (this.inGround) {
					Vec3d vec1 = vec.scale(0.1d * d);
					this.shootingEntity.addVelocity(vec1.x, vec1.y, vec1.z);
					this.shootingEntity.velocityChanged = true;
				} else if (this.arrowShake <= 0) {
					this.retrieve(this.shootingEntity);
				}
			}
		}

		private boolean isTargetable(@Nullable Entity targetIn) {
			return ItemJutsu.canTarget(targetIn);
		}

		@Override
		public void onCollideWithPlayer(EntityPlayer entityIn) {
			if (!this.world.isRemote && this.arrowShake <= 0) {
				boolean flag = false;
				if (this.shootingEntity == null && this.inGround) {
					flag = entityIn.inventory.addItemStackToInventory(this.getArrowStack());
				} else if (entityIn.equals(this.shootingEntity) && this.ticksExisted > 15) {
					flag = entityIn.replaceItemInInventory(ItemScytheHidanThrown.getSlotId(entityIn), this.getArrowStack());
				}
				if (flag) {
	            	((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketCollectItem(this.getEntityId(), entityIn.getEntityId(), 1));
					this.setDead();
				}
			}
		}

		@Override
		protected ItemStack getArrowStack() {
			return this.itemstack;
		}

	    public void setDamage(double damageIn) {
	        this.damage = damageIn;
	    }
	
	    public double getDamage() {
	        return this.damage;
	    }

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			if (compound.hasKey("itemStack")) {
				this.itemstack = new ItemStack((NBTTagCompound)compound.getTag("itemStack"));
			}
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			if (this.itemstack != null) {
				compound.setTag("itemstack", this.itemstack.writeToNBT(new NBTTagCompound()));
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
			RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
				return new RenderCustom(renderManager, Minecraft.getMinecraft().getRenderItem());
			});
		}

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
				EntityLivingBase shooter = entity.getShooter();
				if (shooter != null) {
					Vec3d vec0 = entity.getNeedleEyePos(pt);
					ModelBiped model = (ModelBiped)((RenderLivingBase)this.renderManager.getEntityRenderObject(shooter)).getMainModel();
					Vec3d vec1 = this.transform3rdPerson(new Vec3d(0.0d, -0.5825d, 0.0d),
					 new Vec3d(model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ),
					 shooter, pt).addVector(0.0d, 0.275d, 0.0d);
					this.renderLine(vec0, vec1);
				}
			}
	
			private Vec3d transform3rdPerson(Vec3d startvec, Vec3d angles, EntityLivingBase entity, float pt) {
				//return ProcedureUtils.rotateRoll(startvec, (float)-angles.z).rotatePitch((float)-angles.x).rotateYaw((float)-angles.y)
				return new ProcedureUtils.RotationMatrix().rotateZ((float)-angles.z).rotateY((float)-angles.y).rotateX((float)-angles.x)
				   .transform(startvec).addVector(0.0586F * -6F, 1.02F-(entity.isSneaking()?0.3f:0f), 0.0F)
				   .rotateYaw(-ProcedureUtils.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, pt) * (float)(Math.PI / 180d))
				   .add(this.getPosVec(entity, pt));
			}
	
			private Vec3d getPosVec(Entity entity, float pt) {
				Vec3d vec1 = new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ);
				return entity.getPositionVector().subtract(vec1).scale(pt).add(vec1);
			}
	
			private void renderLine(Vec3d from, Vec3d to) {
				Vec3d vec3d = to.subtract(from);
				float yaw = (float) (MathHelper.atan2(vec3d.x, vec3d.z) * (180d / Math.PI));
				float pitch = (float) (-MathHelper.atan2(vec3d.y, MathHelper.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z)) * (180d / Math.PI));
				GlStateManager.pushMatrix();
				GlStateManager.disableTexture2D();
				GlStateManager.glLineWidth(8.0f);
				GlStateManager.translate(from.x - this.renderManager.viewerPosX, from.y - this.renderManager.viewerPosY, from.z - this.renderManager.viewerPosZ);
				GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
				double d = vec3d.lengthVector();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				GlStateManager.disableLighting();
				bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
				for (double d1 = 0.0d; d1 < d; d1 += 0.2d) {
					bufferbuilder.pos(0.0D, 0.0D, d1).color(0.1f, 0.1f, 0.1f, 1.0f).endVertex();
					bufferbuilder.pos(0.0D, 0.0D, d1 + 0.025d).color(0.1f, 0.1f, 0.1f, 1.0f).endVertex();
					bufferbuilder.pos(0.0D, 0.0D, d1 + 0.025d).color(0.3f, 0.3f, 0.3f, 1.0f).endVertex();
					bufferbuilder.pos(0.0D, 0.0D, d1 + 0.2d).color(0.3f, 0.3f, 0.3f, 1.0f).endVertex();
				}
				tessellator.draw();
				GlStateManager.enableLighting();
				GlStateManager.glLineWidth(1.0f);
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
}


	