
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import net.narutomod.potion.PotionFeatherFalling;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemSenjutsu;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import javax.vecmath.Vector4f;
import java.util.Random;
import io.netty.buffer.ByteBuf;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySnake8Heads extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 377;
	public static final int ENTITYID_RANGED = 378;
	private static final float MODELSCALE = 12.0f;

	public EntitySnake8Heads(ElementsNarutomodMod instance) {
		super(instance, 737);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
		 .id(new ResourceLocation("narutomod", "snake_8_heads"), ENTITYID)
		 .name("snake_8_heads").tracker(128, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntitySnakeHead.class)
		 .id(new ResourceLocation("narutomod", "snake_8_head1"), ENTITYID_RANGED)
		 .name("snake_8_head1").tracker(128, 3, true).egg(-1, -10066330).build());
	}

	public static class EC extends EntityShieldBase {
		private static final DataParameter<Integer> TICKSALIVE = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private final int upTime = 40;
		private final int waitTime = 20;
		private double chakraBurn;

		public EC(World world) {
			super(world);
			this.setSize(0.8f * MODELSCALE, 2.0f * MODELSCALE);
			this.setOwnerCanSteer(true, 0.2F);
			this.stepHeight = this.height / 3;
		}

		public EC(EntityLivingBase summonerIn, double chakraUsagePerSec) {
			super(summonerIn);
			this.setSize(0.8f * MODELSCALE, 2.0f * MODELSCALE);
			this.stepHeight = this.height / 3;
			this.setHealth(this.getMaxHealth());
			this.chakraBurn = chakraUsagePerSec;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(TICKSALIVE, Integer.valueOf(0));
		}

		private void setTicksAlive(int ticks) {
			this.getDataManager().set(TICKSALIVE, Integer.valueOf(ticks));
		}

		public int getTicksAlive() {
			return ((Integer)this.dataManager.get(TICKSALIVE)).intValue();
		}

		@Override
		protected void applyEntityAttributes() {
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5000.0D);
			//this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3D);
		}

		@Override
		public void updatePassenger(Entity passenger) {
			Vec3d vec = new Vec3d(3.5d * 0.0625d * MODELSCALE, 19d * 0.0625d * MODELSCALE, 24d * 0.0625d * MODELSCALE);
			if (this.isPassenger(passenger)) {
				Vec3d vec2 = vec.rotateYaw(-this.rotationYaw * 0.017453292F);
				passenger.setPosition(this.posX + vec2.x, this.posY + vec2.y, this.posZ + vec2.z);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (this.ticksExisted == 1) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodspawn")), 5f, 1f);
			} else if (this.ticksExisted == this.upTime) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:snake_hiss")), 5f, 0.7f);
			} else if (this.ticksExisted <= this.upTime + this.waitTime && this.ticksExisted % 5 == 1) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodgrow")), 2f, 1f);
			}
			if (this.ticksExisted <= this.upTime + this.waitTime) {
				Entity passenger = this.getControllingPassenger();
				if (passenger != null) {
					passenger.setInvisible(true);
				}
			}
			if (this.chakraBurn > 0.0d && this.ticksExisted % 20 == 19) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner != null && (!Chakra.pathway(summoner).consume(this.chakraBurn) || !this.isSageModeActive(summoner))) {
					this.setDead();
				}
			}
			this.setTicksAlive(this.getTicksAlive() + 1);
		}

		private boolean isSageModeActive(EntityLivingBase summoner) {
			if (summoner instanceof EntityPlayer) {
				ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)summoner, ItemSenjutsu.block);
				return stack != null && ItemSenjutsu.isSageModeActivated(stack);
			}
			return false;
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase summoner = this.getSummoner();
				if (summoner != null) {
					summoner.addPotionEffect(new PotionEffect(PotionFeatherFalling.potion, 60, 5));
				}
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:poof")), 2.0F, 1.0F);
				Particles.spawnParticle(this.world, Particles.Types.SMOKE, this.posX, this.posY+this.height/2, this.posZ, 300,
				 this.width * 0.5d, this.height * 0.3d, this.width * 0.5d, 0d, 0d, 0d, 0xD0FFFFFF, 20 + (int)(MODELSCALE * 5));
			}
		}

		@Override
		public void writeEntityToNBT(NBTTagCompound compound) {
			compound.setInteger("life", this.getTicksAlive());
		}

		@Override
		public void readEntityFromNBT(NBTTagCompound compound) {
			this.setTicksAlive(compound.getInteger("life"));
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (!(entity.getRidingEntity() instanceof EC)) {
					entity.world.spawnEntity(new EC(entity, ItemSenjutsu.SNAKE8H.chakraUsage * 0.02d *
					 ((ItemSenjutsu.RangedItem)stack.getItem()).getCurrentJutsuXpModifier(stack, entity)));
					return true;
				}
				return false;
			}
		}
	}

	public static class EntitySnakeHead extends EntitySnake.EntityCustom {
		private EntityLivingBase target;

		public EntitySnakeHead(World world) {
			super(world);
			this.postScaleFixup();
		}

		public EntitySnakeHead(EntityLivingBase summonerIn, EntityLivingBase targetIn) {
			super(summonerIn);
			this.postScaleFixup();
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(100.0d);
			Entity parent = summonerIn.getRidingEntity() instanceof EC ? summonerIn.getRidingEntity() : summonerIn;
			this.rotationYawHead = parent.rotationYaw;
			this.setLocationAndAngles(parent.posX, parent.posY - 10d, parent.posZ, parent.rotationYaw, -45f);
			this.target = targetIn;
		}

		@Override
		public float getScale() {
			return MODELSCALE;
		}

		@Override
		protected void updateAITasks() {
			super.updateAITasks();
			int age = this.getAge();
			if (age < 20) {
				this.getPhaseManager().setPhase(EntitySnake.Phase.ROAMING);
				//this.setAIMoveSpeed(1.0f);
				Vec3d vec = Vec3d.fromPitchYaw(0.0f, this.rotationYaw).scale(0.08d);
				this.motionX += vec.x;
				this.motionZ += vec.z;
				if (age == 1) {
					this.motionY = 2.5d;
				}
			} else if (this.target != null && this.target.isEntityAlive()) {
				this.setAttackTarget(this.target);
			} else {
				this.onDeathUpdate();
			}
		}

		@Override
		public void onUpdate() {
			this.noClip = this.ticksExisted < 20;
			super.onUpdate();
			if (this.getAge() == 1) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodgrow")), 2f, 1f);
			}
		}
	}

	public static class PlayerHook {
		@SideOnly(Side.CLIENT)
		@SubscribeEvent
		public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
			if (event.getEntityPlayer().getRidingEntity() instanceof EC) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(event.getEntityPlayer(), 64d, 2d);
				if (res != null && res.entityHit instanceof EntityLivingBase) {
					NarutomodMod.PACKET_HANDLER.sendToServer(new Message(res.entityHit.getEntityId()));
				}
			}
		}

		public static class Message implements IMessage {
			int id;

			public Message() { }

			public Message(int i) {
				this.id = i;
			}
	
			public static class Handler implements IMessageHandler<Message, IMessage> {
				@Override
				public IMessage onMessage(Message message, MessageContext context) {
					EntityPlayerMP player = context.getServerHandler().player;
					player.getServerWorld().addScheduledTask(() -> {
						Entity entity = player.world.getEntityByID(message.id);
						if (entity instanceof EntityLivingBase) {
							player.world.spawnEntity(new EntitySnakeHead(player, (EntityLivingBase)entity));
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
		elements.addNetworkMessage(PlayerHook.Message.Handler.class, PlayerHook.Message.class, Side.SERVER);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerHook());
	}

	public class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> {
				return new RenderLivingBase<EC>(renderManager, new ModelSnake8h(), 0.5f) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/snake_8h.png");
					@Override
					public float prepareScale(EC entity, float partialTicks) {
						float f = super.prepareScale(entity, partialTicks);
						float f1 = (1.0F - Math.min((partialTicks + entity.ticksExisted) / (float)entity.upTime, 1.0F)) * entity.height * 1.5F;
						GlStateManager.translate(0.0F, 1.5F - 1.5F * MODELSCALE + f1, 0.0F);
						GlStateManager.scale(MODELSCALE, MODELSCALE, MODELSCALE);
						return f;
					}
					@Override
					protected ResourceLocation getEntityTexture(EC entity) {
						return this.texture;
					}
				};
			});
			RenderingRegistry.registerEntityRenderingHandler(EntitySnakeHead.class, renderManager -> {
				return new EntitySnake.RenderSnake<EntitySnakeHead>(renderManager) {
					private final ResourceLocation texture = new ResourceLocation("narutomod:textures/snake_8h1.png");
					@Override
					protected ResourceLocation getEntityTexture(EntitySnakeHead entity) {
						return this.texture;
					}
				};
			});
		}

		// Made with Blockbench 4.2.5
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelSnake8h extends ModelBase {
			private final Random RAND = new Random();
			private final ModelRenderer tails;
			private final ModelRenderer[][] neck = new ModelRenderer[8][10];
			private final ModelRenderer head0;
			private final ModelRenderer bone56;
			private final ModelRenderer bone57;
			private final ModelRenderer bone58;
			private final ModelRenderer bone59;
			private final ModelRenderer bone60;
			private final ModelRenderer bone61;
			private final ModelRenderer bone62;
			private final ModelRenderer bone63;
			private final ModelRenderer bone64;
			private final ModelRenderer bone65;
			private final ModelRenderer bone66;
			private final ModelRenderer jaw0;
			private final ModelRenderer bone67;
			private final ModelRenderer bone68;
			private final ModelRenderer bone69;
			private final ModelRenderer horns0;
			private final ModelRenderer bone70;
			private final ModelRenderer bone71;
			private final ModelRenderer bone72;
			private final ModelRenderer bone73;
			private final ModelRenderer head1;
			private final ModelRenderer bone20;
			private final ModelRenderer bone21;
			private final ModelRenderer bone22;
			private final ModelRenderer bone23;
			private final ModelRenderer bone24;
			private final ModelRenderer bone25;
			private final ModelRenderer bone26;
			private final ModelRenderer bone27;
			private final ModelRenderer bone28;
			private final ModelRenderer bone29;
			private final ModelRenderer bone30;
			private final ModelRenderer jaw1;
			private final ModelRenderer bone31;
			private final ModelRenderer bone32;
			private final ModelRenderer bone33;
			private final ModelRenderer horns1;
			private final ModelRenderer bone34;
			private final ModelRenderer bone35;
			private final ModelRenderer bone36;
			private final ModelRenderer bone37;
			private final ModelRenderer head2;
			private final ModelRenderer bone47;
			private final ModelRenderer bone48;
			private final ModelRenderer bone49;
			private final ModelRenderer bone50;
			private final ModelRenderer bone51;
			private final ModelRenderer bone52;
			private final ModelRenderer bone53;
			private final ModelRenderer bone54;
			private final ModelRenderer bone55;
			private final ModelRenderer bone74;
			private final ModelRenderer bone75;
			private final ModelRenderer jaw2;
			private final ModelRenderer bone76;
			private final ModelRenderer bone77;
			private final ModelRenderer bone78;
			private final ModelRenderer horns2;
			private final ModelRenderer bone79;
			private final ModelRenderer bone80;
			private final ModelRenderer bone81;
			private final ModelRenderer bone82;
			private final ModelRenderer head3;
			private final ModelRenderer bone92;
			private final ModelRenderer bone93;
			private final ModelRenderer bone94;
			private final ModelRenderer bone95;
			private final ModelRenderer bone96;
			private final ModelRenderer bone97;
			private final ModelRenderer bone98;
			private final ModelRenderer bone99;
			private final ModelRenderer bone100;
			private final ModelRenderer bone101;
			private final ModelRenderer bone102;
			private final ModelRenderer jaw3;
			private final ModelRenderer bone103;
			private final ModelRenderer bone104;
			private final ModelRenderer bone105;
			private final ModelRenderer horns3;
			private final ModelRenderer bone106;
			private final ModelRenderer bone107;
			private final ModelRenderer bone108;
			private final ModelRenderer bone109;
			private final ModelRenderer head4;
			private final ModelRenderer bone119;
			private final ModelRenderer bone120;
			private final ModelRenderer bone121;
			private final ModelRenderer bone122;
			private final ModelRenderer bone123;
			private final ModelRenderer bone124;
			private final ModelRenderer bone125;
			private final ModelRenderer bone126;
			private final ModelRenderer bone127;
			private final ModelRenderer bone128;
			private final ModelRenderer bone129;
			private final ModelRenderer jaw4;
			private final ModelRenderer bone130;
			private final ModelRenderer bone131;
			private final ModelRenderer bone132;
			private final ModelRenderer horns4;
			private final ModelRenderer bone133;
			private final ModelRenderer bone134;
			private final ModelRenderer bone135;
			private final ModelRenderer bone136;
			private final ModelRenderer head5;
			private final ModelRenderer bone146;
			private final ModelRenderer bone147;
			private final ModelRenderer bone148;
			private final ModelRenderer bone149;
			private final ModelRenderer bone150;
			private final ModelRenderer bone151;
			private final ModelRenderer bone152;
			private final ModelRenderer bone153;
			private final ModelRenderer bone154;
			private final ModelRenderer bone155;
			private final ModelRenderer bone156;
			private final ModelRenderer jaw5;
			private final ModelRenderer bone157;
			private final ModelRenderer bone158;
			private final ModelRenderer bone159;
			private final ModelRenderer horns5;
			private final ModelRenderer bone160;
			private final ModelRenderer bone161;
			private final ModelRenderer bone162;
			private final ModelRenderer bone163;
			private final ModelRenderer head6;
			private final ModelRenderer bone173;
			private final ModelRenderer bone174;
			private final ModelRenderer bone175;
			private final ModelRenderer bone176;
			private final ModelRenderer bone177;
			private final ModelRenderer bone178;
			private final ModelRenderer bone179;
			private final ModelRenderer bone180;
			private final ModelRenderer bone181;
			private final ModelRenderer bone182;
			private final ModelRenderer bone183;
			private final ModelRenderer jaw6;
			private final ModelRenderer bone184;
			private final ModelRenderer bone185;
			private final ModelRenderer bone186;
			private final ModelRenderer horns6;
			private final ModelRenderer bone187;
			private final ModelRenderer bone188;
			private final ModelRenderer bone189;
			private final ModelRenderer bone190;
			private final ModelRenderer head7;
			private final ModelRenderer bone200;
			private final ModelRenderer bone201;
			private final ModelRenderer bone202;
			private final ModelRenderer bone203;
			private final ModelRenderer bone204;
			private final ModelRenderer bone205;
			private final ModelRenderer bone206;
			private final ModelRenderer bone207;
			private final ModelRenderer bone208;
			private final ModelRenderer bone209;
			private final ModelRenderer bone210;
			private final ModelRenderer jaw7;
			private final ModelRenderer bone211;
			private final ModelRenderer bone212;
			private final ModelRenderer bone213;
			private final ModelRenderer horns7;
			private final ModelRenderer bone214;
			private final ModelRenderer bone215;
			private final ModelRenderer bone216;
			private final ModelRenderer bone217;
			private final ModelRenderer tail0;
			private final ModelRenderer bone224;
			private final ModelRenderer bone225;
			private final ModelRenderer bone226;
			private final ModelRenderer bone223;
			private final ModelRenderer tail1;
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer tail2;
			private final ModelRenderer bone6;
			private final ModelRenderer bone7;
			private final ModelRenderer bone8;
			private final ModelRenderer bone9;
			private final ModelRenderer tail3;
			private final ModelRenderer bone10;
			private final ModelRenderer bone11;
			private final ModelRenderer bone12;
			private final ModelRenderer bone13;
			private final ModelRenderer tail4;
			private final ModelRenderer bone14;
			private final ModelRenderer bone15;
			private final ModelRenderer bone16;
			private final ModelRenderer bone17;
			private final ModelRenderer tail5;
			private final ModelRenderer bone18;
			private final ModelRenderer bone19;
			private final ModelRenderer bone38;
			private final ModelRenderer bone39;
			private final ModelRenderer tail6;
			private final ModelRenderer bone40;
			private final ModelRenderer bone41;
			private final ModelRenderer bone42;
			private final ModelRenderer bone43;
			private final ModelRenderer tail7;
			private final ModelRenderer bone44;
			private final ModelRenderer bone45;
			private final ModelRenderer bone46;
			private final ModelRenderer bone83;
			private final Vector4f[][] neckRotation = {
				{ new Vector4f(-1.5708F, 0.0F, 0.0F, 0.0F), new Vector4f(-0.0436F, -0.0436F, 0.0F, 0.0F),
				  new Vector4f(-0.2618F, -0.0436F, 0.0F, 0.0F), new Vector4f(0.2618F, -0.0436F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.5236F, 0.0F, -0.0436F, 0.0F), new Vector4f(0.5236F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.4363F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F) },
				{ new Vector4f(-1.5708F, 0.0F, 0.0F, 0.0F), new Vector4f(-0.0436F, 0.0436F, 0.0F, 0.0F),
				  new Vector4f(-0.2618F, 0.0436F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0436F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.5236F, 0.0F, 0.0436F, 0.0F), new Vector4f(0.5236F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.4363F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F) },
				{ new Vector4f(-1.5708F, 0.0F, 0.0F, 0.0F), new Vector4f(-0.0873F, 0.1309F, 0.0F, 0.0F),
				  new Vector4f(-0.2618F, 0.1309F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.1309F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.3927F, 0.0F, 0.0873F, 0.0F), new Vector4f(0.3927F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.3927F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F) },
				{ new Vector4f(-1.5708F, 0.0F, 0.0F, 0.0F), new Vector4f(-0.0873F, -0.1309F, 0.0F, 0.0F),
				  new Vector4f(-0.2618F, -0.1309F, 0.0F, 0.0F), new Vector4f(0.2618F, -0.1309F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.3927F, 0.0F, -0.0873F, 0.0F), new Vector4f(0.3927F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.3927F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F) },
				{ new Vector4f(-1.5708F, 0.0F, 0.0F, 0.0F), new Vector4f(-0.2618F, 0.2618F, 0.0F, 0.0F),
				  new Vector4f(-0.2618F, 0.2618F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.2618F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, 0.2618F, 0.0F, 0.0F), new Vector4f(0.2618F, -0.2618F, 0.1745F, 0.0F),
				  new Vector4f(0.4363F, -0.2618F, 0.0873F, 0.0F), new Vector4f(0.4363F, 0.0F, 0.0873F, 0.0F),
				  new Vector4f(0.5236F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F) },
				{ new Vector4f(-1.5708F, 0.0F, 0.0F, 0.0F), new Vector4f(-0.2618F, -0.2618F, 0.0F, 0.0F),
				  new Vector4f(-0.2618F, -0.2618F, 0.0F, 0.0F), new Vector4f(0.2618F, -0.2618F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, -0.2618F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.2618F, -0.1745F, 0.0F),
				  new Vector4f(0.4363F, 0.2618F, -0.1745F, 0.0F), new Vector4f(0.4363F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.5236F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F) },
				{ new Vector4f(-1.5708F, 0.0F, 0.0F, 0.0F), new Vector4f(-0.2618F, 0.1309F, 0.0F, 0.0F),
				  new Vector4f(-0.2618F, 0.1309F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.1309F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, -0.1309F, 0.0F, 0.0F), new Vector4f(0.2618F, -0.0873F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.3491F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F) },
				{ new Vector4f(-1.5708F, 0.0F, 0.0F, 0.0F), new Vector4f(-0.2618F, -0.1309F, 0.0F, 0.0F),
				  new Vector4f(-0.2618F, -0.1309F, 0.0F, 0.0F), new Vector4f(0.2618F, -0.1309F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, 0.1309F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.1309F, 0.0F, 0.0F),
				  new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F),
				  new Vector4f(0.3491F, 0.0F, 0.0F, 0.0F), new Vector4f(0.2618F, 0.0F, 0.0F, 0.0F) }
			};
			
			public ModelSnake8h() {
				textureWidth = 64;
				textureHeight = 64;
				tails = new ModelRenderer(this);
				tails.setRotationPoint(0.0F, 0.0F, 0.0F);
				tails.cubeList.add(new ModelBox(tails, 28, 3, -6.0F, 19.0F, -3.0F, 12, 10, 6, 0.0F, false));
				neck[0][0] = new ModelRenderer(this);
				neck[0][0].setRotationPoint(1.5F, 26.0F, -3.0F);
				tails.addChild(neck[0][0]);
				setRotationAngle(neck[0][0], -1.5708F, 0.0F, 0.0F);
				neck[0][0].cubeList.add(new ModelBox(neck[0][0], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[0][1] = new ModelRenderer(this);
				neck[0][1].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][0].addChild(neck[0][1]);
				//setRotationAngle(neck[0][1], -0.0436F, -0.0436F, 0.0F);
				neck[0][1].cubeList.add(new ModelBox(neck[0][1], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[0][2] = new ModelRenderer(this);
				neck[0][2].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][1].addChild(neck[0][2]);
				//setRotationAngle(neck[0][2], -0.2618F, -0.0436F, 0.0F);
				neck[0][2].cubeList.add(new ModelBox(neck[0][2], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[0][3] = new ModelRenderer(this);
				neck[0][3].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][2].addChild(neck[0][3]);
				//setRotationAngle(neck[0][3], 0.2618F, -0.0436F, 0.0F);
				neck[0][3].cubeList.add(new ModelBox(neck[0][3], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[0][4] = new ModelRenderer(this);
				neck[0][4].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][3].addChild(neck[0][4]);
				//setRotationAngle(neck[0][4], 0.2618F, 0.0F, 0.0F);
				neck[0][4].cubeList.add(new ModelBox(neck[0][4], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[0][5] = new ModelRenderer(this);
				neck[0][5].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][4].addChild(neck[0][5]);
				//setRotationAngle(neck[0][5], 0.2618F, 0.0F, 0.0F);
				neck[0][5].cubeList.add(new ModelBox(neck[0][5], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[0][6] = new ModelRenderer(this);
				neck[0][6].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][5].addChild(neck[0][6]);
				//setRotationAngle(neck[0][6], 0.5236F, 0.0F, -0.0436F);
				neck[0][6].cubeList.add(new ModelBox(neck[0][6], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[0][7] = new ModelRenderer(this);
				neck[0][7].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][6].addChild(neck[0][7]);
				//setRotationAngle(neck[0][7], 0.5236F, 0.0F, 0.0F);
				neck[0][7].cubeList.add(new ModelBox(neck[0][7], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[0][8] = new ModelRenderer(this);
				neck[0][8].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][7].addChild(neck[0][8]);
				//setRotationAngle(neck[0][8], 0.4363F, 0.0F, 0.0F);
				neck[0][8].cubeList.add(new ModelBox(neck[0][8], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[0][9] = new ModelRenderer(this);
				neck[0][9].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][8].addChild(neck[0][9]);
				//setRotationAngle(neck[0][9], 0.2618F, 0.0F, 0.0F);
				neck[0][9].cubeList.add(new ModelBox(neck[0][9], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				head0 = new ModelRenderer(this);
				head0.setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[0][9].addChild(head0);
				head0.cubeList.add(new ModelBox(head0, 16, 0, -2.5F, -2.0F, 0.0F, 5, 4, 1, 0.1F, false));
				bone56 = new ModelRenderer(this);
				bone56.setRotationPoint(1.4F, -0.7F, -5.35F);
				head0.addChild(bone56);
				setRotationAngle(bone56, 0.7854F, 0.0F, 0.6109F);
				bone56.cubeList.add(new ModelBox(bone56, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));
				bone57 = new ModelRenderer(this);
				bone57.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone56.addChild(bone57);
				setRotationAngle(bone57, -0.9599F, 0.0F, 0.0F);
				bone57.cubeList.add(new ModelBox(bone57, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));
				bone58 = new ModelRenderer(this);
				bone58.setRotationPoint(-1.4F, -0.7F, -5.35F);
				head0.addChild(bone58);
				setRotationAngle(bone58, 0.7854F, 0.0F, -0.6109F);
				bone58.cubeList.add(new ModelBox(bone58, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));
				bone59 = new ModelRenderer(this);
				bone59.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone58.addChild(bone59);
				setRotationAngle(bone59, -0.9599F, 0.0F, 0.0F);
				bone59.cubeList.add(new ModelBox(bone59, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));
				bone60 = new ModelRenderer(this);
				bone60.setRotationPoint(0.0F, -1.0F, 0.0F);
				head0.addChild(bone60);
				setRotationAngle(bone60, 0.0436F, 0.0873F, 0.0F);
				bone60.cubeList.add(new ModelBox(bone60, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));
				bone61 = new ModelRenderer(this);
				bone61.setRotationPoint(0.0F, -1.0F, 0.0F);
				head0.addChild(bone61);
				setRotationAngle(bone61, 0.0436F, -0.0873F, 0.0F);
				bone61.cubeList.add(new ModelBox(bone61, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));
				bone62 = new ModelRenderer(this);
				bone62.setRotationPoint(-0.15F, -1.1F, -2.5F);
				head0.addChild(bone62);
				setRotationAngle(bone62, 0.5236F, 0.2618F, 0.0F);
				bone62.cubeList.add(new ModelBox(bone62, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));
				bone63 = new ModelRenderer(this);
				bone63.setRotationPoint(0.15F, -1.1F, -2.5F);
				head0.addChild(bone63);
				setRotationAngle(bone63, 0.5236F, -0.2618F, 0.0F);
				bone63.cubeList.add(new ModelBox(bone63, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));
				bone64 = new ModelRenderer(this);
				bone64.setRotationPoint(2.6F, 0.1F, -3.95F);
				head0.addChild(bone64);
				setRotationAngle(bone64, 0.0F, 0.2618F, 0.0F);
				bone64.cubeList.add(new ModelBox(bone64, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
				bone64.cubeList.add(new ModelBox(bone64, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));
				bone65 = new ModelRenderer(this);
				bone65.setRotationPoint(-2.65F, 0.1F, -3.95F);
				head0.addChild(bone65);
				setRotationAngle(bone65, 0.0F, -0.2618F, 0.0F);
				bone65.cubeList.add(new ModelBox(bone65, 10, 19, 0.05F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
				bone65.cubeList.add(new ModelBox(bone65, 0, 19, 0.05F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));
				bone66 = new ModelRenderer(this);
				bone66.setRotationPoint(1.6F, 1.8F, -5.95F);
				head0.addChild(bone66);
				bone66.cubeList.add(new ModelBox(bone66, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
				bone66.cubeList.add(new ModelBox(bone66, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));
				jaw0 = new ModelRenderer(this);
				jaw0.setRotationPoint(0.0F, 0.5F, 0.0F);
				head0.addChild(jaw0);
				setRotationAngle(jaw0, 0.5236F, 0.0F, 0.0F);
				bone67 = new ModelRenderer(this);
				bone67.setRotationPoint(3.0F, 0.9F, 0.0F);
				jaw0.addChild(bone67);
				setRotationAngle(bone67, 0.0F, 0.2182F, 0.0F);
				bone67.cubeList.add(new ModelBox(bone67, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));
				bone68 = new ModelRenderer(this);
				bone68.setRotationPoint(-3.0F, 0.9F, 0.0F);
				jaw0.addChild(bone68);
				setRotationAngle(bone68, 0.0F, -0.2182F, 0.0F);
				bone68.cubeList.add(new ModelBox(bone68, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));
				bone69 = new ModelRenderer(this);
				bone69.setRotationPoint(0.0F, -0.2F, -5.5F);
				jaw0.addChild(bone69);
				setRotationAngle(bone69, 3.1416F, 3.1416F, 0.0F);
				bone69.cubeList.add(new ModelBox(bone69, 0, 1, 1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, false));
				bone69.cubeList.add(new ModelBox(bone69, 0, 1, -1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, true));
				horns0 = new ModelRenderer(this);
				horns0.setRotationPoint(0.0F, -1.65F, -2.0F);
				head0.addChild(horns0);
				bone70 = new ModelRenderer(this);
				bone70.setRotationPoint(-2.3F, -0.25F, 0.4F);
				horns0.addChild(bone70);
				setRotationAngle(bone70, 0.2618F, -0.5236F, 0.0F);
				bone70.cubeList.add(new ModelBox(bone70, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone70.cubeList.add(new ModelBox(bone70, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
				bone70.cubeList.add(new ModelBox(bone70, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
				bone70.cubeList.add(new ModelBox(bone70, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
				bone70.cubeList.add(new ModelBox(bone70, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));
				bone71 = new ModelRenderer(this);
				bone71.setRotationPoint(-1.2F, -0.25F, 0.8F);
				horns0.addChild(bone71);
				setRotationAngle(bone71, 0.4363F, -0.3491F, 0.0F);
				bone71.cubeList.add(new ModelBox(bone71, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone71.cubeList.add(new ModelBox(bone71, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, false));
				bone71.cubeList.add(new ModelBox(bone71, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
				bone71.cubeList.add(new ModelBox(bone71, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));
				bone72 = new ModelRenderer(this);
				bone72.setRotationPoint(1.2F, -0.25F, 0.8F);
				horns0.addChild(bone72);
				setRotationAngle(bone72, 0.4363F, 0.3491F, 0.0F);
				bone72.cubeList.add(new ModelBox(bone72, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone72.cubeList.add(new ModelBox(bone72, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, true));
				bone72.cubeList.add(new ModelBox(bone72, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
				bone72.cubeList.add(new ModelBox(bone72, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));
				bone73 = new ModelRenderer(this);
				bone73.setRotationPoint(2.3F, -0.25F, 0.4F);
				horns0.addChild(bone73);
				setRotationAngle(bone73, 0.2618F, 0.5236F, 0.0F);
				bone73.cubeList.add(new ModelBox(bone73, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone73.cubeList.add(new ModelBox(bone73, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
				bone73.cubeList.add(new ModelBox(bone73, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
				bone73.cubeList.add(new ModelBox(bone73, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
				bone73.cubeList.add(new ModelBox(bone73, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));
				neck[1][0] = new ModelRenderer(this);
				neck[1][0].setRotationPoint(-1.5F, 26.0F, -3.0F);
				tails.addChild(neck[1][0]);
				setRotationAngle(neck[1][0], -1.5708F, 0.0F, 0.0F);
				neck[1][0].cubeList.add(new ModelBox(neck[1][0], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[1][1] = new ModelRenderer(this);
				neck[1][1].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][0].addChild(neck[1][1]);
				//setRotationAngle(neck[1][1], -0.0436F, 0.0436F, 0.0F);
				neck[1][1].cubeList.add(new ModelBox(neck[1][1], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[1][2] = new ModelRenderer(this);
				neck[1][2].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][1].addChild(neck[1][2]);
				//setRotationAngle(neck[1][2], -0.2618F, 0.0436F, 0.0F);
				neck[1][2].cubeList.add(new ModelBox(neck[1][2], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[1][3] = new ModelRenderer(this);
				neck[1][3].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][2].addChild(neck[1][3]);
				//setRotationAngle(neck[1][3], 0.2618F, 0.0436F, 0.0F);
				neck[1][3].cubeList.add(new ModelBox(neck[1][3], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[1][4] = new ModelRenderer(this);
				neck[1][4].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][3].addChild(neck[1][4]);
				//setRotationAngle(neck[1][4], 0.2618F, 0.0F, 0.0F);
				neck[1][4].cubeList.add(new ModelBox(neck[1][4], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[1][5] = new ModelRenderer(this);
				neck[1][5].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][4].addChild(neck[1][5]);
				//setRotationAngle(neck[1][5], 0.2618F, 0.0F, 0.0F);
				neck[1][5].cubeList.add(new ModelBox(neck[1][5], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[1][6] = new ModelRenderer(this);
				neck[1][6].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][5].addChild(neck[1][6]);
				//setRotationAngle(neck[1][6], 0.5236F, 0.0F, 0.0436F);
				neck[1][6].cubeList.add(new ModelBox(neck[1][6], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[1][7] = new ModelRenderer(this);
				neck[1][7].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][6].addChild(neck[1][7]);
				//setRotationAngle(neck[1][7], 0.5236F, 0.0F, 0.0F);
				neck[1][7].cubeList.add(new ModelBox(neck[1][7], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[1][8] = new ModelRenderer(this);
				neck[1][8].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][7].addChild(neck[1][8]);
				//setRotationAngle(neck[1][8], 0.4363F, 0.0F, 0.0F);
				neck[1][8].cubeList.add(new ModelBox(neck[1][8], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[1][9] = new ModelRenderer(this);
				neck[1][9].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][8].addChild(neck[1][9]);
				//setRotationAngle(neck[1][9], 0.2618F, 0.0F, 0.0F);
				neck[1][9].cubeList.add(new ModelBox(neck[1][9], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				head1 = new ModelRenderer(this);
				head1.setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[1][9].addChild(head1);
				head1.cubeList.add(new ModelBox(head1, 16, 0, -2.5F, -2.0F, 0.0F, 5, 4, 1, 0.1F, false));
				bone20 = new ModelRenderer(this);
				bone20.setRotationPoint(1.4F, -0.7F, -5.35F);
				head1.addChild(bone20);
				setRotationAngle(bone20, 0.7854F, 0.0F, 0.6109F);
				bone20.cubeList.add(new ModelBox(bone20, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));
				bone21 = new ModelRenderer(this);
				bone21.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone20.addChild(bone21);
				setRotationAngle(bone21, -0.9599F, 0.0F, 0.0F);
				bone21.cubeList.add(new ModelBox(bone21, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));
				bone22 = new ModelRenderer(this);
				bone22.setRotationPoint(-1.4F, -0.7F, -5.35F);
				head1.addChild(bone22);
				setRotationAngle(bone22, 0.7854F, 0.0F, -0.6109F);
				bone22.cubeList.add(new ModelBox(bone22, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));
				bone23 = new ModelRenderer(this);
				bone23.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone22.addChild(bone23);
				setRotationAngle(bone23, -0.9599F, 0.0F, 0.0F);
				bone23.cubeList.add(new ModelBox(bone23, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));
				bone24 = new ModelRenderer(this);
				bone24.setRotationPoint(0.0F, -1.0F, 0.0F);
				head1.addChild(bone24);
				setRotationAngle(bone24, 0.0436F, 0.0873F, 0.0F);
				bone24.cubeList.add(new ModelBox(bone24, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));
				bone25 = new ModelRenderer(this);
				bone25.setRotationPoint(0.0F, -1.0F, 0.0F);
				head1.addChild(bone25);
				setRotationAngle(bone25, 0.0436F, -0.0873F, 0.0F);
				bone25.cubeList.add(new ModelBox(bone25, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));
				bone26 = new ModelRenderer(this);
				bone26.setRotationPoint(-0.15F, -1.1F, -2.5F);
				head1.addChild(bone26);
				setRotationAngle(bone26, 0.5236F, 0.2618F, 0.0F);
				bone26.cubeList.add(new ModelBox(bone26, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));
				bone27 = new ModelRenderer(this);
				bone27.setRotationPoint(0.15F, -1.1F, -2.5F);
				head1.addChild(bone27);
				setRotationAngle(bone27, 0.5236F, -0.2618F, 0.0F);
				bone27.cubeList.add(new ModelBox(bone27, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));
				bone28 = new ModelRenderer(this);
				bone28.setRotationPoint(2.6F, 0.1F, -3.95F);
				head1.addChild(bone28);
				setRotationAngle(bone28, 0.0F, 0.2618F, 0.0F);
				bone28.cubeList.add(new ModelBox(bone28, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
				bone28.cubeList.add(new ModelBox(bone28, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));
				bone29 = new ModelRenderer(this);
				bone29.setRotationPoint(-2.65F, 0.1F, -3.95F);
				head1.addChild(bone29);
				setRotationAngle(bone29, 0.0F, -0.2618F, 0.0F);
				bone29.cubeList.add(new ModelBox(bone29, 10, 19, 0.05F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
				bone29.cubeList.add(new ModelBox(bone29, 0, 19, 0.05F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));
				bone30 = new ModelRenderer(this);
				bone30.setRotationPoint(1.6F, 1.8F, -5.95F);
				head1.addChild(bone30);
				bone30.cubeList.add(new ModelBox(bone30, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
				bone30.cubeList.add(new ModelBox(bone30, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));
				jaw1 = new ModelRenderer(this);
				jaw1.setRotationPoint(0.0F, 0.5F, 0.0F);
				head1.addChild(jaw1);
				setRotationAngle(jaw1, 0.5236F, 0.0F, 0.0F);
				bone31 = new ModelRenderer(this);
				bone31.setRotationPoint(3.0F, 0.9F, 0.0F);
				jaw1.addChild(bone31);
				setRotationAngle(bone31, 0.0F, 0.2182F, 0.0F);
				bone31.cubeList.add(new ModelBox(bone31, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));
				bone32 = new ModelRenderer(this);
				bone32.setRotationPoint(-3.0F, 0.9F, 0.0F);
				jaw1.addChild(bone32);
				setRotationAngle(bone32, 0.0F, -0.2182F, 0.0F);
				bone32.cubeList.add(new ModelBox(bone32, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));
				bone33 = new ModelRenderer(this);
				bone33.setRotationPoint(0.0F, -0.2F, -5.5F);
				jaw1.addChild(bone33);
				setRotationAngle(bone33, 3.1416F, 3.1416F, 0.0F);
				bone33.cubeList.add(new ModelBox(bone33, 0, 1, 1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, false));
				bone33.cubeList.add(new ModelBox(bone33, 0, 1, -1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, true));
				horns1 = new ModelRenderer(this);
				horns1.setRotationPoint(0.0F, -1.65F, -2.0F);
				head1.addChild(horns1);
				bone34 = new ModelRenderer(this);
				bone34.setRotationPoint(-2.3F, -0.25F, 0.4F);
				horns1.addChild(bone34);
				setRotationAngle(bone34, 0.2618F, -0.5236F, 0.0F);
				bone34.cubeList.add(new ModelBox(bone34, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone34.cubeList.add(new ModelBox(bone34, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
				bone34.cubeList.add(new ModelBox(bone34, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
				bone34.cubeList.add(new ModelBox(bone34, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
				bone34.cubeList.add(new ModelBox(bone34, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));
				bone35 = new ModelRenderer(this);
				bone35.setRotationPoint(-1.2F, -0.25F, 0.8F);
				horns1.addChild(bone35);
				setRotationAngle(bone35, 0.4363F, -0.3491F, 0.0F);
				bone35.cubeList.add(new ModelBox(bone35, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone35.cubeList.add(new ModelBox(bone35, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, false));
				bone35.cubeList.add(new ModelBox(bone35, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
				bone35.cubeList.add(new ModelBox(bone35, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));
				bone36 = new ModelRenderer(this);
				bone36.setRotationPoint(1.2F, -0.25F, 0.8F);
				horns1.addChild(bone36);
				setRotationAngle(bone36, 0.4363F, 0.3491F, 0.0F);
				bone36.cubeList.add(new ModelBox(bone36, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone36.cubeList.add(new ModelBox(bone36, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, true));
				bone36.cubeList.add(new ModelBox(bone36, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
				bone36.cubeList.add(new ModelBox(bone36, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));
				bone37 = new ModelRenderer(this);
				bone37.setRotationPoint(2.3F, -0.25F, 0.4F);
				horns1.addChild(bone37);
				setRotationAngle(bone37, 0.2618F, 0.5236F, 0.0F);
				bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
				bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
				bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
				bone37.cubeList.add(new ModelBox(bone37, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));
				neck[2][0] = new ModelRenderer(this);
				neck[2][0].setRotationPoint(-3.5F, 26.0F, -1.0F);
				tails.addChild(neck[2][0]);
				setRotationAngle(neck[2][0], -1.5708F, 0.0F, 0.0F);
				neck[2][0].cubeList.add(new ModelBox(neck[2][0], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[2][1] = new ModelRenderer(this);
				neck[2][1].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][0].addChild(neck[2][1]);
				//setRotationAngle(neck[2][1], -0.0873F, 0.1309F, 0.0F);
				neck[2][1].cubeList.add(new ModelBox(neck[2][1], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[2][2] = new ModelRenderer(this);
				neck[2][2].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][1].addChild(neck[2][2]);
				//setRotationAngle(neck[2][2], -0.2618F, 0.1309F, 0.0F);
				neck[2][2].cubeList.add(new ModelBox(neck[2][2], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[2][3] = new ModelRenderer(this);
				neck[2][3].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][2].addChild(neck[2][3]);
				//setRotationAngle(neck[2][3], 0.2618F, 0.1309F, 0.0F);
				neck[2][3].cubeList.add(new ModelBox(neck[2][3], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[2][4] = new ModelRenderer(this);
				neck[2][4].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][3].addChild(neck[2][4]);
				//setRotationAngle(neck[2][4], 0.2618F, 0.0F, 0.0F);
				neck[2][4].cubeList.add(new ModelBox(neck[2][4], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[2][5] = new ModelRenderer(this);
				neck[2][5].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][4].addChild(neck[2][5]);
				//setRotationAngle(neck[2][5], 0.2618F, 0.0F, 0.0F);
				neck[2][5].cubeList.add(new ModelBox(neck[2][5], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[2][6] = new ModelRenderer(this);
				neck[2][6].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][5].addChild(neck[2][6]);
				//setRotationAngle(neck[2][6], 0.3927F, 0.0F, 0.0873F);
				neck[2][6].cubeList.add(new ModelBox(neck[2][6], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[2][7] = new ModelRenderer(this);
				neck[2][7].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][6].addChild(neck[2][7]);
				//setRotationAngle(neck[2][7], 0.3927F, 0.0F, 0.0F);
				neck[2][7].cubeList.add(new ModelBox(neck[2][7], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[2][8] = new ModelRenderer(this);
				neck[2][8].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][7].addChild(neck[2][8]);
				//setRotationAngle(neck[2][8], 0.3927F, 0.0F, 0.0F);
				neck[2][8].cubeList.add(new ModelBox(neck[2][8], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[2][9] = new ModelRenderer(this);
				neck[2][9].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][8].addChild(neck[2][9]);
				//setRotationAngle(neck[2][9], 0.2618F, 0.0F, 0.0F);
				neck[2][9].cubeList.add(new ModelBox(neck[2][9], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				head2 = new ModelRenderer(this);
				head2.setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[2][9].addChild(head2);
				head2.cubeList.add(new ModelBox(head2, 16, 0, -2.5F, -2.0F, 0.0F, 5, 4, 1, 0.1F, false));
				bone47 = new ModelRenderer(this);
				bone47.setRotationPoint(1.4F, -0.7F, -5.35F);
				head2.addChild(bone47);
				setRotationAngle(bone47, 0.7854F, 0.0F, 0.6109F);
				bone47.cubeList.add(new ModelBox(bone47, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));
				bone48 = new ModelRenderer(this);
				bone48.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone47.addChild(bone48);
				setRotationAngle(bone48, -0.9599F, 0.0F, 0.0F);
				bone48.cubeList.add(new ModelBox(bone48, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));
				bone49 = new ModelRenderer(this);
				bone49.setRotationPoint(-1.4F, -0.7F, -5.35F);
				head2.addChild(bone49);
				setRotationAngle(bone49, 0.7854F, 0.0F, -0.6109F);
				bone49.cubeList.add(new ModelBox(bone49, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));
				bone50 = new ModelRenderer(this);
				bone50.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone49.addChild(bone50);
				setRotationAngle(bone50, -0.9599F, 0.0F, 0.0F);
				bone50.cubeList.add(new ModelBox(bone50, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));
				bone51 = new ModelRenderer(this);
				bone51.setRotationPoint(0.0F, -1.0F, 0.0F);
				head2.addChild(bone51);
				setRotationAngle(bone51, 0.0436F, 0.0873F, 0.0F);
				bone51.cubeList.add(new ModelBox(bone51, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));
				bone52 = new ModelRenderer(this);
				bone52.setRotationPoint(0.0F, -1.0F, 0.0F);
				head2.addChild(bone52);
				setRotationAngle(bone52, 0.0436F, -0.0873F, 0.0F);
				bone52.cubeList.add(new ModelBox(bone52, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));
				bone53 = new ModelRenderer(this);
				bone53.setRotationPoint(-0.15F, -1.1F, -2.5F);
				head2.addChild(bone53);
				setRotationAngle(bone53, 0.5236F, 0.2618F, 0.0F);
				bone53.cubeList.add(new ModelBox(bone53, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));
				bone54 = new ModelRenderer(this);
				bone54.setRotationPoint(0.15F, -1.1F, -2.5F);
				head2.addChild(bone54);
				setRotationAngle(bone54, 0.5236F, -0.2618F, 0.0F);
				bone54.cubeList.add(new ModelBox(bone54, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));
				bone55 = new ModelRenderer(this);
				bone55.setRotationPoint(2.6F, 0.1F, -3.95F);
				head2.addChild(bone55);
				setRotationAngle(bone55, 0.0F, 0.2618F, 0.0F);
				bone55.cubeList.add(new ModelBox(bone55, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
				bone55.cubeList.add(new ModelBox(bone55, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));
				bone74 = new ModelRenderer(this);
				bone74.setRotationPoint(-2.65F, 0.1F, -3.95F);
				head2.addChild(bone74);
				setRotationAngle(bone74, 0.0F, -0.2618F, 0.0F);
				bone74.cubeList.add(new ModelBox(bone74, 10, 19, 0.05F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
				bone74.cubeList.add(new ModelBox(bone74, 0, 19, 0.05F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));
				bone75 = new ModelRenderer(this);
				bone75.setRotationPoint(1.6F, 1.8F, -5.95F);
				head2.addChild(bone75);
				bone75.cubeList.add(new ModelBox(bone75, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
				bone75.cubeList.add(new ModelBox(bone75, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));
				jaw2 = new ModelRenderer(this);
				jaw2.setRotationPoint(0.0F, 0.5F, 0.0F);
				head2.addChild(jaw2);
				setRotationAngle(jaw2, 0.5236F, 0.0F, 0.0F);
				bone76 = new ModelRenderer(this);
				bone76.setRotationPoint(3.0F, 0.9F, 0.0F);
				jaw2.addChild(bone76);
				setRotationAngle(bone76, 0.0F, 0.2182F, 0.0F);
				bone76.cubeList.add(new ModelBox(bone76, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));
				bone77 = new ModelRenderer(this);
				bone77.setRotationPoint(-3.0F, 0.9F, 0.0F);
				jaw2.addChild(bone77);
				setRotationAngle(bone77, 0.0F, -0.2182F, 0.0F);
				bone77.cubeList.add(new ModelBox(bone77, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));
				bone78 = new ModelRenderer(this);
				bone78.setRotationPoint(0.0F, -0.2F, -5.5F);
				jaw2.addChild(bone78);
				setRotationAngle(bone78, 3.1416F, 3.1416F, 0.0F);
				bone78.cubeList.add(new ModelBox(bone78, 0, 1, 1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, false));
				bone78.cubeList.add(new ModelBox(bone78, 0, 1, -1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, true));
				horns2 = new ModelRenderer(this);
				horns2.setRotationPoint(0.0F, -1.65F, -2.0F);
				head2.addChild(horns2);
				bone79 = new ModelRenderer(this);
				bone79.setRotationPoint(-2.3F, -0.25F, 0.4F);
				horns2.addChild(bone79);
				setRotationAngle(bone79, 0.2618F, -0.5236F, 0.0F);
				bone79.cubeList.add(new ModelBox(bone79, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone79.cubeList.add(new ModelBox(bone79, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
				bone79.cubeList.add(new ModelBox(bone79, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
				bone79.cubeList.add(new ModelBox(bone79, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
				bone79.cubeList.add(new ModelBox(bone79, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));
				bone80 = new ModelRenderer(this);
				bone80.setRotationPoint(-1.2F, -0.25F, 0.8F);
				horns2.addChild(bone80);
				setRotationAngle(bone80, 0.4363F, -0.3491F, 0.0F);
				bone80.cubeList.add(new ModelBox(bone80, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone80.cubeList.add(new ModelBox(bone80, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, false));
				bone80.cubeList.add(new ModelBox(bone80, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
				bone80.cubeList.add(new ModelBox(bone80, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));
				bone81 = new ModelRenderer(this);
				bone81.setRotationPoint(1.2F, -0.25F, 0.8F);
				horns2.addChild(bone81);
				setRotationAngle(bone81, 0.4363F, 0.3491F, 0.0F);
				bone81.cubeList.add(new ModelBox(bone81, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone81.cubeList.add(new ModelBox(bone81, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, true));
				bone81.cubeList.add(new ModelBox(bone81, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
				bone81.cubeList.add(new ModelBox(bone81, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));
				bone82 = new ModelRenderer(this);
				bone82.setRotationPoint(2.3F, -0.25F, 0.4F);
				horns2.addChild(bone82);
				setRotationAngle(bone82, 0.2618F, 0.5236F, 0.0F);
				bone82.cubeList.add(new ModelBox(bone82, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone82.cubeList.add(new ModelBox(bone82, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
				bone82.cubeList.add(new ModelBox(bone82, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
				bone82.cubeList.add(new ModelBox(bone82, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
				bone82.cubeList.add(new ModelBox(bone82, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));
				neck[3][0] = new ModelRenderer(this);
				neck[3][0].setRotationPoint(3.5F, 26.0F, -1.0F);
				tails.addChild(neck[3][0]);
				setRotationAngle(neck[3][0], -1.5708F, 0.0F, 0.0F);
				neck[3][0].cubeList.add(new ModelBox(neck[3][0], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[3][1] = new ModelRenderer(this);
				neck[3][1].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][0].addChild(neck[3][1]);
				//setRotationAngle(neck[3][1], -0.0873F, -0.1309F, 0.0F);
				neck[3][1].cubeList.add(new ModelBox(neck[3][1], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[3][2] = new ModelRenderer(this);
				neck[3][2].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][1].addChild(neck[3][2]);
				//setRotationAngle(neck[3][2], -0.2618F, -0.1309F, 0.0F);
				neck[3][2].cubeList.add(new ModelBox(neck[3][2], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[3][3] = new ModelRenderer(this);
				neck[3][3].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][2].addChild(neck[3][3]);
				//setRotationAngle(neck[3][3], 0.2618F, -0.1309F, 0.0F);
				neck[3][3].cubeList.add(new ModelBox(neck[3][3], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[3][4] = new ModelRenderer(this);
				neck[3][4].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][3].addChild(neck[3][4]);
				//setRotationAngle(neck[3][4], 0.2618F, 0.0F, 0.0F);
				neck[3][4].cubeList.add(new ModelBox(neck[3][4], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[3][5] = new ModelRenderer(this);
				neck[3][5].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][4].addChild(neck[3][5]);
				//setRotationAngle(neck[3][5], 0.2618F, 0.0F, 0.0F);
				neck[3][5].cubeList.add(new ModelBox(neck[3][5], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[3][6] = new ModelRenderer(this);
				neck[3][6].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][5].addChild(neck[3][6]);
				//setRotationAngle(neck[3][6], 0.3927F, 0.0F, -0.0873F);
				neck[3][6].cubeList.add(new ModelBox(neck[3][6], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[3][7] = new ModelRenderer(this);
				neck[3][7].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][6].addChild(neck[3][7]);
				//setRotationAngle(neck[3][7], 0.3927F, 0.0F, 0.0F);
				neck[3][7].cubeList.add(new ModelBox(neck[3][7], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[3][8] = new ModelRenderer(this);
				neck[3][8].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][7].addChild(neck[3][8]);
				//setRotationAngle(neck[3][8], 0.3927F, 0.0F, 0.0F);
				neck[3][8].cubeList.add(new ModelBox(neck[3][8], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[3][9] = new ModelRenderer(this);
				neck[3][9].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][8].addChild(neck[3][9]);
				//setRotationAngle(neck[3][9], 0.2618F, 0.0F, 0.0F);
				neck[3][9].cubeList.add(new ModelBox(neck[3][9], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				head3 = new ModelRenderer(this);
				head3.setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[3][9].addChild(head3);
				head3.cubeList.add(new ModelBox(head3, 16, 0, -2.5F, -2.0F, 0.0F, 5, 4, 1, 0.1F, false));
				bone92 = new ModelRenderer(this);
				bone92.setRotationPoint(1.4F, -0.7F, -5.35F);
				head3.addChild(bone92);
				setRotationAngle(bone92, 0.7854F, 0.0F, 0.6109F);
				bone92.cubeList.add(new ModelBox(bone92, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));
				bone93 = new ModelRenderer(this);
				bone93.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone92.addChild(bone93);
				setRotationAngle(bone93, -0.9599F, 0.0F, 0.0F);
				bone93.cubeList.add(new ModelBox(bone93, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));
				bone94 = new ModelRenderer(this);
				bone94.setRotationPoint(-1.4F, -0.7F, -5.35F);
				head3.addChild(bone94);
				setRotationAngle(bone94, 0.7854F, 0.0F, -0.6109F);
				bone94.cubeList.add(new ModelBox(bone94, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));
				bone95 = new ModelRenderer(this);
				bone95.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone94.addChild(bone95);
				setRotationAngle(bone95, -0.9599F, 0.0F, 0.0F);
				bone95.cubeList.add(new ModelBox(bone95, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));
				bone96 = new ModelRenderer(this);
				bone96.setRotationPoint(0.0F, -1.0F, 0.0F);
				head3.addChild(bone96);
				setRotationAngle(bone96, 0.0436F, 0.0873F, 0.0F);
				bone96.cubeList.add(new ModelBox(bone96, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));
				bone97 = new ModelRenderer(this);
				bone97.setRotationPoint(0.0F, -1.0F, 0.0F);
				head3.addChild(bone97);
				setRotationAngle(bone97, 0.0436F, -0.0873F, 0.0F);
				bone97.cubeList.add(new ModelBox(bone97, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));
				bone98 = new ModelRenderer(this);
				bone98.setRotationPoint(-0.15F, -1.1F, -2.5F);
				head3.addChild(bone98);
				setRotationAngle(bone98, 0.5236F, 0.2618F, 0.0F);
				bone98.cubeList.add(new ModelBox(bone98, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));
				bone99 = new ModelRenderer(this);
				bone99.setRotationPoint(0.15F, -1.1F, -2.5F);
				head3.addChild(bone99);
				setRotationAngle(bone99, 0.5236F, -0.2618F, 0.0F);
				bone99.cubeList.add(new ModelBox(bone99, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));
				bone100 = new ModelRenderer(this);
				bone100.setRotationPoint(2.6F, 0.1F, -3.95F);
				head3.addChild(bone100);
				setRotationAngle(bone100, 0.0F, 0.2618F, 0.0F);
				bone100.cubeList.add(new ModelBox(bone100, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
				bone100.cubeList.add(new ModelBox(bone100, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));
				bone101 = new ModelRenderer(this);
				bone101.setRotationPoint(-2.65F, 0.1F, -3.95F);
				head3.addChild(bone101);
				setRotationAngle(bone101, 0.0F, -0.2618F, 0.0F);
				bone101.cubeList.add(new ModelBox(bone101, 10, 19, 0.05F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
				bone101.cubeList.add(new ModelBox(bone101, 0, 19, 0.05F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));
				bone102 = new ModelRenderer(this);
				bone102.setRotationPoint(1.6F, 1.8F, -5.95F);
				head3.addChild(bone102);
				bone102.cubeList.add(new ModelBox(bone102, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
				bone102.cubeList.add(new ModelBox(bone102, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));
				jaw3 = new ModelRenderer(this);
				jaw3.setRotationPoint(0.0F, 0.5F, 0.0F);
				head3.addChild(jaw3);
				setRotationAngle(jaw3, 0.5236F, 0.0F, 0.0F);
				bone103 = new ModelRenderer(this);
				bone103.setRotationPoint(3.0F, 0.9F, 0.0F);
				jaw3.addChild(bone103);
				setRotationAngle(bone103, 0.0F, 0.2182F, 0.0F);
				bone103.cubeList.add(new ModelBox(bone103, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));
				bone104 = new ModelRenderer(this);
				bone104.setRotationPoint(-3.0F, 0.9F, 0.0F);
				jaw3.addChild(bone104);
				setRotationAngle(bone104, 0.0F, -0.2182F, 0.0F);
				bone104.cubeList.add(new ModelBox(bone104, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));
				bone105 = new ModelRenderer(this);
				bone105.setRotationPoint(0.0F, -0.2F, -5.5F);
				jaw3.addChild(bone105);
				setRotationAngle(bone105, 3.1416F, 3.1416F, 0.0F);
				bone105.cubeList.add(new ModelBox(bone105, 0, 1, 1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, false));
				bone105.cubeList.add(new ModelBox(bone105, 0, 1, -1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, true));
				horns3 = new ModelRenderer(this);
				horns3.setRotationPoint(0.0F, -1.65F, -2.0F);
				head3.addChild(horns3);
				bone106 = new ModelRenderer(this);
				bone106.setRotationPoint(-2.3F, -0.25F, 0.4F);
				horns3.addChild(bone106);
				setRotationAngle(bone106, 0.2618F, -0.5236F, 0.0F);
				bone106.cubeList.add(new ModelBox(bone106, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone106.cubeList.add(new ModelBox(bone106, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
				bone106.cubeList.add(new ModelBox(bone106, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
				bone106.cubeList.add(new ModelBox(bone106, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
				bone106.cubeList.add(new ModelBox(bone106, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));
				bone107 = new ModelRenderer(this);
				bone107.setRotationPoint(-1.2F, -0.25F, 0.8F);
				horns3.addChild(bone107);
				setRotationAngle(bone107, 0.4363F, -0.3491F, 0.0F);
				bone107.cubeList.add(new ModelBox(bone107, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone107.cubeList.add(new ModelBox(bone107, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, false));
				bone107.cubeList.add(new ModelBox(bone107, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
				bone107.cubeList.add(new ModelBox(bone107, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));
				bone108 = new ModelRenderer(this);
				bone108.setRotationPoint(1.2F, -0.25F, 0.8F);
				horns3.addChild(bone108);
				setRotationAngle(bone108, 0.4363F, 0.3491F, 0.0F);
				bone108.cubeList.add(new ModelBox(bone108, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone108.cubeList.add(new ModelBox(bone108, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, true));
				bone108.cubeList.add(new ModelBox(bone108, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
				bone108.cubeList.add(new ModelBox(bone108, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));
				bone109 = new ModelRenderer(this);
				bone109.setRotationPoint(2.3F, -0.25F, 0.4F);
				horns3.addChild(bone109);
				setRotationAngle(bone109, 0.2618F, 0.5236F, 0.0F);
				bone109.cubeList.add(new ModelBox(bone109, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone109.cubeList.add(new ModelBox(bone109, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
				bone109.cubeList.add(new ModelBox(bone109, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
				bone109.cubeList.add(new ModelBox(bone109, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
				bone109.cubeList.add(new ModelBox(bone109, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));
				neck[4][0] = new ModelRenderer(this);
				neck[4][0].setRotationPoint(-3.5F, 26.0F, 1.0F);
				tails.addChild(neck[4][0]);
				setRotationAngle(neck[4][0], -1.5708F, 0.0F, 0.0F);
				neck[4][0].cubeList.add(new ModelBox(neck[4][0], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[4][1] = new ModelRenderer(this);
				neck[4][1].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][0].addChild(neck[4][1]);
				//setRotationAngle(neck[4][1], -0.2618F, 0.2618F, 0.0F);
				neck[4][1].cubeList.add(new ModelBox(neck[4][1], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[4][2] = new ModelRenderer(this);
				neck[4][2].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][1].addChild(neck[4][2]);
				//setRotationAngle(neck[4][2], -0.2618F, 0.2618F, 0.0F);
				neck[4][2].cubeList.add(new ModelBox(neck[4][2], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[4][3] = new ModelRenderer(this);
				neck[4][3].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][2].addChild(neck[4][3]);
				//setRotationAngle(neck[4][3], 0.2618F, 0.2618F, 0.0F);
				neck[4][3].cubeList.add(new ModelBox(neck[4][3], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[4][4] = new ModelRenderer(this);
				neck[4][4].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][3].addChild(neck[4][4]);
				//setRotationAngle(neck[4][4], 0.2618F, 0.2618F, 0.0F);
				neck[4][4].cubeList.add(new ModelBox(neck[4][4], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[4][5] = new ModelRenderer(this);
				neck[4][5].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][4].addChild(neck[4][5]);
				//setRotationAngle(neck[4][5], 0.2618F, -0.2618F, 0.1745F);
				neck[4][5].cubeList.add(new ModelBox(neck[4][5], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[4][6] = new ModelRenderer(this);
				neck[4][6].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][5].addChild(neck[4][6]);
				//setRotationAngle(neck[4][6], 0.4363F, -0.2618F, 0.0873F);
				neck[4][6].cubeList.add(new ModelBox(neck[4][6], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[4][7] = new ModelRenderer(this);
				neck[4][7].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][6].addChild(neck[4][7]);
				//setRotationAngle(neck[4][7], 0.4363F, 0.0F, 0.0873F);
				neck[4][7].cubeList.add(new ModelBox(neck[4][7], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[4][8] = new ModelRenderer(this);
				neck[4][8].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][7].addChild(neck[4][8]);
				//setRotationAngle(neck[4][8], 0.5236F, 0.0F, 0.0F);
				neck[4][8].cubeList.add(new ModelBox(neck[4][8], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[4][9] = new ModelRenderer(this);
				neck[4][9].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][8].addChild(neck[4][9]);
				//setRotationAngle(neck[4][9], 0.2618F, 0.0F, 0.0F);
				neck[4][9].cubeList.add(new ModelBox(neck[4][9], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				head4 = new ModelRenderer(this);
				head4.setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[4][9].addChild(head4);
				head4.cubeList.add(new ModelBox(head4, 16, 0, -2.5F, -2.0F, 0.0F, 5, 4, 1, 0.1F, false));
				bone119 = new ModelRenderer(this);
				bone119.setRotationPoint(1.4F, -0.7F, -5.35F);
				head4.addChild(bone119);
				setRotationAngle(bone119, 0.7854F, 0.0F, 0.6109F);
				bone119.cubeList.add(new ModelBox(bone119, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));
				bone120 = new ModelRenderer(this);
				bone120.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone119.addChild(bone120);
				setRotationAngle(bone120, -0.9599F, 0.0F, 0.0F);
				bone120.cubeList.add(new ModelBox(bone120, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));
				bone121 = new ModelRenderer(this);
				bone121.setRotationPoint(-1.4F, -0.7F, -5.35F);
				head4.addChild(bone121);
				setRotationAngle(bone121, 0.7854F, 0.0F, -0.6109F);
				bone121.cubeList.add(new ModelBox(bone121, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));
				bone122 = new ModelRenderer(this);
				bone122.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone121.addChild(bone122);
				setRotationAngle(bone122, -0.9599F, 0.0F, 0.0F);
				bone122.cubeList.add(new ModelBox(bone122, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));
				bone123 = new ModelRenderer(this);
				bone123.setRotationPoint(0.0F, -1.0F, 0.0F);
				head4.addChild(bone123);
				setRotationAngle(bone123, 0.0436F, 0.0873F, 0.0F);
				bone123.cubeList.add(new ModelBox(bone123, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));
				bone124 = new ModelRenderer(this);
				bone124.setRotationPoint(0.0F, -1.0F, 0.0F);
				head4.addChild(bone124);
				setRotationAngle(bone124, 0.0436F, -0.0873F, 0.0F);
				bone124.cubeList.add(new ModelBox(bone124, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));
				bone125 = new ModelRenderer(this);
				bone125.setRotationPoint(-0.15F, -1.1F, -2.5F);
				head4.addChild(bone125);
				setRotationAngle(bone125, 0.5236F, 0.2618F, 0.0F);
				bone125.cubeList.add(new ModelBox(bone125, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));
				bone126 = new ModelRenderer(this);
				bone126.setRotationPoint(0.15F, -1.1F, -2.5F);
				head4.addChild(bone126);
				setRotationAngle(bone126, 0.5236F, -0.2618F, 0.0F);
				bone126.cubeList.add(new ModelBox(bone126, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));
				bone127 = new ModelRenderer(this);
				bone127.setRotationPoint(2.6F, 0.1F, -3.95F);
				head4.addChild(bone127);
				setRotationAngle(bone127, 0.0F, 0.2618F, 0.0F);
				bone127.cubeList.add(new ModelBox(bone127, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
				bone127.cubeList.add(new ModelBox(bone127, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));
				bone128 = new ModelRenderer(this);
				bone128.setRotationPoint(-2.65F, 0.1F, -3.95F);
				head4.addChild(bone128);
				setRotationAngle(bone128, 0.0F, -0.2618F, 0.0F);
				bone128.cubeList.add(new ModelBox(bone128, 10, 19, 0.05F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
				bone128.cubeList.add(new ModelBox(bone128, 0, 19, 0.05F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));
				bone129 = new ModelRenderer(this);
				bone129.setRotationPoint(1.6F, 1.8F, -5.95F);
				head4.addChild(bone129);
				bone129.cubeList.add(new ModelBox(bone129, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
				bone129.cubeList.add(new ModelBox(bone129, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));
				jaw4 = new ModelRenderer(this);
				jaw4.setRotationPoint(0.0F, 0.5F, 0.0F);
				head4.addChild(jaw4);
				setRotationAngle(jaw4, 0.5236F, 0.0F, 0.0F);
				bone130 = new ModelRenderer(this);
				bone130.setRotationPoint(3.0F, 0.9F, 0.0F);
				jaw4.addChild(bone130);
				setRotationAngle(bone130, 0.0F, 0.2182F, 0.0F);
				bone130.cubeList.add(new ModelBox(bone130, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));
				bone131 = new ModelRenderer(this);
				bone131.setRotationPoint(-3.0F, 0.9F, 0.0F);
				jaw4.addChild(bone131);
				setRotationAngle(bone131, 0.0F, -0.2182F, 0.0F);
				bone131.cubeList.add(new ModelBox(bone131, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));
				bone132 = new ModelRenderer(this);
				bone132.setRotationPoint(0.0F, -0.2F, -5.5F);
				jaw4.addChild(bone132);
				setRotationAngle(bone132, 3.1416F, 3.1416F, 0.0F);
				bone132.cubeList.add(new ModelBox(bone132, 0, 1, 1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, false));
				bone132.cubeList.add(new ModelBox(bone132, 0, 1, -1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, true));
				horns4 = new ModelRenderer(this);
				horns4.setRotationPoint(0.0F, -1.65F, -2.0F);
				head4.addChild(horns4);
				bone133 = new ModelRenderer(this);
				bone133.setRotationPoint(-2.3F, -0.25F, 0.4F);
				horns4.addChild(bone133);
				setRotationAngle(bone133, 0.2618F, -0.5236F, 0.0F);
				bone133.cubeList.add(new ModelBox(bone133, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone133.cubeList.add(new ModelBox(bone133, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
				bone133.cubeList.add(new ModelBox(bone133, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
				bone133.cubeList.add(new ModelBox(bone133, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
				bone133.cubeList.add(new ModelBox(bone133, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));
				bone134 = new ModelRenderer(this);
				bone134.setRotationPoint(-1.2F, -0.25F, 0.8F);
				horns4.addChild(bone134);
				setRotationAngle(bone134, 0.4363F, -0.3491F, 0.0F);
				bone134.cubeList.add(new ModelBox(bone134, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone134.cubeList.add(new ModelBox(bone134, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, false));
				bone134.cubeList.add(new ModelBox(bone134, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
				bone134.cubeList.add(new ModelBox(bone134, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));
				bone135 = new ModelRenderer(this);
				bone135.setRotationPoint(1.2F, -0.25F, 0.8F);
				horns4.addChild(bone135);
				setRotationAngle(bone135, 0.4363F, 0.3491F, 0.0F);
				bone135.cubeList.add(new ModelBox(bone135, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone135.cubeList.add(new ModelBox(bone135, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, true));
				bone135.cubeList.add(new ModelBox(bone135, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
				bone135.cubeList.add(new ModelBox(bone135, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));
				bone136 = new ModelRenderer(this);
				bone136.setRotationPoint(2.3F, -0.25F, 0.4F);
				horns4.addChild(bone136);
				setRotationAngle(bone136, 0.2618F, 0.5236F, 0.0F);
				bone136.cubeList.add(new ModelBox(bone136, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone136.cubeList.add(new ModelBox(bone136, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
				bone136.cubeList.add(new ModelBox(bone136, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
				bone136.cubeList.add(new ModelBox(bone136, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
				bone136.cubeList.add(new ModelBox(bone136, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));
				neck[5][0] = new ModelRenderer(this);
				neck[5][0].setRotationPoint(3.5F, 26.0F, 1.0F);
				tails.addChild(neck[5][0]);
				setRotationAngle(neck[5][0], -1.5708F, 0.0F, 0.0F);
				neck[5][0].cubeList.add(new ModelBox(neck[5][0], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[5][1] = new ModelRenderer(this);
				neck[5][1].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][0].addChild(neck[5][1]);
				//setRotationAngle(neck[5][1], -0.2618F, -0.2618F, 0.0F);
				neck[5][1].cubeList.add(new ModelBox(neck[5][1], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[5][2] = new ModelRenderer(this);
				neck[5][2].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][1].addChild(neck[5][2]);
				//setRotationAngle(neck[5][2], -0.2618F, -0.2618F, 0.0F);
				neck[5][2].cubeList.add(new ModelBox(neck[5][2], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[5][3] = new ModelRenderer(this);
				neck[5][3].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][2].addChild(neck[5][3]);
				//setRotationAngle(neck[5][3], 0.2618F, -0.2618F, 0.0F);
				neck[5][3].cubeList.add(new ModelBox(neck[5][3], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[5][4] = new ModelRenderer(this);
				neck[5][4].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][3].addChild(neck[5][4]);
				//setRotationAngle(neck[5][4], 0.2618F, -0.2618F, 0.0F);
				neck[5][4].cubeList.add(new ModelBox(neck[5][4], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[5][5] = new ModelRenderer(this);
				neck[5][5].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][4].addChild(neck[5][5]);
				//setRotationAngle(neck[5][5], 0.2618F, 0.2618F, -0.1745F);
				neck[5][5].cubeList.add(new ModelBox(neck[5][5], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[5][6] = new ModelRenderer(this);
				neck[5][6].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][5].addChild(neck[5][6]);
				//setRotationAngle(neck[5][6], 0.4363F, 0.2618F, -0.1745F);
				neck[5][6].cubeList.add(new ModelBox(neck[5][6], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[5][7] = new ModelRenderer(this);
				neck[5][7].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][6].addChild(neck[5][7]);
				//setRotationAngle(neck[5][7], 0.4363F, 0.0F, 0.0F);
				neck[5][7].cubeList.add(new ModelBox(neck[5][7], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[5][8] = new ModelRenderer(this);
				neck[5][8].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][7].addChild(neck[5][8]);
				//setRotationAngle(neck[5][8], 0.5236F, 0.0F, 0.0F);
				neck[5][8].cubeList.add(new ModelBox(neck[5][8], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[5][9] = new ModelRenderer(this);
				neck[5][9].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][8].addChild(neck[5][9]);
				//setRotationAngle(neck[5][9], 0.2618F, 0.0F, 0.0F);
				neck[5][9].cubeList.add(new ModelBox(neck[5][9], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				head5 = new ModelRenderer(this);
				head5.setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[5][9].addChild(head5);
				head5.cubeList.add(new ModelBox(head5, 16, 0, -2.5F, -2.0F, 0.0F, 5, 4, 1, 0.1F, false));
				bone146 = new ModelRenderer(this);
				bone146.setRotationPoint(1.4F, -0.7F, -5.35F);
				head5.addChild(bone146);
				setRotationAngle(bone146, 0.7854F, 0.0F, 0.6109F);
				bone146.cubeList.add(new ModelBox(bone146, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));
				bone147 = new ModelRenderer(this);
				bone147.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone146.addChild(bone147);
				setRotationAngle(bone147, -0.9599F, 0.0F, 0.0F);
				bone147.cubeList.add(new ModelBox(bone147, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));
				bone148 = new ModelRenderer(this);
				bone148.setRotationPoint(-1.4F, -0.7F, -5.35F);
				head5.addChild(bone148);
				setRotationAngle(bone148, 0.7854F, 0.0F, -0.6109F);
				bone148.cubeList.add(new ModelBox(bone148, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));
				bone149 = new ModelRenderer(this);
				bone149.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone148.addChild(bone149);
				setRotationAngle(bone149, -0.9599F, 0.0F, 0.0F);
				bone149.cubeList.add(new ModelBox(bone149, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));
				bone150 = new ModelRenderer(this);
				bone150.setRotationPoint(0.0F, -1.0F, 0.0F);
				head5.addChild(bone150);
				setRotationAngle(bone150, 0.0436F, 0.0873F, 0.0F);
				bone150.cubeList.add(new ModelBox(bone150, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));
				bone151 = new ModelRenderer(this);
				bone151.setRotationPoint(0.0F, -1.0F, 0.0F);
				head5.addChild(bone151);
				setRotationAngle(bone151, 0.0436F, -0.0873F, 0.0F);
				bone151.cubeList.add(new ModelBox(bone151, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));
				bone152 = new ModelRenderer(this);
				bone152.setRotationPoint(-0.15F, -1.1F, -2.5F);
				head5.addChild(bone152);
				setRotationAngle(bone152, 0.5236F, 0.2618F, 0.0F);
				bone152.cubeList.add(new ModelBox(bone152, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));
				bone153 = new ModelRenderer(this);
				bone153.setRotationPoint(0.15F, -1.1F, -2.5F);
				head5.addChild(bone153);
				setRotationAngle(bone153, 0.5236F, -0.2618F, 0.0F);
				bone153.cubeList.add(new ModelBox(bone153, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));
				bone154 = new ModelRenderer(this);
				bone154.setRotationPoint(2.6F, 0.1F, -3.95F);
				head5.addChild(bone154);
				setRotationAngle(bone154, 0.0F, 0.2618F, 0.0F);
				bone154.cubeList.add(new ModelBox(bone154, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
				bone154.cubeList.add(new ModelBox(bone154, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));
				bone155 = new ModelRenderer(this);
				bone155.setRotationPoint(-2.65F, 0.1F, -3.95F);
				head5.addChild(bone155);
				setRotationAngle(bone155, 0.0F, -0.2618F, 0.0F);
				bone155.cubeList.add(new ModelBox(bone155, 10, 19, 0.05F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
				bone155.cubeList.add(new ModelBox(bone155, 0, 19, 0.05F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));
				bone156 = new ModelRenderer(this);
				bone156.setRotationPoint(1.6F, 1.8F, -5.95F);
				head5.addChild(bone156);
				bone156.cubeList.add(new ModelBox(bone156, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
				bone156.cubeList.add(new ModelBox(bone156, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));
				jaw5 = new ModelRenderer(this);
				jaw5.setRotationPoint(0.0F, 0.5F, 0.0F);
				head5.addChild(jaw5);
				setRotationAngle(jaw5, 0.5236F, 0.0F, 0.0F);
				bone157 = new ModelRenderer(this);
				bone157.setRotationPoint(3.0F, 0.9F, 0.0F);
				jaw5.addChild(bone157);
				setRotationAngle(bone157, 0.0F, 0.2182F, 0.0F);
				bone157.cubeList.add(new ModelBox(bone157, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));
				bone158 = new ModelRenderer(this);
				bone158.setRotationPoint(-3.0F, 0.9F, 0.0F);
				jaw5.addChild(bone158);
				setRotationAngle(bone158, 0.0F, -0.2182F, 0.0F);
				bone158.cubeList.add(new ModelBox(bone158, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));
				bone159 = new ModelRenderer(this);
				bone159.setRotationPoint(0.0F, -0.2F, -5.5F);
				jaw5.addChild(bone159);
				setRotationAngle(bone159, 3.1416F, 3.1416F, 0.0F);
				bone159.cubeList.add(new ModelBox(bone159, 0, 1, 1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, false));
				bone159.cubeList.add(new ModelBox(bone159, 0, 1, -1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, true));
				horns5 = new ModelRenderer(this);
				horns5.setRotationPoint(0.0F, -1.65F, -2.0F);
				head5.addChild(horns5);
				bone160 = new ModelRenderer(this);
				bone160.setRotationPoint(-2.3F, -0.25F, 0.4F);
				horns5.addChild(bone160);
				setRotationAngle(bone160, 0.2618F, -0.5236F, 0.0F);
				bone160.cubeList.add(new ModelBox(bone160, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone160.cubeList.add(new ModelBox(bone160, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
				bone160.cubeList.add(new ModelBox(bone160, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
				bone160.cubeList.add(new ModelBox(bone160, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
				bone160.cubeList.add(new ModelBox(bone160, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));
				bone161 = new ModelRenderer(this);
				bone161.setRotationPoint(-1.2F, -0.25F, 0.8F);
				horns5.addChild(bone161);
				setRotationAngle(bone161, 0.4363F, -0.3491F, 0.0F);
				bone161.cubeList.add(new ModelBox(bone161, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone161.cubeList.add(new ModelBox(bone161, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, false));
				bone161.cubeList.add(new ModelBox(bone161, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
				bone161.cubeList.add(new ModelBox(bone161, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));
				bone162 = new ModelRenderer(this);
				bone162.setRotationPoint(1.2F, -0.25F, 0.8F);
				horns5.addChild(bone162);
				setRotationAngle(bone162, 0.4363F, 0.3491F, 0.0F);
				bone162.cubeList.add(new ModelBox(bone162, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone162.cubeList.add(new ModelBox(bone162, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, true));
				bone162.cubeList.add(new ModelBox(bone162, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
				bone162.cubeList.add(new ModelBox(bone162, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));
				bone163 = new ModelRenderer(this);
				bone163.setRotationPoint(2.3F, -0.25F, 0.4F);
				horns5.addChild(bone163);
				setRotationAngle(bone163, 0.2618F, 0.5236F, 0.0F);
				bone163.cubeList.add(new ModelBox(bone163, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone163.cubeList.add(new ModelBox(bone163, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
				bone163.cubeList.add(new ModelBox(bone163, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
				bone163.cubeList.add(new ModelBox(bone163, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
				bone163.cubeList.add(new ModelBox(bone163, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));
				neck[6][0] = new ModelRenderer(this);
				neck[6][0].setRotationPoint(-1.5F, 26.0F, 3.0F);
				tails.addChild(neck[6][0]);
				setRotationAngle(neck[6][0], -1.5708F, 0.0F, 0.0F);
				neck[6][0].cubeList.add(new ModelBox(neck[6][0], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[6][1] = new ModelRenderer(this);
				neck[6][1].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][0].addChild(neck[6][1]);
				//setRotationAngle(neck[6][1], -0.2618F, 0.1309F, 0.0F);
				neck[6][1].cubeList.add(new ModelBox(neck[6][1], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[6][2] = new ModelRenderer(this);
				neck[6][2].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][1].addChild(neck[6][2]);
				//setRotationAngle(neck[6][2], -0.2618F, 0.1309F, 0.0F);
				neck[6][2].cubeList.add(new ModelBox(neck[6][2], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[6][3] = new ModelRenderer(this);
				neck[6][3].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][2].addChild(neck[6][3]);
				//setRotationAngle(neck[6][3], 0.2618F, 0.1309F, 0.0F);
				neck[6][3].cubeList.add(new ModelBox(neck[6][3], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[6][4] = new ModelRenderer(this);
				neck[6][4].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][3].addChild(neck[6][4]);
				//setRotationAngle(neck[6][4], 0.2618F, -0.1309F, 0.0F);
				neck[6][4].cubeList.add(new ModelBox(neck[6][4], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[6][5] = new ModelRenderer(this);
				neck[6][5].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][4].addChild(neck[6][5]);
				//setRotationAngle(neck[6][5], 0.2618F, -0.0873F, 0.0F);
				neck[6][5].cubeList.add(new ModelBox(neck[6][5], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[6][6] = new ModelRenderer(this);
				neck[6][6].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][5].addChild(neck[6][6]);
				//setRotationAngle(neck[6][6], 0.2618F, 0.0F, 0.0F);
				neck[6][6].cubeList.add(new ModelBox(neck[6][6], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[6][7] = new ModelRenderer(this);
				neck[6][7].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][6].addChild(neck[6][7]);
				//setRotationAngle(neck[6][7], 0.2618F, 0.0F, 0.0F);
				neck[6][7].cubeList.add(new ModelBox(neck[6][7], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[6][8] = new ModelRenderer(this);
				neck[6][8].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][7].addChild(neck[6][8]);
				//setRotationAngle(neck[6][8], 0.3491F, 0.0F, 0.0F);
				neck[6][8].cubeList.add(new ModelBox(neck[6][8], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[6][9] = new ModelRenderer(this);
				neck[6][9].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][8].addChild(neck[6][9]);
				//setRotationAngle(neck[6][9], 0.2618F, 0.0F, 0.0F);
				neck[6][9].cubeList.add(new ModelBox(neck[6][9], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				head6 = new ModelRenderer(this);
				head6.setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[6][9].addChild(head6);
				head6.cubeList.add(new ModelBox(head6, 16, 0, -2.5F, -2.0F, 0.0F, 5, 4, 1, 0.1F, false));
				bone173 = new ModelRenderer(this);
				bone173.setRotationPoint(1.4F, -0.7F, -5.35F);
				head6.addChild(bone173);
				setRotationAngle(bone173, 0.7854F, 0.0F, 0.6109F);
				bone173.cubeList.add(new ModelBox(bone173, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));
				bone174 = new ModelRenderer(this);
				bone174.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone173.addChild(bone174);
				setRotationAngle(bone174, -0.9599F, 0.0F, 0.0F);
				bone174.cubeList.add(new ModelBox(bone174, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));
				bone175 = new ModelRenderer(this);
				bone175.setRotationPoint(-1.4F, -0.7F, -5.35F);
				head6.addChild(bone175);
				setRotationAngle(bone175, 0.7854F, 0.0F, -0.6109F);
				bone175.cubeList.add(new ModelBox(bone175, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));
				bone176 = new ModelRenderer(this);
				bone176.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone175.addChild(bone176);
				setRotationAngle(bone176, -0.9599F, 0.0F, 0.0F);
				bone176.cubeList.add(new ModelBox(bone176, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));
				bone177 = new ModelRenderer(this);
				bone177.setRotationPoint(0.0F, -1.0F, 0.0F);
				head6.addChild(bone177);
				setRotationAngle(bone177, 0.0436F, 0.0873F, 0.0F);
				bone177.cubeList.add(new ModelBox(bone177, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));
				bone178 = new ModelRenderer(this);
				bone178.setRotationPoint(0.0F, -1.0F, 0.0F);
				head6.addChild(bone178);
				setRotationAngle(bone178, 0.0436F, -0.0873F, 0.0F);
				bone178.cubeList.add(new ModelBox(bone178, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));
				bone179 = new ModelRenderer(this);
				bone179.setRotationPoint(-0.15F, -1.1F, -2.5F);
				head6.addChild(bone179);
				setRotationAngle(bone179, 0.5236F, 0.2618F, 0.0F);
				bone179.cubeList.add(new ModelBox(bone179, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));
				bone180 = new ModelRenderer(this);
				bone180.setRotationPoint(0.15F, -1.1F, -2.5F);
				head6.addChild(bone180);
				setRotationAngle(bone180, 0.5236F, -0.2618F, 0.0F);
				bone180.cubeList.add(new ModelBox(bone180, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));
				bone181 = new ModelRenderer(this);
				bone181.setRotationPoint(2.6F, 0.1F, -3.95F);
				head6.addChild(bone181);
				setRotationAngle(bone181, 0.0F, 0.2618F, 0.0F);
				bone181.cubeList.add(new ModelBox(bone181, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
				bone181.cubeList.add(new ModelBox(bone181, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));
				bone182 = new ModelRenderer(this);
				bone182.setRotationPoint(-2.65F, 0.1F, -3.95F);
				head6.addChild(bone182);
				setRotationAngle(bone182, 0.0F, -0.2618F, 0.0F);
				bone182.cubeList.add(new ModelBox(bone182, 10, 19, 0.05F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
				bone182.cubeList.add(new ModelBox(bone182, 0, 19, 0.05F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));
				bone183 = new ModelRenderer(this);
				bone183.setRotationPoint(1.6F, 1.8F, -5.95F);
				head6.addChild(bone183);
				bone183.cubeList.add(new ModelBox(bone183, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
				bone183.cubeList.add(new ModelBox(bone183, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));
				jaw6 = new ModelRenderer(this);
				jaw6.setRotationPoint(0.0F, 0.5F, 0.0F);
				head6.addChild(jaw6);
				setRotationAngle(jaw6, 0.5236F, 0.0F, 0.0F);
				bone184 = new ModelRenderer(this);
				bone184.setRotationPoint(3.0F, 0.9F, 0.0F);
				jaw6.addChild(bone184);
				setRotationAngle(bone184, 0.0F, 0.2182F, 0.0F);
				bone184.cubeList.add(new ModelBox(bone184, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));
				bone185 = new ModelRenderer(this);
				bone185.setRotationPoint(-3.0F, 0.9F, 0.0F);
				jaw6.addChild(bone185);
				setRotationAngle(bone185, 0.0F, -0.2182F, 0.0F);
				bone185.cubeList.add(new ModelBox(bone185, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));
				bone186 = new ModelRenderer(this);
				bone186.setRotationPoint(0.0F, -0.2F, -5.5F);
				jaw6.addChild(bone186);
				setRotationAngle(bone186, 3.1416F, 3.1416F, 0.0F);
				bone186.cubeList.add(new ModelBox(bone186, 0, 1, 1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, false));
				bone186.cubeList.add(new ModelBox(bone186, 0, 1, -1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, true));
				horns6 = new ModelRenderer(this);
				horns6.setRotationPoint(0.0F, -1.65F, -2.0F);
				head6.addChild(horns6);
				bone187 = new ModelRenderer(this);
				bone187.setRotationPoint(-2.3F, -0.25F, 0.4F);
				horns6.addChild(bone187);
				setRotationAngle(bone187, 0.2618F, -0.5236F, 0.0F);
				bone187.cubeList.add(new ModelBox(bone187, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone187.cubeList.add(new ModelBox(bone187, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
				bone187.cubeList.add(new ModelBox(bone187, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
				bone187.cubeList.add(new ModelBox(bone187, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
				bone187.cubeList.add(new ModelBox(bone187, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));
				bone188 = new ModelRenderer(this);
				bone188.setRotationPoint(-1.2F, -0.25F, 0.8F);
				horns6.addChild(bone188);
				setRotationAngle(bone188, 0.4363F, -0.3491F, 0.0F);
				bone188.cubeList.add(new ModelBox(bone188, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone188.cubeList.add(new ModelBox(bone188, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, false));
				bone188.cubeList.add(new ModelBox(bone188, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
				bone188.cubeList.add(new ModelBox(bone188, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));
				bone189 = new ModelRenderer(this);
				bone189.setRotationPoint(1.2F, -0.25F, 0.8F);
				horns6.addChild(bone189);
				setRotationAngle(bone189, 0.4363F, 0.3491F, 0.0F);
				bone189.cubeList.add(new ModelBox(bone189, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone189.cubeList.add(new ModelBox(bone189, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, true));
				bone189.cubeList.add(new ModelBox(bone189, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
				bone189.cubeList.add(new ModelBox(bone189, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));
				bone190 = new ModelRenderer(this);
				bone190.setRotationPoint(2.3F, -0.25F, 0.4F);
				horns6.addChild(bone190);
				setRotationAngle(bone190, 0.2618F, 0.5236F, 0.0F);
				bone190.cubeList.add(new ModelBox(bone190, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone190.cubeList.add(new ModelBox(bone190, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
				bone190.cubeList.add(new ModelBox(bone190, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
				bone190.cubeList.add(new ModelBox(bone190, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
				bone190.cubeList.add(new ModelBox(bone190, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));
				neck[7][0] = new ModelRenderer(this);
				neck[7][0].setRotationPoint(1.5F, 26.0F, 3.0F);
				tails.addChild(neck[7][0]);
				setRotationAngle(neck[7][0], -1.5708F, 0.0F, 0.0F);
				neck[7][0].cubeList.add(new ModelBox(neck[7][0], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[7][1] = new ModelRenderer(this);
				neck[7][1].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][0].addChild(neck[7][1]);
				neck[7][1].cubeList.add(new ModelBox(neck[7][1], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[7][2] = new ModelRenderer(this);
				neck[7][2].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][1].addChild(neck[7][2]);
				neck[7][2].cubeList.add(new ModelBox(neck[7][2], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[7][3] = new ModelRenderer(this);
				neck[7][3].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][2].addChild(neck[7][3]);
				neck[7][3].cubeList.add(new ModelBox(neck[7][3], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[7][4] = new ModelRenderer(this);
				neck[7][4].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][3].addChild(neck[7][4]);
				neck[7][4].cubeList.add(new ModelBox(neck[7][4], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[7][5] = new ModelRenderer(this);
				neck[7][5].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][4].addChild(neck[7][5]);
				neck[7][5].cubeList.add(new ModelBox(neck[7][5], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[7][6] = new ModelRenderer(this);
				neck[7][6].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][5].addChild(neck[7][6]);
				neck[7][6].cubeList.add(new ModelBox(neck[7][6], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[7][7] = new ModelRenderer(this);
				neck[7][7].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][6].addChild(neck[7][7]);
				neck[7][7].cubeList.add(new ModelBox(neck[7][7], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[7][8] = new ModelRenderer(this);
				neck[7][8].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][7].addChild(neck[7][8]);
				neck[7][8].cubeList.add(new ModelBox(neck[7][8], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				neck[7][9] = new ModelRenderer(this);
				neck[7][9].setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][8].addChild(neck[7][9]);
				neck[7][9].cubeList.add(new ModelBox(neck[7][9], 0, 0, -2.5F, -2.0F, -5.0F, 5, 4, 6, 0.0F, false));
				head7 = new ModelRenderer(this);
				head7.setRotationPoint(0.0F, 0.0F, -5.0F);
				neck[7][9].addChild(head7);
				head7.cubeList.add(new ModelBox(head7, 16, 0, -2.5F, -2.0F, 0.0F, 5, 4, 1, 0.1F, false));
				bone200 = new ModelRenderer(this);
				bone200.setRotationPoint(1.4F, -0.7F, -5.35F);
				head7.addChild(bone200);
				setRotationAngle(bone200, 0.7854F, 0.0F, 0.6109F);
				bone200.cubeList.add(new ModelBox(bone200, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, false));
				bone201 = new ModelRenderer(this);
				bone201.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone200.addChild(bone201);
				setRotationAngle(bone201, -0.9599F, 0.0F, 0.0F);
				bone201.cubeList.add(new ModelBox(bone201, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, false));
				bone202 = new ModelRenderer(this);
				bone202.setRotationPoint(-1.4F, -0.7F, -5.35F);
				head7.addChild(bone202);
				setRotationAngle(bone202, 0.7854F, 0.0F, -0.6109F);
				bone202.cubeList.add(new ModelBox(bone202, 17, 22, -0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F, true));
				bone203 = new ModelRenderer(this);
				bone203.setRotationPoint(0.0F, -0.5F, 3.0F);
				bone202.addChild(bone203);
				setRotationAngle(bone203, -0.9599F, 0.0F, 0.0F);
				bone203.cubeList.add(new ModelBox(bone203, 22, 5, -0.5F, 0.0F, 0.0F, 1, 1, 3, 0.0F, true));
				bone204 = new ModelRenderer(this);
				bone204.setRotationPoint(0.0F, -1.0F, 0.0F);
				head7.addChild(bone204);
				setRotationAngle(bone204, 0.0436F, 0.0873F, 0.0F);
				bone204.cubeList.add(new ModelBox(bone204, 13, 10, -0.0076F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, false));
				bone205 = new ModelRenderer(this);
				bone205.setRotationPoint(0.0F, -1.0F, 0.0F);
				head7.addChild(bone205);
				setRotationAngle(bone205, 0.0436F, -0.0873F, 0.0F);
				bone205.cubeList.add(new ModelBox(bone205, 13, 10, -2.9924F, -1.5F, -3.8257F, 3, 3, 4, 0.0F, true));
				bone206 = new ModelRenderer(this);
				bone206.setRotationPoint(-0.15F, -1.1F, -2.5F);
				head7.addChild(bone206);
				setRotationAngle(bone206, 0.5236F, 0.2618F, 0.0F);
				bone206.cubeList.add(new ModelBox(bone206, 17, 17, -0.05F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, false));
				bone207 = new ModelRenderer(this);
				bone207.setRotationPoint(0.15F, -1.1F, -2.5F);
				head7.addChild(bone207);
				setRotationAngle(bone207, 0.5236F, -0.2618F, 0.0F);
				bone207.cubeList.add(new ModelBox(bone207, 17, 17, -2.95F, -1.5F, -3.0757F, 3, 2, 3, 0.0F, true));
				bone208 = new ModelRenderer(this);
				bone208.setRotationPoint(2.6F, 0.1F, -3.95F);
				head7.addChild(bone208);
				setRotationAngle(bone208, 0.0F, 0.2618F, 0.0F);
				bone208.cubeList.add(new ModelBox(bone208, 10, 19, -2.0F, -1.0F, -2.75F, 2, 1, 3, 0.0F, false));
				bone208.cubeList.add(new ModelBox(bone208, 0, 19, -2.0F, -0.4F, -2.75F, 2, 1, 3, 0.0F, false));
				bone209 = new ModelRenderer(this);
				bone209.setRotationPoint(-2.65F, 0.1F, -3.95F);
				head7.addChild(bone209);
				setRotationAngle(bone209, 0.0F, -0.2618F, 0.0F);
				bone209.cubeList.add(new ModelBox(bone209, 10, 19, 0.05F, -1.0F, -2.75F, 2, 1, 3, 0.0F, true));
				bone209.cubeList.add(new ModelBox(bone209, 0, 19, 0.05F, -0.4F, -2.75F, 2, 1, 3, 0.0F, true));
				bone210 = new ModelRenderer(this);
				bone210.setRotationPoint(1.6F, 1.8F, -5.95F);
				head7.addChild(bone210);
				bone210.cubeList.add(new ModelBox(bone210, 0, 1, -0.2F, -1.0F, 0.0F, 0, 1, 1, 0.1F, false));
				bone210.cubeList.add(new ModelBox(bone210, 0, 1, -3.0F, -1.0F, 0.0F, 0, 1, 1, 0.1F, true));
				jaw7 = new ModelRenderer(this);
				jaw7.setRotationPoint(0.0F, 0.5F, 0.0F);
				head7.addChild(jaw7);
				setRotationAngle(jaw7, 0.5236F, 0.0F, 0.0F);
				bone211 = new ModelRenderer(this);
				bone211.setRotationPoint(3.0F, 0.9F, 0.0F);
				jaw7.addChild(bone211);
				setRotationAngle(bone211, 0.0F, 0.2182F, 0.0F);
				bone211.cubeList.add(new ModelBox(bone211, 0, 10, -3.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, false));
				bone212 = new ModelRenderer(this);
				bone212.setRotationPoint(-3.0F, 0.9F, 0.0F);
				jaw7.addChild(bone212);
				setRotationAngle(bone212, 0.0F, -0.2182F, 0.0F);
				bone212.cubeList.add(new ModelBox(bone212, 0, 10, 0.0F, -1.0F, -6.7F, 3, 2, 7, -0.1F, true));
				bone213 = new ModelRenderer(this);
				bone213.setRotationPoint(0.0F, -0.2F, -5.5F);
				jaw7.addChild(bone213);
				setRotationAngle(bone213, 3.1416F, 3.1416F, 0.0F);
				bone213.cubeList.add(new ModelBox(bone213, 0, 1, 1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, false));
				bone213.cubeList.add(new ModelBox(bone213, 0, 1, -1.2F, -0.5F, -0.5F, 0, 1, 1, 0.1F, true));
				horns7 = new ModelRenderer(this);
				horns7.setRotationPoint(0.0F, -1.65F, -2.0F);
				head7.addChild(horns7);
				bone214 = new ModelRenderer(this);
				bone214.setRotationPoint(-2.3F, -0.25F, 0.4F);
				horns7.addChild(bone214);
				setRotationAngle(bone214, 0.2618F, -0.5236F, 0.0F);
				bone214.cubeList.add(new ModelBox(bone214, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone214.cubeList.add(new ModelBox(bone214, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, false));
				bone214.cubeList.add(new ModelBox(bone214, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, false));
				bone214.cubeList.add(new ModelBox(bone214, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, false));
				bone214.cubeList.add(new ModelBox(bone214, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, false));
				bone215 = new ModelRenderer(this);
				bone215.setRotationPoint(-1.2F, -0.25F, 0.8F);
				horns7.addChild(bone215);
				setRotationAngle(bone215, 0.4363F, -0.3491F, 0.0F);
				bone215.cubeList.add(new ModelBox(bone215, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, false));
				bone215.cubeList.add(new ModelBox(bone215, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, false));
				bone215.cubeList.add(new ModelBox(bone215, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, false));
				bone215.cubeList.add(new ModelBox(bone215, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, false));
				bone216 = new ModelRenderer(this);
				bone216.setRotationPoint(1.2F, -0.25F, 0.8F);
				horns7.addChild(bone216);
				setRotationAngle(bone216, 0.4363F, 0.3491F, 0.0F);
				bone216.cubeList.add(new ModelBox(bone216, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone216.cubeList.add(new ModelBox(bone216, 28, 0, -0.5F, -0.5F, 0.9F, 1, 1, 1, -0.05F, true));
				bone216.cubeList.add(new ModelBox(bone216, 28, 0, -0.5F, -0.5F, 1.6F, 1, 1, 1, -0.2F, true));
				bone216.cubeList.add(new ModelBox(bone216, 28, 0, -0.5F, -0.5F, 2.1F, 1, 1, 1, -0.3F, true));
				bone217 = new ModelRenderer(this);
				bone217.setRotationPoint(2.3F, -0.25F, 0.4F);
				horns7.addChild(bone217);
				setRotationAngle(bone217, 0.2618F, 0.5236F, 0.0F);
				bone217.cubeList.add(new ModelBox(bone217, 28, 0, -0.5F, -0.5F, 0.0F, 1, 1, 1, 0.1F, true));
				bone217.cubeList.add(new ModelBox(bone217, 28, 0, -0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F, true));
				bone217.cubeList.add(new ModelBox(bone217, 28, 0, -0.5F, -0.5F, 1.9F, 1, 1, 1, -0.1F, true));
				bone217.cubeList.add(new ModelBox(bone217, 28, 0, -0.5F, -0.5F, 2.6F, 1, 1, 1, -0.2F, true));
				bone217.cubeList.add(new ModelBox(bone217, 28, 0, -0.5F, -0.5F, 3.1F, 1, 1, 1, -0.3F, true));
				tail0 = new ModelRenderer(this);
				tail0.setRotationPoint(0.0F, 24.0F, 1.0F);
				tails.addChild(tail0);
				setRotationAngle(tail0, 0.2618F, -0.2182F, 0.0F);
				tail0.cubeList.add(new ModelBox(tail0, 0, 26, -2.0671F, -2.0F, 0.9526F, 5, 4, 12, 0.8F, false));
				bone224 = new ModelRenderer(this);
				bone224.setRotationPoint(0.4329F, 0.0F, 9.9526F);
				tail0.addChild(bone224);
				setRotationAngle(bone224, 0.3491F, 0.0F, 0.0F);
				bone224.cubeList.add(new ModelBox(bone224, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.6F, false));
				bone225 = new ModelRenderer(this);
				bone225.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone224.addChild(bone225);
				setRotationAngle(bone225, 0.3491F, 0.0F, 0.0F);
				bone225.cubeList.add(new ModelBox(bone225, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.4F, false));
				bone226 = new ModelRenderer(this);
				bone226.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone225.addChild(bone226);
				setRotationAngle(bone226, 0.3491F, 0.0F, 0.0F);
				bone226.cubeList.add(new ModelBox(bone226, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.2F, false));
				bone223 = new ModelRenderer(this);
				bone223.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone226.addChild(bone223);
				bone223.cubeList.add(new ModelBox(bone223, 0, 26, -2.5F, -1.0F, 0.0F, 5, 2, 12, 0.0F, false));
				tail1 = new ModelRenderer(this);
				tail1.setRotationPoint(0.0F, 24.0F, 1.0F);
				tails.addChild(tail1);
				setRotationAngle(tail1, 0.2618F, -0.6109F, 0.0F);
				tail1.cubeList.add(new ModelBox(tail1, 0, 26, -2.0671F, -2.0F, 0.9526F, 5, 4, 12, 0.8F, false));
				bone2 = new ModelRenderer(this);
				bone2.setRotationPoint(0.4329F, 0.0F, 9.9526F);
				tail1.addChild(bone2);
				setRotationAngle(bone2, 0.3491F, 0.0F, 0.0F);
				bone2.cubeList.add(new ModelBox(bone2, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.6F, false));
				bone3 = new ModelRenderer(this);
				bone3.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone2.addChild(bone3);
				setRotationAngle(bone3, 0.3491F, 0.0F, 0.0F);
				bone3.cubeList.add(new ModelBox(bone3, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.4F, false));
				bone4 = new ModelRenderer(this);
				bone4.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone3.addChild(bone4);
				setRotationAngle(bone4, 0.3491F, 0.0F, 0.0F);
				bone4.cubeList.add(new ModelBox(bone4, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.2F, false));
				bone5 = new ModelRenderer(this);
				bone5.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone4.addChild(bone5);
				bone5.cubeList.add(new ModelBox(bone5, 0, 26, -2.5F, -1.0F, 0.0F, 5, 2, 12, 0.0F, false));
				tail2 = new ModelRenderer(this);
				tail2.setRotationPoint(0.0F, 24.0F, 1.0F);
				tails.addChild(tail2);
				setRotationAngle(tail2, 0.2618F, -1.0036F, 0.0F);
				tail2.cubeList.add(new ModelBox(tail2, 0, 26, -2.0671F, -2.0F, 0.9526F, 5, 4, 12, 0.8F, false));
				bone6 = new ModelRenderer(this);
				bone6.setRotationPoint(0.4329F, 0.0F, 9.9526F);
				tail2.addChild(bone6);
				setRotationAngle(bone6, 0.3491F, 0.0F, 0.0F);
				bone6.cubeList.add(new ModelBox(bone6, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.6F, false));
				bone7 = new ModelRenderer(this);
				bone7.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone6.addChild(bone7);
				setRotationAngle(bone7, 0.3491F, 0.0F, 0.0F);
				bone7.cubeList.add(new ModelBox(bone7, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.4F, false));
				bone8 = new ModelRenderer(this);
				bone8.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone7.addChild(bone8);
				setRotationAngle(bone8, 0.3491F, 0.0F, 0.0F);
				bone8.cubeList.add(new ModelBox(bone8, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.2F, false));
				bone9 = new ModelRenderer(this);
				bone9.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone8.addChild(bone9);
				bone9.cubeList.add(new ModelBox(bone9, 0, 26, -2.5F, -1.0F, 0.0F, 5, 2, 12, 0.0F, false));
				tail3 = new ModelRenderer(this);
				tail3.setRotationPoint(0.0F, 24.0F, 1.0F);
				tails.addChild(tail3);
				setRotationAngle(tail3, 0.2618F, -1.4399F, 0.0F);
				tail3.cubeList.add(new ModelBox(tail3, 0, 26, -2.0671F, -2.0F, 0.9526F, 5, 4, 12, 0.8F, false));
				bone10 = new ModelRenderer(this);
				bone10.setRotationPoint(0.4329F, 0.0F, 9.9526F);
				tail3.addChild(bone10);
				setRotationAngle(bone10, 0.3491F, 0.0F, 0.0F);
				bone10.cubeList.add(new ModelBox(bone10, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.6F, false));
				bone11 = new ModelRenderer(this);
				bone11.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone10.addChild(bone11);
				setRotationAngle(bone11, 0.3491F, 0.0F, 0.0F);
				bone11.cubeList.add(new ModelBox(bone11, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.4F, false));
				bone12 = new ModelRenderer(this);
				bone12.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone11.addChild(bone12);
				setRotationAngle(bone12, 0.3491F, 0.0F, 0.0F);
				bone12.cubeList.add(new ModelBox(bone12, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.2F, false));
				bone13 = new ModelRenderer(this);
				bone13.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone12.addChild(bone13);
				bone13.cubeList.add(new ModelBox(bone13, 0, 26, -2.5F, -1.0F, 0.0F, 5, 2, 12, 0.0F, false));
				tail4 = new ModelRenderer(this);
				tail4.setRotationPoint(0.0F, 24.0F, 1.0F);
				tails.addChild(tail4);
				setRotationAngle(tail4, 0.2618F, 0.2182F, 0.0F);
				tail4.cubeList.add(new ModelBox(tail4, 0, 26, -2.0671F, -2.0F, 0.9526F, 5, 4, 12, 0.8F, false));
				bone14 = new ModelRenderer(this);
				bone14.setRotationPoint(0.4329F, 0.0F, 9.9526F);
				tail4.addChild(bone14);
				setRotationAngle(bone14, 0.3491F, 0.0F, 0.0F);
				bone14.cubeList.add(new ModelBox(bone14, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.6F, false));
				bone15 = new ModelRenderer(this);
				bone15.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone14.addChild(bone15);
				setRotationAngle(bone15, 0.3491F, 0.0F, 0.0F);
				bone15.cubeList.add(new ModelBox(bone15, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.4F, false));
				bone16 = new ModelRenderer(this);
				bone16.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone15.addChild(bone16);
				setRotationAngle(bone16, 0.3491F, 0.0F, 0.0F);
				bone16.cubeList.add(new ModelBox(bone16, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.2F, false));
				bone17 = new ModelRenderer(this);
				bone17.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone16.addChild(bone17);
				bone17.cubeList.add(new ModelBox(bone17, 0, 26, -2.5F, -1.0F, 0.0F, 5, 2, 12, 0.0F, false));
				tail5 = new ModelRenderer(this);
				tail5.setRotationPoint(0.0F, 24.0F, 1.0F);
				tails.addChild(tail5);
				setRotationAngle(tail5, 0.2618F, 0.6109F, 0.0F);
				tail5.cubeList.add(new ModelBox(tail5, 0, 26, -2.0671F, -2.0F, 0.9526F, 5, 4, 12, 0.8F, false));
				bone18 = new ModelRenderer(this);
				bone18.setRotationPoint(0.4329F, 0.0F, 9.9526F);
				tail5.addChild(bone18);
				setRotationAngle(bone18, 0.3491F, 0.0F, 0.0F);
				bone18.cubeList.add(new ModelBox(bone18, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.6F, false));
				bone19 = new ModelRenderer(this);
				bone19.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone18.addChild(bone19);
				setRotationAngle(bone19, 0.3491F, 0.0F, 0.0F);
				bone19.cubeList.add(new ModelBox(bone19, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.4F, false));
				bone38 = new ModelRenderer(this);
				bone38.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone19.addChild(bone38);
				setRotationAngle(bone38, 0.3491F, 0.0F, 0.0F);
				bone38.cubeList.add(new ModelBox(bone38, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.2F, false));
				bone39 = new ModelRenderer(this);
				bone39.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone38.addChild(bone39);
				bone39.cubeList.add(new ModelBox(bone39, 0, 26, -2.5F, -1.0F, 0.0F, 5, 2, 12, 0.0F, false));
				tail6 = new ModelRenderer(this);
				tail6.setRotationPoint(0.0F, 24.0F, 1.0F);
				tails.addChild(tail6);
				setRotationAngle(tail6, 0.2618F, 1.0036F, 0.0F);
				tail6.cubeList.add(new ModelBox(tail6, 0, 26, -2.0671F, -2.0F, 0.9526F, 5, 4, 12, 0.8F, false));
				bone40 = new ModelRenderer(this);
				bone40.setRotationPoint(0.4329F, 0.0F, 9.9526F);
				tail6.addChild(bone40);
				setRotationAngle(bone40, 0.3491F, 0.0F, 0.0F);
				bone40.cubeList.add(new ModelBox(bone40, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.6F, false));
				bone41 = new ModelRenderer(this);
				bone41.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone40.addChild(bone41);
				setRotationAngle(bone41, 0.3491F, 0.0F, 0.0F);
				bone41.cubeList.add(new ModelBox(bone41, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.4F, false));
				bone42 = new ModelRenderer(this);
				bone42.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone41.addChild(bone42);
				setRotationAngle(bone42, 0.3491F, 0.0F, 0.0F);
				bone42.cubeList.add(new ModelBox(bone42, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.2F, false));
				bone43 = new ModelRenderer(this);
				bone43.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone42.addChild(bone43);
				bone43.cubeList.add(new ModelBox(bone43, 0, 26, -2.5F, -1.0F, 0.0F, 5, 2, 12, 0.0F, false));
				tail7 = new ModelRenderer(this);
				tail7.setRotationPoint(0.0F, 24.0F, 1.0F);
				tails.addChild(tail7);
				setRotationAngle(tail7, 0.2618F, 1.4399F, 0.0F);
				tail7.cubeList.add(new ModelBox(tail7, 0, 26, -2.0671F, -2.0F, 0.9526F, 5, 4, 12, 0.8F, false));
				bone44 = new ModelRenderer(this);
				bone44.setRotationPoint(0.4329F, 0.0F, 9.9526F);
				tail7.addChild(bone44);
				setRotationAngle(bone44, 0.3491F, 0.0F, 0.0F);
				bone44.cubeList.add(new ModelBox(bone44, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.6F, false));
				bone45 = new ModelRenderer(this);
				bone45.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone44.addChild(bone45);
				setRotationAngle(bone45, 0.3491F, 0.0F, 0.0F);
				bone45.cubeList.add(new ModelBox(bone45, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.4F, false));
				bone46 = new ModelRenderer(this);
				bone46.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone45.addChild(bone46);
				setRotationAngle(bone46, 0.3491F, 0.0F, 0.0F);
				bone46.cubeList.add(new ModelBox(bone46, 0, 26, -2.5F, -2.0F, 0.0F, 5, 4, 12, 0.2F, false));
				bone83 = new ModelRenderer(this);
				bone83.setRotationPoint(0.0F, 0.0F, 9.0F);
				bone46.addChild(bone83);
				bone83.cubeList.add(new ModelBox(bone83, 0, 26, -2.5F, -1.0F, 0.0F, 5, 2, 12, 0.0F, false));
	
				for (int i = 0; i < 8; i++) {
					for (int j = 1; j < 10; j++) {
						this.neckRotation[i][j].w = (RAND.nextFloat()-0.5F) * 2.0F;
					}
				}
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				tails.render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
	
			@Override
			public void setRotationAngles(float f, float f1, float ageInTicks, float f3, float f4, float f5, Entity entityIn) {
				super.setRotationAngles(f, f1, ageInTicks, f3, f4, f5, entityIn);
				EC entity = (EC)entityIn;
				ageInTicks = entity.getTicksAlive() + ageInTicks - entity.ticksExisted;
				if (ageInTicks > (float)entity.upTime) {
					if (ageInTicks <= (float)entity.upTime + entity.waitTime) {
						float f6 = (ageInTicks - entity.upTime) / (float)entity.waitTime;
						for (int i = 0; i < 8; i++) {
							for (int j = 2; j < 10; j++) {
								neck[i][j].rotateAngleX = this.neckRotation[i][j].x * f6 * f6;
								neck[i][j].rotateAngleY = this.neckRotation[i][j].y * f6 * f6;
								neck[i][j].rotateAngleZ = this.neckRotation[i][j].z * f6 * f6;
							}
						}
					} else  {
						for (int i = 1; i < 8; i++) {
							for (int j = 1; j < 10; j++) {
								neck[i][j].rotateAngleX = this.neckRotation[i][j].x + MathHelper.sin((ageInTicks - j * (i+1)) * 0.05F) * this.neckRotation[i][j].w * 0.0436F;
								neck[i][j].rotateAngleY = this.neckRotation[i][j].y + MathHelper.cos((ageInTicks - j * (i+1)) * 0.05F) * this.neckRotation[i][j].w * 0.0436F;
								neck[i][j].rotateAngleZ = this.neckRotation[i][j].z;
							}
						}
					}
				} else {
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 2; j++) {
							neck[i][j].rotateAngleX = this.neckRotation[i][j].x;
							neck[i][j].rotateAngleY = this.neckRotation[i][j].y;
							neck[i][j].rotateAngleZ = this.neckRotation[i][j].z;
						}
						for (int j = 2; j < 10; j++) {
							neck[i][j].rotateAngleX = 0.0F;
							neck[i][j].rotateAngleY = MathHelper.sin((ageInTicks + j) * 0.4F) * this.neckRotation[i][j].w * 0.2618F;
							neck[i][j].rotateAngleZ = 0.0F;
						}
					}
				}
			}
		}
	}
}
