
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

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.nbt.NBTTagCompound;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.entity.EntityEarthBlocks;
import net.narutomod.entity.EntityParticle;
import net.narutomod.entity.EntityShieldBase;
import net.narutomod.entity.EntitySandBullet;
import net.narutomod.entity.EntitySandBind;
import net.narutomod.entity.EntitySandLevitation;
import net.narutomod.entity.EntitySandGathering;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.PlayerTracker;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class ItemJiton extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder("narutomod:jiton")
	public static final Item block = null;
	public static final int ENTITYID = 201;
	public static final int ENTITYID_RANGED = 200;
	public static final ItemJutsu.JutsuEnum SANDSHIELD = new ItemJutsu.JutsuEnum(0, "entityjitonshield", 'S', 150, 20d, new EntitySandShield.Jutsu());
	public static final ItemJutsu.JutsuEnum SANDBULLET = new ItemJutsu.JutsuEnum(1, "sand_bullet", 'S', 100, 20d, new EntitySandBullet.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SANDBIND = new ItemJutsu.JutsuEnum(2, "sand_bind", 'S', 200, 100d, new EntitySandBind.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum SANDFLY = new ItemJutsu.JutsuEnum(3, "sand_levitation", 'S', 200, 0.25d, new EntitySandLevitation.EC.Jutsu());
	public static final ItemJutsu.JutsuEnum GATHERING = new ItemJutsu.JutsuEnum(4, "sand_gathering", 'S', 200, 100d, new EntitySandGathering.EC.Jutsu());

	public ItemJiton(ElementsNarutomodMod instance) {
		super(instance, 518);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new RangedItem(SANDSHIELD, SANDBULLET, SANDBIND, SANDFLY, GATHERING));
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntitySandShield.class)
		 .id(new ResourceLocation("narutomod", "entityjitonshield"), ENTITYID).name("entityjitonshield").tracker(64, 1, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(SandParticle.class)
		 .id(new ResourceLocation("narutomod", "jitonparticle"), ENTITYID_RANGED).name("jitonparticle").tracker(64, 1, true).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:jiton", "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntitySandShield.class, renderManager -> new RenderCustom(renderManager));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
	}

	@SideOnly(Side.CLIENT)
	public class RenderCustom extends Render<EntitySandShield> {
		public RenderCustom(RenderManager renderManagerIn) {
			super(renderManagerIn);
		}
		@Override
		public void doRender(EntitySandShield bullet, double d, double d1, double d2, float f, float f1) {
		}
		@Override
		protected ResourceLocation getEntityTexture(EntitySandShield entity) {
			return null;
		}
	}

	public static class RangedItem extends ItemJutsu.Base {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(ItemJutsu.JutsuEnum.Type.JITON, list);
			this.setUnlocalizedName("jiton");
			this.setRegistryName("jiton");
			this.setCreativeTab(TabModTab.tab);
			this.defaultCooldownMap[SANDSHIELD.index] = 0;
			this.defaultCooldownMap[SANDBULLET.index] = 0;
			this.defaultCooldownMap[SANDBIND.index] = 0;
			this.defaultCooldownMap[SANDFLY.index] = 0;
		}

		@Override
		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
			if (this.getCurrentJutsu(stack) == SANDBULLET) {
				return this.getPower(stack, entity, timeLeft, 0.0f, 50f);
			}
			return 1f;
		}

		@Override
		protected float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			ItemJutsu.JutsuEnum jutsu = this.getCurrentJutsu(stack);
			float f = super.getMaxPower(stack, entity);
			if (jutsu == SANDBULLET) {
				return Math.min(f, 5.0f);
			}
			return f;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			if ((entity.isCreative() || (ProcedureUtils.hasItemInInventory(entity, ItemFuton.block) 
			 && ProcedureUtils.hasItemInInventory(entity, ItemDoton.block))) 
			 && (entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ItemGourd.body
			  || (EntityBijuManager.getTails(entity) == 1 && EntityBijuManager.cloakLevel(entity) > 0))) {
				return super.onItemRightClick(world, entity, hand);
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, entity.getHeldItem(hand));
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			super.onUsingTick(stack, player, timeLeft);
			if (!player.world.isRemote && this.getCurrentJutsu(stack) == SANDBULLET
			 && this.getPower(stack, player, timeLeft) < this.getMaxPower(stack, player)) {
				EntitySandBullet.addPos(stack, player, this.getPower(stack, player, timeLeft));
			}
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (!world.isRemote && entity instanceof EntityPlayer && entity.ticksExisted > 100) {
				EntityPlayer player = (EntityPlayer)entity;
				if (!ProcedureUtils.hasItemInInventory(player, ItemGourd.body)) {
					if (!player.getCooldownTracker().hasCooldown(block)) {
						player.getCooldownTracker().setCooldown(block, (int)this.getModifiedCD(2400, player));
					} else if (player.getCooldownTracker().getCooldown(block, 1f) <= 0.1f) {
						ItemStack stack = new ItemStack(ItemGourd.body);
						ItemGourd.setMaterial(stack, getSandType(itemstack));
						ItemHandlerHelper.giveItemToPlayer(player, stack);
					}
				}
				if ((player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == ItemGourd.body
				  || (EntityBijuManager.getTails(player) == 1 && EntityBijuManager.cloakLevel(player) > 0))
				 && this.getCurrentJutsu(itemstack) == SANDBULLET) {
					EntitySandBullet.updateSwarms(itemstack);
				}
				this.enableJutsu(itemstack, GATHERING, getSandType(itemstack) == Type.IRON);
			}
		}

		private double getModifiedCD(double cd, EntityPlayer player) {
			return cd * Chakra.getChakraModifier(player);
		}

		@Override
		public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
			if (attacker.equals(target)) {
				return !EntitySandBind.sandFuneral(attacker);
			}
			return super.onLeftClickEntity(itemstack, attacker, target);
		}

		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(TextFormatting.GREEN + net.minecraft.util.text.translation.I18n.translateToLocal("tooltip.jiton.musthave") + TextFormatting.RESET);
		}
	}

	public enum Type {
		IRON(0, 0xff303030),
		SAND(1, 0xffd8d09d),
		GOLD(2, 0xfffffa58);

		private int id;
		private int color;
		private static final Map<Integer, Type> TYPES = Maps.newHashMap();

		static {
			for (Type type : values())
				TYPES.put(Integer.valueOf(type.getID()), type);
		}

		private Type(int i, int col) {
			this.id = i;
			this.color = col;
		}

		public int getID() {
			return this.id;
		}

		public int getColor() {
			return this.color;
		}

		public static Type getTypeFromId(int i) {
			return i >= 0 && i <= 2 ? TYPES.get(Integer.valueOf(i)) : IRON;
		}
	}

	public static void setSandType(ItemStack stack, Type type) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.getTagCompound().setInteger("MaterialType", type.getID());
	}

	public static Type getSandType(ItemStack stack) {
		return stack.hasTagCompound() ? Type.getTypeFromId(stack.getTagCompound().getInteger("MaterialType")) : Type.IRON;
	}

	public static class EntitySandShield extends EntityShieldBase {
		private final double chakraUsage = 0.5d; // per 1 ticks
		private List<SwarmTarget> sandTargets = Lists.newArrayList();
		private int color;

		public EntitySandShield(World a) {
			super(a);
			this.setSize(3.0F, 3.0F);
		}

		public EntitySandShield(EntityLivingBase user, Type sandType) {
			super(user);
			this.setSize(3.0F, 3.0F);
			double d = user instanceof EntityPlayer ? (2.5d * PlayerTracker.getNinjaLevel((EntityPlayer)user)) : 100d;
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(d);
			this.setHealth(this.getMaxHealth());
			this.color = sandType.getColor();
		}

		@Override
		public void setDead() {
			if (!this.sandTargets.isEmpty()) {
				this.setHealth(0f);
			} else {
				super.setDead();
			}
		}

		@Override
		protected void onDeathUpdate() {
			if (!this.sandTargets.isEmpty()) {
				Iterator<SwarmTarget> iter = this.sandTargets.iterator();
				while (iter.hasNext()) {
					SwarmTarget st = iter.next();
					if (!st.shouldRemove()) {
						st.setTarget(this.getGourdMouthPos(), 0.6f, 0.02f, true);
						st.onUpdate();
					} else {
						iter.remove();
					}
				}
			} else {
				this.setDead();
			}
		}

		@Override
		public void onDeath(DamageSource cause) {
			if (!this.dead) {
				this.setSize(0.1f, 0.1f);
				this.removePassengers();
				EntityLivingBase summoner = this.getSummoner();
				if (!this.world.isRemote && summoner instanceof EntityPlayer) {
					ItemStack stack = ProcedureUtils.getMatchingItemStack((EntityPlayer)summoner, block);
					if (stack != null) {
						((RangedItem)stack.getItem()).setJutsuCooldown(stack, SANDSHIELD,
						 (long)(2400f * ((RangedItem)stack.getItem()).getModifier(stack, summoner)));
					}
				}
			}
			super.onDeath(cause);
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if (!this.world.isRemote && this.getHealth() > 0.0f && source.getImmediateSource() != null) {
				Entity entity = source.getImmediateSource();
				//this.moveSand(this.getGourdMouthPos(), entity.getPositionVector().addVector(0, entity.height/2, 0), 100);
				this.moveSand(this.getTargetPosition(entity), this.getTargetPosition(entity), 2);
				if (entity instanceof EntityLivingBase) {
					ProcedureUtils.pushEntity(this.getSummoner(), entity, 5d, 1.5f);
				}
			}
			return super.attackEntityFrom(source, amount);
		}

		private void moveSand(Vec3d from, Vec3d to, int count) {
			if (!this.world.isRemote) {
				this.sandTargets.add(new SwarmTarget(this.world, count, from, to, 0.95f, this.color));
			}
		}

		private Vec3d getGourdMouthPos() {
			EntityLivingBase summoner = this.getSummoner();
			if (summoner != null) {
				return ItemGourd.getMouthPos(summoner);
			}
			return this.getPositionVector();
		}

		private Vec3d getTargetPosition(Entity target) {
			return target.getPositionVector().addVector(0, target.height/2, 0);
		}

		private void updateTargets() {
			Iterator<SwarmTarget> iter = this.sandTargets.iterator();
			while (iter.hasNext()) {
				SwarmTarget st = iter.next();
				if (st.shouldRemove()) {
					iter.remove();
				} else {
					if (st.getTicks() == 20) {
						st.setTarget(this.getGourdMouthPos(), 0.6f, 0.02f, true);
					}
					st.onUpdate();
				}
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			if (!this.world.isRemote) {
				this.updateTargets();
				EntityLivingBase summoner = this.getSummoner();
				if (summoner == null || (this.getHealth() > 0f
				 && !Chakra.pathway(summoner).consume(this.chakraUsage))) {
					this.setDead();
				}
			}
		}

		@Override
		protected void collideWithEntity(Entity entity) {
			if (!this.world.isRemote && !this.isRidingSameEntity(entity) && ProcedureUtils.getVelocity(entity) > 0.22d) {
				EntityEarthBlocks.BlocksMoveHelper.collideWithEntity(this, entity);
				//this.moveSandTo(entity.posX, entity.posY + entity.height/2, entity.posZ, 100);
				this.moveSand(this.getTargetPosition(entity), this.getTargetPosition(entity), 100);
			}
			super.collideWithEntity(entity);
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ID_KEY = "JitonSandShieldEntityIdKey";
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				Entity entity1 = entity.world.getEntityByID(entity.getEntityData().getInteger(ID_KEY));
				if (!(entity1 instanceof EntitySandShield)) {
					entity1 = new EntitySandShield(entity, getSandType(stack));
					entity.world.spawnEntity(entity1);
					entity.getEntityData().setInteger(ID_KEY, entity1.getEntityId());
					return true;
				}
				return false;
			}
		}
	}

	public static class SandParticle extends EntityParticle.Base {
		private int idleTime;
		private int deathTicks;

		public SandParticle(World w) {
			super(w);
		}

		public SandParticle(World worldIn, double x, double y, double z, double mX, double mY, double mZ, int color, float scale, int maxAgeIn) {
			super(worldIn, x, y, z, mX, mY, mZ, color, scale, maxAgeIn);
		}
				
		@Override
		public void onDeath() {
			if (++this.deathTicks >= 20 && !this.world.isRemote) {
				this.setDead();
			}
			this.motionY -= 0.05d;
		}
		
		@Override
		public void onUpdate() {
			super.onUpdate();
			double d = this.getVelocity();
			this.idleTime = d < 0.001d ? this.idleTime + 1 : 0;
			if (this.world.isRemote) {
				this.setParticleTextureOffset(this.texU + (d > 0.01d ? 1 : 0) % 8);
				for (int i = 0; i < 10; i++) {
					Particles.spawnParticle(this.world, Particles.Types.SAND,
					 this.posX + (this.rand.nextDouble()-0.5d) * this.width,
					 this.posY + this.rand.nextDouble() * this.height,
					 this.posZ + (this.rand.nextDouble()-0.5d) * this.width, 1, 0d, 0d, 0d,
					 this.motionX * (this.rand.nextDouble() * 0.2d + 0.9d),
					 this.motionY * (this.rand.nextDouble() * 0.2d + 0.9d),
					 this.motionZ * (this.rand.nextDouble() * 0.2d + 0.9d),
					 this.getColorInt(), (int)(this.getScale(0f) * 8), 5);
				}
			} else if (this.idleTime > 1000) {
				this.setAge(this.getMaxAge());
			}
		}

		@Override
		public float getScale(float partialTicks) {
			return MathHelper.clamp(((float)this.getAge() + partialTicks) / 10.0F, 0.2F, 1.0F) * this.getScale();
		}
	}

	public static class SwarmTarget {
		private World world;
		private int total;
		private Vec3d startPos;
		private AxisAlignedBB startBB;
		private AxisAlignedBB targetBB;
		private float speed;
		private float inaccuracy;
		private List<Entity> particles;
		private int spawned;
		private int ticks;
		private Random rand;
		private boolean dieOnTargetReached;
		private Vec3d spawnMotion;
		private AxisAlignedBB border;
		private float scale;
		private int color;

		public SwarmTarget(World worldIn, int totalIn, Vec3d startPosIn, Vec3d targetPosIn) {
			this(worldIn, totalIn, startPosIn, targetPosIn, Vec3d.ZERO, 0f, 0f, false, 1f);
		}

		public SwarmTarget(World worldIn, int totalIn, Vec3d startPosIn, Vec3d targetPosIn, float speedIn, int colorIn) {
			this(worldIn, totalIn, startPosIn, targetPosIn, new Vec3d(0.05d, 0.2d, 0.05d), speedIn, 0.02f, false, 1f, colorIn);
		}

		public SwarmTarget(World worldIn, int totalIn, Vec3d startPosIn, Vec3d targetPosIn, Vec3d initialMotion, float speedIn, float inaccuracyIn, boolean dieOnReached, float scaleIn) {
			this(worldIn, totalIn, startPosIn, targetPosIn, initialMotion, speedIn, inaccuracyIn, dieOnReached, scaleIn, Type.IRON.getColor());
		}

		public SwarmTarget(World worldIn, int totalIn, Vec3d startPosIn, Vec3d targetPos, Vec3d initialMotion, float speedIn, float inaccuracyIn, boolean dieOnReached, float scaleIn, int colorIn) {
			this.world = worldIn;
			this.total = totalIn;
			this.startPos = startPosIn;
			this.setTarget(targetPos, speedIn, inaccuracyIn, dieOnReached);
			this.particles = Lists.newArrayList();
			this.rand = new Random();
			this.spawnMotion = initialMotion;
			this.scale = scaleIn;
			this.color = colorIn;
			this.spawnNewParticles();
			this.border = this.particles.get(0).getEntityBoundingBox();
		}

		public SwarmTarget(World worldIn, int totalIn, Vec3d startPosIn, AxisAlignedBB targetBBIn, Vec3d initialMotion, float speedIn, float inaccuracyIn, boolean dieOnReached, float scaleIn, int colorIn) {
			this.world = worldIn;
			this.total = totalIn;
			this.startPos = startPosIn;
			this.setTarget(targetBBIn, speedIn, inaccuracyIn, dieOnReached);
			this.particles = Lists.newArrayList();
			this.rand = new Random();
			this.spawnMotion = initialMotion;
			this.scale = scaleIn;
			this.color = colorIn;
			this.spawnNewParticles();
			this.border = this.particles.get(0).getEntityBoundingBox();
		}

		public SwarmTarget(World worldIn, int totalIn, AxisAlignedBB startBBIn, Vec3d targetPos, Vec3d initialMotion, float speedIn, float inaccuracyIn, boolean dieOnReached, float scaleIn, int colorIn) {
			this.world = worldIn;
			this.total = totalIn;
			this.startBB = startBBIn;
			this.setTarget(targetPos, speedIn, inaccuracyIn, dieOnReached);
			this.particles = Lists.newArrayList();
			this.rand = new Random();
			this.spawnMotion = initialMotion;
			this.scale = scaleIn;
			this.color = colorIn;
			this.spawnNewParticles();
			this.border = this.particles.get(0).getEntityBoundingBox();
		}

		public SwarmTarget(World worldIn, int totalIn, AxisAlignedBB startBBIn, AxisAlignedBB targetBBIn, Vec3d initialMotion, float speedIn, float inaccuracyIn, boolean dieOnReached, float scaleIn, int colorIn) {
			this.world = worldIn;
			this.total = totalIn;
			this.startBB = startBBIn;
			this.setTarget(targetBBIn, speedIn, inaccuracyIn, dieOnReached);
			this.particles = Lists.newArrayList();
			this.rand = new Random();
			this.spawnMotion = initialMotion;
			this.scale = scaleIn;
			this.color = colorIn;
			this.spawnNewParticles();
			this.border = this.particles.get(0).getEntityBoundingBox();
		}

		protected Entity createParticle(double x, double y, double z, double mx, double my, double mz, int c, float sc, int life) {
			return new SandParticle(this.world, x, y, z, mx, my, mz, c, sc, life);
		}

		private void spawnNewParticles() {
			for (int i = 0; this.spawned < this.total && i < 5; i++, this.spawned++) {
				Vec3d vec = this.startPos != null ? this.startPos : this.randomPosInBB(this.startBB);
				Entity p = this.createParticle(vec.x, vec.y, vec.z,
				 (this.rand.nextDouble()-0.5d) * 2d * this.spawnMotion.x, this.spawnMotion.y,
				 (this.rand.nextDouble()-0.5d) * 2d * this.spawnMotion.z, this.color,
				 this.scale + (this.rand.nextFloat()-0.5f) * this.scale * 0.2f, 3600);
				this.world.spawnEntity(p);
				this.particles.add(p);
			}
		}

		public AxisAlignedBB getBorders() {
			return this.border;
		}

		private void updateBorderWith(Entity particle) {
			AxisAlignedBB bb = particle.getEntityBoundingBox();
			double minX = bb.minX < this.border.minX ? bb.minX : this.border.minX;
			double minY = bb.minY < this.border.minY ? bb.minY : this.border.minY;
			double minZ = bb.minZ < this.border.minZ ? bb.minZ : this.border.minZ;
			double maxX = bb.maxX > this.border.maxX ? bb.maxX : this.border.maxX;
			double maxY = bb.maxY > this.border.maxY ? bb.maxY : this.border.maxY;
			double maxZ = bb.maxZ > this.border.maxZ ? bb.maxZ : this.border.maxZ;
			this.border = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		}

		public boolean allParticlesReachedTarget() {
			//return this.border.getAverageEdgeLength() < this.speed + this.inaccuracy * 10;
			return this.targetBB.contains(ProcedureUtils.BB.getCenter(this.border));
		}

		public boolean allParticlesSpawned() {
			return this.spawned >= this.total;
		}

		protected void playFlyingSound(double x, double y, double z, float volume, float pitch) {
			this.world.playSound(null, x, y, z, SoundEvents.BLOCK_SAND_PLACE,
			 net.minecraft.util.SoundCategory.BLOCKS, volume, pitch);
		}

		public void onUpdate() {
			if (!this.particles.isEmpty()) {
				Entity ep = this.particles.get(0);
				this.playFlyingSound(ep.posX, ep.posY, ep.posZ, this.particles.size() * this.speed * 0.0025f, this.rand.nextFloat() * 0.4f + 0.8f);
				this.border = ep.getEntityBoundingBox();
				Iterator<Entity> iter = this.particles.iterator();
				while (iter.hasNext()) {
					ep = iter.next();
					if (ep.isEntityAlive()) {
						Vec3d vec = this.getTargetPos().subtract(ep.getPositionVector());
						if (this.dieOnTargetReached && vec.lengthVector() < 0.1d + this.inaccuracy) {
							ep.setDead();
						} else {
							vec = vec.normalize().scale(this.speed * 0.1d);
							ep.motionX += vec.x + this.rand.nextGaussian() * this.inaccuracy;
							ep.motionY += vec.y + this.rand.nextGaussian() * this.inaccuracy;
							ep.motionZ += vec.z + this.rand.nextGaussian() * this.inaccuracy;
							this.updateBorderWith(ep);
						}
					} else {
						iter.remove();
					}
				}
				this.spawnNewParticles();
			}
			++this.ticks;
		}

		public boolean shouldRemove() {
			return this.particles.isEmpty();
		}

		public void forceRemove() {
			Iterator<Entity> iter = this.particles.iterator();
			while (iter.hasNext()) {
				Entity ep = iter.next();
				if (ep.isEntityAlive()) {
					ep.setDead();
				}
				iter.remove();
			}
		}

		public void setSpeed(float speedIn, float inaccuracyIn) {
			this.speed = speedIn;
			this.inaccuracy = inaccuracyIn;
		}

		public void setTarget(Vec3d newTarget, boolean dieOnReached) {
			this.setTarget(this.convert2AABB(newTarget, this.inaccuracy), dieOnReached);
		}

		public void setTarget(AxisAlignedBB newTargetBB, boolean dieOnReached) {
			this.targetBB = newTargetBB;
			this.dieOnTargetReached = dieOnReached;
		}

		public void setTarget(Vec3d newTargetPos, float speedIn, float inaccuracyIn, boolean dieOnReached) {
			this.setTarget(this.convert2AABB(newTargetPos, inaccuracyIn), speedIn, inaccuracyIn, dieOnReached);
		}

		public void setTarget(AxisAlignedBB newTargetBB, float speedIn, float inaccuracyIn, boolean dieOnReached) {
			this.setTarget(newTargetBB, dieOnReached);
			this.setSpeed(speedIn, inaccuracyIn);
		}

		private AxisAlignedBB convert2AABB(Vec3d vec, float width) {
			return new AxisAlignedBB(vec.x-width*0.5f, vec.y-width*0.5f, vec.z-width*0.5f, vec.x+width*0.5f, vec.y+width*0.5f, vec.z+width*0.5f);
		}
		
		public int getTicks() {
			return this.ticks;
		}

		public Vec3d getTargetPos() {
			return this.randomPosOnBB(this.targetBB);
		}

		private Vec3d randomPosInBB(AxisAlignedBB aabb) {
			return new Vec3d(aabb.minX + this.rand.nextDouble() * (aabb.maxX - aabb.minX),
			 aabb.minY + this.rand.nextDouble() * (aabb.maxY - aabb.minY),
			 aabb.minZ + this.rand.nextDouble() * (aabb.maxZ - aabb.minZ));
		}
		
		private Vec3d randomPosOnBB(AxisAlignedBB aabb) {
			Vec3d vec0 = this.randomPosInBB(aabb);
			final Vec3d[] vec1 = { new Vec3d(aabb.minX, vec0.y, vec0.z), new Vec3d(aabb.maxX, vec0.y, vec0.z),
			 new Vec3d(vec0.x, aabb.minY, vec0.z), new Vec3d(vec0.x, aabb.maxY, vec0.z),
			 new Vec3d(vec0.x, vec0.y, aabb.minZ), new Vec3d(vec0.x, vec0.y, aabb.maxZ) };
			return vec1[this.rand.nextInt(6)];
		}
	}
}
