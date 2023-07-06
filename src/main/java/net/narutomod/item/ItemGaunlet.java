
package net.narutomod.item;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.Multimap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemGaunlet extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:gaunlet")
	public static final Item block = null;
	public static final int ENTITYID = 403;

	public ItemGaunlet(ElementsNarutomodMod instance) {
		super(instance, 793);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class)
				.id(new ResourceLocation("narutomod", "entitybulletgaunlet"), ENTITYID).name("entitybulletgaunlet").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:gaunlet", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityCustom.class, renderManager -> {
			return new RenderCustom(renderManager);
		});
	}

	public static class RangedItem extends Item {
		public RangedItem() {
			super();
			setMaxDamage(500);
			setFull3D();
			setUnlocalizedName("gaunlet");
			setRegistryName("gaunlet");
			maxStackSize = 1;
			setCreativeTab(TabModTab.tab);
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entityLivingBase, int timeLeft) {
			if (!world.isRemote && entityLivingBase instanceof EntityPlayerMP) {
				EntityPlayerMP entity = (EntityPlayerMP) entityLivingBase;
				float f = net.minecraft.item.ItemBow.getArrowVelocity(this.getMaxItemUseDuration(itemstack) - timeLeft);
				EntityCustom entityarrow = new EntityCustom(world, entity, itemstack);
				entityarrow.shoot(entity.getLookVec().x, entity.getLookVec().y, entity.getLookVec().z, f * 2.0f, 0);
				//entityarrow.setSilent(true);
				entityarrow.setDamage(9d);
				//entityarrow.setKnockbackStrength(0);
				world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvent.REGISTRY
				 .getObject(new ResourceLocation("narutomod:bullet")), SoundCategory.NEUTRAL,
				 0.8f, 1f / (itemRand.nextFloat() * 0.5f + 1f) + f);
				world.spawnEntity(entityarrow);
				ItemStack newstack = new ItemStack(ItemGauntletThrown.block);
				((ItemGauntletThrown.RangedItem)newstack.getItem()).setEntity(newstack, entityarrow);
				entity.replaceItemInInventory(getSlotId(entity), newstack);
				itemstack.damageItem(1, entity);
			}
		}

		@Override
		public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
			Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
			if (slot == EntityEquipmentSlot.MAINHAND) {
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 8.0d, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.8d, 0));
			}
			return multimap;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.gauntlet.shoot"));
		}

		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			stack.damageItem(1, attacker);
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
		private double damage;
		private ItemStack stack;
		private EntityLivingBase grabbedEntity;
		private Vec3d grabbedOffset;
		private double chainMaxLength = 25.0d;

		public EntityCustom(World a) {
			super(a);
			this.setSize(0.5F, 0.5F);
		}

		public EntityCustom(World worldIn, EntityLivingBase shooter, ItemStack stackIn) {
			super(worldIn, shooter);
			this.setShooter(shooter);
			this.stack = stackIn.copy();
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

		private boolean setGrabbedEntity(@Nullable EntityLivingBase target) {
			if (target == null) {
				this.grabbedEntity = null;
				this.grabbedOffset = null;
			} else {
				Vec3d vec0 = this.getPositionVector();
				Vec3d vec1 = vec0.addVector(this.motionX, this.motionY, this.motionZ);
				RayTraceResult res = target.getEntityBoundingBox().grow(0.3D).calculateIntercept(vec0, vec1);
				if (res != null) {
					this.grabbedEntity = target;
					this.grabbedOffset = res.hitVec.subtract(target.getPositionVector()); //.rotateYaw(target.renderYawOffset * 0.0174533f);
					this.motionX = 0.0d;
					this.motionY = 0.0d;
					this.motionZ = 0.0d;
					return true;
				}
			}
			return false;
		}

		@Override
		protected void onHit(RayTraceResult raytraceResultIn) {
			Entity entity = raytraceResultIn.entityHit;
			if (entity != null) {
				EntityLivingBase shooter = this.getShooter();
				if (!entity.equals(shooter) && !entity.equals(this.grabbedEntity)) {
					if (this.grabbedEntity == null && entity instanceof EntityLivingBase) {
						this.setGrabbedEntity((EntityLivingBase)entity);
					}
					float f = MathHelper.sqrt(this.getVelocitySq()) * (float)this.getDamage();
					if (entity.attackEntityFrom(DamageSource.causeThrownDamage(this, shooter), f)) {
						if (entity instanceof EntityLivingBase) {
							this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
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
	            if (iblockstate.getMaterial() != Material.AIR) {
	                iblockstate.getBlock().onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
	            }
			}
		}

		private double getVelocitySq() {
			return this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
		}

		public void retrieve() {
			if (this.chainMaxLength > 2.0d) {
				this.chainMaxLength -= 0.5d;
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.doBlockCollisions();
			if (this.shootingEntity != null && !this.shootingEntity.isEntityAlive()) {
				this.setShooter(null);
			}
			if (this.inGround) {
				if ((int)ReflectionHelper.getPrivateValue(EntityArrow.class, this, 12) > 1198) { // this.ticksInGround
					ReflectionHelper.setPrivateValue(EntityArrow.class, this, 1000, 12);
				}
			}
			if (!this.world.isRemote && this.grabbedEntity != null) {
				Vec3d vec = this.grabbedEntity.getPositionVector().add(this.grabbedOffset);
				this.setPosition(vec.x, vec.y, vec.z);
			}
			if (this.shootingEntity != null && this.getDistance(this.shootingEntity) > this.chainMaxLength) {
				Vec3d vec = this.getPositionVector().subtract(this.shootingEntity.getPositionVector()).normalize().scale(0.15d);
				if (this.inGround || this.grabbedEntity != null) {
					this.shootingEntity.addVelocity(vec.x, vec.y, vec.z);
					this.shootingEntity.velocityChanged = true;
				}
				if (this.grabbedEntity != null) {
					vec = vec.scale(-1.5d);
					this.grabbedEntity.addVelocity(vec.x, vec.y, vec.z);
					this.grabbedEntity.velocityChanged = true;
				} else {
					this.motionX *= -0.4d;
					this.motionY *= -0.4d;
					this.motionZ *= -0.4d;
				}
			}
			if (!this.world.isRemote && this.stack == null) {
				this.setDead();
			}
		}

		private boolean isTargetable(@Nullable Entity targetIn) {
			return ItemJutsu.canTarget(targetIn);
		}

		@Override
		public void onCollideWithPlayer(EntityPlayer entityIn) {
			if (!this.world.isRemote && this.arrowShake <= 0) {
				boolean flag = false;
				ItemStack stack = this.getArrowStack();
				entityIn.getCooldownTracker().setCooldown(stack.getItem(), 30);
				if (this.shootingEntity == null && this.inGround) {
					flag = entityIn.inventory.addItemStackToInventory(stack);
				} else if (entityIn.equals(this.shootingEntity) && this.ticksExisted > 15) {
					flag = entityIn.replaceItemInInventory(ItemGauntletThrown.getSlotId(entityIn), stack);
				}
				if (flag) {
	            	((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketCollectItem(this.getEntityId(), entityIn.getEntityId(), 1));
					this.setDead();
				}
			}
		}

		@Override
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}

		protected ItemStack getArrowStack() {
			return this.stack;
		}

	    public void setDamage(double damageIn) {
	        this.damage = damageIn;
	    }
	
	    public double getDamage() {
	        return this.damage;
	    }

	    @Override
	    public void writeEntityToNBT(NBTTagCompound compound) {
	    	super.writeEntityToNBT(compound);
	    	if (this.stack != null) {
	    		compound.setTag("itemstack", this.stack.writeToNBT(new NBTTagCompound()));
	    	}
	    }

	    @Override
	    public void readEntityFromNBT(NBTTagCompound compound) {
	    	super.readEntityFromNBT(compound);
	    	if (compound.hasKey("itemstack", 10)) {
	    		this.stack = new ItemStack(compound.getCompoundTag("itemstack"));
	    	}
	    }
	}

	public static class RenderCustom extends Render<EntityCustom> {
		private final ResourceLocation texture = new ResourceLocation("narutomod:textures/gauntlet_metal_entity.png");
		private final ResourceLocation chainTexture = new ResourceLocation("narutomod:textures/gauntlet_chain.png");
		private final ModelGauntletMetal model = new ModelGauntletMetal();

		public RenderCustom(RenderManager renderManager) {
			super(renderManager);
			shadowSize = 0.1f;
		}

		@Override
		public void doRender(EntityCustom bullet, double d, double d1, double d2, float f, float f1) {
			this.bindEntityTexture(bullet);
			GlStateManager.pushMatrix();
			if (bullet.grabbedEntity != null) {
				Vec3d vec0 = this.getPosVec(bullet.grabbedEntity, f1);
				//float f2 = ProcedureUtils.interpolateRotation(bullet.grabbedEntity.prevRenderYawOffset, bullet.grabbedEntity.renderYawOffset, f1);
				//float f2 = bullet.grabbedEntity.renderYawOffset;
				//Vec3d vec = vec0.add(bullet.grabbedOffset.rotateYaw(f2));
				Vec3d vec = vec0.add(bullet.grabbedOffset);
				f = -ProcedureUtils.getYawFromVec(vec0.x - vec.x, vec0.z - vec.z);
				bullet.setLocationAndAngles(vec.x, vec.y, vec.z, f, 0f);
				d = vec.x - this.renderManager.viewerPosX;
				d1 = vec.y - this.renderManager.viewerPosY;
				d2 = vec.z - this.renderManager.viewerPosZ;
			}
			GlStateManager.translate((float) d, (float) d1, (float) d2);
			GlStateManager.rotate(f, 0, 1, 0);
			GlStateManager.rotate(90f - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * f1, 1, 0, 0);
			this.model.render(bullet, 0, 0, 0, 0, 0, 0.0625f);
			GlStateManager.popMatrix();
			this.renderLineToShooter(bullet, f1);
		}

		private void renderLineToShooter(EntityCustom entity, float pt) {
			EntityLivingBase shooter = entity.getShooter();
			if (shooter != null) {
				ModelBiped model = (ModelBiped)((RenderLivingBase)this.renderManager.getEntityRenderObject(shooter)).getMainModel();
				Vec3d vec = this.transform3rdPerson(new Vec3d(0.0d, -0.5825d, 0.0d),
				 new Vec3d(model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ),
				 shooter, pt).addVector(0.0d, 0.275d, 0.0d);
				this.renderLine(this.getPosVec(entity, pt), vec);
			}
		}

		private Vec3d transform3rdPerson(Vec3d startvec, Vec3d angles, EntityLivingBase entity, float pt) {
			return ProcedureUtils.rotateRoll(startvec, (float)-angles.z).rotatePitch((float)-angles.x).rotateYaw((float)-angles.y)
			   .addVector(0.0586F * -6F, 1.02F-(entity.isSneaking()?0.3f:0f), 0.0F)
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
			this.bindTexture(this.chainTexture);
			GlStateManager.pushMatrix();
			GlStateManager.disableCull();
			GlStateManager.translate(from.x - this.renderManager.viewerPosX, from.y - this.renderManager.viewerPosY, from.z - this.renderManager.viewerPosZ);
			GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
			double d = vec3d.lengthVector();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.disableLighting();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			for (double d1 = 0.0D; d1 < d; d1 += 0.2263D) {
				bufferbuilder.pos(0.0D, 0.0D, d1).tex(0.0D, 1.0D).endVertex();
				bufferbuilder.pos(0.0D, 0.11315D, d1 + 0.11315D).tex(0.0D, 0.0D).endVertex();
				bufferbuilder.pos(0.0D, 0.0D, d1 + 0.2263D).tex(1.0D, 0.0D).endVertex();
				bufferbuilder.pos(0.0D, -0.11315D, d1 + 0.11315D).tex(1.0D, 1.0D).endVertex();
				bufferbuilder.pos(0.0D, 0.0D, d1 + 0.11315D).tex(0.0D, 1.0D).endVertex();
				bufferbuilder.pos(0.11315D, 0.0D, d1 + 0.2263D).tex(0.0D, 0.0D).endVertex();
				bufferbuilder.pos(0.0D, 0.0D, d1 + 0.33945D).tex(1.0D, 0.0D).endVertex();
				bufferbuilder.pos(-0.11315D, 0.0D, d1 + 0.2263D).tex(1.0D, 1.0D).endVertex();
			}
			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityCustom entity) {
			return this.texture;
		}
	}

	// Made with Blockbench 4.7.4
	// Exported for Minecraft version 1.7 - 1.12
	// Paste this class into your mod and generate all required imports
	public static class ModelGauntletMetal extends ModelBase {
		private final ModelRenderer claw;
		private final ModelRenderer finger1;
		private final ModelRenderer cube_r1;
		private final ModelRenderer cube_r2;
		private final ModelRenderer finger2;
		private final ModelRenderer cube_r3;
		private final ModelRenderer cube_r4;
		private final ModelRenderer finger3;
		private final ModelRenderer cube_r5;
		private final ModelRenderer cube_r6;
		private final ModelRenderer finger4;
		private final ModelRenderer cube_r7;
		private final ModelRenderer cube_r8;
		private final ModelRenderer finger5;
		private final ModelRenderer cube_r9;
		private final ModelRenderer cube_r10;
		private final ModelRenderer bone;
		private final ModelRenderer hexadecagon_r1;
		private final ModelRenderer hexadecagon_r2;
		private final ModelRenderer hexadecagon_r3;
		private final ModelRenderer hexadecagon_r4;
		private final ModelRenderer bolts;
		private final ModelRenderer hexadecagon_r5;
		private final ModelRenderer hexadecagon_r6;
		private final ModelRenderer chain;
		public ModelGauntletMetal() {
			textureWidth = 64;
			textureHeight = 64;
			claw = new ModelRenderer(this);
			claw.setRotationPoint(0.0F, 0.0F, 0.0F);
			setRotationAngle(claw, 0.0F, -1.5708F, 0.0F);
			claw.cubeList.add(new ModelBox(claw, 9, 0, -2.0F, 3.0F, -2.0F, 4, 1, 4, 0.0F, false));
			finger1 = new ModelRenderer(this);
			finger1.setRotationPoint(-2.2F, 3.0F, 1.9F);
			claw.addChild(finger1);
			setRotationAngle(finger1, 0.3927F, 0.0F, 0.7854F);
			cube_r1 = new ModelRenderer(this);
			cube_r1.setRotationPoint(0.7F, 1.5F, -2.15F);
			finger1.addChild(cube_r1);
			setRotationAngle(cube_r1, 0.0F, 0.7854F, 0.0F);
			cube_r1.cubeList.add(new ModelBox(cube_r1, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));
			cube_r2 = new ModelRenderer(this);
			cube_r2.setRotationPoint(0.9929F, 2.0F, -0.0287F);
			finger1.addChild(cube_r2);
			setRotationAngle(cube_r2, -0.7854F, 0.0F, 0.0F);
			cube_r2.cubeList.add(new ModelBox(cube_r2, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			finger2 = new ModelRenderer(this);
			finger2.setRotationPoint(-2.2F, 3.0F, 0.65F);
			claw.addChild(finger2);
			setRotationAngle(finger2, 0.1309F, 0.0F, 0.7854F);
			cube_r3 = new ModelRenderer(this);
			cube_r3.setRotationPoint(0.7F, 1.5F, -2.15F);
			finger2.addChild(cube_r3);
			setRotationAngle(cube_r3, 0.0F, 0.7854F, 0.0F);
			cube_r3.cubeList.add(new ModelBox(cube_r3, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));
			cube_r4 = new ModelRenderer(this);
			cube_r4.setRotationPoint(0.9929F, 2.0F, -0.0287F);
			finger2.addChild(cube_r4);
			setRotationAngle(cube_r4, -0.7854F, 0.0F, 0.0F);
			cube_r4.cubeList.add(new ModelBox(cube_r4, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			finger3 = new ModelRenderer(this);
			finger3.setRotationPoint(-2.2F, 3.0F, -0.6F);
			claw.addChild(finger3);
			setRotationAngle(finger3, -0.1309F, 0.0F, 0.7854F);
			cube_r5 = new ModelRenderer(this);
			cube_r5.setRotationPoint(0.7F, 1.5F, -2.15F);
			finger3.addChild(cube_r5);
			setRotationAngle(cube_r5, 0.0F, 0.7854F, 0.0F);
			cube_r5.cubeList.add(new ModelBox(cube_r5, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));
			cube_r6 = new ModelRenderer(this);
			cube_r6.setRotationPoint(0.9929F, 2.0F, -0.0287F);
			finger3.addChild(cube_r6);
			setRotationAngle(cube_r6, -0.7854F, 0.0F, 0.0F);
			cube_r6.cubeList.add(new ModelBox(cube_r6, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			finger4 = new ModelRenderer(this);
			finger4.setRotationPoint(-2.2F, 3.0F, -1.85F);
			claw.addChild(finger4);
			setRotationAngle(finger4, -0.3927F, 0.0F, 0.7854F);
			cube_r7 = new ModelRenderer(this);
			cube_r7.setRotationPoint(0.7F, 1.5F, -2.15F);
			finger4.addChild(cube_r7);
			setRotationAngle(cube_r7, 0.0F, 0.7854F, 0.0F);
			cube_r7.cubeList.add(new ModelBox(cube_r7, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));
			cube_r8 = new ModelRenderer(this);
			cube_r8.setRotationPoint(0.9929F, 2.0F, -0.0287F);
			finger4.addChild(cube_r8);
			setRotationAngle(cube_r8, -0.7854F, 0.0F, 0.0F);
			cube_r8.cubeList.add(new ModelBox(cube_r8, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			finger5 = new ModelRenderer(this);
			finger5.setRotationPoint(1.8F, 3.0F, -1.85F);
			claw.addChild(finger5);
			setRotationAngle(finger5, -1.933F, -0.7519F, 1.8241F);
			cube_r9 = new ModelRenderer(this);
			cube_r9.setRotationPoint(0.7F, 1.5F, -2.15F);
			finger5.addChild(cube_r9);
			setRotationAngle(cube_r9, 0.0F, 0.7854F, 0.0F);
			cube_r9.cubeList.add(new ModelBox(cube_r9, 16, 7, -2.5F, -1.75F, 0.5F, 1, 3, 1, 0.0F, true));
			cube_r10 = new ModelRenderer(this);
			cube_r10.setRotationPoint(0.9929F, 2.0F, -0.0287F);
			finger5.addChild(cube_r10);
			setRotationAngle(cube_r10, -0.7854F, 0.0F, 0.0F);
			cube_r10.cubeList.add(new ModelBox(cube_r10, 16, 5, -1.75F, -0.5F, -0.5F, 3, 1, 1, 0.0F, false));
			bone = new ModelRenderer(this);
			bone.setRotationPoint(0.0F, 1.5F, 0.0F);
			claw.addChild(bone);
			bone.cubeList.add(new ModelBox(bone, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			bone.cubeList.add(new ModelBox(bone, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			bone.cubeList.add(new ModelBox(bone, 0, 10, -3.5F, -1.5F, -0.6962F, 7, 3, 1, 0.0F, false));
			bone.cubeList.add(new ModelBox(bone, 0, 10, -3.5F, -1.5F, -0.2962F, 7, 3, 1, 0.0F, false));
			hexadecagon_r1 = new ModelRenderer(this);
			hexadecagon_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone.addChild(hexadecagon_r1);
			setRotationAngle(hexadecagon_r1, 0.0F, -0.3927F, 0.0F);
			hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 10, -3.5F, -1.5F, -0.2962F, 7, 3, 1, 0.0F, false));
			hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 10, -3.5F, -1.5F, -0.6962F, 7, 3, 1, 0.0F, false));
			hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			hexadecagon_r1.cubeList.add(new ModelBox(hexadecagon_r1, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			hexadecagon_r2 = new ModelRenderer(this);
			hexadecagon_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone.addChild(hexadecagon_r2);
			setRotationAngle(hexadecagon_r2, 0.0F, 0.3927F, 0.0F);
			hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 10, -3.5F, -1.5F, -0.2962F, 7, 3, 1, 0.0F, false));
			hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 10, -3.5F, -1.5F, -0.6962F, 7, 3, 1, 0.0F, false));
			hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			hexadecagon_r2.cubeList.add(new ModelBox(hexadecagon_r2, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			hexadecagon_r3 = new ModelRenderer(this);
			hexadecagon_r3.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone.addChild(hexadecagon_r3);
			setRotationAngle(hexadecagon_r3, 0.0F, -0.7854F, 0.0F);
			hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			hexadecagon_r3.cubeList.add(new ModelBox(hexadecagon_r3, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			hexadecagon_r4 = new ModelRenderer(this);
			hexadecagon_r4.setRotationPoint(0.0F, 0.0F, 0.0F);
			bone.addChild(hexadecagon_r4);
			setRotationAngle(hexadecagon_r4, 0.0F, 0.7854F, 0.0F);
			hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -0.3038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			hexadecagon_r4.cubeList.add(new ModelBox(hexadecagon_r4, 0, 0, -0.7038F, -1.5F, -3.5F, 1, 3, 7, 0.0F, false));
			bolts = new ModelRenderer(this);
			bolts.setRotationPoint(0.0F, -2.5F, 0.0F);
			claw.addChild(bolts);
			hexadecagon_r5 = new ModelRenderer(this);
			hexadecagon_r5.setRotationPoint(0.0F, 4.0F, 0.0F);
			bolts.addChild(hexadecagon_r5);
			setRotationAngle(hexadecagon_r5, 0.0F, -0.7854F, 0.0F);
			hexadecagon_r5.cubeList.add(new ModelBox(hexadecagon_r5, 0, 0, -0.4544F, -0.5F, 3.0F, 1, 1, 1, 0.0F, false));
			hexadecagon_r6 = new ModelRenderer(this);
			hexadecagon_r6.setRotationPoint(0.0F, 4.0F, 0.0F);
			bolts.addChild(hexadecagon_r6);
			setRotationAngle(hexadecagon_r6, 0.0F, 0.7854F, 0.0F);
			hexadecagon_r6.cubeList.add(new ModelBox(hexadecagon_r6, 0, 0, -0.4544F, -0.5F, -4.0F, 1, 1, 1, 0.0F, true));
			chain = new ModelRenderer(this);
			chain.setRotationPoint(0.0F, 0.0F, 0.0F);
			setRotationAngle(chain, 0.0F, 0.0F, 0.7854F);
			chain.cubeList.add(new ModelBox(chain, 26, 1, -1.5F, -1.5F, -0.5F, 3, 3, 1, -0.4F, false));
		}

		@Override
		public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
			claw.render(f5);
			chain.render(f5);
		}

		public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}
	}
}
