package net.narutomod.event;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemNinjaArmorKonoha;
import net.narutomod.item.ItemNinjaArmorSuna;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EventVillageSiege extends SpecialEvent {
	private Entity mobToSpawn;
	private int radius;
	private int spawnInterval;
	private static final List<Class <? extends EntityLiving>> MOBTYPES = Lists.newArrayList(EntityZombie.class, EntitySkeleton.class);
	private static final List<ItemStack> HELMETTYPES = Lists.newArrayList(new ItemStack(ItemNinjaArmorKonoha.helmet), new ItemStack(ItemNinjaArmorSuna.helmet));
	private static final List<ItemStack> VESTTYPES = Lists.newArrayList(new ItemStack(ItemNinjaArmorKonoha.body), new ItemStack(ItemNinjaArmorSuna.body));
	private static final List<ItemStack> PANTSTYPES = Lists.newArrayList(new ItemStack(ItemNinjaArmorKonoha.legs), new ItemStack(ItemNinjaArmorSuna.legs));

	public EventVillageSiege() {
		super();
	}

	public EventVillageSiege(World worldIn, Entity mob, int centerX, int centerY, int centerZ, long startTime, int radiusIn, int spawnIntervalIn) {
		super(EnumEventType.VILLAGE_SIEGE, worldIn, mob, centerX, centerY, centerZ, startTime);
		if (!worldIn.isRemote) {
			this.mobToSpawn = mob;
			this.radius = radiusIn;
			this.spawnInterval = spawnIntervalIn;
		}
	}

	@Override
	protected void onUpdate() {
		if (!this.shouldExecute())
			return;

		super.onUpdate();

		if (this.tick == 1) {
			ProcedureUtils.sendMessageToAllNear(net.minecraft.util.text.translation.I18n.translateToLocal("chattext.specialevent.villagesiege"),
			 this.x0, this.y0, this.z0, this.radius + 10, this.world.provider.getDimension());
			this.doOnTick(0);
		}
		if (this.world.isDaytime()) {
			this.doOnTick(this.tick);
			this.clear();
			return;
		}
		double r = this.rand.nextDouble() * 0.6d + 0.5d;
		if ((this.tick % this.spawnInterval == 0) && (this.rand.nextDouble() <= r - 0.d)) {
			r *= this.radius;
			double a = Math.PI * (this.rand.nextDouble()-0.5d) * 2d;
			double x = this.x0 + Math.cos(a) * r;
			double z = this.z0 + Math.sin(a) * r;
			for (double y = 253d; y > 0d; y -= 1d) {
				if (!this.world.isAirBlock(new BlockPos(x, y, z))) {
					Entity mob = this.mobToSpawn instanceof EntityLiving ? this.mobToSpawn
					 : this.newEntityFromClassName(MOBTYPES.get(this.rand.nextInt(MOBTYPES.size())).getName());
					mob.setPosition(x, y + 1d, z);
					((EntityLiving)mob).onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(mob)), null);
					int i = this.rand.nextInt(VESTTYPES.size());
					((EntityLiving)mob).setItemStackToSlot(EntityEquipmentSlot.HEAD, HELMETTYPES.get(i));
					((EntityLiving)mob).setItemStackToSlot(EntityEquipmentSlot.CHEST, VESTTYPES.get(i));
					((EntityLiving)mob).setItemStackToSlot(EntityEquipmentSlot.LEGS, PANTSTYPES.get(i));
					this.world.spawnEntity(mob);
					return;
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (this.mobToSpawn != null) {
			compound.setString("EntityClass", this.mobToSpawn.getClass().getName());
		}
		compound.setInteger("radius", this.radius);
		compound.setInteger("spawnInterval", this.spawnInterval);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("EntityClass")) {
			this.mobToSpawn = this.newEntityFromClassName(compound.getString("EntityClass"));
		}
		this.radius = compound.getInteger("radius");
		this.spawnInterval = compound.getInteger("spawnInterval");
	}

	@Override
	public String toString() {
		return super.toString() + " {radius:" + this.radius + ",spawnInterval:" + this.spawnInterval + "}";
	}
}
