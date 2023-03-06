/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods, you can remove them as they
 * are overrides of the base class ElementsNarutomodMod.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list, it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor, the compilation will fail.
 *
 * If you want to make a plain independent class, create it in
 * "Workspace" -> "Source" menu.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 */
package net.narutomod;

import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.procedure.ProcedureSync;

import javax.annotation.Nullable;
import java.util.Set;

@ElementsNarutomodMod.ModElement.Tag
public class TailedBeastMap extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder(NarutomodMod.MODID + ":" + TBMapItem.MAP_ID)
	public static final TBMapItem TB_MAP_ITEM = null;

	/**
	 * Do not remove this constructor
	 */
	public TailedBeastMap(ElementsNarutomodMod instance) {
		super(instance, 836);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void initElements() {
		this.elements.items.add(() -> new TBMapItem().setUnlocalizedName(TBMapItem.MAP_ID).setRegistryName(TBMapItem.MAP_ID).setCreativeTab(TabModTab.tab));
	}

	public static class TBMapData extends MapData {
		public final Set<TBMapDecoration> tbDecorations = Sets.newHashSet();

		public TBMapData(String name) {
			super(name);
		}

		public void addTBDeco(BlockPos target, byte index) {
			int i = 1 << this.scale;
			float f = (float)(target.getX() - (double)this.xCenter) / (float)i;
			float f1 = (float)(target.getZ() - (double)this.zCenter) / (float)i;
			byte xIn = (byte)((int)((double)(f * 2.0F) + 0.5D));
			byte yIn = (byte)((int)((double)(f1 * 2.0F) + 0.5D));

			double rotationIn = 180.0D;

			if (f >= -63.0F && f1 >= -63.0F && f <= 63.0F && f1 <= 63.0F)
			{
				rotationIn = rotationIn + (rotationIn < 0.0D ? -8.0D : 8.0D);
				this.tbDecorations.add(new TBMapDecoration(index, xIn, yIn, (byte)((int)(rotationIn * 16.0D / 360.0D))));
			}
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);

			byte[] storage = nbt.getByteArray("tb_decorations");

			if (storage.length > 0) {
				this.deserializeTBDecos(storage);
			}
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			compound = super.writeToNBT(compound);

			if (this.tbDecorations.size() > 0) {
				compound.setByteArray("tb_decorations", serializeTBDecos());
			}
			return compound;
		}

		public void deserializeTBDecos(byte[] storage) {
			this.tbDecorations.clear();

			for (int i = 0; i < storage.length / 4; ++i) {
				byte index = storage[i * 3];
				byte mapX = storage[i * 3 + 1];
				byte mapZ = storage[i * 3 + 2];
				byte mapRotation = storage[i * 3 + 3];

				this.tbDecorations.add(new TBMapDecoration(index, mapX, mapZ, mapRotation));
			}
		}

		public byte[] serializeTBDecos() {
			byte[] storage = new byte[this.tbDecorations.size() * 4];

			int i = 0;

			for (TBMapDecoration deco : this.tbDecorations) {
				storage[i * 3] = (byte) deco.index;
				storage[i * 3 + 1] = deco.getX();
				storage[i * 3 + 2] = deco.getY();
				storage[i * 3 + 3] = deco.getRotation();
				i++;
			}
			return storage;
		}

		public static class TBMapDecoration extends MapDecoration {
			private static final ResourceLocation MAP_ICONS = new ResourceLocation(NarutomodMod.MODID, "textures/map_icons.png");

			private final int index;

			public TBMapDecoration(int index, byte xIn, byte yIn, byte rotationIn) {
				super(Type.TARGET_X, xIn, yIn, rotationIn);
				this.index = index;
			}

			@Override
			@SideOnly(Side.CLIENT)
			public boolean render(int idx) {
				Minecraft.getMinecraft().renderEngine.bindTexture(MAP_ICONS);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F + getX() / 2.0F + 64.0F, 0.0F + getY() / 2.0F + 64.0F, -0.02F);
				GlStateManager.rotate((float) (this.getRotation() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(5.0F, 5.0F, 5.0F);

				// We don't care about depth, just the rendering order which is already sorted out
				GlStateManager.depthMask(false);

				float width = 32.0F;
				float height = 32.0F;
				float x = this.index * width;
				float y = (this.index / 4) * height;

				float max = 128.0F;

				float f0 = x / max;
				float f1 = y / max;
				float f2 = (width + x) / max;
				float f3 = (height + y) / max;

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();
				buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
				buffer.pos(-1.0D, 1.0D, 0.0D).tex(f2, f1).endVertex();
				buffer.pos(1.0D, 1.0D, 0.0D).tex(f0, f1).endVertex();
				buffer.pos(1.0D, -1.0D, 0.0D).tex(f0, f3).endVertex();
				buffer.pos(-1.0D, -1.0D, 0.0D).tex(f2, f3).endVertex();
				tessellator.draw();

				GlStateManager.depthMask(true);
				GlStateManager.popMatrix();

				return true;
			}

			@Override
			public boolean equals(Object obj) {
				if (super.equals(obj) && obj instanceof TBMapDecoration) {
					TBMapDecoration other = (TBMapDecoration) obj;
					return this.index == other.index;
				}
				return false;
			}

			@Override
			public int hashCode() {
				return super.hashCode() * 31 + index;
			}
		}
	}

	public static class TBMapItem extends ItemMap {
		public static final String MAP_ID = "tb_map";

		public static ItemStack setupNewMap(World world, double worldX, double worldZ, byte scale, boolean trackingPosition, boolean unlimitedTracking) {
			ItemStack item = new ItemStack(TailedBeastMap.TB_MAP_ITEM, 1, world.getUniqueDataId(MAP_ID));
			String name = String.format("%s_%s", MAP_ID, item.getMetadata());
			MapData data = new TBMapData(name);
			world.setData(name, data);
			data.scale = scale;
			data.calculateMapCenter(worldX, worldZ, data.scale);
			data.dimension = DimensionType.OVERWORLD.getId();
			data.trackingPosition = trackingPosition;
			data.unlimitedTracking = unlimitedTracking;
			data.markDirty();
			return item;
		}

		@Override
		public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
			super.onCreated(stack, worldIn, playerIn);

			final EntityBijuManager bm = EntityBijuManager.getClosestBiju(playerIn);
			final BlockPos target = bm.getSpawnPos();

			final ItemStack map = TBMapItem.setupNewMap(worldIn, target.getX(), target.getZ(), (byte) 1, true, true);
			TBMapItem.renderBiomePreviewMap(worldIn, map);
			TBMapData data = ((TBMapItem)map.getItem()).getMapData(map, worldIn);
			data.addTBDeco(target, (byte)(bm.getTails() - 1));
		}

		@Nullable
		@SideOnly(Side.CLIENT)
		public static TBMapData loadMapData(int mapId, World worldIn) {
			String name = String.format("%s_%s", MAP_ID, mapId);
			return (TBMapData) worldIn.loadData(TBMapData.class, name);
		}

		@Nullable
		@Override
		public TBMapData getMapData(ItemStack stack, World worldIn) {
			String name = String.format("%s_%s", MAP_ID, stack.getMetadata());
			TBMapData data = (TBMapData) worldIn.loadData(TBMapData.class, name);

			if (data == null && !worldIn.isRemote) {
				stack.setItemDamage(worldIn.getUniqueDataId(MAP_ID));
				name = String.format("%s_%s", MAP_ID, stack.getMetadata());
				data = new TBMapData(name);
				data.scale = 3;
				data.calculateMapCenter(worldIn.getWorldInfo().getSpawnX(), worldIn.getWorldInfo().getSpawnZ(), data.scale);
				data.dimension = worldIn.provider.getDimension();
				data.markDirty();
				worldIn.setData(name, data);
			}
			return data;
		}

		@Nullable
		@Override
		public Packet<?> createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player) {
			Packet<?> packet = super.createMapDataPacket(stack, worldIn, player);

			if (packet instanceof SPacketMaps) {
				TBMapData data = getMapData(stack, worldIn);
				return NarutomodMod.PACKET_HANDLER.getPacketFrom(new ProcedureSync.CPacketTBMap(stack.getItemDamage(), data, (SPacketMaps) packet));
			} else {
				return packet;
			}
		}
	}
}
