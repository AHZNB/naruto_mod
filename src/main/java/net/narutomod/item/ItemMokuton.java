package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Rotation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Mirror;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntityWoodBurial;
import net.narutomod.entity.EntityWoodPrison;
import net.narutomod.entity.EntityWoodGolem;
import net.narutomod.entity.EntityWoodArm;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
//import net.narutomod.Chakra;

import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class ItemMokuton extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:mokuton")
	public static final Item block = null;
	public static final ItemJutsu.JutsuEnum WOODBURIAL = new ItemJutsu.JutsuEnum(0, "wood_burial", 'S', 100d, new EntityWoodBurial.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WOODPRISON = new ItemJutsu.JutsuEnum(1, "wood_prison", 'S', 50d, new EntityWoodPrison.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum WOODHOUSE = new ItemJutsu.JutsuEnum(2, "tooltip.mokuton.rightclick2", 'S', 100d, new JutsuHouse());
	public static final ItemJutsu.JutsuEnum GOLEM = new ItemJutsu.JutsuEnum(3, "wood_golem", 'S', 800, 1000d, new EntityWoodGolem.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum ARMATTACK = new ItemJutsu.JutsuEnum(4, "wood_arm", 'S', 400, 50d, new EntityWoodArm.EC.Jutsu());
	
	public ItemMokuton(ElementsNarutomodMod instance) {
		super(instance, 245);
	}

	public void initElements() {
		this.elements.items.add(() -> new ItemCustom(WOODBURIAL, WOODPRISON, WOODHOUSE, GOLEM, ARMATTACK));
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:mokuton", "inventory"));
	}
	
	public static class ItemCustom extends ItemJutsu.Base {
		public ItemCustom(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.MOKUTON, list);
			this.setUnlocalizedName("mokuton");
			this.setRegistryName("mokuton");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[WOODBURIAL.index] = 0;
			this.defaultCooldownMap[WOODPRISON.index] = 0;
			this.defaultCooldownMap[WOODHOUSE.index] = 0;
			this.defaultCooldownMap[GOLEM.index] = 0;
			this.defaultCooldownMap[ARMATTACK.index] = 0;
		}

		private static boolean canSpawnStructureHere(World world, BlockPos pos) {
			if (world.getBlockState(pos).getMaterial() == Material.GROUND || world.getBlockState(pos).getMaterial() == Material.SAND
			 || world.getBlockState(pos).getMaterial() == Material.GRASS || world.getBlockState(pos).getMaterial() == Material.ROCK)
				return true;
			return false;
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			//if (entity instanceof EntityLivingBase && !world.isRemote && entity.ticksExisted % 20 == 6) {
			//	((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SATURATION, 22, 0, false, false));
			//}
			if (entity instanceof EntityPlayer && !world.isRemote && entity.ticksExisted % 20 == 6) {
				((EntityPlayer)entity).getFoodStats().addStats(20, 0.02f);
			}
		}
	}

	/*public static class JutsuBurial implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(entity, 30.0D, false);
			if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.ENTITY && rtr.entityHit instanceof EntityLivingBase) {
				return this.createJutsu(entity, (EntityLivingBase)rtr.entityHit);
			}
			return false;
		}

		public boolean createJutsu(EntityLivingBase entity, EntityLivingBase target) {
			int i = (int)MathHelper.sqrt(Chakra.pathway(entity).getAmount());
			int maxY = 2 + i / 4;
			int maxW = 2 + i / 8;
			maxY = Math.min((int) Math.ceil(target.height) + 1, maxY);
			maxW = Math.min((int) Math.ceil(target.width) + 2, maxW);
			i = (maxW - 2) * 8;
			if (Chakra.pathway(entity).consume((double)i * i)) {
				Map<BlockPos, IBlockState> map = Maps.newHashMap();
				for (int y = -1; y < maxY; y++) {
					for (int x = -maxW / 2; x <= maxW / 2; x++) {
						for (int z = -maxW / 2; z <= maxW / 2; z++) {
							BlockPos pos = new BlockPos(target).add(x, y, z);
							//if (entity.world.getBlockState(pos).getBlock() != Blocks.BEDROCK.getDefaultState().getBlock()) {
							if (entity.world.isAirBlock(pos)) {
								map.put(pos, Blocks.LOG.getStateFromMeta(0));
								//entity.world.playSound(null, target.posX, target.posY, target.posZ, net.minecraft.init.SoundEvents.BLOCK_WOOD_PLACE,
								// SoundCategory.NEUTRAL, 1.0F, 0.6F);
								//entity.world.setBlockState(pos, Blocks.LOG.getStateFromMeta(0), 3);
							}
						}
					}
				}
				if (!map.isEmpty()) {
					new net.narutomod.event.EventSetBlocks(entity.world, map, 0, 1200, true, true);
				}
				return true;
			}
			return false;
		}
	}*/

	public static class JutsuHouse implements ItemJutsu.IJutsuCallback {
		@Override
		public boolean createJutsu(ItemStack itemstack, EntityLivingBase entity, float power) {
			World world = entity.world;
			if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, entity)) {
				return false;
			}
			RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(entity, 30.0D, false);
			if (rtr.typeOfHit == RayTraceResult.Type.BLOCK && rtr.sideHit == EnumFacing.UP 
			 && ItemCustom.canSpawnStructureHere(world, rtr.getBlockPos()) && !world.isRemote) {
				Template template = ((WorldServer)world).getStructureTemplateManager()
				 .getTemplate(world.getMinecraftServer(), new ResourceLocation("narutomod", "wood_house_2"));
				if (template != null) {
					world.playSound(null, rtr.getBlockPos(), SoundEvent.REGISTRY.getObject(new ResourceLocation(("narutomod:woodspawn"))),
					 SoundCategory.BLOCKS, 2f, world.rand.nextFloat() * 0.4f + 0.8f);
					BlockPos spawnTo, spos;
					Rotation rotation;
					float yaw = MathHelper.wrapDegrees(entity.rotationYaw);
					if (yaw >= 135f || yaw < -135f) {
						spawnTo = rtr.getBlockPos().add(-8, 1, -16);
						spos = spawnTo;
						rotation = Rotation.NONE;
					} else if (yaw >= -45f && yaw < 45f) {
						spawnTo = rtr.getBlockPos().add(8, 1, 16);
						spos = spawnTo.add(-template.getSize().getX(), 0, -template.getSize().getZ());
						rotation = Rotation.CLOCKWISE_180;
					} else if (yaw >= 45f && yaw < 135f) {
						spawnTo = rtr.getBlockPos().add(-16, 1, 8);
						spos = spawnTo.add(0, 0, -template.getSize().getX());
						rotation = Rotation.COUNTERCLOCKWISE_90;
					} else {
						spawnTo = rtr.getBlockPos().add(16, 1, -8);
						spos = spawnTo.add(-template.getSize().getZ(), 0, 0);
						rotation = Rotation.CLOCKWISE_90;
					}
					IBlockState iblockstate = world.getBlockState(spawnTo);
					world.notifyBlockUpdate(spawnTo, iblockstate, iblockstate, 3);
					PlacementSettings placementsetting = new PlacementSettings().setRotation(rotation)
					 .setMirror(Mirror.NONE).setChunk(null).setReplacedBlock(null).setIgnoreStructureBlock(false)
					 .setIgnoreEntities(false);
					for (BlockPos pos : BlockPos.getAllInBoxMutable(spos, spos.add(template.transformedSize(rotation)))) {
						((WorldServer)world).spawnParticle(EnumParticleTypes.BLOCK_DUST, pos.getX()+0.5d, pos.getY(), pos.getZ()+0.5d,
						 6, 0D, 0D, 0D, 0.2D, Block.getIdFromBlock(Blocks.OAK_FENCE));
					}
					template.addBlocksToWorldChunk(world, spawnTo, placementsetting);
				}
				return true;
			}
			return false;
		}
	}

	public static abstract class WoodSegment extends Entity {
		private static final DataParameter<Integer> PARENT_ID = EntityDataManager.<Integer>createKey(WoodSegment.class, DataSerializers.VARINT);
		private static final DataParameter<Float> OFFSET_X = EntityDataManager.<Float>createKey(WoodSegment.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> OFFSET_Y = EntityDataManager.<Float>createKey(WoodSegment.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> OFFSET_Z = EntityDataManager.<Float>createKey(WoodSegment.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> OFFSET_YAW = EntityDataManager.<Float>createKey(WoodSegment.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> OFFSET_PITCH = EntityDataManager.<Float>createKey(WoodSegment.class, DataSerializers.FLOAT);
		private static final DataParameter<Integer> SEG_IDX = EntityDataManager.<Integer>createKey(WoodSegment.class, DataSerializers.VARINT);
		private double lastX;
		private double lastY;
		private double lastZ;

		public WoodSegment(World worldIn) {
			super(worldIn);
			this.setSize(0.5f, 0.5f);
		}
		
		public WoodSegment(WoodSegment segment, float yawOffset, float pitchOffset) {
			this(segment, 0.0d, 0.75d * segment.height, 0.0d, yawOffset, pitchOffset);
		}

		public WoodSegment(WoodSegment segment, double offsetX, double offsetY, double offsetZ, float yawOffset, float pitchOffset) {
			this(segment.world);
			ProcedureUtils.Vec2f vec2f = segment.getOffsetRotation();
			Vec3d vec = new Vec3d(offsetX, offsetY, offsetZ).rotatePitch(-vec2f.y * 0.017453292F)
			 .rotateYaw(-vec2f.x * 0.017453292F).add(segment.getOffsetPosition());
			this.setOffset(vec.x, vec.y, vec.z, vec2f.x + yawOffset, vec2f.y + pitchOffset);
			this.setParent(segment.getParent());
			this.setPositionAndRotationFromParent(1f);
			this.setIndex(segment.getIndex() + 1);
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(PARENT_ID, Integer.valueOf(-1));
			this.getDataManager().register(OFFSET_X, Float.valueOf(0f));
			this.getDataManager().register(OFFSET_Y, Float.valueOf(0f));
			this.getDataManager().register(OFFSET_Z, Float.valueOf(0f));
			this.getDataManager().register(OFFSET_YAW, Float.valueOf(0f));
			this.getDataManager().register(OFFSET_PITCH, Float.valueOf(0f));
			this.getDataManager().register(SEG_IDX, Integer.valueOf(0));
		}

		protected void setParent(Entity entity) {
			this.getDataManager().set(PARENT_ID, Integer.valueOf(entity.getEntityId()));
		}

		@Nullable
		protected Entity getParent() {
			return this.world.getEntityByID(((Integer)this.getDataManager().get(PARENT_ID)).intValue());
		}

		protected void setOffset(double x, double y, double z, float yaw, float pitch) {
			this.getDataManager().set(OFFSET_X, Float.valueOf((float)x));
			this.getDataManager().set(OFFSET_Y, Float.valueOf((float)y));
			this.getDataManager().set(OFFSET_Z, Float.valueOf((float)z));
			this.getDataManager().set(OFFSET_YAW, Float.valueOf(yaw));
			this.getDataManager().set(OFFSET_PITCH, Float.valueOf(pitch));
		}

		protected Vec3d getOffsetPosition() {
			return new Vec3d(((Float)this.dataManager.get(OFFSET_X)).floatValue(),
			 ((Float)this.dataManager.get(OFFSET_Y)).floatValue(),
			 ((Float)this.dataManager.get(OFFSET_Z)).floatValue());
		}

		protected ProcedureUtils.Vec2f getOffsetRotation() {
			return new ProcedureUtils.Vec2f(((Float)this.dataManager.get(OFFSET_YAW)).floatValue(),
			 ((Float)this.dataManager.get(OFFSET_PITCH)).floatValue());
		}

		protected void setIndex(int i) {
			this.getDataManager().set(SEG_IDX, Integer.valueOf(i));
		}

		@Nullable
		protected int getIndex() {
			return ((Integer)this.getDataManager().get(SEG_IDX)).intValue();
		}

		protected void setPositionAndRotationFromParent(float partialTicks) {
			Entity parent = this.getParent();
			if (parent != null) {
				float yaw = parent.prevRotationYaw + MathHelper.wrapDegrees(parent.rotationYaw - parent.prevRotationYaw) * partialTicks;
				double x = parent.lastTickPosX + (parent.posX - parent.lastTickPosX) * (double)partialTicks;
				double y = parent.lastTickPosY + (parent.posY - parent.lastTickPosY) * (double)partialTicks;
				double z = parent.lastTickPosZ + (parent.posZ - parent.lastTickPosZ) * (double)partialTicks;
				Vec3d vec = this.getOffsetPosition().rotateYaw(-yaw * 0.017453292F).addVector(x, y, z);
				ProcedureUtils.Vec2f vec2f = this.getOffsetRotation();
				this.rotationYaw = yaw + vec2f.x;
				this.rotationPitch = vec2f.y;
				this.setPosition(vec.x, vec.y, vec.z);
				if (this.world.isRemote && (this.prevPosX != this.posX || this.prevPosY != this.posY || this.prevPosZ != this.posZ
				 || this.prevRotationYaw != this.rotationYaw || this.prevRotationPitch != this.rotationPitch)) {
					ProcedureSync.EntityPositionAndRotation.sendToServer(this);
					this.prevPosX = this.posX;
					this.prevPosY = this.posY;
					this.prevPosZ = this.posZ;
					this.prevRotationYaw = this.rotationYaw;
					this.prevRotationPitch = this.rotationPitch;
				}
			}
		}

		@Override
		public void onUpdate() {
			BlockPos blockpos = new BlockPos(this);
			IBlockState blockstate = this.world.getBlockState(blockpos);
			if (blockstate.isFullBlock() && (this.ticksExisted == 1 || this.posX != this.lastX || this.posZ != this.lastZ)) {
				for (int i = 0; i < 6; i++) {
					this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.rand.nextDouble() + blockpos.getX(),
					 this.rand.nextDouble() + blockpos.getY(), this.rand.nextDouble() + blockpos.getZ(),
					 0.15d, 0.15d, 0.15d, Block.getIdFromBlock(blockstate.getBlock()));
				}
			}
			this.lastX = this.posX;
			this.lastY = this.posY;
			this.lastZ = this.posZ;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
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
			RenderingRegistry.registerEntityRenderingHandler(WoodSegment.class, renderManager -> new RenderSegment(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderSegment extends Render<WoodSegment> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/woodblock.png");
			protected final ModelWoodSegment model;
	
			public RenderSegment(RenderManager renderManagerIn) {
				super(renderManagerIn);
				this.model = new ModelWoodSegment();
				this.shadowSize = 0.4f;
			}
	
			@Override
			public void doRender(WoodSegment entity, double x, double y, double z, float entityYaw, float pt) {
				if (entity.getParent() != null) {
					entity.setPositionAndRotationFromParent(pt);
					x = entity.posX - this.renderManager.viewerPosX;
					y = entity.posY - this.renderManager.viewerPosY;
					z = entity.posZ - this.renderManager.viewerPosZ;
					this.bindEntityTexture(entity);
					GlStateManager.pushMatrix();
					GlStateManager.translate(x, y, z);
					GlStateManager.rotate(-entity.rotationYaw, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(entity.rotationPitch - 180.0F, 1.0F, 0.0F, 0.0F);
					GlStateManager.rotate(5.0F * entity.getIndex(), 0.0F, 1.0F, 0.0F);
					GlStateManager.scale(2.0f, 2.0f, 2.0f);
					this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
					GlStateManager.popMatrix();
				}
			}
	
			@Override
			protected ResourceLocation getEntityTexture(WoodSegment entity) {
				return this.texture;
			}
		}
	
		// Made with Blockbench 3.9.3
		// Exported for Minecraft version 1.7 - 1.12
		// Paste this class into your mod and generate all required imports
		@SideOnly(Side.CLIENT)
		public class ModelWoodSegment extends ModelBase {
			private final ModelRenderer[] segment = new ModelRenderer[1];
			
			public ModelWoodSegment() {
				textureWidth = 16;
				textureHeight = 16;
				for (int i = 0; i < segment.length; i++) {
					segment[i] = new ModelRenderer(this);
					segment[i].setRotationPoint(0.0F, i == 0 ? 0.0F : -3.0F, 0.0F);
					segment[i].cubeList.add(new ModelBox(segment[i], 0, 0, -2.0F, -3.0F, -2.0F, 4, 4, 4, 0.0F, false));
					ModelRenderer bone5 = new ModelRenderer(this);
					bone5.setRotationPoint(0.0F, 1.0F, 0.0F);
					segment[i].addChild(bone5);
					ModelRenderer bone = new ModelRenderer(this);
					bone.setRotationPoint(0.0F, -4.0F, -2.0F);
					bone5.addChild(bone);
					setRotationAngle(bone, 0.5236F, 3.1416F, 0.0F);
					bone.cubeList.add(new ModelBox(bone, 0, 0, -2.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F, false));
					ModelRenderer bone2 = new ModelRenderer(this);
					bone2.setRotationPoint(0.0F, -4.0F, 2.0F);
					bone5.addChild(bone2);
					setRotationAngle(bone2, 0.5236F, 0.0F, 0.0F);
					bone2.cubeList.add(new ModelBox(bone2, 0, 0, -2.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F, false));
					ModelRenderer bone3 = new ModelRenderer(this);
					bone3.setRotationPoint(-2.0F, -4.0F, 0.0F);
					bone5.addChild(bone3);
					setRotationAngle(bone3, 0.0F, -1.5708F, 0.5236F);
					bone3.cubeList.add(new ModelBox(bone3, 0, 0, -2.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F, false));
					ModelRenderer bone4 = new ModelRenderer(this);
					bone4.setRotationPoint(2.0F, -4.0F, 0.0F);
					bone5.addChild(bone4);
					setRotationAngle(bone4, 0.0F, 1.5708F, -0.5236F);
					bone4.cubeList.add(new ModelBox(bone4, 0, 0, -2.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F, true));
					if (i > 0) {
						segment[i-1].addChild(segment[i]);
					}
				}
			}
	
			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
				segment[0].render(f5);
			}
	
			public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}
	}
}
