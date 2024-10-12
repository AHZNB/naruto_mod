
package net.narutomod.entity;

import net.narutomod.item.ItemIshiken;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemSenjutsu;
import net.narutomod.procedure.ProcedureCameraShake;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.NarutomodMod;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import io.netty.buffer.ByteBuf;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.glu.GLU;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.Lists;

@ElementsNarutomodMod.ModElement.Tag
public class EntityGamarinsho extends ElementsNarutomodMod.ModElement {
	public static final int ENTITY1ID = 39;
	public static final int ENTITY2ID = 463;
	public static final int ENTITY3ID = 464;
	private static final float TOAD_SCALE = 10.0F;

	public EntityGamarinsho(ElementsNarutomodMod instance) {
		super(instance, 894);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "gamarinsho"), ENTITY1ID).name("gamarinsho").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityToadSamurai.class)
		 .id(new ResourceLocation("narutomod", "gamarinsho_toad"), ENTITY2ID).name("gamarinsho_toad").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityDuplicate.class)
		 .id(new ResourceLocation("narutomod", "gamarinsho_playerdup"), ENTITY3ID).name("gamarinsho_playerdup").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> CASTER = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private final List<EntityLivingBase> trappedList = Lists.newArrayList();
		private final double jutsuRadius = 80.0d;
		private final int prepareTime = 300;
		private final int genjutsuDuration = 400;
		private EntityToadFukasaku.EntityCustom toadPa;
		private EntityToadShima.EntityCustom toadMa;
		private boolean renderCube;

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
		}

		private EC(World world, boolean render) {
			this(world);
			this.renderCube = render;
		}

		public EC(EntityLivingBase casterIn) {
			this(casterIn.world);
			this.setCaster(casterIn);
			this.setIdlePosition();
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.SENJUTSU;
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(CASTER, Integer.valueOf(-1));
		}

		private void setCaster(EntityLivingBase shooter) {
			this.getDataManager().set(CASTER, Integer.valueOf(shooter.getEntityId()));
		}

		@Nullable
		protected EntityLivingBase getCaster() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(CASTER)).intValue());
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void setIdlePosition() {
			EntityLivingBase caster = this.getCaster();
			if (caster != null) {
				this.setPosition(caster.posX, caster.posY, caster.posZ);
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase caster = this.getCaster();
				if (caster != null) {
					Jutsu.deActivateCleanup(caster);
					if (caster instanceof EntityPlayer) {
						((EntityPlayer)caster).inventory.clearMatchingItems(ItemIshiken.block, -1, -1, null);
					}
				}
				if (this.toadPa != null) {
					this.toadPa.setDead();
				}
				if (this.toadMa != null) {
					this.toadMa.setDead();
				}
			}
		}

		private void setCooldown(EntityLivingBase caster) {
			if (caster instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)caster, ItemSenjutsu.block);
				if (stack != null) {
					((ItemJutsu.Base)stack.getItem()).setJutsuCooldown(stack, ItemSenjutsu.GAMARINSHO, 3600);
				}
			}
		}

		@Override 
		public void onUpdate() {
			EntityLivingBase caster = this.getCaster();
			if (!this.world.isRemote && (caster == null || this.ticksExisted > this.prepareTime + this.genjutsuDuration
			 || (this.toadPa != null && !this.toadPa.isEntityAlive()) || (this.toadMa != null && !this.toadMa.isEntityAlive())
			 || (caster instanceof EntityPlayer && this.ticksExisted < this.prepareTime && !ItemSenjutsu.isSageModeActivated((EntityPlayer)caster)))) {
				this.setDead();
			} else {
				this.setIdlePosition();
				if (this.ticksExisted == 1 && !this.world.isRemote) {
					this.toadPa = new EntityToadFukasaku.EntityCustom(caster);
					this.toadPa.setLocationAndAngles(caster.posX, caster.posY, caster.posZ, caster.rotationYaw, 0.0f);
					this.toadPa.setSwingingArms(true);
					this.world.spawnEntity(this.toadPa);
					this.toadMa = new EntityToadShima.EntityCustom(caster);
					this.toadMa.setLocationAndAngles(caster.posX, caster.posY, caster.posZ, caster.rotationYaw, 0.0f);
					this.toadMa.setSwingingArms(true);
					this.world.spawnEntity(this.toadMa);
				}
				if (this.ticksExisted < this.prepareTime && this.ticksExisted % 60 == 20) {
					this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:toadchant")), (float)this.ticksExisted / 60f, this.rand.nextFloat() * 0.3F + 0.8F);
				}
				if (this.ticksExisted == this.prepareTime) {
					for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(this.jutsuRadius, 36d, this.jutsuRadius))) {
						if (entity != caster && (entity instanceof EntityPlayer || entity instanceof EntityNinjaMob.Base)) {
							this.trappedList.add(entity);
						}
					}
					if (!this.world.isRemote) {
						this.setCooldown(caster);
						this.toadPa.setSwingingArms(false);
						this.toadMa.setSwingingArms(false);
						for (EntityLivingBase entity : this.trappedList) {
							if (entity instanceof EntityPlayerMP) {
								Genjutsu.activate((EntityPlayerMP)entity, this);
							} else if (entity instanceof EntityNinjaMob.Base) {
								((EntityNinjaMob.Base)entity).haltAIfor(this.genjutsuDuration);
							}
						}
					}
				} else if (this.ticksExisted == this.prepareTime + 1 && caster instanceof EntityPlayerMP) {
					ProcedureUtils.swapItemToSlot((EntityPlayer)caster, EntityEquipmentSlot.MAINHAND, new ItemStack(ItemIshiken.block));
					Genjutsu.activate((EntityPlayerMP)caster, this);
				}
			}
		}

		public List<EntityLivingBase> getTrappedEntities() {
			return this.trappedList;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "GamarinshoEntityIdKey";

			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				EC entity1 = this.getEntity(entity);
				if (entity1 == null) {
					entity1 = new EC(entity);
					entity.world.spawnEntity(entity1);
					entity.getEntityData().setInteger(ID_KEY, entity1.getEntityId());
					return true;
				} else {
					entity1.setDead();
				}
				return false;
			}

			public static EC getEntity(EntityLivingBase user) {
				Entity entity = user.world.getEntityByID(user.getEntityData().getInteger(ID_KEY));
				return entity instanceof EC ? (EC)entity : null;
			}

			private static void deActivateCleanup(EntityLivingBase entity) {
				entity.getEntityData().removeTag(ID_KEY);
			}

			@Override
			public boolean isActivated(EntityLivingBase entity) {
				return this.getEntity(entity) != null;
			}
		}
	}

	public static class EntityToadSamurai extends EntityToad.EntityCustom {
		public EntityToadSamurai(World world) {
			super(world);
			this.postScaleFixup();
		}

		@Override
		public float getScale() {
			return TOAD_SCALE;
		}
	}

	public static class EntityDuplicate extends EntityClone.Base {
		public EntityDuplicate(World world) {
			super(world);
		}

		public EntityDuplicate(EntityLivingBase user) {
			super(user);
		}

		@Override
		public void onUpdate() {
			EntityLivingBase summoner = this.getSummoner();
			if (summoner != null) {
				this.prevRotationYaw = this.rotationYaw;
				this.prevRotationYawHead = this.rotationYawHead;
				this.prevRenderYawOffset = this.renderYawOffset;
				this.prevRotationPitch = this.rotationPitch;
				this.rotationYaw = summoner.rotationYaw;
				this.rotationYawHead = summoner.rotationYawHead;
				this.renderYawOffset = summoner.renderYawOffset;
				this.rotationPitch = summoner.rotationPitch;
			}
			for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox())) {
				entity.applyEntityCollision(this);
			}
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.6d;
			this.motionY *= 0.6d;
			this.motionZ *= 0.6d;
		}
	}

	public static class Genjutsu {
		public static void activate(EntityPlayerMP player, EC entity) {
			Message.sendTo(player, entity);
		}
		
	    @SideOnly(Side.CLIENT)
	    private static void activate(EC entity) {
	        MinecraftForge.EVENT_BUS.register(new GenjutsuOverlayHandler(entity));
	    }
	
	    @SideOnly(Side.CLIENT)
	    private static class GenjutsuOverlayHandler {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/brainmatterog.png");
			private final EntityDuplicate playerDup;
			private final List<EntityDuplicate> trappedEntity = Lists.newArrayList();
			private final EntityToadSamurai toadEntity;
			private final EC cubeEntity;
			private final EC jutsuEntity;
			public final int sphereId;
			private final long startTime;
	        private final long endTime;
	        private final double posX;
	        private final double posY;
	        private final double posZ;
	        private final boolean isCaster;
	
	        public GenjutsuOverlayHandler(EC entity) {
				this.sphereId = GLAllocation.generateDisplayLists(1);
				GlStateManager.glNewList(this.sphereId, 0x1300);
	        	Sphere sphere = new Sphere();
				sphere.setDrawStyle(GLU.GLU_FILL);
				sphere.setNormals(GLU.GLU_SMOOTH);
				sphere.setOrientation(GLU.GLU_INSIDE);
				sphere.setTextureFlag(true);
				sphere.draw(1.0F, 32, 32);
				GlStateManager.glEndList();

	        	Minecraft mc = Minecraft.getMinecraft();
				this.toadEntity = new EntityToadSamurai(mc.world);
				this.cubeEntity = new EC(mc.world, true);
				this.startTime = mc.world.getTotalWorldTime();
	            this.endTime = this.startTime + entity.genjutsuDuration;
	            this.posX = entity.posX;
	            this.posY = entity.posY + 256.0d;
	            this.posZ = entity.posZ;
	            this.jutsuEntity = entity;
	            this.isCaster = entity.getCaster() == mc.player;
	        	this.playerDup = new EntityDuplicate(mc.player);
	        	if (this.isCaster) {
	        		this.playerDup.setLocationAndAngles(this.posX + 3.0d, this.posY - 2.0d, this.posZ + 3.0d, mc.player.rotationYaw, mc.player.rotationPitch);
	        		for (EntityLivingBase trapped : this.jutsuEntity.getTrappedEntities()) {
	        			EntityDuplicate dup = new EntityDuplicate(trapped);
	        			dup.setLocationAndAngles(this.posX + (dup.getRNG().nextFloat()-0.5f), this.posY, this.posZ + (dup.getRNG().nextFloat()-0.5f), trapped.rotationYaw, trapped.rotationPitch);
	        			dup.setNoGravity(true);
	        			mc.world.spawnEntity(dup);
	        			this.trappedEntity.add(dup);
	        		}
	        	} else {
	        		this.playerDup.setLocationAndAngles(this.posX, this.posY, this.posZ, mc.player.rotationYaw, mc.player.rotationPitch);
					mc.player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 80, 0));
					ProcedureCameraShake.sendToClient(mc.player, 80, 40.0f);
	        	}
	        	this.playerDup.setNoGravity(true);
	        	mc.world.spawnEntity(this.playerDup);
	        }
	
	        @SubscribeEvent
	        public void renderGenjutsu(RenderWorldLastEvent event) {
	        	Minecraft mc = Minecraft.getMinecraft();
	        	long worldTime = mc.world.getTotalWorldTime();
                if (worldTime > this.endTime) {
                    MinecraftForge.EVENT_BUS.unregister(this);
                    mc.setRenderViewEntity(mc.player);
                    this.playerDup.setDead();
                    for (EntityDuplicate dup : this.trappedEntity) {
                    	dup.setDead();
                    }
                } else {
				    double x = this.posX - mc.getRenderManager().viewerPosX;
				    double y = this.posY - mc.getRenderManager().viewerPosY;
				    double z = this.posZ - mc.getRenderManager().viewerPosZ;
					GlStateManager.pushMatrix();
					GlStateManager.enableLighting();
					GlStateManager.translate(x, y, z);
					GlStateManager.enableRescaleNormal();
					GlStateManager.scale(30.0F, 30.0F, 30.0F);
					GlStateManager.disableCull();
					GlStateManager.enableTexture2D();
					mc.renderEngine.bindTexture(this.texture);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)0x80, (float)0x80);
					GlStateManager.disableBlend();
					GlStateManager.callList(this.sphereId);
					//GlStateManager.enableBlend();
					GlStateManager.enableCull();
					GlStateManager.disableLighting();
					GlStateManager.popMatrix();
					
		            long ticksElapsed = worldTime - this.startTime;
		            this.renderToad(mc, event.getPartialTicks(), x, y, z, EnumFacing.NORTH, 12.0D);
		            this.renderToad(mc, event.getPartialTicks(), x, y, z, EnumFacing.SOUTH, 12.0D);
		            this.renderToad(mc, event.getPartialTicks(), x, y, z, EnumFacing.EAST, 12.0D);
		            this.renderToad(mc, event.getPartialTicks(), x, y, z, EnumFacing.WEST, 12.0D);
		            this.renderCube(mc, x, y - 0.2D, z, (int)ticksElapsed, event.getPartialTicks());

		            if (this.isCaster) {
	            		mc.setRenderViewEntity(mc.player.getEntityData().getBoolean(NarutomodModVariables.JutsuKey2Pressed) ? this.playerDup : mc.player);
		            } else {
		            	mc.player.getEntityData().setInteger("FearEffect", 5);
	        			mc.setRenderViewEntity(ticksElapsed < 60 && ticksElapsed % ((80-ticksElapsed) / 10) != 0 ? mc.player : this.playerDup);
	        		}
                }
	        }

			private void renderToad(Minecraft mc, float partialTicks, double x, double y, double z, EnumFacing facing, double offset) {
			    x = x + offset * facing.getDirectionVec().getX() + (double)facing.getDirectionVec().getZ();
			    y = y + offset * facing.getDirectionVec().getY();
			    z = z + offset * facing.getDirectionVec().getZ() - (double)facing.getDirectionVec().getX();
		        this.toadEntity.rotationYaw = facing.getOpposite().getHorizontalAngle();
		        mc.getRenderManager().renderEntity(this.toadEntity, x, y - this.toadEntity.height * 0.4f, z, this.toadEntity.rotationYaw, partialTicks, false);
			}

			private void renderCube(Minecraft mc, double x, double y, double z, int ticksElapsed, float partialTicks) {
				this.cubeEntity.ticksExisted = ticksElapsed;
		        mc.getRenderManager().renderEntity(this.cubeEntity, x, y, z, 0.0F, partialTicks, false);
			}
	    }

		public static class Message implements IMessage {
			int id;

			public Message() {
			}
	
			public Message(EC entity) {
				this.id = entity.getEntityId();
			}

			public static void sendTo(EntityPlayerMP target, EC entity) {
				NarutomodMod.PACKET_HANDLER.sendTo(new Message(entity), target);
			}
	
			public static class Handler implements IMessageHandler<Message, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(Message message, MessageContext context) {
					Minecraft mc = Minecraft.getMinecraft();
					mc.addScheduledTask(() -> {
						Entity entity = mc.world.getEntityByID(message.id);
						if (entity instanceof EC) {
							Genjutsu.activate((EC)entity);
						}
					});
					return null;
				}
			}
	
			public void toBytes(ByteBuf buf) {
				buf.writeInt(this.id);
			}
	
			public void fromBytes(ByteBuf buf) {
				this.id = buf.readInt();
			}
		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
		this.elements.addNetworkMessage(Genjutsu.Message.Handler.class, Genjutsu.Message.class, Side.CLIENT);
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderEC(renderManager));
			RenderingRegistry.registerEntityRenderingHandler(EntityToadSamurai.class, renderManager -> new RenderToad(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderEC extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/water1.png");
			private final ModelCubeSimple model = new ModelCubeSimple();

			public RenderEC(RenderManager rendermanager) {
				super(rendermanager);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (entity.renderCube) {
					float f8 = partialTicks + entity.ticksExisted;
					this.bindTexture(this.texture);
					GlStateManager.pushMatrix();
					GlStateManager.translate(x, y, z);
					GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.scale(3.0F, 3.0F, 3.0F);
					this.model.render(entity, 0.0F, 0.0F, f8, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
				}
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class RenderToad extends Render<EntityToadSamurai> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/toad2.png");
			private final ModelToadSamurai model = new ModelToadSamurai();
	
			public RenderToad(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.shadowSize = TOAD_SCALE * 0.8F;
			}
	
			@Override
			public void doRender(EntityToadSamurai entity, double x, double y, double z, float entityYaw, float partialTicks) {
				float f8 = partialTicks + entity.ticksExisted;
				this.bindTexture(this.texture);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);
				GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.enableRescaleNormal();
				GlStateManager.scale(-1.0F, -1.0F, 1.0F);
				GlStateManager.translate(0.0F, -1.501F, 0.0F);
				//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)0xF0, (float)0xF0);
				GlStateManager.disableLighting();
				this.model.scale = TOAD_SCALE;
				this.model.render(entity, 0.0F, 0.0F, f8, 0.0F, 0.0F, 0.0625F);
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
	
			@Override
			protected ResourceLocation getEntityTexture(EntityToadSamurai entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelCubeSimple extends ModelBase {
			private final ModelRenderer bone;
		
			public ModelCubeSimple() {
				textureWidth = 64;
				textureHeight = 64;
				bone = new ModelRenderer(this);
				bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				bone.cubeList.add(new ModelBox(bone, 0, 0, -8.0F, -16.0F, -8.0F, 16, 16, 16, 0.0F, false));
			}
		
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.translate(0.0F, f2 * 0.01F, 0.0F);
				GlStateManager.matrixMode(5888);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				bone.render(f5);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
			}
		}

		// Made with Blockbench 4.10.4
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelToadSamurai extends EntityToad.ModelToad {
			public ModelToadSamurai() {
				textureWidth = 64;
				textureHeight = 64;
				head = new ModelRenderer(this);
				head.setRotationPoint(0.0F, 9.58F, -2.464F);
				head.cubeList.add(new ModelBox(head, 0, 20, -4.46F, -5.558F, -6.0708F, 9, 5, 8, 0.0F, false));
				neck = new ModelRenderer(this);
				neck.setRotationPoint(0.04F, -5.5151F, 1.8721F);
				head.addChild(neck);
				setRotationAngle(neck, -0.9163F, 0.0F, 0.0F);
				neck.cubeList.add(new ModelBox(neck, 0, 42, -4.5F, -0.0449F, 0.0F, 9, 3, 4, 0.0F, false));
				browRight = new ModelRenderer(this);
				browRight.setRotationPoint(-2.71F, -4.308F, -6.5708F);
				head.addChild(browRight);
				setRotationAngle(browRight, 0.0F, 0.0873F, 0.5672F);
				browRight.cubeList.add(new ModelBox(browRight, 13, 49, -2.29F, -0.5F, 0.25F, 4, 1, 5, 0.3F, false));
				browLeft = new ModelRenderer(this);
				browLeft.setRotationPoint(2.71F, -4.308F, -6.5708F);
				head.addChild(browLeft);
				setRotationAngle(browLeft, 0.0F, -0.0873F, -0.5672F);
				browLeft.cubeList.add(new ModelBox(browLeft, 13, 49, -1.71F, -0.5F, 0.25F, 4, 1, 5, 0.3F, true));
				jaw = new ModelRenderer(this);
				jaw.setRotationPoint(0.04F, -0.5003F, -1.1917F);
				head.addChild(jaw);
				setRotationAngle(jaw, 0.0873F, 0.0F, 0.0F);
				jaw.cubeList.add(new ModelBox(jaw, 0, 33, -4.5F, -0.0901F, -4.8784F, 9, 2, 8, 0.0F, false));
				body = new ModelRenderer(this);
				body.setRotationPoint(0.0F, 9.58F, -2.464F);
				chest = new ModelRenderer(this);
				chest.setRotationPoint(0.25F, 1.32F, -0.536F);
				body.addChild(chest);
				setRotationAngle(chest, -1.309F, 0.0F, 0.0F);
				ModelRenderer bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(-0.2F, -1.5397F, 3.6327F);
				chest.addChild(bone6);
				setRotationAngle(bone6, -0.0873F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 0, -6.0F, -5.7157F, -2.0345F, 12, 11, 9, 0.0F, false));
				ModelRenderer bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(-0.2F, -1.5397F, 3.8827F);
				chest.addChild(bone11);
				ModelRenderer chest_r1 = new ModelRenderer(this);
				chest_r1.setRotationPoint(0.0F, -5.9657F, -1.8845F);
				bone11.addChild(chest_r1);
				setRotationAngle(chest_r1, 0.2443F, 0.0F, 0.0F);
				chest_r1.cubeList.add(new ModelBox(chest_r1, 29, 28, -5.5F, 0.1254F, -4.9F, 11, 6, 5, 0.0F, false));
				ModelRenderer bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0343F, -5.6845F);
				bone11.addChild(bone5);
				setRotationAngle(bone5, 0.5323F, 0.0F, 0.0F);
				bone5.cubeList.add(new ModelBox(bone5, 33, 0, -5.5F, 0.0076F, -0.0243F, 11, 6, 3, 0.0F, false));
				bunda = new ModelRenderer(this);
				bunda.setRotationPoint(-0.242F, -6.0646F, 9.9442F);
				chest.addChild(bunda);
				setRotationAngle(bunda, -0.2618F, 0.0F, 0.0F);
				bunda.cubeList.add(new ModelBox(bunda, 30, 39, -5.5F, -0.5F, -0.5F, 11, 9, 4, -0.1F, false));

				armRight = new ModelRenderer(this);
				armRight.setRotationPoint(-5.05F, 1.32F, 1.554F);
				body.addChild(armRight);
				setRotationAngle(armRight, -0.5236F, 0.6109F, 0.3491F);
				armRight.cubeList.add(new ModelBox(armRight, 52, 9, -1.576F, -1.7778F, -1.4648F, 3, 8, 3, 0.2F, false));
		
				forearmRight = new ModelRenderer(this);
				forearmRight.setRotationPoint(-1.572F, 5.7342F, 0.0672F);
				armRight.addChild(forearmRight);
				setRotationAngle(forearmRight, -0.5084F, -0.3763F, -0.8874F);
				forearmRight.cubeList.add(new ModelBox(forearmRight, 40, 52, 0.0F, 0.25F, -1.5F, 3, 6, 3, 0.1F, false));
		
				handRight = new ModelRenderer(this);
				handRight.setRotationPoint(0.472F, 6.1418F, -0.0152F);
				forearmRight.addChild(handRight);
				setRotationAngle(handRight, 0.5434F, 1.3199F, -1.0664F);
				handRight.cubeList.add(new ModelBox(handRight, 26, 24, -2.0038F, -0.2179F, -2.7433F, 4, 1, 3, 0.0F, true));
		
				ModelRenderer bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(1.3922F, -0.2797F, -2.4642F);
				handRight.addChild(bone23);
				setRotationAngle(bone23, 1.5708F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 19, 57, -0.5F, -1.0F, -2.0F, 1, 1, 2, 0.0F, false));
		
				ModelRenderer bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(-0.0078F, -0.2797F, -2.4642F);
				handRight.addChild(bone9);
				setRotationAngle(bone9, 1.5708F, 0.0F, 0.0F);
				bone9.cubeList.add(new ModelBox(bone9, 19, 57, -0.5F, -1.0F, -2.0F, 1, 1, 2, 0.0F, false));
		
				ModelRenderer bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(-1.3578F, -0.2797F, -2.4642F);
				handRight.addChild(bone10);
				setRotationAngle(bone10, 1.5708F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 19, 57, -0.5F, -1.0F, -2.0F, 1, 1, 2, 0.0F, false));
		
				ModelRenderer bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(0.3922F, 0.2203F, -0.4642F);
				handRight.addChild(bone24);
				setRotationAngle(bone24, 0.0F, -1.0472F, 0.7854F);
				bone24.cubeList.add(new ModelBox(bone24, 17, 55, -0.5F, -0.4995F, -3.7282F, 1, 1, 4, 0.0F, false));
		
				blade = new ModelRenderer(this);
				blade.setRotationPoint(0.6965F, 1.2266F, -2.0265F);
				handRight.addChild(blade);
				setRotationAngle(blade, -1.8326F, 0.0F, 0.0F);
				blade.cubeList.add(new ModelBox(blade, 0, 62, -4.25F, -0.5F, -0.5F, 8, 1, 1, 0.0F, false));
				blade.cubeList.add(new ModelBox(blade, 18, 61, 3.75F, 0.0F, -0.5F, 10, 0, 1, 0.02F, false));
				blade.cubeList.add(new ModelBox(blade, 18, 62, 3.5F, 0.0F, -0.5F, 1, 0, 1, 0.02F, false));
		
				armLeft = new ModelRenderer(this);
				armLeft.setRotationPoint(5.05F, 2.32F, -0.446F);
				body.addChild(armLeft);
				setRotationAngle(armLeft, -1.0472F, 0.3054F, -0.3491F);
				armLeft.cubeList.add(new ModelBox(armLeft, 52, 9, -1.424F, -1.7778F, -1.4648F, 3, 8, 3, 0.2F, true));
		
				forearmLeft = new ModelRenderer(this);
				forearmLeft.setRotationPoint(1.572F, 5.7342F, 0.0672F);
				armLeft.addChild(forearmLeft);
				setRotationAngle(forearmLeft, -0.517F, -0.0869F, 0.1103F);
				forearmLeft.cubeList.add(new ModelBox(forearmLeft, 40, 52, -3.0F, 0.25F, -1.5F, 3, 6, 3, 0.1F, true));
		
				handLeft = new ModelRenderer(this);
				handLeft.setRotationPoint(-1.972F, 6.3918F, -2.0152F);
				forearmLeft.addChild(handLeft);
				setRotationAngle(handLeft, 0.0498F, -0.2119F, -0.2678F);
				handLeft.cubeList.add(new ModelBox(handLeft, 47, 59, -3.028F, 0.6082F, -1.9848F, 6, 0, 5, 0.0F, false));
				handLeft.cubeList.add(new ModelBox(handLeft, 26, 24, -1.0F, 0.0F, 0.25F, 4, 1, 3, 0.0F, false));
		
				ModelRenderer bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(1.604F, -0.0618F, 2.0292F);
				handLeft.addChild(bone15);
				setRotationAngle(bone15, 0.0436F, -0.3491F, 0.0F);
				bone15.cubeList.add(new ModelBox(bone15, 17, 55, 0.0F, 0.0F, -3.75F, 1, 1, 4, 0.0F, true));
		
				ModelRenderer bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.604F, -0.0618F, 2.0292F);
				handLeft.addChild(bone16);
				setRotationAngle(bone16, 0.0436F, 0.0F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 17, 55, 0.0F, 0.0F, -3.75F, 1, 1, 4, 0.0F, true));
		
				ModelRenderer bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(-0.396F, -0.0618F, 2.2792F);
				handLeft.addChild(bone17);
				setRotationAngle(bone17, 0.0436F, 0.3491F, 0.0F);
				bone17.cubeList.add(new ModelBox(bone17, 17, 55, 0.0F, 0.0F, -3.75F, 1, 1, 4, 0.0F, true));
		
				ModelRenderer bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.604F, -0.0618F, 3.0292F);
				handLeft.addChild(bone7);
				setRotationAngle(bone7, 0.0436F, 1.5708F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 17, 55, 0.0F, 0.0F, -3.75F, 1, 1, 4, 0.0F, true));

				legRight = new ModelRenderer(this);
				legRight.setRotationPoint(-5.677F, 19.8471F, 1.9223F);
				setRotationAngle(legRight, 0.2618F, 0.5236F, 0.0F);
				thighRight = new ModelRenderer(this);
				thighRight.setRotationPoint(0.241F, 1.0282F, 0.8872F);
				legRight.addChild(thighRight);
				setRotationAngle(thighRight, -0.6981F, 0.0F, 0.0F);
				thighRight.cubeList.add(new ModelBox(thighRight, 32, 10, -2.901F, -1.6142F, -9.4876F, 5, 3, 10, 0.2F, false));
				legLowerRight = new ModelRenderer(this);
				legLowerRight.setRotationPoint(-0.0653F, -4.0517F, -5.8381F);
				legRight.addChild(legLowerRight);
				setRotationAngle(legLowerRight, -0.5236F, 0.0F, 0.0F);
				ModelRenderer legLowerRight3_r1 = new ModelRenderer(this);
				legLowerRight3_r1.setRotationPoint(-0.1735F, 1.045F, -0.854F);
				legLowerRight.addChild(legLowerRight3_r1);
				setRotationAngle(legLowerRight3_r1, -0.7418F, 0.0F, 0.0F);
				legLowerRight3_r1.cubeList.add(new ModelBox(legLowerRight3_r1, 0, 49, -1.3772F, -3.0266F, -0.0999F, 3, 3, 7, 0.2F, false));
				footRight = new ModelRenderer(this);
				footRight.setRotationPoint(-0.0107F, 5.1235F, 4.6603F);
				legLowerRight.addChild(footRight);
				setRotationAngle(footRight, 0.2182F, 0.0F, 0.0F);
				ModelRenderer bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(-0.896F, -0.0341F, -0.0512F);
				footRight.addChild(bone12);
				setRotationAngle(bone12, 0.0F, 0.3491F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));
				ModelRenderer bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.104F, -0.0341F, -0.0512F);
				footRight.addChild(bone3);
				bone3.cubeList.add(new ModelBox(bone3, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));
				ModelRenderer bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(1.104F, -0.0341F, -0.0512F);
				footRight.addChild(bone13);
				setRotationAngle(bone13, 0.0F, -0.3491F, 0.0F);
				bone13.cubeList.add(new ModelBox(bone13, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, false));
				legLeft = new ModelRenderer(this);
				legLeft.setRotationPoint(5.677F, 19.8471F, 1.9223F);
				setRotationAngle(legLeft, 0.2618F, -0.5236F, 0.0F);
				thighLeft = new ModelRenderer(this);
				thighLeft.setRotationPoint(-0.241F, 1.0282F, 0.8872F);
				legLeft.addChild(thighLeft);
				setRotationAngle(thighLeft, -0.6981F, 0.0F, 0.0F);
				thighLeft.cubeList.add(new ModelBox(thighLeft, 32, 10, -2.099F, -1.6142F, -9.4876F, 5, 3, 10, 0.2F, true));
				legLowerLeft = new ModelRenderer(this);
				legLowerLeft.setRotationPoint(0.0653F, -4.0517F, -5.8381F);
				legLeft.addChild(legLowerLeft);
				setRotationAngle(legLowerLeft, -0.5236F, 0.0F, 0.0F);
				ModelRenderer legLowerRight4_r1 = new ModelRenderer(this);
				legLowerRight4_r1.setRotationPoint(0.1735F, 1.045F, -0.854F);
				legLowerLeft.addChild(legLowerRight4_r1);
				setRotationAngle(legLowerRight4_r1, -0.7418F, 0.0F, 0.0F);
				legLowerRight4_r1.cubeList.add(new ModelBox(legLowerRight4_r1, 0, 49, -1.6228F, -3.0266F, -0.0999F, 3, 3, 7, 0.2F, true));
				footLeft = new ModelRenderer(this);
				footLeft.setRotationPoint(0.0107F, 5.1235F, 4.6603F);
				legLowerLeft.addChild(footLeft);
				setRotationAngle(footLeft, 0.2182F, 0.0F, 0.0F);
				ModelRenderer bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.896F, -0.0341F, -0.0512F);
				footLeft.addChild(bone19);
				setRotationAngle(bone19, 0.0F, -0.3491F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
				ModelRenderer bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(-0.104F, -0.0341F, -0.0512F);
				footLeft.addChild(bone20);
				bone20.cubeList.add(new ModelBox(bone20, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
				ModelRenderer bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(-1.104F, -0.0341F, -0.0512F);
				footLeft.addChild(bone22);
				setRotationAngle(bone22, 0.0F, 0.3491F, 0.0F);
				bone22.cubeList.add(new ModelBox(bone22, 26, 52, -1.0F, -1.0F, -4.7F, 2, 2, 5, -0.2F, true));
			}
		}
	}
}
