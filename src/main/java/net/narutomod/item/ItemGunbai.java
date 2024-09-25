
package net.narutomod.item;

import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;

import java.util.Map;
import java.util.HashMap;
import javax.annotation.Nullable;
import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemGunbai extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:gunbai")
	public static final Item block = null;
	public static final int ENTITYID = 390;
	private static final String USE_BLOCKING_MODEL = "UseBlockingModel";
	private static final String USE_THROWN_MODEL = "UseThrownModel";

	public ItemGunbai(ElementsNarutomodMod instance) {
		super(instance, 769);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
		 .id(new ResourceLocation("narutomod", "entitygunbai"), ENTITYID).name("entitygunbai").tracker(64, 1, true).build());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new ForgeEventHook());
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
	    final ModelResourceLocation main_res = new ModelResourceLocation("narutomod:gunbai", "inventory");
	    final ModelResourceLocation blocking_res = new ModelResourceLocation("narutomod:gunbai_blocking", "inventory");
	    final ModelResourceLocation thrown_res = new ModelResourceLocation("narutomod:gunbai_thrown", "inventory");

   	    ModelBakery.registerItemVariants(block, main_res, blocking_res, thrown_res);

	    ModelLoader.setCustomMeshDefinition(block, new ItemMeshDefinition() {
	    	private final ModelResourceLocation main = main_res;
	    	private final ModelResourceLocation blocking = blocking_res;
	    	private final ModelResourceLocation thrown = thrown_res;
	        @Override
	        public ModelResourceLocation getModelLocation(ItemStack stack) {
	        	if (stack.hasTagCompound()) {
		            if (stack.getTagCompound().getBoolean(USE_BLOCKING_MODEL)) {
		                return this.blocking;
		            }
		            if (stack.getTagCompound().getBoolean(USE_THROWN_MODEL)) {
		                return this.thrown;
		            }
	        	}
	            return this.main;
	        }
	    });
	}

	public static class RangedItem extends Item implements ItemOnBody.Interface {
		public RangedItem() {
			super();
			this.setMaxDamage(2500);
			this.setFull3D();
			this.setUnlocalizedName("gunbai");
			this.setRegistryName("gunbai");
			this.maxStackSize = 1;
			this.setCreativeTab(TabModTab.tab);
		}

		@Override
		public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Ranged item modifier", 19.0d, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Ranged item modifier", -2.4, 0));
			}
			return multimap;
		}

		public void setEntity(ItemStack stack, EntityCustom entity) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setInteger("gunbaiEntityId", entity.getEntityId());
		}

		@Nullable
		public EntityCustom getEntity(World world, ItemStack stack) {
			Entity entity = world.getEntityByID(stack.hasTagCompound() ? stack.getTagCompound().getInteger("gunbaiEntityId") : -1);
			return entity instanceof EntityCustom ? (EntityCustom)entity : null;
		}

		@Override
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			if (entityIn instanceof EntityLivingBase && ((EntityLivingBase)entityIn).isHandActive()
			 && ((EntityLivingBase)entityIn).getActiveItemStack().equals(stack) && !stack.getTagCompound().getBoolean(USE_THROWN_MODEL)) {
				if (worldIn.isRemote && !stack.getTagCompound().getBoolean(USE_BLOCKING_MODEL)) {
					stack.getTagCompound().setBoolean(USE_BLOCKING_MODEL, true);
				}
			} else if (stack.getTagCompound().hasKey(USE_BLOCKING_MODEL)) {
				stack.getTagCompound().removeTag(USE_BLOCKING_MODEL);
			}
			if (!worldIn.isRemote && stack.getTagCompound().getBoolean(USE_THROWN_MODEL) && entityIn.ticksExisted % 20 == 19) {
				EntityCustom itemEntity = this.getEntity(worldIn, stack);
				if (itemEntity == null || !entityIn.equals(itemEntity.getShooter())) {
					stack.shrink(1);
				}
			}
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
			if (!world.isRemote && this.isThrown(itemstack)) {
				EntityCustom itemEntity = this.getEntity(world, itemstack);
				if (itemEntity != null && entity.equals(itemEntity.getShooter())) {
			        itemEntity.retrieve(entity);
				}
			}
		}

		@Override
		public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
			if (!attacker.world.isRemote && attacker.equals(target)) {
				if (itemstack.hasTagCompound() && !this.isThrown(itemstack)) {
					itemstack.getTagCompound().setBoolean(USE_THROWN_MODEL, true);
					EntityCustom entityarrow = new EntityCustom(attacker.world, attacker);
					Vec3d vec = attacker.getLookVec();
					entityarrow.shoot(vec.x, vec.y, vec.z, 2.0f, 0);
					entityarrow.setDamage(20d);
					attacker.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
							SoundCategory.NEUTRAL, 1, 1f / (itemRand.nextFloat() * 0.5f + 1f) + 1f);
					attacker.world.spawnEntity(entityarrow);
					this.setEntity(itemstack, entityarrow);
					entityarrow.gunbaiStack = itemstack;
				}
				return true;
			}
			return super.onLeftClickEntity(itemstack, attacker, target);
		}

		private boolean isThrown(ItemStack stack) {
			return stack.hasTagCompound() && stack.getTagCompound().getBoolean(USE_THROWN_MODEL);
		}

		@Override
		public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
			return true;
		}

		@Override
		public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
			return stack.getItem() == block;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return this.isThrown(itemstack) ? EnumAction.BOW : EnumAction.BLOCK;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return 72000;
		}

		@Override
		public ItemOnBody.BodyPart showOnBody(ItemStack stack) {
			return this.isThrown(stack) ? ItemOnBody.BodyPart.NONE : ItemOnBody.BodyPart.TORSO;
		}
	}

	public class ForgeEventHook {
		@SubscribeEvent
		public void onTossItem(ItemTossEvent event) {
			EntityItem entityitem = event.getEntityItem();
			if (entityitem != null) {
				ItemStack stack = entityitem.getItem();
				if (stack.getItem() == block && ((RangedItem)stack.getItem()).isThrown(stack)) {
					event.setCanceled(true);
					EntityCustom entity = ((RangedItem)stack.getItem()).getEntity(entityitem.world, stack);
					if (entity != null) {
						entity.setShooter(null);
					}
				}
			}
		}
	}

	public static class EntityCustom extends EntityArrow {
		private static final DataParameter<Integer> SHOOTERID = EntityDataManager.<Integer>createKey(EntityCustom.class, DataSerializers.VARINT);
		private final double chainMaxLength = 32.0d;
		private double damage;
		private ItemStack gunbaiStack;

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
				this.shootingEntity = shooter;
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
					if (entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), f)) {
						float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
						if (f1 > 0.0f) {
							entity.addVelocity(this.motionX * 1.2D / f1, 0.1D, this.motionZ * 1.2D / f1);
						}
						this.motionX *= 0.85d;
						this.motionY *= 0.85d;
						this.motionZ *= 0.85d;
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
			return new Vec3d(0d, 2.5d, 0d).rotatePitch(-f1 * (float)Math.PI / 180F).rotateYaw(f0 * (float)Math.PI / 180F).add(vec0);
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
	            	((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketCollectItem(this.getEntityId(), entityIn.getEntityId(), 1));
				} else if (this.gunbaiStack != null && entityIn.equals(this.shootingEntity) && this.ticksExisted > 15) {
					ItemStack stack = ProcedureUtils.getMatchingItemStack(entityIn, this.gunbaiStack);
					if (stack != null && stack.getItem() == block && ((RangedItem)stack.getItem()).isThrown(stack)) {
						stack.getTagCompound().removeTag(USE_THROWN_MODEL);
						flag = true;
					}
				}
				if (flag) {
					this.setDead();
				}
			}
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
