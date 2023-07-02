
package net.narutomod.item;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.storage.WorldSavedData;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityBijuManager;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import io.netty.buffer.ByteBuf;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBijuMap extends ElementsNarutomodMod.ModElement {
	@GameRegistry.ObjectHolder(NarutomodMod.MODID + ":" + TBMapItem.MAP_ID)
	public static final Item block = null;

	public ItemBijuMap(ElementsNarutomodMod instance) {
		super(instance, 836);
	}

	@Override
	public void initElements() {
		elements.items.add(() -> new TBMapItem().setUnlocalizedName(TBMapItem.MAP_ID)
				.setRegistryName(TBMapItem.MAP_ID).setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation(NarutomodMod.MODID + ":" + TBMapItem.MAP_ID, "inventory"));
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		this.elements.addNetworkMessage(TBMapItem.CPacketTBMap.ClientHandler.class, TBMapItem.CPacketTBMap.class, Side.CLIENT);
	}

	public static class TBMapData extends MapData {
		private final Set<TBMapDecoration> tbDecorations = Sets.newHashSet();

		public TBMapData(String name) {
			super(name);
		}

		public void addTBDeco(BlockPos target, int index) {
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
				compound.setByteArray("tb_decorations", this.serializeTBDecos());
			}
			return compound;
		}

		public void deserializeTBDecos(byte[] storage) {
			this.tbDecorations.clear();

			for (int i = 0; i < storage.length / 4; ++i) {
				byte index = storage[i * 4];
				byte mapX = storage[i * 4 + 1];
				byte mapZ = storage[i * 4 + 2];
				byte mapRotation = storage[i * 4 + 3];

				this.tbDecorations.add(new TBMapDecoration(index, mapX, mapZ, mapRotation));
			}
		}

		public byte[] serializeTBDecos() {
			byte[] storage = new byte[this.tbDecorations.size() * 4];

			int i = 0;

			for (TBMapDecoration deco : this.tbDecorations) {
				storage[i * 4] = (byte) deco.index;
				storage[i * 4 + 1] = deco.getX();
				storage[i * 4 + 2] = deco.getY();
				storage[i * 4 + 3] = deco.getRotation();
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
				Minecraft mc = Minecraft.getMinecraft();
				mc.renderEngine.bindTexture(MAP_ICONS);
				GlStateManager.pushMatrix();
				GlStateManager.translate(this.getX() / 2.0F + 64.0F, this.getY() / 2.0F + 64.0F, -0.02F);
				GlStateManager.rotate((float) (this.getRotation() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(7.5F, 7.5F, 7.5F);

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
		public static final String MAP_ID = "biju_map";

		@Override
		public int getItemStackLimit(ItemStack stack) {
			return 1;
		}

		@Override
		public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
			super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

			if (!(entityIn instanceof EntityPlayer) || worldIn.isRemote) {
				return;
			}

			EntityPlayer player = (EntityPlayer) entityIn;

			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}

			NBTTagCompound nbt = stack.getTagCompound();

			if (nbt.getBoolean("hasInitialized")) {
				return;
			}

			final EntityBijuManager bm = EntityBijuManager.getClosestBiju(player);

			if (bm != null) {
				final BlockPos target = bm.getPosition();

				this.setupNewMap(stack, worldIn, target.getX(), target.getZ(), true, true);
				TBMapItem.renderBiomePreviewMap(worldIn, stack);

				TBMapData data = ((TBMapItem) stack.getItem()).getMapData(stack, worldIn);
				data.addTBDeco(target, (byte) (bm.getTails() - 1));

				// This way the map will find a tailed beast once one is available :P
				nbt.setBoolean("hasInitialized", true);
			}
			else if (isSelected && worldIn.getTotalWorldTime() % 20 == 0) {
				player.sendStatusMessage(new TextComponentTranslation("overlay.no_biju_available"), true);

				if (!nbt.getBoolean("hasReset")) {
					// Resets the map data
					String name = String.format("%s_%s", MAP_ID, stack.getMetadata());

					if (worldIn.loadData(TBMapData.class, name) != null) {
						worldIn.setData(name, new TBMapData(name));
						nbt.setBoolean("hasReset", true);
					}
				}
			}
		}

		private TBMapData setupNewMap(ItemStack stack, World world, double worldX, double worldZ, boolean trackingPosition, boolean unlimitedTracking) {
			String name = String.format("%s_%s", MAP_ID, stack.getMetadata());
			TBMapData data = new TBMapData(name);
			world.setData(name, data);
			data.calculateMapCenter(worldX, worldZ, data.scale);
			data.dimension = DimensionType.OVERWORLD.getId();
			data.trackingPosition = trackingPosition;
			data.unlimitedTracking = unlimitedTracking;
			data.markDirty();
			return data;
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
				data = this.setupNewMap(stack, worldIn, 0.0D, 0.0D, true, true);
			}
			return data;
		}

		@Nullable
		@Override
		public Packet<?> createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player) {
			Packet<?> packet = super.createMapDataPacket(stack, worldIn, player);

			TBMapData data = this.getMapData(stack, worldIn);

			if (packet instanceof SPacketMaps) {
				return NarutomodMod.PACKET_HANDLER.getPacketFrom(new CPacketTBMap(stack.getItemDamage(), data, (SPacketMaps) packet));
			} else {
				return packet;
			}
		}

		public static class CPacketTBMap implements IMessage {
			private int mapID;
			private byte[] decoData;
			private SPacketMaps inner;

			public CPacketTBMap() {
			}

			public CPacketTBMap(int mapID, TBMapData data, SPacketMaps inner) {
				this.mapID = mapID;
				this.decoData = data.serializeTBDecos();
				this.inner = inner;
			}

			@Override
			public void fromBytes(ByteBuf buf) {
				PacketBuffer tmp = new PacketBuffer(buf);
				mapID = ByteBufUtils.readVarInt(buf, 5);
				decoData = tmp.readByteArray();

				inner = new SPacketMaps();

				try {
					inner.readPacketData(tmp);
				} catch (IOException e) {}
			}

			@Override
			public void toBytes(ByteBuf buf) {
				PacketBuffer tmp = new PacketBuffer(buf);
				ByteBufUtils.writeVarInt(buf, mapID, 5);
				tmp.writeByteArray(decoData);

				try {
					inner.writePacketData(tmp);
				} catch (IOException e) {}
			}

			public static class ClientHandler implements IMessageHandler<CPacketTBMap, IMessage> {
				@SideOnly(Side.CLIENT)
				@Override
				public IMessage onMessage(CPacketTBMap message, MessageContext ctx) {
					Minecraft.getMinecraft().addScheduledTask(() -> {
						MapItemRenderer mapItemRenderer = Minecraft.getMinecraft().entityRenderer.getMapItemRenderer();
						TBMapData data = TBMapItem.loadMapData(message.mapID, Minecraft.getMinecraft().world);

						if (data == null)
						{
							String name = String.format("%s_%s", TBMapItem.MAP_ID, message.mapID);
							data = new TBMapData(name);

							if (mapItemRenderer.getMapInstanceIfExists(name) != null)
							{
								MapData existingData = mapItemRenderer.getData(mapItemRenderer.getMapInstanceIfExists(name));

								if (existingData instanceof TBMapData)
								{
									data = (TBMapData) existingData;
								}
							}
							Minecraft.getMinecraft().world.setData(name, data);
						}

						message.inner.setMapdataTo(data);

						data.deserializeTBDecos(message.decoData);

						Map<String, MapDecoration> saveVanilla = data.mapDecorations;
						data.mapDecorations = Maps.newLinkedHashMap();

						for (TBMapData.TBMapDecoration deco : data.tbDecorations) {
							data.mapDecorations.put(deco.toString(), deco);
						}
						data.mapDecorations.putAll(saveVanilla);

						mapItemRenderer.updateMapTexture(data);
					});
					return null;
				}
			}
		}
	}
}
